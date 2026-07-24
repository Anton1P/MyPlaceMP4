package com.myplaces.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.myplaces.app.di.dataStore
import com.myplaces.app.ui.biometric.BiometricLockScreen
import com.myplaces.app.ui.navigation.NavGraph
import com.myplaces.app.ui.theme.MyPlacesTheme
import com.myplaces.app.util.UserPreferencesKeys
import com.myplaces.app.util.BiometricHelper
import com.myplaces.app.util.initUserIfNeeded
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            // Initialise UUID + prefs au premier lancement
            initUserIfNeeded(dataStore)
        }

        setContent {
            MyPlacesTheme {
                var isUnlocked by remember { mutableStateOf(false) }
                var biometricEnabled by remember { mutableStateOf(false) }
                var isCheckingBiometric by remember { mutableStateOf(true) }

                // Lecture DataStore pour savoir si biometrie activee
                LaunchedEffect(Unit) {
                    val prefs = dataStore.data.first()
                    biometricEnabled = prefs[UserPreferencesKeys.BIOMETRIC_ENABLED] ?: false
                    if (!biometricEnabled) isUnlocked = true
                    isCheckingBiometric = false
                }

                fun launchBiometric() {
                    BiometricHelper.showBiometricPrompt(
                        activity = this@MainActivity,
                        onSuccess = { isUnlocked = true },
                        onError = { /* reste verrouille */ }
                    )
                }

                // Declenche le prompt biometrique au demarrage si necessaire
                LaunchedEffect(isCheckingBiometric, biometricEnabled) {
                    if (!isCheckingBiometric && biometricEnabled && !isUnlocked) {
                        launchBiometric()
                    }
                }

                when {
                    isCheckingBiometric -> { /* Ecran vide pendant la lecture DataStore */ }
                    !isUnlocked -> BiometricLockScreen(onRetry = { launchBiometric() })
                    else -> NavGraph()
                }
            }
        }
    }
}
