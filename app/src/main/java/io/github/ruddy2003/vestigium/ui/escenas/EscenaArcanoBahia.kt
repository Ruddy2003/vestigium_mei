package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaArcano.H
import io.github.ruddy2003.vestigium.ui.escenas.EscenaArcano.Niebla
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.bajio
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.haloDeBruma
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sol
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * La bahía de Horror cósmico: los cabos lejanos, el mar, la punta del acantilado con el faro, las peñas, el muelle con
 * sus barcas, la playa de las redes y la niebla, que va por capas entre todo ello.
 */
internal object BahiaArcano {
    private val C = ColorArcano
    private val E = EscenaArcano

    /** Hasta dónde llega el agua por abajo (lo de más cerca lo tapan la playa, los tejados y la galería). */
    private const val FONDO = 222f

    /** El perfil de la punta del faro, de la izquierda (detrás del muro de la galería) a la punta, puesto a mano. Está cerca: su pie queda por debajo del horizonte. */
    private val PUNTA = listOf(
        Offset(56f, 93.6f), Offset(74f, 92.4f), Offset(90f, 93.4f), Offset(106f, 91.4f), Offset(122f, 92.6f), Offset(136f, 90.6f), Offset(150f, 91.8f), Offset(164f, 90.8f), Offset(178f, 92.2f), Offset(192f, 92.6f),
        Offset(204f, 94.2f), Offset(214f, 95f), Offset(228f, 95.6f), Offset(240f, 96.2f), Offset(247f, 98.2f), Offset(252.6f, 102.6f), Offset(256.4f, 109.6f), Offset(260.6f, 117.4f), Offset(264.6f, 123.6f), Offset(268f, PIE_PUNTA),
    )

    /** Donde la punta toca el agua. */
    private const val PIE_PUNTA = 127f

    /** Donde pisa el faro, y cuánto se ha agrandado respecto del primer dibujo. */
    private const val PIE_FARO = 95.4f
    private const val K_FARO = 1.55f

    /** El muelle: de su arranque en el pueblo a la punta, donde está el farol. */
    private val RAIZ = Offset(268f, 171f)
    private val CABEZA = Offset(156f, 152.4f)
    private fun enElMuelle(t: Float) = Offset(RAIZ.x + (CABEZA.x - RAIZ.x) * t, RAIZ.y + (CABEZA.y - RAIZ.y) * t)

    /** Lo que mide de alto la pared del muelle en [t] (más cerca, más alta), con la marea de hoy. */
    private fun pared(t: Float, marea: Float) = (6.4f - 2.6f * t) + (1f - marea) * (3f - 1.2f * t)

    /** Hasta dónde llega el muelle: entero o, después del temporal, la mitad. */
    private fun largoDelMuelle(m: Momento) = if (E.muelleRoto(m)) 0.54f else 1f

    /** La costa del pueblo, del arranque del muelle al horizonte, y el borde del puerto, del muelle a la playa. */
    private val COSTA = listOf(Offset(268f, 171f), Offset(274f, 164.4f), Offset(283f, 157.6f), Offset(289f, 151f), Offset(299f, 145.6f), Offset(306f, 138.4f), Offset(318f, 132.6f), Offset(329f, 127.4f), Offset(346f, 123.6f), Offset(364f, 121.6f), Offset(384f, 121f))
    private val PUERTO = listOf(Offset(268f, 171f), Offset(262f, 177.6f), Offset(254f, 183.4f), Offset(244f, 189.6f), Offset(235f, 194.4f), Offset(226f, 197.6f))
    private val PLAYA = listOf(Offset(226f, 197.6f), Offset(214f, 200.4f), Offset(199f, 202.2f), Offset(182f, 201.6f), Offset(166f, 202.8f), Offset(150f, 201.2f), Offset(134f, 199.4f), Offset(120f, 198.6f), Offset(100f, 196.4f), Offset(80f, 195.6f), Offset(56f, 194f))

    /** Las barcas del puerto: dónde está cada una a flote (su popa y su largo) y hacia dónde mira. */
    private class Barca(val popa: Offset, val largo: Float, val derecha: Boolean, val franja: Color, val palo: Boolean)
    private val BARCAS = listOf(
        Barca(Offset(226f, 176.4f), 25f, true, C.ALMAGRE, true), Barca(Offset(196f, 170.6f), 20f, false, C.CAL, true), Barca(Offset(214f, 183f), 23f, true, C.MONTE, true),
        Barca(Offset(168f, 193.4f), 40f, false, C.ALMAGRE, true), Barca(Offset(150f, 172f), 16f, true, C.CAL, false), Barca(Offset(241f, 185.4f), 15f, false, C.LIQUEN, false), Barca(Offset(126f, 183f), 19f, true, C.MONTE, true),
    )

    /** Dónde queda cada barca cuando está varada: en la arena, ladeada. */
    private val VARADAS = listOf(Offset(200f, 205f), Offset(118f, 203.4f), Offset(216f, 212f), Offset(150f, 213.6f), Offset(222f, 203.4f), Offset(102f, 211f), Offset(176f, 205f))

    private fun tinteDelMar(m: Momento, c: Color): Color = E.marRojo(m).let { if (it <= 0f) c else lerp(c, lerp(C.ALMAGRE, C.PIEDRA_NEGRA, 0.42f), 0.62f * it) }

    // ---------------------------------------------------------------- La niebla

    /** El nivel de niebla de hoy, de 0 a 3. */
    private fun nivel(m: Momento) = E.niebla(m).ordinal

    /**
     * Un banco de niebla tendido entre dos planos: manchas blandas de [x0] a [x1] alrededor de la altura [y]. Es la
     * pieza que hace que la niebla tenga profundidad: lo que se pinta después queda delante de él.
     */
    fun DrawScope.banco(l: Luces, y: Float, alto: Float, fuerza: Float, az: Azar, x0: Float = 60f, x1: Float = ANCHO + 20f) {
        if (fuerza <= 0.02f) return
        val color = lerp(l.bruma, l.tono(Color.White), 0.22f)
        var x = x0
        while (x < x1) {
            val w = az.entre(46f, 96f)
            mancha(Offset(x + az.entre(-8f, 8f), y + az.entre(-alto * 0.3f, alto * 0.3f)), w, alto * az.entre(0.7f, 1.25f), color, fuerza * az.entre(0.7f, 1f))
            x += w * az.entre(0.5f, 0.8f)
        }
    }

    /** Con niebla el sol no se borra: queda como un disco blanco y sin brillo, que se puede mirar. */
    fun DrawScope.solDeNiebla(m: Momento, l: Luces) {
        if (nivel(m) < 2 || l.noche > 0.5f || l.solAltura < 0.02f || l.solAltura > 1f) return
        val p = l.sol(H)
        mancha(p, 26f, 20f, lerp(l.bajo, Color.White, 0.6f), 0.4f)
        drawCircle(lerp(l.bajo, Color.White, 0.8f).copy(alpha = if (nivel(m) == 3) 0.62f else 0.8f), 5.2f, p)
    }

    /** La niebla que queda entre el pueblo y la galería: la última capa, la más cercana. */
    fun DrawScope.nieblaCercana(m: Momento, l: Luces) {
        val n = nivel(m)
        if (n < 2) return
        val az = m.azar("niebla", 4)
        drawRect(l.bruma.copy(alpha = if (n == 3) 0.2f else 0.08f), Offset(70f, 0f), Size(ANCHO - 70f, 250f))
        banco(l, 220f, 13f, if (n == 3) 0.6f else 0.3f, az)
        if (n == 3) banco(l, 188f, 11f, 0.4f, az)
    }

    // ---------------------------------------------------------------- La parte quieta

    fun DrawScope.bahia(m: Momento, l: Luces, r: Rayo) {
        val fijo = Azar(Semilla.de("arcano", "bahia"))
        val n = nivel(m)
        val marea = E.marea(m)

        // Los cabos lejanos, uno tras otro, cada uno más pálido.
        monte(l, perfil(fijo, 150f, 300f, H + 1f, 6f, 0.3f, 14, isla = true), H + 1.4f, Color(0xFF7E8E94), E.perdido(m, 0.94f))
        monte(l, perfil(fijo, 70f, 214f, H + 1f, 11f, 0.45f, 16, isla = true), H + 1.4f, Color(0xFF6C8078), E.perdido(m, 0.84f))

        // El mar.
        val mar = Rect(56f, H, ANCHO, FONDO)
        val calma = marQuieto(m)
        agua(l, mar, m.azar("mar"), tinteDelMar(m, C.MAR), if (calma) 0f else m.viento, hondo = tinteDelMar(m, C.MAR_HONDO), orilla = tinteDelMar(m, C.MAR_ORILLA))
        if (calma) espejo(l, mar, m.azar("espejo", 1))
        // Con niebla, el mar se pierde hacia el fondo: de lejos no se sabe dónde acaba el agua y dónde empieza el aire.
        if (n >= 1) drawRect(Brush.verticalGradient(listOf(l.bruma.copy(alpha = listOf(0f, 0.4f, 0.9f, 0.97f)[n]), l.bruma.copy(alpha = listOf(0f, 0.1f, 0.5f, 0.8f)[n]), l.bruma.copy(alpha = listOf(0f, 0f, 0.1f, 0.4f)[n])), H, if (n == 3) 200f else 176f), Offset(56f, H - 0.5f), Size(ANCHO - 56f, (if (n == 3) 200f else 176f) - H + 0.5f))
        if (E.marRojo(m) > 0.05f) manchasRojas(m, l)
        banco(l, H + 1f, 5f, listOf(0f, 0.3f, 0.75f, 0.95f)[n], m.azar("niebla", 1))
        if (E.islaQueNoExiste(m)) isla(l)
        if (E.barcasFuera(m) && n < 3) velasLejanas(m, l)

        // La punta del acantilado, con el faro.
        punta(m, l, r, fijo)
        penas(m, l, r, marea)
        banco(l, PIE_PUNTA + 3f, 8f, listOf(0f, 0.2f, 0.55f, 0.9f)[n], m.azar("niebla", 2))
        luzDelFaro(m, l)

        // La orilla del pueblo, hasta el horizonte: lo demás del pueblo lo pinta `PuebloArcano`, encima.
        tierraDelPueblo(m, l, fijo)
        estela(l, Rect(56f, H, ANCHO, 200f), m.azar("estela"))
        if (E.virgenEnBarca(m)) romeria(m, l, r)

        // El muelle y lo que hay a su abrigo.
        reflejosDelPuerto(m, l, marea)
        muelle(m, l, r, marea)
        val afloat = barcasAFlote(m)
        for (i in afloat.sortedBy { BARCAS[it].popa.y }) barcaAFlote(m, l, r, BARCAS[i], i)
        banco(l, 166f, 9f, listOf(0f, 0.12f, 0.36f, 0.8f)[n], m.azar("niebla", 3)); if (n == 3) banco(l, 182f, 9f, 0.6f, m.azar("niebla", 8))
        playa(m, l, r, marea, fijo)
        if (E.temporal(m)) rompientes(m, l)
        if (!E.sinPajaros(m) && l.noche < 0.5f && n < 3) gaviotas(m, l)
    }

