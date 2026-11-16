package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.ChicosAntiguas.CANTO
import io.github.ruddy2003.vestigium.ui.escenas.ChicosAntiguas.CLAVO
import io.github.ruddy2003.vestigium.ui.escenas.ChicosAntiguas.MURO
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.contacto
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.empolvado
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.labio
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.punto
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.sombra
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.trazo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosObjetos.tubo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Los dos objetos chicos que llevan dibujo propio, «imágenes dentro de la imagen»: el libro de crónicas, abierto y
 * recostado en el muro sobre el poyo, y el mapa del paso, en su piel de cabra, colgado del clavo del muro con el pie en la
 * losa. Los dos miran a la plaza, como el muro, y reciben el sol de la tarde de cara. Sin letras: renglones de rayas, y las
 * marcas son las runas inventadas del cuadro.
 */
internal object PapelesAntiguas {
    private val C = ColorAntiguas
    private val O = ColorObjetos

    // ---------------------------------------------------------------- El libro

    /** El libro abierto: el lomo abajo en ([x0], [z0]) y arriba contra el muro; [pt] da un punto de sus hojas (u de −ancho a ancho, v de 0 abajo a 1 arriba, b hacia fuera). */
    private class Libro {
        val x0 = 0.08f; val z0 = MURO - 0.11f; val ancho = 0.3f; val largo = 0.42f
        private val zTop = MURO - 0.016f
        private val yTop = sqrt((largo * largo - (zTop - z0) * (zTop - z0)).coerceAtLeast(0.01f))
        val n: V3 = LuzObjeto.unidad(V3(0f, zTop - z0, -yTop))
        fun pt(u: Float, v: Float, b: Float): V3 = V3(x0 + u, yTop * v + n.y * b, z0 + (zTop - z0) * v + n.z * b)
        /** Cuánto se levanta la hoja sobre su tapa a [u] metros del lomo. */
        fun bombeo(u: Float): Float { val f = (abs(u) / ancho).coerceIn(0f, 1f); return 0.045f * sin(PI.toFloat() * (0.15f + 0.85f * f)) * (1f - 0.35f * f) + 0.006f }
    }

    /**
     * El libro de crónicas, abierto y recostado en el muro sobre la losa (la tinta se seca al sol): las tapas de madera
     * forradas de piel asoman alrededor de las hojas, gastadas en las esquinas, con su correa de cerrar; las hojas se
     * abomban desde el lomo. En la de la izquierda, renglones de rayas (sin letras); en la de la derecha, un dibujo de Brenna
     * a tinta, el paso visto desde la posada (las dos cumbres, la muesca y las piedras del camino), y renglones debajo. La
     * cinta roja de señal cae por el lomo. 1,45 veces más grande que uno de verdad.
     */
    fun DrawScope.libro(lm: LuzObjeto) {
        val g = Libro()
        contacto(lm, g.x0, g.z0, 0.66f, 0.1f)
        val env = listOf(g.pt(-g.ancho - 0.02f, 0f, 0f), g.pt(g.ancho + 0.02f, 0f, 0f), g.pt(-g.ancho - 0.02f, 1f, 0f), g.pt(g.ancho + 0.02f, 1f, 0f), g.pt(0f, 1f, 0.03f), g.pt(0f, 0f, -0.04f))
        sombra(lm, env, MURO, ChicosAntiguas.losa(lm), ChicosAntiguas.muro(lm))
        tapas(lm, g)
        for (lado in listOf(-1f, 1f)) hoja(lm, g, lado)
        drawLine(lm.c(C.ESCORIA, g.n).copy(alpha = 0.7f), lm.q(g.pt(0f, 0.02f, -0.012f)), lm.q(g.pt(0f, 0.98f, -0.012f)), 0.7f)
        renglones(lm, g)
        dibujoDelPaso(lm, g)
        cinta(lm, g)
        empolvado(lm, listOf(lm.q(g.pt(-g.ancho, 1f, 0f)), lm.q(g.pt(0f, 1f, 0f)), lm.q(g.pt(g.ancho, 1f, 0f))), 1.1f)
        labio(lm, g.x0, g.z0, 0.7f)
    }

