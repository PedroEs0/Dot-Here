package com.crochet.app.util;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class FileStorageHelper_Factory implements Factory<FileStorageHelper> {
  private final Provider<Context> contextProvider;

  public FileStorageHelper_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public FileStorageHelper get() {
    return newInstance(contextProvider.get());
  }

  public static FileStorageHelper_Factory create(Provider<Context> contextProvider) {
    return new FileStorageHelper_Factory(contextProvider);
  }

  public static FileStorageHelper newInstance(Context context) {
    return new FileStorageHelper(context);
  }
}
