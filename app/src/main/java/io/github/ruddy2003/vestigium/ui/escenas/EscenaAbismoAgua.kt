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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * **El agua transparente vista desde arriba**, la pieza que distingue a Abismo de los otros dos cuadros a bordo: «el agua
 * tan clara en los días buenos que se ven los peces a diez brazas desde la borda»; «el agua tiene el color de una
 * botella». No es un corte bajo el agua: es la superficie, que cerca deja ver el fondo y lejos devuelve el cielo.
 *
 * **Cómo se ve** **[Aproximación]**: hay dos planos con la misma cámara de agujero. La superficie, a [ojo] metros bajo los
 * ojos, y el fondo, [hondo] metros más abajo (la hondura aparente: el agua acerca el fondo a tres cuartos de la real).
 * Las dos se proyectan hacia el mismo horizonte, pero el fondo «va más despacio»: lo que hay en él se ve más lejos y más
 * chico que lo que flota, y eso es lo que da la hondura. Cuánto fondo se ve depende de cuánto se mira hacia abajo
 * ([transparencia]: el reflejo de Fresnel, que cerca de la borda casi no devuelve nada y hacia el horizonte lo devuelve
 * todo), de la claridad del día ([claridad]: la calma, el agua clara, el norte que la enturbia) y de la luz (de noche,
 * nada). Con la distancia, el fondo se enfría y pierde contraste hacia el verde botella ([absorbe]).
 *
 * Las capas, de abajo arriba ([pintar]): el mar de la biblioteca (`agua`), que lejos sigue siendo el de siempre; el fondo
 * (la arena con sus rizos, las praderas de hierba, las piedras, las esponjas como cabezas negras), con la sombra del
 * barco y del bote ([sombras]), los peces con su sombra y los haces de sol que bajan; y encima, la piel del agua: el
 * cielo devuelto que crece hacia lejos y los rizos. En la parte viva: la red de luz que el sol hace en la arena
 * ([redDeLuz]), las burbujas del buzo ([burbujas]) y el verde que brilla de noche donde algo toca el agua ([verdeVivo]).
 * Todo sin capas (`saveLayer`): lo que se ve del fondo se pinta con su transparencia en el color.
 */
