package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
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
import kotlin.math.sin

/**
 * La pantalla de la Vigía: la imagen de la cámara que mira las ruinas de arriba, «en su noche de verdad». Es el cielo del
 * cuadro (regla 2) y una imagen dentro de la imagen. Se pinta en el plano de la pared, en fuga: cada punto de la imagen
 * ([u] de 0, el borde lejano, a 1, el cercano; [v] de 0, arriba, a 1, abajo) va a su sitio con [q], así que las ruinas y
 * las nubes se ven en perspectiva como el marco.
 *
 * - **El cielo de arriba a su hora** ([EscenaLaboratorio.lucesDeArriba]): el alba dorada, el mediodía, la tarde y la noche
 *   con estrellas y luna; las nubes, del día. **[Aproximación]**: la hora de arriba es la de abajo, y el sol queda fuera
 *   del encuadre de la cámara (se ve su claridad), salvo arriba desde el día del sol.
 * - **Las ruinas** en tres filas: las torres partidas a lo lejos, en la bruma; las del medio, con sus huecos negros y el
 *   árbol torcido en lo alto de una torre caída; delante, los escombros, los hierros torcidos y la hierba rala y
 *   amarilla, y la caseta de hormigón que es la boca del destello.
 * - **Lo verde** ([EscenaLaboratorio.superficie]): desde el día `verde`, una mancha de grama lejos, que crece.
 * - **Arriba**, el campamento de la doctora junto a la caseta: la tienda, el humo del hornillo y los surcos de grama.
 * - **El humo** del 8 de abril de la temporada 1, una columna fina que se dobla con el viento.
 */
internal object PantallaLab {
    private val C = ColorLaboratorio

    /** El punto del lienzo del punto ([u], [v]) de la imagen. */
    fun q(u: Float, v: Float): Offset = CamLab.p(SalaLab.enLaPantalla(u, v))

    /** Cuántas unidades del lienzo mide una unidad de [u] (el ancho de la imagen) en [u]. */
    fun k(u: Float): Float = CamLab.k(SalaLab.enLaPantalla(u, 0.5f)) * (SalaLab.PZ_LEJOS - SalaLab.PZ_CERCA) * 0.42f

    /** Un camino cerrado de puntos de la imagen. */
    private fun forma(ps: List<Pair<Float, Float>>): Path = poligono(ps.map { q(it.first, it.second) })

    /** La línea del horizonte de la imagen. */
    private const val H = 0.52f

    /** Lo que se ve en la pantalla ahora: el cielo y sus luces, y si está encendida. */
    class Imagen(val l: Luces, val encendida: Boolean, val arriba: Boolean, val sol: Boolean, val verde: Float, val humo: Boolean, val clima: Clima, val semilla: Long)

    fun imagen(m: Momento): Imagen = Imagen(
        EscenaLaboratorio.lucesDeArriba(m), !EscenaLaboratorio.pantallaApagada(m), EscenaLaboratorio.arriba(m), EscenaLaboratorio.solEnLaPantalla(m),
        EscenaLaboratorio.superficie(m), EscenaLaboratorio.humo(m), EscenaLaboratorio.climaDeArriba(m), m.ficha.semilla,
    )

    /** La imagen entera, dentro del hueco del marco. */
    fun DrawScope.pantalla(v: VistaLab) {
        val img = imagen(v.m)
        clipPath(SalaLab.formaDeLaPantalla()) {
            if (!img.encendida) { apagada(v); return@clipPath }
            cielo(img)
            ruinas(img)
            if (img.arriba) campamento(img)
            efectos(v, img)
        }
    }

    /** Apagada: el vidrio negro, con el reflejo de la sala. */
    private fun DrawScope.apagada(v: VistaLab) {
        drawPath(SalaLab.formaDeLaPantalla(), lerp(C.NEGRO, C.SOMBRA, 0.25f))
        drawPath(forma(listOf(0.1f to 0f, 0.3f to 0f, 0.6f to 1f, 0.4f to 1f)), v.c(V3(SalaLab.XD - 0.1f, 2f, 3.5f), C.HORMIGON, PiezasLab.IZQ).copy(alpha = 0.12f))
    }

