package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAntiguas.linterna
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.fijo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.borron
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.pts
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraDeFollaje
import io.github.ruddy2003.vestigium.vidas.Azar
import kotlin.math.cos
import kotlin.math.sin

/**
 * El pino viejo de la posada, el marco de la izquierda y de arriba: «más alto que el tejado y más viejo que el Gremio…
 * De sus ramas bajas cuelgan las linternas de los que vuelven tarde». Tronco grande, cortado por el borde izquierdo y por
 * abajo, de corteza gris y en placas abajo y rojiza arriba, con su resina y un clavo con un cabo; dos ramas grandes que
 * entran por arriba con sus copas en masas tendidas (oscuras dentro, con las agujas en haces y la luz en el borde del
 * lado del sol) y sus piñas; de la de abajo cuelgan las linternas, cada una distinta. Desde el rayo de 2031, «partido por
 * el rayo, una mitad verde y la otra negra, de pie».
 */
internal object PinoAntiguas {
    private val C = ColorAntiguas

    /** El tronco: su borde izquierdo y el derecho, de abajo arriba. */
    private val IZQ = pts(-16f, 292f, -6f, 262f, 0f, 220f, 4f, 170f, 6f, 120f, 8f, 70f, 10f, 30f, 12f, -6f)
    private val DER = pts(52f, 292f, 40f, 266f, 36f, 226f, 33f, 176f, 31f, 126f, 31f, 80f, 33f, 40f, 36f, -6f)

    /** La forma del tronco (para su sombra y su luz). */
    val TRONCO: Path by lazy { poligono(IZQ + DER.reversed()) }

    /** Un punto del tronco a la fracción [f] de su ancho (0 el borde izquierdo, 1 el derecho) y a la altura [y]. */
    private fun xEn(f: Float, y: Float): Float {
        val a = TrazosValle.alturaEn(IZQ.map { Offset(it.y, it.x) }.sortedBy { it.x }, y)
        val b = TrazosValle.alturaEn(DER.map { Offset(it.y, it.x) }.sortedBy { it.x }, y)
        return a + (b - a) * f
    }

    /** Dónde pisa el tronco. */
    val PIE = Offset(18f, 288f)

    /** Una rama grande: su eje (del tronco hacia fuera) y sus copas (centro y medio ancho), y si está del lado que mató el rayo. */
    private class Rama(val eje: List<Offset>, val grueso: Float, val copas: List<Pair<Offset, Float>>, val muerta: Boolean)

    private val RAMAS = listOf(
        Rama(pts(10f, 44f, -4f, 36f, -18f, 30f), 6f, listOf(Offset(-6f, 24f) to 20f), false),
        Rama(pts(30f, 30f, 56f, 16f, 92f, 9f, 128f, 11f, 160f, 19f, 182f, 31f), 8f, listOf(Offset(48f, 8f) to 24f, Offset(86f, 0f) to 30f, Offset(124f, 3f) to 25f, Offset(156f, 13f) to 20f, Offset(180f, 25f) to 13f, Offset(104f, 20f) to 12f), false),
        Rama(pts(32f, 70f, 64f, 73f, 100f, 81f, 138f, 84f, 168f, 77f, 188f, 69f), 7f, listOf(Offset(56f, 62f) to 20f, Offset(90f, 69f) to 24f, Offset(124f, 72f) to 19f, Offset(156f, 70f) to 20f, Offset(184f, 62f) to 13f, Offset(74f, 84f) to 10f, Offset(140f, 92f) to 9f), true),
        Rama(pts(32f, 112f, 46f, 116f, 60f, 111f), 3.4f, emptyList(), true),
    )

    /** Las linternas de la rama de abajo: de dónde cuelgan, el largo del cordel y su tamaño. */
    private val LINTERNAS = listOf(Triple(Offset(80f, 77f), 12f, 1.25f), Triple(Offset(114f, 84f), 20f, 1.1f), Triple(Offset(148f, 83f), 10f, 1.2f), Triple(Offset(174f, 74f), 18f, 1f), Triple(Offset(46f, 72f), 28f, 1.05f))

