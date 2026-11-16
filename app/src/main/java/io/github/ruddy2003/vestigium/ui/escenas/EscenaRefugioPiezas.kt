package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.Compuerta
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Desde dónde se mira el cuadro de Refugio: de pie en la sala de bombas (los ojos a 1.6 m del suelo), mirando 20° a la
 * izquierda del eje del pasillo grande. Así las líneas del pasillo (el suelo, los tubos del techo, la fila de máquinas)
 * se van hacia la Puerta, a la derecha (la diagonal y la contradiagonal), y la pared del fondo de la sala, la de los
 * manómetros, se aleja despacio hacia la izquierda.
 *
 * Las medidas van en metros: x hacia la derecha, y hacia arriba desde el suelo, z a lo largo del pasillo (hacia la
 * Puerta). Los ojos están en (0, [OJO], 0).
 */
internal object CamRefugio {
    /** La focal, en unidades del lienzo. */
    const val F = 300f
    const val CX = 190f
    const val CY = 118f
    const val OJO = 1.6f
    private const val GIRO = 0.3490659f                                        // 20°
    val COS = cos(GIRO)
    val SIN = sin(GIRO)
    /** Lo más cerca que se dibuja: lo que queda más acá se recorta. */
    const val CERCA = 0.5f

    /** A qué distancia de los ojos está el punto (a lo largo de la mirada). */
    fun hondo(x: Float, z: Float): Float = -x * SIN + z * COS
    fun hondo(w: V3): Float = hondo(w.x, w.z)

    fun p(x: Float, y: Float, z: Float): Offset {
        val zc = max(hondo(x, z), 0.05f); val xc = x * COS + z * SIN
        return Offset(CX + F * xc / zc, CY - F * (y - OJO) / zc)
    }

    fun p(w: V3): Offset = p(w.x, w.y, w.z)

    /** Cuántas unidades del lienzo mide un metro en [w]. */
    fun k(w: V3): Float = F / max(hondo(w), 0.05f)

    /** Si una cara que pasa por [a] con la normal [n] mira hacia los ojos. */
    fun seVe(a: V3, n: V3): Boolean = n.x * -a.x + n.y * (OJO - a.y) + n.z * -a.z > 0f

    /** El polígono [pts] recortado a lo que está delante de [CERCA] (Sutherland–Hodgman con un solo plano). */
    fun recorta(pts: List<V3>): List<V3> {
        if (pts.none { hondo(it) < CERCA }) return pts
        val r = ArrayList<V3>()
        for (i in pts.indices) {
            val a = pts[i]; val b = pts[(i + 1) % pts.size]
            val ha = hondo(a) - CERCA; val hb = hondo(b) - CERCA
            if (ha >= 0f) r += a
            if ((ha >= 0f) != (hb >= 0f)) { val k = ha / (ha - hb); r += V3(a.x + (b.x - a.x) * k, a.y + (b.y - a.y) * k, a.z + (b.z - a.z) * k) }
        }
        return r
    }

    /** El camino cerrado de los puntos [pts] en el lienzo (vacío si quedan detrás de los ojos). */
    fun forma(pts: List<V3>): Path { val r = recorta(pts); return if (r.size < 3) Path() else poligono(r.map { p(it) }) }

