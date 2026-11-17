package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Renacimiento
// =====================================================================

/**
 * Renacimiento: el mueble de los dibujos en el taller del maestro (la bottega), Florencia, hacia 1490. Es un interior sin
 * ventana: una pared de enlucido de cal tibia, con las pasadas de la llana, una mancha de humedad que baja de la esquina, un
 * desconchón y un zócalo pintado de almagre; la luz de día entra alta, de arriba a la izquierda, fuera del cuadro. El mueble
 * es un armario de nogal con taracea de varias maderas (peral, arce, boj y roble ahumado, cada pieza con su veta): la cornisa
 * con sus dentículos, que hacen sombra, el friso de roleo, las pilastras con sus candelabros, el filete de arce de cada hueco
 * y, abajo, el zócalo de tres paneles (el mazzocchio en su tondo, el trampantojo de cubos, donde se pega el cartellino de
 * «y N más», y una estrella en el suyo) sobre patas de ménsula. Encima, el punto focal: un dodecaedro de madera, grande, a
 * la izquierda; a su lado el candelero de latón con su vela a medio gastar, el mortero de pórfido con su mano y el azul a
 * medio moler, las conchas con los pigmentos (ultramar, ocre, bermellón, tierra verde), el albarelo de mayólica con los
 * pinceles, un cuenco con dos huevos para el temple y, a la derecha, los cartones enrollados y atados con cinta, que se salen
 * por el borde, con un compás de puntas apoyado. En la pared, un delantal de lino manchado de pigmento colgado de un clavo,
 * la cuadrícula de traslado trazada a carboncillo con un trazado de sinopia, una hoja de estudio a la sanguina (una mano)
 * sujeta con dos clavos, y la escuadra y la plomada colgadas. Al pie, sobre las baldosas de barro, una tabla preparada con
 * yeso, apoyada y cortada por el borde izquierdo, un cubo de duelas y la garrafa de aceite de linaza forrada de paja, cortada
 * por el derecho. Cada tema es, según la semilla, un cajoncito cerrado de taracea (el panel de peral, arce o boj con su
 * estrella, sus cubos o su damero y el botón de bronce; asoman las puntas de las hojas), un cajoncito entreabierto de nogal
 * o de cerezo con las hojas de boceto de pie (más con más fichas, alguna con sanguina), o un nicho abierto con rollos de
 * papel atados con cintas de color y a veces un cuaderno de vitela; en los tres, el tema va en un cartellino de papel. Lo que
 * no tiene tema es una carpeta de cartón atada con cintas, atravesada en su nicho; un hueco libre es un cajoncito liso con el
 * cartellino en blanco. Al abrir uno, sale hacia quien mira. De noche todo se hunde en pardo azulado: alumbra solo la vela
 * (el halo cálido en la cal, que deja ver la hoja de estudio y la cuadrícula; el filo en el latón, en el bronce y en las
 * caras del dodecaedro que la miran), la taracea clara encendida en las dos primeras filas y casi perdida en las últimas.
 * Por dentro, tablas de peral con un compás y una regla. Las fichas son hojas de boceto con un estudio a la sanguina.
 */
