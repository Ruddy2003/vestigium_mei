package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hierbaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hojaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo que se ve valle abajo desde el patio, por planos (regla 10), de lo más lejano a lo más cercano:
 * 1. el mar, al final del valle, y los hornos de la costa;
 * 2. los páramos, en tres filas, cada una más oscura y con menos bruma;
 * 3. las laderas, con sus prados partidos por muros, las hileras de casas de ladrillo que las suben, la iglesia y la mina;
 * 4. **el viaducto nuevo**, que cruza el valle entero por detrás del depósito, alto, de piedra rubia; la línea pasa
 *    por debajo, camino del mar;
 * 5. la fábrica de al lado (la nave de dientes de sierra, la casa de máquinas y la chimenea grande), la taberna y los
 *    árboles del borde del patio.
 */
internal object ValleVapor {
    private val C = ColorVapor
    private const val H = EscenaVapor.H
    private val FUGA = EscenaVapor.FUGA

    /** Donde acaba el balasto del patio y empieza lo de fuera. */
    const val BORDE = H + 28f

    /** Donde pisa el viaducto: hasta ahí llegan las vías que huyen (ver `DepositoVapor.T_FIN`). */
    const val PIE = H + 12f

    // ---- El viaducto: nueve apoyos, ocho arcos. Las medidas, en unidades del lienzo.
    private const val VX0 = 80f
    private const val VANO = 38f
    private const val PILA = 8f
    private const val ARRANQUE = 91.5f
    private const val CLAVE = 76.5f
    private const val TABLERO = 71f
    private const val PRETIL = 68.2f

    /** Cuánto se ve del grueso del viaducto: su cara de atrás es la de delante, encogida hacia el punto de fuga. */
    private const val GRUESO = 0.05f
    private fun apoyo(i: Int) = VX0 + i * VANO
    private fun atras(p: Offset) = p + (FUGA - p) * GRUESO

    // ---- Las crestas de los páramos (segunda fila) y de las laderas cercanas, de fuera hacia el centro del valle.
    private val PARAMO_IZQ = listOf(Offset(-6f, 64f), Offset(28f, 58f), Offset(60f, 66f), Offset(92f, 71f), Offset(112f, 77f), Offset(128f, 75f), Offset(152f, 86f), Offset(186f, 98f), Offset(212f, 109f), Offset(233f, 118f))
    private val PARAMO_DER = listOf(Offset(240f, 118f), Offset(262f, 107f), Offset(288f, 97f), Offset(306f, 92f), Offset(322f, 86f), Offset(340f, 81f), Offset(356f, 72f), Offset(372f, 73f), Offset(388f, 66f))
    private val LADERA_IZQ = listOf(Offset(-6f, 72f), Offset(40f, 78f), Offset(84f, 85f), Offset(124f, 93f), Offset(160f, 102f), Offset(196f, 112f), Offset(222f, 120f), Offset(240f, 125f))
    private val LADERA_DER = listOf(Offset(232f, 125f), Offset(256f, 120f), Offset(285f, 113f), Offset(315f, 104f), Offset(348f, 93f), Offset(388f, 81f))

    private fun alturaEn(pts: List<Offset>, x: Float): Float {
        if (x <= pts.first().x) return pts.first().y
        for (i in 0 until pts.size - 1) if (x <= pts[i + 1].x) { val t = (x - pts[i].x) / (pts[i + 1].x - pts[i].x); return pts[i].y + (pts[i + 1].y - pts[i].y) * t }
        return pts.last().y
    }

    /** La altura de la cresta de la ladera cercana en [x]. */
    private fun ladera(x: Float): Float = if (x < 236f) alturaEn(LADERA_IZQ, x) else alturaEn(LADERA_DER, x)

    /** Una hilera de casas: de [x0] a [x1], [baja] por debajo de la cresta de su ladera. */
    private class Fila(val x0: Float, val x1: Float, val baja: Float, val alto: Float, val lejos: Float)

    private val FILAS = listOf(
        Fila(98f, 122f, 6f, 5f, 0.56f), Fila(142f, 157f, 6f, 5f, 0.56f), Fila(100f, 134f, 17f, 5.6f, 0.52f),
        Fila(278f, 318f, 6f, 5.4f, 0.56f), Fila(338f, 386f, 6f, 5.4f, 0.56f), Fila(344f, 386f, 18f, 6f, 0.5f), Fila(244f, 262f, 5f, 4.6f, 0.58f),
    )

    /** La chimenea grande de la fábrica de al lado. */
    private val CHIMENEA = Offset(327f, BORDE)
    private const val CHIMENEA_ALTO = 65f

    /** El color del páramo según la estación: el brezo florece al final del verano. */
    private fun paramo(m: Momento): Color = when (m.ficha.estacion) {
        Estacion.PRIMAVERA -> lerp(C.BREZO, C.PRADO, 0.5f)
        Estacion.VERANO -> lerp(C.BREZO, Color(0xFF9A5E92), 0.4f)
        Estacion.OTONO -> lerp(C.BREZO, C.MADERA, 0.45f)
        Estacion.INVIERNO -> lerp(C.BREZO, C.BALASTO, 0.5f)
        Estacion.SECAS, Estacion.LLUVIAS, Estacion.INUNDACION, Estacion.SIEMBRA, Estacion.COSECHA, Estacion.NINGUNA -> C.BREZO   // el calendario de Albert no las usa
    }.let { if (EscenaVapor.nevado(m)) lerp(it, C.NIEVE, 0.72f) else it }

    private fun prado(m: Momento): Color = (if (EscenaVapor.nevado(m)) lerp(C.PRADO, C.NIEVE, 0.78f) else lerp(C.PRADO, hierbaDe(m.ficha.estacion), 0.5f))

    /** El humo pardo del carbón de fábrica, con la luz del momento. */
    private fun pardo(l: Luces, lejos: Float): Color = l.tono(lerp(C.MADERA, C.HOLLIN, 0.55f), lejos)

    /** Cuántas ventanas tienen luz, de 0 a 1. */
    private fun ventanas(m: Momento, l: Luces): Float = if (l.noche < 0.2f) 0f else if (EscenaVapor.fiesta(m) || EscenaVapor.hogueras(m)) 0.7f else if (m.hora >= 22.5 || m.hora < 4.0) 0.1f else if (m.hora < 6.5) 0.45f else 0.6f

    /** La fábrica de al lado trabaja: de seis a seis. */
    private fun fabrica(m: Momento): Boolean = m.hora >= 6.0 && m.hora < 18.0

    /** Dónde arden las hogueras de Guy Fawkes. */
    private val HOGUERAS = listOf(Offset(112f, 103f), Offset(354f, 105f), Offset(283f, 143f))

    // ---------------------------------------------------------------- La parte quieta

    fun DrawScope.valle(m: Momento, l: Luces, r: Rayo) {
        val fijo = Azar(Semilla.de("vapor", "valle"))
        val viento = EscenaVapor.soplo(m)
        // 1. El mar, al final del valle, entre dos cabos; y en la costa, los hornos.
        drawRect(l.tono(lerp(Color(0xFF8FB0B8), C.HUESO, 0.2f), 0.82f), Offset(224f, H - 0.8f), Size(30f, 7f))
        for (i in 0..4) drawLine(l.tono(Color.White, 0.6f).copy(alpha = 0.4f * (1f - l.noche)), Offset(228f + i * 4.6f + fijo.entre(0f, 2f), H + 0.4f + (i % 3) * 1f), Offset(231f + i * 4.6f, H + 0.4f + (i % 3) * 1f), 0.4f)
        monte(l, listOf(Offset(128f, 108f), Offset(152f, 104f), Offset(176f, 107f), Offset(198f, 105f), Offset(218f, 113f), Offset(234f, 119.8f)), H + 8f, paramo(m), 0.9f)
        monte(l, listOf(Offset(243f, 119.8f), Offset(256f, 113f), Offset(274f, 109f), Offset(292f, 111f), Offset(316f, 105f), Offset(352f, 102f)), H + 8f, paramo(m), 0.9f)
        hornosDeLaCosta(m, l, viento)
        mancha(Offset(238f, H + 2f), 80f, 5f, l.bruma, 0.4f * (1f - 0.5f * l.noche))

        // 2. Los páramos, más cerca: la segunda fila, con la bruma al pie.
        monte(l, PARAMO_IZQ, BORDE, paramo(m), 0.72f)
        monte(l, PARAMO_DER, BORDE, paramo(m), 0.7f)
        // Una cantera en el páramo de la derecha, de donde baja la piedra rubia. [Diseño propio]
        drawPath(poligono(listOf(Offset(300f, 97f), Offset(309f, 93.4f), Offset(316f, 94f), Offset(318f, 100f), Offset(304f, 101f))), l.tono(lerp(C.PIEDRA, paramo(m), 0.3f), 0.68f))
        drawLine(l.tono(lerp(C.PIEDRA, C.HOLLIN, 0.4f), 0.68f).copy(alpha = 0.6f), Offset(302f, 99f), Offset(317f, 97f), 0.4f)
        for ((x, y, w) in listOf(Triple(150f, 99f, 60f), Triple(318f, 102f, 64f))) mancha(Offset(x, y), w, 4.5f, l.bruma, 0.34f * (1f - 0.5f * l.noche))

        // 3. Las laderas: prados, muros de piedra seca y, arriba, el brezo.
        laderas(m, l, r, fijo)
        puebloDeLasLaderas(m, l, r, fijo, viento)
        // La línea, que sigue más allá del viaducto, camino del mar.
        drawPath(poligono(Offset(237.4f, PIE), Offset(258.4f, PIE), Offset(239f, H + 4f), Offset(236.4f, H + 4f)), l.tono(lerp(C.BALASTO, C.HUESO, 0.2f), 0.5f))
        for (x in listOf(238.4f, 247.6f, 257f)) drawLine(l.tono(C.HIERRO, 0.5f), Offset(x, PIE), Offset(237f + (x - 238.4f) * 0.1f, H + 4f), 0.4f)

        // 4. El viaducto.
        viaducto(m, l, r)

        // 5. Lo de al lado del patio: la fábrica, la taberna y los árboles.
        cercania(m, l, r)
    }

