package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * La selva de Mesoamérica: la lejanía en filas con bruma entre ellas, las copas que rodean la ciudad, la milpa de la
 * familia en su ladera y las casas de palma. También las piezas de árbol que usa el patio.
 */
internal object SelvaMeso {
    private val C = ColorMeso
    private const val H = EscenaMeso.H

    /** El contorno de la milpa de la familia, en la ladera de la derecha. */
    val MILPA = listOf(Offset(301f, 158f), Offset(314f, 146f), Offset(332f, 136f), Offset(354f, 129f), Offset(382f, 125f), Offset(382f, 187f), Offset(356f, 176f), Offset(330f, 165f))
    val MILPA_ZONA = Rect(301f, 125f, 382f, 187f)

    /** El verde de la estación: en secas amarillea y clarea; con las lluvias se cierra y oscurece. */
    private fun verde(m: Momento, base: Color): Color = if (EscenaMeso.lluvias(m)) lerp(base, C.SELVA_OSC, 0.12f) else lerp(base, Color(0xFF8A8A4C), 0.26f)

    // ---------------------------------------------------------------- Piezas de árbol

    /**
     * Una copa: nunca una bola. Son varios gajos de follaje de tamaños distintos, tendidos en parasol; cada uno con su
     * sombra debajo, su cuerpo y su lado de luz, y, si está cerca, hojas sueltas en el borde (en lote) que lo desflecan.
     */
    fun DrawScope.copa(l: Luces, r: Rayo, c: Offset, rx: Float, ry: Float, hoja: Color, lejos: Float, az: Azar) {
        val lado = if (r.lat >= 0f) 1f else -1f
        val luzK = 0.25f + 0.75f * r.dia
        val sombra = l.tono(mult(lerp(hoja, C.SELVA_OSC, 0.6f), r.color, 0.5f), lejos)
        val cuerpo = l.tono(hoja, lejos)
        val luz = l.tono(lerp(hoja, C.HOJA_LUZ, 0.62f), lejos).copy(alpha = 0.8f * luzK)
        val cerca = lejos < 0.45f && rx > 4f
        val n = if (cerca) 5 + az.entero(3) else 3
        val gx = FloatArray(n); val gy = FloatArray(n); val gr = FloatArray(n)
        for (i in 0 until n) {
            val t = if (n == 1) 0f else -1f + 2f * i / (n - 1f) + az.entre(-0.14f, 0.14f)
            gr[i] = rx * az.entre(0.3f, 0.52f) * (1f - 0.25f * abs(t))
            gx[i] = c.x + t * rx * 0.8f
            gy[i] = c.y - (1f - t * t) * ry * 0.5f + az.entre(-0.3f, 0.3f) * ry
        }
        for (i in 0 until n) drawOval(sombra, Offset(gx[i] - gr[i] * 1.06f, gy[i] - gr[i] * 0.3f), Size(gr[i] * 2.12f, gr[i] * 1.3f))
        for (i in 0 until n) drawOval(cuerpo, Offset(gx[i] - gr[i], gy[i] - gr[i] * 0.66f), Size(gr[i] * 2f, gr[i] * 1.2f))
        for (i in 0 until n) drawOval(luz, Offset(gx[i] - gr[i] * 0.66f + lado * gr[i] * 0.26f, gy[i] - gr[i] * 0.66f), Size(gr[i] * 1.32f, gr[i] * 0.62f))
        if (cerca) {
            val claras = ArrayList<Offset>(n * 5); val oscuras = ArrayList<Offset>(n * 4)
            for (i in 0 until n) {
                repeat(4) { val a = az.entre(3.3f, 6.1f); claras.add(Offset(gx[i] + cos(a) * gr[i] * az.entre(0.7f, 1.12f), gy[i] + sin(a) * gr[i] * az.entre(0.5f, 0.76f))) }
                repeat(3) { val a = az.entre(0.2f, 2.9f); oscuras.add(Offset(gx[i] + cos(a) * gr[i] * az.entre(0.8f, 1.14f), gy[i] + sin(a) * gr[i] * az.entre(0.4f, 0.66f))) }
            }
            drawPoints(oscuras, PointMode.Points, sombra, 2f - lejos * 1.6f, StrokeCap.Round)
            drawPoints(claras, PointMode.Points, l.tono(lerp(hoja, Color(0xFFD8E08C), 0.5f), lejos).copy(alpha = 0.9f * luzK), 1.7f - lejos * 1.4f, StrokeCap.Round)
        }
    }

