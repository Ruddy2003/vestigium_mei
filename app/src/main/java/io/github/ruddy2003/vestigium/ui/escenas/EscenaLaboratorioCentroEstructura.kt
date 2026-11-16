package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.GaleriaCentro.cabinaDeVidrio
import io.github.ruddy2003.vestigium.ui.escenas.GaleriaCentro.carril
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.ABAJO
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.ARRIBA
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.cara
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.caraGrande
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.linea
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.trazo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
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

    // =================================================================
    //  Las costillas lejanas: detrás del canto de las losas, que vuelan por delante
    // =================================================================

    /** La cara de dentro de las costillas lejanas (justo detrás del canto de las losas), su fondo y su grueso. */
    private const val RL = GeoCentro.RV + 0.04f
    private const val FONDO_L = 0.9f
    private const val GRUESO_L = 0.5f

    /** Hasta dónde sube la parte recta de una costilla lejana, y cuánto baja por debajo de la bóveda la parte curva. */
    private val ALTO_RECTO: Float = GeoCentro.bovedaEn(RL) - 0.5f
    private const val BAJA = 0.5f

    private fun ejes(g: Float): Pair<V3, V3> { val a = GeoCentro.rad(g); return V3(cos(a), 0f, sin(a)) to V3(-sin(a), 0f, cos(a)) }

    /** Las costillas del otro lado (todas menos la de cerca). */
    private val LEJANAS: List<Float> get() = GeoCentro.COSTILLAS.drop(1)

    /**
     * El tramo de [y0] a [y1] de las costillas lejanas: lo pintan en su turno el comedor (bajo la primera galería), la primera
     * galería y la segunda, después de su pared y antes de su suelo y su canto. Así las losas pasan por delante, como bandejas
     * que vuelan, y las costillas quedan detrás, entre piso y piso, como el esqueleto del hall. En la segunda sigue hasta
     * donde se dobla por la bóveda.
     */
    fun DrawScope.costillasEntre(v: VistaCentro, y0: Float, y1: Float) {
        for (g in LEJANAS) {
            val (u, t) = ejes(g); val ht = GRUESO_L / 2f
            fun q(r: Float, s: Float, y: Float) = V3(u.x * r + t.x * s, y, u.z * r + t.z * s)
            val top = if (y1 > GeoCentro.G2) ALTO_RECTO else y1
            val lj = GaleriaCentro.lejos(q(RL, 0f, y0))
            val propio = lerp(C.HORMIGON, C.TIERRA, 0.1f)
            cara(v, listOf(q(RL, -ht, y0), q(RL, ht, y0), q(RL, ht, top), q(RL, -ht, top)), propio, u * -1f, lj)
            cara(v, listOf(q(RL, -ht, y0), q(RL + FONDO_L, -ht, y0), q(RL + FONDO_L, -ht, top), q(RL, -ht, top)), lerp(propio, C.SOMBRA, 0.15f), t * -1f, lj)
            cara(v, listOf(q(RL, ht, y0), q(RL + FONDO_L, ht, y0), q(RL + FONDO_L, ht, top), q(RL, ht, top)), lerp(propio, C.SOMBRA, 0.15f), t, lj)
            // Las juntas del encofrado y una chorrera bajo la losa.
            if (!CamCentro.seVe(q(RL, 0f, y0), u * -1f)) continue
            var y = y0 + 0.9f
            while (y < top - 0.2f) { linea(v, q(RL - 0.005f, -ht, y), q(RL - 0.005f, ht, y), lerp(C.SOMBRA, C.HORMIGON, 0.35f), u * -1f, 0.5f, 0.5f); y += 0.9f }
            val az = Azar(Semilla.de("laboratorio", "centro", "costilla", g.toInt(), y0.toInt()))
            trazo(v, listOf(q(RL - 0.006f, az.entre(-ht, ht) * 0.6f, top), q(RL - 0.006f, az.entre(-ht, ht) * 0.6f, top - az.entre(0.8f, 1.8f))), lerp(C.TIERRA, C.SOMBRA, 0.3f), u * -1f, 0.06f, 0.3f)
            // El filo de luz de la arista que da al vacío.
            val s = if (CamCentro.seVe(q(RL, ht, y0 + 0.5f), t)) ht else -ht
            trazo(v, listOf(q(RL, s, y0 + 0.05f), q(RL, s, top)), lerp(C.HORMIGON, Color.White, 0.45f), u * -1f + t * (s / ht), 0.03f, 0.55f)
        }
    }

    /** La parte curva de las costillas lejanas, por debajo de la bóveda hasta el aro (la pinta la bóveda). */
    fun DrawScope.costillasEnLaBoveda(v: VistaCentro) {
        val n = 8; val ht = GRUESO_L / 2f
        val propio = lerp(C.HORMIGON, C.TIERRA, 0.1f)
        for (g in LEJANAS) {
            val (u, t) = ejes(g)
            fun q(r: Float, s: Float, y: Float) = V3(u.x * r + t.x * s, y, u.z * r + t.z * s)
            val perfil = (0..n).map { i -> val r = RL + (GeoCentro.RO + 0.7f - RL) * i / n; r to GeoCentro.bovedaEn(r) }
            for (i in 0 until n) {
                val (ra, ya) = perfil[i]; val (rb, yb) = perfil[i + 1]
                val ya2 = if (i == 0) ALTO_RECTO else ya - BAJA; val yb2 = yb - BAJA
                val lj = GaleriaCentro.lejos(q(ra, 0f, ya))
                cara(v, listOf(q(ra, -ht, ya2), q(rb, -ht, yb2), q(rb, ht, yb2), q(ra, ht, ya2)), propio, V3(u.x * -0.4f, -1f, u.z * -0.4f), lj)
                cara(v, listOf(q(ra, -ht, ya2), q(rb, -ht, yb2), q(rb, -ht, yb), q(ra, -ht, ya)), propio, t * -1f, lj)
                cara(v, listOf(q(ra, ht, ya2), q(rb, ht, yb2), q(rb, ht, yb), q(ra, ht, ya)), propio, t, lj)
            }
        }
    }

    /** Los carriles de los elevadores, oscuros y pegados al canto de las losas junto a su costilla, y la cabina quieta. */
    fun DrawScope.elevadores(v: VistaCentro) {
        carril(v, GaleriaCentro.CABINA_QUIETA); carril(v, GaleriaCentro.CABINA_VIVA)
        cabinaDeVidrio(v, GaleriaCentro.CABINA_QUIETA, GeoCentro.G1 + 0.02f)
    }

    // =================================================================
    //  La costilla de cerca: el marco
    // =================================================================

    /** La costilla de cerca: su ángulo, el radio de su cara de dentro abajo y su grueso. */
    const val G_CERCA = -112f
    private const val R_CERCA = 7.5f
    private const val GRUESO_CERCA = 0.8f

    /** Cuánto se inclina hacia el vacío la costilla de cerca a la altura [y] (sube recta hasta la primera galería y luego se dobla). */
    fun inclina(y: Float): Float { val s = ((y - 3.3f) / 4.6f).coerceIn(0f, 1f); return 1.7f * s * s * (1.4f - 0.4f * s) }

    /** Cuánto fondo tiene a la altura [y]: se abre al subir, hacia la bóveda. */
    private fun fondo(y: Float): Float { val s = ((y - 4f) / 4f).coerceIn(0f, 1f); return 0.85f + 1.0f * s * s }

    /**
     * La costilla de cerca, a la izquierda, cortada por el borde: un gran cuerpo de hormigón que sube del comedor, atraviesa el
     * suelo de la galería, se dobla hacia el vacío y se abre arriba hacia la bóveda. Se arma por tramos de altura, cada uno con
     * su luz: el costado que da a quien mira, en la luz del vacío; la cara de fuera, en sombra.
     */
    fun DrawScope.costillaCerca(v: VistaCentro) {
        val (u, t) = ejes(G_CERCA); val ht = GRUESO_CERCA / 2f
        fun q(r: Float, s: Float, y: Float) = V3(u.x * r + t.x * s, y, u.z * r + t.z * s)
        val propio = lerp(C.HORMIGON, C.SOMBRA, 0.22f)
        val ys = listOf(0f, 1.2f, 2.4f, GeoCentro.G1) + (1..14).map { GeoCentro.G1 + 0.4f * it }
        for (i in 0 until ys.size - 1) {
            val y0 = ys[i]; val y1 = ys[i + 1]
            val a0 = R_CERCA - inclina(y0); val a1 = R_CERCA - inclina(y1)
            // Bajo la galería solo asoma lo que sale del canto.
            val b0 = if (y0 < GeoCentro.G1) GeoCentro.RV else a0 + fondo(y0); val b1 = if (y1 <= GeoCentro.G1) GeoCentro.RV else a1 + fondo(y1)
            val nDentro = V3(-u.x, (a0 - a1) / (y1 - y0).coerceAtLeast(0.01f), -u.z)
            val nFuera = V3(u.x, (b1 - b0) / (y1 - y0).coerceAtLeast(0.01f) * -1f, u.z)
            cara(v, listOf(q(a0, -ht, y0), q(a0, ht, y0), q(a1, ht, y1), q(a1, -ht, y1)), propio, nDentro)
            cara(v, listOf(q(b0, -ht, y0), q(b0, ht, y0), q(b1, ht, y1), q(b1, -ht, y1)), lerp(propio, C.SOMBRA, 0.12f), nFuera)
            // Los costados: más claros en la arista de dentro (la luz del vacío) que en la de fuera.
            for (sg in listOf(1f, -1f)) {
                val ps = listOf(q(a0, ht * sg, y0), q(b0, ht * sg, y0), q(b1, ht * sg, y1), q(a1, ht * sg, y1))
                if (!CamCentro.seVe(ps[0], t * sg)) continue
                val pa = q((a0 + a1) / 2f, ht * sg, (y0 + y1) / 2f); val pb = q((b0 + b1) / 2f, ht * sg, (y0 + y1) / 2f)
                caraGrande(v, ps, propio, t * sg, pa - u * 1.2f, pb + u * 1.0f, pasos = 2)
                // La sombra propia, que se hace honda hacia la arista de fuera.
                CamCentro.forma(ps)?.let { f -> drawPath(f, Brush.linearGradient(listOf(Color.Transparent, lerp(C.SOMBRA, C.NEGRO, 0.5f).copy(alpha = 0.5f)), CamCentro.p(pa), CamCentro.p(pb))) }
            }
        }
        materiaCerca(v, u, t, ys)
    }

    /** La materia de la costilla de cerca: juntas del encofrado que siguen su curva, chorreras, un remiendo, el filo de luz y la rejilla. */
    private fun DrawScope.materiaCerca(v: VistaCentro, u: V3, t: V3, ys: List<Float>) {
        val ht = GRUESO_CERCA / 2f
        fun q(r: Float, s: Float, y: Float) = V3(u.x * r + t.x * s, y, u.z * r + t.z * s)
        val s = if (CamCentro.seVe(q(R_CERCA, ht, 4.5f), t)) ht + 0.004f else -ht - 0.004f
        val n = t * (if (s > 0f) 1f else -1f)
        val junta = lerp(C.SOMBRA, C.HORMIGON, 0.35f)
        // Las juntas, de la arista de dentro a la de fuera, cada 0.6 m.
        var y = GeoCentro.G1 + 0.3f
        while (y < ys.last() - 0.1f) { val a = R_CERCA - inclina(y); linea(v, q(a, s, y), q(a + fondo(y), s, y), junta, n, 0.7f, 0.5f); y += 0.6f }
        // Dos juntas que siguen la curva.
        for (f in listOf(0.33f, 0.66f)) trazo(v, ys.filter { it >= GeoCentro.G1 }.map { yy -> val a = R_CERCA - inclina(yy); q(a + fondo(yy) * f, s, yy) }, junta, n, 0.012f, 0.45f)
        // Las chorreras bajo la jardinera de arriba y un remiendo de otra pintura.
        val az = Azar(Semilla.de("laboratorio", "centro", "costilla de cerca"))
        repeat(5) {
            val f = az.entre(0.1f, 0.8f); val y0 = ys.last() - az.entre(0f, 0.6f); val largo = az.entre(0.8f, 2.6f)
            trazo(v, (0..4).map { k -> val yy = y0 - largo * k / 4f; val a = R_CERCA - inclina(yy); q(a + fondo(yy) * (f + az.entre(-0.01f, 0.01f)), s, yy) }, lerp(C.TIERRA, C.SOMBRA, 0.35f), n, az.entre(0.02f, 0.05f), az.entre(0.18f, 0.35f))
        }
        val yr = 5.6f; val ar = R_CERCA - inclina(yr)
        cara(v, listOf(q(ar + 0.15f, s, yr), q(ar + 0.55f, s, yr + 0.05f), q(ar + 0.5f, s, yr + 0.5f), q(ar + 0.12f, s, yr + 0.42f)), lerp(C.TELA, C.HORMIGON, 0.4f), n, siempre = true)
        // El filo de luz de la arista de dentro, del lado que se ve.
        trazo(v, ys.filter { it >= GeoCentro.G1 }.map { yy -> q(R_CERCA - inclina(yy), s, yy) }, lerp(C.HORMIGON, Color.White, 0.55f), u * -1f, 0.04f, 0.7f)
        // La rejilla del aire, a la altura de los ojos.
        val yg = CamCentro.OJO_Y - 0.4f; val ag = R_CERCA - inclina(yg)
        cara(v, listOf(q(ag + 0.25f, s, yg), q(ag + 0.7f, s, yg), q(ag + 0.7f, s, yg + 0.3f), q(ag + 0.25f, s, yg + 0.3f)), lerp(C.ACERO, C.HORMIGON, 0.2f), n, siempre = true)
        for (i in 1..4) linea(v, q(ag + 0.27f, s, yg + i * 0.06f), q(ag + 0.68f, s, yg + i * 0.06f), lerp(C.ACERO, C.NEGRO, 0.35f), n, 0.7f)
    }

    // ---------------------------------------------------------------- La pasarela

    /** La pasarela: la losa fina vista por debajo, su canto, las dos barandillas de vidrio y su pasamanos. */
    fun DrawScope.pasarela(v: VistaCentro) {
        val a = GeoCentro.PASA0; val b = GeoCentro.PASA1
        val dir = (b - a).unidad(); val lado = V3(-dir.z, 0f, dir.x) * (GeoCentro.PASA_ANCHO / 2f)
        val y1 = GeoCentro.G2; val y0 = y1 - 0.48f
        val propio = lerp(C.HORMIGON, C.TELA, 0.2f)
        val n = 16
        for (i in 0 until n) {
            val p = a + (b - a) * (i / n.toFloat()); val q = a + (b - a) * ((i + 1) / n.toFloat())
            fun w(c: V3, s: Float, y: Float) = V3(c.x + lado.x * s, y, c.z + lado.z * s)
            val lj = GaleriaCentro.lejos(p)
            cara(v, listOf(w(p, -1f, y0), w(q, -1f, y0), w(q, 1f, y0), w(p, 1f, y0)), lerp(propio, C.SOMBRA, 0.3f), ABAJO, lj)
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
            trazo(v, ps.map { it + V3(0f, y1 - a.y + GeoCentro.BARANDA, 0f) }, lerp(C.VIDRIO, C.GRAMA, 0.35f), ARRIBA, 0.04f, 0.6f)
            trazo(v, ps.map { it + V3(0f, y1 - a.y + GeoCentro.BARANDA + 0.05f, 0f) }, lerp(C.ACERO, C.NEGRO, 0.25f), lado * s, 0.07f)
            // Los montantes de la barandilla.
            for (i in 0..n step 2) linea(v, ps[i] + V3(0f, y1 - a.y, 0f), ps[i] + V3(0f, y1 - a.y + GeoCentro.BARANDA, 0f), lerp(C.ACERO, C.NEGRO, 0.3f), lado * s, 0.6f, 0.8f)
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
    private class Guirnalda(val a0: Float, val a1: Float, val y: Float, val caida: Float, val tam: Float, val verde: Int)
    private val GUIRNALDAS = listOf(
        Guirnalda(-66f, 146f, GeoCentro.G2 + 0.9f, 2.2f, 1f, 3), Guirnalda(12f, 170f, GeoCentro.G2 + 0.9f, 1.9f, 1f, 3),
        Guirnalda(52f, 204f, GeoCentro.G2 + 0.9f, 1.6f, 1f, 3),
        Guirnalda(-120f, 22f, GeoCentro.G2 + 0.7f, 1.7f, 1.3f, 3), Guirnalda(-130f, -38f, GeoCentro.G2 + 0.5f, 1.3f, 1.5f, 2),
    )

    /**
     * La Fundación: banderas de papel con los colores de los departamentos colgadas de galería a galería (la de Botánica,
     * verde, y más que ninguna) **[Diseño propio]**: el cuento las cuelga entre los tanques de los jardines.
     */
    fun DrawScope.banderas(v: VistaCentro) {
        if (!CentroLab.banderas(v.m)) return
        val colores = GaleriaCentro.ALAS
        for ((i, gu) in GUIRNALDAS.withIndex()) {
            val a = GeoCentro.aro(GeoCentro.RV, gu.y, gu.a0); val b = GeoCentro.aro(GeoCentro.RV, gu.y, gu.a1)
            val n = 30; val caida = gu.caida
            val ps = (0..n).map { j -> val t = j / n.toFloat(); a + (b - a) * t - V3(0f, caida * 4f * t * (1f - t), 0f) }
            trazo(v, ps, lerp(C.TELA, C.TIERRA, 0.3f), ARRIBA, 0.015f, 0.8f)
            for (j in 1 until n) {
                val p = ps[j]; val q = CamCentro.p(p); if (!CamCentro.enCuadro(q, 0f)) continue
                val k = CamCentro.k(p)
                val col = if (j % gu.verde == 0) C.GRAMA else colores[(j + i) % colores.size]
                val w = (0.13f * gu.tam * k).coerceAtLeast(1f); val h = (0.24f * gu.tam * k).coerceAtLeast(1.6f)
                val f = io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono(q + androidx.compose.ui.geometry.Offset(-w, 0f), q + androidx.compose.ui.geometry.Offset(w, 0f), q + androidx.compose.ui.geometry.Offset(0f, h))
                drawPath(f, lerp(v.c(p, lerp(col, C.TELA, 0.15f), V3(0f, 0f, -1f), GaleriaCentro.lejos(p)), col, 0.3f))
            }
        }
    }

}
