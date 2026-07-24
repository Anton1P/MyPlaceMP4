package com.myplaces.app.ui.settings

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.myplaces.app.MyPlacesApplication
import com.myplaces.app.di.dataStore
import com.myplaces.app.util.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as MyPlacesApplication).container.placeRepository
    private val dataStore = application.dataStore

    val biometricEnabled: StateFlow<Boolean> = dataStore.getBiometricEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    var importResult: Int? = null
        private set

    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.setBiometricEnabled(enabled)
        }
    }

    fun exportJournal(context: Context) {
        viewModelScope.launch {
            val prefs = dataStore.data.first()
            val authorId = prefs[UserPreferencesKeys.USER_UUID] ?: ""
            val authorName = prefs[UserPreferencesKeys.USER_NAME] ?: "Moi"
            val places = repository.getAllMyPlacesForExport()
            JsonExporter.exportPlaces(context, places, authorId, authorName)
        }
    }

    fun importJournal(context: Context, uri: Uri, onDone: (Int) -> Unit) {
        viewModelScope.launch {
            val count = JsonImporter.importFromUri(context, uri, repository)
            importResult = count
            onDone(count)
        }
    }
}
