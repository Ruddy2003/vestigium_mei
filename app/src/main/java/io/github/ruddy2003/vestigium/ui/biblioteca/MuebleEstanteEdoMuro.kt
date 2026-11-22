package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import kotlin.math.roundToInt
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.HINOKI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.HINOKI_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.HOLLIN
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KAMI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KURI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.SUGI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.SUGI_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.VETA
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.WASHI
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

// La pared y el estante de la casa de té de la posta (MuebleEstanteEdo): las tablas de sugi, el nageshi, el ranma, el pilar,
// el suelo, y el estante de hinoki con sus tablas pasantes y su fondo de papel viejo.

// ---------------------------------------------------------------- la pared

/**
 * La pared de tablas de sugi puestas de pie, ahumadas por el fogón (más oscuras arriba, con el hollín que sube), con sus juntas,
 * su veta y algún nudo; el nageshi de lado a lado; arriba a la izquierda, el ranma de papel; a la derecha, el pilar cortado
 * por el borde. Donde no llega la luz, la madera se va a una penumbra violeta.
 */
internal fun DrawScope.paredEdo(a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w
    val lejos = if (a.lunar) 0f else 0.1f
    val madera = a.luz(SUGI, lejos, 0.1f)
    drawRect(if (a.oscuro) madera else mezcla(madera, SOMBRA_MUEBLE, if (a.noche) 0.16f else 0.1f), Offset.Zero, Size(w, g.suelo))
    // Las tablas, de pie, de ancho desigual: la junta (una raya honda con su labio de luz) y un tono un poco distinto cada una.
    var x = -w * 0.03f; var j = 0
    while (x < w) {
        val an = w * (0.1f + 0.045f * azar(301, j))
        if (j % 2 == 1) drawRect(a.luz(mezcla(SUGI, SUGI_OSC, 0.3f), lejos, 0.1f).copy(alpha = 0.35f), Offset(x, 0f), Size(an, g.suelo))
        if (azar(302, j) > 0.55f) drawRect(a.luz(mezcla(SUGI, KAMI, 0.3f), lejos, 0.1f).copy(alpha = 0.25f), Offset(x, 0f), Size(an, g.suelo))
        // El relieve de cada tabla: el canto de la izquierda recibe la luz; el de la derecha se hunde junto a la junta.
        drawRect(a.claro(0.14f), Offset(x + 1.3f * u, 0f), Size(2.2f * u, g.suelo))
        drawRect(a.sombra(0.12f), Offset(x + an - 3.4f * u, 0f), Size(3.4f * u, g.suelo))
        x += an; j++
        drawLine(a.sombra(if (a.oscuro) 0.6f else 0.42f), Offset(x, 0f), Offset(x, g.suelo), 1.3f * u)
        drawLine(a.claro(0.22f), Offset(x + 1.3f * u, 0f), Offset(x + 1.3f * u, g.suelo), 0.7f * u)
    }
    // La veta, solo donde la pared se ve: arriba, a los lados del estante y bajo él.
    val zonas = listOf(Rect(0f, 0f, w, g.ch), Rect(0f, g.ch, g.posteIzq, g.fondo), Rect(g.posteDer, g.ch, w, g.fondo), Rect(0f, g.fondo, w, g.suelo))
    zonas.forEachIndexed { i, z ->
        if (z.width < 1f || z.height < 1f) return@forEachIndexed
        vetas(z, a.luz(SUGI_OSC, lejos, 0.1f).copy(alpha = if (a.oscuro) 0.5f else 0.42f), 310 + i, densidad = 0.55f, vertical = true, grosor = 0.8f, ondulacion = 0.7f)
        pinceladas(z, a.luz(mezcla(SUGI, KAMI, 0.4f), lejos), 320 + i, (14 * (z.width * z.height / (w * w * 0.3f)).coerceIn(0.3f, 4f)).toInt(), u, 0.12f)
    }
    // Los nudos, con sus anillos.
    listOf(Offset(w * 0.47f, w * 0.17f), Offset(w * 0.69f, w * 0.31f), Offset(w * 0.015f, g.ch + w * 0.3f)).forEachIndexed { i, o ->
        val r = w * (0.009f + 0.004f * azar(330, i))
        drawOval(a.luz(SUGI_OSC, lejos).copy(alpha = 0.8f), Offset(o.x - r, o.y - r * 1.6f), Size(r * 2, r * 3.2f))
        drawOval(a.sombra(0.4f), Offset(o.x - r * 1.8f, o.y - r * 3f), Size(r * 3.6f, r * 6f), style = Stroke(0.7f * u))
        drawOval(a.claro(0.18f), Offset(o.x - r * 2.6f, o.y - r * 4.4f), Size(r * 5.2f, r * 8.8f), style = Stroke(0.6f * u))
        // El relieve del nudo: la luz arriba a la izquierda del anillo y la sombra abajo a la derecha.
        drawArc(a.claro(0.35f), 180f, 90f, false, Offset(o.x - r * 1.3f, o.y - r * 2.2f), Size(r * 2.6f, r * 4.4f), style = Stroke(1f * u))
        drawArc(a.sombra(0.45f), 0f, 90f, false, Offset(o.x - r * 1.3f, o.y - r * 2.2f), Size(r * 2.6f, r * 4.4f), style = Stroke(1f * u))
    }
    // El hollín: la madera se ennegrece hacia el techo, y más sobre el farol, donde sube su humo.
    drawRect(a.luz(HOLLIN, lejos).copy(alpha = 0.45f), Offset.Zero, Size(w, w * 0.05f))
    drawRect(a.luz(HOLLIN, lejos).copy(alpha = 0.22f), Offset.Zero, Size(w, w * 0.12f))
    mancha(Offset(w * MuebleEstanteEdo.LAMP_X, w * 0.06f), w * 0.16f, a.luz(HOLLIN, lejos).copy(alpha = 0.4f))
    // Hacia la derecha y abajo, la pared queda en penumbra de día.
    if (!a.oscuro) {
        drawRect(sombraMueble(0.08f), Offset(w * 0.5f, 0f), Size(w * 0.5f, g.suelo))
        drawRect(sombraMueble(0.08f), Offset(w * 0.75f, 0f), Size(w * 0.25f, g.suelo))
        drawRect(sombraMueble(0.08f), Offset(0f, g.fondo), Size(w, g.suelo - g.fondo))
    }
    panoRanmaEdo(a, g)
    ranmaEdo(a, g)
    nageshiEdo(a, g)
    pilarEdo(a, g)
}

