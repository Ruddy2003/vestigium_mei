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
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.AZUL
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.AZUL_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.AZUL_REPASO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CALIZA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CALIZA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CINABRIO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CINABRIO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ESTUCO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ESTUCO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.TINTA
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

// El testero, la bóveda y el piso del cuarto de guardar (MuebleEstanteMeso): el azul maya con su banda de cinabrio, la
// bóveda falsa con sus lajas en escalones y la viga de amarre, el nicho de la lumbre y el piso de lajas de caliza.

/** Dónde se cierra la bóveda: la tapa, en anchos desde arriba, y el medio ancho que queda abierto arriba. */
private const val TAPA_Y_MESO = 0.032f
private const val TAPA_X_MESO = 0.33f
/** Cuántas hiladas de lajas tiene cada lado de la bóveda. */
private const val HILADAS_MESO = 7

/**
 * Pinta [zona] (unos rectángulos) de noche: el color hondo (lo que no alcanza ninguna lumbre) y, encima, el charco de cada
 * lumbre: un degradado radial de su color encendido que se desvanece hasta su alcance, pintado solo donde llega.
 */
internal fun DrawScope.zonaNocheMeso(m: MesaMeso, zona: List<Rect>, col: Color) {
    val hondo = m.a.c(col, 0f)
    zona.forEach { r -> drawRect(hondo, r.topLeft, r.size) }
    listOf(true, false).forEach { esNicho ->
        val centro = if (esNicho) m.nicho else m.fogon
        val radio = m.w * alcanceLumbreMeso(esNicho, m.a.lunar)
        val caja = Rect(centro.x - radio, centro.y - radio, centro.x + radio, centro.y + radio)
        val tocadas = zona.map { it.intersect(caja) }.filter { it.width > 0f && it.height > 0f }
        if (tocadas.isNotEmpty()) {
            val brocha = charcoMeso(m, col, centro, radio, esNicho)
            tocadas.forEach { x -> drawRect(brocha, x.topLeft, x.size) }
        }
    }
}

/** El charco de una lumbre sobre [col]: su color encendido con la transparencia de lo que llega a cada distancia. */
internal fun charcoMeso(m: MesaMeso, col: Color, centro: Offset, radio: Float, esNicho: Boolean): Brush {
    val lleno = m.a.c(col, 1f)
    val pasos = floatArrayOf(0f, 0.05f, 0.12f, 0.22f, 0.35f, 0.5f, 0.7f, 1f)
    val colores = pasos.map { q ->
        val d = radio * q / m.w
        val k = if (esNicho) nichoLuzMeso(d, 0f, m.a.lunar) else fogonLuzMeso(d, 0f, m.a.lunar)
        lleno.copy(alpha = k)
    }
    return Brush.radialGradient(*pasos.indices.map { pasos[it] to colores[it] }.toTypedArray(), center = centro, radius = radio)
}

/** Un paño de [col] en [zona]: de día, liso; en los Sueños claros, con el atardecer; de noche, con los charcos de las lumbres. */
internal fun DrawScope.panoMeso(m: MesaMeso, zona: List<Rect>, col: Color) {
    if (m.a.oscuro) zonaNocheMeso(m, zona, col)
    else { val c = m.a.c(col, 0f); zona.forEach { r -> drawRect(c, r.topLeft, r.size) } }
}

/** El testero con la bóveda: hasta el arranque, en escalones de lajas que se cierran hacia la tapa. */
internal fun testeroFormaMeso(w: Float, alto: Float): Path = Path().apply {
    val a = w * MuebleEstanteMeso.ARRANQUE
    val yt = w * TAPA_Y_MESO
    val xi = w * TAPA_X_MESO
    val n = HILADAS_MESO
    fun y(j: Int) = a - (a - yt) * j / n
    fun x(j: Int) = xi * j / n
    moveTo(-1f, alto + 1f)
    lineTo(-1f, a)
    for (j in 1..n) { lineTo(x(j - 1), y(j)); lineTo(x(j), y(j)) }
    lineTo(w - xi, yt)
    for (j in n downTo 1) { lineTo(w - x(j - 1), y(j)); lineTo(w - x(j - 1), y(j - 1)) }
    lineTo(w + 1f, a)
    lineTo(w + 1f, alto + 1f)
    close()
}

