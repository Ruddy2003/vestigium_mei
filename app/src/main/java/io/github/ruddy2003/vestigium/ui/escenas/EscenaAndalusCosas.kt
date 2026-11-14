package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.ALMAGRA
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.ANAFE
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.ANIL
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.ARRAYAN
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.BARRO
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.BRONCE
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.CAL
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.CAP
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.CARMESI
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.HOJA
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.LADRILLO
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.MADERA
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.MADERA_OSC
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.MARMOL
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.MESA
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.NARANJA
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.ORO
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.PIEDRA
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.PILA
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.SEDA
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.Telar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.VIDRIADO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LUZ_DE_CASA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.pano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Las cosas del patio de Zaynab (`EscenaAndalus`), cada una con su silueta (para la sombra que arroja), dónde pisa y
 * cómo se pinta; y sus doce objetos del día, que van sobre la mesa baja (el laúd, apoyado en ella; la jarra, colgada
 * del naranjo). Todo pide su color a las [Luces] del momento y tiene su lado de luz y su lado de sombra.
 */
internal object AndalusCosas {
    /** A qué escala van los objetos del día sobre la mesa. */
    private const val KO = 1.8f

    /** A qué escala va el anafe. */
    const val K_ANAFE = 1.6f

    /** Dónde arden las lámparas del Ramadán: la del arco, la del muro, dos faroles sobre la tapia y la que cuelga del naranjo. */
    val LAMPARAS = listOf(Offset(60f, 148f), Offset(186f, CAP - 9f), Offset(236f, CAP - 9f), Offset(322f, 100f))

    /** Dónde cuelga la jarra del día: de una rama baja del naranjo, a la sombra. */
    val JARRA = Offset(368f, 158f)

    /** Dónde queda la mecha de un candil que pisa en [sobre] a la escala [k]. */
    fun picoDelCandil(sobre: Offset, k: Float) = sobre + Offset(7.4f * k, -3.2f * k)

    /** A qué escala va el candil del día, y dónde. */
    const val K_CANDIL = 2.9f

    /** Dónde cuaja la nieve sobre las cosas: los bordes rectos (de dónde a dónde, y cuánta) y lo redondo (dónde, y cuánto de ancho y de alto). */
    val NIEVE_EN_BORDES = listOf(
        Triple(Offset(272f, 153.6f), Offset(324f, 153.6f), 2.6f),                // el castillo del telar, por delante
        Triple(Offset(279f, 144.4f), Offset(317f, 144.4f), 2f),                  // y por detrás
        Triple(Offset(278f, 252.4f), Offset(285f, 252.4f), 1.4f), Triple(Offset(307f, 252.4f), Offset(319f, 252.4f), 1.6f),      // el banco, a los lados de la almohadilla
    )
    val NIEVE_EN_REDONDOS = listOf(
        Triple(Offset(118f, 201.6f), 14f, 6.4f), Triple(Offset(260f, 217.4f), 10.4f, 4.4f), Triple(Offset(150f, 164.8f), 5.6f, 2.8f),     // las tres macetas
        Triple(Offset(373.4f, 191.4f), 14f, 4f),                                 // la tapa de la tinaja
    )

    /** Todo lo que hay en el patio. */
    fun todas(m: Momento, l: Luces, r: Rayo, est: Estacion): List<Cosa> = buildList {
        add(telar(m, l, r))
        add(banco(l, r, Offset(298f, 271f)))
        add(pila(l, r))
        add(maceta(l, r, Offset(118f, 257f), 1.3f, 0, est))
        add(maceta(l, r, Offset(260f, 249f), 1.05f, 1, est))
        add(maceta(l, r, Offset(150f, CAP + 1f), 0.66f, 2, est))
        add(tinaja(l, r, Offset(372f, 252f), 1.45f))
        if (EscenaAndalus.mesaPuesta(m)) add(bandeja(m, l, r))
        add(mesa(m, l, r))
        if (m.ficha.objeto == "laud") add(laud(l, r))
        add(cojin(l, r, Offset(26f, 267f), ANIL, 1.2f))
        add(cojin(l, r, Offset(33f, 279f), CARMESI, 1.35f))
        add(canasto(l, r, Offset(336f, 284f)))
        add(escoba(l, r))
        add(anafe(m, l, r))
        add(EscenaAndalus.troncoCosa(l, r))
        if (m.ficha.objeto == "jarra") add(jarraColgada(l, r))
    }

