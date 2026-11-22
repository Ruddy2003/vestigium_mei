package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteDeco.COSTADO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.GRANATE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MACASAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MACASAR_MIEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MACASAR_NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MARFIL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.NIQUEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.NIQUEL_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.TINTA
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

// =====================================================================
//  Art déco: lo de encima, lo de los huecos y lo del pie
// =====================================================================

/** El papel de estraza de las fundas de los discos y su sombra. */
private val ESTRAZA = Color(0xFFB58D5E)
/** Los colores de las etiquetas de los discos (sin letras): granate, verde, azul de cobalto y crema. */
private val ETIQUETAS_DECO = listOf(Color(0xFF8E2A2E), Color(0xFF2F6A52), Color(0xFF2B4C8C), Color(0xFFD9C79A))

/** La sombra de contacto de lo que pisa algo, con el color de sombra del momento. */
private fun DrawScope.contactoDeco(a: AireDeco, centro: Offset, ancho: Float, alto: Float, alfa: Float = 0.32f) =
    drawOval(a.sombra(alfa), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

/** El níquel de una pieza: con su brillo frío arriba y su sombra abajo, y la luz que le llega. */
private fun AireDeco.niquel(k: Float): Color = luz(NIQUEL, k)

/**
 * Lo de encima del armario, de atrás adelante, apoyado en [apoyo]: los discos en sus fundas contra la pared, el gramófono
 * portátil abierto (el protagonista, a la izquierda), el metrónomo y la taza con su plato bajo el aplique, y la hoja pautada
 * que cuelga del canto. La sombra de cada uno va hacia donde no está la luz.
 */
internal fun DrawScope.encimaDeco(apoyo: Float, a: AireDeco, cerca: (Offset) -> Float) {
    val w = size.width
    val izq = !a.aplique || a.alba
    fundasEncima(Offset(w * 0.53f, apoyo), w * 0.15f, a, cerca(Offset(w * 0.55f, apoyo - w * 0.07f)), izq)
    gramofonoDeco(Rect(w * 0.1f, apoyo - w * 0.095f, w * 0.42f, apoyo), a, cerca, izq)
    metronomoDeco(Offset(w * 0.665f, apoyo), w * 0.17f, a, cerca(Offset(w * 0.665f, apoyo - w * 0.08f)), izq)
    tazaDeco(Offset(w * 0.79f, apoyo), w * 0.075f, a, cerca(Offset(w * 0.79f, apoyo - w * 0.02f)), izq)
    hojaColgandoDeco(apoyo, a, cerca(Offset(w * 0.07f, apoyo)))
}

/** Dos discos en sus fundas de estraza apoyados en la pared, uno delante del otro, con el agujero que deja ver la etiqueta. */
private fun DrawScope.fundasEncima(pie: Offset, lado: Float, a: AireDeco, k: Float, izq: Boolean) {
    val u = density
    for (j in 0 until 2) {
        val l = lado * (1f - 0.12f * j)
        val x = pie.x + j * lado * 0.32f
        val giro = if (j == 0) 7f else -4f
        rotate(giro, Offset(x + l / 2, pie.y)) {
            val r = Rect(x, pie.y - l, x + l, pie.y)
            drawRect(a.sombra(0.3f), Offset(r.left + (if (izq) 3f else -3f) * u, r.top + 2f * u), r.size)
            drawRect(a.luz(mezcla(ESTRAZA, MARFIL, 0.15f * j), k), r.topLeft, r.size)
            // El borde gastado de la funda y el agujero del medio con la etiqueta del disco.
            drawRect(a.sombra(0.2f), r.topLeft, r.size, style = Stroke(1f * u))
            drawLine(a.claro(0.4f), Offset(r.left, r.top + 1f * u), Offset(r.right, r.top + 1f * u), 0.8f * u)
            val c = r.center
            drawCircle(a.luz(TINTA, k), l * 0.2f, c)
            drawCircle(a.luz(ETIQUETAS_DECO[j], k), l * 0.13f, c)
            drawCircle(a.luz(TINTA, k), l * 0.015f + 0.6f * u, c)
            drawCircle(a.sombra(0.3f), l * 0.2f, c, style = Stroke(1f * u))
            drawLine(a.sombra(0.15f), Offset(r.left + l * 0.1f, r.top + l * 0.6f), Offset(r.left + l * 0.35f, r.top + l * 0.95f), 0.8f * u)
        }
    }
}

/**
 * El gramófono portátil abierto, en [r] (su caja): la caja forrada de cuero granate con esquineros de níquel, la tapa
 * levantada detrás con su forro, el plato con un disco puesto, el brazo de níquel con el diafragma sobre el disco y la
 * manivela que sale por el costado. Es lo primero que ve el ojo.
 */
private fun DrawScope.gramofonoDeco(r: Rect, a: AireDeco, cerca: (Offset) -> Float, izq: Boolean) {
    val u = density; val w = size.width
    val k = cerca(r.center)
    val ladoSombra = if (izq) 1f else -1f
    // La sombra en la pared y en la tapa del armario.
    drawRect(a.sombra(if (a.lumbre) 0.4f else 0.26f), Offset(r.left + ladoSombra * w * 0.025f, r.top - w * 0.2f), Size(r.width, w * 0.2f + r.height))
    contactoDeco(a, Offset(r.center.x + ladoSombra * w * 0.01f, r.bottom), r.width * 1.05f, w * 0.014f, if (a.oscuro) 0.5f else 0.36f)
    // La tapa levantada, detrás: el cuero por fuera (se ve el canto) y el forro por dentro.
    val tapa = Rect(r.left + r.width * 0.03f, r.top - w * 0.2f, r.right - r.width * 0.03f, r.top + r.height * 0.2f)
    val echada = tapa.width * 0.06f
    drawPath(TrazoDeco.poligono(tapa.left, tapa.bottom, tapa.left + echada, tapa.top, tapa.right - echada, tapa.top, tapa.right, tapa.bottom), a.luz(mezcla(GRANATE, MACASAR_NEGRO, 0.25f), k))
    val forro = Rect(tapa.left + w * 0.012f, tapa.top + w * 0.012f, tapa.right - w * 0.012f, tapa.bottom)
    drawPath(TrazoDeco.poligono(forro.left, forro.bottom, forro.left + echada, forro.top, forro.right - echada, forro.top, forro.right, forro.bottom), a.luz(Color(0xFF3B2A3E), k))
    drawLine(a.claro(0.35f), Offset(tapa.left + echada, tapa.top), Offset(tapa.right - echada, tapa.top), 1.2f * u)
    // Lo acolchado del forro: un rombo de pespuntes y el bolsillo con un disco de repuesto.
    val pespunte = Path().apply {
        val c = forro.center
        moveTo(c.x, forro.top + forro.height * 0.12f); lineTo(forro.right - forro.width * 0.12f, c.y - forro.height * 0.05f)
        lineTo(c.x, forro.bottom - forro.height * 0.2f); lineTo(forro.left + forro.width * 0.12f, c.y - forro.height * 0.05f); close()
    }
    drawPath(pespunte, a.claro(0.25f).copy(alpha = if (a.oscuro) 0.1f else 0.3f), style = Stroke(0.8f * u))
    val bolsillo = Rect(forro.left, forro.bottom - forro.height * 0.36f, forro.right, forro.bottom)
    drawCircle(a.luz(TINTA, k), forro.width * 0.3f, Offset(forro.center.x + forro.width * 0.08f, bolsillo.top + forro.width * 0.05f))
    drawRect(a.luz(mezcla(GRANATE, Color(0xFF3B2A3E), 0.5f), k), bolsillo.topLeft, bolsillo.size)
    drawLine(a.claro(0.3f), bolsillo.topLeft, bolsillo.topRight, 0.8f * u)
    drawLine(a.claro(0.3f), Offset(tapa.left, tapa.bottom), Offset(tapa.left + echada, tapa.top), 1.4f * u)
    // La caja: el cuero con su grano, el filo de la tapa de arriba (el plato) y los esquineros.
    val caja = Rect(r.left, r.top + r.height * 0.18f, r.right, r.bottom)
    drawRect(a.luz(GRANATE, k), caja.topLeft, caja.size)
    clipRect(caja.left, caja.top, caja.right, caja.bottom) {
        texturaDeco(TexturaDeco.CUERO, caja, a.luz(Color.White, maxOf(k, 0.2f)), 0.9f)
        drawRect(a.claro(0.18f), caja.topLeft, Size(caja.width, caja.height * 0.3f))
        drawRect(a.sombra(0.25f), Offset(caja.left, caja.bottom - caja.height * 0.3f), Size(caja.width, caja.height * 0.3f))
        // El cierre de níquel del frente y la costura.
        drawLine(a.sombra(0.3f), Offset(caja.left, caja.top + caja.height * 0.5f), Offset(caja.right, caja.top + caja.height * 0.5f), 0.7f * u)
    }
    val cierre = Rect(caja.center.x - w * 0.012f, caja.top + caja.height * 0.36f, caja.center.x + w * 0.012f, caja.top + caja.height * 0.64f)
    drawRect(a.niquel(k), cierre.topLeft, cierre.size)
    drawRect(a.sombra(0.4f), cierre.topLeft, cierre.size, style = Stroke(0.6f * u))
    val esq = w * 0.018f
    for ((x, y) in listOf(caja.left to caja.top, caja.right - esq to caja.top, caja.left to caja.bottom - esq, caja.right - esq to caja.bottom - esq)) {
        drawRect(a.niquel(k), Offset(x, y), Size(esq, esq))
        drawLine(a.claro(0.6f), Offset(x, y + 0.6f * u), Offset(x + esq, y + 0.6f * u), 0.7f * u)
    }
    // El plato: la cara de arriba apenas vista, con el fieltro verde y el disco negro con su etiqueta y sus surcos.
    val plato = Rect(r.left + r.width * 0.02f, r.top + r.height * 0.06f, r.right - r.width * 0.02f, caja.top + 1f * u)
    drawRect(a.luz(mezcla(GRANATE, MACASAR_NEGRO, 0.4f), k), plato.topLeft, plato.size)
    val disco = Rect(plato.left + plato.width * 0.08f, plato.top - plato.height * 0.6f, plato.left + plato.width * 0.68f, plato.bottom - plato.height * 0.05f)
    drawOval(a.luz(TINTA, k), disco.topLeft, disco.size)
    drawOval(a.claro(0.3f).copy(alpha = if (a.oscuro) 0.08f else 0.22f), Offset(disco.left + disco.width * 0.08f, disco.top + disco.height * 0.15f), Size(disco.width * 0.84f, disco.height * 0.7f), style = Stroke(0.7f * u))
    drawOval(a.luz(ETIQUETAS_DECO[0], k), Offset(disco.center.x - disco.width * 0.13f, disco.center.y - disco.height * 0.15f), Size(disco.width * 0.26f, disco.height * 0.3f))
    if (a.lumbre) drawArc(a.filo(0.5f * k), 200f, 60f, false, disco.topLeft, disco.size, style = Stroke(1f * u))
    else drawArc(Color.White.copy(alpha = 0.35f), 200f, 50f, false, disco.topLeft, disco.size, style = Stroke(1f * u))
    // El brazo: del pivote, atrás a la derecha, hasta el diafragma sobre el disco.
    val pivote = Offset(plato.right - plato.width * 0.12f, plato.top + plato.height * 0.2f)
    val diafragma = Offset(disco.left + disco.width * 0.72f, disco.center.y - disco.height * 0.1f)
    val brazo = Path().apply {
        moveTo(pivote.x, pivote.y)
        quadraticBezierTo(pivote.x - w * 0.02f, pivote.y - w * 0.035f, diafragma.x + w * 0.012f, diafragma.y - w * 0.012f)
    }
    drawPath(brazo, a.sombra(0.4f), style = Stroke(3.2f * u, cap = StrokeCap.Round))
    drawPath(brazo, a.niquel(k), style = Stroke(2.2f * u, cap = StrokeCap.Round))
    drawPath(brazo, a.claro(0.6f).copy(alpha = if (a.oscuro) 0.2f else 0.6f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
    drawCircle(a.niquel(k), w * 0.013f, diafragma)
    drawCircle(a.sombra(0.4f), w * 0.013f, diafragma, style = Stroke(0.7f * u))
    drawCircle(a.luz(NIQUEL_OSC, k), w * 0.006f, diafragma)
    drawCircle(a.niquel(k), w * 0.009f, pivote)
    // La manivela, por el costado derecho: el eje, el brazo y el puño de madera.
    val eje = Offset(caja.right, caja.top + caja.height * 0.55f)
    drawLine(a.niquel(k), eje, Offset(eje.x + w * 0.02f, eje.y), 2f * u)
    drawLine(a.niquel(k), Offset(eje.x + w * 0.02f, eje.y), Offset(eje.x + w * 0.02f, eje.y + w * 0.035f), 2f * u)
    drawRoundRect(a.luz(MACASAR_NEGRO, k), Offset(eje.x + w * 0.026f, eje.y + w * 0.022f), Size(w * 0.012f, w * 0.03f), androidx.compose.ui.geometry.CornerRadius(w * 0.006f))
    if (a.lumbre && !izq) drawRect(a.filo(0.45f * k), Offset(caja.right - 1.4f * u, caja.top), Size(1.4f * u, caja.height))
}

/** El metrónomo de pirámide, de nogal, con el frente abierto: la escala de marfil y el péndulo de níquel parado de lado. */
private fun DrawScope.metronomoDeco(pie: Offset, alto: Float, a: AireDeco, k: Float, izq: Boolean) {
    val u = density
    val base = alto * 0.48f; val cima = alto * 0.14f
    val cuerpo = TrazoDeco.poligono(pie.x - base / 2, pie.y, pie.x - cima / 2, pie.y - alto, pie.x + cima / 2, pie.y - alto, pie.x + base / 2, pie.y)
    val dx = if (izq) alto * 0.18f else -alto * 0.18f
    drawPath(TrazoDeco.poligono(pie.x - base / 2 + dx * 0.3f, pie.y, pie.x - cima / 2 + dx, pie.y - alto * 0.9f, pie.x + cima / 2 + dx, pie.y - alto * 0.9f, pie.x + base / 2 + dx * 0.3f, pie.y), a.sombra(if (a.lumbre) 0.38f else 0.24f))
    val nogal = mezcla(MACASAR, MACASAR_MIEL, 0.45f)
    drawPath(cuerpo, a.luz(nogal, k))
    clipPath(cuerpo) {
        // La cara de la luz y la de la sombra: la pirámide tiene dos caras vistas.
        val luzIzq = izq
        drawRect(a.claro(0.22f), Offset(if (luzIzq) pie.x - base / 2 else pie.x, pie.y - alto), Size(base / 2, alto))
        drawRect(a.sombra(0.25f), Offset(if (luzIzq) pie.x else pie.x - base / 2, pie.y - alto), Size(base / 2, alto))
        for (j in 0 until 5) drawLine(a.sombra(0.14f), Offset(pie.x - base / 2 + base * (0.15f + 0.17f * j), pie.y), Offset(pie.x - cima / 2 + cima * (0.2f * j), pie.y - alto), 0.6f * u)
    }
    // La escala de marfil y el péndulo, ladeado.
    val escala = Rect(pie.x - base * 0.07f, pie.y - alto * 0.82f, pie.x + base * 0.07f, pie.y - alto * 0.2f)
    drawRect(a.luz(MARFIL, k), escala.topLeft, escala.size)
    for (j in 0 until 8) drawLine(a.luz(TINTA, k).copy(alpha = 0.5f), Offset(escala.left, escala.top + escala.height * j / 8f), Offset(escala.left + escala.width * 0.5f, escala.top + escala.height * j / 8f), 0.5f * u)
    val eje = Offset(pie.x, pie.y - alto * 0.16f)
    val punta = Offset(pie.x + alto * 0.2f, pie.y - alto * 0.98f)
    drawLine(a.niquel(k), eje, punta, 1.4f * u, StrokeCap.Round)
    val pesa = Offset(eje.x + (punta.x - eje.x) * 0.55f, eje.y + (punta.y - eje.y) * 0.55f)
    drawRect(a.niquel(k), Offset(pesa.x - alto * 0.04f, pesa.y - alto * 0.03f), Size(alto * 0.08f, alto * 0.06f))
    drawRect(a.sombra(0.35f), Offset(pesa.x - alto * 0.04f, pesa.y - alto * 0.03f), Size(alto * 0.08f, alto * 0.06f), style = Stroke(0.6f * u))
    // El zócalo.
    drawRect(a.luz(MACASAR_NEGRO, k), Offset(pie.x - base * 0.56f, pie.y - alto * 0.05f), Size(base * 1.12f, alto * 0.05f))
    if (a.lumbre && !izq) drawLine(a.filo(0.5f * k), Offset(pie.x + base / 2, pie.y), Offset(pie.x + cima / 2, pie.y - alto), 1.2f * u)
}

/** La taza de loza con su plato: marfil con un filete verde y otro de oro. */
private fun DrawScope.tazaDeco(pie: Offset, ancho: Float, a: AireDeco, k: Float, izq: Boolean) {
    val u = density
    contactoDeco(a, Offset(pie.x + (if (izq) 1f else -1f) * ancho * 0.12f, pie.y), ancho * 1.5f, ancho * 0.16f, if (a.oscuro) 0.45f else 0.3f)
    val loza = a.luz(MARFIL, k)
    drawOval(loza, Offset(pie.x - ancho * 0.7f, pie.y - ancho * 0.14f), Size(ancho * 1.4f, ancho * 0.2f))
    drawOval(a.sombra(0.2f), Offset(pie.x - ancho * 0.7f, pie.y - ancho * 0.14f), Size(ancho * 1.4f, ancho * 0.2f), style = Stroke(0.6f * u))
    val taza = Path().apply {
        moveTo(pie.x - ancho * 0.5f, pie.y - ancho * 0.72f)
        lineTo(pie.x + ancho * 0.5f, pie.y - ancho * 0.72f)
        cubicTo(pie.x + ancho * 0.48f, pie.y - ancho * 0.3f, pie.x + ancho * 0.3f, pie.y - ancho * 0.1f, pie.x, pie.y - ancho * 0.1f)
        cubicTo(pie.x - ancho * 0.3f, pie.y - ancho * 0.1f, pie.x - ancho * 0.48f, pie.y - ancho * 0.3f, pie.x - ancho * 0.5f, pie.y - ancho * 0.72f)
        close()
    }
    drawArc(loza, -90f, 180f, false, Offset(pie.x + ancho * 0.36f, pie.y - ancho * 0.62f), Size(ancho * 0.32f, ancho * 0.34f), style = Stroke(ancho * 0.07f))
    drawPath(taza, loza)
    clipPath(taza) {
        drawRect(a.sombra(0.22f), Offset(if (izq) pie.x + ancho * 0.1f else pie.x - ancho * 0.5f, pie.y - ancho), Size(ancho * 0.4f, ancho))
        drawRect(a.luz(Color(0xFF2F6A52), k), Offset(pie.x - ancho, pie.y - ancho * 0.6f), Size(ancho * 2f, ancho * 0.05f))
    }
    drawLine(a.luz(Color(0xFFC9A24C), k), Offset(pie.x - ancho * 0.5f, pie.y - ancho * 0.71f), Offset(pie.x + ancho * 0.5f, pie.y - ancho * 0.71f), 1f * u)
    drawOval(a.luz(Color(0xFF3A2418), k), Offset(pie.x - ancho * 0.44f, pie.y - ancho * 0.75f), Size(ancho * 0.88f, ancho * 0.08f))
}

/** La hoja pautada que se quedó en la tapa del armario y cuelga por el canto, a la izquierda: cruza la línea del mueble. */
private fun DrawScope.hojaColgandoDeco(apoyo: Float, a: AireDeco, k: Float) {
    val u = density; val w = size.width
    val x0 = w * 0.03f; val x1 = w * 0.16f
    val abajo = apoyo + w * 0.07f
    val hoja = Path().apply {
        moveTo(x0, apoyo - w * 0.004f)
        lineTo(x1, apoyo - w * 0.006f)
        cubicTo(x1 + w * 0.01f, apoyo + w * 0.02f, x1 - w * 0.004f, abajo - w * 0.02f, x1 - w * 0.008f, abajo)
        lineTo(x0 + w * 0.012f, abajo + w * 0.004f)
        cubicTo(x0 + w * 0.006f, abajo - w * 0.02f, x0 - w * 0.006f, apoyo + w * 0.02f, x0, apoyo - w * 0.004f)
        close()
    }
    drawPath(TrazoDeco.corrido(hoja, 3f * u, 4f * u), a.sombra(0.35f))
    drawPath(hoja, a.luz(mezcla(MARFIL, ESTRAZA, 0.12f), k))
    clipPath(hoja) {
        // Dos pentagramas y unas cabezas de nota a mano (sin letras).
        val pauta = Path()
        for (g in 0 until 2) for (l in 0 until 5) {
            val y = apoyo + w * (0.012f + 0.028f * g) + l * w * 0.0035f
            pauta.moveTo(x0, y); pauta.lineTo(x1, y + w * 0.002f)
        }
        drawPath(pauta, a.luz(TINTA, k).copy(alpha = 0.7f), style = Stroke(0.7f * u))
        val notas = ArrayList<Offset>(10)
        for (j in 0 until 9) notas.add(Offset(x0 + (x1 - x0) * (0.12f + 0.1f * j), apoyo + w * (0.012f + 0.028f * (j % 2)) + w * 0.0035f * (j * 3 % 5)))
        drawPoints(notas, PointMode.Points, a.luz(TINTA, k).copy(alpha = 0.75f), 2.2f * u, StrokeCap.Round)
        drawRect(a.sombra(0.2f), Offset(x0, apoyo), Size(x1 - x0, w * 0.01f))
    }
}

/**
 * El aplique de abanico de la pared, en [c]: el vidrio esmerilado en medio círculo con sus costillas de níquel en escalón,
 * la copa y la placa de la pared. Apagado es un vidrio lechoso y frío; encendido, casi blanco en el medio y cálido al borde.
 */
internal fun DrawScope.apliqueDeco(c: Offset, a: AireDeco, encendido: Boolean) {
    val u = density; val w = size.width
    val radio = w * 0.075f
    val placa = Rect(c.x - w * 0.016f, c.y - w * 0.01f, c.x + w * 0.016f, c.y + w * 0.075f)
    drawRect(a.sombra(0.35f), Offset(placa.left + (if (encendido) -2f else 3f) * u, placa.top + 3f * u), placa.size)
    drawPath(MuebleEstanteDeco.escalonDeco(placa, w * 0.008f), a.luz(NIQUEL, if (a.luna) 0.35f else if (a.lumbre) 0.9f else 1f))
    val vidrio = Rect(c.x - radio, c.y - radio, c.x + radio, c.y + radio)
    // Apagado: de día, un vidrio lechoso; de noche, gris frío, apenas con el filo de la luz de la calle.
    val claro = when {
        encendido -> mezcla(Color(0xFFFFF4DC), PaletaDeco.APLIQUE, if (a.alba) 0.5f else 0.25f)
        a.oscuro -> mezcla(mezcla(PaletaDeco.PLATA, a.hondo, 0.72f), PaletaDeco.LUNA, 0.1f)
        else -> a.luz(mezcla(PaletaDeco.PLATA_LUZ, MARFIL, 0.4f), 0.8f)
    }
    if (!encendido) drawArc(a.sombra(0.3f), 180f, 180f, true, Offset(vidrio.left + 4f * u, vidrio.top + 4f * u), vidrio.size)
    drawArc(claro, 180f, 180f, true, vidrio.topLeft, vidrio.size)
    if (encendido) drawArc(PaletaDeco.APLIQUE.copy(alpha = 0.5f), 180f, 180f, true, vidrio.topLeft, vidrio.size, style = Stroke(radio * 0.18f))
    else drawArc(a.sombra(0.18f), 270f, 90f, true, vidrio.topLeft, vidrio.size)
    if (!encendido && a.oscuro) drawArc(PaletaDeco.LUNA.copy(alpha = 0.7f), 185f, 55f, false, vidrio.topLeft, vidrio.size, style = Stroke(1.2f * u))
    // Las costillas en abanico y el borde escalonado de níquel.
    val costillas = Path()
    for (j in 1 until 6) {
        val ang = Math.toRadians((180.0 + 30.0 * j)).toFloat()
        costillas.moveTo(c.x, c.y)
        costillas.lineTo(c.x + radio * kotlin.math.cos(ang), c.y + radio * kotlin.math.sin(ang))
    }
    val metal = if (encendido) mezcla(NIQUEL_OSC, PaletaDeco.APLIQUE, 0.2f) else a.luz(NIQUEL, if (a.oscuro) 0.35f else 0.9f)
    drawPath(costillas, metal, style = Stroke(1.3f * u))
    drawArc(metal, 180f, 180f, false, vidrio.topLeft, vidrio.size, style = Stroke(1.6f * u))
    drawArc(metal, 180f, 180f, false, Offset(c.x - radio * 0.4f, c.y - radio * 0.4f), Size(radio * 0.8f, radio * 0.8f), style = Stroke(1f * u))
    // La copa de abajo, en escalón.
    drawRect(metal, Offset(c.x - radio, c.y - 1f * u), Size(radio * 2f, w * 0.008f))
    drawRect(metal, Offset(c.x - radio * 0.6f, c.y + w * 0.007f), Size(radio * 1.2f, w * 0.008f))
    drawRect(metal, Offset(c.x - radio * 0.25f, c.y + w * 0.014f), Size(radio * 0.5f, w * 0.012f))
    if (encendido) {
        mancha(Offset(c.x, c.y - radio * 0.3f), radio * 1.6f, Color(0xFFFFE6B8).copy(alpha = if (a.alba) 0.3f else 0.55f))
    }
}

/**
 * Lo de un hueco libre de la repisa [k], de [oc] al costado, de izquierda a derecha mientras quepa: según la repisa, la pila
 * de discos de canto, la sordina de la trompeta de pie, la caja de cañas, la lata de agujas, el rollo de partituras atado con
 * las escobillas de la batería o el cenicero de níquel con su hilo de humo. [frio] es lo que le llega del frío de la escalera.
 */
internal fun DrawScope.huecoDeco(k: Int, r: Rect, oc: Float, a: AireDeco, cerca: Float, izq: Boolean, frio: Float) {
    val u = density
    val fin = r.right - 8f * u
    if (fin - oc < 22f * u || r.height < 10f * u) return
    val alto = r.height
    val orden = when (Math.floorMod(k, 3)) {
        0 -> listOf(0, 1, 2, 3)
        1 -> listOf(1, 4, 0, 2)
        else -> listOf(4, 0, 5, 1)
    }
    clipRect(r.left, r.top, r.right, r.bottom) {
        var x = oc + 6f * u
        for (pieza in orden) {
            val libre = fin - x
            val pide = when (pieza) {
                0 -> minOf(libre, alto * 0.75f).takeIf { it >= 16f * u } ?: 0f
                1 -> alto * 0.62f
                2 -> alto * 0.42f
                3 -> alto * 0.3f
                4 -> alto * 0.95f
                else -> alto * 0.42f
            }
            if (pide <= 0f || pide > libre) continue
            val sitio = Rect(x, r.bottom - alto, x + pide, r.bottom)
            // De noche, el frío de la escalera los deja ver aunque el aplique no llegue.
            val kp = maxOf(cerca, 0.45f * frio)
            when (pieza) {
                0 -> pilaDeCantoDeco(sitio, a, kp, izq, k)
                1 -> sordinaDeco(sitio, a, kp, izq)
                2 -> cajaCanasDeco(sitio, a, kp)
                3 -> lataAgujasDeco(sitio, a, kp)
                4 -> rolloEscobillasDeco(sitio, a, kp)
                else -> ceniceroDeco(sitio, a, kp)
            }
            x += pide + 6f * u
        }
    }
}

/** Los discos de canto, en sus fundas, uno contra otro: el canto de estraza, de marfil o del negro del disco que asoma. */
private fun DrawScope.pilaDeCantoDeco(s: Rect, a: AireDeco, k: Float, izq: Boolean, semilla: Int) {
    val u = density
    val grueso = 2.4f * u
    val n = (s.width / grueso).toInt().coerceIn(3, 40)
    val g = s.width / n
    val alto = s.height * 0.84f
    contactoDeco(a, Offset(s.center.x + (if (izq) 3f else -3f) * u, s.bottom), s.width * 1.15f, 4f * u, 0.45f)
    // El perfil de arriba, desparejo: cada funda asoma un poco distinto.
    val pila = Path().apply {
        moveTo(s.left, s.bottom)
        for (i in 0 until n) {
            val y = s.bottom - alto * (0.93f + 0.07f * azar(1201 + semilla, i))
            lineTo(s.left + g * i, y); lineTo(s.left + g * (i + 1), y)
        }
        lineTo(s.right, s.bottom); close()
    }
    drawPath(pila, a.luz(ESTRAZA, k))
    val claras = Path(); val discos = Path(); val juntas = Path()
    for (i in 0 until n) {
        val xi = s.left + g * i
        val z = azar(1202 + semilla, i)
        if (z < 0.3f) claras.addRect(Rect(xi, s.bottom - alto * 0.93f, xi + g, s.bottom))
        else if (z > 0.82f) discos.addRect(Rect(xi + g * 0.2f, s.bottom - alto, xi + g * 0.8f, s.bottom - alto * 0.88f))
        juntas.moveTo(xi, s.bottom - alto * 0.92f); juntas.lineTo(xi, s.bottom)
    }
    drawPath(claras, a.luz(mezcla(MARFIL, ESTRAZA, 0.25f), k))
    drawPath(discos, a.luz(TINTA, k))
    drawPath(juntas, a.sombra(0.35f), style = Stroke(0.6f * u))
    // La luz de un lado de la pila y la sombra del otro.
    drawRect(a.claro(0.25f), Offset(if (izq) s.left else s.right - s.width * 0.2f, s.bottom - alto * 0.93f), Size(s.width * 0.2f, alto * 0.93f))
    drawRect(a.sombra(0.25f), Offset(if (izq) s.right - s.width * 0.25f else s.left, s.bottom - alto * 0.93f), Size(s.width * 0.25f, alto * 0.93f))
}

/**
 * La sordina recta de la trompeta, tumbada: el cono de aluminio con el fondo ancho y redondo a la derecha, el aro y, en la
 * punta angosta de la izquierda, las tiras de corcho. Tumbada para que no se confunda con el metrónomo de arriba.
 */
private fun DrawScope.sordinaDeco(s: Rect, a: AireDeco, k: Float, izq: Boolean) {
    val u = density
    val largo = s.width
    val diam = minOf(s.height * 0.3f, largo * 0.48f)
    val eje = s.bottom - diam / 2
    val punta = diam * 0.34f
    contactoDeco(a, Offset(s.center.x + (if (izq) 2f else -2f) * u, s.bottom), largo * 1.1f, 4f * u, 0.45f)
    val cono = Path().apply {
        moveTo(s.left, eje - punta / 2)
        lineTo(s.right - diam * 0.3f, eje - diam / 2)
        quadraticBezierTo(s.right, eje - diam / 2, s.right, eje)
        quadraticBezierTo(s.right, eje + diam / 2, s.right - diam * 0.3f, eje + diam / 2)
        lineTo(s.left, eje + punta / 2)
        close()
    }
    drawPath(cono, a.luz(NIQUEL, k))
    clipPath(cono) {
        drawRect(a.claro(0.55f), Offset(s.left, eje - diam * 0.36f), Size(largo, diam * 0.14f))
        drawRect(a.sombra(0.32f), Offset(s.left, eje + diam * 0.12f), Size(largo, diam * 0.4f))
        drawRect(a.luz(NIQUEL_OSC, k), Offset(s.left + largo * 0.66f, eje - diam), Size(1.3f * u, diam * 2f))
    }
    // Las tiras de corcho de la punta.
    val corcho = a.luz(Color(0xFFA8794A), k)
    drawRect(corcho, Offset(s.left + largo * 0.04f, eje - punta * 0.62f), Size(largo * 0.22f, punta * 0.24f))
    drawRect(corcho, Offset(s.left + largo * 0.04f, eje + punta * 0.38f), Size(largo * 0.22f, punta * 0.24f))
}

/** La caja de cañas del clarinete, tumbada: cartón verde con su faja de marfil (sin letras) y dos cañas sueltas encima. */
private fun DrawScope.cajaCanasDeco(s: Rect, a: AireDeco, k: Float) {
    val u = density
    val alto = s.height * 0.15f
    val caja = Rect(s.left, s.bottom - alto, s.right, s.bottom)
    contactoDeco(a, Offset(caja.center.x, s.bottom), caja.width * 1.15f, 4f * u, 0.45f)
    drawRect(a.luz(Color(0xFF2F6A52), k), caja.topLeft, caja.size)
    drawRect(a.luz(MARFIL, k), Offset(caja.left + caja.width * 0.32f, caja.top), Size(caja.width * 0.2f, caja.height))
    drawRect(a.claro(0.35f), caja.topLeft, Size(caja.width, 1.2f * u))
    drawRect(a.sombra(0.3f), Offset(caja.left, caja.bottom - caja.height * 0.3f), Size(caja.width, caja.height * 0.3f))
    val canas = Path().apply {
        moveTo(caja.left + caja.width * 0.1f, caja.top - 0.8f * u); lineTo(caja.left + caja.width * 0.62f, caja.top - 1.6f * u)
        moveTo(caja.left + caja.width * 0.3f, caja.top - 2.4f * u); lineTo(caja.left + caja.width * 0.86f, caja.top - 1.2f * u)
    }
    drawPath(canas, a.luz(Color(0xFFD8BE7A), k), style = Stroke(1.8f * u, cap = StrokeCap.Round))
}

/** La lata de agujas del gramófono: baja y redonda, con la tapa pintada (un abanico, sin letras). */
private fun DrawScope.lataAgujasDeco(s: Rect, a: AireDeco, k: Float) {
    val la = s.width
    val lata = Rect(s.left, s.bottom - la * 0.62f, s.right, s.bottom)
    contactoDeco(a, Offset(lata.center.x, s.bottom), la * 1.3f, la * 0.16f, 0.45f)
    drawRect(a.luz(Color(0xFF2B4C8C), k), lata.topLeft, lata.size)
    drawOval(a.luz(Color(0xFFC9A24C), k), Offset(lata.left, lata.top - la * 0.1f), Size(la, la * 0.2f))
    drawArc(a.luz(MARFIL, k), 180f, 180f, true, Offset(lata.center.x - la * 0.25f, lata.top + la * 0.14f), Size(la * 0.5f, la * 0.5f))
    drawRect(a.claro(0.3f), Offset(lata.left, lata.top), Size(la * 0.15f, lata.height))
    drawRect(a.sombra(0.3f), Offset(lata.right - la * 0.2f, lata.top), Size(la * 0.2f, lata.height))
}

/** El cenicero de níquel con el cigarrillo apoyado y su hilo de humo (el humo del club). */
private fun DrawScope.ceniceroDeco(s: Rect, a: AireDeco, k: Float) {
    val u = density
    val an = s.width; val cx = s.center.x; val suelo = s.bottom
    contactoDeco(a, Offset(cx, suelo), an * 1.2f, an * 0.12f, 0.45f)
    drawRect(a.niquel(k), Offset(cx - an / 2, suelo - an * 0.18f), Size(an, an * 0.18f))
    drawRect(a.niquel(k), Offset(cx - an * 0.3f, suelo - an * 0.24f), Size(an * 0.6f, an * 0.06f))
    drawLine(a.claro(0.6f), Offset(cx - an / 2, suelo - an * 0.17f), Offset(cx + an / 2, suelo - an * 0.17f), 0.8f * u)
    drawLine(a.luz(MARFIL, k), Offset(cx - an * 0.1f, suelo - an * 0.22f), Offset(cx + an * 0.45f, suelo - an * 0.3f), 2f * u)
    drawCircle(Color(0xFFE8763C).copy(alpha = if (a.lumbre) 0.9f else 0.6f), 1.2f * u, Offset(cx + an * 0.46f, suelo - an * 0.3f))
    val humo = Path().apply {
        moveTo(cx + an * 0.46f, suelo - an * 0.32f)
        cubicTo(cx + an * 0.6f, suelo - s.height * 0.4f, cx + an * 0.1f, suelo - s.height * 0.55f, cx + an * 0.35f, suelo - s.height * 0.8f)
    }
    drawPath(humo, a.claro(0.5f).copy(alpha = if (a.oscuro) 0.12f else 0.35f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
}

/** El rollo de partituras, tumbado y atado con cordel rojo, y las escobillas de la batería apoyadas detrás, en diagonal. */
private fun DrawScope.rolloEscobillasDeco(s: Rect, a: AireDeco, k: Float) {
    val u = density
    val alto = s.height * 0.3f
    val largo = s.width
    val suelo = s.bottom
    // Las escobillas, detrás: el mango negro y el abanico de alambre.
    val e0 = Offset(s.left + largo * 0.12f, suelo - alto * 0.5f)
    val e1 = Offset(s.left + largo * 0.85f, suelo - s.height * 0.86f)
    val m = Offset(e0.x + (e1.x - e0.x) * 0.55f, e0.y + (e1.y - e0.y) * 0.55f)
    drawLine(a.luz(MACASAR_NEGRO, k), e0, m, 2.6f * u, StrokeCap.Round)
    val abanico = Path()
    for (j in 0 until 7) { abanico.moveTo(m.x, m.y); abanico.lineTo(e1.x + (j - 3) * 1.8f * u, e1.y + (j - 3) * (j - 3) * 0.5f * u) }
    drawPath(abanico, a.niquel(k), style = Stroke(0.7f * u))
    // El rollo.
    val rollo = Rect(s.left, suelo - alto, s.left + largo, suelo)
    contactoDeco(a, Offset(rollo.center.x, suelo), largo, alto * 0.25f, 0.45f)
    drawRoundRect(a.luz(mezcla(MARFIL, ESTRAZA, 0.2f), k), rollo.topLeft, rollo.size, androidx.compose.ui.geometry.CornerRadius(alto * 0.3f))
    drawRect(a.claro(0.35f), Offset(rollo.left + alto * 0.3f, rollo.top + alto * 0.12f), Size(largo - alto * 0.6f, alto * 0.16f))
    drawRect(a.sombra(0.25f), Offset(rollo.left + alto * 0.2f, rollo.bottom - alto * 0.3f), Size(largo - alto * 0.4f, alto * 0.3f))
    drawOval(a.luz(mezcla(MARFIL, ESTRAZA, 0.4f), k), Offset(rollo.right - alto * 0.5f, rollo.top), Size(alto * 0.5f, alto))
    drawOval(a.sombra(0.3f), Offset(rollo.right - alto * 0.38f, rollo.top + alto * 0.25f), Size(alto * 0.25f, alto * 0.5f), style = Stroke(0.6f * u))
    for (f in listOf(0.3f, 0.7f)) drawLine(a.luz(Color(0xFF8E2A2E), k), Offset(rollo.left + largo * f, rollo.top), Offset(rollo.left + largo * f, rollo.bottom), 1.6f * u)
}

/**
 * El pie del armario: el suelo de terrazo con su junta de latón, la sombra del armario y de cada pata en él; el faldón de
 * Macasar con su friso de dentellones de marfil; las patas ahusadas con sus zapatas de marfil; la pila de discos y el estuche
 * de la trompeta entero, con su campana, su asa y sus cierres. De noche, el frío de la escalera roza las patas y el estuche.
 */
internal fun DrawScope.pieDeco(d: DisposicionEstante, lado: Float, suelo: Float, a: AireDeco, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
    val u = density; val w = size.width; val h = size.height
    val pie = d.base.top
    val costado = w * COSTADO
    if (h - pie <= 1f) return
    val izq = !a.aplique || a.alba
    // El suelo de terrazo: el fondo, las chinas de tres colores y la junta de latón.
    val piso = Rect(0f, suelo, w, h)
    if (piso.height > 1f) {
        val tramos = if (a.lumbre) 10 else 1
        for (t in 0 until tramos) {
            val xa = w * t / tramos; val xb = w * (t + 1) / tramos + 0.6f
            drawRect(a.luz(PaletaDeco.TERRAZO, cerca(Offset((xa + xb) / 2, suelo)) * 0.9f), Offset(xa, suelo), Size(xb - xa, piso.height))
        }
        clipRect(piso.left, piso.top, piso.right, piso.bottom) {
            // Las chinas, en baldosa (una sola operación), apagadas con la luz que le llega al suelo.
            texturaDeco(TexturaDeco.TERRAZO, piso, a.luz(Color.White, if (a.lumbre) maxOf(cerca(Offset(w * 0.5f, suelo)), 0.1f) else 1f), if (a.oscuro) 0.6f else 0.85f)
            drawLine(a.luz(Color(0xFFC9A24C), 0.8f), Offset(0f, suelo + piso.height * 0.78f), Offset(w, suelo + piso.height * 0.74f), 1.4f * u)
            // La sombra del armario en el suelo, bajo el faldón, y la de cada pata, tendida hacia donde no está la luz.
            drawRect(a.sombra(if (a.lumbre) 0.5f else 0.32f), Offset(costado, suelo), Size(w - 2 * costado, piso.height * 0.16f))
            val tiende = (if (izq) 1f else -1f) * piso.height * 0.9f
            for (i in 0 until 2) {
                val px = if (i == 0) lado * 0.6f else w - lado * 0.6f
                val ancho = lado * 0.4f
                drawPath(TrazoDeco.poligono(px - ancho / 2, suelo, px + ancho / 2, suelo, px + ancho / 2 + tiende, suelo + piso.height * 0.6f, px - ancho / 2 + tiende, suelo + piso.height * 0.6f), a.sombra(if (a.lumbre) 0.4f else 0.24f))
            }
            if (!a.oscuro) drawRect(Color.White.copy(alpha = 0.1f), Offset(0f, suelo + piso.height * 0.3f), Size(w * 0.45f, piso.height * 0.25f))
        }
    }
    // El faldón con su friso de dentellones.
    val falda = Rect(costado, pie, w - costado, pie + (suelo - pie) * 0.42f)
    val tramosF = if (a.lumbre) 12 else 1
    for (t in 0 until tramosF) {
        val xa = falda.left + falda.width * t / tramosF; val xb = falda.left + falda.width * (t + 1) / tramosF + 0.6f
        drawRect(a.luz(MACASAR, cerca(Offset((xa + xb) / 2, falda.center.y))), Offset(xa, falda.top), Size(xb - xa, falda.height))
    }
    val dientes = Path()
    val paso = maxOf(3f * u, w * 0.018f)
    var x = falda.left + paso * 0.5f
    val dy = falda.top + falda.height * 0.58f
    while (x < falda.right - paso * 0.5f) { dientes.addRect(Rect(x, dy, x + paso * 0.5f, dy + falda.height * 0.22f)); x += paso }
    drawPath(dientes, a.luz(MARFIL, cerca(Offset(w * 0.5f, dy))).copy(alpha = 0.85f))
    drawRect(a.claro(0.3f), falda.topLeft, Size(falda.width, 1.4f * u))
    drawRect(a.sombra(0.4f), Offset(falda.left, falda.bottom - 1.6f * u), Size(falda.width, 1.6f * u))
    // Las patas ahusadas, bajo los costados, con su zapata de marfil.
    for (i in 0 until 2) {
        val x0 = if (i == 0) costado + lado * 0.1f else w - lado
        val x1 = if (i == 0) lado else w - costado - lado * 0.1f
        val angosto = (x1 - x0) * 0.25f
        val pata = TrazoDeco.poligono(x0, falda.bottom, x1, falda.bottom, x1 - angosto, suelo, x0 + angosto, suelo)
        drawPath(pata, a.luz(MACASAR, cerca(Offset((x0 + x1) / 2, (falda.bottom + suelo) / 2))))
        clipPath(pata) {
            drawRect(a.claro(0.25f), Offset(x0, falda.bottom), Size((x1 - x0) * 0.35f, suelo - falda.bottom))
            drawRect(a.sombra(0.3f), Offset(x1 - (x1 - x0) * 0.3f, falda.bottom), Size((x1 - x0) * 0.3f, suelo - falda.bottom))
            val zap = (suelo - falda.bottom) * 0.22f
            drawRect(a.luz(MARFIL, cerca(Offset((x0 + x1) / 2, suelo))), Offset(x0, suelo - zap), Size(x1 - x0, zap))
            drawRect(a.sombra(0.2f), Offset(x0, suelo - zap), Size(x1 - x0, 1f * u))
            val kf = frio(Offset(x0, (falda.bottom + suelo) / 2))
            if (kf > 0.02f) drawRect(PaletaDeco.LUNA.copy(alpha = 0.6f * kf), Offset(x0, falda.bottom), Size(1.6f * u, suelo - falda.bottom))
        }
    }
    val kfFalda = frio(Offset(falda.left, falda.top))
    if (kfFalda > 0.02f) drawRect(PaletaDeco.LUNA.copy(alpha = 0.5f * kfFalda), falda.topLeft, Size(falda.width * 0.4f, 1.2f * u))
    // La pila de discos en el suelo, en sus fundas, a la izquierda.
    val px = lado * 1.4f; val pa = w * 0.21f
    val py = suelo + piso.height * 0.55f
    contactoDeco(a, Offset(px + pa / 2, py), pa * 1.1f, w * 0.012f, 0.45f)
    for (j in 0 until 6) {
        val y = py - j * w * 0.009f
        val corre = (azar(991, j) - 0.5f) * w * 0.025f
        drawRect(a.luz(if (j % 3 == 1) TINTA else mezcla(ESTRAZA, MARFIL, 0.2f * (j % 2)), cerca(Offset(px, y))), Offset(px + corre, y - w * 0.009f), Size(pa, w * 0.009f))
    }
    drawLine(a.claro(0.4f), Offset(px, py - w * 0.054f), Offset(px + pa, py - w * 0.054f), 0.8f * u)
    estucheDeco(Rect(w * 0.5f, py - w * 0.095f, w * 0.95f, py + w * 0.01f), a, cerca, frio, izq)
}

/**
 * El estuche de la trompeta, tumbado en el suelo: cuero negro de grano, con la campana abombada a la derecha y la punta
 * angosta a la izquierda, la línea de la tapa, el asa de cuero, los dos cierres y la cerradura de níquel, y sus patitas.
 */
private fun DrawScope.estucheDeco(r: Rect, a: AireDeco, cerca: (Offset) -> Float, frio: (Offset) -> Float, izq: Boolean) {
    val u = density
    if (r.width <= 4f * u || r.height <= 4f * u) return
    val k = cerca(r.center)
    val x0 = r.left; val x1 = r.right; val y1 = r.bottom; val al = r.height; val la = r.width
    val forma = Path().apply {
        moveTo(x0 + al * 0.3f, y1)
        lineTo(x1 - al * 0.55f, y1)
        cubicTo(x1 - al * 0.1f, y1, x1, y1 - al * 0.25f, x1, y1 - al * 0.5f)
        cubicTo(x1, y1 - al * 0.85f, x1 - al * 0.2f, y1 - al, x1 - al * 0.6f, y1 - al)
        cubicTo(x1 - la * 0.3f, y1 - al, x1 - la * 0.42f, y1 - al * 0.58f, x0 + la * 0.28f, y1 - al * 0.56f)
        lineTo(x0 + al * 0.3f, y1 - al * 0.56f)
        cubicTo(x0, y1 - al * 0.56f, x0, y1, x0 + al * 0.3f, y1)
        close()
    }
    contactoDeco(a, Offset(r.center.x + (if (izq) 1f else -1f) * la * 0.04f, y1), la * 1.08f, al * 0.22f, 0.5f)
    val cuero = Color(0xFF2A2220)
    drawPath(forma, a.luz(cuero, k))
    clipPath(forma) {
        // El grano del cuero, la luz de arriba, la línea de la tapa y la sombra de abajo.
        texturaDeco(TexturaDeco.CUERO, Rect(x0, y1 - al, x1, y1), a.luz(Color.White, maxOf(k, 0.2f)), 0.9f)
        drawRect(a.claro(0.3f).copy(alpha = if (a.oscuro) 0.06f else 0.3f), Offset(x0, y1 - al), Size(la, al * 0.14f))
        val tapa = Path().apply {
            moveTo(x0, y1 - al * 0.3f); lineTo(x1 - la * 0.4f, y1 - al * 0.3f)
            cubicTo(x1 - la * 0.25f, y1 - al * 0.3f, x1 - la * 0.15f, y1 - al * 0.45f, x1, y1 - al * 0.45f)
        }
        drawPath(tapa, a.sombra(0.6f), style = Stroke(1.2f * u))
        drawPath(TrazoDeco.corrido(tapa, 0f, 1.2f * u), a.claro(0.3f).copy(alpha = if (a.oscuro) 0.06f else 0.25f), style = Stroke(0.6f * u))
        drawRect(a.sombra(0.3f), Offset(x0, y1 - al * 0.14f), Size(la, al * 0.14f))
    }
    // El asa de cuero, arriba de la campana.
    val asa = Offset(x1 - la * 0.32f, y1 - al * 0.78f)
    drawArc(a.luz(cuero, k), 180f, 180f, false, Offset(asa.x - la * 0.08f, asa.y - al * 0.32f), Size(la * 0.16f, al * 0.5f), style = Stroke(2.6f * u))
    drawArc(a.claro(0.3f).copy(alpha = if (a.oscuro) 0.08f else 0.35f), 200f, 60f, false, Offset(asa.x - la * 0.08f, asa.y - al * 0.32f), Size(la * 0.16f, al * 0.5f), style = Stroke(0.8f * u))
    // Los cierres y la cerradura, de níquel.
    for (f in listOf(0.22f, 0.5f, 0.78f)) {
        val c = Offset(x0 + la * f, y1 - al * (if (f > 0.6f) 0.42f else 0.3f))
        val ancho = la * (if (f == 0.5f) 0.035f else 0.03f)
        drawRect(a.niquel(maxOf(k, 0.3f)), Offset(c.x - ancho / 2, c.y - al * 0.09f), Size(ancho, al * 0.18f))
        drawRect(a.claro(0.6f).copy(alpha = if (a.oscuro) 0.15f else 0.6f), Offset(c.x - ancho / 2, c.y - al * 0.09f), Size(ancho, 0.8f * u))
        val kf = frio(c)
        if (kf > 0.02f) drawRect(PaletaDeco.LUNA.copy(alpha = 0.8f * kf), Offset(c.x - ancho / 2, c.y - al * 0.09f), Size(1f * u, al * 0.18f))
    }
    // Las patitas de níquel.
    drawPoints(listOf(Offset(x0 + la * 0.12f, y1), Offset(x0 + la * 0.45f, y1), Offset(x1 - la * 0.12f, y1)), PointMode.Points, a.niquel(maxOf(k, 0.3f)), 2.2f * u, StrokeCap.Round)
    val kf = frio(Offset(x0, y1 - al * 0.3f))
    if (kf > 0.02f) drawPath(forma, PaletaDeco.LUNA.copy(alpha = 0.45f * kf), style = Stroke(1f * u))
}