    // ---- Cuentas de vectores.
    fun punto(a: V3, b: V3): Float = a.x * b.x + a.y * b.y + a.z * b.z
    fun cruz(a: V3, b: V3): V3 = V3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x)
    fun largo(a: V3): Float = sqrt(punto(a, a))
    fun unidad(a: V3): V3 { val l = largo(a).coerceAtLeast(1e-5f); return V3(a.x / l, a.y / l, a.z / l) }
    fun medio(a: V3, b: V3): V3 = V3((a.x + b.x) / 2f, (a.y + b.y) / 2f, (a.z + b.z) / 2f)
    fun centro(pts: List<V3>): V3 { var x = 0f; var y = 0f; var z = 0f; for (q in pts) { x += q.x; y += q.y; z += q.z }; val n = pts.size.coerceAtLeast(1); return V3(x / n, y / n, z / n) }

    val ARRIBA = V3(0f, 1f, 0f)
    val ABAJO = V3(0f, -1f, 0f)
    val FRENTE = V3(0f, 0f, -1f)
    val FONDO = V3(0f, 0f, 1f)
    val IZQ = V3(-1f, 0f, 0f)
    val DER = V3(1f, 0f, 0f)
    val NADA = V3(0f, 0f, 0f)

    /** La envolvente convexa de unos puntos del lienzo (para las siluetas y las sombras). */
    fun casco(ps: List<Offset>): List<Offset> {
        if (ps.size < 3) return ps
        val o = ps.sortedWith(compareBy({ it.x }, { it.y }))
        fun giro(a: Offset, b: Offset, c: Offset) = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)
        val abajo = ArrayList<Offset>(); for (q in o) { while (abajo.size >= 2 && giro(abajo[abajo.size - 2], abajo.last(), q) <= 0f) abajo.removeAt(abajo.size - 1); abajo += q }
        val arriba = ArrayList<Offset>(); for (q in o.asReversed()) { while (arriba.size >= 2 && giro(arriba[arriba.size - 2], arriba.last(), q) <= 0f) arriba.removeAt(arriba.size - 1); arriba += q }
        return abajo.dropLast(1) + arriba.dropLast(1)
    }

    /** Los puntos de un círculo de radio [r] alrededor de [c], en el plano de normal [n]. */
    fun circulo(c: V3, n: V3, r: Float, lados: Int = 20, a0: Float = 0f, a1: Float = (2 * PI).toFloat()): List<V3> {
        val e = unidad(n)
        val u = unidad(if (abs(e.y) > 0.9f) cruz(e, DER) else cruz(e, ARRIBA)); val w = cruz(e, u)
        return (0..lados).map { i -> val a = a0 + (a1 - a0) * i / lados; V3(c.x + (u.x * cos(a) + w.x * sin(a)) * r, c.y + (u.y * cos(a) + w.y * sin(a)) * r, c.z + (u.z * cos(a) + w.z * sin(a)) * r) }
    }
}

/**
 * Una luz del cuadro de Refugio, en metros: [p] dónde está, [color] y [fuerza] lo que da, [alcance] a qué distancia su
 * luz cae a la mitad. [cono]: si alumbra solo hacia un lado, en un cono alrededor de esa dirección (la lámpara de trabajo
 * con su pantallita), con el borde entre [coseFuera] y [coseDentro]. [tumba]: si arroja sombras. [enSala] y [enPasillo]: qué alumbra (el portón
 * hace de pantalla: los tubos del pasillo no alumbran dentro de la sala, por eso la sala recibe su luz por el portón,
 * como una sola luz grande).
 */
internal class FocoRefugio(
    val p: V3, val color: Color, val fuerza: Float, val alcance: Float,
    val cono: V3? = null, val coseFuera: Float = 0f, val coseDentro: Float = 0f, val tumba: Boolean = false,
    val enSala: Boolean = true, val enPasillo: Boolean = true,
)

/**
 * Una cosa del cuadro que se pinta en su turno: [hondo] es su distancia a los ojos (se pintan de la más lejana a la más
 * cercana), [esqueleto] los puntos de su cuerpo que arrojan sombra (su envolvente), y [pintar] la dibuja.
 */
internal class CosaRefugio(val hondo: Float, val esqueleto: List<V3>, val pintar: DrawScope.() -> Unit)

/**
 * Lo que el cuadro necesita saber de un momento, calculado una vez: el estado, qué está encendido, dónde cuelgan las
 * lámparas, la Puerta y lo de fuera, y la luz de cada punto de la sala y del pasillo ([c]).
 */
