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
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Ger
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.caballoLejano
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.ger
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.ovoo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.bajio
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraCorta
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * Lo lejano del campamento de verano ([VeranoMongol]) y el río. De atrás adelante:
 * - tres cadenas de montañas azules, cada una más oscura y más baja; la del fondo, con la nieve vieja en las cumbres;
 * - el cerro del valle de invierno, a la izquierda, con su ovoo recortado contra el cielo y sus cárcavas;
 * - la estepa del otro lado del río, en cuatro lomas que se enciman: la cresta más amarilla y de luz, la hondonada más
 *   verde y en sombra, con las rachas del viento, las sendas del ganado y las manadas como puntos con su sombra;
 * - otros campamentos en los recodos del río, cada uno con su humo (el humo va en la parte viva);
 * - el río, ancho y lento: un remanso detrás de la tienda de Bat y el tramo que cruza por detrás del campamento, con
 *   los bancos de arena, la orilla de allá en sombra y la de acá de grava, los reflejos de la orilla y del cielo, la
 *   estela del sol bajo o de la luna; con lluvia, crecido y del color del té.
 */
internal object LejosVerano {
    private val C = ColorMongol
    private const val H = VeranoMongol.H

    /** Los campamentos de los recodos del río: dónde pisan sus tiendas (su humo sale un poco más arriba). */
    val CAMPAMENTOS = listOf(Offset(340f, 110.4f), Offset(249f, 114.8f), Offset(32f, 121.2f))

    /** Donde arde la lumbre de Mönkh la noche que cuida solo los caballos: lejos, en una loma, junto a la manada. */
    val LUMBRE_DE_MONKH = Offset(178f, 118.6f)

    /** La cresta del cerro del valle de invierno, puesta a mano, con la cima del ovoo. */
    private val CERRO = listOf(
        Offset(-12f, 108f), Offset(2f, 103.4f), Offset(13f, 98.6f), Offset(22f, 94.6f), Offset(30f, 90.4f), Offset(38f, 87.6f), Offset(44f, 86.2f), Offset(49f, 85.4f),
        Offset(54f, 85.6f), Offset(59f, 86.8f), Offset(64f, 88.8f), Offset(72f, 91.2f), Offset(80f, 94.6f), Offset(86f, 95.6f), Offset(93f, 98.2f), Offset(104f, 101.6f),
        Offset(118f, 104.4f), Offset(134f, 106.2f), Offset(152f, 107.2f),
    )
    private val CIMA = Offset(51f, 85.6f)

    /** La cresta de la sierra del fondo, puesta a mano: cuatro cumbres que pasan de la línea de la nieve vieja, con sus collados. */
    private val SIERRA = listOf(
        Offset(96f, 103f), Offset(112f, 99f), Offset(124f, 96.5f), Offset(136f, 93f), Offset(146f, 91.5f), Offset(154f, 88f), Offset(160f, 89.5f), Offset(168f, 86f),
        Offset(176f, 83.5f), Offset(182f, 84.6f), Offset(190f, 81f), Offset(198f, 78.6f), Offset(204f, 80.2f), Offset(212f, 82.8f), Offset(220f, 80.4f), Offset(228f, 77.2f),
        Offset(236f, 75.6f), Offset(242f, 77.4f), Offset(250f, 80.6f), Offset(258f, 79.2f), Offset(266f, 82.4f), Offset(276f, 85.6f), Offset(286f, 83.8f), Offset(296f, 86.4f),
        Offset(306f, 88.8f), Offset(318f, 87.2f), Offset(330f, 90.6f), Offset(344f, 92.2f), Offset(356f, 95.4f), Offset(370f, 94.6f), Offset(384f, 97.8f), Offset(400f, 100f),
    )

    /** La orilla de allá del río (de la derecha a la izquierda), puesta a mano: entrantes, salientes y tramos rectos. */
    val ARRIBA = listOf(
        Offset(392f, 110.0f), Offset(372f, 110.4f), Offset(356f, 111.0f), Offset(344f, 111.8f), Offset(330f, 111.4f), Offset(316f, 111.9f), Offset(302f, 111.6f),
        Offset(288f, 112.2f), Offset(274f, 113.0f), Offset(262f, 114.4f), Offset(250f, 115.6f), Offset(240f, 117.0f), Offset(232f, 118.4f), Offset(224f, 119.6f),
        Offset(216f, 120.6f), Offset(208f, 121.4f), Offset(198f, 122.4f), Offset(184f, 123.0f), Offset(170f, 122.8f), Offset(156f, 122.2f), Offset(142f, 122.4f),
        Offset(128f, 123.8f), Offset(116f, 124.8f), Offset(104f, 124.4f), Offset(92f, 123.2f), Offset(78f, 122.4f), Offset(62f, 122.6f), Offset(46f, 123.4f),
        Offset(30f, 123.2f), Offset(14f, 123.6f), Offset(-12f, 124.4f),
    )

