package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.haloDeBruma
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * La otra orilla: la Cité con sus casas, el puerto, la panadería, la iglesia pequeña y el caserón; Notre-Dame por
 * encima de los tejados; el puente de Notre-Dame con su bomba, y lo que se ve río abajo. Todo lo dibuja lejos
 * (`Luces.tono` con su distancia) y con la niebla de hoy (`EscenaRevolucion.perdido`).
 */
internal object CiudadRevolucion {
    private val C = ColorRevolucion
    private val E = EscenaRevolucion
    private const val H = EscenaRevolucion.H
    private const val WL = EscenaRevolucion.ORILLA

    /** El muelle del puerto: su borde de arriba. */
    private const val MUELLE = 123.2f

    /** Una casa de la orilla. [techo]: 0, pizarra con buhardillas; 1, teja; 2, mansarda. [base]: dónde pisa (el agua o el muelle). */
    internal class Casa(val x0: Float, val x1: Float, val top: Float, val muro: Color, val techo: Int, val pisos: Int, val vanos: Int, val base: Float = WL, val entramado: Boolean = false, val galeria: Boolean = false, val postigo: Color = ColorRevolucion.PIZARRA, val tienda: Boolean = false)

    /** Las casas de la primera fila, de izquierda a derecha. Entre la sexta y la séptima está el puerto. */
    internal val CASAS = listOf(
        Casa(46f, 66f, 99f, C.CAL, 1, 4, 3, entramado = true, galeria = true, postigo = C.MADERA_OSC),
        Casa(66f, 80f, 92f, C.OCRE, 0, 5, 2, postigo = lerp(C.HOJA, C.PIZARRA, 0.5f)),
        Casa(80f, 98f, 97f, C.ROSA, 1, 4, 3, galeria = true, postigo = C.MADERA),
        Casa(98f, 110f, 89f, C.PIEDRA, 0, 6, 2, postigo = lerp(C.GRANZA, C.MADERA_OSC, 0.5f)),
        Casa(110f, 129f, 98f, C.CAL, 1, 4, 3, galeria = true, postigo = lerp(C.ANIL, C.PIZARRA, 0.5f)),
        Casa(129f, 148f, 93f, lerp(C.OCRE, C.PIEDRA, 0.5f), 2, 5, 3, postigo = C.MADERA_OSC),
        Casa(150f, 169f, 102f, C.ROSA, 1, 3, 3, base = MUELLE, postigo = lerp(C.HOJA, C.PIZARRA, 0.4f), tienda = true),          // la panadería
        Casa(244f, 256f, 95f, C.OCRE, 0, 5, 2, base = MUELLE, postigo = C.MADERA),
        Casa(256f, 270f, 100f, C.CAL, 1, 4, 2, base = MUELLE, entramado = true, postigo = lerp(C.GRANZA, C.MADERA_OSC, 0.6f)),
    )

    /** El caserón de piedra, entre la iglesia y el puente. */
    internal val CASERON = Rect(202f, 88f, 244f, MUELLE)

    private fun t(m: Momento, l: Luces, c: Color, lejos: Float): Color = l.tono(c, E.perdido(m, lejos))

    // ---------------------------------------------------------------- Lo lejano

    /** La colina de la orilla izquierda, con sus torres, y el río abajo: las casas del muelle, otro puente y el Louvre. */
    fun DrawScope.lejania(m: Momento, l: Luces, r: Rayo) {
        val az = Azar(Semilla.de("revolucion", "lejania"))
        val lj = 0.84f
        val cresta = perfil(az, 30f, 300f, 113f, 9f, 0.5f, 30)
        drawPath(curva(cresta, fondo = WL), t(m, l, lerp(C.PIZARRA, C.PIEDRA, 0.45f), lj))
        // Sus torres y sus tejados grandes, apenas unas siluetas con un lado claro.
        for ((x, alto) in listOf(152f to 9f, 176f to 13f, 214f to 8f, 238f to 11f, 262f to 7f)) {
            val y = 108f - alto
            drawRect(t(m, l, C.PIEDRA_OSC, lj - 0.04f), Offset(x, y), Size(3.2f, alto + 6f))
            drawRect(t(m, l, C.PIEDRA, lj - 0.06f).copy(alpha = 0.6f * r.dia), Offset(if (r.lat > 0f) x + 2f else x, y), Size(1.2f, alto + 6f))
            drawPath(poligono(Offset(x - 0.4f, y), Offset(x + 3.6f, y), Offset(x + 1.6f, y - 4.5f)), t(m, l, C.PIZARRA, lj - 0.04f))
        }
        // Río abajo, por detrás del puente: el muelle lejano, sus casas, el otro puente y el Louvre, «con sus mil ventanas».
        val lr = 0.72f
        drawRect(t(m, l, C.PIEDRA_OSC, lr), Offset(256f, 121f), Size(ANCHO - 256f, 9f))
        val casas = Path().apply {
            moveTo(256f, 124f); var x = 256f
            while (x < 318f) { val w = az.entre(5f, 9f); val y = az.entre(111f, 117f); lineTo(x, y); lineTo(x + w * 0.5f, y - 2.4f); lineTo(x + w, y); x += w }
            lineTo(318f, 124f); close()
        }
        drawPath(casas, t(m, l, lerp(C.PIEDRA, C.PIZARRA, 0.3f), lr))
        drawRect(t(m, l, C.PIEDRA, lr), Offset(316f, 113f), Size(ANCHO - 316f, 10f))
        drawRect(t(m, l, C.PIZARRA, lr), Offset(316f, 110.4f), Size(ANCHO - 316f, 2.8f))
        for (x in listOf(318f, 344f, 370f)) { drawRect(t(m, l, C.PIEDRA, lr - 0.03f), Offset(x, 108f), Size(9f, 15f)); drawPath(poligono(Offset(x - 0.5f, 108f), Offset(x + 9.5f, 108f), Offset(x + 7f, 103.6f), Offset(x + 2f, 103.6f)), t(m, l, C.PIZARRA, lr - 0.03f)) }
        val ventanas = ArrayList<Offset>()
        for (y in listOf(115.4f, 118.4f, 121.2f)) { var x = 317.5f; while (x < ANCHO) { ventanas += Offset(x, y); x += 2.1f } }
        drawPoints(ventanas, PointMode.Points, if (E.ventanas(m) > 0.3f) C.ACEITE.copy(alpha = 0.55f) else t(m, l, C.PIZARRA_OSC, lr).copy(alpha = 0.6f), 0.7f, StrokeCap.Round)
        // El otro puente, lejos: una raya de piedra con sus ojos.
        drawRect(t(m, l, C.PIEDRA, lr - 0.06f), Offset(262f, 124.4f), Size(74f, 2.2f))
        for (k in 0..6) drawOval(t(m, l, C.SENA_HONDO, 0.5f), Offset(264f + k * 10.4f, 125.4f), Size(6.2f, 4.2f))
    }

    // ---------------------------------------------------------------- Notre-Dame

    /** Cuánto se agranda Notre-Dame respecto de su primer dibujo (lo importante, grande), y desde dónde: su pie, entre las dos torres. */
    private const val TALLA_CATEDRAL = 1.5f
    private val PIE_CATEDRAL = Offset(142f, 100f)

    /** Las dos torres en el cuadro: el borde izquierdo de cada una, su ancho y la altura de su remate. Las usan su reflejo y la cuerda de tender. */
    internal val TORRES: List<Float> = listOf(122f, 135.4f).map { PIE_CATEDRAL.x + (it - PIE_CATEDRAL.x) * TALLA_CATEDRAL }
    internal const val ANCHO_TORRE = 10.4f * TALLA_CATEDRAL
    internal const val REMATE_TORRES = 100f - 50f * TALLA_CATEDRAL

