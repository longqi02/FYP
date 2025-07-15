package com.example.trafficsignapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import com.example.trafficsignapp.YoloPostProcessor

/**
 * Loads the TorchScript model once and provides:
 *   1) runInference(): raw Tensor output
 *   2) detect(): full parsing → List<DetectionResult>
 */

class YoloV11Detector(private val context: Context) {

    companion object {
        private const val MODEL_FILE = "yolov11_android.pt"
        private const val CONF_THRESHOLD = 0.4f
        private const val NMS_THRESHOLD  = 0.5f
    }

    private val module: Module by lazy {
        val modelFile = File(context.filesDir, MODEL_FILE)
        if (!modelFile.exists()) {
            context.assets.open(MODEL_FILE).use { input ->
                FileOutputStream(modelFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
        Module.load(modelFile.absolutePath)
    }

    /** Step 1: Convert Bitmap → input Tensor and run the model */
    fun runInference(bitmap: Bitmap): Tensor {
        val input = TensorImageUtils.bitmapToFloat32Tensor(
            bitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )
        return module.forward(IValue.from(input)).toTensor()
    }

    /** Step 2: Parse raw output, apply confidence filter + NMS, return final detections */
    fun detect(bitmap: Bitmap): List<DetectionResult> {
        // 1. Run the model
        val output = runInference(bitmap)
        val raw = output.dataAsFloatArray

        // 2. TODO: parse `raw` into lists of boxes, scores, classIds
        val (boxes, scores, classIds) = YoloPostProcessor.parseYoloOutput(
            raw,
            bitmap.width,
            bitmap.height,
            CONF_THRESHOLD
        )

        // 3. Run NMS per class
        val results = mutableListOf<DetectionResult>()
        classIds.toSet().forEach { cls ->
            val idxsForClass = classIds
                .mapIndexed { idx, c -> idx to c }
                .filter  { it.second == cls }
                .map     { it.first }
            val classBoxes  = idxsForClass.map { boxes[it] }
            val classScores = idxsForClass.map { scores[it] }
            val keep = nms(classBoxes, classScores, NMS_THRESHOLD)
            keep.forEach { keptIdx ->
                val globalIdx = idxsForClass[keptIdx]
                results += DetectionResult(
                    label      = TrafficSignLabels[cls],
                    confidence = scores[globalIdx],
                    box        = boxes[globalIdx],
                    classIndex = cls
                )
            }
        }
        return results
    }

    /** Copy asset into app-internal storage and return a File reference */
    private fun copyAssetToFile(assetName: String): File {
        val outFile = File(context.filesDir, assetName)
        if (!outFile.exists() || outFile.length() == 0L) {
            context.assets.open(assetName).use { input ->
                FileOutputStream(outFile).use { out ->
                    input.copyTo(out)
                }
            }
        }
        return outFile
    }

    /** Simple NMS implementation */
    private fun nms(boxes: List<RectF>, scores: List<Float>, iouThresh: Float): List<Int> {
        val idxs = scores
            .mapIndexed { i, s -> i to s }
            .filter  { it.second >= CONF_THRESHOLD }
            .sortedBy { -it.second }
            .map     { it.first }
            .toMutableList()

        val keep = mutableListOf<Int>()
        while (idxs.isNotEmpty()) {
            val i = idxs.removeAt(0)
            keep += i
            idxs.removeAll { j ->
                val iou = computeIoU(boxes[i], boxes[j])
                iou > iouThresh
            }
        }
        return keep
    }

    private fun computeIoU(a: RectF, b: RectF): Float {
        val left   = maxOf(a.left, b.left)
        val top    = maxOf(a.top, b.top)
        val right  = minOf(a.right, b.right)
        val bottom = minOf(a.bottom, b.bottom)
        val inter = maxOf(0f, right - left) * maxOf(0f, bottom - top)
        val union = a.width()*a.height() + b.width()*b.height() - inter
        return if (union > 0f) inter/union else 0f
    }

    data class DetectionResult(
        val label: String,
        val confidence: Float,
        val box: RectF,
        val classIndex: Int
    )
}