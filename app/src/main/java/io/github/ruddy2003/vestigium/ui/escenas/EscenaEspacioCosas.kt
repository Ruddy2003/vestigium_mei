package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaEspacio.CALEFACTOR
import io.github.ruddy2003.vestigium.ui.escenas.EscenaEspacio.MESA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerpo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.guirnalda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoH
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.sin

/**
 * Las cosas del primer plano del módulo de la astronauta (`EscenaEspacio`), cada una con su silueta (para la sombra
 * que arroja), dónde pisa y cómo se pinta; lo que hay sobre la mesa según su hora; lo que flota los días sin peso; y
 * los doce objetos del día. Todo pide su color al [Alumbrado] del momento.
 */
internal object CosasEspacio {
    private val C = ColorEspacio
    private val E = EscenaEspacio

    private fun redondo(x0: Float, y0: Float, x1: Float, y1: Float, r: Float) = Path().apply { addRoundRect(RoundRect(x0, y0, x1, y1, CornerRadius(r))) }

    /** Donde se apoya el objeto del día en el dibujo de la mesa (antes de agrandarla): el centro del tablero. */
    private val SOBRE = Offset(MESA.x - 6f, MESA.y - 37.4f)
    /** Lo mismo, ya en el cuadro. */
    private val SOBRE_EN_EL_CUADRO = MESA + (SOBRE - MESA) * E.K_MESA

    /** Cuánto se agranda el objeto del día sobre la mesa, para que se lea a tamaño de teléfono. */
    private const val K_OBJETO = 1.35f

    /** La misma cosa, [k] veces más grande, sin moverla de donde pisa. */
    private fun grande(c: Cosa, k: Float): Cosa {
        val f = Path().apply { addPath(c.forma); transform(Matrix().apply { translate(c.pie.x, c.pie.y); scale(k, k); translate(-c.pie.x, -c.pie.y) }) }
        return Cosa(f, c.pie, c.bajoElFollaje) { scale(k, k, c.pie) { c.pintar(this) } }
    }

    fun cosas(m: Momento, a: Alumbrado): List<Cosa> = buildList {
        add(grande(torre(m, a), 1.1f)); add(grande(cajon(m, a), 1.3f)); add(grande(herramientas(m, a), 1.3f)); add(grande(mesa(m, a), E.K_MESA)); add(algas(m, a))
        add(if (E.parada(m)) grande(calefactor(m, a), 1.15f) else grande(taburete(m, a), 1.25f))
        if (E.cosecha(m)) add(grande(sacos(a), 1.15f))
    }

    // ---------------------------------------------------------------- La torre de cultivo

