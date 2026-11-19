package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteUrbe.LAMAS
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteUrbe.RAYOS
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteUrbe.RAYO_DX
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteUrbe.RAYO_DY
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteUrbe.VEN_ABA
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteUrbe.VEN_ARR
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteUrbe.VEN_DER
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteUrbe.VEN_IZQ
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteUrbe.VEN_MEDIO
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteUrbe.VEN_TRAVESANO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.ALMAGRA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.ANOCHECER
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.CIELO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.CREMA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.HOJA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.LAMPARA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.LUNA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.NOCHE_SUENOS
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.OCASO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.OCRE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.PERSIANA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.SOL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.TEJA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.TRAVERTINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.YESO
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoUrbe.corrido
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoUrbe.poligono
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Urbe: el muro de estuco, la ventana honda con su persiana, el cableado viejo y la luz que entra
// =====================================================================

/**
 * El muro de estuco de almagra, viejo: a manchas y con aguas (las de una gotera que bajan del techo y la línea de una humedad
 * antigua), más gastado y más claro abajo, donde se roza; los desconchones con su borde, el enlucido de yeso y el ladrillo
 * debajo; una grieta que baja de la ventana y el calco de una voluta pegado con cinta. De día, la luz del cuarto es más clara
 * cerca de la ventana y se apaga hacia la derecha; de noche, el abanico de la pantalla y el frío de la ciudad por la
 * ventana; en los Sueños, el frío de la luna junto a la ventana.
 */
