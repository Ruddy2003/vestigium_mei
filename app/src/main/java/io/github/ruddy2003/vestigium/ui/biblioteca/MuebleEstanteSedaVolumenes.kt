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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.ALAMO
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.ALAMO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.ANIL
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.ARCILLA
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.BERMELLON
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.BRONCE
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.CUERO
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.FIELTRO
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.HUESO
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.PAJA
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.PAPEL_VIEJO
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import kotlin.math.roundToInt

// Los volúmenes del almacén de la Ruta de la Seda (MuebleEstanteSeda): tres objetos del mercader, según la semilla.

/** El rollo, el fajo y la bolsa sin ánimo marcado: seda cruda, cáñamo sin teñir y cuero al natural. */
private val CRUDA_SEDA = Color(0xFFCDB894)
private val CANAMO_SEDA = Color(0xFFB4A68A)
private val LUNA_SEDA = Color(0xFFEDE6C8)

internal fun DrawScope.volumenSeda(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
    if (size.width < 2f || size.height < 2f) return
    val a = AireSeda(oscuro, v.noche)
    val fx = MuebleEstanteSeda.LADO + v.centro * (1f - 2 * MuebleEstanteSeda.LADO)
    val pos = v.repisa + 0.55f
    val k = if (a.lumbre) cercaCuerpoSeda(pos, v.repisas, fx, a.lunar) else 1f
    val f = if (a.oscuro) frioCuerpoSeda(pos, v.repisas, fx) else 0f
    // De dónde viene la luz cálida: la lámpara (a la derecha de casi todo).
    val hacia = if (a.lumbre) (if (MuebleEstanteSeda.LAMP_X >= fx) 1f else -1f) else -1f
    when (MuebleEstanteSeda.clase(v.semilla)) {
        0 -> rolloSeda(v, medidor, a, k, f, hacia)
        1 -> fajoSeda(v, medidor, a, k, f, hacia)
        else -> bolsaSeda(v, medidor, a, k, f, hacia)
    }
}

/** El mes en columna, signo bajo signo, como se escribe en vertical; si es largo, de abajo arriba a lo largo. */
private fun DrawScope.mesSeda(medidor: TextMeasurer, mes: String, r: Rect, color: Color, tam: Float) {
    if (r.height < 6f || r.width < 3f) return
    val signos = mes.filter { !it.isWhitespace() }
    if (signos.length in 1..4) {
        // Una sola composición para toda la columna (un signo por renglón): se mide y se dibuja una vez.
        val porAlto = r.height / signos.length / density / 1.1f
        val t = minOf(tam, porAlto, r.width / density / 1.05f).coerceAtLeast(3.5f)
        val estilo = TextStyle(fontFamily = MuebleEstanteSeda.LETRA, fontSize = t.sp, lineHeight = (t * 1.1f).sp, color = color, textAlign = TextAlign.Center)
        val capa = medidor.measure(signos.toList().joinToString("\n"), estilo)
        drawText(capa, topLeft = Offset(r.center.x - capa.size.width / 2f, r.center.y - capa.size.height / 2f))
    } else {
        textoEn(medidor, mes, r, MuebleEstanteSeda.LETRA, color, tam * 0.9f, vertical = true, minimo = 4.5f)
    }
}

/**
 * Las baldosas de las telas, pintadas una sola vez (y otra si cambia la densidad): el labrado de la seda (una red de rombos
 * con una flor en cada uno, en claro y en hondo) y la trama basta del paño. Se pegan en mosaico: es mucho más barato que
 * trazar cada hilo en cada volumen.
 */
private object TelasSeda {
    private var densidad = 0f
    private var seda: ImageBitmap? = null
    private var pano: ImageBitmap? = null
    fun seda(u: Float): ImageBitmap { if (u != densidad || seda == null) rehacer(u); return seda!! }
    fun pano(u: Float): ImageBitmap { if (u != densidad || pano == null) rehacer(u); return pano!! }
    private fun baldosa(lado: Int, pintar: DrawScope.(Float) -> Unit): ImageBitmap {
        val img = ImageBitmap(lado, lado)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(lado.toFloat(), lado.toFloat())) { pintar(lado.toFloat()) }
        return img
    }
    private fun rehacer(u: Float) {
        densidad = u
        val l1 = (15f * u).roundToInt().coerceAtLeast(4)
        seda = baldosa(l1) { l ->
            // Medallones de perlas al tresbolillo (el labrado de las sedas de Sogdiana): un anillo de perlas claras, dentro una
            // flor de cuatro pétalos en hondo, y entre medallón y medallón una estrellita.
            val claro = Color.White.copy(alpha = 0.26f); val hondo = Color(0xFF1A1426).copy(alpha = 0.26f)
            val rm = l * 0.2f
            for (c in listOf(Offset(l * 0.25f, l * 0.25f), Offset(l * 0.75f, l * 0.75f))) {
                for (j in 0 until 10) {
                    val ang = 6.283f * j / 10
                    drawCircle(claro, 0.55f * u, c + Offset(rm * kotlin.math.cos(ang), rm * kotlin.math.sin(ang)))
                }
                drawCircle(hondo, rm * 0.72f, c)
                for (j in 0 until 4) {
                    val ang = 1.5708f * j
                    drawCircle(claro, rm * 0.2f, c + Offset(rm * 0.34f * kotlin.math.cos(ang), rm * 0.34f * kotlin.math.sin(ang)))
                }
            }
            for (c in listOf(Offset(l * 0.75f, l * 0.25f), Offset(l * 0.25f, l * 0.75f))) {
                drawLine(hondo, c - Offset(rm * 0.4f, 0f), c + Offset(rm * 0.4f, 0f), (0.6f * u).coerceAtLeast(0.6f))
                drawLine(hondo, c - Offset(0f, rm * 0.4f), c + Offset(0f, rm * 0.4f), (0.6f * u).coerceAtLeast(0.6f))
            }
        }
        val l2 = (11.2f * u).roundToInt().coerceAtLeast(4)
        pano = baldosa(l2) { l ->
            val g = (0.5f * u).coerceAtLeast(0.6f)
            for (j in 0 until 4) {
                val x = l * j / 4f
                drawLine(Color(0xFF1A1426).copy(alpha = 0.16f), Offset(x, 0f), Offset(x, l), g)
                drawLine(Color.White.copy(alpha = 0.14f), Offset(0f, x), Offset(l, x), g)
            }
        }
    }
}

