package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerpo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoH
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.sin

/**
 * Lo que hay en el taller: en la pared del fondo, el estante de los botes y los cartones clavados; a la derecha, el
 * caballete grande con la tabla del maestro; a la izquierda, el rincón de Iacopo; y delante, la mesa de moler con la
 * losa de pórfido, las conchas de color y el objeto del día, la banqueta, el hornillo de la cola, el arcón con las
 * tablas enyesadas detrás, el jergón y la escoba.
 *
 * **[Diseño propio]**: el cuento nombra la mesa de moler, la losa de pórfido, los caballetes, las tablas, los cartones,
 * el jergón detrás de la mesa, la lámpara de aceite, el brasero y el fuego de la cola; dónde está cada cosa es mío.
 */
internal object ObraRenacimiento {
    private val C = ColorRenacimiento
    private val R get() = EscenaRenacimiento
    private val T get() = TallerRenacimiento

    /** La llama de la lámpara de aceite de la mesa y la lumbre del hornillo. */
    val LUZ_LAMPARA = Offset(97f, 165.6f)
    val LUZ_LUMBRE = Offset(270f, 225.5f)

    /** Por dónde camina el gato: por delante de todo. */
    const val PISO_GATO = 271f

    /** Dónde se apoya el objeto del día, en el extremo de la mesa de moler. */
    val MESA = Offset(192f, 186.8f)

    /** La tabla grande, en su caballete, y la de Iacopo, en el suyo. */
    val TABLA_GRANDE = Rect(282f, 46f, 350f, 141f)
    val TABLA_CHICA = Rect(63f, 106f, 111f, 172f)

    private const val BANQUETA = 228f
    private const val HORNILLO = 270f

    /** El hornillo y la banqueta se dibujan a su medida y se agrandan y se acercan a quien mira: así pesan en el primer plano. */
    private const val CRECE_HORNILLO = 1.5f
    private const val CRECE_BANQUETA = 1.4f
    private val PIE_HORNILLO = Offset(HORNILLO, 249f)
    private val PIE_BANQUETA = Offset(BANQUETA, 257f)

    /** Dónde queda, ya agrandado, un punto del hornillo dibujado a su medida (con el pie en 262, 236). */
    private fun enHornillo(p: Offset): Offset = PIE_HORNILLO + (p - Offset(262f, 236f)) * CRECE_HORNILLO

    // ---------------------------------------------------------------- En la pared del fondo

    fun DrawScope.enLaPared(m: Momento, a: Alumbrado) {
        banco(m, a)
        // El estante de los botes de color, sobre dos palomillas.
        val tabla = a.en(Offset(106f, 70f), lerp(C.MADERA, C.VIGA, 0.3f))
        for (x in listOf(88f, 124f)) drawPath(poligono(Offset(x, 70f), Offset(x + 1.6f, 70f), Offset(x + 1.6f, 77f)), a.en(Offset(x, 73f), C.VIGA))
        val az = Azar(Semilla.de("renacimiento", "estante"))
        for ((k, color) in listOf(C.ULTRAMAR, C.OCRE, C.CINABRIO, C.VERDE, C.YESO, C.CARBON).withIndex()) {
            val x = 84.6f + k * 6.6f; val h = az.entre(6f, 9.4f); val o = Offset(x + 2.4f, 66f)
            val bote = Path().apply { moveTo(x + 0.6f, 69.4f); cubicTo(x - 0.8f, 69.4f - h * 0.5f, x + 0.2f, 69.4f - h, x + 1.2f, 69.4f - h); lineTo(x + 3.8f, 69.4f - h); cubicTo(x + 4.8f, 69.4f - h, x + 5.8f, 69.4f - h * 0.5f, x + 4.4f, 69.4f); close() }
            cuerpo(a, bote, x - 0.4f, x + 5.4f, 66f, if (k % 2 == 0) C.COTTO else lerp(C.COTTO, C.MURO, 0.45f))
            // Lo que el bote lleva dentro se ve en lo que ha chorreado por fuera y en la tapa de trapo.
            drawRect(a.en(o, color), Offset(x + 0.8f, 69.4f - h - 1.2f), Size(3.4f, 1.4f)); drawLine(a.en(o, color).copy(alpha = 0.8f), Offset(x + 2f + (k % 2), 69.4f - h), Offset(x + 2.2f + (k % 2), 69.4f - h * az.entre(0.3f, 0.6f)), 0.8f, StrokeCap.Round)
        }
        // Un vaciado de yeso de una mano, de los que se copian.
        drawPath(Path().apply { moveTo(124.6f, 69.4f); lineTo(124.2f, 63.6f); lineTo(125.4f, 60.4f); lineTo(126.4f, 63.4f); lineTo(127f, 59.6f); lineTo(128f, 63.4f); lineTo(128.8f, 60.6f); lineTo(129.4f, 64f); lineTo(130.6f, 62.6f); lineTo(130.2f, 69.4f); close() }, a.en(Offset(127f, 64f), C.YESO, 1f))
        drawRect(tabla, Offset(81f, 69.4f), Size(51f, 2.2f)); drawLine(a.en(Offset(106f, 70f), lerp(C.MADERA, C.YESO, 0.3f)).copy(alpha = 0.6f), Offset(81f, 69.4f), Offset(132f, 69.4f), 0.5f)
        drawRect(C.SOMBRA.copy(alpha = 0.26f), Offset(81f, 71.6f), Size(51f, 2.2f), blendMode = BlendMode.Multiply)
        // Los cartones, clavados debajo.
        val tono: (Color) -> Color = { a.en(Offset(104f, 98f), it) }
        fun clavado(caja: Rect, cual: Int) {
            drawRect(C.SOMBRA.copy(alpha = 0.3f), caja.topLeft + Offset(1.2f, 1.4f), caja.size, blendMode = BlendMode.Multiply)
            with(PinturasRenacimiento) { carton(caja, cual, tono) }
            for (x in listOf(caja.left + 1.6f, caja.right - 1.6f)) drawCircle(a.en(caja.topLeft, C.CARBON), 0.6f, Offset(x, caja.top + 1.4f))
        }
        when (R.cartones(m)) {
            3 -> { clavado(Rect(81f, 77f, 103f, 113f), 0); clavado(Rect(107f, 78.4f, 130f, 115f), 1); clavado(Rect(93f, 87f, 116f, 116f), 2) }
            2 -> { clavado(Rect(81f, 77f, 103f, 113f), 0); clavado(Rect(107f, 78.4f, 130f, 115f), 1) }
            1 -> clavado(Rect(86f, 77f, 124f, 115f), 3 + m.ficha.temporada % 2)
            else -> for (x in listOf(84f, 101f, 109f, 128f)) drawCircle(a.en(Offset(x, 79f), C.CARBON).copy(alpha = 0.6f), 0.5f, Offset(x, 79f))
        }
        // Los cartones de la capilla, ya enrollados, de pie en el rincón.
        if (R.cartonesEnrollados(m) && !R.enElCampo(m)) for ((k, x) in listOf(79.4f, 83f, 86.4f).withIndex()) {
            val rollo = poligono(Offset(x - 1.6f, 183f), Offset(x + 1.6f, 183f), Offset(x + 2.6f + k, 138f - k * 5f), Offset(x - 0.4f + k, 138f - k * 5f))
            cuerpo(a, rollo, x - 1.6f, x + 3f, 160f, C.PAPEL); drawOval(a.en(Offset(x, 138f), lerp(C.PAPEL, C.VIGA, 0.4f)), Offset(x - 0.4f + k, 137.2f - k * 5f), Size(3f, 1.4f))
        }
        // La cinta azul de Caterina, atada con un lazo al extremo del estante, donde él la ve al moler: cuelga larga.
        if (R.cinta(m)) { val o = Offset(128f, 73f); val azul = a.en(o + Offset(0f, 12f), lerp(C.ULTRAMAR, C.YESO, 0.12f)); val claro = a.en(o + Offset(0f, 12f), lerp(C.ULTRAMAR, C.YESO, 0.5f))
            drawPath(Path().apply { moveTo(o.x + 1.4f, o.y + 1f); cubicTo(o.x + 4.6f, o.y + 9f, o.x - 1f, o.y + 16f, o.x + 3f, o.y + 27f) }, C.SOMBRA.copy(alpha = 0.3f), style = Stroke(2.4f, cap = StrokeCap.Round), blendMode = BlendMode.Multiply)
            val caidas = Path().apply { moveTo(o.x, o.y); cubicTo(o.x - 4f, o.y + 8f, o.x + 2.6f, o.y + 15f, o.x - 2.4f, o.y + 26f); moveTo(o.x, o.y); cubicTo(o.x + 3.6f, o.y + 6f, o.x + 0.6f, o.y + 12f, o.x + 4f, o.y + 20f) }
            drawPath(caidas, azul, style = Stroke(2.2f, cap = StrokeCap.Round)); drawPath(caidas, claro.copy(alpha = 0.7f), style = Stroke(0.6f, cap = StrokeCap.Round))
            for (k in listOf(-1f, 1f)) drawPath(Path().apply { moveTo(o.x, o.y); cubicTo(o.x + k * 2f, o.y - 4f, o.x + k * 6.4f, o.y - 2.6f, o.x + k * 5f, o.y + 0.6f); cubicTo(o.x + k * 3.4f, o.y + 2f, o.x + k * 1.4f, o.y + 1f, o.x, o.y); close() }, azul)
            drawCircle(claro, 1.1f, o) }
    }

