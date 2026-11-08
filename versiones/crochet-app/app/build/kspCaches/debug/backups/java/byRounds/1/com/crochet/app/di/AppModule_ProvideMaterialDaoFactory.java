package com.crochet.app.di;

import com.crochet.app.data.local.AppDatabase;
import com.crochet.app.data.local.dao.MaterialDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideMaterialDaoFactory implements Factory<MaterialDao> {
  private final Provider<AppDatabase> databaseProvider;

  public AppModule_ProvideMaterialDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public MaterialDao get() {
    return provideMaterialDao(databaseProvider.get());
  }

  public static AppModule_ProvideMaterialDaoFactory create(Provider<AppDatabase> databaseProvider) {
    return new AppModule_ProvideMaterialDaoFactory(databaseProvider);
  }

  public static MaterialDao provideMaterialDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideMaterialDao(database));
  }
}