    /**
     * Notre-Dame por encima de los tejados, vista desde el norte: las dos torres a la derecha (el poniente), con sus
     * contrafuertes y los vanos de las campanas; la nave con sus arbotantes, el hastial del crucero con su rosetón y el
     * ábside. Sin aguja: la medieval se desmontó entre 1786 y 1792. Se dibuja en sus medidas de origen y se agranda entera.
     */
    fun DrawScope.catedral(m: Momento, l: Luces, r: Rayo) {
        val lj = 0.44f; val k = r.contraste * r.dia
        val dorado = lerp(C.PIEDRA, Color(0xFFFFE0A0), 0.4f * k * (1f - l.nublado))
        val piedra = t(m, l, C.PIEDRA, lj); val luz = t(m, l, dorado, lj - 0.04f); val sombra = t(m, l, mult(C.PIEDRA, r.color, 0.75f * k), lj)
        val plomo = t(m, l, lerp(C.PIZARRA, C.PIEDRA, 0.22f), lj); val hueco = t(m, l, C.PIZARRA_OSC, lj)
        val der = r.lat > 0f
        scale(TALLA_CATEDRAL, TALLA_CATEDRAL, PIE_CATEDRAL) {
            // El ábside, redondo, con su tejado.
            drawPath(Path().apply { moveTo(66f, 100f); lineTo(66f, 84f); cubicTo(60f, 84f, 55f, 87f, 54f, 92f); lineTo(54f, 100f); close() }, if (der) sombra else piedra)
            drawPath(Path().apply { moveTo(66f, 84f); cubicTo(60f, 84f, 55f, 87f, 54f, 92f); cubicTo(57f, 82f, 61f, 76f, 66f, 73f); close() }, plomo)
            // La nave: el muro alto, los contrafuertes con sus pináculos, los arbotantes y los ventanales entre ellos.
            drawRect(piedra, Offset(64f, 79f), Size(60f, 21f))
            for (i in 1..6) drawLine(sombra.copy(alpha = 0.18f), Offset(64f, 79f + i * 3f), Offset(124f, 79f + i * 3f), 0.2f)
            for (i in 0..7) {
                val x = 66f + i * 7.1f
                drawRect(hueco.copy(alpha = 0.8f), Offset(x + 2.2f, 81.4f), Size(3.4f, 9f)); drawOval(hueco.copy(alpha = 0.8f), Offset(x + 2.2f, 79.8f), Size(3.4f, 3.4f))
                drawLine(piedra.copy(alpha = 0.8f), Offset(x + 3.9f, 80.4f), Offset(x + 3.9f, 90.4f), 0.3f)                                // el parteluz
                drawRect(if (der) luz else piedra, Offset(x, 79.6f), Size(1.5f, 20f)); drawRect(sombra, Offset(if (der) x else x + 0.9f, 79.6f), Size(0.6f, 20f))
                drawPath(poligono(Offset(x - 0.2f, 79.8f), Offset(x + 1.7f, 79.8f), Offset(x + 0.75f, 76.6f)), piedra)
                drawLine(piedra, Offset(x + 0.8f, 84f), Offset(x + 5.6f, 80f), 0.5f); drawLine(sombra.copy(alpha = 0.6f), Offset(x + 0.8f, 84.5f), Offset(x + 5.6f, 80.5f), 0.25f)     // el arbotante y su sombra
            }
            // El tejado de plomo, con su lado claro, y la barandilla de su pie.
            val tejado = poligono(Offset(62.5f, 79.4f), Offset(66f, 70.4f), Offset(122f, 70.4f), Offset(124f, 79.4f))
            drawPath(tejado, plomo)
            clipPath(tejado) { drawRect(t(m, l, lerp(C.PIZARRA, Color.White, 0.3f), lj).copy(alpha = 0.5f * k), Offset(62f, 70f), Size(64f, 3.4f)); for (i in 0..14) drawLine(hueco.copy(alpha = 0.25f), Offset(64f + i * 4.2f, 70.4f), Offset(63.4f + i * 4.2f, 79.4f), 0.3f) }
            for (i in 0..28) drawRect(piedra, Offset(64.4f + i * 2.05f, 78.5f), Size(0.9f, 1f))
            drawLine(luz, Offset(66f, 70.4f), Offset(122f, 70.4f), 0.35f)
            // El crucero del norte: su hastial con el rosetón, entre dos torrecillas.
            drawPath(poligono(Offset(89f, 100f), Offset(89f, 74f), Offset(95.5f, 65.4f), Offset(102f, 74f), Offset(102f, 100f)), if (der) piedra else luz)
            drawRect(sombra, Offset(if (der) 89f else 100.4f, 74f), Size(1.6f, 26f))
            for (tx in listOf(87.8f, 101.6f)) { drawRect(piedra, Offset(tx, 70f), Size(1.6f, 30f)); drawRect(if (der) luz else sombra, Offset(tx + 1f, 70f), Size(0.6f, 30f)); drawPath(poligono(Offset(tx - 0.2f, 70.2f), Offset(tx + 1.8f, 70.2f), Offset(tx + 0.8f, 66f)), plomo) }
            drawCircle(hueco, 3.3f, Offset(95.5f, 79f)); drawCircle(piedra, 3.3f, Offset(95.5f, 79f), style = Stroke(0.6f)); drawCircle(piedra.copy(alpha = 0.7f), 1.2f, Offset(95.5f, 79f), style = Stroke(0.4f))
            for (i in 0..5) { val ang = i * PI.toFloat() / 6f; drawLine(piedra.copy(alpha = 0.7f), Offset(95.5f - cos(ang) * 3.2f, 79f - sin(ang) * 3.2f), Offset(95.5f + cos(ang) * 3.2f, 79f + sin(ang) * 3.2f), 0.22f) }   // la tracería
            for (i in 0..4) drawRect(hueco.copy(alpha = 0.55f), Offset(90.7f + i * 2.05f, 83.4f), Size(1.2f, 1.9f))                           // la galería de debajo
            drawRect(hueco.copy(alpha = 0.7f), Offset(92.4f, 86.4f), Size(2.2f, 8f)); drawRect(hueco.copy(alpha = 0.7f), Offset(96.4f, 86.4f), Size(2.2f, 8f))
            drawPath(poligono(Offset(94.9f, 65.6f), Offset(96.1f, 65.6f), Offset(95.5f, 62.6f)), piedra)
            // Entre las torres, la galería.
            drawRect(sombra, Offset(131f, 66f), Size(6f, 34f))
            for (i in 0..2) drawRect(hueco.copy(alpha = 0.5f), Offset(132.2f + i * 1.2f, 67f), Size(0.6f, 4f))
            // Las dos torres: el lado del sol, dorado; los contrafuertes de las esquinas, con sus retallos; los huecos altos
            // de las campanas, con sus tablillas; la galería de arquillos y la crestería.
            for (x in listOf(122f, 135.4f)) {
                drawRect(piedra, Offset(x, 50f), Size(10.4f, 50f))
                drawRect(luz, Offset(if (der) x + 6.4f else x, 50f), Size(4f, 50f))
                drawRect(sombra, Offset(if (der) x else x + 8.6f, 50f), Size(1.8f, 50f))
                for (i in 1..15) drawLine(sombra.copy(alpha = 0.16f), Offset(x, 50f + i * 3.1f), Offset(x + 10.4f, 50f + i * 3.1f), 0.2f)                // las hiladas
                for (bx in listOf(x - 0.5f, x + 9.5f)) {
                    val alSol = (bx > x + 5f) == der
                    drawRect(if (alSol) luz else piedra, Offset(bx, 52f), Size(1.4f, 48f)); drawRect(sombra, Offset(if (der) bx else bx + 1f, 52f), Size(0.4f, 48f))
                    for (y in listOf(58f, 67f, 75f, 86f)) drawRect(if (alSol) luz else piedra, Offset(bx - 0.25f, y), Size(1.9f, 0.5f))
                    drawPath(poligono(Offset(bx - 0.1f, 52f), Offset(bx + 1.5f, 52f), Offset(bx + 0.7f, 48.8f)), piedra)
                }
                for (dx in listOf(1.9f, 5.9f)) {
                    drawRect(hueco, Offset(x + dx, 54.6f), Size(2.5f, 10.4f)); drawOval(hueco, Offset(x + dx, 53.2f), Size(2.5f, 2.8f))
                    for (i in 0..5) drawLine(piedra.copy(alpha = 0.45f), Offset(x + dx, 56f + i * 1.6f), Offset(x + dx + 2.5f, 56.5f + i * 1.6f), 0.25f)
                    drawLine(luz, Offset(x + dx + (if (der) 0f else 2.5f), 54.4f), Offset(x + dx + (if (der) 0f else 2.5f), 65f), 0.25f)
                }
                drawRect(sombra, Offset(x - 0.4f, 66.4f), Size(11.2f, 1f)); drawRect(luz, Offset(x - 0.4f, 65.6f), Size(11.2f, 0.8f))
                for (i in 0..5) drawRect(hueco.copy(alpha = 0.55f), Offset(x + 0.8f + i * 1.6f, 68.6f), Size(0.8f, 4.4f))                // la galería de arquillos
                drawRect(sombra.copy(alpha = 0.7f), Offset(x - 0.2f, 74f), Size(10.8f, 0.5f))
                drawRect(hueco.copy(alpha = 0.65f), Offset(x + 3.9f, 76f), Size(2.6f, 9f)); drawOval(hueco.copy(alpha = 0.65f), Offset(x + 3.9f, 74.8f), Size(2.6f, 2.6f))
                for (i in 0..4) drawRect(piedra, Offset(x + i * 2.4f, 48.6f), Size(0.9f, 1.6f))                                         // la crestería
                drawRect(luz, Offset(x - 0.3f, 49.6f), Size(11f, 0.7f))
            }
            if (E.nevado(m)) { drawRect(t(m, l, C.CAL, lj - 0.1f).copy(alpha = 0.85f), Offset(65f, 70f), Size(58f, 2.8f)); for (x in listOf(122f, 135.4f)) drawRect(t(m, l, C.CAL, lj - 0.1f).copy(alpha = 0.85f), Offset(x, 49.2f), Size(10.4f, 1.1f)) }
            // Las campanas echan a volar a las palomas de las torres.
            if (E.campanas(m) && l.noche < 0.5f) {
                val az = m.azar("palomas")
                repeat(16) { val o = Offset(az.entre(104f, 166f), az.entre(36f, 60f)); val w = az.entre(0.8f, 1.4f); drawPath(Path().apply { moveTo(o.x - w, o.y - w * 0.4f); lineTo(o.x, o.y); lineTo(o.x + w, o.y - w * 0.5f) }, t(m, l, C.PIZARRA_OSC, 0.3f), style = Stroke(0.4f, cap = StrokeCap.Round)) }
            }
        }
    }

    // ---------------------------------------------------------------- La orilla de enfrente

