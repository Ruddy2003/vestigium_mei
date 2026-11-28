package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRefugio as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

/**
 * La pared del cuarto de registros (la del Ideario): el muro de bloques de hormigón pintado de crema, con sus juntas trabadas y su
 * bisel, los desconchones, la canaleta de cables de lado a lado arriba, la tubería verde azulada que baja al grifo, el reloj de
 * rayos y el esquema de la depuradora pegado con cinta. Y el fondo de cada hueco: melamina oliva con bumeranes.
 */
internal object ParedEstanteRefugio {
    /** Lo hondo: el oscuro con color en que se hunden las sombras. */
    fun honda(g: MarcoRefugio): Color = if (g.a.oscuro) g.a.hondo else mezcla(P.OLIVA_OSC, Color(0xFF14201C), 0.4f)

    /**
     * Un rectángulo pintado del [color] con la luz de cada sitio: franjas a lo alto de [paso] px, cada una con su degradado a lo
     * ancho. Para lo que se pinta una sola vez (la corona guardada).
     */
    fun DrawScope.campo(g: MarcoRefugio, r: Rect, color: Color, delante: Float, paso: Float = 4f, muestras: Int = 11) {
        if (r.width < 1f || r.height < 1f) return
        val franjas = ceil(r.height / maxOf(1f, paso)).toInt().coerceIn(1, 400)
        val alto = r.height / franjas
        drawRect(g.luz(r.center.x, r.center.y, delante).c(color), r.topLeft, r.size)
        for (i in 0 until franjas) {
            val y = r.top + alto * (i + 0.5f)
            val cols = List(muestras) { j -> g.luz(r.left + r.width * j / (muestras - 1f), y, delante).c(color) }
            drawRect(Brush.horizontalGradient(cols, r.left, r.right), Offset(r.left, r.top + alto * i), Size(r.width, minOf(alto + 1.2f, r.bottom - r.top - alto * i)))
        }
    }

    // ---------------------------------------------------------------- la pared de arriba (se pinta una vez, en la corona guardada)

    fun DrawScope.paredArriba(g: MarcoRefugio) {
        val w = g.w; val u = density
        val ch = g.ch
        campo(g, Rect(0f, 0f, w, ch), P.CREMA, 0f, paso = 5f)
        // La pintura dada a rodillo y lo gastado.
        pinceladas(Rect(0f, 0f, w, ch), if (g.a.oscuro) g.a.hondo else P.CREMA_OSC, 8101, 34, u, 0.09f)
        // De día, la mancha de las luminarias arriba a la izquierda (como en el Ideario).
        if (g.a.techo) mancha(Offset(w * 0.05f, w * 0.02f), w * 0.75f, P.LUZ_TECHO.copy(alpha = 0.45f))
        if (g.a.tarde) mancha(Offset(w * 0.04f, w * 0.05f), w * 0.6f, P.MELOCOTON.copy(alpha = 0.32f))
        // De noche, el halo del fósforo en la pared, alrededor de la pantalla.
        if (g.a.pantalla > 0f) mancha(g.pantalla, w * 0.42f, P.FOSFORO.copy(alpha = 0.09f * g.a.pantalla))
        bloques(g, 0f, ch)
        listOf(Triple(0.975f, 0.3f, 0.016f), Triple(0.47f, 0.08f, 0.011f), Triple(0.03f, 0.37f, 0.014f), Triple(0.64f, 0.36f, 0.012f),
            Triple(0.21f, 0.06f, 0.01f)).forEachIndexed { j, (x, y, r) -> desconchon(g, x * w, y * w, r * w, 8120 + j * 7) }
        // El techo: lo de más arriba, en penumbra.
        drawRect(Brush.verticalGradient(listOf(g.a.sombra(if (g.a.oscuro) 0.45f else 0.26f), g.a.sombra(0f)), 0f, w * 0.07f), Offset.Zero, Size(w, w * 0.07f))
        esquema(g)
        reloj(g)
        tuberia(g)
        canaleta(g)
    }