    // ---------------------------------------------------------------- El cielo

    private fun DrawScope.cielo(img: Imagen) {
        val l = img.l
        // En franjas verticales, para que el degradado siga la fuga del marco.
        val n = 8
        for (i in 0 until n) {
            val u0 = i / n.toFloat() - 0.01f; val u1 = (i + 1) / n.toFloat() + 0.01f
            val a = q(u0, 0f); val b = q(u1, 0f); val c = q(u1, H + 0.02f); val d = q(u0, H + 0.02f)
            val y0 = (a.y + b.y) / 2f; val y1 = (c.y + d.y) / 2f
            drawPath(poligono(a, b, c, d), Brush.verticalGradient(0f to l.alto, 0.55f to l.medio, 1f to l.bajo, startY = y0, endY = y1))
        }
        val az = Azar(Semilla.de(img.semilla, "pantalla", "cielo"))
        // Las estrellas y la luna.
        if (l.estrellas > 0.05f) {
            repeat(46) { val u = az.entre(0f, 1f); val vv = az.entre(0.02f, H - 0.05f); val b = az.real()
                drawCircle(Color(0xFFF2F0E6).copy(alpha = (l.estrellas * (0.3f + 0.7f * b) * (1f - vv / H * 0.6f)).coerceIn(0f, 1f)), 0.3f + 0.55f * b * b, q(u, vv)) }
            val lu = (1f - l.lunaX) * 0.5f + 0.25f; val lv = (H * (1f - l.lunaAltura * 0.9f)).coerceIn(0.08f, H - 0.08f)
            val r = 0.035f * k(lu); val p = q(lu, lv)
            drawCircle(Brush.radialGradient(listOf(Color(0xFFDCE6FF).copy(alpha = 0.35f * l.estrellas), Color.Transparent), p, r * 5f), r * 5f, p)
            drawCircle(Color(0xFFF4EEDC).copy(alpha = l.estrellas), r, p)
            drawCircle(lerp(l.alto, Color(0xFF2A3350), 0.4f).copy(alpha = 0.85f * l.estrellas), r * 0.95f, p + Offset(-r * 0.55f, -r * 0.2f))
        }
        // El sol: su claridad, desde fuera del encuadre; el disco, solo arriba desde el día del sol.
        if (l.solAltura > -0.2f && l.solFuerza > 0.02f) {
            val su = l.solX; val sv = H * (1f - l.solAltura.coerceIn(-0.2f, 1.6f) * 0.8f)
            val p = q(su.coerceIn(-0.2f, 1.2f), sv.coerceIn(-0.4f, H))
            val bajo = (1f - l.solAltura / 0.45f).coerceIn(0f, 1f)
            drawCircle(Brush.radialGradient(listOf(l.solColor.copy(alpha = 0.55f), l.solColor.copy(alpha = 0.18f), Color.Transparent), p, k(0.5f) * (0.55f + 0.3f * bajo)), k(0.5f) * (0.55f + 0.3f * bajo), p, blendMode = BlendMode.Screen)
            if (img.sol && sv > -0.05f) { val r = 0.04f * k(su.coerceIn(0f, 1f)); drawCircle(lerp(l.solColor, Color.White, 0.7f), r, p) }
        }
        // Las nubes del día: pisos de borbotones, con la luz del lado del sol y la base en sombra.
        val nubes = when (img.clima) { Clima.LLUVIA, Clima.TORMENTA -> 9; Clima.NUBES -> 6; else -> 2 + az.entero(4) }
        val carga = if (img.clima == Clima.LLUVIA || img.clima == Clima.TORMENTA) 0.7f else 0f
        val luz = lerp(lerp(Color.White, Color(0xFF9AA0AA), carga), l.solColor, if (l.solAltura in 0f..0.5f) 0.45f else 0.1f).let { Color(it.red * (0.35f + 0.65f * (1f - l.noche)), it.green * (0.35f + 0.65f * (1f - l.noche)), it.blue * (0.4f + 0.6f * (1f - l.noche))) }
        val sombra = lerp(lerp(l.medio, Color(0xFF8A86B0), 0.5f), Color(0xFF59616F), carga)
        repeat(nubes) {
            val u = az.entre(-0.1f, 1.05f); val vv = az.entre(0.08f, H - 0.12f); val w = az.entre(0.12f, 0.3f) * (if (carga > 0f) 1.6f else 1f)
            val kk = k(u.coerceIn(0f, 1f))
            for (j in 0..5) { val du = (j - 2.5f) / 5f * w; val alto = (1f - kotlin.math.abs(du) / w * 1.6f).coerceAtLeast(0.25f)
                val c = q(u + du, vv - 0.025f * alto)
                mancha(c, w * kk * 0.26f, w * kk * 0.14f * alto, luz, 0.85f)
                mancha(c + Offset(0f, w * kk * 0.08f), w * kk * 0.26f, w * kk * 0.07f, sombra, 0.6f) }
        }
        if (carga > 0f) drawPath(forma(listOf(0f to 0f, 1f to 0f, 1f to H, 0f to H)), lerp(l.medio, Color(0xFF8A90A0), 0.5f).copy(alpha = 0.35f))
    }

