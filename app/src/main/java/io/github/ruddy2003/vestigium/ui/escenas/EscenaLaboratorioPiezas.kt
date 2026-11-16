package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Tarea
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  La perspectiva del laboratorio
// =====================================================================
//
// La sala se arma en metros y se mira con una cámara de agujero (`composicion_laboratorio.md`): de pie en un rincón,
// mirando en diagonal, con dos paredes a la vista. El eje hondo de la sala (el pasillo que sale por la puerta de vidrio,
// la pared de la pantalla) fuga hacia (95, 120); el de la pared del fondo, fuera del lienzo por la derecha. Así cada
// cosa enseña dos caras en tres cuartos, las sombras de cada lámpara salen de la misma geometría y el cilindro de la
// campana se ve redondo. Es una caja propia en vez de la `Estancia` de la biblioteca: la `Estancia` mira de frente a la
// pared del fondo, y aquí la vista es en diagonal.

/** La cámara del laboratorio: los ojos a [OJO] metros del suelo, en el origen de la sala, girados [ALFA] hacia la derecha. */
internal object CamLab {
    const val F = 300f
    const val CX = 190f
    const val CY = 120f
    const val OJO = 1.6f

    /** El punto del lienzo hacia donde se van las líneas hondas de la sala (las paralelas al pasillo). */
    val FUGA = Offset(95f, CY)
    private val ALFA = atan((CX - FUGA.x) / F)
    val CA = cos(ALFA)
    val SA = sin(ALFA)

    /** A qué distancia de los ojos (en el eje de la vista) está el punto ([x], [z]) de la sala. */
    fun hondo(x: Float, z: Float): Float = max(x * SA + z * CA, 0.12f)

    /** Un punto de la sala (x a la derecha, y hacia arriba desde el suelo, z hacia la pared del fondo, en metros) en el lienzo. */
    fun p(x: Float, y: Float, z: Float): Offset { val cx = x * CA - z * SA; val cz = hondo(x, z); return Offset(CX + F * cx / cz, CY - F * (y - OJO) / cz) }

    fun p(v: V3): Offset = p(v.x, v.y, v.z)

    /** Cuántas unidades del lienzo mide un metro en el punto ([x], [z]). */
    fun k(x: Float, z: Float): Float = F / hondo(x, z)

    fun k(v: V3): Float = k(v.x, v.z)

    /** El camino cerrado que une los [puntos] de la sala, ya en el lienzo. */
    fun forma(puntos: List<V3>): Path = poligono(puntos.map { p(it) })

    /** Si una cara que pasa por [a] con la normal [n] mira hacia los ojos. */
    fun seVe(a: V3, n: V3): Boolean = -n.x * a.x + n.y * (OJO - a.y) - n.z * a.z > 0f
}

internal fun V3.punto(o: V3): Float = x * o.x + y * o.y + z * o.z
internal fun V3.largo(): Float = sqrt(x * x + y * y + z * z)
internal fun V3.unidad(): V3 { val l = largo().coerceAtLeast(0.0001f); return V3(x / l, y / l, z / l) }

/** Lo que hay en la sala, de atrás adelante: su distancia a los ojos ([hondo]), los puntos que arrojan sombra ([esqueleto]) y cómo se pinta. */
internal class CosaLab(val hondo: Float, val esqueleto: List<V3>, val pintar: DrawScope.() -> Unit)

/** De qué es cada luz de la sala. */
internal enum class Fuente { CULTIVO, MESA, PANTALLA, PUERTA, CAMPANA, VELA, LINTERNA, AVISO, PANEL, REBOTE }

/**
 * Una luz de la sala, en metros: dónde está ([w]), su [color] y su [fuerza], a qué distancia su luz cae a la mitad
 * ([alcance]) y, si es una lámpara con pantalla que alumbra hacia abajo, el coseno del borde de su cono ([cono], 0 si
 * alumbra a todos lados). [tumba]: si deja sombras arrojadas.
 */
internal class Luz3(val de: Fuente, val w: V3, val color: Color, val fuerza: Float, val alcance: Float, val cono: Float = 0f, val tumba: Boolean = true)

/**
 * Lo que el cuadro necesita saber de un momento, calculado una vez: dónde está ella, qué hace, las luces encendidas (en
 * metros) y la luz de fondo. Cada cosa pide su color a [c] con su punto y la dirección de su cara: la luz de fondo (azul
 * verdosa de día, azul de noche, más clara en lo que mira arriba) más lo que le llega de cada luz, con su caída y su
 * ángulo. Así cada cuerpo tiene un lado de cada luz, sin pintarlos a mano.
 */