    /** Las juntas de los bloques, en hiladas trabadas, con su bisel claro debajo, entre [y0] e [y1]. En dos caminos. */
    fun DrawScope.bloques(g: MarcoRefugio, y0: Float, y1: Float) {
        val w = g.w; val u = density
        val bh = w * 0.078f; val bw = w * 0.2f
        val juntas = Path(); val biseles = Path()
        var fila = 0
        var y = bh * 0.55f
        var ya = 0f
        while (ya < y1 && fila < 200) {
            if (y > y0 && y < y1) {
                juntas.addRect(Rect(0f, y - 0.6f * u, w, y + 0.6f * u))
                biseles.addRect(Rect(0f, y + 0.6f * u, w, y + 1.3f * u))
            }
            var x = (if (fila % 2 == 0) 0f else bw / 2) + bw * 0.12f
            val a = maxOf(ya, y0); val b = minOf(y, y1)
            if (b > a) while (x < w) {
                juntas.addRect(Rect(x - 0.6f * u, a, x + 0.6f * u, b))
                biseles.addRect(Rect(x + 0.6f * u, a, x + 1.3f * u, b))
                x += bw
            }
            ya = y; y += bh; fila++
        }
        val l = g.luz(w * 0.5f, (y0 + y1) / 2, 0f)
        drawPath(juntas, if (g.a.oscuro) P.NOCHE.copy(alpha = 0.45f) else g.a.sombra(0.14f))
        drawPath(biseles, if (g.a.oscuro) l.b(0.18f) else l.b(0.32f))
    }

    /** Un desconchón: el hueco con su borde de arriba en sombra y el hormigón que asoma, más claro abajo a la derecha. */
    private fun DrawScope.desconchon(g: MarcoRefugio, x: Float, y: Float, r: Float, semilla: Int) {
        val u = density
        val l = g.luz(x, y, 0f)
        val p = escamaRefugio(Offset(x, y), r * 1.4f, r, semilla, 9)
        drawPath(p, l.s(0.18f))
        translate(0.8f * u, 0.8f * u) { clipPath(p) { drawPath(p, l.c(mezcla(P.CREMA_OSC, P.HORMIGON, 0.55f))) } }
        for (j in 0 until 3) drawCircle(l.s(0.25f), 0.5f * u, Offset(x + r * (azar(semilla + 3, j) - 0.4f), y + r * 0.5f * (azar(semilla + 4, j) - 0.3f)))
    }

    /** La canaleta de cables de lado a lado, arriba (la del Ideario): la bandeja de chapa, sus grapas y tres cables que cuelgan. */
    private fun DrawScope.canaleta(g: MarcoRefugio) {
        val w = g.w; val u = density
        val y = w * 0.016f; val al = w * 0.018f
        val l = g.luz(w * 0.3f, y, 0.02f)
        drawRect(l.s(0.2f), Offset(0f, y + al), Size(w, al * 0.7f))
        cromoRefugio(Rect(0f, y, w, y + al), g.luz(w * 0.2f, y, 0.02f), brillo = 0.35f)
        drawRect(g.luz(w * 0.5f, y, 0.02f).c(P.ACERO).copy(alpha = 0.55f), Offset(0f, y), Size(w, al))
        drawRect(l.s(0.25f), Offset(0f, y + al - 1.2f * u), Size(w, 1.2f * u))
        val grapas = listOf(-0.03f, 0.19f, 0.41f, 0.62f, 0.83f, 1.03f)
        val cables = listOf(mezcla(P.TINTA, P.OLIVA_OSC, 0.3f) to 2.4f, P.TEAL to 2f, P.OCRE to 1.6f)
        cables.forEachIndexed { j, (col, gr) ->
            val yc = y + al + (1.2f + j * 1.8f) * u
            for (s in 0 until grapas.size - 1) {
                val a = w * grapas[s]; val bx = w * grapas[s + 1]
                val lc = g.luz((a + bx) / 2, yc, 0.02f)
                val cuelga = w * (0.008f + 0.012f * azar(8140 + j, s))
                val p = Path().apply { moveTo(a, yc - 1f * u); cubicTo(a + (bx - a) * 0.3f, yc + cuelga, a + (bx - a) * 0.7f, yc + cuelga, bx, yc - 1f * u) }
                translate(w * 0.008f, w * 0.006f) { drawPath(p, lc.s(0.12f), style = Stroke(gr * u)) }
                drawPath(p, lc.c(col), style = Stroke(gr * u, cap = StrokeCap.Round))
                drawPath(p, lc.b(0.3f), style = Stroke(gr * 0.3f * u))
            }
        }
        grapas.forEach { fx ->
            val x = w * fx
            val lg = g.luz(x, y, 0.025f)
            drawRect(lg.s(0.2f), Offset(x - w * 0.006f + 1.5f * u, y - 2f * u + 1.5f * u), Size(w * 0.012f, al + 9f * u))
            drawRect(lg.c(P.ACERO_OSC), Offset(x - w * 0.006f, y - 2f * u), Size(w * 0.012f, al + 9f * u))
            drawRect(lg.b(0.4f), Offset(x - w * 0.006f, y - 2f * u), Size(1f * u, al + 9f * u))
            drawCircle(lg.c(P.ACERO), 1.1f * u, Offset(x, y + al + 5.5f * u))
        }
    }

