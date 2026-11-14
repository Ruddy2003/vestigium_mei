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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mata
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

/**
 * Nassau, en la temporada 1 (estado `nassau`): lo que se ve por encima de la amurada con el balandro fondeado en el
 * puerto. Es una variante del cuadro de a bordo (decisión 21).
 *
 * La bahía se aleja de izquierda a derecha: a la izquierda queda cerca una punta de arena, con la taberna y las
 * palmeras grandes; la orilla se mete en una ensenada, con el almacén y su muelle, y sigue hasta el fuerte, ya lejos.
 * Por eso lo que está más abajo en el cuadro está más cerca y se dibuja más grande ([cerca]). Detrás del pueblo, la
 * loma va cubierta de monte.
 *
 * **[Aproximación]**: en 1703 una expedición francesa y española saqueó y quemó el pueblo y desmanteló el fuerte, y
 * en 1718 seguía sin gobierno. **[Diseño propio]**: la taberna de dos pisos, el almacén del muelle y la iglesia
 * quemada no copian ningún edificio documentado; son los que un puerto así pudo tener.
 */
internal object NassauPirata {
    private val C = ColorPirata
    private const val H = EscenaPirata.H

    /** La orilla de la bahía, de izquierda a derecha: la punta cercana, la ensenada y la subida al fuerte. */
    private val ORILLA = listOf(
        Offset(-4f, 152.6f), Offset(12f, 152f), Offset(28f, 152.8f), Offset(44f, 151.8f), Offset(60f, 152.4f), Offset(76f, 151f), Offset(88f, 149.2f), Offset(98f, 146.6f),
        Offset(110f, 144.8f), Offset(126f, 143.4f), Offset(146f, 142.4f), Offset(166f, 141f), Offset(188f, 140.2f), Offset(208f, 138.6f), Offset(230f, 138.2f), Offset(252f, 136.4f),
        Offset(272f, 136.8f), Offset(292f, 134.6f), Offset(312f, 135.8f), Offset(334f, 133.6f), Offset(356f, 134.8f), Offset(384f, 132.6f),
    )

    private val BARCOS = listOf(
        // dónde, eslora, aparejo, hacia la derecha, lejos, pintura, bandera
        listOf(Offset(222f, 151f), 34f, VelasPirata.Aparejo.FRAGATA, false, 0.36f, Color(0xFF4A3A30), Color(0xFFB8382E)),
        listOf(Offset(292f, 152f), 30f, VelasPirata.Aparejo.GOLETA, true, 0.3f, Color(0xFF3E5A6A), Color(0xFF211C20)),
        listOf(Offset(114f, 159f), 44f, VelasPirata.Aparejo.BERGANTIN, true, 0.24f, Color(0xFF2E2622), Color(0xFF211C20)),
        listOf(Offset(20f, 168f), 40f, VelasPirata.Aparejo.BALANDRA, false, 0.2f, Color(0xFF5E4430), Color(0xFFE8E2D0)),
        listOf(Offset(330f, 172f), 58f, VelasPirata.Aparejo.BALANDRA, false, 0.12f, Color(0xFF2E2622), Color(0xFFB8382E)),
    )

    /** Dónde arden las hogueras de la playa. */
    private val HOGUERAS = listOf(40f, 128f, 228f)

    private fun alturaEn(linea: List<Offset>, x: Float): Float {
        for (i in 0 until linea.size - 1) if (x <= linea[i + 1].x) { val a = linea[i]; val b = linea[i + 1]; return a.y + (b.y - a.y) * ((x - a.x) / (b.x - a.x)).coerceIn(0f, 1f) }
        return linea.last().y
    }

    private fun alturaDeLaOrilla(x: Float): Float = alturaEn(ORILLA, x)

    /** Qué tan cerca queda algo que pisa el suelo a la altura [base]: de 0 (al pie de la loma, lejos) a 1 (la punta de arena). */
    private fun cerca(base: Float): Float = ((base - 124f) / 22f).coerceIn(0f, 1f)

    private fun lejosDe(base: Float): Float = 0.46f - 0.28f * cerca(base)

    /** El ancho de la playa: se estrecha al alejarse. */
    private fun playa(x: Float): Float = 7.6f - 5f * (x / 300f).coerceIn(0f, 1f)

    /** Donde acaba el pueblo y empieza la loma. */
    private fun pieDeLoma(x: Float): Float = 129f - 5f * (x / 300f).coerceIn(0f, 1f)