    fun DrawScope.pino(v: VistaAntiguas) {
        val az = fijo("pino")
        val partido = EscenaAntiguas.pinoPartido(v.m)
        // La masa del fondo de la copa, que sale por arriba: oscura, con huecos de cielo.
        masaDelFondo(v, az, partido)
        for (r in RAMAS) rama(v, r, partido)
        tronco(v, az, partido)
        for (r in RAMAS) for ((c, w) in r.copas) copa(v, c, w, az, partido && r.muerta)
        linternas(v)
    }

    /** El pincel de un punto del pino: el de la vista, con la sombra grande si le llega. */
    private fun pincel(v: VistaAntiguas, q: Offset): Pincel = v.pincelEn(q)

    private fun DrawScope.masaDelFondo(v: VistaAntiguas, az: Azar, partido: Boolean) {
        val pe = pincel(v, Offset(80f, 10f))
        val oscuro = pe.sombra(lerp(C.PINO, C.ESCORIA, 0.35f))
        for ((c, w) in listOf(Offset(14f, -8f) to 40f, Offset(76f, -14f) to 46f, Offset(130f, -12f) to 36f)) {
            if (partido && c.x > 60f) continue
            drawPath(borron(c, w, 20f, az, 15, 0.35f, 0.6f), oscuro)
        }
    }

    /** Una rama: de gruesa a fina, con su lado de luz; muerta (negra y pelada) si el rayo partió el pino. */
    private fun DrawScope.rama(v: VistaAntiguas, r: Rama, partido: Boolean) {
        val pe = pincel(v, r.eje[r.eje.size / 2])
        val negra = partido && r.muerta
        val col = if (negra) pe.medio(C.QUEMADO) else pe.por(lerp(C.MADERA, C.ROJIZA, 0.3f), 0.1f)
        val eje = suave(r.eje, 6)
        // La rama, de gruesa a fina, con su lado de abajo en sombra y un filo de luz arriba.
        val n = eje.size
        val arriba = eje.mapIndexed { i, q -> q + Offset(0f, -r.grueso * 0.5f * (1f - 0.75f * i / n)) }
        val abajo = eje.mapIndexed { i, q -> q + Offset(0f, r.grueso * 0.5f * (1f - 0.75f * i / n)) }
        val forma = poligono(arriba + abajo.reversed())
        drawPath(forma, col)
        drawPath(curva(abajo), pe.sombra(C.MADERA_OSC).copy(alpha = 0.8f), style = Stroke(r.grueso * 0.35f, cap = StrokeCap.Round))
        if (!negra) drawPath(curva(arriba.map { it + Offset(0f, 0.8f) }), pe.luz(lerp(C.MADERA, C.ORO, 0.4f)).copy(alpha = 0.5f), style = Stroke(0.8f, cap = StrokeCap.Round))
        // Las ramillas que salen hacia las copas (muertas, sueltas y quebradas).
        for ((c, _) in r.copas) {
            val i = eje.indices.minByOrNull { (eje[it] - c).getDistance() } ?: continue
            val base = eje[i]
            drawPath(Path().apply { moveTo(base); cubicTo(base + Offset((c.x - base.x) * 0.3f, -2f), c + Offset(-(c.x - base.x) * 0.3f, 2f), c) }, col.copy(alpha = 0.9f), style = Stroke(maxOf(1f, r.grueso * 0.28f), cap = StrokeCap.Round))
        }
        if (v.nieveAbajo > 0.2f && !negra) drawPath(curva(arriba.map { it + Offset(0f, -0.6f) }), pe.por(C.NIEVE, 0.5f).copy(alpha = v.nieveAbajo), style = Stroke(1.8f, cap = StrokeCap.Round))
    }

    /** Puntos de una curva suave (Catmull-Rom) por [puntos], [k] por tramo. */
    private fun suave(puntos: List<Offset>, k: Int): List<Offset> = buildList {
        for (i in 0 until puntos.size - 1) {
            val a = puntos[maxOf(i - 1, 0)]; val b = puntos[i]; val c = puntos[i + 1]; val d = puntos[minOf(i + 2, puntos.size - 1)]
            for (j in 0 until k) { val t = j / k.toFloat(); val t2 = t * t; val t3 = t2 * t
                add((b * 2f + (c - a) * t + (a * 2f - b * 5f + c * 4f - d) * t2 + (b * 3f - a - c * 3f + d) * t3) * 0.5f) }
        }
        add(puntos.last())
    }

