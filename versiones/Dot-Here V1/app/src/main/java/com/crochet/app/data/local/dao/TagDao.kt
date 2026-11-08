package com.crochet.app.data.local.dao

import androidx.room.*
import com.crochet.app.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la gestión de etiquetas.
 * Cumple la regla de que al borrar una etiqueta, sus vínculos en 'material_tag_cross_ref'
 * se eliminan en cascada sin alterar los materiales asociados.
 */
@Dao
interface TagDao {

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity): Long

    @Query("DELETE FROM tags WHERE id = :tagId")
    suspend fun deleteTagById(tagId: Long)
}