    /** Hay días, y siempre con niebla, en que el mar está demasiado quieto: ni un rizo. */
    internal fun marQuieto(m: Momento): Boolean = m.viento < 0.25f && !E.mojado(m) && (E.niebla(m).ordinal >= Niebla.NIEBLA.ordinal || m.azar("quieto").entero(5) == 0)

    /** El mar como un plato: franjas largas y lisas que devuelven el cielo. */
    private fun DrawScope.espejo(l: Luces, z: Rect, az: Azar) {
        val claro = lerp(l.bajo, Color.White, 0.2f)
        repeat(9) { val p = az.real(); mancha(Offset(az.entre(z.left, z.right), z.top + 4f + (z.height - 30f) * p), az.entre(60f, 130f), 1.2f + 3.4f * p, claro, 0.22f) }
        repeat(5) { val p = az.real(); mancha(Offset(az.entre(z.left, z.right), z.top + 8f + (z.height - 30f) * p), az.entre(50f, 110f), 1f + 3f * p, l.tono(C.MAR_HONDO), 0.2f) }
    }

    /** El mar rojo: unas vetas más oscuras, como si algo se hubiera removido en el fondo. */
    private fun DrawScope.manchasRojas(m: Momento, l: Luces) {
        val az = m.azar("rojo"); val k = E.marRojo(m)
        repeat(16) { val p = az.real(); mancha(Offset(az.entre(84f, ANCHO), H + 4f + 76f * p), az.entre(30f, 90f), 1.4f + 4f * p, l.tono(if (it % 3 == 0) lerp(C.MAR_HONDO, C.PIEDRA_NEGRA, 0.5f) else Color(0xFF4A2A24)), 0.34f * k) }
    }

    /** Lo que la niebla dibuja donde el mapa pone una isla: un banco más espeso, bajo y largo, con una loma. */
    private fun DrawScope.isla(l: Luces) {
        val c = lerp(l.bruma, l.tono(C.PIZARRA_OSC), 0.3f)
        mancha(Offset(312f, H - 1.4f), 28f, 3.2f, c, 0.6f)
        mancha(Offset(305f, H - 4f), 11f, 3.6f, c, 0.45f)
    }

    /** Las barcas que han salido: unas velas de color teja, lejos. */
    private fun DrawScope.velasLejanas(m: Momento, l: Luces) {
        val az = m.azar("velas")
        repeat(4) { i ->
            val x = az.entre(300f, 340f); val y = H + 2f + az.entre(0f, 6f); val k = 0.55f + (y - H) / 14f
            val lejos = E.perdido(m, 0.6f - 0.2f * k)
            drawPath(poligono(Offset(x, y), Offset(x + 5.4f * k, y), Offset(x + 4.4f * k, y + 1.3f * k), Offset(x + 0.8f * k, y + 1.3f * k)), l.tono(C.PIEDRA_NEGRA, lejos))
            drawPath(poligono(Offset(x + 2.2f * k, y - 5.4f * k), Offset(x + 4.6f * k, y - 0.4f * k), Offset(x + 0.6f * k, y - 0.6f * k)), l.tono(C.ALMAGRE, lejos))
            reflejoRoto(x, x + 5f * k, y + 1.4f * k, 4f * k, l.tono(C.ALMAGRE, lejos), 0.35f, az, 0.8f)
        }
    }

    // ---------------------------------------------------------------- La punta del faro

