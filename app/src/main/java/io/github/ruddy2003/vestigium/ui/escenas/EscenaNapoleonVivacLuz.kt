package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.VivacNapoleon.H
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin

/**
 * La luz del vivac, con la lógica de los cuadros aprobados (`ESCENAS.md`, sección 2 bis) y la del cuadro hermano, la
 * retirada (`EscenaNapoleonRetiradaLuz.kt`, de la que toma el sol con tope, la lumbre y la luna):
 *
 * - **Las sombras de las nubes sobre el campo** ([sombrasDeNube]), como el verano de Mongol: cada nube del cielo del día
 *   echa la suya en el llano, más grande y más cerca cuanto más alta está, corrida al lado contrario del sol; caminan con
 *   la hora. Dentro de ellas las cosas quedan sin sol (`VistaVivac.en`). **[Aproximación]**: no es la sombra real.
 * - **Las sombras del sol** de la tienda, de los pabellones, del asta y de lo del rancho, largas por la mañana y por la
 *   tarde y cortas a mediodía ([sombrasDelSuelo]); de noche, las de la luna.
 * - **La luz en el aire**: el polvo de la columna, dorado con el sol de la tarde (lo pinta el campo); «la última luz del
 *   día» en los cañones de los fusiles ([ultimaLuz]).
 * - **La noche a dos luces** ([lucesDelSuelo], [noche]): el fuego del rancho es el sol del cuadro (un círculo cálido y las
 *   sombras de lo que tiene cerca abiertas en abanico), contra la luna que platea el campo; y lejos, cientos de puntos de
 *   fuego. La ciudad que arde es una tercera luz, roja, que tiñe el cielo y el campo desde el horizonte.
 * - **La bruma por capas** ([velo]) cuando hay nubes bajas, lluvia, nieve o niebla: entre el horizonte, el campo y la base.
 */
internal object VivacLuz {
    private val C = ColorNapoleon

    /** Algo que tumba sombra: su [pie], su silueta en el lienzo y cuánta sombra da (la de un pabellón entero, menos que la de sus fusiles). */
    class Sombra(val pie: Offset, val forma: androidx.compose.ui.graphics.Path, val cuanta: Float = 1f)

    /**
     * El sol del vivac: el de `Rayo`, con la sombra no más larga que cinco veces y media lo alto **[Diseño propio]** (en la
     * retirada, tres): a primera hora y al atardecer las sombras de la fila de pabellones cruzan rasantes la hierba, y a
     * mediodía quedan cortas, al pie de cada cosa.
     */
    fun rayo(l: Luces): Rayo {
        val r = Rayo(l)
        if (r.dia <= 0.05f || l.solAltura <= 0f) return r
        // La elevación baja más deprisa que la de la biblioteca cerca del horizonte (de 3° a 58°, con la altura a la 1,6):
        // a las siete la sombra es cuatro o cinco veces lo alto; a las nueve, una; a mediodía, media.
        val elev = Math.toRadians(3.0 + 55.0 * (l.solAltura / 1.35f).coerceIn(0f, 1f).toDouble().pow(1.6))
        val cot = (1.0 / kotlin.math.tan(elev)).toFloat().coerceAtMost(5.5f)
        val bajo = (1f - l.solAltura / 0.6f).coerceIn(0f, 1f) * (1f - l.nublado).coerceIn(0f, 1f)
        return Rayo(r.dia, r.lat, cot, 1f / cot, -r.lat * cot, -0.33f * (1.3f - 0.6f * abs(r.lat)) * cot,
            (r.fuerza * (1f + 0.45f * bajo)).coerceAtMost(0.72f), r.color, r.contraste)
    }

