package com.example.myplace.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplace.MyPlacesApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as MyPlacesApplication).container.placeRepository

    private val _biometricEnabled = MutableStateFlow(false)
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _biometricEnabled.value = enabled
        }
    }

    fun exportPlaces() {
        viewModelScope.launch {
            _isExporting.value = true
            // TODO: implémenter l'export JSON
            _isExporting.value = false
        }
    }

    fun importPlaces() {
        viewModelScope.launch {
            _isImporting.value = true
            // TODO: implémenter l'import JSON
            _isImporting.value = false
        }
    }
}