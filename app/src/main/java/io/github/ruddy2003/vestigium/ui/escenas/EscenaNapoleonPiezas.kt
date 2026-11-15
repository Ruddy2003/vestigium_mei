package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Las piezas de Napoleónico
// =====================================================================
//
// Los cuerpos se arman en metros, en sus propios ejes, y se proyectan con la perspectiva cónica de Mongol (`V3`,
// `Mirada`, `Sitio`, `Sol3` en `EscenaMongolPiezas.kt`, usada tal cual, con las caras de caja de Botánico): así se ven en
// tres cuartos, con su lado de luz y su lado de sombra (fallo 1 de la tanda S11). Todas las cosas de un cuadro se ponen
// con los ojos a la misma altura ([PiezasNapoleon.OJO]), así que comparten un mismo suelo y un mismo espacio: con eso
// se tumban las sombras del sol, de la luna y, de noche, las del fuego, abiertas en abanico desde él.
//
// La luz de cada cara la da un [Alumbre]: el sol (o la luna) por un lado y, de noche, la lumbre del fuego por el suyo.
// Los pinos, los capotes y las telas, que no tienen caras, se dibujan en el lienzo, pincelada a pincelada.

/** Operaciones de vectores que la perspectiva de Mongol no trae. */
internal object Vec {
    fun largo(a: V3): Float = sqrt(a.x * a.x + a.y * a.y + a.z * a.z)
    fun unidad(a: V3): V3 { val l = largo(a).coerceAtLeast(1e-4f); return V3(a.x / l, a.y / l, a.z / l) }
    fun cruz(a: V3, b: V3): V3 = V3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x)
    fun punto(a: V3, b: V3): Float = a.x * b.x + a.y * b.y + a.z * b.z

    /** Dos direcciones perpendiculares a [eje] (y entre sí), para trazar un círculo alrededor de él. */
    fun base(eje: V3): Pair<V3, V3> {
        val e = unidad(eje)
        val ref = if (abs(e.y) < 0.9f) V3(0f, 1f, 0f) else V3(1f, 0f, 0f)
        val u = unidad(cruz(ref, e)); val v = cruz(e, u)
        return u to v
    }

    /** [p] girado [a] radianes alrededor del eje y. */
    fun girarY(p: V3, a: Float): V3 { val c = cos(a); val s = sin(a); return V3(p.x * c + p.z * s, p.y, -p.x * s + p.z * c) }

    /** [p] girado [a] radianes alrededor del eje z. */
    fun girarZ(p: V3, a: Float): V3 { val c = cos(a); val s = sin(a); return V3(p.x * c - p.y * s, p.x * s + p.y * c, p.z) }

    /** [p] girado [a] radianes alrededor del eje x. */
    fun girarX(p: V3, a: Float): V3 { val c = cos(a); val s = sin(a); return V3(p.x, p.y * c - p.z * s, p.y * s + p.z * c) }
}

/** El centro de unos puntos del lienzo. */
internal fun centroDe(q: List<Offset>): Offset { var x = 0f; var y = 0f; for (p in q) { x += p.x; y += p.y }; return Offset(x / q.size, y / q.size) }

/**
 * La luz de un momento para las piezas: el sol (o, de noche, la luna) y la lumbre de un fuego.
 * - [p] y [sol]: el pincel y la dirección del sol (de noche, `Rayo` sigue a la luna, pero sin fuerza).
 * - [fuego], [lumbre] y [alcance]: dónde está el fuego en el lienzo, cuánto alumbra (de 0 a algo más de 1; de día casi
 *   nada) y a qué distancia su luz cae a la mitad.
 * - [luna] y [lunaSol]: cuánto platea la luna (de 0 a 1) y desde dónde.
 * - [sombra]: si la cosa está a la sombra (de 0 a 1), por ejemplo en una franja de sombra del bosque.
 * Cada cara recibe el sol por su lado y el fuego por el suyo: de noche, una cosa queda naranja del lado del fuego y
 * plata del lado de la luna (la noche a dos luces de Renacimiento, al aire libre).
 */
internal class Alumbre(
    val p: Pincel, val sol: Sol3,
    val fuego: Offset = Offset(-999f, -999f), val lumbre: Float = 0f, val alcance: Float = 100f,
    val luna: Float = 0f, val lunaSol: Sol3? = null, val sombra: Float = 0f,
) {
    val l: Luces get() = p.l

    /** El mismo alumbre a la sombra [k] (de 0 a 1): sin sol directo, más frío. */
    fun aLaSombra(k: Float): Alumbre = if (k <= 0.01f) this else Alumbre(p, sol, fuego, lumbre, alcance, luna, lunaSol, maxOf(sombra, k.coerceIn(0f, 1f)))

    /** Cuánta luz del fuego llega a [en], de 0 a algo más de 1. */
    fun calor(en: Offset): Float {
        if (lumbre <= 0.01f) return 0f
        val d = en - fuego
        return lumbre / (1f + (d.x * d.x + d.y * d.y * 2.6f) / (alcance * alcance))
    }

    private fun haciaElFuego(en: Offset): V3 {
        val dx = fuego.x - en.x; val dz = -(fuego.y - en.y) * 2.4f
        val n = sqrt(dx * dx + dz * dz).coerceAtLeast(1f)
        return V3(dx / n * 0.88f, 0.4f, dz / n * 0.88f)
    }

    /** El color de algo alumbrado de lleno por la luna. */
    fun deLuna(c: Color, lejos: Float = 0f): Color = lerp(Color(c.red * 0.62f * 0.82f + 0.05f, c.green * 0.62f * 0.86f + 0.06f, (c.blue * 0.66f * 0.98f + 0.1f).coerceAtMost(1f)), l.bruma, (0.6f * lejos).coerceIn(0f, 1f))

    /** El color de algo alumbrado de lleno por el fuego. */
    fun deFuego(c: Color): Color {
        val f = ColorNapoleon.LUMBRE
        return Color(((c.red * 0.85f + 0.14f) * f.red).coerceIn(0f, 1f), ((c.green * 0.85f + 0.14f) * f.green).coerceIn(0f, 1f), ((c.blue * 0.85f + 0.14f) * f.blue).coerceIn(0f, 1f), c.alpha)
    }

    /** El color [c] de una cara con la normal [w] (en el mundo) que está en [en] del lienzo. */
    fun cara(c: Color, w: V3, en: Offset, lejos: Float = 0f): Color {
        var col = p.por(c, sol.t(w) * (1f - sombra), lejos)
        if (sombra > 0.01f) col = lerp(col, mult(p.medio(c, lejos), p.r.color, 0.9f), 0.62f * sombra * p.r.dia)
        val ls = lunaSol
        if (luna > 0.02f && ls != null) { val t = ls.t(w); if (t > -0.2f) col = lerp(col, deLuna(c, lejos), (0.48f * luna * (t + 0.2f) / 1.2f).coerceIn(0f, 1f)) }
        val k = calor(en)
        if (k > 0.01f) {
            val f = haciaElFuego(en); val t = Vec.punto(w, f)
            col = lerp(col, deFuego(c), (k * (0.22f + 0.78f * t.coerceIn(0f, 1f))).coerceIn(0f, 0.94f))
        }
        return col
    }

    /** Lo que no tiene caras (una tela, una rama): [mira] va de −1 (mira a la izquierda) a 1 (a la derecha); 0, a quien mira. */
    fun plano(c: Color, en: Offset, mira: Float = 0f, lejos: Float = 0f, alto: Float = 0.25f): Color =
        cara(c, Vec.unidad(V3(mira * 0.85f, alto, -(1f - abs(mira) * 0.55f))), en, lejos)

    /** Lo que mira al cielo. */
    fun arriba(c: Color, en: Offset, lejos: Float = 0f): Color = cara(c, V3(0f, 1f, 0f), en, lejos)

    /** El color de lo que da luz y se ve igual de día y de noche (una brasa): solo lo apaga un poco el día. */
    fun brasa(fuerza: Float): Color = lerp(Color(0xFF8A2A10), Color(0xFFFFB04A), fuerza.coerceIn(0f, 1f))
}

/**
 * Las piezas de los dos cuadros de Napoleónico. Cada una con su lado de luz y de sombra, su textura y sus señales de uso,
 * su versión con nieve y, cuando la hay, su silueta para la sombra que arroja. A cualquier tamaño.
 */
internal object PiezasNapoleon {
    private val C = ColorNapoleon

    /** La altura de los ojos de quien mira sobre el suelo, en metros: la misma para todo, para que todo esté en un mismo espacio. */
    const val OJO = 1.6f

    /** El sitio de una cosa que pisa en [pie] (con [v]), girada [giro]. Un metro mide allí lo que da la altura de los ojos. */
    fun sitio(v: Mirada, pie: Offset, giro: Float = 0f): Sitio = Sitio(v, pie, ((pie.y - v.horizonte) / OJO).coerceAtLeast(0.5f), giro)

    /**
     * La sombra en el suelo del punto [a] de la cosa [s], con el sol [sol]: se tumba según su altura. Para las sombras
     * del fuego, [fuego] (en metros, en el espacio de los ojos) dice desde dónde: la sombra se abre lejos de él.
     */
    fun alSuelo(s: Sitio, sol: Sol3, a: V3): Offset { val h = a.y.coerceAtLeast(0f); return s.pm(s.mx(a.x, a.z) + sol.sx * h, 0f, s.mz(a.x, a.z) + sol.sz * h) }

    /** El punto del suelo, en el espacio de los ojos, donde está el fuego que pisa en [pie] del lienzo. */
    fun enElSuelo(v: Mirada, pie: Offset): Pair<Float, Float> { val k = (pie.y - v.horizonte) / OJO; val z = v.focal / k.coerceAtLeast(0.5f); return ((pie.x - v.centro) / k) to z }

    /** La sombra que el fuego (en [fx], [fz], con la llama a [alto] metros) le hace al punto [a] de [s]: lejos de él, más larga cuanto más alto. */
    fun alSueloDelFuego(s: Sitio, fx: Float, fz: Float, alto: Float, a: V3): Offset {
        val x = s.mx(a.x, a.z); val z = s.mz(a.x, a.z); val dx = x - fx; val dz = z - fz
        val d = sqrt(dx * dx + dz * dz).coerceAtLeast(0.05f)
        val h = a.y.coerceAtLeast(0f)
        val largo = if (h < alto * 0.8f) d * h / (alto - h) else d * 4f + h * 1.2f
        val l = largo.coerceAtMost(3.4f + h)
        return s.pm(x + dx / d * l, 0f, z + dz / d * l)
    }

    // =================================================================
    //  Dibujo de cuerpos
    // =================================================================

    /** Pinta una cara de [s] si se ve y devuelve su forma; si no se ve, null. */
    fun DrawScope.cara(s: Sitio, a: Alumbre, q: List<V3>, n: V3, c: Color, lejos: Float = 0f): Path? {
        val cc = V3(q.sumOf { it.x.toDouble() }.toFloat() / q.size, q.sumOf { it.y.toDouble() }.toFloat() / q.size, q.sumOf { it.z.toDouble() }.toFloat() / q.size)
        if (!s.seVe(cc, n)) return null
        val pts = q.map { s.p(it) }; val f = poligono(pts)
        val col = a.cara(c, s.normal(n), centroDe(pts), lejos)
        drawPath(f, col); drawPath(f, col, style = Stroke(0.45f))
        return f
    }

    /** Una caja de [x0] a [x1], [y0] a [y1], [z0] a [z1]: sus caras visibles, con su luz. Devuelve las caras que se ven, con su forma. */
    fun DrawScope.caja(s: Sitio, a: Alumbre, x0: Float, x1: Float, y0: Float, y1: Float, z0: Float, z1: Float, c: Color, lejos: Float = 0f, arriba: Color = c): List<Pair<Cara3, Path>> =
        visibles(s, carasDeCaja(x0, x1, y0, y1, z0, z1)).mapNotNull { k -> cara(s, a, k.q, k.n, if (k.arriba) arriba else c, lejos)?.let { k to it } }

    /** Un punto de un círculo de radio [r] alrededor de [centro], en el plano perpendicular a [eje] (con su base [b]). */
    fun anillo(centro: V3, b: Pair<V3, V3>, r: Float, ang: Float): V3 = centro + b.first * (r * cos(ang)) + b.second * (r * sin(ang))

    /**
     * Un cilindro (o un tronco de cono) de [base] a [base] + [eje]·[alto], de radio [r0] abajo y [r1] arriba, en
     * [n] facetas: cada una con su luz, así que el cuerpo sale redondo, con el lado de luz, el de sombra y, de noche, el
     * del fuego. Pinta la tapa que se vea ([tapa]: su color, o null para dejarla abierta). Devuelve la silueta.
     */
    fun DrawScope.cilindro(
        s: Sitio, a: Alumbre, base: V3, eje: V3, r0: Float, r1: Float, alto: Float, c: Color,
        tapa: Color? = c, n: Int = 18, lejos: Float = 0f, tapaAbajo: Color? = tapa,
    ): Path {
        val e = Vec.unidad(eje); val b = Vec.base(e); val top = base + e * alto
        val pts = ArrayList<Offset>()
        for (i in 0 until n) {
            val a0 = 2f * PI.toFloat() * i / n; val a1 = 2f * PI.toFloat() * (i + 1) / n; val am = (a0 + a1) / 2f
            val nrm = Vec.unidad(b.first * cos(am) + b.second * sin(am) + e * ((r0 - r1) / alto))
            val q = listOf(anillo(base, b, r0, a0), anillo(base, b, r0, a1), anillo(top, b, r1, a1), anillo(top, b, r1, a0))
            pts += s.p(q[0]); pts += s.p(q[2])
            val mid = (q[0] + q[2]) * 0.5f
            if (!s.seVe(mid, nrm)) continue
            val qq = q.map { s.p(it) }; val f = poligono(qq)
            val col = a.cara(c, s.normal(nrm), centroDe(qq), lejos)
            drawPath(f, col); drawPath(f, col, style = Stroke(0.6f))
        }
        // La tapa que se ve.
        for ((centro, r, normal, color) in listOf(Quad(top, r1, e, tapa), Quad(base, r0, e * -1f, tapaAbajo))) {
            if (color == null || !s.seVe(centro, normal)) continue
            val qq = (0 until n).map { s.p(anillo(centro, b, r, 2f * PI.toFloat() * it / n)) }
            drawPath(poligono(qq), a.cara(color, s.normal(normal), centroDe(qq), lejos))
        }
        return poligono(envolvente(pts))
    }

    private data class Quad(val c: V3, val r: Float, val n: V3, val col: Color?)

    /** La elipse en el lienzo de un círculo de radio [r] alrededor de [centro], perpendicular a [eje]. */
    fun circulo(s: Sitio, centro: V3, eje: V3, r: Float, n: Int = 28): List<Offset> { val b = Vec.base(eje); return (0 until n).map { s.p(anillo(centro, b, r, 2f * PI.toFloat() * it / n)) } }

    /**
     * Una rueda de madera con su llanta de hierro: centro [centro], radio [r], el eje [eje] (la dirección del cubo),
     * [ancho] de llanta y [rayos] rayos. [rotos]: cuántos rayos faltan (los de una rueda rota); [hundida]: cuánto de la
     * rueda tapa la nieve (de 0 a 1, desde abajo, en metros sobre el suelo, ver [suelo]). Devuelve la silueta.
     */
    fun DrawScope.rueda(
        s: Sitio, a: Alumbre, centro: V3, r: Float, eje: V3, ancho: Float = 0.08f, rayos: Int = 12, rotos: Int = 0,
        madera: Color = C.MADERA, lejos: Float = 0f, giro0: Float = 0.2f, nieve: Float = 0f,
    ): Path {
        val e = Vec.unidad(eje); val b = Vec.base(e)
        val frente = if (s.seVe(centro + e * (ancho * 0.5f), e)) 1f else -1f
        val cf = centro + e * (ancho * 0.5f * frente); val cb = centro - e * (ancho * 0.5f * frente)
        val n = 32
        val fuera = (0 until n).map { s.p(anillo(cf, b, r, 2f * PI.toFloat() * it / n)) }
        val fueraAtras = (0 until n).map { s.p(anillo(cb, b, r, 2f * PI.toFloat() * it / n)) }
        val dentro = (0 until n).map { s.p(anillo(cf, b, r * 0.84f, 2f * PI.toFloat() * it / n)) }
        val en = s.p(centro)
        val hierro = a.plano(C.HIERRO, en, 0f, lejos)
        val hierroLuz = a.cara(C.HIERRO, V3(0f, 1f, 0f), en, lejos)
        // La llanta por detrás y por su canto (el grueso de la rueda).
        drawPath(TrazosValle.cerrada(fueraAtras), mult(hierro, C.SOMBRA, 0.35f))
        for (i in 0 until n) { val j = (i + 1) % n; drawPath(poligono(fuera[i], fuera[j], fueraAtras[j], fueraAtras[i]), if (fuera[i].y < en.y) hierroLuz else mult(hierro, C.SOMBRA, 0.3f)) }
        // Las pinas de madera y la llanta, de frente: un aro.
        val aro = Path().apply { addPath(TrazosValle.cerrada(fuera)); addPath(TrazosValle.cerrada(dentro.reversed())) }
        aro.fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
        val w = s.normal(e * frente)
        drawPath(aro, a.cara(madera, w, en, lejos))
        drawPath(TrazosValle.cerrada(fuera), hierro, style = Stroke((r * 0.06f * s.escala(centro.x, centro.z)).coerceIn(0.4f, 3.2f)))
        // Las juntas de las pinas.
        val k = s.escala(centro.x, centro.z)
        for (i in 0 until 6) { val ang = giro0 + 2f * PI.toFloat() * i / 6f; drawLine(mult(a.cara(madera, w, en, lejos), C.SOMBRA, 0.5f), s.p(anillo(cf, b, r * 0.84f, ang)), s.p(anillo(cf, b, r * 0.98f, ang)), (0.012f * k).coerceIn(0.3f, 1.2f)) }
        // Los rayos, torneados: más gruesos junto al cubo.
        val cubo = r * 0.17f
        for (i in 0 until rayos) {
            if (i < rotos) continue
            val ang = giro0 + 2f * PI.toFloat() * (i + 0.5f) / rayos
            val p0 = s.p(anillo(cf, b, cubo, ang)); val p1 = s.p(anillo(cf, b, r * 0.85f, ang))
            val d = p1 - p0; val ln = d.getDistance().coerceAtLeast(0.01f); val nn = Offset(-d.y, d.x) / ln
            val g0 = (0.045f * r * k).coerceIn(0.35f, 4f); val g1 = g0 * 0.6f
            val rayo = poligono(p0 + nn * g0, p1 + nn * g1, p1 - nn * g1, p0 - nn * g0)
            val lado = if (nn.x * (if (a.p.solALaDerecha) 1f else -1f) > 0f) 0.4f else -0.4f
            drawPath(rayo, a.cara(madera, Vec.unidad(V3(w.x + lado, w.y, w.z)), (p0 + p1) / 2f, lejos))
        }
        // El cubo.
        val cuboP = (0 until 14).map { s.p(anillo(cf + e * (0.06f * frente), b, cubo, 2f * PI.toFloat() * it / 14)) }
        drawPath(poligono(cuboP), a.cara(C.MADERA_OSC, w, en, lejos))
        drawCircle(a.plano(C.HIERRO, en, 0f, lejos), (cubo * 0.45f * k).coerceAtLeast(0.4f), s.p(cf + e * (0.1f * frente)))
        // La nieve posada en lo alto de la llanta.
        if (nieve > 0.05f) {
            val arco = (0..10).map { i -> val ang = PI.toFloat() * (0.62f + 0.76f * i / 10f); anillo(cf, b, r * 1.01f, -ang) }
            val pts = arco.filter { it.y > centro.y + r * 0.55f }.map { s.p(it) }
            if (pts.size >= 2) drawPath(curva(pts), a.arriba(C.NIEVE, en, lejos), style = Stroke((0.06f * k * nieve).coerceIn(0.6f, 4f), cap = StrokeCap.Round))
        }
        return poligono(envolvente(fuera + fueraAtras))
    }

    /** Una banda de nieve posada sobre el borde de arriba [borde] (puntos del lienzo de izquierda a derecha), de [grueso]: lo de arriba al sol, el canto de abajo azul. */
    fun DrawScope.nieveEncima(a: Alumbre, borde: List<Offset>, grueso: Float, az: Azar, lejos: Float = 0f) {
        if (borde.size < 2 || grueso <= 0.2f) return
        val n = borde.size
        val arriba = borde.mapIndexed { i, p -> val t = i / (n - 1f); val k = (sin(PI.toFloat() * t)).coerceAtLeast(0.25f); p + Offset(0f, -grueso * k * (0.75f + 0.5f * az.real())) }
        val abajo = borde.mapIndexed { i, p -> val t = i / (n - 1f); p + Offset(0f, grueso * 0.35f * sin(PI.toFloat() * t) * az.real()) }
        val forma = TrazosValle.cerrada(arriba + abajo.reversed())
        val en = centroDe(borde)
        drawPath(forma, a.cara(C.NIEVE, Vec.unidad(V3(0f, 0.7f, -0.7f)), en, lejos))
        drawPath(curva(abajo), a.cara(C.NIEVE_SOMBRA, V3(0f, -0.3f, -1f), en, lejos), style = Stroke((grueso * 0.3f).coerceIn(0.4f, 2.2f), cap = StrokeCap.Round))
        drawPath(curva(arriba), a.arriba(lerp(C.NIEVE, Color.White, 0.35f), en, lejos), style = Stroke((grueso * 0.22f).coerceIn(0.3f, 1.4f), cap = StrokeCap.Round))
    }