    /** La orilla de acá, con los bancos de grava que la meten en el agua y los entrantes donde se ensancha. */
    val ABAJO = listOf(
        Offset(392f, 112.8f), Offset(374f, 114.2f), Offset(358f, 115.8f), Offset(344f, 117.6f), Offset(330f, 119.4f), Offset(314f, 120.6f), Offset(298f, 121.2f),
        Offset(282f, 121.8f), Offset(268f, 123.0f), Offset(256f, 124.8f), Offset(246f, 126.2f), Offset(238f, 127.6f), Offset(230f, 129.0f), Offset(222f, 130.2f),
        Offset(212f, 131.6f), Offset(198f, 133.4f), Offset(182f, 134.6f), Offset(166f, 134.8f), Offset(152f, 133.8f), Offset(140f, 132.6f), Offset(128f, 132.6f),
        Offset(116f, 133.8f), Offset(104f, 135.8f), Offset(90f, 137.6f), Offset(74f, 138.8f), Offset(58f, 139.2f), Offset(40f, 138.8f), Offset(22f, 139.4f),
        Offset(-12f, 140.4f),
    )

    /** El borde de la playa de grava de la orilla de acá, donde empieza la hierba: ancha en los bancos, estrecha en los tramos rectos. */
    val PLAYA: List<Offset> = run {
        val az = Azar(Semilla.de("mongol", "verano", "playa"))
        val anchos = listOf(1.4f, 1.6f, 2f, 2.4f, 2f, 1.8f, 2.6f, 3f, 2.4f, 2f, 2.6f, 3.4f, 4f, 3.4f, 4.4f, 5.4f, 6.6f, 8.4f, 9.6f, 8.8f, 7f, 5f, 4.4f, 5f, 6.6f, 5.4f, 4.6f, 5.6f, 6f)
        ABAJO.indices.map { i -> ABAJO[i] + Offset(az.entre(-1f, 1f), anchos[i % anchos.size]) }
    }

    /** La orilla de acá en la x dada (para saber dónde empieza la hierba del llano). */
    fun orillaDeAca(x: Float): Float {
        val i = ABAJO.indexOfFirst { it.x <= x }
        if (i <= 0) return ABAJO.first().y
        val a = ABAJO[i - 1]; val b = ABAJO[i]
        return a.y + (b.y - a.y) * ((a.x - x) / (a.x - b.x).coerceAtLeast(0.01f))
    }

    // ---------------------------------------------------------------- Las montañas, el cerro y las lomas

