package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LLAMA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LUZ_DE_CASA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.bajio
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/** Lo que se ve a lo lejos desde el «Fortuna»: los otros barcos, cada uno con su aparejo, y las palmeras de los cayos. */
internal object VelasPirata {
    private val C = ColorPirata

    /** Cómo va aparejado un barco: no hay dos iguales en el cuadro. */
    enum class Aparejo { BALANDRA, GOLETA, BERGANTIN, FRAGATA, PIRAGUA }

    /**
     * Un barco de vela visto de costado, con la línea de flotación en [agua] y [largo] de eslora. [velas]: largas
     * (navegando) o aferradas (fondeado). [bandera]: el color de la que lleva, si lleva.
     */
    fun DrawScope.velero(
        l: Luces, r: Rayo, agua: Offset, largo: Float, aparejo: Aparejo, haciaLaDerecha: Boolean, lejos: Float, viento: Float = 0.4f, semilla: Int = 0,
        pintura: Color = C.BREA, velas: Boolean = true, bandera: Color? = null, farol: Boolean = false, conReflejo: Boolean = true,
    ) {
        val L = largo; val h = L * 0.085f; val x0 = agua.x - L / 2f; val y = agua.y
        val az = Azar(Semilla.de("velero", semilla))
        val dibujo: DrawScope.() -> Unit = {
            val madera = l.tono(lerp(pintura, C.MADERA, 0.25f), lejos); val oscuro = l.tono(mult(pintura, r.color, 0.7f), lejos)
            val lona = l.tono(C.LONA, lejos); val lonaSombra = l.tono(mult(C.LONA_OSC, r.color, 0.5f * r.contraste), lejos); val palo = l.tono(Color(0xFF7A6248), lejos)
            if (conReflejo) {
                reflejoRoto(x0 + L * 0.04f, x0 + L * 0.98f, y, h * 2.2f, oscuro, 0.55f, az, maxOf(0.7f, h * 0.2f))
                if (velas) reflejoRoto(x0 + L * 0.2f, x0 + L * 0.8f, y + h * 0.8f, L * 0.3f, lona, 0.3f, az, maxOf(0.8f, h * 0.3f))
            }
            // El casco: la popa alta a la izquierda, el arrufo y la roda lanzada.
            val casco = Path().apply {
                moveTo(x0 + L * 0.04f, y); lineTo(x0, y - h * 1.5f); lineTo(x0 + L * 0.02f, y - h * 1.95f)
                cubicTo(x0 + L * 0.3f, y - h * 1.2f, x0 + L * 0.7f, y - h * 1.15f, x0 + L * 0.97f, y - h * 1.75f)
                cubicTo(x0 + L * 0.95f, y - h * 0.9f, x0 + L * 0.9f, y - h * 0.3f, x0 + L * 0.86f, y); close()
            }
            drawPath(casco, Brush.verticalGradient(listOf(madera, oscuro), y - h * 1.9f, y))
            if (L > 14f) {
                drawPath(Path().apply { moveTo(x0 + L * 0.02f, y - h * 1.5f); cubicTo(x0 + L * 0.3f, y - h * 0.86f, x0 + L * 0.7f, y - h * 0.82f, x0 + L * 0.955f, y - h * 1.36f) }, l.tono(C.OCRE, lejos), style = Stroke(maxOf(0.4f, h * 0.2f)))
                drawLine(l.tono(Color.White, lejos).copy(alpha = 0.6f), Offset(x0 + L * 0.03f, y + 0.2f), Offset(x0 + L * 0.9f, y + 0.2f), maxOf(0.35f, h * 0.1f))
            }
            val grueso = maxOf(0.45f, L * 0.012f)
            fun mastil(k: Float, alto: Float, cae: Float = 0.02f): Pair<Offset, Offset> { val pie = Offset(x0 + L * k, y - h * 1.3f); val tope = pie + Offset(-alto * cae, -alto); drawLine(palo, pie, tope, grueso, StrokeCap.Round); return pie to tope }
            fun pano(a: Offset, b: Offset, c: Offset, d: Offset?) {
                val forma = Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(c.x, c.y); if (d != null) lineTo(d.x, d.y); close() }
                val xs = listOf(a.x, b.x, c.x, d?.x ?: c.x)
                drawPath(forma, Brush.horizontalGradient(if (r.lat >= 0f) listOf(lonaSombra, lona) else listOf(lona, lonaSombra), xs.min(), xs.max()))
                if (L > 20f) drawPath(forma, lonaSombra.copy(alpha = 0.5f), style = Stroke(0.3f))
            }
            fun cangreja(pie: Offset, tope: Offset, botavara: Float, pico: Float) {
                // La vela de cuchillo, a popa del palo: la botavara abajo y el pico arriba.
                val a = pie + Offset(0f, -h * 0.9f); val b = a + Offset(-botavara, h * 0.25f); val alto = pie.y - tope.y
                val c = a + Offset(-pico * 0.92f, -alto * 0.88f); val d = a + Offset(0f, -alto * 0.66f)
                if (velas) { pano(a, b, c, d); drawLine(palo, d, c, grueso * 0.8f, StrokeCap.Round) }
                else { drawLine(lona, a + Offset(0f, -h * 0.25f), b + Offset(0f, -h * 0.2f), maxOf(0.7f, h * 0.5f), StrokeCap.Round); drawLine(palo, a + Offset(0f, -h * 0.7f), b + Offset(botavara * 0.12f, -h * 0.75f), grueso * 0.8f) }
                drawLine(palo, a, b, grueso * 0.9f, StrokeCap.Round)
            }
            fun foque(tope: Offset, punta: Offset, puno: Offset) { if (velas) pano(tope, punta, puno, null) else drawLine(lona, puno, punta, maxOf(0.5f, h * 0.3f), StrokeCap.Round); drawLine(palo.copy(alpha = 0.7f), tope, punta, 0.3f) }
            fun cuadra(tope: Offset, y0: Float, y1: Float, ancho: Float) {
                // Una vela cuadra entre dos vergas, hinchada hacia la proa.
                val cx = tope.x; val w0 = ancho * 0.42f; val w1 = ancho * 0.5f
                if (velas) pano(Offset(cx - w0 * 0.5f, y0), Offset(cx + w0, y0), Offset(cx + w1 + ancho * 0.06f, y1), Offset(cx - w1 * 0.5f, y1))
                else drawLine(lona, Offset(cx - w0 * 0.6f, y0 + 0.4f), Offset(cx + w0, y0 + 0.4f), maxOf(0.6f, h * 0.36f), StrokeCap.Round)
                drawLine(palo, Offset(cx - w0 * 0.6f, y0), Offset(cx + w0 * 1.05f, y0), grueso * 0.7f)
            }
            val proa = Offset(x0 + L * 0.97f, y - h * 1.75f); val baupres = proa + Offset(L * 0.2f, -h * 0.9f)
            when (aparejo) {
                Aparejo.BALANDRA -> {
                    drawLine(palo, proa, baupres, grueso, StrokeCap.Round)
                    val (pie, tope) = mastil(0.58f, L * 0.92f)
                    cangreja(pie, tope, L * 0.56f, L * 0.4f)
                    foque(tope + Offset(0f, L * 0.2f), baupres, pie + Offset(L * 0.1f, -h * 0.5f))
                    foque(tope + Offset(0f, L * 0.36f), proa + Offset(L * 0.04f, -h * 0.3f), pie + Offset(L * 0.03f, -h * 0.4f))
                    if (velas) cuadra(tope, tope.y + L * 0.02f, tope.y + L * 0.22f, L * 0.4f)
                    if (bandera != null) drawPath(poligono(tope, tope + Offset(-L * 0.12f, L * 0.018f), tope + Offset(-L * 0.11f, L * 0.07f), tope + Offset(0f, L * 0.06f)), l.tono(bandera, lejos))
                    if (farol) drawCircle(LUZ_DE_CASA, maxOf(0.6f, L * 0.016f), Offset(x0 + L * 0.03f, y - h * 2.6f))
                }
                Aparejo.GOLETA -> {
                    drawLine(palo, proa, baupres, grueso, StrokeCap.Round)
                    val (pie1, tope1) = mastil(0.66f, L * 0.74f); val (pie2, tope2) = mastil(0.32f, L * 0.82f)
                    cangreja(pie2, tope2, L * 0.36f, L * 0.26f); cangreja(pie1, tope1, L * 0.3f, L * 0.22f)
                    foque(tope1 + Offset(0f, L * 0.1f), baupres, pie1 + Offset(L * 0.08f, -h * 0.5f))
                    if (bandera != null) drawPath(poligono(tope2, tope2 + Offset(-L * 0.1f, L * 0.02f), tope2 + Offset(0f, L * 0.05f)), l.tono(bandera, lejos))
                    if (farol) drawCircle(LUZ_DE_CASA, maxOf(0.6f, L * 0.016f), Offset(x0 + L * 0.03f, y - h * 2.6f))
                }
                Aparejo.BERGANTIN -> {
                    drawLine(palo, proa, baupres, grueso, StrokeCap.Round)
                    val (pie1, tope1) = mastil(0.7f, L * 0.8f); val (pie2, tope2) = mastil(0.3f, L * 0.86f)
                    for (i in 0..2) cuadra(tope1, tope1.y + L * (0.03f + 0.24f * i), tope1.y + L * (0.24f + 0.24f * i) - 0.6f, L * (0.26f + 0.07f * i))
                    cuadra(tope2, tope2.y + L * 0.03f, tope2.y + L * 0.26f, L * 0.26f)
                    cangreja(pie2, Offset(tope2.x, tope2.y + L * 0.28f), L * 0.36f, L * 0.26f)
                    foque(tope1 + Offset(0f, L * 0.16f), baupres, pie1 + Offset(L * 0.06f, -h * 0.5f))
                    if (bandera != null) drawPath(poligono(tope2, tope2 + Offset(-L * 0.1f, L * 0.016f), tope2 + Offset(-L * 0.1f, L * 0.06f), tope2 + Offset(0f, L * 0.055f)), l.tono(bandera, lejos))
                    if (farol) { drawCircle(LUZ_DE_CASA, maxOf(0.6f, L * 0.014f), Offset(x0 + L * 0.03f, y - h * 2.6f)); drawCircle(LUZ_DE_CASA, maxOf(0.5f, L * 0.012f), pie1 + Offset(0f, -h * 1.4f)) }
                }
                Aparejo.FRAGATA -> {
                    drawLine(palo, proa, baupres, grueso, StrokeCap.Round)
                    for ((k, alto) in listOf(0.2f to 0.62f, 0.5f to 0.8f, 0.78f to 0.7f)) {
                        val (_, tope) = mastil(k, L * alto)
                        for (i in 0..2) cuadra(tope, tope.y + L * alto * (0.04f + 0.3f * i), tope.y + L * alto * (0.3f + 0.3f * i) - 0.4f, L * (0.2f + 0.05f * i))
                    }
                    if (bandera != null) drawRect(l.tono(bandera, lejos), Offset(x0 - L * 0.04f, y - h * 3.4f), Size(L * 0.08f, L * 0.05f))
                }
                Aparejo.PIRAGUA -> {
                    val (pie, tope) = mastil(0.5f, L * 0.7f, 0.08f)
                    if (velas) pano(tope, pie + Offset(-L * 0.36f, -h * 0.6f), pie + Offset(L * 0.06f, -h * 0.4f), null)
                }
            }
        }
        if (haciaLaDerecha) dibujo() else scale(-1f, 1f, agua) { dibujo() }
    }

    /** Una palmera lejana: el tronco fino y combado y la copa de palmas caídas. [ladeo]: hacia dónde se inclina. */
    fun DrawScope.palmera(l: Luces, r: Rayo, pie: Offset, alto: Float, ladeo: Float, lejos: Float, az: Azar) {
        val copa = pie + Offset(ladeo, -alto)
        val tronco = Path().apply { moveTo(pie.x, pie.y); cubicTo(pie.x + ladeo * 0.1f, pie.y - alto * 0.4f, copa.x - ladeo * 0.4f, copa.y + alto * 0.4f, copa.x, copa.y) }
        drawPath(tronco, l.tono(Color(0xFF8A7458), lejos), style = Stroke(maxOf(0.5f, alto * 0.07f), cap = StrokeCap.Round))
        val verde = l.tono(C.PALMA, lejos); val luz = l.tono(lerp(C.PALMA, Color(0xFFDDE49A), 0.45f), lejos); val sombra = l.tono(mult(C.PALMA, r.color, 0.85f), lejos)
        val n = 7 + az.entero(3)
        for (i in 0 until n) {
            val a = -3.3f + 3.5f * i / (n - 1f) + az.entre(-0.14f, 0.14f); val largo = alto * az.entre(0.42f, 0.62f)
            val fin = copa + Offset(cos(a) * largo, sin(a) * largo * 0.55f + largo * 0.42f)
            val codo = copa + Offset(cos(a) * largo * 0.6f, sin(a) * largo * 0.7f - largo * 0.12f)
            val c = if (cos(a) * r.lat > 0.25f) luz else if (cos(a) * r.lat < -0.25f) sombra else verde
            drawPath(Path().apply { moveTo(copa.x, copa.y); cubicTo(codo.x, codo.y, codo.x, codo.y, fin.x, fin.y) }, c, style = Stroke(maxOf(0.6f, alto * 0.085f), cap = StrokeCap.Round))
        }
        drawCircle(sombra, maxOf(0.5f, alto * 0.06f), copa)
    }

    /** El día del abordaje: la presa, un mercante al pairo a tiro de mosquete, con las velas agujereadas y la bandera a media driza. */
    fun DrawScope.presa(m: Momento, l: Luces, r: Rayo) {
        velero(l, r, Offset(148f, 170f), 104f, Aparejo.BERGANTIN, haciaLaDerecha = true, lejos = 0.12f, viento = 0.3f, semilla = 7, pintura = Color(0xFF4A3A30), bandera = Color(0xFFE8E2D0))
        // Los balazos en las velas y los garfios que ya vuelan hacia ella.
        val az = Azar(Semilla.de("pirata", "balazos"))
        repeat(9) { val p = Offset(az.entre(126f, 190f), az.entre(96f, 150f)); drawOval(l.medio.copy(alpha = 0.85f), Offset(p.x, p.y), Size(az.entre(1.2f, 2.6f), az.entre(1.4f, 3f))) }
        // La espuma de los tiros cortos.
        for (p in listOf(Offset(86f, 182f), Offset(232f, 178f), Offset(196f, 186f))) { mancha(p, 6f, 1.6f, l.tono(Color.White), 0.7f); drawLine(l.tono(Color.White).copy(alpha = 0.8f), p + Offset(0f, -0.4f), p + Offset(0f, -6f), 1.2f, StrokeCap.Round) }
    }

    /** El humo de la pólvora, entre los dos barcos. */
    fun DrawScope.humoDeLaPresa(l: Luces, reloj: Float, viento: Float) {
        humo(l, Offset(120f, 162f), 34f, 22f, reloj, viento * 0.6f, 0.25f, 0.1f, 51, 10, 1.1f)
        humo(l, Offset(176f, 160f), 28f, 18f, reloj, viento * 0.6f, 0.2f, 0.1f, 52, 9, 1f)
        humo(l, Offset(338f, 190f), 30f, 16f, reloj, viento * 0.8f, 0.3f, 0f, 53, 9, 1f)
    }
}

