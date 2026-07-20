package com.myplaces.app.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.myplaces.app.ui.addplace.AddPlaceScreen
import com.myplaces.app.ui.camera.CameraScreen
import com.myplaces.app.ui.map.MapScreen
import com.myplaces.app.ui.settings.SettingsScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    // Stocke le chemin photo entre CameraScreen et AddPlaceScreen
    var pendingPhotoPath by remember { mutableStateOf<String?>(null) }

    NavHost(
        navController = navController,
        startDestination = Screen.Map.route
    ) {
        composable(Screen.Map.route) {
            MapScreen(
                onNavigateToAddPlace = { lat, lng ->
                    navController.navigate(Screen.AddPlace.createRoute(lat, lng))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.AddPlace.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lng") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
            val lng = backStackEntry.arguments?.getFloat("lng")?.toDouble() ?: 0.0

            AddPlaceScreen(
                lat = lat,
                lng = lng,
                savedPhotoPath = pendingPhotoPath,
                onNavigateBack = {
                    pendingPhotoPath = null
                    navController.popBackStack()
                },
                onNavigateToCamera = {
                    navController.navigate(Screen.Camera.route)
                }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onPhotoTaken = { path ->
                    pendingPhotoPath = path
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
