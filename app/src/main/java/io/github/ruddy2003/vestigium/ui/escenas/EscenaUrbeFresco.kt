package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El fresco de la capilla y el hollín que lo tapa.
 *
 * **[Diseño propio]**: la pintura es inventada y no copia ninguna obra ni ninguna capilla reales. Son figuras de pie,
 * con manto y nimbo, sin rasgos (la única cara que se ve es la del ángel que el cuento nombra), bajo una arquería
 * pintada y delante de unas lomas; en la luneta, dos ángeles a los lados de un sol; en la bóveda, un cielo azul con
 * estrellas de oro, nervios y medallones. No hay letras.
 *
 * Se pinta en cuatro sitios ([Sitio]) y cada uno se parte en zonas ([ZONAS]). Una zona está limpia cuando el arco
 * `fresco` llega a su umbral; si no, la cubre el hollín, que deja adivinar las figuras debajo. La zona que toca ahora
 * tiene dos catas: los cuadrados limpios «como ventanas abiertas en el gris» de que habla el cuento.
 */
internal object FrescoUrbe {
    private val C = ColorUrbe
    private val K = CapillaUrbe

    internal enum class Sitio { BOVEDA, LUNETA, ALTAR, DERECHA }

    /** Una zona del fresco, en las medidas de su superficie, y el valor del arco `fresco` con que queda limpia. */
    internal class Zona(val sitio: Sitio, val u0: Float, val u1: Float, val v0: Float, val v1: Float, val umbral: Float)

    /** En el orden en que se limpian: la bóveda, la luneta, la pared del altar y la pared derecha. */
    internal val ZONAS = listOf(
        Zona(Sitio.BOVEDA, 0f, 0.5f, 0f, 66f, 0.06f), Zona(Sitio.BOVEDA, 0.5f, 1f, 0f, 66f, 0.14f), Zona(Sitio.BOVEDA, 0f, 0.5f, 66f, 132f, 0.24f),
        Zona(Sitio.BOVEDA, 0.5f, 1f, 66f, 132f, 0.36f), Zona(Sitio.BOVEDA, 0f, 1f, 132f, 200f, 0.45f),
        Zona(Sitio.LUNETA, 100f, 182f, 16f, 84f, 0.47f), Zona(Sitio.LUNETA, 182f, 264f, 16f, 84f, 0.49f),
        Zona(Sitio.ALTAR, 100f, 157f, 84f, 132f, 0.53f), Zona(Sitio.ALTAR, 157f, 207f, 84f, 132f, 0.59f), Zona(Sitio.ALTAR, 207f, 264f, 84f, 132f, 0.65f),
        Zona(Sitio.ALTAR, 207f, 264f, 132f, 176f, 0.71f), Zona(Sitio.ALTAR, 157f, 207f, 132f, 176f, 0.77f), Zona(Sitio.ALTAR, 100f, 157f, 132f, 176f, 0.82f),
        Zona(Sitio.ALTAR, 100f, 264f, 176f, 200f, 0.85f),
        Zona(Sitio.DERECHA, 0f, 56f, 88f, 136f, 0.87f), Zona(Sitio.DERECHA, 56f, 120f, 88f, 136f, 0.90f), Zona(Sitio.DERECHA, 120f, 200f, 88f, 136f, 0.93f),
        Zona(Sitio.DERECHA, 120f, 200f, 136f, 200f, 0.95f), Zona(Sitio.DERECHA, 56f, 120f, 136f, 200f, 0.97f), Zona(Sitio.DERECHA, 0f, 56f, 136f, 200f, 1f),
    )

    /** Cuántas zonas de [sitio] están limpias hoy. */
    internal fun limpias(m: Momento, sitio: Sitio): Int = ZONAS.count { it.sitio == sitio && EscenaUrbe.limpia(m, it.umbral) }

