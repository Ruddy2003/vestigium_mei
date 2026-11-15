package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los doce objetos del día de Bat, iguales en los dos cuadros (el valle y el campamento de verano).
 * Cada cuadro dice dónde: los chicos van sobre su soporte (el cofre o el fieltro);
 * los grandes van de pie o apoyados en la tienda o en la carreta.
 *
 * - **Grandes** ([GRANDES], de 45 a 80 unidades de alto a escala 1, como la tinaja y las ánforas de Helénico): el arco
 *   con su aljaba, la pértiga del lazo, la silla en su caballete, la tienda (un tramo de celosía a medio plegar, un rollo
 *   de fieltro y unas varas del techo), el violín de cabeza de caballo con su arco, el abrigo colgado y el cesto del
 *   argal con su horquilla. Pisan en `pie` y se recuestan hacia `apoyo`; el abrigo cuelga de `apoyo`.
 * - **Chicos** (de 18 a 34 unidades a escala 1, como lo que hay sobre el banco de Helénico): el odre de la silla, el
 *   cuchillo en su funda con los palillos, el eslabón en su bolsa con el pedernal, el cuenco con su leche y la copa de
 *   plata. Van sobre el soporte, con su sombra corta.
 *
 * **[Diseño propio]**: el objeto `ger` no es la tienda del cuadro (esa ya está): es lo que queda a la vista cuando se
 * arma o se repara una, un tramo de celosía, un rollo de fieltro atado y unas varas del techo. El `odre` del día es el
 * pequeño de cuero que se lleva en la silla («el odre que lleva en la silla»), no el grande del airag, que el cuadro de
 * verano ya tiene colgado junto a la puerta. La silla descansa en un caballete; el abrigo, colgado de un clavo o de una
 * cuerda; la inscripción de la copa es una franja de rayitas, sin letras.
 */
internal object ObjetosMongol {
    private val C = ColorMongol

    /** Los doce objetos del calendario de Bat (`AnioG4.kt`). */
    val TODOS = listOf("arco", "lazo", "silla", "ger", "odre", "cuchillo", "eslabon", "morin_khuur", "cuenco", "deel", "argal", "copa")

    /** Los que no caben sobre el soporte: van de pie o apoyados. */
    val GRANDES = setOf("arco", "lazo", "silla", "ger", "morin_khuur", "deel", "argal")

    /** Unidades del lienzo por metro, a escala 1 (la del primer plano de los dos cuadros). */
    const val METRO = 60f

    /** Si el objeto de hoy es de los grandes (no cabe sobre el soporte y va apoyado). */
    fun esGrande(m: Momento): Boolean = m.ficha.objeto in GRANDES

    /** Si [id] es de los grandes. */
    fun esGrande(id: String?): Boolean = id in GRANDES

