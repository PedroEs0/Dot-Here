package com.crochet.app.domain.model

/**
 * Modelo de dominio para insumos de crochet (hilos, lanas, agujas, accesorios).
 * Representa la entidad de negocio desacoplada de la base de datos Room.
 */
data class Material(
    val id: Long = 0L,
    val name: String,
    val quantity: Double,
    val unit: String,
    val imagePath: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val tags: List<Tag> = emptyList()
)
