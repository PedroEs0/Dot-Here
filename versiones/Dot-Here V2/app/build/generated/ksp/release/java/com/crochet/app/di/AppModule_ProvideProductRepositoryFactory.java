package com.crochet.app.di;

import com.crochet.app.data.local.dao.ProductDao;
import com.crochet.app.domain.repository.ProductRepository;
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
public final class AppModule_ProvideProductRepositoryFactory implements Factory<ProductRepository> {
  private final Provider<ProductDao> productDaoProvider;

  private final Provider<FileStorageHelper> fileStorageHelperProvider;

  public AppModule_ProvideProductRepositoryFactory(Provider<ProductDao> productDaoProvider,
      Provider<FileStorageHelper> fileStorageHelperProvider) {
    this.productDaoProvider = productDaoProvider;
    this.fileStorageHelperProvider = fileStorageHelperProvider;
  }

  @Override
  public ProductRepository get() {
    return provideProductRepository(productDaoProvider.get(), fileStorageHelperProvider.get());
  }

  public static AppModule_ProvideProductRepositoryFactory create(
      Provider<ProductDao> productDaoProvider,
      Provider<FileStorageHelper> fileStorageHelperProvider) {
    return new AppModule_ProvideProductRepositoryFactory(productDaoProvider, fileStorageHelperProvider);
  }

  public static ProductRepository provideProductRepository(ProductDao productDao,
      FileStorageHelper fileStorageHelper) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideProductRepository(productDao, fileStorageHelper));
  }
}
