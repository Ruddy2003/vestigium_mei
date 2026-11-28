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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El bodegón de la corona de Orden, sobre la cornisa: manda la reliquia bajo su fanal de vidrio (una válvula de vacío de antes
 * de la guerra, con sus placas y su zócalo, sobre un cojín de terciopelo carmesí con borlas y una base de acero con aro de
 * latón); detrás, a la derecha, los dos manuales tumbados con su cadena que cae por el canto y el candelero de púa con su vela
 * gruesa (la luz de arriba de noche); a la izquierda, el rollo de planos grande apoyado en diagonal contra el muro, con otro más
 * fino; delante, una pesa de latón de las del Ideario. En el muro, el plano enmarcado colgado de su riel y el manojo de llaves del
 * archivo en su gancho, con su cordón carmesí. Cada cosa con su sombra en la pared.
 */
internal object CoronaEstanteOrden {
    private val P = PaletaOrden
    private val M = MuebleEstanteOrden

    /** El color de [c] en [p], con la luz del momento: de día, con la mancha de la luminaria. */
    private fun DrawScope.en(c: Color, f: Float, p: Offset, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float, dia: Float = 0.4f): Color {
        val w = size.width
        return if (a.oscuro) { val b = a.luz(c, cerca(p), frio(p)); if (f < 0f) mezcla(b, P.NOCHE, -f * 0.45f) else if (f > 0f) mezcla(b, P.LLAMA, f * 0.25f * cerca(p)) else b }
        else a.luminaria(a.dia(c, f, cerca(p)), M.luminaria(p.x / w, p.y / w) * dia)
    }

    /**
     * La sombra de una cosa en el muro (sin bajar de [apoyo]): de día, abajo a la derecha; de noche, la que tiende la vela del
     * candelero, hacia fuera de ella y solo si está cerca.
     */
    private fun DrawScope.sombraEnMuro(sil: Path, centro: Offset, apoyo: Float, a: AireOrden, cerca: (Offset) -> Float, fuerza: Float = 1f, lejos: Float = 1f) {
        val w = size.width
        clipRect(0f, 0f, w, apoyo + 1f) {
            if (!a.oscuro || a.dormida) translate(w * 0.02f * lejos, w * 0.012f * lejos) { drawPath(sil, a.sombra((if (a.dormida) 0.2f else 0.22f) * fuerza)) }
            if (a.lumbre && !a.dormida) {
                val v = Offset(w * M.VELA_X, w * M.VELA_Y)
                val q = (cerca(centro) * 1.8f).coerceIn(0f, 1f)
                if (q < 0.05f) return@clipRect
                val o = (centro - v) * (0.12f * lejos)
                withTransform({ translate(o.x, o.y); scale(1.05f, 1.05f, centro) }) { drawPath(sil, a.sombra((if (a.oscuro) 0.3f else 0.12f) * fuerza * q)) }
            }
        }
    }

    // ---------------------------------------------------------------- lo del muro

    /** Lo que va detrás del mueble, en el muro: el manojo de llaves del archivo en su gancho. */
    fun DrawScope.atras(w: Float, apoyo: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        planoColgado(w, apoyo, a, cerca, frio)
        llaves(Offset(w * 0.872f, w * 0.118f), w, apoyo, a, cerca, frio)
    }