    /**
     * La tubería verde azulada que baja del techo (la del Ideario), con sus abrazaderas, el codo y el grifo cromado sobre la boca de
     * la garrafa, con una gota a punto de caer.
     */
    private fun DrawScope.tuberia(g: MarcoRefugio) {
        val w = g.w; val u = density
        val x = w * CoronaEstanteRefugio.GRIFO_X; val r = w * 0.011f
        val fin = w * 0.215f
        val l = g.luz(x, fin * 0.6f, 0.02f)
        val tubo = Rect(x - r, 0f, x + r, fin)
        val ar = g.arrojada(1.2f)
        drawRect(l.s(0.18f), Offset(tubo.left + ar.x, 0f), Size(tubo.width, fin + ar.y))
        drawRect(l.c(mezcla(P.TEAL, P.ACERO, 0.2f)), tubo.topLeft, tubo.size)
        cilindroRefugio(Path().apply { addRect(tubo) }, l.c(mezcla(P.TEAL, P.ACERO, 0.2f)), l, -1f, 0.5f)
        listOf(w * 0.075f, w * 0.15f).forEach { y ->
            val la = g.luz(x, y, 0.025f)
            drawRect(la.s(0.2f), Offset(tubo.right, y + 1.5f * u), Size(w * 0.02f, 4f * u))
            drawRect(la.c(P.ACERO_OSC), Offset(tubo.left - 1.5f * u, y), Size(tubo.width + 3f * u, 4f * u))
            drawRect(la.b(0.45f), Offset(tubo.left - 1.5f * u, y), Size(tubo.width + 3f * u, 1f * u))
            drawCircle(la.c(P.ACERO), 1.3f * u, Offset(tubo.right + 3f * u, y + 2f * u))
        }
        // La llave de paso, pequeña, naranja (la del Ideario).
        val lv = g.luz(x, w * 0.11f, 0.03f)
        val c = Offset(x + r * 0.3f, w * 0.112f)
        val rv = r * 1.25f
        drawCircle(lv.s(0.25f), rv, c + Offset(2f * u, 2f * u), style = Stroke(2.2f * u))
        drawCircle(lv.c(P.NARANJA), rv, c, style = Stroke(2.2f * u))
        for (k in 0 until 3) { val a = k * 2.094f + 0.5f; drawLine(lv.c(P.NARANJA), c, Offset(c.x + rv * cos(a), c.y + rv * sin(a)), 1.4f * u) }
        drawCircle(lv.c(P.ACERO), r * 0.3f, c)
        // El grifo cromado: el cuerpo, el caño curvo hacia abajo y el mando en cruz… no: el mando de palanca.
        val lg = g.luz(x, fin, 0.035f)
        val cuerpo = Rect(x - r * 1.3f, fin - r * 0.4f, x + r * 1.3f, fin + r * 1.6f)
        drawRoundRect(lg.s(0.3f), cuerpo.topLeft + Offset(1.6f * u, 1.6f * u), cuerpo.size, CornerRadius(r * 0.5f))
        drawRoundRect(lg.c(P.CROMO_OSC), cuerpo.topLeft, cuerpo.size, CornerRadius(r * 0.5f))
        cromoRefugio(cuerpo.deflate(0.8f * u), lg, vertical = true)
        val cano = Path().apply {
            moveTo(x - r * 0.45f, cuerpo.bottom - 1f)
            lineTo(x - r * 0.45f, cuerpo.bottom + r * 1.2f)
            lineTo(x + r * 0.45f, cuerpo.bottom + r * 1.2f)
            lineTo(x + r * 0.45f, cuerpo.bottom - 1f)
            close()
        }
        drawPath(cano, lg.c(P.CROMO))
        drawRect(lg.b(0.8f), Offset(x - r * 0.3f, cuerpo.bottom), Size(r * 0.2f, r * 1.1f))
        // La palanca, a la izquierda, inclinada.
        drawLine(lg.c(P.CROMO_OSC), Offset(x, cuerpo.top + r * 0.3f), Offset(x - r * 2.6f, cuerpo.top - r * 0.9f), 2.4f * u, StrokeCap.Round)
        drawLine(lg.b(0.8f), Offset(x, cuerpo.top + r * 0.1f), Offset(x - r * 2.4f, cuerpo.top - r * 1.05f), 0.8f * u, StrokeCap.Round)
        // La gota.
        val gota = Offset(x, cuerpo.bottom + r * 2.3f)
        drawCircle(lg.c(mezcla(P.AGUA_VIDRIO, P.CROMO_LUZ, 0.4f)), r * 0.36f, gota)
        drawCircle(lg.b(0.9f), r * 0.12f, gota - Offset(r * 0.1f, r * 0.12f))
    }