    /** Una palma (de corozo o de guano): el tronco fino y las pencas arqueadas, cada una con su lado de luz. */
    fun DrawScope.palma(l: Luces, r: Rayo, pie: Offset, alto: Float, lejos: Float, az: Azar) {
        val lado = if (r.lat >= 0f) 1f else -1f
        val cima = pie + Offset(az.entre(-0.08f, 0.08f) * alto, -alto)
        val tronco = l.tono(lerp(C.MADERA, C.CORTEZA, 0.4f), lejos)
        drawLine(tronco, pie, cima, maxOf(0.7f, alto * 0.045f), StrokeCap.Round)
        drawLine(l.tono(lerp(C.MADERA, Color.White, 0.3f), lejos).copy(alpha = 0.5f * r.dia), pie + Offset(lado * alto * 0.012f, 0f), cima + Offset(lado * alto * 0.012f, 0f), maxOf(0.3f, alto * 0.014f))
        val largo = alto * 0.42f
        for (i in 0 until 9) {
            val a = -3.3f + i * 0.43f + az.entre(-0.1f, 0.1f)
            val fin = cima + Offset(cos(a) * largo, sin(a) * largo * 0.62f + largo * 0.34f * abs(cos(a)))
            val medio = cima + Offset(cos(a) * largo * 0.55f, sin(a) * largo * 0.62f - largo * 0.08f)
            val penca = Path().apply { moveTo(cima.x, cima.y); cubicTo(medio.x, medio.y - largo * 0.1f, medio.x, medio.y, fin.x, fin.y); cubicTo(medio.x, medio.y + largo * 0.16f, cima.x, cima.y + largo * 0.1f, cima.x, cima.y) }
            val claro = cos(a) * lado > -0.2f
            drawPath(penca, l.tono(if (claro) lerp(C.SELVA, C.HOJA_LUZ, 0.4f * r.dia + 0.1f) else lerp(C.SELVA, C.SELVA_OSC, 0.6f), lejos))
        }
        drawCircle(l.tono(C.SELVA_OSC, lejos), maxOf(0.8f, alto * 0.05f), cima)
    }

    /**
     * Una fila de selva: copas de tamaños y verdes distintos sobre una masa en sombra, con algún árbol que sobresale y
     * alguno en flor o pelado en secas. [cresta] da la altura de las copas en cada x.
     */
    private fun DrawScope.dosel(m: Momento, l: Luces, r: Rayo, x0: Float, x1: Float, cresta: (Float) -> Float, fondo: Float, tam: Float, lejos: Float, az: Azar, palmas: Int = 0, flores: Boolean = true, tapado: (Float, Float) -> Boolean = { _, _ -> false }) {
        val secas = !EscenaMeso.lluvias(m)
        val base = verde(m, C.SELVA)
        // La masa en sombra de debajo.
        val puntos = ArrayList<Offset>(); var x = x0
        while (x <= x1 + tam) { puntos.add(Offset(x, cresta(x) + tam * 0.25f)); x += tam * 0.8f }
        drawPath(curva(puntos, fondo), Brush.verticalGradient(listOf(l.tono(lerp(base, C.SELVA_OSC, 0.6f), lejos), l.tono(lerp(base, C.SELVA_OSC, 0.3f), lejos * 0.9f)), cresta(x0), fondo))
        // Dos filas de copas: la de detrás, más oscura; la de delante, más abajo. Alguno sobresale del dosel con su
        // tronco a la vista y la copa tendida, como las ceibas; alguno está en flor, y en secas alguno, pelado.
        for (fila in 0..1) {
            x = x0 - tam * 0.3f * fila
            while (x <= x1 + tam * 0.5f) {
                val rx = tam * az.entre(0.7f, 1.3f); val alza = az.entre(-0.36f, 0.36f) * tam
                val sobresale = fila == 0 && az.entero(6) == 0
                val cy = cresta(x) + alza + fila * tam * 0.62f - (if (sobresale) tam * az.entre(0.8f, 1.3f) else 0f)
                val tinte = az.real()
                var hoja = lerp(base, if (tinte < 0.5f) C.SELVA_OSC else Color(0xFF7A9450), abs(tinte - 0.5f) * 0.8f)
                val rara = az.entero(if (secas) 14 else 40)
                if (rara == 0 && flores) hoja = lerp(hoja, if (secas) lerp(Color(0xFFE2C25C), Color(0xFFD99AA8), az.real()) else Color(0xFFD8B656), 0.7f)      // un árbol en flor
                // Lo que queda entero detrás de un edificio no se pinta (sí se sortea, para que lo demás no cambie).
                if (tapado(x, cy - rx * 0.8f)) { repeat(if (lejos < 0.45f) 4 else 3) { az.real() }; x += rx * az.entre(0.9f, 1.36f); continue }
                if (sobresale) {
                    val pie = Offset(x + az.entre(-1f, 1f), cy + tam * 1.7f)
                    drawLine(l.tono(lerp(C.CORTEZA, C.MADERA, 0.3f), lejos), Offset(x, cy), pie, maxOf(0.6f, tam * 0.11f))
                    for (d in listOf(-0.5f, 0.5f)) drawLine(l.tono(lerp(C.CORTEZA, C.MADERA, 0.3f), lejos), Offset(x, cy + tam * 0.5f), Offset(x + d * rx, cy - tam * 0.05f), maxOf(0.4f, tam * 0.06f))
                    copa(l, r, Offset(x, cy - tam * 0.1f), rx * 1.25f, rx * 0.42f, hoja, lejos, az)
                }
                else if (secas && rara == 1 && lejos < 0.5f) pelado(l, Offset(x, cy + rx * 0.5f), rx * 1.5f, lejos, az)
                else copa(l, r, Offset(x, cy), rx, rx * az.entre(0.5f, 0.74f), hoja, lejos + fila * -0.03f, az)
                x += rx * az.entre(0.9f, 1.36f)
            }
        }
        repeat(palmas) { val px = az.entre(x0 + 4f, x1 - 4f); palma(l, r, Offset(px, cresta(px) + tam * az.entre(0.9f, 1.5f)), tam * az.entre(1.6f, 2.3f), lejos, az) }
    }

