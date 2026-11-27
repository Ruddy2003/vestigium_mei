package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
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
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El mueble de Frente: el estante abierto del archivo, de chapa esmaltada color hueso, de la misma serie que la consola del
 * Ideario. La capota curva con sus dos molduras redondas y su filete de cromo, el friso de consola oscura con la fila de pilotos y
 * la rejilla; los costados gruesos de canto redondo con un piloto o una palanca por nivel; cada casillero, una boca de esquinas
 * redondas abierta a la piedra, con la cara de dentro de los costados y el suelo de la bandeja; cada repisa, chapa doblada con su
 * labio redondo, el filo de cromo y el portaetiquetas. Con su desgaste: la pintura saltada, la humedad y el óxido de los tornillos.
 */
internal object ObraEstanteFrente {
    private val P = PaletaFrente
    private val M = MuebleEstanteFrente

    /** El radio de las esquinas de arriba de cada casillero: la curva grande de las consolas. */
    private const val RADIO = 12f

    /** La luz de la cúpula de día en ([x], [y]) del lienzo: para pintar más clara la chapa cerca de la lámpara. */
    private fun DrawScope.charco(x: Float, y: Float): Float {
        val w = size.width
        return M.cupula(x / w - M.CUPULA_X, y / w - M.CUPULA_Y)
    }

    /** El color de [c] en ([x], [y]): de día con el charco de la cúpula; de noche, con la luz que le llega. */
    private fun DrawScope.en(c: Color, f: Float, x: Float, y: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float, dia: Float = 0.3f): Color =
        if (a.oscuro) a.luz(c, cerca(Offset(x, y)), frio(Offset(x, y))) else a.cupula(a.dia(c, f, cerca(Offset(x, y))), charco(x, y) * dia)

    /** Una franja horizontal [r] de [c] con la luz cambiando a lo ancho en un degradado (para lo que se guarda en la imagen). */
    private fun DrawScope.franjaHSuave(r: Rect, c: Color, f: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float, dia: Float = 0.3f) {
        if (r.width <= 0f || r.height <= 0f) return
        val y = r.center.y
        val paradas = Array(5) { i -> val x = r.left + r.width * i / 4f; (i / 4f) to en(c, f, x, y, a, cerca, frio, dia) }
        drawRect(Brush.horizontalGradient(*paradas, startX = r.left, endX = r.right), r.topLeft, r.size)
    }

    /**
     * Una franja horizontal [r] de [c], con la luz cambiando a lo ancho: en tramos lisos y opacos (en el teléfono, un degradado o
     * un velo cuestan por cada píxel mucho más que un color liso), seis de noche y tres de día; el paso de uno a otro es menor que
     * lo que se ve.
     */
    private fun DrawScope.franjaH(r: Rect, c: Color, f: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float, dia: Float = 0.3f) {
        if (r.width <= 0f || r.height <= 0f) return
        val n = if (a.lumbre) 12 else 4
        val y = r.center.y
        val paso = r.width / n
        for (i in 0 until n) {
            val x0 = r.left + paso * i
            drawRect(en(c, f, x0 + paso / 2, y, a, cerca, frio, dia), Offset(x0, r.top), Size(paso + (if (i < n - 1) 1f else 0f), r.height))
        }
    }

    /**
     * Una franja vertical [r] de [c], con la luz cambiando a lo alto: en tramos lisos y opacos, tres por cada tramo entre los
     * puntos [ys] (de arriba abajo, dentro de [r]); de día, uno solo, que la luz de la sala no cambia a lo alto.
     */
    private fun DrawScope.franjaV(r: Rect, c: Color, f: Float, ys: FloatArray, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float, dia: Float = 0.3f) {
        if (r.width <= 0f || r.height <= 0f) return
        val x = r.center.x
        if (!a.lumbre) { drawRect(en(c, f, x, r.top + r.height * 0.3f, a, cerca, frio, dia), r.topLeft, r.size); return }
        for (i in 0 until ys.size - 1) {
            val y0 = maxOf(r.top, ys[i]); val y1 = minOf(r.bottom, ys[i + 1])
            if (y1 <= y0) continue
            val paso = (y1 - y0) / 3f
            for (j in 0 until 3) {
                val ya = y0 + paso * j
                drawRect(en(c, f, x, ya + paso / 2, a, cerca, frio, dia), Offset(r.left, ya), Size(r.width, paso + (if (i < ys.size - 2 || j < 2) 1f else 0f)))
            }
        }
    }

