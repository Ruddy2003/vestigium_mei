// Sustitutos de dos objetos de data/ que Modelo.kt nombra en valores por omisión (Privado.kt y Lote3.kt dependen de la base
// de datos y de Android). Solo lo que Modelo lee; no cambian nada de lo que se dibuja.
package io.github.ruddy2003.vestigium.data

object Privado {
    val TIPOS = setOf("recaida", "libido", "impulso", "masturbacion", "intimidad")
}

object Suplementos {
    val INICIALES: List<Suplemento> = emptyList()
}
