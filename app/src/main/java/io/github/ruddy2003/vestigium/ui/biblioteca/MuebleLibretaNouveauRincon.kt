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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import io.github.ruddy2003.vestigium.ui.TrazosNouveau
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sin

/**
 * Dónde va cada cosa del rincón, según el tamaño: la pared con su friso, el biombo de la derecha (más angosto en un teléfono
 * chico), la mesa, el atril con el carnet, la lámpara que cuelga a la izquierda, la caja de colores, el vaso y el florero.
 * El carnet se arrima al biombo y la lámpara se corre a la izquierda (hasta salirse un poco) para que el carnet siga grande.
 */
internal class PlanoLibretaNouveau(val w: Float, val h: Float, val u: Float) {
    val friso = h * 0.082f
    val moldura = h * 0.1f
    val zocalo = h * 0.852f
    val suelo = h * 0.885f
    val anchoBiombo = minOf(h * 0.28f, w * 0.2f)
    val xBiombo = w - anchoBiombo
    /** El carnet: su tapa (apaisada, de 7 a 10), el lomo de tela y el rótulo de papel. */
    val cw = (w - anchoBiombo - h * 0.42f).coerceIn(h * 0.56f, h * 0.74f)
    val ch = cw * 0.7f
    val gruesoMax = h * 0.075f
    val apoyo = h * 0.665f
    val cx = xBiombo - h * 0.012f - gruesoMax * 0.72f - cw / 2f
    val tapa = Rect(cx - cw / 2f, apoyo - ch, cx + cw / 2f, apoyo)
    val lomo = Rect(tapa.left, tapa.top, tapa.left + cw * 0.14f, tapa.bottom)
    val etiqueta: Rect = run {
        val ancho = (tapa.right - lomo.right) * 0.8f
        val alto = (ch * 0.38f).coerceIn(minOf(52f * u, ch * 0.52f), ch * 0.52f)
        val x0 = (lomo.right + tapa.right) / 2f - ancho / 2f
        val y0 = tapa.top + ch * 0.15f
        Rect(x0, y0, x0 + ancho, y0 + alto)
    }
    /** La lámpara colgada: la pantalla de vidrio y el foco (debajo, donde está la bombilla). */
    val xLampara = minOf(h * 0.21f, tapa.left - h * 0.12f)
    val pantalla = Rect(xLampara - h * 0.1f, h * 0.208f, xLampara + h * 0.1f, h * 0.345f)
    val foco = Offset(xLampara, h * 0.33f)
    /** La mesa: el fondo y el frente del tablero, el canto, el faldón y el pie de las patas. */
    val mesaFondo = h * 0.703f
    val mesa = h * 0.735f
    val canto = h * 0.764f
    val faldon = h * 0.826f
    val pie = h * 0.972f
    val mesaIzq = w * 0.025f
    val mesaDer = xBiombo + h * 0.075f
    /** El atril: la cima de las patas, la repisa donde se apoya el carnet y el borde de arriba de su listón. */
    val cima = Offset(cx, tapa.top - h * 0.095f)
    val repisa = Rect(tapa.left - h * 0.024f, apoyo - h * 0.006f, tapa.right + h * 0.022f, apoyo + h * 0.017f)
    val labio = apoyo - h * 0.012f
    /** La caja de acuarelas (bajo la lámpara), el vaso del agua (si cabe) y el pie del florero. */
    val caja: Rect = run {
        val ancho = h * 0.18f
        val c = minOf(xLampara, tapa.left - h * 0.035f - ancho / 2f)
        Rect(c - ancho / 2f, mesa - h * 0.052f, c + ancho / 2f, mesa - h * 0.006f)
    }
    val vaso: Rect? = run {
        val ancho = h * 0.046f
        val x1 = caja.left - h * 0.016f
        if (x1 - ancho < mesaIzq + h * 0.012f) null else Rect(x1 - ancho, mesa - h * 0.088f, x1, mesa - h * 0.008f)
    }
    val florero = Offset(caja.right - h * 0.03f, mesaFondo + h * 0.012f)

    /** Cuánto alumbra la lámpara en [q] (0 a 1): mucho debajo de ella, poco por encima de la pantalla y casi nada lejos. */
    fun k(q: Offset): Float {
        val d = (q - foco).getDistance() / h
        var x = (1.25f - 1.45f * d).coerceIn(0f, 1f)
        if (q.y < foco.y - h * 0.04f) x *= 0.2f + 0.8f * ((q.y - (foco.y - h * 0.3f)) / (h * 0.26f)).coerceIn(0f, 1f)
        return x
    }

    /** Cuánto llega la luna que pasa por la seda del biombo (0 a 1): todo en el biombo, nada lejos de él. */
    fun f(q: Offset): Float {
        val x = if (q.x >= xBiombo) 1f else (1f - (xBiombo - q.x) / (h * 0.75f)).coerceIn(0f, 1f)
        return x * x * (0.55f + 0.45f * (1f - abs(q.y - h * 0.5f) / (h * 0.6f)).coerceIn(0f, 1f))
    }