    /**
     * El tronco: la corteza gris en placas abajo y rojiza arriba, con su lado de luz; los surcos; la resina; el clavo con su
     * cabo. Partido: la mitad derecha negra y quemada y, en la raja, la madera clara.
     */
    private fun DrawScope.tronco(v: VistaAntiguas, az: Azar, partido: Boolean) {
        val pe = pincel(v, Offset(20f, 160f))
        val gris = lerp(C.PIEDRA_OSC, C.MADERA, 0.45f); val rojiza = lerp(C.MADERA, C.ROJIZA, 0.55f)
        val lado = if (pe.solALaDerecha) 1f else -1f
        drawPath(TRONCO, Brush.verticalGradient(0f to pe.por(rojiza, 0.1f), 0.45f to pe.por(lerp(rojiza, gris, 0.6f), 0f), 1f to pe.por(gris, -0.1f), startY = 0f, endY = 290f))
        clipPath(TRONCO) {
            // El lado de la luz y el de la sombra.
            drawRect(Brush.horizontalGradient(listOf(pe.sombra(gris).copy(alpha = if (lado > 0f) 0.6f else 0f), Color.Transparent, pe.luz(rojiza).copy(alpha = if (lado > 0f) 0.4f else 0f)), -16f, 52f), Offset(-16f, 0f), Size(68f, 292f))
            drawRect(Brush.horizontalGradient(listOf(pe.luz(rojiza).copy(alpha = if (lado < 0f) 0.4f else 0f), Color.Transparent, pe.sombra(gris).copy(alpha = if (lado < 0f) 0.6f else 0f)), -16f, 52f), Offset(-16f, 0f), Size(68f, 292f))
            // Las placas de la corteza: largas y verticales, separadas por surcos hondos que siguen el tronco; abajo,
            // gruesas y grises; arriba, finas, rojizas y descamadas.
            for (k in 0 until 16) {
                val f = (k + az.entre(0.1f, 0.9f)) / 16f
                var y = 292f
                while (y > -6f) {
                    val largo = az.entre(14f, 30f) * (0.5f + 0.5f * y / 292f)
                    val x0 = xEn(f, y); val x1 = xEn(f, y - largo)
                    val w = 3.6f * (0.6f + 0.4f * y / 292f)
                    val tono = if (y < 130f) lerp(rojiza, C.ORO, az.entre(0f, 0.25f)) else lerp(gris, rojiza, az.entre(0f, 0.3f))
                    drawPath(poligono(Offset(x0 + 0.5f, y - 1f), Offset(x0 + w, y - 2f), Offset(x1 + w - 0.4f, y - largo + 2f), Offset(x1 + 0.6f, y - largo + 1f)), pe.por(tono, az.entre(-0.3f, 0.4f) + (f - 0.5f) * lado * 0.8f).copy(alpha = 0.6f))
                    drawLine(pe.sombra(C.ESCORIA).copy(alpha = 0.6f), Offset(x0, y), Offset(x1, y - largo), 1f, StrokeCap.Round)
                    y -= largo + az.entre(1f, 4f)
                }
            }
            // La resina, en lágrimas ámbar, y el clavo con su cabo.
            repeat(5) { val q = Offset(az.entre(6f, 30f), az.entre(60f, 230f)); drawOval(pe.luz(lerp(C.ORO, C.LANA, 0.3f)).copy(alpha = 0.8f), Offset(q.x - 0.8f, q.y - 1.6f), Size(1.6f, 3.4f)) }
            if (partido) {
                val raja = poligono(pts(14f, -6f, 22f, 60f, 20f, 140f, 24f, 210f, 22f, 230f, 26f, 210f, 27f, 140f, 29f, 60f, 26f, -6f))
                drawRect(pe.medio(C.QUEMADO), Offset(24f, -6f), Size(30f, 240f))
                // El carbón: la corteza quemada en escamas, con su brillo frío.
                var yy = 0f
                while (yy < 232f) { var xx = 25f; while (xx < 52f) { val w = az.entre(3f, 6f); drawLine(pe.luz(lerp(C.QUEMADO, C.ROCA, 0.5f)).copy(alpha = 0.35f), Offset(xx, yy), Offset(xx + w * 0.8f, yy + az.entre(-0.5f, 0.5f)), 0.7f); xx += w }; yy += az.entre(3f, 6f) }
                drawLine(pe.medio(C.ESCORIA), Offset(26f, -6f), Offset(28f, 232f), 1.4f)
                drawPath(raja, pe.por(C.FRESNO, 0.3f))
                drawPath(curva(pts(18f, -6f, 23f, 80f, 22f, 200f)), pe.medio(C.ESCORIA).copy(alpha = 0.8f), style = Stroke(1.2f))
            }
        }
        val clavo = Offset(30f, 150f)
        drawLine(pe.medio(C.ESCORIA), clavo, clavo + Offset(4f, 0.6f), 1.2f, StrokeCap.Round)
        drawPath(Path().apply { moveTo(clavo + Offset(3f, 0.6f)); cubicTo(clavo + Offset(5f, 8f), clavo + Offset(2f, 14f), clavo + Offset(4f, 22f)) }, pe.por(lerp(C.LANA, C.ORO, 0.3f), 0.2f), style = Stroke(1.3f, cap = StrokeCap.Round))
        // Las raíces que salen del pie y agarran el suelo.
        for ((a, b) in listOf(Offset(-4f, 270f) to Offset(-16f, 286f), Offset(40f, 268f) to Offset(66f, 284f), Offset(30f, 276f) to Offset(46f, 290f))) drawLine(pe.por(gris, 0f), a, b, 6f, StrokeCap.Round)
        if (v.nieveAbajo > 0.3f) drawPath(poligono(pts(-16f, 292f, -10f, 280f, 10f, 284f, 30f, 280f, 56f, 286f, 60f, 292f)), pe.por(C.NIEVE, 0.4f).copy(alpha = v.nieveAbajo))
    }