/**
 * El paño de luz que el ranma echa en diagonal sobre las tablas de sugi, hacia la derecha y abajo, con la sombra de los
 * listones de su cuadrícula: de día, el sol del cuarto de al lado; al anochecer de los Sueños, el ocaso; en su noche, la luna.
 */
private fun DrawScope.panoRanmaEdo(a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w
    val luz = when {
        a.lunar -> EdoTinta.LUNA.copy(alpha = 0.13f)
        a.oscuro -> return
        a.noche -> EdoTinta.OCASO.copy(alpha = 0.2f)
        else -> luzMueble(0.26f)
    }
    val y0 = g.nageshiAbajo + 2f * u
    val y1 = g.ch - g.gTapa
    val corre = (y1 - y0) * 0.62f
    val xa = w * 0.03f; val xb = w * 0.43f
    drawPath(poligonoEdo(xa - w * 0.02f, y0, xb + w * 0.02f, y0, xb + corre + w * 0.04f, y1, xa + corre - w * 0.01f, y1), luz.copy(alpha = luz.alpha * 0.45f))
    drawPath(poligonoEdo(xa, y0, xb, y0, xb + corre, y1, xa + corre, y1), luz)
    // La sombra de los listones: rayas que bajan con la misma inclinación, y la del travesaño, a lo ancho.
    val raya = if (a.lunar) a.sombra(0.12f) else sombraMueble(0.1f)
    var x = xa + w * 0.03f
    while (x < xb - 1f) {
        drawPath(poligonoEdo(x, y0, x + 1.8f * u, y0, x + 1.8f * u + corre, y1, x + corre, y1), raya)
        x += w * 0.034f
    }
    val yt = y0 + (y1 - y0) * 0.42f
    val ct = (yt - y0) * 0.62f
    drawPath(poligonoEdo(xa + ct, yt, xb + ct, yt, xb + ct + 2f * u, yt + 2.4f * u, xa + ct + 2f * u, yt + 2.4f * u), raya)
}

/** El farol en la pared: el charco cálido con su caída, y el tizne que deja su humo encima (de noche y en los Sueños). */
internal fun DrawScope.haloFarolEdo(a: AireEdo, g: MarcoEdo) {
    val w = g.w
    val farol = Offset(w * MuebleEstanteEdo.LAMP_X, w * MuebleEstanteEdo.LAMP_Y)
    if (a.lunar) {
        mancha(farol, w * 0.66f, FILO_MUEBLE.copy(alpha = 0.2f))
        mancha(farol, w * 0.32f, FILO_MUEBLE.copy(alpha = 0.36f))
        mancha(farol, w * 0.14f, EdoTinta.LLAMA.copy(alpha = 0.55f))
    } else if (a.lumbre) {
        val f = if (a.oscuro) 1f else 0.6f
        mancha(farol, w * 0.85f, FILO_MUEBLE.copy(alpha = 0.17f * f))
        mancha(farol, w * 0.42f, FILO_MUEBLE.copy(alpha = 0.32f * f))
        mancha(farol, w * 0.18f, EdoTinta.LLAMA.copy(alpha = 0.5f * f))
    }
    // El tizne sobre el farol, en el nageshi y en las tablas de encima.
    mancha(Offset(farol.x, g.nageshiAbajo + w * 0.02f), w * 0.07f, HOLLIN.copy(alpha = if (a.lumbre) 0.5f else 0.3f))
}

/** Todo lo fijo de la pared y del suelo (lo que no cambia de un dibujo a otro): se pinta una vez por tamaño y modo. */
internal fun DrawScope.capaParedEdo(a: AireEdo, g: MarcoEdo, cerca: (Offset) -> Float) {
    paredEdo(a, g)
    luzRanmaEdo(a, g)
    haloFarolEdo(a, g)
    sueloEdo(a, g, cerca)
}

/**
 * La pared que asoma a los lados del estante, entre la corona y la última tabla (lo demás lo tapan el fondo de los huecos y
 * los postes): a la izquierda, las tablas de sugi; a la derecha, el pilar, con la sombra del estante de día. De noche, por tramos.
 */
internal fun DrawScope.paredCostadosEdo(a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w
    if (g.fondo - g.ch < 1f) return
    val tramos = if (a.lumbre) ((g.fondo - g.ch) / (g.fila * 0.25f)).toInt().coerceIn(4, 80) else 1
    for (t in 0 until tramos) {
        val ya = g.ch + (g.fondo - g.ch) * t / tramos; val yb = g.ch + (g.fondo - g.ch) * (t + 1) / tramos + 0.6f
        val pos = ((ya + yb) / 2 - g.ch) / g.fila
        val ki = if (a.lumbre) cercaCuerpoEdo(pos, g.n, 0.015f, a.lunar) else 1f
        val kd = if (a.lumbre) cercaCuerpoEdo(pos, g.n, 0.98f, a.lunar) else 1f
        val fi = frioCuerpoEdo(pos, g.n, 0.015f)
        drawRect(a.luz(if (a.oscuro) SUGI else mezcla(SUGI, LUZ_MUEBLE, 0.12f), ki * 0.7f, fi), Offset(0f, ya), Size(g.posteIzq + 1f, yb - ya))
        drawRect(a.luz(mezcla(KURI, SUGI, 0.35f), kd * 0.9f), Offset(g.posteDer - 1f, ya), Size(w - g.posteDer + 1f, yb - ya))
    }
    drawLine(a.sombra(0.4f), Offset(g.posteIzq * 0.45f, g.ch), Offset(g.posteIzq * 0.45f, g.fondo), 1f * u)
    // De día, el estante echa su sombra en el pilar, a su derecha.
    if (!a.oscuro) drawRect(sombraMueble(if (a.noche) 0.34f else 0.3f), Offset(g.posteDer, g.ch), Size(w * 0.014f, g.fondo - g.ch))
}

