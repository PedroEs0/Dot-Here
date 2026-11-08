package com.crochet.app.ui.navigation

/**
 * Rutas y definiciones de pantallas para Navigation Compose.
 */
sealed class Screen(val route: String) {
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
}
