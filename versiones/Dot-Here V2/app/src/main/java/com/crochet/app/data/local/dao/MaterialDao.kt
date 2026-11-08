package com.crochet.app.data.local.dao

import androidx.room.*
import com.crochet.app.data.local.entity.MaterialEntity
import com.crochet.app.data.local.entity.MaterialTagCrossRef
import com.crochet.app.data.local.relation.MaterialWithTags
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operaciones sobre la tabla 'materials'
 * y sus asociaciones en 'material_tag_cross_ref'.
 */
@Dao
interface MaterialDao {

    @Transaction
    @Query("""
        SELECT * FROM materials
        WHERE (:searchQuery = '' OR name LIKE '%' || :searchQuery || '%' OR notes LIKE '%' || :searchQuery || '%')
        ORDER BY createdAt DESC
    """)
    fun getMaterialsWithTags(searchQuery: String): Flow<List<MaterialWithTags>>

    @Transaction
    @Query("SELECT * FROM materials WHERE id = :id LIMIT 1")
    fun getMaterialWithTagsById(id: Long): Flow<MaterialWithTags?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity): Long

    @Update
    suspend fun updateMaterial(material: MaterialEntity)

    @Query("DELETE FROM materials WHERE id = :id")
    suspend fun deleteMaterialById(id: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRefs(crossRefs: List<MaterialTagCrossRef>)

    @Query("DELETE FROM material_tag_cross_ref WHERE materialId = :materialId")
    suspend fun deleteCrossRefsForMaterial(materialId: Long)

    /**
     * Inserta un material y genera sus referencias a etiquetas de forma atómica.
     */
    @Transaction
    suspend fun insertMaterialWithTags(material: MaterialEntity, tagIds: List<Long>): Long {
        val materialId = insertMaterial(material)
        if (tagIds.isNotEmpty()) {
            val refs = tagIds.map { MaterialTagCrossRef(materialId = materialId, tagId = it) }
            insertCrossRefs(refs)
        }
        return materialId
    }

    /**
     * Actualiza un material y sincroniza las etiquetas asignadas dentro de una transacción.
     */
    @Transaction
    suspend fun updateMaterialWithTags(material: MaterialEntity, tagIds: List<Long>) {
        updateMaterial(material)
        deleteCrossRefsForMaterial(material.id)
        if (tagIds.isNotEmpty()) {
            val refs = tagIds.map { MaterialTagCrossRef(materialId = material.id, tagId = it) }
            insertCrossRefs(refs)
        }
    }
}
