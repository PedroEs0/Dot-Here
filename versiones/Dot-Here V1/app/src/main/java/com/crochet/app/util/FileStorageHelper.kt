package com.crochet.app.util

import android.content.Context
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
 * Utilidad para gestionar el guardado y eliminación de imágenes en el almacenamiento
 * interno privado de la aplicación (context.filesDir/materials/).
 * Garantiza persistencia offline y no expone URIs temporales del sistema.
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
     * Copia una imagen desde una Uri (galería o cámara) al almacenamiento interno privado.
     * Retorna la ruta absoluta del archivo guardado.
     */
    suspend fun saveImageFromUri(sourceUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val fileName = "mat_${UUID.randomUUID()}.jpg"
            val destinationFile = File(materialsDir, fileName)

            val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
            val outputStream = FileOutputStream(destinationFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Genera un archivo temporal físico y su URI a través de FileProvider para que
     * la aplicación de cámara pueda escribir la foto capturada sin fallos de permisos ni archivo inexistente.
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
