package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.ALERO_ALTO
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.S_ALTO
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.S_FIN
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.aleroEn
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.cara
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.p
import io.github.ruddy2003.vestigium.ui.escenas.PuertaAntiguas.puerta
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar

/**
 * La posada del Cuerno, el marco de la derecha: «una casa larga de piedra y madera al pie del paso, con un cuerno de
 * carnero clavado sobre la puerta». En tres cuartos ([FachadaPosada]): el zaguán de una planta, de piedra gris sin labrar
 * en hiladas desiguales, con la puerta (en [PuertaAntiguas]) y su tejado de losas de una sola agua; a la derecha, el cuerpo
 * alto, que sale por el borde: la planta de piedra con el ventanuco de la sala y el poyo, el piso de tablas viejas con la
 * ventana del cuarto alto («la que da al paso») y el alero de losas, que sube hacia la esquina de arriba; entre los dos,
 * la chimenea de la lumbre de la sala, de piedra.
 */
internal object PosadaAntiguas {
    private val C = ColorAntiguas

    /** De dónde sale el humo de la chimenea. */
    val CHIMENEA: Offset get() = p(0.25f, CHIMENEA_ALTO + 0.1f, -0.25f)
    private const val CHIMENEA_ALTO = 4.15f

    /** El piso de tablas del cuerpo alto empieza aquí (en metros). */
    const val PISO = 2.55f

    /** Lo que se ve del cuerpo alto: de su esquina al borde derecho del lienzo, y algo más. */
    private const val S_BORDE = -1.6f

    /** La forma de todo el muro (los dos cuerpos), para las sombras y los recortes. */
    val MURO: Path by lazy { poligono(listOf(p(S_FIN, -0.1f), p(S_BORDE, -0.1f), p(S_BORDE, ALERO_ALTO), p(S_ALTO, ALERO_ALTO), p(S_ALTO, aleroEn(S_ALTO)), p(S_FIN, aleroEn(S_FIN)))) }

    fun DrawScope.posada(v: VistaAntiguas) {
        val az = SitioAntiguas.fijo("posada")
        chimenea(v)
        tejadoAlto(v, az)
        muroDePiedra(v, S_FIN, S_BORDE, az)
        pisoDeTablas(v, az)
        esquinas(v)
        cuartoAlto(v)
        ventanuco(v)
        puerta(v, az)
        aleroDelZaguan(v, az)
        aleroAlto(v, az)
        pieDelMuro(v, az)
        if (v.clima == io.github.ruddy2003.vestigium.vidas.Clima.LLUVIA || v.clima == io.github.ruddy2003.vestigium.vidas.Clima.TORMENTA) mojada(v)
    }

    /** Con lluvia: la piedra oscura de agua, más abajo que arriba, y el brillo del cielo en las losas de los tejados y en el canto de los aleros. */
    private fun DrawScope.mojada(v: VistaAntiguas) {
        drawPath(MURO, Brush.verticalGradient(listOf(C.ESCORIA.copy(alpha = 0.12f), C.ESCORIA.copy(alpha = 0.28f)), p(0.5f, 2.6f).y, p(0.5f, 0f).y))
        val brillo = lerp(v.l.alto, Color.White, 0.35f)
        for (s0 in listOf(FachadaPosada.S_ALTO, 0.6f, 1.4f)) drawLine(brillo.copy(alpha = 0.4f), p(s0, aleroEn(s0) + 0.15f, 0.36f), p(s0 + 0.55f, aleroEn(s0 + 0.55f) + 0.15f, 0.36f), 0.9f, StrokeCap.Round)
        drawLine(brillo.copy(alpha = 0.45f), p(S_ALTO + 0.3f, ALERO_ALTO + 0.12f, 0.62f), p(S_BORDE, ALERO_ALTO + 0.12f, 0.62f), 1f)
        // El agua que cae del alero del zaguán, en hilos.
        var s = S_ALTO + 0.1f
        while (s < S_FIN) { val q = p(s, aleroEn(s), 0.36f); drawLine(brillo.copy(alpha = 0.35f), q, q + Offset(0f, 9f), 0.5f); s += 0.33f }
    }

