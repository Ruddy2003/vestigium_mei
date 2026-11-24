package io.github.ruddy2003.vestigium.ui.biblioteca

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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.cos
import kotlin.math.sin

/**
 * La pared, la ventana y el suelo del estante de Horror cósmico ([MuebleEstanteArcano]). La pared es papel pintado de escamas
 * verde mar con su filete de oro viejo, despegado en las juntas, con las manchas de humedad de la gotera que baja desde la moldura
 * del techo. La ventana de guillotina, arriba a la izquierda, honda, con su persiana de tela enrollada y los vidrios empañados: de
 * día, la niebla; de noche, la niebla verde honda con el haz del faro; en los Sueños, el ocaso o la luna. El suelo es de baldosa
 * hidráulica de damero, hueso y verde pizarra. Lo grande se pinta con la luz entera y la noche va encima en un mapa de luz.
 */
internal object MuroEstanteArcano {
    private val P = PaletaArcano
    private val M = MuebleEstanteArcano

    /** Dónde acaba la moldura del techo, en anchos. */
    private const val TECHO = 0.038f

    // ---------------------------------------------------------------- la pared

    /** El papel de escamas, las juntas de los paños, la humedad y la moldura del techo. */
    fun DrawScope.pared(g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.a
        val alto = g.apoyo + 2f
        val techo = w * TECHO
        drawRect(a.pleno(P.MAR), Offset(0f, 0f), Size(w, alto))
        // La luz de la ventana en el papel: más clara junto al hueco, apagándose hacia la derecha (de día y al ocaso).
        if (!a.oscuro) {
            val cv = Offset(w * (M.VEN_X0 + M.VEN_X1) / 2, w * (M.VEN_Y0 + M.VEN_Y1) / 2)
            val luz = if (a.noche) mezcla(P.MAR_LUZ, P.OCASO, 0.45f) else mezcla(P.MAR_LUZ, P.NIEBLA, 0.25f)
            drawRect(Brush.radialGradient(listOf(luz, luz.copy(alpha = 0.55f), luz.copy(alpha = 0f)), cv, w * 0.55f), Offset.Zero, Size(w, alto))
        }
        pinceladas(Rect(0f, techo, w, alto), a.pleno(P.MAR_OSC), 7101, 40, u, 0.1f)
        escamas(g, techo, alto)
        juntas(g, techo, alto)
        humedad(g, techo, alto)
        techo(g, techo)
    }

    /**
     * Las escamas del papel: filas de medios círculos que se montan, cada uno con su trazo hondo abajo y su labio claro dentro, y
     * un punto de oro viejo en cada encuentro de una fila de cada dos (el dibujo del papel, gastado y bajo).
     */
    private fun DrawScope.escamas(g: MarcoArcano, y0: Float, y1: Float) {
        val w = g.w; val u = density
        val a = g.a
        val r = w * 0.03f
        val hondos = Path(); val labios = Path(); val puntos = Path()
        var fila = 0
        var y = y0 + r * 0.4f
        while (y < y1 + r) {
            var x = -r + (if (fila % 2 == 0) 0f else r)
            while (x < w + r) {
                hondos.addArc(Rect(x - r, y - r, x + r, y + r), 10f, 160f)
                labios.addArc(Rect(x - r * 0.78f, y - r * 0.86f, x + r * 0.78f, y + r * 0.7f), 30f, 120f)
                if (fila % 2 == 0) puntos.addOval(Rect(x - 1.3f * u, y + r - 1.3f * u, x + 1.3f * u, y + r + 1.3f * u))
                x += 2 * r
            }
            y += r * 0.92f; fila++
        }
        clipRect(0f, y0, w, y1) {
            drawPath(hondos, a.pleno(P.ESCAMA).copy(alpha = 0.55f), style = Stroke(1.4f * u))
            drawPath(labios, a.pleno(P.MAR_LUZ).copy(alpha = 0.26f), style = Stroke(1f * u))
            drawPath(puntos, a.pleno(P.ORO).copy(alpha = 0.42f))
        }
    }