    // =================================================================
    //  El fusil y los fusiles en pabellón
    // =================================================================

    /**
     * Un fusil de [culata] (el talón, en el suelo o donde se apoye) a [boca], con la bayoneta puesta si [bayoneta]: la
     * culata ancha y la caña de nogal, el cañón de hierro, las abrazaderas, la llave, el portafusil de ante blanco que
     * cuelga y la hoja de tres filos. Medidas de un fusil de infantería (1.52 m sin la bayoneta, 0.46 la hoja)
     * **[Aproximación]**. [colgando]: cuánto cuelga el portafusil, en metros. Devuelve la silueta.
     */
    fun DrawScope.fusil(
        s: Sitio, a: Alumbre, culata: V3, boca: V3, bayoneta: Boolean = true, colgando: Float = 0.16f, lejos: Float = 0f,
        nieve: Float = 0f, carne: Boolean = false,
    ): Path {
        val ejeV = boca - culata; val largo = Vec.largo(ejeV); val e = Vec.unidad(ejeV)
        fun en(t: Float): V3 = culata + e * (largo * t)
        val k = s.escala(culata.x, culata.z)
        // El costado que mira a quien mira, para el ancho en el lienzo.
        fun ancho(t: Float, m: Float): Pair<Offset, Offset> {
            val p = s.p(en(t)); val q = s.p(en((t + 0.02f).coerceAtMost(1f)) ); val d = (q - p).let { if (it.getDistance() < 0.001f) Offset(0f, -1f) else it / it.getDistance() }
            val nn = Offset(-d.y, d.x); val kk = s.escala(en(t).x, en(t).z)
            return (p + nn * (m * kk * 0.5f)) to (p - nn * (m * kk * 0.5f))
        }
        fun tira(t0: Float, t1: Float, m0: Float, m1: Float): Path {
            val a0 = ancho(t0, m0); val a1 = ancho(t1, m1)
            return poligono(a0.first, a1.first, a1.second, a0.second)
        }
        val medio = s.p(en(0.5f))
        val luzIzq = a.plano(C.MADERA, medio, -0.9f, lejos); val luzDer = a.plano(C.MADERA, medio, 0.9f, lejos)
        val centro = a.plano(C.MADERA, medio, 0f, lejos)
        // El portafusil, detrás, colgando en curva entre la abrazadera de arriba y el guardamonte.
        val p0 = s.p(en(0.27f)); val p1 = s.p(en(0.7f))
        val caida = colgando * k
        val correa = combada(p0, p1, caida, 12)
        drawPath(curva(correa), a.plano(C.CORREA, (p0 + p1) / 2f, 0f, lejos), style = Stroke((0.032f * k).coerceIn(0.4f, 3f), cap = StrokeCap.Round))
        // La caña, de la culata a la boca: la culata ancha, la garganta y la caña, que llega casi a la boca.
        val culataF = tira(0f, 0.3f, 0.15f, 0.058f); val cana = tira(0.28f, 0.92f, 0.056f, 0.042f)
        drawPath(culataF, centro); drawPath(cana, centro)
        // El lado de luz y el de sombra, como un cuerpo redondo: dos tiras finas a cada lado.
        for ((t0, t1, m0, m1) in listOf(Quad4(0f, 0.3f, 0.15f, 0.058f), Quad4(0.28f, 0.92f, 0.056f, 0.042f))) {
            val izq0 = ancho(t0, m0).first; val izq1 = ancho(t1, m1).first; val der0 = ancho(t0, m0).second; val der1 = ancho(t1, m1).second
            drawPath(poligono(izq0, izq1, izq1 + (der1 - izq1) * 0.38f, izq0 + (der0 - izq0) * 0.38f), luzIzq)
            drawPath(poligono(der0, der1, der1 + (izq1 - der1) * 0.3f, der0 + (izq0 - der0) * 0.3f), luzDer)
        }
        // La veta del nogal.
        drawLine(mult(centro, C.SOMBRA, 0.25f), s.p(en(0.04f)), s.p(en(0.26f)), (0.008f * k).coerceIn(0.25f, 0.8f))
        // El talón de latón de la culata.
        val talon = ancho(0f, 0.13f)
        drawLine(a.plano(C.LATON, talon.first, 0f, lejos), talon.first, talon.second, (0.02f * k).coerceIn(0.4f, 2f))
        // El cañón de hierro, por encima de la caña y solo hasta la boca.
        val canon = tira(0.3f, 1f, 0.024f, 0.021f)
        drawPath(canon, a.plano(C.HIERRO, s.p(en(0.7f)), 0f, lejos))
        val brillo = ancho(0.6f, 0.024f).first
        drawLine(lerp(a.plano(C.HIERRO, brillo, if (a.p.solALaDerecha) 0.9f else -0.9f, lejos), a.deFuego(C.PLATA), (a.calor(brillo) * 0.6f).coerceIn(0f, 0.8f)), s.p(en(0.36f)), s.p(en(0.99f)), (0.008f * k).coerceIn(0.25f, 1f))
        // La llave y las abrazaderas.
        val llave = s.p(en(0.33f))
        drawCircle(a.plano(C.HIERRO, llave, 0f, lejos), (0.028f * k).coerceIn(0.4f, 3f), llave)
        drawLine(a.plano(C.HIERRO, llave, 0f, lejos), llave, s.p(en(0.36f)) + Offset(0f, -0.03f * k), (0.012f * k).coerceIn(0.3f, 1.2f))
        for (t in listOf(0.52f, 0.7f, 0.88f)) { val q = ancho(t, 0.054f); drawLine(a.plano(if (t == 0.88f) C.LATON else C.HIERRO, q.first, 0f, lejos), q.first, q.second, (0.014f * k).coerceIn(0.3f, 1.6f)) }
        val forma = Path().apply { addPath(culataF); addPath(cana); addPath(canon) }
        if (bayoneta) {
            // El cubo y el codo de la bayoneta, y la hoja de tres filos, que sigue al cañón.
            val b0 = en(1f); val b1 = boca + e * 0.46f
            val c0 = s.p(b0); val c1 = s.p(b1); val d = c1 - c0; val ln = d.getDistance().coerceAtLeast(0.01f); val nn = Offset(-d.y, d.x) / ln
            val g = (0.022f * k).coerceIn(0.35f, 2.6f)
            val hoja = poligono(c0 + nn * g, c1, c0 - nn * g * 0.4f)
            drawPath(hoja, a.plano(C.PLATA, (c0 + c1) / 2f, -0.4f, lejos))
            drawLine(a.plano(lerp(C.PLATA, Color.White, 0.3f), c0, 0.9f, lejos), c0 + nn * g * 0.3f, c1, (g * 0.35f).coerceAtLeast(0.25f))
            drawCircle(a.plano(C.HIERRO, c0, 0f, lejos), g * 0.8f, c0)
            forma.addPath(hoja)
            if (carne) {
                // Un trozo de carne ensartado en la punta, tostado por el fuego.
                val q = c0 + d * 0.72f
                drawOval(a.plano(lerp(C.ROJO, C.CUERO, 0.55f), q, 0f, lejos), Offset(q.x - g * 2.2f, q.y - g * 1.6f), Size(g * 4.4f, g * 3.2f))
                drawOval(a.plano(lerp(C.ROJO, C.CENIZA, 0.7f), q, 0f, lejos), Offset(q.x - g * 2f, q.y), Size(g * 4f, g * 1.4f))
            }
        }
        if (nieve > 0.1f) {
            // Un poco de nieve en el lomo de la culata y en el talón.
            val q = ancho(0.15f, 0.12f)
            drawLine(a.arriba(C.NIEVE, q.first, lejos), q.first, ancho(0.05f, 0.13f).first, (0.02f * k * nieve).coerceIn(0.3f, 2f), StrokeCap.Round)
        }
        return forma
    }

    private data class Quad4(val a: Float, val b: Float, val c: Float, val d: Float)

    /**
     * Los fusiles en pabellón: [cuantos] fusiles (tres o cuatro) con las culatas abiertas en el suelo alrededor de [s] y
     * las bayonetas trabadas arriba, de los que cuelgan [cartucheras] cartucheras de cuero negro con su placa de latón y,
     * si [chaco], un chacó, colgado de una bayoneta por el barboquejo. [semilla] cambia cómo se abren. Con [carne], el de
     * delante sale del pabellón y se inclina sobre el fuego con la carne en la bayoneta: lo dibuja [fusilAlFuego].
     */
    class Pabellon(val s: Sitio, val semilla: Long, val cuantos: Int = 3, val cartucheras: Int = 2, val chaco: Boolean = true, val nieve: Float = 0f) {
        private val az = Azar(semilla)
        val giro0 = az.entre(0f, 1.2f)
        val apice = V3(az.entre(-0.03f, 0.03f), 1.43f + az.entre(-0.02f, 0.03f), az.entre(-0.03f, 0.03f))
        val culatas: List<V3> = (0 until cuantos).map { i ->
            val ang = giro0 + 2f * PI.toFloat() * i / cuantos + az.entre(-0.18f, 0.18f)
            val r = 0.44f + az.entre(-0.04f, 0.05f)
            V3(r * cos(ang), 0f, r * sin(ang))
        }
        /** Dónde llega la boca de cada fusil: un poco antes del ápice, que es donde se traban las bayonetas. */
        val bocas: List<V3> = culatas.map { c -> val d = apice - c; c + d * (1.52f / Vec.largo(d)) }

        /** Lo que tumba sombra: los fusiles (como varas gruesas) y el chacó. */
        fun puntosDeSombra(): List<List<V3>> = culatas.indices.map { i -> listOf(culatas[i], bocas[i]) }
    }

    /** La silueta de un pabellón en el lienzo (la de sus fusiles con la bayoneta), para las sombras. */
    fun siluetaDePabellon(pb: Pabellon, aSuelo: (V3) -> Offset): Path {
        val formas = pb.culatas.indices.map { i ->
            val c = pb.culatas[i]; val b = pb.bocas[i]; val e = Vec.unidad(b - c); val t = b + e * 0.46f
            val p0 = aSuelo(c); val p1 = aSuelo(t); val d = p1 - p0; val ln = d.getDistance().coerceAtLeast(0.01f); val n = Offset(-d.y, d.x) / ln
            val k = pb.s.k * 0.06f
            poligono(p0 + n * k, p1 + n * k * 0.3f, p1 - n * k * 0.3f, p0 - n * k)
        }
        return union(formas)
    }

    fun DrawScope.pabellon(pb: Pabellon, a: Alumbre, lejos: Float = 0f) {
        val s = pb.s
        // De atrás adelante: los fusiles cuyas culatas están más lejos van primero.
        val orden = pb.culatas.indices.sortedByDescending { s.hondo(pb.culatas[it]) }
        // La nieve al pie de cada culata.
        if (pb.nieve > 0.1f) for (c in pb.culatas) { val q = s.p(c); val k = s.escala(c.x, c.z); mancha(q + Offset(0f, -0.02f * k), 0.11f * k, 0.05f * k, a.arriba(C.NIEVE, q, lejos), 0.9f * pb.nieve) }
        for ((j, i) in orden.withIndex()) {
            fusil(s, a, pb.culatas[i], pb.bocas[i], true, 0.12f + 0.05f * j, lejos, pb.nieve)
            // Las cartucheras cuelgan de su correa del fusil de delante, a media altura.
            if (j == orden.size - 1 && pb.cartucheras > 0) cartuchera(s, a, pb.culatas[i] + (pb.bocas[i] - pb.culatas[i]) * 0.5f + V3(0.02f, -0.18f, -0.06f), 0.25f, lejos)
            if (j == orden.size - 2 && pb.cartucheras > 1) cartuchera(s, a, pb.culatas[i] + (pb.bocas[i] - pb.culatas[i]) * 0.42f + V3(-0.04f, -0.2f, -0.05f), -0.3f, lejos)
        }
        if (pb.chaco) {
            // El chacó, colgado del cruce de las bayonetas, un poco ladeado y de frente.
            val base = pb.apice + V3(0.02f, -0.02f, -0.12f)
            chaco(s, a, base + V3(0f, -0.24f, 0f), Vec.unidad(V3(0.18f, 1f, -0.12f)), Vec.unidad(V3(0.25f, 0f, -1f)), lejos, pb.nieve)
            drawLine(a.plano(C.CUERO, s.p(base), 0f, lejos), s.p(base + V3(0f, -0.05f, 0f)), s.p(pb.apice), (0.012f * s.k).coerceIn(0.3f, 1.2f))
        }
    }

    /** Una cartuchera de cuero negro con la placa de latón, de su correa blanca, que cuelga de [p] (girada [giro]). */
    fun DrawScope.cartuchera(s: Sitio, a: Alumbre, p: V3, giro: Float, lejos: Float = 0f) {
        val w = 0.13f; val h = 0.085f; val d = 0.04f
        fun q(x: Float, y: Float, z: Float) = p + Vec.girarY(V3(x, y, z), giro)
        val frente = listOf(q(-w, -h, -d), q(w, -h, -d), q(w, h, -d), q(-w, h, -d))
        val lado = listOf(q(w, -h, -d), q(w, -h, d), q(w, h, d), q(w, h, -d))
        val tapa = listOf(q(-w, h, -d), q(w, h, -d), q(w, h, d), q(-w, h, d))
        cara(s, a, lado, Vec.girarY(V3(1f, 0f, 0f), giro), C.CUERO, lejos)
        cara(s, a, tapa, V3(0f, 1f, 0f), C.CUERO, lejos)
        val f = cara(s, a, frente, Vec.girarY(V3(0f, 0f, -1f), giro), C.CUERO, lejos)
        if (f != null) {
            // La tapa de cuero que cae por delante, con su brillo de cera, y la placa de latón.
            val c = s.p(q(0f, h * 0.15f, -d - 0.004f)); val k = s.escala(p.x, p.z)
            drawPath(poligono(s.p(q(-w, h, -d - 0.003f)), s.p(q(w, h, -d - 0.003f)), s.p(q(w * 0.95f, -h * 0.45f, -d - 0.003f)), s.p(q(-w * 0.95f, -h * 0.45f, -d - 0.003f))), a.plano(lerp(C.CUERO, C.CENIZA, 0.3f), c, 0.3f, lejos))
            drawOval(a.plano(C.LATON, c, 0.4f, lejos), Offset(c.x - 0.03f * k, c.y - 0.024f * k), Size(0.06f * k, 0.048f * k))
            drawLine(a.plano(lerp(C.CUERO, C.CORREA, 0.25f), c, 0.9f, lejos), s.p(q(-w, h, -d - 0.004f)), s.p(q(w, h, -d - 0.004f)), (0.008f * k).coerceIn(0.25f, 1f))
        }
        // La correa, que sube.
        drawLine(a.plano(C.CORREA, s.p(p), 0f, lejos), s.p(q(-w * 0.8f, h, 0f)), s.p(q(-w * 0.2f, h + 0.18f, 0.02f)), (0.03f * s.escala(p.x, p.z)).coerceIn(0.35f, 2.4f))
        drawLine(a.plano(C.CORREA, s.p(p), 0f, lejos), s.p(q(w * 0.8f, h, 0f)), s.p(q(w * 0.2f, h + 0.18f, 0.02f)), (0.03f * s.escala(p.x, p.z)).coerceIn(0.35f, 2.4f))
    }

    // =================================================================
    //  El chacó
    // =================================================================

    /**
     * El chacó: fieltro negro, un poco más ancho arriba, con la visera de cuero, la placa de latón con el águila delante,
     * el galón de arriba, las escamas del barboquejo y el pompón rojo. [base] es el centro de su borde de abajo, [arriba]
     * su eje y [frente] hacia dónde mira la placa. Medidas de uno de 1812 **[Aproximación]**: 0.19 de alto.
     */
    fun DrawScope.chaco(s: Sitio, a: Alumbre, base: V3, arriba: V3, frente: V3, lejos: Float = 0f, nieve: Float = 0f) {
        val e = Vec.unidad(arriba); val f0 = Vec.unidad(frente); val f = Vec.unidad(f0 - e * Vec.punto(f0, e)); val lado = Vec.cruz(e, f)
        val fieltro = lerp(C.CUERO, C.AZUL, 0.18f)
        val r0 = 0.092f; val r1 = 0.112f; val h = 0.19f
        val k = s.escala(base.x, base.z)
        // La visera, por debajo del frente: media luna de cuero.
        val vis = (0..10).map { i -> val t = PI.toFloat() * i / 10f; base + f * (r0 * 0.6f + 0.06f * sin(t)) + lado * (r0 * 1.02f * cos(t)) - e * 0.004f }
        drawPath(poligono(vis.map { s.p(it) }), a.cara(C.CUERO, s.normal(Vec.unidad(f * 0.4f + e * -0.9f)), s.p(base), lejos))
        cilindro(s, a, base, e, r0, r1, h, fieltro, lerp(fieltro, C.CUERO, 0.5f), 16, lejos, null)
        // El galón de arriba, de cuero, y el de abajo.
        for ((y, g) in listOf(h * 0.97f to 0.016f, h * 0.05f to 0.012f)) {
            val r = r0 + (r1 - r0) * y / h
            val pts = (0..16).map { i -> val t = PI.toFloat() * (0.15f + 0.7f * i / 16f); base + e * y + f * (r * 1.01f * sin(t)) + lado * (r * 1.01f * cos(t)) }.map { s.p(it) }
            drawPath(curva(pts), a.plano(C.CUERO, s.p(base + e * y + f * r), 0f, lejos), style = Stroke((g * k).coerceIn(0.3f, 2.4f)))
        }
        // La placa de latón (un rombo con el águila), mirando al frente.
        val pc = base + e * (h * 0.48f) + f * (r0 + (r1 - r0) * 0.48f + 0.003f)
        val placa = listOf(pc + e * 0.065f, pc + lado * 0.05f, pc - e * 0.055f, pc - lado * 0.05f)
        if (s.seVe(pc, f)) {
            val pp = placa.map { s.p(it) }
            drawPath(poligono(pp), a.cara(C.LATON, s.normal(f), s.p(pc), lejos))
            drawPath(poligono(pp), a.cara(lerp(C.LATON, C.CENIZA, 0.3f), s.normal(f), s.p(pc), lejos), style = Stroke((0.006f * k).coerceIn(0.2f, 0.8f)))
            // El águila, una mancha con las alas abiertas.
            val c = s.p(pc + e * 0.006f)
            drawOval(a.cara(lerp(C.LATON, C.ORO, 0.6f), s.normal(f), c, lejos), Offset(c.x - 0.022f * k, c.y - 0.01f * k), Size(0.044f * k, 0.018f * k))
            // Las escamas del barboquejo, recogidas sobre la visera.
            for (sg in listOf(-1f, 1f)) {
                val q0 = s.p(base + e * 0.02f + lado * (r0 * 0.98f * sg) + f * 0.02f); val q1 = s.p(base + e * 0.1f + lado * (r0 * 0.7f * sg) + f * (r0 * 0.7f))
                drawLine(a.cara(C.LATON, s.normal(f), q0, lejos), q0, q1, (0.012f * k).coerceIn(0.3f, 1.5f), StrokeCap.Round)
            }
        }
        // La escarapela y el pompón rojo, arriba, delante.
        val pom = base + e * (h + 0.035f) + f * (r1 * 0.75f)
        val pq = s.p(pom); val rp = (0.034f * k).coerceAtLeast(0.6f)
        drawCircle(a.cara(C.ROJO, s.normal(Vec.unidad(f + e)), pq, lejos), rp, pq)
        drawCircle(a.cara(lerp(C.ROJO, Color.White, 0.2f), s.normal(e), pq, lejos), rp * 0.55f, pq + Offset(if (a.p.solALaDerecha) rp * 0.3f else -rp * 0.3f, -rp * 0.3f))
        val esc = s.p(base + e * (h * 0.93f) + f * (r1 + 0.002f))
        drawCircle(a.cara(C.AZUL, s.normal(f), esc, lejos), (0.02f * k).coerceAtLeast(0.4f), esc)
        drawCircle(a.cara(C.ROJO, s.normal(f), esc, lejos), (0.011f * k).coerceAtLeast(0.25f), esc)
        if (nieve > 0.2f) {
            // Un copete de nieve en el plato de arriba.
            val t = s.p(base + e * h)
            drawOval(a.arriba(C.NIEVE, t, lejos), Offset(t.x - r1 * 0.8f * k, t.y - 0.03f * k), Size(r1 * 1.6f * k, 0.04f * k))
        }
    }

    // =================================================================
    //  La mochila con la manta enrollada
    // =================================================================

