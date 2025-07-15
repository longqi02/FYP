package com.example.trafficsignapp.ui.theme.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.pytorch.IValue
import org.pytorch.Module
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import org.pytorch.torchvision.TensorImageUtils
import com.example.trafficsignapp.CameraPreview
import java.util.concurrent.Executors
import com.example.trafficsignapp.YoloV11Detector
import com.example.trafficsignapp.YoloV11Detector.DetectionResult
import com.example.trafficsignapp.data.detection.DetectedSignEntity
import com.example.trafficsignapp.data.detection.DetectionDatabase
import com.example.trafficsignapp.data.route.RouteDatabase
import com.google.firebase.auth.FirebaseAuth
import com.example.trafficsignapp.data.route.Route
import com.example.trafficsignapp.data.route.RouteDetectionCrossRef
import kotlinx.coroutines.withContext


@Composable
fun StartDetectionScreen(
    navController: NavHostController,
    routeKey: String
    ) {
    val context = LocalContext.current
    val routedao          = remember { RouteDatabase.getInstance(context).routeDao() }
    var routeId by remember { mutableStateOf<Long?>(null) }


    LaunchedEffect(routeKey) {
        // Try to find an existing route row
        val existing = withContext(Dispatchers.IO) { routedao.findByKey(routeKey) }
        routeId = if (existing != null) {
            existing.routeId
        } else {
            // Insert a new one
            withContext(Dispatchers.IO) {
                routedao.createRoute(
                    Route(
                        routeKey = routeKey,
                        userId   = FirebaseAuth.getInstance().currentUser!!.uid,
                        startTime= System.currentTimeMillis(),
                        endTime  = null
                    )
                )
            }
        }


    }

    DisposableEffect(routeId) {
        onDispose {
            routeId?.let { rid ->
                // update the endTime in the DB
                // (you can wrap in Dispatchers.IO if you like)
                routedao.updateEndTime(rid, System.currentTimeMillis())
            }
        }
    }


    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current


    // ① Detection DB
    val detectionDb = remember { DetectionDatabase.getInstance(context) }

    // === A. Detector setup ===
    // ◀ CHANGED: replace manual Module with YoloV11Detector
    val detector = remember { YoloV11Detector(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Hold the latest detection results
    // ◀ CHANGED: new state for bounding-box results
    var results by remember { mutableStateOf<List<DetectionResult>>(emptyList()) }



    var detectedLabel by remember { mutableStateOf("No Sign") }
    var detectedAccuracy by remember { mutableStateOf("") }
    val detectionHistory = remember { mutableStateListOf<String>() }
    val cameraPermissionGranted = remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionGranted.value = isGranted
    }




    // === B. Analyzer wiring ===
    // Build your ImageAnalysis use-case and attach the detector
    val analysisUseCase = remember {
        ImageAnalysis.Builder()
            .setTargetResolution(Size(640, 640))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analysis ->

                analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    // Convert frame → Bitmap
                    val bitmap = imageProxy.toBitmap()?.let { rotateYUV420Image(it) }
                    if (bitmap != null) {
                        // Run detection off the main thread
                        coroutineScope.launch(Dispatchers.Default) {

                            // resize to model input
                            val resized = Bitmap.createScaledBitmap(bitmap, 640, 640, true)

                            // 1) Run the model
                            val detections = detector.detect(resized)

                            // 2) Persist & link all detections for this route (IO thread)
                            routeId?.let { rid ->
                                withContext(Dispatchers.IO) {
                                    detections.forEach { det ->
                                        val detEntity = DetectedSignEntity(
                                            classId   = det.classIndex,
                                            confidence= det.confidence,
                                            timestamp = System.currentTimeMillis()
                                        )
                                        val detId = detectionDb.detectedSignDao().insert(detEntity)
                                        routedao.linkDetection(
                                            RouteDetectionCrossRef(routeId = rid, detectionId = detId)
                                        )
                                    }
                                }
                            }

                            // 3) Update all Compose state back on the Main dispatcher
                            withContext(Dispatchers.Main) {
                                results = detections

                                if (detections.isNotEmpty()) {
                                    val top = detections.maxByOrNull { it.confidence }!!
                                    detectedLabel    = top.label
                                    detectedAccuracy = String.format("%.1f%%", top.confidence * 100)
                                    detectionHistory.add("${top.label} @ $detectedAccuracy")
                                } else {
                                    detectedLabel    = "No Sign"
                                    detectedAccuracy = ""
                                }
                            }
                        }
                    }
                    imageProxy.close()
                }
            }
    }

    // Your existing Preview & Capture use-cases
    val previewUseCase = remember { Preview.Builder().build() }
    val imageCaptureUseCase = remember { ImageCapture.Builder().build() }



    if (cameraPermissionGranted.value) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 6/10 of screen: camera preview + overlay
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
            ) {
                // ◀ CHANGED: inject prepared use-cases
                CameraPreview(
                    modifier = Modifier.matchParentSize(),
                    previewUseCase = previewUseCase,
                    imageCaptureUseCase = imageCaptureUseCase,
                    analysisUseCase = analysisUseCase,
                    lifecycleOwner = lifecycleOwner
                )

                // === C. Overlay drawing ===
                Canvas(modifier = Modifier.matchParentSize()) {
                    results.forEach { det ->
                        // Draw bounding box
                        drawRect(
                            color = androidx.compose.ui.graphics.Color.Green,
                            topLeft = androidx.compose.ui.geometry.Offset(det.box.left, det.box.top),
                            size = androidx.compose.ui.geometry.Size(det.box.width(), det.box.height()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                        )
                        // Draw label text
                        drawContext.canvas.nativeCanvas.apply {
                            drawText(
                                "${det.label} ${(det.confidence * 100).toInt()}%",
                                det.box.left,
                                det.box.top - 8,
                                Paint().apply {
                                    color = android.graphics.Color.GREEN
                                    textSize = 36f
                                }
                            )
                        }
                    }
                }
            }
            // Detection Info (Bottom 40%)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFEEEEEE))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = detectedLabel,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .background(Color.White)
                            .padding(12.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Column {
                            Text("Detected: $detectedLabel")
                            Text("Confidence: $detectedAccuracy")
                            Text("This sign indicates important traffic instruction.")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable detection history
                Text("Detection History:")
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 8.dp)
                        .background(Color(0xFFF0F0F0))
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    detectionHistory.forEach { entry ->
                        Text(entry)
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Camera permission is required to use this feature.")
        }
    }
}


fun assetFilePath(context: Context, assetName: String): String {
    val file = File(context.filesDir, assetName)
    if (file.exists() && file.length() > 0) return file.absolutePath

    context.assets.open(assetName).use { input ->
        FileOutputStream(file).use { output ->
            val buffer = ByteArray(4 * 1024)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                output.write(buffer, 0, read)
            }
            output.flush()
        }
    }
    return file.absolutePath
}

fun rotateYUV420Image(bitmap: Bitmap?): Bitmap? {
    if (bitmap == null) return null

    val matrix = android.graphics.Matrix().apply {
        postRotate(90f) // or 270f depending on camera orientation
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun ImageProxy.toBitmap(): Bitmap? {
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer
    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
    val yuv = out.toByteArray()
    return BitmapFactory.decodeByteArray(yuv, 0, yuv.size)
}



