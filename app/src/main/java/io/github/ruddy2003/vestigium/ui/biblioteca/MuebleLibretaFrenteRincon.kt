package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.TrazosFrente
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.atan2
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El rincón de la libreta de Frente: el muro de sillares con su tubo de cobre y sus raíces, el flexo atornillado al muro (lo
 * fijo, que se guarda en una imagen), el pupitre de chapa hueso con su labio curvo y su frente de consola oscura, el
 * osciloscopio con su onda verde y la grabadora de cartuchos con sus teclas.
 */
internal object RinconLibretaFrente {
    private val P = PaletaFrente
    private val L = MuebleLibretaFrente

    private fun DrawScope.en(c: Color, f: Float, p: Offset, s: SitioLibretaFrente, a: AireFrente) = L.en(c, f, p, s, a)

    /** La piedra a la luz del momento (de noche, con [k] de luz; de día, tal cual: el charco va encima). */
    private fun piedra(a: AireFrente, k: Float, c: Color) = if (a.oscuro) a.luz(c, k) else c

    // ---------------------------------------------------------------- el muro (fijo)

    /**
     * El muro de arriba, hasta la cara del pupitre: los sillares del Ideario, el charco tibio del flexo (de día, la piedra más
     * clara bajo su boca; de noche, el halo crema) y, de noche, el resplandor verde del osciloscopio y la penumbra lejos; las
     * raíces, el tubo de cobre con sus abrazaderas, el cable del osciloscopio que sube por el muro y el flexo.
     */
    fun DrawScope.muro(s: SitioLibretaFrente, a: AireFrente) {
        val u = density
        val w = s.w; val h = s.h
        val r = Rect(0f, 0f, w, s.mesa + 2f)
        val kMedio = if (a.oscuro) 0.2f else 1f
        val humedad = if (a.oscuro) Color(0xFF04100A).copy(alpha = 0.7f) else P.HUMEDAD.copy(alpha = 0.5f)
        with(TrazosFrente) {
            muroSillares(r, MuroEstanteFrente.HILADA * u, piedra(a, kMedio, P.PIEDRA), piedra(a, kMedio * 0.8f, P.PIEDRA_SOMBRA), humedad,
                piedra(a, kMedio, P.MUSGO), 9, if (a.oscuro) 0.3f else 1f)
        }
        val c = Offset(s.boca.x + h * 0.08f, s.boca.y + h * 0.1f)
        clipRect(r.left, r.top, r.right, r.bottom) {
            if (!a.oscuro) {
                drawOval(Brush.radialGradient(0f to Color(0xFFFFF0D2).copy(alpha = 0.5f), 0.45f to Color(0xFFFFF0D2).copy(alpha = 0.17f), 1f to Color(0xFFFFF0D2).copy(alpha = 0f),
                    center = c, radius = h * 0.78f), Offset(c.x - h * 0.78f, c.y - h * 0.78f), Size(h * 1.56f, h * 1.56f))
                drawRect(Brush.radialGradient(0f to Color.Transparent, 0.4f to Color.Transparent, 1f to a.sombra(0.34f), center = c, radius = h * 1.3f), r.topLeft, r.size)
            } else {
                drawOval(Brush.radialGradient(0f to P.LAMPARA.copy(alpha = 0.66f), 0.25f to P.LAMPARA.copy(alpha = 0.32f), 0.6f to P.LAMPARA.copy(alpha = 0.08f),
                    1f to P.LAMPARA.copy(alpha = 0f), center = c, radius = h * 0.7f), Offset(c.x - h * 0.7f, c.y - h * 0.7f), Size(h * 1.4f, h * 1.4f))
                val v = s.pantalla.center
                drawOval(Brush.radialGradient(0f to P.VIDRIO_TRAZO.copy(alpha = 0.26f), 0.5f to P.VIDRIO_TRAZO.copy(alpha = 0.07f), 1f to P.VIDRIO_TRAZO.copy(alpha = 0f),
                    center = v, radius = h * 0.55f), Offset(v.x - h * 0.55f, v.y - h * 0.55f), Size(h * 1.1f, h * 1.1f))
                drawRect(Brush.radialGradient(0f to Color.Transparent, 0.36f to Color.Transparent, 1f to a.hondo.copy(alpha = 0.72f),
                    center = Offset((c.x + v.x) / 2f, h * 0.45f), radius = maxOf(w * 0.62f, h)), r.topLeft, r.size)
            }
            MuroEstanteFrente.run {
                raices(listOf(Triple(w * 0.022f, h * 0.6f, 3.2f * u), Triple(s.cart.right + h * 0.075f, h * 0.17f, 2f * u), Triple(w * 0.978f, h * 0.48f, 3.4f * u)),
                    0f, a, { p -> L.cerca(s, p) }, 33)
            }
            // El cable del osciloscopio, que sube por el muro hasta el tubo.
            val x0 = s.scope.left + h * 0.36f
            val cable = Path().apply {
                moveTo(x0, s.scope.top + h * 0.05f)
                cubicTo(x0 + h * 0.02f, s.scope.top - h * 0.1f, x0 - h * 0.05f, h * 0.16f, x0 - h * 0.03f, h * 0.035f)
            }
            with(TrazosFrente) { cableGrueso(cable, 3.4f * u, piedra(a, 0.3f, P.BAQUELITA), if (a.oscuro) 0.3f else 1f, if (a.oscuro) 0.4f else 0.28f) }
        }
        // El tubo de cobre con sus abrazaderas, a la altura del del Ideario.
        val yTubo = h * 0.045f
        val kt = L.cerca(s, Offset(w * 0.3f, yTubo))
        with(TrazosFrente) { tubo(Offset(-2f * u, yTubo), Offset(w + 2f * u, yTubo), 4.2f * u, piedra(a, kt, P.COBRE), if (a.oscuro) 0.2f + 0.6f * kt else 1f, juntas = 90f * u) }
        listOf(0.12f, 0.46f, 0.82f).forEach { f ->
            val k = L.cerca(s, Offset(w * f, yTubo))
            drawRoundRect(a.sombra(0.3f), Offset(w * f - 2f * u, yTubo - 3f * u), Size(5 * u, 7f * u), CornerRadius(1 * u))
            drawRoundRect(piedra(a, k, P.CROMO_OSC), Offset(w * f - 2.5f * u, yTubo - 3.4f * u), Size(5 * u, 6.8f * u), CornerRadius(1 * u))
            drawLine(a.brillo(0.3f, k), Offset(w * f - 1.8f * u, yTubo - 2.8f * u), Offset(w * f - 1.8f * u, yTubo + 2.8f * u), 0.7f * u)
        }
        flexo(s, a)
        granoMueble(r, a.oscuro)
    }

