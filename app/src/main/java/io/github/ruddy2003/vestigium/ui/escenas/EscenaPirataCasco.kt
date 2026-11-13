package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Un casco de madera con volumen, para pintarlo en cualquier postura: derecho en el agua, escorado o tumbado en la
 * arena para carenarlo. **[Diseño propio]**: en vez de dibujar cada postura a mano, el casco es una malla sencilla
 * (cuadernas de popa a proa, tracas de la quilla a la borda) que se gira y se proyecta sin perspectiva; cada tabla toma
 * la luz según hacia dónde mira. Así el balandro tumbado en el cayo, los barcos fondeados en Nassau y las velas
 * lejanas salen del mismo dibujo y ninguno es una calcomanía del otro.
 *
 * Medidas en unidades del casco: [eslora] de popa a proa, [manga] la media anchura máxima, [puntal] de la quilla a la
 * cubierta. El casco mira hacia +z (la proa), con +x a estribor y +y hacia arriba.
 */
internal class CascoPirata(val eslora: Float, val manga: Float, val puntal: Float, val amurada: Float = puntal * 0.22f) {
    /** Un punto en el espacio. */
    class P3(val x: Float, val y: Float, val z: Float) {
        operator fun minus(o: P3) = P3(x - o.x, y - o.y, z - o.z)
        fun cruz(o: P3) = P3(y * o.z - z * o.y, z * o.x - x * o.z, x * o.y - y * o.x)
        fun punto(o: P3) = x * o.x + y * o.y + z * o.z
        fun unidad(): P3 { val n = sqrt(x * x + y * y + z * z).coerceAtLeast(0.0001f); return P3(x / n, y / n, z / n) }
    }

    /** Cómo se mira el casco: [escora] (grados, positiva hacia estribor), [guinada] (grados, girado sobre su palo) y [picado] (cuánto se mira desde arriba). */
    class Vista(val centro: Offset, val escala: Float, val escora: Float = 0f, val guinada: Float = 90f, val picado: Float = 8f, val espejo: Boolean = false) {
        private val e = Math.toRadians(escora.toDouble()); private val g = Math.toRadians(guinada.toDouble()); private val p = Math.toRadians(picado.toDouble())
        private val ce = cos(e).toFloat(); private val se = sin(e).toFloat(); private val cg = cos(g).toFloat(); private val sg = sin(g).toFloat(); private val cp = cos(p).toFloat(); private val sp = sin(p).toFloat()

        /** Gira un punto del casco: primero la escora (sobre la quilla), luego la guiñada (sobre el palo) y al final el picado de quien mira. Deja +z hacia quien mira. */
        fun gira(q: P3): P3 {
            val x1 = q.x * ce + q.y * se; val y1 = -q.x * se + q.y * ce; val z1 = q.z
            val x2 = x1 * cg + z1 * sg; val z2 = -x1 * sg + z1 * cg
            val y3 = y1 * cp - z2 * sp; val z3 = y1 * sp + z2 * cp
            return P3(if (espejo) -x2 else x2, y3, z3)
        }

        fun plano(q: P3): Offset = Offset(centro.x + q.x * escala, centro.y - q.y * escala)
        fun en(q: P3): Offset = plano(gira(q))
    }

    /** Los colores del casco. [fondo]: la obra viva (bajo el agua). [limpio], de 0 a 1 desde la proa: cuánto del fondo está ya rascado y untado; con −1 no se distingue. */
    class Pintura(val costado: Color, val faja: Color, val borda: Color, val fondo: Color, val fondoLimpio: Color = fondo, val limpio: Float = -1f, val cubierta: Color = Color(0xFFB89468))

    private val N = 16                                                          // cuadernas
    private val M = 9                                                           // tracas, de la quilla a la cinta

    /** La media manga en la cubierta a [s] de la popa (0) a la proa (1): espejo ancho, lo más lleno hacia el medio y la proa en punta. */
    private fun mediaManga(s: Float): Float = if (s < 0.46f) manga * (0.66f + 0.34f * sin(s / 0.46f * PI.toFloat() / 2f)) else manga * cos((s - 0.46f) / 0.54f * PI.toFloat() / 2f).coerceAtLeast(0f).pow(0.72f)

    /** Cuánto baja la quilla: la roda sube hacia la proa y el codaste un poco hacia la popa. */
    private fun calado(s: Float): Float = puntal * (1f - 0.62f * max(0f, (s - 0.8f) / 0.2f).pow(2f) - 0.16f * max(0f, (0.14f - s) / 0.14f).pow(2f))

    /** El arrufo: la cubierta sube hacia la proa y hacia la popa. */
    private fun arrufo(s: Float): Float = puntal * (if (s > 0.42f) 0.42f * ((s - 0.42f) / 0.58f).pow(2f) else 0.3f * ((0.42f - s) / 0.42f).pow(2f))

