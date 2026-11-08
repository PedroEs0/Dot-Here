package com.crochet.app.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.crochet.app.data.local.entity.MaterialEntity
import com.crochet.app.data.local.entity.ProductEntity
import com.crochet.app.data.local.entity.ProductMaterialCrossRef
import com.crochet.app.data.local.entity.ProductTagCrossRef
import com.crochet.app.data.local.entity.TagEntity

/**
 * Modelo relacional compuesto que une un ProductEntity con su lista de MaterialEntity
 * y TagEntity asociados a través de sus tablas pivote correspondientes.
 */
data class ProductWithDetails(
    @Embedded
    val product: ProductEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ProductMaterialCrossRef::class,
            parentColumn = "productId",
            entityColumn = "materialId"
        )
    )
    val materials: List<MaterialEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ProductTagCrossRef::class,
            parentColumn = "productId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)
