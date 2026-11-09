package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Romano: la domus de mármol, pórfido y oro, con la madera de haya y nogal de sus muebles (la misma casa que su biblioteca,
 * su Ideario y su Hall).
 * - Social: el triclinio visto desde arriba: tres lechos en U con su colchón de pórfido, sus franjas de oro, un cojín por
 *   comensal y el respaldo de bronce; la mesa redonda al centro, tenue, con su anillo de teselas, y el umbral de mosaico
 *   blanco y negro en el lado abierto.
 * - Mapas: la mesa del agrimensor: el tablero de haya con sus vetas, las patas de garra de bronce, la groma apoyada a la
 *   izquierda con sus plomadas, el compás y el estilo a la derecha y el rollo abajo, con un trozo desenrollado.
 * - Otras vidas: un muro de opus reticulatum con hiladas de ladrillo y cinco hornacinas de medio punto, cada una con su
 *   arquivolta de dovelas, la clave de oro y su repisa de mármol, sobre un zócalo de travertino.
 * - Íconos: el templo con frontón y seis columnas, la tabula de cera que raspa un denario y los marcadores de teselas
 *   unidos por una vía.
 * - Notas: la tabula de cera grande con su marco de haya y las rayas de lo ya borrado; cada nota es una tablilla con su
 *   marco de madera y, según el tipo, una bulla de oro, una ramita de laurel, una tesela de pórfido o el estilo clavado.
 */
internal object VidaRomano : DisenoVida {
    private val HAYA = Color(0xFFCDA670)
    private val HAYA_LUZ = Color(0xFFE2C48E)
    private val HAYA_VETA = Color(0xFF7A5630)
    private val NOGAL = Color(0xFF5E3C22)
    private val NOGAL_LUZ = Color(0xFF8C6340)
    private val CERA_NEGRA = Color(0xFF2E241C)
    private val CERA_MIEL = Color(0xFFDDCA9C)
    private val CERA_RAYA = Color(0xFFB59A70)
    /** La cera de las tablillas de día: miel clara, para que la tinta se lea con holgura. */
    private val CERA_CLARA = Color(0xFFE6D6AC)
    private val LINO = Color(0xFFEDE4CC)
    private val NEGRO = Color(0xFF1E1714)
    private val BRONCE = Color(0xFFB98B4A)
    private val BRONCE_OSC = Color(0xFF6E4E22)
    private val BRONCE_LUZ = Color(0xFFE0B878)
    private val PORFIDO = Color(0xFF6E2232)
    private val MARMOL = Color(0xFFEDE8DE)
    private val TRAVERTINO = Color(0xFFD3C29E)
    private val TRAVERTINO_OSC = Color(0xFFA08C66)
    /** La toba volcánica de los rombos del opus reticulatum. */
    private val TOBA = Color(0xFFC9B48A)
    private val LADRILLO = Color(0xFFA0583A)
    private val LAUREL = Color(0xFF5E6B3A)
    private val ORO = Color(0xFFD4AF37)
    private val ORO_OSC = Color(0xFF8E6A1F)
    private val PAPIRO = Color(0xFFE9D9A9)
    private val PAPIRO_OSC = Color(0xFFB59B62)

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** Un número estable de 0 a 1 (las irregularidades de cada cosa, siempre las mismas). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** Un material del tema a la luz de la tarjeta: de noche se hunde hacia el basalto del fondo. */
    private fun t(c: Color, e: EstadoMueble, oscuro: Boolean, extra: Float = 0f): Color = mez(c, e.fondo, (if (oscuro) 0.42f else 0.06f) + extra)

    /** El oro de los filetes: el oro viejo de día y el acento (oro) de noche. */
    private fun oro(e: EstadoMueble, oscuro: Boolean): Color = if (oscuro) e.acento else ORO_OSC

