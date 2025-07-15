package com.example.trafficsignapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trafficsignapp.ui.theme.DetectedSign
import com.example.trafficsignapp.ui.theme.TrafficSignAPPTheme
import com.example.trafficsignapp.ui.theme.nav.Screen
import com.example.trafficsignapp.ui.theme.login.LoginScreen
import com.example.trafficsignapp.ui.theme.main.DetectionHistoryScreen
import com.example.trafficsignapp.ui.theme.main.MainScreen
import com.example.trafficsignapp.ui.theme.splash.SplashScreen
import com.example.trafficsignapp.ui.theme.main.RoutePlannerScreen
import com.example.trafficsignapp.ui.theme.main.StartDetectionScreen
import com.example.trafficsignapp.ui.theme.login.SignUpScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument


@ExperimentalMaterial3Api
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrafficSignAPPTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.Splash.route) {
                    composable(Screen.Splash.route) { SplashScreen(navController) }
                    composable(Screen.Login.route) { LoginScreen(navController) } // Replace with actual login screen
                    composable(Screen.SignUp.route) { SignUpScreen(navController) }
                    composable(Screen.Main.route) { MainScreen(navController) }
                    composable(Screen.RoutePlanner.route) { RoutePlannerScreen(navController) }
                    composable(
                        route = "start_detection?routeKey={routeKey}",
                        arguments = listOf(
                            navArgument("routeKey") { type = NavType.StringType }
                        )
                    ) { backStack ->
                        val key = backStack.arguments!!.getString("routeKey")!!
                        StartDetectionScreen(navController, routeKey = key)
                    }
                    composable(
                        route = Screen.DetectionHistory.route + "?routeId={routeId}",
                        arguments = listOf(navArgument("routeId") {
                            type = NavType.LongType
                            defaultValue = -1L
                        }
                        )
                    ) { backStackEntry ->
                        val raw = backStackEntry.arguments!!.getLong("routeId")
                        val routeId: Long? = if (raw >= 0L) raw else null
                        DetectionHistoryScreen(navController, routeId)
                    }
                }
            }
        }
    }

    @Composable
    fun Greeting2(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview2() {
        TrafficSignAPPTheme {
            Greeting2("Android")
        }
    }

    val sampleDetectedSigns = listOf(
        DetectedSign("Speed Limit 60", "3/3/25", "96.01%", "file:///android_asset/speed60.png"),
        DetectedSign("Stop", "4/3/25", "92.2%", "file:///android_asset/stop.png")
    )
}