package com.crochet.app.data.local.dao

import androidx.room.*
import com.crochet.app.data.local.entity.ProductEntity
import com.crochet.app.data.local.entity.ProductMaterialCrossRef
import com.crochet.app.data.local.entity.ProductTagCrossRef
import com.crochet.app.data.local.relation.ProductWithDetails
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la entidad de productos artesanales,
 * asociaciones con insumos y etiquetas.
 */
@Dao
interface ProductDao {

    @Transaction
    @Query("""
        SELECT * FROM products
        WHERE (:searchQuery = '' OR name LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%')
        ORDER BY createdAt DESC
    """)
    fun getProductsWithDetails(searchQuery: String): Flow<List<ProductWithDetails>>

    @Transaction
    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun getProductWithDetailsById(id: Long): Flow<ProductWithDetails?>

    @Transaction
    @Query("""
        SELECT p.* FROM products p
        INNER JOIN product_material_cross_ref pm ON p.id = pm.productId
        WHERE pm.materialId = :materialId
        ORDER BY p.createdAt DESC
    """)
    fun getProductsForMaterial(materialId: Long): Flow<List<ProductWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMaterialCrossRefs(crossRefs: List<ProductMaterialCrossRef>)

    @Query("DELETE FROM product_material_cross_ref WHERE productId = :productId")
    suspend fun deleteMaterialCrossRefsForProduct(productId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTagCrossRefs(crossRefs: List<ProductTagCrossRef>)

    @Query("DELETE FROM product_tag_cross_ref WHERE productId = :productId")
    suspend fun deleteTagCrossRefsForProduct(productId: Long)

    /**
     * Inserta un producto y genera sus asociaciones a materiales y etiquetas atómicamente.
     */
    @Transaction
    suspend fun insertProductWithDetails(
        product: ProductEntity,
        materialIds: List<Long>,
        tagIds: List<Long>
    ): Long {
        val productId = insertProduct(product)
        if (materialIds.isNotEmpty()) {
            val matRefs = materialIds.map { ProductMaterialCrossRef(productId = productId, materialId = it) }
            insertMaterialCrossRefs(matRefs)
        }
        if (tagIds.isNotEmpty()) {
            val tagRefs = tagIds.map { ProductTagCrossRef(productId = productId, tagId = it) }
            insertTagCrossRefs(tagRefs)
        }
        return productId
    }

    /**
     * Actualiza un producto y sincroniza sus materiales y etiquetas asociadas.
     */
    @Transaction
    suspend fun updateProductWithDetails(
        product: ProductEntity,
        materialIds: List<Long>,
        tagIds: List<Long>
    ) {
        updateProduct(product)
        deleteMaterialCrossRefsForProduct(product.id)
        deleteTagCrossRefsForProduct(product.id)

        if (materialIds.isNotEmpty()) {
            val matRefs = materialIds.map { ProductMaterialCrossRef(productId = product.id, materialId = it) }
            insertMaterialCrossRefs(matRefs)
        }
        if (tagIds.isNotEmpty()) {
            val tagRefs = tagIds.map { ProductTagCrossRef(productId = product.id, tagId = it) }
            insertTagCrossRefs(tagRefs)
        }
    }
}