    /**
     * El objeto chico de hoy, sobre el soporte. No pinta nada si hoy no hay objeto o es de los grandes.
     * [sobre] es el centro de la cara de arriba del soporte; [escala] 1 es el tamaño de primer plano.
     */
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) = chico(m.ficha.objeto, p, sobre, escala)

    /** El objeto chico [id] sobre el soporte (lo mismo que [sobreElSoporte], sin el momento: para el muestrario y las pruebas). */
    fun DrawScope.chico(id: String?, p: Pincel, sobre: Offset, escala: Float = 1f) {
        if (id == null || id in GRANDES || id !in TODOS) return
        val k = escala
        when (id) {
            "odre" -> odre(p, sobre, k)
            "cuchillo" -> cuchillo(p, sobre, k)
            "eslabon" -> eslabon(p, sobre, k)
            "cuenco" -> cuenco(p, sobre, k)
            "copa" -> copa(p, sobre, k)
        }
    }

    /**
     * El objeto grande de hoy: pisa en [pie] y se recuesta en [apoyo], un punto alto de la tienda o de la carreta.
     * Devuelve null si hoy no hay objeto o es de los chicos.
     */
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? = grande(m.ficha.objeto, p, pie, apoyo, escala)

    /** El objeto grande [id] (lo mismo que el de arriba, sin el momento). */
    fun grande(id: String?, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? {
        if (id == null || id !in GRANDES) return null
        val u = METRO * escala
        // Cuánto se recuesta: hacia el apoyo, sin pasar de un cuarto de vuelta pequeño (las cosas apoyadas no se tumban).
        val d = apoyo - pie
        val tope = if (id == "lazo" || id == "ger") 0.42f else 0.26f
        val ang = if (d.getDistance() < 1f) 0f else atan2(d.x, -d.y).coerceIn(-tope, tope)
        val e = Eje(pie, ang, u)
        return when (id) {
            "arco" -> Cosa(siluetaArco(e), pie) { arco(p, e) }
            "lazo" -> Cosa(siluetaLazo(e), pie) { lazo(p, e) }
            "silla" -> { val e0 = Eje(pie, 0f, u); Cosa(siluetaSilla(e0), pie) { silla(p, e0, if (d.x >= 0f) 1f else -1f) } }
            "ger" -> Cosa(siluetaGer(e), pie) { ger(p, e) }
            "morin_khuur" -> Cosa(siluetaViolin(e), pie) { violin(p, e) }
            "deel" -> { val colgado = Colgado(pie, apoyo, u); Cosa(siluetaDeel(colgado), pie) { deel(p, colgado) } }
            "argal" -> { val e0 = Eje(pie, ang * 0.25f, u); Cosa(siluetaArgal(e0), pie) { argal(p, e0, if (d.x >= 0f) 1f else -1f) } }
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

    private fun lado(p: Pincel) = if (p.solALaDerecha) 1f else -1f

    /**
     * Un palo o un tubo que sigue los [puntos] con su medio ancho en cada uno (en unidades): oscuro del lado de la sombra,
     * el color propio en medio y un filo de luz del lado del sol. De noche, con una lumbre cerca, el filo es cálido.
     */
    private fun DrawScope.tubo(p: Pincel, puntos: List<Offset>, anchos: List<Float>, color: Color, brillo: Float = 0.75f, filo: Boolean = true) {
        if (puntos.size < 2) return
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
            if (filo) for (i in 0 until puntos.size - 1) {
                val w = (anchos[i] + anchos[i + 1]) * 0.22f
                if (w > 0.18f) drawLine(p.luz(lerp(color, C.FIELTRO, 0.25f)).copy(alpha = brillo), puntos[i] + Offset(L * anchos[i] * 0.62f, 0f), puntos[i + 1] + Offset(L * anchos[i + 1] * 0.62f, 0f), w, StrokeCap.Round)
            }
        }
    }

    /** Un filo de luz cálida, de noche, del lado de la lumbre (que el pincel trae con su `calida`), sobre lo que es oscuro. */
    private fun DrawScope.filoCalido(p: Pincel, forma: Path, x0: Float, x1: Float) {
        if (p.calida < 0.03f) return
        val a = p.lumbre.copy(alpha = (0.45f * p.calida).coerceIn(0f, 1f))
        clipPath(forma) { drawRect(Brush.horizontalGradient(listOf(a, Color.Transparent, Color.Transparent, a.copy(alpha = a.alpha * 0.4f)), x0, x1), Offset(x0, -400f), Size(x1 - x0, 1200f), blendMode = BlendMode.Plus) }
    }

    /** La sombra corta de un objeto chico sobre la tapa: tendida hacia el lado contrario al sol, baja. */
    private fun DrawScope.sombrita(p: Pincel, pie: Offset, ancho: Float, alto: Float) {
        val r = p.r; if (r.fuerza < 0.05f) return
        val d = (r.ax * alto * 0.7f).coerceIn(-ancho * 1.2f, ancho * 1.2f)
        drawOval(r.color, Offset(minOf(pie.x - ancho / 2f, pie.x - ancho / 2f + d), pie.y - ancho * 0.1f), Size(ancho + abs(d), ancho * 0.18f), alpha = (r.fuerza * 0.6f).coerceIn(0f, 1f), blendMode = BlendMode.Multiply)
    }

    /** La sombra de contacto de algo que se apoya: una mancha oscura justo debajo, que hay también sin sol. */
    private fun DrawScope.contacto(p: Pincel, c: Offset, ancho: Float, alto: Float, fuerza: Float = 0.45f) {
        drawOval(p.sombra(C.HOLLIN).copy(alpha = fuerza), Offset(c.x - ancho / 2f, c.y - alto / 2f), Size(ancho, alto))
    }

    // =================================================================
    //  El arco y su aljaba
    // =================================================================

    /** La línea del arco montado, en metros: las orejas vueltas hacia delante, el puente donde apoya la cuerda y la empuñadura. */
    private val ARCO = listOf(
        Triple(0.075f, 0.02f, 0.010f), Triple(0.03f, 0.08f, 0.013f), Triple(-0.005f, 0.15f, 0.016f), Triple(0.04f, 0.3f, 0.019f), Triple(0.095f, 0.46f, 0.021f),
        Triple(0.118f, 0.63f, 0.024f), Triple(0.098f, 0.8f, 0.021f), Triple(0.046f, 0.96f, 0.019f), Triple(0.0f, 1.11f, 0.016f), Triple(0.035f, 1.18f, 0.013f), Triple(0.08f, 1.24f, 0.010f),
    )

    private fun siluetaArco(e: Eje): Path {
        val aljaba = e.girado(0.09f, e.q(0.22f, 0f))
        return Path().apply {
            addPath(poligono(ARCO.map { e.q(it.first - 0.03f, it.second) } + ARCO.reversed().map { e.q(it.first + 0.03f, it.second) }))
            addPath(poligono(listOf(aljaba.q(-0.05f, 0f), aljaba.q(0.05f, 0f), aljaba.q(0.1f, 0.72f), aljaba.q(-0.09f, 0.95f), aljaba.q(-0.08f, 0.4f))))
        }
    }

    private fun DrawScope.arco(p: Pincel, e: Eje) {
        val L = lado(p); val u = e.u
        // ---- La aljaba, detrás y un poco más tumbada: cuero rojo con sus flejes de latón, y las flechas que asoman.
        val al = e.girado(0.09f, e.q(0.22f, 0f))
        contacto(p, al.q(0f, 0f), 0.16f * u, 0.05f * u)
        contacto(p, e.q(0.07f, 0.01f), 0.1f * u, 0.04f * u)
        val cuero = lerp(C.CUERO, C.ROJO, 0.45f)
        // Las flechas: astiles de sauce, las plumas grises con su franja oscura y el culatín.
        for (j in 0 until 6) {
            val x = -0.06f + j * 0.026f; val alto = 0.92f + (j % 3) * 0.045f
            val a = al.q(x, 0.6f); val b = al.q(x + 0.015f * (j - 2.5f), alto + 0.1f)
            drawLine(p.por(lerp(C.MADERA, C.FIELTRO, 0.3f), if (j % 2 == 0) 0.4f else -0.2f), a, b, 0.012f * u, StrokeCap.Round)
            val d = (b - a) / (b - a).getDistance().coerceAtLeast(0.01f); val n = Offset(-d.y, d.x)
            val f0 = b - d * (0.15f * u); val f1 = b - d * (0.02f * u)
            val pluma = if (j % 2 == 0) lerp(C.PIEDRA, C.FIELTRO, 0.6f) else lerp(C.PIEDRA, C.HOLLIN, 0.3f)
            for (s in listOf(-1f, 1f)) drawPath(poligono(f0, f1, f1 + n * (s * 0.018f * u), f0 + n * (s * 0.028f * u) + d * (0.02f * u)), p.por(pluma, s * L * 0.5f))
            drawLine(p.medio(C.HOLLIN).copy(alpha = 0.7f), f0 + d * (0.05f * u) - n * (0.022f * u), f0 + d * (0.05f * u) + n * (0.022f * u), 0.012f * u)
            drawLine(p.medio(C.ROJO), b, b + d * (0.015f * u), 0.014f * u, StrokeCap.Round)
        }
        val estuche = Path().apply {
            moveTo(al.q(-0.04f, 0.0f)); cubicTo(al.q(0.03f, -0.015f), al.q(0.06f, 0.02f), al.q(0.07f, 0.12f))
            cubicTo(al.q(0.09f, 0.35f), al.q(0.1f, 0.55f), al.q(0.1f, 0.7f)); lineTo(al.q(-0.09f, 0.78f))
            cubicTo(al.q(-0.085f, 0.55f), al.q(-0.08f, 0.3f), al.q(-0.06f, 0.1f)); close()
        }
        drawPath(estuche, Brush.linearGradient(if (L > 0f) listOf(p.por(cuero, -0.7f), p.por(cuero, 0f), p.por(cuero, 0.6f)) else listOf(p.por(cuero, 0.6f), p.por(cuero, 0f), p.por(cuero, -0.7f)), al.q(-0.09f, 0.4f), al.q(0.1f, 0.4f)))
        clipPath(estuche) {
            // El cuero gastado: rozaduras más claras, la costura del lomo, la mugre de abajo.
            drawPath(Path().apply { moveTo(al.q(-0.065f, 0.05f)); cubicTo(al.q(-0.075f, 0.3f), al.q(-0.08f, 0.55f), al.q(-0.085f, 0.76f)) }, p.sombra(C.HOLLIN).copy(alpha = 0.55f), style = Stroke(0.008f * u))
            for (y in listOf(0.22f, 0.52f)) {
                drawLine(p.luz(C.ORO), al.q(-0.1f, y), al.q(0.11f, y - 0.03f), 0.03f * u)
                drawLine(p.sombra(lerp(C.ORO, C.HOLLIN, 0.3f)), al.q(-0.1f, y - 0.018f), al.q(0.11f, y - 0.048f), 0.008f * u)
                for (x in listOf(-0.05f, 0.0f, 0.05f)) drawCircle(p.luz(lerp(C.ORO, Color.White, 0.3f)), 0.008f * u, al.q(x, y - 0.002f))
            }
            mancha(al.q(0.0f, 0.38f), 0.03f * u, 0.12f * u, p.luz(lerp(cuero, C.FIELTRO, 0.4f)), 0.3f)
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(C.TIERRA).copy(alpha = 0.6f)), al.q(0f, 0.15f).y, al.q(0f, 0f).y), Offset(al.q(0f, 0f).x - 0.2f * u, al.q(0f, 0.2f).y), Size(0.4f * u, 0.25f * u))
        }
        // La boca de la aljaba, con su ribete.
        drawLine(p.por(lerp(cuero, C.HOLLIN, 0.4f), 0.3f), al.q(-0.095f, 0.78f), al.q(0.105f, 0.7f), 0.03f * u, StrokeCap.Round)
        filoCalido(p, estuche, al.q(-0.1f, 0.4f).x, al.q(0.1f, 0.4f).x)
        // ---- El arco: el lomo de corteza lacada, el vientre de asta, la empuñadura atada y las orejas de hueso.
        val linea = ARCO.map { e.q(it.first, it.second) }
        val anchos = ARCO.map { it.third * u * 1.45f }
        val lacado = lerp(C.ROJO, C.HOLLIN, 0.5f)
        // La cuerda, primero: de oreja a oreja, tocando los puentes.
        val cuerda = listOf(e.q(0.072f, 0.026f), e.q(-0.012f, 0.15f), e.q(-0.012f, 1.11f), e.q(0.077f, 1.235f))
        drawPath(Path().apply { moveTo(cuerda[0]); for (q in cuerda.drop(1)) lineTo(q) }, p.luz(lerp(C.LANA, C.ESTEPA, 0.3f)), style = Stroke((0.009f * u).coerceAtLeast(0.6f)))
        tubo(p, linea, anchos, lacado, 0.6f)
        // El vientre de asta: una franja clara del lado de la cuerda, en las palas.
        val asta = lerp(C.PIEDRA, C.ORO, 0.35f)
        for (i in 2 until ARCO.size - 3) {
            val a = e.q(ARCO[i].first - ARCO[i].third * 0.5f, ARCO[i].second); val b = e.q(ARCO[i + 1].first - ARCO[i + 1].third * 0.5f, ARCO[i + 1].second)
            if (i != 4 && i != 5) drawLine(p.por(asta, L * -0.2f), a, b, anchos[i] * 0.7f, StrokeCap.Round)
        }
        // La empuñadura: atada con tendón, de vueltas en diagonal, ennegrecida por la mano.
        for (j in 0 until 7) { val y = 0.56f + j * 0.022f; drawLine(p.por(lerp(C.CUERO, C.HOLLIN, 0.25f), if (j % 2 == 0) 0.3f else -0.4f), e.q(0.09f, y), e.q(0.145f, y + 0.012f), 0.012f * u, StrokeCap.Round) }
        // Las orejas de hueso, con la muesca de la cuerda.
        for ((i0, i1) in listOf(0 to 2, ARCO.size - 3 to ARCO.size - 1)) tubo(p, linea.subList(i0, i1 + 1), anchos.subList(i0, i1 + 1), C.FIELTRO_VIEJO, 0.8f)
        // Las pinturas del lomo: unas espirales de oro gastadas.
        for (i in listOf(3, 7)) drawCircle(p.medio(C.ORO).copy(alpha = 0.6f), 0.012f * u, e.q(ARCO[i].first + 0.006f, ARCO[i].second), style = Stroke(0.004f * u))
        val forma = poligono(ARCO.map { e.q(it.first - it.third, it.second) } + ARCO.reversed().map { e.q(it.first + it.third, it.second) })
        filoCalido(p, forma, e.q(-0.02f, 0.6f).x, e.q(0.14f, 0.6f).x)
    }

    // =================================================================
    //  La pértiga del lazo
    // =================================================================

    private fun siluetaLazo(e: Eje): Path = Path().apply {
        addPath(poligono(e.q(-0.03f, 0f), e.q(0.03f, 0f), e.q(0.02f, 5f), e.q(-0.02f, 5f)))
        addPath(poligono(e.q(-0.02f, 2.3f), e.q(0.32f, 1.6f), e.q(0.22f, 1.05f), e.q(-0.02f, 1.5f)))
    }

    private fun DrawScope.lazo(p: Pincel, e: Eje) {
        val u = e.u; val L = lado(p)
        val abedul = lerp(C.MADERA, C.FIELTRO, 0.32f)
        contacto(p, e.q(0f, 0f), 0.12f * u, 0.04f * u)
        // La vara: dos piezas de abedul empalmadas y atadas con tiento; la de abajo, más gruesa y negra de manos.
        val puntos = (0..12).map { e.q(0.006f * sin(it * 0.9f), it * 5f / 12f) }
        val anchos = (0..12).map { (0.027f - 0.011f * it / 12f) * u }
        tubo(p, puntos, anchos, abedul, 0.8f)
        // La corteza del abedul: rayitas oscuras y nudos.
        val az = Azar(Semilla.de("mongol", "objeto", "lazo"))
        repeat(30) { val y = az.entre(0.1f, 3.2f); val w = (0.027f - 0.011f * y / 5f) * u; val c = e.q(az.entre(-0.008f, 0.008f), y); drawLine(p.sombra(C.HOLLIN).copy(alpha = 0.5f), c - Offset(w * 0.7f, 0f), c + Offset(w * 0.5f, 0.2f), 0.4f) }
        // Donde se agarra: la madera pulida y oscura de grasa.
        drawLine(p.sombra(C.CUERO).copy(alpha = 0.55f), e.q(0f, 0.9f), e.q(0f, 1.5f), 0.04f * u, StrokeCap.Round)
        // El empalme, atado con vueltas de tiento crudo.
        for (j in 0 until 9) { val y = 1.85f + j * 0.022f; drawLine(p.por(lerp(C.CUERO, C.FIELTRO, 0.35f), if (j % 2 == 0) 0.4f else -0.3f), e.q(-0.028f, y), e.q(0.028f, y + 0.014f), 0.012f * u, StrokeCap.Round) }
        // La cuerda del lazo: baja de la punta pegada a la vara, se suelta en un ojo grande que cuelga y vuelve a la vara.
        val tiento = lerp(C.CUERO, C.FIELTRO, 0.3f)
        val bajada = (0..10).map { val y = 4.8f - it * 0.24f; e.q(-0.03f - 0.01f * sin(it * 1.3f), y) }
        drawPath(Path().apply { moveTo(bajada[0]); for (q in bajada.drop(1)) lineTo(q) }, p.medio(tiento), style = Stroke((0.012f * u).coerceAtLeast(0.6f), cap = StrokeCap.Round))
        val a = e.q(-0.03f, 2.4f); val b = e.q(-0.025f, 2.12f)
        val ojo = Path().apply { moveTo(a); cubicTo(a + Offset(0.42f * u * L, 0.4f * u), e.q(0.36f, 1.0f), e.q(0.12f, 1.02f)); cubicTo(e.q(-0.06f, 1.04f), e.q(-0.1f, 1.6f), b) }
        drawPath(ojo, p.sombra(tiento), style = Stroke((0.016f * u).coerceAtLeast(0.7f), cap = StrokeCap.Round))
        drawPath(ojo, p.luz(tiento).copy(alpha = 0.7f), style = Stroke((0.006f * u).coerceAtLeast(0.35f), cap = StrokeCap.Round))
        // La lazada: un nudo corredizo donde la cuerda se cierra.
        drawCircle(p.por(tiento, 0.2f), 0.022f * u, e.q(-0.03f, 2.4f))
        drawCircle(p.por(tiento, -0.4f), 0.018f * u, b)
    }

    // =================================================================
    //  La silla de montar en su caballete
    // =================================================================

    private fun siluetaSilla(e: Eje): Path = Path().apply {
        addPath(poligono(listOf(e.q(-0.38f, 0f), e.q(0.38f, 0f), e.q(0.33f, 0.52f), e.q(0.27f, 0.62f), e.q(0.21f, 0.84f), e.q(-0.06f, 0.68f), e.q(-0.23f, 0.97f), e.q(-0.31f, 0.62f), e.q(-0.36f, 0.52f))))
    }

    /** [frente]: hacia dónde mira la perilla de delante (1 a la derecha). */
    private fun DrawScope.silla(p: Pincel, e0: Eje, frente: Float) {
        val u = e0.u; val L = lado(p); val f = -frente                              // el dibujo tiene la perilla a la izquierda
        val e = Eje(e0.pie, 0f, u)
        fun q(x: Float, y: Float) = e.q(x * f, y)
        val madera = lerp(C.MADERA, C.TIERRA, 0.15f)
        contacto(p, e.q(0f, 0f), 0.85f * u, 0.07f * u, 0.35f)
        // ---- El caballete: un travesaño y dos pares de patas abiertas, las de detrás en sombra.
        for (atras in listOf(true, false)) for (x in listOf(-0.27f, 0.27f)) {
            val dx = if (atras) 0.04f * (if (x < 0f) 1f else -1f) else 0f
            val pieX = x * 1.3f + dx; val col = if (atras) p.sombra(madera) else p.por(madera, if (x * L > 0f) 0.5f else -0.2f)
            drawLine(col, e.q(x + dx, 0.5f), e.q(pieX, if (atras) 0.03f else 0f), 0.04f * u, StrokeCap.Round)
            if (!atras) drawLine(p.luz(madera).copy(alpha = 0.6f), e.q(x + dx + 0.01f * L, 0.48f), e.q(pieX + 0.012f * L, 0.02f), 0.01f * u, StrokeCap.Round)
        }
        tubo(p, listOf(e.q(-0.36f, 0.5f), e.q(0.36f, 0.5f)), listOf(0.03f * u, 0.03f * u), madera, 0.7f)
        // ---- La manta de fieltro, echada sobre el travesaño: azul oscuro, con su cenefa roja pespunteada; cae en pliegues.
        val fieltro = lerp(C.AZUL, C.HOLLIN, 0.38f)
        val manta = Path().apply {
            moveTo(q(-0.3f, 0.6f)); lineTo(q(0.29f, 0.6f)); cubicTo(q(0.31f, 0.5f), q(0.3f, 0.42f), q(0.27f, 0.36f))
            cubicTo(q(0.24f, 0.33f), q(0.12f, 0.34f), q(0.0f, 0.32f)); cubicTo(q(-0.12f, 0.31f), q(-0.24f, 0.33f), q(-0.28f, 0.35f))
            cubicTo(q(-0.31f, 0.42f), q(-0.32f, 0.52f), q(-0.3f, 0.6f)); close()
        }
        drawPath(manta, Brush.verticalGradient(listOf(p.por(fieltro, 0.5f), p.por(fieltro, -0.6f)), q(0f, 0.6f).y, q(0f, 0.32f).y))
        clipPath(manta) {
            val cenefa = Path().apply { moveTo(q(-0.33f, 0.39f)); cubicTo(q(-0.2f, 0.36f), q(-0.1f, 0.37f), q(0.0f, 0.375f)); cubicTo(q(0.12f, 0.385f), q(0.22f, 0.37f), q(0.33f, 0.41f)) }
            drawPath(cenefa, p.por(C.ROJO, 0.1f), style = Stroke(0.05f * u))
            drawPath(cenefa, p.luz(C.ORO).copy(alpha = 0.7f), style = Stroke(0.006f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(0.02f * u, 0.015f * u))))
            for (x in listOf(-0.19f, -0.04f, 0.13f)) drawPath(Path().apply { moveTo(q(x, 0.58f)); cubicTo(q(x + 0.01f, 0.48f), q(x - 0.01f, 0.4f), q(x + 0.015f, 0.32f)) }, p.sombra(fieltro).copy(alpha = 0.7f), style = Stroke(0.012f * u, cap = StrokeCap.Round))
            mancha(q(0.05f, 0.5f), 0.14f * u, 0.05f * u, p.luz(lerp(fieltro, C.FIELTRO, 0.2f)), 0.25f)
        }
        // ---- El faldón de cuero, sobre la manta, con sus clavos de plata en las esquinas.
        val cuero = lerp(C.CUERO, C.HOLLIN, 0.12f)
        val faldon = Path().apply { moveTo(q(-0.17f, 0.64f)); lineTo(q(0.16f, 0.64f)); cubicTo(q(0.18f, 0.56f), q(0.17f, 0.47f), q(0.14f, 0.42f)); lineTo(q(-0.15f, 0.42f)); cubicTo(q(-0.18f, 0.47f), q(-0.19f, 0.56f), q(-0.17f, 0.64f)); close() }
        drawPath(faldon, Brush.linearGradient(listOf(p.por(cuero, 0.5f * L * f), p.por(cuero, -0.5f * L * f)), q(-0.18f, 0.5f), q(0.18f, 0.5f)))
        clipPath(faldon) { drawPath(faldon, p.luz(lerp(cuero, C.FIELTRO, 0.3f)).copy(alpha = 0.45f), style = Stroke(0.012f * u)); mancha(q(0f, 0.5f), 0.08f * u, 0.04f * u, p.sombra(C.HOLLIN), 0.3f) }
        val plata = lerp(C.PIEDRA, Color.White, 0.45f)
        for ((x, y) in listOf(-0.13f to 0.6f, 0.12f to 0.6f, -0.12f to 0.46f, 0.11f to 0.46f)) { drawCircle(p.por(plata, 0.2f), 0.014f * u, q(x, y)); drawCircle(p.luz(Color.White).copy(alpha = 0.7f), 0.005f * u, q(x, y) + Offset(0.004f * u * L, -0.004f * u)) }
        // ---- La silla: los fustes de madera, el asiento acolchado, la perilla alta delante y el arzón detrás, lacados de rojo.
        val roja = lerp(C.ROJO, C.MADERA, 0.25f)
        val fuste = Path().apply { moveTo(q(-0.3f, 0.6f)); cubicTo(q(-0.1f, 0.65f), q(0.1f, 0.65f), q(0.29f, 0.6f)); lineTo(q(0.29f, 0.66f)); cubicTo(q(0.1f, 0.71f), q(-0.1f, 0.71f), q(-0.3f, 0.66f)); close() }
        drawPath(fuste, Brush.verticalGradient(listOf(p.por(madera, 0.5f), p.por(madera, -0.4f)), q(0f, 0.71f).y, q(0f, 0.6f).y))
        val asiento = Path().apply { moveTo(q(-0.17f, 0.7f)); cubicTo(q(-0.08f, 0.66f), q(0.06f, 0.66f), q(0.15f, 0.71f)); lineTo(q(0.16f, 0.76f)); cubicTo(q(0.06f, 0.72f), q(-0.08f, 0.72f), q(-0.18f, 0.77f)); close() }
        drawPath(asiento, Brush.verticalGradient(listOf(p.por(lerp(C.CUERO, C.HOLLIN, 0.3f), 0.6f), p.por(lerp(C.CUERO, C.HOLLIN, 0.3f), -0.4f)), q(0f, 0.77f).y, q(0f, 0.66f).y))
        val perilla = Path().apply { moveTo(q(-0.31f, 0.62f)); cubicTo(q(-0.31f, 0.8f), q(-0.28f, 0.91f), q(-0.22f, 0.97f)); cubicTo(q(-0.17f, 0.99f), q(-0.13f, 0.96f), q(-0.13f, 0.9f)); cubicTo(q(-0.15f, 0.82f), q(-0.17f, 0.73f), q(-0.15f, 0.65f)); close() }
        val arzon = Path().apply { moveTo(q(0.29f, 0.62f)); cubicTo(q(0.3f, 0.72f), q(0.27f, 0.8f), q(0.22f, 0.84f)); cubicTo(q(0.19f, 0.86f), q(0.15f, 0.84f), q(0.15f, 0.79f)); cubicTo(q(0.16f, 0.74f), q(0.16f, 0.69f), q(0.14f, 0.65f)); close() }
        for ((forma, cara) in listOf(arzon to 0.1f, perilla to 0.25f)) {
            drawPath(forma, Brush.linearGradient(listOf(p.por(roja, -0.6f + cara), p.por(roja, 0.5f + cara)), q(0f, 0.62f), q(0f, 0.98f)))
            clipPath(forma) {
                drawPath(forma, p.luz(lerp(roja, C.FIELTRO, 0.35f)).copy(alpha = 0.5f), style = Stroke(0.016f * u))
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(C.HOLLIN).copy(alpha = 0.4f)), q(0f, 0.74f).y, q(0f, 0.62f).y), Offset(-1000f, q(0f, 0.74f).y), Size(4000f, 0.15f * u))
            }
        }
        // La plata: una chapa redonda con su roseta en la perilla, clavos en el arzón y el filo de la perilla.
        val chapa = q(-0.22f, 0.84f)
        drawCircle(p.por(plata, 0.2f), 0.045f * u, chapa); drawCircle(p.sombra(plata), 0.045f * u, chapa, style = Stroke(0.007f * u)); drawCircle(p.sombra(plata).copy(alpha = 0.6f), 0.022f * u, chapa, style = Stroke(0.005f * u))
        drawCircle(p.luz(Color.White).copy(alpha = 0.8f), 0.012f * u, chapa + Offset(0.014f * u * L, -0.014f * u))
        for (k in 0..2) drawCircle(p.luz(plata), 0.013f * u, q(0.215f, 0.69f + k * 0.05f))
        drawLine(p.luz(plata), q(-0.22f, 0.97f), q(-0.14f, 0.94f), 0.012f * u, StrokeCap.Round)
        // ---- La ación y el estribo corto, de hierro, de piso ancho; la cincha, que cuelga.
        val estribo = q(0.0f, 0.27f)
        drawLine(p.medio(lerp(C.CUERO, C.HOLLIN, 0.3f)), q(-0.01f, 0.66f), estribo + Offset(0f, -0.07f * u), 0.024f * u)
        val hierro = lerp(C.HOLLIN, C.PIEDRA, 0.35f)
        val aro = Path().apply { moveTo(estribo + Offset(-0.035f * u, -0.07f * u)); cubicTo(estribo + Offset(-0.075f * u, -0.03f * u), estribo + Offset(-0.075f * u, 0.02f * u), estribo + Offset(-0.055f * u, 0.03f * u)); lineTo(estribo + Offset(0.055f * u, 0.03f * u)); cubicTo(estribo + Offset(0.075f * u, 0.02f * u), estribo + Offset(0.075f * u, -0.03f * u), estribo + Offset(0.035f * u, -0.07f * u)) }
        drawPath(aro, p.por(hierro, -0.3f), style = Stroke(0.018f * u, cap = StrokeCap.Round))
        drawPath(aro, p.luz(lerp(hierro, Color.White, 0.3f)).copy(alpha = 0.6f), style = Stroke(0.006f * u, cap = StrokeCap.Round))
        drawLine(p.por(hierro, 0.3f), estribo + Offset(-0.055f * u, 0.03f * u), estribo + Offset(0.055f * u, 0.03f * u), 0.028f * u, StrokeCap.Round)
        drawPath(Path().apply { moveTo(q(0.1f, 0.62f)); cubicTo(q(0.13f, 0.45f), q(0.17f, 0.3f), q(0.14f, 0.18f)) }, p.sombra(lerp(C.CUERO, C.FIELTRO, 0.2f)), style = Stroke(0.026f * u, cap = StrokeCap.Round))
        drawCircle(p.por(hierro, 0.2f), 0.02f * u, q(0.14f, 0.18f), style = Stroke(0.008f * u))
        filoCalido(p, union(listOf(perilla, arzon, manta)), q(-0.4f, 0f).x.coerceAtMost(q(0.4f, 0f).x), q(-0.4f, 0f).x.coerceAtLeast(q(0.4f, 0f).x))
    }

    // =================================================================
    //  La tienda: un tramo de celosía, un rollo de fieltro y varas del techo
    // =================================================================

    private fun siluetaGer(e: Eje): Path = Path().apply {
        addPath(poligono(e.q(-0.36f, 0f), e.q(0.38f, 0f), e.q(0.42f, 1.36f), e.q(-0.32f, 1.36f)))
        val r = e.girado(-e.ang, e.pie)
        addPath(poligono(r.q(-0.2f, 0f), r.q(0.85f, 0f), r.q(0.85f, 0.3f), r.q(-0.2f, 0.3f)))
        addPath(poligono(e.q(-0.45f, 0f), e.q(-0.4f, 0f), e.q(0.2f, 2.4f), e.q(0.14f, 2.4f)))
    }

    private fun DrawScope.ger(p: Pincel, e: Eje) {
        val u = e.u; val L = lado(p)
        val vara = lerp(C.MADERA, C.ROJO, 0.42f)
        contacto(p, e.q(0.03f, 0f), 0.8f * u, 0.06f * u, 0.35f)
        // ---- Las varas del techo, detrás: largas, pintadas de rojo, con la punta clara que entra en el aro.
        val varas = e.girado(-0.12f, e.q(-0.36f, 0f))
        for (j in 0 until 4) {
            val x = j * 0.06f
            tubo(p, listOf(varas.q(x, 0f), varas.q(x + 0.02f, 2.5f)), listOf(0.016f * u, 0.012f * u), vara, 0.6f)
            drawLine(p.luz(lerp(C.MADERA, C.FIELTRO, 0.3f)), varas.q(x + 0.019f, 2.25f), varas.q(x + 0.02f, 2.5f), 0.02f * u, StrokeCap.Round)
        }
        // ---- El tramo de celosía, a medio plegar: dos familias de varas que se cruzan en rombos, atadas con tiento.
        val x0 = -0.33f; val x1 = 0.36f; val alto = 1.34f; val n = 9
        val paso = (x1 - x0) / (n - 1); val cruza = 0.34f
        val lejos = ArrayList<Pair<Offset, Offset>>(); val cerca = ArrayList<Pair<Offset, Offset>>()
        for (i in 0 until n) {
            val x = x0 + i * paso
            lejos += e.q(x, 0f) to e.q(x + cruza, alto)
            cerca += e.q(x + cruza, 0f) to e.q(x, alto)
        }
        val grueso = 0.017f * u
        for ((a, b) in lejos) { drawLine(p.sombra(vara), a, b, grueso, StrokeCap.Round); drawLine(p.medio(vara).copy(alpha = 0.6f), a + Offset(L * 0.3f, 0f), b + Offset(L * 0.3f, 0f), grueso * 0.4f) }
        for ((a, b) in cerca) { drawLine(p.por(vara, -0.25f), a, b, grueso, StrokeCap.Round); drawLine(p.luz(lerp(vara, C.FIELTRO, 0.25f)).copy(alpha = 0.7f), a + Offset(L * grueso * 0.3f, 0f), b + Offset(L * grueso * 0.3f, 0f), grueso * 0.35f) }
        // Los nudos de tiento en cada cruce, y el desgaste de las cabezas de las varas.
        for (i in 0 until n) for (j in 0 until n) {
            val xa = x0 + i * paso; val xb = x0 + j * paso + cruza
            // Donde se cruzan la vara i de una familia y la j de la otra: x = xa + cruza·t = xb − cruza·t.
            val t = (xb - xa) / (2f * cruza); if (t <= 0.02f || t >= 0.98f) continue
            val c = e.q(xa + cruza * t, alto * t)
            drawCircle(p.por(lerp(C.CUERO, C.FIELTRO, 0.3f), 0.2f), grueso * 0.75f, c)
        }
        for ((a, _) in cerca) drawCircle(p.sombra(C.TIERRA).copy(alpha = 0.6f), grueso * 0.6f, a)
        // ---- El rollo de fieltro, tendido delante, atado con dos cuerdas de crin; se le ve la espiral de la cabeza.
        val r = Eje(e.pie + Offset(-0.12f * u, 0.05f * u), 0f, u)
        val radio = 0.15f; val largo = 0.95f
        val cuerpo = Path().apply { moveTo(r.q(0f, 0f)); lineTo(r.q(largo, 0f)); lineTo(r.q(largo, radio * 2f)); lineTo(r.q(0f, radio * 2f)); close() }
        contacto(p, r.q(largo / 2f, 0f), (largo + 0.1f) * u, 0.07f * u, 0.4f)
        val fieltro = lerp(C.FIELTRO, C.FIELTRO_VIEJO, 0.4f)
        drawPath(cuerpo, Brush.verticalGradient(listOf(p.por(fieltro, 0.7f), p.por(fieltro, 0.1f), p.por(fieltro, -0.8f)), r.q(0f, radio * 2f).y, r.q(0f, 0f).y))
        clipPath(cuerpo) {
            // El pelo del fieltro y el canto de la vuelta de fuera.
            val az = Azar(Semilla.de("mongol", "objeto", "rollo"))
            repeat(60) { val c = r.q(az.entre(0f, largo), az.entre(0.02f, radio * 2f)); drawLine(p.luz(lerp(fieltro, Color.White, 0.3f)).copy(alpha = 0.35f), c, c + Offset(az.entre(-1.2f, 1.2f), az.entre(-0.4f, 0.4f)), 0.35f) }
            drawLine(p.sombra(fieltro).copy(alpha = 0.6f), r.q(0f, radio * 1.2f), r.q(largo, radio * 1.3f), 0.01f * u)
            mancha(r.q(largo * 0.6f, 0.05f), 0.25f * u, 0.04f * u, p.sombra(C.TIERRA), 0.4f)
        }
        // La cabeza del rollo, del lado del sol o del otro.
        val cabezaX = if (L > 0f) largo else 0f
        val cab = r.q(cabezaX, radio)
        drawOval(p.por(lerp(fieltro, C.TIERRA, 0.15f), 0.2f * L * (if (cabezaX > 0f) 1f else -1f)), Offset(cab.x - 0.07f * u, cab.y - radio * u), Size(0.14f * u, radio * 2f * u))
        drawPath(Path().apply { for (k in 0..30) { val a = k * 0.62f; val rr = radio * (0.1f + 0.85f * k / 30f); val q = cab + Offset(0.45f * rr * u * cos(a), rr * u * sin(a)); if (k == 0) moveTo(q) else lineTo(q) } }, p.sombra(fieltro).copy(alpha = 0.7f), style = Stroke(0.008f * u))
        for (x in listOf(0.22f, 0.72f)) {
            val a = r.q(x, 0f); val b = r.q(x + 0.02f, radio * 2f)
            drawLine(p.sombra(C.CRIN), a, b, 0.02f * u, StrokeCap.Round)
            drawLine(p.medio(lerp(C.CRIN, C.FIELTRO, 0.3f)).copy(alpha = 0.6f), a + Offset(L * 0.3f, 0f), b + Offset(L * 0.3f, 0f), 0.006f * u)
        }
    }

    // =================================================================
    //  El violín de cabeza de caballo
    // =================================================================

    private fun siluetaViolin(e: Eje): Path = Path().apply {
        fun v(x: Float, y: Float) = e.q(x * 1.18f, y * 1.1f)
        addPath(poligono(listOf(v(-0.13f, 0.02f), v(0.13f, 0.02f), v(0.1f, 0.44f), v(0.03f, 0.46f), v(0.03f, 1.0f), v(-0.09f, 1.12f), v(-0.04f, 0.98f), v(-0.03f, 0.46f), v(-0.1f, 0.44f))))
    }

    private fun DrawScope.violin(p: Pincel, e: Eje) {
        // Todo el violín, un poco más ancho y más alto que la medida de un metro: así se lee a tamaño de teléfono.
        fun v(x: Float, y: Float) = e.q(x * 1.18f, y * 1.1f)
        val u = e.u; val L = lado(p)
        val madera = lerp(C.MADERA, C.HOLLIN, 0.3f)
        contacto(p, v(0f, 0f), 0.3f * u, 0.05f * u)
        // ---- El arco: una vara curva con su mecha de crin, apoyado al lado, cruzado.
        val ar = e.girado(-0.35f, v(0.2f, 0f))
        val varilla = (0..8).map { val t = it / 8f; ar.q(0.06f * sin(t * PI.toFloat()), 0.78f * t) }
        drawPath(Path().apply { moveTo(ar.q(0.005f, 0.04f)); lineTo(ar.q(0.0f, 0.74f)) }, p.luz(lerp(C.LANA, C.ESTEPA, 0.2f)).copy(alpha = 0.85f), style = Stroke(0.01f * u))
        tubo(p, varilla, varilla.map { 0.008f * u }, madera, 0.6f)
        // ---- La pica de abajo, la caja trapecial con su lado en sombra y la tapa de piel.
        drawLine(p.medio(C.HOLLIN), v(0f, 0f), v(0f, 0.04f), 0.02f * u, StrokeCap.Round)
        val canto = 0.035f * L                                                  // el lado de la caja que se ve, del lado del sol
        val caja = Path().apply { moveTo(v(-0.125f, 0.03f)); lineTo(v(0.125f, 0.03f)); cubicTo(v(0.11f, 0.15f), v(0.11f, 0.3f), v(0.1f, 0.43f)); lineTo(v(-0.1f, 0.43f)); cubicTo(v(-0.11f, 0.3f), v(-0.11f, 0.15f), v(-0.125f, 0.03f)); close() }
        val lateral = Path().apply { val s = if (L > 0f) 1f else -1f; moveTo(v(0.125f * s, 0.03f)); lineTo(v(0.125f * s + canto, 0.045f)); cubicTo(v(0.11f * s + canto, 0.16f), v(0.11f * s + canto, 0.3f), v(0.1f * s + canto, 0.42f)); lineTo(v(0.1f * s, 0.43f)); cubicTo(v(0.11f * s, 0.3f), v(0.11f * s, 0.15f), v(0.125f * s, 0.03f)); close() }
        drawPath(lateral, p.por(lerp(madera, C.ROJO, 0.3f), 0.6f))
        drawPath(caja, p.por(lerp(madera, C.ROJO, 0.35f), -0.1f))
        val piel = Path().apply { moveTo(v(-0.105f, 0.05f)); lineTo(v(0.105f, 0.05f)); cubicTo(v(0.093f, 0.16f), v(0.093f, 0.3f), v(0.083f, 0.41f)); lineTo(v(-0.083f, 0.41f)); cubicTo(v(-0.093f, 0.3f), v(-0.093f, 0.16f), v(-0.105f, 0.05f)); close() }
        val pergamino = lerp(C.FIELTRO, C.CUERO, 0.22f)
        drawPath(piel, Brush.linearGradient(if (L > 0f) listOf(p.por(pergamino, -0.3f), p.por(pergamino, 0.6f)) else listOf(p.por(pergamino, 0.6f), p.por(pergamino, -0.3f)), v(-0.1f, 0.2f), v(0.1f, 0.2f)))
        clipPath(piel) {
            // La piel estirada: manchas del curtido, el roce del arco abajo y el borde pintado.
            val az = Azar(Semilla.de("mongol", "objeto", "violin"))
            repeat(7) { mancha(v(az.entre(-0.08f, 0.08f), az.entre(0.08f, 0.38f)), az.entre(0.012f, 0.03f) * u, az.entre(0.01f, 0.025f) * u, p.sombra(C.CUERO), 0.2f) }
            mancha(v(0f, 0.13f), 0.06f * u, 0.025f * u, p.sombra(C.HOLLIN), 0.3f)
            drawPath(piel, p.medio(C.AZUL), style = Stroke(0.012f * u))
            // Una voluta pintada en lo alto de la tapa.
            drawCircle(p.medio(C.ROJO).copy(alpha = 0.7f), 0.022f * u, v(0f, 0.33f), style = Stroke(0.007f * u))
            drawCircle(p.medio(C.ORO).copy(alpha = 0.8f), 0.009f * u, v(0f, 0.33f))
        }
        // ---- El mástil, el clavijero con sus dos clavijas y la cabeza de caballo tallada.
        tubo(p, listOf(v(0f, 0.42f), v(0f, 0.93f)), listOf(0.018f * u, 0.015f * u), madera, 0.7f)
        for ((y, s) in listOf(0.88f to -1f, 0.84f to 1f)) { drawLine(p.por(madera, s * L * 0.5f), v(0f, y), v(s * 0.06f, y + 0.012f), 0.016f * u, StrokeCap.Round); drawCircle(p.por(madera, 0.3f), 0.013f * u, v(s * 0.065f, y + 0.012f)) }
        val cabeza = Path().apply {
            moveTo(v(0.016f, 0.93f)); cubicTo(v(0.02f, 0.99f), v(0.0f, 1.04f), v(-0.025f, 1.06f)); lineTo(v(-0.03f, 1.09f)); lineTo(v(-0.04f, 1.065f))
            cubicTo(v(-0.06f, 1.06f), v(-0.085f, 1.04f), v(-0.1f, 1.0f)); cubicTo(v(-0.105f, 0.985f), v(-0.095f, 0.975f), v(-0.085f, 0.98f))
            cubicTo(v(-0.06f, 0.99f), v(-0.04f, 0.98f), v(-0.025f, 0.96f)); lineTo(v(-0.016f, 0.93f)); close()
        }
        drawPath(cabeza, Brush.linearGradient(listOf(p.por(madera, 0.6f * L), p.por(madera, -0.6f * L)), v(-0.1f, 1f), v(0.02f, 1f)))
        drawLine(p.luz(C.ORO).copy(alpha = 0.8f), v(-0.005f, 1.04f), v(0.012f, 0.95f), 0.006f * u, StrokeCap.Round)          // la crin dorada
        drawCircle(p.luz(C.ORO), 0.006f * u, v(-0.05f, 1.02f))                                                           // el ojo
        // ---- Las dos cuerdas de crin, gruesas, y el puente.
        for (s in listOf(-1f, 1f)) drawLine(p.luz(lerp(C.LANA, C.ESTEPA, 0.25f)).copy(alpha = 0.9f), v(s * 0.006f, 0.86f), v(s * 0.02f, 0.07f), 0.006f * u)
        drawLine(p.por(lerp(C.MADERA, C.FIELTRO, 0.3f), 0.4f), v(-0.035f, 0.17f), v(0.035f, 0.17f), 0.016f * u)
        drawLine(p.medio(C.HOLLIN), v(-0.03f, 0.065f), v(0.03f, 0.065f), 0.018f * u, StrokeCap.Round)
        val todo = Path().apply { addPath(caja); addPath(cabeza) }
        filoCalido(p, todo, v(-0.13f, 0.3f).x, v(0.13f, 0.3f).x)
    }

    // =================================================================
    //  El abrigo, colgado
    // =================================================================

    /** Dónde cuelga el abrigo: de [apoyo] si está alto; si no, a la altura de un clavo sobre [pie]. El bajo no pasa del suelo. */
    private class Colgado(pie: Offset, apoyo: Offset, val u: Float) {
        val gancho: Offset = if (apoyo.y < pie.y - 0.9f * u) apoyo else Offset(pie.x, pie.y - 1.25f * u)
        val bajo: Float = minOf(gancho.y + 1.2f * u, pie.y - 0.5f)
        val largo: Float get() = bajo - gancho.y
        val suelo: Float = pie.y
        /** Un punto del abrigo: [x] en metros desde el eje, [t] de 0 (el cuello) a 1 (el bajo). */
        fun q(x: Float, t: Float) = Offset(gancho.x + x * u, gancho.y + t * largo)
    }

    private fun siluetaDeel(c: Colgado): Path = poligono(listOf(c.q(-0.07f, 0.02f), c.q(0.07f, 0.02f), c.q(0.28f, 0.12f), c.q(0.33f, 0.6f), c.q(0.38f, 1f), c.q(-0.38f, 1f), c.q(-0.33f, 0.6f), c.q(-0.28f, 0.12f)))

    private fun DrawScope.deel(p: Pincel, c: Colgado) {
        val u = c.u; val L = lado(p)
        val pano = lerp(C.AZUL, C.HOLLIN, 0.32f)
        val piel = lerp(C.LANA, C.CUERO, 0.35f)
        val faja = lerp(C.ORO, C.ROJO, 0.3f)
        // El clavo, o la cuerda, y la presilla del cuello.
        drawCircle(p.por(C.MADERA_OSC, 0.3f), 0.018f * u, c.gancho)
        drawPath(Path().apply { moveTo(c.gancho); cubicTo(c.gancho + Offset(-0.03f * u, 0.02f * u), c.q(-0.03f, 0.03f) + Offset(0f, -0.01f * u), c.q(-0.02f, 0.04f)); moveTo(c.gancho); cubicTo(c.gancho + Offset(0.03f * u, 0.02f * u), c.q(0.03f, 0.03f), c.q(0.02f, 0.04f)) }, p.medio(C.CRIN), style = Stroke(0.008f * u))
        // ---- Las mangas, detrás del cuerpo: caen rectas, anchas, con la bocamanga de piel vuelta.
        for (s in listOf(-1f, 1f)) {
            val manga = Path().apply { moveTo(c.q(s * 0.2f, 0.1f)); cubicTo(c.q(s * 0.29f, 0.12f), c.q(s * 0.34f, 0.2f), c.q(s * 0.35f, 0.3f)); lineTo(c.q(s * 0.37f, 0.66f)); lineTo(c.q(s * 0.22f, 0.66f)); lineTo(c.q(s * 0.2f, 0.3f)); close() }
            drawPath(manga, p.por(pano, if (s == L) 0.15f else -0.7f))
            drawPath(poligono(c.q(s * 0.37f, 0.62f), c.q(s * 0.22f, 0.62f), c.q(s * 0.215f, 0.7f), c.q(s * 0.38f, 0.7f)), p.por(piel, if (s == L) 0.4f else -0.4f))
            drawLine(p.sombra(pano).copy(alpha = 0.7f), c.q(s * 0.28f, 0.2f), c.q(s * 0.29f, 0.6f), 0.01f * u)
        }
        // ---- El cuerpo: se ensancha hacia el bajo; los pliegues del peso y el lado de la sombra.
        val cuerpo = Path().apply {
            moveTo(c.q(-0.07f, 0.02f)); lineTo(c.q(0.07f, 0.02f)); cubicTo(c.q(0.16f, 0.06f), c.q(0.22f, 0.1f), c.q(0.24f, 0.16f))
            cubicTo(c.q(0.26f, 0.45f), c.q(0.31f, 0.8f), c.q(0.36f, 1f)); cubicTo(c.q(0.18f, 1.02f), c.q(-0.18f, 1.02f), c.q(-0.36f, 1f))
            cubicTo(c.q(-0.31f, 0.8f), c.q(-0.26f, 0.45f), c.q(-0.24f, 0.16f)); cubicTo(c.q(-0.22f, 0.1f), c.q(-0.16f, 0.06f), c.q(-0.07f, 0.02f)); close()
        }
        drawPath(cuerpo, Brush.linearGradient(if (L > 0f) listOf(p.por(pano, -0.8f), p.por(pano, 0f), p.por(pano, 0.55f)) else listOf(p.por(pano, 0.55f), p.por(pano, 0f), p.por(pano, -0.8f)), c.q(-0.36f, 0.6f), c.q(0.36f, 0.6f)))
        clipPath(cuerpo) {
            // Los pliegues que caen del cuello y del pecho.
            for ((x0, x1, k) in listOf(Triple(-0.12f, -0.2f, -1f), Triple(-0.02f, -0.06f, 1f), Triple(0.1f, 0.16f, -1f), Triple(0.2f, 0.29f, 1f))) {
                val forma = Path().apply { moveTo(c.q(x0, 0.2f)); cubicTo(c.q(x0, 0.5f), c.q(x1, 0.8f), c.q(x1 * 1.1f, 1.02f)) }
                drawPath(forma, (if (k > 0f) p.luz(lerp(pano, C.FIELTRO, 0.12f)) else p.sombra(pano)).copy(alpha = 0.55f), style = Stroke(0.025f * u, cap = StrokeCap.Round))
            }
            // El cruce de la delantera: del cuello a la axila de la derecha, con su ribete de piel y los botones de nudo.
            val cruce = Path().apply { moveTo(c.q(-0.06f, 0.03f)); cubicTo(c.q(0.04f, 0.12f), c.q(0.14f, 0.15f), c.q(0.22f, 0.2f)); lineTo(c.q(0.25f, 0.95f)) }
            drawPath(cruce, p.sombra(pano), style = Stroke(0.03f * u))
            drawPath(cruce, p.por(faja, 0.2f), style = Stroke(0.012f * u))
            for (t in listOf(0.2f, 0.55f, 0.85f)) drawCircle(p.luz(C.ORO), 0.014f * u, Offset(c.q(-0.06f, 0.03f).x + (c.q(0.22f, 0.2f).x - c.q(-0.06f, 0.03f).x) * t, c.q(-0.06f, 0.03f).y + (c.q(0.22f, 0.2f).y - c.q(-0.06f, 0.03f).y) * t - 0.012f * u))
            // El bajo, gastado y con barro; la piel de dentro asoma.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(C.TIERRA).copy(alpha = 0.5f)), c.q(0f, 0.86f).y, c.q(0f, 1f).y), Offset(c.q(-0.4f, 0f).x, c.q(0f, 0.86f).y), Size(0.8f * u, c.largo * 0.16f))
            drawPath(Path().apply { moveTo(c.q(-0.37f, 0.975f)); cubicTo(c.q(-0.18f, 0.995f), c.q(0.18f, 0.995f), c.q(0.37f, 0.975f)) }, p.por(piel, 0f), style = Stroke(0.02f * u))
        }
        // El cuello alto, con la piel de borrego por dentro.
        drawPath(Path().apply { moveTo(c.q(-0.07f, 0.0f)); cubicTo(c.q(-0.05f, 0.05f), c.q(0.05f, 0.05f), c.q(0.07f, 0.0f)); lineTo(c.q(0.08f, 0.04f)); cubicTo(c.q(0.05f, 0.09f), c.q(-0.05f, 0.09f), c.q(-0.08f, 0.04f)); close() }, p.por(piel, 0.3f))
        // ---- La faja, echada sobre el hombro y colgando, con sus flecos.
        val s = -L
        val tira = Path().apply { moveTo(c.q(s * 0.05f, 0.04f)); cubicTo(c.q(s * 0.14f, 0.1f), c.q(s * 0.15f, 0.3f), c.q(s * 0.13f, 0.62f)); lineTo(c.q(s * 0.05f, 0.64f)); cubicTo(c.q(s * 0.07f, 0.3f), c.q(s * 0.05f, 0.12f), c.q(s * 0.0f, 0.06f)); close() }
        drawPath(tira, Brush.linearGradient(listOf(p.por(faja, -0.5f), p.por(faja, 0.4f)), c.q(s * 0.02f, 0.3f), c.q(s * 0.15f, 0.3f)))
        drawLine(p.sombra(faja).copy(alpha = 0.6f), c.q(s * 0.1f, 0.12f), c.q(s * 0.09f, 0.6f), 0.008f * u)
        for (k in 0..5) { val a = c.q(s * (0.055f + k * 0.015f), 0.635f - k * 0.003f); drawLine(p.medio(faja), a, a + Offset(0f, 0.05f * u), 0.006f * u, StrokeCap.Round) }
        // Si el bajo llega al suelo, se arruga: un pliegue que se dobla.
        if (c.bajo >= c.suelo - 1f) contacto(p, Offset(c.gancho.x, c.suelo), 0.7f * u, 0.05f * u, 0.4f)
        filoCalido(p, cuerpo, c.q(-0.4f, 0f).x, c.q(0.4f, 0f).x)
    }

    // =================================================================
    //  El cesto del argal, con su horquilla
    // =================================================================

    private fun siluetaArgal(e: Eje): Path = Path().apply {
        addPath(poligono(listOf(e.q(-0.18f, 0f), e.q(0.18f, 0f), e.q(0.27f, 0.72f), e.q(0.2f, 0.86f), e.q(-0.2f, 0.88f), e.q(-0.27f, 0.72f))))
    }

    /** [hacia]: el lado donde se apoya la horquilla (1 a la derecha). */
    private fun DrawScope.argal(p: Pincel, e: Eje, hacia: Float) {
        val u = e.u; val L = lado(p)
        val mimbre = lerp(C.ESTEPA, C.MADERA, 0.25f)
        val boniga = lerp(C.ESTIERCOL, C.ESTEPA, 0.5f)
        val az = Azar(Semilla.de("mongol", "objeto", "argal"))
        contacto(p, e.q(0f, 0f), 0.5f * u, 0.06f * u, 0.4f)
        // ---- Unas tortas caídas al pie.
        for ((x, w) in listOf(-0.3f to 0.11f, 0.32f * hacia to 0.09f, -0.22f to 0.08f)) { val c = e.q(x, 0.01f); drawOval(p.por(boniga, -0.3f), Offset(c.x - w * u, c.y - w * 0.3f * u), Size(w * 2f * u, w * 0.55f * u)); drawOval(p.por(boniga, 0.5f), Offset(c.x - w * 0.85f * u, c.y - w * 0.36f * u), Size(w * 1.7f * u, w * 0.36f * u)) }
        // ---- La horquilla de madera, detrás, apoyada en el borde del cesto.
        val h = e.girado(hacia * 0.32f, e.q(-0.08f * hacia, 0f))
        tubo(p, listOf(h.q(0f, 0f), h.q(0f, 1.05f)), listOf(0.014f * u, 0.012f * u), lerp(C.MADERA, C.PIEDRA, 0.2f), 0.7f)
        drawLine(p.por(C.MADERA, 0.2f), h.q(-0.08f, 1.0f), h.q(0.08f, 1.0f), 0.022f * u, StrokeCap.Round)
        for (k in 0..3) { val x = -0.07f + k * 0.047f; drawLine(p.por(C.MADERA, if (k % 2 == 0) 0.4f else -0.2f), h.q(x, 1.0f), h.q(x * 1.15f, 1.13f), 0.012f * u, StrokeCap.Round) }
        // ---- El cesto: ancho de boca, de mimbre tejido, con las correas de llevarlo a la espalda.
        val cesto = Path().apply { moveTo(e.q(-0.18f, 0f)); lineTo(e.q(0.18f, 0f)); cubicTo(e.q(0.22f, 0.25f), e.q(0.25f, 0.5f), e.q(0.27f, 0.72f)); lineTo(e.q(-0.27f, 0.72f)); cubicTo(e.q(-0.25f, 0.5f), e.q(-0.22f, 0.25f), e.q(-0.18f, 0f)); close() }
        drawPath(cesto, Brush.linearGradient(if (L > 0f) listOf(p.por(mimbre, -0.8f), p.por(mimbre, 0f), p.por(mimbre, 0.6f)) else listOf(p.por(mimbre, 0.6f), p.por(mimbre, 0f), p.por(mimbre, -0.8f)), e.q(-0.27f, 0.4f), e.q(0.27f, 0.4f)))
        clipPath(cesto) {
            // El tejido: las varas de pie y las vueltas, que hacen un cesteado de puntos de luz.
            for (j in 0..11) { val x = -0.26f + j * 0.047f; drawLine(p.sombra(mimbre).copy(alpha = 0.55f), e.q(x * 0.68f, 0f), e.q(x, 0.72f), 0.008f * u) }
            for (i in 0 until 16) {
                val y = 0.03f + i * 0.045f; val w = 0.18f + (0.27f - 0.18f) * y / 0.72f
                for (j in 0 until 10) { val x0 = -w + (2f * w) * j / 10f + (if (i % 2 == 0) 0f else w / 10f); val c = e.q(x0 + w / 20f, y); val luz = (x0 / w) * L
                    drawOval(p.por(mimbre, 0.1f + 0.6f * luz), Offset(c.x - w / 10f * u * 0.9f, c.y - 0.012f * u), Size(w / 5f * u * 0.9f, 0.024f * u)) }
            }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(C.TIERRA).copy(alpha = 0.55f)), e.q(0f, 0.2f).y, e.q(0f, 0f).y), Offset(e.q(-0.3f, 0f).x, e.q(0f, 0.25f).y), Size(0.6f * u, 0.25f * u))
        }
        // Las correas de cuero de la espalda, a un lado.
        for (s in listOf(0.2f, 0.245f)) drawPath(Path().apply { moveTo(e.q(-L * s, 0.68f)); cubicTo(e.q(-L * (s + 0.05f), 0.55f), e.q(-L * (s + 0.04f), 0.35f), e.q(-L * (s - 0.03f), 0.18f)) }, p.sombra(C.CUERO), style = Stroke(0.014f * u, cap = StrokeCap.Round))
        // ---- Lo de dentro: las tortas de boñiga seca, apiladas por encima del borde, cada una con su canto de luz.
        val tortas = ArrayList<Triple<Offset, Float, Float>>()
        for (f in 0 until 4) { val n = 5 - f; for (k in 0 until n) { val x = -0.22f + (0.44f) * (k + 0.5f) / n + az.entre(-0.03f, 0.03f) + f * 0.01f; tortas += Triple(e.q(x, 0.72f + f * 0.045f + az.entre(0f, 0.02f)), az.entre(0.06f, 0.085f) * (1f - 0.08f * f), az.entre(-0.4f, 0.4f)) } }
        for ((c, w, inclina) in tortas) {
            val col = lerp(boniga, if (az.entero(3) == 0) C.ESTEPA else C.ESTIERCOL, az.entre(0f, 0.35f)); val luz = ((c.x - e.q(0f, 0.8f).x) / (0.25f * u)) * L
            drawOval(p.por(col, -0.4f + 0.4f * luz), Offset(c.x - w * u, c.y - w * 0.42f * u + inclina * 2f), Size(w * 2f * u, w * 0.85f * u))
            drawOval(p.por(col, 0.4f + 0.5f * luz), Offset(c.x - w * 0.85f * u, c.y - w * 0.46f * u + inclina * 2f), Size(w * 1.7f * u, w * 0.4f * u))
            drawLine(p.sombra(col).copy(alpha = 0.5f), c + Offset(-w * 0.4f * u, 0f), c + Offset(w * 0.2f * u, w * 0.05f * u), 0.4f)
        }
        // El borde del cesto, un aro grueso de vara doblada.
        drawPath(Path().apply { moveTo(e.q(-0.275f, 0.72f)); cubicTo(e.q(-0.1f, 0.74f), e.q(0.1f, 0.74f), e.q(0.275f, 0.72f)) }, p.por(mimbre, 0.3f), style = Stroke(0.03f * u, cap = StrokeCap.Round))
        filoCalido(p, cesto, e.q(-0.27f, 0f).x, e.q(0.27f, 0f).x)
    }

    // =================================================================
    //  Los chicos, sobre el soporte
    // =================================================================

    /** El odre de cuero que se lleva en la silla: plano y redondo, de cuello corto, cosido por el canto y repujado. */
    private fun DrawScope.odre(p: Pincel, o: Offset, k: Float) {
        val L = lado(p)
        fun q(x: Float, y: Float) = Offset(o.x + x * k, o.y + y * k)
        sombrita(p, o, 20f * k, 26f * k)
        contacto(p, q(0f, 0f), 20f * k, 3.2f * k)
        val cuero = lerp(C.CUERO, C.ORO, 0.12f)
        val cuerpo = Path().apply { moveTo(q(-3f, -21f)); cubicTo(q(-8f, -20f), q(-12f, -15f), q(-12f, -8f)); cubicTo(q(-12f, -2f), q(-7f, 0f), q(0f, 0f)); cubicTo(q(7f, 0f), q(12f, -2f), q(12f, -8f)); cubicTo(q(12f, -15f), q(8f, -20f), q(3f, -21f)); close() }
        drawPath(cuerpo, Brush.linearGradient(if (L > 0f) listOf(p.por(cuero, -0.8f), p.por(cuero, 0f), p.por(cuero, 0.7f)) else listOf(p.por(cuero, 0.7f), p.por(cuero, 0f), p.por(cuero, -0.8f)), q(-12f, -10f), q(12f, -10f)))
        clipPath(cuerpo) {
            // El repujado: un nudo de volutas en el centro, el brillo de la grasa y el canto más oscuro.
            val dibujo = p.sombra(lerp(cuero, C.HOLLIN, 0.3f)).copy(alpha = 0.75f)
            drawCircle(dibujo, 4.2f * k, q(0f, -9f), style = Stroke(0.6f * k))
            for (a in 0 until 4) { val ang = (PI / 2 * a).toFloat() + 0.4f; val c = q(cos(ang) * 4.2f, -9f + sin(ang) * 4.2f); drawArc(dibujo, ang * 57.3f, 200f, false, Offset(c.x - 2f * k, c.y - 2f * k), Size(4f * k, 4f * k), style = Stroke(0.55f * k)) }
            drawCircle(p.luz(lerp(cuero, C.FIELTRO, 0.3f)).copy(alpha = 0.5f), 1.2f * k, q(0f, -9f))
            drawOval(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.32f * (1f - 0.6f * p.l.noche)), Color.Transparent), q(L * 5f, -13f), 5f * k), Offset(q(L * 5f, -13f).x - 5f * k, q(0f, -18f).y), Size(10f * k, 10f * k))
            drawPath(cuerpo, p.sombra(C.HOLLIN).copy(alpha = 0.4f), style = Stroke(2.6f * k))
        }
        // La costura del canto: un pespunte de tendón.
        val canto = (0..26).map { val a = (PI * (0.15f + 0.7f * it / 26f)).toFloat(); q(-cos(a) * 11f, -8.5f + sin(a) * 8.5f) }
        drawPoints(canto, androidx.compose.ui.graphics.PointMode.Points, p.luz(C.LANA).copy(alpha = 0.8f), 0.6f * k, StrokeCap.Round)
        // El cuello, el tapón de madera y la correa que baja al costado.
        drawPath(poligono(q(-2.6f, -20.5f), q(2.6f, -20.5f), q(2f, -25f), q(-2f, -25f)), p.por(lerp(cuero, C.HOLLIN, 0.2f), L * -0.2f))
        drawRect(p.por(C.MADERA, 0.3f), q(-1.8f, -28.4f), Size(3.6f * k, 3.4f * k))
        drawCircle(p.luz(C.MADERA), 1.1f * k, q(0f, -28.4f))
        drawPath(Path().apply { moveTo(q(-2f, -23f)); cubicTo(q(-12f, -30f), q(-17f, -18f), q(-11.5f, -11f)) }, p.medio(lerp(C.CUERO, C.HOLLIN, 0.35f)), style = Stroke(1.1f * k, cap = StrokeCap.Round))
        filoCalido(p, cuerpo, q(-12f, 0f).x, q(12f, 0f).x)
    }

    /** El cuchillo en su funda de madera con cantoneras de plata, con los palillos de comer al lado y la correa del cinto. */
    private fun DrawScope.cuchillo(p: Pincel, o: Offset, k: Float) {
        val L = lado(p)
        // Tendido en diagonal sobre la tapa, que se ve de arriba y en escorzo.
        val d = Offset(0.94f, -0.34f) * 1.2f; val n = Offset(0.34f, 0.94f) * 0.75f
        fun q(x: Float, y: Float) = o + (d * x + n * y) * k
        sombrita(p, o, 30f * k, 4f * k)
        val plata = lerp(C.PIEDRA, Color.White, 0.42f)
        // La correa, que se enrosca por la tapa.
        drawPath(Path().apply { moveTo(q(-14f, 1f)); cubicTo(q(-20f, 4f), q(-24f, -6f), q(-18f, -9f)); cubicTo(q(-13f, -11f), q(-12f, -4f), q(-16f, -3f)) }, p.medio(lerp(C.CUERO, C.HOLLIN, 0.3f)), style = Stroke(1.2f * k, cap = StrokeCap.Round))
        // Los palillos de hueso, uno algo salido.
        for ((dy, sale) in listOf(-3.4f to 3f, -2.2f to 0f)) {
            drawLine(p.sombra(C.HOLLIN).copy(alpha = 0.3f), q(-6f, dy + 1.4f), q(12f + sale, dy + 1.4f), 1.2f * k)
            drawLine(p.por(lerp(C.FIELTRO, C.ORO, 0.2f), 0.4f), q(-6f, dy), q(12f + sale, dy), 1f * k, StrokeCap.Round)
        }
        // La funda: de madera oscura, con dos cantoneras de plata y la boca.
        val funda = poligono(listOf(q(-14f, -1.6f), q(6f, -2.6f), q(9f, 0f), q(6f, 2.6f), q(-14f, 1.6f)))
        drawPath(Path().apply { addPath(funda, Offset(0f, 1.2f * k)) }, p.sombra(C.HOLLIN).copy(alpha = 0.35f))
        drawPath(funda, Brush.linearGradient(listOf(p.por(C.MADERA_OSC, 0.5f), p.por(C.MADERA_OSC, -0.5f)), q(0f, -2.6f), q(0f, 2.6f)))
        for ((x0, x1) in listOf(-14f to -10.4f, 4f to 9f)) drawPath(poligono(listOf(q(x0, -2f), q(x1, -2.4f), q(x1, 2.4f), q(x0, 2f))), Brush.linearGradient(listOf(p.luz(plata), p.por(plata, -0.3f)), q(0f, -2.4f), q(0f, 2.4f)))
        for (x in listOf(-12f, 6.5f)) drawCircle(p.medio(lerp(C.ROJO, C.ORO, 0.2f)), 0.8f * k, q(x, 0f))
        // El cuchillo, medio sacado: la hoja que brilla y el mango de asta.
        drawPath(poligono(listOf(q(-14f, -1.1f), q(-22f, -0.9f), q(-26f, 0.3f), q(-22f, 1.1f), q(-14f, 1.1f))), Brush.linearGradient(listOf(p.luz(lerp(plata, Color.White, 0.4f)), p.por(lerp(C.PIEDRA, C.HOLLIN, 0.2f), -0.2f)), q(0f, -1.1f), q(0f, 1.1f)))
        drawLine(p.luz(Color.White).copy(alpha = 0.7f * (1f - 0.5f * p.l.noche)), q(-15f, -0.4f), q(-23f, -0.2f), 0.4f * k)
        drawPath(poligono(listOf(q(9f, -1.6f), q(17f, -1.8f), q(18.6f, 0f), q(17f, 1.8f), q(9f, 1.6f))), Brush.linearGradient(listOf(p.por(lerp(C.ORO, C.CUERO, 0.5f), 0.6f), p.por(lerp(C.CUERO, C.HOLLIN, 0.3f), -0.4f)), q(0f, -1.8f), q(0f, 1.8f)))
        drawLine(p.luz(plata), q(9f, -1.8f), q(9f, 1.8f), 0.8f * k)
        if (p.calida > 0.05f) drawLine(p.lumbre.copy(alpha = 0.6f * p.calida), q(-15f, -0.4f), q(-23f, -0.2f), 0.6f * k, blendMode = BlendMode.Plus)
    }

    /** El eslabón: la bolsita de cuero en media luna con el hierro curvo en el canto, de pie; al lado, el pedernal y la yesca. */
    private fun DrawScope.eslabon(p: Pincel, o: Offset, k: Float) {
        val L = lado(p)
        fun q(x: Float, y: Float) = Offset(o.x + x * k, o.y + y * k)
        sombrita(p, o, 22f * k, 12f * k)
        contacto(p, q(-1f, 0f), 26f * k, 3f * k)
        // La yesca: un mechón de fibras secas, y el pedernal, gris, de facetas.
        val yesca = lerp(C.ESTEPA, C.FIELTRO, 0.3f)
        val az = Azar(Semilla.de("mongol", "objeto", "yesca"))
        repeat(16) { val a = q(-13f + az.entre(-2f, 2f), -0.5f); drawLine(p.por(yesca, if (it % 2 == 0) 0.5f else -0.3f), a, a + Offset(az.entre(-3f, 3f) * k, -az.entre(1.5f, 4f) * k), 0.45f * k, StrokeCap.Round) }
        val piedra = lerp(C.PIEDRA, C.HOLLIN, 0.25f)
        val pedernal = poligono(listOf(q(7f, 0f), q(6.5f, -4f), q(9f, -6.4f), q(12.6f, -5.6f), q(14f, -2f), q(13f, 0f)))
        drawPath(pedernal, p.por(piedra, -0.4f))
        drawPath(poligono(listOf(q(9f, -6.4f), q(12.6f, -5.6f), q(14f, -2f), q(10.6f, -3f))), p.por(lerp(piedra, C.FIELTRO, 0.2f), 0.6f * L))
        drawLine(p.luz(Color.White).copy(alpha = 0.5f), q(9.2f, -6f), q(12.2f, -5.4f), 0.35f * k)
        // La bolsa, de pie sobre el hierro: media luna de cuero negro con la chapa de plata y la cuenta de coral.
        val bolsa = Path().apply { moveTo(q(-9f, -12f)); lineTo(q(5f, -12f)); cubicTo(q(6.5f, -6f), q(3.6f, -1.4f), q(-2f, -1.4f)); cubicTo(q(-7.6f, -1.4f), q(-10.5f, -6f), q(-9f, -12f)); close() }
        val negro = lerp(C.HOLLIN, C.CUERO, 0.35f)
        drawPath(bolsa, Brush.linearGradient(if (L > 0f) listOf(p.por(negro, -0.6f), p.por(negro, 0.7f)) else listOf(p.por(negro, 0.7f), p.por(negro, -0.6f)), q(-9f, -7f), q(5f, -7f)))
        clipPath(bolsa) { drawPath(bolsa, p.luz(lerp(negro, C.FIELTRO, 0.35f)).copy(alpha = 0.4f), style = Stroke(1.2f * k)) }
        val plata = lerp(C.PIEDRA, Color.White, 0.45f)
        drawPath(poligono(listOf(q(-5f, -11f), q(1f, -11f), q(2f, -7f), q(-2f, -4.6f), q(-6f, -7f))), Brush.linearGradient(listOf(p.luz(plata), p.por(plata, -0.4f)), q(-6f, -11f), q(2f, -5f)))
        drawPath(poligono(listOf(q(-5f, -11f), q(1f, -11f), q(2f, -7f), q(-2f, -4.6f), q(-6f, -7f))), p.sombra(plata), style = Stroke(0.4f * k))
        drawCircle(p.por(lerp(C.ROJO, C.FIELTRO, 0.15f), 0.4f), 1.4f * k, q(-2f, -8f)); drawCircle(p.luz(Color.White).copy(alpha = 0.6f), 0.4f * k, q(-2.4f, -8.5f))
        // El hierro, curvo, que asoma por el canto de abajo, con sus dos puntas vueltas.
        val hierro = Path().apply { moveTo(q(-11f, -5f)); cubicTo(q(-9f, -0.4f), q(-4f, 0.2f), q(-2f, 0.2f)); cubicTo(q(1f, 0.2f), q(6f, -0.4f), q(7.4f, -5f)) }
        drawPath(hierro, p.por(lerp(C.HOLLIN, C.PIEDRA, 0.4f), -0.2f), style = Stroke(1.6f * k, cap = StrokeCap.Round))
        drawPath(hierro, p.luz(lerp(C.PIEDRA, Color.White, 0.3f)).copy(alpha = 0.7f), style = Stroke(0.45f * k, cap = StrokeCap.Round))
        // La correa con su anilla, de la que se cuelga del cinto.
        drawLine(p.medio(lerp(C.CUERO, C.HOLLIN, 0.3f)), q(-2f, -12f), q(-2f, -16f), 1f * k)
        drawCircle(p.por(plata, 0.3f), 1.6f * k, q(-2f, -17.4f), style = Stroke(0.6f * k))
        filoCalido(p, bolsa, q(-10f, 0f).x, q(6f, 0f).x)
    }

    /** El cuenco de madera de raíz, con el borde de plata y el té con leche; al lado, unos trozos de cuajada seca. */
    private fun DrawScope.cuenco(p: Pincel, o: Offset, k: Float) {
        val L = lado(p)
        fun q(x: Float, y: Float) = Offset(o.x + x * k, o.y + y * k)
        sombrita(p, o, 20f * k, 12f * k)
        contacto(p, q(0f, 0f), 18f * k, 3f * k)
        // La cuajada seca, en trozos, al lado.
        val cuajada = lerp(C.FIELTRO, C.ESTEPA, 0.2f)
        for ((x, y, w) in listOf(Triple(14f, -0.6f, 3.2f), Triple(17.4f, 0.2f, 2.6f), Triple(15.6f, -3.6f, 2.8f))) {
            drawPath(poligono(listOf(q(x - w, y), q(x + w, y - 0.2f), q(x + w * 0.9f, y - w * 0.9f), q(x - w * 0.8f, y - w))), p.por(cuajada, -0.3f))
            drawPath(poligono(listOf(q(x - w * 0.8f, y - w), q(x + w * 0.9f, y - w * 0.9f), q(x + w * 0.6f, y - w * 1.3f), q(x - w, y - w * 1.35f))), p.por(cuajada, 0.7f))
        }
        val madera = lerp(C.MADERA, C.ORO, 0.12f)
        // El pie, el cuerpo y el borde.
        drawPath(poligono(listOf(q(-5f, 0f), q(5f, 0f), q(4.4f, -2f), q(-4.4f, -2f))), p.por(madera, -0.4f))
        val cuerpo = Path().apply { moveTo(q(-11f, -11f)); cubicTo(q(-11f, -5f), q(-7f, -1.6f), q(0f, -1.6f)); cubicTo(q(7f, -1.6f), q(11f, -5f), q(11f, -11f)); close() }
        drawPath(cuerpo, Brush.linearGradient(if (L > 0f) listOf(p.por(madera, -0.8f), p.por(madera, 0.1f), p.por(madera, 0.75f)) else listOf(p.por(madera, 0.75f), p.por(madera, 0.1f), p.por(madera, -0.8f)), q(-11f, -6f), q(11f, -6f)))
        clipPath(cuerpo) {
            // La veta de la raíz: aguas que se enroscan, y una línea de torno.
            val veta = p.sombra(lerp(madera, C.HOLLIN, 0.3f)).copy(alpha = 0.45f)
            for (j in 0 until 5) drawPath(Path().apply { moveTo(q(-11f + j * 4.6f, -10f)); cubicTo(q(-9f + j * 4.6f, -7f), q(-12f + j * 4.6f, -5f), q(-10f + j * 4.6f, -1.6f)) }, veta, style = Stroke(0.4f * k))
            drawCircle(veta, 1.6f * k, q(3f, -6f), style = Stroke(0.35f * k))
            drawLine(p.luz(lerp(madera, C.FIELTRO, 0.3f)).copy(alpha = 0.5f), q(-10.4f, -8.6f), q(10.4f, -8.6f), 0.4f * k)
        }
        // El borde de plata (un aro ancho, que brilla) y el té con leche, con su nata.
        val plata = lerp(C.PIEDRA, Color.White, 0.45f)
        drawOval(Brush.linearGradient(listOf(p.luz(plata), p.por(plata, -0.4f), p.luz(plata)), q(-11.4f, -12f), q(11.4f, -12f)), Offset(q(-11.4f, 0f).x, q(0f, -14.2f).y), Size(22.8f * k, 5.4f * k))
        drawOval(p.por(lerp(C.LANA, C.CUERO, 0.3f), 0.2f), Offset(q(-9.6f, 0f).x, q(0f, -13.6f).y), Size(19.2f * k, 4.1f * k))
        drawOval(p.luz(lerp(C.LANA, Color.White, 0.3f)).copy(alpha = 0.6f), Offset(q(-5f + L * 2f, 0f).x, q(0f, -13f).y), Size(8f * k, 1.4f * k))
        if (p.calida > 0.05f) drawOval(p.lumbre.copy(alpha = 0.5f * p.calida), Offset(q(-11.4f, 0f).x, q(0f, -14.2f).y), Size(22.8f * k, 5.4f * k), style = Stroke(0.8f * k), blendMode = BlendMode.Plus)
    }

    /** La copa de plata, de pie alto, con pájaros grabados y una franja de rayitas que son letras de una lengua lejana (sin letras de verdad). */
    private fun DrawScope.copa(p: Pincel, o: Offset, k: Float) {
        val L = lado(p)
        fun q(x: Float, y: Float) = Offset(o.x + x * k, o.y + y * k)
        sombrita(p, o, 13f * k, 22f * k)
        contacto(p, q(0f, 0f), 13f * k, 2.4f * k)
        val plata = lerp(C.PIEDRA, Color.White, 0.42f)
        val oscura = lerp(C.PIEDRA, C.HOLLIN, 0.45f)
        // La plata refleja lo que tiene alrededor: el cielo arriba, el suelo oscuro abajo, una raya de luz del lado del sol.
        fun metal(forma: Path, x0: Float, x1: Float) {
            drawPath(forma, Brush.linearGradient(if (L > 0f) listOf(p.por(oscura, -0.3f), p.por(plata, -0.2f), p.por(plata, 0.3f), p.luz(lerp(plata, Color.White, 0.5f)), p.por(plata, 0f)) else listOf(p.por(plata, 0f), p.luz(lerp(plata, Color.White, 0.5f)), p.por(plata, 0.3f), p.por(plata, -0.2f), p.por(oscura, -0.3f)), q(x0, 0f), q(x1, 0f)))
        }
        // El pie y el astil, con su nudo.
        val pie = poligono(listOf(q(-6f, 0f), q(6f, 0f), q(4.6f, -1.6f), q(1.2f, -2.6f), q(-1.2f, -2.6f), q(-4.6f, -1.6f)))
        metal(pie, -6f, 6f)
        val astil = poligono(listOf(q(-1.1f, -2.6f), q(1.1f, -2.6f), q(0.9f, -7f), q(-0.9f, -7f)))
        metal(astil, -1.2f, 1.2f)
        drawOval(p.por(plata, 0.2f), Offset(q(-2.2f, 0f).x, q(0f, -5.8f).y), Size(4.4f * k, 2f * k))
        // La taza, que se abre arriba; dentro, la penumbra con un poco de airag.
        val taza = Path().apply { moveTo(q(-7.6f, -19f)); cubicTo(q(-7.2f, -12f), q(-4.6f, -7.6f), q(0f, -7.2f)); cubicTo(q(4.6f, -7.6f), q(7.2f, -12f), q(7.6f, -19f)); close() }
        metal(taza, -7.6f, 7.6f)
        clipPath(taza) {
            // La franja grabada: tres pájaros en vuelo y, debajo, la fila de rayitas de la inscripción.
            val grabado = p.sombra(oscura).copy(alpha = 0.75f)
            for (j in 0 until 3) {
                val c = q(-4.6f + j * 4.6f, -15.4f + (j % 2) * 0.6f)
                drawPath(Path().apply { moveTo(c + Offset(-1.8f * k, -0.6f * k)); cubicTo(c + Offset(-0.9f * k, -1.5f * k), c + Offset(-0.3f * k, -0.6f * k), c); cubicTo(c + Offset(0.3f * k, -0.6f * k), c + Offset(0.9f * k, -1.5f * k), c + Offset(1.8f * k, -0.6f * k)) }, grabado, style = Stroke(0.45f * k, cap = StrokeCap.Round))
                drawOval(grabado, Offset(c.x - 0.6f * k, c.y - 0.2f * k), Size(1.2f * k, 0.8f * k))
            }
            drawLine(grabado.copy(alpha = 0.5f), q(-7f, -12.6f), q(7f, -12.6f), 0.3f * k)
            for (j in 0 until 18) { val x = -6.3f + j * 0.74f; drawLine(grabado.copy(alpha = 0.6f), q(x, -11.8f), q(x + (if (j % 3 == 0) 0.2f else 0f), -10.8f - (if (j % 4 == 1) 0.4f else 0f)), 0.28f * k) }
            drawLine(grabado.copy(alpha = 0.5f), q(-6.4f, -10.2f), q(6.4f, -10.2f), 0.3f * k)
        }
        drawOval(p.sombra(lerp(C.HOLLIN, C.CUERO, 0.3f)), Offset(q(-7.6f, 0f).x, q(0f, -20.3f).y), Size(15.2f * k, 2.6f * k))
        drawOval(p.por(lerp(C.LANA, C.ESTEPA, 0.2f), -0.1f), Offset(q(-5.6f, 0f).x, q(0f, -19.5f).y), Size(11.2f * k, 1.5f * k))
        drawOval(p.luz(C.ORO), Offset(q(-7.6f, 0f).x, q(0f, -20.3f).y), Size(15.2f * k, 2.6f * k), style = Stroke(0.6f * k))
        // El destello.
        val destello = q(L * 3.6f, -16.6f)
        drawCircle(Color.White.copy(alpha = 0.75f * (1f - 0.6f * p.l.noche)), 0.9f * k, destello)
        if (p.calida > 0.05f) { drawLine(p.lumbre.copy(alpha = 0.7f * p.calida), q(-L * 5.4f, -17.6f), q(-L * 3.6f, -9.6f), 0.9f * k, StrokeCap.Round, blendMode = BlendMode.Plus) }
    }
}
