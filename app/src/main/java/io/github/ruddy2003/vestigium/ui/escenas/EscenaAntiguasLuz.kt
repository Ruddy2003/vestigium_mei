package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.COLLADO
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.CRESTA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.luna
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sol
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Clima
import kotlin.math.abs
import kotlin.math.sin

/**
 * La luz del paso, con la lógica de los interiores aprobados llevada al aire libre (`docs/diseno/ESCENAS.md`, 2 bis), y
 * con lo que el cuento ya dice: «la sombra sube por el paso, piedra a piedra, hasta que solo queda luz en el collado.
 * Luego, tampoco».
 *
 * - **La sombra grande que camina** ([sombra]): una línea de sombra con borde roto (la de la sierra que queda a la
 *   espalda de quien mira **[Aproximación]**), que por la mañana baja desde las cumbres (el camino, «todavía azul») y por
 *   la tarde trepa en diagonal por el paso, deja el collado y las cumbres para el final y se las come. De 10:00 a 15:30
 *   no hay. Con el cielo cubierto no hay sombra que arrojar.
 * - **La mancha de sol** ([franja]): por la mañana el sol asoma por la muesca del collado, a contraluz, y por ella entra
 *   una franja de sol que baja por la ladera hasta la plaza, dentro de la sombra; camina con la hora.
 * - **Las sombras de las nubes** ([nubes]): «sus sombras corren por las laderas más que un caballo»; con el cielo
 *   cubierto, al revés: rotos de sol que caminan por la montaña.
 * - **La última luz** ([alpenglow]): rosa en la nieve de las cumbres «mucho después de que el valle esté a oscuras».
 * - **La noche a dos luces** ([luna], [rayo]): la puerta, las ventanas y las linternas, cálidas; la luna sobre el collado,
 *   que platea la nieve y tumba sombras frías.
 *
 * Lo que decide dónde cae la luz son funciones sin dibujo, que lee `EscenaAntiguasPrueba`.
 */
internal object LuzAntiguas {
    private val C = ColorAntiguas

    // ---------------------------------------------------------------- El sol y la luna

    /** La hora del sol (de 5.6 a 20.4) que corresponde a las [l], por dónde va en el cielo. */
    fun horaDelSol(l: Luces): Float = 5.6f + 14.8f * ((0.86f - l.solX) / 0.52f)

    /** El sol que hay, de 0 a 1: nada de noche ni con el cielo cubierto. */
    fun sol(l: Luces): Float = (Rayo(l).fuerza / 0.72f).coerceIn(0f, 1f)

    /** Cuánto alumbra la luna, de 0 (de día, sin luna o cubierto) a 1. */
    fun luna(l: Luces): Float = if (l.lunaAltura <= 0.05f) 0f else ((l.noche - 0.45f) / 0.3f).coerceIn(0f, 1f) * ((0.62f - l.nublado) / 0.3f).coerceIn(0f, 1f)

    /**
     * De dónde viene la luz que tumba las sombras: de día, el sol (`Rayo`); de noche, la luna, que también da su sombra,
     * corta y azul **[Diseño propio]**, como en Abismo y Mongol.
     */
    fun rayo(l: Luces): Rayo {
        val r = Rayo(l)
        if (r.fuerza > 0.05f || l.noche < 0.55f) return r
        val k = luna(l); if (k <= 0.05f) return r
        val cruda = ((l.lunaX - 0.5f) * 2.6f).coerceIn(-1f, 1f); val lat = if (abs(cruda) < 0.35f) (if (cruda < 0f) -0.35f else 0.35f) else cruda
        val cot = 1.1f
        return Rayo(dia = 0f, lat = lat, cot = cot, tan = 1f / cot, ax = -lat * cot, ay = -0.33f * (1.3f - 0.6f * abs(lat)) * cot,
            fuerza = 0.4f * k, color = Color(0xFF5E66AC), contraste = 0.3f + 0.4f * k)
    }

    // ---------------------------------------------------------------- La sombra que sube

    /**
     * La sombra grande a una hora: cuánta hay ([cuanta], de 0 a 1, con el sol y el cielo), si es la de la mañana (que baja)
     * o la de la tarde (que sube) y hasta dónde llega ([sube], de 0, nada, a 1, todo cubierto hasta las cumbres).
     */
    class Sombra(val cuanta: Float, val manana: Boolean, val sube: Float) {
        /** La altura de la línea en la columna [x]: por la tarde, en diagonal, más alta a la izquierda; por la mañana, algo más alta a la derecha. */
        fun y(x: Float): Float = if (manana) (343f - 381f * sube) - 0.28f * (x - 190f) + borde(x) else (385f - 465f * sube) + 0.5f * (x - 190f) + borde(x)

