package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.ITA
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KABE
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KABE_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.LLAMA
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.ORO
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.PAJA
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.SUGI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.SUGI_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.SUMI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.WASHI
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.cos
import kotlin.math.sin

// La pared, el suelo y las dos capas fijas (la corona y el pie) del estante de la casa de té (MuebleEstanteEdo).

// ---------------------------------------------------------------- las capas fijas

/** Todo lo de arriba: la pared con su luz, la cabeza de los postes, la tabla de arriba y el bodegón. Se pinta una vez. */
internal fun DrawScope.coronaEdo(a: AireEdo, g: MarcoEdo) {
    paredEdo(a, g, 0f, g.ch)
    tapaEstanteEdo(a, g)
    postesEdo(a, g, g.ch - g.gTapa - g.w * 0.05f, g.ch, emptyList())
    bodegonEdo(a, g)
}

/** Todo lo de abajo: la pared de los lados, el suelo, el cuerpo bajo de puertas correderas y lo que hay al pie. Se pinta una vez. */
internal fun DrawScope.pieEdo(a: AireEdo, g: MarcoEdo) {
    paredEdo(a, g, g.fondo, g.suelo)
    sueloEdo(a, g)
    postesEdo(a, g, g.fondo, g.pisan, emptyList())
    cuerpoBajoEdo(a, g)
    alPieEdo(a, g)
    if (a.lumbre) {
        val b = g.brasa * g.w
        mancha(b, g.w * 0.22f, EdoTinta.BRASA.copy(alpha = if (a.oscuro) 0.2f else 0.1f))
        mancha(b, g.w * 0.07f, EdoTinta.BRASA.copy(alpha = if (a.oscuro) 0.3f else 0.14f))
    }
}

// ---------------------------------------------------------------- la pared

/**
 * La pared, de [y0] a [y1]: el enlucido de tierra clara (con su luz de día que entra por la izquierda, o el charco del farol de
 * noche), las pajas del amasado y el roce; abajo, el zócalo de tablas ahumadas con su travesaño; arriba, el nageshi y el ranma;
 * a la derecha, el pilar; el paño de luz del ranma en diagonal y, de noche, el halo del farol y su tizne.
 */
internal fun DrawScope.paredEdo(a: AireEdo, g: MarcoEdo, y0: Float, y1: Float) {
    val w = g.w
    if (y1 - y0 < 1f) return
    clipRect(0f, y0, w, y1) {
        val yz = g.zocalo
        if (y0 < yz) enlucidoEdo(a, g, Rect(0f, 0f, w, yz))
        if (y1 > yz) zocaloTablasEdo(a, g, Rect(0f, yz, w, g.suelo))
        if (y0 < g.nageshiAbajo + w * 0.05f) { ranmaEdo(a, g); nageshiEdo(a, g) }
        if (y0 < yz + w * 0.02f && y1 > yz - w * 0.02f) dobuchiEdo(a, g)
        pilarEdo(a, g)
        if (y0 < yz) panoRanmaEdo(a, g)
        haloFarolEdo(a, g)
    }
}

