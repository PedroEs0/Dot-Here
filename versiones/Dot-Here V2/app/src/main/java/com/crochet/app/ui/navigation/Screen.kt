package com.crochet.app.ui.navigation

/**
 * Rutas y definiciones de pantallas para Navigation Compose en Dot-Here.
 * Incluye rutas de Versión 1 (Materiales/Insumos) y Versión 2 (Productos y Visor 3D).
 */
sealed class Screen(val route: String) {
    // Pantalla raíz con pestañas Insumos / Productos 3D
    object MainDashboard : Screen("main_dashboard")

    // V1 - Módulo de Inventario de Materiales
    object MaterialList : Screen("material_list")

    object MaterialForm : Screen("material_form?materialId={materialId}") {
        const val ARG_MATERIAL_ID = "materialId"

        fun createRoute(materialId: Long? = null): String {
            return if (materialId != null && materialId > 0) {
                "material_form?materialId=$materialId"
            } else {
                "material_form"
            }
        }
    }

    object MaterialDetail : Screen("material_detail/{materialId}") {
        const val ARG_MATERIAL_ID = "materialId"

        fun createRoute(materialId: Long): String {
            return "material_detail/$materialId"
        }
    }

    // V2 - Módulo de Productos y Visor 3D
    object ProductList : Screen("product_list")

    object ProductForm : Screen("product_form?productId={productId}") {
        const val ARG_PRODUCT_ID = "productId"

        fun createRoute(productId: Long? = null): String {
            return if (productId != null && productId > 0) {
                "product_form?productId=$productId"
            } else {
                "product_form"
            }
        }
    }

    object ProductDetail3D : Screen("product_detail_3d/{productId}") {
        const val ARG_PRODUCT_ID = "productId"

        fun createRoute(productId: Long): String {
            return "product_detail_3d/$productId"
        }
    }

    object GuidedCapture : Screen("guided_capture/{productId}") {
        const val ARG_PRODUCT_ID = "productId"

        fun createRoute(productId: Long): String {
            return "guided_capture/$productId"
        }
    }
}
