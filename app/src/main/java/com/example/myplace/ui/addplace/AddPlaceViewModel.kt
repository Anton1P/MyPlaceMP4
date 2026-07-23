package com.example.myplace.ui.addplace

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplace.MyPlacesApplication
import com.example.myplace.data.local.PlaceEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddPlaceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as MyPlacesApplication).container.placeRepository

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved

    fun savePlace(
        title: String,
        description: String,
        emoji: String,
        latitude: Double,
        longitude: Double,
        photoPath: String,
        authorId: String,
        authorName: String
    ) {
        viewModelScope.launch {
            _isSaving.value = true

            // Reverse geocoding — récupérer l'adresse
            val address = repository.resolveAddress(latitude, longitude)

            val place = PlaceEntity(
                title = title,
                description = description,
                emoji = emoji,
                latitude = latitude,
                longitude = longitude,
                address = address,
                photoPath = photoPath,
                authorId = authorId,
                authorName = authorName
            )

            repository.insertPlace(place)
            _isSaving.value = false
            _isSaved.value = true
        }
    }
}