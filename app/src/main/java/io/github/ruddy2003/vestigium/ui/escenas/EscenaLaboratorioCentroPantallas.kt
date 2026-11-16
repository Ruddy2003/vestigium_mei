package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.sin

/**
 * La pared de pantallas de la Vigía, en el comedor grande (`composicion_laboratorio_centro.md`, «Enfrente»): un tramo
 * entero de la pared curva, de dos pisos de alto, hecho de muchas pantallas viejas con sus juntas, con **las ruinas de
 * arriba en directo, a su hora de verdad** («Casi nadie las mira. Los niños, sí»). Es el cielo de este cuadro (regla 2):
 * cambia con la hora de arriba, con las nubes del día, con la lluvia y el sol de la subida, y tiñe el hall.
 *
 * - **La imagen grande**, de muchas pantallas: el cielo de arriba a su hora y las ruinas en tres filas, como la pantalla del
 *   laboratorio (`PantallaLab`), pero en la pared curva y en grande. Arriba, en ella, el campamento de la doctora.
 * - **Las pantallas sueltas** de los lados y de abajo: otras cámaras (una calle de escombros, una torre, la caseta del
 *   destello), alguna apagada o de otro tono.
 * - **El humo** del 8 de abril de la temporada 1, «en una de arriba a la derecha»; **la mancha verde** desde `verde`, «en una
 *   de abajo», que crece con `superficie`.
 */
internal object PantallasCentro {
    private val C = ColorLaboratorio

    /** Las pantallas: columnas y filas. */
    const val COLUMNAS = 10
    const val FILAS = 6

    /** La pantalla de abajo con la mancha verde (columna, fila) y la de arriba a la derecha con el humo. */
    const val VERDE_COL = 4
    const val HUMO_COL = 8
    const val HUMO_FILA = 1

    /** Un punto de la pared: [u] de 0 (su borde izquierdo en el cuadro) a 1 (el derecho), [w] de 0 (arriba) a 1 (abajo). */
    fun enLaPared(u: Float, w: Float, metido: Float = 0.03f): V3 =
        GeoCentro.aro(GeoCentro.RW - metido, GeoCentro.PY1 + (GeoCentro.PY0 - GeoCentro.PY1) * w, GeoCentro.PA1 + (GeoCentro.PA0 - GeoCentro.PA1) * u)

    fun q(u: Float, w: Float): Offset = CamCentro.p(enLaPared(u, w))

    /** Cuántas unidades del lienzo mide una unidad de [u] en [u]. */
    fun k(u: Float): Float = CamCentro.k(enLaPared(u, 0.5f)) * GeoCentro.RW * GeoCentro.rad(GeoCentro.PA1 - GeoCentro.PA0)

    private fun forma(ps: List<Pair<Float, Float>>): Path = poligono(ps.map { q(it.first, it.second) })

    /** Un rectángulo de la pared, de ([u0], [w0]) a ([u1], [w1]), con los lados curvos (en [n] pasos). */
    fun rect(u0: Float, w0: Float, u1: Float, w1: Float, n: Int = 6): Path =
        poligono((0..n).map { q(u0 + (u1 - u0) * it / n, w0) } + (0..n).map { q(u1 - (u1 - u0) * it / n, w1) })

    /** La pantalla de la columna [c] y la fila [f], sin su junta. */
    fun pantalla(c: Int, f: Int, junta: Float = 0.006f): Path = rect(c / COLUMNAS.toFloat() + junta, f / FILAS.toFloat() + junta * 1.4f, (c + 1) / COLUMNAS.toFloat() - junta, (f + 1) / FILAS.toFloat() - junta * 1.4f, 2)

    /** El trozo de la imagen grande: las columnas 1 a 8, las filas 0 a 4. */
    private const val U0 = 1f / COLUMNAS
    private const val U1 = 9f / COLUMNAS
    private const val W0 = 0f
    private const val W1 = 5f / FILAS

    /** Lo que ve una cámara: el punto ([s], [t]) de su imagen (de 0 a 1) en el lienzo, y cuánto mide en él una unidad de [s]. */
    class Camara(val u0: Float, val w0: Float, val u1: Float, val w1: Float) {
        fun q(s: Float, t: Float): Offset = PantallasCentro.q(u0 + (u1 - u0) * s, w0 + (w1 - w0) * t)
        fun k(s: Float): Float = PantallasCentro.k(u0 + (u1 - u0) * s) * (u1 - u0)
        fun forma(ps: List<Pair<Float, Float>>): Path = poligono(ps.map { q(it.first, it.second) })
    }

    fun DrawScope.pared(v: VistaCentro) {
        val img = PantallaLab.imagen(v.m)
        val todo = rect(0f, 0f, 1f, 1f, 10)
        // El fondo: el marco de metal viejo de toda la pared y las juntas, oscuras.
        drawPath(rect(-0.012f, -0.02f, 1.012f, 1.03f, 10), v.c(enLaPared(0.5f, 1f), lerp(C.ACERO, C.NEGRO, 0.35f), V3(-1f, 0f, -0.2f)))
        drawPath(todo, lerp(C.NEGRO, C.SOMBRA, 0.2f))
        if (!img.encendida) { apagada(v); return }
        val grande = Camara(U0, W0, U1, W1)
        clipPath(rect(U0, W0, U1, W1, 8)) {
            cielo(grande, img, 0.5f)
            ruinas(grande, img, 0.5f)
            if (img.arriba) campamento(grande, img, 0.5f)
            if (img.humo) humo(grande, img)
        }
        sueltas(v, img)
        juntas(v)
        efectos(v, img)
    }

