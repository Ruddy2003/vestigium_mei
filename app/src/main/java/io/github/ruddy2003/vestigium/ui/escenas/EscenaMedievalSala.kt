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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Estancia
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Lado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hazDeSol
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.manchaDeSol
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoH
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoV
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * La sala del scriptorium: la caja en perspectiva (una [Estancia] de la biblioteca), el techo de vigas, los muros de
 * cal con su sillería pintada, el suelo de losas de barro, las dos ventanas con lo que se ve por ellas, el armario de
 * los libros, la puerta del dormitorio y la mancha de sol.
 *
 * La sala se mide en unidades del testero (la pared del fondo): una unidad es una del lienzo en esa pared. La tercera
 * medida, `t`, es la distancia al testero hacia quien mira.
 */
internal object SalaMedieval {
    private val C = ColorMedieval

    /** La sala: quien mira está de pie, algo a la derecha, a 230 unidades del testero. La pared derecha queda fuera del cuadro. */
    val E = Estancia(fuga = Offset(262f, 128f), ojo = 230f, x0 = 104f, x1 = 440f, techo = 40f, suelo = 196f, cerca = 131f)

    private fun p(x: Float, y: Float, t: Float = 0f): Offset = E.p(x, y, t)
    private fun s(t: Float): Float = E.s(t)

    /** La altura de los ojos: ahí queda el horizonte de lo que se ve por las ventanas. */
    const val HORIZONTE = 128f

    // ---- La ventana del sur, en la pared izquierda: dos luces de medio punto con su columnilla (una bífora).
    private const val B0 = 6f
    private const val B1 = 45f
    private const val B2 = 52f
    private const val B3 = 92f
    private const val B_ALF = 154f
    private const val B_ARR = 96f
    private const val B_RY = 20f
    private const val GRUESO = 16f

    // ---- La ventana del testero, que da al claustro: tres arcos sobre dos columnillas (una trífora).
    private val VANOS = listOf(116f to 162f, 168f to 214f, 220f to 266f)
    private const val T_ALF = 158f
    private const val T_ARR = 99f
    private const val T_GRUESO = 14f

    // ---- El armario de los libros y la puerta del dormitorio, en el testero.
    const val ARM_X0 = 282f
    const val ARM_X1 = 338f
    const val ARM_Y = 92f
    const val PUERTA_X0 = 350f
    const val PUERTA_X1 = 388f
    const val PUERTA_ARR = 120f

    /** Desde dónde alumbra el cielo que entra por cada ventana, y la lámpara del dormitorio por la puerta. */
    val LUZ_SUR = p(E.x0, 122f, 49f)
    val LUZ_CLAUSTRO = Offset(191f, 122f)
    val LUZ_PUERTA = Offset(364f, 152f)

    /** El contorno de una luz de la bífora, como (hondo, alto), del alféizar al arco y otra vez al alféizar. */
    private fun luz(t0: Float, t1: Float): List<Pair<Float, Float>> = buildList {
        add(t0 to B_ALF); val c = (t0 + t1) / 2f; val r = (t1 - t0) / 2f
        for (i in 0..12) { val a = PI.toFloat() * i / 12f; add((c - r * cos(a)) to (B_ARR - B_RY * sin(a))) }
        add(t1 to B_ALF)
    }
    private val LUCES_SUR = listOf(luz(B0, B1), luz(B2, B3))

    /** Un contorno de la pared izquierda, [hondo] unidades dentro del muro. */
    private fun enMuro(puntos: List<Pair<Float, Float>>, hondo: Float): Path = poligono(puntos.map { (t, y) -> p(E.x0 - hondo, y, t) })

    /** Un arco de la trífora, a [t] del testero (negativo: dentro del muro). */
    private fun vano(x0: Float, x1: Float, t: Float): Path = Path().apply {
        val r = (x1 - x0) / 2f; val cx = (x0 + x1) / 2f; val k = 0.5523f * r
        moveTo(p(x0, T_ALF, t)); lineTo(p(x0, T_ARR, t))
        cubicTo(p(x0, T_ARR - k, t), p(cx - k, T_ARR - r, t), p(cx, T_ARR - r, t)); cubicTo(p(cx + k, T_ARR - r, t), p(x1, T_ARR - k, t), p(x1, T_ARR, t))
        lineTo(p(x1, T_ALF, t)); close()
    }

    /** Por dónde se ve el exterior: los vidrios de las dos luces del sur y los tres arcos del claustro. */
    fun huecos(): List<Path> = LUCES_SUR.map { enMuro(it, GRUESO) } + Path().apply { for ((x0, x1) in VANOS) addPath(vano(x0, x1, -T_GRUESO)) }

