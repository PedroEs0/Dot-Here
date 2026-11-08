package com.crochet.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utilidad de almacenamiento optimizada para dispositivos de gama media.
 * Aplica reducción automática a resolución máxima de 1024x1024 px, compresión JPEG al 80%
 * y reciclaje explícito inmediato de Bitmaps para evitar errores de memoria (OutOfMemoryError).
 */
@Singleton
class FileStorageHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val materialsDir: File
        get() = File(context.filesDir, "materials").apply {
            if (!exists()) {
                mkdirs()
            }
        }

    /**
     * Guarda y comprime una imagen de material en context.filesDir/materials/
     * Redimensiona a un máximo de 1024x1024 px con compresión JPEG 80%.
     */
    suspend fun saveImageFromUri(sourceUri: Uri): String? = withContext(Dispatchers.IO) {
        val fileName = "mat_${UUID.randomUUID()}.jpg"
        val destinationFile = File(materialsDir, fileName)
        compressAndSaveImage(sourceUri, destinationFile, maxDimension = 1024, quality = 80)
    }

    /**
     * Genera un archivo temporal físico y su URI a través de FileProvider para que
     * la aplicación de cámara pueda escribir la foto capturada sin fallos de permisos.
     */
    fun createTempCameraUri(): Uri {
        val cacheFolder = File(context.cacheDir, "camera_photos").apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val tempFile = File.createTempFile("photo_", ".jpg", cacheFolder).apply {
            createNewFile()
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    /**
     * Obtiene o crea la carpeta privada para almacenar las 6 caras de un producto.
     * Ruta: context.filesDir/products/{productId}/
     */
    fun getProductDir(productId: Long): File {
        return File(File(context.filesDir, "products"), productId.toString()).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    /**
     * Guarda y comprime la imagen de una cara de producto en context.filesDir/products/{productId}/{faceName}.jpg
     * Aplica reducción a máximo 1024x1024 px, compresión JPEG 80% y reciclaje explícito.
     */
    suspend fun saveProductFaceImage(
        productId: Long,
        faceName: String,
        sourceUri: Uri
    ): String? = withContext(Dispatchers.IO) {
        val productDir = getProductDir(productId)
        val destinationFile = File(productDir, "${faceName.lowercase()}.jpg")
        compressAndSaveImage(sourceUri, destinationFile, maxDimension = 1024, quality = 80)
    }

    /**
     * Motor de compresión y reescalado seguro en memoria:
     * 1. Lee dimensiones sin cargar el Bitmap a RAM (inJustDecodeBounds).
     * 2. Calcula inSampleSize para decodificar una imagen reducida directamente.
     * 3. Corrige la rotación EXIF de la cámara si aplica.
     * 4. Ajusta el tamaño final a un máximo de 1024x1024 px conservando relación de aspecto.
     * 5. Comprime en JPEG al 80% en disco.
     * 6. Llama a recycle() de inmediato para liberar memoria nativa en el acto.
     */
    private fun compressAndSaveImage(
        sourceUri: Uri,
        destinationFile: File,
        maxDimension: Int = 1024,
        quality: Int = 80
    ): String? {
        var decodedBitmap: Bitmap? = null
        var rotatedBitmap: Bitmap? = null
        var scaledBitmap: Bitmap? = null

        try {
            // Paso 1: Leer dimensiones de origen sin alocar memoria de pixeles
            val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            openInputStream(sourceUri)?.use { input ->
                BitmapFactory.decodeStream(input, null, boundsOptions)
            }

            val origWidth = boundsOptions.outWidth
            val origHeight = boundsOptions.outHeight
            if (origWidth <= 0 || origHeight <= 0) return null

            // Paso 2: Calcular inSampleSize para decodificar submuestreado
            var sampleSize = 1
            val maxOriginal = maxOf(origWidth, origHeight)
            while (maxOriginal / (sampleSize * 2) >= maxDimension) {
                sampleSize *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }

            // Paso 3: Decodificar con tamaño reducido
            decodedBitmap = openInputStream(sourceUri)?.use { input ->
                BitmapFactory.decodeStream(input, null, decodeOptions)
            } ?: return null

            // Paso 4: Ajustar orientación EXIF
            val rotationDegrees = getExifOrientationDegrees(sourceUri)
            val orientedBitmap = if (rotationDegrees != 0) {
                val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
                rotatedBitmap = Bitmap.createBitmap(
                    decodedBitmap, 0, 0, decodedBitmap.width, decodedBitmap.height, matrix, true
                )
                if (decodedBitmap != rotatedBitmap) {
                    decodedBitmap.recycle()
                    decodedBitmap = null
                }
                rotatedBitmap
            } else {
                decodedBitmap
            }

            // Paso 5: Escalar con precisión al límite de 1024 px
            val currentMax = maxOf(orientedBitmap.width, orientedBitmap.height)
            val finalBitmap = if (currentMax > maxDimension) {
                val scale = maxDimension.toFloat() / currentMax.toFloat()
                val targetWidth = (orientedBitmap.width * scale).toInt().coerceAtLeast(1)
                val targetHeight = (orientedBitmap.height * scale).toInt().coerceAtLeast(1)
                scaledBitmap = Bitmap.createScaledBitmap(orientedBitmap, targetWidth, targetHeight, true)
                if (orientedBitmap != scaledBitmap) {
                    orientedBitmap.recycle()
                }
                scaledBitmap
            } else {
                orientedBitmap
            }

            // Paso 6: Escribir archivo JPEG al 80% de calidad
            destinationFile.parentFile?.mkdirs()
            FileOutputStream(destinationFile).use { outputStream ->
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                outputStream.flush()
            }

            // Paso 7: Liberación explícita inmediata de memoria nativa
            finalBitmap.recycle()

            return destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            // Aseguramos reciclaje de cualquier referencia intermedia
            decodedBitmap?.let { if (!it.isRecycled) it.recycle() }
            rotatedBitmap?.let { if (!it.isRecycled) it.recycle() }
            scaledBitmap?.let { if (!it.isRecycled) it.recycle() }
        }
    }

    private fun getExifOrientationDegrees(uri: Uri): Int {
        return try {
            val orientation = if (uri.scheme == "file" || uri.scheme == null) {
                val file = File(uri.path ?: "")
                if (file.exists()) {
                    val exif = ExifInterface(file.absolutePath)
                    exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                } else {
                    ExifInterface.ORIENTATION_NORMAL
                }
            } else {
                openInputStream(uri)?.use { stream ->
                    val exif = ExifInterface(stream)
                    exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                } ?: ExifInterface.ORIENTATION_NORMAL
            }
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            0
        }
    }

    private fun openInputStream(uri: Uri): InputStream? {
        return if (uri.scheme == "file" || uri.scheme == null) {
            val file = File(uri.path ?: "")
            if (file.exists()) java.io.FileInputStream(file) else null
        } else {
            context.contentResolver.openInputStream(uri)
        }
    }

    /**
     * Elimina completamente la carpeta y todas las fotos asociadas a un producto.
     */
    suspend fun deleteProductFolder(productId: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val productDir = File(File(context.filesDir, "products"), productId.toString())
            if (productDir.exists()) {
                productDir.deleteRecursively()
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Elimina una imagen del almacenamiento interno a partir de su ruta absoluta.
     */
    suspend fun deleteImage(absolutePath: String?): Boolean = withContext(Dispatchers.IO) {
        if (absolutePath.isNullOrBlank()) return@withContext false
        try {
            val file = File(absolutePath)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
