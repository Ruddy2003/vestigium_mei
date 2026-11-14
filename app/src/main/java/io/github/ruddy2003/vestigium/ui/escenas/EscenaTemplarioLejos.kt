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
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LUZ_DE_CASA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.bajio
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cipres
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hierbaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.olivo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sembrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraCorta
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs

/**
 * Lo lejano del cuadro de Templario: el cielo, el Hermón con su nieve, la meseta del otro lado del lago con su
 * escarpe, el lago de Galilea con sus pueblos de la orilla, el valle del Jordán al norte y, más cerca, las laderas de
 * Galilea con sus bancales de olivos, sus campos de trigo, el camino y el pueblo de Safed en su loma.
 *
 * Todo va de atrás adelante, cada plano con menos bruma que el de detrás. Lo que queda por debajo del muro
 * ([CastilloTemplario.ALMENAS]) lo tapa el castillo.
 */
internal object LejosTemplario {
    private val C = ColorTemplario

    /** El horizonte: al 41 % del alto. */
    const val H = 118f

    /** La orilla de este lado del lago, puesta a mano: calas, puntas y la desembocadura del Jordán. */
    internal val ORILLA_CERCA = listOf(
        Offset(166f, 131.6f), Offset(172f, 133.4f), Offset(177f, 133f), Offset(183f, 135.4f), Offset(190f, 136f), Offset(196f, 138.6f), Offset(204f, 139.2f),
        Offset(209f, 141.8f), Offset(218f, 142.4f), Offset(226f, 144.6f), Offset(236f, 144.8f), Offset(247f, 146.6f), Offset(260f, 146.2f), Offset(272f, 147.8f),
        Offset(286f, 147.4f), Offset(298f, 148.8f), Offset(312f, 148.2f), Offset(326f, 149.6f), Offset(342f, 149.2f), Offset(356f, 150.4f), Offset(370f, 150f), Offset(384f, 151f),
    )

    /** La orilla de enfrente, al pie del escarpe. */
    internal val ORILLA_LEJOS = listOf(
        Offset(166f, 131.6f), Offset(176f, 128.8f), Offset(190f, 128f), Offset(206f, 127.4f), Offset(224f, 127.6f), Offset(244f, 126.8f), Offset(262f, 127.2f),
        Offset(284f, 126.6f), Offset(306f, 127f), Offset(330f, 126.4f), Offset(354f, 126.8f), Offset(384f, 126.2f),
    )

    /** El contorno del lago. */
    internal fun lago(): Path = Path().apply {
        moveTo(ORILLA_LEJOS.first().x, ORILLA_LEJOS.first().y)
        ORILLA_LEJOS.drop(1).forEach { lineTo(it.x, it.y) }
        lineTo(ANCHO + 4f, 126.2f); lineTo(ANCHO + 4f, 151f)
        ORILLA_CERCA.reversed().forEach { lineTo(it.x, it.y) }
        close()
    }

    // ---------------------------------------------------------------- Todo lo lejano

    fun DrawScope.lejos(m: Momento, l: Luces, p: Pincel) {
        val semilla = m.ficha.semilla
        val fijo = Azar(Semilla.de("templario", "tierra"))
        cielo(l, H, semilla)
        astros(l, H, semilla)
        nubes(l, H, m.ficha.clima, semilla)
        // Los montes de Damasco, muy lejos, por encima de la meseta.
        monte(l, perfil(fijo, 150f, 420f, H + 1f, 9f, 0.35f, 26), H + 2f, lerp(C.MONTE, C.SOMBRA, 0.25f), 0.9f)
        hermon(m, l, p, fijo)
        meseta(l, p, fijo)
        lagoDeGalilea(m, l, fijo)
        valleDelJordan(m, l, p)
        laderas(m, l, p, fijo)
        pueblo(m, l, p)
        if (EscenaTemplario.polvo(m)) {
            // El viento del desierto: el aire se espesa y lo lejano desaparece (el lago no se ve).
            drawRect(Brush.verticalGradient(0f to l.bruma.copy(alpha = 0.55f), 0.5f to l.bruma.copy(alpha = 0.92f), 1f to l.bruma.copy(alpha = 0.55f), startY = 70f, endY = 190f), Offset(0f, 70f), Size(ANCHO, 120f))
            val az = m.azar("polvo")
            repeat(7) { mancha(Offset(az.entre(-20f, ANCHO + 20f), az.entre(95f, 175f)), az.entre(70f, 150f), az.entre(6f, 14f), lerp(l.bruma, Color(0xFFE6CC90), 0.4f), 0.45f) }
        }
    }