/** Una tela en mosaico sobre [r] (ya recortado por quien llama). */
private fun DrawScope.telaSeda(b: ImageBitmap, r: Rect, alfa: Float) {
    var y = r.top
    while (y < r.bottom) {
        var x = r.left
        while (x < r.right) { drawImage(b, Offset(x, y), alpha = alfa); x += b.width }
        y += b.height
    }
}

/** El cuerpo de un volumen con su bulto: la luz del lado de la ventana (o de la lámpara) y la sombra del otro, en dos pasos. */
private fun DrawScope.bultoSeda(a: AireSeda, k: Float, f: Float, hacia: Float, arriba: Float, abajo: Float) {
    val u = density; val w = size.width
    val alto = abajo - arriba
    val luzIzq = !a.oscuro || hacia < 0f
    val xClaro = if (luzIzq) w * 0.12f else w * 0.66f
    val xSombra = if (luzIzq) w * 0.66f else 0f
    drawRect(a.claro(0.26f), Offset(xClaro, arriba), Size(w * 0.18f, alto))
    drawRect(a.sombra(0.16f), Offset(xSombra, arriba), Size(w * 0.34f, alto))
    drawRect(a.sombra(0.2f), Offset(if (luzIzq) w * 0.86f else 0f, arriba), Size(w * 0.14f, alto))
    if (a.oscuro) {
        // El filo cálido del lado de la lámpara y el frío de la luna del otro.
        if (k > 0.08f) drawRect(FILO_MUEBLE.copy(alpha = (0.5f * k).coerceAtMost(0.5f)), Offset(if (hacia > 0) w - 1.1f * u else 0f, arriba), Size(1.1f * u, alto))
        if (f > 0.08f) drawRect(a.helado(0.45f * f), Offset(if (hacia > 0) 0f else w - 1f * u, arriba), Size(1f * u, alto))
    }
}

/**
 * La tablilla con el día, colgada de su cordel y un poco ladeada. [forma]: 0, la tira de marfil del rollo (estrecha y larga);
 * 1, la tablilla de álamo del fajo, con las esquinas cortadas; 2, la ficha redonda de hueso de la bolsa.
 */