    /**
     * Un árbol de ribera (un olmo) con su armazón: el tronco, que se abre en tres ramas maestras; de cada una, sus ramas;
     * y en cada rama, su racimo de hojas, con lo oscuro debajo y lo claro del lado de la luz. Entre los racimos se ven
     * las ramas. En otoño clarea y suelta hojas; en invierno queda pelado, con sus ramillas finas.
     */
    internal fun DrawScope.arbolDeRibera(m: Momento, l: Luces, r: Rayo, pie: Offset, alto: Float, lj: Float, az: Azar) {
        val a = alto; val lado = if (r.lat > 0f) 1f else -1f; val k = r.contraste * r.dia
        val corteza = t(m, l, lerp(C.MADERA_OSC, C.PIEDRA_OSC, 0.2f), lj); val cortezaLuz = t(m, l, lerp(C.MADERA, C.PIEDRA, 0.45f), lj); val cortezaSombra = t(m, l, mult(C.MADERA_OSC, r.color, 0.6f * k), lj)
        val est = m.ficha.estacion
        val hoja: Color? = when (est) { Estacion.INVIERNO -> null; Estacion.OTONO -> lerp(C.OCRE, C.GRANZA, 0.35f); Estacion.PRIMAVERA -> lerp(C.HOJA, C.OCRE, 0.3f); else -> C.HOJA }
        // El tronco: ancho en el pie, un poco torcido, hasta la cruz donde se abre.
        val cruz = pie + Offset(az.entre(-0.05f, 0.05f) * a, -a * 0.36f)
        drawPath(Path().apply {
            moveTo(pie.x - a * 0.085f, pie.y); cubicTo(pie.x - a * 0.04f, pie.y - a * 0.06f, cruz.x - a * 0.05f, cruz.y + a * 0.16f, cruz.x - a * 0.036f, cruz.y)
            lineTo(cruz.x + a * 0.036f, cruz.y); cubicTo(cruz.x + a * 0.04f, cruz.y + a * 0.16f, pie.x + a * 0.045f, pie.y - a * 0.06f, pie.x + a * 0.09f, pie.y); close() }, corteza)
        drawLine(cortezaLuz.copy(alpha = 0.75f * r.dia), pie + Offset(lado * a * 0.03f, -a * 0.02f), cruz + Offset(lado * a * 0.02f, 0f), a * 0.02f)
        drawLine(cortezaSombra, pie + Offset(-lado * a * 0.035f, -a * 0.02f), cruz + Offset(-lado * a * 0.024f, 0f), a * 0.018f)
        // Tres ramas maestras, en vaso; de cada una, tres ramas.
        val puntas = ArrayList<Pair<Offset, Float>>()                                        // dónde acaba cada rama y hacia dónde iba
        for ((i, abre) in listOf(-0.66f, -0.06f, 0.56f).withIndex()) {
            val ang = (-PI / 2).toFloat() + abre + az.entre(-0.08f, 0.08f); val largo = a * (if (i == 1) 0.46f else 0.38f) * az.entre(0.92f, 1.08f)
            val fin = cruz + Offset(cos(ang) * largo, sin(ang) * largo * 1.05f)
            val codo = cruz + Offset(cos(ang) * largo * 0.42f, sin(ang) * largo * 0.62f)
            drawPath(Path().apply { moveTo(cruz.x, cruz.y + a * 0.04f); cubicTo(cruz.x, cruz.y - a * 0.04f, codo.x, codo.y + a * 0.02f, codo.x, codo.y) }, corteza, style = Stroke(a * 0.045f, cap = StrokeCap.Round))
            drawLine(corteza, codo, fin, a * 0.028f, StrokeCap.Round)
            drawLine(cortezaLuz.copy(alpha = 0.5f * r.dia), codo + Offset(lado * a * 0.01f, 0f), fin + Offset(lado * a * 0.008f, 0f), a * 0.008f)
            puntas += fin to ang
            for ((kk, tuerce) in listOf(0.2f to -0.75f, 0.55f to 0.7f, 0.85f to -0.55f)) {
                val nace = codo + (fin - codo) * kk; val an = ang + tuerce + az.entre(-0.15f, 0.15f); val lr = a * az.entre(0.17f, 0.26f)
                val p = nace + Offset(cos(an) * lr, sin(an) * lr * 0.9f)
                drawLine(corteza, nace, p, a * 0.017f, StrokeCap.Round); puntas += p to an
            }
        }
        if (hoja == null) {
            // Pelado: de cada rama, un abanico de ramillas finas, y la pelusa de las más finas; la nieve, posada encima.
            mancha(cruz + Offset(0f, -a * 0.34f), a * 0.46f, a * 0.36f, t(m, l, lerp(C.MADERA_OSC, C.PIEDRA, 0.5f), lj), 0.16f)
            for ((p, an) in puntas) for (j in -2..2) {
                val af = an + j * 0.34f + az.entre(-0.1f, 0.1f); val lf = a * az.entre(0.07f, 0.14f); val q = p + Offset(cos(af) * lf, sin(af) * lf)
                drawLine(corteza.copy(alpha = 0.85f), p, q, a * 0.007f + 0.2f, StrokeCap.Round)
                drawLine(corteza.copy(alpha = 0.6f), q, q + Offset(cos(af + 0.5f) * lf * 0.5f, sin(af + 0.5f) * lf * 0.5f), 0.2f, StrokeCap.Round)
            }
            if (E.nevado(m)) { val nieve = t(m, l, C.CAL, lj - 0.1f); for ((p, _) in puntas) drawLine(nieve, p + Offset(-a * 0.05f, -0.3f), p + Offset(a * 0.04f, -0.5f), 0.6f, StrokeCap.Round); drawLine(nieve, cruz + Offset(-a * 0.035f, -0.2f), cruz + Offset(a * 0.035f, -0.2f), 0.7f, StrokeCap.Round) }
            return
        }
        // La copa: un racimo por rama. Primero lo oscuro de debajo (por los huecos se ven las ramas y el cielo), luego el
        // cuerpo de cada racimo y, del lado de la luz, lo claro.
        val escasa = when (est) { Estacion.OTONO -> 0.8f; Estacion.PRIMAVERA -> 0.88f; else -> 1f }
        val racimos = puntas.map { (p, _) -> Triple(p + Offset(az.entre(-0.03f, 0.03f) * a, -a * 0.03f), a * az.entre(0.105f, 0.15f) * escasa, az.real()) }.filter { est != Estacion.OTONO || it.third > 0.16f }
        fun matiz(v: Float) = lerp(hoja, if (v < 0.5f) C.PIZARRA_OSC else C.OCRE, 0.28f * abs(v - 0.5f))
        for ((c, rr, v) in racimos) { val osc = t(m, l, mult(lerp(matiz(v), C.BREA, 0.25f), r.color, 0.5f * k), lj); drawCircle(osc, rr, c + Offset(-lado * rr * 0.22f, rr * 0.3f)); drawCircle(osc, rr * 0.7f, c + Offset(lado * rr * 0.5f, rr * 0.5f)) }
        for ((c, rr, v) in racimos.sortedByDescending { it.first.y }) {                       // de abajo arriba: los de arriba pisan a los de abajo
            val cuerpo = t(m, l, matiz(v), lj); val claro = t(m, l, lerp(matiz(v), Color(0xFFF4EBB0), 0.36f * k + 0.04f), lj - 0.03f)
            drawCircle(cuerpo, rr * 0.86f, c + Offset(lado * rr * 0.1f, -rr * 0.08f))
            drawCircle(cuerpo, rr * 0.5f, c + Offset(-lado * rr * 0.6f, -rr * 0.1f))
            drawCircle(claro, rr * 0.52f, c + Offset(lado * rr * 0.34f, -rr * 0.36f))
            drawCircle(claro, rr * 0.28f, c + Offset(lado * rr * 0.02f, -rr * 0.66f))
        }
        // Las hojas sueltas del borde, las motas de sombra dentro de lo claro y los brillos: la copa no es una bola.
        val sueltas = ArrayList<Offset>(); val motas = ArrayList<Offset>(); val brillos = ArrayList<Offset>()
        for ((c, rr, _) in racimos) {
            repeat(6) { val an = az.entre(0f, 6.283f); val d = rr * az.entre(0.9f, 1.25f); sueltas += c + Offset(cos(an) * d, sin(an) * d * 0.9f) }
            repeat(4) { motas += c + Offset(az.entre(-0.6f, 0.6f) * rr, az.entre(-0.2f, 0.7f) * rr) }
            repeat(3) { brillos += c + Offset(lado * az.entre(0f, 0.7f) * rr, -az.entre(0.2f, 0.9f) * rr) }
        }
        drawPoints(sueltas, PointMode.Points, t(m, l, hoja, lj), a * 0.03f + 0.5f, StrokeCap.Round)
        drawPoints(motas, PointMode.Points, t(m, l, mult(hoja, r.color, 0.55f * k), lj).copy(alpha = 0.7f), a * 0.028f + 0.4f, StrokeCap.Round)
        drawPoints(brillos, PointMode.Points, t(m, l, lerp(hoja, Color(0xFFF4EBB0), 0.5f * k), lj - 0.03f).copy(alpha = 0.5f + 0.4f * r.dia), a * 0.026f + 0.4f, StrokeCap.Round)
        if (est == Estacion.OTONO) { val caidas = ArrayList<Offset>(); repeat(9) { caidas += pie + Offset(az.entre(-0.42f, 0.42f) * a, az.entre(-0.5f, 0.4f)) }; drawPoints(caidas, PointMode.Points, t(m, l, hoja, lj), 0.8f, StrokeCap.Round) }
        if (E.nevado(m)) for ((c, rr, _) in racimos) drawCircle(t(m, l, C.CAL, lj - 0.1f), rr * 0.4f, c + Offset(0f, -rr * 0.55f))
    }

    /** Las ventanas de la casa [k], en el orden en que se pintan: cómo tiene los postigos (0, cerrados; 1, entornados; 2 y 3, abiertos) y si tiene luz. */
    private fun ventanasDe(m: Momento, k: Int, c: Casa): List<Pair<Int, Boolean>> {
        val az = m.azar("ventanas", k); val luzV = E.ventanas(m); val cerrados = E.postigosCerrados(m)
        return buildList { for (p in 0 until c.pisos) for (v in 0 until c.vanos) { if (c.tienda && p == c.pisos - 1) continue; val estado = if (cerrados) 0 else az.entero(4); add(estado to (az.real() < luzV)) } }
    }

    /** Dónde hay luz en las casas de enfrente, para su reflejo en el río: la x de cada hilera de ventanas con alguna encendida, y cuántas tiene. */
    internal fun lucesDeEnfrente(m: Momento): List<Pair<Float, Int>> = buildList {
        if (E.ventanas(m) <= 0f) return@buildList
        for ((k, c) in CASAS.withIndex()) {
            val vs = ventanasDe(m, k, c); val ancho = (c.x1 - c.x0 - 2f) / c.vanos; val cuenta = IntArray(c.vanos); var n = 0
            for (p in 0 until c.pisos) for (v in 0 until c.vanos) { if (c.tienda && p == c.pisos - 1) continue; val (estado, encendida) = vs[n++]; if (encendida && estado != 0) cuenta[v]++ }
            for (v in 0 until c.vanos) if (cuenta[v] > 0) add((c.x0 + 1f + (v + 0.5f) * ancho) to cuenta[v])
        }
    }

