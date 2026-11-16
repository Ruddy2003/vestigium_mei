package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Jardines
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.caja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.linea
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El verde del hall, sin árboles («los pasillos tienen nombres de árboles que nadie ha visto»): **cortinas de judías** que
 * trepan por hilos desde jardineras hasta las galerías, **tomateras** en macetones junto al mostrador y bandejas de
 * **lechuga** en los antepechos. Lo cuida Botánica **[Diseño propio]** y sigue el arco `jardines`, como los tanques del
 * cuento: tras la siega, los hilos casi desnudos con una pelusa de brotes; luego las judías «hasta la mitad de los hilos»;
 * llenos, hasta arriba, con vainas, flores y «los tomates se doblan de rojo».
 */
internal object VerdeCentro {
    private val C = ColorLaboratorio

    /** Hasta dónde ha subido el verde por los hilos, de 0 a 1, según el tramo de los jardines. */
    fun subida(j: Jardines): Float = when (j) { Jardines.VACIOS -> 0f; Jardines.BROTES -> 0.12f; Jardines.MEDIO -> 0.5f; Jardines.LLENOS -> 1f }

    /** Las columnas de judías al otro lado del vacío: (grados, desde, hasta) en metros de alto, desde una jardinera. */
    private val COLUMNAS = listOf(
        Triple(52f, 0f, GeoCentro.G1 - GeoCentro.LOSA), Triple(96f, 0f, GeoCentro.G1 - GeoCentro.LOSA), Triple(138f, 0f, GeoCentro.G1 - GeoCentro.LOSA), Triple(178f, 0f, GeoCentro.G1 - GeoCentro.LOSA),
        Triple(62f, GeoCentro.G1, GeoCentro.G2 - GeoCentro.LOSA), Triple(124f, GeoCentro.G1, GeoCentro.G2 - GeoCentro.LOSA), Triple(160f, GeoCentro.G1, GeoCentro.G2 - GeoCentro.LOSA),
    )

    fun DrawScope.columnas(v: VistaCentro) {
        val sube = subida(v.jardines)
        for ((g, y0, y1) in COLUMNAS) columna(v, g, y0, y1, sube)
        tomateras(v)
        lechugas(v)
    }

    /** Una columna de cultivo lejana: la jardinera, los hilos tensos y las judías que suben por ellos, en masa, con hojas claras en el borde. */
    private fun DrawScope.columna(v: VistaCentro, g: Float, y0: Float, y1: Float, sube: Float) {
        val r = if (y0 < 0.1f) GeoCentro.RV - 0.55f else GeoCentro.RV + 0.25f
        val a = GeoCentro.rad(g); val t = V3(-sin(a), 0f, cos(a))
        val pie = GeoCentro.aro(r, y0, g)
        if (!CamCentro.enCuadro(CamCentro.p(pie), 20f) && !CamCentro.enCuadro(CamCentro.p(GeoCentro.aro(r, y1, g)), 20f)) return
        val lj = GaleriaCentro.lejos(pie)
        caja(v, pie, t, 1.2f, 0.45f, 0.4f, lerp(C.HORMIGON, C.TIERRA, 0.3f), lj, tapa = lerp(C.TIERRA, C.NEGRO, 0.25f))
        val az = Azar(Semilla.de("laboratorio", "centro", "columna", g.toInt()))
        val hilo = lerp(C.TELA, C.TIERRA, 0.3f)
        val n = 6
        val k = CamCentro.k(pie)
        for (i in 0 until n) {
            val s = -0.5f + i / (n - 1f)
            val b = pie + t * s + V3(0f, 0.4f, 0f); val c = GeoCentro.aro(r, y1, g) + t * (s * 0.8f)
            linea(v, b, c, hilo, V3(-cos(a), 0f, -sin(a)), 0.4f, 0.6f)
            if (sube <= 0f) continue
            // Las hojas: de abajo arriba hasta donde ha subido, masa oscura y hojas claras.
            val alto = (c.y - b.y) * sube
            var h = 0f
            while (h < alto) {
                val w = b + (c - b) * (h / (c.y - b.y))
                val q = CamCentro.p(w) + Offset(az.entre(-1.5f, 1.5f), 0f)
                val rr = (0.11f * k).coerceIn(0.8f, 4f)
                mancha(q, rr * 1.3f, rr, v.c(w, C.HOJA_OSC, V3(-cos(a), 0.2f, -sin(a)), lj), 0.9f)
                if (az.real() > 0.45f) mancha(q + Offset(az.entre(-1f, 1f) * rr, -rr * 0.3f), rr * 0.7f, rr * 0.5f, v.c(w, C.GRAMA, V3(-cos(a), 0.4f, -sin(a)), lj), 0.95f)
                if (v.jardines == Jardines.LLENOS && az.real() > 0.85f) drawCircle(v.c(w, C.TELA, V3(-cos(a), 0.4f, -sin(a)), lj), (0.03f * k).coerceAtLeast(0.4f), q + Offset(rr * 0.4f, 0f))
                h += 0.2f + az.entre(0f, 0.12f)
            }
        }
    }

