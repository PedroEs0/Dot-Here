package com.crochet.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Tabla pivote entre productos e insumos/materiales utilizados.
 * Permite registrar la composición y trazabilidad de materiales por producto tejido.
 */
@Entity(
    tableName = "product_material_cross_ref",
    primaryKeys = ["productId", "materialId"],
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MaterialEntity::class,
            parentColumns = ["id"],
            childColumns = ["materialId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId"]),
        Index(value = ["materialId"])
    ]
)
data class ProductMaterialCrossRef(
    val productId: Long,
    val materialId: Long
)
