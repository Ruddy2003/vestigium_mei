package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaUrss.SolDeVentana
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.haloDeBruma
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.sin

/**
 * La luz de la cocina, que es el cuadro:
 * - **De día**, el sol (o la luna, de noche) que entra por la ventana deja **su mancha con la cruz del marco** en la mesa,
 *   el hule, el linóleo y las paredes ([manchas]); la mancha camina con la hora ([cae], [centroDeLaMancha]) y enciende lo
 *   que toca: los cuadros del hule, las losetas y la alfombrilla suben de color dentro de ella ([enElSuelo],
 *   [enLaMesa]). En el aire, el haz con su polvo y el vaho del té ([enElAire], [vivo]). Las cosas que pisan dentro de la
 *   mancha arrojan su sombra hacia quien mira ([rayoDelSol]).
 * - **De noche, a dos luces y media:** la lámpara naranja con su cono sobre la mesa, su halo y las sombras de las cosas
 *   que tumba; fuera, el azul de la nieve y de la luna, que también entra por la ventana con su mancha fría; y el
 *   parpadeo azul del televisor que sale por la puerta del cuarto. El cristal devuelve la lámpara ([CajaUrss]).
 *
 * **[Aproximación]**, la misma de la capilla: la ventana da al mismo cielo que pintan las luces y la mancha cae como si
 * la ventana mirara al sol. Lo que hay en medio (el fogón, la nevera) no corta la mancha; la mesa sí.
 */
internal object LuzCocinaUrss {
    private val C = ColorUrss
    private val R get() = EscenaUrss
    private val Co get() = CocinaUrss
    private val K get() = CocinaUrss.K

    // ---------------------------------------------------------------- Dónde cae el rayo

    /** En qué cosa cae un rayo de sol que entra por la ventana. */
    internal enum class Plano { MESA, SUELO, IZQUIERDA, DERECHA }

    /** Dónde cae un rayo: en qué [plano] y en qué punto de la cocina ([x], [y], [t]). */
    internal class Caida(val plano: Plano, val x: Float, val y: Float, val t: Float) {
        val punto: Offset get() = CocinaUrss.K.p(x, y, t)
    }

    /** Dónde cae el rayo que entra por el punto ([x], [y]) de la ventana: en la mesa, en el suelo o en una pared, lo que encuentre antes. */
    internal fun cae(x: Float, y: Float, s: SolDeVentana): Caida {
        var mejor = Caida(Plano.SUELO, x + s.corre * (Co.SUELO - y) / s.baja, Co.SUELO, (Co.SUELO - y) / s.baja)
        if (y < Co.MY) {
            val d = (Co.MY - y) / s.baja; val xm = x + s.corre * d
            if (xm in Co.MX0..Co.MX1 && d in Co.MT0..Co.MT1) mejor = Caida(Plano.MESA, xm, Co.MY, d)
        }
        if (s.corre < -0.02f) { val d = (x - K.x0) / -s.corre; if (d < mejor.t) mejor = Caida(Plano.IZQUIERDA, K.x0, y + s.baja * d, d) }
        if (s.corre > 0.02f) { val d = (K.x1 - x) / s.corre; if (d < mejor.t) mejor = Caida(Plano.DERECHA, K.x1, y + s.baja * d, d) }
        return mejor
    }

    /**
     * El centro de la mancha que se ve: donde cae el rayo que entra por el centro de la ventana, junto al alféizar (lo que
     * entra más arriba cae, con el sol bajo, detrás de quien mira). De ahí rebota su claridad.
     */
    internal fun centroDeLaMancha(s: SolDeVentana): Offset = cae((Co.VX0 + Co.VX1) / 2f, Co.VY1 - 10f, s).punto

    /** Un punto de la ventana llevado por el rayo hasta el plano [plano] (aunque quede fuera de él: luego se recorta). */
    private fun lleva(x: Float, y: Float, s: SolDeVentana, plano: Plano): Offset {
        val d = when (plano) {
            Plano.SUELO -> (Co.SUELO - y) / s.baja
            Plano.MESA -> (Co.MY - y) / s.baja
            Plano.IZQUIERDA -> (x - K.x0) / -s.corre
            Plano.DERECHA -> (K.x1 - x) / s.corre
        }.coerceIn(0f, 600f)
        return when (plano) {
            Plano.SUELO -> K.p(x + s.corre * d, Co.SUELO, d)
            Plano.MESA -> K.p(x + s.corre * d, Co.MY, d)
            Plano.IZQUIERDA -> K.p(K.x0, y + s.baja * d, d)
            Plano.DERECHA -> K.p(K.x1, y + s.baja * d, d)
        }
    }

