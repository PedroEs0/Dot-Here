package com.crochet.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Tabla pivote entre productos y etiquetas personalizadas.
 * Permite clasificar productos tejidos (ej. "Amigurumi", "Mantas", "Navidad").
 */
@Entity(
    tableName = "product_tag_cross_ref",
    primaryKeys = ["productId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId"]),
        Index(value = ["tagId"])
    ]
)
data class ProductTagCrossRef(
    val productId: Long,
    val tagId: Long
)