    /** Un árbol pelado de las secas: el tronco y unas ramas finas que se abren. */
    private fun DrawScope.pelado(l: Luces, pie: Offset, alto: Float, lejos: Float, az: Azar) {
        val c = l.tono(lerp(C.CORTEZA, C.MADERA, 0.35f), lejos)
        drawLine(c, pie, pie + Offset(0f, -alto * 0.5f), maxOf(0.5f, alto * 0.07f))
        for (i in 0 until 7) { val a = -2.7f + i * 0.38f + az.entre(-0.1f, 0.1f); val d = pie + Offset(0f, -alto * az.entre(0.35f, 0.55f)); drawLine(c, d, d + Offset(cos(a) * alto * 0.5f, sin(a) * alto * 0.5f), maxOf(0.35f, alto * 0.03f)) }
    }

    /** Una casa de palma sobre su plataforma: las paredes de palos y barro, y el techo alto con su fleco. */
    fun DrawScope.casaDePalma(l: Luces, r: Rayo, pie: Offset, ancho: Float, lejos: Float, az: Azar, lumbre: Float = 0f) {
        val lado = if (r.lat >= 0f) 1f else -1f
        val alto = ancho * 0.36f; val techo = ancho * 0.62f
        drawRect(l.tono(lerp(C.PIEDRA, C.ESTUCO, 0.3f), lejos), Offset(pie.x - ancho * 0.62f, pie.y - ancho * 0.1f), Size(ancho * 1.24f, ancho * 0.1f))
        val y0 = pie.y - ancho * 0.1f
        drawRect(Brush.horizontalGradient(listOf(l.cara(C.BARRO.let { lerp(it, C.PALMA, 0.5f) }, -lado * 0.7f, lejos), l.cara(lerp(C.BARRO, C.PALMA, 0.5f), lado * 0.8f, lejos)), pie.x - ancho / 2f, pie.x + ancho / 2f), Offset(pie.x - ancho / 2f, y0 - alto), Size(ancho, alto))
        drawRect(if (lumbre > 0f) lerp(l.tono(C.TINTA, lejos), C.LUMBRE, lumbre) else l.tono(C.TINTA, lejos), Offset(pie.x - ancho * 0.09f, y0 - alto * 0.8f), Size(ancho * 0.18f, alto * 0.8f))
        val paja = Path().apply {
            moveTo(pie.x - ancho * 0.62f, y0 - alto * 0.86f); cubicTo(pie.x - ancho * 0.4f, y0 - alto - techo * 0.6f, pie.x - ancho * 0.24f, y0 - alto - techo, pie.x - ancho * 0.16f, y0 - alto - techo)
            lineTo(pie.x + ancho * 0.16f, y0 - alto - techo); cubicTo(pie.x + ancho * 0.24f, y0 - alto - techo, pie.x + ancho * 0.4f, y0 - alto - techo * 0.6f, pie.x + ancho * 0.62f, y0 - alto * 0.86f); close()
        }
        val propio = lerp(C.PALMA, C.CENIZA, 0.18f + 0.2f * az.real())
        drawPath(paja, Brush.horizontalGradient(listOf(l.cara(propio, -lado * 0.9f, lejos), l.cara(propio, lado * 0.9f, lejos)), pie.x - ancho * 0.62f, pie.x + ancho * 0.62f))
        if (lejos < 0.5f) clipPath(paja) { for (i in 0..7) drawLine(l.tono(lerp(propio, C.TINTA, 0.35f), lejos).copy(alpha = 0.4f), Offset(pie.x - ancho * 0.6f + i * ancho * 0.17f, y0 - alto * 0.86f), Offset(pie.x - ancho * 0.16f + i * ancho * 0.046f, y0 - alto - techo), 0.4f) }
        drawLine(l.tono(lerp(propio, C.TINTA, 0.5f), lejos), Offset(pie.x - ancho * 0.62f, y0 - alto * 0.86f), Offset(pie.x + ancho * 0.62f, y0 - alto * 0.86f), maxOf(0.4f, ancho * 0.03f))
    }