private fun DrawScope.etiquetaDiaSeda(v: Volumen, medidor: TextMeasurer, a: AireSeda, k: Float, ojal: Offset, cuelga: Offset, base: Color, giro: Float, forma: Int) {
    val u = density; val w = size.width
    val an = when (forma) { 0 -> minOf(w * 0.62f, 18f * u); 2 -> minOf(w * 0.66f, 17f * u); else -> minOf(w * 0.74f, 23f * u) }
    val al = when (forma) { 0 -> 15f * u; 2 -> minOf(w * 0.66f, 17f * u); else -> 13f * u }
    val et = Rect(cuelga.x - an / 2, cuelga.y, cuelga.x + an / 2, cuelga.y + al)
    drawLine(a.luz(mezcla(PAJA, CUERO, 0.4f), k), ojal, Offset(cuelga.x, cuelga.y + 1.6f * u), 0.8f * u)
    val silueta = when (forma) {
        2 -> Path().apply { addOval(et) }
        1 -> Path().apply {
            val c = 2.4f * u
            moveTo(et.left + c, et.top); lineTo(et.right - c, et.top); lineTo(et.right, et.top + c); lineTo(et.right, et.bottom)
            lineTo(et.left, et.bottom); lineTo(et.left, et.top + c); close()
        }
        else -> Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(et, CornerRadius(1.6f * u))) }
    }
    rotate(giro, Offset(cuelga.x, cuelga.y)) {
        drawPath(corridoSeda(silueta, 1f * u, 1.2f * u), a.sombra(0.42f))
        drawPath(silueta, a.etiqueta(k, base))
        // La sombra del canto derecho y la luz del de arriba, dibujadas dentro de la silueta (sin recortes, que pesan).
        if (forma == 2) {
            drawArc(a.sombra(0.22f), -70f, 140f, false, Offset(et.left + 1.2f * u, et.top + 1.2f * u), Size(an - 2.4f * u, al - 2.4f * u), style = Stroke(2f * u))
            drawArc(a.claro(0.5f), 200f, 90f, false, Offset(et.left + 1f * u, et.top + 1f * u), Size(an - 2f * u, al - 2f * u), style = Stroke(0.8f * u))
        } else {
            val c = if (forma == 1) 2.4f * u else 1f * u
            drawRect(a.sombra(0.12f), Offset(et.right - an * 0.2f, et.top + c), Size(an * 0.2f - 0.4f * u, al - c - 0.6f * u))
            drawRect(a.claro(0.45f), Offset(et.left + 1.6f * u, et.top + 0.2f * u), Size(an - 3.2f * u, 0.8f * u))
            if (forma == 1) for (j in 0 until 3) drawLine(a.sombra(0.12f), Offset(et.left + 0.3f * u, et.top + al * (0.3f + 0.25f * j)), Offset(et.right - 0.3f * u, et.top + al * (0.32f + 0.25f * j)), 0.5f * u)
        }
        drawCircle(a.sombra(0.55f), 0.9f * u, Offset(cuelga.x, et.top + 2f * u))
        val texto = if (forma == 2) Rect(et.left + 1f * u, et.top + 3.4f * u, et.right - 1f * u, et.bottom - 1.8f * u) else Rect(et.left + 0.5f * u, et.top + 2.8f * u, et.right - 0.5f * u, et.bottom - 0.4f * u)
        textoEn(medidor, v.dia, texto, MuebleEstanteSeda.LETRA, a.tinta, 10.5f, peso = FontWeight.Bold, minimo = 6f)
    }
}

/** El sello del mercader en arcilla, con su impronta de cuatro pétalos; en los Sueños, la luna en la arcilla. */
private fun DrawScope.selloArcillaSeda(c: Offset, rs: Float, luna: Boolean, a: AireSeda, k: Float, semilla: Int) {
    val u = density
    val barro = a.luz(mezcla(ARCILLA, ALAMO, 0.15f * azar(semilla, 70)), maxOf(k, 0.2f))
    drawOval(a.sombra(0.4f), Offset(c.x - rs + 0.8f * u, c.y - rs * 0.82f + 1f * u), Size(rs * 2f, rs * 1.7f))
    drawOval(barro, Offset(c.x - rs * 1.04f, c.y - rs * 0.86f), Size(rs * 2.08f, rs * 1.72f))
    drawOval(barro, Offset(c.x - rs * 0.82f, c.y - rs), Size(rs * 1.6f, rs * 1.9f))
    drawArc(a.claro(0.45f), 170f, 110f, false, Offset(c.x - rs * 0.86f, c.y - rs * 0.76f), Size(rs * 1.72f, rs * 1.5f), style = Stroke(0.7f * u))
    val hondo = mezcla(barro, a.hondo, 0.38f)
    if (luna) {
        drawCircle(hondo, rs * 0.66f, c)
        drawCircle(if (a.oscuro) mezcla(LUNA_SEDA, a.hondo, 0.2f - 0.2f * k) else LUNA_SEDA, rs * 0.5f, c)
        drawCircle(hondo, rs * 0.42f, c + Offset(rs * 0.24f, -rs * 0.16f))
    } else {
        drawCircle(hondo, rs * 0.6f, c, style = Stroke(0.7f * u))
        for (j in 0 until 4) {
            val ang = 1.5708f * j + 0.785f
            drawCircle(hondo, rs * 0.17f, c + Offset(rs * 0.26f * kotlin.math.cos(ang), rs * 0.26f * kotlin.math.sin(ang)))
        }
    }
}

/**
 * El haz de rollos de papel envuelto en seda labrada (el color del ánimo), de pie: arriba asoman las cabezas de los rollos
 * (dos, o tres si escribiste mucho) con las vueltas del papel y la perilla de madera de cada rodillo; la envoltura da la
 * vuelta en diagonal con su orla tejida y la ciñe la cinta (más vueltas cuanto más escribiste); en ella, la tira de papel con
 * el mes en columna y el sello de bermellón (en los Sueños, la luna); de la cinta cuelga la tira de marfil con el día.
 */
