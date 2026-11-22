package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.AI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.HINOKI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.HINOKI_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KAMI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.SHU
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.WARA
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.WASHI
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import kotlin.math.roundToInt

// Los volúmenes de la casa de té de la posta (MuebleEstanteEdo): tres objetos según la semilla.

/** Lo que no tiene ánimo marcado: tela cruda, papel sin teñir y cáñamo. */
private val CRUDO_EDO = Color(0xFFC9BD9E)
private val GRIS_EDO = Color(0xFFB2AA98)
private val LUNA_EDO = Color(0xFFEDE6C8)

/** Las escalas del ánimo de Edo, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_EDO: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.EDO, false) to juegoTonos(TemaId.EDO, true) }

/**
 * El color del ánimo tal como es de día. En el modo oscuro la escala del tema cambia de colores (pensada para la interfaz) y
 * un volumen claro se volvía negro de noche. Aquí el volumen es un objeto del cuarto: conserva su color de día y la luz del
 * farol lo apaga con la distancia. Se ubica el color en la escala de noche (el tramo y la fracción que más se le acercan) y se
 * toma el mismo punto en la de día; si no está en la escala, queda igual. Sin ánimo, null.
 */
internal fun tonoDeDiaEdo(t: Color?, oscuro: Boolean): Color? {
    if (t == null || !oscuro) return t
    val (dia, noche) = TONOS_EDO
    if (dia.size < 3 || noche.size < 3) return t
    var mejor: Color = t
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = noche[seg]; val a1 = noche[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoEdo(t.red - a0.red - dr * f) + cuadradoEdo(t.green - a0.green - dg * f) + cuadradoEdo(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = mezcla(dia[seg], dia[seg + 1], f) }
    }
    return mejor
}

private fun cuadradoEdo(x: Float) = x * x

internal fun DrawScope.volumenEdo(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
    if (size.width < 2f || size.height < 2f) return
    val a = AireEdo(oscuro, v.noche)
    val fx = MuebleEstanteEdo.LADO + v.centro * (1f - 2 * MuebleEstanteEdo.LADO)
    val pos = v.repisa + 0.55f
    val k = if (a.lumbre) cercaCuerpoEdo(pos, v.repisas, fx, a.lunar) else 1f
    val f = if (a.oscuro) frioCuerpoEdo(pos, v.repisas, fx) else 0f
    val hacia = if (a.lumbre) (if (MuebleEstanteEdo.LAMP_X >= fx) 1f else -1f) else -1f
    when (MuebleEstanteEdo.clase(v.semilla)) {
        0 -> estucheEdo(v, medidor, a, k, f, hacia)
        1 -> pilaCuadernosEdo(v, medidor, a, k, f, hacia)
        else -> hatilloEdo(v, medidor, a, k, f, hacia)
    }
}

/** El mes en columna, signo bajo signo, como se escribe en vertical; si es largo, de abajo arriba a lo largo. */
private fun DrawScope.mesEdo(medidor: TextMeasurer, mes: String, r: Rect, color: Color, tam: Float) {
    if (r.height < 6f || r.width < 3f) return
    val signos = mes.filter { !it.isWhitespace() }
    if (signos.length in 1..4) {
        val porAlto = r.height / signos.length / density / 1.08f
        val t = minOf(tam, porAlto, r.width / density / 1.05f).coerceAtLeast(3.5f)
        val estilo = TextStyle(fontFamily = MuebleEstanteEdo.LETRA, fontSize = t.sp, lineHeight = (t * 1.08f).sp, color = color, textAlign = TextAlign.Center)
        val capa = medidor.measure(signos.toList().joinToString("\n"), estilo)
        drawText(capa, topLeft = Offset(r.center.x - capa.size.width / 2f, r.center.y - capa.size.height / 2f))
    } else {
        textoEn(medidor, mes, r, MuebleEstanteEdo.LETRA, color, tam * 0.9f, vertical = true, minimo = 4.5f)
    }
}

/**
 * Las baldosas de las telas, pintadas una sola vez (y otra si cambia la densidad): el estampado menudo de puntos del estuche
 * (komon) y los círculos encadenados del paño del hatillo (shippō). Se pegan en mosaico.
 */
