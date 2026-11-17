package io.github.ruddy2003.vestigium.ui.galeria

import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.porNumero
import java.time.OffsetDateTime
import kotlin.math.floor

/**
 * Los textos de la galería de cuadros (parte 8, encargo C), sin Android: se prueban en escritorio (`TextosGaleriaPrueba`).
 */
internal object TextosGaleria {
    /** La invitación del mueble de Vida cuando no hay ninguno. */
    const val INVITACION = "Dale ♥ a un cuadro para guardarlo aquí"

    /** La línea sobre el respaldo y la exportación (`CUADROS_PLAN.md` 2.4). */
    const val RESPALDO = "Las imágenes viajan en el respaldo completo; no van en la exportación para R."

    /** «13:40» para una hora de 0 a 24 con decimales (la del cuadro guardado). Nunca «13:60». */
    fun hora(h: Double): String {
        if (h.isNaN() || h.isInfinite()) return "—"
        val minutos = floor((((h % 24) + 24) % 24) * 60 + 1e-6).toInt().coerceIn(0, 24 * 60 - 1)
        return "%02d:%02d".format(minutos / 60, minutos % 60)
    }

    /** «17 de noviembre de 2026, 21:04» para un instante ISO; si no se puede leer, el texto tal cual. */
    fun guardado(iso: String): String = runCatching {
        val t = OffsetDateTime.parse(iso)
        "${Fechas.larga(t.toLocalDate().toString())}, ${"%02d:%02d".format(t.hour, t.minute)}"
    }.getOrDefault(iso)

    /** El detalle del recuadro de Vida: la invitación sin cuadros; con ellos, cuántos y de qué día es el último. */
    fun detalle(n: Int, ultimoDia: String?): String {
        if (n <= 0) return INVITACION
        val cuantos = porNumero(n, "1 cuadro guardado", "$n cuadros guardados")
        val dia = ultimoDia?.let { runCatching { Fechas.larga(it) }.getOrNull() } ?: return cuantos
        return "$cuantos · el último, del $dia"
    }
}
