package com.crochet.app.di

import android.content.Context
import androidx.room.Room
import com.crochet.app.data.local.AppDatabase
import com.crochet.app.data.local.dao.MaterialDao
import com.crochet.app.data.local.dao.TagDao
import com.crochet.app.data.repository.InventoryRepositoryImpl
import com.crochet.app.domain.repository.InventoryRepository
import com.crochet.app.util.FileStorageHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de inyección de dependencias con Hilt.
 * Provee la base de datos Room, los DAOs, el gestor de archivos y el repositorio.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "dot_here_inventory.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMaterialDao(database: AppDatabase): MaterialDao {
        return database.materialDao()
    }

    @Provides
    fun provideTagDao(database: AppDatabase): TagDao {
        return database.tagDao()
    }

    @Provides
    @Singleton
    fun provideInventoryRepository(
        materialDao: MaterialDao,
        tagDao: TagDao,
        fileStorageHelper: FileStorageHelper
    ): InventoryRepository {
        return InventoryRepositoryImpl(materialDao, tagDao, fileStorageHelper)
    }
}