private object TelasEdo {
    private var densidad = 0f
    private var komon: ImageBitmap? = null
    private var shippo: ImageBitmap? = null
    fun komon(u: Float): ImageBitmap { if (u != densidad || komon == null) rehacer(u); return komon!! }
    fun shippo(u: Float): ImageBitmap { if (u != densidad || shippo == null) rehacer(u); return shippo!! }
    private fun baldosa(lado: Int, pintar: DrawScope.(Float) -> Unit): ImageBitmap {
        val img = ImageBitmap(lado, lado)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(lado.toFloat(), lado.toFloat())) { pintar(lado.toFloat()) }
        return img
    }
    private fun rehacer(u: Float) {
        densidad = u
        val l1 = (8f * u).roundToInt().coerceAtLeast(4)
        komon = baldosa(l1) { l ->
            val claro = Color.White.copy(alpha = 0.17f); val hondo = Color(0xFF1A1426).copy(alpha = 0.24f)
            for (c in listOf(Offset(l * 0.25f, l * 0.25f), Offset(l * 0.75f, l * 0.75f))) drawCircle(claro, 0.55f * u, c)
            for (c in listOf(Offset(l * 0.75f, l * 0.25f), Offset(l * 0.25f, l * 0.75f))) drawCircle(hondo, 0.45f * u, c)
        }
        val l2 = (10f * u).roundToInt().coerceAtLeast(4)
        shippo = baldosa(l2) { l ->
            // Los círculos encadenados del estampado (shippō): uno en cada esquina y uno en el centro, del mismo radio.
            val claro = Color.White.copy(alpha = 0.24f); val g = (0.7f * u).coerceAtLeast(0.7f)
            for (c in listOf(Offset(0f, 0f), Offset(l, 0f), Offset(0f, l), Offset(l, l), Offset(l / 2, l / 2))) {
                drawCircle(claro, l * 0.5f, c, style = Stroke(g))
            }
            drawCircle(Color(0xFF1A1426).copy(alpha = 0.22f), l * 0.06f, Offset(l / 2, 0f))
            drawCircle(Color(0xFF1A1426).copy(alpha = 0.22f), l * 0.06f, Offset(0f, l / 2))
        }
    }
}

private fun DrawScope.telaEdo(b: ImageBitmap, r: Rect, alfa: Float) {
    var y = r.top
    while (y < r.bottom) {
        var x = r.left
        while (x < r.right) { drawImage(b, Offset(x, y), alpha = alfa); x += b.width }
        y += b.height
    }
}

/** El cuerpo con su bulto: la luz del lado del ranma (o del farol) y la sombra del otro. */
private fun DrawScope.bultoEdo(a: AireEdo, k: Float, f: Float, hacia: Float, l: Float, r: Float, arriba: Float, abajo: Float) {
    val u = density; val w = r - l
    val alto = abajo - arriba
    val luzIzq = !a.oscuro || hacia < 0f
    drawRect(a.claro(0.2f), Offset(if (luzIzq) l + w * 0.1f else l + w * 0.68f, arriba), Size(w * 0.16f, alto))
    drawRect(a.sombra(0.2f), Offset(if (luzIzq) l + w * 0.62f else l, arriba), Size(w * 0.38f, alto))
    drawRect(a.sombra(0.24f), Offset(if (luzIzq) l + w * 0.86f else l, arriba), Size(w * 0.14f, alto))
    if (a.oscuro) {
        if (k > 0.08f) drawRect(FILO_MUEBLE.copy(alpha = (0.5f * k).coerceAtMost(0.5f)), Offset(if (hacia > 0) r - 1.1f * u else l, arriba), Size(1.1f * u, alto))
        if (f > 0.08f) drawRect(a.helado(0.4f * f), Offset(if (hacia > 0) l else r - 1f * u, arriba), Size(1f * u, alto))
    }
}

