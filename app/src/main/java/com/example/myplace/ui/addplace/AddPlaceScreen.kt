package com.example.myplace.ui.addplace

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

val EMOJI_LIST = listOf(
    "😀", "😍", "😢", "😡", "😎", "🤔", "😴",
    "☕", "🍕", "🍷", "🍰",
    "🌳", "🌊", "⛰️", "🌅", "🌸",
    "🏃", "📚", "🎵", "✈️"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceScreen(
    latitude: Double,
    longitude: Double,
    onNavigateBack: () -> Unit,
    onNavigateToCamera: () -> Unit,
    viewModel: AddPlaceViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("😀") }

    val isSaving by viewModel.isSaving.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()

    // Retour auto quand c'est sauvegardé
    LaunchedEffect(isSaved) {
        if (isSaved) onNavigateBack()
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
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Titre
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sélecteur d'émoji
            Text("Choisir un émoji", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            EmojiPicker(
                selectedEmoji = selectedEmoji,
                onEmojiSelected = { selectedEmoji = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Coordonnées (info)
            Text(
                text = "📍 ${"%.4f".format(latitude)}, ${"%.4f".format(longitude)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Bouton sauvegarder
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.savePlace(
                            title = title,
                            description = description,
                            emoji = selectedEmoji,
                            latitude = latitude,
                            longitude = longitude,
                            photoPath = "",
                            authorId = "local_user",
                            authorName = "Moi"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving && title.isNotBlank()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Sauvegarder")
                }
            }
        }
    }
}

@Composable
fun EmojiPicker(
    selectedEmoji: String,
    onEmojiSelected: (String) -> Unit
) {
    val chunked = EMOJI_LIST.chunked(5)
    Column {
        chunked.forEach { row ->
            Row {
                row.forEach { emoji ->
                    TextButton(
                        onClick = { onEmojiSelected(emoji) }
                    ) {
                        Text(
                            text = emoji,
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (emoji == selectedEmoji)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}