    // ---------------------------------------------------------------- el flexo (fijo)

    /**
     * El flexo de taller, atornillado al muro: la placa de cromo con sus dos tornillos, los dos brazos de varilla doble con su
     * muelle, las rótulas de baquelita y la pantalla cónica de chapa hueso que apunta abajo a la derecha, a la grabadora, con
     * la boca encendida (de día y de noche: las lámparas de la base no se apagan). El cordón cuelga de la placa.
     */
    private fun DrawScope.flexo(s: SitioLibretaFrente, a: AireFrente) {
        val u = density
        val h = s.h
        val dl = sqrt(1f + 1.05f * 1.05f)
        val d = Offset(1f / dl, 1.05f / dl)
        val n = Offset(-d.y, d.x)
        val largo = h * 0.13f; val r1 = h * 0.07f; val r0 = h * 0.022f
        val boca = s.boca
        val cima = boca - d * largo
        val nuca = cima - d * (h * 0.022f)
        // El cordón, de la placa abajo por el muro.
        drawPath(Path().apply { moveTo(s.placa.x, s.placa.y + h * 0.05f); cubicTo(s.placa.x + h * 0.02f, s.placa.y + h * 0.2f, s.placa.x - h * 0.03f, s.mesa - h * 0.15f, s.placa.x + h * 0.04f, s.mesa + 2f * u) },
            a.sombra(0.5f), style = Stroke(1.6f * u))
        // La placa atornillada al muro, con su sombra.
        val pl = Rect(s.placa.x - h * 0.025f, s.placa.y - h * 0.055f, s.placa.x + h * 0.025f, s.placa.y + h * 0.055f)
        drawRoundRect(a.sombra(0.4f), pl.topLeft + Offset(2f * u, 2.4f * u), pl.size, CornerRadius(2f * u))
        drawRoundRect(piedra(a, 0.7f, P.CROMO_OSC), pl.topLeft, pl.size, CornerRadius(2f * u))
        drawRoundRect(piedra(a, 0.7f, P.CROMO), pl.topLeft, Size(pl.width * 0.6f, pl.height), CornerRadius(2f * u))
        tornilloFrente(Offset(pl.center.x, pl.top + h * 0.012f), 1.5f * u, a, 0.7f)
        tornilloFrente(Offset(pl.center.x, pl.bottom - h * 0.012f), 1.5f * u, a, 0.7f, 70f)
        // Los dos brazos, con su sombra en el muro.
        brazo(s.placa, s.codo, a, true)
        brazo(s.codo, nuca, a, false)
        listOf(s.placa, s.codo, nuca).forEach { o -> rotula(o, 2.6f * u, a) }
        // La pantalla cónica: su sombra, la mitad de arriba en luz y la de abajo en sombra, el filo y la boca encendida.
        val sombraPant = Offset(h * 0.03f, h * 0.025f)
        drawPath(poligonoFrente((cima + n * r0 + sombraPant).x, (cima + n * r0 + sombraPant).y, (boca + n * r1 + sombraPant).x, (boca + n * r1 + sombraPant).y,
            (boca - n * r1 + sombraPant).x, (boca - n * r1 + sombraPant).y, (cima - n * r0 + sombraPant).x, (cima - n * r0 + sombraPant).y), a.sombra(0.3f))
        val arriba = poligonoFrente(cima.x, cima.y, boca.x, boca.y, (boca - n * r1).x, (boca - n * r1).y, (cima - n * r0).x, (cima - n * r0).y)
        val abajo = poligonoFrente(cima.x, cima.y, boca.x, boca.y, (boca + n * r1).x, (boca + n * r1).y, (cima + n * r0).x, (cima + n * r0).y)
        val luzPant = if (a.oscuro) a.luz(P.HUESO, 0.35f) else P.HUESO_LUZ
        val sombraP = if (a.oscuro) a.luz(P.HUESO_OSC, 0.12f) else mezcla(P.HUESO_OSC, P.SOMBRA, 0.2f)
        drawPath(arriba, luzPant)
        drawPath(abajo, sombraP)
        drawLine(if (a.oscuro) a.filo(0.35f) else a.claro(0.8f), cima - n * (r0 * 0.6f), boca - n * (r1 * 0.75f), 1.2f * u, StrokeCap.Round)
        // El remate de atrás, donde entra el casquillo.
        drawCircle(piedra(a, 0.4f, P.CROMO_OSC), r0 * 1.15f, cima)
        drawCircle(piedra(a, 0.4f, P.CROMO), r0 * 0.7f, cima - n * (r0 * 0.25f))
        // La boca: el aro de cromo y, dentro, la luz.
        val giro = Math.toDegrees(atan2(n.y, n.x).toDouble()).toFloat()
        rotate(giro, boca) {
            drawOval(piedra(a, 0.8f, P.CROMO), Offset(boca.x - r1 * 1.04f, boca.y - r1 * 0.4f), Size(r1 * 2.08f, r1 * 0.8f))
            drawOval(P.LAMPARA, Offset(boca.x - r1 * 0.92f, boca.y - r1 * 0.3f), Size(r1 * 1.84f, r1 * 0.6f))
            drawOval(P.FUEGO, Offset(boca.x - r1 * 0.55f, boca.y - r1 * 0.14f), Size(r1 * 1.1f, r1 * 0.36f))
        }
        if (a.oscuro) {
            val hr = h * 0.3f
            drawCircle(Brush.radialGradient(listOf(P.LAMPARA.copy(alpha = 0.45f), P.LAMPARA.copy(alpha = 0.12f), P.LAMPARA.copy(alpha = 0f)), boca, hr), hr, boca)
        }
    }