/** El sello cuadrado de bermellón (sin signo: un marco con un punto, que no es letra) o, en los Sueños, la luna creciente. */
private fun DrawScope.selloEdo(c: Offset, s: Float, luna: Boolean, a: AireEdo, k: Float, fondo: Color) {
    val u = density
    if (luna) {
        drawCircle(if (a.oscuro) mezcla(LUNA_EDO, a.hondo, 0.15f - 0.15f * k) else mezcla(LUNA_EDO, AI, 0.1f), s * 0.5f, c)
        drawCircle(fondo, s * 0.42f, c + Offset(s * 0.22f, -s * 0.12f))
        return
    }
    drawRect(a.luz(SHU, maxOf(k, 0.35f)), Offset(c.x - s / 2, c.y - s / 2), Size(s, s))
    val marca = fondo.copy(alpha = 0.85f)
    drawRect(marca, Offset(c.x - s * 0.32f, c.y - s * 0.32f), Size(s * 0.64f, s * 0.64f), style = Stroke(0.6f * u))
    drawCircle(marca, s * 0.1f, c)
}

/**
 * El estuche de tela (chitsu): cartón forrado de tela del color del ánimo con su estampado menudo, de pie; la solapa que da
 * la vuelta y se cierra con los broches de hueso (más broches cuanto más escribiste) en sus presillas de hilo; la tira de
 * papel pegada con el día, el mes en columna y el sello (en los Sueños, la luna); arriba asoman los marcadores de papel.
 */
private fun DrawScope.estucheEdo(v: Volumen, medidor: TextMeasurer, a: AireEdo, k: Float, f: Float, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val tela = tonoDeDiaEdo(v.tono, a.oscuro)?.let { a.animo(it, k, f) } ?: a.luz(CRUDO_EDO, k, f)
    val hondo = mezcla(tela, a.hondo, 0.45f)
    val claro = mezcla(tela, if (a.oscuro) FILO_MUEBLE else Color.White, 0.25f)
    val top = 5f * u
    val der = w - 3f * u
    // Los marcadores de papel que asoman por arriba: uno, o hasta cuatro si escribiste mucho.
    val marcas = 1 + (v.lleno * 3).toInt()
    for (j in 0 until marcas) {
        val x = w * (0.18f + 0.17f * j + 0.05f * azar(s, 20 + j))
        val alto = (3f + 4f * azar(s, 25 + j)) * u
        drawRect(a.etiqueta(k, if (j % 2 == 0) WASHI else mezcla(WASHI, SHU, 0.25f)), Offset(x, top - alto), Size(2.6f * u, alto + 1f * u))
        drawRect(a.sombra(0.25f), Offset(x + 2f * u, top - alto), Size(0.6f * u, alto))
    }
    drawRoundRect(tela, Offset(0f, top), Size(der, h - top), CornerRadius(2.2f * u))
    clipRect(0f, top, der, h) {
        telaEdo(TelasEdo.komon(u), Rect(0f, top, der, h), if (a.oscuro) 0.55f else 0.8f)
        // Las orillas de arriba y abajo, gastadas: la tela clara donde roza el cartón.
        drawRect(claro.copy(alpha = 0.5f), Offset(0f, top), Size(der, 1.6f * u))
        drawRect(hondo.copy(alpha = 0.6f), Offset(0f, h - 2.4f * u), Size(der, 2.4f * u))
        // La solapa: su orilla de pie, con el grueso del cartón que hace sombra.
        val xs = der * 0.7f
        drawRect(a.sombra(0.22f), Offset(xs - 2.2f * u, top), Size(2.2f * u, h - top))
        drawLine(hondo, Offset(xs, top), Offset(xs, h), 0.9f * u)
        drawLine(claro.copy(alpha = 0.6f), Offset(xs + 1f * u, top), Offset(xs + 1f * u, h), 0.6f * u)
        // Una mancha de uso y el roce de la esquina.
        drawOval(hondo.copy(alpha = 0.2f), Offset(der * (0.2f + 0.3f * azar(s, 3)), h * (0.7f + 0.1f * azar(s, 4))), Size(der * 0.4f, h * 0.07f))
        bultoEdo(a, k, f, hacia, 0f, der, top, h)
    }
    // Los broches de hueso en el canto derecho, con su presilla de hilo que viene de la solapa.
    val broches = if (v.lleno > 0.6f) 3 else 2
    for (j in 0 until broches) {
        val y = top + (h - top) * (0.22f + 0.56f * j / (broches - 1))
        drawLine(a.luz(WASHI, k, f).copy(alpha = 0.75f), Offset(der * 0.72f, y), Offset(der - 1f * u, y), 0.8f * u)
        val b = Rect(der - 2.2f * u, y - 3.6f * u, der + 2.2f * u, y + 3.6f * u)
        drawOval(a.sombra(0.35f), b.topLeft + Offset(0.8f * u, 0.8f * u), b.size)
        drawOval(a.luz(mezcla(WASHI, KAMI, 0.35f), k + 0.1f, f), b.topLeft, b.size)
        drawLine(a.claro(0.5f), Offset(b.left + 1.1f * u, b.top + 1.6f * u), Offset(b.left + 1.1f * u, b.bottom - 1.6f * u), 0.6f * u)
    }
    // La tira de papel con el día y el mes, pegada arriba a la izquierda.
    val tira = Rect(w * 0.1f, top + (h - top) * 0.06f, minOf(der * 0.64f, w * 0.1f + 22f * u), top + (h - top) * 0.66f)
    drawRect(a.sombra(0.3f), tira.topLeft + Offset(0.8f * u, 0.8f * u), tira.size)
    drawRect(a.etiqueta(k, WASHI), tira.topLeft, tira.size)
    drawRect(a.sombra(0.12f), Offset(tira.right - tira.width * 0.2f, tira.top), Size(tira.width * 0.2f, tira.height))
    drawRect(a.luz(mezcla(WASHI, KAMI, 0.6f), k, f), tira.topLeft, tira.size, style = Stroke(0.5f * u))
    val alturaDia = minOf(tira.height * 0.3f, 16f * u)
    textoEn(medidor, v.dia, Rect(tira.left, tira.top + 1.5f * u, tira.right, tira.top + alturaDia), MuebleEstanteEdo.LETRA, a.tinta, 13f, peso = FontWeight.Bold, minimo = 6f)
    val ss = minOf(tira.width * 0.6f, 6f * u)
    val sello = Offset(tira.center.x, tira.bottom - ss * 0.8f)
    mesEdo(medidor, v.mes, Rect(tira.left, tira.top + alturaDia + 1f * u, tira.right, sello.y - ss * 0.65f), a.tinta, 8f)
    selloEdo(sello, ss, v.noche, a, k, a.etiqueta(k, WASHI))
}