    /** Los altos hornos de la costa: unas chimeneas finas, su humo tendido y, de noche, el resplandor que no se apaga. */
    private fun DrawScope.hornosDeLaCosta(m: Momento, l: Luces, viento: Float) {
        val lejos = 0.74f; val negro = l.tono(lerp(C.HOLLIN, C.LADRILLO, 0.2f), lejos)
        drawRect(negro, Offset(239f, H - 1.6f), Size(22f, 2.6f))
        for (i in 0..3) drawPath(poligono(Offset(240f + i * 4f, H - 1.6f), Offset(240f + i * 4f, H - 3.6f), Offset(244f + i * 4f, H - 1.6f)), negro)
        drawPath(poligono(Offset(256f, H + 1f), Offset(257f, H - 8f), Offset(260f, H - 8f), Offset(261f, H + 1f)), negro)
        val chimeneas = listOf(241.5f to 12f, 246f to 15f, 251f to 10f, 263.4f to 13f)
        for ((x, alto) in chimeneas) drawLine(negro, Offset(x, H + 1f), Offset(x, H - alto), 0.9f)
        with(HumoVapor) { for ((i, c) in chimeneas.withIndex()) penacho(Offset(c.first, H - c.second), 22f, 1.3f, viento, 2.4f + i, pardo(l, 0.7f), 31 + i, alfa = 0.42f, brasa = if (l.noche > 0.3f) Color(0xFFFF7A40) else null, n = 8) }
        if (l.noche > 0.25f) { resplandor(Offset(252f, H - 5f), 34f, 0.62f * l.noche, Color(0xFFFF7A40)); resplandor(Offset(258.5f, H - 8.4f), 7f, 0.9f * l.noche, C.FUEGO); drawCircle(lerp(C.FUEGO, Color.White, 0.4f), 0.9f, Offset(258.5f, H - 8.4f)) }
    }

    private fun DrawScope.laderas(m: Momento, l: Luces, r: Rayo, fijo: Azar) {
        val luzDer = r.lat >= 0f
        val muro = lerp(C.PIEDRA, C.HIERRO, 0.45f)
        for ((pts, izquierda) in listOf(LADERA_IZQ to true, LADERA_DER to false)) {
            val forma = curva(pts, BORDE + 2f)
            val arriba = pts.minOf { it.y }
            drawPath(forma, Brush.verticalGradient(0f to l.tono(lerp(paramo(m), prado(m), 0.25f), 0.6f), 0.4f to l.tono(prado(m), 0.52f), 1f to l.tono(lerp(prado(m), C.PIEDRA, 0.16f), 0.34f), startY = arriba, endY = BORDE))
            clipPath(forma) {
                // La ladera que mira al sol se aclara; la otra queda en sombra.
                val alSol = izquierda == luzDer
                drawRect(if (alSol) C.LUZ.copy(alpha = 0.14f * r.dia * r.contraste) else mult(Color.White, r.color, 0.9f).copy(alpha = 0.2f * r.dia * r.contraste), Offset(pts.first().x, arriba), Size(pts.last().x - pts.first().x, BORDE - arriba))
                // Las parcelas, cada una de su verde, y los muros que bajan y los que siguen la ladera.
                val x0 = pts.first().x; val cae = if (izquierda) 7f else -7f
                repeat(13) { i -> val x = x0 + 6f + i * 19f + fijo.entre(-4f, 4f); val y = ladera(x + 8f) + 3f
                    drawPath(poligono(Offset(x, y), Offset(x + 17f, y), Offset(x + 17f + cae, BORDE), Offset(x + cae, BORDE)), l.tono(lerp(prado(m), if (i % 3 == 0) paramo(m) else if (i % 3 == 1) C.PIEDRA else hierbaDe(m.ficha.estacion), 0.16f + 0.1f * (i % 2)), 0.5f).copy(alpha = 0.6f))
                    drawLine(l.tono(muro, 0.5f).copy(alpha = 0.7f), Offset(x, y - 2f), Offset(x + cae, BORDE), 0.5f) }
                for (baja in listOf(11f, 24f, 38f)) drawPath(curva(pts.map { it + Offset(0f, baja) }), l.tono(muro, 0.48f).copy(alpha = 0.6f), style = Stroke(0.5f))
                // Arbolillos junto a los muros.
                repeat(12) { val x = x0 + fijo.entre(10f, 236f); val y = ladera(x) + fijo.entre(9f, 30f); arbolito(m, l, r, Offset(x, y), fijo.entre(2.2f, 3.4f), 0.5f) }
            }
        }
    }

    /** Un árbol lejano: el tronco y tres pinceladas. */
    private fun DrawScope.arbolito(m: Momento, l: Luces, r: Rayo, pie: Offset, w: Float, lejos: Float) {
        val hoja = hojaDe(m.ficha.estacion)?.let { lerp(it, C.PRADO, 0.4f) }
        drawLine(l.tono(lerp(C.HOLLIN, C.MADERA, 0.4f), lejos), pie, pie + Offset(0f, -w * 1.2f), 0.5f)
        if (hoja == null) { for (dx in listOf(-0.8f, 0f, 0.8f)) drawLine(l.tono(lerp(C.HOLLIN, C.MADERA, 0.4f), lejos).copy(alpha = 0.8f), pie + Offset(0f, -w), pie + Offset(dx * w, -w * 2.2f), 0.3f); return }
        val lado = if (r.lat >= 0f) 1f else -1f
        drawOval(l.tono(mult(hoja, r.color, 0.7f * r.contraste), lejos), Offset(pie.x - w, pie.y - w * 2.3f), Size(w * 2f, w * 1.7f))
        drawOval(l.tono(if (EscenaVapor.nevado(m)) lerp(hoja, C.NIEVE, 0.6f) else lerp(hoja, C.LUZ, 0.2f * r.dia), lejos), Offset(pie.x - w * 0.7f + lado * w * 0.3f, pie.y - w * 2.5f), Size(w * 1.4f, w * 1.1f))
    }

    /** Dónde está la boca de la chimenea de la casa [i] de una hilera, y cuántas casas tiene. */
    private fun casas(f: Fila): Int = ((f.x1 - f.x0) / (f.alto * 1.08f)).toInt().coerceAtLeast(1)