/**
 * Lo fijo pintado una vez: cuatro imágenes (la franja de arriba y la de abajo, del diario y de los Sueños), cada una con la
 * clave de su tamaño y su modo; si la clave cambia, se vuelve a pintar. Pegar una imagen cuesta mucho menos que repetir los
 * degradados grandes y las capas translúcidas de la pared en cada dibujo.
 */
internal object CapaEdo {
    private val claves = arrayOfNulls<String>(4)
    private val imagenes = arrayOfNulls<ImageBitmap>(4)

    fun DrawScope.pegar(ranura: Int, clave: String, r: Rect, pintar: DrawScope.() -> Unit) {
        val ancho = r.width.roundToInt(); val alto = r.height.roundToInt()
        if (ancho < 1 || alto < 1 || ranura !in 0..3) return
        var img = imagenes[ranura]
        if (img == null || claves[ranura] != clave) {
            val nueva = ImageBitmap(ancho, alto)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(ancho.toFloat(), alto.toFloat())) {
                translate(-r.left, -r.top) { pintar() }
            }
            imagenes[ranura] = nueva; claves[ranura] = clave; img = nueva
        }
        drawImage(img, Offset(r.left, r.top))
    }
}

/** El ranma: el tragaluz de papel entre el nageshi y el techo, que da al cuarto de al lado; de él viene la luz de día. */
private fun DrawScope.ranmaEdo(a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w
    val r = Rect(-w * 0.01f, -w * 0.01f, w * 0.44f, g.nageshiArriba)
    val papel = when {
        a.lunar -> mezcla(EdoTinta.LUNA, EdoTinta.NOCHE_SUENOS, 0.42f)
        a.oscuro -> mezcla(WASHI, EdoTinta.NOCHE, 0.78f)
        a.noche -> mezcla(EdoTinta.OCASO, WASHI, 0.35f)
        else -> mezcla(WASHI, EdoTinta.SOL, 0.5f)
    }
    drawRect(papel, r.topLeft, r.size)
    // Detrás del papel: de noche en los Sueños, la luna; al anochecer, el sol bajo; de día, el resplandor parejo.
    when {
        a.lunar -> { mancha(Offset(w * 0.3f, w * 0.02f), w * 0.09f, EdoTinta.LUNA.copy(alpha = 0.85f)); mancha(Offset(w * 0.3f, w * 0.02f), w * 0.2f, EdoTinta.LUNA.copy(alpha = 0.3f)) }
        a.noche && !a.oscuro -> mancha(Offset(w * 0.08f, w * 0.05f), w * 0.2f, EdoTinta.OCASO.copy(alpha = 0.6f))
        !a.oscuro -> mancha(Offset(w * 0.12f, w * 0.03f), w * 0.25f, Color.White.copy(alpha = 0.55f))
        else -> {}
    }
    // La cuadrícula del papel (kumiko): listones finos de pie y uno a lo ancho.
    val liston = a.luz(KURI, if (a.oscuro) 0.05f else 0.2f, 0.2f)
    var x = r.left + w * 0.035f
    while (x < r.right) { drawLine(liston, Offset(x, r.top), Offset(x, r.bottom), 1.1f * u); x += w * 0.034f }
    drawLine(liston, Offset(r.left, r.top + r.height * 0.55f), Offset(r.right, r.top + r.height * 0.55f), 1.1f * u)
    // El marco del ranma: grueso a la derecha, donde termina contra la pared.
    drawRect(a.luz(KURI, 0.1f, 0.2f), Offset(r.right - w * 0.012f, r.top), Size(w * 0.016f, r.height))
    drawRect(a.claro(0.2f), Offset(r.right - w * 0.012f, r.top), Size(1f * u, r.height))
}

/** El nageshi: la viga de lado a lado, oscura y pulida, con su sombra en la pared. */
private fun DrawScope.nageshiEdo(a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w
    val y0 = g.nageshiArriba; val y1 = g.nageshiAbajo
    val tramos = if (a.lumbre) 16 else 1
    for (t in 0 until tramos) {
        val xa = w * t / tramos; val xb = w * (t + 1) / tramos + 0.6f
        val k = alcanceEdo((xa + xb) / 2 / w - MuebleEstanteEdo.LAMP_X, y1 / w - MuebleEstanteEdo.LAMP_Y, g.n.coerceAtLeast(1), a.lunar)
        val f = frioEdo((xa + xb) / 2 / w - MuebleEstanteEdo.RANMA_X, y1 / w - MuebleEstanteEdo.RANMA_Y)
        drawRect(a.luz(KURI, k, f), Offset(xa, y0), Size(xb - xa, y1 - y0))
        if (a.oscuro) drawRect(FILO_MUEBLE.copy(alpha = 0.05f + 0.5f * k), Offset(xa, y1 - 1.4f * u), Size(xb - xa, 1.4f * u))
    }
    clipRect(0f, y0, w, y1) { vetas(Rect(0f, y0, w, y1), a.sombra(0.5f), 341, densidad = 0.5f, grosor = 0.7f, ondulacion = 0.4f) }
    // El canto de arriba, pulido por los años, y la luz que le da desde el ranma, más fuerte a la izquierda.
    drawRect(a.claro(0.3f), Offset(0f, y0), Size(w * 0.6f, 1.2f * u))
    drawRect(a.claro(0.18f), Offset(0f, y0 + 1.2f * u), Size(w * 0.35f, (y1 - y0) * 0.35f))
    drawRect(a.sombra(0.45f), Offset(0f, y1), Size(w, 2.4f * u))
    drawRect(a.sombra(0.2f), Offset(0f, y1 + 2.4f * u), Size(w, 5f * u))
}

