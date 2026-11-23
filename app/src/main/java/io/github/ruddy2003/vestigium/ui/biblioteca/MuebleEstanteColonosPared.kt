package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CAL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CAL_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CORDEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CRUDO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.HIERRO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LADRILLO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LATA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LATA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LUNA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.MAIZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.MARINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.OXIDO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PINO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PONIENTE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.SALVIA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.SOL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.VERDE
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/*
 * La pared de la cocina de Colonos y lo que cuelga de ella: la ventana de botellas (un hueco en arco en el adobe, tapado con
 * fondos de botella asentados en mortero de cal; de día es la luz del cuarto) y la luz que echa sobre la alacena; la sartén, el
 * colador, el manojo de hierbas y el cable de la antena; y la charola apoyada contra la cal.
 */

/** Los vidrios de las botellas: verde, ámbar, azul, claro, pardo y verde agua. */
private val VIDRIOS_COLONOS = listOf(Color(0xFF3F9150), Color(0xFFD08A22), Color(0xFF3A6FB0), Color(0xFFCFE6DC), Color(0xFF8E5426), Color(0xFF5FA89A))

/** La luz que trae la ventana según el momento: el sol, el poniente, la luna (Sueños de noche) o nada (la noche del diario). */
private fun luzVentana(a: AireColonos): Color? = when {
    !a.oscuro && !a.noche -> SOL
    !a.oscuro -> PONIENTE
    a.lunar -> LUNA
    else -> null
}

/** El hueco en arco de medio punto: lados rectos y el arco arriba. */
private fun arcoColonos(r: Rect): Path = Path().apply {
    val rad = r.width / 2
    moveTo(r.left, r.bottom); lineTo(r.left, r.top + rad)
    arcTo(Rect(r.left, r.top, r.right, r.top + 2 * rad), 180f, 180f, false)
    lineTo(r.right, r.bottom); close()
}

/**
 * La ventana de botellas en [r]: el hueco en arco con su derrame encalado (el intradós en sombra, el alféizar con luz), y
 * dentro, en mortero de cal, los fondos de botella en hileras desparejas; de día se encienden de sol y echan su color en el
 * mortero; al anochecer, de poniente; en la noche del diario son azul de noche con algún brillo de estrella; en los Sueños de
 * noche, la luna los enfría. Abajo, el alféizar de tabla.
 */