private fun DrawScope.rolloSeda(v: Volumen, medidor: TextMeasurer, a: AireSeda, k: Float, f: Float, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val seda = v.tono?.let { a.animo(it, k, f) } ?: a.luz(CRUDA_SEDA, k, f)
    val hondoSeda = mezcla(seda, a.hondo, 0.42f)
    val claroSeda = mezcla(seda, if (a.oscuro) FILO_MUEBLE else Color.White, 0.22f)
    val yEnv = h * 0.15f
    val caida = h * 0.075f
    val papel = a.luz(PAPEL, k + 0.05f, f)
    val madera = a.luz(mezcla(ALAMO_OSC, ALAMO, 0.3f), k, f)
    // Las cabezas de los rollos, de atrás adelante: el papel enrollado, sus vueltas y la perilla del rodillo.
    val cabezas = if (v.lleno > 0.55f) 3 else 2
    val anc = if (cabezas == 2) w * 0.52f else w * 0.42f
    for (i in 0 until cabezas) {
        val cx = w * 0.27f + w * 0.46f * i / (cabezas - 1)
        val top = (3.5f + 7f * azar(s, 30 + i)) * u
        val ell = anc * 0.44f
        val fondo = yEnv + caida * (1f - cx / w) + 4f * u
        drawRect(papel, Offset(cx - anc / 2, top + ell / 2), Size(anc, fondo - top - ell / 2))
        drawRect(a.claro(0.3f), Offset(cx - anc * 0.36f, top + ell / 2), Size(anc * 0.16f, fondo - top - ell / 2))
        drawRect(a.sombra(0.22f), Offset(cx + anc * 0.2f, top + ell / 2), Size(anc * 0.3f, fondo - top - ell / 2))
        drawLine(a.sombra(0.3f), Offset(cx + anc * 0.5f, top + ell / 2), Offset(cx + anc * 0.5f, fondo), 0.6f * u)
        drawOval(a.luz(mezcla(PAPEL, PAPEL_VIEJO, 0.4f), k + 0.1f, f), Offset(cx - anc / 2, top), Size(anc, ell))
        drawOval(a.sombra(0.32f), Offset(cx - anc / 2, top), Size(anc, ell), style = Stroke(0.6f * u))
        // Los cantos del papel enrollado: una espiral que se cierra hacia el palo, con la orilla de la hoja de fuera.
        val vueltas = 2.2f + v.lleno * 1.6f + azar(s, 40 + i) * 0.6f
        val pasos = (vueltas * 9).toInt().coerceIn(8, 60)
        val espiral = Path()
        for (j in 0..pasos) {
            val t = j / pasos.toFloat()
            val ang = 0.6f + 6.283f * vueltas * t
            val fr = 0.94f - 0.74f * t
            val q = Offset(cx + anc / 2 * fr * kotlin.math.cos(ang), top + ell / 2 + ell / 2 * fr * kotlin.math.sin(ang))
            if (j == 0) espiral.moveTo(q.x, q.y) else espiral.lineTo(q.x, q.y)
        }
        drawPath(espiral, a.luz(mezcla(PAPEL_VIEJO, ALAMO_OSC, 0.45f), k, f), style = Stroke(0.5f * u))
        // El cabo del palo, que asoma un poco por el centro, con su corte claro.
        val cabo = Rect(cx - anc * 0.09f, top + ell * 0.5f - 2.8f * u, cx + anc * 0.09f, top + ell * 0.5f)
        drawRect(madera, cabo.topLeft, cabo.size)
        drawOval(a.luz(mezcla(SedaTinta.ALAMO, PAPEL_VIEJO, 0.3f), k + 0.1f, f), Offset(cabo.left, cabo.top - cabo.width * 0.25f), Size(cabo.width, cabo.width * 0.5f))
    }
    // La envoltura: de pie, con el borde de arriba que da la vuelta en diagonal y la panza redonda abajo.
    val cuerpo = Path().apply {
        moveTo(0f, yEnv + caida)
        lineTo(w, yEnv)
        lineTo(w, h - w * 0.1f)
        quadraticTo(w, h, w * 0.88f, h)
        lineTo(w * 0.12f, h)
        quadraticTo(0f, h, 0f, h - w * 0.1f)
        close()
    }
    drawPath(cuerpo, seda)
    clipPath(cuerpo) {
        // El labrado de la seda: una red de rombos apenas más clara y más honda.
        telaSeda(TelasSeda.seda(u), Rect(0f, yEnv, w, h), if (a.oscuro) 0.5f else 0.75f)
        // La orla tejida del borde, de otro color, con sus perlas, siguiendo la diagonal.
        val orla = if (seda.luminance() > 0.35f) a.luz(ANIL, k, f) else a.luz(mezcla(BRONCE, HUESO, 0.3f), k, f)
        drawPath(poligonoSeda(0f, yEnv + caida, w, yEnv, w, yEnv + 3.6f * u, 0f, yEnv + caida + 3.6f * u), orla)
        val perlas = Path()
        var px = 1.6f * u
        while (px < w) { val q = Offset(px, yEnv + caida * (1f - px / w) + 1.8f * u); perlas.addOval(Rect(q, 0.7f * u)); px += 3.2f * u }
        drawPath(perlas, mezcla(orla, seda, 0.55f))
        drawPath(poligonoSeda(0f, yEnv + caida + 3.6f * u, w, yEnv + 3.6f * u, w, yEnv + 4.6f * u, 0f, yEnv + caida + 4.6f * u), a.sombra(0.3f))
        // La vuelta de dentro de la envoltura: la otra orilla, más abajo, con su sombra.
        val y2 = yEnv + caida * 2.6f
        drawLine(hondoSeda, Offset(0f, y2 + caida * 0.8f), Offset(w, y2 - caida * 0.4f), 0.9f * u)
        drawLine(claroSeda.copy(alpha = 0.5f), Offset(0f, y2 + caida * 0.8f - 1f * u), Offset(w, y2 - caida * 0.4f - 1f * u), 0.6f * u)
        // Una mancha de agua vieja y el roce que aclaró la seda abajo.
        drawOval(hondoSeda.copy(alpha = 0.22f), Offset(w * (0.45f + 0.2f * azar(s, 3)), h * (0.66f + 0.1f * azar(s, 4))), Size(w * 0.32f, h * 0.06f))
        drawOval(claroSeda.copy(alpha = 0.3f), Offset(w * 0.05f, h * (0.86f + 0.03f * azar(s, 5))), Size(w * 0.45f, h * 0.05f))
        bultoSeda(a, k, f, hacia, yEnv, h)
        // El brillo de la seda en la cresta del bulto, fino y largo.
        drawRect(Color.White.copy(alpha = if (a.oscuro) 0.06f + 0.14f * k else if (v.noche) 0.18f else 0.26f), Offset(w * 0.23f, yEnv), Size(1.1f * u, h - yEnv))
    }
    // Los hilos sueltos de la orla.
    for (j in 0 until 3) {
        val x = w * (0.2f + 0.28f * j + 0.08f * azar(s, 10 + j))
        val y = yEnv + caida * (1f - x / w)
        drawLine(hondoSeda, Offset(x, y), Offset(x + (azar(s, 14 + j) - 0.5f) * 2f * u, y - 2.4f * u), 0.5f * u)
    }
    // La tira de papel con el mes, en columna, y el sello de bermellón (en los Sueños, la luna).
    val tira = Rect(w * 0.1f, yEnv + caida + 7f * u, w * 0.44f, yEnv + caida + 7f * u + (h - yEnv) * 0.36f)
    drawRect(a.sombra(0.3f), tira.topLeft + Offset(0.8f * u, 0.8f * u), tira.size)
    drawRect(a.etiqueta(k, PAPEL_VIEJO), tira.topLeft, tira.size)
    drawRect(a.sombra(0.12f), Offset(tira.right - tira.width * 0.22f, tira.top), Size(tira.width * 0.22f, tira.height))
    val ss = minOf(tira.width * 0.64f, 5.4f * u)
    val sello = Offset(tira.center.x, tira.bottom - ss * 0.85f)
    mesSeda(medidor, v.mes, Rect(tira.left, tira.top + 1f * u, tira.right, sello.y - ss * 0.65f), a.tinta, 7.5f)
    if (v.noche) {
        drawCircle(a.luz(mezcla(ANIL, a.hondo, 0.3f), k, f), ss * 0.62f, sello)
        drawCircle(if (a.oscuro) mezcla(LUNA_SEDA, a.hondo, 0.2f - 0.2f * k) else LUNA_SEDA, ss * 0.44f, sello)
        drawCircle(a.luz(mezcla(ANIL, a.hondo, 0.3f), k, f), ss * 0.36f, sello + Offset(ss * 0.2f, -ss * 0.14f))
    } else {
        drawRect(a.luz(BERMELLON, maxOf(k, 0.3f), f), Offset(sello.x - ss / 2, sello.y - ss / 2), Size(ss, ss))
        drawRect(a.etiqueta(k, PAPEL_VIEJO), Offset(sello.x - ss * 0.3f, sello.y - ss * 0.3f), Size(ss * 0.6f, ss * 0.6f), style = Stroke(0.5f * u))
    }
    // La cinta de seda, que da vueltas (más cuanto más escribiste) y cuelga con la tira de marfil.
    val cinta = if (seda.luminance() > 0.35f) a.luz(FIELTRO, k, f) else a.luz(mezcla(BRONCE, HUESO, 0.4f), k, f)
    val vc = 1 + (v.lleno * 2.4f).toInt()
    val yc = tira.bottom + 5f * u
    for (j in 0 until vc) {
        val y = yc + j * 2.6f * u
        drawLine(a.sombra(0.3f), Offset(0f, y + 1.6f * u), Offset(w, y + 0.6f * u), 1.6f * u)
        drawLine(cinta, Offset(0f, y + 1f * u), Offset(w, y), 1.7f * u)
        drawLine(a.claro(0.3f), Offset(0f, y + 0.4f * u), Offset(w * 0.5f, y + 0.1f * u), 0.5f * u)
    }
    val ojal = Offset(w * 0.66f, yc + vc * 2.6f * u)
    drawLine(cinta, Offset(w * 0.74f, yc), ojal + Offset(0f, 2.5f * u), 1.5f * u, StrokeCap.Round)
    etiquetaDiaSeda(v, medidor, a, k, ojal + Offset(0f, 2f * u), Offset(w * 0.6f, ojal.y + 3.5f * u), HUESO, (azar(s, 8) - 0.5f) * 10f, 0)
    drawRect(a.sombra(0.3f), Offset(0f, h - 1.3f * u), Size(w, 1.3f * u))
}