    /**
     * Una capa de sombras arrojadas, como `capaSombra` de la biblioteca (lo que se pinta dentro oscurece una sola vez
     * aunque se encime), pero solo en la franja de [y0] a [y1]: las sombras del vivac caen todas en el campo, y una capa
     * del tamaño del cuadro entero cuesta el doble.
     */
    inline fun DrawScope.capaSombra(r: Rayo, cuanta: Float = 1f, y0: Float = H - 1f, y1: Float = ALTO, crossinline dibujo: DrawScope.(Color) -> Unit) {
        if (r.fuerza * cuanta <= 0.02f) return
        drawIntoCanvas { tela ->
            tela.saveLayer(Rect(-10f, y0, ANCHO + 10f, y1), Paint().apply { blendMode = BlendMode.Multiply; alpha = (r.fuerza * cuanta).coerceIn(0f, 1f) })
            dibujo(r.color)
            tela.restore()
        }
    }

    // ================================================================ Las sombras de las nubes

    /** Una nube del cielo del día: su borde izquierdo [x], su base [y] y su [ancho], como las pinta `nubes`. */
    internal class Nube(val x: Float, val y: Float, val ancho: Float)

    /** Lo que gasta del azar una `nube` de la biblioteca (`paisaje/Cielo.kt`): dos números y, en cada piso, uno más tres por borbotón. */
    private fun saltarNube(az: Azar) {
        az.real(); az.real()
        for (piso in 0..2) { val n = listOf(6, 4, 2)[piso] + az.entero(2); repeat(n * 3) { az.real() } }
    }

    /**
     * Las nubes de algodón que pinta hoy `nubes` en el cielo del vivac (con el horizonte que le da [VivacLejos]), con la
     * misma semilla y gastando el azar en el mismo orden **[Aproximación]**, como hace el verano de Mongol: si la
     * biblioteca cambia cómo gasta el azar, las sombras dejan de coincidir con sus nubes (siguen siendo las de un cielo de
     * ese día). Los velos, la calima y el cielo cubierto no dan sombras de nube.
     */
    internal fun nubesDelDia(m: Momento): List<Nube> {
        val az = Azar(Semilla.de(m.semilla("nubes"), "nubes")); val h = VivacLejos.CIELO; val lista = ArrayList<Nube>()
        fun nube(x: Float, y: Float, w: Float) { lista += Nube(x, y, w); saltarNube(az) }
        when (m.ficha.clima) {
            Clima.DESPEJADO -> when (az.entero(4)) {
                0 -> {
                    repeat(7) { val x = az.entre(-20f, ANCHO); val y = h * az.entre(0.80f, 0.90f); val w = az.entre(16f, 26f); nube(x, y, w) }
                    repeat(4) { val x = az.entre(-30f, ANCHO - 40f); val y = h * az.entre(0.54f, 0.68f); val w = az.entre(34f, 52f); nube(x, y, w) }
                    repeat(2) { val x = az.entre(44f, ANCHO - 150f); val y = h * az.entre(0.36f, 0.48f); val w = az.entre(64f, 92f); nube(x, y, w) }
                }
                1 -> {
                    var x = az.entre(-30f, 30f)
                    repeat(5) { val w = az.entre(48f, 86f); az.real(); nube(x, h + 3f, w); x += w * az.entre(0.5f, 0.8f) }
                    repeat(2) { val x2 = az.entre(60f, ANCHO - 160f); val y = h * az.entre(0.36f, 0.6f); val w = az.entre(30f, 48f); nube(x2, y, w) }
                }
                2 -> { repeat(7 * (4 + 17 * 2) + 2 + 46 * 4) { az.real() }; val x = az.entre(20f, 160f); nube(x, h * 0.82f, 30f) }
                else -> Unit
            }
            Clima.VIENTO -> { repeat(6 * (4 + 9)) { az.real() }; val x = az.entre(40f, ANCHO - 120f); nube(x, h * 0.82f, 50f) }
            Clima.NUBES -> {
                repeat(3) { val x = az.entre(-20f, ANCHO - 60f); val y = az.entre(h * 0.55f, h * 0.9f); val w = az.entre(46f, 80f); nube(x, y, w) }
                repeat(4) { val x = az.entre(-30f, ANCHO - 90f); val y = az.entre(h * 0.2f, h * 0.55f); val w = az.entre(90f, 150f); nube(x, y, w) }
            }
            else -> Unit
        }
        return lista
    }

