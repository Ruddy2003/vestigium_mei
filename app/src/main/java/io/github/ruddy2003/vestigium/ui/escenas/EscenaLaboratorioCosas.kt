package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.ARRIBA
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.IZQ
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.Marco
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.cajaEn
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.cilindro
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.palo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * Lo que hay en el suelo del laboratorio fuera de la mesa y de la campana: el estante de las semillas bajo la pantalla (con
 * sus frascos, el saco y las macetas de barro apiladas), la caja de tierra de arriba abierta con su pala, el cubo y el
 * taburete de Lauro; y lo que dejan los estados: el catre, los cubos de lejía, la sábana y la mascarilla de la cuarentena,
 * la cinta amarilla y la silla del vigía del cierre, el taburete pegado al vidrio del experimento, y las banderas de papel
 * de la Fundación.
 */
internal object CosasLab {
    private val C = ColorLaboratorio

    /**
     * Si la bata de repuesto de Lauro está echada en su taburete: en el cierre, salvo el día que el objeto es la bata o
     * los guantes (cuelgan de ese mismo asiento); ese día sigue en la percha.
     */
    fun bataDeLauroEnElTaburete(m: Momento): Boolean = EscenaLaboratorio.cierre(m) && !(m.ficha.objeto == "bata" || m.ficha.objeto == "guantes")

    /** El taburete de Lauro: junto a la mesa; en el experimento, pegado al vidrio de la campana. */
    fun taburete(m: Momento): V3 = if (EscenaLaboratorio.enLaCampana(m)) V3(1.05f, 0f, 3.75f) else V3(1.15f, 0f, 3.02f)

    /** La caja de tierra de arriba, abierta, y el cubo, en el suelo, delante del estante de las semillas. */
    val CAJA = V3(2.4f, 0f, 2.45f)
    val CUBO = V3(2.62f, 0f, 2.2f)

    fun cosas(v: VistaLab): List<CosaLab> = buildList {
        val m = v.m
        add(CosaLab(CamLab.hondo(2.8f, 3.6f), caja3(2.62f, 2.98f, 0f, SalaLab.EY, SalaLab.EZ0, SalaLab.EZ1)) { estanteDeSemillas(v) })
        // La caja de tierra abierta; mientras están los diez sacos que bajó Osvaldo, la pila de sacos en su sitio.
        if (EscenaLaboratorio.sacosDeOsvaldo(m)) add(CosaLab(CamLab.hondo(CAJA.x, CAJA.z), caja3(CAJA.x - 0.28f, CAJA.x + 0.28f, 0f, 0.55f, CAJA.z - 0.26f, CAJA.z + 0.26f)) { sacos(v) })
        else add(CosaLab(CamLab.hondo(CAJA.x, CAJA.z), caja3(CAJA.x - 0.26f, CAJA.x + 0.26f, 0f, 0.3f, CAJA.z - 0.18f, CAJA.z + 0.18f)) { cajaDeTierra(v) })
        add(CosaLab(CamLab.hondo(CUBO.x, CUBO.z), PiezasLab.aro(CUBO, 0.15f, 8) + PiezasLab.aro(CUBO + V3(0f, 0.32f, 0f), 0.17f, 8)) { cubo(v, CUBO, lerp(C.ACERO, C.HORMIGON, 0.3f)) })
        val t = taburete(m)
        add(CosaLab(CamLab.hondo(t.x, t.z), PiezasLab.aro(t, 0.17f, 8) + PiezasLab.aro(t + V3(0f, 0.62f, 0f), 0.17f, 8)) { taburete(v, t) })
        if (EscenaLaboratorio.cuarentena(m)) {
            // El catre, delante de la campana (ella duerme en el laboratorio), y los cubos de lejía junto a la mesa.
            val catre = Marco(V3(0.95f, 0f, 3.3f), V3(1f, 0f, -0.04f))
            add(CosaLab(CamLab.hondo(1.9f, 3.6f), listOf(catre.w(0f, 0f, 0f), catre.w(1.85f, 0f, 0f), catre.w(1.85f, 0.66f, 0f), catre.w(0f, 0.66f, 0f), catre.w(0f, 0f, 0.45f), catre.w(1.85f, 0.66f, 0.45f))) { catre(v, catre) })
            for ((i, q) in listOf(V3(0.6f, 0f, 3.75f), V3(0.42f, 0f, 4.15f)).withIndex()) add(CosaLab(CamLab.hondo(q.x, q.z), PiezasLab.aro(q, 0.14f, 8) + PiezasLab.aro(q + V3(0f, 0.3f, 0f), 0.16f, 8)) { cubo(v, q, if (i == 0) C.TELA else lerp(C.TELA, C.AMARILLO, 0.2f)) })
        }
        if (EscenaLaboratorio.cierre(m)) {
            // La silla del vigía, a la izquierda de la campana, mirando a la puerta.
            val s = V3(0.3f, 0f, 4.35f)
            add(CosaLab(CamLab.hondo(s.x, s.z), caja3(s.x - 0.22f, s.x + 0.22f, 0f, 0.9f, s.z - 0.22f, s.z + 0.22f)) { silla(v, s) })
        }
    }