    /**
     * El reloj de rayos de la pared: la esfera crema sin cifras, con doce marcas cortas y las dos agujas, y alrededor los rayos de
     * latón, largos y cortos, que dejan su sombra en el muro. De noche, las agujas de pintura fosforescente.
     */
    private fun DrawScope.reloj(g: MarcoRefugio) {
        val w = g.w; val u = density
        val c = Offset(w * 0.1f, w * 0.165f)
        val re = w * 0.034f
        val l = g.luz(c, 0.02f)
        val rayos = Path(); val luces = Path()
        for (k in 0 until 24) {
            val a = k * 0.2618f
            val largo = re * (if (k % 2 == 0) 2.15f else 1.6f)
            val ancho = if (k % 2 == 0) 0.07f else 0.05f
            val p0 = Offset(c.x + re * 0.9f * cos(a - ancho), c.y + re * 0.9f * sin(a - ancho))
            val p1 = Offset(c.x + largo * cos(a), c.y + largo * sin(a))
            val p2 = Offset(c.x + re * 0.9f * cos(a + ancho), c.y + re * 0.9f * sin(a + ancho))
            rayos.moveTo(p0.x, p0.y); rayos.lineTo(p1.x, p1.y); rayos.lineTo(p2.x, p2.y); rayos.close()
            if (cos(a) + sin(a) < 0f) { luces.moveTo(p0.x, p0.y); luces.lineTo(p1.x, p1.y); luces.lineTo((p0.x + p2.x) / 2, (p0.y + p2.y) / 2); luces.close() }
        }
        val ar = g.arrojada(1f)
        drawPath(corridoRefugio(rayos, ar.x, ar.y), l.s(0.2f))
        drawCircle(l.s(0.25f), re * 1.08f, c + ar)
        drawPath(rayos, l.c(P.OCRE))
        drawPath(luces, l.b(0.45f))
        // Las bolitas de las puntas de los rayos largos.
        for (k in 0 until 12) {
            val a = k * 0.5236f
            val q = Offset(c.x + re * 2.2f * cos(a), c.y + re * 2.2f * sin(a))
            drawCircle(l.c(mezcla(P.OCRE, P.MANTEQUILLA, 0.3f)), re * 0.11f, q)
            drawCircle(l.b(0.6f), re * 0.05f, q - Offset(re * 0.03f, re * 0.03f))
        }
        // La esfera, con su aro cromado.
        drawCircle(l.c(P.CROMO_OSC), re * 1.05f, c)
        drawCircle(l.c(P.CROMO), re, c)
        drawCircle(l.c(mezcla(P.VINILO, P.MANTEQUILLA, 0.25f)), re * 0.86f, c)
        drawArc(l.s(0.25f), 0f, 180f, false, Offset(c.x - re * 0.84f, c.y - re * 0.8f), Size(re * 1.68f, re * 1.68f), style = Stroke(1.4f * u))
        for (k in 0 until 12) {
            val a = k * 0.5236f
            val lar = if (k % 3 == 0) 0.22f else 0.12f
            drawLine(l.c(P.TINTA).copy(alpha = 0.75f), Offset(c.x + re * (0.78f - lar) * cos(a), c.y + re * (0.78f - lar) * sin(a)), Offset(c.x + re * 0.78f * cos(a), c.y + re * 0.78f * sin(a)), (if (k % 3 == 0) 1.3f else 0.8f) * u)
        }
        val aguja = if (g.a.oscuro || g.a.tarde) mezcla(P.FOSFORESCENTE, P.FOSFORO, 0.25f).copy(alpha = if (g.a.oscuro) 0.95f else 0.8f) else l.c(P.TINTA)
        // Las diez y diez de siempre no: las cinco y veinte.
        drawLine(aguja, c, Offset(c.x + re * 0.45f * cos(2.2f), c.y + re * 0.45f * sin(2.2f)), 1.8f * u, StrokeCap.Round)
        drawLine(aguja, c, Offset(c.x + re * 0.68f * cos(0.52f), c.y + re * 0.68f * sin(0.52f)), 1.2f * u, StrokeCap.Round)
        drawLine(l.c(P.CORAL), c, Offset(c.x + re * 0.7f * cos(-1.2f), c.y + re * 0.7f * sin(-1.2f)), 0.6f * u)
        drawCircle(l.c(P.CROMO_OSC), 1.6f * u, c)
        drawArc(l.b(0.7f), 190f, 80f, false, Offset(c.x - re * 0.92f, c.y - re * 0.92f), Size(re * 1.84f, re * 1.84f), style = Stroke(1f * u))
    }