    /** Un punto del forro: [s] de popa a proa, [u] de la quilla (0) a la cinta (1) y, de 1 a 2, la amurada. [lado]: 1 estribor, −1 babor. */
    fun forro(s: Float, u: Float, lado: Float): P3 {
        val b = mediaManga(s); val d = calado(s); val a = arrufo(s)
        val uu = u.coerceAtMost(1f); val t = uu * PI.toFloat() / 2f
        val x = b * sin(t).coerceAtLeast(0f).pow(0.62f); val y = -d * cos(t).coerceAtLeast(0f).pow(1.25f) + a * uu + (u - uu) * amurada
        return P3(lado * x, y, (s - 0.5f) * eslora)
    }

    /** La línea de flotación, en altura del casco. */
    val flotacion: Float get() = -puntal * 0.46f

    private class Cara(val a: Offset, val b: Offset, val c: Offset, val d: Offset, val fondo: Float, val color: Color)

    /**
     * Pinta el casco. [palo]: cuánto mide el palo (0 = sin él). [lejos]: la bruma de la distancia. Devuelve la silueta
     * de lo pintado, por si hace falta para una sombra o un reflejo.
     */
    fun DrawScope.casco(l: Luces, r: Rayo, v: Vista, pinta: Pintura, lejos: Float = 0f, palo: Float = 0f, baupres: Float = 0f, costuras: Boolean = true): Path {
        // De dónde viene la luz, vista desde quien mira: del lado del sol, de arriba y un poco de frente.
        val luz = P3(r.lat * 0.9f, 0.75f, 0.55f).unidad()
        fun tono(propio: Color, n: P3): Color {
            val k = (n.punto(luz) * 0.5f + 0.5f).coerceIn(0f, 1f)
            val clara = l.tono(lerp(propio, Color(0xFFFFF2D0), 0.34f * r.contraste), lejos); val oscura = l.tono(mult(propio, r.color, 0.9f * r.contraste), lejos)
            return lerp(oscura, clara, k * k * (3f - 2f * k))
        }
        // El palo y el baupres: si la cubierta mira al otro lado (el casco tumbado), quedan detrás del forro.
        val cubiertaALaVista = v.gira(P3(0f, 1f, 0f)).z > 0.08f
        val arboladura: () -> Unit = {
            if (palo > 0f) {
            val pie = P3(0f, arrufo(0.6f), 0.1f * eslora); val tope = P3(0f, pie.y + palo, pie.z - palo * 0.04f)
            drawLine(l.tono(Color(0xFF8A6A44), lejos), v.en(pie), v.en(tope), max(0.6f, v.escala * 0.07f * puntal), StrokeCap.Round)
        }
            if (baupres > 0f) {
            val pie = forro(0.97f, 1.8f, 1f); val a = P3(0f, pie.y, pie.z); val b = P3(0f, pie.y + baupres * 0.2f, pie.z + baupres)
            drawLine(l.tono(Color(0xFF8A6A44), lejos), v.en(a), v.en(b), max(0.5f, v.escala * 0.05f * puntal), StrokeCap.Round)
        }
        }
        if (!cubiertaALaVista) arboladura()
        val caras = ArrayList<Cara>(2 * N * (M + 2))
        val silueta = Path()
        for (lado in listOf(1f, -1f)) for (i in 0 until N) for (j in 0 until M + 2) {
            val s0 = i / N.toFloat(); val s1 = (i + 1) / N.toFloat()
            val u0 = if (j < M) j / M.toFloat() else 1f + (j - M) * 0.5f; val u1 = if (j < M) (j + 1) / M.toFloat() else 1f + (j - M + 1) * 0.5f
            val q00 = forro(s0, u0, lado); val q10 = forro(s1, u0, lado); val q11 = forro(s1, u1, lado); val q01 = forro(s0, u1, lado)
            val g00 = v.gira(q00); val g10 = v.gira(q10); val g11 = v.gira(q11); val g01 = v.gira(q01)
            val n = (g10 - g00).cruz(g01 - g00).unidad()
            // Hacia fuera: en estribor la normal calculada mira hacia dentro, y al revés con el espejo.
            val fuera = if ((lado > 0f) != v.espejo) P3(-n.x, -n.y, -n.z) else n
            if (fuera.z <= 0.02f) continue                                      // mira al otro lado: no se ve
            val alto = (q00.y + q10.y + q11.y + q01.y) / 4f; val sm = (s0 + s1) / 2f
            val propio = when {
                j >= M -> pinta.borda
                j == M - 1 -> pinta.faja
                alto < flotacion -> if (pinta.limpio >= 0f && 1f - sm <= pinta.limpio) pinta.fondoLimpio else pinta.fondo
                else -> pinta.costado
            }
            caras += Cara(v.plano(g00), v.plano(g10), v.plano(g11), v.plano(g01), (g00.z + g10.z + g11.z + g01.z) / 4f, tono(propio, fuera))
        }
        caras.sortBy { it.fondo }
        for (c in caras) {
            val cara = Path().apply { moveTo(c.a.x, c.a.y); lineTo(c.b.x, c.b.y); lineTo(c.c.x, c.c.y); lineTo(c.d.x, c.d.y); close() }
            drawPath(cara, c.color); drawPath(cara, c.color, style = Stroke(0.5f, join = StrokeJoin.Round))
            silueta.addPath(cara)
        }
        // Las costuras del forro: una línea por traca, del lado que se ve.
        if (costuras) for (lado in listOf(1f, -1f)) for (j in 1 until M) {
            val u = j / M.toFloat(); val linea = Path(); var empezada = false
            for (i in 0..N) {
                val s = i / N.toFloat()
                val a = v.gira(forro(s, u, lado)); val b = v.gira(forro(s, u + 0.05f, lado)); val c = v.gira(forro((s + 0.03f).coerceAtMost(1f), u, lado))
                val n = (c - a).cruz(b - a); val fuera = if ((lado > 0f) != v.espejo) -n.z else n.z
                val p = v.plano(a)
                if (fuera > 0f && s < 0.985f) { if (empezada) linea.lineTo(p.x, p.y) else { linea.moveTo(p.x, p.y); empezada = true } } else empezada = false
            }
            drawPath(linea, l.tono(Color(0xFF1E1814), lejos).copy(alpha = 0.34f), style = Stroke(max(0.25f, v.escala * 0.012f * puntal), cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        // La quilla, la roda y el codaste: un madero corrido por el canto de abajo.
        val quilla = Path().apply { for (i in 0..N) { val p = v.en(forro(i / N.toFloat(), 0f, 1f)); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; val tope = v.en(forro(1f, 1.9f, 1f)); lineTo(tope.x, tope.y) }
        drawPath(quilla, l.tono(Color(0xFF3A2C22), lejos), style = Stroke(max(0.6f, v.escala * 0.05f * puntal), cap = StrokeCap.Round, join = StrokeJoin.Round))
        // El timón, en el plano de la quilla, detrás del espejo.
        val t0 = forro(0f, 0f, 1f); val t1 = forro(0f, 1.6f, 1f)
        val pala = listOf(P3(0f, t0.y, t0.z), P3(0f, t0.y + puntal * 0.04f, t0.z - eslora * 0.06f), P3(0f, flotacion + puntal * 0.1f, t0.z - eslora * 0.05f), P3(0f, t1.y, t0.z - eslora * 0.012f), P3(0f, t1.y, t0.z))
        val timon = Path().apply { pala.forEachIndexed { i, q -> val p = v.en(q); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
        drawPath(timon, l.tono(lerp(pinta.costado, Color(0xFF6A5038), 0.5f), lejos)); silueta.addPath(timon)
        // El espejo de popa, si mira hacia acá.
        val espejoN = v.gira(P3(0f, 0f, -1f))
        if (espejoN.z > 0.05f) {
            val puntos = (0..M + 2).map { j -> forro(0f, if (j <= M) j / M.toFloat() else 1f + (j - M) * 0.5f, 1f) } + (M + 2 downTo 0).map { j -> forro(0f, if (j <= M) j / M.toFloat() else 1f + (j - M) * 0.5f, -1f) }
            val forma = Path().apply { puntos.forEachIndexed { i, q -> val p = v.en(q); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
            drawPath(forma, tono(lerp(pinta.costado, pinta.faja, 0.35f), espejoN)); silueta.addPath(forma)
            val a = v.en(forro(0f, 1f, 1f)); val b = v.en(forro(0f, 1f, -1f))
            drawLine(l.tono(pinta.faja, lejos), a, b, max(0.5f, v.escala * 0.05f * puntal))
        }
        // La cubierta, si se ve desde arriba.
        val arriba = v.gira(P3(0f, 1f, 0f))
        if (arriba.z > 0.08f) {
            val forma = Path().apply {
                for (i in 0..N) { val p = v.en(forro(i / N.toFloat(), 1f, 1f)); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
                for (i in N downTo 0) { val p = v.en(forro(i / N.toFloat(), 1f, -1f)); lineTo(p.x, p.y) }
                close()
            }
            drawPath(forma, tono(pinta.cubierta, arriba)); silueta.addPath(forma)
        }
        if (cubiertaALaVista) arboladura()
        return silueta
    }

    /** Dónde arranca el palo y dónde acaba, con [largo] de palo, para colgarle velas, aparejos o gallardetes. */
    fun pieDelPalo(): P3 = P3(0f, arrufo(0.6f), 0.1f * eslora)
    fun topeDelPalo(largo: Float): P3 = pieDelPalo().let { P3(0f, it.y + largo, it.z - largo * 0.04f) }
    fun puntaDelBaupres(largo: Float): P3 = forro(0.97f, 1.8f, 1f).let { P3(0f, it.y + largo * 0.2f, it.z + largo) }
    fun coronamiento(): P3 = forro(0f, 2f, 1f).let { P3(0f, it.y, it.z) }
    fun cubiertaEn(s: Float): P3 = P3(0f, arrufo(s), (s - 0.5f) * eslora)
}