    /** La zona en la que se trabaja hoy: la primera que aún no está limpia; null con la capilla terminada. */
    internal fun enObra(m: Momento): Zona? = ZONAS.firstOrNull { !EscenaUrbe.limpia(m, it.umbral) }

    /** Un pincel sobre una superficie: lleva sus medidas al lienzo y pide cada color a la luz del sitio. */
    private class Muro(val pl: Plano, val a: Alumbrado, val mira: Float) {
        fun o(u: Float, v: Float) = pl(u, v)
        fun c(u: Float, v: Float, color: Color) = a.en(pl(u, v), color, mira)
        fun poli(vararg uv: Float) = Path().apply { var i = 0; while (i < uv.size) { val q = pl(uv[i], uv[i + 1]); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y); i += 2 }; close() }
        fun disco(u: Float, v: Float, ru: Float, rv: Float = ru, n: Int = 14) = Path().apply { for (i in 0 until n) { val t = 6.2832f * i / n; val q = pl(u + ru * cos(t), v + rv * sin(t)); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }; close() }
        /** Un recuadro de la superficie, con sus lados largos partidos para que siga una curva. */
        fun cuadro(u0: Float, u1: Float, v0: Float, v1: Float, n: Int = 6) = Path().apply {
            for (i in 0..n) { val q = pl(u0 + (u1 - u0) * i / n, v0); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }
            for (i in n downTo 0) { val q = pl(u0 + (u1 - u0) * i / n, v1); lineTo(q.x, q.y) }
            close()
        }
    }

    private fun DrawScope.franja(w: Muro, color: Color, u0: Float, u1: Float, v0: Float, v1: Float) = drawPath(w.cuadro(u0, u1, v0, v1), w.c((u0 + u1) / 2f, (v0 + v1) / 2f, color))

    // ---------------------------------------------------------------- La pintura

    fun DrawScope.pintar(m: Momento, a: Alumbrado) {
        val boveda = Muro(K.BOVEDA, a, 0f); val fondo = Muro(K.FONDO, a, 0f); val derecha = Muro(K.DERECHA, a, -1f); val izquierda = Muro(K.IZQUIERDA, a, 1f)
        clipPath(K.formaDeLaBoveda()) { cieloDeLaBoveda(boveda) }
        clipPath(K.formaDelFondo()) { luneta(fondo, m); escena(fondo, 100f, 264f, listOf(131f, 182f, 233f), 3, false); zocalo(fondo, 100f, 264f, 8) }
        clipPath(K.formaDerecha()) { escena(derecha, 0f, 200f, listOf(16f, 40f, 66f, 94f, 124f, 156f, 188f), 4, true); zocalo(derecha, 0f, 200f, 8) }
        clipPath(K.formaIzquierda()) { cortina(izquierda); zocalo(izquierda, 0f, 200f, 8); drawPath(K.formaIzquierda(), a.en(K.p(K.X0, 170f, 90f), C.HOLLIN).copy(alpha = 0.08f + 0.4f * (1f - EscenaUrbe.fresco(m)))) }
        // El hollín, por zonas; y lo que la obra ha ido encontrando.
        val enObra = enObra(m)
        for ((i, z) in ZONAS.withIndex()) if (!EscenaUrbe.limpia(m, z.umbral)) {
            val w = when (z.sitio) { Sitio.BOVEDA -> boveda; Sitio.LUNETA -> fondo; Sitio.ALTAR -> fondo; Sitio.DERECHA -> derecha }
            val donde = when (z.sitio) { Sitio.BOVEDA -> K.formaDeLaBoveda(); Sitio.LUNETA -> K.formaDelFondo(); Sitio.ALTAR -> K.formaDelFondo(); Sitio.DERECHA -> K.formaDerecha() }
            clipPath(donde) { hollin(w, z, i, z === enObra && EscenaUrbe.hayAndamio(m), z.sitio == Sitio.LUNETA && z.u0 < 150f && EscenaUrbe.angel(m)) }
        }
        if (EscenaUrbe.grieta(m)) {
            val g = listOf(Offset(240f, 36f), Offset(238f, 52f), Offset(241f, 66f), Offset(236f, 84f), Offset(238f, 100f), Offset(233f, 118f), Offset(235f, 130f))
            for (i in 0 until g.size - 1) drawLine(a.en(g[i], C.HOLLIN).copy(alpha = 0.9f), g[i], g[i + 1], 1.2f, StrokeCap.Round)
            // Los testigos de yeso, que dicen si la grieta se mueve.
            for (o in listOf(Offset(239.4f, 59f), Offset(237f, 93f))) { drawRect(a.en(o, C.CAL), o + Offset(-5f, -1.8f), androidx.compose.ui.geometry.Size(10f, 3.6f)); drawRect(a.en(o, C.HOLLIN).copy(alpha = 0.5f), o + Offset(-5f, -1.8f), androidx.compose.ui.geometry.Size(10f, 3.6f), style = Stroke(0.5f)) }
        }
        if (EscenaUrbe.firma(m)) {
            // La firma, en el pliegue de un manto: un garabato, y al lado la etiqueta de papel que la señala.
            val o = Offset(196f, 152f)
            drawPath(Path().apply { moveTo(o.x - 6f, o.y); cubicTo(o.x - 4f, o.y - 4.4f, o.x - 2f, o.y + 3.6f, o.x, o.y - 0.6f); cubicTo(o.x + 2f, o.y - 3.6f, o.x + 4f, o.y + 3f, o.x + 6.4f, o.y - 1.4f) }, a.en(o, C.HOLLIN), style = Stroke(0.9f, cap = StrokeCap.Round))
            drawRect(a.en(o, C.CAL), o + Offset(8f, -11f), androidx.compose.ui.geometry.Size(9f, 6f)); drawRect(a.en(o, C.ROJO), o + Offset(8f, -11f), androidx.compose.ui.geometry.Size(9f, 1.6f)); drawLine(a.en(o, C.ROJO), o + Offset(8f, -5f), o + Offset(5.4f, -1.6f), 0.8f)
        }
        if (EscenaUrbe.papeles(m)) for ((t, v) in listOf(62f to 118f, 74f to 126f, 66f to 136f)) drawPath(derecha.cuadro(t, t + 9f, v, v + 7f, 1), derecha.c(t, v, C.CAL).copy(alpha = 0.9f))
    }

    /** La bóveda: el cielo azul con sus estrellas de oro, los nervios que la parten en tramos y un medallón en cada uno. */
    private fun DrawScope.cieloDeLaBoveda(w: Muro) {
        for (j in 0 until 6) for (i in 0 until 8) franja(w, lerp(C.AZUL, C.HOLLIN, 0.12f), i / 8f, (i + 1) / 8f, j * 33.4f, (j + 1) * 33.4f)
        val az = Azar(Semilla.de("urbe", "estrellas"))
        repeat(120) { val a = az.entre(0.05f, 0.95f); val t = az.entre(2f, 190f); drawCircle(w.c(a, t, C.ORO), az.entre(0.5f, 1.05f) * K.s(t), w.o(a, t)) }
        // Las cenefas de los arranques y de la clave, y los nervios de un tramo a otro.
        for ((a0, a1) in listOf(0f to 0.05f, 0.95f to 1f)) for (j in 0 until 6) franja(w, C.OCRE, a0, a1, j * 33.4f, (j + 1) * 33.4f)
        for (j in 0 until 6) franja(w, C.ROJO, 0.488f, 0.512f, j * 33.4f, (j + 1) * 33.4f)
        for (t in listOf(0f, 66f, 132f)) { for (i in 0 until 8) franja(w, C.OCRE, i / 8f, (i + 1) / 8f, t, t + 5f); for (i in 0 until 16) { val a = (i + 0.5f) / 16f; drawCircle(w.c(a, t + 2.5f, C.CAL), 0.9f * K.s(t), w.o(a, t + 2.5f)) } }
        for (t in listOf(35f, 100f, 166f)) for (a in listOf(0.25f, 0.75f)) {
            drawPath(w.disco(a, t, 0.062f, 11f), w.c(a, t, C.ORO)); drawPath(w.disco(a, t, 0.048f, 8.6f), w.c(a, t, C.ROJO)); drawPath(w.disco(a, t, 0.022f, 4f), w.c(a, t, C.CAL))
        }
    }

    /** La luneta: sobre azul, un sol de oro entre dos ángeles de medio cuerpo. El de la izquierda es el que encuentra Giulia. */
    private fun DrawScope.luneta(w: Muro, m: Momento) {
        for (k in 0..3) franja(w, lerp(C.AZUL, C.CAL, 0.06f * k), 100f, 264f, 16f + k * 17f, 33f + k * 17f + 1f)
        // Unas nubes pintadas, de las que salen.
        for ((u, v, r) in listOf(Triple(126f, 78f, 16f), Triple(148f, 80f, 12f), Triple(238f, 78f, 16f), Triple(216f, 80f, 12f), Triple(182f, 82f, 14f))) drawPath(w.disco(u, v, r, r * 0.42f), w.c(u, v, lerp(C.CAL, C.AZUL, 0.18f)))
        // El sol.
        for (k in 0 until 12) { val t = 6.2832f * k / 12f; drawLine(w.c(182f, 50f, C.ORO), w.o(182f + cos(t) * 11f, 50f + sin(t) * 11f), w.o(182f + cos(t) * (if (k % 2 == 0) 19f else 15f), 50f + sin(t) * (if (k % 2 == 0) 19f else 15f)), 1.2f, StrokeCap.Round) }
        drawPath(w.disco(182f, 50f, 10f), w.c(182f, 50f, C.ORO)); drawPath(w.disco(182f, 50f, 6.4f), w.c(182f, 50f, lerp(C.ORO, C.CAL, 0.5f)))
        for ((u, gira, manto) in listOf(Triple(136f, 1f, C.ROJO), Triple(228f, -1f, C.VERDE))) {
            val v = 56f
            // Las alas, con sus plumas; el busto, con el manto cruzado; y el nimbo.
            for (s in listOf(-1f, 1f)) {
                drawPath(w.poli(u + s * 5f, v + 9f, u + s * 30f, v - 13f, u + s * 33f, v + 7f, u + s * 22f, v + 13f, u + s * 15f, v + 25f), w.c(u, v, lerp(C.CAL, C.ORO, if (s * gira > 0f) 0.25f else 0.45f)))
                for (k in 1..3) drawLine(w.c(u, v, lerp(C.ORO, C.MADERA_OSC, 0.3f)).copy(alpha = 0.6f), w.o(u + s * (8f + k * 4f), v + 10f + k * 3f), w.o(u + s * (22f + k * 3f), v - 9f + k * 6f), 0.5f)
            }
            drawPath(w.poli(u - 12f, v + 13f, u + 12f, v + 13f, u + 17f, v + 31f, u - 17f, v + 31f), w.c(u, v + 20f, manto))
            drawPath(w.poli(u - gira * 12f, v + 13f, u + gira * 2f, v + 13f, u - gira * 6f, v + 31f, u - gira * 17f, v + 31f), w.c(u, v + 20f, lerp(manto, C.CAL, 0.3f)))
            drawPath(w.disco(u, v, 13f), w.c(u, v, C.ORO)); drawPath(w.disco(u, v, 11f), w.c(u, v, lerp(C.ORO, C.OCRE, 0.4f)))
            cara(w, u + gira * 0.8f, v + 0.8f, 8f, gira)
        }
    }

    /** La cara de un ángel: un muchacho de pelo rizado, con los ojos levantados y la boca a punto de decir algo. */
    private fun DrawScope.cara(w: Muro, u: Float, v: Float, r: Float, gira: Float) {
        val pelo = w.c(u, v, lerp(C.OCRE, C.MADERA_OSC, 0.35f))
        for (k in 0..8) { val t = 3.4f + 2.6f * k / 8f; drawCircle(pelo, r * 0.36f, w.o(u + cos(t) * r * 0.92f, v + sin(t) * r * 0.86f - r * 0.1f)) }
        drawPath(w.disco(u, v + r * 0.1f, r * 0.82f, r), w.c(u, v, C.CARNE))
        drawPath(w.disco(u - gira * r * 0.5f, v + r * 0.2f, r * 0.3f, r * 0.7f), w.c(u, v, lerp(C.CARNE, C.ROJO, 0.22f)).copy(alpha = 0.5f))
        val tinta = w.c(u, v, C.HOLLIN)
        for (s in listOf(-1f, 1f)) { drawCircle(w.c(u, v, C.CAL), r * 0.17f, w.o(u + s * r * 0.34f + gira * r * 0.08f, v - r * 0.08f)); drawCircle(tinta, r * 0.1f, w.o(u + s * r * 0.34f + gira * r * 0.12f, v - r * 0.15f)) }
        drawLine(tinta.copy(alpha = 0.5f), w.o(u + gira * r * 0.1f, v + r * 0.1f), w.o(u + gira * r * 0.16f, v + r * 0.36f), 0.4f)
        drawOval(w.c(u, v, C.ROJO), w.o(u + gira * r * 0.06f, v + r * 0.6f) - Offset(r * 0.16f, r * 0.08f), androidx.compose.ui.geometry.Size(r * 0.34f, r * 0.2f))
    }

    /**
     * Una escena de figuras de pie bajo una arquería pintada, de [u0] a [u1] de una pared: el cielo, las lomas con su
     * pueblo, las columnas y los arcos, y una figura en cada sitio de [pies]. Sirve para la pared del altar y, vista
     * de lado, para la pared derecha ([deLado]: allí las medidas a lo largo son de hondo).
     */
    private fun DrawScope.escena(w: Muro, u0: Float, u1: Float, pies: List<Float>, arcos: Int, deLado: Boolean) {
        val n = 8; val du = (u1 - u0) / n
        for (i in 0 until n) { val a = u0 + i * du
            for (k in 0..3) franja(w, lerp(C.AZUL, C.CAL, 0.1f + 0.14f * k), a, a + du, 88f + k * 13f, 101f + k * 13f + 0.6f)
            franja(w, lerp(C.OCRE, C.VERDE, 0.4f), a, a + du, 156f, 176f) }
        // Las lomas, en dos filas, y un pueblo de torres en la de detrás.
        val paso = (u1 - u0) / 10f
        drawPath(w.poli(u0, 150f, u0 + paso, 141f, u0 + paso * 2.2f, 146f, u0 + paso * 3.6f, 136f, u0 + paso * 5f, 145f, u0 + paso * 6.4f, 137f, u0 + paso * 8f, 144f, u0 + paso * 9f, 139f, u1, 146f, u1, 160f, u0, 160f), w.c((u0 + u1) / 2f, 146f, lerp(C.VERDE, C.AZUL, 0.3f)))
        for (k in 0..4) { val x = u0 + paso * (3.1f + 0.24f * k); val alto = listOf(7f, 11f, 6f, 9f, 5f)[k]; drawPath(w.cuadro(x, x + paso * 0.18f, 138f - alto, 139f, 1), w.c(x, 134f, if (k % 2 == 0) C.CAL else C.OCRE)) }
        drawPath(w.poli(u0, 158f, u0 + paso * 1.6f, 150f, u0 + paso * 3f, 155f, u0 + paso * 4.6f, 149f, u0 + paso * 6f, 154f, u0 + paso * 7.6f, 150f, u1, 156f, u1, 164f, u0, 164f), w.c((u0 + u1) / 2f, 156f, C.VERDE))
        for (k in 0..6) { val x = u0 + paso * (0.5f + 1.45f * k); drawPath(w.poli(x, 152f, x - paso * 0.1f, 144f, x, 137f, x + paso * 0.1f, 144f), w.c(x, 146f, lerp(C.VERDE, C.HOLLIN, 0.35f))) }
        // La arquería pintada: columnas, arcos y la cornisa de arriba, con su friso.
        val luzArco = (u1 - u0 - 8f * (arcos + 1) * (if (deLado) 1f else 0.5f)) / arcos; val grueso = if (deLado) 8f else 4f
        for (k in 0..arcos) {
            val x = u0 + k * (luzArco + grueso)
            drawPath(w.cuadro(x, x + grueso, 106f, 172f, 1), w.c(x, 140f, C.CAL)); drawPath(w.cuadro(x + grueso * 0.6f, x + grueso, 106f, 172f, 1), w.c(x, 140f, lerp(C.CAL, C.SOMBRA, 0.35f)))
            drawPath(w.cuadro(x - grueso * 0.25f, x + grueso * 1.25f, 103f, 107f, 1), w.c(x, 105f, C.ORO)); drawPath(w.cuadro(x - grueso * 0.25f, x + grueso * 1.25f, 170f, 174f, 1), w.c(x, 172f, C.PIEDRA))
            if (k < arcos) {
                val c = x + grueso + luzArco / 2f; val r = luzArco / 2f
                // El arco: una dovela ancha, y encima el muro pintado de rojo hasta la cornisa.
                val enjuta = Path().apply { val q0 = w.o(x + grueso, 104f); moveTo(q0.x, q0.y); for (i in 0..12) { val t = 3.1416f * (1f - i / 12f); val q = w.o(c + cos(t) * r, 104f - sin(t) * 14f); lineTo(q.x, q.y) }; val q1 = w.o(x + grueso + luzArco, 88f); lineTo(q1.x, q1.y); val q2 = w.o(x + grueso, 88f); lineTo(q2.x, q2.y); close() }
                drawPath(enjuta, w.c(c, 94f, lerp(C.ROJO, C.OCRE, 0.25f)))
                drawPath(Path().apply { for (i in 0..12) { val t = 3.1416f * (1f - i / 12f); val q = w.o(c + cos(t) * r, 104f - sin(t) * 14f); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) } }, w.c(c, 92f, C.CAL), style = Stroke(1.6f))
            }
        }
        for (i in 0 until n) { val a = u0 + i * du; franja(w, C.OCRE, a, a + du, 86f, 90.6f); franja(w, C.PIEDRA, a, a + du, 172f, 176f) }
        // Las figuras: cada una con su manto y su túnica; la del centro, más alta, sobre una grada.
        val mantos = listOf(C.ROJO, C.OCRE, C.AZUL, C.VERDE, C.CAL, C.ROJO, C.AZUL, C.OCRE); val tunicas = listOf(C.VERDE, C.AZUL, C.ROJO, C.CAL, C.OCRE, C.CAL, C.VERDE, C.ROJO)
        val ancho = if (deLado) 1.5f else 1.3f
        for ((i, u) in pies.withIndex()) {
            val central = !deLado && i == pies.size / 2
            if (central) drawPath(w.cuadro(u - 20f, u + 20f, 160f, 172f, 1), w.c(u, 164f, lerp(C.PIEDRA, C.CAL, 0.4f)))
            figura(w, u, if (central) 160f else 171f, if (central) 72f else 62f + (i % 3) * 2f, mantos[i % 8], tunicas[i % 8], if (i % 2 == 0) 1f else -1f, (i * 2 + 1) % 4, ancho)
        }
    }

    /** Una figura de pie en [u], con los pies en [pie] y de alto [h]: nimbo, túnica, manto cruzado, cabeza sin rasgos y lo que lleva en la mano. */
    private fun DrawScope.figura(w: Muro, u: Float, pie: Float, h: Float, manto: Color, tunica: Color, gira: Float, lleva: Int, ancho: Float) {
        val b = h * 0.17f * ancho; val hom = pie - h * 0.8f; val cab = pie - h * 0.89f
        drawPath(w.disco(u, cab, h * 0.105f * ancho, h * 0.105f), w.c(u, cab, C.ORO))
        drawPath(w.poli(u - b * 0.62f, hom, u + b * 0.62f, hom, u + b, pie, u - b, pie), w.c(u, pie - h * 0.4f, tunica))
        drawPath(w.poli(u - gira * b * 0.72f, hom + h * 0.02f, u + gira * b * 0.3f, hom - h * 0.01f, u + gira * b * 1.14f, pie - h * 0.2f, u + gira * b * 0.2f, pie - h * 0.05f, u - gira * b * 1.12f, pie - h * 0.34f), w.c(u, pie - h * 0.5f, manto))
        val pliegue = w.c(u, pie - h * 0.4f, lerp(manto, C.HOLLIN, 0.42f))
        for (k in 0..2) drawLine(pliegue, w.o(u - gira * b * (0.5f - 0.3f * k), hom + h * 0.1f), w.o(u + gira * b * (0.2f + 0.3f * k), pie - h * (0.1f + 0.05f * k)), 0.5f)
        drawLine(w.c(u, hom, lerp(manto, C.CAL, 0.5f)), w.o(u - gira * b * 0.68f, hom + h * 0.05f), w.o(u - gira * b * 1.04f, pie - h * 0.34f), 0.8f)
        drawLine(w.c(u, pie, lerp(tunica, C.HOLLIN, 0.4f)), w.o(u + gira * b * 0.5f, pie - h * 0.16f), w.o(u + gira * b * 0.6f, pie), 0.5f)
        drawPath(w.disco(u, cab, h * 0.052f * ancho, h * 0.06f), w.c(u, cab, C.CARNE))
        drawPath(w.poli(u - h * 0.056f * ancho, cab, u - h * 0.04f * ancho, cab - h * 0.066f, u + h * 0.04f * ancho, cab - h * 0.066f, u + h * 0.056f * ancho, cab, u + h * 0.03f * ancho, cab - h * 0.034f, u - h * 0.03f * ancho, cab - h * 0.034f), w.c(u, cab, lerp(C.OCRE, C.MADERA_OSC, 0.5f)))
        when (lleva) {
            1 -> drawPath(w.poli(u + gira * b * 0.1f, pie - h * 0.57f, u + gira * b * 0.74f, pie - h * 0.59f, u + gira * b * 0.76f, pie - h * 0.46f, u + gira * b * 0.12f, pie - h * 0.44f), w.c(u, pie - h * 0.5f, lerp(C.ROJO, C.MADERA_OSC, 0.3f)))
            2 -> drawLine(w.c(u, hom, lerp(C.VERDE, C.HOLLIN, 0.2f)), w.o(u - gira * b * 0.9f, pie - h * 0.42f), w.o(u - gira * b * 1.36f, hom - h * 0.14f), 1.1f, StrokeCap.Round)
            3 -> drawLine(w.c(u, hom, C.ORO), w.o(u + gira * b * 1.24f, pie), w.o(u + gira * b * 1.24f, cab - h * 0.1f), 0.9f)
            else -> Unit
        }
    }

    /** El zócalo pintado de falsos mármoles, que da la vuelta a la capilla. */
    private fun DrawScope.zocalo(w: Muro, u0: Float, u1: Float, paneles: Int) {
        val du = (u1 - u0) / paneles
        for (i in 0 until paneles) { val a = u0 + i * du
            franja(w, lerp(C.PIEDRA, C.CAL, 0.3f), a, a + du, 176f, 200f)
            drawPath(w.cuadro(a + du * 0.1f, a + du * 0.9f, 179f, 197f, 1), w.c(a + du / 2f, 188f, lerp(if (i % 2 == 0) C.ROJO else C.VERDE, C.PIEDRA, 0.45f)))
            drawPath(w.poli(a + du * 0.5f, 181.5f, a + du * 0.8f, 188f, a + du * 0.5f, 194.5f, a + du * 0.2f, 188f), w.c(a + du / 2f, 188f, lerp(if (i % 2 == 0) C.OCRE else C.ROJO, C.CAL, 0.45f))) }
    }

    /** La pared de la ventana no lleva figuras: un friso bajo la cornisa y una cortina pintada, con sus pliegues, de la ventana al zócalo. */
    private fun DrawScope.cortina(w: Muro) {
        for (i in 0 until 8) franja(w, C.OCRE, i * 25f, (i + 1) * 25f, 88f, 93f)
        for (i in 0 until 14) { val t = 6f + i * 13.6f; val c = if (i % 2 == 0) lerp(C.ROJO, C.YESO, 0.7f) else lerp(C.ROJO, C.YESO, 0.82f)
            drawPath(w.poli(t, 152f, t + 13.6f, 152f, t + 13.6f, 174f, t + 6.8f, 176f, t, 174f), w.c(t, 164f, c)) }
        for (i in 0 until 8) franja(w, C.ORO, i * 25f, (i + 1) * 25f, 150.6f, 152.6f)
    }

    // ---------------------------------------------------------------- El hollín

    /**
     * El hollín de siglos de velas sobre la zona [z]: no tapa del todo, deja adivinar las figuras. Con [catas], dos
     * cuadrados limpios; con [angel], el hueco por donde asoma la cara que encontró Giulia.
     */
    private fun DrawScope.hollin(w: Muro, z: Zona, indice: Int, catas: Boolean, angel: Boolean) {
        val az = Azar(Semilla.de("urbe", "hollin", indice)); val du = z.u1 - z.u0; val dv = z.v1 - z.v0
        val capa = w.cuadro(z.u0, z.u1, z.v0, z.v1).apply {
            fillType = PathFillType.EvenOdd
            if (catas) for (k in 0..1) { val u = z.u0 + du * (0.16f + 0.46f * k + az.entre(0f, 0.08f)); val v = z.v0 + dv * (0.2f + 0.34f * k + az.entre(0f, 0.1f)); addPath(w.cuadro(u, u + du * 0.2f, v, v + dv * 0.26f, 2)) }
            if (angel) addPath(w.disco(136.6f, 57f, 15f, 16f, 14))
        }
        val o = (z.u0 + z.u1) / 2f; val v = (z.v0 + z.v1) / 2f
        // Un velo pardo, no una pared negra: debajo se adivina la pintura.
        drawPath(capa, w.c(o, v, lerp(C.HOLLIN, C.MADERA_OSC, 0.55f)).copy(alpha = if (z.sitio == Sitio.BOVEDA) 0.46f else 0.54f))
        // El borde de la limpieza: la zona en la que se trabaja y sus catas llevan un filo claro.
        if (catas || angel) drawPath(capa, w.c(o, v, C.CAL).copy(alpha = 0.6f), style = Stroke(0.9f))
        clipPath(capa) {
            // Más negro arriba, donde se junta el humo, y con chorreras.
            drawPath(w.cuadro(z.u0, z.u1, z.v0, z.v0 + dv * 0.4f), w.c(o, z.v0, C.HOLLIN).copy(alpha = 0.16f))
            repeat(7) { val u = z.u0 + du * az.entre(0.04f, 0.96f); val v0 = z.v0 + dv * az.entre(0f, 0.5f); drawLine(w.c(u, v0, if (it % 2 == 0) C.HOLLIN else C.PIEDRA).copy(alpha = 0.3f), w.o(u, v0), w.o(u, v0 + dv * az.entre(0.2f, 0.5f)), 0.7f) }
        }
    }
}
