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
//  Urbe: lo que hay sobre la cornisa, la lámpara, el pie y los huecos libres
// =====================================================================


/**
 * Lo de Giulia sobre la cornisa, de izquierda a derecha: el potos en su maceta de barro, con las guías que caen por el costado;
 * la tablilla de yeso con las pruebas de color del fresco, apoyada en el muro; los botes de pigmento y el bote de los pinceles
 * con los hisopos; el calco de yeso de un florón, roto; la lámpara de pantalla plisada; y dos libros tumbados, cortados por el
 * borde.
 */
internal fun DrawScope.cornisaUrbe(apoyo: Float, ch: Float, a: AireUrbe, cerca: (Offset) -> Float) {
    val w = size.width
    fun caida(p: Offset, largo: Float): Offset {
        if (!a.lumbre || a.ocaso) return Offset(largo * 1.1f, largo * 0.5f)
        val luz = if (a.luna) Offset(w * MuebleEstanteUrbe.LUNA_X, w * 0.3f) else Offset(w * LAMP_X, w * LAMP_Y)
        val v = p - luz; val dd = v.getDistance().coerceAtLeast(1f)
        return Offset(v.x / dd * largo * 2.6f, minOf(v.y / dd * largo * 2.6f, largo * 0.5f))
    }
    fun hacia(x: Float) = if (a.luna) (if (w * MuebleEstanteUrbe.LUNA_X >= x) 1f else -1f) else if (w * LAMP_X >= x) 1f else -1f
    val pTabla = Offset(w * 0.255f, apoyo)
    pruebaColor(pTabla, w * 0.105f, a, cerca(pTabla - Offset(0f, w * 0.04f)), caida(pTabla, w * 0.008f))
    val pRoseta = Offset(w * 0.625f, apoyo)
    rosetaUrbe(pRoseta, w * 0.046f, a, cerca(pRoseta - Offset(0f, w * 0.05f)), caida(pRoseta, w * 0.01f), entera = false)
    botesPigmentoUrbe(Offset(w * 0.385f, apoyo), w * 0.03f, a, cerca(Offset(w * 0.4f, apoyo - w * 0.02f)), hacia(w * 0.4f), 0)
    botePincelesUrbe(Offset(w * 0.505f, apoyo), w * 0.042f, a, cerca(Offset(w * 0.5f, apoyo - w * 0.04f)), hacia(w * 0.5f), caida(Offset(w * 0.5f, apoyo), w * 0.008f))
    librosTumbadosUrbe(Offset(w * 0.885f, apoyo), w * 0.15f, 2, a, cerca(Offset(w * 0.93f, apoyo)), 5)
    lamparaUrbe(apoyo, a, caida(Offset(w * LAMP_X, apoyo - w * 0.06f), w * 0.012f))
    potos(Offset(w * 0.115f, apoyo), w * 0.07f, ch, a, cerca(Offset(w * 0.1f, apoyo - w * 0.03f)), hacia(w * 0.11f))
}

/**
 * La tablilla de yeso de las pruebas de color, apoyada en el muro: seis cuadros de pigmento al fresco (ocre, almagra, tierra
 * verde, ultramar, teja y blanco), con su borde de brochazo, una esquina rota y su sombra.
 */
private fun DrawScope.pruebaColor(pie: Offset, an: Float, a: AireUrbe, cerca: Float, caida: Offset) {
    val u = density
    val al = an * 0.74f
    val r = Rect(pie.x - an / 2, pie.y - al, pie.x + an / 2, pie.y)
    val forma = poligono(r.left, r.top + an * 0.03f, r.right - an * 0.12f, r.top, r.right, r.top + an * 0.1f, r.right, r.bottom, r.left, r.bottom)
    translate(caida.x, caida.y) { drawPath(forma, a.sombra(if (a.lumbre) 0.4f else 0.24f)) }
    contacto(Offset(pie.x + an * 0.05f, pie.y), an * 1.05f, an * 0.06f, a)
    drawPath(forma, a.luz(GESSO, cerca))
    val colores = listOf(OCRE, ALMAGRA, TIERRA_VERDE, ULTRAMAR, TEJA, mezcla(GESSO, CREMA, 0.5f))
    val cw = an * 0.27f; val chh = al * 0.36f
    clipPath(forma) {
        for (j in 0 until 6) {
            val c = j % 3; val f = j / 3
            val x = r.left + an * 0.07f + c * (cw + an * 0.03f)
            val y = r.top + al * 0.1f + f * (chh + al * 0.08f)
            drawRect(a.luz(colores[j], cerca), Offset(x, y), Size(cw, chh))
            // El brochazo: un borde menos parejo y la huella de las cerdas.
            drawRect(a.luz(colores[j], cerca).copy(alpha = 0.6f), Offset(x + cw * 0.85f, y + chh * 0.15f), Size(cw * 0.25f, chh * 0.6f))
            drawLine(a.claro(0.2f), Offset(x + cw * 0.1f, y + chh * 0.3f), Offset(x + cw * 0.8f, y + chh * 0.32f), 0.5f * u)
        }
        // El canto de arriba, que coge la luz, y la sombra a la derecha.
        drawRect(a.claro(0.4f), r.topLeft, Size(an, 1.2f * u))
        drawRect(a.sombra(0.14f), Offset(r.right - an * 0.12f, r.top), Size(an * 0.12f, al))
    }
    drawPath(forma, a.sombra(0.3f), style = Stroke(0.6f * u))
}

/**
 * El calco de yeso de un florón de techo: ocho pétalos alrededor del botón, con su relieve (la luz arriba a la izquierda);
 * [entera] falso lo deja roto en diagonal, como sale de la obra.
 */
