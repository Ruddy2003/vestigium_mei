package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  La cámara de Orden y las piezas de caras planas
// =====================================================================
//
// Todo el cuadro se arma en metros, en los ejes de la ciudadela, y se proyecta con una cámara de agujero (la misma idea
// que `Mirada` y `Sitio` de Mongol y la cámara del barco de Abismo): así el adarve, las almenas, el patio de abajo, la
// torre y la Garza comparten una sola perspectiva, y las sombras se tumban en tres dimensiones sobre el suelo de cada
// cosa (el patio, el adarve, la muralla del fondo).
//
// Los ejes de la ciudadela, en metros, con los ojos en el origen: [s] a través de la muralla (positivo hacia fuera, a la
// derecha; el borde de dentro del adarve en [CamOrden.BORDE]), [h] hacia arriba desde el suelo del adarve (el patio en
// [CamOrden.PATIO]) y [v] a lo largo de la muralla, hacia delante. Un punto se escribe `V3(s, h, v)`.

/** La cámara del cuadro de Orden. */
internal object CamOrden {
    /** El horizonte del páramo, «recto, como trazado con regla». */
    const val H = 118f
    const val F = 240f
    const val CX = 190f

    /** Los ojos sobre el suelo del adarve: de pie en lo alto de la banqueta **[Diseño propio]**, como en el tambucho de Abismo. */
    const val OJO = 3.0f

    /**
     * Cuánto se gira la vista respecto del camino de ronda: negativo, hacia dentro (17°, revisión 1): se mira en diagonal a
     * través del patio hacia la torre; el adarve se fuga a la derecha del centro y queda como una franja abajo, y por encima
     * del parapeto, a la derecha, el vacío y el páramo.
     */
    const val GIRO = -0.3f
    private val CG = cos(GIRO); private val SG = sin(GIRO)

    /** El borde de dentro del adarve (la caída al patio), a la izquierda de los ojos. */
    const val BORDE = -0.25f
    /** La cara de dentro del parapeto, a la derecha; su cara de fuera. */
    const val PARAPETO = 1.9f
    const val FUERA = 2.75f
    /** El alto del parapeto (el fondo de las troneras) y el de las almenas, sobre el adarve. */
    const val ALTO_PARAPETO = 0.75f
    const val ALTO_ALMENA = 1.85f
    /** El suelo del patio, bajo el adarve. */
    const val PATIO = -8f
    /** El llano, al pie de la meseta. */
    const val LLANO = -96f

    /** De los ejes de la ciudadela (s, v) a los de la cámara (x a la derecha, z de fondo). */
    fun cam(s: Float, v: Float): Offset = Offset(s * CG - v * SG, s * SG + v * CG)

    /** De los de la cámara a los de la ciudadela (para las sombras). */
    fun mundo(x: Float, z: Float): Offset = Offset(x * CG + z * SG, -x * SG + z * CG)

    /** Un punto en metros de la cámara (x, alto sobre el adarve, z) en el lienzo. */
    fun pc(x: Float, h: Float, z: Float): Offset { val zz = z.coerceAtLeast(0.15f); return Offset(CX + F * x / zz, H + F * (OJO - h) / zz) }

    /** Un punto de la ciudadela (s, h, v) en el lienzo. */
    fun p(s: Float, h: Float, v: Float): Offset { val c = cam(s, v); return pc(c.x, h, c.y) }
    fun p(a: V3): Offset = p(a.x, a.y, a.z)

    /** La profundidad de un punto (para pintar de atrás adelante). */
    fun hondo(s: Float, v: Float): Float = cam(s, v).y
    fun hondo(a: V3): Float = hondo(a.x, a.z)

    /** Cuántas unidades del lienzo mide un metro a la profundidad de (s, v). */
    fun k(s: Float, v: Float): Float = F / hondo(s, v).coerceAtLeast(0.15f)

    /** Una dirección de la ciudadela (una normal) en los ejes de la cámara, para la luz de `Sol3`. */
    fun normal(n: V3): V3 { val c = cam(n.x, n.z); return V3(c.x, n.y, c.y) }

    /** Si la cara que pasa por [a] con la normal [n] mira hacia los ojos. */
    fun seVe(a: V3, n: V3): Boolean = n.x * -a.x + n.y * (OJO - a.y) + n.z * -a.z > 0f