object IdeasRenacimiento : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del taller
    /** El nogal del mueble, su luz y su sombra. */
    private val NOGAL = Color(0xFF5E3E22)
    private val NOGAL_LUZ = Color(0xFF7E5834)
    private val NOGAL_OSC = Color(0xFF3A2614)
    /** Las maderas de la taracea: peral, arce, boj, roble ahumado, cerezo y ébano. */
    private val PERAL = Color(0xFFD9B98A)
    private val ARCE = Color(0xFFEAD7AE)
    private val BOJ = Color(0xFFD6B25E)
    private val ROBLE = Color(0xFF4F4135)
    private val CEREZO = Color(0xFF8E4E2E)
    private val EBANO = Color(0xFF2A1E14)
    /** El bronce de los botones y el latón del candelero. */
    private val BRONCE = Color(0xFFA88438)
    private val LATON = Color(0xFFC9A450)
    /** La sanguina, el papel, el papel teñido de los estudios, el cartellino, el lino y la tinta sepia. */
    private val SANGUINA = Color(0xFFA8472A)
    private val PAPEL = Color(0xFFE9D8B2)
    private val PAPEL_TINTO = Color(0xFFD3B88E)
    private val CARTELLINO = Color(0xFFF4EAD0)
    private val LINO = Color(0xFFD5C8AA)
    private val TINTA = Color(0xFF3A2614)
    /** La cal de la pared y su sombra; el barro de las baldosas. */
    private val CAL = Color(0xFFE6DAC2)
    private val CAL_OSC = Color(0xFFBFAF92)
    private val BARRO = Color(0xFFB4694A)
    /** Los pigmentos: el color de la escena. Vuelven en las cintas, en el delantal y en la mayólica. */
    private val ULTRAMAR = Color(0xFF34508A)
    private val OCRE = Color(0xFFC9973F)
    private val BERMELLON = Color(0xFFC2452D)
    private val TIERRA_VERDE = Color(0xFF6F8560)
    /** El pórfido del mortero, el esmalte de la mayólica y la cera de la vela. */
    private val PORFIDO = Color(0xFF6B2F3A)
    private val MAYOLICA = Color(0xFFEDE8DA)
    private val CERA = Color(0xFFEFE3C2)
    /** La llama de la vela y la noche del taller: pardo azulado hondo. */
    private val LLAMA = Color(0xFFFFC56E)
    private val NOCHE = Color(0xFF14121B)
    /**
     * El cartellino de noche lejos de la vela: con [TINTA_ROTULO] da unos 6:1 en el título y más de 4.5:1 en la cuenta (al
     * 78 %); cerca de la vela sube hacia [CARTELLINO] con un poco de llama.
     */
    private val PAPEL_LEJOS = Color(0xFFA39A80)
    private val TINTA_ROTULO = Color(0xFF2A1A0E)
    /** El azul de las marcas de las fichas (sin cambios). */
    private val AZUL = Color(0xFF2F4F6F)

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.5f
    override val corona = 0.66f
    override val base = 0.34f
    override val lado = 0.085f
    override val hueco = 0.035f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** En la corona: el fondo de la tapa, su canto de frente, lo bajo de la cornisa, de los dentículos y del friso, en fracción de su alto. */
    private const val SUP = 0.775f
    private const val FRENTE = 0.84f
    private const val CORNISA = 0.878f
    private const val DENT = 0.912f
    private const val FRISO = 0.975f
    /** Los costados del armario, en fracción del ancho. */
    private const val IZQ = 0.022f
    private const val DER = 0.978f
    /** En la base: los paneles del zócalo, la moldura, donde pisan las patas, el rincón de la pared con el suelo y las cosas del pie. */
    private const val PANEL0 = 0.065f
    private const val PANEL1 = 0.455f
    private const val ZOC = 0.54f
    private const val PISAN = 0.63f
    private const val SUELO = 0.47f
    private const val PIE = 0.965f
    /** Cuánto baja el frente de un cajón (y lo de un nicho) al abrirlo, en fracción de su alto. */
    private const val SALIDA = 0.12f

    // ---------------------------------------------------------------- la luz y la noche

    /** La llama de la vela, en fracciones del ancho. */
    private val LUZ = Offset(0.47f, 0.272f)

    /** Cuánto le llega de la vela a un punto [p] (en fracciones del ancho): cae con la distancia. */
    private fun luz(p: Offset): Float {
        val dx = p.x - LUZ.x
        val dy = (p.y - LUZ.y) * (if (p.y > LUZ.y) 0.8f else 1f)
        val radial = 1.4f * kotlin.math.exp(-3.3f * sqrt(dx * dx + dy * dy))
        return maxOf(radial, luzCuerpo(p)).coerceIn(0.03f, 1f)
    }

    /**
     * La luz que la vela echa hacia abajo sobre el frente del armario: cae de fila en fila (la primera, encendida; la segunda,
     * a medias; la tercera, tenue; las últimas, casi perdidas). Los puntos son los centros de las filas, en fracciones del ancho.
     */
    private val CAIDA = listOf(0.6f to 0.9f, 0.83f to 0.82f, 1.13f to 0.54f, 1.43f to 0.27f, 1.73f to 0.1f, 2.03f to 0.05f, 2.6f to 0.03f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.55f * (abs(p.x - LUZ.x) - 0.08f).coerceAtLeast(0f)).coerceIn(0.7f, 1f)
        return k * lado
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.13f + NOCHE.red * 0.8f, c.green * 0.13f + NOCHE.green * 0.8f, c.blue * 0.13f + NOCHE.blue * 0.8f, c.alpha)

    /**
     * Cómo se pinta algo según dónde está. De día, tal cual (con [dia], un poco más claro). De noche, de su color hondo hacia el
     * suyo según [k] (lo que le llega de la vela), teñido de llama.
     */
    private class Tono(val o: Boolean, val k: Float, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.9f))
        return mezcla(v, LLAMA, 0.16f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, la noche del taller. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la llama. */
    private fun Tono.b(a: Float): Color = if (!o) luzMueble(a) else LLAMA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f))

    /** Lo que el mueble que se está pintando sabe: si es de noche y su ancho (para pasar píxeles a fracciones). */
    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset): Tono = Tono(esNoche, if (esNoche) luz(p / anchoW) else 0f)

    /** De noche la luz viene de la vela: lo que está a su izquierda recibe la luz por la derecha. */
    private fun luzDerecha(x: Float) = esNoche && x / anchoW < LUZ.x - 0.02f

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /**
     * La sombra de algo en la pared: de día corrida a la derecha y abajo; de noche, hacia fuera de la vela, tanto más marcada
     * cuanto más cerca está de ella. No baja de [hasta] (la tapa o el suelo que la corta).
     */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, a: Float = 0.18f, lejos: Float = 1f) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.022f * lejos, w * 0.012f * lejos) { drawPath(p, sombraMueble(a)) }
            else {
                val k = luz(desde / w)
                if (k > 0.1f) {
                    val dx = (desde.x / w - LUZ.x).coerceIn(-0.3f, 0.3f) / 0.3f
                    translate(w * 0.045f * dx * lejos, w * 0.008f * lejos) { drawPath(p, NOCHE.copy(alpha = (0.65f * k).coerceAtMost(0.5f))) }
                }
            }
        }
    }

    /** La sombra de contacto de lo que pisa la tapa o el suelo, corrida a la derecha (de noche, hacia fuera de la vela). */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float) {
        val corre = if (luzDerecha(centro.x)) 0.58f else 0.42f
        drawOval(if (esNoche) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a), Offset(centro.x - ancho * corre, centro.y - alto / 2), Size(ancho, alto))
    }

    /** La luz de un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
    private fun DrawScope.cilindro(r: Rect, t: Tono, fuerza: Float = 1f, der: Boolean = false) {
        fun x(f: Float, a: Float) = if (der) r.right - r.width * (f + a) else r.left + r.width * f
        drawRect(t.b(0.2f * fuerza), Offset(x(0f, 0.3f), r.top), Size(r.width * 0.3f, r.height))
        drawRect(t.b(0.34f * fuerza), Offset(x(0.15f, 0.08f), r.top), Size(r.width * 0.08f, r.height))
        drawRect(t.s(0.16f * fuerza), Offset(x(0.58f, 0.42f), r.top), Size(r.width * 0.42f, r.height))
        drawRect(t.s(0.2f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
    }

    /** Un cuerpo tumbado dentro de [r]: la luz arriba, la sombra abajo (ir dentro de su recorte). */
    private fun DrawScope.tumbado(r: Rect, t: Tono, fuerza: Float = 1f) {
        drawRect(t.b(0.18f * fuerza), r.topLeft, Size(r.width, r.height * 0.32f))
        drawRect(t.b(0.3f * fuerza), Offset(r.left, r.top + r.height * 0.14f), Size(r.width, r.height * 0.08f))
        drawRect(t.s(0.16f * fuerza), Offset(r.left, r.top + r.height * 0.6f), Size(r.width, r.height * 0.4f))
        drawRect(t.s(0.22f * fuerza), Offset(r.left, r.top + r.height * 0.82f), Size(r.width, r.height * 0.18f))
    }

    /**
     * El filo cálido de la vela en algo, de noche: en su canto de arriba y en el costado que mira a la luz ([der]: el derecho),
     * tanto más vivo cuanto más le llega. De día no pinta nada.
     */
    private fun DrawScope.filo(r: Rect, t: Tono, der: Boolean) {
        if (!t.o || t.k < 0.06f) return
        val u = density
        drawRect(LLAMA.copy(alpha = (0.8f * t.k).coerceAtMost(0.7f)), r.topLeft, Size(r.width, 1.4f * u))
        drawRect(LLAMA.copy(alpha = (0.26f * t.k).coerceAtMost(0.26f)), Offset(r.left, r.top + 1.4f * u), Size(r.width, 2.6f * u))
        val x = if (der) r.right - 1.3f * u else r.left
        drawRect(LLAMA.copy(alpha = (0.55f * t.k).coerceAtMost(0.5f)), Offset(x, r.top), Size(1.3f * u, r.height))
    }

    /** El destello de la vela en el latón, el bronce o el esmalte, de noche (de día, la luz crema). */
    private fun DrawScope.destello(c: Offset, r: Float, t: Tono) {
        if (t.o) { if (t.k > 0.06f) { drawCircle(LLAMA.copy(alpha = (0.35f * t.k).coerceAtMost(0.3f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(luzMueble(0.85f), r, c)
    }

    /** Un clavo de hierro en la pared, con su sombra. */
    private fun DrawScope.clavo(p: Offset, r: Float, t: Tono) {
        drawCircle(t.s(0.4f), r * 1.05f, p + Offset(r * 0.7f, r * 0.7f))
        drawCircle(t.c(mezcla(EBANO, ROBLE, 0.5f)), r, p)
        drawCircle(t.b(0.5f), r * 0.4f, p - Offset(r * 0.3f, r * 0.3f))
    }

    /** Una tabla de [col] en [r]: el color y unas pocas vetas a lo largo ([vertical] si corre de arriba abajo). */
    private fun DrawScope.tabla(r: Rect, col: Color, t: Tono, semilla: Int, vertical: Boolean = false, densidad: Float = 0.45f) {
        drawRect(t.c(col), r.topLeft, r.size)
        vetas(r, (if (t.o) NOCHE else mezcla(col, EBANO, 0.6f)).copy(alpha = 0.3f), semilla, densidad, vertical = vertical, grosor = 0.55f, ondulacion = 0.5f)
    }

    /**
     * Una pieza de taracea: la madera [col] en la forma [p], con dos vetas cortas en la dirección [ang] (en grados) alrededor de
     * su centro [c]. Así el dibujo se lee por tono y por veta, sin recortes.
     */
    private fun DrawScope.pieza(p: Path, col: Color, ang: Float, t: Tono, c: Offset, r: Float) {
        drawPath(p, t.c(col))
        val a = Math.toRadians(ang.toDouble())
        val d = Offset(cos(a).toFloat(), sin(a).toFloat())
        val n = Offset(-d.y, d.x)
        val veta = (if (t.o) NOCHE else mezcla(col, EBANO, 0.7f)).copy(alpha = 0.28f)
        drawLine(veta, c + n * (r * 0.28f) - d * (r * 0.6f), c + n * (r * 0.28f) + d * (r * 0.6f), 0.5f * density)
        drawLine(veta, c - n * (r * 0.3f) - d * (r * 0.5f), c - n * (r * 0.3f) + d * (r * 0.5f), 0.5f * density)
    }

    /** Un rombo con centro [c], medio ancho [a] y medio alto [b]. */
    private fun rombo(c: Offset, a: Float, b: Float) = poligono(c.x, c.y - b, c.x + a, c.y, c.x, c.y + b, c.x - a, c.y)

    /** Un punto de la curva de tres puntos [p0], [p1], [p2] en [f], y su dirección. */
    private fun curva(p0: Offset, p1: Offset, p2: Offset, f: Float): Pair<Offset, Offset> {
        val g = 1f - f
        val p = p0 * (g * g) + p1 * (2f * g * f) + p2 * (f * f)
        val d = (p1 - p0) * (2f * g) + (p2 - p1) * (2f * f)
        val l = d.getDistance().coerceAtLeast(0.001f)
        return p to d / l
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val o = oscuro
        esNoche = o
        val w = size.width; val alto = size.height
        anchoW = w
        val ch = d.corona.height
        val b = d.base; val hb = b.height
        val sup = ch * SUP; val frente = ch * FRENTE
        val pieA = sup + (frente - sup) * 0.32f
        val pieF = sup + (frente - sup) * 0.88f
        val suelo = b.top + hb * SUELO

        // ---- la pared de cal y el suelo de baldosas de barro
        pared(w, ch, suelo)
        piso(w, suelo, alto)

        // ---- en la pared: la cuadrícula de traslado, la hoja de estudio, la escuadra con la plomada y el delantal
        cuadricula(Rect(w * 0.165f, w * 0.03f, w * 0.445f, w * 0.27f))
        hojaDeEstudio(Rect(w * 0.565f, w * 0.03f, w * 0.735f, w * 0.25f), sup)
        escuadraYPlomada(w, sup)
        delantal(Offset(w * 0.068f, w * 0.045f), sup)

        // ---- el armario y su tapa
        armario(d, ch, b)
        tapa(w, ch)

        // ---- lo de encima, de atrás adelante
        dodecaedro(Offset(w * 0.245f, pieA + w * 0.004f), w * 0.118f, sup)
        candelero(Offset(w * LUZ.x, pieA), sup)
        albarelo(Offset(w * 0.655f, pieA), sup)
        rollos(Offset(w * 0.78f, pieA + w * 0.006f), sup)
        mortero(Offset(w * 0.385f, pieF - w * 0.004f), sup)
        conchas(pieF, w)
        cuenco(Offset(w * 0.735f, pieF), sup)
        compas(Offset(w * 0.845f, pieF + w * 0.002f))

        // ---- al pie: la tabla enyesada, el cubo y la garrafa forrada de paja
        tablaDeYeso(b, w)
        garrafa(Offset(w * 0.925f, b.top + hb * PIE), w)
        cubo(Offset(w * 0.765f, b.top + hb * (PIE + 0.01f)), w)

        // ---- la placa de «y N más»: un cartellino pegado sobre el panel de cubos
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            drawRect(t.s(0.3f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size)
            drawRect(if (o) PAPEL_LEJOS else CARTELLINO, r.topLeft, r.size)
            drawRect(t.s(0.1f), Offset(r.right - r.width * 0.06f, r.top), Size(r.width * 0.06f, r.height))
            drawPath(poligono(r.right - 7f * u, r.bottom, r.right, r.bottom - 7f * u, r.right, r.bottom), t.c(NOGAL_OSC))
            drawPath(poligono(r.right - 7f * u, r.bottom, r.right, r.bottom - 7f * u, r.right - 6f * u, r.bottom - 6f * u), if (o) mezcla(PAPEL_LEJOS, NOCHE, 0.3f) else mezcla(CARTELLINO, PERAL, 0.5f))
            listOf(r.left + 4f * u, r.right - 4f * u).forEach { x -> drawCircle(t.c(SANGUINA), 1.7f * u, Offset(x, r.top + 3.5f * u)) }
        }

        // ---- la luz de la vela, por encima de lo que tiene cerca
        if (o) {
            val llama = LUZ * w
            mancha(llama + Offset(0f, w * 0.04f), w * 0.46f, LLAMA.copy(alpha = 0.1f))
            mancha(llama + Offset(0f, w * 0.02f), w * 0.19f, LLAMA.copy(alpha = 0.2f))
            mancha(llama, w * 0.06f, Color(0xFFFFE9B8).copy(alpha = 0.3f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    // ---------------------------------------------------------------- la pared y el suelo

    /**
     * La pared de enlucido de cal: la mancha de luz arriba a la izquierda (de noche, el halo de la vela), las pasadas de la
     * llana, la mancha de humedad que baja de la esquina de arriba a la derecha, un desconchón, grietas y clavos viejos; abajo,
     * el zócalo pintado de almagre con la mugre del rincón.
     */
    private fun DrawScope.pared(w: Float, ch: Float, suelo: Float) {
        val u = density
        val o = esNoche
        val dado = suelo - w * 0.12f
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(CAL, LUZ_MUEBLE, 0.4f), CAL, mezcla(CAL, CAL_OSC, 0.7f)), 0f, w), Offset.Zero, Size(w, suelo))
            mancha(Offset(w * 0.1f, 0f), w * 0.8f, luzMueble(0.45f))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombraMueble(0.12f)), ch * 0.4f, ch), Offset(0f, ch * 0.4f), Size(w, suelo - ch * 0.4f))
        } else {
            fun k(d: Float) = Tono(true, luz(LUZ + Offset(d, 0f))).c(CAL)
            drawRect(Brush.radialGradient(0f to k(0.04f), 0.08f to k(0.1f), 0.2f to k(0.24f), 0.38f to k(0.46f), 0.62f to k(0.75f), 1f to k(1.2f),
                center = LUZ * w, radius = w * 1.2f), Offset.Zero, Size(w, suelo))
        }
        // Las pasadas de la llana: arcos largos, unos con luz y otros con sombra.
        for (j in 0 until 34) {
            val c = Offset(w * azar(6001, j), ch * 0.82f * azar(6002, j))
            val r = w * (0.06f + 0.13f * azar(6003, j))
            val tt = tn(c)
            drawArc(if (j % 3 == 0) tt.b(0.3f) else tt.s(0.055f), 180f + 150f * azar(6004, j), 40f + 70f * azar(6005, j), false,
                c - Offset(r, r * 0.6f), Size(2f * r, 1.2f * r), style = Stroke((1.2f + 3f * azar(6006, j)) * u, cap = StrokeCap.Round))
        }
        pinceladas(Rect(0f, 0f, w, suelo), if (o) NOCHE else CAL_OSC, 6007, 36, u, 0.09f)
        pinceladas(Rect(0f, 0f, w, suelo), if (o) NOCHE else mezcla(CAL, LUZ_MUEBLE, 0.6f), 6008, 16, u, 0.12f)
        // La humedad: manchas pardas con verdín, su cerco y las chorreras.
        val th = tn(Offset(w * 0.93f, w * 0.07f))
        val hum = th.c(mezcla(CAL_OSC, TIERRA_VERDE, 0.4f))
        listOf(Triple(0.91f, 0.02f, 0.13f), Triple(0.985f, 0.09f, 0.09f), Triple(0.84f, -0.01f, 0.07f), Triple(0.955f, 0.17f, 0.05f)).forEachIndexed { i, (fx, fy, r) ->
            drawOval(hum.copy(alpha = 0.16f), Offset(w * (fx - r), w * (fy - r * 0.75f)), Size(w * 2f * r, w * 1.5f * r))
            if (i < 2) drawOval(th.c(mezcla(CAL_OSC, NOGAL, 0.35f)).copy(alpha = 0.3f), Offset(w * (fx - r), w * (fy - r * 0.75f)), Size(w * 2f * r, w * 1.5f * r), style = Stroke(1.3f * u))
        }
        listOf(0.875f, 0.93f, 0.965f).forEachIndexed { i, fx ->
            drawLine(hum.copy(alpha = 0.3f), Offset(w * fx, w * 0.1f), Offset(w * (fx + 0.004f), w * (0.2f + 0.05f * i)), (1.5f + i) * u, StrokeCap.Round)
        }
        // El desconchón: el enlucido saltado deja ver el revoco basto, con el canto de la cal en sombra arriba.
        val dr = Rect(w * 0.765f, w * 0.045f, w * 0.83f, w * 0.085f)
        val td = tn(dr.center)
        val roto = contornoIrregular(dr, 6030, 16, 0.22f)
        translate(-1.2f * u, -1.5f * u) { drawPath(roto, td.s(0.3f)) }
        translate(1f * u, 1.2f * u) { drawPath(roto, td.b(0.5f)) }
        drawPath(roto, td.c(mezcla(CAL_OSC, BARRO, 0.3f)))
        clipPath(roto) { pinceladas(dr, if (o) NOCHE else NOGAL, 6031, 10, u * 0.5f, 0.25f) }
        // Grietas finas y agujeros de clavos viejos.
        listOf(Offset(0.5f, 0.02f), Offset(0.02f, 0.3f), Offset(0.74f, 0.3f)).forEachIndexed { i, f ->
            val p0 = Offset(w * f.x, w * f.y)
            val t = tn(p0)
            val g = Path().apply {
                moveTo(p0.x, p0.y)
                var q = p0
                for (j in 0 until 5) { q += Offset(w * (0.008f + 0.014f * azar(6040 + i, j)) * (if (j % 2 == 0) 1f else -0.5f), w * (0.012f + 0.012f * azar(6050 + i, j))); lineTo(q.x, q.y) }
            }
            drawPath(g, t.s(0.24f), style = Stroke(0.7f * u))
            translate(0.8f * u, 0.8f * u) { drawPath(g, t.b(0.35f), style = Stroke(0.5f * u)) }
        }
        listOf(Offset(0.52f, 0.3f), Offset(0.3f, 0.015f), Offset(0.96f, 0.4f), Offset(0.135f, 0.18f)).forEach { f ->
            val p = Offset(w * f.x, w * f.y)
            val t = tn(p)
            drawCircle(t.s(0.4f), 1.1f * u, p)
            drawCircle(t.b(0.4f), 0.7f * u, p + Offset(0.7f * u, 0.7f * u))
        }
        // El zócalo pintado de almagre, gastado, con su raya y la mugre del rincón.
        val tz = tn(Offset(w * 0.5f, dado))
        drawRect(tz.c(mezcla(SANGUINA, CAL_OSC, 0.4f)), Offset(0f, dado), Size(w, suelo - dado))
        pinceladas(Rect(0f, dado, w, suelo), if (o) NOCHE else mezcla(CAL, SANGUINA, 0.3f), 6060, 22, u, 0.2f)
        drawRect(tz.c(NOGAL_OSC).copy(alpha = 0.5f), Offset(0f, dado), Size(w, 1.3f * u))
        drawRect(tz.s(0.22f), Offset(0f, suelo - 6f * u), Size(w, 6f * u))
    }

    /** El suelo de baldosas de barro, a matajunta, que se abren hacia quien mira: cada una de su tono, con sus juntas y su desgaste. */
    private fun DrawScope.piso(w: Float, suelo: Float, alto: Float) {
        val u = density
        val filas = listOf(0f, 0.22f, 0.55f, 1.05f)
        fun x(i: Float, f: Float): Float { val x0 = w * i / 7f; return x0 + (x0 - w / 2f) * 0.5f * f }
        for (r in 0 until 3) for (i in -2..8) {
            val f0 = filas[r]; val f1 = filas[r + 1]
            val y0 = suelo + (alto - suelo) * f0; val y1 = suelo + (alto - suelo) * f1
            val des = if (r % 2 == 0) 0f else 0.5f
            val p = poligono(x(i + des, f0), y0, x(i + 1f + des, f0), y0, x(i + 1f + des, f1), y1, x(i + des, f1), y1)
            val tt = tn(Offset(x(i + 0.5f + des, f0), y0))
            drawPath(p, tt.c(mezcla(mezcla(BARRO, NOGAL, 0.3f * azar(6100 + r, i + 5)), LUZ_MUEBLE, 0.12f * azar(6105 + r, i + 5))))
            drawPath(p, tt.s(0.4f), style = Stroke(1.2f * u))
            drawLine(tt.b(0.22f), Offset(x(i + des, f0) + 1.2f * u, y0 + 1.2f * u), Offset(x(i + 1f + des, f0) - 1.2f * u, y0 + 1.2f * u), 0.7f * u)
        }
        pinceladas(Rect(0f, suelo, w, alto), if (esNoche) NOCHE else NOGAL_OSC, 6110, 18, u, 0.12f)
        pinceladas(Rect(0f, suelo, w, alto), if (esNoche) NOCHE else CAL, 6111, 10, u, 0.12f)
        val tt = tn(Offset(w * 0.5f, suelo))
        drawRect(tt.s(0.3f), Offset(0f, suelo), Size(w, 2.5f * u))
        drawRect(tt.s(0.14f), Offset(0f, suelo + 2.5f * u), Size(w, 5f * u))
    }

    // ---------------------------------------------------------------- en la pared

    private fun Path.m(p: Offset) = moveTo(p.x, p.y)
    private fun Path.l(p: Offset) = lineTo(p.x, p.y)
    private fun Path.c3(a: Offset, b: Offset, c: Offset) = cubicTo(a.x, a.y, b.x, b.y, c.x, c.y)

    /**
     * La cuadrícula de traslado, trazada a carboncillo sobre la cal: rayas a pulso, más cargadas en unos tramos que en otros, y
     * dentro un trazado de sinopia (un círculo con su pentágono: el dodecaedro que se va a pintar). De noche solo se ve donde
     * llega la vela.
     */
    private fun DrawScope.cuadricula(r: Rect) {
        val u = density
        val carbon = mezcla(TINTA, SOMBRA_MUEBLE, 0.45f)
        fun raya(a: Offset, b: Offset, s: Int, col: Color = carbon, fuerza: Float = 1f) {
            for (j in 0 until 4) {
                val p0 = a + (b - a) * (j / 4f); val p1 = a + (b - a) * ((j + 1) / 4f)
                val t = tn((p0 + p1) / 2f)
                val al = (0.2f + 0.3f * azar(s, j)) * fuerza * (if (t.o) (1.5f * t.k).coerceAtMost(1f) else 1f)
                drawLine(col.copy(alpha = al.coerceIn(0f, 1f)), p0 + Offset((azar(s + 1, j) - 0.5f) * u, (azar(s + 2, j) - 0.5f) * u),
                    p1 + Offset((azar(s + 1, j + 1) - 0.5f) * u, (azar(s + 2, j + 1) - 0.5f) * u), (0.8f + 0.7f * azar(s + 3, j)) * u)
            }
        }
        val nx = 7; val ny = 6
        for (i in 0..nx) { val x = r.left + r.width * i / nx; raya(Offset(x, r.top - 5f * u * azar(6200, i)), Offset(x + 1.5f * u * (azar(6201, i) - 0.5f), r.bottom + 6f * u * azar(6202, i)), 6210 + i * 5) }
        for (j in 0..ny) { val y = r.top + r.height * j / ny; raya(Offset(r.left - 6f * u * azar(6203, j), y), Offset(r.right + 5f * u * azar(6204, j), y + 1.5f * u * (azar(6205, j) - 0.5f)), 6260 + j * 5) }
        // La sinopia: el círculo, el pentágono inscrito y dos ejes.
        val c = Offset(r.left + r.width * 0.6f, r.top + r.height * 0.42f)
        val rad = r.height * 0.36f
        val tc = tn(c)
        val sin = SANGUINA.copy(alpha = 0.42f * (if (tc.o) (1.4f * tc.k).coerceAtMost(1f) else 1f))
        drawCircle(sin, rad, c, style = Stroke(1f * u))
        val pent = (0 until 5).map { k -> val a = Math.toRadians(-90.0 + 72.0 * k); Offset(c.x + rad * cos(a).toFloat(), c.y + rad * sin(a).toFloat()) }
        for (k in 0 until 5) { raya(pent[k], pent[(k + 1) % 5], 6300 + k * 5, SANGUINA, 1.3f); raya(pent[k], pent[(k + 2) % 5], 6330 + k * 5, SANGUINA, 0.6f) }
        raya(Offset(c.x - rad * 1.2f, c.y), Offset(c.x + rad * 1.2f, c.y), 6360, SANGUINA, 0.8f)
        raya(Offset(c.x, c.y - rad * 1.2f), Offset(c.x, c.y + rad * 1.15f), 6365, SANGUINA, 0.8f)
    }

    /**
     * La hoja de estudio, de papel teñido, sujeta con dos clavos y un poco torcida: un estudio de paños a la sanguina, una
     * tela colgada de un punto que cae en pliegues de tubo y se quiebra en ángulos al llegar abajo, con el sombreado en
     * diagonal en los hondos y los realces de albayalde en los lomos; abajo, unos renglones de escritura en espejo, sin
     * palabras. La esquina de abajo se levanta.
     */
    private fun DrawScope.hojaDeEstudio(r: Rect, sup: Float) {
        val u = density
        val t = tn(r.center)
        val esq = r.width * 0.15f
        rotate(2.2f, r.center) {
            val hoja = poligono(r.left, r.top, r.right, r.top + r.height * 0.008f, r.right - r.width * 0.008f, r.bottom - esq, r.right - esq, r.bottom, r.left + r.width * 0.012f, r.bottom - r.height * 0.006f)
            sombraEnPared(hoja, r.center, sup, 0.24f, 0.55f)
            val papel = t.c(PAPEL_TINTO)
            drawPath(hoja, papel)
            clipPath(hoja) {
                val sang = t.c(SANGUINA)
                val blanco = t.c(mezcla(CARTELLINO, Color.White, 0.6f))
                fun p(x: Float, y: Float) = Offset(r.left + r.width * x, r.top + r.height * y)
                // El paño: colgado de un clavito arriba a la izquierda, recogido en un nudo de pliegues, cae en tubos casi paralelos
                // que se abren poco, y abajo se quiebra en ángulos; por la derecha, el bajo se vuelve y enseña el revés.
                val punto = p(0.36f, 0.09f)
                val bajo = listOf(0.16f to 0.83f, 0.24f to 0.88f, 0.31f to 0.8f, 0.4f to 0.86f, 0.47f to 0.79f, 0.56f to 0.9f, 0.64f to 0.81f, 0.72f to 0.85f)
                val pano = Path().apply {
                    m(punto)
                    c3(p(0.3f, 0.16f), p(0.2f, 0.3f), p(0.21f, 0.48f))
                    c3(p(0.22f, 0.62f), p(0.14f, 0.72f), p(bajo[0].first, bajo[0].second))
                    bajo.drop(1).forEach { (x, y) -> l(p(x, y)) }
                    l(p(0.8f, 0.7f))
                    c3(p(0.7f, 0.55f), p(0.62f, 0.3f), p(0.44f, 0.15f))
                    close()
                }
                drawPath(pano, mezcla(papel, SANGUINA, 0.14f))
                // El revés del bajo, vuelto: un triángulo más claro con su sombra.
                val reves = poligono(r.left + r.width * 0.72f, r.top + r.height * 0.85f, r.left + r.width * 0.8f, r.top + r.height * 0.7f, r.left + r.width * 0.86f, r.top + r.height * 0.8f)
                drawPath(reves, mezcla(papel, blanco, 0.35f))
                drawPath(reves, sang, style = Stroke(0.8f * u, join = StrokeJoin.Round))
                // Los tubos: los hondos van sombreados en diagonal; los lomos, con un realce de albayalde.
                val arribaX = listOf(0.3f, 0.34f, 0.38f, 0.42f, 0.46f, 0.5f, 0.54f)
                arribaX.forEachIndexed { i, x0 ->
                    val ini = p(x0, 0.2f + 0.02f * (i % 2))
                    val (fx, fy) = bajo[(i + 1).coerceAtMost(bajo.size - 1)]
                    val fin = p(fx, fy - 0.02f)
                    val ctl1 = p(x0 - 0.1f + 0.035f * i, 0.4f)
                    val ctl2 = p(fx + 0.06f * (if (i % 3 == 0) -1 else 1), 0.6f)
                    val linea = Path().apply { m(ini); c3(ctl1, ctl2, fin) }
                    if (i % 2 == 0) {
                        drawPath(linea, sang.copy(alpha = 0.22f), style = Stroke(r.width * 0.07f, cap = StrokeCap.Round))
                        drawPath(linea, sang.copy(alpha = 0.3f), style = Stroke(r.width * 0.035f, cap = StrokeCap.Round))
                        drawPath(linea, sang.copy(alpha = 0.45f), style = Stroke(r.width * 0.01f, cap = StrokeCap.Round))
                        for (j in 0 until 4) {
                            val f = 0.35f + 0.17f * j; val g = 1f - f
                            val q = ini * (g * g * g) + ctl1 * (3f * g * g * f) + ctl2 * (3f * g * f * f) + fin * (f * f * f)
                            drawLine(sang.copy(alpha = 0.7f), q + Offset(-r.width * 0.035f, r.width * 0.022f), q + Offset(r.width * 0.025f, -r.width * 0.02f), 0.55f * u)
                        }
                    } else {
                        clipRect(r.left, r.top + r.height * (0.26f + 0.04f * (i % 3)), r.right, r.top + r.height * (0.62f + 0.06f * (i % 2))) {
                            drawPath(linea, blanco.copy(alpha = 0.6f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
                        }
                    }
                }
                // El nudo de pliegues bajo el clavito: tres curvas cortas que se aprietan.
                for (j in 0 until 3) drawArc(sang.copy(alpha = 0.7f), 20f, 140f, false, Offset(punto.x - r.width * (0.06f + 0.02f * j), punto.y + r.height * (0.02f + 0.03f * j)), Size(r.width * (0.12f + 0.04f * j), r.height * 0.06f), style = Stroke(0.7f * u))
                // El contorno del paño, con los ángulos del bajo marcados.
                drawPath(pano, sang.copy(alpha = 0.85f), style = Stroke(0.8f * u, join = StrokeJoin.Round))
                bajo.zipWithNext().forEachIndexed { i, (a, b) ->
                    if (i % 2 == 0) drawLine(sang.copy(alpha = 0.6f), p(a.first, a.second), p((a.first + b.first) / 2 + 0.01f, a.second - 0.08f), 0.7f * u)
                }
                // El clavito del que cuelga.
                drawCircle(sang, 1.4f * u, punto)
                // La escritura en espejo: renglones de trazos, sin palabras.
                val tinta = t.c(TINTA).copy(alpha = 0.55f)
                for (j in 0 until 2) {
                    val y = 0.93f + 0.035f * j
                    val linea = Path().apply {
                        var x = 0.9f; var k = 0
                        m(p(x, y))
                        while (x > 0.45f) { x -= 0.011f + 0.012f * azar(6410 + j, k); l(p(x, y + (if (k % 2 == 0) -0.01f else 0.004f) * (0.4f + azar(6420 + j, k)))); k++ }
                    }
                    drawPath(linea, tinta, style = Stroke(0.6f * u, join = StrokeJoin.Round))
                }
                // El papel: el claro a la izquierda, el doblez del medio y unas manchas.
                drawRect(t.b(0.12f), r.topLeft, Size(r.width * 0.3f, r.height))
                drawRect(t.s(0.07f), Offset(r.right - r.width * 0.2f, r.top), Size(r.width * 0.2f, r.height))
                drawLine(t.s(0.12f), Offset(r.left, r.top + r.height * 0.5f), Offset(r.right, r.top + r.height * 0.505f), 0.8f * u)
                pinceladas(r, if (esNoche) NOCHE else NOGAL, 6430, 7, u, 0.08f)
            }
            // La esquina que se levanta: el revés y su sombra.
            val doblez = poligono(r.right - r.width * 0.008f, r.bottom - esq, r.right - esq * 0.25f, r.bottom - esq * 0.2f, r.right - esq, r.bottom)
            translate(1.5f * u, 2f * u) { drawPath(doblez, t.s(0.3f)) }
            drawPath(doblez, t.c(mezcla(PAPEL_TINTO, NOGAL, 0.22f)))
            clavo(Offset(r.left + r.width * 0.08f, r.top + r.width * 0.06f), 1.5f * u, t)
            clavo(Offset(r.right - r.width * 0.08f, r.top + r.width * 0.07f), 1.5f * u, t)
        }
    }

    /**
     * La escuadra de madera colgada de un clavo por el agujero de su vértice, un poco caída, con sus muescas de medida, y a su
     * lado la plomada: el cordel que baja de otro clavo y el peso de latón en punta.
     */
    private fun DrawScope.escuadraYPlomada(w: Float, sup: Float) {
        val u = density
        val cl = Offset(w * 0.895f, w * 0.05f)
        val t = tn(cl + Offset(0f, w * 0.1f))
        val a = w * 0.026f
        rotate(-8f, cl) {
            val ele = poligono(cl.x + a * 0.5f, cl.y - a * 0.5f, cl.x + a * 0.5f, cl.y + w * 0.21f, cl.x - a * 0.5f, cl.y + w * 0.21f,
                cl.x - a * 0.5f, cl.y + a * 0.5f, cl.x - w * 0.12f, cl.y + a * 0.5f, cl.x - w * 0.12f, cl.y - a * 0.5f)
            sombraEnPared(ele, cl, sup, 0.24f, 0.45f)
            drawPath(ele, t.c(mezcla(PERAL, BOJ, 0.3f)))
            clipPath(ele) {
                val largo = Rect(cl.x - a * 0.5f, cl.y - a * 0.5f, cl.x + a * 0.5f, cl.y + w * 0.21f)
                val corto = Rect(cl.x - w * 0.12f, cl.y - a * 0.5f, cl.x - a * 0.5f, cl.y + a * 0.5f)
                vetas(largo, (if (esNoche) NOCHE else NOGAL).copy(alpha = 0.35f), 6501, 0.8f, vertical = true, grosor = 0.5f, ondulacion = 0.4f)
                vetas(corto, (if (esNoche) NOCHE else NOGAL).copy(alpha = 0.35f), 6502, 0.8f, grosor = 0.5f, ondulacion = 0.4f)
                // La junta a inglete y las muescas de medida.
                drawLine(t.s(0.35f), Offset(cl.x - a * 0.5f, cl.y + a * 0.5f), Offset(cl.x + a * 0.5f, cl.y - a * 0.5f), 0.7f * u)
                for (k in 1..9) drawLine(t.c(TINTA).copy(alpha = 0.6f), Offset(largo.left, cl.y + a * 0.5f + w * 0.02f * k), Offset(largo.left + a * (if (k % 5 == 0) 0.5f else 0.28f), cl.y + a * 0.5f + w * 0.02f * k), 0.6f * u)
                for (k in 1..5) drawLine(t.c(TINTA).copy(alpha = 0.6f), Offset(cl.x - a * 0.5f - w * 0.02f * k, corto.bottom), Offset(cl.x - a * 0.5f - w * 0.02f * k, corto.bottom - a * (if (k % 5 == 0) 0.5f else 0.28f)), 0.6f * u)
                drawRect(t.b(0.3f), largo.topLeft, Size(1.2f * u, largo.height))
                drawRect(t.b(0.35f), Offset(corto.left, corto.top), Size(corto.width + a, 1.2f * u))
                drawRect(t.s(0.25f), Offset(largo.right - 1.6f * u, largo.top), Size(1.6f * u, largo.height))
                drawRect(t.s(0.25f), Offset(corto.left, corto.bottom - 1.5f * u), Size(corto.width, 1.5f * u))
                // El sobado de la mano en el brazo largo.
                drawOval(t.c(NOGAL).copy(alpha = 0.22f), Offset(largo.left, largo.top + largo.height * 0.45f), Size(a, w * 0.06f))
            }
        }
        clavo(cl, 1.8f * u, t)
        // La plomada.
        val c2 = Offset(w * 0.952f, w * 0.032f)
        val peso = Offset(c2.x, w * 0.3f)
        val tp = tn(peso)
        val cordel = tp.c(mezcla(LINO, NOGAL, 0.45f))
        val forma = Path().apply {
            moveTo(peso.x - w * 0.006f, peso.y); lineTo(peso.x + w * 0.006f, peso.y); lineTo(peso.x + w * 0.006f, peso.y + w * 0.008f)
            cubicTo(peso.x + w * 0.021f, peso.y + w * 0.012f, peso.x + w * 0.02f, peso.y + w * 0.03f, peso.x, peso.y + w * 0.062f)
            cubicTo(peso.x - w * 0.02f, peso.y + w * 0.03f, peso.x - w * 0.021f, peso.y + w * 0.012f, peso.x - w * 0.006f, peso.y + w * 0.008f)
            close()
        }
        val hilo = Path().apply { moveTo(c2.x - 0.6f * u, c2.y); lineTo(c2.x + 0.6f * u, c2.y); lineTo(peso.x + 0.6f * u, peso.y); lineTo(peso.x - 0.6f * u, peso.y); close() }
        sombraEnPared(Path().apply { addPath(hilo); addPath(forma) }, peso, sup, 0.22f, 0.7f)
        drawLine(cordel, c2, peso, 1.1f * u)
        // Las vueltas del cordel en el clavo.
        for (k in 0 until 3) drawLine(cordel, c2 + Offset(-2.5f * u, 1.5f * u + 1.3f * u * k), c2 + Offset(2.5f * u, 2.2f * u + 1.3f * u * k), 1f * u)
        clavo(c2, 1.6f * u, tp)
        drawPath(forma, tp.c(LATON))
        clipPath(forma) {
            cilindro(Rect(peso.x - w * 0.02f, peso.y, peso.x + w * 0.02f, peso.y + w * 0.062f), tp, 1.5f)
            drawLine(tp.s(0.3f), Offset(peso.x - w * 0.02f, peso.y + w * 0.009f), Offset(peso.x + w * 0.02f, peso.y + w * 0.009f), 0.8f * u)
        }
        destello(Offset(peso.x - w * 0.007f, peso.y + w * 0.02f), 1.2f * u, tp)
    }

    /**
     * El delantal de lino colgado de un clavo por su cinta del cuello: el peto, que se comba, la cintura fruncida y la falda
     * que cae con sus pliegues y su bajo desigual; manchado de pigmento donde se limpian las manos, y una cinta de la cintura
     * que cuelga suelta.
     */
    private fun DrawScope.delantal(gancho: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val cx = gancho.x
        val y0 = gancho.y + w * 0.08f
        val yc = y0 + w * 0.11f
        val y1 = gancho.y + w * 0.405f
        val t = tn(Offset(cx, (y0 + y1) / 2))
        val der = luzDerecha(cx)
        val tela = Path().apply {
            moveTo(cx - w * 0.036f, y0)
            cubicTo(cx - w * 0.015f, y0 + w * 0.014f, cx + w * 0.015f, y0 + w * 0.014f, cx + w * 0.036f, y0)
            cubicTo(cx + w * 0.04f, y0 + w * 0.05f, cx + w * 0.046f, y0 + w * 0.085f, cx + w * 0.064f, yc)
            cubicTo(cx + w * 0.07f, yc + w * 0.09f, cx + w * 0.064f, y1 - w * 0.08f, cx + w * 0.08f, y1 - w * 0.014f)
            cubicTo(cx + w * 0.06f, y1 + w * 0.01f, cx + w * 0.042f, y1 - w * 0.014f, cx + w * 0.022f, y1 + w * 0.002f)
            cubicTo(cx + w * 0.002f, y1 + w * 0.014f, cx - w * 0.02f, y1 - w * 0.012f, cx - w * 0.04f, y1 + w * 0.004f)
            cubicTo(cx - w * 0.055f, y1 + w * 0.012f, cx - w * 0.066f, y1 - w * 0.004f, cx - w * 0.076f, y1 - w * 0.018f)
            cubicTo(cx - w * 0.064f, y1 - w * 0.1f, cx - w * 0.072f, yc + w * 0.09f, cx - w * 0.062f, yc)
            cubicTo(cx - w * 0.046f, y0 + w * 0.085f, cx - w * 0.04f, y0 + w * 0.05f, cx - w * 0.036f, y0)
            close()
        }
        sombraEnPared(tela, Offset(cx, y0 + w * 0.15f), sup, 0.3f, 0.6f)
        // La cinta del cuello, del clavo a las dos puntas del peto, y la de la cintura, que cuelga.
        val cinta = t.c(mezcla(LINO, NOGAL, 0.25f))
        listOf(-1f, 1f).forEach { s ->
            translate(w * 0.008f, w * 0.006f) { drawLine(t.s(0.2f), gancho, Offset(cx + s * w * 0.034f, y0 + w * 0.004f), 2.2f * u) }
            drawLine(cinta, gancho, Offset(cx + s * w * 0.034f, y0 + w * 0.004f), 2.4f * u, StrokeCap.Round)
        }
        val suelta = Path().apply { moveTo(cx + w * 0.062f, yc); cubicTo(cx + w * 0.085f, yc + w * 0.03f, cx + w * 0.07f, yc + w * 0.09f, cx + w * 0.088f, yc + w * 0.15f) }
        translate(w * 0.01f, w * 0.006f) { drawPath(suelta, t.s(0.2f), style = Stroke(2.2f * u)) }
        drawPath(suelta, cinta, style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawPath(tela, t.c(mezcla(LINO, NOGAL_LUZ, 0.3f)))
        clipPath(tela) {
            // Los pliegues de la falda, que se abren hacia abajo: cada uno con su lomo de luz y su hondo de sombra.
            listOf(-0.045f to -0.062f, -0.02f to -0.03f, 0.008f to 0.006f, 0.032f to 0.04f, 0.052f to 0.07f).forEachIndexed { i, (a, b) ->
                val pliegue = Path().apply { moveTo(cx + w * a, yc - w * 0.004f); cubicTo(cx + w * a, yc + w * 0.08f, cx + w * b, y1 - w * 0.1f, cx + w * b, y1 + w * 0.02f) }
                drawPath(pliegue, t.s(0.17f), style = Stroke(w * (0.011f + 0.003f * (i % 2))))
                translate(-w * 0.011f, 0f) { drawPath(pliegue, t.b(0.3f), style = Stroke(w * 0.007f)) }
                translate(w * 0.004f, 0f) { drawPath(pliegue, t.s(0.14f), style = Stroke(0.9f * u)) }
            }
            // El fruncido de la cintura y la comba del peto.
            for (k in 0 until 7) { val x = cx + w * (-0.05f + 0.016f * k); drawLine(t.s(0.22f), Offset(x, yc - w * 0.012f), Offset(x + w * 0.003f * (k - 3), yc + w * 0.016f), 0.9f * u) }
            drawRect(t.c(mezcla(LINO, NOGAL, 0.3f)), Offset(cx - w * 0.07f, yc - w * 0.006f), Size(w * 0.14f, w * 0.009f))
            drawRect(t.b(0.3f), Offset(cx - w * 0.07f, yc - w * 0.006f), Size(w * 0.14f, 0.9f * u))
            drawPath(Path().apply { moveTo(cx - w * 0.03f, y0 + w * 0.02f); cubicTo(cx - w * 0.01f, y0 + w * 0.04f, cx + w * 0.012f, y0 + w * 0.04f, cx + w * 0.03f, y0 + w * 0.02f) }, t.s(0.14f), style = Stroke(w * 0.01f))
            // El lado de la luz y el de la sombra.
            if (der) { drawRect(t.b(0.18f), Offset(cx + w * 0.03f, y0), Size(w * 0.06f, y1 - y0 + w * 0.02f)); drawRect(t.s(0.16f), Offset(cx - w * 0.08f, y0), Size(w * 0.035f, y1 - y0 + w * 0.02f)) }
            else { drawRect(t.b(0.16f), Offset(cx - w * 0.08f, y0), Size(w * 0.04f, y1 - y0 + w * 0.02f)); drawRect(t.s(0.16f), Offset(cx + w * 0.045f, y0), Size(w * 0.04f, y1 - y0 + w * 0.02f)) }
            // Las manchas de pigmento: donde se limpian las manos.
            drawOval(t.c(ULTRAMAR).copy(alpha = 0.55f), Offset(cx - w * 0.04f, yc + w * 0.045f), Size(w * 0.034f, w * 0.024f))
            drawOval(t.c(ULTRAMAR).copy(alpha = 0.35f), Offset(cx - w * 0.025f, yc + w * 0.06f), Size(w * 0.03f, w * 0.045f))
            for (k in 0 until 3) drawLine(t.c(OCRE).copy(alpha = 0.6f), Offset(cx + w * (0.008f + 0.011f * k), yc + w * 0.1f), Offset(cx + w * (0.02f + 0.011f * k), yc + w * 0.15f), 2.6f * u, StrokeCap.Round)
            drawOval(t.c(BERMELLON).copy(alpha = 0.6f), Offset(cx + w * 0.004f, y0 + w * 0.05f), Size(w * 0.02f, w * 0.016f))
            drawOval(t.c(BERMELLON).copy(alpha = 0.4f), Offset(cx - w * 0.05f, yc + w * 0.15f), Size(w * 0.016f, w * 0.022f))
            drawOval(t.c(TIERRA_VERDE).copy(alpha = 0.55f), Offset(cx + w * 0.02f, y1 - w * 0.085f), Size(w * 0.036f, w * 0.022f))
            drawOval(t.c(NOGAL).copy(alpha = 0.18f), Offset(cx - w * 0.07f, y1 - w * 0.05f), Size(w * 0.15f, w * 0.06f))
            // El dobladillo.
            translate(0f, -w * 0.012f) { drawPath(tela, t.s(0.16f), style = Stroke(0.8f * u)) }
        }
        clavo(gancho, 1.8f * u, tn(gancho))
    }

    // ---------------------------------------------------------------- el armario

    /**
     * El cuerpo del armario de nogal: su sombra en la pared y en el suelo, el frente (de noche, más encendido arriba), los
     * largueros con su veta, el rebaje de cada hueco con su filete de arce y de ébano, las pilastras con sus candelabros, el
     * friso de roleo, el arquitrabe, el zócalo de tres paneles de taracea, la moldura de abajo y las patas de ménsula.
     */
    private fun DrawScope.armario(d: Disposicion, ch: Float, b: Rect) {
        val u = density
        val o = esNoche
        val w = anchoW
        val hb = b.height
        val xL = w * IZQ; val xR = w * DER
        val suelo = b.top + hb * SUELO
        val pisan = b.top + hb * PISAN
        val zoc = b.top + hb * ZOC
        val cuerpo = Rect(xL, ch * FRISO, xR, zoc)

        // Su sombra en la pared (a la derecha) y en el suelo; lo de debajo, entre las patas, a oscuras.
        val t0 = tn(Offset(xR, ch * 1.3f))
        drawPath(poligono(xR, ch * SUP, xR + w * 0.022f, ch * SUP + w * 0.03f, xR + w * 0.022f, suelo, xR, suelo), t0.s(0.3f))
        val tp = tn(Offset(w * 0.5f, pisan))
        drawRect(tp.s(0.5f), Offset(xL, zoc), Size(xR - xL, pisan - zoc))
        drawRect(tp.s(0.36f), Offset(xL + w * 0.012f, pisan - 3f * u), Size(xR - xL + w * 0.012f, hb * 0.05f))
        drawRect(tp.s(0.17f), Offset(xL + w * 0.03f, pisan + hb * 0.05f - 3f * u), Size(xR - xL, hb * 0.05f))

        // El frente de nogal: los travesaños, de veta tendida.
        if (!o) {
            drawRect(NOGAL, cuerpo.topLeft, cuerpo.size)
            drawRect(Brush.horizontalGradient(listOf(luzMueble(0.16f), Color.Transparent, sombraMueble(0.24f)), xL, xR), cuerpo.topLeft, cuerpo.size)
        } else {
            drawRect(Brush.verticalGradient(List(9) { i -> tn(Offset(w * LUZ.x, cuerpo.top + cuerpo.height * i / 8f)).c(NOGAL) }, cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
            drawRect(Brush.horizontalGradient(listOf(NOCHE.copy(alpha = 0.4f), Color.Transparent, Color.Transparent, NOCHE.copy(alpha = 0.4f)), xL, xR), cuerpo.topLeft, cuerpo.size)
        }
        vetas(cuerpo, (if (o) NOCHE else NOGAL_OSC).copy(alpha = 0.32f), 6601, 0.32f, grosor = 0.6f, ondulacion = 0.6f)
        pinceladas(cuerpo, if (o) NOCHE else NOGAL_OSC, 6602, 22, u, 0.1f)
        // El larguero del medio, de veta de pie, con sus juntas.
        val primera = d.filas.first(); val ultima = d.filas.last()
        if (cols == 2 && d.huecos.size >= 2) {
            val x0 = d.huecos[0].right + 6.8f * u; val x1 = d.huecos[1].left - 6.8f * u
            if (x1 > x0) vetas(Rect(x0, primera.top, x1, ultima.bottom), (if (o) NOCHE else NOGAL_OSC).copy(alpha = 0.3f), 6603, 0.9f, vertical = true, grosor = 0.5f, ondulacion = 0.4f)
        }

        // El rebaje de cada hueco: la sombra de su hondo, el filete de arce entre dos de ébano y el canto de abajo, que coge luz.
        d.huecos.forEach { r ->
            val t = tn(r.center)
            drawRect(t.c(EBANO), Offset(r.left - 6.2f * u, r.top - 6.2f * u), Size(r.width + 12.4f * u, r.height + 12.4f * u), style = Stroke(1.1f * u))
            drawRect(t.c(ARCE), Offset(r.left - 4.6f * u, r.top - 4.6f * u), Size(r.width + 9.2f * u, r.height + 9.2f * u), style = Stroke(1.6f * u))
            drawRect(t.s(0.6f), Offset(r.left - 2.6f * u, r.top - 2.6f * u), Size(r.width + 5.2f * u, r.height + 5.2f * u))
            // El hondo: la tabla del fondo, con la sombra de la repisa de arriba y del costado de la izquierda.
            drawRect(t.c(mezcla(NOGAL_OSC, NOGAL, 0.35f)), r.topLeft, r.size)
            vetas(r, (if (o) NOCHE else EBANO).copy(alpha = 0.3f), 6640 + r.top.toInt() % 97, 0.5f, vertical = true, grosor = 0.5f, ondulacion = 0.4f)
            drawRect(if (o) NOCHE.copy(alpha = 0.3f) else sombraMueble(0.32f), r.topLeft, Size(r.width, r.height * 0.3f))
            drawRect(if (o) NOCHE.copy(alpha = 0.35f) else sombraMueble(0.4f), r.topLeft, Size(r.width, r.height * 0.1f))
            drawPath(poligono(r.left, r.top, r.left + r.width * 0.07f, r.top + r.height * 0.1f, r.left + r.width * 0.07f, r.bottom - r.height * 0.06f, r.left, r.bottom), if (o) NOCHE.copy(alpha = 0.35f) else sombraMueble(0.3f))
            drawPath(poligono(r.right, r.top, r.right - r.width * 0.07f, r.top + r.height * 0.1f, r.right - r.width * 0.07f, r.bottom - r.height * 0.06f, r.right, r.bottom), t.b(if (o) 0.25f else 0.1f))
            drawPath(poligono(r.left, r.bottom, r.left + r.width * 0.07f, r.bottom - r.height * 0.06f, r.right - r.width * 0.07f, r.bottom - r.height * 0.06f, r.right, r.bottom), t.c(mezcla(NOGAL, NOGAL_LUZ, 0.5f)))
            drawRect(t.b(0.35f), Offset(r.left - 2.6f * u, r.bottom + 1.6f * u), Size(r.width + 5.2f * u, 1f * u))
            drawRect(t.b(0.2f), Offset(r.right + 1.6f * u, r.top - 2.6f * u), Size(1f * u, r.height + 5.2f * u))
        }

        // Las pilastras, de arriba abajo del frente.
        val pTop = ch * FRISO + 2f * u; val pBot = b.top + hb * 0.025f
        pilastra(Rect(w * 0.0275f, pTop, w * 0.0665f, pBot), false)
        pilastra(Rect(w * 0.9335f, pTop, w * 0.9725f, pBot), true)

        // El friso de roleo y el arquitrabe.
        friso(Rect(xL - w * 0.004f, ch * DENT, xR + w * 0.004f, ch * FRISO - 3f * u))
        val ta = tn(Offset(w * LUZ.x, ch))
        val arq = Rect(xL - w * 0.006f, ch * FRISO - 3f * u, xR + w * 0.006f, ch * FRISO + 2.5f * u)
        drawRect(ta.c(NOGAL_LUZ), arq.topLeft, arq.size)
        drawRect(ta.b(0.5f), arq.topLeft, Size(arq.width, 1f * u))
        drawRect(ta.s(0.4f), Offset(arq.left, arq.bottom), Size(arq.width, 2f * u))
        drawRect(ta.s(0.18f), Offset(xL, arq.bottom + 2f * u), Size(xR - xL, 3f * u))

        // ---- el zócalo: la moldura que lo separa del cuerpo y los tres paneles de taracea
        val tz = tn(Offset(w * 0.5f, b.top + hb * 0.25f))
        drawRect(tz.s(0.35f), Offset(xL, b.top + hb * 0.012f), Size(xR - xL, 1.2f * u))
        drawRect(tz.c(NOGAL_LUZ), Offset(xL - w * 0.004f, b.top + hb * 0.02f), Size(xR - xL + w * 0.008f, hb * 0.022f))
        drawRect(tz.b(0.45f), Offset(xL - w * 0.004f, b.top + hb * 0.02f), Size(xR - xL + w * 0.008f, 1f * u))
        drawRect(tz.s(0.3f), Offset(xL, b.top + hb * 0.042f), Size(xR - xL, 2f * u))
        val py0 = b.top + hb * PANEL0; val py1 = b.top + hb * PANEL1
        val pa = Rect(w * 0.04f, py0, w * 0.325f, py1)
        val pb = Rect(w * 0.345f, py0, w * 0.655f, py1)
        val pc = Rect(w * 0.675f, py0, w * 0.96f, py1)
        listOf(pa, pb, pc).forEachIndexed { i, r ->
            val t = tn(r.center)
            drawRect(t.s(0.5f), Offset(r.left - 1.5f * u, r.top - 1.5f * u), Size(r.width + 3f * u, r.height + 3f * u))
            tabla(r, if (i == 1) ROBLE else PERAL, t, 6610 + i, densidad = 0.3f)
        }
        tondo(pa, false)
        cubos(pb)
        tondo(pc, true)
        listOf(pa, pb, pc).forEach { r ->
            val t = tn(r.center)
            drawRect(t.c(EBANO), Offset(r.left + 2.2f * u, r.top + 2.2f * u), Size(r.width - 4.4f * u, r.height - 4.4f * u), style = Stroke(1f * u))
            drawRect(t.c(ARCE), Offset(r.left + 0.7f * u, r.top + 0.7f * u), Size(r.width - 1.4f * u, r.height - 1.4f * u), style = Stroke(1.4f * u))
            drawRect(t.s(0.3f), r.topLeft, Size(r.width, 2.2f * u))
            drawRect(t.s(0.2f), r.topLeft, Size(2f * u, r.height))
        }

        // ---- la moldura de abajo, con sus dos planos, y las patas de ménsula
        val mol = Rect(xL - w * 0.012f, b.top + hb * (PANEL1 + 0.025f), xR + w * 0.012f, zoc)
        val m1 = mol.top + mol.height * 0.45f
        drawRect(tz.c(NOGAL_LUZ), mol.topLeft, Size(mol.width, m1 - mol.top))
        drawRect(tz.c(mezcla(NOGAL, NOGAL_OSC, 0.3f)), Offset(mol.left + w * 0.004f, m1), Size(mol.width - w * 0.008f, mol.bottom - m1))
        vetas(mol, (if (o) NOCHE else NOGAL_OSC).copy(alpha = 0.3f), 6620, 0.5f, grosor = 0.5f)
        drawRect(tz.b(0.5f), mol.topLeft, Size(mol.width, 1.1f * u))
        drawRect(tz.s(0.35f), Offset(mol.left, m1), Size(mol.width, 1.2f * u))
        drawRect(tz.s(0.25f), Offset(mol.right - w * 0.014f, mol.top), Size(w * 0.014f, mol.height))
        listOf(false, true).forEach { derecha ->
            val pw = w * 0.115f
            val x0 = if (derecha) xR + w * 0.006f - pw else xL - w * 0.006f
            val y0 = zoc; val y1 = pisan
            val pata = if (!derecha) Path().apply {
                moveTo(x0, y0); lineTo(x0 + pw, y0); lineTo(x0 + pw, y0 + (y1 - y0) * 0.22f)
                cubicTo(x0 + pw * 0.6f, y0 + (y1 - y0) * 0.3f, x0 + pw * 0.5f, y0 + (y1 - y0) * 0.75f, x0 + pw * 0.3f, y1); lineTo(x0, y1); close()
            } else Path().apply {
                moveTo(x0 + pw, y0); lineTo(x0, y0); lineTo(x0, y0 + (y1 - y0) * 0.22f)
                cubicTo(x0 + pw * 0.4f, y0 + (y1 - y0) * 0.3f, x0 + pw * 0.5f, y0 + (y1 - y0) * 0.75f, x0 + pw * 0.7f, y1); lineTo(x0 + pw, y1); close()
            }
            val t = tn(Offset(x0 + pw / 2, y1))
            drawPath(pata, t.c(mezcla(NOGAL, NOGAL_OSC, 0.25f)))
            clipPath(pata) {
                drawRect(t.s(0.3f), Offset(x0, y0), Size(pw, 2.5f * u))
                if (derecha) drawRect(t.s(0.28f), Offset(x0 + pw * 0.72f, y0), Size(pw * 0.28f, y1 - y0))
                else drawRect(t.b(0.25f), Offset(x0, y0), Size(pw * 0.14f, y1 - y0))
            }
        }
        // Un golpe en la moldura y unas rozaduras: el uso.
        drawPath(poligono(mol.right - w * 0.035f, mol.top, mol.right, mol.top, mol.right, m1), tz.c(mezcla(NOGAL_LUZ, PERAL, 0.5f)))
        for (k in 0 until 4) {
            val x = mol.left + mol.width * (0.15f + 0.7f * azar(6630, k))
            drawLine(tz.b(0.3f), Offset(x, m1 + 2f * u), Offset(x + w * 0.02f, m1 + (mol.bottom - m1) * (0.4f + 0.4f * azar(6631, k))), 0.6f * u)
        }
    }

    /**
     * Una pilastra: el campo de roble ahumado con su filete de arce, el capitel y la basa de nogal claro, y el candelabro de
     * taracea que baja por ella en módulos (el vaso de peral con su mitad en sombra, las dos hojas de arce, el botón de boj).
     */
    private fun DrawScope.pilastra(r: Rect, derecha: Boolean) {
        val u = density
        val w = anchoW
        val modulo = w * 0.098f
        val tramos = ((r.height - w * 0.03f) / modulo).toInt().coerceAtLeast(1)
        val paso = (r.height - w * 0.03f) / tramos
        val y0 = r.top + w * 0.015f
        for (i in 0 until tramos) {
            val t = tn(Offset(r.center.x, y0 + paso * (i + 0.5f)))
            drawRect(t.c(ROBLE), Offset(r.left, y0 + paso * i - (if (i == 0) w * 0.015f else 0f)), Size(r.width, paso + 0.6f + (if (i == 0 || i == tramos - 1) w * 0.015f else 0f)))
        }
        vetas(r, (if (esNoche) NOCHE else EBANO).copy(alpha = 0.35f), 6700 + (if (derecha) 1 else 0), 0.7f, vertical = true, grosor = 0.5f, ondulacion = 0.4f)
        for (i in 0 until tramos) {
            val y = y0 + paso * i
            val t = tn(Offset(r.center.x, y + paso / 2))
            val cx = r.center.x; val a = r.width; val m = paso
            // El tallo, las dos hojas, el vaso y los botones.
            drawLine(t.c(ARCE), Offset(cx, y + m * 0.04f), Offset(cx, y + m * 0.96f), 1f * u)
            listOf(-1f, 1f).forEach { s ->
                val hoja = Path().apply {
                    moveTo(cx, y + m * 0.46f)
                    cubicTo(cx + s * a * 0.16f, y + m * 0.42f, cx + s * a * 0.44f, y + m * 0.34f, cx + s * a * 0.4f, y + m * 0.15f)
                    cubicTo(cx + s * a * 0.2f, y + m * 0.24f, cx + s * a * 0.04f, y + m * 0.33f, cx, y + m * 0.46f)
                    close()
                }
                drawPath(hoja, t.c(if (s < 0) ARCE else mezcla(ARCE, PERAL, 0.7f)))
                drawLine(t.c(ROBLE).copy(alpha = 0.6f), Offset(cx + s * a * 0.05f, y + m * 0.42f), Offset(cx + s * a * 0.34f, y + m * 0.22f), 0.5f * u)
                drawCircle(t.c(BOJ), a * 0.055f, Offset(cx + s * a * 0.3f, y + m * 0.9f))
            }
            val vaso = Path().apply {
                moveTo(cx - a * 0.1f, y + m * 0.52f)
                cubicTo(cx - a * 0.44f, y + m * 0.57f, cx - a * 0.4f, y + m * 0.72f, cx - a * 0.1f, y + m * 0.76f)
                lineTo(cx - a * 0.22f, y + m * 0.82f); lineTo(cx + a * 0.22f, y + m * 0.82f); lineTo(cx + a * 0.1f, y + m * 0.76f)
                cubicTo(cx + a * 0.4f, y + m * 0.72f, cx + a * 0.44f, y + m * 0.57f, cx + a * 0.1f, y + m * 0.52f)
                close()
            }
            val medio = Path().apply {
                moveTo(cx, y + m * 0.52f); lineTo(cx + a * 0.1f, y + m * 0.52f)
                cubicTo(cx + a * 0.44f, y + m * 0.57f, cx + a * 0.4f, y + m * 0.72f, cx + a * 0.1f, y + m * 0.76f)
                lineTo(cx + a * 0.22f, y + m * 0.82f); lineTo(cx, y + m * 0.82f); close()
            }
            drawPath(vaso, t.c(if (i % 2 == 0) PERAL else BOJ))
            drawPath(medio, t.c(mezcla(if (i % 2 == 0) PERAL else BOJ, NOGAL, 0.4f)))
            drawLine(t.c(ROBLE), Offset(cx - a * 0.3f, y + m * 0.64f), Offset(cx + a * 0.3f, y + m * 0.64f), 0.8f * u)
            drawPath(rombo(Offset(cx, y + m * 0.09f), a * 0.1f, m * 0.05f), t.c(BOJ))
        }
        // El filete, el capitel y la basa; el canto de la luz y el de la sombra.
        val tc = tn(Offset(r.center.x, r.top)); val tb = tn(Offset(r.center.x, r.bottom))
        for (i in 0 until tramos) {
            val t = tn(Offset(r.center.x, y0 + paso * (i + 0.5f)))
            val ya = y0 + paso * i; val alto = paso + 0.6f
            drawRect(t.c(ARCE), Offset(r.left + 1.6f * u, ya), Size(1.1f * u, alto))
            drawRect(t.c(ARCE), Offset(r.right - 2.7f * u, ya), Size(1.1f * u, alto))
            drawRect(if (derecha) t.s(0.2f) else t.b(0.22f), Offset(r.left, ya), Size(1.1f * u, alto))
            drawRect(t.s(0.35f), Offset(r.right - 1.2f * u, ya), Size(1.2f * u, alto))
            if (derecha) drawRect(t.s(0.14f), Offset(r.left, ya), Size(r.width, alto))
        }
        listOf(Rect(r.left - 2.5f * u, r.top, r.right + 2.5f * u, r.top + w * 0.015f) to tc, Rect(r.left - 2.5f * u, r.bottom - w * 0.015f, r.right + 2.5f * u, r.bottom) to tb).forEach { (q, t) ->
            drawRect(t.s(0.35f), Offset(q.left + 1.5f * u, q.top + 2f * u), q.size)
            drawRect(t.c(NOGAL_LUZ), q.topLeft, q.size)
            drawRect(t.c(BOJ), Offset(q.left + 3f * u, q.center.y - 0.6f * u), Size(q.width - 6f * u, 1.2f * u))
            drawRect(t.b(0.5f), q.topLeft, Size(q.width, 1f * u))
            drawRect(t.s(0.3f), Offset(q.left, q.bottom - 1.2f * u), Size(q.width, 1.2f * u))
        }
    }

    /**
     * El friso: el campo de peral, de veta tendida, entre dos filetes de ébano y arce, con un roleo de roble ahumado y nogal
     * (la onda, la voluta de cada seno y su hoja) y los botones de boj.
     */
    private fun DrawScope.friso(r: Rect) {
        val u = density
        val fh = r.height
        val periodo = fh * 2.5f
        val n = (r.width / periodo).toInt().coerceAtLeast(1)
        val p = r.width / n
        val cy = r.center.y
        for (i in 0 until n) {
            val x0 = r.left + p * i
            val t = tn(Offset(x0 + p / 2, cy))
            drawRect(t.c(PERAL), Offset(x0, r.top), Size(p + 0.6f, fh))
        }
        vetas(r, (if (esNoche) NOCHE else NOGAL).copy(alpha = 0.25f), 6800, 0.6f, grosor = 0.5f, ondulacion = 0.4f)
        for (i in 0 until n) {
            val x0 = r.left + p * i
            val t = tn(Offset(x0 + p / 2, cy))
            val a = fh * 0.26f
            val onda = Path().apply {
                moveTo(x0, cy)
                cubicTo(x0 + p * 0.17f, cy - a * 1.5f, x0 + p * 0.33f, cy - a * 1.5f, x0 + p * 0.5f, cy)
                cubicTo(x0 + p * 0.67f, cy + a * 1.5f, x0 + p * 0.83f, cy + a * 1.5f, x0 + p, cy)
            }
            drawPath(onda, t.c(ROBLE), style = Stroke(1.8f * u))
            listOf(0.25f to 1f, 0.75f to -1f).forEach { (f, s) ->
                val c = Offset(x0 + p * f, cy + s * a * 0.5f)
                val rv = fh * 0.17f
                drawArc(t.c(ROBLE), if (s > 0) 150f else -30f, 290f, false, c - Offset(rv, rv), Size(2f * rv, 2f * rv), style = Stroke(1.5f * u, cap = StrokeCap.Round))
                drawCircle(t.c(BOJ), fh * 0.065f, c)
                // La hoja que nace de la onda, hacia fuera.
                val h0 = Offset(x0 + p * (f + 0.17f), cy - s * a * 0.55f)
                val hoja = Path().apply {
                    moveTo(h0.x, h0.y)
                    cubicTo(h0.x + p * 0.03f, h0.y - s * fh * 0.2f, h0.x + p * 0.1f, h0.y - s * fh * 0.24f, h0.x + p * 0.14f, h0.y - s * fh * 0.16f)
                    cubicTo(h0.x + p * 0.1f, h0.y - s * fh * 0.1f, h0.x + p * 0.05f, h0.y - s * fh * 0.02f, h0.x, h0.y)
                    close()
                }
                drawPath(hoja, t.c(if (s > 0) NOGAL else CEREZO))
            }
        }
        for (i in 0 until n) {
            val x0 = r.left + p * i
            val t = tn(Offset(x0 + p / 2, cy))
            drawRect(t.c(EBANO), Offset(x0, r.top), Size(p + 0.6f, 1.1f * u))
            drawRect(t.c(ARCE), Offset(x0, r.top + 1.1f * u), Size(p + 0.6f, 1.3f * u))
            drawRect(t.c(ARCE), Offset(x0, r.bottom - 2.4f * u), Size(p + 0.6f, 1.3f * u))
            drawRect(t.c(EBANO), Offset(x0, r.bottom - 1.1f * u), Size(p + 0.6f, 1.1f * u))
        }
    }

    /**
     * Un panel con su tondo: el aro de ébano y boj, el disco de arce y, dentro, el mazzocchio en perspectiva (o, con [estrella],
     * una estrella de ocho puntas de dos maderas); a los lados, dos rombos partidos en luz y sombra y cuatro enjutas de roble.
     */
    private fun DrawScope.tondo(r: Rect, estrella: Boolean) {
        val u = density
        val t = tn(r.center)
        val c = r.center
        val rad = r.height * 0.43f
        // Las enjutas y los rombos de los lados.
        val e = r.height * 0.2f
        listOf(r.topLeft to Offset(1f, 1f), Offset(r.right, r.top) to Offset(-1f, 1f), Offset(r.left, r.bottom) to Offset(1f, -1f), r.bottomRight to Offset(-1f, -1f)).forEach { (p, s) ->
            val q = p + Offset(s.x * 4f * u, s.y * 4f * u)
            drawPath(poligono(q.x, q.y, q.x + s.x * e, q.y, q.x, q.y + s.y * e), t.c(ROBLE))
        }
        listOf(-1f, 1f).forEach { s ->
            val rc = Offset(c.x + s * (rad + (r.width / 2 - rad) * 0.5f), c.y)
            val a = (r.width / 2 - rad) * 0.24f; val b = r.height * 0.3f
            pieza(poligono(rc.x, rc.y - b, rc.x, rc.y + b, rc.x - a, rc.y), ARCE, 90f, t, rc - Offset(a * 0.4f, 0f), a * 0.8f)
            pieza(poligono(rc.x, rc.y - b, rc.x + a, rc.y, rc.x, rc.y + b), NOGAL, 90f, t, rc + Offset(a * 0.4f, 0f), a * 0.8f)
        }
        drawCircle(t.s(0.3f), rad + 2.5f * u, c + Offset(1.2f * u, 1.5f * u))
        drawCircle(t.c(EBANO), rad + 2.2f * u, c)
        drawCircle(t.c(BOJ), rad + 0.8f * u, c)
        drawCircle(t.c(if (estrella) ROBLE else ARCE), rad - 0.6f * u, c)
        if (!estrella) {
            for (k in -2..2) drawLine(t.s(0.1f), Offset(c.x + rad * 0.3f * k, c.y - rad * 0.8f), Offset(c.x + rad * 0.3f * k + rad * 0.04f, c.y + rad * 0.8f), 0.5f * u)
            mazzocchio(Offset(c.x, c.y + rad * 0.06f), rad * 0.8f, t)
        } else {
            // La estrella de ocho puntas: cada punta, mitad de boj y mitad de peral, para que parezca tallada.
            for (k in 0 until 8) {
                val a0 = Math.toRadians(-90.0 + 45.0 * k); val a1 = Math.toRadians(-90.0 + 45.0 * k - 22.5); val a2 = Math.toRadians(-90.0 + 45.0 * k + 22.5)
                val punta = Offset(c.x + rad * 0.86f * cos(a0).toFloat(), c.y + rad * 0.86f * sin(a0).toFloat())
                val i1 = Offset(c.x + rad * 0.4f * cos(a1).toFloat(), c.y + rad * 0.4f * sin(a1).toFloat())
                val i2 = Offset(c.x + rad * 0.4f * cos(a2).toFloat(), c.y + rad * 0.4f * sin(a2).toFloat())
                pieza(poligono(c.x, c.y, i1.x, i1.y, punta.x, punta.y), ARCE, -90f + 45f * k, t, (c + i1 + punta) / 3f, rad * 0.22f)
                pieza(poligono(c.x, c.y, punta.x, punta.y, i2.x, i2.y), if (k % 2 == 0) CEREZO else BOJ, -90f + 45f * k, t, (c + i2 + punta) / 3f, rad * 0.22f)
            }
            drawCircle(t.c(EBANO), rad * 0.1f, c)
        }
    }

    /**
     * El mazzocchio: el anillo facetado de los estudios de perspectiva, en taracea. Por fuera, las caras de canto (peral y
     * nogal); por arriba, las de la tapa (arce y roble ahumado), y al fondo, las de dentro (boj y ébano).
     */
    private fun DrawScope.mazzocchio(c: Offset, r: Float, t: Tono) {
        val n = 16
        fun e(a: Double, rx: Float, ry: Float, dy: Float) = Offset(c.x + rx * cos(a).toFloat(), c.y + dy + ry * sin(a).toFloat())
        fun cara(a0: Double, a1: Double, p: (Double) -> Offset, q: (Double) -> Offset) = Path().apply { m(p(a0)); l(p(a1)); l(q(a1)); l(q(a0)); close() }
        val fuera = { a: Double -> e(a, r, r * 0.46f, 0f) }
        val abajo = { a: Double -> e(a, r, r * 0.46f, r * 0.26f) }
        val dentro = { a: Double -> e(a, r * 0.56f, r * 0.24f, -r * 0.04f) }
        val hondo = { a: Double -> e(a, r * 0.56f, r * 0.24f, r * 0.2f) }
        for (k in 0 until n) {
            val a0 = k * 2 * Math.PI / n; val a1 = (k + 1) * 2 * Math.PI / n
            // Las caras de dentro, al fondo (solo se ven las de atrás).
            if (sin((a0 + a1) / 2) < 0) drawPath(cara(a0, a1, dentro, hondo), t.c(if (k % 2 == 0) BOJ else EBANO))
        }
        for (k in 0 until n) {
            val a0 = k * 2 * Math.PI / n; val a1 = (k + 1) * 2 * Math.PI / n
            // La tapa.
            val lado = cos((a0 + a1) / 2).toFloat()
            drawPath(cara(a0, a1, fuera, dentro), t.c(mezcla(if (k % 2 == 0) ARCE else ROBLE, NOGAL, if (lado > 0) 0.22f * lado else 0f)))
        }
        for (k in 0 until n) {
            val a0 = k * 2 * Math.PI / n; val a1 = (k + 1) * 2 * Math.PI / n
            // El canto de fuera (solo el de delante).
            if (sin((a0 + a1) / 2) > 0) {
                val lado = cos((a0 + a1) / 2).toFloat()
                drawPath(cara(a0, a1, fuera, abajo), t.c(mezcla(if (k % 2 == 0) NOGAL else PERAL, EBANO, if (lado > 0) 0.35f * lado else 0f)))
            }
        }
    }

    /**
     * El trampantojo de cubos: tres maderas (arce arriba, boj a la izquierda, nogal a la derecha), cada rombo con su veta en
     * su dirección, sobre el campo de roble ahumado.
     */
    private fun DrawScope.cubos(r: Rect) {
        val t = tn(r.center)
        val s = maxOf(1f, r.height / 4.4f)
        clipRect(r.left + 3f * density, r.top + 3f * density, r.right - 3f * density, r.bottom - 3f * density) {
            var fila = 0
            var cy = r.top + s * 0.4f
            while (cy < r.bottom + s) {
                var cx = r.left + (if (fila % 2 == 0) 0f else s * 0.866f)
                while (cx < r.right + s) {
                    pieza(poligono(cx, cy, cx + 0.866f * s, cy - 0.5f * s, cx, cy - s, cx - 0.866f * s, cy - 0.5f * s), ARCE, 0f, t, Offset(cx, cy - s * 0.5f), s * 0.5f)
                    pieza(poligono(cx, cy, cx - 0.866f * s, cy - 0.5f * s, cx - 0.866f * s, cy + 0.5f * s, cx, cy + s), BOJ, 60f, t, Offset(cx - s * 0.43f, cy + s * 0.25f), s * 0.45f)
                    pieza(poligono(cx, cy, cx + 0.866f * s, cy - 0.5f * s, cx + 0.866f * s, cy + 0.5f * s, cx, cy + s), NOGAL, -60f, t, Offset(cx + s * 0.43f, cy + s * 0.25f), s * 0.45f)
                    cx += maxOf(1f, 1.732f * s)
                }
                cy += maxOf(1f, 1.5f * s); fila++
            }
        }
    }

    /**
     * La tapa del armario: los dentículos bajo la cornisa, cada uno con su sombra a la derecha; la tabla vista un poco desde
     * arriba (su veta, un cerco, gotas de cera y salpicaduras de pigmento), y el canto de la cornisa en dos planos.
     */
    private fun DrawScope.tapa(w: Float, ch: Float) {
        val u = density
        val o = esNoche
        val sup = ch * SUP; val frente = ch * FRENTE; val corn = ch * CORNISA; val dent = ch * DENT
        val xL = w * IZQ - w * 0.016f; val xR = w * DER + w * 0.016f
        // Los dentículos.
        val db = Rect(w * IZQ - w * 0.006f, corn, w * DER + w * 0.006f, dent)
        val paso = db.width / 44f
        for (i in 0 until 44) {
            val x = db.left + paso * i
            val t = tn(Offset(x + paso / 2, corn))
            drawRect(t.c(NOGAL_OSC), Offset(x, db.top), Size(paso + 0.6f, db.height))
            val a = paso * 0.58f
            drawRect(t.s(0.5f), Offset(x + paso * 0.2f + 2.4f * u, db.top), Size(a, db.height - 1f * u))
            drawRect(t.c(NOGAL_LUZ), Offset(x + paso * 0.2f, db.top), Size(a, db.height - 2.6f * u))
            drawRect(t.b(0.4f), Offset(x + paso * 0.2f, db.top), Size(1f * u, db.height - 2.6f * u))
            drawRect(t.s(0.25f), Offset(x + paso * 0.2f + a - 1.2f * u, db.top), Size(1.2f * u, db.height - 2.6f * u))
            drawRect(t.s(0.3f), Offset(x + paso * 0.2f, db.bottom - 3.8f * u), Size(a, 1.2f * u))
            drawRect(t.s(0.42f), Offset(x, db.top), Size(paso + 0.6f, db.height * 0.3f))
            drawRect(t.b(0.3f), Offset(x, db.bottom - 1f * u), Size(paso + 0.6f, 1f * u))
        }
        // La tabla de arriba: más angosta al fondo.
        val fondo = w * 0.014f
        val tabla = poligono(xL + fondo, sup, xR - fondo, sup, xR, frente, xL, frente)
        val madera = mezcla(NOGAL_LUZ, PERAL, 0.22f)
        if (!o) drawPath(tabla, madera)
        else drawPath(tabla, Brush.horizontalGradient(List(7) { i -> tn(Offset(xL + (xR - xL) * i / 6f, sup)).c(madera) }, xL, xR))
        clipPath(tabla) {
            if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.32f), Color.Transparent, sombraMueble(0.14f)), xL, xR), Offset(xL, sup), Size(xR - xL, frente - sup))
            vetas(Rect(xL, sup, xR, frente), (if (o) NOCHE else NOGAL_OSC).copy(alpha = 0.32f), 6900, 0.9f, grosor = 0.5f, ondulacion = 0.4f)
            val tt = tn(Offset(w * 0.5f, sup))
            drawRect(tt.s(0.3f), Offset(xL, sup), Size(xR - xL, 1.5f * u))
            // Un cerco, gotas de cera junto a la vela y salpicaduras de pigmento junto al mortero.
            drawOval(tn(Offset(w * 0.6f, sup)).s(0.2f), Offset(w * 0.565f, sup + (frente - sup) * 0.4f), Size(w * 0.05f, (frente - sup) * 0.4f), style = Stroke(0.9f * u))
            listOf(Offset(0.515f, 0.5f), Offset(0.535f, 0.74f), Offset(0.44f, 0.82f)).forEach { f ->
                val p = Offset(w * f.x, sup + (frente - sup) * f.y)
                drawOval(tn(p).c(CERA), Offset(p.x - w * 0.006f, p.y - 1.4f * u), Size(w * 0.012f, 2.8f * u))
            }
            listOf(Triple(0.33f, 0.8f, ULTRAMAR), Triple(0.31f, 0.55f, ULTRAMAR), Triple(0.445f, 0.5f, OCRE), Triple(0.63f, 0.8f, BERMELLON), Triple(0.345f, 0.92f, ULTRAMAR)).forEach { (fx, fy, col) ->
                val p = Offset(w * fx, sup + (frente - sup) * fy)
                drawOval(tn(p).c(col).copy(alpha = 0.7f), Offset(p.x - w * 0.005f, p.y - 1.1f * u), Size(w * 0.01f, 2.2f * u))
            }
            for (k in 0 until 6) {
                val x = w * (0.06f + 0.86f * azar(6901, k))
                drawLine(tn(Offset(x, sup)).b(0.3f), Offset(x, sup + (frente - sup) * (0.3f + 0.4f * azar(6902, k))), Offset(x + w * 0.03f, sup + (frente - sup) * (0.4f + 0.3f * azar(6903, k))), 0.6f * u)
            }
        }
        // El canto de la cornisa: dos planos (el de arriba recibe la luz) y su vuelo a la derecha, en sombra.
        val m1 = frente + (corn - frente) * 0.5f
        val tramo = (xR - xL) / 10f
        for (k in 0 until 10) {
            val x0 = xL + tramo * k
            val tk = tn(Offset(x0 + tramo / 2, frente))
            drawRect(tk.c(NOGAL_LUZ), Offset(x0, frente), Size(tramo + 0.6f, m1 - frente))
            val mete = w * 0.006f
            val xa = if (k == 0) x0 + mete else x0; val xb = if (k == 9) x0 + tramo - mete else x0 + tramo + 0.6f
            drawRect(tk.c(mezcla(NOGAL, NOGAL_LUZ, 0.3f)), Offset(xa, m1), Size(xb - xa, corn - m1))
            drawRect(tk.b(0.7f), Offset(x0, frente), Size(tramo + 0.6f, 1.2f * u))
            drawRect(tk.s(0.3f), Offset(xa, m1), Size(xb - xa, 1f * u))
            drawRect(tk.b(0.3f), Offset(xa, m1 + 1f * u), Size(xb - xa, 0.8f * u))
            drawRect(tk.s(0.4f), Offset(xa, corn - 1.2f * u), Size(xb - xa, 1.2f * u))
        }
        vetas(Rect(xL, frente, xR, corn), (if (o) NOCHE else NOGAL_OSC).copy(alpha = 0.3f), 6910, 0.5f, grosor = 0.5f, ondulacion = 0.4f)
        val tr = tn(Offset(xR, frente))
        drawRect(tr.s(0.3f), Offset(xR - w * 0.012f, frente), Size(w * 0.012f, corn - frente))
        // Una esquina golpeada, arriba a la izquierda: la madera viva.
        val tg = tn(Offset(xL, frente))
        drawPath(poligono(xL, frente, xL + w * 0.03f, frente, xL + w * 0.008f, m1 + (corn - m1) * 0.3f, xL, m1), tg.c(mezcla(NOGAL_LUZ, PERAL, 0.55f)))
    }

    // ---------------------------------------------------------------- lo de encima

    /** Una cara del dodecaedro ya girado y proyectado (radio 1): sus cinco puntos y hacia dónde mira. */
    private class Cara(val pts: List<Offset>, val nx: Float, val ny: Float, val nz: Float)

    /**
     * Las caras que se ven de un dodecaedro regular apoyado en una de ellas, girado un poco sobre su eje y visto algo desde
     * arriba. Se calculan una vez: los 20 vértices, las 12 caras (los cinco vértices más cercanos a cada normal) y el giro.
     */
    private val CARAS: List<Cara> by lazy {
        val phi = 1.618034f; val ip = 1f / phi
        val vs = ArrayList<FloatArray>()
        val ns = ArrayList<FloatArray>()
        for (a in intArrayOf(-1, 1)) for (b in intArrayOf(-1, 1)) {
            for (c in intArrayOf(-1, 1)) vs += floatArrayOf(a.toFloat(), b.toFloat(), c.toFloat())
            vs += floatArrayOf(0f, a * ip, b * phi); vs += floatArrayOf(a * ip, b * phi, 0f); vs += floatArrayOf(a * phi, 0f, b * ip)
            ns += floatArrayOf(0f, a * phi, b.toFloat()); ns += floatArrayOf(a.toFloat(), 0f, b * phi); ns += floatArrayOf(a * phi, b.toFloat(), 0f)
        }
        val th = atan2(1f, phi)
        val psi = 0.4f; val beta = 0.3f
        fun gira(v: FloatArray): FloatArray {
            val y1 = v[1] * cos(th) + v[2] * sin(th); val z1 = -v[1] * sin(th) + v[2] * cos(th)
            val x2 = v[0] * cos(psi) + z1 * sin(psi); val z2 = -v[0] * sin(psi) + z1 * cos(psi)
            val y3 = y1 * cos(beta) + z2 * sin(beta); val z3 = -y1 * sin(beta) + z2 * cos(beta)
            return floatArrayOf(x2, y3, z3)
        }
        val r3 = sqrt(3f)
        ns.mapNotNull { n ->
            val g = gira(n)
            val largo = sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2])
            if (g[2] / largo < 0.03f) null else {
                val pts = vs.filter { v -> v[0] * n[0] + v[1] * n[1] + v[2] * n[2] > 2.5f }.map { v -> gira(v).let { Offset(it[0] / r3, it[1] / r3) } }
                val cx = pts.map { it.x }.average().toFloat(); val cy = pts.map { it.y }.average().toFloat()
                Cara(pts.sortedBy { atan2(it.y - cy, it.x - cx) }, g[0] / largo, g[1] / largo, g[2] / largo)
            }
        }
    }

    /** Lo más bajo del dodecaedro proyectado (radio 1): donde pisa. */
    private val PIE_DODECAEDRO: Float by lazy { CARAS.maxOf { c -> c.pts.maxOf { it.y } } }

    /**
     * El dodecaedro de madera, el modelo con que se estudian los cuerpos regulares: cada cara con su tono según le da la luz
     * (de día, de arriba a la izquierda; de noche, de la vela, a su derecha), su veta, las aristas sobadas y una cara con las
     * marcas de compás de quien la trazó.
     */
    private fun DrawScope.dodecaedro(pie: Offset, radio: Float, sup: Float) {
        val u = density
        val c = Offset(pie.x, pie.y - PIE_DODECAEDRO * radio)
        val t = tn(c)
        val der = luzDerecha(c.x)
        val lx: Float; val ly: Float; val lz: Float
        if (der) { lx = 0.76f; ly = -0.27f; lz = 0.59f } else { lx = -0.5f; ly = -0.68f; lz = 0.54f }
        val madera = mezcla(PERAL, CEREZO, 0.5f)
        val caras = CARAS.map { k -> k to Path().apply { k.pts.forEachIndexed { i, p -> val q = c + p * radio; if (i == 0) m(q) else l(q) }; close() } }
        val silueta = Path().apply { caras.forEach { addPath(it.second) } }
        sombraEnPared(silueta, c, sup, 0.24f, 1.2f)
        contacto(Offset(pie.x + radio * 0.12f, pie.y - 1f * u), radio * 2.1f, 6f * u, 0.42f)
        caras.forEachIndexed { i, (k, p) ->
            val br = (k.nx * lx + k.ny * ly + k.nz * lz).coerceIn(0f, 1f).pow(0.8f)
            drawPath(p, t.c(mezcla(mezcla(madera, NOGAL_OSC, 0.68f), mezcla(madera, LUZ_MUEBLE, 0.32f), br)))
            clipPath(p) {
                val caja = Rect(c.x + k.pts.minOf { it.x } * radio, c.y + k.pts.minOf { it.y } * radio, c.x + k.pts.maxOf { it.x } * radio, c.y + k.pts.maxOf { it.y } * radio)
                vetas(caja, (if (t.o) NOCHE else NOGAL_OSC).copy(alpha = 0.3f), 7000 + i, 0.45f, vertical = i % 2 == 0, grosor = 0.6f, ondulacion = 0.7f)
                if (br > 0.6f) pinceladas(caja, if (t.o) LLAMA else LUZ_MUEBLE, 7010 + i, 4, u, if (t.o) 0.1f * t.k else 0.12f)
            }
        }
        // Las aristas: sobadas, cogen la luz; la cara de arriba lleva las marcas del compás.
        caras.forEach { (k, p) ->
            val br = (k.nx * lx + k.ny * ly + k.nz * lz).coerceIn(0f, 1f)
            drawPath(p, t.b(0.18f + 0.4f * br), style = Stroke(1f * u, join = StrokeJoin.Round))
        }
        CARAS.maxByOrNull { -it.ny }?.let { k ->
            val cc = c + Offset(k.pts.map { it.x }.average().toFloat(), k.pts.map { it.y }.average().toFloat()) * radio
            val ry = (k.pts.maxOf { it.y } - k.pts.minOf { it.y }) * radio / 2; val rx = (k.pts.maxOf { it.x } - k.pts.minOf { it.x }) * radio / 2
            drawOval(t.c(NOGAL_OSC).copy(alpha = 0.45f), Offset(cc.x - rx * 0.45f, cc.y - ry * 0.45f), Size(rx * 0.9f, ry * 0.9f), style = Stroke(0.6f * u))
            drawCircle(t.c(NOGAL_OSC).copy(alpha = 0.7f), 0.9f * u, cc)
        }
    }

    /**
     * El candelero de latón: el pie de campana, el fuste con sus dos nudos, el platillo que recoge la cera y el cubo; en él,
     * la vela a medio gastar, con sus chorreones. De noche está encendida: la llama, la cera que se trasluce arriba y el
     * brillo en el latón.
     */
    private fun DrawScope.candelero(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val o = esNoche
        val x = pie.x; val y = pie.y
        val t = tn(Offset(x, y - w * 0.06f))
        val plato = y - w * 0.108f
        val campana = Path().apply {
            moveTo(x - w * 0.05f, y); lineTo(x - w * 0.05f, y - w * 0.006f)
            cubicTo(x - w * 0.046f, y - w * 0.03f, x - w * 0.013f, y - w * 0.028f, x - w * 0.01f, y - w * 0.046f)
            lineTo(x + w * 0.01f, y - w * 0.046f)
            cubicTo(x + w * 0.013f, y - w * 0.028f, x + w * 0.046f, y - w * 0.03f, x + w * 0.05f, y - w * 0.006f)
            lineTo(x + w * 0.05f, y); close()
        }
        val fuste = Rect(x - w * 0.0065f, plato + w * 0.014f, x + w * 0.0065f, y - w * 0.044f)
        val nudo1 = Rect(x - w * 0.017f, y - w * 0.07f, x + w * 0.017f, y - w * 0.048f)
        val nudo2 = Rect(x - w * 0.012f, y - w * 0.093f, x + w * 0.012f, y - w * 0.079f)
        val platillo = Path().apply {
            moveTo(x - w * 0.044f, plato); lineTo(x + w * 0.044f, plato)
            cubicTo(x + w * 0.036f, plato + w * 0.012f, x + w * 0.012f, plato + w * 0.012f, x + w * 0.008f, plato + w * 0.02f)
            lineTo(x - w * 0.008f, plato + w * 0.02f)
            cubicTo(x - w * 0.012f, plato + w * 0.012f, x - w * 0.036f, plato + w * 0.012f, x - w * 0.044f, plato)
            close()
        }
        val cubo = Rect(x - w * 0.0125f, plato - w * 0.02f, x + w * 0.0125f, plato)
        val cima = LUZ.y * w + w * 0.02f
        val vela = Rect(x - w * 0.0095f, cima, x + w * 0.0095f, plato - w * 0.017f)
        if (!o) sombraEnPared(Path().apply { addPath(campana); addRect(fuste); addOval(nudo1); addOval(nudo2); addPath(platillo); addRect(cubo); addRect(vela) }, Offset(x, y - w * 0.1f), sup, 0.2f)
        contacto(Offset(x + w * 0.006f, y), w * 0.12f, 4.5f * u, 0.42f)
        val laton = t.c(LATON)
        // El pie de campana, con sus molduras.
        drawPath(campana, laton)
        clipPath(campana) {
            cilindro(Rect(x - w * 0.05f, y - w * 0.05f, x + w * 0.05f, y), t, 1.5f)
            drawRect(t.s(0.3f), Offset(x - w * 0.05f, y - w * 0.008f), Size(w * 0.1f, 1f * u))
            drawRect(t.b(0.5f), Offset(x - w * 0.05f, y - w * 0.008f + 1f * u), Size(w * 0.1f, 0.9f * u))
            drawRect(t.s(0.25f), Offset(x - w * 0.05f, y - w * 0.03f), Size(w * 0.1f, 0.9f * u))
            if (o) drawOval(LLAMA.copy(alpha = 0.3f), Offset(x - w * 0.03f, y - w * 0.036f), Size(w * 0.06f, w * 0.016f))
        }
        drawRect(laton, fuste.topLeft, fuste.size)
        clipRect(fuste.left, fuste.top, fuste.right, fuste.bottom) { cilindro(fuste, t, 1.5f) }
        listOf(nudo1, nudo2).forEach { n ->
            drawOval(laton, n.topLeft, n.size)
            clipPath(Path().apply { addOval(n) }) {
                cilindro(n, t, 1.5f)
                drawRect(t.s(0.22f), Offset(n.left, n.center.y + n.height * 0.15f), Size(n.width, n.height))
                if (o) drawRect(LLAMA.copy(alpha = 0.35f), n.topLeft, Size(n.width, n.height * 0.25f))
            }
            destello(Offset(n.left + n.width * 0.28f, n.top + n.height * 0.35f), 1.1f * u, t)
        }
        // El platillo y el cubo.
        drawPath(platillo, laton)
        clipPath(platillo) {
            cilindro(Rect(x - w * 0.044f, plato, x + w * 0.044f, plato + w * 0.02f), t, 1.4f)
            drawRect(t.s(0.25f), Offset(x - w * 0.044f, plato + w * 0.006f), Size(w * 0.088f, w * 0.02f))
        }
        drawOval(t.c(mezcla(LATON, NOGAL_OSC, 0.45f)), Offset(x - w * 0.044f, plato - w * 0.005f), Size(w * 0.088f, w * 0.01f))
        drawOval(if (o) LLAMA.copy(alpha = 0.8f) else luzMueble(0.6f), Offset(x - w * 0.044f, plato - w * 0.005f), Size(w * 0.088f, w * 0.01f), style = Stroke(1f * u))
        // La cera que cayó en el platillo.
        drawOval(t.c(CERA), Offset(x - w * 0.026f, plato - w * 0.004f), Size(w * 0.03f, w * 0.006f))
        drawRect(laton, cubo.topLeft, cubo.size)
        clipRect(cubo.left, cubo.top, cubo.right, cubo.bottom) { cilindro(cubo, t, 1.5f) }
        drawRect(t.b(0.6f), Offset(cubo.left - 1f * u, cubo.top), Size(cubo.width + 2f * u, 1.3f * u))
        drawRect(t.s(0.3f), Offset(cubo.left, cubo.bottom - 1.2f * u), Size(cubo.width, 1.2f * u))
        // La vela: la cera, su luz y su sombra, los chorreones y el pabilo.
        val tv = tn(vela.center)
        drawRect(tv.c(CERA), vela.topLeft, vela.size)
        clipRect(vela.left, vela.top, vela.right, vela.bottom) {
            cilindro(vela, tv, 0.9f)
            if (o) {
                drawRect(LLAMA.copy(alpha = 0.55f), vela.topLeft, Size(vela.width, w * 0.012f))
                drawRect(LLAMA.copy(alpha = 0.3f), Offset(vela.left, vela.top + w * 0.012f), Size(vela.width, w * 0.016f))
                drawRect(LLAMA.copy(alpha = 0.14f), Offset(vela.left, vela.top + w * 0.028f), Size(vela.width, w * 0.02f))
            }
        }
        listOf(Triple(-0.0095f, 0.03f, 0.004f), Triple(-0.004f, 0.05f, 0.0035f), Triple(0.0055f, 0.022f, 0.003f)).forEach { (dx, largo, a) ->
            drawLine(tv.c(mezcla(CERA, CARTELLINO, 0.5f)), Offset(x + w * dx, vela.top + 1f * u), Offset(x + w * dx, vela.top + w * largo), w * a * 2f, StrokeCap.Round)
            drawLine(tv.s(0.14f), Offset(x + w * (dx + a), vela.top + w * 0.004f), Offset(x + w * (dx + a), vela.top + w * largo), 0.7f * u)
        }
        drawOval(tv.c(mezcla(CERA, OCRE, 0.2f)), Offset(vela.left, vela.top - w * 0.003f), Size(vela.width, w * 0.006f))
        val pabilo = Offset(x, vela.top - w * 0.008f)
        drawLine(Color(0xFF241A14), Offset(x, vela.top), pabilo, 1.1f * u, StrokeCap.Round)
        if (o) {
            // La llama: el halo, la lágrima, su corazón claro y el azul del arranque.
            val yb = pabilo.y + w * 0.004f
            drawCircle(LLAMA.copy(alpha = 0.3f), w * 0.034f, Offset(x, yb - w * 0.02f))
            drawPath(Path().apply {
                moveTo(x, yb)
                cubicTo(x + w * 0.011f, yb - w * 0.006f, x + w * 0.007f, yb - w * 0.026f, x + w * 0.001f, yb - w * 0.044f)
                cubicTo(x - w * 0.006f, yb - w * 0.026f, x - w * 0.011f, yb - w * 0.006f, x, yb)
                close()
            }, LLAMA)
            drawOval(Color(0xFFFFF6DC), Offset(x - w * 0.0038f, yb - w * 0.026f), Size(w * 0.0076f, w * 0.022f))
            drawOval(ULTRAMAR.copy(alpha = 0.55f), Offset(x - w * 0.0035f, yb - w * 0.007f), Size(w * 0.007f, w * 0.006f))
        } else drawLine(Color(0xFF241A14), pabilo, pabilo + Offset(1.5f * u, -1.5f * u), 0.9f * u, StrokeCap.Round)
    }

    /**
     * El albarelo de mayólica: el cuerpo de cintura entrante con su esmalte blanco, las fajas azules, el ramo de hojas en
     * azul y ocre, el vidriado saltado en el borde; dentro, los pinceles, de mango de madera o de pluma, cada uno con su
     * atadura y su pelo, alguno teñido del último color.
     */
    private fun DrawScope.albarelo(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val x = pie.x; val y = pie.y
        val alto = w * 0.125f
        val boca = y - alto
        val t = tn(Offset(x, y - alto * 0.5f))
        val der = luzDerecha(x)
        val cuerpo = Path().apply {
            moveTo(x - w * 0.034f, boca); lineTo(x + w * 0.034f, boca); lineTo(x + w * 0.034f, boca + w * 0.007f)
            cubicTo(x + w * 0.027f, boca + w * 0.012f, x + w * 0.044f, boca + w * 0.018f, x + w * 0.044f, boca + w * 0.03f)
            cubicTo(x + w * 0.034f, boca + alto * 0.45f, x + w * 0.034f, boca + alto * 0.6f, x + w * 0.044f, y - w * 0.024f)
            cubicTo(x + w * 0.044f, y - w * 0.012f, x + w * 0.037f, y - w * 0.008f, x + w * 0.035f, y)
            lineTo(x - w * 0.035f, y)
            cubicTo(x - w * 0.037f, y - w * 0.008f, x - w * 0.044f, y - w * 0.012f, x - w * 0.044f, y - w * 0.024f)
            cubicTo(x - w * 0.034f, boca + alto * 0.6f, x - w * 0.034f, boca + alto * 0.45f, x - w * 0.044f, boca + w * 0.03f)
            cubicTo(x - w * 0.044f, boca + w * 0.018f, x - w * 0.027f, boca + w * 0.012f, x - w * 0.034f, boca + w * 0.007f)
            close()
        }
        // Los pinceles: dónde salen, hacia dónde se inclinan, cuánto miden y de qué está teñido su pelo.
        class Pincel(val dx: Float, val giro: Float, val largo: Float, val pelo: Color?, val mango: Color, val grueso: Float)
        val pinceles = listOf(
            Pincel(-0.022f, -25f, 0.118f, ULTRAMAR, NOGAL_LUZ, 1.9f), Pincel(-0.013f, -12f, 0.15f, null, CARTELLINO, 1.5f),
            Pincel(-0.004f, -4f, 0.108f, BERMELLON, PERAL, 2.2f), Pincel(0.005f, 5f, 0.162f, null, NOGAL_LUZ, 1.6f),
            Pincel(0.014f, 14f, 0.126f, OCRE, CARTELLINO, 1.5f), Pincel(0.023f, 28f, 0.1f, null, PERAL, 2.4f),
        )
        fun punta(p: Pincel): Pair<Offset, Offset> {
            val a = Math.toRadians(p.giro.toDouble() - 90.0)
            val dir = Offset(cos(a).toFloat(), sin(a).toFloat())
            return Offset(x + w * p.dx, boca + w * 0.002f) to dir
        }
        val silueta = Path().apply {
            addPath(cuerpo)
            pinceles.forEach { p -> val (p0, dir) = punta(p); val n = Offset(-dir.y, dir.x) * (1.6f * u); val p1 = p0 + dir * (w * (p.largo + 0.02f)); m(p0 - n); l(p0 + n); l(p1 + n); l(p1 - n); close() }
        }
        sombraEnPared(silueta, Offset(x, boca), sup, 0.2f)
        contacto(Offset(x + w * 0.006f, y), w * 0.1f, 4f * u, 0.42f)
        drawPath(cuerpo, t.c(MAYOLICA))
        clipPath(cuerpo) {
            // Las fajas y el ramo.
            val azul = t.c(ULTRAMAR); val ocre = t.c(OCRE)
            listOf(boca + w * 0.022f, y - w * 0.022f).forEach { fy ->
                drawRect(azul, Offset(x - w * 0.05f, fy), Size(w * 0.1f, w * 0.0075f))
                drawRect(ocre, Offset(x - w * 0.05f, fy + (if (fy < y - alto / 2) w * 0.0105f else -w * 0.0055f)), Size(w * 0.1f, w * 0.0028f))
            }
            val cy = boca + alto * 0.54f
            val tallo = Path().apply { moveTo(x - w * 0.03f, cy + w * 0.016f); cubicTo(x - w * 0.012f, cy - w * 0.02f, x + w * 0.01f, cy + w * 0.022f, x + w * 0.03f, cy - w * 0.014f) }
            drawPath(tallo, azul, style = Stroke(1.2f * u, cap = StrokeCap.Round))
            listOf(Triple(-0.018f, -0.012f, -50f), Triple(-0.004f, 0.012f, 40f), Triple(0.012f, -0.006f, -35f), Triple(0.022f, 0.012f, 60f)).forEachIndexed { i, (dx, dy, g) ->
                val c = Offset(x + w * dx, cy + w * dy)
                rotate(g, c) { drawOval(if (i % 2 == 0) azul else ocre, Offset(c.x - w * 0.004f, c.y - w * 0.011f), Size(w * 0.008f, w * 0.022f)) }
            }
            listOf(-0.027f, 0.0f, 0.027f).forEach { dx -> drawCircle(ocre, w * 0.0028f, Offset(x + w * dx, cy - w * 0.024f)); drawCircle(azul, w * 0.0024f, Offset(x + w * dx, cy + w * 0.028f)) }
            cilindro(Rect(x - w * 0.044f, boca, x + w * 0.044f, y), t, 1.1f, der)
            // El vidriado saltado: el barro que asoma en el pie y en el labio.
            drawOval(t.c(BARRO), Offset(x - w * 0.03f, y - w * 0.006f), Size(w * 0.016f, w * 0.008f))
            drawOval(t.c(BARRO), Offset(x + w * 0.012f, boca - w * 0.002f), Size(w * 0.012f, w * 0.006f))
            drawRect(t.s(0.25f), Offset(x - w * 0.05f, boca + w * 0.007f), Size(w * 0.1f, 1.2f * u))
        }
        destello(Offset(x + (if (der) 0.026f else -0.026f) * w, boca + w * 0.045f), 1.4f * u, t)
        // La boca, los pinceles que salen de ella y el labio de delante.
        drawOval(t.c(mezcla(NOGAL_OSC, ROBLE, 0.5f)), Offset(x - w * 0.034f, boca - w * 0.007f), Size(w * 0.068f, w * 0.014f))
        pinceles.forEach { p ->
            val (p0, dir) = punta(p)
            val tp = tn(p0 + dir * (w * p.largo))
            val n = Offset(-dir.y, dir.x)
            val fin = p0 + dir * (w * p.largo)
            drawLine(tp.c(p.mango), p0, fin, p.grueso * u)
            drawLine(tp.s(0.25f), p0 + n * (p.grueso * 0.3f * u), fin + n * (p.grueso * 0.3f * u), 0.6f * u)
            // La atadura de hilo y el pelo.
            drawLine(tp.c(mezcla(LINO, NOGAL, 0.35f)), p0 + dir * (w * (p.largo - 0.014f)), fin, (p.grueso + 1.2f) * u)
            val ancho = (p.grueso + 1.6f) * u * 0.5f
            val largoPelo = w * (0.016f + 0.004f * p.grueso)
            val pelo = Path().apply {
                m(fin - n * (ancho * 0.7f)); c3(fin - n * (ancho * 1.3f) + dir * (largoPelo * 0.3f), fin - n * (ancho * 0.7f) + dir * (largoPelo * 0.7f), fin + dir * largoPelo)
                c3(fin + n * (ancho * 0.7f) + dir * (largoPelo * 0.7f), fin + n * (ancho * 1.3f) + dir * (largoPelo * 0.3f), fin + n * (ancho * 0.7f)); close()
            }
            drawPath(pelo, tp.c(mezcla(NOGAL_OSC, PERAL, 0.25f)))
            if (p.pelo != null) clipPath(pelo) { drawCircle(tp.c(p.pelo), largoPelo * 0.62f, fin + dir * largoPelo) }
        }
        drawArc(t.c(MAYOLICA), 0f, 180f, false, Offset(x - w * 0.034f, boca - w * 0.007f), Size(w * 0.068f, w * 0.014f), style = Stroke(1.8f * u))
        drawArc(t.b(0.6f), 180f, 180f, false, Offset(x - w * 0.034f, boca - w * 0.007f), Size(w * 0.068f, w * 0.014f), style = Stroke(1f * u))
    }

    /** Un cartón enrollado, tumbado: el papel con su luz arriba y su sombra abajo, el canto de la hoja, la boca en espiral y su cinta con el lazo. */
    private fun DrawScope.rollo(x0: Float, abajo: Float, d: Float, giro: Float, papel: Color, cinta: Color?, semilla: Int) {
        val u = density
        val w = anchoW
        rotate(giro, Offset(x0, abajo)) {
            val r = Rect(x0, abajo - d, w * 1.06f, abajo)
            val t = tn(Offset(x0 + w * 0.08f, abajo - d / 2))
            drawRect(t.c(papel), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) {
                tumbado(r, t, 1.25f)
                // El canto de la hoja de fuera, que no llega a cerrar, y unas arrugas.
                drawLine(t.s(0.3f), Offset(r.left, r.top + d * 0.36f), Offset(r.right, r.top + d * 0.33f), 0.8f * u)
                drawLine(t.b(0.4f), Offset(r.left, r.top + d * 0.36f + 1f * u), Offset(r.right, r.top + d * 0.33f + 1f * u), 0.7f * u)
                for (k in 0 until 3) { val xx = r.left + w * (0.03f + 0.07f * k + 0.03f * azar(semilla, k)); drawLine(t.s(0.12f), Offset(xx, r.top + d * 0.4f), Offset(xx + w * 0.01f, r.bottom), 0.7f * u) }
            }
            // La boca: las vueltas del papel.
            val boca = Rect(x0 - d * 0.2f, abajo - d, x0 + d * 0.2f, abajo)
            drawOval(t.c(mezcla(papel, NOGAL, 0.2f)), boca.topLeft, boca.size)
            drawOval(t.b(0.5f), boca.topLeft, boca.size, style = Stroke(0.9f * u))
            listOf(0.72f, 0.46f).forEach { f -> drawOval(t.s(0.35f), Offset(boca.center.x - boca.width * f / 2, boca.center.y - boca.height * f / 2), Size(boca.width * f, boca.height * f), style = Stroke(0.8f * u)) }
            drawOval(t.c(NOGAL_OSC), Offset(boca.center.x - boca.width * 0.12f, boca.center.y - boca.height * 0.13f), Size(boca.width * 0.24f, boca.height * 0.26f))
            if (cinta != null) {
                // La cinta, con su lazo arriba y sus dos cabos.
                val cx = x0 + w * 0.062f
                drawRect(t.s(0.25f), Offset(cx + w * 0.012f, r.top), Size(1.5f * u, d))
                drawRect(t.c(cinta), Offset(cx, r.top - 0.6f * u), Size(w * 0.012f, d + 1.2f * u))
                drawRect(t.b(0.4f), Offset(cx, r.top - 0.6f * u), Size(w * 0.012f, d * 0.22f))
                drawRect(t.s(0.3f), Offset(cx, r.top + d * 0.65f), Size(w * 0.012f, d * 0.35f + 0.6f * u))
                val nudo = Offset(cx + w * 0.006f, r.top + d * 0.12f)
                listOf(-1f, 1f).forEach { s ->
                    drawOval(t.c(cinta), Offset(nudo.x + (if (s < 0) -w * 0.022f else 0f), nudo.y - w * 0.014f), Size(w * 0.022f, w * 0.016f), style = Stroke(2f * u))
                    val cabo = Path().apply { moveTo(nudo.x, nudo.y); cubicTo(nudo.x + s * w * 0.012f, nudo.y + d * 0.2f, nudo.x + s * w * 0.008f, nudo.y + d * 0.45f, nudo.x + s * w * 0.02f, nudo.y + d * 0.68f) }
                    drawPath(cabo, t.s(0.25f), style = Stroke(3f * u))
                    drawPath(cabo, t.c(mezcla(cinta, NOGAL_OSC, if (s > 0) 0.25f else 0f)), style = Stroke(2.2f * u, cap = StrokeCap.Round))
                }
                drawCircle(t.c(mezcla(cinta, NOGAL_OSC, 0.2f)), 2.2f * u, nudo)
            }
        }
    }

    /** Los cartones enrollados, apilados y atados con cinta, que se salen por el borde derecho: dos abajo y uno encima. */
    private fun DrawScope.rollos(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val pila = poligono(pie.x - w * 0.01f, pie.y, pie.x - w * 0.005f, pie.y - w * 0.05f, pie.x + w * 0.045f, pie.y - w * 0.092f, w * 1.05f, pie.y - w * 0.1f, w * 1.05f, pie.y)
        sombraEnPared(pila, Offset(pie.x + w * 0.1f, pie.y - w * 0.05f), sup, 0.2f, 0.9f)
        contacto(Offset(pie.x + w * 0.14f, pie.y + w * 0.008f), w * 0.3f, 5f * u, 0.4f)
        rollo(pie.x, pie.y, w * 0.052f, -1.5f, PAPEL, BERMELLON, 7101)
        rollo(pie.x + w * 0.05f, pie.y - w * 0.043f, w * 0.05f, 2.5f, mezcla(PAPEL_TINTO, PAPEL, 0.3f), null, 7102)
        rollo(pie.x + w * 0.028f, pie.y + w * 0.014f, w * 0.044f, 0.8f, mezcla(CARTELLINO, PAPEL, 0.4f), ULTRAMAR, 7103)
    }

    /**
     * El mortero de pórfido, con sus pintas, y su mano de madera apoyada en el borde; dentro, el azul de ultramar a medio
     * moler, que mancha la mano.
     */
    private fun DrawScope.mortero(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val x = pie.x; val y = pie.y
        val alto = w * 0.07f
        val boca = y - alto
        val t = tn(Offset(x, y - alto / 2))
        val der = luzDerecha(x)
        val cuerpo = Path().apply {
            moveTo(x - w * 0.056f, boca)
            cubicTo(x - w * 0.056f, boca + alto * 0.6f, x - w * 0.04f, y - w * 0.012f, x - w * 0.034f, y - w * 0.01f)
            lineTo(x - w * 0.039f, y); lineTo(x + w * 0.039f, y); lineTo(x + w * 0.034f, y - w * 0.01f)
            cubicTo(x + w * 0.04f, y - w * 0.012f, x + w * 0.056f, boca + alto * 0.6f, x + w * 0.056f, boca)
            close()
        }
        contacto(Offset(x + w * 0.008f, y), w * 0.12f, 4.5f * u, 0.42f)
        drawPath(cuerpo, t.c(PORFIDO))
        clipPath(cuerpo) {
            // Las pintas del pórfido: claras y oscuras.
            for (k in 0 until 26) {
                val p = Offset(x - w * 0.056f + w * 0.112f * azar(7201, k), boca + alto * azar(7202, k))
                drawCircle(t.c(if (k % 3 == 0) mezcla(PORFIDO, EBANO, 0.45f) else mezcla(PORFIDO, CARTELLINO, 0.5f)), (0.7f + 1.1f * azar(7203, k)) * u, p)
            }
            cilindro(Rect(x - w * 0.056f, boca, x + w * 0.056f, y), t, 1.3f, der)
            drawRect(t.s(0.25f), Offset(x - w * 0.06f, y - w * 0.011f), Size(w * 0.12f, 1f * u))
        }
        // La boca: el labio, lo hondo y el azul.
        val labio = Rect(x - w * 0.056f, boca - w * 0.011f, x + w * 0.056f, boca + w * 0.011f)
        drawOval(t.c(mezcla(PORFIDO, CARTELLINO, 0.22f)), labio.topLeft, labio.size)
        val hueco = Rect(x - w * 0.046f, boca - w * 0.008f, x + w * 0.046f, boca + w * 0.008f)
        drawOval(t.c(mezcla(PORFIDO, EBANO, 0.6f)), hueco.topLeft, hueco.size)
        clipPath(Path().apply { addOval(hueco) }) {
            drawOval(t.c(ULTRAMAR), Offset(x - w * 0.036f, boca - w * 0.001f), Size(w * 0.072f, w * 0.016f))
            drawOval(t.c(mezcla(ULTRAMAR, MAYOLICA, 0.3f)), Offset(x - w * 0.03f, boca + w * 0.001f), Size(w * 0.03f, w * 0.006f))
        }
        // La mano del mortero: sale hacia arriba a la derecha.
        val a = Offset(x - w * 0.006f, boca + w * 0.003f); val b = Offset(x + w * 0.064f, boca - w * 0.078f)
        val dir = (b - a) / (b - a).getDistance(); val n = Offset(-dir.y, dir.x)
        val tm = tn(b)
        val mano = Path().apply { m(a - n * (w * 0.01f)); l(b - n * (w * 0.007f)); c3(b - n * (w * 0.008f) + dir * (w * 0.012f), b + n * (w * 0.008f) + dir * (w * 0.012f), b + n * (w * 0.007f)); l(a + n * (w * 0.01f)); close() }
        if (!esNoche) sombraEnPared(mano, b, sup, 0.16f, 0.7f)
        drawPath(mano, tm.c(mezcla(PERAL, NOGAL_LUZ, 0.5f)))
        clipPath(mano) {
            drawLine(tm.b(0.45f), a - n * (w * 0.005f), b - n * (w * 0.004f), w * 0.004f)
            drawLine(tm.s(0.3f), a + n * (w * 0.006f), b + n * (w * 0.005f), w * 0.006f)
            drawCircle(tm.c(ULTRAMAR).copy(alpha = 0.75f), w * 0.016f, a)
        }
        drawArc(t.c(mezcla(PORFIDO, CARTELLINO, 0.3f)), 0f, 180f, false, labio.topLeft, labio.size, style = Stroke(1.6f * u))
        if (t.o) drawArc(LLAMA.copy(alpha = (0.7f * t.k).coerceAtMost(0.6f)), 200f, 140f, false, labio.topLeft, labio.size, style = Stroke(1.1f * u))
        else drawArc(luzMueble(0.6f), 180f, 110f, false, labio.topLeft, labio.size, style = Stroke(1f * u))
    }

    /** Una concha con su pigmento: la valva con sus costillas y su borde ondulado, y dentro el color molido, con su montón. */
    private fun DrawScope.concha(c: Offset, a: Float, color: Color, giro: Float) {
        val u = density
        val t = tn(c)
        contacto(Offset(c.x + a * 0.06f, c.y + a * 0.2f), a * 1.15f, a * 0.14f, 0.38f)
        rotate(giro, c) {
            val valva = Rect(c.x - a / 2, c.y - a * 0.22f, c.x + a / 2, c.y + a * 0.22f)
            val nacar = mezcla(CARTELLINO, PERAL, 0.3f)
            // El borde ondulado, a la derecha, y la charnela, a la izquierda.
            drawPath(poligono(valva.left - a * 0.06f, c.y - a * 0.05f, valva.left + a * 0.1f, c.y - a * 0.12f, valva.left + a * 0.1f, c.y + a * 0.12f, valva.left - a * 0.06f, c.y + a * 0.05f), t.c(mezcla(nacar, NOGAL, 0.25f)))
            drawOval(t.c(mezcla(ULTRAMAR, EBANO, 0.6f)), Offset(valva.left - a * 0.04f, valva.top - a * 0.04f), Size(valva.width + a * 0.08f, valva.height + a * 0.08f))
            drawOval(t.c(nacar), valva.topLeft, valva.size)
            for (k in 0 until 7) {
                val ang = Math.toRadians(-66.0 + 22.0 * k)
                drawLine(t.s(0.22f), Offset(valva.left + a * 0.04f, c.y), Offset(c.x + a * 0.5f * cos(ang).toFloat(), c.y + a * 0.22f * sin(ang).toFloat()), 0.6f * u)
            }
            drawArc(t.b(0.7f), 180f, 100f, false, valva.topLeft, valva.size, style = Stroke(1f * u))
            drawArc(t.s(0.3f), 10f, 120f, false, valva.topLeft, valva.size, style = Stroke(1f * u))
            // El pigmento.
            val pig = Rect(c.x - a * 0.3f, c.y - a * 0.13f, c.x + a * 0.36f, c.y + a * 0.15f)
            drawOval(t.c(mezcla(color, NOGAL_OSC, 0.25f)), pig.topLeft, pig.size)
            drawOval(t.c(color), Offset(pig.left, pig.top - a * 0.03f), Size(pig.width * 0.9f, pig.height * 0.9f))
            drawOval(t.c(mezcla(color, MAYOLICA, 0.3f)), Offset(pig.left + pig.width * 0.14f, pig.top - a * 0.02f), Size(pig.width * 0.4f, pig.height * 0.35f))
        }
    }

    /** Las conchas de los pigmentos, delante: tierra verde, ultramar, ocre y bermellón. */
    private fun DrawScope.conchas(pieF: Float, w: Float) {
        concha(Offset(w * 0.296f, pieF - w * 0.006f), w * 0.05f, TIERRA_VERDE, 8f)
        concha(Offset(w * 0.548f, pieF - w * 0.013f), w * 0.046f, OCRE, -10f)
        concha(Offset(w * 0.497f, pieF - w * 0.002f), w * 0.054f, ULTRAMAR, 6f)
        concha(Offset(w * 0.604f, pieF - w * 0.003f), w * 0.05f, BERMELLON, -4f)
    }

    /** El cuenco de barro con dos huevos: la yema para el temple. */
    private fun DrawScope.cuenco(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val x = pie.x; val y = pie.y
        val t = tn(Offset(x, y - w * 0.02f))
        val der = luzDerecha(x)
        val borde = y - w * 0.026f
        contacto(Offset(x + w * 0.006f, y), w * 0.075f, 3.5f * u, 0.4f)
        drawOval(t.c(mezcla(BARRO, NOGAL_OSC, 0.5f)), Offset(x - w * 0.038f, borde - w * 0.008f), Size(w * 0.076f, w * 0.016f))
        listOf(Triple(-0.012f, 0f, -18f), Triple(0.012f, -0.002f, 22f)).forEach { (dx, dy, g) ->
            val c = Offset(x + w * dx, borde - w * 0.006f + w * dy)
            rotate(g, c) {
                drawOval(t.c(mezcla(CARTELLINO, OCRE, 0.14f)), Offset(c.x - w * 0.013f, c.y - w * 0.017f), Size(w * 0.026f, w * 0.034f))
                drawOval(t.b(0.6f), Offset(c.x - w * 0.008f, c.y - w * 0.013f), Size(w * 0.008f, w * 0.012f))
                drawArc(t.s(0.25f), -30f, 130f, false, Offset(c.x - w * 0.013f, c.y - w * 0.017f), Size(w * 0.026f, w * 0.034f), style = Stroke(1.6f * u))
            }
        }
        val cuerpo = Path().apply {
            moveTo(x - w * 0.038f, borde)
            cubicTo(x - w * 0.036f, y - w * 0.004f, x - w * 0.018f, y, x - w * 0.013f, y)
            lineTo(x + w * 0.013f, y)
            cubicTo(x + w * 0.018f, y, x + w * 0.036f, y - w * 0.004f, x + w * 0.038f, borde)
            cubicTo(x + w * 0.02f, borde + w * 0.01f, x - w * 0.02f, borde + w * 0.01f, x - w * 0.038f, borde)
            close()
        }
        drawPath(cuerpo, t.c(BARRO))
        clipPath(cuerpo) {
            cilindro(Rect(x - w * 0.038f, borde, x + w * 0.038f, y), t, 1.2f, der)
            drawRect(t.c(MAYOLICA).copy(alpha = 0.5f), Offset(x - w * 0.04f, borde), Size(w * 0.08f, w * 0.014f))
            drawRect(t.s(0.2f), Offset(x - w * 0.04f, y - w * 0.006f), Size(w * 0.08f, w * 0.006f))
        }
        drawArc(t.b(0.6f), 20f, 140f, false, Offset(x - w * 0.038f, borde - w * 0.008f), Size(w * 0.076f, w * 0.016f), style = Stroke(1f * u))
    }

    /** El compás de puntas, de hierro, con su cabeza de latón, apoyado en los cartones. */
    private fun DrawScope.compas(pie: Offset) {
        val u = density
        val w = anchoW
        val cabeza = Offset(pie.x + w * 0.012f, pie.y - w * 0.118f)
        val t = tn(cabeza)
        val hierro = t.c(mezcla(ROBLE, CAL_OSC, 0.3f))
        listOf(Offset(pie.x - w * 0.024f, pie.y) to false, Offset(pie.x + w * 0.03f, pie.y - w * 0.006f) to true).forEach { (punta, sombra) ->
            val dir = (punta - cabeza) / (punta - cabeza).getDistance(); val n = Offset(-dir.y, dir.x)
            val pata = Path().apply { m(cabeza - n * (w * 0.0055f)); l(cabeza + n * (w * 0.0055f)); l(punta + n * (0.4f * u)); l(punta - n * (0.4f * u)); close() }
            translate(w * 0.006f, w * 0.004f) { drawPath(pata, t.s(0.25f)) }
            drawPath(pata, hierro)
            drawLine(if (sombra) t.s(0.3f) else t.b(0.5f), cabeza - n * (w * 0.003f), punta, 0.8f * u)
        }
        drawCircle(t.s(0.3f), w * 0.0125f, cabeza + Offset(1.2f * u, 1.5f * u))
        drawCircle(t.c(LATON), w * 0.012f, cabeza)
        drawCircle(t.s(0.25f), w * 0.012f, cabeza, style = Stroke(1f * u))
        drawCircle(t.c(mezcla(ROBLE, CAL_OSC, 0.2f)), w * 0.004f, cabeza)
        destello(cabeza - Offset(w * 0.005f, w * 0.005f), 1.1f * u, t)
        contacto(Offset(pie.x, pie.y), w * 0.03f, 2.5f * u, 0.35f)
    }

    // ---------------------------------------------------------------- al pie

    /**
     * La tabla preparada con yeso, apoyada en el zócalo y cortada por el borde izquierdo: la cara blanca con las pasadas de
     * la brocha y el arranque de un dibujo a carboncillo, el canto de álamo con el yeso que rebosa y su sombra en el mueble.
     */
    private fun DrawScope.tablaDeYeso(b: Rect, w: Float) {
        val u = density
        val hb = b.height
        val r = Rect(-w * 0.03f, b.top + hb * 0.115f, w * 0.103f, b.top + hb * 0.985f)
        val t = tn(r.center)
        val canto = w * 0.013f
        drawRect(t.s(0.3f), Offset(r.left + w * 0.03f, r.top + w * 0.02f), Size(r.width + canto, r.height - w * 0.03f))
        contacto(Offset(r.right, r.bottom), w * 0.12f, 5f * u, 0.4f)
        // El canto de la derecha, en sombra, y el de arriba, con luz.
        drawRect(t.c(mezcla(PERAL, NOGAL, 0.35f)), Offset(r.right, r.top + 1.5f * u), Size(canto, r.height - 1.5f * u))
        vetas(Rect(r.right, r.top, r.right + canto, r.bottom), (if (esNoche) NOCHE else NOGAL_OSC).copy(alpha = 0.4f), 7300, 0.9f, vertical = true, grosor = 0.5f, ondulacion = 0.3f)
        drawRect(t.s(0.2f), Offset(r.right, r.top + 1.5f * u), Size(canto, r.height))
        val yeso = mezcla(MAYOLICA, CAL_OSC, 0.3f)
        drawRect(t.c(yeso), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            // Las pasadas de la brocha y la luz que resbala.
            for (k in 0 until 9) { val yy = r.top + r.height * (0.05f + 0.105f * k); drawLine(t.s(0.05f), Offset(r.left, yy), Offset(r.right, yy + w * 0.006f * (azar(7301, k) - 0.5f)), (2f + 3f * azar(7302, k)) * u) }
            drawRect(t.b(0.25f), r.topLeft, Size(r.width, r.height * 0.3f))
            drawRect(t.s(0.1f), Offset(r.left, r.top + r.height * 0.7f), Size(r.width, r.height * 0.3f))
            // El arranque del dibujo: un arco y unos pliegues, a carboncillo.
            val carbon = t.c(mezcla(TINTA, SOMBRA_MUEBLE, 0.4f)).copy(alpha = 0.45f)
            drawArc(carbon, 200f, 150f, false, Offset(r.left + r.width * 0.3f, r.top + r.height * 0.1f), Size(r.width * 0.7f, r.width * 0.7f), style = Stroke(0.8f * u))
            listOf(0.42f to 0.86f, 0.6f to 0.8f, 0.8f to 0.9f).forEach { (fx, fx1) ->
                drawPath(Path().apply { moveTo(r.left + r.width * fx, r.top + r.height * 0.42f); cubicTo(r.left + r.width * (fx - 0.1f), r.top + r.height * 0.55f, r.left + r.width * (fx1 + 0.08f), r.top + r.height * 0.7f, r.left + r.width * fx1, r.top + r.height * 0.9f) }, carbon, style = Stroke(0.8f * u))
            }
            // Las salpicaduras del suelo.
            drawOval(t.c(BARRO).copy(alpha = 0.25f), Offset(r.left, r.bottom - w * 0.02f), Size(r.width, w * 0.03f))
        }
        // El yeso que rebosa por el canto y el de arriba, de álamo.
        listOf(0.2f, 0.62f).forEachIndexed { i, f -> drawOval(t.c(yeso), Offset(r.right - 0.5f * u, r.top + r.height * f), Size(canto * 0.4f, w * (0.01f + 0.006f * i))) }
        drawRect(t.c(mezcla(PERAL, CARTELLINO, 0.3f)), Offset(r.left, r.top - w * 0.006f), Size(r.width + canto, w * 0.006f))
        drawRect(t.b(0.5f), Offset(r.left, r.top - w * 0.006f), Size(r.width + canto, 1f * u))
        filo(Rect(r.left, r.top - w * 0.006f, r.right + canto, r.bottom), t, true)
    }

    /**
     * La garrafa del aceite de linaza, forrada de paja y cortada por el borde derecho: la panza con sus hebras y sus tres
     * ataduras, el cuello de vidrio verde con su brillo y el tapón de trapo; una chorrera de aceite oscurece la paja.
     */
    private fun DrawScope.garrafa(pie: Offset, w: Float) {
        val u = density
        val x = pie.x; val y = pie.y
        val t = tn(Offset(x, y - w * 0.1f))
        val hombro = y - w * 0.172f
        val panza = Path().apply {
            moveTo(x - w * 0.07f, y)
            cubicTo(x - w * 0.1f, y - w * 0.03f, x - w * 0.1f, y - w * 0.115f, x - w * 0.052f, y - w * 0.146f)
            cubicTo(x - w * 0.03f, y - w * 0.158f, x - w * 0.019f, y - w * 0.162f, x - w * 0.017f, hombro)
            lineTo(x + w * 0.017f, hombro)
            cubicTo(x + w * 0.019f, y - w * 0.162f, x + w * 0.03f, y - w * 0.158f, x + w * 0.052f, y - w * 0.146f)
            cubicTo(x + w * 0.1f, y - w * 0.115f, x + w * 0.1f, y - w * 0.03f, x + w * 0.07f, y)
            close()
        }
        contacto(Offset(x + w * 0.01f, y), w * 0.2f, 6f * u, 0.42f)
        // El cuello de vidrio y el tapón.
        val cuello = Rect(x - w * 0.013f, hombro - w * 0.05f, x + w * 0.013f, hombro + w * 0.004f)
        val vidrio = mezcla(TIERRA_VERDE, EBANO, 0.35f)
        drawRect(t.c(vidrio), cuello.topLeft, cuello.size)
        clipRect(cuello.left, cuello.top, cuello.right, cuello.bottom) { cilindro(cuello, t, 1.6f) }
        drawRect(t.c(mezcla(vidrio, MAYOLICA, 0.2f)), Offset(cuello.left - w * 0.004f, cuello.top), Size(cuello.width + w * 0.008f, w * 0.008f))
        drawRect(t.b(0.6f), Offset(cuello.left - w * 0.004f, cuello.top), Size(cuello.width + w * 0.008f, 1f * u))
        val trapo = Path().apply {
            moveTo(cuello.left - w * 0.002f, cuello.top)
            cubicTo(cuello.left - w * 0.012f, cuello.top - w * 0.012f, x - w * 0.006f, cuello.top - w * 0.026f, x + w * 0.004f, cuello.top - w * 0.02f)
            cubicTo(x + w * 0.016f, cuello.top - w * 0.028f, cuello.right + w * 0.01f, cuello.top - w * 0.01f, cuello.right + w * 0.002f, cuello.top)
            close()
        }
        drawPath(trapo, t.c(LINO))
        clipPath(trapo) { drawRect(t.s(0.22f), Offset(x + w * 0.002f, cuello.top - w * 0.03f), Size(w * 0.03f, w * 0.03f)); drawLine(t.s(0.25f), Offset(x - w * 0.004f, cuello.top), Offset(x + w * 0.002f, cuello.top - w * 0.02f), 0.8f * u) }
        // La panza de paja.
        val paja = mezcla(OCRE, CARTELLINO, 0.38f)
        drawPath(panza, t.c(paja))
        clipPath(panza) {
            for (i in -8..8) {
                val f = i / 8f
                val hebra = Path().apply { moveTo(x + w * 0.016f * f, hombro); cubicTo(x + w * 0.07f * f, y - w * 0.15f, x + w * 0.105f * f, y - w * 0.09f, x + w * 0.075f * f, y) }
                drawPath(hebra, t.s(0.2f), style = Stroke(0.9f * u))
                translate(1.2f * u, 0f) { drawPath(hebra, t.b(0.3f), style = Stroke(0.7f * u)) }
            }
            cilindro(Rect(x - w * 0.1f, hombro, x + w * 0.1f, y), t, 1.25f)
            // La chorrera de aceite.
            drawPath(Path().apply { moveTo(x - w * 0.012f, hombro); cubicTo(x - w * 0.03f, y - w * 0.15f, x - w * 0.05f, y - w * 0.1f, x - w * 0.045f, y - w * 0.05f) }, t.c(mezcla(OCRE, NOGAL, 0.6f)).copy(alpha = 0.5f), style = Stroke(w * 0.014f, cap = StrokeCap.Round))
            // Las ataduras: tres vueltas de paja trenzada, combadas.
            listOf(0.035f, 0.088f, 0.136f).forEach { dy ->
                val yy = y - w * dy
                val vuelta = Path().apply { moveTo(x - w * 0.11f, yy - w * 0.006f); cubicTo(x - w * 0.05f, yy + w * 0.008f, x + w * 0.05f, yy + w * 0.008f, x + w * 0.11f, yy - w * 0.006f) }
                translate(0f, 2.2f * u) { drawPath(vuelta, t.s(0.3f), style = Stroke(2.5f * u)) }
                drawPath(vuelta, t.c(mezcla(paja, NOGAL, 0.35f)), style = Stroke(w * 0.009f))
                translate(0f, -1.2f * u) { drawPath(vuelta, t.b(0.4f), style = Stroke(0.8f * u)) }
            }
            drawRect(t.s(0.25f), Offset(x - w * 0.1f, y - w * 0.012f), Size(w * 0.2f, w * 0.012f))
        }
        filo(Rect(x - w * 0.09f, hombro, x + w * 0.09f, y), t, false)
    }

    /**
     * El cubo de duelas con sus dos aros de hierro y su asa de cuerda, delante de la garrafa: dentro, el yeso de preparar las
     * tablas, que chorrea por fuera, y el palo de removerlo.
     */
    private fun DrawScope.cubo(pie: Offset, w: Float) {
        val u = density
        val x = pie.x; val y = pie.y
        val alto = w * 0.105f
        val boca = y - alto
        val t = tn(Offset(x, y - alto / 2))
        val cuerpo = poligono(x - w * 0.062f, boca, x + w * 0.062f, boca, x + w * 0.05f, y, x - w * 0.05f, y)
        contacto(Offset(x + w * 0.01f, y), w * 0.14f, 5f * u, 0.45f)
        translate(w * 0.02f, w * 0.006f) { drawPath(cuerpo, t.s(0.2f)) }
        // El palo, que sale por la izquierda.
        val p0 = Offset(x - w * 0.01f, boca + w * 0.004f); val p1 = Offset(x - w * 0.075f, boca - w * 0.085f)
        drawLine(t.s(0.25f), p0 + Offset(w * 0.01f, w * 0.006f), p1 + Offset(w * 0.01f, w * 0.006f), 3f * u)
        drawLine(t.c(mezcla(PERAL, NOGAL_LUZ, 0.5f)), p0, p1, 3.4f * u, StrokeCap.Round)
        drawLine(t.b(0.4f), p0 - Offset(1f * u, 1f * u), p1 - Offset(1f * u, 1f * u), 0.8f * u)
        drawLine(t.c(MAYOLICA), p0, p0 + (p1 - p0) * 0.3f, 3.6f * u)
        val madera = mezcla(PERAL, NOGAL_LUZ, 0.6f)
        drawPath(cuerpo, t.c(madera))
        clipPath(cuerpo) {
            // Las duelas.
            for (i in -4..4) {
                val f = i / 4.5f
                drawLine(t.s(0.3f), Offset(x + w * 0.062f * f, boca), Offset(x + w * 0.05f * f, y), 1f * u)
                drawLine(t.b(0.25f), Offset(x + w * 0.062f * f + 1.2f * u, boca), Offset(x + w * 0.05f * f + 1.2f * u, y), 0.7f * u)
            }
            cilindro(Rect(x - w * 0.062f, boca, x + w * 0.062f, y), t, 1.2f)
            // Los aros de hierro.
            listOf(0.2f, 0.74f).forEach { f ->
                val yy = boca + alto * f
                drawRect(t.s(0.3f), Offset(x - w * 0.07f, yy + w * 0.009f), Size(w * 0.14f, 1.6f * u))
                drawRect(t.c(mezcla(ROBLE, EBANO, 0.4f)), Offset(x - w * 0.07f, yy), Size(w * 0.14f, w * 0.009f))
                drawRect(t.b(0.35f), Offset(x - w * 0.07f, yy), Size(w * 0.05f, 0.9f * u))
            }
            // El yeso que chorrea.
            listOf(Triple(-0.03f, 0.05f, 0.006f), Triple(-0.016f, 0.03f, 0.004f), Triple(0.022f, 0.065f, 0.005f)).forEach { (dx, largo, a) ->
                drawLine(t.c(MAYOLICA), Offset(x + w * dx, boca), Offset(x + w * dx, boca + w * largo), w * a * 2f, StrokeCap.Round)
            }
            drawRect(t.s(0.25f), Offset(x - w * 0.06f, y - w * 0.008f), Size(w * 0.12f, w * 0.008f))
        }
        // La boca: el yeso de dentro y el borde.
        val br = Rect(x - w * 0.062f, boca - w * 0.011f, x + w * 0.062f, boca + w * 0.011f)
        drawOval(t.c(mezcla(madera, NOGAL_OSC, 0.3f)), br.topLeft, br.size)
        drawOval(t.c(mezcla(MAYOLICA, CAL_OSC, 0.25f)), Offset(br.left + w * 0.007f, br.top + w * 0.004f), Size(br.width - w * 0.014f, br.height - w * 0.006f))
        drawLine(t.c(MAYOLICA), p0, p0 + (p1 - p0) * 0.12f, 3.6f * u)
        drawArc(t.b(0.5f), 180f, 180f, false, br.topLeft, br.size, style = Stroke(1f * u))
        drawArc(t.c(madera), 0f, 180f, false, br.topLeft, br.size, style = Stroke(1.8f * u))
        // El asa de cuerda, caída hacia delante.
        val asa = Path().apply { moveTo(br.left + 1f * u, boca); cubicTo(br.left + w * 0.01f, boca + w * 0.05f, br.right - w * 0.01f, boca + w * 0.05f, br.right - 1f * u, boca) }
        translate(1.5f * u, 2f * u) { drawPath(asa, t.s(0.3f), style = Stroke(2.2f * u)) }
        drawPath(asa, t.c(mezcla(LINO, NOGAL, 0.3f)), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        filo(Rect(br.left, boca, br.right, y), t, false)
    }

    // ---------------------------------------------------------------- cada compartimento

    /** El papel teñido de rosa de los estudios a punta de plata, y la vitela del cuaderno. */
    private val PAPEL_ROSA = Color(0xFFDDB8A2)
    private val VITELA = Color(0xFFE3D3A8)
    /** El cartón gris pardo de la carpeta. */
    private val CARTON = Color(0xFF8E7B5F)

    /** El cartellino de noche: lejos de la vela, [PAPEL_LEJOS]; cerca, hacia el papel con un poco de llama. */
    private fun papelCartellino(t: Tono): Color =
        if (!t.o) mezcla(CARTELLINO, LUZ_MUEBLE, t.dia)
        else mezcla(PAPEL_LEJOS, mezcla(CARTELLINO, LLAMA, 0.14f), ((t.k - 0.2f) / 0.6f).coerceIn(0f, 1f))

    /**
     * El cartellino: la tira de papel donde va el tema, pegada con dos gotas de cera roja, con el borde algo irregular, una
     * esquina doblada y su sombra. [blanco]: el de un cajoncito libre, sin nada escrito y con una raya de carboncillo.
     */
    private fun DrawScope.cartellino(r: Rect, t: Tono, semilla: Int, blanco: Boolean = false) {
        val u = density
        val forma = contornoIrregular(r, semilla, 22, 0.012f)
        translate(1.6f * u, 2.2f * u) { drawPath(forma, t.s(0.32f)) }
        val papel = papelCartellino(t)
        drawPath(forma, papel)
        clipPath(forma) {
            // La luz de arriba a la izquierda y la sombra del doblez de abajo.
            drawRect(mezcla(papel, SOMBRA_MUEBLE, 0.1f), Offset(r.left, r.bottom - r.height * 0.16f), Size(r.width, r.height * 0.16f))
            drawRect(if (t.o) LLAMA.copy(alpha = (0.12f * t.k).coerceAtMost(0.1f)) else luzMueble(0.4f), r.topLeft, Size(r.width, 1.2f * u))
            pinceladas(r, mezcla(papel, NOGAL, 0.5f), semilla, 3, u * 0.7f, 0.07f)
            if (blanco) drawLine(t.c(EBANO).copy(alpha = 0.25f), Offset(r.left + r.width * 0.3f, r.center.y + r.height * 0.1f), Offset(r.right - r.width * 0.32f, r.center.y + r.height * 0.06f), 0.8f * u, StrokeCap.Round)
        }
        // La esquina doblada, abajo a la derecha.
        val e = minOf(r.height * 0.26f, 7f * u)
        drawPath(poligono(r.right - e, r.bottom, r.right, r.bottom - e, r.right, r.bottom), t.s(0.5f))
        drawPath(poligono(r.right - e, r.bottom, r.right, r.bottom - e, r.right - e * 0.9f, r.bottom - e * 0.9f), mezcla(papel, NOGAL_LUZ, 0.35f))
        // Las dos gotas de cera.
        listOf(Offset(r.left + 4.5f * u, r.top + 3.5f * u), Offset(r.right - 4.5f * u, r.top + 3.5f * u)).forEach { p ->
            drawCircle(t.s(0.3f), 2.1f * u, p + Offset(0.7f * u, 0.9f * u))
            drawCircle(t.c(mezcla(BERMELLON, PORFIDO, 0.45f)), 1.9f * u, p)
            drawCircle(t.b(0.55f), 0.6f * u, p - Offset(0.6f * u, 0.6f * u))
        }
    }

    /** El botón de bronce: la roseta, el pomo con su lado de sombra y el brillo hacia la luz ([der]: la luz por la derecha). */
    private fun DrawScope.boton(c: Offset, r: Float, t: Tono, der: Boolean) {
        val s = if (der) -1f else 1f
        drawCircle(t.s(0.38f), r * 1.12f, c + Offset(r * 0.35f * s, r * 0.45f))
        drawPath(estrella(c, r * 1.25f, 8, 0.78f, -90f + 22.5f), t.c(mezcla(BRONCE, NOGAL_OSC, 0.35f)))
        drawCircle(t.c(BRONCE), r, c)
        drawCircle(t.c(mezcla(BRONCE, NOGAL_OSC, 0.45f)), r * 0.72f, c + Offset(r * 0.25f * s, r * 0.25f))
        drawCircle(t.c(mezcla(BRONCE, LATON, 0.6f)), r * 0.62f, c - Offset(r * 0.08f * s, r * 0.08f))
        destello(c - Offset(r * 0.32f * s, r * 0.34f), r * 0.24f, t)
    }

    /**
     * El frente de un cajoncito: el marco de [marco] con su bisel; arriba, el travesaño donde va el cartellino; en medio, el
     * panel rehundido de [campo] (veta tendida) con su filete de ébano y arce y, en el centro, el cuadro de taracea del
     * [motivo] (ver [taracea]), con un rombo de dos maderas a cada lado; abajo, el travesaño con el botón de bronce. [panelArriba]
     * y [panelAbajo] son, en píxeles, dónde empieza y acaba el panel.
     */
    private fun DrawScope.frenteCajon(f: Rect, marco: Color, campo: Color, motivo: Int, t: Tono, semilla: Int, der: Boolean,
                                      panelArriba: Float, panelAbajo: Float) {
        val u = density
        val fw = f.width; val fh = f.height
        drawRect(t.c(marco), f.topLeft, f.size)
        vetas(f, (if (t.o) NOCHE else NOGAL_OSC).copy(alpha = 0.3f), semilla, 0.35f, grosor = 0.55f, ondulacion = 0.5f)
        // El bisel del marco: arriba e izquierda con luz, abajo y derecha en sombra (de noche, el lado de la vela con filo).
        drawRect(t.b(0.42f), f.topLeft, Size(fw, 1.3f * u))
        drawRect(if (der) t.s(0.3f) else t.b(0.25f), f.topLeft, Size(1.3f * u, fh))
        drawRect(t.s(0.42f), Offset(f.left, f.bottom - 1.6f * u), Size(fw, 1.6f * u))
        drawRect(if (der) t.b(0.3f) else t.s(0.32f), Offset(f.right - 1.4f * u, f.top), Size(1.4f * u, fh))
        // El panel rehundido: la sombra del marco arriba y a la izquierda, y el canto de abajo con luz.
        val p = Rect(f.left + fw * 0.045f, panelArriba, f.right - fw * 0.045f, panelAbajo)
        if (p.height < 4f * u) return
        drawRect(t.c(campo), p.topLeft, p.size)
        vetas(p, (if (t.o) NOCHE else mezcla(campo, NOGAL_OSC, 0.6f)).copy(alpha = 0.26f), semilla + 1, 0.5f, grosor = 0.5f, ondulacion = 0.5f)
        // El filete: ébano, arce, ébano.
        val fi = Rect(p.left + 2.5f * u, p.top + 2.5f * u, p.right - 2.5f * u, p.bottom - 2.5f * u)
        drawRect(t.c(EBANO), fi.topLeft, fi.size, style = Stroke(2.4f * u))
        drawRect(t.c(ARCE), fi.topLeft, fi.size, style = Stroke(0.9f * u))
        // El cuadro de taracea, en el centro, y un rombo de dos maderas a cada lado.
        val mh = fi.height - 5f * u
        val mw = minOf(fi.width * 0.68f, mh * 2.1f)
        val m = Rect(p.center.x - mw / 2, fi.top + 2.5f * u, p.center.x + mw / 2, fi.top + 2.5f * u + mh)
        listOf((fi.left + m.left) / 2, (m.right + fi.right) / 2).forEach { x ->
            val c = Offset(x, m.center.y)
            val a = minOf((m.left - fi.left) * 0.22f, mh * 0.22f); val b = mh * 0.3f
            if (a > 1.5f * u) {
                pieza(poligono(c.x, c.y - b, c.x, c.y + b, c.x - a, c.y), ARCE, 90f, t, c - Offset(a * 0.4f, 0f), a * 0.8f)
                pieza(poligono(c.x, c.y - b, c.x + a, c.y, c.x, c.y + b), NOGAL, 90f, t, c + Offset(a * 0.4f, 0f), a * 0.8f)
                drawCircle(t.c(BOJ), a * 0.16f, Offset(c.x, c.y - b - a * 0.35f))
                drawCircle(t.c(BOJ), a * 0.16f, Offset(c.x, c.y + b + a * 0.35f))
            }
        }
        drawRect(t.s(0.4f), Offset(m.left - 1.2f * u, m.top - 1.2f * u), Size(m.width + 2.4f * u, m.height + 2.4f * u))
        clipRect(m.left, m.top, m.right, m.bottom) { taracea(m, motivo, t) }
        drawRect(t.c(EBANO), m.topLeft, m.size, style = Stroke(1.6f * u))
        drawRect(t.c(BOJ), Offset(m.left + 1.4f * u, m.top + 1.4f * u), Size(m.width - 2.8f * u, m.height - 2.8f * u), style = Stroke(0.7f * u))
        // El rehundido: la sombra del marco arriba y a la izquierda, el canto de abajo con luz.
        drawRect(t.s(0.32f), p.topLeft, Size(p.width, 2.2f * u))
        drawRect(t.s(0.2f), p.topLeft, Size(2f * u, p.height))
        drawRect(t.b(0.32f), Offset(p.left, p.bottom - 1f * u), Size(p.width, 1f * u))
    }

    /**
     * Un cuadro de taracea en [m], como los del studiolo: cada pieza de su madera y con su veta en su dirección.
     * 0, la estrella de ocho puntas en su tondo; 1, los cubos en perspectiva; 2, el damero en escorzo; 3, el mazzocchio;
     * 4, el poliedro vaciado (las caras hechas marco); 5, la celosía de rombos; 6, el laúd en trampantojo.
     */
    private fun DrawScope.taracea(m: Rect, motivo: Int, t: Tono) {
        val u = density
        val c = m.center
        val r = minOf(m.width, m.height) / 2
        when (motivo) {
            0 -> {
                tabla(m, ROBLE, t, 7501, densidad = 0.6f)
                drawCircle(t.c(EBANO), r * 0.97f, c)
                drawCircle(t.c(BOJ), r * 0.9f, c)
                drawCircle(t.c(mezcla(ROBLE, NOGAL, 0.5f)), r * 0.84f, c)
                for (k in 0 until 8) {
                    val a0 = Math.toRadians(-90.0 + 45.0 * k); val a1 = Math.toRadians(-90.0 + 45.0 * k - 22.5); val a2 = Math.toRadians(-90.0 + 45.0 * k + 22.5)
                    val punta = Offset(c.x + r * 0.8f * cos(a0).toFloat(), c.y + r * 0.8f * sin(a0).toFloat())
                    val i1 = Offset(c.x + r * 0.34f * cos(a1).toFloat(), c.y + r * 0.34f * sin(a1).toFloat())
                    val i2 = Offset(c.x + r * 0.34f * cos(a2).toFloat(), c.y + r * 0.34f * sin(a2).toFloat())
                    pieza(poligono(c.x, c.y, i1.x, i1.y, punta.x, punta.y), ARCE, -90f + 45f * k, t, (c + i1 + punta) / 3f, r * 0.2f)
                    pieza(poligono(c.x, c.y, punta.x, punta.y, i2.x, i2.y), if (k % 2 == 0) CEREZO else BOJ, -90f + 45f * k, t, (c + i2 + punta) / 3f, r * 0.2f)
                }
                // Las esquinas: cuatro hojas de peral.
                listOf(m.topLeft, Offset(m.right, m.top), Offset(m.left, m.bottom), m.bottomRight).forEach { q ->
                    val d = (c - q); val l = d.getDistance().coerceAtLeast(1f); val e = d / l
                    val tip = q + e * (l - r * 1.0f)
                    if ((tip - q).getDistance() > 3f * u) drawLine(t.c(PERAL), q + e * (2f * u), tip, minOf(r * 0.16f, 3f * u), StrokeCap.Round)
                }
            }
            1 -> {
                val s = maxOf(1f, m.height / 2.6f)
                for (fila in 0 until 4) {
                    val cy = m.top + s * 0.5f + fila * 1.5f * s
                    val x0 = m.left - s + (if (fila % 2 == 0) 0f else s * 0.866f)
                    val n = (m.width / (1.732f * s)).toInt() + 3
                    for (i in 0 until n.coerceAtMost(14)) {
                        val cx = x0 + i * 1.732f * s
                        pieza(poligono(cx, cy, cx + 0.866f * s, cy - 0.5f * s, cx, cy - s, cx - 0.866f * s, cy - 0.5f * s), mezcla(ARCE, LUZ_MUEBLE, 0.2f), 0f, t, Offset(cx, cy - s * 0.5f), s * 0.5f)
                        pieza(poligono(cx, cy, cx - 0.866f * s, cy - 0.5f * s, cx - 0.866f * s, cy + 0.5f * s, cx, cy + s), BOJ, 60f, t, Offset(cx - s * 0.43f, cy + s * 0.25f), s * 0.45f)
                        pieza(poligono(cx, cy, cx + 0.866f * s, cy - 0.5f * s, cx + 0.866f * s, cy + 0.5f * s, cx, cy + s), mezcla(NOGAL, NOGAL_OSC, 0.3f), -60f, t, Offset(cx + s * 0.43f, cy + s * 0.25f), s * 0.45f)
                    }
                }
            }
            2 -> {
                // Un suelo de damero que se aleja: más angosto y más apretado arriba, con la línea del horizonte.
                tabla(m, ROBLE, t, 7502, densidad = 0.6f)
                val hz = m.top + m.height * 0.2f
                drawRect(t.c(mezcla(PERAL, ROBLE, 0.35f)), m.topLeft, Size(m.width, hz - m.top))
                drawLine(t.c(BOJ), Offset(m.left, hz), Offset(m.right, hz), 1f * u)
                val filas = 5; val cols = 6
                fun y(k: Int) = hz + (m.bottom - hz) * (k / filas.toFloat()).let { it * (0.45f + 0.55f * it) }
                fun ancho(yy: Float) = m.width * (0.34f + 0.76f * (yy - hz) / (m.bottom - hz))
                fun x(j: Int, yy: Float) = c.x + (j / cols.toFloat() - 0.5f) * ancho(yy)
                for (k in 0 until filas) for (j in 0 until cols) {
                    val ya = y(k); val yb = y(k + 1)
                    val q = poligono(x(j, ya), ya, x(j + 1, ya), ya, x(j + 1, yb), yb, x(j, yb), yb)
                    drawPath(q, t.c(if ((k + j) % 2 == 0) mezcla(ARCE, LUZ_MUEBLE, 0.15f) else mezcla(NOGAL, NOGAL_OSC, 0.4f)))
                }
                // Las vetas siguen las hileras: tendidas en el arce.
                for (k in 1 until filas) drawLine(t.s(0.18f), Offset(x(0, y(k)), y(k)), Offset(x(cols, y(k)), y(k)), 0.5f * u)
            }
            3 -> {
                // El mazzocchio en trampantojo, sobre su repisa, como en los armarios de Urbino: el fondo, la tabla y su sombra.
                tabla(m, ROBLE, t, 7503, densidad = 0.6f)
                repisa(m, t)
                drawOval(t.s(0.45f), Offset(c.x - r * 0.75f, m.bottom - m.height * 0.2f - r * 0.12f), Size(r * 1.9f, r * 0.24f))
                mazzocchio(Offset(c.x, m.bottom - m.height * 0.2f - r * 0.48f), r * 0.82f, t)
            }
            4 -> {
                // El poliedro vaciado: cada cara, un marco de madera que deja ver el fondo; las de la luz, de arce; las de la sombra, de nogal.
                tabla(m, mezcla(ROBLE, EBANO, 0.3f), t, 7504, densidad = 0.6f)
                repisa(m, t)
                val rad = r * 0.86f
                val cc = Offset(c.x, m.bottom - m.height * 0.2f - rad * PIE_DODECAEDRO)
                drawOval(t.s(0.45f), Offset(cc.x - rad * 0.7f, m.bottom - m.height * 0.2f - rad * 0.1f), Size(rad * 1.8f, rad * 0.22f))
                CARAS.sortedBy { it.nz }.forEach { k ->
                    val br = (k.nx * -0.5f + k.ny * -0.68f + k.nz * 0.54f).coerceIn(0f, 1f)
                    val pts = k.pts.map { cc + it * rad }
                    val cen = Offset(pts.map { it.x }.average().toFloat(), pts.map { it.y }.average().toFloat())
                    val fuera = Path().apply { pts.forEachIndexed { i, q -> if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }; close() }
                    val dentro = Path().apply { pts.forEachIndexed { i, q -> val z = cen + (q - cen) * 0.58f; if (i == 0) moveTo(z.x, z.y) else lineTo(z.x, z.y) }; close() }
                    drawPath(fuera, t.c(mezcla(mezcla(NOGAL, NOGAL_OSC, 0.3f), mezcla(ARCE, LUZ_MUEBLE, 0.2f), br)))
                    drawPath(dentro, t.c(mezcla(ROBLE, EBANO, 0.45f)))
                    drawPath(fuera, t.c(EBANO).copy(alpha = 0.5f), style = Stroke(0.6f * u))
                }
            }
            5 -> {
                // La celosía: listones de arce y de peral cruzados en rombo sobre nogal, con un clavito de boj en cada cruce.
                tabla(m, mezcla(NOGAL, NOGAL_OSC, 0.3f), t, 7505, densidad = 0.6f)
                val d = maxOf(4f * u, m.height / 2.4f)
                val ancho = maxOf(1.5f * u, d * 0.2f)
                for (i in -8..8) {
                    val b = c.x + i * d
                    drawLine(t.c(PERAL), Offset(b - m.height, m.bottom), Offset(b + m.height, m.top), ancho)
                }
                for (i in -8..8) {
                    val b = c.x + i * d
                    drawLine(t.c(ARCE), Offset(b - m.height, m.top), Offset(b + m.height, m.bottom), ancho)
                    drawLine(t.s(0.25f), Offset(b - m.height + ancho * 0.5f, m.top + ancho * 0.5f), Offset(b + m.height + ancho * 0.5f, m.bottom + ancho * 0.5f), 0.6f * u)
                }
                for (i in -8..8) for (j in -3..3) {
                    val p = Offset(c.x + (i + 0.5f * (j % 2)) * d, c.y + j * d / 2f)
                    if (p.x > m.left && p.x < m.right && p.y > m.top && p.y < m.bottom) drawCircle(t.c(BOJ), ancho * 0.45f, p)
                }
            }
            else -> {
                // El laúd tumbado en diagonal: la caja de duelas de cerezo y peral, la tapa de arce con su rosa calada, el puente,
                // el mástil de nogal con sus trastes y el clavijero vuelto hacia atrás.
                tabla(m, ROBLE, t, 7506, densidad = 0.6f)
                val cuerpo = Offset(m.left + m.width * 0.36f, m.top + m.height * 0.56f)
                val rr = m.height * 0.4f
                rotate(-24f, cuerpo) {
                    val mastil = Rect(cuerpo.x + rr * 0.8f, cuerpo.y - rr * 0.13f, cuerpo.x + rr * 2.35f, cuerpo.y + rr * 0.13f)
                    drawRect(t.s(0.3f), Offset(mastil.left + 1f * u, mastil.top + 1.5f * u), mastil.size)
                    drawRect(t.c(mezcla(NOGAL, NOGAL_OSC, 0.3f)), mastil.topLeft, mastil.size)
                    for (k in 1..6) drawLine(t.c(BOJ), Offset(mastil.left + mastil.width * k / 7f, mastil.top), Offset(mastil.left + mastil.width * k / 7f, mastil.bottom), 0.6f * u)
                    val clav = poligono(mastil.right, mastil.top, mastil.right + rr * 0.55f, mastil.top + rr * 0.2f, mastil.right + rr * 0.5f, mastil.bottom + rr * 0.2f, mastil.right, mastil.bottom)
                    drawPath(clav, t.c(CEREZO))
                    for (k in 0..2) drawCircle(t.c(EBANO), rr * 0.05f, Offset(mastil.right + rr * (0.12f + 0.15f * k), mastil.top - rr * 0.02f + rr * 0.06f * k))
                    val caja = Path().apply {
                        moveTo(cuerpo.x + rr * 0.95f, cuerpo.y - rr * 0.16f)
                        cubicTo(cuerpo.x + rr * 0.4f, cuerpo.y - rr * 0.85f, cuerpo.x - rr * 1.2f, cuerpo.y - rr * 0.95f, cuerpo.x - rr * 1.1f, cuerpo.y)
                        cubicTo(cuerpo.x - rr * 1.2f, cuerpo.y + rr * 0.95f, cuerpo.x + rr * 0.4f, cuerpo.y + rr * 0.85f, cuerpo.x + rr * 0.95f, cuerpo.y + rr * 0.16f)
                        close()
                    }
                    translate(1.5f * u, 2f * u) { drawPath(caja, t.s(0.35f)) }
                    drawPath(caja, t.c(CEREZO))
                    // La tapa: arce, con su veta a lo largo; las duelas asoman como un filo de peral.
                    clipPath(caja) {
                        drawRect(t.c(mezcla(ARCE, PERAL, 0.25f)), Offset(cuerpo.x - rr * 1.2f, cuerpo.y - rr * 0.82f), Size(rr * 2.2f, rr * 1.64f))
                        for (k in -4..4) drawLine(t.c(PERAL).copy(alpha = 0.6f), Offset(cuerpo.x - rr * 1.2f, cuerpo.y + rr * 0.17f * k), Offset(cuerpo.x + rr, cuerpo.y + rr * 0.17f * k), 0.5f * u)
                        drawRect(t.s(0.22f), Offset(cuerpo.x - rr * 1.2f, cuerpo.y + rr * 0.35f), Size(rr * 2.2f, rr * 0.6f))
                    }
                    drawPath(caja, t.c(CEREZO), style = Stroke(1.6f * u))
                    // La rosa calada y el puente.
                    val rosa = Offset(cuerpo.x - rr * 0.05f, cuerpo.y)
                    drawCircle(t.c(EBANO), rr * 0.3f, rosa)
                    drawPath(estrella(rosa, rr * 0.27f, 6, 0.45f), t.c(BOJ))
                    drawCircle(t.c(EBANO), rr * 0.08f, rosa)
                    drawRect(t.c(mezcla(NOGAL, NOGAL_OSC, 0.4f)), Offset(cuerpo.x - rr * 0.8f, cuerpo.y - rr * 0.3f), Size(rr * 0.09f, rr * 0.6f))
                    for (k in -2..2) drawLine(t.c(mezcla(BOJ, CARTELLINO, 0.4f)).copy(alpha = 0.8f), Offset(cuerpo.x - rr * 0.76f, cuerpo.y + rr * 0.05f * k), Offset(mastil.right, cuerpo.y + rr * 0.04f * k), 0.45f * u)
                }
            }
        }
    }

    /** La repisa pintada del trampantojo: la tabla de peral abajo, con su canto de luz y la sombra que deja en el fondo. */
    private fun DrawScope.repisa(m: Rect, t: Tono) {
        val u = density
        val y = m.bottom - m.height * 0.2f
        drawRect(t.c(PERAL), Offset(m.left, y), Size(m.width, m.bottom - y))
        drawRect(t.c(mezcla(PERAL, NOGAL, 0.4f)), Offset(m.left, y + (m.bottom - y) * 0.45f), Size(m.width, (m.bottom - y) * 0.55f))
        drawRect(t.b(0.5f), Offset(m.left, y), Size(m.width, 1f * u))
        drawRect(t.s(0.3f), Offset(m.left, y - 2.5f * u), Size(m.width, 2.5f * u))
    }

    /**
     * Lo que asoma de un cajón entreabierto entre [arriba] (donde acaba el hueco) y [abajo] (el canto de arriba del frente
     * salido): lo hondo del cajón, sus costados de peral que se meten en el hueco y las hojas de boceto de pie, ladeadas, de
     * papel blanco, teñido de ocre o de rosa, con el borde de arriba irregular; las de delante llevan un estudio a la
     * sanguina (un círculo con su construcción, el perfil de un capitel o una espiral).
     */
    private fun DrawScope.hojas(w: Float, arriba: Float, abajo: Float, n: Int, semilla: Int, t: Tono) {
        val u = density
        val alto = abajo - arriba
        if (alto < 2f * u) return
        drawRect(t.c(mezcla(EBANO, NOGAL_OSC, 0.3f)), Offset(w * 0.02f, arriba), Size(w * 0.96f, alto + 2f * u))
        drawRect(t.s(0.4f), Offset(w * 0.02f, arriba), Size(w * 0.96f, alto * 0.3f))
        val cuantas = (3 + n).coerceIn(3, 9)
        clipRect(w * 0.035f, -alto, w * 0.965f, abajo + 2f * u) {
            for (i in 0 until cuantas) {
                val x = w * (0.06f + 0.72f * (i + 0.5f * azar(semilla, 40 + i)) / cuantas)
                val ancho = w * (0.22f + 0.1f * azar(semilla, 50 + i))
                val y0 = arriba + alto * (0.04f + 0.36f * azar(semilla, 60 + i))
                val giro = (azar(semilla, 65 + i) - 0.5f) * 10f
                val papel = when (Math.floorMod(semilla + i * 7, 4)) { 0 -> PAPEL_TINTO; 1 -> PAPEL_ROSA; else -> PAPEL }
                val tt = Tono(t.o, t.k * (0.7f + 0.3f * i / cuantas), t.dia)
                rotate(giro, Offset(x + ancho / 2, abajo)) {
                    val hoja = Path().apply {
                        moveTo(x, abajo + 2f * u); lineTo(x + w * 0.003f, y0 + 2f * u)
                        for (j in 1..6) lineTo(x + ancho * j / 6f, y0 + (azar(semilla, 70 + i * 7 + j) - 0.3f) * 3f * u)
                        lineTo(x + ancho - w * 0.002f, abajo + 2f * u); close()
                    }
                    translate(1.6f * u, 1.2f * u) { drawPath(hoja, tt.s(0.35f)) }
                    drawPath(hoja, tt.c(papel))
                    drawRect(tt.b(0.35f), Offset(x + w * 0.004f, y0 + 1f * u), Size(ancho - w * 0.008f, 1f * u))
                    drawRect(tt.s(0.12f), Offset(x + ancho * 0.7f, y0), Size(ancho * 0.3f, abajo - y0))
                }
            }
            // Los estudios de las hojas de delante, en la franja que se ve bajo el cartellino.
            val sang = t.c(SANGUINA)
            val yb = abajo - alto * 0.12f
            val sz = minOf(alto * 0.36f, w * 0.06f)
            when (Math.floorMod(semilla ushr 2, 3)) {
                0 -> {
                    val cc = Offset(w * 0.3f, yb - sz)
                    drawCircle(sang, sz, cc, style = Stroke(0.9f * u))
                    drawLine(sang.copy(alpha = 0.6f), cc - Offset(sz * 1.3f, 0f), cc + Offset(sz * 1.3f, 0f), 0.6f * u)
                    drawLine(sang.copy(alpha = 0.6f), cc - Offset(0f, sz * 1.3f), cc + Offset(0f, sz * 0.9f), 0.6f * u)
                    drawPath(estrella(cc, sz, 5, 0.38f), sang.copy(alpha = 0.7f), style = Stroke(0.6f * u))
                }
                1 -> {
                    // El perfil de un capitel: el ábaco, el equino y el collarino.
                    val x0 = w * 0.62f; val y0 = yb - sz * 1.9f
                    drawPath(Path().apply {
                        moveTo(x0, y0); lineTo(x0 + sz * 1.6f, y0); lineTo(x0 + sz * 1.6f, y0 + sz * 0.35f)
                        cubicTo(x0 + sz * 1.6f, y0 + sz * 0.9f, x0 + sz * 0.7f, y0 + sz * 0.8f, x0 + sz * 0.6f, y0 + sz * 1.3f)
                        lineTo(x0 + sz * 0.75f, y0 + sz * 1.45f); lineTo(x0 + sz * 0.6f, y0 + sz * 1.6f); lineTo(x0 + sz * 0.6f, y0 + sz * 2.2f)
                    }, sang, style = Stroke(0.9f * u))
                    for (k in 0 until 5) drawLine(sang.copy(alpha = 0.5f), Offset(x0 + sz * (0.7f + 0.15f * k), y0 + sz * 0.5f), Offset(x0 + sz * (0.6f + 0.15f * k), y0 + sz * 0.95f), 0.5f * u)
                }
                else -> {
                    // Una espiral de voluta con su ojo.
                    val cc = Offset(w * 0.42f, yb - sz)
                    val p = Path()
                    for (k in 0..40) {
                        val a = k * 0.42f; val rad = sz * (1f - k / 44f)
                        val q = Offset(cc.x + rad * cos(a), cc.y + rad * sin(a))
                        if (k == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y)
                    }
                    drawPath(p, sang, style = Stroke(0.8f * u))
                    drawCircle(sang, sz * 0.12f, cc)
                }
            }
        }
        // Los costados del cajón, de peral, que se meten en el hueco desde las esquinas del frente salido.
        listOf(false, true).forEach { d ->
            val xa = if (d) w * 1.0f else 0f
            val xb = if (d) w * 0.95f else w * 0.05f
            val g = if (d) -1f else 1f
            val q = poligono(xa, abajo, xa + g * w * 0.035f, abajo, xb + g * w * 0.02f, arriba + alto * 0.1f, xb, arriba + alto * 0.1f)
            drawPath(q, t.c(if (d) mezcla(PERAL, NOGAL, 0.45f) else PERAL))
            drawPath(q, t.s(0.25f), style = Stroke(0.6f * u))
        }
    }

    /**
     * Un rollo de cartón o de papel tumbado a lo largo en [r]: el cuerpo con su luz arriba, el canto de la hoja que da la
     * vuelta, el extremo de la derecha con su espiral, y dos cintas atadas (una con la punta colgando).
     */
    private fun DrawScope.rolloLargo(r: Rect, papel: Color, cinta: Color, t: Tono, semilla: Int) {
        val u = density
        val e = r.height * 0.34f
        drawRect(t.s(0.35f), Offset(r.left + 2f * u, r.bottom - 1.5f * u), Size(r.width, 3f * u))
        drawRoundRect(t.c(papel), r.topLeft, Size(r.width - e / 2, r.height), androidx.compose.ui.geometry.CornerRadius(e * 0.5f, r.height / 2))
        clipRect(r.left, r.top, r.right - e / 2, r.bottom) { tumbado(r, t, 1.1f) }
        // El canto de la hoja de fuera, que da la vuelta en diagonal.
        val xs = r.left + r.width * (0.25f + 0.4f * azar(semilla, 1))
        drawLine(t.s(0.3f), Offset(xs, r.top + 1f * u), Offset(xs + r.width * 0.06f, r.bottom - 1f * u), 0.9f * u)
        drawLine(t.b(0.35f), Offset(xs - 1f * u, r.top + 1f * u), Offset(xs + r.width * 0.06f - 1f * u, r.bottom - 1f * u), 0.6f * u)
        // El extremo: la espiral.
        val ex = Rect(r.right - e, r.top, r.right, r.bottom)
        drawOval(t.c(mezcla(papel, LUZ_MUEBLE, 0.25f)), ex.topLeft, ex.size)
        drawOval(t.s(0.28f), ex.topLeft, ex.size, style = Stroke(0.9f * u))
        for (k in 1..3) {
            val f = k / 4f
            drawArc(t.s(0.3f), 200f + 40f * k, 260f, false, Offset(ex.center.x - ex.width * f / 2, ex.center.y - ex.height * f / 2), Size(ex.width * f, ex.height * f), style = Stroke(0.6f * u))
        }
        drawOval(t.c(mezcla(EBANO, NOGAL_OSC, 0.5f)), Offset(ex.center.x - ex.width * 0.08f, ex.center.y - ex.height * 0.08f), Size(ex.width * 0.16f, ex.height * 0.16f))
        // Las dos cintas.
        listOf(0.2f, 0.66f).forEachIndexed { i, fx ->
            val x = r.left + r.width * fx
            val cw = maxOf(2.6f * u, r.width * 0.035f)
            drawRect(t.c(cinta), Offset(x, r.top - 0.5f * u), Size(cw, r.height + 1f * u))
            drawRect(t.s(0.3f), Offset(x, r.top + r.height * 0.6f), Size(cw, r.height * 0.4f))
            drawRect(t.b(0.35f), Offset(x, r.top), Size(cw, r.height * 0.2f))
            if (i == 1) {
                val punta = Path().apply {
                    moveTo(x + cw * 0.5f, r.bottom - r.height * 0.3f)
                    cubicTo(x + cw * 2f, r.bottom, x - cw, r.bottom + r.height * 0.15f, x + cw * 1.2f, r.bottom + r.height * 0.38f)
                }
                drawPath(punta, t.c(cinta), style = Stroke(cw * 0.8f, cap = StrokeCap.Butt))
                drawCircle(t.c(mezcla(cinta, EBANO, 0.25f)), cw * 0.75f, Offset(x + cw * 0.5f, r.bottom - r.height * 0.32f))
            }
        }
    }

    /** El cuaderno de vitela de pie, ladeado contra el costado del nicho: el lomo, las tapas flexibles, la correa y su botón. */
    private fun DrawScope.cuadernoVitela(r: Rect, t: Tono, der: Boolean) {
        val u = density
        val p = poligono(r.left + r.width * 0.3f, r.top, r.right, r.top + r.height * 0.04f, r.right - r.width * 0.05f, r.bottom, r.left, r.bottom - r.height * 0.02f)
        translate(-2f * u, 1.5f * u) { drawPath(p, t.s(0.35f)) }
        drawPath(p, t.c(VITELA))
        clipPath(p) {
            drawRect(t.s(0.22f), Offset(r.left + r.width * 0.65f, r.top), Size(r.width * 0.35f, r.height))
            drawRect(t.b(0.35f), Offset(r.left, r.top), Size(r.width * 0.25f, r.height))
            drawLine(t.s(0.25f), Offset(r.left + r.width * 0.28f, r.top), Offset(r.left + r.width * 0.05f, r.bottom), 0.8f * u)
            pinceladas(r, mezcla(VITELA, NOGAL, 0.5f), 7400, 5, u * 0.6f, 0.12f)
        }
        // La correa de cuero y su botón de hueso.
        val y = r.top + r.height * 0.55f
        drawLine(t.c(mezcla(CEREZO, NOGAL_OSC, 0.4f)), Offset(r.left + r.width * 0.12f, y + 1f * u), Offset(r.right - 1f * u, y - 1f * u), 2.2f * u)
        drawCircle(t.s(0.35f), 2.4f * u, Offset(r.left + r.width * 0.55f + 0.8f * u, y + 1f * u))
        drawCircle(t.c(CARTELLINO), 2.2f * u, Offset(r.left + r.width * 0.55f, y))
        destello(Offset(r.left + r.width * 0.55f - 0.7f * u, y - 0.7f * u), 0.6f * u, t)
    }

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece), para repartir bien clases y motivos. */
    private fun mezclar(s: Int): Int {
        var x = s * 0xA5268A23L.toInt()
        x = x xor (x ushr 15)
        x *= 0x1B2E32EF
        return x xor (x ushr 13)
    }

    /** La sal de la mezcla: con ella, los temas de ejemplo salen 3, 3 y 2, sin dos iguales en una fila, y sus seis cajones con seis motivos distintos. */
    private const val SAL = 1750

    /** La clase de un compartimento, por su tema (no por su sitio): 0, cajoncito cerrado; 1, entreabierto; 2, nicho de rollos. */
    private fun clase(semilla: Int): Int = Math.floorMod(mezclar(semilla + SAL), 3)

    /** El motivo de taracea del frente (ver [taracea]). */
    private fun motivo(semilla: Int): Int = Math.floorMod(mezclar(semilla + SAL * 7 + 13) shr 4, 7)

    /** Los dos cordeles de los que cuelga el cartellino de un nicho, desde el canto de arriba. */
    private fun DrawScope.cordeles(r: Rect, t: Tono) {
        val u = density
        listOf(r.left + r.width * 0.1f, r.right - r.width * 0.1f).forEach { x ->
            drawLine(t.s(0.3f), Offset(x + 1f * u, 0f), Offset(x + 1f * u, r.top + 3f * u), 1f * u)
            drawLine(t.c(mezcla(LINO, NOGAL, 0.3f)), Offset(x, 0f), Offset(x, r.top + 3f * u), 0.9f * u)
            drawCircle(t.c(EBANO), 1.3f * u, Offset(x, 1.2f * u))
        }
    }

    /**
     * El cartapacio de lo que no tiene tema: cartón forrado de pergamino, apoyado en diagonal en su nicho, con el lomo hacia
     * la luz, el cartón a la vista en las esquinas gastadas, las hojas que asoman en abanico por arriba, dos cintas atadas en
     * lazo y su sombra en el fondo del nicho.
     */
    private fun DrawScope.cartapacio(w: Float, h: Float, dy: Float, n: Int, t: Tono, semilla: Int) {
        val u = density
        val c = Offset(w * 0.5f, h * 0.68f + dy)
        val cw = w * 0.62f; val chh = h * 0.34f
        val r = Rect(c.x - cw / 2, c.y - chh / 2, c.x + cw / 2, c.y + chh / 2)
        rotate(-17f, c) {
            // Su sombra en el fondo del nicho, corrida a la derecha.
            drawRect(t.s(0.38f), Offset(r.left + w * 0.05f, r.top + h * 0.02f), r.size)
            // Las hojas en abanico.
            val cuantas = (3 + n).coerceIn(3, 8)
            for (j in 0 until cuantas) {
                val giro = -14f + 28f * j / (cuantas - 1).coerceAtLeast(1)
                val pie = Offset(r.left + cw * (0.2f + 0.6f * j / cuantas), r.top + chh * 0.4f)
                rotate(giro, pie) {
                    val hoja = Rect(pie.x - cw * 0.14f, pie.y - chh * (0.75f + 0.25f * azar(semilla, 90 + j)), pie.x + cw * 0.14f, pie.y)
                    drawRect(t.s(0.3f), Offset(hoja.left + 1.5f * u, hoja.top + 1f * u), hoja.size)
                    drawRect(t.c(if (j % 3 == 1) PAPEL_TINTO else if (j % 3 == 2) PAPEL_ROSA else PAPEL), hoja.topLeft, hoja.size)
                    drawRect(t.b(0.35f), hoja.topLeft, Size(hoja.width, 1f * u))
                    if (j == cuantas / 2) drawArc(t.c(SANGUINA).copy(alpha = 0.6f), 200f, 140f, false, Offset(hoja.left + hoja.width * 0.2f, hoja.top + hoja.height * 0.15f), Size(hoja.width * 0.6f, hoja.width * 0.6f), style = Stroke(0.7f * u))
                }
            }
            // Las dos tapas: la de atrás asoma un poco; la de delante, de pergamino, con el cartón en las esquinas.
            drawRect(t.c(mezcla(CARTON, NOGAL_OSC, 0.2f)), Offset(r.left + w * 0.01f, r.top - h * 0.015f), r.size)
            drawRect(t.c(VITELA), r.topLeft, r.size)
            pinceladas(r, mezcla(VITELA, NOGAL, 0.5f), semilla, 6, u, 0.14f)
            drawRect(t.b(0.3f), r.topLeft, Size(r.width * 0.35f, r.height))
            drawRect(t.s(0.2f), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
            listOf(r.topLeft to Offset(1f, 1f), Offset(r.right, r.top) to Offset(-1f, 1f), r.bottomRight to Offset(-1f, -1f)).forEach { (q, s) ->
                val e = chh * 0.22f
                drawPath(poligono(q.x, q.y, q.x + s.x * e, q.y, q.x, q.y + s.y * e), t.c(CARTON))
            }
            // El lomo, abajo, que coge la luz.
            drawRect(t.c(mezcla(VITELA, CARTELLINO, 0.4f)), Offset(r.left, r.bottom - chh * 0.12f), Size(r.width, chh * 0.12f))
            drawRect(t.b(0.45f), Offset(r.left, r.bottom - chh * 0.12f), Size(r.width, 1.1f * u))
            drawRect(t.s(0.35f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
            // Las cintas, atadas en lazo junto al canto de la derecha.
            val cinta = t.c(mezcla(BERMELLON, OCRE, 0.3f))
            val yl = r.top + chh * 0.52f
            drawRect(t.s(0.3f), Offset(r.right - cw * 0.3f + 1f * u, yl + 2.2f * u), Size(cw * 0.3f, 2.6f * u))
            drawRect(cinta, Offset(r.right - cw * 0.3f, yl), Size(cw * 0.3f + w * 0.01f, 2.8f * u))
            val nudo = Offset(r.right - cw * 0.16f, yl + 1.4f * u)
            listOf(-1f, 1f).forEach { s ->
                drawPath(Path().apply { moveTo(nudo.x, nudo.y); cubicTo(nudo.x + s * cw * 0.09f, nudo.y - chh * 0.38f, nudo.x + s * cw * 0.13f, nudo.y + chh * 0.1f, nudo.x, nudo.y) }, cinta, style = Stroke(2.2f * u))
                drawPath(Path().apply { moveTo(nudo.x, nudo.y); cubicTo(nudo.x + s * cw * 0.02f, nudo.y + chh * 0.3f, nudo.x + s * cw * 0.07f, nudo.y + chh * 0.4f, nudo.x + s * cw * 0.05f, nudo.y + chh * 0.62f) }, cinta, style = Stroke(2f * u))
            }
            drawCircle(t.c(mezcla(BERMELLON, PORFIDO, 0.4f)), 2.4f * u, nudo)
            drawCircle(t.b(0.5f), 0.8f * u, nudo - Offset(0.8f * u, 0.8f * u))
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        val n = if (c.vacio) 0 else c.n
        // Dónde está en el mueble (en fracciones del ancho) y cuánto le llega de la vela.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val k = if (oscuro) luz(centro) else 0f
        // De día, la luz alta de la izquierda: la fila de arriba un poco más clara, la columna de la izquierda un punto más.
        val dia = if (oscuro) 0f else 0.07f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.02f else 0f)
        val t = Tono(oscuro, k, dia)
        val der = oscuro && centro.x < LUZ.x
        val baja = h * SALIDA * ab
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
        val clase = if (c.vacio) -1 else if (c.sinTema) 3 else clase(c.semilla)
        val m = mezclar(c.semilla)
        // Las maderas del campo del panel y del marco, según el tema.
        val campos = listOf(PERAL, ARCE, BOJ, mezcla(NOGAL_LUZ, PERAL, 0.45f))
        val campo = campos[Math.floorMod(m shr 8, 4)]
        val marco = listOf(NOGAL, mezcla(NOGAL, CEREZO, 0.55f), mezcla(NOGAL, NOGAL_OSC, 0.3f))[Math.floorMod(m shr 12, 3)]

        when (clase) {
            // El nicho abierto: los rollos atados con cintas, a veces con el cuaderno de vitela; el cartellino cuelga del canto.
            2 -> {
                val dy = baja
                val cintas = listOf(BERMELLON, ULTRAMAR, TIERRA_VERDE, OCRE)
                val papeles = listOf(mezcla(CARTON, PAPEL, 0.3f), PAPEL_TINTO, PAPEL_ROSA, PAPEL)
                val t0 = Math.floorMod(m shr 16, 4)
                val cuaderno = Math.floorMod(m shr 20, 2) == 1
                val xd = if (cuaderno) 0.78f else 0.94f
                if (n >= 5) {
                    val pr = Rect(w * 0.06f, h * 0.36f + dy, w * 0.14f, h * 0.95f + dy)
                    drawRect(t.s(0.35f), Offset(pr.right, pr.top + 3f * u), Size(3f * u, pr.height))
                    drawRect(t.c(PAPEL_TINTO), pr.topLeft, pr.size)
                    clipRect(pr.left, pr.top, pr.right, pr.bottom) { cilindro(pr, t, 1.1f, der) }
                    drawOval(t.c(mezcla(PAPEL_TINTO, LUZ_MUEBLE, 0.3f)), Offset(pr.left, pr.top - pr.width * 0.18f), Size(pr.width, pr.width * 0.36f))
                    drawRect(t.c(cintas[(t0 + 1) % 4]), Offset(pr.left, pr.top + pr.height * 0.3f), Size(pr.width, 2.6f * u))
                }
                val x0 = if (n >= 5) 0.15f else 0.05f
                if (n >= 3) rolloLargo(Rect(w * (x0 + 0.04f), h * 0.36f + dy, w * (xd - 0.24f), h * 0.5f + dy), papeles[(t0 + 2) % 4], cintas[(t0 + 3) % 4], t, c.semilla + 2)
                rolloLargo(Rect(w * (x0 + 0.1f), h * 0.46f + dy, w * (xd - 0.02f), h * 0.67f + dy), papeles[(t0 + 1) % 4], cintas[(t0 + 2) % 4], t, c.semilla + 1)
                rolloLargo(Rect(w * x0, h * 0.65f + dy, w * xd, h * 0.95f + dy), papeles[t0], cintas[t0], t, c.semilla)
                if (cuaderno) cuadernoVitela(Rect(w * 0.8f, h * 0.36f + dy, w * 0.95f, h * 0.95f + dy), t, der)
                cordeles(r, t)
                cartellino(r, t, c.semilla)
            }
            // El cartapacio de lo que no tiene tema, en su nicho.
            3 -> {
                cartapacio(w, h, baja, n, t, c.semilla)
                cordeles(r, t)
                cartellino(r, t, c.semilla)
            }
            else -> {
                val entreabierto = clase == 1
                val e = w * 0.025f * ab
                val f = if (entreabierto) Rect(-w * 0.03f - e, h * 0.49f + baja, w * 1.03f + e, h * 0.965f + baja + h * 0.03f * ab)
                        else Rect(-e, h * (0.012f + 0.004f * n.coerceIn(0, 7)) + baja, w + e, h + baja + h * 0.04f * ab)
                if (entreabierto) {
                    // Las hojas que asoman, los costados del cajón y su sombra en la tabla de abajo.
                    hojas(w, 0f, f.top, maxOf(1, n), c.semilla, t)
                    drawRect(t.s(0.5f), Offset(f.left + 2f * u, f.bottom), Size(f.width, h * 0.035f + 2f * u))
                } else if (f.top > 0.5f) {
                    // Las puntas de las hojas por la rendija de arriba.
                    drawRect(t.c(mezcla(EBANO, NOGAL_OSC, 0.4f)), Offset(w * 0.03f, 0f), Size(w * 0.94f, f.top + 1f * u))
                    for (j in 0 until (2 + n).coerceAtMost(8)) {
                        val x = w * (0.08f + 0.8f * j / (2 + n).coerceAtMost(8))
                        drawRect(t.c(if (j % 3 == 1) PAPEL_TINTO else if (j % 3 == 2) PAPEL_ROSA else PAPEL), Offset(x, f.top * (0.15f + 0.5f * azar(c.semilla, j))), Size(w * 0.1f, f.top))
                    }
                }
                if (ab > 0f && !entreabierto) drawRect(t.s(0.35f * ab), Offset(f.left + 2f * u, f.bottom - 1f * u), Size(f.width, 1f * u + 6f * u * ab))
                val mot = if (c.vacio) Math.floorMod(c.semilla / 7919, 7) else motivo(c.semilla)
                val pa = if (entreabierto) f.top + h * 0.05f else h * 0.355f + baja
                val pb = if (entreabierto) f.bottom - h * 0.1f else f.bottom - h * 0.15f
                frenteCajon(f, if (c.vacio) NOGAL else marco, if (c.vacio) mezcla(NOGAL_LUZ, PERAL, 0.4f) else campo, mot, t, c.semilla, der, pa, pb)
                // El uso: unas rozaduras junto al botón.
                if (Math.floorMod(m shr 24, 3) == 0) for (j in 0 until 5) {
                    val x = w * (0.36f + 0.28f * azar(c.semilla, 300 + j)); val y = f.bottom - h * (0.03f + 0.1f * azar(c.semilla, 310 + j))
                    drawLine(t.b(0.25f), Offset(x, y), Offset(x + w * 0.025f, y - h * 0.02f), 0.6f * u)
                }
                if (entreabierto) cordeles(r, t)
                cartellino(r, t, c.semilla, blanco = c.vacio)
                boton(Offset(w / 2, (pb + f.bottom) / 2), h * (if (entreabierto) 0.04f else 0.048f), t, der)
                // Al abrirlo, sube algo de luz de dentro.
                if (ab > 0f) mancha(Offset(w / 2, f.top), w * 0.4f, if (oscuro) LLAMA.copy(alpha = 0.16f * ab * (0.4f + k)) else luzMueble(0.3f * ab))
            }
        }
        if (ab > 0f && clase >= 2) mancha(Offset(w / 2, h * 0.5f), w * 0.5f, if (oscuro) LLAMA.copy(alpha = 0.14f * ab * (0.4f + k)) else luzMueble(0.25f * ab))
    }

    override val rotulo = Rect(0.06f, 0.045f, 0.94f, 0.335f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA_ROTULO
    override val letraRotulo: FontFamily = LetraExtra.cormorant
    override val salida = SALIDA
    override val placa = Rect(0.365f, 0.13f, 0.635f, 0.39f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA_ROTULO

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Tablas de peral, y sobre ellas un compás y una regla.
        val pe = if (oscuro) mezcla(PERAL, Color.Black, 0.55f) else PERAL
        drawRect(pe)
        val tablas = 4
        for (k in 0 until tablas) {
            val r = Rect(w * k / tablas, 0f, w * (k + 1) / tablas, h)
            vetas(r, NOGAL.copy(alpha = 0.18f), 91 + k, 1.2f, vertical = true, grosor = 0.6f, ondulacion = 0.5f)
            if (k > 0) drawLine(Color.Black.copy(alpha = 0.2f), Offset(r.left, 0f), Offset(r.left, h), 1f * u)
        }
        val bisagra = Offset(w * 0.78f, h * 0.8f)
        drawLine(t(BRONCE, oscuro), bisagra, Offset(bisagra.x - 40 * u, bisagra.y + 110 * u), 2.4f * u, StrokeCap.Round)
        drawLine(t(BRONCE, oscuro), bisagra, Offset(bisagra.x + 10 * u, bisagra.y + 116 * u), 2.4f * u, StrokeCap.Round)
        drawCircle(t(BRONCE, oscuro), 5 * u, bisagra)
        rotate(-12f, Offset(w * 0.3f, h * 0.88f)) {
            drawRect(t(NOGAL_LUZ, oscuro), Offset(w * 0.12f, h * 0.88f), Size(w * 0.42f, 12 * u))
            var rx = w * 0.12f + 6 * u
            while (rx < w * 0.54f) { drawLine(t(EBANO, oscuro), Offset(rx, h * 0.88f), Offset(rx, h * 0.88f + 5 * u), 0.8f * u); rx += 8 * u }
        }
        // Las paredes de nogal con su filete de ébano y arce.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(NOGAL, oscuro), r.topLeft, r.size) }
        drawRect(t(ARCE, oscuro), Offset(pl * 0.4f, pl * 0.4f), Size(w - pl * 0.8f, h - pl * 0.8f), style = Stroke(1.2f * u))
        drawRect(t(EBANO, oscuro), Offset(pl * 0.6f, pl * 0.6f), Size(w - pl * 1.2f, h - pl * 1.2f), style = Stroke(1f * u))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) CARTELLINO else TINTA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 26, 0.006f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(PAPEL.copy(alpha = 0.5f))
        // Un estudio a la sanguina, tenue: un círculo inscrito en un cuadrado con sus diagonales, abajo a la derecha.
        val l = minOf(w, h) * 0.5f
        val o = Offset(w - l * 0.8f, h - l * 0.85f)
        val trazo = SANGUINA.copy(alpha = 0.16f)
        drawRect(trazo, o, Size(l, l), style = Stroke(1f * u))
        drawCircle(trazo, l / 2, Offset(o.x + l / 2, o.y + l / 2), style = Stroke(1f * u))
        drawLine(trazo, o, Offset(o.x + l, o.y + l), 0.8f * u)
        drawLine(trazo, Offset(o.x + l, o.y), Offset(o.x, o.y + l), 0.8f * u)
        // Unos sombreados en diagonal, arriba a la derecha.
        for (k in 0 until 7) {
            val x = w * 0.62f + k * 4 * u
            drawLine(SANGUINA.copy(alpha = 0.12f), Offset(x, h * 0.04f + 10 * u), Offset(x + 10 * u, h * 0.04f), 0.9f * u)
        }
    }

    override val margenes = MargenesFicha(12.dp, 14.dp, 12.dp, 24.dp, 28.dp, 28.dp)
    override val proporcionFicha = 0.95f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.75f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "hecho" -> selloHecho(SANGUINA, p.color)
            "convence" -> drawPath(Path().apply { moveTo(c.x - s * 0.36f, c.y); lineTo(c.x - s * 0.08f, c.y + s * 0.3f); lineTo(c.x + s * 0.4f, c.y - s * 0.36f) }, SANGUINA, style = Stroke(s * 0.13f, cap = StrokeCap.Round))
            // Un compás de puntas.
            "piensa" -> {
                drawLine(AZUL, Offset(c.x, c.y - s * 0.36f), Offset(c.x - s * 0.26f, c.y + s * 0.42f), s * 0.09f, StrokeCap.Round)
                drawLine(AZUL, Offset(c.x, c.y - s * 0.36f), Offset(c.x + s * 0.26f, c.y + s * 0.42f), s * 0.09f, StrokeCap.Round)
                drawCircle(AZUL, s * 0.1f, Offset(c.x, c.y - s * 0.36f))
            }
            "duda" -> circuloDuda(p.tenue)
            // Una tachadura de sombreado cruzado.
            "desacuerdo" -> for (k in -2..2) {
                drawLine(SANGUINA, Offset(c.x - s * 0.36f + k * s * 0.12f, c.y + s * 0.36f), Offset(c.x + s * 0.12f + k * s * 0.12f, c.y - s * 0.36f), s * 0.06f)
                drawLine(SANGUINA, Offset(c.x - s * 0.12f + k * s * 0.12f, c.y - s * 0.36f), Offset(c.x + s * 0.36f + k * s * 0.12f, c.y + s * 0.36f), s * 0.06f)
            }
            else -> {}
        }
    }

    /** Un alfiler de cabeza de bronce. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawLine(Color(0xFF8C8C8C), Offset(c.x - s * 0.04f, c.y - s * 0.1f), Offset(c.x + s * 0.3f, c.y + s * 0.44f), s * 0.06f, StrokeCap.Round)
        drawCircle(BRONCE, s * 0.24f, Offset(c.x - s * 0.1f, c.y - s * 0.2f))
        drawCircle(Color.White.copy(alpha = 0.4f), s * 0.07f, Offset(c.x - s * 0.16f, c.y - s * 0.27f))
    }

    override val desorden = 1f
}
