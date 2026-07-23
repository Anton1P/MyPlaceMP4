package com.example.myplace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.myplace.biometric.BiometricLockScreen
import com.example.myplace.ui.theme.MyPlaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyPlaceTheme {
                var isUnlocked by remember { mutableStateOf(false) }
                // Pour l'instant biométrie désactivée par défaut
                // On affiche directement la carte
                val biometricEnabled = false

                if (biometricEnabled && !isUnlocked) {
                    BiometricLockScreen(
                        onUnlocked = { isUnlocked = true }
                    )
                } else {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}