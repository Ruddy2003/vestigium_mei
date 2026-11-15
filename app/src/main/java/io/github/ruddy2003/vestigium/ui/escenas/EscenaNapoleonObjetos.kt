package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los doce objetos del día de Jean-Baptiste, iguales en los dos cuadros (la retirada y el vivac).
 * Cada cuadro dice dónde: los chicos van sobre su soporte (la mochila de pie del alto, junto al pabellón);
 * los grandes van de pie o apoyados (en los fusiles en pabellón).
 *
 * - **Grandes** ([GRANDES], de 0.4 a 1.6 m, a [METRO] unidades por metro a escala 1; a la escala 1.1 de la retirada dan
 *   las 81 unidades por metro de las cosas del alto, así que el fusil del día mide lo mismo que los del pabellón):
 *   el fusil, plantado de culata en la nieve, con su portafusil blanco suelto y un trapo atado a la llave; el capote y la
 *   manta, colgados a secar de una horquilla de pino clavada junto al fuego (el capote, con las pieles cosidas por
 *   dentro que enseña su faldón vuelto, un remiendo y el bajo chamuscado; la manta, doblada sobre la horquilla, con sus
 *   rayas y tiesa de escarcha); la mochila abierta, con la tapa echada atrás, la camisa dentro y los tirantes blancos
 *   por el suelo, y la manta que llevaba encima, enrollada al lado; y las botas: una de pie con su polaina abotonada y
 *   un trapo, y la otra tumbada de canto delante, enseñando la suela de clavos, rota y remendada. Pisan en `pie` y se recuestan
 *   hacia `apoyo`; cada uno pinta su sombra y, en la nieve, el hoyo que hace y el labio de nieve que tapa su base.
 * - **Chicos** (de 18 a 36 unidades a escala 1, sobre el rollo de la mochila): la bayoneta desnuda de tres filos junto
 *   a su vaina, el chacó con su placa del águila, la cantimplora de calabaza forrada de cuero, tres galletas (una partida)
 *   sobre un trapo, la pipa corta de barro blanco con su bolsa de tabaco, la carta cerrada con su lacre sobre otra
 *   abierta, y la cuchara de estaño en su escudilla. Van con su sombra corta.
 *
 * De noche, el lado que mira al fuego del alto se enciende con la lumbre del pincel (`calida`) y el otro queda en el azul
 * de la luna. Como el contrato no dice dónde está el fuego, se toma el del alto ([RetiradaCerca.FUEGO]; el vivac
 * provisional lo tiene en el mismo sitio).
 *
 * **[Diseño propio]**: la horquilla de secar (el cuento no la nombra; el capote y la manta se secan junto al fuego y así
 * se sostienen también cuando el pabellón no está, de marcha); el trapo de la llave del fusil; la tela de las galletas;
 * la escudilla de la cuchara y la bolsa de tabaco de la pipa (para que algo tan chico se lea a tamaño de teléfono); las
 * rayas azules de la manta (las tiene ya la manta del cuadro). La placa del chacó es un rombo con un águila, sin número;
 * la carta lleva renglones, sin letras.
 */
internal object ObjetosNapoleon {
    private val C = ColorNapoleon

    /** Los doce objetos del calendario de Jean-Baptiste (`AnioG5.kt`). */
    val TODOS = listOf("fusil", "bayoneta", "mochila", "capote", "chaco", "cantimplora", "galleta", "pipa", "carta", "cuchara", "botas", "manta")

    /** Los que no caben sobre el soporte: van de pie o apoyados. */
    val GRANDES = setOf("fusil", "mochila", "capote", "botas", "manta")

    /** Unidades del lienzo por metro, a escala 1 (a 1.1, las del alto de la retirada). */
    const val METRO = 74f

    // ---- Los pocos colores propios: mezclas de la paleta del tema.
    private val NOGAL = lerp(C.MADERA, C.CUERO, 0.22f)                           // la caja del fusil
    private val ACERO = lerp(C.HIERRO, C.PLATA, 0.42f)
    private val FIELTRO = lerp(C.CENIZA, C.AZUL, 0.22f)                          // el chacó
    private val CHAROL = lerp(C.CUERO, C.CENIZA, 0.5f)                           // la visera y los cueros negros
    private val ARCILLA = lerp(C.LONA, C.NIEVE, 0.45f)                           // la pipa de barro blanco
    private val PAPEL = lerp(C.LONA, C.HOJA, 0.14f)
    private val TINTA = lerp(C.CUERO, C.AZUL, 0.3f)
    private val ESTANO = lerp(C.PLATA, C.HIERRO, 0.28f)
    private val GALLETA = lerp(C.HOJA, C.MADERA, 0.32f)
    private val LANA = lerp(C.CAPOTE, C.LONA, 0.38f)                             // la manta de lana gris
    private val PARDO = lerp(C.CAPOTE, C.MADERA, 0.18f)                          // el capote, «gris o pardo»
    private val PELLEJO = lerp(C.PIEL, C.HOJA, 0.3f)                             // las pieles cosidas por dentro
    private val TRAPO = lerp(C.LONA, C.CAPOTE, 0.45f)
    private val CALABAZA = lerp(C.CUERO, C.MADERA, 0.55f)
    private val VARA = C.MADERA_GRIS

    /** Si el objeto de hoy es de los grandes (no cabe sobre el soporte y va apoyado). */
    fun esGrande(m: Momento): Boolean = m.ficha.objeto in GRANDES

    /** Si [id] es de los grandes. */
    fun esGrande(id: String?): Boolean = id in GRANDES