    /** El punto del suelo de la ciudadela a la altura [h] que se ve en el punto [q] del lienzo (para recortar y repartir). */
    fun enElSuelo(q: Offset, h: Float): V3 {
        val dy = (q.y - H).coerceAtLeast(0.01f)
        val z = F * (OJO - h) / dy
        val x = (q.x - CX) * z / F
        val w = mundo(x, z)
        return V3(w.x, h, w.y)
    }
}

/**
 * La luz de un plano, con el sol (o la luna) del momento: el lado de luz de cada cara y hacia dónde caen las sombras. De
 * noche, [plata] (de 0 a 1) es cuánto alumbra la luna: lo que la mira toma su luz fría.
 */
internal class LuzPlana(val p: Pincel, val sol: Sol3, val plata: Float = 0f) {
    /** Hacia dónde se corre la sombra en el suelo, en metros de la ciudadela (s, v) por metro de alto. */
    val corre: Offset = CamOrden.mundo(sol.sx, sol.sz)

    /** De −1 (de espaldas a la luz) a 1 (de cara), para la normal [n] de la ciudadela. */
    fun t(n: V3): Float = sol.t(CamOrden.normal(n))

    /** El color de una cara con la normal [n]; de noche, con la luna, su lado de plata. */
    fun color(c: Color, n: V3, lejos: Float = 0f, claro: Float = 0f): Color {
        val t = (t(n) + claro).coerceIn(-1f, 1f)
        val base = p.por(c, t, lejos)
        if (plata <= 0.02f || t <= 0f) return base
        val luna = Color(lerp(c, Color.White, 0.55f).red * ColorOrden.LUNA.red, lerp(c, Color.White, 0.55f).green * ColorOrden.LUNA.green, lerp(c, Color.White, 0.55f).blue * ColorOrden.LUNA.blue)
        return lerp(base, p.l.tono(luna, lejos).let { lerp(it, luna, 0.35f) }, (0.45f * plata * t).coerceIn(0f, 1f))
    }

    /** El punto donde cae en el suelo a la altura [suelo] la sombra del punto [a]. */
    fun alSuelo(a: V3, suelo: Float): V3 { val d = (a.y - suelo).coerceAtLeast(0f); return V3(a.x + corre.x * d, suelo, a.z + corre.y * d) }
}

/**
 * Las piezas de caras planas de Orden: caras sueltas, cajas, sillares sin labrar, palos y la sombra de un cuerpo sobre un
 * suelo. Todo en metros de la ciudadela, con [CamOrden].
 */
internal object PiezasOrden {
    private val C = ColorOrden

    /** Una cara plana, si se ve: con el color de su lado de luz. Devuelve su forma en el lienzo, o null. */
    fun DrawScope.cara(lz: LuzPlana, puntos: List<V3>, n: V3, color: Color, lejos: Float = 0f, claro: Float = 0f): Path? {
        val c = centro(puntos)
        if (!CamOrden.seVe(c, n)) return null
        val forma = poligono(puntos.map { CamOrden.p(it) })
        val tono = lz.color(color, n, lejos, claro)
        drawPath(forma, tono)
        drawPath(forma, tono, style = Stroke(0.5f))
        return forma
    }

    fun centro(puntos: List<V3>): V3 = V3(puntos.sumOf { it.x.toDouble() }.toFloat() / puntos.size, puntos.sumOf { it.y.toDouble() }.toFloat() / puntos.size, puntos.sumOf { it.z.toDouble() }.toFloat() / puntos.size)

    /**
     * Una caja de base rectangular girada [giro] radianes (0: sus lados siguen la muralla), con el centro de la base en
     * ([s], [v]), [ancho] a lo largo de su x, [fondo] a lo largo de su z, de [h0] a [h1]. [encima] pinta lo de cada cara que
     * se ve (0 delante (−z), 1 detrás, 2 izquierda (−x), 3 derecha, 4 arriba), recortado a ella. Devuelve la silueta.
     */
    fun DrawScope.caja(lz: LuzPlana, s: Float, v: Float, giro: Float, ancho: Float, fondo: Float, h0: Float, h1: Float, color: Color, lejos: Float = 0f,
                       arriba: Color = color, encima: (DrawScope.(Int, Path) -> Unit)? = null): List<V3> {
        val q = esquinas(s, v, giro, ancho, fondo)
        val bajo = q.map { V3(it.x, h0, it.y) }; val alto = q.map { V3(it.x, h1, it.y) }
        val ux = Offset(cos(giro), -sin(giro)); val uz = Offset(sin(giro), cos(giro))
        val caras = listOf(
            listOf(bajo[0], bajo[1], alto[1], alto[0]) to V3(-uz.x, 0f, -uz.y),
            listOf(bajo[3], bajo[2], alto[2], alto[3]) to V3(uz.x, 0f, uz.y),
            listOf(bajo[0], bajo[3], alto[3], alto[0]) to V3(-ux.x, 0f, -ux.y),
            listOf(bajo[1], bajo[2], alto[2], alto[1]) to V3(ux.x, 0f, ux.y),
            alto to V3(0f, 1f, 0f),
        )
        for ((i, c) in caras.withIndex()) {
            val forma = cara(lz, c.first, c.second, if (i == 4) arriba else color, lejos, if (i == 4) 0.22f else 0f) ?: continue
            if (encima != null) clipPath(forma) { encima(i, forma) }
        }
        return bajo + alto
    }