/**
 * El testero: el azul maya gastado (dos azules de repaso, pinceladas, grietas, desconchones con el estuco y la caliza), la
 * banda de cinabrio del arranque entre su filete crema y su filete negro, la mancha de la puerta (el sol recortado, con el
 * borde ondulado de la cortina; en los Sueños claros, el ocaso) y, de noche, la luna chica arriba a la izquierda.
 */
internal fun DrawScope.testeroMeso(m: MesaMeso) {
    val u = density
    val w = m.w; val a = m.a; val ch = m.ch
    val todo = Rect(0f, 0f, w, ch)
    val forma = testeroFormaMeso(w, ch)
    val y0 = w * MuebleEstanteMeso.ARRANQUE
    val banda = Rect(0f, y0 - w * 0.002f, w, y0 + w * 0.03f)
    // El azul va también bajo la bóveda (que lo tapa): así no asoma el fondo en la junta de los escalones.
    panoMeso(m, listOf(todo), AZUL)
    clipPath(forma) {
        // El repaso: dos parches de otro azul, con el borde de la brocha.
        listOf(Offset(0.3f, 0.16f), Offset(0.62f, 0.38f), Offset(0.9f, 0.13f)).forEachIndexed { i, o ->
            val c = Offset(w * o.x, w * o.y)
            val p = contornoMeso(Rect(c.x - w * 0.1f, c.y - w * 0.04f, c.x + w * 0.1f, c.y + w * 0.04f), 610 + i, 0.25f)
            drawPath(p, a.c(if (i == 1) AZUL_REPASO else mezcla(AZUL, ESTUCO, 0.12f), m.k(c), m.f(c)).copy(alpha = 0.55f))
        }
        pinceladas(todo, if (a.oscuro) MesoTinta.NOCHE else mezcla(AZUL, MesoTinta.AZUL_HONDO, 0.5f), 611, 46, u, if (a.oscuro) 0.08f else 0.13f)
        if (!a.oscuro) pinceladas(todo, mezcla(AZUL_LUZ, ESTUCO, 0.3f), 612, 30, u, 0.12f)
        // La banda de cinabrio del arranque, con sus filetes y el borde de la brocha.
        panoMeso(m, listOf(banda), CINABRIO)
        pinceladas(banda, if (a.oscuro) MesoTinta.NOCHE else CINABRIO_OSC, 613, 14, u, 0.16f)
        drawRect(a.c(ESTUCO, m.k(Offset(w * 0.5f, banda.top))), Offset(0f, banda.top - w * 0.005f), Size(w, w * 0.005f))
        drawRect(a.c(TINTA, 0.3f).copy(alpha = 0.85f), Offset(0f, banda.bottom), Size(w, w * 0.0032f))
        drawRect(a.s(0.2f), Offset(0f, banda.bottom + w * 0.0032f), Size(w, w * 0.006f))
        // La mancha de la puerta: el muro repintado con la luz, dentro de su forma.
        if (!a.lumbre || a.ocaso) clipPath(m.puerta) { solMeso(m, todo, banda) }
        // Las grietas del estuco: la raja con su sombra y su borde de luz.
        listOf(Offset(0.47f, 0.06f), Offset(0.97f, 0.27f), Offset(0.2f, 0.29f)).forEachIndexed { i, fr ->
            val p0 = Offset(w * fr.x, w * fr.y)
            val g = Path().apply {
                moveTo(p0.x, p0.y)
                var q = p0
                for (j in 0 until 5) { q += Offset(w * (0.006f + 0.01f * azar(620 + i, j)) * (if (j % 2 == 0) 1f else -0.6f), w * (0.01f + 0.01f * azar(630 + i, j))); lineTo(q.x, q.y) }
            }
            drawPath(g, a.s(0.34f), style = Stroke(0.9f * u))
            translate(0.9f * u, 0.9f * u) { drawPath(g, a.b(0.3f, m.k(p0)), style = Stroke(0.5f * u)) }
        }
        desconchonMeso(m, Rect(w * 0.585f, w * 0.115f, w * 0.655f, w * 0.15f), 640)
        desconchonMeso(m, Rect(w * 0.9f, w * 0.31f, w * 0.97f, w * 0.355f), 641)
        desconchonMeso(m, Rect(w * 0.3f, y0 + w * 0.004f, w * 0.36f, y0 + w * 0.04f), 642)
        // De noche, la luna chica que entra por la puerta (en los Sueños, grande) y el halo de la lumbre del nicho.
        if (a.oscuro) {
            val c = Offset(w * (MuebleEstanteMeso.LUNA_X + if (a.lunar) 0.06f else 0f), w * (MuebleEstanteMeso.LUNA_Y + if (a.lunar) 0.16f else 0f))
            mancha(c, w * (if (a.lunar) 0.62f else 0.3f), MesoTinta.LUNA.copy(alpha = if (a.lunar) 0.26f else 0.15f))
            // El halo de la lumbre del nicho, que tiñe el azul de naranja (en los Sueños, más chico).
            val q = if (a.lunar) 0.55f else 1f
            mancha(m.nicho, w * 0.9f * q, MesoTinta.BRASA.copy(alpha = 0.2f * q))
            mancha(m.nicho, w * 0.42f * q, Color(0xFFFF7A3A).copy(alpha = 0.26f * q))
            mancha(m.nicho, w * 0.14f * q, MesoTinta.LLAMA_VIVA.copy(alpha = 0.3f * q))
        }
    }
}