    /** Una casa de la orilla: el muro con sus manchas, las ventanas con sus postigos, la cornisa y el tejado con buhardillas y chimeneas. */
    private fun DrawScope.casa(m: Momento, l: Luces, r: Rayo, k: Int, c: Casa, lj: Float) {
        val az = Azar(Semilla.de("revolucion", "casa", k)); val deHoy = ventanasDe(m, k, c); var cual = 0
        val w = c.x1 - c.x0; val alto = c.base - c.top
        val muro = t(m, l, c.muro, lj); val sucio = t(m, l, lerp(c.muro, C.PIEDRA_OSC, 0.5f), lj)
        drawRect(Brush.verticalGradient(0f to muro, 0.72f to lerp(muro, sucio, 0.3f), 1f to sucio, startY = c.top, endY = c.base), Offset(c.x0, c.top), Size(w, alto))
        clipRect(c.x0, c.top, c.x1, c.base) {
            repeat(3) { mancha(Offset(az.entre(c.x0, c.x1), az.entre(c.top + 4f, c.base - 3f)), az.entre(2.5f, 6f), az.entre(3f, 8f), sucio, 0.32f) }
            repeat(3) { val x = az.entre(c.x0 + 1f, c.x1 - 1f); drawLine(sucio.copy(alpha = 0.55f), Offset(x, c.top + 0.6f), Offset(x + az.entre(-0.4f, 0.4f), c.top + az.entre(5f, 13f)), 0.5f) }      // las chorreras del alero
            repeat(2) { val o = Offset(az.entre(c.x0 + 1.5f, c.x1 - 4f), az.entre(c.top + 7f, c.base - 7f)); drawOval(t(m, l, lerp(C.LADRILLO, C.PIEDRA_OSC, 0.35f), lj).copy(alpha = 0.6f), o, Size(az.entre(1.6f, 3.6f), az.entre(1.1f, 2.4f))) }   // desconchones
        }
        if (c.entramado) {
            val viga = t(m, l, C.MADERA_OSC, lj).copy(alpha = 0.8f)
            val paso = alto / c.pisos
            for (i in 1 until c.pisos) drawLine(viga, Offset(c.x0, c.top + i * paso), Offset(c.x1, c.top + i * paso), 0.6f)
            for (i in 0..3) { val x = c.x0 + w * i / 3f; drawLine(viga, Offset(x, c.top), Offset(x, c.base - paso), 0.5f) }
            for (i in 0 until 3) drawLine(viga, Offset(c.x0 + w * i / 3f, c.top + paso), Offset(c.x0 + w * (i + 1) / 3f, c.top), 0.4f)
        }
        // El zócalo de piedra, con el verdín de donde llega el agua.
        drawRect(t(m, l, C.PIEDRA_OSC, lj), Offset(c.x0, c.base - 4.4f), Size(w, 4.4f))
        if (c.base == WL) { drawRect(t(m, l, lerp(C.HOJA, C.BREA, 0.5f), lj).copy(alpha = 0.6f), Offset(c.x0, c.base - 1.3f), Size(w, 1.3f)); drawOval(t(m, l, C.BREA, lj).copy(alpha = 0.7f), Offset(c.x0 + w * 0.3f, c.base - 3f), Size(3.2f, 5f)) }
        // Las ventanas: cada una con su alféizar, su dintel en sombra y sus postigos (cerrados, entornados o abiertos).
        val paso = (alto - 6f) / c.pisos; val ancho = (w - 2f) / c.vanos
        val cerrados = E.postigosCerrados(m)
        val postigo = t(m, l, c.postigo, lj); val vidrio = t(m, l, lerp(C.PIZARRA_OSC, C.BREA, 0.4f), lj)
        val cierre = t(m, l, lerp(c.postigo, C.BREA, 0.5f), lj)                                 // los postigos echados del luto: oscuros, de lado a lado
        for (p in 0 until c.pisos) for (v in 0 until c.vanos) {
            if (c.tienda && p == c.pisos - 1) continue
            val hw = 2.8f; val hh = (paso * 0.66f).coerceAtMost(4.4f)
            val x = c.x0 + 1f + v * ancho + (ancho - hw) / 2f; val y = c.top + 2.2f + p * paso
            val (estado, encendida) = deHoy[cual++]
            drawRect(sucio.copy(alpha = 0.7f), Offset(x - 0.3f, y - 0.6f), Size(hw + 0.6f, 0.6f))                                       // la sombra del dintel
            drawRect(if (encendida && estado != 0) C.ACEITE else vidrio, Offset(x, y), Size(hw, hh))
            when (estado) {
                0 -> if (cerrados) { drawRect(cierre, Offset(x - 0.5f, y - 0.3f), Size(hw + 1f, hh + 0.5f)); drawLine(t(m, l, C.BREA, lj), Offset(x + hw / 2f, y - 0.3f), Offset(x + hw / 2f, y + hh + 0.2f), 0.35f); for (i in 1..2) drawLine(t(m, l, C.BREA, lj).copy(alpha = 0.5f), Offset(x - 0.5f, y + hh * i / 3f), Offset(x + hw + 0.5f, y + hh * i / 3f), 0.25f) }
                    else { drawRect(postigo, Offset(x, y), Size(hw / 2f - 0.15f, hh)); drawRect(postigo, Offset(x + hw / 2f + 0.15f, y), Size(hw / 2f - 0.15f, hh)) }
                1 -> { drawRect(postigo, Offset(x, y), Size(hw / 2f, hh)); drawRect(postigo, Offset(x + hw + 0.2f, y), Size(0.9f, hh)) }
                else -> { drawRect(postigo, Offset(x - 1.1f, y), Size(0.9f, hh)); drawRect(postigo, Offset(x + hw + 0.2f, y), Size(0.9f, hh)) }
            }
            if (encendida && estado == 0) drawLine(C.ACEITE.copy(alpha = 0.85f), Offset(x + hw / 2f, y + 0.4f), Offset(x + hw / 2f, y + hh - 0.4f), 0.35f)   // la rendija de luz entre los postigos
            drawRect(t(m, l, lerp(c.muro, Color.White, 0.3f), lj), Offset(x - 0.4f, y + hh), Size(hw + 0.8f, 0.5f))                       // el alféizar
            if (encendida && estado != 0) resplandor(Offset(x + hw / 2f, y + hh / 2f), 4.2f, 0.4f * l.noche, C.ACEITE)
            if (estado >= 2 && !encendida && az.entero(9) == 0 && l.noche < 0.5f) drawRect(t(m, l, C.CAL, lj), Offset(x + 0.2f, y + hh - 0.4f), Size(hw - 0.4f, 1.6f))   // un trapo al aire
        }
        // La galería de madera sobre el agua, con sus tornapuntas.
        if (c.galeria) {
            val gy = c.base - 12.5f; val gx = c.x0 + w * 0.5f
            drawRect(t(m, l, C.MADERA_OSC, lj), Offset(gx, gy), Size(w * 0.4f, 4.6f)); drawRect(t(m, l, C.MADERA, lj), Offset(gx, gy), Size(w * 0.4f, 0.8f))
            drawLine(t(m, l, C.MADERA_OSC, lj), Offset(gx + 0.6f, gy + 4.6f), Offset(gx - 0.2f, gy + 8.4f), 0.5f); drawLine(t(m, l, C.MADERA_OSC, lj), Offset(gx + w * 0.4f - 0.6f, gy + 4.6f), Offset(gx + w * 0.4f - 1.6f, gy + 8.4f), 0.5f)
        }
        // La tienda: el hueco del horno, con su toldo y su enseña (una gavilla dorada, sin letras).
        if (c.tienda) {
            val horno = E.lumbreDeLaPanaderia(m)
            drawPath(Path().apply { moveTo(c.x0 + 3f, c.base); lineTo(c.x0 + 3f, c.base - 6f); cubicTo(c.x0 + 3f, c.base - 9.6f, c.x0 + 10f, c.base - 9.6f, c.x0 + 10f, c.base - 6f); lineTo(c.x0 + 10f, c.base); close() }, if (horno) lerp(Color(0xFFFF9A4A), t(m, l, C.BREA, lj), 0.25f) else t(m, l, C.BREA, lj))
            if (horno) resplandor(Offset(c.x0 + 6.5f, c.base - 5f), 9f, 0.5f * (0.4f + 0.6f * l.noche), Color(0xFFFF9A4A))
            if (!cerrados) for (i in 0..3) drawRect(t(m, l, if (i % 2 == 0) C.CAL else C.GRANZA, lj), Offset(c.x0 + 11.4f + i * 1.6f, c.base - 9.4f), Size(1.6f, 2.6f))
            drawRect(if (cerrados) cierre else t(m, l, C.BREA, lj), Offset(c.x0 + 12f, c.base - 6.4f), Size(5.6f, 4f))
            if (cerrados) drawPath(Path().apply { moveTo(c.x0 + 3f, c.base); lineTo(c.x0 + 3f, c.base - 6f); cubicTo(c.x0 + 3f, c.base - 9.6f, c.x0 + 10f, c.base - 9.6f, c.x0 + 10f, c.base - 6f); lineTo(c.x0 + 10f, c.base); close() }, cierre)   // el tablero echado
            drawLine(t(m, l, C.HIERRO, lj), Offset(c.x0 + 2f, c.base - 12.4f), Offset(c.x0 - 1.6f, c.base - 12.4f), 0.4f)
            drawOval(t(m, l, lerp(C.ACEITE, C.OCRE, 0.4f), lj - 0.1f), Offset(c.x0 - 2.6f, c.base - 12f), Size(2.2f, 3.2f))
        }
        // La cornisa, con su sombra debajo.
        drawRect(t(m, l, lerp(c.muro, Color.White, 0.35f), lj), Offset(c.x0 - 0.5f, c.top - 0.5f), Size(w + 1f, 0.9f))
        with(E) { sombraDe(r, Offset(c.x0, c.top + 0.4f), Size(w, 1.2f), 0.5f) }
        tejado(m, l, r, c.x0, c.x1, c.top, c.techo, az, lj)
    }

    /** El tejado de una casa (0, pizarra; 1, teja; 2, mansarda), con sus buhardillas y sus chimeneas, y la nieve si la hay. */
    private fun DrawScope.tejado(m: Momento, l: Luces, r: Rayo, x0: Float, x1: Float, top: Float, tipo: Int, az: Azar, lj: Float) {
        val w = x1 - x0; val der = r.lat > 0f; val k = r.contraste * r.dia
        val propio = if (tipo == 1) C.TEJA else C.PIZARRA
        val h = when (tipo) { 0 -> 5f + w * 0.12f; 1 -> 3.6f + w * 0.06f; else -> 8f }
        val mete = if (tipo == 2) 1.4f else 2.6f
        val forma = poligono(Offset(x0 - 0.8f, top), Offset(x1 + 0.8f, top), Offset(x1 - mete, top - h), Offset(x0 + mete, top - h))
        val claro = t(m, l, lerp(propio, Color(0xFFF2E8D0), 0.28f * k), lj); val osc = t(m, l, mult(propio, r.color, 0.5f * k), lj)
        drawPath(forma, Brush.horizontalGradient(if (der) listOf(osc, t(m, l, propio, lj), claro) else listOf(claro, t(m, l, propio, lj), osc), x0, x1))
        clipPath(forma) {
            if (tipo == 1) { var x = x0; while (x < x1 + 1f) { drawLine(osc.copy(alpha = 0.5f), Offset(x, top), Offset(x + (x - (x0 + x1) / 2f) * -0.12f, top - h), 0.35f); x += 1.5f } }
            else for (i in 1..3) drawLine(osc.copy(alpha = 0.45f), Offset(x0 - 1f, top - h * i / 4f), Offset(x1 + 1f, top - h * i / 4f), 0.3f)
        }
        // Las buhardillas.
        val cuantas = when (tipo) { 0 -> if (w > 15f) 2 else 1; 1 -> 0; else -> 3 }
        for (i in 0 until cuantas) {
            val x = x0 + w * (i + 0.5f) / cuantas - 1.5f; val y = top - h * 0.7f
            drawRect(t(m, l, C.PIEDRA, lj), Offset(x, y), Size(3f, h * 0.62f)); drawRect(t(m, l, lerp(C.PIZARRA_OSC, C.BREA, 0.4f), lj), Offset(x + 0.6f, y + 0.6f), Size(1.8f, h * 0.62f - 1f))
            drawPath(poligono(Offset(x - 0.5f, y), Offset(x + 3.5f, y), Offset(x + 1.5f, y - 1.6f)), t(m, l, propio, lj))
            if (m.azar("buhardilla", x.toInt()).real() < E.ventanas(m) * 0.6f) drawRect(C.ACEITE, Offset(x + 0.6f, y + 0.6f), Size(1.8f, h * 0.62f - 1f))
        }
        // Las chimeneas, con su lado claro y sus caperuzas.
        for (i in 0 until (if (w > 16f) 2 else 1)) {
            val x = x0 + w * (if (i == 0) az.entre(0.12f, 0.3f) else az.entre(0.68f, 0.86f)); val alto = az.entre(4f, 7.5f) + (if (tipo == 2) 2f else 0f)
            val y = top - h - alto + 2f
            drawRect(t(m, l, C.LADRILLO, lj), Offset(x, y), Size(2.6f, alto + 1f)); drawRect(t(m, l, lerp(C.LADRILLO, Color(0xFFFFF0D0), 0.3f * k), lj), Offset(if (der) x + 1.7f else x, y), Size(0.9f, alto + 1f))
            drawRect(t(m, l, C.PIEDRA_OSC, lj), Offset(x - 0.4f, y - 0.6f), Size(3.4f, 0.8f)); for (dx in listOf(0.3f, 1.5f)) drawRect(t(m, l, C.TEJA, lj), Offset(x + dx, y - 2f), Size(0.8f, 1.5f))
        }
        if (E.nevado(m)) { val nieve = t(m, l, C.CAL, lj - 0.1f); clipPath(forma) { drawRect(nieve.copy(alpha = 0.9f), Offset(x0 - 1f, top - h), Size(w + 2f, h * 0.62f)); drawRect(nieve.copy(alpha = 0.5f), Offset(x0 - 1f, top - h * 0.38f), Size(w + 2f, h * 0.2f)) } }
    }