    /** Las juntas de los paños de papel, con el borde levantado y, en una, la esquina que se despega y deja ver el yeso. */
    private fun DrawScope.juntas(g: MarcoArcano, y0: Float, y1: Float) {
        val w = g.w; val u = density
        val a = g.a
        listOf(0.31f, 0.63f, 0.93f).forEachIndexed { i, fx ->
            val x = w * fx
            drawLine(a.sombra(0.3f), Offset(x + 1.4f * u, y0), Offset(x + 1.4f * u, y1), 1.4f * u)
            drawLine(a.pleno(P.MAR_LUZ).copy(alpha = 0.5f), Offset(x, y0), Offset(x, y1), 0.9f * u)
            // La humedad sube por la junta: el borde se oscurece y se abre.
            drawLine(a.pleno(P.SALOBRE).copy(alpha = 0.22f), Offset(x + 2f * u, y0 + (y1 - y0) * (0.3f + 0.2f * azar(7110, i))), Offset(x + 2f * u, y1), 4f * u)
        }
        // La junta del medio, levantada por la humedad: el borde del paño se separa (una tira de sombra y su canto claro) y hace
        // ampollas.
        val x = w * 0.63f
        val alza = Path().apply {
            moveTo(x, y0); lineTo(x + w * 0.012f, y0); cubicTo(x + w * 0.016f, y0 + w * 0.04f, x + w * 0.008f, y0 + w * 0.08f, x + w * 0.003f, y0 + w * 0.12f); lineTo(x, y0 + w * 0.12f); close()
        }
        drawPath(Path().apply { addPath(alza, Offset(w * 0.006f, w * 0.003f)) }, a.sombra(0.3f))
        drawPath(alza, a.pleno(P.MAR, 0.06f))
        drawPath(Path().apply { moveTo(x + w * 0.012f, y0); cubicTo(x + w * 0.016f, y0 + w * 0.04f, x + w * 0.008f, y0 + w * 0.08f, x + w * 0.003f, y0 + w * 0.12f) },
            a.pleno(P.MAR_LUZ, 0.1f), style = Stroke(1.1f * u))
        listOf(Triple(0.2f, 0.33f, 0.016f), Triple(0.72f, 0.16f, 0.012f)).forEach { (fx, fy, fr) ->
            val c = Offset(w * fx, w * fy)
            drawOval(a.sombra(0.22f), Offset(c.x - w * fr + 1.5f * u, c.y - w * fr * 0.55f + 2f * u), Size(w * fr * 2, w * fr * 1.1f))
            drawOval(a.pleno(P.MAR_LUZ).copy(alpha = 0.5f), Offset(c.x - w * fr, c.y - w * fr * 0.55f), Size(w * fr * 1.2f, w * fr * 0.7f))
        }
    }

    /**
     * La humedad: la gotera que baja desde la moldura del techo con su cerco de salobre, las flores de moho de la esquina de
     * arriba a la derecha y la mancha grande de detrás de la lente, con su cerco.
     */
    private fun DrawScope.humedad(g: MarcoArcano, y0: Float, y1: Float) {
        val w = g.w; val u = density
        val a = g.a
        // La gotera: la mancha que sale de la moldura, con sus cercos de cuando se secó, y dos hilos que bajan.
        val gx = w * 0.56f
        val c0 = Offset(gx, y0 + w * 0.012f)
        listOf(1f, 0.6f).forEachIndexed { i, f ->
            val m = irregularArcano(c0, w * 0.05f * f, w * 0.026f * f, 7125 + i, 13, 0.3f)
            drawPath(m, a.pleno(P.SALOBRE).copy(alpha = 0.14f))
            drawPath(m, a.pleno(mezcla(P.SALOBRE, P.TECA_OSC, 0.35f)).copy(alpha = 0.38f), style = Stroke(1.1f * u))
        }
        listOf(Triple(-0.012f, 0.11f, 2f), Triple(0.014f, 0.07f, 1.6f)).forEach { (dx, largo, grueso) ->
            val x0 = gx + w * dx
            val p = Path().apply { moveTo(x0, y0 + w * 0.03f); cubicTo(x0 + w * 0.003f, y0 + w * (0.03f + largo * 0.4f), x0 - w * 0.002f, y0 + w * (0.03f + largo * 0.7f), x0 + w * 0.001f, y0 + w * (0.03f + largo)) }
            drawPath(p, a.pleno(P.SALOBRE).copy(alpha = 0.26f), style = Stroke(grueso * 1.6f * u, cap = StrokeCap.Round))
        }
        // Las flores de moho, arriba a la derecha y a la izquierda de la ventana: nubes de puntos oscuros con su cerco claro.
        listOf(Triple(0.86f, 0.09f, 0.06f), Triple(0.26f, 0.42f, 0.04f), Triple(0.4f, 0.07f, 0.035f)).forEachIndexed { i, (fx, fy, fr) ->
            val c = Offset(w * fx, w * fy)
            drawPath(irregularArcano(c, w * fr, w * fr * 0.7f, 7140 + i, 11, 0.35f), a.pleno(P.SALOBRE).copy(alpha = 0.18f))
            drawPath(irregularArcano(c, w * fr, w * fr * 0.7f, 7140 + i, 11, 0.35f), a.pleno(mezcla(P.SALOBRE, P.YESO, 0.3f)).copy(alpha = 0.3f), style = Stroke(1f * u))
            val moho = Path()
            for (j in 0 until 22) {
                val ang = azar(7150 + i, j) * 6.283f; val d = azar(7160 + i, j) * w * fr * 0.8f
                val p = c + Offset(cos(ang) * d, sin(ang) * d * 0.7f)
                val rr = (0.6f + 1.2f * azar(7170 + i, j)) * u
                moho.addOval(Rect(p.x - rr, p.y - rr, p.x + rr, p.y + rr))
            }
            drawPath(moho, a.pleno(Color(0xFF1E2A22)).copy(alpha = 0.45f))
        }
    }

