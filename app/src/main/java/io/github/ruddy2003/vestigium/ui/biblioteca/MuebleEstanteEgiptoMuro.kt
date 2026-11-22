package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ADOBE
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.AZUL
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.CARBON
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ENLUCIDO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ENLUCIDO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ENLUCIDO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.LLAMA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.OCRE
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PAJA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PALMA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PALMA_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ROJO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.SOL
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.TIERRA
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

// El testero, la bóveda y el suelo del almacén de la Tumba (MuebleEstanteEgipto).

/**
 * El color del muro (de [col]) en cada punto según la lámpara, como un degradado radial desde la llama: de noche el enlucido
 * se enciende y se tiñe cerca de ella y se hunde lejos, en su mismo tono.
 */
/** Pinta [zona] (unos rectángulos) de noche: lisa con el color de lejos y, donde llega la lámpara, el degradado de [radialNocheEgipto]. */
internal fun DrawScope.zonaNocheEgipto(m: MesaEgipto, zona: List<Rect>, col: Color, radio: Float) {
    val lejos = m.a.c(col, m.k(m.llama + Offset(radio, 0f)))
    val alcance = radio
    val brocha = radialNocheEgipto(m, col, radio)
    zona.forEach { r ->
        val y0 = maxOf(r.top, m.llama.y - alcance)
        if (y0 > r.top) drawRect(lejos, r.topLeft, Size(r.width, minOf(y0, r.bottom) - r.top))
        if (y0 < r.bottom) drawRect(brocha, Offset(r.left, y0), Size(r.width, r.bottom - y0))
    }
}

internal fun radialNocheEgipto(m: MesaEgipto, col: Color, radio: Float): Brush {
    val pasos = floatArrayOf(0f, 0.06f, 0.14f, 0.26f, 0.42f, 0.62f, 0.85f, 1f)
    val colores = pasos.map { p -> m.a.c(col, m.k(m.llama + Offset(radio * p, 0f))) }
    return Brush.radialGradient(*pasos.indices.map { pasos[it] to colores[it] }.toTypedArray(), center = m.llama, radius = radio)
}

/**
 * El testero: enlucido de barro dorado, con las pasadas de la mano, la paja, grietas y dos desconchones donde asoma el adobe.
 * Encima, la mancha de la puerta: de día el sol, casi blanco y recortado en diagonal por el dintel y la jamba; en los Sueños
 * claros, el ocaso, más largo y bajo, con el resto del muro en sombra lila; de noche, la luna en el mismo sitio, fría y con su
 * color, mientras la lámpara del suelo enciende el enlucido de abajo arriba.
 */