    /**
     * El banco corrido de debajo de la ventana grande, con lo que los aprendices copian: tres cuerpos de yeso (una
     * esfera, un cono y un cubo), un bote con rollos de papel, una pila de tablillas y un cántaro.
     */
    private fun DrawScope.banco(m: Momento, a: Alumbrado) {
        val E = T.E; val t1 = 26f; val alto = 159f; val x0 = 138f; val x1 = 260f; val madera = lerp(C.MADERA, C.VIGA, 0.4f)
        val frente = poligono(E.p(x0, alto, t1), E.p(x1, alto, t1), E.p(x1, T.SUELO, t1), E.p(x0, T.SUELO, t1))
        drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(E.p(x0, T.SUELO, t1).x - 4f, E.p(x0, T.SUELO, t1).y - 3f), Size(E.p(x1, T.SUELO, t1).x - E.p(x0, T.SUELO, t1).x + 8f, 7f), blendMode = BlendMode.Multiply)
        planoH(a, frente, E.p(x0, alto, t1).x, E.p(x1, alto, t1).x, 174f, madera)
        clipPath(frente) {
            // Tres puertas de tabla, con su tirador, y el zócalo rozado.
            for (k in 1..2) { val x = x0 + (x1 - x0) * k / 3f; drawLine(a.en(E.p(x, 172f, t1), C.CARBON).copy(alpha = 0.6f), E.p(x, alto, t1), E.p(x, T.SUELO, t1), 0.7f) }
            for (k in 0..2) { val x = x0 + (x1 - x0) * (k + 0.5f) / 3f; drawCircle(a.en(E.p(x, 170f, t1), lerp(C.CARBON, C.ORO, 0.3f)), 0.9f, E.p(x + 12f, 171f, t1)); drawRect(a.en(E.p(x, 170f, t1), lerp(madera, C.CARBON, 0.2f)).copy(alpha = 0.5f), E.p(x - 16f, alto + 3f, t1), Size(34f, 20f), style = Stroke(0.5f)) }
            val az = Azar(Semilla.de("renacimiento", "banco")); val toques = listOf(C.YESO, C.OCRE, C.ULTRAMAR, C.CINABRIO)
            repeat(12) { val q = Offset(az.entre(136f, 264f), az.entre(164f, 186f)); drawLine(a.en(q, toques[it % 4]).copy(alpha = 0.4f), q, q + Offset(az.entre(1f, 4f), az.entre(-0.6f, 0.6f)), 0.8f, StrokeCap.Round) }
        }
        val tapa = poligono(Offset(x0, alto), Offset(x1, alto), E.p(x1 + 1f, alto, t1), E.p(x0 - 1f, alto, t1))
        drawPath(tapa, a.en(Offset(199f, alto), lerp(C.MADERA, C.MURO, 0.3f))); drawLine(a.en(Offset(199f, alto), lerp(C.MADERA, C.YESO, 0.4f)).copy(alpha = 0.7f), E.p(x0 - 1f, alto, t1), E.p(x1 + 1f, alto, t1), 0.6f)
        // Lo de encima, de izquierda a derecha.
        val pie = E.p(199f, alto, 14f).y
        // Un bote con rollos de papel.
        val bote = Offset(146f, pie)
        for ((k, dx) in listOf(-2.4f, 0.4f, 2.6f).withIndex()) { val punta = bote + Offset(dx * 1.9f, -16f - k * 2.2f); drawLine(a.en(punta, C.PAPEL), bote + Offset(dx * 0.6f, -6f), punta, 2.4f); drawCircle(a.en(punta, lerp(C.PAPEL, C.VIGA, 0.4f)), 1.2f, punta) }
        cuerpo(a, poligono(bote + Offset(-4f, -8f), bote + Offset(4f, -8f), bote + Offset(3.4f, 0f), bote + Offset(-3.4f, 0f)), bote.x - 4f, bote.x + 4f, bote.y - 4f, lerp(C.COTTO, C.MURO, 0.2f))
        // Los tres cuerpos de yeso.
        val yeso = lerp(C.YESO, C.MURO, 0.14f)
        val esfera = Offset(164f, pie - 5f); cuerpo(a, Path().apply { addOval(Rect(esfera.x - 5f, esfera.y - 5f, esfera.x + 5f, esfera.y + 5f)) }, esfera.x - 5f, esfera.x + 5f, esfera.y, yeso)
        drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(esfera.x - 4f, pie - 1.2f), Size(10f, 2f), blendMode = BlendMode.Multiply)
        cuerpo(a, poligono(Offset(174f, pie), Offset(184f, pie), Offset(179f, pie - 13f)), 174f, 184f, pie - 6f, yeso)
        val cubo = Offset(193f, pie)
        drawRect(a.en(cubo, yeso, 1f), cubo + Offset(-4f, -8f), Size(8f, 8f)); drawPath(poligono(cubo + Offset(4f, -8f), cubo + Offset(6.6f, -9.6f), cubo + Offset(6.6f, -1.8f), cubo + Offset(4f, 0f)), a.en(cubo, lerp(yeso, C.SOMBRA, 0.3f), -1f)); drawPath(poligono(cubo + Offset(-4f, -8f), cubo + Offset(-1.4f, -9.6f), cubo + Offset(6.6f, -9.6f), cubo + Offset(4f, -8f)), a.en(cubo, C.YESO))
        // Una pila de tablillas de boj, de las de dibujar a punta de plata, y un tintero.
        for (k in 0..3) drawRect(a.en(Offset(216f, pie), if (k % 2 == 0) lerp(C.MADERA, C.YESO, 0.3f) else C.MADERA), Offset(209f + (k % 2) * 1.2f, pie - 2f - k * 1.8f), Size(15f, 1.7f))
        drawRect(a.en(Offset(230f, pie), lerp(C.CARBON, C.PIEDRA, 0.2f)), Offset(228f, pie - 4f), Size(4f, 4f)); drawLine(a.en(Offset(231f, pie - 8f), C.YESO), Offset(230.4f, pie - 3.6f), Offset(234f, pie - 12f), 0.6f)
        // El cántaro del agua.
        val c0 = Offset(250f, pie)
        val cantaro = Path().apply { moveTo(c0.x - 3.4f, c0.y); cubicTo(c0.x - 7f, c0.y - 5f, c0.x - 5f, c0.y - 12f, c0.x - 2f, c0.y - 13.4f); lineTo(c0.x - 2.4f, c0.y - 16f); lineTo(c0.x + 2.4f, c0.y - 16f); lineTo(c0.x + 2f, c0.y - 13.4f); cubicTo(c0.x + 5f, c0.y - 12f, c0.x + 7f, c0.y - 5f, c0.x + 3.4f, c0.y); close() }
        cuerpo(a, cantaro, c0.x - 6.4f, c0.x + 6.4f, c0.y - 7f, C.COTTO); drawArc(a.en(c0, C.COTTO), -70f, 140f, false, Offset(c0.x + 1f, c0.y - 14f), Size(6f, 8f), style = Stroke(1f))
    }

    // ---------------------------------------------------------------- Las cosas del taller

    /** Un paño de lino echado por encima de una tabla: tapa del borde de arriba hasta [hasta] (de 0 a 1 de su alto). */
    private fun DrawScope.pano(a: Alumbrado, caja: Rect, hasta: Float, luto: Boolean) {
        val y1 = caja.top + caja.height * hasta; val o = Offset(caja.center.x, caja.top + caja.height * hasta * 0.5f); val tela = lerp(C.MURO, C.PIEDRA, 0.3f)
        val x0 = caja.left - 5.6f; val x1 = caja.right + 5.8f; val n = 4; val paso = (x1 - x0) / n
        val forma = Path().apply {
            moveTo(x0, caja.top - 5f); cubicTo(x0 + paso, caja.top - 7f, x1 - paso, caja.top - 6.4f, x1, caja.top - 5f); lineTo(x1 + 0.6f, y1 - 2f)
            // El borde de abajo, en ondas: cada pliegue cuelga un poco distinto.
            for (k in n - 1 downTo 0) { val xa = x0 + paso * (k + 1); val xb = x0 + paso * k; val cae = 3f + (k * 37 % 5); cubicTo(xa - paso * 0.3f, y1 + cae, xb + paso * 0.3f, y1 + cae, xb, y1 - 2f + (k % 2) * 2f) }
            close()
        }
        planoH(a, forma, x0, x1, o.y, tela, mira = -0.3f)
        clipPath(forma) {
            // Los pliegues: una banda en sombra y su filo de luz, de arriba abajo.
            for (k in 0 until n) { val x = x0 + paso * (k + 0.5f); val abre = (k - (n - 1) / 2f) * paso * 0.16f
                // Cada pliegue nace a media altura, donde la tela deja de apoyar en la tabla, y abre hacia abajo.
                val nace = caja.top + (y1 - caja.top) * (0.18f + 0.12f * (k % 2))
                drawPath(Path().apply { moveTo(x, nace); cubicTo(x - paso * 0.1f, nace + 8f, x - paso * 0.34f + abre, y1 - 6f, x - paso * 0.4f + abre, y1 + 8f); lineTo(x + paso * 0.1f + abre, y1 + 8f); cubicTo(x + paso * 0.06f + abre, y1 - 6f, x + paso * 0.04f, nace + 8f, x, nace); close() }, C.SOMBRA.copy(alpha = 0.2f), blendMode = BlendMode.Multiply)
                drawPath(Path().apply { moveTo(x + paso * 0.1f, nace + 3f); cubicTo(x + paso * 0.14f, nace + 12f, x + paso * 0.2f + abre, y1 - 6f, x + paso * 0.22f + abre, y1 + 3f) }, a.en(o, C.YESO).copy(alpha = 0.34f), style = Stroke(0.8f, cap = StrokeCap.Round)) }
            // La tela cuelga del canto de arriba: una sombra corta bajo el borde.
            drawRect(androidx.compose.ui.graphics.Brush.verticalGradient(0f to C.SOMBRA.copy(alpha = 0.2f), 1f to Color.Transparent, startY = caja.top, endY = caja.top + 5f), Offset(x0, caja.top), Size(x1 - x0, 5f), blendMode = BlendMode.Multiply)
            // Manchas de color: es un paño de taller.
            val az = Azar(Semilla.de("renacimiento", "pano", caja.left.toInt())); val toques = listOf(C.OCRE, C.ULTRAMAR, C.CINABRIO, C.VERDE)
            repeat(8) { val q = Offset(az.entre(caja.left, caja.right), az.entre(caja.top, y1)); drawOval(a.en(q, toques[it % 4]).copy(alpha = 0.34f), q, Size(az.entre(1.4f, 3.4f), az.entre(1f, 2.2f))) }
        }
        drawPath(forma, a.en(o, lerp(tela, C.VIGA, 0.4f)).copy(alpha = 0.4f), style = Stroke(0.5f))
        if (luto) {
            // El crespón: una banda negra cruzada y su lazo.
            val negro = a.en(o, Color(0xFF2A2630))
            drawPath(poligono(Offset(caja.left - 3f, caja.top + 4f), Offset(caja.left - 3f, caja.top + 12f), Offset(caja.right + 3.6f, caja.top + caja.height * 0.34f), Offset(caja.right + 3.6f, caja.top + caja.height * 0.34f - 8f)), negro)
            val c = Offset(caja.center.x, caja.top - 2f)
            drawPath(poligono(c, c + Offset(-7f, -4f), c + Offset(-7f, 4f)), negro); drawPath(poligono(c, c + Offset(7f, -4f), c + Offset(7f, 4f)), negro); drawPath(poligono(c + Offset(-1.6f, 0f), c + Offset(-4f, 12f), c + Offset(-0.6f, 10f)), negro); drawPath(poligono(c + Offset(1.6f, 0f), c + Offset(4f, 12f), c + Offset(0.6f, 10f)), negro)
        }
    }

    /** Un caballete de tres patas para la [caja] de una tabla: los dos largueros, la pata de atrás, el travesaño donde apoya y el de arriba. */
    private fun DrawScope.caballete(a: Alumbrado, caja: Rect, suelo: Float, grueso: Float) {
        val o = Offset(caja.center.x, caja.bottom); val madera = a.en(o, lerp(C.MADERA, C.VIGA, 0.35f)); val oscura = a.en(o, lerp(C.VIGA, C.CARBON, 0.2f)); val abre = caja.width * 0.06f
        drawLine(oscura, Offset(caja.center.x + abre, caja.top + 8f), Offset(caja.center.x + caja.width * 0.22f, suelo - (suelo - caja.bottom) * 0.22f), grueso * 0.8f, StrokeCap.Round)
        for (k in listOf(-1f, 1f)) { val x0 = caja.center.x + k * (caja.width * 0.36f); val x1 = caja.center.x + k * (caja.width * 0.5f + abre)
            drawLine(madera, Offset(x0, caja.top - 7f), Offset(x1, suelo), grueso, StrokeCap.Round); drawLine(a.en(o, lerp(C.MADERA, C.YESO, 0.25f)).copy(alpha = 0.6f), Offset(x0 - grueso * 0.3f, caja.top - 7f), Offset(x1 - grueso * 0.3f, suelo), grueso * 0.22f) }
        drawLine(madera, Offset(caja.center.x - caja.width * 0.4f, caja.top - 4f), Offset(caja.center.x + caja.width * 0.4f, caja.top - 4f), grueso * 0.8f)
        drawLine(oscura, Offset(caja.center.x - caja.width * 0.56f, suelo - (suelo - caja.bottom) * 0.42f), Offset(caja.center.x + caja.width * 0.56f, suelo - (suelo - caja.bottom) * 0.42f), grueso * 0.7f)
    }

    /** El travesaño donde apoya la tabla, por delante de ella, con sus clavijas. */
    private fun DrawScope.apoyo(a: Alumbrado, caja: Rect, grueso: Float) {
        val o = Offset(caja.center.x, caja.bottom)
        drawRect(a.en(o, lerp(C.MADERA, C.VIGA, 0.2f)), Offset(caja.left - caja.width * 0.08f, caja.bottom), Size(caja.width * 1.16f, grueso * 1.5f)); drawLine(a.en(o, lerp(C.MADERA, C.YESO, 0.3f)).copy(alpha = 0.7f), Offset(caja.left - caja.width * 0.08f, caja.bottom), Offset(caja.right + caja.width * 0.08f, caja.bottom), 0.5f)
        for (k in listOf(0.14f, 0.86f)) drawCircle(a.en(o, C.CARBON), grueso * 0.3f, Offset(caja.left + caja.width * k, caja.bottom + grueso * 0.75f))
    }

    private fun cosas(m: Momento, a: Alumbrado): List<Cosa> = buildList {
        val campo = R.enElCampo(m); val tapadas = R.tapadas(m)
        // ---- El caballete grande, con la tabla del maestro.
        add(Cosa(poligono(Offset(280f, 44f), Offset(352f, 44f), Offset(356f, 204f), Offset(278f, 204f)), Offset(317f, 204f)) {
            val g = TABLA_GRANDE; val tono: (Color) -> Color = { a.en(Offset(312f, 96f), it, -0.3f) }
            caballete(a, g, 204f, 3.2f)
            // El canto de la tabla, de álamo, con sus dos barrotes por detrás.
            val t = R.tablaDelMaestro(m)
            with(PinturasRenacimiento) { tablaGrande(g, t.motivo, t.avance, 4f, tono) }
            // La luz de la ventana grande resbala por la tabla desde la izquierda.
            drawRect(androidx.compose.ui.graphics.Brush.horizontalGradient(0f to Color.White.copy(alpha = 0.1f * a.focos[0].fuerza), 0.6f to Color.Transparent, startX = g.left, endX = g.right), g.topLeft, g.size)
            if (R.maestroDeLuto(m)) pano(a, g, 1f, true) else if (tapadas) pano(a, g, 0.44f, false)
            apoyo(a, g, 3f)
            // Un trapo de limpiar pinceles, colgado de una clavija, y el tiento apoyado en el larguero.
            drawPath(poligono(Offset(352f, 146f), Offset(358f, 146f), Offset(359f, 162f), Offset(355f, 158f), Offset(351f, 163f)), a.en(Offset(355f, 154f), lerp(C.YESO, C.OCRE, 0.3f)))
            for ((k, c) in listOf(C.CINABRIO, C.ULTRAMAR, C.VERDE).withIndex()) drawCircle(a.en(Offset(355f, 154f), c).copy(alpha = 0.6f), 1f, Offset(353.6f + k * 1.8f, 151f + k * 3f))
            drawLine(a.en(Offset(366f, 150f), lerp(C.MADERA, C.YESO, 0.2f)), Offset(372f, 206f), Offset(360f, 92f), 1.1f, StrokeCap.Round); drawCircle(a.en(Offset(360f, 92f), lerp(C.VIGA, C.CINABRIO, 0.3f)), 2f, Offset(360f, 91f))
        })
        // ---- El cubo del yeso, con su brocha, al pie del caballete.
        add(Cosa(poligono(Offset(234f, 194f), Offset(254f, 194f), Offset(252f, 210f), Offset(236f, 210f)), Offset(244f, 210f)) {
            val cubo = Path().apply { moveTo(234.6f, 196f); lineTo(253.4f, 196f); lineTo(251.4f, 210f); lineTo(236.6f, 210f); close() }
            cuerpo(a, cubo, 234.6f, 253.4f, 203f, lerp(C.MADERA, C.VIGA, 0.3f))
            for (y in listOf(199f, 207f)) drawLine(a.en(Offset(244f, y), C.CARBON).copy(alpha = 0.7f), Offset(235f + (y - 196f) * 0.14f, y), Offset(253f - (y - 196f) * 0.14f, y), 0.9f)
            drawOval(a.en(Offset(244f, 196f), C.YESO), Offset(235.4f, 194.2f), Size(17.2f, 3.6f)); drawLine(a.en(Offset(248f, 190f), C.MADERA), Offset(247f, 196f), Offset(251f, 184f), 1.3f, StrokeCap.Round)
            for (x in listOf(238f, 249f)) drawLine(a.en(Offset(x, 200f), C.YESO).copy(alpha = 0.8f), Offset(x, 196.6f), Offset(x + 0.4f, 203f), 1f, StrokeCap.Round)
        })
        // ---- El rincón de Iacopo: su caballete desde que empieza su tabla; antes, las tablas enyesadas que prepara.
        if (R.conCaballete(m)) add(Cosa(poligono(Offset(60f, 102f), Offset(114f, 102f), Offset(118f, 228f), Offset(57f, 228f)), Offset(88f, 228f)) {
            val g = TABLA_CHICA; val tono: (Color) -> Color = { a.en(Offset(90f, 140f), it, 0.2f) }
            caballete(a, g, 228f, 2.4f)
            with(PinturasRenacimiento) { virgen(g, R.tablaDeIacopo(m), R.conTabla(m) && R.firmada(m), 2.6f, tono) }
            if (tapadas && R.conTabla(m)) pano(a, g, 0.4f, false)
            apoyo(a, g, 2.2f)
        }) else add(Cosa(poligono(Offset(80f, 128f), Offset(126f, 132f), Offset(128f, 190f), Offset(82f, 192f)), Offset(104f, 192f)) {
            // Tres tablas de álamo, apoyadas en la pared: la de detrás, en madera; las otras, con sus manos de yeso.
            drawPath(poligono(Offset(98f, 126f), Offset(128f, 128f), Offset(129f, 187f), Offset(100f, 188f)), a.en(Offset(114f, 156f), lerp(C.MADERA, C.MURO, 0.2f)))
            for (y in listOf(140f, 172f)) drawLine(a.en(Offset(114f, y), lerp(C.VIGA, C.MADERA, 0.4f)), Offset(98.4f, y), Offset(128.6f, y + 1f), 2.4f)
            val media = poligono(Offset(90f, 134f), Offset(120f, 132f), Offset(122f, 189f), Offset(92f, 191f))
            planoH(a, media, 90f, 122f, 160f, lerp(C.YESO, C.MURO, 0.25f)); drawPath(media, a.en(Offset(106f, 160f), C.VIGA).copy(alpha = 0.5f), style = Stroke(0.5f))
            val primera = poligono(Offset(80f, 140f), Offset(108f, 138f), Offset(110f, 191f), Offset(82f, 193f))
            planoH(a, primera, 80f, 110f, 166f, lerp(C.YESO, C.MURO, 0.12f)); drawPath(primera, a.en(Offset(96f, 166f), C.VIGA).copy(alpha = 0.5f), style = Stroke(0.5f))
            for (k in 1..6) drawLine(a.en(Offset(96f, 166f), lerp(C.YESO, C.PIEDRA, 0.3f)).copy(alpha = 0.4f), Offset(80.6f + 0.3f * k, 140f + k * 7.4f), Offset(109f + 0.3f * k, 138f + k * 7.4f), 0.5f)
            // La de delante ya lleva el oro y el dibujo de un santo, y espera sus colores.
            with(PinturasRenacimiento) { santo(Rect(81.6f, 140.6f, 108.6f, 190.4f), 1, 0.5f) { c -> a.en(Offset(96f, 166f), c) } }
            drawPath(primera, a.en(Offset(96f, 166f), C.VIGA).copy(alpha = 0.6f), style = Stroke(0.6f))
            // El canto de la madera, que asoma por un lado.
            drawPath(poligono(Offset(108f, 138f), Offset(110f, 138.6f), Offset(112f, 190.6f), Offset(110f, 191f)), a.en(Offset(110f, 166f), lerp(C.MADERA, C.VIGA, 0.3f)))
        })
        // ---- La mesa de moler.
        add(Cosa(poligono(Offset(84f, 178f), Offset(204f, 178f), Offset(214f, 246f), Offset(74f, 246f)), Offset(144f, 246f)) { mesa(m, a) })
        // ---- La banqueta de tres patas; en el campo, patas arriba sobre la mesa.
        if (!campo) add(Cosa(poligono(PIE_BANQUETA + Offset(-14f, -36f), PIE_BANQUETA + Offset(15f, -36f), PIE_BANQUETA + Offset(19f, 0f), PIE_BANQUETA + Offset(-18f, 0f)), PIE_BANQUETA) {
            scale(CRECE_BANQUETA, CRECE_BANQUETA, PIE_BANQUETA) { banqueta(a, PIE_BANQUETA, false) }
        })
        // ---- El hornillo: un brasero de hierro de tres patas, con el bote de la cola encima.
        add(Cosa(poligono(enHornillo(Offset(250f, 212f)), enHornillo(Offset(274f, 212f)), enHornillo(Offset(276f, 236f)), enHornillo(Offset(248f, 236f))), PIE_HORNILLO) {
          translate(PIE_HORNILLO.x - 262f, PIE_HORNILLO.y - 236f) { scale(CRECE_HORNILLO, CRECE_HORNILLO, Offset(262f, 236f)) {
            val hierro = a.en(PIE_HORNILLO + Offset(0f, -12f), lerp(C.CARBON, C.PIEDRA, 0.2f)); val lumbre = R.lumbre(m); val rescoldo = R.rescoldo(m)
            for ((x0, x1) in listOf(251f to 248.6f, 273f to 275.4f, 262f to 262f)) drawLine(hierro, Offset(x0, 226f), Offset(x1, 236f), 1.6f, StrokeCap.Round)
            val cuenco = Path().apply { moveTo(249f, 222f); lineTo(275f, 222f); cubicTo(274f, 229f, 268f, 231f, 262f, 231f); cubicTo(256f, 231f, 250f, 229f, 249f, 222f); close() }
            drawPath(cuenco, androidx.compose.ui.graphics.Brush.horizontalGradient(0f to a.en(PIE_HORNILLO + Offset(-18f, -14f), lerp(C.PIEDRA, C.CARBON, 0.45f), -1f), 0.5f to a.en(PIE_HORNILLO + Offset(0f, -14f), C.CARBON), 1f to a.en(PIE_HORNILLO + Offset(18f, -14f), lerp(C.PIEDRA, C.CARBON, 0.45f), 1f), startX = 249f, endX = 275f))
            drawOval(a.en(PIE_HORNILLO + Offset(0f, -20f), C.CARBON), Offset(249f, 219.6f), Size(26f, 4.8f))
            // Las ascuas: negras apagadas, rojas con lumbre.
            val asc = Azar(Semilla.de("renacimiento", "ascuas"))
            repeat(9) { val q = Offset(asc.entre(252f, 272f), asc.entre(220.6f, 223.4f)); drawCircle(if (lumbre) lerp(Color(0xFFFF7A2A), Color(0xFFFFD27A), asc.entre(0f, 0.6f)) else if (rescoldo) lerp(a.en(q, C.CARBON), Color(0xFFE0562A), asc.entre(0.2f, 0.7f)) else a.en(q, lerp(C.CARBON, C.PIEDRA, asc.entre(0f, 0.4f))), asc.entre(0.9f, 1.6f), q) }
            for (y in listOf(225f, 228f)) drawLine(a.en(PIE_HORNILLO + Offset(0f, -14f), C.CARBON).copy(alpha = 0.6f), Offset(250.6f + (y - 222f) * 0.5f, y), Offset(273.4f - (y - 222f) * 0.5f, y), 0.5f)
            // La trébede y el bote de cobre de la cola, con su mango.
            if (R.trabajando(m) || lumbre) {
                drawLine(hierro, Offset(254f, 220f), Offset(270f, 220f), 0.9f)
                val bote = Path().apply { moveTo(256f, 211f); lineTo(268f, 211f); lineTo(267f, 220f); lineTo(257f, 220f); close() }
                val cobre = lerp(C.COTTO, C.OCRE, 0.4f); val enBote = PIE_HORNILLO + Offset(0f, -32f)
                drawPath(bote, androidx.compose.ui.graphics.Brush.horizontalGradient(0f to a.en(enBote, lerp(cobre, C.YESO, 0.25f), -1f), 0.55f to a.en(enBote, lerp(cobre, C.CARBON, 0.2f)), 1f to a.en(enBote, cobre, 1f), startX = 256f, endX = 268f)); drawOval(a.en(enBote, lerp(C.OCRE, C.YESO, 0.4f)), Offset(256f, 209.8f), Size(12f, 2.6f))
                drawLine(hierro, Offset(268f, 213f), Offset(277f, 210f), 1.2f, StrokeCap.Round)
            }
          } }
            if (R.lumbre(m)) resplandor(LUZ_LUMBRE, 18f, 0.6f, Color(0xFFFF9A4A))
        })
        // ---- El arcón, con las tablas enyesadas apoyadas detrás y un paño doblado encima.
        add(Cosa(poligono(Offset(302f, 150f), Offset(378f, 146f), Offset(378f, 258f), Offset(300f, 258f)), Offset(338f, 258f)) {
            val detras = poligono(Offset(338f, 152f), Offset(372f, 148f), Offset(377f, 220f), Offset(342f, 220f))
            drawPath(poligono(Offset(346f, 146f), Offset(380f, 143f), Offset(381f, 220f), Offset(350f, 220f)), a.en(Offset(364f, 180f), lerp(C.MADERA, C.MURO, 0.2f), -1f))
            // Una tabla grande vuelta del revés: la madera de álamo, con sus dos barrotes y sus colas de milano.
            planoH(a, detras, 338f, 377f, 184f, lerp(C.MADERA, C.MURO, 0.3f), mira = -0.5f)
            clipPath(detras) {
                for (k in 1..3) drawLine(a.en(Offset(356f, 184f), lerp(C.MADERA, C.VIGA, 0.5f)).copy(alpha = 0.5f), Offset(338f + k * 9.4f, 146f), Offset(341f + k * 9.6f, 222f), 0.5f)
                for (y in listOf(164f, 200f)) drawLine(a.en(Offset(356f, y), lerp(C.VIGA, C.MADERA, 0.35f)), Offset(336f, y + 2f), Offset(380f, y - 2f), 4.4f)
                for ((x, y) in listOf(348f to 182f, 367f to 181f)) drawPath(poligono(Offset(x - 2.6f, y - 3f), Offset(x + 2.6f, y - 3f), Offset(x + 1.4f, y), Offset(x + 2.6f, y + 3f), Offset(x - 2.6f, y + 3f)), a.en(Offset(x, y), lerp(C.VIGA, C.CARBON, 0.2f)).copy(alpha = 0.7f))
                val az = Azar(Semilla.de("renacimiento", "reves")); repeat(5) { val q = Offset(az.entre(340f, 374f), az.entre(152f, 216f)); mancha(q, az.entre(3f, 7f), az.entre(4f, 9f), a.en(q, C.VIGA), 0.14f) }
            }
            drawPath(detras, a.en(Offset(356f, 184f), C.VIGA).copy(alpha = 0.6f), style = Stroke(0.6f))
            // Delante, el ala de un retablo, de remate en arco: el bol rojo, el oro ya bruñido en lo alto y un santo dibujado.
            val ala = Path().apply { moveTo(308f, 226f); lineTo(306f, 186f); cubicTo(306f, 166f, 334f, 164f, 335f, 184f); lineTo(338f, 226f); close() }
            val enAla = Offset(322f, 196f)
            drawPath(ala, a.en(enAla, lerp(C.CINABRIO, C.VIGA, 0.25f)))
            clipPath(ala) { with(PinturasRenacimiento) { santo(Rect(305f, 166f, 339f, 227f), 0, 0.9f) { c -> a.en(enAla, c) } } }
            drawPath(ala, a.en(enAla, lerp(C.ORO, C.VIGA, 0.3f)), style = Stroke(1.4f))
            // El arcón: la tapa, el frente con sus dos fajas de hierro y su cerradura.
            val frente = poligono(Offset(300f, 227f), Offset(374f, 227f), Offset(374f, 258f), Offset(300f, 258f))
            planoH(a, frente, 300f, 374f, 242f, lerp(C.VIGA, C.MADERA, 0.3f))
            drawPath(poligono(Offset(305f, 218f), Offset(379f, 218f), Offset(374f, 227f), Offset(300f, 227f)), a.en(Offset(338f, 222f), lerp(C.MADERA, C.VIGA, 0.3f)))
            drawLine(a.en(Offset(338f, 218f), lerp(C.MADERA, C.YESO, 0.3f)).copy(alpha = 0.6f), Offset(305f, 218f), Offset(379f, 218f), 0.6f)
            for (y in listOf(235f, 243f, 251f)) drawLine(a.en(Offset(338f, y), C.CARBON).copy(alpha = 0.4f), Offset(300f, y), Offset(374f, y), 0.5f)
            for (x in listOf(312f, 362f)) { drawRect(a.en(Offset(x, 242f), lerp(C.CARBON, C.PIEDRA, 0.25f)), Offset(x - 1.8f, 227f), Size(3.6f, 31f)); drawPath(poligono(Offset(x - 1.8f, 227f), Offset(x + 1.8f, 227f), Offset(x + 3.4f, 218f), Offset(x - 0.2f, 218f)), a.en(Offset(x, 222f), lerp(C.CARBON, C.PIEDRA, 0.35f))) }
            drawRect(a.en(Offset(337f, 234f), lerp(C.CARBON, C.ORO, 0.35f)), Offset(334f, 229f), Size(6f, 7f)); drawCircle(a.en(Offset(337f, 232f), C.CARBON), 0.9f, Offset(337f, 232f))
            // Rozaduras y manchas de color en el frente: aquí se sientan a comer.
            val az = Azar(Semilla.de("renacimiento", "arcon")); val toques = listOf(C.YESO, C.OCRE, C.ULTRAMAR, C.CINABRIO)
            repeat(10) { val q = Offset(az.entre(302f, 372f), az.entre(229f, 256f)); drawLine(a.en(q, toques[it % 4]).copy(alpha = 0.4f), q, q + Offset(az.entre(1f, 4f), az.entre(-0.6f, 0.6f)), 0.8f, StrokeCap.Round) }
            drawPath(poligono(Offset(354f, 216f), Offset(372f, 215.4f), Offset(374f, 220.6f), Offset(352f, 221.4f)), a.en(Offset(363f, 218f), lerp(C.YESO, C.ULTRAMAR, 0.16f)))
            drawLine(a.en(Offset(363f, 218f), C.PIEDRA).copy(alpha = 0.5f), Offset(353f, 218.6f), Offset(373f, 218f), 0.4f)
            if (R.carnaval(m)) for ((k, c) in listOf(C.CINABRIO, C.ORO).withIndex()) {
                // El martes de carnaval, las dos máscaras de papel que pintaron, apoyadas sobre el arcón con sus cintas.
                val o = Offset(316f + k * 23f, 209f - k * 1.6f); val u = 1.75f
                val cara = Path().apply { moveTo(o.x - 5f * u, o.y - 2f * u); cubicTo(o.x - 5f * u, o.y - 6.4f * u, o.x + 5f * u, o.y - 6.4f * u, o.x + 5f * u, o.y - 2f * u); cubicTo(o.x + 4f * u, o.y + 3f * u, o.x + 1.6f * u, o.y + 3.6f * u, o.x, o.y + 1f * u); cubicTo(o.x - 1.6f * u, o.y + 3.6f * u, o.x - 4f * u, o.y + 3f * u, o.x - 5f * u, o.y - 2f * u); close() }
                drawPath(Path().apply { addPath(cara, Offset(1.6f, 1.2f)) }, C.SOMBRA.copy(alpha = 0.34f), blendMode = BlendMode.Multiply)
                for (dx in listOf(-1f, 1f)) drawPath(Path().apply { moveTo(o.x + dx * 4.8f * u, o.y - 2.6f * u); cubicTo(o.x + dx * 8f * u, o.y, o.x + dx * 5.6f * u, o.y + 4f * u, o.x + dx * 7.4f * u, o.y + 7f * u) }, a.en(o, if (k == 0) C.ULTRAMAR else C.CINABRIO), style = Stroke(1f, cap = StrokeCap.Round))
                cuerpo(a, cara, o.x - 5f * u, o.x + 5f * u, o.y, c)
                clipPath(cara) { drawRect(a.en(o, if (k == 0) C.ORO else C.VERDE), Offset(o.x - 6f * u, o.y - 6.6f * u), Size(12f * u, 2.3f * u)); for (j in -2..2) drawCircle(a.en(o, C.YESO), 0.55f * u, Offset(o.x + j * 2.1f * u, o.y - 5.4f * u)) }
                for (dx in listOf(-2.4f, 2.4f)) { drawOval(a.en(o, C.CARBON), Offset(o.x + (dx - 1.4f) * u, o.y - 2.6f * u), Size(2.8f * u, 1.7f * u)); drawOval(a.en(o, C.YESO).copy(alpha = 0.8f), Offset(o.x + (dx - 1.4f) * u, o.y - 2.6f * u), Size(2.8f * u, 1.7f * u), style = Stroke(0.4f)) }
                drawPath(Path().apply { moveTo(o.x, o.y - 1.6f * u); lineTo(o.x + 1.1f * u, o.y + 1.2f * u); lineTo(o.x - 0.4f * u, o.y + 1.2f * u) }, a.en(o, lerp(c, C.CARBON, 0.4f)), style = Stroke(0.6f))
                drawPath(cara, a.en(o, lerp(c, C.CARBON, 0.5f)), style = Stroke(0.5f))
            }
            if (R.posa(m)) {
                // El día que posa de pastor: el cayado y la zamarra, apoyados en el arcón.
                drawPath(Path().apply { moveTo(304f, 258f); lineTo(296f, 176f); cubicTo(294f, 166f, 304f, 164f, 304f, 172f) }, a.en(Offset(300f, 210f), lerp(C.MADERA, C.VIGA, 0.3f)), style = Stroke(1.8f, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(318f, 218f); cubicTo(322f, 210f, 344f, 210f, 348f, 218f); cubicTo(350f, 230f, 344f, 240f, 340f, 238f); cubicTo(334f, 232f, 326f, 240f, 320f, 236f); close() }, a.en(Offset(333f, 224f), lerp(C.YESO, C.OCRE, 0.3f)))
            }
        })
        // ---- El jergón: de día, enrollado contra la pared; mientras duermen, extendido, con su manta.
        if (R.jergon(m)) add(Cosa(poligono(Offset(2f, 244f), Offset(70f, 240f), Offset(72f, 262f), Offset(0f, 264f)), Offset(34f, 263f)) {
            val paja = lerp(C.MURO, C.OCRE, 0.35f)
            val colchon = poligono(Offset(0f, 247f), Offset(66f, 242f), Offset(72f, 258f), Offset(0f, 264f))
            planoH(a, colchon, 0f, 72f, 254f, paja); for (k in 1..6) drawLine(a.en(Offset(36f, 254f), lerp(paja, C.ULTRAMAR, 0.4f)).copy(alpha = 0.5f), Offset(k * 10f, 247f - k * 0.7f), Offset(k * 10.6f, 263f - k * 0.9f), 0.7f)
            // La manta, de lana, arrebujada.
            val manta = Path().apply { moveTo(0f, 242f); cubicTo(14f, 234f, 34f, 236f, 50f, 240f); cubicTo(60f, 243f, 64f, 250f, 62f, 258f); lineTo(0f, 263f); close() }
            planoH(a, manta, 0f, 64f, 250f, lerp(C.PIEDRA, C.CINABRIO, 0.35f)); clipPath(manta) { for (k in 0..4) drawPath(Path().apply { moveTo(4f + k * 12f, 262f); cubicTo(10f + k * 12f, 252f, 6f + k * 12f, 244f, 14f + k * 12f, 236f) }, a.en(Offset(30f, 250f), lerp(C.PIEDRA, C.CARBON, 0.4f)).copy(alpha = 0.45f), style = Stroke(0.9f)) }
        }) else add(Cosa(poligono(Offset(8f, 236f), Offset(48f, 236f), Offset(48f, 260f), Offset(8f, 260f)), Offset(28f, 260f)) {
            val paja = lerp(C.MURO, C.OCRE, 0.35f)
            val rollo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(8f, 237f, 46f, 260f), CornerRadius(9f))) }
            planoH(a, rollo, 8f, 46f, 248f, paja)
            clipPath(rollo) { for (k in 0..5) drawLine(a.en(Offset(28f, 248f), lerp(paja, C.ULTRAMAR, 0.4f)).copy(alpha = 0.5f), Offset(8f, 240f + k * 3.6f), Offset(46f, 240f + k * 3.8f), 0.7f) }
            drawOval(a.en(Offset(46f, 248f), lerp(paja, C.VIGA, 0.3f), 1f), Offset(41f, 237.6f), Size(9f, 22f))
            for (k in 0..2) drawOval(a.en(Offset(46f, 248f), lerp(paja, C.VIGA, 0.5f)).copy(alpha = 0.7f), Offset(42.6f + k * 1.3f, 240f + k * 3.4f), Size(6f - k * 2.4f, 17f - k * 6.8f), style = Stroke(0.5f))
            for (x in listOf(17f, 31f)) drawLine(a.en(Offset(x, 248f), lerp(C.VIGA, C.MADERA, 0.3f)), Offset(x, 237f), Offset(x + 0.6f, 260f), 1.2f)
        })
        // ---- La escoba, apoyada en la pared de la calle: lo primero del día es barrer.
        add(Cosa(poligono(Offset(8f, 150f), Offset(14f, 150f), Offset(24f, 236f), Offset(6f, 236f)), Offset(15f, 236f)) {
            drawLine(a.en(Offset(14f, 190f), lerp(C.MADERA, C.YESO, 0.15f), 1f), Offset(10f, 152f), Offset(15f, 222f), 1.5f, StrokeCap.Round)
            val ramas = Path().apply { moveTo(12.6f, 218f); lineTo(17.6f, 218f); lineTo(24f, 236f); lineTo(7f, 236f); close() }
            drawPath(ramas, a.en(Offset(15f, 228f), lerp(C.OCRE, C.VIGA, 0.4f))); clipPath(ramas) { for (k in 0..6) drawLine(a.en(Offset(15f, 228f), lerp(C.OCRE, C.YESO, 0.3f)).copy(alpha = 0.6f), Offset(13f + k * 0.7f, 218f), Offset(8f + k * 2.6f, 236f), 0.5f) }
            drawLine(a.en(Offset(15f, 220f), C.VIGA), Offset(12.4f, 220f), Offset(17.8f, 220f), 1.4f)
        })
        // ---- En el suelo, delante de la mesa (y detrás de por donde pasa el gato): las tablas de álamo por enyesar,
        //      con sus virutas; el trapo de limpiar pinceles; y el mortero de bronce con su mano.
        add(Cosa(poligono(Offset(86f, 251f), Offset(140f, 250f), Offset(144f, 262f), Offset(82f, 263f)), Offset(113f, 262.6f)) {
            val az = Azar(Semilla.de("renacimiento", "tablas"))
            for (k in 0..2) { val y = 262f - k * 3.6f; val x0 = 84f + k * 3f + az.entre(-2f, 2f); val x1 = 142f - k * 2f + az.entre(-3f, 3f)
                val canto = poligono(Offset(x0, y - 3.2f), Offset(x1, y - 3.6f), Offset(x1, y - 0.4f), Offset(x0, y)); val cara = poligono(Offset(x0, y - 3.2f), Offset(x1, y - 3.6f), Offset(x1 - 5f, y - 6.6f), Offset(x0 + 6f, y - 6.2f))
                drawPath(canto, a.en(Offset(113f, y), lerp(C.MADERA, C.VIGA, 0.3f))); drawPath(cara, a.en(Offset(113f, y - 5f), if (k == 2) lerp(C.YESO, C.MADERA, 0.2f) else lerp(C.MADERA, C.YESO, 0.2f)))
                for (j in 1..3) drawLine(a.en(Offset(113f, y), C.VIGA).copy(alpha = 0.4f), Offset(x0 + 2f, y - 0.6f - j * 0.7f), Offset(x1 - 2f, y - 1f - j * 0.7f), 0.3f)
                drawLine(a.en(Offset(113f, y - 6f), C.YESO).copy(alpha = 0.6f), Offset(x0 + 6f, y - 6.2f), Offset(x1 - 5f, y - 6.6f), 0.4f) }
            // Las virutas del cepillo, rizadas, y el serrín.
            repeat(14) { val o = Offset(az.entre(70f, 150f), az.entre(259f, 266f)); drawArc(a.en(o, lerp(C.MADERA, C.YESO, 0.5f)), -200f + it * 37f, 250f, false, o - Offset(1.5f, 1.2f), Size(3f, 2.4f), style = Stroke(0.6f)) }
        })
        add(Cosa(poligono(Offset(151f, 256f), Offset(170f, 255f), Offset(172f, 262f), Offset(149f, 263f)), Offset(160f, 262f)) {
            // Un trapo de lino arrugado, con una punta doblada y las manchas de limpiar pinceles.
            val trapo = Path().apply { moveTo(149f, 262.4f); lineTo(151f, 257.6f); lineTo(156f, 258.6f); lineTo(159f, 254.8f); lineTo(165f, 257f); lineTo(170f, 255.6f); lineTo(172.6f, 260f); lineTo(169f, 263.4f); lineTo(161f, 262.6f); lineTo(154f, 264f); close() }
            val lino = lerp(C.YESO, C.PIEDRA, 0.3f)
            drawPath(trapo, a.en(Offset(160f, 259f), lino))
            clipPath(trapo) {
                drawPath(poligono(Offset(159f, 254.8f), Offset(165f, 257f), Offset(161f, 262.6f), Offset(156f, 258.6f)), a.en(Offset(160f, 258f), lerp(lino, C.YESO, 0.5f)))
                for ((x, y, c) in listOf(Triple(153.4f, 260.6f, C.ULTRAMAR), Triple(166.6f, 259.4f, C.CINABRIO), Triple(169f, 261.6f, C.OCRE))) mancha(Offset(x, y), 3f, 1.8f, a.en(Offset(x, y), c), 0.6f)
                for ((x0, x1) in listOf(151f to 156f, 156f to 161f, 165f to 161.6f, 170f to 167f)) drawLine(C.SOMBRA.copy(alpha = 0.34f), Offset(x0, 256f), Offset(x1, 264f), 0.6f)
            }
            drawPath(trapo, C.SOMBRA.copy(alpha = 0.36f), style = Stroke(0.5f))
        })
        if (!R.fiesta(m)) add(Cosa(poligono(Offset(188f, 247f), Offset(206f, 247f), Offset(205f, 262f), Offset(189f, 262f)), Offset(197f, 262f)) {
            val bronce = lerp(C.OCRE, C.VIGA, 0.45f); val o = Offset(197f, 256f)
            drawLine(a.en(o + Offset(4f, -12f), lerp(C.VIGA, C.MADERA, 0.3f)), Offset(198f, 254f), Offset(205f, 241f), 2.2f, StrokeCap.Round); drawCircle(a.en(o + Offset(8f, -15f), lerp(C.VIGA, C.MADERA, 0.3f)), 1.8f, Offset(205.4f, 240.6f))
            val vaso = Path().apply { moveTo(189f, 250f); lineTo(205f, 250f); cubicTo(205f, 256f, 202.6f, 259.6f, 201.6f, 260f); lineTo(203.4f, 262f); lineTo(190.6f, 262f); lineTo(192.4f, 260f); cubicTo(191.4f, 259.6f, 189f, 256f, 189f, 250f); close() }
            cuerpo(a, vaso, 189f, 205f, 256f, bronce)
            drawOval(a.en(o, lerp(bronce, C.CARBON, 0.5f)), Offset(189f, 248.4f), Size(16f, 3.4f)); drawOval(a.en(o, lerp(bronce, C.YESO, 0.4f)), Offset(189f, 248.4f), Size(16f, 3.4f), style = Stroke(0.6f))
            drawLine(a.en(o, lerp(bronce, C.YESO, 0.3f)).copy(alpha = 0.7f), Offset(190f, 254f), Offset(204f, 254f), 0.5f)
        })
        if (R.fiesta(m)) add(Cosa(poligono(Offset(190f, 236f), Offset(206f, 236f), Offset(204f, 262f), Offset(192f, 262f)), Offset(198f, 262f)) {
            // En fiesta, un cántaro con ramas de laurel (el primero de mayo, en flor) al pie de la mesa.
            val az = Azar(Semilla.de("renacimiento", "cantaro")); val boca = Offset(198f, 241f)
            repeat(9) { val q = boca + Offset(az.entre(-13f, 13f), -az.entre(8f, 26f)); drawLine(a.en(q, lerp(C.VIGA, C.VERDE, 0.4f)), boca, q, 0.7f)
                repeat(4) { k -> val h = boca + (q - boca) * (0.4f + 0.2f * k) + Offset(az.entre(-2f, 2f), az.entre(-1.4f, 1.4f)); drawOval(a.en(h, if (R.mayo(m) && k % 2 == 0) C.YESO else lerp(C.VERDE, C.CARBON, az.entre(0f, 0.3f))), h - Offset(1.6f, 0.9f), Size(3.2f, 1.8f)) } }
            val cantaro = Path().apply { moveTo(193f, 262f); cubicTo(186f, 254f, 191f, 244f, 195f, 242f); lineTo(195f, 240f); lineTo(201f, 240f); lineTo(201f, 242f); cubicTo(205f, 244f, 210f, 254f, 203f, 262f); close() }
            cuerpo(a, cantaro, 188f, 208f, 252f, C.COTTO); drawLine(a.en(Offset(198f, 250f), lerp(C.COTTO, C.YESO, 0.4f)).copy(alpha = 0.6f), Offset(191f, 249f), Offset(205f, 249f), 0.6f)
        })
    }

    /** Una banqueta de tres patas, de las de moler sentado. [alReves]: patas arriba. */
    private fun DrawScope.banqueta(a: Alumbrado, pie: Offset, alReves: Boolean) {
        val k = if (alReves) -1f else 1f; val madera = a.en(pie + Offset(0f, -14f * k), lerp(C.MADERA, C.VIGA, 0.3f)); val alto = 24f
        for (dx in listOf(-9f, 9f, 1f)) drawLine(a.en(pie, lerp(C.VIGA, C.MADERA, 0.2f)), pie + Offset(dx * 0.6f, -alto * k + 1f * k), pie + Offset(dx * 1.1f, if (dx == 1f) -3f * k else 0f), 2f, StrokeCap.Round)
        drawLine(a.en(pie, lerp(C.VIGA, C.CARBON, 0.2f)), pie + Offset(-7.6f, -9f * k), pie + Offset(7.6f, -9f * k), 1.2f)
        drawOval(madera, Offset(pie.x - 11f, pie.y - alto * k - 2.6f), Size(22f, 5.2f)); drawOval(a.en(pie, lerp(C.MADERA, C.YESO, 0.3f)).copy(alpha = 0.5f), Offset(pie.x - 9f, pie.y - alto * k - 2.2f), Size(14f, 2.6f))
        if (!alReves) drawOval(a.en(pie, lerp(C.VIGA, C.CARBON, 0.3f)), Offset(pie.x - 11f, pie.y - alto + 0.2f), Size(22f, 3f), style = Stroke(0.6f))
    }

    /** La mesa de moler, con lo que tiene encima. */
    private fun DrawScope.mesa(m: Momento, a: Alumbrado) {
        val campo = R.enElCampo(m); val oscura = lerp(C.VIGA, C.MADERA, 0.25f)
        // Lo de debajo, en sombra, y las cuatro patas con su travesaño.
        drawPath(poligono(Offset(80f, 194f), Offset(208f, 194f), Offset(204f, 238f), Offset(86f, 238f)), C.SOMBRA.copy(alpha = 0.3f), blendMode = BlendMode.Multiply)
        for (x in listOf(93f, 197f)) drawRect(a.en(Offset(x, 214f), lerp(oscura, C.CARBON, 0.25f)), Offset(x - 2f, 190f), Size(4f, 48f))
        for (x in listOf(80f, 208f)) { drawRect(a.en(Offset(x, 220f), oscura, if (x < 144f) -1f else 1f), Offset(x - 2.6f, 194f), Size(5.2f, 52f)); drawLine(a.en(Offset(x, 220f), lerp(C.MADERA, C.YESO, 0.2f)).copy(alpha = 0.5f), Offset(x - 2.4f, 195f), Offset(x - 2.4f, 246f), 0.5f) }
        drawRect(a.en(Offset(144f, 233f), oscura), Offset(80f, 231f), Size(128f, 3.4f))
        // El tablero, grueso, con su faldón.
        val tablero = poligono(Offset(86f, 178f), Offset(202f, 178f), Offset(214f, 190f), Offset(74f, 190f))
        planoH(a, tablero, 74f, 214f, 184f, lerp(C.MADERA, C.MURO, 0.22f))
        planoH(a, poligono(Offset(74f, 190f), Offset(214f, 190f), Offset(214f, 195.4f), Offset(74f, 195.4f)), 74f, 214f, 193f, lerp(C.MADERA, C.VIGA, 0.4f))
        drawLine(a.en(Offset(144f, 190f), lerp(C.MADERA, C.YESO, 0.35f)).copy(alpha = 0.7f), Offset(74f, 190f), Offset(214f, 190f), 0.6f)
        clipPath(tablero) {
            // Vetas, cercos de escudilla y años de colores molidos.
            val az = Azar(Semilla.de("renacimiento", "mesa")); val toques = listOf(C.ULTRAMAR, C.OCRE, C.CINABRIO, C.VERDE, C.YESO)
            for (k in 0..4) drawLine(a.en(Offset(144f, 184f), lerp(C.MADERA, C.VIGA, 0.4f)).copy(alpha = 0.3f), Offset(78f, 180f + k * 2.4f), Offset(212f, 180.6f + k * 2.3f), 0.4f)
            repeat(18) { val q = Offset(az.entre(80f, 208f), az.entre(179f, 189f)); drawOval(a.en(q, toques[it % 5]).copy(alpha = 0.34f), q - Offset(2.6f, 0.8f), Size(az.entre(2.4f, 6f), az.entre(0.9f, 1.8f))) }
        }
        // La lámpara de aceite, de latón: el pie, el fuste y el depósito con su pico.
        val laton = lerp(C.ORO, C.VIGA, 0.35f); val l0 = Offset(97f, 187f)
        drawOval(a.en(l0, laton), Offset(l0.x - 5.4f, l0.y - 1.6f), Size(10.8f, 3.2f)); drawLine(a.en(l0, laton), l0, Offset(97f, 172f), 1.6f)
        val deposito = Path().apply { moveTo(92f, 169f); lineTo(102.6f, 169f); lineTo(105f, 167.6f); lineTo(104f, 172f); cubicTo(101f, 174f, 93f, 174f, 92f, 169f); close() }
        cuerpo(a, deposito, 92f, 105f, 171f, laton); drawLine(a.en(l0, lerp(C.ORO, C.YESO, 0.4f)).copy(alpha = 0.7f), Offset(93f, 169.4f), Offset(102f, 169.4f), 0.5f)
        if (R.lampara(m)) resplandor(LUZ_LAMPARA, 9f, 0.6f, C.LAMPARA)
        // Una escudilla con huevos, para el temple.
        val esc = Offset(110f, 183.6f)
        for ((dx, dy) in listOf(-2.2f to -1.8f, 1.8f to -2f, 0f to -3.2f)) drawOval(a.en(esc, lerp(C.YESO, C.CARNE, 0.3f)), esc + Offset(dx - 1.5f, dy - 1.1f), Size(3f, 2.3f))
        cuerpo(a, Path().apply { moveTo(esc.x - 5f, esc.y - 1.6f); lineTo(esc.x + 5f, esc.y - 1.6f); cubicTo(esc.x + 4f, esc.y + 2.4f, esc.x - 4f, esc.y + 2.4f, esc.x - 5f, esc.y - 1.6f); close() }, esc.x - 5f, esc.x + 5f, esc.y, lerp(C.COTTO, C.MURO, 0.3f))
        if (campo) {
            // Sin él: el hueco limpio donde estaba la losa, que se fue en la mula, y su banqueta patas arriba.
            drawPath(poligono(Offset(121f, 181f), Offset(157f, 181f), Offset(161f, 188f), Offset(117f, 188f)), a.en(Offset(139f, 184f), lerp(C.MADERA, C.YESO, 0.35f)).copy(alpha = 0.7f))
            banqueta(a, Offset(140f, 162f), true)
        } else {
            // La losa de pórfido y su moleta: en ella, el color que se muele hoy.
            val losa = poligono(Offset(121f, 180.6f), Offset(157f, 180.6f), Offset(161f, 187.6f), Offset(117f, 187.6f))
            planoH(a, losa, 117f, 161f, 184f, C.PORFIDO); drawPath(poligono(Offset(117f, 187.6f), Offset(161f, 187.6f), Offset(161f, 190.2f), Offset(117f, 190.2f)), a.en(Offset(139f, 189f), lerp(C.PORFIDO, C.CARBON, 0.35f)))
            clipPath(losa) { val az = Azar(Semilla.de("renacimiento", "porfido")); repeat(30) { drawCircle(a.en(Offset(139f, 184f), lerp(C.PORFIDO, C.YESO, 0.5f)).copy(alpha = 0.6f), 0.35f, Offset(az.entre(118f, 160f), az.entre(181f, 187.4f))) } }
            val hoy = m.azar("molienda").entero(if (R.ultramar(m)) 5 else 4); val color = listOf(C.OCRE, lerp(C.VERDE, C.YESO, 0.2f), C.CINABRIO, lerp(C.CARBON, C.VIGA, 0.3f), C.ULTRAMAR)[hoy]
            val muele = if (R.hoyEs(m, "ultramar")) lerp(C.PIEDRA, C.ULTRAMAR, 0.35f) else color
            drawOval(a.en(Offset(134f, 184f), muele), Offset(123f, 181.8f), Size(22f, 4.8f)); drawOval(a.en(Offset(134f, 184f), lerp(muele, C.YESO, 0.3f)).copy(alpha = 0.6f), Offset(126f, 182.4f), Size(10f, 2f))
            if (R.ultramar(m)) drawOval(a.en(Offset(152f, 186f), C.ULTRAMAR).copy(alpha = 0.6f), Offset(149f, 185.2f), Size(8f, 1.8f))
            val moleta = Path().apply { moveTo(146f, 185.4f); cubicTo(145f, 179f, 147f, 174.6f, 150.6f, 174.6f); cubicTo(154.2f, 174.6f, 156.2f, 179f, 155.2f, 185.4f); cubicTo(152f, 186.6f, 149f, 186.6f, 146f, 185.4f); close() }
            cuerpo(a, moleta, 145f, 156.2f, 180f, lerp(C.PORFIDO, C.PIEDRA, 0.4f)); drawLine(a.en(Offset(150f, 185f), muele), Offset(146.6f, 185.6f), Offset(154.6f, 185.6f), 0.9f)
        }
        if (R.comiendo(m)) {
            // La hora de comer: el pan sin sal, la escudilla de sopa y la jarra de vino aguado.
            val pan = Path().apply { moveTo(160f, 187f); cubicTo(160f, 181f, 176f, 181f, 176f, 187f); close() }
            cuerpo(a, pan, 160f, 176f, 184f, lerp(C.OCRE, C.MADERA, 0.4f)); drawLine(a.en(Offset(168f, 184f), lerp(C.OCRE, C.YESO, 0.4f)), Offset(164f, 184f), Offset(172f, 183.4f), 0.6f)
            val jarra = Path().apply { moveTo(165f, 180f); cubicTo(162.4f, 176f, 163.4f, 170f, 165f, 168f); lineTo(170f, 168f); cubicTo(171.6f, 170f, 172.6f, 176f, 170f, 180f); close() }
            cuerpo(a, jarra, 163f, 172.6f, 174f, lerp(C.COTTO, C.OCRE, 0.2f)); drawArc(a.en(Offset(172f, 174f), C.COTTO), -80f, 160f, false, Offset(169.4f, 170.4f), Size(5f, 6.4f), style = Stroke(1f))
        } else {
            // Las conchas con el color ya molido, y el bote de los pinceles.
            val colores = listOf(if (R.ultramar(m)) C.ULTRAMAR else lerp(C.ULTRAMAR, C.VERDE, 0.5f), C.CINABRIO, C.OCRE)
            for ((k, o) in listOf(Offset(164f, 183f), Offset(169.6f, 187.4f), Offset(160f, 187.6f)).withIndex()) {
                drawOval(a.en(o, lerp(C.YESO, C.CARNE, 0.25f)), o - Offset(3.2f, 1.5f), Size(6.4f, 3f)); drawOval(a.en(o, colores[k]), o - Offset(2.2f, 0.9f), Size(4.4f, 1.8f)); drawOval(a.en(o, lerp(C.YESO, C.PIEDRA, 0.3f)), o - Offset(3.2f, 1.5f), Size(6.4f, 3f), style = Stroke(0.4f))
            }
            val bote = Offset(168.6f, 180.4f)
            for ((k, dx) in listOf(-2.2f, -0.6f, 1f, 2.4f).withIndex()) { drawLine(a.en(bote, lerp(C.MADERA, C.YESO, 0.2f)), bote + Offset(dx * 0.5f, -5f), bote + Offset(dx * 1.6f, -14f - k % 2 * 2.6f), 0.7f); drawLine(a.en(bote, if (k % 2 == 0) lerp(C.VIGA, C.CARBON, 0.3f) else C.OCRE), bote + Offset(dx * 1.6f, -14f - k % 2 * 2.6f), bote + Offset(dx * 1.76f, -16.6f - k % 2 * 2.6f), 1.1f, StrokeCap.Round) }
            cuerpo(a, poligono(bote + Offset(-3f, -6.4f), bote + Offset(3f, -6.4f), bote + Offset(2.6f, 0f), bote + Offset(-2.6f, 0f)), bote.x - 3f, bote.x + 3f, bote.y - 3f, C.COTTO)
        }
        with(ObjetosRenacimiento) { objeto(m.ficha.objeto, MESA, 1.36f) { a.en(MESA + Offset(0f, -6f), it) } }
    }

    fun DrawScope.cosasDelTaller(m: Momento, a: Alumbrado, f: List<Foco>) {
        val cosas = cosas(m, a).sortedBy { it.pie.y }
        // Las sombras: con sol dentro, todas hacia el mismo lado; si no, las tumba la luz que más alumbra a cada cosa.
        capaSombra(rayoFijo(-0.6f, 40f, 0.66f, C.SOMBRA)) { c ->
            clipPath(T.E.formaDelSuelo()) { for (k in cosas) R.rayoEn(m, f, k.pie, 1f)?.let { r -> sombraArrojada(r, k.forma, k.pie, c.copy(alpha = r.fuerza.coerceIn(0f, 1f)), T.SUELO, T.SUELO) } }
        }
        for (k in cosas) { val b = k.forma.getBounds(); drawOval(C.SOMBRA.copy(alpha = 0.32f), Offset(b.left - 1f, k.pie.y - 2.4f), Size(b.width + 2f, 5f), blendMode = BlendMode.Multiply) }
        cosas.forEach { it.pintar(this) }
    }

    /**
     * Pinta [dibujo] (la mancha del sol) solo donde no cae la sombra que cada cosa del taller arroja con el rayo [r]:
     * así la mesa, los caballetes y la banqueta cortan la mancha. La sombra de cada cosa es su silueta tumbada igual
     * que en `sombraArrojada`; aquí se usa de recorte en vez de pintarse.
     */
    fun DrawScope.sinLasSombras(m: Momento, a: Alumbrado, r: Rayo, dibujo: DrawScope.() -> Unit) {
        val cosas = cosas(m, a)
        if (kotlin.math.abs(r.ay) < 0.05f) { dibujo(); return }
        fun desde(i: Int) {
            if (i == cosas.size) { dibujo(); return }
            val k = cosas[i]; val pie = k.pie.y
            val ida = Matrix().apply { values[4] = -r.ax; values[12] = r.ax * pie; values[5] = -r.ay; values[13] = pie * (1f + r.ay) }
            val vuelta = Matrix().apply { values[4] = -r.ax / r.ay; values[12] = r.ax * pie / r.ay; values[5] = -1f / r.ay; values[13] = pie * (1f + r.ay) / r.ay }
            withTransform({ transform(ida) }) { clipPath(k.forma, ClipOp.Difference) { withTransform({ transform(vuelta) }) { desde(i + 1) } } }
        }
        desde(0)
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.llamas(m: Momento, reloj: Float) {
        if (R.lampara(m)) with(T) { llamaDeLaLampara(reloj) }
        if (R.lumbre(m)) {
            for ((i, dx) in listOf(-9f, 0f, 9f).withIndex()) llama(Offset(HORNILLO + dx, PIE_HORNILLO.y - 20f), 7f + (i % 2) * 2.2f, reloj, 20 + i)
            // El vaho de la cola, que hierve despacio.
            if (R.trabajando(m)) humo(R.luces(m), PIE_HORNILLO + Offset(0f, -40f), 30f, 9f, reloj, 0f, 0.04f, 0f, 5, 6)
        } else if (R.rescoldo(m)) {
            brasas(R.luces(m), PIE_HORNILLO + Offset(0f, -20f), 28f, reloj)
            drawCircle(Color(0xFFFF8A4A).copy(alpha = 0.16f + 0.1f * sin(reloj * 1.7f)), 12f, PIE_HORNILLO + Offset(0f, -21f))
        }
    }

    // ---------------------------------------------------------------- El gato

    /** Lo plantado en el tramo del gato: la banqueta (cuando está en el suelo) y el hornillo. */
    fun estorbos(m: Momento): List<Float> = buildList { if (!R.enElCampo(m)) add(BANQUETA); add(HORNILLO) }

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 16f, PISO_GATO, hasta = 364f))
        add(Ancla(TipoAncla.MIRADOR, 334f, 218f))                                // encima del arcón
        add(Ancla(TipoAncla.OBJETO, MESA.x - 12f, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, 140f, PISO_GATO))                            // debajo de la mesa de moler
        if (R.lumbre(m)) add(Ancla(TipoAncla.FUEGO, 312f, PISO_GATO, hasta = HORNILLO))
        if (R.fiesta(m)) add(Ancla(TipoAncla.ADORNO, 198f, 262f))
    }
}
