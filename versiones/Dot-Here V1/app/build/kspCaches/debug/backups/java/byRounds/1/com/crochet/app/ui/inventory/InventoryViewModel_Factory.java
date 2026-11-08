package com.crochet.app.ui.inventory;

import com.crochet.app.domain.repository.InventoryRepository;
import com.crochet.app.util.FileStorageHelper;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class InventoryViewModel_Factory implements Factory<InventoryViewModel> {
  private final Provider<InventoryRepository> repositoryProvider;

  private final Provider<FileStorageHelper> fileStorageHelperProvider;

  public InventoryViewModel_Factory(Provider<InventoryRepository> repositoryProvider,
      Provider<FileStorageHelper> fileStorageHelperProvider) {
    this.repositoryProvider = repositoryProvider;
    this.fileStorageHelperProvider = fileStorageHelperProvider;
  }

  @Override
  public InventoryViewModel get() {
    return newInstance(repositoryProvider.get(), fileStorageHelperProvider.get());
  }

  public static InventoryViewModel_Factory create(Provider<InventoryRepository> repositoryProvider,
      Provider<FileStorageHelper> fileStorageHelperProvider) {
    return new InventoryViewModel_Factory(repositoryProvider, fileStorageHelperProvider);
  }

  public static InventoryViewModel newInstance(InventoryRepository repository,
      FileStorageHelper fileStorageHelper) {
    return new InventoryViewModel(repository, fileStorageHelper);
  }
}