    /** Las tapas: tablas forradas de piel, con su grueso abajo, las esquinas gastadas y la correa de cerrar. */
    private fun DrawScope.tapas(lm: LuzObjeto, g: Libro) {
        val m = 0.022f; val a = -g.ancho - m; val b = g.ancho + m
        drawPath(poligono(lm.q(g.pt(a, -0.03f, -0.004f)), lm.q(g.pt(b, -0.03f, -0.004f)), lm.q(g.pt(b, 1.04f, -0.004f)), lm.q(g.pt(a, 1.04f, -0.004f))), lm.c(O.TAPAS, g.n, -0.1f))
        // El grueso de las tablas, abajo, que apoya en la losa.
        drawPath(poligono(lm.q(g.pt(a, -0.03f, -0.004f)), lm.q(g.pt(b, -0.03f, -0.004f)), lm.q(g.pt(b, -0.03f, 0.03f)), lm.q(g.pt(a, -0.03f, 0.03f))), lm.c(lerp(O.TAPAS, C.MADERA, 0.4f), V3(0f, -0.2f, -1f), 0.1f))
        for (u in listOf(a, b)) for (v in listOf(-0.03f, 1.04f)) punto(lm.q(g.pt(u * 0.97f, v, 0f)), 3.2f, 2.4f, lm.c(lerp(O.TAPAS, C.FRESNO, 0.45f), g.n, 0.2f).copy(alpha = 0.7f))
        val c0 = g.pt(b, 0.45f, 0f)
        tubo(lm, listOf(c0, c0 + V3(0.03f, -0.08f, -0.02f), c0 + V3(0.02f, -0.18f, -0.05f), V3(c0.x + 0.04f, 0.004f, c0.z - 0.1f)), listOf(0.006f, 0.006f, 0.006f, 0.006f), O.TAPAS, 0.3f)
    }

    /** Una hoja abierta, abombada desde el lomo, del [lado] dado (−1 la izquierda, 1 la derecha), con su canto de hojas debajo. */
    private fun DrawScope.hoja(lm: LuzObjeto, g: Libro, lado: Float) {
        val us = (0..10).map { lado * g.ancho * it / 10f }
        val abajo = us.map { lm.q(g.pt(it, 0f, g.bombeo(it))) }; val arriba = us.map { lm.q(g.pt(it, 1f, g.bombeo(it))) }
        drawPath(poligono(abajo + us.reversed().map { lm.q(g.pt(it, 0f, 0f)) }), lm.c(lerp(O.HOJA, C.FRESNO, 0.35f), V3(0f, -0.2f, -1f), -0.1f))
        for (j in 1..3) trazo(us.map { lm.q(g.pt(it, 0f, g.bombeo(it) * j / 4f)) }, lm.c(lerp(O.HOJA, C.CUERO, 0.4f), g.n, -0.4f).copy(alpha = 0.5f), 0.3f)
        val nIn = LuzObjeto.unidad(g.n + V3(lado * 0.35f, 0f, 0f)); val nOut = LuzObjeto.unidad(g.n + V3(-lado * 0.25f, 0f, 0f))
        val x0 = lm.q(g.pt(0f, 0.5f, 0f)).x; val x1 = lm.q(g.pt(lado * g.ancho, 0.5f, 0f)).x
        drawPath(poligono(abajo + arriba.reversed()), Brush.horizontalGradient(listOf(lm.c(O.HOJA, nIn, -0.25f), lm.c(O.HOJA, nOut, 0.1f), lm.c(lerp(O.HOJA, C.FRESNO, 0.3f), g.n)), x0, x1))
    }