    /** La bruma de la mañana entre dos filas de selva: bancos largos, más claros que el aire. */
    private fun DrawScope.bruma(m: Momento, l: Luces, y: Float, alto: Float, cuanta: Float, az: Azar) {
        val k = EscenaMeso.brumaDeLaManana(m) * cuanta
        if (k <= 0.02f) return
        val c = lerp(l.bruma, l.tono(Color.White), 0.45f)
        repeat(6) { mancha(Offset(az.entre(-10f, ANCHO + 10f), y + az.entre(-alto, alto) * 0.5f), az.entre(60f, 130f), alto * az.entre(0.6f, 1.1f), c, 0.9f * k) }
    }

    /** La bruma de la mañana, que se queda en lo hondo de la plaza y sobre la aguada hasta que el sol calienta. Va después de la ciudad. */
    fun DrawScope.brumaDeLaPlaza(m: Momento, l: Luces) {
        val k = EscenaMeso.brumaDeLaManana(m)
        if (k <= 0.02f) return
        val az = m.azar("bruma de la plaza"); val c = lerp(l.bruma, l.tono(Color.White), 0.5f)
        repeat(6) { mancha(Offset(az.entre(20f, ANCHO), az.entre(140f, 166f)), az.entre(50f, 110f), az.entre(3f, 7f), c, 0.6f * k) }
        mancha(Offset(300f, 194f), 52f, 4f, c, 0.4f * k); mancha(Offset(100f, 178f), 60f, 4f, c, 0.3f * k)
    }

    // ---------------------------------------------------------------- La lejanía