    /** El pincel de un punto del muro, con la lumbre de la puerta de noche: más cuanto más cerca de la puerta. */
    fun pincel(v: VistaAntiguas, s: Float, h: Float, fuera: Float = 0f): Pincel {
        val cerca = (1f - kotlin.math.abs(s - 0.5f) / 1.6f).coerceIn(0f, 1f) * (1f - (h / 3f)).coerceIn(0.2f, 1f)
        return v.pincelEn(p(s, h, fuera), lumbre = (v.encendido.sala * v.noche * 0.75f * cerca).coerceIn(0f, 1f))
    }

    /** La luz que recibe la cara del muro (mira a la plaza: al sol de la tarde). */
    fun luzDeCara(v: VistaAntiguas): Float = v.sol.t(FachadaPosada.CARA)

    // ---------------------------------------------------------------- La piedra

    /**
     * El muro de piedra de los dos cuerpos: piedra gris sin labrar, en hiladas desiguales (unas altas y otras bajas, alguna
     * piedra grande que ocupa dos), cada una con su tono, su labio de luz arriba y su junta en sombra abajo, sobre la
     * argamasa vieja; las manchas de agua que bajan del alero y el zócalo más oscuro.
     */
    private fun DrawScope.muroDePiedra(v: VistaAntiguas, s0: Float, s1: Float, az: Azar) {
        val t = luzDeCara(v)
        val piedra = lerp(lerp(C.PIEDRA, C.LANA, 0.12f), C.TIERRA, 0.08f)
        val argamasa = lerp(piedra, C.TIERRA, 0.35f)
        // La argamasa (lo que se ve entre piedra y piedra), en el zaguán hasta su alero y en el cuerpo alto hasta el piso.
        val zaguan = poligono(listOf(p(s0, -0.1f), p(S_ALTO, -0.1f), p(S_ALTO, aleroEn(S_ALTO)), p(s0, aleroEn(s0))))
        val bajo = cara(S_ALTO, s1, -0.1f, PISO)
        drawPath(zaguan, pincel(v, 1.2f, 1.2f).por(argamasa, t - 0.25f))
        drawPath(bajo, pincel(v, -0.6f, 1.2f).por(argamasa, t - 0.25f))
        var h = -0.04f
        while (h < aleroEn(S_ALTO) + 0.05f) {
            val alto = az.entre(0.17f, 0.3f)
            var s = s0 - az.entre(0f, 0.18f)
            while (s > s1) {
                val largo = az.entre(0.22f, 0.5f) * (if (az.real() < 0.12f) 1.7f else 1f)
                val techo = if (s - largo / 2f > S_ALTO) aleroEn(s - largo / 2f) else PISO
                if (h + alto * 0.5f < techo) {
                    val tono = lerp(piedra, if (az.real() < 0.5f) C.TIERRA else C.ROCA, az.entre(0f, 0.38f))
                    val doble = az.real() < 0.08f
                    piedraDelMuro(pincel(v, s - largo / 2f, h), s - largo / 2f, h + alto * (if (doble) 1f else 0.5f), largo / 2f - 0.03f, alto * (if (doble) 1f else 0.5f) - 0.03f, minOf(techo, 9f), tono, t + az.entre(-0.18f, 0.18f), az)
                }
                s -= largo
            }
            h += alto
        }
        // El agua que baja del alero, en regueros oscuros, y el zócalo, más oscuro y verdoso.
        for (k in 0..7) {
            val s = S_FIN - 0.2f - k * 0.42f - az.entre(0f, 0.2f); val arriba = if (s > S_ALTO) aleroEn(s) else PISO
            drawPath(cara(s, s - 0.1f, arriba - az.entre(0.5f, 1.1f), arriba), pincel(v, s, 1.8f).sombra(C.TIERRA).copy(alpha = 0.16f))
        }
        drawPath(cara(s0, s1, -0.1f, 0.32f), Brush.verticalGradient(listOf(Color.Transparent, pincel(v, 0.5f, 0.1f).sombra(lerp(C.TIERRA, C.MUSGO, 0.4f)).copy(alpha = 0.45f)), p(0.5f, 0.32f).y, p(0.5f, 0f).y))
    }

