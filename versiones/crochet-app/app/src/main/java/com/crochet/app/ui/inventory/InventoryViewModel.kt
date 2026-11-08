package com.crochet.app.ui.inventory

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crochet.app.domain.model.Material
import com.crochet.app.domain.model.Tag
import com.crochet.app.domain.repository.InventoryRepository
import com.crochet.app.util.FileStorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de la UI para la gestión de inventario y etiquetas.
 */
data class InventoryUiState(
    val materials: List<Material> = emptyList(),
    val allTags: List<Tag> = emptyList(),
    val searchQuery: String = "",
    val selectedTagIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false
)

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: InventoryRepository,
    private val fileStorageHelper: FileStorageHelper
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedTagIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedTagIds = _selectedTagIds.asStateFlow()

    private val _allTags = repository.getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flujo unificado reactivo que combina búsqueda por texto y filtrado por chips de tags
    val uiState: StateFlow<InventoryUiState> = combine(
        _searchQuery,
        _selectedTagIds,
        _allTags,
        _searchQuery.flatMapLatest { query -> repository.getMaterialsWithTags(query) }
    ) { query, selectedTags, tags, materialsList ->
        // Si hay etiquetas seleccionadas, filtramos los materiales que contengan al menos una de ellas
        val filteredMaterials = if (selectedTags.isEmpty()) {
            materialsList
        } else {
            materialsList.filter { material ->
                material.tags.any { tag -> tag.id in selectedTags }
            }
        }

        InventoryUiState(
            materials = filteredMaterials,
            allTags = tags,
            searchQuery = query,
            selectedTagIds = selectedTags,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InventoryUiState(isLoading = true))

    /**
     * Actualiza el texto de búsqueda en tiempo real.
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    /**
     * Alterna la selección de un chip de etiqueta para filtrar la lista.
     */
    fun onTagFilterToggle(tagId: Long) {
        _selectedTagIds.update { current ->
            if (current.contains(tagId)) {
                current - tagId
            } else {
                current + tagId
            }
        }
    }

    /**
     * Limpia los filtros de etiquetas activos.
     */
    fun clearTagFilters() {
        _selectedTagIds.value = emptySet()
    }

    /**
     * Obtiene el flujo de un material específico para la pantalla de detalle o edición.
     */
    fun getMaterial(id: Long): Flow<Material?> {
        return repository.getMaterialById(id)
    }

    /**
     * Guarda o actualiza un material, copiando la imagen localmente si se seleccionó una nueva.
     */
    fun saveMaterial(
        id: Long = 0L,
        name: String,
        quantity: Double,
        unit: String,
        newImageUri: Uri? = null,
        existingImagePath: String? = null,
        notes: String? = null,
        tagIds: List<Long>,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            var finalImagePath = existingImagePath

            // Si el usuario seleccionó una nueva foto, la copiamos al almacenamiento interno privado
            if (newImageUri != null) {
                val savedPath = fileStorageHelper.saveImageFromUri(newImageUri)
                if (savedPath != null) {
                    // Si se está reemplazando una imagen anterior, limpiamos la vieja
                    if (!existingImagePath.isNullOrBlank() && existingImagePath != savedPath) {
                        fileStorageHelper.deleteImage(existingImagePath)
                    }
                    finalImagePath = savedPath
                }
            }

            val material = Material(
                id = id,
                name = name.trim(),
                quantity = quantity,
                unit = unit.trim(),
                imagePath = finalImagePath,
                notes = notes?.trim()?.ifBlank { null }
            )

            if (id == 0L) {
                repository.insertMaterial(material, tagIds)
            } else {
                repository.updateMaterial(material, tagIds)
            }
            onComplete()
        }
    }

    /**
     * Elimina un material del inventario y su imagen asociada.
     */
    fun deleteMaterial(materialId: Long, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteMaterial(materialId)
            onComplete()
        }
    }

    /**
     * Crea una nueva etiqueta personalizada.
     */
    fun createTag(name: String, colorHex: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.insertTag(Tag(name = name.trim(), colorHex = colorHex))
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    /**
     * Elimina una etiqueta. Por integridad referencial en SQLite, sus referencias se borran
     * y los materiales permanecen intactos.
     */
    fun deleteTag(tagId: Long) {
        viewModelScope.launch {
            // Si la etiqueta estaba seleccionada en el filtro, la quitamos
            _selectedTagIds.update { it - tagId }
            repository.deleteTag(tagId)
        }
    }

    /**
     * Genera un URI temporal seguro para la captura de fotos con la cámara física.
     */
    fun createCameraUri(): Uri {
        return fileStorageHelper.createTempCameraUri()
    }
}