    /** Un brazo del flexo de [p0] a [p1]: dos varillas color hueso con su sombra en el muro y el muelle entre ellas ([muelle]). */
    private fun DrawScope.brazo(p0: Offset, p1: Offset, a: AireFrente, muelle: Boolean) {
        val u = density
        val v = p1 - p0; val l = v.getDistance().coerceAtLeast(1f); val d = v / l; val n = Offset(-d.y, d.x)
        val sombra = Offset(3f * u, 3.6f * u)
        val varilla = if (a.oscuro) a.luz(P.HUESO, 0.3f) else P.HUESO
        for (lado in listOf(-1.6f, 1.6f)) {
            drawLine(a.sombra(0.35f), p0 + n * (lado * u) + sombra, p1 + n * (lado * u) + sombra, 1.8f * u)
        }
        for (lado in listOf(-1.6f, 1.6f)) {
            drawLine(varilla, p0 + n * (lado * u), p1 + n * (lado * u), 1.8f * u, StrokeCap.Round)
        }
        drawLine(if (a.oscuro) a.filo(0.25f) else a.claro(0.7f), p0 - n * (2f * u), p1 - n * (2f * u), 0.5f * u)
        if (muelle) {
            val m = Path()
            val n0 = 0.25f; val n1 = 0.72f
            var t = n0; var lado = 1f
            m.moveTo((p0 + v * t).x, (p0 + v * t).y)
            while (t < n1) {
                t += 0.022f; lado = -lado
                val q = p0 + v * t + n * (lado * 2.2f * u)
                m.lineTo(q.x, q.y)
            }
            drawPath(m, piedra(a, 0.4f, P.CROMO_OSC), style = Stroke(0.9f * u))
        }
    }

    /** Una rótula del flexo: la tuerca de baquelita con su arandela de cromo. */
    private fun DrawScope.rotula(o: Offset, r: Float, a: AireFrente) {
        drawCircle(a.sombra(0.4f), r * 1.1f, o + Offset(r * 0.5f, r * 0.6f))
        drawCircle(piedra(a, 0.5f, P.CROMO), r * 1.25f, o)
        drawCircle(piedra(a, 0.5f, P.BAQUELITA), r, o)
        drawCircle(piedra(a, 0.5f, P.BAQUELITA_LUZ), r * 0.4f, o - Offset(r * 0.3f, r * 0.3f))
    }

    // ---------------------------------------------------------------- el pupitre

