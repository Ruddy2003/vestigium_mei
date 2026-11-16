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
import androidx.compose.ui.graphics.StrokeJoin
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
import kotlin.math.atan
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.pow
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
 * chico que lo que flota, y eso es lo que da la hondura. Lo que nada entre los dos ([sobre]) queda en medio. Cuánto fondo
 * se ve depende de cuánto se mira hacia abajo ([transparencia]: el reflejo de Fresnel, que cerca de la borda casi no
 * devuelve nada y hacia el horizonte lo devuelve todo), de la claridad del día ([claridad]: la calma, el agua clara, el
 * norte que la enturbia) y de la luz (de noche, nada). Con la distancia, el fondo se enfría y pierde contraste hacia el
 * verde botella ([absorbe]); lo que nada, también, según cuánta agua tiene encima.
 *
 * **La luz dentro del agua.** El sol entra quebrado, más empinado que en el aire ([corre]: la ley de Snell con el agua a
 * 1.33; la dirección, la de las sombras de la cubierta): la sombra de lo que flota o nada cae en la arena corrida lejos del
 * sol, y tanto más cuanto más agua hay en medio. Así la sombra del barco y la del bote caminan por el fondo con la hora, y
 * cada pez tiene la suya, más borrosa cuanto más alto nada.
 *
 * Las capas, de abajo arriba ([pintar]): el mar de la biblioteca (`agua`), que lejos sigue siendo el de siempre; el fondo
 * (la arena con sus rizos, las praderas de hierba, las piedras, las esponjas como cabezas negras); las sombras en el fondo
 * (el barco, el bote y lo que diga el cuadro); los bancos de peces con sus sombras ([peces]); lo que el cuadro hunde en el
 * agua (la cadena del ancla, la manguera del buzo: [hundido]); los haces de sol que bajan; y encima, la piel del agua: el
 * cielo devuelto que crece hacia lejos, la calle de la luna ([calleDeLuna]) y los rizos. En la parte viva: la red de luz
 * que el sol hace en la arena ([redDeLuz]), las burbujas del buzo ([burbujas]) y el verde que brilla de noche donde algo
 * toca el agua ([verdeVivo]). Todo sin capas (`saveLayer`): lo que se ve del fondo se pinta con su transparencia en el
 * color.
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

    /** Un punto a [d] metros bajo la superficie (lo que nada, lo que se hunde) en el lienzo. */
    fun sobre(x: Float, d: Float, z: Float): Offset = Offset(centro + focal * x / z.coerceAtLeast(0.2f), horizonte + focal * (ojo + d) / z.coerceAtLeast(0.2f))

    /** A qué profundidad (z) está el fondo que se ve en la altura [y]. */
    fun zDelFondo(y: Float): Float = focal * (ojo + hondo) / (y - horizonte).coerceAtLeast(0.5f)

    /** A qué profundidad (z) está lo que nada a [d] metros bajo la superficie y se ve en la altura [y]. */
    fun zA(y: Float, d: Float): Float = focal * (ojo + d) / (y - horizonte).coerceAtLeast(0.5f)

    /** El color propio del fondo de arena visto en la altura [y]: la arena tibia cerca, el verde botella lejos. */
    fun colorDelFondo(propio: Color, y: Float): Color = l.tono(lerp(lerp(propio, lerp(C.MAR_CLARO, C.MAR, 0.35f), 0.3f), C.MAR, 0.08f + absorbe(y) * 0.86f))

    /** El color de algo que nada a [d] metros bajo la superficie, visto en la altura [y]: cuanta más agua encima, más verde. */
    fun colorEnElAgua(propio: Color, d: Float, y: Float): Color =
        l.tono(lerp(propio, C.MAR, (0.12f + 0.42f * d / hondo + 0.45f * absorbe(y)).coerceIn(0f, 0.92f)))

    /**
     * Hacia dónde y cuánto se corre, por cada metro de agua que atraviesa la luz, la sombra de lo que flota o nada (en
     * metros de la cámara: x, z). Lejos del sol y hacia el fondo del cuadro, como las sombras de la cubierta, pero más
     * empinada: el sol entra en el agua quebrado (Snell, con el agua a 1.33). Con el sol alto casi no se corre; con el sol
     * bajo, como mucho un metro por metro.
     */
    val corre: Offset by lazy {
        val s = Sol3(r); val n = hypot(s.sx, s.sz).coerceAtLeast(0.001f)
        val e = atan(r.tan); val ce = cos(e)
        val tw = (ce / sqrt((1.769f - ce * ce).coerceAtLeast(0.05f))).coerceAtMost(1.05f)
        Offset(s.sx / n * tw, s.sz / n * tw)
    }

    // ---------------------------------------------------------------- La parte quieta

    /**
     * El agua en [zona] (con el borde de arriba en el horizonte): el mar de siempre y, encima, lo que se ve del fondo y
     * la piel del agua. [sombras]: las manchas de sombra en el fondo, en metros (x, z), del barco, del bote y de lo que
     * flote. [semilla]: el fondo del banco, que no cambia de un día a otro; [semillaDia], los peces y la piel, que sí.
     * [hundido]: lo que el cuadro mete en el agua (la cadena, la manguera), que va entre los peces y la piel.
     * [desde]: el borde izquierdo del agua que se ve en cada altura (para no pintar lo que tapa la cubierta).
     */
    fun DrawScope.pintar(zona: Rect, semilla: Long, sombras: List<List<Offset>>, semillaDia: Long, desde: (Float) -> Float = { zona.left }, hundido: (DrawScope.() -> Unit)? = null) {
        // 1. El mar de la biblioteca: lejos es el de siempre.
        agua(l, zona, Azar(semillaDia), propio = C.MAR, viento = viento, hondo = C.MAR_HONDO, orilla = C.MAR_CLARO)
        val y0 = horizonte + focal * 0.12f
        if (transparencia(zona.bottom) > 0.02f) {
            // 2. El fondo: la arena, con su transparencia en el color (sin capas).
            val paradas = (0..8).map { i -> val y = y0 + (zona.bottom - y0) * i / 8f; (i / 8f) to colorDelFondo(C.ARENA, y).copy(alpha = transparencia(y)) }.toTypedArray()
            drawRect(Brush.verticalGradient(*paradas, startY = y0, endY = zona.bottom), Offset(zona.left, y0), Size(zona.width, zona.bottom - y0))
            fondoDelBanco(zona, y0, semilla, desde)
            for (q in sombras) sombraEnElFondo(q)
            peces(zona, semillaDia, desde)
        }
        hundido?.invoke(this)
        if (transparencia(zona.bottom) > 0.02f) haces(zona, y0, semilla)
        // 3. La piel del agua: el cielo devuelto, que crece hacia lejos (Fresnel), y los rizos encima de todo.
        piel(zona, y0, semillaDia)
    }

    /** Una profundidad (z) del fondo al azar entre [z0] y [z1], con más cerca que lejos (cada metro de cerca ocupa más en el lienzo). */
    private fun Azar.zEntre(z0: Float, z1: Float): Float = z0 * Math.pow((z1 / z0).toDouble(), real().toDouble()).toFloat()

    /** El fondo del banco: los rizos de la arena, las praderas de hierba, las piedras y las esponjas, con su sombra. */
    private fun DrawScope.fondoDelBanco(zona: Rect, y0: Float, semilla: Long, desde: (Float) -> Float) {
        val az = Azar(semilla)
        val sx = -r.lat.coerceIn(-1f, 1f)                                         // hacia dónde caen las sombras del fondo
        val zc = zDelFondo(zona.bottom); val zl = zDelFondo(y0 + 6f)
        fun x(z: Float) = (az.entre(-0.2f, 1.15f)) * z * (zona.right - centro) / focal
        fun fuera(a: Offset, margen: Float) = a.x < desde(a.y) - margen
        // Los rizos de la arena: rayas onduladas, paralelas, con su cara de luz y su cara de sombra.
        repeat(170) {
            val z = az.zEntre(zc, zl); val a = enElFondo(x(z), z); if (!zona.contains(a) || fuera(a, 30f)) return@repeat
            val t = transparencia(a.y); if (t < 0.05f) return@repeat
            val k = focal / z; val w = k * az.entre(0.7f, 1.8f)
            val curva = Path().apply { moveTo(a.x - w, a.y + w * 0.04f); cubicTo(a.x - w * 0.4f, a.y - w * 0.12f, a.x + w * 0.3f, a.y + w * 0.1f, a.x + w, a.y - w * 0.06f) }
            drawPath(curva, colorDelFondo(lerp(C.ARENA, C.MADERA_OSC, 0.45f), a.y).copy(alpha = 0.3f * t), style = Stroke((k * 0.05f).coerceIn(0.3f, 1.6f), cap = StrokeCap.Round))
            drawPath(Path().apply { addPath(curva, Offset(0f, -k * 0.05f)) }, colorDelFondo(lerp(C.ARENA, Color.White, 0.5f), a.y).copy(alpha = 0.36f * t), style = Stroke((k * 0.035f).coerceIn(0.25f, 1.1f), cap = StrokeCap.Round))
        }
        // Las praderas de hierba: masas de borde roto, más oscuras en el centro, con briznas que se tumban con la corriente.
        repeat(9) {
            val z = az.zEntre(zc * 1.02f, zl * 0.8f); val cx = x(z)
            val c = enElFondo(cx, z); val t = transparencia(c.y); if (t < 0.04f || c.x < zona.left - 60f || c.x > zona.right + 60f || fuera(c, 60f)) return@repeat
            val k = focal / z; val rx = k * az.entre(2.4f, 5.5f); val ry = rx * ((ojo + hondo) / z).coerceAtMost(1f) * az.entre(0.55f, 0.85f)
            val hierba = colorDelFondo(lerp(C.HIERBA, C.MAR_HONDO, 0.35f), c.y); val clara = colorDelFondo(lerp(C.HIERBA, C.ARENA, 0.3f), c.y)
            // La masa: un contorno a mano, de muchos puntos, con entrantes y salientes.
            val n = 14; val borde = (0 until n).map { i -> val a = i * 2f * PI.toFloat() / n; val rr = az.entre(0.62f, 1.1f); c + Offset(cos(a) * rx * rr, sin(a) * ry * rr) }
            drawPath(lazo(borde.map { it + Offset(sx * rx * 0.1f, ry * 0.12f) }), colorDelFondo(C.MAR_HONDO, c.y).copy(alpha = 0.16f * t))   // su sombra
            drawPath(lazo(borde), hierba.copy(alpha = 0.5f * t))
            drawPath(lazo(borde.map { c + (it - c) * 0.55f }), lerp(hierba, colorDelFondo(C.MAR_HONDO, c.y), 0.35f).copy(alpha = 0.45f * t))
            repeat(16) {
                val a = az.entre(0f, 6.28f); val d = sqrt(az.real()); val q = c + Offset(cos(a) * rx * d, sin(a) * ry * d)
                val rr = rx * az.entre(0.08f, 0.18f)
                drawOval(clara.copy(alpha = 0.32f * t), Offset(q.x - rr, q.y - rr * ry / rx), Size(rr * 2f, rr * 1.6f * ry / rx))
            }
            if (k > 10f) repeat(46) {
                val a = az.entre(0f, 6.28f); val d = sqrt(az.real()) * 1.05f; val q = c + Offset(cos(a) * rx * d, sin(a) * ry * d)
                val lb = k * az.entre(0.2f, 0.45f); val cae = az.entre(-0.3f, 0.5f) + 0.2f * sx
                drawLine((if (az.real() > 0.5f) clara else hierba).copy(alpha = 0.55f * t), q, q + Offset(lb * cae, -lb * 0.55f), (k * 0.035f).coerceAtLeast(0.3f), StrokeCap.Round)
            }
        }
        // Las piedras: lomos de roca blanda, claros arriba y del lado del sol, con su sombra en la arena.
        repeat(18) {
            val z = az.zEntre(zc, zl * 0.7f); val c = enElFondo(x(z), z); val t = transparencia(c.y); if (t < 0.05f || fuera(c, 20f)) return@repeat
            val k = focal / z; val w = k * az.entre(0.4f, 1.4f); val h = w * az.entre(0.35f, 0.6f)
            val roca = lerp(C.ARENA, C.PLOMO, az.entre(0.2f, 0.5f))
            drawOval(colorDelFondo(lerp(C.MAR_HONDO, C.SOMBRA, 0.3f), c.y).copy(alpha = 0.45f * t), Offset(c.x - w * 0.55f + sx * w * 0.35f, c.y - h * 0.15f), Size(w * 1.3f, h * 0.7f))
            val forma = lazo(listOf(Offset(c.x - w * 0.5f, c.y), Offset(c.x - w * 0.38f, c.y - h * 0.75f), Offset(c.x + w * 0.05f, c.y - h), Offset(c.x + w * 0.45f, c.y - h * 0.55f), Offset(c.x + w * 0.48f, c.y + h * 0.05f), Offset(c.x, c.y + h * 0.18f)))
            val luzR = colorDelFondo(lerp(roca, Color.White, 0.3f), c.y); val sombraR = colorDelFondo(lerp(roca, C.MAR_HONDO, 0.45f), c.y)
            drawPath(forma, Brush.linearGradient(listOf(luzR.copy(alpha = t), sombraR.copy(alpha = t)), Offset(c.x - sx * w * 0.4f, c.y - h), Offset(c.x + sx * w * 0.4f, c.y + h * 0.2f)))
            if (k > 12f) repeat(3) { val q = c + Offset(az.entre(-0.3f, 0.3f) * w, -az.entre(0.2f, 0.7f) * h); drawCircle(sombraR.copy(alpha = 0.5f * t), w * 0.05f, q) }
        }
        // Las esponjas vivas, «como cabezas negras», sueltas y en corros, cada una con su sombra.
        repeat(30) {
            val z = az.zEntre(zc, zl * 0.75f); val cx = x(z)
            val cuantas = if (az.real() < 0.4f) 1 else 2 + az.entero(4)
            repeat(cuantas) {
                val xx = cx + az.entre(-0.9f, 0.9f) * (if (cuantas > 1) 1f else 0f); val zz = z * (1f + az.entre(-0.04f, 0.04f))
                val c = enElFondo(xx, zz); val t = transparencia(c.y); if (t < 0.05f || fuera(c, 10f)) return@repeat
                val k = focal / zz; val rr = k * az.entre(0.12f, 0.28f)
                drawOval(colorDelFondo(C.MAR_HONDO, c.y).copy(alpha = 0.45f * t), Offset(c.x - rr * 0.9f + sx * rr * 0.8f, c.y - rr * 0.2f), Size(rr * 2.6f, rr * 0.8f))
                val forma = lazo(listOf(Offset(c.x - rr, c.y), Offset(c.x - rr * 0.9f, c.y - rr * 0.9f), Offset(c.x - rr * 0.2f, c.y - rr * 1.35f), Offset(c.x + rr * 0.6f, c.y - rr * 1.1f), Offset(c.x + rr, c.y - rr * 0.3f), Offset(c.x + rr * 0.3f, c.y + rr * 0.2f)))
                drawPath(forma, Brush.linearGradient(listOf(colorDelFondo(lerp(C.ESPONJA_VIVA, C.HIERBA, 0.3f), c.y).copy(alpha = 0.92f * t), colorDelFondo(C.ESPONJA_VIVA, c.y).copy(alpha = 0.92f * t)), Offset(c.x - sx * rr, c.y - rr * 1.3f), Offset(c.x + sx * rr, c.y)))
                if (rr > 1.6f) {
                    drawCircle(colorDelFondo(C.BREA, c.y).copy(alpha = 0.7f * t), rr * 0.18f, Offset(c.x - sx * rr * 0.1f, c.y - rr * 1.05f))
                    drawArc(colorDelFondo(lerp(C.ESPONJA_VIVA, C.ARENA, 0.5f), c.y).copy(alpha = 0.5f * t), 200f, 90f, false, Offset(c.x - rr * 0.8f, c.y - rr * 1.3f), Size(rr * 1.6f, rr * 1.2f), style = Stroke(rr * 0.12f))
                }
            }
        }
    }

    /**
     * Una mancha de sombra en el fondo (en metros: x, z), del color frío del agua honda, con su transparencia: el núcleo y
     * la penumbra (la luz que entra quebrada por la piel del agua no deja bordes netos a cuatro brazas).
     */
    private fun DrawScope.sombraEnElFondo(q: List<Offset>) {
        if (q.size < 3 || r.fuerza < 0.05f) return
        val pts = q.filter { it.y > 0.5f }.map { enElFondo(it.x, it.y) }
        if (pts.size < 3) return
        val forma = poligono(pts)
        val y = pts.maxOf { it.y }.coerceAtMost(horizonte + focal * 0.9f)
        val t = transparencia(y)
        val f = 0.55f * t * (r.fuerza / 0.72f).coerceAtMost(1f)
        val color = l.tono(lerp(C.MAR_HONDO, C.SOMBRA, 0.3f))
        drawPath(forma, color.copy(alpha = 0.22f * f), style = Stroke(10f, join = StrokeJoin.Round))
        drawPath(forma, color.copy(alpha = 0.3f * f), style = Stroke(4.5f, join = StrokeJoin.Round))
        drawPath(forma, color.copy(alpha = f))
    }

    // ---------------------------------------------------------------- Los peces

    /** Un banco: dónde se ve su centro en el lienzo ([x], [y]), a cuántos metros bajo la superficie nada ([d]), cuántos peces, de qué largo, hacia dónde y cuánto se abre. */
    private class Banco(val x: Float, val y: Float, val d: Float, val n: Int, val largo: Float, val rumbo: Float, val ancho: Float, val plata: Float)

    /**
     * Los bancos de peces del día, cada uno con su rumbo, su hondura y su tamaño, y cada pez con su sombra en la arena: «un
     * banco de peces plateados por encima de su cabeza, como una nube». La nube de los chicos, plateados, a media agua; unos
     * sargos más grandes cerca del fondo, más apagados; un pez grande solo junto a las esponjas; y lejos, una mancha de
     * alevines que apenas se ve. Cambian de un día a otro ([semillaDia]).
     */
    private fun DrawScope.peces(zona: Rect, semillaDia: Long, desde: (Float) -> Float) {
        val az = Azar(semillaDia * 13 + 5)
        val bancos = listOf(
            Banco(az.entre(262f, 330f), az.entre(178f, 230f), hondo * az.entre(0.22f, 0.34f), 26 + az.entero(12), 0.34f, az.entre(-0.5f, 0.5f) + (if (az.real() > 0.5f) PI.toFloat() else 0f), 1.4f, 1f),
            Banco(az.entre(296f, 360f), az.entre(232f, 268f), hondo * az.entre(0.62f, 0.75f), 5 + az.entero(4), 0.6f, az.entre(-0.7f, 0.7f) + (if (az.real() > 0.5f) PI.toFloat() else 0f), 1.0f, 0.4f),
            Banco(az.entre(262f, 300f), az.entre(255f, 280f), hondo * 0.85f, 1, 0.9f, az.entre(0f, 6.28f), 0f, 0.2f),
            Banco(az.entre(305f, 370f), az.entre(160f, 180f), hondo * 0.4f, 16 + az.entero(8), 0.26f, az.entre(-0.4f, 0.4f), 2.2f, 0.8f),
        )
        val c = corre
        for (b in bancos) {
            if (b.x < desde(b.y) + 14f) continue
            val z0 = zA(b.y, b.d); val x0 = (b.x - centro) * z0 / focal
            val ca = cos(b.rumbo); val sa = sin(b.rumbo)
            // Primero las sombras de todo el banco, en la arena; luego los peces encima.
            val peces = (0 until b.n).map {
                val g1 = (az.real() + az.real() + az.real()) / 1.5f - 1f; val g2 = (az.real() + az.real() + az.real()) / 1.5f - 1f
                val a = g1 * b.ancho; val t = g2 * b.ancho * 0.45f                 // a lo largo del rumbo y de través
                val dx = a * ca - t * sa; val dz = a * sa + t * ca
                val d = (b.d + az.entre(-0.35f, 0.35f) * (if (b.n > 1) 1f else 0f)).coerceIn(0.3f, hondo - 0.25f)
                val rumbo = b.rumbo + az.entre(-0.22f, 0.22f) + (if (az.real() < 0.08f) az.entre(-0.7f, 0.7f) else 0f)
                floatArrayOf(x0 + dx, z0 + dz, d, b.largo * az.entre(0.78f, 1.22f), rumbo)
            }
            for (p in peces) {
                val alto = hondo - p[2]
                val s = enElFondo(p[0] + c.x * alto, p[1] + c.y * alto); val t = transparencia(s.y)
                if (t < 0.05f || s.x < desde(s.y) - 4f || r.fuerza < 0.05f) continue
                val k = focal / (p[1] + c.y * alto); val chato = (ojo + hondo) / (p[1] + c.y * alto)
                val dir = Offset(cos(p[4]), sin(p[4]) * chato)
                val lg = p[3] * k * (1f + 0.08f * alto)
                drawLine(colorDelFondo(lerp(C.MAR_HONDO, C.SOMBRA, 0.25f), s.y).copy(alpha = (0.42f - 0.05f * alto) * t * (r.fuerza / 0.72f).coerceAtMost(1f)), s - dir * (lg * 0.42f), s + dir * (lg * 0.42f), lg * (0.2f + 0.04f * alto), StrokeCap.Round)
            }
            for (p in peces) {
                val q = sobre(p[0], p[2], p[1]); val t = transparencia(q.y)
                if (t < 0.06f || q.x < desde(q.y) - 2f) continue
                pez(q, p[3] * focal / p[1], p[4], (ojo + p[2]) / p[1], p[2], b.plata, t)
            }
        }
    }

    /**
     * Un pez visto desde arriba y de lado, en [c], de [lg] unidades de largo, con el rumbo [a] (en el plano del agua) y el
     * escorzo [chato] de ese plano: el lomo oscuro, el flanco plateado del lado que mira a quien mira (con su brillo del
     * lado del sol, más vivo en los plateados: [plata]), la cola en horquilla; a [d] metros de agua, más verde.
     */
    private fun DrawScope.pez(c: Offset, lg: Float, a: Float, chato: Float, d: Float, plata: Float, t: Float) {
        if (lg < 1.2f) return
        val D = Offset(cos(a), sin(a) * chato) * lg; var N = Offset(-sin(a), cos(a) * chato) * lg
        if (N.y < 0f) N = -N                                                     // el flanco que mira a quien mira
        fun q(s: Float, w: Float) = c + D * s + N * w
        val lomo = colorEnElAgua(lerp(C.MAR_HONDO, C.PLOMO, 0.35f), d, c.y)
        val flanco = colorEnElAgua(lerp(lerp(C.BLANCO, C.MAR_CLARO, 0.3f), C.PLOMO, 0.4f * (1f - plata)), d, c.y)
        val brillo = lerp(flanco, lerp(l.bajo, Color.White, 0.6f), 0.55f * plata * r.contraste)
        val cuerpo = lazo(listOf(q(0.5f, 0f), q(0.3f, 0.12f), q(0f, 0.14f), q(-0.3f, 0.07f), q(-0.42f, 0.025f), q(-0.42f, -0.025f), q(-0.3f, -0.07f), q(0f, -0.13f), q(0.3f, -0.11f)))
        val cola = poligono(q(-0.38f, 0f), q(-0.56f, 0.13f), q(-0.5f, 0f), q(-0.56f, -0.13f))
        drawPath(cola, lomo.copy(alpha = 0.85f * t))
        drawPath(cuerpo, lomo.copy(alpha = 0.9f * t))
        if (lg < 3f) { drawLine(brillo.copy(alpha = 0.7f * t), q(0.3f, 0.06f), q(-0.25f, 0.05f), lg * 0.08f, StrokeCap.Round); return }
        drawPath(lazo(listOf(q(0.42f, 0.03f), q(0.2f, 0.12f), q(-0.05f, 0.135f), q(-0.32f, 0.06f), q(-0.1f, 0.05f), q(0.2f, 0.035f))), flanco.copy(alpha = 0.9f * t))
        drawLine(brillo.copy(alpha = 0.8f * t), q(0.28f, 0.08f), q(-0.18f, 0.09f), lg * 0.035f, StrokeCap.Round)
        drawLine(lerp(lomo, Color.Black, 0.2f).copy(alpha = 0.6f * t), q(0.3f, -0.02f), q(-0.25f, -0.01f), lg * 0.03f, StrokeCap.Round)   // el espinazo
        drawCircle(lerp(lomo, Color.Black, 0.35f).copy(alpha = 0.8f * t), lg * 0.025f, q(0.38f, 0.05f))
    }

    // ---------------------------------------------------------------- Lo que se hunde

    /**
     * Una cuerda, una manguera o una cadena que entra en el agua por los [puntos] (x, d, z: metros de la cámara, d bajo la
     * superficie) y se pierde: más verde y más tenue cuanto más hondo, hasta que el agua se la come.
     */
    fun DrawScope.hundido(puntos: List<V3>, propio: Color, grueso: Float, cadena: Boolean = false) {
        for (i in 0 until puntos.size - 1) {
            val a = puntos[i]; val b = puntos[i + 1]
            val pa = sobre(a.x, a.y, a.z); val pb = sobre(b.x, b.y, b.z)
            val d = (a.y + b.y) / 2f
            val al = ((1f - d / (hondo * 1.05f)) * (0.35f + 0.65f * transparencia(pb.y))).coerceIn(0f, 1f) * (0.4f + 0.6f * dia)
            if (al < 0.03f) continue
            val g = (grueso * focal / ((a.z + b.z) / 2f)).coerceAtLeast(0.35f)
            val col = colorEnElAgua(propio, d, pb.y)
            drawLine(col.copy(alpha = al), pa, pb, g, StrokeCap.Round)
            if (cadena && g > 0.8f) drawLine(lerp(col, Color.White, 0.25f).copy(alpha = al * 0.5f), pa, pb, g * 0.35f, StrokeCap.Round)
        }
    }

    /** Los haces de sol que bajan por el agua clara: franjas claras que se juntan hacia el punto contrario al sol, bajo quien mira. */
    private fun DrawScope.haces(zona: Rect, y0: Float, semilla: Long) {
        if (r.fuerza < 0.08f) return
        val az = Azar(semilla * 3 + 1)
        val f = (r.fuerza / 0.72f).coerceAtMost(1f) * claridad
        val color = lerp(l.solColor, Color.White, 0.55f)
        val fuga = Offset(centro - r.lat * focal * 0.35f, zona.bottom + focal * 0.9f)    // el punto contrario al sol, abajo
        repeat(9) {
            val x0 = az.entre(zona.left + 60f, zona.right + 40f); val y = y0 + az.entre(-4f, 20f)
            val arriba = Offset(x0, y)
            val dir = (fuga - arriba).let { it / it.getDistance() }
            val largo = az.entre(70f, 170f); val ancho = az.entre(3f, 11f)
            val abajo = arriba + dir * largo
            val n = Offset(-dir.y, dir.x)
            val haz = poligono(arriba - n * (ancho * 0.3f), arriba + n * (ancho * 0.3f), abajo + n * ancho, abajo - n * ancho)
            val t = transparencia((arriba.y + abajo.y) / 2f)
            drawPath(haz, Brush.linearGradient(listOf(color.copy(alpha = 0f), color.copy(alpha = (0.11f * f * t).coerceIn(0f, 1f)), color.copy(alpha = 0f)), arriba, abajo), blendMode = BlendMode.Screen)
        }
    }

    /**
     * La piel del agua: el cielo devuelto, que crece hacia el horizonte (con el claro de las nubes del día lejos), los rizos
     * claros y oscuros, y la sombra de las nubes.
     */
    private fun DrawScope.piel(zona: Rect, y0: Float, semillaDia: Long) {
        val cielo = lerp(l.bajo, l.medio, 0.45f)
        val alto = y0 + (zona.bottom - y0) * 0.55f
        // El reflejo del cielo que tapa el fondo hacia lejos (solo donde el fondo se veía).
        if (claridad * dia > 0.02f) {
            drawRect(Brush.verticalGradient(0f to cielo.copy(alpha = 0f), 0.35f to cielo.copy(alpha = 0.18f * claridad * dia), 0.7f to cielo.copy(alpha = 0.08f * claridad * dia), 1f to cielo.copy(alpha = 0f), startY = alto, endY = y0 - 8f), Offset(zona.left, y0 - 8f), Size(zona.width, alto - y0 + 8f))
        }
        val az = Azar(semillaDia * 7 + 3)
        // Las nubes devueltas: manchas claras tendidas, lejos, donde el agua ya es espejo.
        if (dia > 0.3f && l.nublado in 0.05f..0.8f) repeat(5) {
            val y = horizonte + az.entre(3f, 26f); val w = az.entre(30f, 80f) * (0.5f + (y - horizonte) / 30f)
            mancha(Offset(az.entre(zona.left + 40f, zona.right), y), w, az.entre(1.2f, 3f) * (0.5f + (y - horizonte) / 20f), lerp(l.bajo, Color.White, 0.5f), 0.16f * dia)
        }
        // Los rizos de cerca: trazos curvos, más largos y más separados cuanto más cerca, con su lado de cielo y su lado oscuro.
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

    /**
     * La calle de la luna en el agua: una columna de trazos de plata bajo la luna, estrecha junto al horizonte y abierta
     * hacia quien mira, más viva en el centro, con el disco devuelto en el horizonte. [fuerza], de 0 a 1 (la luna que
     * alumbra: de noche y sin nubes). [hasta]: dónde se corta (la borda).
     */
    fun DrawScope.calleDeLuna(zona: Rect, fuerza: Float, semilla: Long, desde: (Float) -> Float) {
        if (fuerza < 0.03f) return
        val x0 = l.lunaX * zona.right
        val az = Azar(semilla)
        val plata = Color(0xFFE6EEFF)
        mancha(Offset(x0, horizonte + 2f), 22f, 2.2f, plata, 0.5f * fuerza)
        repeat(170) {
            val k = az.real().pow(1.4f); val y = horizonte + 1.5f + (zona.bottom - horizonte) * k
            val ancho = 6f + 90f * k
            val u = (az.real() + az.real() + az.real()) / 1.5f - 1f
            val x = x0 + u * ancho
            if (x < desde(y) + 2f) return@repeat
            val largo = (1.2f + 9f * k) * az.entre(0.5f, 1.3f)
            val a = 0.85f * fuerza * az.entre(0.3f, 1f) * (1f - 0.75f * abs(u)) * (1f - 0.35f * k)
            drawLine(plata.copy(alpha = a.coerceIn(0f, 1f)), Offset(x - largo / 2f, y), Offset(x + largo / 2f, y), 0.5f + 1.1f * k, StrokeCap.Round, blendMode = BlendMode.Screen)
        }
    }

    // ---------------------------------------------------------------- La parte viva

    /** Un número de 0 a 1 que sale fijo de tres enteros (para que la red de luz no baile de forma al cambiar de fila). */
    private fun ruido(i: Int, j: Int, k: Int): Float { val s = sin(i * 12.9898f + j * 78.233f + k * 37.719f) * 43758.547f; return s - floor(s) }

    /**
     * La red de luz que el sol hace en la arena del fondo (las cáusticas): una malla de hilos de luz que tiemblan, con las
     * mallas más grandes cerca y más chicas lejos, sobre el fondo que se ve y fuera de las [sombras] (la del barco y la del
     * bote en el fondo, ya en el lienzo). Los nudos se mueven despacio con el [reloj]. [desde]: el borde izquierdo del agua
     * en cada altura. Barata: tres trazos (uno por distancia), sin capas.
     */
    fun DrawScope.redDeLuz(zona: Path, limites: Rect, reloj: Float, sombras: List<Path>, desde: (Float) -> Float) {
        if (r.fuerza < 0.1f || claridad < 0.2f || dia < 0.2f) return
        val f = (r.fuerza / 0.72f).coerceAtMost(1f) * claridad
        val color = lerp(l.solColor, Color.White, 0.62f)
        val zc = zDelFondo(limites.bottom) * 0.97f; val zl = zDelFondo(horizonte + focal * 0.3f)
        val bandas = Array(3) { Path() }
        fun nudo(i: Int, j: Int, z: Float, paso: Float): Offset {
            val ph = ruido(i, j, 2) * 6.28f; val v = 0.5f + 0.6f * ruido(i, j, 3)
            return Offset((i + (ruido(i, j, 0) - 0.5f) * 0.8f) * paso + 0.2f * paso * sin(reloj * v + ph), z + (ruido(i, j, 1) - 0.5f) * 0.6f * paso + 0.16f * paso * cos(reloj * v * 0.8f + ph * 1.3f))
        }
        fun arista(b: Int, a: Offset, c: Offset, fase: Float) {
            val pa = enElFondo(a.x, a.y); val pc = enElFondo(c.x, c.y)
            val d = pc - pa; val n = Offset(-d.y, d.x) * (0.22f * sin(reloj * 0.9f + fase))
            bandas[b].moveTo(pa.x, pa.y)
            bandas[b].cubicTo(pa.x + d.x / 3f + n.x, pa.y + d.y / 3f + n.y, pa.x + d.x * 2f / 3f + n.x * 0.6f, pa.y + d.y * 2f / 3f + n.y * 0.6f, pc.x, pc.y)
        }
        var z = zc; var j = 0; var zAnt = 0f; var pasoAnt = 0f
        while (z < zl && j < 40) {
            val paso = 0.46f * (z / zc).pow(0.5f)
            val y = horizonte + focal * (ojo + hondo) / z
            val b = if (z < zc * 1.3f) 0 else if (z < zc * 1.75f) 1 else 2
            val i0 = floor(((desde(y) - 10f) - centro) * z / focal / paso).toInt(); val i1 = ceil((limites.right + 14f - centro) * z / focal / paso).toInt()
            for (i in i0..i1) {
                val a = nudo(i, j, z, paso)
                arista(b, a, nudo(i + 1, j, z, paso), ruido(i, j, 4) * 6.28f)
                if (j > 0) { val k = Math.round(a.x / pasoAnt); arista(b, a, nudo(k, j - 1, zAnt, pasoAnt), ruido(i, j, 5) * 6.28f) }
            }
            zAnt = z; pasoAnt = paso; z += paso * 0.8f; j++
        }
        val anchos = floatArrayOf(0.8f, 0.55f, 0.38f)
        val ys = floatArrayOf(limites.bottom - 12f, horizonte + focal * (ojo + hondo) / (zc * 1.5f), horizonte + focal * (ojo + hondo) / (zc * 2.2f))
        fun dibujo(ds: DrawScope) = with(ds) {
            for (b in 0..2) {
                val al = (0.2f * f * transparencia(ys[b])).coerceIn(0f, 1f)
                if (al < 0.02f) continue
                drawPath(bandas[b], color.copy(alpha = al * 0.35f), style = Stroke(anchos[b] * 2.6f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawPath(bandas[b], color.copy(alpha = al), style = Stroke(anchos[b], cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
        val fuera = if (sombras.isEmpty()) null else Path().apply { for (s in sombras) addPath(s) }
        clipPath(zona) { if (fuera != null) clipPath(fuera, ClipOp.Difference) { dibujo(this) } else dibujo(this) }
    }

    /**
     * El collar de burbujas del buzo: del fondo sube una columna que se ve por el agua clara (abajo se la come el verde)
     * y, arriba, en [c], las burbujas revientan en la superficie en un corro que hierve. [k]: unidades por metro ahí.
     */
    fun DrawScope.burbujas(c: Offset, k: Float, reloj: Float, semilla: Int = 0) {
        val claro = lerp(l.bajo, Color.White, 0.6f)
        val az = Azar(semilla.toLong() * 7 + 11)
        val t = transparencia(c.y + k * 3f)
        repeat(16) {
            val ph = ((reloj * az.entre(0.25f, 0.4f) + it / 16f) % 1f)
            val q = c + Offset(az.entre(-0.4f, 0.4f) * k * (1f - 0.5f * ph) + 0.15f * k * sin(reloj * 3f + it), (1f - ph) * k * 4f)
            val al = (0.1f + 0.5f * t) * ph * ph
            drawCircle(claro.copy(alpha = al), (0.03f + 0.06f * ph) * k, q, style = Stroke(0.45f))
            drawCircle(Color.White.copy(alpha = al * 0.6f), (0.012f + 0.02f * ph) * k, q + Offset(-0.015f * k, -0.015f * k))
        }
        // El hervor de la superficie: anillos que nacen y se abren, y la mancha clara del agua revuelta.
        drawOval(claro.copy(alpha = 0.16f), Offset(c.x - k * 1.1f, c.y - k * 0.22f), Size(k * 2.2f, k * 0.44f))
        repeat(14) {
            val ph = ((reloj * az.entre(0.5f, 0.9f) + it / 14f) % 1f)
            val q = c + Offset(az.entre(-1f, 1f) * k * 0.9f, az.entre(-0.3f, 0.3f) * k * 0.5f)
            val rr = (0.03f + 0.14f * ph) * k
            drawOval(claro.copy(alpha = 0.75f * (1f - ph)), Offset(q.x - rr, q.y - rr * 0.4f), Size(rr * 2f, rr * 0.8f), style = Stroke(0.55f))
        }
    }

    /**
     * El verde que brilla de noche donde algo toca el agua («el agua alrededor del barco se ilumina de verde cuando la
     * tocas»): en cada uno de los [puntos] (el bote, la cadena del ancla, la hornacina sobre el agua), unas chispas que se
     * encienden y se apagan; a lo largo de la [cadena] (los eslabones que se hunden); la estela de un pez que pasa cerca,
     * que se enciende detrás de él y se apaga; y unas pocas sueltas en el agua de cerca. Luz propia, sin capas.
     */
    fun DrawScope.verdeVivo(puntos: List<Offset>, cadena: List<Offset>, zona: Rect, reloj: Float, fuerza: Float, desde: (Float) -> Float) {
        if (fuerza <= 0.02f) return
        val az = Azar(91)
        val v = C.VERDE_VIVO; val blanco = lerp(v, Color.White, 0.45f)
        for ((i, p) in puntos.withIndex()) repeat(6) {
            val ph = sin(reloj * az.entre(1.2f, 2.6f) + i * 1.3f + it * 2.1f).let { it * it }
            val q = p + Offset(az.entre(-8f, 8f), az.entre(-1.5f, 2.5f))
            val rr = az.entre(0.6f, 1.6f)
            drawCircle(v.copy(alpha = (0.12f * fuerza * ph).coerceIn(0f, 1f)), rr * 1.8f, q, blendMode = BlendMode.Plus)
            drawCircle(blanco.copy(alpha = (0.85f * fuerza * ph).coerceIn(0f, 1f)), rr * 0.6f, q, blendMode = BlendMode.Plus)
        }
        for ((i, p) in cadena.withIndex()) {
            val ph = sin(reloj * 1.7f + i * 0.9f).let { if (it > 0.2f) (it - 0.2f) / 0.8f else 0f }
            if (ph > 0f) { drawCircle(v.copy(alpha = (0.22f * fuerza * ph * (1f - i / (cadena.size + 1f))).coerceIn(0f, 1f)), 2.6f, p, blendMode = BlendMode.Plus); drawCircle(blanco.copy(alpha = (0.5f * fuerza * ph).coerceIn(0f, 1f)), 0.5f, p, blendMode = BlendMode.Plus) }
        }
        // La estela de un pez: cruza el agua de cerca en una curva y se enciende detrás de él.
        val vuelta = (reloj * 0.07f) % 1f
        val a = Offset(zona.right + 20f, zona.top + (zona.bottom - zona.top) * 0.62f); val b = Offset(zona.right - 160f, zona.bottom + 10f)
        val m = Offset(zona.right - 40f, zona.bottom - 30f)
        for (i in 0 until 14) {
            val t = vuelta - i * 0.012f; if (t < 0f || t > 1f) continue
            val q = a * ((1f - t) * (1f - t)) + m * (2f * t * (1f - t)) + b * (t * t) + Offset(1.6f * sin(reloj * 4f + i), 0f)
            if (q.x < desde(q.y)) continue
            val al = fuerza * (1f - i / 14f) * (0.7f + 0.3f * sin(reloj * 9f + i * 2f))
            drawCircle(v.copy(alpha = (0.18f * al).coerceIn(0f, 1f)), 3.2f - 0.15f * i, q, blendMode = BlendMode.Plus)
            drawCircle(blanco.copy(alpha = (0.7f * al).coerceIn(0f, 1f)), 0.7f, q, blendMode = BlendMode.Plus)
        }
        repeat(18) {
            val q = Offset(az.entre(zona.left, zona.right), az.entre(horizonte + (zona.bottom - horizonte) * 0.45f, zona.bottom))
            if (q.x < desde(q.y)) return@repeat
            val ph = sin(reloj * az.entre(0.6f, 1.4f) + it * 0.9f).let { if (it > 0.7f) (it - 0.7f) / 0.3f else 0f }
            if (ph > 0f) drawCircle(v.copy(alpha = (0.5f * fuerza * ph).coerceIn(0f, 1f)), 0.7f + 0.5f * abs(sin(it.toFloat())), q, blendMode = BlendMode.Plus)
        }
    }
}
