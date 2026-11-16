package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.GaleriaCentro.cabinaDeVidrio
import io.github.ruddy2003.vestigium.ui.escenas.GaleriaCentro.carril
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.ABAJO
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.ARRIBA
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.cara
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.linea
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.trazo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo que sostiene el hall y lo que lo cruza (`composicion_laboratorio_centro.md`, «Marco» y «Lo que cruza»):
 * - **las costillas** de hormigón, aletas que suben del suelo del comedor por el borde del vacío, atraviesan las galerías y se
 *   doblan arriba para correr por la bóveda hasta el aro del pozo. La de cerca, a la izquierda, cortada por el borde, es el
 *   marco del cuadro (de lado de luz y de sombra, con las juntas del encofrado, chorreras y una rejilla); las del otro lado
 *   dibujan la curva del espacio;
 * - **la pasarela**, de un lado a otro del vacío a la altura de la segunda galería, en diagonal: hormigón fino y barandilla de
 *   vidrio. Su sombra cae en la mancha del pozo por la mañana ([CentroLab.sombraDeLaPasarela]);
 * - **los elevadores**, que corren por su carril en la cara de dos costillas.
 */
internal object EstructuraCentro {
    private val C = ColorLaboratorio

    /** Hasta dónde sube la parte recta de una costilla, y cuánto baja por debajo de la bóveda la parte curva. */
    private val ALTO_RECTO: Float = GeoCentro.bovedaEn(GeoCentro.RC) - 0.5f
    private const val BAJA = 0.5f

    private fun ejes(g: Float): Pair<V3, V3> { val a = GeoCentro.rad(g); return V3(cos(a), 0f, sin(a)) to V3(-sin(a), 0f, cos(a)) }

    /** Las costillas que se ven al otro lado del vacío (todas menos la de cerca), con los carriles y el elevador quieto. */
    fun DrawScope.costillasLejos(v: VistaCentro) {
        for (g in GeoCentro.COSTILLAS) if (g != GeoCentro.COSTILLAS[0]) costilla(v, g)
        carril(v, GaleriaCentro.CABINA_QUIETA); carril(v, GaleriaCentro.CABINA_VIVA)
        cabinaDeVidrio(v, GaleriaCentro.CABINA_QUIETA, GeoCentro.G1 + 0.02f)
    }

    /** La costilla de cerca, a la izquierda: el marco. */
    fun DrawScope.costillaCerca(v: VistaCentro) { costilla(v, GeoCentro.COSTILLAS[0]) }

    /**
     * Una costilla: la parte recta (tres tramos: bajo la primera galería solo asoma lo que sale del canto; luego entera) y la
     * parte curva, que corre por debajo de la bóveda hasta el aro.
     */
    private fun DrawScope.costilla(v: VistaCentro, g: Float) {
        val (u, t) = ejes(g)
        val r0 = GeoCentro.RC; val ht = GeoCentro.GRUESO_COSTILLA / 2f
        val propio = lerp(C.HORMIGON, C.TIERRA, 0.1f)
        fun caja(ra: Float, rb: Float, y0: Float, y1: Float) {
            fun q(r: Float, s: Float, y: Float) = V3(u.x * r + t.x * s, y, u.z * r + t.z * s)
            val lj = GaleriaCentro.lejos(q(ra, 0f, y0))
            cara(v, listOf(q(ra, -ht, y0), q(ra, ht, y0), q(ra, ht, y1), q(ra, -ht, y1)), propio, u * -1f, lj)          // la cara de dentro
            cara(v, listOf(q(rb, -ht, y0), q(rb, ht, y0), q(rb, ht, y1), q(rb, -ht, y1)), propio, u, lj)               // la de fuera
            cara(v, listOf(q(ra, -ht, y0), q(rb, -ht, y0), q(rb, -ht, y1), q(ra, -ht, y1)), propio, t * -1f, lj)       // los costados
            cara(v, listOf(q(ra, ht, y0), q(rb, ht, y0), q(rb, ht, y1), q(ra, ht, y1)), propio, t, lj)
        }
        caja(r0, GeoCentro.RV, 0f, GeoCentro.G1)
        caja(r0, r0 + GeoCentro.FONDO_COSTILLA, GeoCentro.G1, ALTO_RECTO)
        // La parte curva, por debajo de la bóveda: la cara de abajo y el costado que se ve.
        val n = 8
        val perfil = (0..n).map { i -> val r = r0 + (GeoCentro.RO + 0.7f - r0) * i / n; r to GeoCentro.bovedaEn(r) }
        for (i in 0 until n) {
            val (ra, ya) = perfil[i]; val (rb, yb) = perfil[i + 1]
            val ya2 = if (i == 0) ALTO_RECTO else ya - BAJA; val yb2 = yb - BAJA
            fun q(r: Float, s: Float, y: Float) = V3(u.x * r + t.x * s, y, u.z * r + t.z * s)
            val lj = GaleriaCentro.lejos(q(ra, 0f, ya))
            cara(v, listOf(q(ra, -ht, ya2), q(rb, -ht, yb2), q(rb, ht, yb2), q(ra, ht, ya2)), propio, V3(u.x * -0.4f, -1f, u.z * -0.4f), lj)
            cara(v, listOf(q(ra, -ht, ya2), q(rb, -ht, yb2), q(rb, -ht, yb), q(ra, -ht, ya)), propio, t * -1f, lj)
            cara(v, listOf(q(ra, ht, ya2), q(rb, ht, yb2), q(rb, ht, yb), q(ra, ht, ya)), propio, t, lj)
        }
        materia(v, g, u, t)
    }