    /** El pincel de [franjaV], para pintar con él una figura cualquiera sin recortarla (recortar con un camino es caro). */
    private fun DrawScope.pincelV(r: Rect, c: Color, f: Float, ys: FloatArray, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float, dia: Float = 0.3f): Brush {
        val x = r.center.x
        val alto = maxOf(1f, r.height)
        val paradas = Array(ys.size) { i -> ((ys[i] - r.top) / alto).coerceIn(0f, 1f) to en(c, f, x, ys[i], a, cerca, frio, dia) }
        return Brush.verticalGradient(*paradas, startY = r.top, endY = r.bottom)
    }

    // ---------------------------------------------------------------- la capota y el friso

    /**
     * La capota en [r] (de donde pisan las cosas de arriba hasta el primer casillero): la cara de arriba, el labio curvo de
     * esquinas redondas, las dos molduras redondas, el filete de cromo y el friso de consola oscura con su fila de pilotos, su
     * rejilla de aire y dos palancas.
     */
    fun DrawScope.capota(r: Rect, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = r.width
        val y0 = r.top
        val rad = w * 0.04f
        // La cara de arriba, donde pisan las cosas: vista apenas desde arriba, clara (mira a la cúpula), con su canto de atrás.
        val cara = Rect(r.left + w * 0.012f, y0, r.right - w * 0.012f, y0 + w * 0.024f)
        val formaCara = Path().apply { addRoundRect(RoundRect(cara, CornerRadius(rad, w * 0.02f), CornerRadius(rad, w * 0.02f), CornerRadius.Zero, CornerRadius.Zero)) }
        clipPath(formaCara) {
            franjaHSuave(Rect(cara.left, cara.top, cara.right, cara.top + cara.height * 0.3f), P.HUESO_OSC, -0.06f, a, cerca, frio, 0.4f)
            franjaHSuave(Rect(cara.left, cara.top + cara.height * 0.3f, cara.right, cara.bottom + 1f), P.HUESO_LUZ, 0.04f, a, cerca, frio, 0.6f)
            for (j in 0 until 5) {
                val x = cara.left + cara.width * azar(331, j); val y = cara.top + cara.height * (0.3f + 0.5f * azar(333, j))
                drawOval(a.sombra(0.1f), Offset(x, y), Size((6f + 14f * azar(337, j)) * u, 1.6f * u))
            }
        }
        drawLine(a.sombra(0.3f), Offset(cara.left + rad * 0.6f, cara.top + 0.6f * u), Offset(cara.right - rad * 0.6f, cara.top + 0.6f * u), 0.8f * u)
        val labio = Rect(r.left, cara.bottom, r.right, cara.bottom + w * 0.052f)
        // El labio: chapa doblada en curva, de esquinas redondas arriba.
        val forma = Path().apply { addRoundRect(RoundRect(labio, CornerRadius(rad * 0.7f), CornerRadius(rad * 0.7f), CornerRadius.Zero, CornerRadius.Zero)) }
        clipPath(forma) {
            val alto = labio.height
            // El canto curvo: la luz de arriba, el brillo que corre por la curva y la panza que se va a la sombra.
            franjaHSuave(Rect(labio.left, labio.top, labio.right, labio.top + alto * 0.14f), P.HUESO_LUZ, 0.14f, a, cerca, frio, 0.55f)
            franjaHSuave(Rect(labio.left, labio.top + alto * 0.14f, labio.right, labio.top + alto * 0.38f), P.HUESO_LUZ, 0.02f, a, cerca, frio, 0.5f)
            franjaHSuave(Rect(labio.left, labio.top + alto * 0.38f, labio.right, labio.top + alto * 0.62f), P.HUESO, -0.03f, a, cerca, frio, 0.35f)
            franjaHSuave(Rect(labio.left, labio.top + alto * 0.62f, labio.right, labio.top + alto * 0.84f), P.HUESO_OSC, -0.1f, a, cerca, frio, 0.22f)
            franjaHSuave(Rect(labio.left, labio.top + alto * 0.84f, labio.right, labio.bottom + 1f), P.HUESO_OSC, -0.3f, a, cerca, frio, 0.1f)
            // El brillo de la curva, más vivo del lado de la cúpula.
            drawRect(Brush.horizontalGradient(listOf(a.brillo(0.75f, cerca(Offset(w * 0.2f, labio.top))), a.brillo(0.2f, cerca(Offset(w * 0.8f, labio.top)))), labio.left, labio.right),
                Offset(labio.left, labio.top + alto * 0.24f), Size(labio.width, 1.3f * u))
            // Las esquinas, más hondas: la curva da la vuelta.
            drawRect(Brush.horizontalGradient(0f to a.sombra(0.35f), 0.05f to Color.Transparent, 0.95f to Color.Transparent, 1f to a.sombra(0.45f), startX = labio.left, endX = labio.right), labio.topLeft, labio.size)
            // La humedad que baja desde arriba.
            for (j in 0 until 4) {
                val x = labio.left + w * (0.1f + 0.8f * azar(311, j))
                val largo = labio.height * (0.5f + 0.5f * azar(313, j))
                val an = (1.4f + 2.6f * azar(315, j)) * u
                drawRect(Brush.verticalGradient(listOf(a.sombra(0.16f), Color.Transparent), labio.top, labio.top + largo), Offset(x - an / 2, labio.top), Size(an, largo))
            }
            // El esmalte saltado, como en la consola del Ideario: el acero oscuro, el óxido alrededor y el canto claro del esmalte,
            // más en las esquinas y en el canto de arriba, donde se golpea.
            val saltados = listOf(0.015f to 0.3f, 0.06f to 0.55f, 0.97f to 0.4f, 0.93f to 0.7f, 0.22f to 0.2f, 0.41f to 0.65f, 0.58f to 0.25f, 0.74f to 0.5f, 0.86f to 0.18f, 0.32f to 0.8f)
            saltados.forEachIndexed { j, (fx, fy) ->
                val x = labio.left + w * fx; val y = labio.top + alto * fy
                val sz = (2.2f + 4.2f * azar(323, j)) * u
                val p = contornoIrregular(Rect(x - sz / 2, y - sz * 0.3f, x + sz / 2, y + sz * 0.3f), 900 + j, 7, 0.3f)
                if (j % 3 == 0) drawOval(en(P.OXIDO, 0f, x, y, a, cerca, frio).copy(alpha = 0.3f), Offset(x - sz * 0.8f, y - sz * 0.45f), Size(sz * 1.6f, sz * 0.9f))
                translate(0f, 0.7f * u) { drawPath(p, a.claro(0.5f)) }
                drawPath(p, en(P.ACERO, -0.1f, x, y, a, cerca, frio))
            }
        }
        // La línea de sombra bajo el labio, en la primera moldura.
        drawLine(a.sombra(0.65f), Offset(labio.left + 1f * u, labio.bottom + 0.6f * u), Offset(labio.right - 1f * u, labio.bottom + 0.6f * u), 1.4f * u)
        drawLine(a.brillo(0.8f, cerca(Offset(w * 0.2f, labio.top))), Offset(labio.left + rad * 0.6f, labio.top + 1f * u), Offset(labio.right - rad * 0.6f, labio.top + 1f * u), 1.1f * u, StrokeCap.Round)
        // Las dos molduras redondas, como anillos de la consola, y el filete de cromo.
        var y = labio.bottom
        for (k in 0 until 2) {
            val m = Rect(r.left + w * 0.006f, y, r.right - w * 0.006f, y + w * 0.012f)
            franjaHSuave(Rect(m.left, m.top, m.right, m.top + m.height * 0.4f), P.HUESO_LUZ, 0.06f, a, cerca, frio, 0.45f)
            franjaHSuave(Rect(m.left, m.top + m.height * 0.4f, m.right, m.bottom), P.HUESO_OSC, -0.08f, a, cerca, frio, 0.3f)
            drawLine(a.sombra(0.5f), Offset(m.left, m.bottom - 0.3f * u), Offset(m.right, m.bottom - 0.3f * u), 0.8f * u)
            y = m.bottom
        }
        val cromo = Rect(r.left, y, r.right, y + w * 0.009f)
        franjaHSuave(Rect(cromo.left, cromo.top, cromo.right, cromo.center.y), P.CROMO_LUZ, 0f, a, cerca, frio, 0.3f)
        franjaHSuave(Rect(cromo.left, cromo.center.y, cromo.right, cromo.bottom), P.CROMO_OSC, 0f, a, cerca, frio, 0.3f)
        // El friso: un panel de consola oscura, metido entre la capota y el primer casillero.
        val friso = Rect(r.left, cromo.bottom, r.right, r.bottom)
        franjaHSuave(friso, P.CONSOLA, 0f, a, cerca, frio, 0.2f)
        drawRect(a.sombra(0.55f), friso.topLeft, Size(friso.width, 3f * u))
        drawRect(a.brillo(0.18f, cerca(Offset(w * 0.3f, friso.bottom))), Offset(friso.left, friso.bottom - 4.6f * u), Size(friso.width, 0.8f * u))
        val cy = friso.top + (friso.bottom - 2f - friso.top) * 0.5f
        // La fila de pilotos (ámbar, alguno apagado, uno rojo y uno verde), cada uno con su tornillo.
        val luces = listOf(P.AMBAR, P.AMBAR, null, P.RADAR, P.AMBAR, P.ROJO, null)
        val rp = minOf(2.6f * u, friso.height * 0.16f)
        for (i in luces.indices) {
            val x = r.left + w * (0.12f + 0.045f * i)
            val luz = luces[i]
            val k = cerca(Offset(x, cy))
            pilotoFrente(Offset(x, cy), rp, luz ?: P.AMBAR, luz != null && !(a.dormida && i % 2 == 0), a, k)
        }
        // La rejilla de aire, de ranuras redondeadas.
        val rej = Rect(r.left + w * 0.47f, cy - friso.height * 0.24f, r.left + w * 0.74f, cy + friso.height * 0.24f)
        var x = rej.left
        while (x < rej.right - 1f) {
            val k = cerca(Offset(x, cy))
            drawRoundRect(a.luz(Color.Black, 0f).copy(alpha = 0.85f), Offset(x, rej.top), Size(2.2f * u, rej.height), CornerRadius(1.1f * u))
            drawLine(a.brillo(0.16f, k), Offset(x + 0.4f * u, rej.bottom + 1f * u), Offset(x + 2f * u, rej.bottom + 1f * u), 0.6f * u)
            x += 4.4f * u
        }
        // Dos palancas chicas de baquelita, una arriba y otra abajo.
        listOf(0.81f to true, 0.87f to false).forEach { (f, arriba) ->
            val c = Offset(r.left + w * f, cy)
            val k = cerca(c)
            drawCircle(a.luz(P.CROMO_OSC, k), friso.height * 0.17f, c)
            drawCircle(a.luz(P.CROMO, k), friso.height * 0.12f, c)
            val punta = Offset(c.x + 0.6f * u, c.y + (if (arriba) -1f else 1f) * friso.height * 0.3f)
            drawLine(a.sombra(0.4f), Offset(c.x + 1f * u, c.y + 1.2f * u), Offset(punta.x + 1f * u, punta.y + 1.2f * u), 1.6f * u, StrokeCap.Round)
            drawLine(a.luz(P.CROMO_LUZ, k), c, punta, 1.4f * u, StrokeCap.Round)
            drawCircle(a.luz(P.BAQUELITA, k), 1.6f * u, punta)
        }
        listOf(r.left + w * 0.04f, r.right - w * 0.04f).forEachIndexed { i, xx -> tornilloFrente(Offset(xx, cy), 1.8f * u, a, cerca(Offset(xx, cy)), 30f + 70f * i, frio(Offset(xx, cy))) }
    }