/** El pilar de la derecha, cortado por el borde: su arista achaflanada y su sombra en la pared. */
private fun DrawScope.pilarEdo(a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w
    val x0 = w * 0.952f
    drawRect(a.sombra(if (a.oscuro) 0.2f else 0.25f), Offset(x0 - 5f * u, 0f), Size(5f * u, g.suelo))
    val k = if (a.lumbre) alcanceEdo(0.97f - MuebleEstanteEdo.LAMP_X, 0.3f - MuebleEstanteEdo.LAMP_Y, g.n.coerceAtLeast(1), a.lunar) else 1f
    drawRect(a.luz(mezcla(KURI, SUGI, 0.35f), k * 0.9f), Offset(x0, 0f), Size(w - x0, g.suelo))
    vetas(Rect(x0, 0f, w, g.suelo), a.sombra(0.4f), 351, densidad = 0.8f, vertical = true, grosor = 0.6f)
    drawRect(a.luz(mezcla(KURI, HINOKI, 0.4f), k), Offset(x0, 0f), Size(2.2f * u, g.suelo))
    if (a.oscuro) drawRect(FILO_MUEBLE.copy(alpha = 0.45f * k), Offset(x0, 0f), Size(1.1f * u, g.suelo))
}

/** La luz que entra por el ranma: el sol del cuarto de al lado de día, el anochecer en los Sueños, la luna en su noche. */
internal fun DrawScope.luzRanmaEdo(a: AireEdo, g: MarcoEdo) {
    val w = g.w
    val c = Offset(w * MuebleEstanteEdo.RANMA_X, w * 0.13f)
    when {
        a.lunar -> { mancha(c, w * 0.7f, EdoTinta.LUNA.copy(alpha = 0.12f)); mancha(c, w * 0.32f, EdoTinta.LUNA.copy(alpha = 0.12f)) }
        a.oscuro -> mancha(c, w * 0.5f, EdoTinta.LUNA.copy(alpha = 0.05f))
        a.noche -> { mancha(c, w * 0.9f, EdoTinta.OCASO.copy(alpha = 0.24f)); mancha(c, w * 0.4f, EdoTinta.OCASO.copy(alpha = 0.26f)) }
        else -> {
            mancha(c, w * 0.85f, luzMueble(0.26f))
            mancha(c, w * 0.42f, luzMueble(0.3f))
            mancha(Offset(w * 0.12f, g.ch + w * 0.2f), w * 0.4f, luzMueble(0.1f))
        }
    }
}

// ---------------------------------------------------------------- el suelo

/** El suelo de tablas pulidas a fuerza de pasos: las juntas, que se abren hacia delante, el reflejo del estante y su sombra. */
internal fun DrawScope.sueloEdo(a: AireEdo, g: MarcoEdo, cerca: (Offset) -> Float) {
    val u = density; val w = g.w; val h = g.h
    if (h - g.suelo < 1f) return
    val tramos = if (a.lumbre) 10 else 1
    for (t in 0 until tramos) {
        val xa = w * t / tramos; val xb = w * (t + 1) / tramos + 0.6f
        val k = if (a.lumbre) cerca(Offset((xa + xb) / 2, g.suelo)) * 0.8f else 0.6f
        drawRect(a.luz(mezcla(HINOKI_OSC, SUGI, 0.55f), k, 0.05f), Offset(xa, g.suelo), Size(xb - xa, h - g.suelo))
    }
    // Las juntas de las tablas, que corren a lo ancho y se separan hacia delante.
    for (j in 1..5) {
        val t = (j / 5.5f).let { it * it }
        val y = g.suelo + (h - g.suelo) * t
        drawLine(a.sombra(0.4f), Offset(0f, y), Offset(w, y), 1f * u)
        drawLine(a.claro(0.15f), Offset(0f, y + 1f * u), Offset(w, y + 1f * u), 0.6f * u)
    }
    vetas(Rect(0f, g.suelo, w, h), a.sombra(0.25f), 361, densidad = 0.5f, grosor = 0.6f, ondulacion = 0.4f)
    // El zócalo de la pared y la sombra del estante, que de día cae hacia la derecha.
    drawRect(a.luz(KURI, 0.1f), Offset(0f, g.suelo - 2.6f * u), Size(w, 2.6f * u))
    // La sombra del estante en el suelo: de día corrida hacia la derecha y abajo; de noche, lejos del farol, a la izquierda.
    val corre = if (a.oscuro) -w * 0.05f else w * 0.08f
    drawPath(poligonoEdo(g.posteIzq, g.suelo, g.posteDer, g.suelo, g.posteDer + corre, g.suelo + w * 0.05f, g.posteIzq + corre, g.suelo + w * 0.05f), a.sombra(if (a.oscuro) 0.34f else 0.22f))
    drawRect(a.sombra(if (a.oscuro) 0.5f else 0.34f), Offset(g.posteIzq, g.suelo), Size(g.posteDer - g.posteIzq, w * 0.012f))
    // El brillo del suelo encerado: el reflejo de los postes y una mancha de luz a la izquierda.
    if (!a.oscuro) {
        drawRect(luzMueble(0.1f), Offset(g.posteIzq, g.suelo + w * 0.03f), Size(g.x0 - g.posteIzq, (h - g.suelo) * 0.5f))
        mancha(Offset(w * 0.25f, g.suelo + (h - g.suelo) * 0.5f), w * 0.3f, luzMueble(0.12f))
    }
}