    // ---------------------------------------------------------------- El Hermón

    /** El Hermón: un macizo ancho con la cumbre redonda, la nieve arriba (más en invierno) y sus barrancos. */
    private fun DrawScope.hermon(m: Momento, l: Luces, p: Pincel, fijo: Azar) {
        val cresta = listOf(
            Offset(18f, H + 1f), Offset(40f, 112f), Offset(58f, 106.6f), Offset(74f, 101f), Offset(90f, 96.2f), Offset(104f, 91.6f), Offset(116f, 88.6f), Offset(126f, 86.4f),
            Offset(134f, 85.2f), Offset(142f, 84.8f), Offset(150f, 85.6f), Offset(160f, 88f), Offset(172f, 92.4f), Offset(186f, 98f), Offset(202f, 104f), Offset(222f, 110f), Offset(248f, 114.6f), Offset(280f, H + 1f),
        )
        val lejos = 0.8f
        monte(l, cresta, H + 2f, C.MONTE, lejos)
        val forma = curva(cresta, H + 2f)
        val nieve = EscenaTemplario.nieveDelHermon(m)
        clipPath(forma) {
            // Los barrancos: canales que bajan de la cumbre, en sombra del lado contrario al sol.
            val cae = if (p.solALaDerecha) -1f else 1f
            for (i in 0 until 9) {
                val x0 = 70f + i * 17f + fijo.entre(-4f, 4f); val y0 = 94f + abs(x0 - 142f) * 0.16f
                drawPath(Path().apply { moveTo(x0, y0); cubicTo(x0 + cae * 3f, y0 + 8f, x0 + cae * 1f, y0 + 14f, x0 + cae * 6f, H + 2f) }, l.tono(mult(C.MONTE, C.SOMBRA, 0.7f), lejos).copy(alpha = 0.32f), style = Stroke(1.1f))
            }
            // La nieve: baja más cuanto más invierno, con el borde roto por los barrancos.
            val linea = 87f + (H - 6f - 87f) * nieve * 0.72f
            val borde = Path().apply {
                moveTo(0f, 0f); lineTo(ANCHO, 0f); lineTo(ANCHO, linea)
                var x = ANCHO
                while (x > 0f) { val dx = fijo.entre(7f, 14f); x -= dx; lineTo(x + dx * 0.5f, linea + fijo.entre(-1f, 1f)); lineTo(x, linea + (if (fijo.real() > 0.6f) fijo.entre(1f, 4f) * (0.4f + nieve) else fijo.entre(-1.4f, 0.5f))) }
                close()
            }
            val blanco = l.tono(Color(0xFFF6F4EE), lejos); val azul = l.tono(mult(Color(0xFFE8E8F2), C.SOMBRA, 0.55f), lejos)
            clipPath(borde) {
                drawPath(forma, Brush.horizontalGradient(if (p.solALaDerecha) listOf(azul, blanco, blanco) else listOf(blanco, blanco, azul), 60f, 230f))
                // Las vetas de roca que asoman en la nieve.
                for (i in 0 until 14) { val x = 76f + i * 11f + fijo.entre(-3f, 3f); val y = 90f + abs(x - 142f) * 0.14f + fijo.entre(0f, 6f); drawLine(l.tono(C.MONTE, lejos).copy(alpha = 0.35f), Offset(x, y), Offset(x + fijo.entre(-1f, 1f), y + fijo.entre(2f, 7f)), 0.7f) }
            }
        }
    }

    // ---------------------------------------------------------------- La meseta y el lago