internal fun DrawScope.ventanaBotellasColonos(g: MarcoColonos, r: Rect) {
    val u = density; val w = g.w; val a = g.a
    val luz = luzVentana(a)
    val der = w * 0.02f
    val fuera = Rect(r.left - der, r.top - der, r.right + der, r.bottom + der * 0.35f)
    val hueco = arcoColonos(r)
    val marco = arcoColonos(fuera)
    // El resplandor del hueco en la cal de alrededor (de día, de poniente o de luna).
    if (luz != null) mancha(r.center, w * 0.2f, luz.copy(alpha = if (a.oscuro) 0.16f else 0.3f))
    // El derrame: la cara del hueco, de cal más clara (le da la luz que entra), el intradós del arco en sombra.
    val k = g.k(r.center)
    drawPath(corridoColonos(marco, 1.5f * u, 2f * u), a.sombra(0.2f))
    drawPath(marco, if (a.oscuro) calEnColonos(g, r.center, CAL_LUZ) else a.luz(mezcla(CAL, CAL_LUZ, 0.55f), k))
    clipPath(marco) {
        drawRect(a.sombra(0.28f), Offset(fuera.left, fuera.top), Size(fuera.width, r.top + r.width * 0.25f - fuera.top))
        drawRect(a.sombra(0.16f), Offset(fuera.left, fuera.top), Size(der * 0.9f, fuera.height))
        if (luz != null) drawRect(luz.copy(alpha = 0.18f), Offset(fuera.left, r.bottom), Size(fuera.width, fuera.bottom - r.bottom))
    }
    drawPath(marco, a.sombra(0.2f), style = Stroke(0.8f * u))
    // El mortero de cal donde van asentadas las botellas.
    val mortero = mezcla(CRUDO, CAL, 0.35f)
    drawPath(hueco, when {
        luz != null && !a.oscuro -> mezcla(mortero, luz, 0.25f)
        a.lunar -> mezcla(a.luz(mortero, 0.15f), LUNA, 0.18f)
        else -> a.luz(mortero, 0.12f)
    })
    clipPath(hueco) {
        // Las botellas, en hileras desparejas que llenan el arco.
        val filas = 4
        for (f in 0 until filas) {
            val cols = if (f % 2 == 0) 3 else 4
            for (c in 0 until cols) {
                val fx = (c + 0.5f) / cols
                val cx = r.left + r.width * (0.06f + 0.88f * fx) + r.width * 0.02f * (azar(7130 + f, c) - 0.5f)
                val cy = r.top + r.height * (0.17f + 0.23f * f) + r.height * 0.02f * (azar(7131 + f, c) - 0.5f)
                val rr = r.width * (0.108f + 0.025f * azar(7132 + f, c))
                botellaColonos(g, Offset(cx, cy), rr, VIDRIOS_COLONOS[(f * 2 + c * 3 + f * c) % VIDRIOS_COLONOS.size], luz, (f * 7 + c) % 5 == 3)
            }
        }
        // La sombra del intradós sobre lo de arriba del hueco.
        drawRect(a.sombra(0.22f), r.topLeft, Size(r.width, r.height * 0.16f))
        drawRect(a.sombra(0.14f), r.topLeft, Size(r.width * 0.08f, r.height))
    }
    // El alféizar de tabla, con su canto y su sombra.
    val al = Rect(fuera.left - w * 0.01f, fuera.bottom - w * 0.004f, fuera.right + w * 0.012f, fuera.bottom + w * 0.013f)
    drawRect(a.sombra(0.3f), Offset(al.left + 2f * u, al.bottom), Size(al.width, 3f * u))
    drawRect(g.c(PINO, al.center), al.topLeft, al.size)
    drawRect(g.c(PINO_OSC, al.center), Offset(al.left, al.top + al.height * 0.5f), Size(al.width, al.height * 0.5f))
    drawRect(if (luz != null && !a.oscuro) luz.copy(alpha = 0.5f) else a.brillo(0.45f, g.k(al.center)), al.topLeft, Size(al.width, 1f * u))
}

/**
 * Un fondo de botella en el mortero: el canto oscuro del vidrio, el vidrio, el culo hundido del centro y, con luz detrás, el
 * centro encendido y su color derramado en el mortero; sin luz, el azul de la noche y un brillo. [estrella]: en la noche, por
 * ese vidrio claro se ve una estrella.
 */
private fun DrawScope.botellaColonos(g: MarcoColonos, c: Offset, r: Float, vidrio: Color, luz: Color?, estrella: Boolean) {
    val u = density; val a = g.a
    if (luz != null) {
        val fuerte = if (a.oscuro) 0.55f else 1f
        mancha(c, r * 1.9f, mezcla(vidrio, luz, 0.3f).copy(alpha = 0.4f * fuerte))
        drawCircle(a.sombra(0.35f), r * 1.06f, c + Offset(0.5f * u, 0.7f * u))
        drawCircle(mezcla(vidrio, a.hondo, if (a.oscuro) 0.45f else 0.25f), r, c)
        drawCircle(mezcla(vidrio, luz, 0.35f * fuerte), r * 0.8f, c)
        drawCircle(mezcla(vidrio, luz, 0.7f * fuerte), r * 0.48f, c + Offset(-r * 0.06f, -r * 0.05f))
        drawCircle(mezcla(luz, Color.White, 0.5f).copy(alpha = 0.9f * fuerte), r * 0.2f, c + Offset(-r * 0.05f, -r * 0.04f))
        drawCircle(a.sombra(0.2f), r * 0.48f, c, style = Stroke(r * 0.07f))
    } else {
        drawCircle(a.sombra(0.4f), r * 1.06f, c + Offset(0.5f * u, 0.7f * u))
        drawCircle(mezcla(vidrio, a.hondo, 0.78f), r, c)
        drawCircle(mezcla(MARINO, vidrio, 0.3f).copy(alpha = 0.8f), r * 0.7f, c)
        drawCircle(a.sombra(0.25f), r * 0.48f, c, style = Stroke(r * 0.07f))
        drawCircle(Color.White.copy(alpha = 0.22f), r * 0.14f, c + Offset(-r * 0.42f, -r * 0.4f))
        if (estrella) drawCircle(Color.White.copy(alpha = 0.85f), maxOf(0.7f * u, r * 0.08f), c + Offset(r * 0.18f, r * 0.1f))
    }
}

