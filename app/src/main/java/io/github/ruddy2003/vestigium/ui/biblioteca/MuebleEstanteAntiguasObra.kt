package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.ceil

/**
 * El armario de la crónica del estante de Tierras Antiguas ([MuebleEstanteAntiguas]), de roble ahumado: la cornisa volada con su
 * tapa, el bocel, el filete dorado, los dentellones y el friso tallado con dos cintas que se entrelazan y guardan en cada lazo
 * un cabujón engastado en latón; los costados como pilastras de tablero rehundido con su capitel y una roseta de latón con su
 * gema a la altura de cada tabla; las tablas de canto moldurado con clavos de latón; los casilleros, que dejan ver la pared de
 * lapislázuli en la sombra del mueble; el zócalo de rombos sobre pies de bollo. Se pinta con la luz entera y encima va el mapa
 * de luz del cuerpo ([luzCuerpo]).
 */
internal object ObraEstanteAntiguas {
    private val P = PaletaAntiguas
    private val GEMAS = listOf(P.RUBI, P.ESMERALDA, P.ZAFIRO, P.AMATISTA)

    // ---------------------------------------------------------------- la cornisa

    fun DrawScope.cornisa(g: MarcoAntiguas) {
        val w = g.w; val u = density
        val am = g.am
        val y0 = g.apoyo; val hh = g.ch - g.apoyo
        fun y(f: Float) = y0 + hh * f
        fun c(col: Color, f: Float = 0f) = am.dia(col, f, 1f)
        // La tapa, vista un poco desde arriba, con su veta.
        drawRect(c(P.ROBLE_LUZ, 0.04f), Offset(0f, y0), Size(w, hh * 0.08f))
        vetas(Rect(0f, y0, w, y(0.08f)), c(P.ROBLE).copy(alpha = 0.5f), 7601, densidad = 0.7f, grosor = 0.5f, ondulacion = 0.4f)
        drawRect(c(P.ROBLE_LUZ, 0.25f), Offset(0f, y(0.075f)), Size(w, 1.2f * u))
        // El bocel: luz arriba, la cara y la sombra abajo.
        drawRect(c(P.ROBLE_LUZ, 0.08f), Offset(0f, y(0.08f)), Size(w, hh * 0.05f))
        drawRect(c(P.ROBLE), Offset(0f, y(0.13f)), Size(w, hh * 0.05f))
        drawRect(c(P.ROBLE_OSC), Offset(0f, y(0.18f)), Size(w, hh * 0.05f))
        drawRect(am.sombra(0.4f), Offset(0f, y(0.22f)), Size(w, 1.2f * u))
        // El filete dorado.
        drawRect(c(P.ORO), Offset(0f, y(0.23f)), Size(w, hh * 0.035f))
        drawRect(c(P.ORO_LUZ).copy(alpha = 0.8f), Offset(0f, y(0.23f)), Size(w, 0.8f * u))
        // Los dentellones: tacos con su luz a la izquierda y su sombra a la derecha, sobre el fondo hondo.
        val dt0 = y(0.265f); val dt1 = y(0.4f)
        drawRect(c(P.ROBLE_HONDO), Offset(0f, dt0), Size(w, dt1 - dt0))
        val ancho = w * 0.017f; val paso = w * 0.027f
        var x = -w * 0.004f
        val tacos = Path(); val luces = Path(); val sombras = Path()
        while (x < w) {
            tacos.addRect(Rect(x, dt0, x + ancho, dt1))
            luces.addRect(Rect(x, dt0, x + 1.2f * u, dt1))
            sombras.addRect(Rect(x + ancho, dt0 + 1f * u, x + ancho + 2.2f * u, dt1))
            x += paso
        }
        drawPath(sombras, am.sombra(0.5f))
        drawPath(tacos, c(P.ROBLE))
        drawPath(luces, c(P.ROBLE_LUZ, 0.15f))
        drawRect(c(P.ROBLE_LUZ, 0.1f), Offset(0f, dt0), Size(w, 0.9f * u))
        // El listel bajo los dentellones.
        drawRect(c(P.ROBLE_OSC), Offset(0f, dt1), Size(w, hh * 0.035f))
        drawRect(c(P.ROBLE_LUZ, 0.1f), Offset(0f, dt1), Size(w, 0.8f * u))
        // El friso tallado.
        friso(Rect(0f, y(0.435f), w, y(0.8f)), g)
        // El filete dorado de abajo y la gola en sombra.
        drawRect(c(P.ORO_OSC), Offset(0f, y(0.8f)), Size(w, hh * 0.04f))
        drawRect(c(P.ORO_LUZ).copy(alpha = 0.6f), Offset(0f, y(0.8f)), Size(w, 0.8f * u))
        drawRect(c(P.ROBLE), Offset(0f, y(0.84f)), Size(w, hh * 0.06f))
        drawRect(c(P.ROBLE_OSC), Offset(0f, y(0.9f)), Size(w, hh * 0.1f + 1f))
        drawRect(am.sombra(0.35f), Offset(0f, y(0.95f)), Size(w, hh * 0.05f + 1f))
        // El desgaste: unos golpes claros en el bocel y en la tapa.
        for (i in 0 until 9) {
            val xx = w * (0.04f + 0.11f * i + 0.04f * azar(7610, i))
            drawLine(c(P.ROBLE_LUZ, 0.25f).copy(alpha = 0.7f), Offset(xx, y(0.1f + 0.08f * azar(7611, i))), Offset(xx + w * 0.012f, y(0.12f + 0.08f * azar(7611, i))), 1.1f * u, StrokeCap.Round)
        }
    }

