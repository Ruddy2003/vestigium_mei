package io.github.ruddy2003.vestigium.ui

// =====================================================================
//  Cuándo cambia el fondo del teléfono (parte 6 del plan de mejoras, 17 nov 2026)
// =====================================================================
//
// Sin Android ni Compose, para poder probarlo con JUnit. Lo usa FondosTelefono: el pulso de 15 minutos, la app al cambiar
// de tema y la pantalla de los fondos.

/** Los dos estilos del fondo fijo: el cuadro del tema (si lo tiene) o el papel del tema (emblema y marco). */
const val ESTILO_CUADRO = "cuadro"
const val ESTILO_PAPEL = "papel"

/**
 * Qué fondo hay puesto (o tocaría poner): el [tema] (su id), si va en [oscuro] y, en un tema con caras, la [cara]. Con el
 * cuadro ([cuadro] true), además el [dia] lógico y el tramo de [luz] del cuadro (el nombre de su `Franja`: amanecer,
 * mañana, mediodía, tarde, atardecer, noche…): el cuadro cambia con el día y con la luz; el papel no.
 */
data class ClaveFondo(
    val tema: String, val oscuro: Boolean, val cara: String = "", val cuadro: Boolean = false, val dia: String = "", val luz: String = "",
) {
    /** Como se guarda en las preferencias de los fondos. */
    fun texto(): String = listOf(VERSION, tema, oscuro.toString(), cara, if (cuadro) ESTILO_CUADRO else ESTILO_PAPEL, dia, luz).joinToString("|")

    /** La misma, sin lo que cambia con la hora (el día y la luz): lo que distingue un tema, un modo o un estilo de otro. */
    fun sinHora(): ClaveFondo = copy(dia = "", luz = "")

    companion object {
        private const val VERSION = "f2"

        /** Lee una clave guardada; null si no hay o si es de las de antes («romano/false»): entonces se pone el fondo una vez. */
        fun leer(s: String?): ClaveFondo? {
            val p = s?.split("|") ?: return null
            if (p.size != 7 || p[0] != VERSION || p[1].isEmpty()) return null
            val oscuro = p[2].toBooleanStrictOrNull() ?: return null
            if (p[4] != ESTILO_CUADRO && p[4] != ESTILO_PAPEL) return null
            return ClaveFondo(p[1], oscuro, p[3], p[4] == ESTILO_CUADRO, p[5], p[6])
        }
    }
}

/**
 * Lo mínimo entre dos cambios de fondo que solo cambian la luz o el día del cuadro: diez minutos, menos que el pulso de 15.
 * Así un tramo nuevo se pone como mucho una vez por pulso aunque la app también lo pida al abrirse.
 */
const val MS_ENTRE_CAMBIOS_DE_LUZ = 10 * 60_000L

/**
 * ¿Hay que poner el fondo [ahora]? [ultimo] es el último que se puso (null si no se sabe) y [msDesdeUltimo], hace cuánto.
 * - Nada puesto o una clave vieja: sí.
 * - El mismo: no (poner un fondo no es gratis: el teléfono recalcula sus colores y puede parpadear).
 * - Otro tema, otro modo, otra cara u otro estilo: sí, en el momento.
 * - Solo otra luz u otro día del cuadro: sí, pero no antes de [MS_ENTRE_CAMBIOS_DE_LUZ] desde el último (un reloj que
 *   va hacia atrás cuenta como que ya pasó).
 */
fun tocaCambiarFondo(ultimo: ClaveFondo?, ahora: ClaveFondo, msDesdeUltimo: Long): Boolean {
    if (ultimo == null) return true
    if (ultimo == ahora) return false
    if (ultimo.sinHora() != ahora.sinHora()) return true
    return msDesdeUltimo < 0 || msDesdeUltimo >= MS_ENTRE_CAMBIOS_DE_LUZ
}