internal fun DrawScope.rosetaUrbe(pie: Offset, r: Float, a: AireUrbe, cerca: Float, caida: Offset, entera: Boolean) {
    val u = density
    val c = Offset(pie.x, pie.y - r * 1.02f)
    val disco = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
    val roto = if (entera) disco else Path().apply {
        op(disco, poligono(c.x + r * 0.2f, c.y - r * 1.2f, c.x + r * 1.3f, c.y - r * 1.2f, c.x + r * 1.3f, c.y + r * 0.1f, c.x + r * 0.75f, c.y - r * 0.05f, c.x + r * 0.55f, c.y - r * 0.5f), androidx.compose.ui.graphics.PathOperation.Difference)
    }
    translate(caida.x, caida.y) { drawPath(roto, a.sombra(if (a.lumbre) 0.42f else 0.24f)) }
    contacto(Offset(pie.x + r * 0.1f, pie.y), r * 1.6f, r * 0.12f, a)
    val yeso = a.luz(GESSO, cerca)
    drawPath(roto, yeso)
    clipPath(roto) {
        // El fondo del florón, hundido, y la sarta de perlas del borde.
        drawCircle(a.sombra(0.16f), r * 0.82f, c + Offset(r * 0.03f, r * 0.03f))
        drawCircle(yeso, r * 0.76f, c)
        for (j in 0 until 18) {
            val rad = Math.toRadians(20.0 * j)
            val p = Offset(c.x + r * 0.89f * cos(rad).toFloat(), c.y + r * 0.89f * sin(rad).toFloat())
            drawCircle(a.sombra(0.22f), r * 0.07f, p + Offset(r * 0.025f, r * 0.03f))
            drawCircle(a.claro(0.35f), r * 0.045f, p - Offset(r * 0.02f, r * 0.02f))
        }
        // Las ocho hojas: gotas que salen del botón, con su nervio en sombra.
        for (j in 0 until 8) {
            val ang = 45.0 * j
            val rad = Math.toRadians(ang)
            val dx = cos(rad).toFloat(); val dy = sin(rad).toFloat()
            val raiz = Offset(c.x + r * 0.16f * dx, c.y + r * 0.16f * dy)
            val punta = Offset(c.x + r * 0.7f * dx, c.y + r * 0.7f * dy)
            val nx = -dy * r * 0.17f; val ny = dx * r * 0.17f
            val mid = Offset(c.x + r * 0.46f * dx, c.y + r * 0.46f * dy)
            val gota = Path().apply {
                moveTo(raiz.x, raiz.y)
                cubicTo(mid.x + nx * 1.4f, mid.y + ny * 1.4f, punta.x + nx * 0.6f, punta.y + ny * 0.6f, punta.x, punta.y)
                cubicTo(punta.x - nx * 0.6f, punta.y - ny * 0.6f, mid.x - nx * 1.4f, mid.y - ny * 1.4f, raiz.x, raiz.y)
                close()
            }
            drawPath(corrido(gota, r * 0.05f, r * 0.06f), a.sombra(0.24f))
            drawPath(gota, yeso)
            drawLine(a.sombra(0.2f), raiz, punta, 0.6f * u)
            drawLine(a.claro(0.4f), raiz - Offset(0.6f * u, 0.6f * u), mid - Offset(0.6f * u, 0.6f * u), 0.6f * u)
        }
        drawCircle(a.sombra(0.28f), r * 0.17f, c + Offset(r * 0.04f, r * 0.05f))
        drawCircle(yeso, r * 0.15f, c)
        drawCircle(a.claro(0.5f), r * 0.06f, c - Offset(r * 0.04f, r * 0.04f))
        // El polvo de la obra y la sombra del lado contrario a la luz.
        drawRect(a.sombra(0.1f), Offset(c.x + r * 0.35f, c.y - r), Size(r * 0.8f, r * 2f))
    }
    drawArc(a.claro(0.5f), 180f, 90f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(1f * u))
    if (!entera) drawPath(roto, a.sombra(0.25f), style = Stroke(0.6f * u))
}

/** Los botes de vidrio del pigmento en polvo, con su tapa de latón: ocre, ultramar y tierra verde, llenos hasta distinta altura. */
internal fun DrawScope.botesPigmentoUrbe(izq: Offset, an: Float, a: AireUrbe, cerca: Float, haciaX: Float, semilla: Int) {
    val u = density
    val polvos = listOf(OCRE, ULTRAMAR, TIERRA_VERDE)
    for (j in 0 until 3) {
        val al = an * (1.35f + 0.35f * azar(870 + semilla, j))
        val x = izq.x + j * an * 1.08f
        val r = Rect(x, izq.y - al, x + an, izq.y)
        contacto(Offset(r.center.x + an * 0.1f, izq.y), an * 1.3f, an * 0.18f, a)
        val vidrio = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(r.left, r.top + al * 0.18f, r.right, r.bottom), CornerRadius(an * 0.18f))) }
        drawPath(vidrio, a.luz(mezcla(CIELO, CREMA, 0.6f), cerca).copy(alpha = 0.5f))
        clipPath(vidrio) {
            val lleno = 0.35f + 0.4f * azar(871 + semilla, j)
            drawRect(a.luz(polvos[(j + semilla) % 3], cerca), Offset(r.left, r.bottom - al * lleno), Size(an, al * lleno))
            drawRect(a.claro(0.3f), Offset(r.left, r.bottom - al * lleno), Size(an, 1f * u))
        }
        redondo(vidrio, a, cerca, haciaX, 0.8f)
        drawLine(a.claro(0.6f), Offset(r.left + an * 0.22f, r.top + al * 0.3f), Offset(r.left + an * 0.22f, r.bottom - al * 0.12f), 0.8f * u)
        // La tapa de latón.
        drawRoundRect(a.luz(LATON, cerca), Offset(r.left - an * 0.04f, r.top + al * 0.05f), Size(an * 1.08f, al * 0.16f), CornerRadius(an * 0.06f))
        drawRect(a.claro(0.4f), Offset(r.left, r.top + al * 0.06f), Size(an, 0.8f * u))
        drawRect(a.sombra(0.25f), Offset(r.left - an * 0.04f, r.top + al * 0.17f), Size(an * 1.08f, 0.8f * u))
    }
}