/** El enlucido de tierra clara: de día, más claro a la izquierda; de noche, el charco del farol con su caída. */
private fun DrawScope.enlucidoEdo(a: AireEdo, g: MarcoEdo, r: Rect) {
    val u = density; val w = g.w
    if (!a.oscuro) {
        val base = a.luz(KABE, 0.5f)
        drawRect(Brush.horizontalGradient(listOf(mezcla(base, LUZ_MUEBLE, 0.3f), base, mezcla(base, KABE_OSC, 0.55f)), 0f, w), r.topLeft, r.size)
        mancha(Offset(-w * 0.05f, r.height * 0.45f), w * 0.7f, luzMueble(if (a.noche) 0.12f else 0.28f))
    } else {
        // El charco del farol: de su color hondo lejos al color de la tierra encendida cerca, con la caída del Ideario.
        val f = Offset(w * MuebleEstanteEdo.LAMP_X, w * MuebleEstanteEdo.LAMP_Y)
        fun k(d: Float) = a.luz(KABE, farolEdo(MuebleEstanteEdo.LAMP_X + d, MuebleEstanteEdo.LAMP_Y, a.lunar))
        drawRect(Brush.radialGradient(0f to k(0.03f), 0.1f to k(0.12f), 0.22f to k(0.26f), 0.4f to k(0.5f), 0.65f to k(0.8f), 1f to k(1.25f),
            center = f, radius = w * 1.25f), r.topLeft, r.size)
        // Lo lejano del farol se hunde en la noche con color (no en un gris parejo), y cerca la tierra se entibia.
        drawRect(Brush.horizontalGradient(listOf(a.hondo.copy(alpha = if (a.lunar) 0.22f else 0.38f), Color.Transparent), 0f, w * 0.6f), r.topLeft, r.size)
        mancha(f, w * 0.55f, LLAMA.copy(alpha = if (a.lunar) 0.1f else 0.16f))
        if (a.lunar) mancha(Offset(w * MuebleEstanteEdo.RANMA_X, w * 0.14f), w * 0.55f, EdoTinta.LUNA.copy(alpha = 0.16f))
    }
    pinceladas(r, a.luz(KABE_OSC, 0.4f), 3001, 40, u, 0.1f)
    pinceladas(r, a.luz(mezcla(KABE, LUZ_MUEBLE, 0.5f), 0.4f), 3002, 20, u, 0.1f)
    // Las pajas del amasado: cortas, en todas direcciones; unas en sombra y otras que brillan.
    for (j in 0 until 110) {
        val p = Offset(w * azar(3003, j), r.top + r.height * azar(3004, j))
        val k = g.cerca(p)
        val ang = (azar(3005, j) - 0.5f) * 2.8f
        val l = (2.4f + 5f * azar(3006, j)) * u
        val q = Offset(p.x + l * cos(ang), p.y + l * sin(ang) * 0.7f)
        if (j % 3 == 0) drawLine(a.sombra(0.2f), p, q, 0.7f * u)
        else drawLine(a.luz(mezcla(PAJA, LUZ_MUEBLE, 0.3f), k).copy(alpha = 0.65f), p, q, 0.7f * u)
    }
    // El roce de los hombros a la altura del zócalo y dos agujeros de clavos viejos.
    drawRect(a.sombra(0.06f), Offset(0f, g.zocalo - w * 0.07f), Size(w, w * 0.07f))
    listOf(Offset(w * 0.4f, w * 0.2f), Offset(w * 0.72f, w * 0.43f)).forEach { o ->
        drawCircle(a.sombra(0.4f), 1.1f * u, o)
        drawCircle(a.claro(0.4f), 0.8f * u, o + Offset(0.8f * u, 0.8f * u))
    }
}

/** El zócalo de la pared: tablas de sugi de pie, ennegrecidas por el humo del fogón, con sus juntas y su relieve. */
private fun DrawScope.zocaloTablasEdo(a: AireEdo, g: MarcoEdo, r: Rect) {
    val u = density; val w = g.w
    if (r.height < 1f) return
    val tramos = if (a.oscuro) 10 else 1
    for (t in 0 until tramos) {
        val xa = w * t / tramos; val xb = w * (t + 1) / tramos + 0.6f
        val k = g.cerca(Offset((xa + xb) / 2, r.top + w * 0.06f))
        drawRect(a.luz(ITA, k), Offset(xa, r.top), Size(xb - xa, r.height))
    }
    if (!a.oscuro) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.16f), Color.Transparent, sombraMueble(0.12f)), 0f, w), r.topLeft, r.size)
    // El humo: más negro arriba, bajo el travesaño.
    drawRect(Brush.verticalGradient(listOf(a.sombra(0.3f), Color.Transparent), r.top, r.top + w * 0.08f), r.topLeft, Size(w, w * 0.08f))
    var x = -w * 0.02f; var j = 0
    while (x < w) {
        val an = w * (0.09f + 0.04f * azar(3011, j))
        drawRect(a.claro(0.16f), Offset(x + 1.2f * u, r.top), Size(2f * u, r.height))
        drawRect(a.sombra(0.14f), Offset(x + an - 3f * u, r.top), Size(3f * u, r.height))
        x += an; j++
        drawLine(a.sombra(0.6f), Offset(x, r.top), Offset(x, r.bottom), 1.2f * u)
    }
    vetas(r, a.sombra(0.4f), 3012, densidad = 0.6f, vertical = true, grosor = 0.7f, ondulacion = 0.6f)
}

