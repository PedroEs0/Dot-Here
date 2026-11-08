package com.crochet.app.ui.products

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.crochet.app.domain.model.ProductFace
import java.io.File
import java.util.concurrent.Executors

/**
 * Asistente de captura guiada de 6 caras para la reconstrucción 3D en Dot-Here.
 * Utiliza CameraX con guías visuales de encuadre y permite capturar secuencialmente
 * las 6 caras ortogonales (Frontal, Trasera, Izquierda, Derecha, Superior, Inferior).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidedCaptureScreen(
    productId: Long,
    viewModel: ProductViewModel,
    onFinishCapture: (Map<ProductFace, String>) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentFace by viewModel.currentCaptureFace.collectAsState()
    val capturedFaces by viewModel.capturedFacesMap.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Permiso de cámara necesario para el escaneo 3D", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Cámara CameraX
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val imageCapture = remember { ImageCapture.Builder().build() }

    // Selector de galería como método alternativo
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.saveFaceImageFromUri(productId, currentFace, it) { savedPath ->
                Toast.makeText(context, "${currentFace.title} guardada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Captura con cámara nativa del sistema (fallback)
    var cameraTempUriString by rememberSaveable { mutableStateOf<String?>(null) }
    val systemCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && cameraTempUriString != null) {
            val uri = Uri.parse(cameraTempUriString)
            viewModel.saveFaceImageFromUri(productId, currentFace, uri) {
                Toast.makeText(context, "${currentFace.title} guardada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Asistente 3D: 6 Caras",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${capturedFaces.size} de 6 caras capturadas",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    if (capturedFaces.isNotEmpty()) {
                        FilledTonalButton(
                            onClick = { onFinishCapture(capturedFaces) },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Done, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Listo")
                        }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            // Vista previa de cámara en tiempo real
            if (hasCameraPermission) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageCapture
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Concede permiso de cámara para ver la guía de captura en tiempo real.",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }

            // Guía visual de encuadre 3D superpuesta
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Marco delimitador de silueta
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ViewInAr,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                        modifier = Modifier
                            .size(72.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            // Cabecera superior con instrucciones de la cara actual
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.75f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentFace.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = currentFace.guideHint,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Barra inferior con selector de caras y botones de captura
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(vertical = 12.dp)
            ) {
                // Tira horizontal con el progreso de las 6 caras
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ProductFace.entries) { face ->
                        val isSelected = face == currentFace
                        val isCaptured = capturedFaces.containsKey(face)
                        val facePath = capturedFaces[face]

                        FaceThumbnailChip(
                            face = face,
                            isSelected = isSelected,
                            isCaptured = isCaptured,
                            imagePath = facePath,
                            onClick = { viewModel.selectCaptureFace(face) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Controles de disparo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón para elegir desde galería
                    IconButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = "Galería", tint = Color.White)
                    }

                    // Botón principal de Captura CameraX (con fallback a TakePicture)
                    Button(
                        onClick = {
                            if (hasCameraPermission) {
                                val productDir = viewModel.createCameraUri()
                                val destFile = File(context.cacheDir, "scan_${currentFace.name}.jpg")
                                val outputOptions = ImageCapture.OutputFileOptions.Builder(destFile).build()

                                imageCapture.takePicture(
                                    outputOptions,
                                    cameraExecutor,
                                    object : ImageCapture.OnImageSavedCallback {
                                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                            val savedUri = Uri.fromFile(destFile)
                                            viewModel.saveFaceImageFromUri(productId, currentFace, savedUri) {
                                                try {
                                                    if (destFile.exists()) destFile.delete()
                                                } catch (_: Exception) {}
                                                ContextCompat.getMainExecutor(context).execute {
                                                    Toast.makeText(context, "${currentFace.title} registrada", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            // Fallback al launcher del sistema
                                            ContextCompat.getMainExecutor(context).execute {
                                                val uri = viewModel.createCameraUri()
                                                cameraTempUriString = uri.toString()
                                                systemCameraLauncher.launch(uri)
                                            }
                                        }
                                    }
                                )
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.size(72.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Tomar Foto",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Botón para finalizar y volver
                    FilledTonalButton(
                        onClick = { onFinishCapture(capturedFaces) },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Terminar")
                    }
                }
            }
        }
    }
}

/**
 * Chip miniatura interactivo para una cara en la tira de captura.
 */
@Composable
private fun FaceThumbnailChip(
    face: ProductFace,
    isSelected: Boolean,
    isCaptured: Boolean,
    imagePath: String?,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            ),
        color = Color(0xFF2A2421)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                if (!imagePath.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(File(imagePath))
                            .size(128, 128)
                            .crossfade(true)
                            .build(),
                        contentDescription = face.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (isCaptured) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                text = face.name.take(3),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = Color.White
            )
        }
    }
}