internal fun DrawScope.muroEgipto(m: MesaEgipto, fondo: Float) {
    val u = density
    val w = m.w; val a = m.a
    val alto = m.suelo
    // Solo donde se ve: la corona, las orillas de fuera de los troncos y la franja de abajo (los tramos y los tablones lo tapan).
    val zona = listOf(Rect(0f, 0f, w, m.ch), Rect(0f, m.ch, m.posteI.right, fondo), Rect(m.posteD.left, m.ch, w, fondo), Rect(0f, fondo, w, alto))
    if (!a.lumbre) {
        val brocha = Brush.horizontalGradient(listOf(ENLUCIDO_LUZ, ENLUCIDO, mezcla(ENLUCIDO, ENLUCIDO_OSC, 0.6f)), 0f, w)
        zona.forEach { r -> drawRect(brocha, r.topLeft, r.size) }
    } else if (!a.oscuro) {
        val brocha = Brush.horizontalGradient(listOf(a.c(ENLUCIDO_LUZ, 0f), a.c(ENLUCIDO, 0f), a.c(ENLUCIDO_OSC, 0f)), 0f, w)
        zona.forEach { r -> drawRect(brocha, r.topLeft, r.size) }
        mancha(m.llama, w * 0.34f, LLAMA.copy(alpha = 0.3f))
    } else {
        zonaNocheEgipto(m, zona, ENLUCIDO, w * 0.95f)
    }
    val kc = if (a.oscuro) 0.25f else 1f
    pinceladas(Rect(0f, 0f, w, m.ch), if (a.oscuro) EgiptoTinta.NOCHE else ENLUCIDO_OSC, 611, 16, u, if (a.oscuro) 0.06f else 0.12f)
    if (!a.oscuro) pinceladas(Rect(0f, 0f, w, m.ch), ENLUCIDO_LUZ, 613, 8, u, 0.16f)
    // Las pasadas de la mano al alisar el barro: arcos anchos, con su borde de sombra y su borde de luz.
    val pasadas = Path()
    for (j in 0 until 18) {
        val c = Offset(w * azar(j, 501), m.ch * azar(j, 502))
        val l = (24f + 30f * azar(j, 503)) * u
        pasadas.moveTo(c.x - l, c.y + l * 0.1f); pasadas.quadraticTo(c.x, c.y - l * (0.2f + 0.1f * azar(j, 504)), c.x + l, c.y + l * 0.06f)
    }
    drawPath(pasadas, a.s(0.07f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
    translate(0f, 1.6f * u) { drawPath(pasadas, a.b(0.12f, 0.3f), style = Stroke(1.2f * u, cap = StrokeCap.Round)) }
    // La paja del barro, en un solo camino.
    val paja = Path()
    for (j in 0 until 80) {
        val q = Offset(w * azar(j, 511), m.ch * azar(j, 512))
        val l = (2.5f + 4f * azar(j, 513)) * u; val g = (azar(j, 514) - 0.5f) * 1.4f
        paja.moveTo(q.x, q.y); paja.lineTo(q.x + l, q.y + l * g)
    }
    drawPath(paja, a.c(mezcla(PAJA, ADOBE, 0.35f), kc).copy(alpha = 0.55f), style = Stroke(0.7f * u))
    // La mancha de la puerta.
    when {
        !a.lumbre -> {
            drawPath(m.puerta, SOL.copy(alpha = 0.84f))
        }
        a.ocaso -> drawPath(m.puerta, mezcla(EgiptoTinta.OCASO, ENLUCIDO_LUZ, 0.45f).copy(alpha = 0.72f))
        else -> drawPath(m.puerta, a.c(ENLUCIDO_LUZ, 0f, 1f).copy(alpha = 0.95f))
    }
    // Las grietas del enlucido: la raja con su sombra y su borde de luz.
    listOf(Offset(0.87f, 0.18f), Offset(0.33f, 0.3f), Offset(0.62f, 0.86f)).forEachIndexed { i, fr ->
        val p0 = Offset(w * fr.x, m.ch * fr.y)
        val g = Path().apply {
            moveTo(p0.x, p0.y)
            var q = p0
            for (j in 0 until 5) { q += Offset(w * (0.007f + 0.012f * azar(520 + i, j)) * (if (j % 2 == 0) 1f else -0.5f), w * (0.012f + 0.012f * azar(530 + i, j))); lineTo(q.x, q.y) }
        }
        drawPath(g, a.s(0.32f), style = Stroke(0.9f * u))
        translate(0.9f * u, 0.9f * u) { drawPath(g, a.b(0.3f, m.k(p0)), style = Stroke(0.5f * u)) }
    }
    // Los desconchones, con el adobe a la vista.
    desconchonEgipto(m, Rect(w * 0.7f, m.ch * 0.1f, w * 0.79f, m.ch * 0.22f), 540)
    desconchonEgipto(m, Rect(w * 0.035f, m.ch * 0.58f, w * 0.09f, m.ch * 0.68f), 541)
}

/** Un desconchón en [r]: el hueco irregular, las hiladas de adobe con su paja, la sombra del borde de arriba y el canto del enlucido. */
private fun DrawScope.desconchonEgipto(m: MesaEgipto, r: Rect, semilla: Int) {
    val u = density
    val a = m.a
    val k = m.k(r.center); val f = m.f(r.center)
    val hoyo = contornoEgipto(r, semilla, 0.16f)
    val aureola = contornoEgipto(Rect(r.left - r.width * 0.18f, r.top - r.height * 0.2f, r.right + r.width * 0.2f, r.bottom + r.height * 0.22f), semilla + 1, 0.12f)
    drawPath(aureola, a.c(ENLUCIDO_OSC, k, f).copy(alpha = 0.4f))
    drawPath(hoyo, a.c(mezcla(ADOBE, ENLUCIDO_OSC, 0.25f), k, f))
    clipPath(hoyo) {
        val hilada = m.w * 0.02f
        var y = r.top; var n = 0
        val juntas = Path()
        while (y < r.bottom) {
            juntas.moveTo(r.left, y); juntas.lineTo(r.right, y)
            var x = r.left - (if (n % 2 == 0) 0f else hilada)
            while (x < r.right) { juntas.moveTo(x, y); juntas.lineTo(x, y + hilada); x += hilada * 2.2f }
            y += hilada; n++
        }
        drawPath(juntas, a.s(0.3f), style = Stroke(1f * u))
        val pajas = Path()
        for (j in 0 until 10) {
            val p = Offset(r.left + r.width * azar(semilla, j), r.top + r.height * azar(semilla, j + 20))
            pajas.moveTo(p.x, p.y); pajas.lineTo(p.x + r.width * 0.12f, p.y + (azar(semilla, j + 40) - 0.5f) * r.height * 0.15f)
        }
        drawPath(pajas, a.c(PAJA, k, f).copy(alpha = 0.7f), style = Stroke(0.7f * u))
        translate(2.4f * u, 2.8f * u) { drawPath(hoyo, a.s(0.42f), style = Stroke(4f * u)) }
    }
    clipRect(r.center.x - r.width * 0.1f, r.center.y, r.right + 4f * u, r.bottom + 4f * u) {
        translate(0.8f * u, 0.8f * u) { drawPath(hoyo, a.b(0.6f, k), style = Stroke(1.2f * u)) }
    }
}

/** Un contorno irregular dentro de [r], de dieciséis puntos. */
private fun contornoEgipto(r: Rect, semilla: Int, irregular: Float): Path = Path().apply {
    for (i in 0 until 16) {
        val ang = i / 16f * 6.2832f
        val rr = 1f - irregular + irregular * 2f * azar(semilla, i)
        val x = r.center.x + r.width / 2 * rr * kotlin.math.cos(ang)
        val y = r.center.y + r.height / 2 * rr * kotlin.math.sin(ang)
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

/**
 * La bóveda de cañón vista desde dentro: sobre el testero, el arco de ladrillos inclinados en dos roscas (sus juntas en
 * abanico, la luz rasante en el canto de abajo), la franja pintada de la casa que sigue el arco (azul, negro, rojo, negro) y,
 * fuera del arco, el intradós que se viene hacia acá, en sombra violeta tostada.
 */
internal fun DrawScope.bovedaEgipto(m: MesaEgipto) {
    val u = density
    val w = m.w; val a = m.a; val ch = m.ch
    val arranque = ch * 0.8f
    val clave = ch * 0.07f
    val rosca = w * 0.046f
    fun yc(y0: Float, ap: Float) = 2 * ap - y0
    fun arco(dentro: Float) = Path().apply {
        val y0 = arranque + dentro * 0.25f
        moveTo(-w * 0.02f, y0); quadraticTo(w / 2, yc(y0, clave + dentro), w * 1.02f, y0)
    }
    val kA = m.kAlto(Offset(w * 0.5f, clave)); val fA = if (a.oscuro) 0.25f else 0f
    // El intradós, fuera de las roscas.
    val fuera = Path().apply {
        moveTo(-1f, -1f); lineTo(w + 1f, -1f)
        val y0 = arranque - rosca * 2 * 0.25f
        lineTo(w + 1f, y0); quadraticTo(w / 2, yc(y0, clave - rosca * 2), -1f, y0); close()
    }
    drawPath(fuera, mezcla(a.c(mezcla(ENLUCIDO_OSC, ENLUCIDO, 0.25f), kA, fA), if (a.oscuro) EgiptoTinta.NOCHE_SOMBRA else Color(0xFF2A2436), if (a.oscuro) 0.25f else 0.2f))
    for (j in 1..4) drawPath(arco(-rosca * 2 - j * w * 0.04f), a.s(0.14f), style = Stroke(1.2f * u))
    // Las dos roscas de ladrillo.
    val banda = Path().apply {
        moveTo(-w * 0.02f, arranque); quadraticTo(w / 2, yc(arranque, clave), w * 1.02f, arranque)
        val y0 = arranque - rosca * 2 * 0.25f
        lineTo(w * 1.02f, y0); quadraticTo(w / 2, yc(y0, clave - rosca * 2), -w * 0.02f, y0); close()
    }
    drawPath(banda, a.c(mezcla(ADOBE, ENLUCIDO_OSC, 0.3f), kA, if (a.oscuro) 0.5f else 0f))
    val juntas = Path()
    for (anillo in 0..1) {
        val n = 44 + anillo * 6
        val dentro0 = -rosca * anillo; val dentro1 = -rosca * (anillo + 1)
        for (i in 0..n) {
            val t = (i + (if (anillo == 1) 0.5f else 0f)) / n
            fun punto(dd: Float): Offset {
                val y0 = arranque + dd * 0.25f
                val ycc = yc(y0, clave + dd)
                val x = 2 * (1 - t) * t * (w / 2) + t * t * w * 1.02f - w * 0.02f * (1 - t) * (1 - t)
                val y = (1 - t) * (1 - t) * y0 + 2 * (1 - t) * t * ycc + t * t * y0
                return Offset(x, y)
            }
            val p0 = punto(dentro0 - 0.6f * u); val p1 = punto(dentro1 + 0.6f * u)
            juntas.moveTo(p0.x, p0.y); juntas.lineTo(p1.x, p1.y)
        }
    }
    drawPath(juntas, a.s(0.32f), style = Stroke(0.9f * u))
    drawPath(arco(-rosca), a.s(0.35f), style = Stroke(1.1f * u))
    drawPath(arco(-rosca * 2), a.s(0.45f), style = Stroke(1.3f * u))
    drawPath(arco(0f), a.s(0.5f), style = Stroke(1.6f * u))
    drawPath(arco(-1.6f * u), a.b(0.3f, kA), style = Stroke(1.1f * u))
    // La franja pintada que sigue el arco por dentro, sobre el enlucido del testero.
    val g = w * 0.008f
    drawPath(arco(g * 1.2f), a.c(AZUL, kA, 0.3f), style = Stroke(g * 0.8f))
    drawPath(arco(g * 2.1f), a.c(CARBON, kA), style = Stroke(0.9f * u))
    drawPath(arco(g * 3.4f), a.c(ROJO, kA, 0.3f), style = Stroke(g * 1.5f))
    drawPath(arco(g * 4.6f), a.c(CARBON, kA), style = Stroke(0.9f * u))
    drawPath(arco(g * 2.8f), a.b(0.22f, kA), style = Stroke(0.6f * u))
}

/**
 * El pie del muro y el suelo: el zócalo ocre de la casa con su franja (azul, negro, rojo, negro), el rincón en sombra, el suelo
 * de tierra apisonada con sus piedrecillas y la estera de junco con su orla teñida en damero. De noche, todo según la lámpara.
 */
internal fun DrawScope.sueloEgipto(m: MesaEgipto, b: Rect) {
    val u = density
    val w = m.w; val a = m.a
    val h = size.height
    val suelo = m.suelo
    // El zócalo ocre.
    val dado = suelo - w * 0.1f
    if (a.oscuro) drawRect(radialNocheEgipto(m, OCRE, w * 1.7f), Offset(0f, dado), Size(w, suelo - dado))
    else drawRect(a.c(OCRE, 1f), Offset(0f, dado), Size(w, suelo - dado))
    if (!a.lumbre) drawRect(sombraMueble(0.12f), Offset(w * 0.62f, dado), Size(w * 0.38f, suelo - dado))
    pinceladas(Rect(0f, dado, w, suelo), if (a.oscuro) EgiptoTinta.NOCHE else mezcla(OCRE, ADOBE, 0.4f), 651, 12, u, 0.16f)
    val kz = m.k(Offset(w * 0.5f, dado))
    drawRect(a.c(AZUL, kz), Offset(0f, dado - w * 0.007f), Size(w, w * 0.004f))
    drawRect(a.c(CARBON, kz), Offset(0f, dado - w * 0.003f), Size(w, 0.9f * u))
    drawRect(a.c(ROJO, kz), Offset(0f, dado), Size(w, w * 0.012f))
    drawRect(a.b(0.25f, kz), Offset(0f, dado), Size(w, 0.7f * u))
    drawRect(a.c(CARBON, kz), Offset(0f, dado + w * 0.012f), Size(w, 0.9f * u))
    drawRect(a.c(mezcla(OCRE, ADOBE, 0.5f), kz), Offset(0f, suelo - w * 0.016f), Size(w, w * 0.016f))
    // El suelo de tierra.
    if (h - suelo < 1f) return
    val piso = Rect(0f, suelo, w, h)
    if (a.oscuro) drawRect(radialNocheEgipto(m, TIERRA, w * 1.3f), piso.topLeft, piso.size)
    else drawRect(a.c(TIERRA, 1f), piso.topLeft, piso.size)
    if (!a.lumbre) drawRect(sombraMueble(0.1f), Offset(w * 0.62f, piso.top), Size(w * 0.38f, piso.height))
    pinceladas(piso, if (a.oscuro) EgiptoTinta.NOCHE else ADOBE, 661, 20, u, 0.16f)
    drawRect(a.s(0.32f), Offset(0f, suelo), Size(w, w * 0.012f))
    drawRect(a.s(0.15f), Offset(0f, suelo + w * 0.012f), Size(w, w * 0.02f))
    for (j in 0 until 9) {
        val p = Offset(w * azar(671, j), suelo + (h - suelo) * (0.25f + 0.7f * azar(672, j)))
        val r = w * (0.002f + 0.003f * azar(673, j))
        drawOval(a.s(0.3f), Offset(p.x - r * 0.5f, p.y), Size(r * 2.4f, r))
        drawCircle(a.c(EgiptoTinta.PIEDRA, m.k(p)), r, p)
    }
    esteraEgipto(m, h)
}

/** La estera de junco en el suelo, vista un poco desde arriba: los juncos a lo ancho, la orla teñida en damero rojo y negro y los cabos sueltos. */
private fun DrawScope.esteraEgipto(m: MesaEgipto, h: Float) {
    val u = density
    val w = m.w; val a = m.a
    val y0 = m.suelo + (h - m.suelo) * 0.2f; val y1 = h - (h - m.suelo) * 0.05f
    if (y1 - y0 < 4f * u) return
    val x0 = w * 0.24f; val x1 = w * 0.86f
    val c = Offset((x0 + x1) / 2, (y0 + y1) / 2)
    val k = m.k(c)
    val forma = poligonoEgipto(x0 + w * 0.03f, y0, x1 - w * 0.01f, y0, x1 + w * 0.03f, y1, x0 - w * 0.02f, y1)
    drawPath(forma, a.s(0.32f))
    translate(0f, -w * 0.004f) {
        drawPath(forma, a.c(mezcla(PALMA_LUZ, PAJA, 0.4f), k))
        clipPath(forma) {
            // Los juncos, a lo ancho: una tira de luz y una de sombra.
            var y = y0; var n = 0
            val sombra = Path(); val luz = Path()
            while (y < y1) { (if (n % 2 == 0) sombra else luz).apply { moveTo(x0 - w * 0.05f, y); lineTo(x1 + w * 0.05f, y) }; y += 2.6f * u; n++ }
            drawPath(sombra, a.s(0.18f), style = Stroke(1f * u))
            drawPath(luz, a.b(0.22f, k), style = Stroke(1f * u))
            // La orla teñida, en damero, cerca de cada borde.
            listOf(y0 + (y1 - y0) * 0.1f, y1 - (y1 - y0) * 0.22f).forEach { yb ->
                val alto = (y1 - y0) * 0.1f
                var x = x0 - w * 0.03f; var j = 0
                while (x < x1 + w * 0.04f) {
                    drawRect(a.c(if (j % 2 == 0) ROJO else CARBON, k), Offset(x, yb + (if (j % 2 == 0) 0f else alto / 2)), Size(w * 0.016f, alto / 2))
                    x += w * 0.016f; j++
                }
            }
            // Las ataduras de fibra a lo largo.
            listOf(0.18f, 0.5f, 0.82f).forEach { fx ->
                val x = x0 + (x1 - x0) * fx
                drawLine(a.c(mezcla(ADOBE, ROJO, 0.25f), k), Offset(x, y0), Offset(x + w * 0.012f, y1), 1.6f * u)
                drawLine(a.b(0.3f, k), Offset(x - 1f * u, y0), Offset(x - 1f * u + w * 0.012f, y1), 0.6f * u)
            }
        }
        // Los cabos sueltos del borde de la derecha.
        val cabos = Path()
        for (j in 0 until 8) {
            val yy = y0 + (y1 - y0) * (j + 0.5f) / 8f
            val x = x1 - w * 0.01f + w * 0.04f * (yy - y0) / (y1 - y0)
            cabos.moveTo(x, yy); cabos.lineTo(x + w * (0.008f + 0.01f * azar(681, j)), yy + (azar(682, j) - 0.5f) * 4f * u)
        }
        drawPath(cabos, a.c(PALMA, k), style = Stroke(0.8f * u))
    }
    // La sombra del anaquel y del pie de los postes, encima de la estera.
    drawRect(a.s(0.1f), Offset(x0, y0 - w * 0.004f), Size(x1 - x0, w * 0.01f))
}