    /** La Cité: la segunda fila de tejados, los árboles del jardín, el puerto, las casas, la iglesia pequeña y el caserón. */
    fun DrawScope.orilla(m: Momento, l: Luces, r: Rayo) {
        val fijo = Azar(Semilla.de("revolucion", "orilla"))
        val der = r.lat > 0f
        // La segunda fila: tejados y chimeneas de las calles de detrás, más pálidos.
        for ((i, f) in listOf(Rect(142f, 91f, 158f, 124f), Rect(156f, 85f, 176f, 124f), Rect(188f, 87f, 206f, 124f), Rect(232f, 81f, 250f, 124f), Rect(248f, 88f, 268f, 124f)).withIndex()) {
            val lj = 0.6f
            drawRect(t(m, l, if (i % 2 == 0) lerp(C.OCRE, C.PIEDRA, 0.5f) else C.PIEDRA_OSC, lj), f.topLeft, f.size)
            drawRect(t(m, l, C.PIEDRA, lj).copy(alpha = 0.5f * r.dia), Offset(if (der) f.right - 3f else f.left, f.top), Size(3f, f.height))
            val pts = ArrayList<Offset>(); for (p in 0..3) for (v in 0..2) pts += Offset(f.left + 3f + v * (f.width - 6f) / 2f, f.top + 4f + p * 5.2f)
            drawPoints(pts, PointMode.Points, if (E.ventanas(m) > 0.3f && i % 2 == 0) C.ACEITE.copy(alpha = 0.7f) else t(m, l, C.PIZARRA_OSC, lj).copy(alpha = 0.7f), 1.3f)
            tejado(m, l, r, f.left, f.right, f.top, if (i % 2 == 0) 0 else 1, Azar(Semilla.de("revolucion", "fila", i)), lj)
        }
        // Los árboles del jardín, a la izquierda, por detrás de la primera casa.
        arbolDeRibera(m, l, r, Offset(53f, 112f), 31f, 0.44f, Azar(Semilla.de("revolucion", "arbol", 1)))
        // El muelle del puerto: el muro de sillares, la escalera que baja al agua y lo que hay encima.
        val piedra = t(m, l, C.PIEDRA, 0.34f); val junta = t(m, l, C.PIEDRA_OSC, 0.34f)
        drawRect(piedra, Offset(148f, MUELLE), Size(124f, WL - MUELLE))
        drawRect(t(m, l, lerp(C.PIEDRA, Color.White, 0.3f), 0.3f), Offset(148f, MUELLE - 0.3f), Size(124f, 0.8f))
        for (i in 0..2) drawLine(junta.copy(alpha = 0.5f), Offset(148f, MUELLE + 1.8f + i * 2f), Offset(272f, MUELLE + 1.8f + i * 2f), 0.3f)
        run { var x = 149f; var i = 0; while (x < 272f) { drawLine(junta.copy(alpha = 0.45f), Offset(x, MUELLE + 1.8f + (i % 3) * 2f - 2f), Offset(x, MUELLE + 1.8f + (i % 3) * 2f), 0.3f); x += fijo.entre(3f, 6f); i++ } }
        drawRect(t(m, l, lerp(C.HOJA, C.BREA, 0.5f), 0.34f).copy(alpha = 0.6f), Offset(148f, WL - 1.2f), Size(124f, 1.2f))
        for (i in 0..3) drawRect(t(m, l, lerp(C.PIEDRA, Color.White, 0.2f - 0.05f * i), 0.33f), Offset(175f + i * 0.8f, MUELLE + 0.6f + i * 1.6f), Size(11f - i * 1.6f, 1f))                 // la escalera
        for (x in listOf(164f, 208f, 238f)) { drawCircle(t(m, l, C.HIERRO, 0.3f), 0.9f, Offset(x, MUELLE + 3f), style = Stroke(0.4f)) }                                                 // las argollas
        // Las casas, la iglesia pequeña y el caserón.
        for ((k, c) in CASAS.withIndex()) casa(m, l, r, k, c, 0.36f)
        iglesia(m, l, r)
        caseron(m, l, r)
        // La sombra que cada casa alta echa sobre la vecina del lado contrario al sol.
        if (r.fuerza > 0.05f) for (i in 0 until CASAS.size) {
            val c = CASAS[i]; val j = if (der) i + 1 else i - 1
            val otra = CASAS.getOrNull(j) ?: continue
            if (abs((if (der) otra.x0 - c.x1 else c.x0 - otra.x1)) > 1f || otra.top > c.top - 2f) continue
            val d = minOf((c.x1 - c.x0) * 0.8f, (c.top - otra.top) * r.cot * 0.4f + 2f); val baja = (d * 1.3f).coerceAtMost(c.base - c.top - 2f)
            with(E) { sombraDe(r, if (der) poligono(Offset(c.x1, c.top), Offset(c.x1 - d, c.top), Offset(c.x1, c.top + baja)) else poligono(Offset(c.x0, c.top), Offset(c.x0 + d, c.top), Offset(c.x0, c.top + baja)), 0.6f) }
        }
        // Los olmos del muelle (el grande del puerto y otros dos hacia el puente), la leña de las barcazas y unos toneles.
        arbolDeRibera(m, l, r, Offset(266.5f, MUELLE), 27f, 0.36f, Azar(Semilla.de("revolucion", "arbol", 4)))
        arbolDeRibera(m, l, r, Offset(248.5f, MUELLE), 23f, 0.35f, Azar(Semilla.de("revolucion", "arbol", 3)))
        arbolDeRibera(m, l, r, Offset(197f, MUELLE), 35f, 0.34f, Azar(Semilla.de("revolucion", "arbol", 2)))
        val lena = t(m, l, C.MADERA, 0.33f); val corte = t(m, l, lerp(C.OCRE, C.CAL, 0.3f), 0.33f)
        drawRect(lena, Offset(186.4f, MUELLE - 4.6f), Size(8.4f, 4.6f)); val cortes = ArrayList<Offset>(); for (i in 0..5) for (j in 0..2) cortes += Offset(187.2f + i * 1.4f, MUELLE - 0.8f - j * 1.5f)
        drawPoints(cortes, PointMode.Points, corte, 1.1f, StrokeCap.Round)
        for (x in listOf(171.4f, 173.8f)) { drawRoundRect(t(m, l, C.MADERA_OSC, 0.33f), Offset(x, MUELLE - 3.6f), Size(2.2f, 3.6f), androidx.compose.ui.geometry.CornerRadius(0.8f)); drawLine(t(m, l, C.HIERRO, 0.33f), Offset(x, MUELLE - 2.4f), Offset(x + 2.2f, MUELLE - 2.4f), 0.3f) }
        // La cuerda de la cola del pan, atada a sus argollas, y los cestos que guardan el sitio.
        val cola = E.cola(m)
        if (cola > 0f) {
            val x1 = 161f + 34f * cola; val cuerdaC = t(m, l, lerp(C.PAJA, C.MADERA, 0.4f), 0.3f)
            var x = 161f; while (x < x1 - 0.5f) { val xf = minOf(x + 8.5f, x1); drawPath(Path().apply { moveTo(x, MUELLE - 3.2f); cubicTo(x + (xf - x) * 0.3f, MUELLE - 2.2f, x + (xf - x) * 0.7f, MUELLE - 2.2f, xf, MUELLE - 3.2f) }, cuerdaC, style = Stroke(0.45f)); drawLine(t(m, l, C.HIERRO, 0.3f), Offset(xf, MUELLE - 3.6f), Offset(xf, MUELLE), 0.4f); x = xf }
            if (E.cestosEnLaCola(m)) { val n = 2 + (cola * 8f).toInt(); for (i in 0 until n) { val cx = 162.5f + (x1 - 163f) * i / n.coerceAtLeast(1); drawOval(t(m, l, C.MIMBRE, 0.3f), Offset(cx, MUELLE - 1.7f), Size(2f, 1.7f)); drawLine(t(m, l, C.MADERA_OSC, 0.3f), Offset(cx + 0.2f, MUELLE - 1.6f), Offset(cx + 1.8f, MUELLE - 1.6f), 0.3f) } }
        }
        // En la gran colada, las cuerdas del muelle «se llenan de blanco».
        if (E.colada(m) && !E.barcoCerrado(m) && l.noche < 0.6f) {
            val pts = cuerda(Offset(206f, MUELLE - 7.4f), Offset(242f, MUELLE - 7f), 1.4f, 12)
            drawPath(curva(pts), t(m, l, C.MADERA_OSC, 0.3f), style = Stroke(0.3f))
            for (i in 1 until pts.size - 1) drawRect(t(m, l, C.CAL, 0.26f), pts[i] + Offset(-1.2f, 0f), Size(2.5f, 4.6f + (i % 3) * 0.8f))
            for (x in listOf(206f, 242f)) drawLine(t(m, l, C.MADERA_OSC, 0.3f), Offset(x, MUELLE), Offset(x, MUELLE - 7.6f), 0.5f)
        }
        // Las banderas de tres colores, según el año.
        val bs = E.banderas(m)
        // (la primera, en lo alto del caserón; las de las casas de delante de Notre-Dame cuelgan de un balcón, para no taparla).
        val sitios = listOf(Offset(223f, 78.6f), Offset(99.4f, 107f), Offset(159.5f, 97.4f), Offset(130.4f, 108f), Offset(250f, 88f))
        for (i in 0 until bs) bandera(m, l, sitios[i], when (i) { 0 -> 18f; 1, 3 -> 11.5f; else -> 13f }, when (i) { 0 -> 14f; 1, 3 -> 9.4f; else -> 10f })
        // Desde la Federación, la colgadura de tres colores en el balcón del caserón, sobre el portal.
        if (bs >= 3) { for ((i, c) in listOf(C.ANIL, C.CAL, C.GRANZA).withIndex()) drawRect(t(m, l, c, 0.3f), Offset(214.6f + i * 5.6f, 108.6f), Size(5.6f, 4.8f)); drawLine(t(m, l, C.PIZARRA_OSC, 0.3f).copy(alpha = 0.5f), Offset(214.6f, 113.4f), Offset(231.4f, 113.4f), 0.4f) }
        // Los faroles del puerto, colgados de su palomilla.
        for (p in listOf(Offset(153.4f, 114.6f), Offset(200.6f, 115.6f), Offset(240f, 115f))) farol(m, l, p, 0.34f, 1f)
        // En la helada, de noche: la hoguera del muelle, con su leña, sus brasas, que alumbran las paredes de detrás.
        if (E.hogueraDelMuelle(m)) {
            val f = HOGUERA; val fuego = Color(0xFFFF9A4A)
            resplandor(f + Offset(0f, -3f), 30f, 0.85f * l.noche, fuego); resplandor(f + Offset(0f, -2f), 11f, 0.9f * l.noche, Color(0xFFFFD27A))
            for ((dx, dy) in listOf(-3.4f to -0.2f, 3.2f to -0.4f, -1f to -1.2f)) drawLine(t(m, l, C.BREA, 0.2f), f + Offset(dx, 0f), f + Offset(-dx * 0.4f, dy - 1.4f), 1f, StrokeCap.Round)
            drawOval(Color(0xFFE2582A), f + Offset(-3f, -1.6f), Size(6f, 1.8f))
            drawPath(Path().apply { moveTo(f.x - 2.4f, f.y - 1f); cubicTo(f.x - 3f, f.y - 4f, f.x - 0.6f, f.y - 5f, f.x - 0.4f, f.y - 8f); cubicTo(f.x + 1f, f.y - 6f, f.x + 3f, f.y - 4.4f, f.x + 2.4f, f.y - 1f); close() }, Brush.verticalGradient(0f to Color(0xFFFFE9A8), 1f to fuego, startY = f.y - 8f, endY = f.y - 1f))
            // Los bancos y el caldero de la sopa de los pobres, a contraluz.
            drawRect(t(m, l, C.BREA, 0.2f), f + Offset(-10f, -2.4f), Size(5.6f, 0.9f)); drawRect(t(m, l, C.BREA, 0.2f), f + Offset(5f, -2.2f), Size(5f, 0.9f)); drawOval(t(m, l, C.BREA, 0.2f), f + Offset(6.4f, -4.6f), Size(2.6f, 2.4f))
        }
        // La nieve posada en el muelle.
        if (E.nevado(m)) drawRect(t(m, l, C.CAL, 0.22f), Offset(148f, MUELLE - 0.9f), Size(124f, 1.3f))
    }

    /** La hoguera del muelle en la helada: delante de la iglesia pequeña, junto a la escalera. */
    internal val HOGUERA = Offset(181.5f, MUELLE - 0.2f)

    private fun DrawScope.bandera(m: Momento, l: Luces, pie: Offset, asta: Float, ancho: Float) = with(E) { bandera(l, pie, asta, ancho, perdido(m, 0.3f), caida = ancho * 0.12f) }