internal class VistaRefugio(val m: Momento) {
    private val R = EscenaRefugio
    private val C = ColorRefugio

    val estado: EscenaRefugio.Estado = R.estado(m)
    val dia: Boolean = R.deDia(m)
    val noche: Float = R.nocheAzul(m)
    val obras: List<EscenaRefugio.Obra> = R.lamparas(m)
    val l: Luces = R.luces(m)
    val alarma: Boolean = R.alarma(m)
    val compuerta: Compuerta = R.compuerta(m)
    val puerta: Puerta = R.puerta(m)
    val tanques: Float = R.tanques(m)
    val cultivo: Float = R.cultivo(m)
    val reserva: Float = R.reserva(m)
    val solFalso: Boolean = R.solFalso(m)
    val guardia: Float = R.guardia(m)
    /** El cielo de fuera, si la Puerta deja verlo. */
    val fuera: Luces? = if (puerta != Puerta.CERRADA) R.cieloDeFuera(m) else null
    val sol: EscenaRefugio.SolDePuerta? = R.solPorLaPuerta(m)
    val fuego: Float = R.fuego(m)

    /** El color de los tubos del pasillo: blanco frío de día, azul de noche, cálido el día del sol falso. */
    val colorTubo: Color = when { solFalso -> lerp(C.TUBO, C.LAMPARA, 0.5f); dia -> C.TUBO; else -> C.PISCINA }
    /** Cuánto dan los tubos del pasillo. */
    val fuerzaTubo: Float = when { solFalso -> 1.1f; dia -> 0.62f; else -> 0.3f }

    /**
     * La penumbra de la sala, la luz que no viene de ningún foco: verde azulada honda de día (nunca gris), azul de noche.
     * El día del sol falso, la sala se llena del blanco caliente que entra por el portón.
     */
    val ambienteSala: Color = when {
        solFalso -> Color(0xFF8E8C88)
        dia -> Color(0xFF6E7A9C)
        else -> Color(0xFF2E3A66)
    }

    /** La del pasillo: el blanco parejo de los tubos de día; de noche, el azul «del fondo de una piscina». */
    val ambientePasillo: Color = when {
        solFalso -> Color(0xFFD8C7A8)
        dia -> Color(0xFF78858A)
        else -> Color(0xFF2E4C78)
    }

    /** Dónde está el sol que entra por la Puerta (su rebote en el suelo del pasillo), o null. */
    val manchaDeSol: V3? = sol?.let { PasilloRefugio.centroDeLaMancha(it) }