/** El bote de los pinceles: un tarro de loza con su raya azul, los pinceles de mango de madera y los hisopos de algodón. */
internal fun DrawScope.botePincelesUrbe(pie: Offset, an: Float, a: AireUrbe, cerca: Float, haciaX: Float, caida: Offset) {
    val u = density
    val al = an * 1.2f
    val r = Rect(pie.x - an / 2, pie.y - al, pie.x + an / 2, pie.y)
    // Lo que asoma, detrás del borde: los pinceles, de alturas y colores distintos, y dos hisopos.
    val mangos = listOf(Triple(-0.3f, 2.3f, mezcla(TEJA, NOGAL, 0.3f)), Triple(-0.05f, 2.7f, TINTA), Triple(0.18f, 2.1f, mezcla(OCRE, NOGAL, 0.3f)), Triple(0.34f, 1.8f, ALMAGRA))
    mangos.forEachIndexed { i, (dx, largo, col) ->
        val p0 = Offset(pie.x + an * dx * 0.5f, r.top + 2f * u)
        val p1 = Offset(pie.x + an * dx * 1.3f, r.bottom - an * largo)
        drawLine(a.sombra(0.2f), p0 + caida, p1 + caida, 1.8f * u)
        drawLine(a.luz(col, cerca), p0, p1, 1.8f * u, StrokeCap.Round)
        // El casquillo y las cerdas.
        val q = p1 + (p0 - p1) * 0.12f
        drawLine(a.luz(mezcla(LATON, CREMA, 0.3f), cerca), p1, q, 2.2f * u)
        drawLine(a.luz(if (i % 2 == 0) NOGAL_OSC else mezcla(CREMA, OCRE, 0.4f), cerca), p1, p1 + (p1 - q) * 0.6f, 2f * u, StrokeCap.Round)
    }
    listOf(-0.15f, 0.05f).forEachIndexed { i, dx ->
        val p0 = Offset(pie.x + an * dx, r.top + 2f * u)
        val p1 = Offset(pie.x + an * (dx - 0.25f + 0.5f * i), r.top - an * (0.55f + 0.2f * i))
        drawLine(a.luz(CREMA, cerca), p0, p1, 0.9f * u)
        drawOval(a.luz(GESSO, cerca), Offset(p1.x - 1.6f * u, p1.y - 2.2f * u), Size(3.2f * u, 4f * u))
    }
    contacto(Offset(pie.x + an * 0.1f, pie.y), an * 1.3f, an * 0.12f, a)
    val tarro = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(r, CornerRadius(an * 0.12f))) }
    drawPath(tarro, a.luz(GESSO, cerca))
    clipPath(tarro) {
        drawRect(a.luz(ULTRAMAR, cerca), Offset(r.left, r.top + al * 0.22f), Size(an, al * 0.1f))
        drawRect(a.luz(ULTRAMAR, cerca).copy(alpha = 0.7f), Offset(r.left, r.top + al * 0.36f), Size(an, al * 0.03f))
        // Un chorreón de pintura seca.
        drawRoundRect(a.luz(OCRE, cerca), Offset(r.left + an * 0.62f, r.top), Size(an * 0.08f, al * 0.3f), CornerRadius(an * 0.04f))
    }
    redondo(tarro, a, cerca, haciaX)
    drawRect(a.luz(mezcla(GESSO, TINTA, 0.25f), cerca), Offset(r.left, r.top), Size(an, 1.4f * u))
}

/** Libros tumbados, uno sobre otro, vistos por el canto de las hojas o por el lomo; cortados por el borde si no caben. */
internal fun DrawScope.librosTumbadosUrbe(izq: Offset, an: Float, cuantos: Int, a: AireUrbe, cerca: Float, semilla: Int) {
    val u = density
    val g = 8.5f * u
    val tapas = listOf(mezcla(ALMAGRA, NOGAL, 0.25f), mezcla(ULTRAMAR, AZUCAR, 0.4f), mezcla(OCRE, CREMA, 0.3f), mezcla(PERSIANA, AZUCAR, 0.3f))
    contacto(Offset(izq.x + an * 0.55f, izq.y), an * 1.1f, 4f * u, a, 0.36f)
    drawRect(a.sombra(if (a.lumbre) 0.34f else 0.2f), Offset(izq.x + 6f * u, izq.y - cuantos * g + 3f * u), Size(an, cuantos * g - 3f * u))
    for (j in 0 until cuantos) {
        val dx = (azar(semilla + 50, j) - 0.5f) * 7f * u
        val r = Rect(izq.x + dx, izq.y - (j + 1) * g, izq.x + dx + an * (0.86f + 0.14f * azar(semilla + 51, j)), izq.y - j * g)
        val tapa = a.luz(tapas[(semilla + j) % tapas.size], cerca)
        if ((semilla + j) % 2 == 0) {
            // Por el canto: las hojas entre las dos tapas.
            drawRect(tapa, r.topLeft, Size(r.width, r.height - 0.6f * u))
            drawRect(a.luz(mezcla(CREMA, OCRE, 0.12f), cerca), Offset(r.left + 2f * u, r.top + 1.6f * u), Size(r.width - 3f * u, r.height - 3.8f * u))
            for (k in 1 until 4) drawLine(a.sombra(0.12f), Offset(r.left + 2f * u, r.top + 1.6f * u + k * (r.height - 3.8f * u) / 4), Offset(r.right - 1f * u, r.top + 1.6f * u + k * (r.height - 3.8f * u) / 4), 0.4f * u)
        } else {
            // Por el lomo: la tapa redondeada y una franja.
            drawRoundRect(tapa, r.topLeft, Size(r.width, r.height - 0.6f * u), CornerRadius(2f * u))
            drawRect(a.luz(CREMA, cerca).copy(alpha = 0.8f), Offset(r.left + r.width * 0.12f, r.top + 2f * u), Size(r.width * 0.3f, r.height - 4.6f * u))
            drawRect(a.claro(0.25f), Offset(r.left, r.top + 1f * u), Size(r.width, 1f * u))
        }
        drawRect(a.sombra(0.25f), Offset(r.left, r.bottom - 1.6f * u), Size(r.width, 1f * u))
        if (a.lumbre && j == cuantos - 1) drawRect(a.filo(0.5f * cerca), r.topLeft, Size(r.width, 0.9f * u))
    }
}

/**
 * El potos en su maceta de barro: unas hojas de corazón que se levantan y tres guías que se escurren por la cornisa y caen por
 * el costado izquierdo, delante del muro. Lo que cruza el marco.
 */