    /**
     * Una copa: una masa tendida, de base plana, oscura dentro; encima, las agujas en haces (abanicos cortos de trazos)
     * por el borde, claros del lado del sol; dos o tres piñas; con nieve, su capa encima con su sombra azul. Muerta: solo
     * ramillas negras y alguna aguja parda.
     */
    private fun DrawScope.copa(v: VistaAntiguas, c: Offset, w: Float, az: Azar, muerta: Boolean) {
        val pe = pincel(v, c)
        val lado = if (pe.solALaDerecha) 1f else -1f
        if (muerta) {
            for (k in 0..7) { val a = -2.9f + k * 0.42f + az.entre(-0.2f, 0.2f); val q = c + Offset(cos(a) * w * 0.9f, sin(a) * w * 0.55f); drawLine(pe.medio(C.QUEMADO), c + Offset(0f, w * 0.2f), q, 0.9f, StrokeCap.Round); drawLine(pe.medio(C.QUEMADO), q, q + Offset(cos(a + 0.6f) * 4f, sin(a + 0.6f) * 3f), 0.6f, StrokeCap.Round) }
            return
        }
        val h = w * 0.36f
        // La masa: varios bultos tendidos que se enciman, de borde quebrado, con la base casi plana.
        val bultos = (0 until 4).map { k -> val d = (k - 1.5f) / 1.5f; TrazosValle.borron(c + Offset(d * w * 0.55f, -az.entre(0f, h * 0.7f) + kotlin.math.abs(d) * h * 0.3f), w * az.entre(0.42f, 0.58f), h * az.entre(0.7f, 1f), az, 12, 0.32f, 0.5f) }
        val masa = union(bultos)
        val hondo = pe.sombra(lerp(C.PINO, C.ESCORIA, 0.3f)); val medio = pe.por(C.PINO, 0f); val claro = pe.luz(lerp(C.PINO, C.ALERCE, 0.5f))
        drawPath(masa, hondo)
        // El lomo de cada bulto, donde da la luz (arriba y del lado del sol); abajo, la sombra honda.
        clipPath(masa) { for (bu in bultos.indices) { val q = c + Offset(((bu - 1.5f) / 1.5f) * w * 0.55f + lado * w * 0.12f, -h * 0.7f); drawOval(medio.copy(alpha = 0.85f), Offset(q.x - w * 0.36f, q.y - h * 0.5f), Size(w * 0.72f, h * 1.1f)) } }
        // Las agujas en haces: abanicos de trazos cortos que salen por el borde de arriba y de los lados; claros del lado del sol.
        val n = (w * 1.4f).toInt()
        for (i in 0 until n) {
            val a = -3.1416f + 3.1416f * (i + az.entre(-0.3f, 0.3f)) / n
            val q = c + Offset(cos(a) * w * az.entre(0.7f, 0.98f), sin(a) * h * az.entre(0.9f, 1.4f) - h * 0.2f)
            val col = if (cos(a) * lado > -0.1f) claro else medio
            for (k in -2..2) { val ang = a + k * 0.24f + az.entre(-0.08f, 0.08f); drawLine(col, q, q + Offset(cos(ang) * w * 0.15f, sin(ang) * w * 0.15f), 0.55f, StrokeCap.Round) }
        }
        // Los haces que cuelgan por debajo, oscuros.
        repeat((w * 0.4f).toInt()) { val q = c + Offset(az.entre(-0.8f, 0.8f) * w, h * 0.35f); for (k in -1..1) drawLine(hondo, q, q + Offset(k * 1.2f + az.entre(-0.5f, 0.5f), w * 0.1f), 0.55f, StrokeCap.Round) }
        // Las piñas, colgadas bajo la masa.
        repeat(2) { val q = c + Offset(az.entre(-0.5f, 0.5f) * w, h * 0.5f); drawOval(pe.por(lerp(C.CUERO, C.MADERA, 0.4f), 0.2f), Offset(q.x - 1.3f, q.y - 0.4f), Size(2.6f, 3.8f)); drawLine(pe.sombra(C.MADERA_OSC), q + Offset(-1f, 1.4f), q + Offset(1f, 1.4f), 0.4f) }
        if (v.nieveAbajo > 0.15f) {
            val k = v.nieveAbajo
            val arriba = (0..10).map { i -> val t = i / 10f; c + Offset((t - 0.5f) * 1.7f * w, -h * (0.8f + 0.35f * sin(t * 3.1416f)) - k * 3.6f * sin(t * 3.1416f)) }
            val abajo = (10 downTo 0).map { i -> val t = i / 10f; c + Offset((t - 0.5f) * 1.6f * w, -h * (0.35f + 0.25f * sin(t * 3.1416f)) + 1.2f * sin(t * 13f)) }
            drawPath(TrazosValle.cerrada(arriba + abajo), pe.por(C.NIEVE, 0.5f).copy(alpha = (0.5f + 0.5f * k).coerceAtMost(1f)))
            drawPath(curva(abajo), pe.sombra(C.NIEVE_SOMBRA).copy(alpha = 0.7f), style = Stroke(1f))
        }
    }