/**
 * La luz que la ventana echa sobre la cara de arriba de la alacena (la pared no puede recibirla: la ventana está en ella): de
 * día, un charco de sol que se alarga a la derecha con los puntos de color que dejan las botellas; al anochecer, de poniente,
 * más largo; en los Sueños de noche, de luna, frío. En la noche del diario no hay.
 */
internal fun DrawScope.luzVentanaEnTapaColonos(g: MarcoColonos, r: Rect) {
    val w = g.w; val a = g.a
    val luz = luzVentana(a) ?: return
    val largo = if (a.noche && !a.oscuro) 1.3f else 1f
    val y0 = g.apoyoAtras; val y1 = g.apoyo
    val x0 = r.left + r.width * 0.2f; val x1 = r.right + w * 0.34f * largo
    for (j in 0 until 3) {
        val e = w * 0.02f * j
        drawPath(poligonoColonos(x0 + e, y0, x1 - e * 2f, y0, x1 - e * 2f + w * 0.03f, y1, x0 + e + w * 0.03f, y1), luz.copy(alpha = if (a.lunar) 0.12f else 0.16f))
    }
    // Los puntos de color de las botellas, alargados.
    for (i in 0 until 9) {
        val c = Offset(x0 + (x1 - x0) * azar(7140, i), y0 + (y1 - y0) * (0.15f + 0.6f * azar(7141, i)))
        val col = mezcla(VIDRIOS_COLONOS[i % VIDRIOS_COLONOS.size], luz, 0.25f)
        drawOval(col.copy(alpha = if (a.lunar) 0.3f else 0.5f), Offset(c.x - w * 0.012f, c.y - w * 0.003f), Size(w * 0.024f, w * 0.006f))
    }
}

// ---------------------------------------------------------------- lo que cuelga

/**
 * La sartén de hierro colgada de un clavo por el agujero del mango: el mango hacia arriba, la cara de dentro negra con el
 * curado que brilla, el borde con su luz y su grueso, y su sombra en la cal.
 */