    /** Un farol de aceite con su palomilla: apagado, un vidrio gris; encendido, su llama, su halo y la luz que deja en el muro. */
    private fun DrawScope.farol(m: Momento, l: Luces, p: Offset, lj: Float, talla: Float) {
        drawLine(t(m, l, C.HIERRO, lj), p + Offset(-2.2f * talla, -1.6f * talla), p + Offset(0f, -1.6f * talla), 0.4f * talla); drawLine(t(m, l, C.HIERRO, lj), p + Offset(0f, -1.6f * talla), p + Offset(0f, -0.8f * talla), 0.3f * talla)
        val caja = Rect(p.x - 0.9f * talla, p.y - 0.9f * talla, p.x + 0.9f * talla, p.y + 1.1f * talla)
        if (E.faroles(m)) {
            val k = (0.4f + 0.6f * l.noche) * (if (E.niebla(m)) 1.3f else 1f)
            resplandor(p, 9f * talla, 0.6f * k, C.ACEITE); haloDeBruma(p, 15f * talla, 11f * talla, C.ACEITE, 0.42f * l.noche)      // el halo, y debajo la luz que deja en la piedra
            haloDeBruma(p + Offset(0f, 5f * talla), 6f * talla, 6f * talla, C.ACEITE, 0.4f * l.noche)
            if (E.niebla(m)) haloDeBruma(p, 16f * talla, 11f * talla, C.ACEITE, 0.6f)
            drawRect(lerp(C.ACEITE, Color.White, 0.5f), caja.topLeft, caja.size)
        } else drawRect(t(m, l, lerp(C.PIZARRA, C.CAL, 0.4f), lj), caja.topLeft, caja.size)
        drawRect(t(m, l, C.HIERRO, lj), caja.topLeft, caja.size, style = Stroke(0.3f * talla))
    }

    /** La iglesia pequeña del puerto: la nave con su hastial y su rosetón, y la torre con su chapitel, su campana y su cruz. */
    private fun DrawScope.iglesia(m: Momento, l: Luces, r: Rayo) {
        val lj = 0.35f; val der = r.lat > 0f; val k = r.contraste * r.dia
        val piedra = t(m, l, C.PIEDRA, lj); val luz = t(m, l, lerp(C.PIEDRA, Color(0xFFFFF0D0), 0.3f * k), lj); val sombra = t(m, l, mult(C.PIEDRA, r.color, 0.7f * k), lj); val hueco = t(m, l, lerp(C.PIZARRA_OSC, C.BREA, 0.4f), lj)
        val nave = poligono(Offset(170f, MUELLE), Offset(170f, 106f), Offset(177.5f, 96.4f), Offset(185f, 106f), Offset(185f, MUELLE))
        drawPath(nave, piedra)
        clipPath(nave) { for (i in 0..5) drawLine(sombra.copy(alpha = 0.3f), Offset(170f, 108f + i * 2.8f), Offset(185f, 108f + i * 2.8f), 0.25f); mancha(Offset(174f, 116f), 4f, 6f, sombra, 0.3f) }
        drawLine(t(m, l, C.TEJA, lj), Offset(169.4f, 106.4f), Offset(177.5f, 96f), 1.1f); drawLine(t(m, l, lerp(C.TEJA, Color(0xFFFFF0D0), 0.25f * k), lj), Offset(177.5f, 96f), Offset(185.6f, 106.4f), 1.1f)
        drawCircle(hueco, 2.1f, Offset(177.5f, 105f)); drawCircle(luz, 2.1f, Offset(177.5f, 105f), style = Stroke(0.45f))
        drawPath(Path().apply { moveTo(175f, MUELLE); lineTo(175f, 116f); cubicTo(175f, 112.6f, 180f, 112.6f, 180f, 116f); lineTo(180f, MUELLE); close() }, t(m, l, C.MADERA_OSC, lj))
        // La torre.
        drawRect(piedra, Offset(185f, 82f), Size(8.6f, MUELLE - 82f)); drawRect(luz, Offset(if (der) 190.6f else 185f, 82f), Size(3f, MUELLE - 82f)); drawRect(sombra, Offset(if (der) 185f else 192f, 82f), Size(1.6f, MUELLE - 82f))
        drawPath(Path().apply { moveTo(187.4f, 92f); lineTo(187.4f, 87f); cubicTo(187.4f, 84.4f, 191.2f, 84.4f, 191.2f, 87f); lineTo(191.2f, 92f); close() }, hueco)
        drawRect(sombra, Offset(184.6f, 93.6f), Size(9.4f, 0.8f)); drawRect(hueco.copy(alpha = 0.6f), Offset(188.4f, 100f), Size(1.8f, 5f))
        val chapitel = poligono(Offset(184.2f, 82.2f), Offset(194.4f, 82.2f), Offset(189.3f, 67.6f))
        drawPath(chapitel, Brush.horizontalGradient(if (der) listOf(t(m, l, C.PIZARRA_OSC, lj), t(m, l, C.PIZARRA, lj)) else listOf(t(m, l, C.PIZARRA, lj), t(m, l, C.PIZARRA_OSC, lj)), 184f, 194.4f))
        if (E.nevado(m)) clipPath(chapitel) { drawRect(t(m, l, C.CAL, 0.2f).copy(alpha = 0.8f), Offset(if (der) 189.3f else 184f, 66f), Size(5.4f, 17f)) }
        if (!E.sinCruz(m)) {
            drawPath(Path().apply { moveTo(187.9f, 91.2f); cubicTo(188f, 88.6f, 188.5f, 87f, 189.3f, 86.8f); cubicTo(190.1f, 87f, 190.6f, 88.6f, 190.7f, 91.2f); close() }, t(m, l, C.COBRE, lj))   // la campana
            // La cruz de hierro, sobre su bola: grande, para que se note el día que falte.
            val hierro = t(m, l, C.HIERRO, lj - 0.14f)
            drawCircle(hierro, 1f, Offset(189.3f, 67.4f)); drawLine(hierro, Offset(189.3f, 67.4f), Offset(189.3f, 55.6f), 1f); drawLine(hierro, Offset(185.6f, 59.4f), Offset(193f, 59.4f), 1f)
            drawLine(t(m, l, lerp(C.HIERRO, C.CAL, 0.4f), lj - 0.14f).copy(alpha = 0.7f * r.dia), Offset(189.3f + (if (der) 0.4f else -0.4f), 67f), Offset(189.3f + (if (der) 0.4f else -0.4f), 56f), 0.3f)
        } else bandera(m, l, Offset(189.3f, 68f), 12f, 9.6f)
    }

    /** El caserón: sillares, tres pisos de ventanas altas, el portal, la cornisa y la mansarda de pizarra con sus chimeneas. */
    private fun DrawScope.caseron(m: Momento, l: Luces, r: Rayo) {
        val lj = 0.34f; val f = CASERON; val der = r.lat > 0f; val k = r.contraste * r.dia
        val piedra = t(m, l, C.PIEDRA, lj); val sucio = t(m, l, C.PIEDRA_OSC, lj); val hueco = t(m, l, lerp(C.PIZARRA_OSC, C.BREA, 0.4f), lj)
        val az = Azar(Semilla.de("revolucion", "caseron")); val hoyAz = m.azar("caseron")
        drawRect(Brush.verticalGradient(0f to piedra, 1f to lerp(piedra, sucio, 0.5f), startY = f.top, endY = f.bottom), f.topLeft, f.size)
        clipRect(f.left, f.top, f.right, f.bottom) {
            for (i in 1..11) drawLine(sucio.copy(alpha = 0.3f), Offset(f.left, f.top + i * 3f), Offset(f.right, f.top + i * 3f), 0.25f)
            repeat(5) { mancha(Offset(az.entre(f.left, f.right), az.entre(f.top + 3f, f.bottom - 2f)), az.entre(3f, 8f), az.entre(3f, 7f), sucio, 0.28f) }
            repeat(6) { val x = az.entre(f.left + 1f, f.right - 1f); drawLine(sucio.copy(alpha = 0.5f), Offset(x, f.top + 1f), Offset(x, f.top + az.entre(5f, 13f)), 0.5f) }
        }
        // Las esquinas almohadilladas.
        for (x in listOf(f.left, f.right - 2f)) for (i in 0..10) drawRect(t(m, l, lerp(C.PIEDRA, Color.White, 0.18f), lj), Offset(x + (if (i % 2 == 0) 0f else 0.4f), f.top + 1f + i * 3.1f), Size(if (i % 2 == 0) 2f else 1.6f, 2.2f))
        // Las ventanas: seis ejes; las del piso noble, con su arco y su balcón de hierro.
        val luzV = E.ventanas(m); val cerrados = E.postigosCerrados(m); val postigo = if (cerrados) t(m, l, lerp(C.PIZARRA_OSC, C.BREA, 0.35f), lj) else t(m, l, lerp(C.PIZARRA, C.CAL, 0.3f), lj)
        for (p in 0..2) for (v in 0..5) {
            if (p == 2 && (v == 2 || v == 3)) continue
            val x = f.left + 4f + v * 6.1f; val y = f.top + 3.4f + p * 10.6f; val hh = if (p == 1) 6.6f else 5.6f
            val estado = if (cerrados) 0 else hoyAz.entero(3); val encendida = hoyAz.real() < luzV * 1.2f
            drawRect(sucio.copy(alpha = 0.6f), Offset(x - 0.5f, y - 0.9f), Size(3.8f, 0.9f))
            if (p == 0) drawOval(if (encendida && estado != 0) C.ACEITE else hueco, Offset(x, y - 1.2f), Size(2.8f, 2.6f))
            drawRect(if (encendida && estado != 0) C.ACEITE else hueco, Offset(x, y), Size(2.8f, hh))
            if (estado == 0) { drawRect(postigo, Offset(x - (if (cerrados) 0.4f else 0f), y - (if (cerrados) 0.4f else 0f)), Size(2.8f + (if (cerrados) 0.8f else 0f), hh + (if (cerrados) 0.4f else 0f))); if (cerrados) drawLine(t(m, l, C.BREA, lj), Offset(x + 1.4f, y - 0.4f), Offset(x + 1.4f, y + hh), 0.35f) } else { drawLine(t(m, l, C.CAL, lj), Offset(x + 1.4f, y), Offset(x + 1.4f, y + hh), 0.3f); drawLine(t(m, l, C.CAL, lj), Offset(x, y + hh * 0.45f), Offset(x + 2.8f, y + hh * 0.45f), 0.25f) }
            drawRect(t(m, l, lerp(C.PIEDRA, Color.White, 0.3f), lj), Offset(x - 0.6f, y + hh), Size(4f, 0.6f))
            if (p == 0) for (i in 0..3) drawLine(t(m, l, C.HIERRO, lj), Offset(x - 0.3f + i * 1.1f, y + hh - 2f), Offset(x - 0.3f + i * 1.1f, y + hh), 0.25f)
            if (encendida && estado != 0) resplandor(Offset(x + 1.4f, y + hh / 2f), 5f, 0.42f * l.noche, C.ACEITE)
        }
        // El portal, con sus hojas de madera.
        drawPath(Path().apply { moveTo(219.6f, f.bottom); lineTo(219.6f, f.bottom - 8f); cubicTo(219.6f, f.bottom - 12.4f, 226.4f, f.bottom - 12.4f, 226.4f, f.bottom - 8f); lineTo(226.4f, f.bottom); close() }, t(m, l, C.MADERA_OSC, lj))
        drawLine(hueco, Offset(223f, f.bottom), Offset(223f, f.bottom - 11f), 0.3f)
        // La cornisa, con sus dentículos, y su sombra.
        drawRect(t(m, l, lerp(C.PIEDRA, Color.White, 0.3f), lj), Offset(f.left - 0.8f, f.top - 0.8f), Size(f.width + 1.6f, 1.3f))
        for (i in 0..20) drawRect(sucio, Offset(f.left + i * 2.05f, f.top + 0.5f), Size(1f, 0.7f))
        with(E) { sombraDe(r, Offset(f.left, f.top + 1.2f), Size(f.width, 1.6f), 0.5f) }
        // La mansarda: el faldón empinado de pizarra, sus buhardillas de ojo redondo y las chimeneas altas.
        val faldon = poligono(Offset(f.left - 0.6f, f.top - 0.8f), Offset(f.right + 0.6f, f.top - 0.8f), Offset(f.right - 2.4f, f.top - 9.6f), Offset(f.left + 2.4f, f.top - 9.6f))
        val osc = t(m, l, mult(C.PIZARRA, r.color, 0.5f * k), lj); val claro = t(m, l, lerp(C.PIZARRA, Color(0xFFF2E8D0), 0.3f * k), lj)
        drawPath(faldon, Brush.horizontalGradient(if (der) listOf(osc, t(m, l, C.PIZARRA, lj), claro) else listOf(claro, t(m, l, C.PIZARRA, lj), osc), f.left, f.right))
        clipPath(faldon) { for (i in 1..4) drawLine(osc.copy(alpha = 0.45f), Offset(f.left - 1f, f.top - 0.8f - i * 1.8f), Offset(f.right + 1f, f.top - 0.8f - i * 1.8f), 0.3f) }
        drawRect(t(m, l, C.PIZARRA_OSC, lj), Offset(f.left + 2.4f, f.top - 10.4f), Size(f.width - 4.8f, 0.9f))
        for (v in 0..3) { val x = f.left + 6f + v * 9.4f; drawRect(piedra, Offset(x, f.top - 7.4f), Size(3.6f, 5.6f)); drawOval(piedra, Offset(x, f.top - 9f), Size(3.6f, 3.4f)); drawOval(hueco, Offset(x + 0.8f, f.top - 6.8f), Size(2f, 2.6f)); if (hoyAz.real() < luzV * 0.5f) drawOval(C.ACEITE, Offset(x + 0.8f, f.top - 6.8f), Size(2f, 2.6f)) }
        for (x in listOf(f.left + 3f, f.left + 19f, f.right - 7f)) { drawRect(t(m, l, C.LADRILLO, lj), Offset(x, f.top - 16f), Size(3.4f, 7f)); drawRect(t(m, l, lerp(C.LADRILLO, Color(0xFFFFF0D0), 0.3f * k), lj), Offset(if (der) x + 2.3f else x, f.top - 16f), Size(1.1f, 7f)); drawRect(sucio, Offset(x - 0.4f, f.top - 16.6f), Size(4.2f, 0.8f)); for (dx in listOf(0.3f, 1.3f, 2.3f)) drawRect(t(m, l, C.TEJA, lj), Offset(x + dx, f.top - 18f), Size(0.7f, 1.5f)) }
        if (E.nevado(m)) clipPath(faldon) { drawRect(t(m, l, C.CAL, 0.2f).copy(alpha = 0.9f), Offset(f.left - 1f, f.top - 10f), Size(f.width + 2f, 4.6f)) }
    }

