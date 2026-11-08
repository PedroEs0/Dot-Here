package com.crochet.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.crochet.app.ui.navigation.NavGraph
import com.crochet.app.ui.theme.DotHereTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Actividad principal de Dot-Here.
 * Configurada con @AndroidEntryPoint para inyección de dependencias de Hilt.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DotHereTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