/** El travesaño de la pared (dōbuchi), oscuro, con su arista de luz arriba y su sombra en las tablas. */
private fun DrawScope.dobuchiEdo(a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w
    val y = g.zocalo - w * 0.012f
    val alto = w * 0.024f
    for (t in 0 until 8) {
        val xa = w * t / 8f; val k = g.cerca(Offset(xa + w / 16f, y))
        drawRect(a.luz(SUGI, k), Offset(xa, y), Size(w / 8f + 1f, alto))
        drawRect(a.brillo(0.35f, k), Offset(xa, y), Size(w / 8f + 1f, 1f * u))
    }
    vetas(Rect(0f, y, w, y + alto), a.sombra(0.4f), 3021, densidad = 0.6f, grosor = 0.6f, ondulacion = 0.4f)
    drawRect(a.sombra(0.4f), Offset(0f, y + alto), Size(w, 2f * u))
    drawRect(a.sombra(0.16f), Offset(0f, y + alto + 2f * u), Size(w, 4f * u))
}

/** El ranma: el tragaluz de papel entre el nageshi y el techo, que da al cuarto de al lado; de él viene la luz de día. */
private fun DrawScope.ranmaEdo(a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w
    val r = Rect(-w * 0.01f, -w * 0.01f, w * 0.46f, g.nageshiArriba)
    val papel = when {
        a.lunar -> mezcla(EdoTinta.LUNA, EdoTinta.NOCHE_SUENOS, 0.4f)
        a.oscuro -> mezcla(WASHI, EdoTinta.NOCHE, 0.8f)
        a.noche -> mezcla(EdoTinta.OCASO, WASHI, 0.3f)
        else -> mezcla(WASHI, EdoTinta.SOL, 0.5f)
    }
    drawRect(papel, r.topLeft, r.size)
    when {
        a.lunar -> { mancha(Offset(w * 0.3f, w * 0.02f), w * 0.08f, EdoTinta.LUNA.copy(alpha = 0.9f)); mancha(Offset(w * 0.3f, w * 0.02f), w * 0.2f, EdoTinta.LUNA.copy(alpha = 0.3f)) }
        a.noche && !a.oscuro -> mancha(Offset(w * 0.08f, w * 0.05f), w * 0.22f, EdoTinta.OCASO.copy(alpha = 0.6f))
        !a.oscuro -> mancha(Offset(w * 0.12f, w * 0.03f), w * 0.26f, Color.White.copy(alpha = 0.6f))
        else -> mancha(Offset(w * 0.3f, w * 0.02f), w * 0.12f, EdoTinta.LUNA.copy(alpha = 0.12f))
    }
    // La cuadrícula del papel (kumiko): listones finos de pie y dos a lo ancho, con su lado de luz.
    val liston = a.luz(SUGI, if (a.oscuro) 0.05f else 0.4f, 0.3f)
    var x = r.left + w * 0.035f
    while (x < r.right - w * 0.01f) {
        drawLine(liston, Offset(x, r.top), Offset(x, r.bottom), 1.2f * u)
        x += w * 0.034f
    }
    listOf(0.4f, 0.75f).forEach { f -> drawLine(liston, Offset(r.left, r.top + r.height * f), Offset(r.right, r.top + r.height * f), 1.2f * u) }
    // El marco, grueso a la derecha, donde termina contra el enlucido, y la sombra que echa en él.
    drawRect(a.luz(SUGI, 0.2f, 0.2f), Offset(r.right - w * 0.014f, r.top), Size(w * 0.018f, r.height))
    drawRect(a.claro(0.3f), Offset(r.right - w * 0.014f, r.top), Size(1f * u, r.height))
    drawRect(a.sombra(0.2f), Offset(r.right + w * 0.004f, r.top), Size(4f * u, r.height))
}