// ---------------------------------------------------------------- el estante

/**
 * El fondo de un hueco: la tabla del fondo forrada con hojas de papel viejo reaprovechado, pegadas con engrudo y encimadas en
 * dos hileras (de tamaños distintos, alguna con la esquina levantada y su sombra), un parche más nuevo y más claro, manchas de
 * humedad con su cerco y, en algunas hojas, la aguada gris de unas columnas de cuentas ya lavadas (manchas alargadas, sin forma
 * de signo). De día la luz del ranma aclara arriba a la izquierda; encima, la sombra de la tabla de arriba, la del poste que le
 * quita la luz y la del último volumen.
 */
internal fun DrawScope.fondoHuecoEdo(k: Int, r: Rect, oc: Float, a: AireEdo, g: MarcoEdo, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
    val u = density; val w = g.w
    val x0 = g.x0; val x1 = g.x1
    clipRect(x0, r.top, x1, r.bottom) {
        val an = x1 - x0
        val papel = mezcla(mezcla(KAMI, SUGI_OSC, 0.42f), SOMBRA_MUEBLE, if (a.oscuro) 0f else 0.12f)
        // La tabla del fondo, más honda, que se ve entre hoja y hoja.
        val tramos = if (a.lumbre) 8 else 1
        for (t in 0 until tramos) {
            val xa = x0 + an * t / tramos; val xb = x0 + an * (t + 1) / tramos + 0.6f
            val p = Offset((xa + xb) / 2, r.center.y)
            drawRect(a.luz(mezcla(papel, SUGI_OSC, 0.45f), cerca(p) * 0.8f, frio(p)), Offset(xa, r.top), Size(xb - xa, r.height))
        }
        // Las hojas, en dos hileras encimadas: primero la de abajo, luego la de arriba, que monta sobre ella.
        for (fila in 1 downTo 0) {
            var x = x0 - w * 0.03f * azar(380 + k, fila); var j = 0
            while (x < x1 && j < 12) {
                val sem = 381 + k * 7 + fila
                val sw = w * (0.11f + 0.15f * azar(sem, j))
                val sh = r.height * (if (fila == 0) 0.5f + 0.25f * azar(sem + 1, j) else 0.6f + 0.3f * azar(sem + 1, j))
                val sy = if (fila == 0) r.top - r.height * 0.08f * azar(sem + 2, j) else r.bottom - sh + r.height * 0.06f * azar(sem + 2, j)
                val sx = x + (azar(sem + 3, j) - 0.5f) * 3f * u
                val tono = mezcla(papel, if (azar(sem + 4, j) > 0.5f) KAMI else SUGI_OSC, 0.12f * azar(sem + 5, j))
                val p = Offset(sx + sw / 2, sy + sh / 2)
                val color = a.luz(tono, cerca(p) * 0.85f, frio(p))
                drawRect(a.sombra(0.2f), Offset(sx + 1.2f * u, sy + 1.2f * u), Size(sw, sh))
                drawRect(color, Offset(sx, sy), Size(sw, sh))
                drawRect(a.claro(0.18f), Offset(sx, sy), Size(sw, 0.8f * u))
                // La aguada de unas cuentas ya lavadas: columnas de manchas grises, de derecha a izquierda, rotas y desiguales.
                if ((j + fila + k) % 3 == 1) aguadaCuentasEdo(Rect(sx, sy, sx + sw, sy + sh), color, sem + 6 + j)
                // Una esquina levantada: el papel despegado, más claro, con su sombra debajo.
                if (azar(sem + 7, j) > 0.62f) {
                    val q = Offset(sx + sw, if (fila == 0) sy + sh else sy)
                    val s = (5f + 4f * azar(sem + 8, j)) * u
                    val dy = if (fila == 0) -1f else 1f
                    drawPath(poligonoEdo(q.x - s, q.y, q.x, q.y, q.x, q.y + dy * s), a.luz(mezcla(papel, SUGI_OSC, 0.5f), cerca(q) * 0.7f, frio(q)))
                    drawPath(poligonoEdo(q.x - s, q.y, q.x - s * 0.2f, q.y + dy * s * 0.15f, q.x, q.y + dy * s), a.sombra(0.3f))
                    drawPath(poligonoEdo(q.x - s, q.y, q.x - s * 0.45f, q.y + dy * s * 0.7f, q.x, q.y + dy * s), a.luz(mezcla(tono, KAMI, 0.35f), cerca(q), frio(q)))
                }
                x += sw * (0.72f + 0.2f * azar(sem + 9, j)); j++
            }
        }
        // El parche más nuevo y más claro, pegado encima.
        val pw = w * (0.07f + 0.04f * azar(390, k)); val ph = r.height * (0.28f + 0.12f * azar(391, k))
        val pp = Offset(x0 + an * (0.15f + 0.7f * azar(392, k)), r.top + r.height * (0.2f + 0.4f * azar(393, k)))
        drawRect(a.sombra(0.22f), Offset(pp.x + 1f * u, pp.y + 1f * u), Size(pw, ph))
        drawRect(a.luz(mezcla(KAMI, EdoTinta.WASHI, 0.25f), cerca(pp) * 0.85f, frio(pp)), pp, Size(pw, ph))
        // Las manchas de humedad, con su cerco más oscuro.
        for (m in 0 until 2) {
            val c = Offset(x0 + an * azar(394 + k, m), r.top + r.height * (0.3f + 0.6f * azar(395 + k, m)))
            val rx = w * (0.035f + 0.04f * azar(396 + k, m)); val ry = rx * (0.6f + 0.3f * azar(397 + k, m))
            drawOval(a.sombra(0.08f), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
            drawOval(a.luz(mezcla(VETA, SUGI_OSC, 0.3f), 0.3f).copy(alpha = 0.22f), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2), style = Stroke(1f * u))
        }
        // De día, la luz del ranma: más clara arriba a la izquierda de cada hueco, pasada la sombra del poste.
        if (!a.oscuro) {
            val l = x0 + 18f * u
            drawPath(poligonoEdo(l, r.top, l + an * 0.6f, r.top, l + an * 0.3f, r.bottom, l, r.bottom), luzMueble(if (a.noche) 0.05f else 0.08f))
            drawPath(poligonoEdo(l, r.top, l + an * 0.36f, r.top, l + an * 0.1f, r.top + r.height * 0.7f, l, r.top + r.height * 0.7f), luzMueble(if (a.noche) 0.05f else 0.08f))
        }
        // La sombra de la tabla de arriba: honda junto a ella y deshecha hacia abajo.
        val f = if (a.oscuro) 1.2f else 1f
        drawRect(a.sombra(0.36f * f), Offset(x0, r.top), Size(an, 7f * u))
        drawRect(a.sombra(0.2f * f), Offset(x0, r.top), Size(an, 17f * u))
        drawRect(a.sombra(0.1f * f), Offset(x0, r.top), Size(an, 30f * u))
        // El poste de la izquierda le quita la luz a ese borde de día; de noche, el farol está a la derecha.
        if (!a.oscuro) {
            drawRect(a.sombra(0.3f), Offset(x0, r.top), Size(10f * u, r.height))
            drawRect(a.sombra(0.15f), Offset(x0 + 10f * u, r.top), Size(9f * u, r.height))
        } else {
            drawRect(a.sombra(0.3f), Offset(x1 - 11f * u, r.top), Size(11f * u, r.height))
            drawRect(a.sombra(0.16f), Offset(x1 - 21f * u, r.top), Size(10f * u, r.height))
        }
        // La sombra que el último volumen echa en el fondo, a su derecha.
        if (oc > r.left + 6f * u) {
            val largo = (if (a.oscuro) 15f else 11f) * u
            drawPath(poligonoEdo(oc - 2f * u, r.bottom, oc - 2f * u, r.top + r.height * 0.24f, oc + largo, r.top + r.height * 0.34f, oc + largo, r.bottom), a.sombra(if (a.oscuro) 0.34f else 0.24f))
        }
    }
}