    /** Los renglones de la hoja de la izquierda y los de debajo del dibujo: rayas de tinta de largos distintos, sin letras. */
    private fun DrawScope.renglones(lm: LuzObjeto, g: Libro) {
        val az = SitioAntiguas.fijo("renglones", lm.e.dia % 7)
        val tinta = lm.c(O.TINTA, g.n, -0.2f).copy(alpha = 0.72f)
        fun renglon(u0: Float, u1: Float, v: Float) {
            var u = u0
            while (u < u1) {
                val fin = minOf(u + 0.015f + az.real01() * 0.05f, u1)
                drawLine(tinta, lm.q(g.pt(u, v, g.bombeo(u) + 0.002f)), lm.q(g.pt(fin, v, g.bombeo(fin) + 0.002f)), 0.38f)
                u = fin + 0.012f + az.real01() * 0.01f
            }
        }
        for (j in 0 until 11) renglon(-g.ancho + 0.035f, -0.03f - (if (j == 10) 0.11f else 0f), 0.88f - j * 0.075f)
        for (j in 0 until 3) renglon(0.035f, g.ancho - 0.035f - (if (j == 2) 0.12f else 0f), 0.3f - j * 0.075f)
    }

    /** El dibujo de la hoja de la derecha, a tinta: dos cumbres con su muesca, las piedras del camino que suben y la casa abajo. */
    private fun DrawScope.dibujoDelPaso(lm: LuzObjeto, g: Libro) {
        val tinta = lm.c(O.TINTA, g.n, -0.2f).copy(alpha = 0.85f)
        fun q(u: Float, v: Float): Offset { val x = 0.03f + u * (g.ancho - 0.06f); return lm.q(g.pt(x, v, g.bombeo(x) + 0.002f)) }
        trazo(listOf(q(0f, 0.6f), q(0.18f, 0.78f), q(0.3f, 0.92f), q(0.42f, 0.8f), q(0.55f, 0.72f), q(0.68f, 0.84f), q(0.8f, 0.9f), q(1f, 0.66f)), tinta, 0.5f)
        trazo(listOf(q(0.22f, 0.8f), q(0.26f, 0.72f)), tinta, 0.35f); trazo(listOf(q(0.74f, 0.82f), q(0.7f, 0.74f)), tinta, 0.35f)
        for ((u, v) in listOf(0.4f to 0.42f, 0.66f to 0.48f, 0.36f to 0.55f, 0.62f to 0.62f, 0.5f to 0.68f)) punto(q(u, v), 1.3f, 1f, tinta)
        trazo(listOf(q(0.42f, 0.36f), q(0.42f, 0.4f), q(0.5f, 0.43f), q(0.58f, 0.4f), q(0.58f, 0.36f)), tinta, 0.4f)
    }

    /** La cinta roja de señal, que sale del lomo y cae a la losa. */
    private fun DrawScope.cinta(lm: LuzObjeto, g: Libro) {
        val eje = listOf(g.pt(0.004f, 0.97f, 0.008f), g.pt(0.012f, 0.6f, 0.02f), g.pt(0.01f, 0.1f, 0.03f), V3(g.x0 + 0.03f, 0.003f, g.z0 - 0.09f))
        tubo(lm, eje, eje.map { 0.005f }, C.RUBI, 0.2f)
    }

    // ---------------------------------------------------------------- El mapa

    /** El contorno de la piel de cabra, en (u, v): u de −1 a 1 a lo ancho, v de 0 (abajo) a 1 (arriba): el cuello arriba, las cuatro patas, la cola abajo. */
    private val PIEL = listOf(
        0f to 1.03f, 0.16f to 0.97f, 0.48f to 0.93f, 0.8f to 0.98f, 0.94f to 0.9f, 0.8f to 0.82f, 0.9f to 0.6f, 0.85f to 0.33f,
        0.98f to 0.2f, 0.9f to 0.09f, 0.72f to 0.12f, 0.4f to 0.03f, 0.1f to 0f, -0.04f to -0.04f, -0.15f to 0f, -0.45f to 0.04f,
        -0.76f to 0.11f, -0.96f to 0.07f, -1f to 0.19f, -0.85f to 0.31f, -0.9f to 0.58f, -0.82f to 0.8f, -0.97f to 0.88f, -0.86f to 0.97f,
        -0.5f to 0.92f, -0.16f to 0.97f,
    )