    /** Apagadas (el tercer día de las jornadas): el vidrio negro de cada pantalla, con el reflejo del hall. */
    private fun DrawScope.apagada(v: VistaCentro) {
        for (c in 0 until COLUMNAS) for (f in 0 until FILAS) drawPath(pantalla(c, f), lerp(C.NEGRO, C.SOMBRA, 0.15f + 0.04f * ((c * 3 + f) % 4)))
        drawPath(forma(listOf(0.2f to 0f, 0.4f to 0f, 0.7f to 1f, 0.5f to 1f)), v.c(enLaPared(0.5f, 0.5f), C.HORMIGON, V3(-1f, 0f, 0f)).copy(alpha = 0.1f))
        juntas(v)
    }

    // ---------------------------------------------------------------- El cielo de arriba

    private fun DrawScope.cielo(cam: Camara, img: PantallaLab.Imagen, h: Float) {
        val l = img.l
        for (i in 0 until 8) {
            val s0 = i / 8f - 0.01f; val s1 = (i + 1) / 8f + 0.01f
            val a = cam.q(s0, 0f); val b = cam.q(s1, 0f); val c = cam.q(s1, h + 0.02f); val d = cam.q(s0, h + 0.02f)
            drawPath(poligono(a, b, c, d), Brush.verticalGradient(0f to l.alto, 0.55f to l.medio, 1f to l.bajo, startY = (a.y + b.y) / 2f, endY = (c.y + d.y) / 2f))
        }
        val az = Azar(Semilla.de(img.semilla, "centro", "cielo"))
        if (l.estrellas > 0.05f) {
            repeat(70) { val s = az.real(); val t = az.entre(0.02f, h - 0.04f); val b = az.real()
                drawCircle(Color(0xFFF2F0E6).copy(alpha = (l.estrellas * (0.3f + 0.7f * b) * (1f - t / h * 0.6f)).coerceIn(0f, 1f)), 0.3f + 0.5f * b * b, cam.q(s, t)) }
            val p = cam.q(0.3f + 0.4f * (1f - l.lunaX), (h * (1f - l.lunaAltura * 0.9f)).coerceIn(0.08f, h - 0.08f)); val r = 0.025f * cam.k(0.5f)
            drawCircle(Brush.radialGradient(listOf(Color(0xFFDCE6FF).copy(alpha = 0.35f * l.estrellas), Color.Transparent), p, r * 5f), r * 5f, p)
            drawCircle(Color(0xFFF4EEDC).copy(alpha = l.estrellas), r, p)
        }
        // El sol: su claridad desde fuera del encuadre; el disco, solo arriba desde el día del sol.
        if (l.solAltura > -0.2f && l.solFuerza > 0.02f) {
            val sv = h * (1f - l.solAltura.coerceIn(-0.2f, 1.6f) * 0.8f)
            val p = cam.q(l.solX.coerceIn(-0.2f, 1.2f), sv.coerceIn(-0.4f, h))
            val bajo = (1f - l.solAltura / 0.45f).coerceIn(0f, 1f); val r = cam.k(0.5f) * (0.4f + 0.25f * bajo)
            drawCircle(Brush.radialGradient(listOf(l.solColor.copy(alpha = 0.55f), l.solColor.copy(alpha = 0.16f), Color.Transparent), p, r), r, p, blendMode = BlendMode.Screen)
            if (img.sol && sv > -0.05f) drawCircle(lerp(l.solColor, Color.White, 0.7f), 0.03f * cam.k(0.5f), p)
        }
        // Las nubes del día, en pisos: la luz del lado del sol y la base en sombra.
        val lluvia = img.clima == Clima.LLUVIA || img.clima == Clima.TORMENTA
        val nubes = if (lluvia) 11 else if (img.clima == Clima.NUBES) 8 else 3 + az.entero(5)
        val luz = lerp(lerp(Color.White, Color(0xFF9AA0AA), if (lluvia) 0.7f else 0f), l.solColor, if (l.solAltura in 0f..0.5f) 0.45f else 0.1f).let { val d = 0.35f + 0.65f * (1f - l.noche); Color(it.red * d, it.green * d, it.blue * (0.4f + 0.6f * (1f - l.noche))) }
        val sombra = lerp(lerp(l.medio, Color(0xFF8A86B0), 0.5f), Color(0xFF59616F), if (lluvia) 0.7f else 0f)
        repeat(nubes) {
            val s = az.entre(-0.1f, 1.05f); val t = az.entre(0.06f, h - 0.1f); val w = az.entre(0.08f, 0.2f) * (if (lluvia) 1.6f else 1f)
            val kk = cam.k(s.coerceIn(0f, 1f))
            for (j in 0..5) { val ds = (j - 2.5f) / 5f * w; val alto = (1f - abs(ds) / w * 1.6f).coerceAtLeast(0.25f)
                val c = cam.q(s + ds, t - 0.02f * alto)
                mancha(c, w * kk * 0.26f, w * kk * 0.12f * alto, luz, 0.85f)
                mancha(c + Offset(0f, w * kk * 0.07f), w * kk * 0.26f, w * kk * 0.06f, sombra, 0.6f) }
        }
        if (lluvia) drawPath(cam.forma(listOf(0f to 0f, 1f to 0f, 1f to h, 0f to h)), lerp(l.medio, Color(0xFF8A90A0), 0.5f).copy(alpha = 0.35f))
    }