internal fun DrawScope.paredUrbe(a: AireUrbe, ch: Float, suelo: Float, cerca: (Offset) -> Float) {
    val u = density; val w = size.width
    val lejos = if (a.lumbre) 0.1f else 1f
    drawRect(a.luz(ALMAGRA, lejos), Offset.Zero, Size(w, suelo))
    val veces = (suelo / (w * 0.5f)).coerceIn(1f, 12f)
    val pano = Rect(0f, 0f, w, ch)
    // El estuco a manchas: la cal se secó desigual y la almagra no cubre igual en todas partes.
    for (j in 0 until 14) {
        val c = Offset(w * azar(815, j), ch * azar(816, j))
        val rx = w * (0.08f + 0.16f * azar(817, j)); val ry = rx * (0.45f + 0.35f * azar(818, j))
        val col = if (j % 3 == 0) mezcla(ALMAGRA, OCRE, 0.6f) else if (j % 3 == 1) mezcla(ALMAGRA, SOMBRA_MUEBLE, 0.4f) else mezcla(ALMAGRA, TEJA, 0.5f)
        drawOval(a.luz(col, lejos).copy(alpha = 0.08f + 0.06f * azar(819, j)), Offset(c.x - rx, c.y - ry), Size(2 * rx, 2 * ry))
    }
    pinceladas(pano, a.luz(mezcla(ALMAGRA, SOMBRA_MUEBLE, 0.4f), lejos), 811, 40, u, 0.1f)
    pinceladas(pano, a.luz(mezcla(ALMAGRA, OCRE, 0.55f), lejos), 812, 30, u, 0.13f)
    pinceladas(Rect(0f, ch, w, suelo), a.luz(mezcla(ALMAGRA, SOMBRA_MUEBLE, 0.4f), lejos), 814, (10 * veces).toInt(), u, 0.1f)
    // Las aguas: lo que bajó de una gotera, en lenguas largas y más oscuras, con su cerco.
    listOf(0.71f to 0.34f, 0.76f to 0.22f, 0.46f to 0.16f).forEachIndexed { j, (x, largo) ->
        val ancho = w * (0.028f + 0.012f * j)
        val agua = Path().apply {
            moveTo(w * x - ancho, 0f)
            cubicTo(w * x - ancho * 1.1f, w * largo * 0.4f, w * x - ancho * 0.5f, w * largo * 0.8f, w * x + ancho * 0.1f, w * largo)
            cubicTo(w * x + ancho * 0.5f, w * largo * 0.85f, w * x + ancho * 1.2f, w * largo * 0.4f, w * x + ancho, 0f)
            close()
        }
        drawPath(agua, a.luz(mezcla(ALMAGRA, SOMBRA_MUEBLE, 0.3f), lejos).copy(alpha = 0.16f))
        drawPath(agua, a.luz(mezcla(ALMAGRA, TINTA, 0.35f), lejos).copy(alpha = 0.22f), style = Stroke(0.8f * u))
    }
    // La línea de una humedad vieja y, debajo, el estuco más gastado y claro, rozado por lo que se apoya en la cornisa.
    val marea = Path().apply {
        moveTo(0f, w * 0.4f)
        var x = 0f; var j = 0
        while (x < w) {
            val x2 = x + w * 0.08f
            cubicTo(x + w * 0.03f, w * (0.39f + 0.02f * azar(820, j)), x2 - w * 0.03f, w * (0.41f - 0.02f * azar(821, j)), x2, w * (0.4f + 0.012f * (azar(822, j) - 0.5f)))
            x = x2; j++
        }
    }
    drawPath(marea, a.luz(mezcla(ALMAGRA, TINTA, 0.3f), lejos).copy(alpha = 0.3f), style = Stroke(1f * u))
    clipRect(0f, w * 0.4f, w, ch) {
        drawRect(a.luz(mezcla(ALMAGRA, OCRE, 0.45f), lejos).copy(alpha = 0.22f), Offset(0f, w * 0.4f), Size(w, ch - w * 0.4f))
        pinceladas(Rect(0f, w * 0.4f, w, ch), a.luz(mezcla(ALMAGRA, YESO, 0.45f), lejos), 823, 26, u, 0.16f)
    }
    // Los desconchones y la grieta.
    desconchon(Offset(w * 0.43f, w * 0.06f), w * 0.04f, 826, a, cerca(Offset(w * 0.43f, w * 0.06f)), false)
    desconchon(Offset(w * 0.66f, w * 0.31f), w * 0.05f, 824, a, cerca(Offset(w * 0.66f, w * 0.31f)), true)
    desconchon(Offset(w * 0.015f, w * 0.53f), w * 0.04f, 825, a, cerca(Offset(w * 0.015f, w * 0.53f)), false)
    val grieta = Path().apply {
        moveTo(w * 0.37f, w * 0.475f)
        lineTo(w * 0.385f, w * 0.495f); lineTo(w * 0.38f, w * 0.51f); lineTo(w * 0.395f, w * 0.53f); lineTo(w * 0.39f, w * 0.545f)
    }
    drawPath(grieta, a.sombra(0.5f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
    drawPath(corrido(grieta, 0.8f * u, 0.7f * u), a.claro(0.3f), style = Stroke(0.5f * u))
    // El calco en papel de seda de una voluta, pegado con cinta: lo que Giulia se trajo de la obra.
    lucido(Offset(w * 0.555f, w * 0.13f), w * 0.11f, a, cerca(Offset(w * 0.555f, w * 0.13f)))
    // La luz del cuarto.
    if (!a.oscuro) {
        // De día: la claridad que rebota de la ventana, arriba a la izquierda; a la derecha y abajo, penumbra.
        mancha(Offset(w * 0.2f, w * 0.25f), w * 0.85f, luzMueble(if (a.noche) 0.08f else 0.34f))
        mancha(Offset(w * 1.05f, w * 0.5f), w * 0.8f, sombraMueble(if (a.noche) 0.12f else 0.16f))
        if (a.noche) {
            mancha(Offset(w * 0.22f, w * 0.3f), w * 0.7f, OCASO.copy(alpha = 0.26f))
            mancha(Offset(w * 0.3f, w * 0.62f), w * 0.5f, OCASO.copy(alpha = 0.16f))
        }
    }
    if (a.lampara) abanicoLampara(a, w)
    if (a.oscuro && !a.noche) {
        // De noche, el resplandor frío de la ciudad por la ventana: la otra temperatura.
        mancha(Offset(w * (VEN_IZQ + VEN_DER) / 2, w * (VEN_ARR + VEN_ABA) / 2), w * 0.55f, PaletaUrbe.FRIO.copy(alpha = 0.24f))
    }
    if (a.luna) {
        val c = Offset(w * (VEN_IZQ + VEN_DER) / 2, w * (VEN_ARR + VEN_ABA) / 2)
        mancha(c, w * 0.75f, LUNA.copy(alpha = 0.14f))
        mancha(Offset(w * MuebleEstanteUrbe.LUNA_X, w * MuebleEstanteUrbe.LUNA_Y), w * 0.6f, LUNA.copy(alpha = 0.12f))
    }
}

/**
 * La luz de la lámpara en el muro: el halo y la forma de la pantalla, un abanico que se abre hacia arriba desde la boca de
 * arriba, con el borde blando (tres pasos, de más fuerte a más tenue) y el tope curvo.
 */
private fun DrawScope.abanicoLampara(a: AireUrbe, w: Float) {
    val f = if (a.oscuro) 1f else 0.75f
    val l = Offset(w * MuebleEstanteUrbe.LAMP_X, w * MuebleEstanteUrbe.LAMP_Y)
    mancha(l, w * 1.05f, LAMPARA.copy(alpha = 0.15f * f))
    mancha(l, w * 0.5f, LAMPARA.copy(alpha = 0.24f * f))
    val sup = w * 0.052f; val al = w * 0.098f
    val boca = l.y - al * 0.5f
    listOf(1.25f to 0.1f, 1.12f to 0.16f, 1f to 0.24f).forEach { (e, alfa) ->
        val abre = w * 0.13f * e; val alto = w * 0.27f * e
        val forma = Path().apply {
            moveTo(l.x - sup * 0.9f, boca)
            lineTo(l.x - sup - abre, boca - alto * 0.86f)
            cubicTo(l.x - sup - abre * 0.6f, boca - alto * 1.04f, l.x + sup + abre * 0.6f, boca - alto * 1.04f, l.x + sup + abre, boca - alto * 0.86f)
            lineTo(l.x + sup * 0.9f, boca)
            close()
        }
        clipPath(forma) { mancha(Offset(l.x, boca), alto * 1.05f, LAMPARA.copy(alpha = alfa * 1.6f * f)) }
    }
}

/**
 * Un desconchón del estuco: el borde quebrado del estuco con su canto (la sombra que hace dentro arriba a la izquierda y la luz
 * en el labio de abajo), el enlucido de yeso con su grano y, más hondo, el revoque basto y, si [ladrillo], las hiladas de
 * ladrillo con su llaga; unas escamas sueltas alrededor.
 */
private fun DrawScope.desconchon(c: Offset, r: Float, semilla: Int, a: AireUrbe, cerca: Float, ladrillo: Boolean) {
    val u = density
    fun forma(escala: Float, s: Int): Path {
        val p = Path()
        val n = 15
        for (k in 0 until n) {
            val ang = 6.283f * k / n + azar(s, k) * 0.3f
            val rr = r * escala * (0.68f + 0.32f * azar(s, k + 20) - (if (k % 4 == 1) 0.12f else 0f))
            val o = Offset(c.x + rr * cos(ang) * 1.35f, c.y + rr * sin(ang) * 0.85f)
            if (k == 0) p.moveTo(o.x, o.y) else p.lineTo(o.x, o.y)
        }
        p.close()
        return p
    }
    val k = if (a.lumbre) cerca.coerceAtLeast(0.1f) else 1f
    val fuera = forma(1f, semilla)
    val dentro = forma(0.62f, semilla + 3)
    val yeso = a.luz(mezcla(YESO, ALMAGRA, 0.12f), k)
    drawPath(fuera, yeso)
    clipPath(fuera) {
        for (j in 0 until 18) drawCircle(a.luz(mezcla(YESO, TINTA, 0.25f), k).copy(alpha = 0.4f), (0.4f + 0.5f * azar(semilla, 40 + j)) * u, Offset(c.x + r * 1.3f * (azar(semilla, 41 + j) * 2 - 1), c.y + r * 0.8f * (azar(semilla, 42 + j) * 2 - 1)))
    }
    drawPath(dentro, a.luz(mezcla(YESO, OCRE, 0.4f), k))
    clipPath(dentro) {
        pinceladas(Rect(c.x - r * 1.4f, c.y - r, c.x + r * 1.4f, c.y + r), a.luz(mezcla(OCRE, TINTA, 0.35f), k), semilla + 9, 10, u, 0.2f)
        if (ladrillo) {
            // Tres hiladas de ladrillo, con la llaga de mortero.
            val alto = r * 0.3f
            for (f in 0 until 3) {
                val y = c.y - r * 0.35f + f * alto
                drawRect(a.luz(mezcla(TEJA, ALMAGRA, 0.25f), k), Offset(c.x - r * 1.4f, y), Size(r * 2.8f, alto - 1f * u))
                var x = c.x - r * 1.4f + (if (f % 2 == 0) 0f else r * 0.35f)
                while (x < c.x + r * 1.4f) { drawLine(a.luz(mezcla(YESO, OCRE, 0.3f), k), Offset(x, y), Offset(x, y + alto - 1f * u), 1f * u); x += maxOf(1f, r * 0.7f) }
                drawLine(a.claro(0.25f), Offset(c.x - r * 1.4f, y), Offset(c.x + r * 1.4f, y), 0.6f * u)
            }
        }
    }
    // El canto del estuco: su sombra dentro, arriba a la izquierda, y la luz en el labio de abajo a la derecha.
    drawPath(TrazoUrbe.borde(fuera, 1.6f * u, 1.8f * u), a.sombra(0.42f))
    drawPath(TrazoUrbe.borde(corrido(fuera, 1.2f * u, 1.4f * u), -1.2f * u, -1.4f * u), a.luz(mezcla(ALMAGRA, CREMA, 0.4f), k))
    // Escamas sueltas alrededor, a punto de caer.
    for (j in 0 until 3) {
        val ang = 6.283f * azar(semilla, 60 + j)
        val p = Offset(c.x + r * 1.6f * cos(ang), c.y + r * 1.0f * sin(ang))
        drawPath(poligono(p.x, p.y, p.x + r * 0.16f, p.y + r * 0.04f, p.x + r * 0.1f, p.y + r * 0.13f), yeso)
    }
}

/**
 * El calco en papel de seda, pegado al muro con dos tiras de cinta: una voluta de hojas trazada con sinopia (inventada, de
 * ninguna obra). El papel transparenta el estuco y se levanta en la esquina de abajo.
 */
private fun DrawScope.lucido(c: Offset, an: Float, a: AireUrbe, cerca: Float) {
    val u = density
    val al = an * 1.25f
    val r = Rect(c.x - an / 2, c.y - al / 2, c.x + an / 2, c.y + al / 2)
    val k = if (a.lumbre) cerca.coerceAtLeast(0.08f) else 1f
    val hoja = poligono(r.left, r.top, r.right, r.top + an * 0.02f, r.right - an * 0.01f, r.bottom - an * 0.16f, r.right - an * 0.15f, r.bottom, r.left + an * 0.02f, r.bottom - an * 0.01f)
    drawPath(corrido(hoja, 2.2f * u, 2.6f * u), a.sombra(if (a.lumbre) 0.3f else 0.16f))
    drawPath(hoja, a.luz(mezcla(CREMA, LUZ_MUEBLE, 0.3f), k).copy(alpha = 0.6f))
    clipPath(hoja) {
        // La voluta: un tallo que se enrosca y sus hojas, a trazo de sinopia.
        val tinta = a.luz(mezcla(ALMAGRA, TINTA, 0.25f), k).copy(alpha = 0.8f)
        val tallo = Path().apply {
            moveTo(r.left + an * 0.12f, r.bottom - al * 0.1f)
            cubicTo(r.left + an * 0.2f, r.top + al * 0.4f, r.right - an * 0.05f, r.top + al * 0.55f, r.right - an * 0.25f, r.top + al * 0.25f)
            cubicTo(r.right - an * 0.35f, r.top + al * 0.1f, r.left + an * 0.45f, r.top + al * 0.15f, r.left + an * 0.5f, r.top + al * 0.3f)
            cubicTo(r.left + an * 0.55f, r.top + al * 0.4f, r.left + an * 0.68f, r.top + al * 0.33f, r.left + an * 0.62f, r.top + al * 0.26f)
        }
        drawPath(tallo, tinta, style = Stroke(1.1f * u, cap = StrokeCap.Round))
        for (j in 0 until 5) {
            val p0 = Offset(r.left + an * (0.18f + 0.13f * j), r.bottom - al * (0.22f + 0.13f * j))
            val hojaV = Path().apply {
                moveTo(p0.x, p0.y)
                cubicTo(p0.x - an * 0.12f, p0.y - al * 0.02f, p0.x - an * 0.14f, p0.y - al * 0.1f, p0.x - an * 0.06f, p0.y - al * 0.12f)
                cubicTo(p0.x - an * 0.08f, p0.y - al * 0.06f, p0.x - an * 0.02f, p0.y - al * 0.03f, p0.x, p0.y)
            }
            drawPath(hojaV, tinta, style = Stroke(0.8f * u))
        }
        // Las líneas de encaje del calco: las cruces de registro.
        listOf(Offset(r.left + an * 0.1f, r.top + al * 0.08f), Offset(r.right - an * 0.12f, r.bottom - al * 0.2f)).forEach { q ->
            drawLine(tinta.copy(alpha = 0.5f), q - Offset(an * 0.05f, 0f), q + Offset(an * 0.05f, 0f), 0.6f * u)
            drawLine(tinta.copy(alpha = 0.5f), q - Offset(0f, an * 0.05f), q + Offset(0f, an * 0.05f), 0.6f * u)
        }
    }
    // La esquina que se levanta, con el envés del papel.
    drawPath(poligono(r.right - an * 0.01f, r.bottom - an * 0.16f, r.right - an * 0.15f, r.bottom, r.right - an * 0.19f, r.bottom - an * 0.2f), a.luz(mezcla(CREMA, YESO, 0.5f), k))
    drawPath(poligono(r.right - an * 0.15f, r.bottom, r.right - an * 0.19f, r.bottom - an * 0.2f, r.right - an * 0.12f, r.bottom - an * 0.06f), a.sombra(0.15f))
    // Las dos tiras de cinta de papel, torcidas.
    listOf(Offset(r.left + an * 0.04f, r.top + 1f * u) to -30f, Offset(r.right - an * 0.04f, r.top + 1f * u) to 25f).forEach { (q, g) ->
        val t = Path().apply { addRect(Rect(q.x - an * 0.1f, q.y - an * 0.035f, q.x + an * 0.1f, q.y + an * 0.035f)) }
        val m = androidx.compose.ui.graphics.Matrix().apply { translate(q.x, q.y); rotateZ(g); translate(-q.x, -q.y) }
        t.transform(m)
        drawPath(t, a.luz(mezcla(CREMA, OCRE, 0.35f), k).copy(alpha = 0.85f))
    }
}

/**
 * La ventana honda del muro grueso, arriba a la izquierda: el derrame enlucido de los lados y de arriba, el alféizar de
 * travertino, el marco de madera pintado de blanco con sus dos hojas, su travesaño y la manilla de latón. Detrás de la hoja
 * izquierda, la persiana verde cerrada con la luz entre las lamas; por la derecha, abierta, los tejados: el cielo, un pino,
 * una antena, las fachadas con sus persianas, una terraza con ropa tendida y un tejado de teja; la hoja abierta de la
 * persiana, al borde. De día, el sol; de noche, el azul hondo con las ventanas encendidas; al anochecer (Sueños, claro), el
 * cielo rosa; con la luna (Sueños, oscuro), la luna.
 */
internal fun DrawScope.ventanaUrbe(a: AireUrbe, cerca: (Offset) -> Float) {
    val u = density; val w = size.width
    val hueco = Rect(w * (VEN_IZQ - 0.034f), w * (VEN_ARR - 0.034f), w * (VEN_DER + 0.034f), w * (VEN_ABA + 0.027f))
    val marco = Rect(w * (VEN_IZQ - 0.012f), w * (VEN_ARR - 0.013f), w * (VEN_DER + 0.012f), w * (VEN_ABA + 0.013f))
    val vid = Rect(w * VEN_IZQ, w * VEN_ARR, w * VEN_DER, w * VEN_ABA)
    val medio = w * VEN_MEDIO
    val k = if (a.lumbre) cerca(hueco.center) else 1f
    // El derrame: arriba, en sombra; a la izquierda, con el rebote; a la derecha, donde da el sol.
    val enlucido = mezcla(YESO, OCRE, 0.25f)
    drawRect(a.sombra(if (a.lumbre) 0.4f else 0.22f), Offset(hueco.left + 2f * u, hueco.top + 3f * u), hueco.size)
    drawPath(poligono(hueco.left, hueco.top, hueco.right, hueco.top, marco.right, marco.top, marco.left, marco.top), a.luz(mezcla(enlucido, SOMBRA_MUEBLE, 0.3f), k))
    drawPath(poligono(hueco.left, hueco.top, marco.left, marco.top, marco.left, marco.bottom, hueco.left, hueco.bottom), a.luz(mezcla(enlucido, SOMBRA_MUEBLE, 0.12f), k))
    val derrameDer = poligono(hueco.right, hueco.top, hueco.right, hueco.bottom, marco.right, marco.bottom, marco.right, marco.top)
    drawPath(derrameDer, a.luz(enlucido, k))
    // El sol (o la luna) en el derrame de la derecha, rayado por las lamas.
    val luzFuera: Color? = when { a.luna -> LUNA; a.ocaso -> OCASO; a.oscuro -> null; else -> SOL }
    if (luzFuera != null) clipPath(derrameDer) {
        val paso = (marco.height) / LAMAS
        for (i in 0 until LAMAS) drawRect(luzFuera.copy(alpha = if (a.oscuro) 0.4f else 0.55f), Offset(marco.right, marco.top + paso * (i + 0.56f)), Size(hueco.right - marco.right, paso * 0.44f))
    }
    drawLine(a.sombra(0.3f), Offset(hueco.left, hueco.top), Offset(marco.left, marco.top), 0.6f * u)
    drawLine(a.sombra(0.2f), Offset(hueco.right, hueco.top), Offset(marco.right, marco.top), 0.6f * u)
    // Lo de fuera, en la hoja derecha; la persiana cerrada, en la izquierda.
    val fuera = Rect(medio, vid.top, vid.right, vid.bottom)
    clipRect(fuera.left, fuera.top, fuera.right, fuera.bottom) { vistaUrbe(fuera, a) }
    val izq = Rect(vid.left, vid.top, medio, vid.bottom)
    persianaCerrada(izq, a, luzFuera)
    // El marco y las hojas: pintura blanca, saltada en los cantos.
    val pintura = a.luz(CREMA, maxOf(k, 0.12f))
    val canto = w * 0.009f
    clipRect(marco.left, marco.top, marco.right, marco.bottom) {
        drawRect(pintura, marco.topLeft, Size(marco.width, vid.top - marco.top + canto * 0.3f))
        drawRect(pintura, Offset(marco.left, vid.bottom - canto * 0.3f), Size(marco.width, marco.bottom - vid.bottom + canto * 0.3f))
        drawRect(pintura, marco.topLeft, Size(vid.left - marco.left + canto * 0.3f, marco.height))
        drawRect(pintura, Offset(vid.right - canto * 0.3f, marco.top), Size(marco.right - vid.right + canto * 0.3f, marco.height))
        // El montante del medio, donde se juntan las hojas, y el travesaño.
        drawRect(pintura, Offset(medio - w * 0.009f, vid.top), Size(w * 0.018f, vid.height))
        drawRect(pintura, Offset(vid.left, w * VEN_TRAVESANO - w * 0.004f), Size(vid.width, w * 0.008f))
        // El canto de las hojas: una línea de sombra.
        drawLine(a.sombra(0.3f), Offset(medio, vid.top), Offset(medio, vid.bottom), 0.7f * u)
        drawRect(a.sombra(0.22f), Offset(vid.left, w * VEN_TRAVESANO + w * 0.004f), Size(vid.width, 1f * u))
        drawRect(a.claro(0.4f), Offset(marco.left, marco.top), Size(marco.width, 0.9f * u))
        // La pintura saltada: la madera gris que asoma.
        for (j in 0 until 5) {
            val p = Offset(marco.left + marco.width * azar(831, j), if (j % 2 == 0) marco.bottom - canto * 0.6f else marco.top + canto * 0.4f)
            drawOval(a.luz(mezcla(TRAVERTINO, TINTA, 0.35f), k), Offset(p.x, p.y - 0.8f * u), Size((2f + 3f * azar(832, j)) * u, 1.6f * u))
        }
    }
    drawRect(a.sombra(0.25f), Offset(vid.left, vid.top), Size(vid.width, 1.2f * u))
    // La manilla de latón en el montante.
    val m = Offset(medio, w * (VEN_TRAVESANO + 0.09f))
    drawRoundRect(a.luz(LATON, maxOf(k, 0.2f)), Offset(m.x - 1.4f * u, m.y - w * 0.012f), Size(2.8f * u, w * 0.024f), CornerRadius(1.4f * u))
    drawLine(a.luz(LATON, maxOf(k, 0.2f)), m, m + Offset(w * 0.022f, w * 0.004f), 2f * u, StrokeCap.Round)
    drawCircle(a.claro(0.6f), 0.8f * u, m - Offset(0.6f * u, w * 0.008f))
    // El reflejo en el vidrio de la hoja abierta.
    if (!a.oscuro) drawLine(luzMueble(0.22f), Offset(fuera.left + fuera.width * 0.15f, fuera.bottom - fuera.height * 0.08f), Offset(fuera.right - fuera.width * 0.1f, fuera.top + fuera.height * 0.5f), 3f * u)
    // El alféizar de travertino, que vuela un poco, con su sombra.
    val alf = Rect(hueco.left - w * 0.014f, hueco.bottom - w * 0.006f, hueco.right + w * 0.014f, hueco.bottom + w * 0.014f)
    drawRect(a.sombra(if (a.lumbre) 0.45f else 0.28f), Offset(alf.left + 2f * u, alf.bottom), Size(alf.width, 4f * u))
    drawRect(a.luz(TRAVERTINO, maxOf(k, 0.12f)), alf.topLeft, alf.size)
    drawRect(a.luz(mezcla(TRAVERTINO, LUZ_MUEBLE, 0.4f), maxOf(k, 0.12f)), alf.topLeft, Size(alf.width, alf.height * 0.3f))
    for (j in 0 until 7) {
        val x = alf.left + alf.width * azar(833, j)
        drawLine(a.sombra(0.25f), Offset(x, alf.top + alf.height * (0.4f + 0.4f * azar(834, j))), Offset(x + (2f + 3f * azar(835, j)) * u, alf.top + alf.height * (0.4f + 0.4f * azar(834, j))), 0.7f * u)
    }
    drawRect(a.sombra(0.3f), Offset(alf.left, alf.bottom - 1f * u), Size(alf.width, 1f * u))
    if (luzFuera != null) drawRect(luzFuera.copy(alpha = if (a.oscuro) 0.3f else 0.35f), Offset(alf.left + alf.width * 0.4f, alf.top), Size(alf.width * 0.5f, alf.height * 0.3f))
}

/**
 * La hoja cerrada de la persiana, vista desde dentro: las lamas verdes a contraluz y, entre ellas, la luz de fuera (las mismas
 * rendijas que dibujan [MuebleEstanteUrbe.RAYOS] en el mueble).
 */
private fun DrawScope.persianaCerrada(r: Rect, a: AireUrbe, luz: Color?) {
    val u = density
    val lama = mezcla(PERSIANA, SOMBRA_MUEBLE, if (a.oscuro) 0.5f else 0.2f)
    drawRect(lama, r.topLeft, r.size)
    val paso = r.height / LAMAS
    val rendija = when {
        luz == null -> mezcla(NOCHE_SUENOS, LAMPARA, 0.12f)
        a.luna -> mezcla(LUNA, NOCHE_SUENOS, 0.25f)
        a.ocaso -> mezcla(OCASO, CREMA, 0.3f)
        else -> mezcla(SOL, CREMA, 0.4f)
    }
    for (i in 0 until LAMAS) {
        val y = r.top + paso * i
        drawRect(rendija, Offset(r.left, y + paso * 0.56f), Size(r.width, paso * 0.44f))
        // El canto de la lama, que coge la luz por abajo.
        drawRect(mezcla(lama, rendija, 0.45f), Offset(r.left, y + paso * 0.5f), Size(r.width, paso * 0.08f))
    }
    // El bastidor de la persiana, por detrás del vidrio.
    drawRect(lama, Offset(r.left, r.top), Size(r.width * 0.12f, r.height))
    drawRect(lama, Offset(r.right - r.width * 0.1f, r.top), Size(r.width * 0.1f, r.height))
    drawRect(a.sombra(0.15f), r.topLeft, Size(r.width, r.height * 0.1f))
    if (!a.oscuro) drawLine(luzMueble(0.15f), Offset(r.left + r.width * 0.2f, r.bottom), Offset(r.right, r.top + r.height * 0.45f), 2.2f * u)
}

/** Lo que se ve por la hoja abierta: el cielo, un pino, una antena, dos filas de fachadas, una terraza con ropa y un tejado. */
private fun DrawScope.vistaUrbe(r: Rect, a: AireUrbe) {
    val u = density
    val w = r.width; val h = r.height
    val noche = a.oscuro
    // El cielo, en tres bandas.
    val (alto, medio, bajo) = when {
        a.luna -> Triple(Color(0xFF0C1330), Color(0xFF1A2650), Color(0xFF2B3866))
        noche -> Triple(Color(0xFF101631), Color(0xFF1C2448), Color(0xFF3A3557))
        a.ocaso -> Triple(mezcla(ANOCHECER, CIELO, 0.25f), mezcla(OCASO, ANOCHECER, 0.35f), mezcla(OCASO, SOL, 0.4f))
        else -> Triple(CIELO, mezcla(CIELO, CREMA, 0.3f), mezcla(CIELO, CREMA, 0.6f))
    }
    drawRect(alto, r.topLeft, Size(w, h * 0.4f))
    drawRect(medio, Offset(r.left, r.top + h * 0.3f), Size(w, h * 0.25f))
    drawRect(bajo, Offset(r.left, r.top + h * 0.48f), Size(w, h * 0.3f))
    pinceladas(Rect(r.left, r.top, r.right, r.top + h * 0.6f), if (noche) Color(0xFF2A3360) else LUZ_MUEBLE, 841, 8, u, 0.12f)
    // La luna y las estrellas; o, de día y al anochecer, unas golondrinas.
    if (a.luna) {
        val c = Offset(r.left + w * 0.34f, r.top + h * 0.13f)
        mancha(c, w * 0.6f, LUNA.copy(alpha = 0.35f))
        drawCircle(LUNA, w * 0.12f, c)
        drawCircle(mezcla(LUNA, Color(0xFF8C9CC4), 0.4f), w * 0.04f, c + Offset(w * 0.03f, w * 0.02f))
    }
    if (noche) for (j in 0 until 6) drawCircle(Color(0xFFE8E4D2).copy(alpha = 0.5f + 0.4f * azar(842, j)), (0.5f + 0.5f * azar(843, j)) * u, Offset(r.left + w * azar(844, j), r.top + h * 0.35f * azar(845, j)))
    else for (j in 0 until 3) {
        val p = Offset(r.left + w * (0.2f + 0.25f * j), r.top + h * (0.08f + 0.06f * azar(846, j)))
        val s = (2.2f + 1.2f * azar(847, j)) * u
        val ave = Path().apply { moveTo(p.x - s, p.y - s * 0.3f); cubicTo(p.x - s * 0.6f, p.y - s * 0.6f, p.x - s * 0.2f, p.y - s * 0.4f, p.x, p.y); cubicTo(p.x + s * 0.2f, p.y - s * 0.4f, p.x + s * 0.6f, p.y - s * 0.6f, p.x + s, p.y - s * 0.3f) }
        drawPath(ave, (if (a.ocaso) TINTA else mezcla(TINTA, CIELO, 0.3f)).copy(alpha = 0.7f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
    }
    // El pino: el tronco que sube casi derecho por detrás de los tejados, con dos ramas, y la copa de paraguas en dos pisos,
    // cortada por el borde derecho.
    val pinoOsc = if (noche) Color(0xFF0E1426) else if (a.ocaso) mezcla(PERSIANA, ANOCHECER, 0.35f) else mezcla(PERSIANA, TINTA, 0.25f)
    val pinoLuz = if (noche) Color(0xFF1A2440) else if (a.ocaso) mezcla(HOJA, OCASO, 0.3f) else HOJA
    val corteza = if (noche) pinoOsc else mezcla(TEJA, TINTA, 0.45f)
    val pie = Offset(r.left + w * 0.8f, r.top + h * 0.6f)
    val cruz = Offset(r.left + w * 0.74f, r.top + h * 0.33f)
    drawLine(corteza, pie, cruz, 2.4f * u, StrokeCap.Round)
    drawLine(corteza, cruz, Offset(r.left + w * 0.52f, r.top + h * 0.27f), 1.4f * u, StrokeCap.Round)
    drawLine(corteza, cruz, Offset(r.left + w * 0.95f, r.top + h * 0.26f), 1.6f * u, StrokeCap.Round)
    val copa = r.top + h * 0.25f
    for (j in 0 until 6) {
        val cx = r.left + w * (0.42f + 0.16f * j)
        val cy = copa + h * 0.012f * (azar(848, j) - 0.5f)
        drawOval(pinoOsc, Offset(cx - w * 0.17f, cy - h * 0.03f), Size(w * 0.34f, h * 0.06f))
    }
    for (j in 0 until 5) {
        val cx = r.left + w * (0.5f + 0.15f * j)
        drawOval(pinoLuz, Offset(cx - w * 0.12f, copa - h * 0.05f), Size(w * 0.24f, h * 0.035f))
    }
    for (j in 0 until 3) {
        val cx = r.left + w * (0.62f + 0.18f * j)
        drawOval(mezcla(pinoLuz, CREMA, if (noche) 0f else 0.2f), Offset(cx - w * 0.07f, copa - h * 0.06f), Size(w * 0.14f, h * 0.018f))
    }
    // La antena de un tejado.
    val ant = Offset(r.left + w * 0.24f, r.top + h * 0.6f)
    val hierro = if (noche) Color(0xFF070A16) else mezcla(TINTA, CIELO, 0.25f)
    drawLine(hierro, ant, ant - Offset(0f, h * 0.24f), 0.9f * u)
    for (j in 0 until 3) {
        val y = ant.y - h * (0.22f - 0.05f * j)
        drawLine(hierro, Offset(ant.x - w * (0.16f - 0.03f * j), y), Offset(ant.x + w * (0.16f - 0.03f * j), y), 0.7f * u)
    }
    // Las fachadas de atrás (rosa y ocre) con sus ventanas, y su cornisa de teja.
    val fondo = if (noche) Color(0xFF232540) else if (a.ocaso) mezcla(PaletaUrbe.OCRE, OCASO, 0.5f) else mezcla(PaletaUrbe.OCRE, CREMA, 0.2f)
    val rosa = if (noche) Color(0xFF2C2840) else if (a.ocaso) mezcla(Color(0xFFD9906E), OCASO, 0.4f) else Color(0xFFD9906E)
    drawRect(fondo, Offset(r.left, r.top + h * 0.6f), Size(w * 0.55f, h * 0.4f))
    drawRect(rosa, Offset(r.left + w * 0.5f, r.top + h * 0.55f), Size(w * 0.5f, h * 0.45f))
    drawRect(if (noche) Color(0xFF1A1C30) else mezcla(TEJA, TINTA, 0.15f), Offset(r.left, r.top + h * 0.59f), Size(w * 0.55f, h * 0.018f))
    drawRect(if (noche) Color(0xFF1A1C30) else mezcla(TEJA, TINTA, 0.15f), Offset(r.left + w * 0.5f, r.top + h * 0.54f), Size(w * 0.5f, h * 0.018f))
    for (f in 0 until 2) for (c in 0 until 2) {
        val x = r.left + w * (0.08f + 0.22f * c)
        val y = r.top + h * (0.64f + 0.1f * f)
        val x2 = r.left + w * (0.58f + 0.2f * c)
        val y2 = r.top + h * (0.6f + 0.1f * f)
        val enc = azar(849, f * 2 + c) < 0.55f
        val vent = if (noche) (if (enc) LAMPARA.copy(alpha = 0.85f) else Color(0xFF11131F)) else mezcla(PERSIANA, TINTA, 0.15f)
        if (f == 0) drawRect(vent, Offset(x, y), Size(w * 0.09f, h * 0.05f))
        drawRect(if (noche) (if (azar(850, f * 2 + c) < 0.5f) LAMPARA.copy(alpha = 0.8f) else Color(0xFF11131F)) else mezcla(PERSIANA, TINTA, 0.15f), Offset(x2, y2), Size(w * 0.08f, h * 0.05f))
    }
    // La terraza: el antepecho, la cuerda con dos prendas que cuelgan y una maceta.
    val ter = r.top + h * 0.82f
    drawRect(if (noche) Color(0xFF191A2C) else mezcla(CREMA, PaletaUrbe.OCRE, 0.35f), Offset(r.left, ter), Size(w * 0.6f, h * 0.035f))
    val cuerda = Path().apply { moveTo(r.left, ter - h * 0.07f); cubicTo(r.left + w * 0.2f, ter - h * 0.055f, r.left + w * 0.4f, ter - h * 0.055f, r.left + w * 0.6f, ter - h * 0.075f) }
    drawPath(cuerda, if (noche) Color(0xFF3A3A55) else mezcla(TINTA, CREMA, 0.4f), style = Stroke(0.5f * u))
    listOf(Triple(0.1f, CREMA, 0.08f), Triple(0.34f, Color(0xFF6E8FB0), 0.065f)).forEachIndexed { i, (x, col, alto0) ->
        val px = r.left + w * x; val py = ter - h * (0.066f + 0.005f * i)
        val prenda = Path().apply {
            moveTo(px, py); lineTo(px + w * 0.16f, py + h * 0.002f)
            lineTo(px + w * 0.155f, py + h * alto0); cubicTo(px + w * 0.11f, py + h * (alto0 + 0.01f), px + w * 0.05f, py + h * (alto0 + 0.01f), px + w * 0.004f, py + h * alto0 * 0.9f); close()
        }
        drawPath(prenda, if (noche) mezcla(col, Color(0xFF151830), 0.75f) else if (a.ocaso) mezcla(col, OCASO, 0.25f) else col)
        drawLine(a.sombra(0.2f), Offset(px + w * 0.06f, py), Offset(px + w * 0.07f, py + h * alto0), 0.5f * u)
    }
    drawRect(if (noche) Color(0xFF241A1A) else TEJA, Offset(r.left + w * 0.42f, ter - h * 0.03f), Size(w * 0.12f, h * 0.03f))
    drawOval(if (noche) Color(0xFF101A14) else HOJA, Offset(r.left + w * 0.4f, ter - h * 0.055f), Size(w * 0.16f, h * 0.03f))
    // El tejado de delante, de teja, con sus canales.
    val tej = Path().apply {
        moveTo(r.left + w * 0.45f, r.bottom); lineTo(r.left + w * 0.62f, r.top + h * 0.84f); lineTo(r.right, r.top + h * 0.8f); lineTo(r.right, r.bottom); close()
    }
    drawPath(tej, if (noche) Color(0xFF2A1C24) else if (a.ocaso) mezcla(TEJA, OCASO, 0.3f) else TEJA)
    clipPath(tej) {
        var x = r.left + w * 0.45f
        while (x < r.right) {
            drawLine(if (noche) Color(0xFF1A1018) else mezcla(TEJA, TINTA, 0.35f), Offset(x, r.bottom), Offset(x + w * 0.12f, r.top + h * 0.8f), 1f * u)
            x += maxOf(1f, w * 0.09f)
        }
    }
    // La hoja abierta de la persiana, contra la fachada, al borde derecho.
    val hoja = Rect(r.right - w * 0.11f, r.top, r.right, r.bottom)
    drawRect(mezcla(PERSIANA, if (noche) NOCHE_SUENOS else SOMBRA_MUEBLE, if (noche) 0.55f else 0.15f), hoja.topLeft, hoja.size)
    var y = hoja.top + 2f * u
    while (y < hoja.bottom) { drawLine(mezcla(PERSIANA, if (noche) NOCHE_SUENOS else LUZ_MUEBLE, 0.4f), Offset(hoja.left, y), Offset(hoja.right, y), 0.7f * u); y += maxOf(1f, 3.2f * u) }
}

/**
 * El cableado de antes, por fuera del muro: el cordón trenzado sobre sus aisladores de porcelana, que baja del techo hasta el
 * interruptor de pera giratoria; el enchufe de al lado, con el cordón de la lámpara que cuelga en curva.
 */
internal fun DrawScope.cableadoUrbe(a: AireUrbe, apoyo: Float, cerca: (Offset) -> Float) {
    val u = density; val w = size.width
    val x = w * 0.935f
    val sw = Offset(x, w * 0.27f)
    val en = Offset(x, w * 0.345f)
    val k = if (a.lumbre) cerca(sw) else 1f
    val cordon = a.luz(mezcla(TEJA, TINTA, 0.55f), k)
    // El cordón que baja del techo, con su trenzado.
    drawLine(a.sombra(0.25f), Offset(x + 1.6f * u, 0f), Offset(sw.x + 1.6f * u, sw.y), 1.8f * u)
    drawLine(cordon, Offset(x, 0f), sw, 1.8f * u)
    var y = 2f * u
    while (y < sw.y - 4f * u) { drawLine(a.claro(0.3f), Offset(x - 0.8f * u, y), Offset(x + 0.8f * u, y + 1.2f * u), 0.5f * u); y += maxOf(1f, 2.6f * u) }
    // Los aisladores de porcelana.
    listOf(0.07f, 0.17f).forEach { f ->
        val p = Offset(x, w * f)
        drawCircle(a.sombra(0.3f), 3f * u, p + Offset(1.2f * u, 1.4f * u))
        drawCircle(a.luz(CREMA, k), 3f * u, p)
        drawCircle(a.claro(0.5f), 1.1f * u, p - Offset(1f * u, 1f * u))
    }
    // El cordón de la lámpara: del enchufe a la cornisa, en curva floja.
    val lam = Offset(w * (MuebleEstanteUrbe.LAMP_X + 0.03f), apoyo - 1f * u)
    val cl = Path().apply { moveTo(en.x - 2f * u, en.y + 3f * u); cubicTo(en.x - w * 0.01f, en.y + w * 0.12f, lam.x + w * 0.06f, apoyo - w * 0.02f, lam.x, lam.y) }
    drawPath(corrido(cl, 1.6f * u, 1.6f * u), a.sombra(0.22f), style = Stroke(1.5f * u))
    drawPath(cl, a.luz(mezcla(CREMA, TINTA, 0.25f), k), style = Stroke(1.5f * u, cap = StrokeCap.Round))
    // El interruptor y el enchufe: cajas redondas de porcelana.
    listOf(sw, en).forEachIndexed { i, p ->
        val r = w * 0.016f
        drawCircle(a.sombra(0.35f), r, p + Offset(1.5f * u, 2f * u))
        drawCircle(a.luz(CREMA, k), r, p)
        drawArc(a.sombra(0.2f), -20f, 160f, false, Offset(p.x - r * 0.8f, p.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(1f * u))
        drawCircle(a.claro(0.5f), r * 0.35f, p - Offset(r * 0.35f, r * 0.35f))
        if (i == 0) {
            drawRoundRect(a.luz(mezcla(TINTA, TEJA, 0.3f), k), Offset(p.x - r * 0.18f, p.y - r * 0.55f), Size(r * 0.36f, r * 1.1f), CornerRadius(r * 0.18f))
        } else {
            drawCircle(a.sombra(0.6f), 0.9f * u, p - Offset(r * 0.28f, 0f))
            drawCircle(a.sombra(0.6f), 0.9f * u, p + Offset(r * 0.28f, 0f))
        }
    }
}

/**
 * La luz de la ventana en el mueble y, en el aire, el haz que la trae. Es la misma forma sesgada que cae en los volúmenes
 * ([MuebleEstanteUrbe.FORMAS_RAYO]), pero sigue el relieve: en el fondo de los casilleros, que está más hondo, cae más abajo y
 * más a la derecha; en los cantos de la cornisa y de las baldas, que están más cerca, un poco antes; así se quiebra en cada
 * canto y sube por los lomos. El haz es una banda tenue por cada rendija de la persiana y otra por la hoja abierta.
 * [cornisa] es donde empieza el canto de la cornisa y [arriba], su cara de arriba.
 */
internal fun DrawScope.rayoUrbe(c: Color, a: AireUrbe, d: DisposicionEstante, arriba: Float, cornisa: Float) {
    val w = size.width
    val todo = MuebleEstanteUrbe.RAYO_TODO
    fun toca(r: Rect) = r.right / w > todo.left - 0.04f && r.left / w < todo.right + 0.04f && r.bottom / w > todo.top - 0.04f && r.top / w < todo.bottom + 0.05f
    // En el fondo de los casilleros.
    with(MuebleEstanteUrbe) {
        for (r in d.repisas) if (toca(r)) clipRect(r.left, r.top, r.right, r.bottom) { pintaRayo(c, w, -FONDO_DX, -FONDO_DY) }
        // En los cantos: la cornisa (con su cara de arriba), las baldas y el costado izquierdo.
        clipRect(0f, arriba, w, d.corona.bottom) { pintaRayo(c, w, -CANTO_DX, -CANTO_DY) }
        for (t in d.tablas) if (toca(t)) clipRect(t.left, t.top, t.right, t.bottom + 4f * density) { pintaRayo(c, w, -CANTO_DX, -CANTO_DY) }
        if (toca(d.izquierda)) clipRect(d.izquierda.left, d.izquierda.top, d.izquierda.right, d.izquierda.bottom) { pintaRayo(c, w, -CANTO_DX, -CANTO_DY) }
    }
    // El haz en el aire, del vidrio al mueble, por encima de la cornisa.
    val aire = c.copy(alpha = c.alpha * (if (a.oscuro) 0.13f else 0.11f))
    clipRect(0f, w * VEN_ABA * 0.5f, w, arriba) {
        for (t in RAYOS) {
            val x0 = (t.left - RAYO_DX) * w; val x1 = (t.right - RAYO_DX) * w
            val y0 = (t.top - RAYO_DY) * w; val y1 = (t.bottom - RAYO_DY) * w
            val b = MuebleEstanteUrbe.sesga(t.right, t.top) * w
            val e = MuebleEstanteUrbe.sesga(t.right, t.bottom) * w
            val f = MuebleEstanteUrbe.sesga(t.left, t.bottom) * w
            drawPath(poligono(x0, y0, x1, y0, b.x, b.y, e.x, e.y, f.x, f.y, x0, y1), aire, blendMode = BlendMode.Screen)
        }
        // Las motas de polvo que flotan en el haz.
        if (!a.oscuro) for (j in 0 until 16) {
            val g = azar(861, j)
            val x0 = VEN_IZQ + (VEN_DER - VEN_IZQ) * azar(862, j)
            val y0 = VEN_ARR + (VEN_ABA - VEN_ARR) * azar(863, j)
            val fin = MuebleEstanteUrbe.sesga(x0 + RAYO_DX, y0 + RAYO_DY)
            val p = Offset(x0 + (fin.x - x0) * g, y0 + (fin.y - y0) * g) * w
            drawCircle(luzMueble(0.5f + 0.4f * azar(864, j)), (0.5f + 0.6f * azar(865, j)) * density, p)
        }
    }
}