    /** Las tomateras, en macetones junto al mostrador: matas oscuras con hojas claras y, llenos, los tomates rojos. */
    private fun DrawScope.tomateras(v: VistaCentro) {
        val az = Azar(Semilla.de("laboratorio", "centro", "tomateras"))
        val sube = subida(v.jardines)
        for (g in listOf(126f, 170f, 30f)) {
            val c = GeoCentro.aro(GeoCentro.RV - 0.45f, 0f, g)
            if (!CamCentro.enCuadro(CamCentro.p(c), 10f)) continue
            val lj = GaleriaCentro.lejos(c)
            PiezasCentro.run { cilindro(v, c, 0.38f, 0.5f, lerp(C.BARRO, C.HORMIGON, 0.3f), lerp(C.TIERRA, C.NEGRO, 0.3f), rArriba = 0.42f) }
            if (sube <= 0f) continue
            val k = CamCentro.k(c); val alto = 0.3f + 1.3f * sube
            for (j in 0 until 14) {
                val w = c + V3(az.entre(-0.4f, 0.4f), 0.5f + az.entre(0f, alto), az.entre(-0.4f, 0.4f))
                mancha(CamCentro.p(w), 0.2f * k, 0.15f * k, v.c(w, if (j % 3 == 0) C.GRAMA else C.HOJA_OSC, V3(0f, 0.3f, -1f), lj), 0.9f)
            }
            if (v.jardines == Jardines.LLENOS) repeat(7) {
                val w = c + V3(az.entre(-0.35f, 0.35f), 0.6f + az.entre(0f, alto * 0.8f), az.entre(-0.35f, 0.35f))
                drawCircle(v.c(w, C.TOMATE, V3(0f, 0.3f, -1f), lj), (0.06f * k).coerceAtLeast(0.5f), CamCentro.p(w))
            }
        }
    }

    /** Las bandejas de lechuga colgadas en el antepecho de la primera galería, del lado del vacío. */
    private fun DrawScope.lechugas(v: VistaCentro) {
        val sube = subida(v.jardines); if (sube <= 0f) return
        var g = 45f
        while (g < 175f) {
            val w = GeoCentro.aro(GeoCentro.RV - 0.18f, GeoCentro.G1 + 0.6f, g)
            val q = CamCentro.p(w); val k = CamCentro.k(w)
            if (CamCentro.enCuadro(q, 5f)) {
                val a = GeoCentro.rad(g)
                caja(v, w, V3(-sin(a), 0f, cos(a)), 0.9f, 0.22f, 0.14f, lerp(C.ACERO, C.HORMIGON, 0.3f), GaleriaCentro.lejos(w))
                for (i in 0..3) {
                    val p = CamCentro.p(w + V3(-sin(a), 0f, cos(a)) * (-0.33f + 0.22f * i) + V3(0f, 0.17f, 0f))
                    mancha(p, 0.12f * k * (0.4f + 0.6f * sube), 0.07f * k * (0.4f + 0.6f * sube), v.c(w, lerp(C.GRAMA, C.PAJA, 0.15f), V3(0f, 1f, -1f), GaleriaCentro.lejos(w)), 0.95f)
                }
            }
            g += 9f
        }
    }

    // =================================================================
    //  La cortina de cerca
    // =================================================================

    /** Los hilos de la cortina de cerca, que cuelgan de la jardinera del borde de la segunda galería, junto a la costilla. */
    private val HILOS = listOf(-109.6f, -108.7f, -107.9f, -107.0f, -106.2f)
    private const val R_CORTINA = 7.62f

