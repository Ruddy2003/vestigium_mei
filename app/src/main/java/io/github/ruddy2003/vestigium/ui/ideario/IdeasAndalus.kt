package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
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
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Al-Ándalus
// =====================================================================

/**
 * Al-Ándalus: la alacena del cuarto de trabajo de una tejedora de seda en la Córdoba califal, hacia el año 970. Es un interior
 * sin ventana: un muro encalado, con sus desconchones y una mancha de humedad, y abajo un zócalo pintado a la almagra con un
 * lazo blanco sencillo. La luz de día entra por una celosía que queda fuera del cuadro, arriba a la izquierda: su trama de
 * rombos y estrellas cae como luz sobre el muro, el arco y la devanadera. En el muro se abre el arco de herradura de dovelas
 * alternas rojas y blancas, con su alfiz y un ataurique tallado en el yeso de las albanegas; dentro, la alacena empotrada de
 * cedro: arriba el fondo del nicho, encalado y en sombra bajo el arco, con una repisilla sobre modillones de rollos; debajo,
 * las baldas con su canto tallado de tallos y hojas y el umbral de cedro. Una hoja de puerta de lacería queda entreabierta
 * contra la jamba derecha. Encima de la alacena, el punto focal: la devanadera de aspas con una madeja de seda carmesí
 * puesta y otras dos (azafrán e índigo) colgadas de su clavija, con un cabo suelto; a su pie, el tintero de bronce con dos
 * cálamos; el cuenco de verde y manganeso con granadas; el candil de piquera de bronce en su pie alto. En la repisilla, la
 * píxide de marfil tallado y un jarro de verde y manganeso con unas ramas de arrayán. En el muro de la izquierda, unas tijeras
 * de muelle colgadas de un clavo. Al pie, sobre el suelo de ladrillo: la estera de esparto (cortada por el borde izquierdo),
 * un cojín de seda carmesí con una banda de signos bordados (inventados, que no son letra) apoyado en el zócalo, y una cesta
 * de madejas cortada por el borde derecho. Cada tema es, según la semilla, una cajita de taracea de nogal con hueso y ébano
 * y la tapa entreabierta, una arqueta de marfil (o de cedro) con tapa a dos aguas y herrajes de plata, o un envoltorio de
 * seda con bandas de tiraz atado con su cordón; en las tres, el tema en una placa de hueso y las hojas que asoman (más con más fichas). Lo
 * que no tiene tema es una cesta de palma con las hojas sueltas; un hueco libre es la balda con una granada o un ovillo de
 * seda. Al abrir uno, sube. De noche todo se hunde en azul hondo: alumbra solo el candil (el halo en el yeso del nicho, el
 * arco recortado, el filo en el bronce, el marfil y la seda); las primeras baldas quedan encendidas y las de abajo casi se
 * pierden. Por dentro, un alicatado claro de estrellas. Las fichas son hojas con su recuadro de oro y azul y dos medallones.
 */