    /**
     * La materia de una costilla, en las caras que se ven: las juntas del encofrado cada 0.9 m, las chorreras bajo cada losa,
     * unos desconchones con el gris de debajo y el filo de luz de la arista que da al vacío; en la de cerca, la rejilla del aire.
     */
    private fun DrawScope.materia(v: VistaCentro, g: Float, u: V3, t: V3) {
        val r0 = GeoCentro.RC; val r1 = r0 + GeoCentro.FONDO_COSTILLA; val ht = GeoCentro.GRUESO_COSTILLA / 2f
        fun q(r: Float, s: Float, y: Float) = V3(u.x * r + t.x * s, y, u.z * r + t.z * s)
        // Las caras que se ven, cada una como (de, a) en sus ejes: la de dentro, la de fuera y los dos costados.
        data class Cara(val a: (Float) -> V3, val b: (Float) -> V3, val n: V3, val lado: (Float, Float) -> V3)
        val caras = listOf(
            Cara({ y -> q(r0 - 0.005f, -ht, y) }, { y -> q(r0 - 0.005f, ht, y) }, u * -1f) { f, y -> q(r0 - 0.005f, -ht + 2f * ht * f, y) },
            Cara({ y -> q(r1 + 0.005f, -ht, y) }, { y -> q(r1 + 0.005f, ht, y) }, u) { f, y -> q(r1 + 0.005f, -ht + 2f * ht * f, y) },
            Cara({ y -> q(r0, ht + 0.005f, y) }, { y -> q(r1, ht + 0.005f, y) }, t) { f, y -> q(r0 + (r1 - r0) * f, ht + 0.005f, y) },
            Cara({ y -> q(r0, -ht - 0.005f, y) }, { y -> q(r1, -ht - 0.005f, y) }, t * -1f) { f, y -> q(r0 + (r1 - r0) * f, -ht - 0.005f, y) },
        ).filter { CamCentro.seVe(it.a(2f), it.n) }
        val junta = lerp(C.SOMBRA, C.HORMIGON, 0.35f)
        val az = Azar(Semilla.de("laboratorio", "centro", "costilla", g.toInt()))
        val sucio = lerp(C.TIERRA, C.SOMBRA, 0.3f)
        for (c in caras) {
            var y = 0.9f
            while (y < ALTO_RECTO) {
                if (y < GeoCentro.G1 - GeoCentro.LOSA || y > GeoCentro.G1) linea(v, c.a(y), c.b(y), junta, c.n, (0.022f * CamCentro.k(c.a(y))).coerceIn(0.25f, 1.6f), 0.55f)
                y += 0.9f
            }
            // Las chorreras: el agua de los riegos que baja bajo cada losa.
            for (y0 in listOf(GeoCentro.G2 - GeoCentro.LOSA, ALTO_RECTO, GeoCentro.G1 - GeoCentro.LOSA)) repeat(2) {
                val f = az.entre(0.1f, 0.9f); val largo = az.entre(0.6f, 2.4f)
                trazo(v, listOf(c.lado(f, y0), c.lado(f + az.entre(-0.05f, 0.05f), y0 - largo * 0.5f), c.lado(f, y0 - largo)), sucio, c.n, az.entre(0.03f, 0.07f), az.entre(0.15f, 0.3f))
            }
            // Un desconchón, con el hormigón gris a la vista.
            val f = az.entre(0.15f, 0.7f); val y1 = az.entre(1.2f, ALTO_RECTO - 1.5f); val h = az.entre(0.15f, 0.45f); val w = az.entre(0.12f, 0.25f)
            cara(v, listOf(c.lado(f, y1), c.lado(f + w, y1 + 0.04f), c.lado(f + w * 0.8f, y1 + h), c.lado(f - 0.03f, y1 + h * 0.8f)), lerp(C.TIERRA, C.HORMIGON, 0.45f), c.n, siempre = true)
        }
        // El filo de luz: la arista de dentro que da al vacío, del lado que mira a los ojos.
        val s = if (CamCentro.seVe(q(r0, ht, 2f), t)) ht else -ht
        trazo(v, listOf(q(r0, s, GeoCentro.G1 + 0.05f), q(r0, s, ALTO_RECTO)), lerp(C.HORMIGON, Color.White, 0.5f), u * -1f + t * (s / ht), 0.035f, 0.6f)
        if (g == GeoCentro.COSTILLAS[0]) {
            // La rejilla del aire, a la altura de los ojos, en el costado que se ve.
            val c = caras.firstOrNull { it.n.punto(t) != 0f && kotlin.math.abs(it.n.punto(u)) < 0.1f } ?: return
            val ya = CamCentro.OJO_Y + 0.9f
            cara(v, listOf(c.lado(0.45f, ya), c.lado(0.72f, ya), c.lado(0.72f, ya + 0.26f), c.lado(0.45f, ya + 0.26f)), lerp(C.ACERO, C.HORMIGON, 0.2f), c.n, siempre = true)
            for (i in 1..4) linea(v, c.lado(0.47f, ya + i * 0.052f), c.lado(0.7f, ya + i * 0.052f), lerp(C.ACERO, C.NEGRO, 0.35f), c.n, 0.7f)
        }
    }