    /** Las linternas de la rama de abajo: cada una distinta; encendidas las de los que vuelven tarde (de noche, y todas la noche de las linternas y los días de la búsqueda). */
    private fun DrawScope.linternas(v: VistaAntiguas) {
        val n = v.encendido.linternasDelPino
        for ((i, li) in LINTERNAS.withIndex()) {
            val (cuelga, largo, k) = li
            val pe = pincel(v, cuelga + Offset(0f, largo))
            val luz = if (i < n) (if (v.noche > 0.25f) 1f else 0.7f) else 0f
            linterna(pe, cuelga, largo, k * 1.15f, luz, 0f, giro = if (i % 2 == 0) 0.4f else -0.3f, nieve = v.nieveAbajo)
        }
    }

    /**
     * La sombra del pino en el suelo de delante (dentro de la capa de sombras del primer plano): la del tronco, tumbada, y
     * la de la copa, una masa con claros, donde caiga dentro del suelo.
     */
    fun DrawScope.sombraDelPino(v: VistaAntiguas, c: Color) {
        val f = 1f - v.sombraEn(Offset(60f, 270f)); if (f <= 0.02f) return
        val cc = c.copy(alpha = f)
        sombraArrojada(v.r, TRONCO, PIE, cc, 250f, 250f)
        val h = 230f
        val centro = PIE + Offset(v.r.ax * h + 70f, v.r.ay * h)
        if (centro.y > 230f && v.r.dia > 0.2f) clipRect(0f, 248f, 392f, ALTO) { sombraDeFollaje(fijo("sombra del pino"), Rect(centro.x - 70f, centro.y - 10f, centro.x + 70f, centro.y + 24f), v.r, cc, 22, 60) }
    }
}