object IdeasAndalus : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta (sale de la del cuadro, EscenaAndalus)
    /** La cal del muro, su sombra y el yeso hondo del nicho. */
    private val CAL = Color(0xFFF2EADA)
    private val CAL_OSC = Color(0xFFD6C8AE)
    private val NICHO = Color(0xFFCDBB9C)
    /** La almagra del zócalo y de las dovelas; el ladrillo del suelo y de lo que asoma en los desconchones. */
    private val ALMAGRA = Color(0xFFA9482F)
    private val LADRILLO = Color(0xFFC98458)
    /** El cedro de la alacena, su luz y su sombra (los dos de siempre), el nogal y el ébano de la taracea. */
    private val CEDRO = Color(0xFF7A4A2A)
    private val CEDRO_LUZ = Color(0xFFAA7A4A)
    private val CEDRO_OSC = Color(0xFF3E2414)
    private val NOGAL = Color(0xFF4A3020)
    private val EBANO = Color(0xFF2A2018)
    /** El marfil, el hueso de las placas y la plata de los herrajes. */
    private val MARFIL = Color(0xFFEFE6CF)
    private val HUESO = Color(0xFFE6D8B6)
    private val PLATA = Color(0xFFB9B7B0)
    /** La loza de verde y manganeso: el verde de cobre y el pardo morado del manganeso. */
    private val VIDRIADO = Color(0xFF3E8A6C)
    private val MANGANESO = Color(0xFF4A2E2A)
    /** Las sedas: carmesí, azafrán e índigo. */
    private val CARMESI = Color(0xFFB02A3C)
    private val AZAFRAN = Color(0xFFDDA83A)
    private val ANIL = Color(0xFF2F5C88)
    /** El bronce del candil y del tintero; el esparto y la palma; el arrayán; la granada. */
    private val BRONCE = Color(0xFFB7853E)
    private val ESPARTO = Color(0xFFC8A868)
    private val ARRAYAN = Color(0xFF557C48)
    private val GRANADA = Color(0xFFB8403A)
    private val TINTA = Color(0xFF2A1A12)
    /** La noche del cuarto: azul hondo. Y la llama del candil. */
    private val NOCHE = Color(0xFF111528)
    private val LLAMA = Color(0xFFFFC56E)
    /**
     * La placa de hueso de noche, lejos del candil: con [TINTA] da unos 7:1 en el título y 4.6:1 en la cuenta (al 78 %);
     * cerca del candil sube hacia [HUESO] con un poco de llama.
     */
    private val HUESO_LEJOS = Color(0xFFB0A78F)

    /** Los del interior, las fichas y las marcas (los de siempre). */
    private val VERDE = Color(0xFF1E6B5C)
    private val AZUL = Color(0xFF2A4F9E)
    private val OCRE = Color(0xFFC8963A)
    private val NEGRO = Color(0xFF221C18)
    private val TERRACOTA = Color(0xFFB5432A)
    private val LATON = Color(0xFFC9A04A)

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.25f
    override val corona = 0.7f
    override val base = 0.3f
    override val lado = 0.13f
    override val hueco = 0.035f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** El vano del arco (de jamba a jamba), lo alto de su clave por dentro y el grueso del anillo de dovelas, en fracciones del ancho. */
    private const val VANO_L = 0.11f
    private const val VANO_R = 0.89f
    private const val CLAVE = 0.09f
    private const val ANILLO = 0.04f
    /** Donde pisan las cosas de encima de la alacena (la tabla de remate) y donde está la repisilla del nicho. */
    private const val TABLA = 0.7f
    private const val REPISA = 0.42f
    /** En la base: el umbral de cedro, lo alto del zócalo y lo hondo del suelo, en fracciones de su alto. */
    private const val UMBRAL = 0.2f
    private const val ZOCALO = 0.58f
    /** El piso de cada hueco (la balda) y cuánto sube su contenido al abrirlo, en fracción de su alto. */
    private const val PISO = 0.9f
    private const val SALIDA = 0.07f

    /** El radio del arco de herradura y la altura de su centro, en fracciones del ancho. */
    private val RADIO = (VANO_R - VANO_L) / 2f / 0.906f
    private val CENTRO_Y = CLAVE + RADIO

    // ---------------------------------------------------------------- la luz y la noche

    /** La llama del candil (la punta de la piquera), en fracciones del ancho. */
    private val LUZ = Offset(0.782f, 0.574f)

    /** Cuánto le llega del candil a un punto [p] (en fracciones del ancho): cae con la distancia; lo lejano, casi nada. */
    private fun luz(p: Offset): Float {
        val dx = p.x - LUZ.x
        val dy = (p.y - LUZ.y) * (if (p.y > LUZ.y) 0.8f else 1f)
        val radial = 1.4f * kotlin.math.exp(-4.4f * sqrt(dx * dx + dy * dy))
        return maxOf(radial, luzCuerpo(p)).coerceIn(0.03f, 1f)
    }

    /**
     * La luz que el candil echa hacia abajo sobre las baldas: cae de fila en fila (la primera, encendida; la segunda, a medias;
     * la tercera, tenue; la cuarta y la quinta, casi perdidas), algo menos en la columna de la izquierda. Los puntos son los
     * centros de las filas con 9 temas, en fracciones del ancho.
     */
    private val CAIDA = listOf(0.72f to 0.95f, 0.876f to 0.82f, 1.193f to 0.5f, 1.51f to 0.24f, 1.827f to 0.085f, 2.144f to 0.04f, 2.7f to 0.03f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.6f * (abs(p.x - LUZ.x) - 0.08f).coerceAtLeast(0f)).coerceIn(0.66f, 1f)
        return k * lado
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire azul de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.14f + NOCHE.red * 0.8f, c.green * 0.14f + NOCHE.green * 0.8f, c.blue * 0.14f + NOCHE.blue * 0.8f, c.alpha)

    /** Cómo se pinta algo según dónde está: de día tal cual (con [dia], un poco más claro); de noche, según lo que le llega del candil. */
    private class Tono(val o: Boolean, val k: Float, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.9f))
        return mezcla(v, LLAMA, 0.16f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, el azul de la noche. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la llama. */
    private fun Tono.b(a: Float): Color = if (!o) luzMueble(a) else LLAMA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f))

    /** Lo que el mueble que se está pintando sabe: si es de noche y su ancho (para pasar fracciones a píxeles). */
    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset, dia: Float = 0f): Tono = Tono(esNoche, if (esNoche) luz(p / anchoW) else 0f, dia)
    /** El tono en un punto dado en fracciones del ancho. */
    private fun tf(x: Float, y: Float): Tono = Tono(esNoche, if (esNoche) luz(Offset(x, y)) else 0f)
    /** Un punto en fracciones del ancho, en píxeles. */
    private fun P(x: Float, y: Float) = Offset(x * anchoW, y * anchoW)
    private fun X(f: Float) = f * anchoW

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /** La sombra de algo en el muro: de día corrida a la derecha y abajo; de noche, hacia fuera del candil y solo cerca de él. */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, a: Float = 0.16f) {
        val w = anchoW
        if (!esNoche) translate(w * 0.02f, w * 0.012f) { drawPath(p, sombraMueble(a)) }
        else {
            val k = luz(desde / w)
            if (k > 0.15f) {
                val dx = (desde.x / w - LUZ.x).coerceIn(-0.3f, 0.3f) / 0.3f
                translate(w * 0.035f * dx, w * 0.012f) { drawPath(p, NOCHE.copy(alpha = (0.6f * k).coerceAtMost(0.45f))) }
            }
        }
    }

    /** La sombra de contacto de lo que pisa una tabla o el suelo, corrida a la derecha. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float) =
        drawOval(if (esNoche) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /** La luz de un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
    private fun DrawScope.cilindro(r: Rect, t: Tono, fuerza: Float = 1f, der: Boolean = false) {
        fun x(f: Float, a: Float) = if (der) r.right - r.width * (f + a) else r.left + r.width * f
        drawRect(t.b(0.2f * fuerza), Offset(x(0f, 0.3f), r.top), Size(r.width * 0.3f, r.height))
        drawRect(t.b(0.34f * fuerza), Offset(x(0.15f, 0.08f), r.top), Size(r.width * 0.08f, r.height))
        drawRect(t.s(0.16f * fuerza), Offset(x(0.58f, 0.42f), r.top), Size(r.width * 0.42f, r.height))
        drawRect(t.s(0.2f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
    }

    /** El filo cálido del candil en un recipiente, de noche: en su canto de arriba y en el costado que mira a la luz. */
    private fun DrawScope.filo(r: Rect, t: Tono, der: Boolean) {
        if (!t.o || t.k < 0.06f) return
        val u = density
        drawRect(LLAMA.copy(alpha = (0.85f * t.k).coerceAtMost(0.7f)), r.topLeft, Size(r.width, 1.4f * u))
        val x = if (der) r.right - 1.3f * u else r.left
        drawRect(LLAMA.copy(alpha = (0.6f * t.k).coerceAtMost(0.5f)), Offset(x, r.top), Size(1.3f * u, r.height))
    }

    /** El destello de la llama en el bronce, el marfil o la loza (de día, la luz crema). */
    private fun DrawScope.destello(c: Offset, r: Float, t: Tono) {
        if (t.o) { if (t.k > 0.06f) { drawCircle(LLAMA.copy(alpha = (0.35f * t.k).coerceAtMost(0.3f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(luzMueble(0.8f), r, c)
    }

    /** Una hoja de arrayán o de ataurique: un óvalo de punta con su lado en sombra, girado [giro] grados. */
    private fun DrawScope.hojita(c: Offset, largo: Float, giro: Float, col: Color, t: Tono) {
        val a = largo * 0.26f; val l = largo / 2
        rotate(giro, c) {
            val f = Path().apply {
                moveTo(c.x, c.y + l)
                cubicTo(c.x + a * 1.3f, c.y + l * 0.4f, c.x + a, c.y - l * 0.5f, c.x, c.y - l)
                cubicTo(c.x - a, c.y - l * 0.5f, c.x - a * 1.3f, c.y + l * 0.4f, c.x, c.y + l)
                close()
            }
            drawPath(f, col)
            clipPath(f) { drawRect(t.s(0.22f), Offset(c.x, c.y - l), Size(a * 1.4f, largo)) }
        }
    }

    /** Una granada: la bola rojiza con su luz y su sombra, la corona de cinco puntas y el brillo. */
    private fun DrawScope.granada(c: Offset, r: Float, t: Tono, giro: Float = 0f) {
        drawCircle(t.c(GRANADA), r, c)
        clipPath(Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }) {
            drawCircle(t.c(mezcla(GRANADA, AZAFRAN, 0.35f)), r * 0.7f, c - Offset(r * 0.35f, r * 0.35f))
            drawCircle(t.s(0.28f), r * 1.1f, c + Offset(r * 0.6f, r * 0.55f))
        }
        val tc = c + Offset(r * 0.15f * cos(Math.toRadians(giro.toDouble() - 90.0)).toFloat(), -r * 0.92f)
        drawPath(estrella(tc, r * 0.32f, 5, 0.45f, giro - 90f), t.c(mezcla(GRANADA, MANGANESO, 0.45f)))
        drawCircle(t.b(0.55f), r * 0.18f, c - Offset(r * 0.42f, r * 0.38f))
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val o = oscuro
        esNoche = o
        val w = size.width; val alto = size.height
        anchoW = w
        val b = d.base; val hb = b.height

        // ---- el muro encalado, el zócalo de almagra y el suelo de ladrillo
        muro(b)
        zocalo(b)
        suelo(b, alto)

        // ---- el nicho bajo el arco y lo que hay dentro, de atrás adelante
        val vano = arcoHerradura(Rect(X(VANO_L), X(CLAVE), X(VANO_R), b.top + hb * UMBRAL))
        clipPath(vano) {
            nicho(b)
            repisa()
            jarro(P(0.785f, REPISA))
            pixide(P(0.64f, REPISA))
            devanadera()
            tintero(P(0.425f, TABLA))
            cuenco(P(0.555f, TABLA))
            candil(P(0.705f, TABLA))
            alacena(d, b)
        }

        // ---- el arco: el anillo de dovelas, el alfiz, el ataurique de las albanegas y las impostas
        arco()
        // ---- lo que hay en el muro: las tijeras de muelle
        tijeras(P(0.052f, 0.98f))
        // ---- la hoja de puerta de lacería, entreabierta contra la jamba derecha
        puerta()

        // ---- al pie: la estera, el cojín y la cesta de madejas
        estera(b, alto)
        cojin(b)
        cesta(b, alto)

        // ---- la placa de «y N más»: una placa de hueso clavada en el umbral
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            drawRect(t.s(0.3f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size)
            drawRect(if (o) HUESO_LEJOS else HUESO, r.topLeft, r.size)
            drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1f * u))
            drawRect(t.c(MANGANESO).copy(alpha = 0.5f), Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5f * u, r.height - 5f * u), style = Stroke(0.7f * u))
            listOf(r.left + 1.6f * u, r.right - 1.6f * u).forEach { x -> drawCircle(t.c(PLATA), 1f * u, Offset(x, r.center.y)) }
        }

        // ---- la luz: de día, la trama de la celosía; de noche, el candil
        if (!o) celosia()
        else {
            val llama = LUZ * w
            mancha(llama + Offset(0f, w * 0.02f), w * 0.5f, LLAMA.copy(alpha = 0.1f))
            mancha(llama, w * 0.2f, LLAMA.copy(alpha = 0.2f))
            mancha(llama, w * 0.05f, LLAMA.copy(alpha = 0.5f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    // ---------------------------------------------------------------- el muro

    /** El muro encalado: la luz de arriba a la izquierda (de noche, el halo del candil), pinceladas, grietas, desconchones y una mancha de humedad. */
    private fun DrawScope.muro(b: Rect) {
        val u = density
        val o = esNoche
        val w = anchoW
        val hasta = b.top + b.height * 0.02f
        if (!o) {
            drawRect(Brush.linearGradient(listOf(mezcla(CAL, LUZ_MUEBLE, 0.5f), CAL, mezcla(CAL, CAL_OSC, 0.75f)), Offset.Zero, Offset(w, hasta)), Offset.Zero, Size(w, hasta))
        } else {
            fun k(dd: Float) = Tono(true, luz(LUZ + Offset(dd, 0f))).c(CAL)
            drawRect(Brush.radialGradient(0f to k(0.03f), 0.1f to k(0.12f), 0.22f to k(0.27f), 0.42f to k(0.5f), 0.7f to k(0.85f), 1f to k(1.3f),
                center = LUZ * w, radius = w * 1.25f), Offset.Zero, Size(w, hasta))
        }
        pinceladas(Rect(0f, 0f, w, hasta), if (o) NOCHE else CAL_OSC, 5001, 70, u, 0.12f)
        pinceladas(Rect(0f, 0f, w, hasta), if (o) NOCHE else LUZ_MUEBLE, 5002, 30, u, 0.14f)
        // La mancha de humedad: sube del zócalo por el muro de la izquierda y de la derecha, con su cerco.
        // El borde de arriba es una línea de marea blanda: sube y baja poco, con lóbulos redondos, nunca en picos.
        listOf(Triple(-0.02f, 0.14f, 5010), Triple(0.88f, 0.15f, 5011)).forEach { (x0, ancho, s) ->
            val t = tn(P(x0 + ancho / 2, b.top / w - 0.1f))
            listOf(1f, 0.62f).forEachIndexed { capa, esc ->
                val p = Path().apply {
                    moveTo(X(x0), hasta)
                    val pasos = 5
                    var prev = Offset(X(x0), hasta - X(0.05f) * esc)
                    lineTo(prev.x, prev.y)
                    for (j in 1..pasos) {
                        val f = j / pasos.toFloat()
                        val y = hasta - X(0.07f + 0.05f * azar(s + capa, j)) * esc * (1f - 0.5f * abs(f - 0.45f))
                        val q = Offset(X(x0 + ancho * f), y)
                        cubicTo(prev.x + (q.x - prev.x) * 0.3f, prev.y - X(0.006f) * esc, prev.x + (q.x - prev.x) * 0.7f, q.y - X(0.006f) * esc, q.x, q.y)
                        prev = q
                    }
                    lineTo(X(x0 + ancho), hasta); close()
                }
                drawPath(p, t.c(mezcla(CAL_OSC, ESPARTO, 0.3f + 0.15f * capa)).copy(alpha = 0.3f))
                if (capa == 0) drawPath(p, t.c(mezcla(CAL_OSC, MANGANESO, 0.2f)).copy(alpha = 0.3f), style = Stroke(1f * u))
            }
        }
        // Grietas finas del enlucido.
        listOf(Offset(0.02f, 0.3f), Offset(0.93f, 0.08f), Offset(0.95f, 1.75f), Offset(0.04f, 1.6f)).forEachIndexed { i, f ->
            val p0 = P(f.x, f.y)
            val t = tn(p0)
            val g = Path().apply {
                moveTo(p0.x, p0.y)
                var q = p0
                for (j in 0 until 4) { q += Offset(w * (0.006f + 0.01f * azar(5020 + i, j)) * (if (j % 2 == 0) 1f else -0.6f), w * (0.012f + 0.012f * azar(5030 + i, j))); lineTo(q.x, q.y) }
            }
            drawPath(g, t.s(0.3f), style = Stroke(0.7f * u))
            translate(0.8f * u, 0.8f * u) { drawPath(g, t.b(0.3f), style = Stroke(0.5f * u)) }
        }
        // Los desconchones: el enlucido caído deja ver el ladrillo, con el borde de cal en sombra arriba a la izquierda.
        listOf(Triple(0.045f, 1.36f, 0.032f), Triple(0.95f, 0.3f, 0.026f), Triple(0.95f, 2.0f, 0.03f)).forEachIndexed { i, (fx, fy, r) ->
            val c = P(fx, fy)
            val t = tn(c)
            val forma = Path().apply {
                for (j in 0 until 9) {
                    val a = j / 9f * 6.283f
                    val rr = X(r) * (0.55f + 0.45f * azar(5040 + i, j)) * (if (j % 2 == 0) 1f else 0.7f)
                    val p = Offset(c.x + rr * cos(a) * 1.25f, c.y + rr * sin(a) * 0.8f)
                    if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
                close()
            }
            // Debajo de la cal, el revoque pardo, un poco hundido: su borde de arriba hace sombra; el de abajo apenas brilla.
            drawPath(forma, t.c(mezcla(CAL_OSC, ESPARTO, 0.3f)).copy(alpha = 0.75f))
            clipPath(forma) {
                translate(0f, X(r * 0.18f)) { drawPath(forma, t.c(mezcla(CAL_OSC, LADRILLO, 0.2f)).copy(alpha = 0.5f)) }
                translate(-1.2f * u, -1.2f * u) { drawPath(forma, t.s(0.25f), style = Stroke(1.6f * u)) }
            }
        }
    }

    /** El zócalo pintado a la almagra: la franja roja, sus filetes blancos y el lazo blanco de dos cintas; gastado abajo. */
    private fun DrawScope.zocalo(b: Rect) {
        val u = density
        val w = anchoW
        val hb = b.height
        val z = Rect(0f, b.top, w, b.top + hb * ZOCALO)
        val t = tn(Offset(w * 0.5f, z.center.y))
        drawRect(t.c(ALMAGRA), z.topLeft, z.size)
        if (!esNoche) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.14f), Color.Transparent, sombraMueble(0.14f)), 0f, w), z.topLeft, z.size)
        else drawRect(Brush.horizontalGradient(listOf(NOCHE.copy(alpha = 0.5f), Color.Transparent, NOCHE.copy(alpha = 0.15f)), 0f, w), z.topLeft, z.size)
        pinceladas(z, if (esNoche) NOCHE else mezcla(ALMAGRA, MANGANESO, 0.4f), 5101, 40, u, 0.14f)
        pinceladas(z, if (esNoche) NOCHE else mezcla(ALMAGRA, LADRILLO, 0.5f), 5102, 20, u, 0.18f)
        // Los filetes blancos de arriba y de abajo.
        val blanco = t.c(mezcla(CAL, ESPARTO, 0.15f))
        val fa = z.top + hb * 0.25f; val fb = z.bottom - hb * 0.07f
        drawRect(blanco, Offset(0f, fa), Size(w, hb * 0.02f))
        drawRect(blanco, Offset(0f, fb), Size(w, hb * 0.02f))
        // El lazo: dos cintas onduladas que se cruzan y un punto en cada ojo.
        val cy = (fa + fb) / 2 + hb * 0.01f; val amp = (fb - fa) * 0.32f
        val paso = w * 0.11f
        listOf(0f, Math.PI.toFloat()).forEach { fase ->
            val p = Path()
            var x = -paso
            var first = true
            while (x <= w + paso) {
                val y = cy + amp * sin(x / paso * Math.PI.toFloat() + fase)
                if (first) { p.moveTo(x, y); first = false } else p.lineTo(x, y)
                x += paso / 10f
            }
            translate(0.8f * u, 1f * u) { drawPath(p, t.s(0.25f), style = Stroke(1.6f * u)) }
            drawPath(p, blanco, style = Stroke(1.6f * u))
        }
        var x = paso / 2
        while (x < w) { drawCircle(blanco, 1.7f * u, Offset(x, cy)); x += paso }
        // El desgaste: la pintura rozada abajo, y la sombra del rincón.
        for (k in 0 until 9) {
            val px = w * azar(5110, k)
            drawOval(t.c(mezcla(ALMAGRA, CAL, 0.4f)).copy(alpha = 0.35f), Offset(px, z.bottom - hb * (0.06f + 0.08f * azar(5111, k))), Size(w * (0.03f + 0.05f * azar(5112, k)), hb * 0.04f))
        }
        drawRect(t.s(0.25f), Offset(0f, z.top), Size(w, 1.4f * u))
    }

    /** El suelo de ladrillo a la palma, con sus juntas que se abren hacia quien mira y la sombra del rincón. */
    private fun DrawScope.suelo(b: Rect, alto: Float) {
        val u = density
        val w = anchoW
        val s0 = b.top + b.height * ZOCALO
        val t = tn(Offset(w * 0.5f, alto))
        drawRect(t.c(mezcla(LADRILLO, CAL_OSC, 0.42f)), Offset(0f, s0), Size(w, alto - s0))
        var y = s0; var fila = 0
        while (y < alto) {
            val alto1 = (alto - s0) * (0.16f + 0.05f * fila)
            drawLine(t.s(0.35f), Offset(0f, y), Offset(w, y), 1.2f * u)
            drawLine(t.b(0.2f), Offset(0f, y + 1.2f * u), Offset(w, y + 1.2f * u), 0.6f * u)
            val largo = w * (0.12f + 0.025f * fila)
            var x = if (fila % 2 == 0) 0f else largo / 2
            while (x < w) { drawLine(t.s(0.35f), Offset(x, y), Offset(x + (x - w / 2) * 0.04f, y + alto1), 1f * u); x += largo }
            y += alto1; fila++
        }
        pinceladas(Rect(0f, s0, w, alto), if (esNoche) NOCHE else CAL_OSC, 5120, 18, u, 0.14f)
        drawRect(t.s(0.38f), Offset(0f, s0), Size(w, 2.5f * u))
        drawRect(t.s(0.16f), Offset(0f, s0 + 2.5f * u), Size(w, 5f * u))
    }

    // ---------------------------------------------------------------- el nicho y la alacena

    /**
     * El fondo del nicho: el yeso hondo, con la sombra que el arco echa en su lado izquierdo y arriba (la luz entra de arriba a
     * la izquierda) y una franja de luz donde la deja pasar.
     */
    private fun DrawScope.nicho(b: Rect) {
        val u = density
        val w = anchoW
        val r = Rect(X(VANO_L) - X(0.03f), X(CLAVE), X(VANO_R) + X(0.03f), b.top)
        if (!esNoche) {
            drawRect(mezcla(NICHO, CEDRO_OSC, 0.12f), r.topLeft, r.size)
            // La parte que recibe la luz: el mismo arco corrido abajo a la derecha.
            val lit = arcoHerradura(Rect(X(VANO_L + 0.06f), X(CLAVE + 0.07f), X(VANO_R + 0.06f), b.top))
            drawPath(lit, mezcla(NICHO, LUZ_MUEBLE, 0.2f))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombraMueble(0.22f)), X(0.3f), X(TABLA)), Offset(r.left, X(0.3f)), Size(r.width, X(TABLA - 0.3f)))
        } else {
            fun k(dd: Float) = Tono(true, luz(LUZ + Offset(dd, 0f))).c(NICHO)
            drawRect(Brush.radialGradient(0f to k(0.02f), 0.15f to k(0.1f), 0.35f to k(0.24f), 0.6f to k(0.45f), 1f to k(0.8f),
                center = LUZ * w, radius = w * 0.8f), r.topLeft, Size(r.width, X(TABLA) - r.top))
        }
        pinceladas(Rect(r.left, r.top, r.right, X(TABLA)), if (esNoche) NOCHE else CEDRO_OSC, 5201, 26, u, 0.08f)
        // El canto del arco por dentro (el intradós) arriba: una franja en sombra.
        val intra = arcoHerradura(Rect(X(VANO_L), X(CLAVE), X(VANO_R), b.top))
        val adentro = arcoHerradura(Rect(X(VANO_L + 0.025f), X(CLAVE + 0.022f), X(VANO_R - 0.025f), b.top))
        val franja = Path().apply { op(intra, adentro, PathOperation.Difference) }
        clipRect(0f, 0f, w, X(TABLA)) {
            drawPath(franja, if (esNoche) NOCHE.copy(alpha = 0.35f) else sombraMueble(0.2f))
            clipRect(X(0.5f), 0f, w, X(TABLA)) { drawPath(franja, tf(0.8f, 0.4f).b(0.2f)) }
        }
    }

    /** La repisilla del nicho, a la derecha: una tabla de cedro sobre dos modillones de rollos. */
    private fun DrawScope.repisa() {
        val u = density
        val y = X(REPISA)
        val t = tf(0.75f, REPISA)
        val r = Rect(X(0.55f), y, X(0.93f), y + X(0.016f))
        drawRect(t.s(0.25f), Offset(r.left + X(0.012f), r.bottom), Size(r.width, X(0.02f)))
        listOf(0.6f, 0.84f).forEach { fx ->
            val m = Rect(X(fx - 0.018f), r.bottom, X(fx + 0.018f), r.bottom + X(0.04f))
            drawRect(t.s(0.25f), Offset(m.left + X(0.01f), m.top + X(0.004f)), m.size)
            // Los rollos: tres círculos que se achican hacia abajo, con su luz.
            for (j in 0 until 3) {
                val rr = X(0.016f) * (1f - 0.22f * j)
                val c = Offset(m.center.x - X(0.004f) * j, m.top + X(0.007f) + X(0.012f) * j)
                drawRect(t.c(CEDRO), Offset(m.left, c.y - rr * 0.6f), Size(m.width * (1f - 0.18f * j), rr * 1.2f))
                drawCircle(t.c(CEDRO_LUZ), rr * 0.6f, Offset(m.left + rr * 0.3f, c.y))
                drawCircle(t.s(0.25f), rr * 0.3f, Offset(m.left + rr * 0.4f, c.y + rr * 0.15f))
            }
        }
        drawRect(t.c(CEDRO), r.topLeft, r.size)
        drawRect(t.c(CEDRO_LUZ), Offset(r.left, r.top - X(0.008f)), Size(r.width, X(0.008f)))
        drawRect(t.b(0.45f), Offset(r.left, r.top - X(0.008f)), Size(r.width, 0.9f * u))
        drawRect(t.s(0.35f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
        vetas(r, (if (esNoche) NOCHE else CEDRO_OSC).copy(alpha = 0.4f), 5210, 0.6f, grosor = 0.5f)
    }

    /**
     * El cuerpo de la alacena: la tabla de remate (vista un poco desde arriba, con su canto), los largueros y el parteluz de
     * cedro, las baldas con su canto tallado de tallos y hojas, el derrame de las jambas y el umbral de abajo.
     */
    private fun DrawScope.alacena(d: Disposicion, b: Rect) {
        val u = density
        val o = esNoche
        val w = anchoW
        val hb = b.height
        val x0 = X(VANO_L); val x1 = X(VANO_R)
        val arriba = X(TABLA)
        val abajo = b.top + hb * UMBRAL
        // El frente de cedro (los huecos los pinta cada compartimento encima).
        val cuerpo = Rect(x0, arriba, x1, b.top)
        if (!o) drawRect(CEDRO, cuerpo.topLeft, cuerpo.size)
        else drawRect(Brush.verticalGradient(List(6) { i -> tn(Offset(w * 0.6f, cuerpo.top + cuerpo.height * i / 5f)).c(CEDRO) }, cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, (if (o) NOCHE else CEDRO_OSC).copy(alpha = 0.35f), 5301, 0.5f, vertical = true, grosor = 0.6f, ondulacion = 0.5f)
        // El parteluz del medio.
        val pm = Rect(X(lado + (1f - 2 * lado - hueco) / 2), cuerpo.top, X(lado + (1f - 2 * lado - hueco) / 2 + hueco), cuerpo.bottom)
        for (k in 0 until 8) {
            val y0 = cuerpo.top + cuerpo.height * k / 8f
            val t = tn(Offset(pm.center.x, y0 + cuerpo.height / 16f))
            drawRect(t.b(0.3f), Offset(pm.left, y0), Size(1.2f * u, cuerpo.height / 8f))
            drawRect(t.s(0.25f), Offset(pm.right - 1.4f * u, y0), Size(1.4f * u, cuerpo.height / 8f))
        }
        // Las baldas: su canto tallado, una por cada separación entre filas, y la de abajo.
        val ys = buildList {
            d.filas.forEachIndexed { i, f -> add(f.bottom to (if (i < d.filas.size - 1) d.filas[i + 1].top else f.bottom + d.separacion)) }
        }
        ys.forEachIndexed { i, (ya, yb) ->
            val t = tn(Offset(w * 0.6f, ya))
            val r = Rect(x0, ya, x1, yb)
            drawRect(t.c(mezcla(CEDRO, CEDRO_LUZ, 0.35f)), r.topLeft, r.size)
            drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1f * u))
            drawRect(t.s(0.35f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
            talla(r, t, 5310 + i)
        }
        // Los largueros de los lados, con el derrame de la jamba: el de la izquierda en sombra, el de la derecha con luz.
        for (k in 0 until 10) {
            val y0 = arriba + (b.top - arriba) * k / 10f
            val yc = y0 + (b.top - arriba) / 20f
            val tl = tn(Offset(x0, yc)); val tr = tn(Offset(x1, yc))
            drawRect(tl.c(CEDRO_OSC), Offset(x0, y0), Size(X(0.014f), (b.top - arriba) / 10f + 0.5f))
            drawRect(tl.s(0.3f), Offset(x0 + X(0.014f), y0), Size(X(0.008f), (b.top - arriba) / 10f + 0.5f))
            drawRect(tr.c(mezcla(CAL_OSC, CEDRO_LUZ, 0.3f)), Offset(x1 - X(0.012f), y0), Size(X(0.012f), (b.top - arriba) / 10f + 0.5f))
            drawRect(tr.b(0.4f), Offset(x1 - X(0.012f), y0), Size(1f * u, (b.top - arriba) / 10f + 0.5f))
        }
        // La tabla de remate: la cara de arriba (donde pisan las cosas), su canto y la talla.
        val tt = tn(P(0.6f, TABLA))
        val sup = Rect(x0 - X(0.01f), arriba - X(0.016f), x1 + X(0.01f), arriba)
        drawRect(tt.c(mezcla(CEDRO_LUZ, CEDRO, 0.2f)), sup.topLeft, sup.size)
        vetas(sup, (if (o) NOCHE else CEDRO_OSC).copy(alpha = 0.3f), 5320, 0.8f, grosor = 0.5f)
        drawRect(tt.s(0.25f), sup.topLeft, Size(sup.width, 1.2f * u))
        val canto = Rect(sup.left, arriba, sup.right, d.filas.first().top - X(0.004f))
        drawRect(tt.c(CEDRO), canto.topLeft, canto.size)
        drawRect(tt.b(0.6f), canto.topLeft, Size(canto.width, 1.2f * u))
        talla(canto, tt, 5330)
        drawRect(tt.s(0.35f), Offset(canto.left, canto.bottom), Size(canto.width, 2f * u))
        // El umbral de cedro, abajo: un tablón grueso que sale del muro, con su sombra en el zócalo.
        val tu = tn(Offset(w * 0.5f, b.top))
        val um = Rect(x0 - X(0.025f), b.top, x1 + X(0.025f), abajo)
        drawRect(tu.s(0.3f), Offset(um.left + X(0.012f), um.bottom), Size(um.width, hb * 0.07f))
        drawRect(tu.c(CEDRO), um.topLeft, um.size)
        vetas(um, (if (o) NOCHE else CEDRO_OSC).copy(alpha = 0.4f), 5340, 0.7f, grosor = 0.6f)
        drawRect(tu.c(CEDRO_LUZ), um.topLeft, Size(um.width, um.height * 0.2f))
        drawRect(tu.b(0.55f), um.topLeft, Size(um.width, 1.1f * u))
        drawRect(tu.s(0.3f), Offset(um.left, um.top + um.height * 0.2f), Size(um.width, 1.2f * u))
        drawRect(tu.s(0.3f), Offset(um.right - X(0.012f), um.top), Size(X(0.012f), um.height))
        // Las rozaduras del canto del umbral, donde se pisa.
        for (k in 0 until 6) {
            val x = um.left + um.width * (0.1f + 0.8f * azar(5341, k))
            drawLine(tu.b(0.35f), Offset(x, um.top + um.height * 0.3f), Offset(x + X(0.03f), um.top + um.height * (0.35f + 0.2f * azar(5342, k))), 0.7f * u)
        }
    }

    /** La talla del canto de una balda: un tallo ondulado que corre de lado a lado, con una hoja en cada vuelta (ataurique sencillo). */
    private fun DrawScope.talla(r: Rect, t: Tono, s: Int) {
        val u = density
        if (r.height < 3f * u) return
        val cy = r.center.y; val amp = r.height * 0.22f
        val paso = X(0.045f)
        val p = Path()
        var x = r.left + X(0.008f)
        var first = true
        while (x <= r.right) {
            val y = cy + amp * sin((x - r.left) / paso * Math.PI.toFloat())
            if (first) { p.moveTo(x, y); first = false } else p.lineTo(x, y)
            x += paso / 6f
        }
        translate(0.7f * u, 0.8f * u) { drawPath(p, t.s(0.4f), style = Stroke(0.9f * u)) }
        drawPath(p, t.b(0.35f), style = Stroke(0.7f * u))
        var k = 0
        x = r.left + X(0.008f) + paso / 2
        while (x < r.right - X(0.01f)) {
            val arriba = k % 2 == 0
            val c = Offset(x + paso * 0.15f, cy + (if (arriba) -amp * 1.2f else amp * 1.2f))
            val giro = if (arriba) 40f else 140f
            translate(0.7f * u, 0.8f * u) { hojita(c, r.height * 0.45f, giro, t.s(0.35f), t) }
            hojita(c, r.height * 0.45f, giro, t.c(mezcla(CEDRO, CEDRO_LUZ, 0.6f)), t)
            x += paso; k++
        }
    }

    // ---------------------------------------------------------------- el arco

    /**
     * El arco de herradura: el anillo de dovelas alternas de almagra y cal (cada una con su lado de luz y de sombra), el alfiz
     * que lo enmarca con su moldura, el ataurique tallado en las dos albanegas y las impostas donde arranca.
     */
    private fun DrawScope.arco() {
        val u = density
        val w = anchoW
        val o = esNoche
        val radO = RADIO + ANILLO
        val cy = X(CENTRO_Y); val cx = w / 2
        val ext = arcoHerradura(Rect(w / 2 - X(radO * 0.906f), X(CENTRO_Y - radO), w / 2 + X(radO * 0.906f), X(TABLA + 0.03f)))
        val int = arcoHerradura(Rect(X(VANO_L), X(CLAVE), X(VANO_R), X(TABLA + 0.03f)))
        val anillo = Path().apply { op(ext, int, PathOperation.Difference) }
        clipRect(0f, 0f, w, X(CENTRO_Y + radO * 0.423f + 0.002f)) {
            clipPath(anillo) {
                val n = 19
                for (j in 0 until n) {
                    val a0 = 155f + j * (230f / n)
                    val am = Math.toRadians((a0 + 230f / n / 2).toDouble())
                    val pm = Offset(cx + X(RADIO + ANILLO / 2) * cos(am).toFloat(), cy + X(RADIO + ANILLO / 2) * sin(am).toFloat())
                    val t = tn(pm)
                    val roja = j % 2 == 0
                    val col = if (roja) ALMAGRA else mezcla(CAL, ESPARTO, 0.12f)
                    drawArc(t.c(col), a0, 230f / n, true, Offset(cx - X(radO * 1.1f), cy - X(radO * 1.1f)), Size(X(radO * 2.2f), X(radO * 2.2f)))
                    // De día, la luz arriba a la izquierda: las dovelas de ese lado más claras, las de abajo a la derecha más hondas.
                    val luzAng = cos(am - Math.toRadians(225.0)).toFloat()
                    if (!o) drawArc(if (luzAng > 0f) luzMueble(0.18f * luzAng) else sombraMueble(-0.16f * luzAng), a0, 230f / n, true, Offset(cx - X(radO * 1.1f), cy - X(radO * 1.1f)), Size(X(radO * 2.2f), X(radO * 2.2f)))
                    // La junta.
                    val ja = Math.toRadians(a0.toDouble())
                    drawLine(t.s(0.35f), Offset(cx + X(RADIO) * cos(ja).toFloat(), cy + X(RADIO) * sin(ja).toFloat()), Offset(cx + X(radO) * cos(ja).toFloat(), cy + X(radO) * sin(ja).toFloat()), 1f * u)
                }
                pinceladas(Rect(0f, X(CENTRO_Y - radO), w, X(TABLA)), if (o) NOCHE else MANGANESO, 5401, 30, u, 0.08f)
            }
            // El canto de dentro (luz en el lado derecho, que mira a la celosía) y la moldura de fuera.
            drawPath(int, tf(0.5f, 0.3f).s(0.4f), style = Stroke(1.4f * u))
            clipRect(w / 2, 0f, w, X(TABLA)) { drawPath(int, if (o) LLAMA.copy(alpha = 0.4f) else luzMueble(0.45f), style = Stroke(1.2f * u)) }
            drawPath(ext, tf(0.5f, 0.1f).s(0.3f), style = Stroke(1.2f * u))
            translate(1f * u, 1f * u) { drawPath(ext, tf(0.5f, 0.1f).b(0.4f), style = Stroke(0.8f * u)) }
        }
        // El alfiz: la moldura rectangular que enmarca el arco, con su luz arriba a la izquierda y su sombra abajo a la derecha.
        val al = Rect(X(0.008f), X(0.012f), X(0.992f), X(CENTRO_Y + radO * 0.423f + 0.012f))
        val g = X(0.014f)
        listOf(Rect(al.left, al.top, al.right, al.top + g), Rect(al.left, al.top, al.left + g, al.bottom), Rect(al.right - g, al.top, al.right, al.bottom)).forEachIndexed { i, r ->
            // De noche, la jamba tapa el candil: al alfiz le llega la mitad.
            val t0 = tn(r.center); val t = Tono(t0.o, t0.k * 0.5f)
            drawRect(t.s(0.25f), Offset(r.left + 1.5f * u, r.top + 1.5f * u), r.size)
            drawRect(t.c(mezcla(CAL, CAL_OSC, 0.25f)), r.topLeft, r.size)
            drawRect(t.b(0.55f), r.topLeft, Size(r.width, 1f * u))
            drawRect(t.b(0.4f), r.topLeft, Size(1f * u, r.height))
            drawRect(t.s(0.3f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
            drawRect(t.s(0.3f), Offset(r.right - 1f * u, r.top), Size(1f * u, r.height))
            // Una cuerda de puntos en la moldura.
            if (i == 0) { var x = r.left + g; while (x < r.right - g) { drawCircle(t.s(0.25f), 0.9f * u, Offset(x, r.center.y)); x += X(0.018f) } }
        }
        // El ataurique de las albanegas: un tallo en espiral con sus hojas, tallado en el yeso (luz arriba a la izquierda).
        listOf(false, true).forEach { der -> ataurique(der) }
        // Las impostas: dos bloques con su moldura donde arranca el arco.
        listOf(VANO_L - 0.055f to VANO_L + 0.006f, VANO_R - 0.006f to VANO_R + 0.055f).forEach { (a, z) ->
            val y = X(CENTRO_Y + RADIO * 0.423f)
            val r = Rect(X(a), y - X(0.002f), X(z), y + X(0.018f))
            val t = tn(r.center)
            drawRect(t.s(0.3f), Offset(r.left + X(0.008f), r.bottom), Size(r.width, X(0.014f)))
            drawRect(t.c(mezcla(CAL, CAL_OSC, 0.3f)), r.topLeft, r.size)
            drawRect(t.b(0.6f), r.topLeft, Size(r.width, 1.2f * u))
            drawRect(t.s(0.25f), Offset(r.left, r.top + r.height * 0.5f), Size(r.width, 1f * u))
            drawRect(t.s(0.35f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
        }
    }

    /** El ataurique de una albanega: un tallo que se enrosca hacia la esquina, con hojas de palmeta, en relieve. */
    private fun DrawScope.ataurique(der: Boolean) {
        val u = density
        fun f(x: Float) = if (der) 1f - x else x
        val t = tn(P(f(0.1f), 0.1f))
        val base = mezcla(CAL, CAL_OSC, 0.18f)
        val tallo = Path().apply {
            moveTo(X(f(0.06f)), X(0.27f))
            cubicTo(X(f(0.05f)), X(0.14f), X(f(0.12f)), X(0.05f), X(f(0.22f)), X(0.05f))
            cubicTo(X(f(0.28f)), X(0.05f), X(f(0.3f)), X(0.1f), X(f(0.26f)), X(0.11f))
        }
        val rizo = Path().apply {
            moveTo(X(f(0.1f)), X(0.11f))
            cubicTo(X(f(0.14f)), X(0.14f), X(f(0.17f)), X(0.1f), X(f(0.14f)), X(0.085f))
        }
        listOf(tallo, rizo).forEach { p ->
            translate(1.2f * u, 1.2f * u) { drawPath(p, t.s(0.35f), style = Stroke(2f * u, cap = StrokeCap.Round)) }
            drawPath(p, t.c(base), style = Stroke(2f * u, cap = StrokeCap.Round))
            translate(-0.6f * u, -0.6f * u) { drawPath(p, t.b(0.5f), style = Stroke(0.7f * u, cap = StrokeCap.Round)) }
        }
        listOf(Triple(0.065f, 0.2f, -20f), Triple(0.07f, 0.13f, 30f), Triple(0.13f, 0.06f, 70f), Triple(0.19f, 0.035f, 100f), Triple(0.265f, 0.075f, 150f), Triple(0.04f, 0.17f, -60f)).forEach { (fx, fy, g) ->
            val c = P(f(fx), fy)
            val gg = if (der) -g else g
            translate(1.2f * u, 1.2f * u) { hojita(c, X(0.034f), gg, t.s(0.3f), t) }
            hojita(c, X(0.034f), gg, t.c(base), t)
        }
        drawCircle(t.s(0.3f), X(0.008f), P(f(0.143f), 0.096f) + Offset(1f * u, 1f * u))
        drawCircle(t.c(base), X(0.008f), P(f(0.143f), 0.096f))
    }

    /** La celosía de día: la luz que entra por ella cae en rombos y estrellas, oblicua, sobre el muro, el arco y la devanadera. */
    private fun DrawScope.celosia() {
        val w = anchoW
        val mancha = poligono(X(-0.02f), X(0.04f), X(0.2f), X(-0.01f), X(0.56f), X(0.6f), X(0.22f), X(0.68f))
        clipPath(mancha) {
            drawPath(mancha, luzMueble(0.1f))
            val o = P(-0.02f, 0.0f)
            val a = Offset(X(0.048f), X(0.014f)); val bb = Offset(X(0.016f), X(0.052f))
            fun m(uu: Float, vv: Float) = Offset(o.x + uu * a.x + vv * bb.x, o.y + uu * a.y + vv * bb.y)
            for (i in -2..12) for (j in 0..14) {
                val c = m(i.toFloat(), j.toFloat())
                if (c.x < -w * 0.05f || c.x > w * 0.62f || c.y > w * 0.72f) continue
                // Más viva cerca del centro de la mancha, más tenue en sus bordes.
                val dc = (c - P(0.22f, 0.3f)).getDistance() / w
                val al = (0.5f - 0.7f * dc).coerceIn(0.1f, 0.45f)
                val p = Path()
                if ((i + j) % 2 == 0) {
                    for (q in 0 until 16) {
                        val ang = q * Math.PI / 8 - Math.PI / 2
                        val rr = if (q % 2 == 0) 0.36f else 0.2f
                        val pt = m(i + rr * cos(ang).toFloat(), j + rr * sin(ang).toFloat())
                        if (q == 0) p.moveTo(pt.x, pt.y) else p.lineTo(pt.x, pt.y)
                    }
                } else {
                    val r = 0.2f
                    val p0 = m(i - r, j.toFloat()); val p1 = m(i.toFloat(), j - r); val p2 = m(i + r, j.toFloat()); val p3 = m(i.toFloat(), j + r)
                    p.moveTo(p0.x, p0.y); p.lineTo(p1.x, p1.y); p.lineTo(p2.x, p2.y); p.lineTo(p3.x, p3.y)
                }
                p.close()
                drawPath(p, luzMueble(al))
            }
        }
    }

    // ---------------------------------------------------------------- lo de encima

    /**
     * La devanadera de aspas: el pie de cedro con sus dos patas, el poste torneado, la cruz de seis aspas con sus clavijas y la
     * madeja carmesí puesta en ellas, con un cabo suelto que cae; de una clavija del poste cuelgan otras dos madejas, de azafrán
     * y de índigo.
     */
    private fun DrawScope.devanadera() {
        val u = density
        val px = 0.27f
        val hub = P(px, 0.43f)
        val R = X(0.135f)
        val t = tf(px, 0.45f)
        val giro = 12.0
        val puntas = List(6) { k -> val a = Math.toRadians(giro + k * 60.0); Offset(hub.x + R * cos(a).toFloat(), hub.y + R * sin(a).toFloat()) }
        val madeja = Path().apply { puntas.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
        // Las sombras en el fondo del nicho: el poste, las aspas y la madeja.
        if (!esNoche) translate(X(0.03f), X(0.018f)) {
            drawLine(sombraMueble(0.18f), hub, P(px, TABLA), X(0.016f))
            puntas.forEach { p -> drawLine(sombraMueble(0.14f), hub, p, X(0.008f)) }
            drawPath(madeja, sombraMueble(0.16f), style = Stroke(X(0.022f), join = StrokeJoin.Round))
        }
        // El pie: un travesaño con dos patas.
        val pie = Rect(P(px - 0.075f, TABLA - 0.03f).x, X(TABLA - 0.03f), X(px + 0.075f), X(TABLA - 0.012f))
        contacto(P(px + 0.01f, TABLA - 0.002f), X(0.17f), X(0.012f), 0.35f)
        listOf(px - 0.07f, px + 0.05f).forEach { fx -> drawRect(t.c(CEDRO_OSC), Offset(X(fx), X(TABLA - 0.014f)), Size(X(0.022f), X(0.014f))) }
        drawRect(t.c(CEDRO), pie.topLeft, pie.size)
        drawRect(t.b(0.5f), pie.topLeft, Size(pie.width, 1.1f * u))
        drawRect(t.s(0.3f), Offset(pie.left, pie.bottom - 1.2f * u), Size(pie.width, 1.2f * u))
        // El poste torneado, con sus anillos.
        val poste = Rect(X(px - 0.009f), hub.y, X(px + 0.009f), pie.top)
        drawRect(t.c(CEDRO), poste.topLeft, poste.size)
        clipRect(poste.left, poste.top, poste.right, poste.bottom) { cilindro(poste, t, 1.3f) }
        listOf(0.18f, 0.55f, 0.85f).forEach { fy ->
            val y = poste.top + poste.height * fy
            val an = Rect(poste.left - X(0.006f), y, poste.right + X(0.006f), y + X(0.012f))
            drawOval(t.c(CEDRO_LUZ), an.topLeft, an.size)
            drawOval(t.s(0.3f), Offset(an.left + an.width * 0.5f, an.top), Size(an.width * 0.5f, an.height))
        }
        // La clavija de la que cuelgan las madejas.
        val cl = P(px - 0.009f, 0.545f)
        drawLine(t.s(0.3f), cl + Offset(0f, 1.5f * u), cl + Offset(-X(0.06f), -X(0.004f) + 1.5f * u), X(0.008f), StrokeCap.Round)
        drawLine(t.c(CEDRO_LUZ), cl, cl + Offset(-X(0.06f), -X(0.004f)), X(0.008f), StrokeCap.Round)
        // Las dos madejas colgadas: la de índigo detrás, más larga; la de azafrán delante.
        madejaColgada(P(0.2f, 0.541f), X(0.18f), X(0.046f), ANIL, t, 5501)
        madejaColgada(P(0.24f, 0.543f), X(0.145f), X(0.048f), AZAFRAN, t, 5502)
        // Las aspas y sus clavijas.
        puntas.forEach { p ->
            drawLine(t.c(CEDRO_OSC), hub, p, X(0.009f), StrokeCap.Round)
            drawLine(t.b(0.3f), hub, p, X(0.003f))
            val d = (p - hub) / R
            val perp = Offset(-d.y, d.x)
            drawLine(t.c(CEDRO), p - perp * X(0.016f), p + perp * X(0.016f), X(0.007f), StrokeCap.Round)
        }
        // La madeja carmesí, tensa en las clavijas, con su brillo de seda y un cabo suelto.
        val seda = t.c(mezcla(CARMESI, MANGANESO, 0.12f))
        drawPath(madeja, seda, style = Stroke(X(0.024f), join = StrokeJoin.Round))
        drawPath(madeja, t.c(mezcla(CARMESI, MANGANESO, 0.4f)), style = Stroke(X(0.007f), join = StrokeJoin.Round))
        translate(-X(0.004f), -X(0.005f)) { drawPath(madeja, if (esNoche) LLAMA.copy(alpha = 0.3f * t.k) else luzMueble(0.4f), style = Stroke(X(0.004f), join = StrokeJoin.Round)) }
        // Las vueltas de hilo que se cruzan sobre la madeja.
        for (k in 0 until 6) {
            val a = puntas[k]; val bq = puntas[(k + 1) % 6]
            for (q in 1..3) {
                val m = a + (bq - a) * (q / 4f)
                val d = (bq - a); val n = Offset(-d.y, d.x) / d.getDistance()
                drawLine(t.c(mezcla(CARMESI, AZAFRAN, 0.25f)).copy(alpha = 0.6f), m - n * X(0.01f) - d * 0.03f, m + n * X(0.01f) + d * 0.03f, 0.6f * u)
            }
        }
        val cabo = Path().apply {
            moveTo(puntas[1].x, puntas[1].y)
            cubicTo(puntas[1].x + X(0.04f), puntas[1].y + X(0.05f), X(0.37f), X(0.6f), X(0.43f), X(0.645f))
        }
        drawPath(cabo, seda, style = Stroke(1.2f * u))
        // El cubo de las aspas.
        drawCircle(t.s(0.3f), X(0.017f), hub + Offset(1.5f * u, 1.5f * u))
        drawCircle(t.c(CEDRO), X(0.017f), hub)
        drawCircle(t.b(0.5f), X(0.007f), hub - Offset(X(0.006f), X(0.006f)))
        drawCircle(t.c(CEDRO_OSC), X(0.004f), hub)
    }

    /** Una madeja de seda colgada de una clavija: el haz que cae doblado, sus hilos, la vuelta de abajo y la atadura. */
    private fun DrawScope.madejaColgada(arriba: Offset, largo: Float, ancho: Float, col: Color, t: Tono, s: Int) {
        val u = density
        val sesgo = ancho * (azar(s, 1) - 0.5f) * 0.6f
        val p = Path().apply {
            moveTo(arriba.x - ancho * 0.18f, arriba.y)
            cubicTo(arriba.x - ancho * 0.55f, arriba.y + largo * 0.35f, arriba.x - ancho * 0.6f + sesgo, arriba.y + largo * 0.8f, arriba.x - ancho * 0.25f + sesgo, arriba.y + largo)
            cubicTo(arriba.x + sesgo, arriba.y + largo * 1.06f, arriba.x + ancho * 0.25f + sesgo, arriba.y + largo * 1.03f, arriba.x + ancho * 0.45f + sesgo, arriba.y + largo * 0.95f)
            cubicTo(arriba.x + ancho * 0.6f + sesgo, arriba.y + largo * 0.7f, arriba.x + ancho * 0.45f, arriba.y + largo * 0.3f, arriba.x + ancho * 0.18f, arriba.y)
            close()
        }
        if (!esNoche) translate(X(0.018f), X(0.01f)) { drawPath(p, sombraMueble(0.18f)) }
        drawPath(p, t.c(col))
        clipPath(p) {
            // La luz a la izquierda, la sombra a la derecha, y los hilos a lo largo.
            drawRect(t.b(0.22f), Offset(arriba.x - ancho, arriba.y), Size(ancho * 0.7f, largo * 1.1f))
            drawRect(t.s(0.25f), Offset(arriba.x + ancho * 0.15f + sesgo, arriba.y), Size(ancho, largo * 1.1f))
            for (k in 0 until 7) {
                val fx = (k - 3) / 3f
                val l = Path().apply {
                    moveTo(arriba.x + fx * ancho * 0.15f, arriba.y)
                    cubicTo(arriba.x + fx * ancho * 0.45f, arriba.y + largo * 0.4f, arriba.x + fx * ancho * 0.5f + sesgo, arriba.y + largo * 0.8f, arriba.x + fx * ancho * 0.3f + sesgo, arriba.y + largo * 1.05f)
                }
                drawPath(l, if (k % 2 == 0) t.s(0.2f) else t.b(0.25f), style = Stroke(0.6f * u))
            }
        }
        // La atadura, cerca de arriba.
        val ay = arriba.y + largo * 0.18f
        drawLine(t.c(if (col == AZAFRAN) CARMESI else AZAFRAN), Offset(arriba.x - ancho * 0.38f, ay), Offset(arriba.x + ancho * 0.36f, ay + 1f * u), 1.6f * u)
    }

    /** El tintero de bronce, cilíndrico y con su tapa abierta, y dos cálamos apoyados en él. */
    private fun DrawScope.tintero(pie: Offset) {
        val u = density
        val t = tn(pie - Offset(0f, X(0.03f)))
        val r = Rect(pie.x - X(0.02f), pie.y - X(0.042f), pie.x + X(0.02f), pie.y)
        contacto(Offset(pie.x + X(0.008f), pie.y), X(0.07f), X(0.01f), 0.4f)
        // Los cálamos, detrás, inclinados hacia la derecha.
        listOf(Triple(0.004f, 0.075f, 0.11f), Triple(-0.006f, 0.05f, 0.12f)).forEach { (dx, inc, l) ->
            val a = Offset(pie.x + X(dx), r.top + X(0.01f))
            val z = Offset(a.x + X(inc), a.y - X(l))
            drawLine(t.s(0.25f), a + Offset(X(0.01f), X(0.004f)), z + Offset(X(0.016f), X(0.008f)), X(0.006f))
            drawLine(t.c(mezcla(ESPARTO, CEDRO_LUZ, 0.3f)), a, z, X(0.006f), StrokeCap.Round)
            drawLine(t.b(0.4f), a, z, X(0.002f))
            drawLine(t.c(MANGANESO), a, a + (z - a) * 0.08f, X(0.006f))
        }
        drawRect(t.c(BRONCE), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) { cilindro(r, t, 1.4f) }
        drawRect(t.c(mezcla(BRONCE, MANGANESO, 0.4f)), Offset(r.left, r.top + r.height * 0.3f), Size(r.width, 1.2f * u))
        drawRect(t.c(mezcla(BRONCE, MANGANESO, 0.4f)), Offset(r.left, r.bottom - r.height * 0.25f), Size(r.width, 1.2f * u))
        drawOval(t.c(EBANO), Offset(r.left + 1f * u, r.top - X(0.004f)), Size(r.width - 2f * u, X(0.008f)))
        // La tapa abierta, de lado, con su bisagra.
        rotate(-55f, Offset(r.left, r.top)) { drawRect(t.c(BRONCE), Offset(r.left - X(0.002f), r.top - X(0.004f)), Size(r.width, X(0.006f))) }
        destello(Offset(r.left + r.width * 0.25f, r.top + r.height * 0.55f), 1.1f * u, t)
    }

    /** El cuenco de verde y manganeso (loza blanca con trazos verdes y pardos) colmado de granadas. */
    private fun DrawScope.cuenco(pie: Offset) {
        val u = density
        val t = tn(pie - Offset(0f, X(0.03f)))
        val bw = X(0.13f); val bh = X(0.042f)
        val boca = pie.y - bh
        contacto(Offset(pie.x + X(0.01f), pie.y), bw * 1.05f, X(0.012f), 0.4f)
        // Las granadas, colmando el cuenco.
        granada(Offset(pie.x - X(0.03f), boca - X(0.008f)), X(0.026f), t, -10f)
        granada(Offset(pie.x + X(0.028f), boca - X(0.006f)), X(0.024f), t, 15f)
        granada(Offset(pie.x - X(0.002f), boca - X(0.03f)), X(0.027f), t, 5f)
        // El cuerpo: media elipse y su pie.
        val cuerpo = Path().apply {
            moveTo(pie.x - bw / 2, boca)
            cubicTo(pie.x - bw / 2, boca + bh * 0.7f, pie.x - bw * 0.25f, pie.y - X(0.006f), pie.x, pie.y - X(0.006f))
            cubicTo(pie.x + bw * 0.25f, pie.y - X(0.006f), pie.x + bw / 2, boca + bh * 0.7f, pie.x + bw / 2, boca)
            close()
        }
        drawRect(t.c(mezcla(CAL, ESPARTO, 0.2f)), Offset(pie.x - X(0.025f), pie.y - X(0.008f)), Size(X(0.05f), X(0.008f)))
        drawPath(cuerpo, t.c(mezcla(CAL, ESPARTO, 0.12f)))
        clipPath(cuerpo) {
            drawRect(t.c(VIDRIADO), Offset(pie.x - bw / 2, boca + bh * 0.12f), Size(bw, bh * 0.16f))
            drawRect(t.c(MANGANESO), Offset(pie.x - bw / 2, boca + bh * 0.1f), Size(bw, 0.9f * u))
            drawRect(t.c(MANGANESO), Offset(pie.x - bw / 2, boca + bh * 0.3f), Size(bw, 0.9f * u))
            // Unas palmetas de manganeso rellenas de verde.
            for (k in 0 until 4) {
                val c = Offset(pie.x - bw * 0.36f + bw * 0.24f * k, boca + bh * 0.58f)
                drawPath(poligono(c.x, c.y - bh * 0.18f, c.x + bw * 0.06f, c.y, c.x, c.y + bh * 0.18f, c.x - bw * 0.06f, c.y), t.c(VIDRIADO))
                drawPath(poligono(c.x, c.y - bh * 0.18f, c.x + bw * 0.06f, c.y, c.x, c.y + bh * 0.18f, c.x - bw * 0.06f, c.y), t.c(MANGANESO), style = Stroke(0.7f * u))
            }
            drawRect(t.b(0.3f), Offset(pie.x - bw / 2, boca), Size(bw * 0.25f, bh))
            drawRect(t.s(0.25f), Offset(pie.x + bw * 0.18f, boca), Size(bw * 0.32f, bh))
        }
        drawOval(t.c(mezcla(CAL, ESPARTO, 0.2f)), Offset(pie.x - bw / 2, boca - X(0.004f)), Size(bw, X(0.008f)), style = Stroke(1.2f * u))
        destello(Offset(pie.x - bw * 0.32f, boca + bh * 0.45f), 1.2f * u, t)
    }

    /** El candil de piquera de bronce en su pie alto: el plato, el fuste con su nudo, la cazoleta, el asa y la piquera larga; de noche, la llama. */
    private fun DrawScope.candil(pie: Offset) {
        val u = density
        val t = tn(pie - Offset(0f, X(0.06f)))
        val bronce = t.c(BRONCE)
        val todo = Path().apply {
            addRect(Rect(pie.x - X(0.005f), pie.y - X(0.085f), pie.x + X(0.005f), pie.y))
            addOval(Rect(pie.x - X(0.03f), pie.y - X(0.11f), pie.x + X(0.03f), pie.y - X(0.08f)))
        }
        sombraEnPared(todo, pie - Offset(0f, X(0.08f)), 0.16f)
        contacto(Offset(pie.x + X(0.008f), pie.y), X(0.08f), X(0.012f), 0.4f)
        // El plato del pie.
        drawOval(bronce, Offset(pie.x - X(0.035f), pie.y - X(0.012f)), Size(X(0.07f), X(0.014f)))
        drawOval(t.s(0.3f), Offset(pie.x, pie.y - X(0.012f)), Size(X(0.035f), X(0.014f)))
        drawOval(t.b(0.45f), Offset(pie.x - X(0.03f), pie.y - X(0.012f)), Size(X(0.03f), X(0.006f)))
        // El fuste, con el nudo a media altura.
        val fuste = Rect(pie.x - X(0.005f), pie.y - X(0.085f), pie.x + X(0.005f), pie.y - X(0.008f))
        drawRect(bronce, fuste.topLeft, fuste.size)
        clipRect(fuste.left, fuste.top, fuste.right, fuste.bottom) { cilindro(fuste, t, 1.4f, der = esNoche) }
        val nudo = Rect(pie.x - X(0.012f), pie.y - X(0.05f), pie.x + X(0.012f), pie.y - X(0.036f))
        drawOval(bronce, nudo.topLeft, nudo.size)
        drawOval(t.b(0.4f), Offset(nudo.left + nudo.width * 0.15f, nudo.top + 1f * u), Size(nudo.width * 0.35f, nudo.height * 0.4f))
        // La cazoleta con su tapa, la piquera hacia la derecha y el asa de anilla a la izquierda.
        val caz = Rect(pie.x - X(0.03f), pie.y - X(0.112f), pie.x + X(0.026f), pie.y - X(0.082f))
        drawArc(bronce, 0f, 180f, true, Offset(caz.left, caz.top - caz.height * 0.2f), Size(caz.width, caz.height * 1.2f))
        val piq = Path().apply {
            moveTo(caz.right - X(0.012f), caz.top + caz.height * 0.25f)
            lineTo(X(LUZ.x) - X(0.002f), X(LUZ.y) + X(0.012f))
            lineTo(X(LUZ.x) + X(0.004f), X(LUZ.y) + X(0.017f))
            lineTo(caz.right - X(0.004f), caz.top + caz.height * 0.72f)
            close()
        }
        drawPath(piq, bronce)
        drawPath(piq, t.s(0.25f), style = Stroke(0.7f * u))
        drawOval(t.c(mezcla(BRONCE, MANGANESO, 0.25f)), Offset(caz.left, caz.top - X(0.004f)), Size(caz.width, X(0.012f)))
        drawCircle(bronce, X(0.006f), Offset(caz.center.x - X(0.004f), caz.top - X(0.006f)))
        drawArc(bronce, 90f, 220f, false, Offset(caz.left - X(0.02f), caz.top - X(0.004f)), Size(X(0.026f), X(0.024f)), style = Stroke(X(0.005f)))
        clipPath(Path().apply { addOval(Rect(caz.left, caz.top - caz.height * 0.2f, caz.right, caz.bottom)) }) {
            drawRect(t.b(0.35f), Offset(caz.left, caz.top), Size(caz.width * 0.3f, caz.height))
            drawRect(t.s(0.25f), Offset(caz.left + caz.width * 0.65f, caz.top), Size(caz.width * 0.35f, caz.height))
        }
        destello(Offset(caz.left + caz.width * 0.3f, caz.top + caz.height * 0.3f), 1.3f * u, t)
        // La mecha, y de noche la llama en la punta de la piquera.
        val tip = P(LUZ.x, LUZ.y + 0.014f)
        drawLine(t.c(MANGANESO), tip, tip + Offset(X(0.003f), -X(0.006f)), 1.4f * u, StrokeCap.Round)
        if (esNoche) {
            val lc = LUZ * anchoW
            drawCircle(LLAMA.copy(alpha = 0.35f), X(0.026f), lc)
            drawPath(Path().apply {
                moveTo(lc.x, lc.y - X(0.022f))
                cubicTo(lc.x + X(0.01f), lc.y - X(0.008f), lc.x + X(0.008f), lc.y + X(0.009f), lc.x, lc.y + X(0.01f))
                cubicTo(lc.x - X(0.008f), lc.y + X(0.009f), lc.x - X(0.008f), lc.y - X(0.006f), lc.x, lc.y - X(0.022f))
                close()
            }, LLAMA)
            drawOval(Color(0xFFFFF4D8), Offset(lc.x - X(0.003f), lc.y - X(0.006f)), Size(X(0.006f), X(0.012f)))
        }
    }

    /** La píxide de marfil: el bote cilíndrico tallado con su tapa de cúpula, el pomo, dos bandas, las hojas talladas y el cierre de plata. */
    private fun DrawScope.pixide(pie: Offset) {
        val u = density
        val t = tn(pie - Offset(0f, X(0.05f)))
        val bw = X(0.095f); val bh = X(0.068f)
        val cuerpo = Rect(pie.x - bw / 2, pie.y - bh, pie.x + bw / 2, pie.y)
        val tapa = Rect(cuerpo.left - X(0.003f), cuerpo.top - X(0.022f), cuerpo.right + X(0.003f), cuerpo.top + X(0.004f))
        val silueta = Path().apply { addRect(cuerpo); addArc(Rect(tapa.left, tapa.top, tapa.right, tapa.bottom + tapa.height), 180f, 180f) }
        sombraEnPared(silueta, cuerpo.center, 0.18f)
        contacto(Offset(pie.x + X(0.008f), pie.y), bw * 1.2f, X(0.01f), 0.4f)
        drawRect(t.c(MARFIL), cuerpo.topLeft, cuerpo.size)
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            // La talla: un tallo que se enrosca alrededor del bote, con sus hojas, y su relieve.
            val talla = mezcla(MARFIL, ESPARTO, 0.6f)
            val p = Path()
            for (q in 0..16) {
                val x = cuerpo.left + cuerpo.width * q / 16f
                val y = cuerpo.center.y + bh * 0.16f * sin(q / 16f * 2.5f * 6.283f)
                if (q == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            translate(0.6f * u, 0.6f * u) { drawPath(p, t.s(0.25f), style = Stroke(1f * u)) }
            drawPath(p, t.c(talla), style = Stroke(1f * u))
            for (k in 0 until 5) {
                val c = Offset(cuerpo.left + cuerpo.width * (0.1f + 0.2f * k), cuerpo.center.y + (if (k % 2 == 0) -bh * 0.2f else bh * 0.2f))
                hojita(c, bh * 0.26f, if (k % 2 == 0) 35f else 145f, t.c(mezcla(MARFIL, ESPARTO, 0.4f)), t)
            }
            cilindro(cuerpo, t, 1.5f, der = esNoche)
        }
        listOf(cuerpo.top + bh * 0.08f, cuerpo.bottom - bh * 0.16f).forEach { y ->
            drawRect(t.c(mezcla(MARFIL, ESPARTO, 0.4f)), Offset(cuerpo.left, y), Size(cuerpo.width, bh * 0.08f))
            drawRect(t.s(0.2f), Offset(cuerpo.left, y + bh * 0.08f), Size(cuerpo.width, 0.8f * u))
        }
        // La tapa de cúpula y el pomo.
        drawArc(t.c(MARFIL), 180f, 180f, true, Offset(tapa.left, tapa.top), Size(tapa.width, tapa.height * 2))
        clipPath(Path().apply { addArc(Rect(tapa.left, tapa.top, tapa.right, tapa.bottom + tapa.height), 180f, 180f) }) {
            cilindro(Rect(tapa.left, tapa.top, tapa.right, tapa.bottom), t, 1.4f, der = esNoche)
            drawArc(t.c(mezcla(MARFIL, ESPARTO, 0.5f)), 180f, 180f, false, Offset(tapa.left + tapa.width * 0.2f, tapa.top + tapa.height * 0.35f), Size(tapa.width * 0.6f, tapa.height * 1.2f), style = Stroke(0.8f * u))
        }
        drawRect(t.c(mezcla(MARFIL, ESPARTO, 0.3f)), Offset(tapa.left, tapa.bottom - X(0.006f)), Size(tapa.width, X(0.006f)))
        drawCircle(t.c(MARFIL), X(0.007f), Offset(pie.x, tapa.top))
        drawCircle(t.b(0.5f), X(0.003f), Offset(pie.x - X(0.003f), tapa.top - X(0.003f)))
        // El cierre de plata, chico, en el borde de la tapa.
        drawRect(t.c(PLATA), Offset(pie.x - X(0.005f), tapa.bottom - X(0.003f)), Size(X(0.01f), X(0.012f)))
        drawCircle(t.c(mezcla(PLATA, EBANO, 0.4f)), X(0.0018f), Offset(pie.x, tapa.bottom + X(0.004f)))
        filo(cuerpo, t, true)
        destello(Offset(cuerpo.left + bw * 0.2f, cuerpo.top + bh * 0.4f), 1.1f * u, t)
    }

    /** El jarro de verde y manganeso: el cuerpo globular con su cuello, el asa, las bandas y las palmetas; asoman unas ramas de arrayán. */
    private fun DrawScope.jarro(pie: Offset) {
        val u = density
        val t = tn(pie - Offset(0f, X(0.06f)))
        val bw = X(0.088f); val bh = X(0.085f)
        val cc = Offset(pie.x, pie.y - bh * 0.52f)
        val cuello = Rect(pie.x - X(0.015f), cc.y - bh * 0.5f - X(0.04f), pie.x + X(0.015f), cc.y - bh * 0.3f)
        val silueta = Path().apply { addOval(Rect(cc.x - bw / 2, cc.y - bh / 2, cc.x + bw / 2, cc.y + bh / 2)); addRect(cuello) }
        // Las ramas de arrayán, detrás.
        listOf(Triple(-0.02f, -0.11f, -18f), Triple(0.01f, -0.13f, 8f), Triple(0.03f, -0.09f, 30f)).forEachIndexed { i, (dx, dy, g) ->
            val a = Offset(pie.x, cuello.top + X(0.01f)); val z = Offset(pie.x + X(dx + 0.01f * i - 0.01f), cuello.top + X(dy))
            drawLine(t.c(mezcla(ARRAYAN, MANGANESO, 0.4f)), a, z, 1.2f * u)
            for (q in 1..6) {
                val m = a + (z - a) * (q / 6.5f)
                hojita(m + Offset(X(0.007f), 0f), X(0.016f), g + 50f, t.c(if (q % 2 == 0) ARRAYAN else mezcla(ARRAYAN, VIDRIADO, 0.3f)), t)
                hojita(m - Offset(X(0.007f), 0f), X(0.016f), g - 50f, t.c(ARRAYAN), t)
            }
        }
        sombraEnPared(silueta, cc, 0.18f)
        contacto(Offset(pie.x + X(0.008f), pie.y), bw * 0.9f, X(0.01f), 0.4f)
        val loza = t.c(mezcla(CAL, ESPARTO, 0.12f))
        // El asa a la derecha.
        drawArc(loza, -90f, 180f, false, Offset(cc.x + bw * 0.12f, cuello.top + X(0.012f)), Size(bw * 0.5f, bh * 0.55f), style = Stroke(X(0.007f)))
        drawArc(t.s(0.25f), -40f, 100f, false, Offset(cc.x + bw * 0.12f, cuello.top + X(0.012f)), Size(bw * 0.5f, bh * 0.55f), style = Stroke(X(0.003f)))
        drawRect(loza, cuello.topLeft, cuello.size)
        drawOval(loza, Offset(cuello.left - X(0.006f), cuello.top - X(0.004f)), Size(cuello.width + X(0.012f), X(0.01f)))
        drawOval(t.c(MANGANESO), Offset(cuello.left - X(0.002f), cuello.top - X(0.002f)), Size(cuello.width + X(0.004f), X(0.006f)))
        val cuerpo = Rect(cc.x - bw / 2, cc.y - bh / 2, cc.x + bw / 2, cc.y + bh / 2)
        drawOval(loza, cuerpo.topLeft, cuerpo.size)
        clipPath(Path().apply { addOval(cuerpo); addRect(cuello) }) {
            // Las bandas verdes con su filete de manganeso y las palmetas.
            listOf(cuerpo.top + bh * 0.2f, cuerpo.bottom - bh * 0.25f).forEach { y ->
                drawRect(t.c(VIDRIADO), Offset(cuerpo.left, y), Size(bw, bh * 0.07f))
                drawRect(t.c(MANGANESO), Offset(cuerpo.left, y - 0.9f * u), Size(bw, 0.9f * u))
                drawRect(t.c(MANGANESO), Offset(cuerpo.left, y + bh * 0.07f), Size(bw, 0.9f * u))
            }
            for (k in 0 until 3) {
                val c = Offset(cuerpo.left + bw * (0.22f + 0.28f * k), cc.y + bh * 0.02f)
                val hoja = Path().apply {
                    moveTo(c.x, c.y - bh * 0.17f)
                    cubicTo(c.x + bw * 0.1f, c.y - bh * 0.05f, c.x + bw * 0.07f, c.y + bh * 0.12f, c.x, c.y + bh * 0.15f)
                    cubicTo(c.x - bw * 0.07f, c.y + bh * 0.12f, c.x - bw * 0.1f, c.y - bh * 0.05f, c.x, c.y - bh * 0.17f)
                    close()
                }
                drawPath(hoja, t.c(VIDRIADO))
                drawPath(hoja, t.c(MANGANESO), style = Stroke(0.8f * u))
                drawLine(t.c(MANGANESO), Offset(c.x, c.y - bh * 0.1f), Offset(c.x, c.y + bh * 0.12f), 0.6f * u)
            }
            cilindro(Rect(cuerpo.left, cuello.top - X(0.01f), cuerpo.right, cuerpo.bottom), t, 1.4f, der = esNoche)
            drawRect(t.s(0.2f), Offset(cuerpo.left, cc.y + bh * 0.25f), Size(bw, bh * 0.3f))
        }
        // Un desconchado del vidriado en el borde de la panza.
        drawCircle(t.c(mezcla(LADRILLO, CAL, 0.4f)), X(0.004f), Offset(cuerpo.right - bw * 0.12f, cc.y + bh * 0.28f))
        destello(Offset(cuerpo.left + bw * 0.25f, cuerpo.top + bh * 0.3f), 1.4f * u, t)
    }

    // ---------------------------------------------------------------- en el muro

    /** Unas tijeras de muelle de hierro colgadas de un clavo, con su sombra. */
    private fun DrawScope.tijeras(clavo: Offset) {
        val u = density
        val t = tn(clavo)
        val hierro = t.c(mezcla(PLATA, EBANO, 0.45f))
        val l = X(0.12f)
        val p = Path().apply {
            // El muelle (el aro de arriba) y las dos hojas que bajan casi juntas.
            moveTo(clavo.x - X(0.012f), clavo.y + X(0.03f))
            cubicTo(clavo.x - X(0.02f), clavo.y - X(0.004f), clavo.x + X(0.02f), clavo.y - X(0.004f), clavo.x + X(0.012f), clavo.y + X(0.03f))
            lineTo(clavo.x + X(0.008f), clavo.y + l)
            lineTo(clavo.x + X(0.001f), clavo.y + l * 0.55f)
            lineTo(clavo.x - X(0.006f), clavo.y + l * 0.98f)
            close()
        }
        if (!esNoche) translate(X(0.012f), X(0.01f)) { drawPath(p, sombraMueble(0.2f)) }
        drawPath(p, hierro, style = Stroke(X(0.006f), join = StrokeJoin.Round))
        drawPath(Path().apply { moveTo(clavo.x + X(0.001f), clavo.y + l * 0.55f); lineTo(clavo.x + X(0.008f), clavo.y + l); lineTo(clavo.x - X(0.006f), clavo.y + l * 0.98f); close() }, hierro)
        drawLine(t.b(0.5f), Offset(clavo.x - X(0.004f), clavo.y + l * 0.6f), Offset(clavo.x - X(0.005f), clavo.y + l * 0.95f), 0.7f * u)
        drawCircle(t.s(0.4f), X(0.004f), clavo + Offset(1f * u, 1f * u))
        drawCircle(t.c(EBANO), X(0.004f), clavo)
    }

    /**
     * La hoja de puerta de lacería, entreabierta hacia quien mira contra la jamba derecha, en escorzo: el bastidor de cedro, una
     * estrella de ocho puntas en el tablero de en medio, rombos arriba y abajo, su canto y su sombra en el muro.
     */
    private fun DrawScope.puerta() {
        val u = density
        val xa = 0.893f; val xb = 0.978f
        val ya0 = 0.745f; val ya1 = 1.62f; val yb0 = 0.722f; val yb1 = 1.655f
        // Un punto de la hoja: [uu] de la bisagra (0) al canto (1), [vv] de arriba (0) abajo (1).
        fun m(uu: Float, vv: Float): Offset {
            val x = xa + (xb - xa) * uu
            val y0 = ya0 + (yb0 - ya0) * uu; val y1 = ya1 + (yb1 - ya1) * uu
            return P(x, y0 + (y1 - y0) * vv)
        }
        fun quad(u0: Float, v0: Float, u1: Float, v1: Float): Path {
            val a = m(u0, v0); val b = m(u1, v0); val c = m(u1, v1); val d = m(u0, v1)
            return poligono(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y)
        }
        // De noche la tabla de remate le tapa casi todo el candil: le llega la mitad que a lo que tiene al lado.
        val t0 = tn(m(0.5f, 0.2f))
        val t = Tono(t0.o, t0.k * 0.5f)
        // La sombra en el muro, a la derecha.
        val s0 = m(1f, 0f); val s1 = m(1f, 1f)
        drawPath(poligono(s0.x, s0.y, s0.x + X(0.03f), s0.y + X(0.03f), s1.x + X(0.03f), s1.y + X(0.01f), s1.x, s1.y), t.s(if (esNoche) 0.2f else 0.24f))
        // El canto de la hoja (su grueso), a la derecha.
        drawPath(poligono(s0.x, s0.y, s0.x + X(0.012f), s0.y + X(0.004f), s1.x + X(0.012f), s1.y - X(0.004f), s1.x, s1.y), t.c(CEDRO_OSC))
        drawPath(quad(0f, 0f, 1f, 1f), t.c(CEDRO))
        clipPath(quad(0f, 0f, 1f, 1f)) {
            val r = Rect(m(0f, 0f).x, m(1f, 0f).y, m(1f, 1f).x, m(1f, 1f).y)
            vetas(r, (if (esNoche) NOCHE else CEDRO_OSC).copy(alpha = 0.4f), 5601, 0.5f, vertical = true, grosor = 0.5f)
            drawRect(t.b(0.12f), r.topLeft, Size(r.width * 0.4f, r.height))
        }
        // Los tres tableros, rebajados, con su sombra arriba.
        val tableros = listOf(0.05f to 0.22f, 0.27f to 0.73f, 0.78f to 0.95f)
        tableros.forEach { (v0, v1) ->
            drawPath(quad(0.18f, v0, 0.82f, v1), t.c(mezcla(CEDRO, CEDRO_OSC, 0.35f)))
            drawPath(quad(0.18f, v0, 0.82f, v0 + 0.012f), t.s(0.35f))
            drawPath(quad(0.18f, v1 - 0.006f, 0.82f, v1), t.b(0.35f))
        }
        // La lacería: rombos en los tableros chicos y una estrella de ocho en el grande, en hueso.
        val hueso = t.c(mezcla(HUESO, CEDRO_LUZ, 0.25f))
        listOf(0.135f, 0.865f).forEach { vc ->
            val a = m(0.5f, vc - 0.06f); val b = m(0.78f, vc); val c = m(0.5f, vc + 0.06f); val d = m(0.22f, vc)
            drawPath(poligono(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y), hueso, style = Stroke(1.6f * u))
        }
        val ec = 0.5f; val er = 0.13f
        val estr = Path()
        for (k in 0 until 16) {
            val ang = k * Math.PI / 8 - Math.PI / 2
            val rr = if (k % 2 == 0) 1f else 0.62f
            val q = m(0.5f + 0.31f * rr * cos(ang).toFloat(), ec + er * rr * sin(ang).toFloat())
            if (k == 0) estr.moveTo(q.x, q.y) else estr.lineTo(q.x, q.y)
        }
        estr.close()
        drawPath(estr, hueso, style = Stroke(1.6f * u))
        listOf(0.3f, 0.7f).forEach { vv -> drawLine(hueso, m(0.18f, vv), m(0.82f, vv), 1.2f * u) }
        drawLine(hueso, m(0.5f, 0.27f), m(0.5f, ec - er), 1.2f * u)
        drawLine(hueso, m(0.5f, ec + er), m(0.5f, 0.73f), 1.2f * u)
        drawCircle(hueso, X(0.004f), m(0.5f, ec))
        // El tirador de bronce y las bisagras de hierro.
        drawCircle(t.c(BRONCE), X(0.006f), m(0.9f, 0.5f))
        drawCircle(t.b(0.5f), X(0.002f), m(0.88f, 0.49f))
        listOf(0.1f, 0.9f).forEach { vv -> drawRect(t.c(mezcla(PLATA, EBANO, 0.5f)), m(0f, vv) - Offset(X(0.004f), X(0.006f)), Size(X(0.014f), X(0.012f))) }
        filo(Rect(m(0f, 0f).x, m(1f, 0f).y, m(1f, 1f).x, m(1f, 1f).y), t, false)
    }

    // ---------------------------------------------------------------- al pie

    /** La estera de esparto, cortada por el borde izquierdo: la trenza en espiga, las costuras y el borde deshilachado. */
    private fun DrawScope.estera(b: Rect, alto: Float) {
        val u = density
        val hb = b.height
        val y0 = b.top + hb * (ZOCALO + 0.06f); val y1 = alto - hb * 0.03f
        val x0 = -X(0.02f); val x1 = X(0.66f)
        val forma = poligono(x0, y0, x1, y0, x1 + X(0.03f), y1, x0, y1)
        val t = tn(Offset(X(0.3f), y1))
        drawPath(poligono(x0, y1, x1 + X(0.03f), y1, x1 + X(0.045f), y1 + hb * 0.02f, x0, y1 + hb * 0.02f), t.s(0.35f))
        drawPath(poligono(x1, y0, x1 + X(0.03f), y1, x1 + X(0.045f), y1, x1 + X(0.02f), y0 + X(0.006f)), t.s(0.3f))
        drawPath(forma, t.c(ESPARTO))
        clipPath(forma) {
            // La trenza: hileras en espiga, una clara y otra en sombra.
            val fila = (y1 - y0) / 9f
            for (r in 0 until 9) {
                val y = y0 + fila * r
                val p = Path()
                var x = x0; var k = 0
                p.moveTo(x, y + fila * 0.5f)
                while (x < x1 + X(0.05f)) { x += X(0.012f); p.lineTo(x, if (k % 2 == 0) y + fila * 0.1f else y + fila * 0.9f); k++ }
                val dir = if (r % 2 == 0) 1f else -1f
                drawPath(p, t.c(mezcla(ESPARTO, CEDRO_LUZ, 0.35f)), style = Stroke(0.8f * u))
                translate(dir * 1.2f * u, 0f) { drawPath(p, t.b(0.25f), style = Stroke(0.5f * u)) }
                drawLine(t.s(0.3f), Offset(x0, y + fila), Offset(x1 + X(0.05f), y + fila), 1f * u)
            }
            pinceladas(Rect(x0, y0, x1 + X(0.03f), y1), if (esNoche) NOCHE else CEDRO, 5701, 10, u, 0.12f)
            drawRect(t.b(0.2f), Offset(x0, y0), Size(x1 - x0, 1.2f * u))
        }
        // El borde deshilachado de la derecha.
        for (k in 0 until 10) {
            val y = y0 + (y1 - y0) * (k + 0.5f) / 10f
            val x = x1 + X(0.03f) * (y - y0) / (y1 - y0)
            drawLine(t.c(ESPARTO), Offset(x, y), Offset(x + X(0.008f + 0.008f * azar(5702, k)), y + X(0.004f) * (azar(5703, k) - 0.5f)), 0.8f * u)
        }
    }

    /** El cojín de seda carmesí apoyado en el zócalo: hinchado, con sus pliegues, la banda de signos bordados y las borlas. */
    private fun DrawScope.cojin(b: Rect) {
        val u = density
        val hb = b.height
        val l = X(0.26f); val r = X(0.6f)
        val top = b.top + hb * 0.3f; val bot = b.top + hb * 0.9f
        val t = tn(Offset((l + r) / 2, top))
        val forma = Path().apply {
            moveTo(l + X(0.01f), top + X(0.02f))
            cubicTo(l + X(0.1f), top - X(0.005f), r - X(0.12f), top + X(0.008f), r - X(0.006f), top - X(0.002f))
            cubicTo(r - X(0.022f), top + (bot - top) * 0.35f, r - X(0.02f), top + (bot - top) * 0.7f, r + X(0.008f), bot)
            cubicTo(r - X(0.12f), bot - X(0.016f), l + X(0.12f), bot - X(0.006f), l - X(0.008f), bot - X(0.006f))
            cubicTo(l + X(0.016f), top + (bot - top) * 0.65f, l + X(0.022f), top + (bot - top) * 0.3f, l + X(0.01f), top + X(0.02f))
            close()
        }
        // Su sombra en el zócalo (a la derecha) y en la estera.
        translate(X(0.03f), X(0.008f)) { drawPath(forma, t.s(0.3f)) }
        contacto(Offset((l + r) / 2 + X(0.03f), bot), r - l + X(0.04f), X(0.025f), 0.4f)
        drawPath(forma, t.c(CARMESI))
        clipPath(forma) {
            // La banda de tiraz, con los signos bordados (inventados: astiles y ganchos, sin ser letra).
            val by = top + (bot - top) * 0.36f; val bh = (bot - top) * 0.24f
            val banda = Path().apply {
                moveTo(l - X(0.02f), by + X(0.006f)); cubicTo(l + X(0.1f), by - X(0.004f), r - X(0.1f), by + X(0.006f), r + X(0.02f), by - X(0.004f))
                lineTo(r + X(0.02f), by + bh - X(0.002f)); cubicTo(r - X(0.1f), by + bh + X(0.008f), l + X(0.1f), by + bh - X(0.002f), l - X(0.02f), by + bh + X(0.008f)); close()
            }
            drawPath(banda, t.c(AZAFRAN))
            drawPath(banda, t.c(mezcla(CARMESI, MANGANESO, 0.3f)), style = Stroke(1.2f * u))
            var x = l + X(0.035f); var k = 0
            val linea = by + bh * 0.72f
            drawLine(t.c(ANIL), Offset(l + X(0.03f), linea), Offset(r - X(0.04f), linea - X(0.002f)), 1.2f * u)
            while (x < r - X(0.045f)) {
                val al = bh * (0.25f + 0.4f * azar(5801, k))
                drawLine(t.c(ANIL), Offset(x, linea), Offset(x, linea - al), 1.2f * u)
                when (k % 4) {
                    1 -> drawLine(t.c(ANIL), Offset(x, linea - al), Offset(x + X(0.006f), linea - al - bh * 0.08f), 1f * u)
                    3 -> drawArc(t.c(ANIL), 180f, 180f, false, Offset(x, linea - bh * 0.18f), Size(X(0.012f), bh * 0.2f), style = Stroke(1f * u))
                    else -> {}
                }
                x += X(0.012f + 0.008f * azar(5802, k)); k++
            }
            // El volumen: luz a la izquierda y arriba, sombra a la derecha y abajo; los pliegues desde las esquinas.
            val ch = bot - top; val cw = r - l
            drawOval(t.s(0.24f), Offset(l + cw * 0.45f, top + ch * 0.2f), Size(cw * 0.75f, ch * 1.1f))
            drawOval(t.s(0.18f), Offset(l - cw * 0.1f, top + ch * 0.72f), Size(cw * 1.2f, ch * 0.6f))
            drawOval(t.b(0.16f), Offset(l + cw * 0.04f, top + ch * 0.06f), Size(cw * 0.5f, ch * 0.55f))
            drawOval(t.b(0.14f), Offset(l + cw * 0.1f, top + ch * 0.12f), Size(cw * 0.28f, ch * 0.28f))
            listOf(Offset(l, top) to Offset(l + X(0.07f), top + X(0.04f)), Offset(r, top) to Offset(r - X(0.08f), top + X(0.035f)), Offset(r, bot) to Offset(r - X(0.08f), bot - X(0.04f)), Offset(l, bot) to Offset(l + X(0.06f), bot - X(0.03f))).forEach { (a, z) ->
                drawLine(t.s(0.3f), a, z, 1.6f * u, StrokeCap.Round)
                drawLine(t.b(0.25f), a + Offset(0f, -1.5f * u), z + Offset(0f, -1.5f * u), 0.8f * u, StrokeCap.Round)
            }
            pinceladas(Rect(l, top, r, bot), if (esNoche) NOCHE else mezcla(CARMESI, AZAFRAN, 0.3f), 5803, 8, u, 0.12f)
        }
        // El brillo de la seda y las borlas de las esquinas.
        drawPath(Path().apply { moveTo(l + X(0.04f), top + X(0.012f)); cubicTo(l + X(0.12f), top + X(0.002f), l + X(0.2f), top + X(0.008f), l + X(0.26f), top + X(0.006f)) }, t.b(0.45f), style = Stroke(1.2f * u))
        listOf(Offset(l + X(0.01f), top + X(0.02f)), Offset(r - X(0.006f), top - X(0.002f)), Offset(r + X(0.008f), bot), Offset(l - X(0.008f), bot - X(0.006f))).forEachIndexed { i, c ->
            val col = t.c(if (i % 2 == 0) AZAFRAN else mezcla(AZAFRAN, CARMESI, 0.3f))
            drawCircle(col, X(0.007f), c)
            for (q in -2..2) drawLine(col, c, c + Offset(X(0.003f) * q + (if (i == 1 || i == 2) X(0.006f) else -X(0.006f)), X(0.024f)), 1f * u)
        }
    }

    /** La cesta de esparto con madejas, cortada por el borde derecho: las vueltas cosidas, el borde, y las madejas que asoman. */
    private fun DrawScope.cesta(b: Rect, alto: Float) {
        val u = density
        val hb = b.height
        val l = X(0.74f); val r = X(1.06f)
        val top = b.top + hb * 0.48f; val bot = alto - hb * 0.04f
        val t = tn(Offset(X(0.85f), top))
        contacto(Offset((l + r) / 2, bot), r - l + X(0.04f), X(0.03f), 0.45f)
        // Las madejas que asoman: índigo, verde y azafrán, y una carmesí que cuelga por el borde.
        listOf(Triple(0.79f, ANIL, 0.07f), Triple(0.86f, VIDRIADO, 0.085f), Triple(0.93f, AZAFRAN, 0.075f), Triple(1.0f, CARMESI, 0.08f)).forEachIndexed { i, (fx, col, al) ->
            val c = Offset(X(fx), top - X(al) * 0.45f)
            val rr = Rect(c.x - X(0.04f), c.y - X(al) * 0.5f, c.x + X(0.04f), top + X(0.01f))
            drawOval(t.c(col), rr.topLeft, rr.size)
            clipPath(Path().apply { addOval(rr) }) {
                drawRect(t.b(0.2f), rr.topLeft, Size(rr.width * 0.35f, rr.height))
                drawRect(t.s(0.25f), Offset(rr.left + rr.width * 0.65f, rr.top), Size(rr.width * 0.35f, rr.height))
                for (q in 0 until 5) drawLine(if (q % 2 == 0) t.s(0.2f) else t.b(0.2f), Offset(rr.left + rr.width * (0.15f + 0.17f * q), rr.top), Offset(rr.left + rr.width * (0.05f + 0.2f * q), rr.bottom), 0.6f * u)
            }
            drawLine(t.c(if (col == AZAFRAN) CARMESI else AZAFRAN), Offset(rr.left + rr.width * 0.2f, rr.top + rr.height * 0.3f), Offset(rr.right - rr.width * 0.2f, rr.top + rr.height * 0.36f), 1.4f * u)
        }
        madejaColgada(Offset(X(0.79f), top + X(0.006f)), X(0.09f), X(0.03f), CARMESI, t, 5901)
        // El cuerpo de la cesta: las vueltas de esparto cosidas.
        val cuerpo = poligono(l, top, r, top, r, bot, l + X(0.03f), bot)
        drawPath(cuerpo, t.c(mezcla(ESPARTO, CEDRO_LUZ, 0.2f)))
        clipPath(cuerpo) {
            val vueltas = 7
            for (k in 0 until vueltas) {
                val y = top + (bot - top) * k / vueltas
                drawLine(t.s(0.3f), Offset(l, y), Offset(r, y), 1.2f * u)
                drawLine(t.b(0.3f), Offset(l, y + 1.4f * u), Offset(r, y + 1.4f * u), 0.7f * u)
                var x = l + (if (k % 2 == 0) 0f else X(0.012f))
                while (x < r) { drawLine(t.s(0.25f), Offset(x, y + 2f * u), Offset(x + X(0.004f), y + (bot - top) / vueltas - 1f * u), 0.7f * u); x += X(0.024f) }
            }
            drawRect(t.b(0.18f), Offset(l, top), Size(X(0.06f), bot - top))
            drawRect(t.s(0.2f), Offset(l, bot - (bot - top) * 0.25f), Size(r - l, (bot - top) * 0.25f))
        }
        // El borde trenzado.
        drawRect(t.c(ESPARTO), Offset(l - X(0.006f), top - X(0.012f)), Size(r - l + X(0.01f), X(0.018f)))
        var x = l
        while (x < r) { drawLine(t.s(0.3f), Offset(x, top - X(0.012f)), Offset(x + X(0.01f), top + X(0.006f)), 0.8f * u); x += X(0.014f) }
        drawRect(t.b(0.45f), Offset(l - X(0.006f), top - X(0.012f)), Size(r - l, 1f * u))
    }

    // ---------------------------------------------------------------- los compartimentos

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las tres clases. */
    private fun mezclar(s: Int): Int {
        var x = s * 0x9E3779B1L.toInt()
        x = x xor (x ushr 15)
        x *= 0x85EBCA6BL.toInt()
        return x xor (x ushr 13)
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este hueco en el mueble (en fracciones del ancho), para saber cuánto le llega del candil.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val k = if (!oscuro) 0f else if (dentro) luz(centro) else 0.5f
        val dia = if (oscuro) 0f else 0.07f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.025f else 0f)
        val t = Tono(oscuro, k, dia)
        val der = oscuro
        fondoHueco(w, h, t)
        if (c.vacio) { vacio(w, h, c, t); return }
        val m = mezclar(c.semilla)
        translate(0f, -SALIDA * h * ab) {
            when {
                c.sinTema -> cestaPalma(w, h, c, t)
                else -> when (Math.floorMod(m, 3)) {
                    0 -> cajita(w, h, c, m, t, der, ab)
                    1 -> arqueta(w, h, c, m, t, der)
                    else -> envoltorio(w, h, c, m, t, der)
                }
            }
        }
    }

    /**
     * El fondo de un hueco: las tablas de cedro del fondo, la sombra de la balda de arriba en escalones, el costado de la
     * izquierda en sombra y la balda del piso con su canto.
     */
    private fun DrawScope.fondoHueco(w: Float, h: Float, t: Tono) {
        val u = density
        drawRect(t.c(mezcla(CEDRO_OSC, CEDRO, 0.45f)), Offset.Zero, Size(w, h))
        drawRect(t.b(0.08f), Offset(w * 0.5f, 0f), Size(w * 0.5f, h))
        listOf(0.24f, 0.5f, 0.77f).forEach { fx ->
            drawRect(t.s(0.3f), Offset(w * fx, 0f), Size(1f * u, h))
            drawRect(t.b(0.16f), Offset(w * fx + 1f * u, 0f), Size(0.7f * u, h))
        }
        listOf(0.34f to 0.1f, 0.2f to 0.12f, 0.08f to 0.22f).forEach { (a, al) -> drawRect(t.s(al), Offset.Zero, Size(w, h * a)) }
        drawRect(t.s(0.22f), Offset.Zero, Size(w * 0.06f, h))
        drawRect(t.s(0.1f), Offset(w * 0.94f, 0f), Size(w * 0.06f, h))
        val piso = h * PISO
        drawRect(t.c(mezcla(CEDRO, CEDRO_LUZ, 0.4f)), Offset(0f, piso), Size(w, h - piso))
        drawRect(t.b(0.45f), Offset(0f, piso), Size(w, 1f * u))
    }

    /** La placa de hueso del tema, con su sombra, un filete y dos clavos de plata; de noche, más hundida cuanto más lejos del candil. */
    private fun DrawScope.placaHueso(w: Float, h: Float, t: Tono, filete: Boolean = true) {
        val u = density
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t.s(0.3f), Offset(r.left + 1.2f * u, r.top + 1.6f * u), r.size)
        val fondo = if (t.o) mezcla(HUESO_LEJOS, mezcla(HUESO, LLAMA, 0.1f), (1.3f * t.k.pow(0.6f)).coerceAtMost(1f)) else t.c(HUESO)
        drawRect(fondo, r.topLeft, r.size)
        if (filete) drawRect(MANGANESO.copy(alpha = 0.45f), Offset(r.left + 2.4f * u, r.top + 2.4f * u), Size(r.width - 4.8f * u, r.height - 4.8f * u), style = Stroke(0.7f * u))
        drawRect(t.s(0.1f), Offset(r.right - r.width * 0.04f, r.top), Size(r.width * 0.04f, r.height))
        drawRect(t.b(0.45f), r.topLeft, Size(r.width, 0.8f * u))
        listOf(r.left + 1.3f * u, r.right - 1.3f * u).forEach { x -> drawCircle(t.c(PLATA), 0.9f * u, Offset(x, r.center.y)) }
    }

    /** Las hojas que asoman entre [x0] y [x1] por encima de [arriba]: más y más altas cuanto más guarda, ladeadas. */
    private fun DrawScope.hojas(x0: Float, x1: Float, arriba: Float, h: Float, c: EstadoCajon, t: Tono, extra: Float = 0f) {
        val u = density
        val n = c.n.coerceIn(0, 10)
        val cuantas = (1 + n / 2).coerceIn(1, 5)
        val sube = h * (0.05f + 0.014f * n + extra)
        for (i in 0 until cuantas) {
            val f = (i + 0.5f) / cuantas
            val pw = (x1 - x0) * (0.3f + 0.15f * azar(c.semilla, i + 20))
            val x = x0 + (x1 - x0 - pw) * (f * 0.9f + 0.05f * azar(c.semilla, i + 30))
            val y = arriba - sube * (0.45f + 0.55f * azar(c.semilla, i))
            val g = (azar(c.semilla, i + 40) - 0.5f) * 8f
            rotate(g, Offset(x + pw / 2, arriba)) {
                drawRect(t.s(0.22f), Offset(x + 1.5f * u, y + 1.5f * u), Size(pw, arriba - y))
                drawRect(t.c(if (i % 2 == 0) MARFIL else mezcla(MARFIL, ESPARTO, 0.3f)), Offset(x, y), Size(pw, arriba - y + 2f * u))
                drawRect(t.b(0.45f), Offset(x, y), Size(pw, 0.9f * u))
                drawRect(t.s(0.12f), Offset(x + pw * 0.85f, y), Size(pw * 0.15f, arriba - y))
                // Unos renglones a cálamo.
                for (q in 0 until 2) drawLine(t.c(TINTA).copy(alpha = 0.35f), Offset(x + pw * 0.15f, y + h * (0.02f + 0.018f * q)), Offset(x + pw * (0.6f + 0.2f * azar(c.semilla, i + q)), y + h * (0.02f + 0.018f * q)), 0.6f * u)
            }
        }
    }

    /**
     * La cajita de taracea: el cuerpo de nogal con dos franjas de dientes de hueso y ébano, la tapa entreabierta (más cuanto
     * más guarda, y al abrirla) con su franja de estrellitas, las hojas que asoman por la rendija y la placa de hueso.
     */
    private fun DrawScope.cajita(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean, ab: Float) {
        val u = density
        val piso = h * PISO
        val caja = Rect(w * 0.06f, h * 0.38f, w * 0.94f, piso)
        val madera = when (Math.floorMod(m ushr 3, 3)) { 0 -> NOGAL; 1 -> mezcla(NOGAL, CEDRO, 0.4f); else -> mezcla(NOGAL, EBANO, 0.3f) }
        drawRect(t.s(0.28f), Offset(caja.left + w * 0.035f, caja.top + h * 0.03f), caja.size)
        contacto(Offset(caja.center.x + w * 0.04f, piso), caja.width * 1.05f, h * 0.05f, 0.42f)
        // La tapa, levantada por la izquierda desde la bisagra de la derecha (más cuanto más guarda, y al abrirla): por la rendija
        // se ve lo oscuro de dentro y asoman las hojas.
        val giro = 3f + 1.1f * c.n.coerceIn(0, 8) + 7f * ab
        val tapa = Rect(w * 0.04f, caja.top - h * 0.12f, w * 0.96f, caja.top)
        val rad = Math.toRadians(giro.toDouble())
        val esquina = Offset(tapa.right + (tapa.left - tapa.right) * cos(rad).toFloat(), tapa.bottom + (tapa.left - tapa.right) * sin(rad).toFloat())
        drawPath(poligono(caja.left, caja.top + 1f * u, caja.right, caja.top + 1f * u, esquina.x, esquina.y), t.c(mezcla(EBANO, NOGAL, 0.3f)))
        clipPath(poligono(caja.left - w * 0.05f, caja.top + 1f * u, caja.right, caja.top + 1f * u, esquina.x - w * 0.05f, esquina.y - h * 0.4f)) {
            hojas(caja.left + w * 0.03f, caja.left + caja.width * 0.6f, caja.top, h, c, t, extra = giro / 300f)
        }
        rotate(giro, Offset(tapa.right, tapa.bottom)) {
            drawRect(t.s(0.3f), Offset(tapa.left + 1.5f * u, tapa.bottom), Size(tapa.width, 2f * u))
            drawRect(t.c(madera), tapa.topLeft, tapa.size)
            drawRect(t.b(0.5f), tapa.topLeft, Size(tapa.width, 1.1f * u))
            drawRect(t.s(0.3f), Offset(tapa.left, tapa.bottom - 1.4f * u), Size(tapa.width, 1.4f * u))
            // La franja de estrellitas de hueso y verde en el canto de la tapa.
            val ns = 6
            for (j in 0 until ns) {
                val sc = Offset(tapa.left + tapa.width * (j + 0.5f) / ns, tapa.center.y)
                drawPath(estrella(sc, tapa.height * 0.32f, 8, 0.6f), t.c(if (j % 2 == 0) HUESO else VIDRIADO))
                drawCircle(t.c(madera), tapa.height * 0.08f, sc)
            }
            drawRect(t.c(HUESO), tapa.topLeft, tapa.size, style = Stroke(0.8f * u))
            filo(tapa, t, der)
        }
        // El cuerpo, con la veta y las dos franjas de dientes.
        drawRect(t.c(madera), caja.topLeft, caja.size)
        vetas(caja, (if (t.o) NOCHE else EBANO).copy(alpha = 0.35f), c.semilla, 0.6f, grosor = 0.5f)
        fun dientes(y: Float, alto: Float) {
            val nn = (caja.width / alto).toInt().coerceAtLeast(4)
            val aw = caja.width / nn
            drawRect(t.c(EBANO), Offset(caja.left, y), Size(caja.width, alto))
            for (j in 0 until nn) {
                val x = caja.left + aw * j
                drawPath(poligono(x, y + alto, x + aw / 2, y, x + aw, y + alto), t.c(if (j % 3 == 2) VIDRIADO else HUESO))
            }
        }
        dientes(caja.top + h * 0.035f, h * 0.05f)
        dientes(piso - h * 0.085f, h * 0.05f)
        if (der) drawRect(t.b(0.16f), Offset(caja.right - caja.width * 0.14f, caja.top), Size(caja.width * 0.14f, caja.height))
        else { drawRect(t.b(0.14f), caja.topLeft, Size(caja.width * 0.15f, caja.height)); drawRect(t.s(0.2f), Offset(caja.right - caja.width * 0.1f, caja.top), Size(caja.width * 0.1f, caja.height)) }
        drawRect(t.c(HUESO), caja.topLeft, caja.size, style = Stroke(0.8f * u))
        // Una pieza de hueso que falta en la franja de abajo.
        drawRect(t.c(EBANO), Offset(caja.left + caja.width * (0.2f + 0.5f * azar(m, 3)), piso - h * 0.085f), Size(w * 0.04f, h * 0.05f))
        filo(caja, t, der)
        placaHueso(w, h, t)
    }

    /**
     * La arqueta de marfil: el cuerpo tallado con sus hojas, tres fajas de plata con sus clavos, la cerradura, la tapa a dos
     * aguas con su cumbrera; detrás, las hojas que asoman (más con más fichas).
     */
    private fun DrawScope.arqueta(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val caja = Rect(w * 0.08f, h * 0.42f, w * 0.92f, piso)
        val clase = Math.floorMod(m ushr 3, 3)
        val marfil = when (clase) { 0 -> MARFIL; 1 -> mezcla(MARFIL, AZAFRAN, 0.2f); else -> mezcla(CEDRO, CEDRO_LUZ, 0.55f) }
        val talla = if (clase == 2) mezcla(marfil, CEDRO_OSC, 0.45f) else mezcla(marfil, ESPARTO, 0.6f)
        hojas(caja.left + w * 0.04f, caja.right - w * 0.04f, h * 0.3f, h, c, t, extra = 0.04f)
        drawRect(t.s(0.28f), Offset(caja.left + w * 0.035f, caja.top + h * 0.03f), caja.size)
        contacto(Offset(caja.center.x + w * 0.04f, piso), caja.width * 1.05f, h * 0.05f, 0.42f)
        drawRect(t.c(marfil), caja.topLeft, caja.size)
        clipRect(caja.left, caja.top, caja.right, caja.bottom) {
            // La talla: tallos ondulados con hojas, en relieve.
            val p = Path()
            var x = caja.left; var first = true
            while (x <= caja.right) {
                val y = caja.top + caja.height * 0.2f + h * 0.025f * sin((x - caja.left) / (w * 0.12f) * Math.PI.toFloat())
                if (first) { p.moveTo(x, y); first = false } else p.lineTo(x, y)
                x += w * 0.02f
            }
            drawPath(p, t.c(talla), style = Stroke(1f * u))
            for (j in 0 until 6) hojita(Offset(caja.left + caja.width * (0.08f + 0.17f * j), caja.top + caja.height * (if (j % 2 == 0) 0.12f else 0.3f)), h * 0.07f, if (j % 2 == 0) 30f else 150f, t.c(talla), t)
            if (der) drawRect(t.b(0.16f), Offset(caja.right - caja.width * 0.18f, caja.top), Size(caja.width * 0.18f, caja.height))
            else { drawRect(t.b(0.2f), caja.topLeft, Size(caja.width * 0.18f, caja.height)); drawRect(t.s(0.18f), Offset(caja.right - caja.width * 0.12f, caja.top), Size(caja.width * 0.12f, caja.height)) }
            drawRect(t.s(0.2f), Offset(caja.left, piso - h * 0.05f), Size(caja.width, h * 0.05f))
        }
        // Las fajas de plata con sus clavos.
        listOf(0.03f, 0.97f).forEach { fx ->
            val fr = Rect(caja.left + caja.width * fx - w * 0.025f, caja.top, caja.left + caja.width * fx + w * 0.025f, caja.bottom)
            drawRect(t.c(PLATA), fr.topLeft, fr.size)
            drawRect(t.b(0.45f), fr.topLeft, Size(1f * u, fr.height))
            drawRect(t.s(0.3f), Offset(fr.right - 1f * u, fr.top), Size(1f * u, fr.height))
            for (q in 0 until 3) drawCircle(t.c(mezcla(PLATA, EBANO, 0.4f)), 0.9f * u, Offset(fr.center.x, fr.top + fr.height * (0.2f + 0.3f * q)))
        }
        // La tapa a dos aguas (en tronco de pirámide): el faldón de delante, su cumbrera y sus cantoneras.
        val tb = caja.top; val tt = h * 0.27f
        val tapa = poligono(caja.left - w * 0.02f, tb, caja.left + w * 0.08f, tt, caja.right - w * 0.08f, tt, caja.right + w * 0.02f, tb)
        drawPath(tapa, t.c(marfil))
        clipPath(tapa) {
            drawRect(t.b(0.3f), Offset(caja.left - w * 0.02f, tt), Size(caja.width + w * 0.04f, (tb - tt) * 0.3f))
            drawRect(t.s(0.15f), Offset(caja.left - w * 0.02f, tt + (tb - tt) * 0.6f), Size(caja.width + w * 0.04f, (tb - tt) * 0.4f))
            for (j in 0 until 5) drawCircle(t.c(talla), h * 0.018f, Offset(caja.left + caja.width * (0.15f + 0.175f * j), (tt + tb) / 2), style = Stroke(0.8f * u))
        }
        drawRect(t.c(PLATA), Offset(caja.left + w * 0.08f, tt - h * 0.012f), Size(caja.width - w * 0.16f, h * 0.02f))
        drawRect(t.b(0.55f), Offset(caja.left + w * 0.08f, tt - h * 0.012f), Size(caja.width - w * 0.16f, 0.8f * u))
        drawRect(t.s(0.3f), Offset(caja.left - w * 0.02f, tb - 1.2f * u), Size(caja.width + w * 0.04f, 1.6f * u))
        // La cerradura: la placa de plata con su aldabilla que baja desde la tapa.
        val cx = w * 0.5f
        drawRect(t.s(0.3f), Offset(cx - w * 0.03f + 1.2f * u, tb - h * 0.05f + 1.5f * u), Size(w * 0.06f, h * 0.1f))
        drawRect(t.c(PLATA), Offset(cx - w * 0.03f, tb - h * 0.05f), Size(w * 0.06f, h * 0.1f))
        drawRect(t.c(EBANO), Offset(cx - w * 0.004f, tb + h * 0.01f), Size(w * 0.008f, h * 0.025f))
        destello(Offset(cx - w * 0.018f, tb - h * 0.035f), 1.1f * u, t)
        filo(caja, t, der)
        placaHueso(w, h, t)
    }

    /**
     * El envoltorio de seda: un fardo blando (carmesí, índigo o azafrán según la semilla) con dos bandas de tiraz y sus signos
     * bordados, atado con un cordón que se cruza y su nudo con borlas arriba; por la boca de la izquierda asoman las hojas.
     */
    private fun DrawScope.envoltorio(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val (seda, banda) = when (Math.floorMod(m ushr 3, 3)) { 0 -> CARMESI to AZAFRAN; 1 -> ANIL to AZAFRAN; else -> AZAFRAN to CARMESI }
        val l = w * 0.06f; val r = w * 0.94f; val top = h * 0.36f
        val forma = Path().apply {
            moveTo(l + w * 0.03f, piso)
            cubicTo(l - w * 0.01f, piso - h * 0.2f, l - w * 0.005f, top + h * 0.1f, l + w * 0.05f, top + h * 0.02f)
            cubicTo(l + w * 0.25f, top - h * 0.05f, r - w * 0.3f, top + h * 0.04f, r - w * 0.06f, top - h * 0.01f)
            cubicTo(r + w * 0.01f, top + h * 0.1f, r + w * 0.01f, piso - h * 0.2f, r - w * 0.02f, piso)
            close()
        }
        hojas(l + w * 0.06f, w * 0.6f, top + h * 0.02f, h, c, t)
        translate(w * 0.035f, h * 0.03f) { drawPath(forma, t.s(0.28f)) }
        contacto(Offset(w * 0.54f, piso), (r - l) * 1.05f, h * 0.05f, 0.42f)
        drawPath(forma, t.c(seda))
        clipPath(forma) {
            // Las dos bandas de tiraz, onduladas como la tela, con sus signos.
            listOf(0.44f, 0.84f).forEach { fy ->
                val y = h * fy
                val bh = h * 0.07f
                val bp = Path().apply {
                    moveTo(l - w * 0.02f, y); cubicTo(w * 0.3f, y - h * 0.03f, w * 0.6f, y + h * 0.025f, r + w * 0.02f, y - h * 0.01f)
                    lineTo(r + w * 0.02f, y - h * 0.01f + bh); cubicTo(w * 0.6f, y + h * 0.025f + bh, w * 0.3f, y - h * 0.03f + bh, l - w * 0.02f, y + bh); close()
                }
                drawPath(bp, t.c(banda))
                var x = l + w * 0.05f; var k = 0
                while (x < r - w * 0.05f) {
                    val yy = y + bh * 0.8f + (if (x < w * 0.5f) -h * 0.012f else h * 0.004f)
                    drawLine(t.c(mezcla(seda, MANGANESO, 0.3f)), Offset(x, yy), Offset(x, yy - bh * (0.3f + 0.35f * azar(c.semilla + 9, k))), 0.9f * u)
                    x += w * (0.025f + 0.02f * azar(c.semilla + 7, k)); k++
                }
            }
            // Los pliegues blandos: sombras y luces curvas que bajan del cuello.
            for (j in 0 until 4) {
                val x = l + (r - l) * (0.15f + 0.22f * j + 0.05f * azar(m, j))
                val p = Path().apply { moveTo(x, top); cubicTo(x + w * 0.04f, top + h * 0.15f, x - w * 0.03f, piso - h * 0.2f, x + w * 0.02f, piso) }
                drawPath(p, t.s(0.22f), style = Stroke(w * 0.022f))
                translate(-w * 0.018f, 0f) { drawPath(p, t.b(0.18f), style = Stroke(w * 0.008f)) }
            }
            if (der) drawRect(t.b(0.15f), Offset(r - w * 0.14f, 0f), Size(w * 0.14f, h))
            else { drawRect(t.b(0.16f), Offset(l, 0f), Size(w * 0.14f, h)); drawRect(t.s(0.2f), Offset(r - w * 0.1f, 0f), Size(w * 0.12f, h)) }
            drawRect(t.s(0.2f), Offset(0f, piso - h * 0.06f), Size(w, h * 0.06f))
        }
        // El cordón que da la vuelta y se cruza, y el nudo con dos borlas arriba a la derecha.
        val cord = t.c(if (seda == AZAFRAN) ANIL else mezcla(AZAFRAN, HUESO, 0.3f))
        val nudo = Offset(w * 0.7f, top + h * 0.03f)
        listOf(Offset(l + w * 0.02f, piso - h * 0.08f), Offset(r - w * 0.02f, piso - h * 0.12f)).forEach { a ->
            val p = Path().apply { moveTo(a.x, a.y); cubicTo(a.x + (nudo.x - a.x) * 0.3f, a.y - h * 0.15f, nudo.x, nudo.y + h * 0.2f, nudo.x, nudo.y) }
            translate(1f * u, 1.5f * u) { drawPath(p, t.s(0.3f), style = Stroke(1.8f * u)) }
            drawPath(p, cord, style = Stroke(1.8f * u))
        }
        drawCircle(cord, w * 0.018f, nudo)
        drawCircle(t.b(0.45f), w * 0.007f, nudo - Offset(w * 0.006f, w * 0.006f))
        listOf(-1f, 1f).forEach { s ->
            val e = nudo + Offset(s * w * 0.05f, -h * 0.06f)
            drawLine(cord, nudo, e, 1.4f * u)
            for (q in -1..1) drawLine(cord, e, e + Offset(q * w * 0.008f + s * w * 0.01f, -h * 0.05f), 1f * u)
        }
        filo(Rect(l, top, r, piso), t, der)
        placaHueso(w, h, t)
    }

    /** Lo que no tiene tema: una cesta de palma tejida en tiras, con las hojas sueltas que se salen y una placa lisa atada. */
    private fun DrawScope.cestaPalma(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val piso = h * PISO
        val top = h * 0.4f
        val cuerpo = poligono(w * 0.05f, top, w * 0.95f, top, w * 0.88f, piso, w * 0.12f, piso)
        hojas(w * 0.08f, w * 0.92f, top + h * 0.02f, h, c, t, extra = 0.05f)
        // Dos hojas sueltas que se salen ladeadas.
        listOf(Triple(0.08f, -20f, 0.32f), Triple(0.68f, 25f, 0.36f)).forEach { (fx, g, fy) ->
            rotate(g, Offset(w * fx + w * 0.12f, h * fy)) {
                drawRect(t.s(0.2f), Offset(w * fx + 1.5f * u, h * fy - h * 0.12f + 1.5f * u), Size(w * 0.24f, h * 0.16f))
                drawRect(t.c(MARFIL), Offset(w * fx, h * fy - h * 0.12f), Size(w * 0.24f, h * 0.16f))
                drawRect(t.b(0.4f), Offset(w * fx, h * fy - h * 0.12f), Size(w * 0.24f, 0.8f * u))
            }
        }
        translate(w * 0.035f, h * 0.03f) { drawPath(cuerpo, t.s(0.28f)) }
        contacto(Offset(w * 0.54f, piso), w * 0.85f, h * 0.05f, 0.42f)
        drawPath(cuerpo, t.c(mezcla(ESPARTO, AZAFRAN, 0.15f)))
        clipPath(cuerpo) {
            // El tejido en espiga de las tiras de palma, con una tira teñida de carmesí.
            val filas = 6
            for (r in 0 until filas) {
                val y = top + (piso - top) * r / filas
                val fh = (piso - top) / filas
                if (r == 2) drawRect(t.c(mezcla(CARMESI, ESPARTO, 0.25f)), Offset(0f, y), Size(w, fh))
                var x = 0f; var k = 0
                while (x < w) {
                    val dir = if ((k + r) % 2 == 0) 1f else -1f
                    drawLine(t.s(0.25f), Offset(x, y + fh * 0.5f - dir * fh * 0.4f), Offset(x + w * 0.04f, y + fh * 0.5f + dir * fh * 0.4f), 0.8f * u)
                    x += w * 0.04f; k++
                }
                drawLine(t.s(0.3f), Offset(0f, y), Offset(w, y), 1f * u)
            }
            drawRect(t.b(0.16f), Offset(0f, top), Size(w * 0.2f, piso - top))
            drawRect(t.s(0.18f), Offset(w * 0.82f, top), Size(w * 0.18f, piso - top))
        }
        drawRect(t.c(ESPARTO), Offset(w * 0.04f, top - h * 0.025f), Size(w * 0.92f, h * 0.035f))
        drawRect(t.b(0.45f), Offset(w * 0.04f, top - h * 0.025f), Size(w * 0.92f, 0.9f * u))
        filo(Rect(w * 0.05f, top, w * 0.95f, piso), t, true)
        placaHueso(w, h, t, filete = false)
    }

    /** Un hueco libre: la balda con una granada (y su hoja), o con un ovillo de seda y su hebra suelta, según la semilla. */
    private fun DrawScope.vacio(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val piso = h * PISO
        if (Math.floorMod(c.semilla ushr 2, 2) == 0) {
            val g = Offset(w * 0.42f, piso - h * 0.16f)
            contacto(Offset(g.x + w * 0.02f, piso), w * 0.3f, h * 0.05f, 0.45f)
            granada(g, h * 0.16f, t, 12f)
            hojita(Offset(w * 0.6f, piso - h * 0.03f), h * 0.12f, 75f, t.c(ARRAYAN), t)
            granada(Offset(w * 0.24f, piso - h * 0.06f), h * 0.06f, t, -20f)
        } else {
            val o = Offset(w * 0.4f, piso - h * 0.17f)
            val rr = h * 0.17f
            contacto(Offset(o.x + w * 0.02f, piso), rr * 2.4f, h * 0.05f, 0.45f)
            drawCircle(t.c(ANIL), rr, o)
            clipPath(Path().apply { addOval(Rect(o.x - rr, o.y - rr, o.x + rr, o.y + rr)) }) {
                for (k in 0 until 7) {
                    val a = k * 26f - 30f
                    rotate(a, o) { drawOval(if (k % 2 == 0) t.b(0.25f) else t.s(0.25f), Offset(o.x - rr * 0.9f, o.y - rr * 0.35f), Size(rr * 1.8f, rr * 0.7f), style = Stroke(0.8f * u)) }
                }
                drawCircle(t.s(0.28f), rr, o + Offset(rr * 0.5f, rr * 0.45f))
            }
            drawPath(Path().apply { moveTo(o.x + rr * 0.6f, o.y + rr * 0.7f); cubicTo(w * 0.62f, piso - h * 0.01f, w * 0.72f, piso - h * 0.02f, w * 0.86f, piso - h * 0.005f) }, t.c(ANIL), style = Stroke(1f * u))
            drawCircle(t.b(0.45f), rr * 0.2f, o - Offset(rr * 0.4f, rr * 0.4f))
        }
    }

    override val rotulo = Rect(0.08f, 0.53f, 0.92f, 0.81f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.amiri
    override val salida = -SALIDA
    override val placa = Rect(0.37f, 0.02f, 0.63f, 0.18f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Un alicatado claro de estrellas de ocho puntas, con lazos de colores tenues.
        val fondo = if (oscuro) mezcla(MARFIL, Color.Black, 0.6f) else MARFIL
        drawRect(fondo)
        val p = 54 * u
        var y = 0f; var fila = 0
        while (y < h + p) {
            var x = if (fila % 2 == 0) 0f else p / 2
            while (x < w + p) {
                val col = if ((fila + (x / p).toInt()) % 2 == 0) VERDE else AZUL
                drawPath(estrella(Offset(x, y), p * 0.36f, 8, 0.7f), col.copy(alpha = 0.16f))
                drawPath(estrella(Offset(x, y), p * 0.36f, 8, 0.7f), col.copy(alpha = 0.32f), style = Stroke(1.2f * u))
                drawCircle(OCRE.copy(alpha = 0.3f), p * 0.07f, Offset(x, y))
                x += p
            }
            y += p / 2; fila++
        }
        // Las paredes de cedro, con su banda de arquillos tallados.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(CEDRO, oscuro), r.topLeft, r.size) }
        var ax = pl
        while (ax < w - pl) { drawArc(t(CEDRO_OSC, oscuro), 180f, 180f, false, Offset(ax, pl * 0.3f), Size(7 * u, pl * 0.8f), style = Stroke(1f * u)); ax += 8 * u }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) MARFIL else TINTA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(2.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.7f else 1f
        drawRect(MARFIL.copy(alpha = 0.35f))
        // El recuadro (jadwal): un filete negro fino, uno de oro y uno azul por dentro.
        val m = 5 * u * g
        drawRect(NEGRO.copy(alpha = 0.6f), Offset(m - 1.6f * u * g, m - 1.6f * u * g), Size(w - 2 * m + 3.2f * u * g, h - 2 * m + 3.2f * u * g), style = Stroke(0.5f * u))
        drawRect(LATON, Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(1.5f * u * g))
        drawRect(AZUL.copy(alpha = 0.7f), Offset(m + 2.4f * u * g, m + 2.4f * u * g), Size(w - 2 * m - 4.8f * u * g, h - 2 * m - 4.8f * u * g), style = Stroke(0.6f * u))
        // Dos medallones dorados en el margen, a media altura.
        listOf(m, w - m).forEach { x ->
            val c = Offset(x, h / 2)
            drawCircle(LATON, 3.6f * u * g, c)
            drawPath(estrella(c, 3.2f * u * g, 8, 0.55f), AZUL.copy(alpha = 0.8f))
            drawCircle(MARFIL, 1 * u * g, c)
        }
    }

    override val margenes = MargenesFicha(14.dp, 16.dp, 14.dp, 26.dp, 30.dp, 28.dp)
    override val proporcionFicha = 0.82f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "hecho" -> selloHecho(TERRACOTA, p.color)
            "convence" -> drawPath(estrella(c, s * 0.46f, 8, 0.7f), VERDE)
            // Dos aros entrelazados.
            "piensa" -> {
                drawCircle(AZUL, s * 0.24f, Offset(c.x - s * 0.13f, c.y), style = Stroke(s * 0.08f))
                drawCircle(AZUL, s * 0.24f, Offset(c.x + s * 0.13f, c.y), style = Stroke(s * 0.08f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(TERRACOTA)
            else -> {}
        }
    }

    /** Una roseta de oro (shamsa) con su centro azul. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(estrella(c, s * 0.48f, 12, 0.72f), LATON)
        drawCircle(AZUL, s * 0.2f, c)
        drawCircle(LATON, s * 0.07f, c)
    }

    override val desorden = 0f
}