    /** La meseta del otro lado del lago: un borde casi llano, con un escarpe de barrancos que baja hasta el agua. */
    private fun DrawScope.meseta(l: Luces, p: Pincel, fijo: Azar) {
        val borde = listOf(Offset(150f, 124f), Offset(162f, 120.4f), Offset(176f, 118.8f), Offset(196f, 118.2f), Offset(222f, 117.6f), Offset(250f, 117.9f), Offset(272f, 116.8f), Offset(300f, 117.2f),
            Offset(326f, 116.4f), Offset(352f, 116.9f), Offset(384f, 116.2f))
        val lejos = 0.62f
        val forma = curva(borde + listOf(Offset(384f, 128f), Offset(150f, 128f))).apply { close() }
        drawPath(forma, Brush.verticalGradient(listOf(p.luz(C.SILLAR_OSC, lejos), p.medio(lerp(C.SILLAR_OSC, C.MONTE, 0.4f), lejos)), 116f, 128f))
        clipPath(forma) {
            // Los barrancos del escarpe, con su cara en sombra, y el borde de basalto oscuro.
            val cae = if (p.solALaDerecha) -1f else 1f
            var x = 156f
            while (x < 386f) {
                // Un barranco: una hendidura que se abre hacia abajo, con su lado en sombra; no todos llegan al agua.
                val w = fijo.entre(2f, 5f); val hasta = fijo.entre(123f, 128f); val arranca = fijo.entre(118.5f, 121f)
                drawPath(Path().apply { moveTo(x, arranca); cubicTo(x + cae * w * 0.2f, arranca + 3f, x + cae * w * 0.5f, hasta - 2f, x + cae * w * 0.6f, hasta); lineTo(x + w * 0.5f, hasta); cubicTo(x + w * 0.2f, hasta - 3f, x + 0.3f, arranca + 2f, x, arranca); close() }, p.sombra(C.SILLAR_OSC, lejos).copy(alpha = 0.45f))
                x += w + fijo.entre(6f, 16f)
            }
            drawRect(p.medio(C.BASALTO, lejos).copy(alpha = 0.5f), Offset(150f, 116f), Size(240f, 2.2f))
        }
    }

    /**
     * El lago de Galilea, visto desde arriba: hondo y oscuro al pie del escarpe y claro en esta orilla; el escarpe y el
     * cielo reflejados; la calle de luz del sol bajo o de la luna; y los pueblos de pescadores de la orilla.
     */
    private fun DrawScope.lagoDeGalilea(m: Momento, l: Luces, fijo: Azar) {
        val forma = lago()
        val zona = Rect(160f, 126f, ANCHO + 4f, 151f)
        clipPath(forma) {
            agua(l, zona, m.azar("lago"), C.LAGO, m.viento * 0.6f, null, C.LAGO_HONDO, C.LAGO_ORILLA)
            val az = Azar(Semilla.de("templario", "reflejo"))
            // El escarpe se mira en el agua: una franja oscura pegada a la orilla de enfrente.
            for (i in 0 until ORILLA_LEJOS.size - 1) reflejoRoto(ORILLA_LEJOS[i].x, ORILLA_LEJOS[i + 1].x, maxOf(ORILLA_LEJOS[i].y, ORILLA_LEJOS[i + 1].y) - 0.2f, 5.4f, l.tono(lerp(C.SILLAR_OSC, C.MONTE, 0.5f), 0.55f), 0.55f, az, 0.7f)
            estela(l, zona, m.azar("estela"))
        }
        bajio(l, curva(ORILLA_CERCA), 6f, C.LAGO_ORILLA)
        // La franja de paso de esta orilla: guijarros claros, luego el talud de tierra, luego la hierba de las laderas.
        drawPath(curva(ORILLA_CERCA), l.tono(Color(0xFFD6CCB0), 0.5f).copy(alpha = 0.45f * (1f - 0.6f * l.noche)), style = Stroke(0.8f, cap = StrokeCap.Round))
        // Unas barcas de pescadores.
        val barcas = listOf(Offset(240f, 138f), Offset(296f, 134f), Offset(330f, 141f))
        barcas.forEachIndexed { i, b -> if ((m.azar("barca", i).real() > 0.35f) && !EscenaTemplario.polvo(m)) {
            drawLine(l.tono(C.MADERA_OSC, 0.5f), b, b + Offset(3.6f, 0f), 0.9f, StrokeCap.Round)
            drawPath(poligono(b + Offset(1.6f, -0.4f), b + Offset(1.6f, -4f), b + Offset(3.6f, -0.6f)), l.tono(C.LINO, 0.5f))
            reflejoRoto(b.x, b.x + 3.6f, b.y, 2.6f, l.tono(C.MADERA_OSC, 0.5f), 0.4f, fijo, 0.5f)
        } }
    }