    val focos: List<FocoRefugio> = buildList {
        val sala = SalaRefugio
        // ---- Las lámparas de trabajo: cálidas, hacia abajo, con borde.
        for (o in obras) add(FocoRefugio(sala.bombilla(o), C.LAMPARA, 1.35f, 2.4f, cono = sala.haciaDonde(o), coseFuera = 0.5f, coseDentro = 0.8f, tumba = true))
        // ---- La luz del pasillo que entra por el portón: una sola luz grande, fría (o azul, o cálida el día del sol falso).
        val porton = V3(PasilloRefugio.CENTRO_X, 2.1f, SalaRefugio.FONDO + 1.4f)
        add(FocoRefugio(porton, colorTubo, if (solFalso) 1.1f else if (dia) 0.62f else 0.4f, if (dia) 7f else 6f, tumba = true, enPasillo = false))
        // ---- Los tubos del techo del pasillo, uno por tramo entre costillas.
        for (z in PasilloRefugio.TUBOS) add(FocoRefugio(V3(PasilloRefugio.CENTRO_X, PasilloRefugio.BOVEDA - 0.25f, z), colorTubo, fuerzaTubo, 2.8f, enSala = false))
        // ---- La lucecita de guardia de la sala, sobre el cuadro de mando.
        add(FocoRefugio(sala.GUARDIA, C.GUARDIA, 0.9f * guardia, 2.2f, tumba = !dia, enPasillo = false))
        // ---- Los tanques de cultivo, por el ventanal del pasillo: violeta.
        val ventanal = V3(PasilloRefugio.IZQ + 0.3f, 1.7f, (PasilloRefugio.VENTANAL_Z0 + PasilloRefugio.VENTANAL_Z1) / 2f)
        add(FocoRefugio(ventanal, C.VIOLETA, 0.25f + 0.75f * tanques * (0.5f + 0.5f * noche), 4.2f, enSala = false))
        add(FocoRefugio(V3(PasilloRefugio.IZQ + 0.3f, 1.4f, PasilloRefugio.VENTANAL_Z0 + 0.8f), C.VIOLETA, 0.4f * tanques * noche, 2.6f, enSala = false))
        // ---- La alarma: una baliza roja sobre el portón y otra en el pasillo.
        if (alarma) {
            add(FocoRefugio(sala.BALIZA, C.ALARMA, 0.85f, 3.6f))
            add(FocoRefugio(V3(PasilloRefugio.DER - 0.3f, 3.0f, 20f), C.ALARMA, 0.7f, 4f, enSala = false))
        }
        // ---- La compuerta bajada: su luz roja de aviso.
        if (compuerta == Compuerta.BAJADA) add(FocoRefugio(PasilloRefugio.LUZ_COMPUERTA, C.ALARMA, 0.75f, 3.2f, enSala = false))
        // ---- La Puerta: lo que entra por ella.
        fuera?.let { f ->
            val abre = if (puerta == Puerta.ABIERTA) 1f else 0.18f
            val cielo = lerp(lerp(f.medio, Color.White, 0.35f), f.bajo, 0.3f)
            val claridad = (1f - f.noche).coerceIn(0f, 1f)
            add(FocoRefugio(V3(PasilloRefugio.CENTRO_X, 2.2f, PasilloRefugio.PUERTA_Z - 1.0f), cielo, 1.1f * abre * claridad, 6f, enSala = false))
            if (puerta == Puerta.ABIERTA) add(FocoRefugio(V3(PasilloRefugio.CENTRO_X, 1.5f, SalaRefugio.FONDO + 1f), cielo, 0.22f * claridad, 5f, enPasillo = false))
        }
        manchaDeSol?.let { s -> sol?.let { add(FocoRefugio(V3(s.x, 0.6f, s.z), it.color, 0.9f * it.fuerza, 4.5f, enSala = false)) } }
        if (fuego > 0.02f) add(FocoRefugio(V3(PasilloRefugio.CENTRO_X, 0.7f, PasilloRefugio.PUERTA_Z + 2.5f), C.FUEGO, 1.6f * fuego, 9f, enSala = false))
        // ---- Las lucecitas del árbol de Nochebuena.
        if (R.lucesDelArbol(m)) add(FocoRefugio(PasilloRefugio.ARBOL + V3(0f, 1.1f, 0f), lerp(C.GUARDIA, C.ROJO, 0.3f), 0.5f, 1.8f))
        // ---- La radio encendida del día de la señal.
        if (R.senal(m)) add(FocoRefugio(SalaRefugio.RADIO, lerp(C.ALGA, C.TUBO, 0.5f), 0.25f, 0.6f, enPasillo = false))
    }

    /** Los focos que tumban sombras en la sala. */
    val tumban: List<FocoRefugio> = focos.filter { it.tumba && it.fuerza > 0.1f }

