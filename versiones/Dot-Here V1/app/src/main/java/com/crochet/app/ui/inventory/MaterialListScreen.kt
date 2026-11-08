package com.crochet.app.ui.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.crochet.app.domain.model.Material
import com.crochet.app.domain.model.Tag
import com.crochet.app.ui.tags.TagManagerDialog
import com.crochet.app.ui.tags.parseHexColor
import java.io.File

/**
 * Pantalla principal del catálogo de insumos de Crochet.
 * Permite buscar en tiempo real, filtrar por etiquetas y acceder a la creación/edición.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialListScreen(
    viewModel: InventoryViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (materialId: Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showTagDialog by remember { mutableStateOf(false) }

    if (showTagDialog) {
        TagManagerDialog(
            tags = uiState.allTags,
            onDismissRequest = { showTagDialog = false },
            onCreateTag = { name, hex -> viewModel.createTag(name, hex) },
            onDeleteTag = { id -> viewModel.deleteTag(id) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dot-Here",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showTagDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Label,
                            contentDescription = "Gestionar Etiquetas"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nuevo Insumo")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra de búsqueda por texto (filtra por nombre o notas)
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar por nombre o notas...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Chips horizontales para filtrar por etiquetas
            if (uiState.allTags.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedTagIds.isEmpty(),
                            onClick = { viewModel.clearTagFilters() },
                            label = { Text("Todos") }
                        )
                    }
                    items(uiState.allTags) { tag ->
                        val isSelected = uiState.selectedTagIds.contains(tag.id)
                        val tagColor = remember(tag.colorHex) { parseHexColor(tag.colorHex) }

                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onTagFilterToggle(tag.id) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(tagColor)
                                )
                            },
                            label = { Text(tag.name) }
                        )
                    }
                }
            }

            // Lista reactiva de materiales
            if (uiState.materials.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (uiState.searchQuery.isNotBlank() || uiState.selectedTagIds.isNotEmpty()) {
                            "No se encontraron insumos con los filtros seleccionados."
                        } else {
                            "Tu inventario en Dot-Here está vacío.\nPulsa el botón + para registrar tu primer material."
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.materials, key = { it.id }) { material ->
                        MaterialItemCard(
                            material = material,
                            onClick = { onNavigateToDetail(material.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta individual para mostrar un material con su foto local, cantidad y etiquetas.
 */
@Composable
fun MaterialItemCard(
    material: Material,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del material (desde almacenamiento interno privado o placeholder)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (!material.imagePath.isNullOrBlank()) {
                    AsyncImage(
                        model = File(material.imagePath),
                        contentDescription = material.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = material.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = material.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Stock: ${formatQuantity(material.quantity)} ${material.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                if (!material.notes.isNullOrBlank()) {
                    Text(
                        text = material.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Badges de etiquetas asociadas
                if (material.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(material.tags) { tag ->
                            TagBadge(tag = tag)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Insignia visual con color HEX para una etiqueta.
 */
@Composable
fun TagBadge(tag: Tag) {
    val tagColor = remember(tag.colorHex) { parseHexColor(tag.colorHex) }
    Surface(
        color = tagColor.copy(alpha = 0.2f),
        contentColor = tagColor,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, tagColor.copy(alpha = 0.6f))
    ) {
        Text(
            text = tag.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

fun formatQuantity(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        value.toString()
    }
}