    /** La luz del día (0 a 1): más cerca de la ventana de la derecha, más clara; abajo y a la izquierda, más honda. */
    fun d(q: Offset): Float = (0.36f + 0.5f * (q.x / w) + 0.12f * (1f - q.y / h)).coerceIn(0f, 1f)
}

/** Pinta en [r] el color que da [color] en cada punto, como una imagen chica estirada: liso, sin escalones (la luz pinta). */
internal fun DrawScope.campoLibretaNouveau(r: Rect, cols: Int, filas: Int, color: (Offset) -> Color) {
    if (r.width < 1f || r.height < 1f) return
    val img = ImageBitmap(cols, filas)
    CanvasDrawScope().draw(Density(1f), layoutDirection, Canvas(img), Size(cols.toFloat(), filas.toFloat())) {
        for (j in 0 until filas) for (i in 0 until cols) {
            val q = Offset(r.left + r.width * (i + 0.5f) / cols, r.top + r.height * (j + 0.5f) / filas)
            drawRect(color(q).copy(alpha = 1f), Offset(i.toFloat(), j.toFloat()), Size(1f, 1f))
        }
    }
    drawImage(img, IntOffset.Zero, IntSize(cols, filas), IntOffset(floor(r.left).toInt(), floor(r.top).toInt()),
        IntSize(ceil(r.right).toInt() - floor(r.left).toInt(), ceil(r.bottom).toInt() - floor(r.top).toInt()))
}

/** Una línea horizontal de [x0] a [x1] en [y], en tramos que toman cada uno su color de [color] (la luz cambia a lo ancho). */
internal fun DrawScope.tramosLibretaNouveau(x0: Float, x1: Float, y: Float, grueso: Float, n: Int, color: (Offset) -> Color) {
    for (i in 0 until n) {
        val a = x0 + (x1 - x0) * i / n; val b = x0 + (x1 - x0) * (i + 1) / n
        drawLine(color(Offset((a + b) / 2f, y)), Offset(a, y), Offset(b + 0.5f, y), grueso)
    }
}

/**
 * El rincón del carnet de bocetos de Art nouveau ([MuebleLibretaNouveau]): la pared con su friso y su estarcido, el parqué, la
 * luz de la lámpara en la pared, el biombo de seda y la mesa de nogal (lo de encima de la mesa está en [PiezasLibretaNouveau]).
 * Todo se pinta una sola vez, dentro de la imagen guardada, con la luz del sitio donde está.
 */
internal object RinconLibretaNouveau {
    private val P = PaletaLibretaNouveau

    // ---------------------------------------------------------------- la pared y el suelo

