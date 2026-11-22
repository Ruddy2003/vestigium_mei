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
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.AI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.HINOKI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KAKI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KAMI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KURI_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.MATCHA
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.SHU
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.WARA
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.WASHI
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import kotlin.math.roundToInt

// Los volúmenes de la casa de té de la posta (MuebleEstanteEdo): el estuche de tela, la pila de cuadernos y el hatillo.

/** Lo que no tiene ánimo marcado: tela cruda y papel sin teñir. */
private val CRUDO_EDO = Color(0xFFC9BD9E)
private val GRIS_EDO = Color(0xFFB9B09A)
private val LUNA_EDO = Color(0xFFEDE6C8)

/** Las escalas del ánimo de Edo, la de día y la de noche (bajo, medio, alto). */
private val TONOS_EDO: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.EDO, false) to juegoTonos(TemaId.EDO, true) }

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaEdo(t: Color, escala: List<Color>): Float? {
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoEdo(t.red - a0.red - dr * f) + cuadradoEdo(t.green - a0.green - dg * f) + cuadradoEdo(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

private fun cuadradoEdo(x: Float) = x * x

/**
 * El color del ánimo tal como es de día. En el modo oscuro la escala del tema cambia de colores (pensada para la interfaz) y
 * un volumen claro se volvía negro de noche. Aquí el volumen es un objeto del cuarto: conserva su color de día y la luz del
 * farol lo apaga con la distancia. Se ubica el color en la escala de noche y se toma el mismo punto en la de día; si no está
 * en la escala, queda igual. Sin ánimo, null.
 */
internal fun tonoDeDiaEdo(t: Color?, oscuro: Boolean): Color? {
    if (t == null || !oscuro) return t
    val (dia, noche) = TONOS_EDO
    val x = enEscalaEdo(t, noche) ?: return t
    return puntoEscalaEdo(dia, x)
}

private fun puntoEscalaEdo(e: List<Color>, x: Float): Color = if (x < 0.5f) mezcla(e[0], e[1], x * 2) else mezcla(e[1], e[2], (x - 0.5f) * 2)

/**
 * El ánimo como tinte de tela o de papel: la escala del tema va del añil al papel y del papel al bermellón, y sus puntos de en
 * medio salían lavados, en pastel. Un tinte se queda con más cuerpo: el mismo orden y los mismos extremos, pero los puntos de
 * en medio se acercan a su extremo (el papel blanco sigue siendo el ánimo de en medio). Si no está en la escala, queda igual.
 */
internal fun tenidoEdo(t: Color?): Color? {
    if (t == null) return null
    val dia = TONOS_EDO.first
    val x = enEscalaEdo(t, dia) ?: return t
    val q = if (x < 0.5f) { val y = 1f - x * 2; 0.5f - 0.5f * (1f - (1f - y) * (1f - y)) } else { val y = (x - 0.5f) * 2; 0.5f + 0.5f * (1f - (1f - y) * (1f - y)) }
    return puntoEscalaEdo(dia, q)
}

internal fun DrawScope.volumenEdo(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
    if (size.width < 2f || size.height < 2f) return
    val a = AireEdo(oscuro, v.noche)
    val p = lugarVolumenEdo(v.repisa, v.repisas, v.centro)
    val k = if (a.oscuro) luzEdo(p.x, p.y, a.lunar, brasaNominalEdo(v.repisas)) else if (a.lumbre) farolEdo(p.x, p.y, false) else 1f
    val f = frioEdo(p.x, p.y)
    // De dónde viene la luz: de día, del ranma (izquierda); de noche, del farol.
    val hacia = if (a.oscuro) (if (MuebleEstanteEdo.LAMP_X >= p.x) 1f else -1f) else -1f
    val tinte = tenidoEdo(tonoDeDiaEdo(v.tono, oscuro))
    // Lejos del farol, lo guardado se hunde con color pero no desaparece: nunca baja de un poco de luz.
    val kv = if (a.oscuro) maxOf(k, 0.12f) else k
    when (MuebleEstanteEdo.clase(v.semilla)) {
        0 -> estucheEdo(v, medidor, a, kv, f, hacia, tinte)
        1 -> pilaCuadernosEdo(v, medidor, a, kv, f, hacia, tinte)
        else -> hatilloEdo(v, medidor, a, kv, f, hacia, tinte)
    }
    // La brasa del pie, de noche, deja un borde cálido desde abajo en lo que tiene cerca.
    if (a.oscuro) {
        val b = brasaNominalEdo(v.repisas)
        val kb = brasaEdo(p.x, p.y, b)
        if (kb > 0.03f) {
            val u = density
            val calor = EdoTinta.BRASA.copy(alpha = (kb * 3.2f).coerceAtMost(0.6f))
            drawRect(calor, Offset(1f * u, size.height - 1.6f * u), Size(size.width - 2f * u, 1.6f * u))
            val x = if (b.x < p.x) 0f else size.width - 1.2f * u
            drawRect(calor.copy(alpha = calor.alpha * 0.7f), Offset(x, size.height * 0.62f), Size(1.2f * u, size.height * 0.36f))
        }
    }
}

// ---------------------------------------------------------------- ayudantes

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
 * Las baldosas de las telas, pintadas una sola vez (y otra si cambia la densidad): la trama del algodón con su estampado menudo
 * de puntos (komon) para el estuche y los círculos encadenados (shippō) del paño del hatillo. Se pegan en mosaico.
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
        val l1 = (6f * u).roundToInt().coerceAtLeast(4)
        komon = baldosa(l1) { l ->
            val claro = Color.White.copy(alpha = 0.2f); val hondo = Color(0xFF1A1426).copy(alpha = 0.2f)
            val g = (0.5f * u).coerceAtLeast(0.6f)
            // La trama: hilos finos a lo largo y a lo ancho, uno claro y uno hondo.
            for (j in 0 until 3) {
                val x = l * j / 3f
                drawLine(hondo.copy(alpha = 0.1f), Offset(x, 0f), Offset(x, l), g)
                drawLine(claro.copy(alpha = 0.1f), Offset(0f, x + l / 6f), Offset(l, x + l / 6f), g)
            }
            drawCircle(claro, 0.6f * u, Offset(l * 0.25f, l * 0.25f))
            drawCircle(claro, 0.6f * u, Offset(l * 0.75f, l * 0.75f))
            drawCircle(hondo, 0.4f * u, Offset(l * 0.75f, l * 0.25f))
        }
        val l2 = (11f * u).roundToInt().coerceAtLeast(4)
        shippo = baldosa(l2) { l ->
            // Los círculos encadenados del estampado (shippō): uno en cada esquina y uno en el centro, del mismo radio.
            val claro = Color.White.copy(alpha = 0.3f); val g = (0.8f * u).coerceAtLeast(0.7f)
            for (c in listOf(Offset(0f, 0f), Offset(l, 0f), Offset(0f, l), Offset(l, l), Offset(l / 2, l / 2))) drawCircle(claro, l * 0.5f, c, style = Stroke(g))
            drawCircle(Color(0xFF1A1426).copy(alpha = 0.25f), l * 0.07f, Offset(l / 2, 0f))
            drawCircle(Color(0xFF1A1426).copy(alpha = 0.25f), l * 0.07f, Offset(0f, l / 2))
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

/** El bulto de un cuerpo de [l] a [r]: el plano de luz del lado de donde viene, el de sombra del otro, y los filos de noche. */
private fun DrawScope.bultoEdo(a: AireEdo, k: Float, f: Float, hacia: Float, l: Float, r: Float, arriba: Float, abajo: Float) {
    val u = density; val w = r - l
    val alto = abajo - arriba
    val luzIzq = !a.oscuro || hacia < 0f
    drawRect(a.brillo(0.24f, k), Offset(if (luzIzq) l + w * 0.08f else l + w * 0.7f, arriba), Size(w * 0.18f, alto))
    drawRect(a.brillo(0.16f, k), Offset(if (luzIzq) l + w * 0.14f else l + w * 0.75f, arriba), Size(w * 0.06f, alto))
    drawRect(a.sombra(0.18f), Offset(if (luzIzq) l + w * 0.6f else l, arriba), Size(w * 0.4f, alto))
    drawRect(a.sombra(0.22f), Offset(if (luzIzq) l + w * 0.86f else l, arriba), Size(w * 0.14f, alto))
    if (a.oscuro) {
        if (k > 0.06f) drawRect(a.brillo(0.75f, k), Offset(if (hacia > 0) r - 1.2f * u else l, arriba), Size(1.2f * u, alto))
        if (a.lunar && f > 0.08f) drawRect(a.helado(0.45f * f), Offset(if (hacia > 0) l else r - 1f * u, arriba), Size(1f * u, alto))
    }
}

/** La sombra que un volumen echa en el fondo y en la tabla: de día a la derecha, de noche lejos del farol. */
private fun DrawScope.sombraArrojadaEdo(a: AireEdo, hacia: Float, arriba: Float) {
    val u = density; val w = size.width; val h = size.height
    val lado = if (hacia < 0f) 1f else -1f
    val x = if (lado > 0) w else 0f
    val largo = (if (a.oscuro) 8f else 7f) * u
    drawPath(poligonoEdo(x, h, x, arriba + (h - arriba) * 0.12f, x + lado * largo, arriba + (h - arriba) * 0.24f, x + lado * largo, h), a.sombra(if (a.oscuro) 0.4f else 0.3f))
    drawRect(a.sombra(0.45f), Offset(if (lado > 0) 0f else -largo * 0.6f, h - 1.6f * u), Size(w + largo * 0.6f, 1.6f * u))
}

/** El sello cuadrado de bermellón (sin signo: un marco con un punto, que no es letra) o, en los Sueños, la luna creciente. */
private fun DrawScope.selloEdo(c: Offset, s: Float, luna: Boolean, a: AireEdo, k: Float, fondo: Color) {
    val u = density
    if (luna) {
        drawCircle(if (a.oscuro) mezcla(LUNA_EDO, a.hondo, 0.15f - 0.15f * k) else mezcla(LUNA_EDO, AI, 0.1f), s * 0.52f, c)
        drawCircle(fondo, s * 0.43f, c + Offset(s * 0.24f, -s * 0.12f))
        return
    }
    drawRect(a.luz(SHU, maxOf(k, 0.35f)), Offset(c.x - s / 2, c.y - s / 2), Size(s, s))
    val marca = fondo.copy(alpha = 0.85f)
    drawRect(marca, Offset(c.x - s * 0.3f, c.y - s * 0.3f), Size(s * 0.6f, s * 0.6f), style = Stroke(0.6f * u))
    drawCircle(marca, s * 0.1f, c)
}

/** Una etiqueta de papel o de madera con el día grande, el mes en columna y el sello abajo. */
private fun DrawScope.diaEnEtiquetaEdo(v: Volumen, medidor: TextMeasurer, a: AireEdo, k: Float, r: Rect, fondo: Color, tamDia: Float) {
    val u = density
    val alturaDia = minOf(r.height * 0.4f, 20f * u)
    textoEn(medidor, v.dia, Rect(r.left, r.top + 1.2f * u, r.right, r.top + 1.2f * u + alturaDia), MuebleEstanteEdo.LETRA, a.tinta, tamDia, peso = FontWeight.Bold, minimo = 6f)
    val ss = minOf(r.width * 0.5f, 6f * u)
    val sello = Offset(r.center.x, r.bottom - ss * 0.8f)
    mesEdo(medidor, v.mes, Rect(r.left + 0.5f * u, r.top + 1.2f * u + alturaDia, r.right - 0.5f * u, sello.y - ss * 0.62f), a.tinta, 8f)
    selloEdo(sello, ss, v.noche, a, k, fondo)
}

// ---------------------------------------------------------------- el estuche

/**
 * El estuche de tela (chitsu), de pie: cartón forrado de algodón del color del ánimo, con su trama y su estampado menudo; los
 * cantos redondeados por el uso, donde la tela clarea; la solapa que da la vuelta con el grueso del cartón; los broches de
 * hueso (más broches cuanto más escribiste) con su brillo y sus presillas de hilo; la tira de papel pegada con el día, el mes y
 * el sello (en los Sueños, la luna); arriba, los marcadores de papel con su cordoncillo.
 */
private fun DrawScope.estucheEdo(v: Volumen, medidor: TextMeasurer, a: AireEdo, k: Float, f: Float, hacia: Float, tinte: Color?) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val tela = tinte?.let { a.animo(it, k, f) } ?: a.luz(CRUDO_EDO, k, f)
    val hondo = mezcla(tela, a.hondo, 0.5f)
    val claro = mezcla(tela, if (a.oscuro) EdoTinta.LLAMA else Color.White, 0.3f)
    val top = 6f * u
    val der = w - 4f * u
    sombraArrojadaEdo(a, hacia, top)
    // Los marcadores que asoman por arriba: uno, o hasta cuatro si escribiste mucho, cada uno con su cordoncillo.
    val marcas = 1 + (v.lleno * 3).toInt()
    for (j in 0 until marcas) {
        val x = w * (0.16f + 0.17f * j + 0.05f * azar(s, 20 + j))
        val alto = (4f + 4f * azar(s, 25 + j)) * u
        drawRect(a.sombra(0.3f), Offset(x + 1f * u, top - alto + 1f * u), Size(3f * u, alto))
        drawRect(a.etiqueta(k, if (j % 2 == 0) WASHI else mezcla(WASHI, KAKI, 0.25f)), Offset(x, top - alto), Size(3f * u, alto + 1f * u))
        drawLine(a.luz(listOf(SHU, AI, MATCHA)[j % 3], k, f), Offset(x + 1.5f * u, top - alto), Offset(x + 1.5f * u + (azar(s, 28 + j) - 0.5f) * 4f * u, top - alto - 3f * u), 0.8f * u)
    }
    val cuerpo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(0f, top, der, h), CornerRadius(3f * u))) }
    drawPath(cuerpo, tela)
    clipPath(cuerpo) {
        telaEdo(TelasEdo.komon(u), Rect(0f, top, der, h), if (a.oscuro) 0.6f else 0.9f)
        // Los cantos de arriba y abajo, redondeados por el uso: la tela clarea donde roza el cartón.
        drawRect(claro.copy(alpha = 0.55f), Offset(0f, top), Size(der, 2f * u))
        drawRect(hondo.copy(alpha = 0.55f), Offset(0f, top + 2f * u), Size(der, 0.8f * u))
        drawRect(hondo.copy(alpha = 0.65f), Offset(0f, h - 3f * u), Size(der, 3f * u))
        drawRect(claro.copy(alpha = 0.35f), Offset(0f, h - 3.8f * u), Size(der, 0.8f * u))
        // La solapa: su orilla de pie, con el grueso del cartón (sombra a un lado, luz en el canto).
        val xs = der * 0.7f
        drawRect(a.sombra(0.26f), Offset(xs - 3f * u, top), Size(3f * u, h - top))
        drawRect(hondo, Offset(xs, top), Size(1f * u, h - top))
        drawRect(claro.copy(alpha = 0.7f), Offset(xs + 1f * u, top), Size(0.9f * u, h - top))
        // El uso: una mancha de manos y el roce que aclaró la tela abajo.
        drawOval(hondo.copy(alpha = 0.22f), Offset(der * (0.15f + 0.3f * azar(s, 3)), h * (0.66f + 0.1f * azar(s, 4))), Size(der * 0.42f, h * 0.08f))
        drawOval(claro.copy(alpha = 0.25f), Offset(der * 0.05f, h * 0.88f), Size(der * 0.5f, h * 0.06f))
        bultoEdo(a, k, f, hacia, 0f, der, top, h)
        // El brillo del algodón satinado, fino y largo, en la cresta del bulto.
        drawRect(a.brillo(if (a.oscuro) 0.3f else 0.35f, k), Offset(der * 0.19f, top), Size(1f * u, h - top))
    }
    // Las esquinas gastadas, donde asoma el cartón.
    listOf(Offset(0f, top), Offset(0f, h)).forEach { e ->
        val dy = if (e.y > top + 1f) -1f else 1f
        drawPath(poligonoEdo(e.x, e.y + dy * 4f * u, e.x, e.y, e.x + 4f * u, e.y), a.luz(KAMI, k, f).copy(alpha = 0.7f))
    }
    // Los broches de hueso en el canto derecho, con su presilla de hilo que viene de la solapa.
    val broches = if (v.lleno > 0.6f) 3 else 2
    for (j in 0 until broches) {
        val y = top + (h - top) * (0.2f + 0.6f * j / (broches - 1))
        drawLine(a.luz(mezcla(WASHI, KAMI, 0.3f), k, f), Offset(der * 0.72f, y - 1.2f * u), Offset(der - 1f * u, y - 1.2f * u), 0.8f * u)
        drawLine(a.luz(mezcla(WASHI, KAMI, 0.3f), k, f), Offset(der * 0.72f, y + 1.2f * u), Offset(der - 1f * u, y + 1.2f * u), 0.8f * u)
        val b = Rect(der - 2.6f * u, y - 4.6f * u, der + 2.6f * u, y + 4.6f * u)
        drawOval(a.sombra(0.4f), b.topLeft + Offset(1f * u, 1f * u), b.size)
        drawOval(a.luz(mezcla(WASHI, KAMI, 0.4f), k + 0.1f, f), b.topLeft, b.size)
        drawOval(a.sombra(0.25f), Offset(b.center.x, b.top + 1f * u), Size(b.width * 0.45f, b.height - 2f * u))
        drawLine(a.brillo(0.75f, maxOf(k, 0.4f)), Offset(b.left + 1.4f * u, b.top + 2f * u), Offset(b.left + 1.4f * u, b.bottom - 2f * u), 0.8f * u)
    }
    // La tira de papel con el día y el mes, pegada arriba a la izquierda, con su orilla un poco levantada.
    val tira = Rect(w * 0.1f, top + (h - top) * 0.06f, minOf(der * 0.64f, w * 0.1f + 22f * u), top + (h - top) * 0.7f)
    drawRect(a.sombra(0.35f), tira.topLeft + Offset(1f * u, 1.2f * u), tira.size)
    val papel = a.etiqueta(k, WASHI)
    drawRect(papel, tira.topLeft, tira.size)
    drawRect(a.sombra(0.12f), Offset(tira.right - tira.width * 0.18f, tira.top), Size(tira.width * 0.18f, tira.height))
    drawRect(a.luz(mezcla(WASHI, KAMI, 0.7f), k, f), tira.topLeft, tira.size, style = Stroke(0.6f * u))
    drawPath(poligonoEdo(tira.right - 5f * u, tira.top, tira.right, tira.top, tira.right, tira.top + 5f * u), a.luz(mezcla(WASHI, KAMI, 0.5f), k, f))
    diaEnEtiquetaEdo(v, medidor, a, k, tira, papel, 13f)
}