    /**
     * La mochila de piel de vaca con el pelo por fuera, cuadrada, con la manta (o el capote) enrollada encima y atada con
     * dos correas, en [s]. Echada en el suelo con la espalda hacia arriba si [tumbada]; si no, de pie. [manchas] cambia las
     * manchas del pelo. Medidas **[Aproximación]**: 0.36 de ancho, 0.32 de alto, 0.14 de grueso. Devuelve su silueta y el
     * centro de su cara de arriba (donde puede ir algo encima).
     */
    fun DrawScope.mochila(s: Sitio, a: Alumbre, manchas: Long, tumbada: Boolean = false, rollo: Color = C.CAPOTE, lejos: Float = 0f, nieve: Float = 0f): Pair<Path, Offset> {
        val az = Azar(manchas)
        val w = 0.19f; val h = if (tumbada) 0.15f else 0.32f; val d = if (tumbada) 0.17f else 0.075f
        val caras = caja(s, a, -w, w, 0f, h, -d, d, C.PIEL, lejos, arriba = if (tumbada) C.PIEL else lerp(C.PIEL, C.CUERO, 0.3f))
        // Las manchas blancas de la piel y el pelo, en las caras que se ven.
        for ((k3, f) in caras) {
            clipPath(f) {
                val c = centroDe(k3.q.map { s.p(it) })
                repeat(3) { val u = az.entre(0.1f, 0.9f); val v = az.entre(0.1f, 0.9f); val q = s.p(k3.en(u, v)); val r = az.entre(0.05f, 0.09f) * s.k
                    drawPath(TrazosValle.blando(q, r, r * 0.7f, az, 7, 0.3f), a.cara(lerp(C.CORREA, C.NIEVE, 0.4f), s.normal(k3.n), c, lejos)) }
                val pelo = a.cara(lerp(C.PIEL, C.CUERO, 0.4f), s.normal(k3.n), c, lejos).copy(alpha = 0.5f)
                repeat(30) { val q = s.p(k3.en(az.real(), az.real())); drawLine(pelo, q, q + Offset(az.entre(-0.6f, 0.6f), az.entre(0.6f, 1.6f)), 0.35f) }
            }
        }
        // La manta enrollada encima, a lo largo, con sus dos correas.
        val rr = 0.07f; val cy = h + rr * 0.85f
        val sil = cilindro(s, a, V3(-w - 0.04f, cy, if (tumbada) -0.02f else 0f), V3(1f, 0f, 0f), rr, rr, 2f * w + 0.08f, rollo, lerp(rollo, C.CENIZA, 0.15f), 14, lejos)
        // Las vueltas de la manta, en la tapa del rollo que se ve.
        val ladoVisto = if (s.seVe(V3(w + 0.04f, cy, 0f), V3(1f, 0f, 0f))) w + 0.041f else -w - 0.041f
        for (r in listOf(0.045f, 0.025f)) drawPath(TrazosValle.cerrada(circulo(s, V3(ladoVisto, cy, 0f), V3(1f, 0f, 0f), r, 12)), mult(a.plano(rollo, s.p(V3(ladoVisto, cy, 0f)), 0f, lejos), C.SOMBRA, 0.4f), style = Stroke((0.006f * s.k).coerceIn(0.2f, 0.8f)))
        for (x in listOf(-w * 0.55f, w * 0.55f)) {
            val arco = (0..10).map { i -> val t = PI.toFloat() * (-0.15f + 1.3f * i / 10f); s.p(V3(x, cy + rr * 1.04f * sin(t), rr * 1.04f * cos(t) * (if (tumbada) 1f else 1f))) }
            drawPath(curva(arco), a.plano(C.CORREA, arco[5], 0f, lejos), style = Stroke((0.022f * s.k).coerceIn(0.35f, 2.2f)))
        }
        // Las correas de los hombros, por delante, y la hebilla.
        if (!tumbada) for (x in listOf(-w * 0.55f, w * 0.55f)) {
            val p0 = s.p(V3(x, h * 0.92f, -d - 0.005f)); val p1 = s.p(V3(x * 1.2f, h * 0.05f, -d - 0.04f))
            drawPath(curva(combada(p0, p1, 0.02f * s.k, 6)), a.plano(C.CORREA, p0, 0f, lejos), style = Stroke((0.024f * s.k).coerceIn(0.35f, 2f)))
            drawCircle(a.plano(C.LATON, p0, 0f, lejos), (0.012f * s.k).coerceAtLeast(0.3f), p0 + Offset(0f, 0.04f * s.k))
        }
        if (nieve > 0.15f) nieveEncima(a, (0..8).map { i -> s.p(V3(-w - 0.04f + (2f * w + 0.08f) * i / 8f, cy + rr, 0f)) }, (0.03f * s.k * nieve).coerceAtMost(3f), az, lejos)
        val todos = esquinas(-w, w, 0f, h + 2f * rr, -d, d).map { s.p(it) }
        return poligono(envolvente(todos)) to s.p(V3(0f, cy + rr, 0f))
    }

    // =================================================================
    //  El capote, la manta, las ramas
    // =================================================================

    /**
     * Un capote tirado (o una manta extendida, con [manta]): un bulto de paño de borde blando, con sus pliegues de luz y
     * de sombra, el cuello, una manga que sale y, en el capote, las dos filas de botones. [ancho] y [alto] en el lienzo,
     * [mira]: hacia dónde cae. Con nieve, un polvo blanco en lo alto de los pliegues.
     */
    fun DrawScope.capote(a: Alumbre, c: Offset, ancho: Float, alto: Float, az: Azar, manta: Boolean = false, lejos: Float = 0f, nieve: Float = 0f, color: Color = if (manta) lerp(C.CAPOTE, C.LONA, 0.25f) else C.CAPOTE): Path {
        val forma = TrazosValle.blando(c, ancho / 2f, alto / 2f, az, 11, 0.16f, 0.75f)
        drawPath(forma, a.plano(color, c, 0f, lejos, 0.6f))
        clipPath(forma) {
            // La cara de sombra (abajo, del lado contrario al sol) y la de luz.
            val lado = if (a.p.solALaDerecha) 1f else -1f
            mancha(c + Offset(-lado * ancho * 0.25f, alto * 0.25f), ancho * 0.45f, alto * 0.4f, a.plano(color, c, -lado, lejos, -0.3f), 0.8f)
            mancha(c + Offset(lado * ancho * 0.2f, -alto * 0.2f), ancho * 0.35f, alto * 0.3f, a.plano(color, c, lado, lejos, 0.8f), 0.7f)
            // Los pliegues: lomos de luz con su valle oscuro al lado.
            repeat(6) {
                val x = c.x + az.entre(-0.42f, 0.42f) * ancho; val y0 = c.y - alto * az.entre(0.1f, 0.45f); val y1 = c.y + alto * az.entre(0.15f, 0.45f)
                val b = az.entre(-0.12f, 0.12f) * ancho
                val pl = Path().apply { moveTo(x, y0); cubicTo(x + b, y0 + (y1 - y0) * 0.3f, x - b, y0 + (y1 - y0) * 0.7f, x + b * 0.5f, y1) }
                drawPath(pl, a.plano(color, Offset(x, y0), -lado, lejos, -0.5f), style = Stroke(ancho * 0.03f + 0.4f, cap = StrokeCap.Round))
                val pl2 = Path().apply { moveTo(x + 1.2f, y0); cubicTo(x + b + 1.2f, y0 + (y1 - y0) * 0.3f, x - b + 1.2f, y0 + (y1 - y0) * 0.7f, x + b * 0.5f + 1.2f, y1) }
                drawPath(pl2, a.plano(color, Offset(x, y0), lado, lejos, 0.9f), style = Stroke(ancho * 0.02f + 0.3f, cap = StrokeCap.Round))
            }
            if (!manta) {
                // El cuello y las dos filas de botones de latón.
                val q = c + Offset(-ancho * 0.18f, -alto * 0.18f)
                drawOval(a.plano(lerp(color, C.CENIZA, 0.25f), q, 0f, lejos, 0.5f), Offset(q.x - ancho * 0.14f, q.y - alto * 0.12f), Size(ancho * 0.28f, alto * 0.22f))
                for (j in 0 until 4) for (sg in listOf(-1f, 1f)) { val b = c + Offset(ancho * (0.05f + 0.07f * sg), -alto * 0.1f + j * alto * 0.12f); drawCircle(a.plano(C.LATON, b, 0.5f, lejos, 0.6f), (ancho * 0.018f).coerceAtLeast(0.35f), b) }
            } else {
                // Las rayas de la manta.
                for (u in listOf(-0.32f, 0.3f)) drawLine(a.plano(lerp(color, C.AZUL, 0.35f), c, 0f, lejos, 0.5f), c + Offset(ancho * u, -alto * 0.5f), c + Offset(ancho * (u + 0.06f), alto * 0.5f), ancho * 0.03f + 0.3f)
            }
            if (nieve > 0.15f) repeat(5) { val q = c + Offset(az.entre(-0.4f, 0.4f) * ancho, -alto * az.entre(0.05f, 0.4f)); mancha(q, ancho * 0.12f, alto * 0.07f, a.arriba(C.NIEVE, q, lejos), 0.75f * nieve) }
        }
        return forma
    }

    /** Unas ramas de pino cortadas y echadas en el suelo «para dormir encima», en [c] (ancho y alto en el lienzo). */
    fun DrawScope.ramas(a: Alumbre, c: Offset, ancho: Float, alto: Float, az: Azar, lejos: Float = 0f) {
        repeat(9) {
            val q0 = c + Offset(az.entre(-0.45f, 0.35f) * ancho, az.entre(-0.4f, 0.4f) * alto)
            val ang = az.entre(-0.5f, 0.5f) + (if (az.real() > 0.5f) 0f else PI.toFloat())
            val largo = ancho * az.entre(0.3f, 0.5f)
            val q1 = q0 + Offset(cos(ang) * largo, sin(ang) * largo * 0.3f)
            drawLine(a.plano(C.MADERA_OSC, q0, 0f, lejos), q0, q1, 0.9f, StrokeCap.Round)
            val pasos = 9
            for (i in 1..pasos) {
                val t = i / (pasos + 1f); val p = q0 + (q1 - q0) * t; val hoja = (1f - t * 0.6f) * alto * 0.22f
                val col = a.plano(if (i % 2 == 0) C.PINO_LUZ else C.PINO, p, if (i % 3 == 0) 0.5f else -0.3f, lejos, 0.6f)
                drawLine(col, p, p + Offset(hoja * 0.6f, -hoja), 0.7f, StrokeCap.Round)
                drawLine(col, p, p + Offset(-hoja * 0.5f, hoja * 0.5f), 0.7f, StrokeCap.Round)
            }
        }
    }

    // =================================================================
    //  El tambor y la marmita
    // =================================================================

    /**
     * El tambor de la compañía: caja de latón, aros pintados de azul con su borde rojo, la cuerda de ante en zigzag con
     * sus tirantes, el parche de piel con su mancha de uso y los dos palillos. De pie en [s] si no [tumbado]; si no,
     * echado de lado. Medidas **[Aproximación]**: 0.42 de diámetro, 0.42 de alto. Devuelve la silueta.
     */
    fun DrawScope.tambor(s: Sitio, a: Alumbre, tumbado: Boolean = false, lejos: Float = 0f, nieve: Float = 0f): Path {
        val r = 0.21f; val h = 0.42f
        val base = if (tumbado) V3(-h / 2f, r, 0f) else V3(0f, 0f, 0f)
        val eje = if (tumbado) Vec.unidad(V3(1f, 0.05f, 0.25f)) else V3(0f, 1f, 0f)
        val b = Vec.base(eje)
        val sil = cilindro(s, a, base, eje, r, r, h, C.LATON, lerp(C.LONA, C.CORREA, 0.3f), 22, lejos)
        // Los aros, arriba y abajo.
        for (y in listOf(0.012f, h - 0.04f)) cilindroAro(s, a, base + eje * y, eje, r * 1.02f, 0.045f, b, lejos)
        // La cuerda en zigzag, solo por el lado que se ve.
        val k = s.escala(base.x, base.z)
        val top = base + eje * (h - 0.05f); val bot = base + eje * 0.06f
        val n = 12
        for (i in 0 until n) {
            val a0 = 2f * PI.toFloat() * i / n; val a1 = a0 + PI.toFloat() / n
            val p0 = anillo(top, b, r * 1.01f, a0); val p1 = anillo(bot, b, r * 1.01f, a1); val p2 = anillo(top, b, r * 1.01f, a0 + 2f * PI.toFloat() / n)
            val nn = b.first * cos(a1) + b.second * sin(a1)
            if (!s.seVe(p1, nn)) continue
            val col = a.cara(C.CORREA, s.normal(nn), s.p(p1), lejos)
            drawLine(col, s.p(p0), s.p(p1), (0.008f * k).coerceIn(0.25f, 1f)); drawLine(col, s.p(p1), s.p(p2), (0.008f * k).coerceIn(0.25f, 1f))
            // Los tirantes de cuero, en cada vértice de abajo.
            val t0 = anillo(base + eje * 0.08f, b, r * 1.015f, a1); val t1 = anillo(base + eje * 0.13f, b, r * 1.015f, a1)
            drawLine(a.cara(C.CUERO, s.normal(nn), s.p(t0), lejos), s.p(t0), s.p(t1), (0.016f * k).coerceIn(0.3f, 1.6f))
        }
        // La mancha de uso en el parche, donde dan los palillos.
        val parche = base + eje * (h + 0.002f)
        if (s.seVe(parche, eje)) { val q = s.p(parche + b.first * 0.04f); mancha(q, 0.07f * k, 0.035f * k, a.cara(lerp(C.LONA, C.MADERA, 0.4f), s.normal(eje), q, lejos), 0.5f) }
        // Los palillos, cruzados encima (o al lado si está tumbado).
        for ((d0, d1) in listOf(V3(-0.2f, 0.01f, -0.1f) to V3(0.16f, 0.01f, 0.14f), V3(-0.12f, 0.02f, 0.16f) to V3(0.2f, 0.02f, -0.06f))) {
            val q0 = if (tumbado) V3(d0.x, 0.02f, -r - 0.12f + d0.z * 0.3f) else parche + d0; val q1 = if (tumbado) V3(d1.x, 0.03f, -r - 0.18f + d1.z * 0.3f) else parche + d1
            val p0 = s.p(q0); val p1 = s.p(q1)
            drawLine(a.plano(C.MADERA, p0, 0f, lejos), p0, p1, (0.016f * k).coerceIn(0.4f, 1.6f), StrokeCap.Round)
            drawCircle(a.plano(C.MADERA_OSC, p1, 0f, lejos), (0.014f * k).coerceIn(0.4f, 1.6f), p1)
        }
        if (nieve > 0.2f && !tumbado) { val q = s.p(parche); drawOval(a.arriba(C.NIEVE, q, lejos), Offset(q.x - r * 0.7f * k, q.y - 0.02f * k), Size(r * 1.4f * k, 0.05f * k)) }
        return sil
    }

    private fun DrawScope.cilindroAro(s: Sitio, a: Alumbre, base: V3, eje: V3, r: Float, alto: Float, b: Pair<V3, V3>, lejos: Float) {
        cilindro(s, a, base, eje, r, r, alto, C.AZUL, null, 22, lejos, null)
        val pts = (0..22).map { anillo(base + eje * (alto * 0.5f), b, r * 1.003f, 2f * PI.toFloat() * it / 22f) }
        for (i in 0 until 22) { val nn = b.first * cos(2f * PI.toFloat() * (i + 0.5f) / 22f) + b.second * sin(2f * PI.toFloat() * (i + 0.5f) / 22f); if (s.seVe(pts[i], nn)) drawLine(a.cara(C.ROJO, s.normal(nn), s.p(pts[i]), lejos), s.p(pts[i]), s.p(pts[i + 1]), (0.01f * s.escala(base.x, base.z)).coerceIn(0.25f, 1.1f)) }
    }

    /**
     * La marmita «para ocho», de hierro, con su tapa y su asa, en [base] (el centro de su fondo). Con [hirviendo], la
     * tapa ladeada. Devuelve la silueta. **[Aproximación]**: 0.34 de ancho, 0.28 de alto.
     */
    fun DrawScope.marmita(s: Sitio, a: Alumbre, base: V3, hirviendo: Boolean = false, lejos: Float = 0f, tapaEnElSuelo: Boolean = false): Path {
        val r = 0.17f; val h = 0.26f; val e = V3(0f, 1f, 0f); val b = Vec.base(e); val k = s.escala(base.x, base.z)
        // El cuerpo, panzudo: dos troncos de cono.
        val sil = Path()
        sil.addPath(cilindro(s, a, base, e, r * 0.82f, r, h * 0.45f, C.HIERRO, null, 18, lejos, C.HIERRO))
        sil.addPath(cilindro(s, a, base + e * (h * 0.45f), e, r, r * 0.92f, h * 0.55f, C.HIERRO, if (tapaEnElSuelo) lerp(C.CENIZA, C.LONA, 0.15f) else C.HIERRO, 18, lejos))
        // El hollín de abajo y el borde.
        val boca = (0..18).map { s.p(anillo(base + e * h, b, r * 0.92f, 2f * PI.toFloat() * it / 18f)) }
        drawPath(TrazosValle.cerrada(boca), a.plano(lerp(C.HIERRO, C.PLATA, 0.25f), boca[4], 0.5f, lejos), style = Stroke((0.012f * k).coerceIn(0.3f, 1.4f)))
        if (!tapaEnElSuelo) {
            // La tapa, ladeada si hierve, con su asidero.
            val inc = if (hirviendo) 0.25f else 0f
            val tc = base + e * (h + 0.012f) + V3(inc * 0.06f, 0f, 0f)
            val tp = (0 until 18).map { i -> val ang = 2f * PI.toFloat() * i / 18f; val q = anillo(tc, b, r * 0.9f, ang); s.p(q + V3(0f, inc * (q.x - tc.x) * 0.6f, 0f)) }
            drawPath(poligono(tp), a.cara(C.HIERRO, s.normal(e), s.p(tc), lejos))
            drawCircle(a.cara(C.HIERRO, s.normal(e), s.p(tc), lejos), (0.02f * k).coerceAtLeast(0.4f), s.p(tc + V3(0f, 0.02f, 0f)))
        }
        // El asa de alambre, que sube en arco.
        val asa = (0..12).map { i -> val t = PI.toFloat() * i / 12f; s.p(base + V3(-r * cos(t), h + 0.16f * sin(t), 0f)) }
        drawPath(curva(asa), a.plano(C.HIERRO, asa[6], 0f, lejos), style = Stroke((0.008f * k).coerceIn(0.3f, 1f)))
        return sil
    }

    // =================================================================
    //  La hoguera
    // =================================================================