    /**
     * La pared: el revoque rosa viejo con sus pinceladas; arriba, el friso estarcido (un tallo que ondula, con hojas de sauce y
     * bayas de tres en tres, entre dos filetes de ocre) y la moldura de nogal de colgar cuadros; abajo, el rodapié.
     */
    fun DrawScope.muro(pl: PlanoLibretaNouveau, a: AireLibretaNouveau) {
        val u = pl.u; val w = pl.w; val h = pl.h
        val pared = Rect(0f, pl.moldura, w, pl.zocalo)
        campoLibretaNouveau(pared, 40, 22) { q -> a.luz(P.ROSA, pl.k(q), pl.f(q), pl.d(q)) }
        if (!a.oscuro) pinceladas(pared, P.ROSA_LUZ, 821, 34, u, 0.14f)
        pinceladas(pared, a.sombra(1f), 822, 28, u, if (a.oscuro) 0.025f else 0.04f)
        pinceladas(pared, if (a.oscuro) P.HONDO else P.ROSA_OSC, 823, 18, u, if (a.oscuro) 0.025f else 0.06f)
        // El estarcido de la pared, «a semis»: una hoja de sauce suelta con su rabillo, en tresbolillo, ladeada a un lado y al
        // otro en filas alternas, un tono más honda que el rosa (una hoja sola no se lee como letra ni como bicho).
        val paso = h * 0.1f
        var fila = 0
        var y = pared.top + paso * 0.6f
        while (y < pared.bottom - paso * 0.3f) {
            var x = if (fila % 2 == 0) paso * 0.3f else paso * 0.8f
            val lado = if (fila % 2 == 0) 1f else -1f
            while (x < w) {
                val q = Offset(x, y)
                val col = a.luz(P.ROSA_OSC, pl.k(q), pl.f(q), pl.d(q)).copy(alpha = 0.3f)
                val base = q + Offset(-lado * h * 0.008f, h * 0.012f)
                val punta = q + Offset(lado * h * 0.011f, -h * 0.014f)
                drawLine(col, base, base + Offset(-lado * h * 0.004f, h * 0.006f), 0.9f * u, StrokeCap.Round)
                drawPath(TrazosNouveau.hojaLanza(base, punta, h * 0.006f, 0.16f * lado), col)
                x += paso
            }
            y += paso * 0.85f; fila++
        }
        // El friso estarcido.
        val fr = Rect(0f, 0f, w, pl.friso)
        campoLibretaNouveau(fr, 40, 3) { q -> a.luz(P.FRISO, pl.k(q), pl.f(q), pl.d(q)) }
        pinceladas(fr, a.sombra(1f), 824, 12, u, 0.08f)
        val oro = { q: Offset -> a.luz(P.ORO, pl.k(q), pl.f(q), pl.d(q)) }
        tramosLibretaNouveau(0f, w, pl.friso * 0.1f, 1.1f * u, 12, oro)
        tramosLibretaNouveau(0f, w, pl.friso * 0.9f, 1.1f * u, 12, oro)
        val lam = h * 0.17f; val amp = pl.friso * 0.17f; val y0 = pl.friso * 0.52f
        var x = -lam
        while (x < w + lam) {
            val q = Offset(x + lam / 2f, y0)
            val tallo = a.luz(P.FILETE, pl.k(q), pl.f(q), pl.d(q))
            val hoja = a.luz(P.TALLO, pl.k(q), pl.f(q), pl.d(q))
            val baya = a.luz(P.ORO, pl.k(q), pl.f(q), pl.d(q))
            val p = Path()
            for (j in 0..14) {
                val xx = x + lam * j / 14f
                val yy = y0 + amp * sin(2f * PI.toFloat() * xx / lam)
                if (j == 0) p.moveTo(xx, yy) else p.lineTo(xx, yy)
            }
            drawPath(p, tallo, style = Stroke(1.3f * u, cap = StrokeCap.Round))
            // En la cresta, una hoja que sube; en el valle, una que baja; entre las dos, tres bayas.
            val cresta = Offset(x + lam * 0.75f, y0 - amp)
            val valle = Offset(x + lam * 0.25f, y0 + amp)
            drawPath(TrazosNouveau.hojaLanza(cresta, cresta + Offset(lam * 0.2f, -pl.friso * 0.26f), pl.friso * 0.09f, 0.12f), hoja)
            drawPath(TrazosNouveau.hojaLanza(valle, valle + Offset(-lam * 0.2f, pl.friso * 0.26f), pl.friso * 0.09f, 0.12f), hoja)
            val bq = Offset(x + lam * 0.5f, y0 - pl.friso * 0.2f)
            for (b in 0 until 3) drawCircle(baya, pl.friso * 0.055f, bq + Offset((b - 1) * pl.friso * 0.11f, if (b == 1) -pl.friso * 0.08f else 0f))
            x += lam
        }
        // La moldura de colgar cuadros: su filete de luz arriba, la canal y la sombra que echa en la pared.
        val mo = Rect(0f, pl.friso, w, pl.moldura)
        campoLibretaNouveau(mo, 24, 2) { q -> a.luz(P.NOGAL, pl.k(q), pl.f(q), pl.d(q)) }
        tramosLibretaNouveau(0f, w, mo.top + 0.8f * u, 1.4f * u, 10) { q -> if (a.oscuro) a.luz(P.NOGAL_LUZ, pl.k(q) * 1.2f, pl.f(q)) else a.luz(P.NOGAL_LUZ, 0f, 0f, pl.d(q) + 0.2f) }
        drawLine(a.sombra(0.4f), Offset(0f, mo.center.y + 1f * u), Offset(w, mo.center.y + 1f * u), 1f * u)
        drawRect(a.sombra(0.26f), Offset(0f, mo.bottom), Size(w, 2.5f * u))
        drawRect(a.sombra(0.1f), Offset(0f, mo.bottom + 2.5f * u), Size(w, 4f * u))
        // El rodapié.
        val ro = Rect(0f, pl.zocalo, w, pl.suelo)
        campoLibretaNouveau(ro, 24, 2) { q -> a.luz(P.NOGAL_OSC, pl.k(q) * 0.7f, pl.f(q), pl.d(q)) }
        tramosLibretaNouveau(0f, w, ro.top + 0.7f * u, 1.2f * u, 10) { q -> if (a.oscuro) a.luz(P.NOGAL, pl.k(q), pl.f(q)) else a.luz(P.NOGAL_LUZ, 0f, 0f, pl.d(q)) }
        drawRect(a.sombra(0.2f), Offset(0f, ro.top - 3f * u), Size(w, 3f * u))
    }

    /** El parqué encerado: las tablas que vienen hacia uno, sus juntas, y el brillo de la cera hacia la ventana (o la luna). */
    fun DrawScope.suelo(pl: PlanoLibretaNouveau, a: AireLibretaNouveau) {
        val u = pl.u; val w = pl.w; val h = pl.h
        val r = Rect(0f, pl.suelo, w, h)
        campoLibretaNouveau(r, 30, 4) { q -> a.luz(P.PARQUE, pl.k(q) * 0.6f, pl.f(q) * 1.2f, pl.d(q) + 0.06f) }
        val fuga = Offset(w * 0.5f, pl.suelo - h * 1.6f)
        val juntas = Path()
        val tabla = h * 0.07f
        var i = -(w / tabla).toInt() - 2
        while (i <= (w / tabla).toInt() + 2) {
            val x = w * 0.5f + i * tabla
            val xb = fuga.x + (x - fuga.x) * (h - fuga.y) / (pl.suelo - fuga.y)
            juntas.moveTo(x, pl.suelo); juntas.lineTo(xb, h)
            // Una junta de testa, a una altura distinta en cada tabla.
            val f = 0.25f + 0.6f * azar(931, i + 40)
            val y = pl.suelo + (h - pl.suelo) * f
            val xa = fuga.x + (x - fuga.x) * (y - fuga.y) / (pl.suelo - fuga.y)
            val xn = fuga.x + (x + tabla - fuga.x) * (y - fuga.y) / (pl.suelo - fuga.y)
            juntas.moveTo(xa, y); juntas.lineTo(xn, y)
            i++
        }
        drawPath(juntas, a.sombra(0.42f), style = Stroke(0.7f * u))
        pinceladas(r, if (a.oscuro) P.HONDO else P.NOGAL_LUZ, 932, 14, u, 0.1f)
        drawRect(a.sombra(0.3f), Offset(0f, pl.suelo), Size(w, 2f * u))
    }