    /** Las manchas de los cristales en el plano [plano]: cada cristal, llevado por el rayo. */
    internal fun manchas(m: Momento, s: SolDeVentana, plano: Plano): Path {
        val p = Path()
        if (plano == Plano.IZQUIERDA && s.corre > -0.05f) return p
        if (plano == Plano.DERECHA && s.corre < 0.05f) return p
        for (c in Co.cristales(R.ventanaAbierta(m))) p.addPath(poligono(c.map { (x, y) -> lleva(x, y, s, plano) }))
        return p
    }

    /** La sombra de la tapa de la mesa en el suelo: por ahí no llega el sol. */
    private fun sombraDeLaMesa(s: SolDeVentana): Path {
        val d = (Co.SUELO - Co.MY) / s.baja
        fun q(x: Float, t: Float) = K.p(x + s.corre * d, Co.SUELO, t + d)
        return poligono(q(Co.MX0 - 2f, Co.MT0), q(Co.MX1, Co.MT0), q(Co.MX1, Co.MT1 + 3f), q(Co.MX0 - 2f, Co.MT1 + 3f))
    }

    /**
     * El [Rayo] con que el sol que entra tumba la sombra de lo que pisa en [pie] (en el suelo o en la mesa): hacia quien
     * mira, larga con el sol bajo y corta a mediodía, y hacia el lado contrario al sol. Sale de llevar la punta de cada
     * cosa por el rayo hasta el plano en que pisa.
     */
    internal fun rayoDelSol(s: SolDeVentana, fuerza: Float, pie: Offset = Offset(200f, 250f)): Rayo {
        // Una punta a la altura H sobre el plano cae H / baja más cerca de quien mira y H · corre / baja más a un lado; en el
        // cuadro, eso es (lo que se corre) / (lo que mide la cosa en el cuadro):
        val ax = (s.corre / s.baja + (pie.x - K.fuga.x) / (K.ojo * s.baja)).coerceIn(-2.6f, 2.6f)
        val ay = ((pie.y - K.fuga.y) / (K.ojo * s.baja)).coerceIn(0.05f, 2.2f)
        return Rayo(dia = 1f, lat = if (s.corre < 0f) 1f else -1f, cot = 1f / s.baja, tan = s.baja, ax = ax, ay = ay, fuerza = ((if (s.deLuna) 0.45f else 0.7f) * s.fuerza * fuerza).coerceIn(0f, 1f), color = C.SOMBRA, contraste = 0.8f)
    }

    /** El color de algo donde le da la mancha: sube y se calienta (de luna, se enfría y platea). */
    internal fun encendido(lz: LuzUrss, s: SolDeVentana, c: Color, q: Offset, k: Float = 1f): Color {
        val base = lz.en(q, c)
        // De luna, la luz fría se suma a lo que ya hay (también a la de la lámpara): plata sobre naranja.
        if (s.deLuna) { val f = 0.42f * s.fuerza * k; return Color((base.red + c.red * s.color.red * f).coerceIn(0f, 1f), (base.green + c.green * s.color.green * f).coerceIn(0f, 1f), (base.blue + (c.blue * 0.6f + 0.4f) * s.color.blue * f).coerceIn(0f, 1f)) }
        // Al sol, el color propio con la luz cálida: más claro y más vivo que en la penumbra, sin llegar al blanco.
        val luz = Color((c.red * s.color.red * 1.02f).coerceIn(0f, 1f), (c.green * s.color.green * 0.95f).coerceIn(0f, 1f), (c.blue * s.color.blue * 0.82f).coerceIn(0f, 1f))
        return lerp(base, luz, (0.88f * s.fuerza * k).coerceIn(0f, 1f))
    }

    /** El color de algo donde le da la lámpara de lleno: el naranja de la pantalla, con lo pintado encendido. */
    internal fun alaLampara(lz: LuzUrss, c: Color, q: Offset, k: Float): Color {
        val base = lz.en(q, c)
        val luz = Color((c.red * 1.04f).coerceIn(0f, 1f), (c.green * 0.86f).coerceIn(0f, 1f), (c.blue * 0.6f).coerceIn(0f, 1f))
        return lerp(base, luz, k.coerceIn(0f, 1f))
    }

