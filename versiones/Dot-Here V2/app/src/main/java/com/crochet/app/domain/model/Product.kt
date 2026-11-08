package com.crochet.app.domain.model

/**
 * Modelo de dominio para un producto artesanal tejido en crochet.
 * Contiene información de catálogo, insumos utilizados y las rutas a las 6 texturas
 * capturadas para el renderizado tridimensional.
 */
data class Product(
    val id: Long = 0L,
    val name: String,
    val description: String? = null,
    val price: Double = 0.0,
    val dimensions: String? = null, // Ej. "25x20x15 cm"
    val frontImagePath: String? = null,
    val backImagePath: String? = null,
    val leftImagePath: String? = null,
    val rightImagePath: String? = null,
    val topImagePath: String? = null,
    val bottomImagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val materials: List<Material> = emptyList(),
    val tags: List<Tag> = emptyList()
) {
    /**
     * Retorna la ruta absoluta del archivo de textura correspondiente a una cara.
     */
    fun getFaceImagePath(face: ProductFace): String? {
        return when (face) {
            ProductFace.FRONT -> frontImagePath
            ProductFace.BACK -> backImagePath
            ProductFace.LEFT -> leftImagePath
            ProductFace.RIGHT -> rightImagePath
            ProductFace.TOP -> topImagePath
            ProductFace.BOTTOM -> bottomImagePath
        }
    }

    /**
     * Indica si el producto tiene al menos una imagen capturada.
     */
    val hasAnyImage: Boolean
        get() = !frontImagePath.isNullOrBlank() ||
                !backImagePath.isNullOrBlank() ||
                !leftImagePath.isNullOrBlank() ||
                !rightImagePath.isNullOrBlank() ||
                !topImagePath.isNullOrBlank() ||
                !bottomImagePath.isNullOrBlank()

    /**
     * Cuenta cuántas de las 6 caras fueron capturadas.
     */
    val capturedFacesCount: Int
        get() {
            var count = 0
            if (!frontImagePath.isNullOrBlank()) count++
            if (!backImagePath.isNullOrBlank()) count++
            if (!leftImagePath.isNullOrBlank()) count++
            if (!rightImagePath.isNullOrBlank()) count++
            if (!topImagePath.isNullOrBlank()) count++
            if (!bottomImagePath.isNullOrBlank()) count++
            return count
        }
}