    // ---------------------------------------------------------------- Las ruinas

    /** Una torre partida: de [u0] a [u1], con la base en [base] y lo alto en [arriba], con el corte quebrado. */
    private fun torre(u0: Float, u1: Float, base: Float, arriba: Float, az: Azar): List<Pair<Float, Float>> {
        val ps = ArrayList<Pair<Float, Float>>()
        ps += u0 to base
        ps += u0 to arriba + az.entre(0f, 0.05f)
        val dientes = 3 + az.entero(3)
        for (i in 1 until dientes) ps += (u0 + (u1 - u0) * i / dientes) to (arriba + az.entre(-0.03f, 0.08f))
        ps += u1 to arriba + az.entre(0.02f, 0.12f)
        ps += u1 to base
        return ps
    }

    private fun DrawScope.ruinas(img: Imagen) {
        val l = img.l
        val dia = (1f - l.noche).coerceIn(0f, 1f)
        val solDer = l.solX > 0.5f
        val az = Azar(Semilla.de("laboratorio", "ruinas"))
        val piedraBase = Color(0xFF8E887E); val oscura = Color(0xFF55524D)
        // La luz de arriba en la piedra: [cara] 1, al sol; 0, de frente; −1, a la sombra. [lejos] la lleva a la bruma.
        fun luz(c: Color, lejos: Float, cara: Int): Color {
            val base = Color(c.red * l.ambiente.red * 0.86f, c.green * l.ambiente.green * 0.86f, c.blue * l.ambiente.blue * 0.86f)
            val col = when {
                cara > 0 -> lerp(base, Color((base.red * l.solColor.red * 1.3f).coerceIn(0f, 1f), (base.green * l.solColor.green * 1.25f).coerceIn(0f, 1f), (base.blue * l.solColor.blue * 1.1f).coerceIn(0f, 1f)), 0.7f * dia)
                cara < 0 -> lerp(base, Color(base.red * 0.52f, base.green * 0.56f, base.blue * 0.74f), 0.4f + 0.5f * dia)
                else -> base
            }
            return lerp(col, l.bruma, (lejos * l.brumaFuerza).coerceIn(0f, 0.92f))
        }
        val lado = if (solDer) 1 else -1
        // El suelo: de la bruma del horizonte a los escombros pardos de delante.
        drawPath(forma(listOf(-0.02f to H - 0.01f, 1.02f to H - 0.01f, 1.02f to 1.02f, -0.02f to 1.02f)),
            Brush.verticalGradient(0f to luz(piedraBase, 0.85f, 0), 0.3f to luz(piedraBase, 0.45f, 0), 1f to luz(lerp(oscura, C.TIERRA, 0.4f), 0f, 0), startY = q(0.5f, H).y, endY = q(0.5f, 1f).y))
        // Fila de lejos: muchas torres partidas, pálidas en la bruma.
        var u = -0.04f
        while (u < 1.05f) {
            val w = az.entre(0.025f, 0.06f); val alto = az.entre(0.05f, 0.17f)
            drawPath(forma(torre(u, u + w, H + 0.01f, H - alto, az)), luz(piedraBase, 0.72f, if (az.real() > 0.5f) lado else -lado))
            u += w + az.entre(0.003f, 0.03f)
        }
        // La calima entre la fila de lejos y la del medio.
        for (i in 0 until 6) mancha(q(i / 5f, H - 0.015f), k(i / 5f) * 0.28f, k(i / 5f) * 0.03f, l.bruma, 0.55f)
        if (img.verde > 0.0005f) verde(img, H + 0.005f, H + 0.05f, img.verde.coerceAtLeast(0.03f), 0.6f)
        // Fila del medio: torres grandes en tres cuartos (la cara de frente y la de lado), con sus pisos, sus huecos y el
        // corte quebrado; una tumbada, con el árbol torcido encima.
        val torres = listOf(Triple(0.03f, 0.1f, 0.3f), Triple(0.2f, 0.07f, 0.17f), Triple(0.36f, 0.12f, 0.38f), Triple(0.57f, 0.08f, 0.24f), Triple(0.74f, 0.11f, 0.33f), Triple(0.93f, 0.09f, 0.2f))
        for ((i, t) in torres.withIndex()) {
            val (u0, w, alto) = t; val base = H + 0.06f + 0.012f * (i % 2); val arriba = base - alto
            val ps = torre(u0, u0 + w, base, arriba, az)
            drawPath(forma(ps), luz(piedraBase, 0.32f, 0))
            // La cara de lado: hacia el centro de la imagen (la cámara está en medio), al sol o a la sombra.
            val haciaDer = u0 + w / 2f < 0.5f; val s = if (haciaDer) 1 else -1; val ancho = w * 0.32f
            val x0 = if (haciaDer) u0 + w else u0; val x1 = x0 + s * ancho
            drawPath(forma(listOf(x0 to base, x0 to ps[if (haciaDer) ps.size - 2 else 1].second, x1 to ps[if (haciaDer) ps.size - 2 else 1].second + 0.025f, x1 to base - 0.01f)), luz(piedraBase, 0.32f, if (s == lado) 1 else -1))
            // Los pisos (líneas) y los huecos negros de las ventanas, en rejilla, con alguno roto.
            var f = base - 0.03f
            while (f > arriba + 0.04f) {
                drawLine(luz(oscura, 0.32f, 0).copy(alpha = 0.5f), q(u0, f), q(u0 + w, f), 0.35f)
                for (c in 0 until 4) { if (az.real() < 0.2f) continue; val wu = u0 + w * (0.08f + c * 0.23f)
                    drawPath(forma(listOf(wu to f - 0.024f, wu + w * 0.13f to f - 0.024f, wu + w * 0.13f to f - 0.006f, wu to f - 0.006f)), luz(Color(0xFF26262A), 0.3f, 0)) }
                f -= 0.034f
            }
            // Las varillas que asoman del corte.
            repeat(3) { val uu = u0 + w * az.entre(0.1f, 0.9f); val vv = ps.minOf { it.second } + 0.02f; drawLine(luz(C.HIERRO, 0.3f, 1), q(uu, vv + 0.01f), q(uu + az.entre(-0.01f, 0.01f), vv - 0.03f), 0.4f) }
            if (i == 1) arbolTorcido(img, u0 + w * 0.55f, arriba + 0.02f)
        }
        // El campo de escombros del medio: bloques sueltos en filas, más grandes cuanto más cerca, cada uno con su cara al sol.
        for (fila in 0..3) {
            val vv = H + 0.1f + fila * 0.07f; val lejos = 0.3f - fila * 0.08f; val t = 0.008f + fila * 0.006f
            repeat(10 + fila * 2) {
                val uu = az.entre(-0.02f, 1.02f); val h = t * az.entre(0.6f, 1.6f); val ww = t * az.entre(1.2f, 3f)
                drawPath(forma(listOf(uu to vv, uu + ww to vv, uu + ww * 0.85f to vv - h, uu + ww * 0.1f to vv - h * 0.8f)), luz(piedraBase, lejos, 0))
                drawPath(forma(listOf(uu + ww * 0.85f to vv - h, uu + ww to vv, uu + ww + t * 0.6f to vv - 0.002f, uu + ww * 0.9f + t * 0.5f to vv - h * 0.9f)), luz(piedraBase, lejos, lado))
            }
        }
        // La fila de cerca del medio: muros rotos, con su hueco de ventana, y la caseta de hormigón (la boca del destello).
        for ((i, u0) in listOf(0.06f, 0.6f, 0.86f).withIndex()) {
            val w = 0.13f + 0.03f * i; val base = H + 0.25f; val alto = 0.08f + 0.03f * (i % 2)
            val ps = listOf(u0 to base, u0 to base - alto * 0.7f, u0 + w * 0.3f to base - alto, u0 + w * 0.55f to base - alto * 0.85f, u0 + w * 0.7f to base - alto * 0.4f, u0 + w to base - alto * 0.3f, u0 + w to base)
            drawPath(forma(ps), luz(piedraBase, 0.12f, if (i == 1) lado else 0))
            drawPath(forma(listOf(u0 + w * 0.3f to base - alto * 0.6f, u0 + w * 0.45f to base - alto * 0.6f, u0 + w * 0.45f to base - alto * 0.25f, u0 + w * 0.3f to base - alto * 0.25f)), luz(Color(0xFF26262A), 0.1f, 0))
        }
        val cu = 0.38f; val cv = H + 0.27f
        val caseta = lerp(C.HORMIGON, C.TIERRA, 0.45f)
        drawPath(forma(listOf(cu to cv, cu + 0.13f to cv, cu + 0.13f to cv - 0.1f, cu to cv - 0.1f)), luz(caseta, 0.1f, 0))
        drawPath(forma(listOf(cu + 0.13f to cv, cu + 0.17f to cv - 0.012f, cu + 0.17f to cv - 0.11f, cu + 0.13f to cv - 0.1f)), luz(caseta, 0.1f, lado))
        drawPath(forma(listOf(cu - 0.01f to cv - 0.1f, cu + 0.14f to cv - 0.1f, cu + 0.18f to cv - 0.115f, cu + 0.03f to cv - 0.118f)), luz(caseta, 0.1f, 1))
        // Las manchas de la caseta: hollín y óxido que chorrea.
        for (i in 0..2) drawLine(luz(C.HIERRO, 0.1f, 0).copy(alpha = 0.5f), q(cu + 0.02f + i * 0.045f, cv - 0.1f), q(cu + 0.022f + i * 0.045f, cv - 0.05f), 0.5f)
        drawPath(forma(listOf(cu + 0.04f to cv, cu + 0.08f to cv, cu + 0.08f to cv - 0.06f, cu + 0.04f to cv - 0.06f)), luz(Color(0xFF26262A), 0.08f, 0))
        if (img.verde > 0.15f) verde(img, H + 0.12f, H + 0.28f, (img.verde - 0.15f) * 2f, 0.25f)
        // Delante: los montones de escombro, con losas partidas, los hierros torcidos y la hierba rala y amarilla.
        for ((i, u0) in listOf(-0.06f, 0.28f, 0.62f).withIndex()) {
            val w = 0.34f + 0.05f * i; val base = 1.02f; val cima = 0.76f + 0.05f * i
            val monte = listOf(u0 to base, u0 + w * 0.12f to cima + 0.07f, u0 + w * 0.35f to cima + 0.01f, u0 + w * 0.5f to cima, u0 + w * 0.62f to cima + 0.03f, u0 + w * 0.85f to cima + 0.06f, u0 + w to base)
            drawPath(forma(monte), luz(lerp(oscura, piedraBase, 0.5f), 0f, 0))
            // Una losa partida, de canto, con su cara al sol.
            val lu = u0 + w * 0.42f; val lv = cima + 0.03f
            drawPath(forma(listOf(lu to lv + 0.05f, lu + 0.09f to lv + 0.02f, lu + 0.1f to lv - 0.02f, lu + 0.01f to lv)), luz(piedraBase, 0f, lado))
            drawPath(forma(listOf(lu to lv + 0.05f, lu + 0.01f to lv, lu - 0.015f to lv + 0.012f, lu - 0.012f to lv + 0.06f)), luz(piedraBase, 0f, -lado))
            repeat(9) { val uu = az.entre(u0 + 0.03f, u0 + w - 0.03f); val vv = az.entre(cima + 0.05f, 0.99f); val ss = az.entre(0.01f, 0.026f)
                drawPath(forma(listOf(uu to vv, uu + ss to vv - ss * 0.7f, uu + ss * 2f to vv)), luz(piedraBase, 0f, if (az.real() > 0.5f) lado else 0)) }
            // Los hierros torcidos: varillas rectas dobladas en ángulo.
            val hu = u0 + w * 0.68f; val k0 = k(hu)
            drawPath(Path().apply { val a = q(hu, cima + 0.04f); moveTo(a.x, a.y); val b = q(hu + 0.01f, cima - 0.06f); lineTo(b.x, b.y); val c = q(hu + 0.06f, cima - 0.09f); lineTo(c.x, c.y) },
                luz(C.HIERRO, 0f, 1), style = androidx.compose.ui.graphics.drawscope.Stroke(0.004f * k0 + 0.45f, cap = StrokeCap.Round))
            // La hierba rala y amarilla, en matas.
            repeat(5) { val cu2 = az.entre(u0, u0 + w); val cv2 = az.entre(cima + 0.04f, 1f)
                repeat(5) { j -> val p = q(cu2 + j * 0.004f, cv2); val kk = k(cu2); drawLine(luz(C.PAJA, 0f, 1), p, p + Offset((j - 2) * 0.004f * kk, -az.entre(0.012f, 0.03f) * kk), 0.4f) } }
        }
        if (img.verde > 0.3f) verde(img, 0.84f, 1f, (img.verde - 0.3f) * 3f, 0f)
    }