private fun DrawScope.potos(pie: Offset, an: Float, ch: Float, a: AireUrbe, cerca: Float, haciaX: Float) {
    val u = density; val w = size.width
    val al = an * 0.82f
    val boca = pie.y - al
    val verde = a.luz(HOJA, cerca)
    val verdeOsc = a.luz(mezcla(HOJA, PERSIANA, 0.6f), cerca * 0.8f)
    val claro = a.luz(mezcla(HOJA, CREMA, 0.45f), cerca)
    val hoja = Path().apply {
        moveTo(0f, 0f)
        cubicTo(-0.55f, -0.15f, -0.6f, -0.75f, 0f, -0.55f)
        cubicTo(0.6f, -0.75f, 0.55f, -0.15f, 0f, 0f)
        close()
    }
    fun DrawScope.una(p: Offset, s: Float, giro: Float, color: Color, nervio: Boolean) {
        translate(p.x, p.y) {
            rotate(giro, Offset.Zero) {
                scale(s, Offset.Zero) { drawPath(hoja, color) }
            }
        }
        if (nervio) {
            val rad = Math.toRadians(giro.toDouble())
            drawLine(claro.copy(alpha = 0.7f), p, p + Offset(-s * 0.42f * sin(rad).toFloat(), s * 0.42f * cos(rad).toFloat()) * -1f, 0.6f * u)
        }
    }
    val sombraHoja = a.sombra(0.22f)
    // Las guías que caen: por la cornisa y por el costado.
    val guias = listOf(
        listOf(Offset(pie.x - an * 0.3f, boca), Offset(w * 0.06f, boca - an * 0.1f), Offset(w * 0.03f, ch + w * 0.02f), Offset(w * 0.035f, ch + w * 0.27f)),
        listOf(Offset(pie.x - an * 0.1f, boca), Offset(w * 0.1f, pie.y - an * 0.1f), Offset(w * 0.055f, ch + w * 0.04f), Offset(w * 0.048f, ch + w * 0.14f)),
        listOf(Offset(pie.x + an * 0.2f, boca), Offset(pie.x + an * 0.6f, pie.y - an * 0.2f), Offset(pie.x + an * 0.75f, pie.y), Offset(pie.x + an * 0.7f, ch - w * 0.012f)),
    )
    guias.forEachIndexed { g, q ->
        val tallo = Path().apply { moveTo(q[0].x, q[0].y); cubicTo(q[1].x, q[1].y, q[2].x, q[2].y, q[3].x, q[3].y) }
        drawPath(corrido(tallo, 2f * u, 2f * u), a.sombra(0.2f), style = Stroke(1f * u))
        drawPath(tallo, verdeOsc, style = Stroke(1f * u, cap = StrokeCap.Round))
        val hojas = if (g == 0) 8 else if (g == 1) 5 else 3
        for (j in 1..hojas) {
            val t = j / (hojas + 0.6f)
            val m = 1f - t
            val p = q[0] * (m * m * m) + q[1] * (3 * m * m * t) + q[2] * (3 * m * t * t) + q[3] * (t * t * t)
            val lado = if (j % 2 == 0) -1f else 1f
            val s = an * (0.34f - 0.1f * t) * (0.85f + 0.3f * azar(880 + g, j))
            val giro = 180f + lado * (35f + 25f * azar(881 + g, j))
            una(p + Offset(2.2f * u, 2.5f * u), s, giro, sombraHoja, false)
            una(p, s, giro, if (j % 3 == 0) verdeOsc else verde, true)
        }
    }
    // Las hojas de arriba, que se levantan.
    for (j in 0 until 5) {
        val p = Offset(pie.x + an * (-0.3f + 0.15f * j), boca + 1f * u)
        una(p, an * (0.36f + 0.1f * azar(882, j)), -50f + 25f * j, if (j % 2 == 0) verde else verdeOsc, true)
    }
    // La maceta de barro, con su borde, y su sombra.
    contacto(Offset(pie.x + an * 0.1f, pie.y), an * 1.2f, an * 0.12f, a)
    val maceta = poligono(pie.x - an * 0.4f, boca + al * 0.15f, pie.x + an * 0.4f, boca + al * 0.15f, pie.x + an * 0.3f, pie.y, pie.x - an * 0.3f, pie.y)
    drawPath(maceta, a.luz(TEJA, cerca))
    redondo(maceta, a, cerca, haciaX, 0.9f)
    val borde = Rect(pie.x - an * 0.46f, boca, pie.x + an * 0.46f, boca + al * 0.18f)
    drawRect(a.luz(mezcla(TEJA, OCRE, 0.25f), cerca), borde.topLeft, borde.size)
    drawRect(a.claro(0.3f), borde.topLeft, Size(borde.width, 1f * u))
    drawRect(a.sombra(0.3f), Offset(borde.left, borde.bottom), Size(borde.width, 1.2f * u))
    // La cal que deja el riego.
    drawRect(a.luz(mezcla(TEJA, GESSO, 0.5f), cerca).copy(alpha = 0.5f), Offset(pie.x - an * 0.36f, boca + al * 0.4f), Size(an * 0.7f, al * 0.06f))
}

/**
 * La lámpara de la cornisa: el pie de loza con vidriado verde cobre, el portalámparas de latón y la pantalla de tela plisada.
 * De día, apagada; de noche y al anochecer, la pantalla se enciende por dentro y deja la luz abajo, en la cornisa; con la luna,
 * apagada y fría.
 */
