package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Refugio: una placa esmaltada de las de antes de la guerra, como los carteles de las paredes del refugio («amarillos,
 * rotos por las esquinas», dice el cuento de la residente): crema, de esquinas muy redondas (la «pantalla de tubo» del
 * tema), con su borde verde azulado y desconchada, con el hierro y el óxido a la vista en las esquinas. En ella, en
 * relieve, una llave de paso de latón con su rueda roja («válvulas con ruedas rojas», las de su sala de bombas) y la gota
 * de agua que cae del caño, que es su oficio; debajo, la franja amarilla y negra de aviso. (Una rueda roja con la gota en
 * medio, sin más, se leía como una señal de prohibido.) Cuelga ladeada de dos cadenitas de un tramo de tubo con
 * sus bridas, como todo en la sala de bombas, y del tubo cuelga una gota de humedad. Sin letras ni cifras (ni engranes ni
 * números: nada de los refugios de otras obras). Al mantener el dedo sube desde abajo el fósforo verde de «la pantalla
 * verde» de la terminal (el tema de noche), con sus líneas de barrido, y lo que queda debajo pasa a tinta de pantalla
 * para que se siga viendo.
 * **[Diseño propio]**: el tema de pixel no tiene banderita; los colores son los del tema (`Disenios.kt`: crema #F1E8D0,
 * verde azulado #1C6E6A, tinta #1F2A2A, fósforo #3DFF7F, #9CF5B0, #1E8A45, pantalla #070D08) y los de la sala de bombas
 * del cuadro (el rojo de las ruedas #B53A2D, el latón #C59B4C, la cinta amarilla #E5B436, el acero #8A9294, el agua).
 */
internal object BanderitaRefugio : Banderita {
    private val ESMALTE = Color(0xFFF1E8D0)
    private val ESMALTE_LUZ = Color(0xFFFBF7EA)
    private val ESMALTE_OSC = Color(0xFFD6C8A0)
    private val VERDE = Color(0xFF1C6E6A)
    private val HIERRO = Color(0xFF1F2A2A)
    private val HIERRO_AZUL = Color(0xFF2A3540)
    private val OXIDO = Color(0xFF8E4A24)
    private val ROJO = Color(0xFFB53A2D)
    private val ROJO_LUZ = Color(0xFFE0705A)
    private val ROJO_OSC = Color(0xFF7A2219)
    private val LATON = Color(0xFFC59B4C)
    private val LATON_LUZ = Color(0xFFEBD08C)
    private val LATON_OSC = Color(0xFF7A5A1E)
    private val AGUA = Color(0xFF3A8FB0)
    private val AGUA_LUZ = Color(0xFFC4E8F0)
    private val AGUA_HONDA = Color(0xFF1D5874)
    private val AMARILLO = Color(0xFFE5B436)
    private val ACERO = Color(0xFF8A9294)
    private val ACERO_LUZ = Color(0xFFCDD3D2)
    private val ACERO_OSC = Color(0xFF4C5752)
    private val FOSFORO = Color(0xFF3DFF7F)
    private val FOSFORO_LUZ = Color(0xFF9CF5B0)
    private val FOSFORO_HONDO = Color(0xFF1E8A45)
    private val PANTALLA = Color(0xFF070D08)
    private val TINTA_PANTALLA = Color(0xFF0B3318)

    /** La placa: sus orillas, su redondeo y de dónde cuelga (el medio de arriba). */
    private const val IZQ = 2.8f
    private const val DER = 23.2f
    private const val ARRIBA = 6.8f
    private const val ABAJO = 38.4f
    private const val RADIO = 3.8f
    private const val MEDIO = 13f
    /** Cuánto se ladea (grados): la cadena de la izquierda cuelga un poco más. */
    private const val LADEO = -2f
    /** Los dos agujeros por los que cuelga, en la placa. */
    private val AGUJEROS = listOf(Offset(6.4f, 8.2f), Offset(19.6f, 8.2f))
    /** La llave de paso: la rueda, el cuerpo de la válvula, la boca del caño y la gota que cae; y la franja de aviso. */
    private val RUEDA = Offset(11.4f, 15f)
    private const val R_RUEDA = 4.4f
    private val CUERPO = Offset(11.4f, 22f)
    private val BOCA = Offset(16.5f, 25.3f)
    private val GOTA = Offset(16.5f, 28.4f)
    private const val FRANJA_ARRIBA = 31.5f
    private const val FRANJA_ABAJO = 34.3f

    /** Los colores de lo que va pintado en la placa: los suyos, o los de tinta de pantalla bajo el fósforo. */
    private class Tintas(
        val verde: Color, val rojo: Color, val rojoLuz: Color, val rojoOsc: Color, val laton: Color, val latonLuz: Color, val latonOsc: Color,
        val agua: Color, val aguaLuz: Color, val aguaHonda: Color, val amarillo: Color, val negro: Color, val relieve: Color,
    )

    private val PROPIAS = Tintas(
        VERDE, ROJO, ROJO_LUZ, ROJO_OSC, LATON, LATON_LUZ, LATON_OSC, AGUA, AGUA_LUZ, AGUA_HONDA, AMARILLO, HIERRO, HIERRO.copy(alpha = 0.3f),
    )
    private val DE_PANTALLA = Tintas(
        TINTA_PANTALLA, TINTA_PANTALLA, FOSFORO_LUZ, PANTALLA, TINTA_PANTALLA, FOSFORO_LUZ, PANTALLA, FOSFORO_HONDO, FOSFORO_LUZ, TINTA_PANTALLA,
        FOSFORO_LUZ, TINTA_PANTALLA, PANTALLA.copy(alpha = 0.3f),
    )

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            tubo()
            rotate(LADEO, Offset(MEDIO, ARRIBA)) { placa(lleno.coerceIn(0f, 1f)) }
            for (a in AGUJEROS) { val b = enElMundo(a); cadena(Offset(b.x, 3.9f), b) }
        }
    }

    /** Dónde queda un punto de la placa ([p]) una vez ladeada. */
    private fun enElMundo(p: Offset): Offset {
        val a = LADEO * PI.toFloat() / 180f
        val d = p - Offset(MEDIO, ARRIBA)
        return Offset(MEDIO + d.x * cos(a) - d.y * sin(a), ARRIBA + d.x * sin(a) + d.y * cos(a))
    }

    private fun forma(dx: Float = 0f, dy: Float = 0f): Path = Path().apply {
        addRoundRect(RoundRect(IZQ + dx, ARRIBA + dy, DER + dx, ABAJO + dy, CornerRadius(RADIO)))
    }

    /** La placa: su sombra, el canto de hierro, el esmalte, lo pintado, el desgaste, el brillo y los agujeros. */
    private fun DrawScope.placa(l: Float) {
        val cuerpo = forma()
        val nivel = ABAJO + 0.4f - (ABAJO - ARRIBA + 0.8f) * l
        drawPath(forma(0.9f, 1.3f), Color.Black.copy(alpha = 0.3f))
        drawPath(forma(0.35f, 0.45f), HIERRO)
        clipPath(cuerpo) {
            drawRect(Brush.radialGradient(listOf(ESMALTE_LUZ, ESMALTE, ESMALTE_OSC), Offset(8f, 12f), 32f), Offset.Zero, Size(26f, 40f))
            // Lo que se ha llenado: el fósforo, más hondo abajo.
            if (l > 0f) drawRect(Brush.verticalGradient(listOf(FOSFORO_LUZ, FOSFORO, FOSFORO_HONDO), nivel, ABAJO + 0.4f), Offset(0f, nivel), Size(26f, 40f - nivel))
        }
        clipPath(cuerpo) {
            clipRect(0f, 0f, 26f, nivel) { pintado(PROPIAS) }
            if (nivel < 40f) clipRect(0f, nivel, 26f, 40f) { pintado(DE_PANTALLA) }
            if (l > 0f) barrido(nivel)
            desgaste()
            clipRect(0f, 0f, 26f, nivel) { brillo() }
        }
        drawPath(cuerpo, HIERRO.copy(alpha = 0.55f), style = Stroke(0.4f))
        for (a in AGUJEROS) {
            drawCircle(HIERRO, 0.7f, a)
            drawCircle(ACERO, 0.95f, a, style = Stroke(0.4f))
            drawCircle(ACERO_LUZ, 0.95f, a + Offset(-0.1f, -0.12f), style = Stroke(0.15f))
        }
    }

    /** Lo pintado en el esmalte: el borde verde con su filete, la llave de paso con su gota y la franja de aviso. */
    private fun DrawScope.pintado(t: Tintas) {
        drawRoundRect(t.verde, Offset(IZQ + 1.35f, ARRIBA + 1.35f), Size(DER - IZQ - 2.7f, ABAJO - ARRIBA - 2.7f), CornerRadius(RADIO - 1.3f), style = Stroke(1.5f))
        drawRoundRect(t.verde, Offset(IZQ + 2.9f, ARRIBA + 2.9f), Size(DER - IZQ - 5.8f, ABAJO - ARRIBA - 5.8f), CornerRadius(RADIO - 2.4f), style = Stroke(0.3f))
        grifo(t)
        franja(t)
    }

    /** El latón de la llave de paso: el tubo que sale de la pared, el cuerpo de la válvula y el caño que baja hasta la boca. */
    private fun canio(): Path {
        val y0 = CUERPO.y - 0.8f; val y1 = CUERPO.y + 0.8f
        val tubo = Path().apply { addRect(Rect(IZQ + 2.9f, y0, CUERPO.x, y1)) }
        val cuerpo = Path().apply { addRoundRect(RoundRect(CUERPO.x - 2.6f, CUERPO.y - 1.8f, CUERPO.x + 2.6f, CUERPO.y + 1.8f, CornerRadius(1.4f))) }
        val cano = Path().apply {
            moveTo(CUERPO.x, y0); lineTo(BOCA.x - 0.9f, y0)
            cubicTo(BOCA.x + 0.4f, y0, BOCA.x + 0.8f, y0 + 1f, BOCA.x + 0.8f, y1 + 1.2f)
            lineTo(BOCA.x + 0.8f, BOCA.y); lineTo(BOCA.x - 0.8f, BOCA.y); lineTo(BOCA.x - 0.8f, y1 + 1.2f)
            cubicTo(BOCA.x - 0.8f, y1 + 0.3f, BOCA.x - 1.1f, y1, BOCA.x - 1.8f, y1)
            lineTo(CUERPO.x, y1); close()
        }
        val boca = Path().apply { addRoundRect(RoundRect(BOCA.x - 1.15f, BOCA.y - 0.6f, BOCA.x + 1.15f, BOCA.y + 0.25f, CornerRadius(0.3f))) }
        return listOf(cuerpo, cano, boca).fold(tubo) { a, b -> Path.combine(PathOperation.Union, a, b) }
    }

    /** La llave de paso, en relieve: el latón con su luz, las bridas, el vástago, la rueda roja y la gota que cae. */
    private fun DrawScope.grifo(t: Tintas) {
        val metal = canio()
        val sombra = Offset(0.35f, 0.45f)
        val vastago = CUERPO.y - 1.8f - RUEDA.y
        drawPath(Path().apply { addPath(metal, sombra) }, t.relieve)
        drawRect(t.relieve, Offset(RUEDA.x - 0.55f, RUEDA.y) + sombra, Size(1.1f, vastago))
        drawRect(t.latonOsc, Offset(RUEDA.x - 0.55f, RUEDA.y), Size(1.1f, vastago))
        drawPath(metal, Brush.verticalGradient(listOf(t.latonLuz, t.laton, t.latonOsc), CUERPO.y - 1.8f, BOCA.y + 0.3f))
        drawLine(t.latonLuz, Offset(IZQ + 3.6f, CUERPO.y - 0.4f), Offset(BOCA.x - 1.2f, CUERPO.y - 0.4f), 0.3f, StrokeCap.Round)
        for (x in listOf(IZQ + 3.5f, CUERPO.x - 3.2f, CUERPO.x + 3.2f)) drawRoundRect(t.latonOsc, Offset(x - 0.4f, CUERPO.y - 1.2f), Size(0.8f, 2.4f), CornerRadius(0.25f))
        drawPath(metal, t.latonOsc.copy(alpha = 0.7f), style = Stroke(0.25f))
        drawCircle(t.latonLuz, 0.45f, CUERPO + Offset(-1.3f, -0.9f))
        rueda(t)
        gota(t)
    }

    /** La rueda roja, en relieve: cinco radios, el aro con su luz arriba a la izquierda y el cubo con la tuerca. */
    private fun DrawScope.rueda(t: Tintas) {
        val c = RUEDA; val r = R_RUEDA
        val sombra = Offset(0.35f, 0.45f)
        drawCircle(t.relieve, r, c + sombra, style = Stroke(1.2f))
        for (i in 0 until 5) {
            val a = (-PI / 2 + 2 * PI * i / 5).toFloat()
            val u = Offset(cos(a), sin(a))
            drawLine(t.relieve, c + u + sombra, c + u * (r - 0.3f) + sombra, 0.85f, StrokeCap.Round)
            drawLine(t.rojo, c + u, c + u * (r - 0.3f), 0.85f, StrokeCap.Round)
        }
        drawCircle(t.rojoOsc, r, c + Offset(0.12f, 0.16f), style = Stroke(1.2f))
        drawCircle(t.rojo, r, c, style = Stroke(1.05f))
        val rl = r - 0.25f
        drawArc(t.rojoLuz, 190f, 90f, false, Offset(c.x - rl, c.y - rl), Size(rl * 2f, rl * 2f), style = Stroke(0.38f, cap = StrokeCap.Round))
        drawCircle(t.rojo, 1.45f, c)
        drawArc(t.rojoLuz, 190f, 90f, false, Offset(c.x - 1.2f, c.y - 1.2f), Size(2.4f, 2.4f), style = Stroke(0.25f))
        drawCircle(t.latonOsc, 0.65f, c)
        drawCircle(t.latonLuz, 0.25f, c + Offset(-0.2f, -0.2f))
    }

    /** La gota de agua con [c] en su medio y [alto] de alto: punta arriba, panza abajo. */
    private fun silueta(c: Offset, alto: Float): Path = Path().apply {
        val w = alto * 0.36f; val arriba = c.y - alto * 0.5f; val abajo = c.y + alto * 0.5f
        moveTo(c.x, arriba)
        cubicTo(c.x + w * 0.15f, arriba + alto * 0.3f, c.x + w, c.y - alto * 0.04f, c.x + w, c.y + alto * 0.17f)
        cubicTo(c.x + w, c.y + alto * 0.4f, c.x + w * 0.55f, abajo, c.x, abajo)
        cubicTo(c.x - w * 0.55f, abajo, c.x - w, c.y + alto * 0.4f, c.x - w, c.y + alto * 0.17f)
        cubicTo(c.x - w, c.y - alto * 0.04f, c.x - w * 0.15f, arriba + alto * 0.3f, c.x, arriba)
        close()
    }

    /** La gota que cae del caño (y la que ya asoma en la boca): el agua con su luz, su reflejo a la izquierda y un brillo. */
    private fun DrawScope.gota(t: Tintas) {
        val c = GOTA; val alto = 4.4f
        drawPath(silueta(c + Offset(0.35f, 0.45f), alto), t.relieve)
        val g = silueta(c, alto)
        drawPath(g, Brush.linearGradient(listOf(t.aguaLuz, t.agua, t.aguaHonda), Offset(c.x - 1.6f, c.y - 2f), Offset(c.x + 1.6f, c.y + 2.4f)))
        drawPath(Path().apply { moveTo(c.x - 0.95f, c.y + 0.3f); cubicTo(c.x - 1.15f, c.y + 1.1f, c.x - 0.7f, c.y + 1.7f, c.x + 0.05f, c.y + 1.85f) }, t.aguaLuz.copy(alpha = 0.85f), style = Stroke(0.35f, cap = StrokeCap.Round))
        drawCircle(t.aguaLuz, 0.3f, Offset(c.x - 0.4f, c.y - 0.6f))
        drawPath(g, t.aguaHonda.copy(alpha = 0.7f), style = Stroke(0.25f))
        drawCircle(t.agua, 0.42f, BOCA + Offset(0f, 0.6f))
        drawCircle(t.aguaLuz, 0.14f, BOCA + Offset(-0.15f, 0.45f))
    }

    /** La franja de aviso, amarilla y negra en diagonal, con su marco. */
    private fun DrawScope.franja(t: Tintas) {
        val x0 = IZQ + 3.7f; val x1 = DER - 3.7f; val alto = FRANJA_ABAJO - FRANJA_ARRIBA
        drawRect(t.relieve, Offset(x0 + 0.35f, FRANJA_ARRIBA + 0.45f), Size(x1 - x0, alto))
        clipRect(x0, FRANJA_ARRIBA, x1, FRANJA_ABAJO) {
            drawRect(t.amarillo, Offset(x0, FRANJA_ARRIBA), Size(x1 - x0, alto))
            var x = x0 - 4f
            while (x < x1 + 1f) {
                drawPath(Path().apply { moveTo(x, FRANJA_ABAJO); lineTo(x + 1.5f, FRANJA_ABAJO); lineTo(x + 1.5f + alto, FRANJA_ARRIBA); lineTo(x + alto, FRANJA_ARRIBA); close() }, t.negro)
                x += 3f
            }
        }
        drawRect(t.negro, Offset(x0, FRANJA_ARRIBA), Size(x1 - x0, alto), style = Stroke(0.35f))
    }

    /** Las líneas de barrido del fósforo y el filo de luz que sube. */
    private fun DrawScope.barrido(nivel: Float) {
        clipRect(0f, nivel, 26f, 40f) {
            var y = nivel + 0.6f
            while (y < 40f) { drawLine(PANTALLA.copy(alpha = 0.24f), Offset(0f, y), Offset(26f, y), 0.28f); y += 0.9f }
        }
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, FOSFORO.copy(alpha = 0.4f)), nivel - 2.4f, nivel), Offset(0f, nivel - 2.4f), Size(26f, 2.4f))
        drawLine(Color.White.copy(alpha = 0.85f), Offset(0f, nivel), Offset(26f, nivel), 0.4f)
    }

    /** Los desconchones del esmalte, con el hierro y el óxido a la vista: (medio, radio). */
    private val DESCONCHONES = listOf(
        Offset(IZQ + 0.7f, ARRIBA + 0.9f) to 1.7f, Offset(DER - 0.2f, 22.6f) to 1.0f, Offset(DER - 0.9f, ABAJO - 0.6f) to 2.0f,
        Offset(IZQ + 0.3f, 31.6f) to 0.85f, Offset(17.4f, 26.4f) to 0.45f, Offset(8.6f, 35.4f) to 0.5f,
    )

    /** Los desconchones, la chorrera de óxido bajo un agujero y una grieta fina del esmalte. */
    private fun DrawScope.desgaste() {
        for ((i, d) in DESCONCHONES.withIndex()) desconchon(d.first, d.second, i)
        for ((dx, largo) in listOf(-0.2f to 5.2f, 0.35f to 3.4f)) {
            val x = AGUJEROS[1].x + dx; val y = AGUJEROS[1].y + 0.6f
            drawRect(Brush.verticalGradient(listOf(OXIDO.copy(alpha = 0.5f), Color.Transparent), y, y + largo), Offset(x - 0.25f, y), Size(0.5f, largo))
        }
        val g = DESCONCHONES[2].first
        drawPath(Path().apply { moveTo(g.x - 1.6f, g.y - 1f); lineTo(g.x - 3.2f, g.y - 2.2f); lineTo(g.x - 3.9f, g.y - 4.1f); lineTo(g.x - 5.4f, g.y - 4.8f) }, HIERRO.copy(alpha = 0.4f), style = Stroke(0.16f))
    }

    /** Un desconchón: un borde roto, el hierro oscuro dentro, el cerco de óxido y el filo del esmalte que coge la luz. */
    private fun DrawScope.desconchon(c: Offset, r: Float, i: Int) {
        val roto = Path().apply {
            for (j in 0 until 8) {
                val a = (2 * PI * j / 8).toFloat() + i * 0.7f
                val rr = r * (0.7f + 0.3f * ((j * 37 + i * 11) % 5) / 4f)
                val q = c + Offset(cos(a) * rr, sin(a) * rr * 0.85f)
                if (j == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
            }
            close()
        }
        drawPath(roto, OXIDO.copy(alpha = 0.55f), style = Stroke(0.55f))
        drawPath(roto, HIERRO_AZUL)
        drawPath(Path().apply { addPath(roto, Offset(0.18f, 0.22f)) }, ESMALTE_LUZ.copy(alpha = 0.55f), style = Stroke(0.16f))
        drawCircle(OXIDO.copy(alpha = 0.7f), r * 0.28f, c + Offset(r * 0.2f, r * 0.15f))
    }

    /** El brillo del esmalte: una franja en diagonal arriba a la izquierda y la luz del canto de arriba. */
    private fun DrawScope.brillo() {
        drawPath(Path().apply { moveTo(IZQ + 4.2f, ARRIBA - 1f); lineTo(IZQ + 8.6f, ARRIBA - 1f); lineTo(IZQ - 1f, ARRIBA + 17f); lineTo(IZQ - 5.4f, ARRIBA + 17f); close() }, Color.White.copy(alpha = 0.16f))
        drawLine(Color.White.copy(alpha = 0.55f), Offset(IZQ + RADIO, ARRIBA + 0.5f), Offset(DER - RADIO - 1.5f, ARRIBA + 0.5f), 0.35f, StrokeCap.Round)
        drawLine(Color.White.copy(alpha = 0.35f), Offset(IZQ + 0.5f, ARRIBA + RADIO), Offset(IZQ + 0.5f, 24f), 0.3f, StrokeCap.Round)
    }

    /** El tramo de tubo de acero del que cuelga: su luz, una mancha de óxido, sus dos bridas con pernos y una gota de humedad. */
    private fun DrawScope.tubo() {
        drawRoundRect(Brush.verticalGradient(listOf(ACERO_LUZ, ACERO, ACERO_OSC), 0.8f, 4f), Offset(1.2f, 0.8f), Size(23.6f, 3.2f), CornerRadius(0.6f))
        drawLine(Color.White.copy(alpha = 0.4f), Offset(2f, 1.5f), Offset(24f, 1.5f), 0.4f, StrokeCap.Round)
        drawOval(OXIDO.copy(alpha = 0.35f), Offset(9.4f, 2.5f), Size(2.8f, 1.3f))
        drawOval(OXIDO.copy(alpha = 0.25f), Offset(15.8f, 1.2f), Size(1.6f, 0.9f))
        for (x in listOf(1.0f, 25.0f)) {
            drawRoundRect(Brush.horizontalGradient(listOf(ACERO_LUZ, ACERO, ACERO_OSC), x - 1f, x + 1f), Offset(x - 1f, 0.1f), Size(2f, 4.6f), CornerRadius(0.4f))
            for (y in listOf(0.95f, 3.75f)) { drawCircle(ACERO_OSC, 0.38f, Offset(x, y)); drawCircle(ACERO_LUZ, 0.16f, Offset(x - 0.12f, y - 0.12f)) }
        }
        // La gota de humedad que cuelga del tubo, entre las dos cadenas.
        val g = Offset(15.2f, 4.5f)
        drawPath(silueta(g, 1.4f), AGUA_LUZ.copy(alpha = 0.9f))
        drawPath(silueta(g, 1.4f), AGUA.copy(alpha = 0.8f), style = Stroke(0.15f))
        drawCircle(Color.White, 0.14f, g + Offset(-0.12f, 0.1f))
    }

    /** Una cadenita de [a] (el tubo) a [b] (el agujero): eslabones de cara y de canto, y la vuelta de alambre al tubo. */
    private fun DrawScope.cadena(a: Offset, b: Offset) {
        drawLine(HIERRO, Offset(a.x - 0.55f, 0.75f), Offset(a.x + 0.45f, 3.95f), 0.45f, StrokeCap.Round)
        val n = maxOf(3, ((b.y - a.y) / 1.1f).toInt())
        for (i in 0 until n) {
            val c = a + (b - a) * ((i + 0.5f) / n)
            if (i % 2 == 0) {
                drawOval(HIERRO, Offset(c.x - 0.5f, c.y - 0.78f), Size(1f, 1.56f), style = Stroke(0.34f))
                drawLine(ACERO_LUZ.copy(alpha = 0.6f), Offset(c.x - 0.36f, c.y - 0.4f), Offset(c.x - 0.36f, c.y + 0.1f), 0.16f)
            } else {
                drawLine(HIERRO, c - Offset(0f, 0.75f), c + Offset(0f, 0.75f), 0.46f, StrokeCap.Round)
                drawLine(ACERO.copy(alpha = 0.7f), c - Offset(0.08f, 0.55f), c + Offset(-0.08f, 0.4f), 0.15f)
            }
        }
        drawArc(HIERRO, 180f, 200f, false, Offset(b.x - 0.6f, b.y - 0.9f), Size(1.2f, 1.4f), style = Stroke(0.34f))
    }
}