    /** Las cuatro esquinas de una base girada: delante-izquierda, delante-derecha, detrás-derecha, detrás-izquierda (en s, v). */
    fun esquinas(s: Float, v: Float, giro: Float, ancho: Float, fondo: Float): List<Offset> {
        val ux = Offset(cos(giro), -sin(giro)) * (ancho / 2f); val uz = Offset(sin(giro), cos(giro)) * (fondo / 2f); val c = Offset(s, v)
        return listOf(c - ux - uz, c + ux - uz, c + ux + uz, c - ux + uz)
    }

    /**
     * Piedra sin labrar sobre una cara plana: de [a] (abajo a la izquierda) a [b] (abajo a la derecha), [alto] metros hacia
     * arriba. Hiladas torcidas de piedras de tamaños distintos, cada una un polígono irregular con su tono, sobre el mortero
     * (más claro y arenoso, metido hacia dentro): mampostería, no ladrillo. [tono] es el color de la cara ya iluminada.
     */
    fun DrawScope.sillares(a: V3, b: V3, alto: Float, tono: Color, az: Azar, hilada: Float = 0.42f, piedra: Float = 0.7f, junta: Color = tono, lejos: Float = 0f) {
        val largo = sqrt((b.x - a.x) * (b.x - a.x) + (b.z - a.z) * (b.z - a.z) + (b.y - a.y) * (b.y - a.y)).coerceAtLeast(0.01f)
        fun punto(u: Float, w: Float) = CamOrden.p(a.x + (b.x - a.x) * u / largo, a.y + (b.y - a.y) * u / largo + w, a.z + (b.z - a.z) * u / largo)
        val k = CamOrden.k(a.x, a.z).coerceAtLeast(CamOrden.k(b.x, b.z))
        if (k * hilada < 1.8f) return                                            // muy lejos: no se ven las piedras
        // El mortero, entre las piedras: más claro.
        drawRect(lerp(tono, Color(0xFFE8D4B0), 0.16f).copy(alpha = 0.5f - 0.3f * lejos), Offset(-10f, -10f), androidx.compose.ui.geometry.Size(400f, 300f))
        val claro = lerp(tono, Color(0xFFFFE8D0), 0.12f); val oscuro = lerp(tono, Color(0xFF4A2E3A), 0.14f); val rojo = lerp(tono, Color(0xFF8A3A2A), 0.12f)
        var w = 0f
        // Las piedras grandes de la hilada de abajo que suben a esta (de dónde a dónde): la de arriba las salta.
        var altas = emptyList<Pair<Float, Float>>()
        while (w < alto) {
            val h0 = hilada * az.entre(0.65f, 1.3f)
            var u = -az.entre(0f, piedra)
            val antes = altas; val nuevas = ArrayList<Pair<Float, Float>>()
            while (u < largo) {
                var l = piedra * az.entre(0.45f, 1.7f)
                val tapa = antes.firstOrNull { u < it.second && u + l > it.first }
                if (tapa != null) { if (u < tapa.first - 0.15f) l = tapa.first - u else { u = tapa.second; continue } }
                val doble = az.real() < 0.14f && w + h0 * 2f < alto
                val h = if (doble) h0 * 1.9f else h0
                if (doble) nuevas += u to (u + l)
                val g = 0.04f + 0.04f * az.real()
                val sube = az.entre(-0.06f, 0.06f)
                val q = listOf(
                    punto(u + g, w + g + az.entre(0f, 0.05f)), punto(u + l * 0.5f, w + g * 0.6f), punto(u + l - g, w + g + az.entre(0f, 0.06f)),
                    punto(u + l - g * az.entre(0.5f, 1.5f), w + h * 0.55f + sube), punto(u + l - g, w + h - g), punto(u + l * 0.45f, w + h - g * 0.5f + sube),
                    punto(u + g, w + h - g - az.entre(0f, 0.05f)), punto(u + g * az.entre(0.5f, 1.5f), w + h * 0.45f),
                )
                val c = when (az.entero(6)) { 0 -> claro; 1 -> oscuro; 2 -> rojo; 3 -> lerp(tono, claro, 0.5f); else -> tono }
                drawPath(poligono(q), c)
                // La sombrita del canto de abajo de cada piedra (sobresale del mortero).
                if (k * h > 7f) drawLine(oscuro.copy(alpha = 0.4f), q[6], q[4], (k * 0.022f).coerceIn(0.3f, 1f))
                u += l
            }
            altas = nuevas
            w += h0
        }
    }

