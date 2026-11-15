package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.pow

/**
 * La luz del campamento de verano ([VeranoMongol]), en la ronda de luz de la tanda S12 (`revision_3_luz.md`): la lógica
 * de los interiores aprobados (la capilla, Scriptorium, Renacimiento) llevada a la estepa. Aquí está lo que decide
 * dónde cae la luz, en funciones que se pueden probar (`EscenaMongolVeranoPrueba`), y cómo se pinta:
 * - **B.** Las sombras de las nubes del día sobre la estepa ([sombrasDeNube]), el sol raso de los días claros
 *   ([rasante]) y los rayos que bajan entre las nubes de tormenta ([claroDeTormenta]).
 * - **C.** La luz en el aire: la bruma del río al alba ([brumaDelRio]), el polvo dorado de las manadas por la tarde
 *   ([polvoDorado]) y el humo encendido a contraluz ([humoEncendido]).
 * - **D.** Lo pintado se enciende al sol y se apaga en la sombra ([pintado]); lo que queda bajo una nube recibe un
 *   pincel de sombra ([enSombra]).
 * - **E.** La noche a dos luces ([dosLuces]): la lumbre y la luna, con su lado plata ([filoDeLuna]) y sus sombras
 *   ([rayoDeSombras]); la hora azul que recorta las crestas ([recorte]).
 */
internal object LuzVerano {
    private val C = ColorMongol
    private const val H = VeranoMongol.H

    // ================================================================ B. Las sombras de las nubes

    /** Una nube del cielo del día: su borde izquierdo [x], su base [y] y su [ancho], como las pinta `nubes`. */
    internal class Nube(val x: Float, val y: Float, val ancho: Float)

    /** Lo que gasta del azar una `nube` de la biblioteca (`paisaje/Cielo.kt`): dos números y, en cada piso, uno más tres por borbotón. */
    private fun saltarNube(az: Azar) {
        az.real(); az.real()
        for (piso in 0..2) { val n = listOf(6, 4, 2)[piso] + az.entero(2); repeat(n * 3) { az.real() } }
    }