    /** Una hilera de casas de ladrillo, iguales, que sube la ladera a escalones: tejado de pizarra y una chimenea por casa. */
    private fun DrawScope.hilera(m: Momento, l: Luces, r: Rayo, f: Fila, az: Azar, encendidas: Float, viento: Float) {
        val luzDer = r.lat >= 0f; val nieve = EscenaVapor.nevado(m)
        val n = casas(f); val w = (f.x1 - f.x0) / n; val a = f.alto
        val muro = lerp(C.LADRILLO, C.HOLLIN, 0.2f); val teja = if (nieve) lerp(C.PIZARRA, C.NIEVE, 0.8f) else C.PIZARRA
        val claro = l.tono(lerp(muro, C.LUZ, 0.2f * r.dia * r.contraste), f.lejos); val oscuro = l.tono(mult(muro, r.color, 0.75f * r.contraste), f.lejos)
        for (i in 0 until n) {
            val x = f.x0 + i * w; val y = ladera(x + w / 2f) + f.baja
            drawRect(claro, Offset(x, y - a), Size(w + 0.3f, a + 1.6f))
            // El testero que asoma en cada escalón, del lado de la sombra.
            drawRect(oscuro, Offset(if (luzDer) x else x + w * 0.84f, y - a), Size(w * 0.16f, a + 1.6f))
            drawPath(poligono(Offset(x - 0.3f, y - a), Offset(x + w + 0.3f, y - a), Offset(x + w - 0.2f, y - a * 1.44f), Offset(x + 0.2f, y - a * 1.44f)), l.tono(lerp(teja, C.LUZ, 0.16f * r.dia), f.lejos))
            drawLine(l.tono(lerp(teja, C.HUESO, 0.5f), f.lejos).copy(alpha = 0.7f), Offset(x + 0.2f, y - a * 1.44f), Offset(x + w - 0.2f, y - a * 1.44f), 0.35f)
            drawRect(oscuro.copy(alpha = 0.6f), Offset(x, y - a), Size(w, 0.5f))
            // La chimenea con su sombrerete, la puerta y las ventanas.
            drawRect(l.tono(lerp(C.LADRILLO_OSC, C.HOLLIN, 0.3f), f.lejos), Offset(x + w * 0.4f, y - a * 1.8f), Size(w * 0.22f, a * 0.4f))
            drawRect(l.tono(lerp(C.COBRE, C.HOLLIN, 0.3f), f.lejos), Offset(x + w * 0.45f, y - a * 1.92f), Size(w * 0.12f, a * 0.13f))
            drawRect(l.tono(lerp(C.HOLLIN, C.MADERA, 0.3f), f.lejos), Offset(x + w * 0.14f, y - a * 0.46f), Size(w * 0.2f, a * 0.46f))
            for ((dx, dy) in listOf(0.54f to 0.5f, 0.54f to 0.92f, 0.14f to 0.92f)) {
                val luz = encendidas > 0f && az.real() < encendidas * (if (dy > 0.6f) 0.5f else 1f)
                val v = Offset(x + w * dx, y - a * dy)
                drawRect(if (luz) lerp(C.GAS, C.FUEGO, 0.25f) else l.tono(lerp(C.HOLLIN, C.PIZARRA, 0.5f), f.lejos), v, Size(w * 0.24f, a * 0.24f))
                if (luz) resplandor(v + Offset(w * 0.12f, a * 0.12f), a * 0.9f, 0.34f * l.noche, C.GAS)
                drawRect(l.tono(C.PIEDRA, f.lejos).copy(alpha = 0.7f), v + Offset(-0.2f, a * 0.24f), Size(w * 0.28f, 0.4f))
            }
            // El humo de su lumbre: una casa sí y otra no. Va aquí, quieto, porque el viaducto pasa por delante.
            if (i % 2 == 0) with(HumoVapor) { hilo(Offset(x + w * 0.51f, y - a * 1.94f), a * 1.5f, viento, 1.3f + i * 0.61f, l.tono(Color(0xFFD8D2C6), f.lejos), i) }
        }
        // Una farola de gas cada tres casas.
        if (l.noche > 0.3f) for (i in 0 until n step 3) { val x = f.x0 + i * w; val c = Offset(x, ladera(x) + f.baja - a * 0.5f); drawCircle(C.GAS.copy(alpha = 0.9f * l.noche), 0.5f, c); resplandor(c, 4.4f, 0.5f * l.noche, C.GAS) }
    }

    /** Una chimenea de fábrica, de ladrillo: se estrecha hacia arriba, tiene su lado en sombra, sus zunchos y su corona. [caida]: el vendaval la dejó desmochada. */
    private fun DrawScope.chimenea(l: Luces, r: Rayo, pie: Offset, alto: Float, abajo: Float, arriba: Float, lejos: Float, caida: Boolean = false) {
        val h = if (caida) alto * 0.62f else alto; val arr = if (caida) abajo + (arriba - abajo) * 0.62f else arriba
        val luzDer = r.lat >= 0f; val ladrillo = lerp(C.LADRILLO, C.HOLLIN, 0.3f)
        drawPath(poligono(pie + Offset(-abajo, 0f), pie + Offset(-arr, -h), pie + Offset(arr, -h), pie + Offset(abajo, 0f)), Brush.verticalGradient(0f to l.tono(lerp(ladrillo, C.HOLLIN, 0.35f), lejos), 0.3f to l.tono(lerp(ladrillo, C.LUZ, 0.14f * r.dia), lejos), 1f to l.tono(ladrillo, lejos - 0.04f), startY = pie.y - h, endY = pie.y))
        val s = if (luzDer) -1f else 1f
        drawPath(poligono(pie + Offset(s * abajo, 0f), pie + Offset(s * arr, -h), pie + Offset(s * arr * 0.1f, -h), pie + Offset(s * abajo * 0.1f, 0f)), l.tono(mult(ladrillo, r.color, 0.75f * (0.4f + 0.6f * r.contraste)), lejos).copy(alpha = 0.85f))
        if (alto > 30f) for (k in 1..4) { val t = k / 5f; val w = abajo + (arr - abajo) * t; drawLine(l.tono(C.HIERRO, lejos).copy(alpha = 0.6f), pie + Offset(-w, -h * t), pie + Offset(w, -h * t), 0.4f) }
        if (!caida) { drawRect(l.tono(lerp(ladrillo, C.PIEDRA, 0.3f), lejos), pie + Offset(-arr - 0.9f, -h - 0.4f), Size(arr * 2f + 1.8f, 1.6f)); drawRect(l.tono(C.HOLLIN, lejos), pie + Offset(-arr - 0.3f, -h - 2f), Size(arr * 2f + 0.6f, 1.6f)) }
        else drawPath(poligono(pie + Offset(-arr, -h), pie + Offset(-arr * 0.3f, -h - 2f), pie + Offset(arr * 0.2f, -h - 0.4f), pie + Offset(arr, -h - 1.6f), pie + Offset(arr, -h)), l.tono(ladrillo, lejos))
    }