    // ---------------------------------------------------------------- El valle del Jordán y las laderas

    /** Al norte del lago, el llano del Jordán: un verde de marjal con charcas y el río que entra en el lago. */
    private fun DrawScope.valleDelJordan(m: Momento, l: Luces, p: Pincel) {
        val lejos = 0.6f
        val hierba = hierbaDe(m.ficha.estacion, mediterraneo = true)
        val llano = curva(listOf(Offset(96f, H + 1f), Offset(130f, 119.6f), Offset(160f, 121.6f), Offset(176f, 124f), Offset(168f, 131.6f), Offset(140f, 134f), Offset(100f, 134f)), null).apply { close() }
        drawPath(llano, Brush.verticalGradient(listOf(l.tono(lerp(hierba, C.MONTE, 0.3f), lejos), l.tono(lerp(hierba, C.OLIVO, 0.4f), lejos)), H, 134f))
        val az = Azar(Semilla.de("templario", "jordan"))
        clipPath(llano) {
            repeat(10) { mancha(Offset(az.entre(104f, 170f), az.entre(121f, 133f)), az.entre(5f, 12f), az.entre(0.8f, 1.6f), l.tono(lerp(C.LAGO, Color.White, 0.25f), lejos), 0.45f) }
            repeat(26) { val q = Offset(az.entre(100f, 172f), az.entre(120f, 134f)); drawCircle(l.tono(C.OLIVO, lejos).copy(alpha = 0.6f), az.entre(0.5f, 1.1f), q) }
        }
        // El Jordán, que serpentea hasta el lago.
        drawPath(curva(listOf(Offset(122f, 120f), Offset(134f, 123f), Offset(128f, 126f), Offset(146f, 128.2f), Offset(158f, 128.8f), Offset(168f, 131.6f))), l.tono(lerp(C.LAGO, Color.White, 0.2f), lejos), style = Stroke(0.9f, cap = StrokeCap.Round))
    }