internal class AguaClara(
    val l: Luces, val r: Rayo, val horizonte: Float, val focal: Float, val centro: Float,
    val ojo: Float, val hondo: Float, val claridad: Float, val viento: Float,
) {
    private val C = ColorAbismo

    /** Cuánto es de día (de 0 a 1): el fondo solo se ve con luz. */
    val dia: Float = (1f - l.noche * 1.25f).coerceIn(0f, 1f)

    /** La transparencia del agua en la altura [y] del lienzo, de 0 (devuelve el cielo) a 1 (deja ver el fondo), con la claridad y la luz de hoy. */
    fun transparencia(y: Float): Float {
        val a = (y - horizonte) / focal                                          // la tangente de cuánto se mira hacia abajo
        val t = ((a - 0.12f) / 0.36f).coerceIn(0f, 1f)
        return (t * t * (3f - 2f * t)) * claridad * dia * (1f - 0.35f * l.nublado)
    }

    /** Cuánta agua hay entre el fondo y los ojos en la altura [y], de 0 (poca: el fondo con su color) a 1 (todo verde botella). */
    fun absorbe(y: Float): Float = (1f - ((y - horizonte) / focal - 0.12f) / 0.75f).coerceIn(0.05f, 1f)

    /** Un punto de la superficie (x a la derecha, z de fondo, en metros desde los ojos) en el lienzo. */
    fun superficie(x: Float, z: Float): Offset = Offset(centro + focal * x / z, horizonte + focal * ojo / z)

    /** Un punto del fondo en el lienzo. */
    fun enElFondo(x: Float, z: Float): Offset = Offset(centro + focal * x / z, horizonte + focal * (ojo + hondo) / z)

    /** A qué profundidad (z) está el fondo que se ve en la altura [y]. */
    fun zDelFondo(y: Float): Float = focal * (ojo + hondo) / (y - horizonte).coerceAtLeast(0.5f)

    /** El color propio del fondo de arena visto en la altura [y]: la arena tibia cerca, el verde botella lejos. */
    fun colorDelFondo(propio: Color, y: Float): Color = l.tono(lerp(lerp(propio, lerp(C.MAR_CLARO, C.MAR, 0.35f), 0.3f), C.MAR, 0.08f + absorbe(y) * 0.86f))

    // ---------------------------------------------------------------- La parte quieta

    /**
     * El agua en [zona] (con el borde de arriba en el horizonte): el mar de siempre y, encima, lo que se ve del fondo y
     * la piel del agua. [sombras]: las manchas de sombra en el fondo, en metros (x, z), del barco, del bote y de lo que
     * flote. [semilla]: el fondo del banco, que no cambia de un día a otro.
     */
    fun DrawScope.pintar(zona: Rect, semilla: Long, sombras: List<List<Offset>>, semillaDia: Long) {
        // 1. El mar de la biblioteca: lejos es el de siempre.
        agua(l, zona, Azar(semillaDia), propio = C.MAR, viento = viento, hondo = C.MAR_HONDO, orilla = C.MAR_CLARO)
        val y0 = horizonte + focal * 0.12f
        if (transparencia(zona.bottom) > 0.02f) {
            // 2. El fondo: la arena, con su transparencia en el color (sin capas).
            val paradas = (0..8).map { i -> val y = y0 + (zona.bottom - y0) * i / 8f; (i / 8f) to colorDelFondo(C.ARENA, y).copy(alpha = transparencia(y)) }.toTypedArray()
            drawRect(Brush.verticalGradient(*paradas, startY = y0, endY = zona.bottom), Offset(zona.left, y0), Size(zona.width, zona.bottom - y0))
            fondoDelBanco(zona, y0, semilla)
            for (q in sombras) sombraEnElFondo(q)
            peces(zona, semilla, y0)
            haces(zona, y0, semilla)
        }
        // 3. La piel del agua: el cielo devuelto, que crece hacia lejos (Fresnel), y los rizos encima de todo.
        piel(zona, y0, semillaDia)
    }

    /** Una profundidad (z) del fondo al azar entre [z0] y [z1], con más cerca que lejos (cada metro de cerca ocupa más en el lienzo). */
    private fun Azar.zEntre(z0: Float, z1: Float): Float = z0 * Math.pow((z1 / z0).toDouble(), real().toDouble()).toFloat()

    /** El fondo del banco: los rizos de la arena, las praderas de hierba, las piedras y las esponjas, con su sombra. */
    private fun DrawScope.fondoDelBanco(zona: Rect, y0: Float, semilla: Long) {
        val az = Azar(semilla)
        val sx = -r.lat.coerceIn(-1f, 1f)                                         // hacia dónde caen las sombras del fondo
        val zc = zDelFondo(zona.bottom); val zl = zDelFondo(y0 + 6f)
        fun x(z: Float) = (az.entre(-0.2f, 1.15f)) * z * (zona.right - centro) / focal
        // Los rizos de la arena: rayas onduladas, paralelas, con su cara de luz y su cara de sombra.
        repeat(150) {
            val z = az.zEntre(zc, zl); val a = enElFondo(x(z), z); if (!zona.contains(a)) return@repeat
            val t = transparencia(a.y); if (t < 0.05f) return@repeat
            val k = focal / z; val w = k * az.entre(0.7f, 1.8f)
            val curva = Path().apply { moveTo(a.x - w, a.y + w * 0.04f); cubicTo(a.x - w * 0.4f, a.y - w * 0.12f, a.x + w * 0.3f, a.y + w * 0.1f, a.x + w, a.y - w * 0.06f) }
            drawPath(curva, colorDelFondo(lerp(C.ARENA, C.MADERA_OSC, 0.45f), a.y).copy(alpha = 0.32f * t), style = Stroke((k * 0.05f).coerceIn(0.3f, 1.6f), cap = StrokeCap.Round))
            drawPath(Path().apply { addPath(curva, Offset(0f, -k * 0.05f)) }, colorDelFondo(lerp(C.ARENA, Color.White, 0.5f), a.y).copy(alpha = 0.4f * t), style = Stroke((k * 0.035f).coerceIn(0.25f, 1.1f), cap = StrokeCap.Round))
        }
        // Las praderas de hierba: masas de borde roto hechas de matas, más oscuras en el centro, con briznas en las de cerca.
        repeat(8) {
            val z = az.zEntre(zc * 1.02f, zl * 0.8f); val cx = x(z)
            val c = enElFondo(cx, z); val t = transparencia(c.y); if (t < 0.04f || c.x < zona.left - 60f || c.x > zona.right + 60f) return@repeat
            val k = focal / z; val rx = k * az.entre(2.4f, 5.5f); val ry = rx * ((ojo + hondo) / z).coerceAtMost(1f) * az.entre(0.55f, 0.85f)
            val hierba = colorDelFondo(lerp(C.HIERBA, C.MAR_HONDO, 0.35f), c.y); val clara = colorDelFondo(lerp(C.HIERBA, C.ARENA, 0.3f), c.y)
            // La sombra que la pradera deja en la arena, del lado contrario al sol.
            drawOval(colorDelFondo(C.MAR_HONDO, c.y).copy(alpha = 0.18f * t), Offset(c.x - rx + sx * rx * 0.12f, c.y - ry * 0.6f), Size(rx * 2.1f, ry * 1.5f))
            repeat(22) {
                val a = az.entre(0f, 6.28f); val d = sqrt(az.real()); val q = c + Offset(cos(a) * rx * d, sin(a) * ry * d)
                val rr = rx * az.entre(0.1f, 0.24f) * (1.2f - 0.5f * d)
                drawOval((if (az.real() > 0.3f) hierba else clara).copy(alpha = 0.6f * t), Offset(q.x - rr, q.y - rr * ry / rx * 1.4f), Size(rr * 2f, rr * 2.4f * ry / rx))
            }
            if (k > 10f) repeat(40) {
                val a = az.entre(0f, 6.28f); val d = sqrt(az.real()); val q = c + Offset(cos(a) * rx * d, sin(a) * ry * d)
                val lb = k * az.entre(0.2f, 0.45f); val cae = az.entre(-0.4f, 0.4f) + 0.2f * sx
                drawLine((if (az.real() > 0.5f) clara else hierba).copy(alpha = 0.6f * t), q, q + Offset(lb * cae, -lb * 0.55f), (k * 0.035f).coerceAtLeast(0.3f), StrokeCap.Round)
            }
        }
        // Las piedras: lomos de roca blanda, claros arriba y del lado del sol, con su sombra en la arena.
        repeat(16) {
            val z = az.zEntre(zc, zl * 0.7f); val c = enElFondo(x(z), z); val t = transparencia(c.y); if (t < 0.05f) return@repeat
            val k = focal / z; val w = k * az.entre(0.4f, 1.4f); val h = w * az.entre(0.35f, 0.6f)
            val roca = lerp(C.ARENA, C.PLOMO, az.entre(0.2f, 0.5f))
            drawOval(colorDelFondo(lerp(C.MAR_HONDO, C.SOMBRA, 0.3f), c.y).copy(alpha = 0.45f * t), Offset(c.x - w * 0.55f + sx * w * 0.35f, c.y - h * 0.15f), Size(w * 1.3f, h * 0.7f))
            val forma = lazo(listOf(Offset(c.x - w * 0.5f, c.y), Offset(c.x - w * 0.38f, c.y - h * 0.75f), Offset(c.x + w * 0.05f, c.y - h), Offset(c.x + w * 0.45f, c.y - h * 0.55f), Offset(c.x + w * 0.48f, c.y + h * 0.05f), Offset(c.x, c.y + h * 0.18f)))
            val luzR = colorDelFondo(lerp(roca, Color.White, 0.3f), c.y); val sombraR = colorDelFondo(lerp(roca, C.MAR_HONDO, 0.45f), c.y)
            drawPath(forma, Brush.linearGradient(listOf(luzR.copy(alpha = t), sombraR.copy(alpha = t)), Offset(c.x - sx * w * 0.4f, c.y - h), Offset(c.x + sx * w * 0.4f, c.y + h * 0.2f)))
            if (k > 12f) repeat(3) { val q = c + Offset(az.entre(-0.3f, 0.3f) * w, -az.entre(0.2f, 0.7f) * h); drawCircle(sombraR.copy(alpha = 0.5f * t), w * 0.05f, q) }
        }
        // Las esponjas vivas, «como cabezas negras», sueltas y en corros, cada una con su sombra.
        repeat(26) {
            val z = az.zEntre(zc, zl * 0.75f); val cx = x(z); val k0 = focal / z
            val cuantas = if (az.real() < 0.4f) 1 else 2 + az.entero(4)
            repeat(cuantas) {
                val xx = cx + az.entre(-0.9f, 0.9f) * (if (cuantas > 1) 1f else 0f); val zz = z * (1f + az.entre(-0.04f, 0.04f))
                val c = enElFondo(xx, zz); val t = transparencia(c.y); if (t < 0.05f) return@repeat
                val k = focal / zz; val rr = k * az.entre(0.12f, 0.26f)
                drawOval(colorDelFondo(C.MAR_HONDO, c.y).copy(alpha = 0.45f * t), Offset(c.x - rr * 0.9f + sx * rr * 0.8f, c.y - rr * 0.2f), Size(rr * 2.6f, rr * 0.8f))
                val forma = lazo(listOf(Offset(c.x - rr, c.y), Offset(c.x - rr * 0.9f, c.y - rr * 0.9f), Offset(c.x - rr * 0.2f, c.y - rr * 1.35f), Offset(c.x + rr * 0.6f, c.y - rr * 1.1f), Offset(c.x + rr, c.y - rr * 0.3f), Offset(c.x + rr * 0.3f, c.y + rr * 0.2f)))
                drawPath(forma, colorDelFondo(C.ESPONJA_VIVA, c.y).copy(alpha = 0.92f * t))
                if (rr > 1.6f) {
                    drawOval(colorDelFondo(lerp(C.ESPONJA_VIVA, C.ARENA, 0.45f), c.y).copy(alpha = 0.55f * t), Offset(c.x - rr * 0.5f - sx * rr * 0.2f, c.y - rr * 1.25f), Size(rr, rr * 0.4f))
                    drawCircle(colorDelFondo(C.BREA, c.y).copy(alpha = 0.7f * t), rr * 0.16f, Offset(c.x - sx * rr * 0.1f, c.y - rr * 1.1f))
                }
            }
        }
    }

    /** Una mancha de sombra en el fondo (en metros: x, z), del color frío del agua honda, con su transparencia. */
    private fun DrawScope.sombraEnElFondo(q: List<Offset>) {
        if (q.size < 3 || r.fuerza < 0.05f) return
        val pts = q.filter { it.y > 0.5f }.map { enElFondo(it.x, it.y) }
        if (pts.size < 3) return
        val forma = poligono(pts)
        val y = pts.sumOf { it.y.toDouble() }.toFloat() / pts.size
        val t = transparencia(y.coerceAtMost(horizonte + focal * 0.9f))
        drawPath(forma, l.tono(lerp(C.MAR_HONDO, C.SOMBRA, 0.25f)).copy(alpha = 0.42f * t * (r.fuerza / 0.72f).coerceAtMost(1f)))
    }

    /** Un banco de peces plateados a media agua («como una nube»), y debajo, en la arena, sus sombras. */
    private fun DrawScope.peces(zona: Rect, semilla: Long, y0: Float) {
        val az = Azar(semilla * 13 + 5)
        repeat(2) { banco ->
            val z = az.entre(zDelFondo(zona.bottom) * 0.75f, zDelFondo(zona.bottom) * 1.6f); val x = az.entre(0.35f, 0.75f) * z
            val mitad = ojo + hondo * 0.5f
            val c = Offset(centro + focal * x / z, horizonte + focal * mitad / z)
            val t = transparencia(c.y); if (t < 0.08f || !zona.contains(c)) return@repeat
            val k = focal / z
            val rumbo = az.entre(-0.6f, 0.6f) + (if (banco == 0) 0f else PI.toFloat())
            val sombraC = enElFondo(x - r.lat * hondo * 0.5f, z + hondo * 0.2f)
            val plata = l.tono(lerp(C.BLANCO, C.MAR, absorbe(c.y) * 0.5f))
            val lomo = l.tono(lerp(C.MAR_HONDO, C.PLOMO, 0.3f))
            repeat(12 + az.entero(9)) {
                val d = Offset(az.entre(-1.6f, 1.6f), az.entre(-0.45f, 0.45f)) * k
                val largo = k * az.entre(0.3f, 0.42f)
                val a = rumbo + az.entre(-0.15f, 0.15f)
                val dir = Offset(cos(a), sin(a) * 0.4f)
                val q = c + d
                val s = sombraC + d * 0.9f
                drawLine(colorDelFondo(C.MAR_HONDO, s.y).copy(alpha = 0.35f * t), s - dir * (largo * 0.5f), s + dir * (largo * 0.5f), largo * 0.32f, StrokeCap.Round)
                drawLine(lomo.copy(alpha = 0.8f * t), q - dir * (largo * 0.5f), q + dir * (largo * 0.5f), largo * 0.3f, StrokeCap.Round)
                drawLine(plata.copy(alpha = 0.75f * t), q - dir * (largo * 0.45f) + Offset(0f, largo * 0.06f), q + dir * (largo * 0.4f) + Offset(0f, largo * 0.06f), largo * 0.12f, StrokeCap.Round)
                // La cola.
                val cola = q - dir * (largo * 0.55f)
                drawLine(lomo.copy(alpha = 0.7f * t), cola, cola - dir * (largo * 0.18f) + Offset(0f, largo * 0.1f), largo * 0.1f, StrokeCap.Round)
                drawLine(lomo.copy(alpha = 0.7f * t), cola, cola - dir * (largo * 0.18f) - Offset(0f, largo * 0.1f), largo * 0.1f, StrokeCap.Round)
            }
        }
    }

    /** Los haces de sol que bajan por el agua clara: rayas claras inclinadas del lado del sol, que se juntan hacia la sombra de quien mira. */
    private fun DrawScope.haces(zona: Rect, y0: Float, semilla: Long) {
        if (r.fuerza < 0.08f) return
        val az = Azar(semilla * 3 + 1)
        val f = (r.fuerza / 0.72f).coerceAtMost(1f) * claridad
        val color = lerp(l.solColor, Color.White, 0.55f)
        val fuga = Offset(centro - r.lat * focal * 0.35f, zona.bottom + focal * 0.9f)    // el punto contrario al sol, abajo
        repeat(11) {
            val x0 = az.entre(zona.left - 40f, zona.right + 40f); val y = y0 + az.entre(-4f, 20f)
            val arriba = Offset(x0, y)
            val dir = (fuga - arriba).let { it / it.getDistance() }
            val largo = az.entre(70f, 170f); val ancho = az.entre(3f, 11f)
            val abajo = arriba + dir * largo
            val n = Offset(-dir.y, dir.x)
            val haz = poligono(arriba - n * (ancho * 0.3f), arriba + n * (ancho * 0.3f), abajo + n * ancho, abajo - n * ancho)
            val t = transparencia((arriba.y + abajo.y) / 2f)
            drawPath(haz, Brush.linearGradient(listOf(color.copy(alpha = 0f), color.copy(alpha = (0.13f * f * t).coerceIn(0f, 1f)), color.copy(alpha = 0f)), arriba, abajo), blendMode = BlendMode.Screen)
        }
    }

    /** La piel del agua: el cielo devuelto, que crece hacia el horizonte, los rizos claros y oscuros, y la sombra de las nubes. */
    private fun DrawScope.piel(zona: Rect, y0: Float, semillaDia: Long) {
        val cielo = lerp(l.bajo, l.medio, 0.45f)
        val alto = y0 + (zona.bottom - y0) * 0.55f
        // El reflejo del cielo que tapa el fondo hacia lejos (solo donde el fondo se veía).
        if (claridad * dia > 0.02f) {
            drawRect(Brush.verticalGradient(0f to cielo.copy(alpha = 0f), 0.35f to cielo.copy(alpha = 0.16f * claridad * dia), 0.7f to cielo.copy(alpha = 0.06f * claridad * dia), 1f to cielo.copy(alpha = 0f), startY = alto, endY = y0 - 8f), Offset(zona.left, y0 - 8f), Size(zona.width, alto - y0 + 8f))
        }
        // Los rizos de cerca: trazos curvos, más largos y más separados cuanto más cerca, con su lado de cielo y su lado oscuro.
        val az = Azar(semillaDia * 7 + 3)
        val calma = (1f - claridad).coerceIn(0f, 1f) * 0.5f + 0.5f
        val claro = lerp(l.bajo, Color.White, 0.35f); val oscuro = lerp(l.tono(C.MAR_HONDO), Color(0xFF0A1A2C), 0.25f)
        val cuantos = (90 * (0.35f + viento) * calma).toInt().coerceIn(18, 220)
        repeat(cuantos) {
            val k = az.real(); val y = y0 + (zona.bottom - y0) * k
            val x = az.entre(zona.left - 10f, zona.right)
            val largo = (4f + 26f * k) * az.entre(0.5f, 1.3f); val alza = 0.6f + 2.4f * k
            val a = (0.07f + 0.14f * az.real()) * (0.7f + 0.6f * viento) * (if (claridad > 0.9f) 0.65f else 1f)
            val c = if (az.real() > 0.45f) claro else oscuro
            drawPath(Path().apply { moveTo(x, y); cubicTo(x + largo * 0.3f, y - alza, x + largo * 0.7f, y - alza, x + largo, y + alza * 0.2f) }, c.copy(alpha = a.coerceIn(0f, 0.5f)), style = Stroke(0.5f + 1.1f * k, cap = StrokeCap.Round))
        }
        // Las sombras de las nubes sobre el agua («sus sombras corren sobre el agua»), los días de nubes.
        if (l.nublado in 0.3f..0.75f && dia > 0.3f) repeat(3) {
            val y = az.entre(horizonte + 6f, zona.bottom - 20f)
            mancha(Offset(az.entre(zona.left, zona.right), y), az.entre(60f, 130f) * (0.4f + (y - horizonte) / 120f), az.entre(5f, 14f) * (0.4f + (y - horizonte) / 80f), l.tono(C.MAR_HONDO), 0.22f)
        }
    }

    // ---------------------------------------------------------------- La parte viva

    /**
     * La red de luz que el sol hace en la arena del fondo (las cáusticas), que se mueve con el [reloj]: una malla de trazos
     * claros que tiemblan, solo donde se ve el fondo y fuera de [sombra] (la sombra del barco en el fondo, en el lienzo).
     * Barata: unas pocas decenas de nudos, sin capas.
     */
    fun DrawScope.redDeLuz(zona: Path, limites: Rect, reloj: Float, sombra: Path?) {
        if (r.fuerza < 0.1f || claridad < 0.2f) return
        val f = (r.fuerza / 0.72f).coerceAtMost(1f) * claridad
        val color = lerp(l.solColor, Color.White, 0.65f)
        val zc = zDelFondo(limites.bottom); val zl = zDelFondo(horizonte + focal * 0.34f)
        val az = Azar(77)
        fun dibujo(ds: DrawScope) = with(ds) {
            // Celdas de luz: anillos torcidos que tiemblan, juntos como las mallas de una red, chatos por la perspectiva.
            repeat(150) { i ->
                val z = zc * Math.pow((zl / zc).toDouble(), az.real().toDouble()).toFloat()
                val x = az.entre(-0.1f, 1.1f) * z * (limites.right - centro) / focal
                val c = enElFondo(x, z); val t = transparencia(c.y)
                if (t < 0.1f) { repeat(6) { az.real() }; return@repeat }
                val k = focal / z; val rr = k * az.entre(0.3f, 0.55f); val chato = ((ojo + hondo) / z).coerceIn(0.25f, 0.9f)
                val fase = az.entre(0f, 6.28f); val vel = az.entre(0.8f, 1.6f)
                val pts = (0 until 6).map { j ->
                    val a = j * 1.047f + 0.3f * sin(reloj * vel + fase + j)
                    val d = rr * (0.8f + 0.25f * sin(reloj * vel * 1.3f + fase * 2f + j * 2.1f))
                    Offset(c.x + cos(a) * d, c.y + sin(a) * d * chato)
                }
                val al = (0.26f * f * t * (0.55f + 0.45f * sin(reloj * 1.7f + i))).coerceIn(0f, 1f)
                drawPath(lazo(pts), color.copy(alpha = al), style = Stroke((k * 0.06f).coerceIn(0.35f, 1.6f), join = androidx.compose.ui.graphics.StrokeJoin.Round))
            }
        }
        clipPath(zona) { if (sombra != null) clipPath(sombra, ClipOp.Difference) { dibujo(this) } else dibujo(this) }
    }

    /**
     * El collar de burbujas del buzo: del fondo sube una columna que se ve por el agua clara y, arriba, en [c], las
     * burbujas revientan en la superficie en un corro que hierve. [k]: unidades por metro ahí.
     */
    fun DrawScope.burbujas(c: Offset, k: Float, reloj: Float, semilla: Int = 0) {
        val claro = lerp(l.bajo, Color.White, 0.6f)
        val az = Azar(semilla.toLong() * 7 + 11)
        // La columna que sube, vista a través del agua (abajo, más apagada).
        val t = transparencia(c.y + k * 3f)
        repeat(14) {
            val ph = ((reloj * az.entre(0.25f, 0.4f) + it / 14f) % 1f)
            val q = c + Offset(az.entre(-0.5f, 0.5f) * k + 0.15f * k * sin(reloj * 3f + it), (1f - ph) * k * 4.5f)
            drawCircle(claro.copy(alpha = (0.15f + 0.45f * t) * ph), (0.025f + 0.05f * ph) * k, q, style = Stroke(0.4f))
        }
        // El hervor de la superficie: anillos que nacen y se abren.
        repeat(12) {
            val ph = ((reloj * az.entre(0.5f, 0.9f) + it / 12f) % 1f)
            val q = c + Offset(az.entre(-1f, 1f) * k * 0.9f, az.entre(-0.3f, 0.3f) * k * 0.5f)
            val rr = (0.03f + 0.12f * ph) * k
            drawOval(claro.copy(alpha = 0.7f * (1f - ph)), Offset(q.x - rr, q.y - rr * 0.4f), Size(rr * 2f, rr * 0.8f), style = Stroke(0.5f))
        }
        drawOval(claro.copy(alpha = 0.18f), Offset(c.x - k, c.y - k * 0.2f), Size(k * 2f, k * 0.4f))
    }

    /**
     * El verde que brilla de noche donde algo toca el agua («el agua alrededor del barco se ilumina de verde cuando la
     * tocas»): en cada uno de los [puntos] (el casco, el bote, la cadena del ancla, la manguera), unas chispas que se
     * encienden y se apagan, y unas pocas sueltas en el agua de cerca. Luz propia, sin capas.
     */
    fun DrawScope.verdeVivo(puntos: List<Offset>, zona: Rect, reloj: Float, fuerza: Float) {
        if (fuerza <= 0.02f) return
        val az = Azar(91)
        val v = C.VERDE_VIVO
        for ((i, p) in puntos.withIndex()) repeat(5) {
            val ph = sin(reloj * az.entre(1.2f, 2.6f) + i * 1.3f + it * 2.1f).let { it * it }
            val q = p + Offset(az.entre(-7f, 7f), az.entre(-1.5f, 2.5f))
            val rr = az.entre(0.6f, 1.6f)
            drawCircle(v.copy(alpha = (0.18f * fuerza * ph).coerceIn(0f, 1f)), rr * 3f, q, blendMode = BlendMode.Plus)
            drawCircle(lerp(v, Color.White, 0.4f).copy(alpha = (0.6f * fuerza * ph).coerceIn(0f, 1f)), rr * 0.6f, q, blendMode = BlendMode.Plus)
        }
        repeat(16) {
            val q = Offset(az.entre(zona.left, zona.right), az.entre(horizonte + (zona.bottom - horizonte) * 0.45f, zona.bottom))
            val ph = sin(reloj * az.entre(0.6f, 1.4f) + it * 0.9f).let { if (it > 0.7f) (it - 0.7f) / 0.3f else 0f }
            if (ph > 0f) drawCircle(v.copy(alpha = (0.5f * fuerza * ph).coerceIn(0f, 1f)), 0.7f + 0.5f * abs(sin(it.toFloat())), q, blendMode = BlendMode.Plus)
        }
    }
}
