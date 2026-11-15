package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.Fechas
import java.time.LocalDate

/**
 * Galia (lote 10; `docs/diseno/temas/galia.md`, fuentes en `docs/diseno/temas/fuentes_galia.md`): lo que el tema calcula
 * para sus textos. Todo sale solo de la fecha, así que el mismo día dice siempre lo mismo y no se guarda nada.
 * - **La noche:** César cuenta que los galos se tienen por hijos de Dis Pater y que por eso cuentan el tiempo por noches,
 *   no por días (*Guerra de las Galias* 6.18). Se cuenta la noche del año: «Noche 212».
 * - **El mes de Coligny,** con su marca MAT o ANM tal como va en el bronce. Recurso: el bronce es de finales del siglo II
 *   d. C. y no se sabe cuándo empezaba su año; aquí Samonios es noviembre, la propuesta que lo une a la fiesta de
 *   noviembre (Le Roux y Guyonvarc'h); la otra lo pone en mayo o junio. Qué quieren decir MAT y ANM se discute («bueno /
 *   no bueno» o «completo / incompleto»): por eso se escribe la marca y no se traduce.
 * - **Los números:** los ordinales galos de las cuentas de los alfareros de La Graufesenque (siglo I d. C., en letras
 *   latinas; algunos, reconstruidos), del 1 al 10; por encima, números romanos, como los días del bronce de Coligny.
 */
object Galia {
    /** Un mes de Coligny: su nombre con la grafía del bronce y su marca. */
    data class Mes(val nombre: String, val marca: String)

    /** Los doce meses de Coligny en su orden, desde Samonios. */
    val MESES = listOf(
        Mes("Samonios", "MAT"), Mes("Dumannios", "ANM"), Mes("Rivros", "MAT"), Mes("Anagantios", "ANM"),
        Mes("Ogronios", "MAT"), Mes("Cutios", "MAT"), Mes("Giamonios", "ANM"), Mes("Simivisonnios", "MAT"),
        Mes("Equos", "ANM"), Mes("Elembivios", "ANM"), Mes("Edrinios", "MAT"), Mes("Cantlos", "ANM"),
    )

    /** El mes de Coligny de un mes nuestro (1 a 12): Samonios es noviembre (recurso, ver arriba). */
    fun mes(n: Int): Mes = MESES[Math.floorMod(n - 11, 12)]

    /** La noche del año, de 1 a 366. */
    fun noche(d: LocalDate): Int = d.dayOfYear

    /** «Noche 290 · Cantlos ANM». */
    fun fecha(iso: String): String = runCatching {
        val d = Fechas.parse(iso)
        val m = mes(d.monthValue)
        "Noche ${noche(d)} · ${m.nombre} ${m.marca}"
    }.getOrDefault(iso)

    /** Los ordinales galos de La Graufesenque, del 1 al 10. */
    val ORDINALES = listOf("cintuxos", "allos", "tritios", "petuarios", "pinpetos", "suexos", "sextametos", "oxtumetos", "nametos", "decametos")

    /** Un número: del 1 al 10, su ordinal galo; por encima, en números romanos (hasta 3999); si no, en cifras. */
    fun numero(n: Int): String = when (n) {
        in 1..10 -> ORDINALES[n - 1]
        in 11..3999 -> romano(n)
        else -> n.toString()
    }

    private fun romano(n: Int): String {
        val valores = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
        val letras = arrayOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
        var resto = n
        val sb = StringBuilder()
        for (i in valores.indices) while (resto >= valores[i]) { sb.append(letras[i]); resto -= valores[i] }
        return sb.toString()
    }
}