        /** El borde roto: la sombra de una sierra, con sus dientes desiguales. */
        private fun borde(x: Float): Float = 4.5f * sin(x * 0.061f + 1.3f) + 2.6f * sin(x * 0.17f + 0.4f) + 1.4f * sin(x * 0.41f + 2.2f)
    }

    /**
     * Hasta dónde llega la sombra a la hora del sol [h]: por la mañana baja de 1 (al salir el sol) a 0 a las 10:00; por la
     * tarde sube de 0 (a las 15:30) a 1 (a las 20:18, cuando el sol se pone detrás de la sierra) **[Aproximación]**.
     */
    fun sube(h: Float): Float = if (h < 12.8f) ((10f - h) / 3.75f).coerceIn(0f, 1f) else ((h - 15.5f) / 4.8f).coerceIn(0f, 1f)

    fun sombra(l: Luces): Sombra {
        if (l.solAltura < -0.05f) return Sombra(0f, false, 1f)
        val h = horaDelSol(l)
        val cuanta = sol(l) * ((0.55f - l.nublado) / 0.25f).coerceIn(0f, 1f)
        return Sombra(cuanta, h < 12.8f, sube(h))
    }

    /** Cuánta sombra grande hay en [p], de 0 a 1 (con el borde blando). */
    fun sombraEn(s: Sombra, p: Offset): Float = if (s.cuanta <= 0.01f) 0f else s.cuanta * ((p.y - s.y(p.x) + 6f) / 12f).coerceIn(0f, 1f)

    /** La forma de lo que queda debajo de la línea (la zona en sombra), hasta abajo del lienzo. */
    fun zonaEnSombra(s: Sombra): Path {
        val arriba = (-10..390 step 6).map { x -> Offset(x.toFloat(), s.y(x.toFloat())) }
        return poligono(arriba + listOf(Offset(390f, ALTO + 10f), Offset(-10f, ALTO + 10f)))
    }

    /** La tierra: todo lo que queda debajo de la cresta lejana (lo que la sombra puede cubrir; el cielo, nunca). */
    val TIERRA: Path by lazy { poligono(listOf(Offset(-10f, CRESTA.first().y)) + CRESTA + listOf(Offset(392f, CRESTA.last().y), Offset(392f, ALTO + 10f), Offset(-10f, ALTO + 10f))) }

    // ---------------------------------------------------------------- La franja de sol por la muesca

    /** La franja de sol que entra por el collado: de [a] (en la muesca) a [b], con su medio ancho en cada punta, y su [fuerza]. */
    class Franja(val a: Offset, val b: Offset, val anchoA: Float, val anchoB: Float, val fuerza: Float)

    /**
     * Por la mañana, de 6:10 a 8:40, con sol: la luz que pasa por la muesca baja por la ladera del paso y llega a la plaza a
     * las 7:00; se va acortando mientras el sol sube **[Aproximación]**: no es la luz real por ese collado.
     */
    fun franja(l: Luces): Franja? {
        val s = sombra(l); if (!s.manana || s.cuanta <= 0.05f) return null
        val h = horaDelSol(l); if (h < 6.1f || h > 8.7f) return null
        val u = ((h - 6.1f) / 2.6f).coerceIn(0f, 1f)
        val f = s.cuanta * (if (h < 6.5f) (h - 6.1f) / 0.4f else if (h > 8f) (8.7f - h) / 0.7f else 1f).coerceIn(0f, 1f)
        val a = COLLADO + Offset(-1f, 3f)
        val largo = 150f - 92f * u
        val dir = Offset(-0.26f + 0.08f * u, 1f).let { it / it.getDistance() }
        return Franja(a, a + dir * largo, 3.4f, 15f - 6f * u, f)
    }

    /** La forma de la franja: bordes ondulados, como la luz sobre una ladera con relieve. */
    fun formaDeLaFranja(f: Franja): Path {
        val ab = f.b - f.a; val u = ab / ab.getDistance(); val n = Offset(-u.y, u.x)
        fun ancho(t: Float) = f.anchoA + (f.anchoB - f.anchoA) * t
        val izq = (0..14).map { i -> val t = i / 14f; f.a + ab * t + n * (ancho(t) * (1f + 0.22f * sin(t * 10f + 1.1f) + 0.1f * sin(t * 23f))) }
        val der = (0..14).map { i -> val t = i / 14f; f.a + ab * t - n * (ancho(t) * (1f + 0.2f * sin(t * 12f + 2.6f) + 0.12f * sin(t * 19f + 0.5f))) }
        return TrazosValle.cerrada(listOf(f.a - u * f.anchoA * 0.6f) + izq + der.reversed())
    }