    private fun oval(c: Offset, rx: Float, ry: Float) = Path().apply { addOval(Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry)) }

    // ---------------------------------------------------------------- Las esteras

    /** Lo que está tendido en el suelo: la estera de esparto donde se sienta la familia y, a la hora de rezar, la esterilla. */
    fun DrawScope.esteras(m: Momento, l: Luces) {
        val az = Azar(Semilla.de("andalus", "estera"))
        val esparto = l.tono(Color(0xFFD2B872)); val oscuro = l.tono(Color(0xFF9A7E48))
        val forma = poligono(Offset(16f, 256f), Offset(130f, 255f), Offset(146f, 290f), Offset(0f, 290f))
        drawPath(forma, Brush.verticalGradient(listOf(lerp(esparto, oscuro, 0.3f), esparto), 255f, 290f))
        clipPath(forma) {
            // La trama, en espiga, y dos cenefas teñidas.
            var y = 255f
            while (y < 292f) { drawLine(oscuro.copy(alpha = 0.5f), Offset(10f, y), Offset(152f, y - 1f), 0.4f); y += 2.2f }
            var x = 0f
            while (x < 156f) { drawLine(oscuro.copy(alpha = 0.3f), Offset(x, 254f), Offset(x - 6f, 292f), 0.4f); x += 3.4f }
            for ((dy, color) in listOf(3.4f to ALMAGRA, 6f to Color(0xFF3A342E))) {
                drawLine(l.tono(color).copy(alpha = 0.75f), Offset(16f - dy * 0.47f, 256f + dy), Offset(130f + dy * 0.47f, 255f + dy), 1.1f)
            }
            repeat(14) { mancha(Offset(az.entre(6f, 142f), az.entre(257f, 290f)), az.entre(3f, 8f), az.entre(0.8f, 2f), oscuro, 0.2f) }
        }
        drawPath(forma, oscuro.copy(alpha = 0.8f), style = Stroke(0.6f))
        // Los flecos de un lado, deshilachados.
        for (i in 0..15) { val p = Offset(130f + i * 1.0f, 255f + i * 2.2f); drawLine(esparto, p, p + Offset(az.entre(1.6f, 3.6f), az.entre(-0.6f, 0.8f)), 0.5f) }
        if (m.hacer == io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer.REZAR) {
            // La esterilla de la oración, tendida al sesgo: mira al sureste.
            val e = poligono(Offset(230f, 272f), Offset(262f, 268.4f), Offset(284f, 285f), Offset(248f, 289.4f))
            drawPath(e, l.tono(lerp(CARMESI, Color(0xFF6A3030), 0.3f)))
            clipPath(e) {
                drawPath(poligono(Offset(234f, 273.4f), Offset(260.6f, 270.4f), Offset(278f, 283.6f), Offset(249.4f, 287.2f)), l.tono(ORO), style = Stroke(0.8f))
                drawPath(Path().apply { moveTo(246f, 275f); cubicTo(250f, 270f, 258f, 270.6f, 262f, 273.4f); lineTo(270f, 280f); lineTo(252f, 282.6f); close() }, l.tono(ANIL).copy(alpha = 0.8f))
            }
            drawPath(e, l.tono(Color(0xFF5A2424)), style = Stroke(0.5f))
        }
    }

    // ---------------------------------------------------------------- El telar

    // Los pies del telar: los de delante (izquierdo y derecho) y los de atrás.
    private val TF_I = Offset(273f, 258f); private val TF_D = Offset(323f, 258f); private val TB_I = Offset(280f, 240f); private val TB_D = Offset(316f, 240f)
    private const val T_ALTO = 102f
    private const val T_PLANO = 55f
    private const val T_ROLLO = 38f

    private fun entre(a: Offset, b: Offset, k: Float) = a + (b - a) * k

    /** El punto del telar que está a [u] de su fondo (0, atrás; 1, delante), a [v] de su ancho (0, a la izquierda) y a [h] del suelo. */
    private fun enElTelar(u: Float, v: Float, h: Float) = entre(entre(TB_I, TB_D, v), entre(TF_I, TF_D, v), u) + Offset(0f, -h * (0.92f + 0.08f * u))

    private fun palo(a: Offset, b: Offset, grueso: Float) = Path().apply {
        val d = b - a; val n = Offset(-d.y, d.x) / maxOf(0.01f, d.getDistance()) * (grueso / 2f)
        moveTo(a + n); lineTo(b + n); lineTo(b - n); lineTo(a - n); close()
    }

    /**
     * El telar, visto desde el sitio de la tejedora: un bastidor de cuatro pies con su castillo, del que cuelgan los
     * lizos y el batán con el peine. Atrás, el enjulio con la urdimbre; delante, la tela que baja a enrollarse en el
     * suyo, que engorda según avanza la pieza ([EscenaAndalus.tela]). Carmesí la pieza del califa, azul la de la boda,
     * las dos con su banda de oro. Terminada la pieza, el telar queda desnudo.
     */
    private fun telar(m: Momento, l: Luces, r: Rayo): Cosa {
        val estado = EscenaAndalus.telar(m); val avance = EscenaAndalus.tela(m)
        val propia = if (EscenaAndalus.telaDeLaBoda(m)) ANIL else CARMESI
        val rt = 2.2f + (if (estado == Telar.TEJIENDO) 6f * avance else 0f)
        val forma = Path().apply {
            for ((p, u) in listOf(TF_I to 1f, TF_D to 1f, TB_I to 0f, TB_D to 0f)) addPath(palo(p, p + Offset(0f, -T_ALTO * (0.92f + 0.08f * u)), 4f))
            addPath(palo(enElTelar(1f, 0f, T_ALTO), enElTelar(1f, 1f, T_ALTO), 4f)); addPath(palo(enElTelar(0f, 0f, T_ALTO), enElTelar(0f, 1f, T_ALTO), 3f))
            if (estado != Telar.DESNUDO) addPath(poligono(enElTelar(0f, 0f, T_PLANO), enElTelar(0f, 1f, T_PLANO), enElTelar(1f, 1f, T_ROLLO - rt), enElTelar(1f, 0f, T_ROLLO - rt)))
        }
        return Cosa(forma, Offset(298f, 258f)) {
            val madera = l.tono(MADERA); val luz = l.tono(lerp(MADERA, Color(0xFFFFE8C0), 0.4f * r.contraste)); val sombra = l.tono(mult(MADERA_OSC, r.color, 0.5f * r.contraste)); val oscura = l.tono(MADERA_OSC)
            fun pie(p: Offset, u: Float) {
                val base = if (u < 0.5f) lerp(madera, sombra, 0.5f) else madera
                val alto = T_ALTO * (0.92f + 0.08f * u); val g = if (u < 0.5f) 3.2f else 4.2f
                drawOval(Color.Black.copy(alpha = 0.2f), Offset(p.x - 5f, p.y - 1.2f), Size(10f, 2.8f))
                drawPath(palo(p, p + Offset(0f, -alto), g), Brush.horizontalGradient(if (r.lat >= 0f) listOf(sombra, base, luz) else listOf(luz, base, sombra), p.x - g / 2f, p.x + g / 2f))
                // Las vetas y los golpes de la madera.
                for (i in 0..3) drawLine(oscura.copy(alpha = 0.3f), p + Offset(-g * 0.2f + i * 0.5f, -alto * (0.1f + i * 0.22f)), p + Offset(-g * 0.2f + i * 0.5f, -alto * (0.22f + i * 0.22f)), 0.35f)
            }
            fun barra(a: Offset, b: Offset, grueso: Float, color: Color = madera) { drawPath(palo(a, b, grueso), color); drawLine(luz.copy(alpha = 0.8f), a + Offset(0f, -grueso * 0.36f), b + Offset(0f, -grueso * 0.36f), 0.5f); drawLine(sombra.copy(alpha = 0.7f), a + Offset(0f, grueso * 0.4f), b + Offset(0f, grueso * 0.4f), 0.5f) }
            val hilo = l.tono(lerp(propia, Color(0xFFF4E8D2), 0.55f)); val hiloOsc = l.tono(lerp(propia, Color(0xFF2A1A20), 0.15f))
            val tela = l.tono(propia); val telaLuz = l.tono(lerp(propia, Color.White, 0.25f)); val telaSombra = l.tono(mult(lerp(propia, Color(0xFF1E1420), 0.3f), r.color, 0.5f)); val oro = l.tono(ORO)

            // Atrás: los pies, el travesaño de arriba, el de abajo y los pedales.
            pie(TB_I, 0f); pie(TB_D, 0f)
            barra(enElTelar(0f, 0f, T_ALTO), enElTelar(0f, 1f, T_ALTO), 2.8f, lerp(madera, sombra, 0.5f))
            barra(enElTelar(0f, 0f, 14f), enElTelar(0f, 1f, 14f), 2.4f, lerp(madera, sombra, 0.6f))
            for (v in listOf(0.34f, 0.64f)) { barra(enElTelar(0.05f, v, 9f), enElTelar(0.95f, v + (v - 0.5f) * 0.3f, 2.6f), 2.2f, oscura); drawLine(l.tono(Color(0xFFB89A5E)), enElTelar(0.4f, v, 7f), enElTelar(0.42f, v, T_PLANO - 12f), 0.4f) }
            // El enjulio de atrás, con la urdimbre que queda por tejer: se ve por debajo cuando delante todavía no cuelga la tela.
            val quedan = if (estado == Telar.DESNUDO) 0f else 1f - avance
            val ra = 2f + 4.4f * quedan
            val e0 = enElTelar(0f, 0.03f, T_ROLLO); val e1 = enElTelar(0f, 0.97f, T_ROLLO)
            drawPath(palo(e0, e1, ra * 2f), Brush.verticalGradient(if (quedan > 0f) listOf(lerp(hilo, l.tono(Color.White), 0.2f), hilo, hiloOsc) else listOf(luz, madera, sombra), e0.y - ra, e0.y + ra))
            if (quedan > 0f) for (i in 0..18) { val x = e0.x + (e1.x - e0.x) * i / 18f; drawLine(hiloOsc.copy(alpha = 0.4f), Offset(x, e0.y - ra), Offset(x, e0.y + ra), 0.3f) }
            if (estado != Telar.DESNUDO) {
                // La urdimbre: sube del enjulio a la barra de atrás y viene, tirante, hasta la de delante.
                drawPath(poligono(e0 + Offset(0f, -ra), e1 + Offset(0f, -ra), enElTelar(0f, 0.97f, T_PLANO), enElTelar(0f, 0.03f, T_PLANO)), hilo.copy(alpha = 0.8f))
                val plano = poligono(enElTelar(0f, 0.03f, T_PLANO), enElTelar(0f, 0.97f, T_PLANO), enElTelar(1f, 0.97f, T_PLANO), enElTelar(1f, 0.03f, T_PLANO))
                drawPath(plano, Brush.verticalGradient(listOf(lerp(hilo, hiloOsc, 0.25f), lerp(hilo, l.tono(Color.White), 0.2f)), enElTelar(0f, 0f, T_PLANO).y, enElTelar(1f, 0f, T_PLANO).y))
                clipPath(plano) { for (i in 0..30) { val v = 0.03f + 0.94f * i / 30f; drawLine(hiloOsc.copy(alpha = 0.5f), enElTelar(0f, v, T_PLANO), enElTelar(1f, v, T_PLANO), 0.35f) } }
                if (estado == Telar.TEJIENDO) {
                    // Lo tejido, del peine a la barra de delante.
                    val desde = 1f - 0.34f * (avance * 8f).coerceIn(0.3f, 1f)
                    val tejido = poligono(enElTelar(desde, 0.03f, T_PLANO), enElTelar(desde, 0.97f, T_PLANO), enElTelar(1f, 0.97f, T_PLANO), enElTelar(1f, 0.03f, T_PLANO))
                    drawPath(tejido, Brush.verticalGradient(listOf(tela, telaLuz), enElTelar(desde, 0f, T_PLANO).y, enElTelar(1f, 0f, T_PLANO).y))
                    drawLine(l.tono(Color.White).copy(alpha = 0.7f), enElTelar(desde, 0.03f, T_PLANO), enElTelar(desde, 0.97f, T_PLANO), 0.5f)
                }
            }
            // Los lizos: dos marcos de hilos que cuelgan del castillo, uno más alto que el otro (la calada abierta).
            for ((u, sube) in listOf(0.36f to 0f, 0.5f to 5f)) {
                val a = enElTelar(u, 0.06f, 86f + sube); val b = enElTelar(u, 0.94f, 86f + sube); val c = enElTelar(u, 0.94f, T_PLANO - 2f); val d = enElTelar(u, 0.06f, T_PLANO - 2f)
                for (v in listOf(0.2f, 0.8f)) drawLine(l.tono(Color(0xFFB89A5E)), enElTelar(u, v, T_ALTO), enElTelar(u, v, 86f + sube), 0.5f)
                for (i in 0..24) { val v = i / 24f; drawLine(l.tono(Color(0xFFF0E6CC)).copy(alpha = 0.55f), entre(a, b, v), entre(d, c, v), 0.3f) }
                barra(a, b, 2f)
            }
            // El batán: dos brazos que cuelgan y, entre ellos, el peine.
            val ub = 0.7f
            for (v in listOf(0f, 1f)) drawLine(madera, enElTelar(ub, v, T_ALTO), enElTelar(ub, v, T_PLANO - 3f), 1.6f)
            val p0 = enElTelar(ub, 0f, T_PLANO + 12f); val p1 = enElTelar(ub, 1f, T_PLANO + 12f); val p2 = enElTelar(ub, 1f, T_PLANO + 0.5f); val p3 = enElTelar(ub, 0f, T_PLANO + 0.5f)
            for (i in 0..36) { val v = i / 36f; drawLine(l.tono(Color(0xFFDCCB96)).copy(alpha = 0.8f), entre(p0, p1, v), entre(p3, p2, v), 0.3f) }
            barra(p0, p1, 2.6f); barra(p3, p2, 2.4f)
            // Los largueros del castillo, que vienen de atrás adelante.
            for (v in listOf(0f, 1f)) barra(enElTelar(0f, v, T_ALTO), enElTelar(1f, v, T_ALTO), 2.8f, if ((v - 0.5f) * r.lat > 0f) luz else lerp(madera, sombra, 0.3f))
            for (v in listOf(0f, 1f)) drawPath(palo(enElTelar(0f, v, T_PLANO), enElTelar(1f, v, T_PLANO), 2.4f), lerp(madera, sombra, 0.3f))

            // Delante: la tela, que baja de la barra a su enjulio, y el rollo, que engorda.
            val b0 = enElTelar(1f, 0.03f, T_PLANO); val b1 = enElTelar(1f, 0.97f, T_PLANO); val r0 = enElTelar(1f, 0.03f, T_ROLLO); val r1 = enElTelar(1f, 0.97f, T_ROLLO)
            if (estado == Telar.TEJIENDO) {
                val pano = poligono(b0, b1, r1, r0)
                drawPath(pano, Brush.horizontalGradient(if (r.lat >= 0f) listOf(telaSombra, tela, telaLuz) else listOf(telaLuz, tela, telaSombra), b0.x, b1.x))
                clipPath(pano) {
                    // La banda de oro con sus letras (no se leen: son trazos) y dos filetes.
                    val y = b0.y + 5.4f
                    drawRect(oro, Offset(b0.x, y), Size(b1.x - b0.x, 3.6f))
                    var x = b0.x + 1.6f; var i = 0
                    while (x < b1.x - 2f) { val w = listOf(1.4f, 2.6f, 0.8f, 2f)[i % 4]; drawLine(l.tono(lerp(propia, Color.Black, 0.35f)), Offset(x, y + 0.9f), Offset(x + w * 0.3f, y + 2.7f), 0.55f); if (i % 3 == 0) drawLine(l.tono(lerp(propia, Color.Black, 0.35f)), Offset(x, y + 2.7f), Offset(x + w, y + 2.7f), 0.45f); x += w + 0.9f; i++ }
                    drawRect(oro, Offset(b0.x, y - 1.8f), Size(b1.x - b0.x, 0.6f)); drawRect(oro, Offset(b0.x, y + 4.8f), Size(b1.x - b0.x, 0.6f))
                    // El brillo de la seda y un par de pliegues.
                    drawRect(Brush.horizontalGradient(listOf(Color.Transparent, l.tono(Color.White).copy(alpha = 0.3f), Color.Transparent), b0.x + 8f, b0.x + 22f), Offset(b0.x + 8f, b0.y), Size(14f, 20f))
                    for (dx in listOf(13f, 31f)) drawLine(telaSombra.copy(alpha = 0.5f), Offset(b0.x + dx, b0.y), Offset(b0.x + dx + 1.4f, r0.y), 0.6f)
                }
            }
            val rollo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(r0.x - 0.5f, r0.y - rt, r1.x + 0.5f, r0.y + rt, CornerRadius(rt * 0.7f))) }
            if (estado == Telar.TEJIENDO) {
                drawPath(rollo, Brush.verticalGradient(listOf(telaLuz, tela, telaSombra), r0.y - rt, r0.y + rt))
                clipPath(rollo) { if (avance > 0.12f) { drawRect(oro, Offset(r0.x, r0.y - rt * 0.5f), Size(r1.x - r0.x, 1.1f)); if (avance > 0.5f) drawRect(oro.copy(alpha = 0.7f), Offset(r0.x, r0.y + rt * 0.3f), Size(r1.x - r0.x, 0.7f)) } }
            } else drawPath(rollo, Brush.verticalGradient(listOf(luz, madera, sombra), r0.y - rt, r0.y + rt))
            // Las ruedas dentadas del enjulio, con su trinquete.
            for (p in listOf(r0 + Offset(-2.6f, 0f), r1 + Offset(2.6f, 0f))) { drawCircle(oscura, 3.4f, p); drawCircle(luz, 1.2f, p); for (i in 0 until 8) { val a = i * 0.785f; drawLine(oscura, p + Offset(cos(a) * 3f, sin(a) * 3f), p + Offset(cos(a) * 4.4f, sin(a) * 4.4f), 0.9f) } }
            barra(enElTelar(1f, 0f, T_PLANO), enElTelar(1f, 1f, T_PLANO), 3f)
            barra(enElTelar(1f, 0f, 14f), enElTelar(1f, 1f, 14f), 2.8f)
            // Los pies de delante y el travesaño del castillo.
            pie(TF_I, 1f); pie(TF_D, 1f)
            barra(enElTelar(1f, 0f, T_ALTO), enElTelar(1f, 1f, T_ALTO), 4f)
            for (p in listOf(TF_I, TF_D)) for (h in listOf(14f, T_PLANO, T_ALTO)) drawCircle(oscura, 0.8f, p + Offset(0f, -h))
            // Una lanzadera, olvidada sobre la tela.
            if (estado == Telar.TEJIENDO) lanzadera(l, enElTelar(0.92f, 0.62f, T_PLANO + 0.6f), 0.6f)
            // El duelo: el telar, tapado con un lienzo.
            if (EscenaAndalus.luto(m)) pano(l, enElTelar(1f, 0.02f, T_ALTO - 2f), enElTelar(1f, 0.98f, T_ALTO - 2f), 52f, Color(0xFFF1ECDF))
        }
    }

    /** El banco de la tejedora: un tablón sobre dos pies, pulido por el uso, con su almohadilla. */
    private fun banco(l: Luces, r: Rayo, pie: Offset): Cosa {
        val x0 = pie.x - 22f; val x1 = pie.x + 22f; val yt = pie.y - 15f
        val forma = Path().apply { addRect(Rect(x0, yt - 3f, x1, yt + 3f)); addRect(Rect(x0 + 4f, yt, x0 + 8f, pie.y)); addRect(Rect(x1 - 8f, yt, x1 - 4f, pie.y)) }
        return Cosa(forma, pie) {
            val madera = l.tono(MADERA); val luz = l.tono(lerp(MADERA, Color(0xFFFFE8C0), 0.45f)); val sombra = l.tono(mult(MADERA_OSC, r.color, 0.5f))
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(x0, pie.y - 2.4f), Size(x1 - x0, 5f))
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), yt + 3f, yt + 11f), Offset(x0 + 6f, yt + 3f), Size(x1 - x0 - 12f, 8f))
            for (x in listOf(x0 + 4f, x1 - 9f)) { drawRect(Brush.horizontalGradient(if (r.lat >= 0f) listOf(sombra, madera, luz) else listOf(luz, madera, sombra), x, x + 5f), Offset(x, yt + 2f), Size(5f, pie.y - yt - 2f)) }
            val tablero = poligono(Offset(x0 + 2f, yt - 3.4f), Offset(x1 - 2f, yt - 3.4f), Offset(x1, yt), Offset(x0, yt))
            drawPath(tablero, Brush.verticalGradient(listOf(madera, luz), yt - 3.4f, yt))
            drawRect(Brush.verticalGradient(listOf(madera, sombra), yt, yt + 3f), Offset(x0, yt), Size(x1 - x0, 3f))
            for (i in 0..4) drawLine(sombra.copy(alpha = 0.4f), Offset(x0 + 4f + i * 9f, yt - 3f), Offset(x0 + 2f + i * 9.4f, yt), 0.35f)
            // La almohadilla, aplastada.
            val c = Offset(pie.x - 4f, yt - 3.2f)
            val coj = Path().apply { moveTo(c.x - 11f, c.y); cubicTo(c.x - 11f, c.y - 5f, c.x + 11f, c.y - 5f, c.x + 11f, c.y); cubicTo(c.x + 8f, c.y + 1.4f, c.x - 8f, c.y + 1.4f, c.x - 11f, c.y); close() }
            volumen(l, r, coj, c.x - 11f, c.x + 11f, Color(0xFF5E7A52))
            drawPath(Path().apply { moveTo(c.x - 9f, c.y - 2.2f); cubicTo(c.x - 3f, c.y - 1f, c.x + 3f, c.y - 1f, c.x + 9f, c.y - 2.2f) }, l.tono(SEDA), style = Stroke(0.6f))
        }
    }

    // ---------------------------------------------------------------- La pila, las macetas y la tinaja

    /** La pila de la fuente: un pie corto y una taza de mármol, gallonada, que rebosa en la alberca. */
    private fun pila(l: Luces, r: Rayo): Cosa {
        val p = PILA
        val taza = Path().apply {
            moveTo(p.x - 21f, p.y - 14f); cubicTo(p.x - 20f, p.y - 6f, p.x - 9f, p.y - 4.4f, p.x - 4.4f, p.y - 4f); lineTo(p.x - 6f, p.y + 2f); lineTo(p.x + 6f, p.y + 2f); lineTo(p.x + 4.4f, p.y - 4f)
            cubicTo(p.x + 9f, p.y - 4.4f, p.x + 20f, p.y - 6f, p.x + 21f, p.y - 14f); close()
        }
        val forma = Path().apply { addPath(taza); addOval(Rect(p.x - 21f, p.y - 17.6f, p.x + 21f, p.y - 10.4f)) }
        return Cosa(forma, p + Offset(0f, 3f)) {
            val az = Azar(Semilla.de("andalus", "pila"))
            // Su reflejo en la alberca.
            reflejoRoto(p.x - 15f, p.x + 15f, p.y + 5f, 12f, l.tono(MARMOL), 0.5f, az, 0.9f)
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(p.x - 9f, p.y), Size(18f, 4f))
            volumen(l, r, taza, p.x - 21f, p.x + 21f, MARMOL)
            clipPath(taza) {
                // Los gallones, el verdín y la sombra del labio.
                for (i in -5..5) drawPath(Path().apply { moveTo(p.x + i * 3.9f, p.y - 14f); cubicTo(p.x + i * 3.4f, p.y - 8f, p.x + i * 1.6f, p.y - 5f, p.x + i * 0.8f, p.y - 4f) }, l.tono(mult(MARMOL, r.color, 0.8f)).copy(alpha = 0.7f), style = Stroke(0.6f))
                repeat(7) { mancha(Offset(az.entre(p.x - 17f, p.x + 17f), az.entre(p.y - 11f, p.y - 5f)), az.entre(2f, 5f), az.entre(1f, 2f), l.tono(Color(0xFF8E9A6A)), 0.34f) }
                drawRect(Brush.verticalGradient(listOf(l.tono(mult(MARMOL, r.color, 0.9f)).copy(alpha = 0.7f), Color.Transparent), p.y - 13f, p.y - 9f), Offset(p.x - 22f, p.y - 13f), Size(44f, 4f))
            }
            // El labio y el agua de dentro.
            drawOval(l.tono(lerp(MARMOL, Color.White, 0.45f)), Offset(p.x - 21f, p.y - 17.6f), Size(42f, 7.2f))
            drawOval(lerp(l.tono(Color(0xFF3E7A78)), l.medio, 0.3f), Offset(p.x - 18.4f, p.y - 16.6f), Size(36.8f, 5.2f))
            drawOval(lerp(l.bajo, Color.White, 0.3f).copy(alpha = 0.5f), Offset(p.x - 11f, p.y - 15.8f), Size(13f, 1.8f))
            // El caño de bronce del surtidor.
            drawLine(l.tono(BRONCE), p + Offset(0f, -14.4f), p + Offset(0f, -18.6f), 1.4f)
            // Los hilos de agua que caen del labio a la alberca.
            val agua = lerp(l.tono(Color.White), Color.White, 0.2f)
            for (dx in listOf(-13f, 11f, 17f)) drawLine(agua.copy(alpha = 0.6f), p + Offset(dx, -12f), p + Offset(dx * 1.03f, 5f), 0.6f)
        }
    }

    /** Una maceta de barro con su planta: [cual] 0, arrayán recortado; 1, albahaca; 2, alhelíes. */
    private fun maceta(l: Luces, r: Rayo, pie: Offset, k: Float, cual: Int, est: Estacion): Cosa {
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx * k, pie.y + dy * k)
        val tiesto = Path().apply { moveTo(p(-6f, 0f)); lineTo(p(-9f, -14f)); lineTo(p(-10.4f, -14f)); lineTo(p(-10.4f, -17f)); lineTo(p(10.4f, -17f)); lineTo(p(10.4f, -14f)); lineTo(p(9f, -14f)); lineTo(p(6f, 0f)); close() }
        val copa = when (cual) { 0 -> oval(p(0f, -32f), 14f * k, 15f * k); 1 -> oval(p(0f, -25f), 12f * k, 9f * k); else -> oval(p(0f, -24f), 10f * k, 8f * k) }
        val forma = Path().apply { addPath(tiesto); addPath(copa) }
        return Cosa(forma, pie) {
            val az = Azar(Semilla.de("andalus", "maceta", cual))
            drawOval(Color.Black.copy(alpha = 0.22f), p(-10f, -2f), Size(21f * k, 4.6f * k))
            volumen(l, r, tiesto, pie.x - 10f * k, pie.x + 10f * k, if (cual == 1) VIDRIADO else BARRO)
            clipPath(tiesto) {
                drawLine(l.tono(Color(0xFF7A4A2E)).copy(alpha = 0.7f), p(-11f, -13.6f), p(11f, -13.6f), 0.6f * k)
                repeat(5) { mancha(p(az.entre(-8f, 8f), az.entre(-12f, -1f)), az.entre(2f, 5f) * k, az.entre(1.4f, 3f) * k, l.tono(if (it % 2 == 0) Color(0xFFE8D8C0) else Color(0xFF6E7A4A)), 0.26f) }
                if (cual == 1) for (i in -2..2) drawLine(l.tono(Color(0xFF2A2428)).copy(alpha = 0.7f), p(i * 3.4f, -12f), p(i * 2.6f, -2f), 0.5f * k)       // verde y manganeso
            }
            val verde = when (cual) { 0 -> ARRAYAN; 1 -> Color(0xFF6E9A4A); else -> Color(0xFF5A8250) }
            val hondo = l.tono(lerp(verde, Color(0xFF1E2E26), 0.5f)); val medio = l.tono(verde); val claro = l.tono(lerp(verde, Color(0xFFE6E6A0), 0.45f * r.dia + 0.1f))
            val c = when (cual) { 0 -> p(0f, -32f); 1 -> p(0f, -25f); else -> p(0f, -24f) }
            val rx = when (cual) { 0 -> 14f; 1 -> 12f; else -> 10f } * k; val ry = when (cual) { 0 -> 15f; 1 -> 9f; else -> 8f } * k
            // El tallo y la mata: una masa oscura y, encima, hojas menudas; las del lado del sol, claras.
            drawLine(l.tono(Color(0xFF5A4A34)), p(0f, -16f), c + Offset(0f, ry * 0.4f), 1.4f * k)
            drawPath(copa, hondo)
            repeat((90 * k).toInt()) {
                val a = az.entre(0f, 6.283f); val d = az.real().let { kotlin.math.sqrt(it) }
                val q = c + Offset(cos(a) * rx * d, sin(a) * ry * d)
                val lado = (q.x - c.x) / rx * r.lat - (q.y - c.y) / ry * 0.6f
                val g = az.entre(0f, 6.283f); val n = az.entre(1.4f, 2.6f) * k
                drawLine(if (lado > 0.25f && az.real() > 0.3f) claro else if (az.real() > 0.5f) medio else hondo, q, q + Offset(cos(g) * n, sin(g) * n * 0.7f), 1.3f * k, StrokeCap.Round)
            }
            // Las flores, según la planta y la estación.
            val flor = when { cual == 2 && est != Estacion.INVIERNO -> Color(0xFFC84A6A); cual == 0 && est == Estacion.VERANO -> Color(0xFFFFFDF2); else -> null }
            if (flor != null) repeat(if (cual == 2) 11 else 14) { val a = az.entre(0f, 6.283f); val d = az.entre(0.2f, 0.95f); drawCircle(l.tono(flor), (if (cual == 2) 1.3f else 0.7f) * k, c + Offset(cos(a) * rx * d, sin(a) * ry * d)) }
        }
    }

    /** La tinaja del agua, con su tapa de madera y el cazo colgado del borde. */
    private fun tinaja(l: Luces, r: Rayo, pie: Offset, k: Float): Cosa {
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx * k, pie.y + dy * k)
        val forma = Path().apply {
            moveTo(p(-6f, 0f)); cubicTo(p(-18f, -8f), p(-18f, -30f), p(-9f, -36f)); lineTo(p(-11f, -37f)); lineTo(p(-11f, -40.5f)); lineTo(p(11f, -40.5f)); lineTo(p(11f, -37f)); lineTo(p(9f, -36f))
            cubicTo(p(18f, -30f), p(18f, -8f), p(6f, 0f)); close()
        }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.24f), p(-16f, -2.6f), Size(33f * k, 6f * k))
            volumen(l, r, forma, pie.x - 17f * k, pie.x + 17f * k, Color(0xFFC88C5C))
            val sombra = l.tono(Color(0xFF7E4A2C))
            clipPath(forma) {
                // Una cenefa incisa, las marcas del torno, el rezumado y el verdín del pie.
                for (y in listOf(-27f, -24.4f)) drawLine(sombra.copy(alpha = 0.8f), p(-18f, y), p(18f, y), 0.8f * k)
                for (i in -5..5) drawLine(sombra.copy(alpha = 0.6f), p(i * 3.2f, -27f), p(i * 3.2f + 1.6f, -24.4f), 0.5f)
                val az = Azar(Semilla.de("andalus", "tinaja"))
                repeat(12) { val y = az.entre(-34f, -2f); drawLine(sombra.copy(alpha = 0.14f), p(-18f, y), p(18f, y), 0.5f) }
                repeat(8) { mancha(p(az.entre(-14f, 14f), az.entre(-22f, -3f)), az.entre(3f, 7f) * k, az.entre(3f, 7f) * k, if (it % 3 == 0) l.tono(Color(0xFFE6D2B8)) else l.tono(Color(0xFF8A5A3A)), 0.24f) }
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF6E7A4A)).copy(alpha = 0.4f)), p(0f, -8f).y, pie.y), p(-18f, -8f), Size(36f * k, 8f * k))
            }
            drawRoundRect(l.tono(Color(0xFFD2925E)), p(-11f, -40.5f), Size(22f * k, 3.6f * k), CornerRadius(1.6f * k))
            drawOval(l.tono(MADERA_OSC), p(-9.6f, -43.2f), Size(21f * k, 4f * k)); drawOval(l.tono(MADERA), p(-8.6f, -43.4f), Size(19f * k, 2.8f * k))
            // El cazo de cobre, colgado del borde.
            drawLine(l.tono(BRONCE), p(-11f, -39f), p(-13.4f, -27f), 0.9f * k)
            drawOval(l.tono(lerp(BRONCE, Color(0xFFFFE0A0), 0.3f)), p(-16.6f, -28f), Size(6.4f * k, 4.6f * k)); drawOval(l.tono(Color(0xFF6A4A26)), p(-15.8f, -27.6f), Size(4.8f * k, 2.4f * k))
        }
    }

    // ---------------------------------------------------------------- La mesa, los cojines, la bandeja y el anafe

    /** Un cojín de seda en el suelo: mullido, con un galón y borlas en las puntas. */
    private fun cojin(l: Luces, r: Rayo, pie: Offset, propio: Color, k: Float): Cosa {
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx * k, pie.y + dy * k)
        val forma = Path().apply {
            moveTo(p(-17f, -2f)); cubicTo(p(-18f, -9f), p(-12f, -12.4f), p(0f, -12f)); cubicTo(p(12f, -12.4f), p(18f, -9f), p(17f, -2f))
            cubicTo(p(12f, 1f), p(-12f, 1f), p(-17f, -2f)); close()
        }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.2f), p(-17f, -3f), Size(35f * k, 5.4f * k))
            volumen(l, r, forma, pie.x - 17f * k, pie.x + 17f * k, propio)
            clipPath(forma) {
                // El galón de oro, que sigue la panza, y el brillo de la seda.
                drawPath(Path().apply { moveTo(p(-18f, -5.4f)); cubicTo(p(-8f, -3f), p(8f, -3f), p(18f, -5.4f)) }, l.tono(ORO), style = Stroke(1.1f * k))
                drawPath(Path().apply { moveTo(p(-12f, -10f)); cubicTo(p(-4f, -8.6f), p(4f, -8.6f), p(12f, -10f)) }, l.tono(lerp(propio, Color.White, 0.4f)).copy(alpha = 0.5f), style = Stroke(1.6f * k, cap = StrokeCap.Round))
                for (i in -2..2) drawCircle(l.tono(ORO).copy(alpha = 0.8f), 0.7f * k, p(i * 6f, -7.4f + kotlin.math.abs(i) * 0.5f))
            }
            for (s in listOf(-1f, 1f)) { drawLine(l.tono(ORO), p(s * 17f, -2.4f), p(s * 19.4f, 0.6f), 0.8f * k); drawCircle(l.tono(ORO), 1.2f * k, p(s * 19.6f, 1.2f)) }
        }
    }

    /** La mesa baja, redonda, de madera con taracea, y encima el objeto del día. */
    private fun mesa(m: Momento, l: Luces, r: Rayo): Cosa {
        val c = MESA; val rx = 40f; val ry = 10.6f; val pie = Offset(c.x, 281f)
        val tablero = oval(c, rx, ry)
        val forma = Path().apply {
            addPath(tablero); addRect(Rect(c.x - rx, c.y, c.x + rx, c.y + 3.6f))
            for (dx in listOf(-27f, 27f)) addPath(palo(Offset(c.x + dx, c.y + 2f), Offset(c.x + dx * 1.08f, pie.y), 6f))
        }
        return Cosa(forma, pie) {
            val madera = l.tono(MADERA); val luz = l.tono(lerp(MADERA, Color(0xFFFFE8C0), 0.45f)); val sombra = l.tono(mult(MADERA_OSC, r.color, 0.5f)); val oscura = l.tono(MADERA_OSC)
            drawOval(Color.Black.copy(alpha = 0.24f), Offset(c.x - rx, pie.y - 3f), Size(rx * 2f, 6f))
            // Lo de debajo del tablero, en sombra, y las tres patas torneadas.
            drawOval(Color.Black.copy(alpha = 0.3f), Offset(c.x - rx * 0.8f, c.y + 7f), Size(rx * 1.6f, 11f))
            for (dx in listOf(-27f, 2f, 27f)) {
                val a = Offset(c.x + dx, c.y + 4f); val b = Offset(c.x + dx * 1.08f, if (dx == 2f) pie.y - 5f else pie.y)
                drawPath(palo(a, b, 6f), Brush.horizontalGradient(if (r.lat >= 0f) listOf(sombra, madera, luz) else listOf(luz, madera, sombra), a.x - 3f, a.x + 3f))
                // El torneado: dos anillos y el pie, más ancho.
                for (t in listOf(0.38f, 0.62f)) { val q = a + (b - a) * t; drawOval(oscura, Offset(q.x - 3.8f, q.y - 1.6f), Size(7.6f, 3.2f)); drawOval(luz, Offset(q.x - 3.8f, q.y - 2.2f), Size(7.6f, 1.6f)) }
                drawOval(oscura, Offset(b.x - 3.8f, b.y - 2.2f), Size(7.6f, 2.8f))
            }
            // El canto y el tablero, con su estrella de taracea, gastada.
            drawPath(Path().apply { addOval(Rect(c.x - rx, c.y - ry + 5f, c.x + rx, c.y + ry + 5f)) }, Brush.horizontalGradient(if (r.lat >= 0f) listOf(sombra, madera, luz) else listOf(luz, madera, sombra), c.x - rx, c.x + rx))
            drawPath(tablero, Brush.verticalGradient(listOf(madera, luz), c.y - ry, c.y + ry))
            clipPath(tablero) {
                drawOval(oscura.copy(alpha = 0.8f), Offset(c.x - rx + 2.4f, c.y - ry + 1f), Size(rx * 2f - 4.8f, ry * 2f - 2f), style = Stroke(0.7f))
                for (i in 0 until 8) { val a = i * 0.7854f; drawLine(l.tono(Color(0xFFF2E6CC)).copy(alpha = 0.8f), c + Offset(cos(a) * rx * 0.5f, sin(a) * ry * 0.5f), c + Offset(cos(a + 1.18f) * rx * 0.86f, sin(a + 1.18f) * ry * 0.86f), 0.5f); drawLine(oscura.copy(alpha = 0.7f), c + Offset(cos(a) * rx * 0.5f, sin(a) * ry * 0.5f), c + Offset(cos(a - 1.18f) * rx * 0.86f, sin(a - 1.18f) * ry * 0.86f), 0.5f) }
                val az = Azar(Semilla.de("andalus", "mesa"))
                repeat(5) { mancha(c + Offset(az.entre(-rx, rx), az.entre(-ry, ry)), az.entre(4f, 9f), az.entre(1f, 2f), sombra, 0.2f) }
            }
            // El objeto del día, con su sombra sobre el tablero.
            val objeto = m.ficha.objeto
            if (objeto != null && objeto != "laud" && objeto != "jarra" && r.fuerza > 0.05f) clipPath(tablero) { drawOval(r.color, Offset(c.x - 21f + r.ax * 3f, c.y - 2f), Size(42f, 6.4f), alpha = r.fuerza * 0.7f, blendMode = androidx.compose.ui.graphics.BlendMode.Multiply) }
            val sobre = c + Offset(0f, 2.2f)
            when (objeto) {
                "lanzadera" -> lanzadera(l, sobre, KO)
                "peine" -> peine(l, r, sobre, KO)
                "huso" -> huso(l, sobre, KO)
                "seda" -> madeja(l, r, sobre, KO, SEDA)
                "carmesi" -> grana(l, r, sobre, KO)
                "candil" -> candilDeBarro(l, sobre, K_CANDIL)
                "alcuza" -> alcuza(l, r, sobre, KO)
                "agua_rosas" -> redoma(l, r, sobre, KO)
                "alhena" -> alhena(l, r, sobre, KO)
                "dirham" -> dirhams(l, sobre, KO)
                else -> Unit
            }
        }
    }

    /** La bandeja de la tarde, en la estera: de latón, con la jarra, los vasos y, en Ramadán, los dátiles y el pan. */
    private fun bandeja(m: Momento, l: Luces, r: Rayo): Cosa {
        val c = Offset(146f, 279f)
        val forma = Path().apply { addOval(Rect(c.x - 17f, c.y - 4.6f, c.x + 17f, c.y + 4.6f)); addPath(jarroForma(c + Offset(-7f, 1f), 1f)) }
        return Cosa(forma, c + Offset(0f, 4f)) {
            val laton = l.tono(lerp(BRONCE, Color(0xFFF0D080), 0.4f))
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(c.x - 17f, c.y - 2.6f), Size(34f, 8.4f))
            drawOval(l.tono(mult(BRONCE, r.color, 0.5f)), Offset(c.x - 17f, c.y - 3.6f), Size(34f, 9.2f))
            drawOval(laton, Offset(c.x - 17f, c.y - 4.6f), Size(34f, 9.2f)); drawOval(l.tono(BRONCE), Offset(c.x - 14.4f, c.y - 3.6f), Size(28.8f, 7.2f), style = Stroke(0.5f))
            drawOval(l.tono(lerp(BRONCE, Color.White, 0.6f)).copy(alpha = 0.6f), Offset(c.x - 11f + r.lat * 4f, c.y - 3f), Size(9f, 2f))
            // Los vasos de vidrio.
            for ((dx, dy) in listOf(4f to -1.6f, 10f to 0.6f, 3f to 2.2f)) {
                val p = c + Offset(dx, dy)
                drawPath(poligono(p + Offset(-1.6f, 0f), p + Offset(1.6f, 0f), p + Offset(2.1f, -5f), p + Offset(-2.1f, -5f)), l.tono(Color(0xFFBFE0D6)).copy(alpha = 0.75f))
                drawLine(l.tono(Color.White).copy(alpha = 0.8f), p + Offset(1.3f, -0.6f), p + Offset(1.7f, -4.6f), 0.4f)
            }
            if (EscenaAndalus.ramadan(m)) {
                // El cuenco de dátiles y la hogaza, para romper el ayuno.
                val q = c + Offset(-19f, -7f)
                drawOval(l.tono(mult(VIDRIADO, r.color, 0.5f)), Offset(q.x - 7f, q.y - 2f), Size(14f, 5f)); drawOval(l.tono(VIDRIADO), Offset(q.x - 7f, q.y - 3.4f), Size(14f, 5f))
                for (i in 0..5) drawOval(l.tono(Color(0xFF6A3A22)), Offset(q.x - 5f + i * 1.7f, q.y - 4.4f + (i % 2) * 1.1f), Size(2.6f, 1.6f))
                val h = c + Offset(12f, -8f)
                drawOval(l.tono(Color(0xFFD9A860)), Offset(h.x - 6.4f, h.y - 3.4f), Size(12.8f, 5.6f)); drawOval(l.tono(Color(0xFFF0CE8E)), Offset(h.x - 4.6f, h.y - 3f), Size(8f, 2.4f))
            }
            jarro(l, r, c + Offset(-7f, 1f), 1f)
        }
    }

    private fun jarroForma(pie: Offset, k: Float) = Path().apply {
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx * k, pie.y + dy * k)
        moveTo(p(-3f, 0f)); cubicTo(p(-6.6f, -3f), p(-6f, -9f), p(-2f, -11f)); lineTo(p(-1.6f, -15f)); lineTo(p(-3f, -17f)); lineTo(p(3f, -17f)); lineTo(p(1.6f, -15f)); lineTo(p(2f, -11f))
        cubicTo(p(6f, -9f), p(6.6f, -3f), p(3f, 0f)); close()
    }

    /** Un jarro de cerámica vidriada, con su asa. */
    private fun DrawScope.jarro(l: Luces, r: Rayo, pie: Offset, k: Float) {
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx * k, pie.y + dy * k)
        val forma = jarroForma(pie, k)
        drawPath(Path().apply { moveTo(p(2f, -15f)); cubicTo(p(8f, -15f), p(8.6f, -7f), p(5f, -5.4f)) }, l.tono(lerp(VIDRIADO, Color(0xFF1E3A30), 0.3f)), style = Stroke(1.3f * k, cap = StrokeCap.Round))
        volumen(l, r, forma, pie.x - 6.4f * k, pie.x + 6.4f * k, VIDRIADO)
        clipPath(forma) { for (y in listOf(-8.6f, -3.4f)) drawLine(l.tono(Color(0xFF2A2428)).copy(alpha = 0.7f), p(-7f, y), p(7f, y), 0.5f * k); drawLine(l.tono(Color.White).copy(alpha = 0.6f), p(r.lat * 2.4f, -9f), p(r.lat * 3f, -3f), 0.7f * k) }
    }

    /** El anafe: el hornillo de barro, con su boca para la leña y la olla encima a la hora de guisar. */
    private fun anafe(m: Momento, l: Luces, r: Rayo): Cosa {
        val pie = ANAFE; val guisa = EscenaAndalus.guisando(m); val k = K_ANAFE
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx * k, pie.y + dy * k)
        val cuerpo = Path().apply { moveTo(p(-9f, 0f)); lineTo(p(-12.4f, -11f)); lineTo(p(-13.4f, -11f)); lineTo(p(-13.4f, -14f)); lineTo(p(13.4f, -14f)); lineTo(p(13.4f, -11f)); lineTo(p(12.4f, -11f)); lineTo(p(9f, 0f)); close() }
        val olla = Path().apply { moveTo(p(-7f, -13f)); cubicTo(p(-13f, -17f), p(-11.6f, -26f), p(-6f, -28f)); lineTo(p(-7.4f, -30.4f)); lineTo(p(7.4f, -30.4f)); lineTo(p(6f, -28f)); cubicTo(p(11.6f, -26f), p(13f, -17f), p(7f, -13f)); close() }
        val forma = Path().apply { addPath(cuerpo); if (guisa) addPath(olla) }
        return Cosa(forma, pie) {
            val az = Azar(Semilla.de("andalus", "ceniza"))
            repeat(5) { mancha(p(az.entre(-16f, 18f), az.entre(-1f, 2.6f)), az.entre(5f, 11f) * k, az.entre(1.2f, 2.4f) * k, l.tono(Color(0xFF8E8880)), 0.4f) }
            drawOval(Color.Black.copy(alpha = 0.22f), p(-13f, -2.4f), Size(27f * k, 5.4f * k))
            volumen(l, r, cuerpo, pie.x - 13f * k, pie.x + 13f * k, Color(0xFFB0683E))
            clipPath(cuerpo) {
                // El tizne que sube de la boca, y unas rayas incisas.
                drawRect(Brush.verticalGradient(listOf(l.tono(Color(0xFF2A2220)).copy(alpha = 0.7f), Color.Transparent), p(0f, -14f).y, p(0f, -6f).y), p(-14f, -14f), Size(28f * k, 8f * k))
                for (i in -3..3) drawLine(l.tono(Color(0xFF6E3A22)).copy(alpha = 0.6f), p(i * 3.4f - 1f, -3f), p(i * 3.4f + 1f, -1f), 0.5f * k)
            }
            // La boca: un arquillo, con las brasas dentro.
            val boca = Path().apply { moveTo(p(-4f, 0f)); lineTo(p(-4f, -4f)); cubicTo(p(-4f, -8.4f), p(4f, -8.4f), p(4f, -4f)); lineTo(p(4f, 0f)); close() }
            drawPath(boca, l.tono(Color(0xFF241A18)))
            if (m.hacer != io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer.DORMIR) clipPath(boca) { drawOval(Color(0xFFFF7A2A).copy(alpha = if (guisa) 0.9f else 0.5f), p(-3.4f, -2.6f), Size(6.8f * k, 3f * k)); drawOval(Color(0xFFFFD27A).copy(alpha = if (guisa) 0.8f else 0.3f), p(-1.8f, -2f), Size(3.6f * k, 1.4f * k)) }
            drawOval(l.tono(Color(0xFF3A2A22)), p(-13.4f, -16f), Size(26.8f * k, 4.4f * k))
            if (guisa) {
                // La olla, tiznada por abajo, con su tapadera y sus dos asas.
                volumen(l, r, olla, pie.x - 12f * k, pie.x + 12f * k, Color(0xFF8A5A40))
                clipPath(olla) { drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF1E1614)).copy(alpha = 0.85f)), p(0f, -24f).y, p(0f, -13f).y), p(-14f, -24f), Size(28f * k, 11f * k)) }
                for (s in listOf(-1f, 1f)) drawPath(Path().apply { moveTo(p(s * 9.6f, -25f)); cubicTo(p(s * 13.6f, -25.4f), p(s * 13.6f, -21f), p(s * 10.6f, -20.6f)) }, l.tono(Color(0xFF6A4230)), style = Stroke(1.2f * k, cap = StrokeCap.Round))
                drawOval(l.tono(Color(0xFF9A6A4C)), p(-7.6f, -32f), Size(15.2f * k, 3.4f * k)); drawCircle(l.tono(Color(0xFF6A4230)), 1.1f * k, p(0f, -32.6f))
            }
        }
    }

    /** El canasto de la seda: de esparto, con las madejas crudas, amarillas, y unas canillas ya devanadas. */
    private fun canasto(l: Luces, r: Rayo, pie: Offset): Cosa {
        val forma = Path().apply { moveTo(pie.x - 13f, pie.y); lineTo(pie.x - 17f, pie.y - 15f); lineTo(pie.x + 17f, pie.y - 15f); lineTo(pie.x + 13f, pie.y); close(); addOval(Rect(pie.x - 17f, pie.y - 22f, pie.x + 17f, pie.y - 10f)) }
        return Cosa(forma, pie) {
            val cesto = Path().apply { moveTo(pie.x - 13f, pie.y); lineTo(pie.x - 17f, pie.y - 15f); lineTo(pie.x + 17f, pie.y - 15f); lineTo(pie.x + 13f, pie.y); cubicTo(pie.x + 6f, pie.y + 2f, pie.x - 6f, pie.y + 2f, pie.x - 13f, pie.y); close() }
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(pie.x - 15f, pie.y - 2f), Size(31f, 5f))
            volumen(l, r, cesto, pie.x - 17f, pie.x + 17f, Color(0xFFC9A860))
            clipPath(cesto) {
                // El trenzado.
                for (i in 0..5) drawLine(l.tono(Color(0xFF8A6E38)).copy(alpha = 0.7f), Offset(pie.x - 18f, pie.y - 13f + i * 2.6f), Offset(pie.x + 18f, pie.y - 13f + i * 2.6f), 0.5f)
                for (i in -8..8) drawLine(l.tono(Color(0xFF8A6E38)).copy(alpha = 0.5f), Offset(pie.x + i * 2.1f, pie.y - 15f), Offset(pie.x + i * 1.7f + 1f, pie.y + 2f), 0.4f)
            }
            drawOval(l.tono(Color(0xFF7A5E30)), Offset(pie.x - 17f, pie.y - 18.4f), Size(34f, 7f))
            // Las madejas, que asoman.
            for ((dx, dy, color) in listOf(Triple(-8f, -19f, SEDA), Triple(3f, -21f, lerp(SEDA, Color.White, 0.35f)), Triple(9f, -18f, SEDA), Triple(-2f, -17f, lerp(SEDA, Color(0xFFB08A2A), 0.3f)))) {
                val o = oval(Offset(pie.x + dx, pie.y + dy + 2f), 7f, 4f)
                volumen(l, r, o, pie.x + dx - 7f, pie.x + dx + 7f, color)
                clipPath(o) { for (k in -3..3) drawLine(l.tono(Color(0xFFA8862A)).copy(alpha = 0.5f), Offset(pie.x + dx + k * 2f - 2f, pie.y + dy - 2f), Offset(pie.x + dx + k * 2f + 2f, pie.y + dy + 6f), 0.35f) }
            }
            // Dos canillas de hilo teñido, caídas junto al canasto.
            for ((dx, color) in listOf(-22f to CARMESI, 21f to ANIL)) { val q = Offset(pie.x + dx, pie.y - 0.6f); drawLine(l.tono(MADERA_OSC), q + Offset(-4f, 0.6f), q + Offset(4f, -0.6f), 1f, StrokeCap.Round); drawLine(l.tono(color), q + Offset(-2.4f, 0.4f), q + Offset(2.4f, -0.4f), 2.6f, StrokeCap.Round) }
        }
    }

    /** La escoba de palma, apoyada en la tapia. */
    private fun escoba(l: Luces, r: Rayo): Cosa {
        val pie = Offset(226f, 236f); val punta = Offset(240f, 186f)
        val mango = palo(pie + Offset(1.6f, -12f), punta, 1.8f)
        val cabeza = Path().apply { moveTo(pie.x + 0.6f, pie.y - 16f); lineTo(pie.x + 3.6f, pie.y - 15f); lineTo(pie.x + 5f, pie.y); lineTo(pie.x - 7f, pie.y + 0.6f); close() }
        val forma = Path().apply { addPath(mango); addPath(cabeza) }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(pie.x - 8f, pie.y - 1.4f), Size(15f, 3f))
            drawPath(mango, l.tono(MADERA)); drawLine(l.tono(lerp(MADERA, Color.White, 0.4f)), pie + Offset(1.2f, -12f), punta + Offset(-0.4f, 0f), 0.4f)
            drawPath(cabeza, l.tono(Color(0xFFC8B070)))
            clipPath(cabeza) { for (i in 0..9) drawLine(l.tono(Color(0xFF8A7438)).copy(alpha = 0.7f), Offset(pie.x + 2f, pie.y - 15f), Offset(pie.x - 7f + i * 1.3f, pie.y + 1f), 0.4f) }
            drawLine(l.tono(Color(0xFF7A5A30)), pie + Offset(0.4f, -13.4f), pie + Offset(4f, -12.4f), 1.2f)
        }
    }

    // ---------------------------------------------------------------- Las lámparas

    /** Una lámpara del Ramadán: de vidrio verdoso con su casquete de bronce. Cuelga de una cadena, o está de pie sobre la tapia. */
    fun DrawScope.lampara(l: Luces, c: Offset, encendida: Boolean) {
        val bronce = l.tono(BRONCE); val bronceLuz = l.tono(lerp(BRONCE, Color(0xFFFFE8A0), 0.4f))
        val dePie = c.y > 165f
        if (dePie) {
            drawLine(bronce, c + Offset(0f, 4f), c + Offset(0f, CAP - c.y + 0.6f), 1f); drawOval(bronce, Offset(c.x - 3.4f, CAP - 1.2f), Size(6.8f, 2f))
        } else {
            for (i in 0..5) drawOval(bronce, Offset(c.x - 0.6f, c.y - 6.4f - i * 2.1f), Size(1.2f, 2.2f), style = Stroke(0.4f))
            if (c.x > 300f) drawLine(bronce, c + Offset(0f, -19f), c + Offset(0f, -58f), 0.4f)
        }
        val globo = Path().apply { moveTo(c.x - 2.2f, c.y - 4.4f); cubicTo(c.x - 6.4f, c.y - 2f, c.x - 5.4f, c.y + 4f, c.x - 1.8f, c.y + 5f); lineTo(c.x + 1.8f, c.y + 5f); cubicTo(c.x + 5.4f, c.y + 4f, c.x + 6.4f, c.y - 2f, c.x + 2.2f, c.y - 4.4f); close() }
        val vidrio = if (encendida) lerp(Color(0xFFFFE9A8), Color(0xFFFFC060), 0.3f) else l.tono(Color(0xFF9CC4B4))
        drawPath(globo, vidrio.copy(alpha = if (encendida) 0.95f else 0.8f))
        drawPath(globo, (if (encendida) Color(0xFFD08A30) else l.tono(Color(0xFF5E8A7A))).copy(alpha = 0.8f), style = Stroke(0.5f))
        if (!encendida) drawLine(l.tono(Color.White).copy(alpha = 0.7f), c + Offset(-2.6f, -1.6f), c + Offset(-2.2f, 2.4f), 0.5f)
        drawPath(poligono(c + Offset(-2.6f, -4.2f), c + Offset(2.6f, -4.2f), c + Offset(1.2f, -6.6f), c + Offset(-1.2f, -6.6f)), bronceLuz)
        drawRect(bronce, Offset(c.x - 2f, c.y + 4.8f), Size(4f, 1.2f))
    }

    // ---------------------------------------------------------------- Los doce objetos del día

    /** La lanzadera: una barca de madera pulida con su canilla de hilo dentro. */
    fun DrawScope.lanzadera(l: Luces, sobre: Offset, k: Float) {
        fun p(dx: Float, dy: Float) = Offset(sobre.x + dx * k, sobre.y + dy * k)
        val forma = Path().apply { moveTo(p(-16f, -3f)); cubicTo(p(-9f, -7.4f), p(9f, -7.4f), p(16f, -3f)); cubicTo(p(9f, 1.2f), p(-9f, 1.2f), p(-16f, -3f)); close() }
        drawOval(Color.Black.copy(alpha = 0.2f), p(-14f, -1.4f), Size(28f * k, 3f * k))
        drawPath(forma, Brush.verticalGradient(listOf(l.tono(lerp(MADERA, Color(0xFFFFE8C0), 0.55f)), l.tono(MADERA), l.tono(MADERA_OSC)), p(0f, -7f).y, p(0f, 1.2f).y))
        drawPath(Path().apply { moveTo(p(-14f, -3.4f)); cubicTo(p(-8f, -6.4f), p(8f, -6.4f), p(14f, -3.4f)) }, l.tono(Color(0xFFFFF0D0)).copy(alpha = 0.7f), style = Stroke(0.5f * k))
        // El hueco, con la canilla de hilo dentro.
        drawRoundRect(l.tono(Color(0xFF4A3222)), p(-6.4f, -4.6f), Size(12.8f * k, 3f * k), CornerRadius(1.4f * k))
        drawRoundRect(l.tono(CARMESI), p(-5f, -4.3f), Size(10f * k, 2.2f * k), CornerRadius(1f * k))
        for (i in 0..5) drawLine(l.tono(lerp(CARMESI, Color.White, 0.4f)).copy(alpha = 0.7f), p(-4.4f + i * 1.7f, -4.3f), p(-4f + i * 1.7f, -2.1f), 0.3f * k)
        drawPath(Path().apply { moveTo(p(5f, -3.2f)); cubicTo(p(11f, -9f), p(17f, -1f), p(21f, -4f)) }, l.tono(CARMESI), style = Stroke(0.45f * k, cap = StrokeCap.Round))
        drawCircle(l.tono(BRONCE), 0.8f * k, p(-15.4f, -3f)); drawCircle(l.tono(BRONCE), 0.8f * k, p(15.4f, -3f))
    }

    /** El peine del telar: un marco con cientos de púas de caña, apoyado de canto. */
    private fun DrawScope.peine(l: Luces, r: Rayo, sobre: Offset, k: Float) {
        fun p(dx: Float, dy: Float) = Offset(sobre.x + dx * k, sobre.y + dy * k)
        val a = p(-14f, -1f); val b = p(14f, 0.6f); val c = p(15f, -9.4f); val d = p(-13f, -11f)
        drawOval(Color.Black.copy(alpha = 0.2f), p(-15f, -1.2f), Size(31f * k, 3f * k))
        drawPath(poligono(a, b, c, d), l.tono(Color(0xFFE6D6A4)).copy(alpha = 0.9f))
        for (i in 0..40) { val t = i / 40f; drawLine(l.tono(Color(0xFF9A8450)).copy(alpha = 0.8f), a + (b - a) * t, d + (c - d) * t, 0.3f * k) }
        for ((u, v) in listOf(a to b, d to c)) { drawLine(l.tono(MADERA_OSC), u, v, 2.2f * k, StrokeCap.Round); drawLine(l.tono(lerp(MADERA, Color.White, 0.3f)), u + Offset(0f, -0.6f * k), v + Offset(0f, -0.6f * k), 0.5f * k) }
        for (i in 0..5) { val t = i / 5f; drawLine(l.tono(Color(0xFFF2E6C0)), a + (b - a) * t + Offset(-0.8f, 0f), a + (b - a) * t + Offset(0.8f, 0f), 2.4f * k) }
    }

    /** El huso: el palo, la tortera de hueso abajo y el copo de hilo, con su cabo suelto. */
    private fun DrawScope.huso(l: Luces, sobre: Offset, k: Float) {
        fun p(dx: Float, dy: Float) = Offset(sobre.x + dx * k, sobre.y + dy * k)
        drawOval(Color.Black.copy(alpha = 0.2f), p(-13f, -1f), Size(27f * k, 2.6f * k))
        val a = p(-13f, -1.4f); val b = p(13f, -7.4f)
        drawLine(l.tono(MADERA_OSC), a, b, 1.2f * k, StrokeCap.Round)
        val copo = Path().apply { moveTo(p(-4f, -1.6f)); cubicTo(p(-1f, -8f), p(5f, -9.4f), p(8f, -6.2f)); cubicTo(p(6f, -3f), p(0f, 0f), p(-4f, -1.6f)); close() }
        drawPath(copo, Brush.verticalGradient(listOf(l.tono(lerp(SEDA, Color.White, 0.4f)), l.tono(lerp(SEDA, Color(0xFF8A6A20), 0.3f))), p(0f, -9f).y, p(0f, 0f).y))
        clipPath(copo) { for (i in 0..7) drawLine(l.tono(Color(0xFFB8963A)).copy(alpha = 0.7f), p(-4f + i * 1.6f, -9f), p(-5f + i * 1.6f, 1f), 0.3f * k) }
        drawOval(l.tono(Color(0xFFF2EAD8)), p(-11.6f, -3.6f), Size(3.4f * k, 5f * k)); drawOval(l.tono(Color(0xFFB8AC94)), p(-11.6f, -3.6f), Size(3.4f * k, 5f * k), style = Stroke(0.4f))
        drawPath(Path().apply { moveTo(p(8f, -6.2f)); cubicTo(p(12f, -2f), p(15f, -3f), p(17f, 0.4f)) }, l.tono(SEDA), style = Stroke(0.45f * k, cap = StrokeCap.Round))
    }

    /** Una madeja de seda, retorcida en ocho, lustrosa. */
    private fun DrawScope.madeja(l: Luces, r: Rayo, sobre: Offset, k: Float, propio: Color) {
        fun p(dx: Float, dy: Float) = Offset(sobre.x + dx * k, sobre.y + dy * k)
        drawOval(Color.Black.copy(alpha = 0.2f), p(-13f, -1.6f), Size(26f * k, 3.4f * k))
        val forma = Path().apply {
            moveTo(p(-13f, -4f)); cubicTo(p(-13f, -9f), p(-4f, -9f), p(0f, -4.6f)); cubicTo(p(4f, -9f), p(13f, -9f), p(13f, -4f))
            cubicTo(p(13f, 1f), p(4f, 1f), p(0f, -3.4f)); cubicTo(p(-4f, 1f), p(-13f, 1f), p(-13f, -4f)); close()
        }
        volumen(l, r, forma, sobre.x - 13f * k, sobre.x + 13f * k, propio)
        clipPath(forma) {
            for (i in -6..6) drawPath(Path().apply { moveTo(p(i * 2.2f - 3f, -9f)); cubicTo(p(i * 2.2f, -6f), p(i * 2.2f, -2f), p(i * 2.2f + 3f, 1f)) }, l.tono(lerp(propio, Color(0xFF7A5A18), 0.5f)).copy(alpha = 0.55f), style = Stroke(0.35f * k))
            drawPath(Path().apply { moveTo(p(-11f, -6f)); cubicTo(p(-6f, -8f), p(-3f, -7f), p(-1f, -5f)) }, l.tono(Color.White).copy(alpha = 0.6f), style = Stroke(0.9f * k, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(p(2f, -6f)); cubicTo(p(5f, -8f), p(9f, -8f), p(11f, -6f)) }, l.tono(Color.White).copy(alpha = 0.5f), style = Stroke(0.9f * k, cap = StrokeCap.Round))
        }
        drawLine(l.tono(lerp(propio, Color(0xFF6A4A10), 0.5f)), p(-1.2f, -6f), p(1.2f, -2f), 1.6f * k, StrokeCap.Round)
    }

    /** El carmesí: el saquito de grana, abierto, con los granos derramados, y una madejita ya teñida. */
    private fun DrawScope.grana(l: Luces, r: Rayo, sobre: Offset, k: Float) {
        fun p(dx: Float, dy: Float) = Offset(sobre.x + dx * k, sobre.y + dy * k)
        drawOval(Color.Black.copy(alpha = 0.2f), p(-14f, -1.6f), Size(28f * k, 3.4f * k))
        val saco = Path().apply { moveTo(p(-13f, 0f)); cubicTo(p(-16f, -5f), p(-12f, -9f), p(-9f, -9.6f)); lineTo(p(-10.4f, -12.4f)); lineTo(p(-4.6f, -12.4f)); lineTo(p(-6f, -9.6f)); cubicTo(p(-3f, -9f), p(1f, -5f), p(-2f, 0f)); close() }
        volumen(l, r, saco, p(-15f, 0f).x, p(0f, 0f).x, Color(0xFFD8C8A4))
        drawLine(l.tono(MADERA_OSC), p(-10f, -9.8f), p(-5.6f, -9.8f), 0.8f * k)
        val az = Azar(Semilla.de("andalus", "grana"))
        repeat(26) { drawCircle(l.tono(if (it % 3 == 0) Color(0xFF7A1422) else CARMESI), az.entre(0.5f, 0.9f) * k, p(az.entre(-2f, 9f), az.entre(-2.4f, 0.8f))) }
        translateEscala(p(9f, 0f), 0.5f) { madeja(l, r, p(9f, 0f), k, CARMESI) }
    }

    private inline fun DrawScope.translateEscala(centro: Offset, k: Float, bloque: DrawScope.() -> Unit) = scale(k, k, centro) { bloque() }

    /** Un candil de barro, de piquera: la cazoleta, el pico largo donde va la mecha y el asa. */
    fun DrawScope.candilDeBarro(l: Luces, sobre: Offset, k: Float) {
        fun p(dx: Float, dy: Float) = Offset(sobre.x + dx * k, sobre.y + dy * k)
        drawOval(Color.Black.copy(alpha = 0.2f), p(-5f, -0.8f), Size(13f * k, 1.8f * k))
        val forma = Path().apply {
            moveTo(p(-3.4f, 0f)); cubicTo(p(-6f, -1f), p(-5.4f, -4.4f), p(-2f, -4.8f)); lineTo(p(-1.6f, -6.2f)); lineTo(p(1.6f, -6.2f)); lineTo(p(2f, -4.6f))
            cubicTo(p(4f, -4f), p(6f, -3.6f), p(8f, -3.4f)); lineTo(p(8f, -2.2f)); cubicTo(p(6f, -1.6f), p(4.4f, -0.6f), p(3.4f, 0f)); close()
        }
        drawPath(Path().apply { moveTo(p(-4.6f, -3.4f)); cubicTo(p(-8.4f, -4.4f), p(-8.4f, -0.6f), p(-4.4f, -1f)) }, l.tono(Color(0xFF8A5A3A)), style = Stroke(0.9f * k, cap = StrokeCap.Round))
        drawPath(forma, Brush.verticalGradient(listOf(l.tono(lerp(BARRO, Color(0xFFFFE0B8), 0.35f)), l.tono(lerp(BARRO, Color(0xFF6A3A22), 0.4f))), p(0f, -6f).y, p(0f, 0f).y))
        drawLine(l.tono(Color(0xFF3A2A22)), p(6.6f, -3.3f), p(8f, -3.2f), 0.7f * k)
        drawLine(l.tono(VIDRIADO).copy(alpha = 0.8f), p(-3.6f, -2.6f), p(2.4f, -2.8f), 0.6f * k)
    }

    /** La alcuza de cobre, vieja y abollada: la panza, el cuello, el pico fino y el asa. */
    private fun DrawScope.alcuza(l: Luces, r: Rayo, sobre: Offset, k: Float) {
        fun p(dx: Float, dy: Float) = Offset(sobre.x + dx * k, sobre.y + dy * k)
        drawOval(Color.Black.copy(alpha = 0.2f), p(-7f, -1.4f), Size(15f * k, 3f * k))
        val cobre = Color(0xFFB86A3C)
        val forma = Path().apply { moveTo(p(-3.6f, 0f)); cubicTo(p(-8f, -2f), p(-7.4f, -9f), p(-2.4f, -10.4f)); lineTo(p(-1.8f, -14.6f)); lineTo(p(-3f, -16f)); lineTo(p(3f, -16f)); lineTo(p(1.8f, -14.6f)); lineTo(p(2.4f, -10.4f)); cubicTo(p(7.4f, -9f), p(8f, -2f), p(3.6f, 0f)); close() }
        drawPath(Path().apply { moveTo(p(-2f, -14.4f)); cubicTo(p(-9f, -15f), p(-10f, -7f), p(-6.4f, -5.6f)) }, l.tono(lerp(cobre, Color(0xFF4A2A1A), 0.3f)), style = Stroke(1.2f * k, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(p(5f, -6f)); cubicTo(p(9f, -8f), p(10f, -12f), p(12.6f, -15f)) }, l.tono(cobre), style = Stroke(1.1f * k, cap = StrokeCap.Round))
        volumen(l, r, forma, p(-7.4f, 0f).x, p(7.4f, 0f).x, cobre)
        clipPath(forma) {
            mancha(p(-2f, -5f), 2.4f * k, 1.6f * k, l.tono(Color(0xFF5A2E1C)), 0.5f); mancha(p(3f, -7.4f), 1.6f * k, 1.2f * k, l.tono(Color(0xFF5A2E1C)), 0.4f)       // las abolladuras
            mancha(p(-4f, -2f), 3f * k, 1.4f * k, l.tono(Color(0xFF5E9A7A)), 0.35f)                                                                                         // el cardenillo
            drawLine(l.tono(Color(0xFFFFE0B0)).copy(alpha = 0.7f), p(r.lat * 3f, -9f), p(r.lat * 3.6f, -3f), 0.7f * k)
        }
        drawLine(l.tono(Color(0xFFD89060)), p(-3f, -16f), p(3f, -16f), 0.8f * k)
    }

    /** El frasco de agua de rosas: una redoma de vidrio verdoso, de cuello largo, con su tapón; dentro, el agua, apenas rosada. */
    private fun DrawScope.redoma(l: Luces, r: Rayo, sobre: Offset, k: Float) {
        fun p(dx: Float, dy: Float) = Offset(sobre.x + dx * k, sobre.y + dy * k)
        drawOval(Color.Black.copy(alpha = 0.18f), p(-7f, -1.2f), Size(14f * k, 2.6f * k))
        val forma = Path().apply { moveTo(p(-3f, 0f)); cubicTo(p(-8f, -1.6f), p(-8f, -9.6f), p(-1.6f, -10.6f)); lineTo(p(-1.3f, -17f)); lineTo(p(-2.4f, -18f)); lineTo(p(2.4f, -18f)); lineTo(p(1.3f, -17f)); lineTo(p(1.6f, -10.6f)); cubicTo(p(8f, -9.6f), p(8f, -1.6f), p(3f, 0f)); close() }
        drawPath(forma, l.tono(Color(0xFFB4D8C8)).copy(alpha = 0.6f))
        clipPath(forma) {
            drawRect(l.tono(Color(0xFFF0B8C0)).copy(alpha = 0.75f), p(-8f, -7.4f), Size(16f * k, 7.6f * k))
            drawLine(l.tono(Color.White).copy(alpha = 0.6f), p(-8f, -7.4f), p(8f, -7.4f), 0.4f * k)
            drawLine(l.tono(Color.White).copy(alpha = 0.85f), p(-r.lat * 4.4f, -8.4f), p(-r.lat * 5f, -3f), 0.8f * k)
            mancha(p(r.lat * 3f, -4f), 2f * k, 3f * k, l.tono(Color(0xFF5E8A7A)), 0.3f)
        }
        drawPath(forma, l.tono(Color(0xFF6E9A8A)).copy(alpha = 0.9f), style = Stroke(0.5f * k))
        drawRoundRect(l.tono(Color(0xFFC8A070)), p(-1.6f, -20.4f), Size(3.2f * k, 2.8f * k), CornerRadius(0.8f * k))
        // Dos rosas secas, al lado.
        for ((dx, dy) in listOf(11f to -1.6f, 14.6f to -0.4f)) { drawCircle(l.tono(Color(0xFFC0506A)), 1.9f * k, p(dx, dy)); drawCircle(l.tono(Color(0xFFE8909C)), 0.9f * k, p(dx - 0.4f, dy - 0.4f)) }
    }

    /** La alheña: un cuenco verde y manganeso con la pasta, la paleta para mezclarla y unas hojas secas. */
    private fun DrawScope.alhena(l: Luces, r: Rayo, sobre: Offset, k: Float) {
        fun p(dx: Float, dy: Float) = Offset(sobre.x + dx * k, sobre.y + dy * k)
        drawOval(Color.Black.copy(alpha = 0.2f), p(-10f, -1.4f), Size(20f * k, 3f * k))
        val cuenco = Path().apply { moveTo(p(-9f, -7f)); cubicTo(p(-8f, -1f), p(-4f, 0f), p(0f, 0f)); cubicTo(p(4f, 0f), p(8f, -1f), p(9f, -7f)); close() }
        volumen(l, r, cuenco, p(-9f, 0f).x, p(9f, 0f).x, Color(0xFFE8E0C8))
        clipPath(cuenco) { for (i in -3..3) drawPath(Path().apply { moveTo(p(i * 2.8f, -7f)); cubicTo(p(i * 2.4f, -4f), p(i * 1.4f, -1.4f), p(i * 0.6f, 0f)) }, l.tono(if (i % 2 == 0) VIDRIADO else Color(0xFF3A2A30)), style = Stroke(0.7f * k)) }
        drawOval(l.tono(Color(0xFFF2EAD4)), p(-9f, -8.6f), Size(18f * k, 3.4f * k))
        drawOval(l.tono(Color(0xFF6E7A3A)), p(-7.4f, -8.2f), Size(14.8f * k, 2.6f * k)); drawOval(l.tono(Color(0xFF8E9A4E)), p(-5f, -8f), Size(7f * k, 1.2f * k))
        drawLine(l.tono(MADERA), p(2f, -7.4f), p(12f, -12.4f), 1.1f * k, StrokeCap.Round)
        for ((dx, dy, g) in listOf(Triple(-13f, -0.8f, 0.4f), Triple(-15.4f, -2f, -0.5f), Triple(13f, -0.6f, 2.6f))) drawLine(l.tono(Color(0xFF7E8A4A)), p(dx, dy), p(dx + cos(g) * 3f, dy + sin(g) * 1.2f), 1.3f * k, StrokeCap.Round)
        // Una mano de Fátima no: unas motas rojizas, donde ya tiñó el paño.
        drawOval(l.tono(Color(0xFFB85A32)).copy(alpha = 0.7f), p(-16f, 0.2f), Size(5f * k, 1.2f * k))
    }

    /** Unos dírhams de plata sobre un pañuelo: con sus orlas de puntos y sin ninguna cara. */
    private fun DrawScope.dirhams(l: Luces, sobre: Offset, k: Float) {
        fun p(dx: Float, dy: Float) = Offset(sobre.x + dx * k, sobre.y + dy * k)
        drawPath(poligono(p(-14f, -1f), p(0f, -5.4f), p(15f, -1.6f), p(1f, 2.6f)), l.tono(ANIL))
        drawPath(poligono(p(-14f, -1f), p(0f, -5.4f), p(15f, -1.6f), p(1f, 2.6f)), l.tono(ORO).copy(alpha = 0.7f), style = Stroke(0.4f * k))
        val plata = l.tono(Color(0xFFD9DCE0)); val sombra = l.tono(Color(0xFF7E848E)); val luz = l.tono(Color.White)
        for ((dx, dy) in listOf(-6f to -1.4f, -1f to -2.6f, 4.6f to -1f, 0.6f to 0.4f, 8.6f to -2.4f)) {
            drawOval(sombra, p(dx - 2.6f, dy - 0.7f), Size(5.2f * k, 2.4f * k)); drawOval(plata, p(dx - 2.6f, dy - 1.2f), Size(5.2f * k, 2.4f * k))
            drawOval(sombra.copy(alpha = 0.8f), p(dx - 1.7f, dy - 0.9f), Size(3.4f * k, 1.6f * k), style = Stroke(0.3f * k))
            drawLine(sombra.copy(alpha = 0.8f), p(dx - 1f, dy - 0.2f), p(dx + 1f, dy - 0.2f), 0.3f * k); drawLine(sombra.copy(alpha = 0.8f), p(dx - 0.8f, dy + 0.2f), p(dx + 0.8f, dy + 0.2f), 0.3f * k)
            drawLine(luz.copy(alpha = 0.9f), p(dx - 1.8f, dy - 0.9f), p(dx - 0.4f, dy - 1.1f), 0.4f * k)
        }
        // Una, de canto, apoyada en las otras.
        drawOval(plata, p(-11f, -5.6f), Size(1.6f * k, 4.6f * k)); drawOval(sombra, p(-11f, -5.6f), Size(1.6f * k, 4.6f * k), style = Stroke(0.3f))
    }

    /** El laúd, apoyado en la mesa: la caja de media pera, hecha de costillas; la tapa con su roseta; el mástil corto y el clavijero doblado hacia atrás. */
    private fun laud(l: Luces, r: Rayo): Cosa {
        val pie = Offset(146f, 277f)
        val caja = Path().apply { moveTo(pie.x - 2f, pie.y); cubicTo(pie.x - 16f, pie.y - 2f, pie.x - 15f, pie.y - 22f, pie.x - 3f, pie.y - 31f); lineTo(pie.x + 2.4f, pie.y - 30f); cubicTo(pie.x + 13f, pie.y - 18f, pie.x + 13f, pie.y - 3f, pie.x + 3f, pie.y); close() }
        val mastil = palo(pie + Offset(-0.6f, -30f), pie + Offset(6.4f, -50f), 4.2f)
        val forma = Path().apply { addPath(caja); addPath(mastil) }
        return Cosa(forma, pie) {
            val madera = Color(0xFFC08A4E)
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(pie.x - 14f, pie.y - 2f), Size(28f, 4.6f))
            // El clavijero, doblado hacia atrás, con sus clavijas.
            val cl0 = pie + Offset(6.4f, -50f); val cl1 = pie + Offset(-2.6f, -57f)
            drawPath(palo(cl0, cl1, 3f), l.tono(MADERA_OSC))
            for (i in 0..4) { val q = cl0 + (cl1 - cl0) * (0.15f + i * 0.2f); drawLine(l.tono(Color(0xFFE8DCC0)), q + Offset(-2.4f, 2f), q + Offset(2.4f, -2f), 0.8f, StrokeCap.Round) }
            drawPath(mastil, Brush.horizontalGradient(if (r.lat >= 0f) listOf(l.tono(MADERA_OSC), l.tono(MADERA)) else listOf(l.tono(MADERA), l.tono(MADERA_OSC)), pie.x - 2f, pie.x + 8f))
            volumen(l, r, caja, pie.x - 15f, pie.x + 13f, madera)
            clipPath(caja) {
                // La tapa, clara, con la roseta calada y el puente; las cuerdas de seda.
                val tapa = Path().apply { moveTo(pie.x - 1f, pie.y - 1.6f); cubicTo(pie.x - 12f, pie.y - 4f, pie.x - 11f, pie.y - 21f, pie.x - 1.6f, pie.y - 29f); lineTo(pie.x + 1.6f, pie.y - 28.4f); cubicTo(pie.x + 9.6f, pie.y - 18f, pie.x + 9.6f, pie.y - 4f, pie.x + 1.6f, pie.y - 1.6f); close() }
                drawPath(tapa, l.tono(Color(0xFFE6CC96)))
                drawCircle(l.tono(Color(0xFF4A3220)), 3.2f, pie + Offset(-0.6f, -17f)); drawCircle(l.tono(Color(0xFFE6CC96)), 3.2f, pie + Offset(-0.6f, -17f), style = Stroke(0.5f))
                for (i in 0 until 6) { val a = i * 1.047f; drawLine(l.tono(Color(0xFFE6CC96)), pie + Offset(-0.6f, -17f), pie + Offset(-0.6f + cos(a) * 3f, -17f + sin(a) * 3f), 0.4f) }
                drawLine(l.tono(MADERA_OSC), pie + Offset(-5f, -6.4f), pie + Offset(4f, -6f), 1.3f)
                for (i in 0..4) drawLine(l.tono(Color(0xFFF6F0DC)).copy(alpha = 0.85f), pie + Offset(-3.6f + i * 1.6f, -6.2f), pie + Offset(-1.2f + i * 0.9f, -29.4f), 0.3f)
            }
            for (i in 0..4) drawLine(l.tono(Color(0xFFF6F0DC)).copy(alpha = 0.85f), pie + Offset(-1.2f + i * 0.9f, -29.4f), pie + Offset(4.4f + i * 0.6f, -49.6f), 0.3f)
            drawPath(caja, l.tono(Color(0xFF6A4424)).copy(alpha = 0.7f), style = Stroke(0.5f))
        }
    }

    /** La jarra de barro poroso, colgada de una rama del naranjo, a la sombra: suda, y por eso enfría. */
    private fun jarraColgada(l: Luces, r: Rayo): Cosa {
        val c = JARRA
        val forma = Path().apply { moveTo(c.x - 3f, c.y + 13f); cubicTo(c.x - 9.6f, c.y + 10f, c.x - 9f, c.y - 1f, c.x - 3.4f, c.y - 4f); lineTo(c.x - 2.6f, c.y - 9f); lineTo(c.x - 4f, c.y - 10.6f); lineTo(c.x + 4f, c.y - 10.6f); lineTo(c.x + 2.6f, c.y - 9f); lineTo(c.x + 3.4f, c.y - 4f); cubicTo(c.x + 9f, c.y - 1f, c.x + 9.6f, c.y + 10f, c.x + 3f, c.y + 13f); close() }
        return Cosa(Path(), Offset(c.x, 286f), bajoElFollaje = false) {
            // La cuerda de esparto, que sube a la rama, y la red que la abraza.
            val cuerda = l.tono(Color(0xFFB89A5E))
            drawLine(cuerda, c + Offset(0f, -10f), c + Offset(1f, -40f), 0.8f)
            for (s in listOf(-1f, 1f)) drawPath(Path().apply { moveTo(c.x, c.y - 16f); cubicTo(c.x + s * 4f, c.y - 13f, c.x + s * 9f, c.y - 6f, c.x + s * 9f, c.y + 2f) }, cuerda, style = Stroke(0.6f))
            volumen(l, r, forma, c.x - 9.6f, c.x + 9.6f, Color(0xFFDDBB94))
            clipPath(forma) {
                // El barro, más oscuro donde rezuma, y las gotas.
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF8E6A4A)).copy(alpha = 0.55f)), c.y + 1f, c.y + 13f), Offset(c.x - 10f, c.y + 1f), Size(20f, 12f))
                for (y in listOf(-1f, 1.4f)) drawLine(l.tono(Color(0xFF9A7250)).copy(alpha = 0.7f), Offset(c.x - 10f, c.y + y), Offset(c.x + 10f, c.y + y), 0.5f)
                val az = Azar(Semilla.de("andalus", "jarra"))
                repeat(9) { drawCircle(l.tono(Color.White).copy(alpha = 0.7f), 0.5f, Offset(az.entre(c.x - 7f, c.x + 7f), az.entre(c.y + 2f, c.y + 12f))) }
            }
            for (s in listOf(-1f, 1f)) drawPath(Path().apply { moveTo(c.x + s * 3f, c.y - 8.6f); cubicTo(c.x + s * 8f, c.y - 9f, c.x + s * 8.6f, c.y - 3f, c.x + s * 6f, c.y - 1.6f) }, l.tono(Color(0xFFC49A72)), style = Stroke(1.2f, cap = StrokeCap.Round))
            drawLine(l.tono(Color(0xFFEED4B0)), Offset(c.x - 4f, c.y - 10.6f), Offset(c.x + 4f, c.y - 10.6f), 0.9f)
            drawLine(cuerda, Offset(c.x - 9.4f, c.y + 2f), Offset(c.x + 9.4f, c.y + 2f), 0.6f)
        }
    }
}