/** Nassau, en la temporada 1 (estado `nassau`): lo que se ve por encima de la amurada con el balandro fondeado en el puerto. Es una variante del cuadro de a bordo (decisión 21). */
internal object NassauPirata {
    private val C = ColorPirata
    private const val H = EscenaPirata.H

    /** La orilla de la bahía, de izquierda a derecha: playa, una punta y la subida al fuerte. */
    private val ORILLA = listOf(
        Offset(-4f, 141f), Offset(14f, 140f), Offset(30f, 141.2f), Offset(48f, 139.4f), Offset(66f, 140.6f), Offset(86f, 138.6f), Offset(104f, 139.6f), Offset(124f, 137.8f),
        Offset(146f, 138.8f), Offset(166f, 137f), Offset(188f, 138.2f), Offset(208f, 136.4f), Offset(230f, 137.6f), Offset(252f, 135.6f), Offset(272f, 136.8f), Offset(292f, 134.6f),
        Offset(312f, 135.8f), Offset(334f, 133.6f), Offset(356f, 134.8f), Offset(384f, 132.6f),
    )

    private val BARCOS = listOf(
        // dónde, eslora, aparejo, hacia la derecha, lejos, pintura, bandera
        listOf(Offset(196f, 147f), 34f, VelasPirata.Aparejo.FRAGATA, false, 0.36f, Color(0xFF4A3A30), Color(0xFFB8382E)),
        listOf(Offset(292f, 152f), 30f, VelasPirata.Aparejo.GOLETA, true, 0.3f, Color(0xFF3E5A6A), Color(0xFF211C20)),
        listOf(Offset(120f, 158f), 44f, VelasPirata.Aparejo.BERGANTIN, true, 0.24f, Color(0xFF2E2622), Color(0xFF211C20)),
        listOf(Offset(20f, 166f), 40f, VelasPirata.Aparejo.BALANDRA, false, 0.2f, Color(0xFF5E4430), Color(0xFFE8E2D0)),
        listOf(Offset(330f, 172f), 58f, VelasPirata.Aparejo.BALANDRA, false, 0.12f, Color(0xFF2E2622), Color(0xFFB8382E)),
    )