    /**
     * La hoguera de la retirada: «un brazado de ruedas y tablas» de los carros rotos y las casas quemadas. Rayos de rueda
     * torneados, tablas, un trozo de pina y alguna rama, apoyados en pirámide si arde; caídos y negros si no. Alrededor,
     * la nieve derretida y negra (o el barro y la ceniza, sin nieve). [fuego], de 0 (ceniza fría) a 1 (encendido): las
     * puntas que arden se ponen naranjas (dan luz). Las llamas, las chispas y el humo van en la parte viva. Devuelve la
     * silueta de la leña.
     */
    fun DrawScope.hoguera(s: Sitio, a: Alumbre, fuego: Float, nieve: Float, semilla: Long, lejos: Float = 0f): Path {
        val az = Azar(semilla); val k = s.k
        // El suelo: la nieve derretida y negra alrededor; dentro, la ceniza.
        val anillo = (0 until 16).map { i -> val ang = 2f * PI.toFloat() * i / 16f; val r = 0.78f * (1f + az.entre(-0.14f, 0.14f)); s.p(V3(r * cos(ang), 0f, r * sin(ang) * 0.95f)) }
        val ceniza = (0 until 14).map { i -> val ang = 2f * PI.toFloat() * i / 14f; val r = 0.44f * (1f + az.entre(-0.12f, 0.12f)); s.p(V3(r * cos(ang), 0f, r * sin(ang))) }
        val centro = s.p(V3(0f, 0f, 0f))
        if (nieve > 0.1f) {
            val borde = TrazosValle.cerrada(anillo)
            drawPath(borde, a.arriba(lerp(C.BARRO, C.CENIZA, 0.4f), centro, lejos))
            // El agua de la nieve derretida, con su brillo, y el borde de la nieve, blando y sucio.
            drawPath(borde, a.arriba(lerp(C.NIEVE_SUCIA, C.CENIZA, 0.3f), centro, lejos), style = Stroke(0.07f * k, join = StrokeJoin.Round))
            drawPath(borde, a.arriba(C.NIEVE_SOMBRA, centro, lejos), style = Stroke(0.025f * k, join = StrokeJoin.Round))
            repeat(5) { val q = s.p(V3(az.entre(-0.6f, 0.6f), 0f, az.entre(-0.5f, 0.5f))); mancha(q, 0.12f * k, 0.03f * k, a.arriba(lerp(C.PLATA, Color.White, 0.3f), q, lejos), 0.35f) }
        } else {
            drawPath(TrazosValle.cerrada(anillo), a.arriba(lerp(C.BARRO, C.CENIZA, 0.25f), centro, lejos))
        }
        drawPath(TrazosValle.cerrada(ceniza), a.arriba(lerp(C.CENIZA, C.LONA, 0.35f), centro, lejos))
        repeat(14) { val q = s.p(V3(az.entre(-0.38f, 0.38f), 0f, az.entre(-0.34f, 0.34f))); mancha(q, az.entre(0.03f, 0.07f) * k, 0.02f * k, a.arriba(if (az.real() > 0.5f) C.CENIZA else lerp(C.CENIZA, C.LONA, 0.6f), q, lejos), 0.8f) }
        // Las brasas del fondo (dan luz).
        if (fuego > 0.02f) {
            drawPath(TrazosValle.cerrada(ceniza.map { centro + (it - centro) * 0.6f }), a.brasa(fuego).copy(alpha = (0.35f + 0.5f * fuego).coerceIn(0f, 1f)))
            repeat(10) { val q = s.p(V3(az.entre(-0.24f, 0.24f), 0.01f, az.entre(-0.2f, 0.2f))); drawCircle(a.brasa(0.6f + 0.4f * az.real()).copy(alpha = (0.5f + 0.5f * fuego).coerceIn(0f, 1f)), az.entre(0.012f, 0.03f) * k, q) }
        }
        // La leña: en pirámide si arde, caída si no.
        val forma = Path()
        val enPie = fuego >= 0.45f
        val palos = ArrayList<Triple<V3, V3, Int>>()                                    // de, a, tipo (0 rayo, 1 tabla, 2 pina)
        val n = 7
        for (i in 0 until n) {
            val ang = 2f * PI.toFloat() * i / n + az.entre(-0.25f, 0.25f)
            val r = az.entre(0.36f, 0.5f)
            val pie = V3(r * cos(ang), 0f, r * sin(ang))
            val punta = if (enPie) V3(az.entre(-0.06f, 0.06f), az.entre(0.42f, 0.56f), az.entre(-0.05f, 0.05f)) else V3(pie.x * az.entre(-0.4f, 0.1f), az.entre(0.03f, 0.1f), pie.z * az.entre(-0.4f, 0.1f))
            palos += Triple(pie, punta, if (i % 3 == 1) 1 else 0)
        }
        // Un trozo de pina de rueda, curvo, atravesado.
        palos += Triple(V3(-0.5f, 0.02f, 0.12f), V3(0.42f, 0.06f, -0.22f), 2)
        for ((de, hacia, tipo) in palos.sortedByDescending { s.hondo((it.first + it.second) * 0.5f) }) {
            val p0 = s.p(de); val p1 = s.p(hacia); val d = p1 - p0; val ln = d.getDistance().coerceAtLeast(0.01f); val nn = Offset(-d.y, d.x) / ln
            val g = when (tipo) { 1 -> 0.06f; 2 -> 0.05f; else -> 0.03f } * k
            val tronco = when (tipo) {
                2 -> { val m = (p0 + p1) / 2f + nn * (0.12f * k); Path().apply { moveTo(p0 + nn * g); cubicTo(p0 + (m - p0) * 0.7f + nn * g, m + nn * g, p1 + nn * g * 0.8f); lineTo(p1 - nn * g * 0.8f); cubicTo(m - nn * g, p0 + (m - p0) * 0.7f - nn * g, p0 - nn * g) ; close() } }
                else -> poligono(p0 + nn * g, p1 + nn * (g * if (tipo == 1) 1f else 0.6f), p1 - nn * (g * if (tipo == 1) 1f else 0.6f), p0 - nn * g)
            }
            val madera = when (tipo) { 1 -> C.MADERA_GRIS; 2 -> C.MADERA; else -> lerp(C.MADERA, C.LONA, 0.15f) }
            // Quemada: más negra cuanto más dentro del fuego (y toda si es ceniza).
            val quemado = if (fuego > 0.02f) 0.55f else 0.85f
            val base = a.plano(lerp(madera, C.CENIZA, 0.25f), (p0 + p1) / 2f, if (nn.x > 0f) 0.5f else -0.5f, lejos)
            drawPath(tronco, Brush.linearGradient(listOf(base, lerp(base, a.plano(C.CENIZA, p1, 0f, lejos), quemado), a.plano(C.CENIZA, p1, 0f, lejos)), p0, p1))
            // El canto de luz.
            drawLine(a.plano(madera, p0, if (a.p.solALaDerecha) 0.9f else -0.9f, lejos), p0 + nn * g * 0.7f, p0 + d * 0.55f + nn * g * 0.6f, (g * 0.35f).coerceAtLeast(0.25f))
            if (tipo == 0) { val m = p0 + d * 0.22f; drawLine(mult(base, C.SOMBRA, 0.4f), m + nn * g, m - nn * g, (0.008f * k).coerceAtLeast(0.25f)) }   // el anillo torneado del rayo
            if (tipo == 1) drawLine(mult(base, C.SOMBRA, 0.35f), p0, p1, (0.006f * k).coerceAtLeast(0.2f))                                                  // la junta de la tabla
            // La punta que arde (da luz): un filo naranja.
            if (fuego > 0.02f) { val q = p0 + d * 0.86f; drawLine(a.brasa(fuego).copy(alpha = (0.4f + 0.6f * fuego).coerceIn(0f, 1f)), q, p1, g * 1.4f, StrokeCap.Round) }
            forma.addPath(tronco)
        }
        // La ceniza vieja cubierta de nieve nueva, si está fría y nieva sobre ella.
        if (fuego <= 0.02f && nieve > 0.5f) repeat(6) { val q = s.p(V3(az.entre(-0.4f, 0.4f), 0.02f, az.entre(-0.3f, 0.3f))); mancha(q, 0.1f * k, 0.03f * k, a.arriba(C.NIEVE, q, lejos), 0.6f) }
        return forma
    }

    /** Las dos horquillas y la vara de la que cuelga la marmita sobre el fuego (un baqueta de fusil, una rama). */
    fun DrawScope.horquillas(s: Sitio, a: Alumbre, lejos: Float = 0f): V3 {
        val k = s.k
        val izq = V3(-0.52f, 0f, 0.05f); val der = V3(0.5f, 0f, -0.04f); val alto = 0.72f
        for (p in listOf(izq, der)) {
            val top = p + V3(0f, alto, 0f)
            drawLine(a.plano(C.MADERA_OSC, s.p(p), 0f, lejos), s.p(p), s.p(top), (0.03f * k).coerceIn(0.5f, 3f), StrokeCap.Round)
            drawLine(a.plano(C.MADERA_OSC, s.p(top), 0f, lejos), s.p(top), s.p(top + V3(-0.06f, 0.08f, 0f)), (0.02f * k).coerceIn(0.4f, 2f), StrokeCap.Round)
            drawLine(a.plano(C.MADERA_OSC, s.p(top), 0f, lejos), s.p(top), s.p(top + V3(0.06f, 0.07f, 0f)), (0.02f * k).coerceIn(0.4f, 2f), StrokeCap.Round)
        }
        val v0 = izq + V3(-0.08f, alto + 0.02f, 0f); val v1 = der + V3(0.1f, alto + 0.02f, 0f)
        drawLine(a.plano(C.HIERRO, s.p(v0), 0f, lejos), s.p(v0), s.p(v1), (0.014f * k).coerceIn(0.4f, 1.6f), StrokeCap.Round)
        return (v0 + v1) * 0.5f
    }

    // =================================================================
    //  El cofre abierto con el cuadro
    // =================================================================

    /**
     * El cofre abierto de lo robado en Moscú, medio hundido en la nieve: de madera con flejes de hierro y cantoneras de
     * latón, la tapa abombada abierta hacia atrás, dentro telas (un terciopelo rojo y un brocado de oro que cuelgan por el
     * borde) y, apoyados, **un candelabro de plata** de tres brazos y **un cuadro de un santo con su marco dorado** («un
     * cuadro de un santo», dice el cuento): la imagen dentro de la imagen. Un icono de fondo de oro, con la aureola, el
     * manto rojo y la túnica azul, en pocas manchas, sin cara dibujada **[Diseño propio]**. Medidas **[Aproximación]**:
     * 0.8 de largo, 0.46 de alto. [hundido]: cuánto lo tapa la nieve (de 0 a 1). Devuelve la silueta.
     */
    fun DrawScope.cofre(s: Sitio, a: Alumbre, hundido: Float, lejos: Float = 0f, nieve: Float = 0f): Path {
        val w = 0.4f; val h = 0.34f; val d = 0.24f; val az = Azar(Semilla.de("napoleon", "cofre"))
        val k = s.k
        // La tapa, abierta hacia atrás y ladeada: su cara de dentro mira a quien mira.
        val gozne = V3(0f, h, d)
        val tapaQ = listOf(V3(-w, h, d), V3(w, h, d), V3(w, h + 0.3f, d + 0.12f), V3(-w, h + 0.3f, d + 0.12f))
        val nTapa = Vec.unidad(V3(0f, -0.37f, -0.93f))
        cara(s, a, tapaQ, nTapa, lerp(C.TERCIOPELO, C.MADERA, 0.35f), lejos)
        // El forro de la tapa: un papel pintado con flores, viejo.
        val forro = tapaQ.map { V3(it.x * 0.9f, h + (it.y - h) * 0.9f + 0.015f, d + (it.z - d) * 0.9f - 0.005f) }
        val fp = forro.map { s.p(it) }
        drawPath(poligono(fp), a.cara(lerp(C.LONA, C.ROJO, 0.18f), s.normal(nTapa), centroDe(fp), lejos))
        clipPath(poligono(fp)) { repeat(16) { val q = centroDe(fp) + Offset(az.entre(-0.45f, 0.45f) * w * k, az.entre(-0.14f, 0.14f) * k); drawCircle(a.cara(lerp(C.LONA, C.ROJO, 0.4f), s.normal(nTapa), q, lejos).copy(alpha = 0.6f), 0.01f * k, q) } }
        // La caja.
        val caras = caja(s, a, -w, w, 0f, h, -d, d, C.MADERA_GRIS, lejos, arriba = C.CENIZA)
        for ((c3, f) in caras) {
            if (c3.arriba) continue
            clipPath(f) {
                val cc = centroDe(c3.q.map { s.p(it) })
                // Las vetas de la madera y los flejes de hierro.
                for (v in listOf(0.3f, 0.62f)) linea(s, c3, 0f, v, 1f, v + 0.02f, mult(a.cara(C.MADERA_GRIS, s.normal(c3.n), cc, lejos), C.SOMBRA, 0.35f), (0.006f * k).coerceAtLeast(0.25f))
                for (u in listOf(0.18f, 0.82f)) linea(s, c3, u, 0f, u, 1f, a.cara(C.HIERRO, s.normal(c3.n), cc, lejos), (0.03f * k).coerceAtLeast(0.4f))
                linea(s, c3, 0f, 0.96f, 1f, 0.96f, a.cara(C.LATON, s.normal(c3.n), cc, lejos), (0.02f * k).coerceAtLeast(0.3f))
            }
        }
        // El hueco de dentro, oscuro, y las telas que asoman y cuelgan por el borde de delante.
        val boca = listOf(V3(-w * 0.94f, h, -d * 0.9f), V3(w * 0.94f, h, -d * 0.9f), V3(w * 0.94f, h, d * 0.9f), V3(-w * 0.94f, h, d * 0.9f)).map { s.p(it) }
        drawPath(poligono(boca), a.arriba(lerp(C.CENIZA, C.TERCIOPELO, 0.4f), centroDe(boca), lejos))
        val ter0 = s.p(V3(-w * 0.85f, h + 0.05f, -d * 0.2f)); val ter1 = s.p(V3(-w * 0.05f, h + 0.07f, -d * 0.1f))
        val terc = Path().apply { moveTo(ter0); cubicTo(ter0 + Offset(0f, -0.08f * k), ter1 + Offset(-0.1f * k, -0.08f * k), ter1); lineTo(s.p(V3(-w * 0.1f, h, -d - 0.02f))); cubicTo(s.p(V3(-w * 0.2f, h * 0.5f, -d - 0.04f)), s.p(V3(-w * 0.45f, h * 0.3f, -d - 0.03f)), s.p(V3(-w * 0.55f, h * 0.35f, -d - 0.02f))); lineTo(s.p(V3(-w * 0.8f, h, -d - 0.01f))); close() }
        drawPath(terc, a.plano(C.TERCIOPELO, ter1, -0.2f, lejos, 0.5f))
        drawPath(Path().apply { moveTo(s.p(V3(-w * 0.2f, h, -d - 0.025f))); cubicTo(s.p(V3(-w * 0.25f, h * 0.6f, -d - 0.04f)), s.p(V3(-w * 0.35f, h * 0.45f, -d - 0.04f)), s.p(V3(-w * 0.48f, h * 0.42f, -d - 0.03f))) },
            a.plano(lerp(C.TERCIOPELO, C.ROJO, 0.7f), ter1, 0.8f, lejos, 0.5f), style = Stroke(0.02f * k, cap = StrokeCap.Round))
        // El brocado de oro.
        val bro = s.p(V3(w * 0.35f, h + 0.04f, -d * 0.3f))
        drawPath(Path().apply { moveTo(bro + Offset(-0.12f * k, 0f)); cubicTo(bro + Offset(-0.05f * k, -0.07f * k), bro + Offset(0.1f * k, -0.05f * k), bro + Offset(0.16f * k, 0.02f * k)); lineTo(s.p(V3(w * 0.6f, h * 0.55f, -d - 0.02f))); lineTo(s.p(V3(w * 0.2f, h * 0.7f, -d - 0.02f))); close() },
            a.plano(lerp(C.ORO, C.LONA, 0.2f), bro, 0.3f, lejos, 0.5f))
        repeat(8) { val q = bro + Offset(az.entre(-0.1f, 0.12f) * k, az.entre(-0.02f, 0.12f) * k); drawCircle(a.plano(C.ORO, q, 0.6f, lejos, 0.6f), 0.008f * k, q) }
        // El candelabro de plata, ladeado contra el borde.
        candelabro(s, a, V3(w * 0.55f, h - 0.04f, -d * 0.55f), Vec.unidad(V3(0.75f, 0.85f, -0.45f)), lejos)
        // El cuadro, apoyado delante, contra el costado del cofre, mirando hacia el fuego y el cielo.
        cuadroDelSanto(s, a, V3(-w - 0.05f, 0.0f, -d - 0.12f), lejos)
        // La nieve: hunde el pie del cofre y se posa en los cantos.
        if (nieve > 0.1f) {
            val pie = (0..10).map { i -> s.p(V3(-w - 0.15f + (2f * w + 0.3f) * i / 10f, (0.05f + 0.06f * sin(i * 1.7f)) * hundido + 0.02f, -d - 0.05f)) }
            drawPath(curva(pie + listOf(pie.last() + Offset(0f, 0.12f * k), pie.first() + Offset(0f, 0.12f * k))).apply { close() }, a.arriba(C.NIEVE, pie[5], lejos))
            nieveEncima(a, listOf(s.p(V3(-w, h, -d)), s.p(V3(-w * 0.5f, h, -d)), s.p(V3(-w * 0.1f, h, -d))), 0.018f * k * nieve, az, lejos)
        }
        return poligono(envolvente(esquinas(-w, w, 0f, h + 0.3f, -d, d + 0.12f).map { s.p(it) }))
    }

    /**
     * El cuadro del santo en su marco dorado, apoyado en el suelo en [pie] e inclinado hacia atrás: el marco con su
     * moldura (luz arriba, sombra en el canto de abajo), el fondo de oro, la aureola, el manto y la túnica, una grieta y
     * un desconchón. La luz lo enciende: el oro y el rojo suben donde le da.
     */
    fun DrawScope.cuadroDelSanto(s: Sitio, a: Alumbre, pie: V3, lejos: Float = 0f) {
        val ancho = 0.42f; val alto = 0.52f; val inc = 0.32f
        val n = Vec.unidad(V3(0.35f, inc, -1f))
        fun q(u: Float, v: Float) = pie + V3(u * ancho * 0.94f, v * alto, -u * ancho * 0.33f + v * alto * inc)
        val marco = listOf(q(0f, 0f), q(1f, 0f), q(1f, 1f), q(0f, 1f))
        val mp = marco.map { s.p(it) }; val cc = centroDe(mp); val k = s.escala(pie.x, pie.z)
        val oro = a.cara(C.ORO, s.normal(n), cc, lejos)
        drawPath(poligono(mp), oro)
        // La moldura: el canto de arriba claro, el de abajo y el de un lado en sombra, y los adornos.
        drawLine(lerp(oro, Color.White, 0.25f), mp[3], mp[2], (0.025f * k).coerceIn(0.3f, 2.4f))
        drawLine(mult(oro, C.SOMBRA, 0.5f), mp[0], mp[1], (0.03f * k).coerceIn(0.3f, 2.6f))
        drawLine(mult(oro, C.SOMBRA, 0.3f), mp[1], mp[2], (0.02f * k).coerceIn(0.3f, 2f))
        for ((u, v) in listOf(0.03f to 0.03f, 0.97f to 0.03f, 0.97f to 0.97f, 0.03f to 0.97f)) drawCircle(lerp(oro, Color.White, 0.2f), 0.02f * k, s.p(q(u, v)))
        // La pintura, dentro del marco.
        val tela = listOf(q(0.1f, 0.08f), q(0.9f, 0.08f), q(0.9f, 0.92f), q(0.1f, 0.92f)).map { s.p(it) }
        val tf = poligono(tela)
        drawPath(tf, a.cara(lerp(C.ORO, C.MADERA, 0.25f), s.normal(n), cc, lejos))
        clipPath(tf) {
            // El fondo de oro, con el craquelado.
            fun en(u: Float, v: Float) = s.p(q(0.1f + 0.8f * u, 0.08f + 0.84f * v))
            drawPath(poligono(en(0f, 0.72f), en(1f, 0.72f), en(1f, 1f), en(0f, 1f)), a.cara(lerp(C.ORO, Color.White, 0.12f), s.normal(n), cc, lejos))
            // La aureola.
            val cab = en(0.5f, 0.76f); val rr = 0.06f * k
            drawCircle(a.cara(lerp(C.ORO, Color.White, 0.35f), s.normal(n), cab, lejos), rr * 1.6f, cab)
            drawCircle(mult(a.cara(C.ORO, s.normal(n), cab, lejos), C.SOMBRA, 0.35f), rr * 1.6f, cab, style = Stroke((0.006f * k).coerceAtLeast(0.25f)))
            // El rostro, solo una mancha clara sin facciones, y el manto.
            drawOval(a.cara(lerp(C.PIEL, C.LONA, 0.55f), s.normal(n), cab, lejos), Offset(cab.x - rr * 0.6f, cab.y - rr * 0.75f), Size(rr * 1.2f, rr * 1.5f))
            val manto = Path().apply { moveTo(en(0.5f, 0.68f)); cubicTo(en(0.2f, 0.62f), en(0.08f, 0.2f), en(0.12f, -0.02f)); lineTo(en(0.88f, -0.02f)); cubicTo(en(0.92f, 0.2f), en(0.8f, 0.62f), en(0.5f, 0.68f)); close() }
            drawPath(manto, a.cara(C.ROJO, s.normal(n), cc, lejos))
            val tunica = Path().apply { moveTo(en(0.5f, 0.6f)); cubicTo(en(0.38f, 0.5f), en(0.36f, 0.2f), en(0.4f, -0.02f)); lineTo(en(0.62f, -0.02f)); cubicTo(en(0.64f, 0.2f), en(0.62f, 0.5f), en(0.5f, 0.6f)); close() }
            drawPath(tunica, a.cara(C.AZUL, s.normal(n), cc, lejos))
            // Los pliegues dorados del manto (el asist) y la mano con el libro.
            for (u in listOf(0.3f, 0.7f)) drawLine(a.cara(C.ORO, s.normal(n), cc, lejos), en(u, 0.5f), en(u + (0.5f - u) * 0.3f, 0.05f), (0.005f * k).coerceAtLeast(0.22f))
            val libro = en(0.56f, 0.38f); drawRect(a.cara(lerp(C.ROJO, C.CUERO, 0.5f), s.normal(n), libro, lejos), libro - Offset(0.025f * k, 0.03f * k), Size(0.05f * k, 0.06f * k))
            // El craquelado y un desconchón.
            val gr = mult(a.cara(C.ORO, s.normal(n), cc, lejos), C.SOMBRA, 0.45f)
            drawLine(gr, en(0.05f, 0.9f), en(0.4f, 0.55f), (0.004f * k).coerceAtLeast(0.2f)); drawLine(gr, en(0.4f, 0.55f), en(0.35f, 0.3f), (0.004f * k).coerceAtLeast(0.2f))
            val des = en(0.82f, 0.18f); drawOval(a.cara(lerp(C.LONA, C.MADERA, 0.2f), s.normal(n), des, lejos), des - Offset(0.03f * k, 0.015f * k), Size(0.06f * k, 0.03f * k))
        }
        // La sombra que el marco echa sobre la pintura (arriba y del lado contrario a la luz).
        drawLine(mult(a.cara(C.ORO, s.normal(n), cc, lejos), C.SOMBRA, 0.55f).copy(alpha = 0.6f), tela[3], tela[2], (0.012f * k).coerceIn(0.3f, 1.4f))
    }

    /** Un candelabro de plata de tres brazos, con su pie, apoyado en [base] y con el eje [eje]; sin velas, con un cabo. */
    fun DrawScope.candelabro(s: Sitio, a: Alumbre, base: V3, eje: V3, lejos: Float = 0f) {
        val e = Vec.unidad(eje); val lado = Vec.unidad(Vec.cruz(e, V3(0f, 0f, -1f))); val k = s.escala(base.x, base.z)
        val pl = C.PLATA
        cilindro(s, a, base, e, 0.07f, 0.02f, 0.05f, pl, pl, 12, lejos)
        val fuste = base + e * 0.32f
        drawLine(a.plano(pl, s.p(base), 0f, lejos), s.p(base), s.p(fuste), (0.022f * k).coerceIn(0.4f, 2.4f), StrokeCap.Round)
        for (j in listOf(0.12f, 0.22f)) { val q = s.p(base + e * j); drawCircle(a.plano(pl, q, 0.3f, lejos), (0.02f * k).coerceAtLeast(0.4f), q) }
        for (sg in listOf(-1f, 0f, 1f)) {
            val b0 = fuste; val b1 = fuste + lado * (0.13f * sg) + e * (0.04f + if (sg == 0f) 0.08f else 0f)
            val p0 = s.p(b0); val p1 = s.p(b1)
            drawPath(curva(listOf(p0, (p0 + p1) / 2f + Offset(0f, 0.03f * k * abs(sg)), p1)), a.plano(pl, p1, sg, lejos), style = Stroke((0.012f * k).coerceIn(0.35f, 1.5f), cap = StrokeCap.Round))
            val cup = s.p(b1 + e * 0.02f)
            drawOval(a.plano(pl, cup, sg, lejos), cup - Offset(0.025f * k, 0.012f * k), Size(0.05f * k, 0.03f * k))
            drawOval(lerp(a.plano(pl, cup, sg, lejos), Color.White, 0.3f), cup - Offset(0.012f * k, 0.012f * k), Size(0.02f * k, 0.008f * k))
            if (sg == 1f) { val v = s.p(b1 + e * 0.07f); drawLine(a.plano(C.LONA, v, 0.5f, lejos), cup, v, (0.016f * k).coerceIn(0.35f, 1.6f)) }
        }
    }