    // ---------------------------------------------------------------- Las ruinas

    /** La luz de arriba en la piedra: [cara] 1 al sol, 0 de frente, −1 a la sombra; [lejos] la lleva a la bruma. */
    private fun luz(l: Luces, c: Color, lejos: Float, cara: Int): Color {
        val dia = (1f - l.noche).coerceIn(0f, 1f)
        val base = Color(c.red * l.ambiente.red * 0.86f, c.green * l.ambiente.green * 0.86f, c.blue * l.ambiente.blue * 0.86f)
        val col = when {
            cara > 0 -> lerp(base, Color((base.red * l.solColor.red * 1.3f).coerceIn(0f, 1f), (base.green * l.solColor.green * 1.25f).coerceIn(0f, 1f), (base.blue * l.solColor.blue * 1.1f).coerceIn(0f, 1f)), 0.7f * dia)
            cara < 0 -> lerp(base, Color(base.red * 0.52f, base.green * 0.56f, base.blue * 0.74f), 0.4f + 0.5f * dia)
            else -> base
        }
        return lerp(col, l.bruma, (lejos * l.brumaFuerza).coerceIn(0f, 0.92f))
    }

    private fun torre(s0: Float, s1: Float, base: Float, arriba: Float, az: Azar): List<Pair<Float, Float>> {
        val ps = ArrayList<Pair<Float, Float>>()
        ps += s0 to base; ps += s0 to arriba + az.entre(0f, 0.05f)
        val dientes = 3 + az.entero(3)
        for (i in 1 until dientes) ps += (s0 + (s1 - s0) * i / dientes) to (arriba + az.entre(-0.03f, 0.08f))
        ps += s1 to arriba + az.entre(0.02f, 0.12f); ps += s1 to base
        return ps
    }

