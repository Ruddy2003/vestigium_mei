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
 * Lo de Giulia sobre la cornisa. Lo principal, en el medio y atrás: un trozo de fresco arrancado de una bóveda, montado en su
 * bastidor y apoyado contra el muro. Delante y a los lados, a distintas profundidades: el potos en su maceta de barro, con las
 * guías que caen por el costado; la tablilla de yeso con las pruebas de color, bajo la ventana; los botes de pigmento delante
 * del fresco; el bote de los pinceles con los hisopos; el calco de un florón, roto; la lámpara de pantalla plisada; y dos libros
 * tumbados, cortados por el borde.
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
    val pFresco = Offset(w * 0.505f, apoyo)
    frescoUrbe(pFresco, w * 0.27f, w * 0.235f, a, { p -> cerca(p) }, caida(pFresco - Offset(0f, w * 0.12f), w * 0.016f))
    val pTabla = Offset(w * 0.255f, apoyo)
    pruebaColor(pTabla, w * 0.1f, a, cerca(pTabla - Offset(0f, w * 0.04f)), caida(pTabla, w * 0.008f))
    val pRoseta = Offset(w * 0.69f, apoyo)
    rosetaUrbe(pRoseta, w * 0.036f, a, cerca(pRoseta - Offset(0f, w * 0.04f)), caida(pRoseta, w * 0.008f), entera = false)
    botesPigmentoUrbe(Offset(w * 0.39f, apoyo + w * 0.002f), w * 0.03f, a, cerca(Offset(w * 0.4f, apoyo - w * 0.02f)), hacia(w * 0.4f), 0)
    botePincelesUrbe(Offset(w * 0.6f, apoyo + w * 0.002f), w * 0.042f, a, cerca(Offset(w * 0.6f, apoyo - w * 0.04f)), hacia(w * 0.6f), caida(Offset(w * 0.6f, apoyo), w * 0.008f))
    librosTumbadosUrbe(Offset(w * 0.885f, apoyo), w * 0.15f, 2, a, cerca(Offset(w * 0.93f, apoyo)), 5)
    lamparaUrbe(apoyo, a, caida(Offset(w * LAMP_X, apoyo - w * 0.06f), w * 0.012f))
    potos(Offset(w * 0.115f, apoyo), w * 0.07f, ch, a, cerca(Offset(w * 0.1f, apoyo - w * 0.03f)), hacia(w * 0.11f))
}

/**
 * El trozo de fresco arrancado (un «stacco») de la bóveda de una capilla, pasado a un soporte nuevo y montado en su bastidor de
 * abeto, apoyado contra el muro: el borde quebrado del fresco sobre el enlucido liso del soporte; el azul de la bóveda con sus
 * estrellas doradas de ocho puntas; la banda de almagra del nervio con su filete blanco; las faltas, rellenas con el rayado
 * fino de la restauración (el «rigatino»), y las grietas del craquelado. Ninguna obra en particular: una bóveda estrellada
 * cualquiera, como la del cuadro de Giulia.
 */