/** El muro de [todo] con la luz de la puerta (se llama dentro de su recorte): el azul al sol, la banda encendida, sus filetes. */
private fun DrawScope.solMeso(m: MesaMeso, todo: Rect, banda: Rect) {
    val u = density
    val w = m.w; val a = m.a
    val luz = if (a.ocaso) mezcla(MesoTinta.OCASO, AZUL_LUZ, 0.45f) else mezcla(AZUL_LUZ, MesoTinta.SOL, 0.3f)
    drawRect(luz.copy(alpha = if (a.ocaso) 0.6f else 0.9f), todo.topLeft, todo.size)
    pinceladas(todo, mezcla(luz, ESTUCO, 0.5f), 614, 30, u, 0.16f)
    pinceladas(todo, mezcla(luz, AZUL, 0.6f), 615, 22, u, 0.12f)
    drawRect(mezcla(CINABRIO, MesoTinta.ACHIOTE, 0.35f).copy(alpha = if (a.ocaso) 0.6f else 0.95f), banda.topLeft, banda.size)
    pinceladas(banda, CINABRIO, 616, 10, u, 0.2f)
    drawRect(ESTUCO, Offset(0f, banda.top - w * 0.005f), Size(w, w * 0.005f))
    drawRect(TINTA.copy(alpha = 0.8f), Offset(0f, banda.bottom), Size(w, w * 0.0032f))
}

/**
 * La bóveda falsa: las hiladas de lajas de cada lado, en escalones que se cierran hacia la tapa, vistas desde abajo (las caras
 * que miran al medio, más claras; las de abajo, en sombra; de día, el lado derecho recibe la luz de la puerta), la tapa de
 * lajas con sus juntas, el filete de estuco que sigue los escalones, la sombra de cada laja en el testero y la viga de amarre.
 */
