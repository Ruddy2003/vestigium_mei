package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Tramo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.casaQuemada
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.hoguera
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.pino
import io.github.ruddy2003.vestigium.ui.escenas.PiezasNapoleon.pinoAlbar
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaCamino.RUMBO
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaCamino.k
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaCamino.w
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaNapoleon.H
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaNapoleon.MIRADA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraCorta
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.sin

/**
 * La lejanía de la retirada: el cielo de invierno, las lomas blancas que se enciman con sus bosques como manchas, el
 * humo de otros fuegos y, en el llano de la derecha, la granja de madera quemada con su humo («Los pueblos del camino
 * de vuelta están quemados»). Los tres días del Berezina, el río con el hielo suelto, los dos puentes de caballetes y
 * las fogatas de la orilla. El día de Krasnoi, el humo de los cañones en las lomas; el día que queman los puentes, la
 * columna de humo del puente, a lo lejos.
 */
internal object RetiradaLejos {
    private val C = ColorNapoleon

    /** Las lomas lejanas, de la más honda a la más cercana: su altura sobre el horizonte y su semilla. */
    private val LOMAS = listOf(Triple(13f, 0.92f, "a"), Triple(9f, 0.8f, "b"), Triple(5.5f, 0.66f, "c"))

    /** La cresta de una loma: de lado a lado, ondulada, con su altura [alto] sobre el horizonte. */
    private fun cresta(alto: Float, semilla: String): List<Offset> {
        val az = Azar(Semilla.de("napoleon", "loma", semilla)); val f1 = az.entre(0f, 6f); val f2 = az.entre(0f, 6f)
        return (0..24).map { i -> val x = -20f + (ANCHO + 40f) * i / 24f; val u = i / 24f
            Offset(x, H - alto * (0.45f + 0.3f * sin(u * 5.2f + f1) + 0.18f * sin(u * 11f + f2) + az.entre(-0.06f, 0.06f))) }
    }

    /** Dónde echan humo los fuegos de lejos (de otros grupos, de los pueblos que arden). */
    val HUMOS = listOf(Offset(262f, 112f), Offset(352f, 116f), Offset(168f, 108f))

    /** La granja quemada del llano, a la derecha, y su compañera más lejos. */
    val GRANJA: Sitio = PiezasNapoleon.sitio(MIRADA, w(19f, 72f), 0.5f)
    val GRANJA_LEJOS: Sitio = PiezasNapoleon.sitio(MIRADA, w(15f, 120f), -0.3f)

    /** Por dónde sale el humo de la granja (lo que queda de la estufa). */
    val HUMO_GRANJA: Offset get() = GRANJA.p(0.4f, 3.2f, 0.3f)

    // ---------------------------------------------------------------- El cielo

    fun DrawScope.cielo(v: VistaNapoleon) {
        val m = v.m
        cielo(v.l, H, m.semilla("cielo"))
        astros(v.l, H, m.semilla("astros"))
        nubes(v.l, H - 4f, m.ficha.clima, m.semilla("nubes"))
    }

    // ---------------------------------------------------------------- Las lomas y los bosques lejanos