    /**
     * El objeto chico de hoy, sobre el soporte. No pinta nada si hoy no hay objeto o es de los grandes.
     * [sobre] es el centro de la cara de arriba del soporte; [escala] 1 es el tamaño de primer plano.
     */
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) = chico(m.ficha.objeto, p, sobre, escala, EscenaNapoleon.nieve(m))

    /** El objeto chico [id] sobre el soporte (lo mismo que [sobreElSoporte], sin el momento: para el muestrario y las pruebas). */
    fun DrawScope.chico(id: String?, p: Pincel, sobre: Offset, escala: Float = 1f, nieve: Float = 0f) {
        if (id == null || id in GRANDES || id !in TODOS) return
        val k = escala
        val f = haciaElFuego(sobre)
        when (id) {
            "bayoneta" -> bayoneta(p, sobre, k * 1.2f, f)
            "chaco" -> chaco(p, sobre, k, f, nieve)
            "cantimplora" -> cantimplora(p, sobre, k, f)
            "galleta" -> galletas(p, sobre, k, f)
            "pipa" -> pipa(p, sobre, k * 1.2f, f)
            "carta" -> carta(p, sobre, k, f)
            "cuchara" -> cuchara(p, sobre, k, f)
        }
    }

    /**
     * El objeto grande de hoy: pisa en [pie] y se recuesta en [apoyo], un punto alto de los fusiles en pabellón.
     * Devuelve null si hoy no hay objeto o es de los chicos.
     */
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? = grande(m.ficha.objeto, p, pie, apoyo, escala, EscenaNapoleon.nieve(m))

    /** El objeto grande [id] (lo mismo que el de arriba, sin el momento). */
    fun grande(id: String?, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f, nieve: Float = 0f): Cosa? {
        if (id == null || id !in GRANDES) return null
        val u = METRO * escala
        // Cuánto se recuesta: hacia el apoyo, poco (lo plantado en la nieve no se tumba).
        val d = apoyo - pie
        val ang = if (d.getDistance() < 1f) 0f else atan2(d.x, -d.y).coerceIn(-0.2f, 0.2f)
        val hacia = if (d.x >= 0f) 1f else -1f
        val f = haciaElFuego(pie)
        // Junto al fuego la nieve está derretida: ni hoyo ni labio, el barro negro del cuadro.
        val nv = if (derretido(pie)) 0f else nieve
        return when (id) {
            "fusil" -> { val e = Eje(pie, ang, u); val s = siluetaFusil(e); Cosa(s, pie) { sombraEnElSuelo(p, s, pie); hoyo(p, pie, 0.2f * u, nv); fusil(p, e, f); labio(p, pie, 0.16f * u, nv) } }
            "capote" -> { val e = Eje(pie, ang * 0.6f, u); val s = siluetaColgado(e, 0.36f, 1.3f); Cosa(s, pie) { sombraEnElSuelo(p, s, pie); hoyo(p, pie, 0.16f * u, nv); horquilla(p, e, f); capote(p, e, f, nieve); labio(p, pie, 0.1f * u, nv) } }
            "manta" -> { val e = Eje(pie, ang * 0.6f, u); val s = siluetaColgado(e, 0.28f, 0.8f, CUELGA_MANTA); Cosa(s, pie) { sombraEnElSuelo(p, s, pie); hoyo(p, pie, 0.16f * u, nv); horquilla(p, e, f, CUELGA_MANTA); manta(p, e, f, nieve); labio(p, pie, 0.1f * u, nv) } }
            "mochila" -> { val e = Eje(pie + Offset(-0.1f * u, 0f), ang * 0.2f, u); val s = siluetaMochila(e); Cosa(s, pie) { sombraEnElSuelo(p, s, pie); hoyo(p, e.q(-0.02f, 0f), 0.6f * u, nv); mochila(p, e, f, nieve); labio(p, e.q(0.0f, -0.01f), 0.46f * u, nv) } }
            "botas" -> { val e = Eje(pie + Offset(-0.08f * u, 0f), 0f, u); val s = siluetaBotas(e); Cosa(s, pie) { sombraEnElSuelo(p, s, pie); hoyo(p, e.q(-0.06f, 0f), 0.6f * u, nv); botas(p, e, f, nieve); labio(p, e.q(-0.04f, -0.01f), 0.34f * u, nv) } }
            else -> null
        }
    }

    // =================================================================
    //  Ayudas
    // =================================================================

    /** Los ejes de una cosa de pie en [pie], recostada [ang] radianes (positivo, hacia la derecha), con [u] unidades por metro. */
    private class Eje(val pie: Offset, val ang: Float, val u: Float) {
        private val c = cos(ang); private val s = sin(ang)
        /** Un punto de la cosa: [x] a su derecha, [y] hacia arriba, en metros. */
        fun q(x: Float, y: Float): Offset = Offset(pie.x + (x * c + y * s) * u, pie.y + (x * s - y * c) * u)
        fun girado(dAng: Float, desde: Offset = pie) = Eje(desde, ang + dAng, u)
    }

    /**
     * La cara de arriba del soporte, vista desde quien mira: [x] a la derecha, [z] hacia el fondo, [h] hacia arriba, en
     * unidades a escala 1. [aplasta] es cuánto se ve de lo hondo.
     */
    private class Plano(val o: Offset, val k: Float, giro: Float = 0f, val aplasta: Float = 0.42f) {
        private val c = cos(giro); private val s = sin(giro)
        fun q(x: Float, z: Float, h: Float = 0f) = Offset(o.x + (x * c - z * s) * k, o.y - ((x * s + z * c) * aplasta + h) * k)
    }

    /** Hacia dónde está el fuego del alto, visto desde [en]: 1 a la derecha, −1 a la izquierda. */
    private fun haciaElFuego(en: Offset): Float = if (RetiradaCerca.FUEGO.x >= en.x) 1f else -1f

    /** Si [en] cae en el corro de nieve derretida alrededor del fuego del alto (el barro negro que pinta el cuadro). */
    private fun derretido(en: Offset): Boolean { val dx = (en.x - RetiradaCerca.FUEGO.x) / 56f; val dy = (en.y - RetiradaCerca.FUEGO.y - 6f) / 16f; return dx * dx + dy * dy < 1f }

    private fun lado(p: Pincel) = if (p.solALaDerecha) 1f else -1f

    /**
     * Un palo o un tubo que sigue los [puntos] con su medio ancho en cada uno (en unidades): oscuro del lado de la sombra,
     * el color propio en medio y un filo de luz del lado del sol.
     */
    private fun DrawScope.tubo(p: Pincel, puntos: List<Offset>, anchos: List<Float>, color: Color, brillo: Float = 0.75f): Path {
        val izq = ArrayList<Offset>(); val der = ArrayList<Offset>()
        for (i in puntos.indices) {
            val a = puntos[maxOf(i - 1, 0)]; val b = puntos[minOf(i + 1, puntos.size - 1)]
            val d = b - a; val l = d.getDistance().coerceAtLeast(0.001f); val n = Offset(-d.y / l, d.x / l)
            izq += puntos[i] + n * anchos[i]; der += puntos[i] - n * anchos[i]
        }
        val forma = poligono(izq + der.reversed())
        val L = lado(p)
        drawPath(forma, p.por(color, -0.55f))
        clipPath(forma) {
            for (i in 0 until puntos.size - 1) drawLine(p.por(color, 0.2f), puntos[i] + Offset(L * anchos[i] * 0.4f, 0f), puntos[i + 1] + Offset(L * anchos[i + 1] * 0.4f, 0f), anchos[i] + anchos[i + 1], StrokeCap.Round)
            for (i in 0 until puntos.size - 1) {
                val w = (anchos[i] + anchos[i + 1]) * 0.22f
                if (w > 0.15f) drawLine(p.luz(lerp(color, C.LONA, 0.25f)).copy(alpha = brillo), puntos[i] + Offset(L * anchos[i] * 0.62f, 0f), puntos[i + 1] + Offset(L * anchos[i + 1] * 0.62f, 0f), w, StrokeCap.Round)
            }
        }
        return forma
    }

    /**
     * De noche, el lado que mira al fuego ([f]: 1 a la derecha) se enciende con la lumbre del pincel, en degradado
     * sobre la [forma], entre [x0] y [x1]; el otro lado se queda en el azul de la luna.
     */
    private fun DrawScope.filoCalido(p: Pincel, forma: Path, x0: Float, x1: Float, f: Float, fuerza: Float = 0.55f) {
        if (p.calida < 0.03f) return
        val a = p.lumbre.copy(alpha = (fuerza * p.calida).coerceIn(0f, 1f))
        val cero = Color.Transparent
        val colores = if (f > 0f) listOf(cero, cero, a.copy(alpha = a.alpha * 0.35f), a) else listOf(a, a.copy(alpha = a.alpha * 0.35f), cero, cero)
        clipPath(forma) { drawRect(Brush.horizontalGradient(colores, minOf(x0, x1), maxOf(x0, x1)), Offset(minOf(x0, x1), -400f), Size(abs(x1 - x0).coerceAtLeast(1f), 1200f), blendMode = BlendMode.Plus) }
    }

    /** La sombra corta de un objeto chico sobre el rollo: tendida hacia el lado contrario al sol, baja. */
    private fun DrawScope.sombrita(p: Pincel, pie: Offset, ancho: Float, alto: Float) {
        val r = p.r
        // La de contacto, que hay siempre (también de noche).
        drawOval(p.sombra(C.CENIZA).copy(alpha = 0.32f), Offset(pie.x - ancho * 0.45f, pie.y - ancho * 0.07f), Size(ancho * 0.9f, ancho * 0.14f))
        if (r.fuerza < 0.05f) return
        val d = (r.ax * alto * 0.7f).coerceIn(-ancho * 1.2f, ancho * 1.2f)
        drawOval(r.color, Offset(minOf(pie.x - ancho / 2f, pie.x - ancho / 2f + d), pie.y - ancho * 0.1f), Size(ancho + abs(d), ancho * 0.2f), alpha = (r.fuerza * 0.6f).coerceIn(0f, 1f), blendMode = BlendMode.Multiply)
    }

    /** La sombra arrojada de un objeto grande sobre el suelo, desde su [forma] y su [pie] (la misma cuenta que `sombraArrojada`). */
    private fun DrawScope.sombraEnElSuelo(p: Pincel, forma: Path, pie: Offset) {
        val r = p.r; if (r.fuerza < 0.05f) return
        withTransform({ transform(Matrix().apply { values[4] = -r.ax; values[12] = r.ax * pie.y; values[5] = -r.ay; values[13] = pie.y * (1f + r.ay) }) }) {
            drawPath(forma, r.color, alpha = (r.fuerza * 0.62f).coerceIn(0f, 1f), blendMode = BlendMode.Multiply)
        }
    }

    /**
     * Lo que hace un objeto grande en el suelo, antes de pintarlo: la sombra de contacto y, en la nieve, el hoyo azul
     * que abrió al hundirse (una mancha fría, ancha y baja).
     */
    private fun DrawScope.hoyo(p: Pincel, c: Offset, ancho: Float, nieve: Float) {
        val alto = ancho * 0.2f
        if (nieve > 0.2f) {
            drawOval(p.sombra(C.NIEVE_SOMBRA).copy(alpha = (0.75f * nieve).coerceIn(0f, 1f)), Offset(c.x - ancho * 0.6f, c.y - alto * 0.55f), Size(ancho * 1.2f, alto * 1.1f))
        }
        drawOval(p.sombra(C.CENIZA).copy(alpha = 0.4f), Offset(c.x - ancho * 0.4f, c.y - alto * 0.3f), Size(ancho * 0.8f, alto * 0.6f))
    }

    /** Después de pintar el objeto: el labio de nieve de delante, que le tapa la base (está hundido, no posado). */
    private fun DrawScope.labio(p: Pincel, c: Offset, ancho: Float, nieve: Float) {
        if (nieve < 0.2f) return
        val alto = ancho * 0.22f
        val forma = Path().apply {
            moveTo(c + Offset(-ancho * 0.6f, alto * 0.25f))
            cubicTo(c + Offset(-ancho * 0.35f, -alto * 0.15f), c + Offset(ancho * 0.3f, -alto * 0.2f), c + Offset(ancho * 0.62f, alto * 0.2f))
            cubicTo(c + Offset(ancho * 0.3f, alto * 0.75f), c + Offset(-ancho * 0.3f, alto * 0.8f), c + Offset(-ancho * 0.6f, alto * 0.25f)); close()
        }
        // La nieve de cerca del fuego está pisada y sucia: el labio, del mismo color, y su lomo apenas con luz.
        val sucia = lerp(C.NIEVE, C.NIEVE_SUCIA, 0.5f)
        drawPath(forma, p.arriba(sucia).copy(alpha = (nieve * 0.85f).coerceIn(0f, 1f)))
        drawPath(Path().apply { moveTo(c + Offset(-ancho * 0.4f, alto * 0.08f)); cubicTo(c + Offset(-ancho * 0.2f, -alto * 0.08f), c + Offset(ancho * 0.2f, -alto * 0.1f), c + Offset(ancho * 0.42f, alto * 0.08f)) }, p.luz(sucia).copy(alpha = 0.45f * nieve), style = Stroke((ancho * 0.025f).coerceIn(0.3f, 1.2f), cap = StrokeCap.Round))
    }

    /** Un polvo de nieve sobre lo alto de algo: copos aplastados a lo largo de [puntos]. */
    private fun DrawScope.empolvado(p: Pincel, puntos: List<Offset>, grueso: Float, nieve: Float, az: Azar) {
        if (nieve < 0.3f) return
        for (i in 0 until puntos.size - 1) {
            val a = puntos[i]; val b = puntos[i + 1]
            repeat(3) { val q = a + (b - a) * az.real(); mancha(q + Offset(0f, -grueso * 0.2f), grueso * az.entre(0.7f, 1.3f), grueso * 0.45f, p.arriba(C.NIEVE), 0.85f * nieve) }
        }
    }

    /** Un renglón escrito, sin letras: trazos cortos y largos como palabras, con su hueco entre una y otra. */
    private fun DrawScope.renglon(a: Offset, b: Offset, c: Color, ancho: Float, az: Azar, lleno: Float = 0.92f) {
        val d = b - a; var s = 0f
        while (s < lleno) { val e = minOf(s + az.entre(0.07f, 0.22f), lleno); drawLine(c, a + d * s, a + d * e, ancho, StrokeCap.Round); s = e + az.entre(0.04f, 0.07f) }
    }

    // =================================================================
    //  El fusil
    // =================================================================

    /** La caja del fusil, de la culata a la boca: (alto, borde de la izquierda, borde de la derecha), en metros. */
    private val CAJA = listOf(
        Triple(0.0f, -0.052f, 0.078f), Triple(0.1f, -0.049f, 0.07f), Triple(0.22f, -0.041f, 0.056f), Triple(0.33f, -0.03f, 0.037f),
        Triple(0.41f, -0.019f, 0.021f), Triple(0.48f, -0.021f, 0.025f), Triple(0.62f, -0.019f, 0.021f), Triple(1.0f, -0.017f, 0.018f), Triple(1.37f, -0.014f, 0.014f),
    )
    private const val LARGO_FUSIL = 1.52f

    private fun siluetaFusil(e: Eje): Path = poligono(CAJA.map { e.q(it.second, it.first) } + listOf(e.q(-0.02f, LARGO_FUSIL), e.q(0.0f, LARGO_FUSIL)) + CAJA.reversed().map { e.q(it.third, it.first) })

    private fun DrawScope.fusil(p: Pincel, e: Eje, f: Float) {
        val u = e.u; val L = lado(p)
        val az = Azar(Semilla.de("napoleon", "objeto", "fusil"))
        // ---- El cañón: de hierro pavonado, con su filo de luz y la boca negra.
        val canon = tubo(p, listOf(e.q(-0.011f, 0.42f), e.q(-0.010f, 1.0f), e.q(-0.009f, LARGO_FUSIL)), listOf(0.012f * u, 0.011f * u, 0.0095f * u), ACERO, 0.9f)
        drawOval(p.sombra(C.CENIZA), Offset(e.q(-0.009f, LARGO_FUSIL).x - 0.009f * u, e.q(-0.009f, LARGO_FUSIL).y - 0.004f * u), Size(0.018f * u, 0.008f * u))
        // El punto de mira, en la boca.
        drawLine(p.luz(C.LATON), e.q(-0.019f, 1.49f), e.q(-0.024f, 1.495f), 0.007f * u, StrokeCap.Round)
        // ---- La caja de nogal: la culata ancha, la garganta, la caña larga hasta la última abrazadera.
        val caja = poligono(CAJA.map { e.q(it.second, it.first) } + CAJA.reversed().map { e.q(it.third, it.first) })
        val izq = e.q(-0.06f, 0.3f); val der = e.q(0.08f, 0.3f)
        drawPath(caja, Brush.linearGradient(if (L > 0f) listOf(p.por(NOGAL, -0.7f), p.por(NOGAL, 0.05f), p.por(NOGAL, 0.6f)) else listOf(p.por(NOGAL, 0.6f), p.por(NOGAL, 0.05f), p.por(NOGAL, -0.7f)), izq, der))
        clipPath(caja) {
            // Las vetas, a lo largo, y el brillo de la madera aceitada en el lomo de la culata.
            repeat(9) {
                val x = az.entre(-0.04f, 0.05f); val y0 = az.entre(0.02f, 0.25f)
                drawPath(Path().apply { moveTo(e.q(x, y0)); cubicTo(e.q(x + 0.01f, y0 + 0.08f), e.q(x * 0.5f, y0 + 0.16f), e.q(x * 0.3f, y0 + 0.3f)) }, p.sombra(lerp(NOGAL, C.CENIZA, 0.4f)).copy(alpha = 0.45f), style = Stroke(0.004f * u))
            }
            drawLine(p.luz(lerp(NOGAL, C.LONA, 0.3f)).copy(alpha = 0.55f), e.q(0.05f * L, 0.03f), e.q(0.012f * L, 0.36f), 0.012f * u, StrokeCap.Round)
            drawLine(p.luz(lerp(NOGAL, C.LONA, 0.3f)).copy(alpha = 0.4f), e.q(0.008f * L, 0.5f), e.q(0.006f * L, 1.35f), 0.005f * u, StrokeCap.Round)
            // Los golpes y la mugre de manos en la garganta.
            mancha(e.q(0.0f, 0.42f), 0.03f * u, 0.05f * u, p.sombra(C.CENIZA), 0.35f)
            repeat(6) { drawCircle(p.sombra(C.CENIZA).copy(alpha = 0.4f), 0.004f * u, e.q(az.entre(-0.03f, 0.05f), az.entre(0.05f, 0.3f))) }
        }
        filoCalido(p, caja, izq.x, der.x, f)
        // La cantonera de hierro, abajo.
        drawLine(p.por(C.HIERRO, 0.1f), e.q(-0.052f, 0.006f), e.q(0.078f, 0.006f), 0.012f * u)
        // ---- Las tres abrazaderas, la baqueta y los dos anillos del portafusil.
        for (y in listOf(0.95f, 1.15f, 1.35f)) {
            drawLine(p.por(ACERO, -0.3f), e.q(-0.026f, y), e.q(0.02f, y + 0.004f), 0.018f * u, StrokeCap.Butt)
            drawLine(p.luz(lerp(ACERO, Color.White, 0.3f)).copy(alpha = 0.8f), e.q(-0.026f + 0.024f * (L + 1f) / 2f, y - 0.004f), e.q(-0.006f + 0.024f * (L + 1f) / 2f, y), 0.005f * u)
        }
        drawLine(p.medio(ACERO), e.q(0.009f, 0.62f), e.q(0.007f, 1.48f), 0.006f * u)
        drawCircle(p.luz(ACERO), 0.008f * u, e.q(0.007f, 1.48f))
        // ---- La llave: la platina, el pie de gato con su piedra y el rastrillo; el guardamonte, debajo.
        val platina = poligono(e.q(-0.03f, 0.44f), e.q(0.004f, 0.43f), e.q(0.004f, 0.58f), e.q(-0.026f, 0.6f))
        drawPath(platina, p.por(C.HIERRO, 0.2f * L))
        drawPath(platina, p.luz(ACERO).copy(alpha = 0.6f), style = Stroke(0.004f * u))
        val gato = Path().apply { moveTo(e.q(-0.022f, 0.46f)); cubicTo(e.q(-0.05f, 0.47f), e.q(-0.06f, 0.5f), e.q(-0.05f, 0.53f)); lineTo(e.q(-0.04f, 0.535f)); cubicTo(e.q(-0.045f, 0.51f), e.q(-0.035f, 0.49f), e.q(-0.018f, 0.485f)); close() }
        drawPath(gato, p.por(C.HIERRO, 0.3f))
        drawPath(poligono(e.q(-0.058f, 0.535f), e.q(-0.036f, 0.533f), e.q(-0.036f, 0.548f), e.q(-0.056f, 0.551f)), p.por(lerp(C.CENIZA, C.CAPOTE, 0.5f), 0.4f))     // la piedra
        drawLine(p.por(ACERO, 0.1f), e.q(-0.038f, 0.555f), e.q(-0.034f, 0.61f), 0.012f * u, StrokeCap.Round)                                                       // el rastrillo
        drawPath(Path().apply { moveTo(e.q(0.021f, 0.39f)); cubicTo(e.q(0.058f, 0.41f), e.q(0.058f, 0.48f), e.q(0.024f, 0.5f)) }, p.por(ACERO, 0.2f), style = Stroke(0.009f * u, cap = StrokeCap.Round))
        drawLine(p.sombra(C.HIERRO), e.q(0.022f, 0.44f), e.q(0.04f, 0.45f), 0.006f * u, StrokeCap.Round)                                                           // el gatillo
        // ---- El trapo atado debajo de la llave, contra la nieve: dos vueltas y la punta que cuelga.
        val trapo = Path().apply { moveTo(e.q(-0.034f, 0.415f)); lineTo(e.q(0.03f, 0.405f)); lineTo(e.q(0.032f, 0.45f)); lineTo(e.q(-0.032f, 0.462f)); close() }
        drawPath(trapo, p.por(TRAPO, 0.3f * L))
        clipPath(trapo) { drawLine(p.sombra(TRAPO).copy(alpha = 0.7f), e.q(-0.04f, 0.44f), e.q(0.04f, 0.425f), 0.006f * u) }
        val punta = Path().apply { moveTo(e.q(0.026f, 0.43f)); cubicTo(e.q(0.06f, 0.42f), e.q(0.07f, 0.38f), e.q(0.068f, 0.33f)); lineTo(e.q(0.054f, 0.335f)); cubicTo(e.q(0.055f, 0.38f), e.q(0.045f, 0.41f), e.q(0.024f, 0.415f)); close() }
        drawPath(punta, p.por(TRAPO, -0.25f))
        filoCalido(p, trapo, e.q(-0.04f, 0.4f).x, e.q(0.07f, 0.4f).x, f)
        // ---- El portafusil de ante blanco, suelto: baja del anillo de abajo, cuelga en una comba y sube a la abrazadera.
        val a = e.q(0.024f, 0.36f); val b = e.q(0.02f, 0.96f)
        val correa = Path().apply { moveTo(a); cubicTo(e.q(0.13f, 0.4f), e.q(0.12f, 0.72f), b) }
        drawPath(correa, p.sombra(C.CORREA), style = Stroke(0.026f * u, cap = StrokeCap.Round))
        drawPath(correa, p.por(C.CORREA, 0.35f * L), style = Stroke(0.017f * u, cap = StrokeCap.Round))
        val hebilla = e.q(0.098f, 0.6f)
        drawRect(p.luz(C.LATON), Offset(hebilla.x - 0.016f * u, hebilla.y - 0.012f * u), Size(0.032f * u, 0.024f * u), style = Stroke(0.006f * u))
        for (q in listOf(a, b)) drawCircle(p.por(ACERO, 0.2f), 0.009f * u, q, style = Stroke(0.004f * u))
        // El último brillo rojo del fuego en el cañón («brillan rojos un momento»).
        filoCalido(p, canon, e.q(-0.03f, 1f).x, e.q(0.01f, 1f).x, f, 0.8f)
    }

    // =================================================================
    //  La horquilla de secar, el capote y la manta
    // =================================================================

    /** Lo alto de la horquilla, donde cuelga la ropa: en metros sobre el suelo, en el eje del palo. */
    private const val CUELGA = 1.42f

    /** La de la manta, más baja: doblada en cuatro llega casi al suelo. */
    private const val CUELGA_MANTA = 1.0f

    private fun siluetaColgado(e: Eje, medioAncho: Float, largo: Float, cu: Float = CUELGA): Path {
        val t = e.q(0f, cu); val alto = (e.pie.y - t.y) / e.u
        val h = Eje(Offset(t.x, e.pie.y), 0f, e.u)
        val bajo = (alto - largo).coerceAtLeast(0.05f)
        return Path().apply {
            addPath(poligono(e.q(-0.02f, 0f), e.q(0.02f, 0f), e.q(0.02f, cu + 0.18f), e.q(-0.02f, cu + 0.18f)))
            addPath(poligono(h.q(-0.1f, alto), h.q(0.1f, alto), h.q(medioAncho, bajo), h.q(-medioAncho, bajo)))
        }
    }

    /** Una rama de pino pelada, clavada en la nieve, con su horquilla arriba y los muñones de las ramitas cortadas. */
    private fun DrawScope.horquilla(p: Pincel, e: Eje, f: Float, cu: Float = CUELGA) {
        val u = e.u
        val palo = tubo(p, listOf(e.q(0f, -0.04f), e.q(0.004f, 0.7f), e.q(-0.003f, cu)), listOf(0.018f * u, 0.016f * u, 0.014f * u), VARA, 0.7f)
        for ((dx, dy) in listOf(-0.09f to 0.2f, 0.08f to 0.17f)) tubo(p, listOf(e.q(-0.002f, cu - 0.02f), e.q(dx * 0.6f, cu + dy * 0.6f), e.q(dx, cu + dy)), listOf(0.012f * u, 0.01f * u, 0.008f * u), VARA, 0.6f)
        // Los muñones y la corteza que queda a trozos.
        for (y in listOf(0.38f, 0.86f)) drawLine(p.por(VARA, -0.2f), e.q(0f, y), e.q(0.04f * (if (y > 0.5f) 1f else -1f), y + 0.03f), 0.012f * u, StrokeCap.Round)
        val az = Azar(Semilla.de("napoleon", "objeto", "horquilla"))
        clipPath(palo) { repeat(8) { val y = az.entre(0.05f, cu); drawLine(p.sombra(C.MADERA_OSC).copy(alpha = 0.7f), e.q(-0.02f, y), e.q(0.02f, y + az.entre(0.02f, 0.08f)), 0.008f * u) } }
        filoCalido(p, palo, e.q(-0.03f, 0.5f).x, e.q(0.03f, 0.5f).x, f)
    }

    private fun DrawScope.capote(p: Pincel, e: Eje, f: Float, nieve: Float) {
        val u = e.u; val L = lado(p)
        val az = Azar(Semilla.de("napoleon", "objeto", "capote"))
        val t = e.q(0f, CUELGA); val alto = (e.pie.y - t.y) / u
        val h = Eje(Offset(t.x, e.pie.y), 0f, u)
        fun q(x: Float, y: Float) = h.q(x, alto - y)                                 // y: cuánto baja desde el cuello
        val largo = (alto - 0.08f).coerceAtMost(1.3f)
        val hondo = largo                                                            // el bajo
        // ---- Las mangas, detrás del cuerpo: cuelgan de los hombros, con su vuelta en el puño.
        for (s in listOf(-1f, 1f)) {
            val manga = Path().apply {
                moveTo(q(s * 0.2f, 0.1f)); cubicTo(q(s * 0.27f, 0.13f), q(s * 0.3f, 0.3f), q(s * 0.31f, 0.58f))
                lineTo(q(s * 0.2f, 0.6f)); cubicTo(q(s * 0.21f, 0.4f), q(s * 0.19f, 0.25f), q(s * 0.16f, 0.16f)); close()
            }
            drawPath(manga, p.por(PARDO, if (s == L) 0.3f else -0.6f))
            drawPath(poligono(q(s * 0.2f, 0.53f), q(s * 0.31f, 0.52f), q(s * 0.315f, 0.6f), q(s * 0.2f, 0.61f)), p.por(lerp(PARDO, C.CENIZA, 0.25f), if (s == L) 0.1f else -0.7f))
            filoCalido(p, manga, q(s * 0.15f, 0f).x, q(s * 0.32f, 0f).x, f, 0.5f)
        }
        // ---- El cuerpo: cuelga de los hombros y se abre hacia el bajo, que ondula.
        val cuerpo = Path().apply {
            moveTo(q(-0.08f, 0f)); lineTo(q(0.08f, 0f))
            cubicTo(q(0.16f, 0.03f), q(0.22f, 0.08f), q(0.235f, 0.14f))
            cubicTo(q(0.25f, 0.5f), q(0.3f, 0.9f), q(0.33f, hondo - 0.02f))
            cubicTo(q(0.2f, hondo + 0.03f), q(0.1f, hondo - 0.03f), q(0.0f, hondo + 0.01f))
            cubicTo(q(-0.12f, hondo + 0.04f), q(-0.22f, hondo - 0.02f), q(-0.33f, hondo))
            cubicTo(q(-0.3f, 0.9f), q(-0.25f, 0.5f), q(-0.235f, 0.14f))
            cubicTo(q(-0.22f, 0.08f), q(-0.16f, 0.03f), q(-0.08f, 0f)); close()
        }
        val iz = q(-0.34f, 0.5f); val de = q(0.34f, 0.5f)
        drawPath(cuerpo, Brush.horizontalGradient(if (L > 0f) listOf(p.por(PARDO, -0.75f), p.por(PARDO, -0.1f), p.por(PARDO, 0.55f)) else listOf(p.por(PARDO, 0.55f), p.por(PARDO, -0.1f), p.por(PARDO, -0.75f)), iz.x, de.x))
        clipPath(cuerpo) {
            // El faldón de la derecha, vuelto: enseña las pieles que se le cosieron por dentro.
            val vuelta = Path().apply {
                moveTo(q(0.02f, 0.55f)); cubicTo(q(0.06f, 0.8f), q(0.1f, 1.0f), q(0.16f, hondo + 0.05f))
                lineTo(q(-0.04f, hondo + 0.05f)); cubicTo(q(-0.03f, 1.0f), q(-0.01f, 0.8f), q(0.02f, 0.55f)); close()
            }
            drawPath(vuelta, p.por(PELLEJO, 0.25f * L))
            clipPath(vuelta) {
                repeat(70) { val c = q(az.entre(-0.04f, 0.16f), az.entre(0.55f, hondo)); drawLine(p.por(lerp(PELLEJO, C.LONA, 0.3f), az.entre(-0.5f, 0.6f)).copy(alpha = 0.75f), c, c + Offset(az.entre(-0.8f, 0.8f), az.entre(1.2f, 2.6f)), 0.45f) }
                mancha(q(0.06f, hondo - 0.1f), 0.08f * u, 0.06f * u, p.sombra(C.CUERO), 0.45f)
            }
            drawPath(Path().apply { moveTo(q(0.02f, 0.55f)); cubicTo(q(0.06f, 0.8f), q(0.1f, 1.0f), q(0.16f, hondo + 0.05f)) }, p.sombra(C.CENIZA).copy(alpha = 0.6f), style = Stroke(0.014f * u))
            // Los pliegues que bajan de la cintura: el lomo de luz y el valle de sombra.
            for (x in listOf(-0.23f, -0.15f, -0.05f, 0.21f, 0.27f)) {
                val b = az.entre(-0.02f, 0.02f)
                val pl = Path().apply { moveTo(q(x * 0.85f, 0.35f)); cubicTo(q(x * 0.92f + b, 0.6f), q(x + b, 0.9f), q(x * 1.08f, hondo)) }
                drawPath(pl, p.sombra(PARDO).copy(alpha = 0.75f), style = Stroke(0.014f * u, cap = StrokeCap.Round))
                val pl2 = Path().apply { moveTo(q(x * 0.85f + 0.016f * L, 0.35f)); cubicTo(q(x * 0.92f + b + 0.016f * L, 0.6f), q(x + b + 0.016f * L, 0.9f), q(x * 1.08f + 0.016f * L, hondo)) }
                drawPath(pl2, p.luz(PARDO).copy(alpha = 0.55f), style = Stroke(0.008f * u, cap = StrokeCap.Round))
            }
            // Un remiendo de otro paño, con sus puntadas.
            val rem = poligono(q(-0.2f, 0.66f), q(-0.09f, 0.65f), q(-0.085f, 0.76f), q(-0.195f, 0.775f))
            drawPath(rem, p.por(lerp(PARDO, C.AZUL, 0.42f), 0.1f * L))
            drawPath(rem, p.luz(C.LONA).copy(alpha = 0.7f), style = Stroke(0.004f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.012f * u, 0.01f * u))))
            // El bajo, con barro y chamuscado del lado del fuego, y el polvo de nieve.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(C.BARRO).copy(alpha = 0.75f)), q(0f, hondo - 0.2f).y, q(0f, hondo).y), Offset(iz.x, q(0f, hondo - 0.2f).y), Size(de.x - iz.x, 0.25f * u))
            mancha(q(0.26f * f, hondo - 0.02f), 0.09f * u, 0.07f * u, p.sombra(C.CENIZA), 0.8f)
            repeat(4) { drawCircle(p.sombra(C.CENIZA), az.entre(0.006f, 0.011f) * u, q(f * az.entre(0.16f, 0.3f), hondo - az.entre(0.02f, 0.1f))) }
            // El faldón de la derecha cae encima del vuelto: su canto con luz.
            drawLine(p.luz(PARDO).copy(alpha = 0.5f), q(-0.04f, 0.56f), q(-0.05f, hondo), 0.008f * u)
        }
        // ---- Los hombros y el cuello alto, oscuro, sobre la horquilla.
        val cuello = poligono(q(-0.085f, -0.06f), q(0.085f, -0.06f), q(0.1f, 0.05f), q(-0.1f, 0.05f))
        drawPath(cuello, p.por(lerp(PARDO, C.AZUL, 0.35f), 0.2f * L))
        drawLine(p.luz(PARDO).copy(alpha = 0.6f), q(-0.085f, -0.055f), q(0.085f, -0.055f), 0.01f * u, StrokeCap.Round)
        // ---- Las dos filas de botones de latón, que se juntan hacia la cintura; uno falta, con sus hilos.
        for (j in 0 until 6) for (s in listOf(-1f, 1f)) {
            val y = 0.1f + j * 0.08f; val x = s * (0.085f - j * 0.01f)
            if (j == 3 && s > 0f) { drawLine(p.sombra(C.LONA).copy(alpha = 0.7f), q(x, y), q(x + 0.01f, y + 0.02f), 0.003f * u); continue }
            val b = q(x, y)
            drawCircle(p.por(C.LATON, -0.3f), 0.013f * u, b)
            drawCircle(p.luz(lerp(C.LATON, Color.White, 0.35f)), 0.006f * u, b + Offset(0.004f * u * L, -0.004f * u))
        }
        filoCalido(p, cuerpo, iz.x, de.x, f)
        empolvado(p, listOf(q(-0.24f, 0.13f), q(-0.1f, 0.0f), q(0.1f, 0.0f), q(0.24f, 0.13f)), 0.03f * u, nieve, az)
    }

    private fun DrawScope.manta(p: Pincel, e: Eje, f: Float, nieve: Float) {
        val u = e.u; val L = lado(p)
        val az = Azar(Semilla.de("napoleon", "objeto", "manta"))
        val t = e.q(0f, CUELGA_MANTA); val alto = (e.pie.y - t.y) / u
        val h = Eje(Offset(t.x, e.pie.y), 0f, u)
        fun q(x: Float, y: Float) = h.q(x * 0.78f, alto - y)                         // y: cuánto baja desde la horquilla; doblada en cuatro, más estrecha
        val atras = (alto - 0.1f).coerceAtMost(0.8f)                                // el bajo de la mitad de detrás
        val delante = atras * 0.72f
        val tiesa = nieve > 0.3f
        // ---- La mitad de detrás, que asoma por debajo y por los lados, más en sombra.
        val tras = Path().apply {
            moveTo(q(-0.3f, 0.02f)); lineTo(q(0.32f, 0.04f)); lineTo(q(0.36f, atras - 0.03f))
            cubicTo(q(0.2f, atras + 0.02f), q(-0.1f, atras - 0.03f), q(-0.33f, atras + 0.01f)); close()
        }
        drawPath(tras, p.por(LANA, -0.6f))
        clipPath(tras) {
            for (dy in listOf(0.1f, 0.15f)) drawLine(p.sombra(lerp(LANA, C.AZUL, 0.6f)), q(-0.4f, atras - dy), q(0.4f, atras - dy + 0.01f), 0.03f * u)
            repeat(14) { val x = az.entre(-0.33f, 0.36f); drawLine(p.sombra(LANA), q(x, atras - 0.01f), q(x + az.entre(-0.01f, 0.01f), atras + 0.03f), 0.004f * u) }
        }
        // ---- La mitad de delante: dobleces tiesos, rayas azules cerca del bajo, flecos, un agujero quemado.
        val frente = Path().apply {
            moveTo(q(-0.31f, -0.02f)); cubicTo(q(-0.1f, -0.05f), q(0.12f, -0.05f), q(0.33f, -0.01f))
            lineTo(q(0.34f, delante)); lineTo(q(0.18f, delante + 0.03f)); lineTo(q(0.02f, delante - 0.01f)); lineTo(q(-0.14f, delante + 0.04f)); lineTo(q(-0.32f, delante + 0.01f)); close()
        }
        val iz = q(-0.34f, 0.3f); val de = q(0.34f, 0.3f)
        drawPath(frente, Brush.horizontalGradient(if (L > 0f) listOf(p.por(LANA, -0.5f), p.por(LANA, 0f), p.por(LANA, 0.55f)) else listOf(p.por(LANA, 0.55f), p.por(LANA, 0f), p.por(LANA, -0.5f)), iz.x, de.x))
        clipPath(frente) {
            // La lana áspera: pelusa clara y oscura.
            repeat(140) { val c = q(az.entre(-0.32f, 0.34f), az.entre(0f, delante)); drawLine((if (az.real() > 0.5f) p.luz(LANA) else p.sombra(LANA)).copy(alpha = 0.35f), c, c + Offset(az.entre(-1f, 1f), az.entre(-0.5f, 0.5f)), 0.4f) }
            // Los dobleces: tiesos de escarcha, rectos; si no, blandos.
            for (x in listOf(-0.16f, 0.05f, 0.2f)) {
                val b = if (tiesa) 0f else az.entre(-0.02f, 0.02f)
                drawPath(Path().apply { moveTo(q(x, 0.06f)); cubicTo(q(x + b, delante * 0.4f), q(x - b, delante * 0.7f), q(x + 0.01f, delante)) }, p.sombra(LANA).copy(alpha = 0.7f), style = Stroke(0.016f * u))
                drawPath(Path().apply { moveTo(q(x + 0.018f * L, 0.06f)); cubicTo(q(x + b + 0.018f * L, delante * 0.4f), q(x - b + 0.018f * L, delante * 0.7f), q(x + 0.01f + 0.018f * L, delante)) }, p.luz(LANA).copy(alpha = 0.6f), style = Stroke(0.008f * u))
            }
            // Las dos rayas azules del orillo.
            for (dy in listOf(0.09f, 0.14f)) drawPath(Path().apply { moveTo(q(-0.4f, delante - dy + 0.02f)); lineTo(q(0.02f, delante - dy - 0.01f)); lineTo(q(0.4f, delante - dy + 0.01f)) }, p.por(lerp(C.AZUL, LANA, 0.2f), 0.1f), style = Stroke(0.028f * u))
            // El agujero chamuscado de una chispa, y la mancha de hollín.
            val ag = q(0.12f * f, 0.32f)
            drawOval(p.sombra(C.CENIZA).copy(alpha = 0.5f), Offset(ag.x - 0.05f * u, ag.y - 0.035f * u), Size(0.1f * u, 0.07f * u))
            drawOval(p.sombra(C.CENIZA), Offset(ag.x - 0.022f * u, ag.y - 0.016f * u), Size(0.044f * u, 0.032f * u))
            drawOval(p.por(PARDO, -0.8f), Offset(ag.x - 0.014f * u, ag.y - 0.01f * u), Size(0.028f * u, 0.02f * u))
            // La escarcha: la lana tiesa y blanca de la noche al raso.
            if (tiesa) repeat(80) { val c = q(az.entre(-0.32f, 0.34f), az.entre(0f, delante)); drawCircle(p.luz(C.NIEVE).copy(alpha = 0.55f * nieve), az.entre(0.003f, 0.007f) * u, c) }
        }
        // Los flecos del bajo.
        repeat(22) { val x = -0.3f + 0.62f * it / 21f; val y = delante + 0.03f * (if (it % 4 == 1) 1f else 0f) - 0.005f; drawLine(p.por(LANA, -0.2f), q(x, y), q(x + 0.004f, y + 0.035f), 0.006f * u, StrokeCap.Round) }
        // ---- El lomo, donde se dobla sobre la horquilla: redondo, con su luz.
        drawLine(p.luz(LANA), q(-0.3f, -0.012f), q(0.32f, 0.0f), 0.022f * u, StrokeCap.Round)
        filoCalido(p, frente, iz.x, de.x, f)
        empolvado(p, listOf(q(-0.3f, -0.02f), q(0f, -0.04f), q(0.33f, -0.01f)), 0.035f * u, nieve, az)
    }

    // =================================================================
    //  La mochila, soltada en el suelo
    // =================================================================

    private fun siluetaMochila(e: Eje): Path = poligono(listOf(e.q(-0.26f, 0f), e.q(0.24f, 0.05f), e.q(0.3f, 0.5f), e.q(-0.25f, 0.5f)))

    /**
     * La mochila de piel de vaca, cuadrada, de pie en el suelo: la tapa cerrada con sus dos correas, el capote enrollado
     * encima con sus correas blancas, una manga de la camisa que asoma por un lado y los tirantes de ante tirados en la
     * nieve, en lazos, con sus hebillas. Más grande que las del cuadro y sin nada encima: es la que se acaba de soltar.
     */
    private fun DrawScope.mochila(p: Pincel, e: Eje, f: Float, nieve: Float) {
        val hacia = f                                                             // los tirantes caen lejos del fuego
        val u = e.u; val L = lado(p)
        val az = Azar(Semilla.de("napoleon", "objeto", "mochila"))
        // ---- Los tirantes, detrás y por el suelo: salen de lo alto de la espalda y caen en lazos hacia un lado.
        for (s in listOf(-1f, 1f)) {
            val a = e.q(s * 0.12f, 0.36f)
            val fin = e.q(-hacia * (0.3f + 0.08f * s), 0.02f + 0.03f * s)
            val tira = Path().apply {
                moveTo(a); cubicTo(e.q(s * 0.12f - 0.18f * hacia, 0.42f), e.q(-hacia * 0.32f, 0.28f), e.q(-hacia * (0.27f + 0.03f * s), 0.12f))
                cubicTo(e.q(-hacia * 0.24f, 0.04f), e.q(-hacia * 0.36f, -0.02f + 0.02f * s), fin)
            }
            drawPath(tira, p.sombra(C.CORREA), style = Stroke(0.036f * u, cap = StrokeCap.Round))
            drawPath(tira, p.por(C.CORREA, 0.35f * L), style = Stroke(0.024f * u, cap = StrokeCap.Round))
            drawRect(p.luz(C.LATON), Offset(fin.x - 0.016f * u, fin.y - 0.013f * u), Size(0.032f * u, 0.026f * u), style = Stroke(0.007f * u))
        }
        // ---- La caja: el frente y el costado de piel con su pelo y sus manchas blancas.
        val frente = poligono(e.q(-0.21f, 0f), e.q(0.19f, 0f), e.q(0.19f, 0.36f), e.q(-0.21f, 0.36f))
        val costado = poligono(e.q(0.19f, 0f), e.q(0.24f, 0.05f), e.q(0.24f, 0.41f), e.q(0.19f, 0.36f))
        drawPath(costado, p.por(C.PIEL, if (L > 0f) 0.45f else -0.75f))
        drawPath(frente, p.por(C.PIEL, 0.1f * L))
        for (forma in listOf(frente, costado)) clipPath(forma) {
            for ((x, y) in listOf(-0.12f to 0.1f, 0.06f to 0.05f, 0.22f to 0.2f, -0.02f to 0.14f)) mancha(e.q(x, y), 0.08f * u, 0.05f * u, p.por(lerp(C.CORREA, C.NIEVE, 0.4f), 0.1f * L), 0.95f)
            repeat(90) { val c = e.q(az.entre(-0.21f, 0.24f), az.entre(0f, 0.38f)); drawLine(p.sombra(lerp(C.PIEL, C.CUERO, 0.3f)).copy(alpha = 0.5f), c, c + Offset(az.entre(-0.5f, 0.5f), az.entre(0.8f, 1.8f)), 0.4f) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(C.BARRO).copy(alpha = 0.65f)), e.q(0f, 0.1f).y, e.q(0f, 0f).y), Offset(e.q(-0.3f, 0f).x, e.q(0f, 0.12f).y), Size(0.6f * u, 0.13f * u))
        }
        // La manga de la camisa, que asoma por debajo de la tapa, del lado de fuera.
        val manga = Path().apply { moveTo(e.q(-0.2f, 0.3f)); cubicTo(e.q(-0.27f, 0.28f), e.q(-0.3f, 0.2f), e.q(-0.28f, 0.12f)); lineTo(e.q(-0.22f, 0.12f)); cubicTo(e.q(-0.24f, 0.18f), e.q(-0.22f, 0.24f), e.q(-0.2f, 0.25f)); close() }
        drawPath(manga, p.por(C.LONA, 0.2f * L))
        drawLine(p.sombra(C.LONA), e.q(-0.285f, 0.13f), e.q(-0.22f, 0.13f), 0.012f * u)
        // ---- La tapa: baja por delante hasta media altura, con su ribete de cuero y sus dos correas con hebilla.
        val tapa = Path().apply {
            moveTo(e.q(-0.215f, 0.37f)); lineTo(e.q(0.195f, 0.37f)); lineTo(e.q(0.195f, 0.2f))
            cubicTo(e.q(0.195f, 0.17f), e.q(0.18f, 0.16f), e.q(0.15f, 0.16f)); lineTo(e.q(-0.17f, 0.16f))
            cubicTo(e.q(-0.2f, 0.16f), e.q(-0.215f, 0.17f), e.q(-0.215f, 0.2f)); close()
        }
        drawPath(tapa, p.por(lerp(C.PIEL, C.CUERO, 0.2f), 0.25f * L))
        clipPath(tapa) {
            mancha(e.q(0.08f, 0.3f), 0.07f * u, 0.04f * u, p.por(lerp(C.CORREA, C.NIEVE, 0.4f), 0.2f * L), 0.95f)
            repeat(50) { val c = e.q(az.entre(-0.21f, 0.19f), az.entre(0.16f, 0.37f)); drawLine(p.sombra(C.PIEL).copy(alpha = 0.45f), c, c + Offset(az.entre(-0.4f, 0.4f), az.entre(0.8f, 1.6f)), 0.4f) }
            drawPath(tapa, p.por(CHAROL, 0f), style = Stroke(0.022f * u))
        }
        drawRect(Brush.verticalGradient(listOf(p.sombra(C.CENIZA).copy(alpha = 0.55f), Color.Transparent), e.q(0f, 0.16f).y, e.q(0f, 0.12f).y), Offset(e.q(-0.21f, 0f).x, e.q(0f, 0.16f).y), Size(0.4f * u, 0.04f * u))
        for (x in listOf(-0.12f, 0.1f)) {
            drawLine(p.sombra(C.CORREA), e.q(x, 0.24f), e.q(x, 0.08f), 0.034f * u)
            drawLine(p.por(C.CORREA, 0.3f * L), e.q(x, 0.24f), e.q(x, 0.08f), 0.024f * u)
            val h = e.q(x, 0.13f)
            drawRect(p.luz(C.LATON), Offset(h.x - 0.022f * u, h.y - 0.016f * u), Size(0.044f * u, 0.032f * u), style = Stroke(0.008f * u))
        }
        // ---- El capote enrollado encima, a lo largo, con sus dos correas blancas y la cabeza del rollo con sus vueltas.
        val rr = 0.068f
        val r0 = e.q(-0.25f, 0.37f); val r1 = e.q(0.29f, 0.4f)
        val rollo = Path().apply { moveTo(r0); lineTo(r1); lineTo(r1 + Offset(0f, -2f * rr * u)); lineTo(r0 + Offset(0f, -2f * rr * u)); close() }
        drawPath(rollo, Brush.verticalGradient(listOf(p.por(PARDO, 0.6f), p.por(PARDO, 0.05f), p.por(PARDO, -0.8f)), r0.y - 2f * rr * u, r0.y))
        clipPath(rollo) { repeat(5) { val x = az.entre(-0.2f, 0.25f); drawLine(p.sombra(PARDO).copy(alpha = 0.5f), e.q(x, 0.37f), e.q(x + 0.03f, 0.37f + 2f * rr), 0.008f * u) } }
        val cabeza = (if (L > 0f) r1 else r0) + Offset(0f, -rr * u)
        drawOval(p.por(PARDO, 0.3f), Offset(cabeza.x - rr * u * 0.45f, cabeza.y - rr * u), Size(rr * u * 0.9f, rr * u * 2f))
        drawPath(Path().apply { for (j in 0..24) { val a = j * 0.62f; val rad = rr * u * (0.1f + 0.85f * j / 24f); val c = cabeza + Offset(0.45f * rad * cos(a), rad * sin(a)); if (j == 0) moveTo(c) else lineTo(c) } }, p.sombra(PARDO), style = Stroke(0.006f * u))
        for (x in listOf(-0.13f, 0.17f)) {
            val a = e.q(x, 0.37f + 0.02f * (x + 0.25f)); val b = a + Offset(0f, -2.05f * rr * u)
            drawLine(p.sombra(C.CORREA), a + Offset(0f, 0.01f * u), b, 0.034f * u)
            drawLine(p.por(C.CORREA, 0.4f * L), a, b + Offset(0f, 0.005f * u), 0.024f * u)
        }
        val todo = Path().apply { addPath(frente); addPath(costado); addPath(rollo) }
        filoCalido(p, todo, e.q(-0.26f, 0.3f).x, e.q(0.3f, 0.3f).x, f)
        empolvado(p, listOf(r0 + Offset(0f, -2f * rr * u), r1 + Offset(0f, -2f * rr * u)), 0.03f * u, nieve, az)
    }

    // =================================================================
    //  Las botas
    // =================================================================

    /** El perfil del zapato con su polaina, la punta a la derecha: (x, y) en metros. */
    private val BOTA = listOf(
        -0.11f to 0.0f, 0.12f to 0.0f, 0.16f to 0.012f, 0.17f to 0.04f, 0.15f to 0.065f, 0.08f to 0.09f, 0.045f to 0.13f,
        0.05f to 0.25f, 0.065f to 0.42f, -0.07f to 0.43f, -0.07f to 0.25f, -0.09f to 0.12f, -0.115f to 0.06f,
    )

    private fun siluetaBotas(e: Eje): Path = Path().apply {
        addPath(poligono(BOTA.map { e.q(it.first - 0.02f, it.second) }))
        addPath(poligono(e.q(-0.17f, 0f), e.q(0.14f, 0f), e.q(0.14f, 0.095f), e.q(-0.17f, 0.095f)))
    }

    private fun DrawScope.botas(p: Pincel, e: Eje, f: Float, nieve: Float) {
        val u = e.u; val L = lado(p)
        val az = Azar(Semilla.de("napoleon", "objeto", "botas"))
        val b = Eje(e.q(-0.02f, 0f), 0f, u)
        // ---- La de pie: el zapato de cuero y la polaina negra abotonada por fuera, con un trapo colgado del borde.
        val perfil = poligono(BOTA.map { b.q(it.first, it.second) })
        drawPath(perfil, Brush.horizontalGradient(if (L > 0f) listOf(p.por(CHAROL, -0.6f), p.por(CHAROL, 0.1f), p.por(CHAROL, 0.6f)) else listOf(p.por(CHAROL, 0.6f), p.por(CHAROL, 0.1f), p.por(CHAROL, -0.6f)), b.q(-0.12f, 0f).x, b.q(0.17f, 0f).x))
        clipPath(perfil) {
            // El zapato, de cuero pardo engrasado, y la suela con su canto.
            val zapato = poligono(listOf(b.q(-0.12f, 0f), b.q(0.18f, 0f), b.q(0.18f, 0.08f), b.q(0.06f, 0.1f), b.q(0.02f, 0.06f), b.q(-0.12f, 0.05f)))
            drawPath(zapato, p.por(lerp(C.CUERO, C.MADERA, 0.3f), 0.2f * L))
            drawLine(p.sombra(C.CENIZA), b.q(-0.12f, 0.008f), b.q(0.17f, 0.008f), 0.018f * u)
            drawLine(p.luz(lerp(C.CUERO, C.MADERA, 0.5f)).copy(alpha = 0.6f), b.q(0.06f, 0.075f), b.q(0.15f, 0.06f), 0.008f * u, StrokeCap.Round)
            // La trabilla de la polaina, que pasa por debajo, y su borde sobre el empeine.
            drawPath(Path().apply { moveTo(b.q(-0.1f, 0.06f)); cubicTo(b.q(-0.02f, 0.03f), b.q(0.03f, 0.05f), b.q(0.08f, 0.09f)) }, p.sombra(CHAROL), style = Stroke(0.012f * u))
            // Las arrugas de la polaina.
            for (y in listOf(0.17f, 0.24f, 0.3f, 0.36f)) drawPath(Path().apply { moveTo(b.q(-0.075f, y)); cubicTo(b.q(-0.03f, y - 0.015f), b.q(0.0f, y + 0.012f), b.q(0.06f, y - 0.005f)) }, p.luz(CHAROL).copy(alpha = 0.45f), style = Stroke(0.006f * u))
            // El barro de abajo y la nieve pegada.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.por(C.BARRO, 0f).copy(alpha = 0.8f)), b.q(0f, 0.12f).y, b.q(0f, 0.02f).y), Offset(b.q(-0.2f, 0f).x, b.q(0f, 0.13f).y), Size(0.45f * u, 0.13f * u))
            if (nieve > 0.3f) repeat(8) { mancha(b.q(az.entre(-0.1f, 0.16f), az.entre(0.01f, 0.05f)), 0.02f * u, 0.012f * u, p.arriba(C.NIEVE), 0.8f) }
        }
        // La fila de botones, del lado de fuera, de la polaina.
        for (j in 0 until 7) { val c = b.q(0.046f + 0.003f * j, 0.15f + j * 0.04f); drawCircle(p.por(C.CENIZA, 0.4f), 0.009f * u, c); drawCircle(p.luz(ACERO).copy(alpha = 0.6f), 0.004f * u, c + Offset(0.002f * u * L, -0.002f * u)) }
        // La boca de la polaina, con el trapo de los pies que cuelga por fuera.
        drawOval(p.sombra(C.CENIZA), Offset(b.q(-0.07f, 0.43f).x, b.q(0f, 0.445f).y), Size(0.135f * u, 0.03f * u))
        val trapo = Path().apply {
            moveTo(b.q(-0.065f, 0.44f)); cubicTo(b.q(-0.1f, 0.43f), b.q(-0.115f, 0.36f), b.q(-0.1f, 0.28f))
            lineTo(b.q(-0.07f, 0.29f)); cubicTo(b.q(-0.08f, 0.35f), b.q(-0.06f, 0.4f), b.q(-0.02f, 0.44f)); close()
        }
        drawPath(trapo, p.por(TRAPO, -0.2f * L))
        drawLine(p.sombra(TRAPO), b.q(-0.1f, 0.33f), b.q(-0.075f, 0.335f), 0.006f * u)
        filoCalido(p, perfil, b.q(-0.12f, 0f).x, b.q(0.17f, 0f).x, f)
        // ---- La otra, tumbada de canto delante de la primera: se le ve la suela de clavos, rota en la planta y remendada
        // con un trapo y cordel. Su eje va de talón (a la izquierda) a punta (a la derecha).
        val s = Eje(e.q(-0.17f, 0.046f), 1.5708f, u)
        // La suela vista de frente: (dónde a lo largo, medio ancho), en metros.
        val anchos = listOf(0.0f to 0.035f, 0.02f to 0.04f, 0.06f to 0.038f, 0.12f to 0.03f, 0.18f to 0.042f, 0.24f to 0.045f, 0.28f to 0.035f, 0.3f to 0.0f)
        val suela = poligono(anchos.map { s.q(-it.second, it.first) } + anchos.reversed().map { s.q(it.second, it.first) })
        // El empeine que asoma por un lado.
        drawPath(poligono(anchos.map { s.q(it.second * 1.15f + 0.012f, it.first) } + anchos.reversed().map { s.q(it.second * 0.6f, it.first) }), p.por(lerp(C.CUERO, C.MADERA, 0.3f), 0.3f))
        drawPath(suela, p.por(lerp(C.CUERO, C.CENIZA, 0.2f), 0.35f * L))
        clipPath(suela) {
            drawPath(suela, p.luz(lerp(C.CUERO, C.MADERA, 0.4f)).copy(alpha = 0.5f), style = Stroke(0.014f * u))
            mancha(s.q(0f, 0.1f), 0.05f * u, 0.08f * u, p.por(C.BARRO, 0.1f), 0.5f)
        }
        // Los clavos, por el canto y en el tacón.
        for (it in anchos.dropLast(1)) for (sg in listOf(-1f, 1f)) drawCircle(p.luz(ACERO), 0.006f * u, s.q(sg * (it.second - 0.012f), it.first + 0.01f))
        for ((x, y) in listOf(0f to 0.03f, -0.015f to 0.05f, 0.015f to 0.05f)) drawCircle(p.luz(ACERO), 0.006f * u, s.q(x, y))
        // El agujero de la planta, con el trapo que asoma.
        val ag = s.q(0.0f, 0.2f)
        drawOval(p.sombra(C.CENIZA), Offset(ag.x - 0.022f * u, ag.y - 0.03f * u), Size(0.044f * u, 0.06f * u))
        mancha(ag + Offset(0.004f * u, 0.004f * u), 0.016f * u, 0.022f * u, p.por(TRAPO, 0.2f), 0.95f)
        // El cordel que sujeta la suela, dos vueltas.
        for (y in listOf(0.13f, 0.155f)) drawLine(p.por(lerp(C.HOJA, C.LONA, 0.3f), 0.2f), s.q(-0.05f, y), s.q(0.05f, y + 0.01f), 0.007f * u, StrokeCap.Round)
        filoCalido(p, suela, s.q(-0.05f, 0.15f).x, s.q(0.05f, 0.15f).x, f)
    }

    // =================================================================
    //  Los chicos
    // =================================================================

    /** La bayoneta desnuda, de tres filos, con su cubo y el codo; detrás, la vaina de cuero negro con su contera. */
    private fun DrawScope.bayoneta(p: Pincel, o: Offset, k: Float, f: Float) {
        val pl = Plano(o + Offset(-1f * k, -1f * k), k, 0.12f, 0.45f)
        sombrita(p, o, 38f * k, 3f * k)
        // ---- La vaina, vacía, que cuelga por delante del rollo: cuero negro con su brocal y su contera de latón.
        val v0 = o + Offset(3f * k, -0.5f * k); val v1 = o + Offset(15f * k, 8f * k)
        tubo(p, listOf(v0, lerp(v0, v1, 0.5f) + Offset(0f, 0.8f * k), v1), listOf(1.6f * k, 1.4f * k, 1.0f * k), CHAROL, 0.7f)
        drawLine(p.luz(C.LATON), v0, lerp(v0, v1, 0.12f), 3.4f * k)
        drawLine(p.luz(C.LATON), lerp(v0, v1, 0.88f), v1, 2.2f * k, StrokeCap.Round)
        // ---- El cubo: un tubo que entra en la boca del fusil, con su ranura en zigzag y su anillo.
        val cubo = tubo(p, listOf(pl.q(-18f, -2f, 2f), pl.q(-9f, -2f, 2f)), listOf(2.4f * k, 2.4f * k), ACERO, 0.9f)
        drawPath(Path().apply { moveTo(pl.q(-17f, -2f, 2.2f)); lineTo(pl.q(-14f, -2f, 2.2f)); lineTo(pl.q(-13f, -2f, 3.4f)); lineTo(pl.q(-10.5f, -2f, 3.4f)) }, p.sombra(C.CENIZA), style = Stroke(0.6f * k))
        drawLine(p.por(ACERO, 0.5f), pl.q(-16.5f, -2f, 0f), pl.q(-16.5f, -2f, 4.4f), 1.1f * k)
        // ---- El codo y la hoja: de tres caras, una con luz y otra en sombra, con su vaceo y la punta fina.
        drawLine(p.por(ACERO, 0.1f), pl.q(-9.5f, -2f, 2.2f), pl.q(-8f, -2f, 3.2f), 1.6f * k, StrokeCap.Round)
        val b0 = pl.q(-8f, -2f, 3.2f); val punta = pl.q(21f, -1f, 1.6f)
        val ancho = 2.4f * k
        val arriba = poligono(b0 + Offset(0f, -ancho), punta, b0)
        val abajo = poligono(b0, punta, b0 + Offset(0f, ancho * 0.8f))
        val claro = lerp(ACERO, C.PLATA, 0.7f)
        drawPath(abajo, p.por(claro, -0.45f))
        drawPath(arriba, Brush.linearGradient(listOf(p.luz(lerp(claro, Color.White, 0.3f)), p.por(claro, 0.25f)), b0, punta))
        drawPath(Path().apply { addPath(arriba); addPath(abajo) }, p.sombra(C.HIERRO).copy(alpha = 0.7f), style = Stroke(0.3f * k))
        drawLine(p.sombra(C.HIERRO).copy(alpha = 0.6f), b0 + Offset(1f * k, -ancho * 0.55f), lerp(b0, punta, 0.75f) + Offset(0f, -ancho * 0.12f), 0.5f * k)
        drawLine(Color.White.copy(alpha = 0.55f * p.r.dia), lerp(b0, punta, 0.2f) + Offset(0f, -ancho * 0.25f), lerp(b0, punta, 0.55f) + Offset(0f, -ancho * 0.12f), 0.4f * k, StrokeCap.Round)
        // El orín, junto al codo.
        val az = Azar(Semilla.de("napoleon", "objeto", "bayoneta"))
        repeat(5) { drawCircle(p.por(lerp(C.MADERA, C.ROJO, 0.4f), -0.1f).copy(alpha = 0.6f), az.entre(0.3f, 0.6f) * k, lerp(b0, punta, az.entre(0.02f, 0.2f)) + Offset(0f, az.entre(-1f, 0.6f) * k)) }
        val hoja = Path().apply { addPath(arriba); addPath(abajo); addPath(cubo) }
        filoCalido(p, hoja, pl.q(-18f, 0f).x, pl.q(21f, 0f).x, f, 0.8f)
    }

    /** El chacó: el fieltro negro, el galón de cuero, la visera de charol, la placa del águila, la escarapela, los cordones y el pompón. */
    private fun DrawScope.chaco(p: Pincel, o: Offset, k: Float, f: Float, nieve: Float) {
        val L = lado(p)
        fun q(x: Float, y: Float) = Offset(o.x + x * k, o.y - y * k)
        sombrita(p, o, 26f * k, 30f * k)
        val alto = 25f; val abajo = 8.5f; val arriba = 10.5f
        val cuerpo = Path().apply { moveTo(q(-abajo, 0f)); lineTo(q(abajo, 0f)); cubicTo(q(abajo + 0.6f, 9f), q(arriba, 18f), q(arriba, alto)); lineTo(q(-arriba, alto)); cubicTo(q(-arriba, 18f), q(-abajo - 0.6f, 9f), q(-abajo, 0f)); close() }
        drawPath(cuerpo, Brush.horizontalGradient(if (L > 0f) listOf(p.por(FIELTRO, -0.7f), p.por(FIELTRO, 0f), p.por(FIELTRO, 0.65f), p.por(FIELTRO, 0.1f)) else listOf(p.por(FIELTRO, 0.1f), p.por(FIELTRO, 0.65f), p.por(FIELTRO, 0f), p.por(FIELTRO, -0.7f)), q(-arriba, 0f).x, q(arriba, 0f).x))
        clipPath(cuerpo) {
            // El fieltro gastado: rozaduras más claras; el galón de cuero arriba y abajo.
            mancha(q(4f * L, 15f), 3f * k, 6f * k, p.luz(lerp(FIELTRO, C.CAPOTE, 0.4f)), 0.35f)
            drawRect(p.por(CHAROL, 0.25f * L), Offset(q(-12f, 0f).x, q(0f, alto).y), Size(24f * k, 2.8f * k))
            drawRect(p.por(CHAROL, 0.15f * L), Offset(q(-12f, 0f).x, q(0f, 2.6f).y), Size(24f * k, 2.6f * k))
            drawLine(p.luz(CHAROL).copy(alpha = 0.6f), q(-12f, alto - 2.8f), q(12f, alto - 2.8f), 0.4f * k)
        }
        // La tapa de arriba, de cuero, en óvalo.
        drawOval(p.arriba(CHAROL), Offset(q(-arriba, 0f).x, q(0f, alto + 1.4f).y), Size(2f * arriba * k, 2.8f * k))
        if (nieve > 0.3f) drawOval(p.arriba(C.NIEVE).copy(alpha = nieve), Offset(q(-arriba + 0.6f, 0f).x, q(0f, alto + 1.8f).y), Size((2f * arriba - 1.2f) * k, 2.4f * k))
        // Las carrilleras de escamas de latón, a los lados, con su roseta.
        for (s in listOf(-1f, 1f)) {
            for (j in 0 until 5) drawCircle(p.por(C.LATON, if (s == L) 0.6f else -0.3f), 1.0f * k, q(s * (abajo + 0.2f - j * 0.15f), 7f - j * 1.5f))
            drawCircle(p.luz(C.LATON), 1.4f * k, q(s * (abajo + 0.4f), 8.3f))
        }
        // La visera de charol, que baja por delante, con su brillo.
        val visera = Path().apply { moveTo(q(-abajo - 0.3f, 1.4f)); cubicTo(q(-5f, -2.4f), q(5f, -2.4f), q(abajo + 0.3f, 1.4f)); cubicTo(q(4f, -0.4f), q(-4f, -0.4f), q(-abajo - 0.3f, 1.4f)); close() }
        drawPath(visera, p.por(CHAROL, -0.2f))
        drawPath(Path().apply { moveTo(q(-4f, -1.2f)); cubicTo(q(-1.5f, -1.7f), q(1.5f, -1.7f), q(3f, -1.3f)) }, p.luz(lerp(CHAROL, Color.White, 0.5f)).copy(alpha = 0.75f), style = Stroke(0.6f * k, cap = StrokeCap.Round))
        // Los cordones blancos, en guirnalda por delante, y la borla que cuelga.
        val cordon = Path().apply { moveTo(q(-9.8f, 18f)); cubicTo(q(-6f, 10f), q(-2f, 8.5f), q(0f, 9f)); cubicTo(q(3f, 9.5f), q(7f, 12f), q(9.6f, 18f)) }
        drawPath(cordon, p.sombra(C.CORREA), style = Stroke(1.3f * k, cap = StrokeCap.Round))
        drawPath(cordon, p.por(C.CORREA, 0.4f * L), style = Stroke(0.7f * k, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.1f * k, 0.5f * k))))
        drawLine(p.por(C.CORREA, 0.2f), q(9.6f, 18f), q(10.6f, 9f), 0.8f * k)
        drawPath(poligono(q(10f, 9.5f), q(11.6f, 9.5f), q(12f, 5.5f), q(9.6f, 5.5f)), p.por(C.CORREA, 0.1f))
        // La placa de latón: un rombo con el águila de alas abiertas, sin número.
        val pc = q(0f, 14.5f)
        val placa = poligono(pc + Offset(0f, -7.5f * k), pc + Offset(6f * k, 0f), pc + Offset(0f, 6.5f * k), pc + Offset(-6f * k, 0f))
        drawPath(placa, Brush.linearGradient(listOf(p.luz(lerp(C.LATON, Color.White, 0.2f)), p.por(C.LATON, -0.4f)), pc + Offset(-5f * k * L, -6f * k), pc + Offset(5f * k * L, 6f * k)))
        drawPath(placa, p.sombra(C.LATON), style = Stroke(0.35f * k))
        val ag = pc + Offset(0f, -1f * k)
        val aguila = Path().apply {
            moveTo(ag + Offset(-4.2f * k, -1.8f * k)); cubicTo(ag + Offset(-2.5f * k, -0.6f * k), ag + Offset(-1.2f * k, -0.4f * k), ag + Offset(-0.7f * k, 0.4f * k))
            lineTo(ag + Offset(-1.1f * k, 3.2f * k)); lineTo(ag + Offset(1.1f * k, 3.2f * k)); lineTo(ag + Offset(0.7f * k, 0.4f * k))
            cubicTo(ag + Offset(1.2f * k, -0.4f * k), ag + Offset(2.5f * k, -0.6f * k), ag + Offset(4.2f * k, -1.8f * k))
            cubicTo(ag + Offset(2.6f * k, 1.2f * k), ag + Offset(1.4f * k, 1f * k), ag + Offset(0.6f * k, -0.6f * k))
            lineTo(ag + Offset(0f, -1.8f * k)); lineTo(ag + Offset(-0.6f * k, -0.6f * k))
            cubicTo(ag + Offset(-1.4f * k, 1f * k), ag + Offset(-2.6f * k, 1.2f * k), ag + Offset(-4.2f * k, -1.8f * k)); close()
        }
        drawPath(aguila, p.sombra(lerp(C.LATON, C.CUERO, 0.35f)))
        drawCircle(p.sombra(lerp(C.LATON, C.CUERO, 0.35f)), 0.7f * k, ag + Offset(0f, -2.1f * k))
        drawCircle(Color.White.copy(alpha = 0.6f * p.r.dia), 0.5f * k, pc + Offset(-2f * k * L, -3f * k))
        // La escarapela de tres colores, encima de la placa, y el pompón rojo, arriba.
        val es = q(0f, 22.2f)
        drawCircle(p.por(C.ROJO, 0.2f), 2.3f * k, es); drawCircle(p.por(C.NIEVE, 0.1f), 1.55f * k, es); drawCircle(p.por(C.AZUL, 0.2f), 0.85f * k, es)
        drawLine(p.por(CHAROL, 0f), q(0f, alto + 0.5f), q(0f, alto + 2.6f), 1f * k)
        val po = q(0f, alto + 4.6f)
        drawCircle(p.por(C.ROJO, -0.3f), 2.9f * k, po)
        drawCircle(p.luz(lerp(C.ROJO, C.LONA, 0.3f)), 1.4f * k, po + Offset(0.9f * k * L, -0.9f * k))
        if (nieve > 0.3f) drawOval(p.arriba(C.NIEVE).copy(alpha = nieve), Offset(po.x - 2.2f * k, po.y - 3.2f * k), Size(4.4f * k, 1.8f * k))
        filoCalido(p, cuerpo, q(-arriba, 0f).x, q(arriba, 0f).x, f)
    }

    /** La cantimplora: una calabaza de dos senos forrada de cuero cosido, con su tapón de corcho y su correa. */
    private fun DrawScope.cantimplora(p: Pincel, o: Offset, k: Float, f: Float) {
        val L = lado(p)
        val e = Eje(o + Offset(-2f * k, 0f), 0.14f, k)
        sombrita(p, o + Offset(-1f * k, 0f), 24f * k, 26f * k)
        // ---- La correa: sale del cuello y cae en lazo por el rollo.
        val correa = Path().apply { moveTo(e.q(-1.8f, 21f)); cubicTo(e.q(-9f, 22f), e.q(-14f, 10f), e.q(-11f, 2f)); cubicTo(e.q(-8f, -1.5f), e.q(4f, -1.8f), e.q(12f, 1f)) }
        drawPath(correa, p.sombra(C.CUERO), style = Stroke(2.2f * k, cap = StrokeCap.Round))
        drawPath(correa, p.por(lerp(C.CUERO, C.MADERA, 0.4f), 0.3f * L), style = Stroke(1.3f * k, cap = StrokeCap.Round))
        // ---- La calabaza: el seno grande abajo, el chico arriba, la cintura en medio.
        val forma = Path().apply {
            moveTo(e.q(0f, 0f)); cubicTo(e.q(8.5f, 0f), e.q(9.5f, 8.5f), e.q(6.2f, 11.5f)); cubicTo(e.q(4.3f, 13f), e.q(6.2f, 14.5f), e.q(5.3f, 17.5f))
            cubicTo(e.q(4.6f, 19.5f), e.q(2.6f, 20.5f), e.q(1.8f, 21f)); lineTo(e.q(-1.8f, 21f))
            cubicTo(e.q(-2.6f, 20.5f), e.q(-4.6f, 19.5f), e.q(-5.3f, 17.5f)); cubicTo(e.q(-6.2f, 14.5f), e.q(-4.3f, 13f), e.q(-6.2f, 11.5f))
            cubicTo(e.q(-9.5f, 8.5f), e.q(-8.5f, 0f), e.q(0f, 0f)); close()
        }
        val cuero = CALABAZA
        drawPath(forma, Brush.linearGradient(if (L > 0f) listOf(p.por(cuero, -0.75f), p.por(cuero, 0f), p.por(cuero, 0.65f)) else listOf(p.por(cuero, 0.65f), p.por(cuero, 0f), p.por(cuero, -0.75f)), e.q(-9f, 8f), e.q(9f, 8f)))
        clipPath(forma) {
            // El brillo de cada seno, la costura del medio con sus puntadas, la mugre de abajo.
            drawCircle(p.luz(lerp(cuero, C.LONA, 0.35f)).copy(alpha = 0.6f), 2.6f * k, e.q(3.4f * L, 7.5f))
            drawCircle(p.luz(lerp(cuero, C.LONA, 0.35f)).copy(alpha = 0.5f), 1.4f * k, e.q(2.2f * L, 17f))
            val costura = Path().apply { moveTo(e.q(0.5f, 0f)); cubicTo(e.q(1.2f, 6f), e.q(0.6f, 14f), e.q(0.2f, 21f)) }
            drawPath(costura, p.sombra(C.CUERO), style = Stroke(0.6f * k))
            drawPath(costura, p.luz(C.LONA).copy(alpha = 0.7f), style = Stroke(0.4f * k, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.7f * k, 0.8f * k))))
            drawLine(p.sombra(C.CUERO).copy(alpha = 0.6f), e.q(-7f, 11.6f), e.q(7f, 11.6f), 1.0f * k)
            mancha(e.q(0f, 1.5f), 6f * k, 2f * k, p.sombra(C.BARRO), 0.5f)
        }
        // El tapón de corcho, con su cordel.
        drawPath(poligono(e.q(-1.6f, 20.6f), e.q(1.6f, 20.6f), e.q(1.3f, 24f), e.q(-1.3f, 24f)), p.por(lerp(C.MADERA, C.HOJA, 0.5f), 0.3f * L))
        drawOval(p.arriba(lerp(C.MADERA, C.HOJA, 0.5f)), Offset(e.q(-1.3f, 24f).x, e.q(0f, 24.5f).y), Size(2.6f * k, 0.9f * k))
        drawLine(p.medio(C.LONA), e.q(-1.6f, 21.5f), e.q(1.6f, 21.2f), 0.5f * k)
        filoCalido(p, forma, e.q(-9f, 8f).x, e.q(9f, 8f).x, f)
    }

    /** Una galleta cuadrada de [lado2]·2 de lado y [grueso] de alto, en el plano [pl], con sus agujeros y su canto. */
    private fun DrawScope.galleta(p: Pincel, pl: Plano, cx: Float, cz: Float, h: Float, lado2: Float, inclina: Float, grueso: Float, sinEsquina: Boolean) {
        fun c(x: Float, z: Float) = pl.q(cx + x, cz + z, h + inclina * z + grueso)
        fun b(x: Float, z: Float) = pl.q(cx + x, cz + z, h + inclina * z)
        val s = lado2
        val cara = if (sinEsquina) listOf(c(-s, -s), c(s * 0.2f, -s), c(s * 0.45f, -s * 0.2f), c(s * 0.1f, s * 0.15f), c(s, s * 0.4f), c(s, s), c(-s, s)) else listOf(c(-s, -s), c(s, -s), c(s, s), c(-s, s))
        // El canto de delante y el del lado.
        drawPath(poligono(b(-s, -s), if (sinEsquina) b(s * 0.2f, -s) else b(s, -s), if (sinEsquina) c(s * 0.2f, -s) else c(s, -s), c(-s, -s)), p.por(lerp(GALLETA, C.MADERA, 0.3f), -0.35f))
        if (!sinEsquina) drawPath(poligono(b(s, -s), b(s, s), c(s, s), c(s, -s)), p.por(lerp(GALLETA, C.MADERA, 0.3f), if (p.solALaDerecha) 0.3f else -0.6f))
        else drawPath(poligono(b(s * 0.2f, -s), b(s * 0.45f, -s * 0.2f), c(s * 0.45f, -s * 0.2f), c(s * 0.2f, -s)), p.por(lerp(GALLETA, C.HOJA, 0.4f), 0.1f))
        val tapa = poligono(cara)
        drawPath(tapa, p.arriba(GALLETA))
        clipPath(tapa) {
            // Lo más tostado en los bordes, y los agujeros de la cocción, en cuadrícula.
            drawPath(tapa, p.sombra(lerp(GALLETA, C.MADERA, 0.5f)).copy(alpha = 0.5f), style = Stroke(1.4f * pl.k))
            for (i in -1..1) for (j in -1..1) drawCircle(p.sombra(lerp(GALLETA, C.CUERO, 0.4f)).copy(alpha = 0.8f), 0.55f * pl.k, c(i * s * 0.55f, j * s * 0.55f))
        }
    }

    /** Tres galletas de munición sobre un trapo: dos enteras, una encima ladeada y otra partida con sus migas. */
    private fun DrawScope.galletas(p: Pincel, o: Offset, k: Float, f: Float) {
        val pl = Plano(o, k, -0.2f, 0.5f)
        sombrita(p, o, 34f * k, 5f * k)
        // El trapo, con un pico que cuelga.
        val trapo = Path().apply { moveTo(pl.q(-16f, -6f)); lineTo(pl.q(13f, -9f)); lineTo(pl.q(16f, 8f)); lineTo(pl.q(-13f, 10f)); close() }
        drawPath(trapo, p.arriba(TRAPO))
        drawPath(poligono(pl.q(-16f, -6f), pl.q(-6f, -7f), pl.q(-11f, -7f, -4f)), p.por(TRAPO, -0.4f))
        clipPath(trapo) { for (z in listOf(-3f, 3f)) drawLine(p.medio(lerp(TRAPO, C.ROJO, 0.4f)), pl.q(-18f, z), pl.q(18f, z - 1f), 0.8f * k) }
        galleta(p, pl, 5f, 1f, 0f, 6.5f, 0f, 2.6f, false)
        galleta(p, pl, -6f, 2f, 0f, 6.5f, 0f, 2.6f, true)
        galleta(p, pl, 0f, -2f, 2.4f, 6.5f, 0.12f, 2.6f, false)
        // El pedazo partido, de canto, y las migas.
        val tr = pl.q(-12f, -5f, 0f)
        drawPath(poligono(tr, tr + Offset(6f * k, -1f * k), tr + Offset(5f * k, -5f * k), tr + Offset(-0.5f * k, -3.5f * k)), p.por(lerp(GALLETA, C.HOJA, 0.3f), 0.25f * (if (p.solALaDerecha) 1f else -1f)))
        val az = Azar(Semilla.de("napoleon", "objeto", "galleta"))
        repeat(9) { drawCircle(p.arriba(GALLETA), az.entre(0.3f, 0.7f) * k, pl.q(az.entre(-16f, -2f), az.entre(-8f, -2f))) }
        if (p.calida > 0.05f) drawPath(trapo, p.lumbre.copy(alpha = 0.15f * p.calida), blendMode = BlendMode.Plus)
    }

    /** La pipa corta de barro blanco, con la cazoleta negra de quemada; detrás, la bolsa de tabaco tendida, con hebras sueltas. */
    private fun DrawScope.pipa(p: Pincel, o: Offset, k: Float, f: Float) {
        val L = lado(p)
        sombrita(p, o, 30f * k, 8f * k)
        // ---- La bolsa de tabaco, detrás a la derecha: cuero blando, chata, fruncida en la boca con su cordón.
        val bc = o + Offset(5f * k, -1.6f * k)
        val bolsa = Path().apply {
            moveTo(bc + Offset(-6f * k, 0f)); cubicTo(bc + Offset(-6.5f * k, -3.5f * k), bc + Offset(-2f * k, -4.6f * k), bc + Offset(3f * k, -3.8f * k))
            lineTo(bc + Offset(6.5f * k, -2.6f * k)); lineTo(bc + Offset(6.5f * k, -0.4f * k)); cubicTo(bc + Offset(3f * k, 0.6f * k), bc + Offset(-3f * k, 0.8f * k), bc + Offset(-6f * k, 0f)); close()
        }
        drawPath(bolsa, Brush.verticalGradient(listOf(p.por(C.PIEL, 0.5f), p.por(C.PIEL, -0.6f)), bc.y - 4.6f * k, bc.y + 0.6f * k))
        clipPath(bolsa) { for (x in listOf(3.5f, 4.5f, 5.5f)) drawLine(p.sombra(C.PIEL).copy(alpha = 0.7f), bc + Offset(x * k, -3.4f * k), bc + Offset((x - 2.5f) * k, -1.5f * k), 0.4f * k) }
        drawLine(p.por(C.CORREA, 0.2f), bc + Offset(5.2f * k, -3.3f * k), bc + Offset(5.2f * k, -0.2f * k), 0.8f * k, StrokeCap.Round)
        drawPath(Path().apply { moveTo(bc + Offset(5.2f * k, -1.8f * k)); cubicTo(bc + Offset(8f * k, -2f * k), bc + Offset(9.5f * k, 0f), bc + Offset(9f * k, 2f * k)) }, p.por(C.CORREA, 0f), style = Stroke(0.5f * k))
        val az = Azar(Semilla.de("napoleon", "objeto", "pipa"))
        repeat(14) { val c = bc + Offset(az.entre(6f, 11f) * k, az.entre(-1.5f, 0.5f) * k); drawLine(p.por(lerp(C.MADERA, C.CUERO, 0.3f), 0.2f), c, c + Offset(az.entre(-1.2f, 1.2f) * k, az.entre(-0.4f, 0.4f) * k), 0.35f * k) }
        // ---- La pipa, delante, tendida en el rollo: la caña larga hacia la derecha y la cazoleta que se abre hacia
        // atrás, a la izquierda, como en las de barro.
        val cz = o + Offset(-8f * k, -0.5f * k)
        val eb = Eje(cz, -0.32f, k)
        val trazo = listOf(cz + Offset(1.2f * k, -1.2f * k), cz + Offset(10f * k, -2.2f * k), cz + Offset(19f * k, -1.4f * k))
        drawPath(Path().apply { moveTo(trazo[0]); cubicTo(trazo[1], trazo[1], trazo[2]) }, p.sombra(C.CENIZA).copy(alpha = 0.7f), style = Stroke(2.7f * k, cap = StrokeCap.Round))
        val cana = tubo(p, trazo, listOf(1.2f * k, 1.0f * k, 0.85f * k), ARCILLA, 0.95f)
        drawCircle(p.sombra(lerp(ARCILLA, C.CENIZA, 0.4f)), 0.7f * k, trazo.last())
        val cazoleta = Path().apply {
            moveTo(eb.q(-3.3f, 10.5f)); lineTo(eb.q(3.1f, 10.5f))
            cubicTo(eb.q(3.1f, 5f), eb.q(2.4f, 1.2f), eb.q(0.6f, 0f))
            cubicTo(eb.q(-1.4f, 0.4f), eb.q(-3f, 5f), eb.q(-3.3f, 10.5f)); close()
        }
        drawPath(cazoleta, p.sombra(C.CENIZA).copy(alpha = 0.7f), style = Stroke(0.7f * k))
        drawPath(cazoleta, Brush.horizontalGradient(if (L > 0f) listOf(p.por(ARCILLA, -0.6f), p.por(ARCILLA, 0.2f), p.luz(ARCILLA)) else listOf(p.luz(ARCILLA), p.por(ARCILLA, 0.2f), p.por(ARCILLA, -0.6f)), cz.x - 6f * k, cz.x + 3f * k))
        clipPath(cazoleta) { drawPath(poligono(eb.q(-4f, 10.6f), eb.q(4f, 10.6f), eb.q(4f, 7f), eb.q(-4f, 7f)), p.sombra(C.CENIZA).copy(alpha = 0.7f)) }
        val boca = eb.q(-0.1f, 10.5f)
        drawOval(p.sombra(C.CENIZA), Offset(boca.x - 3.2f * k, boca.y - 0.9f * k), Size(6.4f * k, 1.8f * k))
        drawLine(p.luz(lerp(ARCILLA, Color.White, 0.4f)).copy(alpha = 0.8f), eb.q(1.6f * L, 7.5f), eb.q(1.2f * L, 3f), 0.6f * k, StrokeCap.Round)
        // De noche, la brasa del tabaco en la boca de la cazoleta.
        if (p.r.dia < 0.4f) drawCircle(lerp(p.lumbre, Color(0xFFFF6A2A), 0.4f).copy(alpha = 0.8f * (1f - p.r.dia)), 1.1f * k, boca, blendMode = BlendMode.Plus)
        filoCalido(p, Path().apply { addPath(cazoleta); addPath(cana) }, cz.x - 6f * k, cz.x + 20f * k, f, 0.7f)
    }
    /** La carta: una abierta, con sus renglones y sus dobleces, y encima otra cerrada, con las señas y el lacre. */
    private fun DrawScope.carta(p: Pincel, o: Offset, k: Float, f: Float) {
        val az = Azar(Semilla.de("napoleon", "objeto", "carta"))
        sombrita(p, o, 34f * k, 3f * k)
        // ---- La abierta, debajo: tres paneles con sus dobleces que se levantan un poco.
        val pl = Plano(o + Offset(-1f * k, 1f * k), k, 0.12f, 0.6f)
        val zs = listOf(-9f, -3f, 3f, 9f); val hs = listOf(0.3f, 1.3f, 0.3f, 1.1f)
        val x0 = -13f; val x1 = 11f
        for (i in 0 until 3) {
            val panel = poligono(pl.q(x0, zs[i], hs[i]), pl.q(x1, zs[i], hs[i]), pl.q(x1, zs[i + 1], hs[i + 1]), pl.q(x0, zs[i + 1], hs[i + 1]))
            val tt = if (hs[i + 1] > hs[i]) 0.15f else 0.5f
            drawPath(panel, p.por(PAPEL, tt))
            clipPath(panel) {
                mancha(pl.q(az.entre(-8f, 6f), (zs[i] + zs[i + 1]) / 2f, 1f), 5f * k, 2f * k, p.por(lerp(PAPEL, C.BARRO, 0.35f), tt), 0.3f)
                var z = zs[i] + 1f
                while (z < zs[i + 1] - 0.6f) { val g = (z - zs[i]) / (zs[i + 1] - zs[i]); val hh = hs[i] + (hs[i + 1] - hs[i]) * g; renglon(pl.q(x0 + 1.6f, z, hh), pl.q(x1 - 1.6f, z, hh), p.por(TINTA, tt).copy(alpha = 0.7f), 0.38f * k, az); z += 1.5f }
            }
        }
        drawPath(poligono(pl.q(x0, zs[0], hs[0]), pl.q(x1, zs[0], hs[0]), pl.q(x1, zs[0], 0f), pl.q(x0, zs[0], 0f)), p.sombra(PAPEL).copy(alpha = 0.5f))
        // ---- La cerrada, encima y ladeada: el pliego doblado, las señas en tres renglones y el lacre rojo.
        val pc = Plano(o + Offset(4f * k, -1.5f * k), k, -0.3f, 0.55f)
        val h = 1.8f
        val sobre = listOf(pc.q(-8f, -5f, h), pc.q(8f, -5f, h), pc.q(8f, 5f, h), pc.q(-8f, 5f, h))
        drawPath(poligono(sobre[0], sobre[1], sobre[1] + Offset(0f, 1.2f * k), sobre[0] + Offset(0f, 1.2f * k)), p.por(lerp(PAPEL, C.HOJA, 0.3f), -0.4f))
        val cerrada = poligono(sobre)
        drawPath(cerrada, p.por(lerp(PAPEL, C.LONA, 0.3f), 0.45f))
        clipPath(cerrada) {
            for (i in 0..2) renglon(pc.q(-3f + i * 0.6f, 1.5f - i * 1.7f, h), pc.q(6f, 1.5f - i * 1.7f, h), p.por(TINTA, 0.2f).copy(alpha = 0.8f), 0.42f * k, az, 0.85f)
            drawLine(p.sombra(PAPEL).copy(alpha = 0.4f), pc.q(-8f, 0f, h), pc.q(-2.5f, 5f, h), 0.4f * k)
            mancha(pc.q(5f, -3f, h), 3f * k, 1.5f * k, p.sombra(C.BARRO), 0.35f)
        }
        val lacre = pc.q(-5f, 2.5f, h)
        drawCircle(p.por(C.ROJO, -0.2f), 1.9f * k, lacre)
        drawCircle(p.sombra(C.ROJO), 1.1f * k, lacre, style = Stroke(0.35f * k))
        drawCircle(p.luz(lerp(C.ROJO, C.LONA, 0.3f)), 0.5f * k, lacre + Offset(0.6f * k * lado(p), -0.6f * k))
        if (p.calida > 0.05f) drawPath(Path().apply { addPath(cerrada); addPath(poligono(pl.q(x0, zs[0], hs[0]), pl.q(x1, zs[0], hs[0]), pl.q(x1, zs[3], hs[3]), pl.q(x0, zs[3], hs[3]))) }, p.lumbre.copy(alpha = 0.2f * p.calida), blendMode = BlendMode.Plus)
    }

    /** La cuchara de estaño, grande, cruzada sobre su escudilla de hojalata con un resto de sopa. */
    private fun DrawScope.cuchara(p: Pincel, o: Offset, k: Float, f: Float) {
        val L = lado(p)
        val pl = Plano(o, k, 0f, 0.42f)
        sombrita(p, o, 30f * k, 4f * k)
        // ---- La escudilla: el canto, el borde con luz, el fondo con la sopa de harina.
        val c = pl.q(-3f, 1f)
        val rx = 10f * k; val ry = rx * 0.42f; val hondo = 3.6f * k
        val cuerpo = Path().apply { moveTo(c + Offset(-rx, -hondo)); lineTo(c + Offset(rx, -hondo)); cubicTo(c + Offset(rx * 0.95f, -hondo * 0.2f), c + Offset(rx * 0.6f, ry * 0.9f), c + Offset(0f, ry * 0.9f)); cubicTo(c + Offset(-rx * 0.6f, ry * 0.9f), c + Offset(-rx * 0.95f, -hondo * 0.2f), c + Offset(-rx, -hondo)); close() }
        drawPath(cuerpo, Brush.horizontalGradient(if (L > 0f) listOf(p.por(ESTANO, -0.7f), p.por(ESTANO, 0.1f), p.luz(ESTANO), p.por(ESTANO, -0.1f)) else listOf(p.por(ESTANO, -0.1f), p.luz(ESTANO), p.por(ESTANO, 0.1f), p.por(ESTANO, -0.7f)), c.x - rx, c.x + rx))
        clipPath(cuerpo) { repeat(3) { drawLine(p.sombra(ESTANO).copy(alpha = 0.5f), c + Offset((-5f + it * 4f) * k, -hondo), c + Offset((-4f + it * 4f) * k, ry * 0.6f), 0.35f * k) } }
        val boca = Offset(c.x - rx, c.y - hondo - ry)
        drawOval(p.por(ESTANO, -0.5f), boca, Size(2f * rx, 2f * ry))
        drawOval(p.por(lerp(C.LONA, C.HOJA, 0.35f), -0.1f), Offset(c.x - rx * 0.75f, c.y - hondo - ry * 0.55f), Size(1.5f * rx, 1.2f * ry))
        drawOval(p.luz(lerp(ESTANO, Color.White, 0.3f)), boca, Size(2f * rx, 2f * ry), style = Stroke(0.7f * k))
        // ---- La cuchara: la pala honda dentro de la escudilla, el mango que sale por el borde y descansa en el rollo.
        val pa = c + Offset(-1.5f * k, -hondo - 0.4f * k)
        val pala = Path().apply { moveTo(pa + Offset(-4.4f * k, 0f)); cubicTo(pa + Offset(-4.4f * k, -2.8f * k), pa + Offset(2.6f * k, -3.2f * k), pa + Offset(3.6f * k, -0.6f * k)); cubicTo(pa + Offset(3.2f * k, 2.2f * k), pa + Offset(-4.2f * k, 2.4f * k), pa + Offset(-4.4f * k, 0f)); close() }
        drawPath(pala, Brush.linearGradient(listOf(p.sombra(ESTANO), p.por(ESTANO, 0.3f), p.luz(lerp(ESTANO, Color.White, 0.4f))), pa + Offset(-4f * k * L, 2f * k), pa + Offset(3f * k * L, -2f * k)))
        drawOval(p.luz(Color.White).copy(alpha = 0.6f * (0.3f + 0.7f * p.r.dia)), Offset(pa.x + (0.2f * L - 1f) * k, pa.y - 1.6f * k), Size(2.2f * k, 0.9f * k))
        val mango = listOf(pa + Offset(3.4f * k, -0.8f * k), c + Offset(rx + 1f * k, -hondo - 2.2f * k), c + Offset(rx + 9f * k, -hondo + 0.2f * k))
        val m = tubo(p, mango, listOf(0.7f * k, 0.75f * k, 1.25f * k), ESTANO, 0.9f)
        drawCircle(p.por(ESTANO, 0.2f), 1.3f * k, mango.last())
        filoCalido(p, Path().apply { addPath(cuerpo); addPath(pala); addPath(m) }, c.x - rx, c.x + rx + 10f * k, f, 0.6f)
    }
}