/** El nageshi: la viga de lado a lado, oscura y pulida, con sus tapaclavos de bronce y su sombra en el enlucido. */
private fun DrawScope.nageshiEdo(a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w
    val y0 = g.nageshiArriba; val y1 = g.nageshiAbajo
    for (t in 0 until 16) {
        val xa = w * t / 16f; val xb = w * (t + 1) / 16f + 0.6f
        val k = g.cerca(Offset((xa + xb) / 2, y1))
        drawRect(a.luz(SUGI, k, g.frio(Offset((xa + xb) / 2, y1))), Offset(xa, y0), Size(xb - xa, y1 - y0))
        if (a.oscuro) drawRect(a.brillo(0.6f, k), Offset(xa, y1 - 1.4f * u), Size(xb - xa, 1.4f * u))
    }
    vetas(Rect(0f, y0, w, y1), a.sombra(0.5f), 3031, densidad = 0.5f, grosor = 0.7f, ondulacion = 0.4f)
    if (!a.oscuro) {
        drawRect(a.claro(0.4f), Offset(0f, y0), Size(w * 0.6f, 1.2f * u))
        drawRect(a.claro(0.14f), Offset(0f, y0 + 1.2f * u), Size(w * 0.4f, (y1 - y0) * 0.4f))
    }
    // Los tapaclavos: dos chapas de bronce con su punto, sobre la viga.
    listOf(w * 0.56f, w * 0.93f).forEach { x ->
        val k = g.cerca(Offset(x, (y0 + y1) / 2))
        val c = Offset(x, (y0 + y1) / 2)
        drawOval(a.sombra(0.4f), Offset(c.x - w * 0.016f + 1f * u, c.y - (y1 - y0) * 0.28f + 1f * u), Size(w * 0.032f, (y1 - y0) * 0.56f))
        drawOval(a.luz(mezcla(ORO, SUGI, 0.25f), k), Offset(c.x - w * 0.016f, c.y - (y1 - y0) * 0.28f), Size(w * 0.032f, (y1 - y0) * 0.56f))
        drawOval(a.brillo(0.5f, k), Offset(c.x - w * 0.012f, c.y - (y1 - y0) * 0.22f), Size(w * 0.012f, (y1 - y0) * 0.16f))
        drawCircle(a.luz(HIERRO, k), 1.2f * u, c)
    }
    drawRect(a.sombra(0.45f), Offset(0f, y1), Size(w, 2.4f * u))
    drawRect(a.sombra(0.18f), Offset(0f, y1 + 2.4f * u), Size(w, w * 0.018f))
}

/** El pilar de la derecha, cortado por el borde: su veta, la arista que mira a la luz y su sombra en el enlucido. */
private fun DrawScope.pilarEdo(a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w
    val x0 = w * 0.95f
    drawRect(a.sombra(if (a.oscuro) 0.1f else 0.16f), Offset(x0 - 6f * u, g.nageshiAbajo), Size(6f * u, g.suelo - g.nageshiAbajo))
    for (t in 0 until 10) {
        val ya = g.suelo * t / 10f; val yb = g.suelo * (t + 1) / 10f + 0.6f
        val k = g.cerca(Offset(x0, (ya + yb) / 2))
        drawRect(a.luz(mezcla(SUGI, SUGI_LUZ, 0.25f), k), Offset(x0, ya), Size(w - x0, yb - ya))
        drawRect(a.brillo(0.45f, k), Offset(x0, ya), Size(1.4f * u, yb - ya))
    }
    vetas(Rect(x0, 0f, w, g.suelo), a.sombra(0.4f), 3041, densidad = 0.8f, vertical = true, grosor = 0.7f, ondulacion = 0.7f)
}

