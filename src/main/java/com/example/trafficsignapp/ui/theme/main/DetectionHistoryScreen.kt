package com.example.trafficsignapp.ui.theme.main

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.trafficsignapp.ui.theme.DetectedSign
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.trafficsignapp.data.detection.DetectedSignEntity
import com.example.trafficsignapp.data.detection.DetectionDatabase
import java.util.*
import androidx.compose.runtime.*
import java.text.SimpleDateFormat
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.ArrowBack
import com.example.trafficsignapp.TrafficSignLabels
import com.example.trafficsignapp.data.route.RouteDatabase



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionHistoryScreen(navController: NavHostController,routeId: Long?) {

    val context = LocalContext.current

    val detDao  = remember { DetectionDatabase.getInstance(context).detectedSignDao() }
    // ② route DAO from your RouteDatabase
    val routeDao = remember { RouteDatabase.getInstance(context).routeDao() }

   // var items by remember { mutableStateOf<List<DetectedSignEntity>>(emptyList()) }

    // 1️⃣ Load raw entities from Room
    var entities by remember { mutableStateOf<List<DetectedSignEntity>>(emptyList()) }

   // val allDetections = remember { mutableStateListOf<DetectedSignEntity>() }

    // 2) Whenever routeId changes, fetch from the correct DAO
    LaunchedEffect(routeId) {
        entities = if (routeId != null) {
            // fetch only this route’s detections
            routeDao.getRouteWithDetections(routeId).detections
        } else {
            // fetch *all* detections globally
            detDao.getAllDetections()
        }
    }

    // 3️⃣ Search/filter state
    var searchQuery by remember { mutableStateOf("") }
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }


    // map Entity → DetectedSign, then filter by query
    val filtered = entities
        .map { ent ->
            DetectedSign(
                name     = TrafficSignLabels[ent.classId],                              // assume you added a `label` field; otherwise map classId → TrafficSignLabels[classId]
                date     = sdf.format(Date(ent.timestamp)),
                accuracy = String.format("%.1f%%", ent.confidence * 100),
                imageUri = ""                      // if you store a snapshot URI, otherwise blank
            )
        }
        .filter { it.name.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detection History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // ── List ────────────────────────────────────
            LazyColumn {
                items(filtered) { sign ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // imageUri may be empty → show placeholder
                        Image(
                            painter = rememberAsyncImagePainter(sign.imageUri),
                            contentDescription = sign.name,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(sign.name, style = MaterialTheme.typography.bodyLarge)
                            Text("Date: ${sign.date}")
                            Text("Accuracy: ${sign.accuracy}")
                        }
                    }
                    Divider()
                    }
                }
            }
        }
}