    /** Un palo (redondo o de hierro) de [a] a [b], de [grueso] metros, con su filo de luz del lado del sol. */
    fun DrawScope.palo(lz: LuzPlana, a: V3, b: V3, grueso: Float, color: Color, lejos: Float = 0f) {
        val pa = CamOrden.p(a); val pb = CamOrden.p(b); val d = pb - pa; val largo = d.getDistance().coerceAtLeast(0.01f)
        val n = Offset(-d.y / largo, d.x / largo)
        val wa = (grueso * CamOrden.k(a.x, a.z) / 2f).coerceAtLeast(0.25f); val wb = (grueso * CamOrden.k(b.x, b.z) / 2f).coerceAtLeast(0.25f)
        drawPath(poligono(pa + n * wa, pb + n * wb, pb - n * wb, pa - n * wa), lz.p.medio(color, lejos))
        val luz = if ((n.x > 0f) == lz.p.solALaDerecha) 1f else -1f
        val w = (wa + wb) / 2f
        if (w > 0.4f) drawLine(lz.p.luz(color, lejos), pa + n * (wa * 0.5f * luz), pb + n * (wb * 0.5f * luz), (w * 0.6f).coerceAtLeast(0.3f), StrokeCap.Round)
    }

    /** Una línea fina (un cabo, una cuerda) de [a] a [b] que cuelga [caida] metros en medio. */
    fun DrawScope.cabo(a: V3, b: V3, caida: Float, color: Color, grueso: Float = 0.6f) {
        val pts = (0..10).map { i -> val t = i / 10f; CamOrden.p(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t - caida * 4f * t * (1f - t), a.z + (b.z - a.z) * t) }
        val camino = Path().apply { moveTo(pts[0].x, pts[0].y); for (q in pts.drop(1)) lineTo(q.x, q.y) }
        drawPath(camino, color, style = Stroke(grueso, cap = StrokeCap.Round))
    }

    /**
     * La sombra de un cuerpo convexo de puntos [puntos] sobre el suelo a la altura [suelo]: los puntos tumbados con la
     * luz y su envolvente, en el lienzo. Null si no hay sol.
     */
    fun sombraEnElSuelo(lz: LuzPlana, puntos: List<V3>, suelo: Float): Path? {
        if (lz.sol.r.fuerza <= 0.02f) return null
        val q = envolvente(puntos.map { CamOrden.p(lz.alSuelo(it, suelo)) })
        return if (q.size < 3) null else poligono(q)
    }

    /**
     * La sombra de un cuerpo convexo sobre una pared vertical que sigue la muralla (el plano v = [v0]), hacia la que va la
     * luz: el punto donde el rayo de cada punto corta la pared. Null si la luz no va hacia ella.
     */
    fun sombraEnLaPared(lz: LuzPlana, puntos: List<V3>, v0: Float): Path? {
        if (lz.sol.r.fuerza <= 0.02f || lz.corre.y <= 0.02f) return null
        val q = envolvente(puntos.mapNotNull { a ->
            val t = (v0 - a.z) / lz.corre.y
            if (t < 0f) null else CamOrden.p(a.x + lz.corre.x * t, a.y - t, v0)
        })
        return if (q.size < 3) null else poligono(q)
    }

    /** Un azar fijo de Peñaseca (lo que no cambia de un día a otro). */
    fun fijo(vararg partes: Any): Azar = Azar(io.github.ruddy2003.vestigium.vidas.Semilla.de("orden", "penaseca", *partes))

    /** Los tonos de una cosa a lo lejos: la piedra se va al color de la bruma. */
    fun piedra(lz: LuzPlana, n: V3, lejos: Float): Color = lz.color(C.PIEDRA, n, lejos)
}
