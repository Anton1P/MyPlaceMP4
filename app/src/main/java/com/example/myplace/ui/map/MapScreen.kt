package com.example.myplace.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myplace.data.local.PlaceEntity
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen(
    onNavigateToAddPlace: (Double, Double) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: MapViewModel = viewModel()
) {
    val places by viewModel.places.collectAsState()
    var selectedPlace by remember { mutableStateOf<PlaceEntity?>(null) }

    // Position par défaut : Paris
    val defaultLocation = LatLng(48.8566, 2.3522)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLongClick = { latLng ->
                onNavigateToAddPlace(latLng.latitude, latLng.longitude)
            }
        ) {
            // Afficher un marqueur pour chaque lieu
            places.forEach { place ->
                Marker(
                    state = MarkerState(
                        position = LatLng(place.latitude, place.longitude)
                    ),
                    title = place.emoji + " " + place.title,
                    snippet = place.description,
                    onClick = {
                        onNavigateToDetail(place.id)
                        true
                    }
                )
            }
        }

        // Bouton Settings en haut à droite
        IconButton(
            onClick = onNavigateToSettings,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Paramètres",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Bouton Ajouter un lieu en bas à droite
        FloatingActionButton(
            onClick = {
                val center = cameraPositionState.position.target
                onNavigateToAddPlace(center.latitude, center.longitude)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Ajouter un lieu")
        }
    }
}