    private fun DrawScope.punta(m: Momento, l: Luces, r: Rayo, fijo: Azar) {
        val lejos = E.perdido(m, 0.4f)
        val pie = PIE_PUNTA
        val forma = curva(PUNTA, pie + 0.6f)
        val roca = l.tono(Color(0xFF90907E), lejos); val rocaLuz = l.tono(lerp(Color(0xFFB2B29E), C.LUZ, 0.4f * r.dia), lejos); val rocaSombra = l.tono(mult(Color(0xFF6E7068), r.color, 0.65f * r.contraste), lejos)
        val hierba = l.tono(lerp(C.MONTE, C.LIQUEN, if (E.invierno(m)) 0.5f else 0.22f), lejos)
        // El reflejo de la punta en el agua, antes que ella.
        reflejoRoto(56f, 268f, pie + 0.6f, 13f, l.tono(Color(0xFF3E5650), lejos), 0.5f, Azar(Semilla.de("arcano", "reflejo punta")), 0.9f)
        drawPath(forma, Brush.verticalGradient(listOf(hierba, roca, roca, rocaSombra), 90f, pie + 1f))
        clipPath(forma) {
            val az = Azar(Semilla.de("arcano", "punta"))
            // Los estratos, tendidos y un poco caídos hacia la punta: unos más claros, otros más oscuros.
            repeat(15) { val y = 100f + it * 1.8f; drawLine(l.tono(if (it % 3 == 0) Color(0xFFB4B4A0) else Color(0xFF5E6058), lejos).copy(alpha = if (it % 3 == 0) 0.3f else 0.36f), Offset(54f, y + az.entre(-0.6f, 0.6f)), Offset(270f, y + 2.4f + az.entre(-0.6f, 0.6f)), az.entre(0.4f, 0.9f)) }
            // Las caras del acantilado: contrafuertes que bajan hasta el agua, cada uno con su lado de luz y su lado de sombra.
            repeat(22) { val x = az.entre(56f, 262f); val w = az.entre(6f, 15f); val arriba = 98f + az.entre(0f, 4f)
                drawPath(poligono(Offset(x, arriba), Offset(x + w, arriba + 0.8f), Offset(x + w * (if (r.lat >= 0f) 0.24f else 0.76f), pie + 1f)), (if (it % 2 == 0) rocaSombra else rocaLuz).copy(alpha = 0.6f)) }
            // Grietas, cuevas a ras de agua y chorreras oscuras.
            repeat(40) { val x = az.entre(56f, 264f); val y0 = 98f + az.entre(0f, 8f); drawLine(l.tono(Color(0xFF4A4E4A), lejos).copy(alpha = az.entre(0.25f, 0.6f)), Offset(x, y0), Offset(x + az.entre(-2f, 2f), y0 + az.entre(5f, 20f)), az.entre(0.4f, 1f)) }
            for (x in listOf(96f, 141f, 187f, 233f)) drawPath(Path().apply { moveTo(x - 3.4f, pie + 1f); cubicTo(x - 3f, pie - 6f, x + 3f, pie - 7f, x + 3.6f, pie + 1f); close() }, l.tono(Color(0xFF242C2C), lejos).copy(alpha = 0.8f))
            // La hierba de arriba, que se descuelga por las repisas, con brezo y unas matas que el viento tumbó.
            for (i in 0 until PUNTA.size - 5) { val a = PUNTA[i]; val b = PUNTA[i + 1]; drawPath(poligono(a, b, b + Offset(0f, 6.4f + az.entre(-1.4f, 2f)), a + Offset(0f, 6f + az.entre(-1.4f, 2f))), hierba) }
            repeat(16) { val x = az.entre(60f, 246f); val y = 100f + az.entre(0f, 7f); drawPath(poligono(Offset(x, y - 2f), Offset(x + az.entre(4f, 9f), y - 1.6f), Offset(x + 2f, y + az.entre(1.5f, 4f))), hierba.copy(alpha = 0.85f)) }
            repeat(34) { val x = az.entre(58f, 244f); mancha(Offset(x, 94.6f + az.entre(0f, 5f)), az.entre(3f, 10f), az.entre(0.9f, 2f), l.tono(if (it % 3 == 0) Color(0xFF7A5E70) else lerp(C.MONTE, Color(0xFF2E4034), 0.4f), lejos), 0.5f) }
            // El pie, negro y mojado, con su franja de verdín.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF2E3A32), lejos).copy(alpha = 0.6f), l.tono(C.PIEDRA_NEGRA, lejos).copy(alpha = 0.9f)), pie - 7f, pie + 1f), Offset(54f, pie - 7f), Size(216f, 8f))
        }
        // La rompiente al pie: la línea de espuma, los borbotones contra las rocas caídas y el agua batida.
        val az = Azar(Semilla.de("arcano", "espuma punta"))
        val blanco = l.tono(Color.White, lejos * 0.6f)
        repeat(14) { val x = az.entre(60f, 266f); val w = az.entre(2.4f, 6f); drawPath(poligono(Offset(x, pie + 0.8f), Offset(x + w * 0.4f, pie - az.entre(1.4f, 3.4f)), Offset(x + w, pie + 0.9f)), l.tono(C.PIEDRA_NEGRA, lejos)) }
        repeat(54) { val x = az.entre(58f, 270f); drawLine(blanco.copy(alpha = az.entre(0.3f, 0.8f)), Offset(x, pie + 0.9f + az.entre(0f, 2f)), Offset(x + az.entre(2f, 8f), pie + 1f + az.entre(0f, 2f)), az.entre(0.5f, 0.9f), StrokeCap.Round) }
        repeat(9) { val x = az.entre(70f, 262f); mancha(Offset(x, pie - 0.4f), az.entre(2.4f, 5f), az.entre(1.2f, 2.6f), blanco, 0.55f) }
        // El camino que sube desde el embarcadero de la punta: tres revueltas talladas en la roca, con su pretil.
        val senda = Path().apply { moveTo(258f, 124.6f); lineTo(247f, 119.6f); lineTo(254.6f, 113.4f); lineTo(242f, 108.4f); lineTo(249f, 102.6f); lineTo(238f, 97.4f) }
        drawPath(senda, l.tono(lerp(C.ARENA, C.CAL, 0.3f), lejos), style = Stroke(1.1f, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
        drawPath(senda, l.tono(C.PIEDRA_NEGRA, lejos).copy(alpha = 0.4f), style = Stroke(0.3f))
        drawRect(l.tono(C.PIEDRA_OSC, lejos), Offset(255f, 124.4f), Size(8f, 1.4f)); drawLine(l.tono(C.MADERA_OSC, lejos), Offset(262.6f, 124.4f), Offset(262.6f, 121f), 0.5f)
        // El sendero de arriba, que viene del pueblo por lo alto del acantilado, y el murete de piedra seca.
        drawPath(Path().apply { moveTo(232f, 96.4f); cubicTo(204f, 95.6f, 186f, 97.6f, 164f, 94f); cubicTo(140f, 91.6f, 112f, 95.4f, 62f, 93.6f) }, l.tono(C.ARENA, lejos).copy(alpha = 0.75f), style = Stroke(0.9f, cap = StrokeCap.Round))
        for (i in 0..13) { val x = 130f + i * 5.2f; drawLine(l.tono(C.PIEDRA_OSC, lejos), Offset(x, 91.6f + (i % 3) * 0.3f), Offset(x + 4f, 91.8f + ((i + 1) % 3) * 0.3f), 1.1f) }
        faro(m, l, r, lejos)
    }

    /**
     * El faro: una torre blanca y gruesa, con su zócalo, su franja, su galería de hierro, su linterna de vidrio y su
     * cúpula; y, pegada, la casa del torrero. **[Diseño propio]**: el cuento la da blanca; la franja y la cúpula de
     * almagre son del gato de pixel del tema. Mientras el archivista vive allí (estado `faro`) hay ropa tendida junto a
     * la casa, con su abrigo oscuro.
     */
    private fun DrawScope.faro(m: Momento, l: Luces, r: Rayo, lejos: Float) {
        val x = EscenaArcano.LINTERNA.x; val pie = PIE_FARO; val k = K_FARO; val hombro = pie - 14.8f * k
        val lj = lejos * 0.8f
        val luz = l.tono(lerp(C.CAL, Color.White, 0.5f), lj); val cal = l.tono(C.CAL, lj); val sombra = l.tono(mult(C.CAL, r.color, 0.75f * r.contraste + 0.1f), lj)
        val hierro = l.tono(C.HIERRO, lj)
        val alSol = r.lat >= 0f
        val conGente = E.casaDelTorrero(m); val suEstancia = E.lugar(m) == EscenaArcano.Lugar.FARO
        // Su sombra en la hierba.
        sombraOvalo(r, Offset(x - 7f + r.ax * 5f, pie - 0.8f), Size(30f + abs(r.ax) * 8f, 2.4f), 0.7f)
        // La casa del torrero, a la derecha de la torre: encalada, con su tejado de pizarra, su chimenea, su puerta y dos ventanas.
        val casa = Rect(x + 4.6f * k, pie - 6f * k, x + 4.6f * k + 20f, pie + 0.4f)
        drawRect(if (alSol) luz else cal, casa.topLeft, casa.size)
        drawRect(sombra, Offset(if (alSol) casa.left else casa.right - 5.4f, casa.top), Size(5.4f, casa.height))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF8E8E7C), lj).copy(alpha = 0.5f)), casa.bottom - 3f, casa.bottom), Offset(casa.left, casa.bottom - 3f), Size(casa.width, 3f))
        val tejado = poligono(Offset(casa.left - 1.2f, casa.top + 0.3f), Offset(casa.left + 3.4f, casa.top - 5f), Offset(casa.right - 2.4f, casa.top - 5f), Offset(casa.right + 1.2f, casa.top + 0.3f))
        drawPath(tejado, l.tono(if (E.nevado(m)) lerp(C.PIZARRA, Color.White, 0.7f) else C.PIZARRA, lejos)); drawLine(l.tono(lerp(C.PIZARRA, Color.White, 0.35f), lejos).copy(alpha = 0.8f), Offset(casa.left + 3.4f, casa.top - 5f), Offset(casa.right - 2.4f, casa.top - 5f), 0.4f)
        drawLine(l.tono(C.PIZARRA_OSC, lejos).copy(alpha = 0.5f), Offset(casa.left + 1f, casa.top - 2.4f), Offset(casa.right - 0.6f, casa.top - 2.4f), 0.3f)
        drawRect(l.tono(C.PIEDRA_OSC, lejos), Offset(casa.right - 6.4f, casa.top - 8.4f), Size(2.4f, 4.4f)); drawRect(l.tono(C.PIEDRA_NEGRA, lejos), Offset(casa.right - 6.8f, casa.top - 9f), Size(3.2f, 0.8f))
        drawRect(l.tono(C.MADERA_OSC, lj), Offset(casa.left + 6.6f, casa.bottom - 5.6f), Size(2.6f, 5.6f))
        for ((i, vx) in listOf(casa.left + 11.6f, casa.left + 15.8f).withIndex()) {
            val encendida = conGente && (i == 0 || suEstancia)
            drawRect(if (encendida) C.ACEITE else l.tono(C.PIZARRA_OSC, lejos), Offset(vx, casa.top + 2.4f), Size(2.4f, 3.2f)); drawLine(l.tono(C.CAL, lj).copy(alpha = 0.8f), Offset(vx + 1.2f, casa.top + 2.4f), Offset(vx + 1.2f, casa.top + 5.6f), 0.25f)
            if (encendida) mancha(Offset(vx + 1.2f, casa.top + 4f), 9f, 6f, C.ACEITE, 0.34f * maxOf(l.noche, 0.4f))
        }
        if (suEstancia) {
            // La cuerda de tender, de la casa a un poste: una camisa, un paño y su abrigo oscuro.
            val a = Offset(casa.right, casa.top + 1f); val b = Offset(casa.right + 13f, casa.top + 1.6f)
            drawLine(l.tono(C.MADERA_OSC, lj), Offset(b.x, pie + 1.6f), b + Offset(0f, -1f), 0.7f); drawLine(l.tono(C.RED, lj), a, b, 0.3f)
            drawRect(l.tono(C.CAL, lj), a + Offset(1.6f, 0.2f), Size(2.6f, 3.6f)); drawRect(l.tono(C.PAPEL, lj), a + Offset(4.8f, 0.3f), Size(2f, 2.6f))
            drawPath(poligono(a + Offset(7.6f, 0.4f), a + Offset(11.4f, 0.5f), a + Offset(12f, 6.6f), a + Offset(7.2f, 6.4f)), l.tono(C.PIZARRA_OSC, lj))
        }
        // La torre, que se estrecha hacia arriba: el lado del sol, la cal y el lado en sombra.
        val torre = poligono(Offset(x - 4.3f * k, pie), Offset(x - 3.1f * k, hombro), Offset(x + 3.1f * k, hombro), Offset(x + 4.3f * k, pie))
        drawPath(torre, Brush.horizontalGradient(if (alSol) listOf(sombra, cal, luz) else listOf(luz, cal, sombra), x - 4.3f * k, x + 4.3f * k))
        clipPath(torre) {
            drawRect(l.tono(C.ALMAGRE, lj), Offset(x - 8f, pie - 9.4f * k), Size(16f, 3.2f * k))
            drawRect(Brush.horizontalGradient(if (alSol) listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent) else listOf(Color.Transparent, Color.Black.copy(alpha = 0.25f)), x - 4.3f * k, x + 4.3f * k), Offset(x - 8f, pie - 9.4f * k), Size(16f, 3.2f * k))
            // El zócalo de piedra, las saeteras de la escalera y la humedad del pie.
            drawRect(l.tono(C.PIEDRA_OSC, lj), Offset(x - 8f, pie - 2.6f), Size(16f, 2.6f))
            for (vy in listOf(pie - 12.6f * k, pie - 6.2f * k, pie - 3.4f * k)) { drawRect(l.tono(C.PIZARRA_OSC, lejos), Offset(x - 0.7f, vy), Size(1.4f, 2.4f)); drawRect(l.tono(C.PIEDRA_OSC, lj).copy(alpha = 0.6f), Offset(x - 1.1f, vy + 2.4f), Size(2.2f, 0.4f)) }
            drawRect(l.tono(C.MADERA_OSC, lj), Offset(x - 1.3f, pie - 4.4f), Size(2.6f, 4.4f))
            val az = Azar(Semilla.de("arcano", "faro")); repeat(6) { mancha(Offset(x + az.entre(-5f, 5f), az.entre(hombro, pie)), 1.6f, az.entre(2f, 5f), l.tono(if (it % 2 == 0) Color(0xFF8E8E7C) else C.LIQUEN, lj), 0.22f) }
        }
        // La galería con su barandilla y sus ménsulas, la linterna de vidrio y la cúpula con su veleta.
        drawPath(poligono(Offset(x - 3.3f * k, hombro + 1.4f), Offset(x - 4.6f * k, hombro - 0.2f), Offset(x + 4.6f * k, hombro - 0.2f), Offset(x + 3.3f * k, hombro + 1.4f)), hierro)
        drawRect(hierro, Offset(x - 4.6f * k, hombro - 1.3f), Size(9.2f * k, 1.3f))
        for (i in 0..6) drawLine(hierro, Offset(x - 4.4f * k + i * 8.8f * k / 6f, hombro - 3.2f * k), Offset(x - 4.4f * k + i * 8.8f * k / 6f, hombro - 1f), 0.35f)
        drawLine(hierro, Offset(x - 4.5f * k, hombro - 3.2f * k), Offset(x + 4.5f * k, hombro - 3.2f * k), 0.45f)
        val encendido = E.faroEncendido(m)
        val vidrio = Rect(x - 2.3f * k, hombro - 7f * k, x + 2.3f * k, hombro - 0.8f * k)
        drawRect(if (encendido) C.FARO else lerp(l.tono(Color(0xFF9CB4B4), lejos), l.bajo, 0.35f), vidrio.topLeft, vidrio.size)
        if (!encendido) { drawOval(l.tono(C.LIQUEN, lj), Offset(x - 1.2f, vidrio.center.y - 1.6f), Size(2.4f, 3.2f)); drawLine(Color.White.copy(alpha = 0.5f * (1f - 0.6f * l.noche)), vidrio.topLeft + Offset(0.8f, 1f), vidrio.topLeft + Offset(0.8f, vidrio.height - 1f), 0.4f) }
        for (i in 0..4) drawLine(hierro, Offset(vidrio.left + i * vidrio.width / 4f, vidrio.top), Offset(vidrio.left + i * vidrio.width / 4f, vidrio.bottom), 0.35f)
        drawLine(hierro, Offset(vidrio.left, vidrio.center.y), Offset(vidrio.right, vidrio.center.y), 0.3f)
        val cupula = Path().apply { moveTo(x - 3f * k, vidrio.top); cubicTo(x - 2.6f * k, vidrio.top - 3.6f * k, x + 2.6f * k, vidrio.top - 3.6f * k, x + 3f * k, vidrio.top); close() }
        drawPath(cupula, Brush.horizontalGradient(if (alSol) listOf(l.tono(mult(C.ALMAGRE, r.color, 0.6f), lj), l.tono(lerp(C.ALMAGRE, C.LUZ, 0.3f * r.dia), lj)) else listOf(l.tono(lerp(C.ALMAGRE, C.LUZ, 0.3f * r.dia), lj), l.tono(mult(C.ALMAGRE, r.color, 0.6f), lj)), x - 3f * k, x + 3f * k))
        drawRect(hierro, Offset(x - 3.2f * k, vidrio.top - 0.5f), Size(6.4f * k, 0.9f))
        drawLine(hierro, Offset(x, vidrio.top - 2.6f * k), Offset(x, vidrio.top - 5f * k), 0.5f); drawCircle(hierro, 0.8f, Offset(x, vidrio.top - 3.2f * k)); drawLine(hierro, Offset(x - 1.6f, vidrio.top - 4.6f * k), Offset(x + 2.2f, vidrio.top - 4.6f * k), 0.4f)
    }

    /** La luz quieta del faro: el resplandor de la linterna y su reflejo en el agua. El haz que gira va en la parte viva. */
    private fun DrawScope.luzDelFaro(m: Momento, l: Luces) {
        if (!E.faroEncendido(m)) return
        val p = EscenaArcano.LINTERNA; val n = nivel(m)
        val k = maxOf(l.noche, if (n >= 2) 0.4f else 0f)
        haloDeBruma(p, 30f + 10f * n, 22f + 8f * n, C.FARO, k * (0.7f + 0.2f * n))
        drawCircle(Color.White.copy(alpha = 0.95f), 2.2f, p)
        reflejoRoto(p.x - 5f, p.x + 5f, PIE_PUNTA + 2f, 36f, C.FARO, 0.5f * k, m.azar("reflejo faro"), 1.1f)
    }

    /** Las peñas de delante de la punta, con su espuma; en la bajamar asoman más. Y, tres semanas, la barca que el temporal echó contra ellas. */
    private fun DrawScope.penas(m: Momento, l: Luces, r: Rayo, marea: Float) {
        val lejos = E.perdido(m, 0.36f)
        val az = Azar(Semilla.de("arcano", "penas"))
        for ((cx, w, h) in listOf(Triple(281f, 14f, 8.6f), Triple(294.6f, 8f, 4.6f), Triple(273f, 6f, 3.4f), Triple(305f, 5f, 2.4f))) {
            val alto = h + (1f - marea) * 2.6f; val base = PIE_PUNTA + 2.4f + (cx - 270f) * 0.06f
            val forma = poligono(listOf(Offset(cx - w / 2f, base), Offset(cx - w * 0.3f, base - alto * 0.7f), Offset(cx - w * 0.05f, base - alto), Offset(cx + w * 0.22f, base - alto * 0.62f), Offset(cx + w / 2f, base)))
            reflejoRoto(cx - w / 2f, cx + w / 2f, base + 0.4f, alto * 1.2f, l.tono(C.PIEDRA_NEGRA, lejos), 0.45f, az, 0.8f)
            drawPath(forma, l.tono(C.PIEDRA_NEGRA, lejos))
            drawPath(poligono(Offset(cx - w * 0.05f, base - alto), Offset(cx + (if (r.lat >= 0f) w * 0.22f else -w * 0.3f), base - alto * 0.65f), Offset(cx + (if (r.lat >= 0f) w * 0.5f else -w * 0.5f), base)), l.tono(lerp(C.PIEDRA_OSC, C.LUZ, 0.3f * r.dia), lejos))
            drawLine(l.tono(Color.White, lejos * 0.5f).copy(alpha = 0.8f), Offset(cx - w * 0.62f, base + 0.3f), Offset(cx + w * 0.62f, base + 0.3f), 0.8f, StrokeCap.Round)
        }
        if (E.naufragio(m)) {
            // El casco, de costado sobre la peña grande, con la quilla al aire y el palo roto.
            val o = Offset(275.4f, PIE_PUNTA - 3.4f)
            drawPath(Path().apply { moveTo(o.x, o.y); cubicTo(o.x + 3f, o.y - 4.6f, o.x + 9f, o.y - 5f, o.x + 12.4f, o.y - 1.4f); lineTo(o.x + 10f, o.y + 1.4f); lineTo(o.x + 2f, o.y + 1.6f); close() }, l.tono(C.MADERA_OSC, lejos))
            drawLine(l.tono(C.ALMAGRE, lejos), o + Offset(1.6f, -1f), o + Offset(11f, -2f), 0.8f)
            drawLine(l.tono(C.MADERA, lejos), o + Offset(7f, -3.6f), o + Offset(12.6f, -8.6f), 0.6f)
            for (i in 0..2) drawLine(l.tono(C.PIEDRA_NEGRA, lejos), o + Offset(3f + i * 2.6f, -3.4f), o + Offset(3.4f + i * 2.6f, 0.6f), 0.35f)
        }
    }

    // ---------------------------------------------------------------- La orilla del pueblo y el puerto

    /** La tierra sobre la que se asienta el pueblo: su orilla de roca hasta el horizonte y el borde del muelle viejo. */
    private fun DrawScope.tierraDelPueblo(m: Momento, l: Luces, fijo: Azar) {
        val az = Azar(Semilla.de("arcano", "orilla"))
        val borde = COSTA.reversed() + PUERTO.drop(1)
        val tierra = Path().apply { moveTo(borde[0].x, borde[0].y); for (p in borde.drop(1)) lineTo(p.x, p.y); lineTo(226f, FONDO + 4f); lineTo(ANCHO + 4f, FONDO + 4f); close() }
        // El reflejo de la orilla, pegado a ella.
        for (i in 0 until COSTA.size - 1) { val lejos = E.perdido(m, 0.2f + 0.5f * i / COSTA.size); reflejoRoto(COSTA[i].x - 5f, COSTA[i + 1].x - 3f, maxOf(COSTA[i].y, COSTA[i + 1].y) + 0.6f, 6f - 0.4f * i, l.tono(Color(0xFF3C4E4A), lejos), 0.42f, az, 0.9f) }
        drawPath(tierra, Brush.verticalGradient(listOf(l.tono(lerp(C.MONTE, C.PIEDRA_OSC, 0.4f), E.perdido(m, 0.7f)), l.tono(lerp(C.PIEDRA_OSC, C.MONTE, 0.3f), E.perdido(m, 0.2f))), H, 200f))
        // Las rocas de la orilla y su espuma: la franja entre el pueblo y el agua.
        for (i in 0 until COSTA.size - 1) {
            val a = COSTA[i]; val b = COSTA[i + 1]; val lejos = E.perdido(m, 0.2f + 0.5f * i / COSTA.size); val g = 3.4f - 0.26f * i
            drawPath(poligono(a, b, b + Offset(g * 1.6f, g * 0.5f), a + Offset(g * 1.9f, g * 0.6f)), l.tono(C.PIEDRA_NEGRA, lejos))
            repeat(3) { val t = az.real(); val p = a + (b - a) * t; drawPath(poligono(p + Offset(-g * 0.5f, 0.4f), p + Offset(az.entre(-0.5f, 0.6f) * g, -g * az.entre(0.5f, 1.1f)), p + Offset(g * 0.9f, 0.5f)), l.tono(lerp(C.PIEDRA_NEGRA, C.PIEDRA_OSC, az.real()), lejos)) }
            drawLine(l.tono(Color.White, lejos * 0.5f).copy(alpha = 0.6f), a + Offset(-1.6f, 0.8f), b + Offset(-1.4f, 0.8f), 0.7f, StrokeCap.Round)
        }
    }

    /** Lo que el muelle, las barcas y la playa dejan en el agua. */
    private fun DrawScope.reflejosDelPuerto(m: Momento, l: Luces, marea: Float) {
        val az = Azar(Semilla.de("arcano", "reflejos puerto"))
        val hasta = largoDelMuelle(m)
        var t = 0f
        while (t < hasta - 0.02f) { val a = enElMuelle(t); val b = enElMuelle(t + 0.06f); reflejoRoto(b.x, a.x, a.y + pared(t, marea) + 0.4f, 7f - 3f * t, l.tono(Color(0xFF28383A), E.perdido(m, 0.3f)), 0.5f, az, 0.9f); t += 0.06f }
        // El borde del puerto, del lado del pueblo.
        for (i in 0 until PUERTO.size - 1) reflejoRoto(PUERTO[i + 1].x - 6f, PUERTO[i].x - 1f, PUERTO[i].y + 4.6f, 7f, l.tono(Color(0xFF2E3E3E), E.perdido(m, 0.2f)), 0.42f, az)
        bajio(l, curva(PLAYA.map { it + Offset(0f, -3f) }), 16f, tinteDelMar(m, C.MAR_ORILLA))
    }

    /**
     * El muelle, de piedra negra y comido por el mar: la calzada, la pared con sus argollas y su franja de verdín, la
     * escalera que baja al agua y el farol de la punta. Con la marea baja se ve más pared y más escalones.
     */
    private fun DrawScope.muelle(m: Momento, l: Luces, r: Rayo, marea: Float) {
        val hasta = largoDelMuelle(m)
        val az = Azar(Semilla.de("arcano", "muelle"))
        fun lj(t: Float) = E.perdido(m, 0.18f + 0.22f * t)
        fun arriba(t: Float) = enElMuelle(t) + Offset(0.6f * (1f - t), -(4f - 1.6f * t))
        val pasos = 18
        // La pared que mira al puerto.
        for (i in 0 until (pasos * hasta).toInt()) {
            val t0 = i / pasos.toFloat(); val t1 = (i + 1) / pasos.toFloat()
            val a = enElMuelle(t0); val b = enElMuelle(t1); val ha = pared(t0, marea); val hb = pared(t1, marea)
            val q = poligono(a, b, b + Offset(0f, hb), a + Offset(0f, ha))
            drawPath(q, Brush.verticalGradient(listOf(l.tono(lerp(C.PIEDRA_NEGRA, C.PIEDRA_OSC, 0.35f + 0.3f * az.real()), lj(t0)), l.tono(C.PIEDRA_NEGRA, lj(t0))), a.y, a.y + ha))
            // La franja de verdín y de lapas que la marea deja al aire.
            val verdin = ha * (0.34f + 0.3f * (1f - marea))
            drawPath(poligono(a + Offset(0f, ha - verdin), b + Offset(0f, hb - verdin * 0.9f), b + Offset(0f, hb), a + Offset(0f, ha)), l.tono(Color(0xFF2A3A30), lj(t0)).copy(alpha = 0.85f))
            drawLine(l.tono(Color(0xFF20282A), lj(t0)), b, b + Offset(0f, hb), 0.4f)
            if (i % 3 == 1) { val o = a + Offset(-2f, ha * 0.32f); drawCircle(l.tono(C.LIQUEN, lj(t0)).copy(alpha = 0.7f), 0.9f - 0.3f * t0, o, style = Stroke(0.4f)) }            // una argolla, con su herrumbre
            drawLine(l.tono(Color.White, lj(t0) * 0.5f).copy(alpha = 0.45f), a + Offset(0f, ha + 0.3f), b + Offset(0f, hb + 0.3f), 0.5f)
        }
        // La calzada: losas claras, gastadas, con el canto que mira al sol.
        val calzada = Path().apply { moveTo(enElMuelle(0f)); lineTo(enElMuelle(hasta)); lineTo(arriba(hasta)); lineTo(arriba(0f)); close() }
        drawPath(calzada, Brush.horizontalGradient(listOf(l.tono(lerp(C.PIEDRA_OSC, C.LUZ, 0.2f * r.dia), lj(1f)), l.tono(lerp(C.PIEDRA_OSC, C.PIEDRA_NEGRA, 0.25f), lj(0f))), CABEZA.x, RAIZ.x))
        clipPath(calzada) { for (i in 0..(26 * hasta).toInt()) { val t = i / 26f; drawLine(l.tono(C.PIEDRA_OSC, lj(t)).copy(alpha = 0.6f), enElMuelle(t), arriba(t), 0.35f) }; repeat(10) { val t = az.real() * hasta; mancha(enElMuelle(t) + Offset(0f, -1.6f), 5f, 1.2f, l.tono(C.PIEDRA_NEGRA, lj(t)), 0.3f) } }
        drawLine(l.tono(lerp(C.PIEDRA, Color.White, 0.3f), lj(0.5f)).copy(alpha = 0.7f), enElMuelle(0f), enElMuelle(hasta), 0.4f)
        // Los norays, un montón de redes y unas nasas.
        for (t in listOf(0.1f, 0.3f, 0.5f, 0.72f, 0.92f)) if (t < hasta) { val p = enElMuelle(t) + Offset(0f, -1f); drawRect(l.tono(C.HIERRO, lj(t)), p + Offset(-0.7f, -2.2f + t), Size(1.4f - 0.4f * t, 2.2f - t)) }
        if (hasta > 0.4f) { val p = enElMuelle(0.36f) + Offset(0f, -2.4f); mancha(p, 5.4f, 1.8f, l.tono(C.RED, lj(0.36f)), 0.95f); for (i in 0..3) drawCircle(l.tono(C.ALMAGRE, lj(0.36f)), 0.5f, p + Offset(-3f + i * 2f, -0.6f + (i % 2) * 0.6f)) }
        for (i in 0..2) { val p = enElMuelle(0.16f + i * 0.022f) + Offset(0f, -2.6f - (i % 2) * 1.4f); drawRect(l.tono(C.MADERA, lj(0.16f)), p + Offset(-1.3f, -1.2f), Size(2.6f, 1.6f)); drawLine(l.tono(C.MADERA_OSC, lj(0.16f)), p + Offset(-1.3f, -0.4f), p + Offset(1.3f, -0.4f), 0.3f) }
        // La escalera que baja al agua «y se pierde dentro»: con la marea baja se ven más escalones.
        val te = 0.44f
        if (te < hasta) {
            val o = enElMuelle(te); val alto = pared(te, marea)
            val cuantos = if (E.mareaBajisima(m)) 11 else (3 + (1f - marea) * 4f).toInt()
            for (i in 0 until cuantos) { val p = o + Offset(-1.6f - i * 1.9f, 0.4f + i * (alto / (cuantos.coerceAtMost(7)).toFloat()) * 0.92f); drawRect(l.tono(lerp(C.PIEDRA, C.PIEDRA_NEGRA, (i / 7f).coerceAtMost(0.8f)), lj(te)), p, Size(2.2f, 0.9f)); drawRect(l.tono(Color(0xFF20282A), lj(te)), p + Offset(0f, 0.9f), Size(2.2f, 0.7f)) }
        }
        // Después del temporal: los sillares caídos, medio hundidos, donde estuvo la mitad de fuera.
        if (hasta < 1f) {
            val roto = Azar(Semilla.de("arcano", "muelle roto"))
            drawPath(poligono(enElMuelle(hasta), enElMuelle(hasta) + Offset(-3.6f, 1.4f), enElMuelle(hasta) + Offset(-2.4f, pared(hasta, marea)), enElMuelle(hasta) + Offset(0f, pared(hasta, marea))), l.tono(C.PIEDRA_NEGRA, lj(hasta)))
            repeat(16) { val t = hasta + 0.02f + roto.real() * (1f - hasta); val p = enElMuelle(t) + Offset(roto.entre(-3f, 3f), roto.entre(0.4f, 4.2f - 2f * t) + (1f - marea) * -1f); val w = roto.entre(1.6f, 4.4f) * (1.2f - 0.5f * t)
                drawPath(poligono(p, p + Offset(w, roto.entre(-0.4f, 0.4f)), p + Offset(w * 0.9f, w * 0.5f), p + Offset(0.2f, w * 0.44f)), l.tono(lerp(C.PIEDRA_NEGRA, C.PIEDRA_OSC, roto.real() * 0.6f), lj(t)))
                drawLine(l.tono(Color.White, lj(t) * 0.5f).copy(alpha = 0.5f), p + Offset(-0.6f, w * 0.5f), p + Offset(w + 0.6f, w * 0.5f), 0.5f) }
        }
        // El farol de la punta, mientras hay punta.
        if (hasta >= 1f) {
            val pie = CABEZA + Offset(-2.2f, -2.2f); val f = EscenaArcano.FAROL
            drawLine(l.tono(C.HIERRO, lj(1f)), pie, f + Offset(0f, 1.6f), 0.8f); drawLine(l.tono(C.HIERRO, lj(1f)), f + Offset(-1.6f, 1.6f), f + Offset(1.6f, 1.6f), 0.5f)
            val on = farolEncendido(m)
            drawRect(if (on) C.ACEITE else lerp(l.tono(Color(0xFF9CB0AC), lj(1f)), l.bajo, 0.3f), f + Offset(-1.2f, -1.6f), Size(2.4f, 3.2f)); drawPath(poligono(f + Offset(-1.8f, -1.6f), f + Offset(0f, -3.2f), f + Offset(1.8f, -1.6f)), l.tono(C.HIERRO, lj(1f)))
            if (on) { haloDeBruma(f, 16f + 8f * nivel(m), 12f + 6f * nivel(m), C.ACEITE, maxOf(l.noche, 0.5f) * (0.8f + 0.2f * nivel(m))); reflejoRoto(f.x - 2.4f, f.x + 2.4f, CABEZA.y + pared(1f, marea) + 0.6f, 20f, C.ACEITE, 0.55f * maxOf(l.noche, 0.4f), m.azar("reflejo farol"), 1.1f) }
        }
        // Las sombras cortas de lo que hay en el muelle.
        for (t in listOf(0.1f, 0.3f, 0.5f, 0.72f, 0.92f)) if (t < hasta) { val p = enElMuelle(t) + Offset(0f, -1f); sombraOvalo(r, Offset(p.x - 1f + r.ax * 1.4f, p.y - 0.5f), Size(2.4f + abs(r.ax), 0.9f), 0.8f) }
        // Los días señalados: banderines a lo largo del muelle y, de noche, sus faroles de papel.
        if (E.bendicion(m) || E.procesion(m) || E.hoy(m, "carmen")) {
            val colores = listOf(C.ALMAGRE, C.CAL, C.LIQUEN, C.MONTE)
            var t = 0.04f
            while (t < hasta - 0.2f) { val a = enElMuelle(t) + Offset(0f, -9f + 3f * t); val b = enElMuelle(t + 0.2f) + Offset(0f, -9f + 3f * (t + 0.2f)); drawLine(l.tono(C.MADERA_OSC, lj(t)), enElMuelle(t) + Offset(0f, -2f), a, 0.5f); banderines(l, a, b, 2.4f, colores, 2.6f - t, lejos = lj(t)); t += 0.2f }
            drawLine(l.tono(C.MADERA_OSC, lj(t)), enElMuelle(t) + Offset(0f, -2f), enElMuelle(t) + Offset(0f, -9f + 3f * t), 0.5f)
            if (E.procesion(m) && l.noche > 0.3f) for (i in 0..7) { val u = 0.06f + i * 0.11f; if (u < hasta) { val p = enElMuelle(u) + Offset(0f, -5.4f + 2f * u); drawCircle(C.ACEITE, 0.9f, p); mancha(p, 5f, 4f, C.ACEITE, 0.35f * l.noche); reflejoRoto(p.x - 1.4f, p.x + 1.4f, enElMuelle(u).y + pared(u, marea) + 0.6f, 10f, C.ACEITE, 0.45f * l.noche, az, 1.1f) } }
        }
    }

    /** El farol del muelle se enciende al anochecer y con niebla. */
    internal fun farolEncendido(m: Momento): Boolean = !E.muelleRoto(m) && (E.luces(m).noche > 0.3f || nivel(m) >= 2)

    /** Qué barcas están hoy a flote en el puerto. Varadas, ninguna; faenando, dos se quedan. */
    internal fun barcasAFlote(m: Momento): List<Int> = when {
        E.barcasVaradas(m) -> emptyList()
        E.barcasFuera(m) -> listOf(2, 5)
        E.virgenEnBarca(m) -> listOf(1, 4, 5)
        E.muelleRoto(m) -> listOf(0, 1, 2, 3, 5)                                // sin la mitad de fuera, queda menos abrigo
        else -> BARCAS.indices.toList()
    }

    /** El casco de una barca de pesca de dos proas, visto de costado desde arriba: [popa] es su extremo de la izquierda en la línea del agua. */
    private fun casco(popa: Offset, largo: Float, alto: Float) = Path().apply {
        moveTo(popa.x - largo * 0.02f, popa.y - alto * 1.25f); cubicTo(popa.x + largo * 0.3f, popa.y - alto * 0.62f, popa.x + largo * 0.7f, popa.y - alto * 0.62f, popa.x + largo * 1.02f, popa.y - alto * 1.3f)
        cubicTo(popa.x + largo * 0.98f, popa.y - alto * 0.2f, popa.x + largo * 0.9f, popa.y + alto * 0.2f, popa.x + largo * 0.84f, popa.y + alto * 0.24f)
        lineTo(popa.x + largo * 0.14f, popa.y + alto * 0.24f); cubicTo(popa.x + largo * 0.08f, popa.y + alto * 0.2f, popa.x + largo * 0.02f, popa.y - alto * 0.2f, popa.x - largo * 0.02f, popa.y - alto * 1.25f); close()
    }

    /** Una barca con su casco embreado, su franja de color, sus bancos y, si lo lleva, su palo con la vela aferrada. */
    private fun DrawScope.barca(l: Luces, r: Rayo, popa: Offset, largo: Float, b: Barca, lejos: Float, engalanada: Boolean, palida: Boolean = false) {
        val alto = largo * 0.16f
        val forma = casco(popa, largo, alto)
        val brea = if (palida) lerp(C.PIEDRA, C.PAPEL, 0.4f) else Color(0xFF2E3234)
        drawPath(forma, Brush.verticalGradient(listOf(l.tono(lerp(brea, C.LUZ, 0.14f * r.dia), lejos), l.tono(brea, lejos)), popa.y - alto * 1.3f, popa.y + alto * 0.3f))
        clipPath(forma) {
            // La franja de la regala y lo de dentro: las cuadernas y los bancos.
            drawPath(Path().apply { moveTo(popa.x - 1f, popa.y - alto * 1.28f); cubicTo(popa.x + largo * 0.3f, popa.y - alto * 0.5f, popa.x + largo * 0.7f, popa.y - alto * 0.5f, popa.x + largo + 1f, popa.y - alto * 1.3f) }, l.tono(if (palida) C.PIEDRA else b.franja, lejos), style = Stroke(alto * 0.42f))
            drawPath(Path().apply { moveTo(popa.x + largo * 0.06f, popa.y - alto * 1.06f); cubicTo(popa.x + largo * 0.3f, popa.y - alto * 0.82f, popa.x + largo * 0.7f, popa.y - alto * 0.82f, popa.x + largo * 0.94f, popa.y - alto * 1.08f) }, l.tono(if (palida) C.PAPEL else lerp(C.MADERA, C.CARTON, 0.4f), lejos), style = Stroke(alto * 0.3f))
        }
        for (k in listOf(0.3f, 0.5f, 0.7f)) drawLine(l.tono(C.MADERA_OSC, lejos), Offset(popa.x + largo * k, popa.y - alto * 0.98f), Offset(popa.x + largo * k + 0.3f, popa.y - alto * 0.7f), 0.5f)
        drawLine(l.tono(lerp(brea, Color.White, 0.5f), lejos).copy(alpha = 0.6f), Offset(popa.x + largo * 0.1f, popa.y + alto * 0.14f), Offset(popa.x + largo * 0.88f, popa.y + alto * 0.14f), 0.35f)
        if (b.palo && !palida) {
            val pie = Offset(popa.x + largo * (if (b.derecha) 0.62f else 0.38f), popa.y - alto * 0.8f); val tope = pie + Offset(if (b.derecha) 0.8f else -0.8f, -largo * 0.62f)
            drawLine(l.tono(C.MADERA, lejos), pie, tope, 0.7f)
            // La verga, bajada a lo largo de la barca, con la vela de color teja aferrada.
            val a = Offset(popa.x + largo * 0.16f, popa.y - alto * 1.5f); val z = Offset(popa.x + largo * 0.86f, popa.y - alto * 1.7f)
            drawLine(l.tono(C.ALMAGRE, lejos), a, z, 1.5f, StrokeCap.Round); drawLine(l.tono(lerp(C.ALMAGRE, C.LUZ, 0.35f), lejos).copy(alpha = 0.8f), a + Offset(0f, -0.5f), z + Offset(0f, -0.5f), 0.4f)
            for (k in listOf(0.2f, 0.5f, 0.8f)) drawLine(l.tono(C.MADERA_OSC, lejos), a + (z - a) * k + Offset(0f, -0.9f), a + (z - a) * k + Offset(0f, 0.9f), 0.3f)
            drawLine(l.tono(C.HIERRO, lejos).copy(alpha = 0.6f), tope, Offset(popa.x + largo * (if (b.derecha) 1f else 0f), popa.y - alto * 1.3f), 0.25f)
            if (engalanada) banderines(l, tope, Offset(popa.x + largo * (if (b.derecha) 0f else 1f), popa.y - alto * 1.4f), 1.6f, listOf(C.ALMAGRE, C.CAL, C.LIQUEN, C.MONTE), 2.2f, lejos = lejos)
        }
    }

    private fun DrawScope.barcaAFlote(m: Momento, l: Luces, r: Rayo, b: Barca, i: Int) {
        val lejos = E.perdido(m, 0.12f + (192f - b.popa.y) / 110f)
        val az = Azar(Semilla.de("arcano", "barca", i))
        reflejoRoto(b.popa.x, b.popa.x + b.largo, b.popa.y + b.largo * 0.05f, b.largo * 0.3f, l.tono(Color(0xFF1E2A2C), lejos), 0.55f, az)
        if (b.palo) reflejoRoto(b.popa.x + b.largo * 0.44f, b.popa.x + b.largo * 0.56f, b.popa.y + b.largo * 0.05f, b.largo * 0.55f, l.tono(C.MADERA_OSC, lejos), 0.36f, az)
        barca(l, r, b.popa, b.largo, b, lejos, E.bendicion(m) || E.hoy(m, "carmen") || E.procesion(m))
        // El cabo que la amarra.
        drawLine(l.tono(C.RED, lejos).copy(alpha = 0.7f), Offset(b.popa.x + if (b.derecha) b.largo else 0f, b.popa.y - b.largo * 0.2f), Offset(b.popa.x + (if (b.derecha) b.largo + 5f else -5f), b.popa.y + 0.6f), 0.3f)
        drawLine(l.tono(Color.White, lejos * 0.5f).copy(alpha = 0.4f), Offset(b.popa.x + 1f, b.popa.y + b.largo * 0.04f), Offset(b.popa.x + b.largo - 1f, b.popa.y + b.largo * 0.04f), 0.4f)
    }

    /** La Virgen del Carmen da la vuelta a la bahía: una barca con su arco de flores y cuatro detrás, en fila. */
    private fun DrawScope.romeria(m: Momento, l: Luces, r: Rayo) {
        val avance = ((m.hora.toFloat() - 10f) / 10f).coerceIn(0f, 1f)
        val x0 = 262f - 130f * avance; val y0 = 141f + 7f * sin(avance * PI.toFloat())
        for (i in 4 downTo 0) {
            val popa = Offset(x0 + i * 20f, y0 - i * 2.2f); val largo = if (i == 0) 22f else 16f - i * 0.9f
            val lejos = E.perdido(m, 0.36f + 0.04f * i); val b = BARCAS[(i + 1) % BARCAS.size]
            reflejoRoto(popa.x, popa.x + largo, popa.y + 0.6f, largo * 0.3f, l.tono(Color(0xFF1E2A2C), lejos), 0.5f, m.azar("romeria", i))
            barca(l, r, popa, largo, Barca(popa, largo, false, b.franja, i != 0), lejos, engalanada = true)
            if (i == 0) {
                // El arco de flores y el manto blanco, sin figura: un bulto claro bajo el arco.
                drawPath(Path().apply { moveTo(popa.x + 6.4f, popa.y - 3f); cubicTo(popa.x + 6.4f, popa.y - 13f, popa.x + 15.6f, popa.y - 13f, popa.x + 15.6f, popa.y - 3f) }, l.tono(C.MONTE, lejos), style = Stroke(1.4f))
                for (k in 0..6) { val a = PI.toFloat() * k / 6f; drawCircle(l.tono(if (k % 2 == 0) C.CAL else C.ALMAGRE, lejos), 0.8f, Offset(popa.x + 11f - 4.6f * cos(a), popa.y - 3.4f - 7.6f * sin(a))) }
                drawPath(poligono(Offset(popa.x + 9f, popa.y - 3f), Offset(popa.x + 11f, popa.y - 9.6f), Offset(popa.x + 13f, popa.y - 3f)), l.tono(C.CAL, lejos))
            }
        }
    }

    // ---------------------------------------------------------------- La playa

    /**
     * La playa de este lado del puerto: arena gruesa, algas en la línea de la marea, los palos con las redes tendidas, el
     * cabrestante y, de octubre a mayo, las barcas varadas. Aquí llegan también la ballena y la barca que el mar devolvió.
     */
    private fun DrawScope.playa(m: Momento, l: Luces, r: Rayo, marea: Float, fijo: Azar) {
        val az = Azar(Semilla.de("arcano", "playa"))
        val baja = (1f - marea) * 3.4f
        val orilla = PLAYA.map { it + Offset(0f, -baja) }
        val arena = curva(orilla, 252f)
        val seco = l.tono(C.ARENA, E.perdido(m, 0.06f)); val mojada = l.tono(lerp(C.ARENA, C.PIEDRA_NEGRA, 0.5f), E.perdido(m, 0.08f))
        drawPath(arena, Brush.verticalGradient(listOf(mojada, lerp(mojada, seco, 0.7f), seco, lerp(seco, l.tono(C.PIEDRA_OSC, 0.03f), 0.3f)), 194f - baja, 244f))
        clipPath(arena) {
            // Guijarros, conchas y la línea de algas que deja la pleamar.
            repeat(260) { val p = Offset(az.entre(60f, 232f), az.entre(196f, 250f)); drawCircle(l.tono(listOf(C.PIEDRA, C.PIEDRA_OSC, C.CAL, C.PIEDRA_NEGRA)[az.entero(4)], 0.05f).copy(alpha = 0.7f), az.entre(0.3f, 0.9f), p) }
            for (i in 0 until PLAYA.size - 1) { val a = PLAYA[i] + Offset(0f, 2.6f); val b = PLAYA[i + 1] + Offset(0f, 2.6f); drawLine(l.tono(Color(0xFF3A4430), 0.05f).copy(alpha = 0.7f), a + Offset(0f, az.entre(-0.5f, 0.5f)), b + Offset(0f, az.entre(-0.5f, 0.5f)), az.entre(0.6f, 1.4f), StrokeCap.Round) }
            if (baja > 1.2f) repeat(8) { val x = az.entre(108f, 220f); mancha(Offset(x, 198f - baja * 0.4f), az.entre(4f, 10f), 0.9f, l.tono(Color.White, 0.05f), 0.3f) }
        }
        // La espuma de la orilla.
        drawPath(curva(orilla.map { it + Offset(0f, -0.6f) }), l.tono(Color.White, 0.05f).copy(alpha = 0.7f), style = Stroke(0.8f, cap = StrokeCap.Round))
        repeat(18) { val t = az.real(); val i = (t * (orilla.size - 1)).toInt().coerceAtMost(orilla.size - 2); val p = orilla[i] + (orilla[i + 1] - orilla[i]) * (t * (orilla.size - 1) - i); drawLine(l.tono(Color.White, 0.05f).copy(alpha = az.entre(0.25f, 0.6f)), p + Offset(0f, -1.6f - az.entre(0f, 2f)), p + Offset(az.entre(2f, 7f), -1.6f - az.entre(0f, 2f)), 0.5f, StrokeCap.Round) }

        val lejos = E.perdido(m, 0.07f)
        // Las barcas varadas, cada una calzada y ladeada.
        if (E.barcasVaradas(m)) {
            val orden = VARADAS.indices.sortedBy { VARADAS[it].y }
            for (i in orden) { val p = VARADAS[i]; val b = BARCAS[i]; sombraOvalo(r, Offset(p.x - 1f + r.ax * 3f, p.y - 1f), Size(b.largo * 0.92f + abs(r.ax) * 4f, 2.6f), 0.85f) }
            for (i in orden) { val p = VARADAS[i]; val b = BARCAS[i]
                drawLine(l.tono(C.MADERA_OSC, lejos), p + Offset(b.largo * 0.3f, 0.6f), p + Offset(b.largo * 0.24f, -b.largo * 0.1f), 0.7f); drawLine(l.tono(C.MADERA_OSC, lejos), p + Offset(b.largo * 0.7f, 0.6f), p + Offset(b.largo * 0.76f, -b.largo * 0.1f), 0.7f)
                barca(l, r, p + Offset(0f, -b.largo * 0.05f), b.largo * 0.9f, b, lejos, engalanada = false) }
        }
        if (E.barcaSeca(m)) {
            // La que el mar devolvió: apartada de las otras, blanqueada, sin una gota dentro.
            val p = Offset(140f, 216.6f)
            sombraOvalo(r, Offset(p.x - 1f + r.ax * 3f, p.y - 1f), Size(24f + abs(r.ax) * 4f, 2.8f), 0.85f)
            barca(l, r, p + Offset(0f, -1.2f), 24f, BARCAS[0], lejos, engalanada = false, palida = true)
        }
        // Los palos de las redes: tres vanos, con la red colgando en panzas y sus corchos.
        val palos = listOf(Offset(94f, 197f), Offset(113f, 198.6f), Offset(132f, 200.2f), Offset(151f, 201.4f), Offset(170f, 202.4f))
        if (r.fuerza > 0.05f) for (p in palos) drawLine(r.color, p, p + Offset(r.ax * 6f, -1.6f), 0.9f, alpha = r.fuerza * 0.8f, blendMode = BlendMode.Multiply)
        if (!E.temporal(m) && !E.ballena(m)) for (i in 0 until palos.size - 1) {
            val a = palos[i] + Offset(0f, -15f); val b = palos[i + 1] + Offset(0f, -15.6f)
            val red = Path().apply { moveTo(a); cubicTo(a.x + 5f, a.y + 3f, b.x - 5f, b.y + 3f, b.x, b.y); lineTo(b.x - 1f, b.y + 10f); cubicTo(b.x - 6f, b.y + 13.4f, a.x + 6f, a.y + 13f, a.x + 1f, a.y + 10f); close() }
            drawPath(red, l.tono(C.RED, lejos).copy(alpha = 0.5f))
            clipPath(red) { for (k in -6..14) { drawLine(l.tono(C.MADERA_OSC, lejos).copy(alpha = 0.55f), Offset(a.x + k * 2.2f, a.y), Offset(a.x + k * 2.2f + 9f, a.y + 16f), 0.25f); drawLine(l.tono(C.MADERA_OSC, lejos).copy(alpha = 0.55f), Offset(a.x + k * 2.2f + 9f, a.y), Offset(a.x + k * 2.2f, a.y + 16f), 0.25f) } }
            drawPath(Path().apply { moveTo(a); cubicTo(a.x + 5f, a.y + 3f, b.x - 5f, b.y + 3f, b.x, b.y) }, l.tono(C.MADERA_OSC, lejos), style = Stroke(0.5f))
            for (k in 1..5) { val t = k / 6f; val q = 1f - t; val p = Offset(q * q * q * a.x + 3f * q * q * t * (a.x + 5f) + 3f * q * t * t * (b.x - 5f) + t * t * t * b.x, q * q * q * a.y + 3f * q * q * t * (a.y + 3f) + 3f * q * t * t * (b.y + 3f) + t * t * t * b.y); drawCircle(l.tono(C.ALMAGRE, lejos), 0.7f, p) }
        }
        for (p in palos) { drawLine(l.tono(C.MADERA_OSC, lejos), p, p + Offset(0.4f, -16.4f), 1.1f); drawLine(l.tono(lerp(C.MADERA, C.LUZ, 0.3f * r.dia), lejos), p + Offset(if (r.lat >= 0f) 0.4f else -0.4f, -0.4f), p + Offset(if (r.lat >= 0f) 0.8f else 0f, -16f), 0.35f) }
        // El cabrestante de varar y unas nasas apiladas.
        val cab = Offset(212f, 206.6f)
        drawRect(l.tono(C.MADERA_OSC, lejos), cab + Offset(-2.2f, -5.4f), Size(4.4f, 5.4f)); drawLine(l.tono(C.MADERA, lejos), cab + Offset(-6f, -4f), cab + Offset(6f, -5f), 0.8f); drawLine(l.tono(C.RED, lejos), cab + Offset(0f, -2.4f), cab + Offset(-10f, 0.6f), 0.35f)
        for (i in 0..3) { val p = Offset(180f + (i % 2) * 4.6f + (i / 2) * 1.4f, 213f - (i / 2) * 3f); drawRoundRect(l.tono(lerp(C.RED, C.CARTON, 0.3f), lejos), p, Size(4.6f, 3f), androidx.compose.ui.geometry.CornerRadius(1.4f)); drawLine(l.tono(C.MADERA_OSC, lejos).copy(alpha = 0.7f), p + Offset(1.5f, 0f), p + Offset(1.5f, 3f), 0.25f); drawLine(l.tono(C.MADERA_OSC, lejos).copy(alpha = 0.7f), p + Offset(3.1f, 0f), p + Offset(3.1f, 3f), 0.25f) }
        if (E.ballena(m)) ballena(l, r, lejos)
    }

    /** La ballena muerta: un lomo largo y gris en la arena, con la aleta caída y el vientre claro. No se ve más. */
    private fun DrawScope.ballena(l: Luces, r: Rayo, lejos: Float) {
        val o = Offset(122f, 204f)
        sombraOvalo(r, Offset(o.x - 2f + r.ax * 4f, o.y - 1.6f), Size(64f + abs(r.ax) * 5f, 4f), 0.9f)
        val lomo = Path().apply { moveTo(o.x, o.y); cubicTo(o.x + 4f, o.y - 9f, o.x + 26f, o.y - 13f, o.x + 44f, o.y - 7f); cubicTo(o.x + 52f, o.y - 4.6f, o.x + 56f, o.y - 3.6f, o.x + 62f, o.y - 5.6f); lineTo(o.x + 60f, o.y - 1.6f); lineTo(o.x + 63f, o.y + 0.6f); cubicTo(o.x + 52f, o.y + 0.4f, o.x + 30f, o.y + 1f, o.x, o.y); close() }
        drawPath(lomo, Brush.verticalGradient(listOf(l.tono(lerp(C.PIZARRA, C.LUZ, 0.2f * r.dia), lejos), l.tono(C.PIZARRA_OSC, lejos), l.tono(Color(0xFF9A988A), lejos)), o.y - 12f, o.y + 1f))
        clipPath(lomo) { for (i in 0..9) drawLine(l.tono(Color(0xFFB4B0A0), lejos).copy(alpha = 0.5f), Offset(o.x + 3f + i * 2.6f, o.y - 1.6f), Offset(o.x + 22f + i * 2.2f, o.y + 0.6f), 0.3f) }
        drawPath(poligono(Offset(o.x + 17f, o.y - 2.6f), Offset(o.x + 24f, o.y + 1.4f), Offset(o.x + 14f, o.y + 1f)), l.tono(C.PIZARRA_OSC, lejos))
    }

    /** El temporal: los golpes de mar que rompen contra el muelle y saltan por encima. */
    private fun DrawScope.rompientes(m: Momento, l: Luces) {
        val az = m.azar("rompientes"); val blanco = l.tono(Color.White, 0.1f)
        val hasta = largoDelMuelle(m)
        repeat(7) { val t = az.real() * hasta; val p = enElMuelle(t) + Offset(0f, -3f); val alto = az.entre(5f, 13f) * (1.2f - 0.5f * t)
            mancha(p + Offset(0f, -alto * 0.4f), alto * 0.7f, alto * 0.7f, blanco, 0.5f)
            repeat(7) { drawCircle(blanco.copy(alpha = az.entre(0.3f, 0.8f)), az.entre(0.3f, 0.9f), p + Offset(az.entre(-alto * 0.7f, alto * 0.7f), -az.entre(0f, alto * 1.2f))) } }
        repeat(34) { val x = az.entre(84f, 270f); val y = az.entre(H + 10f, 196f); val w = az.entre(4f, 12f); drawPath(Path().apply { moveTo(x, y); cubicTo(x + w * 0.3f, y - 2f, x + w * 0.7f, y - 2f, x + w, y) }, blanco.copy(alpha = 0.6f), style = Stroke(0.8f, cap = StrokeCap.Round)) }
    }

    /** Las gaviotas del puerto: unas en el aire, sobre las barcas y las peñas, y dos posadas en el muelle. */
    private fun DrawScope.gaviotas(m: Momento, l: Luces) {
        val az = m.azar("gaviotas")
        val c = lerp(l.tono(Color.White), l.tono(C.PIZARRA_OSC), 0.35f)
        repeat(5 + az.entero(4)) {
            val p = if (it % 3 == 0) Offset(az.entre(250f, 300f), az.entre(84f, 112f)) else Offset(az.entre(110f, 250f), az.entre(132f, 164f)); val w = az.entre(1.8f, 3.6f)
            drawPath(Path().apply { moveTo(p.x - w, p.y - w * 0.3f); cubicTo(p.x - w * 0.5f, p.y - w * 0.7f, p.x - w * 0.2f, p.y - w * 0.2f, p.x, p.y); cubicTo(p.x + w * 0.2f, p.y - w * 0.2f, p.x + w * 0.5f, p.y - w * 0.7f, p.x + w, p.y - w * 0.2f) }, c.copy(alpha = 0.9f), style = Stroke(0.6f, cap = StrokeCap.Round))
        }
        for (t in listOf(0.24f, 0.62f)) if (t < largoDelMuelle(m)) { val p = enElMuelle(t) + Offset(0f, -4f + 1.4f * t); drawOval(l.tono(Color.White, 0.2f), p + Offset(-1.1f, -0.9f), Size(2.2f, 1.2f)); drawCircle(l.tono(Color.White, 0.2f), 0.5f, p + Offset(1f, -1.2f)) }
    }

    // ---------------------------------------------------------------- La parte viva

    fun DrawScope.bahiaViva(m: Momento, l: Luces, reloj: Float) {
        val n = nivel(m)
        if (!marQuieto(m)) destellos(l, Rect(84f, H + 1f, ANCHO, 196f), reloj, m.semilla("destellos"), if (n >= 2) 12 else 26)
        // El haz del faro: dos brazos de luz que giran; cuando uno apunta hacia aquí, la linterna da un destello.
        if (E.faroEncendido(m)) {
            val p = EscenaArcano.LINTERNA
            val k = maxOf(l.noche, if (n >= 2) 0.3f else 0f) * (0.55f + 0.12f * n)
            // Con el reloj quieto, el haz queda de lado y un poco hacia quien mira.
            val giro = (reloj / E.vueltaDelFaro(m)) * 2f * PI.toFloat() + 0.5f
            for (brazo in 0..1) {
                val a = giro + brazo * PI.toFloat()
                val lado = cos(a); val hondo = sin(a)                           // hondo > 0: viene hacia quien mira
                val largo = 170f * abs(lado) + 14f; val ancho = 3f + 11f * abs(lado) + 12f * hondo.coerceAtLeast(0f)
                val fin = p + Offset(lado * largo, 4f * hondo.coerceAtLeast(0f))
                val fuerza = k * (0.3f + 0.5f * abs(lado)) * (if (hondo < 0f) 0.7f else 1f)
                if (abs(lado) > 0.08f) drawPath(poligono(p, fin + Offset(0f, -ancho), fin + Offset(0f, ancho)), Brush.horizontalGradient(listOf(C.FARO.copy(alpha = (0.5f * fuerza).coerceIn(0f, 1f)), C.FARO.copy(alpha = 0f)), p.x, fin.x), blendMode = BlendMode.Plus)
                if (hondo > 0.86f) { val f = (hondo - 0.86f) / 0.14f; haloDeBruma(p, 22f + 22f * f, 16f + 16f * f, C.FARO, k * f * 1.1f); drawCircle(Color.White.copy(alpha = f.coerceIn(0f, 1f)), 3.4f, p) }
            }
            reflejo(Offset(p.x, PIE_PUNTA + 2f), 26f, C.FARO, 0.5f * k, reloj, 3)
        }
        if (farolEncendido(m)) reflejo(Offset(EscenaArcano.FAROL.x, CABEZA.y + 6f), 16f, C.ACEITE, 0.5f * maxOf(l.noche, 0.4f), reloj, 5)
        // El humo de la casa del torrero: todo el día mientras el archivista vive allí; con frío, mientras haya torrero.
        if (E.lugar(m) == EscenaArcano.Lugar.FARO || (E.torrero(m) != EscenaArcano.Torrero.NADIE && E.invierno(m) && n < 3)) humo(l, Offset(EscenaArcano.LINTERNA.x + 4.6f * K_FARO + 14.8f, PIE_FARO - 6f * K_FARO - 9f), 24f, 5.4f, reloj, -(0.2f + 0.8f * m.viento) * 0.6f, 0.15f, E.perdido(m, 0.3f), 40, 7, if (E.lugar(m) == EscenaArcano.Lugar.FARO) 0.95f else 0.6f)
        // La niebla se mueve: unos bancos que cruzan despacio, por delante del faro y por delante del muelle.
        if (n >= 1) {
            val color = lerp(l.bruma, l.tono(Color.White), 0.22f)
            val az = Azar(m.semilla("deriva"))
            repeat(2 + n) { i ->
                val y = if (i % 2 == 0) az.entre(H - 4f, H + 14f) else az.entre(150f, 200f); val w = az.entre(60f, 110f); val paso = az.entre(2.2f, 5f)
                val x = 60f + ((az.entre(0f, 450f) + reloj * paso) % 450f) - 60f
                clipRect(70f, 0f, ANCHO, 246f) { mancha(Offset(x, y), w, az.entre(5f, 10f), color, 0.14f + 0.07f * n) }
            }
        }
        // El temporal: la espuma que salta en el muelle.
        if (E.temporal(m)) {
            val hasta = largoDelMuelle(m); val blanco = l.tono(Color.White, 0.1f)
            for (i in 0..4) { val t0 = ((reloj * 0.31f + i * 0.2f) % 1f); val t = (i * 0.19f + 0.06f) * hasta; val p = enElMuelle(t) + Offset(0f, -3f); val sube = sin(t0 * PI.toFloat())
                repeat(5) { j -> drawCircle(blanco.copy(alpha = (0.75f * sube).coerceIn(0f, 1f)), 0.5f + 0.5f * sube, p + Offset((j - 2) * 2.2f * sube, -(4f + j * 1.6f + i) * sube)) } }
        }
        // Las velas de la romería se mecen muy poco: basta con su reflejo, que tiembla.
        if (E.procesion(m) && l.noche > 0.3f) for (i in 0..3) { val u = 0.1f + i * 0.2f; if (u < largoDelMuelle(m)) reflejo(enElMuelle(u) + Offset(0f, 7f - 3f * u), 9f, C.ACEITE, 0.4f * l.noche, reloj, 10 + i) }
    }
}