internal fun DrawScope.bovedaMeso(m: MesaMeso) {
    val u = density
    val w = m.w; val a = m.a
    val y0 = w * MuebleEstanteMeso.ARRANQUE
    val yt = w * TAPA_Y_MESO
    val xi = w * TAPA_X_MESO
    val n = HILADAS_MESO
    fun y(j: Int) = y0 - (y0 - yt) * j / n
    fun x(j: Int) = xi * j / n
    // La tapa de lajas, arriba, a todo lo ancho: honda, con sus juntas.
    val tapaR = Rect(-1f, -1f, w + 1f, yt + 1f)
    drawRect(a.c(mezcla(CALIZA_OSC, CALIZA, 0.35f), m.k(Offset(w * 0.5f, 0f)) * 0.7f), tapaR.topLeft, tapaR.size)
    val juntasTapa = Path()
    for (j in 0 until 7) { val x = w * (0.06f + 0.15f * j + 0.04f * azar(j, 655)); juntasTapa.moveTo(x, 0f); juntasTapa.lineTo(x, yt) }
    drawPath(juntasTapa, a.s(0.35f), style = Stroke(0.9f * u))
    drawRect(a.s(0.35f), Offset(0f, yt - w * 0.006f), Size(w, w * 0.006f))
    // Las dos alas: cada hilada de lajas sale un poco más que la de abajo (la bóveda falsa); se ve su cara, con sus juntas y su
    // desgaste, y debajo, la sombra de la hilada que sale. El ala de la derecha mira a la puerta y de día recibe su luz.
    for (lado in 0..1) {
        for (j in 1..n) {
            val alto = y(j - 1) - y(j)
            val hasta = x(j - 1)
            val cara = if (lado == 0) Rect(-1f, y(j), hasta, y(j - 1)) else Rect(w - hasta, y(j), w + 1f, y(j - 1))
            // La fila de la junta con la hilada de arriba (que se pinta después) puede caer a medio píxel: esta sube un píxel.
            val bajo = Rect(cara.left, cara.top - 1f, cara.right, cara.bottom)
            if (cara.width < 1f) continue
            val pc = cara.center
            val tono = mezcla(mezcla(CALIZA, ESTUCO, 0.45f), CALIZA_OSC, if (lado == 0) 0.38f else 0.12f) .let { mezcla(it, CALIZA_OSC, 0.12f * azar(j + lado * 9, 656)) }
            drawRect(a.c(tono, m.k(pc), m.f(pc) * 0.6f), bajo.topLeft, bajo.size)
            // Las lajas de la hilada, cada una con su tono, su junta y una esquina comida.
            val juntas = Path(); val claras = Path(); val oscuras = Path()
            var xj = cara.left - w * 0.03f * azar(j + lado * 7, 657); var q = 0
            while (xj < cara.right && q < 24) {
                val largo = w * (0.04f + 0.045f * azar(j * 5 + q + lado * 3, 658))
                val bloque = Rect(xj, cara.top, xj + largo, cara.bottom)
                val t = azar(j * 7 + q + lado * 13, 659)
                if (t > 0.62f) claras.addRect(bloque) else if (t < 0.3f) oscuras.addRect(bloque)
                juntas.moveTo(bloque.right, cara.top); juntas.lineTo(bloque.right + w * 0.001f, cara.bottom)
                if (t > 0.5f) juntas.addPath(poligonoMeso(bloque.right - alto * 0.3f, cara.bottom, bloque.right, cara.bottom - alto * 0.25f, bloque.right, cara.bottom))
                xj += largo; q++
            }
            drawPath(claras, a.b(0.12f, m.k(pc)))
            drawPath(oscuras, a.s(0.1f))
            drawPath(juntas, a.s(0.32f), style = Stroke(0.9f * u))
            // Arriba, la sombra de la hilada que sale encima; abajo, el canto de la laja con su luz.
            drawRect(a.s(if (lado == 0) 0.36f else 0.26f), cara.topLeft, Size(cara.width, alto * 0.26f))
            drawRect(a.b(0.35f, m.k(pc)), Offset(cara.left, cara.bottom - 1.2f * u), Size(cara.width, 1.2f * u))
            pinceladas(cara, if (a.oscuro) MesoTinta.NOCHE else CALIZA_OSC, 659 + j + lado * 11, 3, u, 0.2f)
        }
    }
    // El filete de estuco que sigue los escalones y, debajo, la sombra de cada laja en el testero.
    val perfil = Path()
    perfil.moveTo(0f, y0)
    for (j in 1..n) { perfil.lineTo(x(j - 1), y(j)); perfil.lineTo(x(j), y(j)) }
    perfil.lineTo(w - xi, yt)
    for (j in n downTo 1) { perfil.lineTo(w - x(j - 1), y(j)); perfil.lineTo(w - x(j - 1), y(j - 1)) }
    translate(w * 0.004f, w * 0.006f) { drawPath(perfil, a.s(0.3f), style = Stroke(w * 0.008f)) }
    drawPath(perfil, a.c(ESTUCO, m.k(Offset(w * 0.5f, yt)), 0.5f), style = Stroke(w * 0.005f))
    drawPath(perfil, a.c(CINABRIO, m.k(Offset(w * 0.5f, yt))).copy(alpha = 0.6f), style = Stroke(0.8f * u))
    vigaMeso(m)
}

/**
 * La viga de amarre de chicozapote que cruza la bóveda de lado a lado a la altura del arranque: el rollizo descortezado,
 * labrado a hacha (las facetas a lo largo), con su plano de luz arriba, su sombra abajo y su sombra en el testero.
 */