    /** La sombra de una nube en el llano: su [centro], sus radios y la [semilla] de su borde. */
    internal class SombraDeNube(val centro: Offset, val rx: Float, val ry: Float, val semilla: Int)

    /** Hasta dónde bajan las sombras de las nubes: la base de delante queda fuera (la alumbra el sol, o una nube entera). */
    const val HASTA = 232f

    /**
     * Dónde caen hoy, a esta hora, las sombras de las nubes del día sobre el campo **[Aproximación]**: bajo cada nube,
     * más cerca y más grande cuanto más alta está en el cielo, y corrida al lado contrario del sol y hacia el fondo, más
     * cuanto más bajo está el sol: así caminan con la hora. Sin sol (de noche, al alba y al ocaso, cubierto), ninguna.
     */
    internal fun sombrasDeNube(m: Momento, l: Luces, r: Rayo): List<SombraDeNube> {
        if (r.fuerza < 0.12f) return emptyList()
        val ax = r.ax.coerceIn(-3f, 3f); val ay = r.ay.coerceIn(-3f, 0f)
        return nubesDelDia(m).mapIndexed { i, n ->
            val f = ((VivacLejos.CIELO - n.y) / VivacLejos.CIELO).coerceIn(0.03f, 1f); val g = f.pow(1.1f)
            val cx = n.x + n.ancho / 2f + ax * (10f + 60f * g)
            val cy = (H + 3f + 120f * g + ay * (2f + 14f * g)).coerceAtLeast(H + 2f)
            val rx = (n.ancho * (0.5f + 1.1f * g) * (1f + 0.15f * abs(ax))).coerceAtMost(58f)
            SombraDeNube(Offset(cx, cy), rx, (rx * (0.06f + 0.22f * g)).coerceAtMost(11f), i)
        }.filter { it.centro.x + it.rx > -10f && it.centro.x - it.rx < ANCHO + 10f && it.centro.y - it.ry < HASTA }
    }

    /** Cuánto está en la sombra de una nube el punto [q] del campo, de 0 a 1. */
    internal fun enSombraDeNube(sombras: List<SombraDeNube>, q: Offset): Float {
        if (q.y > HASTA + 20f) return 0f
        var k = 0f
        for (s in sombras) { val dx = (q.x - s.centro.x) / s.rx; val dy = (q.y - s.centro.y) / s.ry; k = maxOf(k, (1.3f - 1.3f * (dx * dx + dy * dy)).coerceIn(0f, 1f)) }
        return k
    }

    /** Pinta las sombras de las nubes en una capa de sombra (una sola, aunque se encimen): masas blandas con borde de nube. */
    private fun DrawScope.sombrasDeNubes(v: VistaVivac) {
        val sombras = v.nubes; if (sombras.isEmpty()) return
        val r = v.r; val l = v.l
        // Los días de cúmulos, el sol que se cuela entre las nubes enciende el campo que queda fuera de sus sombras.
        if (l.nublado > 0.2f) drawRect(Brush.verticalGradient(0f to Color.Transparent, 0.3f to l.solColor.copy(alpha = 0.1f * l.nublado), 0.8f to l.solColor.copy(alpha = 0.06f * l.nublado), 1f to Color.Transparent, startY = H, endY = HASTA),
            Offset(-10f, H), Size(ANCHO + 20f, HASTA - H), blendMode = BlendMode.Plus)
        val alto = (l.solAltura / 0.9f).coerceIn(0f, 1f)
        val honda = 0.26f + 0.24f * (r.fuerza / 0.72f).coerceIn(0f, 1f) + 0.22f * alto + 0.14f * l.nublado
        capaSombra(r, honda / r.fuerza.coerceAtLeast(0.05f), H - 1f, HASTA + 10f) { c ->
            clipRect(-10f, H - 1f, ANCHO + 10f, HASTA + 10f) {
                for (s in sombras) {
                    val az = Azar(Semilla.de(v.m.semilla("nubes"), "sombra", s.semilla))
                    repeat(6) {
                        val q = s.centro + Offset(az.entre(-0.55f, 0.55f) * s.rx, az.entre(-0.3f, 0.3f) * s.ry)
                        val baja = (1f - (q.y - 200f) / (HASTA + 10f - 200f)).coerceIn(0f, 1f)
                        mancha(q, s.rx * az.entre(0.4f, 0.6f), s.ry * az.entre(0.7f, 1.05f), c, baja)
                    }
                }
            }
        }
    }

