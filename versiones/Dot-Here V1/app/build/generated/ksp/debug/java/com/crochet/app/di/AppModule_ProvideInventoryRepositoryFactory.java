package com.crochet.app.di;

import com.crochet.app.data.local.dao.MaterialDao;
import com.crochet.app.data.local.dao.TagDao;
import com.crochet.app.domain.repository.InventoryRepository;
import com.crochet.app.util.FileStorageHelper;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideInventoryRepositoryFactory implements Factory<InventoryRepository> {
  private final Provider<MaterialDao> materialDaoProvider;

  private final Provider<TagDao> tagDaoProvider;

  private final Provider<FileStorageHelper> fileStorageHelperProvider;

  public AppModule_ProvideInventoryRepositoryFactory(Provider<MaterialDao> materialDaoProvider,
      Provider<TagDao> tagDaoProvider, Provider<FileStorageHelper> fileStorageHelperProvider) {
    this.materialDaoProvider = materialDaoProvider;
    this.tagDaoProvider = tagDaoProvider;
    this.fileStorageHelperProvider = fileStorageHelperProvider;
  }

  @Override
  public InventoryRepository get() {
    return provideInventoryRepository(materialDaoProvider.get(), tagDaoProvider.get(), fileStorageHelperProvider.get());
  }

  public static AppModule_ProvideInventoryRepositoryFactory create(
      Provider<MaterialDao> materialDaoProvider, Provider<TagDao> tagDaoProvider,
      Provider<FileStorageHelper> fileStorageHelperProvider) {
    return new AppModule_ProvideInventoryRepositoryFactory(materialDaoProvider, tagDaoProvider, fileStorageHelperProvider);
  }

  public static InventoryRepository provideInventoryRepository(MaterialDao materialDao,
      TagDao tagDao, FileStorageHelper fileStorageHelper) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideInventoryRepository(materialDao, tagDao, fileStorageHelper));
  }
}