/**
 * El fajo de cartas a casa: varias cartas dobladas (más cuanto más escribiste), envueltas en un paño teñido del color del
 * ánimo y atadas en cruz con un cordón de cáñamo; asoman por arriba los cantos del papel. En el cruce, el sello de arcilla
 * (en los Sueños, la luna); bajo el cordón, la tira con el mes; del nudo cuelga la tablilla de álamo con el día. El paño es
 * basto, con su trama, sus arrugas donde aprieta el cordón y, a veces, un remiendo.
 */
private fun DrawScope.fajoSeda(v: Volumen, medidor: TextMeasurer, a: AireSeda, k: Float, f: Float, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val pano = v.tono?.let { a.animo(it, k, f) } ?: a.luz(CANAMO_SEDA, k, f)
    val hondoPano = mezcla(pano, a.hondo, 0.4f)
    val claroPano = mezcla(pano, if (a.oscuro) FILO_MUEBLE else Color.White, 0.2f)
    // Los cantos de las cartas, que asoman por arriba, desparejos.
    val cartas = 2 + (v.lleno * 4).toInt()
    val yPano = h * 0.13f
    for (j in 0 until cartas) {
        val an = w / cartas * 1.7f
        val x = (w - an) * j / (cartas - 1).coerceAtLeast(1)
        val y = h * 0.075f * azar(s, 20 + j)
        val r = Rect(x, y, x + an, yPano + 4f * u)
        drawRect(a.luz(mezcla(PAPEL, ALAMO, 0.1f + 0.15f * azar(s, 30 + j)), k + 0.05f, f), r.topLeft, r.size)
        drawLine(a.sombra(0.3f), Offset(r.right, r.top), Offset(r.right, r.bottom), 0.6f * u)
        drawLine(a.claro(0.4f), Offset(r.left, r.top + 0.4f * u), Offset(r.right, r.top + 0.4f * u), 0.5f * u)
        drawLine(a.sombra(0.15f), Offset(r.left + an * 0.5f, r.top), Offset(r.left + an * 0.5f, r.bottom), 0.5f * u)
    }
    // El paño: un bulto blando, con los costados apenas combados y el labio doblado arriba.
    val cuerpo = Path().apply {
        moveTo(0.6f * u, yPano)
        cubicTo(w * 0.3f, yPano - 1.6f * u, w * 0.7f, yPano + 1.2f * u, w - 0.6f * u, yPano - 0.6f * u)
        cubicTo(w + 0.6f * u, h * 0.4f, w + 0.4f * u, h * 0.75f, w - 0.4f * u, h)
        lineTo(0.4f * u, h)
        cubicTo(-0.4f * u, h * 0.75f, -0.6f * u, h * 0.4f, 0.6f * u, yPano)
        close()
    }
    drawPath(cuerpo, pano)
    // El paño es casi un rectángulo: basta con recortar al rectángulo (recortar a la forma pesa).
    clipRect(0f, yPano - 2f * u, w, h) {
        // La trama basta del paño.
        telaSeda(TelasSeda.pano(u), Rect(0f, yPano, w, h), if (a.oscuro) 0.6f else 1f)
        // El labio doblado de arriba.
        drawRect(claroPano.copy(alpha = 0.5f), Offset(0f, yPano), Size(w, 2.4f * u))
        drawRect(hondoPano.copy(alpha = 0.6f), Offset(0f, yPano + 2.4f * u), Size(w, 1.4f * u))
        // Las arrugas donde aprieta el cordón: abren en abanico desde el cruce.
        val cruce = Offset(w * 0.5f, h * 0.55f)
        listOf(-1f, 1f).forEach { l ->
            for (j in 0 until 2) {
                val fin = Offset(w * 0.5f + l * w * (0.36f + 0.1f * j), h * (0.3f + 0.4f * j) + (azar(s, 40 + j) - 0.5f) * h * 0.1f)
                drawLine(hondoPano.copy(alpha = 0.5f), cruce + Offset(l * 2f * u, 0f), fin, 0.9f * u, StrokeCap.Round)
                drawLine(claroPano.copy(alpha = 0.4f), cruce + Offset(l * 2f * u, -1.2f * u), fin - Offset(0f, 1.2f * u), 0.6f * u, StrokeCap.Round)
            }
        }
        // Un remiendo, a veces: un trozo de paño crudo cosido a puntadas.
        if (azar(s, 50) < 0.45f) {
            val rr = Rect(w * (0.08f + 0.2f * azar(s, 51)), h * 0.78f, w * (0.5f + 0.2f * azar(s, 51)), h * 0.92f)
            drawRect(a.luz(CANAMO_SEDA, k, f), rr.topLeft, rr.size)
            val puntadas = Path()
            var px = rr.left + 1f * u
            while (px < rr.right) { puntadas.moveTo(px, rr.top - 1f * u); puntadas.lineTo(px + 1f * u, rr.top + 1f * u); px += 2.4f * u }
            drawPath(puntadas, hondoPano, style = Stroke(0.5f * u))
        }
        bultoSeda(a, k, f, hacia, yPano, h)
    }
    // La tira con el mes, metida bajo el cordón.
    val tira = Rect(w * 0.28f, yPano + 6f * u, w * 0.72f, h * 0.48f)
    drawRect(a.sombra(0.28f), tira.topLeft + Offset(0.8f * u, 0.8f * u), tira.size)
    drawRect(a.etiqueta(k, PAPEL_VIEJO), tira.topLeft, tira.size)
    mesSeda(medidor, v.mes, tira.deflate(1f * u), a.tinta, 8.5f)
    // El cordón en cruz, con su torsión.
    val cord = a.luz(mezcla(PAJA, CUERO, 0.25f), k, f)
    val cordOsc = mezcla(cord, a.hondo, 0.4f)
    val yc = h * 0.55f
    drawLine(a.sombra(0.35f), Offset(w * 0.5f + 1f * u, yPano - 1f * u), Offset(w * 0.5f + 1f * u, h), 1.8f * u)
    drawLine(cord, Offset(w * 0.5f, yPano - 1.5f * u), Offset(w * 0.5f, h), 1.7f * u)
    drawLine(a.sombra(0.35f), Offset(0f, yc + 1.2f * u), Offset(w, yc + 1.2f * u), 1.8f * u)
    drawLine(cord, Offset(0f, yc), Offset(w, yc), 1.7f * u)
    val torsion = Path()
    var t = 1f * u
    while (t < w) { torsion.moveTo(t, yc - 0.8f * u); torsion.lineTo(t + 1f * u, yc + 0.8f * u); t += 2.2f * u }
    t = yPano
    while (t < h) { torsion.moveTo(w * 0.5f - 0.8f * u, t); torsion.lineTo(w * 0.5f + 0.8f * u, t + 1f * u); t += 2.2f * u }
    drawPath(torsion, cordOsc, style = Stroke(0.5f * u))
    // El sello de arcilla en el cruce y, colgando del nudo, la tablilla de álamo con el día.
    val rs = minOf(w * 0.22f, 5f * u)
    etiquetaDiaSeda(v, medidor, a, k, Offset(w * 0.5f, yc + 2f * u), Offset(w * 0.5f, yc + rs + 4f * u), mezcla(ALAMO, HUESO, 0.6f), (azar(s, 9) - 0.5f) * 12f, 1)
    selloArcillaSeda(Offset(w * 0.5f, yc), rs, v.noche, a, k, s)
    drawRect(a.sombra(0.3f), Offset(0f, h - 1.3f * u), Size(w, 1.3f * u))
}

