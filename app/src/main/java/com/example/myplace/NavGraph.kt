package com.example.myplace

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myplace.ui.addplace.AddPlaceScreen
import com.example.myplace.ui.detail.PlaceDetailScreen
import com.example.myplace.ui.map.MapScreen
import com.example.myplace.ui.settings.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Map.route
    ) {
        // Écran carte principal
        composable(Screen.Map.route) {
            MapScreen(
                onNavigateToAddPlace = { lat, lon ->
                    navController.navigate(Screen.AddPlace.createRoute(lat, lon))
                },
                onNavigateToDetail = { placeId ->
                    navController.navigate(Screen.PlaceDetail.createRoute(placeId))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // Écran ajout lieu
        composable(
            route = Screen.AddPlace.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lon") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
            val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: 0.0
            AddPlaceScreen(
                latitude = lat,
                longitude = lon,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCamera = {
                    navController.navigate(Screen.Camera.route)
                }
            )
        }

        // Écran détail lieu
        composable(
            route = Screen.PlaceDetail.route,
            arguments = listOf(
                navArgument("placeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getLong("placeId") ?: 0L
            PlaceDetailScreen(
                placeId = placeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Écran paramètres
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}