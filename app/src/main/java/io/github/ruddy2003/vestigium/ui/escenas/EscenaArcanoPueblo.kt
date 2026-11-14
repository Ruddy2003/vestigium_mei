package io.github.ruddy2003.vestigium.ui.escenas

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
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaArcano.H
import io.github.ruddy2003.vestigium.ui.escenas.EscenaArcano.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaArcano.Niebla
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs

/**
 * El pueblo de Horror cósmico: «tres calles empinadas que bajan al puerto, casas de piedra gris con tejados de pizarra,
 * una iglesia con un campanario que se inclina, una taberna, una lonja y el muelle. Detrás, el monte». Y, más cerca,
 * los tejados de las casas que quedan debajo de la galería del archivo.
 */
internal object PuebloArcano {
    private val C = ColorArcano
    private val E = EscenaArcano

    /** La cresta del monte de detrás del pueblo, puesta a mano: sube hacia la derecha y deja libres el sol y la luna. */
    private val MONTE = listOf(Offset(286f, 122f), Offset(296f, 118.4f), Offset(304f, 115f), Offset(314f, 112.6f), Offset(322f, 107.6f), Offset(333f, 104.6f), Offset(342f, 99f), Offset(352f, 96.4f), Offset(361f, 91f), Offset(371f, 88.4f), Offset(384f, 83f))

    /** Una casa del pueblo: dónde pisa su fachada, lo que mide y cómo es. */
    private class Casa(val x: Float, val base: Float, val w: Float, val h: Float, val muro: Color, val pisos: Int, val lejos: Float, val galeria: Boolean = false, val chimenea: Float = 0.7f)

    /** Las tres hileras, de arriba (lejos) abajo (la calle del muelle). */
    private val ARRIBA = listOf(
        Casa(300f, 136f, 11f, 7.4f, C.PIEDRA, 2, 0.46f), Casa(343f, 134.6f, 10f, 7f, C.CAL, 2, 0.46f), Casa(354f, 136.6f, 12f, 8.4f, C.PIEDRA_OSC, 2, 0.45f), Casa(367f, 135f, 13f, 8f, C.PIEDRA, 2, 0.45f),
        Casa(307f, 144f, 12f, 8.6f, C.CAL, 2, 0.42f), Casa(344.6f, 144.6f, 13f, 9f, C.PIEDRA, 2, 0.41f), Casa(359f, 145.6f, 12f, 9.4f, C.PIEDRA_OSC, 2, 0.4f), Casa(372f, 145f, 14f, 9f, C.CAL, 2, 0.4f),
    )
    private val MEDIO = listOf(
        Casa(291f, 157f, 15f, 11f, C.PIEDRA, 2, 0.33f, chimenea = 0.3f), Casa(307f, 160f, 17f, 13f, C.PIEDRA_OSC, 3, 0.31f, galeria = true), Casa(325.6f, 158.6f, 15f, 11.6f, C.CAL, 2, 0.3f),
        Casa(342f, 161f, 18f, 13.6f, C.PIEDRA, 3, 0.29f, chimenea = 0.25f), Casa(361f, 160f, 16f, 12f, C.PIEDRA_OSC, 2, 0.28f, galeria = true), Casa(376f, 162f, 14f, 13f, C.PIEDRA, 3, 0.28f),
    )

    /** La posada de doña Inés: tres pisos de piedra gris en la calle del muelle. Su ventana es la del último piso que da al mar. */
    private val POSADA = Casa(302f, 188f, 23f, 30f, C.PIEDRA, 3, 0.2f, galeria = true, chimenea = 0.78f)
    internal val VENTANA_SUYA = Offset(313.6f, 163.6f)

    private val CALLE = listOf(
        Casa(279.6f, 181f, 19f, 15f, C.CAL, 2, 0.22f, chimenea = 0.3f), POSADA, Casa(327f, 189.6f, 20f, 18f, C.PIEDRA_OSC, 2, 0.19f, chimenea = 0.25f),
        Casa(348.6f, 191f, 18f, 21f, C.CAL, 3, 0.18f, galeria = true), Casa(367.6f, 192f, 20f, 17f, C.PIEDRA, 2, 0.18f, chimenea = 0.4f),
    )

    /** Las chimeneas que echan humo (en la parte viva): las de la posada, una de la calle de en medio y la del tejado de debajo de la galería. */
    private val HUMOS = listOf(Offset(320f, 153.4f), Offset(346.6f, 144.4f), Offset(372.6f, 171.4f))
    internal val CHIMENEA_CERCANA = Offset(264f, 187f)

    private fun alSol(r: Rayo) = r.lat >= 0f

    // ---------------------------------------------------------------- La parte quieta

    fun DrawScope.pueblo(m: Momento, l: Luces, r: Rayo) {
        monte(m, l, r)
        iglesia(m, l, r)
        val n = E.niebla(m).ordinal
        for (c in ARRIBA.sortedBy { it.base }) casa(m, l, r, c)
        with(BahiaArcano) { banco(l, 149f, 6f, listOf(0f, 0.1f, 0.4f, 0.7f)[n], m.azar("niebla", 5), 270f) }
        for (c in MEDIO.sortedBy { it.base }) casa(m, l, r, c)
        with(BahiaArcano) { banco(l, 170f, 7f, listOf(0f, 0.06f, 0.3f, 0.6f)[n], m.azar("niebla", 6), 262f) }
        lonja(m, l, r)
        for (c in CALLE.sortedBy { it.base }) casa(m, l, r, c)
        calleDelMuelle(m, l, r)
        with(BahiaArcano) { banco(l, 196f, 8f, listOf(0f, 0f, 0.2f, 0.45f)[n], m.azar("niebla", 7), 230f) }
        tejadosCercanos(m, l, r)
    }

