package com.crochet.app.data.repository;

import com.crochet.app.data.local.dao.MaterialDao;
import com.crochet.app.data.local.dao.TagDao;
import com.crochet.app.util.FileStorageHelper;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class InventoryRepositoryImpl_Factory implements Factory<InventoryRepositoryImpl> {
  private final Provider<MaterialDao> materialDaoProvider;

  private final Provider<TagDao> tagDaoProvider;

  private final Provider<FileStorageHelper> fileStorageHelperProvider;

  public InventoryRepositoryImpl_Factory(Provider<MaterialDao> materialDaoProvider,
      Provider<TagDao> tagDaoProvider, Provider<FileStorageHelper> fileStorageHelperProvider) {
    this.materialDaoProvider = materialDaoProvider;
    this.tagDaoProvider = tagDaoProvider;
    this.fileStorageHelperProvider = fileStorageHelperProvider;
  }

  @Override
  public InventoryRepositoryImpl get() {
    return newInstance(materialDaoProvider.get(), tagDaoProvider.get(), fileStorageHelperProvider.get());
  }

  public static InventoryRepositoryImpl_Factory create(Provider<MaterialDao> materialDaoProvider,
      Provider<TagDao> tagDaoProvider, Provider<FileStorageHelper> fileStorageHelperProvider) {
    return new InventoryRepositoryImpl_Factory(materialDaoProvider, tagDaoProvider, fileStorageHelperProvider);
  }

  public static InventoryRepositoryImpl newInstance(MaterialDao materialDao, TagDao tagDao,
      FileStorageHelper fileStorageHelper) {
    return new InventoryRepositoryImpl(materialDao, tagDao, fileStorageHelper);
  }
}
