package com.example.myplace.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val biometricEnabled by viewModel.biometricEnabled.collectAsState()
    val isExporting by viewModel.isExporting.collectAsState()
    val isImporting by viewModel.isImporting.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paramètres") },
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
                .fillMaxSize()
        ) {
            // Toggle biométrie
            Text("Sécurité", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Verrouillage biométrique")
                Switch(
                    checked = biometricEnabled,
                    onCheckedChange = { viewModel.setBiometricEnabled(it) }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Export
            Text("Données", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.exportPlaces() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isExporting
            ) {
                if (isExporting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Exporter mes lieux (JSON)")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Import
            OutlinedButton(
                onClick = { viewModel.importPlaces() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isImporting
            ) {
                if (isImporting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Importer des lieux (JSON)")
                }
            }
        }
    }
}