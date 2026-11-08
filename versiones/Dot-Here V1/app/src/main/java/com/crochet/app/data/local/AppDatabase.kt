package com.crochet.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.crochet.app.data.local.dao.MaterialDao
import com.crochet.app.data.local.dao.TagDao
import com.crochet.app.data.local.entity.MaterialEntity
import com.crochet.app.data.local.entity.MaterialTagCrossRef
import com.crochet.app.data.local.entity.TagEntity

/**
 * Base de datos SQLite Room de CrochetApp.
 * Almacena entidades de materiales, etiquetas y tabla pivote de relaciones.
 */
@Database(
    entities = [
        MaterialEntity::class,
        TagEntity::class,
        MaterialTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun materialDao(): MaterialDao
    abstract fun tagDao(): TagDao
}
