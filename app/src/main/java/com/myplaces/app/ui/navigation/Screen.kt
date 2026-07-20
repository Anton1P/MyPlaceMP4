package com.myplaces.app.ui.navigation

sealed class Screen(val route: String) {
    object Map : Screen("map")
    object AddPlace : Screen("add_place/{lat}/{lng}") {
        fun createRoute(lat: Double, lng: Double) = "add_place/$lat/$lng"
    }
    object Camera : Screen("camera")
    object Settings : Screen("settings")
}
