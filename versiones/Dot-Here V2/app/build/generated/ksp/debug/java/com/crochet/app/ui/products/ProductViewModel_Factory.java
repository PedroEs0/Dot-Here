package com.crochet.app.ui.products;

import com.crochet.app.domain.repository.InventoryRepository;
import com.crochet.app.domain.repository.ProductRepository;
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
public final class ProductViewModel_Factory implements Factory<ProductViewModel> {
  private final Provider<ProductRepository> productRepositoryProvider;

  private final Provider<InventoryRepository> inventoryRepositoryProvider;

  private final Provider<FileStorageHelper> fileStorageHelperProvider;

  public ProductViewModel_Factory(Provider<ProductRepository> productRepositoryProvider,
      Provider<InventoryRepository> inventoryRepositoryProvider,
      Provider<FileStorageHelper> fileStorageHelperProvider) {
    this.productRepositoryProvider = productRepositoryProvider;
    this.inventoryRepositoryProvider = inventoryRepositoryProvider;
    this.fileStorageHelperProvider = fileStorageHelperProvider;
  }

  @Override
  public ProductViewModel get() {
    return newInstance(productRepositoryProvider.get(), inventoryRepositoryProvider.get(), fileStorageHelperProvider.get());
  }

  public static ProductViewModel_Factory create(
      Provider<ProductRepository> productRepositoryProvider,
      Provider<InventoryRepository> inventoryRepositoryProvider,
      Provider<FileStorageHelper> fileStorageHelperProvider) {
    return new ProductViewModel_Factory(productRepositoryProvider, inventoryRepositoryProvider, fileStorageHelperProvider);
  }

  public static ProductViewModel newInstance(ProductRepository productRepository,
      InventoryRepository inventoryRepository, FileStorageHelper fileStorageHelper) {
    return new ProductViewModel(productRepository, inventoryRepository, fileStorageHelper);
  }
}