private fun DrawScope.frescoUrbe(pie: Offset, an: Float, al: Float, a: AireUrbe, cerca: (Offset) -> Float, caida: Offset) {
    val u = density
    val k = cerca(pie - Offset(0f, al * 0.5f))
    val kd = cerca(pie + Offset(an * 0.4f, -al * 0.5f))
    // Apoyado: arriba se va un poco hacia atrás y se estrecha.
    val ent = an * 0.025f
    val marco = poligono(pie.x - an / 2 + ent, pie.y - al, pie.x + an / 2 - ent, pie.y - al, pie.x + an / 2, pie.y, pie.x - an / 2, pie.y)
    translate(caida.x, caida.y) { drawPath(marco, a.sombra(if (a.lumbre) 0.42f else 0.26f)) }
    contacto(Offset(pie.x + an * 0.05f, pie.y), an * 1.04f, an * 0.04f, a, 0.4f)
    // El canto del bastidor, a la derecha y abajo, y su cara de abeto.
    val abeto = mezcla(PaletaUrbe.NOGAL, CREMA, 0.55f)
    drawPath(TrazoUrbe.corrido(marco, 2.2f * u, 1.2f * u), a.luz(mezcla(abeto, TINTA, 0.35f), kd))
    drawPath(marco, a.luz(abeto, k))
    val barra = an * 0.05f
    val panel = poligono(pie.x - an / 2 + ent + barra, pie.y - al + barra, pie.x + an / 2 - ent - barra, pie.y - al + barra, pie.x + an / 2 - barra, pie.y - barra, pie.x - an / 2 + barra, pie.y - barra)
    // Las vetas del abeto y los ingletes de las esquinas.
    clipPath(marco) {
        for (j in 0 until 6) drawLine(a.luz(mezcla(abeto, TINTA, 0.25f), k).copy(alpha = 0.4f), Offset(pie.x - an / 2, pie.y - al + barra * (0.2f + 0.12f * j)), Offset(pie.x + an / 2, pie.y - al + barra * (0.25f + 0.12f * j)), 0.5f * u)
        drawRect(a.claro(0.35f), Offset(pie.x - an / 2, pie.y - al), Size(an, 1f * u))
    }
    // El soporte nuevo: enlucido liso, gris cálido.
    val soporte = a.luz(mezcla(YESO, CREMA, 0.4f), k)
    drawPath(panel, soporte)
    // El fresco: un borde quebrado, que no llega a las esquinas.
    val r = Rect(pie.x - an / 2 + barra, pie.y - al + barra, pie.x + an / 2 - barra, pie.y - barra)
    val trozo = Path()
    val n = 18
    for (j in 0 until n) {
        val ang = 6.283f * j / n
        val rr = 0.8f + 0.14f * azar(910, j) - (if (j % 5 == 2) 0.14f else 0f)
        // Un rectángulo de esquinas rotas: el radio crece hacia las diagonales, sin pasar del soporte.
        val esq = 1f + 0.32f * (kotlin.math.abs(cos(ang) * sin(ang)) * 2f)
        val q = Offset(r.center.x + r.width * 0.5f * minOf(1f, rr * esq) * cos(ang) * 0.98f, r.center.y + r.height * 0.5f * minOf(1f, rr * esq) * sin(ang) * 0.98f)
        if (j == 0) trozo.moveTo(q.x, q.y) else trozo.lineTo(q.x, q.y)
    }
    trozo.close()
    drawPath(TrazoUrbe.corrido(trozo, 1f * u, 1.2f * u), a.sombra(0.3f))
    val azul = a.luz(mezcla(PaletaUrbe.ULTRAMAR, CIELO, 0.25f), k)
    drawPath(trozo, azul)
    clipPath(trozo) {
        // El azul a manchas, como se pinta al fresco: más hondo en unas partes, más gastado en otras.
        for (j in 0 until 7) drawOval(a.luz(if (j % 2 == 0) PaletaUrbe.ULTRAMAR else mezcla(CIELO, CREMA, 0.3f), k).copy(alpha = 0.22f), Offset(r.left + r.width * azar(911, j) - an * 0.1f, r.top + r.height * azar(912, j) - an * 0.06f), Size(an * 0.22f, an * 0.12f))
        // La banda de almagra del nervio, abajo, con su filete blanco y una línea de ocre.
        val yb = r.top + r.height * 0.72f
        drawRect(a.luz(ALMAGRA, k), Offset(r.left, yb), Size(r.width, r.height * 0.28f))
        drawRect(a.luz(CREMA, k), Offset(r.left, yb - 1.4f * u), Size(r.width, 1.6f * u))
        drawRect(a.luz(OCRE, k), Offset(r.left, yb + r.height * 0.12f), Size(r.width, 1.2f * u))
        // Las estrellas de ocho puntas, de oro gastado.
        val oro = a.luz(mezcla(LATON, CREMA, 0.25f), k)
        listOf(Offset(0.22f, 0.2f), Offset(0.58f, 0.14f), Offset(0.82f, 0.38f), Offset(0.4f, 0.48f), Offset(0.12f, 0.58f)).forEachIndexed { j, f ->
            val c = Offset(r.left + r.width * f.x, r.top + r.height * f.y)
            val s = an * (0.038f + 0.012f * azar(913, j))
            drawLine(oro, c - Offset(s, 0f), c + Offset(s, 0f), 1.2f * u, StrokeCap.Round)
            drawLine(oro, c - Offset(0f, s), c + Offset(0f, s), 1.2f * u, StrokeCap.Round)
            drawLine(oro, c - Offset(s * 0.65f, s * 0.65f), c + Offset(s * 0.65f, s * 0.65f), 1f * u, StrokeCap.Round)
            drawLine(oro, c - Offset(s * 0.65f, -s * 0.65f), c + Offset(s * 0.65f, -s * 0.65f), 1f * u, StrokeCap.Round)
            drawCircle(oro, s * 0.3f, c)
        }
        // Las faltas, rellenas con el rayado fino de la restauración, en azul y en ocre.
        listOf(Rect(r.left + r.width * 0.6f, r.top + r.height * 0.42f, r.left + r.width * 0.86f, r.top + r.height * 0.66f), Rect(r.left + r.width * 0.2f, r.top + r.height * 0.74f, r.left + r.width * 0.42f, r.top + r.height * 0.95f)).forEachIndexed { j, f ->
            val falta = Path().apply {
                moveTo(f.left, f.top + f.height * 0.3f); lineTo(f.left + f.width * 0.4f, f.top); lineTo(f.right, f.top + f.height * 0.2f)
                lineTo(f.right - f.width * 0.1f, f.bottom); lineTo(f.left + f.width * 0.2f, f.bottom - f.height * 0.1f); close()
            }
            drawPath(falta, a.luz(mezcla(CREMA, YESO, 0.5f), k))
            clipPath(falta) {
                var x = f.left
                var i = 0
                while (x < f.right) {
                    drawLine(a.luz(if (j == 0) mezcla(PaletaUrbe.ULTRAMAR, CREMA, 0.35f) else mezcla(ALMAGRA, CREMA, 0.3f), k).copy(alpha = 0.8f), Offset(x, f.top), Offset(x, f.bottom), 0.6f * u)
                    x += maxOf(1f, 1.5f * u); i++
                }
            }
            drawPath(falta, a.sombra(0.25f), style = Stroke(0.6f * u))
        }
        // El craquelado.
        for (j in 0 until 5) {
            val p0 = Offset(r.left + r.width * azar(914, j), r.top + r.height * azar(915, j) * 0.7f)
            val grieta = Path().apply { moveTo(p0.x, p0.y); lineTo(p0.x + an * 0.04f, p0.y + an * 0.02f); lineTo(p0.x + an * 0.05f, p0.y + an * 0.06f); lineTo(p0.x + an * 0.09f, p0.y + an * 0.07f) }
            drawPath(grieta, a.sombra(0.3f), style = Stroke(0.5f * u))
        }
        // La luz de día por la izquierda; el lado contrario, en sombra.
        drawRect(a.claro(0.12f), Offset(r.left, r.top), Size(r.width * 0.3f, r.height))
        drawRect(a.sombra(0.14f), Offset(r.right - r.width * 0.25f, r.top), Size(r.width * 0.25f, r.height))
    }
    // El borde del fresco: un poco más alto que el soporte, que coge la luz.
    drawPath(trozo, a.claro(0.4f), style = Stroke(0.8f * u))
    // Las cuñas de las esquinas del bastidor, por detrás, y la etiqueta de inventario de la obra (una tira sin letras).
    drawRect(a.luz(CREMA, k), Offset(pie.x + an * 0.18f, pie.y - barra * 0.75f), Size(an * 0.18f, barra * 0.5f))
    drawLine(a.luz(TINTA, k).copy(alpha = 0.5f), Offset(pie.x + an * 0.2f, pie.y - barra * 0.5f), Offset(pie.x + an * 0.33f, pie.y - barra * 0.5f), 0.6f * u)
    if (a.lumbre) drawPath(TrazoUrbe.borde(marco, -2f * u, 0f), a.filo(0.45f * kd))
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