    fun DrawScope.lejos(v: VistaNapoleon) {
        val l = v.l; val p = v.p
        val lado = if (p.solALaDerecha) 1f else -1f
        val tierra = lerp(lerp(C.HOJA, C.BARRO, 0.35f), C.NIEVE, v.nieve)
        for ((j, lm) in LOMAS.withIndex()) {
            val (alto, lejos, sem) = lm
            val cr = cresta(alto, sem)
            val forma = curva(cr, H + 4f)
            drawPath(forma, p.medio(tierra, lejos))
            // La cara de sombra de cada loma (del lado contrario al sol) y su cresta al sol.
            clipPath(forma) {
                for (i in 1 until cr.size - 1) {
                    val a = cr[i - 1]; val b = cr[i]; val c = cr[i + 1]
                    val sube = (b.y - a.y) * lado                                      // mira al sol si sube hacia él
                    if (sube > 0f) drawPath(poligono(a, b, Offset(b.x, H + 4f), Offset(a.x, H + 4f)), p.sombra(tierra, lejos).copy(alpha = 0.5f))
                    if (c.y > b.y && b.y < a.y) mancha(b + Offset(0f, 1.5f), 8f, 2f, p.luz(tierra, lejos), 0.5f)
                }
                // Los bosques como manchas: grupos de pinos chicos, con su lado de luz.
                val az = Azar(Semilla.de("napoleon", "manchas", sem))
                repeat(5 + j * 2) {
                    val cx = az.entre(-10f, ANCHO + 10f); val y = H - alto * az.entre(0.05f, 0.45f); val ancho = az.entre(14f, 46f) * (1.2f - lejos * 0.5f)
                    val n = (ancho / 2.6f).toInt()
                    for (i in 0 until n) {
                        val x = cx - ancho / 2f + ancho * i / n + az.entre(-1f, 1f); val h = az.entre(2.2f, 4.6f) * (1.3f - lejos * 0.6f)
                        val y0 = y + az.entre(-1f, 1f)
                        drawPath(poligono(Offset(x - h * 0.28f, y0), Offset(x - h * 0.12f, y0 - h * 0.55f), Offset(x, y0 - h), Offset(x + h * 0.12f, y0 - h * 0.6f), Offset(x + h * 0.28f, y0)),
                            if (i % 3 == 0) p.lado(C.PINO, lejos) else p.medio(C.PINO_OSC, lejos))
                    }
                }
            }
        }
        // El bosque lejano de la derecha, una banda oscura y desigual que se apoya en el horizonte, con la bruma al pie.
        val az = Azar(Semilla.de("napoleon", "bandaDerecha"))
        val banda = Path().apply {
            moveTo(250f, H + 1f); var x = 250f
            while (x < ANCHO + 12f) { val h = az.entre(2f, 7f) * (if (az.real() < 0.15f) 0.3f else 1f); lineTo(x + 0.8f, H - h * 0.5f); lineTo(x + 1.6f, H - h); lineTo(x + 2.4f, H - h * 0.55f); x += az.entre(2.2f, 4.2f) }
            lineTo(ANCHO + 12f, H + 1f); close()
        }
        drawPath(banda, p.medio(C.PINO_OSC, 0.78f))
        if (v.nieve > 0.4f) repeat(40) { val x = az.entre(250f, ANCHO); mancha(Offset(x, H - az.entre(1f, 4f)), 1.4f, 0.4f, p.arriba(C.NIEVE, 0.75f), 0.6f) }
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.bruma.copy(alpha = 0.55f)), H - 6f, H + 1f), Offset(0f, H - 6f), Size(ANCHO, 7f))
        // Krasnoi: el humo blanco de los cañones en las lomas, junto al camino, y algún fogonazo.
        if (EscenaNapoleon.canonesEnLasColinas(v.m)) {
            val ak = Azar(Semilla.de("napoleon", "krasnoi", v.m.hora.toInt()))
            for (i in 0 until 7) {
                val q = Offset(ak.entre(150f, 370f), H - ak.entre(5f, 12f))
                mancha(q, ak.entre(6f, 14f), ak.entre(3f, 6f), lerp(l.tono(C.NIEVE), Color.White, 0.2f), 0.75f)
                mancha(q + Offset(4f, -3f), ak.entre(5f, 10f), ak.entre(2.5f, 5f), l.tono(lerp(C.NIEVE, C.CAPOTE, 0.3f)), 0.5f)
            }
        }
    }

    // ---------------------------------------------------------------- La granja quemada

    fun DrawScope.granja(v: VistaNapoleon) {
        val arde = EscenaNapoleon.puebloArde(v.m)
        val lejos0 = 0.55f; val lejos1 = 0.75f
        sombraCorta(v.r, GRANJA_LEJOS.pie, 30f, 18f)
        casaQuemada(GRANJA_LEJOS, v.en(GRANJA_LEJOS.pie), 5f, 4f, 31L, v.nieve, arde, lejos1)
        // La cerca rota, de estacas, que va de la granja al camino.
        val az = Azar(Semilla.de("napoleon", "cerca"))
        val estacas = (0 until 14).map { i -> val z = 56f - i * 2.6f; w(9.5f - i * 0.15f + az.entre(-0.2f, 0.2f), z) }
        for ((i, q) in estacas.withIndex()) {
            if (az.real() < 0.25f) continue
            val kk = k(RetiradaCamino.zEn(q.y)); val alto = az.entre(0.7f, 1.2f) * kk; val ladeo = az.entre(-0.25f, 0.25f) * alto
            drawLine(v.en(q).plano(C.MADERA_GRIS, q, 0f, 0.5f), q, q + Offset(ladeo, -alto), (0.1f * kk).coerceIn(0.4f, 2f), StrokeCap.Round)
            if (i > 0 && az.real() < 0.6f) { val r = estacas[i - 1]; drawLine(v.en(q).plano(C.MADERA_GRIS, q, 0f, 0.5f), q + Offset(0f, -alto * 0.6f), r + Offset(0f, -alto * 0.55f), (0.06f * kk).coerceIn(0.3f, 1.4f)) }
        }
        casaQuemada(GRANJA, v.en(GRANJA.pie), 6f, 5f, 17L, v.nieve, arde, lejos0)
        // Más casas del pueblo quemado, más allá, y el bosquecillo de pinos de detrás.
        for ((i, q) in listOf(w(27f, 150f) to 0.8f, w(9f, 190f) to -0.6f, w(38f, 240f) to 0.3f).withIndex()) {
            val s = PiezasNapoleon.sitio(MIRADA, q.first, q.second); casaQuemada(s, v.en(s.pie), 5f, 4f, 40L + i, v.nieve, arde, 0.75f + 0.05f * i)
        }
        for (pn in RetiradaCamino.bosquecillo()) {
            val q = pn.pie; val lejos = (pn.z / 180f).coerceIn(0f, 0.85f)
            if (pn.albar) pinoAlbar(v.en(q), q, pn.altoL, pn.anchoL, v.nieve * 0.7f, Azar(pn.semilla), lejos, (pn.k / 24f).coerceIn(0.2f, 1f))
            else pino(v.en(q), q, pn.altoL, pn.anchoL, v.nieve * 0.6f, Azar(pn.semilla), lejos, (pn.k / 24f).coerceIn(0.2f, 1f))
        }
        // Un fuego apagado junto a la granja, de los que pasaron antes.
        hoguera(PiezasNapoleon.sitio(MIRADA, w(11f, 46f)), v.en(w(11f, 46f)), 0f, v.nieve, 99L, 0.5f)
    }

    // ---------------------------------------------------------------- El Berezina

    /** La orilla de acá y la de allá del Berezina, en metros de profundidad. */
    const val ORILLA_ACA = 21f
    const val ORILLA_ALLA = 40f

    /** El río del Berezina, como forma. */
    fun rio(): Path {
        val az = Azar(Semilla.de("napoleon", "berezina"))
        val aca = (0..14).map { i -> val x = -20f + 420f * i / 14f; Offset(x, w(0f, ORILLA_ACA).y + az.entre(-1.2f, 1.2f)) }
        val alla = (0..14).map { i -> val x = -20f + 420f * i / 14f; Offset(x, w(0f, ORILLA_ALLA).y + az.entre(-0.6f, 0.6f)) }
        return poligono(alla + aca.reversed())
    }

    /**
     * El Berezina, que «no estaba helado… sino lleno de hielo suelto»: el agua negra con las placas blancas, los dos
     * puentes de caballetes de los pontoneros, el camino que acaba en la orilla, y las fogatas de la orilla de acá.
     */
    fun DrawScope.berezina(v: VistaNapoleon) {
        if (v.tramo != Tramo.BEREZINA) return
        val p = v.p; val l = v.l; val forma = rio(); val az = Azar(Semilla.de("napoleon", "berezina", "hielo"))
        val y0 = w(0f, ORILLA_ALLA).y; val y1 = w(0f, ORILLA_ACA).y
        drawPath(forma, Brush.verticalGradient(listOf(lerp(p.medio(lerp(C.AZUL, C.CENIZA, 0.6f), 0.35f), l.bajo, 0.2f), p.medio(lerp(C.AZUL, C.CENIZA, 0.72f), 0.15f)), y0, y1))
        clipPath(forma) {
            // El cielo reflejado en el agua, y las placas de hielo, más grandes y más separadas cerca.
            drawRect(Brush.verticalGradient(listOf(l.bajo.copy(alpha = 0.35f), Color.Transparent), y0, y1), Offset(-10f, y0), Size(ANCHO + 20f, y1 - y0))
            repeat(34) {
                val z = az.entre(ORILLA_ACA, ORILLA_ALLA); val q = w(az.entre(-30f, 40f), z); val kk = k(z)
                val placa = TrazosValle.borron(q, az.entre(0.6f, 1.8f) * kk, az.entre(0.1f, 0.22f) * kk, az, 7, 0.3f, 0.6f)
                drawPath(placa, p.arriba(C.NIEVE, (z / 140f)))
                drawPath(placa, v.a.plano(C.NIEVE_SOMBRA, q, 0f, z / 140f), style = Stroke(0.4f))
            }
            // Las fogatas reflejadas en el agua, de noche.
            if (EscenaNapoleon.fuegosLejos(v.m)) for (q in fogatas()) drawLine(C.LUMBRE.copy(alpha = 0.45f * v.noche), Offset(q.x, y1 - 1f), Offset(q.x + 0.5f, y0 + 2f), 1.2f, blendMode = BlendMode.Plus)
        }
        // Los dos puentes de caballetes, del camino a la otra orilla, con sus patas en el agua.
        for (x0 in listOf(-1.7f, 3.6f)) {
            // De lejos a cerca: los caballetes (patas en A metidas en el agua) y el tablero de tablas encima, que se va al fondo.
            val zs = generateSequence(ORILLA_ALLA + 0.5f) { it - 2.6f }.takeWhile { it >= ORILLA_ACA - 1.5f }.toList()
            fun borde(dx: Float, z: Float) = w(x0 + RUMBO * z + dx, z) + Offset(0f, -1.0f * k(z))
            for (z in zs) {
                val kk = k(z); val x = x0 + RUMBO * z; val lejos = z / 140f
                for (dx in listOf(-1.3f, 1.3f)) {
                    val pata = w(x + dx, z); val pata2 = w(x + dx * 0.6f, z)
                    drawLine(v.a.plano(C.MADERA_OSC, pata, 0f, lejos), pata + Offset(0f, 0.15f * kk), borde(dx * 0.9f, z), (0.1f * kk).coerceIn(0.3f, 1.6f))
                    drawLine(v.a.plano(C.MADERA_OSC, pata2, 0f, lejos), pata2 + Offset(0f, 0.15f * kk), borde(dx * 0.9f, z), (0.07f * kk).coerceIn(0.25f, 1.2f))
                }
            }
            val tablero = poligono(zs.map { borde(-1.5f, it) } + zs.reversed().map { borde(1.5f, it) })
            drawPath(tablero, p.arriba(C.MADERA_GRIS, 0.45f))
            clipPath(tablero) { for (z in zs) for (j in 0..2) { val zz = z - j * 0.85f; drawLine(v.a.plano(C.MADERA_OSC, borde(0f, zz), 0f, 0.5f), borde(-1.5f, zz), borde(1.5f, zz), 0.35f) } }
            // El canto del tablero, en sombra, y la nieve pisada encima.
            drawPath(curva(zs.map { borde(1.5f, it) + Offset(0f, 0.12f * k(it)) }), v.a.plano(C.MADERA_OSC, borde(1.5f, zs.last()), 0f, 0.5f), style = Stroke(1.1f))
            if (v.nieve > 0.3f) drawPath(curva(zs.map { borde(0f, it) }), p.arriba(C.NIEVE_SUCIA, 0.45f), style = Stroke(1.2f, cap = StrokeCap.Round))
        }
        // Las fogatas de la orilla de acá: muchas, de los que esperan.
        for ((i, q) in fogatas().withIndex()) { val s = PiezasNapoleon.sitio(MIRADA, q, i * 0.7f); hoguera(s, v.en(q), if (EscenaNapoleon.fuegosLejos(v.m) || v.cubierto > 0.4f) 0.7f else 0.12f, v.nieve, Semilla.de("napoleon", "fogata", i), 0.45f) }
    }

    /** Las fogatas de la orilla de acá del Berezina. */
    fun fogatas(): List<Offset> { val az = Azar(Semilla.de("napoleon", "fogatas")); return (0 until 8).map { val z = az.entre(14f, ORILLA_ACA - 1f); w(if (az.real() < 0.5f) az.entre(-12f, -4.5f) else az.entre(1.5f, 14f), z) } }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(v: VistaNapoleon, reloj: Float) {
        val l = v.l
        // El humo de otros fuegos, lejos.
        for ((j, q) in HUMOS.withIndex()) humo(l, q, 34f, 2.2f, reloj, v.m.viento + 0.2f, 0.25f, 0.7f, j + 10, 5, 0.35f)
        // La granja, que humea; en Maloyaroslavets, que arde.
        val arde = EscenaNapoleon.puebloArde(v.m)
        if (arde) {
            val c = GRANJA.p(0f, 1.2f, 0f); val kk = GRANJA.k
            resplandor(c, 6f * kk, 0.5f + 0.5f * v.noche)
            for (i in 0 until 5) llama(GRANJA.p(-2.4f + i * 1.2f, 1.6f + (i % 2) * 0.6f, 0f), (1.2f + (i % 3) * 0.5f) * kk, reloj, i + 60)
            humo(l, c + Offset(0f, -2f * kk), 70f, 14f, reloj, v.m.viento + 0.3f, 0.7f, 0.3f, 61, 9, 0.85f)
        } else humo(l, HUMO_GRANJA, 46f, 6f, reloj, v.m.viento + 0.25f, 0.45f, 0.4f, 62, 7, 0.55f)
        // El humo del puente que arde, del otro lado: una columna oscura a lo lejos.
        if (EscenaNapoleon.humoDelPuente(v.m)) humo(l, Offset(206f, H - 2f), 96f, 16f, reloj, 0.35f, 0.75f, 0.35f, 63, 10, 0.9f)
        // Krasnoi: el humo de los cañones, que deriva.
        if (EscenaNapoleon.canonesEnLasColinas(v.m)) for (i in 0 until 3) humo(l, Offset(180f + i * 70f, H - 8f), 22f, 6f, reloj, 0.5f, 0.1f, 0.6f, 64 + i, 5, 0.5f)
        // Las fogatas del Berezina.
        if (v.tramo == Tramo.BEREZINA) for ((i, q) in fogatas().withIndex()) {
            val kk = k(RetiradaCamino.zEn(q.y))
            if (EscenaNapoleon.fuegosLejos(v.m) || v.cubierto > 0.4f) { resplandor(q, 2.6f * kk, 0.3f + 0.6f * v.noche); llama(q, 0.8f * kk, reloj, i + 80) }
            humo(l, q, 7f * kk, 0.7f * kk, reloj, v.m.viento, 0.3f, 0.5f, i + 80, 4, 0.3f)
        }
    }

}
