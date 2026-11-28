package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.TrazosArcontes
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.sin

/**
 * La roca de la cueva en el estante de Arcontes ([MuebleEstanteArcontes]): la misma caliza del Ideario, con sus estratos
 * ondulados, sus poros y el picado del cincel (pintados una vez en una baldosa que se pega), el techo de roca que avanza arriba,
 * los bordes de la cueva que se tuercen hacia dentro a los lados, el tizne de la lámpara y la grieta con la luz de lo alto. De
 * día, la mancha del sol que entra por la boca (fuera del cuadro, arriba a la izquierda), con el velo de lino a su izquierda; de
 * noche, la luz de la lámpara de encima del armario y lo demás en el azul negro.
 */
internal object RocaEstanteArcontes {
    private val P = PaletaArcontes

    /** Dónde corre la grieta de la pared, en anchos: de lo alto a la cornisa, detrás del bodegón. */
    const val GRIETA_X0 = 0.652f
    const val GRIETA_Y0 = 0.11f
    const val GRIETA_X1 = 0.632f
    const val GRIETA_Y1 = 0.34f

    // ---------------------------------------------------------------- la baldosa de la caliza

    private var densidadBaldosa = -1f
    private var baldosa: ImageBitmap? = null
    /** El ancho y el alto de la baldosa, en dp. */
    private const val BW = 180f
    private const val BH = 140f

    /**
     * La caliza en una baldosa neutra (sombra violeta y luz crema, sin color propio), pintada una vez por densidad: los estratos
     * en bandas onduladas que cierran con la baldosa de al lado, los poros y los parches del picado del cincel.
     */
    private fun baldosaCaliza(u: Float): ImageBitmap {
        val hecha = baldosa
        if (hecha != null && densidadBaldosa == u) return hecha
        val ancho = BW * u; val alto = BH * u
        val img = ImageBitmap(ceil(ancho).toInt().coerceAtLeast(1), ceil(alto).toInt().coerceAtLeast(1))
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(ancho, alto)) {
            val oscura = SOMBRA_MUEBLE
            val clara = LUZ_MUEBLE
            // Los estratos: bandas onduladas que dan la vuelta a la baldosa (una onda entera o dos a lo ancho); como en el Ideario,
            // lo que más se ve de la piedra.
            var y = 4f * u; var k = 0
            while (y < alto && k < 40) {
                val grueso = (3f + 8f * azar(7101, k)) * u
                val fase = azar(7102, k) * 6.28f
                val ondas = 1 + (azar(7103, k) * 2f).toInt()
                val banda = Path()
                val pasos = 24
                for (j in 0..pasos) {
                    val x = ancho * j / pasos
                    val yy = y + 2.2f * u * sin(fase + 2f * PI.toFloat() * ondas * j / pasos)
                    if (j == 0) banda.moveTo(x, yy) else banda.lineTo(x, yy)
                }
                for (j in pasos downTo 0) {
                    val x = ancho * j / pasos
                    banda.lineTo(x, y + grueso + 1.5f * u * sin(fase * 1.3f + 2f * PI.toFloat() * ondas * j / pasos))
                }
                banda.close()
                drawPath(banda, (if (k % 3 == 1) clara else oscura).copy(alpha = 0.045f + 0.05f * azar(7104, k)))
                y += (10f + 22f * azar(7105, k)) * u; k++
            }
            // Los poros: puntos hondos y, alguno, claro (pocos y suaves: muchos y grises agrisaban la piedra).
            val hondos = Path(); val claros = Path()
            for (j in 0 until 150) {
                val p = Offset(ancho * azar(7110, j), alto * azar(7111, j))
                val t = (0.35f + 0.85f * azar(7112, j)) * u
                if (azar(7113, j) > 0.82f) claros.addOval(Rect(p.x - t, p.y - t, p.x + t, p.y + t))
                else hondos.addOval(Rect(p.x - t, p.y - t, p.x + t, p.y + t))
            }
            drawPath(hondos, oscura.copy(alpha = 0.13f))
            drawPath(claros, clara.copy(alpha = 0.35f))
            // El picado del cincel: golpes cortos sueltos, cada uno con su labio claro, de largo e inclinación distintos (nunca en
            // fila: en fila se leen como rayas de contar).
            val golpes = Path(); val labios = Path()
            for (j in 0 until 60) {
                val o = Offset(ancho * azar(7120, j), alto * azar(7121, j))
                val an = 0.7f + 1.4f * azar(7122, j)
                val l = (1.2f + 2.2f * azar(7123, j)) * u
                val dx = kotlin.math.cos(an) * l; val dy = kotlin.math.sin(an) * l
                golpes.moveTo(o.x, o.y); golpes.lineTo(o.x + dx, o.y + dy)
                labios.moveTo(o.x + 0.6f * u, o.y + 0.3f * u); labios.lineTo(o.x + dx + 0.6f * u, o.y + dy + 0.3f * u)
            }
            drawPath(golpes, oscura.copy(alpha = 0.15f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
            drawPath(labios, clara.copy(alpha = 0.2f), style = Stroke(0.45f * u, cap = StrokeCap.Round))
            // Pinceladas que rompen el plano.
            for (j in 0 until 26) {
                val l = (14f + 26f * azar(7130, j)) * u
                val a = l * (0.16f + 0.14f * azar(7131, j))
                val x = ancho * azar(7132, j) - l / 2; val yy = alto * azar(7133, j) - a / 2
                drawOval((if (j % 2 == 0) clara else oscura).copy(alpha = 0.05f + 0.04f * azar(7134, j)), Offset(x, yy), Size(l, a))
            }
        }
        baldosa = img; densidadBaldosa = u
        return img
    }

    /** La textura de la caliza sobre [r], en mosaico; [fuerza] la baja (de noche y en los fondos en sombra). */
    fun DrawScope.textura(r: Rect, fuerza: Float, desfase: Float = 0f) {
        if (r.width < 1f || r.height < 1f) return
        val b = baldosaCaliza(density)
        val a = fuerza.coerceIn(0f, 1f)
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top - (desfase % b.height)
            while (y < r.bottom) {
                var x = r.left - ((desfase * 1.7f) % b.width)
                while (x < r.right) { drawImage(b, Offset(x, y), alpha = a); x += b.width }
                y += b.height
            }
        }
    }