    /**
     * Los milanos que planean sobre el campamento los días claros, uno o dos, cada día en otro sitio del cielo y nunca
     * delante del sol ni de la luna **[Diseño propio]**: el cuento nombra alondras, garzas y patos; el águila es la del
     * gato de pixel. Dan vida al cielo los días en que no trae nubes.
     */
    private fun DrawScope.milanos(m: Momento, p: Pincel) {
        val l = p.l
        if (l.noche > 0.4f || m.ficha.clima == io.github.ruddy2003.vestigium.vidas.Clima.LLUVIA || m.ficha.clima == io.github.ruddy2003.vestigium.vidas.Clima.TORMENTA) return
        val az = m.azar("milanos"); val cuantos = 1 + az.entero(2)
        val ave = lerp(C.CUERO, C.HOLLIN, 0.35f)
        repeat(cuantos) {
            val c = if (az.real() < 0.7f) Offset(az.entre(150f, 262f), az.entre(16f, 62f)) else Offset(az.entre(336f, 370f), az.entre(14f, 40f))
            val w = az.entre(5f, 8.5f) * (if (it == 1) 0.75f else 1f); val ladea = az.entre(-0.25f, 0.25f); val d = if (az.real() < 0.5f) 1f else -1f
            fun q(x: Float, y: Float) = c + Offset(x * w * d, (y + x * ladea) * w)
            val alas = Path().apply { moveTo(q(-1f, -0.05f)); cubicTo(q(-0.7f, -0.22f), q(-0.35f, -0.2f), q(-0.12f, 0f)); lineTo(q(0.12f, 0f)); cubicTo(q(0.35f, -0.24f), q(0.7f, -0.26f), q(1f, -0.12f)); cubicTo(q(0.7f, -0.1f), q(0.35f, 0.02f), q(0.1f, 0.12f)); lineTo(q(-0.1f, 0.12f)); cubicTo(q(-0.35f, 0.02f), q(-0.7f, 0.04f), q(-1f, -0.05f)); close() }
            drawPath(alas, p.medio(ave, 0.25f))
            drawPath(poligono(q(-0.08f, 0.1f), q(0.08f, 0.1f), q(0.12f, 0.42f), q(0f, 0.34f), q(-0.12f, 0.42f)), p.medio(ave, 0.25f))
            drawPath(Path().apply { moveTo(q(-0.8f, -0.06f)); cubicTo(q(-0.5f, -0.12f), q(-0.25f, -0.06f), q(-0.12f, 0.04f)) }, p.luz(lerp(ave, C.ESTEPA, 0.5f), 0.25f).copy(alpha = 0.6f), style = Stroke(0.35f))
        }
    }

    fun DrawScope.lejos(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l; val fijo = Azar(Semilla.de("mongol", "verano", "montes")); val verde = VeranoMongol.hierba(m)
        milanos(m, p)
        // La cadena del fondo, alta y quebrada, con la nieve vieja en las cumbres; luego la de en medio, más oscura.
        val fondo = SIERRA
        monte(l, fondo, H + 2f, lerp(C.MONTE, C.NIEVE, 0.06f), 0.64f)
        nieveVieja(l, fondo, H + 2f, 85f, 0.64f, Azar(Semilla.de("mongol", "verano", "nieve")))
        mancha(Offset(250f, H - 4f), 190f, 4f, lerp(l.bruma, Color.White, 0.2f), 0.45f * (1f - 0.6f * l.noche))
        val medio = perfil(fijo, -40f, 300f, H, 16f, 0.65f, 34, isla = true)
        monte(l, medio, H + 3f, lerp(C.MONTE, C.SOMBRA, 0.36f), 0.52f)
        nieveVieja(l, medio, H + 3f, 92f, 0.52f, Azar(Semilla.de("mongol", "verano", "nieve2")))
        mancha(Offset(130f, H - 1f), 220f, 3f, lerp(l.bruma, Color.White, 0.25f), 0.4f * (1f - 0.6f * l.noche))
        monte(l, perfil(fijo, -24f, 404f, H + 2f, 6f, 0.3f, 34), H + 6f, lerp(C.MONTE, C.VERDE, 0.45f), 0.44f)
        // El cerro del valle de invierno, con sus cárcavas, su banda de roca y el ovoo en la cima.
        cerro(m, p, verde)
        // La estepa del otro lado del río.
        lomas(m, p, sol, verde)
        // Los campamentos de los recodos.
        for ((j, c) in CAMPAMENTOS.withIndex()) {
            val n = if (j == 1) 1 else 2
            for (i in 0 until n) {
                val q = c + Offset(i * (if (j == 2) 7f else 5.6f), i * 0.5f); val k = if (j == 2) 2.1f else 1.5f
                sombraCorta(sol.r, q, 3.2f * k, 2.6f * k)
                ger(Ger(Sitio(VeranoMongol.MIRADA, q, k, 0.4f - i * 0.3f), 960L + j * 7 + i, 0.5f, EscenaMongol.Puerta.ABIERTA, 0.6f, lejos = if (j == 2) 0.42f else 0.6f, detalle = false), p, sol)
            }
            // Un carro y unas manchas de ganado junto a cada campamento.
            val az = Azar(Semilla.de("mongol", "verano", "recodo", j))
            repeat(6) { val q = c + Offset(az.entre(-14f, 18f), az.entre(-0.4f, 1.6f)); drawOval(p.por(if (it % 2 == 0) C.LANA else C.CUERO, 0.2f, 0.6f), Offset(q.x - 0.8f, q.y - 0.7f), Size(1.6f, 0.9f)) }
        }
        // La lumbre de Mönkh: su resplandor en la loma (la llama va en la parte viva).
        if (EscenaMongol.fuegoDeMonkh(m)) resplandor(LUMBRE_DE_MONKH + Offset(0f, -1f), 9f, 0.5f * l.noche, C.LUMBRE)
        // La meta de la carrera: un palo con la cinta azul, en el llano de allá, y el polvo de los que corren.
        if (VeranoMongol.carrera(m)) {
            val q = Offset(304f, 112.2f)
            drawLine(p.medio(C.MADERA_OSC, 0.5f), q, q + Offset(0f, -7f), 0.5f)
            drawPath(poligono(q + Offset(0f, -7f), q + Offset(3.6f + 2f * m.viento, -6.2f), q + Offset(0f, -5f)), p.por(C.AZUL, 0.3f, 0.45f))
            for (k in 0 until 5) mancha(Offset(270f + k * 7f, 109.6f - k * 0.3f), 6f + k, 1.6f, p.luz(lerp(C.ESTEPA, C.FIELTRO, 0.4f), 0.5f), 0.35f * (1f - 0.6f * l.noche))
            for (k in 0 until 4) caballoLejano(p, sol, Offset(272f + k * 6.4f, 110f + (k % 2) * 0.6f), 2.2f, -1f, if (k == 1) PiezasMongol.BLANCO else PiezasMongol.BAYO, 0.5f)
        }
    }