    /** Lo que hay en las laderas, al otro lado del viaducto: las hileras, la iglesia, la capilla, la mina y la fábrica del fondo. */
    private fun DrawScope.puebloDeLasLaderas(m: Momento, l: Luces, r: Rayo, fijo: Azar, viento: Float) {
        val enc = ventanas(m, l); val nieve = EscenaVapor.nevado(m)
        val az = m.azar("ventanas", if (m.hora >= 22.5 || m.hora < 4.0) 1 else 0)
        val piedra = lerp(C.PIEDRA, C.HOLLIN, 0.32f); val teja = if (nieve) lerp(C.PIZARRA, C.NIEVE, 0.8f) else C.PIZARRA

        // ---- La mina, en la ladera de la izquierda: la casa de máquinas con su chimenea, el castillete y la escombrera.
        val lm = 0.56f; val hierro = l.tono(C.HIERRO, lm)
        val escoria = poligono(listOf(Offset(184f, 111f), Offset(195f, 100f), Offset(202f, 99f), Offset(218f, 113f)))
        drawPath(escoria, l.tono(lerp(C.CARBONILLA, if (nieve) C.NIEVE else C.BREZO, 0.25f), lm))
        drawPath(poligono(if (r.lat >= 0f) listOf(Offset(184f, 111f), Offset(195f, 100f), Offset(198f, 99.6f), Offset(194f, 111.4f)) else listOf(Offset(218f, 113f), Offset(202f, 99f), Offset(198f, 99.6f), Offset(204f, 112f))), l.tono(mult(C.CARBONILLA, r.color, 0.6f), lm).copy(alpha = 0.6f))
        val cm = Offset(160f, ladera(165f) + 3f)
        chimenea(l, r, cm + Offset(2.4f, 0f), 23f, 1.3f, 0.8f, lm, caida = EscenaVapor.vendaval(m))
        drawRect(l.tono(lerp(C.LADRILLO, C.HOLLIN, 0.25f), lm), cm + Offset(4.4f, -10f), Size(8f, 10f)); drawPath(poligono(cm + Offset(3.8f, -10f), cm + Offset(8.4f, -13.4f), cm + Offset(13f, -10f)), l.tono(teja, lm))
        drawRect(if (l.noche > 0.3f) C.GAS.copy(alpha = 0.85f) else l.tono(C.HOLLIN, lm), cm + Offset(7f, -7f), Size(2.4f, 3.4f))
        val cas = Offset(179f, 110f)
        for (dx in listOf(-4.6f, 4.6f)) drawLine(hierro, cas + Offset(dx, 0f), cas + Offset(dx * 0.4f, -20f), 0.9f)
        drawLine(hierro, cas + Offset(-3.6f, -7f), cas + Offset(3.6f, -7f), 0.6f); drawLine(hierro, cas + Offset(-2.8f, -14f), cas + Offset(2.8f, -14f), 0.6f)
        drawLine(hierro, cas + Offset(-3.6f, -7f), cas + Offset(2.8f, -14f), 0.4f); drawLine(hierro, cas + Offset(4.6f, 0f), cas + Offset(-3.6f, -7f), 0.4f)
        for (dx in listOf(-2.5f, 2.5f)) { drawCircle(hierro, 2.5f, cas + Offset(dx, -21.4f), style = Stroke(0.7f)); drawLine(hierro, cas + Offset(dx - 2.5f, -21.4f), cas + Offset(dx + 2.5f, -21.4f), 0.3f); drawLine(hierro, cas + Offset(dx, -23.9f), cas + Offset(dx, -18.9f), 0.3f) }
        drawLine(l.tono(C.HOLLIN, lm), cas + Offset(-5f, -21.4f), cm + Offset(12f, -8f), 0.4f)
        with(HumoVapor) { penacho(cm + Offset(2.4f, if (EscenaVapor.vendaval(m)) -15f else -24.4f), 22f, 1.4f, viento, 5.3f, pardo(l, 0.54f), 41, alfa = 0.5f, n = 8) }

        // ---- Las hileras, que suben las dos laderas a escalones.
        for (f in FILAS) hilera(m, l, r, f, az, enc, viento)
        // Su casa, en la calle de los ferroviarios, desde que se mudan: la última de la hilera de abajo, con luz al anochecer.
        if (EscenaVapor.suCasa(m) && l.noche > 0.2f && m.hora >= 17.5 && m.hora < 22.0) { val f = FILAS[5]; val c = Offset(f.x0 + 1.6f, ladera(f.x0 + 3f) + f.baja - f.alto * 0.46f); drawRect(lerp(C.GAS, Color.White, 0.2f), c, Size(1.8f, 1.7f)); resplandor(c + Offset(0.9f, 0.8f), 8f, 0.6f * l.noche, C.GAS) }

        // ---- La iglesia, con su torre y su aguja, entre las casas de la izquierda.
        val ig = Offset(124f, ladera(131f) + 5f)
        drawRect(l.tono(piedra, 0.56f), ig + Offset(0f, -7.4f), Size(10f, 7.4f)); drawPath(poligono(ig + Offset(-0.5f, -7.4f), ig + Offset(0.6f, -11f), ig + Offset(10f, -11f), ig + Offset(10f, -7.4f)), l.tono(teja, 0.56f))
        drawRect(l.tono(lerp(piedra, C.HOLLIN, 0.1f), 0.56f), ig + Offset(10f, -12.4f), Size(5f, 12.4f)); drawRect(l.tono(mult(piedra, r.color, 0.7f * r.contraste), 0.56f), ig + Offset(if (r.lat >= 0f) 10f else 13.6f, -12.4f), Size(1.4f, 12.4f))
        drawPath(poligono(ig + Offset(9.6f, -12.4f), ig + Offset(12.5f, -21.4f), ig + Offset(15.4f, -12.4f)), l.tono(teja, 0.56f))
        for (dx in listOf(2f, 5f, 8f)) drawRect(if (enc > 0f) C.GAS.copy(alpha = 0.75f) else l.tono(C.HOLLIN, 0.56f), ig + Offset(dx - 0.5f, -5.4f), Size(1f, 3.4f))
        drawRect(l.tono(C.HOLLIN, 0.56f), ig + Offset(11.8f, -10f), Size(1.4f, 2.4f))

        // ---- La capilla metodista, lisa, en la otra ladera.
        val cp = Offset(323f, ladera(328f) + 8f)
        drawRect(l.tono(lerp(C.LADRILLO, C.HOLLIN, 0.24f), 0.56f), cp + Offset(0f, -8f), Size(10f, 8f)); drawPath(poligono(cp + Offset(-0.6f, -8f), cp + Offset(5f, -12.4f), cp + Offset(10.6f, -8f)), l.tono(lerp(lerp(C.LADRILLO, C.HOLLIN, 0.24f), C.LUZ, 0.1f * r.dia), 0.56f))
        drawLine(l.tono(teja, 0.56f), cp + Offset(-0.8f, -7.8f), cp + Offset(5f, -12.6f), 0.7f); drawLine(l.tono(teja, 0.56f), cp + Offset(5f, -12.6f), cp + Offset(10.8f, -7.8f), 0.7f)
        for (dx in listOf(2f, 6.4f)) drawRect(if (enc > 0f) C.GAS.copy(alpha = 0.75f) else l.tono(C.HOLLIN, 0.56f), cp + Offset(dx, -5.6f), Size(1.4f, 3.2f)); drawCircle(l.tono(C.PIEDRA, 0.56f), 1f, cp + Offset(5f, -9.4f))

        // ---- La fábrica del fondo, al pie de la ladera de la derecha: su nave de dientes de sierra y su chimenea.
        val nave = Offset(276f, PIE - 2f); val lf = 0.46f
        chimenea(l, r, Offset(302f, PIE - 2f), 30f, 1.7f, 1f, lf)
        for (i in 0..4) drawPath(poligono(nave + Offset(i * 4.6f, -6f), nave + Offset(i * 4.6f, -9.6f), nave + Offset(i * 4.6f + 4.6f, -6f)), l.tono(teja, lf))
        for (i in 0..4) drawLine(if (l.noche > 0.3f && fabrica(m)) C.GAS.copy(alpha = 0.7f) else l.tono(lerp(C.PIZARRA, C.HUESO, 0.4f), lf), nave + Offset(i * 4.6f + 0.2f, -6.2f), nave + Offset(i * 4.6f + 0.2f, -9.4f), 0.5f)
        drawRect(l.tono(lerp(C.LADRILLO, C.HOLLIN, 0.3f), lf), nave + Offset(0f, -6f), Size(23f, 6f))
        for (i in 0..5) drawRect(if (enc > 0f && (fabrica(m) || i % 3 == 0)) Color(0xFFFFB060).copy(alpha = 0.9f) else l.tono(C.HOLLIN, lf), nave + Offset(1.4f + i * 3.6f, -4.4f), Size(2f, 2.4f))
        with(HumoVapor) { penacho(Offset(302f, PIE - 33.4f), 34f, 1.8f, viento, 7.1f, pardo(l, 0.44f), 43, alfa = if (fabrica(m)) 0.55f else 0.3f, n = 9) }

        // El velo de humo de carbón que se queda sobre los tejados.
        for ((c, w) in listOf(Offset(124f, 90f) to 40f, Offset(346f, 92f) to 44f)) mancha(c, w, 6f, pardo(l, 0.6f), 0.16f)

        // Guy Fawkes: hogueras en las calles y cohetes sobre los tejados.
        if (EscenaVapor.hogueras(m) && l.noche > 0.3f) {
            for (c in HOGUERAS) { resplandor(c, 18f, 0.9f * l.noche, C.FUEGO); drawPath(poligono(c + Offset(-2.4f, 0f), c + Offset(0f, -6f), c + Offset(2.4f, 0f)), lerp(C.FUEGO, Color(0xFFFFE0A0), 0.4f)) }
            val cz = m.azar("cohetes")
            repeat(5) { val c = Offset(cz.entre(30f, 350f), cz.entre(14f, 56f)); val color = listOf(C.FUEGO, C.GAS, C.VERDE_SENAL, C.ROJO_SENAL, C.GAS)[it]
                for (k in 0 until 12) { val a = k * 0.5236f; drawLine(color.copy(alpha = 0.85f), c + Offset(cos(a), sin(a)) * 2f, c + Offset(cos(a), sin(a)) * cz.entre(6f, 10f), 0.6f, StrokeCap.Round) }
                resplandor(c, 16f, 0.5f, color) }
        }
    }

    // ---------------------------------------------------------------- El viaducto

    /** El hueco de un arco entre [x0] y [x1], hasta el suelo; [t] lleva cada punto a la cara de delante o a la de atrás. */
    private fun hueco(x0: Float, x1: Float, t: (Offset) -> Offset): Path = Path().apply {
        val ctrl = ARRANQUE - (ARRANQUE - CLAVE) / 0.75f
        val a = t(Offset(x0, PIE + 6f)); val b = t(Offset(x0, ARRANQUE)); val c1 = t(Offset(x0, ctrl)); val c2 = t(Offset(x1, ctrl)); val d = t(Offset(x1, ARRANQUE)); val e = t(Offset(x1, PIE + 6f))
        moveTo(a.x, a.y); lineTo(b.x, b.y); cubicTo(c1.x, c1.y, c2.x, c2.y, d.x, d.y); lineTo(e.x, e.y); close()
    }

    private fun arco(x0: Float, x1: Float): Path = Path().apply { val ctrl = ARRANQUE - (ARRANQUE - CLAVE) / 0.75f; moveTo(x0, ARRANQUE); cubicTo(x0, ctrl, x1, ctrl, x1, ARRANQUE) }