    /** El borde blando de una mancha y su claridad encima. */
    private fun DrawScope.brillo(forma: Path, s: SolDeVentana, k: Float) {
        val color = if (s.deLuna) s.color else lerp(s.color, Color(0xFFFFD49A), 0.45f)
        for (ancho in listOf(6f, 3.4f, 1.6f)) drawPath(forma, color.copy(alpha = (0.03f * k * s.fuerza).coerceIn(0f, 1f)), style = Stroke(ancho), blendMode = BlendMode.Plus)
        drawPath(forma, color.copy(alpha = (0.06f * k * s.fuerza).coerceIn(0f, 1f)), blendMode = BlendMode.Plus)
    }

    // ---------------------------------------------------------------- En el suelo y en las paredes

    /**
     * La mancha en el suelo (sin la sombra de la mesa) y en las paredes de los lados, con lo pintado encendido dentro:
     * las losetas y la alfombrilla. Va después del suelo y antes de las cosas.
     */
    fun DrawScope.enElSuelo(m: Momento, lz: LuzUrss) {
        val s = lz.sol ?: return
        val enSuelo = manchas(m, s, Plano.SUELO)
        clipPath(K.formaDelSuelo()) {
            clipPath(sombraDeLaMesa(s), clipOp = ClipOp.Difference) {
                clipPath(enSuelo) {
                    with(CajaUrss) { dibujoDelLinoleo(lz) { c, q -> encendido(lz, s, c, q) }; alfombrilla(lz) { c, q -> encendido(lz, s, c, q) } }
                }
                brillo(enSuelo, s, 1f)
            }
        }
        for ((plano, forma) in listOf(Plano.IZQUIERDA to K.formaIzquierda(), Plano.DERECHA to K.formaDerecha())) {
            val pared = manchas(m, s, plano)
            clipPath(forma) { clipPath(pared) { drawRect(lerp(s.color, C.PARED, 0.4f).copy(alpha = 0.22f * s.fuerza), Offset.Zero, Size(ANCHO, ALTO), blendMode = BlendMode.Plus) }; brillo(pared, s, 0.6f) }
        }
        // La claridad que la mancha devuelve alrededor.
        mancha(centroDeLaMancha(s), 60f, 24f, s.color, 0.1f * s.fuerza)
    }

    /** La mancha en la mesa: el hule se enciende dentro, con la cruz del marco. Va justo después de la mesa. */
    fun DrawScope.enLaMesa(m: Momento, lz: LuzUrss) {
        val s = lz.sol ?: return
        val enMesa = manchas(m, s, Plano.MESA)
        clipPath(PiezasUrss.tapa(Co.MX0, Co.MX1, Co.MT0, Co.MT1, Co.MY)) {
            clipPath(enMesa) {
                drawRect(encendido(lz, s, C.PAPEL, Co.SOBRE_LA_MESA), Offset.Zero, Size(ANCHO, ALTO))
                with(PiezasUrss) { dibujoDelHule(Co.MX0, Co.MX1, Co.MT0, Co.MT1, Co.MY) { c, q -> encendido(lz, s, c, q) } }
            }
            brillo(enMesa, s, 1.2f)
        }
    }

    /** Lo que la mancha de la mesa deja ver: su forma, para recortar las sombras de lo que hay encima. */
    internal fun manchaEnLaMesa(m: Momento, s: SolDeVentana): Path = manchas(m, s, Plano.MESA)
    internal fun manchaEnElSuelo(m: Momento, s: SolDeVentana): Path = manchas(m, s, Plano.SUELO)

    // ---------------------------------------------------------------- La lámpara, de noche