    /**
     * El color del [propio] de algo que está en [w], con su cara mirando a [n] (unitaria; [CamRefugio.NADA] si recibe la
     * luz de todos lados), en la sala o en el pasillo ([enPasillo]): la penumbra de color más lo que le llega de cada foco,
     * según la distancia, el ángulo y, para la lámpara, su cono.
     */
    fun c(w: V3, n: V3, propio: Color, enPasillo: Boolean = false): Color {
        val amb = if (enPasillo) ambientePasillo else ambienteSala
        var r = propio.red * amb.red; var g = propio.green * amb.green; var b = propio.blue * amb.blue
        val conCara = n.x != 0f || n.y != 0f || n.z != 0f
        for (f in focos) {
            if (if (enPasillo) !f.enPasillo else !f.enSala) continue
            if (f.fuerza <= 0.01f) continue
            val dx = f.p.x - w.x; val dy = f.p.y - w.y; val dz = f.p.z - w.z
            val d2 = dx * dx + dy * dy + dz * dz + 0.02f; val d = sqrt(d2)
            var k = f.fuerza / (1f + d2 / (f.alcance * f.alcance))
            if (conCara) k *= (((n.x * dx + n.y * dy + n.z * dz) / d + 0.22f) / 1.22f).coerceIn(0f, 1f)
            f.cono?.let { e -> k *= ((-(dx * e.x + dy * e.y + dz * e.z) / d - f.coseFuera) / (f.coseDentro - f.coseFuera)).coerceIn(0f, 1f).let { it * it * (3f - 2f * it) } }
            if (k <= 0.002f) continue
            r += (propio.red * 0.9f + 0.05f) * f.color.red * k; g += (propio.green * 0.9f + 0.05f) * f.color.green * k; b += (propio.blue * 0.9f + 0.05f) * f.color.blue * k
        }
        return Color(r.coerceIn(0f, 1f), g.coerceIn(0f, 1f), b.coerceIn(0f, 1f), propio.alpha)
    }

    /** Cuánta luz cálida de lámpara le llega a [w] (de 0 a 1): para encender lo pintado y los filos de lo oscuro. */
    fun deLampara(w: V3): Float {
        var t = 0f
        for (f in focos) { val e = f.cono ?: continue
            val dx = f.p.x - w.x; val dy = f.p.y - w.y; val dz = f.p.z - w.z; val d2 = dx * dx + dy * dy + dz * dz + 0.02f; val d = sqrt(d2)
            t += f.fuerza / (1f + d2 / (f.alcance * f.alcance)) * ((-(dx * e.x + dy * e.y + dz * e.z) / d - f.coseFuera) / (f.coseDentro - f.coseFuera)).coerceIn(0f, 1f)
        }
        return t.coerceIn(0f, 1f)
    }

    /** El pincel de un sitio del cuadro, para lo que se pinta con `Pincel` (los objetos del día): su luz y su sombra. */
    fun pincel(w: V3): Pincel {
        val luz = c(w, CamRefugio.NADA, Color(0xFFE8E8E8))
        val local = Luces(alto = l.alto, medio = l.medio, bajo = l.bajo, ambiente = luz, bruma = l.bruma, brumaFuerza = l.brumaFuerza,
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = 0f)
        val manda = tumban.maxByOrNull { f -> val d = CamRefugio.largo(f.p - w); f.fuerza / (1f + d * d / (f.alcance * f.alcance)) }
        val lat = if (manda == null || CamRefugio.p(manda.p).x >= CamRefugio.p(w).x) 1f else -1f
        return Pincel(local, rayoFijo(0.7f * lat, 52f, 0.45f, ColorRefugio.SOMBRA), 0f, ColorRefugio.LAMPARA)
    }
}

/**
 * Las primitivas del cuadro de Refugio: caras planas, cajas, cilindros, discos y barras en metros, cada una con su luz
 * ([VistaRefugio.c]). Las piezas de la sala y del pasillo se arman con ellas.
 */
internal object Trazo3 {
    private val K = CamRefugio

    /** La cara plana [pts] con normal [n], de un color: el de la luz en su centro. */
    fun DrawScope.cara(v: VistaRefugio, pts: List<V3>, n: V3, propio: Color, enPasillo: Boolean = false, alfa: Float = 1f) {
        val f = K.forma(pts); if (f.isEmpty) return
        drawPath(f, v.c(K.centro(pts), n, propio, enPasillo).copy(alpha = alfa))
    }