    private fun DrawScope.viaducto(m: Momento, l: Luces, r: Rayo) {
        val lejos = 0.26f
        if (!EscenaVapor.hayObra(m)) {
            // Todavía no hay viaducto: solo los jalones de los agrimensores, rojos y blancos, cruzando el valle.
            for (x in listOf(118f, 232f, 270f)) { val y = if (x < 200f) ladera(x) + 10f else PIE
                drawLine(l.tono(C.HUESO, 0.4f), Offset(x, y), Offset(x, y - 8f), 0.7f); for (k in 0..1) drawLine(l.tono(C.ROJO, 0.4f), Offset(x, y - 1f - k * 4f), Offset(x, y - 3f - k * 4f), 0.7f); drawRect(l.tono(C.ROJO, 0.4f), Offset(x, y - 8f), Size(2.6f, 1.6f)) }
            return
        }
        val luzDer = r.lat >= 0f; val nieve = EscenaVapor.nevado(m)
        val piedra = if (nieve) lerp(C.PIEDRA, C.NIEVE, 0.18f) else C.PIEDRA
        val frente = l.tono(lerp(piedra, C.LUZ, 0.18f * r.dia * r.contraste), lejos)
        val alPie = lerp(frente, l.bruma, 0.3f)
        val alSol = l.tono(lerp(piedra, C.LUZ, 0.45f * r.dia * r.contraste), lejos)
        val aLaSombra = l.tono(mult(lerp(piedra, C.MADERA, 0.25f), r.color, 0.9f * (0.4f + 0.6f * r.contraste)), lejos)
        val honda = l.tono(mult(lerp(piedra, C.HOLLIN, 0.4f), r.color, 1f), lejos)
        val madera = l.tono(lerp(C.MADERA, C.HUESO, 0.12f), lejos)
        val cerrados = EscenaVapor.arcosCerrados(m); val terminado = EscenaVapor.viaductoTerminado(m)
        val w = PILA / 2f

        // ---- Las pilas, cada una hasta donde ha subido: su cara de frente, con las hiladas, y el costado que mira al centro del valle.
        for (i in 0 until EscenaVapor.APOYOS) {
            val f = EscenaVapor.pilar(m, i); if (f <= 0f) continue
            val x = apoyo(i); val cima = PIE - f * (PIE - ARRANQUE)
            val s = if (x < FUGA.x) 1f else -1f; val xe = x + s * w
            drawPath(poligono(Offset(xe, cima), atras(Offset(xe, cima)), atras(Offset(xe + s * 0.8f, PIE)), Offset(xe + s * 0.8f, PIE)), if ((s > 0f) == luzDer) alSol else aLaSombra)
            drawPath(poligono(Offset(x - w - 0.8f, PIE), Offset(x - w, cima), Offset(x + w, cima), Offset(x + w + 0.8f, PIE)), Brush.verticalGradient(0f to frente, 1f to alPie, startY = ARRANQUE, endY = PIE))
            var y = PIE - 3f; while (y > cima + 1f) { drawLine(honda.copy(alpha = 0.22f), Offset(x - w, y), Offset(x + w, y), 0.3f); y -= 3.4f }
            // El zócalo, más ancho, al pie.
            drawRect(alPie, Offset(x - w - 1.6f, PIE - 3f), Size(PILA + 3.2f, 3f)); drawRect(honda.copy(alpha = 0.3f), Offset(x - w - 0.8f, PIE - 3.6f), Size(PILA + 1.6f, 0.6f))
            if (f >= 1f) {
                // La imposta, de donde arrancan los arcos.
                drawRect(alSol, Offset(x - w - 0.9f, ARRANQUE - 0.8f), Size(PILA + 1.8f, 1.5f)); drawRect(honda.copy(alpha = 0.45f), Offset(x - w, ARRANQUE + 0.7f), Size(PILA, 0.8f))
                if (nieve && i >= cerrados + 1) drawRect(l.tono(C.NIEVE, lejos), Offset(x - w - 0.9f, ARRANQUE - 1.6f), Size(PILA + 1.8f, 0.9f))
            } else {
                // El andamio de palos, que sube por delante de la obra, y arriba la cabria con su sillar colgado.
                val tope = cima - 6f
                for (dx in listOf(-w - 3f, w + 3f)) drawLine(madera, Offset(x + dx, PIE), Offset(x + dx, tope), 0.7f)
                var ya = PIE - 5f; var k = 0
                while (ya > tope) { drawLine(madera, Offset(x - w - 3f, ya), Offset(x + w + 3f, ya), 0.45f); if (ya - 6f > tope) drawLine(madera.copy(alpha = 0.7f), Offset(x + (if (k % 2 == 0) -w - 3f else w + 3f), ya), Offset(x + (if (k % 2 == 0) w + 3f else -w - 3f), ya - 6f), 0.35f); ya -= 6f; k++ }
                drawRect(madera, Offset(x - w - 4f, cima - 1f), Size(PILA + 8f, 1f))
                drawLine(l.tono(C.HIERRO, lejos), Offset(x - 1f, cima - 1f), Offset(x - 1f, cima - 12f), 0.8f); drawLine(l.tono(C.HIERRO, lejos), Offset(x - 1f, cima - 12f), Offset(x + 9f, cima - 8f), 0.7f); drawLine(l.tono(C.HIERRO, lejos), Offset(x - 1f, cima - 4f), Offset(x + 9f, cima - 8f), 0.4f)
                drawLine(l.tono(C.HOLLIN, lejos), Offset(x + 9f, cima - 8f), Offset(x + 9f, cima - 3f), 0.3f); drawRect(frente, Offset(x + 7.6f, cima - 3f), Size(2.8f, 2f))
            }
        }

        // ---- Los arcos cerrados: el tímpano, la bóveda por dentro (se ve desde abajo), las dovelas y la clave. Y la cimbra del que viene.
        for (i in 0 until EscenaVapor.APOYOS - 1) {
            val x0 = apoyo(i) + w; val x1 = apoyo(i + 1) - w; val xm = (x0 + x1) / 2f
            if (i < cerrados) {
                val delante = hueco(x0, x1) { it }; val detras = hueco(x0, x1) { atras(it) }
                clipPath(delante, ClipOp.Difference) { drawRect(frente, Offset(apoyo(i) - w, TABLERO), Size(VANO + PILA, ARRANQUE - TABLERO)) }
                clipPath(delante) { clipPath(detras, ClipOp.Difference) { drawRect(honda, Offset(x0 - 1f, CLAVE - 1f), Size(x1 - x0 + 2f, ARRANQUE - CLAVE + 1.6f)) } }
                // La sombra que el pretil y la cornisa echan en el tímpano, y las hiladas.
                drawRect(honda.copy(alpha = 0.34f), Offset(apoyo(i) - w, TABLERO), Size(VANO + PILA, 1.7f))
                clipPath(delante, ClipOp.Difference) { for (yy in listOf(TABLERO + 5f, TABLERO + 9f, TABLERO + 13f, TABLERO + 17f)) drawLine(honda.copy(alpha = 0.16f), Offset(apoyo(i) - w, yy), Offset(apoyo(i + 1) + w, yy), 0.3f) }
                drawPath(arco(x0, x1), l.tono(lerp(piedra, Color.White, 0.3f), lejos), style = Stroke(1.5f))
                drawPath(arco(x0 - 0.9f, x1 + 0.9f), honda.copy(alpha = 0.3f), style = Stroke(0.4f))
                drawPath(poligono(Offset(xm - 1.1f, CLAVE - 2.2f), Offset(xm + 1.1f, CLAVE - 2.2f), Offset(xm + 0.8f, CLAVE + 0.9f), Offset(xm - 0.8f, CLAVE + 0.9f)), alSol)
            } else if (i == cerrados && EscenaVapor.pilar(m, i) >= 1f && EscenaVapor.pilar(m, i + 1) >= 1f) {
                // La cimbra: el arco de madera sobre el que se asientan las dovelas, con su tirante y su abanico de puntales.
                drawPath(arco(x0, x1), madera, style = Stroke(1.4f)); drawPath(arco(x0 + 2.4f, x1 - 2.4f), madera.copy(alpha = 0.7f), style = Stroke(0.5f))
                drawLine(madera, Offset(x0, ARRANQUE), Offset(x1, ARRANQUE), 0.9f)
                for (k in 1..7) { val a = PI.toFloat() * (k / 8f); drawLine(madera, Offset(xm, ARRANQUE), Offset(xm - cos(a) * (x1 - x0) / 2f * 0.96f, ARRANQUE - sin(a) * (ARRANQUE - CLAVE) * 0.97f), 0.5f) }
                for (dx in listOf(-9f, 0f, 9f)) drawLine(madera.copy(alpha = 0.8f), Offset(xm + dx, ARRANQUE), Offset(xm + dx * 1.3f, PIE), 0.5f)
                // Las primeras dovelas, ya puestas en los arranques.
                for (s in listOf(-1f, 1f)) drawLine(l.tono(lerp(piedra, Color.White, 0.3f), lejos), Offset(xm + s * (x1 - x0) / 2f, ARRANQUE), Offset(xm + s * (x1 - x0) / 2f * 0.95f, ARRANQUE - 5f), 1.5f)
            }
        }

        // ---- El tablero: la cornisa, el pretil con sus apartaderos sobre cada pila y, mientras dura la obra, la grúa en la punta.
        if (cerrados > 0) {
            val ini = apoyo(0) - w - 16f; val fin = if (terminado) ANCHO + 6f else apoyo(cerrados) + w
            drawRect(l.tono(lerp(piedra, C.LUZ, 0.1f * r.dia), lejos), Offset(ini, PRETIL), Size(fin - ini, TABLERO - PRETIL))
            drawRect(alSol, Offset(ini, PRETIL - 0.5f), Size(fin - ini, 0.8f)); drawRect(alSol, Offset(ini, TABLERO - 0.9f), Size(fin - ini, 1.1f))
            for (i in 0..cerrados) { val x = apoyo(i); if (x + 2.6f > fin + 0.1f) continue; drawRect(alSol, Offset(x - 2.6f, PRETIL - 1.3f), Size(5.2f, TABLERO - PRETIL + 1.5f)); drawRect(honda.copy(alpha = 0.3f), Offset(if (luzDer) x - 2.6f else x + 1.6f, PRETIL - 1.3f), Size(1f, TABLERO - PRETIL + 1.5f)) }
            if (nieve) drawRect(l.tono(C.NIEVE, lejos), Offset(ini, PRETIL - 1.1f), Size(fin - ini, 0.8f))
            if (!terminado) {
                val x = fin - 4f
                drawLine(l.tono(C.HIERRO, lejos), Offset(x, PRETIL), Offset(x, PRETIL - 13f), 0.9f); drawLine(l.tono(C.HIERRO, lejos), Offset(x, PRETIL - 13f), Offset(x + 13f, PRETIL - 9f), 0.8f); drawLine(l.tono(C.HIERRO, lejos), Offset(x, PRETIL - 4f), Offset(x + 13f, PRETIL - 9f), 0.4f); drawLine(l.tono(C.HIERRO, lejos), Offset(x, PRETIL - 13f), Offset(x - 6f, PRETIL), 0.4f)
                drawLine(l.tono(C.HOLLIN, lejos), Offset(x + 13f, PRETIL - 9f), Offset(x + 13f, PRETIL + 5f), 0.3f); drawRect(frente, Offset(x + 11.4f, PRETIL + 5f), Size(3.2f, 2.2f))
                for (k in 0..2) drawRect(frente, Offset(x - 16f + k * 3.4f, PRETIL - 2.2f - (k % 2) * 1.6f), Size(3f, 1.7f + (k % 2) * 1.6f))
            } else {
                // Los postes del telégrafo, que cruzan con la vía.
                for (i in 0..8) { val x = apoyo(i) + 19f; drawLine(l.tono(lerp(C.MADERA, C.HOLLIN, 0.4f), lejos), Offset(x, PRETIL), Offset(x, PRETIL - 9f), 0.5f); drawLine(l.tono(lerp(C.MADERA, C.HOLLIN, 0.4f), lejos), Offset(x - 1.6f, PRETIL - 7.6f), Offset(x + 1.6f, PRETIL - 7.6f), 0.4f) }
                if (EscenaVapor.trenEnElViaducto(m)) tren(m, l)
            }
        }
    }