    fun DrawScope.puerto(m: Momento, l: Luces, r: Rayo) {
        val fijo = Azar(Semilla.de("pirata", "nassau"))
        val az = Azar(Semilla.de("pirata", "nassau casas"))
        val encendida = l.noche > 0.45f && m.hacer != Hacer.DORMIR

        // 1. Las lomas, de atrás adelante: la del fondo, azulada; la que corre detrás del pueblo, cubierta de monte.
        monte(l, perfil(fijo, 130f, 470f, H + 1f, 12f, 0.3f, 20, isla = true), 131f, Color(0xFF6A8482), 0.8f)
        val loma = perfil(fijo, -40f, 420f, H + 1f, 12f, 0.34f, 34).map { Offset(it.x, it.y + (it.x - 150f) * 0.012f) }
        val verdeLoma = Color(0xFF5C8256)
        bosque(l, r, loma, 131f, 128f, verdeLoma, 0.6f, 2.1f, Azar(Semilla.de("pirata", "nassau monte")), 5)

        // 2. El agua de la bahía: quieta y clara.
        val zona = Rect(0f, 132f, ANCHO, 206f)
        agua(l, zona, m.azar("bahia"), propio = lerp(C.MAR, C.BAJIO, 0.35f), viento = 0.12f, hondo = lerp(C.MAR_HONDO, C.MAR, 0.5f), orilla = C.BAJIO)
        val orilla = curva(ORILLA)
        bajio(l, orilla, 26f, C.BAJIO)

        // 3. El cerro de la punta, más cerca: se le distinguen las copas.
        val cerro = perfil(Azar(Semilla.de("pirata", "nassau cerro")), -80f, 178f, 130f, 27f, 0.3f, 24, isla = true)
        val verdeCerro = Color(0xFF527A4A)
        bosque(l, r, cerro, 132f, 131f, verdeCerro, 0.4f, 3.4f, Azar(Semilla.de("pirata", "nassau monte cercano")), 4)

        // 4. El suelo del pueblo, de la loma a la playa, y la playa.
        val tierra = curva(ORILLA).apply { lineTo(ANCHO + 4f, pieDeLoma(ANCHO) - 0.6f); lineTo(300f, pieDeLoma(300f) - 0.6f); lineTo(-4f, pieDeLoma(-4f) - 0.6f); close() }
        drawPath(tierra, Brush.verticalGradient(listOf(l.tono(Color(0xFF86A06A), 0.44f), l.tono(Color(0xFFB2B484), 0.34f), l.tono(Color(0xFFD9CCA2), 0.22f)), 123f, 147f))
        val arriba = ORILLA.map { Offset(it.x, it.y - playa(it.x)) }
        val arenal = curva(ORILLA).apply { for (p in arriba.reversed()) lineTo(p.x, p.y); close() }
        clipPath(tierra) {
            // Los claros de tierra pisada y las matas.
            repeat(46) { val x = az.entre(0f, ANCHO); val y = az.entre(pieDeLoma(x), alturaDeLaOrilla(x) - playa(x)); val k = 0.6f + cerca(y); mancha(Offset(x, y), az.entre(3f, 9f) * k, az.entre(0.7f, 1.5f) * k, l.tono(if (it % 3 == 0) Color(0xFF6C8C58) else Color(0xFFE0D4AC), lejosDe(y)), 0.42f) }
            // Los caminos que suben de la playa al pie de la loma: se estrechan al alejarse.
            val senda = l.tono(Color(0xFFE6DAB4), 0.26f).copy(alpha = 0.8f)
            for ((abajo, cima) in listOf(Offset(88f, 143.4f) to Offset(104f, 128f), Offset(136f, 137.4f) to Offset(128f, 127.4f), Offset(214f, 134.6f) to Offset(226f, 125.6f), Offset(262f, 133f) to Offset(258f, 124.8f))) {
                val ancho = 0.8f + 2.2f * cerca(abajo.y); val medio = Offset((abajo.x + cima.x) / 2f + (abajo.x - cima.x) * 0.3f, (abajo.y + cima.y) / 2f)
                drawPath(Path().apply { moveTo(abajo.x - ancho, abajo.y); cubicTo(medio.x - ancho * 0.6f, medio.y, medio.x - ancho * 0.4f, medio.y, cima.x - 0.3f, cima.y); lineTo(cima.x + 0.3f, cima.y); cubicTo(medio.x + ancho * 0.4f, medio.y, medio.x + ancho * 0.6f, medio.y, abajo.x + ancho, abajo.y); close() }, senda)
            }
        }
        drawPath(arenal, Brush.horizontalGradient(listOf(l.tono(C.ARENA, 0.16f), l.tono(C.ARENA, 0.32f)), 0f, ANCHO))
        clipPath(arenal) { repeat(44) { val x = az.entre(0f, ANCHO); val k = 0.5f + cerca(alturaDeLaOrilla(x)); drawOval(l.tono(Color(0xFFCDBB94), 0.3f).copy(alpha = 0.7f), Offset(x, alturaDeLaOrilla(x) - az.entre(1f, playa(x))), Size(az.entre(1.4f, 5f) * k, az.entre(0.5f, 1f) * k)) } }
        // Las matas y los árboles sueltos al pie de la loma.
        var mx = -2f
        while (mx < 300f) { val y = pieDeLoma(mx) + az.entre(-0.4f, 0.8f); val w = (2.6f + 4.4f * cerca(y + 6f)) * az.entre(0.7f, 1.3f); mata(l, Offset(mx, y), w, if (az.real() > 0.5f) Color(0xFF4E7446) else Color(0xFF6A8C52), lejosDe(y) + 0.06f, az); mx += w * az.entre(0.7f, 1.6f) }

        fuerte(l, r)

        // 5. El pueblo, de atrás adelante: casas de tabla en las calles de arriba; en la playa, chozas de palma y
        // tiendas hechas con velas. Entre ellas, los tres edificios grandes.
        val cosas = ArrayList<Pair<Float, DrawScope.() -> Unit>>()
        val ocupado = listOf(Rect(44f, 134f, 86f, 148f), Rect(138f, 131f, 181f, 141.5f), Rect(181f, 126f, 209f, 135f))
        fun libre(x: Float, w: Float, base: Float) = ocupado.none { x + w > it.left && x < it.right && base > it.top && base < it.bottom }
        var n = 0
        for (fila in 0..3) {
            val f = fila / 3f
            var x = -3f + 2f * fila
            while (x < 296f) {
                val arribaDeLaPlaya = alturaDeLaOrilla(x) - playa(x)
                val base = pieDeLoma(x) + 1.4f + (arribaDeLaPlaya - 0.4f - pieDeLoma(x) - 1.4f) * f + az.entre(-0.5f, 0.5f)
                val s = 0.8f + 1.5f * cerca(base); val w = az.entre(3.4f, 5.4f) * s; val h = az.entre(2f, 3f) * s
                val suerte = az.real(); val ladeo = az.entre(-0.1f, 0.1f); val alto = az.entre(4.6f, 6.6f) * s; val numero = n++
                if (libre(x, w, base)) {
                    val x0 = x
                    when {
                        suerte < (if (fila == 3) 0.34f else 0.62f) -> cosas += base to { casaDeTabla(l, r, numero, x0, base, w, h, lejosDe(base), encendida && numero % 2 == 0) }
                        suerte < (if (fila == 3) 0.66f else 0.7f) -> cosas += base to { choza(l, r, numero, x0, base, w * 0.9f, lejosDe(base)) }
                        suerte < 0.86f -> cosas += base to { with(VelasPirata) { palmera(l, r, Offset(x0 + w / 2f, base), alto, ladeo * alto, lejosDe(base), Azar(Semilla.de("pirata", "nassau palma", numero))) } }
                    }
                }
                x += w + az.entre(0.5f, 2.6f) * s
            }
        }
        cosas += 131.8f to { iglesia(l, r, 184f, 131.8f, 21f, 9.8f, lejosDe(131.8f)) }
        cosas += 139.2f to { almacen(l, r, 145f, 139.2f, 31f, 5.6f, lejosDe(139.2f), encendida) }
        cosas += 145f to { taberna(l, r, 50f, 145f, 28f, 14f, lejosDe(145f), encendida) }
        // Las palmeras grandes de la punta, que cruzan la loma y el cielo.
        for ((i, p) in listOf(Triple(30f, 146.6f, 25f), Triple(38f, 148f, 19f), Triple(92f, 143f, 22f), Triple(9f, 145f, 21f)).withIndex())
            cosas += p.second to { palmaDeLaPunta(l, r, Offset(p.first, p.second), p.third, if (i % 2 == 0) -3.4f else 3f, 0.17f, Azar(Semilla.de("pirata", "nassau palmera", i))) }
        for ((_, dibuja) in cosas.sortedBy { it.first }) dibuja()

        // 6. La playa: las barcas varadas boca abajo y los barriles, más grandes cuanto más cerca.
        for ((i, bx) in listOf(20f, 86f, 110f, 198f, 246f, 276f).withIndex()) {
            val by = alturaDeLaOrilla(bx) - playa(bx) * 0.4f; val k = 0.7f + 1.2f * cerca(by - 2f); val lejos = lejosDe(by)
            val pintura = listOf(Color(0xFF5E4430), Color(0xFF3E5A6A), Color(0xFF6A5038))[i % 3]
            if (r.fuerza > 0.05f) drawOval(r.color, Offset(bx - 5.4f * k, by - 0.4f * k), Size(11f * k, 1.1f * k), alpha = r.fuerza * 0.7f, blendMode = BlendMode.Multiply)
            val casco = Path().apply { moveTo(bx - 5f * k, by); cubicTo(bx - 4.2f * k, by - 1.7f * k, bx + 2.4f * k, by - 2.1f * k, bx + 5.6f * k, by - 0.2f * k); lineTo(bx + 5.2f * k, by); close() }
            drawPath(casco, Brush.verticalGradient(listOf(l.tono(lerp(pintura, Color(0xFFD8C8A0), 0.3f), lejos), l.tono(pintura, lejos)), by - 2f * k, by))
            clipPath(casco) { for (t in listOf(0.7f, 1.3f)) drawPath(Path().apply { moveTo(bx - 5f * k, by - t * k * 0.5f); cubicTo(bx - 3.6f * k, by - (1f + t) * k * 0.8f, bx + 2.4f * k, by - (1.1f + t) * k * 0.8f, bx + 5.6f * k, by - t * k * 0.4f) }, l.tono(C.BREA, lejos).copy(alpha = 0.4f), style = Stroke(0.22f * k)) }
            drawPath(Path().apply { moveTo(bx - 4.6f * k, by - 0.6f * k); cubicTo(bx - 3.8f * k, by - 1.9f * k, bx + 2.4f * k, by - 2.3f * k, bx + 5.6f * k, by - 0.3f * k) }, l.tono(Color(0xFFE2D4AE), lejos), style = Stroke(0.3f * k, cap = StrokeCap.Round))
        }
        muelle(l, r, az)
        // La espuma mansa de la orilla y los reflejos de la tierra.
        drawPath(orilla, l.tono(Color.White, 0.25f).copy(alpha = 0.75f * (1f - 0.5f * l.noche)), style = Stroke(0.9f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        for (i in 0 until ORILLA.size - 1) reflejoRoto(ORILLA[i].x, ORILLA[i + 1].x, maxOf(ORILLA[i].y, ORILLA[i + 1].y) + 0.8f, 5f + 5f * cerca(ORILLA[i].y), l.tono(lerp(C.ARENA, Color(0xFF8E9A70), 0.4f), 0.3f), 0.4f, az)
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

    /**
     * Una loma cubierta de monte cerrado, para que no parezca un prado: las copas le rizan la cresta; ladera abajo,
     * cada copa tiene su lado de luz y su sombra debajo, más grandes cuanto más cerca; y por encima asoman unos pocos
     * pinos, sueltos o de dos en dos. Se rellena hasta [fondo]; las copas llegan hasta [pie]. [copa]: el tamaño de una
     * copa a esa distancia.
     */
    private fun DrawScope.bosque(l: Luces, r: Rayo, cresta: List<Offset>, fondo: Float, pie: Float, propio: Color, lejos: Float, copa: Float, az: Azar, pinos: Int) {
        val c = l.tono(propio, lejos)
        val cima = lerp(c, l.tono(Color.White, lejos), 0.08f)
        val sombra = l.tono(mult(lerp(propio, Color(0xFF1E3A2A), 0.5f), r.color, 0.5f * r.contraste), lejos)
        val luz = l.tono(lerp(propio, Color(0xFFDCE296), 0.42f), lejos)
        val x0 = cresta.first().x; val x1 = cresta.last().x
        val arriba = cresta.minOf { it.y }
        val lado = if (r.lat >= 0f) 1f else -1f
        val forma = curva(cresta, fondo)
        // Los pinos que sobresalen: el tronco fino y la copa rala, de dos o tres manchas.
        repeat(pinos) {
            var x = az.entre(x0 + 14f, x1 - 14f)
            repeat(1 + az.entero(2)) {
                val base = Offset(x, alturaEn(cresta, x) + copa * 0.3f); val alto = copa * az.entre(0.9f, 1.5f); val tope = base + Offset(alto * az.entre(-0.1f, 0.1f), -alto)
                if (base.y < pie - copa * 1.5f) {
                    drawLine(sombra, base, tope, maxOf(0.3f, copa * 0.1f))
                    drawOval(cima, tope + Offset(-copa * 0.62f, -copa * 0.3f), Size(copa * 1.24f, copa * 0.8f))
                    drawOval(cima, tope + Offset(-copa * 0.2f - lado * copa * 0.3f, copa * 0.2f), Size(copa * 0.8f, copa * 0.5f))
                    drawOval(sombra.copy(alpha = 0.5f), tope + Offset(-copa * 0.5f - lado * copa * 0.12f, copa * 0.05f), Size(copa * 1f, copa * 0.4f))
                }
                x += copa * az.entre(1.1f, 1.8f)
            }
        }
        // La loma, con la cresta rizada por las copas.
        drawPath(forma, Brush.verticalGradient(listOf(cima, c, lerp(c, l.bruma, 0.3f * lejos)), arriba, fondo))
        var x = x0
        while (x < x1) {
            val rad = copa * az.entre(0.55f, 1f); val y = alturaEn(cresta, x)
            if (y < pie - 1f) drawOval(cima, Offset(x - rad, y - rad * 0.55f), Size(rad * 2f, rad * 1.6f))
            x += rad * az.entre(0.9f, 1.6f)
        }
        // La ladera: primero las hondonadas en sombra y los lomos al sol; luego, copa a copa, de atrás adelante.
        val cuantas = ((x1 - x0) * (pie - arriba) * 0.3f / (copa * copa)).toInt().coerceIn(0, 260)
        val copas = (0 until cuantas).map {
            val cx = az.entre(x0, x1); val top = alturaEn(cresta, cx); val d = az.real()
            Triple(cx, top + copa * 0.5f + d * (pie - top - copa * 0.5f).coerceAtLeast(0f), copa * (0.6f + 0.6f * d) * az.entre(0.8f, 1.2f))
        }.sortedBy { it.second }
        clipPath(forma) {
            repeat(7) { val cx = az.entre(x0, x1); val top = alturaEn(cresta, cx); mancha(Offset(cx, top + (pie - top) * az.entre(0.3f, 0.8f)), az.entre(20f, 46f), (pie - top) * az.entre(0.14f, 0.3f), if (it % 2 == 0) sombra else luz, if (it % 2 == 0) 0.3f else 0.1f + 0.2f * r.dia) }
            for ((cx, cy, rad) in copas) {
                drawOval(sombra.copy(alpha = 0.5f), Offset(cx - rad - lado * rad * 0.2f, cy - rad * 0.3f), Size(rad * 2f, rad * 1.3f))
                drawOval(luz.copy(alpha = 0.16f + 0.34f * r.dia), Offset(cx - rad * 0.75f + lado * rad * 0.25f, cy - rad * 0.8f), Size(rad * 1.5f, rad * 0.95f))
            }
        }
    }

    /**
     * Una palmera de la punta, a media distancia: ya se le ven las pencas, cada una con su nervio y las hojuelas
     * colgando, y los cocos. [ladeo]: cuánto se echa a un lado la copa.
     */
    private fun DrawScope.palmaDeLaPunta(l: Luces, r: Rayo, pie: Offset, alto: Float, ladeo: Float, lejos: Float, az: Azar) {
        val copa = pie + Offset(ladeo, -alto)
        val tronco = Path().apply { moveTo(pie.x, pie.y); cubicTo(pie.x + ladeo * 0.05f, pie.y - alto * 0.45f, copa.x - ladeo * 0.45f, copa.y + alto * 0.4f, copa.x, copa.y) }
        drawPath(tronco, l.tono(Color(0xFF8A7458), lejos), style = Stroke(alto * 0.05f, cap = StrokeCap.Round))
        drawPath(tronco, l.tono(mult(Color(0xFF8A7458), r.color, 0.8f), lejos).copy(alpha = 0.7f), style = Stroke(alto * 0.018f, cap = StrokeCap.Round))
        val verde = l.tono(C.PALMA, lejos); val luz = l.tono(lerp(C.PALMA, Color(0xFFDDE49A), 0.45f), lejos); val sombra = l.tono(mult(C.PALMA, r.color, 0.85f), lejos)
        val n = 9 + az.entero(3)
        // Primero las pencas de atrás (las que caen), luego las de arriba.
        for (i in (0 until n).sortedBy { abs(it - (n - 1) / 2f) * -1f }) {
            val a = -3.6f + 4.06f * i / (n - 1f) + az.entre(-0.12f, 0.12f); val largo = alto * az.entre(0.36f, 0.5f)
            val codo = copa + Offset(cos(a) * largo * 0.55f, sin(a) * largo * 0.6f - largo * 0.2f)
            val fin = copa + Offset(cos(a) * largo, sin(a) * largo * 0.55f + largo * 0.4f)
            val c = if (cos(a) * r.lat > 0.25f) luz else if (cos(a) * r.lat < -0.25f) sombra else verde
            for (j in 2..10) {
                val t = j / 10f; val u = 1f - t
                val p = Offset(u * u * copa.x + 2f * u * t * codo.x + t * t * fin.x, u * u * copa.y + 2f * u * t * codo.y + t * t * fin.y)
                val cuelga = largo * 0.26f * (1f - 0.55f * t)
                drawLine(c, p, p + Offset(cos(a) * cuelga * 0.3f, cuelga), maxOf(0.5f, alto * 0.022f), StrokeCap.Round)
            }
            drawPath(Path().apply { moveTo(copa.x, copa.y); quadraticTo(codo.x, codo.y, fin.x, fin.y) }, lerp(c, luz, 0.35f), style = Stroke(maxOf(0.45f, alto * 0.02f), cap = StrokeCap.Round))
        }
        for (k in listOf(-0.5f, 0.3f, -0.05f)) drawCircle(l.tono(Color(0xFF5E4A30), lejos), alto * 0.022f, copa + Offset(k * alto * 0.05f, alto * 0.035f))
    }

    /** La sombra que una casa echa en el suelo, del lado contrario al sol. */
    private fun DrawScope.sombraEnElSuelo(r: Rayo, x: Float, base: Float, w: Float, h: Float) {
        if (r.fuerza > 0.05f) drawOval(r.color, Offset(if (r.lat >= 0f) x - w * 0.55f else x + w * 0.5f, base - h * 0.1f), Size(w * 1.05f, maxOf(0.9f, h * 0.22f)), alpha = r.fuerza * 0.8f, blendMode = BlendMode.Multiply)
    }

    /** El lado de un muro que no mira al sol. */
    private fun DrawScope.ladoEnSombra(r: Rayo, x: Float, arriba: Float, w: Float, h: Float, cuanto: Float = 0.38f) {
        drawRect(r.color, Offset(if (r.lat >= 0f) x else x + w * (1f - cuanto), arriba), Size(w * cuanto, h), alpha = 0.6f * r.contraste, blendMode = BlendMode.Multiply)
    }

    /** Una casa de tabla, cada una con su color y su techo: a dos aguas de costado o de frente, de palma, o de una sola agua. */
    private fun DrawScope.casaDeTabla(l: Luces, r: Rayo, n: Int, x: Float, base: Float, w: Float, h: Float, lejos: Float, encendida: Boolean) {
        val az = Azar(Semilla.de("pirata", "nassau casa", n))
        val propio = listOf(Color(0xFFD9C8A4), Color(0xFFB89A72), Color(0xFFEDE4CE), Color(0xFFC8A678), Color(0xFFA88A66))[n % 5]
        val techo = l.tono(listOf(Color(0xFF8A7A60), Color(0xFFB09868), Color(0xFF7A6652))[n % 3], lejos)
        val vano = l.tono(Color(0xFF3E3026), lejos)
        sombraEnElSuelo(r, x, base, w, h)
        drawRect(l.tono(propio, lejos), Offset(x, base - h), Size(w, h))
        ladoEnSombra(r, x, base - h, w, h)
        if (w > 6f) for (i in 1..2) drawLine(vano.copy(alpha = 0.22f), Offset(x, base - h * i / 3f), Offset(x + w, base - h * i / 3f), 0.25f)
        val alero = h * az.entre(0.5f, 0.8f); val y = base - h
        when (n % 4) {
            0 -> drawPath(poligono(Offset(x - w * 0.08f, y), Offset(x + w * 1.08f, y), Offset(x + w * az.entre(0.6f, 0.8f), y - alero), Offset(x + w * az.entre(0.2f, 0.4f), y - alero)), techo)
            1 -> { drawPath(poligono(Offset(x - w * 0.08f, y), Offset(x + w * 1.08f, y), Offset(x + w * 0.5f, y - alero * 1.2f)), l.tono(propio, lejos)); drawPath(Path().apply { moveTo(x - w * 0.12f, y + 0.2f); lineTo(x + w * 0.5f, y - alero * 1.25f); lineTo(x + w * 1.12f, y + 0.2f) }, techo, style = Stroke(maxOf(0.5f, w * 0.1f), join = StrokeJoin.Miter)) }
            2 -> { val paja = l.tono(Color(0xFFB89C62), lejos); drawPath(Path().apply { moveTo(x - w * 0.14f, y + h * 0.16f); cubicTo(x + w * 0.1f, y - alero * 0.9f, x + w * 0.3f, y - alero * 1.5f, x + w * 0.5f, y - alero * 1.5f); cubicTo(x + w * 0.7f, y - alero * 1.5f, x + w * 0.9f, y - alero * 0.9f, x + w * 1.14f, y + h * 0.16f); close() }, paja); if (w > 5f) for (k in listOf(0.24f, 0.5f, 0.76f)) drawLine(l.tono(Color(0xFF8A6E42), lejos).copy(alpha = 0.5f), Offset(x + w * k, y - alero * 1.2f), Offset(x + w * (k + (k - 0.5f) * 0.5f), y + h * 0.1f), 0.3f) }
            else -> drawPath(poligono(Offset(x - w * 0.08f, y), Offset(x + w * 1.08f, y), Offset(x + w * 1.04f, y - alero * 0.9f), Offset(x - w * 0.04f, y - alero * 0.3f)), techo)
        }
        val px = x + w * az.entre(0.18f, 0.56f)
        drawRect(vano, Offset(px, base - h * 0.62f), Size(maxOf(0.5f, w * 0.18f), h * 0.62f))
        if (w > 6f) { val vx = if (px > x + w * 0.4f) x + w * 0.12f else x + w * 0.68f; drawRect(if (encendida) LUZ_DE_CASA else vano, Offset(vx, base - h * 0.66f), Size(w * 0.16f, h * 0.3f)); drawRect(l.tono(C.ROJO, lejos), Offset(vx + w * 0.16f, base - h * 0.66f), Size(w * 0.05f, h * 0.3f)) }
        else if (encendida) drawCircle(LUZ_DE_CASA.copy(alpha = 0.9f), maxOf(0.5f, w * 0.08f), Offset(x + w * 0.76f, base - h * 0.5f))
    }

    /** Una choza de palma, o una tienda hecha con una vela tendida en un palo. */
    private fun DrawScope.choza(l: Luces, r: Rayo, n: Int, x: Float, base: Float, w: Float, lejos: Float) {
        val az = Azar(Semilla.de("pirata", "nassau choza", n))
        val lona = n % 2 == 0
        val c = l.tono(if (lona) C.LONA else Color(0xFFB89C62), lejos); val s = l.tono(mult(if (lona) C.LONA_OSC else Color(0xFF8A6E42), r.color, 0.6f * r.contraste), lejos)
        val cima = Offset(x + w * az.entre(0.35f, 0.65f), base - w * az.entre(0.6f, 0.9f))
        sombraEnElSuelo(r, x, base, w, w * 0.5f)
        drawPath(poligono(Offset(x, base), Offset(x + w, base), cima), c)
        drawPath(if (r.lat >= 0f) poligono(Offset(x, base), Offset(x + w * 0.45f, base), cima) else poligono(Offset(x + w * 0.55f, base), Offset(x + w, base), cima), s)
        drawLine(l.tono(Color(0xFF2E2620), lejos), Offset(cima.x, base), Offset(cima.x, base - w * 0.34f), maxOf(0.4f, w * 0.14f))
        if (lona && w > 6f) drawLine(l.tono(Color(0xFF6A5038), lejos), cima, cima + Offset(0f, -w * 0.14f), 0.4f)
    }

    /** La taberna: la única casa de dos pisos, con la galería corrida arriba, la escalera por fuera y la muestra colgada en la esquina. */
    private fun DrawScope.taberna(l: Luces, r: Rayo, x: Float, base: Float, w: Float, h: Float, lejos: Float, encendida: Boolean) {
        val sol = r.lat >= 0f
        val cal = l.tono(Color(0xFFE8DEC4), lejos); val tabla = l.tono(Color(0xFFB4926A), lejos); val vano = l.tono(Color(0xFF3A2C24), lejos)
        val teja = l.tono(Color(0xFF7C6650), lejos); val tejaLuz = l.tono(Color(0xFFA0886C), lejos); val rojo = l.tono(C.ROJO, lejos); val palo = l.tono(Color(0xFF6A5038), lejos)
        val luz = LUZ_DE_CASA
        val piso = h * 0.5f; val alero = base - h
        sombraEnElSuelo(r, x, base, w, h)
        drawRect(cal, Offset(x, base - piso), Size(w, piso))
        drawRect(tabla, Offset(x, alero), Size(w, piso))
        for (i in 1..3) drawLine(vano.copy(alpha = 0.2f), Offset(x, alero + piso * i / 4f), Offset(x + w, alero + piso * i / 4f), 0.25f)
        ladoEnSombra(r, x, alero, w, h, 0.34f)
        // Abajo, el portón y dos ventanas con sus postigos; arriba, tres puertas a la galería.
        drawRect(if (encendida) luz else vano, Offset(x + w * 0.42f, base - piso * 0.8f), Size(w * 0.16f, piso * 0.8f))
        for (k in listOf(0.13f, 0.75f)) {
            drawRect(if (encendida) luz else vano, Offset(x + w * k, base - piso * 0.72f), Size(w * 0.1f, piso * 0.4f))
            drawRect(rojo, Offset(x + w * (k - 0.04f), base - piso * 0.72f), Size(w * 0.04f, piso * 0.4f)); drawRect(rojo, Offset(x + w * (k + 0.1f), base - piso * 0.72f), Size(w * 0.04f, piso * 0.4f))
        }
        for ((i, k) in listOf(0.15f, 0.455f, 0.76f).withIndex()) drawRect(if (encendida && i != 1) luz else vano, Offset(x + w * k, alero + piso * 0.26f), Size(w * 0.09f, piso * 0.74f))
        // La galería: el piso, los postes y el barandal.
        drawRect(palo, Offset(x - w * 0.05f, base - piso - 0.5f), Size(w * 1.1f, 0.9f))
        for (i in 0..5) { val px = x - w * 0.04f + w * 1.08f * i / 5f; drawLine(palo, Offset(px, base), Offset(px, alero), 0.6f) }
        drawLine(palo, Offset(x - w * 0.04f, base - piso * 1.36f), Offset(x + w * 1.04f, base - piso * 1.36f), 0.45f)
        // El tejado a cuatro aguas, de tejamanil: el faldón que mira al sol, más claro.
        val rh = h * 0.42f
        drawPath(poligono(Offset(x - w * 0.08f, alero), Offset(x + w * 1.08f, alero), Offset(x + w * 0.76f, alero - rh), Offset(x + w * 0.24f, alero - rh)), teja)
        drawPath(if (sol) poligono(Offset(x + w * 1.08f, alero), Offset(x + w * 0.76f, alero - rh), Offset(x + w * 0.8f, alero)) else poligono(Offset(x - w * 0.08f, alero), Offset(x + w * 0.24f, alero - rh), Offset(x + w * 0.2f, alero)), tejaLuz)
        for (i in 1..2) { val k = i / 3f; drawLine(vano.copy(alpha = 0.3f), Offset(x - w * 0.08f + w * 0.32f * k, alero - rh * k), Offset(x + w * 1.08f - w * 0.32f * k, alero - rh * k), 0.3f) }
        drawRect(r.color, Offset(x, alero), Size(w, 0.9f), alpha = 0.6f * r.contraste, blendMode = BlendMode.Multiply)
        // La escalera de fuera, del lado del sol, y la muestra, del otro.
        val dir = if (sol) 1f else -1f; val ex = if (sol) x + w * 1.04f else x - w * 0.04f
        drawLine(palo, Offset(ex + dir * w * 0.2f, base), Offset(ex, base - piso), 0.8f)
        drawLine(palo, Offset(ex + dir * w * 0.2f, base - piso * 0.4f), Offset(ex, base - piso * 1.36f), 0.4f)
        val mx = if (sol) x - w * 0.05f else x + w * 1.05f
        drawLine(palo, Offset(mx, base - piso * 1.7f), Offset(mx - dir * w * 0.12f, base - piso * 1.7f), 0.5f)
        drawRect(vano, Offset(mx - dir * w * 0.1f - 1f, base - piso * 1.66f), Size(2f, 2.2f)); drawCircle(l.tono(C.OCRE, lejos), 0.6f, Offset(mx - dir * w * 0.1f, base - piso * 1.66f + 1.1f))
        // Los barriles de la puerta.
        for (bx in listOf(0.3f, 0.36f, 0.64f)) { drawOval(l.tono(Color(0xFF8A6A44), lejos), Offset(x + w * bx, base - 2.2f), Size(1.5f, 2.2f)); drawLine(vano.copy(alpha = 0.6f), Offset(x + w * bx, base - 1.2f), Offset(x + w * bx + 1.5f, base - 1.2f), 0.3f) }
    }

    /** El almacén del muelle: largo y bajo, con el tejado de palma más grande del pueblo, el portón abierto y el pescante en el hastial. */
    private fun DrawScope.almacen(l: Luces, r: Rayo, x: Float, base: Float, w: Float, h: Float, lejos: Float, encendida: Boolean) {
        val tabla = l.tono(Color(0xFFA2805A), lejos); val vano = l.tono(Color(0xFF33281F), lejos); val hoja = l.tono(Color(0xFF7A5C3E), lejos); val palo = l.tono(Color(0xFF6A5038), lejos)
        val alero = base - h; val rh = h * 1.05f
        sombraEnElSuelo(r, x, base, w, h)
        // El cobertizo pegado a un costado.
        val cx = if (r.lat >= 0f) x - w * 0.16f else x + w
        drawRect(l.tono(Color(0xFFBCA27A), lejos), Offset(cx, base - h * 0.72f), Size(w * 0.16f, h * 0.72f))
        drawPath(if (r.lat >= 0f) poligono(Offset(cx - 0.6f, base - h * 0.7f), Offset(cx + w * 0.16f, base - h * 1.05f), Offset(cx + w * 0.16f, base - h * 0.86f), Offset(cx - 0.6f, base - h * 0.56f)) else poligono(Offset(cx, base - h * 1.05f), Offset(cx + w * 0.16f + 0.6f, base - h * 0.7f), Offset(cx + w * 0.16f + 0.6f, base - h * 0.56f), Offset(cx, base - h * 0.86f)), l.tono(Color(0xFF7A6652), lejos))
        drawRect(tabla, Offset(x, alero), Size(w, h))
        var tx = x + 1.6f
        while (tx < x + w) { drawLine(vano.copy(alpha = 0.2f), Offset(tx, alero), Offset(tx, base), 0.25f); tx += 1.6f }
        ladoEnSombra(r, x, alero, w, h, 0.3f)
        // El portón, abierto de par en par, y dos ventanucos.
        drawRect(if (encendida) lerp(vano, LUZ_DE_CASA, 0.6f) else vano, Offset(x + w * 0.3f, base - h * 0.84f), Size(w * 0.2f, h * 0.84f))
        drawRect(hoja, Offset(x + w * 0.23f, base - h * 0.84f), Size(w * 0.07f, h * 0.84f)); drawRect(hoja, Offset(x + w * 0.5f, base - h * 0.84f), Size(w * 0.07f, h * 0.84f))
        for (k in listOf(0.7f, 0.86f)) drawRect(vano, Offset(x + w * k, base - h * 0.7f), Size(w * 0.05f, h * 0.3f))
        // El tejado de palma: alto, con las hiladas de pencas y la cumbrera cosida.
        val paja = l.tono(Color(0xFFBCA068), lejos); val pajaLuz = l.tono(Color(0xFFD8C088), lejos); val pajaOsc = l.tono(Color(0xFF8A6E42), lejos)
        val tejado = poligono(Offset(x - w * 0.04f, alero + 0.5f), Offset(x + w * 1.04f, alero + 0.5f), Offset(x + w * 0.97f, alero - rh), Offset(x + w * 0.03f, alero - rh))
        drawPath(tejado, Brush.verticalGradient(listOf(pajaLuz, paja), alero - rh, alero))
        clipPath(tejado) {
            for (i in 1..3) drawLine(pajaOsc.copy(alpha = 0.45f), Offset(x - w * 0.04f, alero - rh * i / 4f), Offset(x + w * 1.04f, alero - rh * i / 4f), 0.35f)
            var sx = x; var i = 0
            while (sx < x + w * 1.04f) { drawLine(pajaOsc.copy(alpha = 0.3f), Offset(sx, alero - rh), Offset(sx + ((i % 3) - 1) * 0.5f, alero + 0.5f), 0.25f); sx += 1.3f; i++ }
        }
        drawLine(pajaOsc, Offset(x + w * 0.03f, alero - rh), Offset(x + w * 0.97f, alero - rh), 0.7f, StrokeCap.Round)
        drawRect(r.color, Offset(x, alero + 0.5f), Size(w, 0.8f), alpha = 0.6f * r.contraste, blendMode = BlendMode.Multiply)
        // El pescante, con su aparejo colgando, y la carga en la puerta: barriles y fardos.
        val gx = if (r.lat >= 0f) x + w * 1.04f else x - w * 0.04f; val dir = if (r.lat >= 0f) 1f else -1f
        drawLine(palo, Offset(gx - dir * 1f, alero - rh * 0.5f), Offset(gx + dir * 3f, alero - rh * 0.62f), 0.6f, StrokeCap.Round)
        drawLine(l.tono(C.CABO, lejos), Offset(gx + dir * 2.8f, alero - rh * 0.6f), Offset(gx + dir * 2.8f, base - 2.4f), 0.3f)
        drawRect(l.tono(C.LONA_OSC, lejos), Offset(gx + dir * 2.8f - 1f, base - 2.6f), Size(2f, 1.6f))
        for ((i, k) in listOf(0.58f, 0.63f, 0.605f).withIndex()) { val by = if (i == 2) base - 3.4f else base - 1.9f; drawOval(l.tono(Color(0xFF8A6A44), lejos), Offset(x + w * k, by), Size(1.4f, 1.9f)); drawLine(vano.copy(alpha = 0.6f), Offset(x + w * k, by + 0.9f), Offset(x + w * k + 1.4f, by + 0.9f), 0.25f) }
        drawRect(l.tono(C.LONA, lejos), Offset(x + w * 0.1f, base - 1.5f), Size(2.4f, 1.5f)); drawRect(l.tono(C.LONA_OSC, lejos), Offset(x + w * 0.13f, base - 2.7f), Size(2f, 1.2f))
    }

    /**
     * La iglesia quemada: los muros de piedra sin techo, el hastial con su ojo vacío, las vigas negras y la torre
     * desmochada. Es de la misma piedra que el fuerte.
     */
    private fun DrawScope.iglesia(l: Luces, r: Rayo, x: Float, base: Float, w: Float, h: Float, lejos: Float) {
        val piedra = l.tono(Color(0xFFD6CCB2), lejos); val piedraLuz = l.tono(Color(0xFFE6DEC6), lejos); val hueco = l.tono(Color(0xFF4A4238), lejos); val tizne = l.tono(Color(0xFF3A322C), lejos)
        val verde = l.tono(Color(0xFF5E8250), lejos)
        val sol = r.lat >= 0f
        sombraEnElSuelo(r, x, base, w, h)
        // La nave, de costado: el muro desmochado y sus tres ventanas de medio punto.
        val nave = poligono(listOf(Offset(x + w * 0.2f, base), Offset(x + w * 0.2f, base - h * 0.8f), Offset(x + w * 0.3f, base - h * 0.86f), Offset(x + w * 0.37f, base - h * 0.66f), Offset(x + w * 0.46f, base - h * 0.76f), Offset(x + w * 0.55f, base - h * 0.6f), Offset(x + w * 0.64f, base - h * 0.74f), Offset(x + w * 0.72f, base - h * 0.7f), Offset(x + w * 0.72f, base)))
        drawPath(nave, piedra)
        for (k in listOf(0.27f, 0.42f, 0.57f)) { val vw = w * 0.055f; drawRect(hueco, Offset(x + w * k, base - h * 0.5f), Size(vw, h * 0.28f)); drawCircle(hueco, vw / 2f, Offset(x + w * k + vw / 2f, base - h * 0.5f)) }
        // Las vigas quemadas que quedan cruzadas arriba.
        drawLine(tizne, Offset(x + w * 0.34f, base - h * 0.7f), Offset(x + w * 0.6f, base - h * 1.06f), 0.5f, StrokeCap.Round)
        drawLine(tizne, Offset(x + w * 0.5f, base - h * 0.66f), Offset(x + w * 0.74f, base - h * 1.0f), 0.45f, StrokeCap.Round)
        // El hastial, de frente: la puerta, el ojo y el remate donde iba la cruz.
        val gx = x + w * 0.68f; val gw = w * 0.32f
        val hastial = poligono(Offset(gx, base), Offset(gx, base - h * 0.84f), Offset(gx + gw / 2f, base - h * 1.4f), Offset(gx + gw, base - h * 0.84f), Offset(gx + gw, base))
        drawPath(hastial, piedraLuz)
        clipPath(hastial) { ladoEnSombra(r, gx, base - h * 1.4f, gw, h * 1.4f, 0.3f) }
        drawRect(hueco, Offset(gx + gw * 0.36f, base - h * 0.4f), Size(gw * 0.28f, h * 0.4f)); drawCircle(hueco, gw * 0.14f, Offset(gx + gw * 0.5f, base - h * 0.4f))
        drawCircle(hueco, gw * 0.13f, Offset(gx + gw * 0.5f, base - h * 0.88f))
        // La torre, a los pies de la nave: desmochada, con el hueco de la campana.
        val tw = w * 0.24f; val th = h * 1.62f
        val torre = poligono(listOf(Offset(x, base), Offset(x + tw * 0.04f, base - th * 0.9f), Offset(x + tw * 0.3f, base - th), Offset(x + tw * 0.5f, base - th * 0.92f), Offset(x + tw * 0.72f, base - th * 0.98f), Offset(x + tw * 0.96f, base - th * 0.84f), Offset(x + tw, base)))
        drawPath(torre, Brush.horizontalGradient(if (sol) listOf(mult(piedra, r.color, 0.7f * r.contraste), piedraLuz) else listOf(piedraLuz, mult(piedra, r.color, 0.7f * r.contraste)), x, x + tw))
        drawRect(hueco, Offset(x + tw * 0.36f, base - th * 0.74f), Size(tw * 0.28f, th * 0.16f)); drawCircle(hueco, tw * 0.14f, Offset(x + tw * 0.5f, base - th * 0.74f))
        drawRect(hueco, Offset(x + tw * 0.42f, base - th * 0.38f), Size(tw * 0.16f, th * 0.1f))
        drawLine(hueco.copy(alpha = 0.4f), Offset(x + tw * 0.03f, base - th * 0.52f), Offset(x + tw * 0.98f, base - th * 0.52f), 0.3f)
        // El tizne sobre los vanos y la maleza que ya crece en los muros.
        for (k in listOf(0.27f, 0.42f, 0.57f)) mancha(Offset(x + w * (k + 0.03f), base - h * 0.62f), w * 0.05f, h * 0.12f, tizne, 0.4f)
        for (p in listOf(Offset(x + w * 0.33f, base - h * 0.78f), Offset(x + w * 0.52f, base - h * 0.64f), Offset(x + tw * 0.2f, base - th * 0.95f), Offset(x + w * 0.7f, base - h * 0.74f))) drawOval(verde, p + Offset(-0.9f, -0.6f), Size(1.8f, 1.1f))
        for (p in listOf(Offset(x + w * 0.24f, base), Offset(x + w * 0.62f, base), Offset(x + w * 1.02f, base))) drawOval(verde, p + Offset(-1.4f, -1f), Size(2.8f, 1.4f))
    }

    /** El muelle: una pasarela de tablones sobre pilotes que sale del almacén hacia quien mira, con un bote amarrado. */
    private fun DrawScope.muelle(l: Luces, r: Rayo, az: Azar) {
        val a0 = Offset(169f, 140.6f); val a1 = Offset(174.6f, 140.4f); val b0 = Offset(181f, 150.6f); val b1 = Offset(190.6f, 150.4f)
        val tablon = l.tono(C.CUBIERTA, 0.24f); val canto = l.tono(C.MADERA_OSC, 0.24f); val pilote = l.tono(Color(0xFF4A3A2C), 0.24f)
        // La sombra de la pasarela en el agua y los reflejos de los pilotes.
        drawPath(poligono(a0 + Offset(0f, 0.8f), b0 + Offset(0f, 2.4f), b1 + Offset(0f, 2.4f), a1 + Offset(0f, 0.8f)), r.color, alpha = 0.3f + 0.4f * r.fuerza, blendMode = BlendMode.Multiply)
        reflejoRoto(b0.x, b1.x, b1.y + 2.6f, 6f, pilote, 0.5f, az, 0.9f)
        // Los pilotes: dos filas, más largos y gruesos cuanto más cerca.
        for (i in 0..4) {
            val k = i / 4f; val izq = Offset(a0.x + (b0.x - a0.x) * k, a0.y + (b0.y - a0.y) * k); val der = Offset(a1.x + (b1.x - a1.x) * k, a1.y + (b1.y - a1.y) * k)
            val largo = 0.8f + 2.6f * k; val grueso = 0.5f + 0.7f * k
            drawLine(pilote, izq + Offset(0.3f, 0f), izq + Offset(0.3f, largo), grueso); drawLine(pilote, der + Offset(-0.3f, 0f), der + Offset(-0.3f, largo), grueso)
        }
        // La pasarela, con las juntas de los tablones, y su canto.
        drawPath(poligono(a0, a1, b1, b0), tablon)
        for (i in 1..9) { val k = i / 10f; drawLine(canto.copy(alpha = 0.4f), Offset(a0.x + (b0.x - a0.x) * k, a0.y + (b0.y - a0.y) * k), Offset(a1.x + (b1.x - a1.x) * k, a1.y + (b1.y - a1.y) * k), 0.25f + 0.2f * k) }
        drawPath(poligono(b0, b1, b1 + Offset(0f, 1f), b0 + Offset(0f, 1f)), canto)
        drawPath(poligono(a0, b0, b0 + Offset(0f, 1f), a0 + Offset(0f, 0.4f)), canto)
        // Los norays, un barril y una aduja de cabo.
        for (k in listOf(0.5f, 0.94f)) { val p = Offset(a1.x + (b1.x - a1.x) * k - 0.6f, a1.y + (b1.y - a1.y) * k); drawLine(pilote, p, p + Offset(0f, -1.3f - k), 0.7f + 0.3f * k, StrokeCap.Round) }
        drawOval(l.tono(Color(0xFF8A6A44), 0.24f), Offset(180f, 145.4f), Size(2f, 2.6f)); drawLine(pilote.copy(alpha = 0.6f), Offset(180f, 146.7f), Offset(182f, 146.7f), 0.3f)
        drawOval(l.tono(C.CABO, 0.24f), Offset(183.6f, 148.4f), Size(2.4f, 1f), style = Stroke(0.4f))
        // El bote amarrado al costado.
        val bote = Offset(174f, 149.6f)
        reflejoRoto(bote.x - 4.4f, bote.x + 4.6f, bote.y + 0.6f, 3f, canto, 0.45f, az, 0.8f)
        drawPath(Path().apply { moveTo(bote.x - 5f, bote.y - 1.6f); lineTo(bote.x + 5.4f, bote.y - 1.8f); cubicTo(bote.x + 4.2f, bote.y + 0.8f, bote.x - 3.6f, bote.y + 0.8f, bote.x - 5f, bote.y - 1.6f); close() }, l.tono(Color(0xFF6A5038), 0.22f))
        drawLine(l.tono(Color(0xFFD8C8A0), 0.22f), Offset(bote.x - 4.8f, bote.y - 1.6f), Offset(bote.x + 5.2f, bote.y - 1.8f), 0.4f)
        drawLine(l.tono(C.CABO, 0.22f), Offset(bote.x + 5.2f, bote.y - 1.8f), Offset(181.6f, 150.2f), 0.3f)
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
        for ((i, x) in HOGUERAS.withIndex()) {
            val pie = Offset(x, alturaDeLaOrilla(x) - playa(x) * 0.45f); val k = 0.8f + 0.9f * cerca(pie.y)
            humo(l, pie + Offset(0f, -1f), (22f + 4f * i) * k, 6f * k, reloj, -0.25f, 0.2f, 0.26f, 60 + i, 8, 0.7f)
            if (l.noche > 0.3f && despiertos) { llama(pie, 2.6f * k, reloj, 60 + i); resplandor(pie + Offset(0f, -1f), 9f * k, 0.5f * l.noche); reflejo(Offset(pie.x, alturaDeLaOrilla(x) + 1f), 10f, LLAMA, 0.5f * l.noche, reloj, 60 + i) }
        }
        if (l.noche > 0.4f && despiertos) BARCOS.forEachIndexed { i, b ->
            val p = b[0] as Offset; val largo = b[1] as Float; val lado = if (b[3] as Boolean) -1f else 1f
            reflejo(p + Offset(lado * largo * 0.46f, 0.5f), 6f + largo * 0.16f, LUZ_DE_CASA, 0.55f * l.noche, reloj, 70 + i)
        }
    }
}