/**
 * La aguada gris de unas cuentas ya lavadas en una hoja [r]: de cuatro a seis columnas de manchas alargadas, de derecha a
 * izquierda, cada una rota en dos o tres trozos de largo desigual y muy tenue. No son trazos: no tienen forma de signo.
 */
private fun DrawScope.aguadaCuentasEdo(r: Rect, papel: Color, semilla: Int) {
    val u = density
    val gris = mezcla(papel, EdoTinta.SUMI, 0.22f).copy(alpha = 0.55f)
    val columnas = 4 + (azar(semilla, 0) * 3).toInt()
    val paso = 3.4f * u
    for (c in 0 until columnas) {
        val x = r.right - 3f * u - paso * c
        if (x < r.left + 2f * u) break
        var y = r.top + r.height * (0.08f + 0.1f * azar(semilla, 10 + c))
        var t = 0
        while (y < r.bottom - r.height * 0.12f && t < 3) {
            val largo = r.height * (0.08f + 0.22f * azar(semilla, 20 + c * 3 + t))
            val fin = minOf(y + largo, r.bottom - r.height * 0.1f)
            drawLine(gris, Offset(x + (azar(semilla, 40 + c) - 0.5f) * u, y), Offset(x, fin), (1.4f + 0.8f * azar(semilla, 50 + c * 3 + t)) * u, StrokeCap.Round)
            y = fin + r.height * (0.04f + 0.08f * azar(semilla, 60 + c * 3 + t)); t++
        }
    }
}

/**
 * Una tabla pasante (nuki) de hinoki: atraviesa los dos postes y sale por fuera, con su testa a la vista y la cuña de madera
 * que la traba. Su cara de arriba apenas se ve; el canto lleva su veta, su arista gastada y su sombra. De noche, por tramos.
 */
internal fun DrawScope.tablaPasanteEdo(k: Int, r: Rect, a: AireEdo, g: MarcoEdo, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
    tablaEdo(Rect(g.w * 0.004f, r.top, g.w * 0.996f, r.bottom), 3.4f * density, 400 + 7 * k, a, g, { x -> cerca(Offset(x, r.center.y)) to frio(Offset(x, r.center.y)) })
}

