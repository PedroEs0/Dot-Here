package com.crochet.app.domain.repository

import com.crochet.app.domain.model.Material
import com.crochet.app.domain.model.Tag
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para la gestión del inventario y etiquetas.
 * Define las operaciones abstractas de datos requeridas por la aplicación.
 */
interface InventoryRepository {
    /**
     * Obtiene el flujo reactivo de materiales con sus etiquetas asociadas,
     * permitiendo filtrar opcionalmente por nombre o notas.
     */
    fun getMaterialsWithTags(searchQuery: String = ""): Flow<List<Material>>

    /**
     * Obtiene un material por su identificador único junto a sus etiquetas.
     */
    fun getMaterialById(id: Long): Flow<Material?>

    /**
     * Inserta un nuevo material y asocia las etiquetas indicadas.
     * @return El ID generado para el material.
     */
    suspend fun insertMaterial(material: Material, tagIds: List<Long>): Long

    /**
     * Actualiza la información de un material y sincroniza sus etiquetas asociadas.
     */
    suspend fun updateMaterial(material: Material, tagIds: List<Long>)

    /**
     * Elimina un material por su ID, eliminando automáticamente sus asociaciones en la tabla pivote.
     */
    suspend fun deleteMaterial(materialId: Long)

    /**
     * Obtiene el listado completo de etiquetas disponibles.
     */
    fun getAllTags(): Flow<List<Tag>>

    /**
     * Inserta una nueva etiqueta personalizada.
     */
    suspend fun insertTag(tag: Tag): Long

    /**
     * Elimina una etiqueta por ID.
     * Al eliminarse, sus referencias pivote se borran, pero los materiales permanecen intactos.
     */
    suspend fun deleteTag(tagId: Long)
}
