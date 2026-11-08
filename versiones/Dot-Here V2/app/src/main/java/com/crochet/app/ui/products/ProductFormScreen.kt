package com.crochet.app.ui.products

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ViewInAr
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.crochet.app.domain.model.Product
import com.crochet.app.domain.model.ProductFace
import com.crochet.app.ui.tags.parseHexColor
import java.io.File

/**
 * Formulario para creación y edición de productos tejidos en crochet (V2).
 * Permite ingresar información comercial, seleccionar insumos de V1 usados,
 * vincular etiquetas y gestionar las 6 fotos del escaneo 3D.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productId: Long? = null,
    viewModel: ProductViewModel,
    onNavigateToGuidedCapture: (productId: Long) -> Unit,
    onNavigateTo3DPreview: (productId: Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val isEditing = productId != null && productId > 0
    val stableProductId = rememberSaveable(productId) {
        if (isEditing && productId != null) productId else System.currentTimeMillis()
    }

    val allMaterials by viewModel.allMaterials.collectAsState()
    val allTags by viewModel.allTags.collectAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var dimensions by remember { mutableStateOf("") }

    var selectedMaterialIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var selectedTagIds by remember { mutableStateOf<Set<Long>>(emptySet()) }

    val capturedFacesMap by viewModel.capturedFacesMap.collectAsState()

    var nameError by remember { mutableStateOf(false) }
    var showMaterialDialog by remember { mutableStateOf(false) }

    if (showMaterialDialog) {
        MaterialSelectorDialog(
            allMaterials = allMaterials,
            initialSelectedIds = selectedMaterialIds,
            onDismissRequest = { showMaterialDialog = false },
            onConfirmSelection = { newSelectedIds ->
                selectedMaterialIds = newSelectedIds
                showMaterialDialog = false
            }
        )
    }

    // Cargar datos si estamos en modo edición
    LaunchedEffect(productId) {
        if (isEditing && productId != null) {
            viewModel.getProduct(productId).collect { product ->
                product?.let {
                    name = it.name
                    description = it.description ?: ""
                    priceText = if (it.price > 0) it.price.toString() else ""
                    dimensions = it.dimensions ?: ""
                    selectedMaterialIds = it.materials.map { m -> m.id }.toSet()
                    selectedTagIds = it.tags.map { t -> t.id }.toSet()

                    viewModel.setInitialFaces(
                        mapOf(
                            ProductFace.FRONT to it.frontImagePath,
                            ProductFace.BACK to it.backImagePath,
                            ProductFace.LEFT to it.leftImagePath,
                            ProductFace.RIGHT to it.rightImagePath,
                            ProductFace.TOP to it.topImagePath,
                            ProductFace.BOTTOM to it.bottomImagePath
                        )
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Producto" else "Nuevo Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (name.isBlank()) {
                                nameError = true
                                return@IconButton
                            }
                            val price = priceText.toDoubleOrNull() ?: 0.0
                            val productToSave = Product(
                                id = if (isEditing) productId ?: 0L else 0L,
                                name = name.trim(),
                                description = description.trim().ifBlank { null },
                                price = price,
                                dimensions = dimensions.trim().ifBlank { null },
                                frontImagePath = capturedFacesMap[ProductFace.FRONT],
                                backImagePath = capturedFacesMap[ProductFace.BACK],
                                leftImagePath = capturedFacesMap[ProductFace.LEFT],
                                rightImagePath = capturedFacesMap[ProductFace.RIGHT],
                                topImagePath = capturedFacesMap[ProductFace.TOP],
                                bottomImagePath = capturedFacesMap[ProductFace.BOTTOM]
                            )
                            viewModel.saveProduct(
                                product = productToSave,
                                materialIds = selectedMaterialIds.toList(),
                                tagIds = selectedTagIds.toList(),
                                onComplete = { onNavigateBack() }
                            )
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
            // Sección de Captura 3D (6 Caras)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Mapeo 3D de Texturas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${capturedFacesMap.size} de 6 caras capturadas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        FilledTonalButton(
                            onClick = {
                                onNavigateToGuidedCapture(stableProductId)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Capturar")
                        }
                    }

                    // Grilla de miniaturas de las 6 caras
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProductFace.entries.forEach { face ->
                            val path = capturedFacesMap[face]
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .clickable {
                                            viewModel.selectCaptureFace(face)
                                            onNavigateToGuidedCapture(stableProductId)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!path.isNullOrBlank()) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(File(path))
                                                .size(128, 128)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = face.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Text(
                                            text = face.name.take(2),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                                Text(
                                    text = face.name.take(3),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }

                    // Botón para previsualizar en 3D si hay fotos capturadas
                    if (capturedFacesMap.isNotEmpty()) {
                        OutlinedButton(
                            onClick = {
                                if (isEditing && productId != null) {
                                    onNavigateTo3DPreview(productId)
                                } else {
                                    val price = priceText.toDoubleOrNull() ?: 0.0
                                    val tempProduct = Product(
                                        id = 0L,
                                        name = name.trim().ifBlank { "Tejido 3D" },
                                        description = description.trim().ifBlank { null },
                                        price = price,
                                        dimensions = dimensions.trim().ifBlank { null },
                                        frontImagePath = capturedFacesMap[ProductFace.FRONT],
                                        backImagePath = capturedFacesMap[ProductFace.BACK],
                                        leftImagePath = capturedFacesMap[ProductFace.LEFT],
                                        rightImagePath = capturedFacesMap[ProductFace.RIGHT],
                                        topImagePath = capturedFacesMap[ProductFace.TOP],
                                        bottomImagePath = capturedFacesMap[ProductFace.BOTTOM]
                                    )
                                    viewModel.saveProduct(
                                        product = tempProduct,
                                        materialIds = selectedMaterialIds.toList(),
                                        tagIds = selectedTagIds.toList(),
                                        onComplete = { newId ->
                                            onNavigateTo3DPreview(newId)
                                        }
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.ViewInAr, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Abrir en Visor 3D Interactivo")
                        }
                    }
                }
            }

            // Nombre del producto
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                label = { Text("Nombre del producto tejido *") },
                placeholder = { Text("Ej. Bolso Wayuu Geométrico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = nameError,
                supportingText = {
                    if (nameError) Text("El nombre es requerido", color = MaterialTheme.colorScheme.error)
                }
            )

            // Precio y Dimensiones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Precio estimado ($)") },
                    placeholder = { Text("Ej. 45000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = dimensions,
                    onValueChange = { dimensions = it },
                    label = { Text("Dimensiones") },
                    placeholder = { Text("Ej. 25x20x10 cm") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            // Insumos vinculados al producto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Insumos vinculados (${selectedMaterialIds.size}):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = { showMaterialDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Vincular Insumos")
                }
            }

            val linkedMaterials = remember(selectedMaterialIds, allMaterials) {
                allMaterials.filter { it.id in selectedMaterialIds }
            }

            if (linkedMaterials.isEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMaterialDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Toca para vincular lanas, hilos o agujas usadas en este producto.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    linkedMaterials.forEach { mat ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!mat.imagePath.isNullOrBlank()) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(File(mat.imagePath))
                                                    .size(128, 128)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = mat.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Text(
                                                text = mat.name.take(2).uppercase(),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = mat.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Stock disponible: ${mat.quantity} ${mat.unit}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { selectedMaterialIds = selectedMaterialIds - mat.id }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Desvincular",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Etiquetas
            Text(
                text = "Etiquetas de clasificación:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (allTags.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(allTags) { tag ->
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
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(tagColor)
                                )
                            },
                            label = { Text(tag.name) }
                        )
                    }
                }
            }

            // Descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción o técnica de tejido") },
                placeholder = { Text("Ej. Tejido con punto bajo y punto red, técnica tradicional de Cúcuta.") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    val price = priceText.toDoubleOrNull() ?: 0.0
                    val productToSave = Product(
                        id = if (isEditing) productId ?: 0L else 0L,
                        name = name.trim(),
                        description = description.trim().ifBlank { null },
                        price = price,
                        dimensions = dimensions.trim().ifBlank { null },
                        frontImagePath = capturedFacesMap[ProductFace.FRONT],
                        backImagePath = capturedFacesMap[ProductFace.BACK],
                        leftImagePath = capturedFacesMap[ProductFace.LEFT],
                        rightImagePath = capturedFacesMap[ProductFace.RIGHT],
                        topImagePath = capturedFacesMap[ProductFace.TOP],
                        bottomImagePath = capturedFacesMap[ProductFace.BOTTOM]
                    )
                    viewModel.saveProduct(
                        product = productToSave,
                        materialIds = selectedMaterialIds.toList(),
                        tagIds = selectedTagIds.toList(),
                        onComplete = { onNavigateBack() }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Actualizar Producto" else "Guardar Producto")
            }
        }
    }
}