    /**
     * El esquema de la depuradora, en papel clavado con cinta: dos tanques con su agua, la columna del filtro con sus capas de grava,
     * arena y carbón, la bomba, los caños con sus flechas y la franja de abajo con renglones sin letra; un doblez y una esquina
     * levantada.
     */
    private fun DrawScope.esquema(g: MarcoRefugio) {
        val w = g.w; val u = density
        val r = Rect(w * 0.665f, w * 0.075f, w * 0.935f, w * 0.29f)
        val l = g.luz(r.center, 0.008f)
        val papel = l.c(mezcla(P.ETIQUETA, Color.White, 0.3f))
        val ar = g.arrojada(0.7f)
        drawRect(l.s(0.24f), r.topLeft + ar, r.size)
        val esq = r.width * 0.13f
        val hoja = poligonoRefugio(listOf(Offset(r.left, r.top), Offset(r.right, r.top), Offset(r.right, r.bottom - esq), Offset(r.right - esq, r.bottom), Offset(r.left, r.bottom)))
        drawPath(hoja, papel)
        clipPath(hoja) {
            val tinta = l.c(P.TEAL)
            val m = r.width * 0.07f
            // El tanque de la izquierda (agua turbia) y el de la derecha (agua limpia), con su nivel y sus ondas.
            val t1 = Rect(r.left + m, r.top + r.height * 0.18f, r.left + r.width * 0.33f, r.top + r.height * 0.62f)
            val t2 = Rect(r.left + r.width * 0.66f, r.top + r.height * 0.22f, r.right - m, r.top + r.height * 0.62f)
            listOf(t1 to mezcla(P.OCRE, P.CREMA, 0.4f), t2 to mezcla(P.AGUA_VIDRIO, P.CREMA, 0.2f)).forEach { (t, agua) ->
                drawRect(l.c(agua), Offset(t.left, t.top + t.height * 0.35f), Size(t.width, t.height * 0.65f))
                drawRoundRect(tinta, t.topLeft, t.size, CornerRadius(t.width * 0.18f), style = Stroke(1.3f * u))
                for (j in 0 until 2) {
                    val yy = t.top + t.height * (0.5f + 0.2f * j)
                    val onda = Path().apply {
                        moveTo(t.left + t.width * 0.15f, yy)
                        var x = t.left + t.width * 0.15f
                        var k = 0
                        while (x < t.right - t.width * 0.2f && k < 12) { quadraticTo(x + t.width * 0.08f, yy - 1.5f * u, x + t.width * 0.16f, yy); x += t.width * 0.16f; k++ }
                    }
                    drawPath(onda, tinta.copy(alpha = 0.6f), style = Stroke(0.8f * u))
                }
            }
            // La columna del filtro, en medio: las capas de carbón, arena y grava.
            val f = Rect(r.left + r.width * 0.43f, r.top + r.height * 0.12f, r.left + r.width * 0.56f, r.top + r.height * 0.66f)
            val capas = listOf(P.TINTA to 0.28f, mezcla(P.OCRE, P.CREMA, 0.3f) to 0.3f, mezcla(P.HORMIGON, P.OCRE, 0.3f) to 0.22f)
            var yy = f.top + f.height * 0.2f
            capas.forEach { (col, fr) -> drawRect(l.c(col).copy(alpha = 0.75f), Offset(f.left, yy), Size(f.width, f.height * fr)); yy += f.height * fr }
            drawRoundRect(tinta, f.topLeft, f.size, CornerRadius(f.width * 0.4f), style = Stroke(1.3f * u))
            // Los caños, en ángulo recto, con sus flechas.
            val canos = Path().apply {
                moveTo(t1.right, t1.top + t1.height * 0.45f); lineTo(f.left + f.width * 0.5f, t1.top + t1.height * 0.45f); lineTo(f.left + f.width * 0.5f, f.top)
                moveTo(f.left + f.width * 0.5f, f.bottom); lineTo(f.left + f.width * 0.5f, r.top + r.height * 0.74f); lineTo(t2.left + t2.width * 0.5f, r.top + r.height * 0.74f); lineTo(t2.left + t2.width * 0.5f, t2.bottom)
            }
            drawPath(canos, tinta, style = Stroke(1.1f * u))
            listOf(Offset(t1.right + (f.left - t1.right) * 0.4f, t1.top + t1.height * 0.45f) to 0f, Offset(f.left + f.width * 1.6f, r.top + r.height * 0.74f) to 0f).forEach { (p, _) ->
                drawPath(poligonoRefugio(Offset(p.x - 2f * u, p.y - 2.2f * u), Offset(p.x + 2.6f * u, p.y), Offset(p.x - 2f * u, p.y + 2.2f * u)), l.c(P.CORAL))
            }
            // La bomba: un círculo con su boca de salida.
            val bc = Offset(r.left + r.width * 0.3f, r.top + r.height * 0.8f)
            drawCircle(l.c(P.CORAL).copy(alpha = 0.85f), r.width * 0.045f, bc)
            drawCircle(tinta, r.width * 0.045f, bc, style = Stroke(1.1f * u))
            drawRect(tinta, Offset(bc.x + r.width * 0.04f, bc.y - r.width * 0.045f), Size(r.width * 0.05f, 1.1f * u))
            // La franja de abajo, con renglones sin letra.
            val franja = Rect(r.left, r.top + r.height * 0.88f, r.right, r.bottom)
            drawRect(l.c(P.TEAL), franja.topLeft, franja.size)
            drawPath(renglonesRefugio(Rect(franja.left + m, franja.top + franja.height * 0.3f, franja.right - m * 2f, franja.bottom), franja.height * 0.22f, franja.height * 0.4f, 8170), papel)
            // El papel gastado: el doblez del medio y manchas.
            val dx = r.left + r.width * 0.5f
            drawLine(l.s(0.14f), Offset(dx, r.top), Offset(dx, r.bottom), 1f * u)
            drawLine(l.b(0.4f), Offset(dx + 1f * u, r.top), Offset(dx + 1f * u, r.bottom), 0.7f * u)
            pinceladas(r, if (g.a.oscuro) g.a.hondo else P.CREMA_OSC, 8160, 8, u, 0.14f)
        }
        // La esquina levantada: el revés y su sombra.
        val doblez = poligonoRefugio(Offset(r.right, r.bottom - esq), Offset(r.right - esq * 0.15f, r.bottom - esq * 0.15f), Offset(r.right - esq, r.bottom))
        translate(1.5f * u, 2f * u) { drawPath(doblez, l.s(0.22f)) }
        drawPath(doblez, l.c(mezcla(P.ETIQUETA, P.CREMA_OSC, 0.6f)))
        // La cinta en las cuatro esquinas, ámbar transparente.
        listOf(Offset(r.left, r.top) to -35f, Offset(r.right, r.top) to 35f, Offset(r.left, r.bottom) to 35f).forEach { (p, gr) ->
            rotate(gr, p) { drawRect(l.c(mezcla(P.ETIQUETA, P.OCRE, 0.35f)).copy(alpha = 0.7f), Offset(p.x - r.width * 0.07f, p.y - 3f * u), Size(r.width * 0.14f, 6f * u)) }
        }
    }