    /** El cono de la lámpara sobre la mesa, su halo en la pared y en el aire, y su charco en el suelo. Va con las cosas de la mesa. */
    fun DrawScope.conoDeLaLampara(m: Momento, lz: LuzUrss) {
        if (!lz.lampara) return
        val f = lz.a.focos.firstOrNull { it.p == Co.BOMBILLA } ?: return
        val k = (0.45f + 0.55f * lz.noche)
        clipPath(PiezasUrss.tapa(Co.MX0, Co.MX1, Co.MT0, Co.MT1, Co.MY)) {
            // El círculo de la lámpara en el hule: dentro, los cuadros se encienden de naranja; el borde, blando.
            for ((rx, ry, a) in listOf(Triple(78f, 22f, 0.45f), Triple(58f, 16f, 0.75f), Triple(40f, 11f, 1f))) {
                val c = Co.BAJO_LA_LAMPARA + Offset(-6f, -2f)
                val circulo = Path().apply { addOval(Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry)) }
                clipPath(circulo) {
                    drawRect(alaLampara(lz, C.PAPEL, c, 0.85f * a * k), Offset.Zero, Size(ANCHO, ALTO))
                    with(PiezasUrss) { dibujoDelHule(Co.MX0, Co.MX1, Co.MT0, Co.MT1, Co.MY) { col, q -> alaLampara(lz, col, q, 0.85f * a * k) } }
                }
            }
            charcoDeLuz(f, Co.BAJO_LA_LAMPARA + Offset(-6f, -2f), 60f, 15f, C.PAPEL, 0.5f)
        }
        clipPath(K.formaDelSuelo()) { charcoDeLuz(f, K.p(Co.LX - 40f, Co.SUELO, Co.LT + 12f), 90f, 18f, C.LINOLEO, 0.7f) }
    }

    // ---------------------------------------------------------------- En el aire

    /**
     * El haz de sol en el aire, de los cristales a donde cae, con su polvo; el halo de la lámpara; la claridad azul que
     * sale de la puerta del cuarto con la tele encendida. Va después de las cosas.
     */
    fun DrawScope.enElAire(m: Momento, lz: LuzUrss) {
        lz.sol?.let { s ->
            val color = if (s.deLuna) s.color else lerp(s.color, Color(0xFFFFD49A), 0.35f)
            for (c in Co.cristales(R.ventanaAbierta(m))) for (i in c.indices) {
                val (x0, y0) = c[i]; val (x1, y1) = c[(i + 1) % c.size]
                drawPath(poligono(K.p(x0, y0, 0f), K.p(x1, y1, 0f), cae(x1, y1, s).punto, cae(x0, y0, s).punto), color.copy(alpha = 0.03f * s.fuerza), blendMode = BlendMode.Plus)
            }
            val az = Azar(Semilla.de("urss", "polvo"))
            repeat(46) {
                val x = az.entre(Co.VX0 + 6f, Co.VX1 - 6f); val y = az.entre(Co.VY0 + 6f, Co.VY1 - 6f); val u = az.entre(0.06f, 0.9f)
                val a = K.p(x, y, 0f); val b = cae(x, y, s).punto
                drawCircle(color.copy(alpha = (0.55f * s.fuerza * az.real()).coerceIn(0f, 1f)), az.entre(0.3f, 0.7f), a + (b - a) * u)
            }
        }
        if (lz.lampara) {
            val b = Co.BOMBILLA
            mancha(b + Offset(0f, 14f), 46f, 30f, C.LAMPARA, 0.03f + 0.06f * lz.noche)
            haloDeBruma(b + Offset(0f, 10f), 30f, 18f, C.LAMPARA, 0.3f + 0.4f * lz.noche)
            // El cono de luz en el aire, de la boca de la pantalla a la mesa: muy tenue.
            val a0 = b + Offset(-34f, 8f); val a1 = b + Offset(34f, 8f)
            drawPath(poligono(a0, a1, Co.BAJO_LA_LAMPARA + Offset(70f, 6f), Co.BAJO_LA_LAMPARA + Offset(-86f, 6f)), C.LAMPARA.copy(alpha = 0.018f * lz.noche), blendMode = BlendMode.Plus)
        }
        if (R.televisor(m)) {
            // La claridad azul de la puerta del cuarto, en el suelo y en el marco (su parpadeo va en la parte viva).
            val q = K.p(K.x1, Co.SUELO, 40f)
            mancha(q + Offset(-6f, -4f), 30f, 9f, C.TELE, 0.12f * (0.4f + lz.noche))
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    /**
     * Lo que se mueve en la cocina: el vaho del samovar y del hervidor (que se enciende si pasa por el haz de sol o por
     * la luz de la lámpara), la llama azul del gas y el parpadeo de la tele en el suelo y en la puerta.
     */
    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val l = R.luces(m); val s = R.solQueEntra(m); val lampara = R.lampara(m)
        val tinte = when { s != null && !s.deLuna -> lerp(Color.White, s.color, 0.4f); lampara -> lerp(C.LAMPARA, Color.White, 0.5f); else -> lerp(C.PAPEL, C.LUNA, 0.5f) }
        // El vaho: del samovar (la tetera de encima) y del pico del hervidor.
        val fuentes = buildList {
            if (R.samovar(m)) add(CosasUrss.VAHO_SAMOVAR)
            if (R.fogon(m)) add(CosasUrss.VAHO_HERVIDOR)
        }
        for ((n, f) in fuentes.withIndex()) {
            repeat(9) { i ->
                val fase = ((reloj * 0.22f + i / 9f + n * 0.37f) % 1f)
                val y = f.y - fase * 46f; val x = f.x + sin(reloj * 0.9f + i * 1.7f + n) * (3f + 9f * fase) + fase * 6f
                val a = (0.22f * (1f - fase) * (fase * 6f).coerceAtMost(1f)).coerceIn(0f, 1f)
                mancha(Offset(x, y), 3f + 9f * fase, 2.6f + 7f * fase, tinte, a)
            }
        }
        // La llama azul del gas, que tiembla.
        if (R.fogon(m)) {
            val q = CocinaUrss.LUZ_GAS; val k = K.s(22f)
            for (j in 0..11) { val ang = j * 0.5236f; val alto = (2.4f + 0.8f * sin(reloj * 9f + j * 1.3f)) * k; drawLine(lerp(C.AZUL, Color(0xFFBFD8FF), 0.5f).copy(alpha = 0.8f), q + Offset(kotlin.math.cos(ang) * 2.6f * k, sin(ang) * 0.8f * k), q + Offset(kotlin.math.cos(ang) * 3.2f * k, sin(ang) * 0.9f * k - alto), 0.7f, StrokeCap.Round) }
        }
        // El parpadeo azul de la tele: la claridad del hueco de la puerta y la que cae en el suelo de la cocina.
        if (R.televisor(m)) {
            val k = (0.65f + 0.2f * sin(reloj * 3.1f) + 0.15f * sin(reloj * 7.7f + 1f) + (if (sin(reloj * 0.9f) > 0.85f) 0.25f else 0f)).coerceIn(0.3f, 1.2f)
            val hueco = CosasUrss.huecoDeLaPuerta()
            drawPath(hueco, C.TELE.copy(alpha = (0.1f * k).coerceIn(0f, 1f)), blendMode = BlendMode.Plus)
            val suelo = poligono(K.p(K.x1, Co.SUELO, Co.PT0 + 2f), K.p(K.x1, Co.SUELO, Co.PT1), K.p(K.x1 - 46f, Co.SUELO, Co.PT1 + 20f), K.p(K.x1 - 40f, Co.SUELO, Co.PT0 + 6f))
            drawPath(suelo, C.TELE.copy(alpha = (0.07f * k * (0.5f + l.noche)).coerceIn(0f, 1f)), blendMode = BlendMode.Plus)
        }
        // La pelusa de los álamos que entra por la ventana abierta.
        if (R.pelusa(m) > 0.1f && R.ventanaAbierta(m)) {
            val az = m.azar("pelusa dentro")
            repeat(8) { val x = (az.entre(150f, 300f) + reloj * az.entre(4f, 10f)) % 380f; val y = az.entre(60f, 200f) + sin(reloj * 0.7f + it) * 8f; drawCircle(C.PAPEL.copy(alpha = 0.7f), 0.9f, Offset(x, y)) }
        }
    }

    /** La pelusa de los álamos que flota fuera, en junio: copos blancos que van de lado, despacio. */
    fun DrawScope.pelusaQueFlota(m: Momento, reloj: Float, zona: Rect) {
        val az = m.azar("pelusa"); val cuanta = R.pelusa(m)
        repeat((30 * cuanta).toInt() + 4) {
            val rapidez = az.entre(5f, 14f); val x = zona.left + ((az.entre(0f, zona.width) + reloj * rapidez) % zona.width)
            val y = zona.top + ((az.entre(0f, zona.height) + reloj * az.entre(1f, 4f) + sin(reloj * 0.8f + it) * 4f) % zona.height)
            drawCircle(Color(0xFFF6F2E8).copy(alpha = az.entre(0.4f, 0.85f)), az.entre(0.5f, 1.1f), Offset(x, y))
        }
    }

    /** Si [a] y [b] son casi el mismo número (para las pruebas). */
    internal fun cerca(a: Float, b: Float, tol: Float = 0.5f) = abs(a - b) < tol
}