    /**
     * La nieve vieja que queda en las cumbres que pasan de [nivel]: en cada cumbre, un casquete que baja por las dos
     * laderas con el borde quebrado, claro del lado del sol y violeta del otro, y unos regueros por las canales.
     */
    private fun DrawScope.nieveVieja(l: io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces, cresta: List<Offset>, fondo: Float, nivel: Float, lejos: Float, az: Azar) {
        val r = io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo(l); val luzALaIzq = r.lat < 0f
        val claro = l.tono(lerp(C.NIEVE, Color.White, 0.2f), lejos); val oscuro = l.tono(C.NIEVE_SOMBRA, lejos)
        clipPath(curva(cresta, fondo)) {
            for (i in 1 until cresta.size - 1) {
                val b = cresta[i]
                if (b.y > nivel || b.y > cresta[i - 1].y || b.y > cresta[i + 1].y) continue
                val hondo = (nivel - b.y) * 0.75f + 1.2f
                val j0 = maxOf(0, i - 3); val j1 = minOf(cresta.size - 1, i + 3)
                val arriba = (j0..j1).map { cresta[it] + Offset(0f, -0.6f) }
                val abajo = (j0..j1).map { j -> val q = cresta[j]; q + Offset(az.entre(-0.8f, 0.8f), ((hondo - (q.y - b.y)).coerceAtLeast(0f)) * az.entre(0.55f, 1.15f)) }
                val casquete = poligono(arriba + abajo.reversed())
                clipPath(casquete) {
                    drawRect(if (luzALaIzq) claro else oscuro, Offset(b.x - 60f, b.y - 2f), Size(60f, hondo + 4f))
                    drawRect(if (luzALaIzq) oscuro else claro, Offset(b.x, b.y - 2f), Size(60f, hondo + 4f))
                }
                // Los regueros de nieve que bajan por las canales, por debajo del casquete.
                repeat(3) { val x = b.x + az.entre(-hondo, hondo) * 1.2f; val y0 = b.y + hondo * az.entre(0.4f, 0.8f); drawLine(if ((x < b.x) == luzALaIzq) claro.copy(alpha = 0.7f) else oscuro.copy(alpha = 0.7f), Offset(x, y0), Offset(x + az.entre(-0.8f, 0.8f), y0 + az.entre(1.5f, 4.5f)), az.entre(0.4f, 0.8f), StrokeCap.Round) }
            }
        }
    }

