package com.crochet.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla 'materials'.
 * Almacena información de insumos y la ruta local String absoluta a la foto interna.
 */
@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val quantity: Double,
    val unit: String,
    val imagePath: String?,
    val notes: String?,
    val createdAt: Long = System.currentTimeMillis()
)