    /** Una piedra del muro, de canto redondeado, centrada en ([s], [h]) con sus medios largos en metros, sin pasar de [techo]. */
    private fun DrawScope.piedraDelMuro(pe: Pincel, s: Float, h: Float, rs: Float, rh: Float, techo: Float, tono: Color, luz: Float, az: Azar) {
        fun j() = az.entre(-0.16f, 0.16f)
        val arriba = minOf(h + rh * (1f + j()), techo - 0.02f); val arriba2 = minOf(h + rh * (1f + j()), techo - 0.02f)
        val a = p(s + rs * (1f + j()), arriba); val b = p(s - rs * (1f + j()), arriba2)
        val c = p(s - rs * (1f + j()), h - rh * (1f + j())); val d = p(s + rs * (1f + j()), h - rh * (1f + j()))
        val m1 = (a + b) / 2f + Offset(0f, -az.entre(0f, 1f)); val m2 = (c + d) / 2f + Offset(0f, az.entre(0f, 0.8f))
        val m3 = (b + c) / 2f + Offset(az.entre(-1f, 0.4f), 0f); val m4 = (d + a) / 2f + Offset(az.entre(-0.4f, 1f), 0f)
        // Una piedra sin labrar: de cantos redondos, abombada (la luz arriba, la sombra abajo), con su junta honda debajo.
        val forma = TrazosValle.cerrada(listOf(a, m1, b, m3, c, m2, d, m4))
        drawPath(forma, Brush.verticalGradient(listOf(pe.por(tono, luz + 0.35f), pe.por(tono, luz), pe.por(lerp(tono, C.ESCORIA, 0.2f), luz - 0.45f)), minOf(a.y, b.y), maxOf(c.y, d.y)))
        drawPath(TrazosValle.cerrada(listOf(c + Offset(0f, 0.6f), m2 + Offset(0f, 0.7f), d + Offset(0f, 0.6f), d, m2, c)), pe.sombra(lerp(tono, C.ESCORIA, 0.5f)).copy(alpha = 0.6f))
        if (az.real() < 0.25f) drawCircle(pe.medio(C.LIQUEN).copy(alpha = 0.5f), az.entre(0.6f, 1.4f), (a + c) / 2f + Offset(az.entre(-2f, 2f), az.entre(-1f, 1f)))
    }

    /** Las piedras grandes de la esquina del zaguán (una larga y una corta, alternadas) y el poste de esquina del piso de tablas. */
    private fun DrawScope.esquinas(v: VistaAntiguas) {
        val t = luzDeCara(v); val piedra = lerp(C.PIEDRA, C.NIEVE, 0.14f)
        val techo = aleroEn(S_FIN)
        var h = 0f; var k = 0
        while (h < techo - 0.1f) {
            val alto = minOf(0.36f, techo - h); val largo = if (k % 2 == 0) 0.5f else 0.3f
            drawPath(cara(S_FIN - 0.01f, S_FIN - largo, h + 0.02f, h + alto - 0.02f, 0.015f), pincel(v, S_FIN, h).por(piedra, t + 0.12f))
            drawLine(pincel(v, S_FIN, h).luz(C.NIEVE).copy(alpha = 0.4f), p(S_FIN - 0.01f, h + alto - 0.03f, 0.015f), p(S_FIN - largo, h + alto - 0.03f, 0.015f), 0.6f)
            h += alto; k++
        }
        val pe = pincel(v, S_ALTO, 3.4f)
        drawPath(cara(S_ALTO + 0.02f, S_ALTO - 0.14f, PISO, ALERO_ALTO, 0.03f), pe.por(lerp(C.MADERA, C.MADERA_OSC, 0.25f), t + 0.1f))
        drawLine(pe.luz(C.MADERA).copy(alpha = 0.4f), p(S_ALTO + 0.02f, PISO, 0.03f), p(S_ALTO + 0.02f, ALERO_ALTO, 0.03f), 0.6f)
    }