    /**
     * La luz de la lámpara en la pared, de noche: el resplandor del vidrio alrededor de la pantalla. De día, la sombra que la
     * pantalla y la vara echan en la pared, a la izquierda (la ventana está a la derecha).
     */
    fun DrawScope.luzDeLampara(pl: PlanoLibretaNouveau, a: AireLibretaNouveau) {
        val h = pl.h; val u = pl.u
        val c = pl.pantalla.center
        if (a.oscuro) {
            mancha(c, h * 0.36f, P.LAMPARA.copy(alpha = 0.22f))
            mancha(c + Offset(0f, h * 0.04f), h * 0.18f, P.LLAMA.copy(alpha = 0.16f))
        } else {
            // La lámpara cuelga lejos de la pared: su sombra cae corrida, ancha y tenue.
            val s = Offset(-h * 0.06f, h * 0.05f)
            drawLine(a.sombra(0.07f), Offset(pl.xLampara + s.x, pl.moldura), Offset(pl.xLampara + s.x, pl.pantalla.top + s.y), 2.6f * u)
            mancha(Offset(pl.pantalla.center.x + s.x, pl.pantalla.center.y + s.y), pl.pantalla.width * 0.62f, a.sombra(0.1f))
        }
    }

    // ---------------------------------------------------------------- el biombo

    /**
     * El biombo de nogal delante de la ventana, de tres hojas (la tercera, cortada por el borde) en zigzag: las aristas de
     * delante bajan y suben más que las de atrás. Cada hoja, su bastidor con la cresta arqueada y su capullo tallado, la seda
     * plisada y fruncida en sus varillas (de día brilla con la ventana detrás; de noche, fría, con la luna), el bordado de un
     * tallo con hojas y un capullo (que a contraluz se ve oscuro), el tablero bajo tallado y las patitas.
     */
    fun DrawScope.biombo(pl: PlanoLibretaNouveau, a: AireLibretaNouveau) {
        val h = pl.h
        val anchos = floatArrayOf(0.1f, 0.13f, 0.12f, 0.13f)
        val bordes = ArrayList<Float>()
        var x = pl.xBiombo; bordes.add(x)
        var i = 0
        while (x < pl.w && i < anchos.size) { x += anchos[i] * h; bordes.add(x); i++ }
        for (j in 0 until bordes.size - 1) hojaBiombo(pl, a, j, bordes[j], bordes[j + 1])
    }

