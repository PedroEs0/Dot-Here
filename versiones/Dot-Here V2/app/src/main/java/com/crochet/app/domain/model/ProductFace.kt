package com.crochet.app.domain.model

/**
 * Representa las 6 caras ortogonales de captura guiada para la reconstrucción
 * y mapeo de texturas del producto en el visor 3D interactivo.
 */
enum class ProductFace(
    val title: String,
    val subtitle: String,
    val guideHint: String
) {
    FRONT(
        title = "Cara Frontal",
        subtitle = "Vista delantera del tejido",
        guideHint = "Coloca el producto de frente centrando el patrón principal."
    ),
    BACK(
        title = "Cara Trasera",
        subtitle = "Vista posterior",
        guideHint = "Gira el tejido 180° mostrando el reverso o remates."
    ),
    LEFT(
        title = "Cara Izquierda",
        subtitle = "Costado izquierdo",
        guideHint = "Muestra el lateral izquierdo del producto."
    ),
    RIGHT(
        title = "Cara Derecha",
        subtitle = "Costado derecho",
        guideHint = "Muestra el lateral derecho del producto."
    ),
    TOP(
        title = "Cara Superior",
        subtitle = "Vista cenital / arriba",
        guideHint = "Apunta desde arriba mostrando la parte superior o cierre."
    ),
    BOTTOM(
        title = "Cara Inferior",
        subtitle = "Base o fondo",
        guideHint = "Muestra la base, fondo o soporte del tejido."
    );

    val nextFace: ProductFace?
        get() {
            val nextIndex = ordinal + 1
            return if (nextIndex < entries.size) entries[nextIndex] else null
        }
}