    /**
     * El monte de detrás: un robledal cerrado, con sus copas una a una en la cresta, unos pinos que sobresalen, dos
     * prados cercados y la senda que sube. De lejos se tiene que leer como bosque, no como pasto.
     */
    private fun DrawScope.monte(m: Momento, l: Luces, r: Rayo) {
        val lejos = E.perdido(m, 0.6f)
        val az = Azar(Semilla.de("arcano", "monte"))
        val otono = m.ficha.estacion == io.github.ruddy2003.vestigium.vidas.Estacion.OTONO
        val verde = if (E.invierno(m)) lerp(C.MONTE, Color(0xFF6A6658), 0.45f) else if (otono) lerp(C.MONTE, Color(0xFF8A6A3C), 0.35f) else C.MONTE
        val forma = curva(MONTE, 150f)
        drawPath(forma, Brush.verticalGradient(listOf(l.tono(lerp(verde, C.LUZ, 0.12f * r.dia), lejos), l.tono(verde, lejos * 0.92f), l.tono(lerp(verde, Color(0xFF2C3C34), 0.3f), lejos * 0.85f)), 84f, 150f))
        // Las copas de la cresta: un festón, no una raya.
        for (i in 0 until MONTE.size - 1) { val a = MONTE[i]; val b = MONTE[i + 1]; var t = 0f
            while (t < 1f) { val p = a + (b - a) * t; val rad = az.entre(1.4f, 2.6f); drawCircle(l.tono(lerp(verde, Color(0xFF2C3C34), az.real() * 0.3f), lejos), rad, p + Offset(0f, rad * 0.3f)); t += az.entre(0.12f, 0.2f) } }
        clipPath(forma) {
            // Cada copa con su luz de un lado y su sombra del otro, en hiladas que bajan la ladera.
            val luz = l.tono(lerp(verde, Color(0xFFD8D49A), 0.34f), lejos).copy(alpha = 0.5f * r.dia + 0.08f); val sombra = l.tono(mult(verde, r.color, 0.7f), lejos * 0.9f).copy(alpha = 0.5f)
            repeat(170) {
                val x = az.entre(288f, 382f); val cresta = 122f - (x - 286f) * 0.4f; val y = cresta + az.entre(1f, 30f); val rad = az.entre(1.5f, 3f)
                drawCircle(sombra, rad, Offset(x + (if (alSol(r)) -0.7f else 0.7f), y + 0.6f)); drawCircle(luz, rad * 0.62f, Offset(x + (if (alSol(r)) 0.6f else -0.6f), y - 0.5f))
            }
            // Dos prados con su cerca de piedra, y la senda.
            for ((cx, cy, w) in listOf(Triple(348f, 112f, 13f), Triple(370f, 101f, 10f))) {
                val prado = poligono(Offset(cx - w / 2f, cy + 2f), Offset(cx - w * 0.3f, cy - 2.4f), Offset(cx + w * 0.5f, cy - 3.4f), Offset(cx + w * 0.62f, cy + 1.4f))
                drawPath(prado, l.tono(lerp(Color(0xFF8E9A62), C.LIQUEN, if (E.invierno(m)) 0.4f else 0.1f), lejos)); drawPath(prado, l.tono(C.PIEDRA_OSC, lejos), style = Stroke(0.4f))
            }
            drawPath(Path().apply { moveTo(336f, 126f); cubicTo(344f, 118f, 350f, 120f, 356f, 108f); cubicTo(360f, 100f, 368f, 98f, 376f, 90f) }, l.tono(C.ARENA, lejos).copy(alpha = 0.6f), style = Stroke(0.6f, cap = StrokeCap.Round))
        }
        // Unos pinos que sobresalen del robledal.
        for ((x, alto) in listOf(337f to 7f, 357f to 8f, 365f to 6.4f, 378f to 8.4f, 316f to 5.6f)) {
            val cima = 122f - (x - 286f) * 0.4f - alto + 1f
            drawLine(l.tono(C.MADERA_OSC, lejos), Offset(x, cima + alto), Offset(x, cima + 2f), 0.5f)
            for (k in 0..2) drawPath(poligono(Offset(x, cima + k * alto * 0.2f), Offset(x - 1.4f - k * 0.7f, cima + alto * 0.36f + k * alto * 0.2f), Offset(x + 1.4f + k * 0.7f, cima + alto * 0.36f + k * alto * 0.2f)), l.tono(Color(0xFF32483C), lejos))
        }
    }