    private fun DrawScope.cerro(m: Momento, p: Pincel, verde: Color) {
        val l = p.l; val az = Azar(Semilla.de("mongol", "verano", "cerro"))
        val propio = lerp(lerp(verde, C.ESTEPA, 0.55f), C.MONTE, 0.18f)
        monte(l, CERRO, H + 8f, propio, 0.38f)
        val forma = curva(CERRO, H + 8f)
        val luzIzq = !p.solALaDerecha
        clipPath(forma) {
            // La banda de roca rota bajo la cima y los canchales que bajan de ella.
            val roca = lerp(C.PIEDRA, C.MONTE, 0.3f)
            for (i in 0 until 9) { val x = 36f + i * 3.4f + az.entre(-1f, 1f); val y = 89f + kotlin.math.abs(x - CIMA.x) * 0.32f + az.entre(-0.6f, 0.6f); drawRect(l.tono(if ((x < CIMA.x) == luzIzq) lerp(roca, C.FIELTRO, 0.3f) else lerp(roca, C.SOMBRA, 0.3f), 0.36f), Offset(x, y), Size(az.entre(1.6f, 3f), az.entre(0.8f, 1.4f))) }
            // Las cárcavas: surcos que bajan de la cima y se bifurcan, oscuros, con un filo claro del lado del sol.
            for (k in 0 until 7) {
                var q = Offset(CIMA.x + (k - 3f) * 8.5f + az.entre(-2f, 2f), 0f); q = Offset(q.x, cresta(q.x) + az.entre(2.5f, 5f))
                val pasos = 3 + az.entero(3); val camino = ArrayList<Offset>(); camino += q
                repeat(pasos) { q += Offset((q.x - CIMA.x) * 0.06f + az.entre(-1.5f, 1.5f), az.entre(2.4f, 4f)); camino += q }
                val trazo = Path().apply { moveTo(camino[0]); for (c in camino.drop(1)) lineTo(c) }
                drawPath(trazo, l.tono(lerp(propio, C.SOMBRA, 0.55f), 0.36f).copy(alpha = 0.55f), style = Stroke(0.7f, cap = StrokeCap.Round))
                drawPath(trazo.apply { translate(Offset(if (luzIzq) -0.5f else 0.5f, 0f)) }, l.tono(lerp(propio, C.FIELTRO, 0.4f), 0.36f).copy(alpha = 0.35f), style = Stroke(0.4f, cap = StrokeCap.Round))
                if (az.real() < 0.6f) { val c = camino[camino.size / 2]; drawLine(l.tono(lerp(propio, C.SOMBRA, 0.5f), 0.36f).copy(alpha = 0.45f), c, c + Offset(if (c.x < CIMA.x) -2.6f else 2.6f, 2.6f), 0.5f, StrokeCap.Round) }
            }
            // Matas de hierba más oscura en las hondonadas.
            repeat(30) { val x = az.entre(0f, 140f); val y = cresta(x) + az.entre(2f, 18f); drawOval(l.tono(lerp(propio, C.VERDE, 0.5f), 0.38f).copy(alpha = 0.5f), Offset(x, y), Size(az.entre(1.2f, 3f), 0.7f)) }
        }
        ovoo(p, CIMA + Offset(0f, 0.3f), 8.6f, EscenaMongol.cintasDelOvoo(m), Azar(Semilla.de("mongol", "ovoo")), 0.32f, 0f, m.viento, EscenaMongol.diaDelOvoo(m))
    }

    /** La altura de la cresta del cerro en [x]. */
    private fun cresta(x: Float): Float {
        val i = CERRO.indexOfFirst { it.x >= x }
        if (i <= 0) return CERRO.first().y
        val a = CERRO[i - 1]; val b = CERRO[i]
        return a.y + (b.y - a.y) * ((x - a.x) / (b.x - a.x).coerceAtLeast(0.01f))
    }