internal fun DrawScope.tablaEdo(cara: Rect, fondo: Float, semilla: Int, a: AireEdo, g: MarcoEdo, luz: (Float) -> Pair<Float, Float>) {
    val u = density
    val tramos = if (a.lumbre) 22 else 1
    for (t in 0 until tramos) {
        val xa = cara.left + cara.width * t / tramos; val xb = cara.left + cara.width * (t + 1) / tramos + 0.6f
        val (k, f) = luz((xa + xb) / 2)
        drawRect(a.luz(mezcla(HINOKI, LUZ_MUEBLE, 0.3f), k, f), Offset(xa, cara.top - fondo), Size(xb - xa, fondo))
        drawRect(a.luz(HINOKI, k, f), Offset(xa, cara.top), Size(xb - xa, cara.height))
        if (a.oscuro) drawRect(FILO_MUEBLE.copy(alpha = 0.06f + 0.5f * k), Offset(xa, cara.top), Size(xb - xa, 1.1f * u))
        if (a.oscuro && f > 0.04f) drawRect(a.helado(0.4f * f), Offset(xa, cara.top + 1.1f * u), Size(xb - xa, 0.8f * u))
    }
    clipRect(cara.left, cara.top - fondo, cara.right, cara.bottom) {
        vetas(cara, a.luz(VETA, 0.5f).copy(alpha = if (a.oscuro) 0.4f else 0.34f), semilla, densidad = 0.75f, grosor = 0.6f, ondulacion = 0.3f)
        // La arista gastada: mellas cortas.
        for (j in 0 until 5) {
            val x = cara.left + cara.width * azar(semilla, 11 + j)
            drawPath(poligonoEdo(x, cara.top, x + 3f * u, cara.top, x + 1.4f * u, cara.top + 1.8f * u), a.sombra(0.3f))
        }
    }
    if (!a.oscuro) drawRect(a.claro(0.45f), cara.topLeft, Size(cara.width, 1.1f * u))
    drawLine(a.sombra(0.36f), Offset(cara.left, cara.top - fondo), Offset(cara.right, cara.top - fondo), 0.7f * u)
    drawRect(a.sombra(0.36f), Offset(cara.left, cara.bottom - 1.5f * u), Size(cara.width, 1.5f * u))
    drawRect(a.sombra(0.2f), Offset(cara.left, cara.bottom), Size(cara.width, 3f * u))
    // Las testas, fuera de los postes: la madera de punta, más oscura, con sus anillos, y la cuña que la traba.
    listOf(cara.left to 1f, cara.right to -1f).forEach { (xb, s) ->
        val (k, f) = luz(xb)
        val testa = Rect(minOf(xb, xb + s * 2.4f * u), cara.top, maxOf(xb, xb + s * 2.4f * u), cara.bottom)
        drawRect(a.luz(HINOKI_OSC, k, f), testa.topLeft, testa.size)
        val xc = xb + s * g.salida * 0.5f
        val cu = poligonoEdo(xc - 2.6f * u, cara.top - fondo - 4f * u, xc + 2.6f * u, cara.top - fondo - 4f * u, xc + 1.4f * u, cara.bottom + 3.5f * u, xc - 1.4f * u, cara.bottom + 3.5f * u)
        drawPath(corridoEdo(cu, 1.2f * u, 1f * u), a.sombra(0.4f))
        drawPath(cu, a.luz(mezcla(HINOKI_OSC, KURI, 0.35f), k, f))
        drawLine(a.claro(0.35f), Offset(xc - 2.2f * u, cara.top - fondo - 3.6f * u), Offset(xc - 1.2f * u, cara.bottom + 3f * u), 0.6f * u)
        if (a.oscuro && f > 0.03f) drawLine(a.helado(0.7f * f), Offset(xc - 2.4f * u, cara.top - fondo - 3.8f * u), Offset(xc - 1.3f * u, cara.bottom + 3.2f * u), 0.9f * u)
        drawLine(a.sombra(0.4f), Offset(xc - 2.6f * u, cara.top - fondo - 4f * u), Offset(xc + 2.6f * u, cara.top - fondo - 4f * u), 0.7f * u)
    }
}

/**
 * Los dos postes del estante: de hinoki, con las aristas achaflanadas (una raya de luz en la que mira a la luz), que suben un
 * poco por encima de la tabla de arriba con la cabeza cortada en bisel y bajan hasta el suelo. De noche, por tramos.
 */
internal fun DrawScope.postesEdo(a: AireEdo, g: MarcoEdo, d: DisposicionEstante) {
    val u = density; val w = g.w
    val arriba = g.ch - g.gTapa - w * 0.035f
    val abajo = g.suelo + w * 0.012f
    val tramos = if (a.lumbre) ((abajo - arriba) / (g.fila * 0.25f)).toInt().coerceIn(4, 80) else 1
    listOf(g.posteIzq to g.x0, g.x1 to g.posteDer).forEachIndexed { i, (xa, xb) ->
        for (t in 0 until tramos) {
            val ya = arriba + (abajo - arriba) * t / tramos; val yb = arriba + (abajo - arriba) * (t + 1) / tramos + 0.6f
            val p = Offset((xa + xb) / 2, (ya + yb) / 2)
            val k = if (p.y <= g.ch) alcanceEdo(p.x / w - MuebleEstanteEdo.LAMP_X, p.y / w - MuebleEstanteEdo.LAMP_Y, g.n, a.lunar) else cercaCuerpoEdo((p.y - g.ch) / g.fila, g.n, p.x / w, a.lunar)
            val f = if (p.y <= g.ch) frioEdo(p.x / w - MuebleEstanteEdo.RANMA_X, p.y / w - MuebleEstanteEdo.RANMA_Y) else frioCuerpoEdo((p.y - g.ch) / g.fila, g.n, p.x / w)
            drawRect(a.luz(mezcla(HINOKI, HINOKI_OSC, 0.18f), k, f), Offset(xa, ya), Size(xb - xa, yb - ya))
            if (a.oscuro) {
                // El chaflán que mira al farol se enciende.
                drawRect(FILO_MUEBLE.copy(alpha = (0.55f * k).coerceAtMost(0.55f)), Offset(xb - 1.6f * u, ya), Size(1.6f * u, yb - ya))
                // El chaflán de la izquierda, que mira al ranma, se enfría con la luna.
                if (f > 0.04f) drawRect(a.helado(0.5f * f), Offset(xa, ya), Size(1.2f * u, yb - ya))
            }
        }
        clipRect(xa, arriba, xb, abajo) {
            vetas(Rect(xa, arriba, xb, abajo), a.luz(VETA, 0.5f).copy(alpha = if (a.oscuro) 0.4f else 0.32f), 420 + i, densidad = 0.9f, vertical = true, grosor = 0.6f, ondulacion = 0.25f)
        }
        // Los chaflanes: a la izquierda la luz del ranma, a la derecha la sombra.
        if (!a.oscuro) drawRect(a.claro(0.5f), Offset(xa + 1.2f * u, arriba), Size(1.6f * u, abajo - arriba))
        drawRect(a.sombra(0.32f), Offset(xb - 2.2f * u, arriba), Size(2.2f * u, abajo - arriba))
        drawRect(a.sombra(0.16f), Offset(xa, arriba), Size(1.2f * u, abajo - arriba))
        // La cabeza cortada en bisel y su sombra en la pared.
        val bis = (xb - xa) * 0.35f
        drawPath(poligonoEdo(xa, arriba, xa + bis, arriba - bis * 0.6f, xb - bis, arriba - bis * 0.6f, xb, arriba), a.luz(mezcla(HINOKI, LUZ_MUEBLE, 0.4f), 0.4f))
        drawRect(a.sombra(if (a.oscuro) 0.25f else 0.2f), Offset(xb, arriba), Size(4f * u, g.ch - g.gTapa - arriba))
        // Donde la tabla pasa por el poste, la junta: una raya fina arriba y abajo.
        d.tablas.forEach { t ->
            drawLine(a.sombra(0.4f), Offset(xa, t.top - 3.4f * u), Offset(xb, t.top - 3.4f * u), 0.7f * u)
            drawLine(a.sombra(0.4f), Offset(xa, t.bottom), Offset(xb, t.bottom), 0.7f * u)
        }
        // La sombra del poste en el suelo.
        drawOval(a.sombra(0.4f), Offset(xa - 1f * u, abajo - 2f * u), Size(xb - xa + 6f * u, 4f * u))
    }
}