    // ---------------------------------------------------------------- la pared de la corona

    /** El color de la pared en [r] con la luz del momento: de día, de la boca hacia abajo a la derecha; de noche, de la lámpara. */
    private fun DrawScope.fondo(s: SitioArcontes, r: Rect) {
        val w = size.width
        val a = s.a
        if (!a.oscuro) {
            // A lo largo de la diagonal, de la boca (arriba a la izquierda) a lo hondo (abajo a la derecha): la caliza del Ideario,
            // con su luz de fondo; fuera de la mancha (la mancha va encima, como luz).
            val antes = a.forzar
            a.forzar = 0f
            val n = 6
            val cs = List(n) { i ->
                val f = i / (n - 1f)
                s.t(Offset(r.left + r.width * f, r.top + r.height * f)).c(mezclaRapida(P.CALIZA, P.CALIZA_LUZ, 0.3f))
            }
            a.forzar = antes
            drawRect(Brush.linearGradient(cs, r.topLeft, r.bottomRight), r.topLeft, r.size)
        } else {
            // De noche: el halo de la lámpara de encima del armario en la pared y lo lejano hundido en el azul violáceo.
            val l = Offset(a.lampX * w, a.lampY * w)
            val antes = a.forzar
            a.forzar = 0f
            fun k(d: Float) = s.t(l + Offset(-d * w, -d * w * 0.3f)).c(P.CALIZA)
            val brocha = Brush.radialGradient(0f to k(0.01f), 0.08f to k(0.08f), 0.18f to k(0.18f), 0.32f to k(0.32f), 0.5f to k(0.5f), 0.75f to k(0.75f), 1f to k(1.1f),
                center = l + Offset(0f, w * 0.02f), radius = w * 1.1f)
            a.forzar = antes
            drawRect(brocha, r.topLeft, r.size)
        }
    }