    /** Los trozos de ladera que se ven por encima del muro, de atrás adelante: dos lomas, con bancales, olivos y trigo. */
    private fun DrawScope.laderas(m: Momento, l: Luces, p: Pincel, fijo: Azar) {
        val hierba = lerp(hierbaDe(m.ficha.estacion, mediterraneo = true), C.TIERRA, 0.25f)
        val az = Azar(Semilla.de("templario", "laderas"))
        // La loma de atrás: baja hacia el lago y deja ver la orilla.
        val atras = listOf(Offset(-4f, 136f), Offset(30f, 134f), Offset(66f, 136.4f), Offset(96f, 134.6f), Offset(118f, 138f), Offset(150f, 141f), Offset(180f, 145.6f), Offset(208f, 150f), Offset(240f, 152.6f), Offset(278f, 154f), Offset(316f, 154.8f), Offset(352f, 156f), Offset(386f, 156.4f))
        terreno(l, p, atras, 0.42f, hierba, az)
        // La loma de delante, la del castillo: sube hacia la izquierda, donde está el pueblo.
        val delante = listOf(Offset(-4f, 146f), Offset(40f, 147f), Offset(84f, 148.6f), Offset(116f, 151.6f), Offset(150f, 156.6f), Offset(186f, 161.4f), Offset(226f, 165f), Offset(264f, 166.4f), Offset(300f, 165.2f), Offset(338f, 166.6f), Offset(386f, 166f))
        terreno(l, p, delante, 0.24f, hierba, az)
        // Bancales: muretes de piedra seca que siguen la loma.
        for (k in 0 until 4) {
            val dy = 5f + k * 5.2f
            drawPath(curva(delante.map { it + Offset(0f, dy + az.entre(-0.6f, 0.6f)) }), l.tono(C.SILLAR, 0.25f).copy(alpha = 0.55f), style = Stroke(0.7f))
            drawPath(curva(delante.map { it + Offset(0f, dy + 0.8f) }), l.tono(mult(C.TIERRA, C.SOMBRA, 0.7f), 0.25f).copy(alpha = 0.4f), style = Stroke(0.6f))
        }
        // Los campos de trigo: dos en la loma de atrás y tres en la de delante.
        val etapa = EscenaTemplario.trigo(m)
        val segado = EscenaTemplario.segado(m)
        val campos = listOf(
            listOf(Offset(262f, 154.6f), Offset(300f, 155f), Offset(304f, 159.6f), Offset(258f, 160f)) to 0.4f,
            listOf(Offset(318f, 155.4f), Offset(360f, 156.4f), Offset(364f, 161.2f), Offset(312f, 160.8f)) to 0.4f,
            listOf(Offset(280f, 166f), Offset(326f, 166.8f), Offset(332f, 175f), Offset(276f, 174.4f)) to 0.24f,
            listOf(Offset(332f, 167.4f), Offset(386f, 167f), Offset(386f, 176f), Offset(338f, 176f)) to 0.22f,
            listOf(Offset(286f, 177.6f), Offset(344f, 178.4f), Offset(352f, 190f), Offset(280f, 190f)) to 0.16f,
        )
        val siega = EscenaTemplario.siega(m); val guerra = EscenaTemplario.guerra(m)
        val azc = m.azar("campos")
        campos.forEachIndexed { i, (c, lejos) ->
            val rect = Rect(c.minOf { it.x }, c.minOf { it.y }, c.maxOf { it.x }, c.maxOf { it.y })
            val forma = poligono(c)
            if (segado || (siega && i % 2 == 0)) {
                // El rastrojo: amarillo pálido con las hileras de la hoz, y las gavillas si es la siega.
                drawPath(forma, l.tono(lerp(C.PAJA, C.TIERRA, 0.3f), lejos))
                clipPath(forma) { var y = rect.top + 1f; while (y < rect.bottom) { drawLine(l.tono(mult(C.PAJA, C.SOMBRA, 0.5f), lejos).copy(alpha = 0.5f), Offset(rect.left, y), Offset(rect.right, y + 0.4f), 0.45f); y += 1.6f } }
                if (siega) sembrado(l, forma, rect, 1f, lejos, azc, gavillas = true)
            } else sembrado(l, forma, rect, etapa, lejos, azc, quemado = if (guerra && i == 2) 0.85f else 0f)
        }
        // Los olivares, en filas que siguen los bancales, y algún algarrobo suelto.
        for (fila in 0 until 4) {
            var x = 212f + fila * 3f
            while (x < 276f - fila * 2f) {
                val y = 166f + fila * 6.4f + (x - 212f) * 0.02f + az.entre(-0.5f, 0.5f)
                sombraCorta(p.r, Offset(x, y), 3f, 4f)
                olivo(l, Offset(x, y), 6f + fila * 1.1f, 0.26f - fila * 0.04f, fijo)
                x += az.entre(7f, 10f) + fila
            }
        }
        for (x in listOf(30f, 52f, 74f, 92f, 340f, 364f)) { val y = if (x < 100f) 147.6f + az.entre(-1f, 1f) else 165.4f; sombraCorta(p.r, Offset(x, y), 3f, 5f); olivo(l, Offset(x, y), 7f, 0.26f, fijo) }
        // El camino que baja hacia el lago, en zigzag.
        val camino = curva(listOf(Offset(206f, 190f), Offset(214f, 182f), Offset(206f, 172f), Offset(222f, 164f), Offset(246f, 157f), Offset(236f, 152.4f), Offset(254f, 149f)))
        drawPath(camino, l.tono(lerp(C.CALIZA, C.TIERRA, 0.3f), 0.3f), style = Stroke(1.4f, cap = StrokeCap.Round))
        drawPath(camino, l.tono(mult(C.TIERRA, C.SOMBRA, 0.5f), 0.3f).copy(alpha = 0.4f), style = Stroke(0.4f, cap = StrokeCap.Round))
    }

