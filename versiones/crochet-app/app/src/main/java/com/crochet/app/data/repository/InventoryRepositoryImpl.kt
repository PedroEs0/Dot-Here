package com.crochet.app.data.repository

import com.crochet.app.data.local.dao.MaterialDao
import com.crochet.app.data.local.dao.TagDao
import com.crochet.app.data.local.entity.MaterialEntity
import com.crochet.app.data.local.entity.TagEntity
import com.crochet.app.data.local.relation.MaterialWithTags
import com.crochet.app.domain.model.Material
import com.crochet.app.domain.model.Tag
import com.crochet.app.domain.repository.InventoryRepository
import com.crochet.app.util.FileStorageHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación concreta del repositorio de inventario con Room.
 * Realiza el mapeo entre entidades de datos y modelos de dominio.
 */
@Singleton
class InventoryRepositoryImpl @Inject constructor(
    private val materialDao: MaterialDao,
    private val tagDao: TagDao,
    private val fileStorageHelper: FileStorageHelper
) : InventoryRepository {

    override fun getMaterialsWithTags(searchQuery: String): Flow<List<Material>> {
        return materialDao.getMaterialsWithTags(searchQuery).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getMaterialById(id: Long): Flow<Material?> {
        return materialDao.getMaterialWithTagsById(id).map { it?.toDomain() }
    }

    override fun getAllTags(): Flow<List<Tag>> {
        return tagDao.getAllTags().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertMaterial(material: Material, tagIds: List<Long>): Long {
        return materialDao.insertMaterialWithTags(material.toEntity(), tagIds)
    }

    override suspend fun updateMaterial(material: Material, tagIds: List<Long>) {
        materialDao.updateMaterialWithTags(material.toEntity(), tagIds)
    }

    override suspend fun deleteMaterial(materialId: Long) {
        // Obtenemos el material antes de borrarlo para limpiar su archivo de imagen local si existe
        val currentMaterial = materialDao.getMaterialWithTagsById(materialId).firstOrNull()?.material
        currentMaterial?.imagePath?.let { path ->
            fileStorageHelper.deleteImage(path)
        }
        materialDao.deleteMaterialById(materialId)
    }

    override suspend fun insertTag(tag: Tag): Long {
        return tagDao.insertTag(tag.toEntity())
    }

    override suspend fun deleteTag(tagId: Long) {
        tagDao.deleteTagById(tagId)
    }

    // Funciones de extensión para mapeo Entity <-> Domain
    private fun MaterialWithTags.toDomain(): Material {
        return Material(
            id = material.id,
            name = material.name,
            quantity = material.quantity,
            unit = material.unit,
            imagePath = material.imagePath,
            notes = material.notes,
            createdAt = material.createdAt,
            tags = tags.map { it.toDomain() }
        )
    }

    private fun TagEntity.toDomain(): Tag {
        return Tag(
            id = id,
            name = name,
            colorHex = colorHex
        )
    }

    private fun Material.toEntity(): MaterialEntity {
        return MaterialEntity(
            id = id,
            name = name,
            quantity = quantity,
            unit = unit,
            imagePath = imagePath,
            notes = notes,
            createdAt = createdAt
        )
    }

    private fun Tag.toEntity(): TagEntity {
        return TagEntity(
            id = id,
            name = name,
            colorHex = colorHex
        )
    }
}