    fun DrawScope.lejania(m: Momento, l: Luces, r: Rayo) {
        val fijo = Azar(Semilla.de("meso", "selva"))
        val dia = m.azar("bruma")
        val base = verde(m, C.SELVA)
        // Las colinas del fondo, cubiertas de selva: tres filas, cada una más cerca y más verde.
        val loma1 = perfil(fijo, -10f, ANCHO + 10f, H - 2f, 26f, 0.25f, 26)
        monte(l, loma1, H + 6f, lerp(base, Color(0xFF8AA4A0), 0.5f), 0.9f)
        crestaDeCopas(l, r, fijo, loma1, 1.9f, 0.9f, base)
        // Otras pirámides, lejos, que salen de la selva «como islas».
        for ((x, y, k) in listOf(Triple(104f, 106.5f, 1f), Triple(176f, 111.5f, 0.7f), Triple(334f, 108f, 0.85f))) {
            val c = l.tono(lerp(C.ESTUCO, C.ROJO, 0.3f), 0.8f)
            // Escalonada: tres cuerpos, el templo y su crestería.
            for (i in 0..2) drawRect(c, Offset(x - (9.6f - i * 2.6f) * k, y + (7.4f - i * 3.7f) * k), Size((19.2f - i * 5.2f) * k, 3.8f * k))
            drawRect(c, Offset(x - 2.4f * k, y - 3.4f * k), Size(4.8f * k, 3.5f * k)); drawRect(l.tono(lerp(C.ESTUCO, C.ROJO, 0.45f), 0.78f), Offset(x - 1.5f * k, y - 7f * k), Size(3f * k, 3.7f * k))
            drawRect(l.tono(Color.White, 0.8f).copy(alpha = 0.5f), Offset(x - 1f * k, y), Size(2f * k, 11.2f * k))
            drawPath(poligono(Offset(x - 10f * k, y + 11f * k), Offset(x - 3.4f * k, y), Offset(x, y), Offset(x, y + 11f * k)), mult(c, r.color, 0.3f).copy(alpha = if (r.lat > 0f) 0.55f else 0f))
        }
        val loma2 = perfil(fijo, -10f, ANCHO + 10f, H + 3f, 19f, 0.3f, 30)
        monte(l, loma2, H + 10f, lerp(base, Color(0xFF7A9A8C), 0.3f), 0.74f)
        crestaDeCopas(l, r, fijo, loma2, 2.7f, 0.74f, base)
        bruma(m, l, H + 1f, 7f, 1f, dia)
        val loma3 = perfil(fijo, -10f, ANCHO + 10f, H + 8f, 14f, 0.35f, 34)
        monte(l, loma3, H + 16f, lerp(base, Color(0xFF6E9080), 0.15f), 0.58f)
        crestaDeCopas(l, r, fijo, loma3, 3.5f, 0.58f, base)
        // Milpas lejanas, robadas a la selva: claros del color de la estación; humean cuando se queman.
        for ((x, y, w) in listOf(Triple(58f, 126f, 17f), Triple(212f, 128.6f, 13f), Triple(286f, 125.6f, 11f))) drawOval(l.tono(colorDeLaMilpa(m), 0.6f), Offset(x - w, y - 2f), Size(w * 2f, 4.4f))
        bruma(m, l, H + 9f, 8f, 0.9f, dia)
        // La selva que rodea la ciudad, por detrás del palacio y del templo.
        dosel(m, l, r, -8f, ANCHO + 8f, { x -> 124f + 2.6f * sin(x * 0.05f + 1f) }, 146f, 7.6f, 0.46f, fijo, palmas = 3)
        bruma(m, l, 133f, 7f, 0.7f, dia)
        dosel(m, l, r, -8f, ANCHO + 8f, { x -> 136f + 3f * sin(x * 0.036f) - (if (x < 70f) (70f - x) * 0.1f else 0f) - (if (x > 300f) (x - 300f) * 0.16f else 0f) }, 166f, 10.5f, 0.3f, fijo, palmas = 5, tapado = { x, y -> CiudadMeso.tapa(x, y) })
        // Las casas de la gente, en las colinas, entre los árboles; en la de la derecha, la milpa de la familia.
        val lumbre = if (l.noche > 0.4f && m.hora !in 23.0..24.0 && m.hora >= 5.0 && !EscenaMeso.wayeb(m)) 0.8f else 0f
        casaDePalma(l, r, Offset(20f, 158f), 17f, 0.24f, fijo, lumbre); casaDePalma(l, r, Offset(84f, 154f), 13f, 0.3f, fijo)
        laMilpa(m, l, r)
        // El borde de la milpa con la plaza: una orla de monte bajo, que no la deja cortar en seco.
        for ((i, c) in listOf(Offset(303f, 160f), Offset(316f, 162.6f), Offset(331f, 167.4f), Offset(345f, 173f), Offset(359f, 179f), Offset(372f, 184.6f)).withIndex()) copa(l, r, c, 7f + (i % 2) * 2f, 3.6f + (i % 3), lerp(base, if (i % 2 == 0) C.SELVA_OSC else Color(0xFF7A9450), 0.4f), 0.16f, fijo)
        casaDePalma(l, r, Offset(343f, 133f), 14f, 0.28f, fijo, lumbre * 0.8f); casaDePalma(l, r, Offset(322f, 141f), 10f, 0.3f, fijo)
        // El troje de la milpa, sobre sus cuatro palos.
        val t = Offset(308f, 152f)
        for (dx in listOf(-3.4f, 3.4f)) drawLine(l.tono(C.MADERA, 0.2f), t + Offset(dx, 0f), t + Offset(dx, -4.6f), 0.7f)
        drawRect(l.tono(lerp(C.PALMA, C.MADERA, 0.4f), 0.2f), t + Offset(-4.4f, -8.6f), Size(8.8f, 4.2f)); drawPath(poligono(t + Offset(-5.6f, -8.4f), t + Offset(0f, -13.6f), t + Offset(5.6f, -8.4f)), l.cara(C.PALMA, if (r.lat >= 0f) 0.6f else -0.4f, 0.2f))
    }

