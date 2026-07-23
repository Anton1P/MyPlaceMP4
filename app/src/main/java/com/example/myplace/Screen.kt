package com.example.myplace

sealed class Screen(val route: String) {
    object Map : Screen("map")
    object AddPlace : Screen("add_place/{lat}/{lon}") {
        fun createRoute(lat: Double, lon: Double) = "add_place/$lat/$lon"
    }
    object PlaceDetail : Screen("place_detail/{placeId}") {
        fun createRoute(placeId: Long) = "place_detail/$placeId"
    }
    object Settings : Screen("settings")
    object Camera : Screen("camera")
}