    // ---------------------------------------------------------------- cada casillero

    /**
     * El casillero [k] en [r]: la piedra del muro por detrás, en la sombra del mueble (la de la tabla de arriba y la del costado
     * de la luz, francas), la cara de dentro de los costados, la panza de la tabla de arriba, el suelo de la bandeja, las esquinas
     * redondas de la boca y, de noche, la luz que le llega (y el charco del foco, si en este hueco hay uno en [xFoco]).
     */
    fun DrawScope.casillero(k: Int, r: Rect, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float, xFoco: Float) {
        val u = density
        val w = size.width
        val af = AireFrente(a.oscuro, a.noche, fondo = true)
        // La piedra, con el hondo del nicho ya pintado en la baldosa: su borde de arriba, en el de este casillero.
        with(MuroEstanteFrente) { piedraEn(r, af, k * 137f * u, r.top) }
        // De noche, la piedra ya viene hundida en la baldosa: solo se enciende donde llega una luz (el lado de la cúpula, el del
        // vidrio, el del cargador), con un resplandor que se apaga hacia el otro lado.
        if (a.oscuro) {
            val y = r.top + r.height * 0.45f
            val kI = cerca(Offset(r.left, y)); val kD = cerca(Offset(r.right, y)); val vD = frio(Offset(r.right, y))
            val kMax = maxOf(kI, kD)
            if (kMax > 0.18f) {
                val izqMas = kI >= kD
                val c = Offset(if (izqMas) r.left else r.right, r.top + r.height * 0.3f)
                val rr = r.width * (0.35f + 0.4f * kMax)
                drawRect(Brush.radialGradient(listOf(P.LAMPARA.copy(alpha = ((kMax - 0.12f) * 0.5f).coerceAtMost(0.3f)), P.LAMPARA.copy(alpha = 0f)), c, rr),
                    Offset(if (izqMas) r.left else maxOf(r.left, r.right - rr), r.top), Size(minOf(rr, r.width), r.height))
            }
            if (vD > 0.22f) {
                val c = Offset(r.right, r.top + r.height * 0.2f)
                val rr = r.width * 0.45f
                drawRect(Brush.radialGradient(listOf(P.VIDRIO_TRAZO.copy(alpha = ((vD - 0.18f) * 0.4f).coerceAtMost(0.2f)), P.VIDRIO_TRAZO.copy(alpha = 0f)), c, rr),
                    Offset(r.right - rr, r.top), Size(rr, r.height))
            }
            if (xFoco >= 0f) {
                val c = Offset(xFoco, r.bottom - r.height * M.FOCO_Y)
                val rr = w * 0.25f
                drawCircle(Brush.radialGradient(0f to P.FOCO.copy(alpha = if (a.noche) 0.28f else 0.46f), 0.35f to P.FOCO.copy(alpha = if (a.noche) 0.1f else 0.18f),
                    1f to P.FOCO.copy(alpha = 0f), center = c, radius = rr), rr, c)
            }
        }
        // La sombra franca del costado de la luz y de la tabla de arriba en el fondo, con su penumbra.
        val izq = r.left + 6f * u
        val hT = r.height * 0.2f; val wL = w * 0.055f
        val sombra = Path().apply {
            moveTo(r.left, r.top + hT * 0.6f); lineTo(izq + wL + hT * 0.5f, r.top + hT * 0.6f); lineTo(izq + wL, r.top + hT + wL * 0.4f)
            lineTo(izq + wL * 0.85f, r.bottom); lineTo(r.left, r.bottom); close()
        }
        drawPath(sombra, a.sombra(if (a.oscuro) 0.3f else 0.38f))
        // La cara de dentro de los costados: la de la izquierda, en sombra; la de la derecha, mirando a la luz.
        val hondo = 6f * u
        val ys = floatArrayOf(r.top, r.center.y, r.bottom)
        val caraIzq = Path().apply { moveTo(r.left, r.top); lineTo(r.left + hondo, r.top + hondo * 0.8f); lineTo(r.left + hondo, r.bottom - 3f * u); lineTo(r.left, r.bottom); close() }
        drawPath(caraIzq, pincelV(Rect(r.left, r.top, r.left + hondo, r.bottom), P.HUESO_OSC, -0.35f, ys, a, cerca, frio, 0.05f))
        drawLine(a.sombra(0.35f), Offset(r.left + hondo, r.top + hondo * 0.8f), Offset(r.left + hondo, r.bottom - 3f * u), 0.8f * u)
        val caraDer = Path().apply { moveTo(r.right, r.top); lineTo(r.right - hondo, r.top + hondo * 0.8f); lineTo(r.right - hondo, r.bottom - 3f * u); lineTo(r.right, r.bottom); close() }
        drawPath(caraDer, pincelV(Rect(r.right - hondo, r.top, r.right, r.bottom), P.HUESO, 0.02f, ys, a, cerca, frio, 0.25f))
        drawLine(a.sombra(0.25f), Offset(r.right - hondo, r.top + hondo * 0.8f), Offset(r.right - hondo, r.bottom - 3f * u), 0.7f * u)
        // La panza de la tabla de arriba, en sombra.
        val panza = Rect(r.left, r.top, r.right, r.top + 4f * u)
        franjaH(panza, P.HUESO_OSC, -0.45f, a, cerca, frio, 0f)
        drawLine(a.sombra(0.5f), Offset(r.left, panza.bottom), Offset(r.right, panza.bottom), 0.8f * u)
        // El suelo de la bandeja, visto apenas desde arriba: la chapa con su luz y su borde de atrás.
        val suelo = Rect(r.left + hondo * 0.5f, r.bottom - 4.2f * u, r.right - hondo * 0.5f, r.bottom)
        franjaH(suelo, P.HUESO, 0.02f, a, cerca, frio, 0.3f)
        drawLine(a.sombra(0.45f), Offset(suelo.left, suelo.top), Offset(suelo.right, suelo.top), 0.9f * u)
        // Las esquinas redondas de la boca: la chapa del frente, con el canto de su grueso.
        val rad = RADIO * u
        listOf(true, false).forEach { esIzq ->
            val x0 = if (esIzq) r.left else r.right - rad
            val esquina = Path().apply {
                if (esIzq) { moveTo(r.left, r.top); lineTo(r.left + rad, r.top); quadraticTo(r.left, r.top, r.left, r.top + rad); close() }
                else { moveTo(r.right, r.top); lineTo(r.right - rad, r.top); quadraticTo(r.right, r.top, r.right, r.top + rad); close() }
            }
            drawPath(esquina, en(P.HUESO, 0f, x0, r.top, a, cerca, frio))
            val canto = Path().apply {
                if (esIzq) { moveTo(r.left + rad, r.top); quadraticTo(r.left, r.top, r.left, r.top + rad) }
                else { moveTo(r.right - rad, r.top); quadraticTo(r.right, r.top, r.right, r.top + rad) }
            }
            drawPath(canto, if (esIzq) a.sombra(0.45f) else a.brillo(0.6f, cerca(Offset(r.right, r.top))), style = Stroke(1.2f * u))
        }
    }

