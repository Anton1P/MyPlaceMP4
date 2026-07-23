package com.myplaces.app.ui.map

import android.Manifest
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.myplaces.app.data.local.entity.PlaceEntity
import com.myplaces.app.ui.components.PlaceDetailSheet
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    onNavigateToAddPlace: (lat: Double, lng: Double) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    // --- Permissions de localisation ---
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    // Demander les permissions au premier affichage
    LaunchedEffect(Unit) {
        locationPermissionState.launchMultiplePermissionRequest()
    }
    val isLocationGranted = locationPermissionState.allPermissionsGranted

    val places by viewModel.places.collectAsStateWithLifecycle()
    var selectedPlace by remember { mutableStateOf<PlaceEntity?>(null) }

    // Position initiale: Paris
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(48.8566, 2.3522), 12f)
    }

    // --- Récupérer la position et centrer la carte ---
    LaunchedEffect(isLocationGranted) {
        if (isLocationGranted) {
            try {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                val location = fusedClient.lastLocation.await()
                if (location != null) {
                    cameraPositionState.animate(
                        update = com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            15f
                        )
                    )
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    val mapProperties = remember(isLocationGranted) {
        MapProperties(isMyLocationEnabled = isLocationGranted)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Places") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Paramètres")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val center = cameraPositionState.position.target
                    onNavigateToAddPlace(center.latitude, center.longitude)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un lieu")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                onMapLongClick = { latLng ->
                    onNavigateToAddPlace(latLng.latitude, latLng.longitude)
                }
            ) {
                places.forEach { place ->
                    Marker(
                        state = MarkerState(position = LatLng(place.latitude, place.longitude)),
                        title = "${place.emoji} ${place.title}",
                        snippet = place.address ?: "",
                        onClick = {
                            selectedPlace = place
                            false
                        }
                    )
                }
            }

            // Indicateur si la carte est vide
            if (places.isEmpty()) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "🗺️ Appui long sur la carte pour ajouter un lieu !",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    // ModalBottomSheet détail lieu
    selectedPlace?.let { place ->
        PlaceDetailSheet(
            place = place,
            onDismiss = { selectedPlace = null },
            onDelete = { viewModel.deletePlace(it) }
        )
    }
}