private fun DrawScope.lamparaUrbe(apoyo: Float, a: AireUrbe, caida: Offset) {
    val u = density; val w = size.width
    val cx = w * LAMP_X
    val k = if (a.luna) 0.22f else if (a.lampara) 0.95f else 1f
    val pieAl = w * 0.105f
    val pieAn = w * 0.07f
    val pieTop = apoyo - pieAl
    // El pie: una calabaza de loza.
    val cuerpo = Path().apply {
        moveTo(cx - pieAn * 0.18f, pieTop)
        cubicTo(cx - pieAn * 0.2f, pieTop + pieAl * 0.25f, cx - pieAn * 0.6f, pieTop + pieAl * 0.35f, cx - pieAn * 0.5f, pieTop + pieAl * 0.75f)
        cubicTo(cx - pieAn * 0.45f, apoyo - pieAl * 0.04f, cx - pieAn * 0.3f, apoyo, cx, apoyo)
        cubicTo(cx + pieAn * 0.3f, apoyo, cx + pieAn * 0.45f, apoyo - pieAl * 0.04f, cx + pieAn * 0.5f, pieTop + pieAl * 0.75f)
        cubicTo(cx + pieAn * 0.6f, pieTop + pieAl * 0.35f, cx + pieAn * 0.2f, pieTop + pieAl * 0.25f, cx + pieAn * 0.18f, pieTop)
        close()
    }
    translate(caida.x, caida.y) { drawPath(cuerpo, a.sombra(if (a.lumbre) 0.4f else 0.22f)) }
    contacto(Offset(cx + pieAn * 0.1f, apoyo), pieAn * 1.3f, pieAn * 0.14f, a)
    drawPath(cuerpo, a.luz(VIDRIADO, k))
    clipPath(cuerpo) {
        // El vidriado chorreado: más claro donde se adelgaza, y el barro del pie sin vidriar.
        for (j in 0 until 5) drawRoundRect(a.luz(mezcla(VIDRIADO, CIELO, 0.35f), k).copy(alpha = 0.6f), Offset(cx - pieAn * 0.45f + j * pieAn * 0.2f, pieTop + pieAl * (0.2f + 0.08f * azar(890, j))), Size(pieAn * 0.07f, pieAl * (0.3f + 0.2f * azar(891, j))), CornerRadius(pieAn * 0.04f))
        drawRect(a.luz(mezcla(TEJA, CREMA, 0.3f), k), Offset(cx - pieAn, apoyo - pieAl * 0.07f), Size(pieAn * 2, pieAl * 0.07f))
    }
    redondo(cuerpo, a, k, if (a.luna) -1f else 1f)
    if (!a.oscuro) drawOval(LUZ_MUEBLE.copy(alpha = if (a.noche) 0.3f else 0.55f), Offset(cx - pieAn * 0.34f, pieTop + pieAl * 0.36f), Size(pieAn * 0.12f, pieAl * 0.26f))
    // El portalámparas de latón.
    drawRect(a.luz(LATON, k), Offset(cx - pieAn * 0.12f, pieTop - pieAl * 0.2f), Size(pieAn * 0.24f, pieAl * 0.22f))
    drawRect(a.claro(0.4f), Offset(cx - pieAn * 0.1f, pieTop - pieAl * 0.2f), Size(pieAn * 0.05f, pieAl * 0.22f))
    // La pantalla plisada.
    val sup = w * 0.052f; val inf = w * 0.084f; val al = w * 0.098f
    val top = w * LAMP_Y - al * 0.5f; val bot = top + al
    val pantalla = poligono(cx - sup, top, cx + sup, top, cx + inf, bot, cx - inf, bot)
    if (!a.lampara) translate(caida.x, caida.y * 0.5f) { drawPath(pantalla, a.sombra(if (a.luna) 0.35f else 0.18f)) }
    val tela = when {
        a.lampara -> mezcla(CREMA, LAMPARA, if (a.oscuro) 0.55f else 0.35f)
        else -> a.luz(CREMA, k)
    }
    drawPath(pantalla, tela)
    clipPath(pantalla) {
        val pliegues = 14
        for (j in 0..pliegues) {
            val t = j / pliegues.toFloat()
            val x0 = cx - sup + 2 * sup * t; val x1 = cx - inf + 2 * inf * t
            val luzP = if (j % 2 == 0) a.claro(0.35f) else mezcla(tela, if (a.lampara) Color(0xFFB0703A) else SOMBRA_MUEBLE, 0.25f).copy(alpha = 0.6f)
            drawLine(luzP, Offset(x0, top), Offset(x1, bot), 1.4f * u)
        }
        if (!a.lampara) {
            // La luz de día por la izquierda; la derecha, en sombra.
            drawPath(poligono(cx + sup * 0.3f, top, cx + sup, top, cx + inf, bot, cx + inf * 0.3f, bot), a.sombra(0.18f))
        } else {
            // Encendida: más luz abajo, donde está la bombilla.
            drawRect(LAMPARA.copy(alpha = if (a.oscuro) 0.5f else 0.3f), Offset(cx - inf, bot - al * 0.35f), Size(inf * 2, al * 0.35f))
            drawRect(Color(0xFFFFF0D0).copy(alpha = 0.5f), Offset(cx - inf, bot - al * 0.12f), Size(inf * 2, al * 0.12f))
        }
    }
    // Los ribetes de arriba y de abajo.
    val ribete = if (a.lampara) mezcla(LAMPARA, OCRE, 0.4f) else a.luz(mezcla(OCRE, CREMA, 0.3f), k)
    drawLine(ribete, Offset(cx - sup, top), Offset(cx + sup, top), 1.6f * u)
    drawLine(ribete, Offset(cx - inf, bot), Offset(cx + inf, bot), 1.8f * u)
}

/** Lo que alumbra la lámpara encendida, encima de todo: el resplandor de la pantalla y la luz que cae en la cornisa. */
internal fun DrawScope.brilloLamparaUrbe(a: AireUrbe, apoyo: Float) {
    val w = size.width
    val f = if (a.oscuro) 1f else 0.5f
    val c = Offset(w * LAMP_X, w * LAMP_Y)
    mancha(c + Offset(0f, w * 0.02f), w * 0.14f, Color(0xFFFFE6B8).copy(alpha = 0.32f * f))
    // El charco de luz en la cornisa, bajo la boca de la pantalla.
    val charco = poligono(c.x - w * 0.084f, c.y + w * 0.049f, c.x + w * 0.084f, c.y + w * 0.049f, c.x + w * 0.16f, apoyo + w * 0.004f, c.x - w * 0.16f, apoyo + w * 0.004f)
    clipPath(charco) { mancha(Offset(c.x, apoyo), w * 0.2f, LAMPARA.copy(alpha = 0.5f * f)) }
}

// ---------------------------------------------------------------- al pie

/**
 * El pie de la librería: el mueble bajo de dos puertas con su cuarterón, la llave puesta con su borla y la puerta de la
 * izquierda entornada; las patas torneadas, el suelo de baldosas hidráulicas con su dibujo gastado, el bolso de lona de la
 * obra, caído, y una pila de libros grandes de arte en el suelo, cortada por el borde.
 */