    // ---------------------------------------------------------------- las tablas

    /**
     * La tabla [k] en [r]: la bandeja de chapa doblada en un labio redondo (luz arriba, sombra abajo), el filo de cromo, la cara
     * de chapa con su pintura saltada, dos tornillos con su chorreón de óxido y la línea de sombra que echa abajo.
     */
    fun DrawScope.tabla(k: Int, r: Rect, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val h = r.height
        // Debajo, la fila entera (de píxel entero) del color de la cara, para que la junta con el casillero no deje asomar nada.
        drawRect(en(P.HUESO, 0f, r.center.x, r.center.y, a, cerca, frio), Offset(r.left, kotlin.math.floor(r.top)), Size(r.width, kotlin.math.ceil(r.bottom) - kotlin.math.floor(r.top)))
        franjaH(Rect(r.left, r.top, r.right, r.top + h * 0.1f), P.HUESO_LUZ, 0.2f, a, cerca, frio, 0.5f)
        franjaH(Rect(r.left, r.top + h * 0.1f, r.right, r.top + h * 0.3f), P.HUESO, 0.03f, a, cerca, frio, 0.4f)
        franjaH(Rect(r.left, r.top + h * 0.3f, r.right, r.top + h * 0.44f), P.HUESO_OSC, -0.08f, a, cerca, frio, 0.25f)
        // El filo de cromo bajo el labio.
        franjaH(Rect(r.left, r.top + h * 0.44f, r.right, r.top + h * 0.55f), P.CROMO, 0f, a, cerca, frio, 0.2f)
        drawRect(a.brillo(0.35f, cerca(r.center)), Offset(r.left, r.top + h * 0.44f), Size(r.width, h * 0.035f))
        // La cara de la bandeja.
        franjaH(Rect(r.left, r.top + h * 0.55f, r.right, r.bottom), P.HUESO, -0.04f, a, cerca, frio, 0.3f)
        drawRect(a.sombra(0.14f), Offset(r.left, r.bottom - h * 0.16f), Size(r.width, h * 0.16f))
        // La pintura saltada, sobre todo en el labio, donde se rozan las cosas.
        for (j in 0 until 7) {
            val x = r.left + r.width * azar(k * 7 + 3, j)
            val y = r.top + h * (0.1f + 0.75f * azar(k * 7 + 4, j))
            val sz = (1.8f + 3.6f * azar(k * 7 + 5, j)) * u
            drawPath(contornoIrregular(Rect(x, y, x + sz, y + sz * 0.55f), k * 31 + j, 6, 0.3f), en(P.ACERO, 0f, x, y, a, cerca, frio).copy(alpha = 0.72f))
        }
        // Los dos tornillos de las puntas, con el chorreón de óxido que baja.
        listOf(r.left + 9f * u, r.right - 9f * u).forEachIndexed { i, x ->
            val c = Offset(x, r.top + h * 0.75f)
            val kk = cerca(c)
            drawLine(en(P.OXIDO, 0f, x, c.y, a, cerca, frio).copy(alpha = 0.45f), Offset(x, c.y + 1f * u), Offset(x + 0.4f * u, r.bottom), 1.6f * u, StrokeCap.Round)
            tornilloFrente(c, 1.9f * u, a, kk, 20f + 61f * (k + i), frio(c))
        }
        drawRect(a.sombra(0.5f), Offset(r.left, r.bottom - 0.9f * u), Size(r.width, 0.9f * u))
        // En los Sueños oscuros, el filo frío del vidrio en el canto del labio, más vivo del lado del vidrio.
        if (a.dormida) for (i in 0 until 6) {
            val x0 = r.left + r.width * i / 6f
            val kv = frio(Offset(x0 + r.width / 12f, r.top))
            if (kv > 0.06f) drawRect(a.filoVidrio((1.2f * kv).coerceAtMost(0.8f)), Offset(x0, r.top + 0.4f * u), Size(r.width / 6f + 1f, 1.2f * u))
        }
    }

