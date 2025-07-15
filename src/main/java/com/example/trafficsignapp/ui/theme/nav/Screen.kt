package com.example.trafficsignapp.ui.theme.nav

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Main : Screen("main") // For when user logs in
    object RoutePlanner : Screen("route_planner")
    object StartDetection : Screen("start_detection")
    object DetectionHistory : Screen("detection_history?routeId={routeId}")

}