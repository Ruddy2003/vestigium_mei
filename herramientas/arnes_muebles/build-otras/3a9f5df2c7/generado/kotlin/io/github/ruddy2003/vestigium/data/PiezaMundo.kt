// Generado por el arnés: piezas de Mundo.kt (CiudadVisitada, SitioMarcado), copiadas tal cual en cada compilación. No se edita.
@file:Suppress("unused")
package io.github.ruddy2003.vestigium.data

import kotlinx.serialization.Serializable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/** Una ciudad que añadiste a mano: de dónde (para ubicarla) y, si quieres, cuándo o cuántos días. */
@Serializable
data class CiudadVisitada(val id: String, val nombre: String, val pais: String, val lat: Double, val lon: Double,
                          val desde: String? = null, val hasta: String? = null, val dias: Int? = null, val nota: String = "")

/**
 * Un sitio del Patrimonio Mundial marcado a mano como visitado. [fecha] es el día en que lo marcaste; [visita] (esquema 23),
 * el día en que estuviste, si lo dijiste. Sin [visita] cuenta en tus totales pero no en el reporte de ningún periodo: antes
 * contaba en el día de la marca, y marcar hoy un sitio de un viaje viejo lo ponía en el reporte de este mes.
 */
@Serializable
data class SitioMarcado(val id: Int, val fecha: String, val visita: String? = null) {
    /** El día de la visita como número de día, o null si no se sabe. */
    val diaVisita: Int? get() = visita?.let { v -> runCatching { Fechas.parse(v).toEpochDay().toInt() }.getOrNull() }
}