    // ---------------------------------------------------------------- el fondo de cada hueco

    private var claveBaldosa = ""
    private var pincel: ShaderBrush? = null
    private var claveBaldosa2 = ""
    private var pincel2: ShaderBrush? = null

    /**
     * El dibujo de la melamina del fondo: bumeranes y motas de la época, apagados, en una baldosa que empalma por los cuatro lados
     * y se pinta una sola vez por densidad y momento. Es transparente salvo el dibujo: se pega encima del oliva ya alumbrado.
     */
    private fun baldosa(u: Float, a: AireRefugio): ShaderBrush {
        val clave = "$u:${a.oscuro}:${a.noche}"
        if (clave == claveBaldosa) pincel?.let { return it }
        if (clave == claveBaldosa2) pincel2?.let { return it }
        val ancho = (120f * u).toInt().coerceAtLeast(8)
        val alto = (90f * u).toInt().coerceAtLeast(8)
        val img = ImageBitmap(ancho, alto)
        val fuerza = if (a.oscuro) 0.45f else 1f
        val cols = listOf(P.OCRE, P.CREMA, P.TEAL, P.CORAL, P.OLIVA_LUZ)
        CanvasDrawScope().draw(Density(u), LayoutDirection.Ltr, Canvas(img), Size(ancho.toFloat(), alto.toFloat())) {
            val aw = ancho.toFloat(); val ah = alto.toFloat()
            // Los bumeranes: dos brazos curvos que se juntan, a distintos giros, de cinco colores apagados.
            for (k in 0 until 9) {
                val cx = aw * azar(8300, k); val cy = ah * azar(8301, k)
                val giro = 360f * azar(8302, k)
                val col = cols[k % cols.size].copy(alpha = (0.13f + 0.06f * azar(8303, k)) * fuerza)
                val t = 10f * u * (0.8f + 0.4f * azar(8304, k))
                for (dx in floatArrayOf(-aw, 0f, aw)) for (dy in floatArrayOf(-ah, 0f, ah)) {
                    val c = Offset(cx + dx, cy + dy)
                    if (c.x < -t * 2 || c.x > aw + t * 2 || c.y < -t * 2 || c.y > ah + t * 2) continue
                    rotate(giro, c) {
                        val bum = Path().apply {
                            moveTo(c.x - t, c.y + t * 0.3f)
                            quadraticTo(c.x - t * 0.2f, c.y - t * 0.55f, c.x + t * 0.05f, c.y - t * 0.6f)
                            quadraticTo(c.x + t * 0.2f, c.y - t * 0.1f, c.x + t, c.y + t * 0.35f)
                            quadraticTo(c.x + t * 0.1f, c.y - t * 0.15f, c.x - t, c.y + t * 0.3f)
                            close()
                        }
                        drawPath(bum, col)
                    }
                }
            }
            // Las motas.
            for (k in 0 until 40) {
                val c = Offset(aw * azar(8310, k), ah * azar(8311, k))
                drawCircle(cols[(k + 2) % cols.size].copy(alpha = 0.16f * fuerza), (0.6f + 0.8f * azar(8312, k)) * u, c)
            }
        }
        val nuevo = ShaderBrush(ImageShader(img, TileMode.Repeated, TileMode.Repeated))
        claveBaldosa2 = claveBaldosa; pincel2 = pincel
        claveBaldosa = clave; pincel = nuevo
        return nuevo
    }