    /**
     * El pupitre: la cara de arriba (más clara bajo el charco del flexo; de noche, el verde del osciloscopio a la derecha), la
     * junta con el muro, el labio curvo con su brillo, el esmalte saltado y la línea de sombra, y el frente de consola oscura con
     * sus rejillas y sus tornillos.
     */
    fun DrawScope.pupitre(s: SitioLibretaFrente, a: AireFrente) {
        val u = density
        val w = s.w; val h = s.h
        // Cada franja, con la luz que le llega a lo ancho (un degradado de seis paradas, sin escalones).
        fun tramos(y0: Float, y1: Float, c: Color, f: Float) {
            val paradas = Array(6) { i -> (i / 5f) to en(c, f, Offset(w * i / 5f, (y0 + y1) / 2), s, a) }
            drawRect(Brush.horizontalGradient(*paradas, startX = 0f, endX = w), Offset(0f, y0), Size(w, y1 - y0 + 0.5f))
        }
        // La junta con el muro: la sombra del pupitre en la piedra.
        drawRect(a.sombra(0.35f), Offset(0f, s.mesa - 3f * u), Size(w, 3f * u))
        tramos(s.mesa, s.canto, P.HUESO, 0.04f)
        // La cara de arriba se aclara hacia delante (mira a la luz) y tiene su roce.
        tramos(s.canto - (s.canto - s.mesa) * 0.35f, s.canto, P.HUESO_LUZ, 0.06f)
        drawRect(a.sombra(0.18f), Offset(0f, s.mesa), Size(w, 2f * u))
        for (j in 0 until 6) {
            val x = w * azar(1201, j); val y = s.mesa + (s.canto - s.mesa) * (0.3f + 0.5f * azar(1203, j))
            drawLine(a.sombra(0.1f), Offset(x, y), Offset(x + (14f + 24f * azar(1205, j)) * u, y + 0.4f * u), 0.6f * u)
        }
        if (a.oscuro) {
            // El charco verde del osciloscopio en la cara del pupitre, delante de él.
            val c = Offset(s.pantalla.center.x, s.canto)
            clipRect(0f, s.mesa, w, s.canto) {
                drawOval(Brush.radialGradient(listOf(P.VIDRIO_TRAZO.copy(alpha = 0.2f), P.VIDRIO_TRAZO.copy(alpha = 0f)), c, h * 0.4f), Offset(c.x - h * 0.4f, c.y - h * 0.1f), Size(h * 0.8f, h * 0.2f))
            }
        }
        // El labio curvo, de chapa hueso: el canto de arriba a la luz, la panza que se va a la sombra, el brillo de la curva.
        val al = s.labio - s.canto
        tramos(s.canto, s.canto + al * 0.2f, P.HUESO_LUZ, 0.18f)
        tramos(s.canto + al * 0.2f, s.canto + al * 0.5f, P.HUESO, 0.02f)
        tramos(s.canto + al * 0.5f, s.canto + al * 0.78f, P.HUESO_OSC, -0.08f)
        tramos(s.canto + al * 0.78f, s.labio, P.HUESO_OSC, -0.28f)
        drawRect(Brush.horizontalGradient(listOf(a.brillo(0.7f, L.cerca(s, Offset(s.cart.left, s.canto))), a.brillo(0.15f, L.cerca(s, Offset(w * 0.9f, s.canto)))), 0f, w),
            Offset(0f, s.canto + al * 0.3f), Size(w, 1.2f * u))
        // El esmalte saltado: el acero oscuro, el óxido alrededor y el canto claro del esmalte.
        listOf(0.04f to 0.35f, 0.31f to 0.6f, 0.58f to 0.25f, 0.9f to 0.5f, 0.74f to 0.75f).forEachIndexed { j, (fx, fy) ->
            val x = w * fx; val y = s.canto + al * fy
            val sz = (2.4f + 3.6f * azar(1211, j)) * u
            val p = contornoIrregular(Rect(x - sz / 2, y - sz * 0.3f, x + sz / 2, y + sz * 0.3f), 1300 + j, 7, 0.3f)
            if (j % 2 == 0) drawOval(en(P.OXIDO, 0f, Offset(x, y), s, a).copy(alpha = 0.3f), Offset(x - sz * 0.8f, y - sz * 0.45f), Size(sz * 1.6f, sz * 0.9f))
            drawPath(p, en(P.ACERO, -0.1f, Offset(x, y), s, a))
        }
        drawLine(a.sombra(0.65f), Offset(0f, s.labio + 0.6f * u), Offset(w, s.labio + 0.6f * u), 1.4f * u)
        // El frente de consola oscura, con su sombra bajo el labio, el bisel de arriba, las juntas de los paneles, el tirador de
        // cromo, las rejillas, los tornillos y el roce de los pies.
        tramos(s.labio + 1f * u, h, P.CONSOLA, 0f)
        drawRect(Brush.verticalGradient(listOf(a.sombra(0.5f), Color.Transparent), s.labio, s.labio + (h - s.labio) * 0.45f), Offset(0f, s.labio), Size(w, (h - s.labio) * 0.45f))
        val bisel = s.labio + (h - s.labio) * 0.2f
        tramos(bisel, bisel + 2f * u, P.CONSOLA_LUZ, 0.04f)
        drawRect(a.sombra(0.5f), Offset(0f, bisel + 2f * u), Size(w, 1f * u))
        listOf(s.deck.left - s.dw * 0.06f, s.deck.right + s.dw * 0.06f).forEach { x ->
            drawRect(a.sombra(0.6f), Offset(x, bisel + 3f * u), Size(1.2f * u, h - bisel))
            drawRect(en(P.CONSOLA_LUZ, 0.05f, Offset(x, h), s, a), Offset(x + 1.2f * u, bisel + 3f * u), Size(0.7f * u, h - bisel))
        }
        val ti = Rect(s.cx - s.dw * 0.13f, bisel + (h - bisel) * 0.26f, s.cx + s.dw * 0.13f, bisel + (h - bisel) * 0.26f + 4f * u)
        drawRoundRect(a.sombra(0.45f), ti.topLeft + Offset(1f * u, 2.2f * u), ti.size, CornerRadius(2f * u))
        listOf(ti.left, ti.right - 4f * u).forEach { x -> drawRect(en(P.CROMO_OSC, 0f, ti.center, s, a), Offset(x, ti.top - 1f * u), Size(4f * u, ti.height + 2f * u)) }
        drawRoundRect(en(P.CROMO, 0.05f, ti.center, s, a), ti.topLeft, ti.size, CornerRadius(2f * u))
        drawLine(a.brillo(0.5f, L.cerca(s, ti.center)), Offset(ti.left + 3f * u, ti.top + 1f * u), Offset(ti.right - 3f * u, ti.top + 1f * u), 0.8f * u)
        for (j in 0 until 9) {
            val x = w * azar(1221, j); val y = h - (h - bisel) * (0.06f + 0.3f * azar(1223, j))
            drawLine(en(P.CONSOLA_LUZ, 0.1f, Offset(x, y), s, a), Offset(x, y), Offset(x + (6f + 16f * azar(1225, j)) * u, y - 0.6f * u), 0.7f * u)
        }
        val yr = s.labio + (h - s.labio) * 0.62f
        listOf(s.deck.left + s.dw * 0.06f, s.deck.right - s.dw * 0.34f).forEach { x0 ->
            for (fila in 0 until 2) {
                var x = x0
                while (x < x0 + s.dw * 0.28f) {
                    val k = L.cerca(s, Offset(x, yr))
                    drawRoundRect(Color.Black.copy(alpha = 0.7f), Offset(x, yr + fila * 5f * u - 1f * u), Size(5f * u, 2f * u), CornerRadius(1f * u))
                    drawLine(a.brillo(0.14f, k), Offset(x + 0.5f * u, yr + fila * 5f * u + 1.4f * u), Offset(x + 4.5f * u, yr + fila * 5f * u + 1.4f * u), 0.6f * u)
                    x += 7f * u
                }
            }
        }
        listOf(w * 0.04f, w * 0.96f).forEachIndexed { i, x -> tornilloFrente(Offset(x, yr + 2f * u), 1.6f * u, a, L.cerca(s, Offset(x, yr)), 40f + 50f * i) }
    }

    // ---------------------------------------------------------------- el osciloscopio

    private var claveOnda = ""
    private var onda = Path()

