package com.example.myplace.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myplace.MyPlacesApplication
import com.example.myplace.data.local.PlaceEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaceDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as MyPlacesApplication).container.placeRepository

    private val _place = MutableStateFlow<PlaceEntity?>(null)
    val place: StateFlow<PlaceEntity?> = _place

    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted

    fun loadPlace(id: Long) {
        viewModelScope.launch {
            _place.value = repository.getPlaceById(id)
        }
    }

    fun deletePlace(place: PlaceEntity) {
        viewModelScope.launch {
            repository.deletePlace(place)
            _isDeleted.value = true
        }
    }
}