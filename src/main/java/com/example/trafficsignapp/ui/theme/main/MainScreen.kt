package com.example.trafficsignapp.ui.theme.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.trafficsignapp.R
import com.example.trafficsignapp.ui.theme.main.RoutePlannerScreen
import androidx.navigation.NavHostController
import com.example.trafficsignapp.ui.theme.nav.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Traffic Sign Planner") },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Profile") },
                            onClick = { /* TODO */ }
                        )
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = { /* TODO */ }
                        )
                    }
                }
            )
        }
    ) { Padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.trafficapp_icon),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text("Welcome!", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(32.dp))

            MainMenuButton(
                icon = Icons.Default.CameraAlt,
                label = "Start Detection",
                onClick = {  navController.navigate(Screen.StartDetection.route)  }
            )

            MainMenuButton(
                icon = Icons.Default.Map,
                label = "Route Planner (Kampar)",
                onClick = { navController.navigate(Screen.RoutePlanner.route) }
            )

            MainMenuButton(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                label = "Detection History",
                onClick = {  navController.navigate(Screen.DetectionHistory.route)  }
            )
        }
    }
}


@Composable
fun MainMenuButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(label)
    }
}