    /**
     * Las nubes de algodón que pinta hoy `nubes` (`paisaje/Cielo.kt`) en el cielo de este cuadro: con la misma semilla
     * y los mismos números del azar, en el mismo orden **[Aproximación]**: si la biblioteca cambia cómo gasta el azar,
     * las sombras ya no coinciden con las nubes (siguen siendo las de un cielo de ese día). Con el cielo despejado, los
     * borregos en filas, las torres del horizonte o la nube de los días de velos (los velos no dan sombra); con
     * «nublado», los cúmulos; con viento, la nube de entre los cirros. Los días de calima y los cubiertos, ninguna.
     */
    internal fun nubesDelDia(m: Momento): List<Nube> {
        val az = Azar(Semilla.de(m.semilla("cielo"), "nubes")); val h = H; val lista = ArrayList<Nube>()
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

    /** Hasta dónde bajan las sombras de las nubes: el llano de este lado queda al sol (es lo que se mira). */
    const val HASTA = 206f

    /**
     * Dónde caen hoy, a esta hora, las sombras de las nubes del día ([nubesDelDia]) sobre la estepa **[Aproximación]**:
     * cada nube la echa bajo ella, más cerca y más grande cuanto más alta está en el cielo (las del horizonte, chicas y
     * juntas en las lomas del fondo; las de arriba, anchas sobre el campamento), y corrida al lado contrario del sol y
     * hacia el fondo, más cuanto más bajo está el sol (como `Rayo.ax` y `Rayo.ay`): así caminan con la hora. Sin sol
     * (de noche, al alba y al ocaso, con el cielo cubierto) no hay ninguna.
     */
    internal fun sombrasDeNube(m: Momento): List<SombraDeNube> {
        val l = EscenaMongol.luces(m); val r = Rayo(l)
        if (r.fuerza < 0.12f) return emptyList()
        val ax = r.ax.coerceIn(-3f, 3f); val ay = r.ay.coerceIn(-3f, 0f)
        return nubesDelDia(m).mapIndexed { i, n ->
            val f = ((H - n.y) / H).coerceIn(0.03f, 1f); val g = f.pow(1.15f)
            val cx = n.x + n.ancho / 2f + ax * (10f + 50f * g)
            val cy = (H + 3f + 95f * g + ay * (2f + 12f * g)).coerceAtLeast(H + 2f)
            // Ni tan grandes que oscurezcan el llano entero (entonces no se leen como sombras de nube).
            val rx = (n.ancho * (0.6f + 1.3f * g) * (1f + 0.15f * abs(ax))).coerceAtMost(72f)
            SombraDeNube(Offset(cx, cy), rx, (rx * (0.06f + 0.3f * g)).coerceAtMost(9f), i)
        }.filter { it.centro.x + it.rx > -10f && it.centro.x - it.rx < ANCHO + 10f && it.centro.y - it.ry < HASTA }
    }

    /** Cuánto está en la sombra de una nube el punto [q] del llano, de 0 a 1. */
    internal fun enSombraDeNube(m: Momento, q: Offset): Float = enSombraDeNube(sombrasDeNube(m), q)

    /** Lo mismo con las sombras ya calculadas. */
    internal fun enSombraDeNube(sombras: List<SombraDeNube>, q: Offset): Float {
        var k = 0f
        for (s in sombras) { val dx = (q.x - s.centro.x) / s.rx; val dy = (q.y - s.centro.y) / s.ry; k = maxOf(k, (1.3f - 1.3f * (dx * dx + dy * dy)).coerceIn(0f, 1f)) }
        return k
    }

    /**
     * Pinta las sombras de las nubes, en una capa de sombra (una sola, aunque se encimen): cada una con su borde de nube
     * (unos lóbulos blandos) y, hacia el llano de este lado, cada vez más tenues hasta [HASTA].
     */
    fun DrawScope.sombrasDeNubes(m: Momento, r: Rayo) {
        val sombras = sombrasDeNube(m); if (sombras.isEmpty()) return
        // Los días de cúmulos, el sol que se cuela entre las nubes enciende el llano que queda fuera de sus sombras.
        val l = EscenaMongol.luces(m)
        if (l.nublado > 0.2f) drawRect(Brush.verticalGradient(0f to Color.Transparent, 0.35f to l.solColor.copy(alpha = 0.12f * l.nublado), 0.75f to l.solColor.copy(alpha = 0.08f * l.nublado), 1f to Color.Transparent, startY = H, endY = HASTA),
            Offset(-10f, H), androidx.compose.ui.geometry.Size(ANCHO + 20f, HASTA - H), blendMode = BlendMode.Plus)
        // Más honda que la de las cosas (una nube tapa todo el cielo de un lado): casi la misma con sol fuerte o velado.
        val honda = 0.4f + 0.3f * (r.fuerza / 0.72f).coerceIn(0f, 1f) + 0.2f * l.nublado
        capaSombra(r, honda / r.fuerza.coerceAtLeast(0.05f)) { c ->
            clipRect(-10f, H - 1f, ANCHO + 10f, HASTA) {
                for (s in sombras) {
                    val az = Azar(Semilla.de(m.semilla("cielo"), "sombra", s.semilla))
                    repeat(6) {
                        val q = s.centro + Offset(az.entre(-0.55f, 0.55f) * s.rx, az.entre(-0.35f, 0.35f) * s.ry)
                        val baja = (1f - (q.y - 176f) / (HASTA - 176f)).coerceIn(0f, 1f)
                        mancha(q, s.rx * az.entre(0.5f, 0.75f), s.ry * az.entre(0.7f, 1.05f), c, baja)
                    }
                }
            }
        }
    }

    /** El mismo pincel bajo la sombra de una nube ([k], de 0 a 1): sin sol directo, sin lado de luz y más frío. */
    fun enSombra(p: Pincel, k: Float): Pincel {
        if (k <= 0.01f) return p
        val l = p.l; val r = p.r
        val l2 = Luces(l.alto, l.medio, l.bajo, lerp(l.ambiente, mult(l.ambiente, r.color), 0.42f * k), l.bruma, l.brumaFuerza, l.solX, l.solAltura, l.solColor, l.solFuerza, l.lunaX, l.lunaAltura, l.estrellas, l.noche, l.nublado, l.eclipse)
        val r2 = Rayo(r.dia, r.lat, r.cot, r.tan, r.ax, r.ay, r.fuerza * (1f - k), r.color, r.contraste * (1f - 0.6f * k))
        return Pincel(l2, r2, p.calida, p.lumbre)
    }

    // ================================================================ B. El sol raso

    /**
     * Cuánto rasa el sol, de 0 a 1: con el sol bajo y el cielo limpio, las lomas tienen la cara de sombra larga y la
     * cresta encendida. Sube al alba y al atardecer; a mediodía y con el cielo cubierto, nada.
     */
    internal fun rasante(m: Momento): Float {
        val l = EscenaMongol.luces(m); val r = Rayo(l)
        if (r.fuerza < 0.1f) return 0f
        return ((0.95f - r.tan) / 0.6f).coerceIn(0f, 1f) * (1f - 1.4f * l.nublado).coerceIn(0f, 1f)
    }

    // ================================================================ B. La tormenta

    /** Un claro entre las nubes de tormenta: de dónde bajan los rayos ([arriba]), dónde caen en el llano ([abajo]), su [ancho] y su [fuerza]. */
    internal class Claro(val arriba: Offset, val abajo: Offset, val ancho: Float, val fuerza: Float)

    /**
     * Con tormenta, de día, el sol se cuela por un claro de las nubes y baja en rayos hasta un trozo del llano del otro
     * lado del río **[Diseño propio]**: el claro, cada día en otro sitio, se corre con la hora, y los rayos caen
     * inclinados al lado contrario del sol. Null sin tormenta o de noche.
     */
    internal fun claroDeTormenta(m: Momento): Claro? {
        if (m.ficha.clima != Clima.TORMENTA) return null
        val l = EscenaMongol.luces(m)
        if (l.noche > 0.35f || l.solAltura < 0.08f) return null
        val az = m.azar("claro"); val r = Rayo(l)
        val x = az.entre(100f, 290f) + (m.hora.toFloat() - 13f) * 5f
        val arriba = Offset(x, az.entre(24f, 44f)); val abajo = Offset(x - r.lat * 36f, H + az.entre(7f, 13f))
        return Claro(arriba, abajo, az.entre(28f, 40f), ((1f - l.noche * 2.5f) * (l.solAltura / 0.4f).coerceIn(0f, 1f)).coerceIn(0f, 1f))
    }

    /** Pinta los rayos de la tormenta y el trozo de llano que encienden. */
    fun DrawScope.rayosDeTormenta(m: Momento, p: Pincel) {
        val claro = claroDeTormenta(m) ?: return
        val l = p.l; val k = claro.fuerza
        val luz = lerp(l.solColor, Color.White, 0.35f)
        // El claro en las nubes: un borde encendido.
        mancha(claro.arriba, claro.ancho * 1.1f, 6f, lerp(l.tono(Color.White), luz, 0.4f), 0.5f * k)
        // Los rayos: haces que se abren al bajar, más fuertes abajo, donde cruzan el aire húmedo.
        val az = Azar(Semilla.de(m.semilla("claro"), "rayos"))
        for (j in 0 until 5) {
            val u = j - 2f; val a = claro.arriba + Offset(u * claro.ancho * 0.16f, 0f); val b = claro.abajo + Offset(u * claro.ancho * 0.5f, 0f)
            val w0 = az.entre(1.2f, 3f); val w1 = az.entre(5f, 11f)
            drawPath(poligono(a + Offset(-w0, 0f), a + Offset(w0, 0f), b + Offset(w1, 0f), b + Offset(-w1, 0f)),
                Brush.verticalGradient(listOf(luz.copy(alpha = 0.03f * k), luz.copy(alpha = (0.13f + 0.06f * az.real()) * k)), a.y, b.y), blendMode = BlendMode.Plus)
        }
        // El trozo de llano encendido: la hierba toma el sol entre la sombra de las nubes.
        mancha(claro.abajo, claro.ancho * 1.5f, 4.6f, p.luz(lerp(C.ESTEPA, C.VERDE, 0.35f), 0.35f), 0.6f * k)
        resplandor(claro.abajo, claro.ancho * 0.9f, 0.35f * k, luz)
    }

    // ================================================================ C. La luz en el aire

    /** La bruma baja del río, de 0 a 1: al alba, sin viento y sin lluvia, la más espesa hacia las 6:40. */
    internal fun brumaDelRio(m: Momento): Float {
        val c = m.ficha.clima
        if (VeranoMongol.rioTurbio(m) || c == Clima.VIENTO || c == Clima.POLVO) return 0f
        return (1f - abs(m.hora.toFloat() - 6.6f) / 1.9f).coerceIn(0f, 1f)
    }

    /** Pinta la bruma del río: tendida sobre el agua y las orillas, encendida del lado del sol, que la atraviesa raso. */
    fun DrawScope.brumaDelRio(m: Momento, p: Pincel) {
        val k = brumaDelRio(m); if (k <= 0.02f) return
        val l = p.l; val az = Azar(Semilla.de("mongol", "verano", "bruma"))
        val solX = l.solX * ANCHO
        val fria = l.tono(lerp(Color.White, C.SOMBRA, 0.15f), 0.3f); val encendida = lerp(l.tono(Color.White, 0.2f), l.solColor, 0.5f)
        val arriba = LejosVerano.ARRIBA
        for (i in 0 until 26) {
            val t = i / 25f; val a = arriba[(t * (arriba.size - 1)).toInt()]; val x = a.x
            val y = (a.y + LejosVerano.orillaDeAca(x)) / 2f + az.entre(-1.5f, 1.5f)
            val sol = (1f - abs(x - solX) / 220f).coerceIn(0f, 1f)
            mancha(Offset(x + az.entre(-6f, 6f), y), az.entre(22f, 40f), az.entre(2.2f, 4f) * (1f + (y - H) / 40f), lerp(fria, encendida, 0.3f + 0.7f * sol), (0.26f + 0.2f * sol) * k)
        }
        // Un velo más bajo y más tenue que se arrastra por el llano de este lado, entre las tiendas.
        for (i in 0 until 7) { val x = az.entre(-20f, 300f); mancha(Offset(x, az.entre(140f, 156f)), az.entre(40f, 70f), az.entre(3f, 5f), lerp(fria, encendida, 0.4f), 0.16f * k) }
    }

    /** El polvo que levantan las manadas, dorado con el sol de la tarde: de 0 a 1, de las 14:30 a las 19:50, sin lluvia ni cielo cubierto. */
    internal fun polvoDorado(m: Momento): Float {
        val l = EscenaMongol.luces(m)
        if (VeranoMongol.rioTurbio(m) || l.nublado > 0.5f || Rayo(l).fuerza < 0.1f) return 0f
        val h = m.hora.toFloat()
        return ((h - 14.5f) / 2f).coerceIn(0f, 1f) * ((19.8f - h) / 1.3f).coerceIn(0f, 1f)
    }

    /**
     * El humo encendido por dentro (a contraluz del sol bajo, o por la lumbre del aro de noche): resplandores a lo
     * largo de la columna que sube de [pie], tumbada por el [viento] como la de `humo` (`paisaje/Fuego.kt`).
     */
    fun DrawScope.humoEncendido(pie: Offset, alto: Float, ancho: Float, viento: Float, color: Color, k: Float) {
        if (k < 0.02f) return
        for (p in listOf(0.06f, 0.18f, 0.32f, 0.5f, 0.7f)) {
            val c = Offset(pie.x + viento * alto * 0.75f * p.pow(1.5f), pie.y - alto * p)
            resplandor(c, ancho * (0.3f + 0.7f * p) * (1f + 0.4f * abs(viento) * p), k * (1f - p), color)
        }
    }

    /** Cuánto enciende el sol bajo el humo, a contraluz: con el sol bajo y el cielo limpio. */
    internal fun contraluz(m: Momento): Float = rasante(m)

    // ================================================================ D. Lo pintado

    private fun saturado(c: Color, k: Float): Color {
        val g = (c.red + c.green + c.blue) / 3f
        fun s(v: Float) = (v + (v - g) * k).coerceIn(0f, 1f)
        return Color(s(c.red), s(c.green), s(c.blue), c.alpha)
    }

    /**
     * El color de una cosa pintada (un fieltro con su dibujo, una manta, una cinta, una silla pintada) que mira a la
     * luz [t] (de −1 a 1): donde le da el sol el color sube (más vivo y más caliente); en la sombra baja y se enfría. Con
     * el cielo cubierto y de noche, el de siempre. Todo con el pincel ([Pincel.luz], [Pincel.sombra]), sin velos encima.
     */
    fun pintado(p: Pincel, c: Color, t: Float, lejos: Float = 0f): Color {
        val base = p.por(c, t, lejos)
        val sol = (p.r.fuerza / 0.72f).coerceIn(0f, 1f)
        if (sol <= 0.02f) return base
        return if (t >= 0f) lerp(base, p.luz(saturado(c, 0.55f), lejos), 0.5f * t * sol)
        else lerp(base, p.sombra(lerp(c, saturado(c, -0.5f), 0.6f), lejos), 0.45f * -t * sol)
    }

    // ================================================================ E. La noche a dos luces

    /**
     * Las dos luces de la noche, de 0 a 1: la cálida (la puerta abierta de Bat y el fuego del campamento) y la fría (la
     * luna, [PiezasMongol.lunaQueAlumbra]). De día, ninguna de las dos.
     */
    internal fun dosLuces(m: Momento): Pair<Float, Float> {
        val l = EscenaMongol.luces(m)
        val puerta = if (EscenaMongol.puerta(m) != Puerta.CERRADA) (l.noche - 0.1f).coerceAtLeast(0f) * maxOf(EscenaMongol.fuegoDentro(m), 0.6f) else 0f
        val fuego = EscenaMongol.fuegoFuera(m) * l.noche
        return maxOf(puerta, fuego).coerceIn(0f, 1f) to PiezasMongol.lunaQueAlumbra(l)
    }

    /**
     * El rayo de las sombras arrojadas: el del sol de día y el de la luna de noche ([PiezasMongol.rayoDeLuna], sombras
     * frías y flojas), para que las mismas capas de sombra sirvan a las dos luces.
     */
    fun rayoDeSombras(l: Luces): Rayo { val r = Rayo(l); return if (r.fuerza > 0.05f) r else PiezasMongol.rayoDeLuna(l) }

    /**
     * El filo plata de la luna en el borde de una cosa ([forma], su silueta): lo que queda de la silueta al correrla
     * [ancho] unidades al lado contrario de la luna, alumbrado en azul plata. Nada sin luna.
     */
    fun DrawScope.filoDeLuna(forma: Path, l: Luces, ancho: Float = 1.2f, k: Float = 1f) {
        val luna = PiezasMongol.lunaQueAlumbra(l) * k; if (luna < 0.03f) return
        val r = PiezasMongol.rayoDeLuna(l)
        val corrida = Path().apply { addPath(forma, Offset(-r.lat * ancho * 0.7f, ancho)) }
        val filo = Path(); filo.op(forma, corrida, PathOperation.Difference)
        drawPath(filo, PiezasMongol.LUNA.copy(alpha = (0.26f * luna).coerceIn(0f, 1f)), blendMode = BlendMode.Plus)
    }

    /**
     * La hora azul, de 0 a 1: la noche ya cerrada, pero con el resplandor del ocaso todavía en el horizonte, que recorta
     * las crestas, el ovoo y las tiendas. Nada con el cielo cubierto.
     */
    fun recorte(l: Luces): Float = ((l.noche - 0.3f) / 0.3f).coerceIn(0f, 1f) * ((1f - l.noche) / 0.12f).coerceIn(0f, 1f) * (1f - 1.5f * l.nublado).coerceIn(0f, 1f) * (if (l.solAltura > -0.6f) 1f else 0f)

    /** Pinta el filo del resplandor del ocaso a lo largo de una [cresta] recortada contra el cielo. */
    fun DrawScope.filoDelOcaso(cresta: Path, l: Luces, k: Float) {
        if (k < 0.02f) return
        drawPath(cresta, lerp(l.bajo, Color.White, 0.15f).copy(alpha = (0.55f * k).coerceIn(0f, 1f)), style = Stroke(0.7f), blendMode = BlendMode.Plus)
    }
}