    /**
     * La luz de la boca en la pared, encima de la caliza: la misma piedra un paso más clara y más cálida (un velo de luz crema,
     * no un amarillo), con el borde blando por abajo y a la derecha (la penumbra, en pasos que se apagan) y, arriba, cortado por el
     * canto quebrado del techo que avanza (el techo se pinta después). Al ocaso, melocotón; con la luna, plata fría y corta.
     */
    private fun DrawScope.luzDeLaBoca(s: SitioArcontes, r: Rect) {
        val w = size.width
        val a = s.a
        val q = a.parche ?: return
        val (luz, fuerza) = when {
            a.oscuro -> P.LUNA_RAS to 0.2f
            a.noche -> P.OCASO to 0.34f
            else -> Color(0xFFFFF1CC) to 0.62f
        }
        // La penumbra: la mancha agrandada en cinco pasos, cada uno con un poco de luz.
        for (j in 5 downTo 1) {
            drawPath(caminoManchaArcontes(q, w, true, 0.011f * j), luz.copy(alpha = fuerza * 0.11f))
        }
        drawPath(caminoManchaArcontes(q, w, true), luz.copy(alpha = fuerza * 0.45f))
    }

    /** La pared de la corona en [r], de arriba hasta donde pisan las cosas ([apoyo]) y un poco más (la cornisa la cubre). */
    fun DrawScope.pared(s: SitioArcontes, r: Rect, apoyo: Float) {
        val w = size.width; val u = density
        val a = s.a
        fondo(s, r)
        luzDeLaBoca(s, r)
        // El velo de lino: por la izquierda de la mancha el sol pasa por la tela y deja sus pliegues en rayas.
        if (a.parche === AireArcontes.SOL_Q) clipPath(caminoManchaArcontes(AireArcontes.SOL_Q, w, true, 0.03f)) { velo(s, r) }
        // El polvo dorado que flota en el aire de la mancha, poco.
        if (!a.oscuro && !a.noche) clipPath(caminoManchaArcontes(AireArcontes.SOL_Q, w, true)) {
            val motas = Path(); val chicas = Path()
            for (j in 0 until 34) {
                val p = Offset(w * (0.06f + 0.56f * azar(7201, j)), w * (0.08f + 0.55f * azar(7202, j)))
                val t = (0.45f + 0.8f * azar(7203, j) * azar(7204, j)) * u
                (if (j % 3 == 0) motas else chicas).addOval(Rect(p.x - t, p.y - t, p.x + t, p.y + t))
            }
            drawPath(motas, P.SOL.copy(alpha = 0.7f))
            drawPath(chicas, P.ORO.copy(alpha = 0.4f))
        }
        textura(r, if (a.oscuro) 0.45f else 0.7f)
        tizne(s)
        techo(s, r)
        bordes(s, r.top, r.bottom)
        grieta(s)
    }

    /** El velo sobre la pared, dentro de la mancha: la parte de la izquierda, más baja, con las rayas claras y hondas de los pliegues. */
    private fun DrawScope.velo(s: SitioArcontes, r: Rect) {
        val w = size.width
        val antes = s.a.forzar
        s.a.forzar = 0f
        val fuera = s.a.en(0.1f, 0.3f, 0f)
        s.a.forzar = antes
        val x0 = AireArcontes.VELO_X; val dx = AireArcontes.VELO_Y * 0.8f
        val zona = Path().apply {
            moveTo(-0.08f * w, 0f); lineTo(x0 * w, 0f); lineTo((x0 + dx) * w, 0.8f * w); lineTo(-0.08f * w, 0.8f * w); close()
        }
        clipPath(zona) {
            drawRect(fuera.c(P.CALIZA).copy(alpha = 0.36f), r.topLeft, r.size)
            // Los pliegues: pocas bandas anchas y blandas que bajan casi rectas, una más clara y otra más honda.
            var x = -0.08f; var j = 0
            while (x < x0 + dx && j < 12) {
                val ancho = 0.03f + 0.02f * azar(7210, j)
                val p = Path().apply {
                    moveTo(x * w, 0f); lineTo((x + ancho) * w, 0f)
                    lineTo((x + ancho + 0.03f) * w, 0.8f * w); lineTo((x + 0.03f) * w, 0.8f * w); close()
                }
                drawPath(p, if (j % 2 == 0) SOMBRA_MUEBLE.copy(alpha = 0.05f + 0.03f * azar(7211, j)) else LUZ_MUEBLE.copy(alpha = 0.08f + 0.05f * azar(7212, j)))
                x += ancho; j++
            }
        }
        // El borde del velo: un filo más claro donde la tela deja pasar la luz directa.
        val t = s.t(Offset(0.2f * w, 0.3f * w))
        drawLine(t.b(0.3f), Offset(x0 * w, 0f), Offset((x0 + dx) * w, 0.8f * w), 1.2f * density)
    }