    /**
     * La cortina de judías de cerca, que cuelga de la jardinera del borde de la galería de arriba, junto a la costilla: las
     * guías bajan por sus hilos tanto como han crecido (tras la siega, casi nada); las hojas una por una (de tres en tres,
     * como las de la judía), oscuras por dentro y claras en el borde que da al vacío; vainas y flores cuando está llena.
     */
    fun DrawScope.cortina(v: VistaCentro) {
        val sube = subida(v.jardines)
        val az = Azar(Semilla.de("laboratorio", "centro", "cortina"))
        val arriba = GeoCentro.G2 - 0.05f
        // La jardinera colgada del canto de la losa: un cajón de hormigón con tierra.
        val a = GeoCentro.rad(-107.9f)
        caja(v, GeoCentro.aro(R_CORTINA - 0.05f, arriba - 0.35f, -107.9f), V3(-sin(a), 0f, cos(a)), 1.1f, 0.32f, 0.35f, lerp(C.HORMIGON, C.TIERRA, 0.25f), tapa = lerp(C.TIERRA, C.NEGRO, 0.3f))
        val hilo = lerp(C.TELA, C.TIERRA, 0.25f)
        val abajo = GeoCentro.G1 + 1.45f
        for (g in HILOS) linea(v, GeoCentro.aro(R_CORTINA, arriba - 0.35f, g), GeoCentro.aro(R_CORTINA + 0.02f, abajo, g), hilo, V3(0f, 0f, -1f), 0.6f, 0.8f)
        if (sube <= 0f) return
        val hasta = arriba - 0.35f - (arriba - 0.35f - abajo) * sube
        // Primero la masa de detrás (hojas oscuras), luego las de delante, claras donde les da el hall.
        for (pasada in 0..1) for ((i, g) in HILOS.withIndex()) {
            var y = arriba - 0.42f - az.entre(0f, 0.1f)
            val fin = hasta + az.entre(-0.25f, 0.25f) * sube
            while (y > fin) {
                val w = GeoCentro.aro(R_CORTINA + az.entre(-0.06f, 0.04f), y, g + az.entre(-0.35f, 0.35f))
                val ang = 180f + az.entre(-70f, 70f) + (if (i % 2 == 0) 25f else -25f)
                val delante = az.real() > 0.45f
                if ((pasada == 0) != delante) hoja(v, w, ang, az.entre(0.75f, 1.15f), delante)
                if (pasada == 1 && v.jardines == Jardines.LLENOS && az.real() > 0.8f) vaina(v, w, az)
                if (pasada == 1 && v.jardines == Jardines.LLENOS && az.real() > 0.88f) flor(v, w + V3(0f, -0.04f, 0f))
                y -= 0.11f + az.entre(0f, 0.08f)
            }
        }
    }

    /** Una hoja de judía: tres folíolos en punta desde un rabillo, oscura o clara según de qué lado de la mata está. */
    private fun DrawScope.hoja(v: VistaCentro, w: V3, angulo: Float, tam: Float, delante: Boolean) {
        val q = CamCentro.p(w); val k = CamCentro.k(w) * 0.085f * tam
        val n = V3(0.2f, 0.25f, 1f)
        val col = if (delante) v.c(w, lerp(C.GRAMA, C.PAJA, 0.12f), V3(0.6f, 0.4f, 0.8f)) else v.c(w, C.HOJA_OSC, n)
        val borde = lerp(col, Color.White, 0.12f)
        rotate(angulo, q) {
            for ((dx, dy, rot) in listOf(Triple(0f, -1.15f, 0f), Triple(-0.85f, -0.35f, -55f), Triple(0.85f, -0.35f, 55f))) {
                val c = q + Offset(dx * k, dy * k)
                rotate(rot, c) { drawPath(foliolo(c, k * 0.62f), col) }
            }
            drawLine(lerp(col, C.NEGRO, 0.2f), q, q + Offset(0f, -0.9f * k), (0.08f * k).coerceAtLeast(0.3f), StrokeCap.Round)
            if (delante) drawLine(borde.copy(alpha = 0.6f), q + Offset(-0.1f * k, -1.6f * k), q + Offset(-0.05f * k, -0.7f * k), (0.06f * k).coerceAtLeast(0.25f))
        }
    }

    /** Un folíolo: una hoja ovalada que acaba en punta, centrada en [c], de largo 2 × [r]. */
    private fun foliolo(c: Offset, r: Float): Path = Path().apply {
        moveTo(c.x, c.y + r)
        cubicTo(c.x - r * 0.9f, c.y + r * 0.6f, c.x - r * 0.7f, c.y - r * 0.7f, c.x, c.y - r * 1.15f)
        cubicTo(c.x + r * 0.7f, c.y - r * 0.7f, c.x + r * 0.9f, c.y + r * 0.6f, c.x, c.y + r)
        close()
    }

    /** Una vaina colgando, algo curva. */
    private fun DrawScope.vaina(v: VistaCentro, w: V3, az: Azar) {
        val q = CamCentro.p(w); val k = CamCentro.k(w)
        val largo = 0.12f * k; val lado = az.entre(-0.3f, 0.3f) * largo
        drawPath(Path().apply { moveTo(q.x, q.y); cubicTo(q.x + lado, q.y + largo * 0.4f, q.x + lado, q.y + largo * 0.8f, q.x + lado * 0.4f, q.y + largo) },
            v.c(w, lerp(C.GRAMA, C.PAJA, 0.3f), V3(0.5f, 0.3f, 1f)), style = Stroke((0.012f * k).coerceAtLeast(0.6f), cap = StrokeCap.Round))
    }

    /** Una flor de judía, blanca con un toque violeta. */
    private fun DrawScope.flor(v: VistaCentro, w: V3) {
        val q = CamCentro.p(w); val k = CamCentro.k(w)
        drawCircle(v.c(w, C.TELA, V3(0.5f, 0.3f, 1f)), (0.014f * k).coerceAtLeast(0.5f), q)
        drawCircle(v.c(w, lerp(C.VIOLETA, C.TELA, 0.4f), V3(0.5f, 0.3f, 1f)), (0.007f * k).coerceAtLeast(0.3f), q + Offset(0.006f * k, -0.004f * k))
    }
}
