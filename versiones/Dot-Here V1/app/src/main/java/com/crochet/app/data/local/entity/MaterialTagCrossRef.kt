package com.crochet.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Tabla pivote muchos a muchos entre materiales y etiquetas.
 * Posee claves foráneas con ON DELETE CASCADE:
 * - Si un material se borra, se borran sus vínculos.
 * - Si una etiqueta se borra, se borran sus vínculos, dejando intactos los materiales.
 */
@Entity(
    tableName = "material_tag_cross_ref",
    primaryKeys = ["materialId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = MaterialEntity::class,
            parentColumns = ["id"],
            childColumns = ["materialId"],
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
        Index(value = ["materialId"]),
        Index(value = ["tagId"])
    ]
)
data class MaterialTagCrossRef(
    val materialId: Long,
    val tagId: Long
)