    /** La onda de lo que se graba, en [r]: irregular, como la de una voz (ni un latido ni una letra). Se arma una vez por tamaño. */
    private fun ondaEn(r: Rect): Path {
        val clave = "${r.left}:${r.top}:${r.width}:${r.height}"
        if (clave == claveOnda) return onda
        val p = Path()
        val n = 140
        for (i in 0..n) {
            val t = i / n.toFloat()
            val env = 0.3f + 0.7f * (0.5f + 0.5f * sin(t * 6.3f + 0.6f)) * (0.75f + 0.25f * sin(t * 17f + 2f))
            val y = r.center.y - r.height * 0.38f * env * (0.7f * sin(t * 38f) + 0.3f * sin(t * 71f + 1.3f))
            val x = r.left + r.width * t
            if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
        }
        onda = p; claveOnda = clave
        return p
    }

    /**
     * El osciloscopio, sobre el pupitre a la derecha: la caja de consola oscura con su cara de arriba y su asa de cromo, la
     * pantalla de fósforo verde con su retícula y la onda, las dos perillas, el piloto y la rejilla. De noche, su verde frío.
     */
    fun DrawScope.osciloscopio(s: SitioLibretaFrente, a: AireFrente) {
        val u = density
        val h = s.h
        val r = s.scope
        if (r.left > s.w) return
        contactoFrente(Offset(r.center.x, r.bottom), r.width * 1.1f, 7f * u, a, 0.42f)
        val arriba = Rect(r.left, r.top, r.right, r.top + h * 0.03f)
        drawRoundRect(en(P.CONSOLA, 0f, r.center, s, a), r.topLeft, r.size, CornerRadius(4f * u))
        drawRoundRect(en(P.CONSOLA_LUZ, 0.05f, arriba.center, s, a), arriba.topLeft, arriba.size, CornerRadius(4f * u))
        drawRect(a.sombra(0.4f), Offset(r.left, arriba.bottom), Size(r.width, 1.2f * u))
        drawRect(a.sombra(0.3f), Offset(r.left, r.bottom - h * 0.03f), Size(r.width, h * 0.03f))
        if (a.oscuro) drawRect(a.filoVidrio(0.5f), Offset(r.left, r.top + 2f * u), Size(1.2f * u, r.height - 4f * u))
        else drawRect(a.claro(0.3f), Offset(r.left, r.top + 2f * u), Size(1.2f * u, r.height - 4f * u))
        // El asa de cromo, arriba.
        val ay = r.top - h * 0.028f
        listOf(r.left + h * 0.06f, r.left + h * 0.3f).forEach { x -> drawRect(en(P.CROMO_OSC, 0f, Offset(x, ay), s, a), Offset(x - 1.2f * u, ay), Size(2.4f * u, h * 0.03f)) }
        drawLine(a.sombra(0.35f), Offset(r.left + h * 0.06f + 2f * u, ay + 2.5f * u), Offset(r.left + h * 0.3f + 2f * u, ay + 2.5f * u), 2.4f * u, StrokeCap.Round)
        drawLine(en(P.CROMO, 0.05f, Offset(r.left + h * 0.18f, ay), s, a), Offset(r.left + h * 0.06f, ay), Offset(r.left + h * 0.3f, ay), 2.4f * u, StrokeCap.Round)
        // La pantalla: el bisel de baquelita, el fósforo, la retícula y la onda.
        val pa = s.pantalla
        val bisel = pa.inflate(h * 0.014f)
        drawRoundRect(en(P.BAQUELITA, 0f, pa.center, s, a), bisel.topLeft, bisel.size, CornerRadius(4f * u))
        drawRoundRect(a.claro(0.25f), bisel.topLeft, Size(bisel.width, 1f * u), CornerRadius(4f * u))
        val fosforo = if (a.oscuro) mezcla(P.RADAR, P.PANTALLA, 0.55f) else mezcla(P.RADAR, P.PANTALLA, 0.78f)
        drawRoundRect(Brush.radialGradient(listOf(fosforo, P.PANTALLA), pa.center, pa.width * 0.7f), pa.topLeft, pa.size, CornerRadius(3f * u))
        val reticula = P.RADAR.copy(alpha = if (a.oscuro) 0.28f else 0.2f)
        for (i in 1 until 6) drawLine(reticula, Offset(pa.left + pa.width * i / 6f, pa.top + 1f * u), Offset(pa.left + pa.width * i / 6f, pa.bottom - 1f * u), 0.5f * u)
        for (i in 1 until 4) drawLine(reticula, Offset(pa.left + 1f * u, pa.top + pa.height * i / 4f), Offset(pa.right - 1f * u, pa.top + pa.height * i / 4f), 0.5f * u)
        val o = ondaEn(pa.deflate(2f * u))
        drawPath(o, P.RADAR.copy(alpha = if (a.oscuro) 0.3f else 0.18f), style = Stroke(3.6f * u))
        drawPath(o, mezcla(P.RADAR, Color.White, if (a.oscuro) 0.35f else 0.15f), style = Stroke(1.1f * u))
        drawLine(Color.White.copy(alpha = 0.18f), Offset(pa.left + pa.width * 0.1f, pa.top + 2f * u), Offset(pa.left + pa.width * 0.45f, pa.top + 2f * u), 1f * u, StrokeCap.Round)
        // Las perillas, el piloto y la rejilla.
        val yk = pa.bottom + h * 0.075f
        listOf(pa.left + pa.width * 0.2f, pa.left + pa.width * 0.62f).forEachIndexed { i, x -> perilla(Offset(x, yk), h * 0.03f, 40f + 70f * i, s, a) }
        pilotoFrente(Offset(pa.right + h * 0.05f, pa.top + h * 0.02f), 2f * u, P.AMBAR, true, a, L.cerca(s, pa.center))
        var x = pa.right + h * 0.03f
        while (x < minOf(r.right - h * 0.03f, s.w)) {
            drawRoundRect(Color.Black.copy(alpha = 0.7f), Offset(x, pa.top + h * 0.06f), Size(2f * u, h * 0.16f), CornerRadius(1f * u))
            x += 5f * u
        }
        tornilloFrente(Offset(r.left + 4f * u, r.bottom - 5f * u), 1.4f * u, a, L.cerca(s, r.center))
        // Abajo, las dos palancas y la toma de la sonda, con el cable que va a la grabadora por el pupitre.
        val yb = r.bottom - h * 0.045f
        listOf(pa.left + pa.width * 0.41f, pa.left + pa.width * 0.86f).forEach { x ->
            drawRoundRect(en(P.CROMO_OSC, 0f, Offset(x, yb), s, a), Offset(x - 2.6f * u, yb - 2.6f * u), Size(5.2f * u, 5.2f * u), CornerRadius(1f * u))
            drawLine(a.sombra(0.4f), Offset(x + 0.8f * u, yb), Offset(x + 1.6f * u, yb - 6f * u), 1.6f * u, StrokeCap.Round)
            drawLine(en(P.CROMO, 0.1f, Offset(x, yb), s, a), Offset(x, yb), Offset(x + 0.6f * u, yb - 6.4f * u), 1.4f * u, StrokeCap.Round)
        }
        val toma = Offset(pa.left + pa.width * 0.18f, yb)
        drawCircle(en(P.CROMO_OSC, 0f, toma, s, a), 3.4f * u, toma)
        drawCircle(en(P.CROMO, 0.1f, toma, s, a), 2.4f * u, toma)
        val cable = Path().apply {
            moveTo(toma.x, toma.y)
            cubicTo(toma.x - h * 0.02f, toma.y + h * 0.06f, s.deck.right + h * 0.03f, s.canto - h * 0.005f, s.deck.right - 3f * u, s.deck.bottom - h * 0.06f)
        }
        with(TrazosFrente) { cableGrueso(cable, 2.2f * u, en(P.BAQUELITA, 0f, toma, s, a), if (a.oscuro) 0.3f else 1f, if (a.oscuro) 0.4f else 0.25f) }
        drawCircle(en(P.CROMO, 0.05f, toma, s, a), 1.6f * u, toma)
    }

