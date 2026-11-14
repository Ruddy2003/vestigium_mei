package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaSeda.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.seMueve
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.sin

/**
 * El campamento de la caravana, a la puerta de un caravasar: es el primer plano del cuadro del camino y es el mismo en
 * todo el viaje («todos los caravasares son iguales, y por eso uno se siente en casa en todos»). Lo que cambia con la
 * ruta es lo de detrás ([PaisajesSeda]).
 *
 * - **A la izquierda:** el muro de adobe del caravasar, con sus almenas, su puerta grande y la lámpara del nicho.
 * - **Arriba:** el faldón del toldo de fieltro de la caravana, con sus picos y sus borlas: es el marco.
 * - **Delante:** la alfombra con el cojín y el objeto del día; los fardos atados y sellados; el palo del toldo, que
 *   cruza el cuadro de abajo arriba, con el odre, la campana y el farol; la lumbre con su trébede; y los camellos de
 *   dos jorobas: uno grande, cerca, y otro detrás. A las horas de camino están de pie y cargados; en los altos,
 *   echados, con la carga en el suelo.
 *
 * **[Aproximación]**: el camello de dos jorobas, el toldo de fieltro, los fardos sellados y el caravasar de adobe son
 * los que nombra el cuento; cómo eran exactamente en el 750 no se comprobó contra ninguna fuente en esta sesión.
 */
internal object CampamentoSeda {
    private val C = ColorSeda
    private const val H = PaisajesSeda.H
    private const val CAMPO = PaisajesSeda.CAMPO
    const val PISO_GATO = 274f

    /** Donde arde la lumbre, donde cuelga el farol del palo y donde está la lámpara del nicho. */
    val LUMBRE = Offset(238f, 256f)
    val FAROL = Offset(208f, 100f)
    val NICHO = Offset(84f, 130f)
    private const val PALO = 196f
    private val OBJETO = Offset(104f, 248f)
    private val FARDOS = Offset(146f, 228f)

    private fun cae(d: Offset, alcance: Float) = 1f / (1f + (d.x * d.x + d.y * d.y) / (alcance * alcance))

    /** De noche, cuánto alumbra lo que está en [en] la lumbre, el farol o la lámpara del nicho: de 0 a 1. */
    fun calor(m: Momento, en: Offset): Float {
        val noche = m.luces().noche; if (noche < 0.05f) return 0f
        val fuego = EscenaSeda.lumbre(m) * cae(en - LUMBRE, 96f)
        val farol = if (EscenaSeda.farol(m)) maxOf(0.5f * cae(en - Offset(FAROL.x, 230f), 70f), 0.5f * cae(en - Offset(NICHO.x, 200f), 60f)) else 0f
        return (noche * maxOf(fuego, farol)).coerceIn(0f, 1f)
    }

    fun seMueve(m: Momento): Boolean = EscenaSeda.lumbre(m) > 0f || EscenaSeda.farol(m) || seMueve(m.ficha.clima) || EscenaSeda.fiesta(m) || EscenaSeda.enMarcha(m)

    fun DrawScope.cuadro(m: Momento) {
        val l = EscenaSeda.luces(m); val r = Rayo(l); val s = SolSeda(l, r); val semilla = m.semilla("cielo")
        cielo(l, H, semilla); astros(l, H, semilla)
        // El cielo del desierto, de noche: muchas más estrellas y la banda de la Vía Láctea.
        if (l.estrellas > 0.3f && l.nublado < 0.6f) {
            val az = Azar(Semilla.de("seda", "estrellas")); val k = l.estrellas * (1f - l.nublado)
            for (j in 0..9) mancha(Offset(40f + j * 34f, 14f + j * 8f + az.entre(-4f, 4f)), 30f, 9f, Color(0xFFC8D0FF), 0.05f * k)
            drawPoints((0 until 150).map { Offset(az.entre(0f, ANCHO), az.entre(0f, H - 6f)) }, PointMode.Points, Color.White.copy(alpha = 0.5f * k), 0.7f, StrokeCap.Round)
        }
        nubes(l, H, m.ficha.clima, semilla)
        with(PaisajesSeda) { lejania(m, s) }
        caravanaLejana(m, s)
        suelo(m, s)
        caravasar(m, s)
        val todas = cosas(m, s).sortedBy { it.pie.y }
        // Las sombras en el suelo: la del muro, la del toldo y la de cada cosa.
        capaSombra(r) { c ->
            sombraArrojada(r, Path().apply { addRect(Rect(-2f, 66f, 108f, CAMPO)) }, Offset(52f, CAMPO), c, CAMPO - 2f, CAMPO - 2f)
            clipRect(0f, CAMPO, ANCHO, ALTO) { sombra(sombraDelToldo(r), c) }
            for (k in todas) sombraArrojada(r, k.forma, k.pie, c, CAMPO - 2f, CAMPO - 2f)
        }
        // De noche, el charco de luz de la lumbre y el del farol en el suelo.
        if (l.noche > 0.1f) {
            val f = EscenaSeda.lumbre(m)
            if (f > 0f) { resplandor(LUMBRE + Offset(0f, 2f), 92f, 0.75f * f * l.noche, C.LUMBRE); mancha(LUMBRE + Offset(0f, 6f), 70f, 16f, C.LUMBRE, 0.22f * f * l.noche) }
            if (EscenaSeda.farol(m)) { mancha(Offset(FAROL.x, 262f), 46f, 10f, C.LUMBRE, 0.16f * l.noche); resplandor(NICHO, 30f, 0.5f * l.noche, C.LUMBRE) }
        }
        for (k in todas) with(k) { pintar() }
        toldo(m, s)
        adornos(m, s)
        if (EscenaSeda.nevado(m)) nievePosada(s)
        with(PaisajesSeda) { velo(l, m.ficha.clima, H, semilla) }
        grano(Azar(Semilla.de("seda", "grano", "camino")), 0.6f + 0.4f * r.dia)
    }

    /** La sombra que el toldo echa en el suelo: una mancha grande que se corre con el sol. */
    private fun sombraDelToldo(r: Rayo): Path { val dx = (r.ax * 16f).coerceIn(-40f, 40f); return poligono(Offset(-10f + dx, 234f), Offset(178f + dx, 230f), Offset(214f + dx, ALTO + 4f), Offset(-10f + dx, ALTO + 4f)) }

    // ---------------------------------------------------------------- El camello

    /**
     * Un camello de dos jorobas que pisa en [pie] y mira a la izquierda; [k] es su tamaño (con 1 mide unas 100 unidades
     * de largo). [echado]: con las patas dobladas debajo y el cuello en alto. [cargado]: con la albarda, dos fardos por
     * lado y el fieltro enrollado encima.
     */
    fun DrawScope.camello(s: SolSeda, pie: Offset, k: Float, echado: Boolean, cargado: Boolean, pelo: Color, oscuro: Color, lejos: Float = 0f, espejo: Boolean = false) {
        if (espejo) scale(-1f, 1f, pie) { pintaCamello(s, pie, k, echado, cargado, pelo, oscuro, lejos, true) } else pintaCamello(s, pie, k, echado, cargado, pelo, oscuro, lejos, false)
    }