/** La tabla de arriba, pasante como las otras, donde se apoya lo de encima; su sombra cae en la primera repisa. */
internal fun DrawScope.tapaEstanteEdo(a: AireEdo, g: MarcoEdo, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
    val y = g.ch - g.gTapa
    tablaEdo(Rect(g.w * 0.004f, y, g.w * 0.996f, g.ch), g.fTapa, 470, a, g, { x -> cerca(Offset(x, y)) to frio(Offset(x, y)) })
}

/** Bajo la última repisa: el travesaño cerca del suelo y la sombra del hueco de abajo. */
internal fun DrawScope.pieEstanteEdo(a: AireEdo, g: MarcoEdo, cerca: (Offset) -> Float) {
    val u = density
    if (g.suelo - g.fondo < 4f * u) return
    drawRect(a.sombra(if (a.oscuro) 0.45f else 0.3f), Offset(g.x0, g.fondo), Size(g.x1 - g.x0, (g.suelo - g.fondo) * 0.6f))
    val y = g.suelo - g.w * 0.032f
    val k = cerca(Offset(g.w * 0.6f, y))
    drawRect(a.luz(mezcla(HINOKI, HINOKI_OSC, 0.3f), k), Offset(g.x0, y), Size(g.x1 - g.x0, g.w * 0.02f))
    if (!a.oscuro) drawRect(a.claro(0.35f), Offset(g.x0, y), Size(g.x1 - g.x0, 1f * u))
    drawRect(a.sombra(0.3f), Offset(g.x0, y + g.w * 0.02f), Size(g.x1 - g.x0, 2f * u))
}

/** El rótulo de un mes: una tablilla de hinoki nuevo, con las esquinas cortadas, clavada en el canto de la tabla. */
internal fun DrawScope.rotuloEdo(p: PlacaEstante, medidor: TextMeasurer, a: AireEdo, cerca: Float) {
    val u = density
    val r = Rect(p.r.left, p.r.top - 0.6f * u, p.r.right, p.r.bottom + 0.6f * u)
    val c = minOf(r.height * 0.3f, 3f * u)
    val forma = poligonoEdo(r.left + c, r.top, r.right - c, r.top, r.right, r.top + c, r.right, r.bottom - c, r.right - c, r.bottom, r.left + c, r.bottom, r.left, r.bottom - c, r.left, r.top + c)
    drawPath(corridoEdo(forma, 1.2f * u, 1.4f * u), a.sombra(0.4f))
    drawPath(forma, a.etiqueta(maxOf(cerca, 0.2f), mezcla(HINOKI, WASHI, 0.55f)))
    clipRect(r.left, r.top, r.right, r.bottom) {
        vetas(r, a.luz(VETA, 0.6f).copy(alpha = 0.22f), 480 + p.texto.length, densidad = 0.6f, grosor = 0.5f, ondulacion = 0.3f)
    }
    drawRect(a.claro(0.4f), Offset(r.left + c, r.top + 0.4f * u), Size(r.width - 2 * c, 0.8f * u))
    // Los dos clavos de hierro, con su punto de luz.
    listOf(r.left + 4f * u, r.right - 4f * u).forEach { x ->
        drawCircle(a.luz(EdoTinta.SUMI, 0.3f), 1.3f * u, Offset(x, r.center.y))
        drawCircle(a.claro(0.5f).copy(alpha = if (a.oscuro) 0.2f else 0.6f), 0.5f * u, Offset(x - 0.4f * u, r.center.y - 0.4f * u))
    }
    textoEn(medidor, p.texto, Rect(r.left + 8f * u, r.top + 1.2f * u, r.right - 8f * u, r.bottom - 1.2f * u), MuebleEstanteEdo.LETRA, a.tinta, 10f, peso = FontWeight.Bold, minimo = 5f)
}

/** Una raya de luz cálida en un canto que mira al farol (de noche). */
internal fun DrawScope.filoFarolEdo(desde: Offset, hasta: Offset, k: Float, a: AireEdo) {
    if (a.oscuro && k > 0.05f) drawLine(FILO_MUEBLE.copy(alpha = (0.6f * k).coerceAtMost(0.6f)), desde, hasta, 1.1f * density, StrokeCap.Round)
}