    /** Un punto de la piel: colgada del muro y con lo de abajo doblado hacia delante sobre la losa. */
    private fun enPiel(u: Float, v: Float): V3 = V3(0.1f + u * 0.24f, (v * 0.6f).coerceAtLeast(0.004f), MURO - 0.008f - (0.14f - v).coerceAtLeast(0f) * 0.55f)

    /**
     * El mapa del paso, «a mano, con tinta de agallas, en piel de cabra raspada»: la piel entera, con su cuello y sus patas,
     * colgada del clavo del muro por una tira de cuero, con el borde de abajo apoyado y algo doblado en la losa. «No se
     * pinta el camino, que cambia, sino las piedras marcadas»: de abajo arriba, la posada, el torrente con su puente, las
     * piedras una tras otra en zetas, cada una con su signo, y arriba las dos cumbres con la muesca. Sin lago, sin rosa de
     * los vientos, sin cruces ni letras. 1,3 veces más grande.
     */
    fun DrawScope.mapa(lm: LuzObjeto) {
        val borde = PIEL.map { (u, v) -> enPiel(u, v) }
        val forma = poligono(borde.map { lm.q(it) })
        val n = V3(0f, 0.05f, -1f)
        contacto(lm, 0.1f, MURO - 0.06f, 0.5f, 0.08f, fuerza = 0.3f)
        sombra(lm, borde + listOf(V3(0.1f, 0.62f, MURO - 0.02f)), MURO, ChicosAntiguas.losa(lm), ChicosAntiguas.muro(lm))
        trazo(listOf(lm.q(CLAVO), lm.q(enPiel(0f, 1.0f))), lm.c(C.CUERO, n, -0.1f), 0.9f)
        punto(lm.q(CLAVO), 1.6f, 1.6f, lm.c(O.HIERRO, V3(0f, 0.3f, -1f), 0.3f))
        drawPath(forma, lm.c(O.CABRA, n, 0.05f))
        clipPath(forma) {
            drawPath(forma, lm.c(lerp(O.CABRA, C.CUERO, 0.45f), n, -0.1f).copy(alpha = 0.55f), style = Stroke(3.2f))
            val az = SitioAntiguas.fijo("piel")
            repeat(14) { val u = az.entre(-0.7f, 0.7f); val v = az.entre(0.15f, 0.85f); drawLine(lm.c(lerp(O.CABRA, C.NIEVE, 0.4f), n, 0.2f).copy(alpha = 0.35f), lm.q(enPiel(u, v)), lm.q(enPiel(u + 0.18f, v + 0.03f)), 0.5f) }
            // Lo doblado de abajo, que mira al cielo, más claro.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, lm.c(O.CABRA, V3(0f, 1f, -0.3f), 0.3f).copy(alpha = 0.6f)), lm.q(enPiel(0f, 0.14f)).y, lm.q(enPiel(0f, 0f)).y), Offset(lm.q(enPiel(-1.1f, 0f)).x, lm.q(enPiel(0f, 0.14f)).y), androidx.compose.ui.geometry.Size(60f, 12f))
        }
        pelos(lm, borde)
        dibujoDelMapa(lm, n)
        empolvado(lm, listOf(lm.q(enPiel(-0.86f, 0.97f)), lm.q(enPiel(-0.5f, 0.93f)), lm.q(enPiel(0f, 1.0f)), lm.q(enPiel(0.48f, 0.94f)), lm.q(enPiel(0.8f, 0.98f))), 1f)
        labio(lm, 0.1f, MURO - 0.1f, 0.5f)
    }