    /**
     * Las copas que rematan una loma lejana y bajan por su ladera: puntos en lote, oscuros por un lado y claros por el
     * otro, con alguno que sobresale, para que se lea como selva y no como pasto.
     */
    private fun DrawScope.crestaDeCopas(l: Luces, r: Rayo, az: Azar, cresta: List<Offset>, tam: Float, lejos: Float, base: Color) {
        val lado = if (r.lat >= 0f) 1f else -1f
        val oscuras = ArrayList<Offset>(300); val claras = ArrayList<Offset>(300)
        for (i in 0 until cresta.size - 1) {
            val a = cresta[i]; val b = cresta[i + 1]
            var t = az.real() * 0.2f
            while (t < 1f) {
                val p = a + (b - a) * t
                // En la cresta y, más ralas, ladera abajo.
                for (fila in 0..2) {
                    if (fila > 0 && az.entero(3) == 0) continue
                    val c = Offset(p.x + az.entre(-tam, tam) * 0.5f, p.y + fila * tam * 1.5f + az.entre(-0.5f, 0.3f) * tam - (if (fila == 0 && az.entero(5) == 0) tam * 0.8f else 0f))
                    oscuras.add(c); claras.add(Offset(c.x + lado * tam * 0.3f, c.y - tam * 0.3f))
                }
                t += tam * az.entre(0.8f, 1.4f) / maxOf(1f, b.x - a.x)
            }
        }
        drawPoints(oscuras, PointMode.Points, l.tono(lerp(base, C.SELVA_OSC, 0.5f), lejos), tam * 1.3f, StrokeCap.Round)
        drawPoints(claras, PointMode.Points, l.tono(lerp(base, C.HOJA_LUZ, 0.5f), lejos).copy(alpha = 0.3f + 0.5f * r.dia), tam * 0.7f, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- La milpa

    /** El color del suelo de la milpa en cada etapa. */
    private fun colorDeLaMilpa(m: Momento): Color = when (EscenaMeso.milpa(m)) {
        EscenaMeso.Milpa.RASTROJO -> lerp(C.TIERRA, C.MAIZ, 0.45f)
        EscenaMeso.Milpa.ARDE -> lerp(C.CENIZA, C.TINTA, 0.4f)
        EscenaMeso.Milpa.CENIZA -> lerp(lerp(C.CENIZA, C.TIERRA, 0.4f), C.PIEDRA, 0.35f)
        EscenaMeso.Milpa.BAJA -> lerp(C.TIERRA, C.HOJA_LUZ, 0.3f + 0.5f * EscenaMeso.etapaDeLaMilpa(m))
        EscenaMeso.Milpa.ALTA -> if (EscenaMeso.mustia(m)) lerp(C.HOJA_LUZ, C.MAIZ, 0.6f) else lerp(C.SELVA, C.HOJA_LUZ, 0.7f)
        EscenaMeso.Milpa.DOBLADA -> lerp(C.MAIZ, C.TIERRA, 0.2f)
    }

    /** Dónde están los frentes de fuego de la quema, dentro de la milpa: los lee también la parte viva. */
    val FUEGOS = listOf(Offset(324f, 156f), Offset(340f, 144f), Offset(358f, 158f), Offset(352f, 136f), Offset(346f, 168f))

    /** La milpa de la familia, en su ladera: la tierra, los surcos que siguen la cuesta y el maíz de la etapa de hoy. */
    private fun DrawScope.laMilpa(m: Momento, l: Luces, r: Rayo) {
        val az = Azar(Semilla.de("meso", "milpa")); val forma = curva(MILPA + MILPA.first())
        val estado = EscenaMeso.milpa(m); val suelo = colorDeLaMilpa(m); val lado = if (r.lat >= 0f) 1f else -1f
        val tierra = if (estado == EscenaMeso.Milpa.BAJA || estado == EscenaMeso.Milpa.ALTA || estado == EscenaMeso.Milpa.DOBLADA) lerp(C.TIERRA, suelo, 0.35f) else suelo
        drawPath(forma, Brush.verticalGradient(listOf(l.tono(tierra, 0.3f), l.tono(lerp(tierra, C.TIERRA, 0.2f), 0.14f)), MILPA_ZONA.top, MILPA_ZONA.bottom))
        // El borde: troncos cortados y maleza, donde la milpa le gana a la selva.
        drawPath(forma, l.tono(lerp(C.TIERRA, C.SELVA_OSC, 0.5f), 0.2f), style = Stroke(1.6f))
        clipPath(forma) {
            // Los surcos, que bajan por la ladera.
            for (i in 0..16) { val x = 296f + i * 6f; drawLine(l.tono(lerp(tierra, C.TINTA, 0.3f), 0.2f).copy(alpha = 0.34f), Offset(x + 16f, 124f), Offset(x - 12f, 190f), 0.5f) }
            when (estado) {
                EscenaMeso.Milpa.RASTROJO -> {
                    // Cañas secas, cortas y dobladas, y hierba que vuelve.
                    val canas = ArrayList<Offset>(240)
                    repeat(150) { val p = Offset(az.entre(302f, 382f), az.entre(127f, 186f)); canas.add(p); canas.add(p + Offset(az.entre(-1.6f, 1.6f), -az.entre(1.6f, 3.4f))) }
                    drawPoints(canas, PointMode.Lines, l.tono(lerp(C.MAIZ, C.TIERRA, 0.3f), 0.2f), 0.6f)
                    repeat(20) { mancha(Offset(az.entre(306f, 380f), az.entre(130f, 184f)), az.entre(3f, 7f), az.entre(1f, 2f), l.tono(lerp(C.HOJA_LUZ, C.TIERRA, 0.5f), 0.2f), 0.4f) }
                }
                EscenaMeso.Milpa.ARDE, EscenaMeso.Milpa.CENIZA -> {
                    repeat(20) { mancha(Offset(az.entre(306f, 380f), az.entre(130f, 184f)), az.entre(4f, 10f), az.entre(1.4f, 3f), l.tono(if (it % 3 != 0) Color(0xFFD8D0C4) else C.TINTA, 0.2f), 0.4f) }
                    // Tocones y troncos quemados.
                    repeat(14) { val p = Offset(az.entre(308f, 378f), az.entre(132f, 184f)); drawLine(l.tono(C.TINTA, 0.16f), p, p + Offset(az.entre(-1f, 1f), -az.entre(2f, 4.6f)), 1.1f); if (it % 4 == 0) drawLine(l.tono(C.TINTA, 0.16f), p, p + Offset(az.entre(4f, 8f), az.entre(-1f, 1f)), 1f) }
                    if (estado == EscenaMeso.Milpa.ARDE) for (f in FUEGOS) { drawOval(C.LUMBRE.copy(alpha = 0.9f), Offset(f.x - 5f, f.y - 1f), Size(10f, 2.2f)); drawOval(Color(0xFFFFE08A), Offset(f.x - 2.4f, f.y - 0.7f), Size(4.8f, 1.2f)) }
                }
                EscenaMeso.Milpa.BAJA, EscenaMeso.Milpa.ALTA, EscenaMeso.Milpa.DOBLADA -> {
                    val e = EscenaMeso.etapaDeLaMilpa(m); val tumbada = EscenaMeso.tumbada(m); val corte = EscenaMeso.cosechado(m)
                    val altoBase = if (estado == EscenaMeso.Milpa.BAJA) 1.4f + 7f * e else if (estado == EscenaMeso.Milpa.ALTA) 5f + 5f * e else 8.6f
                    val hoja = if (estado == EscenaMeso.Milpa.DOBLADA) lerp(C.MAIZ, C.PALMA, 0.4f) else if (EscenaMeso.mustia(m)) lerp(C.HOJA_LUZ, C.MAIZ, 0.7f) else lerp(C.SELVA, C.HOJA_LUZ, 0.55f)
                    // Las matas, por hileras, de atrás adelante: cada una más grande que la de detrás.
                    for (fila in 0..13) {
                        val y = 129f + fila * 4.3f; val k = 0.5f + fila * 0.045f
                        val tallos = ArrayList<Offset>(80); val hojasL = ArrayList<Offset>(80); val hojasS = ArrayList<Offset>(80); val espigas = ArrayList<Offset>(40)
                        var x = 300f + az.entre(0f, 3f)
                        while (x < 384f) {
                            // Lo cosechado ya: de izquierda a derecha, quedan las cañas cortas.
                            val cortada = (x - 300f) / 84f < corte
                            val alto = (if (cortada) 2f else altoBase) * k * az.entre(0.8f, 1.15f)
                            val inclina = if (tumbada) alto * 0.9f * lado else az.entre(-0.6f, 0.6f)
                            val cima = Offset(x + inclina, y - (if (tumbada) alto * 0.3f else alto))
                            tallos.add(Offset(x, y)); tallos.add(cima)
                            if (!cortada && alto > 2.4f) {
                                val h1 = Offset(x + inclina * 0.5f, y - alto * 0.55f)
                                hojasL.add(h1); hojasL.add(h1 + Offset(lado * alto * 0.34f, -alto * 0.1f)); hojasS.add(h1); hojasS.add(h1 + Offset(-lado * alto * 0.34f, -alto * 0.06f))
                                if (estado == EscenaMeso.Milpa.DOBLADA) { hojasS.add(cima); hojasS.add(cima + Offset(lado * 1.6f, alto * 0.34f)) } else if (e > 0.7f) espigas.add(cima + Offset(0f, -0.6f))
                            }
                            x += 3.1f * k * az.entre(0.8f, 1.2f)
                        }
                        drawPoints(tallos, PointMode.Lines, l.tono(lerp(hoja, C.SELVA_OSC, 0.3f), 0.2f), 0.9f * k)
                        drawPoints(hojasS, PointMode.Lines, l.tono(lerp(hoja, C.SELVA_OSC, 0.45f), 0.2f), 0.9f * k, StrokeCap.Round)
                        drawPoints(hojasL, PointMode.Lines, l.tono(lerp(hoja, Color(0xFFE6E89A), 0.4f * r.dia), 0.2f), 0.9f * k, StrokeCap.Round)
                        if (espigas.isNotEmpty()) drawPoints(espigas, PointMode.Points, l.tono(Color(0xFFF0DC94), 0.2f), 1.1f * k, StrokeCap.Round)
                    }
                    // Lo cosechado se amontona al pie.
                    if (corte > 0.05f) for (i in 0 until (1 + corte * 4).toInt()) { val p = Offset(312f + i * 9f, 166f + i * 3.4f); drawPath(poligono(p + Offset(-3.4f, 0f), p + Offset(0f, -3.2f), p + Offset(3.4f, 0f)), l.tono(Color(0xFFE8C860), 0.14f)) }
                }
            }
        }
    }

    // ---------------------------------------------------------------- Lo que rodea la plaza, por delante

    /** Los árboles de la ladera entre el patio y la plaza, a los dos lados: grandes, con su tronco, y palmas. Van después de la ciudad. */
    fun DrawScope.orlaDeLaPlaza(m: Momento, l: Luces, r: Rayo) {
        val fijo = Azar(Semilla.de("meso", "orla"))
        dosel(m, l, r, -10f, 34f, { x -> 176f - (40f - x) * 0.3f }, 216f, 14f, 0.1f, fijo, palmas = 0, flores = false)
        palma(l, r, Offset(47f, 214f), 39f, 0.08f, fijo); palma(l, r, Offset(60f, 215f), 27f, 0.08f, fijo)
        dosel(m, l, r, 344f, ANCHO + 10f, { x -> 201f - (x - 344f) * 0.16f }, 216f, 12f, 0.1f, fijo, palmas = 0, flores = false)
        palma(l, r, Offset(337f, 214f), 30f, 0.08f, fijo)
        // Matas al pie de la plataforma, que rompen su borde.
        val matas = ArrayList<Offset>(60)
        repeat(30) { val x = fijo.entre(92f, 334f); matas.add(Offset(x, 213f)); matas.add(Offset(x + fijo.entre(-2f, 2f), 213f - fijo.entre(1.6f, 4.6f))) }
        drawPoints(matas, PointMode.Lines, l.tono(lerp(verde(m, C.SELVA), C.HOJA_LUZ, 0.3f), 0.06f), 0.9f, StrokeCap.Round)
    }
}