// ---------------------------------------------------------------- la pila de cuadernos

/**
 * La pila de cuadernos cosidos que presta el librero, tumbados uno sobre otro (más cuadernos cuanto más escribiste): de cada
 * uno se ve el lomo, con su tapa de papel teñido del color del ánimo (cada uno de un tinte un poco distinto), las esquinas
 * gastadas, el canto de las hojas y las cuatro puntadas del cosido, que dan la vuelta al lomo con su hilo; arriba, la tapa del
 * de encima; los ata un cordón de papel torcido con su nudo, del que cuelga a un lado la etiqueta con el día y el mes.
 */
private fun DrawScope.pilaCuadernosEdo(v: Volumen, medidor: TextMeasurer, a: AireEdo, k: Float, f: Float, hacia: Float, tinte: Color?) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val tela = tinte?.let { a.animo(it, k, f) } ?: a.luz(GRIS_EDO, k, f)
    val n = (3 + (v.lleno * 4).toInt()).coerceIn(3, 7)
    val arriba = 4.5f * u
    sombraArrojadaEdo(a, hacia, arriba)
    // La etiqueta cuelga a la derecha: las puntadas se ven a su izquierda.
    val tw = (w * 0.54f).coerceIn(16f * u, 22f * u).coerceAtMost(w - 3f * u)
    val th = minOf(h * 0.8f, 42f * u)
    val cx = w - tw / 2 - 1.5f * u
    val limite = cx - tw / 2 - 1f * u
    val hojas = a.luz(mezcla(WASHI, KAMI, 0.35f), k, f)
    val hojasOsc = a.luz(mezcla(KAMI, KURI_OSC, 0.25f), k, f)
    val hilo = mezcla(tela, a.luz(mezcla(WASHI, KAMI, 0.3f), k, f), 0.6f)
    // El grueso de cada cuaderno, desigual, y lo que se corre cada uno: la pila no es un bloque.
    val gruesos = List(n) { 0.8f + 0.4f * azar(s, 30 + it) }
    val total = gruesos.sum()
    var y1 = h
    var izqTop = 0f; var derTop = w
    for (i in 0 until n) {
        val banda = (h - arriba) * gruesos[i] / total
        val y0 = y1 - banda
        val corre = (azar(s, 40 + i) - 0.5f) * w * 0.16f
        val l = maxOf(0f, corre) + 0.5f * u; val r = w + minOf(0f, corre) - 1f * u
        if (i == n - 1) { izqTop = l; derTop = r }
        val tapa = mezcla(tela, if (azar(s, 50 + i) > 0.5f) a.hondo else Color.White, 0.04f + 0.14f * azar(s, 60 + i))
        val hondoT = mezcla(tapa, a.hondo, 0.45f)
        val claroT = mezcla(tapa, if (a.oscuro) EdoTinta.LLAMA else Color.White, 0.3f)
        rotate((azar(s, 70 + i) - 0.5f) * 2.6f, Offset((l + r) / 2, (y0 + y1) / 2)) {
            // Las dos tapas de papel teñido y, entre ellas, el canto de las hojas, un poco remetido, con sus hojas.
            drawRoundRect(tapa, Offset(l, y0 + 0.4f * u), Size(r - l, banda - 0.4f * u), CornerRadius(2.2f * u))
            val ha = y0 + banda * 0.36f; val hb = y1 - banda * 0.34f
            drawRect(hojas, Offset(l + 1.6f * u, ha), Size(r - l - 3.2f * u, hb - ha))
            val hojitas = ((hb - ha) / (1.3f * u)).toInt().coerceIn(1, 8)
            for (j in 1..hojitas) {
                val yy = ha + (hb - ha) * j / (hojitas + 1)
                drawLine(hojasOsc.copy(alpha = 0.55f), Offset(l + 1.6f * u, yy), Offset(r - 1.6f * u - (azar(s, 80 + i * 9 + j) * 3f * u), yy), 0.5f * u)
            }
            drawRect(a.sombra(0.3f), Offset(l + 1.6f * u, ha), Size(r - l - 3.2f * u, 0.9f * u))
            // La luz en el canto de arriba de cada tapa, la sombra del de encima y las esquinas gastadas.
            drawRect(claroT.copy(alpha = 0.8f), Offset(l + 1.5f * u, y0 + 0.6f * u), Size(r - l - 3f * u, 0.9f * u))
            drawRect(claroT.copy(alpha = 0.5f), Offset(l + 1.5f * u, hb + 0.4f * u), Size(r - l - 3f * u, 0.7f * u))
            drawRect(a.sombra(0.42f), Offset(l, y1 - 1.2f * u), Size(r - l, 1.2f * u))
            drawRect(mezcla(tapa, KAMI, 0.5f), Offset(l, y0 + 0.8f * u), Size(1.4f * u, banda - 1.6f * u))
            drawRect(hondoT.copy(alpha = 0.5f), Offset(r - 1.6f * u, y0 + 0.8f * u), Size(1.6f * u, banda - 1.6f * u))
            // Las fibras del papel de la tapa.
            for (j in 0 until 3) {
                val xx = l + (r - l) * azar(s, 90 + i * 3 + j)
                drawLine(claroT.copy(alpha = 0.4f), Offset(xx, y0 + banda * 0.12f), Offset(xx + 2.4f * u, y0 + banda * 0.14f), 0.5f * u)
            }
            // Las cuatro puntadas del cosido, a la izquierda de la etiqueta: el hilo sale por un agujero de la tapa de arriba,
            // da la vuelta al lomo con su relieve (luz a un lado, sombra al otro) y entra por la de abajo.
            val lim = minOf(r - 2f * u, limite)
            for (j in 0 until 4) {
                val x = l + (lim - l) * (0.14f + 0.24f * j)
                if (x > lim) continue
                drawLine(a.sombra(0.4f), Offset(x + 0.9f * u, y0 + banda * 0.1f), Offset(x + 0.9f * u, y1 - banda * 0.1f), 1.2f * u, StrokeCap.Round)
                drawLine(hilo, Offset(x, y0 + banda * 0.08f), Offset(x, y1 - banda * 0.1f), 1.2f * u, StrokeCap.Round)
                drawLine(a.brillo(0.6f, maxOf(k, 0.3f)), Offset(x - 0.5f * u, y0 + banda * 0.1f), Offset(x - 0.5f * u, y1 - banda * 0.14f), 0.5f * u)
                drawCircle(hondoT, 0.9f * u, Offset(x, y0 + banda * 0.08f))
                drawLine(hilo, Offset(x - 1.4f * u, y0 + 0.8f * u), Offset(x + 1.4f * u, y0 + 0.8f * u), 0.9f * u)
            }
        }
        y1 = y0
    }
    // La tapa del de arriba, vista un poco desde arriba, con la tira del título (en blanco: el libro es del librero).
    val tapaArriba = mezcla(tela, Color.White, 0.14f)
    drawRoundRect(tapaArriba, Offset(izqTop + 1f * u, 0.5f * u), Size(derTop - izqTop - 2f * u, arriba), CornerRadius(1.6f * u))
    drawRect(a.etiqueta(k, WASHI), Offset(izqTop + (derTop - izqTop) * 0.12f, 1.2f * u), Size((derTop - izqTop) * 0.18f, arriba - 1.6f * u))
    drawRect(a.brillo(0.3f, k), Offset(izqTop + 2f * u, 0.8f * u), Size((derTop - izqTop) * 0.5f, 1f * u))
    bultoEdo(a, k, f, hacia, 0f, w, arriba, h)
    // El cordón de papel torcido que la ata, a un lado, y su nudo encima.
    val cordon = a.luz(mezcla(WASHI, KAMI, 0.25f), k + 0.1f, f)
    drawLine(a.sombra(0.35f), Offset(cx + 1.2f * u, 0f), Offset(cx + 1.2f * u, h), 1.8f * u)
    drawLine(cordon, Offset(cx, 0f), Offset(cx, h), 1.6f * u)
    var y = 2f * u
    while (y < h) { drawLine(a.sombra(0.25f), Offset(cx - 0.8f * u, y), Offset(cx + 0.8f * u, y + 1.4f * u), 0.5f * u); y += 2.8f * u }
    drawCircle(cordon, 2.6f * u, Offset(cx, 2f * u))
    drawCircle(a.brillo(0.5f, k), 1f * u, Offset(cx - 0.8f * u, 1.2f * u))
    drawLine(cordon, Offset(cx, 2f * u), Offset(cx - 5f * u, 6f * u), 1.1f * u, StrokeCap.Round)
    // La etiqueta, grande, colgada del nudo a la derecha, con el día que se lee de un vistazo.
    val cuelga = Offset(cx, 4f * u)
    rotate(-2f + 4f * azar(s, 70), cuelga) {
        val et = Rect(cuelga.x - tw / 2, cuelga.y + 2.4f * u, cuelga.x + tw / 2, cuelga.y + 2.4f * u + th)
        drawRect(a.sombra(0.45f), et.topLeft + Offset(1.2f * u, 1.4f * u), et.size)
        val papel = a.etiqueta(k, WASHI)
        drawRect(papel, et.topLeft, et.size)
        drawRect(a.sombra(0.12f), Offset(et.right - tw * 0.18f, et.top), Size(tw * 0.18f, th))
        drawRect(a.luz(mezcla(WASHI, KAMI, 0.7f), k, f), et.topLeft, et.size, style = Stroke(0.5f * u))
        drawCircle(a.sombra(0.55f), 1f * u, Offset(cuelga.x, et.top + 2f * u))
        diaEnEtiquetaEdo(v, medidor, a, k, Rect(et.left, et.top + 3f * u, et.right, et.bottom), papel, 16f)
    }
}