    /** La cara plana [pts] con un degradado entre la luz de [a] y la de [b] (dos puntos de la cara): así se ve cómo cae la luz. */
    fun DrawScope.caraDegradada(v: VistaRefugio, pts: List<V3>, n: V3, propio: Color, a: V3, b: V3, enPasillo: Boolean = false, pasos: Int = 3) {
        val f = K.forma(pts); if (f.isEmpty) return
        val pa = K.p(a); val pb = K.p(b)
        if ((pa - pb).getDistance() < 1f) { drawPath(f, v.c(K.centro(pts), n, propio, enPasillo)); return }
        val paradas = Array(pasos + 1) { i -> val t = i / pasos.toFloat(); t to v.c(V3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t), n, propio, enPasillo) }
        drawPath(f, Brush.linearGradient(*paradas, start = pa, end = pb))
    }

    /**
     * Una caja de [x0] a [x1], de [y0] a [y1] y de [z0] a [z1]: pinta las caras que se ven, de la más lejana a la más
     * cercana, cada una con su luz (degradada de arriba abajo en las de los lados). [arriba] es el color de la tapa.
     */
    fun DrawScope.caja(v: VistaRefugio, x0: Float, x1: Float, y0: Float, y1: Float, z0: Float, z1: Float, propio: Color, arriba: Color = propio, enPasillo: Boolean = false) {
        val caras = listOf(
            Triple(listOf(V3(x0, y1, z0), V3(x1, y1, z0), V3(x1, y1, z1), V3(x0, y1, z1)), K.ARRIBA, arriba),
            Triple(listOf(V3(x0, y0, z0), V3(x1, y0, z0), V3(x1, y1, z0), V3(x0, y1, z0)), K.FRENTE, propio),
            Triple(listOf(V3(x1, y0, z0), V3(x1, y0, z1), V3(x1, y1, z1), V3(x1, y1, z0)), K.DER, propio),
            Triple(listOf(V3(x0, y0, z1), V3(x0, y0, z0), V3(x0, y1, z0), V3(x0, y1, z1)), K.IZQ, propio),
            Triple(listOf(V3(x0, y0, z1), V3(x1, y0, z1), V3(x1, y1, z1), V3(x0, y1, z1)), K.FONDO, propio),
            Triple(listOf(V3(x0, y0, z0), V3(x1, y0, z0), V3(x1, y0, z1), V3(x0, y0, z1)), K.ABAJO, propio),
        )
        for ((pts, n, col) in caras.filter { K.seVe(K.centro(it.first), it.second) }.sortedByDescending { K.hondo(K.centro(it.first)) }) {
            if (n.y == 0f) { val c = K.centro(pts); caraDegradada(v, pts, n, col, V3(c.x, y1, c.z), V3(c.x, y0, c.z), enPasillo) }
            else cara(v, pts, n, col, enPasillo)
        }
    }

    /**
     * Un cilindro de radio [r] con el eje de [a] a [b]: sus tiras de lado que se ven, cada una con la luz de su cara (así
     * queda su lado de luz y su lado de sombra), y la tapa que se ve. [lados]: cuántas tiras.
     */
    fun DrawScope.cilindro(v: VistaRefugio, a: V3, b: V3, r: Float, propio: Color, tapa: Color = propio, lados: Int = 18, enPasillo: Boolean = false, tapas: Boolean = true) {
        val eje = K.unidad(b - a)
        val u = K.unidad(if (abs(eje.y) > 0.9f) K.cruz(eje, K.DER) else K.cruz(eje, K.ARRIBA)); val w = K.cruz(eje, u)
        fun dir(i: Int): V3 { val t = 2f * PI.toFloat() * i / lados; return V3(u.x * cos(t) + w.x * sin(t), u.y * cos(t) + w.y * sin(t), u.z * cos(t) + w.z * sin(t)) }
        val dirs = (0..lados).map { dir(it) }
        // La silueta entera primero, del color medio, para que no queden rendijas entre las tiras.
        val todos = dirs.flatMap { listOf(K.p(a + it * r), K.p(b + it * r)) }
        val medio = v.c(K.medio(a, b), K.NADA, propio, enPasillo)
        if (K.hondo(a) > K.CERCA && K.hondo(b) > K.CERCA) drawPath(poligono(K.casco(todos)), medio)
        val tiras = (0 until lados).mapNotNull { i ->
            val n = K.unidad(dirs[i] + dirs[i + 1]); val c = K.medio(a, b) + n * r
            if (K.seVe(c, n)) Triple(i, n, K.hondo(c)) else null
        }.sortedByDescending { it.third }
        for ((i, n, _) in tiras) {
            val pts = listOf(a + dirs[i] * r, a + dirs[i + 1] * r, b + dirs[i + 1] * r, b + dirs[i] * r)
            caraDegradada(v, pts, n, propio, a + n * r, b + n * r, enPasillo, 2)
        }
        if (tapas) {
            if (K.seVe(b, eje)) cara(v, dirs.map { b + it * r }, eje, tapa, enPasillo)
            if (K.seVe(a, eje * -1f)) cara(v, dirs.map { a + it * r }, eje * -1f, tapa, enPasillo)
        }
    }

    /** Un disco plano de radio [r] en [c], mirando a [n]. No pinta nada si da la espalda. */
    fun DrawScope.disco(v: VistaRefugio, c: V3, n: V3, r: Float, propio: Color, enPasillo: Boolean = false, lados: Int = 20, luz: Boolean = true) {
        if (!K.seVe(c, n)) return
        val f = K.forma(K.circulo(c, n, r, lados)); if (f.isEmpty) return
        drawPath(f, if (luz) v.c(c, n, propio, enPasillo) else propio)
    }

    /** Un aro (el canto de una rueda, de un manómetro) de radio [r] y grueso [grueso] (en metros), con su color. */
    fun DrawScope.aro(c: V3, n: V3, r: Float, grueso: Float, color: Color, lados: Int = 22) {
        val pts = K.circulo(c, n, r, lados).map { K.p(it) }
        val camino = Path().apply { moveTo(pts[0].x, pts[0].y); for (i in 1 until pts.size) lineTo(pts[i].x, pts[i].y); close() }
        drawPath(camino, color, style = Stroke(grueso * K.k(c), cap = StrokeCap.Round))
    }

    /** Una barra (un cable, un radio, una varilla) de [a] a [b], de [grueso] metros. */
    fun DrawScope.barra(a: V3, b: V3, grueso: Float, color: Color, punta: StrokeCap = StrokeCap.Round) {
        if (K.hondo(a) < K.CERCA || K.hondo(b) < K.CERCA) return
        drawLine(color, K.p(a), K.p(b), (grueso * K.k(K.medio(a, b))).coerceAtLeast(0.35f), punta)
    }

    /** Un cable que cuelga en comba de [a] a [b], con [caida] metros de flecha en el medio. */
    fun DrawScope.comba(a: V3, b: V3, caida: Float, grueso: Float, color: Color, pasos: Int = 12) {
        val pts = (0..pasos).map { i -> val t = i / pasos.toFloat(); V3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t - caida * 4f * t * (1f - t), a.z + (b.z - a.z) * t) }
        val q = pts.map { K.p(it) }
        val camino = Path().apply { moveTo(q[0].x, q[0].y); for (i in 1 until q.size) lineTo(q[i].x, q[i].y) }
        drawPath(camino, color, style = Stroke((grueso * K.k(K.medio(a, b))).coerceAtLeast(0.4f), cap = StrokeCap.Round))
    }
}