    /** La iglesia, pequeña y oscura, con el campanario que se inclina, y el cementerio de las rocas, con las tumbas mirando al mar. */
    private fun DrawScope.iglesia(m: Momento, l: Luces, r: Rayo) {
        val lejos = E.perdido(m, 0.44f)
        val piedra = l.tono(C.PIEDRA_OSC, lejos); val luz = l.tono(lerp(C.PIEDRA, C.LUZ, 0.3f * r.dia), lejos); val sombra = l.tono(mult(C.PIEDRA_OSC, r.color, 0.7f * r.contraste), lejos)
        // El cementerio: la loma de roca, su tapia y las cruces.
        val loma = poligono(listOf(Offset(294f, 140f), Offset(298f, 134.4f), Offset(306f, 131.6f), Offset(316f, 130.6f), Offset(322f, 132f), Offset(322f, 141f)))
        drawPath(loma, Brush.verticalGradient(listOf(l.tono(lerp(C.MONTE, C.LIQUEN, 0.3f), lejos), l.tono(C.PIEDRA_NEGRA, lejos)), 130f, 141f))
        drawLine(l.tono(C.PIEDRA, lejos), Offset(298f, 134.6f), Offset(321f, 131.4f), 0.9f)
        val az = Azar(Semilla.de("arcano", "cementerio"))
        repeat(9) { val x = 299.4f + it * 2.4f; val y = 133.6f - it * 0.36f + az.entre(-0.3f, 0.3f); drawLine(l.tono(C.CAL, lejos), Offset(x, y), Offset(x, y - 2f), 0.4f); drawLine(l.tono(C.CAL, lejos), Offset(x - 0.6f, y - 1.4f), Offset(x + 0.6f, y - 1.4f), 0.35f) }
        // La cruz de hierro de los que el mar no devolvió.
        drawLine(l.tono(C.HIERRO, lejos), Offset(296.4f, 135.6f), Offset(296.4f, 129.4f), 0.6f); drawLine(l.tono(C.HIERRO, lejos), Offset(294.6f, 131.2f), Offset(298.2f, 131.2f), 0.5f)
        if ((E.difuntos(m) || E.candelaria(m)) && l.noche > 0.25f) repeat(11) { val p = Offset(298.6f + it * 2.1f, 134.2f - it * 0.34f); drawCircle(C.ACEITE, 0.5f, p); mancha(p, 2.4f, 2f, C.ACEITE, 0.3f * l.noche) }
        // La nave, con su tejado de pizarra y su ábside.
        val nave = Rect(326f, 128.6f, 345f, 139.6f)
        drawRect(if (alSol(r)) sombra else luz, nave.topLeft, Size(5f, nave.height)); drawRect(Brush.horizontalGradient(if (alSol(r)) listOf(piedra, luz) else listOf(luz, piedra), nave.left + 5f, nave.right), Offset(nave.left + 5f, nave.top), Size(nave.width - 5f, nave.height))
        drawPath(poligono(Offset(nave.left - 0.6f, nave.top + 0.3f), Offset(nave.left + 2.6f, nave.top - 5f), Offset(nave.right - 1.4f, nave.top - 5f), Offset(nave.right + 0.8f, nave.top + 0.3f)), l.tono(C.PIZARRA, lejos))
        drawLine(l.tono(lerp(C.PIZARRA, C.LUZ, 0.4f), lejos).copy(alpha = 0.7f), Offset(nave.left + 2.6f, nave.top - 5f), Offset(nave.right - 1.4f, nave.top - 5f), 0.35f)
        for (x in listOf(332.6f, 337.6f, 342f)) { drawRect(l.tono(C.PIZARRA_OSC, lejos), Offset(x, 131.6f), Size(1.1f, 3.4f)); drawCircle(l.tono(C.PIZARRA_OSC, lejos), 0.55f, Offset(x + 0.55f, 131.6f)) }
        // El campanario: se inclina hacia el mar. **[Diseño propio]**: cuánto y hacia dónde.
        val pie = Offset(322.6f, 140f); val ladeo = -3.2f; val alto = 37f; val w = 7.4f
        fun en(k: Float, dx: Float) = Offset(pie.x + ladeo * k + dx, pie.y - alto * k)
        val torre = poligono(en(0f, -w / 2f), en(1f, -w * 0.42f), en(1f, w * 0.42f), en(0f, w / 2f))
        drawPath(torre, Brush.horizontalGradient(if (alSol(r)) listOf(sombra, piedra, luz) else listOf(luz, piedra, sombra), pie.x - w / 2f + ladeo * 0.5f, pie.x + w / 2f + ladeo * 0.5f))
        clipPath(torre) {
            for (k in listOf(0.3f, 0.56f, 0.78f)) drawLine(l.tono(C.PIEDRA_NEGRA, lejos).copy(alpha = 0.5f), en(k, -w), en(k, w), 0.4f)
            repeat(7) { mancha(en(az.real(), az.entre(-3f, 3f)), 2.4f, 3.6f, l.tono(if (it % 2 == 0) C.LIQUEN else C.PIEDRA_NEGRA, lejos), 0.24f) }
        }
        // El vano de las campanas (con luz, la noche en que suenan solas) y una saetera.
        val campanas = E.hoy(m, "campanas") && l.noche > 0.5f
        val vano = Path().apply { val a = en(0.8f, -1.3f); val b = en(0.8f, 1.3f); val c = en(0.93f, 1.3f); val d = en(0.93f, -1.3f); moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(c.x, c.y); cubicTo(c.x, c.y - 1.6f, d.x, d.y - 1.6f, d.x, d.y); close() }
        drawPath(vano, if (campanas) lerp(C.ACEITE, Color(0xFF9CC8B0), 0.6f) else l.tono(Color(0xFF242C30), lejos))
        if (campanas) mancha(en(0.86f, 0f), 9f, 8f, Color(0xFF9CC8B0), 0.3f)
        drawLine(l.tono(C.LIQUEN, lejos).copy(alpha = if (campanas) 0.3f else 0.8f), en(0.9f, -0.5f), en(0.84f, 0.3f), 0.8f)
        drawRect(l.tono(Color(0xFF242C30), lejos), en(0.46f, -0.4f), Size(0.8f, 2.2f))
        // El chapitel de pizarra, la cruz y el gallo de la veleta.
        val punta = en(1.36f, 0.2f)
        drawPath(poligono(en(1f, -w * 0.5f), punta, en(1f, w * 0.5f)), l.tono(C.PIZARRA, lejos)); drawPath(poligono(en(1f, if (alSol(r)) 0f else -w * 0.5f), punta, en(1f, if (alSol(r)) w * 0.5f else 0f)), l.tono(lerp(C.PIZARRA, C.LUZ, 0.3f * r.dia), lejos))
        drawLine(l.tono(C.HIERRO, lejos), punta, punta + Offset(0f, -4.4f), 0.4f); drawLine(l.tono(C.HIERRO, lejos), punta + Offset(-1.2f, -2.6f), punta + Offset(1.2f, -2.6f), 0.4f)
        if (E.luto(m)) drawRect(l.tono(Color(0xFF1E1C22), lejos), en(0.62f, -1.4f), Size(2.8f, 5f))            // el crespón en el campanario
    }

    private fun conLuz(m: Momento, id: Int, cuantas: Float): Boolean = cuantas > 0f && Azar(Semilla.de("arcano", "ventana", id, m.ficha.dia)).real() < cuantas