    private fun DrawScope.hojaBiombo(pl: PlanoLibretaNouveau, a: AireLibretaNouveau, j: Int, x0: Float, x1: Float) {
        val u = pl.u; val h = pl.h
        fun arriba(e: Int) = if (e % 2 == 1) h * 0.04f else h * 0.068f
        fun abajo(e: Int) = if (e % 2 == 1) h * 0.955f else h * 0.925f
        val t0 = arriba(j); val t1 = arriba(j + 1); val b0 = abajo(j); val b1 = abajo(j + 1)
        val arco = h * 0.024f
        fun xs(s: Float) = x0 + (x1 - x0) * s
        fun y(s: Float, v: Float): Float {
            val top = t0 + (t1 - t0) * s - arco * 4f * s * (1f - s)
            val bot = b0 + (b1 - b0) * s
            return top + (bot - top) * v
        }
        fun pt(s: Float, v: Float) = Offset(xs(s), y(s, v))
        val aDerecha = j % 2 == 1
        val c = pt(0.5f, 0.45f)
        val kc = pl.k(c); val fc = pl.f(c)
        val madera = when {
            !a.oscuro -> a.luz(P.NOGAL, 0f, 0f, if (aDerecha) 0.78f else 0.4f)
            aDerecha -> a.luz(P.NOGAL, kc * 0.5f, 0.9f)
            else -> a.luz(P.NOGAL, kc * 1.15f, fc * 0.4f)
        }
        // La hoja entera: el bastidor.
        val hoja = Path()
        for (m in 0..12) { val q = pt(m / 12f, 0f); if (m == 0) hoja.moveTo(q.x, q.y) else hoja.lineTo(q.x, q.y) }
        hoja.lineTo(x1, b1); hoja.lineTo(x0, b0); hoja.close()
        drawPath(hoja, madera)
        // La seda, entre los largueros y entre la cresta y el travesaño.
        val ss = (h * 0.016f / (x1 - x0)).coerceAtMost(0.2f)
        val v0 = 0.055f; val v1 = 0.76f
        val seda = Path()
        for (m in 0..10) { val q = pt(ss + (1f - 2f * ss) * m / 10f, v0); if (m == 0) seda.moveTo(q.x, q.y) else seda.lineTo(q.x, q.y) }
        for (m in 10 downTo 0) { val q = pt(ss + (1f - 2f * ss) * m / 10f, v1); seda.lineTo(q.x, q.y) }
        seda.close()
        val caja = Rect(x0, y(0.5f, 0f) - arco, x1, maxOf(y(0f, v1), y(1f, v1)))
        clipPath(seda) {
            campoLibretaNouveau(caja, 6, 14) { q ->
                val v = ((q.y - y(0.5f, v0)) / (y(0.5f, v1) - y(0.5f, v0))).coerceIn(0f, 1f)
                val brillo = (1f - abs(v - 0.42f) / 0.5f).coerceIn(0f, 1f) * (if (aDerecha) 1f else 0.78f)
                if (!a.oscuro) mezcla(P.SEDA, P.SEDA_LUZ, 0.25f + 0.65f * brillo)
                else {
                    val luna = mezcla(P.HONDO_FRIO, mezcla(P.LUNA_RAS, P.SEDA, 0.3f), (0.3f + 0.4f * brillo) * (if (aDerecha) 1f else 0.75f))
                    mezcla(luna, a.luz(P.SEDA, pl.k(q)), (pl.k(q) * (if (aDerecha) 0.3f else 0.8f)).coerceIn(0f, 1f))
                }
            }
            // Los pliegues: una línea honda y, al lado, una clara.
            val paso = h * 0.011f
            val pliegues = (((x1 - x0) * (1f - 2f * ss)) / paso).toInt().coerceAtLeast(2)
            val hondos = Path(); val claros = Path()
            for (m in 0..pliegues) {
                val s = ss + (1f - 2f * ss) * m / pliegues
                val s2 = (s + (1f - 2f * ss) * 0.4f / pliegues).coerceAtMost(1f - ss)
                hondos.moveTo(xs(s), y(s, v0)); hondos.lineTo(xs(s), y(s, v1))
                claros.moveTo(xs(s2), y(s2, v0)); claros.lineTo(xs(s2), y(s2, v1))
            }
            drawPath(hondos, if (a.oscuro) P.HONDO_FRIO.copy(alpha = 0.45f) else P.SEDA_OSC.copy(alpha = 0.5f), style = Stroke(1.3f * u))
            drawPath(claros, if (a.oscuro) P.LUNA.copy(alpha = 0.12f * (if (aDerecha) 1f else 0.6f)) else Color.White.copy(alpha = 0.3f), style = Stroke(0.9f * u))
            // El bordado, en las hojas de en medio: a contraluz se lee como silueta.
            if (j == 1 || j == 2) bordado(pl, a, ::pt, j == 2, v0, v1)
            // El frunce de arriba y de abajo.
            val frunce = Path()
            for (m in 0 until pliegues) {
                val sa = ss + (1f - 2f * ss) * m / pliegues; val sb = ss + (1f - 2f * ss) * (m + 1) / pliegues
                for (v in floatArrayOf(v0 + 0.012f, v1 - 0.012f)) {
                    val qa = pt(sa, v); val qb = pt(sb, v)
                    frunce.moveTo(qa.x, qa.y); frunce.quadraticTo((qa.x + qb.x) / 2f, (qa.y + qb.y) / 2f + (if (v < 0.5f) 2.2f else -2.2f) * u, qb.x, qb.y)
                }
            }
            drawPath(frunce, a.sombra(0.35f), style = Stroke(0.8f * u))
        }
        // Las varillas de latón de la seda.
        val laton = a.luz(P.BRONCE_LUZ, kc, fc, 0.8f)
        for (v in floatArrayOf(v0, v1)) drawLine(laton, pt(ss, v), pt(1f - ss, v), 1.1f * u)
        // La cresta: su filete de luz siguiendo el arco y el capullo tallado en medio.
        val cresta = Path()
        for (m in 0..12) { val q = pt(m / 12f, 0f) + Offset(0f, 1f * u); if (m == 0) cresta.moveTo(q.x, q.y) else cresta.lineTo(q.x, q.y) }
        drawPath(cresta, if (a.oscuro) (if (aDerecha) a.fria(0.5f, 1f) else a.filo(0.6f, kc)) else a.claro(if (aDerecha) 0.55f else 0.25f), style = Stroke(1.2f * u))
        val cap = pt(0.5f, 0f)
        val brote = a.luz(P.NOGAL_LUZ, kc, fc, if (aDerecha) 0.8f else 0.45f)
        drawPath(TrazosNouveau.hojaLanza(cap + Offset(0f, 1f * u), cap + Offset(-h * 0.026f, h * 0.01f), h * 0.008f, -0.2f), madera)
        drawPath(TrazosNouveau.hojaLanza(cap + Offset(0f, 1f * u), cap + Offset(h * 0.026f, h * 0.01f), h * 0.008f, 0.2f), madera)
        drawOval(madera, Offset(cap.x - h * 0.008f, cap.y - h * 0.03f), Size(h * 0.016f, h * 0.032f))
        drawOval(brote, Offset(cap.x - h * 0.004f + (if (aDerecha) h * 0.002f else -h * 0.002f), cap.y - h * 0.026f), Size(h * 0.007f, h * 0.02f))
        // El travesaño y el tablero bajo, con su moldura tallada.
        drawLine(a.sombra(0.4f), pt(ss, 0.785f), pt(1f - ss, 0.785f), 1.2f * u)
        drawLine(if (a.oscuro) a.filo(0.4f, kc) else a.claro(if (aDerecha) 0.4f else 0.15f), pt(0f, v1 + 0.004f), pt(1f, v1 + 0.004f), 0.9f * u)
        val tablero = Path()
        val pts = listOf(pt(ss * 1.8f, 0.81f), pt(1f - ss * 1.8f, 0.81f), pt(1f - ss * 1.8f, 0.91f), pt(ss * 1.8f, 0.91f))
        tablero.moveTo(pts[0].x, pts[0].y)
        tablero.quadraticTo(pt(0.5f, 0.79f).x, pt(0.5f, 0.79f).y, pts[1].x, pts[1].y)
        tablero.lineTo(pts[2].x, pts[2].y); tablero.lineTo(pts[3].x, pts[3].y); tablero.close()
        drawPath(tablero, mezcla(madera, a.sombra(1f), 0.18f))
        drawPath(tablero, a.sombra(0.4f), style = Stroke(1f * u))
        translate(if (aDerecha) -0.8f * u else 0.8f * u, 0.8f * u) { drawPath(tablero, if (a.oscuro) a.filo(0.25f, kc) else a.claro(0.25f), style = Stroke(0.6f * u)) }
        // La arista: la de delante, con luz; la de atrás, un pliegue hondo.
        for ((e, xe) in listOf(j to x0, (j + 1) to x1)) {
            val delante = e % 2 == 1
            val col = if (delante) (if (a.oscuro) a.fria(0.35f, 1f) else a.claro(0.4f)) else a.sombra(0.45f)
            drawLine(col, Offset(xe, arriba(e) + 2f * u), Offset(xe, abajo(e) - 2f * u), 1f * u)
        }
        // Las patitas y su sombra en el suelo.
        for ((e, xe) in listOf(j to x0, (j + 1) to x1)) {
            val yb = abajo(e)
            drawOval(a.sombra(0.4f), Offset(xe - h * 0.02f, yb - h * 0.004f), Size(h * 0.04f, h * 0.012f))
            drawOval(mezcla(madera, a.sombra(1f), 0.25f), Offset(xe - h * 0.011f, yb - h * 0.014f), Size(h * 0.022f, h * 0.016f))
        }
    }