    // ---------------------------------------------------------------- El puente

    private const val PX0 = 258f
    private const val PX1 = 392f
    private const val ABRE = 3.3f

    /** Cuánto se agranda el puente en el punto [u] (0, el estribo de enfrente; 1, donde se sale del cuadro). */
    internal fun esc(u: Float) = 1f + ABRE * u
    internal fun px(u: Float) = PX0 + (PX1 - PX0) * u
    internal fun aguaDelPuente(u: Float) = H + 6f * esc(u)
    private fun pretil(u: Float) = H - 12.8f * esc(u)
    private fun tablero(u: Float) = H - 10.6f * esc(u)

    /** El punto [u] del cuadro que corresponde a la distancia real [tau] a lo largo del puente (0, el estribo de enfrente). */
    internal fun uDe(tau: Float): Float { val z = (1f - tau * (1f - 1f / esc(1f))).coerceAtLeast(0.06f); return (1f / z - 1f) / ABRE }

    /** Los ojos del puente: de cada uno, dónde empieza y dónde acaba ([u]). */
    internal val OJOS: List<Pair<Float, Float>> = (0..5).map { uDe(it * 0.2f + 0.022f) to uDe(it * 0.2f + 0.178f) }

    /** Un punto del puente en el cuadro: [u] a lo largo y [alza] en alturas de puente sobre los ojos de quien mira (negativa, hacia el agua). */
    internal fun pt(u: Float, alza: Float) = Offset(px(u), H - alza * esc(u))

    /** El contorno de un ojo: los dos pies en el agua y el arco de medio punto. */
    private fun ojo(u0: Float, u1: Float): Path = Path().apply {
        val um = (u0 + u1) / 2f
        moveTo(px(u0), aguaDelPuente(u0)); lineTo(px(u0), H - 1f * esc(u0))
        cubicTo(px(u0), H - 6.4f * esc(u0), px(um) - (px(um) - px(u0)) * 0.5f, H - 8.6f * esc(um), px(um), H - 8.6f * esc(um))
        cubicTo(px(um) + (px(u1) - px(um)) * 0.5f, H - 8.6f * esc(um), px(u1), H - 6.4f * esc(u1), px(u1), H - 1f * esc(u1))
        lineTo(px(u1), aguaDelPuente(u1)); close()
    }

    /**
     * El puente de Notre-Dame, de piedra, visto en escorzo desde río arriba: sus ojos, sus tajamares, el pretil con los
     * faroles y la bomba, sobre sus pilotes. Sin casas: las derribaron en 1786.
     */
    fun DrawScope.puente(m: Momento, l: Luces, r: Rayo) {
        val der = r.lat > 0f; val k = r.contraste * r.dia
        val lejos0 = 0.34f
        fun lj(u: Float) = (lejos0 - 0.26f * u).coerceAtLeast(0.04f)
        // La cara del puente, con los ojos abiertos: se recorta todo menos ellos.
        val cara = Path().apply { moveTo(px(0f) - 4f, pretil(0f)); lineTo(px(1.06f), pretil(1.06f)); lineTo(px(1.06f), aguaDelPuente(1.06f)); lineTo(px(0f) - 4f, aguaDelPuente(0f)); close() }
        val ojos = OJOS.map { ojo(it.first, it.second) }
        val huecos = Path().apply { for (o in ojos) addPath(o) }
        val macizo = Path.combine(androidx.compose.ui.graphics.PathOperation.Difference, cara, huecos)
        val cerca = t(m, l, C.PIEDRA, lj(1f)); val lejosC = t(m, l, C.PIEDRA, lj(0f))
        drawPath(macizo, Brush.horizontalGradient(listOf(lejosC, cerca), px(0f), px(1f)))
        clipPath(macizo) {
            val az = Azar(Semilla.de("revolucion", "puente"))
            // Las hiladas de sillares, que se abren al acercarse, y las manchas de humedad que bajan del pretil.
            for (i in -5..9) drawLine(t(m, l, C.PIEDRA_OSC, 0.2f).copy(alpha = 0.3f), Offset(px(0f), H - i * 1.3f * esc(0f)), Offset(px(1f), H - i * 1.3f * esc(1f)), 0.3f)
            repeat(26) { val u = az.real() * az.real() * 0.2f + az.real() * 0.8f; val x = px(u); val y0 = tablero(u); drawLine(t(m, l, C.PIEDRA_OSC, lj(u)).copy(alpha = 0.4f), Offset(x, y0), Offset(x, y0 + az.entre(2f, 7f) * esc(u)), 0.4f + 0.5f * u) }
            repeat(10) { val u = az.real(); mancha(Offset(px(u), H - az.entre(-3f, 8f) * esc(u)), az.entre(2f, 5f) * esc(u), az.entre(1.5f, 4f) * esc(u), t(m, l, C.PIEDRA_OSC, lj(u)), 0.26f) }
            // El verdín de la línea del agua.
            drawPath(Path().apply { moveTo(px(0f) - 4f, aguaDelPuente(0f) - 1f); lineTo(px(1.06f), aguaDelPuente(1.06f) - 1.6f * esc(1f)); lineTo(px(1.06f), aguaDelPuente(1.06f)); lineTo(px(0f) - 4f, aguaDelPuente(0f)); close() }, t(m, l, lerp(C.HOJA, C.BREA, 0.5f), 0.2f).copy(alpha = 0.55f))
        }
        // Por debajo de cada bóveda, la sombra; y el grueso de la bóveda, que se ve por el lado de acá.
        for ((i, o) in ojos.withIndex()) {
            val (u0, u1) = OJOS[i]; val um = (u0 + u1) / 2f
            clipPath(o) {
                drawRect(Brush.verticalGradient(0f to C.SOMBRA.copy(alpha = 0.6f - 0.2f * l.noche), 1f to C.SOMBRA.copy(alpha = 0.12f), startY = H - 8.6f * esc(um), endY = aguaDelPuente(um)), Offset(px(u0), H - 9f * esc(um)), Size(px(u1) - px(u0), 16f * esc(um)), blendMode = androidx.compose.ui.graphics.BlendMode.Multiply)
                val grueso = (px(u1) - px(u0)) * 0.2f
                drawPath(Path().apply { addPath(o, Offset(-(px(u1) - px(u0)) + grueso * 0.25f, 0f)) }, t(m, l, mult(C.PIEDRA, r.color, 0.75f), lj(um)))
                drawPath(Path().apply { addPath(o, Offset((px(u1) - px(u0)) - grueso, 0f)) }, t(m, l, mult(C.PIEDRA, r.color, 0.5f), lj(um)))
            }
            drawPath(o, t(m, l, lerp(C.PIEDRA, Color.White, 0.22f), lj(um)), style = Stroke(0.35f + 0.5f * um))                             // las dovelas
        }
        // Los tajamares: la proa de cada pila, con su lado de luz.
        for (i in 0..4) {
            val ua = OJOS[i].second; val ub = OJOS[i + 1].first; val um = (ua + ub) / 2f; val s = esc(um)
            val base = aguaDelPuente(um); val punta = Offset(px(um) - 2.6f * s, base + 0.9f * s)
            drawPath(poligono(Offset(px(ua), aguaDelPuente(ua)), punta, Offset(px(um) - 1.6f * s, H - 2f * s), Offset(px(ua), H - 2.8f * s)), t(m, l, if (der) mult(C.PIEDRA, r.color, 0.7f * k) else lerp(C.PIEDRA, Color(0xFFFFF0D0), 0.3f * k), lj(um)))
            drawPath(poligono(punta, Offset(px(ub), aguaDelPuente(ub)), Offset(px(ub), H - 2.8f * s), Offset(px(um) - 1.6f * s, H - 2f * s)), t(m, l, if (der) lerp(C.PIEDRA, Color(0xFFFFF0D0), 0.3f * k) else mult(C.PIEDRA, r.color, 0.7f * k), lj(um)))
            drawPath(poligono(Offset(px(ua), H - 2.8f * s), Offset(px(um) - 1.6f * s, H - 2f * s), Offset(px(ub), H - 2.8f * s), Offset(px(um), H - 4.6f * s)), t(m, l, lerp(C.PIEDRA, Color.White, 0.2f), lj(um)))
        }
        // El pretil: la imposta, con su sombra, y los sillares de arriba.
        val franja = Path().apply { moveTo(px(0f) - 4f, pretil(0f)); lineTo(px(1.06f), pretil(1.06f)); lineTo(px(1.06f), tablero(1.06f)); lineTo(px(0f) - 4f, tablero(0f)); close() }
        drawPath(franja, Brush.horizontalGradient(listOf(t(m, l, lerp(C.PIEDRA, Color.White, 0.16f), lj(0f)), t(m, l, lerp(C.PIEDRA, Color.White, 0.16f), lj(1f))), px(0f), px(1f)))
        drawLine(t(m, l, lerp(C.PIEDRA, Color.White, 0.4f), 0.1f), Offset(px(0f) - 4f, pretil(0f)), Offset(px(1.06f), pretil(1.06f)), 0.7f)
        with(E) { sombraDe(r, Path().apply { moveTo(px(0f) - 4f, tablero(0f)); lineTo(px(1.06f), tablero(1.06f)); lineTo(px(1.06f), tablero(1.06f) + 1.1f * esc(1f)); lineTo(px(0f) - 4f, tablero(0f) + 1.1f); close() }, 0.5f) }
        if (E.nevado(m)) drawLine(t(m, l, C.CAL, 0.06f), Offset(px(0f) - 4f, pretil(0f) - 0.2f), Offset(px(1.06f), pretil(1.06f) - 1f), 1.4f)
        // Los faroles del puente, en lo alto del pretil.
        for (tau in listOf(0.1f, 0.3f, 0.5f, 0.7f, 0.9f)) { val u = uDe(tau); val s = esc(u); val pie = Offset(px(u), pretil(u)); drawLine(t(m, l, C.HIERRO, lj(u)), pie, pie + Offset(0f, -2.4f * s), 0.3f + 0.25f * s); farol(m, l, pie + Offset(0f, -3f * s), lj(u), 0.5f + 0.3f * s) }
        if (E.banderas(m) >= 5) { val u = uDe(0.86f); with(E) { bandera(l, pt(u, 12.8f), 20f, 16f, perdido(m, lj(u)), caida = 2f) } }
        bomba(m, l, r)
    }

