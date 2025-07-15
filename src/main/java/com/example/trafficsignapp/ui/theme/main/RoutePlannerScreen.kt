package com.example.trafficsignapp.ui.theme.main

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.trafficsignapp.ui.theme.nav.Screen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import androidx.compose.material3.MenuAnchorType
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.platform.LocalContext 
import java.security.MessageDigest
import android.content.Context
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.Alignment
import com.example.trafficsignapp.data.route.Route
import com.example.trafficsignapp.data.route.RouteDatabase
import com.example.trafficsignapp.R


data class RouteOption(
    val key: String,
    val points: List<LatLng>,
    val duration: String,
    val distance: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutePlannerScreen(navController: NavHostController) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var originPlace by remember { mutableStateOf<Place?>(null) }
    var destinationPlace by remember { mutableStateOf<Place?>(null) }

    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    val cameraPositionState = rememberCameraPositionState()

    var routeOptions    by remember { mutableStateOf<List<RouteOption>>(emptyList()) }
    var selectedOption  by remember { mutableStateOf<RouteOption?>(null) }


    val routeDb = remember { RouteDatabase.getInstance(context) }
    var existingRoute by remember { mutableStateOf<Route?>(null) }
    var showRecordDialog by remember { mutableStateOf(false) }

    // When originPlace changes, center map there
    LaunchedEffect(originPlace) {
        originPlace?.latLng?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 14f)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Route Planner ") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.Search, contentDescription = "Back")
                    }
                })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ── A) PLACE AUTOCOMPLETE FIELDS ─────────────────────────────
            Text("From", fontSize = 16.sp)
            PlaceSearchField { place -> originPlace = place }
            Spacer(Modifier.height(8.dp))
            Text("To", fontSize = 16.sp)
            PlaceSearchField { place -> destinationPlace = place }

            Spacer(Modifier.height(16.dp))

            // --- FIND ROUTE BUTTON ---
            Button(
                onClick = {
                    originPlace?.latLng?.let { start ->
                        destinationPlace?.latLng?.let { end ->
                            coroutineScope.launch {
                                routeOptions   = fetchRouteOptions(context,start, end)
                                selectedOption = routeOptions.firstOrNull()
                            }
                        }
                    }
                },
                enabled = originPlace != null && destinationPlace != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("\uD83D\uDD0D Find Route")
            }

            Spacer(Modifier.height(12.dp))


            // --- GOOGLE MAP AT TOP ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    originPlace?.latLng?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Start"
                        )
                    }
                    destinationPlace?.latLng?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "End"
                        )
                    }
                    // Draw every alternative route, but highlight the selected one
                    routeOptions.forEach { opt ->
                        Polyline(
                            points = opt.points,
                            color = if (opt == selectedOption) Color.Blue else Color.Gray,
                            width = if (opt == selectedOption) 8f else 4f
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 4) **Route picker cards** (this is Step 4):
            routeOptions.forEach { opt ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedOption = opt
                            coroutineScope.launch {
                            existingRoute   = routeDb.routeDao().findByKey(opt.key)
                            showRecordDialog= existingRoute == null
                        }
                                   },
                    colors = CardDefaults.cardColors(
                        containerColor = if (opt == selectedOption)
                            Color(0xFFBBDEFB) else Color.White
                    )
                ) {
                    Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Time: ${opt.duration}")
                            Text("Distance: ${opt.distance}")
                        }
                        if (opt == selectedOption) {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── E) RECORD PROMPT ─────────────────────────────────────────
            if (showRecordDialog && selectedOption != null) {
                AlertDialog(
                    onDismissRequest = { showRecordDialog = false },
                    title   = { Text("Unrecorded Route") },
                    text    = { Text("You haven’t recorded this route’s signs yet. Record now?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showRecordDialog = false
                            navController.navigate("${Screen.StartDetection.route}?routeKey=${selectedOption!!.key}")
                        }) { Text("Yes") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showRecordDialog = false }) { Text("No") }
                    }
                )
            }


            // --- START RECORDING BUTTON ---
            Button(
                onClick = {
                    // Navigate to detection screen, passing routePoints if needed
                    navController.navigate(Screen.StartDetection.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("\uD83C\uDFA5 Start Recording")
            }

            // ── F) VIEW HISTORY BUTTON ───────────────────────────────────
            existingRoute?.let { route ->
                Button(
                    onClick = {
                        navController.navigate("${Screen.DetectionHistory.route}?routeId=${route.routeId}")
                    },
                    Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text("View Recorded Signs")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}


@Composable
fun PlaceSearchField(onPlaceSelected: (Place) -> Unit) {
    val context = LocalContext.current

    // 1️⃣ Prepare the launcher
    val launcher = rememberLauncherForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val place = Autocomplete.getPlaceFromIntent(data)
                onPlaceSelected(place)
            }
        }
        // you can handle AUTOCOMPLETE_ERROR and RESULT_CANCELED if you like
    }

    // 2️⃣ UI
    OutlinedTextField(
        value = "",               // always blank, since we launch the picker
        onValueChange = { },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // 3️⃣ Build and launch the Intent
                val fields = listOf(Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG)
                val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields
                ).build(context)
                launcher.launch(intent)
            },
        label = { Text("Search place") },
        trailingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        }
    )
}


private suspend fun fetchRouteOptions(
    context: Context,
    start:  LatLng,
    end:  LatLng
): List<RouteOption> = withContext(Dispatchers.IO) {
    val apiKey = context.getString(R.string.google_maps_key)
    val url = buildString {
        append("https://maps.googleapis.com/maps/api/directions/json")
        append("?origin=${start.latitude},${start.longitude}")
        append("&destination=${end.latitude},${end.longitude}")
        append("&alternatives=true")
        append("&key=$apiKey")
    }

    // Fetch & parse
    val response = URL(url).readText()
    val json     = JSONObject(response)
    val routes   = json.getJSONArray("routes")
    val options  = mutableListOf<RouteOption>()
    for (i in 0 until routes.length()) {
        val routeObj = routes.getJSONObject(i)
        val leg      = routeObj.getJSONArray("legs").getJSONObject(0)
        val dur      = leg.getJSONObject("duration").getString("text")
        val dist     = leg.getJSONObject("distance").getString("text")
        val polyStr  = routeObj.getJSONObject("overview_polyline").getString("points")
        val pts      = decodePoly(polyStr)
        val keyBytes = MessageDigest
            .getInstance("SHA-256")
            .digest(polyStr.toByteArray())
        val key      = keyBytes.joinToString("") { "%02x".format(it) }
        options += RouteOption(key, pts, dur, dist)
    }
    options
}



fun decodePoly(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        poly.add(LatLng(lat / 1E5, lng / 1E5))
    }

    return poly
}