// ---------------------------------------------------------------- el hatillo

/**
 * El hatillo de furoshiki: el paño cuadrado estampado (círculos encadenados, del color del ánimo) que envuelve el bulto de las
 * cartas y se anuda arriba con dos puntas en orejas, que dejan ver el revés más claro del paño; panzón (más cuanto más
 * escribiste) y bajo, con los pliegues que tiran hacia el nudo, el nudo con su volumen y el doblez de abajo. Atada al nudo, la
 * tablilla de madera de cinco lados de los bultos de la posta, con el día, el mes y el sello.
 */
private fun DrawScope.hatilloEdo(v: Volumen, medidor: TextMeasurer, a: AireEdo, k: Float, f: Float, hacia: Float, tinte: Color?) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val tela = tinte?.let { a.animo(it, k, f) } ?: a.luz(CRUDO_EDO, k, f)
    val hondo = mezcla(tela, a.hondo, 0.5f)
    val claro = mezcla(tela, if (a.oscuro) EdoTinta.LLAMA else Color.White, 0.3f)
    val reves = mezcla(tela, a.luz(WASHI, k, f), 0.45f)
    val cx = w * (0.48f + 0.06f * (azar(s, 120) - 0.5f))
    val nudoY = h * 0.24f
    val hombro = h * 0.3f
    val panza = 0.02f + 0.06f * v.lleno
    sombraArrojadaEdo(a, hacia, hombro)
    val cuerpo = Path().apply {
        moveTo(cx - w * 0.16f, nudoY + h * 0.03f)
        cubicTo(cx - w * 0.38f, hombro, -w * panza, h * 0.42f, -w * panza * 0.5f, h * 0.72f)
        cubicTo(0f, h * 0.96f, w * 0.12f, h, w * 0.3f, h)
        lineTo(w * 0.7f, h)
        cubicTo(w * 0.88f, h, w, h * 0.96f, w * (1f + panza * 0.5f), h * 0.72f)
        cubicTo(w * (1f + panza), h * 0.42f, cx + w * 0.38f, hombro, cx + w * 0.16f, nudoY + h * 0.03f)
        close()
    }
    drawPath(cuerpo, tela)
    clipPath(cuerpo) {
        telaEdo(TelasEdo.shippo(u), Rect(0f, nudoY, w, h), if (a.oscuro) 0.55f else 0.9f)
        // La luz de arriba (donde el paño se tensa sobre el bulto) y la sombra de abajo, donde se recoge.
        drawOval(claro.copy(alpha = 0.3f), Offset(w * 0.1f, hombro), Size(w * 0.55f, h * 0.3f))
        drawRect(hondo.copy(alpha = 0.35f), Offset(0f, h * 0.78f), Size(w, h * 0.22f))
        // Los pliegues que tiran hacia el nudo, con su lado de luz.
        for (j in 0 until 3) {
            for (lado in listOf(-1f, 1f)) {
                val x1 = cx + lado * w * (0.22f + 0.15f * j)
                val y1 = h * (0.55f + 0.14f * j + 0.04f * azar(s, 130 + j))
                val pl = Path().apply { moveTo(cx + lado * w * 0.08f, nudoY + h * 0.05f); quadraticTo(cx + lado * w * (0.2f + 0.1f * j), h * (0.42f + 0.06f * j), x1, y1) }
                drawPath(pl, hondo.copy(alpha = 0.6f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
                drawPath(corridoEdo(pl, -1.2f * u, 0f), claro.copy(alpha = 0.45f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
            }
        }
        // El doblez de abajo, donde el paño da la vuelta al bulto, con su orilla.
        val doblez = Path().apply { moveTo(w * 0.02f, h * 0.8f); quadraticTo(w * 0.5f, h * (0.88f + 0.04f * azar(s, 136)), w * 0.98f, h * 0.78f) }
        drawPath(doblez, hondo.copy(alpha = 0.6f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        drawPath(corridoEdo(doblez, 0f, -1.2f * u), claro.copy(alpha = 0.4f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        bultoEdo(a, k, f, hacia, 0f, w, hombro, h)
    }
    // Las dos orejas del nudo: las puntas del paño, una hacia cada lado, que dejan ver su revés más claro.
    // Asimétricas, como quedan al anudar: la de la izquierda cae de lado; la de la derecha, más corta, se levanta.
    val orejaI = Path().apply {
        moveTo(cx - w * 0.05f, nudoY + h * 0.03f)
        quadraticTo(cx - w * 0.26f, nudoY + h * 0.05f, cx - w * 0.44f, nudoY - h * 0.005f)
        quadraticTo(cx - w * 0.3f, nudoY - h * 0.12f, cx + w * 0.02f, nudoY - h * 0.04f)
        close()
    }
    val orejaD = Path().apply {
        moveTo(cx + w * 0.04f, nudoY + h * 0.01f)
        quadraticTo(cx + w * 0.22f, nudoY - h * 0.06f, cx + w * 0.3f, nudoY - h * 0.16f)
        quadraticTo(cx + w * 0.1f, nudoY - h * 0.1f, cx - w * 0.02f, nudoY - h * 0.03f)
        close()
    }
    listOf(orejaI to Offset(cx - w * 0.4f, nudoY), orejaD to Offset(cx + w * 0.27f, nudoY - h * 0.14f)).forEachIndexed { i, (o, punta) ->
        drawPath(corridoEdo(o, 1f * u, 1.4f * u), a.sombra(0.3f))
        drawPath(o, if (i == 0) mezcla(tela, a.hondo, 0.15f) else tela)
        clipPath(o) {
            telaEdo(TelasEdo.shippo(u), Rect(0f, 0f, w, nudoY + h * 0.05f), if (a.oscuro) 0.5f else 0.85f)
            // El revés, más claro, en la vuelta de la punta.
            drawCircle(reves, w * 0.09f, punta + Offset(if (i == 0) w * 0.04f else -w * 0.04f, h * 0.025f))
        }
        drawPath(o, hondo.copy(alpha = 0.6f), style = Stroke(0.6f * u))
    }
    // El nudo, con su volumen: dos lazadas apretadas, cada una con su luz y su sombra.
    val nudo = Rect(cx - w * 0.15f, nudoY - h * 0.055f, cx + w * 0.15f, nudoY + h * 0.065f)
    drawOval(a.sombra(0.35f), nudo.topLeft + Offset(1f * u, 2f * u), nudo.size)
    drawOval(mezcla(tela, a.hondo, 0.2f), nudo.topLeft, nudo.size)
    clipRect(nudo.left, nudo.top, nudo.right, nudo.bottom) {
        drawOval(mezcla(tela, a.hondo, 0.05f), Offset(nudo.left, nudo.top), Size(nudo.width * 0.6f, nudo.height))
        drawOval(claro.copy(alpha = 0.55f), Offset(nudo.left + nudo.width * 0.1f, nudo.top + nudo.height * 0.12f), Size(nudo.width * 0.3f, nudo.height * 0.35f))
        drawOval(hondo.copy(alpha = 0.45f), Offset(nudo.left + nudo.width * 0.55f, nudo.top + nudo.height * 0.3f), Size(nudo.width * 0.4f, nudo.height * 0.6f))
    }
    drawLine(hondo, Offset(cx + w * 0.02f, nudo.top + 1f * u), Offset(cx - w * 0.01f, nudo.bottom - 1f * u), 1f * u)
    // La tablilla de madera de cinco lados, atada al nudo con un cordel, con el día, el mes y el sello (en los Sueños, la luna).
    val tw = (w * 0.56f).coerceIn(16f * u, 22f * u).coerceAtMost(w - 3f * u)
    val th = minOf(h * 0.66f, 40f * u)
    val cuelga = Offset(minOf(cx + w * 0.16f, w - tw / 2 - 1f * u), nudoY + h * 0.06f)
    val cordel = a.luz(mezcla(WARA, WASHI, 0.3f), k, f)
    rotate(4f - 6f * azar(s, 110), cuelga) {
        val t0 = cuelga.y + 3f * u
        val pico = tw * 0.26f
        val forma = poligonoEdo(cuelga.x - tw / 2, t0 + pico, cuelga.x, t0, cuelga.x + tw / 2, t0 + pico, cuelga.x + tw / 2, t0 + th, cuelga.x - tw / 2, t0 + th)
        drawLine(cordel, Offset(cuelga.x - 2f * u, cuelga.y - 2f * u), Offset(cuelga.x, t0 + pico * 0.6f), 0.9f * u)
        drawPath(corridoEdo(forma, 1.2f * u, 1.5f * u), a.sombra(0.45f))
        val madera = a.etiqueta(k, mezcla(HINOKI, WASHI, 0.45f))
        drawPath(forma, madera)
        drawRect(a.sombra(0.12f), Offset(cuelga.x + tw * 0.3f, t0 + pico * 0.6f), Size(tw * 0.2f, th - pico * 0.6f))
        for (j in 0 until 3) drawLine(a.luz(mezcla(HINOKI, KURI_OSC, 0.4f), k, f).copy(alpha = 0.3f), Offset(cuelga.x - tw * (0.35f - 0.25f * j), t0 + pico + 2f * u), Offset(cuelga.x - tw * (0.33f - 0.25f * j), t0 + th - 1f * u), 0.5f * u)
        drawPath(forma, a.luz(mezcla(HINOKI, KURI_OSC, 0.5f), k, f), style = Stroke(0.6f * u))
        drawCircle(a.sombra(0.55f), 1f * u, Offset(cuelga.x, t0 + pico * 0.6f))
        diaEnEtiquetaEdo(v, medidor, a, k, Rect(cuelga.x - tw / 2, t0 + pico * 0.85f, cuelga.x + tw / 2, t0 + th), madera, 16f)
    }
    if (a.oscuro && k > 0.06f) drawLine(a.brillo(0.6f, k), Offset(w * (1f + panza * 0.4f) - 1f * u, h * 0.45f), Offset(w * (1f + panza * 0.4f) - 1f * u, h * 0.85f), 1f * u)
}