internal fun DrawScope.pieUrbe(d: DisposicionEstante, suelo: Float, a: AireUrbe, cerca: (Offset) -> Float) {
    val u = density; val w = size.width; val h = size.height
    val b = d.base
    val costado = w * MuebleEstanteUrbe.COSTADO
    val pata = w * 0.016f
    val mueble = Rect(costado, b.top, w - costado, suelo - pata)
    // El suelo de baldosas hidráulicas: dos filas, en escorzo.
    baldosas(Rect(0f, suelo, w, h), a, cerca)
    // La sombra del mueble en el suelo, corrida a la derecha.
    drawRect(a.sombra(if (a.lumbre) 0.5f else 0.34f), Offset(costado + w * 0.01f, suelo), Size(w - costado, (h - suelo) * 0.3f))
    drawRect(a.sombra(0.15f), Offset(costado + w * 0.02f, suelo + (h - suelo) * 0.3f), Size(w - costado, (h - suelo) * 0.2f))
    // Las patas.
    listOf(costado + w * 0.02f, w / 2 - w * 0.015f, w - costado - w * 0.05f).forEach { x ->
        val k = cerca(Offset(x, suelo))
        drawRoundRect(a.luz(NOGAL_OSC, k), Offset(x, suelo - pata - 1f * u), Size(w * 0.03f, pata + 1f * u), CornerRadius(w * 0.01f))
        drawRect(a.claro(0.2f), Offset(x + w * 0.004f, suelo - pata), Size(w * 0.005f, pata))
    }
    // El cuerpo, por tramos (de noche, cada uno con su luz).
    val tramos = if (a.lumbre) 20 else 1
    for (t in 0 until tramos) {
        val xa = mueble.left + mueble.width * t / tramos; val xb = mueble.left + mueble.width * (t + 1) / tramos + 0.6f
        drawRect(a.luz(NOGAL, cerca(Offset((xa + xb) / 2, mueble.center.y))), Offset(xa, mueble.top), Size(xb - xa, mueble.height))
    }
    // El larguero de arriba y el zócalo de abajo.
    drawRect(a.claro(0.25f), mueble.topLeft, Size(mueble.width, 1.4f * u))
    drawRect(a.sombra(0.3f), Offset(mueble.left, mueble.top + w * 0.014f), Size(mueble.width, 1f * u))
    drawRect(a.sombra(0.3f), Offset(mueble.left, mueble.bottom - w * 0.02f), Size(mueble.width, w * 0.02f))
    drawRect(a.claro(0.2f), Offset(mueble.left, mueble.bottom - w * 0.02f), Size(mueble.width, 1f * u))
    // Los dos cajones: el de la izquierda, entreabierto, con el trapo de la obra que asoma.
    val m = w * 0.03f
    val medio = w / 2
    val cajones = listOf(Rect(mueble.left + m, mueble.top + w * 0.022f, medio - 1.2f * u, mueble.bottom - w * 0.026f), Rect(medio + 1.2f * u, mueble.top + w * 0.022f, mueble.right - m, mueble.bottom - w * 0.026f))
    cajones.forEachIndexed { i, p ->
        val k = cerca(p.center)
        val abierto = i == 0
        if (abierto) drawRect(a.luz(NOGAL_OSC, k * 0.3f), Offset(p.left, p.top - 1.5f * u), Size(p.width, 5f * u))
        drawRect(a.sombra(0.3f), Offset(p.left + 1.5f * u, p.top + 1.5f * u), p.size)
        val f = if (abierto) p.translate(0f, 2.5f * u) else p
        drawRect(a.luz(NOGAL, k), f.topLeft, f.size)
        vetas(f, a.luz(NOGAL_OSC, k).copy(alpha = 0.5f), 897 + i, densidad = 0.35f, grosor = 0.8f)
        // El tablero rehundido del frente, con su moldura.
        val cuart = f.deflate(minOf(f.width, f.height) * 0.16f)
        if (cuart.width > 2f && cuart.height > 2f) {
            drawRect(a.luz(mezcla(NOGAL, NOGAL_OSC, 0.25f), k), cuart.topLeft, cuart.size)
            vetas(cuart, a.luz(mezcla(NOGAL, CREMA, 0.2f), k).copy(alpha = 0.35f), 899 + i, densidad = 0.5f, grosor = 0.7f)
            drawRect(a.sombra(0.22f), cuart.topLeft, Size(cuart.width, 2f * u))
            drawRect(a.sombra(0.16f), cuart.topLeft, Size(2f * u, cuart.height))
            drawRect(a.claro(0.25f), Offset(cuart.left, cuart.bottom - 1.4f * u), Size(cuart.width, 1.4f * u))
            drawRect(a.claro(0.2f), Offset(cuart.right - 1.4f * u, cuart.top), Size(1.4f * u, cuart.height))
        }
        drawRect(a.claro(0.3f), f.topLeft, Size(f.width, 1f * u))
        drawRect(a.claro(0.2f), f.topLeft, Size(1f * u, f.height))
        drawRect(a.sombra(0.3f), Offset(f.right - 1.2f * u, f.top), Size(1.2f * u, f.height))
        if (a.lumbre && k > 0.05f) drawRect(a.filo(0.4f * k), f.topLeft, Size(f.width, 1f * u))
        // Los dos pomos de latón.
        listOf(0.25f, 0.75f).forEach { t ->
            val c = Offset(f.left + f.width * t, f.center.y)
            val kk = maxOf(k, 0.15f)
            drawCircle(a.sombra(0.35f), w * 0.011f, c + Offset(1.4f * u, 1.8f * u))
            drawCircle(a.luz(mezcla(LATON, NOGAL_OSC, 0.2f), kk), w * 0.011f, c)
            drawCircle(a.luz(LATON, kk), w * 0.007f, c - Offset(w * 0.002f, w * 0.002f))
            drawCircle(a.claro(0.6f), w * 0.0025f, c - Offset(w * 0.004f, w * 0.004f))
        }
        if (abierto) trapo(f, a, k)
    }
    // El escudo de la cerradura del cajón de la derecha y la llave con su borla.
    val ce = Offset(cajones[1].center.x, cajones[1].center.y - w * 0.004f)
    val kc = maxOf(cerca(ce), 0.15f)
    drawRoundRect(a.luz(LATON, kc), Offset(ce.x - w * 0.009f, ce.y - w * 0.016f), Size(w * 0.018f, w * 0.032f), CornerRadius(w * 0.006f))
    drawCircle(a.claro(0.5f), 1f * u, ce - Offset(w * 0.004f, w * 0.009f))
    drawRect(a.luz(mezcla(LATON, TINTA, 0.3f), kc), Offset(ce.x - 0.8f * u, ce.y - w * 0.006f), Size(1.6f * u, w * 0.016f))
    drawCircle(a.luz(LATON, kc), w * 0.008f, ce + Offset(0f, -w * 0.002f), style = Stroke(1.4f * u))
    val borla = Path().apply { moveTo(ce.x, ce.y + w * 0.006f); cubicTo(ce.x + w * 0.004f, ce.y + w * 0.02f, ce.x - w * 0.002f, ce.y + w * 0.03f, ce.x + w * 0.002f, ce.y + w * 0.04f) }
    drawPath(borla, a.luz(ALMAGRA, kc), style = Stroke(1f * u))
    drawPath(poligono(ce.x - w * 0.006f, ce.y + w * 0.04f, ce.x + w * 0.01f, ce.y + w * 0.04f, ce.x + w * 0.012f, ce.y + w * 0.062f, ce.x - w * 0.008f, ce.y + w * 0.062f), a.luz(ALMAGRA, kc))
    drawRect(a.luz(mezcla(ALMAGRA, OCRE, 0.4f), kc), Offset(ce.x - w * 0.006f, ce.y + w * 0.04f), Size(w * 0.016f, w * 0.005f))
    // El bolso de lona de la obra, caído contra el cajón, y los libros grandes del suelo.
    bolso(Offset(w * 0.17f, h - (h - suelo) * 0.22f), w * 0.2f, a, maxOf(cerca(Offset(w * 0.17f, suelo)), 0.12f))
    librosTumbadosUrbe(Offset(w * 0.79f, h - (h - suelo) * 0.2f), w * 0.24f, 3, a, maxOf(cerca(Offset(w * 0.85f, suelo)), 0.12f), 2)
}