    /**
     * Un espejo ovalado de marco dorado, tirado en la nieve y un poco alzado por un lado, roto: el marco con su moldura
     * (la luz arriba, la sombra en el canto de abajo y sus hojas talladas) y el cristal, que devuelve el cielo
     * ([cieloAlto] y [cieloBajo]) y una rama oscura del pino, con las grietas en estrella y un trozo que falta.
     */
    fun DrawScope.espejo(s: Sitio, a: Alumbre, cieloAlto: Color, cieloBajo: Color, lejos: Float = 0f) {
        val k = s.k; val rx = 0.3f; val rz = 0.21f
        val n = Vec.unidad(V3(-0.15f, 1f, -0.32f))
        fun q(ang: Float, r: Float): V3 { val x = rx * r * cos(ang); val z = rz * r * sin(ang); return V3(x, 0.04f + 0.32f * (-z) + 0.15f * x, z) }
        val fuera = (0 until 28).map { s.p(q(2f * PI.toFloat() * it / 28, 1f)) }
        val dentro = (0 until 28).map { s.p(q(2f * PI.toFloat() * it / 28, 0.8f)) }
        val c = centroDe(fuera)
        // El canto del marco, de grueso, por debajo.
        drawPath(TrazosValle.cerrada(fuera.map { it + Offset(0f, 0.035f * k) }), mult(a.cara(C.ORO, s.normal(n), c, lejos), C.SOMBRA, 0.55f))
        drawPath(TrazosValle.cerrada(fuera), a.cara(C.ORO, s.normal(n), c, lejos))
        // La moldura: la luz en el lado de arriba y las hojas talladas.
        drawPath(curva(fuera.subList(15, 27)), lerp(a.cara(C.ORO, s.normal(n), c, lejos), Color.White, 0.3f), style = Stroke((0.012f * k).coerceIn(0.3f, 1.4f), cap = StrokeCap.Round))
        for (i in 0 until 14) { val p = fuera[i * 2] + (dentro[i * 2] - fuera[i * 2]) * 0.5f; drawCircle(mult(a.cara(C.ORO, s.normal(n), c, lejos), C.SOMBRA, 0.3f), (0.012f * k).coerceAtLeast(0.3f), p) }
        val vf = TrazosValle.cerrada(dentro)
        drawPath(vf, Brush.verticalGradient(listOf(cieloBajo, cieloAlto), dentro.minOf { it.y }, dentro.maxOf { it.y }))
        clipPath(vf) {
            // La rama del pino que se refleja, oscura, y su nieve.
            val r0 = c + Offset(-0.32f * k, -0.04f * k); val r1 = c + Offset(0.1f * k, 0.06f * k)
            drawLine(lerp(cieloAlto, C.PINO_OSC, 0.75f), r0, r1, (0.05f * k).coerceAtLeast(0.8f), StrokeCap.Round)
            drawLine(lerp(cieloAlto, C.NIEVE, 0.6f), r0 + Offset(0f, 0.02f * k), r1 + Offset(0f, 0.02f * k), (0.015f * k).coerceAtLeast(0.3f), StrokeCap.Round)
            // Las grietas en estrella, el trozo que falta (se ve la tabla de detrás) y un brillo.
            val g = c + Offset(0.06f * k, -0.01f * k); val gr = lerp(cieloAlto, Color.White, 0.6f)
            for (i in 0 until 7) { val ang = i * 0.9f + 0.3f; drawLine(gr, g, g + Offset(cos(ang), sin(ang) * 0.4f) * (0.28f * k), (0.006f * k).coerceAtLeast(0.25f)) }
            drawPath(poligono(g, g + Offset(0.13f * k, -0.03f * k), g + Offset(0.1f * k, 0.045f * k)), a.cara(lerp(C.MADERA, C.CENIZA, 0.3f), s.normal(n), g, lejos))
            drawLine(Color.White.copy(alpha = 0.55f), dentro[18], dentro[4], (0.014f * k).coerceAtLeast(0.3f))
        }
    }

    // =================================================================
    //  El águila envuelta en trapos
    // =================================================================

    /**
     * El asta del regimiento con el águila, de [pie] a [punta] (en el lienzo), apoyada. «Las águilas van envueltas en
     * trapos»: con [envuelta], el águila es un bulto de trapo atado con cordel, del que asoma la punta de un ala dorada,
     * y la bandera va enrollada al asta y forrada; sin envolver, el águila dorada con las alas abiertas sobre su peana y
     * la bandera recogida. Con [luto], un crespón negro atado bajo el águila **[Diseño propio]**. Devuelve la silueta.
     */
    fun DrawScope.aguila(a: Alumbre, pie: Offset, punta: Offset, grueso: Float, envuelta: Boolean, luto: Boolean, az: Azar, lejos: Float = 0f, nieve: Float = 0f): Path {
        val d = punta - pie; val ln = d.getDistance().coerceAtLeast(0.01f); val u = d / ln; val n = Offset(-u.y, u.x)
        val lado = if (a.p.solALaDerecha) 1f else -1f
        val luzLado = if (n.x * lado > 0f) 1f else -1f
        // El asta, de madera pintada de azul, con su lado de luz.
        val asta = poligono(pie + n * grueso * 0.5f, punta + n * grueso * 0.4f, punta - n * grueso * 0.4f, pie - n * grueso * 0.5f)
        drawPath(asta, a.plano(lerp(C.AZUL, C.MADERA, 0.3f), (pie + punta) / 2f, 0f, lejos))
        drawLine(a.plano(lerp(C.AZUL, C.MADERA, 0.3f), (pie + punta) / 2f, lado, lejos), pie + n * grueso * 0.25f * luzLado, punta + n * grueso * 0.2f * luzLado, grueso * 0.3f)
        drawLine(a.plano(C.LATON, pie, 0f, lejos), pie, pie + u * grueso * 2f, grueso * 1.1f)                         // el regatón
        // La bandera enrollada al asta, bajo el águila, y forrada de trapo.
        val b0 = punta - u * (ln * 0.22f); val b1 = punta - u * (grueso * 2.6f)
        val bandera = Path().apply {
            moveTo(b0 + n * grueso * 0.9f); cubicTo(b0 + u * (ln * 0.06f) + n * grueso * 1.9f, b1 - u * (ln * 0.05f) + n * grueso * 1.7f, b1 + n * grueso * 1.3f)
            lineTo(b1 - n * grueso * 1.3f); cubicTo(b1 - u * (ln * 0.05f) - n * grueso * 1.6f, b0 + u * (ln * 0.06f) - n * grueso * 1.8f, b0 - n * grueso * 0.9f); close()
        }
        val trapo = lerp(C.CAPOTE, C.CENIZA, 0.38f)
        drawPath(bandera, a.plano(if (envuelta) trapo else C.AZUL, (b0 + b1) / 2f, 0f, lejos))
        clipPath(bandera) {
            if (envuelta) {
                // El trapo, atado en vueltas con un cordel, y por una rendija el azul y el rojo de la seda.
                for (i in 0 until 3) { val t = 0.18f + i * 0.3f; val c = b0 + (b1 - b0) * t; drawLine(a.plano(C.CUERO, c, 0f, lejos), c + n * grueso * 3f + u * grueso * 0.6f, c - n * grueso * 3f - u * grueso * 0.6f, grueso * 0.2f) }
                val r = b0 + (b1 - b0) * 0.42f
                drawLine(a.plano(C.ROJO, r, 0f, lejos), r + n * grueso * 0.6f, r + n * grueso * 0.6f + u * grueso * 2.5f, grueso * 0.7f)
                drawLine(a.plano(C.AZUL, r, 0f, lejos), r - n * grueso * 0.6f, r - n * grueso * 0.6f + u * grueso * 2.2f, grueso * 0.6f)
                for (i in 0 until 5) { val c = b0 + (b1 - b0) * az.real(); drawLine(a.plano(trapo, c, lado, lejos, 0.8f), c + n * grueso * 1.6f, c + n * grueso * 0.4f + u * grueso, grueso * 0.3f) }
            } else {
                // La seda tricolor recogida: franjas que siguen el asta, y el fleco de oro.
                for ((j, col) in listOf(C.AZUL, C.NIEVE, C.ROJO).withIndex()) { val c = b0 + (b1 - b0) * (0.2f + j * 0.3f); drawLine(a.plano(col, c, 0f, lejos), c + n * grueso * 2.4f, c - n * grueso * 2.4f + u * grueso * 2f, grueso * 1.6f) }
                drawLine(a.plano(C.ORO, b0, 0f, lejos), b0 + n * grueso * 1.5f, b0 - n * grueso * 1.5f, grueso * 0.4f)
            }
        }
        // El crespón de luto.
        if (luto) { val c = b1 - u * grueso * 0.4f; drawLine(a.plano(C.CENIZA, c, 0f, lejos), c + n * grueso * 1.2f, c - n * grueso * 1.2f, grueso * 0.6f)
            drawLine(a.plano(C.CENIZA, c, 0f, lejos), c - n * grueso * 1f, c - n * grueso * 1.4f - u * grueso * 3.4f + n * grueso * 0.5f, grueso * 0.4f); drawLine(a.plano(C.CENIZA, c, 0f, lejos), c - n * grueso * 0.6f, c - n * grueso * 0.2f - u * grueso * 3.8f, grueso * 0.35f) }
        // El águila.
        val e0 = punta - u * grueso * 2.6f
        val forma = Path().apply { addPath(asta); addPath(bandera) }
        if (envuelta) {
            val bulto = TrazosValle.blando(punta + u * grueso * 0.6f, grueso * 2.6f, grueso * 2.2f, az, 9, 0.2f, 0.85f)
            drawPath(bulto, a.plano(trapo, punta, 0f, lejos, 0.6f))
            clipPath(bulto) {
                mancha(punta + n * grueso * 1.4f * (-lado) + u * grueso, grueso * 2f, grueso * 1.6f, a.plano(trapo, punta, -lado, lejos, -0.3f), 0.8f)
                for (i in 0 until 3) { val c = punta + u * grueso * (i - 0.5f); drawLine(a.plano(C.CUERO, c, 0f, lejos), c + n * grueso * 3f, c - n * grueso * 3f + u * grueso * 0.8f, grueso * 0.22f) }
            }
            // La punta del ala dorada que asoma.
            val ala = punta + n * grueso * 2.2f + u * grueso * 1.6f
            drawPath(poligono(ala, ala + n * grueso * 1.6f + u * grueso * 1.2f, ala + u * grueso * 0.8f), a.plano(C.ORO, ala, 0.6f, lejos, 0.6f))
            // La peana de latón, a la vista bajo el trapo.
            drawLine(a.plano(C.LATON, e0, 0f, lejos), e0 + n * grueso * 1.2f, e0 - n * grueso * 1.2f, grueso * 1.3f)
            forma.addPath(bulto)
            if (nieve > 0.3f) mancha(punta + u * grueso * 2.4f, grueso * 2f, grueso * 0.8f, a.arriba(C.NIEVE, punta, lejos), 0.8f)
        } else {
            drawLine(a.plano(C.LATON, e0, 0f, lejos), e0 + n * grueso * 1.4f, e0 - n * grueso * 1.4f, grueso * 1.4f)
            val c = punta + u * grueso * 1.6f
            val alas = Path().apply { moveTo(c); cubicTo(c + n * grueso * 2f + u * grueso * 1f, c + n * grueso * 3.4f + u * grueso * 3f, c + n * grueso * 2.6f + u * grueso * 4.4f); lineTo(c + n * grueso * 0.6f + u * grueso * 1.6f)
                cubicTo(c + u * grueso * 2.8f, c + u * grueso * 2.8f, c - n * grueso * 0.6f + u * grueso * 1.6f); lineTo(c - n * grueso * 2.6f + u * grueso * 4.4f); cubicTo(c - n * grueso * 3.4f + u * grueso * 3f, c - n * grueso * 2f + u * grueso * 1f, c); close() }
            drawPath(alas, a.plano(C.ORO, c, 0f, lejos, 0.5f))
            drawLine(a.plano(lerp(C.ORO, Color.White, 0.3f), c, lado, lejos, 0.8f), c + n * grueso * 0.4f * lado, c + n * grueso * 2.4f * lado + u * grueso * 4f, grueso * 0.3f)
            forma.addPath(alas)
        }
        return forma
    }

    // =================================================================
    //  Los pinos
    // =================================================================

    /**
     * Un pino (un abeto del norte) en el lienzo, de [pie] con [alto] y [ancho] (en el lienzo; el ancho es el de las ramas
     * de abajo). Tronco, ramas en pisos que bajan por el peso (más con la nieve), masa oscura con claros entre los pisos,
     * las agujas de luz en el fleco del lado del sol y la nieve posada en cada rama, con su sombra azul debajo. [lejos]
     * lo desvanece; [detalle], de 0 (una silueta de bosque) a 1 (el pino de primer plano). [roto]: la punta rota. Nunca un
     * triángulo: cada piso tiene su largo, su caída y su hueco.
     */
    fun DrawScope.pino(
        a: Alumbre, pie: Offset, alto: Float, ancho: Float, nieve: Float, az: Azar, lejos: Float = 0f, detalle: Float = 1f, roto: Boolean = false,
        hastaY: Float = 99999f, caidaPropia: Float? = null, grande: Boolean = false,
    ) {
        val lado = if (a.p.solALaDerecha) 1f else -1f
        val top = pie.y - alto * (if (roto) 0.88f else 1f)
        val oscuro = a.plano(C.PINO_OSC, pie, 0f, lejos, 0f)
        // El tronco: se ve entre los pisos de abajo.
        val gt = (ancho * 0.045f).coerceAtLeast(0.5f)
        drawPath(poligono(pie + Offset(-gt, 0f), pie + Offset(gt, 0f), Offset(pie.x + gt * 0.25f, top), Offset(pie.x - gt * 0.25f, top)), a.plano(lerp(C.MADERA_OSC, C.CENIZA, 0.3f), pie, 0f, lejos))
        if (detalle > 0.5f) drawLine(a.plano(C.MADERA, pie, lado, lejos), pie + Offset(gt * 0.5f * lado, 0f), Offset(pie.x + gt * 0.15f * lado, top + alto * 0.3f), gt * 0.4f)
        // Los pisos, de arriba abajo.
        val pisos = (6 + (alto / 12f).toInt().coerceAtMost(16) * detalle.coerceIn(0.4f, 1f)).toInt().coerceIn(5, 18)
        val caida = caidaPropia ?: (0.35f + 0.4f * nieve)
        for (i in 0 until pisos) {
            val t = (i + 0.5f + az.entre(-0.25f, 0.25f)) / pisos
            val y = top + (pie.y - top) * (0.02f + 0.9f * t)
            if (y > hastaY) {
                // Más abajo, el tronco pelado: los muñones de las ramas muertas, con su poco de nieve (no en todos los pisos).
                for (sg in listOf(-1f, 1f)) {
                    if (az.real() < 0.6f) continue
                    val l = ancho * az.entre(0.03f, 0.08f); val q0 = Offset(pie.x + sg * gt * 0.6f, y); val q1 = q0 + Offset(sg * l, l * az.entre(-0.1f, 0.35f))
                    drawLine(a.plano(lerp(C.MADERA_OSC, C.CENIZA, 0.3f), q0, sg, lejos), q0, q1, (gt * 0.18f).coerceAtLeast(0.6f), StrokeCap.Round)
                    if (nieve > 0.2f) drawLine(a.arriba(C.NIEVE, q0, lejos), q0 + Offset(0f, -gt * 0.08f), q1 + Offset(0f, -gt * 0.08f), (gt * 0.08f).coerceAtLeast(0.4f), StrokeCap.Round)
                }
                continue
            }
            for (sg in listOf(-1f, 1f)) {
                val w = ancho * 0.5f * (0.12f + 0.88f * t) * az.entre(0.72f, 1.12f)
                if (az.real() < 0.08f * detalle && i > 1) continue                                  // un hueco: una rama que falta
                val grueso = (alto / pisos) * az.entre(0.75f, 1.05f)
                if (grande) ramaGrande(a, Offset(pie.x + sg * gt * 0.4f, y), sg, w, grueso * 0.8f, caida, nieve, az, lejos, lado)
                else rama(a, Offset(pie.x, y), sg, w, grueso, caida, nieve, az, lejos, detalle, oscuro, lado)
            }
        }
        // La punta.
        if (!roto) { val p = Offset(pie.x, top); drawPath(poligono(p + Offset(-ancho * 0.03f, alto * 0.06f), p + Offset(0f, -alto * 0.02f), p + Offset(ancho * 0.03f, alto * 0.06f)), oscuro)
            if (nieve > 0.3f) drawLine(a.arriba(C.NIEVE, p, lejos), p + Offset(0f, alto * 0.02f), p + Offset(0f, alto * 0.06f), (ancho * 0.015f).coerceAtLeast(0.4f), StrokeCap.Round) }
    }

    /**
     * Una rama de pino de primer plano, de [desde] hacia [sg] (−1 izquierda, 1 derecha): la rama misma, que sube un poco y
     * cae por el peso, y colgando de ella las ramillas en abanico, una tras otra, cada una con su masa oscura, sus agujas
     * (más claras en el borde del lado del sol) y su almohada de nieve encima, con el canto azul debajo y el filo de luz
     * arriba. Entre ramilla y ramilla se ve lo de detrás.
     */
    fun DrawScope.ramaGrande(a: Alumbre, desde: Offset, sg: Float, largo: Float, grueso: Float, caida: Float, nieve: Float, az: Azar, lejos: Float, lado: Float) {
        val punta = desde + Offset(sg * largo, largo * caida * az.entre(0.8f, 1.15f))
        val c1 = desde + Offset(sg * largo * 0.35f, -grueso * 0.35f); val c2 = desde + Offset(sg * largo * 0.72f, largo * caida * 0.3f)
        fun eje(t: Float): Offset { val u = 1f - t; return desde * (u * u * u) + c1 * (3f * u * u * t) + c2 * (3f * u * t * t) + punta * (t * t * t) }
        val enLuz = sg * lado > 0f
        val oscuro = a.plano(lerp(C.PINO_OSC, C.PINO, 0.45f), punta, 0f, lejos, -0.2f)
        val medio = a.plano(C.PINO, punta, sg * 0.3f, lejos, 0.2f)
        val claro = a.plano(C.PINO_LUZ, punta, sg, lejos, 0.5f)
        // La rama, que se ve entre las ramillas cerca del tronco.
        drawPath(Path().apply { moveTo(desde); cubicTo(c1, c2, punta) }, a.plano(lerp(C.MADERA_OSC, C.CENIZA, 0.2f), desde, sg, lejos), style = Stroke((grueso * 0.16f).coerceAtLeast(0.8f), cap = StrokeCap.Round))
        val n = (largo / (grueso * 0.55f)).toInt().coerceIn(5, 16)
        for (i in 0 until n) {
            val t0 = 0.12f + 0.88f * i / n; val t1 = (0.12f + 0.88f * (i + 1.25f) / n).coerceAtMost(1.02f)
            val p0 = eje(t0); val p1 = eje(t1.coerceAtMost(1f)); val pm = (p0 + p1) / 2f
            if (i % 4 == 3 && az.real() < 0.6f) continue                                   // un hueco entre ramillas
            val cuelga = grueso * (0.4f + 0.5f * sin(PI.toFloat() * (t0 * 0.85f + 0.15f))) * az.entre(0.7f, 1.2f)
            val vuelo = sg * grueso * 0.18f * az.entre(0f, 1f)
            // La ramilla: un abanico que cuelga del eje, de borde festoneado.
            val bajo = (0..6).map { j -> val u = j / 6f; val q = p0 + (p1 - p0) * u; q + Offset(vuelo * u, cuelga * (0.45f + 0.55f * sin(PI.toFloat() * u)) * az.entre(0.82f, 1.12f)) }
            val forma = Path().apply { moveTo(p0 + Offset(0f, -grueso * 0.06f)); cubicTo(p0 + (p1 - p0) * 0.3f + Offset(0f, -grueso * 0.14f), p0 + (p1 - p0) * 0.7f + Offset(0f, -grueso * 0.12f), p1 + Offset(0f, -grueso * 0.04f)); for (q in bajo.reversed()) lineTo(q); close() }
            drawPath(forma, oscuro)
            // Las agujas: trazos cortos que salen hacia abajo y hacia fuera, más claros en el borde y del lado del sol.
            val agujas = (6 + (p1 - p0).getDistance() / 1.6f).toInt().coerceAtMost(26)
            for (j in 0 until agujas) {
                val u = az.real(); val base = p0 + (p1 - p0) * u + Offset(0f, cuelga * az.entre(0.05f, 0.75f) * sin(PI.toFloat() * u.coerceIn(0.1f, 0.9f)))
                val ang = az.entre(-0.2f, 1.35f); val l = grueso * az.entre(0.1f, 0.3f)
                val borde = az.real() < 0.45f
                drawLine(if (borde && enLuz) claro else if (borde) medio else lerp(oscuro, medio, 0.4f), base, base + Offset(sg * l * kotlin.math.cos(ang), l * kotlin.math.sin(ang)), (grueso * 0.035f).coerceIn(0.35f, 1.2f), StrokeCap.Round)
            }
            // Las puntas de luz de la ramilla, del lado del sol.
            if (enLuz) for (q in bajo.filterIndexed { j, _ -> j % 2 == 1 }) drawLine(claro, q + Offset(-sg * 1f, -grueso * 0.08f), q + Offset(sg * grueso * 0.05f, grueso * 0.06f), (grueso * 0.04f).coerceIn(0.35f, 1.3f), StrokeCap.Round)
        }
        // La nieve posada a lo largo de la rama: una capa seguida, gruesa en medio y fina en la punta, con bultos donde
        // se juntan las ramillas, el canto de abajo azul y el filo de arriba encendido. No cubre toda la rama: deja
        // huecos donde el viento la sacudió.
        if (nieve > 0.05f) {
            val tramos = listOf(0.08f to az.entre(0.38f, 0.55f), az.entre(0.6f, 0.68f) to az.entre(0.86f, 0.97f))
            for ((ta, tb) in tramos) {
                val pasos = 12
                val arriba = ArrayList<Offset>(); val abajo = ArrayList<Offset>()
                for (j in 0..pasos) {
                    val t = ta + (tb - ta) * j / pasos; val q = eje(t)
                    val forma = sin(PI.toFloat() * j / pasos).coerceAtLeast(0.12f)
                    val g = grueso * (0.1f + 0.24f * nieve) * forma * (0.75f + 0.5f * sin(t * 23f + sg * 3f).let { it * it })
                    arriba += q + Offset(0f, -g - grueso * 0.04f)
                    abajo += q + Offset(sg * 0.5f, g * 0.28f + grueso * 0.05f * sin(t * 37f))
                }
                val capa = TrazosValle.cerrada(arriba + abajo.reversed())
                val pm = eje((ta + tb) / 2f)
                drawPath(capa, a.cara(C.NIEVE, Vec.unidad(V3(sg * 0.15f, 0.8f, -0.5f)), pm, lejos))
                drawPath(curva(abajo), a.cara(C.NIEVE_SOMBRA, V3(0f, -0.4f, -0.9f), pm, lejos), style = Stroke((grueso * 0.07f).coerceIn(0.5f, 2.4f), cap = StrokeCap.Round))
                drawPath(curva(arriba.subList(2, arriba.size - 2)), a.arriba(lerp(C.NIEVE, Color.White, 0.45f), pm, lejos), style = Stroke((grueso * 0.045f).coerceIn(0.35f, 1.6f), cap = StrokeCap.Round))
            }
        }
    }