/**
 * La pila de cuadernos cosidos que presta el librero, tumbados uno sobre otro (más cuadernos cuanto más escribiste): de
 * cada uno se ve el lomo, con su tapa del color del ánimo (cada uno de un tinte un poco distinto), el canto de las hojas y
 * las cuatro puntadas del cosido; los ata un cordón de papel torcido con su nudo, del que cuelga la etiqueta con el día y el mes.
 */
private fun DrawScope.pilaCuadernosEdo(v: Volumen, medidor: TextMeasurer, a: AireEdo, k: Float, f: Float, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val tela = tonoDeDiaEdo(v.tono, a.oscuro)?.let { a.animo(it, k, f) } ?: a.luz(GRIS_EDO, k, f)
    val n = (3 + (v.lleno * 4).toInt()).coerceIn(3, 7)
    val banda = h / n
    val hojas = a.luz(mezcla(WASHI, KAMI, 0.45f), k, f)
    for (i in 0 until n) {
        val y1 = h - banda * i; val y0 = y1 - banda
        val corre = (azar(s, 40 + i) - 0.5f) * w * 0.14f
        val l = maxOf(0f, corre); val r = w + minOf(0f, corre)
        val tapa = mezcla(tela, if (azar(s, 50 + i) > 0.5f) a.hondo else Color.White, 0.05f + 0.16f * azar(s, 60 + i))
        val hondoTapa = mezcla(tapa, a.hondo, 0.4f)
        drawRoundRect(tapa, Offset(l, y0 + 0.4f * u), Size(r - l, banda - 0.4f * u), CornerRadius(1.6f * u))
        // El canto de las hojas, delgado, entre las dos tapas, y la sombra que deja el cuaderno de arriba.
        drawRect(hojas, Offset(l + 2f * u, y0 + banda * 0.42f), Size(r - l - 4f * u, banda * 0.16f))
        drawRect(a.sombra(0.34f), Offset(l, y0 + 0.4f * u), Size(r - l, 1f * u))
        drawRect(mezcla(tapa, Color.White, 0.22f).copy(alpha = 0.7f), Offset(l + 1f * u, y1 - 1.2f * u), Size(r - l - 2f * u, 0.8f * u))
        // Las puntadas del cosido en el lomo: cuatro nuditos de hilo, a lo largo.
        for (j in 0 until 4) {
            val x = l + (r - l) * (0.14f + 0.24f * j)
            drawLine(hondoTapa, Offset(x, y0 + banda * 0.18f), Offset(x, y0 + banda * 0.36f), 0.9f * u, StrokeCap.Round)
            drawLine(hondoTapa, Offset(x, y0 + banda * 0.64f), Offset(x, y0 + banda * 0.82f), 0.9f * u, StrokeCap.Round)
        }
    }
    // El bulto de la pila.
    bultoEdo(a, k, f, hacia, 0f, w, 0f, h)
    // El cordón de papel torcido que la ata y su nudo encima.
    val xc = w * 0.74f
    val cordon = a.luz(mezcla(WASHI, KAMI, 0.2f), k + 0.1f, f)
    drawLine(a.sombra(0.3f), Offset(xc + 1f * u, 0f), Offset(xc + 1f * u, h), 1.5f * u)
    drawLine(cordon, Offset(xc, 0f), Offset(xc, h), 1.4f * u)
    drawCircle(cordon, 2.2f * u, Offset(xc, 1.6f * u))
    drawLine(cordon, Offset(xc, 1.6f * u), Offset(xc - 4f * u, 5f * u), 1f * u, StrokeCap.Round)
    // La etiqueta, colgada del nudo a un lado, para que se vean los lomos cosidos.
    val tw = minOf(w * 0.44f, 15f * u)
    val th = minOf(h * 0.7f, 32f * u)
    val cuelga = Offset(minOf(xc, w - tw / 2 - 1.5f * u), 4f * u)
    rotate(-3f + 5f * azar(s, 70), cuelga) {
        val et = Rect(cuelga.x - tw / 2, cuelga.y + 2f * u, cuelga.x + tw / 2, cuelga.y + 2f * u + th)
        drawRect(a.sombra(0.4f), et.topLeft + Offset(1f * u, 1.2f * u), et.size)
        drawRect(a.etiqueta(k, WASHI), et.topLeft, et.size)
        drawRect(a.sombra(0.12f), Offset(et.right - tw * 0.2f, et.top), Size(tw * 0.2f, th))
        drawCircle(a.sombra(0.5f), 0.9f * u, Offset(cuelga.x, et.top + 2f * u))
        val alturaDia = minOf(th * 0.4f, 15f * u)
        textoEn(medidor, v.dia, Rect(et.left, et.top + 3f * u, et.right, et.top + 3f * u + alturaDia), MuebleEstanteEdo.LETRA, a.tinta, 12f, peso = FontWeight.Bold, minimo = 6f)
        val ss = minOf(tw * 0.42f, 5f * u)
        val sello = Offset(et.center.x, et.bottom - ss * 0.8f)
        mesEdo(medidor, v.mes, Rect(et.left + 1f * u, et.top + 3f * u + alturaDia, et.right - 1f * u, sello.y - ss * 0.6f), a.tinta, 7f)
        selloEdo(sello, ss, v.noche, a, k, a.etiqueta(k, WASHI))
    }
}

