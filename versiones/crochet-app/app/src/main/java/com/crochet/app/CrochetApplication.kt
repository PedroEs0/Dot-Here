package com.crochet.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Punto de entrada principal de la aplicación para Hilt Dependency Injection.
 * Permite que los módulos y dependencias de nivel aplicación estén disponibles globalmente.
 */
@HiltAndroidApp
class CrochetApplication : Application()
