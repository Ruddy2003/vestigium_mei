package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteUrbe.LAMP_X
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteUrbe.LAMP_Y
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.ALMAGRA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.AZUCAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.CIELO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.CREMA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.VIDRIADO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.TIERRA_VERDE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.GESSO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.HOJA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.LAMPARA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.NOGAL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.NOGAL_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.OCRE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.PERSIANA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.TEJA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.TELA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.ULTRAMAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.YESO
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoUrbe.contacto
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoUrbe.corrido
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoUrbe.poligono
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoUrbe.redondo
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Urbe: lo que Giulia deja en los huecos libres de las repisas
// =====================================================================

/**
 * Lo de Giulia en lo que queda libre de la repisa [k], a la derecha de [oc]: según cuánto hueco haya, un libro inclinado contra
 * el último volumen, una pila de libros tumbados con el bote de los pinceles, los botes de pigmento, un cuenco con las llaves o
 * un trozo de calco de yeso. Cada repisa escoge lo suyo, para que no se repita.
 */
internal fun DrawScope.huecoUrbe(k: Int, r: Rect, oc: Float, a: AireUrbe, cerca: Float, haciaX: Float) {
    val u = density
    val libre = (r.right - oc) / u
    val y = r.bottom - 0.5f * u
    val caida = Offset(4f * u, 2f * u)
    when {
        libre >= 150f -> if (k % 2 == 0) {
            libroInclinado(oc, y, r.height * 0.74f, a, cerca + 0.04f, k)
            librosTumbadosUrbe(Offset(oc + 34f * u, y), 64f * u, 3, a, cerca, k)
            botePincelesUrbe(Offset(oc + 58f * u, y - 3 * 8.5f * u), 18f * u, a, cerca, haciaX, caida)
            cuencoLlaves(Offset(r.right - 34f * u, y), 34f * u, a, cerca - 0.03f, haciaX)
        } else {
            libroInclinado(oc, y, r.height * 0.7f, a, cerca + 0.04f, k)
            botesPigmentoUrbe(Offset(oc + 36f * u, y), 13f * u, a, cerca, haciaX, k)
            rosetaUrbe(Offset(r.right - 36f * u, y), 19f * u, a, cerca - 0.03f, caida, entera = true)
        }
        libre >= 74f -> when (k % 3) {
            0 -> { librosTumbadosUrbe(Offset(oc + 8f * u, y), 58f * u, 2, a, cerca, k); botePincelesUrbe(Offset(oc + 30f * u, y - 2 * 8.5f * u), 17f * u, a, cerca, haciaX, caida) }
            1 -> { libroInclinado(oc, y, r.height * 0.72f, a, cerca, k); botesPigmentoUrbe(Offset(oc + 30f * u, y), 12f * u, a, cerca, haciaX, k) }
            else -> { libroInclinado(oc, y, r.height * 0.7f, a, cerca, k); cuencoLlaves(Offset(oc + 52f * u, y), 30f * u, a, cerca, haciaX) }
        }
        libre >= 46f -> when (k % 3) {
            0 -> libroInclinado(oc, y, r.height * 0.72f, a, cerca, k)
            1 -> cuencoLlaves(Offset((oc + r.right) / 2, y), 28f * u, a, cerca, haciaX)
            else -> botesPigmentoUrbe(Offset(oc + 6f * u, y), 11f * u, a, cerca, haciaX, k)
        }
        libre >= 22f -> libroInclinado(oc, y, r.height * 0.66f, a, cerca, k)
    }
}