/**
 * El hatillo de furoshiki: el paño cuadrado estampado (círculos encadenados, del color del ánimo) que envuelve el bulto de
 * las cartas y se anuda arriba con dos puntas en orejas; panzón (más cuanto más escribiste) y bajo, con los pliegues que tiran
 * hacia el nudo. Atada al nudo, la tablilla de madera de cinco lados de los bultos de la posta, con el día, el mes y el sello.
 */
private fun DrawScope.hatilloEdo(v: Volumen, medidor: TextMeasurer, a: AireEdo, k: Float, f: Float, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val tela = tonoDeDiaEdo(v.tono, a.oscuro)?.let { a.animo(it, k, f) } ?: a.luz(CRUDO_EDO, k, f)
    val hondo = mezcla(tela, a.hondo, 0.45f)
    val claro = mezcla(tela, if (a.oscuro) FILO_MUEBLE else Color.White, 0.28f)
    val cx = w * (0.48f + 0.06f * (azar(s, 120) - 0.5f))
    val nudoY = h * 0.25f
    val arriba = h * 0.3f
    val panza = 0.04f + 0.08f * v.lleno
    // El bulto: de los hombros que tiran hacia el nudo a la panza redonda que se asienta en la tabla.
    val cuerpo = Path().apply {
        moveTo(cx - w * 0.16f, nudoY + h * 0.02f)
        cubicTo(cx - w * 0.36f, arriba, -w * panza, h * 0.4f, -w * panza * 0.5f, h * 0.72f)
        cubicTo(0f, h * 0.96f, w * 0.12f, h, w * 0.3f, h)
        lineTo(w * 0.7f, h)
        cubicTo(w * 0.88f, h, w, h * 0.96f, w * (1f + panza * 0.5f), h * 0.72f)
        cubicTo(w * (1f + panza), h * 0.4f, cx + w * 0.36f, arriba, cx + w * 0.16f, nudoY + h * 0.02f)
        close()
    }
    // Las dos orejas del nudo: las puntas del paño, una hacia cada lado.
    val orejaI = Path().apply {
        moveTo(cx - w * 0.04f, nudoY)
        quadraticTo(cx - w * 0.3f, nudoY - h * 0.02f, cx - w * 0.36f, h * 0.03f)
        quadraticTo(cx - w * 0.12f, h * 0.08f, cx + w * 0.02f, nudoY - h * 0.03f)
        close()
    }
    val orejaD = Path().apply {
        moveTo(cx + w * 0.03f, nudoY)
        quadraticTo(cx + w * 0.22f, nudoY - h * 0.08f, cx + w * 0.3f, h * 0.06f)
        quadraticTo(cx + w * 0.1f, h * 0.12f, cx - w * 0.02f, nudoY - h * 0.02f)
        close()
    }
    drawPath(corridoEdo(cuerpo, -hacia * 1.6f * u, 1.2f * u), a.sombra(0.3f))
    drawPath(cuerpo, tela)
    clipPath(cuerpo) {
        telaEdo(TelasEdo.shippo(u), Rect(0f, nudoY, w, h), if (a.oscuro) 0.5f else 0.85f)
        // Los pliegues que tiran hacia el nudo, con su lado de luz, y el doblez de abajo, donde el paño da la vuelta.
        for (j in 0 until 3) {
            for (lado in listOf(-1f, 1f)) {
                val x1 = cx + lado * w * (0.22f + 0.15f * j)
                val y1 = h * (0.55f + 0.14f * j + 0.04f * azar(s, 130 + j))
                val pl = Path().apply { moveTo(cx + lado * w * 0.08f, nudoY + h * 0.04f); quadraticTo(cx + lado * w * (0.2f + 0.1f * j), h * (0.4f + 0.06f * j), x1, y1) }
                drawPath(pl, hondo.copy(alpha = 0.5f), style = Stroke(1f * u, cap = StrokeCap.Round))
                drawPath(corridoEdo(pl, -1.1f * u, 0f), claro.copy(alpha = 0.35f), style = Stroke(0.6f * u, cap = StrokeCap.Round))
            }
        }
        val doblez = Path().apply { moveTo(w * 0.04f, h * 0.8f); quadraticTo(w * 0.5f, h * (0.86f + 0.04f * azar(s, 136)), w * 0.96f, h * 0.78f) }
        drawPath(doblez, hondo.copy(alpha = 0.45f), style = Stroke(1f * u, cap = StrokeCap.Round))
        drawRect(hondo.copy(alpha = 0.35f), Offset(0f, h * 0.94f), Size(w, h * 0.06f))
        bultoEdo(a, k, f, hacia, 0f, w, arriba, h)
    }
    for ((o, i) in listOf(orejaI to 0, orejaD to 1)) {
        drawPath(o, if (i == 0) mezcla(tela, a.hondo, 0.12f) else tela)
        clipPath(o) { telaEdo(TelasEdo.shippo(u), Rect(0f, 0f, w, nudoY), if (a.oscuro) 0.45f else 0.8f) }
        drawPath(o, hondo.copy(alpha = 0.5f), style = Stroke(0.6f * u))
    }
    // El nudo: el paño apretado, con su sombra debajo.
    val nudo = Rect(cx - w * 0.13f, nudoY - h * 0.05f, cx + w * 0.13f, nudoY + h * 0.06f)
    drawOval(a.sombra(0.3f), nudo.topLeft + Offset(0.8f * u, 1.6f * u), nudo.size)
    drawOval(mezcla(tela, a.hondo, 0.18f), nudo.topLeft, nudo.size)
    drawArc(claro.copy(alpha = 0.6f), 190f, 120f, false, nudo.topLeft + Offset(1f * u, 1f * u), Size(nudo.width - 2f * u, nudo.height - 2f * u), style = Stroke(0.8f * u))
    drawLine(hondo, Offset(cx - w * 0.02f, nudo.top + 1f * u), Offset(cx + w * 0.03f, nudo.bottom - 1f * u), 0.8f * u)
    // La tablilla de madera de cinco lados, atada al nudo con un cordel, con el día, el mes y el sello (en los Sueños, la luna).
    val tw = minOf(w * 0.46f, 15f * u)
    val th = minOf(h * 0.55f, 30f * u)
    val cuelga = Offset(minOf(cx + w * 0.14f, w - tw / 2 - 1f * u), nudoY + h * 0.03f)
    val cordel = a.luz(mezcla(WARA, WASHI, 0.3f), k, f)
    rotate(4f - 6f * azar(s, 110), cuelga) {
        val t0 = cuelga.y + 3f * u
        val pico = tw * 0.32f
        val forma = poligonoEdo(cuelga.x - tw / 2, t0 + pico, cuelga.x, t0, cuelga.x + tw / 2, t0 + pico, cuelga.x + tw / 2, t0 + th, cuelga.x - tw / 2, t0 + th)
        drawLine(cordel, Offset(cuelga.x - 2f * u, cuelga.y - 2f * u), Offset(cuelga.x, t0 + pico * 0.6f), 0.8f * u)
        drawPath(corridoEdo(forma, 1f * u, 1.3f * u), a.sombra(0.42f))
        drawPath(forma, a.etiqueta(k, mezcla(HINOKI, WASHI, 0.5f)))
        drawRect(a.sombra(0.12f), Offset(cuelga.x + tw * 0.3f, t0 + pico * 0.6f), Size(tw * 0.2f, th - pico * 0.6f))
        drawLine(a.luz(HINOKI_OSC, k, f).copy(alpha = 0.35f), Offset(cuelga.x - tw * 0.3f, t0 + pico + 2f * u), Offset(cuelga.x - tw * 0.3f, t0 + th - 1f * u), 0.5f * u)
        drawCircle(a.sombra(0.55f), 0.9f * u, Offset(cuelga.x, t0 + pico * 0.6f))
        val alturaDia = minOf((th - pico) * 0.42f, 14f * u)
        val y0 = t0 + pico * 0.85f
        textoEn(medidor, v.dia, Rect(cuelga.x - tw / 2, y0, cuelga.x + tw / 2, y0 + alturaDia), MuebleEstanteEdo.LETRA, a.tinta, 11.5f, peso = FontWeight.Bold, minimo = 6f)
        val ss = minOf(tw * 0.4f, 5f * u)
        val sello = Offset(cuelga.x, t0 + th - ss * 0.8f)
        mesEdo(medidor, v.mes, Rect(cuelga.x - tw / 2 + 1f * u, y0 + alturaDia, cuelga.x + tw / 2 - 1f * u, sello.y - ss * 0.6f), a.tinta, 7f)
        selloEdo(sello, ss, v.noche, a, k, a.etiqueta(k, mezcla(HINOKI, WASHI, 0.5f)))
    }
}