    /** Dónde va la máquina del tren que cruza el viaducto. */
    private fun trenX(m: Momento): Float = 150f + m.azar("tren", "x", kotlin.math.floor(m.hora).toInt()).entero(150)

    private fun DrawScope.tren(m: Momento, l: Luces) {
        val x = trenX(m); val y = PRETIL; val lejos = 0.3f
        val primero = EscenaVapor.hoyEs(m, "primer_tren")
        val chapa = l.tono(if (primero) C.VERDE else C.HOLLIN, lejos); val negro = l.tono(C.HOLLIN, lejos)
        // La máquina (la caldera, la cabina, la chimenea y el domo) y tres coches con sus ventanillas.
        drawRect(chapa, Offset(x, y - 5f), Size(11f, 5f)); drawRect(negro, Offset(x + 9f, y - 5.4f), Size(3f, 5.4f)); drawRect(negro, Offset(x + 9.6f, y - 8.6f), Size(1.6f, 3.4f)); drawCircle(l.tono(C.LATON, lejos), 1.1f, Offset(x + 5f, y - 5.4f))
        drawRect(chapa, Offset(x - 2.6f, y - 7.4f), Size(4.4f, 7.4f)); drawRect(negro, Offset(x - 3f, y - 8f), Size(5.2f, 0.9f)); drawRect(chapa, Offset(x - 11f, y - 5.6f), Size(7.6f, 5.6f)); drawRect(negro, Offset(x - 10.4f, y - 6.6f), Size(6.4f, 1.2f))
        for (i in 1..3) { val c = x - 11.6f - i * 16.4f; drawRect(l.tono(lerp(C.LADRILLO_OSC, C.MADERA, 0.4f), lejos), Offset(c, y - 6.2f), Size(15.4f, 6.2f)); drawRect(l.tono(C.HUESO, lejos).copy(alpha = 0.6f), Offset(c - 0.3f, y - 6.9f), Size(16f, 0.8f))
            for (k in 0..4) drawRect(if (l.noche > 0.3f) C.GAS.copy(alpha = 0.9f) else l.tono(lerp(C.HOLLIN, C.PIZARRA, 0.3f), lejos), Offset(c + 1.2f + k * 2.8f, y - 5f), Size(1.8f, 2.2f)) }
        if (primero) for (dx in listOf(-1f, 9f)) { drawLine(l.tono(C.MADERA, lejos), Offset(x + dx, y - 5f), Offset(x + dx, y - 11f), 0.4f); drawPath(poligono(Offset(x + dx, y - 8.4f), Offset(x + dx - 3.6f, y - 9.6f), Offset(x + dx, y - 11f)), l.tono(C.ROJO, lejos)) }
        if (l.noche > 0.3f) resplandor(Offset(x - 1f, y - 4f), 6f, 0.6f * l.noche, C.FUEGO)
    }

    // ---------------------------------------------------------------- Los árboles

    private class Rama(val a: Offset, val b: Offset, val g: Float, val nivel: Int)
    private class Racimo(val c: Offset, val w: Float, val giro: Float)

    /**
     * Un árbol de hoja ancha: el tronco, que se abre en tres brazos, cada brazo en ramas y cada rama en ramillas; y en
     * las puntas, los racimos de hojas, en tres tonos (la sombra por debajo, el cuerpo y la luz arriba y del lado del
     * sol), con el borde roto por pinceladas sueltas y con claros por donde se ve el ramaje. En otoño pierde racimos; en
     * invierno queda pelado, con sus ramillas. El viento fuerte lo vence hacia la izquierda.
     */
    internal fun DrawScope.arbol(m: Momento, l: Luces, r: Rayo, pie: Offset, alto: Float, lejos: Float, az: Azar) {
        val lado = if (r.lat >= 0f) 1f else -1f
        val nieve = EscenaVapor.nevado(m)
        val empuje = -((abs(EscenaVapor.soplo(m)) - 0.4f) / 0.6f).coerceIn(0f, 1f)
        val corteza = lerp(C.HOLLIN, C.MADERA, 0.42f)
        val cLuz = l.tono(lerp(corteza, C.LUZ, 0.3f * r.dia * r.contraste), lejos); val cSombra = l.tono(mult(corteza, r.color, 0.7f * (0.4f + 0.6f * r.contraste)), lejos)
        val ramas = ArrayList<Rama>(); val puntas = ArrayList<Offset>(); val nudos = ArrayList<Offset>()
        fun crece(a: Offset, ang: Float, largo: Float, g: Float, nivel: Int) {
            val b = a + Offset(cos(ang), sin(ang)) * largo + Offset(empuje * largo * 0.22f * (4 - nivel), 0f)
            ramas += Rama(a, b, g, nivel)
            if (nivel == 0) { puntas += b; return }
            nudos += b
            val n = if (nivel == 3) 3 else 2
            for (k in 0 until n) {
                val abre = (k - (n - 1) / 2f) * az.entre(0.6f, 0.9f) + az.entre(-0.2f, 0.2f)
                crece(a + (b - a) * (if (k == n / 2) 1f else az.entre(0.62f, 0.95f)), ang + abre, largo * az.entre(0.64f, 0.8f), g * 0.6f, nivel - 1)
            }
        }
        val cruz = pie + Offset(alto * az.entre(-0.03f, 0.03f), -alto * 0.36f)
        crece(cruz, -PI.toFloat() / 2f + az.entre(-0.12f, 0.12f), alto * 0.23f, alto * 0.05f, 3)

        // La sombra al pie y el tronco, más ancho en la base, con su lado de luz.
        mancha(pie + Offset(-lado * alto * 0.1f, 0.6f), alto * 0.3f, alto * 0.035f, l.tono(mult(C.PRADO, r.color, 0.9f), lejos), 0.5f)
        val g0 = alto * 0.055f; val g1 = alto * 0.03f
        drawPath(poligono(listOf(pie + Offset(-g0 * 1.7f, 0f), pie + Offset(-g0, -alto * 0.06f), cruz + Offset(-g1, 0f), cruz + Offset(g1, 0f), pie + Offset(g0, -alto * 0.06f), pie + Offset(g0 * 1.7f, 0f))), cSombra)
        drawPath(poligono(listOf(pie + Offset(lado * g0 * 1.7f, 0f), pie + Offset(lado * g0, -alto * 0.06f), cruz + Offset(lado * g1, 0f), cruz + Offset(lado * g1 * 0.1f, 0f), pie + Offset(lado * g0 * 0.2f, 0f))), cLuz)
        val hoja = hojaDe(m.ficha.estacion)?.let { lerp(it, C.PRADO, 0.3f) }
        fun ramaje(hasta: Int, alfa: Float) { for (q in ramas) if (q.nivel >= hasta) { drawLine(cSombra.copy(alpha = alfa), q.a, q.b, q.g.coerceAtLeast(0.45f), StrokeCap.Round); if (q.nivel >= 2) drawLine(cLuz.copy(alpha = alfa), q.a + Offset(lado * q.g * 0.25f, 0f), q.b + Offset(lado * q.g * 0.2f, 0f), (q.g * 0.4f).coerceAtLeast(0.3f), StrokeCap.Round) } }
        if (hoja == null) {
            // Pelado: todo el ramaje y, en cada punta, las ramillas finas. Con nieve, una raya blanca encima de los brazos.
            ramaje(0, 1f)
            for (p in puntas) for (k in 0..2) drawLine(cSombra.copy(alpha = 0.75f), p, p + Offset(az.entre(-1f, 1f) * alto * 0.07f + empuje * alto * 0.05f, -alto * az.entre(0.02f, 0.09f)), 0.35f, StrokeCap.Round)
            if (nieve) for (q in ramas) if (q.nivel >= 1) drawLine(l.tono(C.NIEVE, lejos), q.a + Offset(0f, -q.g * 0.5f), q.b + Offset(0f, -q.g * 0.5f), (q.g * 0.4f).coerceAtLeast(0.35f), StrokeCap.Round)
            return
        }
        val otono = m.ficha.estacion == Estacion.OTONO
        val oscuro = l.tono(mult(lerp(hoja, C.HOLLIN, 0.2f), r.color, 0.85f * (0.4f + 0.6f * r.contraste)), lejos)
        val medio = l.tono(hoja, lejos)
        val claro = l.tono(lerp(lerp(hoja, C.LUZ, 0.36f * r.dia), C.NIEVE, if (nieve) 0.6f else 0f), lejos)
        val racimos = (puntas + nudos.drop(1)).filter { !otono || az.real() > 0.28f }.map { Racimo(it + Offset(az.entre(-1f, 1f), az.entre(-1f, 0.4f)) * (alto * 0.03f), alto * az.entre(0.085f, 0.125f), az.entre(0f, 6.28f)) }
        fun redondel(forma: Path, c: Offset, rr: Float) = forma.addOval(Rect(c.x - rr, c.y - rr * 0.86f, c.x + rr, c.y + rr * 0.86f))
        val forma = Path()
        // 1. La sombra de cada racimo, por debajo; y el ramaje, que se ve en los claros.
        for (q in racimos) for (k in 0 until 5) { val an = q.giro + k * 1.257f; redondel(forma, q.c + Offset(cos(an) * q.w * 0.62f - lado * q.w * 0.12f, q.w * 0.3f + sin(an) * q.w * 0.42f), q.w * 0.52f) }
        drawPath(forma, oscuro)
        ramaje(1, 0.9f)
        // 2. El cuerpo.
        forma.rewind()
        for (q in racimos) for (k in 0 until 6) { val an = q.giro + 0.6f + k * 1.047f; redondel(forma, q.c + Offset(cos(an) * q.w * 0.6f, -q.w * 0.12f + sin(an) * q.w * 0.48f), q.w * (0.36f + 0.1f * sin(an * 3f))) }
        drawPath(forma, medio)
        // 3. La luz: pinceladas pequeñas arriba y del lado del sol.
        forma.rewind()
        for (q in racimos) for (k in 0 until 4) { val an = q.giro + 1.3f + k * 1.571f; redondel(forma, q.c + Offset(lado * q.w * 0.32f + cos(an) * q.w * 0.42f, -q.w * 0.44f + sin(an) * q.w * 0.2f), q.w * 0.2f) }
        drawPath(forma, claro)
        // 4. El borde roto: hojas sueltas alrededor, y toques de sombra entre racimo y racimo.
        for (q in racimos) {
            for (k in 0 until 5) { val an = q.giro + 2.1f + k * 1.31f; val c = q.c + Offset(cos(an), sin(an) * 0.8f) * (q.w * az.entre(0.95f, 1.2f)) + Offset(empuje * q.w * 0.5f, 0f); drawOval(if (sin(an) < -0.2f) claro else if (k % 2 == 0) medio else oscuro, Offset(c.x - q.w * 0.14f, c.y - q.w * 0.09f), Size(q.w * 0.28f, q.w * 0.18f)) }
            for (k in 0 until 2) { val c = q.c + Offset(az.entre(-0.5f, 0.5f) * q.w - lado * q.w * 0.2f, az.entre(0.05f, 0.4f) * q.w); drawOval(oscuro.copy(alpha = 0.8f), Offset(c.x - q.w * 0.16f, c.y - q.w * 0.08f), Size(q.w * 0.32f, q.w * 0.16f)) }
        }
    }

