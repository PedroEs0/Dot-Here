package com.crochet.app.ui.inventory

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.crochet.app.ui.tags.parseHexColor
import java.io.File

/**
 * Pantalla de creación y edición de materiales.
 * Permite capturar fotos mediante cámara o galería, definir unidades y vincular etiquetas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialFormScreen(
    materialId: Long? = null,
    viewModel: InventoryViewModel,
    onNavigateBack: () -> Unit
) {
    val isEditing = materialId != null && materialId > 0
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("ovillos") }
    var notes by remember { mutableStateOf("") }
    var selectedTagIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var existingImagePath by remember { mutableStateOf<String?>(null) }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }

    var cameraTempUriString by rememberSaveable { mutableStateOf<String?>(null) }
    var showPhotoOptionsDialog by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf(false) }
    var quantityError by remember { mutableStateOf(false) }

    // Si estamos en modo edición, cargamos el material existente
    LaunchedEffect(materialId) {
        if (isEditing && materialId != null) {
            viewModel.getMaterial(materialId).collect { material ->
                material?.let {
                    name = it.name
                    quantityText = formatQuantity(it.quantity)
                    unit = it.unit
                    notes = it.notes ?: ""
                    existingImagePath = it.imagePath
                    selectedTagIds = it.tags.map { t -> t.id }.toSet()
                }
            }
        }
    }

    // Selector de Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            newImageUri = it
        }
    }

    // Captura con Cámara física
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && cameraTempUriString != null) {
            newImageUri = Uri.parse(cameraTempUriString)
        }
    }

    // Función segura para crear URI y abrir la cámara física
    val launchCameraSafe: () -> Unit = {
        try {
            val uri = viewModel.createCameraUri()
            cameraTempUriString = uri.toString()
            cameraLauncher.launch(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "No se pudo iniciar la cámara: ${e.localizedMessage ?: "error"}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Solicitud de permiso de cámara en tiempo de ejecución
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launchCameraSafe()
        } else {
            Toast.makeText(
                context,
                "Permiso de cámara denegado. Puedes seleccionar una foto desde la Galería.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Diálogo selector de origen de foto (Cámara o Galería)
    if (showPhotoOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoOptionsDialog = false },
            title = { Text("Seleccionar foto del insumo") },
            text = { Text("Elige cómo deseas agregar la foto local del material:") },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoOptionsDialog = false
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        launchCameraSafe()
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {
                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cámara")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoOptionsDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Galería")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEditing) "Editar Insumo" else "Nuevo Insumo")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val qty = quantityText.toDoubleOrNull()
                            val isNameValid = name.isNotBlank()
                            val isQtyValid = qty != null && qty >= 0

                            nameError = !isNameValid
                            quantityError = !isQtyValid

                            if (isNameValid && isQtyValid && qty != null) {
                                viewModel.saveMaterial(
                                    id = if (isEditing) materialId ?: 0L else 0L,
                                    name = name,
                                    quantity = qty,
                                    unit = unit,
                                    newImageUri = newImageUri,
                                    existingImagePath = existingImagePath,
                                    notes = notes,
                                    tagIds = selectedTagIds.toList(),
                                    onComplete = onNavigateBack
                                )
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Guardar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Contenedor de Foto con selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { showPhotoOptionsDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        newImageUri != null -> {
                            AsyncImage(
                                model = newImageUri,
                                contentDescription = "Foto seleccionada",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        !existingImagePath.isNullOrBlank() -> {
                            AsyncImage(
                                model = File(existingImagePath!!),
                                contentDescription = "Foto actual",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        else -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Toca para agregar foto (Cámara / Galería)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Nombre del material
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                label = { Text("Nombre del material *") },
                placeholder = { Text("Ej. Hilo Macramé Beige") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = nameError,
                supportingText = {
                    if (nameError) Text("El nombre es obligatorio", color = MaterialTheme.colorScheme.error)
                }
            )

            // Cantidad y Unidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = {
                        quantityText = it
                        quantityError = false
                    },
                    label = { Text("Cantidad *") },
                    placeholder = { Text("Ej. 4") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    isError = quantityError,
                    supportingText = {
                        if (quantityError) Text("Cantidad inválida", color = MaterialTheme.colorScheme.error)
                    }
                )

                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unidad *") },
                    placeholder = { Text("ovillos") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            // Chips rápidos para unidades estándar
            Text(
                text = "Unidades sugeridas:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val suggestedUnits = listOf("ovillos", "gramos", "unidades", "mm", "metros")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(suggestedUnits) { suggested ->
                    SuggestionChip(
                        onClick = { unit = suggested },
                        label = { Text(suggested) }
                    )
                }
            }

            // Selector de etiquetas
            Text(
                text = "Vincular Etiquetas:",
                style = MaterialTheme.typography.titleMedium
            )

            if (uiState.allTags.isEmpty()) {
                Text(
                    text = "No hay etiquetas creadas. Puedes crearlas en el menú principal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.allTags) { tag ->
                        val isSelected = selectedTagIds.contains(tag.id)
                        val tagColor = remember(tag.colorHex) { parseHexColor(tag.colorHex) }

                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedTagIds = if (isSelected) {
                                    selectedTagIds - tag.id
                                } else {
                                    selectedTagIds + tag.id
                                }
                            },
                            leadingIcon = {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(tagColor)
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.surface,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                            },
                            label = { Text(tag.name) }
                        )
                    }
                }
            }

            // Notas
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas adicionales") },
                placeholder = { Text("Ej. Color número 42, para proyecto de tapiz.") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val qty = quantityText.toDoubleOrNull()
                    val isNameValid = name.isNotBlank()
                    val isQtyValid = qty != null && qty >= 0

                    nameError = !isNameValid
                    quantityError = !isQtyValid

                    if (isNameValid && isQtyValid && qty != null) {
                        viewModel.saveMaterial(
                            id = if (isEditing) materialId ?: 0L else 0L,
                            name = name,
                            quantity = qty,
                            unit = unit,
                            newImageUri = newImageUri,
                            existingImagePath = existingImagePath,
                            notes = notes,
                            tagIds = selectedTagIds.toList(),
                            onComplete = onNavigateBack
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Actualizar Insumo" else "Guardar Insumo")
            }
        }
    }
}