    // ---------------------------------------------------------------- Los dibujos

    /**
     * La sombra grande sobre todo lo pintado hasta ahora (la montaña, la ladera de las vetas, la aldea y la plaza): una capa
     * que multiplica, azul violeta y honda, con el borde blando; la franja de la muesca, recortada dentro. Lo cercano (la
     * posada, el pino, las cosas de la puerta) la recibe con su pincel ([VistaAntiguas.pincelEn]) y con otra pasada al final.
     */
    fun DrawScope.sombraQueSube(v: VistaAntiguas) {
        val s = v.sombra; if (s.cuanta <= 0.02f) return
        val c = lerp(v.r.color, Color(0xFF585AA8), 0.35f)
        val zona = zonaEnSombra(s)
        val fr = franja(v.l)
        drawIntoCanvas { tela ->
            tela.saveLayer(Rect(0f, 0f, ANCHO, ALTO), Paint().apply { blendMode = BlendMode.Multiply; alpha = (0.88f * s.cuanta).coerceIn(0f, 1f) })
            clipPath(TIERRA) {
                drawPath(zona, c)
                // La penumbra del borde: unas pasadas finas por encima de la línea.
                val linea = Path().apply { moveTo(-10f, s.y(-10f)); for (x in -4..390 step 6) lineTo(x.toFloat(), s.y(x.toFloat())) }
                for ((w, a) in listOf(10f to 0.18f, 5f to 0.3f)) drawPath(linea, c.copy(alpha = a), style = Stroke(w))
                if (fr != null) {
                    val forma = formaDeLaFranja(fr)
                    drawPath(forma, Color.Black, alpha = 0.8f * fr.fuerza, blendMode = BlendMode.DstOut)
                    for ((w, a) in listOf(7f to 0.2f, 3f to 0.3f)) drawPath(forma, Color.Black.copy(alpha = a * fr.fuerza), style = Stroke(w), blendMode = BlendMode.DstOut)
                }
            }
            tela.restore()
        }
        // Lo que queda al sol, con el sol bajo, se dora: la luz enciende lo pintado.
        val bajo = ((1.1f - v.l.solAltura) / 0.8f).coerceIn(0f, 1f)
        if (bajo > 0.05f) clipPath(TIERRA) { clipPath(zona, androidx.compose.ui.graphics.ClipOp.Difference) {
            val calido = lerp(v.l.solColor, Color(0xFFFFB878), 0.5f)
            drawRect(calido.copy(alpha = 0.16f * bajo * s.cuanta), Offset(0f, 0f), Size(ANCHO, ALTO), blendMode = BlendMode.Plus)
        } }
        if (fr != null) clipPath(TIERRA) {
            val forma = formaDeLaFranja(fr)
            drawPath(forma, lerp(v.l.solColor, Color(0xFFFFC890), 0.4f).copy(alpha = 0.1f * fr.fuerza), blendMode = BlendMode.Plus)
        }
    }

    /**
     * Las nubes del día sobre la montaña: con el cielo despejado o con viento, sus sombras (manchas blandas que corren por
     * las laderas con la hora); con nubes, rotos de sol que caminan por la montaña. Solo de 8:30 a 17:30.
     */
    fun DrawScope.nubesQuePasan(v: VistaAntiguas) {
        val h = v.m.hora.toFloat(); if (h < 8.5f || h > 17.5f || v.niebla >= 2) return
        val az = v.m.azar("sombras de nubes")
        val corre = (h - 8.5f) * 14f
        when (v.clima) {
            Clima.DESPEJADO, Clima.VIENTO -> {
                val n = az.entero(4)                                                   // un día de cada cuatro sin ninguna
                if (n == 0) return
                val c = lerp(v.r.color, Color(0xFF6A6CB0), 0.3f)
                drawIntoCanvas { tela ->
                    tela.saveLayer(Rect(0f, 0f, ANCHO, ALTO), Paint().apply { blendMode = BlendMode.Multiply; alpha = (0.55f * v.r.fuerza / 0.72f).coerceIn(0f, 1f) })
                    clipPath(TIERRA) { repeat(n + 1) { manchaDeNube(az, corre, c) } }
                    tela.restore()
                }
            }
            Clima.NUBES -> clipPath(TIERRA) {
                val c = lerp(v.l.solColor, Color(0xFFFFF0D0), 0.4f)
                repeat(2 + az.entero(2)) {
                    val q = Offset(((az.entre(-40f, 380f) + corre * 0.8f) % 440f) - 30f, az.entre(60f, 200f))
                    val w = az.entre(28f, 54f)
                    drawPath(TrazosValle.blando(q, w, w * 0.32f, az, 11, 0.25f), c.copy(alpha = 0.16f), blendMode = BlendMode.Plus)
                    drawPath(TrazosValle.blando(q + Offset(2f, -1f), w * 0.6f, w * 0.18f, az, 9, 0.25f), c.copy(alpha = 0.1f), blendMode = BlendMode.Plus)
                }
            }
            Clima.NIEBLA, Clima.LLUVIA, Clima.TORMENTA, Clima.NIEVE, Clima.POLVO, Clima.SIN_CLIMA -> Unit
        }
    }