internal class VistaLab(val m: Momento) {
    val lugar: EscenaLaboratorio.Lugar = EscenaLaboratorio.lugar(m)
    val tarea: Tarea = EscenaLaboratorio.tarea(m)
    val noche: Boolean = EscenaLaboratorio.deNoche(m)
    val l: Luces = EscenaLaboratorio.luces(m)
    val general: Float = EscenaLaboratorio.general(m)
    val cultivo: FloatArray = FloatArray(EscenaLaboratorio.LAMPARAS) { EscenaLaboratorio.cultivo(m, it) }
    val cultivoNoche: Float = EscenaLaboratorio.cultivoDeNoche(m)
    val mesa: Float = EscenaLaboratorio.lamparaDeMesa(m)
    val violeta: Float = EscenaLaboratorio.violeta(m)
    val jardinBlanco: Boolean = EscenaLaboratorio.jardinEnBlanco(m)
    val campana: Float = EscenaLaboratorio.luzDeLaCampana(m)
    val roja: Boolean = EscenaLaboratorio.luzRoja(m)
    val pantalla: Float = EscenaLaboratorio.brilloDePantalla(m)
    val colorPantalla: Color = EscenaLaboratorio.colorDePantalla(m)
    val velas: Boolean = EscenaLaboratorio.velas(m)
    val linterna: Boolean = EscenaLaboratorio.linterna(m)

    /** El color de las lámparas de cultivo: de día, el blanco que imita el día; de noche, el violeta. */
    val colorCultivo: Color = if (noche) ColorLaboratorio.VIOLETA else ColorLaboratorio.LUZ_DIA

    /** Lo que da la puerta: el violeta de los jardines de noche; de día, su blanco lejano; con aviso, el rojo del pasillo. */
    val colorPuerta: Color = when {
        roja -> lerp(ColorLaboratorio.AVISO, ColorLaboratorio.VIOLETA, if (noche) 0.25f else 0f)
        noche && !jardinBlanco -> ColorLaboratorio.VIOLETA
        else -> lerp(ColorLaboratorio.LUZ_DIA, ColorLaboratorio.GRAMA, 0.12f)
    }

    val luces: List<Luz3> = LucesLab.de(this)

    /** Las luces que dejan sombra arrojada, con la fuerza que llega a la sala. */
    val tumban: List<Luz3> = luces.filter { it.tumba && it.fuerza > 0.08f }

    /**
     * El color [propio] de algo que está en [w] con la cara mirando a [n] (no hace falta que sea unitaria). [lejos] lo
     * lleva hacia la bruma (lo del pasillo y de los jardines).
     */
    fun c(w: V3, propio: Color, n: V3, lejos: Float = 0f): Color {
        val nn = n.unidad(); val amb = l.ambiente
        // La luz de fondo: lo que mira arriba recibe la de los paneles del techo; lo que mira abajo, la que rebota del suelo.
        val arriba = 0.8f + 0.2f * nn.y + 0.1f * general * nn.y.coerceAtLeast(0f)
        var r = propio.red * amb.red * arriba; var g = propio.green * amb.green * arriba; var b = propio.blue * amb.blue * arriba
        for (f in luces) {
            if (f.fuerza <= 0.01f) continue
            val d = f.w - w; val dist = d.largo().coerceAtLeast(0.05f); val u = d * (1f / dist)
            val lam = ((nn.punto(u) + 0.15f) / 1.15f).coerceAtLeast(0f)
            var k = f.fuerza * caida(dist, f.alcance) * lam
            if (f.cono > 0f) k *= ((-u.y - f.cono) / 0.16f).coerceIn(0f, 1f) + 0.06f
            if (k <= 0.002f) continue
            r += (propio.red * 0.9f + 0.05f) * f.color.red * k; g += (propio.green * 0.9f + 0.05f) * f.color.green * k; b += (propio.blue * 0.9f + 0.05f) * f.color.blue * k
        }
        val col = Color(r.coerceIn(0f, 1f), g.coerceIn(0f, 1f), b.coerceIn(0f, 1f), propio.alpha)
        return if (lejos > 0.01f) lerp(col, l.bruma.copy(alpha = propio.alpha), (lejos * l.brumaFuerza).coerceIn(0f, 0.9f)) else col
    }