    // ---------------------------------------------------------------- Social: el triclinio

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        // El piso de la sala, apenas marcado, para que el grafo se lea encima.
        drawRect(e.tinta.copy(alpha = if (oscuro) 0.03f else 0.022f), Offset.Zero, size)
        // La mesa redonda (mensa) al centro, tenue: el tablero, su moldura y un anillo de teselas alrededor.
        val c = Offset(w / 2, h * 0.47f)
        val rx = h * 0.36f; val ry = h * 0.29f
        drawOval(e.acento.copy(alpha = 0.045f), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
        drawOval(e.acento.copy(alpha = 0.16f), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2), style = Stroke(0.7f * u))
        drawOval(e.acento.copy(alpha = 0.09f), Offset(c.x - rx * 0.86f, c.y - ry * 0.86f), Size(rx * 1.72f, ry * 1.72f), style = Stroke(0.5f * u))
        val n = 40
        for (k in 0 until n) {
            val a = k * 2.0 * Math.PI / n
            val p = Offset(c.x + rx * 1.2f * cos(a).toFloat(), c.y + ry * 1.24f * sin(a).toFloat())
            drawRect(e.tinta.copy(alpha = 0.08f), Offset(p.x - 0.8f * u, p.y - 0.8f * u), Size(1.6f * u, 1.6f * u))
        }
        // El umbral de mosaico en el lado abierto (arriba), por donde entran los que sirven.
        mosaico(Rect(w * 0.14f, h * 0.072f, w * 0.86f, h * 0.122f), e, oscuro)
        // Los tres lechos en U: izquierdo, derecho y el del fondo (abajo), con el respaldo en una punta.
        lecho(Rect(w * 0.012f, h * 0.06f, w * 0.106f, h * 0.985f), true, e, oscuro)
        lecho(Rect(w * 0.894f, h * 0.06f, w * 0.988f, h * 0.985f), true, e, oscuro)
        lecho(Rect(w * 0.118f, h * 0.855f, w * 0.882f, h * 0.985f), false, e, oscuro)
    }

    /**
     * Un lecho de triclinio visto desde arriba: el armazón de nogal, el colchón de pórfido con dos franjas de oro (clavi),
     * tres cojines de lino (uno por comensal) y el respaldo curvo de bronce (fulcrum) en la cabecera: arriba en los lechos
     * de los lados, a la izquierda en el del fondo.
     */
    private fun DrawScope.lecho(r: Rect, vertical: Boolean, e: EstadoMueble, oscuro: Boolean) {
        val u = size.minDimension / 100f
        val corto = if (vertical) r.width else r.height
        val rad = CornerRadius(corto * 0.18f)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.08f), Offset(r.left + 0.6f * u, r.top + 0.9f * u), r.size, rad)
        drawRoundRect(t(NOGAL_LUZ, e, oscuro), r.topLeft, r.size, rad)
        val m = corto * 0.13f
        val col = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
        drawRoundRect(t(PORFIDO, e, oscuro, if (oscuro) 0.05f else 0.12f), col.topLeft, col.size, CornerRadius(corto * 0.14f))
        val franja = oro(e, oscuro).copy(alpha = 0.75f)
        if (vertical) listOf(0.3f, 0.7f).forEach { f ->
            val x = col.left + col.width * f
            drawLine(franja, Offset(x, col.top + m), Offset(x, col.bottom - m), 0.5f * u)
        } else listOf(0.3f, 0.7f).forEach { f ->
            val y = col.top + col.height * f
            drawLine(franja, Offset(col.left + m, y), Offset(col.right - m, y), 0.5f * u)
        }
        // Los tres cojines.
        val largo = if (vertical) col.height else col.width
        val cojin = t(LINO, e, oscuro, if (oscuro) 0.08f else 0f)
        val costura = t(CERA_RAYA, e, oscuro)
        val a = corto * 0.34f; val b = corto * 0.21f
        for (k in 0 until 3) {
            val p = (k + 0.5f) / 3f
            val cc = if (vertical) Offset(col.center.x, col.top + largo * p) else Offset(col.left + largo * p, col.center.y)
            val tl = if (vertical) Offset(cc.x - a, cc.y - b) else Offset(cc.x - b, cc.y - a)
            val sz = if (vertical) Size(a * 2, b * 2) else Size(b * 2, a * 2)
            drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.1f), Offset(tl.x + 0.4f * u, tl.y + 0.6f * u), sz, CornerRadius(b))
            drawRoundRect(cojin, tl, sz, CornerRadius(b))
            drawRoundRect(costura, tl, sz, CornerRadius(b), style = Stroke(0.4f * u))
        }
        // El respaldo de bronce, con un medallón en cada punta.
        val bronce = t(BRONCE, e, oscuro)
        val g = corto * 0.12f
        if (vertical) {
            val y = r.top + m * 0.75f
            drawLine(bronce, Offset(r.left + m, y), Offset(r.right - m, y), g, StrokeCap.Round)
            listOf(r.left + m, r.right - m).forEach { x -> drawCircle(t(BRONCE_LUZ, e, oscuro), g * 0.75f, Offset(x, y)) }
        } else {
            val x = r.left + m * 0.75f
            drawLine(bronce, Offset(x, r.top + m), Offset(x, r.bottom - m), g, StrokeCap.Round)
            listOf(r.top + m, r.bottom - m).forEach { y -> drawCircle(t(BRONCE_LUZ, e, oscuro), g * 0.75f, Offset(x, y)) }
        }
    }

    /** Un umbral de mosaico: cenefas del acento arriba y abajo y dos hileras de teselas en ajedrez, con su junta. */
    private fun DrawScope.mosaico(r: Rect, e: EstadoMueble, oscuro: Boolean) {
        val lado = r.height / 4f
        if (lado < 0.5f) return
        drawRect(e.tinta.copy(alpha = 0.04f), r.topLeft, r.size)
        val cenefa = e.acento.copy(alpha = if (oscuro) 0.32f else 0.38f)
        drawRect(cenefa, r.topLeft, Size(r.width, lado * 0.8f))
        drawRect(cenefa, Offset(r.left, r.bottom - lado * 0.8f), Size(r.width, lado * 0.8f))
        val negra = e.tinta.copy(alpha = if (oscuro) 0.3f else 0.36f)
        val cols = (r.width / lado).toInt().coerceIn(1, 220)
        val paso = r.width / cols
        for (i in 0 until cols) for (j in 0..1) if ((i + j) % 2 == 0) {
            drawRect(negra, Offset(r.left + i * paso + paso * 0.08f, r.top + lado * (1 + j) + lado * 0.08f), Size(paso * 0.84f, lado * 0.84f))
        }
    }

    // ---------------------------------------------------------------- Mapas: la mesa del agrimensor

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val mapa = Rect(w * 0.06f, h * 0.1f, w * 0.94f, h * 0.9f)
        // El tablero de haya, con su sombra, sus vetas y el canto claro.
        val tab = Rect(w * 0.012f, h * 0.035f, w * 0.988f, h * 0.935f)
        val veta = t(HAYA_VETA, e, oscuro)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(tab.left + u, tab.top + 1.5f * u), tab.size)
        drawRect(t(HAYA, e, oscuro, if (oscuro) 0f else 0.1f), tab.topLeft, tab.size)
        for (k in 0 until 14) {
            val y = tab.top + tab.height * (k + azar(e.semilla, k)) / 14f
            val a = (0.6f + 1.4f * azar(e.semilla, k + 50)) * u
            val p = Path().apply {
                moveTo(tab.left, y)
                cubicTo(tab.left + tab.width * 0.3f, y - a, tab.left + tab.width * 0.6f, y + a, tab.right, y + a * 0.4f)
            }
            drawPath(p, veta.copy(alpha = 0.22f), style = Stroke(0.45f * u))
        }
        drawRect(veta.copy(alpha = 0.5f), tab.topLeft, tab.size, style = Stroke(0.8f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.3f), tab.topLeft, Offset(tab.right, tab.top), 0.8f * u)
        // Donde va el mapa, el tablero se calma bajo una hoja de pergamino tenue.
        drawRect(mez(e.fondo, PAPIRO, if (oscuro) 0.05f else 0.25f).copy(alpha = 0.85f), mapa.topLeft, mapa.size)
        drawRect(t(PAPIRO_OSC, e, oscuro).copy(alpha = 0.35f), mapa.topLeft, mapa.size, style = Stroke(0.5f * u))
        // Las patas de garra (trapezóforos) de bronce que asoman bajo el tablero.
        val bronce = t(BRONCE, e, oscuro); val bronceOsc = t(BRONCE_OSC, e, oscuro)
        listOf(w * 0.14f, w * 0.86f).forEach { x ->
            val y0 = tab.bottom; val y1 = h
            val pata = Path().apply {
                moveTo(x - 2.4f * u, y0); lineTo(x + 2.4f * u, y0)
                quadraticTo(x + 1.4f * u, (y0 + y1) / 2, x + 3f * u, y1 - 1.1f * u)
                lineTo(x - 3f * u, y1 - 1.1f * u)
                quadraticTo(x - 1.4f * u, (y0 + y1) / 2, x - 2.4f * u, y0); close()
            }
            drawPath(pata, bronce)
            for (d in 0 until 3) drawCircle(bronceOsc, 1.05f * u, Offset(x - 2.1f * u + d * 2.1f * u, y1 - 1.05f * u))
        }
        // La groma apoyada a la izquierda: el asta, el brazo y la cruz con sus plomadas colgando.
        val ax = w * 0.034f
        drawLine(t(NOGAL, e, oscuro), Offset(ax, h * 0.2f), Offset(ax, h * 0.97f), 1.1f * u, StrokeCap.Round)
        val cruz = Offset(ax + w * 0.012f, h * 0.15f)
        drawLine(bronce, Offset(ax, h * 0.22f), cruz, 0.8f * u, StrokeCap.Round)
        val brazo = w * 0.028f
        drawLine(bronce, Offset(cruz.x - brazo, cruz.y), Offset(cruz.x + brazo, cruz.y), 0.9f * u, StrokeCap.Round)
        drawLine(bronce, Offset(cruz.x - brazo * 0.35f, cruz.y - h * 0.025f), Offset(cruz.x + brazo * 0.35f, cruz.y + h * 0.025f), 0.9f * u, StrokeCap.Round)
        drawCircle(bronceOsc, 1f * u, cruz)
        val hilo = e.tinta.copy(alpha = 0.4f)
        listOf(
            Triple(cruz.x - brazo, cruz.y, h * 0.13f),
            Triple(cruz.x + brazo, cruz.y, h * 0.11f),
            Triple(cruz.x - brazo * 0.35f, cruz.y - h * 0.025f, h * 0.07f),
            Triple(cruz.x + brazo * 0.35f, cruz.y + h * 0.025f, h * 0.15f),
        ).forEach { (x, y, largo) ->
            drawLine(hilo, Offset(x, y), Offset(x, y + largo), 0.35f * u)
            plomada(Offset(x, y + largo), 1.2f * u, bronceOsc)
        }
        // El compás de bronce abierto y el estilo, a la derecha.
        val pv = Offset(w * 0.968f, h * 0.2f)
        drawLine(bronce, pv, Offset(w * 0.952f, h * 0.52f), 0.9f * u, StrokeCap.Round)
        drawLine(bronce, pv, Offset(w * 0.984f, h * 0.5f), 0.9f * u, StrokeCap.Round)
        drawCircle(bronceOsc, 1.2f * u, pv)
        drawLine(bronceOsc, Offset(w * 0.962f, h * 0.6f), Offset(w * 0.976f, h * 0.86f), 0.7f * u, StrokeCap.Round)
        drawLine(bronce, Offset(w * 0.974f, h * 0.82f), Offset(w * 0.977f, h * 0.87f), 1.4f * u, StrokeCap.Round)
        // El rollo sobre el borde de abajo, con sus umbilicos y un trozo desenrollado con renglones.
        val ry0 = h * 0.902f; val ry1 = h * 0.944f; val rh = ry1 - ry0
        val papiro = t(PAPIRO, e, oscuro); val papiroOsc = t(PAPIRO_OSC, e, oscuro)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(w * 0.4f, ry0 + rh * 0.3f), Size(w * 0.4f, rh * 0.85f))
        drawRect(papiro, Offset(w * 0.4f, ry0 + rh * 0.15f), Size(w * 0.17f, rh * 0.7f))
        for (k in 0 until 3) {
            val y = ry0 + rh * (0.32f + 0.18f * k)
            drawLine(papiroOsc, Offset(w * 0.415f, y), Offset(w * 0.55f, y), 0.35f * u)
        }
        drawRoundRect(papiro, Offset(w * 0.56f, ry0), Size(w * 0.24f, rh), CornerRadius(rh / 2))
        drawLine(papiroOsc, Offset(w * 0.56f + rh / 2, ry1 - rh * 0.25f), Offset(w * 0.8f - rh / 2, ry1 - rh * 0.25f), 0.5f * u)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.4f), Offset(w * 0.56f + rh / 2, ry0 + rh * 0.25f), Offset(w * 0.8f - rh / 2, ry0 + rh * 0.25f), 0.5f * u)
        listOf(w * 0.56f, w * 0.8f).forEach { x ->
            drawCircle(bronce, rh * 0.42f, Offset(x, ry0 + rh / 2))
            drawCircle(bronceOsc, rh * 0.42f, Offset(x, ry0 + rh / 2), style = Stroke(0.4f * u))
        }
    }

    /** Una plomada: la gota de plomo colgada de su hilo. */
    private fun DrawScope.plomada(arriba: Offset, r: Float, color: Color) {
        val p = Path().apply {
            moveTo(arriba.x, arriba.y)
            quadraticTo(arriba.x + r * 1.1f, arriba.y + r * 1.4f, arriba.x, arriba.y + r * 2.4f)
            quadraticTo(arriba.x - r * 1.1f, arriba.y + r * 1.4f, arriba.x, arriba.y)
            close()
        }
        drawPath(p, color)
    }

    // ---------------------------------------------------------------- Otras vidas: las hornacinas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        // El muro de opus reticulatum: la toba en rombos, con sus juntas a 45°.
        drawRect(mez(TOBA, e.fondo, if (oscuro) 0.76f else 0.45f), Offset.Zero, size)
        val junta = e.tinta.copy(alpha = if (oscuro) 0.12f else 0.1f)
        val paso = 5f * u
        var d = -h
        while (d < w + h) {
            drawLine(junta, Offset(d, 0f), Offset(d + h, h), 0.35f * u)
            drawLine(junta, Offset(d + h, 0f), Offset(d, h), 0.35f * u)
            d += paso
        }
        // Las hiladas de ladrillo arriba (opus mixtum).
        hiladas(Rect(0f, 0f, w, h * 0.06f), e, oscuro)
        // Las cinco hornacinas, una por medalla.
        val paso5 = w * 0.184f
        val r = minOf(paso5 * 0.86f, h * 0.5f) / 2f
        for (i in 0 until 5) hornacina(w / 2 + (i - 2) * paso5, r, h * 0.085f, h * 0.8f, e, oscuro)
        // El zócalo de travertino, con su moldura y sus poros.
        val zocalo = h * 0.86f
        drawRect(t(TRAVERTINO, e, oscuro, if (oscuro) 0.12f else 0.1f), Offset(0f, zocalo), Size(w, h - zocalo))
        drawRect(t(TRAVERTINO_OSC, e, oscuro), Offset(0f, zocalo), Size(w, 0.9f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.35f), Offset(0f, zocalo + 1.4f * u), Offset(w, zocalo + 1.4f * u), 0.5f * u)
        val poro = t(TRAVERTINO_OSC, e, oscuro).copy(alpha = 0.6f)
        for (k in 0 until 26) {
            val x = w * azar(e.semilla, k); val y = zocalo + 2.5f * u + (h - zocalo - 4f * u) * azar(e.semilla, k + 40)
            drawOval(poro, Offset(x, y), Size(u * (1f + 2f * azar(e.semilla, k + 80)), u * 0.5f))
        }
    }

    /** Dos hiladas de ladrillo con sus juntas a matajunta. */
    private fun DrawScope.hiladas(r: Rect, e: EstadoMueble, oscuro: Boolean) {
        val u = size.minDimension / 100f
        drawRect(mez(LADRILLO, e.fondo, if (oscuro) 0.6f else 0.3f), r.topLeft, r.size)
        val junta = mez(e.fondo, TOBA, 0.3f).copy(alpha = 0.7f)
        val filas = 2; val alto = r.height / filas
        if (alto < 0.5f) return
        for (f in 0..filas) drawLine(junta, Offset(r.left, r.top + f * alto), Offset(r.right, r.top + f * alto), 0.4f * u)
        val largo = alto * 3.6f
        for (f in 0 until filas) {
            var x = r.left + (if (f % 2 == 0) 0f else largo / 2)
            while (x < r.right) {
                drawLine(junta, Offset(x, r.top + f * alto), Offset(x, r.top + (f + 1) * alto), 0.4f * u)
                x += largo
            }
        }
    }

    /**
     * Una hornacina de medio punto centrada en [cx], de radio [r], con la bóveda en [arriba] y la repisa en [repisa]: el
     * fondo hundido (más sombra bajo la bóveda), las jambas y la arquivolta de ladrillo con sus dovelas, la clave de oro y
     * la repisa de mármol que sobresale.
     */
    private fun DrawScope.hornacina(cx: Float, r: Float, arriba: Float, repisa: Float, e: EstadoMueble, oscuro: Boolean) {
        val u = size.minDimension / 100f
        val hueco = Path().apply {
            moveTo(cx - r, repisa); lineTo(cx - r, arriba + r)
            arcTo(Rect(cx - r, arriba, cx + r, arriba + 2 * r), 180f, 180f, false)
            lineTo(cx + r, repisa); close()
        }
        drawPath(hueco, Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.42f else 0.13f), Color.Black.copy(alpha = if (oscuro) 0.24f else 0.04f)), arriba, repisa))
        drawPath(hueco, PORFIDO.copy(alpha = if (oscuro) 0.08f else 0.05f))
        // Las jambas y la arquivolta, de ladrillo, con sus juntas.
        val g = 2.6f * u
        val ladrillo = mez(LADRILLO, e.fondo, if (oscuro) 0.55f else 0.25f)
        val junta = mez(e.fondo, TOBA, 0.3f)
        drawRect(ladrillo, Offset(cx - r - g, arriba + r), Size(g, repisa - arriba - r))
        drawRect(ladrillo, Offset(cx + r, arriba + r), Size(g, repisa - arriba - r))
        var y = arriba + r + 3f * u
        while (y < repisa) {
            drawLine(junta, Offset(cx - r - g, y), Offset(cx - r, y), 0.35f * u)
            drawLine(junta, Offset(cx + r, y), Offset(cx + r + g, y), 0.35f * u)
            y += 3f * u
        }
        drawArc(ladrillo, 180f, 180f, false, Offset(cx - r - g / 2, arriba - g / 2), Size(2 * r + g, 2 * r + g), style = Stroke(g))
        for (k in 1 until 9) {
            val a = Math.toRadians(180.0 + k * 20.0)
            val ca = cos(a).toFloat(); val sa = sin(a).toFloat()
            drawLine(junta, Offset(cx + ca * r, arriba + r + sa * r), Offset(cx + ca * (r + g), arriba + r + sa * (r + g)), 0.4f * u)
        }
        drawRect(oro(e, oscuro).copy(alpha = 0.85f), Offset(cx - g * 0.45f, arriba - g * 1.05f), Size(g * 0.9f, g * 1.3f))
        // La repisa de mármol, con su sombra.
        val sr = Rect(cx - r - g * 1.4f, repisa, cx + r + g * 1.4f, repisa + 2.4f * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.12f), Offset(sr.left + 0.5f * u, sr.bottom), Size(sr.width - u, 1.2f * u))
        drawRect(t(MARMOL, e, oscuro, if (oscuro) 0.1f else 0f), sr.topLeft, sr.size)
        drawRect(t(TRAVERTINO_OSC, e, oscuro).copy(alpha = 0.6f), sr.topLeft, sr.size, style = Stroke(0.4f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.5f), sr.topLeft, Offset(sr.right, sr.top), 0.5f * u)
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val c = Offset(size.width / 2, size.height / 2)
        val a = size.height * 0.4f
        when (id) {
            "unesco" -> templo(c, a, e)
            "rascar" -> tabulaRascada(c, a, e)
            else -> teselas(c, a, e)
        }
    }

    /** El templo: la escalinata, seis columnas con su capitel, el entablamento y el frontón con su tímpano y sus acroteras. */
    private fun DrawScope.templo(c: Offset, a: Float, e: EstadoIcono) {
        val base = c.y + a * 0.95f
        drawRect(e.c2, Offset(c.x - a * 1.25f, base - a * 0.13f), Size(a * 2.5f, a * 0.13f))
        drawRect(e.c2.copy(alpha = 0.8f), Offset(c.x - a * 1.13f, base - a * 0.26f), Size(a * 2.26f, a * 0.13f))
        val estilobato = base - a * 0.26f
        val arquitrabe = c.y - a * 0.3f
        for (i in 0 until 6) {
            val x = c.x - a * 0.92f + i * a * 1.84f / 5f
            drawRect(e.c1, Offset(x - a * 0.075f, arquitrabe), Size(a * 0.15f, estilobato - arquitrabe))
            drawRect(e.c1, Offset(x - a * 0.12f, arquitrabe), Size(a * 0.24f, a * 0.06f))
            drawRect(e.c1, Offset(x - a * 0.11f, estilobato - a * 0.05f), Size(a * 0.22f, a * 0.05f))
        }
        drawRect(e.c1, Offset(c.x - a * 1.08f, arquitrabe - a * 0.16f), Size(a * 2.16f, a * 0.16f))
        drawLine(e.fondo.copy(alpha = 0.5f), Offset(c.x - a * 1.04f, arquitrabe - a * 0.08f), Offset(c.x + a * 1.04f, arquitrabe - a * 0.08f), a * 0.025f)
        val fb = arquitrabe - a * 0.2f
        drawRect(e.c1, Offset(c.x - a * 1.15f, fb), Size(a * 2.3f, a * 0.05f))
        val apice = Offset(c.x, c.y - a * 0.95f)
        drawPath(Path().apply { moveTo(c.x - a * 1.15f, fb); lineTo(apice.x, apice.y); lineTo(c.x + a * 1.15f, fb); close() }, e.c1)
        drawPath(Path().apply { moveTo(c.x - a * 0.8f, fb - a * 0.05f); lineTo(c.x, c.y - a * 0.79f); lineTo(c.x + a * 0.8f, fb - a * 0.05f); close() }, e.fondo.copy(alpha = 0.55f))
        drawCircle(e.c2, a * 0.075f, Offset(c.x, fb - a * 0.14f))
        listOf(Offset(c.x - a * 1.15f, fb), apice, Offset(c.x + a * 1.15f, fb)).forEach { p -> drawCircle(e.c2, a * 0.07f, Offset(p.x, p.y - a * 0.06f)) }
    }

    /** La tabula de cera con su marco: un denario ya rascó una franja en zigzag que deja ver el color del mapa. */
    private fun DrawScope.tabulaRascada(c: Offset, a: Float, e: EstadoIcono) {
        val g = 1.6f * density
        val marco = Rect(c.x - a * 1.35f, c.y - a * 0.78f, c.x + a * 1.2f, c.y + a * 0.9f)
        drawRoundRect(e.c2, marco.topLeft, marco.size, CornerRadius(a * 0.1f))
        val cera = Rect(marco.left + a * 0.16f, marco.top + a * 0.16f, marco.right - a * 0.16f, marco.bottom - a * 0.16f)
        drawRect(mez(e.fondo, e.tinta, 0.22f), cera.topLeft, cera.size)
        val raspa = Path().apply {
            moveTo(cera.left + a * 0.15f, cera.bottom - a * 0.3f)
            lineTo(cera.left + a * 0.6f, cera.top + a * 0.35f)
            lineTo(cera.left + a * 0.95f, cera.bottom - a * 0.28f)
            lineTo(cera.left + a * 1.4f, cera.top + a * 0.32f)
        }
        clipRect(cera.left, cera.top, cera.right, cera.bottom) {
            drawPath(raspa, e.c1, style = Stroke(a * 0.3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        // Las rayas del estilo en la cera que queda.
        for (k in 0 until 3) {
            val y = cera.top + a * (0.32f + 0.3f * k)
            drawLine(e.tinta.copy(alpha = 0.35f), Offset(cera.right - a * 0.7f, y), Offset(cera.right - a * 0.15f, y), g * 0.6f, StrokeCap.Round)
        }
        // El denario, con la cabeza laureada de perfil.
        val mc = Offset(cera.left + a * 1.58f, cera.top + a * 0.14f)
        val mr = a * 0.38f
        drawCircle(Color.Black.copy(alpha = 0.18f), mr, Offset(mc.x + g * 0.6f, mc.y + g * 0.9f))
        drawCircle(e.c1, mr, mc)
        drawCircle(e.fondo.copy(alpha = 0.6f), mr * 0.8f, mc, style = Stroke(g * 0.6f))
        drawOval(e.fondo.copy(alpha = 0.55f), Offset(mc.x - mr * 0.28f, mc.y - mr * 0.38f), Size(mr * 0.56f, mr * 0.72f))
        drawArc(e.c2, 200f, 160f, false, Offset(mc.x - mr * 0.34f, mc.y - mr * 0.46f), Size(mr * 0.68f, mr * 0.6f), style = Stroke(g * 0.7f))
    }

    /** Tus lugares: tres marcadores de teselas unidos por una vía empedrada a trazos. */
    private fun DrawScope.teselas(c: Offset, a: Float, e: EstadoIcono) {
        val g = 1.6f * density
        val p1 = Offset(c.x - a * 1.25f, c.y + a * 0.45f)
        val p2 = Offset(c.x - a * 0.1f, c.y - a * 0.35f)
        val p3 = Offset(c.x + a * 1.2f, c.y + a * 0.25f)
        val via = Path().apply {
            moveTo(p1.x, p1.y)
            quadraticTo(c.x - a * 0.8f, c.y - a * 0.4f, p2.x, p2.y)
            quadraticTo(c.x + a * 0.6f, c.y - a * 0.3f, p3.x, p3.y)
        }
        drawPath(via, e.c2.copy(alpha = 0.7f), style = Stroke(g, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 1.6f, g * 1.4f))))
        listOf(p1 to 1f, p2 to 1.2f, p3 to 0.9f).forEach { (p, k) -> marcaTesela(p, a * 0.2f * k, e) }
    }

    /** Un marcador de teselas: cuatro cuadritos del acento en rombo, con la tesela del centro en el secundario, sobre su sombra. */
    private fun DrawScope.marcaTesela(p: Offset, s: Float, e: EstadoIcono) {
        drawOval(Color.Black.copy(alpha = 0.15f), Offset(p.x - s * 1.3f, p.y + s * 1.1f), Size(s * 2.6f, s * 0.7f))
        val l = s * 0.92f; val hu = s * 0.12f
        rotate(45f, p) {
            for (dx in listOf(-1, 1)) for (dy in listOf(-1, 1)) {
                drawRect(e.c1, Offset(p.x + (if (dx < 0) -l - hu else hu), p.y + (if (dy < 0) -l - hu else hu)), Size(l, l))
            }
            drawRect(e.c2, Offset(p.x - l * 0.35f, p.y - l * 0.35f), Size(l * 0.7f, l * 0.7f))
        }
    }

    // ---------------------------------------------------------------- Notas: la tabula de cera y sus tablillas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        // El marco de haya, con su sombra y sus vetas.
        val rad = CornerRadius(2.5f * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(0.6f * u, 1.2f * u), Size(w - 1.2f * u, h - 1.2f * u), rad)
        drawRoundRect(Brush.verticalGradient(listOf(t(HAYA_LUZ, e, oscuro), t(HAYA, e, oscuro), t(mez(HAYA, HAYA_VETA, 0.3f), e, oscuro)), 0f, h), Offset.Zero, Size(w, h - 0.6f * u), rad)
        val veta = t(HAYA_VETA, e, oscuro).copy(alpha = 0.3f)
        for (k in 0 until 10) {
            val y = h * (k + azar(e.semilla, k + 7)) / 10f
            val a = (0.5f + 1.5f * azar(e.semilla, k + 60)) * u
            drawPath(Path().apply { moveTo(0f, y); cubicTo(w * 0.3f, y - a, w * 0.65f, y + a, w, y - a * 0.3f) }, veta, style = Stroke(0.45f * u))
        }
        // El campo de cera, rebajado en el marco: sombra arriba e izquierda, luz abajo.
        val cera = Rect(w * 0.035f, h * 0.06f, w * 0.965f, h * 0.94f)
        drawRect(if (oscuro) mez(CERA_NEGRA, e.fondo, 0.2f) else mez(CERA_MIEL, e.fondo, 0.15f), cera.topLeft, cera.size)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.16f), cera.topLeft, Size(cera.width, 1.2f * u))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.1f), cera.topLeft, Size(1.2f * u, cera.height))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.3f), Offset(cera.left, cera.bottom), Offset(cera.right, cera.bottom), 0.6f * u)
        // Lo que se escribió y se alisó: renglones de rayas finas en la cera.
        val raya = if (oscuro) CERA_RAYA.copy(alpha = 0.16f) else NEGRO.copy(alpha = 0.08f)
        var y = cera.top + 5f * u; var fila = 0
        while (y < cera.bottom - 3f * u && fila < 30) {
            var x = cera.left + 3f * u + 4f * u * azar(e.semilla, fila)
            var k = 0
            while (x < cera.right - 6f * u && k < 16) {
                val l = (5f + 12f * azar(e.semilla + fila, k)) * u
                drawLine(raya, Offset(x, y), Offset(minOf(x + l, cera.right - 3f * u), y + (azar(fila, k) - 0.5f) * u), 0.4f * u)
                x += l + (2f + 3f * azar(e.semilla, k + fila * 7)) * u; k++
            }
            y += 6f * u; fila++
        }
        // Los clavos de bronce en las esquinas y los agujeros del cordel del díptico.
        val bronce = t(BRONCE, e, oscuro); val bronceOsc = t(BRONCE_OSC, e, oscuro)
        listOf(Offset(w * 0.0175f, h * 0.03f), Offset(w * 0.9825f, h * 0.03f), Offset(w * 0.0175f, h * 0.97f), Offset(w * 0.9825f, h * 0.97f)).forEach { p ->
            drawCircle(bronce, 1.1f * u, p)
            drawCircle(bronceOsc, 1.1f * u, p, style = Stroke(0.35f * u))
        }
        listOf(0.36f, 0.64f).forEach { f ->
            drawLine(t(LINO, e, oscuro, 0.1f), Offset(w * 0.0175f, h * f), Offset(0f, h * f + 2.5f * u), 0.6f * u)
            drawCircle(Color.Black.copy(alpha = 0.45f), 0.9f * u, Offset(w * 0.0175f, h * f))
        }
        // El estilo de bronce, apoyado sobre el marco de abajo, con su paleta para borrar.
        val sy = h * 0.97f
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(w * 0.62f, sy + 0.8f * u), Offset(w * 0.86f, sy + 0.4f * u), 1.1f * u, StrokeCap.Round)
        drawLine(bronce, Offset(w * 0.62f, sy), Offset(w * 0.86f, sy - 0.4f * u), 0.9f * u, StrokeCap.Round)
        drawPath(Path().apply {
            moveTo(w * 0.855f, sy - 0.4f * u - 0.5f * u); lineTo(w * 0.878f, sy - 0.45f * u - 1.3f * u)
            lineTo(w * 0.878f, sy - 0.45f * u + 1.3f * u); lineTo(w * 0.855f, sy - 0.4f * u + 0.5f * u); close()
        }, bronce)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        if (r.width < 1f || r.height < 1f) return
        val k = minOf(r.width, r.height) / 100f
        val rad = CornerRadius(4f * k)
        // La sombra sobre la cera y el marco de la tablilla.
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f), Offset(r.left + 1.5f * k, r.top + 2.5f * k), r.size, rad)
        drawRoundRect(if (oscuro) mez(NOGAL_LUZ, Color.Black, 0.15f) else HAYA, r.topLeft, r.size, rad)
        drawRoundRect(if (oscuro) NOGAL else HAYA_VETA.copy(alpha = 0.6f), r.topLeft, r.size, rad, style = Stroke(0.8f * k))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.1f else 0.35f), Offset(r.left + 4f * k, r.top + 1.2f * k), Offset(r.right - 4f * k, r.top + 1.2f * k), 0.8f * k)
        // La cera: miel clara de día, negra de noche; con su rebaje.
        val m = 6f * k
        val campo = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
        drawRect(if (oscuro) CERA_NEGRA else CERA_CLARA, campo.topLeft, campo.size)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), campo.topLeft, Size(campo.width, 1.2f * k))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), campo.topLeft, Size(1.2f * k, campo.height))
        if (e.fijada) {
            // Destacada: el filo del acento y el clavo de bronce que la sujeta arriba.
            drawRoundRect(e.acento, r.topLeft, r.size, rad, style = Stroke(1.6f * density))
            val cl = Offset(r.center.x, r.top + m * 0.5f)
            drawCircle(Color.Black.copy(alpha = 0.3f), 3.2f * k, Offset(cl.x + 0.6f * k, cl.y + 0.9f * k))
            drawCircle(BRONCE, 3.2f * k, cl)
            drawCircle(BRONCE_LUZ, 1.2f * k, Offset(cl.x - 0.9f * k, cl.y - 0.9f * k))
        }
        // El detalle del tipo, en la esquina de arriba a la derecha de la cera.
        val mc = Offset(r.right - m - 5f * k, r.top + m + 4.5f * k)
        when (e.tipo) {
            "idea" -> {
                // Una bulla de oro.
                drawCircle(ORO, 3.4f * k, mc)
                drawCircle(ORO_OSC, 3.4f * k, mc, style = Stroke(0.8f * k))
                drawCircle(ORO_OSC, 1.1f * k, mc)
            }
            "pensamiento" -> {
                // Una ramita de laurel.
                val hoja = if (oscuro) mez(LAUREL, Color.White, 0.25f) else LAUREL
                drawLine(hoja, Offset(mc.x - 4f * k, mc.y + 2.5f * k), Offset(mc.x + 3.5f * k, mc.y - 2.5f * k), 0.6f * k)
                for (j in 0 until 3) {
                    val p = Offset(mc.x - 2.5f * k + j * 2.5f * k, mc.y + 1.5f * k - j * 1.7f * k)
                    rotate(-35f + (if (j % 2 == 0) -40f else 40f), p) { drawOval(hoja, Offset(p.x - 2f * k, p.y - 0.8f * k), Size(4f * k, 1.6f * k)) }
                }
            }
            "proposito" -> {
                // Una tesela de pórfido con su filete de oro.
                drawRect(PORFIDO, Offset(mc.x - 3f * k, mc.y - 3f * k), Size(6f * k, 6f * k))
                drawRect(if (oscuro) ORO else ORO_OSC, Offset(mc.x - 3f * k, mc.y - 3f * k), Size(6f * k, 6f * k), style = Stroke(0.7f * k))
            }
            "recordatorio" -> {
                // El estilo clavado en la cera, con su paleta hacia arriba.
                val punta = Offset(r.right - m - 8f * k, r.top + m + 6.5f * k)
                val cabo = Offset(r.right - 1.5f * k, r.top + 1.5f * k)
                drawCircle(Color.Black.copy(alpha = 0.5f), 1.2f * k, punta)
                drawLine(Color.Black.copy(alpha = 0.25f), Offset(punta.x + k, punta.y + k), Offset(cabo.x + k, cabo.y + k), 1.6f * k, StrokeCap.Round)
                drawLine(BRONCE, punta, cabo, 1.5f * k, StrokeCap.Round)
                drawLine(BRONCE_LUZ, Offset(punta.x + (cabo.x - punta.x) * 0.75f, punta.y + (cabo.y - punta.y) * 0.75f), cabo, 2.6f * k, StrokeCap.Round)
            }
            else -> {}
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) LINO else NEGRO
}