    /**
     * La estepa del otro lado del río: cuatro lomas que se enciman, cada una más cerca, más verde y más nítida. En cada
     * una, la cresta toma la luz y amarillea, y la hondonada de su pie queda más verde y en sombra; encima, las rachas que
     * peinan la hierba, las sendas del ganado que bajan al río y las manadas.
     */
    private fun DrawScope.lomas(m: Momento, p: Pincel, sol: Sol3, verde: Color) {
        val l = p.l
        val crestas = listOf(
            Triple(H + 1.8f, 3.2f, 0.56f), Triple(H + 5.4f, 4.6f, 0.47f), Triple(H + 9.6f, 5.6f, 0.38f), Triple(H + 14.2f, 6.4f, 0.3f),
        )
        for ((j, c) in crestas.withIndex()) {
            val (base, alto, lejos) = c
            val az = Azar(Semilla.de("mongol", "verano", "loma", j))
            val x0 = if (j % 2 == 0) -20f else 60f; val x1 = if (j == 3) 250f else if (j == 1) 404f else 330f
            val cresta = perfil(az, x0, x1, base, alto, 0.45f, 14 + 3 * j, isla = j != 0)
            val fondo = if (j == 3) 136f else base + 22f
            val forma = curva(cresta, fondo)
            val arriba = cresta.minOf { it.y }
            val claro = lerp(verde, C.ESTEPA, 0.55f); val hondo = lerp(verde, C.MONTE, 0.25f)
            drawPath(forma, Brush.verticalGradient(0f to p.luz(claro, lejos), 0.25f to p.medio(lerp(claro, verde, 0.5f), lejos), 0.7f to p.medio(verde, lejos), 1f to p.sombra(hondo, lejos), startY = arriba, endY = arriba + alto + 12f + 2f * j))
            clipPath(forma) {
                // El lado de sombra de cada lomo: del lado contrario al sol, una cuña más oscura.
                for (i in 1 until cresta.size - 1) {
                    val q = cresta[i]
                    if (q.y <= cresta[i - 1].y && q.y < cresta[i + 1].y) {
                        val cae = if (p.solALaDerecha) -1f else 1f
                        drawPath(poligono(q, q + Offset(cae * (14f + 4f * j), 7f + 2f * j), q + Offset(cae * 2f, 9f + 2f * j)), p.sombra(hondo, lejos).copy(alpha = 0.32f * (0.4f + 0.6f * sol.r.dia)))
                        drawPath(poligono(q, q + Offset(-cae * (12f + 3f * j), 6f + 2f * j), q + Offset(-cae * 1f, 8f + 2f * j)), p.luz(claro, lejos).copy(alpha = 0.25f * sol.r.dia))
                    }
                }
                // Las rachas del viento: pinceladas largas y claras, tendidas, y las calvas de tierra en los lomos.
                repeat(5 + 2 * j) { val q = Offset(az.entre(x0, x1), az.entre(arriba + 1f, arriba + alto + 6f)); mancha(q, az.entre(10f, 30f), 0.8f + 0.3f * j, if (it % 3 == 0) p.sombra(hondo, lejos) else p.luz(lerp(claro, C.FIELTRO, 0.2f), lejos), 0.28f) }
                // Las sendas del ganado: bajan en diagonal de lomo en lomo hacia el río.
                repeat(2 + j) {
                    var q = Offset(az.entre(x0 + 20f, x1 - 20f), arriba + az.entre(0.5f, 2f)); val dx = az.entre(-1f, 1f) * (6f + 3f * j)
                    val camino = Path().apply { moveTo(q); val a = q + Offset(dx * 0.4f, alto * 0.6f + 2f); val b = q + Offset(dx * 0.7f, alto + 3f); q += Offset(dx, alto + 7f + j); cubicTo(a, b, q) }
                    drawPath(camino, p.luz(lerp(C.ESTEPA, C.FIELTRO, 0.35f), lejos).copy(alpha = 0.55f), style = Stroke(0.35f + 0.12f * j, cap = StrokeCap.Round))
                }
                // Matas sueltas, oscuras.
                repeat(14 + 8 * j) { val q = Offset(az.entre(x0, x1), az.entre(arriba + 1f, arriba + alto + 10f)); drawOval(p.sombra(lerp(verde, C.MONTE, 0.3f), lejos).copy(alpha = 0.45f), Offset(q.x, q.y), Size(az.entre(0.8f, 2.4f), 0.5f + 0.1f * j)) }
            }
            // El filo de luz de la cresta.
            drawPath(curva(cresta), p.luz(lerp(claro, C.FIELTRO, 0.3f), lejos).copy(alpha = 0.5f * (0.3f + 0.7f * sol.r.dia)), style = Stroke(0.5f))
            // Las manadas de los otros campamentos, en los lomos de esta loma: caballos y ovejas, con su sombra corta.
            val manadas = Azar(Semilla.de("mongol", "verano", "manadas", j, m.ficha.dia.substring(5, 7)))
            repeat(if (j == 0) 1 else 2) {
                val c0 = Offset(manadas.entre(x0 + 30f, x1 - 30f), 0f); val y = cresta.minByOrNull { kotlin.math.abs(it.x - c0.x) }!!.y + 2f + j * 0.8f
                val caballos = manadas.real() < 0.45f
                repeat(4 + manadas.entero(6)) {
                    val q = Offset(c0.x + manadas.entre(-8f, 8f) * (1f + 0.3f * j), y + manadas.entre(-0.6f, 1.2f))
                    if (caballos) caballoLejano(p, sol, q, 1.4f + 0.45f * j, if (manadas.real() < 0.5f) 1f else -1f, if (it % 3 == 0) PiezasMongol.TORDO else PiezasMongol.BAYO, lejos, pasta = it % 2 == 0)
                    else { sombraCorta(sol.r, q, 1.4f + 0.4f * j, 0.8f); drawOval(p.por(if (it % 4 == 0) C.CABEZA else C.LANA, 0.3f, lejos), Offset(q.x - 0.7f - 0.2f * j, q.y - 0.8f - 0.2f * j), Size(1.4f + 0.4f * j, 0.9f + 0.25f * j)) }
                }
            }
        }
        // La manada de Mönkh, la noche que la cuida: siluetas junto a su lumbre.
        if (EscenaMongol.fuegoDeMonkh(m)) for (k in 0 until 5) caballoLejano(p, sol, LUMBRE_DE_MONKH + Offset(-10f + k * 4.2f, 0.4f + (k % 2) * 0.5f), 2.2f, if (k % 2 == 0) 1f else -1f, PiezasMongol.TORDO, 0.3f, pasta = k == 2)
    }