    /** Cómo cae la luz con la distancia: a la mitad a [alcance] metros, y nada más allá de tres veces el alcance. */
    private fun caida(dist: Float, alcance: Float): Float { val q = dist / alcance; val corte = (1f - q * q / 9f).coerceAtLeast(0f); return corte * corte / (1f + q * q) }

    /** Cuánto alumbra la luz [f] el punto [w] de una cara que mira a [n] (sin el color), para los charcos y los brillos. */
    fun cuanto(f: Luz3, w: V3, n: V3): Float {
        val d = f.w - w; val dist = d.largo().coerceAtLeast(0.05f); val u = d * (1f / dist)
        val lam = n.unidad().punto(u).coerceAtLeast(0f)
        var k = f.fuerza * caida(dist, f.alcance) * lam
        if (f.cono > 0f) k *= ((-u.y - f.cono) / 0.16f).coerceIn(0f, 1f)
        return k
    }

    /** Las luces de la sala como focos del lienzo, para lo que se pinta con las piezas de `paisaje/` (un [Alumbrado]). */
    val focos: List<Foco> = luces.map { f -> Foco(CamLab.p(f.w), f.color, (f.fuerza * 0.6f).coerceIn(0f, 1.2f), f.alcance * CamLab.k(f.w), CamLab.p(f.w.x, 0f, f.w.z).y) }
    val a: Alumbrado = Alumbrado(l, focos)

    /**
     * El pincel de un sitio del lienzo, para lo que se pinta con `Pincel` (los objetos del día): su luz es la que llega ahí
     * a algo que mira arriba y a quien mira, y sus sombras las tumba la luz que más lo alumbra.
     */
    fun pincel(w: V3): Pincel {
        val luz = c(w, Color.White, V3(0f, 0.6f, -1f))
        val local = Luces(alto = l.alto, medio = l.medio, bajo = l.bajo, ambiente = luz, bruma = l.bruma, brumaFuerza = l.brumaFuerza,
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = 0f)
        val manda = luces.maxByOrNull { f -> cuanto(f, w, V3(0f, 1f, -0.4f)) }
        val lat = if (manda == null || manda.w.x >= w.x) 1f else -1f
        return Pincel(local, rayoFijo(0.7f * lat, 55f, 0.45f, ColorLaboratorio.SOMBRA), if (mesa > 0.2f) 0.25f * mesa else 0f, ColorLaboratorio.LAMPARA)
    }

    /** La luz que más alumbra el punto [w] (para la sombra del gato y de los objetos). */
    fun manda(w: V3): Luz3? = tumban.maxByOrNull { f -> cuanto(f, w, V3(0f, 1f, -0.3f)) }
}

// =====================================================================
//  Ayudas de dibujo en la sala
// =====================================================================

internal object PiezasLab {
    val ARRIBA = V3(0f, 1f, 0f)
    val FRENTE = V3(0f, 0f, -1f)
    val IZQ = V3(-1f, 0f, 0f)
    val DER = V3(1f, 0f, 0f)

    /** Una cara plana de la sala, con su color de luz en el centro. */
    fun DrawScope.cara(v: VistaLab, puntos: List<V3>, propio: Color, n: V3, lejos: Float = 0f) {
        val c = V3(puntos.sumOf { it.x.toDouble() }.toFloat() / puntos.size, puntos.sumOf { it.y.toDouble() }.toFloat() / puntos.size, puntos.sumOf { it.z.toDouble() }.toFloat() / puntos.size)
        drawPath(CamLab.forma(puntos), v.c(c, propio, n, lejos))
    }

    /**
     * Una cara grande (una pared, el suelo, el tablero), con el color de cada esquina mezclado a lo largo de su eje más
     * largo en el lienzo: así se ve cómo cambia la luz de un extremo a otro.
     */
    fun DrawScope.caraGrande(v: VistaLab, puntos: List<V3>, propio: Color, n: V3, lejos: Float = 0f, pasos: Int = 5, a: V3? = null, b: V3? = null) {
        val forma = CamLab.forma(puntos)
        val ini = a ?: puntos[0]; val fin = b ?: puntos[puntos.size / 2]
        val pi = CamLab.p(ini); val pf = CamLab.p(fin)
        val paradas = Array(pasos + 1) { i -> val t = i / pasos.toFloat(); t to v.c(ini + (fin - ini) * t, propio, n, lejos) }
        drawPath(forma, Brush.linearGradient(*paradas, start = pi, end = pf))
    }