    /** Una perilla de baquelita con su falda de cromo estriada y la raya que marca, girada [giro] grados. */
    private fun DrawScope.perilla(c: Offset, r: Float, giro: Float, s: SitioLibretaFrente, a: AireFrente) {
        val u = density
        drawCircle(a.sombra(0.4f), r * 1.25f, c + Offset(r * 0.25f, r * 0.35f))
        drawCircle(en(P.CROMO_OSC, 0f, c, s, a), r * 1.25f, c)
        drawCircle(en(P.CROMO, 0.05f, c, s, a), r * 1.1f, c - Offset(r * 0.08f, r * 0.08f))
        drawCircle(en(P.BAQUELITA, 0f, c, s, a), r * 0.82f, c)
        drawCircle(en(P.BAQUELITA_LUZ, 0f, c, s, a), r * 0.42f, c - Offset(r * 0.22f, r * 0.22f))
        rotate(giro, c) { drawLine(en(P.HUESO, 0f, c, s, a), c, c - Offset(0f, r * 0.72f), 1.1f * u, StrokeCap.Round) }
    }

    // ---------------------------------------------------------------- la grabadora

    /**
     * La grabadora de cartuchos: la cara de arriba con la ranura y su bisel de cromo; el frente con las teclas de piano color hueso
     * (la roja de grabar, hundida), la perilla, el piloto ámbar y el rojo de grabar encendidos, la rejilla del altavoz, los
     * tornillos y los tacos de goma.
     */
    fun DrawScope.grabadora(s: SitioLibretaFrente, a: AireFrente) {
        val u = density
        val h = s.h
        val d = s.deck
        contactoFrente(Offset(d.center.x, d.bottom), d.width * 1.06f, 9f * u, a, 0.5f)
        // Los tacos de goma.
        listOf(d.left + d.width * 0.08f, d.right - d.width * 0.08f).forEach { x ->
            drawRoundRect(en(P.BAQUELITA, -0.1f, Offset(x, d.bottom), s, a), Offset(x - 7f * u, d.bottom - 2f * u), Size(14f * u, 3.6f * u), CornerRadius(1.2f * u))
        }
        val n = 8
        // La cara de arriba, que mira a la luz.
        fun luzAncha(c: Color, f: Float, y: Float) = Brush.horizontalGradient(*Array(n) { i -> (i / (n - 1f)) to en(c, f, Offset(d.left + d.width * i / (n - 1f), y), s, a) }, startX = d.left, endX = d.right)
        drawRoundRect(luzAncha(P.CONSOLA_LUZ, 0.06f, d.top), d.topLeft, Size(d.width, s.tapa - d.top + 4f * u), CornerRadius(5f * u))
        drawLine(if (a.oscuro) a.filo(0.3f) else a.claro(0.5f), Offset(d.left + 5f * u, d.top + 0.8f * u), Offset(d.right - 5f * u, d.top + 0.8f * u), 0.8f * u)
        // La ranura: el hondo y su bisel de cromo (el canto de delante lo pone [delanteDeck], sobre el cartucho).
        val ra = Rect(s.cart.left - 3.5f * u, d.top + h * 0.008f, s.cart.right + 3.5f * u, s.cart.bottom)
        drawRoundRect(en(P.CROMO_OSC, 0f, ra.center, s, a), ra.inflate(1.6f * u).topLeft, ra.inflate(1.6f * u).size, CornerRadius(2.5f * u))
        drawRoundRect(Color(0xFF050907), ra.topLeft, ra.size, CornerRadius(2f * u))
        // El frente, con la luz del flexo de la izquierda.
        val fr = Rect(d.left, s.tapa, d.right, d.bottom - 1f * u)
        drawRoundRect(luzAncha(P.CONSOLA, 0f, fr.center.y), fr.topLeft, fr.size, CornerRadius(5f * u))
        // El roce de la pintura: rayas claras finas y una esquina gastada.
        for (j in 0 until 6) {
            val x = fr.left + fr.width * azar(1231, j); val y = fr.top + fr.height * (0.15f + 0.7f * azar(1233, j))
            drawLine(en(P.CONSOLA_LUZ, 0.12f, Offset(x, y), s, a), Offset(x, y), Offset(x + (4f + 10f * azar(1235, j)) * u, y + (azar(1237, j) - 0.5f) * 2f * u), 0.6f * u)
        }
        drawOval(en(P.ACERO, 0f, Offset(fr.left, fr.bottom), s, a), Offset(fr.left + 1f * u, fr.bottom - 9f * u), Size(7f * u, 5f * u))
        drawRect(a.sombra(0.35f), Offset(fr.left + 3f * u, fr.bottom - fr.height * 0.18f), Size(fr.width - 6f * u, fr.height * 0.18f - 2f * u))
        drawRect(en(P.CROMO, 0.05f, Offset(d.left, fr.top), s, a), Offset(fr.left + 2f * u, fr.top), Size(fr.width - 4f * u, 1.6f * u))
        // Las teclas de piano: el pozo y cada tecla con su sombra y su luz; la roja de grabar, hundida.
        val kw = s.dw * 0.072f; val gap = s.dw * 0.012f
        val x0 = d.left + s.dw * 0.06f
        val ky0 = fr.top + h * 0.035f; val ky1 = fr.top + h * 0.125f
        drawRoundRect(Color(0xFF07100C), Offset(x0 - 2f * u, ky0 - 2f * u), Size(5 * kw + 4 * gap + 4f * u, ky1 - ky0 + 4f * u), CornerRadius(2f * u))
        for (i in 0 until 5) {
            val x = x0 + i * (kw + gap)
            val hundida = i == 1
            val top = ky0 + if (hundida) 3f * u else 0f
            val c = if (hundida) P.BOTON else P.HUESO
            val k = Offset(x + kw / 2, top)
            drawRect(en(mezcla(c, P.SOMBRA, 0.35f), 0f, k, s, a), Offset(x, top), Size(kw, ky1 - top))
            drawRect(en(c, 0.04f, k, s, a), Offset(x, top), Size(kw, (ky1 - top) * 0.78f))
            drawRect(en(c, 0.16f, k, s, a), Offset(x, top), Size(kw, (ky1 - top) * 0.26f))
            drawRect(a.sombra(0.18f), Offset(x, top + (ky1 - top) * 0.26f), Size(kw, 0.8f * u))
            drawRect(if (a.oscuro) a.filo(0.4f * L.cerca(s, k)) else a.claro(0.55f), Offset(x + 1f * u, top + 0.6f * u), Size(kw - 2f * u, 1f * u))
        }
        // La perilla del volumen, los pilotos (encendido, ámbar; grabando, rojo) y la rejilla del altavoz.
        val xp = x0 + 5 * kw + 4 * gap + s.dw * 0.07f
        perilla(Offset(xp, (ky0 + ky1) / 2), h * 0.032f, -30f, s, a)
        pilotoFrente(Offset(xp + s.dw * 0.075f, ky0 + h * 0.012f), 2.1f * u, P.AMBAR, true, a, L.cerca(s, Offset(xp, ky0)))
        pilotoFrente(Offset(xp + s.dw * 0.075f, ky1 - h * 0.012f), 2.1f * u, P.ROJO, true, a, L.cerca(s, Offset(xp, ky1)))
        val gr = Rect(d.right - s.dw * 0.33f, fr.top + h * 0.03f, d.right - s.dw * 0.05f, fr.bottom - h * 0.045f)
        drawRoundRect(en(P.CROMO_OSC, 0f, gr.center, s, a), gr.inflate(1.4f * u).topLeft, gr.inflate(1.4f * u).size, CornerRadius(2.5f * u))
        drawRoundRect(en(P.CONSOLA_OSC, 0f, gr.center, s, a), gr.topLeft, gr.size, CornerRadius(2f * u))
        var y = gr.top + 3f * u
        while (y < gr.bottom - 2f * u) {
            drawLine(Color.Black.copy(alpha = 0.75f), Offset(gr.left + 3f * u, y), Offset(gr.right - 3f * u, y), 1.6f * u, StrokeCap.Round)
            drawLine(a.brillo(0.12f, L.cerca(s, gr.center)), Offset(gr.left + 3f * u, y + 1.2f * u), Offset(gr.right - 3f * u, y + 1.2f * u), 0.5f * u)
            y += 4f * u
        }
        // Los tornillos de las esquinas y los filos de la noche: tibio a la izquierda, frío a la derecha.
        listOf(Offset(fr.left + 5f * u, fr.top + 5f * u), Offset(fr.right - 5f * u, fr.top + 5f * u), Offset(fr.left + 5f * u, fr.bottom - 6f * u), Offset(fr.right - 5f * u, fr.bottom - 6f * u))
            .forEachIndexed { i, o -> tornilloFrente(o, 1.4f * u, a, L.cerca(s, o), 20f + 40f * i, L.frio(s, o, a.oscuro)) }
        if (a.oscuro) {
            drawRect(a.filo(0.55f * L.cerca(s, Offset(d.left, fr.center.y))), Offset(d.left, d.top + 4f * u), Size(1.4f * u, d.height - 8f * u))
            drawRect(a.filoVidrio(0.9f * L.frio(s, Offset(d.right, fr.center.y), true)), Offset(d.right - 1.4f * u, d.top + 4f * u), Size(1.4f * u, d.height - 8f * u))
        }
    }

