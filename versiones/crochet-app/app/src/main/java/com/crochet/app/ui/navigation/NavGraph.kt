package com.crochet.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.crochet.app.ui.inventory.InventoryViewModel
import com.crochet.app.ui.inventory.MaterialDetailScreen
import com.crochet.app.ui.inventory.MaterialFormScreen
import com.crochet.app.ui.inventory.MaterialListScreen

/**
 * Grafo central de navegación para Jetpack Compose.
 * Coordina las transiciones entre Lista, Formulario (Creación/Edición) y Detalle.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.MaterialList.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla Principal: Listado y búsqueda de insumos
        composable(route = Screen.MaterialList.route) {
            val viewModel: InventoryViewModel = hiltViewModel()
            MaterialListScreen(
                viewModel = viewModel,
                onNavigateToCreate = {
                    navController.navigate(Screen.MaterialForm.createRoute())
                },
                onNavigateToDetail = { materialId ->
                    navController.navigate(Screen.MaterialDetail.createRoute(materialId))
                }
            )
        }

        // Pantalla de Formulario: Crear o Editar material
        composable(
            route = Screen.MaterialForm.route,
            arguments = listOf(
                navArgument(Screen.MaterialForm.ARG_MATERIAL_ID) {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val materialId = backStackEntry.arguments?.getLong(Screen.MaterialForm.ARG_MATERIAL_ID)
            val viewModel: InventoryViewModel = hiltViewModel()
            MaterialFormScreen(
                materialId = if (materialId != null && materialId > 0) materialId else null,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de Detalle: Ver información completa del material
        composable(
            route = Screen.MaterialDetail.route,
            arguments = listOf(
                navArgument(Screen.MaterialDetail.ARG_MATERIAL_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val materialId = backStackEntry.arguments?.getLong(Screen.MaterialDetail.ARG_MATERIAL_ID) ?: 0L
            val viewModel: InventoryViewModel = hiltViewModel()
            MaterialDetailScreen(
                materialId = materialId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { editId ->
                    navController.navigate(Screen.MaterialForm.createRoute(editId))
                }
            )
        }
    }
}