    // ---------------------------------------------------------------- La pasarela

    /** La pasarela: la losa fina vista por debajo, su canto, las dos barandillas de vidrio y su pasamanos. */
    fun DrawScope.pasarela(v: VistaCentro) {
        val a = GeoCentro.PASA0; val b = GeoCentro.PASA1
        val dir = (b - a).unidad(); val lado = V3(-dir.z, 0f, dir.x) * (GeoCentro.PASA_ANCHO / 2f)
        val y1 = GeoCentro.G2; val y0 = y1 - 0.32f
        val propio = lerp(C.HORMIGON, C.TELA, 0.25f)
        val n = 16
        for (i in 0 until n) {
            val p = a + (b - a) * (i / n.toFloat()); val q = a + (b - a) * ((i + 1) / n.toFloat())
            fun w(c: V3, s: Float, y: Float) = V3(c.x + lado.x * s, y, c.z + lado.z * s)
            val lj = GaleriaCentro.lejos(p)
            cara(v, listOf(w(p, -1f, y0), w(q, -1f, y0), w(q, 1f, y0), w(p, 1f, y0)), lerp(propio, C.TIERRA, 0.12f), ABAJO, lj)
            cara(v, listOf(w(p, -1f, y0), w(q, -1f, y0), w(q, -1f, y1), w(p, -1f, y1)), propio, lado * -1f, lj)
            cara(v, listOf(w(p, 1f, y0), w(q, 1f, y0), w(q, 1f, y1), w(p, 1f, y1)), propio, lado, lj)
        }
        // Las barandillas: el vidrio, su canto de arriba y el pasamanos.
        for (s in listOf(-1f, 1f)) {
            val ps = (0..n).map { a + (b - a) * (it / n.toFloat()) + lado * (s * 0.92f) }
            for (i in 0 until n) {
                val f = CamCentro.forma(listOf(ps[i] + V3(0f, y1 - a.y, 0f), ps[i + 1] + V3(0f, y1 - a.y, 0f), ps[i + 1] + V3(0f, y1 - a.y + GeoCentro.BARANDA, 0f), ps[i] + V3(0f, y1 - a.y + GeoCentro.BARANDA, 0f))) ?: continue
                drawPath(f, v.c(ps[i], C.VIDRIO, lado * s, GaleriaCentro.lejos(ps[i])).copy(alpha = 0.2f))
            }
            trazo(v, ps.map { it + V3(0f, y1 - a.y + GeoCentro.BARANDA, 0f) }, lerp(C.VIDRIO, C.GRAMA, 0.25f), ARRIBA, 0.05f, 0.85f)
            trazo(v, ps.map { it + V3(0f, y1 - a.y + GeoCentro.BARANDA + 0.05f, 0f) }, lerp(C.ACERO, C.LATON, 0.15f), ARRIBA, 0.06f)
        }
        // Las luces de debajo de la pasarela: una fila de puntos.
        for (i in 1 until 8) {
            val p = a + (b - a) * (i / 8f) + V3(0f, y0 - a.y - 0.01f, 0f)
            val q = CamCentro.p(p); if (!CamCentro.enCuadro(q)) continue
            val k = CamCentro.k(p)
            val rr = (0.05f * k).coerceIn(0.35f, 1.2f)
            drawOval(lerp(C.LUZ_DIA, Color.White, 0.4f), q - androidx.compose.ui.geometry.Offset(rr, rr * 0.4f), androidx.compose.ui.geometry.Size(rr * 2f, rr * 0.8f))
            mancha(q, rr * 3f, rr * 2f, C.LUZ_DIA, 0.15f + 0.2f * v.noche)
        }
    }