    // ---------------------------------------------------------------- Lo de al lado del patio

    private fun DrawScope.cercania(m: Momento, l: Luces, r: Rayo) {
        val fijo = Azar(Semilla.de("vapor", "cerca"))
        val lejos = 0.24f; val luzDer = r.lat >= 0f; val nieve = EscenaVapor.nevado(m)
        val seto = hojaDe(m.ficha.estacion)?.let { lerp(it, C.PRADO, 0.4f) } ?: lerp(C.MADERA, C.HOLLIN, 0.5f)
        // La sombra de las pilas en el prado, y un seto de espino a lo largo de la vía.
        for (i in listOf(4, 5)) if (EscenaVapor.pilar(m, i) > 0.2f) mancha(Offset(apoyo(i) + (if (luzDer) -7f else 7f), PIE + 0.4f), 11f, 1.6f, l.tono(mult(C.PRADO, r.color, 0.9f), 0.3f), 0.5f * r.dia)
        repeat(7) { val x = 262f + it * 4.4f + fijo.entre(-1f, 1f); val y = PIE + 3f + it * 1.9f; val w = 2f + it * 0.3f
            mancha(Offset(x, y), w, w * 0.6f, l.tono(mult(seto, r.color, 0.5f), 0.3f), 0.9f); mancha(Offset(x + (if (luzDer) 0.6f else -0.6f), y - 0.8f), w * 0.7f, w * 0.4f, l.tono(lerp(seto, C.LUZ, 0.2f * r.dia), 0.3f), 0.9f) }

        // ---- La fábrica de al lado. Primero la chimenea grande, que sale de detrás de la nave.
        chimenea(l, r, CHIMENEA, CHIMENEA_ALTO, 3.7f, 2.1f, lejos)
        val muro = lerp(C.LADRILLO, C.HOLLIN, 0.25f); val teja = if (nieve) lerp(C.PIZARRA, C.NIEVE, 0.8f) else C.PIZARRA
        val claro = l.tono(lerp(muro, C.LUZ, 0.18f * r.dia * r.contraste), lejos); val oscuro = l.tono(mult(muro, r.color, 0.8f * (0.4f + 0.6f * r.contraste)), lejos)
        val trabaja = fabrica(m); val conLuz = l.noche > 0.2f && (trabaja || m.hora < 22.0)
        val horno = Color(0xFFFFB060)
        // La nave: cuatro dientes de sierra, cada uno con su cristal al norte y su faldón de pizarra.
        val nx = 298f; val alero = BORDE - 15f; val diente = 8f
        for (i in 0..3) { val x = nx + i * diente
            drawPath(poligono(Offset(x, alero), Offset(x + 1.4f, alero - 7f), Offset(x + diente, alero)), l.tono(lerp(teja, C.LUZ, 0.14f * r.dia), lejos))
            drawPath(poligono(Offset(x, alero), Offset(x, alero - 7f), Offset(x + 1.4f, alero - 7f), Offset(x + 1.4f, alero)), if (conLuz && trabaja) lerp(horno, C.GAS, 0.5f) else l.tono(lerp(C.PIZARRA, l.medio, 0.4f), lejos))
            drawLine(l.tono(C.HOLLIN, lejos).copy(alpha = 0.5f), Offset(x + 1.4f, alero - 7f), Offset(x + diente, alero), 0.4f) }
        drawRect(claro, Offset(nx, alero), Size(diente * 4f, 16f)); drawRect(oscuro.copy(alpha = 0.6f), Offset(nx, alero), Size(diente * 4f, 0.9f))
        drawRect(l.tono(lerp(C.PIEDRA, C.HOLLIN, 0.3f), lejos), Offset(nx, alero + 1f), Size(diente * 4f, 0.8f))
        for (i in 0..5) { val v = Offset(nx + 1.6f + i * 5.2f, alero + 4f)
            val vent = Path().apply { moveTo(v.x, v.y + 6.4f); lineTo(v.x, v.y + 1.4f); cubicTo(v.x, v.y - 0.6f, v.x + 2.8f, v.y - 0.6f, v.x + 2.8f, v.y + 1.4f); lineTo(v.x + 2.8f, v.y + 6.4f); close() }
            drawPath(vent, if (conLuz && (trabaja || i % 3 == 1)) horno else l.tono(lerp(C.HOLLIN, C.PIZARRA, 0.4f), lejos)); drawLine(l.tono(C.PIEDRA, lejos).copy(alpha = 0.7f), v + Offset(-0.3f, 6.6f), v + Offset(3.1f, 6.6f), 0.5f)
            drawLine(oscuro.copy(alpha = 0.5f), Offset(nx + i * 5.2f + 0.6f, alero + 1.8f), Offset(nx + i * 5.2f + 0.6f, BORDE), 0.4f) }
        if (conLuz && trabaja) resplandor(Offset(nx + 16f, alero + 6f), 24f, 0.4f * l.noche, horno)
        // La casa de máquinas: alta, con tres pisos de ventanas de arco, su frontón y su óculo.
        val c0 = 332f; val c1 = 352f; val techo = BORDE - 34f
        drawRect(claro, Offset(c0, techo), Size(c1 - c0, 35f)); drawRect(oscuro, Offset(if (luzDer) c0 else c1 - 4.4f, techo), Size(4.4f, 35f))
        drawPath(poligono(Offset(c0 - 1f, techo), Offset((c0 + c1) / 2f, techo - 7f), Offset(c1 + 1f, techo)), l.tono(lerp(muro, C.LUZ, 0.1f * r.dia), lejos))
        drawLine(l.tono(teja, lejos), Offset(c0 - 1.4f, techo + 0.2f), Offset((c0 + c1) / 2f, techo - 7.4f), 1.1f); drawLine(l.tono(lerp(teja, C.LUZ, 0.2f * r.dia), lejos), Offset((c0 + c1) / 2f, techo - 7.4f), Offset(c1 + 1.4f, techo + 0.2f), 1.1f)
        drawCircle(if (conLuz) horno else l.tono(lerp(C.HOLLIN, C.PIZARRA, 0.4f), lejos), 1.6f, Offset((c0 + c1) / 2f, techo - 2.4f)); drawCircle(l.tono(C.PIEDRA, lejos), 1.6f, Offset((c0 + c1) / 2f, techo - 2.4f), style = Stroke(0.4f))
        for (piso in 0..2) { val y = techo + 3.4f + piso * 10.4f
            drawRect(l.tono(lerp(C.PIEDRA, C.HOLLIN, 0.3f), lejos).copy(alpha = 0.8f), Offset(c0, y + 8.2f), Size(c1 - c0, 0.6f))
            for (k in 0..2) { val v = Offset(c0 + 2.6f + k * 5.8f, y)
                val vent = Path().apply { moveTo(v.x, v.y + 7f); lineTo(v.x, v.y + 1.6f); cubicTo(v.x, v.y - 0.8f, v.x + 3.4f, v.y - 0.8f, v.x + 3.4f, v.y + 1.6f); lineTo(v.x + 3.4f, v.y + 7f); close() }
                val luz = conLuz && (trabaja || (piso == 2 && k == 1))
                drawPath(vent, if (luz) horno else l.tono(lerp(C.HOLLIN, C.PIZARRA, 0.4f), lejos)); drawLine(l.tono(C.HUESO, lejos).copy(alpha = if (luz) 0.5f else 0.35f), v + Offset(1.7f, 0f), v + Offset(1.7f, 7f), 0.35f)
                if (luz) resplandor(v + Offset(1.7f, 3.6f), 7f, 0.4f * l.noche, horno) } }
        if (nieve) drawLine(l.tono(C.NIEVE, lejos), Offset(c0 - 1.4f, techo), Offset((c0 + c1) / 2f, techo - 7.6f), 0.8f)
        // De noche la boca de la chimenea y la puerta de las calderas dan su lumbre.
        if (l.noche > 0.3f) { resplandor(CHIMENEA + Offset(0f, -CHIMENEA_ALTO - 2f), 10f, (if (trabaja) 0.8f else 0.4f) * l.noche, C.FUEGO); drawRect(C.FUEGO.copy(alpha = 0.9f), Offset(325.4f, BORDE - 6f), Size(3.2f, 4f)); resplandor(Offset(327f, BORDE - 4f), 12f, 0.7f * l.noche, C.FUEGO) }

        // ---- El árbol grande, que asoma por detrás de la taberna, y la taberna de la esquina con su rótulo (sin letras).
        arbol(m, l, r, Offset(355f, BORDE), 42f, lejos, fijo)
        val t = Rect(357f, BORDE - 13f, 384f, BORDE + 1f); val cal = lerp(C.HUESO, C.HOLLIN, 0.2f)
        drawRect(l.tono(lerp(cal, C.LUZ, 0.1f * r.dia), lejos), t.topLeft, t.size); drawRect(l.tono(mult(cal, r.color, 0.6f * r.contraste), lejos), Offset(if (luzDer) t.left else t.right - 5f, t.top), Size(5f, t.height))
        drawPath(poligono(Offset(t.left - 1.6f, t.top), Offset(t.left + 3f, t.top - 6.4f), Offset(t.right, t.top - 6.4f), Offset(t.right, t.top)), l.tono(lerp(teja, C.LUZ, 0.12f * r.dia), lejos))
        drawRect(l.tono(C.HOLLIN, lejos).copy(alpha = 0.4f), Offset(t.left, t.top), Size(t.width, 0.8f))
        drawRect(l.tono(lerp(C.LADRILLO_OSC, C.HOLLIN, 0.3f), lejos), Offset(t.left + 4f, t.top - 10.4f), Size(2.8f, 5f)); drawRect(l.tono(lerp(C.COBRE, C.HOLLIN, 0.3f), lejos), Offset(t.left + 4.6f, t.top - 11.6f), Size(1.6f, 1.4f))
        val abierta = EscenaVapor.taberna(m) && l.noche > 0.15f
        for ((x, y) in listOf(360f to t.top + 2f, 368f to t.top + 2f, 375f to t.top + 2f, 360f to t.top + 8f, 375f to t.top + 8f)) { drawRect(if (abierta) lerp(C.GAS, C.FUEGO, 0.3f) else l.tono(lerp(C.HOLLIN, C.PIZARRA, 0.4f), lejos), Offset(x, y), Size(3.8f, 3.8f)); drawLine(l.tono(C.HUESO, lejos).copy(alpha = 0.6f), Offset(x + 1.9f, y), Offset(x + 1.9f, y + 3.8f), 0.3f) }
        drawRect(l.tono(lerp(C.VERDE, C.HOLLIN, 0.4f), lejos), Offset(367.4f, t.top + 7.6f), Size(4.4f, 6.4f))
        drawLine(l.tono(C.HIERRO, lejos), Offset(t.left, t.top + 1.6f), Offset(t.left - 5f, t.top + 1.6f), 0.5f); drawRect(l.tono(lerp(C.ROJO, C.LATON, 0.3f), lejos), Offset(t.left - 4.6f, t.top + 2f), Size(3.2f, 3.8f))
        if (abierta) { resplandor(Offset(369f, t.top + 7f), 24f, 0.5f * l.noche, C.GAS); drawCircle(C.GAS, 0.9f, Offset(t.left - 5.6f, t.top + 1.4f)); resplandor(Offset(t.left - 5.6f, t.top + 1.4f), 7f, 0.6f * l.noche, C.GAS) }

        // ---- El árbol de la punta de la nave y la valla de traviesas del borde del patio, con su seto.
        arbol(m, l, r, Offset(294f, BORDE + 1f), 27f, lejos + 0.02f, fijo)
        repeat(13) { val x = 296f + it * 6.6f + fijo.entre(-1f, 1f); val w = fijo.entre(3f, 4.6f); val y = BORDE - 0.6f
            mancha(Offset(x, y), w, w * 0.62f, l.tono(mult(seto, r.color, 0.5f), 0.2f), 0.95f); mancha(Offset(x + (if (luzDer) 0.8f else -0.8f), y - 1.1f), w * 0.66f, w * 0.38f, l.tono(lerp(seto, C.LUZ, 0.22f * r.dia), 0.2f), 0.95f) }
        val palo = l.tono(lerp(C.MADERA, C.HOLLIN, 0.45f), 0.14f)
        var x = 292f; while (x < ANCHO) { drawRect(palo, Offset(x, BORDE - 2.6f), Size(1.5f, 4.6f)); x += 6f }
        for (y in listOf(BORDE - 1.6f, BORDE + 0.4f)) drawLine(palo, Offset(291f, y), Offset(ANCHO, y), 0.6f)
        if (nieve) drawLine(l.tono(C.NIEVE, 0.14f), Offset(291f, BORDE - 2.2f), Offset(ANCHO, BORDE - 2.2f), 0.5f)
    }