    // ---------------------------------------------------------------- La madera

    /** El piso de tablas del cuerpo alto: tablas viejas tendidas, grises de sol, con sus juntas y los clavos; la viga del piso. */
    private fun DrawScope.pisoDeTablas(v: VistaAntiguas, az: Azar) {
        val t = luzDeCara(v)
        val madera = lerp(lerp(C.MADERA, C.PIEDRA, 0.32f), C.LANA, 0.08f)
        drawPath(cara(S_ALTO, S_BORDE, PISO, ALERO_ALTO), pincel(v, -0.6f, 3.4f).por(madera, t - 0.05f))
        var h = PISO + 0.06f
        while (h < ALERO_ALTO) {
            val pe = pincel(v, -0.6f, h); val alto = az.entre(0.17f, 0.24f)
            drawPath(cara(S_ALTO, S_BORDE, h, h + alto - 0.02f), pe.por(lerp(madera, if (az.real() < 0.5f) C.TIERRA else C.LANA, az.entre(0f, 0.25f)), t + az.entre(-0.15f, 0.12f)))
            drawLine(pe.sombra(lerp(madera, C.ESCORIA, 0.5f)), p(S_ALTO, h, 0.005f), p(S_BORDE, h, 0.005f), 1.1f)
            drawLine(pe.luz(madera).copy(alpha = 0.4f * v.r.dia), p(S_ALTO, h + alto - 0.05f, 0.005f), p(S_BORDE, h + alto - 0.05f, 0.005f), 0.5f)
            // Una veta y un nudo de vez en cuando; los clavos de las tablas.
            if (az.real() < 0.5f) { val s = az.entre(S_BORDE, S_ALTO); drawCircle(pe.sombra(madera).copy(alpha = 0.6f), 0.9f, p(s, h + alto * 0.5f)) }
            for (s in listOf(S_ALTO - 0.08f, -0.9f)) drawCircle(pe.medio(C.ESCORIA).copy(alpha = 0.7f), 0.5f, p(s, h + alto * 0.5f, 0.01f))
            h += alto
        }
        // La viga del piso, gruesa, con su cabeza saliente en la esquina.
        drawPath(cara(S_ALTO + 0.12f, S_BORDE, PISO - 0.08f, PISO + 0.1f, 0.06f), pincel(v, -0.5f, PISO).por(lerp(C.MADERA, C.MADERA_OSC, 0.3f), t))
        drawLine(pincel(v, -0.5f, PISO).luz(C.MADERA).copy(alpha = 0.5f), p(S_ALTO + 0.12f, PISO + 0.1f, 0.06f), p(S_BORDE, PISO + 0.1f, 0.06f), 0.7f)
    }