/** El trapo de algodón de la obra, con manchas de pigmento, que cuelga blando por el canto del cajón entreabierto. */
private fun DrawScope.trapo(f: Rect, a: AireUrbe, k: Float) {
    val u = density
    val x0 = f.left + f.width * 0.42f; val x1 = f.left + f.width * 0.66f
    val tela = Path().apply {
        moveTo(x0, f.top - 2.5f * u)
        cubicTo(x0 + f.width * 0.08f, f.top - 4f * u, x1 - f.width * 0.06f, f.top - 3f * u, x1, f.top - 2f * u)
        cubicTo(x1 + 1.5f * u, f.top + f.height * 0.3f, x1 - f.width * 0.03f, f.top + f.height * 0.5f, x1 - f.width * 0.05f, f.top + f.height * 0.62f)
        cubicTo(x1 - f.width * 0.09f, f.top + f.height * 0.56f, x0 + f.width * 0.08f, f.top + f.height * 0.7f, x0 + f.width * 0.04f, f.top + f.height * 0.48f)
        cubicTo(x0 + f.width * 0.01f, f.top + f.height * 0.3f, x0 - 1f * u, f.top + f.height * 0.1f, x0, f.top - 2.5f * u)
        close()
    }
    drawPath(corrido(tela, 1.6f * u, 2f * u), a.sombra(0.3f))
    val c = a.luz(mezcla(CREMA, YESO, 0.4f), k)
    drawPath(tela, c)
    clipPath(tela) {
        // Los pliegues blandos y las manchas de pigmento.
        listOf(0.3f, 0.55f, 0.75f).forEachIndexed { j, t ->
            val x = x0 + (x1 - x0) * t
            drawLine(a.sombra(0.16f), Offset(x, f.top - 2f * u), Offset(x - (x1 - x0) * 0.08f * (j - 1), f.top + f.height * 0.6f), 1.2f * u)
            drawLine(a.claro(0.3f), Offset(x - 1.2f * u, f.top - 2f * u), Offset(x - 1.2f * u - (x1 - x0) * 0.08f * (j - 1), f.top + f.height * 0.5f), 0.6f * u)
        }
        drawOval(a.luz(OCRE, k).copy(alpha = 0.7f), Offset(x0 + (x1 - x0) * 0.2f, f.top + f.height * 0.1f), Size((x1 - x0) * 0.3f, f.height * 0.14f))
        drawOval(a.luz(ULTRAMAR, k).copy(alpha = 0.6f), Offset(x0 + (x1 - x0) * 0.55f, f.top + f.height * 0.32f), Size((x1 - x0) * 0.2f, f.height * 0.1f))
        drawOval(a.luz(ALMAGRA, k).copy(alpha = 0.5f), Offset(x0 + (x1 - x0) * 0.3f, f.top + f.height * 0.42f), Size((x1 - x0) * 0.16f, f.height * 0.08f))
    }
}

/** El suelo de baldosas hidráulicas: cada una con un rombo gris azulado y cuartos de círculo de teja en las esquinas, gastadas. */
private fun DrawScope.baldosas(r: Rect, a: AireUrbe, cerca: (Offset) -> Float) {
    val u = density
    val k = if (a.lumbre) maxOf(cerca(Offset(r.center.x, r.top)), 0.08f) else 1f
    val fondo = a.luz(mezcla(CREMA, YESO, 0.5f), k)
    drawRect(fondo, r.topLeft, r.size)
    val filas = listOf(0f to 0.42f, 0.42f to 1f)
    clipRect(r.left, r.top, r.right, r.bottom) {
        filas.forEachIndexed { f, (t0, t1) ->
            val y0 = r.top + r.height * t0; val y1 = r.top + r.height * t1
            val lado = maxOf(4f * u, r.width * (0.11f + 0.03f * f))
            var x = r.left - lado * (0.3f + 0.4f * f)
            var j = 0
            while (x < r.right) {
                val c = Offset(x + lado / 2, (y0 + y1) / 2)
                val rx = lado * 0.32f; val ry = (y1 - y0) * 0.32f
                drawPath(poligono(c.x, c.y - ry, c.x + rx, c.y, c.x, c.y + ry, c.x - rx, c.y), a.luz(mezcla(AZUCAR, TINTA, 0.2f), k).copy(alpha = 0.65f))
                listOf(Offset(x, y0), Offset(x + lado, y0), Offset(x, y1), Offset(x + lado, y1)).forEach { e ->
                    drawOval(a.luz(mezcla(TEJA, CREMA, 0.3f), k).copy(alpha = 0.55f), Offset(e.x - lado * 0.2f, e.y - (y1 - y0) * 0.22f), Size(lado * 0.4f, (y1 - y0) * 0.44f))
                }
                drawLine(a.sombra(0.25f), Offset(x, y0), Offset(x, y1), 0.7f * u)
                x += lado; j++
            }
            drawLine(a.sombra(0.25f), Offset(r.left, y1), Offset(r.right, y1), 0.7f * u)
        }
        // El gastado de las pisadas y la cera vieja.
        pinceladas(r, a.claro(1f), 895, 16, u, 0.14f)
        pinceladas(r, a.sombra(1f), 896, 10, u, 0.08f)
    }
}