    /** Los ocho puntos de una caja, para su sombra. */
    fun caja3(x0: Float, x1: Float, y0: Float, y1: Float, z0: Float, z1: Float): List<V3> = listOf(V3(x0, y0, z0), V3(x1, y0, z0), V3(x1, y0, z1), V3(x0, y0, z1), V3(x0, y1, z0), V3(x1, y1, z0), V3(x1, y1, z1), V3(x0, y1, z1))

    // ---------------------------------------------------------------- El estante de las semillas

    /**
     * El estante de las semillas, bajo la pantalla: un mueble de acero de tres tablas, con frascos de semillas en fila, el
     * saco de grama atado y las macetas de barro apiladas encima («una que ya ha sido maceta cuatro veces»). En el cierre,
     * precintado con cinta amarilla, y con el hueco del saco que se llevó Lauro.
     */
    private fun DrawScope.estanteDeSemillas(v: VistaLab) {
        val x0 = SalaLab.EX; val x1 = SalaLab.XD - 0.02f; val z0 = SalaLab.EZ0; val z1 = SalaLab.EZ1
        val acero = lerp(C.ACERO, C.HORMIGON, 0.25f)
        val tablas = listOf(0.1f, 0.5f, SalaLab.EY)
        // Los montantes y las tablas.
        for (z in listOf(z0, z1)) for (x in listOf(x0)) palo(v, V3(x, 0f, z), V3(x, SalaLab.EY, z), 0.03f, acero)
        for (y in tablas) PiezasLab.run { caja(v, x0, x1, y - 0.025f, y, z0, z1, acero) }
        // Los frascos de semillas, en fila, cada uno con su cinta.
        val az = Azar(Semilla.de("laboratorio", "frascos"))
        for ((ti, y) in listOf(0.1f, 0.5f).withIndex()) {
            var z = z1 - 0.12f
            while (z > z0 + 0.1f) {
                val saco = ti == 1 && z < z0 + 0.62f && z > z0 + 0.12f
                if (!saco) {
                    val r = az.entre(0.038f, 0.064f); val alto = az.entre(0.12f, 0.27f); val c = V3(x0 + 0.15f, y, z)
                    val tapa = listOf(lerp(C.NEGRO, C.ACERO, 0.3f), C.LATON, lerp(C.MADERA, C.TELA, 0.3f), lerp(C.CIAN, C.NEGRO, 0.3f))[az.entero(4)]
                    cilindro(v, c, r, alto, lerp(C.VIDRIO, C.TIERRA, 0.12f), tapa)
                    // Lo de dentro: semillas hasta su nivel, cada frasco con las suyas (pardas, negras, pajizas, verdosas).
                    val semilla = listOf(lerp(C.MADERA, C.TIERRA, 0.3f), lerp(C.NEGRO, C.MADERA, 0.4f), C.PAJA, lerp(C.HOJA_OSC, C.PAJA, 0.4f))[az.entero(4)]
                    val dentro = PiezasLab.mitad(c + V3(0f, alto * az.entre(0.15f, 0.9f), 0f), r * 0.9f, true).map { CamLab.p(it) } + PiezasLab.mitad(c + V3(0f, 0.01f, 0f), r * 0.9f, true).reversed().map { CamLab.p(it) }
                    drawPath(poligono(dentro), v.c(c, semilla, IZQ).copy(alpha = 0.88f))
                    // La cinta o la etiqueta de papel.
                    val cinta = listOf(C.AMARILLO, C.CIAN, C.TELA, C.AVISO, C.GRAMA)[az.entero(5)]
                    if (az.real() > 0.4f) drawLine(v.c(c, cinta, IZQ), CamLab.p(c.x - r, y + alto * 0.5f, z), CamLab.p(c.x - r, y + alto * 0.68f, z), (0.04f * CamLab.k(c)).coerceAtLeast(0.8f))
                    else drawLine(v.c(c, cinta, IZQ), CamLab.p(c.x - r, y + alto * 0.6f, z - r * 0.6f), CamLab.p(c.x - r, y + alto * 0.6f, z + r * 0.6f), (0.02f * CamLab.k(c)).coerceAtLeast(0.7f))
                }
                z -= az.entre(0.11f, 0.16f)
            }
        }
        // El saco de semillas de grama, atado, en la tabla del medio (en el cierre, solo el hueco con el polvo).
        val saco = V3(x0 + 0.17f, 0.5f, z0 + 0.36f)
        if (!EscenaLaboratorio.cierre(v.m)) {
            val p = CamLab.p(saco); val k = CamLab.k(saco)
            // Un saco de arpillera lleno, caído hacia un lado, con el cuello atado y la boca doblada.
            val forma = Path().apply { moveTo(p.x - 0.15f * k, p.y)
                cubicTo(p.x - 0.19f * k, p.y - 0.1f * k, p.x - 0.15f * k, p.y - 0.24f * k, p.x - 0.05f * k, p.y - 0.27f * k)
                lineTo(p.x - 0.03f * k, p.y - 0.31f * k); lineTo(p.x + 0.04f * k, p.y - 0.32f * k); lineTo(p.x + 0.03f * k, p.y - 0.27f * k)
                cubicTo(p.x + 0.13f * k, p.y - 0.25f * k, p.x + 0.18f * k, p.y - 0.12f * k, p.x + 0.15f * k, p.y); close() }
            val arpillera = lerp(C.MADERA, C.AMARILLO, 0.25f)
            drawPath(forma, androidx.compose.ui.graphics.Brush.horizontalGradient(0f to v.c(saco, arpillera, V3(-1f, 0.3f, -0.5f)), 0.5f to v.c(saco, arpillera, V3(0f, 0.3f, -1f)), 1f to v.c(saco, lerp(arpillera, C.TIERRA, 0.4f), V3(0.6f, 0f, -0.6f)), startX = p.x - 0.18f * k, endX = p.x + 0.18f * k))
            // La trama de la arpillera y los pliegues.
            for (i in 1..5) drawLine(v.c(saco, lerp(arpillera, C.NEGRO, 0.3f), IZQ).copy(alpha = 0.35f), Offset(p.x - 0.13f * k + i * 0.045f * k, p.y - 0.02f * k), Offset(p.x - 0.08f * k + i * 0.03f * k, p.y - 0.24f * k), 0.4f)
            drawLine(v.c(saco, C.MADERA, IZQ), Offset(p.x - 0.045f * k, p.y - 0.275f * k), Offset(p.x + 0.035f * k, p.y - 0.27f * k), (0.018f * k).coerceAtLeast(0.7f))
        } else {
            mancha(CamLab.p(saco), 0.12f * CamLab.k(saco), 0.03f * CamLab.k(saco), lerp(C.TIERRA, C.HORMIGON, 0.3f), 0.5f)
        }
        // Las macetas de barro, metidas unas en otras en dos pilas: de cada una se ve el borde grueso; la de arriba, con su hueco.
        for ((j, z) in listOf(z0 + 0.3f, z0 + 0.62f).withIndex()) {
            val n = 5 - j
            for (i in 0 until n) {
                val c = V3(x0 + 0.17f, SalaLab.EY + i * 0.035f, z); val barro = if ((i + j) % 3 == 2) lerp(C.BARRO, C.TIERRA, 0.3f) else lerp(C.BARRO, C.MADERA, 0.12f * (i % 2))
                PiezasLab.run { tronco(v, c, 0.068f, 0.095f, 0.12f, barro, tapa = if (i == n - 1) lerp(C.BARRO, C.NEGRO, 0.55f) else null) }
                // El borde, más claro, que coge la luz.
                val borde = PiezasLab.mitad(c + V3(0f, 0.12f, 0f), 0.097f, true).map { CamLab.p(it) }
                for (h in 0 until borde.size - 1) drawLine(v.c(c + V3(0f, 0.12f, 0f), lerp(barro, C.TELA, 0.25f), IZQ), borde[h], borde[h + 1], 1.1f)
            }
        }
        // La de la grama de Hilda, en el suelo junto al estante: ya ha sido maceta cuatro veces.
        val mg = V3(x0 - 0.12f, 0f, z0 + 0.1f)
        PiezasLab.run { tronco(v, mg, 0.1f, 0.14f, 0.18f, lerp(C.BARRO, C.TIERRA, 0.2f), tapa = C.TIERRA) }
        // El cierre: la cinta amarilla, en cruz, por delante del estante.
        if (EscenaLaboratorio.cierre(v.m)) for ((a, b) in listOf(V3(x0 - 0.01f, 0.05f, z0) to V3(x0 - 0.01f, SalaLab.EY, z1), V3(x0 - 0.01f, SalaLab.EY, z0) to V3(x0 - 0.01f, 0.05f, z1)))
            drawLine(v.c((a + b) * 0.5f, C.AMARILLO, IZQ), CamLab.p(a), CamLab.p(b), (0.035f * CamLab.k((a + b) * 0.5f)).coerceAtLeast(1f))
    }