    /** Los pelos que quedan en el borde de la piel, donde no se raspó. */
    private fun DrawScope.pelos(lm: LuzObjeto, borde: List<V3>) {
        val c = lm.c(lerp(O.CABRA, C.CUERO, 0.6f), V3(0f, 0f, -1f), -0.1f).copy(alpha = 0.7f)
        for (i in borde.indices step 2) { val a = lm.q(borde[i]); val b = lm.q(borde[(i + 1) % borde.size]); val m = (a + b) / 2f; drawLine(c, m, m + (m - lm.q(enPiel(0f, 0.5f))) * 0.04f, 0.5f) }
    }

    /** Lo dibujado a tinta: la posada, el torrente y su puente, las piedras marcadas en zetas, cada una con su signo, y las dos cumbres. */
    private fun DrawScope.dibujoDelMapa(lm: LuzObjeto, n: V3) {
        val tinta = lm.c(O.TINTA, n, -0.3f).copy(alpha = 0.88f)
        fun q(u: Float, v: Float) = lm.q(enPiel(u * 0.78f, 0.06f + v * 0.88f))
        // La posada: una casa con su tejado.
        trazo(listOf(q(-0.07f, 0.1f), q(-0.07f, 0.16f), q(0f, 0.21f), q(0.07f, 0.16f), q(0.07f, 0.1f), q(-0.07f, 0.1f)), tinta, 0.5f)
        // El torrente, de lado a lado, y el puente que lo cruza.
        trazo((0..12).map { j -> val u = -0.85f + j * 1.7f / 12f; q(u, 0.27f + 0.025f * sin(j * 1.3f)) }, tinta, 0.45f)
        trazo((0..12).map { j -> val u = -0.85f + j * 1.7f / 12f; q(u, 0.31f + 0.025f * sin(j * 1.3f + 0.4f)) }, tinta, 0.35f)
        for (du in listOf(-0.04f, 0.03f)) trazo(listOf(q(-0.16f + du, 0.24f), q(-0.12f + du, 0.34f)), tinta, 0.45f)
        // Las piedras marcadas, una tras otra en zetas, sin el camino entre ellas; cada una con su signo, un rasguño claro.
        val piedras = listOf(-0.42f to 0.4f, 0.5f to 0.45f, -0.58f to 0.53f, 0.4f to 0.6f, -0.4f to 0.68f, 0.3f to 0.74f, 0.0f to 0.81f)
        for ((i, uv) in piedras.withIndex()) piedraDelMapa(lm, q(uv.first, uv.second), i, tinta, n)
        // Las dos cumbres y la muesca del collado.
        trazo(listOf(q(-0.85f, 0.84f), q(-0.55f, 0.94f), q(-0.38f, 0.99f), q(-0.18f, 0.9f), q(0f, 0.86f), q(0.16f, 0.92f), q(0.42f, 0.98f), q(0.7f, 0.9f), q(0.85f, 0.84f)), tinta, 0.55f)
    }

    /**
     * Una piedra del mapa: el mojón dibujado en tinta llena, una piedra alzada en punta como las del camino, con su signo
     * rasgado en claro dentro (cada piedra el suyo: una raya tendida o en sesgo, o un punto). Nada que se lea como letra.
     */
    private fun DrawScope.piedraDelMapa(lm: LuzObjeto, c: Offset, i: Int, tinta: Color, n: V3) {
        val k = lm.k() * 0.026f
        drawPath(poligono(listOf(c + Offset(-k, k * 0.55f), c + Offset(-k * 0.55f, -k * 0.35f), c + Offset(-k * 0.1f, -k * 1.05f), c + Offset(k * 0.5f, -k * 0.4f), c + Offset(k * 0.95f, k * 0.55f))), tinta)
        val claro = lm.c(O.CABRA, n, 0.15f)
        when (i % 3) {
            0 -> drawLine(claro, c + Offset(-k * 0.4f, k * 0.05f), c + Offset(k * 0.4f, k * 0.05f), 0.4f)
            1 -> drawLine(claro, c + Offset(-k * 0.3f, k * 0.25f), c + Offset(k * 0.25f, -k * 0.3f), 0.4f)
            else -> drawCircle(claro, k * 0.17f, c + Offset(0f, k * 0.05f))
        }
    }
}
