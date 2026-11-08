package com.crochet.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla 'products'.
 * Almacena información general del producto tejido y las rutas absolutas a las 6 texturas
 * guardadas en el almacenamiento interno privado del dispositivo.
 */
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String?,
    val price: Double,
    val dimensions: String?,
    val frontImagePath: String?,
    val backImagePath: String?,
    val leftImagePath: String?,
    val rightImagePath: String?,
    val topImagePath: String?,
    val bottomImagePath: String?,
    val createdAt: Long = System.currentTimeMillis()
)