    /**
     * El fondo de un hueco: la melamina oliva de detrás, con la luz de cada sitio y su dibujo de bumeranes; la cara de dentro de los
     * costados (la de la izquierda a la luz de día, la de la derecha en sombra) y la de abajo de la tabla de arriba; la sombra franca
     * de la tabla y del costado; de noche, el charco de la luz que le toca; y una rozadura.
     */
    fun DrawScope.fondoHueco(k: Int, r: Rect, g: MarcoRefugio) {
        val w = g.w; val u = density
        if (r.width < 2f || r.height < 2f) return
        val xs = List(9) { j -> r.left + r.width * j / 8f }
        val fondoCol = mezcla(P.OLIVA_OSC, P.OLIVA, 0.3f)
        val arriba = xs.map { g.luz(it, r.top + r.height * 0.2f, 0f).c(fondoCol) }
        val abajo = xs.map { g.luz(it, r.top + r.height * 0.78f, 0f).c(fondoCol) }
        drawRect(Brush.horizontalGradient(arriba, r.left, r.right), r.topLeft, Size(r.width, r.height * 0.5f + 1f))
        drawRect(Brush.horizontalGradient(abajo, r.left, r.right), Offset(r.left, r.top + r.height * 0.5f), Size(r.width, r.height * 0.5f))
        // El dibujo de la melamina, corrido en cada hueco para que no se repita igual.
        val b = baldosa(u, g.a)
        val dx = w * 0.37f * k; val dy = w * 0.11f * k
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawContext.transform.translate(dx, dy)
            drawRect(b, Offset(r.left - dx, r.top - dy), r.size)
            drawContext.transform.translate(-dx, -dy)
        }
        val s = 8400 + k * 17
        pinceladas(r, if (g.a.oscuro) g.a.hondo else P.OLIVA_OSC, s, 6, u, 0.12f)
        // La rozadura de algo que se arrastró: dos rayas claras.
        val ry = r.top + r.height * (0.55f + 0.3f * azar(s, 1))
        val rx = r.left + r.width * (0.1f + 0.6f * azar(s, 2))
        val lr = g.luz(rx, ry, 0f)
        drawLine(lr.b(0.22f), Offset(rx, ry), Offset(rx + w * 0.09f, ry - w * 0.006f), 0.8f * u)
        drawLine(lr.b(0.14f), Offset(rx + w * 0.01f, ry + 2f * u), Offset(rx + w * 0.07f, ry + 1.2f * u), 0.7f * u)
        // La cara de dentro de los costados: un trapecio estrecho, más ancho delante, que se cierra hacia el fondo.
        val hondo = 0.024f * w
        val izq = poligonoRefugio(Offset(r.left, r.top), Offset(r.left + hondo, r.top + hondo * 0.8f), Offset(r.left + hondo, r.bottom), Offset(r.left, r.bottom))
        val der = poligonoRefugio(Offset(r.right, r.top), Offset(r.right - hondo, r.top + hondo * 0.8f), Offset(r.right - hondo, r.bottom), Offset(r.right, r.bottom))
        val li = g.luz(r.left + hondo / 2, r.center.y, 0.015f)
        val ld = g.luz(r.right - hondo / 2, r.center.y, 0.015f)
        drawPath(izq, if (g.a.oscuro) mezcla(li.c(P.AGUA), g.a.hondo, 0.3f) else mezcla(li.c(P.AGUA_OSC), P.OLIVA_OSC, 0.25f))
        drawPath(der, if (g.a.oscuro) mezcla(ld.c(P.AGUA), g.a.hondo, 0.3f) else ld.c(mezcla(P.AGUA, P.AGUA_LUZ, 0.2f)))
        drawLine(ld.b(0.35f), Offset(r.right - hondo, r.top + hondo * 0.8f), Offset(r.right - hondo, r.bottom), 1f * u)
        // La cara de abajo de la tabla de arriba (se ve un poco desde abajo).
        drawPath(poligonoRefugio(Offset(r.left, r.top), Offset(r.right, r.top), Offset(r.right - hondo, r.top + hondo * 0.8f), Offset(r.left + hondo, r.top + hondo * 0.8f)),
            mezcla(g.luz(r.center.x, r.top, 0.015f).c(P.AGUA_OSC), honda(g), 0.35f))
        // La sombra franca de la tabla de arriba y la del costado de la izquierda en el fondo.
        val hs = r.height * (if (g.a.oscuro) 0.15f else 0.21f)
        val ws = if (g.a.oscuro) 0f else 0.028f * w
        val sombra = poligonoRefugio(listOf(Offset(r.left + hondo, r.top + hondo * 0.8f), Offset(r.right - hondo, r.top + hondo * 0.8f), Offset(r.right - hondo, r.top + hs),
            Offset(r.left + hondo + ws + hs * 0.4f, r.top + hs), Offset(r.left + hondo + ws, r.top + hs + hs * 0.6f), Offset(r.left + hondo + ws, r.bottom), Offset(r.left + hondo, r.bottom)))
        drawPath(sombra, g.a.sombra(if (g.a.oscuro) 0.3f else 0.32f))
        drawRect(g.a.sombra(if (g.a.oscuro) 0.26f else 0.16f), Offset(r.left, r.top), Size(r.width, hs * 0.35f))
        // De noche, el charco de la luz de cerca en el fondo.
        if (g.a.oscuro) {
            val kc = xs.map { g.luz(it, r.center.y, 0f).k }
            val mx = kc.indices.maxByOrNull { kc[it] } ?: 0
            if (kc[mx] > 0.15f) mancha(Offset(xs[mx], r.center.y), r.height * 0.9f, g.luz(xs[mx], r.center.y, 0f).tinte.copy(alpha = 0.1f * kc[mx]))
        }
        // El contacto de lo que pisa: una línea honda al pie del fondo.
        drawRect(g.a.sombra(0.35f), Offset(r.left, r.bottom - 2f * u), Size(r.width, 2f * u))
    }
}