    /**
     * El friso: una fila de casetones rehundidos, separados por tacos tallados con su estría; en cada casetón, una gema de talla
     * cuadrada (de mesa, con las esquinas cortadas) en su engaste de latón de cuatro garras (rubí, esmeralda, zafiro, amatista).
     */
    private fun DrawScope.friso(r: Rect, g: MarcoAntiguas) {
        val w = g.w; val u = density
        val am = g.am
        fun c(col: Color, f: Float = 0f) = am.dia(col, f, 1f)
        drawRect(c(P.ROBLE), r.topLeft, r.size)
        val paso = w * 0.074f
        val taco = w * 0.016f
        val b = r.height * 0.1f
        var x = -paso * 0.35f
        var j = 0
        val fondos = Path(); val sombras = Path(); val luces = Path(); val estrias = Path()
        val gemas = ArrayList<Offset>()
        while (x < w) {
            val cas = Rect(x + taco, r.top + r.height * 0.12f, x + paso, r.bottom - r.height * 0.12f)
            fondos.addRect(cas)
            // Los biseles del casetón: arriba y a la izquierda en sombra, abajo y a la derecha a la luz.
            sombras.addPath(poligonoAntiguas(cas.left, cas.top, cas.right, cas.top, cas.right - b, cas.top + b, cas.left + b, cas.top + b))
            sombras.addPath(poligonoAntiguas(cas.left, cas.top, cas.left + b, cas.top + b, cas.left + b, cas.bottom - b, cas.left, cas.bottom))
            luces.addPath(poligonoAntiguas(cas.left, cas.bottom, cas.left + b, cas.bottom - b, cas.right - b, cas.bottom - b, cas.right, cas.bottom))
            luces.addPath(poligonoAntiguas(cas.right, cas.top, cas.right, cas.bottom, cas.right - b, cas.bottom - b, cas.right - b, cas.top + b))
            // La estría del taco.
            estrias.addRect(Rect(x + taco * 0.4f, r.top + r.height * 0.16f, x + taco * 0.62f, r.bottom - r.height * 0.16f))
            gemas.add(cas.center)
            x += paso; j++
        }
        drawPath(fondos, c(P.ROBLE_HONDO))
        drawPath(sombras, am.sombra(0.55f))
        drawPath(luces, c(P.ROBLE_LUZ, 0.08f))
        drawPath(estrias, am.sombra(0.45f))
        drawRect(c(P.ROBLE_LUZ, 0.12f), r.topLeft, Size(r.width, 1f * u))
        gemas.forEachIndexed { i, cg -> gemaCuadrada(cg, r.height * 0.27f, GEMAS[Math.floorMod(i, GEMAS.size)], am, 1f) }
    }

    /** Una gema de talla de mesa cuadrada, con las esquinas cortadas, en su engaste de latón de cuatro garras. */
    fun DrawScope.gemaCuadrada(c: Offset, r: Float, gema: Color, a: AireAntiguas, k: Float) {
        val u = density
        val e = r * 1.3f
        drawRect(a.sombra(0.5f), Offset(c.x - e + r * 0.2f, c.y - e + r * 0.25f), Size(e * 2, e * 2))
        drawRect(a.luz(P.LATON_OSC, k), Offset(c.x - e, c.y - e), Size(e * 2, e * 2))
        drawRect(a.luz(P.LATON, k), Offset(c.x - e, c.y - e), Size(e * 2 - r * 0.2f, e * 2 - r * 0.2f))
        val q = r * 0.32f
        val piedra = poligonoAntiguas(c.x - r + q, c.y - r, c.x + r - q, c.y - r, c.x + r, c.y - r + q, c.x + r, c.y + r - q,
            c.x + r - q, c.y + r, c.x - r + q, c.y + r, c.x - r, c.y + r - q, c.x - r, c.y - r + q)
        drawPath(piedra, a.luz(mezcla(gema, Color.Black, 0.3f), k))
        // Las facetas: la de arriba a la izquierda clara, la de abajo a la derecha honda y la mesa en medio.
        val m = r * 0.48f
        drawPath(poligonoAntiguas(c.x - r + q, c.y - r, c.x + r - q, c.y - r, c.x + m, c.y - m, c.x - m, c.y - m), a.luz(mezcla(gema, Color.White, 0.35f), k))
        drawPath(poligonoAntiguas(c.x - r, c.y - r + q, c.x - m, c.y - m, c.x - m, c.y + m, c.x - r, c.y + r - q), a.luz(mezcla(gema, Color.White, 0.18f), k))
        drawRect(a.luz(gema, k), Offset(c.x - m, c.y - m), Size(m * 2, m * 2))
        drawRect(a.claro(0.85f).copy(alpha = if (a.oscuro) 0f else 0.7f), Offset(c.x - m, c.y - m), Size(m * 0.7f, m * 0.5f))
        if (a.oscuro && k > 0.1f) drawCircle(P.FUEGO.copy(alpha = (0.9f * k).coerceAtMost(0.85f)), r * 0.16f, Offset(c.x - m * 0.5f, c.y - m * 0.5f))
        // Las cuatro garras, en las esquinas cortadas.
        listOf(Offset(-1f, -1f), Offset(1f, -1f), Offset(1f, 1f), Offset(-1f, 1f)).forEach { d ->
            drawCircle(a.luz(P.LATON_LUZ, k), r * 0.2f, Offset(c.x + d.x * (r - q * 0.4f), c.y + d.y * (r - q * 0.4f)))
        }
        drawRect(a.sombra(0.4f), Offset(c.x - e, c.y - e), Size(e * 2, e * 2), style = Stroke(0.6f * u))
    }

