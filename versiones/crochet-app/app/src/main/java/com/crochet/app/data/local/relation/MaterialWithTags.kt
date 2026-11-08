package com.crochet.app.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.crochet.app.data.local.entity.MaterialEntity
import com.crochet.app.data.local.entity.MaterialTagCrossRef
import com.crochet.app.data.local.entity.TagEntity

/**
 * Modelo de relación de Room que une un MaterialEntity con su lista de TagEntity
 * a través de la tabla intermedia MaterialTagCrossRef.
 */
data class MaterialWithTags(
    @Embedded
    val material: MaterialEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = MaterialTagCrossRef::class,
            parentColumn = "materialId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)