    // ================================================================ Las sombras del suelo

    /** Con qué se tumban las sombras ahora: el sol si lo hay; si no, la luna; si tampoco, nada. */
    fun deSombras(v: VistaVivac): Rayo? = if (v.r.fuerza > 0.05f) v.r else v.rLuna

    /**
     * Las sombras del suelo: las de las nubes (su capa) y, en otra capa, la sombra de cada cosa de la base y del campo con
     * el sol, o con la luna de noche. Antes, de noche, la luna platea el campo.
     */
    fun DrawScope.sombrasDelSuelo(v: VistaVivac) {
        sombrasDeNubes(v)
        if (v.lugar == EscenaNapoleon.Lugar.PUEBLO) with(VivacParra) { sombra(v) }
        if (v.luna > 0.02f) plata(v)
        val r = deSombras(v) ?: return
        val cosas = VivacCerca.sombras(v) + VivacCampo.sombras(v)
        capaSombra(r, 1.3f) { c -> for (s in cosas) sombraArrojada(r, s.forma, s.pie, c.copy(alpha = c.alpha * s.cuanta), H, H) }
    }

    /** El campo plateado por la luna: más del lado de la luna, y más con nieve. */
    private fun DrawScope.plata(v: VistaVivac) {
        val k = v.luna * (0.3f + 0.5f * v.nieve); if (k <= 0.02f) return
        val derecha = v.l.lunaX > 0.5f
        val cerca = RetiradaLuz.LUNA.copy(alpha = (0.26f * k).coerceIn(0f, 1f)); val lejos = RetiradaLuz.LUNA.copy(alpha = (0.1f * k).coerceIn(0f, 1f))
        drawRect(Brush.horizontalGradient(0f to (if (derecha) lejos else cerca), 1f to (if (derecha) cerca else lejos), startX = 0f, endX = ANCHO), Offset(-10f, H), Size(ANCHO + 20f, ALTO - H + 10f), blendMode = BlendMode.Plus)
    }

    // ================================================================ La luz del fuego

    /** El fuego del rancho como foco. */
    fun foco(v: VistaVivac): Foco = Foco(VivacCerca.LUZ_DEL_FUEGO, C.LUMBRE, v.lumbre, 104f, suelo = VivacCerca.FUEGO.y)

    /** Cuánto manda el fuego sobre el sol y la luna, de 0 a 1. */
    fun mandaElFuego(v: VistaVivac): Float = (v.lumbre * 1.5f - v.r.fuerza).coerceIn(0f, 1f)