    /** El árbol que alguien plantó una vez en lo alto de una torre caída, y sigue allí, torcido. */
    private fun DrawScope.arbolTorcido(img: Imagen, u: Float, v: Float) {
        val l = img.l; val kk = k(u)
        val tronco = Color(0xFF4A3A2E).let { Color(it.red * l.ambiente.red, it.green * l.ambiente.green, it.blue * l.ambiente.blue) }
        val a = q(u, v); val b = q(u + 0.03f, v - 0.06f); val c = q(u + 0.06f, v - 0.08f)
        drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x, a.y - 0.03f * kk, b.x, b.y, c.x, c.y) }, tronco, style = androidx.compose.ui.graphics.drawscope.Stroke(0.01f * kk + 0.5f, cap = StrokeCap.Round))
        val hoja = lerp(Color(lerp(C.HOJA_OSC, C.PAJA, 0.3f).red * l.ambiente.red, lerp(C.HOJA_OSC, C.PAJA, 0.3f).green * l.ambiente.green, lerp(C.HOJA_OSC, C.PAJA, 0.3f).blue * l.ambiente.blue), l.bruma, 0.2f)
        for ((du, dv, r) in listOf(Triple(0.06f, -0.09f, 0.025f), Triple(0.085f, -0.075f, 0.02f), Triple(0.04f, -0.1f, 0.018f))) mancha(q(u + du, v + dv), r * kk, r * kk * 0.7f, hoja, 0.95f)
    }

    /**
     * La grama que ven las cámaras: manchas verdes entre las piedras, de [v0] a [v1] de alto, tantas como diga [cuanto]
     * (de 0 a 1). Empieza lejos del Claustro, a la izquierda de la imagen, y se extiende.
     */
    private fun DrawScope.verde(img: Imagen, v0: Float, v1: Float, cuanto: Float, lejos: Float) {
        val l = img.l; val k0 = cuanto.coerceIn(0f, 1f)
        val vivo = lerp(C.GRAMA, Color(0xFF9CD45C), 0.35f)
        val col = lerp(Color(vivo.red * l.ambiente.red, vivo.green * l.ambiente.green, vivo.blue * l.ambiente.blue), l.bruma, lejos * 0.4f)
        val oscuro = lerp(col, Color(C.HOJA_OSC.red * l.ambiente.red, C.HOJA_OSC.green * l.ambiente.green, C.HOJA_OSC.blue * l.ambiente.blue), 0.6f)
        val az = Azar(Semilla.de("laboratorio", "verde", (v0 * 100).toInt()))
        val n = (6 + 34 * k0).toInt()
        repeat(n) { val u = az.entre(0.02f, 0.22f + 0.76f * k0); val vv = az.entre(v0, v1); val kk = k(u); val w = az.entre(0.025f, 0.07f) * kk; val h = az.entre(0.008f, 0.02f) * kk
            mancha(q(u, vv) + Offset(0f, h * 0.4f), w, h, oscuro, 0.85f); mancha(q(u, vv), w * 0.85f, h * 0.8f, col, 0.95f) }
    }

    // ---------------------------------------------------------------- Arriba: el campamento

    /** El campamento de la doctora, junto a la caseta: la tienda de lona, la caja del equipo, el humo del hornillo y los surcos de grama. */
    private fun DrawScope.campamento(img: Imagen) {
        val l = img.l
        fun t(c: Color) = Color(c.red * l.ambiente.red, c.green * l.ambiente.green, c.blue * l.ambiente.blue)
        // La tienda.
        val tu = 0.58f; val tv = H + 0.22f
        drawPath(forma(listOf(tu to tv, tu + 0.09f to tv - 0.09f, tu + 0.18f to tv)), t(lerp(C.TELA, C.TIERRA, 0.35f)))
        drawPath(forma(listOf(tu + 0.09f to tv - 0.09f, tu + 0.18f to tv, tu + 0.22f to tv - 0.01f, tu + 0.12f to tv - 0.095f)), t(lerp(C.TELA, C.TIERRA, 0.55f)))
        drawPath(forma(listOf(tu + 0.075f to tv, tu + 0.09f to tv - 0.05f, tu + 0.105f to tv)), t(C.NEGRO))
        // La caja del equipo y la de raciones.
        drawPath(forma(listOf(tu + 0.24f to tv, tu + 0.3f to tv, tu + 0.3f to tv - 0.03f, tu + 0.24f to tv - 0.03f)), t(C.MADERA))
        // Los surcos cortos de grama, delante.
        for (i in 0..4) { val u0 = 0.18f + i * 0.13f; val vv = 0.88f + 0.02f * (i % 2)
            drawPath(forma(listOf(u0 to vv, u0 + 0.09f to vv - 0.01f, u0 + 0.09f to vv + 0.012f, u0 to vv + 0.02f)), t(lerp(C.TIERRA, C.NEGRO, 0.2f)))
            for (j in 0..4) mancha(q(u0 + 0.01f + j * 0.018f, vv - 0.004f), 0.008f * k(u0), 0.005f * k(u0), t(C.GRAMA), 0.9f) }
        // El humo del hornillo (la columna quieta; la parte viva la mueve).
        for (j in 0..6) { val vv = tv - 0.02f - j * 0.035f; mancha(q(tu + 0.27f + j * 0.008f, vv), (0.01f + j * 0.005f) * k(tu), (0.008f + j * 0.003f) * k(tu), lerp(t(C.TELA), l.medio, 0.4f), 0.4f - j * 0.04f) }
    }

    // ---------------------------------------------------------------- Lo que la hace pantalla

    /** Las líneas de barrido, la viñeta de la cámara, el brillo del vidrio y, el día del sol, el deslumbre. */
    private fun DrawScope.efectos(v: VistaLab, img: Imagen) {
        // El humo de las ruinas, el día que lo ven (la columna; la parte viva la dobla).
        if (img.humo) for (j in 0..13) { val vv = H + 0.04f - j * 0.033f; val u = 0.52f + j * 0.006f + j * j * 0.0019f
            val gris = lerp(Color(0xFF4A4846), img.l.medio, 0.1f + 0.045f * j)
            mancha(q(u, vv), (0.02f + j * 0.009f) * k(u), (0.022f + j * 0.006f) * k(u), gris, 0.9f - j * 0.05f) }
        // Las líneas de barrido: la imagen es de una cámara, no una ventana.
        var vv = 0f
        while (vv < 1f) { drawLine(Color.Black.copy(alpha = 0.06f), q(0f, vv), q(1f, vv), 0.35f); vv += 0.018f }
        // La viñeta y un velo frío: el color de la cámara.
        drawPath(SalaLab.formaDeLaPantalla(), Brush.horizontalGradient(0f to Color(0xFF0E1A28).copy(alpha = 0.35f), 0.2f to Color.Transparent, 0.85f to Color.Transparent, 1f to Color(0xFF0E1A28).copy(alpha = 0.3f), startX = q(0f, 0.5f).x, endX = q(1f, 0.5f).x))
        drawPath(SalaLab.formaDeLaPantalla(), Color(0xFF7FD8E0).copy(alpha = 0.05f))
        // El reflejo de la sala en el vidrio: una franja al sesgo.
        drawPath(forma(listOf(0.55f to 0f, 0.68f to 0f, 0.48f to 1f, 0.38f to 1f)), Color.White.copy(alpha = 0.04f + 0.03f * v.general))
        if (EscenaLaboratorio.deslumbra(v.m)) {
            // El día del sol la cámara deslumbra: la imagen se lava de blanco cálido desde el lado del sol.
            drawPath(SalaLab.formaDeLaPantalla(), Color(0xFFFFF4D6).copy(alpha = 0.22f))
            drawPath(SalaLab.formaDeLaPantalla(), Brush.radialGradient(listOf(Color(0xFFFFFAEA).copy(alpha = 0.9f), Color(0xFFFFF0C8).copy(alpha = 0.4f), Color.Transparent), q(img.l.solX.coerceIn(0.1f, 0.7f), 0.12f), k(0.5f) * 1.5f))
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    /** Las nubes que pasan, el humo que se dobla, la lluvia en la pantalla los días que llueve arriba. Barato: unas manchas. */
    fun DrawScope.vivo(v: VistaLab, reloj: Float) {
        val img = imagen(v.m); if (!img.encendida) return
        clipPath(SalaLab.formaDeLaPantalla()) {
            val l = img.l
            // Una nube suelta que cruza despacio.
            val u = ((reloj * 0.006f + 0.3f) % 1.3f) - 0.15f
            val kk = k(u.coerceIn(0f, 1f))
            mancha(q(u, 0.22f), 0.06f * kk, 0.018f * kk, lerp(Color.White, l.medio, 0.4f + 0.4f * l.noche), 0.35f)
            if (img.humo) for (j in 0..4) { val f = ((reloj * 0.05f + j / 5f) % 1f); val vv = H - 0.4f * f; val uu = 0.52f + 0.32f * f * f
                mancha(q(uu + 0.01f * sin(reloj + j), vv), (0.01f + 0.03f * f) * k(uu), (0.01f + 0.015f * f) * k(uu), Color(0xFF8A8A88), 0.3f * (1f - f)) }
            if (img.arriba) for (j in 0..3) { val f = ((reloj * 0.07f + j / 4f) % 1f); val vv = H + 0.18f - 0.2f * f; val uu = 0.85f + 0.02f * f
                mancha(q(uu, vv), (0.008f + 0.02f * f) * k(uu), (0.006f + 0.01f * f) * k(uu), lerp(Color.White, l.medio, 0.5f), 0.35f * (1f - f)) }
            if (img.clima == Clima.LLUVIA || img.clima == Clima.TORMENTA) {
                val az = Azar(Semilla.de("laboratorio", "lluvia"))
                repeat(30) { val uu = az.real(); val f = ((az.real() + reloj * 1.2f) % 1f); val p = q(uu, f); drawLine(Color.White.copy(alpha = 0.3f), p, p + Offset(-0.4f, 2.2f), 0.35f) }
            }
        }
    }
}
