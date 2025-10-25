package com.makeyourhomeai.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.makeyourhomeai.viewmodel.TransformViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransformScreen(
    imageUri: Uri?,
    onNavigateToCamera: () -> Unit,
    viewModel: TransformViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var selectedRoomType by remember { mutableStateOf("living_room") }
    var selectedStyle by remember { mutableStateOf("modern") }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.resetState()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trasforma il tuo Ambiente") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Immagine selezionata",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Text(
                        text = "Tipo di Ambiente",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedRoomType == "living_room",
                            onClick = { selectedRoomType = "living_room" },
                            label = { Text("Soggiorno") }
                        )
                        FilterChip(
                            selected = selectedRoomType == "bedroom",
                            onClick = { selectedRoomType = "bedroom" },
                            label = { Text("Camera") }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedRoomType == "kitchen",
                            onClick = { selectedRoomType = "kitchen" },
                            label = { Text("Cucina") }
                        )
                        FilterChip(
                            selected = selectedRoomType == "bathroom",
                            onClick = { selectedRoomType = "bathroom" },
                            label = { Text("Bagno") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Stile Ambiente",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedStyle == "modern",
                            onClick = { selectedStyle = "modern" },
                            label = { Text("Moderno") }
                        )
                        FilterChip(
                            selected = selectedStyle == "minimalist",
                            onClick = { selectedStyle = "minimalist" },
                            label = { Text("Minimal") }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedStyle == "industrial",
                            onClick = { selectedStyle = "industrial" },
                            label = { Text("Industrial") }
                        )
                        FilterChip(
                            selected = selectedStyle == "scandinavian",
                            onClick = { selectedStyle = "scandinavian" },
                            label = { Text("Nordico") }
                        )
                    }
                }
            }

            when (val state = uiState) {
                is TransformViewModel.UiState.Idle -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                imageUri?.let {
                                    viewModel.transformImage(
                                        context = context,
                                        imageUri = it,
                                        roomType = selectedRoomType,
                                        style = selectedStyle
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = imageUri != null
                        ) {
                            Text("Trasforma")
                        }

                        OutlinedButton(
                            onClick = onNavigateToCamera,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Scatta Foto")
                        }

                        FilledTonalButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Galleria")
                        }
                    }
                }

                is TransformViewModel.UiState.Loading -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "Trasformazione in corso...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                is TransformViewModel.UiState.Success -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Risultato",
                                style = MaterialTheme.typography.titleLarge
                            )
                            AsyncImage(
                                model = state.imageUrl,
                                contentDescription = "Immagine trasformata",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                imageUri?.let {
                                    viewModel.transformImage(
                                        context = context,
                                        imageUri = it,
                                        roomType = selectedRoomType,
                                        style = selectedStyle
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ritrasforma")
                        }

                        OutlinedButton(
                            onClick = onNavigateToCamera,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Nuova Foto")
                        }
                    }
                }

                is TransformViewModel.UiState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Errore",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                imageUri?.let {
                                    viewModel.transformImage(
                                        context = context,
                                        imageUri = it,
                                        roomType = selectedRoomType,
                                        style = selectedStyle
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Riprova")
                        }

                        OutlinedButton(
                            onClick = onNavigateToCamera,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Nuova Foto")
                        }
                    }
                }
            }
        }
    }
}