    /** Un cabujón engastado: el bisel de latón con su luz, la gema abombada con su sombra y su brillo. */
    fun DrawScope.cabujon(c: Offset, r: Float, gema: Color, a: AireAntiguas, k: Float) {
        val u = density
        drawCircle(a.sombra(0.5f), r * 1.35f, c + Offset(r * 0.25f, r * 0.3f))
        drawCircle(a.luz(P.LATON_OSC, k), r * 1.32f, c)
        drawCircle(a.luz(P.LATON, k), r * 1.18f, c - Offset(r * 0.06f, r * 0.06f))
        drawCircle(a.luz(mezcla(gema, Color.Black, 0.35f), k), r, c)
        drawCircle(a.luz(gema, k), r * 0.82f, c - Offset(r * 0.1f, r * 0.12f))
        drawCircle(a.luz(mezcla(gema, Color.White, 0.3f), k), r * 0.4f, c - Offset(r * 0.25f, r * 0.28f))
        drawCircle(a.claro(0.9f).copy(alpha = if (a.oscuro) 0f else 0.85f), r * 0.17f, c - Offset(r * 0.38f, r * 0.42f))
        if (a.oscuro && k > 0.1f) drawCircle(P.FUEGO.copy(alpha = (0.9f * k).coerceAtMost(0.85f)), r * 0.15f, c - Offset(r * 0.38f, r * 0.42f))
        drawCircle(a.brillo(0.5f, k), r * 1.25f, c, style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- el cuerpo

    /**
     * Un casillero: la pared de lapislázuli en la sombra del mueble (con su estarcido apagado, sus pinceladas y, en alguno, un
     * desconchón), la sombra de la tabla de arriba y la del costado que tapa la luz, la tabla de encima vista por debajo, las dos
     * caras de dentro de los costados y la tapa de la tabla de abajo, donde pisa lo guardado.
     */
    fun DrawScope.casillero(k: Int, r: Rect, d: DisposicionEstante, g: MarcoAntiguas) {
        val w = g.w; val u = density
        val am = g.am; val a = g.a
        val x0 = g.x0; val x1 = g.x1
        val dT = w * 0.02f; val dI = w * 0.026f
        // El fondo es la pared: de noche se pinta con su color y lo hunde el mapa de luz (no se le echa el ámbar de la llama).
        fun cp(col: Color, f: Float = 0f) = if (a.oscuro) col else a.dia(col, f, 1f)
        fun cm(col: Color, f: Float = 0f) = am.dia(col, f, 1f)
        drawRect(cm(P.ROBLE_OSC), Offset(x0 - 1f, r.top - 1f), Size(x1 - x0 + 2f, r.height + 2f))
        val fondo = Rect(x0 + dI, r.top + dT, x1 - dI, r.bottom)
        drawRect(cp(mezcla(P.LAPIS, P.LAPIS_OSC, 0.35f)), fondo.topLeft, fondo.size)
        FondoCasilleroAntiguas.pintar(this, fondo, r.height, cp(P.LAPIS_LUZ), cp(P.LAPIS_HONDO), cp(P.ORO))
        // Un desconchón en algunos: si la repisa tiene hueco libre, en el hueco (donde se ve); si no, detrás de lo guardado.
        val libre = (d.ocupado.getOrNull(k) ?: fondo.right).coerceIn(fondo.left, fondo.right)
        if (Math.floorMod(k, 3) == 1 || fondo.right - libre > w * 0.25f) {
            val x0 = if (fondo.right - libre > w * 0.14f) libre + (fondo.right - libre) * (0.25f + 0.4f * azar(7700, k)) else fondo.left + fondo.width * (0.55f + 0.2f * azar(7700, k))
            desconchonChico(Rect(x0, fondo.top + fondo.height * 0.2f, x0 + w * 0.07f, fondo.top + fondo.height * 0.4f), g, k)
        }
        // La sombra de la tabla de arriba, en escalones, y la del costado de la izquierda (la luz viene de la ventana).
        drawRect(a.sombra(0.46f), fondo.topLeft, Size(fondo.width, r.height * 0.075f))
        drawRect(a.sombra(0.24f), Offset(fondo.left, fondo.top + r.height * 0.075f), Size(fondo.width, r.height * 0.12f))
        drawRect(a.sombra(0.1f), Offset(fondo.left, fondo.top + r.height * 0.195f), Size(fondo.width, r.height * 0.14f))
        drawRect(a.sombra(0.26f), fondo.topLeft, Size(w * 0.028f, fondo.height))
        drawRect(a.sombra(0.12f), Offset(fondo.left + w * 0.028f, fondo.top), Size(w * 0.03f, fondo.height))
        // La tabla de encima vista por debajo y las dos caras de dentro: la de la izquierda en sombra, la de la derecha a la luz.
        drawPath(poligonoAntiguas(x0, r.top, x1, r.top, x1 - dI, r.top + dT, x0 + dI, r.top + dT), cm(P.ROBLE_HONDO))
        val caraIzq = poligonoAntiguas(x0, r.top, x0 + dI, r.top + dT, x0 + dI, r.bottom, x0, r.bottom)
        val caraDer = poligonoAntiguas(x1, r.top, x1, r.bottom, x1 - dI, r.bottom, x1 - dI, r.top + dT)
        drawPath(caraIzq, cm(P.ROBLE_OSC, -0.1f))
        drawPath(caraDer, cm(P.ROBLE, 0.1f))
        drawLine(cm(P.ROBLE_LUZ, 0.1f).copy(alpha = 0.5f), Offset(x1 - dI * 0.5f, r.top + dT), Offset(x1 - dI * 0.5f, r.bottom), 0.7f * u)
        drawLine(am.sombra(0.45f), Offset(x0 + dI, r.top + dT), Offset(x0 + dI, r.bottom), 1f * u)
        drawLine(am.sombra(0.3f), Offset(x1 - dI, r.top + dT), Offset(x1 - dI, r.bottom), 1f * u)
        drawLine(am.sombra(0.55f), Offset(x0 + dI, r.top + dT), Offset(x1 - dI, r.top + dT), 1f * u)
        // La tapa de la tabla de abajo, donde pisa lo guardado.
        val tapa = w * 0.012f
        drawPath(poligonoAntiguas(x0 + dI, r.bottom - tapa, x1 - dI, r.bottom - tapa, x1, r.bottom, x0, r.bottom), cm(P.ROBLE, 0.08f))
        drawLine(am.sombra(0.4f), Offset(x0 + dI, r.bottom - tapa), Offset(x1 - dI, r.bottom - tapa), 1f * u)
    }

    /** Un desconchón chico en el fondo de un casillero: la cal y la piedra de la casa. */
    private fun DrawScope.desconchonChico(r: Rect, g: MarcoAntiguas, k: Int) {
        val u = density
        val a = g.a
        fun cp(col: Color) = if (a.oscuro) col else a.dia(col, 0f, 1f)
        val forma = Path().apply {
            // Borde irregular pero sin puntas alternas (eso se leería como una estrella).
            val n = 13
            for (i in 0 until n) {
                val t = i / n.toFloat() * 6.283f
                val rad = 0.72f + 0.28f * azar(7710 + k, i)
                val p = Offset(r.center.x + kotlin.math.cos(t) * r.width / 2 * rad, r.center.y + kotlin.math.sin(t) * r.height / 2 * rad)
                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }
        // El mortero y dos piedras de la casa; la sombra del canto de arriba; la cal del grueso, clara abajo a la derecha.
        drawPath(forma, cp(P.PIEDRA_OSC))
        clipPath(forma) {
            drawRoundRect(cp(mezcla(P.PIEDRA, P.OCRE, 0.15f)), Offset(r.left - r.width * 0.1f, r.top + r.height * 0.08f), Size(r.width * 0.62f, r.height * 0.42f), CornerRadius(2f * u))
            drawRoundRect(cp(mezcla(P.PIEDRA, P.PIEDRA_OSC, 0.3f)), Offset(r.left + r.width * 0.42f, r.top + r.height * 0.56f), Size(r.width * 0.7f, r.height * 0.4f), CornerRadius(2f * u))
            drawRoundRect(cp(mezcla(P.PIEDRA, P.OCRE, 0.08f)), Offset(r.left - r.width * 0.05f, r.top + r.height * 0.56f), Size(r.width * 0.42f, r.height * 0.4f), CornerRadius(2f * u))
            translate(1.8f * u, 1.8f * u) { drawPath(forma, a.sombra(0.5f), style = Stroke(3.2f * u)) }
        }
        translate(-1f * u, -1f * u) { drawPath(forma, cp(P.CAL), style = Stroke(1.8f * u)) }
        drawPath(forma, cp(P.LAPIS_HONDO), style = Stroke(0.8f * u))
    }

    /**
     * Una tabla con su canto moldurado: la arista de luz, el bocel, la ranura en sombra, la cara de roble con su veta, los clavos
     * de latón y el canto de abajo; y el desgaste (golpes y una mancha).
     */
    fun DrawScope.tabla(k: Int, r: Rect, g: MarcoAntiguas) {
        val u = density
        val am = g.am
        val h = r.height
        fun c(col: Color, f: Float = 0f) = am.dia(col, f, 1f)
        drawRect(c(P.ROBLE), r.topLeft, r.size)
        drawRect(c(P.ROBLE_LUZ, 0.18f), r.topLeft, Size(r.width, h * 0.08f))
        drawRect(c(P.ROBLE_LUZ, 0.02f), Offset(r.left, r.top + h * 0.08f), Size(r.width, h * 0.13f))
        drawRect(c(P.ROBLE), Offset(r.left, r.top + h * 0.21f), Size(r.width, h * 0.1f))
        drawRect(c(P.ROBLE_OSC), Offset(r.left, r.top + h * 0.31f), Size(r.width, h * 0.07f))
        drawRect(am.sombra(0.5f), Offset(r.left, r.top + h * 0.37f), Size(r.width, 1.1f * u))
        drawRect(c(P.ROBLE_LUZ, 0f).copy(alpha = 0.5f), Offset(r.left, r.top + h * 0.395f), Size(r.width, 0.8f * u))
        // La veta de la cara.
        val veta = Path()
        for (j in 0 until 4) {
            val y = r.top + h * (0.48f + 0.1f * j)
            veta.moveTo(r.left, y); veta.cubicTo(r.left + r.width * 0.35f, y + 1.6f * u * (azar(7801 + k, j) - 0.5f), r.left + r.width * 0.65f, y - 1.2f * u, r.right, y + 0.8f * u)
        }
        drawPath(veta, am.sombra(0.2f), style = Stroke(0.7f * u))
        // Los clavos de latón.
        var x = r.left + r.width * 0.06f
        while (x < r.right - r.width * 0.04f) {
            remacheAntiguas(Offset(x, r.top + h * 0.66f), 1.7f * u, am, 1f, P.LATON)
            x += r.width * 0.148f
        }
        // El desgaste: golpes claros en el bocel y una mancha.
        for (i in 0 until 4) {
            val xx = r.left + r.width * (0.1f + 0.22f * i + 0.1f * azar(7810 + k, i))
            drawLine(c(P.ROBLE_LUZ, 0.3f).copy(alpha = 0.6f), Offset(xx, r.top + h * 0.12f), Offset(xx + 4f * u, r.top + h * 0.15f), 1.1f * u, StrokeCap.Round)
        }
        drawOval(am.sombra(0.14f), Offset(r.left + r.width * (0.2f + 0.5f * azar(7820, k)), r.top + h * 0.55f), Size(r.width * 0.08f, h * 0.22f))
        // El canto de abajo, en sombra, y la sombra que deja en el casillero de debajo.
        drawRect(c(P.ROBLE_HONDO), Offset(r.left, r.bottom - h * 0.09f), Size(r.width, h * 0.09f + 0.6f))
        drawRect(c(P.ROBLE_LUZ, 0f).copy(alpha = 0.45f), Offset(r.left, r.bottom - h * 0.11f), Size(r.width, 0.8f * u))
    }

    /**
     * Un costado: la pilastra de roble con su vuelta de canto, el tablero rehundido con sus biseles (el de la izquierda y el de
     * arriba en sombra, los otros a la luz), la veta, el capitel bajo la cornisa, la basa sobre el zócalo y una roseta de latón
     * con su gema a la altura de cada tabla.
     */
    fun DrawScope.costado(r: Rect, izquierdo: Boolean, d: DisposicionEstante, g: MarcoAntiguas) {
        val w = g.w; val u = density
        val am = g.am
        fun c(col: Color, f: Float = 0f) = am.dia(col, f, 1f)
        val l = r.width
        drawRect(c(P.ROBLE), r.topLeft, r.size)
        vetas(r, c(P.ROBLE_OSC).copy(alpha = 0.5f), if (izquierdo) 7901 else 7902, densidad = 0.9f, vertical = true, grosor = 0.7f, ondulacion = 0.8f)
        // La vuelta del canto de fuera y el bocel de la arista de dentro.
        val fuera = if (izquierdo) Rect(r.left, r.top, r.left + l * 0.1f, r.bottom) else Rect(r.right - l * 0.1f, r.top, r.right, r.bottom)
        drawRect(if (izquierdo) c(P.ROBLE_LUZ, 0.1f) else c(P.ROBLE_OSC), fuera.topLeft, fuera.size)
        val dentro = if (izquierdo) r.right - l * 0.08f else r.left
        drawRect(if (izquierdo) c(P.ROBLE_OSC) else c(P.ROBLE_LUZ, 0.12f), Offset(dentro, r.top), Size(l * 0.08f, r.height))
        // El capitel y la basa.
        val cap = w * 0.045f
        drawRect(c(P.ROBLE_LUZ, 0.06f), Offset(r.left, r.top), Size(l, cap * 0.35f))
        drawRect(c(P.ROBLE), Offset(r.left, r.top + cap * 0.35f), Size(l, cap * 0.4f))
        drawRect(c(P.ORO), Offset(r.left, r.top + cap * 0.75f), Size(l, cap * 0.12f))
        drawRect(am.sombra(0.45f), Offset(r.left, r.top + cap * 0.87f), Size(l, cap * 0.13f))
        val basa = w * 0.035f
        drawRect(c(P.ROBLE_LUZ, 0.08f), Offset(r.left, r.bottom - basa), Size(l, basa * 0.25f))
        drawRect(c(P.ROBLE), Offset(r.left, r.bottom - basa * 0.75f), Size(l, basa * 0.75f))
        // El tablero rehundido.
        val t = Rect(r.left + l * 0.22f, r.top + cap + w * 0.012f, r.right - l * 0.22f, r.bottom - basa - w * 0.012f)
        if (t.height > 4f * u) {
            val b = l * 0.08f
            drawRect(c(P.ROBLE_OSC, -0.05f), t.topLeft, t.size)
            vetas(t, c(P.ROBLE_HONDO).copy(alpha = 0.5f), if (izquierdo) 7911 else 7912, densidad = 1f, vertical = true, grosor = 0.6f, ondulacion = 0.5f)
            drawPath(poligonoAntiguas(t.left, t.top, t.left + b, t.top + b, t.left + b, t.bottom - b, t.left, t.bottom), am.sombra(0.4f))
            drawPath(poligonoAntiguas(t.right, t.top, t.right, t.bottom, t.right - b, t.bottom - b, t.right - b, t.top + b), c(P.ROBLE_LUZ, 0.12f))
            drawPath(poligonoAntiguas(t.left, t.top, t.right, t.top, t.right - b, t.top + b, t.left + b, t.top + b), am.sombra(0.5f))
            drawPath(poligonoAntiguas(t.left, t.bottom, t.left + b, t.bottom - b, t.right - b, t.bottom - b, t.right, t.bottom), c(P.ROBLE_LUZ, 0.06f))
            // Por el tablero baja una cadena de rombos tallados en relieve: cada uno con su cara de luz (arriba a la izquierda) y
            // su cara de sombra, unidos por un bocel. Se lee de lejos como talla, no como raya.
            drawLine(c(P.ROBLE_LUZ, 0.05f), Offset(t.center.x - 1f * u, t.top + b), Offset(t.center.x - 1f * u, t.bottom - b), 1.6f * u)
            drawLine(am.sombra(0.4f), Offset(t.center.x + 1f * u, t.top + b), Offset(t.center.x + 1f * u, t.bottom - b), 1f * u)
            val rr = minOf(t.width * 0.36f, w * 0.012f)
            val paso = rr * 3.4f
            val caras = Path(); val luces = Path(); val sombras = Path()
            var yy = t.top + b + rr * 1.6f
            while (yy < t.bottom - b - rr * 1.2f) {
                val cx = t.center.x
                caras.addPath(poligonoAntiguas(cx, yy - rr * 1.4f, cx + rr, yy, cx, yy + rr * 1.4f, cx - rr, yy))
                luces.addPath(poligonoAntiguas(cx, yy - rr * 1.4f, cx, yy, cx - rr, yy))
                sombras.addPath(poligonoAntiguas(cx, yy, cx + rr, yy, cx, yy + rr * 1.4f))
                yy += paso
            }
            drawPath(caras, c(P.ROBLE))
            drawPath(luces, c(P.ROBLE_LUZ, 0.14f))
            drawPath(sombras, am.sombra(0.45f))
        }
        // Una roseta con su gema a la altura de cada tabla.
        d.tablas.forEachIndexed { k, tb ->
            val cc = Offset(r.center.x, tb.center.y)
            roseta(cc, l * 0.22f, GEMAS[Math.floorMod(k + (if (izquierdo) 0 else 2), GEMAS.size)], am)
        }
        if (izquierdo && g.n > 0) arandela(g)
        // Los golpes del uso en la arista.
        for (i in 0 until 5) {
            val yy = r.top + r.height * (0.15f + 0.17f * i + 0.05f * azar(if (izquierdo) 7920 else 7921, i))
            val xx = if (izquierdo) r.right - l * 0.12f else r.left + l * 0.04f
            drawLine(c(P.ROBLE_LUZ, 0.3f).copy(alpha = 0.6f), Offset(xx, yy), Offset(xx + l * 0.06f, yy + 3f * u), 1f * u, StrokeCap.Round)
        }
    }

    /**
     * La arandela clavada en la pilastra de la izquierda, a la altura de la repisa de en medio: la pletina de hierro con sus
     * remates enroscados, el brazo en ese, el platillo y la vela de leer del cronista (la llama se pinta aparte, encima de la
     * noche).
     */
    private fun DrawScope.arandela(g: MarcoAntiguas) {
        val w = g.w; val u = density
        val am = g.am
        val f = g.arandela
        fun c(col: Color, k: Float = 0f) = am.dia(col, k, 1f)
        val yPlato = f.y + w * 0.062f
        val pl0 = f.y + w * 0.02f; val pl1 = f.y + w * 0.125f
        // La sombra en la pilastra (la luz del día viene de la izquierda).
        val sombra = Path().apply {
            addRect(Rect(f.x - w * 0.006f, pl0, f.x + w * 0.006f, pl1))
            addRect(Rect(f.x - w * 0.024f, yPlato - w * 0.06f, f.x + w * 0.024f, yPlato + w * 0.008f))
        }
        translate(w * 0.008f, w * 0.006f) { drawPath(sombra, am.sombra(0.4f)) }
        // La pletina con sus remates.
        drawRect(c(P.HIERRO), Offset(f.x - w * 0.006f, pl0), Size(w * 0.012f, pl1 - pl0))
        drawRect(am.claro(0.3f), Offset(f.x - w * 0.006f, pl0), Size(1f * u, pl1 - pl0))
        listOf(pl0, pl1).forEach { y -> drawCircle(c(P.HIERRO), w * 0.008f, Offset(f.x, y), style = Stroke(1.6f * u)) }
        remacheAntiguas(Offset(f.x, pl0 + w * 0.02f), 1.3f * u, am, 1f, P.HIERRO)
        remacheAntiguas(Offset(f.x, pl1 - w * 0.02f), 1.3f * u, am, 1f, P.HIERRO)
        // El brazo en ese, del pie de la pletina al platillo.
        drawPath(Path().apply {
            moveTo(f.x, pl1 - w * 0.03f)
            cubicTo(f.x + w * 0.03f, pl1 - w * 0.035f, f.x + w * 0.03f, yPlato + w * 0.03f, f.x, yPlato + w * 0.006f)
        }, c(P.HIERRO), style = Stroke(2.2f * u))
        // El platillo y la vela, de cera cruda, con su chorreón.
        val plato = Rect(f.x - w * 0.026f, yPlato - w * 0.005f, f.x + w * 0.026f, yPlato + w * 0.007f)
        drawOval(c(P.HIERRO), plato.topLeft, plato.size)
        drawOval(am.claro(0.3f), Offset(plato.left + w * 0.004f, plato.top), Size(plato.width * 0.5f, plato.height * 0.4f))
        val vela = Rect(f.x - w * 0.008f, f.y + w * 0.012f, f.x + w * 0.008f, yPlato)
        drawRect(c(P.CERA), vela.topLeft, vela.size)
        drawRect(am.claro(0.35f), Offset(vela.left + 1f * u, vela.top), Size(1.2f * u, vela.height))
        drawRect(am.sombra(0.25f), Offset(vela.right - vela.width * 0.35f, vela.top), Size(vela.width * 0.35f, vela.height))
        drawRoundRect(c(P.CERA), Offset(vela.left - w * 0.001f, vela.top + w * 0.004f), Size(w * 0.005f, vela.height * 0.4f), CornerRadius(w * 0.003f))
        drawLine(Color(0xFF1C1612), Offset(f.x, vela.top), Offset(f.x, vela.top - w * 0.006f), 1.2f * u, StrokeCap.Round)
    }

    /** La llama de la arandela y su charco en la pilastra y en las repisas de en medio (de noche; al ocaso, recién encendida). */
    fun DrawScope.llamaArandela(g: MarcoAntiguas, abajo: Float) {
        val a = g.a
        if (!a.lumbre || g.n == 0) return
        val w = g.w
        val c = g.arandela
        val f = if (!a.oscuro) 0.35f else if (a.noche) 0.6f else 1f
        clipRect(0f, g.ch, w, abajo) {
            drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.38f * f), P.LLAMA.copy(alpha = 0.12f * f), Color.Transparent), c, w * 0.3f), w * 0.3f, c, blendMode = BlendMode.Screen)
        }
        llamaAntiguas(c, w * (if (a.noche) 0.014f else 0.018f), a)
    }

    /** La roseta de latón: un disco de pétalos repujados alrededor del cabujón. */
    private fun DrawScope.roseta(c: Offset, r: Float, gema: Color, a: AireAntiguas) {
        val u = density
        drawCircle(a.sombra(0.5f), r * 1.1f, c + Offset(r * 0.18f, r * 0.24f))
        drawCircle(a.dia(P.LATON_OSC, 0f, 1f), r * 1.08f, c)
        puntosEnCirculoAntiguas(c, r * 0.72f, 8, 0.2f).forEach { p ->
            drawCircle(a.dia(P.LATON, 0f, 1f), r * 0.3f, p)
            drawCircle(a.claro(0.5f), r * 0.1f, p - Offset(r * 0.08f, r * 0.08f))
        }
        cabujon(c, r * 0.46f, gema, a, 1f)
        drawCircle(a.sombra(0.35f), r * 1.08f, c, style = Stroke(0.7f * u))
    }

    /** El rótulo del mes: una placa de latón clavada en el canto de la tabla, con su filete grabado y el texto en tinta. */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, g: MarcoAntiguas) {
        val u = density
        val am = g.am
        val k = g.cerca(p.r.center)
        // Ancha lo justo para el nombre de la luna, el año y el mes común; en un teléfono angosto, casi de costado a costado.
        val ancho = minOf((g.x1 - g.x0) * 0.92f, maxOf((g.x1 - g.x0) * 0.66f, 236f * u))
        val r = Rect(g.w / 2 - ancho / 2, p.r.top + p.r.height * 0.06f, g.w / 2 + ancho / 2, p.r.bottom - p.r.height * 0.02f)
        drawRect(am.sombra(0.4f), Offset(r.left + 1.4f * u, r.top + 1.8f * u), r.size)
        val laton = am.etiqueta(P.LATON, k)
        drawRoundRect(laton, r.topLeft, r.size, CornerRadius(1.5f * u))
        drawRect(am.etiqueta(P.LATON_LUZ, k).copy(alpha = 0.7f), Offset(r.left + 1f * u, r.top + 0.8f * u), Size(r.width - 2f * u, 1f * u))
        drawRect(am.etiqueta(P.LATON_OSC, k).copy(alpha = 0.6f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
        drawRoundRect(am.etiqueta(P.LATON_OSC, k), Offset(r.left + 2.4f * u, r.top + 2.2f * u), Size(r.width - 4.8f * u, r.height - 4.4f * u), CornerRadius(1f * u), style = Stroke(0.7f * u))
        listOf(r.left + 4.2f * u, r.right - 4.2f * u).forEach { x -> remacheAntiguas(Offset(x, r.center.y), 1.5f * u, am, k, P.HIERRO) }
        val tinta = if (am.oscuro) mezcla(P.TINTA, Color.Black, 0.2f) else P.TINTA
        textoEn(medidor, p.texto, Rect(r.left + 8f * u, r.top + 1f * u, r.right - 8f * u, r.bottom - 1f * u), P.LETRA, tinta, 10.5f, peso = FontWeight.SemiBold, espaciado = 0.4f, minimo = 6f)
        if (am.oscuro && k > 0.15f) drawRect(P.LLAMA.copy(alpha = (0.3f * k).coerceAtMost(0.25f)), Offset(r.left, r.top), Size(r.width, 1.2f * u))
    }

    /**
     * La noche (y la sombra del día) del cuerpo: un mapa de luz estirado sobre los casilleros, las tablas y los costados. El roble
     * se hunde en el pardo rojizo y el fondo de los casilleros en el añil; junto a cada luz, encendido y con su calor.
     */
    fun DrawScope.luzCuerpo(d: DisposicionEstante, g: MarcoAntiguas, luces: FloatArray) {
        val w = g.w
        val abajo = d.tablas.lastOrNull()?.bottom ?: return
        val tablas = d.tablas.map { (it.top / w) to (it.bottom / w) }
        val xa = g.x0 / w + 0.026f; val xb = g.x1 / w - 0.026f
        val clave = "c:${w.toInt()}:${g.ch}:$abajo:${g.a.oscuro}:${g.a.noche}:${luces.joinToString(",")}"
        with(MapaLuzAntiguas) {
            pintar(1, clave, g.ch, abajo, 96) { x, y ->
                val madera = x < xa || x > xb || tablas.any { (t0, t1) -> y >= t0 - 0.004f && y <= t1 }
                val k = g.cerca(Offset(x * w, y * w))
                mascaraAntiguas(if (madera) g.am else g.a, x, y, k)
            }
        }
    }

    // ---------------------------------------------------------------- el zócalo

    /**
     * El zócalo, desde la última tabla: la moldura de arriba, la banda con su fila de rombos tallados y un clavo de latón en cada
     * uno, la moldura de abajo y los pies de bollo torneados; entre los pies, la sombra de debajo del mueble.
     */
    fun DrawScope.zocalo(g: MarcoAntiguas, arriba: Float) {
        val w = g.w; val u = density
        val am = g.am
        fun c(col: Color, f: Float = 0f) = am.dia(col, f, 1f)
        val y0 = minOf(arriba + 1f, g.base); val y1 = g.suelo
        val z = w * MuebleEstanteAntiguas.ZOCALO
        fun y(f: Float) = g.base + z * f
        // La sombra de debajo, entre los pies.
        drawRect(am.sombra(0.8f), Offset(0f, y(0.58f)), Size(w, y1 - y(0.58f) + 1f))
        drawRect(c(P.ROBLE), Offset(0f, y0), Size(w, y(0.6f) - y0))
        // La moldura de arriba.
        drawRect(c(P.ROBLE_LUZ, 0.12f), Offset(0f, y(0f)), Size(w, z * 0.05f))
        drawRect(c(P.ROBLE_OSC), Offset(0f, y(0.05f)), Size(w, z * 0.04f))
        drawRect(c(P.ORO), Offset(0f, y(0.09f)), Size(w, z * 0.025f))
        // La banda con los rombos.
        val b0 = y(0.12f); val b1 = y(0.46f)
        drawRect(c(P.ROBLE, -0.04f), Offset(0f, b0), Size(w, b1 - b0))
        vetas(Rect(0f, b0, w, b1), c(P.ROBLE_OSC).copy(alpha = 0.4f), 7951, densidad = 0.7f, grosor = 0.6f, ondulacion = 0.6f)
        val rr = (b1 - b0) * 0.36f
        val paso = rr * 3.2f
        var x = w * 0.06f
        val rombos = Path(); val sombras = Path(); val luces = Path()
        while (x < w) {
            val cy = (b0 + b1) / 2
            rombos.addPath(poligonoAntiguas(x, cy - rr, x + rr * 1.3f, cy, x, cy + rr, x - rr * 1.3f, cy))
            sombras.addPath(poligonoAntiguas(x, cy - rr, x - rr * 1.3f, cy, x - rr * 0.9f, cy, x, cy - rr * 0.65f))
            luces.addPath(poligonoAntiguas(x, cy + rr, x + rr * 1.3f, cy, x + rr * 0.9f, cy, x, cy + rr * 0.65f))
            x += paso
        }
        drawPath(rombos, c(P.ROBLE_OSC))
        drawPath(sombras, am.sombra(0.5f))
        drawPath(luces, c(P.ROBLE_LUZ, 0.12f))
        var xc = w * 0.06f
        while (xc < w) { remacheAntiguas(Offset(xc, (b0 + b1) / 2), 1.6f * u, am, 1f, P.LATON); xc += paso }
        // La moldura de abajo.
        drawRect(c(P.ROBLE_OSC), Offset(0f, b1), Size(w, z * 0.04f))
        drawRect(c(P.ROBLE_LUZ, 0.1f), Offset(0f, y(0.5f)), Size(w, z * 0.04f))
        drawRect(c(P.ROBLE), Offset(0f, y(0.54f)), Size(w, z * 0.05f))
        drawRect(am.sombra(0.45f), Offset(0f, y(0.58f)), Size(w, z * 0.02f))
        // Los pies de bollo torneados: el collarín y la bola achatada, con su luz a la izquierda y su sombra en el suelo.
        listOf(0.045f, 0.5f, 0.955f).forEach { fx ->
            val cx = w * fx; val r = z * 0.22f
            val collar = Rect(cx - r * 0.9f, y(0.6f), cx + r * 0.9f, y(0.68f))
            val bola = Rect(cx - r * 1.35f, y(0.66f), cx + r * 1.35f, y1)
            drawOval(am.sombra(0.55f), Offset(bola.left + r * 0.5f, y1 - r * 0.3f), Size(bola.width * 1.15f, r * 0.6f))
            drawRect(c(P.ROBLE_OSC), collar.topLeft, collar.size)
            drawRect(c(P.ROBLE_LUZ, 0.1f), collar.topLeft, Size(collar.width * 0.3f, collar.height))
            drawOval(c(P.ROBLE), bola.topLeft, bola.size)
            clipPath(Path().apply { addOval(bola) }) {
                drawRect(c(P.ROBLE_LUZ, 0.12f), Offset(bola.left + bola.width * 0.12f, bola.top), Size(bola.width * 0.22f, bola.height))
                drawRect(c(P.ROBLE_LUZ, 0.3f), Offset(bola.left + bola.width * 0.2f, bola.top), Size(bola.width * 0.06f, bola.height))
                drawRect(c(P.ROBLE_OSC), Offset(bola.left + bola.width * 0.62f, bola.top), Size(bola.width * 0.38f, bola.height))
                drawRect(am.sombra(0.35f), Offset(bola.left, bola.bottom - bola.height * 0.25f), Size(bola.width, bola.height * 0.25f))
            }
            drawLine(c(P.ROBLE_HONDO), Offset(bola.left + r * 0.3f, bola.top + 1f * u), Offset(bola.right - r * 0.3f, bola.top + 1f * u), 1f * u)
        }
    }
}

/**
 * El fondo de los casilleros: las pinceladas del azul y el estarcido apagado, armados una vez por tamaño y pegados en cada
 * casillero (no se rehacen por dibujo).
 */
internal object FondoCasilleroAntiguas {
    private var clave = ""
    private val claros = Path()
    private val hondos = Path()
    private val trama = Path()

    private fun armar(ancho: Float, alto: Float, u: Float) {
        val c = "$ancho:$alto:$u"
        if (c == clave) return
        claros.reset(); hondos.reset(); trama.reset()
        for (i in 0 until 16) {
            val x = ancho * azar(7960, i); val y = alto * (0.2f + 0.75f * azar(7961, i))
            val l = (10f + 20f * azar(7962, i)) * u
            val p = if (i % 2 == 0) claros else hondos
            p.addOval(Rect(x - l / 2, y - l * 0.12f, x + l / 2, y + l * 0.12f))
        }
        val paso = ancho * 0.12f
        var x = -alto
        while (x < ancho + alto) {
            trama.moveTo(x, 0f); trama.lineTo(x + alto, alto)
            trama.moveTo(x + alto, 0f); trama.lineTo(x, alto)
            x += paso
        }
        clave = c
    }

    fun pintar(ds: DrawScope, r: Rect, alto: Float, claro: Color, hondo: Color, oro: Color) {
        val u = ds.density
        armar(ceil(r.width), ceil(alto), u)
        ds.clipRect(r.left, r.top, r.right, r.bottom) {
            translate(r.left, r.top) {
                drawPath(claros, claro.copy(alpha = 0.12f))
                drawPath(hondos, hondo.copy(alpha = 0.18f))
                drawPath(trama, oro.copy(alpha = 0.1f), style = Stroke(1f * u))
            }
        }
    }
}