    /** La moldura de yeso del techo: tres listeles con su luz y su sombra, y el cerco de la gotera. */
    private fun DrawScope.techo(g: MarcoArcano, alto: Float) {
        val w = g.w; val u = density
        val a = g.a
        drawRect(a.pleno(P.YESO, -0.08f), Offset.Zero, Size(w, alto))
        drawRect(a.pleno(P.YESO, 0.08f), Offset(0f, alto * 0.18f), Size(w, alto * 0.22f))
        drawRect(a.pleno(P.YESO_OSC), Offset(0f, alto * 0.4f), Size(w, alto * 0.12f))
        drawRect(a.pleno(P.YESO), Offset(0f, alto * 0.52f), Size(w, alto * 0.3f))
        drawRect(a.sombra(0.35f), Offset(0f, alto * 0.82f), Size(w, alto * 0.18f))
        drawRect(a.sombra(0.4f), Offset(0f, alto), Size(w, 2.2f * u))
        pinceladas(Rect(0f, 0f, w, alto), a.pleno(P.YESO_OSC), 7180, 12, u, 0.14f)
        // La humedad de la gotera en la moldura: el listel de abajo, manchado.
        drawRect(a.pleno(P.SALOBRE).copy(alpha = 0.3f), Offset(w * 0.52f, alto * 0.52f), Size(w * 0.08f, alto * 0.48f))
    }

    // ---------------------------------------------------------------- la ventana

    /**
     * El hueco de la ventana, lo que va debajo de la noche: el marco de madera pintada de crema que se pela, la jamba de la derecha
     * y el dintel (el derrame, que se ve por estar alta), el alféizar con su canto y la persiana de tela enrollada arriba.
     */
    fun DrawScope.ventanaHueco(g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.a
        val x0 = w * M.VEN_X0; val x1 = w * M.VEN_X1; val y0 = w * M.VEN_Y0; val y1 = w * M.VEN_Y1
        val m = w * 0.014f
        val hondo = w * 0.026f
        // La sombra del marco en la pared, a la derecha y abajo.
        drawRect(a.sombra(0.28f), Offset(x1 + m, y0 - m + 3f * u), Size(w * 0.012f, y1 - y0 + 2 * m))
        // El marco: madera pintada de crema, pelada hasta la teca en los cantos.
        val crema = mezcla(P.YESO, P.PAPEL, 0.5f)
        drawRect(a.pleno(crema, -0.05f), Offset(x0 - m, y0 - m), Size(x1 - x0 + 2 * m, y1 - y0 + 2 * m))
        drawRect(a.pleno(crema, 0.1f), Offset(x0 - m, y0 - m), Size(x1 - x0 + 2 * m, 1.4f * u))
        drawRect(a.pleno(crema, 0.1f), Offset(x0 - m, y0 - m), Size(1.4f * u, y1 - y0 + 2 * m))
        drawRect(a.sombra(0.25f), Offset(x0 - m * 0.45f, y0 - m * 0.45f), Size(x1 - x0 + m * 0.9f, y1 - y0 + m * 0.9f), style = Stroke(0.9f * u))
        for (j in 0 until 6) {
            val fx = x0 - m + (x1 - x0 + 2 * m) * azar(7200, j)
            val enArriba = j % 2 == 0
            val p = if (enArriba) Offset(fx, y0 - m * (0.2f + 0.6f * azar(7201, j))) else Offset(x1 + m * (0.2f + 0.6f * azar(7201, j)), y0 + (y1 - y0) * azar(7202, j))
            drawPath(irregularArcano(p, w * (0.006f + 0.008f * azar(7203, j)), w * 0.004f, 7204 + j, 7, 0.5f), a.pleno(P.TECA_GRIS))
        }
        // El dintel (su cara de abajo, en escorzo) y la jamba de la derecha, en sombra.
        drawPath(poligonoArcano(x0, y0, x1, y0, x1 - hondo * 0.6f, y0 + hondo * 0.55f, x0, y0 + hondo * 0.55f), a.pleno(mezcla(crema, P.MAR_OSC, 0.35f), -0.1f))
        drawPath(poligonoArcano(x1, y0, x1, y1, x1 - hondo * 0.6f, y1 - hondo * 0.1f, x1 - hondo * 0.6f, y0 + hondo * 0.55f), a.pleno(mezcla(crema, P.MAR_OSC, 0.2f)))
        drawLine(a.sombra(0.4f), Offset(x1 - hondo * 0.6f, y0 + hondo * 0.55f), Offset(x1 - hondo * 0.6f, y1 - hondo * 0.1f), 1f * u)
        // El alféizar: la tabla que vuela, su canto y su sombra.
        val al = Rect(x0 - m * 1.6f, y1 + m * 0.3f, x1 + m * 1.6f, y1 + m * 1.25f)
        drawRect(a.sombra(0.35f), Offset(al.left + 2f * u, al.bottom), Size(al.width, 3f * u))
        drawRect(a.pleno(crema, 0.02f), al.topLeft, al.size)
        drawRect(a.pleno(crema, 0.15f), al.topLeft, Size(al.width, 1.4f * u))
        drawRect(a.sombra(0.3f), Offset(al.left, al.bottom - 1.6f * u), Size(al.width, 1.6f * u))
        drawRect(a.pleno(P.TECA_GRIS), Offset(al.left + al.width * 0.6f, al.top + 1.4f * u), Size(al.width * 0.2f, 1.6f * u))
    }

