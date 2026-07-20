package com.myplaces.app.ui.addplace

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.myplaces.app.ui.components.EmojiPicker
import com.myplaces.app.util.FileUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceScreen(
    lat: Double,
    lng: Double,
    onNavigateBack: () -> Unit,
    onNavigateToCamera: () -> Unit,
    savedPhotoPath: String? = null,
    viewModel: AddPlaceViewModel = viewModel()
) {
    val context = LocalContext.current

    // Recuperer la photo depuis la camera si retour
    LaunchedEffect(savedPhotoPath) {
        if (!savedPhotoPath.isNullOrBlank()) {
            viewModel.photoPath = savedPhotoPath
        }
    }

    // Naviguer apres sauvegarde
    LaunchedEffect(viewModel.isSaved) {
        if (viewModel.isSaved) onNavigateBack()
    }

    // Galerie
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val copiedPath = FileUtils.copyUriToInternalStorage(context, it)
            if (copiedPath != null) viewModel.photoPath = copiedPath
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouveau lieu") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Coordonnees (info)
            Text(
                text = "📍 ${String.format("%.5f", lat)}, ${String.format("%.5f", lng)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )

            // Titre
            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                label = { Text("Titre *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Description
            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = { Text("Description") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            // Emoji
            Text("Emoji", style = MaterialTheme.typography.labelLarge)
            EmojiPicker(
                selectedEmoji = viewModel.selectedEmoji,
                onEmojiSelected = { viewModel.selectedEmoji = it }
            )

            // Photo
            Text("Photo", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onNavigateToCamera,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Camera")
                }
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Galerie")
                }
            }

            // Preview photo
            if (!viewModel.photoPath.isNullOrBlank()) {
                AsyncImage(
                    model = File(viewModel.photoPath!!),
                    contentDescription = "Photo choisie",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bouton valider
            Button(
                onClick = { viewModel.savePlace(lat, lng) },
                enabled = viewModel.title.isNotBlank() && !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Enregistrer le lieu")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
