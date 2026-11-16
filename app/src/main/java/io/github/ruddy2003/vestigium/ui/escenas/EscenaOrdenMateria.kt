package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.palo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar

/**
 * La materia de lo que estaba liso **[Diseño propio]**: las losas del tejadillo de la fragua y del suelo del adarve una por
 * una, la mampostería de los muretes, las manchas y desconchones de los muros, los parches de la Garza y las filas de
 * mesetas con calima. Todo en la parte quieta, sin capas.
 */
internal object MateriaOrden {
    private val C = ColorOrden
    private const val P = CamOrden.PATIO

    private fun en(a: V3, b: V3, t: Float) = V3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t)

    /** Un punto de un cuadrilátero [q] (bajo-delante, alto-delante, alto-detrás, bajo-detrás) en (u hacia arriba, w hacia atrás). */
    private fun bil(q: List<V3>, u: Float, w: Float): V3 = en(en(q[0], q[3], w), en(q[1], q[2], w), u)

    /**
     * Losas una por una sobre un faldón [q] (bajo-delante, alto-delante, alto-detrás, bajo-detrás): [filas] hiladas a lo
     * largo de la pendiente, de tonos distintos; alguna corrida, alguna rota; el hollín junto a [hollin] (de 0 a 1 en w).
     */
    fun DrawScope.losas(v: VistaOrden, q: List<V3>, n: V3, base: Color, filas: Int, porFila: Int, az: Azar, hollin: Float = -1f, lejos: Float = 0f) {
        val lz = v.lz
        for (f in 0 until filas) {
            val u0 = f / filas.toFloat(); val u1 = (f + 1) / filas.toFloat() + 0.02f
            var w = -az.entre(0f, 0.5f) / porFila
            while (w < 1f) {
                val ancho = az.entre(0.6f, 1.3f) / porFila; val corre = if (az.real() < 0.08f) az.entre(-0.03f, 0.03f) else 0f
                val pts = listOf(bil(q, u0 + corre, w), bil(q, u1 + corre, w), bil(q, u1 + corre, w + ancho - 0.004f), bil(q, u0 + corre, w + ancho - 0.004f)).map { CamOrden.p(it) }
                val sucio = if (hollin >= 0f) (1f - kotlin.math.abs(w + ancho / 2f - hollin) * 3f).coerceIn(0f, 1f) * u0 else 0f
                val tono = when (az.entero(5)) { 0 -> lerp(base, C.ARENA, 0.22f); 1 -> lerp(base, C.ROCA_HONDA, 0.2f); 2 -> lerp(base, C.MADERA, 0.15f); else -> base }
                val c = lerp(lz.color(tono, n, lejos), v.l.tono(C.HOLLIN, lejos), 0.55f * sucio)
                drawPath(poligono(pts), c)
                // El canto de abajo de cada losa, claro, y su sombra corta: así se leen una por una.
                drawLine(lerp(c, C.ARENA, 0.3f), pts[0], pts[3], 0.5f)
                drawLine(lerp(c, C.HOLLIN, 0.35f), pts[1], pts[2], 0.45f)
                if (az.real() < 0.05f) drawLine(lerp(c, C.HOLLIN, 0.5f), pts[0], pts[2], 0.4f)    // una rota
                w += ancho
            }
        }
    }

    /** El tejadillo de la fragua: sus losas, el hollín junto a la chimenea, la piedra que las sujeta y las vigas del alero. */
    fun DrawScope.tejadoDeLaFragua(v: VistaOrden, tejado: List<V3>) {
        if (v.nieve > 0f) return
        val n = V3(-0.4f, 1f, 0f); val az = PiezasOrden.fijo("tejado de la fragua")
        // tejado: bajo-delante, alto-delante, alto-detrás, bajo-detrás
        losas(v, tejado, n, C.LOSA, 6, 9, az, hollin = 0.85f, lejos = 0.1f)
        // La piedra gorda que sujeta las losas contra el viento.
        val c = CamOrden.p(bil(tejado, 0.5f, 0.35f)); val k = CamOrden.k(tejado[0].x, tejado[0].z)
        drawOval(v.lz.color(C.PIEDRA_OSC, V3(-0.3f, 1f, -0.5f), 0.1f), Offset(c.x - k * 0.35f, c.y - k * 0.22f), Size(k * 0.7f, k * 0.36f))
        // Las cabezas de las vigas bajo el alero.
        for (j in 1..6) { val a = en(tejado[0], tejado[3], j / 7f); palo(v.lz, V3(a.x + 0.35f, a.y - 0.12f, a.z), V3(a.x - 0.25f, a.y - 0.2f, a.z), 0.16f, C.MADERA_OSC, 0.1f) }
    }

    /**
     * La chimenea de la fragua: un tiro de piedra girado, con su cara de luz y su cara de sombra, que se estrecha hacia
     * arriba; el remate de losa con la boca negra de hollín y las chorreras que bajan por las dos caras. Más baja que el
     * adarve, para que no compita con la torre.
     */
    fun DrawScope.chimenea(v: VistaOrden, s: Float, vv: Float, pie: Float) {
        val lz = v.lz; val giro = 0.7f; val piedra = lerp(C.PIEDRA, C.HOLLIN, 0.1f)
        with(PiezasOrden) {
            caja(lz, s, vv, giro, 1.3f, 1.3f, pie, -3.2f, piedra, 0.1f, arriba = C.LOSA)
            caja(lz, s, vv, giro, 1f, 1f, -3.2f, -1.45f, piedra, 0.1f)
            caja(lz, s, vv, giro, 1.25f, 1.25f, -1.45f, -1.25f, lerp(C.LOSA, C.ARENA, 0.15f), 0.1f, arriba = lerp(C.HOLLIN, C.ROCA_HONDA, 0.25f))
        }
        val q = PiezasOrden.esquinas(s, vv, giro, 1.02f, 1.02f)
        for ((a, b) in listOf(q[0] to q[1], q[1] to q[2])) for (t in listOf(0.25f, 0.6f, 0.85f)) {
            val x = a.x + (b.x - a.x) * t; val z = a.y + (b.y - a.y) * t
            drawLine(v.l.tono(C.HOLLIN).copy(alpha = 0.5f), CamOrden.p(x, -1.5f, z), CamOrden.p(x, -2.1f - t * 0.9f, z), 0.9f, StrokeCap.Round)
        }
    }

    /** El suelo del adarve de cerca: las losas con sus juntas de arena, el centro gastado y la mancha de aceite bajo el candil. */
    fun DrawScope.sueloDelAdarve(v: VistaOrden) {
        val lz = v.lz; val az = PiezasOrden.fijo("losas de cerca"); val arriba = V3(0f, 1f, 0f)
        clipPath(AdarveOrden.formaDelSuelo()) {
            var vv = 2.4f; var fila = 0
            while (vv < 10.5f) {
                val largo = az.entre(0.55f, 0.8f); var s = CamOrden.BORDE + (if (fila % 2 == 0) 0f else -0.3f)
                while (s < CamOrden.PARAPETO) {
                    val ancho = az.entre(0.55f, 0.85f)
                    val q = listOf(V3(s, 0f, vv), V3(s + ancho, 0f, vv), V3(s + ancho, 0f, vv + largo), V3(s, 0f, vv + largo)).map { CamOrden.p(it) }
                    val tono = when (az.entero(4)) { 0 -> lerp(C.LOSA, C.ARENA, 0.3f); 1 -> lerp(C.LOSA, C.ROCA, 0.25f); else -> C.LOSA }
                    drawPath(poligono(q), lz.color(tono, arriba).copy(alpha = 0.4f))
                    drawPath(poligono(q), lz.color(lerp(C.ARENA, C.PAPEL, 0.3f), arriba).copy(alpha = 0.45f), style = Stroke(0.5f))
                    s += ancho
                }
                vv += largo; fila++
            }
            // El centro, gastado de tanto pasar: más claro y pulido.
            for (j in 0 until 5) { val c = CamOrden.p(0.75f, 0f, 3.4f + j * 1.4f); val k = CamOrden.k(0.75f, 3.4f + j * 1.4f); mancha(c, k * 0.55f, k * 0.5f, lz.color(lerp(C.LOSA, C.ARENA, 0.5f), arriba), 0.3f) }
            // La mancha de aceite bajo el candil del cubo.
            val o = CamOrden.p(1.45f, 0f, 7.35f); val k = CamOrden.k(1.45f, 7.35f)
            mancha(o, k * 0.45f, k * 0.18f, v.l.tono(lerp(C.HOLLIN, C.MADERA_OSC, 0.4f)), 0.35f)
        }
    }

    /** Mampostería pintada sobre una cara ya recortada: piedras de tamaños distintos en hiladas torcidas, de su tono. */
    fun DrawScope.mampuesto(forma: Path, tono: Color, az: Azar, tam: Float = 2.6f) {
        val r = forma.getBounds(); var y = r.top
        while (y < r.bottom) {
            val h = tam * az.entre(0.7f, 1.2f); var x = r.left - az.entre(0f, tam)
            while (x < r.right) {
                val w = tam * az.entre(1.1f, 2.4f)
                val c = when (az.entero(5)) { 0 -> lerp(tono, Color(0xFFFFE8D0), 0.14f); 1 -> lerp(tono, Color(0xFF4A2E3A), 0.16f); 2 -> lerp(tono, Color(0xFF8A3A2A), 0.12f); else -> tono }
                drawRect(c, Offset(x + 0.3f, y + 0.3f), Size(w - 0.6f, h - 0.6f))
                x += w
            }
            y += h
        }
    }

    /** Las manchas de un muro: desconchones claros, la chorrera oscura bajo la azotea y el liquen del lado de la sombra. */
    fun DrawScope.manchasDelMuro(v: VistaOrden, a: V3, b: V3, alto: Float, n: V3, az: Azar, lejos: Float) {
        val tono = v.lz.color(C.PIEDRA, n, lejos)
        repeat(3) { val t = az.entre(0.1f, 0.9f); val c = CamOrden.p(en(a, b, t).let { V3(it.x, it.y + alto * az.entre(0.2f, 0.7f), it.z) }); val k = CamOrden.k(a.x, a.z)
            mancha(c, k * az.entre(0.5f, 1.1f), k * az.entre(0.3f, 0.6f), lerp(tono, C.ARENA, 0.35f), 0.45f) }
        repeat(3) { val t = az.entre(0.1f, 0.9f); val arriba = en(a, b, t).let { V3(it.x, it.y + alto, it.z) }
            val q0 = CamOrden.p(arriba); val q1 = CamOrden.p(arriba.x, arriba.y - alto * az.entre(0.3f, 0.6f), arriba.z)
            drawLine(lerp(tono, C.HOLLIN, 0.25f).copy(alpha = 0.45f), q0, q1, (CamOrden.k(a.x, a.z) * 0.18f).coerceAtLeast(0.6f), StrokeCap.Round) }
    }

    /** La sombra del alero en lo alto de una cara de muro (de [a] a [b], arriba en [alto]). */
    fun DrawScope.sombraDelAlero(v: VistaOrden, a: V3, b: V3, alto: Float, n: V3) {
        if (!CamOrden.seVe(en(a, b, 0.5f).let { V3(it.x, alto - 0.3f, it.z) }, n)) return
        val q = listOf(V3(a.x, alto - 0.45f, a.z), V3(b.x, alto - 0.45f, b.z), V3(b.x, alto, b.z), V3(a.x, alto, a.z)).map { CamOrden.p(it) }
        drawPath(poligono(q), v.l.tono(lerp(C.ROCA_HONDA, C.HOLLIN, 0.35f)).copy(alpha = 0.4f * (1f - 0.6f * v.l.noche)))
    }

    /**
     * Los parches de la Garza, de formas y tamaños distintos y cosidos atravesados sobre dos paños, de otra tela (pardo,
     * azul desteñido, gris nuevo), con sus puntadas a la vista.
     */
    fun DrawScope.parches(v: VistaOrden, b: TorreOrden.Ballena, lejos: Float) {
        val az = PiezasOrden.fijo("parches de la garza")
        val telas = listOf(lerp(C.TELA, C.MADERA, 0.55f), lerp(C.TELA, Color(0xFF6F8FB0), 0.45f), C.TELA_NUEVA, lerp(C.TELA_VIEJA, C.ARENA, 0.35f))
        repeat(12) { k ->
            val t = az.entre(0.12f, 0.85f); val a = az.entre(-1.7f, 1.7f); val dt = az.entre(0.03f, 0.075f); val da = az.entre(0.18f, 0.42f); val g = az.entre(-0.5f, 0.5f)
            val n = b.radial(a); val c = b.punto(t, a)
            if (!CamOrden.seVe(c, n)) return@repeat
            val esquinas = listOf(-1f to -1f, 1f to -1f, 1f to 1f, -1f to 1f).map { (x, y) ->
                val tt = t + x * dt + y * dt * g * 0.6f; val aa = a + y * da + x * da * g * 0.5f + az.entre(-0.05f, 0.05f)
                b.punto(tt, aa) + b.radial(aa) * 0.05f
            }.map { CamOrden.p(it) }
            val forma = poligono(esquinas)
            val tono = v.lz.color(telas[k % telas.size], n, lejos)
            drawPath(forma, tono)
            drawPath(forma, lerp(tono, v.l.tono(C.HOLLIN), 0.35f), style = Stroke(0.45f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f, 0.6f))))
        }
    }

    /** Las filas de mesetas de detrás, más claras y azules cuanto más lejos, con su calima: el fondo con hondura. */
    fun DrawScope.mesetasLejanas(v: VistaOrden) {
        val h = CamOrden.H
        for ((fila, lista) in listOf(0 to listOf(150f to 60f, 250f to 80f, 35f to 55f), 1 to listOf(95f to 70f, 210f to 45f, 330f to 70f))) {
            val color = lerp(lerp(C.MESETA, v.l.bruma, 0.55f + 0.2f * fila), v.l.medio, 0.15f)
            for ((x, w) in lista) {
                val alto = 5f - fila * 1.6f; val y = h - 1f
                val forma = Path().apply {
                    moveTo(x - w / 2f, y); cubicTo(x - w / 2f + 4f, y - alto * 0.4f, x - w / 2f + 6f, y - alto, x - w / 2f + 12f, y - alto)
                    lineTo(x + w / 2f - 10f, y - alto); cubicTo(x + w / 2f - 5f, y - alto, x + w / 2f - 3f, y - alto * 0.4f, x + w / 2f, y); close()
                }
                drawPath(forma, color.copy(alpha = 0.85f))
            }
        }
    }
}
