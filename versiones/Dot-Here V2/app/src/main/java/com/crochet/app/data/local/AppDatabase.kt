package com.crochet.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.crochet.app.data.local.dao.MaterialDao
import com.crochet.app.data.local.dao.ProductDao
import com.crochet.app.data.local.dao.TagDao
import com.crochet.app.data.local.entity.*

/**
 * Base de datos SQLite Room de Dot-Here.
 * Soporta inventario de insumos (V1) y catálogo de productos con 6 caras 3D (V2).
 */
@Database(
    entities = [
        MaterialEntity::class,
        TagEntity::class,
        MaterialTagCrossRef::class,
        ProductEntity::class,
        ProductMaterialCrossRef::class,
        ProductTagCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun materialDao(): MaterialDao
    abstract fun tagDao(): TagDao
    abstract fun productDao(): ProductDao
}