    /** Dónde está la ventana de la bomba (para su luz y su reflejo). */
    internal val BOMBA = Offset(311f, 111f)

    /**
     * La bomba del puente: una casa de madera y piedra sobre pilotes, arrimada a una pila, con su torrecilla y su rueda.
     * **[Aproximación]**: existió (1676 a mediados del XIX), pero su forma y de qué lado del puente estaba no se
     * comprobaron.
     */
    private fun DrawScope.bomba(m: Momento, l: Luces, r: Rayo) {
        val lj = 0.24f; val der = r.lat > 0f; val k = r.contraste * r.dia
        val agua = 137.6f
        // Los pilotes, con sus riostras, y la rueda entre ellos.
        for (x in listOf(297f, 304f, 311f, 318f, 325f)) drawLine(t(m, l, C.MADERA_OSC, lj), Offset(x, 120f), Offset(x, agua), 1.1f)
        drawLine(t(m, l, C.MADERA_OSC, lj), Offset(297f, 124f), Offset(311f, 134f), 0.6f); drawLine(t(m, l, C.MADERA_OSC, lj), Offset(325f, 124f), Offset(311f, 134f), 0.6f)
        drawCircle(t(m, l, C.MADERA, lj), 5.6f, Offset(311f, 130.4f), style = Stroke(0.8f)); for (i in 0..5) { val ang = i * PI.toFloat() / 3f; drawLine(t(m, l, C.MADERA, lj), Offset(311f, 130.4f), Offset(311f + cos(ang) * 5.6f, 130.4f + sin(ang) * 5.6f), 0.4f) }
        // El cuerpo: abajo, piedra; arriba, tabla; el tejado de pizarra y la torrecilla.
        drawRect(t(m, l, C.PIEDRA, lj), Offset(295f, 113f), Size(32f, 7.6f)); drawRect(t(m, l, C.MADERA, lj), Offset(295f, 101.6f), Size(32f, 11.6f))
        drawRect(t(m, l, lerp(C.MADERA, Color(0xFFFFF0D0), 0.25f * k), lj), Offset(if (der) 319f else 295f, 101.6f), Size(8f, 19f)); drawRect(t(m, l, mult(C.MADERA, r.color, 0.7f * k), lj), Offset(if (der) 295f else 323f, 101.6f), Size(4f, 19f))
        for (i in 0..10) drawLine(t(m, l, C.MADERA_OSC, lj).copy(alpha = 0.5f), Offset(296.4f + i * 3f, 101.6f), Offset(296.4f + i * 3f, 113f), 0.3f)
        for (x in listOf(300f, BOMBA.x - 1.6f, 320f)) { val luz = E.faroles(m) && x > 305f && x < 315f; drawRect(if (luz) C.ACEITE else t(m, l, lerp(C.PIZARRA_OSC, C.BREA, 0.4f), lj), Offset(x, 105.4f), Size(3.2f, 4.4f)); drawLine(t(m, l, C.CAL, lj), Offset(x + 1.6f, 105.4f), Offset(x + 1.6f, 109.8f), 0.25f); if (luz) resplandor(Offset(x + 1.6f, 107.6f), 7f, 0.5f * l.noche, C.ACEITE) }
        val tejado = poligono(Offset(293.6f, 101.8f), Offset(328.4f, 101.8f), Offset(323f, 94.6f), Offset(299f, 94.6f))
        drawPath(tejado, Brush.horizontalGradient(if (der) listOf(t(m, l, C.PIZARRA_OSC, lj), t(m, l, C.PIZARRA, lj)) else listOf(t(m, l, C.PIZARRA, lj), t(m, l, C.PIZARRA_OSC, lj)), 293f, 329f))
        drawRect(t(m, l, C.PIEDRA, lj), Offset(303f, 86.4f), Size(7.6f, 9f)); drawRect(t(m, l, lerp(C.PIZARRA_OSC, C.BREA, 0.4f), lj), Offset(305.4f, 88.6f), Size(2.6f, 3.6f))
        drawPath(poligono(Offset(302.2f, 86.6f), Offset(311.4f, 86.6f), Offset(306.8f, 79.4f)), t(m, l, C.PIZARRA, lj))
        if (E.nevado(m)) { clipPath(tejado) { drawRect(t(m, l, C.CAL, 0.12f).copy(alpha = 0.9f), Offset(293f, 94.6f), Size(36f, 4f)) } }
        if (E.banderas(m) >= 3) bandera(m, l, Offset(306.8f, 79.6f), 13f, 10.4f)
    }

    /**
     * Lo que arde lejos, por detrás de las casas: la casa de Réveillon (tres días), la pólvora de la Bastilla, a la
     * izquierda, y la de las Tullerías, río abajo; y el humo de la hoguera de San Juan, que queda a este lado, fuera del
     * cuadro. Va en la parte quieta para que quede por detrás de la ropa, del palo y de la escalera.
     */
    fun DrawScope.humosLejanos(m: Momento, l: Luces) {
        val viento = m.viento * 0.8f + 0.25f
        if (E.incendio(m)) { resplandor(Offset(92f, 96f), 30f, 0.6f * l.noche + 0.15f, Color(0xFFFF7A3A)); for (i in 0..2) humo(l, Offset(84f + i * 8f, 96f), 78f, 34f, 7f + i, viento, 0.9f, 0.4f, 20 + i, 14, 1f) }
        when (E.humoDeGuerra(m)) {
            -1 -> for (i in 0..3) humo(l, Offset(150f + i * 12f, 100f), 66f, 32f, 5f + i, viento, 0.7f, 0.4f, 30 + i, 14, 1f)
            1 -> for (i in 0..3) humo(l, Offset(300f + i * 14f, 108f), 60f, 28f, 5f + i, -viento, 0.6f, 0.5f, 40 + i, 13, 1f)
            else -> Unit
        }
        if (E.hogueraDeSanJuan(m)) humo(l, Offset(374f, 100f), 80f, 30f, 9f, -0.35f, 0.55f, 0.2f, 50, 12, 0.9f)
    }

    // ---------------------------------------------------------------- La parte viva

    /** El humo de las chimeneas; los humos del día (Réveillon, la Bastilla, las Tullerías, San Juan) y las llamas de los faroles. */
    fun DrawScope.ciudadViva(m: Momento, l: Luces, reloj: Float) {
        val viento = (m.viento * 0.8f + 0.12f)
        // Las chimeneas: más de mañana y en invierno; casi ninguna con el pan caro y la leña cara de la helada.
        val cuantas = when { E.helada(m) -> 2; E.invierno(m) -> 6; m.hora < 9.0 || m.hora >= 17.0 -> 5; else -> 3 }
        val bocas = listOf(Offset(71f, 82f), Offset(104f, 79f), Offset(137f, 80f), Offset(208f, 70f), Offset(226f, 70f), Offset(247f, 85f))
        for (i in 0 until cuantas) humo(l, bocas[(i * 5) % bocas.size], 16f, 6f, reloj, viento, 0.25f, E.perdido(m, 0.36f), i, 6, 0.55f)
        if (E.lumbreDeLaPanaderia(m)) humo(l, Offset(164f, 95f), 20f, 7f, reloj, viento, 0.35f, E.perdido(m, 0.34f), 9, 7, 0.75f)
        if (E.hogueraDeSanJuan(m)) {
            resplandor(Offset(388f, 150f), 90f, 0.5f, Color(0xFFFF8A3A))
            val az = Azar(Semilla.de("revolucion", "pavesas")); val pts = ArrayList<Offset>()
            repeat(22) { val sube = (reloj * az.entre(8f, 16f) + az.entre(0f, 80f)) % 80f; pts += Offset(380f - az.entre(0f, 16f) - sube * 0.1f + 2f * sin(reloj * 2f + it), 110f - sube) }
            drawPoints(pts, PointMode.Points, Color(0xFFFFC56A).copy(alpha = 0.8f), 1f, StrokeCap.Round)
        }
        if (E.hogueraDelMuelle(m)) {
            for (i in 0..2) llama(HOGUERA + Offset(-2f + i * 2f, -0.6f), 5.5f + (i % 2) * 2.4f, reloj, 60 + i)
            humo(l, HOGUERA + Offset(0f, -7f), 34f, 10f, reloj, viento, 0.4f, 0.2f, 61, 8, 0.6f)
            resplandor(HOGUERA + Offset(0f, -3f), 22f + 3f * sin(reloj * 5.3f), 0.25f * l.noche, Color(0xFFFF9A4A))
            val az = Azar(Semilla.de("revolucion", "chispas")); val pts = ArrayList<Offset>()
            repeat(9) { val sube = (reloj * az.entre(5f, 10f) + az.entre(0f, 26f)) % 26f; pts += HOGUERA + Offset(az.entre(-3f, 3f) + 1.4f * sin(reloj * 2f + it), -6f - sube) }
            drawPoints(pts, PointMode.Points, Color(0xFFFFC56A).copy(alpha = 0.8f), 0.7f, StrokeCap.Round)
        }
        // Las llamas de los faroles tiemblan.
        if (E.faroles(m)) for ((i, p) in listOf(Offset(153.4f, 114.6f), Offset(200.6f, 115.6f), Offset(240f, 115f)).withIndex()) drawCircle(Color(0xFFFFF2C0).copy(alpha = 0.55f + 0.3f * sin(reloj * 7f + i * 2.1f)), 0.6f, p)
    }
}
