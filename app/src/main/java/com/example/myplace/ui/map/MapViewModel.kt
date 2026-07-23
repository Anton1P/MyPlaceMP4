package com.example.myplace.ui.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplace.MyPlacesApplication
import com.example.myplace.data.local.PlaceEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as MyPlacesApplication).container.placeRepository

    // Liste de tous les lieux — mise à jour automatique via Flow
    val places: StateFlow<List<PlaceEntity>> = repository.getAllPlaces()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Supprimer un lieu
    fun deletePlace(place: PlaceEntity) {
        viewModelScope.launch {
            repository.deletePlace(place)
        }
    }
}