    /**
     * Una casa de piedra con su tejado de pizarra a dos aguas: la fachada, que mira al puerto; el costado que se ve
     * desde la galería; la chimenea, las ventanas (cada una con luz o sin ella) y, en algunas, la galería de madera.
     */
    private fun DrawScope.casa(m: Momento, l: Luces, r: Rayo, c: Casa) {
        val lejos = E.perdido(m, c.lejos); val cerca = 1f - c.lejos * 2f
        val lado = c.w * 0.3f; val alto = c.h; val tejado = alto * 0.34f + 1.4f
        val luz = l.tono(lerp(c.muro, C.LUZ, 0.3f * r.dia), lejos); val medio = l.tono(c.muro, lejos); val sombra = l.tono(mult(c.muro, r.color, 0.72f * r.contraste + 0.06f), lejos)
        val x0 = c.x; val x1 = c.x + c.w; val top = c.base - alto
        // El costado (el hastial) y la fachada.
        drawPath(poligono(Offset(x0, c.base), Offset(x0, top), Offset(x0 + lado / 2f, top - tejado), Offset(x0 + lado, top), Offset(x0 + lado, c.base)), if (alSol(r)) sombra else luz)
        drawRect(Brush.verticalGradient(listOf(if (alSol(r)) luz else medio, if (alSol(r)) medio else lerp(medio, sombra, 0.5f)), top, c.base), Offset(x0 + lado, top), Size(c.w - lado, alto))
        // El tejado: la vertiente que se ve, con sus hiladas, su cumbrera y su liquen.
        val vert = poligono(Offset(x0 + lado / 2f, top - tejado), Offset(x1 - lado / 2f + 0.6f, top - tejado), Offset(x1 + 0.8f, top + 0.5f), Offset(x0 + lado - 0.4f, top + 0.5f))
        val pizarra = if (E.nevado(m)) lerp(C.PIZARRA, Color.White, 0.7f) else if (E.mojado(m)) lerp(C.PIZARRA, C.PIZARRA_OSC, 0.5f) else C.PIZARRA
        drawPath(vert, Brush.verticalGradient(listOf(l.tono(lerp(pizarra, C.LUZ, 0.22f * r.dia), lejos), l.tono(pizarra, lejos)), top - tejado, top))
        val az = Azar(Semilla.de("arcano", "casa", c.x.toInt(), c.base.toInt()))
        clipPath(vert) {
            if (cerca > 0.3f) for (k in 1..3) drawLine(l.tono(C.PIZARRA_OSC, lejos).copy(alpha = 0.4f), Offset(x0, top - tejado + k * tejado / 4f), Offset(x1 + 1f, top - tejado + k * tejado / 4f), 0.3f)
            repeat(3) { mancha(Offset(az.entre(x0 + lado, x1), az.entre(top - tejado, top)), az.entre(1.4f, 3.4f), az.entre(0.8f, 1.6f), l.tono(if (it == 0) C.LIQUEN else C.PIZARRA_OSC, lejos), 0.4f) }
        }
        drawLine(l.tono(lerp(pizarra, Color.White, 0.3f), lejos).copy(alpha = 0.7f), Offset(x0 + lado / 2f, top - tejado), Offset(x1 - lado / 2f + 0.6f, top - tejado), 0.4f)
        drawLine(l.tono(Color(0xFF2A3034), lejos).copy(alpha = 0.6f), Offset(x0 + lado - 0.4f, top + 0.6f), Offset(x1 + 0.8f, top + 0.6f), 0.4f)
        // La chimenea.
        val cx = x0 + lado / 2f + (c.w - lado) * c.chimenea
        drawRect(l.tono(C.PIEDRA_OSC, lejos), Offset(cx - 1f, top - tejado - 2.6f), Size(2f, 3.6f)); drawRect(if (alSol(r)) luz else sombra, Offset(cx + 0.2f, top - tejado - 2.6f), Size(0.8f, 3.6f)); drawRect(l.tono(C.PIEDRA_NEGRA, lejos), Offset(cx - 1.3f, top - tejado - 3.2f), Size(2.6f, 0.7f))
        // Las ventanas, por pisos; y la puerta.
        val cuantas = E.ventanas(m); val columnas = ((c.w - lado) / (3.6f + 1.4f * cerca)).toInt().coerceAtLeast(1)
        val vw = (1.1f + 0.9f * cerca); val vh = vw * 1.5f
        val velas = E.candelaria(m) && l.noche > 0.25f
        for (p in 0 until c.pisos) for (k in 0 until columnas) {
            val v = Offset(x0 + lado + (c.w - lado) * (k + 0.5f) / columnas - vw / 2f, top + alto * (p + 0.28f) / c.pisos)
            if (p == c.pisos - 1 && k == columnas / 2 && c.pisos > 1 && c.base > 150f) { drawRect(l.tono(C.MADERA_OSC, lejos), Offset(v.x - 0.2f, c.base - vh * 1.5f), Size(vw + 0.4f, vh * 1.5f)); continue }
            if (c === POSADA && p == 0 && k == 0) continue                       // la suya va aparte, más abajo
            val encendida = conLuz(m, (c.x * 7 + p * 31 + k * 3).toInt(), cuantas)
            drawRect(if (encendida) C.ACEITE else l.tono(Color(0xFF2E3A40), lejos), v, Size(vw, vh))
            if (cerca > 0.4f) { drawLine(l.tono(C.CAL, lejos).copy(alpha = 0.7f), v + Offset(vw / 2f, 0f), v + Offset(vw / 2f, vh), 0.2f); drawRect(l.tono(C.PIEDRA_NEGRA, lejos).copy(alpha = 0.5f), v + Offset(-0.3f, vh), Size(vw + 0.6f, 0.4f)) }
            if (encendida) mancha(v + Offset(vw / 2f, vh / 2f), vw * 2.6f, vh * 1.8f, C.ACEITE, 0.22f * maxOf(l.noche, 0.3f))
            if (velas && !encendida) { drawCircle(C.ACEITE, 0.45f, v + Offset(vw / 2f, vh - 0.4f)); mancha(v + Offset(vw / 2f, vh - 0.4f), 2f, 1.6f, C.ACEITE, 0.3f) }
        }
        // La galería de madera del piso alto, cerrada con cristales.
        if (c.galeria) { val gy = top + alto * 0.14f; val gh = alto / c.pisos * 0.62f
            drawRect(l.tono(C.MADERA_OSC, lejos), Offset(x0 + lado + 0.6f, gy), Size(c.w - lado - 1.2f, gh)); var x = x0 + lado + 1.2f
            while (x < x1 - 1.6f) { drawRect(if (conLuz(m, (c.x * 3 + x).toInt(), cuantas)) C.ACEITE else l.tono(Color(0xFF5A6C70), lejos), Offset(x, gy + 0.5f), Size(1.5f * (0.6f + 0.5f * cerca), gh - 1f)); x += 2.2f * (0.6f + 0.5f * cerca) } }
        // El zócalo, comido por la humedad, y lo que el salitre le ha hecho a la fachada.
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(C.PIEDRA_NEGRA, lejos).copy(alpha = 0.5f)), c.base - alto * 0.24f, c.base), Offset(x0, c.base - alto * 0.24f), Size(c.w, alto * 0.24f))
        if (cerca > 0.5f) repeat(4) { mancha(Offset(az.entre(x0 + lado, x1), az.entre(top + 2f, c.base - 2f)), az.entre(1.6f, 3.4f), az.entre(2f, 5f), l.tono(if (it % 2 == 0) C.CAL else C.PIEDRA_NEGRA, lejos), 0.2f) }
        // La sombra corta que arroja a su lado.
        val largo = (abs(r.ax) * alto * 0.34f).coerceAtMost(8f)
        sombraSuelta(r, if (r.ax > 0f) poligono(Offset(x1, c.base), Offset(x1 + largo, c.base - 1f), Offset(x1 + largo, c.base - 2.6f), Offset(x1, top + alto * 0.6f)) else poligono(Offset(x0, c.base), Offset(x0 - largo, c.base - 1f), Offset(x0 - largo, c.base - 2.6f), Offset(x0, top + alto * 0.6f)), 0.75f)
        if (c === POSADA) {
            // El letrero de madera que el salitre ha comido (sin letras), en su brazo de hierro.
            drawLine(l.tono(C.HIERRO, lejos), Offset(x0 + lado, top + alto * 0.58f), Offset(x0 + lado - 3.4f, top + alto * 0.58f), 0.4f)
            drawRect(l.tono(lerp(C.MADERA, C.PIEDRA, 0.4f), lejos), Offset(x0 + lado - 3.6f, top + alto * 0.6f), Size(3f, 2.4f))
            // Su ventana, la del último piso que da al mar: la única con postigos. Cerrados casi siempre; las semanas de
            // la fiebre, abiertos de par en par, con luz dentro de día y de noche, la cortina corrida a medias y una vela.
            val v = VENTANA_SUYA; val enCama = E.ventanaDeLaPosada(m)
            val verde = l.tono(lerp(C.MONTE, C.TINTA, 0.5f), lejos)
            drawRect(l.tono(C.CAL, lejos), v + Offset(-3f, -3.6f), Size(6f, 7.6f))
            if (enCama) {
                mancha(v, 17f, 13f, C.ACEITE, 0.3f + 0.25f * l.noche)
                drawRect(C.ACEITE, v + Offset(-2.4f, -3f), Size(4.8f, 6.4f)); drawRect(lerp(C.ACEITE, Color.White, 0.6f), v + Offset(-2.4f, -3f), Size(1.8f, 6.4f))
                drawLine(l.tono(C.MADERA_OSC, lejos), v + Offset(0f, -3f), v + Offset(0f, 3.4f), 0.35f); drawLine(l.tono(C.MADERA_OSC, lejos), v + Offset(-2.4f, 0f), v + Offset(2.4f, 0f), 0.3f)
                drawRect(verde, v + Offset(-5.2f, -3.2f), Size(2.2f, 6.8f)); drawRect(verde, v + Offset(3f, -3.2f), Size(2.2f, 6.8f))
                drawCircle(Color(0xFFFFF4D0), 0.7f, v + Offset(1.2f, 2.6f)); mancha(v + Offset(1.2f, 2.4f), 3.4f, 3f, Color(0xFFFFF4D0), 0.5f)
            } else {
                drawRect(verde, v + Offset(-2.4f, -3f), Size(4.8f, 6.4f)); drawLine(l.tono(C.PIZARRA_OSC, lejos), v + Offset(0f, -3f), v + Offset(0f, 3.4f), 0.35f)
                for (k in 0..3) drawLine(l.tono(C.PIZARRA_OSC, lejos).copy(alpha = 0.5f), v + Offset(-2.2f, -2f + k * 1.5f), v + Offset(2.2f, -2f + k * 1.5f), 0.25f)
            }
            drawRect(l.tono(C.PIEDRA_NEGRA, lejos).copy(alpha = 0.6f), v + Offset(-3.2f, 3.6f), Size(6.4f, 0.6f))
        }
    }

    /** La lonja: una nave larga y baja con cuatro arcos al muelle, donde cada mañana se vende el pescado sobre las losas. */
    private fun DrawScope.lonja(m: Momento, l: Luces, r: Rayo) {
        val lejos = E.perdido(m, 0.22f)
        val x0 = 243f; val x1 = 280f; val b0 = 187.4f; val b1 = 176.6f; val alto = 9.6f
        fun base(x: Float) = b0 + (b1 - b0) * (x - x0) / (x1 - x0)
        val muro = poligono(Offset(x0, base(x0)), Offset(x1, base(x1)), Offset(x1, base(x1) - alto), Offset(x0, base(x0) - alto))
        drawPath(muro, Brush.horizontalGradient(if (alSol(r)) listOf(l.tono(C.PIEDRA_OSC, lejos), l.tono(lerp(C.PIEDRA, C.LUZ, 0.25f * r.dia), lejos)) else listOf(l.tono(lerp(C.PIEDRA, C.LUZ, 0.25f * r.dia), lejos), l.tono(C.PIEDRA_OSC, lejos)), x0, x1))
        val abierta = m.hora in 5.0..11.0 && !E.barcasVaradas(m)
        for (i in 0..3) { val x = x0 + 3f + i * 8.6f; val y = base(x + 3f)
            drawPath(Path().apply { moveTo(x, y); lineTo(x, y - 4.6f); cubicTo(x, y - 8f, x + 6f, y - 8.4f, x + 6f, y - 5f); lineTo(x + 6f, base(x + 6f)); close() }, if (abierta && l.noche > 0.3f) lerp(C.ACEITE, Color(0xFF3A3020), 0.5f) else l.tono(Color(0xFF242C30), lejos))
            if (abierta) for (k in 0..1) drawRect(l.tono(lerp(C.CARTON, C.CAL, 0.3f), lejos), Offset(x + 1f + k * 2.6f, y - 1.6f), Size(2f, 1.2f)) }
        val tejado = poligono(Offset(x0 - 1f, base(x0) - alto + 0.4f), Offset(x0 + 3f, base(x0) - alto - 4.4f), Offset(x1 + 0.6f, base(x1) - alto - 4.6f), Offset(x1 + 1.4f, base(x1) - alto + 0.4f))
        drawPath(tejado, l.tono(if (E.nevado(m)) lerp(C.PIZARRA, Color.White, 0.7f) else C.PIZARRA, lejos)); drawLine(l.tono(lerp(C.PIZARRA, Color.White, 0.3f), lejos).copy(alpha = 0.7f), Offset(x0 + 3f, base(x0) - alto - 4.4f), Offset(x1 + 0.6f, base(x1) - alto - 4.6f), 0.4f)
        clipPath(tejado) { val az = Azar(Semilla.de("arcano", "lonja")); repeat(6) { mancha(Offset(az.entre(x0, x1), az.entre(165f, 180f)), az.entre(2f, 5f), 1.2f, l.tono(if (it % 2 == 0) C.LIQUEN else C.PIZARRA_OSC, lejos), 0.4f) } }
    }

    /** La calle del muelle: su pretil, el borde de piedra del puerto con su verdín, los toneles, las cajas de pescado y un carro de mano. */
    private fun DrawScope.calleDelMuelle(m: Momento, l: Luces, r: Rayo) {
        val lejos = E.perdido(m, 0.2f)
        val az = Azar(Semilla.de("arcano", "calle"))
        val borde = listOf(Offset(268f, 171f), Offset(262f, 177.6f), Offset(254f, 183.4f), Offset(244f, 189.6f), Offset(235f, 194.4f), Offset(226f, 197.6f))
        val marea = E.marea(m)
        // La calle, de losas mojadas.
        val calle = Path().apply { moveTo(borde[0].x, borde[0].y); for (p in borde.drop(1)) lineTo(p.x, p.y); lineTo(226f, 220f); lineTo(ANCHO + 2f, 220f); lineTo(ANCHO + 2f, 190f); lineTo(300f, 186f); lineTo(280f, 178f); close() }
        drawPath(calle, Brush.verticalGradient(listOf(l.tono(C.PIEDRA_OSC, lejos), l.tono(lerp(C.PIEDRA, C.PIEDRA_OSC, 0.4f), E.perdido(m, 0.1f))), 172f, 216f))
        clipPath(calle) { repeat(90) { val p = Offset(az.entre(228f, ANCHO), az.entre(174f, 218f)); drawLine(l.tono(if (it % 3 == 0) C.CAL else C.PIEDRA_NEGRA, lejos).copy(alpha = 0.3f), p, p + Offset(az.entre(2f, 6f), az.entre(-0.6f, 0.6f)), 0.35f) }
            if (E.mojado(m) || E.niebla(m).ordinal >= Niebla.NIEBLA.ordinal) repeat(9) { mancha(Offset(az.entre(232f, ANCHO), az.entre(180f, 216f)), az.entre(5f, 12f), 1.2f, lerp(l.bajo, Color.White, 0.2f), 0.3f) } }
        // La pared del puerto, con su franja de verdín.
        for (i in 0 until borde.size - 1) { val a = borde[i]; val b = borde[i + 1]; val h = 4.4f + i * 0.5f + (1f - marea) * 2.4f
            drawPath(poligono(a, b, b + Offset(0f, h), a + Offset(0f, h)), Brush.verticalGradient(listOf(l.tono(lerp(C.PIEDRA_NEGRA, C.PIEDRA_OSC, 0.4f), lejos), l.tono(Color(0xFF28342E), lejos)), a.y, a.y + h))
            drawLine(l.tono(lerp(C.PIEDRA, Color.White, 0.2f), lejos).copy(alpha = 0.8f), a, b, 0.6f); drawLine(l.tono(Color.White, lejos * 0.5f).copy(alpha = 0.4f), a + Offset(0f, h + 0.3f), b + Offset(0f, h + 0.3f), 0.5f) }
        // Toneles, cajas de pescado, un carro de mano y un noray.
        for ((x, y) in listOf(250f to 187.6f, 253.4f to 186.4f, 240f to 193.4f)) { val f = Path().apply { moveTo(x - 1.5f, y); cubicTo(x - 2.2f, y - 1.6f, x - 2.2f, y - 3f, x - 1.5f, y - 4.4f); lineTo(x + 1.5f, y - 4.4f); cubicTo(x + 2.2f, y - 3f, x + 2.2f, y - 1.6f, x + 1.5f, y); close() }
            drawPath(f, Brush.horizontalGradient(if (alSol(r)) listOf(l.tono(C.MADERA_OSC, lejos), l.tono(C.MADERA, lejos)) else listOf(l.tono(C.MADERA, lejos), l.tono(C.MADERA_OSC, lejos)), x - 2f, x + 2f)); drawLine(l.tono(C.HIERRO, lejos), Offset(x - 2f, y - 1.3f), Offset(x + 2f, y - 1.3f), 0.3f); drawLine(l.tono(C.HIERRO, lejos), Offset(x - 2f, y - 3.2f), Offset(x + 2f, y - 3.2f), 0.3f) }
        for (i in 0..2) { val p = Offset(259f + i * 2.4f, 180.6f - i * 1.6f); drawRect(l.tono(lerp(C.MADERA, C.CARTON, 0.4f), lejos), p + Offset(-1.6f, -1.6f), Size(3.2f, 1.6f)); drawLine(l.tono(C.MADERA_OSC, lejos), p + Offset(-1.6f, -0.8f), p + Offset(1.6f, -0.8f), 0.25f) }
        val carro = Offset(288f, 186.4f)
        drawRect(l.tono(C.MADERA, lejos), carro + Offset(-4f, -3.4f), Size(7f, 2f)); drawCircle(l.tono(C.MADERA_OSC, lejos), 1.7f, carro + Offset(-0.6f, -0.8f), style = Stroke(0.5f)); drawLine(l.tono(C.MADERA_OSC, lejos), carro + Offset(3f, -3f), carro + Offset(7f, -0.4f), 0.5f)
        for ((x, y) in listOf(250f to 187.6f, 240f to 193.4f, 288f to 186.4f)) sombraOvalo(r, Offset(x - 2f + r.ax * 2f, y - 0.8f), Size(5f + abs(r.ax) * 2f, 1.4f), 0.75f)
        // Las farolas de la calle y, en las fiestas, los banderines de casa a casa.
        val encendidas = l.noche > 0.3f || E.niebla(m).ordinal >= Niebla.NIEBLA.ordinal
        for ((x, y) in listOf(272f to 180f, 316f to 190f, 356f to 193.6f)) {
            drawLine(l.tono(C.HIERRO, lejos), Offset(x, y), Offset(x, y - 9f), 0.6f); drawRect(if (encendidas) C.ACEITE else l.tono(Color(0xFF9CB0AC), lejos), Offset(x - 0.9f, y - 11f), Size(1.8f, 2.2f)); drawPath(poligono(Offset(x - 1.3f, y - 11f), Offset(x, y - 12.2f), Offset(x + 1.3f, y - 11f)), l.tono(C.HIERRO, lejos))
            if (encendidas) { mancha(Offset(x, y - 10f), 9f + 4f * E.niebla(m).ordinal, 7f + 3f * E.niebla(m).ordinal, C.ACEITE, 0.36f * maxOf(l.noche, 0.45f)); mancha(Offset(x, y + 0.6f), 8f, 1.8f, C.ACEITE, 0.3f * maxOf(l.noche, 0.3f)) }
        }
        if (E.fiesta(m) || E.bendicion(m)) { val colores = listOf(C.ALMAGRE, C.CAL, C.LIQUEN, C.MONTE); banderines(l, Offset(272f, 169f), Offset(316f, 179f), 3f, colores, 2.4f, lejos = lejos); banderines(l, Offset(316f, 179f), Offset(356f, 182.6f), 3f, colores, 2.6f, lejos = lejos) }
        // Lo que la calle deja en el agua, de noche: la luz de la primera farola.
        if (encendidas) reflejoRoto(268f, 275f, 184f, 13f, C.ACEITE, 0.4f * maxOf(l.noche, 0.3f), az, 1.1f)
    }

    /**
     * Los tejados de las dos casas que quedan debajo de la galería, a la derecha: sus vertientes de pizarra vistas desde
     * arriba, cada una a su manera, con sus hiladas, su cumbrera, su liquen, una claraboya, una buhardilla y sus
     * chimeneas (la de la casa de delante echa humo y cruza por delante del muelle).
     */
    private fun DrawScope.tejadosCercanos(m: Momento, l: Luces, r: Rayo) {
        val az = Azar(Semilla.de("arcano", "tejados"))
        val pizarra = if (E.nevado(m)) lerp(C.PIZARRA, Color.White, 0.72f) else if (E.mojado(m)) lerp(C.PIZARRA, C.PIZARRA_OSC, 0.55f) else C.PIZARRA
        val lejos = E.perdido(m, 0.04f)
        class Tejado(val x0: Float, val x1: Float, val cumbre: Float, val alero: Float, val sesgo: Float, val tono: Float)
        val tejados = listOf(Tejado(292f, 394f, 196f, 256f, -11f, 0.08f), Tejado(226f, 306f, 203.4f, 256f, -7f, 0.24f))
        for ((n, t) in tejados.withIndex()) {
            // El hastial de la casa de delante: encalado, con su ventanuco y su desconchón.
            if (n == 1) {
                val hastial = poligono(listOf(Offset(t.x0 + t.sesgo, t.cumbre + 0.6f), Offset(t.x0, t.alero), Offset(t.x0 - 16f, t.alero), Offset(t.x0 - 16f, t.cumbre + 13f)))
                val cal = lerp(C.CAL, C.PIEDRA, 0.3f); val c = Offset(t.x0 - 8f, 230f)
                drawPath(hastial, Brush.verticalGradient(listOf(l.tono(if (alSol(r)) mult(cal, r.color, 0.5f * r.contraste) else lerp(cal, C.LUZ, 0.2f * r.dia), lejos), l.tono(lerp(cal, C.PIEDRA_OSC, 0.4f), lejos)), t.cumbre, t.alero))
                clipPath(hastial) { repeat(6) { mancha(Offset(az.entre(t.x0 - 16f, t.x0), az.entre(t.cumbre + 8f, t.alero)), az.entre(2f, 5f), az.entre(3f, 7f), l.tono(if (it % 2 == 0) C.PIEDRA_OSC else C.PIEDRA_NEGRA, lejos), 0.3f) } }
                val luz = conLuz(m, 9003, E.ventanas(m))
                drawRect(if (luz) C.ACEITE else l.tono(Color(0xFF2E3A40), lejos), Offset(c.x - 2.4f, 222f), Size(5f, 6.4f)); drawRect(l.tono(C.MADERA_OSC, lejos), Offset(c.x - 2.4f, 222f), Size(5f, 6.4f), style = Stroke(0.5f)); drawLine(l.tono(C.MADERA_OSC, lejos), Offset(c.x + 0.1f, 222f), Offset(c.x + 0.1f, 228.4f), 0.4f)
                if (luz) mancha(Offset(c.x, 225f), 10f, 9f, C.ACEITE, 0.25f * l.noche)
                if (E.candelaria(m) && l.noche > 0.25f) { drawCircle(C.ACEITE, 0.7f, Offset(c.x, 227.6f)); mancha(Offset(c.x, 227f), 4f, 3.4f, C.ACEITE, 0.4f) }
            }
            val forma = poligono(Offset(t.x0 + t.sesgo, t.cumbre), Offset(t.x1 + t.sesgo, t.cumbre - 1.4f), Offset(t.x1, t.alero), Offset(t.x0, t.alero))
            val base = lerp(pizarra, C.PIZARRA_OSC, t.tono)
            drawPath(forma, Brush.verticalGradient(listOf(l.tono(lerp(base, C.LUZ, 0.3f * r.dia), lejos), l.tono(base, lejos), l.tono(lerp(base, C.PIZARRA_OSC, 0.3f), lejos)), t.cumbre, t.alero))
            clipPath(forma) {
                // Las hiladas de pizarra, cada vez más anchas hacia quien mira, y las juntas al tresbolillo.
                var y = t.cumbre + 2.2f; var fila = 0
                while (y < t.alero) { val paso = 2.4f + (y - t.cumbre) * 0.05f
                    drawLine(l.tono(C.PIZARRA_OSC, lejos).copy(alpha = 0.55f), Offset(t.x0 - 12f, y), Offset(t.x1 + 12f, y - 1f), 0.4f)
                    var x = t.x0 - 12f + (fila % 2) * 2.4f; while (x < t.x1 + 12f) { drawLine(l.tono(C.PIZARRA_OSC, lejos).copy(alpha = 0.35f), Offset(x, y), Offset(x + t.sesgo * 0.05f, y + paso), 0.3f); x += 4.4f + (y - t.cumbre) * 0.05f }
                    y += paso; fila++ }
                // Pizarras de otro tono, liquen amarillo y musgo; con lluvia, el brillo del agua.
                repeat(30) { val p = Offset(az.entre(t.x0, t.x1), az.entre(t.cumbre + 2f, t.alero)); drawRect(l.tono(lerp(base, if (it % 3 == 0) C.PIEDRA else C.PIZARRA_OSC, 0.4f), lejos).copy(alpha = 0.55f), p, Size(az.entre(2.4f, 4.4f), az.entre(2f, 3.2f))) }
                repeat(14) { mancha(Offset(az.entre(t.x0, t.x1), az.entre(t.cumbre + 1f, t.alero)), az.entre(2f, 6f), az.entre(1f, 2.4f), l.tono(if (it % 2 == 0) C.LIQUEN else Color(0xFF5E7048), lejos), 0.45f) }
                if (E.mojado(m)) repeat(7) { mancha(Offset(az.entre(t.x0, t.x1), az.entre(t.cumbre + 2f, t.alero)), az.entre(8f, 18f), 1.4f, lerp(l.bajo, Color.White, 0.3f), 0.3f) }
            }
            // El borde de la vertiente y la cumbrera, de piezas de barro encaladas.
            drawLine(l.tono(C.PIZARRA_OSC, lejos), Offset(t.x0 + t.sesgo, t.cumbre), Offset(t.x0, t.alero), 0.9f)
            drawLine(l.tono(lerp(C.PIEDRA, C.LUZ, 0.3f * r.dia), lejos), Offset(t.x0 + t.sesgo, t.cumbre), Offset(t.x1 + t.sesgo, t.cumbre - 1.4f), 1.7f); drawLine(l.tono(C.PIZARRA_OSC, lejos).copy(alpha = 0.6f), Offset(t.x0 + t.sesgo, t.cumbre + 1f), Offset(t.x1 + t.sesgo, t.cumbre - 0.4f), 0.4f)
            var x = t.x0 + t.sesgo + 4f; while (x < t.x1 + t.sesgo) { val y = t.cumbre - (x - t.x0 - t.sesgo) * 1.4f / (t.x1 - t.x0); drawLine(l.tono(C.PIEDRA_OSC, lejos), Offset(x, y - 0.9f), Offset(x, y + 0.9f), 0.3f); x += 5f }
        }
        // La chimenea de la casa de delante: de piedra, con sus dos caños de barro.
        val ch = CHIMENEA_CERCANA
        val cuerpo = poligono(Offset(ch.x - 5f, 216f), Offset(ch.x - 4.4f, ch.y + 2f), Offset(ch.x + 4.4f, ch.y + 2f), Offset(ch.x + 5f, 218f))
        sombraSuelta(r, poligono(Offset(ch.x - 5f, 217f), Offset(ch.x + 5f, 218f), Offset(ch.x + 5f + r.ax * 14f, 226f), Offset(ch.x - 5f + r.ax * 14f, 225f)))
        drawPath(cuerpo, Brush.horizontalGradient(if (alSol(r)) listOf(l.tono(mult(C.PIEDRA_OSC, r.color, 0.6f * r.contraste), lejos), l.tono(lerp(C.PIEDRA, C.LUZ, 0.3f * r.dia), lejos)) else listOf(l.tono(lerp(C.PIEDRA, C.LUZ, 0.3f * r.dia), lejos), l.tono(mult(C.PIEDRA_OSC, r.color, 0.6f * r.contraste), lejos)), ch.x - 5f, ch.x + 5f))
        clipPath(cuerpo) { for (k in 0..7) drawLine(l.tono(C.PIEDRA_NEGRA, lejos).copy(alpha = 0.4f), Offset(ch.x - 6f, ch.y + 5f + k * 4f), Offset(ch.x + 6f, ch.y + 5f + k * 4f), 0.3f); repeat(5) { mancha(Offset(ch.x + az.entre(-4f, 4f), ch.y + az.entre(3f, 26f)), 2.4f, 3f, l.tono(if (it % 2 == 0) Color(0xFF2A2A2A) else C.LIQUEN, lejos), 0.3f) } }
        drawRect(l.tono(C.PIEDRA_NEGRA, lejos), Offset(ch.x - 5.6f, ch.y), Size(11.2f, 2.2f)); drawRect(l.tono(lerp(C.PIEDRA, C.LUZ, 0.2f * r.dia), lejos), Offset(ch.x - 5.6f, ch.y), Size(11.2f, 0.6f))
        for (dx in listOf(-2.6f, 2.2f)) { drawRect(l.tono(C.ALMAGRE, lejos), Offset(ch.x + dx - 1.3f, ch.y - 4f), Size(2.6f, 4f)); drawRect(l.tono(Color(0xFF2A2A2A), lejos), Offset(ch.x + dx - 1.3f, ch.y - 4f), Size(2.6f, 0.8f)) }
        // Una claraboya (con luz, de noche) en ese tejado.
        val clara = Offset(236f, 209f)
        drawPath(poligono(clara, clara + Offset(11f, -0.4f), clara + Offset(10f, 6.4f), clara + Offset(-1f, 6.8f)), l.tono(C.HIERRO, lejos))
        val luzClara = conLuz(m, 9001, E.ventanas(m))
        drawPath(poligono(clara + Offset(1f, 1f), clara + Offset(10f, 0.6f), clara + Offset(9.2f, 5.4f), clara + Offset(0.2f, 5.8f)), if (luzClara) C.ACEITE else lerp(l.tono(Color(0xFF7E9698), lejos), l.alto, 0.3f))
        drawLine(l.tono(C.HIERRO, lejos), clara + Offset(5.4f, 0.4f), clara + Offset(4.6f, 6f), 0.5f)
        if (luzClara) mancha(clara + Offset(5f, 3f), 16f, 9f, C.ACEITE, 0.25f * l.noche)
        // La buhardilla del tejado de detrás, con su ventana de cuatro cristales.
        val bu = Offset(318f, 201f)
        drawPath(poligono(bu + Offset(0f, 14f), bu + Offset(0f, 4f), bu + Offset(8f, -3f), bu + Offset(16f, 4f), bu + Offset(16f, 15f)), l.tono(if (alSol(r)) lerp(C.CAL, C.LUZ, 0.2f * r.dia) else mult(C.CAL, r.color, 0.5f * r.contraste), lejos))
        drawPath(poligono(listOf(bu + Offset(-2f, 5f), bu + Offset(8f, -4.6f), bu + Offset(18f, 5f), bu + Offset(16f, 5.4f), bu + Offset(8f, -2f), bu + Offset(0f, 5.4f))), l.tono(C.PIZARRA_OSC, lejos))
        val luzBu = conLuz(m, 9002, E.ventanas(m))
        drawRect(if (luzBu) C.ACEITE else l.tono(Color(0xFF2E3A40), lejos), bu + Offset(4.4f, 4f), Size(7.2f, 8f)); drawLine(l.tono(C.CAL, lejos), bu + Offset(8f, 4f), bu + Offset(8f, 12f), 0.5f); drawLine(l.tono(C.CAL, lejos), bu + Offset(4.4f, 8f), bu + Offset(11.6f, 8f), 0.5f)
        if (luzBu) mancha(bu + Offset(8f, 8f), 14f, 10f, C.ACEITE, 0.25f * l.noche)
        if (E.candelaria(m) && l.noche > 0.25f) { drawCircle(C.ACEITE, 0.8f, bu + Offset(8f, 11f)); mancha(bu + Offset(8f, 10f), 5f, 4f, C.ACEITE, 0.4f) }
        // Su chimenea, y una gaviota posada en la cumbrera de delante.
        val c2 = Offset(360f, 182f)
        sombraSuelta(r, poligono(c2 + Offset(-3.4f, 14f), c2 + Offset(3.4f, 14f), c2 + Offset(3.4f + r.ax * 10f, 22f), c2 + Offset(-3.4f + r.ax * 10f, 22f)))
        drawRect(l.tono(if (alSol(r)) lerp(C.PIEDRA, C.LUZ, 0.2f * r.dia) else C.PIEDRA_OSC, lejos), c2 + Offset(-3.4f, 0f), Size(6.8f, 15f)); drawRect(l.tono(mult(C.PIEDRA_OSC, r.color, 0.5f), lejos), c2 + Offset(if (alSol(r)) -3.4f else 1f, 0f), Size(2.4f, 15f)); drawRect(l.tono(C.PIEDRA_NEGRA, lejos), c2 + Offset(-4f, -1.4f), Size(8f, 1.8f))
        if (!E.sinPajaros(m) && l.noche < 0.6f && !E.temporal(m)) { val g = Offset(288f, 202.6f); drawOval(l.tono(Color.White, lejos), g + Offset(-2.6f, -3f), Size(5.4f, 3f)); drawCircle(l.tono(Color.White, lejos), 1.2f, g + Offset(2.6f, -3.6f)); drawLine(l.tono(C.LIQUEN, lejos), g + Offset(3.6f, -3.5f), g + Offset(5f, -3.2f), 0.5f); drawPath(poligono(g + Offset(-2.6f, -2.6f), g + Offset(-5f, -1.6f), g + Offset(-1.6f, -0.8f)), l.tono(C.PIZARRA, lejos)); drawLine(l.tono(C.LIQUEN, lejos), g + Offset(0.4f, -0.2f), g + Offset(0.4f, 1.2f), 0.35f) }
    }

    // ---------------------------------------------------------------- La parte viva

    fun DrawScope.puebloVivo(m: Momento, l: Luces, reloj: Float) {
        val viento = -(0.2f + 0.8f * m.viento)
        val frio = E.invierno(m) || l.noche > 0.4f || m.ficha.estacion == io.github.ruddy2003.vestigium.vidas.Estacion.OTONO
        // Los humos de las cocinas: con frío y a las horas de comer, más.
        for ((i, p) in HUMOS.withIndex()) if (frio || i == 0) humo(l, p, 26f - i * 3f, 6f, reloj, viento * 0.6f, 0.2f, E.perdido(m, 0.3f), 20 + i, 7, if (frio) 0.8f else 0.5f)
        // La chimenea de debajo de la galería: su humo cruza el cuadro por delante del puerto.
        if (frio || m.hora in 8.0..10.0 || m.hora in 12.0..14.5 || m.hora in 19.0..21.5) humo(l, CHIMENEA_CERCANA + Offset(0f, -5f), 74f, 15f, reloj, viento, 0.16f, 0f, 30, 10, if (frio) 0.75f else 0.5f)
    }
}
