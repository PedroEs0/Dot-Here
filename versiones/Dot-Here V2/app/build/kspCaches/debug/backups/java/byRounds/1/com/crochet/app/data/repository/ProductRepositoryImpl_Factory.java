package com.crochet.app.data.repository;

import com.crochet.app.data.local.dao.ProductDao;
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
public final class ProductRepositoryImpl_Factory implements Factory<ProductRepositoryImpl> {
  private final Provider<ProductDao> productDaoProvider;

  private final Provider<FileStorageHelper> fileStorageHelperProvider;

  public ProductRepositoryImpl_Factory(Provider<ProductDao> productDaoProvider,
      Provider<FileStorageHelper> fileStorageHelperProvider) {
    this.productDaoProvider = productDaoProvider;
    this.fileStorageHelperProvider = fileStorageHelperProvider;
  }

  @Override
  public ProductRepositoryImpl get() {
    return newInstance(productDaoProvider.get(), fileStorageHelperProvider.get());
  }

  public static ProductRepositoryImpl_Factory create(Provider<ProductDao> productDaoProvider,
      Provider<FileStorageHelper> fileStorageHelperProvider) {
    return new ProductRepositoryImpl_Factory(productDaoProvider, fileStorageHelperProvider);
  }

  public static ProductRepositoryImpl newInstance(ProductDao productDao,
      FileStorageHelper fileStorageHelper) {
    return new ProductRepositoryImpl(productDao, fileStorageHelper);
  }
}