    /** La ventana del cuarto alto, «la de la ventana al paso»: su marco, los postigos abiertos y el alféizar; encendida en la crianza y cuando viven arriba; los pañales tendidos en la crianza de día. */
    private fun DrawScope.cuartoAlto(v: VistaAntiguas) {
        val s0 = -0.42f; val s1 = -1.02f; val h0 = 2.95f; val h1 = 3.62f
        val forma = cara(s0, s1, h0, h1)
        val luz = v.encendido.cuartoAlto * maxOf(v.noche, 0.15f)
        val pe = pincel(v, -0.7f, 3.3f); val t = luzDeCara(v)
        if (luz > 0.05f) {
            drawPath(forma, Brush.verticalGradient(listOf(lerp(C.LUMBRE, Color(0xFFFFE6B0), 0.5f), lerp(C.LUMBRE, C.MADERA, 0.25f)), p(s0, h1).y, p(s0, h0).y), alpha = (0.5f + 0.5f * luz).coerceAtMost(1f))
            resplandor(p(-0.72f, 3.3f), 18f, 0.45f * luz, C.LUMBRE)
        } else {
            drawPath(forma, pe.medio(C.ESCORIA))
            // El reflejo del cielo en el vidrio de abajo, partido por el travesaño.
            drawPath(cara(s0 - 0.04f, s1 + 0.04f, h0 + 0.04f, h0 + 0.3f), lerp(v.l.medio, v.l.bajo, 0.4f).copy(alpha = 0.25f * v.r.dia))
        }
        drawLine(pe.medio(C.MADERA_OSC), p(-0.72f, h0), p(-0.72f, h1), 1f)
        drawLine(pe.medio(C.MADERA_OSC), p(s0, 3.28f), p(s1, 3.28f), 0.9f)
        // El marco, el dintel y el alféizar.
        val marco = lerp(C.MADERA, C.MADERA_OSC, 0.2f)
        drawPath(cara(s0 + 0.06f, s0, h0 - 0.04f, h1 + 0.04f, 0.03f), pe.por(marco, t + 0.1f))
        drawPath(cara(s0 + 0.08f, s1 - 0.08f, h1, h1 + 0.1f, 0.04f), pe.por(marco, t + 0.15f))
        drawPath(cara(s0 + 0.12f, s1 - 0.1f, h0 - 0.1f, h0, 0.1f), pe.por(lerp(marco, C.LANA, 0.2f), 0.45f))
        // El postigo abierto contra el muro, pintado de verde viejo, con sus tablas.
        val postigo = lerp(lerp(C.PINO, C.MADERA, 0.4f), C.PIEDRA, 0.2f)
        drawPath(cara(s0 + 0.08f, s0 + 0.4f, h0 - 0.02f, h1 + 0.02f, 0.05f), pe.por(postigo, t))
        for (k in 1..2) drawLine(pe.sombra(postigo), p(s0 + 0.08f + k * 0.107f, h0, 0.055f), p(s0 + 0.08f + k * 0.107f, h1, 0.055f), 0.6f)
        if (v.invierno) drawPath(cara(s0 + 0.12f, s1 - 0.1f, h0, h0 + 0.08f, 0.11f), pe.por(C.NIEVE, 0.5f))
        // Los pañales de la crianza, tendidos en una cuerda bajo la ventana.
        if (EscenaAntiguas.cuna(v.m) && v.noche < 0.5f) {
            drawLine(pe.medio(C.CUERO), p(s0 + 0.3f, 2.86f, 0.12f), p(s1, 2.86f, 0.12f), 0.5f)
            for (k in 0..2) { val q = p(s0 + 0.18f - k * 0.3f, 2.86f, 0.12f); drawPath(poligono(q, q + Offset(5f, 0.4f), q + Offset(4.6f, 7f), q + Offset(0.4f, 6.4f)), pe.por(C.LANA, 0.4f)) }
        }
    }

    /** El ventanuco de la sala, en la planta de piedra: hondo, con su reja; encendido de noche; con escarcha («helechos blancos») en invierno. */
    private fun DrawScope.ventanuco(v: VistaAntiguas) {
        val s0 = -0.5f; val s1 = -0.82f; val h0 = 1.22f; val h1 = 1.6f
        val forma = cara(s0, s1, h0, h1)
        val luz = v.encendido.sala * v.noche
        val pe = pincel(v, -0.66f, 1.4f)
        if (luz > 0.05f) drawPath(forma, lerp(C.LUMBRE, Color(0xFFFFE2A8), 0.4f).copy(alpha = (0.4f + 0.6f * luz).coerceAtMost(1f)))
        else drawPath(forma, pe.medio(C.ESCORIA))
        // El grueso del muro: el derrame de la izquierda, que mira a la derecha.
        drawPath(FachadaPosada.canto(s0, 0f, -0.3f, h0, h1), pe.por(C.PIEDRA, v.sol.t(FachadaPosada.CANTO) - 0.2f))
        if (v.invierno) drawPath(forma, C.NIEVE.copy(alpha = 0.32f))
        for (s in listOf(-0.6f, -0.71f)) drawLine(pe.medio(C.ESCORIA), p(s, h0, -0.05f), p(s, h1, -0.05f), 0.8f)
        drawPath(cara(s0 + 0.06f, s1 - 0.06f, h0 - 0.07f, h0, 0.07f), pe.por(lerp(C.PIEDRA, C.NIEVE, 0.2f), 0.4f))
        drawPath(cara(s0 + 0.08f, s1 - 0.08f, h1, h1 + 0.12f, 0.02f), pe.por(lerp(C.PIEDRA, C.NIEVE, 0.2f), luzDeCara(v) + 0.1f))
    }