    /**
     * Lo que el fuego del rancho deja en el suelo de noche (y al caer la tarde, o cubierto): el círculo cálido y las sombras
     * de lo que tiene cerca abiertas en abanico, lejos de él; y el trapecio de luz del farol delante de la tienda.
     */
    fun DrawScope.lucesDelSuelo(v: VistaVivac) {
        val manda = mandaElFuego(v); val f = foco(v)
        if (manda > 0.02f) {
            val cosas = VivacCerca.sombrasDelFuego(v)
            val rf = rayoFijo(1f, 30f, 0.66f * manda, C.SOMBRA)
            capaSombra(rf) { c ->
                for (s in cosas) {
                    val d = (s.pie - f.p).getDistance(); val kk = (1f / (1f + d * d / (150f * 150f))).coerceIn(0f, 1f)
                    sombraArrojada(rayoDeFoco(f, s.pie, 1f, c), s.forma, s.pie, c.copy(alpha = kk * s.cuanta), H, H)
                }
            }
        }
        val k = v.lumbre * (0.3f + 0.7f * v.l.noche)
        if (k > 0.02f) {
            val suelo = VivacCampo.colorDelCampo(v, 0f)
            charcoDeLuz(f, VivacCerca.FUEGO + Offset(-24f, 8f), 170f * (0.55f + 0.45f * v.fuego), 44f * (0.55f + 0.45f * v.fuego), suelo, 0.62f * (0.3f + 0.7f * v.l.noche))
            charcoDeLuz(f, VivacCerca.FUEGO, 50f, 15f, suelo, 0.38f * (0.4f + 0.6f * v.l.noche))
        }
        // El farol de la tienda deja su luz en el suelo delante de la puerta.
        val farol = VivacNapoleon.farol(v.m)
        if (farol > 0.02f && v.lugar != EscenaNapoleon.Lugar.PUEBLO) {
            val s = VivacCerca.S_TIENDA; val q = s.p(V3(0.5f, 0f, -1.7f - 0.9f))
            charcoDeLuz(Foco(q + Offset(0f, -30f), C.LUMBRE, farol, 60f, q.y), q, 1.5f * s.k, 0.42f * s.k, VivacCampo.colorDelCampo(v, 0f), 0.75f)
        }
    }

    // ================================================================ El relente de la mañana

    /**
     * Cuánto relente hay sobre el campo, de 0 a 1: la bruma baja del amanecer, de 4:30 a 9:30, más espesa hacia las seis
     * y media, en los días sin lluvia ni nieve **[Diseño propio]**.
     */
    fun relente(m: Momento, l: Luces): Float {
        val h = m.hora.toFloat(); if (h < 4.5f || h > 9.5f) return 0f
        when (m.ficha.clima) { Clima.LLUVIA, Clima.TORMENTA, Clima.NIEVE, Clima.POLVO -> return 0f; else -> Unit }
        val k = if (h < 6.5f) (h - 4.5f) / 2f else 1f - (h - 6.5f) / 3f
        return (k * (1f - 0.5f * l.nublado)).coerceIn(0f, 1f)
    }

    /**
     * El relente: bandas de bruma baja tendidas sobre el campo, entre los campamentos y la lejanía, que el sol bajo
     * atraviesa y dora del lado del sol (la luz que se ve en el aire). Va después de los campamentos y antes de la base.
     */
    fun DrawScope.relente(v: VistaVivac) {
        val k = relente(v.m, v.l); if (k <= 0.02f) return
        val az = VivacNapoleon.fijo("relente"); val sol = v.l.solX * ANCHO
        val fria = lerp(v.l.bruma, Color.White, 0.25f); val dorada = lerp(v.l.solColor, Color(0xFFFFE2A8), 0.45f)
        for (i in 0 until 9) {
            val y = az.entre(126f, 196f); val x = az.entre(-20f, ANCHO + 20f); val ancho = az.entre(60f, 150f) * (0.6f + (y - 120f) / 80f)
            val cerca = (1f - kotlin.math.abs(x - sol) / 260f).coerceIn(0f, 1f)
            mancha(Offset(x, y), ancho, az.entre(3f, 7f) * (0.6f + (y - 120f) / 90f), lerp(fria, dorada, 0.25f + 0.6f * cerca * (v.r.fuerza / 0.72f).coerceIn(0f, 1f)), 0.42f * k)
        }
        // Donde el sol entra en la bruma, el resplandor dorado.
        if (v.r.fuerza > 0.05f) mancha(Offset(sol, 140f), 130f, 22f, dorada, 0.22f * k * (v.r.fuerza / 0.72f).coerceIn(0f, 1f))
    }

    // ================================================================ La noche