    /** Una caja de x0..x1, y0..y1, z0..z1 (en la sala, sin girar), con las caras que se ven. [encima] pinta sobre cada cara (0 frente, 1 izquierda, 2 derecha, 3 arriba). */
    fun DrawScope.caja(v: VistaLab, x0: Float, x1: Float, y0: Float, y1: Float, z0: Float, z1: Float, propio: Color, lejos: Float = 0f, encima: (DrawScope.(Int, Path) -> Unit)? = null) {
        val caras = listOf(
            Triple(0, listOf(V3(x0, y0, z0), V3(x1, y0, z0), V3(x1, y1, z0), V3(x0, y1, z0)), FRENTE),
            Triple(1, listOf(V3(x0, y0, z1), V3(x0, y0, z0), V3(x0, y1, z0), V3(x0, y1, z1)), IZQ),
            Triple(2, listOf(V3(x1, y0, z0), V3(x1, y0, z1), V3(x1, y1, z1), V3(x1, y1, z0)), DER),
            Triple(3, listOf(V3(x0, y1, z0), V3(x1, y1, z0), V3(x1, y1, z1), V3(x0, y1, z1)), ARRIBA),
        )
        for ((i, ps, n) in caras) {
            if (!CamLab.seVe(ps[0], n)) continue
            val forma = CamLab.forma(ps)
            val c = V3((x0 + x1) / 2f + n.x * (x1 - x0) / 2f, (y0 + y1) / 2f + n.y * (y1 - y0) / 2f, (z0 + z1) / 2f + n.z * (z1 - z0) / 2f)
            drawPath(forma, v.c(c, propio, n, lejos))
            encima?.invoke(this, i, forma)
        }
    }

    /** Un marco girado en la sala: su origen [o] (en el suelo) y hacia dónde va su eje largo ([u], horizontal y unitario). */
    class Marco(val o: V3, val u: V3) {
        /** El eje de lado: a la izquierda del largo, mirando hacia donde va. */
        val v = V3(-u.z, 0f, u.x)
        /** Un punto del marco: [a] a lo largo, [b] de lado (hacia la izquierda), [y] de alto. */
        fun w(a: Float, b: Float, y: Float): V3 = V3(o.x + u.x * a + v.x * b, o.y + y, o.z + u.z * a + v.z * b)
        fun p(a: Float, b: Float, y: Float): Offset = CamLab.p(w(a, b, y))
        fun forma(puntos: List<Triple<Float, Float, Float>>): Path = poligono(puntos.map { p(it.first, it.second, it.third) })
        /** Una dirección del marco (a lo largo, de lado, arriba) en la sala. */
        fun n(a: Float, b: Float, y: Float): V3 = V3(u.x * a + v.x * b, y, u.z * a + v.z * b)
    }

    /** Una caja en los ejes del [mk]: de [a0] a [a1] a lo largo, de [b0] a [b1] de lado, de [y0] a [y1] de alto. */
    fun DrawScope.cajaEn(v: VistaLab, mk: Marco, a0: Float, a1: Float, b0: Float, b1: Float, y0: Float, y1: Float, propio: Color, encima: (DrawScope.(Int, Path) -> Unit)? = null) {
        val caras = listOf(
            Triple(0, listOf(Triple(a0, b0, y0), Triple(a0, b1, y0), Triple(a0, b1, y1), Triple(a0, b0, y1)), Triple(-1f, 0f, 0f)),   // el extremo de cerca
            Triple(1, listOf(Triple(a0, b1, y0), Triple(a1, b1, y0), Triple(a1, b1, y1), Triple(a0, b1, y1)), Triple(0f, 1f, 0f)),    // el costado izquierdo
            Triple(2, listOf(Triple(a0, b0, y0), Triple(a1, b0, y0), Triple(a1, b0, y1), Triple(a0, b0, y1)), Triple(0f, -1f, 0f)),   // el costado derecho
            Triple(3, listOf(Triple(a0, b0, y1), Triple(a1, b0, y1), Triple(a1, b1, y1), Triple(a0, b1, y1)), Triple(0f, 0f, 1f)),    // arriba
            Triple(4, listOf(Triple(a1, b0, y0), Triple(a1, b1, y0), Triple(a1, b1, y1), Triple(a1, b0, y1)), Triple(1f, 0f, 0f)),    // el extremo lejano
        )
        for ((i, ps, nl) in caras) {
            val n = mk.n(nl.first, nl.second, nl.third)
            val w0 = mk.w(ps[0].first, ps[0].second, ps[0].third)
            if (!CamLab.seVe(w0, n)) continue
            val forma = mk.forma(ps)
            val cm = mk.w(ps.sumOf { it.first.toDouble() }.toFloat() / 4f, ps.sumOf { it.second.toDouble() }.toFloat() / 4f, ps.sumOf { it.third.toDouble() }.toFloat() / 4f)
            drawPath(forma, v.c(cm, propio, n))
            encima?.invoke(this, i, forma)
        }
    }