/**
 * El paño de luz que el ranma echa en diagonal sobre el enlucido, hacia la derecha y abajo, con la sombra de los listones de su
 * cuadrícula: de día, el sol del cuarto de al lado; al anochecer de los Sueños, el ocaso; en su noche, la luna.
 */
private fun DrawScope.panoRanmaEdo(a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w
    val luz = when {
        a.lunar -> EdoTinta.LUNA.copy(alpha = 0.16f)
        a.oscuro -> return
        a.noche -> EdoTinta.OCASO.copy(alpha = 0.22f)
        else -> luzMueble(0.3f)
    }
    val y0 = g.nageshiAbajo + 2f * u
    val y1 = g.zocalo
    val corre = (y1 - y0) * 0.6f
    val xa = w * 0.03f; val xb = w * 0.44f
    clipRect(0f, y0, w, y1) {
        drawPath(poligonoEdo(xa - w * 0.025f, y0, xb + w * 0.025f, y0, xb + corre + w * 0.05f, y1, xa + corre - w * 0.015f, y1), luz.copy(alpha = luz.alpha * 0.4f))
        drawPath(poligonoEdo(xa, y0, xb, y0, xb + corre, y1, xa + corre, y1), luz)
        val raya = if (a.lunar) a.sombra(0.14f) else sombraMueble(0.11f)
        var x = xa + w * 0.03f
        while (x < xb - 1f) {
            drawPath(poligonoEdo(x, y0, x + 2f * u, y0, x + 2f * u + corre, y1, x + corre, y1), raya)
            x += w * 0.034f
        }
        listOf(0.36f, 0.7f).forEach { f ->
            val yt = y0 + (y1 - y0) * f
            val ct = (yt - y0) * 0.6f
            drawPath(poligonoEdo(xa + ct, yt, xb + ct, yt, xb + ct + 2.4f * u, yt + 2.6f * u, xa + ct + 2.4f * u, yt + 2.6f * u), raya)
        }
    }
}

/** El farol en la pared: el halo cálido sobre el enlucido y el tizne que deja su humo en el nageshi, encima. */
private fun DrawScope.haloFarolEdo(a: AireEdo, g: MarcoEdo) {
    val w = g.w
    val f = Offset(w * MuebleEstanteEdo.LAMP_X, w * MuebleEstanteEdo.LAMP_Y)
    if (a.lunar) {
        mancha(f, w * 0.45f, LLAMA.copy(alpha = 0.2f))
        mancha(f, w * 0.2f, LLAMA.copy(alpha = 0.34f))
    } else if (a.oscuro) {
        mancha(f, w * 0.78f, LLAMA.copy(alpha = 0.17f))
        mancha(f, w * 0.36f, LLAMA.copy(alpha = 0.3f))
        mancha(f, w * 0.14f, Color(0xFFFFF0C8).copy(alpha = 0.34f))
    } else if (a.noche) {
        mancha(f, w * 0.4f, LLAMA.copy(alpha = 0.16f))
    }
    mancha(Offset(f.x, g.nageshiAbajo + w * 0.015f), w * 0.07f, SUMI.copy(alpha = if (a.lumbre) 0.4f else 0.22f))
}

/**
 * La pared que asoma a los lados del estante, entre la corona y la última tabla (lo demás lo tapan los huecos y los postes):
 * a la izquierda, el zócalo de tablas; a la derecha, el pilar, con la sombra del estante de día. De noche, por tramos.
 */