private fun DrawScope.vigaMeso(m: MesaMeso) {
    val u = density
    val w = m.w; val a = m.a
    val cy = w * VIGA_Y_MESO
    val r = w * 0.019f
    val viga = Rect(-2f, cy - r, w + 2f, cy + r)
    // Su sombra en el testero: de día, abajo y a la derecha (honda dentro del sol); de noche, lejos de la lumbre del nicho.
    val sombra = Path().apply { addRect(viga) }
    clipPath(testeroFormaMeso(w, m.ch)) { sombraMuroMeso(m, sombra, Offset(w * 0.3f, cy), 0.26f) }
    // El rollizo, con la luz que le llega a lo largo.
    val n = 6
    for (i in 0 until n) {
        val x0 = w * i / n; val x1 = w * (i + 1) / n
        val p = Offset((x0 + x1) / 2, cy)
        drawRect(a.c(MesoTinta.ZAPOTE, m.k(p), m.f(Offset(p.x, cy + w * 0.03f)) * 0.6f), Offset(x0 - 1f, viga.top), Size(x1 - x0 + 2f, viga.height))
    }
    clipRect(viga.left, viga.top, viga.right, viga.bottom) {
        drawRect(a.b(0.3f, m.k(Offset(w * 0.8f, cy))), Offset(0f, viga.top), Size(w, r * 0.45f))
        drawRect(a.b(0.4f, m.k(Offset(w * 0.8f, cy))), Offset(0f, viga.top + r * 0.18f), Size(w, r * 0.12f))
        drawRect(a.s(0.3f), Offset(0f, cy + r * 0.25f), Size(w, r * 0.75f))
        drawRect(a.s(0.25f), Offset(0f, cy + r * 0.7f), Size(w, r * 0.3f))
        // Las facetas del hacha: tajos cortos a lo largo, con su borde de luz.
        val tajos = Path()
        for (j in 0 until 26) {
            val x = w * azar(j, 651); val yy = cy - r * 0.6f + r * 1.2f * azar(j, 652)
            val l = w * (0.02f + 0.03f * azar(j, 653))
            tajos.moveTo(x, yy); tajos.quadraticTo(x + l * 0.5f, yy - r * 0.18f, x + l, yy + r * 0.05f)
        }
        drawPath(tajos, a.s(0.28f), style = Stroke(0.7f * u))
        translate(0f, -0.8f * u) { drawPath(tajos, a.b(0.22f, m.k(Offset(w * 0.8f, cy))), style = Stroke(0.5f * u)) }
        // Las grietas del secado, a lo largo.
        drawLine(a.s(0.4f), Offset(w * 0.12f, cy - r * 0.1f), Offset(w * 0.34f, cy - r * 0.02f), 0.8f * u)
        drawLine(a.s(0.4f), Offset(w * 0.58f, cy + r * 0.05f), Offset(w * 0.86f, cy - r * 0.08f), 0.8f * u)
    }
}

/** Dónde cruza la viga de amarre, en anchos desde arriba. */
internal const val VIGA_Y_MESO = 0.218f

/** Un desconchón del estuco en [r]: el estuco crema que asoma bajo el azul y, en el medio, la caliza; sus cantos. */
private fun DrawScope.desconchonMeso(m: MesaMeso, r: Rect, semilla: Int) {
    val u = density
    val a = m.a
    val k = m.k(r.center); val f = m.f(r.center)
    // Alrededor, el azul que se levantó en escamas (más claro); dentro, el estuco crema gastado y, en el medio, la caliza.
    val aureola = contornoMeso(r.inflate(minOf(r.width, r.height) * 0.3f), semilla + 2, 0.25f)
    drawPath(aureola, a.c(mezcla(MesoTinta.AZUL, ESTUCO, 0.3f), k, f).copy(alpha = 0.45f))
    val hoyo = contornoMeso(r, semilla, 0.3f)
    val piedra = contornoMeso(r.deflate(minOf(r.width, r.height) * 0.24f), semilla + 1, 0.35f)
    drawPath(hoyo, a.c(mezcla(ESTUCO_OSC, CALIZA, 0.45f), k, f))
    drawPath(piedra, a.c(mezcla(CALIZA, CALIZA_OSC, 0.45f), k, f))
    clipPath(piedra) {
        for (j in 0 until 4) drawCircle(a.c(CALIZA_OSC, k, f), minOf(r.width, r.height) * (0.04f + 0.04f * azar(semilla, j)),
            Offset(r.left + r.width * azar(semilla, j + 10), r.top + r.height * azar(semilla, j + 20)))
    }
    drawPath(bordeMeso(hoyo, -1.2f * u, -1.4f * u), a.s(0.38f))
    drawPath(bordeMeso(hoyo, 0.9f * u, 1f * u), a.b(0.3f, k))
}