    // ---------------------------------------------------------------- La parte viva

    fun DrawScope.valleVivo(m: Momento, l: Luces, r: Rayo, reloj: Float) {
        val viento = EscenaVapor.soplo(m)
        with(HumoVapor) {
            // El humo pardo de la chimenea grande, tendido por el viento del día; de noche lo tiñe la lumbre de las calderas.
            penacho(CHIMENEA + Offset(0f, -CHIMENEA_ALTO - 2f), 92f, 3.6f, viento, reloj, pardo(l, 0.16f), 11, alfa = if (fabrica(m)) 0.8f else 0.42f, brasa = if (l.noche > 0.3f) C.FUEGO else null, n = 16)
            if (EscenaVapor.taberna(m)) hilo(Offset(362.4f, BORDE - 24.6f), 12f, viento, reloj, l.tono(Color(0xFFD8D2C6), 0.2f), 15)
            // El tren que cruza: el humo se le queda atrás.
            if (EscenaVapor.trenEnElViaducto(m)) bocanadas(l, r, Offset(trenX(m) + 10.4f, PRETIL - 8.8f), 14f, 13f, reloj * 1.5f, -1.5f, 0.3f, 16, 5, fuerza = 0.9f, ritmo = 0.12f)
            // Con el vendaval vuelan las hojas y los papeles.
            if (EscenaVapor.ventarron(m)) volanderas(reloj, l.tono(hojaDe(m.ficha.estacion)?.let { lerp(it, C.MADERA, 0.4f) } ?: lerp(C.MADERA, C.HUESO, 0.3f)), l.tono(C.HUESO), m.ficha.semilla.toInt() and 0xff, 280f)
        }
        if (EscenaVapor.hogueras(m) && l.noche > 0.3f) for (c in HOGUERAS) resplandor(c + Offset(0f, -2f), 10f, 0.5f + 0.3f * sin(reloj * 6f + c.x), C.FUEGO)
    }
}