/**
 * El bolso de lona de la obra, caído: la lona blanda que se dobla, la boca abierta con un rollo de papel y un pincel ancho que
 * asoman, el asa que cae por delante y el bolsillo con una mancha de cal.
 */
private fun DrawScope.bolso(pie: Offset, an: Float, a: AireUrbe, cerca: Float) {
    val u = density
    val al = an * 0.6f
    val lona = a.luz(TELA, cerca)
    val lonaOsc = a.luz(mezcla(TELA, NOGAL_OSC, 0.35f), cerca)
    // Lo que asoma de la boca: un rollo de papel y la brocha.
    drawLine(a.luz(CREMA, cerca), Offset(pie.x - an * 0.12f, pie.y - al * 0.8f), Offset(pie.x - an * 0.3f, pie.y - al * 1.25f), an * 0.09f, StrokeCap.Round)
    drawCircle(a.luz(mezcla(CREMA, OCRE, 0.2f), cerca), an * 0.045f, Offset(pie.x - an * 0.3f, pie.y - al * 1.25f))
    drawLine(a.luz(NOGAL, cerca), Offset(pie.x + an * 0.12f, pie.y - al * 0.7f), Offset(pie.x + an * 0.2f, pie.y - al * 1.18f), an * 0.035f, StrokeCap.Round)
    drawRect(a.luz(mezcla(TINTA, NOGAL, 0.4f), cerca), Offset(pie.x + an * 0.16f, pie.y - al * 1.3f), Size(an * 0.1f, al * 0.12f))
    contacto(Offset(pie.x + an * 0.08f, pie.y), an * 1.15f, al * 0.14f, a, 0.42f)
    // La lona: ancha abajo, la boca hundida y el lado derecho que se dobla hacia dentro.
    val cuerpo = Path().apply {
        moveTo(pie.x - an * 0.46f, pie.y)
        cubicTo(pie.x - an * 0.52f, pie.y - al * 0.4f, pie.x - an * 0.44f, pie.y - al * 0.78f, pie.x - an * 0.36f, pie.y - al * 0.86f)
        cubicTo(pie.x - an * 0.15f, pie.y - al * 0.74f, pie.x + an * 0.1f, pie.y - al * 0.78f, pie.x + an * 0.3f, pie.y - al * 0.92f)
        cubicTo(pie.x + an * 0.36f, pie.y - al * 0.7f, pie.x + an * 0.5f, pie.y - al * 0.35f, pie.x + an * 0.48f, pie.y)
        close()
    }
    drawPath(cuerpo, lona)
    clipPath(cuerpo) {
        // El pliegue grande y la sombra del lado contrario a la luz.
        val pliegue = Path().apply { moveTo(pie.x + an * 0.06f, pie.y - al * 0.8f); cubicTo(pie.x + an * 0.12f, pie.y - al * 0.5f, pie.x + an * 0.02f, pie.y - al * 0.3f, pie.x + an * 0.1f, pie.y) }
        drawPath(pliegue, lonaOsc.copy(alpha = 0.6f), style = Stroke(2.4f * u))
        drawPath(corrido(pliegue, -1.6f * u, 0f), a.claro(0.25f), style = Stroke(1f * u))
        drawRect(a.sombra(0.22f), Offset(pie.x + an * 0.22f, pie.y - al), Size(an * 0.4f, al))
        drawRect(a.claro(0.2f), Offset(pie.x - an * 0.46f, pie.y - al), Size(an * 0.12f, al))
        // La trama de la lona.
        pinceladas(Rect(pie.x - an / 2, pie.y - al, pie.x + an / 2, pie.y), lonaOsc, 897, 10, u, 0.12f)
        // El bolsillo, con su costura y una mancha de cal.
        val bol = Rect(pie.x - an * 0.3f, pie.y - al * 0.5f, pie.x + an * 0.02f, pie.y - al * 0.12f)
        drawRect(mezcla(lona, lonaOsc, 0.25f), bol.topLeft, bol.size)
        drawRect(lonaOsc, bol.topLeft, Size(bol.width, 1.2f * u))
        drawOval(a.luz(GESSO, cerca).copy(alpha = 0.6f), Offset(bol.left + bol.width * 0.5f, bol.top + bol.height * 0.4f), Size(bol.width * 0.35f, bol.height * 0.3f))
        // La boca: el borde hundido, en sombra.
        drawPath(Path().apply { moveTo(pie.x - an * 0.36f, pie.y - al * 0.86f); cubicTo(pie.x - an * 0.15f, pie.y - al * 0.74f, pie.x + an * 0.1f, pie.y - al * 0.78f, pie.x + an * 0.3f, pie.y - al * 0.92f) }, lonaOsc, style = Stroke(2.6f * u))
    }
    // El asa, que cae por delante en curva floja.
    val asa = Path().apply { moveTo(pie.x - an * 0.3f, pie.y - al * 0.82f); cubicTo(pie.x - an * 0.4f, pie.y - al * 0.3f, pie.x + an * 0.05f, pie.y - al * 0.15f, pie.x + an * 0.22f, pie.y - al * 0.88f) }
    drawPath(corrido(asa, 1.5f * u, 2f * u), a.sombra(0.25f), style = Stroke(2.6f * u))
    drawPath(asa, lonaOsc, style = Stroke(2.6f * u, cap = StrokeCap.Round))
    drawPath(corrido(asa, -0.6f * u, -0.6f * u), a.claro(0.2f), style = Stroke(0.7f * u))
}
