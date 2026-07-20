package com.myplaces.app.ui.addplace

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.myplaces.app.MyPlacesApplication
import com.myplaces.app.data.local.entity.PlaceEntity
import com.myplaces.app.di.dataStore
import com.myplaces.app.util.UserPreferencesKeys
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddPlaceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as MyPlacesApplication).container.placeRepository
    private val dataStore = application.dataStore

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var selectedEmoji by mutableStateOf("😀")
    var photoPath by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var isSaved by mutableStateOf(false)

    fun savePlace(lat: Double, lng: Double) {
        if (title.isBlank()) return
        viewModelScope.launch {
            isLoading = true
            val prefs = dataStore.data.first()
            val authorId = prefs[UserPreferencesKeys.USER_UUID] ?: "unknown"
            val authorName = prefs[UserPreferencesKeys.USER_NAME] ?: "Moi"

            // Reverse geocoding (ne bloque pas si echec)
            val address = repository.resolveAddress(lat, lng)

            val place = PlaceEntity(
                title = title,
                description = description,
                latitude = lat,
                longitude = lng,
                address = address,
                emoji = selectedEmoji,
                photoPath = photoPath,
                authorId = authorId,
                authorName = authorName,
                createdAt = System.currentTimeMillis()
            )
            repository.insertPlace(place)
            isLoading = false
            isSaved = true
        }
    }
}