    fun DrawScope.puerto(m: Momento, l: Luces, r: Rayo) {
        val fijo = Azar(Semilla.de("pirata", "nassau"))
        val az = Azar(Semilla.de("pirata", "nassau casas"))
        // La isla: una loma baja y verde detrás del pueblo.
        monte(l, perfil(fijo, -40f, 430f, 122f, 17f, 0.34f, 30), 126f, Color(0xFF6E8A66), 0.6f)
        monte(l, perfil(fijo, 180f, 430f, 124f, 12f, 0.3f, 18, isla = true), 128f, Color(0xFF64865A), 0.46f)
        // El agua de la bahía: quieta y clara.
        val zona = Rect(0f, 132f, ANCHO, 206f)
        agua(l, zona, m.azar("bahia"), propio = lerp(C.MAR, C.BAJIO, 0.35f), viento = 0.12f, hondo = lerp(C.MAR_HONDO, C.MAR, 0.5f), orilla = C.BAJIO)
        val orilla = curva(ORILLA)
        bajio(l, orilla, 26f, C.BAJIO)
        // La tierra: de la loma a la playa.
        val tierra = curva(ORILLA).apply { lineTo(ANCHO + 4f, 118f); lineTo(-4f, 118f); close() }
        drawPath(tierra, Brush.verticalGradient(listOf(l.tono(Color(0xFF7A9468), 0.5f), l.tono(Color(0xFFA8AE7C), 0.4f), l.tono(C.ARENA, 0.3f), l.tono(C.ARENA, 0.26f)), 118f, 141f))
        clipPath(tierra) {
            repeat(40) { val p = Offset(az.entre(0f, ANCHO), az.entre(120f, 131f)); mancha(p, az.entre(3f, 9f), az.entre(0.8f, 1.8f), l.tono(if (it % 2 == 0) Color(0xFF58784E) else Color(0xFF94A670), 0.42f), 0.45f) }
            // La playa, con las huellas, las barcas varadas y los barriles.
            repeat(30) { val p = Offset(az.entre(0f, ANCHO), az.entre(133.6f, 139f)); drawOval(l.tono(Color(0xFFCDBB94), 0.3f).copy(alpha = 0.7f), p, Size(az.entre(1.4f, 5f), az.entre(0.5f, 1f))) }
        }
        fuerte(l, r)
        // El pueblo: casas de tabla y, hacia la playa, chozas de palma y tiendas hechas con velas. Cada una distinta.
        var n = 0
        fun casita(x: Float, base: Float, w: Float, h: Float, lejos: Float) {
            val propio = listOf(Color(0xFFD9C8A4), Color(0xFFB89A72), Color(0xFFEDE4CE), Color(0xFFC8A678), Color(0xFFA88A66))[n % 5]
            val techo = listOf(Color(0xFF8A7A60), Color(0xFFB09868), Color(0xFF7A6652))[n % 3]; n++
            if (r.fuerza > 0.05f) drawOval(r.color, Offset(if (r.lat >= 0f) x - w * 0.7f else x + w * 0.5f, base - 0.5f), Size(w * 1.2f, 0.9f), alpha = r.fuerza * 0.8f, blendMode = BlendMode.Multiply)
            drawRect(l.tono(propio, lejos), Offset(x, base - h), Size(w, h))
            drawRect(l.tono(mult(propio, r.color, 0.7f * r.contraste), lejos), Offset(if (r.lat >= 0f) x else x + w * 0.6f, base - h), Size(w * 0.4f, h))
            val alero = h * az.entre(0.5f, 0.8f)
            drawPath(poligono(Offset(x - 0.5f, base - h), Offset(x + w + 0.5f, base - h), Offset(x + w * az.entre(0.5f, 0.75f), base - h - alero), Offset(x + w * az.entre(0.2f, 0.4f), base - h - alero)), l.tono(techo, lejos))
            drawRect(l.tono(Color(0xFF3E3026), lejos), Offset(x + w * az.entre(0.2f, 0.6f), base - h * 0.6f), Size(maxOf(0.5f, w * 0.18f), h * 0.6f))
            if (l.noche > 0.45f && n % 2 == 0) drawCircle(LUZ_DE_CASA.copy(alpha = 0.9f), 0.5f, Offset(x + w * 0.7f, base - h * 0.5f))
        }
        fun choza(x: Float, base: Float, w: Float, lejos: Float) {
            // Una choza de palma: un cono de paja, o una tienda de lona tendida en un palo.
            val lona = n % 2 == 0; n++
            val c = l.tono(if (lona) C.LONA else Color(0xFFB89C62), lejos); val s = l.tono(mult(if (lona) C.LONA_OSC else Color(0xFF8A6E42), r.color, 0.6f * r.contraste), lejos)
            val cima = Offset(x + w * az.entre(0.35f, 0.65f), base - w * az.entre(0.6f, 0.9f))
            drawPath(poligono(Offset(x, base), Offset(x + w, base), cima), c)
            drawPath(if (r.lat >= 0f) poligono(Offset(x, base), Offset(x + w * 0.45f, base), cima) else poligono(Offset(x + w * 0.55f, base), Offset(x + w, base), cima), s)
            drawLine(l.tono(Color(0xFF2E2620), lejos), Offset(cima.x, base), Offset(cima.x, base - w * 0.34f), maxOf(0.4f, w * 0.14f))
        }
        var x = 6f
        while (x < 296f) { val w = az.entre(3f, 5f); if (az.real() > 0.3f) casita(x, 126.6f + az.entre(-0.5f, 0.5f), w, az.entre(1.8f, 2.8f), 0.46f) else with(VelasPirata) { palmera(l, r, Offset(x + 1f, 127f), az.entre(5f, 8f), az.entre(-1.4f, 1.4f), 0.46f, az) }; x += w + az.entre(0.6f, 3f) }
        x = -2f
        while (x < 300f) { val w = az.entre(4.5f, 7.5f); if (az.real() > 0.22f) casita(x, 130.6f + az.entre(-0.6f, 0.6f), w, az.entre(2.6f, 4f), 0.36f) else with(VelasPirata) { palmera(l, r, Offset(x + 2f, 131f), az.entre(8f, 12f), az.entre(-2f, 2f), 0.36f, az) }; x += w + az.entre(0.8f, 4f) }
        x = 2f
        while (x < 330f) { val w = az.entre(3.4f, 6f); val base = alturaDeLaOrilla(x + w / 2f) - az.entre(2.6f, 4.2f); if (az.real() > 0.35f) choza(x, base, w, 0.28f) else if (az.real() > 0.5f) with(VelasPirata) { palmera(l, r, Offset(x, base), az.entre(9f, 14f), az.entre(-2.4f, 2.4f), 0.28f, az) }; x += w + az.entre(1.5f, 7f) }
        // Las barcas varadas en la arena, boca abajo.
        for (bx in listOf(40f, 112f, 178f, 246f)) { val by = alturaDeLaOrilla(bx) - 1.2f; drawPath(Path().apply { moveTo(bx - 4f, by); cubicTo(bx - 2.6f, by - 2.6f, bx + 2.6f, by - 2.6f, bx + 4f, by); close() }, l.tono(listOf(C.BREA, Color(0xFF6A5038), Color(0xFF3E5A6A))[(bx.toInt() / 7) % 3], 0.26f)) }
        // La espuma mansa de la orilla y los reflejos de la tierra.
        drawPath(orilla, l.tono(Color.White, 0.25f).copy(alpha = 0.75f * (1f - 0.5f * l.noche)), style = Stroke(0.9f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        for (i in 0 until ORILLA.size - 1) reflejoRoto(ORILLA[i].x, ORILLA[i + 1].x, maxOf(ORILLA[i].y, ORILLA[i + 1].y) + 0.8f, 6f, l.tono(lerp(C.ARENA, Color(0xFF8E9A70), 0.4f), 0.3f), 0.4f, az)
        estela(l, zona, m.azar("estela"))
        // Los barcos fondeados, de atrás adelante: todos con las velas aferradas, cada uno con su aparejo y su bandera.
        val deNoche = l.noche > 0.4f && m.hacer != Hacer.DORMIR
        with(VelasPirata) {
            BARCOS.forEachIndexed { i, b ->
                velero(l, r, b[0] as Offset, b[1] as Float, b[2] as VelasPirata.Aparejo, b[3] as Boolean, b[4] as Float, 0.1f, 20 + i, b[5] as Color, velas = false, bandera = b[6] as Color, farol = deNoche)
                // El cable del ancla.
                val p = b[0] as Offset; val largo = b[1] as Float; val lado = if (b[3] as Boolean) 1f else -1f
                drawLine(l.tono(C.CABO, b[4] as Float).copy(alpha = 0.8f), p + Offset(lado * largo * 0.46f, -largo * 0.1f), p + Offset(lado * largo * 0.7f, 0.6f), 0.4f)
            }
        }
        // El cable del ancla del «Fortuna», que sale por la proa y se pierde en el agua.
        drawPath(Path().apply { moveTo(ANCHO + 2f, 190f); cubicTo(360f, 188f, 340f, 186f, 322f, 187.4f) }, l.tono(C.CABO), style = Stroke(1.2f, cap = StrokeCap.Round))
        mancha(Offset(322f, 187.8f), 5f, 1.2f, l.tono(Color.White), 0.6f)
    }

    private fun alturaDeLaOrilla(x: Float): Float {
        for (i in 0 until ORILLA.size - 1) if (x <= ORILLA[i + 1].x) { val a = ORILLA[i]; val b = ORILLA[i + 1]; return a.y + (b.y - a.y) * ((x - a.x) / (b.x - a.x)).coerceIn(0f, 1f) }
        return ORILLA.last().y
    }

    /** El fuerte, medio caído, en su altillo: un lienzo entero, otro desmoronado, un baluarte y el asta sin bandera. */
    private fun DrawScope.fuerte(l: Luces, r: Rayo) {
        val lejos = 0.34f
        val piedra = l.tono(Color(0xFFD6CCB2), lejos); val sombra = l.tono(mult(Color(0xFFB4A88C), r.color, 0.7f * r.contraste), lejos)
        val alto = curva(listOf(Offset(296f, 131.6f), Offset(306f, 127.4f), Offset(320f, 124.6f), Offset(338f, 123.6f), Offset(356f, 124.2f), Offset(372f, 126f), Offset(384f, 127.4f)), 133f)
        drawPath(alto, Brush.verticalGradient(listOf(l.tono(Color(0xFF8E9C6E), 0.4f), l.tono(Color(0xFFBEB48A), 0.32f)), 123.6f, 133f))
        // El lienzo de la muralla, con su talud, y la brecha por donde se vino abajo.
        val muro = Path().apply {
            moveTo(310f, 126.4f); lineTo(311.5f, 118.8f); lineTo(326f, 118.4f); lineTo(327f, 120.6f); lineTo(331f, 121.4f); lineTo(333f, 119.4f); lineTo(336f, 122.6f); lineTo(340f, 123.2f)
            lineTo(342f, 118f); lineTo(366f, 117.6f); lineTo(367.6f, 125.4f); close()
        }
        drawPath(muro, Brush.horizontalGradient(if (r.lat >= 0f) listOf(sombra, piedra) else listOf(piedra, sombra), 310f, 368f))
        for (xx in listOf(314f, 318.6f, 323.2f, 345f, 350f, 355f, 360f)) drawRect(l.tono(Color(0xFF4A4238), lejos), Offset(xx, 119.8f), Size(1.5f, 1.3f))
        // El baluarte, más alto, y los escombros al pie de la brecha.
        drawPath(poligono(Offset(356f, 125.6f), Offset(357.4f, 114.6f), Offset(372f, 114.4f), Offset(373.6f, 125.6f)), Brush.horizontalGradient(if (r.lat >= 0f) listOf(sombra, piedra) else listOf(piedra, sombra), 356f, 374f))
        for (i in 0..3) drawRect(piedra, Offset(357.6f + i * 3.9f, 113f), Size(2.4f, 1.6f))
        for (p in listOf(Offset(329f, 126f), Offset(333f, 126.8f), Offset(336.6f, 125.8f), Offset(331f, 128f))) drawOval(sombra, p, Size(2.8f, 1.5f))
        drawLine(l.tono(Color(0xFF6A5A46), lejos), Offset(364.6f, 114.4f), Offset(364.6f, 103f), 0.6f)
        if (r.fuerza > 0.05f) drawLine(r.color, Offset(310f, 127f), Offset(368f, 126f), 1.2f, alpha = r.fuerza * 0.6f, blendMode = BlendMode.Multiply)
    }

    /** Lo que se mueve en el puerto: los humos de la playa y, de noche, las hogueras y los faroles de los barcos con su reflejo. */
    fun DrawScope.puertoVivo(m: Momento, l: Luces, reloj: Float) {
        val despiertos = m.hacer != Hacer.DORMIR
        for ((i, x) in listOf(64f, 150f, 226f).withIndex()) {
            val pie = Offset(x, alturaDeLaOrilla(x) - 2.6f)
            humo(l, pie + Offset(0f, -1f), 22f + 4f * i, 6f, reloj, -0.25f, 0.2f, 0.26f, 60 + i, 8, 0.7f)
            if (l.noche > 0.3f && despiertos) { llama(pie, 2.6f, reloj, 60 + i); resplandor(pie + Offset(0f, -1f), 9f, 0.5f * l.noche); reflejo(pie + Offset(0f, 3.6f), 10f, LLAMA, 0.5f * l.noche, reloj, 60 + i) }
        }
        if (l.noche > 0.4f && despiertos) BARCOS.forEachIndexed { i, b ->
            val p = b[0] as Offset; val largo = b[1] as Float; val lado = if (b[3] as Boolean) -1f else 1f
            reflejo(p + Offset(lado * largo * 0.46f, 0.5f), 6f + largo * 0.16f, LUZ_DE_CASA, 0.55f * l.noche, reloj, 70 + i)
        }
    }
}