    // ---------------------------------------------------------------- los costados

    /**
     * El costado en [r] ([izq]: el de la izquierda): el montante de chapa de canto redondo (luz del lado de la cúpula, sombra del
     * otro), el canal con su filete de cromo y, en cada nivel, un piloto (a la izquierda) o una palanca (a la derecha) con sus
     * tornillos. Por el de la izquierda baja una raíz desde la capota, como las que caen por los costados de la consola del Ideario.
     */
    fun DrawScope.costado(r: Rect, izq: Boolean, d: DisposicionEstante, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        if (r.height <= 1f || r.width <= 1f) return
        // Los puntos donde se mide la luz a lo alto: arriba, en cada repisa y abajo.
        val ys = FloatArray(d.repisas.size + 2)
        ys[0] = r.top
        d.repisas.forEachIndexed { k, rep -> ys[k + 1] = rep.center.y.coerceIn(r.top, r.bottom) }
        ys[ys.size - 1] = r.bottom
        // Debajo, un píxel más hacia dentro del mueble, para que la junta con los casilleros (que puede caer a medio píxel) no
        // deje asomar el fondo.
        franjaV(Rect(if (izq) r.right - 2f else r.left - 1f, r.top, if (izq) r.right + 1f else r.left + 2f, r.bottom), P.HUESO, if (izq) -0.32f else 0.24f, ys, a, cerca, frio, 0.35f)
        // Los dos cantos redondos de chapa hueso, de la luz a la sombra a lo ancho; cada franja con su luz a lo alto.
        val franjas = floatArrayOf(0f, 0.06f, 0.14f, 0.24f, 0.76f, 0.86f, 0.94f, 1f)
        val tonos = floatArrayOf(0.24f, 0.12f, 0f, -0.1f, -0.06f, -0.16f, -0.32f)
        for (i in 0 until 7) {
            val x0 = r.left + r.width * franjas[i]; val x1 = r.left + r.width * franjas[i + 1]
            franjaV(Rect(x0, r.top, x1 + (if (i < 6) 1f else 0f), r.bottom), if (tonos[i] > 0f) P.HUESO_LUZ else P.HUESO, tonos[i], ys, a, cerca, frio, 0.35f)
        }
        drawLine(a.sombra(0.45f), Offset(r.left + 0.4f * u, r.top), Offset(r.left + 0.4f * u, r.bottom), 0.9f * u)
        drawLine(a.sombra(0.55f), Offset(r.right - 0.5f * u, r.top), Offset(r.right - 0.5f * u, r.bottom), 1f * u)
        // El panel de consola oscura metido entre los dos cantos, con su filete de cromo y la sombra del canto de la luz.
        val panel = Rect(r.left + r.width * 0.24f, r.top, r.left + r.width * 0.76f, r.bottom)
        franjaV(panel, P.CONSOLA, 0f, ys, a, cerca, frio, 0.15f)
        drawRect(a.sombra(0.5f), panel.topLeft, Size(2.2f * u, panel.height))
        franjaV(Rect(panel.right - 1f * u, panel.top, panel.right, panel.bottom), P.CROMO, 0f, ys, a, cerca, frio, 0.3f)
        drawRect(Brush.verticalGradient(listOf(a.sombra(0.55f), Color.Transparent), panel.top, panel.top + 14f * u), panel.topLeft, Size(panel.width, 14f * u))
        // La humedad que baja desde la capota por el canto de fuera.
        val xh = if (izq) r.left + r.width * 0.1f else r.right - r.width * 0.12f
        drawRect(Brush.verticalGradient(listOf(a.sombra(0.2f), Color.Transparent), r.top, r.top + w * 0.5f), Offset(xh - 2f * u, r.top), Size(4f * u, w * 0.5f))
        // En cada nivel, en el panel: un piloto (a la izquierda) o una palanca (a la derecha), la rejilla de tres ranuras y los
        // tornillos de la chapa.
        val cx = panel.center.x
        d.repisas.forEachIndexed { k, rep ->
            val s = k * 13 + (if (izq) 0 else 7)
            val c = Offset(cx, rep.top + rep.height * 0.24f)
            val kk = cerca(c); val kv = frio(c)
            if (izq) {
                val prendido = a.lumbre || azar(s, 3) > 0.3f
                val luz = if (azar(s, 5) > 0.82f) P.ROJO else P.AMBAR
                pilotoFrente(c, minOf(panel.width * 0.22f, 2.6f * u), luz, prendido && !(a.dormida && k % 2 == 1), a, kk)
            } else {
                val arriba = azar(s, 9) > 0.45f
                drawCircle(a.luz(P.CROMO_OSC, kk, kv), 3.2f * u, c)
                drawCircle(a.luz(P.CROMO, kk, kv), 2.3f * u, c)
                val punta = Offset(c.x + 0.6f * u, c.y + (if (arriba) -1f else 1f) * 6.5f * u)
                drawLine(a.sombra(0.5f), Offset(c.x + 1.2f * u, c.y + 1.4f * u), Offset(punta.x + 1.2f * u, punta.y + 1.4f * u), 2f * u, StrokeCap.Round)
                drawLine(a.luz(P.CROMO_LUZ, kk, kv), c, punta, 1.6f * u, StrokeCap.Round)
                drawCircle(a.luz(P.BAQUELITA_LUZ, kk, kv), 2f * u, punta)
                drawCircle(a.brillo(0.35f, kk), 0.7f * u, Offset(punta.x - 0.6f * u, punta.y - 0.6f * u))
            }
            for (j in 0 until 3) {
                val y = rep.top + rep.height * (0.5f + 0.07f * j)
                drawRoundRect(Color.Black.copy(alpha = 0.7f), Offset(panel.left + panel.width * 0.22f, y), Size(panel.width * 0.56f, 1.6f * u), CornerRadius(0.8f * u))
                drawLine(a.brillo(0.14f, kk), Offset(panel.left + panel.width * 0.22f, y + 2.1f * u), Offset(panel.right - panel.width * 0.22f, y + 2.1f * u), 0.5f * u)
            }
            listOf(rep.top + rep.height * 0.86f, rep.bottom + 9f * u).forEachIndexed { j, yy ->
                val t = Offset(cx, yy)
                tornilloFrente(t, 1.4f * u, a, cerca(t), 30f + 40f * (k + j), frio(t))
            }
            // El óxido que baja de un tornillo.
            if (azar(s, 11) > 0.5f) drawLine(en(P.OXIDO, 0f, cx, rep.bottom, a, cerca, frio).copy(alpha = 0.45f),
                Offset(cx, rep.top + rep.height * 0.88f), Offset(cx + 0.3f * u, rep.bottom + 4f * u), 1.3f * u, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- el rótulo del ciclo

    /**
     * El rótulo del ciclo en [p]: el portaetiquetas de cromo remachado al labio, con la cinta de papel metida dentro y el ciclo
     * escrito a máquina; teñido por la luz [k] que le llega.
     */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, a: AireFrente, k: Float, kv: Float) {
        val u = density
        val r = p.r
        val marco = r.inflate(1.6f * u)
        drawRoundRect(a.sombra(0.45f), Offset(marco.left + 0.8f * u, marco.top + 1.2f * u), marco.size, CornerRadius(2.4f * u))
        drawRoundRect(Brush.verticalGradient(listOf(a.luz(P.CROMO_LUZ, k, kv), a.luz(P.CROMO, k, kv), a.luz(P.CROMO_OSC, k, kv)), marco.top, marco.bottom), marco.topLeft, marco.size, CornerRadius(2.4f * u))
        val dentro = r.deflate(0.6f * u)
        drawRect(a.etiqueta(P.CINTA, k, kv), dentro.topLeft, dentro.size)
        drawRect(a.etiqueta(P.CINTA_OSC, k, kv).copy(alpha = 0.6f), Offset(dentro.left, dentro.bottom - dentro.height * 0.18f), Size(dentro.width, dentro.height * 0.18f))
        drawRect(a.sombra(0.3f), dentro.topLeft, Size(dentro.width, 1.2f * u))
        // Los dos remaches.
        listOf(marco.left + 2.4f * u, marco.right - 2.4f * u).forEach { x -> drawCircle(a.luz(P.CROMO_OSC, k, kv), 1.1f * u, Offset(x, marco.center.y)) }
        val caja = Rect(dentro.left + 4f * u, dentro.top, dentro.right - 4f * u, dentro.bottom)
        textoEn(medidor, p.texto, caja, P.LETRA, a.tinta(k), 9f, peso = FontWeight.Medium, espaciado = 0.4f, minimo = 5f)
    }
}