    private fun DrawScope.ruinas(cam: Camara, img: PantallaLab.Imagen, h: Float) {
        val l = img.l; val lado = if (l.solX > 0.5f) 1 else -1
        val az = Azar(Semilla.de("laboratorio", "centro", "ruinas"))
        val piedra = Color(0xFF8E887E); val oscura = Color(0xFF55524D); val hueco = Color(0xFF26262A)
        drawPath(cam.forma(listOf(-0.02f to h - 0.01f, 1.02f to h - 0.01f, 1.02f to 1.02f, -0.02f to 1.02f)),
            Brush.verticalGradient(0f to luz(l, piedra, 0.85f, 0), 0.3f to luz(l, piedra, 0.45f, 0), 1f to luz(l, lerp(oscura, C.TIERRA, 0.4f), 0f, 0), startY = cam.q(0.5f, h).y, endY = cam.q(0.5f, 1f).y))
        // La fila de lejos: torres partidas, pálidas en la bruma.
        var s = -0.04f
        while (s < 1.05f) { val w = az.entre(0.018f, 0.045f); val alto = az.entre(0.05f, 0.16f)
            drawPath(cam.forma(torre(s, s + w, h + 0.01f, h - alto, az)), luz(l, piedra, 0.72f, if (az.real() > 0.5f) lado else -lado)); s += w + az.entre(0.003f, 0.025f) }
        for (i in 0 until 7) mancha(cam.q(i / 6f, h - 0.015f), cam.k(i / 6f) * 0.2f, cam.k(i / 6f) * 0.025f, l.bruma, 0.55f)
        if (img.verde > 0.0005f) verde(cam, img, h + 0.005f, h + 0.05f, img.verde.coerceAtLeast(0.03f), 0.6f)
        // La fila del medio: torres en tres cuartos, con sus pisos y sus huecos; una con el árbol torcido encima.
        val torres = listOf(Triple(0.02f, 0.07f, 0.3f), Triple(0.14f, 0.05f, 0.17f), Triple(0.25f, 0.08f, 0.36f), Triple(0.41f, 0.06f, 0.22f), Triple(0.55f, 0.08f, 0.32f), Triple(0.7f, 0.05f, 0.19f), Triple(0.8f, 0.09f, 0.34f), Triple(0.95f, 0.06f, 0.2f))
        for ((i, tt) in torres.withIndex()) {
            val (s0, w, alto) = tt; val base = h + 0.06f + 0.012f * (i % 2); val top = base - alto
            val ps = torre(s0, s0 + w, base, top, az)
            drawPath(cam.forma(ps), luz(l, piedra, 0.32f, 0))
            val der = s0 + w / 2f < 0.5f; val sg = if (der) 1 else -1
            val x0 = if (der) s0 + w else s0; val x1 = x0 + sg * w * 0.32f
            val cima = ps[if (der) ps.size - 2 else 1].second
            drawPath(cam.forma(listOf(x0 to base, x0 to cima, x1 to cima + 0.025f, x1 to base - 0.01f)), luz(l, piedra, 0.32f, if (sg == lado) 1 else -1))
            var f = base - 0.03f
            while (f > top + 0.04f) {
                for (c in 0 until 4) { if (az.real() < 0.2f) continue; val wu = s0 + w * (0.08f + c * 0.23f)
                    drawPath(cam.forma(listOf(wu to f - 0.022f, wu + w * 0.13f to f - 0.022f, wu + w * 0.13f to f - 0.006f, wu to f - 0.006f)), luz(l, hueco, 0.3f, 0)) }
                f -= 0.032f
            }
            if (i == 2) arbol(cam, l, s0 + w * 0.55f, top + 0.02f)
        }
        // El campo de escombros del medio, y los muros rotos de cerca con la caseta del destello.
        for (fila in 0..3) { val vv = h + 0.1f + fila * 0.07f; val lj = 0.3f - fila * 0.08f; val t = 0.006f + fila * 0.005f
            repeat(12 + fila * 3) { val uu = az.entre(-0.02f, 1.02f); val hh = t * az.entre(0.6f, 1.6f); val ww = t * az.entre(1.2f, 3f)
                drawPath(cam.forma(listOf(uu to vv, uu + ww to vv, uu + ww * 0.85f to vv - hh, uu + ww * 0.1f to vv - hh * 0.8f)), luz(l, piedra, lj, 0))
                drawPath(cam.forma(listOf(uu + ww * 0.85f to vv - hh, uu + ww to vv, uu + ww + t * 0.6f to vv - 0.002f, uu + ww * 0.9f + t * 0.5f to vv - hh * 0.9f)), luz(l, piedra, lj, lado)) } }
        for ((i, s0) in listOf(0.05f, 0.62f, 0.85f).withIndex()) {
            val w = 0.1f + 0.03f * i; val base = h + 0.25f; val alto = 0.08f + 0.03f * (i % 2)
            drawPath(cam.forma(listOf(s0 to base, s0 to base - alto * 0.7f, s0 + w * 0.3f to base - alto, s0 + w * 0.55f to base - alto * 0.85f, s0 + w * 0.7f to base - alto * 0.4f, s0 + w to base - alto * 0.3f, s0 + w to base)), luz(l, piedra, 0.12f, if (i == 1) lado else 0))
            drawPath(cam.forma(listOf(s0 + w * 0.3f to base - alto * 0.6f, s0 + w * 0.45f to base - alto * 0.6f, s0 + w * 0.45f to base - alto * 0.25f, s0 + w * 0.3f to base - alto * 0.25f)), luz(l, hueco, 0.1f, 0))
        }
        val cu = 0.4f; val cv = h + 0.27f; val caseta = lerp(C.HORMIGON, C.TIERRA, 0.45f)
        drawPath(cam.forma(listOf(cu to cv, cu + 0.1f to cv, cu + 0.1f to cv - 0.1f, cu to cv - 0.1f)), luz(l, caseta, 0.1f, 0))
        drawPath(cam.forma(listOf(cu + 0.1f to cv, cu + 0.13f to cv - 0.012f, cu + 0.13f to cv - 0.11f, cu + 0.1f to cv - 0.1f)), luz(l, caseta, 0.1f, lado))
        drawPath(cam.forma(listOf(cu + 0.03f to cv, cu + 0.06f to cv, cu + 0.06f to cv - 0.06f, cu + 0.03f to cv - 0.06f)), luz(l, hueco, 0.08f, 0))
        if (img.verde > 0.15f) verde(cam, img, h + 0.12f, h + 0.28f, (img.verde - 0.15f) * 2f, 0.25f)
        // Delante: montones de escombro, losas partidas, hierros torcidos y la hierba rala y amarilla.
        for ((i, s0) in listOf(-0.06f, 0.28f, 0.62f).withIndex()) {
            val w = 0.34f + 0.05f * i; val cima = 0.78f + 0.05f * i
            drawPath(cam.forma(listOf(s0 to 1.02f, s0 + w * 0.12f to cima + 0.07f, s0 + w * 0.35f to cima + 0.01f, s0 + w * 0.5f to cima, s0 + w * 0.62f to cima + 0.03f, s0 + w * 0.85f to cima + 0.06f, s0 + w to 1.02f)), luz(l, lerp(oscura, piedra, 0.5f), 0f, 0))
            val lu = s0 + w * 0.42f; val lv = cima + 0.03f
            drawPath(cam.forma(listOf(lu to lv + 0.05f, lu + 0.08f to lv + 0.02f, lu + 0.09f to lv - 0.02f, lu + 0.01f to lv)), luz(l, piedra, 0f, lado))
            val hu = s0 + w * 0.68f; val k0 = cam.k(hu)
            drawPath(Path().apply { val a = cam.q(hu, cima + 0.04f); moveTo(a.x, a.y); val b = cam.q(hu + 0.01f, cima - 0.06f); lineTo(b.x, b.y); val c = cam.q(hu + 0.05f, cima - 0.09f); lineTo(c.x, c.y) },
                luz(l, C.HIERRO, 0f, 1), style = Stroke(0.003f * k0 + 0.4f, cap = StrokeCap.Round))
            repeat(5) { val c2 = az.entre(s0, s0 + w); val v2 = az.entre(cima + 0.04f, 1f)
                repeat(4) { j -> val p = cam.q(c2 + j * 0.004f, v2); val kk = cam.k(c2); drawLine(luz(l, C.PAJA, 0f, 1), p, p + Offset((j - 2) * 0.004f * kk, -az.entre(0.012f, 0.03f) * kk), 0.4f) } }
        }
        if (img.verde > 0.3f) verde(cam, img, 0.84f, 1f, (img.verde - 0.3f) * 3f, 0f)
    }