    /**
     * Lo último de la noche: los rincones de abajo, lejos del fuego, se hunden en azul (el campo de lejos no, que lo
     * alumbran sus fuegos y la luna); y la ciudad que arde tiñe de rojo el cielo bajo y el campo desde el horizonte.
     */
    fun DrawScope.noche(v: VistaVivac) {
        val n = v.l.noche
        if (n > 0.3f) {
            val hondo = lerp(Color.White, Color(0xFF6A6CA8), 0.42f * (n - 0.3f) / 0.7f)
            drawRect(Brush.radialGradient(0f to Color.White, 0.5f to Color.White, 1f to hondo, center = VivacCerca.LUZ_DEL_FUEGO + Offset(-30f, 40f), radius = 320f), Offset(0f, H + 30f), Size(ANCHO, ALTO - H - 30f), blendMode = BlendMode.Multiply)
        }
        val k = v.incendio * (0.35f + 0.65f * n)
        if (k > 0.02f) {
            val c = VivacLejos.ARDE
            drawRect(Brush.radialGradient(listOf(Color(0xFFFF7A3A).copy(alpha = (0.32f * k).coerceIn(0f, 1f)), Color(0xFFE0502A).copy(alpha = (0.12f * k).coerceIn(0f, 1f)), Color.Transparent), c, 300f), Offset.Zero, Size(ANCHO, ALTO), blendMode = BlendMode.Plus)
        }
    }

    // ================================================================ La bruma por capas

    /**
     * El aire entre los planos cuando hay niebla, nieve, lluvia o nubes: una banda de [y0] a [y1], más espesa abajo, que
     * despega cada plano del de atrás aunque no haya sombras (como en Horror cósmico).
     */
    fun DrawScope.velo(v: VistaVivac, cuanto: Float, y0: Float, y1: Float) {
        val k = when (v.m.ficha.clima) { Clima.NIEVE -> 0.85f; Clima.NIEBLA -> 1f; Clima.NUBES -> 0.3f; Clima.LLUVIA, Clima.TORMENTA -> 0.45f; else -> 0f } * cuanto
        if (k <= 0.01f) return
        val color = lerp(v.l.bruma, v.l.bajo, 0.35f)
        drawRect(Brush.verticalGradient(0f to color.copy(alpha = 0f), 0.35f to color.copy(alpha = 0.55f * k), 0.62f to color.copy(alpha = k), 0.8f to color.copy(alpha = 0.6f * k), 1f to color.copy(alpha = 0f), startY = y0, endY = y1), Offset(0f, y0), Size(ANCHO, y1 - y0))
    }

    // ================================================================ La luz en el aire

    /**
     * «La última luz del día se queda en los cañones de los fusiles puestos en pabellón, que brillan rojos un momento»:
     * de 0 a 1, con el sol muy bajo de la tarde (o de la mañana), limpio.
     */
    fun ultimaLuz(l: Luces): Float {
        if (l.solAltura <= 0f || l.solFuerza < 0.2f) return 0f
        return ((0.32f - l.solAltura) / 0.24f).coerceIn(0f, 1f) * (1f - 1.4f * l.nublado).coerceIn(0f, 1f) * (if (l.solX < 0.5f) 1f else 0.5f)
    }

    /** Los copos que caen, con el viento (encima de la lluvia de la biblioteca, que no pinta nieve aquí). */
    fun DrawScope.copos(v: VistaVivac, reloj: Float) {
        val az = Azar(Semilla.de("napoleon", "vivac", "copos"))
        val c = lerp(v.l.tono(C.NIEVE), Color.White, 0.4f)
        repeat(26) {
            val y0 = az.entre(-20f, ALTO); val x0 = az.entre(0f, ANCHO); val rap = az.entre(10f, 22f); val r = az.entre(0.6f, 1.5f) * (0.6f + y0 / ALTO)
            val y = (y0 + reloj * rap) % (ALTO + 20f) - 10f; val x = x0 + 6f * sin(reloj * 0.8f + y0)
            drawCircle(c.copy(alpha = 0.65f), r, Offset(x, y))
        }
    }

}