    /** La torre de cultivo: un tubo blanco con bolsillos de los que salen las matas, sobre su depósito de agua. Crece con el arco `granja`. */
    private fun torre(m: Momento, a: Alumbrado): Cosa {
        val pie = Offset(30f, 280f); val crecido = E.cultivo(m)
        val deposito = redondo(11f, 258f, 49f, 280f, 4.4f); val tubo = redondo(23.4f, 146f, 36.6f, 260f, 5f)
        val forma = Path().apply { addPath(deposito); addPath(tubo) }
        return Cosa(Path().apply { addPath(forma, Offset(0f, -3f)) }, pie + Offset(0f, -3f)) { translate(0f, -3f) {
            // La manguera del riego, que sube hasta el techo, y la lámpara de arriba.
            drawPath(Path().apply { moveTo(30f, 148f); cubicTo(31f, 110f, 22f, 60f, 26f, -3f) }, a.en(Offset(28f, 80f), C.TELA_AZUL), style = Stroke(1.3f, cap = StrokeCap.Round))
            cuerpo(a, tubo, 23.4f, 36.6f, 204f, C.TELA)
            clipPath(tubo) { for (y in listOf(170f, 196f, 222f, 248f)) drawLine(C.SOMBRA.copy(alpha = 0.3f), Offset(22f, y), Offset(38f, y), 0.6f); val az = Azar(Semilla.de("espacio", "tubo")); repeat(5) { mancha(Offset(az.entre(24f, 36f), az.entre(150f, 258f)), 3f, az.entre(4f, 9f), C.VERDE_OSC, 0.12f) } }
            // Los bolsillos, a un lado y a otro, y lo que crece en cada uno.
            for (i in 0..6) {
                val lado = if (i % 2 == 0) -1f else 1f; val y = 158f + i * 15.4f
                val boca = Offset(30f + lado * 6.6f, y)
                with(ModuloEspacio) { mataDeHoja(a, boca + Offset(lado * 2.4f, -0.6f), 5f + 13f * crecido * (if (i == 0) 1.15f else 1f), if (i % 3 == 0) C.VERDE_OSC else C.VERDE, Azar(Semilla.de("espacio", "torre", i)), redonda = i % 2 == 0) }
                drawPath(poligono(boca + Offset(-lado * 1f, -2f), boca + Offset(lado * 5.4f, -3.4f), boca + Offset(lado * 4.4f, 3f), boca + Offset(-lado * 1f, 4.4f)), a.en(boca, lerp(C.TELA, C.CASCO_OSC, 0.3f), lado))
                drawLine(Color.White.copy(alpha = 0.35f), boca + Offset(-lado * 1f, -2f), boca + Offset(lado * 5.4f, -3.4f), 0.5f)
            }
            with(ModuloEspacio) { mataDeHoja(a, Offset(30f, 148f), 7f + 12f * crecido, C.VERDE, Azar(Semilla.de("espacio", "torre", 9)), redonda = true) }
            // El depósito: con su mirilla del agua, su tapón y su banda.
            cuerpo(a, deposito, 11f, 49f, 270f, lerp(C.CASCO, C.TELA, 0.5f))
            clipPath(deposito) {
                drawRect(a.en(Offset(30f, 262f), C.NARANJA), Offset(11f, 262f), Size(38f, 2.6f))
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, C.SOMBRA.copy(alpha = 0.3f)), 270f, 280f), Offset(11f, 270f), Size(38f, 10f))
            }
            drawRoundRect(C.GOMA, Offset(16f, 266.6f), Size(5f, 10f), CornerRadius(1.4f)); drawRoundRect(lerp(C.CIAN, C.TELA_AZUL, 0.4f), Offset(16.8f, 270.4f), Size(3.4f, 5.6f), CornerRadius(1f))
            drawRoundRect(a.en(Offset(42f, 258f), C.NARANJA), Offset(38.6f, 255.4f), Size(7f, 3.6f), CornerRadius(1.2f))
            for (x in listOf(15f, 45f)) drawOval(a.en(Offset(x, 281f), C.GOMA), Offset(x - 2.6f, 278.6f), Size(5.2f, 4f))
        } }
    }

    // ---------------------------------------------------------------- El cajón de carga y la caja de herramientas

    /** El cajón de carga: de plástico crema, con cantoneras, dos cinchas naranjas y los roces de quince años. Encima se sube el gato. */
    private fun cajon(m: Momento, a: Alumbrado): Cosa {
        val pie = Offset(104f, 270f)
        val frente = redondo(77f, 237f, 131f, 270f, 2.4f)
        val tapa = poligono(Offset(77.6f, 237.4f), Offset(130.4f, 237.4f), Offset(126.6f, 231.6f), Offset(81.4f, 231.6f))
        val forma = Path().apply { addPath(frente); addPath(tapa) }
        return Cosa(Path().apply { addPath(forma, Offset(0f, -5f)) }, pie + Offset(0f, -5f)) { translate(0f, -5f) {
            planoH(a, tapa, 77f, 131f, 234f, lerp(C.TELA, Color.White, 0.16f), pasos = 3)
            cuerpo(a, frente, 77f, 131f, 254f, lerp(C.TELA, C.CASCO, 0.35f))
            clipPath(frente) {
                val az = Azar(Semilla.de("espacio", "cajon"))
                drawLine(C.SOMBRA.copy(alpha = 0.35f), Offset(77f, 243.6f), Offset(131f, 243.6f), 0.7f); drawLine(Color.White.copy(alpha = 0.3f), Offset(77f, 244.4f), Offset(131f, 244.4f), 0.4f)
                repeat(9) { val o = Offset(az.entre(79f, 129f), az.entre(246f, 268f)); drawLine(C.SOMBRA.copy(alpha = 0.2f), o, o + Offset(az.entre(3f, 10f), az.entre(-1.4f, 1.4f)), 0.5f) }
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, C.SOMBRA.copy(alpha = 0.28f)), 260f, 270f), Offset(77f, 260f), Size(54f, 10f))
                // La etiqueta: bloques de color, sin letra.
                drawRect(a.en(Offset(104f, 256f), C.TRIGO), Offset(98f, 250f), Size(13f, 9f)); drawPath(poligono(Offset(98f, 259f), Offset(104f, 250f), Offset(107f, 250f), Offset(101f, 259f)), a.en(Offset(104f, 256f), C.GOMA)); drawRect(a.en(Offset(104f, 256f), C.ROJO), Offset(112.4f, 250f), Size(3f, 9f))
                for (x in listOf(77f, 126f)) for (y in listOf(237f, 265f)) drawRect(a.en(Offset(x, y), C.ACERO), Offset(x, y), Size(5f, 5f))
            }
            // Las cinchas, que pasan por encima de la tapa, con sus hebillas.
            for (x in listOf(86f, 120f)) {
                val cincha = a.en(Offset(x, 250f), C.NARANJA)
                drawRect(cincha, Offset(x, 237f), Size(4.6f, 33f)); drawPath(poligono(Offset(x, 237.4f), Offset(x + 4.6f, 237.4f), Offset(x + 4.2f, 231.6f), Offset(x + 0.6f, 231.6f)), lerp(cincha, Color.White, 0.16f))
                drawLine(C.SOMBRA.copy(alpha = 0.3f), Offset(x + 4.6f, 237f), Offset(x + 4.6f, 270f), 0.5f)
                drawRect(a.en(Offset(x, 248f), C.ACERO), Offset(x - 0.8f, 246f), Size(6.2f, 4.4f)); drawRect(C.GOMA, Offset(x + 0.8f, 247.2f), Size(3f, 2f))
            }
            drawPath(tapa, C.GOMA.copy(alpha = 0.4f), style = Stroke(0.5f)); drawPath(frente, C.GOMA.copy(alpha = 0.5f), style = Stroke(0.6f))
            if (E.fiesta(m) && !E.lamparitas(m)) {
                // La botella del vino de la granja y dos vasos, encima.
                val o = Offset(118f, 234f)
                val botella = Path().apply { moveTo(o.x - 3f, o.y); lineTo(o.x - 3f, o.y - 11f); cubicTo(o.x - 3f, o.y - 15f, o.x - 1.2f, o.y - 15f, o.x - 1.2f, o.y - 19f); lineTo(o.x + 1.2f, o.y - 19f); cubicTo(o.x + 1.2f, o.y - 15f, o.x + 3f, o.y - 15f, o.x + 3f, o.y - 11f); lineTo(o.x + 3f, o.y); close() }
                cuerpo(a, botella, o.x - 3f, o.x + 3f, o.y - 8f, C.VERDE_OSC); drawRect(a.en(o, C.TELA), Offset(o.x - 3f, o.y - 9f), Size(6f, 4.4f))
                for (dx in listOf(-8f, 7.4f)) { val v = redondo(o.x + dx - 2.2f, o.y - 6f + 0.6f, o.x + dx + 2.2f, o.y + 0.6f, 0.8f); cuerpo(a, v, o.x + dx - 2.2f, o.x + dx + 2.2f, o.y - 3f, C.TELA); drawRect(a.en(o, C.ROJO).copy(alpha = 0.7f), Offset(o.x + dx - 1.8f, o.y - 3f), Size(3.6f, 3f)) }
            }
            // Un jersey que alguien dejó en la esquina del cajón, con una manga colgando.
            val jersey = lerp(C.ROJO, C.TELA, 0.45f)
            drawPath(poligono(listOf(Offset(79f, 237.4f), Offset(81.6f, 232.2f), Offset(93f, 231.8f), Offset(95f, 237.4f), Offset(95.6f, 246f), Offset(92f, 248.6f), Offset(89f, 243.6f), Offset(86.6f, 253f), Offset(81.4f, 252f), Offset(82f, 242f), Offset(78.4f, 243f))), a.en(Offset(86f, 244f), lerp(jersey, C.SOMBRA, 0.14f)))
            drawPath(poligono(Offset(79f, 237.4f), Offset(81.6f, 232.2f), Offset(93f, 231.8f), Offset(95f, 237.4f)), a.en(Offset(86f, 234f), lerp(jersey, Color.White, 0.14f)))
            drawLine(a.en(Offset(84f, 252f), C.TELA), Offset(81.6f, 251f), Offset(86.4f, 252f), 1.3f); drawLine(C.SOMBRA.copy(alpha = 0.25f), Offset(84.6f, 238f), Offset(84f, 250f), 0.5f); drawLine(C.SOMBRA.copy(alpha = 0.2f), Offset(79f, 237.6f), Offset(95f, 237.6f), 0.5f)
        } }
    }

    /** La caja de herramientas, roja: cerrada, o abierta y con las herramientas a la vista a sus horas de trabajo. */
    private fun herramientas(m: Momento, a: Alumbrado): Cosa {
        val pie = Offset(149f, 277f); val abierta = E.trabajando(m) && !E.sinPeso(m)
        val caja = redondo(135f, 265f, 163f, 277f, 1.6f)
        val forma = Path().apply { addPath(caja); if (abierta) addPath(poligono(Offset(136f, 265f), Offset(162f, 265f), Offset(165f, 253f), Offset(139f, 253f))) else addRect(Rect(137f, 261f, 161f, 265f)) }
        return Cosa(Path().apply { addPath(forma, Offset(0f, -4f)) }, pie + Offset(0f, -4f)) { translate(0f, -4f) {
            if (abierta) {
                val tapa = poligono(Offset(136f, 265f), Offset(162f, 265f), Offset(165f, 253f), Offset(139f, 253f))
                drawPath(tapa, a.en(Offset(150f, 258f), lerp(C.ROJO, C.SOMBRA, 0.35f))); drawPath(tapa, C.GOMA.copy(alpha = 0.5f), style = Stroke(0.5f))
                // Lo que asoma: una llave, un destornillador y un rollo de cable.
                drawLine(a.en(Offset(142f, 258f), C.ACERO), Offset(141f, 266f), Offset(138f, 255f), 1.6f, StrokeCap.Round); drawCircle(a.en(Offset(138f, 254f), C.ACERO), 2f, Offset(137.8f, 254f), style = Stroke(1.1f))
                drawLine(a.en(Offset(150f, 258f), C.NARANJA), Offset(150f, 266f), Offset(153f, 259f), 2.2f, StrokeCap.Round); drawLine(a.en(Offset(154f, 256f), C.ACERO), Offset(153f, 259f), Offset(155.4f, 253.6f), 0.8f, StrokeCap.Round)
                drawOval(a.en(Offset(158f, 262f), C.TELA_AZUL), Offset(155f, 259.6f), Size(6f, 5f), style = Stroke(1.3f))
            } else {
                drawRect(a.en(Offset(149f, 263f), lerp(C.ROJO, C.SOMBRA, 0.2f)), Offset(137f, 261.4f), Size(24f, 4f))
                drawArc(a.en(Offset(149f, 260f), C.GOMA), 180f, 180f, false, Offset(144f, 257.6f), Size(10f, 8f), style = Stroke(1.2f))
            }
            cuerpo(a, caja, 135f, 163f, 271f, C.ROJO)
            drawLine(C.SOMBRA.copy(alpha = 0.4f), Offset(135f, 268.6f), Offset(163f, 268.6f), 0.5f); for (x in listOf(142f, 156f)) drawRect(a.en(Offset(x, 268f), C.ACERO), Offset(x - 1.4f, 266.6f), Size(2.8f, 3.4f))
        } }
    }

    // ---------------------------------------------------------------- La mesa

    /** La mesa de trabajo: el tablero, las patas, la balda con su caja, la lámpara de brazo y, encima, el objeto del día y lo que toca a esa hora. */
    private fun mesa(m: Momento, a: Alumbrado): Cosa {
        val tablero = poligono(Offset(176f, 229f), Offset(268f, 229f), Offset(274f, 236.4f), Offset(170f, 236.4f))
        val forma = Path().apply { addPath(tablero); addRect(Rect(170f, 236f, 274f, 240f)); addRect(Rect(174f, 240f, 178.4f, 272f)); addRect(Rect(265.6f, 240f, 270f, 272f)); addRect(Rect(190f, 243f, 228f, 257f)) }
        return Cosa(Path().apply { addPath(forma, Offset(MESA.x - 222f, MESA.y - 272f)) }, MESA) { translate(MESA.x - 222f, MESA.y - 272f) {
            // Las patas de atrás, la balda y lo que guarda; luego las de delante.
            for (x in listOf(183f, 261f)) drawRect(a.en(Offset(x, 252f), lerp(C.ACERO, C.SOMBRA, 0.45f)), Offset(x - 1.2f, 238f), Size(2.4f, 29f))
            drawPath(poligono(Offset(180f, 255f), Offset(264f, 255f), Offset(268f, 259f), Offset(176f, 259f)), a.en(Offset(222f, 257f), lerp(C.CASCO_OSC, C.SOMBRA, 0.2f)))
            val caja = redondo(190f, 243f, 228f, 257f, 1.6f)
            cuerpo(a, caja, 190f, 228f, 250f, C.TELA_AZUL); drawRect(a.en(Offset(209f, 247f), C.TELA), Offset(203f, 246f), Size(12f, 4.6f)); drawLine(C.SOMBRA.copy(alpha = 0.3f), Offset(190f, 245.4f), Offset(228f, 245.4f), 0.5f)
            drawOval(a.en(Offset(244f, 252f), C.TELA), Offset(236f, 247f), Size(16f, 9.6f)); drawOval(a.en(Offset(244f, 252f), lerp(C.TELA, C.SOMBRA, 0.4f)), Offset(241f, 250f), Size(6f, 3.6f))
            for (x in listOf(176.2f, 267.8f)) { val pata = Path().apply { addRect(Rect(x - 2.2f, 239f, x + 2.2f, 272f)) }; cuerpo(a, pata, x - 2.2f, x + 2.2f, 256f, C.ACERO); drawOval(a.en(Offset(x, 272f), C.GOMA), Offset(x - 3.2f, 272f - 1.6f), Size(6.4f, 3f)) }
            drawRect(a.en(Offset(222f, 264f), lerp(C.ACERO, C.SOMBRA, 0.2f)), Offset(176f, 262.6f), Size(92f, 1.8f))
            // El tablero: la cara de arriba, gastada, y el canto con su filo.
            planoH(a, tablero, 170f, 274f, 233f, lerp(C.CASCO, Color.White, 0.1f), pasos = 5)
            clipPath(tablero) { val az = Azar(Semilla.de("espacio", "mesa")); repeat(12) { val o = Offset(az.entre(172f, 270f), az.entre(229.6f, 236f)); drawLine(C.SOMBRA.copy(alpha = 0.16f), o, o + Offset(az.entre(3f, 12f), az.entre(-0.4f, 0.4f)), 0.5f) }; drawOval(C.SOMBRA.copy(alpha = 0.14f), Offset(192f, 231f), Size(9f, 3f), style = Stroke(0.5f)) }
            planoH(a, Path().apply { addRect(Rect(170f, 236.4f, 274f, 240f)) }, 170f, 274f, 238f, lerp(C.CASCO_OSC, C.SOMBRA, 0.14f), pasos = 5)
            drawLine(Color.White.copy(alpha = 0.4f), Offset(170f, 236.6f), Offset(274f, 236.6f), 0.5f); drawRect(a.en(Offset(196f, 238f), C.NARANJA), Offset(190f, 237.2f), Size(12f, 2f))
            flexo(m, a)
            val s222 = Offset(216f, 234.6f)
            if (!E.sinPeso(m)) { sobreLaMesa(m, a); if (m.ficha.objeto != null) drawOval(C.SOMBRA.copy(alpha = 0.28f), Offset(s222.x - 17f, s222.y - 1.6f), Size(36f, 3.4f)); scale(K_OBJETO, K_OBJETO, s222) { objeto(m, a, s222) } }
            else { for (x in listOf(184f, 204f, 228f)) drawRect(a.en(Offset(x, 232f), C.GOMA).copy(alpha = 0.6f), Offset(x, 231f), Size(8f, 2.4f)) }   // las tiras de agarre, vacías
            if (E.fiesta(m) && !E.lamparitas(m)) guirnalda(E.luzDeAdorno(m, a, MESA + Offset(0f, -30f)), Offset(172f, 239.6f), Offset(272f, 239.6f), 7f, m.azar("guirnalda"), hoja = C.VERDE, flores = listOf(C.NARANJA, C.TELA, C.LUZ_CULTIVO), grueso = 2.6f)
        } }
    }

    /** La lámpara de brazo de la mesa, sujeta al canto con su mordaza. */
    private fun DrawScope.flexo(m: Momento, a: Alumbrado) {
        val base = Offset(264f, 231f); val codo = Offset(267f, 220f); val cabeza = Offset(256f, 216.6f)
        drawRect(a.en(base, C.GOMA), Offset(base.x - 2.6f, base.y - 1f), Size(5.2f, 7f))
        drawLine(a.en(codo, C.ACERO), base, codo, 1.3f, StrokeCap.Round); drawLine(a.en(codo, C.ACERO), codo, cabeza, 1.3f, StrokeCap.Round); drawCircle(a.en(codo, C.NARANJA), 1.5f, codo)
        val pantalla = poligono(cabeza + Offset(-1f, -3f), cabeza + Offset(3f, -1f), cabeza + Offset(-3f, 7f), cabeza + Offset(-9f, 3.6f))
        drawPath(pantalla, a.en(cabeza, C.NARANJA, -1f)); drawPath(pantalla, C.GOMA.copy(alpha = 0.5f), style = Stroke(0.5f))
        if (E.flexo(m)) {
            drawLine(Color(0xFFFFF4D8), cabeza + Offset(-8.4f, 4f), cabeza + Offset(-3.2f, 7f), 1.4f, StrokeCap.Round)
            drawPath(poligono(cabeza + Offset(-8.6f, 4f), cabeza + Offset(-3f, 7.2f), Offset(240f, 236f), Offset(196f, 236f)), Brush.verticalGradient(listOf(Color(0xFFFFE2B0).copy(alpha = 0.3f), Color(0xFFFFE2B0).copy(alpha = 0.03f)), 220f, 236f))
            mancha(Offset(214f, 233f), 30f, 4.4f, Color(0xFFFFE8C0), 0.4f)
        }
    }

    /** Lo que hay en el extremo de la mesa según lo que ella hace a esa hora. */
    private fun DrawScope.sobreLaMesa(m: Momento, a: Alumbrado) {
        val o = Offset(186f, 234f); val hoy = m.azar("mesa")
        fun taza(c: Offset, color: Color, k: Float = 1f) { val f = redondo(c.x - 3.2f * k, c.y - 7.4f * k, c.x + 3.2f * k, c.y, 1.2f * k); cuerpo(a, f, c.x - 3.2f * k, c.x + 3.2f * k, c.y - 4f, color); drawRect(a.en(c, C.GOMA), Offset(c.x - 3.4f * k, c.y - 8.6f * k), Size(6.8f * k, 1.6f * k)); drawArc(a.en(c, color), 270f, 180f, false, Offset(c.x + 1.6f * k, c.y - 6f * k), Size(4f * k, 4f * k), style = Stroke(0.9f * k)) }
        when (m.hacer) {
            Hacer.COMER -> {
                // La bandeja, el cuenco y la taza con tapa.
                drawPath(poligono(o + Offset(-12f, -3f), o + Offset(13f, -3f), o + Offset(15f, 1f), o + Offset(-14f, 1f)), a.en(o, lerp(C.ACERO, C.TELA, 0.4f))); drawLine(C.SOMBRA.copy(alpha = 0.3f), o + Offset(-14f, 1.2f), o + Offset(15f, 1.2f), 0.6f)
                val cuenco = Path().apply { moveTo(o.x - 9f, o.y - 6f); cubicTo(o.x - 8f, o.y - 0.6f, o.x + 2f, o.y - 0.6f, o.x + 3f, o.y - 6f); close() }
                cuerpo(a, cuenco, o.x - 9f, o.x + 3f, o.y - 3f, C.TELA); drawOval(a.en(o, C.TRIGO), Offset(o.x - 8.4f, o.y - 7.4f), Size(10.8f, 2.8f)); drawOval(a.en(o, C.VERDE), Offset(o.x - 5f, o.y - 7.6f), Size(4f, 1.8f))
                taza(o + Offset(9f, -1.6f), C.NARANJA, 0.9f)
            }
            Hacer.GENTE -> {
                // Las tazas de los que han venido y el tablero de fichas.
                val n = 2 + hoy.entero(2)
                for (i in 0 until n) taza(o + Offset(-10f + i * 8.6f + hoy.entre(-1f, 1f), -0.4f - (i % 2) * 1.6f), listOf(C.NARANJA, C.TELA_AZUL, C.TELA, C.VERDE)[(i + hoy.entero(4)) % 4], 0.86f)
                val t = Offset(238f, 233f); drawPath(poligono(t + Offset(-9f, -3.4f), t + Offset(9f, -3.4f), t + Offset(11f, 1.6f), t + Offset(-11f, 1.6f)), a.en(t, C.VERDE_OSC))
                for (i in 0..4) drawRect(a.en(t, if (i % 2 == 0) C.TELA else C.TRIGO), t + Offset(-8f + i * 3.6f, -2.4f + (i % 3) * 1f), Size(2.4f, 1.6f))
            }
            Hacer.TRABAJO -> {
                // El medidor, con sus dos puntas, y unas piezas sueltas.
                val med = redondo(o.x - 9f, o.y - 12f, o.x + 1f, o.y, 1.4f)
                cuerpo(a, med, o.x - 9f, o.x + 1f, o.y - 6f, C.TRIGO); drawRect(Color(0xFF16203A), Offset(o.x - 7.4f, o.y - 10.4f), Size(6.8f, 4f)); drawLine(C.CIAN.copy(alpha = 0.8f * E.pantallas(m)), Offset(o.x - 6.6f, o.y - 8f), Offset(o.x - 2f, o.y - 9f), 0.5f); drawCircle(a.en(o, C.GOMA), 1.6f, Offset(o.x - 4f, o.y - 3f))
                drawPath(Path().apply { moveTo(o.x - 1f, o.y - 2f); cubicTo(o.x + 6f, o.y - 9f, o.x + 9f, o.y + 3f, o.x + 14f, o.y - 1f) }, a.en(o, C.ROJO), style = Stroke(0.7f, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(o.x - 1f, o.y - 4f); cubicTo(o.x + 4f, o.y - 13f, o.x + 12f, o.y - 2f, o.x + 17f, o.y - 2.4f) }, a.en(o, C.GOMA), style = Stroke(0.7f, cap = StrokeCap.Round))
                for (i in 0..2) drawCircle(a.en(o, C.ACERO), 0.9f, Offset(240f + i * 3.4f + hoy.entre(-1f, 1f), 233f + (i % 2)))
            }
            Hacer.CUIDAR -> {
                // La toalla doblada y el frasco.
                drawRoundRect(a.en(o, C.TELA_AZUL), o + Offset(-12f, -4.6f), Size(18f, 4.6f), CornerRadius(1.4f)); drawLine(Color.White.copy(alpha = 0.3f), o + Offset(-11f, -2.4f), o + Offset(5f, -2.4f), 0.5f)
                val frasco = redondo(o.x + 9f, o.y - 9f, o.x + 14f, o.y, 1.4f); cuerpo(a, frasco, o.x + 9f, o.x + 14f, o.y - 4f, C.TELA); drawRect(a.en(o, C.VERDE), Offset(o.x + 9f, o.y - 6f), Size(5f, 2.6f)); drawRect(a.en(o, C.GOMA), Offset(o.x + 10f, o.y - 11f), Size(3f, 2f))
            }
            Hacer.LIBRE -> { taza(o + Offset(-4f, -0.6f), if (E.enLaVentana(m)) C.TELA_AZUL else C.NARANJA) ; if (E.ejercicio(m)) drawOval(a.en(o, C.NARANJA), o + Offset(4f, -3.4f), Size(9f, 3.4f), style = Stroke(1.2f)) }
            else -> Unit
        }
    }


    // ---------------------------------------------------------------- El taburete, el calefactor, las algas y los sacos

    private fun taburete(m: Momento, a: Alumbrado): Cosa {
        val pie = Offset(316f, 276f)
        val forma = poligono(listOf(pie + Offset(-11f, 0f), pie + Offset(-3f, -22f), pie + Offset(-11f, -24f), pie + Offset(-11f, -28f), pie + Offset(11f, -28f), pie + Offset(11f, -24f), pie + Offset(3f, -22f), pie + Offset(11f, 0f)))
        return Cosa(forma, pie) {
            for (dx in listOf(-10f, 0f, 10f)) drawLine(a.en(pie + Offset(dx, -2f), if (dx == 0f) lerp(C.ACERO, C.SOMBRA, 0.3f) else C.ACERO), pie + Offset(0f, -6f), pie + Offset(dx, if (dx == 0f) -2.4f else 0f), 1.8f, StrokeCap.Round)
            val columna = Path().apply { addRect(Rect(pie.x - 1.8f, pie.y - 24f, pie.x + 1.8f, pie.y - 5f)) }; cuerpo(a, columna, pie.x - 1.8f, pie.x + 1.8f, pie.y - 14f, C.ACERO)
            drawOval(a.en(pie + Offset(0f, -12f), C.ACERO), pie + Offset(-7f, -13f), Size(14f, 3.4f), style = Stroke(0.9f))
            val asiento = redondo(pie.x - 11f, pie.y - 28.4f, pie.x + 11f, pie.y - 23.6f, 2.4f)
            cuerpo(a, asiento, pie.x - 11f, pie.x + 11f, pie.y - 26f, C.NARANJA); drawLine(Color.White.copy(alpha = 0.3f), pie + Offset(-9f, -27.6f), pie + Offset(9f, -27.6f), 0.5f); drawLine(C.SOMBRA.copy(alpha = 0.3f), pie + Offset(-10f, -24.2f), pie + Offset(10f, -24.2f), 0.5f)
        }
    }

    /** El calefactor de la parada: una caja con su rejilla al rojo. */
    private fun calefactor(m: Momento, a: Alumbrado): Cosa {
        val pie = CALEFACTOR; val caja = redondo(pie.x - 13f, pie.y - 28f, pie.x + 13f, pie.y, 2.4f)
        return Cosa(caja, pie) {
            val encendido = E.calefactor(m)
            cuerpo(a, caja, pie.x - 13f, pie.x + 13f, pie.y - 14f, lerp(C.CASCO_OSC, C.GOMA, 0.3f))
            val rejilla = redondo(pie.x - 10f, pie.y - 23f, pie.x + 10f, pie.y - 6f, 1.4f)
            drawPath(rejilla, if (encendido) Brush.verticalGradient(listOf(Color(0xFFFFB25E), Color(0xFFE8562A)), pie.y - 23f, pie.y - 6f) else Brush.verticalGradient(listOf(Color(0xFF3A3238), Color(0xFF2A2630)), pie.y - 23f, pie.y - 6f))
            for (i in 0..5) drawLine(C.GOMA.copy(alpha = 0.85f), Offset(pie.x - 10f, pie.y - 21f + i * 2.8f), Offset(pie.x + 10f, pie.y - 21f + i * 2.8f), 1f)
            drawArc(a.en(pie + Offset(0f, -30f), C.ACERO), 180f, 180f, false, pie + Offset(-6f, -32f), Size(12f, 8f), style = Stroke(1.2f))
            drawPath(Path().apply { moveTo(pie.x + 13f, pie.y - 4f); cubicTo(pie.x + 24f, pie.y - 2f, pie.x + 20f, pie.y - 30f, pie.x + 34f, pie.y - 28f) }, a.en(pie + Offset(22f, -12f), C.NARANJA), style = Stroke(1f, cap = StrokeCap.Round))
            if (encendido) { resplandor(pie + Offset(0f, -14f), 26f, 0.5f, Color(0xFFFF8A4A)); mancha(pie + Offset(-8f, 2f), 26f, 4f, Color(0xFFFF9A5A), 0.3f) }
        }
    }

    /** El tanque de algas: un cilindro de vidrio con el caldo verde, que da su poco de luz; las tapas de acero, la llave y las mangueras. Es bajo, para no tapar el ventanal. */
    private fun algas(m: Momento, a: Alumbrado): Cosa {
        val pie = Offset(356f, 281f)
        val vidrio = redondo(342f, 200f, 370f, 272f, 5f); val forma = Path().apply { addPath(vidrio); addRect(Rect(340f, 270f, 372f, 281f)); addRect(Rect(341f, 192f, 371f, 202f)) }
        return Cosa(forma, pie) {
            // Las mangueras: salen de la tapa y bajan por detrás, hacia la pared.
            drawPath(Path().apply { moveTo(366f, 196f); cubicTo(376f, 194f, 380f, 206f, 378f, 226f); cubicTo(376f, 244f, 382f, 252f, 384f, 262f) }, a.en(Offset(376f, 220f), C.ALGA, -1f), style = Stroke(1.5f, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(350f, 194f); cubicTo(350f, 187f, 361f, 187f, 362f, 194f) }, a.en(Offset(356f, 190f), C.GOMA, -1f), style = Stroke(1.5f, cap = StrokeCap.Round))
            // El caldo: da luz propia, así que no se apaga de noche.
            val vivo = if (E.aOscuras(m)) 0.25f else 0.62f
            val claro = lerp(a.en(Offset(350f, 216f), C.ALGA, -1f), lerp(C.ALGA, Color.White, 0.24f), vivo); val hondo = lerp(a.en(Offset(356f, 260f), C.VERDE_OSC), C.VERDE, vivo * 0.6f)
            drawPath(vidrio, Brush.verticalGradient(listOf(claro, lerp(claro, hondo, 0.5f), hondo), 206f, 272f))
            clipPath(vidrio) {
                drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.22f), Color.Transparent, C.SOMBRA.copy(alpha = 0.3f)), 342f, 370f), Offset(342f, 200f), Size(28f, 72f))
                drawRect(a.en(Offset(356f, 202f), lerp(C.CASCO, C.SOMBRA, 0.2f)).copy(alpha = 0.55f), Offset(342f, 200f), Size(28f, 7f)); drawLine(Color.White.copy(alpha = 0.5f), Offset(342f, 207f), Offset(370f, 207f), 0.5f)
                val az = Azar(Semilla.de("espacio", "algas")); repeat(11) { mancha(Offset(az.entre(344f, 368f), az.entre(212f, 268f)), az.entre(3f, 7f), az.entre(4f, 10f), if (it % 2 == 0) C.VERDE_OSC else Color(0xFFD8F0A0), 0.16f) }
                drawLine(Color.White.copy(alpha = 0.4f), Offset(345.6f, 212f), Offset(345.6f, 262f), 1f)
                for (y in listOf(224f, 242f, 260f)) drawLine(Color.White.copy(alpha = 0.3f), Offset(364f, y), Offset(369f, y), 0.5f)
            }
            drawPath(vidrio, C.GOMA.copy(alpha = 0.5f), style = Stroke(0.6f))
            val tapa = redondo(340.6f, 192f, 371.4f, 202f, 2f); cuerpo(a, tapa, 340.6f, 371.4f, 197f, C.ACERO)
            val peana = redondo(339.6f, 269.6f, 372.4f, 281f, 2f); cuerpo(a, peana, 339.6f, 372.4f, 275f, lerp(C.ACERO, C.CASCO_OSC, 0.4f))
            drawCircle(a.en(Offset(348f, 275f), C.NARANJA), 3f, Offset(348f, 275.4f), style = Stroke(1.2f)); drawLine(a.en(Offset(348f, 275f), C.NARANJA), Offset(345f, 275.4f), Offset(351f, 275.4f), 0.8f)
            for (x in listOf(344f, 368f)) drawCircle(C.GOMA.copy(alpha = 0.7f), 0.8f, Offset(x, 197f))
            if (!E.aOscuras(m)) mancha(Offset(356f, 236f), 30f, 44f, C.ALGA, 0.05f + 0.1f * (1f - E.programa(m).nivel))
        }
    }

    /** La cosecha: dos sacos de grano y una gavilla de trigo enano. */
    private fun sacos(a: Alumbrado): Cosa {
        val pie = Offset(206f, 283f)   // se dibuja a la izquierda de la mesa, delante del cajón
        val s1 = Path().apply { moveTo(190f, 283f); cubicTo(184f, 270f, 188f, 258f, 198f, 257f); cubicTo(208f, 256f, 212f, 270f, 208f, 283f); close() }
        val s2 = Path().apply { moveTo(206f, 283f); cubicTo(204f, 272f, 210f, 264f, 218f, 264f); cubicTo(228f, 264f, 230f, 274f, 226f, 283f); close() }
        return Cosa(Path().apply { addPath(s1, Offset(-56f, -1f)); addPath(s2, Offset(-56f, -1f)) }, pie + Offset(-56f, -1f)) { translate(-56f, -1f) {
            cuerpo(a, s1, 186f, 211f, 270f, C.TELA); drawPath(poligono(Offset(194f, 258f), Offset(202f, 258f), Offset(204f, 252f), Offset(192f, 252.6f)), a.en(Offset(198f, 255f), lerp(C.TELA, C.SOMBRA, 0.2f))); drawLine(a.en(Offset(198f, 258f), C.NARANJA), Offset(193f, 258f), Offset(203f, 258f), 1f)
            clipPath(s1) { for (k in 0..3) drawLine(C.SOMBRA.copy(alpha = 0.14f), Offset(186f, 264f + k * 5f), Offset(212f, 263f + k * 5f), 0.5f) }
            cuerpo(a, s2, 205f, 229f, 274f, lerp(C.TELA, C.TRIGO, 0.3f)); drawOval(a.en(Offset(217f, 264f), C.TRIGO), Offset(210f, 262f), Size(14f, 4.4f))
            // La gavilla, apoyada en los sacos.
            for (i in 0..8) { val x = 226f + i * 1.3f; drawLine(a.en(Offset(x, 268f), if (i % 2 == 0) C.TRIGO else lerp(C.TRIGO, C.SOMBRA, 0.25f)), Offset(232f, 283f), Offset(x + i * 0.9f, 252f - (i % 3) * 2f), 0.8f, StrokeCap.Round); drawOval(a.en(Offset(x, 252f), lerp(C.TRIGO, Color.White, 0.2f)), Offset(x + i * 0.9f - 1f, 247f - (i % 3) * 2f), Size(2f, 6f)) }
            drawLine(a.en(Offset(232f, 270f), C.NARANJA), Offset(229f, 270f), Offset(236f, 269f), 1.1f)
        } }
    }

    // ---------------------------------------------------------------- Lo que no da sombra: adornos y cosas de una hora

    fun DrawScope.sueltas(m: Momento, a: Alumbrado) {
        // Su rato de ejercicio: las gomas, colgadas de la barra.
        if (E.ejercicio(m) && !E.sinPeso(m)) for (dx in listOf(-3f, 3f)) { val arriba = Offset(164f + dx * 0.4f, 100f); val abajo = Offset(164f + dx * 2.4f, 142f); drawLine(a.en(abajo, C.TELA_AZUL), arriba, abajo, 1.1f, StrokeCap.Round); drawRoundRect(a.en(abajo, C.GOMA), abajo + Offset(-2.6f, 0f), Size(5.2f, 3f), CornerRadius(1f)) }
        // El aseo: la toalla, echada sobre el canto del poyo.
        if (m.hacer == Hacer.CUIDAR) {
            val toalla = poligono(listOf(E.p(274f, 178f, 1.5f), E.p(274f, 178f, 1.63f), E.p(266.6f, 178f, 1.63f), E.p(266.6f, 194f, 1.62f), E.p(266.6f, 192f, 1.5f), E.p(266.6f, 178f, 1.5f)))
            drawPath(toalla, a.en(Offset(322f, 210f), C.TELA_AZUL, -1f)); drawLine(Color.White.copy(alpha = 0.3f), E.p(266.6f, 189f, 1.5f), E.p(266.6f, 191f, 1.62f), 0.8f)
        }
        if (E.lamparitas(m)) {
            // La fiesta de las luces: una lámpara pequeña por cada uno que se quedó en la Tierra, por todas partes.
            for ((i, q) in LAMPARITAS.withIndex()) { drawRect(a.en(q, C.ACERO), q + Offset(-1.3f, 0f), Size(2.6f, 1.6f)); drawOval(Color(0xFFFFE8B8), q + Offset(-1.6f, -4f), Size(3.2f, 4.2f)); resplandor(q + Offset(0f, -2f), 8f + (i % 3), 0.5f, Color(0xFFFFC070)) }
        }
        if (E.luto(m)) {
            // Un vaso con una rama de la granja, junto a la pantalla.
            val o = Offset(240f, 117.6f)
            val vaso = redondo(o.x - 2.4f, o.y - 6f, o.x + 2.4f, o.y, 0.8f); cuerpo(a, vaso, o.x - 2.4f, o.x + 2.4f, o.y - 3f, C.TELA)
            drawLine(a.en(o, C.VERDE_OSC), o + Offset(0f, -5f), o + Offset(1.6f, -15f), 0.7f); for (k in 0..3) drawOval(a.en(o, C.VERDE), o + Offset(-1f + (k % 2) * 3f, -9f - k * 2f), Size(3f, 1.6f)); drawCircle(a.en(o, C.TELA), 1.5f, o + Offset(1.8f, -16f))
        }
    }

    private val LAMPARITAS = listOf(
        Offset(76f, 214.6f), Offset(98f, 214.6f), Offset(130f, 214.6f), Offset(176f, 213f), Offset(204f, 213f), Offset(262f, 213f), Offset(70f, 204f), Offset(176f, 117.4f), Offset(196f, 117.4f), Offset(232f, 117.4f),
        Offset(190f, 206f), Offset(246f, 206f), Offset(300f, 226f), Offset(322f, 240f), Offset(58f, 268f), Offset(186f, 281f), Offset(298f, 187.6f), Offset(331f, 205f), Offset(262f, 87f), Offset(275f, 87f),
    )

    /** El temblor de las lamparitas de la fiesta de las luces. */
    fun DrawScope.lamparitasVivas(reloj: Float) {
        for ((i, q) in LAMPARITAS.withIndex()) resplandor(q + Offset(0f, -2f), 5f, 0.16f + 0.14f * sin(reloj * (2f + (i % 5) * 0.6f) + i * 1.3f), Color(0xFFFFD890))
    }

    // ---------------------------------------------------------------- Sin peso

    /** Los días sin peso: lo que no está sujeto flota por el módulo, cada día en otro sitio; y el objeto del día, atado a la mesa con su cuerda. */
    fun DrawScope.flotando(m: Momento, a: Alumbrado) {
        val hoy = m.azar("flota")
        val cosas: List<DrawScope.(Offset) -> Unit> = listOf(
            { o -> drawLine(a.en(o, C.ACERO), o + Offset(-8f, 0f), o + Offset(6f, 0f), 2f, StrokeCap.Round); drawCircle(a.en(o, C.ACERO), 3f, o + Offset(8f, 0f), style = Stroke(1.6f)) },                                    // una llave
            { o -> drawLine(a.en(o, C.TRIGO), o + Offset(-7f, 0f), o + Offset(6f, 0f), 1.4f, StrokeCap.Round); drawLine(a.en(o, C.GOMA), o + Offset(6f, 0f), o + Offset(8f, 0f), 1f, StrokeCap.Round) },                           // un lápiz
            { o -> val f = redondo(o.x - 4f, o.y - 5f, o.x + 4f, o.y + 5f, 1.4f); cuerpo(a, f, o.x - 4f, o.x + 4f, o.y, C.NARANJA); drawRect(a.en(o, C.GOMA), Offset(o.x - 4.4f, o.y - 6.6f), Size(8.8f, 2f)) },                   // la taza con tapa
            { o -> drawPath(Path().apply { moveTo(o.x - 6f, o.y - 3f); cubicTo(o.x, o.y - 5f, o.x + 4f, o.y - 1f, o.x + 3f, o.y + 6f); lineTo(o.x - 1f, o.y + 6f); cubicTo(o.x - 1f, o.y + 1f, o.x - 3f, o.y, o.x - 6f, o.y + 1f); close() }, a.en(o, C.TELA_AZUL)) },   // un calcetín
            { o -> val f = redondo(o.x - 6f, o.y - 4f, o.x + 6f, o.y + 4f, 1f); cuerpo(a, f, o.x - 6f, o.x + 6f, o.y, C.ACERO); drawRect(a.en(o, C.TRIGO), Offset(o.x - 3f, o.y - 2f), Size(6f, 4f)); drawRect(a.en(o, C.TELA), Offset(o.x + 6f, o.y - 1f), Size(2.4f, 2f)) },   // una bolsa de comida
            { o -> drawLine(a.en(o, C.NARANJA), o + Offset(-6f, 0f), o + Offset(0f, 0f), 2.2f, StrokeCap.Round); drawLine(a.en(o, C.ACERO), o, o + Offset(7f, 0f), 0.9f, StrokeCap.Round) },                                       // un destornillador
            { o -> drawOval(a.en(o, C.TELA), Offset(o.x - 5f, o.y - 2.4f), Size(7f, 4.8f)); drawLine(a.en(o, C.ACERO), o + Offset(1f, 0f), o + Offset(9f, 0.6f), 0.9f, StrokeCap.Round) },                                           // una cuchara
            { o -> drawOval(a.en(o, C.VERDE), Offset(o.x - 4f, o.y - 2f), Size(8f, 4f)); drawLine(a.en(o, C.VERDE_OSC), o + Offset(-4f, 0f), o + Offset(4f, 0f), 0.4f) },                                                            // una hoja suelta
        )
        val sitios = listOf(Offset(96f, 150f), Offset(214f, 166f), Offset(258f, 138f), Offset(136f, 216f), Offset(300f, 200f), Offset(60f, 196f), Offset(238f, 196f), Offset(196f, 142f))
        for ((i, dibuja) in cosas.withIndex()) {
            val o = sitios[(i + hoy.entero(8)) % 8] + Offset(hoy.entre(-12f, 12f), hoy.entre(-9f, 9f))
            rotate(hoy.entre(-70f, 70f), o) { scale(1.7f, 1.7f, o) { dibuja(o) } }
        }
        // Una toalla que se quedó a medio doblar y una bota, que nadie ha ido a buscar.
        val t = Offset(118f, 124f) + Offset(hoy.entre(-10f, 10f), hoy.entre(-8f, 8f))
        rotate(hoy.entre(-30f, 30f), t) {
            val toalla = Path().apply { moveTo(t.x - 14f, t.y - 5f); cubicTo(t.x - 5f, t.y - 11f, t.x + 4f, t.y - 1f, t.x + 14f, t.y - 7f); cubicTo(t.x + 17f, t.y, t.x + 12f, t.y + 6f, t.x + 15f, t.y + 10f); cubicTo(t.x + 5f, t.y + 14f, t.x - 4f, t.y + 5f, t.x - 13f, t.y + 9f); cubicTo(t.x - 16f, t.y + 3f, t.x - 11f, t.y - 1f, t.x - 14f, t.y - 5f); close() }
            drawPath(toalla, Brush.verticalGradient(listOf(a.en(t, lerp(C.TELA_AZUL, Color.White, 0.2f)), a.en(t, lerp(C.TELA_AZUL, C.SOMBRA, 0.25f))), t.y - 10f, t.y + 12f))
            clipPath(toalla) { for (k in -1..1) drawPath(Path().apply { moveTo(t.x - 16f, t.y + k * 6f); cubicTo(t.x - 5f, t.y - 6f + k * 6f, t.x + 4f, t.y + 5f + k * 6f, t.x + 16f, t.y - 2f + k * 6f) }, Color.White.copy(alpha = 0.22f), style = Stroke(0.8f)) }
        }
        val b = Offset(286f, 150f) + Offset(hoy.entre(-10f, 10f), hoy.entre(-8f, 8f))
        rotate(hoy.entre(100f, 200f), b) {
            val bota = Path().apply { moveTo(b.x - 5f, b.y - 12f); lineTo(b.x + 4f, b.y - 12f); lineTo(b.x + 4.6f, b.y + 2f); cubicTo(b.x + 12f, b.y + 3f, b.x + 13f, b.y + 8f, b.x + 12f, b.y + 9f); lineTo(b.x - 5.6f, b.y + 9f); close() }
            cuerpo(a, bota, b.x - 6f, b.x + 13f, b.y, C.CASCO_OSC); drawRect(a.en(b, C.NARANJA), Offset(b.x - 5.4f, b.y - 9f), Size(9.6f, 2f)); drawRect(a.en(b, C.GOMA), Offset(b.x - 5.6f, b.y + 7.4f), Size(18f, 1.8f))
        }
        // El objeto del día: flota sobre la mesa, sujeto por una cuerda.
        if (m.ficha.objeto != null) {
            val ancla = SOBRE_EN_EL_CUADRO
            val o = Offset(ancla.x + hoy.entre(-8f, 8f), ancla.y - 26f - hoy.entre(0f, 10f))
            drawPath(Path().apply { moveTo(ancla.x, ancla.y - 3f); cubicTo(ancla.x - 6f, ancla.y - 11f, o.x + 8f, o.y + 10f, o.x, o.y) }, a.en(o, C.NARANJA), style = Stroke(0.7f, cap = StrokeCap.Round))
            drawRect(a.en(ancla, C.ACERO), Offset(ancla.x - 2f, ancla.y - 4.6f), Size(4f, 3f))
            rotate(hoy.entre(-18f, 18f), o) { scale(E.K_MESA * K_OBJETO, E.K_MESA * K_OBJETO, o) { objeto(m, a, o + Offset(0f, 6f)) } }
        }
    }

    // ---------------------------------------------------------------- Los doce objetos del día

    /** El objeto del día, apoyado en [s]. */
    fun DrawScope.objeto(m: Momento, a: Alumbrado, s: Offset) {
        val noche = 1f - E.programa(m).nivel
        when (m.ficha.objeto) {
            "tableta" -> {
                // Apoyada en su pie, con la pantalla encendida.
                drawPath(poligono(s + Offset(4f, 0f), s + Offset(9f, 0f), s + Offset(3f, -9f)), a.en(s, C.ACERO))
                val cuerpoT = poligono(s + Offset(-12f, -1f), s + Offset(8f, -1f), s + Offset(11f, -17f), s + Offset(-9f, -17f))
                drawPath(cuerpoT, a.en(s + Offset(0f, -9f), C.GOMA)); val vidrio = poligono(s + Offset(-10f, -2.6f), s + Offset(6.6f, -2.6f), s + Offset(9.2f, -15.4f), s + Offset(-7.4f, -15.4f))
                drawPath(vidrio, lerp(Color(0xFF12203A), C.CIAN, 0.45f)); clipPath(vidrio) { for (k in 0..3) drawLine(Color.White.copy(alpha = 0.6f), s + Offset(-7f + k * 0.5f, -5f - k * 2.8f), s + Offset(-7f + k * 0.5f + 5f + (k * 37 % 7), -5f - k * 2.8f), 0.7f); drawCircle(C.AMBAR, 1.6f, s + Offset(5f, -11f)) }
                mancha(s + Offset(0f, -9f), 14f, 10f, C.CIAN, 0.1f + 0.14f * noche)
            }
            "llave" -> {
                // La llave inglesa, tumbada: el mango, la cabeza con su boca y el tornillo.
                drawLine(a.en(s, lerp(C.ACERO, C.SOMBRA, 0.3f)), s + Offset(-13f, -1.4f), s + Offset(8f, -2.6f), 3.4f, StrokeCap.Round); drawLine(a.en(s, lerp(C.ACERO, Color.White, 0.3f)), s + Offset(-12f, -2.4f), s + Offset(7f, -3.6f), 0.8f, StrokeCap.Round)
                val cabeza = poligono(listOf(s + Offset(6f, -6.4f), s + Offset(14f, -8f), s + Offset(15f, -5f), s + Offset(11f, -4f), s + Offset(11.6f, -1.4f), s + Offset(15.6f, -1.4f), s + Offset(15f, 0f), s + Offset(7f, 0f)))
                drawPath(cabeza, a.en(s + Offset(10f, -4f), C.ACERO)); drawCircle(a.en(s, C.GOMA), 1.1f, s + Offset(8.6f, -2.8f)); drawCircle(a.en(s, C.GOMA), 1f, s + Offset(-11f, -1.8f), style = Stroke(0.6f))
            }
            "botella" -> {
                val f = redondo(s.x - 4.4f, s.y - 19f, s.x + 4.4f, s.y, 2.4f)
                drawPath(f, Brush.horizontalGradient(listOf(a.en(s, Color(0xFFBFE0F0), -1f), a.en(s, Color(0xFF6AA8D0)), a.en(s, Color(0xFFBFE0F0), 1f)), s.x - 4.4f, s.x + 4.4f))
                clipPath(f) { drawRect(Color.White.copy(alpha = 0.3f), Offset(s.x - 4.4f, s.y - 19f), Size(8.8f, 5.6f)); drawLine(Color.White.copy(alpha = 0.6f), s + Offset(-2.6f, -17f), s + Offset(-2.6f, -3f), 0.7f) }
                drawRect(a.en(s, C.NARANJA), Offset(s.x - 3f, s.y - 23f), Size(6f, 4.4f)); drawRect(a.en(s, C.GOMA), Offset(s.x - 1.2f, s.y - 25.4f), Size(2.4f, 2.6f)); drawRect(a.en(s, C.TELA_AZUL), Offset(s.x - 4.4f, s.y - 11f), Size(8.8f, 3f))
            }
            "foto" -> {
                // Una foto de papel, sujeta en una pinza: el mar y el cielo de la Tierra, sin nadie.
                drawRect(a.en(s, C.ACERO), Offset(s.x - 3f, s.y - 2f), Size(6f, 2f)); drawLine(a.en(s, C.ACERO), s + Offset(0f, -2f), s + Offset(0f, -5f), 0.8f)
                val papel = poligono(s + Offset(-9f, -4f), s + Offset(8.6f, -4.6f), s + Offset(9.4f, -18f), s + Offset(-8.4f, -17.2f))
                drawPath(papel, a.en(s + Offset(0f, -11f), C.TELA)); val vista = poligono(s + Offset(-7.4f, -5.6f), s + Offset(7.2f, -6.2f), s + Offset(7.8f, -16.4f), s + Offset(-6.8f, -15.8f))
                clipPath(vista) { drawRect(a.en(s, Color(0xFFBFD8E8)), s + Offset(-9f, -18f), Size(20f, 7f)); drawRect(a.en(s, Color(0xFF4E8AA0)), s + Offset(-9f, -11f), Size(20f, 3f)); drawRect(a.en(s, C.TRIGO), s + Offset(-9f, -8.2f), Size(20f, 4f)); drawCircle(a.en(s, Color(0xFFFFF0C0)), 1.5f, s + Offset(3.6f, -13.6f)) }
                drawPath(papel, C.SOMBRA.copy(alpha = 0.3f), style = Stroke(0.4f))
            }
            "arnes" -> {
                // El arnés: las cinchas hechas un montón, con dos mosquetones; una cuelga por el canto de la mesa.
                val cincha = a.en(s, C.NARANJA)
                drawPath(Path().apply { moveTo(s.x - 12f, s.y - 1f); cubicTo(s.x - 14f, s.y - 12f, s.x - 2f, s.y - 13f, s.x - 3f, s.y - 2f); cubicTo(s.x - 2f, s.y - 10f, s.x + 10f, s.y - 12f, s.x + 10f, s.y - 2f) }, cincha, style = Stroke(2.6f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawPath(Path().apply { moveTo(s.x + 6f, s.y - 3f); cubicTo(s.x + 14f, s.y - 3f, s.x + 13f, s.y + 6f, s.x + 15f, s.y + 16f) }, lerp(cincha, C.SOMBRA, 0.2f), style = Stroke(2.4f, cap = StrokeCap.Round))
                for (q in listOf(s + Offset(-7f, -1.6f), s + Offset(15f, 17f))) { drawOval(a.en(q, C.ACERO), q + Offset(-2.6f, -3.4f), Size(5.2f, 6.8f), style = Stroke(1.1f)); drawLine(a.en(q, C.TELA_AZUL), q + Offset(2.4f, -2f), q + Offset(2.4f, 2f), 0.9f) }
                drawRect(a.en(s, C.ACERO), Offset(s.x + 2f, s.y - 6f), Size(4.4f, 3.4f)); drawLine(Color.White.copy(alpha = 0.3f), s + Offset(-11f, -6f), s + Offset(-5f, -10.4f), 0.5f)
            }
            "lampara" -> {
                // La lámpara de frente: la cinta, en aro, y el foco delante. De noche luce.
                drawOval(a.en(s, C.TELA_AZUL), Offset(s.x - 11f, s.y - 9f), Size(20f, 8.6f), style = Stroke(2.4f)); drawOval(Color.White.copy(alpha = 0.2f), Offset(s.x - 10f, s.y - 9.4f), Size(18f, 7f), style = Stroke(0.4f))
                val foco = redondo(s.x + 4f, s.y - 10f, s.x + 13f, s.y - 1f, 2f); cuerpo(a, foco, s.x + 4f, s.x + 13f, s.y - 5f, C.ACERO)
                drawCircle(lerp(Color(0xFFD8D4C0), Color(0xFFFFF4D0), noche), 2.8f, s + Offset(8.6f, -5.4f)); drawCircle(a.en(s, C.GOMA), 2.8f, s + Offset(8.6f, -5.4f), style = Stroke(0.6f))
                if (noche > 0.3f) { resplandor(s + Offset(8.6f, -5.4f), 11f, 0.6f * noche, Color(0xFFFFE8B8)); mancha(s + Offset(22f, -1f), 16f, 2.4f, Color(0xFFFFE8B8), 0.3f * noche) }
            }
            "auriculares" -> {
                // El auricular, con su cable enrollado y su estuche abierto.
                val estuche = redondo(s.x - 13f, s.y - 6f, s.x - 1f, s.y, 2f); cuerpo(a, estuche, s.x - 13f, s.x - 1f, s.y - 3f, C.GOMA)
                drawPath(poligono(s + Offset(-13f, -6f), s + Offset(-1f, -6f), s + Offset(0.6f, -13f), s + Offset(-11.4f, -13f)), a.en(s + Offset(-6f, -10f), lerp(C.GOMA, C.CASCO_OSC, 0.5f))); drawOval(a.en(s, C.NARANJA), Offset(s.x - 9.6f, s.y - 11.6f), Size(6f, 3.6f))
                drawPath(Path().apply { moveTo(s.x - 1f, s.y - 2f); cubicTo(s.x + 3f, s.y - 6f, s.x + 5f, s.y + 1f, s.x + 8f, s.y - 3f); cubicTo(s.x + 10f, s.y - 6f, s.x + 6f, s.y - 9f, s.x + 10f, s.y - 9f) }, a.en(s, C.TELA), style = Stroke(0.7f, cap = StrokeCap.Round))
                drawOval(a.en(s, C.TELA), Offset(s.x + 8.6f, s.y - 12f), Size(5.4f, 6.6f)); drawOval(a.en(s, C.GOMA), Offset(s.x + 11.6f, s.y - 10.6f), Size(3f, 3.8f)); drawCircle(C.CIAN, 0.6f, s + Offset(10.4f, -10.4f))
            }
            "semilla" -> {
                // Una semilla, en su platillo, y el sobre de papel del que salió.
                val sobre = poligono(s + Offset(1f, -1f), s + Offset(13f, -1.6f), s + Offset(13.6f, -15f), s + Offset(1.6f, -14.4f))
                drawPath(sobre, a.en(s + Offset(7f, -8f), C.TELA)); drawPath(poligono(s + Offset(1.6f, -14.4f), s + Offset(13.6f, -15f), s + Offset(7.6f, -9.6f)), a.en(s + Offset(7f, -12f), lerp(C.TELA, C.SOMBRA, 0.16f))); drawRect(a.en(s, C.VERDE), s + Offset(5f, -7f), Size(5f, 3.6f))
                drawOval(a.en(s, lerp(C.TELA, Color.White, 0.3f)), Offset(s.x - 13f, s.y - 3.4f), Size(13f, 3.6f)); drawOval(a.en(s, C.CASCO_OSC), Offset(s.x - 13f, s.y - 3.4f), Size(13f, 3.6f), style = Stroke(0.4f))
                drawOval(a.en(s, Color(0xFF8A5A30)), Offset(s.x - 8.6f, s.y - 5.6f), Size(4.6f, 3.4f)); drawOval(a.en(s, C.TRIGO).copy(alpha = 0.8f), Offset(s.x - 8f, s.y - 5.4f), Size(2f, 1.2f))
            }
            "traje" -> {
                // El casco del traje de salida, con los guantes al lado.
                val c = s + Offset(-2f, -11f)
                drawRect(a.en(s, C.ACERO), Offset(c.x - 7.6f, s.y - 2.6f), Size(15.2f, 2.6f))
                drawCircle(a.en(c, C.TELA), 11f, c); drawCircle(a.en(c, lerp(C.TELA, C.SOMBRA, 0.3f)), 11f, c, style = Stroke(0.6f))
                drawOval(Brush.horizontalGradient(listOf(a.en(c, Color(0xFF8A6A2E), -1f), a.en(c, C.AMBAR), a.en(c, Color(0xFF5A4424), 1f)), c.x - 8f, c.x + 8f), c + Offset(-7.6f, -6.4f), Size(15.2f, 12f)); drawArc(Color.White.copy(alpha = 0.5f), 200f, 60f, false, c + Offset(-6f, -5f), Size(10f, 8f), style = Stroke(0.7f))
                for (dx in listOf(11f, 15.6f)) { val g = redondo(s.x + dx - 2.6f, s.y - 7f, s.x + dx + 2.6f, s.y, 1.6f); cuerpo(a, g, s.x + dx - 2.6f, s.x + dx + 2.6f, s.y - 3f, C.TELA); drawRect(a.en(s, C.NARANJA), Offset(s.x + dx - 2.6f, s.y - 2.4f), Size(5.2f, 1.6f)) }
            }
            "taza" -> {
                // La taza con tapa, grande, con su asa y la boquilla.
                val f = redondo(s.x - 6.4f, s.y - 14f, s.x + 6.4f, s.y, 2f); cuerpo(a, f, s.x - 6.4f, s.x + 6.4f, s.y - 7f, C.NARANJA)
                drawArc(a.en(s, C.NARANJA), 270f, 180f, false, Offset(s.x + 3.6f, s.y - 11.6f), Size(8f, 8f), style = Stroke(1.6f))
                drawRoundRect(a.en(s, C.GOMA), Offset(s.x - 7f, s.y - 16.6f), Size(14f, 3.4f), CornerRadius(1.2f)); drawRect(a.en(s, C.GOMA), Offset(s.x - 4f, s.y - 19f), Size(3f, 3f)); drawRect(a.en(s, C.TELA), Offset(s.x - 6.4f, s.y - 9f), Size(12.8f, 2f))
            }
            "cuaderno" -> {
                // El cuaderno de papel, abierto, con sus renglones y el lápiz encima.
                val izq = poligono(s + Offset(-14f, -1f), s + Offset(-1f, -0.4f), s + Offset(0f, -6.6f), s + Offset(-12f, -7.4f)); val der = poligono(s + Offset(-1f, -0.4f), s + Offset(13f, -1.2f), s + Offset(12f, -7.6f), s + Offset(0f, -6.6f))
                drawPath(poligono(s + Offset(-14.6f, 0f), s + Offset(13.6f, -0.2f), s + Offset(13f, -1.2f), s + Offset(-14f, -1f)), a.en(s, C.TELA_AZUL))
                drawPath(izq, a.en(s + Offset(-7f, -4f), lerp(C.TELA, Color.White, 0.3f))); drawPath(der, a.en(s + Offset(7f, -4f), lerp(C.TELA, Color.White, 0.14f)))
                for (k in 0..3) { drawLine(C.TELA_AZUL.copy(alpha = 0.5f), s + Offset(-11.4f + k * 0.3f, -6f + k * 1.3f), s + Offset(-2.4f, -5.4f + k * 1.3f), 0.35f); if (k < 2) drawLine(C.TELA_AZUL.copy(alpha = 0.5f), s + Offset(2f, -5.6f + k * 1.3f), s + Offset(10f + k, -6.2f + k * 1.3f), 0.35f) }
                drawLine(a.en(s, C.TRIGO), s + Offset(2f, -2.4f), s + Offset(13f, -5f), 1.2f, StrokeCap.Round); drawLine(a.en(s, C.GOMA), s + Offset(13f, -5f), s + Offset(14.4f, -5.4f), 0.9f, StrokeCap.Round)
            }
            "planta" -> {
                // Una planta en un vaso de vidrio: el agua, las raíces, el tallo y sus hojas.
                val vaso = poligono(s + Offset(-5f, 0f), s + Offset(5f, 0f), s + Offset(6f, -12f), s + Offset(-6f, -12f))
                drawPath(vaso, a.en(s, Color(0xFFBFE0F0)).copy(alpha = 0.55f)); drawPath(poligono(s + Offset(-5f, 0f), s + Offset(5f, 0f), s + Offset(5.6f, -7f), s + Offset(-5.6f, -7f)), a.en(s, Color(0xFF7AB8D8)).copy(alpha = 0.6f))
                for (dx in listOf(-2f, 0.6f, 2.6f)) drawLine(a.en(s, C.TELA).copy(alpha = 0.8f), s + Offset(0f, -7f), s + Offset(dx, -1.4f), 0.4f)
                drawPath(vaso, Color.White.copy(alpha = 0.4f), style = Stroke(0.5f)); drawLine(Color.White.copy(alpha = 0.6f), s + Offset(-4f, -10f), s + Offset(-3.6f, -2f), 0.6f)
                drawPath(Path().apply { moveTo(s.x, s.y - 7f); cubicTo(s.x - 1f, s.y - 16f, s.x + 3f, s.y - 20f, s.x + 1f, s.y - 28f) }, a.en(s + Offset(0f, -18f), C.VERDE_OSC), style = Stroke(0.9f, cap = StrokeCap.Round))
                for ((k, q) in listOf(Offset(-6f, -17f), Offset(5f, -20f), Offset(-5f, -24f), Offset(5f, -27f), Offset(0f, -31f)).withIndex()) { val c = s + q; drawOval(a.en(c, if (k % 2 == 0) C.VERDE else lerp(C.VERDE, Color(0xFFE8F0A8), 0.4f)), c + Offset(-4f, -2f), Size(8f, 4.4f)); drawLine(a.en(c, C.VERDE_OSC).copy(alpha = 0.6f), c + Offset(-3f, 0.2f), c + Offset(3f, 0.2f), 0.3f) }
            }
            else -> Unit
        }
    }
}