    /** Un tallo bordado en la seda: sube con su leve curva, tres pares de hojas de sauce y el capullo cerrado con sus sépalos. */
    private fun DrawScope.bordado(pl: PlanoLibretaNouveau, a: AireLibretaNouveau, pt: (Float, Float) -> Offset, espejo: Boolean, v0: Float, v1: Float) {
        val u = pl.u; val h = pl.h
        val hilo = if (a.oscuro) P.HONDO_FRIO.copy(alpha = 0.72f) else P.BORDADO.copy(alpha = 0.78f)
        val sx = if (espejo) -1f else 1f
        val base = pt(0.5f - 0.08f * sx, v1 - 0.01f)
        val cima = pt(0.5f + 0.1f * sx, v0 + (if (espejo) 0.14f else 0.08f))
        val tallo = Path().apply {
            moveTo(base.x, base.y)
            cubicTo(base.x + sx * h * 0.03f, base.y - (base.y - cima.y) * 0.4f, cima.x - sx * h * 0.03f, cima.y + (base.y - cima.y) * 0.35f, cima.x, cima.y)
        }
        drawPath(tallo, hilo, style = Stroke(1.3f * u, cap = StrokeCap.Round))
        for (m in 0 until 3) {
            val f = 0.25f + 0.22f * m
            val q = Offset(base.x + (cima.x - base.x) * f + sx * h * 0.012f * sin(f * 3f), base.y + (cima.y - base.y) * f)
            val largo = h * (0.07f - 0.012f * m)
            drawPath(TrazosNouveau.hojaLanza(q, q + Offset(-largo * 0.7f, -largo * 0.55f), largo * 0.12f, 0.16f), hilo)
            drawPath(TrazosNouveau.hojaLanza(q, q + Offset(largo * 0.7f, -largo * 0.45f), largo * 0.12f, -0.16f), hilo)
        }
        drawOval(hilo, Offset(cima.x - h * 0.008f, cima.y - h * 0.034f), Size(h * 0.016f, h * 0.038f))
        drawPath(TrazosNouveau.hojaLanza(cima, cima + Offset(-h * 0.014f, -h * 0.018f), h * 0.004f, 0.1f), hilo)
        drawPath(TrazosNouveau.hojaLanza(cima, cima + Offset(h * 0.014f, -h * 0.018f), h * 0.004f, -0.1f), hilo)
        if (!a.oscuro) drawLine(Color.White.copy(alpha = 0.25f), Offset(cima.x + 0.6f * u, cima.y - h * 0.03f), Offset(cima.x + 0.6f * u, cima.y - h * 0.006f), 0.6f * u)
    }