    private fun DrawScope.pintaCamello(s: SolSeda, pie: Offset, k: Float, echado: Boolean, cargado: Boolean, pelo: Color, oscuro: Color, lejos: Float, espejo: Boolean) {
        val derecha = s.solALaDerecha != espejo                                  // de qué lado le da el sol, visto en su dibujo
        fun p(x: Float, y: Float) = pie + Offset(x * k, y * k)
        val b = if (echado) 30f else 0f                                         // cuánto baja el cuerpo al echarse
        val luz = s.luz(pelo, lejos); val medio = s.medio(pelo, lejos); val sombra = s.sombra(lerp(pelo, oscuro, 0.5f), lejos); val osc = s.medio(oscuro, lejos)
        fun vestir(forma: Path, y0: Float, y1: Float) {
            drawPath(forma, medio)
            clipPath(forma) {
                drawRect(Brush.verticalGradient(0f to luz, 0.42f to luz.copy(alpha = 0f), 0.6f to sombra.copy(alpha = 0f), 1f to sombra, startY = pie.y + y0 * k, endY = pie.y + y1 * k), p(-70f, y0), Size(120f * k, (y1 - y0) * k))
                val x0 = pie.x - 68f * k; val x1 = pie.x + 42f * k
                drawRect(Brush.horizontalGradient(if (derecha) listOf(sombra.copy(alpha = 0.5f), Color.Transparent) else listOf(Color.Transparent, sombra.copy(alpha = 0.5f)), x0, x1), Offset(x0, pie.y + y0 * k), Size(x1 - x0, (y1 - y0) * k))
            }
        }
        if (!echado) {
            // Las patas: primero las del otro lado, más oscuras.
            for ((lejana, x) in listOf(true to -18f, true to 24f, false to -25f, false to 30f)) {
                val c = if (lejana) sombra else medio; val atras = x > 0f; val rod = if (atras) 4.4f else -0.6f; val g = if (atras) 5.2f else 3.9f
                // El muslo (o el brazo), que se afina hacia la rodilla; la caña; y la almohadilla ancha del pie.
                drawPath(poligono(listOf(p(x - g, -46f), p(x + g, -46f), p(x + rod + 2.3f, -21f), p(x + rod - 2.3f, -21f))), c)
                drawPath(poligono(listOf(p(x + rod - 2.1f, -22f), p(x + rod + 2.1f, -22f), p(x + 2.4f, -2f), p(x - 1.4f, -2f))), c)
                drawCircle(if (lejana) sombra else osc.copy(alpha = 0.7f), 2.7f * k, p(x + rod, -21.4f))
                if (!lejana) drawLine(luz.copy(alpha = 0.6f), p(x + (if (derecha) g - 0.8f else -g + 0.8f), -45f), p(x + rod + (if (derecha) 1.8f else -1.8f), -22f), 0.8f * k)
                drawOval(if (lejana) sombra else osc, p(x - 3.2f, -3f), Size(8.4f * k, 3.4f * k))
            }
        } else {
            // Echado: la sombra de contacto bajo la panza, y las patas del otro lado, dobladas, que asoman por delante y por detrás.
            drawOval(s.sombra(lerp(C.ARENA, C.MADERA_OSC, 0.5f), lejos).copy(alpha = 0.5f), p(-54f, -4f), Size(108f * k, 9f * k))
            drawLine(sombra, p(-30f, -7f), p(-51f, -2.8f), 6f * k, StrokeCap.Round); drawLine(sombra, p(28f, -3f), p(52f, -2.8f), 5f * k, StrokeCap.Round)
        }
        // La cola, con su mechón.
        drawLine(medio, p(37f, -56f + b), p(41f, -36f + b * 0.8f), 1.8f * k, StrokeCap.Round); drawLine(osc, p(41f, -36f + b * 0.8f), p(41.6f, -28f + b * 0.8f), 2.8f * k, StrokeCap.Round)
        // El cuerpo, con sus dos jorobas, que no son iguales: la de delante cae un poco hacia un lado.
        val cuerpo = if (!echado) Path().apply {
            moveTo(p(-31f, -46f)); cubicTo(p(-33f, -58f), p(-27f, -67f), p(-19f, -67f)); cubicTo(p(-19f, -84f), p(-7f, -93f), p(-2f, -70f)); cubicTo(p(0f, -66f), p(4f, -66f), p(6f, -70f))
            cubicTo(p(9f, -91f), p(22f, -86f), p(24f, -68f)); cubicTo(p(33f, -66f), p(39f, -58f), p(37f, -46f)); cubicTo(p(31f, -34f), p(-22f, -32f), p(-31f, -46f)); close()
        } else Path().apply {
            moveTo(p(-34f, 0f)); cubicTo(p(-38f, -12f), p(-36f, -30f), p(-24f, -37f)); lineTo(p(-19f, -37f)); cubicTo(p(-20f, -58f), p(-6f, -72f), p(-2f, -40f)); cubicTo(p(0f, -36f), p(4f, -36f), p(6f, -40f))
            cubicTo(p(8f, -68f), p(22f, -64f), p(24f, -38f)); cubicTo(p(36f, -36f), p(44f, -24f), p(42f, -8f)); lineTo(p(40f, 0f)); close()
        }
        vestir(cuerpo, if (echado) -64f else -92f, if (echado) 0f else -32f)
        clipPath(cuerpo) {
            // El pelo: mechones cortos, más oscuros abajo y más claros en el lomo.
            val az = Azar(Semilla.de("seda", "pelo", (pie.x * 7f + pie.y).toInt()))
            repeat(((if (echado) 96 else 46) * k).toInt().coerceAtLeast(8)) { val q = p(az.entre(-32f, 40f), az.entre(-58f, -2f) - (if (echado) 0f else 32f)); drawLine((if (it % 3 == 0) luz else sombra).copy(alpha = if (echado) 0.46f else 0.3f), q, q + Offset(az.entre(-1f, 1f) * k, az.entre(2f, 4.4f) * k), 0.7f * k, StrokeCap.Round) }
        }
        if (echado) {
            clipPath(cuerpo) {
                // La panza, en sombra; y el anca y la paletilla, dos bultos redondos con su luz arriba.
                drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to s.sombra(oscuro, lejos).copy(alpha = 0.75f), startY = pie.y - 17f * k, endY = pie.y), p(-40f, -17f), Size(90f * k, 17f * k))
                for ((q, rx, ry) in listOf(Triple(Offset(27f, -19f), 17f, 16f), Triple(Offset(-25f, -21f), 12f, 15f)))
                    drawOval(Brush.radialGradient(listOf(luz.copy(alpha = 0.9f), luz.copy(alpha = 0f)), p(q.x + (if (derecha) 4f else -4f), q.y - 6f), rx * 1.15f * k), p(q.x - rx, q.y - ry), Size(2f * rx * k, 2f * ry * k))
                // El pelo de invierno, que le cuelga del pecho y de la paletilla en greñas oscuras.
                for (n in 0..7) { val x = -35f + n * 2.6f; drawLine(osc.copy(alpha = 0.6f), p(x, -30f + n * 0.6f), p(x - 1.2f, -17f + (if (n % 2 == 0) 3f else 0f)), 1.7f * k, StrokeCap.Round) }
            }
            // El borde del anca (la babilla hacia delante) y el del codo.
            drawPath(Path().apply { moveTo(p(27f, -35f)); cubicTo(p(13f, -31f), p(7f, -15f), p(13f, -4f)) }, sombra.copy(alpha = 0.8f), style = Stroke(1.5f * k, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(p(-16f, -34f)); cubicTo(p(-11f, -25f), p(-13f, -12f), p(-21f, -6f)) }, sombra.copy(alpha = 0.6f), style = Stroke(1.2f * k, cap = StrokeCap.Round))
            // La mano de este lado: el brazo hasta la rodilla, que apoya en el suelo con su callo, y la caña doblada debajo, con el pie.
            vestir(poligono(listOf(p(-24f, -17f), p(-43f, -10f), p(-49f, -3f), p(-45f, 0f), p(-22f, -4f))), -17f, 0f)
            for (n in 0..4) drawLine(osc.copy(alpha = 0.55f), p(-27f - n * 3.6f, -15f + n * 1.4f), p(-27.6f - n * 3.6f, -9f + n * 1.2f), 1.3f * k, StrokeCap.Round)
            drawLine(sombra, p(-43f, -1.6f), p(-25f, -1.6f), 3.4f * k, StrokeCap.Round); drawOval(osc, p(-28f, -4.2f), Size(8.6f * k, 4.2f * k))
            drawOval(s.medio(lerp(oscuro, C.HOLLIN, 0.3f), lejos), p(-50f, -5f), Size(6.4f * k, 4.6f * k))
            // La pata de atrás: la caña tendida en el suelo, del corvejón al pie.
            vestir(poligono(listOf(p(17f, -6.4f), p(42f, -6f), p(47f, -2f), p(45f, 0f), p(16f, 0f))), -6.4f, 0f)
            drawCircle(medio, 3.3f * k, p(43f, -4f)); drawOval(osc, p(11f, -4.4f), Size(9f * k, 4.4f * k)); drawLine(luz.copy(alpha = 0.6f), p(20f, -5.6f), p(40f, -5.4f), 0.7f * k)
        }
        // El pelo largo de lo alto de las jorobas: un mechón corto, de borde desflecado.
        for ((cx, cy) in listOf(-11f to -87f, 15f to -86f)) {
            val g = if (echado) 1.5f else 1f; val y = cy + b - (if (echado) 5f else 0f)
            val mechon = Path().apply { moveTo(p(cx - 5f * g, y + 6f * g)); cubicTo(p(cx - 4f * g, y + 1f * g), p(cx - 2f * g, y - 1.4f * g), p(cx, y - 1f * g)); cubicTo(p(cx + 2f * g, y - 1.4f * g), p(cx + 4f * g, y + 1f * g), p(cx + 5f * g, y + 6f * g))
                for (n in 4 downTo -4) lineTo(p(cx + n * 1.1f * g, y + (if (n % 2 == 0) 6f else 8.4f) * g)); close() }
            drawPath(mechon, osc.copy(alpha = if (echado) 0.82f else 0.6f))
            if (echado) for (n in -2..2) drawLine((if ((n > 0) == derecha) luz else sombra).copy(alpha = 0.6f), p(cx + n * 2.2f, y + 0.8f + kotlin.math.abs(n) * 1.3f), p(cx + n * 2.7f, y + 8f), 0.6f * k, StrokeCap.Round)
        }
        if (!echado) for (x in listOf(-29f, -26f, -23f, -20f)) drawLine(osc.copy(alpha = 0.7f), p(x, -46f), p(x - 0.8f, -33f), 1.5f * k, StrokeCap.Round)
        // El cuello, grueso y lanudo, y la cabeza, de morro largo.
        val cuello = if (!echado) Path().apply {
            moveTo(p(-21f, -66f)); cubicTo(p(-32f, -66f), p(-43f, -74f), p(-46f, -88f)); cubicTo(p(-47f, -94f), p(-48f, -98f), p(-50f, -100f)); lineTo(p(-58f, -102f))
            cubicTo(p(-66f, -101f), p(-72f, -96f), p(-75f, -91f)); lineTo(p(-75f, -87f)); cubicTo(p(-70f, -85f), p(-65f, -86f), p(-60f, -87f)); cubicTo(p(-59f, -72f), p(-52f, -53f), p(-34f, -43f)); lineTo(p(-27f, -47f)); close()
        } else Path().apply {
            // El cuello en S: sale bajo del pecho, se adelanta en la papada y sube derecho a la cabeza.
            moveTo(p(-19f, -38f)); cubicTo(p(-28f, -35f), p(-37f, -38f), p(-40f, -51f)); cubicTo(p(-42f, -61f), p(-42f, -71f), p(-45f, -78f)); lineTo(p(-53f, -80f))
            cubicTo(p(-61f, -79f), p(-67f, -74f), p(-70f, -69f)); lineTo(p(-70f, -65f)); cubicTo(p(-65f, -63f), p(-60f, -64f), p(-55f, -65f)); cubicTo(p(-56f, -52f), p(-61f, -36f), p(-53f, -22f)); cubicTo(p(-49f, -14f), p(-43f, -9f), p(-35f, -7f)); close()
        }
        vestir(cuello, if (echado) -80f else -102f, if (echado) -9f else -43f)
        // La melena que le cuelga de la garganta, el copete, la oreja, el ojo con su párpado, el ollar y la boca.
        val garganta = if (echado) listOf(Offset(-56f, -59f), Offset(-57f, -51f), Offset(-57.6f, -43f), Offset(-56.6f, -35f), Offset(-54f, -27f), Offset(-49f, -19f)) else listOf(Offset(-60f, -83f), Offset(-59f, -75f), Offset(-57f, -67f), Offset(-53f, -60f), Offset(-48f, -53f), Offset(-42f, -47f))
        // (una sola masa lanuda, de borde ondulado: no un peine)
        drawPath(Path().apply {
            moveTo(p(garganta.first().x + 2f, garganta.first().y - 3f)); for (q in garganta) lineTo(p(q.x + 3f, q.y)); lineTo(p(garganta.last().x + 2f, garganta.last().y + 3f))
            for ((n, q) in garganta.reversed().withIndex()) lineTo(p(q.x - 3.4f - (if (n % 2 == 0) 1.6f else 0f), q.y + 5f + (if (n % 2 == 0) 1.4f else -0.6f))); close()
        }, osc.copy(alpha = 0.88f))
        val cab = if (echado) Offset(-51f, -78f) else Offset(-56f, -100f)
        for (d in -1..1) drawLine(osc, p(cab.x + 4f + d * 1.4f, cab.y - 0.6f), p(cab.x + 4.6f + d * 2.2f, cab.y - 4f), 1.2f * k, StrokeCap.Round)
        drawPath(poligono(p(cab.x + 7f, cab.y + 1f), p(cab.x + 11.4f, cab.y - 3.4f), p(cab.x + 11f, cab.y + 3f)), medio); drawPath(poligono(p(cab.x + 8.4f, cab.y + 0.8f), p(cab.x + 10.6f, cab.y - 1.6f), p(cab.x + 10.4f, cab.y + 2f)), osc)
        drawOval(s.medio(C.HOLLIN, lejos), p(cab.x - 6.6f, cab.y + 3.6f), Size(3f * k, 2.4f * k)); drawLine(osc, p(cab.x - 7.4f, cab.y + 3.2f), p(cab.x - 3f, cab.y + 2.6f), 0.8f * k, StrokeCap.Round); drawCircle(Color.White.copy(alpha = 0.6f), 0.4f * k, p(cab.x - 5.6f, cab.y + 4.2f))
        if (echado) { drawOval(luz.copy(alpha = 0.55f), p(cab.x - 18.4f, cab.y + 6.4f), Size(9f * k, 5f * k)); drawLine(sombra.copy(alpha = 0.7f), p(cab.x - 9f, cab.y + 9f), p(cab.x - 1f, cab.y + 10.4f), 1f * k, StrokeCap.Round) }   // el morro claro y la quijada
        drawOval(osc, p(cab.x - 17.4f, cab.y + 8.6f), Size(1.8f * k, 1.1f * k)); drawLine(osc, p(cab.x - 18f, cab.y + 12.6f), p(cab.x - 10f, cab.y + 13.4f), 0.7f * k, StrokeCap.Round)
        // El ronzal: el cabezal de cuerda roja y la rienda, que cuelga.
        drawLine(s.medio(C.FIELTRO, lejos), p(cab.x - 13f, cab.y + 6f), p(cab.x - 11f, cab.y + 14.6f), 1f * k); drawLine(s.medio(C.FIELTRO, lejos), p(cab.x - 12f, cab.y + 10f), p(cab.x + 2f, cab.y + 12f), 0.8f * k)
        drawPath(Path().apply { moveTo(p(cab.x - 11f, cab.y + 14.6f)); cubicTo(p(cab.x - 12f, cab.y + 26f), p(cab.x - 2f, cab.y + 30f), p(cab.x + 6f, cab.y + 24f)) }, s.medio(C.FIELTRO, lejos), style = Stroke(0.7f * k))
        if (!cargado) {
            // Descargado le queda la mantilla de fieltro: el faldón sobre el costado, con su ribete, sus botones y su fleco, y la cincha.
            val yb = b - 68f
            val faldon = poligono(listOf(p(-15f, yb + 1f), p(21f, yb + 1f), p(24f, yb + 17f), p(-17f, yb + 16f)))
            drawPath(faldon, Brush.verticalGradient(0f to s.luz(C.FIELTRO, lejos), 1f to s.sombra(C.FIELTRO, lejos), startY = pie.y + (yb + 1f) * k, endY = pie.y + (yb + 17f) * k))
            drawPath(poligono(listOf(p(-16.6f, yb + 13f), p(23.6f, yb + 14f), p(24f, yb + 17f), p(-17f, yb + 16f))), s.medio(C.ANIL, lejos))
            for (x in -4..5) drawLine(s.luz(C.BRONCE, lejos), p(x * 4f, yb + 16.6f), p(x * 4f, yb + 20f), 0.6f * k)
            for (q in listOf(Offset(-6f, yb + 7f), Offset(4f, yb + 7.4f), Offset(14f, yb + 7.8f))) drawCircle(s.luz(C.BRONCE, lejos), 1.5f * k, p(q.x, q.y))
            drawLine(s.sombra(C.MADERA_OSC, lejos), p(8f, yb + 17f), p(9f, if (echado) -1f else -34f), 1.1f * k)
        }
        // De noche, la lumbre le da por un lado: el pecho, el cuello y la cabeza se encienden (o la grupa, si mira al otro lado).
        if (s.calida > 0.08f) for (forma in listOf(cuerpo, cuello)) clipPath(forma) {
            val a = C.LUMBRE.copy(alpha = (0.55f * s.calida).coerceIn(0f, 1f))
            drawRect(Brush.horizontalGradient(if (espejo) listOf(Color.Transparent, a) else listOf(a, Color.Transparent), pie.x + (if (espejo) 0f else -74f) * k, pie.x + (if (espejo) 46f else -4f) * k), p(-76f, -104f), Size(124f * k, 106f * k), blendMode = BlendMode.Plus)
        }
        if (cargado) {
            // La albarda: la manta de fieltro bajo las jorobas (que asoman por encima), dos fardos a este lado, y entre las jorobas, un fieltro enrollado.
            val y = b - 66f
            drawPath(poligono(listOf(p(-26f, y), p(31f, y + 1f), p(32f, y + 9f), p(-27f, y + 8f))), s.medio(C.FIELTRO, lejos)); drawLine(s.luz(C.BRONCE, lejos), p(-26.6f, y + 7f), p(31.8f, y + 8f), 0.7f * k)
            for ((x, w, c) in listOf(Triple(-25f, 25f, C.ADOBE), Triple(3f, 26f, C.ANIL))) {
                val fardo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(p(x, y + 5f), Size(w * k, 25f * k)), androidx.compose.ui.geometry.CornerRadius(3f * k))) }
                drawPath(fardo, Brush.verticalGradient(0f to s.luz(c, lejos), 1f to s.sombra(c, lejos), startY = pie.y + (y + 5f) * k, endY = pie.y + (y + 30f) * k))
                for (d in listOf(0.3f, 0.7f)) drawLine(s.sombra(C.MADERA_OSC, lejos), p(x + w * d, y + 5f), p(x + w * d, y + 30f), 0.8f * k); drawLine(s.sombra(C.MADERA_OSC, lejos), p(x, y + 17f), p(x + w, y + 17f), 0.8f * k)
                drawLine(s.sombra(C.MADERA_OSC, lejos), p(x + w * 0.5f, y + 5f), p(x + w * 0.5f, y - 2f), 0.7f * k)
            }
            val rollo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(p(-3f, y - 9f), Size(11f * k, 10f * k)), androidx.compose.ui.geometry.CornerRadius(5f * k))) }
            drawPath(rollo, Brush.verticalGradient(0f to s.luz(lerp(C.CAL, C.ROCA, 0.3f), lejos), 1f to s.sombra(lerp(C.CAL, C.ROCA, 0.3f), lejos), startY = pie.y + (y - 9f) * k, endY = pie.y + (y + 1f) * k)); drawLine(s.medio(C.FIELTRO, lejos), p(2.4f, y - 9f), p(2.4f, y + 1f), 0.9f * k)
            drawOval(s.sombra(C.MADERA_OSC, lejos), p(27f, y + 8f), Size(8f * k, 14f * k)); drawOval(s.medio(lerp(C.MADERA_OSC, C.PELO, 0.4f), lejos), p(27.6f, y + 8f), Size(5.4f * k, 10f * k))
        }
    }

    /** La silueta de un camello, para su sombra. */
    private fun siluetaDeCamello(pie: Offset, k: Float, echado: Boolean, espejo: Boolean = false): Path = Path().apply {
        if (echado && espejo) { addOval(Rect(pie.x - 44f * k, pie.y - 50f * k, pie.x + 40f * k, pie.y)); addOval(Rect(pie.x + 36f * k, pie.y - 80f * k, pie.x + 70f * k, pie.y - 16f * k)) }
        else if (echado) { addOval(Rect(pie.x - 40f * k, pie.y - 50f * k, pie.x + 44f * k, pie.y)); addOval(Rect(pie.x - 70f * k, pie.y - 80f * k, pie.x - 36f * k, pie.y - 16f * k)) }
        else { addOval(Rect(pie.x - 32f * k, pie.y - 88f * k, pie.x + 36f * k, pie.y - 34f * k)); addRect(Rect(pie.x - 28f * k, pie.y - 44f * k, pie.x - 18f * k, pie.y)); addRect(Rect(pie.x + 24f * k, pie.y - 44f * k, pie.x + 34f * k, pie.y)); addOval(Rect(pie.x - 66f * k, pie.y - 94f * k, pie.x - 30f * k, pie.y - 48f * k)) }
    }

    private fun pelo(blanco: Boolean, pardo: Boolean = false): Pair<Color, Color> = if (blanco) lerp(C.CAL, C.PELO, 0.16f) to lerp(C.PELO, C.CAL, 0.35f) else if (pardo) lerp(C.PELO, C.MADERA_OSC, 0.34f) to lerp(C.PELO_OSC, C.HOLLIN, 0.3f) else C.PELO to C.PELO_OSC

    /** La fila de la caravana, que pasa por la pista a las horas de camino; de noche, la hoguera de otra caravana, lejos. */
    private fun DrawScope.caravanaLejana(m: Momento, s: SolSeda) {
        if (EscenaSeda.enMarcha(m)) for ((j, q) in listOf(Offset(183f, H + 8f), Offset(190f, H + 13f), Offset(198f, H + 20f), Offset(203f, H + 29f), Offset(200f, H + 41f), Offset(188f, H + 56f)).withIndex()) {
            val k = 0.05f + j * 0.042f; val (a, b) = pelo(false)
            drawOval(s.sombra(C.MADERA_OSC, 0.4f).copy(alpha = 0.3f), Offset(q.x - 40f * k, q.y - 2f * k), Size(80f * k, 5f * k)); camello(s, q, k, false, true, a, b, 0.62f - j * 0.09f)
        }
        val l = s.l
        // De noche, las lumbres de otras caravanas, lejos: cada una con su tienda y sus camellos echados, a contraluz.
        if (l.noche > 0.4f && EscenaSeda.lugar(m) == Lugar.CAMINO) for ((q, g) in listOf(Offset(214f, H + 21f) to 1f, Offset(322f, H + 27f) to 1.5f, Offset(150f, H + 12f) to 0.7f)) {
            mancha(q + Offset(0f, 1.6f * g), 10f * g, 1.8f * g, C.LUMBRE, 0.3f * l.noche); resplandor(q, 8f * g, 0.5f * l.noche, C.LUMBRE); drawCircle(C.LUMBRE.copy(alpha = 0.95f * l.noche), 1.1f * g, q)
            drawPath(poligono(q + Offset(3f * g, 1f * g), q + Offset(6.4f * g, -3.4f * g), q + Offset(10f * g, 1f * g)), l.tono(C.HOLLIN, 0.3f)); for (d in listOf(-9f, -5.4f)) drawOval(l.tono(C.HOLLIN, 0.3f), q + Offset(d * g, -1.4f * g), Size(3.4f * g, 2.2f * g))
        }
    }

    // ---------------------------------------------------------------- El suelo y el caravasar

    /** El suelo pisado del campamento: tierra apelmazada, huellas de camello, cagajones, piedras y unas matas. */
    private fun DrawScope.suelo(m: Momento, s: SolSeda) {
        val l = s.l; val az = Azar(Semilla.de("seda", "suelo")); val p = EscenaSeda.pesos(EscenaSeda.ruta(m)); val nev = EscenaSeda.nevado(m)
        val base = lerp(PaisajesSeda.suelo(m, p), if (nev) C.NIEVE else C.ADOBE, if (nev) 0.2f else 0.4f)
        drawRect(Brush.verticalGradient(0f to Color.Transparent, 0.2f to l.tono(base, 0.05f), 1f to l.tono(lerp(base, C.ARENA, 0.25f)), startY = CAMPO - 12f, endY = ALTO), Offset(0f, CAMPO - 12f), Size(ANCHO, ALTO - CAMPO + 12f))
        clipRect(0f, CAMPO, ANCHO, ALTO) {
            repeat(40) { val y = az.entre(CAMPO, ALTO); val k = 0.5f + (y - CAMPO) / 90f; mancha(Offset(az.entre(0f, ANCHO), y), az.entre(10f, 34f) * k, az.entre(1.4f, 4f) * k, if (it % 3 == 0) s.luz(base) else s.sombra(base), 0.2f) }
            // Las huellas: dos óvalos juntos, la almohadilla hendida del camello.
            repeat(46) { val y = az.entre(CAMPO + 6f, ALTO); val k = 0.5f + (y - CAMPO) / 90f; val x = az.entre(40f, ANCHO)
                for (d in listOf(-1f, 1f)) drawOval(s.sombra(lerp(base, C.MADERA_OSC, 0.3f)).copy(alpha = 0.42f), Offset(x + d * 1.3f * k - 1.2f * k, y), Size(2.4f * k, 1.6f * k)) }
            repeat(18) { val y = az.entre(CAMPO + 4f, ALTO); val k = 0.5f + (y - CAMPO) / 90f; val x = az.entre(0f, ANCHO)
                drawOval(s.sombra(lerp(C.ROCA, C.PELO_OSC, 0.6f)), Offset(x, y), Size(4f * k, 1.9f * k)); drawOval(s.luz(lerp(C.PELO_OSC, C.ARENA, 0.5f)), Offset(x + (if (s.solALaDerecha) 1.2f else 0f) * k, y - 0.3f * k), Size(2.8f * k, 1.3f * k)) }
            // El sendero pisado de la puerta a la pista, con sus rodadas.
            for (j in 0..2) drawPath(Path().apply { moveTo(44f + j * 9f, CAMPO + 3f); cubicTo(96f, 216f + j * 5f, 150f, 208f + j * 4f, 148f + j * 16f, CAMPO + 1f) }, s.sombra(lerp(base, C.MADERA_OSC, 0.25f)).copy(alpha = 0.34f), style = Stroke(1.3f))
            if (!nev) {
                // Lo propio de cada tramo: los rizos del viento en la arena; hierba y guijarros en la montaña; polvo amarillo y grietas en el loess.
                repeat((30 * p.desierto).toInt()) { val y = az.entre(CAMPO + 6f, ALTO); val k = 0.5f + (y - CAMPO) / 80f; val x = az.entre(190f, ANCHO); val largo = az.entre(14f, 34f) * k
                    drawPath(Path().apply { moveTo(x, y); cubicTo(x + largo * 0.3f, y - 1.8f * k, x + largo * 0.7f, y + 1.8f * k, x + largo, y - 0.4f) }, s.sombra(base).copy(alpha = 0.4f), style = Stroke(0.7f * k, cap = StrokeCap.Round))
                    drawPath(Path().apply { moveTo(x, y + 1.3f * k); cubicTo(x + largo * 0.3f, y - 0.5f * k, x + largo * 0.7f, y + 3.1f * k, x + largo, y + 0.9f * k) }, s.luz(lerp(base, C.CAL, 0.35f)).copy(alpha = 0.5f), style = Stroke(0.6f * k, cap = StrokeCap.Round)) }
                // y las ondas largas, cada vez más separadas hacia quien mira.
                if (p.desierto > 0.3f) for (n in 0..15) { val u = n / 15f; val y = CAMPO + 5f + 82f * u * (0.4f + 0.6f * u); val x = 196f + az.entre(0f, 40f); val largo = az.entre(70f, 150f)
                    drawPath(Path().apply { moveTo(x, y); cubicTo(x + largo * 0.3f, y - 1.6f - 2f * u, x + largo * 0.6f, y + 1.6f + 2f * u, x + largo, y - 0.6f) }, s.sombra(base).copy(alpha = 0.3f * p.desierto), style = Stroke(0.6f + 0.9f * u, cap = StrokeCap.Round))
                    drawPath(Path().apply { moveTo(x, y + 1f + 1.4f * u); cubicTo(x + largo * 0.3f, y - 0.6f - 0.6f * u, x + largo * 0.6f, y + 2.6f + 3.4f * u, x + largo, y + 0.4f + 1.4f * u) }, s.luz(lerp(base, C.CAL, 0.35f)).copy(alpha = 0.34f * p.desierto), style = Stroke(0.5f + 0.7f * u, cap = StrokeCap.Round)) }
                repeat((34 * p.montana).toInt()) { val q = Offset(az.entre(104f, ANCHO), az.entre(CAMPO + 3f, ALTO)); val k = 0.6f + (q.y - CAMPO) / 90f
                    if (it % 3 == 0) { drawOval(s.sombra(C.ROCA), Offset(q.x, q.y), Size(6f * k, 3f * k)); drawOval(s.luz(lerp(C.ROCA, C.CAL, 0.4f)), Offset(q.x + (if (s.solALaDerecha) 1.6f else 0f) * k, q.y - 0.5f * k), Size(4.2f * k, 2f * k)) }
                    else for (j in -3..3) drawLine(if (j % 2 == 0) s.luz(C.VERDE) else s.sombra(C.VERDE), q, q + Offset(j * 1.5f * k, -(3.4f + az.real() * 4f) * k), 0.7f, StrokeCap.Round) }
                repeat((12 * p.loess).toInt()) { var q = Offset(az.entre(110f, ANCHO), az.entre(CAMPO + 8f, ALTO - 6f)); val grieta = Path().apply { moveTo(q.x, q.y); repeat(4) { q += Offset(az.entre(5f, 12f), az.entre(-3f, 3f)); lineTo(q.x, q.y) } }
                    drawPath(grieta, s.sombra(lerp(C.LOESS, C.MADERA_OSC, 0.4f)).copy(alpha = 0.55f), style = Stroke(0.8f)); mancha(q, az.entre(14f, 30f), 3f, s.luz(C.LOESS), 0.32f) }
            }
            // Unas piedras grandes, cerca, con su lado de luz; la ceniza alrededor de la lumbre; y las boñigas de los camellos.
            for ((q, w) in listOf(Offset(214f, 280f) to 9f, Offset(300f, 281f) to 7f, Offset(8f, 278f) to 8f, Offset(176f, 276f) to 5f)) { drawOval(s.sombra(lerp(C.ROCA, C.PELO, 0.65f)), q, Size(w, w * 0.56f)); drawOval(s.luz(lerp(C.PELO, C.ARENA, 0.6f)), q + Offset(if (s.solALaDerecha) w * 0.24f else 0f, -0.6f), Size(w * 0.72f, w * 0.36f)) }
            mancha(LUMBRE + Offset(0f, 5f), 30f, 8f, l.tono(lerp(C.CAL, C.ROCA, 0.55f)), 0.5f); mancha(LUMBRE + Offset(4f, 6f), 14f, 4f, l.tono(C.HOLLIN), 0.3f)
            for (c in listOf(Offset(296f, 232f), Offset(352f, 276f), Offset(226f, 226f))) repeat(5) { val q = c + Offset(az.entre(-7f, 7f), az.entre(-2.4f, 2.4f)); drawOval(s.sombra(C.PELO_OSC), Offset(q.x, q.y), Size(3.4f, 2f)); drawOval(s.luz(C.PELO_OSC).copy(alpha = 0.7f), Offset(q.x + 0.4f, q.y - 0.3f), Size(2f, 1f)) }
            if (l.noche > 0.3f) mancha(Offset(l.lunaX * ANCHO, 244f), 120f, 30f, C.LUNA, 0.08f * l.noche)      // la luna en el suelo
            if (nev) { val an = Azar(Semilla.de("seda", "nieve")); repeat(34) { val y = an.entre(CAMPO, ALTO); val k = 0.5f + (y - CAMPO) / 80f; mancha(Offset(an.entre(0f, ANCHO), y), an.entre(14f, 40f) * k, an.entre(2f, 5f) * k, s.luz(C.NIEVE), 0.75f) } }
            if (!nev) repeat(8) { val q = Offset(az.entre(110f, ANCHO), az.entre(CAMPO + 2f, 236f)); for (j in -2..2) drawLine(s.medio(lerp(C.VERDE_OSC, C.PELO, 0.5f)), q, q + Offset(j * 1.5f, -(3f + az.real() * 4f)), 0.6f, StrokeCap.Round) }
        }
    }

    /** El muro del caravasar, de adobe: las almenas, los rollizos que asoman, la puerta grande con su hoja y el nicho de la lámpara. */
    private fun DrawScope.caravasar(m: Momento, s: SolSeda) {
        val l = s.l; val az = Azar(Semilla.de("seda", "muro")); val nev = EscenaSeda.nevado(m); val noche = l.noche
        val muro = s.con(calor(m, Offset(90f, 210f)) * 0.7f)
        drawRect(Brush.verticalGradient(0f to muro.frente(lerp(C.ADOBE, C.CAL, 0.12f)), 1f to muro.frente(lerp(C.ADOBE, C.MADERA_OSC, 0.14f)), startY = 66f, endY = CAMPO + 2f), Offset(-2f, 66f), Size(98f, CAMPO - 64f))
        drawPath(poligono(Offset(96f, 66f), Offset(108f, 72f), Offset(108f, CAMPO - 3f), Offset(96f, CAMPO + 2f)), muro.lado(C.ADOBE))
        for (j in 0..6) { drawRect(muro.frente(lerp(C.ADOBE, C.CAL, 0.12f)), Offset(-2f + j * 15f, 58f), Size(9f, 8.4f)); drawRect(muro.lado(C.ADOBE), Offset(5.4f + j * 15f, 58f), Size(1.6f, 8.4f)) }
        // Las hiladas de adobe, los desconchones, las chorreras y el zócalo comido.
        clipRect(-2f, 66f, 96f, CAMPO + 2f) {
            for (j in 0..18) drawLine(muro.sombra(C.ADOBE).copy(alpha = 0.22f), Offset(-2f, 70f + j * 7f), Offset(96f, 70f + j * 7f), 0.4f)
            repeat(9) { val q = Offset(az.entre(0f, 92f), az.entre(72f, 190f)); drawPath(PaisajesSeda.masa(az, q, az.entre(4f, 9f), az.entre(3f, 7f), 8, 0.4f, 1f), muro.sombra(lerp(C.ADOBE, C.MADERA, 0.3f)).copy(alpha = 0.5f)); for (j in 0..2) drawLine(muro.luz(C.ADOBE).copy(alpha = 0.5f), q + Offset(-3f, j * 2.2f - 2f), q + Offset(3f, j * 2.2f - 2f), 0.4f) }
            repeat(7) { val x = az.entre(2f, 94f); drawRect(Brush.verticalGradient(0f to muro.sombra(C.MADERA_OSC).copy(alpha = 0.3f), 1f to Color.Transparent, startY = 66f, endY = 66f + az.entre(20f, 60f)), Offset(x, 66f), Size(az.entre(1.4f, 3.4f), 60f)) }
            drawPath(androidx.compose.ui.graphics.Path().apply { moveTo(-2f, CAMPO + 2f); var x = -2f; while (x < 98f) { lineTo(x, CAMPO - az.entre(5f, 12f)); x += az.entre(5f, 11f) }; lineTo(98f, CAMPO + 2f); close() }, muro.sombra(lerp(C.ADOBE, C.MADERA_OSC, 0.35f)).copy(alpha = 0.6f))
        }
        for (j in 0..7) { drawCircle(muro.sombra(C.MADERA_OSC), 1.5f, Offset(6f + j * 12f, 82f)); drawCircle(muro.luz(C.MADERA), 0.9f, Offset(6f + j * 12f + (if (s.solALaDerecha) 0.4f else -0.4f), 81.6f)) }
        // La sombra corta de cada rollizo, que cae hacia el lado contrario al sol; y bajo las almenas, la del pretil.
        for (j in 0..7) drawLine(muro.sombra(C.MADERA_OSC).copy(alpha = 0.55f * s.r.dia), Offset(6f + j * 12f, 83f), Offset(6f + j * 12f + (if (s.solALaDerecha) -5f else 5f), 93f), 1.3f, StrokeCap.Round)
        drawRect(Brush.verticalGradient(0f to muro.sombra(C.MADERA_OSC).copy(alpha = 0.4f), 1f to Color.Transparent, startY = 66f, endY = 77f), Offset(-2f, 66f), Size(98f, 11f))
        // El paño del muro no es parejo: se aclara hacia el lado del sol.
        drawRect(Brush.horizontalGradient(if (s.solALaDerecha) listOf(muro.sombra(C.ADOBE).copy(alpha = 0.3f * s.r.dia), Color.Transparent) else listOf(Color.Transparent, muro.sombra(C.ADOBE).copy(alpha = 0.3f * s.r.dia)), -2f, 96f), Offset(-2f, 66f), Size(98f, CAMPO - 64f))
        // El ventanuco de la cámara de arriba, con su celosía de madera.
        drawRect(muro.luz(lerp(C.ADOBE, C.CAL, 0.3f)), Offset(31f, 87f), Size(19f, 14f)); drawRect(lerp(muro.sombra(C.HOLLIN), C.LUMBRE, 0.55f * noche), Offset(33f, 89f), Size(15f, 10f))
        for (j in 1..4) drawLine(muro.medio(C.MADERA_OSC), Offset(33f + j * 3f, 89f), Offset(33f + j * 3f, 99f), 0.6f); drawLine(muro.medio(C.MADERA_OSC), Offset(33f, 94f), Offset(48f, 94f), 0.6f); drawRect(muro.sombra(C.MADERA_OSC).copy(alpha = 0.5f), Offset(31f, 101f), Size(19f, 1.4f))
        // El contrafuerte de la esquina de la izquierda, en talud, cortado por el borde.
        drawPath(poligono(Offset(-2f, 116f), Offset(5f, 116f), Offset(10f, CAMPO + 3f), Offset(-2f, CAMPO + 3f)), muro.frente(lerp(C.ADOBE, C.CAL, 0.2f))); drawPath(poligono(Offset(5f, 116f), Offset(7.4f, 118f), Offset(12.4f, CAMPO + 2f), Offset(10f, CAMPO + 3f)), muro.lado(C.ADOBE))
        drawPath(poligono(Offset(-2f, 113f), Offset(6f, 113f), Offset(8f, 116.4f), Offset(-2f, 116.4f)), muro.luz(lerp(C.ADOBE, C.CAL, 0.4f))); for (j in 1..9) drawLine(muro.sombra(C.ADOBE).copy(alpha = 0.34f), Offset(-2f, 116f + j * 8.4f), Offset(5f + j * 0.5f, 116f + j * 8.4f), 0.4f)
        // La puerta: el arco, el hueco (con lumbre dentro, de noche) y una hoja de tablones claveteados, a medio abrir.
        val arco = Path().apply { moveTo(16f, CAMPO + 1f); lineTo(16f, 138f); cubicTo(16f, 106f, 66f, 106f, 66f, 138f); lineTo(66f, CAMPO + 1f); close() }
        drawPath(Path().apply { moveTo(11f, CAMPO + 1f); lineTo(11f, 138f); cubicTo(11f, 100f, 71f, 100f, 71f, 138f); lineTo(71f, CAMPO + 1f); close() }, muro.luz(lerp(C.ADOBE, C.CAL, 0.3f)))
        // Las dovelas del arco y las hiladas de las jambas, de ladrillo.
        for (j in 0..14) { val a = 3.1416f * (1f + (j + 0.5f) / 15f); val c = Offset(41f, 138f); val q0 = c + Offset(kotlin.math.cos(a) * 25.6f, sin(a) * 24.6f); val q1 = c + Offset(kotlin.math.cos(a) * 29.6f, sin(a) * 28.2f)
            drawLine(if (j % 2 == 0) muro.luz(C.LADRILLO) else muro.sombra(C.LADRILLO), q0, q1, 4.6f) }
        for (j in 0..9) for (x in listOf(11f, 66f)) { drawRect(if ((j + x.toInt()) % 2 == 0) muro.luz(C.LADRILLO) else muro.medio(C.LADRILLO), Offset(x, 138f + j * 6f), Size(5f, 5.2f)) }
        drawPath(arco, lerp(l.tono(C.HOLLIN), C.LUMBRE, 0.3f * noche))
        if (noche > 0.2f) clipPath(arco) { resplandor(Offset(30f, 180f), 40f, 0.7f * noche, C.LUMBRE) }
        val cerrada = EscenaSeda.guerra(m) || EscenaSeda.tormentaNegra(m)
        val hoja = if (cerrada) Path().apply { addPath(arco) } else poligono(Offset(42f, 112f), Offset(66f, 122f), Offset(66f, CAMPO + 1f), Offset(42f, CAMPO + 4f))
        clipPath(arco) {
            drawPath(hoja, Brush.horizontalGradient(listOf(muro.sombra(C.MADERA), muro.frente(C.MADERA)), 42f, 66f))
            for (j in 0..7) drawLine(muro.sombra(C.MADERA_OSC).copy(alpha = 0.6f), Offset(if (cerrada) 18f + j * 6.4f else 42f + j * 3.4f, 106f), Offset(if (cerrada) 18f + j * 6.4f else 42f + j * 3.4f, CAMPO + 4f), 0.5f)
            for (y in listOf(134f, 160f, 186f)) { drawLine(muro.sombra(C.HOLLIN), Offset(if (cerrada) 16f else 42f, y), Offset(66f, y + (if (cerrada) 0f else 2f)), 1.6f); for (j in 0..(if (cerrada) 7 else 3)) drawCircle(muro.luz(C.BRONCE), 0.8f, Offset((if (cerrada) 20f else 45f) + j * 6f, y + (if (cerrada) 0f else j * 0.5f))) }
        }
        // El nicho de la lámpara.
        val nicho = Path().apply { moveTo(NICHO.x - 6f, NICHO.y + 7f); lineTo(NICHO.x - 6f, NICHO.y - 2f); cubicTo(NICHO.x - 6f, NICHO.y - 9f, NICHO.x + 6f, NICHO.y - 9f, NICHO.x + 6f, NICHO.y - 2f); lineTo(NICHO.x + 6f, NICHO.y + 7f); close() }
        drawPath(nicho, if (EscenaSeda.farol(m)) lerp(muro.sombra(C.MADERA_OSC), C.LUMBRE, 0.55f) else muro.sombra(C.MADERA_OSC))
        drawOval(muro.medio(C.BRONCE), Offset(NICHO.x - 3.4f, NICHO.y + 3f), Size(6.8f, 3f)); drawRect(muro.sombra(C.HOLLIN).copy(alpha = 0.5f), Offset(NICHO.x - 6f, NICHO.y - 14f), Size(12f, 6f))
        if (nev) { drawRect(s.luz(C.NIEVE), Offset(-2f, 64.6f), Size(98f, 2.4f)); for (j in 0..6) drawRect(s.luz(C.NIEVE), Offset(-2f + j * 15f, 56.6f), Size(9f, 2f)) }
    }

    // ---------------------------------------------------------------- Las cosas

    private fun fardo(x: Float, y: Float, w: Float, h: Float): Path = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(x, y, x + w, y + h), androidx.compose.ui.geometry.CornerRadius(3f))) }

    /** Un fardo: la tela, las cuerdas que lo atan en cruz y el sello de barro. */
    private fun DrawScope.pintaFardo(s: SolSeda, x: Float, y: Float, w: Float, h: Float, tela: Color, az: Azar) {
        val f = fardo(x, y, w, h)
        drawPath(f, Brush.verticalGradient(0f to s.luz(tela), 0.5f to s.medio(tela), 1f to s.sombra(tela), startY = y, endY = y + h))
        clipPath(f) {
            drawRect(Brush.horizontalGradient(if (s.solALaDerecha) listOf(s.sombra(tela).copy(alpha = 0.6f), Color.Transparent) else listOf(Color.Transparent, s.sombra(tela).copy(alpha = 0.6f)), x, x + w), Offset(x, y), Size(w, h))
            repeat(5) { val q = Offset(az.entre(x, x + w), az.entre(y, y + h)); drawLine(s.sombra(tela).copy(alpha = 0.4f), q, q + Offset(az.entre(3f, 8f), az.entre(-1f, 1f)), 0.4f) }
        }
        val cuerda = s.medio(lerp(C.MADERA_OSC, C.PELO, 0.3f))
        for (d in listOf(0.32f, 0.68f)) drawLine(cuerda, Offset(x + w * d, y), Offset(x + w * d, y + h), 0.9f); drawLine(cuerda, Offset(x, y + h * 0.5f), Offset(x + w, y + h * 0.5f), 0.9f)
        drawCircle(s.medio(lerp(C.ADOBE, C.FIELTRO, 0.4f)), 1.7f, Offset(x + w * 0.32f, y + h * 0.5f)); drawCircle(s.luz(C.ADOBE), 0.7f, Offset(x + w * 0.32f - 0.4f, y + h * 0.5f - 0.4f))
    }

    private fun cosas(m: Momento, s0: SolSeda): List<Cosa> = buildList {
        val l = s0.l; val r = s0.r; val marcha = EscenaSeda.enMarcha(m); val az = Azar(Semilla.de("seda", "cosas")); val ciudad = EscenaSeda.lugar(m) == Lugar.SAMARCANDA
        fun s(en: Offset) = s0.con(calor(m, en))
        // Los petates enrollados, al pie del muro; con Farn, dos. Y la alforja.
        add(Cosa(Path().apply { addRect(Rect(112f, 198f, 150f, 210f)) }, Offset(130f, 210f)) { val z = s(Offset(130f, 206f))
            for ((j, x) in (if (EscenaSeda.conFarn(m)) listOf(112f, 131f) else listOf(114f)).withIndex()) {
                val rollo = Path().apply { addOval(Rect(x, 197f + j, x + 17f, 210f + j)) }
                volumen(l, r, rollo, x, x + 17f, if (j == 0) C.FIELTRO else C.ANIL); drawOval(z.sombra(C.HOLLIN).copy(alpha = 0.3f), Offset(x + 6.4f, 201.4f + j), Size(4.4f, 4.4f)); drawLine(z.luz(C.CAL), Offset(x + 3f, 198f + j), Offset(x + 3f, 209.6f + j), 0.7f)
            }
        })
        // Las tinajas del agua, al pie del muro, y la escalera de mano por la que se sube a la azotea del caravasar.
        add(Cosa(Path().apply { addRect(Rect(72f, 172f, 108f, 204f)) }, Offset(90f, 204f)) { val z = s(Offset(90f, 196f)); val cuantas = 2 + m.azar("tinajas").entero(2)
            for ((x, h, c) in listOf(Triple(74f, 30f, lerp(C.ADOBE, C.MADERA_OSC, 0.4f)), Triple(103f, 17f, lerp(C.ADOBE, C.PELO, 0.5f)), Triple(90f, 24f, lerp(C.ADOBE, C.FIELTRO, 0.3f))).filterIndexed { n, _ -> n != 1 || cuantas == 3 }) {
                val tinaja = Path().apply { moveTo(x + 3f, 204f); cubicTo(x - 4f, 204f - h * 0.5f, x + 1f, 204f - h * 0.9f, x + 5f, 204f - h); lineTo(x + 11f, 204f - h); cubicTo(x + 15f, 204f - h * 0.9f, x + 20f, 204f - h * 0.5f, x + 13f, 204f); close() }
                volumen(l, r, tinaja, x - 2f, x + 18f, c); drawOval(z.sombra(C.HOLLIN), Offset(x + 4.6f, 203f - h), Size(6.8f, 2.4f)); drawLine(z.sombra(C.MADERA_OSC).copy(alpha = 0.5f), Offset(x, 204f - h * 0.55f), Offset(x + 16f, 204f - h * 0.55f), 0.5f)
            }
            val palo = z.medio(C.MADERA); for (dx in listOf(0f, 9f)) drawLine(palo, Offset(99f + dx, 206f), Offset(88f + dx, 62f), 1.5f, StrokeCap.Round)
            for (j in 1..10) { val y = 206f - j * 13f; val x = 99f - j * 1f; drawLine(z.sombra(C.MADERA_OSC), Offset(x, y), Offset(x + 9f, y), 1.1f) }
        })
        // El montón de leña y de estiércol seco para la lumbre, y la estaca donde se atan los camellos.
        add(Cosa(Path().apply { addOval(Rect(262f, 268f, 296f, 282f)) }, Offset(279f, 282f)) { val z = s(Offset(279f, 276f)); val an = Azar(Semilla.de("seda", "lenia"))
            repeat(5 + m.azar("lenia").entero(8)) { val q = Offset(an.entre(266f, 290f), an.entre(273f, 281f)); drawOval(z.sombra(lerp(C.PELO_OSC, C.HOLLIN, 0.3f)), Offset(q.x - 4f, q.y - 2f), Size(8f, 4.4f)); drawOval(z.luz(C.PELO_OSC), Offset(q.x - 3.4f, q.y - 2.4f), Size(5.6f, 2.6f)) }
            for ((a, b) in listOf(Offset(260f, 280f) to Offset(284f, 270f), Offset(270f, 282f) to Offset(298f, 274f), Offset(264f, 273f) to Offset(290f, 279f))) { drawLine(z.sombra(C.MADERA_OSC), a, b, 2.2f, StrokeCap.Round); drawLine(z.luz(C.MADERA), a + Offset(0f, -0.6f), b + Offset(0f, -0.6f), 0.7f, StrokeCap.Round) }
        })
        // Los fardos: en el alto, apilados (tres o cuatro, según el día); en marcha queda el que espera carga. Hacia el
        // oeste son de seda, y al lado asoman los rollos; hacia el este, lo del oeste: sacos, cuero y un tapete enrollado.
        val este = EscenaSeda.haciaElEste(m)
        val telas = if (este) listOf(C.ADOBE, lerp(C.PELO, C.MADERA, 0.5f), lerp(C.ADOBE, C.CAL, 0.4f), C.FIELTRO) else listOf(lerp(C.ADOBE, C.CAL, 0.45f), C.ANIL, lerp(C.FIELTRO, C.CAL, 0.4f), lerp(C.VERDE, C.ADOBE, 0.4f))
        val pila = if (marcha) listOf(Triple(126f, 210f, 0)) else listOf(Triple(116f, 208f, 0), Triple(148f, 206f, 1), Triple(132f, 186f, 2), Triple(120f, 166f, 3)).take(fardos(m))
        add(Cosa(Path().apply { for ((x, y, _) in pila) addRect(Rect(x, y, x + 32f, FARDOS.y)) }, FARDOS, bajoElFollaje = false) { val z = s(FARDOS)
            for ((x, y, j) in pila) pintaFardo(z, x, y, if (j == 3) 26f else 32f, if (j >= 2) 20f else FARDOS.y - y, telas[j], az)
            if (!marcha) {
                // En la repisa que deja el fardo de arriba: los rollos de seda (hacia el oeste) o el tapete enrollado (hacia el este), vistos de punta.
                if (este) { val y = 197.4f; val x = 164f
                    drawRoundRect(Brush.verticalGradient(0f to z.luz(C.FIELTRO), 1f to z.sombra(C.FIELTRO), startY = y, endY = y + 8.6f), Offset(x, y), Size(16f, 8.6f), androidx.compose.ui.geometry.CornerRadius(4f))
                    for (d in listOf(5f, 8.4f, 11.8f)) drawLine(z.medio(if (d == 8.4f) C.BRONCE else C.ANIL), Offset(x + d, y + 0.5f), Offset(x + d, y + 8.1f), 1f)
                    drawOval(z.luz(lerp(C.FIELTRO, C.CAL, 0.4f)), Offset(x - 0.8f, y + 0.4f), Size(4.4f, 7.8f)); drawOval(z.sombra(C.FIELTRO), Offset(x + 0.4f, y + 2.4f), Size(2f, 3.8f)); for (n in 0..3) drawLine(z.luz(C.CAL), Offset(x + 15.6f, y + 1.4f + n * 2f), Offset(x + 18f, y + 1.8f + n * 2f), 0.5f) }
                else for ((n, tela) in listOf(lerp(C.FIELTRO, C.CAL, 0.5f), C.BRONCE, C.ANIL).withIndex()) { val y = 200.6f - n * 5f; val x = 164.4f + n * 1.2f; val largo = 15.6f - n * 2.2f
                    drawRoundRect(Brush.verticalGradient(0f to z.luz(tela), 1f to z.sombra(tela), startY = y, endY = y + 5.4f), Offset(x, y), Size(largo, 5.4f), androidx.compose.ui.geometry.CornerRadius(2.6f))
                    drawOval(z.luz(lerp(tela, C.CAL, 0.5f)), Offset(x - 0.6f, y + 0.3f), Size(3.2f, 4.8f)); drawCircle(z.sombra(C.MADERA_OSC), 0.6f, Offset(x + 1f, y + 2.7f)); drawLine(z.sombra(tela).copy(alpha = 0.7f), Offset(x + largo * 0.62f, y + 0.4f), Offset(x + largo * 0.62f, y + 5f), 0.6f) }
                // Una cuerda enrollada, al pie.
                for (n in 0..2) drawOval(z.medio(lerp(C.PELO, C.CAL, 0.3f)), Offset(168f + n * 1.6f, 230f + n * 0.7f), Size(16f - n * 3.2f, 5.4f - n * 1.4f), style = Stroke(1f)); drawLine(z.medio(lerp(C.PELO, C.CAL, 0.3f)), Offset(183f, 233f), Offset(190f, 238f), 1f, StrokeCap.Round)
            }
            if (EscenaSeda.guerra(m)) for ((x, d) in listOf(128f to -3f, 141f to 2f)) { drawLine(z.medio(C.MADERA), Offset(x, 212f), Offset(x + d, 192f), 0.7f); drawPath(poligono(Offset(x + d - 1.6f, 192f), Offset(x + d + 1.4f, 192.6f), Offset(x + d + 0.4f, 188f)), z.medio(C.CAL)) }
        })
        // La ropa tendida, los días de buen tiempo que hay alto: de la escalera al palo.
        if (!marcha && ropaTendida(m)) add(Cosa(Path(), Offset(150f, CAMPO + 1f)) { val z = s(Offset(150f, 150f)); val dia = m.azar("ropa")
            val soga = cuerda(Offset(93f, 128f), Offset(PALO, 136f), 9f, 12); drawPath(io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva(soga), z.medio(C.MADERA_OSC), style = Stroke(0.5f))
            for ((n, c) in listOf(C.CAL, C.ANIL, lerp(C.FIELTRO, C.CAL, 0.3f), C.CAL).withIndex()) { val q = soga[2 + n * 2] + Offset(-5f, 0f); val w = dia.entre(9f, 15f); val h = dia.entre(14f, 23f); if (dia.real() < 0.25f) continue
                val prenda = Path().apply { moveTo(q.x, q.y); lineTo(q.x + w, q.y + 0.6f); cubicTo(q.x + w + 1f, q.y + h * 0.5f, q.x + w - 1.4f, q.y + h * 0.8f, q.x + w - 0.6f, q.y + h); lineTo(q.x + 1f, q.y + h - 1.4f); cubicTo(q.x - 1f, q.y + h * 0.6f, q.x + 1.2f, q.y + h * 0.3f, q.x, q.y); close() }
                drawPath(prenda, Brush.horizontalGradient(if (s0.solALaDerecha) listOf(z.sombra(c), z.medio(c), z.luz(c)) else listOf(z.luz(c), z.medio(c), z.sombra(c)), q.x, q.x + w)); drawLine(z.sombra(c).copy(alpha = 0.6f), Offset(q.x + w * 0.5f, q.y + 1f), Offset(q.x + w * 0.45f, q.y + h - 1f), 0.5f) }
        })
        // El camello de detrás: el blanco, desde que lo compra; antes, uno pardo. En los altos está echado mirando al otro
        // lado, atado a su estaca; en marcha va detrás del grande. El día que se le muere uno, falta.
        if (!EscenaSeda.camelloMuerto(m)) { val pie = if (marcha) Offset(270f, 220f) else Offset(304f, 213f); val k = if (marcha) 0.64f else 0.6f; val (a, b) = pelo(EscenaSeda.camelloBlanco(m), pardo = true)
            add(Cosa(siluetaDeCamello(pie, k, !marcha, espejo = !marcha), pie, bajoElFollaje = false) { camello(s(pie), pie, k, !marcha, marcha, a, b, 0.08f, espejo = !marcha)
                if (!marcha) drawPath(Path().apply { moveTo(pie.x + 62f * k, pie.y - 63.4f * k); cubicTo(pie.x + 60f * k, pie.y - 30f * k, 352f, 226f, 357f, 217f) }, s(pie).medio(C.FIELTRO), style = Stroke(0.7f)) }) }
        add(Cosa(Path().apply { addRect(Rect(355f, 215f, 359f, 226f)) }, Offset(357f, 226f)) { val z = s(Offset(357f, 222f)); drawLine(z.medio(C.MADERA_OSC), Offset(357f, 226f), Offset(357.6f, 215f), 2.2f, StrokeCap.Round); drawLine(z.luz(C.MADERA), Offset(356.6f, 225f), Offset(357.2f, 216f), 0.6f) })
        // La alfombra, con su cojín y el objeto del día; en los altos, la tetera y los cuencos.
        add(Cosa(Path().apply { addOval(Rect(40f, 232f, 76f, 246f)) }, Offset(92f, 262f), bajoElFollaje = false) { val z = s(OBJETO)
            val alfombra = poligono(Offset(36f, 240f), Offset(152f, 236f), Offset(168f, 262f), Offset(20f, 267f))
            drawPath(alfombra, Brush.verticalGradient(0f to z.medio(C.FIELTRO), 1f to z.luz(C.FIELTRO), startY = 236f, endY = 267f))
            clipPath(alfombra) {
                drawPath(poligono(Offset(40f, 242.6f), Offset(149f, 239f), Offset(161f, 259.4f), Offset(27f, 264f)), z.medio(C.ANIL), style = Stroke(2.2f))
                // Los medallones de perlas, como en las telas de los sogdianos: aros con puntos alrededor.
                for ((j, q) in listOf(Offset(56f, 255f), Offset(96f, 253f), Offset(136f, 250f)).withIndex()) { drawOval(z.medio(C.BRONCE), Offset(q.x - 11f, q.y - 5f), Size(22f, 10f), style = Stroke(1.2f)); for (t in 0 until 10) { val a = t * 0.6283f; drawCircle(z.luz(C.CAL), 0.7f, q + Offset(kotlin.math.cos(a) * 11f, sin(a) * 5f)) }; drawOval(z.medio(if (j == 1) C.ANIL else C.VERDE_OSC), Offset(q.x - 5f, q.y - 2.4f), Size(10f, 4.8f)) }
                repeat(14) { val q = Offset(az.entre(24f, 164f), az.entre(238f, 266f)); drawLine(z.sombra(C.FIELTRO).copy(alpha = 0.5f), q, q + Offset(az.entre(4f, 10f), 0.4f), 0.5f) }
            }
            for (j in 0..14) drawLine(z.luz(C.CAL), Offset(21f + j * 10.5f, 267f - j * 0.36f), Offset(20.4f + j * 10.5f, 270f - j * 0.36f), 0.6f)
            val cojin = Path().apply { moveTo(40f, 246f); cubicTo(40f, 232f, 76f, 230f, 76f, 244f); cubicTo(74f, 249f, 44f, 250f, 40f, 246f); close() }
            volumen(l, r, cojin, 40f, 76f, C.ANIL); drawLine(z.luz(C.BRONCE), Offset(46f, 240f), Offset(70f, 238f), 0.8f); drawCircle(z.medio(C.BRONCE), 1.4f, Offset(40.6f, 245.4f)); drawCircle(z.medio(C.BRONCE), 1.4f, Offset(75.6f, 243.6f))
            if (!marcha && (m.hacer == Hacer.COMER || m.hacer == Hacer.GENTE)) {
                val tetera = Path().apply { moveTo(136f, 252f); cubicTo(132f, 244f, 150f, 244f, 146f, 252f); close() }
                volumen(l, r, tetera, 134f, 148f, C.BRONCE); drawLine(z.medio(C.BRONCE), Offset(146.6f, 248f), Offset(151f, 245f), 1f); drawLine(z.medio(C.BRONCE), Offset(141f, 245.6f), Offset(141f, 243.4f), 1.2f)
                for (x in listOf(124f, 154f)) { drawOval(z.sombra(C.CAL), Offset(x, 252.4f), Size(6f, 3f)); drawOval(z.luz(C.CAL), Offset(x + 0.6f, 252f), Size(4.8f, 1.9f)) }
            }
            // Lo que hay ese día en la alfombra, al lado del cojín: el gorro de fieltro, las botas, o nada.
            when (m.azar("alfombra").entero(3)) {
                0 -> { val fieltro = lerp(C.PELO, C.CAL, 0.3f); val gorro = Path().apply { moveTo(24f, 263f); cubicTo(26f, 255f, 29f, 250f, 33f, 247f); cubicTo(35f, 251f, 38f, 256f, 40f, 262.6f); close() }; volumen(l, r, gorro, 24f, 40f, fieltro); drawLine(z.medio(C.PELO_OSC), Offset(23.4f, 262.4f), Offset(40.6f, 262f), 2.6f, StrokeCap.Round); drawLine(z.medio(C.FIELTRO), Offset(27f, 256f), Offset(37.4f, 255.6f), 0.8f) }
                1 -> for (x in listOf(22f, 31f)) { val bota = Path().apply { moveTo(x, 264f); lineTo(x + 1f, 250f); lineTo(x + 5f, 250f); lineTo(x + 5.4f, 260f); lineTo(x + 10f, 262f); lineTo(x + 10f, 264f); close() }; drawPath(bota, z.medio(C.PELO_OSC)); drawLine(z.luz(C.PELO), Offset(x + 1.6f, 251f), Offset(x + 1.2f, 262f), 0.7f); drawLine(z.medio(C.FIELTRO), Offset(x + 0.8f, 251f), Offset(x + 5.2f, 251f), 1.2f) }
                else -> Unit
            }
            with(ObjetosSeda) { objeto(m.ficha.objeto, OBJETO, 1.25f, z) }
            // La sombra del toldo también cae en la alfombra y en lo que tiene encima (sin capa: multiplica lo pintado).
            if (r.fuerza > 0.02f) clipPath(Path().apply { addPath(alfombra); addPath(cojin) }) { drawPath(sombraDelToldo(r), r.color.copy(alpha = (r.fuerza * 0.85f).coerceIn(0f, 1f)), blendMode = BlendMode.Multiply) }
        })
        // El palo del toldo, que cruza el cuadro: con el odre del agua, la campana de la fila y el farol.
        add(Cosa(Path().apply { addRect(Rect(PALO - 2f, 12f, PALO + 2f, 268f)); addOval(Rect(PALO - 15f, 150f, PALO - 2f, 184f)) }, Offset(PALO, 268f), bajoElFollaje = false) { val z = s(Offset(PALO, 230f))
            val palo = Path().apply { addRect(Rect(PALO - 2f, 8f, PALO + 2.2f, 268f)) }
            volumen(l, r, palo, PALO - 2f, PALO + 2.2f, C.MADERA); for (y in listOf(60f, 118f, 190f)) drawLine(z.sombra(C.MADERA_OSC), Offset(PALO - 2.2f, y), Offset(PALO + 2.4f, y + 1f), 1.2f)
            for ((q, w) in listOf(Offset(PALO - 5f, 266f) to 7f, Offset(PALO + 1f, 267f) to 8f, Offset(PALO - 1f, 262f) to 6f)) { drawOval(z.sombra(lerp(C.ROCA, C.PELO_OSC, 0.6f)), q, Size(w, w * 0.6f)); drawOval(z.luz(lerp(C.PELO_OSC, C.ARENA, 0.45f)), q + Offset(if (s0.solALaDerecha) 1.4f else 0f, -0.4f), Size(w * 0.7f, w * 0.4f)) }
            // El odre: un pellejo de cabra hinchado, colgado de una clavija.
            drawLine(z.medio(C.MADERA_OSC), Offset(PALO - 2f, 150f), Offset(PALO - 8f, 152f), 0.9f)
            val odre = Path().apply { moveTo(PALO - 8f, 152f); cubicTo(PALO - 17f, 156f, PALO - 18f, 180f, PALO - 10f, 185f); cubicTo(PALO - 3f, 184f, PALO - 1.6f, 160f, PALO - 8f, 152f); close() }
            volumen(l, r, odre, PALO - 17f, PALO - 2f, lerp(C.PELO, C.MADERA, 0.35f)); drawLine(z.medio(C.MADERA_OSC), Offset(PALO - 11f, 184.6f), Offset(PALO - 13f, 189f), 1.2f, StrokeCap.Round); drawLine(z.medio(C.MADERA_OSC), Offset(PALO - 8f, 184f), Offset(PALO - 6.4f, 188f), 1.2f, StrokeCap.Round); drawLine(z.sombra(C.HOLLIN).copy(alpha = 0.5f), Offset(PALO - 12f, 158f), Offset(PALO - 11f, 180f), 0.5f)
            // La campana de camello, de bronce.
            drawLine(z.medio(C.MADERA_OSC), Offset(PALO + 2f, 196f), Offset(PALO + 7f, 199f), 0.8f)
            val campana = Path().apply { moveTo(PALO + 3.6f, 211f); cubicTo(PALO + 3.6f, 198f, PALO + 11f, 198f, PALO + 11f, 211f); close() }
            volumen(l, r, campana, PALO + 3.6f, PALO + 11f, C.BRONCE); drawCircle(z.sombra(C.BRONCE), 1.2f, Offset(PALO + 7.3f, 212.4f))
            // El farol: un brazo de madera y, colgando, la caja de bronce con sus cuatro caras de asta.
            drawLine(z.medio(C.MADERA_OSC), Offset(PALO + 1f, 84f), Offset(FAROL.x + 1f, 80f), 1.4f); drawLine(z.medio(C.HOLLIN), Offset(FAROL.x, 80f), Offset(FAROL.x, FAROL.y - 9f), 0.5f)
            drawPath(poligono(Offset(FAROL.x - 6f, FAROL.y - 9f), Offset(FAROL.x + 6f, FAROL.y - 9f), Offset(FAROL.x, FAROL.y - 14f)), z.medio(C.BRONCE))
            drawRect(if (EscenaSeda.farol(m)) lerp(C.LUMBRE, Color.White, 0.3f) else z.medio(lerp(C.CAL, C.BRONCE, 0.3f)), Offset(FAROL.x - 5f, FAROL.y - 9f), Size(10f, 13f)); drawRect(z.medio(C.BRONCE), Offset(FAROL.x - 5f, FAROL.y - 9f), Size(10f, 13f), style = Stroke(0.9f)); drawLine(z.medio(C.BRONCE), Offset(FAROL.x, FAROL.y - 9f), Offset(FAROL.x, FAROL.y + 4f), 0.6f)
            drawRect(z.sombra(C.BRONCE), Offset(FAROL.x - 6f, FAROL.y + 4f), Size(12f, 1.6f))
            if (EscenaSeda.luto(m)) drawPath(poligono(Offset(PALO - 2f, 120f), Offset(PALO - 2f, 128f), Offset(PALO - 20f, 146f), Offset(PALO - 16f, 134f)), z.medio(C.CAL))
        })
        // La lumbre: el corro de piedras, la leña y la trébede con su caldero.
        add(Cosa(Path().apply { addOval(Rect(LUMBRE.x - 15f, LUMBRE.y - 4f, LUMBRE.x + 15f, LUMBRE.y + 6f)); addRect(Rect(LUMBRE.x - 5f, LUMBRE.y - 34f, LUMBRE.x + 5f, LUMBRE.y)) }, LUMBRE + Offset(0f, 6f), bajoElFollaje = false) { val z = s(LUMBRE + Offset(20f, 4f)); val f = EscenaSeda.lumbre(m)
            drawOval(z.sombra(lerp(C.HOLLIN, C.ROCA, 0.4f)), Offset(LUMBRE.x - 13f, LUMBRE.y - 2.6f), Size(26f, 8f))
            for (j in 0..8) { val a = j * 0.698f; val q = LUMBRE + Offset(kotlin.math.cos(a) * 14f, 1.4f + sin(a) * 4.4f); drawOval(z.sombra(lerp(C.ROCA, C.PELO_OSC, 0.6f)), Offset(q.x - 3.2f, q.y - 1.8f), Size(6.4f, 3.8f)); drawOval(z.luz(lerp(C.PELO_OSC, C.ARENA, 0.45f)), Offset(q.x - 2.6f + (if (s0.solALaDerecha) 0.8f else -0.4f), q.y - 2f), Size(4.4f, 2.4f)) }
            for ((a, b) in listOf(Offset(-8f, 2f) to Offset(6f, -1.4f), Offset(-5f, -1.6f) to Offset(8f, 2.4f), Offset(-1f, 3f) to Offset(3f, -2.4f))) drawLine(if (f > 0f) lerp(l.tono(C.HOLLIN), Color(0xFFFF7A2A), 0.5f * f) else z.medio(lerp(C.HOLLIN, C.CAL, 0.35f)), LUMBRE + a, LUMBRE + b, 2.2f, StrokeCap.Round)
            // La trébede: tres palos atados arriba y el caldero colgado de su cadena.
            val cima = LUMBRE + Offset(0f, -36f)
            for (dx in listOf(-14f, 13f, 2f)) drawLine(z.medio(C.MADERA_OSC), cima, LUMBRE + Offset(dx, if (dx == 2f) -1f else 4f), 1.3f, StrokeCap.Round)
            drawLine(z.medio(C.HOLLIN), cima, cima + Offset(0f, 15f), 0.6f)
            val caldero = Path().apply { moveTo(LUMBRE.x - 7.4f, LUMBRE.y - 20f); cubicTo(LUMBRE.x - 8.4f, LUMBRE.y - 8f, LUMBRE.x + 8.4f, LUMBRE.y - 8f, LUMBRE.x + 7.4f, LUMBRE.y - 20f); close() }
            volumen(l, r, caldero, LUMBRE.x - 8f, LUMBRE.x + 8f, lerp(C.HOLLIN, C.BRONCE, 0.25f)); drawOval(z.medio(lerp(C.HOLLIN, C.BRONCE, 0.45f)), Offset(LUMBRE.x - 7.6f, LUMBRE.y - 21.6f), Size(15.2f, 3.4f))
            if (f > 0f && l.noche > 0.2f) drawPath(caldero, C.LUMBRE.copy(alpha = 0.3f * f * l.noche))
        })
        // El camello grande, cerca: echado en los altos; de pie y cargado a las horas de camino.
        run { val pie = Offset(318f, 264f); val (a, b) = pelo(false)
            add(Cosa(siluetaDeCamello(pie, 1f, !marcha), pie, bajoElFollaje = false) { camello(s(pie + Offset(-30f, -20f)), pie, 1f, !marcha, marcha, a, b) }) }
        // Su carga, en el suelo, cuando está echado: la albarda de madera y un fardo, cortados por el borde.
        if (!marcha) add(Cosa(Path().apply { addRect(Rect(352f, 246f, 384f, 282f)) }, Offset(368f, 282f), bajoElFollaje = false) { val z = s(Offset(360f, 270f))
            pintaFardo(z, 356f, 250f, 30f, 32f, if (ciudad) lerp(C.ADOBE, C.CAL, 0.4f) else C.ADOBE, az)
            drawLine(z.medio(C.MADERA), Offset(344f, 282f), Offset(356f, 256f), 2.2f, StrokeCap.Round); drawLine(z.medio(C.MADERA), Offset(366f, 284f), Offset(356f, 256f), 2.2f, StrokeCap.Round); drawLine(z.sombra(C.MADERA_OSC), Offset(348f, 274f), Offset(363f, 276f), 1.6f)
        })
    }

    // ---------------------------------------------------------------- El toldo y los adornos

    /** El faldón del toldo de fieltro, que cuelga por arriba: picos con su ribete añil, su cordón de bronce y sus borlas. */
    private fun DrawScope.toldo(m: Momento, s: SolSeda) {
        val az = Azar(Semilla.de("seda", "toldo")); val nev = EscenaSeda.nevado(m)
        val z = s.con(calor(m, Offset(140f, 200f)) * 0.5f)
        val picos = (0..10).map { Offset(-6f + it * 24f, 36f - it * 2.1f) }
        val faldon = Path().apply {
            moveTo(-4f, -4f); lineTo(246f, -4f); lineTo(picos.last().x, picos.last().y - 9f)
            for (i in picos.size - 1 downTo 1) { val a = picos[i]; val b = picos[i - 1]; cubicTo(a.x - 4f, a.y + 3f, b.x + 16f, b.y + 9f, b.x + 12f, b.y + 5f); cubicTo(b.x + 8f, b.y + 1f, b.x + 4f, b.y - 5f, b.x, b.y - 9f) }
            close()
        }
        drawPath(faldon, Brush.verticalGradient(0f to z.sombra(C.FIELTRO), 1f to z.frente(C.FIELTRO), startY = 0f, endY = 44f))
        clipPath(faldon) {
            repeat(60) { val q = Offset(az.entre(0f, 240f), az.entre(0f, 44f)); drawLine((if (it % 2 == 0) z.sombra(C.FIELTRO) else z.luz(C.FIELTRO)).copy(alpha = 0.3f), q, q + Offset(az.entre(-3f, 3f), az.entre(2f, 5f)), 0.6f) }
            // El ribete añil y el cordón de bronce, que siguen los picos.
            for ((dy, c, g) in listOf(Triple(-4.4f, z.frente(C.ANIL), 4.4f), Triple(-8f, z.luz(C.BRONCE), 0.9f), Triple(-1f, z.luz(C.BRONCE), 0.7f))) drawPath(Path().apply { addPath(faldon, Offset(0f, dy)) }, c, style = Stroke(g))
        }
        // Las borlas, en la punta de cada pico; y la costura por donde el palo sujeta el toldo.
        for (i in 0 until picos.size - 1) { val q = Offset(picos[i].x + 12.6f, picos[i].y + 5.4f); drawLine(z.medio(C.BRONCE), q, q + Offset(0f, 3f), 0.7f); drawOval(z.frente(C.ANIL), Offset(q.x - 1.5f, q.y + 3f), Size(3f, 4.6f)); drawLine(z.luz(C.BRONCE), q + Offset(-1.2f, 4.6f), q + Offset(1.2f, 4.6f), 0.6f) }
        if (nev) drawRect(s.luz(C.NIEVE).copy(alpha = 0.9f), Offset(0f, 0f), Size(244f, 2.6f))
    }

    /** Dónde cuelgan las linternas de la fiesta: del muro al palo. */
    internal fun linternas(): List<Offset> = cuerda(Offset(100f, 72f), Offset(PALO, 46f), 16f, 12).let { c -> c.filterIndexed { i, _ -> i in 1 until c.size - 1 && i % 2 == 1 } }

    private fun DrawScope.adornos(m: Momento, s: SolSeda) {
        val l = s.l
        if (EscenaSeda.fiesta(m)) banderines(l, Offset(100f, 70f), Offset(PALO, 44f), 18f, listOf(C.FIELTRO, C.BRONCE, C.ANIL, C.CAL), 9f)
        if (EscenaSeda.linternas(m) || EscenaSeda.fiesta(m)) {
            drawPath(io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva(cuerda(Offset(100f, 72f), Offset(PALO, 46f), 16f, 12)), l.tono(C.MADERA_OSC), style = Stroke(0.5f))
            for ((j, q) in linternas().withIndex()) {
                // Una linterna de seda encerada: el cuerpo redondo, sus dos tapas y su borla.
                val c = if (j % 2 == 0) C.FIELTRO else lerp(C.BRONCE, C.FIELTRO, 0.3f)
                drawLine(l.tono(C.HOLLIN), q, q + Offset(0f, 3f), 0.5f)
                drawOval(lerp(s.medio(c), C.LUMBRE, 0.7f * l.noche), Offset(q.x - 4.4f, q.y + 3f), Size(8.8f, 10f)); for (d in listOf(-2.2f, 0f, 2.2f)) drawLine(s.sombra(c).copy(alpha = 0.5f), q + Offset(d, 3.4f), q + Offset(d * 1.1f, 12.6f), 0.4f)
                drawRect(s.medio(C.HOLLIN), Offset(q.x - 2.4f, q.y + 2.4f), Size(4.8f, 1.2f)); drawRect(s.medio(C.HOLLIN), Offset(q.x - 2.4f, q.y + 12.4f), Size(4.8f, 1.2f)); drawLine(s.medio(C.BRONCE), q + Offset(0f, 13.6f), q + Offset(0f, 17.6f), 0.7f)
                if (l.noche > 0.2f) resplandor(q + Offset(0f, 8f), 13f, 0.5f * l.noche, C.LUMBRE)
            }
        }
        if (EscenaSeda.guerra(m)) {
            // Los guardias, alerta: dos lanzas y un escudo redondo apoyados junto a la puerta.
            for ((x, y) in listOf(102f to 116f, 110f to 124f)) { drawLine(s.medio(C.MADERA), Offset(x + 6f, CAMPO + 6f), Offset(x, y), 1.1f); drawPath(poligono(Offset(x - 1.8f, y + 1f), Offset(x + 1.6f, y), Offset(x - 1.2f, y - 8f)), s.luz(lerp(C.TEJA, C.CAL, 0.3f))) }
            drawOval(s.sombra(C.MADERA_OSC), Offset(98f, 176f), Size(20f, 24f)); drawOval(s.frente(C.FIELTRO), Offset(99f, 177f), Size(17f, 21f)); drawCircle(s.luz(C.BRONCE), 2.4f, Offset(107.4f, 187.4f))
        }
    }

    /** La nieve posada encima de los fardos (la del suelo, a manchones, va con el suelo, debajo de las cosas). */
    private fun DrawScope.nievePosada(s: SolSeda) {
        val az = Azar(Semilla.de("seda", "nieve"))
        for ((x, y, w) in listOf(Triple(120f, 165f, 26f), Triple(148f, 205f, 32f))) drawOval(s.luz(C.NIEVE), Offset(x, y), Size(w, 3.4f))
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val l = EscenaSeda.luces(m); val f = EscenaSeda.lumbre(m)
        if (f > 0f) {
            humo(l, LUMBRE + Offset(0f, -30f), 70f, 10f, reloj, m.viento, 0.25f, 0f, 3, 8, 0.6f * f)
            for ((dx, alto) in listOf(-5f to 9f, 0f to 14f, 5f to 8f)) llama(LUMBRE + Offset(dx, 1f), alto * (0.5f + 0.5f * f), reloj, dx.toInt() + 6)
        } else if (EscenaSeda.enMarcha(m) && m.hora < 8.0) humo(l, LUMBRE + Offset(0f, -2f), 40f, 5f, reloj, m.viento, 0.1f, 0f, 2, 5, 0.35f)
        if (EscenaSeda.farol(m)) {
            llama(FAROL + Offset(0f, 2f), 5f, reloj, 9); resplandor(FAROL, 20f, 0.5f * l.noche, C.LUMBRE)
            llama(NICHO + Offset(0f, 3.4f), 4.4f, reloj, 4)
        }
        if (l.noche > 0.2f && (EscenaSeda.linternas(m) || EscenaSeda.fiesta(m))) for ((j, q) in linternas().withIndex()) drawCircle(Color.White.copy(alpha = (0.16f + 0.14f * sin(reloj * 1.9f + j * 1.3f)).coerceIn(0f, 1f)), 2f, q + Offset(0f, 8f))
        tiempoVivo(l, m.ficha.clima, reloj, H, m.semilla("tiempo"))
    }

    // ---------------------------------------------------------------- El gato

    /** Hay ropa tendida: con buen tiempo, algo menos de la mitad de los días que hay alto. */
    internal fun ropaTendida(m: Momento): Boolean = (m.ficha.clima == Clima.DESPEJADO || m.ficha.clima == Clima.NUBES) && !EscenaSeda.nevado(m) && !EscenaSeda.tormentaNegra(m) && !EscenaSeda.fiesta(m) && !EscenaSeda.linternas(m) && m.azar("ropa", "hay").real() < 0.45f

    /** Cuántos fardos hay apilados en el alto: tres o cuatro, según el día (tres, si hay ropa tendida encima). */
    internal fun fardos(m: Momento): Int = if (ropaTendida(m)) 3 else 3 + m.azar("fardos").entero(2)

    /** Lo plantado en el tramo por donde camina el gato: el palo del toldo, la lumbre (también apagada) y el montón de leña. */
    fun estorbos(m: Momento): List<Float> = listOf(PALO, LUMBRE.x, 279f)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 24f, PISO_GATO, hasta = 330f))
        if (EscenaSeda.enMarcha(m)) add(Ancla(TipoAncla.MIRADOR, 142f, 210f)) else if (fardos(m) >= 4) add(Ancla(TipoAncla.MIRADOR, 133f, 166f)) else add(Ancla(TipoAncla.MIRADOR, 148f, 186f))  // encima de los fardos
        add(Ancla(TipoAncla.OBJETO, OBJETO.x + 30f, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, 48f, PISO_GATO))                             // debajo del toldo, junto al muro
        if (EscenaSeda.lumbre(m) > 0f) add(Ancla(TipoAncla.FUEGO, LUMBRE.x + 34f, PISO_GATO, hasta = LUMBRE.x))
        if (EscenaSeda.fiesta(m) || EscenaSeda.linternas(m)) add(Ancla(TipoAncla.ADORNO, 150f, 86f))
    }
}