    /** Una loma con el perfil [cresta], rellena hasta abajo: hierba que cambia de lejos a cerca, matas y piedras. */
    private fun DrawScope.terreno(l: Luces, p: Pincel, cresta: List<Offset>, lejos: Float, hierba: Color, az: Azar) {
        val forma = curva(cresta, 200f)
        val arriba = cresta.minOf { it.y }
        drawPath(forma, Brush.verticalGradient(listOf(l.tono(lerp(hierba, C.TIERRA, 0.15f), lejos), l.tono(hierba, lejos * 0.8f), l.tono(lerp(hierba, C.TIERRA, 0.4f), lejos * 0.6f)), arriba, 200f))
        clipPath(forma) {
            // Lomas dentro de la loma, con su lado de sombra: no es una mesa.
            repeat(6) {
                val c = Offset(az.entre(0f, ANCHO), az.entre(arriba + 3f, arriba + 22f)); val w = az.entre(24f, 60f)
                mancha(c, w, az.entre(2f, 4f), p.sombra(hierba, lejos), 0.35f)
                mancha(c + Offset(if (p.solALaDerecha) 5f else -5f, -2.6f), w * 0.7f, az.entre(1.2f, 2.4f), p.luz(hierba, lejos), 0.3f)
            }
            repeat(70) { val q = Offset(az.entre(0f, ANCHO), az.entre(arriba + 1f, 200f)); drawOval(l.tono(if (it % 3 == 0) C.SILLAR else lerp(C.OLIVO, C.MONTE, 0.3f), lejos).copy(alpha = 0.55f), Offset(q.x, q.y), Size(az.entre(0.8f, 2.4f), az.entre(0.5f, 1f))) }
        }
        // El borde de la loma: un filo claro del lado del sol.
        drawPath(curva(cresta), p.luz(hierba, lejos).copy(alpha = 0.35f), style = Stroke(0.5f))
    }

    // ---------------------------------------------------------------- El pueblo

    /** Cuántas ventanas del pueblo tienen luz, de 0 a 1: al anochecer muchas, de madrugada casi ninguna. */
    internal fun encendidas(m: Momento): Float {
        val h = m.hora
        return when { h >= 18.5 && h < 21.5 -> 0.6f; h >= 21.5 || h < 1.0 -> 0.3f; h < 4.5 -> 0.08f; h < 6.5 -> 0.3f; else -> 0f }
    }

