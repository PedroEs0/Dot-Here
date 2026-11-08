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
import com.crochet.app.ui.products.GuidedCaptureScreen
import com.crochet.app.ui.products.ProductDetail3DScreen
import com.crochet.app.ui.products.ProductFormScreen
import com.crochet.app.ui.products.ProductListScreen
import com.crochet.app.ui.products.ProductViewModel

/**
 * Grafo central de navegación para Dot-Here (V1 + V2).
 * Coordina navegación entre:
 * - Panel Principal (Insumos V1 y Productos 3D V2)
 * - Módulo V1: Creación, Detalle y Lista de Materiales
 * - Módulo V2: Asistente de Captura 6 Caras, Formulario y Visor 3D Interactivo
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.MainDashboard.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla Principal unificada con barra inferior de navegación
        composable(route = Screen.MainDashboard.route) {
            MainDashboardScreen(
                onNavigateToCreateMaterial = {
                    navController.navigate(Screen.MaterialForm.createRoute())
                },
                onNavigateToMaterialDetail = { materialId ->
                    navController.navigate(Screen.MaterialDetail.createRoute(materialId))
                },
                onNavigateToCreateProduct = {
                    navController.navigate(Screen.ProductForm.createRoute())
                },
                onNavigateToProductDetail3D = { productId ->
                    navController.navigate(Screen.ProductDetail3D.createRoute(productId))
                }
            )
        }

        // ==========================================
        // V1: INVENTARIO DE MATERIALES / INSUMOS
        // ==========================================
        composable(route = Screen.MaterialList.route) {
            val viewModel: InventoryViewModel = hiltViewModel()
            MaterialListScreen(
                viewModel = viewModel,
                onNavigateToCreate = {
                    navController.navigate(Screen.MaterialForm.createRoute())
                },
                onNavigateToDetail = { materialId ->
                    navController.navigate(Screen.MaterialDetail.createRoute(materialId))
                },
                showTopBar = true
            )
        }

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
                onNavigateBack = { navController.popBackStack() }
            )
        }

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
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { editId ->
                    navController.navigate(Screen.MaterialForm.createRoute(editId))
                }
            )
        }

        // ==========================================
        // V2: CATÁLOGO Y VISOR 3D DE PRODUCTOS
        // ==========================================
        composable(route = Screen.ProductList.route) {
            val viewModel: ProductViewModel = hiltViewModel()
            ProductListScreen(
                viewModel = viewModel,
                onNavigateToCreate = {
                    navController.navigate(Screen.ProductForm.createRoute())
                },
                onNavigateToDetail3D = { productId ->
                    navController.navigate(Screen.ProductDetail3D.createRoute(productId))
                }
            )
        }

        composable(
            route = Screen.ProductForm.route,
            arguments = listOf(
                navArgument(Screen.ProductForm.ARG_PRODUCT_ID) {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong(Screen.ProductForm.ARG_PRODUCT_ID)
            val viewModel: ProductViewModel = hiltViewModel()
            ProductFormScreen(
                productId = if (productId != null && productId > 0) productId else null,
                viewModel = viewModel,
                onNavigateToGuidedCapture = { pId ->
                    navController.navigate(Screen.GuidedCapture.createRoute(pId))
                },
                onNavigateTo3DPreview = { pId ->
                    navController.navigate(Screen.ProductDetail3D.createRoute(pId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ProductDetail3D.route,
            arguments = listOf(
                navArgument(Screen.ProductDetail3D.ARG_PRODUCT_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong(Screen.ProductDetail3D.ARG_PRODUCT_ID) ?: 0L
            val viewModel: ProductViewModel = hiltViewModel()
            ProductDetail3DScreen(
                productId = productId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { pId ->
                    navController.navigate(Screen.ProductForm.createRoute(pId))
                },
                onNavigateToRecapture = { pId ->
                    navController.navigate(Screen.GuidedCapture.createRoute(pId))
                }
            )
        }

        composable(
            route = Screen.GuidedCapture.route,
            arguments = listOf(
                navArgument(Screen.GuidedCapture.ARG_PRODUCT_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong(Screen.GuidedCapture.ARG_PRODUCT_ID) ?: 0L
            val viewModel: ProductViewModel = hiltViewModel()
            GuidedCaptureScreen(
                productId = productId,
                viewModel = viewModel,
                onFinishCapture = {
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