    /** A qué parte de su alto empieza la copa de un pino albar. */
    const val COPA_ALBAR = 0.58f

    /**
     * Un pino albar (el pino de los «bosques de pinos» del cuento), de [pie] con [alto] y [ancho] de copa (en el lienzo):
     * el tronco recto y desnudo, rojizo arriba y gris abajo, con su lado de luz cálido y los muñones de las ramas
     * muertas; la copa alta, en tres o cuatro masas de agujas tendidas, oscuras y con claros, con su nieve encima. Sus
     * troncos son los que dejan pasar el sol en franjas.
     */
    fun DrawScope.pinoAlbar(a: Alumbre, pie: Offset, alto: Float, ancho: Float, nieve: Float, az: Azar, lejos: Float = 0f, detalle: Float = 1f) {
        val lado = if (a.p.solALaDerecha) 1f else -1f
        val gt = (ancho * 0.07f).coerceAtLeast(0.45f)
        val top = pie.y - alto; val copa = pie.y - alto * COPA_ALBAR
        val inclina = az.entre(-0.04f, 0.04f) * alto
        val cima = Offset(pie.x + inclina, top)
        // El tronco, con su lado de luz.
        val tronco = poligono(pie + Offset(-gt, 0f), pie + Offset(gt, 0f), cima + Offset(gt * 0.35f, alto * 0.12f), cima + Offset(-gt * 0.35f, alto * 0.12f))
        drawPath(tronco, Brush.verticalGradient(listOf(a.plano(lerp(C.MADERA, C.ROJO, 0.25f), cima, 0f, lejos), a.plano(lerp(C.MADERA_GRIS, C.CENIZA, 0.2f), pie, 0f, lejos)), top, pie.y))
        if (detalle > 0.3f) {
            drawLine(a.plano(lerp(C.MADERA, C.LATON, 0.3f), cima, lado, lejos), pie + Offset(gt * 0.5f * lado, 0f), cima + Offset(gt * 0.18f * lado, alto * 0.14f), (gt * 0.55f).coerceAtLeast(0.35f))
            // Los muñones de las ramas muertas, a lo largo del tronco.
            repeat((4 * detalle).toInt() + 1) {
                val y = pie.y - alto * az.entre(0.2f, COPA_ALBAR); val sg = if (az.real() < 0.5f) -1f else 1f; val l = ancho * az.entre(0.05f, 0.14f)
                val q = Offset(pie.x + inclina * (pie.y - y) / alto, y)
                drawLine(a.plano(C.MADERA_GRIS, q, sg, lejos), q, q + Offset(sg * l, -l * 0.3f), (gt * 0.35f).coerceAtLeast(0.3f), StrokeCap.Round)
            }
            if (nieve > 0.2f) drawLine(a.plano(C.NIEVE, pie, -lado, lejos), pie + Offset(-gt * 0.6f * lado, 0f), Offset(pie.x - gt * 0.5f * lado + inclina * 0.4f, pie.y - alto * 0.4f), (gt * 0.4f).coerceAtLeast(0.3f))
        }
        // La copa: una masa oscura de fondo y, encima, las masas tendidas en pisos, de base plana, que se enciman,
        // unidas por ramas que salen del tronco.
        val masas = (4 + (detalle * 3f).toInt()).coerceIn(4, 7)
        val oscuro = a.plano(C.PINO_OSC, cima, 0f, lejos, 0f)
        val fondo = Offset(pie.x + inclina * (1f - (COPA_ALBAR + 1f) / 2f) + az.entre(-0.1f, 0.1f) * ancho, (copa + top) / 2f)
        drawPath(TrazosValle.borron(fondo, ancho * 0.42f, (copa - top) * 0.36f, az, 13, 0.22f, 0.8f), lerp(oscuro, a.plano(C.CENIZA, fondo, 0f, lejos), 0.25f))
        for (i in 0 until masas) {
            val t = i / (masas - 1f)
            val y = copa - (copa - top) * (0.05f + 0.82f * t) + az.entre(-1f, 1f) * alto * 0.025f
            val sg = if (i % 2 == 0) -1f else 1f
            val cx = pie.x + inclina * (pie.y - y) / alto + sg * ancho * az.entre(0.05f, 0.3f) * (1f - 0.45f * t)
            val rx = ancho * az.entre(0.32f, 0.52f) * (1f - 0.4f * t); val ry = alto * az.entre(0.05f, 0.085f)
            val tx = pie.x + inclina * (pie.y - y) / alto
            drawLine(a.plano(C.MADERA_OSC, Offset(tx, y), sg, lejos), Offset(tx, y + ry * 1.2f), Offset(cx, y + ry * 0.2f), (gt * 0.4f).coerceAtLeast(0.3f), StrokeCap.Round)
            val masa = TrazosValle.borron(Offset(cx, y), rx, ry, az, 11, 0.25f, 0.45f)
            drawPath(masa, oscuro)
            if (detalle > 0.35f) clipPath(masa) {
                // El lado del sol, más claro, y las agujas del fleco.
                mancha(Offset(cx + lado * rx * 0.35f, y - ry * 0.3f), rx * 0.6f, ry * 0.8f, a.plano(C.PINO, Offset(cx, y), lado, lejos, 0.5f), 0.7f)
                repeat((12 * detalle).toInt()) { val q = Offset(cx + az.entre(-1f, 1f) * rx, y + az.entre(-0.4f, 0.9f) * ry); drawLine(a.plano(if (az.real() < 0.5f) C.PINO_LUZ else C.PINO, q, lado, lejos, 0.3f), q, q + Offset(az.entre(-1f, 1f) * ry * 0.4f, ry * 0.5f), (ry * 0.12f).coerceIn(0.25f, 1f), StrokeCap.Round) }
            }
            if (nieve > 0.1f) {
                val g = ry * (0.25f + 0.45f * nieve)
                val capa = TrazosValle.blando(Offset(cx, y - ry * 0.55f), rx * 0.8f, g, az, 9, 0.2f, 0.5f)
                drawPath(capa, a.cara(C.NIEVE, Vec.unidad(V3(0f, 0.8f, -0.5f)), Offset(cx, y), lejos))
                if (detalle > 0.35f) drawLine(a.cara(C.NIEVE_SOMBRA, V3(0f, -0.4f, -0.9f), Offset(cx, y), lejos), Offset(cx - rx * 0.7f, y - ry * 0.45f + g * 0.4f), Offset(cx + rx * 0.7f, y - ry * 0.4f + g * 0.4f), (g * 0.3f).coerceIn(0.3f, 1.6f), StrokeCap.Round)
            }
        }
    }

    /** Una rama de pino en pisos: el lomo que sale del tronco en [desde] hacia [sg] (−1 izquierda, 1 derecha), su fleco de agujas y su nieve. */
    fun DrawScope.rama(a: Alumbre, desde: Offset, sg: Float, largo: Float, grueso: Float, caida: Float, nieve: Float, az: Azar, lejos: Float, detalle: Float, oscuro: Color, lado: Float) {
        val punta = desde + Offset(sg * largo, largo * caida * az.entre(0.7f, 1.2f))
        val medio = desde + Offset(sg * largo * 0.5f, -grueso * 0.25f + largo * caida * 0.15f)
        // El lomo de arriba (una curva que sube un poco y luego cae) y el fleco de abajo, colgante y dentado.
        val arriba = listOf(desde, medio, punta)
        val dientes = (4 + (largo / 6f).toInt()).coerceIn(4, 14)
        val abajo = (0..dientes).map { j ->
            val tt = 1f - j / dientes.toFloat()
            val base = desde + (punta - desde) * tt + Offset(0f, grueso * (0.55f + 0.5f * sin(PI.toFloat() * tt)))
            base + Offset(az.entre(-0.8f, 0.8f), if (j % 2 == 0) grueso * az.entre(0.15f, 0.45f) else -grueso * az.entre(0f, 0.15f))
        }
        val forma = Path().apply {
            moveTo(desde); cubicTo(desde + (medio - desde) * 0.6f, medio, medio + (punta - medio) * 0.5f); lineTo(punta)
            for (q in abajo) lineTo(q); close()
        }
        val enLuz = sg * lado > 0f
        drawPath(forma, oscuro)
        if (detalle > 0.25f) {
            // El fleco: agujas cortas que cuelgan del borde, más claras del lado del sol y en las puntas.
            val col = a.plano(if (enLuz) C.PINO_LUZ else C.PINO, punta, sg, lejos, 0.1f)
            val n = (largo / 2.2f * detalle).toInt().coerceIn(3, 40)
            for (j in 0 until n) {
                val tt = 0.25f + 0.75f * j / n; val q = desde + (punta - desde) * tt + Offset(0f, grueso * az.entre(0.1f, 0.7f))
                val l = grueso * az.entre(0.3f, 0.6f)
                drawLine(col, q, q + Offset(sg * l * 0.4f, l), (grueso * 0.09f).coerceIn(0.3f, 1.4f), StrokeCap.Round)
            }
            // El lomo de luz.
            drawPath(curva(listOf(desde + (medio - desde) * 0.5f, medio, medio + (punta - medio) * 0.7f)), a.plano(C.PINO, medio, sg, lejos, 0.7f), style = Stroke((grueso * 0.14f).coerceIn(0.3f, 1.6f), cap = StrokeCap.Round))
        }
        // La nieve posada encima, siguiendo el lomo; más gruesa en medio, y su canto de abajo azul.
        if (nieve > 0.05f && az.real() > 0.3f) {
            val g = grueso * (0.06f + 0.24f * nieve) * az.entre(0.5f, 1.2f)
            val p0 = desde + (medio - desde) * az.entre(0.2f, 0.7f); val p1 = medio + (punta - medio) * az.entre(-0.2f, 0.25f); val p2 = medio + (punta - medio) * az.entre(0.4f, 0.95f)
            val sube = Offset(0f, -g)
            val capa = Path().apply {
                moveTo(p0); cubicTo(p0 + sube * 0.8f, p1 + sube * 1.1f + Offset(-sg * largo * 0.1f, 0f), p1 + sube); cubicTo(p1 + sube * 0.9f + Offset(sg * largo * 0.15f, 0f), p2 + sube * 0.4f, p2)
                cubicTo(p2 + Offset(0f, g * 0.5f), p1 + Offset(sg * largo * 0.1f, g * 0.55f), p1 + Offset(0f, g * 0.35f)); cubicTo(p1 + Offset(-sg * largo * 0.12f, g * 0.4f), p0 + Offset(0f, g * 0.3f), p0); close()
            }
            drawPath(capa, a.cara(C.NIEVE, Vec.unidad(V3(sg * 0.2f, 0.75f, -0.5f)), p1, lejos))
            if (detalle > 0.3f) {
                drawPath(Path().apply { moveTo(p2); cubicTo(p2 + Offset(0f, g * 0.5f), p1 + Offset(sg * largo * 0.1f, g * 0.55f), p1 + Offset(0f, g * 0.35f)); cubicTo(p1 + Offset(-sg * largo * 0.12f, g * 0.4f), p0 + Offset(0f, g * 0.3f), p0) },
                    a.cara(C.NIEVE_SOMBRA, V3(0f, -0.5f, -0.8f), p1, lejos), style = Stroke((g * 0.35f).coerceIn(0.3f, 2.4f), cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(p0); cubicTo(p0 + sube * 0.8f, p1 + sube * 1.1f + Offset(-sg * largo * 0.1f, 0f), p1 + sube) },
                    a.arriba(lerp(C.NIEVE, Color.White, 0.4f), p1, lejos), style = Stroke((g * 0.22f).coerceIn(0.25f, 1.6f), cap = StrokeCap.Round))
            }
        }
    }

    // =================================================================
    //  La carroza rota, volcada
    // =================================================================

    /**
     * La carroza rota, volcada al borde del camino: una berlina de caja lacada de azul con filetes de oro, tumbada sobre
     * su costado izquierdo y apoyada en sus ruedas aplastadas, ladeada unos 60°. El costado derecho queda arriba, mirando
     * al cielo y a quien mira: el panel lacado con su filete y su blasón, el hueco de la portezuela con el terciopelo rojo
     * de dentro y la portezuela abierta hacia arriba, con su forro capitoné a la vista; la rueda trasera de ese lado sigue
     * en su eje y cruza el cielo; la delantera se rompió (es [ruedaSuelta]). Medidas **[Aproximación]**: la caja 2.4 m de
     * largo, 1.4 de ancho, 1.5 de alto. [s] es su sitio; el giro dice hacia dónde mira su frente.
     */
    class Carroza(val s: Sitio, val nieve: Float) {
        val w = 0.7f; val y0 = 0.5f; val y1 = 2.0f; val l = 1.2f
        /** Cuánto se ladea sobre su largo (radianes): unos 46°, apoyada en las ruedas rotas del costado de allá. */
        val tumbo = 0.8f
        /** El perfil del costado de la caja (z, y), con la panza de abajo curva y el techo un poco abombado. */
        val perfil: List<Pair<Float, Float>> = listOf(-l to 1.97f, -l * 1.03f to 1.25f, -l * 0.94f to 0.8f, -l * 0.62f to 0.57f, 0f to 0.5f, l * 0.62f to 0.57f, l * 0.94f to 0.8f, l * 1.03f to 1.25f, l to 1.97f)
        private val alza: Float = (w + 0.17f) * sin(tumbo) - 0.08f
        /** De la carroza derecha a la volcada. */
        fun t(p: V3): V3 = Vec.girarZ(p, tumbo) + V3(0.9f, alza, 0f)
        fun tn(n: V3): V3 = Vec.girarZ(n, tumbo)
        /** La rueda que cruza el cielo: la trasera derecha, en su eje (un poco torcido). */
        val ruedaAlta: V3 get() = t(V3(w + 0.17f, 0.62f, 0.95f))
        val ejeAlto: V3 get() = Vec.unidad(tn(V3(1f, 0.12f, -0.18f)))
        val radioAlto = 0.64f
        /** Cuánto está abierta la portezuela (radianes). */
        val ABRE = 0.75f
        /** La portezuela abierta: su punto ([u], [v]) de 0 a 1, girada sobre la bisagra de delante unos 100°. */
        fun puerta(u: Float, v: Float): V3 {
            val ang = ABRE; val dz = 0.72f * u
            return t(V3(w + dz * sin(ang), 0.74f + 1.06f * v, -0.36f + dz * cos(ang)))
        }
        fun esquinas(): List<V3> = listOf(V3(-w, y0, -l), V3(w, y0, -l), V3(-w, y1, -l), V3(w, y1, -l), V3(-w, y0, l), V3(w, y0, l), V3(-w, y1, l), V3(w, y1, l)).map { t(it) } +
            listOf(puerta(1f, 1f), puerta(1f, 0f))
    }

    /** La silueta de la carroza en el lienzo. */
    fun siluetaDeCarroza(c: Carroza): Path {
        val pts = c.esquinas().map { c.s.p(it) } + circulo(c.s, c.ruedaAlta, c.ejeAlto, c.radioAlto, 16)
        return poligono(envolvente(pts))
    }