    /** Cuánta luz se ve en las ventanas de la iglesia: la lámpara que no se apaga y, en los oficios de noche, todas. */
    fun iglesiaEncendida(m: Momento): Float { val n = m.luces().noche; return if (n < 0.3f) 0f else n * (if (EscenaMedieval.enElCoro(m)) 1f else 0.28f) }

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val l = EscenaMedieval.luces(m); val f = EscenaMedieval.focos(m); val a = Alumbrado(l, f); val ext = m.luces(); val sol = EscenaMedieval.sol(m)
        drawRect(l.tono(C.MADERA_OSC), Offset.Zero, Size(ANCHO, ALTO))
        techo(a)
        muros(m, a)
        suelo(m, a)
        ventanaDelSur(m, a, ext)
        ventanaDelClaustro(m, a, ext)
        armario(m, a)
        puerta(m, a)
        rincones(l)
        if (sol != null) {
            // De luna la mancha es tenue: se pinta dos veces para que quede fría y marcada.
            repeat(if (sol.deLuna) 2 else 1) { manchaDeSol(E, LUCES_SUR, sol, Lado.IZQUIERDA, enParedes = 0.7f) }
            hazDeSol(E, luz(B0, B3), sol, Azar(Semilla.de("medieval", "polvo")), Lado.IZQUIERDA, motas = 46)
        }
        with(PupitresMedieval) { fondo(m, a); primerPlano(m, a, f) }
        vigaCercana(m, a)
        // De día, lo que queda lejos de las ventanas (la derecha y lo alto) está en penumbra.
        val dia = (1f - l.noche).coerceIn(0f, 1f)
        drawRect(Brush.horizontalGradient(0f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.12f * dia), startX = 200f, endX = ANCHO), Offset(200f, 0f), Size(ANCHO - 200f, ALTO), blendMode = BlendMode.Multiply)
        drawRect(Brush.verticalGradient(0f to C.SOMBRA.copy(alpha = 0.26f * dia), 1f to Color.Transparent, startY = 0f, endY = 80f), Offset.Zero, Size(ANCHO, 80f), blendMode = BlendMode.Multiply)
        // El aire de la sala: el halo de la lámpara y la penumbra de los bordes.
        val k = EscenaMedieval.lampara(m)
        if (k > 0.05f) mancha(LUZ_PUERTA + Offset(-14f, 16f), 70f, 60f, C.LAMPARA, 0.07f * k)
        drawRect(Brush.radialGradient(0f to Color.Transparent, 0.55f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.3f), center = Offset(190f, 150f), radius = 290f), Offset.Zero, Size(ANCHO, ALTO), blendMode = BlendMode.Multiply)
        grano(Azar(Semilla.de("medieval", "grano")), 0.55f + 0.35f * (1f - l.noche))
    }

    // ---------------------------------------------------------------- El techo, los muros y el suelo

    /** El techo de madera: tablas que huyen hacia el testero y tres vigas de lado a lado. La cuarta, la cercana, va al final. */
    private fun DrawScope.techo(a: Alumbrado) {
        drawPath(E.formaDelTecho(), a.en(p(250f, 40f, 30f), lerp(C.MADERA_OSC, C.TINTA, 0.45f)))
        val az = Azar(Semilla.de("medieval", "techo"))
        clipPath(E.formaDelTecho()) {
            var x = 104f
            while (x < 440f) {
                val w = az.entre(11f, 17f)
                drawPath(poligono(p(x, 40f), p(x + w - 1f, 40f), p(x + w - 1f, 40f, 84f), p(x, 40f, 84f)), a.en(p(x, 40f, 20f), lerp(C.MADERA_OSC, C.MADERA, az.entre(0.05f, 0.4f))))
                x += w
            }
        }
        for (t in listOf(10f, 34f, 58f)) viga(a, t)
    }

    /** Una viga de lado a lado, a [t] del testero: la cara que mira a quien entra y la de abajo. */
    private fun DrawScope.viga(a: Alumbrado, t: Float, alto: Float = 9f, ancho: Float = 9f) {
        val abajo = poligono(p(E.x0, 40f + alto, t - ancho), p(E.x1, 40f + alto, t - ancho), p(E.x1, 40f + alto, t), p(E.x0, 40f + alto, t))
        drawPath(abajo, a.en(p(240f, 40f + alto, t), lerp(C.MADERA_OSC, C.TINTA, 0.3f)))
        val cara = poligono(p(E.x0, 40f, t), p(E.x1, 40f, t), p(E.x1, 40f + alto, t), p(E.x0, 40f + alto, t))
        planoH(a, cara, p(E.x0, 40f, t).x, ANCHO, p(E.x0, 44f, t).y, C.MADERA_OSC)
        val az = Azar(Semilla.de("medieval", "viga", t.toInt()))
        clipPath(cara) {
            // Vetas, nudos y el filo de luz de arriba abajo.
            repeat(7) { val y = p(0f, 40f + az.entre(1f, alto - 1f), t).y; val x = az.entre(0f, 300f); drawLine(a.en(Offset(x, y), C.TINTA).copy(alpha = 0.3f), Offset(x, y), Offset(x + az.entre(40f, 110f), y + az.entre(-0.6f, 0.6f)), 0.5f) }
            drawLine(a.en(p(200f, 40f + alto, t), lerp(C.MADERA, C.CAL, 0.3f)).copy(alpha = 0.5f), p(E.x0, 40f + alto - 0.6f, t), p(E.x1, 40f + alto - 0.6f, t), 0.7f)
        }
    }

    /** Los muros: cal sobre mampostería, con la sillería fingida de líneas rojas, la cenefa de arriba y el zócalo gastado. */
    private fun DrawScope.muros(m: Momento, a: Alumbrado) {
        planoH(a, E.formaDelFondo(), E.x0, ANCHO, 118f, lerp(C.CAL, C.ARENISCA, 0.5f))
        planoH(a, E.formaIzquierda(), 0f, E.x0, 150f, lerp(C.CAL, C.ARENISCA, 0.5f), mira = 1f)
        val junta = lerp(C.ROJO, C.CAL, 0.35f)
        val az = Azar(Semilla.de("medieval", "muro"))
        // ---- El testero.
        clipPath(E.formaDelFondo()) {
            // Manchas de humedad y parches de cal más nueva.
            repeat(12) { val o = Offset(az.entre(106f, 378f), az.entre(44f, 194f)); mancha(o, az.entre(10f, 26f), az.entre(8f, 20f), a.en(o, lerp(C.ARENISCA, C.MADERA_OSC, 0.4f)), 0.12f) }
            repeat(6) { val o = Offset(az.entre(106f, 378f), az.entre(60f, 180f)); mancha(o, az.entre(8f, 18f), az.entre(5f, 10f), a.en(o, Color.White), 0.14f) }
            var fila = 0; var y = 68f
            while (y < 182f) {
                drawLine(a.en(Offset(240f, y), junta).copy(alpha = 0.5f), Offset(E.x0, y), Offset(ANCHO, y), 0.6f); drawLine(a.en(Offset(240f, y), junta).copy(alpha = 0.3f), Offset(E.x0, y + 1.6f), Offset(ANCHO, y + 1.6f), 0.4f)
                var x = E.x0 + (if (fila % 2 == 0) 10f else 26f)
                while (x < ANCHO) { drawLine(a.en(Offset(x, y), junta).copy(alpha = 0.46f), Offset(x, y + 1.6f), Offset(x, y + 14f), 0.55f); x += 32f }
                y += 14f; fila++
            }
            // La cenefa: una banda roja con rombos de cal, entre dos filetes.
            // La franja pintada de arriba, desvaída: un tallo que ondula, con sus roleos, hojas verdes y frutos de oro, entre dos filetes.
            drawRect(a.en(Offset(240f, 52f), lerp(C.ROJO, C.ARENISCA, 0.6f)), Offset(E.x0, 44f), Size(ANCHO - E.x0, 16f))
            var x = E.x0 + 2f; var k = 0
            while (x < ANCHO) {
                val arriba = k % 2 == 0; val cy = if (arriba) 49.4f else 54.6f
                drawArc(a.en(Offset(x, 52f), C.CAL).copy(alpha = 0.75f), if (arriba) 180f else 0f, 180f, false, Offset(x, 47.4f), Size(13f, 9.2f), style = Stroke(1.1f))
                drawArc(a.en(Offset(x, 52f), lerp(C.VERDE, C.CAL, 0.3f)).copy(alpha = 0.8f), if (arriba) 20f else 200f, 250f, false, Offset(x + 4f, cy - 2.6f), Size(5.2f, 5.2f), style = Stroke(1f))
                drawCircle(a.en(Offset(x, 52f), C.ORO).copy(alpha = 0.8f), 1.1f, Offset(x + 6.6f, cy))
                drawOval(a.en(Offset(x, 52f), lerp(C.VERDE, C.TINTA, 0.2f)).copy(alpha = 0.6f), Offset(x + 10.4f, if (arriba) 53f else 48.4f), Size(3.4f, 1.8f))
                x += 13f; k++
            }
            for (yy in listOf(44f, 60f)) { drawLine(a.en(Offset(240f, yy), C.ORO).copy(alpha = 0.6f), Offset(E.x0, yy), Offset(ANCHO, yy), 1f); drawLine(a.en(Offset(240f, yy), C.TINTA).copy(alpha = 0.35f), Offset(E.x0, yy + 0.9f), Offset(ANCHO, yy + 0.9f), 0.5f) }
            // Donde la pintura se ha perdido, se ve la cal de debajo.
            for ((xp, wp) in listOf(146f to 16f, 236f to 22f, 300f to 12f)) mancha(Offset(xp, 52f), wp, 7f, a.en(Offset(xp, 52f), C.CAL), 0.55f)
            // Los desconchones: donde el enlucido se cayó asoma el mampuesto de arenisca, con sus juntas.
            for ((cx, cy, rx, ry) in listOf(listOf(294f, 70f, 12f, 6f), listOf(124f, 172f, 11f, 8f), listOf(246f, 184f, 16f, 7f), listOf(338f, 100f, 7f, 9f), listOf(108f, 76f, 8f, 10f))) desconchon(a, Offset(cx, cy), rx, ry, Azar(Semilla.de("medieval", "desconchon", cx.toInt()))) { q -> q }
            // Dos grietas viejas, que bajan del techo.
            for ((x0g, semilla) in listOf(278f to 1, 112f to 2)) { val g = Azar(Semilla.de("medieval", "grieta", semilla)); var q = Offset(x0g, 60f)
                repeat(9) { val s2 = q + Offset(g.entre(-2.4f, 2.4f), g.entre(5f, 11f)); drawLine(a.en(q, C.TINTA).copy(alpha = 0.4f), q, s2, 0.5f); q = s2 } }
            // El hollín que dejó, años atrás, una lámpara sobre la puerta.
            mancha(Offset(369f, 64f), 12f, 14f, a.en(Offset(369f, 64f), C.TINTA), 0.3f)
            // El arrimadero pintado: unos cortinajes fingidos, ocres, colgados de sus anillas.
            drawRect(a.en(Offset(240f, 180f), lerp(C.ORO, C.ARENISCA, 0.55f)).copy(alpha = 0.5f), Offset(E.x0, 166f), Size(ANCHO - E.x0, 30f))
            var xc = E.x0 + 4f
            while (xc < ANCHO) {
                drawArc(a.en(Offset(xc, 166f), lerp(C.ROJO, C.ARENISCA, 0.3f)).copy(alpha = 0.7f), 0f, 180f, false, Offset(xc, 161.6f), Size(14f, 8f), style = Stroke(0.8f))
                drawLine(a.en(Offset(xc, 180f), lerp(C.ROJO, C.MADERA_OSC, 0.4f)).copy(alpha = 0.35f), Offset(xc + 7f, 169.6f), Offset(xc + 5f, 196f), 0.6f)
                drawLine(a.en(Offset(xc, 180f), C.CAL).copy(alpha = 0.3f), Offset(xc + 1f, 166f), Offset(xc + 2.4f, 196f), 0.8f); xc += 14f
            }
            drawLine(a.en(Offset(240f, 165f), lerp(C.ROJO, C.ARENISCA, 0.3f)).copy(alpha = 0.7f), Offset(E.x0, 165.4f), Offset(ANCHO, 165.4f), 0.8f)
            // El zócalo: más sucio, con roces y desconchones que enseñan la piedra.
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to a.en(Offset(240f, 190f), C.MADERA_OSC).copy(alpha = 0.3f), startY = 172f, endY = 196f), Offset(E.x0, 172f), Size(ANCHO - E.x0, 24f))
            repeat(9) { val o = Offset(az.entre(108f, 376f), az.entre(180f, 195f)); mancha(o, az.entre(4f, 10f), az.entre(2.4f, 5f), a.en(o, C.ARENISCA), 0.5f) }
        }
        // ---- El muro del sur.
        clipPath(E.formaIzquierda()) {
            for ((tt, yy, rr) in listOf(Triple(100f, 176f, 9f), Triple(112f, 86f, 8f), Triple(2f, 140f, 5f))) desconchon(a, p(E.x0, yy, tt), rr * s(tt), rr * 0.8f * s(tt), Azar(Semilla.de("medieval", "desconchon", tt.toInt()))) { q -> q }
            // Un medallón pintado, desvaído: una rueda de pétalos en su orla.
            val med = p(E.x0, 118f, 116f); val rm = 15f * s(116f)
            drawOval(a.en(med, lerp(C.ROJO, C.ARENISCA, 0.55f), 1f), Offset(med.x - rm * 0.5f, med.y - rm), Size(rm, rm * 2f)); drawOval(a.en(med, lerp(C.AZUL, C.CAL, 0.55f), 1f), Offset(med.x - rm * 0.38f, med.y - rm * 0.78f), Size(rm * 0.76f, rm * 1.56f))
            for (kk in 0..7) { val ang = kk * PI.toFloat() / 4f; drawLine(a.en(med, lerp(C.ORO, C.CAL, 0.3f), 1f).copy(alpha = 0.8f), med, med + Offset(cos(ang) * rm * 0.34f, sin(ang) * rm * 0.7f), 1.1f) }
            drawOval(a.en(med, C.ORO, 1f).copy(alpha = 0.7f), Offset(med.x - rm * 0.5f, med.y - rm), Size(rm, rm * 2f), style = Stroke(0.9f)); mancha(med + Offset(2f, 8f), rm * 0.4f, rm * 0.5f, a.en(med, C.CAL, 1f), 0.5f)
            repeat(8) { val o = p(E.x0, az.entre(50f, 194f), az.entre(4f, 90f)); mancha(o, az.entre(6f, 16f), az.entre(8f, 22f), a.en(o, lerp(C.ARENISCA, C.MADERA_OSC, 0.4f), 1f), 0.14f) }
            var fila = 0; var y = 68f
            while (y < 182f) {
                drawLine(a.en(p(E.x0, y, 40f), junta, 1f).copy(alpha = 0.5f), p(E.x0, y), p(E.x0, y, 96f), 0.6f)
                var t = if (fila % 2 == 0) 6f else 18f
                while (t < 96f) { drawLine(a.en(p(E.x0, y, t), junta, 1f).copy(alpha = 0.46f), p(E.x0, y + 1f, t), p(E.x0, y + 13f, t), 0.55f); t += 24f }
                y += 14f; fila++
            }
            drawPath(poligono(p(E.x0, 51f), p(E.x0, 58f), p(E.x0, 58f, 96f), p(E.x0, 51f, 96f)), a.en(p(E.x0, 55f, 40f), lerp(C.ROJO, C.ARENISCA, 0.5f), 1f))
            var t = 5f
            while (t < 96f) { val c = p(E.x0, 54.5f, t); val k = s(t); drawPath(poligono(c + Offset(-1.6f * k, 0f), c + Offset(0f, -2.2f * k), c + Offset(1.6f * k, 0f), c + Offset(0f, 2.2f * k)), a.en(c, if ((t / 9f).toInt() % 2 == 0) C.CAL else C.ORO, 1f).copy(alpha = 0.6f)); t += 9f }
            drawPath(poligono(p(E.x0, 166f), p(E.x0, 196f), p(E.x0, 196f, 96f), p(E.x0, 166f, 96f)), a.en(p(E.x0, 180f, 40f), lerp(C.ORO, C.ARENISCA, 0.55f), 1f).copy(alpha = 0.5f))
            var tc = 3f
            while (tc < 96f) { drawLine(a.en(p(E.x0, 180f, tc), lerp(C.ROJO, C.MADERA_OSC, 0.4f), 1f).copy(alpha = 0.35f), p(E.x0, 169f, tc), p(E.x0, 196f, tc - 1.4f), 0.6f); tc += 9f }
            drawLine(a.en(p(E.x0, 166f, 40f), lerp(C.ROJO, C.ARENISCA, 0.3f), 1f).copy(alpha = 0.7f), p(E.x0, 165.4f), p(E.x0, 165.4f, 96f), 0.8f)
            repeat(7) { val o = p(E.x0, az.entre(180f, 195f), az.entre(4f, 92f)); mancha(o, az.entre(3f, 8f), az.entre(3f, 7f), a.en(o, C.ARENISCA, 1f), 0.5f) }
        }
    }

    /** El suelo de losas de barro cocido, a matajunta: más claras por donde se pasa, con juntas, roturas y gotas de tinta. */
    private fun DrawScope.suelo(m: Momento, a: Alumbrado) {
        val az = Azar(Semilla.de("medieval", "suelo")); val hondo = 13.4f; val ancho = 19f
        drawPath(E.formaDelSuelo(), a.en(p(240f, 196f, 60f), lerp(C.LOSA, C.TINTA, 0.42f)))
        clipPath(E.formaDelSuelo()) {
            var j = 0; var t0 = 0f
            while (t0 < E.cerca + 4f) {
                var x = E.x0 - (if (j % 2 == 0) 0f else ancho / 2f)
                while (x < E.x1) {
                    val base = lerp(lerp(C.LOSA, C.ARENISCA, az.entre(0f, 0.6f)), C.MADERA_OSC, az.entre(0f, 0.34f))
                    val o = p(x + ancho / 2f, 196f, t0 + hondo / 2f)
                    if (o.x > -30f && o.x < ANCHO + 30f) {
                        val losa = poligono(p(x + 0.5f, 196f, t0 + 0.4f), p(x + ancho - 0.5f, 196f, t0 + 0.4f), p(x + ancho - 0.5f, 196f, t0 + hondo - 0.4f), p(x + 0.5f, 196f, t0 + hondo - 0.4f))
                        drawPath(losa, a.en(o, base, 0f, 0.4f * (1f - t0 / 140f)))
                        // Alguna losa rota por una esquina, o partida.
                        if (az.real() < 0.16f) drawLine(a.en(o, C.TINTA).copy(alpha = 0.4f), p(x + az.entre(2f, ancho - 2f), 196f, t0 + 0.5f), p(x + az.entre(2f, ancho - 2f), 196f, t0 + hondo - 0.5f), 0.5f)
                    } else az.real()
                    x += ancho
                }
                t0 += hondo; j++
            }
            // La senda que deja el paso de los años, de la puerta a los pupitres; y el polvo de piedra pómez junto a ellos.
            for (k in 0..7) { val t = 8f + k * 15f; val o = p(330f - k * 16f, 196f, t); mancha(o, 26f * s(t), 5f * s(t), a.en(o, C.CAL), 0.09f) }
            repeat(40) { val o = p(az.entre(110f, 300f), 196f, az.entre(2f, 126f)); drawCircle(a.en(o, C.PERGAMINO).copy(alpha = az.entre(0.2f, 0.5f)), az.entre(0.4f, 1f), o) }
            repeat(9) { val o = p(az.entre(180f, 290f), 196f, az.entre(70f, 124f)); drawOval(a.en(o, C.TINTA).copy(alpha = az.entre(0.25f, 0.5f)), o - Offset(1.6f, 0.6f), Size(az.entre(1.6f, 3.6f), az.entre(0.7f, 1.3f))) }
            // Lo que el suelo devuelve de las dos ventanas.
            mancha(p(E.x0 + 30f, 196f, 48f), 46f, 12f, a.focos[0].color, 0.12f * a.focos[0].fuerza)
            mancha(p(191f, 196f, 14f), 60f, 9f, a.focos[1].color, 0.12f * a.focos[1].fuerza)
        }
    }

    /** La penumbra de los rincones, que es lo que da hondura a la caja. */
    private fun DrawScope.rincones(l: Luces) {
        val c = C.SOMBRA.copy(alpha = 0.3f)
        drawRect(Brush.horizontalGradient(0f to c, 1f to Color.Transparent, startX = E.x0, endX = E.x0 + 18f), Offset(E.x0, 40f), Size(18f, 156f), blendMode = BlendMode.Multiply)
        drawRect(Brush.verticalGradient(0f to C.SOMBRA.copy(alpha = 0.55f), 1f to Color.Transparent, startY = 40f, endY = 78f), Offset(E.x0, 40f), Size(ANCHO - E.x0, 38f), blendMode = BlendMode.Multiply)
        drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to c.copy(alpha = 0.22f), startY = 184f, endY = 196f), Offset(E.x0, 184f), Size(ANCHO - E.x0, 12f), blendMode = BlendMode.Multiply)
        clipPath(E.formaDelSuelo()) { drawRect(Brush.verticalGradient(0f to c, 1f to Color.Transparent, startY = 196f, endY = 210f), Offset(0f, 196f), Size(ANCHO, 14f), blendMode = BlendMode.Multiply) }
        clipPath(E.formaIzquierda()) { drawRect(Brush.horizontalGradient(0f to Color.Transparent, 1f to c, startX = E.x0 - 16f, endX = E.x0), Offset(E.x0 - 16f, 0f), Size(16f, ALTO), blendMode = BlendMode.Multiply) }
        if (l.noche > 0.3f) drawRect(Brush.verticalGradient(0f to c.copy(alpha = 0.3f * l.noche), 1f to Color.Transparent, startY = 0f, endY = 90f), Offset.Zero, Size(ANCHO, 90f), blendMode = BlendMode.Multiply)
    }

    // ---------------------------------------------------------------- La ventana del sur

    /** El emplomado: los vidrios pequeños, verdosos, con su red de plomo. Deja ver lo de fuera. */
    private fun DrawScope.emplomado(ext: Luces, lineas: List<Pair<Offset, Offset>>) {
        drawRect(lerp(C.VERDE, Color.White, 0.5f).copy(alpha = 0.1f + 0.05f * ext.noche), Offset.Zero, Size(ANCHO, ALTO))
        val plomo = lerp(C.HIERRO, ext.medio, 0.25f * (1f - ext.noche)).copy(alpha = 0.5f)
        for ((p0, p1) in lineas) drawLine(plomo, p0, p1, 0.5f)
    }

    private fun DrawScope.ventanaDelSur(m: Momento, a: Alumbrado, ext: Luces) {
        val claro = lerp(a.en(LUZ_SUR, C.CAL), lerp(ext.medio, Color.White, 0.5f), 0.45f * (1f - ext.noche))
        // El recuadro de arenisca que abraza las dos luces.
        val marco = buildList { add(B0 - 5f to B_ALF + 3f); add(B0 - 5f to B_ARR - 4f); for (i in 0..12) { val ang = PI.toFloat() * i / 12f; add(((B0 + B3) / 2f - ((B3 - B0) / 2f + 5f) * cos(ang)) to (B_ARR - 4f - (B_RY + 12f) * sin(ang))) }; add(B3 + 5f to B_ARR - 4f); add(B3 + 5f to B_ALF + 3f) }
        drawPath(enMuro(marco, 0f), a.en(LUZ_SUR + Offset(10f, 0f), lerp(C.ARENISCA, C.CAL, 0.25f), 1f))
        for ((i, luz) in LUCES_SUR.withIndex()) {
            val dentro = enMuro(luz, 0f); val vidrio = enMuro(luz, GRUESO)
            // El derrame del muro: de día es lo más claro de la sala.
            drawPath(dentro, claro); clipPath(dentro) { drawRect(Brush.verticalGradient(0f to C.SOMBRA.copy(alpha = 0.6f), 1f to Color.Transparent, startY = p(E.x0, B_ARR - B_RY, 40f).y, endY = p(E.x0, B_ARR + 8f, 40f).y), Offset(0f, 0f), Size(E.x0, 140f), blendMode = BlendMode.Multiply) }
            clipPath(dentro) {
                clipPath(vidrio) {
                    with(FueraMedieval) { valle(m, ext) }
                    val t0 = if (i == 0) B0 else B2; val t1 = if (i == 0) B1 else B3
                    emplomado(ext, buildList {
                        var t = t0 + 6f; while (t < t1) { add(p(E.x0 - GRUESO, B_ARR - B_RY, t) to p(E.x0 - GRUESO, B_ALF, t)); t += 6f }
                        var y = B_ARR - 9f; while (y < B_ALF) { add(p(E.x0 - GRUESO, y, t0) to p(E.x0 - GRUESO, y, t1)); y += 10f }
                    })
                }
                // La jamba del fondo, que da la cara a quien mira: se ve el grueso del muro.
                drawPath(vidrio, a.en(LUZ_SUR, C.TINTA).copy(alpha = 0.5f), style = Stroke(0.9f))
            }
            drawPath(dentro, a.en(LUZ_SUR + Offset(20f, 20f), C.ARENISCA, 1f), style = Stroke(2.4f))
            drawPath(dentro, lerp(ext.medio, Color.White, 0.6f).copy(alpha = 0.45f * (1f - ext.noche)), style = Stroke(0.7f))
        }
        // Las dovelas de cada luz, alternando la arenisca roja y la amarilla.
        for ((t0, t1) in listOf(B0 to B1, B2 to B3)) { val c = (t0 + t1) / 2f; val r = (t1 - t0) / 2f
            for (i in 0 until 7) { val a0 = PI.toFloat() * i / 7f; val a1 = PI.toFloat() * (i + 1) / 7f
                fun q(ang: Float, k: Float) = p(E.x0, B_ARR - (B_RY + k) * sin(ang), c - (r + k) * cos(ang))
                drawPath(poligono(q(a0, 1f), q(a1, 1f), q(a1, 6.4f), q(a0, 6.4f)), a.en(LUZ_SUR + Offset(10f, -20f), if (i % 2 == 0) lerp(C.ROJO, C.ARENISCA, 0.55f) else lerp(C.ARENISCA, C.CAL, 0.55f), 1f))
                drawLine(a.en(LUZ_SUR, C.TINTA).copy(alpha = 0.25f), q(a0, 1f), q(a0, 6.4f), 0.4f) } }
        // La columnilla, con su capitel de cubo y su basa.
        val hx = E.x0 - GRUESO * 0.45f
        val fuste = poligono(p(hx, B_ARR + 2f, B1 + 1.4f), p(hx, B_ARR + 2f, B2 - 1.4f), p(hx, B_ALF - 3f, B2 - 1.4f), p(hx, B_ALF - 3f, B1 + 1.4f))
        drawPath(fuste, Brush.horizontalGradient(0f to a.en(LUZ_SUR, lerp(C.ARENISCA, C.TINTA, 0.35f)), 1f to a.en(LUZ_SUR, lerp(C.ARENISCA, C.CAL, 0.4f), 1f), startX = p(hx, 120f, B2).x, endX = p(hx, 120f, B1).x))
        drawPath(poligono(p(hx, B_ARR - 5f, B1 - 2f), p(hx, B_ARR - 5f, B2 + 2f), p(hx, B_ARR + 2f, B2 - 1f), p(hx, B_ARR + 2f, B1 + 1f)), a.en(LUZ_SUR, lerp(C.ARENISCA, C.CAL, 0.2f), 1f))
        drawPath(poligono(p(hx, B_ALF - 3f, B1 + 1f), p(hx, B_ALF - 3f, B2 - 1f), p(hx, B_ALF, B2 + 2f), p(hx, B_ALF, B1 - 2f)), a.en(LUZ_SUR, C.ARENISCA, 1f))
        // El alféizar, que sobresale, con su filo de luz; y la claridad que el cielo deja en la pared de alrededor.
        drawPath(poligono(p(E.x0, B_ALF, B0 - 6f), p(E.x0, B_ALF, B3 + 6f), p(E.x0 + 5f, B_ALF + 1f, B3 + 6f), p(E.x0 + 5f, B_ALF + 1f, B0 - 6f)), a.en(LUZ_SUR + Offset(0f, 30f), lerp(C.ARENISCA, C.CAL, 0.4f), 1f))
        drawPath(poligono(p(E.x0 + 5f, B_ALF + 1f, B0 - 6f), p(E.x0 + 5f, B_ALF + 1f, B3 + 6f), p(E.x0 + 5f, B_ALF + 5f, B3 + 6f), p(E.x0 + 5f, B_ALF + 5f, B0 - 6f)), a.en(LUZ_SUR + Offset(0f, 30f), lerp(C.ARENISCA, C.MADERA_OSC, 0.3f), 1f))
        // Las contraventanas, abiertas contra el muro, con sus herrajes.
        for ((t0, t1) in listOf(99f to 106f)) {
            val hoja = poligono(p(E.x0 + 1f, B_ARR - 16f, t0), p(E.x0 + 1f, B_ARR - 16f, t1), p(E.x0 + 1f, B_ALF, t1), p(E.x0 + 1f, B_ALF, t0))
            drawPath(hoja, a.en(p(E.x0, 124f, t0), C.MADERA_OSC, 1f))
            for (y in listOf(B_ARR, B_ALF - 12f)) drawLine(a.en(p(E.x0, y, t0), C.HIERRO), p(E.x0 + 1f, y, t0), p(E.x0 + 1f, y, t1), 0.9f * s(t0))
        }
        mancha(LUZ_SUR, 58f, 70f, lerp(ext.medio, Color.White, 0.5f), 0.1f * (1f - ext.noche) * (1f - 0.4f * ext.nublado))
    }

    /** Un desconchón del enlucido: el mampuesto de arenisca que asoma, con sus juntas y el borde de cal en sombra. */
    private fun DrawScope.desconchon(a: Alumbrado, c: Offset, rx: Float, ry: Float, az: Azar, lleva: (Offset) -> Offset) {
        val borde = (0..9).map { val ang = it * PI.toFloat() * 2f / 10f; val k = az.entre(0.6f, 1.1f); lleva(c + Offset(cos(ang) * rx * k, sin(ang) * ry * k)) }
        val forma = poligono(borde)
        drawPath(Path().apply { addPath(forma, Offset(-0.6f, -0.8f)) }, a.en(c, lerp(C.MADERA_OSC, C.SOMBRA, 0.4f)).copy(alpha = 0.6f))
        drawPath(forma, a.en(c, lerp(C.ARENISCA, C.MADERA_OSC, 0.3f)))
        clipPath(forma) {
            var y = c.y - ry; var fila = 0
            while (y < c.y + ry) { drawLine(a.en(c, C.TINTA).copy(alpha = 0.4f), Offset(c.x - rx, y), Offset(c.x + rx, y), 0.5f)
                var x = c.x - rx + (if (fila % 2 == 0) 2f else 5f); while (x < c.x + rx) { drawLine(a.en(c, C.TINTA).copy(alpha = 0.35f), Offset(x, y), Offset(x, y + 3.6f), 0.5f); if (az.real() < 0.4f) drawRect(a.en(c, lerp(C.ARENISCA, C.CAL, 0.35f)).copy(alpha = 0.6f), Offset(x + 0.6f, y + 0.6f), Size(4.4f, 2.4f)); x += 6.6f }
                y += 3.6f; fila++ }
        }
    }

    /** Las dovelas de un arco del testero, alternando la arenisca roja y la amarilla, como en las iglesias del Rin. */
    private fun DrawScope.dovelas(a: Alumbrado, cx: Float, arr: Float, r0: Float, r1: Float, n: Int) {
        drawArc(C.SOMBRA.copy(alpha = 0.45f), 180f, 180f, false, Offset(cx - r1 - 0.4f, arr - r1 - 1.6f), Size(2f * r1 + 0.8f, 2f * r1 + 0.8f), style = Stroke(2.2f), blendMode = BlendMode.Multiply)
        for (i in 0 until n) {
            val a0 = PI.toFloat() * i / n; val a1 = PI.toFloat() * (i + 1) / n
            val c = if (i % 2 == 0) lerp(C.ROJO, C.ARENISCA, 0.55f) else lerp(C.ARENISCA, C.CAL, 0.55f)
            drawPath(poligono(Offset(cx - r0 * cos(a0), arr - r0 * sin(a0)), Offset(cx - r0 * cos(a1), arr - r0 * sin(a1)), Offset(cx - r1 * cos(a1), arr - r1 * sin(a1)), Offset(cx - r1 * cos(a0), arr - r1 * sin(a0))), a.en(Offset(cx, arr - r1), c))
            drawLine(a.en(Offset(cx, arr - r1), C.TINTA).copy(alpha = 0.25f), Offset(cx - r0 * cos(a0), arr - r0 * sin(a0)), Offset(cx - r1 * cos(a0), arr - r1 * sin(a0)), 0.4f)
        }
    }

    // ---------------------------------------------------------------- La ventana del claustro

    private fun DrawScope.ventanaDelClaustro(m: Momento, a: Alumbrado, ext: Luces) {
        val claro = lerp(a.en(LUZ_CLAUSTRO, C.CAL), lerp(ext.medio, Color.White, 0.5f), 0.4f * (1f - ext.noche))
        // El paño de arenisca de los tres arcos, con su imposta.
        val pano = Path().apply {
            moveTo(110f, T_ALF + 2f); lineTo(110f, T_ARR); cubicTo(Offset(110f, 62f), Offset(272f, 62f), Offset(272f, T_ARR)); lineTo(272f, T_ALF + 2f); close()
        }
        drawPath(pano, a.en(Offset(191f, 86f), lerp(C.ARENISCA, C.CAL, 0.55f)))
        drawPath(pano, a.en(Offset(191f, 86f), C.TINTA).copy(alpha = 0.2f), style = Stroke(0.6f))
        val todos = Path(); val fuera = Path()
        for ((x0, x1) in VANOS) { todos.addPath(vano(x0, x1, 0f)); fuera.addPath(vano(x0, x1, -T_GRUESO)) }
        drawPath(todos, claro)
        // El grueso del muro: el intradós de cada arco, en sombra; la jamba que mira a la luz, clara; el alféizar, lo más claro.
        clipPath(todos) {
            drawRect(Brush.verticalGradient(0f to C.SOMBRA.copy(alpha = 0.7f), 1f to Color.Transparent, startY = 74f, endY = 102f), Offset(110f, 70f), Size(164f, 34f), blendMode = BlendMode.Multiply)
            for ((x0, x1) in VANOS) drawRect(Brush.horizontalGradient(0f to lerp(C.CAL, Color.White, 0.3f).copy(alpha = 0.5f * (1f - ext.noche)), 1f to Color.Transparent, startX = x0, endX = x0 + 9f), Offset(x0, 100f), Size(9f, 58f))
            drawRect(lerp(C.CAL, Color.White, 0.5f).copy(alpha = 0.55f * (1f - ext.noche)), Offset(110f, 154.6f), Size(164f, 4f))
        }
        clipPath(todos) {
            clipPath(fuera) {
                with(FueraMedieval) { claustro(m, ext) }
                emplomado(ext, buildList {
                    for ((x0, x1) in VANOS) { var x = x0 + 9f; while (x < x1 - 2f) { add(p(x, T_ARR - 24f, -T_GRUESO) to p(x, T_ALF, -T_GRUESO)); x += 9.2f } }
                    var y = T_ARR - 12f; while (y < T_ALF) { add(p(112f, y, -T_GRUESO) to p(270f, y, -T_GRUESO)); y += 12f }
                })
            }
            drawPath(fuera, a.en(LUZ_CLAUSTRO, C.TINTA).copy(alpha = 0.45f), style = Stroke(0.9f))
        }
        // Los arcos, con sus dovelas, y su filo de luz.
        for ((x0, x1) in VANOS) {
            val v = vano(x0, x1, 0f)
            drawPath(v, a.en(Offset((x0 + x1) / 2f, 90f), C.ARENISCA), style = Stroke(2.6f))
            drawPath(v, lerp(ext.medio, Color.White, 0.6f).copy(alpha = 0.4f * (1f - ext.noche)), style = Stroke(0.7f))
            val cx = (x0 + x1) / 2f; val r = (x1 - x0) / 2f
            dovelas(a, cx, T_ARR, r + 1.3f, r + 8f, 9)
        }
        // Las dos columnillas: basa, fuste con su lado de luz y capitel de cubo.
        for (x in listOf(165f, 217f)) {
            val luzIzq = a.en(Offset(x - 3f, 128f), lerp(C.ARENISCA, C.CAL, 0.45f), -1f); val sombra = a.en(Offset(x + 3f, 128f), lerp(C.ARENISCA, C.TINTA, 0.3f), 1f)
            drawRect(Brush.horizontalGradient(0f to luzIzq, 0.5f to a.en(Offset(x, 128f), C.ARENISCA), 1f to sombra, startX = x - 2.6f, endX = x + 2.6f), Offset(x - 2.6f, T_ARR + 6f), Size(5.2f, T_ALF - T_ARR - 11f))
            drawPath(poligono(Offset(x - 5.4f, T_ARR - 2f), Offset(x + 5.4f, T_ARR - 2f), Offset(x + 2.8f, T_ARR + 6f), Offset(x - 2.8f, T_ARR + 6f)), a.en(Offset(x, T_ARR), lerp(C.ARENISCA, C.CAL, 0.2f)))
            drawRect(a.en(Offset(x, T_ARR - 3f), lerp(C.ARENISCA, C.CAL, 0.4f)), Offset(x - 6f, T_ARR - 4.4f), Size(12f, 2.6f))
            drawPath(poligono(Offset(x - 2.8f, T_ALF - 5f), Offset(x + 2.8f, T_ALF - 5f), Offset(x + 4.6f, T_ALF), Offset(x - 4.6f, T_ALF)), a.en(Offset(x, T_ALF - 3f), C.ARENISCA))
        }
        // El alféizar corrido, con unas plumas y un tiesto de albahaca que alguien dejó.
        drawRect(a.en(Offset(191f, T_ALF), lerp(C.ARENISCA, C.CAL, 0.45f)), Offset(110f, T_ALF), Size(162f, 2.4f))
        drawRect(a.en(Offset(191f, T_ALF + 4f), lerp(C.ARENISCA, C.MADERA_OSC, 0.3f)), Offset(110f, T_ALF + 2.4f), Size(162f, 3.4f))
        mancha(LUZ_CLAUSTRO, 96f, 58f, lerp(ext.medio, Color.White, 0.5f), 0.1f * (1f - ext.noche) * (1f - 0.4f * ext.nublado))
    }

    // ---------------------------------------------------------------- El armario de los libros y la puerta

    /** El armario de los libros, que tiene llave: dos hojas de roble con herrajes. Mientras se trabaja, una hoja abierta deja ver los libros, tumbados. */
    private fun DrawScope.armario(m: Momento, a: Alumbrado) {
        val medio = (ARM_X0 + ARM_X1) / 2f; val abierto = EscenaMedieval.salaEnMarcha(m)
        // La sombra que arroja en la pared y el cuerpo.
        drawRect(C.SOMBRA.copy(alpha = 0.3f), Offset(ARM_X0 - 5f, ARM_Y + 3f), Size(6f, E.suelo - ARM_Y - 3f))
        planoH(a, poligono(Offset(ARM_X0, ARM_Y), Offset(ARM_X1, ARM_Y), Offset(ARM_X1, E.suelo), Offset(ARM_X0, E.suelo)), ARM_X0, ARM_X1, 140f, C.MADERA_OSC)
        // La cornisa y el frontón bajo, tallado con un arquillo.
        drawPath(poligono(Offset(ARM_X0 - 3f, ARM_Y), Offset(ARM_X1 + 3f, ARM_Y), Offset(ARM_X1 + 1f, ARM_Y - 4f), Offset(medio, ARM_Y - 12f), Offset(ARM_X0 - 1f, ARM_Y - 4f)), a.en(Offset(medio, ARM_Y - 4f), lerp(C.MADERA_OSC, C.MADERA, 0.35f)))
        drawArc(a.en(Offset(medio, ARM_Y - 3f), C.TINTA).copy(alpha = 0.5f), 180f, 180f, false, Offset(medio - 6f, ARM_Y - 9f), Size(12f, 10f), style = Stroke(0.8f))
        drawRect(a.en(Offset(medio, ARM_Y), lerp(C.MADERA, C.CAL, 0.2f)), Offset(ARM_X0 - 3f, ARM_Y), Size(ARM_X1 - ARM_X0 + 6f, 2.2f))
        val hueco = poligono(Offset(ARM_X0 + 4f, ARM_Y + 7f), Offset(medio - 1f, ARM_Y + 7f), Offset(medio - 1f, E.suelo - 8f), Offset(ARM_X0 + 4f, E.suelo - 8f))
        if (abierto) {
            // Dentro: tres baldas con los libros tumbados, de canto, como se guardaban.
            drawPath(hueco, a.en(Offset(ARM_X0 + 14f, 140f), lerp(C.TINTA, C.MADERA_OSC, 0.4f)))
            val az = Azar(Semilla.de("medieval", "armario"))
            for (k in 0..3) { val y = ARM_Y + 30f + k * 23f
                drawRect(a.en(Offset(ARM_X0 + 14f, y), C.MADERA), Offset(ARM_X0 + 4f, y), Size(medio - ARM_X0 - 5f, 1.6f))
                var alto = 0f
                repeat(2 + az.entero(3)) { val h = az.entre(2.6f, 4.6f); val w = az.entre(14f, 21f); alto += h
                    drawRect(a.en(Offset(ARM_X0 + 12f, y - alto), listOf(C.ROJO, C.AZUL, C.MADERA, C.VERDE, C.LANA)[az.entero(5)]), Offset(ARM_X0 + 6f + az.entre(0f, 3f), y - alto), Size(w, h - 0.6f))
                    drawRect(a.en(Offset(ARM_X0 + 12f, y - alto), C.PERGAMINO), Offset(ARM_X0 + 6.6f + w * 0.9f, y - alto + 0.5f), Size(w * 0.12f, h - 1.6f)) }
            }
            // La hoja abierta, vista casi de canto.
            drawPath(poligono(Offset(ARM_X0 + 4f, ARM_Y + 7f), Offset(ARM_X0 - 6f, ARM_Y + 3f), Offset(ARM_X0 - 6f, E.suelo - 4f), Offset(ARM_X0 + 4f, E.suelo - 8f)), a.en(Offset(ARM_X0 - 2f, 140f), lerp(C.MADERA_OSC, C.MADERA, 0.3f), -1f))
            for (y in listOf(ARM_Y + 22f, E.suelo - 24f)) drawLine(a.en(Offset(ARM_X0, y), C.HIERRO), Offset(ARM_X0 + 4f, y), Offset(ARM_X0 - 6f, y - 1.5f), 1.3f)
        }
        // Las hojas, con sus tablas, sus bisagras de hierro en cola de golondrina y la cerradura.
        for ((k, x0) in listOf(ARM_X0 + 4f, medio + 1f).withIndex()) {
            if (k == 0 && abierto) continue
            val w = medio - ARM_X0 - 5f
            drawRect(a.en(Offset(x0 + w / 2f, 140f), lerp(C.MADERA_OSC, C.MADERA, 0.5f)), Offset(x0, ARM_Y + 7f), Size(w, E.suelo - ARM_Y - 15f))
            for (dx in listOf(w * 0.34f, w * 0.67f)) drawLine(a.en(Offset(x0, 140f), C.TINTA).copy(alpha = 0.3f), Offset(x0 + dx, ARM_Y + 7f), Offset(x0 + dx, E.suelo - 8f), 0.5f)
            for (y in listOf(ARM_Y + 22f, 144f, E.suelo - 24f)) { val desde = if (k == 0) x0 else x0 + w; val dir = if (k == 0) 1f else -1f
                drawLine(a.en(Offset(x0, y), C.HIERRO), Offset(desde, y), Offset(desde + dir * w * 0.7f, y), 1.5f, StrokeCap.Round)
                drawLine(a.en(Offset(x0, y), C.HIERRO), Offset(desde + dir * w * 0.7f, y), Offset(desde + dir * w * 0.86f, y - 2.2f), 0.9f, StrokeCap.Round); drawLine(a.en(Offset(x0, y), C.HIERRO), Offset(desde + dir * w * 0.7f, y), Offset(desde + dir * w * 0.86f, y + 2.2f), 0.9f, StrokeCap.Round) }
        }
        drawRect(a.en(Offset(medio, 136f), lerp(C.HIERRO, C.ORO, 0.3f)), Offset(medio - 0.6f, 131f), Size(5.4f, 7f)); drawCircle(a.en(Offset(medio, 134f), C.TINTA), 0.9f, Offset(medio + 2.2f, 133.6f))
        // Las patas y el filo de luz del canto.
        for (x in listOf(ARM_X0, ARM_X1 - 4f)) drawRect(a.en(Offset(x, 192f), lerp(C.MADERA_OSC, C.TINTA, 0.3f)), Offset(x, E.suelo - 8f), Size(4f, 8f))
        drawLine(a.en(Offset(ARM_X0, 120f), lerp(C.MADERA, C.CAL, 0.3f), -1f).copy(alpha = 0.6f), Offset(ARM_X0 + 0.4f, ARM_Y + 2f), Offset(ARM_X0 + 0.4f, E.suelo), 0.8f)
    }

    /** La puerta del dormitorio, de arco, entornada: de noche se cuela por ella la luz de la lámpara que arde allí. */
    private fun DrawScope.puerta(m: Momento, a: Alumbrado) {
        val k = EscenaMedieval.lampara(m)
        val cx = (PUERTA_X0 + PUERTA_X1) / 2f; val r = (PUERTA_X1 - PUERTA_X0) / 2f; val q = 0.5523f * r
        val vano = Path().apply { moveTo(PUERTA_X0, E.suelo); lineTo(PUERTA_X0, PUERTA_ARR); cubicTo(PUERTA_X0, PUERTA_ARR - q, cx - q, PUERTA_ARR - r, cx, PUERTA_ARR - r); cubicTo(cx + q, PUERTA_ARR - r, PUERTA_X1, PUERTA_ARR - q, PUERTA_X1, PUERTA_ARR); lineTo(PUERTA_X1, E.suelo); close() }
        // El hueco: oscuro de día; de noche, la luz cálida del dormitorio.
        drawPath(vano, lerp(a.l.tono(lerp(C.TINTA, C.MADERA_OSC, 0.3f)), lerp(C.LAMPARA, Color(0xFFFFE2B0), 0.4f), 0.85f * k))
        if (k > 0.05f) clipPath(vano) { drawRect(Brush.radialGradient(listOf(Color(0xFFFFF2D0).copy(alpha = 0.8f * k), Color.Transparent), LUZ_PUERTA + Offset(-6f, 4f), 26f), Offset(PUERTA_X0, 90f), Size(40f, 110f)) }
        // La hoja, entornada hacia dentro: se ve en escorzo, con sus tablas y sus dos fajas de hierro.
        val hoja = Path().apply { moveTo(PUERTA_X0 + 13f, E.suelo); lineTo(PUERTA_X0 + 13f, PUERTA_ARR - r * 0.82f); cubicTo(PUERTA_X0 + 20f, PUERTA_ARR - r - 1f, PUERTA_X1 - 6f, PUERTA_ARR - r * 0.7f, PUERTA_X1, PUERTA_ARR); lineTo(PUERTA_X1, E.suelo); close() }
        planoH(a, hoja, PUERTA_X0 + 13f, PUERTA_X1, 150f, lerp(C.MADERA_OSC, C.MADERA, 0.4f), mira = -1f)
        clipPath(hoja) {
            for (x in listOf(PUERTA_X0 + 19f, PUERTA_X0 + 25f, PUERTA_X0 + 31f)) drawLine(a.en(Offset(x, 150f), C.TINTA).copy(alpha = 0.35f), Offset(x, 100f), Offset(x, E.suelo), 0.5f)
            for (y in listOf(128f, 176f)) drawLine(a.en(Offset(cx, y), C.HIERRO), Offset(PUERTA_X0 + 13f, y), Offset(PUERTA_X1, y), 1.8f)
        }
        if (k > 0.05f) drawLine(lerp(C.LAMPARA, Color.White, 0.3f).copy(alpha = 0.8f * k), Offset(PUERTA_X0 + 13f, PUERTA_ARR - r * 0.8f), Offset(PUERTA_X0 + 13f, E.suelo), 0.9f)
        // El arco de arenisca, con sus dovelas, y el escalón.
        drawPath(vano, a.en(Offset(cx, 120f), C.ARENISCA), style = Stroke(3f))
        dovelas(a, cx, PUERTA_ARR, r + 0.4f, r + 5.4f, 7)
        drawRect(a.en(Offset(cx, 195f), lerp(C.ARENISCA, C.CAL, 0.2f)), Offset(PUERTA_X0 - 3f, E.suelo - 2.4f), Size(PUERTA_X1 - PUERTA_X0 + 6f, 2.4f))
        // La cuña de luz que la lámpara tiende por el suelo, hacia quien mira.
        if (k > 0.05f) clipPath(E.formaDelSuelo()) {
            drawPath(poligono(Offset(PUERTA_X0 + 1f, E.suelo), Offset(PUERTA_X0 + 13f, E.suelo), Offset(318f, 252f), Offset(232f, 240f)), Brush.verticalGradient(0f to C.LAMPARA.copy(alpha = 0.34f * k), 1f to Color.Transparent, startY = E.suelo, endY = 252f), blendMode = BlendMode.Plus)
        }
        // Sobre la puerta, una hornacina en el grueso del muro: una cruz de madera pintada y la lamparilla, que aquí no se enciende.
        val nicho = Path().apply { moveTo(358f, 92f); lineTo(358f, 76f); cubicTo(358f, 67f, 380f, 67f, 380f, 76f); lineTo(380f, 92f); close() }
        drawPath(nicho, a.en(Offset(369f, 82f), lerp(C.ARENISCA, C.MADERA_OSC, 0.45f)))
        drawPath(nicho, a.en(Offset(369f, 82f), lerp(C.ROJO, C.ARENISCA, 0.5f)), style = Stroke(1.4f))
        drawLine(a.en(Offset(369f, 82f), C.AZUL), Offset(369f, 72f), Offset(369f, 91f), 2.2f); drawLine(a.en(Offset(369f, 80f), C.AZUL), Offset(363.6f, 78f), Offset(374.4f, 78f), 2.2f)
        drawLine(a.en(Offset(369f, 82f), C.ORO), Offset(369f, 73f), Offset(369f, 90f), 0.6f); drawLine(a.en(Offset(369f, 80f), C.ORO), Offset(364.4f, 78f), Offset(373.6f, 78f), 0.6f)
        drawRect(a.en(Offset(369f, 92f), lerp(C.ARENISCA, C.CAL, 0.4f)), Offset(356f, 92f), Size(26f, 2f)); drawOval(a.en(Offset(377f, 90f), C.LOSA), Offset(374.6f, 88.6f), Size(4.4f, 3.4f))
        // La percha de la entrada: el zurrón y el bordón de Odo, que no están cuando él está en Lorsch.
        val percha = Offset(344f, 112f)
        drawLine(a.en(percha, C.MADERA_OSC), Offset(341f, 110f), Offset(348f, 110f), 1.6f, StrokeCap.Round)
        if (!EscenaMedieval.enLorsch(m)) {
            drawLine(a.en(percha, C.MADERA), Offset(346.6f, 104f), Offset(345f, E.suelo - 1f), 1.5f, StrokeCap.Round)
            drawLine(a.en(percha, lerp(C.MADERA_OSC, C.TINTA, 0.3f)), Offset(343f, 110.6f), Offset(341.4f, 128f), 0.7f)
            val zurron = Path().apply { moveTo(337.6f, 128f); lineTo(345.4f, 128f); cubicTo(347f, 134f, 346f, 140f, 341.6f, 141f); cubicTo(337f, 140f, 336f, 134f, 337.6f, 128f); close() }
            drawPath(zurron, a.en(Offset(341f, 134f), lerp(C.MADERA, C.ARENISCA, 0.4f))); drawLine(a.en(Offset(341f, 131f), C.MADERA_OSC), Offset(337.4f, 131.4f), Offset(345.6f, 131.4f), 0.8f)
        }
    }

    // ---------------------------------------------------------------- La viga cercana y lo que cuelga de ella

    /**
     * La cuarta viga, la más cercana: enmarca el cuadro por arriba. De sus ganchos cuelgan un manojo de plumas de ganso
     * puestas a secar, un saquito de agallas de roble y, en fiesta, una rama verde. **[Diseño propio]**.
     */
    private fun DrawScope.vigaCercana(m: Momento, a: Alumbrado) {
        viga(a, 76f, alto = 9.5f, ancho = 10f)
        val y0 = p(0f, 49.5f, 76f).y
        // El manojo de plumas: los cañones arriba, atados, y las barbas abiertas hacia abajo.
        val nudo = Offset(308f, y0 + 9f)
        drawLine(a.en(nudo, C.HIERRO), Offset(308f, y0 - 1f), nudo, 0.8f)
        val az = Azar(Semilla.de("medieval", "plumas"))
        for (k in 0..6) {
            val ang = -0.5f + k * 0.17f + az.entre(-0.04f, 0.04f); val largo = az.entre(22f, 30f)
            val punta = nudo + Offset(sin(ang) * largo, cos(ang) * largo); val medio = nudo + Offset(sin(ang) * largo * 0.4f, cos(ang) * largo * 0.4f)
            val lado = Offset(cos(ang), -sin(ang)) * 2.6f
            drawPath(Path().apply { moveTo(medio); cubicTo(medio + lado, punta + lado * 0.9f, punta); cubicTo(punta - lado * 0.7f, medio - lado * 0.8f, medio); close() }, a.en(punta, if (k % 3 == 0) lerp(C.PERGAMINO, C.PIZARRA, 0.35f) else C.PERGAMINO, if (k < 3) -1f else 1f))
            drawLine(a.en(medio, lerp(C.PERGAMINO, C.MADERA, 0.4f)), nudo, punta, 0.5f)
        }
        drawCircle(a.en(nudo, C.ROJO), 1.6f, nudo)
        // El saquito de agallas.
        val saco = Offset(346f, y0 + 16f)
        drawLine(a.en(saco, C.HIERRO), Offset(346f, y0 - 1f), saco + Offset(0f, -6f), 0.8f)
        drawPath(Path().apply { moveTo(saco.x - 2f, saco.y - 6f); cubicTo(saco.x - 10f, saco.y + 2f, saco.x - 8f, saco.y + 12f, saco.x, saco.y + 12f); cubicTo(saco.x + 8f, saco.y + 12f, saco.x + 10f, saco.y + 2f, saco.x + 2f, saco.y - 6f); close() }, Brush.horizontalGradient(0f to a.en(saco, lerp(C.ARENISCA, C.CAL, 0.3f), -1f), 1f to a.en(saco, lerp(C.ARENISCA, C.MADERA_OSC, 0.4f), 1f), startX = saco.x - 9f, endX = saco.x + 9f))
        drawLine(a.en(saco, C.MADERA_OSC), saco + Offset(-2.6f, -5f), saco + Offset(2.6f, -5f), 1f)
        if (EscenaMedieval.fiesta(m) || EscenaMedieval.campanas(m)) {
            // En fiesta, ramas verdes atadas a la viga.
            val r = Azar(Semilla.de("medieval", "ramas"))
            for (x0 in listOf(120f, 196f)) for (k in 0..15) { val o = Offset(x0 + k * 4.4f + r.entre(-1f, 1f), y0 + 1f + r.entre(0f, 9f) * sin(k / 15f * PI.toFloat())); drawOval(a.en(o, lerp(C.VERDE, if (k % 4 == 0) C.ORO else C.TINTA, 0.2f)), o - Offset(4f, 1.9f), Size(8f, 3.8f)); if (k % 5 == 2) drawCircle(a.en(o, if (k % 2 == 0) Color.White else C.ROJO), 1.5f, o + Offset(0f, 2f)) }
        }
        if (EscenaMedieval.luto(m)) {
            // En luto, un paño negro cuelga de la viga.
            drawPath(poligono(Offset(196f, y0 - 1f), Offset(226f, y0 - 1f), Offset(224f, y0 + 20f), Offset(211f, y0 + 15f), Offset(198f, y0 + 20f)), a.en(Offset(211f, y0 + 8f), C.LANA))
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val k = EscenaMedieval.lampara(m)
        // La lámpara del dormitorio, que tiembla un poco tras la puerta.
        if (k > 0.1f) resplandor(LUZ_PUERTA + Offset(-8f, 4f), 16f + 1.6f * sin(reloj * 5.1f), 0.42f * k, C.LAMPARA)
        with(FueraMedieval) { vivo(m, reloj) }
    }
}