    /**
     * El pueblo de Safed, en la loma de la izquierda: casas de piedra de azotea plana, alguna con su cúpula, que se
     * enciman unas a otras y bajan escalonadas hacia el lago, con higueras entre ellas. Delante, el muro de las huertas;
     * detrás, la loma.
     */
    private fun DrawScope.pueblo(m: Momento, l: Luces, p: Pincel) {
        val az = Azar(Semilla.de("templario", "pueblo"))
        val noche = l.noche > 0.4f; val prob = encendidas(m)
        val tonos = listOf(C.SILLAR, lerp(C.SILLAR, C.TIERRA, 0.35f), lerp(C.CALIZA, C.SILLAR, 0.5f), lerp(C.SILLAR, C.SILLAR_OSC, 0.3f))
        fun casa(x: Float, base: Float, w: Float, h: Float, lejos: Float) {
            val muro = tonos[az.entero(tonos.size)]
            sombraCorta(p.r, Offset(x + w / 2f, base), w * 1.1f, h)
            val lado = w * az.entre(0.25f, 0.4f)
            drawRect(p.frente(muro, lejos), Offset(x, base - h), Size(w - lado, h))
            drawRect(if (p.solALaDerecha) p.luz(muro, lejos) else p.sombra(muro, lejos), Offset(x + w - lado, base - h), Size(lado, h))
            // La azotea, que se ve un poco desde arriba, con su pretil.
            drawPath(poligono(Offset(x - 0.3f, base - h), Offset(x + w - lado, base - h), Offset(x + w + 0.6f, base - h - 1.2f), Offset(x + 0.6f, base - h - 1.2f)), p.arriba(lerp(muro, C.CALIZA, 0.35f), lejos))
            if (az.real() > 0.84f) drawPath(Path().apply { val c = x + (w - lado) * 0.5f; moveTo(c - w * 0.28f, base - h - 0.8f); cubicTo(c - w * 0.28f, base - h - w * 0.5f, c + w * 0.28f, base - h - w * 0.5f, c + w * 0.28f, base - h - 0.8f); close() },
                Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.CALIZA, lejos), p.luz(C.CALIZA, lejos)) else listOf(p.luz(C.CALIZA, lejos), p.sombra(C.CALIZA, lejos)), x, x + w))
            // Una puerta o una ventana: oscura de día; de noche, con luz en algunas.
            val v = Offset(x + (w - lado) * az.entre(0.25f, 0.6f), base - h * az.entre(0.45f, 0.7f))
            val vw = maxOf(0.6f, w * 0.13f); val vh = maxOf(0.8f, h * 0.24f)
            if (noche && az.real() < prob) { drawRect(LUZ_DE_CASA.copy(alpha = 0.95f), v, Size(vw, vh)); drawCircle(LUZ_DE_CASA.copy(alpha = 0.18f), vh * 1.6f, v + Offset(vw / 2f, vh / 2f)) }
            else drawRect(l.tono(lerp(C.MADERA_OSC, C.NEGRO, 0.3f), lejos), v, Size(vw, vh))
            if (az.real() > 0.6f) drawRect(l.tono(C.NEGRO, lejos).copy(alpha = 0.7f), Offset(x + (w - lado) * 0.75f, base - h * 0.42f), Size(maxOf(0.5f, w * 0.1f), h * 0.42f))
        }
        fun arbol(pie: Offset, alto: Float, lejos: Float) {
            sombraCorta(p.r, pie, alto * 0.5f, alto)
            val oscuro = l.tono(lerp(C.OLIVO, C.MONTE, 0.45f), lejos); val claro = l.tono(lerp(C.OLIVO, Color(0xFFD8D8A0), 0.25f), lejos)
            drawLine(l.tono(C.MADERA_OSC, lejos), pie, pie + Offset(0f, -alto * 0.4f), maxOf(0.5f, alto * 0.1f))
            for (i in 0 until 4) { val q = pie + Offset(az.entre(-alto * 0.3f, alto * 0.3f), -alto * az.entre(0.55f, 0.85f)); drawCircle(if (i == 3) claro else oscuro, alto * az.entre(0.22f, 0.32f), q + if (i == 3) Offset(if (p.solALaDerecha) alto * 0.1f else -alto * 0.1f, -alto * 0.08f) else Offset.Zero) }
        }
        // Las filas, de atrás adelante: cada una más grande, más nítida y más abajo; la loma sube hacia la izquierda.
        val filas = listOf(
            Triple(150f, 0.4f, 0.6f), Triple(157f, 0.34f, 0.75f), Triple(165f, 0.28f, 0.9f), Triple(174f, 0.22f, 1.05f), Triple(183f, 0.18f, 1.2f),
        )
        for ((base0, lejos, k) in filas) {
            var x = 102f - az.entre(0f, 6f)
            val fin = 212f - (k - 0.6f) * 20f
            while (x < fin) {
                val w = az.entre(5f, 9f) * k; val h = az.entre(3.4f, 6f) * k
                val base = base0 + (x - 102f) * 0.1f + az.entre(-1.2f, 1.2f)
                if (az.real() > 0.82f) arbol(Offset(x + w / 2f, base + 0.4f), az.entre(5f, 8f) * k, lejos) else casa(x, base, w, h, lejos)
                x += w * az.entre(0.7f, 0.95f)
            }
        }
        // Un alminar de piedra entre las casas, y la cúpula de la sinagoga: el pueblo no es solo de los frailes.
        val al = Offset(156f, 166f)
        drawRect(p.frente(C.SILLAR, 0.3f), Offset(al.x - 1.8f, al.y - 16f), Size(3.6f, 16f)); drawRect(if (p.solALaDerecha) p.luz(C.SILLAR, 0.3f) else p.sombra(C.SILLAR, 0.3f), Offset(al.x + 0.6f, al.y - 16f), Size(1.2f, 16f))
        drawRect(p.arriba(C.CALIZA, 0.3f), Offset(al.x - 2.4f, al.y - 17f), Size(4.8f, 1.2f)); drawPath(poligono(Offset(al.x - 1.4f, al.y - 17f), Offset(al.x + 1.4f, al.y - 17f), Offset(al.x, al.y - 20f)), p.medio(C.SILLAR_OSC, 0.3f))
        // El muro de las huertas, por delante, con su sombra, y las higueras de las huertas.
        val a = Offset(96f, 184f); val b = Offset(210f, 190f)
        drawLine(p.medio(C.SILLAR, 0.15f), a, b, 1.8f)
        drawLine(p.luz(C.CALIZA, 0.15f), a + Offset(0f, -0.8f), b + Offset(0f, -0.8f), 0.6f)
        if (p.r.fuerza > 0.05f) drawLine(p.r.color, a + Offset(0f, 1.4f), b + Offset(0f, 1.4f), 1.2f, alpha = p.r.fuerza * 0.6f, blendMode = BlendMode.Multiply)
    }
}
