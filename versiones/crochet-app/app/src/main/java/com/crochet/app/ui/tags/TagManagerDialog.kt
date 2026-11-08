package com.crochet.app.ui.tags

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.crochet.app.domain.model.Tag
import com.crochet.app.ui.theme.TagColorPalette

/**
 * Diálogo modal para la administración de etiquetas.
 * Permite:
 * 1. Crear nuevas etiquetas con selector de color.
 * 2. Visualizar y eliminar etiquetas existentes.
 */
@Composable
fun TagManagerDialog(
    tags: List<Tag>,
    onDismissRequest: () -> Unit,
    onCreateTag: (name: String, colorHex: String) -> Unit,
    onDeleteTag: (tagId: Long) -> Unit
) {
    var tagName by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf(TagColorPalette.first()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Cabecera con botón de cierre
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gestor de Etiquetas",
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = onDismissRequest) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Campo para el nombre de la etiqueta
                OutlinedTextField(
                    value = tagName,
                    onValueChange = {
                        tagName = it
                        errorMessage = null
                    },
                    label = { Text("Nombre de la etiqueta") },
                    placeholder = { Text("Ej. Algodón rústico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Selecciona un color:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Selector horizontal de paleta de colores
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(TagColorPalette) { hex ->
                        val color = remember(hex) { parseHexColor(hex) }
                        val isSelected = selectedColorHex.equals(hex, ignoreCase = true)

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorHex = hex }
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Seleccionado",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val trimmed = tagName.trim()
                        if (trimmed.isBlank()) {
                            errorMessage = "El nombre no puede estar vacío"
                        } else if (tags.any { it.name.equals(trimmed, ignoreCase = true) }) {
                            errorMessage = "Ya existe una etiqueta con este nombre"
                        } else {
                            onCreateTag(trimmed, selectedColorHex)
                            tagName = ""
                            errorMessage = null
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crear Etiqueta")
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Listado de etiquetas existentes
                Text(
                    text = "Etiquetas creadas (${tags.size})",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (tags.isEmpty()) {
                    Text(
                        text = "Aún no has creado etiquetas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        tags.forEach { tag ->
                            val tagColor = remember(tag.colorHex) { parseHexColor(tag.colorHex) }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(tagColor)
                                    )
                                    Text(
                                        text = tag.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteTag(tag.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar etiqueta",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Función utilitaria para transformar cadenas HEX en objetos Compose Color seguros.
 */
fun parseHexColor(hexString: String): Color {
    return try {
        val cleanHex = hexString.removePrefix("#")
        val colorInt = if (cleanHex.length == 6) {
            android.graphics.Color.parseColor("#$cleanHex")
        } else if (cleanHex.length == 8) {
            android.graphics.Color.parseColor("#$cleanHex")
        } else {
            android.graphics.Color.DKGRAY
        }
        Color(colorInt)
    } catch (e: Exception) {
        Color.Gray
    }
}