    fun DrawScope.carroza(c: Carroza, a: Alumbre, lejos: Float = 0f) {
        val s = c.s; val k = s.k; val az = Azar(Semilla.de("napoleon", "carroza"))
        val laca = C.AZUL
        // Las ruedas del costado caído, aplastadas debajo (lo que asoma).
        rueda(s, a, c.t(V3(-c.w - 0.15f, 0.62f, c.l * 0.8f)), 0.62f, c.tn(V3(1f, 0f, 0f)), 0.08f, 12, 3, C.MADERA_GRIS, lejos, 0.4f, c.nieve)
        rueda(s, a, c.t(V3(-c.w - 0.15f, 0.45f, -c.l * 0.8f)), 0.45f, c.tn(V3(1f, 0f, 0f)), 0.07f, 10, 2, C.MADERA_GRIS, lejos, 1.1f, c.nieve)
        // La caja: las tiras de la panza (el fondo, de madera oscura) y los testeros lacados, entre los dos costados (solo las que se ven), y el techo.
        val perfil = c.perfil
        fun costado(x: Float) = perfil.map { (z, y) -> c.t(V3(x, y, z)) }
        val arriba = costado(c.w); val abajo = costado(-c.w)
        for (i in 0 until perfil.size - 1) {
            val q = listOf(abajo[i], abajo[i + 1], arriba[i + 1], arriba[i])
            val (z0, y0) = perfil[i]; val (z1, y1) = perfil[i + 1]
            val nLocal = Vec.unidad(V3(0f, -(z1 - z0), (y1 - y0)))
            val col = if (i == 0 || i == perfil.size - 2) laca else lerp(C.MADERA_OSC, C.CENIZA, 0.25f)
            val f = cara(s, a, q, c.tn(nLocal), col, lejos) ?: continue
            if (i in 2..5) clipPath(f) {
                // Las tablas del fondo y el barro seco de los caminos.
                for (u in listOf(0.25f, 0.5f, 0.75f)) drawLine(mult(a.cara(col, s.normal(c.tn(nLocal)), s.p(q[0]), lejos), C.SOMBRA, 0.45f), s.p(q[0] + (q[3] - q[0]) * u), s.p(q[1] + (q[2] - q[1]) * u), (0.012f * k).coerceAtLeast(0.25f))
                repeat(3) { val qq = s.p(q[0] + (q[1] - q[0]) * az.real() + (q[3] - q[0]) * az.real()); mancha(qq, 0.18f * k, 0.06f * k, a.cara(C.BARRO, s.normal(c.tn(nLocal)), qq, lejos), 0.5f) }
            }
            if (i == perfil.size - 2) clipPath(f) {
                // El testero de detrás: la ventanilla de atrás y el filete.
                val vq = listOf(V3(-c.w * 0.55f, 1.45f, c.l + 0.01f), V3(c.w * 0.55f, 1.45f, c.l + 0.01f), V3(c.w * 0.55f, 1.85f, c.l + 0.01f), V3(-c.w * 0.55f, 1.85f, c.l + 0.01f)).map { s.p(c.t(it)) }
                drawPath(poligono(vq), a.cara(lerp(C.CENIZA, C.AZUL, 0.4f), s.normal(c.tn(nLocal)), centroDe(vq), lejos))
                drawPath(poligono(vq), a.cara(C.ORO, s.normal(c.tn(nLocal)), centroDe(vq), lejos), style = Stroke((0.012f * k).coerceIn(0.3f, 1.2f)))
            }
        }
        cara(s, a, listOf(c.t(V3(-c.w, c.y1 - 0.03f, -c.l)), c.t(V3(c.w, c.y1 - 0.03f, -c.l)), c.t(V3(c.w, c.y1 - 0.03f, c.l)), c.t(V3(-c.w, c.y1 - 0.03f, c.l))), c.tn(V3(0f, 1f, 0f)), lerp(C.CUERO, laca, 0.3f), lejos)
        // El tren de abajo, sobre el fondo: la viga, los dos ejes de hierro y la ballesta en C de detrás, de cuero y hierro.
        val v0 = s.p(c.t(V3(0f, 0.42f, -c.l * 1.05f))); val v1 = s.p(c.t(V3(0f, 0.42f, c.l * 1.05f)))
        drawLine(a.plano(C.MADERA_OSC, v1, 0.3f, lejos), v0, v1, (0.09f * k).coerceIn(0.8f, 7f), StrokeCap.Round)
        for ((z, y) in listOf(-c.l * 0.8f to 0.42f, c.l * 0.8f to 0.45f)) {
            val p0 = s.p(c.t(V3(-c.w - 0.2f, y, z))); val p1 = s.p(c.t(V3(c.w + 0.2f, y, z)))
            drawLine(a.plano(C.HIERRO, p1, 0.4f, lejos), p0, p1, (0.06f * k).coerceIn(0.6f, 5f), StrokeCap.Round)
            drawLine(lerp(a.plano(C.HIERRO, p1, 0.9f, lejos), Color.White, 0.15f), p0 + Offset(0f, -0.02f * k), p1 + Offset(0f, -0.02f * k), (0.015f * k).coerceIn(0.25f, 1.2f), StrokeCap.Round)
        }
        val b0 = s.p(c.t(V3(c.w * 0.5f, 0.45f, c.l * 0.8f))); val b1 = s.p(c.t(V3(c.w * 0.5f, 0.3f, c.l * 1.2f))); val b2 = s.p(c.t(V3(c.w * 0.5f, 0.62f, c.l * 1.05f)))
        drawPath(curva(listOf(b0, b1, b2)), a.plano(C.HIERRO, b1, 0.5f, lejos), style = Stroke((0.045f * k).coerceIn(0.4f, 3.6f), cap = StrokeCap.Round))
        drawPath(curva(listOf(b0, b1, b2)), a.plano(C.CUERO, b1, 0.5f, lejos), style = Stroke((0.02f * k).coerceIn(0.3f, 1.6f), cap = StrokeCap.Round))
        // El costado de arriba: el panel lacado, con su brillo, su filete de oro, el blasón y los desconchones.
        val fs = arriba.map { s.p(it) }; val panel = poligono(fs)
        val nSup = c.tn(V3(1f, 0f, 0f))
        drawPath(panel, a.cara(laca, s.normal(nSup), centroDe(fs), lejos))
        clipPath(panel) {
            // El reflejo del cielo en la laca, una banda clara a lo largo.
            val r0 = s.p(c.t(V3(c.w, 1.7f, -c.l))); val r1 = s.p(c.t(V3(c.w, 1.7f, c.l)))
            drawLine(lerp(a.cara(laca, s.normal(nSup), r0, lejos), a.l.medio, 0.35f), r0, r1, (0.14f * k).coerceIn(0.8f, 9f), StrokeCap.Round)
            repeat(14) { val q = s.p(c.t(V3(c.w, az.entre(0.6f, 1.9f), az.entre(-c.l, c.l)))); mancha(q, az.entre(0.04f, 0.1f) * k, 0.03f * k, a.cara(lerp(C.BARRO, laca, 0.35f), s.normal(nSup), q, lejos), 0.55f) }
        }
        val filete = perfil.map { (z, y) -> s.p(c.t(V3(c.w + 0.002f, 0.5f + (y - 0.5f) * 0.88f + 0.06f, z * 0.9f))) }
        drawPath(poligono(filete), a.cara(C.ORO, s.normal(nSup), centroDe(fs), lejos), style = Stroke((0.022f * k).coerceIn(0.3f, 2f)))
        val esc = c.t(V3(c.w + 0.004f, 1.2f, c.l * 0.68f)); val escP = circulo(s, esc, nSup, 0.13f, 14)
        drawPath(poligono(escP), a.cara(C.ROJO, s.normal(nSup), s.p(esc), lejos))
        drawPath(poligono(escP), a.cara(C.ORO, s.normal(nSup), s.p(esc), lejos), style = Stroke((0.02f * k).coerceAtLeast(0.3f)))
        drawPath(poligono(circulo(s, esc, nSup, 0.05f, 8)), a.cara(C.ORO, s.normal(nSup), s.p(esc), lejos))
        // El hueco de la portezuela: dentro, el asiento de terciopelo rojo y la penumbra.
        val hueco = listOf(V3(c.w, 0.74f, -0.36f), V3(c.w, 0.74f, 0.36f), V3(c.w, 1.8f, 0.36f), V3(c.w, 1.8f, -0.36f)).map { s.p(c.t(it)) }
        drawPath(poligono(hueco), a.cara(lerp(C.TERCIOPELO, C.CENIZA, 0.55f), s.normal(nSup), centroDe(hueco), lejos))
        clipPath(poligono(hueco)) {
            val asiento = listOf(V3(c.w * 0.2f, 0.74f, -0.3f), V3(c.w * 0.2f, 0.74f, 0.3f), V3(c.w * 0.2f, 1.25f, 0.3f), V3(c.w * 0.2f, 1.25f, -0.3f)).map { s.p(c.t(it)) }
            drawPath(poligono(asiento), a.cara(C.TERCIOPELO, s.normal(nSup), centroDe(asiento), lejos))
            drawLine(a.cara(lerp(C.TERCIOPELO, C.ROJO, 0.6f), s.normal(nSup), asiento[2], lejos), asiento[3], asiento[2], (0.03f * k).coerceIn(0.3f, 2.4f))
        }
        drawPath(poligono(hueco), a.cara(C.ORO, s.normal(nSup), centroDe(hueco), lejos), style = Stroke((0.016f * k).coerceIn(0.3f, 1.6f)))
        // La portezuela abierta hacia arriba: el canto lacado, el forro capitoné y la ventanilla rota que devuelve el cielo.
        val pq = listOf(c.puerta(0f, 0f), c.puerta(1f, 0f), c.puerta(1f, 1f), c.puerta(0f, 1f))
        val pp = pq.map { s.p(it) }; val nDentro = c.tn(Vec.unidad(V3(-cos(c.ABRE), 0f, sin(c.ABRE))))
        val dentroSeVe = s.seVe(centroDe3(pq), nDentro)
        val nPuerta = if (dentroSeVe) nDentro else nDentro * -1f
        drawPath(poligono(pp), a.cara(if (dentroSeVe) C.TERCIOPELO else laca, s.normal(nPuerta), centroDe(pp), lejos))
        fun ep(u: Float, v: Float): Offset { val q0 = pp[0] + (pp[1] - pp[0]) * u; val q1 = pp[3] + (pp[2] - pp[3]) * u; return q0 + (q1 - q0) * v }
        if (dentroSeVe) {
            val boton = mult(a.cara(C.TERCIOPELO, s.normal(nPuerta), centroDe(pp), lejos), C.SOMBRA, 0.55f)
            for (i in 1..4) for (j in 1..3) { val q = ep(j / 4f + (if (i % 2 == 0) 0.12f else 0f), 0.06f + i * 0.12f)
                drawCircle(boton, (0.02f * k).coerceAtLeast(0.3f), q); drawCircle(lerp(a.cara(C.ROJO, s.normal(nPuerta), q, lejos), Color.White, 0.15f), (0.009f * k).coerceAtLeast(0.2f), q + Offset(-0.008f * k, -0.008f * k)) }
        } else {
            drawPath(poligono(listOf(ep(0.1f, 0.05f), ep(0.9f, 0.05f), ep(0.9f, 0.55f), ep(0.1f, 0.55f))), a.cara(C.ORO, s.normal(nPuerta), centroDe(pp), lejos), style = Stroke((0.012f * k).coerceIn(0.3f, 1.2f)))
        }
        val vt = listOf(ep(0.16f, 0.64f), ep(0.84f, 0.64f), ep(0.84f, 0.93f), ep(0.16f, 0.93f))
        drawPath(poligono(vt), a.cara(lerp(C.CENIZA, C.AZUL, 0.35f), s.normal(nPuerta), centroDe(vt), lejos))
        drawPath(poligono(vt[0], vt[1], vt[0] + (vt[3] - vt[0]) * 0.7f), lerp(a.l.medio, a.l.alto, 0.5f).copy(alpha = 0.85f))
        drawLine(lerp(a.l.bajo, Color.White, 0.35f).copy(alpha = 0.7f), vt[1], vt[0] + (vt[3] - vt[0]) * 0.7f, (0.008f * k).coerceAtLeast(0.25f))
        drawPath(poligono(pp), a.cara(laca, s.normal(nPuerta), centroDe(pp), lejos), style = Stroke((0.045f * k).coerceIn(0.5f, 4f)))
        drawPath(poligono(pp), a.cara(C.ORO, s.normal(nPuerta), centroDe(pp), lejos), style = Stroke((0.012f * k).coerceIn(0.25f, 1.2f)))
        // La rueda de arriba, en su eje, contra el cielo; y el muñón del eje de la de delante, que se rompió.
        val ra = c.ruedaAlta
        val e0 = s.p(c.t(V3(c.w, 0.62f, c.l * 0.8f))); val e1 = s.p(ra)
        drawLine(a.plano(C.HIERRO, e0, 0f, lejos), e0, e1, (0.06f * k).coerceIn(0.6f, 5f), StrokeCap.Round)
        val m0 = s.p(c.t(V3(c.w, 0.45f, -c.l * 0.8f))); val m1 = s.p(c.t(V3(c.w + 0.22f, 0.45f, -c.l * 0.8f)))
        drawLine(a.plano(C.HIERRO, m1, 0.5f, lejos), m0, m1, (0.06f * k).coerceIn(0.6f, 5f), StrokeCap.Round)
        drawLine(a.plano(C.MADERA_GRIS, m1, 0.5f, lejos), m1, m1 + Offset(0.12f * k, -0.07f * k), (0.035f * k).coerceIn(0.4f, 3f), StrokeCap.Round)
        rueda(s, a, ra, c.radioAlto, c.ejeAlto, 0.09f, 12, 1, C.MADERA, lejos, 0.9f, c.nieve)
        // La nieve: posada en lo alto del panel y del techo, en el canto de la portezuela, y arrimada al pie.
        if (c.nieve > 0.1f) {
            val cresta = (0..8).map { i -> s.p(c.t(V3(c.w, c.y1 - 0.02f, -c.l + 2f * c.l * i / 8f))) }.sortedBy { it.x }
            nieveEncima(a, cresta, (0.07f * k * c.nieve).coerceAtMost(7f), az, lejos)
            nieveEncima(a, listOf(s.p(c.puerta(0f, 1f)), s.p(c.puerta(0.5f, 1f)), s.p(c.puerta(1f, 1f))).sortedBy { it.x }, (0.03f * k * c.nieve).coerceAtMost(3f), az, lejos)
            val base = (0..12).map { i -> val z = -c.l * 1.1f + 2.2f * c.l * i / 12f; s.p(V3(c.t(V3(-c.w, c.y0, z)).x - 0.3f + 0.6f * az.real(), 0.06f + 0.22f * c.nieve * (0.6f + 0.4f * sin(i * 1.3f)), z)) }
            drawPath(TrazosValle.cerrada(base + base.reversed().map { it + Offset(0f, 0.22f * k) }), a.arriba(C.NIEVE, base[6], lejos))
        }
    }

    private fun centroDe3(q: List<V3>): V3 = V3(q.sumOf { it.x.toDouble() }.toFloat() / q.size, q.sumOf { it.y.toDouble() }.toFloat() / q.size, q.sumOf { it.z.toDouble() }.toFloat() / q.size)

    /** La rueda que se le cayó a la carroza, echada en la nieve y cortada por el borde: la pina, los rayos rotos, el cubo. */
    fun DrawScope.ruedaSuelta(s: Sitio, a: Alumbre, nieve: Float, lejos: Float = 0f): Path {
        val sil = rueda(s, a, V3(0f, 0.08f, 0f), 0.62f, Vec.unidad(V3(0.12f, 1f, -0.2f)), 0.09f, 12, 3, C.MADERA, lejos, 0.2f, nieve)
        if (nieve > 0.2f) { val q = s.p(V3(-0.2f, 0.1f, -0.3f)); mancha(q, 0.4f * s.k, 0.06f * s.k, a.arriba(C.NIEVE, q, lejos), 0.85f * nieve) }
        return sil
    }

    // =================================================================
    //  El cañón abandonado con su armón
    // =================================================================

    /**
     * Un cañón de campaña abandonado: el tubo de bronce con sus molduras y sus asas, la cureña de dos gualderas con sus
     * herrajes, las dos ruedas (una hundida en la nieve, así que todo se ladea [ladeo]) y la contera en el suelo.
     * Medidas de un cañón de 6 **[Aproximación]**: 1.8 m de tubo, ruedas de 1.4 m. [s]: su sitio (con giro 0 la boca mira
     * a quien mira). Devuelve la silueta.
     */
    fun DrawScope.canon(s: Sitio, a: Alumbre, ladeo: Float, nieve: Float, lejos: Float = 0f): Path {
        fun t(p: V3): V3 = Vec.girarZ(p, ladeo)
        val r = 0.68f
        val rd = t(V3(0.72f, r, 0.3f)); val ri = t(V3(-0.72f, r - 0.2f, 0.3f))
        val pts = ArrayList<Offset>()
        // La rueda de detrás primero.
        val ruedas = listOf(rd, ri).sortedByDescending { s.hondo(it) }
        val atras = ruedas[0]; val delante = ruedas[1]
        pts += circulo(s, atras, t(V3(1f, 0f, 0f)), r, 12); pts += circulo(s, delante, t(V3(1f, 0f, 0f)), r, 12)
        rueda(s, a, atras, r, t(V3(1f, 0f, 0f)), 0.09f, 14, 0, C.MADERA_GRIS, lejos, 0.3f, nieve)
        // La cureña: dos gualderas que bajan hasta la contera, en el suelo, detrás.
        for (x in listOf(0.24f, -0.24f)) {
            val q = listOf(t(V3(x, 0.55f, -0.35f)), t(V3(x, 0.95f, -0.25f)), t(V3(x * 0.6f, 0.32f, 2.2f)), t(V3(x * 0.6f, 0.05f, 2.3f)))
            cara(s, a, q.map { it }, t(V3(if (x > 0f) 1f else -1f, 0f, 0f)), lerp(C.MADERA_GRIS, C.AZUL, 0.25f), lejos)
            val top = listOf(t(V3(x - 0.05f, 0.95f, -0.25f)), t(V3(x + 0.05f, 0.95f, -0.25f)), t(V3(x * 0.6f + 0.05f, 0.32f, 2.2f)), t(V3(x * 0.6f - 0.05f, 0.32f, 2.2f)))
            cara(s, a, top, t(Vec.unidad(V3(0f, 1f, 0.3f))), lerp(C.MADERA_GRIS, C.AZUL, 0.25f), lejos)
            pts += q.map { s.p(it) }
            // Los herrajes.
            for (z in listOf(0.2f, 0.9f, 1.6f)) { val h0 = s.p(t(V3(x * 1.02f, 0.9f - z * 0.28f, z))); val h1 = s.p(t(V3(x * 1.02f, 0.52f - z * 0.2f, z))); drawLine(a.plano(C.HIERRO, h0, 0f, lejos), h0, h1, (0.03f * s.k).coerceIn(0.3f, 2f)) }
        }
        // El tubo de bronce, sobre los muñones, un poco alzado.
        val culata = t(V3(0f, 0.98f, 0.55f)); val boca = t(V3(0f, 1.08f, -1.25f))
        cilindro(s, a, culata, boca - culata, 0.15f, 0.1f, Vec.largo(boca - culata), C.BRONCE, C.BRONCE, 16, lejos, C.BRONCE)
        // Las molduras y el brocal de la boca.
        val ee = Vec.unidad(boca - culata)
        for (f in listOf(0.08f, 0.42f, 0.95f)) { val c = culata + ee * (Vec.largo(boca - culata) * f); val rr = 0.15f - 0.05f * f + 0.012f; drawPath(TrazosValle.cerrada(circulo(s, c, ee, rr, 14)), a.plano(lerp(C.BRONCE, C.LATON, 0.5f), s.p(c), 0.5f, lejos), style = Stroke((0.025f * s.k).coerceIn(0.3f, 2f))) }
        val bp = s.p(boca); drawPath(poligono(circulo(s, boca + ee * 0.01f, ee, 0.06f, 12)), a.plano(C.CENIZA, bp, 0f, lejos))
        // Las asas (los delfines).
        val asa = culata + ee * 0.7f + t(V3(0f, 0.15f, 0f))
        drawPath(curva(listOf(s.p(asa + ee * -0.12f), s.p(asa + t(V3(0f, 0.08f, 0f))), s.p(asa + ee * 0.12f))), a.plano(C.BRONCE, s.p(asa), 0f, lejos), style = Stroke((0.03f * s.k).coerceIn(0.3f, 2f)))
        // El verdín del bronce.
        mancha(s.p(culata + ee * 1.1f), 0.12f * s.k, 0.04f * s.k, a.plano(lerp(C.BRONCE, C.PINO_LUZ, 0.4f), s.p(culata), 0f, lejos), 0.35f)
        rueda(s, a, delante, r, t(V3(1f, 0f, 0f)), 0.09f, 14, 0, C.MADERA_GRIS, lejos, 0.1f, nieve)
        pts += listOf(s.p(boca), s.p(culata))
        // La nieve: en el tubo, en la cureña, y la que hunde la rueda y la contera.
        if (nieve > 0.1f) {
            nieveEncima(a, (0..6).map { i -> s.p(culata + (boca - culata) * (i / 6f) + V3(0f, 0.13f - 0.04f * i / 6f, 0f)) }.sortedBy { it.x }, (0.05f * s.k * nieve).coerceAtMost(4f), Azar(Semilla.de("napoleon", "canon")), lejos)
            for (p in listOf(ri, t(V3(0f, 0f, 2.3f)))) { val q = s.p(V3(p.x, 0f, p.z)); mancha(q, 0.5f * s.k, 0.12f * s.k, a.arriba(C.NIEVE, q, lejos), 0.95f) }
        }
        return poligono(envolvente(pts))
    }

    /**
     * El armón del cañón: dos ruedas, el eje, el cofre de munición con su tapa y la lanza, que cae al suelo hacia
     * delante. [s]: su sitio. Devuelve la silueta.
     */
    fun DrawScope.armon(s: Sitio, a: Alumbre, nieve: Float, lejos: Float = 0f): Path {
        val r = 0.6f
        val pts = ArrayList<Offset>()
        val ruedas = listOf(V3(0.68f, r, 0f), V3(-0.68f, r, 0f)).sortedByDescending { s.hondo(it) }
        rueda(s, a, ruedas[0], r, V3(1f, 0f, 0f), 0.08f, 12, 0, C.MADERA_GRIS, lejos, 0.5f, nieve)
        // La lanza, hacia delante, con la punta en la nieve.
        val l0 = s.p(V3(0f, 0.55f, -0.4f)); val l1 = s.p(V3(0.1f, 0.02f, -2.6f))
        drawLine(a.plano(C.MADERA_GRIS, l0, 0f, lejos), l0, l1, (0.06f * s.k).coerceIn(0.5f, 4f), StrokeCap.Round)
        pts += listOf(l0, l1)
        // El cofre de munición, verde oliva como lo pintaban **[Aproximación]**, con su tapa en lomo y sus herrajes.
        val verde = lerp(C.PINO_LUZ, C.CAPOTE, 0.35f)
        val caras = caja(s, a, -0.55f, 0.55f, 0.75f, 1.15f, -0.32f, 0.32f, verde, lejos, arriba = verde)
        for ((c3, f) in caras) clipPath(f) { for (u in listOf(0.1f, 0.9f)) linea(s, c3, u, 0f, u, 1f, a.cara(C.HIERRO, s.normal(c3.n), s.p(c3.centro), lejos), (0.025f * s.k).coerceIn(0.3f, 2f)) }
        pts += esquinas(-0.55f, 0.55f, 0.75f, 1.15f, -0.32f, 0.32f).map { s.p(it) }
        rueda(s, a, ruedas[1], r, V3(1f, 0f, 0f), 0.08f, 12, 1, C.MADERA_GRIS, lejos, 0.2f, nieve)
        pts += circulo(s, ruedas[0], V3(1f, 0f, 0f), r, 10) + circulo(s, ruedas[1], V3(1f, 0f, 0f), r, 10)
        if (nieve > 0.1f) nieveEncima(a, listOf(s.p(V3(-0.55f, 1.15f, 0f)), s.p(V3(0f, 1.15f, 0f)), s.p(V3(0.55f, 1.15f, 0f))).sortedBy { it.x }, (0.06f * s.k * nieve).coerceAtMost(4f), Azar(Semilla.de("napoleon", "armon")), lejos)
        return poligono(envolvente(pts))
    }

    // =================================================================
    //  El carro volcado
    // =================================================================