    // ---------------------------------------------------------------- Las banderas de la Fundación

    /** Las guirnaldas de banderas de papel de la Fundación: de galería a galería, a través del vacío (grados de cada punta). */
    private val GUIRNALDAS = listOf(-66f to 146f, 12f to 170f, 52f to 204f)

    /**
     * La Fundación: banderas de papel con los colores de los departamentos colgadas de galería a galería (la de Botánica,
     * verde, y más que ninguna) **[Diseño propio]**: el cuento las cuelga entre los tanques de los jardines.
     */
    fun DrawScope.banderas(v: VistaCentro) {
        if (!CentroLab.banderas(v.m)) return
        val colores = GaleriaCentro.ALAS
        for ((i, ab) in GUIRNALDAS.withIndex()) {
            val a = GeoCentro.aro(GeoCentro.RV, GeoCentro.G2 + 0.9f, ab.first); val b = GeoCentro.aro(GeoCentro.RV, GeoCentro.G2 + 0.9f, ab.second)
            val n = 30; val caida = 2.2f - 0.3f * i
            val ps = (0..n).map { j -> val t = j / n.toFloat(); a + (b - a) * t - V3(0f, caida * 4f * t * (1f - t), 0f) }
            trazo(v, ps, lerp(C.TELA, C.TIERRA, 0.3f), ARRIBA, 0.015f, 0.8f)
            for (j in 1 until n) {
                val p = ps[j]; val q = CamCentro.p(p); if (!CamCentro.enCuadro(q, 0f)) continue
                val k = CamCentro.k(p)
                val col = if (j % 3 == 0) C.GRAMA else colores[(j + i) % colores.size]
                val w = (0.13f * k).coerceAtLeast(1f); val h = (0.24f * k).coerceAtLeast(1.6f)
                val f = io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono(q + androidx.compose.ui.geometry.Offset(-w, 0f), q + androidx.compose.ui.geometry.Offset(w, 0f), q + androidx.compose.ui.geometry.Offset(0f, h))
                drawPath(f, v.c(p, lerp(col, C.TELA, 0.15f), V3(0f, 0f, -1f), GaleriaCentro.lejos(p)))
            }
        }
    }

}