/** Un contorno irregular dentro de [r], de dieciséis puntos. */
internal fun contornoMeso(r: Rect, semilla: Int, irregular: Float): Path = Path().apply {
    for (i in 0 until 16) {
        val ang = i / 16f * 6.2832f
        val rr = 1f - irregular + irregular * 2f * azar(semilla, i)
        val x = r.center.x + r.width / 2 * rr * kotlin.math.cos(ang)
        val y = r.center.y + r.height / 2 * rr * kotlin.math.sin(ang)
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

/** El nicho del testero, en anchos: su hueco (sin el marco). */
internal fun nichoRectMeso(w: Float) = Rect(w * 0.752f, w * 0.318f, w * 0.858f, w * 0.44f)

/**
 * El nicho del testero, donde de noche arde la lumbre de ocote: el hueco con su arco de dos escalones (la misma bóveda, en
 * chico), el marco de estuco con su filete rojo, el alféizar que sale, la hondura (los costados de dentro, el fondo, el piso)
 * y, en el piso, el tiesto con las rajas: de día, apagadas y tiznadas; arriba, el hollín que dejó la lumbre en el azul.
 */
internal fun DrawScope.nichoMeso(m: MesaMeso) {
    val u = density
    val w = m.w; val a = m.a
    val r = nichoRectMeso(w)
    val hueco = huecoNichoMeso(r)
    val k = m.k(Offset(r.center.x, r.bottom))
    // El hollín: sube del hueco y se abre en el azul (de noche lo dora la lumbre).
    val tizne = if (a.oscuro) mezcla(TINTA, MesoTinta.BRASA, 0.25f) else TINTA
    for (j in 0 until 4) {
        val ancho = r.width * (1.25f - 0.22f * j)
        val alto = w * (0.13f - 0.022f * j)
        drawOval(tizne.copy(alpha = if (a.oscuro) 0.09f else 0.07f), Offset(r.center.x - ancho / 2 + w * 0.006f * j, r.top - alto + w * 0.025f), Size(ancho, alto))
    }
    // El marco de estuco, con su sombra en el muro y su filete rojo.
    val marco = huecoNichoMeso(Rect(r.left - w * 0.011f, r.top - w * 0.011f, r.right + w * 0.011f, r.bottom + w * 0.004f))
    sombraMuroMeso(m, marco, r.center, 0.22f)
    drawPath(marco, a.c(ESTUCO, k, m.f(r.center)))
    drawPath(bordeMeso(marco, -1.6f * u, -1.6f * u), a.b(0.4f, k))
    drawPath(bordeMeso(marco, 1.6f * u, 1.6f * u), a.s(0.28f))
    // El hueco: hondo, con el fondo, los costados y el piso; de noche lo pinta la lumbre ([lumbreNichoMeso]).
    drawPath(hueco, a.c(mezcla(MesoTinta.BARRO_OSC, TINTA, 0.55f), k * 0.5f))
    clipPath(hueco) {
        val fondo = Rect(r.left + r.width * 0.16f, r.top + r.height * 0.12f, r.right - r.width * 0.12f, r.bottom - r.height * 0.14f)
        drawRect(a.c(mezcla(MesoTinta.BARRO_OSC, TINTA, 0.35f), k * 0.6f), fondo.topLeft, fondo.size)
        drawPath(poligonoMeso(r.left, r.top, fondo.left, fondo.top, fondo.left, fondo.bottom, r.left, r.bottom), a.c(mezcla(MesoTinta.BARRO_OSC, TINTA, 0.65f), k * 0.4f))
        drawPath(poligonoMeso(r.right, r.top, fondo.right, fondo.top, fondo.right, fondo.bottom, r.right, r.bottom), a.c(mezcla(ESTUCO_OSC, MesoTinta.BARRO_OSC, 0.6f), k * 0.6f))
        drawPath(poligonoMeso(r.left, r.bottom, fondo.left, fondo.bottom, fondo.right, fondo.bottom, r.right, r.bottom), a.c(mezcla(ESTUCO_OSC, MesoTinta.BARRO_OSC, 0.45f), k * 0.7f))
        // El hollín de dentro, arriba.
        drawRect(TINTA.copy(alpha = 0.4f), Offset(r.left, r.top), Size(r.width, r.height * 0.3f))
    }
    tiestoMeso(m, Offset(r.center.x, r.bottom - r.height * 0.09f), r.width * 0.62f)
    // El alféizar de estuco que sale, con su luz arriba y su sombra debajo.
    val alf = Rect(r.left - w * 0.016f, r.bottom, r.right + w * 0.016f, r.bottom + w * 0.011f)
    drawRect(a.s(0.3f), Offset(alf.left + w * 0.006f, alf.bottom), Size(alf.width, w * 0.008f))
    drawRect(a.c(ESTUCO, k, m.f(alf.center)), alf.topLeft, alf.size)
    drawRect(a.b(0.5f, k), alf.topLeft, Size(alf.width, 1.2f * u))
    drawRect(a.s(0.25f), Offset(alf.left, alf.bottom - 1.4f * u), Size(alf.width, 1.4f * u))
    drawLine(a.c(CINABRIO, k).copy(alpha = 0.7f), Offset(alf.left + 2f * u, alf.top + alf.height * 0.55f), Offset(alf.right - 2f * u, alf.top + alf.height * 0.55f), 0.8f * u)
}

/** El hueco del nicho con su arco de dos escalones (la bóveda en chico). */
private fun huecoNichoMeso(r: Rect): Path {
    val e = r.width * 0.16f; val v = r.height * 0.1f
    return poligonoMeso(r.left, r.bottom, r.left, r.top + 2 * v, r.left + e, r.top + 2 * v, r.left + e, r.top + v, r.left + 2 * e, r.top + v,
        r.left + 2 * e, r.top, r.right - 2 * e, r.top, r.right - 2 * e, r.top + v, r.right - e, r.top + v, r.right - e, r.top + 2 * v, r.right, r.top + 2 * v, r.right, r.bottom)
}

/** El tiesto de barro del nicho con sus rajas de ocote: de día, apagadas, con las puntas tiznadas y un poco de ceniza. */
private fun DrawScope.tiestoMeso(m: MesaMeso, pie: Offset, ancho: Float) {
    val u = density
    val a = m.a
    val k = m.k(pie)
    // Las rajas, cruzadas, que asoman del tiesto.
    val rajas = listOf(-0.32f to -0.4f, -0.1f to -0.62f, 0.12f to -0.55f, 0.3f to -0.36f)
    rajas.forEachIndexed { i, (fx, fy) ->
        val p0 = Offset(pie.x + ancho * fx * 0.4f, pie.y - ancho * 0.12f)
        val p1 = Offset(pie.x + ancho * fx, pie.y + ancho * fy * 0.55f)
        drawLine(a.c(MesoTinta.OCOTE, k), p0, p1, ancho * 0.07f, StrokeCap.Butt)
        drawLine(a.c(TINTA, 0.3f), p1, p1 + (p0 - p1) * 0.22f, ancho * 0.075f, StrokeCap.Butt)
        if (i % 2 == 0) drawLine(a.b(0.3f, k), p0 - Offset(ancho * 0.02f, 0f), p1 - Offset(ancho * 0.02f, 0f), 0.6f * u)
    }
    val cuenco = Path().apply {
        moveTo(pie.x - ancho * 0.5f, pie.y - ancho * 0.18f)
        cubicTo(pie.x - ancho * 0.46f, pie.y + ancho * 0.02f, pie.x + ancho * 0.46f, pie.y + ancho * 0.02f, pie.x + ancho * 0.5f, pie.y - ancho * 0.18f)
        close()
    }
    drawPath(cuenco, a.c(MesoTinta.BARRO, k))
    drawPath(bordeMeso(cuenco, -ancho * 0.08f, -ancho * 0.03f), a.s(0.35f))
    drawLine(a.c(CINABRIO_OSC, k), Offset(pie.x - ancho * 0.5f, pie.y - ancho * 0.18f), Offset(pie.x + ancho * 0.5f, pie.y - ancho * 0.18f), 1.4f * u)
    if (!a.oscuro) drawOval(MesoTinta.CENIZA.copy(alpha = 0.7f), Offset(pie.x - ancho * 0.3f, pie.y - ancho * 0.22f), Size(ancho * 0.5f, ancho * 0.07f))
}

/**
 * El pie del testero detrás de la base (el zócalo de cinabrio) y el piso de lajas de caliza que viene hacia quien mira: las
 * hiladas cada vez más altas, las juntas que corren hacia el punto de fuga, cada laja con su tono, su desgaste y su canto; la
 * sombra del anaquel en el piso y, de noche, el charco del fogón; en los Sueños de noche, la luna que entra por la puerta.
 */
internal fun DrawScope.pisoMeso(m: MesaMeso, fondo: Float) {
    val u = density
    val w = m.w; val a = m.a; val h = m.alto
    val piso = m.piso
    // El zócalo del testero, entre las zapatas.
    val zocalo = Rect(0f, fondo - 1f, w, piso + 1f)
    panoMeso(m, listOf(zocalo), CINABRIO_OSC)
    drawRect(a.s(0.35f), zocalo.topLeft, Size(w, (piso - fondo) * 0.5f))
    // Las lajas.
    val suelo = Rect(0f, piso, w, h)
    val laja = mezcla(CALIZA, CALIZA_OSC, 0.18f)
    panoMeso(m, listOf(suelo), laja)
    val fuga = Offset(w * 0.5f, piso - w * 0.9f)
    val filas = 5
    fun yFila(i: Int): Float { val t = i / filas.toFloat(); return piso + (h - piso) * (t * (0.55f + 0.45f * t)) }
    val juntas = Path()
    for (i in 0 until filas) {
        val ya = yFila(i); val yb = yFila(i + 1)
        juntas.moveTo(0f, yb); juntas.lineTo(w, yb)
        val paso = w * (0.2f + 0.08f * i)
        var x = -paso * azar(i, 671)
        var j = 0
        while (x < w + paso && j < 20) {
            // Cada laja: su tono propio y su junta con la siguiente, que corre hacia la fuga.
            val xa = x; val xb = x + paso * (0.75f + 0.5f * azar(i * 13 + j, 672))
            fun enY(x0: Float, yy: Float) = fuga.x + (x0 - fuga.x) * (yy - fuga.y) / (ya - fuga.y)
            val p = poligonoMeso(xa, ya, xb, ya, enY(xb, yb), yb, enY(xa, yb), yb)
            val c = Offset((xa + xb) / 2, (ya + yb) / 2)
            val tono = mezcla(laja, if (azar(i * 7 + j, 673) > 0.5f) CALIZA_OSC else ESTUCO, 0.1f + 0.2f * azar(i * 5 + j, 674))
            drawPath(p, a.c(tono, m.k(c), m.f(c)).copy(alpha = 0.7f))
            juntas.moveTo(xb, ya); juntas.lineTo(enY(xb, yb), yb)
            // El canto gastado de la laja, a la luz.
            drawLine(a.b(0.18f, m.k(c)), Offset(xa + 2f * u, ya + 1.2f * u), Offset(xb - 2f * u, ya + 1.2f * u), 0.9f * u)
            x = xb; j++
        }
    }
    drawPath(juntas, a.s(0.4f), style = Stroke(1.1f * u))
    pinceladas(suelo, if (a.oscuro) MesoTinta.NOCHE else CALIZA_OSC, 675, 22, u, 0.12f)
    // La sombra del anaquel en el piso, junto al testero, y la del pie del muro.
    drawRect(a.s(0.42f), Offset(0f, piso), Size(w, 2f * u))
    drawRect(a.s(0.2f), Offset(0f, piso + 2f * u), Size(w, w * 0.03f))
    if (!a.oscuro) drawRect(a.s(0.08f), Offset(0f, piso), Size(w, (h - piso) * 0.5f))
    // La luna de los Sueños, que entra por la puerta y cae en el piso.
    if (a.lunar) mancha(Offset(w * 0.3f, h - w * 0.05f), w * 0.5f, MesoTinta.LUNA.copy(alpha = 0.2f))
    if (a.ocaso) mancha(Offset(w * 0.15f, h), w * 0.6f, MesoTinta.OCASO.copy(alpha = 0.2f))
}