    /**
     * Un carro de bagaje volcado de lado, con una rueda al aire y la otra rota debajo, y su carga desparramada (sacos,
     * un barril, un fardo). [cargado]: el primer día de la retirada todavía pasa cargado, de pie, con su toldo. [s]: su
     * sitio. Devuelve la silueta. Sirve de cerca y de lejos (con [lejos]).
     */
    fun DrawScope.carro(s: Sitio, a: Alumbre, nieve: Float, semilla: Long, lejos: Float = 0f, cargado: Boolean = false): Path {
        val az = Azar(semilla); val pts = ArrayList<Offset>()
        val madera = lerp(C.MADERA_GRIS, C.MADERA, az.entre(0f, 0.6f))
        if (cargado) {
            // De pie, con su toldo de lona sobre los arcos y la carga asomando.
            val r = 0.6f
            rueda(s, a, V3(-0.8f, r, 0f), r, V3(1f, 0f, 0f), 0.08f, 12, 0, madera, lejos)
            caja(s, a, -0.7f, 0.7f, 0.6f, 1.1f, -1.2f, 1.2f, madera, lejos)
            val arcos = (0..8).map { i -> val t = PI.toFloat() * i / 8f; V3(-0.75f * cos(t), 1.1f + 0.75f * sin(t), 0f) }
            for (i in 0 until 8) cara(s, a, listOf(arcos[i] + V3(0f, 0f, -1.25f), arcos[i + 1] + V3(0f, 0f, -1.25f), arcos[i + 1] + V3(0f, 0f, 1.25f), arcos[i] + V3(0f, 0f, 1.25f)), Vec.unidad(V3(arcos[i].x + arcos[i + 1].x, arcos[i].y + arcos[i + 1].y - 2.2f, 0f)), C.LONA, lejos)
            rueda(s, a, V3(0.8f, r, 0f), r, V3(1f, 0f, 0f), 0.08f, 12, 0, madera, lejos)
            pts += esquinas(-0.8f, 0.8f, 0f, 1.85f, -1.25f, 1.25f).map { s.p(it) }
            return poligono(envolvente(pts))
        }
        // La caja, volcada: el fondo mira a quien mira y el borde de arriba toca el suelo.
        fun t(p: V3) = V3(-p.y + 0.55f, p.x + 0.7f, p.z)
        val tn = { n: V3 -> V3(-n.y, n.x, n.z) }
        val e = esquinas(-0.7f, 0.7f, 0f, 0.55f, -1.2f, 1.2f).map { t(it) }
        for (c3 in visibles(s, carasDeCaja(-0.7f, 0.7f, 0f, 0.55f, -1.2f, 1.2f).map { k -> Cara3(k.q.map { t(it) }, tn(k.n)) })) {
            val f = cara(s, a, c3.q, c3.n, madera, lejos) ?: continue
            clipPath(f) { for (v in listOf(0.25f, 0.5f, 0.75f)) linea(s, c3, 0f, v, 1f, v, mult(a.cara(madera, s.normal(c3.n), s.p(c3.centro), lejos), C.SOMBRA, 0.35f), (0.01f * s.k).coerceAtLeast(0.25f)) }
        }
        pts += e.map { s.p(it) }
        // La carga: sacos y un barril desparramados delante.
        repeat(3) { i ->
            val q = s.p(V3(az.entre(-0.5f, 1.4f), 0f, -1.4f + i * 0.5f + az.entre(-0.2f, 0.2f)))
            val k = s.escala(0f, -1f); val sz = az.entre(0.22f, 0.32f) * k
            drawPath(TrazosValle.blando(q + Offset(0f, -sz * 0.4f), sz, sz * 0.55f, az, 8, 0.2f), a.plano(lerp(C.LONA, C.BARRO, 0.3f), q, if (i % 2 == 0) 0.4f else -0.4f, lejos, 0.5f))
            pts += q
        }
        val barril = V3(1.0f, 0.22f, -0.9f)
        cilindro(s, a, barril + V3(0f, 0f, -0.3f), V3(0.3f, 0f, 1f), 0.22f, 0.22f, 0.55f, madera, lerp(madera, C.CENIZA, 0.2f), 12, lejos)
        // La rueda al aire (de lado, por encima) y la rota, debajo.
        rueda(s, a, t(V3(0.75f, 0.55f, 0.3f)) + V3(0f, 0.05f, 0f), 0.55f, Vec.unidad(V3(0.2f, 1f, -0.3f)), 0.07f, 12, 2, madera, lejos, az.entre(0f, 1f), nieve)
        pts += circulo(s, t(V3(0.75f, 0.55f, 0.3f)), Vec.unidad(V3(0.2f, 1f, -0.3f)), 0.55f, 10)
        if (nieve > 0.1f) nieveEncima(a, listOf(s.p(e[2]), s.p((e[2] + e[6]) * 0.5f), s.p(e[6])).sortedBy { it.x }, (0.08f * s.k * nieve).coerceAtMost(6f), az, lejos)
        return poligono(envolvente(pts))
    }

    // =================================================================
    //  La casa de madera quemada
    // =================================================================

    /**
     * Una casa de madera quemada («Los pueblos del camino de vuelta están quemados»; «lo que queda de una granja, sin
     * techo»): las paredes de troncos, comidas por el fuego desde arriba (altas en las esquinas, donde los troncos se
     * traban, y bajas en medio, negras en el borde), el hueco de la ventana, la estufa encalada con su chimenea, que no
     * ardió, los dos cabios de un hastial que quedan en pie y la viga caída, y la nieve en lo que queda. Con [ardiendo]
     * (Maloyaroslavets), el interior al rojo: las llamas y el humo van en la parte viva. [s]: su sitio; [ancho] y
     * [fondo] en metros. Devuelve la silueta.
     */
    fun DrawScope.casaQuemada(s: Sitio, a: Alumbre, ancho: Float, fondo: Float, semilla: Long, nieve: Float, ardiendo: Boolean, lejos: Float = 0f): Path {
        val az = Azar(semilla); val w = ancho / 2f; val d = fondo / 2f; val alto = 1.5f
        val pts = ArrayList<Offset>()
        val kk = s.k
        // El perfil roto de una pared: alta en las dos esquinas, comida en medio, en tramos (no en dientes).
        fun perfil(): List<Pair<Float, Float>> {
            val m1 = az.entre(0.22f, 0.4f); val m2 = az.entre(0.6f, 0.78f)
            return listOf(0f to alto * az.entre(0.7f, 1f), 0.08f to alto * az.entre(0.5f, 0.75f), m1 to alto * az.entre(0.2f, 0.4f), (m1 + m2) / 2f to alto * az.entre(0.12f, 0.3f),
                m2 to alto * az.entre(0.45f, 0.7f), 0.92f to alto * az.entre(0.75f, 0.9f), 1f to alto * az.entre(0.85f, 1f))
        }
        val paredes = listOf(
            Triple(V3(-w, 0f, -d), V3(w, 0f, -d), V3(0f, 0f, -1f)), Triple(V3(w, 0f, -d), V3(w, 0f, d), V3(1f, 0f, 0f)),
            Triple(V3(w, 0f, d), V3(-w, 0f, d), V3(0f, 0f, 1f)), Triple(V3(-w, 0f, d), V3(-w, 0f, -d), V3(-1f, 0f, 0f)),
        ).map { (p0, p1, n) -> Pared(p0, p1, n, perfil()) }
        // El suelo de dentro: ceniza (y nieve encima), o al rojo si arde.
        val suelo = listOf(V3(-w, 0.02f, -d), V3(w, 0.02f, -d), V3(w, 0.02f, d), V3(-w, 0.02f, d)).map { s.p(it) }
        drawPath(poligono(suelo), if (ardiendo) a.brasa(0.55f) else a.arriba(lerp(C.CENIZA, C.NIEVE, 0.7f * nieve), centroDe(suelo), lejos))
        // Primero las paredes de detrás (se ve su cara de dentro, negra) y la estufa; luego las de delante.
        val (detras, delante) = paredes.partition { !s.seVe((it.p0 + it.p1) * 0.5f + V3(0f, 1f, 0f), it.n) }
        for (pw in detras.sortedByDescending { s.hondo((it.p0 + it.p1) * 0.5f) }) pared(s, a, pw, false, ardiendo, nieve, az, lejos, pts)
        // La estufa encalada y su chimenea, en pie, con el hollín arriba.
        val ex = az.entre(-w * 0.35f, w * 0.1f); val ez = az.entre(-d * 0.1f, d * 0.3f)
        val estufa = caja(s, a, ex - 0.75f, ex + 0.75f, 0f, 1.5f, ez - 0.7f, ez + 0.7f, lerp(C.LONA, C.NIEVE, 0.3f), lejos)
        for ((c3, f) in estufa) clipPath(f) { mancha(s.p(c3.en(0.5f, 0.95f)), 0.6f * kk, 0.25f * kk, a.cara(C.CENIZA, s.normal(c3.n), s.p(c3.centro), lejos), 0.55f); if (!c3.arriba) mancha(s.p(c3.en(0.45f, 0.35f)), 0.25f * kk, 0.2f * kk, a.cara(C.CENIZA, s.normal(c3.n), s.p(c3.centro), lejos), 0.8f) }
        val chim = caja(s, a, ex - 0.28f, ex + 0.28f, 1.5f, 3.9f, ez - 0.28f, ez + 0.28f, lerp(C.LONA, C.ROJO, 0.18f), lejos)
        for ((c3, f) in chim) clipPath(f) {
            for (j in 0 until 10) linea(s, c3, 0f, j / 10f, 1f, j / 10f, mult(a.cara(C.LONA, s.normal(c3.n), s.p(c3.centro), lejos), C.SOMBRA, 0.25f), (0.025f * kk).coerceAtLeast(0.2f))
            drawRect(Brush.verticalGradient(listOf(a.cara(C.CENIZA, s.normal(c3.n), s.p(c3.centro), lejos), Color.Transparent), s.p(c3.en(0.5f, 1f)).y, s.p(c3.en(0.5f, 0.55f)).y), Offset(-10f, s.p(c3.en(0.5f, 1f)).y), Size(ANCHO + 20f, s.p(c3.en(0.5f, 0.55f)).y - s.p(c3.en(0.5f, 1f)).y))
        }
        pts += s.p(V3(ex, 3.9f, ez))
        if (nieve > 0.15f) nieveEncima(a, listOf(s.p(V3(ex - 0.75f, 1.5f, ez - 0.7f)), s.p(V3(ex, 1.5f, ez - 0.7f)), s.p(V3(ex + 0.75f, 1.5f, ez - 0.7f))).sortedBy { it.x }, (0.12f * kk * nieve).coerceAtMost(3f), az, lejos)
        // Los dos cabios del hastial de un lado, que quedan en pie, y la viga del caballete, caída en diagonal.
        val hx = if (az.real() < 0.5f) -w else w
        val c0 = V3(hx, alto * 0.9f, -d); val c1 = V3(hx, alto * 0.9f, d); val cumbre = V3(hx, alto + 1.7f, 0f)
        val roto = c0 + (cumbre - c0) * 0.6f
        run { val q0 = s.p(c0); val q1 = s.p(roto); drawLine(a.plano(C.CENIZA, q0, 0f, lejos), q0, q1, (0.14f * kk).coerceIn(0.5f, 3f), StrokeCap.Round); pts += q1 }
        val v0 = s.p(c1 + V3(0f, -0.3f, 0f)); val v1 = s.p(V3(-hx * 0.6f, 0.3f, az.entre(-0.4f, 0.4f)))
        drawLine(a.plano(lerp(C.CENIZA, C.MADERA_GRIS, 0.3f), v0, 0f, lejos), v0, v1, (0.16f * kk).coerceIn(0.5f, 3.2f), StrokeCap.Round)
        if (nieve > 0.2f) drawLine(a.arriba(C.NIEVE, v0, lejos), v0 + Offset(0f, -0.08f * kk), v1 + Offset(0f, -0.08f * kk), (0.06f * kk * nieve).coerceIn(0.3f, 1.6f), StrokeCap.Round)
        for (pw in delante.sortedByDescending { s.hondo((it.p0 + it.p1) * 0.5f) }) pared(s, a, pw, true, ardiendo, nieve, az, lejos, pts)
        return poligono(envolvente(pts))
    }

    /** Una pared de troncos de la casa quemada: de [p0] a [p1], con la normal [n] hacia fuera y el [perfil] de lo que queda (t, alto). */
    private class Pared(val p0: V3, val p1: V3, val n: V3, val perfil: List<Pair<Float, Float>>)

    private fun DrawScope.pared(s: Sitio, a: Alumbre, pw: Pared, fuera: Boolean, ardiendo: Boolean, nieve: Float, az: Azar, lejos: Float, pts: ArrayList<Offset>) {
        val kk = s.k
        val arriba = pw.perfil.map { (t, h) -> pw.p0 + (pw.p1 - pw.p0) * t + V3(0f, h, 0f) }
        val q = listOf(pw.p0, pw.p1) + arriba.reversed()
        val qq = q.map { s.p(it) }; val f = poligono(qq); pts += qq
        val nVista = if (fuera) pw.n else pw.n * -1f
        val madera = if (fuera) lerp(C.CENIZA, C.MADERA_GRIS, 0.55f) else lerp(C.CENIZA, C.MADERA_GRIS, 0.2f)
        val col = a.cara(madera, s.normal(nVista), centroDe(qq), lejos)
        drawPath(f, col)
        clipPath(f) {
            // Los troncos: cada uno con su canto de luz arriba y su junta oscura abajo.
            for (j in 1 until 11) {
                val y = j * 0.25f
                drawLine(mult(col, C.SOMBRA, 0.5f), s.p(pw.p0 + V3(0f, y, 0f)), s.p(pw.p1 + V3(0f, y, 0f)), (0.04f * kk).coerceIn(0.25f, 1.8f))
                drawLine(lerp(col, Color.White, 0.12f), s.p(pw.p0 + V3(0f, y + 0.05f, 0f)), s.p(pw.p1 + V3(0f, y + 0.05f, 0f)), (0.02f * kk).coerceIn(0.2f, 1f))
            }
            // Lo quemado: negro desde el borde de arriba, que baja en lenguas.
            val borde = arriba.map { s.p(it) }
            val negro = a.cara(C.CENIZA, s.normal(nVista), centroDe(qq), lejos)
            drawPath(poligono(borde + borde.reversed().map { it + Offset(0f, (0.5f + 0.3f * az.real()) * kk) }), negro)
            for (i in 0 until 4) { val b = borde[1 + i % (borde.size - 2)]; mancha(b + Offset(0f, 0.6f * kk), 0.25f * kk, 0.5f * kk, negro, 0.8f) }
            if (fuera) {
                // El hueco de la ventana, negro (o al rojo si arde), con su marco quemado.
                val u = 0.45f + az.entre(-0.1f, 0.1f)
                val vq = listOf(pw.p0 + (pw.p1 - pw.p0) * u + V3(0f, 0.85f, 0f), pw.p0 + (pw.p1 - pw.p0) * (u + 0.14f) + V3(0f, 0.85f, 0f), pw.p0 + (pw.p1 - pw.p0) * (u + 0.14f) + V3(0f, 1.5f, 0f), pw.p0 + (pw.p1 - pw.p0) * u + V3(0f, 1.5f, 0f)).map { s.p(it + pw.n * 0.01f) }
                drawPath(poligono(vq), if (ardiendo) a.brasa(0.85f) else a.plano(C.CENIZA, centroDe(vq), 0f, lejos))
            }
        }
        // Las cabezas de los troncos en las esquinas, que sobresalen (el ensamble).
        for (pe in listOf(pw.p0, pw.p1)) for (j in 0 until 9) {
            val h = 0.12f + j * 0.25f; if (h > pw.perfil.first().second) break
            val c = s.p(pe + V3(0f, h, 0f) + pw.n * 0.08f)
            drawCircle(mult(col, C.SOMBRA, 0.2f), (0.13f * kk).coerceAtLeast(0.4f), c)
            drawCircle(lerp(col, C.LONA, 0.25f), (0.07f * kk).coerceAtLeast(0.25f), c + Offset(-0.03f * kk, -0.03f * kk))
        }
        if (nieve > 0.15f) nieveEncima(a, arriba.map { s.p(it) }.sortedBy { it.x }, (0.14f * kk * nieve).coerceAtMost(4f), az, lejos)
    }

    // =================================================================
    //  La tienda de oficial
    // =================================================================

    /**
     * La tienda de un oficial (el soldado duerme al raso; los oficiales, en tienda): una tienda de pared de lona, con los
     * dos mástiles, el caballete, los faldones atados, la puerta abierta con su penumbra (y, de noche, el farol que la
     * alumbra por dentro: [farol], de 0 a 1), los vientos tensos hasta las estacas y los remiendos. Medidas
     * **[Aproximación]**: 2.6 de ancho, 3.4 de fondo, 1.3 de pared y 2.6 de caballete. [s]: su sitio (la puerta mira a
     * −z). Devuelve la silueta.
     */
    fun DrawScope.tienda(s: Sitio, a: Alumbre, farol: Float, nieve: Float, lejos: Float = 0f): Path {
        val w = 1.3f; val d = 1.7f; val pared = 1.3f; val cumbre = 2.6f; val az = Azar(Semilla.de("napoleon", "tienda"))
        val lona = C.LONA
        val pts = ArrayList<Offset>()
        // Los vientos, por detrás y a los lados.
        for ((x, z) in listOf(-w to -d * 0.6f, -w to d * 0.6f, w to -d * 0.6f, w to d * 0.6f)) {
            val p0 = s.p(V3(x, pared, z)); val p1 = s.p(V3(x * 2.4f, 0f, z * 1.3f))
            drawLine(a.plano(C.CORREA, p0, 0f, lejos).copy(alpha = 0.9f), p0, p1, (0.012f * s.k).coerceIn(0.3f, 1.2f))
            drawLine(a.plano(C.MADERA, p1, 0f, lejos), p1, p1 + Offset(0f, -0.15f * s.k), (0.03f * s.k).coerceIn(0.4f, 2f))
            pts += p1
        }
        // Las paredes y los dos faldones del techo.
        val caras = listOf(
            listOf(V3(-w, 0f, -d), V3(w, 0f, -d), V3(w, pared, -d), V3(0f, cumbre, -d), V3(-w, pared, -d)) to V3(0f, 0f, -1f),
            listOf(V3(w, 0f, d), V3(-w, 0f, d), V3(-w, pared, d), V3(0f, cumbre, d), V3(w, pared, d)) to V3(0f, 0f, 1f),
            listOf(V3(w, 0f, -d), V3(w, 0f, d), V3(w, pared, d), V3(w, pared, -d)) to V3(1f, 0f, 0f),
            listOf(V3(-w, 0f, d), V3(-w, 0f, -d), V3(-w, pared, -d), V3(-w, pared, d)) to V3(-1f, 0f, 0f),
            listOf(V3(w, pared, -d), V3(w, pared, d), V3(0f, cumbre, d), V3(0f, cumbre, -d)) to Vec.unidad(V3(cumbre - pared, w, 0f)),
            listOf(V3(-w, pared, d), V3(-w, pared, -d), V3(0f, cumbre, -d), V3(0f, cumbre, d)) to Vec.unidad(V3(-(cumbre - pared), w, 0f)),
        )
        for ((q, n) in caras) {
            val f = cara(s, a, q, n, lona, lejos) ?: continue
            val qq = q.map { s.p(it) }; pts += qq
            clipPath(f) {
                // Las costuras de los paños y los remiendos, y la mancha de barro de abajo.
                val c = centroDe(qq)
                for (j in 1..4) { val u = j / 5f; val a0 = q[0] + (q[1] - q[0]) * u; drawLine(mult(a.cara(lona, s.normal(n), c, lejos), C.SOMBRA, 0.3f), s.p(a0), s.p(a0 + V3(0f, cumbre, 0f)), 0.4f) }
                mancha(c + Offset(az.entre(-6f, 6f), az.entre(-4f, 4f)), 0.25f * s.k, 0.18f * s.k, a.cara(lerp(lona, C.HOJA, 0.3f), s.normal(n), c, lejos), 0.5f)
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, a.cara(lerp(lona, C.BARRO, 0.45f), s.normal(n), c, lejos).copy(alpha = 0.7f)), qq.maxOf { it.y } - 0.35f * s.k, qq.maxOf { it.y }), Offset(qq.minOf { it.x }, qq.maxOf { it.y } - 0.35f * s.k), Size(qq.maxOf { it.x } - qq.minOf { it.x }, 0.35f * s.k))
            }
        }
        // La puerta abierta, con los faldones recogidos y atados, y la penumbra de dentro.
        if (s.seVe(V3(0f, 1f, -d), V3(0f, 0f, -1f))) {
            val pq = listOf(V3(-0.42f, 0f, -d - 0.01f), V3(0.42f, 0f, -d - 0.01f), V3(0.1f, 2.2f, -d - 0.01f), V3(-0.1f, 2.2f, -d - 0.01f)).map { s.p(it) }
            val dentro = lerp(a.plano(lerp(C.CENIZA, lona, 0.25f), centroDe(pq), 0f, lejos), C.LUMBRE, 0.75f * farol)
            drawPath(poligono(pq), dentro)
            if (farol > 0.05f) drawPath(poligono(pq), Brush.radialGradient(listOf(C.LUMBRE.copy(alpha = 0.6f * farol), Color.Transparent), centroDe(pq), 0.7f * s.k))
            for (sg in listOf(-1f, 1f)) {
                val f0 = s.p(V3(0.12f * sg, 2.15f, -d - 0.02f)); val f1 = s.p(V3(0.62f * sg, 0.9f, -d - 0.03f)); val f2 = s.p(V3(0.5f * sg, 0f, -d - 0.02f))
                drawPath(poligono(f0, f1, f2, f2 + Offset(-0.12f * sg * s.k, 0f)), a.cara(lerp(lona, C.CAPOTE, 0.15f), s.normal(V3(0.3f * sg, 0f, -1f)), f1, lejos))
                drawLine(a.plano(C.CORREA, f1, 0f, lejos), f1 + Offset(-0.05f * s.k, 0f), f1 + Offset(0.06f * s.k, 0.04f * s.k), (0.02f * s.k).coerceAtLeast(0.3f))
            }
        }
        // El mástil de delante, que asoma por arriba, con su perilla.
        val m0 = s.p(V3(0f, cumbre, -d)); val m1 = s.p(V3(0f, cumbre + 0.25f, -d))
        drawLine(a.plano(C.MADERA, m0, 0f, lejos), m0, m1, (0.04f * s.k).coerceIn(0.4f, 3f)); drawCircle(a.plano(C.LATON, m1, 0f, lejos), (0.04f * s.k).coerceAtLeast(0.4f), m1)
        pts += m1
        if (nieve > 0.15f) nieveEncima(a, listOf(s.p(V3(-w, pared, -d)), s.p(V3(0f, cumbre, -d)), s.p(V3(w, pared, -d))).sortedBy { it.x }, (0.06f * s.k * nieve).coerceAtMost(5f), az, lejos)
        return poligono(envolvente(pts))
    }
}
