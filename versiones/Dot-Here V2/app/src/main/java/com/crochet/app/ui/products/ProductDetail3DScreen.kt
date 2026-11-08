package com.crochet.app.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.crochet.app.domain.model.Product
import com.crochet.app.domain.model.ProductFace
import com.crochet.app.ui.inventory.TagBadge
import com.crochet.app.ui.inventory.formatDate
import java.io.File

/**
 * Pantalla de detalle de producto: muestra las 6 fotos capturadas como
 * un carrusel 2D deslizable y la información comercial del producto.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetail3DScreen(
    productId: Long,
    viewModel: ProductViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (productId: Long) -> Unit,
    onNavigateToRecapture: (productId: Long) -> Unit
) {
    val productState by viewModel.getProduct(productId).collectAsState(initial = null)
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Eliminar producto") },
            text = { Text("¿Estás seguro de que deseas eliminar este producto y sus fotos?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deleteProduct(productId) { onNavigateBack() }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToRecapture(productId) }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Capturar fotos")
                    }
                    IconButton(onClick = { onNavigateToEdit(productId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
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
        productState?.let { product ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── Carrusel 2D de caras ──────────────────────────────────────
                FaceCarousel(product = product)

                // ── Info del producto ─────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Nombre + chip de caras
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        AssistChip(
                            onClick = { onNavigateToRecapture(productId) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text("${product.capturedFacesCount}/6 Fotos") }
                        )
                    }

                    // Precio y dimensiones
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Precio de Venta",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = if (product.price > 0) "$ ${"%,.0f".format(product.price)} COP" else "No especificado",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            if (!product.dimensions.isNullOrBlank()) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "Dimensiones",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = product.dimensions,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }

                    // Insumos vinculados
                    if (product.materials.isNotEmpty()) {
                        Text(
                            "Insumos utilizados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            product.materials.forEach { mat ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
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
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                mat.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                "Stock disponible: ${mat.quantity} ${mat.unit}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Etiquetas
                    if (product.tags.isNotEmpty()) {
                        Text(
                            "Etiquetas vinculadas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(product.tags) { tag -> TagBadge(tag = tag) }
                        }
                    }

                    // Descripción / técnica
                    if (!product.description.isNullOrBlank()) {
                        Text(
                            "Detalles y Técnica de Tejido",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = product.description,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }

                    // Fecha
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Registrado el: ${formatDate(product.createdAt)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Carrusel 2D deslizable de las 6 caras capturadas
// Usa LazyRow snap-scroll para evitar APIs experimentales del Pager.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FaceCarousel(product: Product) {
    val faceItems = remember(product) {
        listOf(
            ProductFace.FRONT  to product.frontImagePath,
            ProductFace.BACK   to product.backImagePath,
            ProductFace.LEFT   to product.leftImagePath,
            ProductFace.RIGHT  to product.rightImagePath,
            ProductFace.TOP    to product.topImagePath,
            ProductFace.BOTTOM to product.bottomImagePath
        )
    }

    val listState = rememberLazyListState()

    // Calcula la página visible más centrada
    val currentPage by remember {
        derivedStateOf {
            val firstVisible = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            // Si se ha desplazado más de la mitad del ítem, avanza al siguiente
            if (offset > 0) minOf(firstVisible + 1, faceItems.lastIndex)
            else firstVisible
        }
    }

    val currentFace = faceItems[currentPage].first

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(0xFF1A1A1A))
    ) {
        // Carrusel deslizable
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(faceItems) { (face, path) ->
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .fillParentMaxHeight()
                ) {
                    FacePage(face = face, imagePath = path)
                }
            }
        }

        // Indicadores de punto en la parte inferior
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            faceItems.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(if (currentPage == index) 10.dp else 7.dp)
                        .clip(CircleShape)
                        .background(
                            if (currentPage == index) Color.White
                            else Color.White.copy(alpha = 0.4f)
                        )
                )
            }
        }

        // Etiqueta flotante con el nombre de la cara actual
        Surface(
            color = Color.Black.copy(alpha = 0.55f),
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = currentFace.title,
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun FacePage(face: ProductFace, imagePath: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (!imagePath.isNullOrBlank() && File(imagePath).exists()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(File(imagePath))
                    .size(1024, 1024)
                    .crossfade(true)
                    .build(),
                contentDescription = face.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Placeholder cuando la cara aún no fue capturada
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = face.title,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Sin foto aún",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
