package com.crochet.app.data.repository

import android.net.Uri
import com.crochet.app.data.local.dao.ProductDao
import com.crochet.app.data.local.entity.MaterialEntity
import com.crochet.app.data.local.entity.ProductEntity
import com.crochet.app.data.local.entity.TagEntity
import com.crochet.app.data.local.relation.ProductWithDetails
import com.crochet.app.domain.model.Material
import com.crochet.app.domain.model.Product
import com.crochet.app.domain.model.ProductFace
import com.crochet.app.domain.model.Tag
import com.crochet.app.domain.repository.ProductRepository
import com.crochet.app.util.FileStorageHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de productos y sus 6 texturas 3D.
 */
@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val fileStorageHelper: FileStorageHelper
) : ProductRepository {

    private val _currentDraftFaces = MutableStateFlow<Map<ProductFace, String>>(emptyMap())
    override val currentDraftFaces: StateFlow<Map<ProductFace, String>> = _currentDraftFaces.asStateFlow()

    override fun updateDraftFace(face: ProductFace, path: String) {
        _currentDraftFaces.update { it + (face to path) }
    }

    override fun setDraftFaces(faces: Map<ProductFace, String?>) {
        val filtered = faces.filterValues { !it.isNullOrBlank() } as Map<ProductFace, String>
        _currentDraftFaces.value = filtered
    }

    override fun clearDraftFaces() {
        _currentDraftFaces.value = emptyMap()
    }

    override fun getProductsWithDetails(searchQuery: String): Flow<List<Product>> {
        return productDao.getProductsWithDetails(searchQuery).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getProductById(id: Long): Flow<Product?> {
        return productDao.getProductWithDetailsById(id).map { it?.toDomain() }
    }

    override fun getProductsForMaterial(materialId: Long): Flow<List<Product>> {
        return productDao.getProductsForMaterial(materialId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertProduct(
        product: Product,
        materialIds: List<Long>,
        tagIds: List<Long>
    ): Long {
        return productDao.insertProductWithDetails(product.toEntity(), materialIds, tagIds)
    }

    override suspend fun updateProduct(
        product: Product,
        materialIds: List<Long>,
        tagIds: List<Long>
    ) {
        productDao.updateProductWithDetails(product.toEntity(), materialIds, tagIds)
    }

    override suspend fun deleteProduct(productId: Long) {
        fileStorageHelper.deleteProductFolder(productId)
        productDao.deleteProductById(productId)
    }

    override suspend fun saveFaceImage(
        productId: Long,
        face: ProductFace,
        sourceUri: Uri
    ): String? {
        val savedPath = fileStorageHelper.saveProductFaceImage(productId, face.name, sourceUri)
        if (savedPath != null) {
            updateDraftFace(face, savedPath)
        }
        return savedPath
    }

    // Funciones de mapeo Entity <-> Domain
    private fun ProductWithDetails.toDomain(): Product {
        return Product(
            id = product.id,
            name = product.name,
            description = product.description,
            price = product.price,
            dimensions = product.dimensions,
            frontImagePath = product.frontImagePath,
            backImagePath = product.backImagePath,
            leftImagePath = product.leftImagePath,
            rightImagePath = product.rightImagePath,
            topImagePath = product.topImagePath,
            bottomImagePath = product.bottomImagePath,
            createdAt = product.createdAt,
            materials = materials.map { it.toDomain() },
            tags = tags.map { it.toDomain() }
        )
    }

    private fun MaterialEntity.toDomain(): Material {
        return Material(
            id = id,
            name = name,
            quantity = quantity,
            unit = unit,
            imagePath = imagePath,
            notes = notes,
            createdAt = createdAt
        )
    }

    private fun TagEntity.toDomain(): Tag {
        return Tag(
            id = id,
            name = name,
            colorHex = colorHex
        )
    }

    private fun Product.toEntity(): ProductEntity {
        return ProductEntity(
            id = id,
            name = name,
            description = description,
            price = price,
            dimensions = dimensions,
            frontImagePath = frontImagePath,
            backImagePath = backImagePath,
            leftImagePath = leftImagePath,
            rightImagePath = rightImagePath,
            topImagePath = topImagePath,
            bottomImagePath = bottomImagePath,
            createdAt = createdAt
        )
    }
}