    /**
     * Lo de rotular, en el pupitre delante de la grabadora: el rollo de cinta de papel de las etiquetas, acostado, con su cabo
     * colgando por el canto, y el lápiz graso rojo, con su papel a medio pelar.
     */
    fun DrawScope.utiles(s: SitioLibretaFrente, a: AireFrente) {
        val u = density
        val h = s.h
        val c = Offset(s.deck.left + h * 0.03f, s.canto - h * 0.022f)
        val rx = h * 0.062f; val ry = h * 0.018f; val alto = h * 0.034f
        contactoFrente(Offset(c.x + h * 0.01f, c.y + alto * 0.5f), rx * 2.4f, ry * 2.2f, a, 0.45f)
        // El canto del rollo (la cinta enrollada, con sus vueltas) y la cara de arriba con el cartón del centro.
        val k = L.cerca(s, c); val v = L.frio(s, c, a.oscuro)
        drawRect(a.etiqueta(P.CINTA_OSC, k, v), Offset(c.x - rx, c.y), Size(rx * 2, alto))
        drawOval(a.etiqueta(P.CINTA_OSC, k, v), Offset(c.x - rx, c.y + alto - ry), Size(rx * 2, ry * 2))
        for (j in 1 until 4) drawLine(a.sombra(0.14f), Offset(c.x - rx + 1f * u, c.y + alto * j / 4f), Offset(c.x + rx - 1f * u, c.y + alto * j / 4f), 0.5f * u)
        drawRect(a.claro(0.3f), Offset(c.x - rx + 1.5f * u, c.y + 1f * u), Size(2f * u, alto - 2f * u))
        drawOval(a.etiqueta(P.CINTA, k, v), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
        drawOval(a.etiqueta(P.CARTON, k, v), Offset(c.x - rx * 0.58f, c.y - ry * 0.58f), Size(rx * 1.16f, ry * 1.16f))
        drawOval(a.sombra(0.7f), Offset(c.x - rx * 0.44f, c.y - ry * 0.44f), Size(rx * 0.88f, ry * 0.88f))
        // El cabo de la cinta, que cuelga por el canto del pupitre con su corte en dientes.
        val x0 = c.x + rx * 0.55f
        val y1 = s.canto + (s.labio - s.canto) * 0.9f
        drawRect(a.sombra(0.3f), Offset(x0 + 1f * u, c.y + alto), Size(h * 0.03f, y1 - c.y - alto + 1.5f * u))
        drawRect(a.etiqueta(P.CINTA, L.cerca(s, Offset(x0, s.canto)), L.frio(s, Offset(x0, s.canto), a.oscuro)), Offset(x0, c.y + alto - 1f * u), Size(h * 0.03f, y1 - c.y - alto + 1f * u))
        val dientes = Path().apply {
            moveTo(x0, y1)
            for (j in 0..4) lineTo(x0 + h * 0.03f * j / 4f, y1 + (if (j % 2 == 0) 0f else 1.6f * u))
            lineTo(x0 + h * 0.03f, y1 - 1f * u); lineTo(x0, y1 - 1f * u); close()
        }
        drawPath(dientes, a.etiqueta(P.CINTA, L.cerca(s, Offset(x0, y1)), 0f))
        // El lápiz graso, acostado delante, con la punta negra y el papel a medio pelar.
        val p0 = Offset(c.x + rx + h * 0.025f, s.canto - h * 0.012f)
        val p1 = p0 + Offset(h * 0.13f, -h * 0.012f)
        drawLine(a.sombra(0.4f), p0 + Offset(1.5f * u, 2.2f * u), p1 + Offset(1.5f * u, 2.2f * u), 3.6f * u, StrokeCap.Round)
        drawLine(en(P.BOTON, 0f, p0, s, a), p0, p1, 3.4f * u, StrokeCap.Butt)
        drawLine(a.etiqueta(P.CINTA, L.cerca(s, p0), 0f), p0 + (p1 - p0) * 0.62f, p1, 3.4f * u, StrokeCap.Butt)
        for (j in 0 until 4) {
            val q = p0 + (p1 - p0) * (0.66f + 0.08f * j)
            drawLine(a.sombra(0.3f), q + Offset(-1f * u, -1.7f * u), q + Offset(1.4f * u, 1.7f * u), 0.5f * u)
        }
        drawLine(en(P.BAQUELITA, 0f, p0, s, a), p0 - (p1 - p0) * 0.1f, p0, 2.4f * u, StrokeCap.Round)
        drawLine(if (a.oscuro) a.filo(0.4f * L.cerca(s, p0)) else a.claro(0.6f), p0 + Offset(0f, -1.1f * u), p0 + (p1 - p0) * 0.6f + Offset(0f, -1.1f * u), 0.6f * u)
    }

    /** El canto de delante de la ranura (su bisel de cromo y la cara de arriba que queda delante), sobre el pie del cartucho. */
    fun DrawScope.delanteDeck(s: SitioLibretaFrente, a: AireFrente) {
        val u = density
        val y0 = s.cart.bottom - 2.5f * u
        val ra = Rect(s.cart.left - 5f * u, y0, s.cart.right + 5f * u, s.tapa)
        drawRect(en(P.CONSOLA_LUZ, 0.06f, ra.center, s, a), ra.topLeft, ra.size)
        drawRect(en(P.CROMO, 0.1f, Offset(s.cart.left, y0), s, a), Offset(ra.left, y0), Size(ra.width, 1.6f * u))
        drawRect(a.sombra(0.4f), Offset(ra.left, y0 + 1.6f * u), Size(ra.width, 0.8f * u))
        drawRect(en(P.CROMO, 0.05f, Offset(s.deck.left, s.tapa), s, a), Offset(s.deck.left + 2f * u, s.tapa), Size(s.deck.width - 4f * u, 1.6f * u))
    }
}