    /**
     * Un cilindro de pie, de radio [r], con su base en [c] y su altura [alto]: el costado con el color de cada lado (cada
     * luz lo alumbra por el suyo) y, si se ve, la tapa.
     */
    fun DrawScope.cilindro(v: VistaLab, c: V3, r: Float, alto: Float, propio: Color, tapa: Color? = propio, n: Int = 8) = tronco(v, c, r, r, alto, propio, tapa, n)

    /**
     * Un tronco de cono de pie (una maceta, un cubo): de radio [rAbajo] en su base [c] a [rArriba] arriba. Como [cilindro];
     * si la boca queda por debajo de los ojos se ve su [tapa] (o el hueco, si se le pasa un color oscuro).
     */
    fun DrawScope.tronco(v: VistaLab, c: V3, rAbajo: Float, rArriba: Float, alto: Float, propio: Color, tapa: Color? = propio, n: Int = 8) {
        val r = (rAbajo + rArriba) / 2f
        val arriba = V3(c.x, c.y + alto, c.z)
        // El borde de arriba que se ve: el de delante si la tapa queda por encima de los ojos; si no, el de detrás (y la tapa encima).
        val sobreLosOjos = arriba.y > CamLab.OJO
        val bordeArriba = mitad(arriba, rArriba, cerca = sobreLosOjos).map { CamLab.p(it) }
        val bordeAbajo = mitad(c, rAbajo, cerca = true).map { CamLab.p(it) }
        val cuerpo = Path().apply {
            moveTo(bordeArriba.first().x, bordeArriba.first().y)
            for (q in bordeArriba) lineTo(q.x, q.y)
            for (q in bordeAbajo.reversed()) lineTo(q.x, q.y)
            close()
        }
        // El color de cada vertical del costado, de un borde al otro por delante: su cara mira hacia fuera del eje.
        val frente = mitad(V3(c.x, c.y + alto * 0.5f, c.z), r, cerca = true, n = n)
        val xs = frente.map { CamLab.p(it).x }; val x0 = xs.first(); val x1 = xs.last()
        var ultimo = -1f
        val paradas = frente.mapIndexed { i, q ->
            val t = ((xs[i] - x0) / (x1 - x0).coerceAtLeast(0.01f)).coerceIn(0f, 1f).coerceAtLeast(ultimo + 0.0001f).coerceAtMost(1f); ultimo = t
            t to v.c(q, propio, V3(q.x - c.x, 0f, q.z - c.z))
        }.toTypedArray()
        drawPath(cuerpo, Brush.horizontalGradient(*paradas, startX = x0, endX = x1))
        if (tapa != null && !sobreLosOjos) drawPath(poligono(aro(arriba, rArriba).map { CamLab.p(it) }), v.c(arriba, tapa, ARRIBA))
    }

    /**
     * La mitad de un círculo horizontal (centro [c], radio [r]) que mira a los ojos ([cerca]) o la de detrás, de su borde
     * izquierdo a su borde derecho tal como se ven en el lienzo.
     */
    fun mitad(c: V3, r: Float, cerca: Boolean, n: Int = 16): List<V3> {
        val a0 = kotlin.math.atan2(-c.z, -c.x)                                  // hacia los ojos
        val centro = if (cerca) a0 else a0 + Math.PI.toFloat()
        val ps = (0..n).map { i -> val a = centro - (Math.PI / 2).toFloat() + Math.PI.toFloat() * i / n; V3(c.x + r * cos(a), c.y, c.z + r * sin(a)) }
        return if (CamLab.p(ps.first()).x <= CamLab.p(ps.last()).x) ps else ps.reversed()
    }

    /** Los puntos de un círculo horizontal de radio [r] alrededor de [c]. */
    fun aro(c: V3, r: Float, n: Int = 24): List<V3> = (0 until n).map { i -> val a = i * 2f * Math.PI.toFloat() / n; V3(c.x + r * cos(a), c.y, c.z + r * sin(a)) }