    /** La caja de madera con tierra gris de arriba, abierta, con su pala de mano clavada. */
    private fun DrawScope.cajaDeTierra(v: VistaLab) {
        val mk = Marco(CAJA + V3(-0.25f, 0f, -0.15f), V3(0.98f, 0f, 0.2f).let { val l = kotlin.math.sqrt(it.x * it.x + it.z * it.z); V3(it.x / l, 0f, it.z / l) })
        cajaEn(v, mk, 0f, 0.52f, -0.34f, 0f, 0f, 0.3f, C.MADERA) { cara, f ->
            if (cara == 3) drawPath(f, v.c(mk.w(0.26f, -0.17f, 0.3f), C.TIERRA, ARRIBA))
            if (cara == 0 || cara == 2) for (t in 1..2) drawLine(v.c(mk.w(0f, 0f, 0.15f), lerp(C.MADERA, C.NEGRO, 0.4f), mk.n(-1f, 0f, 0f)).copy(alpha = 0.6f), mk.p(if (cara == 0) 0f else 0.02f, if (cara == 0) -0.02f else 0f, 0.3f * t / 3f), mk.p(if (cara == 0) 0f else 0.5f, if (cara == 0) -0.32f else 0f, 0.3f * t / 3f), 0.5f)
        }
        // La tierra amontonada y la pala de mano, clavada, con su mango de madera.
        val c = mk.w(0.3f, -0.18f, 0.31f); val p = CamLab.p(c); val k = CamLab.k(c)
        mancha(p, 0.14f * k, 0.035f * k, v.c(c, lerp(C.TIERRA, Color.White, 0.15f), ARRIBA), 0.7f)
        palo(v, c + V3(0f, 0.02f, 0f), c + V3(0.06f, 0.2f, -0.04f), 0.025f, C.MADERA)
        drawPath(poligono(p + Offset(-0.03f * k, 0f), p + Offset(0.03f * k, 0f), p + Offset(0.02f * k, 0.04f * k), p + Offset(-0.02f * k, 0.04f * k)), v.c(c, C.ACERO, IZQ))
    }