    // ---------------------------------------------------------------- Los tejados

    /**
     * El tejado del cuerpo alto, que sube por la esquina de arriba a la derecha: las losas grises de pizarra en hileras,
     * cada hilera con su canto de luz y alguna piedra encima para el viento; con nieve, el manto.
     */
    private fun DrawScope.tejadoAlto(v: VistaAntiguas, az: Azar) {
        val vuelo = 0.62f; val s0 = S_ALTO + 0.3f
        fun q(s: Float, atras: Float) = p(s, ALERO_ALTO + atras * 0.58f, vuelo - atras)
        val forma = poligono(listOf(q(s0, 0f), q(S_BORDE - 1f, 0f), q(S_BORDE - 1f, 6f), q(s0, 6f)))
        val pe = v.pincelEn(q(-0.5f, 1f)); val tejado = lerp(C.LOSA, C.PIEDRA_OSC, 0.2f)
        val tt = v.sol.t(V3(-0.42f, 0.86f, -0.28f))
        drawPath(forma, pe.por(tejado, tt))
        clipPath(forma) {
            var atras = 0.25f
            while (atras < 6f) {
                drawLine(pe.sombra(lerp(tejado, C.ESCORIA, 0.4f)), q(s0, atras), q(S_BORDE - 1f, atras), 1.3f)
                drawLine(pe.luz(lerp(tejado, C.NIEVE, 0.3f)).copy(alpha = 0.35f), q(s0, atras - 0.05f), q(S_BORDE - 1f, atras - 0.05f), 0.6f)
                var s = s0 - az.entre(0f, 0.3f)
                while (s > S_BORDE - 1f) { drawLine(pe.sombra(tejado).copy(alpha = 0.5f), q(s, atras), q(s, atras + 0.22f), 0.6f); s -= az.entre(0.25f, 0.45f) }
                atras += 0.26f
            }
            if (v.nieveArriba > 0.25f || v.nieveAbajo > 0.3f) drawPath(forma, pe.por(C.NIEVE, tt + 0.2f).copy(alpha = maxOf(v.nieveAbajo, v.nieveArriba * 0.6f).coerceIn(0f, 1f)))
        }
        // El canto del tejado por la izquierda (el vuelo sobre la esquina), con su sombra debajo.
        drawLine(pe.luz(lerp(tejado, C.NIEVE, 0.4f)).copy(alpha = 0.6f), q(s0, 0f), q(s0, 6f), 1f)
    }