    /** Un palo redondo de [a] a [b] (en la sala), de [grueso] metros, con un filo de luz. */
    fun DrawScope.palo(v: VistaLab, a: V3, b: V3, grueso: Float, propio: Color, filo: Boolean = true) {
        val pa = CamLab.p(a); val pb = CamLab.p(b); val medio = (a + b) * 0.5f; val g = grueso * CamLab.k(medio)
        val d = pb - pa; val l = d.getDistance().coerceAtLeast(0.01f); val lado = Offset(-d.y / l, d.x / l)
        val nIzq = V3(-1f, 0.3f, -0.6f); val nDer = V3(1f, 0.3f, -0.6f)
        drawLine(v.c(medio, propio, FRENTE), pa, pb, g.coerceAtLeast(0.3f), StrokeCap.Butt)
        if (filo && g > 0.8f) {
            drawLine(v.c(medio, lerp(propio, Color.White, 0.3f), if (lado.x < 0f) nIzq else nDer).copy(alpha = 0.8f), pa + lado * (g * 0.28f), pb + lado * (g * 0.28f), (g * 0.3f).coerceAtLeast(0.3f), StrokeCap.Butt)
            drawLine(v.c(medio, lerp(propio, ColorLaboratorio.NEGRO, 0.3f), if (lado.x < 0f) nDer else nIzq).copy(alpha = 0.7f), pa - lado * (g * 0.3f), pb - lado * (g * 0.3f), (g * 0.3f).coerceAtLeast(0.3f), StrokeCap.Butt)
        }
    }

    /**
     * El charco de luz de [color] que deja una lámpara sobre un plano horizontal a la altura [y]: un óvalo de radio [r]
     * metros alrededor de [c], que se desvanece hacia el borde, sumado a lo que hay debajo.
     */
    fun DrawScope.charco(c: V3, r: Float, color: Color, fuerza: Float, duro: Float = 0.5f) {
        if (fuerza <= 0.01f || r <= 0f) return
        val p = CamLab.p(c); val cz = CamLab.hondo(c.x, c.z)
        val rx = CamLab.F * r / cz; val ry = (CamLab.F * r * abs(CamLab.OJO - c.y) / (cz * cz)).coerceAtLeast(rx * 0.06f)
        val k = fuerza.coerceIn(0f, 1.4f)
        scale(rx / ry, 1f, p) {
            drawCircle(Brush.radialGradient(0f to color.copy(alpha = (0.5f * k).coerceIn(0f, 1f)), duro to color.copy(alpha = (0.4f * k).coerceIn(0f, 1f)), 1f to Color.Transparent, center = p, radius = ry), ry, p, blendMode = BlendMode.Plus)
        }
    }

    /**
     * Un halo de luz de [color] en una pared vertical: un óvalo de [ancho] por [alto] metros, centrado en [c], visto en
     * su sitio (algo aplastado por la fuga), sumado a lo que hay debajo.
     */
    fun DrawScope.halo(c: V3, ancho: Float, alto: Float, color: Color, fuerza: Float) {
        if (fuerza <= 0.01f) return
        val p = CamLab.p(c); val k = CamLab.k(c)
        val rx = ancho * k * 0.5f; val ry = alto * k * 0.5f
        scale(rx / ry, 1f, p) {
            drawCircle(Brush.radialGradient(0f to color.copy(alpha = (0.42f * fuerza).coerceIn(0f, 1f)), 0.45f to color.copy(alpha = (0.18f * fuerza).coerceIn(0f, 1f)), 1f to Color.Transparent, center = p, radius = ry), ry, p, blendMode = BlendMode.Plus)
        }
    }

    /** La sombra que arroja sobre el plano de altura [plano] lo que tiene los puntos [esqueleto], con la luz en [luz]. */
    fun sombraEnPlano(esqueleto: List<V3>, luz: V3, plano: Float): Path? {
        val ps = esqueleto.mapNotNull { q ->
            val alto = luz.y - plano; val sobre = q.y - plano
            if (alto <= 0.05f || sobre >= alto - 0.05f) return@mapNotNull null
            val k = (alto / (alto - sobre)).coerceAtMost(6f)
            CamLab.p(luz.x + (q.x - luz.x) * k, plano, luz.z + (q.z - luz.z) * k)
        }
        if (ps.size < 3) return null
        return poligono(envolvente(ps))
    }

    /** Un rectángulo del lienzo con [color] (para los velos de toda la sala). */
    fun DrawScope.velo(color: Color, blend: BlendMode = BlendMode.SrcOver) = drawRect(color, Offset.Zero, Size(io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO, io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO), blendMode = blend)
}