    /** La sombra de una nube sobre la montaña: una masa blanda, más ancha que alta, tendida por la ladera. */
    private fun DrawScope.manchaDeNube(az: io.github.ruddy2003.vestigium.vidas.Azar, corre: Float, c: Color) {
        val q = Offset(((az.entre(-60f, 380f) + corre) % 460f) - 40f, az.entre(50f, 215f))
        val w = az.entre(34f, 70f) * (0.6f + q.y / 300f)
        drawPath(TrazosValle.blando(q, w, w * 0.3f, az, 12, 0.24f), c)
        drawPath(TrazosValle.blando(q + Offset(w * 0.4f, -w * 0.08f), w * 0.5f, w * 0.2f, az, 9, 0.24f), c)
    }

    /**
     * La última luz: el rosa en la nieve y la roca de lo alto «cuando la aldea ya está gris», de 19:20 a 20:50, por encima
     * de la sombra que sube; y después, la hora azul, un resplandor detrás de la cresta que la recorta.
     */
    fun DrawScope.ultimaLuz(v: VistaAntiguas) {
        val k = alpenglow(v.m, v.l); val az = horaAzul(v.m, v.l)
        if (k > 0.02f) clipPath(TIERRA) {
            val rosa = Color(0xFFFF9C8C)
            val s = v.sombra
            val limite = if (s.cuanta > 0.05f) zonaEnSombra(s) else null
            val pinta: DrawScope.() -> Unit = {
                drawRect(Brush.verticalGradient(0f to rosa.copy(alpha = 0.34f * k), 0.5f to rosa.copy(alpha = 0.18f * k), 1f to Color.Transparent, startY = 20f, endY = 150f), Offset(0f, 0f), Size(ANCHO, 150f), blendMode = BlendMode.Plus)
            }
            if (limite != null) clipPath(limite, androidx.compose.ui.graphics.ClipOp.Difference) { pinta() } else pinta()
        }
        if (az > 0.02f) {
            val c = lerp(v.l.bajo, Color(0xFFFF9E78), 0.3f)
            for ((w, a) in listOf(24f to 0.035f, 12f to 0.05f, 5f to 0.06f)) drawPath(io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva(CRESTA), c.copy(alpha = a * az), style = Stroke(w), blendMode = BlendMode.Plus)
        }
    }

    /** El rosa de la última luz en las cumbres, de 0 a 1: de 19:20 a 20:50, sin el cielo cubierto. */
    fun alpenglow(m: Momento, l: Luces): Float {
        val h = m.hora.toFloat()
        val k = when { h < 19.2f || h > 21f -> 0f; h < 19.8f -> (h - 19.2f) / 0.6f; h < 20.3f -> 1f; else -> (21f - h) / 0.7f }
        return k * ((0.6f - l.nublado) / 0.3f).coerceIn(0f, 1f)
    }

    /** La hora azul: el resplandor que queda detrás de la cresta después de ponerse el sol. */
    fun horaAzul(m: Momento, l: Luces): Float {
        val h = m.hora.toFloat()
        val k = when { h < 20.2f || h > 22.4f -> 0f; h < 20.8f -> (h - 20.2f) / 0.6f; else -> (22.4f - h) / 1.6f }
        return k * ((0.7f - l.nublado) / 0.4f).coerceIn(0f, 1f)
    }

