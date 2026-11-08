package com.crochet.app.domain.repository

import android.net.Uri
import com.crochet.app.domain.model.Product
import com.crochet.app.domain.model.ProductFace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Contrato de repositorio para la gestión de productos tejidos y sus 6 texturas.
 */
interface ProductRepository {
    fun getProductsWithDetails(searchQuery: String = ""): Flow<List<Product>>
    fun getProductById(id: Long): Flow<Product?>
    fun getProductsForMaterial(materialId: Long): Flow<List<Product>>
    suspend fun insertProduct(product: Product, materialIds: List<Long>, tagIds: List<Long>): Long
    suspend fun updateProduct(product: Product, materialIds: List<Long>, tagIds: List<Long>)
    suspend fun deleteProduct(productId: Long)
    suspend fun saveFaceImage(productId: Long, face: ProductFace, sourceUri: Uri): String?

    val currentDraftFaces: StateFlow<Map<ProductFace, String>>
    fun updateDraftFace(face: ProductFace, path: String)
    fun setDraftFaces(faces: Map<ProductFace, String?>)
    fun clearDraftFaces()
}