/** Un libro de bolsillo inclinado contra el último volumen, con su lomo gastado. */
private fun DrawScope.libroInclinado(oc: Float, y: Float, alto: Float, a: AireUrbe, cerca: Float, semilla: Int) {
    val u = density
    val an = 15f * u
    val giro = 13f + 5f * azar(900, semilla)
    val s = sin(Math.toRadians(giro.toDouble())).toFloat()
    val pivote = Offset(oc + alto * s + 1.5f * u, y)
    val tapas = listOf(mezcla(OCRE, CREMA, 0.2f), mezcla(ULTRAMAR, AZUCAR, 0.35f), mezcla(PERSIANA, OCRE, 0.3f), mezcla(ALMAGRA, TEJA, 0.4f))
    val r = Rect(pivote.x, pivote.y - alto, pivote.x + an, pivote.y)
    rotate(-giro, pivote) {
        drawRect(a.sombra(if (a.lumbre) 0.36f else 0.22f), Offset(r.left + 4f * u, r.top + 2f * u), r.size)
        drawRoundRect(a.luz(tapas[Math.floorMod(semilla, tapas.size)], cerca), r.topLeft, r.size, CornerRadius(1.6f * u))
        drawRect(a.claro(0.26f), Offset(r.left + an * 0.1f, r.top), Size(an * 0.14f, alto))
        drawRect(a.sombra(0.2f), Offset(r.right - an * 0.22f, r.top), Size(an * 0.22f, alto))
        drawRect(a.luz(CREMA, cerca).copy(alpha = 0.85f), Offset(r.left + an * 0.18f, r.top + alto * 0.08f), Size(an * 0.64f, alto * 0.12f))
        for (j in 0 until 3) drawLine(a.claro(0.3f), Offset(r.left + an * (0.3f + 0.2f * j), r.top + alto * 0.3f), Offset(r.left + an * (0.32f + 0.2f * j), r.top + alto * (0.6f + 0.1f * j)), 0.6f * u)
        if (a.lumbre) drawRect(a.filo(0.45f * cerca), r.topLeft, Size(an, 0.9f * u))
    }
}

/** Un cuenco de loza, blanco por fuera y azul por dentro, con un manojo de llaves que cuelga del borde y unas monedas. */
private fun DrawScope.cuencoLlaves(pie: Offset, an: Float, a: AireUrbe, cerca: Float, haciaX: Float) {
    val u = density
    val al = an * 0.42f
    contacto(Offset(pie.x + an * 0.08f, pie.y), an * 1.2f, al * 0.2f, a, 0.4f)
    val boca = Rect(pie.x - an / 2, pie.y - al - an * 0.08f, pie.x + an / 2, pie.y - al + an * 0.08f)
    val cuenco = Path().apply {
        moveTo(boca.left, boca.center.y)
        cubicTo(boca.left + an * 0.04f, pie.y - al * 0.2f, pie.x - an * 0.2f, pie.y, pie.x, pie.y)
        cubicTo(pie.x + an * 0.2f, pie.y, boca.right - an * 0.04f, pie.y - al * 0.2f, boca.right, boca.center.y)
        close()
    }
    drawOval(a.luz(mezcla(ULTRAMAR, CREMA, 0.3f), cerca * 0.8f), boca.topLeft, boca.size)
    // Las llaves dentro: el aro y una que asoma.
    drawCircle(a.luz(LATON, cerca), an * 0.1f, Offset(pie.x + an * 0.06f, boca.center.y - 0.5f * u), style = Stroke(1.2f * u))
    drawPath(cuenco, a.luz(GESSO, cerca))
    clipPath(cuenco) { drawRect(a.luz(ULTRAMAR, cerca), Offset(boca.left, boca.center.y + al * 0.25f), Size(an, al * 0.1f)) }
    redondo(cuenco, a, cerca, haciaX)
    // La llave que cuelga por fuera del borde.
    val l0 = Offset(boca.right - an * 0.18f, boca.center.y)
    drawLine(a.luz(LATON, cerca), l0, l0 + Offset(an * 0.08f, al * 0.55f), 1.6f * u, StrokeCap.Round)
    drawCircle(a.luz(LATON, cerca), an * 0.05f, l0 + Offset(an * 0.02f, al * 0.05f), style = Stroke(1.2f * u))
    drawRect(a.luz(LATON, cerca), Offset(l0.x + an * 0.06f, l0.y + al * 0.42f), Size(an * 0.05f, al * 0.06f))
    // Unas monedas en la tabla.
    drawOval(a.luz(mezcla(LATON, YESO, 0.2f), cerca), Offset(pie.x - an * 0.72f, pie.y - an * 0.05f), Size(an * 0.16f, an * 0.06f))
}