    /**
     * Lo encendido que alumbra lo de alrededor, después del primer plano: el charco de la puerta en las losas y su halo en
     * el muro, el del ventanuco, el de la ventana del cuarto alto; y la luna, que platea lo que mira hacia ella.
     */
    fun DrawScope.lumbres(v: VistaAntiguas) {
        val n = v.noche; val e = v.encendido
        val sala = e.sala * n
        val puerta = EscenaAntiguas.puerta(v.m)
        if (sala > 0.05f && puerta != EscenaAntiguas.Puerta.CERRADA) {
            // El charco: la luz sale por la puerta y se tiende por el suelo hacia la plaza, más ancha cuanto más lejos.
            val boca = FachadaPosada.p(10.1f, 0f)
            val charco = poligono(listOf(FachadaPosada.p(9.7f, 0f), FachadaPosada.p(10.6f, 0f), FachadaPosada.p(11.4f, 0f, 3.4f), FachadaPosada.p(9.6f, 0f, 3.6f)))
            drawPath(charco, Brush.radialGradient(listOf(C.LUMBRE.copy(alpha = 0.42f * sala), C.LUMBRE.copy(alpha = 0.12f * sala), Color.Transparent), boca, 130f), blendMode = BlendMode.Plus)
            resplandor(FachadaPosada.p(10.2f, 1.2f), 70f, 0.5f * sala, C.LUMBRE)
        }
        if (sala > 0.05f) resplandor(FachadaPosada.p(12.6f, 1.5f), 18f, 0.35f * sala, C.LUMBRE)
        val alto = e.cuartoAlto * n
        if (alto > 0.05f) resplandor(FachadaPosada.p(11.25f, 2.9f), 26f, 0.5f * alto, C.LUMBRE)
        // La luna sobre el collado: un halo frío en la nieve de arriba.
        if (v.luna > 0.05f) {
            val q = v.l.luna(SitioAntiguas.H)
            clipPath(TIERRA) { drawCircle(Brush.radialGradient(listOf(C.LUNA.copy(alpha = 0.16f * v.luna * (0.5f + 0.5f * v.nieveArriba)), Color.Transparent), q + Offset(0f, 40f), 120f), 120f, q + Offset(0f, 40f), blendMode = BlendMode.Plus) }
        }
    }

    /**
     * La luz en el aire: por la mañana, el haz que entra por la muesca del collado con sus motas de polvo de nieve y la
     * bruma del torrente, encendida donde la atraviesa; al caer la tarde, el aire dorado del valle.
     */
    fun DrawScope.luzEnElAire(v: VistaAntiguas) {
        val fr = franja(v.l) ?: return
        val az = SitioAntiguas.fijo("haz")
        val calido = lerp(v.l.solColor, Color(0xFFFFD6A0), 0.4f)
        val sol = v.l.sol(SitioAntiguas.H)
        val haz = poligono(sol + Offset(-6f, 4f), sol + Offset(6f, 4f), fr.b + Offset(fr.anchoB * 1.3f, 0f), fr.b + Offset(-fr.anchoB * 1.3f, 0f))
        drawPath(haz, Brush.linearGradient(listOf(calido.copy(alpha = 0.1f * fr.fuerza), calido.copy(alpha = 0.02f * fr.fuerza)), sol, fr.b), blendMode = BlendMode.Plus)
        repeat(46) {
            val t = az.real(); val q = sol + (fr.b - sol) * t + Offset(az.entre(-1f, 1f) * (3f + fr.anchoB * t), az.entre(-2f, 2f))
            drawCircle(calido.copy(alpha = (0.7f * fr.fuerza * az.real()).coerceIn(0f, 1f)), az.entre(0.25f, 0.6f), q, blendMode = BlendMode.Plus)
        }
        // La bruma del torrente, encendida donde la cruza el haz.
        clipPath(formaDeLaFranja(fr)) { mancha(Offset(fr.b.x + 10f, SitioAntiguas.torrenteEn(fr.b.x) - 3f), 60f, 6f, calido, 0.3f * fr.fuerza) }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(v: VistaAntiguas, reloj: Float) {
        val l = v.l
        // El humo de la chimenea de la posada: «sube derecho, como un camino para nadie» cuando no hay viento.
        val hum = EscenaAntiguas.humo(v.m)
        humo(l, PosadaAntiguas.CHIMENEA, 120f, 9f, reloj, v.m.viento * 0.8f, 0.2f, 0f, 3, 10, 0.4f + 0.5f * hum)
        // Los de la aldea, la herrería y las casas de los mineros.
        with(AldeaAntiguas) { humos(v, reloj) }
        with(VetasAntiguas) { humos(v, reloj) }
        // El torrente: los brillos del agua que corre.
        if (!v.invierno && v.noche < 0.8f) destellos(l, Rect(150f, 192f, 336f, 214f), reloj, v.m.semilla("torrente"), 16)
        // La fila de linternas de los mineros que bajan de las vetas, «como una procesión».
        with(VetasAntiguas) { filaDeLinternas(v, reloj) }
        with(PasoAntiguas) { linternasEnLaLadera(v, reloj) }
    }
}
