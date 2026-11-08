package com.crochet.app.ui.products

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crochet.app.domain.model.Material
import com.crochet.app.domain.model.Product
import com.crochet.app.domain.model.ProductFace
import com.crochet.app.domain.model.Tag
import com.crochet.app.domain.repository.InventoryRepository
import com.crochet.app.domain.repository.ProductRepository
import com.crochet.app.util.FileStorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val allTags: List<Tag> = emptyList(),
    val searchQuery: String = "",
    val selectedTagIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false
)

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val inventoryRepository: InventoryRepository,
    private val fileStorageHelper: FileStorageHelper
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedTagIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedTagIds = _selectedTagIds.asStateFlow()

    val allMaterials: StateFlow<List<Material>> = inventoryRepository.getMaterialsWithTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTags: StateFlow<List<Tag>> = inventoryRepository.getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<ProductListUiState> = combine(
        _searchQuery,
        _selectedTagIds,
        allTags,
        _searchQuery.flatMapLatest { query -> productRepository.getProductsWithDetails(query) }
    ) { query, selectedTags, tags, productList ->
        val filtered = if (selectedTags.isEmpty()) {
            productList
        } else {
            productList.filter { product ->
                product.tags.any { tag -> tag.id in selectedTags }
            }
        }
        ProductListUiState(
            products = filtered,
            allTags = tags,
            searchQuery = query,
            selectedTagIds = selectedTags,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProductListUiState(isLoading = true))

    // Estado del Asistente de Captura Guiada (6 Caras) conectado al repositorio compartido
    private val _currentCaptureFace = MutableStateFlow(ProductFace.FRONT)
    val currentCaptureFace = _currentCaptureFace.asStateFlow()

    val capturedFacesMap: StateFlow<Map<ProductFace, String>> = productRepository.currentDraftFaces

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTagFilterToggle(tagId: Long) {
        _selectedTagIds.update { current ->
            if (current.contains(tagId)) current - tagId else current + tagId
        }
    }

    fun clearTagFilters() {
        _selectedTagIds.value = emptySet()
    }

    fun getProduct(id: Long): Flow<Product?> {
        return productRepository.getProductById(id)
    }

    // Métodos para el flujo de captura guiada
    fun selectCaptureFace(face: ProductFace) {
        _currentCaptureFace.value = face
    }

    fun setInitialFaces(existingFaces: Map<ProductFace, String?>) {
        productRepository.setDraftFaces(existingFaces)
    }

    fun clearCaptureState() {
        productRepository.clearDraftFaces()
    }

    fun recordCapturedFace(face: ProductFace, path: String) {
        productRepository.updateDraftFace(face, path)
        face.nextFace?.let { next ->
            _currentCaptureFace.value = next
        }
    }

    fun saveFaceImageFromUri(
        productId: Long,
        face: ProductFace,
        sourceUri: Uri,
        onSaved: (String) -> Unit
    ) {
        viewModelScope.launch {
            val savedPath = productRepository.saveFaceImage(productId, face, sourceUri)
            if (savedPath != null) {
                face.nextFace?.let { next ->
                    _currentCaptureFace.value = next
                }
                onSaved(savedPath)
            }
        }
    }

    fun createCameraUri(): Uri {
        return fileStorageHelper.createTempCameraUri()
    }

    fun saveProduct(
        product: Product,
        materialIds: List<Long>,
        tagIds: List<Long>,
        onComplete: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val targetId = if (product.id == 0L) {
                productRepository.insertProduct(product, materialIds, tagIds)
            } else {
                productRepository.updateProduct(product, materialIds, tagIds)
                product.id
            }
            productRepository.clearDraftFaces()
            onComplete(targetId)
        }
    }

    fun deleteProduct(productId: Long, onComplete: () -> Unit) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId)
            onComplete()
        }
    }
}