    /** El alero del cuerpo alto: el vuelo visto desde abajo, oscuro, con las cabezas de las vigas y el canto de las losas. */
    private fun DrawScope.aleroAlto(v: VistaAntiguas, az: Azar) {
        val vuelo = 0.62f; val s0 = S_ALTO + 0.3f
        val pe = pincel(v, -0.6f, ALERO_ALTO)
        drawPath(poligono(listOf(p(s0, ALERO_ALTO, 0f), p(S_BORDE, ALERO_ALTO, 0f), p(S_BORDE, ALERO_ALTO, vuelo), p(s0, ALERO_ALTO, vuelo))), pe.sombra(C.MADERA_OSC))
        var s = s0 - 0.08f
        while (s > S_BORDE) { drawPath(poligono(listOf(p(s, ALERO_ALTO - 0.02f, 0f), p(s - 0.1f, ALERO_ALTO - 0.02f, 0f), p(s - 0.1f, ALERO_ALTO - 0.12f, vuelo - 0.04f), p(s, ALERO_ALTO - 0.12f, vuelo - 0.04f))), pe.por(C.MADERA, -0.2f)); s -= 0.42f }
        drawPath(cara(s0, S_BORDE, ALERO_ALTO - 0.02f, ALERO_ALTO + 0.12f, vuelo), pe.por(lerp(C.LOSA, C.ESCORIA, 0.2f), 0.15f))
        drawLine(pe.luz(lerp(C.LOSA, C.NIEVE, 0.4f)).copy(alpha = 0.6f), p(s0, ALERO_ALTO + 0.12f, vuelo), p(S_BORDE, ALERO_ALTO + 0.12f, vuelo), 0.8f)
        if (v.invierno) { var ss = s0 - 0.1f; while (ss > S_BORDE) { val q = p(ss, ALERO_ALTO - 0.02f, vuelo); drawLine(pe.luz(lerp(C.NIEVE, C.AGUA, 0.25f)).copy(alpha = 0.75f), q, q + Offset(0f, az.entre(3f, 11f)), 0.9f, StrokeCap.Round); ss -= az.entre(0.15f, 0.35f) } }
    }

    /**
     * El alero del zaguán: el tejado de una sola agua, de losas, que baja hacia la izquierda; se ve su canto (dos hiladas
     * de losas gruesas, con musgo) y, debajo, el vuelo en sombra; con nieve, el manto encima y los carámbanos.
     */
    private fun DrawScope.aleroDelZaguan(v: VistaAntiguas, az: Azar) {
        val vuelo = 0.34f; val s0 = S_ALTO; val s1 = S_FIN + 0.24f
        val pe = pincel(v, 1f, 2.5f)
        val pasos = (0..10).map { s0 + (s1 - s0) * it / 10f }
        drawPath(poligono(pasos.map { p(it, aleroEn(it), 0f) } + pasos.reversed().map { p(it, aleroEn(it), vuelo) }), pe.sombra(C.MADERA_OSC))
        for (s in pasos.drop(1)) drawLine(pe.por(C.MADERA, -0.25f), p(s, aleroEn(s) - 0.01f, 0f), p(s, aleroEn(s) - 0.05f, vuelo - 0.03f), 1.2f)
        val canto = poligono(pasos.map { p(it, aleroEn(it), vuelo) } + pasos.reversed().map { p(it, aleroEn(it) + 0.15f, vuelo + 0.02f) })
        drawPath(canto, pe.por(lerp(C.LOSA, C.ESCORIA, 0.15f), 0.1f))
        clipPath(canto) {
            var s = s0 + az.entre(0f, 0.2f)
            while (s < s1) { drawLine(pe.sombra(C.ESCORIA).copy(alpha = 0.7f), p(s, aleroEn(s), vuelo), p(s, aleroEn(s) + 0.16f, vuelo), 0.6f); s += az.entre(0.2f, 0.4f) }
            drawPath(poligono(pasos.map { p(it, aleroEn(it) + 0.07f, vuelo + 0.01f) } + pasos.reversed().map { p(it, aleroEn(it) + 0.08f, vuelo + 0.01f) }), pe.sombra(C.ESCORIA).copy(alpha = 0.6f))
            for (k in 0..4) { val s = s0 + az.entre(0.2f, s1 - s0 - 0.2f); drawCircle(pe.medio(lerp(C.MUSGO, C.ALERCE, 0.4f)).copy(alpha = 0.7f), az.entre(1f, 2f), p(s, aleroEn(s) + 0.12f, vuelo)) }
        }
        drawLine(pe.luz(lerp(C.LOSA, C.NIEVE, 0.4f)).copy(alpha = 0.6f), p(s0, aleroEn(s0) + 0.15f, vuelo + 0.02f), p(s1, aleroEn(s1) + 0.15f, vuelo + 0.02f), 0.8f)
        if (v.nieveAbajo > 0.15f) {
            val alto = 0.08f + 0.28f * v.nieveAbajo
            drawPath(poligono(pasos.map { p(it, aleroEn(it) + 0.14f, vuelo + 0.01f) } + pasos.reversed().map { p(it, aleroEn(it) + 0.14f + alto * (0.8f + 0.2f * kotlin.math.sin(it * 9f)), vuelo - 0.02f) }), pe.por(C.NIEVE, 0.35f).copy(alpha = v.nieveAbajo.coerceAtMost(1f)))
            if (v.invierno) { var s = s0 + 0.1f; while (s < s1) { val q = p(s, aleroEn(s), vuelo); drawLine(pe.luz(lerp(C.NIEVE, C.AGUA, 0.25f)).copy(alpha = 0.7f), q, q + Offset(0f, az.entre(2f, 8f)), 0.7f, StrokeCap.Round); s += az.entre(0.15f, 0.35f) } }
        }
    }

