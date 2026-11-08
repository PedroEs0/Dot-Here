package com.crochet.app.domain.model

/**
 * Modelo de dominio que representa una etiqueta personalizada.
 * Se utiliza para clasificar materiales (ej. "Algodón", "Lana", "Crochet 3mm").
 */
data class Tag(
    val id: Long = 0L,
    val name: String,
    val colorHex: String
)