internal fun DrawScope.sartenColonos(g: MarcoColonos, clavo: Offset) {
    val u = density; val w = g.w; val a = g.a
    val r = w * 0.052f
    val c = Offset(clavo.x + w * 0.004f, clavo.y + w * 0.115f)
    val mango = Path().apply {
        moveTo(clavo.x - w * 0.007f, clavo.y - w * 0.006f); lineTo(clavo.x + w * 0.007f, clavo.y - w * 0.006f)
        lineTo(c.x + w * 0.009f, c.y - r + w * 0.004f); lineTo(c.x - w * 0.009f, c.y - r + w * 0.004f); close()
    }
    val todo = Path().apply { addPath(mango); addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
    sombraParedColonos(g, todo, c, g.apoyoAtras, 0.22f)
    val k = g.k(c); val br = g.br(c)
    drawPath(mango, g.c(HIERRO, clavo))
    drawLine(a.brillo(0.35f, k, br), Offset(clavo.x - w * 0.005f, clavo.y), Offset(c.x - w * 0.007f, c.y - r), 1f * u)
    drawCircle(g.c(HIERRO, c), r, c)
    drawCircle(g.c(mezcla(HIERRO, HIERRO_LUZ, 0.25f), c), r * 0.84f, c)
    drawCircle(g.c(HIERRO, c), r * 0.78f, c)
    // El curado: un brillo ancho en la cara de dentro, del lado de la luz (de día, de la ventana; de noche, de la bombilla).
    val lado = if (a.oscuro) 1f else -1f
    drawArc(a.brillo(0.3f, k, br), if (a.oscuro) -60f else 150f, 80f, false, Offset(c.x - r * 0.62f, c.y - r * 0.62f), Size(r * 1.24f, r * 1.24f), style = Stroke(r * 0.18f, cap = StrokeCap.Round))
    drawCircle(a.brillo(0.12f, k, br), r * 0.3f, c + Offset(lado * r * 0.25f, -r * 0.2f))
    drawArc(a.brillo(0.55f, k, br), if (a.oscuro) -80f else 170f, 90f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(1.2f * u))
    drawArc(a.sombra(0.5f), if (a.oscuro) 100f else -10f, 90f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(1.2f * u))
    // El agujero del mango y el clavo.
    drawCircle(a.hondo.copy(alpha = 0.8f), w * 0.004f, clavo)
    clavoColonos(clavo, 1.5f * u, g)
}

/**
 * El cable de la antena: sube de la radio a un clavo junto a la viga, con su panza, su aislador de porcelana en el clavo y su
 * sombra en la cal.
 */
internal fun DrawScope.antenaColonos(g: MarcoColonos, desde: Offset, hasta: Offset) {
    val u = density; val a = g.a
    val cable = Path().apply {
        moveTo(desde.x, desde.y)
        quadraticTo((desde.x + hasta.x) / 2 + g.w * 0.02f, (desde.y + hasta.y) / 2 + g.w * 0.03f, hasta.x, hasta.y)
        lineTo(hasta.x + g.w * 0.08f, g.w * 0.056f)
    }
    // Su sombra en la cal: de día, a la derecha y abajo; de noche, hacia fuera de la bombilla.
    if (!a.oscuro) translate(g.w * 0.012f, g.w * 0.008f) { drawPath(cable, a.sombra(0.18f), style = Stroke(1f * u)) }
    else translate(-g.w * 0.03f, g.w * 0.006f) { drawPath(cable, a.hondo.copy(alpha = 0.35f), style = Stroke(1f * u)) }
    drawPath(cable, g.c(OXIDO, hasta), style = Stroke(1.1f * u))
    // El aislador de huevo, atado al clavo.
    val ais = Rect(hasta.x - g.w * 0.007f, hasta.y - g.w * 0.005f, hasta.x + g.w * 0.007f, hasta.y + g.w * 0.005f)
    drawOval(a.sombra(0.3f), ais.topLeft + Offset(1f * u, 1.4f * u), ais.size)
    drawOval(g.c(CRUDO, hasta), ais.topLeft, ais.size)
    drawOval(a.brillo(0.6f, g.k(hasta)), Offset(ais.left + ais.width * 0.2f, ais.top + 1f), Size(ais.width * 0.35f, ais.height * 0.4f))
    clavoColonos(Offset(hasta.x + g.w * 0.012f, hasta.y - g.w * 0.002f), 1.2f * u, g)
}

/**
 * La charola de lámina pintada, apoyada de canto contra la pared detrás de la cafetera: el borde levantado con su luz, el fondo
 * verde hondo con una flor de cinco pétalos de ladrillo con su centro de maíz y sus hojas, y una corona de puntos; gastada en el
 * borde, con su sombra en la cal.
 */
internal fun DrawScope.charolaColonos(g: MarcoColonos, pie: Offset, r: Float) {
    val u = density; val a = g.a
    val c = Offset(pie.x, pie.y - r * 0.95f)
    val ov = Rect(c.x - r, c.y - r * 0.95f, c.x + r, c.y + r * 0.95f)
    sombraParedColonos(g, Path().apply { addOval(ov) }, c, g.apoyoAtras, 0.2f)
    val k = g.k(c); val br = g.br(c)
    val fondo = Color(0xFF2F4A3A)
    drawOval(a.luz(mezcla(fondo, HIERRO, 0.3f), k, br), ov.topLeft, ov.size)
    val dentro = ov.deflate(r * 0.12f)
    drawOval(a.luz(fondo, k, br), dentro.topLeft, dentro.size)
    clipPath(Path().apply { addOval(dentro) }) {
        // La corona de puntos de maíz.
        val puntos = Path()
        for (i in 0 until 18) {
            val ang = i * 0.349f
            val q = Offset(c.x + r * 0.7f * kotlin.math.cos(ang), c.y + r * 0.66f * kotlin.math.sin(ang))
            puntos.addOval(Rect(q.x - r * 0.035f, q.y - r * 0.035f, q.x + r * 0.035f, q.y + r * 0.035f))
        }
        drawPath(puntos, a.luz(MAIZ, k, br).copy(alpha = 0.8f))
        // Las hojas y la flor.
        val hojas = Path()
        for (j in 0 until 3) {
            val ang = 0.6f + j * 2.094f
            val q = Offset(c.x + r * 0.36f * kotlin.math.cos(ang), c.y + r * 0.34f * kotlin.math.sin(ang))
            hojas.addOval(Rect(q.x - r * 0.15f, q.y - r * 0.08f, q.x + r * 0.15f, q.y + r * 0.08f))
        }
        drawPath(hojas, a.luz(SALVIA, k, br))
        val flor = Path()
        for (j in 0 until 5) {
            val ang = j * 1.2566f - 1.5708f
            val q = Offset(c.x + r * 0.2f * kotlin.math.cos(ang), c.y + r * 0.19f * kotlin.math.sin(ang))
            flor.addOval(Rect(q.x - r * 0.15f, q.y - r * 0.15f, q.x + r * 0.15f, q.y + r * 0.15f))
        }
        drawPath(flor, a.luz(LADRILLO, k, br))
        drawCircle(a.luz(MAIZ, k, br), r * 0.1f, c)
        // Lo gastado, la sombra del borde levantado y el brillo de la lámina.
        drawOval(a.sombra(0.35f), Offset(dentro.left, dentro.top), Size(dentro.width, dentro.height * 0.22f))
        drawRect(a.sombra(0.2f), Offset(c.x + r * 0.3f, dentro.top), Size(r, dentro.height))
    }
    drawArc(a.brillo(0.5f, maxOf(k, if (a.oscuro) 0f else 1f), br), if (a.oscuro) -60f else 160f, 90f, false, ov.topLeft, ov.size, style = Stroke(1.2f * u))
    drawOval(a.luz(OXIDO, k, br).copy(alpha = 0.4f), ov.topLeft, ov.size, style = Stroke(1.4f * u))
}

/**
 * El manojo de hierbas secas colgado de la viga cabeza abajo: el cordel, la atadura y las ramas que se abren hacia abajo, con
 * sus hojitas de salvia y de oliva secas; su sombra en la cal.
 */
internal fun DrawScope.hierbasColonos(g: MarcoColonos, viga: Offset) {
    val u = density; val w = g.w; val a = g.a
    val nudo = Offset(viga.x, viga.y + w * 0.03f)
    val largo = w * 0.14f
    val ramas = Path(); val hojas = Path(); val secas = Path()
    for (i in 0 until 7) {
        val f = (i - 3) / 3f
        val fin = Offset(nudo.x + f * w * 0.034f + w * 0.006f * (azar(7760, i) - 0.5f), nudo.y + largo * (0.8f + 0.2f * azar(7761, i)))
        ramas.moveTo(nudo.x, nudo.y); ramas.quadraticTo(nudo.x + f * w * 0.01f, (nudo.y + fin.y) / 2, fin.x, fin.y)
        for (j in 0 until 5) {
            val t = 0.25f + 0.16f * j
            val p = Offset(nudo.x + (fin.x - nudo.x) * t, nudo.y + (fin.y - nudo.y) * t)
            val lado = if (j % 2 == 0) 1f else -1f
            val hoja = Rect(p.x + lado * w * 0.002f - w * 0.006f, p.y - w * 0.003f, p.x + lado * w * 0.002f + w * 0.006f, p.y + w * 0.003f)
            (if ((i + j) % 3 == 0) secas else hojas).addOval(hoja)
        }
    }
    val todo = Path().apply { addPath(hojas); addPath(secas) }
    sombraParedColonos(g, todo, Offset(nudo.x, nudo.y + largo * 0.5f), g.apoyoAtras, 0.14f)
    val p = Offset(nudo.x, nudo.y + largo * 0.5f)
    val k = g.k(p); val br = g.br(p)
    drawLine(g.c(CORDEL, nudo), viga, nudo, 1f * u)
    drawPath(ramas, a.luz(PINO_OSC, k, br), style = Stroke(0.8f * u))
    drawPath(hojas, a.luz(mezcla(SALVIA, VERDE, 0.3f), k, br))
    drawPath(secas, a.luz(mezcla(SALVIA, OXIDO, 0.45f), k, br))
    drawRect(g.c(CORDEL, nudo), Offset(nudo.x - w * 0.006f, nudo.y - w * 0.004f), Size(w * 0.012f, w * 0.008f))
}

/**
 * El colador de lata colgado de un clavo por su argolla: la taza con sus agujeros en hileras que siguen la curva, el borde
 * enrollado con su brillo, las dos asas chicas y su sombra en la cal.
 */
internal fun DrawScope.coladorColonos(g: MarcoColonos, clavo: Offset, r: Float) {
    val u = density; val a = g.a
    val borde = clavo.y + r * 0.7f
    val c = Offset(clavo.x, borde)
    val taza = Path().apply { moveTo(c.x - r, borde); cubicTo(c.x - r, borde + r * 1.25f, c.x + r, borde + r * 1.25f, c.x + r, borde); close() }
    val todo = Path().apply { addPath(taza); addOval(Rect(c.x - r, borde - r * 0.22f, c.x + r, borde + r * 0.22f)) }
    sombraParedColonos(g, todo, c, g.apoyoAtras, 0.18f)
    val k = g.k(c); val br = g.br(c)
    // La argolla del clavo.
    drawCircle(g.c(LATA_OSC, clavo), r * 0.22f, Offset(clavo.x, clavo.y + r * 0.22f), style = Stroke(1.2f * u))
    drawPath(taza, a.luz(LATA, k, br))
    clipPath(taza) {
        val agujeros = Path()
        for (f in 0 until 3) {
            val yy = borde + r * (0.22f + 0.22f * f)
            val ancho = r * (0.88f - 0.22f * f * f * 0.5f)
            val n = 7 - f * 2
            for (i in 0 until n) {
                val x = c.x - ancho + 2 * ancho * (i + 0.5f) / n
                agujeros.addOval(Rect(x - r * 0.045f, yy - r * 0.045f, x + r * 0.045f, yy + r * 0.045f))
            }
        }
        drawPath(agujeros, a.sombra(0.6f))
        drawRect(a.claro(0.3f), Offset(c.x - r * 0.75f, borde), Size(r * 0.3f, r))
        drawRect(a.sombra(0.25f), Offset(c.x + r * 0.3f, borde), Size(r * 0.7f, r))
    }
    drawOval(a.luz(mezcla(LATA, Color.White, 0.2f), k, br), Offset(c.x - r * 1.05f, borde - r * 0.18f), Size(r * 2.1f, r * 0.36f))
    drawOval(a.sombra(0.35f), Offset(c.x - r * 0.9f, borde - r * 0.1f), Size(r * 1.8f, r * 0.2f))
    drawArc(a.brillo(0.6f, maxOf(k, if (a.oscuro) 0f else 1f), br), 180f, 90f, false, Offset(c.x - r * 1.05f, borde - r * 0.18f), Size(r * 2.1f, r * 0.36f), style = Stroke(0.9f * u))
    listOf(-1f, 1f).forEach { s -> drawLine(a.luz(LATA_OSC, k, br), Offset(c.x + s * r * 1.02f, borde), Offset(c.x + s * r * 1.22f, borde - r * 0.08f), 1.6f * u) }
    clavoColonos(clavo, 1.3f * u, g)
}