internal fun DrawScope.paredCostadosEdo(a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w
    val tramos = if (a.lumbre) ((g.fondo - g.ch) / (g.fila * 0.25f)).toInt().coerceIn(2, 80) else 1
    for (t in 0 until tramos) {
        val ya = g.ch + (g.fondo - g.ch) * t / tramos; val yb = g.ch + (g.fondo - g.ch) * (t + 1) / tramos + 0.6f
        val yc = (ya + yb) / 2
        drawRect(a.luz(if (a.oscuro) ITA else mezcla(ITA, LUZ_MUEBLE, 0.1f), g.cerca(Offset(w * 0.01f, yc))), Offset(0f, ya), Size(g.posteIzq + 1f, yb - ya))
        drawRect(a.luz(mezcla(SUGI, SUGI_LUZ, 0.25f), g.cerca(Offset(w * 0.98f, yc))), Offset(g.posteDer - 1f, ya), Size(w - g.posteDer + 1f, yb - ya))
    }
    drawLine(a.sombra(0.5f), Offset(g.posteIzq * 0.4f, g.ch), Offset(g.posteIzq * 0.4f, g.fondo), 1f * u)
    // De día, el estante echa su sombra en el pilar, a su derecha.
    if (!a.oscuro) drawRect(sombraMueble(0.3f), Offset(g.posteDer, g.ch), Size(w * 0.016f, g.fondo - g.ch))
}

// ---------------------------------------------------------------- el suelo

/**
 * El suelo de tablas de castaño, pulidas por los pasos: corren hacia quien mira y se abren, con sus juntas, su veta, el brillo
 * de la cera donde da la luz y la sombra del estante. De noche, el charco del farol que baja y el rescoldo de la brasa.
 */
internal fun DrawScope.sueloEdo(a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w; val h = g.h
    if (h - g.suelo < 1f) return
    val r = Rect(0f, g.suelo, w, h)
    val tramos = if (a.lumbre) 12 else 1
    for (t in 0 until tramos) {
        val xa = w * t / tramos; val xb = w * (t + 1) / tramos + 0.6f
        val p = Offset((xa + xb) / 2, g.suelo + (h - g.suelo) * 0.5f)
        val k = if (a.lumbre) g.cerca(p) else 0.6f
        drawRect(a.luz(EdoTinta.SUELO, k, 0f, g.brasaEn(p) / (k + 0.001f)), Offset(xa, g.suelo), Size(xb - xa, h - g.suelo))
    }
    if (!a.oscuro) drawRect(Brush.verticalGradient(listOf(sombraMueble(0.25f), Color.Transparent, luzMueble(0.06f)), g.suelo, h), r.topLeft, r.size)
    // Las juntas de las tablas, que vienen del rincón y se abren hacia delante.
    val fuga = Offset(w * 0.5f, g.suelo - w * 1.6f)
    for (j in -6..6) {
        val xb = w * 0.5f + j * w * 0.13f
        val t0 = (g.suelo - fuga.y) / (h - fuga.y)
        val xa = fuga.x + (xb - fuga.x) * t0
        drawLine(a.sombra(0.45f), Offset(xa, g.suelo), Offset(xb, h), 1f * u)
        drawLine(a.claro(0.18f), Offset(xa + 1f * u, g.suelo), Offset(xb + 1.2f * u, h), 0.6f * u)
    }
    clipRect(0f, g.suelo, w, h) { vetas(r, a.sombra(0.22f), 3051, densidad = 0.7f, vertical = true, grosor = 0.6f, ondulacion = 0.5f) }
    // El zócalo de la pared, donde se junta con el suelo.
    drawRect(a.luz(SUGI, 0.2f), Offset(0f, g.suelo - 2.4f * u), Size(w, 2.4f * u))
    // La sombra del estante y de su cuerpo bajo en el suelo: de día, hacia la derecha y abajo; de noche, lejos del farol.
    val corre = if (a.oscuro) -w * 0.06f else w * 0.09f
    drawPath(poligonoEdo(g.posteIzq, g.pisan, g.posteDer, g.pisan, g.posteDer + corre, g.pisan + w * 0.07f, g.posteIzq + corre, g.pisan + w * 0.07f), a.sombra(if (a.oscuro) 0.4f else 0.26f))
    drawRect(a.sombra(if (a.oscuro) 0.55f else 0.4f), Offset(g.posteIzq, g.pisan - 1f * u), Size(g.posteDer - g.posteIzq, w * 0.012f))
    // El brillo de la cera donde da la luz del cuarto: el reflejo largo de los postes.
    if (!a.oscuro) {
        drawRect(luzMueble(0.1f), Offset(g.posteIzq + 2f * u, g.pisan + w * 0.02f), Size(g.x0 - g.posteIzq - 4f * u, (h - g.pisan) * 0.6f))
        mancha(Offset(w * 0.22f, g.pisan + (h - g.pisan) * 0.6f), w * 0.28f, luzMueble(0.12f))
    }
}

