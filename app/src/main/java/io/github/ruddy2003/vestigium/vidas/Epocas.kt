package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas

/**
 * La época de cada vida, para enlazar un acontecimiento de «Hoy en la historia» con un cuento (plan de historia y
 * escenas, sección 3.6; tanda H2, 13 nov 2026). Si el año del acontecimiento cae en la época de una vida que el tema
 * deja ver y que tiene cuento, el acontecimiento lleva «en la época de Demeas ›», que abre su cuento. No se copia texto
 * de un lado al otro: solo se enlaza.
 *
 * La época es el año de la vida más y menos [MARGEN] años, como dice el plan. Dos excepciones **[Diseño propio]**:
 * - **Las vidas inventadas no tienen época** ([OtrasVidas.Vida.ficticia]): su mundo no es el de la historia de
 *   Wikipedia. Algunas ni siquiera cuentan los años como nosotros (Brenna vive en el año 312 «de los Caminos»; la
 *   recolectora, en el año 40 «tras el silencio»), y la astronauta, la residente o la doctora son del futuro.
 * - **Las vidas de hoy tampoco** (las de [DESDE_HOY] en adelante: Alguien del mundo, Alguien de México, Giulia): su
 *   época es la tuya, y se llevarían todos los acontecimientos recientes.
 */
object Epocas {
    /** Años antes y después del de la vida (el plan: `anio ± 30`). */
    const val MARGEN = 30

    /** Desde este año, una vida es de hoy y no tiene época. */
    const val DESDE_HOY = 2000

    /** Los años de su época, o null si no tiene (inventada o de hoy). */
    fun de(v: OtrasVidas.Vida): IntRange? = if (v.ficticia || v.anio >= DESDE_HOY) null else (v.anio - MARGEN)..(v.anio + MARGEN)

    /**
     * La vida de [vidas] en cuya época cae [anio], o null. Si caen varias, la de año más cercano; a igual distancia, la
     * del tema ([delTema]) y luego la primera de la lista. [anio] cuenta como Wikipedia: negativo, antes de Cristo.
     */
    fun vidaDelAnio(anio: Int, vidas: List<OtrasVidas.Vida>, delTema: String? = null): OtrasVidas.Vida? =
        vidas.filter { v -> de(v)?.let { anio in it } == true }
            .minWithOrNull(compareBy<OtrasVidas.Vida> { kotlin.math.abs(it.anio - anio) }.thenBy { if (it.tema != null && it.tema == delTema) 0 else 1 })
}