    /**
     * El plano enmarcado en el muro, sobre los manuales: el riel de colgar con sus dos escuadras, las dos cadenillas, el marco de
     * acero con sus esquinas de latón y el plano azul con su cuadrícula, una pieza acotada y su cajetín sin letras.
     */
    private fun DrawScope.planoColgado(w: Float, apoyo: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val t = Rect(w * 0.53f, w * 0.062f, w * 0.75f, w * 0.198f)
        val k0 = cerca(t.center); val f0 = frio(t.center)
        // El riel y sus escuadras.
        val ry = w * 0.03f
        drawRect(a.sombra(0.3f), Offset(w * 0.49f + 2f * u, ry + 2f * u), Size(w * 0.3f, w * 0.006f))
        drawRect(en(P.ACERO_MEDIO, 0f, Offset(w * 0.64f, ry), a, cerca, frio), Offset(w * 0.49f, ry - w * 0.003f), Size(w * 0.3f, w * 0.006f))
        drawRect(a.destello(0.4f, k0, f0), Offset(w * 0.49f, ry - w * 0.003f), Size(w * 0.3f, 0.8f * u))
        listOf(w * 0.5f, w * 0.78f).forEach { x ->
            drawRect(en(P.ACERO_HONDO, 0f, Offset(x, ry), a, cerca, frio), Offset(x - w * 0.004f, ry - w * 0.012f), Size(w * 0.008f, w * 0.018f))
            roblonOrden(Offset(x, ry - w * 0.007f), w * 0.0032f, a, cerca(Offset(x, ry)), P.ACERO_CLARO, frio(Offset(x, ry)))
        }
        val sil = Path().apply { addRect(t) }
        sombraEnMuro(sil, t.center, apoyo, a, cerca, 1f, 0.5f)
        // Las cadenillas.
        listOf(t.left + w * 0.02f, t.right - w * 0.02f).forEach { x ->
            var y = ry + w * 0.004f
            var q = 0
            while (y < t.top && q < 20) {
                if (q % 2 == 0) drawOval(en(P.ACERO_CLARO, 0f, Offset(x, y), a, cerca, frio), Offset(x - w * 0.002f, y), Size(w * 0.004f, w * 0.007f), style = Stroke(0.9f * u))
                else drawLine(en(P.ACERO_MEDIO, 0f, Offset(x, y), a, cerca, frio), Offset(x, y), Offset(x, y + w * 0.007f), 1.2f * u)
                y += w * 0.006f; q++
            }
        }
        // El marco de acero.
        drawRect(en(P.PAVON, 0f, t.center, a, cerca, frio), t.topLeft, t.size)
        drawRect(a.destello(0.4f, k0, f0), t.topLeft, Size(t.width, 1f * u))
        drawRect(a.destello(0.3f, k0, f0), t.topLeft, Size(1f * u, t.height))
        drawRect(a.sombra(0.4f), Offset(t.left, t.bottom - 1.4f * u), Size(t.width, 1.4f * u))
        drawRect(a.sombra(0.35f), Offset(t.right - 1.4f * u, t.top), Size(1.4f * u, t.height))
        val pl = Rect(t.left + w * 0.009f, t.top + w * 0.009f, t.right - w * 0.009f, t.bottom - w * 0.009f)
        drawRect(a.sombra(0.4f), pl.topLeft, pl.size)
        val adentro = Rect(pl.left + 1f * u, pl.top + 1f * u, pl.right, pl.bottom)
        drawRect(en(P.AZUL_PLANO, 0f, adentro.center, a, cerca, frio), adentro.topLeft, adentro.size)
        val tr = a.luz(P.TRAZO, cerca(pl.center), frio(pl.center)).copy(alpha = 0.55f)
        // La cuadrícula tenue, en un solo camino, y una pieza acotada: el corte de una válvula, su eje y dos cotas.
        val reja = Path()
        var gx = pl.left + w * 0.01f
        while (gx < pl.right) { reja.moveTo(gx, pl.top); reja.lineTo(gx, pl.bottom); gx += w * 0.012f }
        var gy = pl.top + w * 0.01f
        while (gy < pl.bottom) { reja.moveTo(pl.left, gy); reja.lineTo(pl.right, gy); gy += w * 0.012f }
        drawPath(reja, tr.copy(alpha = 0.14f), style = Stroke(0.5f * u))
        val pc = Offset(pl.left + pl.width * 0.4f, pl.top + pl.height * 0.45f)
        val pw = pl.width * 0.22f; val ph = pl.height * 0.32f
        drawRect(tr, Offset(pc.x - pw, pc.y - ph), Size(pw * 2f, ph * 2f), style = Stroke(1f * u))
        drawRect(tr, Offset(pc.x - pw * 0.6f, pc.y - ph * 1.4f), Size(pw * 1.2f, ph * 0.4f), style = Stroke(0.8f * u))
        drawCircle(tr, ph * 0.55f, pc, style = Stroke(0.8f * u))
        drawLine(tr.copy(alpha = 0.4f), Offset(pc.x, pc.y - ph * 1.7f), Offset(pc.x, pc.y + ph * 1.3f), 0.6f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(4f * u, 2f * u)))
        drawLine(tr, Offset(pc.x - pw, pc.y + ph * 1.35f), Offset(pc.x + pw, pc.y + ph * 1.35f), 0.6f * u)
        drawLine(tr, Offset(pc.x - pw, pc.y + ph * 1.15f), Offset(pc.x - pw, pc.y + ph * 1.55f), 0.6f * u)
        drawLine(tr, Offset(pc.x + pw, pc.y + ph * 1.15f), Offset(pc.x + pw, pc.y + ph * 1.55f), 0.6f * u)
        drawLine(tr, Offset(pc.x + pw * 1.35f, pc.y - ph), Offset(pc.x + pw * 1.35f, pc.y + ph), 0.6f * u)
        // El cajetín, abajo a la derecha, sin letras: sus casillas.
        val caj = Rect(pl.right - pl.width * 0.36f, pl.bottom - pl.height * 0.26f, pl.right - w * 0.004f, pl.bottom - w * 0.004f)
        drawRect(tr, caj.topLeft, caj.size, style = Stroke(0.7f * u))
        drawLine(tr, Offset(caj.left, caj.center.y), Offset(caj.right, caj.center.y), 0.5f * u)
        drawLine(tr, Offset(caj.left + caj.width * 0.4f, caj.top), Offset(caj.left + caj.width * 0.4f, caj.bottom), 0.5f * u)
        // Las esquinas de latón del marco.
        listOf(t.topLeft, Offset(t.right, t.top), Offset(t.left, t.bottom), t.bottomRight).forEach { o ->
            val sx = if (o.x < t.center.x) 1f else -1f; val sy = if (o.y < t.center.y) 1f else -1f
            val l = w * 0.016f
            drawPath(poligonoOrden(o.x, o.y, o.x + sx * l, o.y, o.x, o.y + sy * l), en(P.LATON, 0f, o, a, cerca, frio))
        }
        // De noche, la vela de debajo lo alumbra desde abajo: el canto de abajo del marco, encendido.
        if (a.lumbre) drawRect(a.filo(0.55f * cerca(Offset(t.center.x, t.bottom))), Offset(t.left, t.bottom - 1.6f * u), Size(t.width, 1.6f * u))
    }

    /** Las llaves: el gancho, la argolla, cuatro llaves de paletón con su anillo, el cordón carmesí y su borla. */
    private fun DrawScope.llaves(g: Offset, w: Float, apoyo: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val ra = w * 0.026f
        val ca = Offset(g.x, g.y + ra * 0.9f)
        // Cuatro llaves que cuelgan de la argolla, un poco abiertas en abanico.
        val llaves = listOf(Triple(-14f, 0.135f, 0), Triple(-4f, 0.16f, 1), Triple(7f, 0.145f, 2), Triple(17f, 0.115f, 3))
        val sil = Path()
        llaves.forEach { (giro, largo, _) ->
            val ang = Math.toRadians(giro.toDouble())
            val p0 = Offset(ca.x + ra * sin(ang).toFloat(), ca.y + ra * cos(ang).toFloat())
            val p1 = Offset(p0.x + w * largo * sin(ang).toFloat(), p0.y + w * largo * cos(ang).toFloat())
            sil.moveTo(p0.x - w * 0.005f, p0.y); sil.lineTo(p0.x + w * 0.005f, p0.y); sil.lineTo(p1.x + w * 0.012f, p1.y); sil.lineTo(p1.x - w * 0.005f, p1.y); sil.close()
        }
        sil.addOval(Rect(ca.x - ra, ca.y - ra, ca.x + ra, ca.y + ra))
        sombraEnMuro(sil, Offset(g.x, g.y + w * 0.1f), apoyo, a, cerca, 0.9f, 0.6f)
        // El gancho: la placa remachada al muro y la púa.
        val k = cerca(g); val fr = frio(g)
        val placa = Rect(g.x - w * 0.012f, g.y - w * 0.03f, g.x + w * 0.012f, g.y + w * 0.004f)
        drawRect(a.sombra(0.35f), placa.topLeft + Offset(1.4f * u, 1.6f * u), placa.size)
        drawRect(en(P.ACERO_HONDO, 0f, g, a, cerca, frio), placa.topLeft, placa.size)
        drawRect(a.destello(0.35f, k, fr), placa.topLeft, Size(placa.width, 1f * u))
        roblonOrden(Offset(g.x, placa.top + w * 0.008f), w * 0.0042f, a, k, P.ACERO_MEDIO, fr)
        drawLine(en(P.ACERO_MEDIO, 0f, g, a, cerca, frio), Offset(g.x, placa.bottom - w * 0.004f), Offset(g.x, g.y + w * 0.012f), 2.4f * u, StrokeCap.Round)
        // Las llaves, detrás de la argolla las de atrás.
        llaves.forEach { (giro, largo, i) ->
            val ang = giro
            rotate(-ang, ca) {
                val x = ca.x
                val y0 = ca.y + ra * 0.85f
                val y1 = y0 + w * largo
                val hierro = en(if (i % 2 == 0) P.ACERO_MEDIO else mezcla(P.ACERO_HONDO, P.OXIDO, 0.25f), 0f, Offset(x, (y0 + y1) / 2), a, cerca, frio)
                val luz = a.destello(0.55f, cerca(Offset(x, y0)), frio(Offset(x, y0)))
                // El anillo (ovalado, abierto por dentro) por donde pasa la argolla.
                val ar = w * (0.013f + 0.003f * (i % 2))
                translate(1.2f * u, 1.4f * u) { drawOval(a.sombra(0.4f), Offset(x - ar, y0 - ar * 0.2f), Size(ar * 2, ar * 1.6f), style = Stroke(w * 0.0055f)) }
                drawOval(hierro, Offset(x - ar, y0 - ar * 0.2f), Size(ar * 2, ar * 1.6f), style = Stroke(w * 0.0055f))
                drawArc(luz, 180f, 80f, false, Offset(x - ar, y0 - ar * 0.2f), Size(ar * 2, ar * 1.6f), style = Stroke(1f * u))
                // La caña con su collar.
                val cy = y0 + ar * 1.4f
                translate(1.4f * u, 1.6f * u) { drawLine(a.sombra(0.4f), Offset(x, cy), Offset(x, y1), w * 0.0065f) }
                drawLine(hierro, Offset(x, cy), Offset(x, y1), w * 0.0065f)
                drawLine(luz, Offset(x - w * 0.0018f, cy), Offset(x - w * 0.0018f, y1), 0.8f * u)
                drawRect(hierro, Offset(x - w * 0.006f, cy + w * 0.004f), Size(w * 0.012f, w * 0.006f))
                // El paletón: un bloque con dos muescas, a un lado de la punta.
                val pal = Rect(x, y1 - w * 0.03f, x + w * (0.018f + 0.004f * (i % 3)), y1 - w * 0.004f)
                drawRect(a.sombra(0.35f), pal.topLeft + Offset(1.2f * u, 1.4f * u), pal.size)
                drawRect(hierro, pal.topLeft, pal.size)
                drawRect(a.sombra(0.6f), Offset(pal.right - pal.width * 0.42f, pal.top + pal.height * 0.3f), Size(pal.width * 0.42f, pal.height * 0.16f))
                if (i % 2 == 0) drawRect(a.sombra(0.6f), Offset(pal.right - pal.width * 0.3f, pal.top + pal.height * 0.68f), Size(pal.width * 0.3f, pal.height * 0.14f))
                drawRect(luz, pal.topLeft, Size(pal.width, 0.8f * u))
            }
        }
        // La argolla, por delante.
        translate(1.4f * u, 1.6f * u) { drawCircle(a.sombra(0.45f), ra, ca, style = Stroke(w * 0.006f)) }
        drawCircle(en(P.ACERO_MEDIO, 0f, ca, a, cerca, frio), ra, ca, style = Stroke(w * 0.006f))
        drawArc(a.destello(0.6f, k, fr), 160f, 110f, false, Offset(ca.x - ra, ca.y - ra), Size(ra * 2, ra * 2), style = Stroke(1.1f * u))
        // El cordón carmesí atado a la argolla, con su borla.
        val c0 = Offset(ca.x + ra * 0.7f, ca.y + ra * 0.7f)
        val c1 = Offset(c0.x + w * 0.006f, c0.y + w * 0.075f)
        val cordon = Path().apply { moveTo(c0.x, c0.y); cubicTo(c0.x + w * 0.01f, c0.y + w * 0.02f, c1.x - w * 0.004f, c1.y - w * 0.03f, c1.x, c1.y) }
        val grana = en(P.GRANA, 0f, c1, a, cerca, frio)
        translate(1.2f * u, 1.4f * u) { drawPath(cordon, a.sombra(0.35f), style = Stroke(1.6f * u)) }
        drawPath(cordon, grana, style = Stroke(1.6f * u))
        val borla = poligonoOrden(c1.x - w * 0.003f, c1.y, c1.x + w * 0.003f, c1.y, c1.x + w * 0.008f, c1.y + w * 0.03f, c1.x - w * 0.008f, c1.y + w * 0.03f)
        translate(w * 0.005f, w * 0.004f) { drawPath(borla, a.sombra(0.3f)) }
        drawPath(borla, grana)
        clipPath(borla) {
            for (q in 0 until 5) drawLine(en(P.GRANA_HONDA, 0f, c1, a, cerca, frio), Offset(c1.x - w * 0.006f + q * w * 0.003f, c1.y + w * 0.008f), Offset(c1.x - w * 0.008f + q * w * 0.004f, c1.y + w * 0.03f), 0.6f * u)
            drawRect(a.brillo(0.3f, cerca(c1)), Offset(c1.x - w * 0.008f, c1.y), Size(w * 0.004f, w * 0.03f))
        }
        drawCircle(en(P.LATON, 0f, c1, a, cerca, frio), w * 0.0045f, c1)
    }

    // ---------------------------------------------------------------- lo de encima de la cornisa

    /** Lo que pisa la cornisa, de atrás adelante. */
    fun DrawScope.delante(w: Float, apoyo: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        rollos(w, apoyo, a, cerca, frio)
        manuales(w, apoyo, a, cerca, frio)
        candelero(w, apoyo, a, cerca, frio)
        fanal(w, apoyo, a, cerca, frio)
        tintero(Offset(w * 0.15f, apoyo), w, apoyo, a, cerca, frio)
        pesa(Offset(w * 0.215f, apoyo + w * 0.002f), w, a, cerca, frio)
        if (a.lumbre) llama(Offset(w * M.VELA_X, w * (M.VELA_Y + 0.0095f)), w * (if (a.oscuro && !a.dormida) 0.0105f else 0.0085f), a)
    }

    /** Un rollo de plano inclinado [giro] grados desde su pie [pie], de [largo] y [grueso], con su espiral arriba y dos ataduras. */
    private fun DrawScope.rollo(pie: Offset, largo: Float, grueso: Float, giro: Float, w: Float, apoyo: Float, papel: Color, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float, semilla: Int) {
        val u = density
        val cuerpo = Rect(pie.x - grueso / 2, pie.y - largo, pie.x + grueso / 2, pie.y)
        val ang = Math.toRadians(giro.toDouble())
        val centro = Offset(pie.x + largo / 2 * sin(ang).toFloat(), pie.y - largo / 2 * cos(ang).toFloat())
        // La silueta girada, para su sombra en el muro.
        val sil = Path().apply { addRect(cuerpo) }
        val silG = Path().apply {
            fun g(x: Float, y: Float): Offset { val dx = x - pie.x; val dy = y - pie.y; return Offset(pie.x + dx * cos(ang).toFloat() - dy * sin(ang).toFloat(), pie.y + dx * sin(ang).toFloat() + dy * cos(ang).toFloat()) }
            val q = listOf(g(cuerpo.left, cuerpo.bottom), g(cuerpo.right, cuerpo.bottom), g(cuerpo.right, cuerpo.top), g(cuerpo.left, cuerpo.top))
            moveTo(q[0].x, q[0].y); q.drop(1).forEach { lineTo(it.x, it.y) }; close()
        }
        sombraEnMuro(silG, centro, apoyo, a, cerca, 1f, 0.8f)
        rotate(giro, pie) {
            val k = cerca(centro); val fr = frio(centro)
            val cara = a.luz(papel, k, fr).let { if (!a.oscuro) a.luminaria(it, M.luminaria(centro.x / w, centro.y / w) * 0.4f) else it }
            redondoOrden(cuerpo, cara, mezcla(cara, if (a.oscuro) P.LLAMA else P.LUZ_FRIA, if (a.oscuro) 0.12f * k else 0.35f), mezcla(cara, P.NOCHE, 0.55f), -1f)
            // La orilla de la última vuelta, en espiral, y unos trazos del plano por dentro.
            clipPath(sil) {
                val tr = a.luz(P.TRAZO, k * 0.8f, fr).copy(alpha = 0.38f)
                var y = cuerpo.top + largo * 0.1f
                var i = 0
                while (y < cuerpo.bottom && i < 30) {
                    drawLine(tr, Offset(cuerpo.left, y + grueso * 0.5f), Offset(cuerpo.right, y - grueso * 0.2f), 0.6f * u)
                    y += largo * (0.07f + 0.05f * azar(semilla, i)); i++
                }
                drawLine(a.sombra(0.4f), Offset(cuerpo.left, cuerpo.top + largo * 0.7f), Offset(cuerpo.right, cuerpo.top + largo * 0.62f), 1f * u)
                drawLine(a.brillo(0.4f, k), Offset(cuerpo.left, cuerpo.top + largo * 0.7f - 1f * u), Offset(cuerpo.right, cuerpo.top + largo * 0.62f - 1f * u), 0.7f * u)
            }
            // Las ataduras: un cordón crudo en dos vueltas.
            listOf(0.22f, 0.78f).forEach { f ->
                val y = cuerpo.top + largo * f
                val cord = a.luz(P.PAPEL, k * 0.8f, fr)
                drawRect(a.sombra(0.4f), Offset(cuerpo.left, y + 1.4f * u), Size(cuerpo.width, 1.2f * u))
                drawRect(cord, Offset(cuerpo.left, y), Size(cuerpo.width, 1.6f * u))
                drawRect(cord, Offset(cuerpo.left, y + 2.6f * u), Size(cuerpo.width, 1.2f * u))
            }
            // La boca de arriba: el canto del rollo con su espiral.
            val bo = Rect(cuerpo.left, cuerpo.top - grueso * 0.22f, cuerpo.right, cuerpo.top + grueso * 0.22f)
            drawOval(a.luz(mezcla(papel, P.TRAZO, 0.45f), k, fr), bo.topLeft, bo.size)
            for (q in 1..3) {
                val f = 1f - q * 0.24f
                drawOval(a.luz(papel, k, fr).copy(alpha = 0.8f), Offset(bo.center.x - bo.width / 2 * f, bo.center.y - bo.height / 2 * f), Size(bo.width * f, bo.height * f), style = Stroke(0.7f * u))
            }
            drawOval(a.sombra(0.35f), bo.topLeft, bo.size, style = Stroke(0.7f * u))
        }
    }

    /** El rollo de planos grande apoyado en diagonal contra el muro y otro más fino al lado. */
    private fun DrawScope.rollos(w: Float, apoyo: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        rollo(Offset(w * 0.3f, apoyo - w * 0.002f), w * 0.33f, w * 0.03f, 22f, w, apoyo, mezcla(P.AZUL_PLANO, P.TRAZO, 0.08f), a, cerca, frio, 71)
        rollo(Offset(w * 0.245f, apoyo - w * 0.002f), w * 0.41f, w * 0.046f, 12f, w, apoyo, P.AZUL_PLANO, a, cerca, frio, 72)
        // Su pie en la cornisa.
        contactoOrden(Offset(w * 0.27f, apoyo), w * 0.11f, w * 0.008f, a, 0.4f)
    }

    /** Los dos manuales tumbados, uno encima del otro, con sus cantoneras y la cadena del de arriba que cae por el canto. */
    private fun DrawScope.manuales(w: Float, apoyo: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val abajo = Rect(w * 0.47f, apoyo - w * 0.044f, w * 0.79f, apoyo)
        val arriba = Rect(w * 0.53f, abajo.top - w * 0.037f, w * 0.77f, abajo.top)
        val sil = Path().apply { addRect(abajo); addRect(arriba) }
        sombraEnMuro(sil, Offset(w * 0.63f, apoyo - w * 0.04f), apoyo, a, cerca, 1f, 0.6f)
        contactoOrden(Offset(abajo.center.x, apoyo), abajo.width * 1.05f, w * 0.01f, a, 0.45f)
        // El de abajo: se le ve el lomo, de cuero pardo, con sus nervios y las cantoneras.
        lomoTumbado(abajo, P.CUERO_PARDO, a, cerca, frio, 3)
        // El de arriba: se le ve el canto de las hojas, entre las tapas de cuero carmesí oscuro.
        contactoOrden(Offset(arriba.center.x, arriba.bottom), arriba.width, w * 0.007f, a, 0.4f)
        val tapa = w * 0.007f
        val k = cerca(arriba.center); val fr = frio(arriba.center)
        drawRect(en(P.GRANA_HONDA, 0f, arriba.center, a, cerca, frio), arriba.topLeft, Size(arriba.width, tapa))
        drawRect(en(P.GRANA_HONDA, -0.1f, arriba.center, a, cerca, frio), Offset(arriba.left, arriba.bottom - tapa), Size(arriba.width, tapa))
        val hojas = Rect(arriba.left + w * 0.004f, arriba.top + tapa, arriba.right - w * 0.006f, arriba.bottom - tapa)
        drawRect(en(P.PAPEL, 0f, hojas.center, a, cerca, frio), hojas.topLeft, hojas.size)
        for (q in 0 until 6) drawLine(a.sombra(0.16f), Offset(hojas.left + 1f * u, hojas.top + hojas.height * (q + 0.5f) / 6), Offset(hojas.right, hojas.top + hojas.height * (q + 0.5f) / 6), 0.5f * u)
        drawRect(a.sombra(0.3f), Offset(hojas.right - w * 0.012f, hojas.top), Size(w * 0.012f, hojas.height))
        drawLine(a.destello(0.35f, k, fr), Offset(arriba.left, arriba.top + 0.5f * u), Offset(arriba.right, arriba.top + 0.5f * u), 0.9f * u)
        listOf(arriba.left to 1f, arriba.right to -1f).forEach { (x, s) ->
            drawPath(poligonoOrden(x, arriba.top, x + s * w * 0.018f, arriba.top, x + s * w * 0.016f, arriba.top + tapa, x, arriba.top + tapa), en(P.ACERO_CLARO, 0f, Offset(x, arriba.top), a, cerca, frio))
            drawPath(poligonoOrden(x, arriba.bottom - tapa, x + s * w * 0.016f, arriba.bottom - tapa, x + s * w * 0.018f, arriba.bottom, x, arriba.bottom), en(P.ACERO_MEDIO, 0f, Offset(x, arriba.bottom), a, cerca, frio))
        }
        // El broche de acero que cruza el canto.
        val bx = arriba.left + arriba.width * 0.32f
        drawRect(a.sombra(0.35f), Offset(bx + 1.2f * u, arriba.top + tapa * 0.5f), Size(w * 0.012f, arriba.height - tapa))
        drawRect(en(P.ACERO_MEDIO, 0.05f, Offset(bx, arriba.center.y), a, cerca, frio), Offset(bx - w * 0.006f, arriba.top + tapa * 0.3f), Size(w * 0.012f, arriba.height - tapa * 0.6f))
        drawRect(a.destello(0.5f, k, fr), Offset(bx - w * 0.006f, arriba.top + tapa * 0.3f), Size(1f * u, arriba.height - tapa * 0.6f))
        // La cadena: de la grapa del canto, cae por delante de la cornisa en una comba y vuelve a la barra del mueble.
        val g0 = Offset(arriba.right - w * 0.02f, arriba.center.y)
        drawRect(en(P.LATON, 0f, g0, a, cerca, frio), Offset(g0.x - w * 0.005f, g0.y - w * 0.007f), Size(w * 0.01f, w * 0.014f))
        cadena(listOf(g0, Offset(w * 0.8f, apoyo - w * 0.006f), Offset(w * 0.805f, apoyo + w * 0.03f), Offset(w * 0.82f, apoyo + w * 0.058f), Offset(w * 0.845f, apoyo + w * 0.05f), Offset(w * 0.858f, apoyo + w * 0.026f)), w, a, cerca, frio)
    }

    /** Un manual tumbado visto por el lomo en [r]: el cuero con su luz arriba, los nervios, las cantoneras y el desgaste. */
    private fun DrawScope.lomoTumbado(r: Rect, cuero: Color, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float, nervios: Int) {
        val u = density
        val w = size.width
        val k = cerca(r.center); val fr = frio(r.center)
        val c = en(cuero, 0f, r.center, a, cerca, frio)
        drawRect(c, r.topLeft, r.size)
        drawRect(mezcla(c, if (a.oscuro) P.LLAMA else P.LUZ_FRIA, if (a.oscuro) 0.15f * k else 0.22f), r.topLeft, Size(r.width, r.height * 0.28f))
        drawRect(mezcla(c, P.NOCHE, 0.4f), Offset(r.left, r.bottom - r.height * 0.3f), Size(r.width, r.height * 0.3f))
        for (q in 1..nervios) {
            val x = r.left + r.width * q / (nervios + 1)
            drawRect(a.sombra(0.4f), Offset(x + 1.4f * u, r.top), Size(1.4f * u, r.height))
            drawRect(mezcla(c, P.LUZ_FRIA, if (a.oscuro) 0.05f else 0.25f), Offset(x - 1.4f * u, r.top), Size(2.6f * u, r.height))
        }
        // Las cantoneras de acero de las puntas.
        listOf(true, false).forEach { izq ->
            val x0 = if (izq) r.left else r.right - w * 0.02f
            val can = Rect(x0, r.top, x0 + w * 0.02f, r.bottom)
            drawRect(en(P.ACERO_MEDIO, 0f, can.center, a, cerca, frio), can.topLeft, can.size)
            drawRect(a.destello(0.4f, k, fr), can.topLeft, Size(can.width, 1f * u))
            drawRect(a.sombra(0.35f), Offset(can.right - 1.4f * u, can.top), Size(1.4f * u, can.height))
            roblonOrden(can.center, w * 0.0035f, a, k, P.ACERO_CLARO, fr)
        }
        for (q in 0 until 5) {
            val x = r.left + r.width * azar(931, q); val y = r.top + r.height * azar(932, q)
            drawLine(a.destello(0.2f, k, fr), Offset(x, y), Offset(x + w * 0.02f, y + 0.4f * u), 0.6f * u)
        }
    }

    /** Una cadena de eslabones de acero por los puntos [ps], alternando eslabón de canto y de frente. */
    private fun DrawScope.cadena(ps: List<Offset>, w: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val paso = w * 0.0105f
        var i = 0
        for (s in 0 until ps.size - 1) {
            val p0 = ps[s]; val p1 = ps[s + 1]
            val d = (p1 - p0).getDistance()
            val n = maxOf(1, (d / paso).toInt())
            val ang = Math.toDegrees(kotlin.math.atan2((p1.y - p0.y).toDouble(), (p1.x - p0.x).toDouble())).toFloat()
            for (j in 0 until n) {
                val p = p0 + (p1 - p0) * ((j + 0.5f) / n)
                val acero = en(P.ACERO_MEDIO, 0f, p, a, cerca, frio)
                rotate(ang, p) {
                    if (i % 2 == 0) {
                        drawOval(a.sombra(0.4f), Offset(p.x - paso * 0.75f + 1f * u, p.y - paso * 0.42f + 1.2f * u), Size(paso * 1.5f, paso * 0.84f), style = Stroke(1.5f * u))
                        drawOval(acero, Offset(p.x - paso * 0.75f, p.y - paso * 0.42f), Size(paso * 1.5f, paso * 0.84f), style = Stroke(1.5f * u))
                        drawArc(a.destello(0.6f, cerca(p), frio(p)), 190f, 90f, false, Offset(p.x - paso * 0.75f, p.y - paso * 0.42f), Size(paso * 1.5f, paso * 0.84f), style = Stroke(0.7f * u))
                    } else {
                        drawLine(acero, Offset(p.x - paso * 0.7f, p.y), Offset(p.x + paso * 0.7f, p.y), 1.9f * u, StrokeCap.Round)
                        drawLine(a.destello(0.5f, cerca(p), frio(p)), Offset(p.x - paso * 0.5f, p.y - 0.6f * u), Offset(p.x + paso * 0.4f, p.y - 0.6f * u), 0.6f * u)
                    }
                }
                i++
            }
        }
    }

    /** El candelero de púa sobre los manuales: el pie, el nudo, la arandela y la vela gruesa con sus chorreras. */
    private fun DrawScope.candelero(w: Float, apoyo: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val cx = w * M.VELA_X
        val pisa = apoyo - w * 0.081f
        val topeVela = w * (M.VELA_Y + 0.0095f)
        val arandela = topeVela + w * 0.06f
        val sil = poligonoOrden(cx - w * 0.03f, pisa, cx + w * 0.03f, pisa, cx + w * 0.006f, arandela, cx + w * 0.016f, arandela, cx + w * 0.016f, topeVela, cx - w * 0.016f, topeVela, cx - w * 0.016f, arandela, cx - w * 0.006f, arandela)
        sombraEnMuro(sil, Offset(cx, (pisa + topeVela) / 2), apoyo, a, cerca, if (a.oscuro) 0f else 1f, 0.7f)
        val k = cerca(Offset(cx, arandela)); val fr = frio(Offset(cx, arandela))
        contactoOrden(Offset(cx, pisa), w * 0.07f, w * 0.008f, a, 0.45f)
        // El pie, de campana.
        val pie = Path().apply {
            moveTo(cx - w * 0.032f, pisa); lineTo(cx + w * 0.032f, pisa)
            cubicTo(cx + w * 0.026f, pisa - w * 0.008f, cx + w * 0.008f, pisa - w * 0.012f, cx + w * 0.006f, pisa - w * 0.02f)
            lineTo(cx - w * 0.006f, pisa - w * 0.02f)
            cubicTo(cx - w * 0.008f, pisa - w * 0.012f, cx - w * 0.026f, pisa - w * 0.008f, cx - w * 0.032f, pisa)
            close()
        }
        val hierro = en(P.ACERO_HONDO, 0f, Offset(cx, pisa), a, cerca, frio)
        drawPath(pie, hierro)
        clipPath(pie) {
            drawRect(a.destello(0.35f, k, fr), Offset(cx - w * 0.03f, pisa - w * 0.02f), Size(w * 0.016f, w * 0.02f))
            drawRect(a.sombra(0.35f), Offset(cx + w * 0.008f, pisa - w * 0.02f), Size(w * 0.03f, w * 0.02f))
        }
        // El fuste con su nudo.
        val fuste = Rect(cx - w * 0.0045f, arandela, cx + w * 0.0045f, pisa - w * 0.019f)
        redondoOrden(fuste, hierro, mezcla(hierro, P.LUZ_FRIA, if (a.oscuro) 0.05f else 0.4f), mezcla(hierro, P.NOCHE, 0.5f), if (a.oscuro && cx > w * M.VELA_X) 1f else -1f)
        val nudo = Rect(cx - w * 0.011f, fuste.top + fuste.height * 0.45f, cx + w * 0.011f, fuste.top + fuste.height * 0.45f + w * 0.014f)
        drawOval(hierro, nudo.topLeft, nudo.size)
        drawOval(a.destello(0.45f, k, fr), Offset(nudo.left + nudo.width * 0.15f, nudo.top + nudo.height * 0.15f), Size(nudo.width * 0.3f, nudo.height * 0.3f))
        // La arandela de la cera, de latón.
        val ar = Rect(cx - w * 0.024f, arandela - w * 0.004f, cx + w * 0.024f, arandela + w * 0.005f)
        drawOval(a.sombra(0.35f), ar.topLeft + Offset(1f * u, 1.4f * u), ar.size)
        drawOval(en(P.LATON, 0f, ar.center, a, cerca, frio, 0.5f), ar.topLeft, ar.size)
        drawOval(en(P.LATON_BRILLO, 0f, ar.center, a, cerca, frio), Offset(ar.left + ar.width * 0.1f, ar.top), Size(ar.width * 0.5f, ar.height * 0.45f))
        // La vela gruesa: la cera con su lado de luz, el borde derretido, las chorreras y la mecha.
        val vela = Rect(cx - w * 0.013f, topeVela, cx + w * 0.013f, arandela - w * 0.002f)
        val kc = cerca(vela.center)
        val cera = en(P.CERA, 0f, vela.center, a, cerca, frio, 0.5f)
        drawRect(cera, vela.topLeft, vela.size)
        if (a.oscuro) {
            // De noche la llama alumbra la cera desde arriba: más clara cerca de la llama.
            drawRect(Brush.verticalGradient(listOf(P.LLAMA.copy(alpha = 0.45f * kc), Color.Transparent), vela.top, vela.bottom), vela.topLeft, vela.size)
        } else drawRect(P.LUZ_FRIA.copy(alpha = 0.4f), vela.topLeft, Size(vela.width * 0.3f, vela.height))
        drawRect(a.sombra(if (a.oscuro) 0.15f else 0.22f), Offset(vela.right - vela.width * 0.3f, vela.top), Size(vela.width * 0.3f, vela.height))
        drawOval(mezcla(cera, P.SOMBRA, 0.18f), Offset(vela.left + vela.width * 0.1f, vela.top - vela.width * 0.1f), Size(vela.width * 0.8f, vela.width * 0.26f))
        listOf(0.14f to 0.55f, 0.72f to 0.85f, 0.42f to 0.3f).forEach { (fx, fl) ->
            val x = vela.left + vela.width * fx
            drawLine(mezcla(cera, P.LUZ_FRIA, if (a.oscuro) 0.1f else 0.3f), Offset(x, vela.top + 0.6f * u), Offset(x + 0.3f * u, vela.top + vela.height * fl), vela.width * 0.16f, StrokeCap.Round)
        }
        drawLine(mezcla(cera, P.LUZ_FRIA, 0.2f), Offset(ar.left + ar.width * 0.2f, ar.top + 1f * u), Offset(ar.left + ar.width * 0.16f, ar.bottom + w * 0.006f), 2f * u, StrokeCap.Round)
        drawLine(en(P.BETUN, 0f, vela.center, a, cerca, frio), Offset(cx, vela.top + 0.6f * u), Offset(cx + w * 0.001f, vela.top - w * 0.006f), 1.1f * u, StrokeCap.Round)
    }

    /** La llama de una vela encendida con su resplandor; en la guardia de los Sueños oscuros, baja. */
    fun DrawScope.llama(p: Offset, s: Float, a: AireOrden) {
        val f = if (a.dormida) 0.55f else if (a.oscuro) 1f else 0.6f
        drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.34f * f), P.LLAMA.copy(alpha = 0f)), p - Offset(0f, s * 0.6f), s * 7f), s * 7f, p - Offset(0f, s * 0.6f))
        drawPath(Path().apply {
            moveTo(p.x, p.y - s * 1.75f)
            cubicTo(p.x + s * 0.55f, p.y - s * 0.85f, p.x + s * 0.5f, p.y, p.x, p.y + s * 0.15f)
            cubicTo(p.x - s * 0.5f, p.y, p.x - s * 0.55f, p.y - s * 0.85f, p.x, p.y - s * 1.75f)
            close()
        }, P.LLAMA)
        drawPath(Path().apply {
            moveTo(p.x, p.y - s * 1.1f)
            cubicTo(p.x + s * 0.3f, p.y - s * 0.6f, p.x + s * 0.26f, p.y - s * 0.05f, p.x, p.y + s * 0.02f)
            cubicTo(p.x - s * 0.26f, p.y - s * 0.05f, p.x - s * 0.3f, p.y - s * 0.6f, p.x, p.y - s * 1.1f)
            close()
        }, P.CORAZON)
        drawOval(P.AZUL_PLANO.copy(alpha = 0.5f), Offset(p.x - s * 0.22f, p.y - s * 0.12f), Size(s * 0.44f, s * 0.26f))
    }

    /**
     * El fanal con la reliquia: la base de acero con su aro de latón, el cojín de terciopelo carmesí con sus borlas, la válvula de
     * vacío de pie (el zócalo de baquelita, la ampolla con la placa, las micas, la rejilla y el espejo del getter arriba) y la
     * campana de vidrio con su pomo, que deja ver el muro y coge la luz en sus cantos.
     */
    private fun DrawScope.fanal(w: Float, apoyo: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val cx = w * 0.44f
        val base = Rect(cx - w * 0.088f, apoyo - w * 0.026f, cx + w * 0.088f, apoyo)
        val cojin = Rect(cx - w * 0.07f, base.top - w * 0.036f, cx + w * 0.07f, base.top + w * 0.004f)
        val campana = Rect(cx - w * 0.074f, apoyo - w * 0.31f, cx + w * 0.074f, base.top + w * 0.002f)
        val sil = Path().apply { addRoundRect(RoundRect(campana, CornerRadius(campana.width / 2, campana.width / 2))); addRect(base) }
        sombraEnMuro(sil, Offset(cx, apoyo - w * 0.15f), apoyo, a, cerca, 0.55f, 0.9f)
        val k = cerca(Offset(cx, apoyo - w * 0.12f)); val fr = frio(Offset(cx, apoyo - w * 0.12f))
        contactoOrden(Offset(cx, apoyo), base.width * 1.05f, w * 0.011f, a, 0.5f)
        // La base: la cara de arriba en escorzo, el frente de acero y el aro de latón.
        drawOval(en(P.PAVON_LUZ, 0.05f, base.center, a, cerca, frio), Offset(base.left, base.top - w * 0.008f), Size(base.width, w * 0.016f))
        drawRect(en(P.PAVON, 0f, base.center, a, cerca, frio), Offset(base.left, base.top), Size(base.width, base.height))
        redondoOrden(Rect(base.left, base.top + base.height * 0.45f, base.right, base.top + base.height * 0.8f), en(P.LATON, 0f, base.center, a, cerca, frio),
            en(P.LATON_BRILLO, 0f, base.center, a, cerca, frio), en(P.LATON_HONDO, 0f, base.center, a, cerca, frio), if (a.oscuro && !a.dormida) 1f else -1f, 0.9f)
        drawRect(a.sombra(0.4f), Offset(base.left, base.bottom - 1.4f * u), Size(base.width, 1.4f * u))
        drawRect(a.destello(0.4f, k, fr), Offset(base.left, base.top), Size(base.width, 1f * u))
        // El cojín de terciopelo carmesí: panzón, con su luz arriba a la izquierda y sus borlas.
        val cj = Path().apply { addRoundRect(RoundRect(cojin, CornerRadius(cojin.height * 0.5f, cojin.height * 0.42f))) }
        drawPath(cj, en(P.GRANA, 0f, cojin.center, a, cerca, frio))
        clipPath(cj) {
            drawOval(en(P.GRANA_LUZ, 0f, cojin.center, a, cerca, frio).copy(alpha = 0.55f), Offset(cojin.left + cojin.width * 0.08f, cojin.top + cojin.height * 0.08f), Size(cojin.width * 0.5f, cojin.height * 0.42f))
            drawRect(en(P.GRANA_HONDA, 0f, cojin.center, a, cerca, frio).copy(alpha = 0.7f), Offset(cojin.left, cojin.bottom - cojin.height * 0.32f), Size(cojin.width, cojin.height * 0.32f))
            drawRect(en(P.GRANA_HONDA, 0f, cojin.center, a, cerca, frio).copy(alpha = 0.45f), Offset(cojin.right - cojin.width * 0.22f, cojin.top), Size(cojin.width * 0.22f, cojin.height))
            // La costura del borde, en latón.
            drawLine(en(P.LATON, 0f, cojin.center, a, cerca, frio).copy(alpha = 0.7f), Offset(cojin.left + 3f * u, cojin.center.y + cojin.height * 0.1f), Offset(cojin.right - 3f * u, cojin.center.y + cojin.height * 0.1f), 0.8f * u)
        }
        listOf(cojin.left + 2f * u, cojin.right - 2f * u).forEach { x ->
            val b0 = Offset(x, cojin.center.y + cojin.height * 0.1f)
            val bor = poligonoOrden(b0.x - w * 0.003f, b0.y, b0.x + w * 0.003f, b0.y, b0.x + w * 0.006f, b0.y + w * 0.024f, b0.x - w * 0.006f, b0.y + w * 0.024f)
            drawPath(bor, en(P.LATON, 0f, b0, a, cerca, frio))
            drawLine(en(P.LATON_HONDO, 0f, b0, a, cerca, frio), Offset(b0.x + w * 0.002f, b0.y + w * 0.006f), Offset(b0.x + w * 0.003f, b0.y + w * 0.024f), 0.6f * u)
            drawCircle(en(P.LATON_HONDO, 0f, b0, a, cerca, frio), w * 0.004f, b0)
        }
        // La válvula: el zócalo de baquelita con su guía y la ampolla de vidrio con su punta.
        val zocalo = Rect(cx - w * 0.024f, cojin.top + w * 0.004f - w * 0.028f, cx + w * 0.024f, cojin.top + w * 0.006f)
        val bq = en(P.BAQUELITA, 0f, zocalo.center, a, cerca, frio)
        redondoOrden(zocalo, bq, mezcla(bq, P.LUZ_FRIA, if (a.oscuro) 0.06f else 0.28f), mezcla(bq, P.NOCHE, 0.4f), -1f)
        drawRect(a.sombra(0.4f), Offset(zocalo.left, zocalo.bottom - 1.4f * u), Size(zocalo.width, 1.4f * u))
        val amp = Rect(cx - w * 0.033f, zocalo.top - w * 0.16f, cx + w * 0.033f, zocalo.top + w * 0.002f)
        val ampolla = Path().apply {
            moveTo(amp.left, amp.bottom); lineTo(amp.left, amp.top + amp.width * 0.5f)
            cubicTo(amp.left, amp.top + amp.width * 0.1f, amp.right, amp.top + amp.width * 0.1f, amp.right, amp.top + amp.width * 0.5f)
            lineTo(amp.right, amp.bottom); close()
        }
        // Por dentro: el espejo del getter arriba, la placa con su ranura, las dos micas, la rejilla y los hilos.
        clipPath(ampolla) {
            drawRect(en(Color(0xFF3A4048), 0f, amp.center, a, cerca, frio).copy(alpha = 0.22f), amp.topLeft, amp.size)
            drawRect(en(Color(0xFFB8BEC6), 0.1f, amp.center, a, cerca, frio), Offset(amp.left, amp.top), Size(amp.width, amp.width * 0.38f))
            drawRect(en(Color(0xFF5E636B), 0f, amp.center, a, cerca, frio), Offset(amp.left, amp.top + amp.width * 0.3f), Size(amp.width, amp.width * 0.1f))
        }
        val placa = Rect(cx - w * 0.02f, amp.top + amp.height * 0.36f, cx + w * 0.02f, amp.bottom - amp.height * 0.16f)
        val gris = en(Color(0xFF4A4F57), 0f, placa.center, a, cerca, frio)
        drawRect(gris, placa.topLeft, placa.size)
        drawRect(mezcla(gris, P.LUZ_FRIA, if (a.oscuro) 0.06f else 0.3f), placa.topLeft, Size(placa.width * 0.3f, placa.height))
        drawRect(mezcla(gris, P.NOCHE, 0.45f), Offset(placa.center.x - 0.8f * u, placa.top + placa.height * 0.12f), Size(1.6f * u, placa.height * 0.76f))
        listOf(placa.top - w * 0.004f, placa.bottom).forEach { y ->
            drawRect(en(Color(0xFFE6E2D4), 0f, placa.center, a, cerca, frio).copy(alpha = 0.85f), Offset(amp.left + w * 0.003f, y), Size(amp.width - w * 0.006f, w * 0.004f))
        }
        val hilo = en(P.ACERO_CLARO, 0f, placa.center, a, cerca, frio).copy(alpha = 0.7f)
        listOf(-0.027f, 0.027f).forEach { dx -> drawLine(hilo, Offset(cx + w * dx * 0.95f, placa.top - w * 0.004f), Offset(cx + w * dx * 0.95f, amp.bottom), 0.6f * u) }
        for (q in 0 until 5) drawLine(hilo.copy(alpha = 0.35f), Offset(placa.left - w * 0.003f, placa.top + placa.height * (q + 0.5f) / 5), Offset(placa.right + w * 0.003f, placa.top + placa.height * (q + 0.5f) / 5), 0.5f * u)
        // El vidrio de la ampolla: su canto y sus brillos.
        drawPath(ampolla, a.luz(P.VIDRIO, k, fr).copy(alpha = 0.55f), style = Stroke(1f * u))
        drawLine(a.brillo(0.75f, k), Offset(amp.left + amp.width * 0.18f, amp.top + amp.width * 0.4f), Offset(amp.left + amp.width * 0.18f, amp.bottom - amp.height * 0.1f), 1.4f * u, StrokeCap.Round)
        drawLine(en(P.VIDRIO, 0f, amp.center, a, cerca, frio), Offset(cx, amp.top + amp.width * 0.24f), Offset(cx, amp.top - w * 0.005f), 1.6f * u, StrokeCap.Round)
        // La campana de vidrio: casi transparente, con el muro detrás un punto más frío; sus cantos y brillos.
        val cam = Path().apply { addRoundRect(RoundRect(campana, CornerRadius(campana.width / 2, campana.width / 2))) }
        drawPath(cam, a.luz(P.VIDRIO, k * 0.5f, fr).copy(alpha = if (a.oscuro) 0.12f else 0.14f))
        clipPath(cam) {
            // Los reflejos: una banda ancha a la izquierda (la luminaria) y una fina a la derecha.
            val luzV = if (a.oscuro) (if (a.dormida) P.GUARDIA.copy(alpha = 0.28f) else P.LLAMA.copy(alpha = 0.12f + 0.3f * k)) else (if (a.noche) P.VIGILIA else P.LUZ_FRIA).copy(alpha = 0.42f)
            drawRect(luzV, Offset(campana.left + campana.width * 0.1f, campana.top + campana.width * 0.22f), Size(campana.width * 0.1f, campana.height * 0.7f))
            drawRect(luzV.copy(alpha = luzV.alpha * 0.5f), Offset(campana.left + campana.width * 0.23f, campana.top + campana.width * 0.3f), Size(campana.width * 0.03f, campana.height * 0.6f))
            // De noche, el reflejo de la vela en el lado que la mira.
            if (a.oscuro && !a.dormida) drawRect(P.LLAMA.copy(alpha = 0.5f), Offset(campana.right - campana.width * 0.16f, campana.top + campana.height * 0.32f), Size(campana.width * 0.05f, campana.height * 0.16f))
            drawRect(a.sombra(0.14f), Offset(campana.right - campana.width * 0.12f, campana.top), Size(campana.width * 0.12f, campana.height))
        }
        drawPath(cam, a.luz(mezcla(P.VIDRIO, P.ACERO_CLARO, 0.3f), maxOf(k, 0.35f), fr).copy(alpha = if (a.oscuro) 0.6f else 0.65f), style = Stroke(1.6f * u))
        drawArc(a.brillo(0.8f, maxOf(k, 0.3f)), 200f, 60f, false, Offset(campana.left + campana.width * 0.12f, campana.top + campana.width * 0.12f), Size(campana.width * 0.76f, campana.width * 0.76f), style = Stroke(1.6f * u))
        // El pomo de vidrio de arriba.
        val pomo = Offset(cx, campana.top - w * 0.006f)
        drawCircle(a.luz(P.VIDRIO, k, fr).copy(alpha = 0.55f), w * 0.009f, pomo)
        drawCircle(a.brillo(0.85f, maxOf(k, 0.3f)), w * 0.003f, pomo - Offset(w * 0.003f, w * 0.003f))
        drawRect(a.luz(P.VIDRIO, k, fr).copy(alpha = 0.5f), Offset(cx - w * 0.004f, campana.top - w * 0.002f), Size(w * 0.008f, w * 0.004f))
        // El canto de abajo de la campana, donde asienta en la base.
        drawRect(a.luz(mezcla(P.VIDRIO, P.ACERO_CLARO, 0.4f), k, fr).copy(alpha = 0.7f), Offset(campana.left, campana.bottom - w * 0.004f), Size(campana.width, w * 0.004f))
    }

    /**
     * El tintero de la escriba, a la izquierda de la cornisa: el pie de acero ochavado, el vaso de vidrio con la tinta, la tapa
     * de latón abierta sobre su bisagra y la pluma de acero en su mango, apoyada en diagonal.
     */
    private fun DrawScope.tintero(pie: Offset, w: Float, apoyo: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val x = pie.x; val y = pie.y
        val k = cerca(Offset(x, y - w * 0.03f)); val fr = frio(Offset(x, y - w * 0.03f))
        // La pluma: el mango de baquelita con su virola de latón y el plumín de acero, apoyada en el vaso.
        val p0 = Offset(x - w * 0.006f, y - w * 0.03f)
        val p1 = Offset(x - w * 0.075f, y - w * 0.15f)
        val sil = poligonoOrden(p0.x, p0.y, p1.x - w * 0.004f, p1.y, p1.x + w * 0.004f, p1.y)
        sombraEnMuro(sil, Offset(x - w * 0.04f, y - w * 0.09f), apoyo, a, cerca, 1f, 0.6f)
        drawLine(a.sombra(0.35f), p0 + Offset(1.4f * u, 1.6f * u), p1 + Offset(1.4f * u, 1.6f * u), w * 0.008f, StrokeCap.Round)
        drawLine(en(P.BAQUELITA, 0f, p1, a, cerca, frio), p0, p1, w * 0.008f, StrokeCap.Round)
        drawLine(a.destello(0.45f, k, fr), p0 + Offset(-w * 0.002f, -w * 0.001f), p1 + Offset(-w * 0.002f, -w * 0.001f), 0.8f * u)
        val vir = p0 + (p1 - p0) * 0.12f
        drawLine(en(P.LATON, 0f, vir, a, cerca, frio), vir, p0 + (p1 - p0) * 0.2f, w * 0.0095f)
        // El pie de acero ochavado y el vaso.
        contactoOrden(Offset(x, y), w * 0.075f, w * 0.009f, a, 0.45f)
        val base = Rect(x - w * 0.034f, y - w * 0.012f, x + w * 0.034f, y)
        drawPath(poligonoOrden(base.left + w * 0.008f, base.top, base.right - w * 0.008f, base.top, base.right, base.bottom, base.left, base.bottom), en(P.PAVON, 0f, base.center, a, cerca, frio))
        drawLine(a.destello(0.45f, k, fr), Offset(base.left + w * 0.008f, base.top), Offset(base.right - w * 0.008f, base.top), 0.9f * u)
        val vaso = Rect(x - w * 0.022f, y - w * 0.04f, x + w * 0.022f, base.top)
        redondoOrden(vaso, a.luz(Color(0xFF2A3440), k, fr), a.luz(Color(0xFF8A98A8), k, fr), a.luz(Color(0xFF12161C), k, fr), -1f)
        drawLine(a.brillo(0.6f, maxOf(k, 0.3f)), Offset(vaso.left + vaso.width * 0.22f, vaso.top + 2f * u), Offset(vaso.left + vaso.width * 0.22f, vaso.bottom - 2f * u), 1.2f * u)
        // El cuello de latón y la tapa abierta, de canto, hacia atrás.
        val cuello = Rect(x - w * 0.012f, vaso.top - w * 0.006f, x + w * 0.012f, vaso.top)
        drawRect(en(P.LATON, 0f, cuello.center, a, cerca, frio), cuello.topLeft, cuello.size)
        drawRect(a.destello(0.5f, k, fr), cuello.topLeft, Size(cuello.width, 0.8f * u))
        drawOval(en(P.LATON, 0.05f, cuello.center, a, cerca, frio), Offset(x + w * 0.004f, cuello.top - w * 0.02f), Size(w * 0.01f, w * 0.022f))
        drawOval(en(P.LATON_HONDO, 0f, cuello.center, a, cerca, frio), Offset(x + w * 0.006f, cuello.top - w * 0.017f), Size(w * 0.005f, w * 0.016f))
    }

    /** Una pesa de latón de las del plano del Ideario, en la cornisa: el cilindro con su perilla. */
    private fun DrawScope.pesa(pie: Offset, w: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val x = pie.x; val y = pie.y
        contactoOrden(Offset(x, y), w * 0.04f, w * 0.008f, a, 0.45f)
        val cuerpo = Rect(x - w * 0.015f, y - w * 0.024f, x + w * 0.015f, y)
        val k = cerca(cuerpo.center); val fr = frio(cuerpo.center)
        redondoOrden(cuerpo, en(P.LATON, 0f, cuerpo.center, a, cerca, frio), en(P.LATON_BRILLO, 0f, cuerpo.center, a, cerca, frio), en(P.LATON_HONDO, 0f, cuerpo.center, a, cerca, frio), -1f)
        drawOval(en(P.LATON_BRILLO, 0f, cuerpo.center, a, cerca, frio), Offset(cuerpo.left, cuerpo.top - w * 0.004f), Size(cuerpo.width, w * 0.008f))
        drawOval(en(P.LATON, 0f, cuerpo.center, a, cerca, frio), Offset(x - w * 0.0055f, cuerpo.top - w * 0.012f), Size(w * 0.011f, w * 0.01f))
        drawCircle(a.destello(0.7f, k, fr), w * 0.002f, Offset(x - w * 0.002f, cuerpo.top - w * 0.008f))
        drawLine(en(P.LATON_HONDO, 0f, cuerpo.center, a, cerca, frio), Offset(cuerpo.left, y - w * 0.008f), Offset(cuerpo.right, y - w * 0.008f), 0.7f * u)
    }
}