    // ---------------------------------------------------------------- El río

    /** La forma del agua: entre las dos orillas. Con el río crecido, más ancho. */
    private fun forma(crecido: Boolean): Path {
        val a = if (crecido) ARRIBA.map { it + Offset(0f, -0.6f) } else ARRIBA
        val b = if (crecido) ABAJO.map { it + Offset(0f, 1.4f) } else ABAJO
        return poligono(a + b.reversed())
    }

    fun DrawScope.rio(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l; val az = Azar(Semilla.de("mongol", "verano", "rio")); val verde = VeranoMongol.hierba(m)
        val turbio = VeranoMongol.rioTurbio(m)
        // La orilla de allá: un talud de tierra en sombra, cortado por el agua, con la grava al pie.
        val talud = poligono(ARRIBA.map { it + Offset(0f, -1.6f - (it.y - H) * 0.03f) } + ARRIBA.reversed().map { it + Offset(0f, 0.3f) })
        drawPath(talud, p.sombra(lerp(C.TIERRA, verde, 0.3f), 0.35f))
        // La orilla de acá: la playa de grava y arena, ancha en los bancos (la franja de paso entre la hierba y el agua).
        val playa = poligono(ABAJO.map { it + Offset(0f, -0.4f) } + PLAYA.reversed().map { it + Offset(0f, 0.8f) })
        val arena = lerp(C.ESTEPA, C.FIELTRO, 0.35f)
        drawPath(playa, Brush.verticalGradient(listOf(p.luz(arena, 0.3f), p.medio(lerp(arena, C.PIEDRA, 0.4f), 0.25f)), H + 8f, 155f))
        clipPath(playa) {
            repeat(110) { val q = Offset(az.entre(-10f, 392f), az.entre(H + 8f, 156f)); val w = 0.4f + (q.y - H) * 0.02f; drawOval(p.por(lerp(C.PIEDRA, if (it % 3 == 0) C.TIERRA else C.FIELTRO, 0.3f), if (it % 2 == 0) 0.6f else -0.6f, 0.3f), Offset(q.x - w, q.y - w * 0.4f), Size(w * 2f, w * 0.8f)) }
        }
        // El agua: el cielo reflejado, más oscuro en lo hondo; con lluvia, del color del té.
        val cauce = forma(turbio)
        val propio = if (turbio) lerp(C.RIO, C.TIERRA, 0.62f) else C.RIO
        val hondo = if (turbio) lerp(C.RIO_HONDO, C.TIERRA, 0.55f) else C.RIO_HONDO
        agua(l, Rect(-12f, H + 4f, 392f, 150f), Azar(Semilla.de("mongol", "verano", "agua")), propio, m.viento, cauce, hondo, lerp(propio, C.ESTEPA, 0.45f))
        clipPath(cauce) {
            // El cielo en el agua: el remanso, quieto, devuelve el claro del horizonte.
            if (!turbio) { mancha(Offset(296f, 117f), 46f, 3.2f, lerp(l.bajo, Color.White, 0.2f), 0.45f * (1f - 0.5f * l.noche)); mancha(Offset(90f, 130.6f), 90f, 3.4f, lerp(l.medio, l.bajo, 0.5f), 0.35f * (1f - 0.5f * l.noche)) }
            // La orilla de allá, reflejada: una banda oscura y rota bajo el talud; y los bancos de arena que asoman.
            for (i in 0 until ARRIBA.size - 1) { val a = ARRIBA[i]; val b = ARRIBA[i + 1]; reflejoRoto(b.x, a.x, minOf(a.y, b.y), 1.6f + (a.y - H) * 0.18f, p.sombra(lerp(verde, C.MONTE, 0.3f), 0.3f), 0.55f, az, 0.6f) }
            for ((c, w) in listOf(Offset(292f, 116.6f) to 12f, Offset(186f, 129.4f) to 9f, Offset(58f, 132.2f) to 15f)) {
                drawPath(Path().apply { moveTo(c + Offset(-w, 0.6f)); cubicTo(c + Offset(-w * 0.5f, -1.6f), c + Offset(w * 0.4f, -1.4f), c + Offset(w, 0.4f)); cubicTo(c + Offset(w * 0.4f, 1.6f), c + Offset(-w * 0.5f, 1.8f), c + Offset(-w, 0.6f)); close() }, p.luz(arena, 0.3f))
                drawPath(Path().apply { moveTo(c + Offset(-w * 0.8f, 1.2f)); cubicTo(c + Offset(-w * 0.3f, 1.8f), c + Offset(w * 0.4f, 1.6f), c + Offset(w * 0.9f, 0.8f)) }, p.sombra(C.TIERRA, 0.3f).copy(alpha = 0.5f), style = Stroke(0.5f))
                if (!turbio) reflejoRoto(c.x - w * 0.8f, c.x + w * 0.8f, c.y + 1.2f, 2f, p.luz(arena, 0.3f), 0.4f, az, 0.5f)
            }
            // La estela del sol bajo o de la luna.
            estela(l, Rect(-12f, H + 4f, 392f, 150f), m.azar("estela"))
            // Con lluvia, la corriente arrastra espuma y ramas.
            if (turbio) repeat(26) { val q = Offset(az.entre(-10f, 380f), az.entre(H + 6f, 148f)); drawLine(p.luz(lerp(C.FIELTRO, C.TIERRA, 0.3f), 0.3f).copy(alpha = 0.4f), q, q + Offset(az.entre(2f, 6f), 0f), 0.5f, StrokeCap.Round) }
        }
        // El agua clara junto a las orillas, y el filo de la orilla de acá.
        if (!turbio) bajio(l, curva(ABAJO), 3f, lerp(C.RIO, C.FIELTRO, 0.35f))
        drawPath(curva(ABAJO.map { it + Offset(0f, 0.2f) }), p.luz(lerp(arena, Color.White, 0.3f), 0.3f).copy(alpha = 0.6f * (1f - 0.5f * l.noche)), style = Stroke(0.6f, cap = StrokeCap.Round))
        // Las aves del río: una garza en la orilla de allá y unos patos en el remanso.
        if (l.noche < 0.5f) {
            val g = Offset(204f, 121.4f)
            drawLine(p.medio(C.PIEDRA, 0.3f), g, g + Offset(0f, -1.6f), 0.3f)
            drawOval(p.por(lerp(C.PIEDRA, C.FIELTRO, 0.4f), 0.3f, 0.3f), Offset(g.x - 1f, g.y - 2.8f), Size(2f, 1.3f))
            drawLine(p.medio(lerp(C.PIEDRA, C.FIELTRO, 0.3f), 0.3f), g + Offset(-0.6f, -2.4f), g + Offset(-1.2f, -4f), 0.35f, StrokeCap.Round)
            for (k in 0 until 3) { val q = Offset(274f + k * 3.2f, 119.4f + (k % 2) * 0.5f); drawOval(p.medio(C.CUERO, 0.35f), Offset(q.x - 0.7f, q.y - 0.5f), Size(1.4f, 0.6f)); drawLine(lerp(l.bajo, Color.White, 0.3f).copy(alpha = 0.4f), q + Offset(0.8f, 0.2f), q + Offset(3f, 0.2f), 0.3f) }
        }
    }
}