    // ---------------------------------------------------------------- la mesa

    /**
     * La mesa de trabajo de nogal: su sombra en el suelo y en el rodapié, las patas de atrás, el tablero (con el charco de la
     * lámpara de noche), el canto moldurado con su filete de luz, el faldón con la talla de dos látigos que se encuentran y las
     * patas de delante, que bajan como tallos y se enroscan en el pie.
     */
    fun DrawScope.mesa(pl: PlanoLibretaNouveau, a: AireLibretaNouveau) {
        val u = pl.u; val h = pl.h
        val x0 = pl.mesaIzq; val x1 = pl.mesaDer
        // La sombra de la mesa: en el rodapié y en el suelo, debajo.
        drawRect(a.sombra(0.3f), Offset(x0 + h * 0.03f, pl.zocalo), Size(x1 - x0 - h * 0.06f, pl.suelo - pl.zocalo))
        drawPath(poligonoNouveau(x0 + h * 0.01f, pl.suelo, x1 - h * 0.01f, pl.suelo, x1 + h * 0.012f, pl.pie + h * 0.006f, x0 - h * 0.004f, pl.pie + h * 0.006f), a.sombra(if (a.oscuro) 0.5f else 0.34f))
        // Las patas de atrás, más angostas y en sombra.
        for (xb in floatArrayOf(x0 + h * 0.09f, x1 - h * 0.09f)) {
            val q = Offset(xb, pl.faldon)
            drawRect(a.luz(P.NOGAL_OSC, pl.k(q) * 0.3f, pl.f(q) * 0.5f, 0.35f), Offset(xb - h * 0.011f, pl.faldon - h * 0.01f), Size(h * 0.022f, pl.suelo + h * 0.022f - pl.faldon))
            drawOval(a.sombra(0.4f), Offset(xb - h * 0.02f, pl.suelo + h * 0.016f), Size(h * 0.04f, h * 0.012f))
        }
        // El tablero: la luz de la ventana de día y el charco de la lámpara de noche.
        val sup = poligonoNouveau(x0 + h * 0.012f, pl.mesaFondo, x1 - h * 0.012f, pl.mesaFondo, x1, pl.mesa, x0, pl.mesa)
        clipPath(sup) {
            campoLibretaNouveau(Rect(x0, pl.mesaFondo, x1, pl.mesa), 40, 3) { q -> a.luz(P.NOGAL, (pl.k(q) * 1.3f).coerceAtMost(1f), pl.f(q), pl.d(q) + 0.14f) }
            val vetas = Path()
            for (m in 0 until 5) {
                val y = pl.mesaFondo + (pl.mesa - pl.mesaFondo) * (0.15f + 0.18f * m)
                vetas.moveTo(x0, y); vetas.cubicTo(x0 + (x1 - x0) * 0.3f, y - 1f * u, x0 + (x1 - x0) * 0.6f, y + 1.2f * u, x1, y)
            }
            drawPath(vetas, a.sombra(0.12f), style = Stroke(0.7f * u))
        }
        // El canto: moldurado, con el filete de luz arriba, el bocel y la sombra abajo.
        val canto = Rect(x0, pl.mesa, x1, pl.canto)
        campoLibretaNouveau(canto, 40, 2) { q -> a.luz(P.NOGAL, pl.k(q) * 0.95f, pl.f(q), pl.d(q)) }
        tramosLibretaNouveau(x0, x1, canto.top + 0.7f * u, 1.4f * u, 14) { q -> if (a.oscuro) mezcla(a.luz(P.NOGAL_LUZ, pl.k(q) * 1.2f, pl.f(q)), P.LLAMA, 0.3f * pl.k(q)) else a.luz(P.NOGAL_LUZ, 0f, 0f, pl.d(q) + 0.25f) }
        drawLine(a.sombra(0.35f), Offset(x0, canto.top + canto.height * 0.55f), Offset(x1, canto.top + canto.height * 0.55f), 0.9f * u)
        tramosLibretaNouveau(x0, x1, canto.top + canto.height * 0.55f + 1.2f * u, 0.7f * u, 10) { q -> if (a.oscuro) a.filo(0.35f, pl.k(q)) else a.claro(0.3f) }
        drawLine(a.sombra(0.45f), Offset(x0, canto.bottom - 0.6f * u), Offset(x1, canto.bottom - 0.6f * u), 1.2f * u)
        // El faldón, debajo del vuelo del tablero.
        val fal = Rect(x0 + h * 0.026f, pl.canto, x1 - h * 0.026f, pl.faldon)
        campoLibretaNouveau(fal, 30, 2) { q -> a.luz(P.NOGAL, pl.k(q) * 0.55f, pl.f(q) * 0.7f, pl.d(q) - 0.06f) }
        drawRect(a.sombra(0.35f), fal.topLeft, Size(fal.width, h * 0.01f))
        drawLine(a.sombra(0.4f), Offset(fal.left, fal.bottom), Offset(fal.right, fal.bottom), 1.2f * u)
        // La talla: dos látigos que se encuentran en medio, con un capullo.
        val cx = fal.center.x; val cy = fal.center.y + h * 0.004f
        val kt = pl.k(Offset(cx, cy)) * 0.55f
        val talla = a.luz(P.NOGAL_LUZ, kt, pl.f(Offset(cx, cy)), 0.6f)
        listOf(-1f, 1f).forEach { s ->
            with(TrazosNouveau) {
                latigo(Offset(cx + s * h * 0.012f, cy), Offset(cx + s * h * 0.15f, cy - h * 0.006f), h * 0.014f, h * 0.007f, talla, 0.22f * s, s > 0, true)
            }
        }
        drawOval(talla, Offset(cx - h * 0.007f, cy - h * 0.016f), Size(h * 0.014f, h * 0.024f))
        drawOval(a.sombra(0.35f), Offset(cx - h * 0.007f, cy - h * 0.016f), Size(h * 0.014f, h * 0.024f), style = Stroke(0.7f * u))
        // Los dos cajones de los lados, con su junta y su botón de latón.
        if (fal.width > h * 0.75f) for (s in floatArrayOf(-1f, 1f)) {
            val xc = cx + s * fal.width * 0.36f
            val cj = Rect(xc - fal.width * 0.11f, fal.top + h * 0.012f, xc + fal.width * 0.11f, fal.bottom - h * 0.008f)
            val q = cj.center
            drawRect(a.sombra(0.4f), cj.topLeft, cj.size, style = Stroke(1f * u))
            drawLine(if (a.oscuro) a.filo(0.4f, pl.k(q)) else a.claro(0.3f), Offset(cj.left + 1f * u, cj.bottom + 1f * u), Offset(cj.right + 1f * u, cj.bottom + 1f * u), 0.7f * u)
            drawCircle(a.sombra(0.35f), h * 0.008f, q + Offset(0.8f * u, 1.2f * u))
            drawCircle(a.luz(P.BRONCE, pl.k(q) * 0.8f, pl.f(q), 0.7f), h * 0.008f, q)
            drawCircle(if (a.oscuro) a.filo(0.8f, pl.k(q)) else a.claro(0.7f), h * 0.0028f, q + Offset(h * 0.002f, -h * 0.002f))
        }
        // Las patas de delante.
        pata(pl, a, x0 + h * 0.03f, false)
        pata(pl, a, x1 - h * 0.03f, true)
    }