/**
 * La sombra de algo en la pared: [silueta] lo pinta del color que se le da. De día la luz es rasante y la sombra se corre larga
 * a la derecha y abajo; de noche huye del farol, tanto más marcada cuanto más cerca está de él.
 */
internal inline fun DrawScope.sombraParedEdo(a: AireEdo, g: MarcoEdo, desde: Offset, alfa: Float, crossinline silueta: DrawScope.(Color) -> Unit) {
    val w = g.w
    if (!a.oscuro) {
        translate(w * 0.045f, w * 0.022f) { silueta(sombraMueble(alfa * 0.55f)) }
        translate(w * 0.02f, w * 0.01f) { silueta(sombraMueble(alfa)) }
    } else {
        val f = Offset(desde.x / w - MuebleEstanteEdo.LAMP_X, desde.y / w - MuebleEstanteEdo.LAMP_Y)
        val d = f.getDistance()
        val k = farolEdo(desde.x / w, desde.y / w, a.lunar)
        if (k > 0.08f && d > 0.04f) {
            val tibia = mezcla(a.hondo, Color(0xFF4A2A1A), 0.5f)
            val al = (1.6f * alfa * k).coerceAtMost(0.32f)
            translate(w * 0.024f * f.x / d, w * 0.016f * f.y / d) { silueta(tibia.copy(alpha = al * 0.45f)) }
            translate(w * 0.013f * f.x / d, w * 0.009f * f.y / d) { silueta(tibia.copy(alpha = al * 0.55f)) }
        }
    }
}

/** Una mancha de contacto bajo lo que pisa una tabla o el suelo, corrida a la derecha de día y lejos del farol de noche. */
internal fun DrawScope.contactoEdo(a: AireEdo, centro: Offset, ancho: Float, alto: Float, alfa: Float) {
    val dx = if (a.oscuro) (if (centro.x / size.width < MuebleEstanteEdo.LAMP_X) -0.15f else 0.15f) else 0.15f
    drawOval(a.sombra(alfa), Offset(centro.x - ancho * (0.5f - dx), centro.y - alto / 2), Size(ancho, alto))
}

/** Una pieza de hierro con la forma [p]: su sombra corrida, el negro, el bisel claro arriba y a la izquierda y el filo de luz. */
internal fun DrawScope.chapaEdo(p: Path, a: AireEdo, k: Float) {
    val u = density
    translate(0.9f * u, 1.2f * u) { drawPath(p, a.sombra(0.4f)) }
    drawPath(p, a.luz(HIERRO, k))
    clipPath(p) { translate(0.8f * u, 0.9f * u) { drawPath(p, a.luz(mezcla(HIERRO, EdoTinta.HIERRO_LUZ, 0.22f), k)) } }
    drawPath(p, a.brillo(0.3f, k), style = Stroke(0.7f * u))
}

/** Un remache de hierro: su sombra, su cabeza y el punto de luz. */
internal fun DrawScope.remacheEdo(p: Offset, r: Float, a: AireEdo, k: Float) {
    drawCircle(a.sombra(0.4f), r * 1.1f, p + Offset(r * 0.45f, r * 0.5f))
    drawCircle(a.luz(mezcla(HIERRO, EdoTinta.HIERRO_LUZ, 0.3f), k), r, p)
    drawCircle(a.brillo(0.55f, k), r * 0.42f, p - Offset(r * 0.3f, r * 0.32f))
}