    /** El tizne de tantas noches encima de la lámpara de arriba: una mancha blanda y oscura que sube hacia el techo. */
    private fun DrawScope.tizne(s: SitioArcontes) {
        val w = size.width
        val l = Offset(s.a.lampX * w, s.a.lampY * w)
        val al = if (s.a.oscuro) 0.3f else 0.2f
        listOf(Triple(0.045f, 0.035f, 1f), Triple(0.1f, 0.05f, 0.75f), Triple(0.17f, 0.06f, 0.5f)).forEach { (dy, r, f) ->
            val c = Offset(l.x + w * 0.006f * f, l.y - w * dy)
            drawCircle(Brush.radialGradient(listOf(P.TINTA.copy(alpha = al * f), P.TINTA.copy(alpha = 0f)), c, w * r), w * r, c)
        }
    }

    /** El techo de la cueva: la roca de arriba que avanza, con su canto y la sombra que echa en la pared. */
    private fun DrawScope.techo(s: SitioArcontes, r: Rect) {
        val w = size.width; val u = density
        val borde = ArrayList<Offset>()
        for (k in 0..18) borde.add(Offset(w * k / 18f, w * (0.03f + 0.035f * azar(7301, k) + (if (k in 5..8) 0.012f else 0f))))
        val roca = Path().apply {
            moveTo(-1f, -1f); lineTo(w + 1f, -1f)
            for (k in borde.indices.reversed()) lineTo(borde[k].x, borde[k].y)
            close()
        }
        // La sombra en la pared, debajo del canto: blanda, en tres pasos que se van apagando (corta la mancha del sol con el canto
        // quebrado del techo).
        val antes = s.a.forzar
        s.a.forzar = 0f
        val t0 = s.t(Offset(w * 0.3f, w * 0.04f))
        listOf(0.034f to 0.1f, 0.022f to 0.12f, 0.011f to 0.16f).forEach { (largo, al) ->
            val sombra = Path().apply {
                moveTo(borde[0].x, borde[0].y)
                for (p in borde) lineTo(p.x + 3f * u, p.y + w * largo)
                for (k in borde.indices.reversed()) lineTo(borde[k].x, borde[k].y)
                close()
            }
            drawPath(sombra, t0.s(al))
        }
        val cs = List(5) { i -> s.t(Offset(w * i / 4f, w * 0.02f)).c(mezclaRapida(P.CALIZA_SOMBRA, P.ROCA, 0.45f)) }
        s.a.forzar = antes
        drawPath(roca, Brush.horizontalGradient(cs, 0f, w))
        clipPath(roca) { textura(Rect(0f, 0f, w, w * 0.08f), if (s.a.oscuro) 0.5f else 0.9f, 37f) }
        val canto = Path().apply { borde.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) } }
        translate(0f, 1.2f * u) { drawPath(canto, t0.s(0.35f), style = Stroke(1.4f * u)) }
    }

    /**
     * Los bordes de la cueva a los dos lados, de [y0] a [y1]: la roca se tuerce hacia dentro y se hunde en la sombra (como en el
     * Ideario). Los usan la corona y el cuerpo (por fuera de cada costado).
     */
    fun DrawScope.bordes(s: SitioArcontes, y0: Float, y1: Float, lados: List<Boolean> = listOf(false, true)) {
        val w = size.width; val u = density
        val ancho = w * MuebleEstanteArcontes.ROCA_LADO
        lados.forEach { der ->
            val x0 = if (der) w else 0f
            val p = Path()
            p.moveTo(x0, y0)
            val paso = maxOf(4f * u, w * 0.03f)
            var y = y0; var k = 0
            while (y <= y1 + paso && k < 400) {
                val e = ancho * (0.55f + 0.9f * azar(if (der) 7313 else 7311, k + (y0 / paso).toInt()))
                p.lineTo(if (der) w - e else e, minOf(y, y1))
                y += paso; k++
            }
            p.lineTo(x0, y1); p.close()
            val t = s.t(Offset(if (der) w - ancho else ancho, (y0 + y1) / 2))
            drawPath(p, s.aLoAlto(y0, y1, if (der) w - ancho else ancho) { it.c(mezclaRapida(P.CALIZA_SOMBRA, P.ROCA, 0.6f), -0.1f) })
            drawPath(p, (if (der) t.s(0.3f) else t.b(0.3f)), style = Stroke(0.8f * u))
        }
    }

    /** La grieta de la pared: una raja fina con la luz blanca y violeta, que se abre más de noche y en los Sueños. */
    private fun DrawScope.grieta(s: SitioArcontes) {
        val w = size.width; val u = density
        val luz = when {
            s.a.oscuro && s.a.noche -> 1f
            s.a.oscuro -> 0.85f
            s.a.noche -> 0.6f
            else -> 0.42f
        }
        // La roca alrededor, un poco hundida: el labio en sombra de un lado y en luz del otro.
        val a = Offset(w * GRIETA_X0, w * GRIETA_Y0); val b = Offset(w * GRIETA_X1, w * GRIETA_Y1)
        val t = s.t(Offset((a.x + b.x) / 2, (a.y + b.y) / 2))
        drawLine(t.s(0.3f), a + Offset(-1.6f * u, 0f), b + Offset(-1.6f * u, 0f), 2.2f * u, StrokeCap.Round)
        drawLine(t.b(0.3f), a + Offset(1.8f * u, 0.5f * u), b + Offset(1.8f * u, 0.5f * u), 0.9f * u, StrokeCap.Round)
        with(TrazosArcontes) { grieta(a, b, 1.05f * u, luz, 23) }
        // De noche, el violeta tiñe un poco la roca de alrededor.
        if (s.a.oscuro) charcoArcontes(Offset((a.x + b.x) / 2, (a.y + b.y) / 2), w * 0.16f, P.GRIETA_HALO, if (s.a.noche) 0.55f else 0.4f)
    }

    /** La luz de la lámpara de arriba sobre lo que tiene cerca, de noche: el aire caliente alrededor de la llama. */
    fun DrawScope.lumbreArriba(s: SitioArcontes, ch: Float) {
        if (!s.a.oscuro) return
        val w = size.width
        val l = Offset(s.a.lampX * w, s.a.lampY * w)
        val f = if (s.a.noche) 0.5f else 1f
        val e = MuebleEstanteArcontes.escala(w, density)
        charcoArcontes(l, w * e * 0.38f, P.LLAMA_HALO, 0.6f * f)
        charcoArcontes(l, w * e * 0.12f, P.LLAMA_HALO, 0.9f * f)
    }

    // ---------------------------------------------------------------- el fondo de cada hueco

    /**
     * La roca del fondo de un hueco [r] (el armario no tiene fondo): la misma caliza crema de la pared, que se hunde solo por la
     * sombra del armario: la sombra franca de la tabla de arriba y su penumbra, la del costado de la izquierda (la luz viene de la
     * boca, arriba a la izquierda), y todo un poco más hondo que la pared de fuera; con su textura y, en la primera repisa, la
     * grieta que vuelve a asomar.
     */
    fun DrawScope.fondoHueco(k: Int, r: Rect, s: SitioArcontes, techo: Float, oc: Float) {
        val u = density; val w = size.width
        val fr = Rect(r.left - 1f, r.top - 1f, r.right + 1f, r.bottom + 1f)
        val roca = mezclaRapida(P.CALIZA, P.CALIZA_LUZ, 0.3f)
        val y1 = fr.top + techo + r.height * 0.2f + 1f; val y2 = fr.top + techo + r.height * 0.34f + 1f
        val lado = w * 0.05f
        if (!s.a.lumbre) {
            val antes = s.a.forzar
            s.a.forzar = 0f
            val tc = s.t(r.center)
            s.a.forzar = antes
            val c = tc.c(roca, -0.1f)
            val sombra = mezclaRapida(c, SOMBRA_MUEBLE, 0.4f)
            val y3 = fr.top + fr.height * 0.86f
            // Debajo, todo el fondo de un color opaco: las bandas se tocan a medio píxel y, si no, asomaría lo de detrás.
            drawRect(c, fr.topLeft, fr.size)
            // La sombra franca de la tabla de arriba y su penumbra; la del costado de la izquierda y la suya.
            drawRect(sombra, Offset(fr.left, fr.top), Size(fr.width, y1 - fr.top))
            drawRect(mezclaRapida(c, sombra, 0.45f), Offset(fr.left, y1), Size(fr.width, y2 - y1))
            drawRect(sombra, Offset(fr.left, y1), Size(lado, fr.bottom - y1))
            drawRect(mezclaRapida(c, sombra, 0.45f), Offset(fr.left + lado, y2), Size(lado * 0.6f, fr.bottom - y2))
            // Abajo, la luz que rebota en la tabla.
            drawRect(mezclaRapida(c, LUZ_MUEBLE, 0.08f), Offset(fr.left + lado * 1.6f, y3), Size(fr.width - lado * 1.6f, fr.bottom - y3))
        } else {
            // Detrás de las entradas solo asoman las rendijas: ahí va liso; arriba y en el hueco libre, con la luz que cambia.
            val xo = oc.coerceIn(fr.left, fr.right)
            val yo = r.top + r.height * 0.34f
            drawRect(s.t(Offset((fr.left + xo) / 2, r.center.y)).c(roca, -0.1f), fr.topLeft, fr.size)
            drawRect(s.aLoAncho(fr.left, fr.right, r.center.y + r.height * 0.15f) { it.c(roca, -0.1f) }, fr.topLeft, Size(fr.width, yo - fr.top))
            if (fr.right > xo) drawRect(s.aLoAncho(xo, fr.right, r.center.y + r.height * 0.15f) { it.c(roca, -0.1f) }, Offset(xo, yo), Size(fr.right - xo, fr.bottom - yo))
            val tc = s.t(r.center)
            drawRect(tc.s(0.5f), Offset(r.left, r.top), Size(r.width, techo + r.height * 0.2f))
            drawRect(tc.s(0.22f), Offset(r.left, r.top + techo + r.height * 0.2f), Size(r.width, r.height * 0.14f))
            drawRect(tc.s(0.35f), Offset(r.left, y1), Size(lado, r.bottom - y1))
        }
        val fz = if (s.a.oscuro) 0.3f else 0.6f
        val x = oc.coerceIn(r.left, r.right)
        textura(Rect(r.left, r.top, x, r.top + r.height * 0.26f), fz, 53f * k + 11f)
        textura(Rect(x, r.top, r.right, r.bottom), fz, 53f * k + 11f)
        // Una mancha de humedad o una lasca saltada, distinta en cada hueco.
        val dc = Offset(r.left + r.width * (0.15f + 0.7f * azar(7401, k)), r.top + r.height * (0.45f + 0.25f * azar(7402, k)))
        val td = s.t(dc)
        val pts = Array(6) { j ->
            val an = j * 1.047f
            val e = w * (0.028f + 0.02f * azar(7403 + k, j))
            Offset(dc.x + e * kotlin.math.cos(an) * 1.4f, dc.y + e * kotlin.math.sin(an) * 0.7f)
        }
        val forma = Path().apply {
            val m0 = Offset((pts[5].x + pts[0].x) / 2, (pts[5].y + pts[0].y) / 2)
            moveTo(m0.x, m0.y)
            for (j in 0 until 6) { val a0 = pts[j]; val b0 = pts[(j + 1) % 6]; quadraticTo(a0.x, a0.y, (a0.x + b0.x) / 2, (a0.y + b0.y) / 2) }
            close()
        }
        // La humedad: una mancha blanda, más honda en su centro, y unos poros saltados alrededor.
        drawPath(forma, td.s(0.1f))
        translate(w * 0.006f, w * 0.004f) { drawPath(forma, td.s(0.06f)) }
        for (j in 0 until 5) {
            val p = Offset(dc.x + w * (azar(7405 + k, j) - 0.5f) * 0.12f, dc.y + w * (azar(7406 + k, j) - 0.5f) * 0.05f)
            drawCircle(td.s(0.3f), (0.7f + 0.8f * azar(7407 + k, j)) * u, p)
            drawCircle(td.b(0.25f), 0.5f * u, p + Offset(0.6f * u, 0.6f * u))
        }
        // La grieta que corre por detrás del armario vuelve a asomar en el fondo de la primera repisa.
        if (k == 0) {
            val x = w * (RocaEstanteArcontes.GRIETA_X1 - 0.012f)
            val g0 = Offset(x, r.top + techo * 0.6f); val g1 = Offset(x - w * 0.01f, r.top + r.height * 0.52f)
            drawLine(td.s(0.35f), g0 + Offset(-1.4f * u, 0f), g1 + Offset(-1.4f * u, 0f), 1.8f * u, StrokeCap.Round)
            with(TrazosArcontes) { grieta(g0, g1, 0.85f * u, if (s.a.oscuro) 0.9f else 0.5f, 29) }
        }
    }
}
