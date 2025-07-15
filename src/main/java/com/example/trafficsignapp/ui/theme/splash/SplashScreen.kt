package com.example.trafficsignapp.ui.theme.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import com.example.trafficsignapp.ui.theme.nav.Screen
import kotlinx.coroutines.delay
import com.example.trafficsignapp.R

private const val SPLASH_DELAY_MS = 3000L

@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(Unit) {
        delay(SPLASH_DELAY_MS)
        navController.navigate(Screen.Login.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.trafficapp_icon), // Replace with your actual logo
                contentDescription = "App Logo",
                modifier = Modifier.size(180.dp)
                    .clip(CircleShape) //  Makes it round
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Smart Traffic Sign Planner",
                fontSize = 20.sp,
                color = Color.DarkGray
            )
        }
    }
}