    /**
     * Los vidrios de la ventana, encima de la noche: lo que se ve fuera (la niebla de día, el haz del faro de noche, el ocaso o la
     * luna en los Sueños), el vaho con sus gotas, el vidrio roto pegado con papel, las dos hojas de la guillotina con sus
     * junquillos y la persiana enrollada arriba con su cordón.
     */
    fun DrawScope.vidrios(g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.a
        val x0 = w * M.VEN_X0; val x1 = w * M.VEN_X1 - w * 0.016f; val y0 = w * M.VEN_Y0 + w * 0.014f; val y1 = w * M.VEN_Y1
        val hueco = Rect(x0, y0, x1, y1)
        fuera(hueco, a)
        // El vaho: más blanco abajo, donde se junta; las gotas que bajan con su rastro.
        val vaho = if (a.oscuro) P.FRIO_NOCHE.copy(alpha = 0.22f) else if (a.noche) mezcla(P.OCASO, Color.White, 0.5f).copy(alpha = 0.22f) else Color.White.copy(alpha = 0.3f)
        drawRect(Brush.verticalGradient(listOf(vaho.copy(alpha = 0f), vaho), hueco.top + hueco.height * 0.35f, hueco.bottom), hueco.topLeft, hueco.size)
        val gotas = Path(); val rastros = Path()
        for (j in 0 until 26) {
            val gx = hueco.left + hueco.width * azar(7300, j)
            val gy = hueco.top + hueco.height * (0.3f + 0.68f * azar(7301, j))
            val gr = (0.7f + 1.3f * azar(7302, j)) * u
            gotas.addOval(Rect(gx - gr, gy - gr * 1.2f, gx + gr, gy + gr * 1.2f))
            if (j % 3 == 0) { rastros.moveTo(gx, gy - hueco.height * (0.05f + 0.15f * azar(7303, j))); rastros.lineTo(gx + 0.4f * u, gy) }
        }
        val claroGota = if (a.oscuro) P.HAZ.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.5f)
        drawPath(rastros, claroGota.copy(alpha = claroGota.alpha * 0.6f), style = Stroke(0.9f * u))
        drawPath(gotas, claroGota)
        // Las dos hojas de la guillotina: tres por dos vidrios cada una; la de abajo, delante; el travesaño del encuentro, más
        // grueso. La madera, a contraluz: de día casi en silueta, con el filo de la niebla.
        val marco = mezcla(P.YESO, P.PAPEL, 0.4f)
        val k = g.cerca(hueco.center)
        val cMarco = if (a.oscuro) a.luz(mezcla(marco, P.MAR_OSC, 0.5f), k) else mezcla(a.dia(marco, -0.25f), P.MAR_OSC, 0.25f)
        val cFilo = a.frio(if (a.oscuro) 0.45f else 0.7f)
        val barra = 2.4f * u
        val medio = hueco.top + hueco.height * 0.5f
        val barras = Path()
        for (c in 1 until 3) { val x = hueco.left + hueco.width * c / 3f; barras.addRect(Rect(x - barra / 2, hueco.top, x + barra / 2, hueco.bottom)) }
        listOf(0.25f, 0.75f).forEach { f -> val y = hueco.top + hueco.height * f; barras.addRect(Rect(hueco.left, y - barra / 2, hueco.right, y + barra / 2)) }
        drawPath(barras, cMarco)
        // El travesaño del encuentro y el marco de las hojas, alrededor.
        drawRect(cMarco, Offset(hueco.left, medio - 3.4f * u), Size(hueco.width, 6.8f * u))
        drawRect(cFilo, Offset(hueco.left, medio - 3.4f * u), Size(hueco.width, 1f * u))
        drawRect(cMarco, hueco.topLeft, Size(hueco.width, 3.4f * u))
        drawRect(cMarco, Offset(hueco.left, hueco.bottom - 4.6f * u), Size(hueco.width, 4.6f * u))
        drawRect(cMarco, hueco.topLeft, Size(3.4f * u, hueco.height))
        drawRect(cMarco, Offset(hueco.right - 3.4f * u, hueco.top), Size(3.4f * u, hueco.height))
        drawRect(cFilo, Offset(hueco.left + 3.4f * u, hueco.top + 3.4f * u), Size(0.9f * u, hueco.height - 8f * u))
        // El pestillo de latón en el encuentro.
        drawRect(a.luz(P.LATON, k), Offset(hueco.center.x - 3f * u, medio - 4.8f * u), Size(6f * u, 2.4f * u))
        // El vidrio roto de abajo a la derecha, pegado con dos tiras de papel.
        val vx = hueco.left + hueco.width * (2f / 3f) + barra; val vy = medio + 3.4f * u
        val vw = hueco.width / 3f - barra * 1.5f - 3.4f * u
        val grieta = Path().apply { moveTo(vx, vy + vw * 0.3f); lineTo(vx + vw * 0.45f, vy + vw * 0.42f); lineTo(vx + vw * 0.6f, vy + vw * 0.2f); lineTo(vx + vw, vy + vw * 0.28f) }
        drawPath(grieta, claroGota.copy(alpha = 0.7f), style = Stroke(0.7f * u))
        val papel = a.luz(P.MANILA, k)
        listOf(0.28f, 0.62f).forEach { f ->
            val tx = vx + vw * f
            drawRect(papel.copy(alpha = 0.9f), Offset(tx - 2.6f * u, vy + vw * 0.08f), Size(5.2f * u, vw * 0.45f))
        }
        // La persiana de tela enrollada arriba, bajada un poco: la tela, su listón y el cordón con su anilla.
        val pt = Rect(hueco.left - 1f * u, hueco.top - 2f * u, hueco.right + 1f * u, hueco.top + hueco.height * 0.16f)
        val tela = mezcla(P.LONA, P.SALOBRE, 0.25f)
        drawRect(a.luz(tela, k).let { if (!a.oscuro) a.dia(tela, -0.05f) else it }, pt.topLeft, pt.size)
        drawRect(a.sombra(0.25f), Offset(pt.left, pt.top), Size(pt.width, pt.height * 0.3f))
        for (j in 0 until 3) drawLine(a.sombra(0.15f), Offset(pt.left + pt.width * (0.2f + 0.3f * j), pt.top), Offset(pt.left + pt.width * (0.25f + 0.3f * j), pt.bottom), 1.2f * u)
        drawRect(a.dia(P.TECA_OSC, 0f, k), Offset(pt.left, pt.bottom - 2.6f * u), Size(pt.width, 2.6f * u))
        drawRect(a.sombra(0.3f), Offset(pt.left, pt.bottom), Size(pt.width, 2f * u))
        val cx = pt.left + pt.width * 0.62f
        drawLine(a.dia(P.LONA_OSC, -0.1f, k), Offset(cx, pt.bottom), Offset(cx, pt.bottom + w * 0.05f), 1f * u)
        drawCircle(a.dia(P.LATON, 0f, k), 2.4f * u, Offset(cx, pt.bottom + w * 0.05f + 2.4f * u), style = Stroke(1.1f * u))
        // El rollo de arriba.
        drawRect(a.dia(tela, 0.05f, k), Offset(pt.left, pt.top - 3f * u), Size(pt.width, 4f * u))
        drawRect(a.brillo(0.35f, k), Offset(pt.left, pt.top - 3f * u), Size(pt.width, 0.9f * u))
    }

    /** Lo que se ve fuera: la niebla, y de noche el haz del faro que la cruza; en los Sueños, el ocaso o la luna. */
    private fun DrawScope.fuera(r: Rect, a: AireArcano) {
        val u = density
        when {
            !a.lumbre -> {
                drawRect(Brush.verticalGradient(listOf(P.NIEBLA_ALTA, P.NIEBLA, mezcla(P.NIEBLA, Color.White, 0.3f)), r.top, r.bottom), r.topLeft, r.size)
                // Bancos de niebla más densos que pasan.
                clipRect(r.left, r.top, r.right, r.bottom) {
                    for (j in 0 until 4) {
                        val c = Offset(r.left + r.width * azar(7400, j), r.top + r.height * (0.2f + 0.6f * azar(7401, j)))
                        drawOval(Color.White.copy(alpha = 0.22f), Offset(c.x - r.width * 0.5f, c.y - r.height * 0.05f), Size(r.width, r.height * 0.1f))
                    }
                }
            }
            !a.oscuro -> {
                drawRect(Brush.verticalGradient(listOf(P.OCASO_ALTO, P.OCASO, mezcla(P.OCASO, Color(0xFFFFE0B0), 0.5f)), r.top, r.bottom), r.topLeft, r.size)
                clipRect(r.left, r.top, r.right, r.bottom) {
                    for (j in 0 until 3) {
                        val c = Offset(r.left + r.width * azar(7410, j), r.top + r.height * (0.3f + 0.5f * azar(7411, j)))
                        drawOval(Color(0xFFFFE6C8).copy(alpha = 0.3f), Offset(c.x - r.width * 0.5f, c.y - r.height * 0.04f), Size(r.width, r.height * 0.08f))
                    }
                }
            }
            a.noche -> {
                // Los Sueños: la niebla azul de la luna, y la media luna con su halo en la niebla.
                drawRect(Brush.verticalGradient(listOf(P.NIEBLA_NOCHE_ALTA, P.NIEBLA_SUENOS, mezcla(P.NIEBLA_SUENOS, P.LUNA_RAS, 0.25f)), r.top, r.bottom), r.topLeft, r.size)
                clipRect(r.left, r.top, r.right, r.bottom) {
                    val c = Offset(r.left + r.width * 0.64f, r.top + r.height * 0.3f)
                    val rr = r.width * 0.09f
                    drawCircle(Brush.radialGradient(listOf(P.LUNA_RAS.copy(alpha = 0.5f), P.LUNA_RAS.copy(alpha = 0f)), c, rr * 4f), rr * 4f, c)
                    drawPath(crecienteArcano(c, rr), P.LUNA.copy(alpha = 0.92f))
                    drawOval(P.LUNA_RAS.copy(alpha = 0.18f), Offset(r.left - r.width * 0.1f, c.y + rr * 0.6f), Size(r.width * 1.2f, rr * 1.2f))
                }
            }
            else -> {
                // La noche: la niebla verde honda y el haz del faro, que la cruza en diagonal (fuera, en la niebla).
                drawRect(Brush.verticalGradient(listOf(P.NIEBLA_NOCHE_ALTA, P.NIEBLA_NOCHE, mezcla(P.NIEBLA_NOCHE, P.FRIO_NOCHE, 0.4f)), r.top, r.bottom), r.topLeft, r.size)
                clipRect(r.left, r.top, r.right, r.bottom) {
                    val haz = Path().apply {
                        moveTo(r.right + r.width * 0.2f, r.top + r.height * 0.18f)
                        lineTo(r.left - r.width * 0.4f, r.top + r.height * 0.38f)
                        lineTo(r.left - r.width * 0.4f, r.top + r.height * 0.7f)
                        lineTo(r.right + r.width * 0.2f, r.top + r.height * 0.3f)
                        close()
                    }
                    drawPath(haz, Brush.horizontalGradient(listOf(P.HAZ.copy(alpha = 0.55f), P.HAZ.copy(alpha = 0.06f)), r.right + r.width * 0.2f, r.left - r.width * 0.2f))
                    val nucleo = Path().apply {
                        moveTo(r.right + r.width * 0.2f, r.top + r.height * 0.21f)
                        lineTo(r.left - r.width * 0.4f, r.top + r.height * 0.48f)
                        lineTo(r.left - r.width * 0.4f, r.top + r.height * 0.56f)
                        lineTo(r.right + r.width * 0.2f, r.top + r.height * 0.26f)
                        close()
                    }
                    drawPath(nucleo, Brush.horizontalGradient(listOf(P.HAZ.copy(alpha = 0.5f), P.HAZ.copy(alpha = 0f)), r.right + r.width * 0.2f, r.left))
                    // La niebla que el haz deja a la vista, en bancos.
                    for (j in 0 until 3) {
                        val c = Offset(r.left + r.width * azar(7420, j), r.top + r.height * (0.35f + 0.3f * azar(7421, j)))
                        drawOval(P.HAZ.copy(alpha = 0.1f), Offset(c.x - r.width * 0.5f, c.y - r.height * 0.04f), Size(r.width, r.height * 0.08f))
                    }
                }
                drawRect(P.FRIO_NOCHE.copy(alpha = 0.15f), r.topLeft, Size(r.width, 1.2f * u))
            }
        }
    }

    /**
     * El ocaso de los Sueños claros donde la ventana alumbra: la jamba de la derecha, el alféizar y, delante, el canto de la
     * cornisa hasta media pared, cada uno con su borde (la mancha termina en diagonal, por donde ya no llega la ventana).
     */
    fun DrawScope.ocasoArriba(g: MarcoArcano) {
        val a = g.a
        if (a.oscuro || !a.noche) return
        val w = g.w
        val x0 = w * M.VEN_X0; val x1 = w * M.VEN_X1; val y0 = w * M.VEN_Y0; val y1 = w * M.VEN_Y1
        val m = w * 0.014f; val hondo = w * 0.026f
        ocasoEn(a, poligonoArcano(x1, y0 + hondo * 0.55f, x1, y1, x1 - hondo * 0.6f, y1 - hondo * 0.1f, x1 - hondo * 0.6f, y0 + hondo * 0.9f), 0.8f)
        ocasoEn(a, Path().apply { addRect(Rect(x0 - m * 1.6f, y1 + m * 0.3f, x1 + m * 1.6f, y1 + m * 0.8f)) }, 1.1f)
        val ap = g.apoyo
        ocasoEn(a, poligonoArcano(0f, ap, w * 0.56f, ap, w * 0.5f, ap + w * 0.012f, 0f, ap + w * 0.012f), 0.9f)
        val r0 = ap + w * 0.012f; val r1 = ap + w * 0.048f
        ocasoEn(a, poligonoArcano(0f, r0, w * 0.46f, r0, w * 0.42f, r0 + (r1 - r0) * 0.45f, 0f, r0 + (r1 - r0) * 0.45f), 0.65f)
    }

    // ---------------------------------------------------------------- la noche de arriba

    /** La noche (y la sombra del día) de la pared y la cornisa: un mapa chico de luz estirado encima. */
    fun DrawScope.nocheArriba(g: MarcoArcano) {
        val w = g.w
        val ap = g.apoyo / w
        val clave = "a:${w.toInt()}:${g.ch}:${g.a.oscuro}:${g.a.noche}"
        with(MapaLuzArcano) {
            pintar(0, clave, 0f, g.ch, 48) { x, y ->
                val p = Offset(x * w, y * w)
                mascaraArcano(if (y >= ap) g.am else g.a, x, y, g.cerca(p), g.frio(p))
            }
        }
    }

    // ---------------------------------------------------------------- el suelo

    /**
     * El suelo de baldosa hidráulica en damero, hueso y verde pizarra, en escorzo hacia la pared: las juntas que se abren hacia
     * delante, el desgaste del paso, una baldosa partida y la mancha de agua.
     */
    fun DrawScope.suelo(g: MarcoArcano) {
        val w = g.w; val h = g.h; val u = density
        val a = g.asu
        val y0 = g.suelo
        if (h - y0 < 2f) return
        drawRect(a.pleno(mezcla(P.HUESO, P.CORCHO, 0.25f), -0.1f), Offset(0f, y0), Size(w, h - y0))
        val fx = w * 0.5f
        val fondo = 1.1f * w
        // Las filas: más juntas hacia la pared.
        val filas = 6
        fun yFila(i: Int): Float { val t = i / filas.toFloat(); return y0 + (h - y0) * (t * (0.55f + 0.45f * t)) }
        fun xEn(col: Float, y: Float): Float { val s = (fondo + (y - y0)) / fondo; return fx + (w * (col * 0.092f) - fx + w * 0.046f) * s }
        val oscuras = Path()
        for (i in 0 until filas) {
            val ya = yFila(i); val yb = yFila(i + 1)
            for (c in -9..10) {
                if (Math.floorMod(c + i, 2) == 0) continue
                oscuras.addPath(poligonoArcano(xEn(c.toFloat(), ya), ya, xEn(c + 1f, ya), ya, xEn(c + 1f, yb), yb, xEn(c.toFloat(), yb), yb))
            }
        }
        drawPath(oscuras, a.pleno(mezcla(P.PIZARRA, P.HUESO, 0.45f)))
        // El desgaste del paso: las baldosas de delante, más claras y lavadas en el centro.
        drawOval(a.pleno(P.HUESO, 0.1f).copy(alpha = 0.2f), Offset(w * 0.25f, y0 + (h - y0) * 0.45f), Size(w * 0.5f, (h - y0) * 0.5f))
        // Las juntas.
        val juntas = Path()
        for (i in 0..filas) { val y = yFila(i); juntas.moveTo(0f, y); juntas.lineTo(w, y) }
        for (c in -9..11) { juntas.moveTo(xEn(c.toFloat(), y0), y0); juntas.lineTo(xEn(c.toFloat(), h), h) }
        drawPath(juntas, a.sombra(0.35f), style = Stroke(1.1f * u))
        drawPath(juntas, a.claro(0.12f), style = Stroke(0.5f * u))
        pinceladas(Rect(0f, y0, w, h), a.pleno(P.PIZARRA), 7501, 18, u, 0.12f)
        // Una baldosa partida, con su grieta y un trozo que falta.
        val ya = yFila(3); val yb = yFila(4)
        val grieta = Path().apply { moveTo(xEn(2f, ya), ya + (yb - ya) * 0.2f); lineTo(xEn(2.4f, ya), ya + (yb - ya) * 0.5f); lineTo(xEn(2.3f, yb), yb - (yb - ya) * 0.25f); lineTo(xEn(2.9f, yb), yb) }
        drawPath(grieta, a.sombra(0.55f), style = Stroke(1.2f * u))
        drawPath(poligonoArcano(xEn(-3f, yb), yb, xEn(-2.6f, yb), yb, xEn(-2.8f, yb) , yb - (yb - ya) * 0.3f), a.sombra(0.5f))
        // La mancha de agua, alrededor de donde cae la gotera: oscura, con su cerco claro de cal.
        val c = Offset(w * 0.5f, y0 + (h - y0) * 0.62f)
        val mancha = irregularArcano(c, w * 0.2f, (h - y0) * 0.2f, 7510, 14, 0.3f)
        drawPath(mancha, a.sombra(0.28f))
        drawPath(mancha, a.pleno(P.YESO).copy(alpha = 0.3f), style = Stroke(1.4f * u))
        // La sombra del zócalo en el suelo.
        drawRect(a.sombra(0.5f), Offset(0f, y0), Size(w, w * 0.012f))
        drawRect(a.sombra(0.2f), Offset(0f, y0 + w * 0.012f), Size(w, w * 0.02f))
    }

    /** La noche del zócalo y del suelo, en su mapa de luz. */
    fun DrawScope.nocheAbajo(g: MarcoArcano, arriba: Float) {
        val w = g.w
        val clave = "p:${w.toInt()}:${g.h}:${g.base}:${g.a.oscuro}:${g.a.noche}"
        val s = g.suelo / w
        with(MapaLuzArcano) {
            pintar(2, clave, arriba, g.h, 48) { x, y ->
                val p = Offset(x * w, y * w)
                mascaraArcano(if (y < s) g.am else g.asu, x, y, g.cerca(p), 0f)
            }
        }
    }
}

/** Una forma irregular alrededor de [c], de radios [rx] y [ry]: manchas, desconchones, salobre. */
internal fun irregularArcano(c: Offset, rx: Float, ry: Float, semilla: Int, puntos: Int = 9, hondo: Float = 0.5f) = Path().apply {
    for (j in 0 until puntos) {
        val ang = j * 6.2832f / puntos + 0.4f * azar(semilla, 40 + j)
        val f = 1f - hondo + hondo * 1.2f * azar(semilla, j)
        val p = Offset(c.x + rx * f * cos(ang), c.y + ry * f * sin(ang))
        if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
    }
    close()
}

/** Una media luna de radio [r] alrededor de [c], con la parte iluminada a la izquierda (como un creciente). */
internal fun crecienteArcano(c: Offset, r: Float): Path {
    val fuera = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
    val dentro = Path().apply { addOval(Rect(c.x - r * 0.62f, c.y - r * 1.02f, c.x + r * 1.3f, c.y + r * 0.9f)) }
    return Path().apply { op(fuera, dentro, androidx.compose.ui.graphics.PathOperation.Difference) }
}