    /** Una pata de delante: sale ancha del faldón, adelgaza como un tallo y abajo se enrosca hacia afuera en un pie de hoja. */
    private fun DrawScope.pata(pl: PlanoLibretaNouveau, a: AireLibretaNouveau, x: Float, espejo: Boolean) {
        val u = pl.u; val h = pl.h
        val s = if (espejo) -1f else 1f
        val y0 = pl.canto; val y1 = pl.pie
        val p = Path().apply {
            moveTo(x - s * h * 0.02f, y0)
            lineTo(x + s * h * 0.022f, y0)
            cubicTo(x + s * h * 0.03f, pl.faldon + h * 0.02f, x + s * h * 0.004f, y1 - h * 0.08f, x + s * h * 0.008f, y1 - h * 0.012f)
            lineTo(x + s * h * 0.004f, y1)
            lineTo(x - s * h * 0.024f, y1)
            quadraticTo(x - s * h * 0.04f, y1 - h * 0.006f, x - s * h * 0.034f, y1 - h * 0.02f)
            quadraticTo(x - s * h * 0.022f, y1 - h * 0.03f, x - s * h * 0.016f, y1 - h * 0.02f)
            cubicTo(x - s * h * 0.012f, y1 - h * 0.08f, x - s * h * 0.03f, pl.faldon + h * 0.02f, x - s * h * 0.02f, y0)
            close()
        }
        val q = Offset(x, (y0 + y1) / 2f)
        val arriba = a.luz(P.NOGAL, pl.k(Offset(x, y0)) * 0.6f, pl.f(q), pl.d(q))
        val abajo = a.luz(P.NOGAL_OSC, pl.k(Offset(x, y1)) * 0.4f, pl.f(q), pl.d(q) - 0.1f)
        drawOval(a.sombra(0.45f), Offset(x - h * 0.04f, y1 - h * 0.006f), Size(h * 0.08f, h * 0.014f))
        drawPath(p, Brush.verticalGradient(listOf(arriba, abajo), y0, y1))
        // El filo del lado de la luz: de día, el de la ventana (a la derecha); de noche, el de la lámpara (a la izquierda).
        val lado = if (a.oscuro) -1f else 1f
        val filo = if (a.oscuro) a.filo(0.35f, pl.k(q)) else a.claro(0.35f)
        drawLine(filo, Offset(x + lado * h * 0.012f, y0 + h * 0.008f), Offset(x + lado * h * 0.004f, y1 - h * 0.03f), 1f * u)
        drawPath(p, a.sombra(0.4f), style = Stroke(0.7f * u))
    }
}