    /** La chimenea de la lumbre de la sala, de piedra, entre el zaguán y el cuerpo alto, con su losa de remate. */
    private fun DrawScope.chimenea(v: VistaAntiguas) {
        val f = -0.25f; val s0 = 0.08f; val s1 = 0.42f; val h0 = aleroEn(0.2f)
        val pe = v.pincelEn(p(0.25f, 3.2f, f))
        val t = luzDeCara(v)
        drawPath(cara(s0, s1, h0, CHIMENEA_ALTO, f), pe.por(C.PIEDRA, t - 0.05f))
        var h = h0 + 0.1f; var k = 0
        while (h < CHIMENEA_ALTO - 0.05f) { drawLine(pe.sombra(C.PIEDRA).copy(alpha = 0.6f), p(s0, h, f), p(s1, h, f), 0.6f); val s = if (k % 2 == 0) 0.25f else 0.18f; drawLine(pe.sombra(C.PIEDRA).copy(alpha = 0.5f), p(s, h, f), p(s, h + 0.2f, f), 0.5f); h += 0.2f; k++ }
        // El hollín de la boca y la losa de remate.
        drawPath(cara(s0, s1, CHIMENEA_ALTO - 0.18f, CHIMENEA_ALTO, f), pe.medio(lerp(C.PIEDRA, C.ESCORIA, 0.5f)))
        drawPath(cara(s0 - 0.06f, s1 + 0.06f, CHIMENEA_ALTO, CHIMENEA_ALTO + 0.07f, f + 0.06f), pe.por(C.LOSA, 0.3f))
        if (v.nieveAbajo > 0.3f) drawPath(cara(s0 - 0.05f, s1 + 0.05f, CHIMENEA_ALTO + 0.07f, CHIMENEA_ALTO + 0.07f + 0.12f * v.nieveAbajo, f + 0.06f), pe.por(C.NIEVE, 0.5f).copy(alpha = v.nieveAbajo))
    }

    /** El pie del muro: la franja de paso entre la piedra y el suelo (hierba, ortigas, piedras caídas); con nieve, el ventisquero arrimado. */
    private fun DrawScope.pieDelMuro(v: VistaAntiguas, az: Azar) {
        val hierba = EscenaAntiguas.hierba(v.m)
        var s = S_FIN - 0.05f
        while (s > S_BORDE) {
            val enPuerta = s in PuertaAntiguas.PUERTA_S0 - 0.1f..PuertaAntiguas.PUERTA_S1 + 0.1f
            if (!enPuerta && v.nieveAbajo < 0.6f) {
                val q = p(s, 0f, 0.04f); val pe = v.pincelEn(q); val k = FachadaPosada.escala(s) / 55f
                for (j in 0..2) drawLine(pe.por(lerp(hierba, C.PINO, 0.3f), 0.1f), q, q + Offset(az.entre(-2f, 2f) * k, -az.entre(2f, 6f) * k), 0.6f * k, StrokeCap.Round)
            }
            s -= az.entre(0.12f, 0.3f)
        }
    }
}