/**
 * La bolsa de cuero donde guarda las cuentas: el cuero teñido del color del ánimo, panzona abajo (más llena cuanto más
 * escribiste), fruncida en el cuello por el cordón (más vueltas) con el sello de arcilla en el nudo (en los Sueños, la luna) y
 * la tablilla de hueso con el día; en la panza, la etiqueta de cuero claro cosida con el mes; las arrugas, la costura y el
 * roce que aclaró el cuero.
 */
private fun DrawScope.bolsaSeda(v: Volumen, medidor: TextMeasurer, a: AireSeda, k: Float, f: Float, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val cuero = v.tono?.let { a.animo(it, k, f) } ?: a.luz(CUERO, k, f)
    val hondoCuero = mezcla(cuero, a.hondo, 0.45f)
    val claroCuero = mezcla(cuero, if (a.oscuro) FILO_MUEBLE else Color.White, 0.22f)
    val yCuello = h * 0.24f
    val cuello = w * (0.17f - 0.03f * v.lleno)
    val panza = 0.5f + 0.06f * v.lleno
    // El fruncido de arriba: la boca, que se abre en pliegues.
    val boca = Path().apply {
        moveTo(w * 0.5f - cuello, yCuello)
        lineTo(w * 0.14f, h * 0.04f)
        val pliegues = 5
        for (j in 0 until pliegues) {
            val xa = w * 0.14f + w * 0.72f * j / pliegues
            val xb = w * 0.14f + w * 0.72f * (j + 1) / pliegues
            quadraticTo((xa + xb) / 2, -h * 0.01f + h * 0.03f * azar(s, 60 + j), xb, h * 0.035f + h * 0.02f * azar(s, 66 + j))
        }
        lineTo(w * 0.5f + cuello, yCuello)
        close()
    }
    drawPath(boca, mezcla(cuero, a.hondo, 0.12f))
    clipPath(boca) {
        for (j in 1 until 5) {
            val x = w * 0.14f + w * 0.72f * j / 5
            drawLine(hondoCuero, Offset(x, h * 0.04f), Offset(w * 0.5f + (x - w * 0.5f) * 0.3f, yCuello), 0.9f * u)
            drawLine(claroCuero.copy(alpha = 0.6f), Offset(x - 1.6f * u, h * 0.05f), Offset(w * 0.5f + (x - w * 0.5f) * 0.3f - 0.8f * u, yCuello), 0.6f * u)
        }
    }
    // La panza.
    val cuerpo = Path().apply {
        moveTo(w * 0.5f - cuello, yCuello)
        cubicTo(w * 0.5f - cuello * 1.3f, h * 0.36f, 0f, h * panza, 0f, h * 0.78f)
        cubicTo(0f, h * 0.95f, w * 0.18f, h, w * 0.5f, h)
        cubicTo(w * 0.82f, h, w, h * 0.95f, w, h * 0.78f)
        cubicTo(w, h * panza, w * 0.5f + cuello * 1.3f, h * 0.36f, w * 0.5f + cuello, yCuello)
        close()
    }
    drawPath(cuerpo, cuero)
    clipPath(cuerpo) {
        // Las arrugas que bajan del cuello, abiertas en abanico.
        for (j in 0 until 4) {
            val l = if (j % 2 == 0) -1f else 1f
            val fin = Offset(w * 0.5f + l * w * (0.18f + 0.14f * j), h * (0.46f + 0.07f * j))
            drawLine(hondoCuero.copy(alpha = 0.6f), Offset(w * 0.5f + l * cuello * 0.6f, yCuello + 2f * u), fin, 0.9f * u, StrokeCap.Round)
            drawLine(claroCuero.copy(alpha = 0.45f), Offset(w * 0.5f + l * cuello * 0.6f - 1.2f * u, yCuello + 2f * u), fin - Offset(1.2f * u, 0f), 0.6f * u, StrokeCap.Round)
        }
        // La costura del costado, a puntadas.
        val xs = w * 0.82f
        val costura = Path()
        var y = h * 0.4f
        while (y < h * 0.95f) { costura.moveTo(xs, y); costura.lineTo(xs - 0.5f * u, y + 1.6f * u); y += 3f * u }
        drawPath(costura, hondoCuero, style = Stroke(0.7f * u))
        // El roce: el cuero aclarado abajo, donde se arrastra.
        drawOval(claroCuero.copy(alpha = 0.35f), Offset(w * 0.08f, h * 0.84f), Size(w * 0.5f, h * 0.1f))
        drawOval(a.sombra(0.2f), Offset(0f, h * 0.9f), Size(w, h * 0.2f))
        bultoSeda(a, k, f, hacia, yCuello, h)
        // El redondeo de la panza: una media luna de sombra del lado contrario a la luz.
        val sl = if (a.oscuro) hacia else -1f
        drawOval(a.sombra(0.16f), Offset(if (sl < 0) w * 0.7f else -w * 0.4f, h * 0.3f), Size(w * 0.7f, h * 0.9f))
    }
    // La etiqueta de cuero claro, cosida en la panza, con el mes.
    val et = Rect(w * 0.3f, h * 0.5f, w * 0.7f, h * 0.88f)
    drawRect(a.sombra(0.3f), et.topLeft + Offset(0.8f * u, 0.8f * u), et.size)
    drawRoundRect(a.etiqueta(k, mezcla(PAPEL_VIEJO, CUERO, 0.15f)), et.topLeft, et.size, CornerRadius(1.2f * u))
    val hilo = Path()
    var py = et.top + 1.5f * u
    while (py < et.bottom - 1f * u) {
        hilo.moveTo(et.left + 0.8f * u, py); hilo.lineTo(et.left + 0.8f * u, py + 1f * u)
        hilo.moveTo(et.right - 0.8f * u, py); hilo.lineTo(et.right - 0.8f * u, py + 1f * u)
        py += 2.4f * u
    }
    drawPath(hilo, a.sombra(0.4f), style = Stroke(0.5f * u))
    mesSeda(medidor, v.mes, et.deflate(1.4f * u), a.tinta, 8.5f)
    // El cordón que la frunce, con sus vueltas, el sello en el nudo y la tablilla de hueso con el día.
    val cord = a.luz(mezcla(PAJA, CUERO, 0.3f), k, f)
    val vueltas = 1 + (v.lleno * 2.5f).toInt()
    for (j in 0 until vueltas) {
        val y = yCuello - 1f * u + j * 2.2f * u
        drawLine(a.sombra(0.35f), Offset(w * 0.5f - cuello - 1f * u, y + 1f * u), Offset(w * 0.5f + cuello + 1f * u, y + 1.4f * u), 1.6f * u)
        drawLine(cord, Offset(w * 0.5f - cuello - 1f * u, y), Offset(w * 0.5f + cuello + 1f * u, y + 0.5f * u), 1.6f * u)
    }
    val nudo = Offset(w * 0.5f, yCuello + vueltas * 1.1f * u)
    val rs = minOf(w * 0.2f, 4.6f * u)
    drawLine(cord, nudo, nudo + Offset(-w * 0.12f, h * 0.06f), 1.1f * u, StrokeCap.Round)
    etiquetaDiaSeda(v, medidor, a, k, nudo, Offset(w * 0.6f, nudo.y + rs + 2.5f * u), HUESO, (azar(s, 9) - 0.5f) * 12f, 2)
    selloArcillaSeda(nudo, rs, v.noche, a, k, s)
}