    private fun DrawScope.arbol(cam: Camara, l: Luces, s: Float, t: Float) {
        val kk = cam.k(s)
        val tronco = Color(0xFF4A3A2E).let { Color(it.red * l.ambiente.red, it.green * l.ambiente.green, it.blue * l.ambiente.blue) }
        val a = cam.q(s, t); val b = cam.q(s + 0.03f, t - 0.06f); val c = cam.q(s + 0.06f, t - 0.08f)
        drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x, a.y - 0.03f * kk, b.x, b.y, c.x, c.y) }, tronco, style = Stroke(0.008f * kk + 0.4f, cap = StrokeCap.Round))
        val hoja = lerp(lerp(C.HOJA_OSC, C.PAJA, 0.3f).let { Color(it.red * l.ambiente.red, it.green * l.ambiente.green, it.blue * l.ambiente.blue) }, l.bruma, 0.2f)
        for ((ds, dt, r) in listOf(Triple(0.06f, -0.085f, 0.03f), Triple(0.085f, -0.07f, 0.024f), Triple(0.035f, -0.095f, 0.022f), Triple(0.07f, -0.105f, 0.02f))) mancha(cam.q(s + ds, t + dt), r * kk, r * kk * 0.75f, hoja, 0.97f)
    }

    /** La grama que ven las cámaras: manchas verdes entre las piedras, de [t0] a [t1] de alto, tantas como diga [cuanto]. */
    private fun DrawScope.verde(cam: Camara, img: PantallaLab.Imagen, t0: Float, t1: Float, cuanto: Float, lejos: Float) {
        val l = img.l; val k0 = cuanto.coerceIn(0f, 1f)
        val vivo = lerp(C.GRAMA, Color(0xFF9CD45C), 0.35f)
        val col = lerp(Color(vivo.red * l.ambiente.red, vivo.green * l.ambiente.green, vivo.blue * l.ambiente.blue), l.bruma, lejos * 0.4f)
        val oscuro = lerp(col, Color(C.HOJA_OSC.red * l.ambiente.red, C.HOJA_OSC.green * l.ambiente.green, C.HOJA_OSC.blue * l.ambiente.blue), 0.6f)
        val az = Azar(Semilla.de("laboratorio", "centro", "verde", (t0 * 100).toInt()))
        repeat((5 + 30 * k0).toInt()) { val s = az.entre(0.02f, 0.2f + 0.78f * k0); val t = az.entre(t0, t1); val kk = cam.k(s); val w = az.entre(0.02f, 0.05f) * kk; val hh = az.entre(0.008f, 0.018f) * kk
            mancha(cam.q(s, t) + Offset(0f, hh * 0.4f), w, hh, oscuro, 0.85f); mancha(cam.q(s, t), w * 0.85f, hh * 0.8f, col, 0.95f) }
    }

    /** Arriba: el campamento de la doctora junto a la caseta: la tienda, la caja, los surcos de grama y el humo del hornillo. */
    private fun DrawScope.campamento(cam: Camara, img: PantallaLab.Imagen, h: Float) {
        val l = img.l
        fun t(c: Color) = Color(c.red * l.ambiente.red, c.green * l.ambiente.green, c.blue * l.ambiente.blue)
        val tu = 0.56f; val tv = h + 0.23f
        drawPath(cam.forma(listOf(tu to tv, tu + 0.07f to tv - 0.09f, tu + 0.14f to tv)), t(lerp(C.TELA, C.TIERRA, 0.35f)))
        drawPath(cam.forma(listOf(tu + 0.07f to tv - 0.09f, tu + 0.14f to tv, tu + 0.17f to tv - 0.01f, tu + 0.1f to tv - 0.095f)), t(lerp(C.TELA, C.TIERRA, 0.55f)))
        drawPath(cam.forma(listOf(tu + 0.06f to tv, tu + 0.07f to tv - 0.05f, tu + 0.08f to tv)), t(C.NEGRO))
        drawPath(cam.forma(listOf(tu + 0.2f to tv, tu + 0.25f to tv, tu + 0.25f to tv - 0.03f, tu + 0.2f to tv - 0.03f)), t(C.MADERA))
        for (i in 0..4) { val s0 = 0.16f + i * 0.13f; val vv = 0.88f + 0.02f * (i % 2)
            drawPath(cam.forma(listOf(s0 to vv, s0 + 0.08f to vv - 0.01f, s0 + 0.08f to vv + 0.012f, s0 to vv + 0.02f)), t(lerp(C.TIERRA, C.NEGRO, 0.2f)))
            for (j in 0..4) mancha(cam.q(s0 + 0.01f + j * 0.016f, vv - 0.004f), 0.007f * cam.k(s0), 0.005f * cam.k(s0), t(C.GRAMA), 0.9f) }
        for (j in 0..6) mancha(cam.q(tu + 0.23f + j * 0.007f, tv - 0.02f - j * 0.035f), (0.01f + j * 0.004f) * cam.k(tu), (0.008f + j * 0.003f) * cam.k(tu), lerp(t(C.TELA), l.medio, 0.4f), 0.4f - j * 0.04f)
    }

    /** El humo de las ruinas, «una columna fina y gris que sale de entre las ruinas, lejos, y se dobla con el viento», en la pantalla de arriba a la derecha. */
    private fun DrawScope.humo(cam: Camara, img: PantallaLab.Imagen) {
        // La pantalla del humo es la (HUMO_COL, HUMO_FILA): en la imagen grande, su centro.
        val s = ((HUMO_COL + 0.3f) / COLUMNAS - U0) / (U1 - U0); val base = 0.53f
        for (j in 0..15) { val t = base - j * 0.024f; val ss = s - j * 0.002f + j * j * 0.0011f
            val gris = lerp(Color(0xFF3E3C3A), img.l.medio, 0.05f + 0.035f * j)
            mancha(cam.q(ss, t), (0.008f + j * 0.0035f) * cam.k(ss), (0.012f + j * 0.0025f) * cam.k(ss), gris, 0.95f - j * 0.04f) }
    }

    // ---------------------------------------------------------------- Las pantallas sueltas

    /**
     * Las pantallas de los lados y de abajo, cada una con su cámara: una calle de escombros, una torre de cerca, la caseta
     * del destello, el cielo solo; alguna apagada, alguna de otro tono (más vieja). Abajo, la de la mancha verde.
     */
    private fun DrawScope.sueltas(v: VistaCentro, img: PantallaLab.Imagen) {
        val l = img.l
        val az = Azar(Semilla.de("laboratorio", "centro", "sueltas"))
        val celdas = (0 until FILAS).flatMap { f -> listOf(0 to f, COLUMNAS - 1 to f) } + (1 until COLUMNAS - 1).map { it to FILAS - 1 }
        for ((c, f) in celdas) {
            val forma = pantalla(c, f, 0f)
            val tipo = (c * 7 + f * 3) % 6
            val cam = Camara(c / COLUMNAS.toFloat(), f / FILAS.toFloat(), (c + 1) / COLUMNAS.toFloat(), (f + 1) / FILAS.toFloat())
            clipPath(forma) {
                if (c == VERDE_COL && f == FILAS - 1) { calle(cam, l, az); verdeDeCerca(cam, img); return@clipPath }
                when (tipo) {
                    0 -> drawPath(forma, lerp(C.NEGRO, C.SOMBRA, 0.12f))                     // apagada
                    1, 4 -> calle(cam, l, az)
                    2 -> torreDeCerca(cam, l)
                    3 -> { drawPath(forma, Brush.verticalGradient(listOf(l.alto, l.medio, l.bajo), startY = cam.q(0.5f, 0f).y, endY = cam.q(0.5f, 1f).y)); drawPath(cam.forma(listOf(0f to 0.8f, 0.3f to 0.72f, 0.5f to 0.78f, 0.8f to 0.7f, 1f to 0.76f, 1f to 1f, 0f to 1f)), luz(l, Color(0xFF6E6A62), 0.3f, 0)) }
                    else -> casetaDeCerca(cam, l)
                }
            }
        }
    }

    /** Una calle de escombros vista de cerca: el suelo, dos muros rotos en fuga y un hierro. */
    private fun DrawScope.calle(cam: Camara, l: Luces, az: Azar) {
        val piedra = Color(0xFF8E887E); val lado = if (l.solX > 0.5f) 1 else -1
        drawPath(cam.forma(listOf(0f to 0f, 1f to 0f, 1f to 0.45f, 0f to 0.45f)), Brush.verticalGradient(listOf(l.alto, l.bajo), startY = cam.q(0.5f, 0f).y, endY = cam.q(0.5f, 0.45f).y))
        drawPath(cam.forma(listOf(0f to 0.45f, 1f to 0.45f, 1f to 1f, 0f to 1f)), luz(l, lerp(piedra, C.TIERRA, 0.4f), 0.1f, 0))
        val x = az.entre(0.3f, 0.6f)
        drawPath(cam.forma(listOf(0f to 0.05f, x - 0.1f to 0.38f, x - 0.1f to 0.5f, 0f to 1f)), luz(l, piedra, 0.05f, lado))
        drawPath(cam.forma(listOf(1f to 0.15f, x + 0.12f to 0.4f, x + 0.12f to 0.5f, 1f to 0.95f)), luz(l, piedra, 0.05f, -lado))
        repeat(5) { val s = az.entre(0.2f, 0.8f); val t = az.entre(0.6f, 0.95f); mancha(cam.q(s, t), cam.k(s) * 0.06f, cam.k(s) * 0.025f, luz(l, piedra, 0f, lado), 0.9f) }
    }

    private fun DrawScope.torreDeCerca(cam: Camara, l: Luces) {
        val piedra = Color(0xFF8E887E); val lado = if (l.solX > 0.5f) 1 else -1
        drawPath(cam.forma(listOf(0f to 0f, 1f to 0f, 1f to 1f, 0f to 1f)), Brush.verticalGradient(listOf(l.alto, l.medio, l.bajo), startY = cam.q(0.5f, 0f).y, endY = cam.q(0.5f, 1f).y))
        drawPath(cam.forma(listOf(0.2f to 1f, 0.2f to 0.25f, 0.35f to 0.18f, 0.48f to 0.3f, 0.6f to 0.22f, 0.6f to 1f)), luz(l, piedra, 0.15f, 0))
        drawPath(cam.forma(listOf(0.6f to 1f, 0.6f to 0.22f, 0.72f to 0.27f, 0.72f to 1f)), luz(l, piedra, 0.15f, lado))
        for (f in 0..5) for (c in 0..2) drawPath(cam.forma(listOf(0.24f + c * 0.12f to 0.36f + f * 0.11f, 0.3f + c * 0.12f to 0.36f + f * 0.11f, 0.3f + c * 0.12f to 0.42f + f * 0.11f, 0.24f + c * 0.12f to 0.42f + f * 0.11f)), luz(l, Color(0xFF26262A), 0.1f, 0))
    }

    private fun DrawScope.casetaDeCerca(cam: Camara, l: Luces) {
        val lado = if (l.solX > 0.5f) 1 else -1
        drawPath(cam.forma(listOf(0f to 0f, 1f to 0f, 1f to 0.55f, 0f to 0.55f)), Brush.verticalGradient(listOf(l.medio, l.bajo), startY = cam.q(0.5f, 0f).y, endY = cam.q(0.5f, 0.55f).y))
        drawPath(cam.forma(listOf(0f to 0.55f, 1f to 0.55f, 1f to 1f, 0f to 1f)), luz(l, lerp(Color(0xFF8E887E), C.TIERRA, 0.5f), 0.05f, 0))
        val caseta = lerp(C.HORMIGON, C.TIERRA, 0.45f)
        drawPath(cam.forma(listOf(0.25f to 0.8f, 0.65f to 0.8f, 0.65f to 0.4f, 0.25f to 0.4f)), luz(l, caseta, 0f, 0))
        drawPath(cam.forma(listOf(0.65f to 0.8f, 0.78f to 0.74f, 0.78f to 0.36f, 0.65f to 0.4f)), luz(l, caseta, 0f, lado))
        drawPath(cam.forma(listOf(0.38f to 0.8f, 0.5f to 0.8f, 0.5f to 0.56f, 0.38f to 0.56f)), luz(l, Color(0xFF26262A), 0f, 0))
    }

    /** La mancha verde de cerca, en la pantalla de abajo: desde `verde`, crece con `superficie`. */
    private fun DrawScope.verdeDeCerca(cam: Camara, img: PantallaLab.Imagen) {
        if (img.verde <= 0.0005f) return
        val l = img.l; val k0 = (img.verde / 0.5f).coerceIn(0.08f, 1f)
        val col = lerp(C.GRAMA, Color(0xFF9CD45C), 0.3f).let { Color(it.red * l.ambiente.red, it.green * l.ambiente.green, it.blue * l.ambiente.blue) }
        val az = Azar(Semilla.de("laboratorio", "centro", "verde de cerca"))
        repeat((6 + 26 * k0).toInt()) { val s = 0.5f + az.entre(-0.45f, 0.45f) * k0; val t = 0.75f + az.entre(-0.18f, 0.2f) * (0.4f + 0.6f * k0)
            mancha(cam.q(s, t), cam.k(s) * az.entre(0.04f, 0.09f), cam.k(s) * az.entre(0.02f, 0.04f), if (az.real() > 0.4f) col else lerp(col, C.HOJA_OSC, 0.5f), 0.95f) }
    }

    // ---------------------------------------------------------------- Las juntas y el vidrio

    /** Las juntas entre pantallas: líneas oscuras, más gruesas cerca; y algunas pantallas de otro tono (más viejas o cansadas). */
    private fun DrawScope.juntas(v: VistaCentro) {
        val junta = lerp(C.NEGRO, C.ACERO, 0.15f)
        for (c in 0..COLUMNAS) { val u = c / COLUMNAS.toFloat(); drawLine(junta, q(u, 0f), q(u, 1f), (0.06f * k(u) / 11f).coerceIn(0.5f, 2.6f)) }
        for (f in 0..FILAS) { val w = f / FILAS.toFloat(); drawPath(Path().apply { val a = q(0f, w); moveTo(a.x, a.y); for (i in 1..10) { val b = q(i / 10f, w); lineTo(b.x, b.y) } }, junta, style = Stroke((0.05f * k(0.5f) / 11f).coerceIn(0.5f, 2.2f))) }
        // Cada pantalla con lo suyo: un poco más clara o más apagada que la de al lado, y el borde oscurecido (la viñeta del panel viejo).
        val az = Azar(Semilla.de("laboratorio", "centro", "paneles"))
        for (c in 0 until COLUMNAS) for (f in 0 until FILAS) {
            val forma = pantalla(c, f)
            val t = az.real()
            if (t < 0.3f) drawPath(forma, Color.Black.copy(alpha = 0.05f + 0.1f * t)) else if (t > 0.8f) drawPath(forma, Color.White.copy(alpha = 0.05f * (t - 0.8f) * 5f))
            drawPath(forma, Color.Black.copy(alpha = 0.22f), style = Stroke((0.06f * k(c / COLUMNAS.toFloat()) / 11f).coerceIn(0.5f, 2f)))
        }
        // Las pantallas cansadas: una más verde, una más magenta, una más oscura.
        for ((cf, tinte) in listOf((3 to 1) to Color(0xFF7FE0A0), (6 to 3) to Color(0xFFE08AD0), (2 to 4) to Color(0xFF101828), (7 to 0) to Color(0xFFFFE0A0))) {
            drawPath(pantalla(cf.first, cf.second), tinte.copy(alpha = if (tinte.red < 0.2f) 0.4f else 0.14f))
        }
    }

    /** Las líneas de barrido, la viñeta, el reflejo del hall en el vidrio y, el día del sol, el deslumbre. */
    private fun DrawScope.efectos(v: VistaCentro, img: PantallaLab.Imagen) {
        var w = 0f
        while (w < 1f) { drawPath(Path().apply { val a = q(0f, w); moveTo(a.x, a.y); val b = q(0.5f, w); lineTo(b.x, b.y); val c = q(1f, w); lineTo(c.x, c.y) }, Color.Black.copy(alpha = 0.05f), style = Stroke(0.35f)); w += 0.012f }
        drawPath(rect(0f, 0f, 1f, 1f, 10), Color(0xFF7FD8E0).copy(alpha = 0.04f))
        // El reflejo de la bóveda clara en la parte de arriba de la pared, y unas franjas al sesgo.
        drawPath(forma(listOf(0.15f to 0f, 0.3f to 0f, 0.18f to 1f, 0.05f to 1f)), Color.White.copy(alpha = 0.035f + 0.02f * (1f - v.noche)))
        drawPath(forma(listOf(0.62f to 0f, 0.68f to 0f, 0.58f to 1f, 0.53f to 1f)), Color.White.copy(alpha = 0.03f))
        if (EscenaLaboratorio.deslumbra(v.m)) {
            drawPath(rect(0f, 0f, 1f, 1f, 10), Color(0xFFFFF4D6).copy(alpha = 0.2f))
            val p = q(img.l.solX.coerceIn(0.1f, 0.8f), 0.12f)
            drawCircle(Brush.radialGradient(listOf(Color(0xFFFFFAEA).copy(alpha = 0.85f), Color(0xFFFFF0C8).copy(alpha = 0.35f), Color.Transparent), p, k(0.5f) * 0.6f), k(0.5f) * 0.6f, p)
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    /** Una nube que cruza la imagen grande, el humo que se dobla, la lluvia de arriba y una pantalla que parpadea. */
    fun DrawScope.vivo(v: VistaCentro, reloj: Float) {
        val img = PantallaLab.imagen(v.m); if (!img.encendida) return
        val cam = Camara(U0, W0, U1, W1)
        clipPath(rect(U0, W0, U1, W1, 8)) {
            val s = ((reloj * 0.004f + 0.3f) % 1.3f) - 0.15f
            mancha(cam.q(s, 0.2f), 0.05f * cam.k(s.coerceIn(0f, 1f)), 0.014f * cam.k(s.coerceIn(0f, 1f)), lerp(Color.White, img.l.medio, 0.4f + 0.4f * img.l.noche), 0.3f)
            if (img.humo) for (j in 0..4) { val f = ((reloj * 0.05f + j / 5f) % 1f); val ss = ((HUMO_COL + 0.4f) / COLUMNAS - U0) / (U1 - U0) + 0.12f * f * f
                mancha(cam.q(ss + 0.008f * sin(reloj + j), 0.5f - 0.3f * f), (0.008f + 0.02f * f) * cam.k(ss), (0.008f + 0.012f * f) * cam.k(ss), Color(0xFF8A8A88), 0.3f * (1f - f)) }
            if (img.clima == Clima.LLUVIA || img.clima == Clima.TORMENTA) {
                val az = Azar(Semilla.de("laboratorio", "centro", "lluvia"))
                repeat(40) { val uu = az.real(); val f = ((az.real() + reloj * 1.2f) % 1f); val p = cam.q(uu, f); drawLine(Color.White.copy(alpha = 0.28f), p, p + Offset(-0.3f, 2f), 0.35f) }
            }
        }
        // Una pantalla vieja que parpadea.
        val parpadeo = (sin(reloj * 9f) * sin(reloj * 2.3f)).coerceAtLeast(0f)
        if (parpadeo > 0.3f) drawPath(pantalla(COLUMNAS - 1, 2), Color.White.copy(alpha = 0.08f * parpadeo))
    }
}