    /** Un cubo de chapa con su asa. */
    private fun DrawScope.cubo(v: VistaLab, c: V3, propio: Color) {
        cilindro(v, c, 0.15f, 0.32f, propio, tapa = lerp(propio, C.NEGRO, 0.3f))
        val a = CamLab.p(c + V3(-0.15f, 0.3f, 0f)); val b = CamLab.p(c + V3(0.15f, 0.3f, 0f)); val k = CamLab.k(c)
        drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x, a.y - 0.18f * k, b.x, b.y - 0.18f * k, b.x, b.y) }, v.c(c, C.ACERO, PiezasLab.FRENTE), style = Stroke(0.6f))
    }

    /** El taburete de Lauro: asiento redondo de madera sobre tres patas de acero, con un aro para los pies. */
    private fun DrawScope.taburete(v: VistaLab, t: V3) {
        val alto = 0.62f
        for (i in 0..2) { val a = i * 2.094f + 0.5f; palo(v, t + V3(0.17f * kotlin.math.cos(a), 0f, 0.17f * kotlin.math.sin(a)), t + V3(0.1f * kotlin.math.cos(a), alto, 0.1f * kotlin.math.sin(a)), 0.022f, C.ACERO) }
        val aro = PiezasLab.mitad(t + V3(0f, 0.22f, 0f), 0.14f, true).map { CamLab.p(it) }
        for (i in 0 until aro.size - 1) drawLine(v.c(t + V3(0f, 0.22f, 0f), C.ACERO, PiezasLab.FRENTE), aro[i], aro[i + 1], 0.8f)
        cilindro(v, t + V3(0f, alto, 0f), 0.17f, 0.04f, C.MADERA)
        // En el cierre, su bata de repuesto echada encima, con la franja gris en la manga que cuelga.
        if (bataDeLauroEnElTaburete(v.m)) {
            val c = t + V3(0f, alto + 0.04f, 0f); val k = CamLab.k(c); val p = CamLab.p(c)
            fun q(x: Float, y: Float) = p + Offset(x * k, -y * k)
            val tela = v.c(c, C.TELA, V3(-0.3f, 0.6f, -1f)); val sombra = v.c(c, lerp(C.TELA, C.SOMBRA, 0.3f), V3(0.8f, 0f, -0.4f))
            val echada = Path().apply {
                val a = q(-0.22f, 0.02f); moveTo(a.x, a.y)
                val b = q(0.2f, 0.03f); val b1 = q(0f, 0.08f); cubicTo(b1.x, b1.y, b1.x, b1.y, b.x, b.y)
                val c2 = q(0.24f, -0.12f); lineTo(c2.x, c2.y)
                val d = q(0.16f, -0.3f); lineTo(d.x, d.y)
                val e = q(-0.02f, -0.06f); val e1 = q(0.06f, -0.04f); cubicTo(e1.x, e1.y, e1.x, e1.y, e.x, e.y)
                val f = q(-0.2f, -0.38f); lineTo(f.x, f.y)
                val g = q(-0.28f, -0.34f); lineTo(g.x, g.y)
                close()
            }
            drawPath(echada, androidx.compose.ui.graphics.Brush.horizontalGradient(0f to tela, 0.6f to tela, 1f to sombra, startX = q(-0.28f, 0f).x, endX = q(0.24f, 0f).x))
            drawLine(v.c(c, C.CUELLO, PiezasLab.FRENTE), q(-0.12f, 0.05f), q(0.04f, 0.06f), (0.03f * k).coerceAtLeast(0.7f))
            drawLine(v.c(c, lerp(C.ACERO, C.NEGRO, 0.2f), PiezasLab.FRENTE), q(-0.2f, -0.24f), q(-0.26f, -0.22f), (0.03f * k).coerceAtLeast(0.7f))
            drawLine(sombra.copy(alpha = 0.6f), q(-0.05f, 0f), q(-0.18f, -0.33f), 0.5f)
        }
    }

    /** Los diez sacos de tierra de arriba que bajó Osvaldo: arpillera atada, en pila (cuatro, tres, dos y uno), de lejos a cerca. */
    private fun DrawScope.sacos(v: VistaLab) {
        val arpillera = lerp(C.TIERRA, C.MADERA, 0.35f)
        val capas = listOf(
            listOf(-0.13f to 0.12f, 0.13f to 0.12f, -0.13f to -0.12f, 0.13f to -0.12f),
            listOf(0f to 0.12f, -0.13f to -0.06f, 0.13f to -0.06f),
            listOf(-0.07f to 0.02f, 0.07f to 0.02f),
            listOf(0f to 0f),
        )
        for ((capa, sitios) in capas.withIndex()) for ((dx, dz) in sitios) {
            val c = V3(CAJA.x + dx, 0.06f + capa * 0.13f, CAJA.z + dz); val k = CamLab.k(c); val p = CamLab.p(c)
            val saco = Path().apply {
                moveTo(p.x - 0.13f * k, p.y + 0.05f * k)
                cubicTo(p.x - 0.16f * k, p.y - 0.06f * k, p.x - 0.1f * k, p.y - 0.11f * k, p.x, p.y - 0.11f * k)
                cubicTo(p.x + 0.1f * k, p.y - 0.11f * k, p.x + 0.16f * k, p.y - 0.06f * k, p.x + 0.13f * k, p.y + 0.05f * k)
                close()
            }
            drawPath(saco, androidx.compose.ui.graphics.Brush.verticalGradient(0f to v.c(c, arpillera, V3(-0.4f, 1f, -0.5f)), 1f to v.c(c, lerp(arpillera, C.SOMBRA, 0.35f), V3(-0.6f, -0.2f, -0.5f)), startY = p.y - 0.11f * k, endY = p.y + 0.05f * k))
            drawLine(v.c(c, lerp(arpillera, C.NEGRO, 0.4f), PiezasLab.FRENTE), Offset(p.x + 0.09f * k, p.y - 0.07f * k), Offset(p.x + 0.13f * k, p.y - 0.1f * k), (0.015f * k).coerceAtLeast(0.6f))
            drawLine(v.c(c, lerp(arpillera, C.NEGRO, 0.25f), PiezasLab.FRENTE).copy(alpha = 0.4f), Offset(p.x - 0.08f * k, p.y - 0.08f * k), Offset(p.x - 0.05f * k, p.y + 0.03f * k), 0.4f)
        }
    }

    /** El catre de la cuarentena, junto a la puerta, con la sábana revuelta; la mascarilla de tela colgada en su cabecera. */
    private fun DrawScope.catre(v: VistaLab, mk: Marco) {
        cajaEn(v, mk, 0f, 1.9f, 0f, 0.7f, 0.36f, 0.42f, lerp(C.TELA, C.CIAN, 0.15f))
        cajaEn(v, mk, 0.15f, 1.3f, 0.05f, 0.65f, 0.42f, 0.5f, lerp(C.TELA, C.TIERRA, 0.2f))
        for (a in listOf(0.05f, 1.85f)) for (b in listOf(0.05f, 0.65f)) palo(v, mk.w(a, b, 0f), mk.w(a, b, 0.36f), 0.02f, C.ACERO, filo = false)
        // La sábana doblada a los pies y la mascarilla de tela, colgada de la cabecera.
        cajaEn(v, mk, 1.45f, 1.75f, 0.1f, 0.56f, 0.5f, 0.58f, lerp(C.TELA, C.CIAN, 0.08f))
        val p = CamLab.p(mk.w(0.02f, 0.35f, 0.6f)); val k = CamLab.k(mk.w(0.02f, 0.35f, 0.6f))
        palo(v, mk.w(0.02f, 0.05f, 0.36f), mk.w(0.02f, 0.05f, 0.7f), 0.02f, C.ACERO, filo = false); palo(v, mk.w(0.02f, 0.61f, 0.36f), mk.w(0.02f, 0.61f, 0.7f), 0.02f, C.ACERO, filo = false)
        palo(v, mk.w(0.02f, 0.05f, 0.7f), mk.w(0.02f, 0.61f, 0.7f), 0.02f, C.ACERO, filo = false)
        drawRect(v.c(mk.w(1.9f, 0.35f, 0.7f), lerp(C.TELA, C.CIAN, 0.25f), PiezasLab.FRENTE), p - Offset(0.06f * k, 0f), Size(0.12f * k, 0.07f * k))
    }

    /** La silla del vigía, junto a la puerta, en el cierre. */
    private fun DrawScope.silla(v: VistaLab, s: V3) {
        val mk = Marco(s + V3(-0.2f, 0f, -0.2f), V3(1f, 0f, 0f))
        for (a in listOf(0.02f, 0.38f)) for (b in listOf(-0.02f, -0.38f)) palo(v, mk.w(a, b, 0f), mk.w(a, b, 0.45f), 0.02f, C.ACERO, filo = false)
        cajaEn(v, mk, 0f, 0.4f, -0.4f, 0f, 0.45f, 0.48f, C.MADERA)
        cajaEn(v, mk, 0f, 0.4f, -0.4f, -0.37f, 0.48f, 0.9f, C.MADERA)
    }

    // ---------------------------------------------------------------- Lo de los días

    /**
     * Las banderas de papel de la Fundación, colgadas de una cuerda entre el cable de una lámpara y la campana, con los
     * colores de los departamentos; la de Botánica, verde, la más grande y en medio.
     */
    fun DrawScope.adornos(v: VistaLab) {
        if (EscenaLaboratorio.fundacion(v.m)) {
            val a = CamLab.p(MesaLab.lampara(4).x, 2.6f, MesaLab.lampara(4).z); val b = CamLab.p(CampanaLab.CENTRO.x - 0.6f, 2.75f, CampanaLab.CENTRO.z - 0.7f)
            banderines(v.l, a, b, 14f, listOf(C.CIAN, lerp(C.AMARILLO, C.TELA, 0.2f), C.GRAMA, lerp(C.AVISO, C.TELA, 0.3f), C.VIOLETA), 9f)
        }
        // La noche larga: los cestos de fruta seca y las velas (las llamas, en la parte viva).
        if (EscenaLaboratorio.velas(v.m) || EscenaLaboratorio.nocheLarga(v.m)) for ((a, b) in LucesLab.VELAS) {
            val c = MesaLab.MK.w(a, b, MesaLab.ALTO); cilindro(v, c, 0.025f, 0.12f, lerp(C.VELA, C.AMARILLO, 0.3f))
        }
        // El apagón: las linternas de manivela, cargándose en la mesa.
        if (EscenaLaboratorio.apagon(v.m)) for (i in 0..1) { val c = MesaLab.MK.w(0.9f + 0.25f * i, 0.3f, MesaLab.ALTO)
            cajaEn(v, Marco(c, MesaLab.MK.u), 0f, 0.18f, -0.05f, 0.05f, 0f, 0.09f, lerp(C.AMARILLO, C.NEGRO, 0.4f)) }
    }
}
