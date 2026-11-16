package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

// =====================================================================
//  Urbe
// =====================================================================

/**
 * Urbe: la edícola, el quiosco de periódicos de hierro verde de Roma, vista de frente y arrimada a un muro romano que llena el
 * fondo (sin cielo ni calle): estuco ocre anaranjado desconchado que deja ver el ladrillo, con su zócalo de travertino. En el
 * muro, arriba, la placa de calle de mármol (sus letras son de un alfabeto que no existe, como las del letrero, los carteles
 * y las portadas), los restos de carteles pegados y arrancados, los cables con su caja de empalme, la bajante de zinc y, a la
 * derecha, lo más alto: la hornacina de la madonnella con su tejadillo, su imagen (una silueta), su bote de flores y su
 * lamparilla de vidrio rojo. De día el sol entra entre las casas de arriba a la izquierda: la sombra de la casa de enfrente
 * cae en diagonal sobre el muro y lo que cuelga de él, y el quiosco deja la suya a la derecha y en el suelo. El quiosco tiene
 * su techo de zinc con juntas y canalón, su letrero crema, el lambrequín festoneado y calado, los pilares acanalados, el
 * costado izquierdo abatido con dos revistas, el mostrador de tabla y un friso de rombos de fundición; el hierro pintado tiene
 * desconchones y óxido. El punto focal, a la izquierda: los diarios y las revistas colgados con pinzas de un alambre bajo el
 * toldo; a su lado cuelga la bombilla con su platillo de esmalte y, por dentro, se ven las revistas de las repisas, en
 * penumbra. En el mostrador, el bote de bolígrafos, la cafetera moka con su tacita, el expositor giratorio de postales, la
 * radio con su antena y la maceta de geranios; de una percha del pilar izquierdo cuelga el casco de motorino. Al pie, sobre una
 * franja de sampietrini, tres fardos de periódicos atados con cuerda (cortados por la izquierda), un cubo de zinc con un trapo
 * y una caja de botellas (cortada por la derecha). Cada tema es, según la semilla, una latita de boletos (roja, verde azulada,
 * mostaza o azul polvo) con su ranura y los boletos que asoman, un fajo de revistas sujeto con una goma (la portada de delante
 * y las demás asomando detrás) o una caja de cartón de postales o de cromos con la tapa apoyada detrás; más llenos cuanto más
 * guardan, y el tema en una placa esmaltada atornillada a su frente. Lo que no tiene tema es una caja de zapatos con boletos
 * sueltos y su rótulo en un cartón atado; un hueco libre guarda un diario doblado con unas monedas, o un mechero y unas
 * cerillas. Al abrir uno, sale hacia arriba. De noche el quiosco está cerrado a medias (la persiana baja a la derecha) y alumbra
 * la bombilla bajo el toldo: los diarios, el mostrador y las dos primeras filas encendidos, y la luz cae fila a fila hasta
 * perderse abajo; arriba, donde el toldo tapa la bombilla, solo la lamparilla de la hornacina tiñe de rojo su trozo de muro y
 * el techo. Por dentro, lata litografiada. Las fichas son boletos de tranvía con las casillas para perforar a los lados.
 */
object IdeasUrbe : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta (de los cuadros de Roma)
    /** El estuco ocre anaranjado del muro, su luz de sol y su sombra; el ladrillo de debajo; el travertino y el mármol. */
    private val ESTUCO = Color(0xFFD08A4E)
    private val ESTUCO_LUZ = Color(0xFFEDB978)
    private val ESTUCO_OSC = Color(0xFFA2643A)
    private val LADRILLO = Color(0xFF9C4C33)
    private val TRAVERTINO = Color(0xFFDDD0B2)
    private val MARMOL = Color(0xFFEAE5D8)
    /** El hierro verde del quiosco, su luz y su sombra; el óxido. */
    private val VERDE = Color(0xFF2F4A36)
    private val VERDE_LUZ = Color(0xFF4E6E52)
    private val VERDE_OSC = Color(0xFF1C2E22)
    private val OXIDO = Color(0xFF8C4A26)
    /** El zinc del techo, del cubo y de la moka. */
    private val ZINC = Color(0xFF9AA19C)
    private val ZINC_OSC = Color(0xFF636B67)
    /** El papel de diario, la tinta, la crema de los letreros y el cartón. */
    private val PAPEL = Color(0xFFE6DCC2)
    private val TINTA = Color(0xFF2B2420)
    private val CREMA = Color(0xFFF2E8CF)
    private val KRAFT = Color(0xFFAE8957)
    /** La lata roja, el oro de sus filetes, el azul polvo y la mostaza de las portadas, la hoja de los geranios, el adoquín. */
    private val LATA = Color(0xFFB0322A)
    private val LATA_OSC = Color(0xFF7A1E18)
    private val ORO = Color(0xFFC9A24C)
    private val AZUL = Color(0xFF6C8698)
    private val OCRE = Color(0xFFE0C47A)
    private val HOJA = Color(0xFF55743A)
    private val ADOQUIN = Color(0xFF6E6862)
    /** La bombilla, la lamparilla de la madonnella y la noche (azul violeta, como la azotea de agosto). */
    private val BOMBILLA = Color(0xFFFFC56E)
    private val LAMPARILLA = Color(0xFFE5482E)
    private val NOCHE = Color(0xFF161327)
    /**
     * El esmalte de la placa de noche, lejos de la bombilla: con [TINTA] da más de 5:1 en el título; cerca de la bombilla sube
     * hacia [CREMA] con un poco de luz.
     */
    private val ESMALTE_LEJOS = Color(0xFF9E9783)
    /** Las de las fichas (sin cambios). */
    private val ROSA = Color(0xFFE8B4A8)
    private val VERDE_BOLETO = Color(0xFFB8D0A8)
    private val BRONCE = Color(0xFFA8783A)

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.38f
    override val corona = 0.87f
    override val base = 0.3f
    override val lado = 0.1f
    override val hueco = 0.04f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** El quiosco, en fracciones del ancho: sus costados, el techo, el letrero, el lambrequín y el mostrador. */
    private const val QX0 = 0.06f
    private const val QX1 = 0.94f
    private const val PILAR = 0.03f
    private const val TECHO0 = 0.305f
    private const val ALERO = 0.38f
    private const val LETRERO1 = 0.458f
    private const val LAMBRE1 = 0.5f
    private const val SUP = 0.76f
    private const val FRENTE = 0.782f
    private const val CANTO = 0.812f
    /** En la base: el rincón del muro con el suelo, donde pisa el quiosco y lo alto del zócalo del quiosco. */
    private const val SUELO = 0.3f
    private const val PISA = 0.42f
    private const val ZOCALO = 0.36f
    /** El piso de cada hueco (la repisa) y cuánto sube su contenido al abrirlo, en fracción de su alto. */
    private const val PISO = 0.9f
    private const val SALIDA = 0.07f

    // ---------------------------------------------------------------- la luz y la noche

    /** La bombilla bajo el toldo y la lamparilla de la hornacina, en fracciones del ancho. */
    private val LUZ = Offset(0.575f, 0.578f)
    private val MADONNA = Offset(0.752f, 0.212f)

    /**
     * Cuánto le llega de la bombilla al muro en [p]: cae con la distancia; el toldo tapa lo de arriba (el letrero recibe
     * poco y el muro sobre el techo, casi nada).
     */
    private fun luzMuro(p: Offset): Float {
        val dx = p.x - LUZ.x
        val dy = (p.y - LUZ.y) * (if (p.y > LUZ.y) 0.8f else 1.3f)
        val radial = 1.4f * exp(-3.5f * sqrt(dx * dx + dy * dy))
        return (radial * (if (p.y < ALERO) 0.08f else if (p.y < LAMBRE1 - 0.01f) 0.16f else 1f)).coerceIn(0.03f, 1f)
    }

    /** Cuánto le llega de la bombilla a [p], en el quiosco y en lo que tiene encima y al pie. */
    private fun luz(p: Offset): Float = maxOf(luzMuro(p), luzCuerpo(p))

    /**
     * La luz que la bombilla echa sobre el frente del quiosco, fila a fila: la primera encendida, la segunda a medias, la
     * tercera tenue, la cuarta y la quinta casi perdidas. Entre los centros de dos filas se pasa de una cifra a la otra.
     */
    private val CAIDA = listOf(0.82f, 0.54f, 0.27f, 0.095f, 0.045f, 0.03f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CANTO || p.x < QX0 - 0.03f || p.x > QX1 + 0.03f) return 0f
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val y0 = corona + hueco + altoH / 2
        val k = if (p.y < y0) 0.92f + (CAIDA[0] - 0.92f) * (p.y - CANTO) / (y0 - CANTO) else {
            val f = ((p.y - y0) / (altoH + hueco)).coerceAtMost(CAIDA.size - 1.001f)
            val i = f.toInt()
            CAIDA[i] + (CAIDA[i + 1] - CAIDA[i]) * (f - i)
        }
        return k * (1f - 0.55f * (abs(p.x - LUZ.x) - 0.08f).coerceAtLeast(0f)).coerceIn(0.72f, 1f)
    }

    /** Cuánto le llega de la lamparilla roja a [p]: la hornacina y el muro de su alrededor. */
    private fun luzRoja(p: Offset): Float {
        val dx = p.x - MADONNA.x; val dy = p.y - MADONNA.y
        return (1.15f * exp(-6.5f * sqrt(dx * dx + dy * dy))).coerceIn(0f, 1f)
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono, con el aire de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.14f + NOCHE.red * 0.8f, c.green * 0.14f + NOCHE.green * 0.8f, c.blue * 0.14f + NOCHE.blue * 0.8f, c.alpha)

    /**
     * Cómo se pinta algo según dónde está. De día, tal cual (con [dia], un poco más claro). De noche, de su color hondo hacia
     * el suyo según [k] (lo que le llega de la bombilla), teñido de su luz; [r] es lo que le llega de la lamparilla roja.
     */
    private class Tono(val o: Boolean, val k: Float, val r: Float = 0f, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, maxOf(1.05f * k, 0.55f * r).coerceAtMost(0.9f))
        return mezcla(mezcla(v, BOMBILLA, 0.16f * k), LAMPARILLA, 0.3f * r)
    }

    /** La sombra: de día, violeta hondo; de noche, la noche. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la bombilla (o rojo, junto a la hornacina). */
    private fun Tono.b(a: Float): Color =
        if (!o) luzMueble(a) else if (r > k) LAMPARILLA.copy(alpha = (a * 0.8f * r).coerceIn(0f, 1f)) else BOMBILLA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f))

    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset, dia: Float = 0f): Tono {
        val f = p / anchoW
        return if (esNoche) Tono(true, luz(f), luzRoja(f)) else Tono(false, 0f, 0f, dia)
    }

    /** De noche la luz viene de la bombilla: lo que está a su izquierda la recibe por la derecha. */
    private fun luzDerecha(x: Float) = esNoche && x / anchoW < LUZ.x - 0.03f

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    private fun revolver(s: Int): Int {
        // La sal reparte bien las variantes y los colores en la hoja de ejemplo (ninguna más de tres veces ni dos seguidas).
        var x = (s xor 1703) * -1640531535
        x = x xor (x ushr 15); x *= 0x2c1b3c6d; x = x xor (x ushr 12)
        return x and 0x7fffffff
    }

    /** La sombra de algo en lo que tiene detrás: de día corrida a la derecha y abajo; de noche, hacia fuera de la bombilla. */
    private fun DrawScope.sombraDetras(p: Path, desde: Offset, a: Float = 0.18f) {
        val w = anchoW
        if (!esNoche) translate(w * 0.016f, w * 0.011f) { drawPath(p, sombraMueble(a)) }
        else {
            val k = luz(desde / w)
            if (k > 0.15f) {
                val dx = ((desde.x / w - LUZ.x) / 0.3f).coerceIn(-1f, 1f)
                translate(w * 0.03f * dx, w * 0.016f) { drawPath(p, NOCHE.copy(alpha = (0.6f * k).coerceAtMost(0.5f))) }
            }
        }
    }

    /** La sombra de contacto de lo que pisa, corrida a la derecha. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float) =
        drawOval(if (esNoche) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /** La luz de un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte). */
    private fun DrawScope.cilindro(r: Rect, t: Tono, fuerza: Float = 1f, der: Boolean = false) {
        fun x(f: Float, a: Float) = if (der) r.right - r.width * (f + a) else r.left + r.width * f
        drawRect(t.b(0.2f * fuerza), Offset(x(0f, 0.3f), r.top), Size(r.width * 0.3f, r.height))
        drawRect(t.b(0.36f * fuerza), Offset(x(0.15f, 0.08f), r.top), Size(r.width * 0.08f, r.height))
        drawRect(t.s(0.16f * fuerza), Offset(x(0.58f, 0.42f), r.top), Size(r.width * 0.42f, r.height))
        drawRect(t.s(0.2f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
    }

    /** El filo de la bombilla en un recipiente, de noche: canto de arriba y el costado que la mira. */
    private fun DrawScope.filo(r: Rect, t: Tono, der: Boolean) {
        if (!t.o || t.k < 0.06f) return
        val u = density
        drawRect(BOMBILLA.copy(alpha = (0.85f * t.k).coerceAtMost(0.75f)), r.topLeft, Size(r.width, 1.5f * u))
        drawRect(BOMBILLA.copy(alpha = (0.3f * t.k).coerceAtMost(0.3f)), Offset(r.left, r.top + 1.5f * u), Size(r.width, 3f * u))
        val x = if (der) r.right - 1.3f * u else r.left
        drawRect(BOMBILLA.copy(alpha = (0.6f * t.k).coerceAtMost(0.55f)), Offset(x, r.top), Size(1.3f * u, r.height))
    }

    /** El destello de la luz en el metal o el vidrio. */
    private fun DrawScope.destello(c: Offset, r: Float, t: Tono) {
        if (t.o) { if (t.k > 0.06f) { drawCircle(BOMBILLA.copy(alpha = (0.35f * t.k).coerceAtMost(0.3f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(luzMueble(0.85f), r, c)
    }

    /** Un remache o un tornillo, con su sombra y su brillo. */
    private fun DrawScope.remache(p: Offset, r: Float, t: Tono, col: Color = VERDE_LUZ) {
        drawCircle(t.s(0.35f), r * 1.05f, p + Offset(r * 0.5f, r * 0.5f))
        drawCircle(t.c(col), r, p)
        drawCircle(t.b(0.55f), r * 0.4f, p - Offset(r * 0.3f, r * 0.3f))
    }

    /** Un contorno roto (desconchón, cartel arrancado): [n] puntos alrededor de [c], con entrantes. */
    private fun roto(c: Offset, rx: Float, ry: Float, semilla: Int, n: Int = 11): Path = Path().apply {
        for (k in 0 until n) {
            val a = 6.2832f * k / n
            val f = 0.62f + 0.45f * azar(semilla, k) * (if (k % 3 == 0) 0.6f else 1f)
            val x = c.x + rx * f * kotlin.math.cos(a); val y = c.y + ry * f * kotlin.math.sin(a)
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    // ---------------------------------------------------------------- el mueble entero

    /** Lo mismo para lo que está en el muro: ahí la luz de la bombilla solo llega por los lados del toldo. */
    private fun tm(p: Offset): Tono {
        val f = p / anchoW
        return if (esNoche) Tono(true, luzMuro(f), luzRoja(f)) else Tono(false, 0f)
    }

    /**
     * La sombra de la casa de enfrente sobre el muro y lo que cuelga de él (solo de día): el sol entra entre las casas en una
     * franja oblicua, de arriba a la izquierda hacia abajo a la derecha; lo de arriba a la derecha y lo de abajo quedan en
     * sombra, con su borde de penumbra.
     */
    private fun DrawScope.sombraDeCasas(w: Float, suelo: Float) {
        if (esNoche) return
        // Arriba a la derecha: el alero de enfrente.
        drawPath(poligono(w * 0.36f, 0f, w, w * 0.215f, w, 0f), sombraMueble(0.14f))
        drawPath(poligono(w * 0.4f, 0f, w, w * 0.2f, w, 0f), sombraMueble(0.2f))
        // Abajo: de la franja para abajo, el muro en sombra (se ve a los lados del quiosco).
        drawPath(poligono(0f, w * 0.5f, w, w * 0.86f, w, suelo, 0f, suelo), sombraMueble(0.14f))
        drawPath(poligono(0f, w * 0.52f, w, w * 0.88f, w, suelo, 0f, suelo), sombraMueble(0.2f))
    }

    /**
     * Una letra pintada o grabada de un alfabeto que no existe: un palo o dos y una barra o dos, de ocho formas. Con ellas se
     * escriben el letrero, la placa de la calle, los carteles y las portadas sin que digan nada.
     */
    private fun DrawScope.letraFalsa(x: Float, y: Float, lw: Float, lh: Float, forma: Int, tinta: Color) {
        val g = lw * 0.3f; val gh = lh * 0.2f
        fun palo(fx: Float) = drawRect(tinta, Offset(x + fx, y), Size(g, lh))
        fun barra(fy: Float, largo: Float = lw) = drawRect(tinta, Offset(x, y + fy), Size(largo, gh))
        when (Math.floorMod(forma, 8)) {
            0 -> { palo(0f); palo(lw - g); barra(0f) }
            1 -> { palo(0f); palo(lw - g); barra(lh - gh) }
            2 -> { palo(0f); barra(0f); barra(lh - gh) }
            3 -> { palo(lw - g); barra(0f); barra(lh - gh) }
            4 -> { palo((lw - g) / 2); barra(lh - gh) }
            5 -> { palo(0f); barra((lh - gh) / 2) }
            6 -> { palo(lw - g); barra((lh - gh) / 2) }
            else -> { barra(0f); barra(lh - gh); barra((lh - gh) / 2, lw * 0.6f) }
        }
    }

    /** Un renglón de letras que no dicen nada, centrado en [cx]: [formas] trae la forma de cada una (-1, un espacio). */
    private fun DrawScope.renglonFalso(cx: Float, y: Float, alto: Float, paso: Float, formas: List<Int>, tinta: Color) {
        val x0 = cx - formas.size * paso / 2
        formas.forEachIndexed { i, f -> if (f >= 0) letraFalsa(x0 + i * paso, y, paso * 0.7f, alto, f, tinta) }
    }

    /** Una fracción del ancho del mueble, en píxeles. */
    private fun fw(f: Float) = anchoW * f

    /** Un fardo: el frente con los cantos de los diarios doblados, la cara de arriba con su cabecera y la cuerda con su nudo. */
    private fun DrawScope.fardo(r: Rect, giro: Float, s: Int, papel: Color) {
        val u = density
        val w = anchoW
        val t = tn(r.center)
        val f = w * 0.026f
        val dx = w * 0.024f
        rotate(giro, Offset(r.center.x, r.bottom)) {
            drawRect(t.s(0.3f), Offset(r.left + w * 0.016f, r.top - f + w * 0.01f), Size(r.width + dx, r.height + f))
            // La cara de arriba, en escorzo: el diario de encima con su cabecera y su foto.
            val arriba = poligono(r.left, r.top, r.left + dx, r.top - f, r.right + dx, r.top - f, r.right, r.top)
            drawPath(arriba, t.c(mezcla(papel, CREMA, 0.5f)))
            clipPath(arriba) {
                drawRect(t.c(TINTA).copy(alpha = 0.75f), Offset(r.left + r.width * 0.32f, r.top - f * 0.82f), Size(r.width * 0.42f, f * 0.22f))
                drawRect(t.c(mezcla(AZUL, papel, 0.4f)), Offset(r.left + r.width * 0.16f, r.top - f * 0.5f), Size(r.width * 0.25f, f * 0.4f))
                for (k in 0 until 2) drawRect(t.c(TINTA).copy(alpha = 0.32f), Offset(r.left + r.width * 0.46f, r.top - f * (0.44f - 0.18f * k)), Size(r.width * 0.46f, 0.8f * u))
            }
            // El costado derecho, en sombra.
            val lado = poligono(r.right, r.top, r.right + dx, r.top - f, r.right + dx, r.bottom - f, r.right, r.bottom)
            drawPath(lado, t.c(mezcla(papel, KRAFT, 0.35f)))
            drawPath(lado, t.s(0.3f))
            // El frente: los lomos doblados, capa sobre capa, con algún suplemento de color.
            drawRect(t.c(papel), r.topLeft, r.size)
            var y = r.top + 1.5f * u; var k = 0
            while (y < r.bottom) {
                val col = if (k % 7 == 3) mezcla(ROSA, papel, 0.35f) else if (k % 9 == 5) mezcla(AZUL, papel, 0.6f) else mezcla(papel, KRAFT, 0.15f + 0.4f * azar(s, k))
                drawRect(t.c(col), Offset(r.left, y), Size(r.width, 1f * u))
                y += 2.5f * u; k++
            }
            drawRect(t.b(0.3f), r.topLeft, Size(r.width, r.height * 0.16f))
            drawRect(t.s(0.16f), Offset(r.left, r.bottom - r.height * 0.28f), Size(r.width, r.height * 0.28f))
            // La cuerda en cruz, que muerde el canto, y su nudo arriba.
            val cx = r.left + r.width * 0.6f
            val cuerda = t.c(mezcla(KRAFT, OXIDO, 0.25f))
            drawRect(t.s(0.28f), Offset(cx + 1f * u, r.top), Size(1.6f * u, r.height))
            drawLine(cuerda, Offset(cx, r.top), Offset(cx, r.bottom), 2f * u)
            drawLine(t.b(0.4f), Offset(cx - 0.7f * u, r.top), Offset(cx - 0.7f * u, r.bottom), 0.6f * u)
            drawLine(cuerda, Offset(cx, r.top), Offset(cx + dx, r.top - f), 2f * u)
            drawLine(cuerda, Offset(r.left + dx * 0.5f, r.top - f * 0.5f), Offset(r.right + dx * 0.5f, r.top - f * 0.5f), 1.6f * u)
            val nudo = Offset(cx + dx * 0.5f, r.top - f * 0.5f)
            drawLine(cuerda, nudo, nudo + Offset(w * 0.016f, -w * 0.012f), 1.4f * u)
            drawLine(cuerda, nudo, nudo + Offset(-w * 0.012f, -w * 0.015f), 1.4f * u)
            drawCircle(cuerda, 2.4f * u, nudo)
            drawCircle(t.b(0.4f), 1f * u, nudo - Offset(0.8f * u, 0.8f * u))
        }
    }

    /** La sombra de lo que pisa la repisa de un hueco, corrida a la derecha. */
    private fun DrawScope.pisa(t: Tono, cx: Float, y: Float, ancho: Float, alto: Float) =
        drawOval(t.s(0.4f), Offset(cx - ancho * 0.47f, y - alto / 2), Size(ancho, alto))

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val o = oscuro
        esNoche = o
        val w = size.width; val alto = size.height
        anchoW = w
        val b = d.base; val hb = b.height
        val suelo = b.top + hb * SUELO

        // ---- el muro, con lo que tiene, la sombra de la casa de enfrente y el suelo de sampietrini
        muro(w, suelo)
        placaCalle(Rect(w * 0.05f, w * 0.03f, w * 0.33f, w * 0.14f))
        carteles(w)
        hornacina(w)
        cableYBajante(w, suelo)
        sombraDeCasas(w, suelo)
        sampietrini(w, suelo, alto)

        // ---- el quiosco: el cuerpo, la parte alta por dentro, el costado abatido, el mostrador y el techo
        quiosco(d, b)
        parteAlta(w)
        costado(w)
        mostrador(w)
        techo(w)
        if (o) persiana(w)

        // ---- lo colgado
        diarios(w)
        bombilla(w)
        casco(Offset(w * 0.04f, w * 0.935f))

        // ---- en el mostrador, de atrás adelante
        geranios(Offset(w * 0.9f, w * (SUP + 0.004f)))
        postales(Offset(w * 0.712f, w * (SUP + 0.006f)))
        boligrafos(Offset(w * 0.14f, w * (FRENTE - 0.004f)))
        moka(Offset(w * 0.598f, w * (FRENTE - 0.003f)))
        tacita(Offset(w * 0.658f, w * (FRENTE - 0.002f)))
        radio(Offset(w * 0.812f, w * (FRENTE - 0.002f)))

        // ---- al pie
        fardos(b, w, hb)
        cubo(Offset(w * 0.675f, b.top + hb * 0.93f))
        botellas(Offset(w * 0.87f, b.top + hb * 0.985f))

        // ---- la placa de «y N más», esmaltada en el zócalo del quiosco
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            drawRoundRect(t.s(0.3f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size, CornerRadius(3f * u))
            drawRoundRect(if (o) ESMALTE_LEJOS else CREMA, r.topLeft, r.size, CornerRadius(3f * u))
            drawRoundRect(t.c(VERDE), Offset(r.left + 2f * u, r.top + 2f * u), Size(r.width - 4f * u, r.height - 4f * u), CornerRadius(2f * u), style = Stroke(1.1f * u))
            remache(Offset(r.left + 4.5f * u, r.center.y), 1.2f * u, t, ZINC)
            remache(Offset(r.right - 4.5f * u, r.center.y), 1.2f * u, t, ZINC)
        }

        // ---- la luz de la bombilla y de la lamparilla, por encima de lo que tienen cerca
        if (o) {
            val bb = LUZ * w
            mancha(bb + Offset(-w * 0.03f, w * 0.07f), w * 0.55f, BOMBILLA.copy(alpha = 0.1f))
            mancha(bb + Offset(0f, w * 0.03f), w * 0.22f, BOMBILLA.copy(alpha = 0.2f))
            mancha(MADONNA * w + Offset(0f, -w * 0.01f), w * 0.13f, LAMPARILLA.copy(alpha = 0.2f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    // ---------------------------------------------------------------- el muro

    /**
     * El muro de estuco ocre anaranjado: de día, al sol que entra entre las casas (la sombra de la casa de enfrente se pinta
     * después, encima de lo que cuelga del muro: [sombraDeCasas]); de noche, hundido en azul violeta, con lo que le llega de
     * la bombilla a los lados del quiosco y el rojo de la lamparilla arriba. Pinceladas, chorreras de humedad, una grieta,
     * desconchones con su ladrillo y, abajo, el zócalo de travertino con sus juntas y su mugre.
     */
    private fun DrawScope.muro(w: Float, suelo: Float) {
        val u = density
        val o = esNoche
        val zoc = suelo - w * 0.15f
        if (!o) {
            drawRect(mezcla(ESTUCO, ESTUCO_LUZ, 0.42f), Offset.Zero, Size(w, zoc))
            mancha(Offset(w * 0.1f, w * 0.05f), w * 0.5f, luzMueble(0.3f))
        } else {
            drawRect(hondo(ESTUCO), Offset.Zero, Size(w, zoc))
            fun k(d: Float) = Tono(true, (1.4f * exp(-3.5f * d)).coerceIn(0.03f, 1f)).c(ESTUCO)
            drawRect(Brush.radialGradient(0f to k(0f), 0.15f to k(0.15f), 0.3f to k(0.3f), 0.5f to k(0.5f), 0.75f to k(0.75f), 1f to k(1f),
                center = LUZ * w, radius = w), Offset(0f, w * ALERO), Size(w, zoc - w * ALERO))
            // Sobre el techo no llega la bombilla: solo el rojo de la lamparilla.
            drawRect(Brush.radialGradient(listOf(Tono(true, 0.03f, 0.95f).c(ESTUCO), Tono(true, 0.03f, 0.4f).c(ESTUCO), hondo(ESTUCO)), MADONNA * w, w * 0.42f),
                Offset.Zero, Size(w, w * ALERO))
        }
        pinceladas(Rect(0f, 0f, w, zoc), if (o) NOCHE else ESTUCO_OSC, 5001, 50, u, 0.11f)
        pinceladas(Rect(0f, 0f, w, zoc), if (o) NOCHE else ESTUCO_LUZ, 5002, 26, u, 0.12f)
        // Parches de estuco más nuevo, de otro tono.
        listOf(Rect(w * 0.6f, w * 0.2f, w * 0.72f, w * 0.31f), Rect(-w * 0.02f, w * 0.2f, w * 0.1f, w * 0.3f)).forEachIndexed { i, r ->
            drawPath(roto(r.center, r.width * 0.6f, r.height * 0.6f, 5005 + i, 9), tm(r.center).c(mezcla(ESTUCO, LADRILLO, 0.3f)).copy(alpha = 0.3f))
        }
        // Las chorreras de humedad bajo la placa, bajo la repisa de la hornacina y junto a la bajante.
        listOf(0.085f to 0.14f, 0.3f to 0.14f, 0.7f to 0.25f, 0.975f to 0.6f, 0.02f to 1.2f).forEachIndexed { i, (fx, fy) ->
            val p = Offset(w * fx, w * fy)
            val t = tm(p)
            drawPath(Path().apply {
                moveTo(p.x - w * 0.006f, p.y)
                cubicTo(p.x - w * 0.008f, p.y + w * 0.05f, p.x + w * 0.004f, p.y + w * 0.08f, p.x, p.y + w * (0.1f + 0.06f * azar(5003, i)))
                lineTo(p.x + w * 0.01f, p.y + w * 0.06f)
                lineTo(p.x + w * 0.008f, p.y)
                close()
            }, t.s(0.12f))
        }
        // Una grieta fina que baja del borde de arriba.
        val tg = tm(Offset(w * 0.6f, w * 0.05f))
        val grieta = Path().apply {
            moveTo(w * 0.635f, 0f); lineTo(w * 0.628f, w * 0.022f); lineTo(w * 0.64f, w * 0.04f); lineTo(w * 0.632f, w * 0.058f)
            lineTo(w * 0.612f, w * 0.066f); lineTo(w * 0.618f, w * 0.078f)
        }
        drawPath(grieta, tg.s(0.4f), style = Stroke(1f * u))
        translate(1f * u, 0.6f * u) { drawPath(grieta, tg.b(0.35f), style = Stroke(0.6f * u)) }
        // Los desconchones: el estuco caído deja ver el ladrillo.
        desconchon(Offset(w * 0.19f, w * 0.262f), w * 0.07f, w * 0.036f, 5010)
        desconchon(Offset(w * 0.93f, w * 0.015f), w * 0.05f, w * 0.03f, 5011)
        desconchon(Offset(w * 0.02f, w * 1.72f), w * 0.045f, w * 0.07f, 5012)
        desconchon(Offset(w * 0.985f, w * 2.2f), w * 0.04f, w * 0.06f, 5013)
        desconchon(Offset(w * 0.015f, w * 0.44f), w * 0.03f, w * 0.035f, 5014)
        // El zócalo de travertino: sillares con sus juntas, los poros y la mugre de abajo.
        val tz = tm(Offset(w * 0.5f, zoc))
        drawRect(tz.c(TRAVERTINO), Offset(0f, zoc), Size(w, suelo - zoc))
        if (!o) drawRect(sombraMueble(0.12f), Offset(0f, zoc), Size(w, suelo - zoc))
        var x = -w * 0.06f; var j = 0
        while (x < w) {
            val tt = tm(Offset(x, (zoc + suelo) / 2))
            drawLine(tt.s(0.35f), Offset(x, zoc), Offset(x, suelo), 1.1f * u)
            drawLine(tt.b(0.3f), Offset(x + 1.1f * u, zoc), Offset(x + 1.1f * u, suelo), 0.7f * u)
            for (q in 0 until 6) drawLine(tt.s(0.2f), Offset(x + w * 0.02f + w * 0.2f * azar(5020 + j, q), zoc + (suelo - zoc) * azar(5030 + j, q)),
                Offset(x + w * 0.035f + w * 0.2f * azar(5020 + j, q), zoc + (suelo - zoc) * azar(5030 + j, q)), 0.8f * u)
            x += w * 0.24f; j++
        }
        drawRect(tz.b(0.5f), Offset(0f, zoc), Size(w, 1.2f * u))
        drawRect(tz.s(0.25f), Offset(0f, zoc + 1.2f * u), Size(w, 2f * u))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, if (o) NOCHE.copy(alpha = 0.5f) else sombraMueble(0.3f)), suelo - w * 0.05f, suelo), Offset(0f, suelo - w * 0.05f), Size(w, w * 0.05f))
    }

    /** Un desconchón: el hueco del estuco con las hiladas de ladrillo y la llaga de mortero, y el canto del estuco. */
    private fun DrawScope.desconchon(c: Offset, rx: Float, ry: Float, semilla: Int) {
        val u = density
        val t = tn(c)
        val forma = roto(c, rx, ry, semilla)
        drawPath(forma, t.c(LADRILLO))
        clipPath(forma) {
            val hl = ry * 0.36f
            var y = c.y - ry; var f = 0
            while (y < c.y + ry) {
                drawRect(t.c(mezcla(TRAVERTINO, ESTUCO_OSC, 0.4f)), Offset(c.x - rx, y), Size(rx * 2, 1.2f * u))
                var x = c.x - rx + (if (f % 2 == 0) 0f else hl)
                var q = 0
                while (x < c.x + rx) {
                    drawRect(t.c(mezcla(TRAVERTINO, ESTUCO_OSC, 0.4f)), Offset(x, y), Size(1.1f * u, hl))
                    drawRect(t.c(mezcla(LADRILLO, if (azar(semilla, f * 9 + q) > 0.5f) ESTUCO_OSC else LATA_OSC, 0.3f * azar(semilla + 1, f * 9 + q))), Offset(x + 1.1f * u, y + 1.2f * u), Size(hl * 2 - 1.1f * u, hl - 1.2f * u))
                    x += hl * 2; q++
                }
                y += hl; f++
            }
            translate(1.4f * u, 1.4f * u) { drawPath(forma, t.s(0.45f), style = Stroke(2.2f * u)) }
        }
        translate(1f * u, 1f * u) { drawPath(forma, t.b(0.45f), style = Stroke(1f * u)) }
    }

    /**
     * La placa de calle de mármol: la losa con su moldura, tres renglones de letras grabadas y pintadas de rojo oscuro (no
     * dicen nada), las vetas, las dos grapas de hierro y el óxido que chorrea de una.
     */
    private fun DrawScope.placaCalle(r: Rect) {
        val u = density
        val t = tm(r.center)
        val forma = Path().apply { addRect(r) }
        sombraDetras(forma, r.center, 0.3f)
        drawRect(t.c(MARMOL), r.topLeft, r.size)
        val m = r.height * 0.11f
        val dentro = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
        drawRect(t.b(0.6f), r.topLeft, Size(r.width, m * 0.45f))
        drawRect(t.b(0.3f), r.topLeft, Size(m * 0.45f, r.height))
        drawRect(t.s(0.24f), Offset(r.left, r.bottom - m * 0.5f), Size(r.width, m * 0.5f))
        drawRect(t.s(0.2f), Offset(r.right - m * 0.5f, r.top), Size(m * 0.5f, r.height))
        drawRect(t.c(mezcla(MARMOL, TRAVERTINO, 0.4f)), dentro.topLeft, dentro.size)
        drawRect(t.s(0.25f), dentro.topLeft, Size(dentro.width, 1.2f * u))
        drawRect(t.s(0.18f), dentro.topLeft, Size(1.2f * u, dentro.height))
        drawRect(t.b(0.5f), Offset(dentro.left, dentro.bottom - 1f * u), Size(dentro.width, 1f * u))
        clipRect(dentro.left, dentro.top, dentro.right, dentro.bottom) {
            // Las vetas grises del mármol.
            listOf(0.12f, 0.47f, 0.8f).forEachIndexed { i, f ->
                val x = dentro.left + dentro.width * f
                drawLine(t.c(ZINC).copy(alpha = 0.35f), Offset(x, dentro.top), Offset(x + dentro.width * (0.1f + 0.08f * i), dentro.bottom), (0.8f + 0.5f * i) * u)
            }
        }
        val tinta = t.c(mezcla(LATA_OSC, TINTA, 0.4f))
        renglonFalso(dentro.center.x, dentro.top + dentro.height * 0.1f, dentro.height * 0.13f, dentro.width * 0.036f, listOf(5, -1, 1, 3, 0), tinta)
        renglonFalso(dentro.center.x, dentro.top + dentro.height * 0.35f, dentro.height * 0.32f, dentro.width * 0.08f, listOf(4, 0, 6, -1, 2, 1, 5, 7, 3, 0), tinta)
        renglonFalso(dentro.center.x, dentro.top + dentro.height * 0.79f, dentro.height * 0.1f, dentro.width * 0.03f, listOf(2, 7, -1, 4, 0, 6, 1, -1, 3, 5), tinta)
        pinceladas(r, if (esNoche) NOCHE else TRAVERTINO, 5042, 6, u, 0.2f)
        // Las grapas, a media altura de cada lado, y el óxido que chorrea de la derecha por el muro.
        listOf(r.left, r.right).forEach { x ->
            drawRect(t.s(0.3f), Offset(x - fw(0.006f) + 1.2f * u, r.center.y - fw(0.004f) + 1.5f * u), Size(fw(0.012f), fw(0.009f)))
            drawRect(t.c(mezcla(ZINC_OSC, OXIDO, 0.4f)), Offset(x - fw(0.006f), r.center.y - fw(0.004f)), Size(fw(0.012f), fw(0.009f)))
            drawRect(t.b(0.45f), Offset(x - fw(0.006f), r.center.y - fw(0.004f)), Size(fw(0.012f), 1f * u))
        }
        drawRect(t.c(OXIDO).copy(alpha = 0.4f), Offset(r.right - fw(0.002f), r.center.y + fw(0.005f)), Size(1.6f * u, r.height * 0.8f))
    }

    /**
     * Los restos de carteles pegados unos sobre otros, arrancados por abajo, con sus bloques de color, sus titulares que no
     * dicen nada y la cola que amarillea.
     */
    private fun DrawScope.carteles(w: Float) {
        val u = density
        val capas = listOf(
            Triple(Rect(w * 0.365f, w * 0.12f, w * 0.485f, w * 0.278f), mezcla(AZUL, PAPEL, 0.45f), 5050),
            Triple(Rect(w * 0.535f, w * 0.105f, w * 0.635f, w * 0.255f), mezcla(OCRE, PAPEL, 0.35f), 5052),
            Triple(Rect(w * 0.435f, w * 0.055f, w * 0.585f, w * 0.255f), mezcla(ROSA, PAPEL, 0.3f), 5051),
        )
        capas.forEachIndexed { i, (r, col, s) ->
            val t = tm(r.center)
            // El borde de abajo, arrancado a tirones.
            val p = Path().apply {
                moveTo(r.left, r.top); lineTo(r.right, r.top)
                val n = 8
                for (k in 0..n) {
                    val x = r.right - r.width * k / n
                    val y = r.bottom - r.height * (0.04f + 0.3f * azar(s, k)) * (if (k % 2 == 0) 1f else 0.25f)
                    lineTo(x, y)
                }
                close()
            }
            sombraDetras(p, r.center, 0.16f)
            // La capa de papel blanco que queda pegada al muro donde se arrancó.
            drawRect(t.c(mezcla(PAPEL, ESTUCO_LUZ, 0.35f)).copy(alpha = 0.7f), Offset(r.left + r.width * 0.1f, r.bottom - r.height * 0.12f), Size(r.width * 0.5f, r.height * 0.14f))
            drawPath(p, t.c(col))
            clipPath(p) {
                val tin = t.c(mezcla(col, TINTA, 0.6f))
                renglonFalso(r.center.x, r.top + r.height * 0.08f, r.height * 0.1f, r.width * 0.115f, List(7) { (it * 3 + i * 5) % 8 }, tin)
                drawRect(t.c(if (i == 2) LATA else mezcla(col, TINTA, 0.35f)), Offset(r.left + r.width * 0.1f, r.top + r.height * 0.25f), Size(r.width * 0.5f, r.height * 0.3f))
                if (i == 2) drawCircle(t.c(CREMA), r.width * 0.11f, Offset(r.left + r.width * 0.35f, r.top + r.height * 0.4f))
                else drawPath(poligono(r.left + r.width * 0.1f, r.top + r.height * 0.55f, r.left + r.width * 0.34f, r.top + r.height * 0.32f, r.left + r.width * 0.6f, r.top + r.height * 0.55f), t.c(mezcla(col, CREMA, 0.4f)))
                for (k in 0 until 4) drawRect(t.c(TINTA).copy(alpha = 0.4f), Offset(r.left + r.width * 0.66f, r.top + r.height * (0.27f + 0.07f * k)), Size(r.width * (0.26f - 0.04f * (k % 2)), r.height * 0.028f))
                drawRect(tin, Offset(r.left + r.width * 0.1f, r.top + r.height * 0.62f), Size(r.width * 0.8f, r.height * 0.045f))
                for (k in 0 until 2) drawRect(t.c(TINTA).copy(alpha = 0.35f), Offset(r.left + r.width * 0.1f, r.top + r.height * (0.71f + 0.05f * k)), Size(r.width * (0.7f - 0.2f * k), r.height * 0.02f))
                drawRect(t.b(0.16f), r.topLeft, Size(r.width * 0.3f, r.height))
                drawRect(t.s(0.1f), Offset(r.right - r.width * 0.2f, r.top), Size(r.width * 0.2f, r.height))
                // Las arrugas de la cola.
                drawLine(t.s(0.14f), Offset(r.left + r.width * 0.7f, r.top), Offset(r.left + r.width * 0.62f, r.bottom), 1f * u)
                pinceladas(r, if (esNoche) NOCHE else KRAFT, s, 6, u, 0.16f)
            }
        }
    }

    /**
     * La hornacina de la madonnella: el marco de travertino con su tejadillo de zinc a dos aguas, el nicho de medio punto con
     * el fondo azul y la imagen (una silueta: la aureola, el velo y el manto), y la repisa con la lamparilla de vidrio rojo y
     * un bote con flores.
     */
    private fun DrawScope.hornacina(w: Float) {
        val u = density
        val o = esNoche
        val cx = w * 0.775f
        val e = w * 0.085f
        val r = Rect(cx - e, w * 0.052f, cx + e, w * 0.232f)
        val t = tm(r.center)
        val marco = Path().apply { addRect(Rect(r.left, r.top, r.right, r.bottom + w * 0.014f)) }
        sombraDetras(marco, r.center, 0.3f)
        drawPath(marco, t.c(TRAVERTINO))
        // Los poros y las juntas del travertino, y sus cantos.
        for (k in 0 until 9) {
            val p = Offset(r.left + r.width * azar(5060, k), r.top + r.height * azar(5061, k))
            drawLine(t.s(0.2f), p, p + Offset(w * 0.012f, 0f), 0.8f * u)
        }
        drawRect(t.b(0.5f), r.topLeft, Size(w * 0.006f, r.height))
        drawRect(t.s(0.22f), Offset(r.right - w * 0.008f, r.top), Size(w * 0.008f, r.height))
        listOf(0.36f, 0.7f).forEach { f -> drawLine(t.s(0.22f), Offset(r.left, r.top + r.height * f), Offset(r.right, r.top + r.height * f), 0.8f * u) }
        // El nicho: rehundido, con su sombra arriba y a la izquierda.
        val vano = Rect(cx - e * 0.7f, r.top + w * 0.03f, cx + e * 0.7f, r.bottom)
        val nicho = arcoMedioPunto(vano)
        drawPath(nicho, t.c(mezcla(AZUL, NOCHE, 0.6f)))
        clipPath(nicho) {
            translate(w * 0.009f, w * 0.011f) { drawPath(nicho, t.c(mezcla(AZUL, NOCHE, 0.22f))) }
            // Las estrellas pintadas del fondo.
            listOf(Offset(-0.036f, 0.062f), Offset(0.04f, 0.07f), Offset(-0.03f, 0.105f), Offset(0.036f, 0.115f), Offset(0.012f, 0.05f)).forEach { f ->
                drawCircle(t.c(ORO), 1.1f * u, Offset(cx + w * f.x, r.top + w * f.y))
            }
            if (o) drawCircle(LAMPARILLA.copy(alpha = 0.28f), w * 0.07f, Offset(cx - w * 0.023f, r.bottom))
            // La imagen: la aureola, el velo, la cara y el manto que cae en campana.
            val ic = Offset(cx + w * 0.004f, r.top + w * 0.082f)
            val manto = t.c(mezcla(AZUL, CREMA, 0.32f))
            drawCircle(t.c(ORO), w * 0.024f, ic)
            drawCircle(t.c(mezcla(ORO, CREMA, 0.45f)), w * 0.024f, ic, style = Stroke(1.2f * u))
            val campana = Path().apply {
                moveTo(ic.x, ic.y - w * 0.017f)
                cubicTo(ic.x - w * 0.022f, ic.y - w * 0.016f, ic.x - w * 0.016f, ic.y + w * 0.012f, ic.x - w * 0.026f, ic.y + w * 0.03f)
                cubicTo(ic.x - w * 0.04f, ic.y + w * 0.055f, ic.x - w * 0.04f, ic.y + w * 0.08f, ic.x - w * 0.046f, r.bottom)
                lineTo(ic.x + w * 0.046f, r.bottom)
                cubicTo(ic.x + w * 0.04f, ic.y + w * 0.08f, ic.x + w * 0.04f, ic.y + w * 0.055f, ic.x + w * 0.026f, ic.y + w * 0.03f)
                cubicTo(ic.x + w * 0.016f, ic.y + w * 0.012f, ic.x + w * 0.022f, ic.y - w * 0.016f, ic.x, ic.y - w * 0.017f)
                close()
            }
            drawPath(campana, manto)
            clipPath(campana) {
                // La túnica rosa que asoma por delante y los pliegues del manto.
                drawPath(poligono(ic.x - w * 0.008f, ic.y + w * 0.014f, ic.x + w * 0.008f, ic.y + w * 0.014f, ic.x + w * 0.02f, r.bottom, ic.x - w * 0.02f, r.bottom), t.c(mezcla(LATA, ROSA, 0.45f)))
                drawRect(t.b(0.22f), Offset(ic.x - w * 0.05f, r.top), Size(w * 0.026f, r.height))
                drawRect(t.s(0.25f), Offset(ic.x + w * 0.022f, r.top), Size(w * 0.03f, r.height))
                drawLine(t.s(0.22f), Offset(ic.x - w * 0.02f, ic.y + w * 0.04f), Offset(ic.x - w * 0.03f, r.bottom), 1f * u)
                drawLine(t.s(0.22f), Offset(ic.x + w * 0.014f, ic.y + w * 0.05f), Offset(ic.x + w * 0.018f, r.bottom), 1f * u)
            }
            drawOval(t.c(mezcla(ROSA, ESTUCO_LUZ, 0.5f)), Offset(ic.x - w * 0.009f, ic.y - w * 0.011f), Size(w * 0.018f, w * 0.022f))
            drawOval(t.s(0.16f), Offset(ic.x + w * 0.001f, ic.y - w * 0.011f), Size(w * 0.008f, w * 0.022f))
        }
        // El canto del vano: la arista de luz abajo a la derecha.
        translate(-1f * u, -1f * u) { drawPath(nicho, t.s(0.3f), style = Stroke(1.2f * u)) }
        // El tejadillo de zinc a dos aguas, con su tímpano y su sombra en el marco.
        val ap = Offset(cx, r.top - w * 0.034f)
        val yl = r.top + w * 0.014f
        val g = w * 0.014f
        drawPath(poligono(r.left, yl + g, cx, ap.y + g, r.right, yl + g), t.c(mezcla(TRAVERTINO, ESTUCO_OSC, 0.25f)))
        drawRect(t.s(0.28f), Offset(r.left, yl + g), Size(r.width, w * 0.01f))
        drawPath(poligono(r.left - w * 0.024f, yl, ap.x, ap.y, ap.x, ap.y + g, r.left - w * 0.016f, yl + g), t.c(ZINC))
        drawPath(poligono(ap.x, ap.y, r.right + w * 0.024f, yl, r.right + w * 0.016f, yl + g, ap.x, ap.y + g), t.c(ZINC_OSC))
        drawLine(t.b(0.55f), Offset(r.left - w * 0.024f, yl), ap, 1.1f * u)
        drawLine(t.s(0.35f), Offset(r.left - w * 0.016f, yl + g), Offset(ap.x, ap.y + g), 1f * u)
        drawLine(t.s(0.35f), Offset(ap.x, ap.y + g), Offset(r.right + w * 0.016f, yl + g), 1f * u)
        drawCircle(t.c(ZINC_OSC), w * 0.006f, ap + Offset(0f, w * 0.002f))
        // La repisa, con su canto de luz y su sombra en el muro.
        val rep = Rect(r.left - w * 0.014f, r.bottom, r.right + w * 0.014f, r.bottom + w * 0.016f)
        drawRect(t.s(0.3f), Offset(rep.left + w * 0.01f, rep.bottom), Size(rep.width, w * 0.012f))
        drawRect(t.c(TRAVERTINO), rep.topLeft, rep.size)
        drawRect(t.b(0.55f), rep.topLeft, Size(rep.width, 1.2f * u))
        drawRect(t.s(0.25f), Offset(rep.left, rep.bottom - w * 0.005f), Size(rep.width, w * 0.005f))
        // El bote con flores, a la derecha.
        val fx = cx + w * 0.04f
        listOf(Triple(-0.012f, 0.058f, ROSA), Triple(0.002f, 0.07f, CREMA), Triple(0.014f, 0.052f, mezcla(LATA, ROSA, 0.3f))).forEach { (dx, alto, col) ->
            val fl = Offset(fx + w * dx, rep.top - w * alto)
            drawLine(t.c(HOJA), Offset(fx, rep.top - w * 0.02f), fl, 1.1f * u)
            for (k in 0 until 5) drawCircle(t.c(col), w * 0.005f, fl + Offset(w * 0.006f * kotlin.math.cos(k * 1.26f), w * 0.006f * kotlin.math.sin(k * 1.26f)))
            drawCircle(t.c(ORO), w * 0.003f, fl)
        }
        val bote = Rect(fx - w * 0.01f, rep.top - w * 0.024f, fx + w * 0.01f, rep.top)
        drawRect(t.c(mezcla(ZINC, AZUL, 0.3f)), bote.topLeft, bote.size)
        drawRect(t.b(0.4f), bote.topLeft, Size(bote.width * 0.3f, bote.height))
        drawRect(t.s(0.25f), Offset(bote.right - bote.width * 0.3f, bote.top), Size(bote.width * 0.3f, bote.height))
        // La lamparilla: el vaso de vidrio rojo en su platillo; de noche, encendida.
        val lc = Offset(MADONNA.x * w, rep.top)
        val vaso = poligono(lc.x - w * 0.011f, lc.y - w * 0.026f, lc.x + w * 0.011f, lc.y - w * 0.026f, lc.x + w * 0.008f, lc.y, lc.x - w * 0.008f, lc.y)
        drawOval(t.c(ZINC_OSC), Offset(lc.x - w * 0.014f, lc.y - w * 0.004f), Size(w * 0.028f, w * 0.007f))
        drawPath(vaso, if (o) mezcla(LAMPARILLA, Color(0xFFFFB08A), 0.3f) else mezcla(LATA, ROSA, 0.15f))
        clipPath(vaso) {
            drawRect(if (o) Color(0xFFFFD2B0).copy(alpha = 0.55f) else luzMueble(0.45f), Offset(lc.x - w * 0.009f, lc.y - w * 0.026f), Size(w * 0.005f, w * 0.026f))
            if (!o) drawRect(sombraMueble(0.25f), Offset(lc.x + w * 0.004f, lc.y - w * 0.026f), Size(w * 0.008f, w * 0.026f))
            else drawOval(Color(0xFFFFE0A8), Offset(lc.x - w * 0.004f, lc.y - w * 0.02f), Size(w * 0.008f, w * 0.014f))
        }
        if (o) drawCircle(LAMPARILLA.copy(alpha = 0.4f), w * 0.022f, Offset(lc.x, lc.y - w * 0.014f))
    }

    /**
     * Los cables del muro, con sus grapas: el que viene de la izquierda hasta su caja de empalme y el que baja a la hornacina;
     * y la bajante de zinc de la derecha con sus abrazaderas y su óxido.
     */
    private fun DrawScope.cableYBajante(w: Float, suelo: Float) {
        val u = density
        val negro = Color(0xFF2A2622)
        val cable = Path().apply {
            moveTo(-w * 0.01f, w * 0.178f)
            cubicTo(w * 0.1f, w * 0.215f, w * 0.24f, w * 0.235f, w * 0.325f, w * 0.2f)
        }
        val bajada = Path().apply { moveTo(w * 0.334f, w * 0.215f); lineTo(w * 0.334f, w * 0.31f) }
        val alto = Path().apply {
            moveTo(w * 0.668f, 0f); lineTo(w * 0.668f, w * 0.19f)
            cubicTo(w * 0.668f, w * 0.2f, w * 0.672f, w * 0.204f, w * 0.69f, w * 0.204f)
        }
        listOf(cable, bajada, alto).forEachIndexed { i, p ->
            val t = tm(if (i == 2) Offset(w * 0.668f, w * 0.12f) else Offset(w * 0.2f, w * 0.21f))
            if (!esNoche) translate(w * 0.007f, w * 0.008f) { drawPath(p, sombraMueble(0.2f), style = Stroke(1.4f * u)) }
            drawPath(p, t.c(negro), style = Stroke(1.6f * u))
        }
        // La caja de empalme y las grapas.
        val cj = Rect(w * 0.318f, w * 0.186f, w * 0.35f, w * 0.216f)
        val tc = tm(cj.center)
        drawRect(tc.s(0.3f), Offset(cj.left + w * 0.006f, cj.top + w * 0.006f), cj.size)
        drawRect(tc.c(mezcla(ZINC_OSC, TINTA, 0.25f)), cj.topLeft, cj.size)
        drawRect(tc.b(0.4f), cj.topLeft, Size(cj.width, 1f * u))
        drawRect(tc.b(0.25f), cj.topLeft, Size(1f * u, cj.height))
        listOf(Offset(w * 0.13f, w * 0.215f), Offset(w * 0.668f, w * 0.06f), Offset(w * 0.668f, w * 0.15f)).forEach { drawCircle(tm(it).c(ZINC), 1.5f * u, it) }
        // La bajante.
        val x0 = w * 0.957f; val x1 = w * 0.985f
        if (!esNoche) drawRect(sombraMueble(0.2f), Offset(x1, 0f), Size(w * 0.012f, suelo))
        var y = 0f; var k = 0
        val tramo = w * 0.21f
        while (y < suelo) {
            val tt = tm(Offset(x0, y + tramo / 2))
            val y1 = minOf(y + tramo, suelo)
            drawRect(tt.c(mezcla(ZINC, ESTUCO_OSC, 0.25f)), Offset(x0, y), Size(x1 - x0, y1 - y + 0.5f))
            clipRect(x0, y, x1, y1 + 0.5f) { cilindro(Rect(x0, y, x1, y1 + 0.5f), tt, 1.2f) }
            if (k % 2 == 0) {
                // La abrazadera, con su tornillo y la chorrera de óxido.
                val ya = y + tramo * 0.3f
                drawRect(tt.c(ZINC_OSC), Offset(x0 - w * 0.004f, ya), Size(x1 - x0 + w * 0.008f, w * 0.009f))
                drawRect(tt.b(0.45f), Offset(x0 - w * 0.004f, ya), Size(x1 - x0 + w * 0.008f, 0.9f * u))
                drawRect(tt.s(0.3f), Offset(x0 - w * 0.004f, ya + w * 0.009f), Size(x1 - x0 + w * 0.008f, 1.2f * u))
                drawRect(tt.c(OXIDO).copy(alpha = 0.5f), Offset(x0 + (x1 - x0) * 0.6f, ya + w * 0.009f), Size(1.3f * u, w * 0.06f))
            }
            y += tramo; k++
        }
    }

    /**
     * La franja de sampietrini que pisa el quiosco: hiladas de adoquines que se agrandan hacia quien mira, cada uno con su canto
     * de luz y su sombra, las juntas de arena y alguna piedra hundida.
     */
    private fun DrawScope.sampietrini(w: Float, suelo: Float, alto: Float) {
        val u = density
        val t0 = tn(Offset(w * 0.5f, (suelo + alto) / 2))
        drawRect(t0.c(mezcla(ADOQUIN, KRAFT, 0.35f)), Offset(0f, suelo), Size(w, alto - suelo))
        var y = suelo; var fila = 0
        while (y < alto) {
            val hf = w * (0.016f + 0.006f * fila)
            var x = -hf * azar(5100, fila)
            var k = 0
            while (x < w) {
                val aw = hf * (0.95f + 0.3f * azar(5101 + fila, k))
                val t = tn(Offset(x + aw / 2, y + hf / 2))
                val tono = mezcla(ADOQUIN, if (azar(5102 + fila, k) > 0.6f) KRAFT else ZINC_OSC, 0.35f * azar(5103 + fila, k))
                val r = Rect(x + 0.9f * u, y + 0.9f * u, x + aw - 0.9f * u, y + hf - 0.6f * u)
                drawRoundRect(t.c(tono), r.topLeft, r.size, CornerRadius(hf * 0.25f))
                drawRect(t.b(0.3f), Offset(r.left + hf * 0.1f, r.top), Size(r.width * 0.75f, 1f * u))
                drawRect(t.s(0.25f), Offset(r.left + hf * 0.1f, r.bottom - 1.2f * u), Size(r.width * 0.85f, 1.2f * u))
                x += aw; k++
            }
            y += hf; fila++
        }
        drawRect(t0.s(0.3f), Offset(0f, suelo), Size(w, 2.5f * u))
    }

    // ---------------------------------------------------------------- el quiosco

    /**
     * El cuerpo del quiosco: su sombra en el muro y en el suelo, el frente de hierro verde (de noche más encendido arriba,
     * cerca de la bombilla), el friso de rosetas bajo el mostrador, los marcos remachados de los huecos, el canto de cada
     * repisa, los pilares acanalados, el zócalo con sus patas y los desconchones de la pintura con su óxido.
     */
    private fun DrawScope.quiosco(d: Disposicion, b: Rect) {
        val u = density
        val o = esNoche
        val w = anchoW
        val hb = b.height
        val xL = w * QX0; val xR = w * QX1
        val pisa = b.top + hb * PISA
        val suelo = b.top + hb * SUELO
        val cuerpo = Rect(xL, w * CANTO, xR, b.top + hb * ZOCALO)

        // Su sombra en el muro (a la derecha) y en el suelo.
        val t0 = tn(Offset(xR, w * 1.4f))
        drawPath(poligono(xR, w * ALERO, xR + w * 0.03f, w * (ALERO + 0.03f), xR + w * 0.03f, suelo, xR, suelo), t0.s(0.3f))
        val tp = tn(Offset(w * 0.5f, pisa))
        drawRect(tp.s(0.34f), Offset(xL + w * 0.02f, pisa - 2f * u), Size(xR - xL + w * 0.02f, hb * 0.06f))
        drawRect(tp.s(0.16f), Offset(xL + w * 0.035f, pisa + hb * 0.06f - 2f * u), Size(xR - xL, hb * 0.07f))

        // El frente de hierro verde.
        if (!o) drawRect(VERDE, cuerpo.topLeft, cuerpo.size)
        else drawRect(Brush.verticalGradient(List(6) { i -> tn(Offset(w * 0.5f, cuerpo.top + cuerpo.height * i / 5f)).c(VERDE) }, cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.14f), Color.Transparent, sombraMueble(0.22f)), xL, xR), cuerpo.topLeft, cuerpo.size)
        pinceladas(cuerpo, if (o) NOCHE else VERDE_LUZ, 5201, 30, u, 0.12f)
        pinceladas(cuerpo, if (o) NOCHE else VERDE_OSC, 5202, 24, u, 0.12f)

        // El friso bajo el mostrador: tres tableros rehundidos, cada uno con su rombo de fundición.
        val friso = Rect(xL + w * PILAR, w * (CANTO + 0.012f), xR - w * PILAR, d.huecos.first().top - w * 0.02f)
        for (k in 0 until 3) {
            val pr = Rect(friso.left + friso.width * k / 3f + w * 0.008f, friso.top + w * 0.008f, friso.left + friso.width * (k + 1) / 3f - w * 0.008f, friso.bottom - w * 0.004f)
            val tt = tn(pr.center)
            drawRect(tt.c(mezcla(VERDE, VERDE_OSC, 0.5f)), pr.topLeft, pr.size)
            drawRect(tt.s(0.35f), pr.topLeft, Size(pr.width, 2f * u))
            drawRect(tt.s(0.25f), pr.topLeft, Size(1.6f * u, pr.height))
            drawRect(tt.b(0.4f), Offset(pr.left, pr.bottom - 1f * u), Size(pr.width, 1f * u))
            drawRect(tt.b(0.25f), Offset(pr.right - 1f * u, pr.top), Size(1f * u, pr.height))
            val c = pr.center; val rx = pr.height * 1.1f; val ry = pr.height * 0.3f
            translate(1.4f * u, 1.8f * u) { drawPath(poligono(c.x - rx, c.y, c.x, c.y - ry, c.x + rx, c.y, c.x, c.y + ry), tt.s(0.35f)) }
            drawPath(poligono(c.x - rx, c.y, c.x, c.y - ry, c.x + rx, c.y, c.x, c.y + ry), tt.c(mezcla(VERDE, VERDE_LUZ, 0.6f)))
            drawPath(poligono(c.x - rx, c.y, c.x, c.y - ry, c.x, c.y), tt.b(0.3f))
            drawPath(poligono(c.x, c.y, c.x + rx, c.y, c.x, c.y + ry), tt.s(0.25f))
            remache(c, 1.6f * u, tt)
        }

        // Los marcos de los huecos, remachados.
        val m = w * 0.014f
        d.huecos.forEach { r ->
            val t = tn(r.center)
            val marco = Rect(r.left - m, r.top - m, r.right + m, r.bottom + m)
            drawRect(t.s(0.3f), Offset(marco.left + 2.2f * u, marco.top + 2.6f * u), marco.size)
            drawRect(t.c(mezcla(VERDE, VERDE_LUZ, 0.45f)), marco.topLeft, marco.size)
            drawRect(t.b(0.5f), marco.topLeft, Size(marco.width, 1.2f * u))
            drawRect(t.b(0.3f), marco.topLeft, Size(1.2f * u, marco.height))
            drawRect(t.s(0.38f), Offset(marco.left, marco.bottom - 1.4f * u), Size(marco.width, 1.4f * u))
            drawRect(t.s(0.3f), Offset(marco.right - 1.4f * u, marco.top), Size(1.4f * u, marco.height))
            listOf(marco.topLeft, Offset(marco.right, marco.top), Offset(marco.left, marco.bottom), Offset(marco.right, marco.bottom)).forEach { q ->
                remache(Offset(q.x + (if (q.x < r.center.x) m * 0.5f else -m * 0.5f), q.y + (if (q.y < r.center.y) m * 0.5f else -m * 0.5f)), 1.3f * u, t)
            }
        }
        // El canto de cada repisa, de lado a lado.
        d.filas.forEachIndexed { i, f ->
            val y = f.bottom + d.separacion / 2
            if (i < d.filas.size - 1) {
                val t = tn(Offset(w * 0.5f, y))
                drawRect(t.s(0.3f), Offset(xL, y - 2f * u), Size(xR - xL, 1f * u))
                drawRect(t.c(VERDE_LUZ), Offset(xL, y - 1f * u), Size(xR - xL, 2f * u))
                drawRect(t.b(0.5f), Offset(xL, y - 1f * u), Size(xR - xL, 0.7f * u))
                drawRect(t.s(0.3f), Offset(xL, y + 1f * u), Size(xR - xL, 1.2f * u))
            }
        }

        // Los pilares acanalados de los costados (el derecho en sombra), con su capitel y su basa.
        listOf(false, true).forEach { derecha ->
            val x0 = if (derecha) xR - w * PILAR else xL
            val col = Rect(x0, w * ALERO, x0 + w * PILAR, cuerpo.bottom)
            for (k in 0 until 12) {
                val y0 = col.top + col.height * k / 12f
                val t = tn(Offset(col.center.x, y0 + col.height / 24f))
                drawRect(t.c(if (derecha) mezcla(VERDE, VERDE_OSC, 0.4f) else VERDE_LUZ), Offset(col.left, y0), Size(col.width, col.height / 12f + 0.5f))
                listOf(0.25f, 0.5f, 0.75f).forEach { fx ->
                    drawRect(t.s(0.3f), Offset(col.left + col.width * fx, y0), Size(1.1f * u, col.height / 12f + 0.5f))
                    drawRect(t.b(0.35f), Offset(col.left + col.width * fx - 0.9f * u, y0), Size(0.9f * u, col.height / 12f + 0.5f))
                }
                if (derecha) drawRect(t.b(0.5f), Offset(col.left, y0), Size(1f * u, col.height / 12f + 0.5f))
                else drawRect(t.s(0.25f), Offset(col.right - 1.4f * u, y0), Size(1.4f * u, col.height / 12f + 0.5f))
            }
            listOf(col.bottom - w * 0.025f to w * 0.025f).forEach { (y, h) ->
                val t = tn(Offset(col.center.x, y))
                drawRect(t.c(VERDE_LUZ), Offset(col.left - w * 0.006f, y), Size(col.width + w * 0.012f, h))
                drawRect(t.b(0.45f), Offset(col.left - w * 0.006f, y), Size(col.width + w * 0.012f, 1f * u))
                drawRect(t.s(0.3f), Offset(col.left - w * 0.006f, y + h * 0.45f), Size(col.width + w * 0.012f, 1f * u))
            }
        }

        // El zócalo del quiosco: el tablero rehundido y las patas de fundición.
        val tz = tn(Offset(w * 0.5f, b.top))
        val zt = d.filas.last().bottom + d.separacion * 0.6f
        val zoc = Rect(xL + w * PILAR, zt, xR - w * PILAR, cuerpo.bottom - w * 0.008f)
        drawRect(tz.s(0.3f), zoc.topLeft, Size(zoc.width, 1.4f * u))
        drawRect(tz.c(mezcla(VERDE, VERDE_OSC, 0.3f)), Offset(zoc.left + w * 0.012f, zoc.top + w * 0.008f), Size(zoc.width - w * 0.024f, zoc.height - w * 0.016f))
        drawRect(tz.b(0.35f), Offset(zoc.left + w * 0.012f, zoc.bottom - w * 0.008f), Size(zoc.width - w * 0.024f, 1f * u))
        listOf(xL, xR - w * 0.07f, w * 0.465f).forEach { x ->
            val t = tn(Offset(x, pisa))
            val pata = poligono(x, cuerpo.bottom, x + w * 0.07f, cuerpo.bottom, x + w * 0.06f, pisa, x + w * 0.01f, pisa)
            drawPath(pata, t.c(VERDE_OSC))
            drawRect(t.b(0.3f), Offset(x + w * 0.005f, cuerpo.bottom), Size(w * 0.06f, 1f * u))
        }
        drawRect(tz.c(mezcla(VERDE_OSC, OXIDO, 0.25f)), Offset(xL, cuerpo.bottom - w * 0.008f), Size(xR - xL, w * 0.008f))

        // Los desconchones de la pintura: el óxido que asoma y la chorrera bajo un remache.
        listOf(Offset(0.075f, 1.22f), Offset(0.925f, 1.5f), Offset(0.5f, 1.185f), Offset(0.075f, 2.25f), Offset(0.925f, 2.42f), Offset(0.3f, b.top / w + 0.06f)).forEachIndexed { i, f ->
            val c = f * w
            val t = tn(c)
            val p = roto(c, w * (0.008f + 0.008f * azar(5210, i)), w * (0.006f + 0.006f * azar(5211, i)), 5212 + i, 8)
            drawPath(p, t.c(mezcla(OXIDO, VERDE_LUZ, 0.2f)))
            drawRect(t.c(OXIDO).copy(alpha = 0.4f), Offset(c.x, c.y), Size(1.1f * u, w * 0.03f))
        }
    }

    /**
     * La parte alta del quiosco, bajo el toldo: el fondo por dentro, en sombra, con dos repisas de revistas puestas de frente y
     * encimadas (sus portadas apagadas por la penumbra), la sombra del letrero y del lambrequín arriba y el costado derecho más
     * hondo.
     */
    private fun DrawScope.parteAlta(w: Float) {
        val u = density
        val r = Rect(w * (QX0 + PILAR), w * LAMBRE1 - w * 0.04f, w * (QX1 - PILAR), w * SUP)
        val t = tn(r.center)
        drawRect(t.c(mezcla(VERDE_OSC, NOCHE, 0.3f)), r.topLeft, r.size)
        val colores = listOf(LATA, AZUL, OCRE, PAPEL, VERDE_LUZ, ROSA, ESTUCO)
        clipRect(r.left, r.top, r.right, r.bottom) {
            listOf(0.56f, 0.97f).forEachIndexed { j, fy ->
                val y = r.top + r.height * fy
                val mh = r.height * 0.36f
                val paso = w * 0.05f
                val mw = w * 0.066f
                var x = r.left + w * 0.004f - paso * 0.4f * j; var k = 0
                while (x < r.right) {
                    val col = colores[(k * 3 + j * 2) % 7]
                    val tt = tn(Offset(x + paso / 2, y - mh / 2))
                    val apagado = mezcla(col, VERDE_OSC, 0.48f)
                    val alto = mh * (0.88f + 0.12f * azar(5300 + j, k))
                    drawRect(tt.s(0.4f), Offset(x - 1.6f * u, y - alto + 2f * u), Size(1.6f * u, alto - 2f * u))
                    drawRect(tt.c(apagado), Offset(x, y - alto), Size(mw, alto))
                    drawRect(tt.c(mezcla(CREMA, VERDE_OSC, 0.42f)), Offset(x + mw * 0.1f, y - alto * 0.92f), Size(mw * 0.55f, alto * 0.13f))
                    if (k % 2 == 0) drawCircle(tt.c(mezcla(apagado, CREMA, 0.28f)), mw * 0.2f, Offset(x + mw * 0.36f, y - alto * 0.42f))
                    else drawRect(tt.c(mezcla(apagado, TINTA, 0.3f)), Offset(x + mw * 0.1f, y - alto * 0.66f), Size(mw * 0.5f, alto * 0.4f))
                    drawRect(tt.b(0.12f), Offset(x, y - alto), Size(mw * 0.16f, alto))
                    x += paso; k++
                }
                // El listón que las sujeta y la repisa.
                val tt = tn(Offset(w * 0.5f, y))
                drawRect(tt.c(VERDE), Offset(r.left, y - mh * 0.2f), Size(r.width, w * 0.006f))
                drawRect(tt.b(0.35f), Offset(r.left, y - mh * 0.2f), Size(r.width, 0.8f * u))
                drawRect(tt.s(0.3f), Offset(r.left, y - mh * 0.2f + w * 0.006f), Size(r.width, 1.4f * u))
            }
        }
        // La sombra del letrero y del toldo, arriba, y el costado derecho hondo.
        listOf(0.42f to 0.14f, 0.28f to 0.18f, 0.16f to 0.26f).forEach { (a, al) -> drawRect(t.s(al), r.topLeft, Size(r.width, r.height * a)) }
        drawRect(t.s(0.25f), Offset(r.right - w * 0.04f, r.top), Size(w * 0.04f, r.height))
    }

    /**
     * El costado izquierdo abatido hacia quien mira: la hoja de hierro en escorzo, con su marco y dos revistas sujetas con
     * pinzas, cortada por el borde del cuadro.
     */
    private fun DrawScope.costado(w: Float) {
        val u = density
        val x0 = -w * 0.012f; val x1 = w * QX0 + w * 0.004f
        val y0 = w * (LAMBRE1 + 0.006f); val y1 = w * (CANTO - 0.008f)
        val hoja = poligono(x0, y0 - w * 0.022f, x1, y0, x1, y1, x0, y1 + w * 0.022f)
        val t = tn(Offset(w * 0.03f, (y0 + y1) / 2))
        if (!esNoche) drawPath(poligono(x1, y0, x1 + w * 0.022f, y0 + w * 0.012f, x1 + w * 0.022f, y1, x1, y1), sombraMueble(0.28f))
        drawPath(hoja, t.c(VERDE_LUZ))
        clipPath(hoja) {
            drawRect(t.b(0.2f), Offset(x0, y0 - w * 0.022f), Size((x1 - x0) * 0.4f, y1 - y0 + w * 0.044f))
            drawRect(t.s(0.3f), Offset(x1 - w * 0.006f, y0 - w * 0.022f), Size(w * 0.006f, y1 - y0 + w * 0.044f))
            // Dos revistas sujetas.
            listOf(Triple(y0 + w * 0.022f, AZUL, 5310), Triple(y0 + w * 0.16f, LATA, 5311)).forEach { (yy, col, _) ->
                val rr = Rect(x0 + w * 0.006f, yy, x1 - w * 0.01f, yy + w * 0.115f)
                drawRect(t.s(0.25f), Offset(rr.left + 1.5f * u, rr.top + 2f * u), rr.size)
                drawRect(t.c(col), rr.topLeft, rr.size)
                drawRect(t.c(CREMA), Offset(rr.left + rr.width * 0.1f, rr.top + rr.height * 0.07f), Size(rr.width * 0.8f, rr.height * 0.12f))
                drawRect(t.c(mezcla(col, CREMA, 0.4f)), Offset(rr.left + rr.width * 0.15f, rr.top + rr.height * 0.3f), Size(rr.width * 0.7f, rr.height * 0.4f))
                drawCircle(t.c(mezcla(col, TINTA, 0.3f)), rr.width * 0.2f, Offset(rr.left + rr.width * 0.55f, rr.top + rr.height * 0.5f))
                drawRect(t.c(KRAFT), Offset(rr.center.x - w * 0.004f, rr.top - w * 0.01f), Size(w * 0.008f, w * 0.022f))
            }
        }
        drawPath(hoja, t.c(VERDE_OSC), style = Stroke(1.8f * u))
        listOf(y0 + w * 0.03f, y1 - w * 0.03f).forEach { y -> drawRect(t.c(VERDE_OSC), Offset(x1 - w * 0.003f, y), Size(w * 0.008f, w * 0.018f)) }
    }

    /** El mostrador: la tabla de madera vista un poco desde arriba, con sus rozaduras y un cerco de taza, y su canto de hierro. */
    private fun DrawScope.mostrador(w: Float) {
        val u = density
        val o = esNoche
        val xL = w * 0.03f; val xR = w * 0.97f
        val sup = w * SUP; val frente = w * FRENTE; val canto = w * CANTO
        val tabla = Rect(xL, sup, xR, frente)
        val madera = mezcla(KRAFT, OXIDO, 0.3f)
        if (!o) drawRect(madera, tabla.topLeft, tabla.size)
        else drawRect(Brush.horizontalGradient(List(6) { i -> tn(Offset(xL + (xR - xL) * i / 5f, sup)).c(madera) }, xL, xR), tabla.topLeft, tabla.size)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.3f), Color.Transparent, sombraMueble(0.12f)), xL, xR), tabla.topLeft, tabla.size)
        vetas(tabla, (if (o) NOCHE else TINTA).copy(alpha = 0.22f), 5401, 0.8f, grosor = 0.5f, ondulacion = 0.4f)
        drawRect(tn(Offset(w * 0.5f, sup)).s(0.3f), tabla.topLeft, Size(tabla.width, 1.4f * u))
        drawOval(tn(Offset(w * 0.3f, sup)).s(0.22f), Offset(w * 0.3f, sup + (frente - sup) * 0.3f), Size(w * 0.04f, (frente - sup) * 0.5f), style = Stroke(0.9f * u))
        for (k in 0 until 6) {
            val x = w * (0.05f + 0.88f * azar(5402, k))
            drawLine(tn(Offset(x, sup)).b(0.3f), Offset(x, sup + (frente - sup) * (0.3f + 0.4f * azar(5403, k))), Offset(x + w * 0.03f, sup + (frente - sup) * (0.4f + 0.3f * azar(5404, k))), 0.6f * u)
        }
        // El canto de hierro, con el filete de luz y los remaches.
        for (k in 0 until 8) {
            val x0 = xL + (xR - xL) * k / 8f
            val tk = tn(Offset(x0 + (xR - xL) / 16f, frente))
            drawRect(tk.c(VERDE_LUZ), Offset(x0, frente), Size((xR - xL) / 8f + 0.5f, canto - frente))
            drawRect(tk.b(0.6f), Offset(x0, frente), Size((xR - xL) / 8f + 0.5f, 1.2f * u))
            drawRect(tk.s(0.3f), Offset(x0, canto - 2f * u), Size((xR - xL) / 8f + 0.5f, 2f * u))
            remache(Offset(x0 + (xR - xL) / 16f, (frente + canto) / 2), 1.2f * u, tk)
        }
        // La sombra del mostrador en el friso.
        val t = tn(Offset(w * 0.5f, canto))
        drawRect(t.s(0.35f), Offset(w * QX0, canto), Size(w * (QX1 - QX0), 2.4f * u))
        drawRect(t.s(0.15f), Offset(w * QX0, canto + 2.4f * u), Size(w * (QX1 - QX0), 4f * u))
    }

    /**
     * El techo de zinc del quiosco, visto de frente y un poco desde abajo: los faldones con sus juntas alzadas y su pátina, la
     * cumbrera con su remate, el canalón del alero, el letrero crema con su marco de hierro y sus letras pintadas (no dicen
     * nada) y el lambrequín festoneado y calado; su sombra en el muro, de día.
     */
    private fun DrawScope.techo(w: Float) {
        val u = density
        val o = esNoche
        val y0 = w * TECHO0; val y1 = w * ALERO
        val faldon = poligono(w * 0.13f, y0, w * 0.87f, y0, w * 0.995f, y1, w * 0.005f, y1)
        if (!o) translate(w * 0.022f, w * 0.014f) { drawPath(faldon, sombraMueble(0.26f)) }
        val t = tn(Offset(w * 0.5f, y0))
        clipPath(faldon) {
            // Por tramos: de noche el rojo de la lamparilla solo llega a los de debajo de la hornacina.
            for (k in 0 until 10) drawRect(tn(Offset(w * (k + 0.5f) / 10f, y0 + w * 0.01f)).c(ZINC), Offset(w * k / 10f, y0), Size(w / 10f + 0.5f, y1 - y0))
            if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.34f), Color.Transparent, sombraMueble(0.24f)), 0f, w), Offset(0f, y0), Size(w, y1 - y0))
            drawRect(t.s(0.14f), Offset(0f, y1 - (y1 - y0) * 0.3f), Size(w, (y1 - y0) * 0.3f))
            for (k in 0..18) {
                val xb = w * (0.005f + 0.99f * k / 18f)
                val xt = w * (0.13f + 0.74f * k / 18f)
                val tk = tn(Offset(xt, y0 + w * 0.01f))
                drawLine(tk.s(0.35f), Offset(xt + 1.3f * u, y0), Offset(xb + 1.3f * u, y1), 1.3f * u)
                drawLine(tk.b(0.5f), Offset(xt, y0), Offset(xb, y1), 1f * u)
            }
            pinceladas(Rect(0f, y0, w, y1), if (o) NOCHE else ZINC_OSC, 5501, 18, u, 0.14f)
            pinceladas(Rect(0f, y0, w, y1), if (o) NOCHE else CREMA, 5505, 8, u, 0.12f)
            drawRect(t.c(OXIDO).copy(alpha = 0.25f), Offset(w * 0.7f, y1 - w * 0.025f), Size(w * 0.05f, w * 0.025f))
            drawRect(t.c(OXIDO).copy(alpha = 0.18f), Offset(w * 0.22f, y1 - w * 0.018f), Size(w * 0.03f, w * 0.018f))
        }
        // La cumbrera y el remate.
        drawRect(t.c(ZINC_OSC), Offset(w * 0.12f, y0 - w * 0.008f), Size(w * 0.76f, w * 0.011f))
        drawRect(t.b(0.5f), Offset(w * 0.12f, y0 - w * 0.008f), Size(w * 0.76f, 1f * u))
        drawRect(t.s(0.3f), Offset(w * 0.12f, y0 + w * 0.003f), Size(w * 0.76f, 1.4f * u))
        drawRect(t.c(VERDE), Offset(w * 0.494f, y0 - w * 0.03f), Size(w * 0.012f, w * 0.025f))
        drawCircle(t.c(VERDE), w * 0.014f, Offset(w * 0.5f, y0 - w * 0.038f))
        drawCircle(t.s(0.25f), w * 0.009f, Offset(w * 0.504f, y0 - w * 0.034f))
        drawCircle(t.b(0.5f), w * 0.005f, Offset(w * 0.495f, y0 - w * 0.043f))

        // El letrero.
        val le = Rect(w * 0.015f, y1, w * 0.985f, w * LETRERO1)
        val tl = tn(le.center)
        drawRect(tl.c(VERDE), le.topLeft, le.size)
        val pl = Rect(le.left + w * 0.05f, le.top + w * 0.014f, le.right - w * 0.05f, le.bottom - w * 0.012f)
        drawRect(tl.s(0.3f), Offset(pl.left, pl.top), Size(pl.width, 1.5f * u))
        drawRect(tl.c(CREMA), Offset(pl.left, pl.top + 1.5f * u), Size(pl.width, pl.height - 1.5f * u))
        // Las letras pintadas: dos palabras que no dicen nada, gastadas.
        renglonFalso(pl.center.x, pl.top + pl.height * 0.25f, pl.height * 0.52f, pl.width * 0.05f,
            listOf(0, 3, 5, 1, 7, 2, 4, -1, -1, 6, 1, 3, 0, 5, 4), tl.c(VERDE_OSC))
        pinceladas(pl, tl.c(CREMA), 5503, 10, u, 0.5f)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.25f), Color.Transparent, sombraMueble(0.15f)), pl.left, pl.right), pl.topLeft, pl.size)
        pinceladas(pl, if (o) NOCHE else KRAFT, 5502, 8, u, 0.14f)
        listOf(le.left + w * 0.025f, le.right - w * 0.025f).forEach { cx -> remache(Offset(cx, le.center.y), w * 0.008f, tl) }
        // El canalón del alero, con su canto de luz y su sombra sobre el letrero.
        drawRect(t.c(ZINC_OSC), Offset(0f, y1 - w * 0.007f), Size(w, w * 0.013f))
        drawRect(t.b(0.55f), Offset(0f, y1 - w * 0.007f), Size(w, 1.2f * u))
        drawRect(tl.s(0.35f), Offset(le.left, y1 + w * 0.006f), Size(le.width, w * 0.008f))

        // El lambrequín festoneado, en chapa verde con su filete crema y sus calados.
        val ya = w * LETRERO1; val yb = w * LAMBRE1
        val nf = 13
        val recto = ya + (yb - ya) * 0.42f
        val lam = Path().apply {
            moveTo(le.left, ya)
            lineTo(le.right, ya)
            lineTo(le.right, recto)
            for (j in nf - 1 downTo 0) {
                val xa = le.left + le.width * (j + 1) / nf; val xb = le.left + le.width * j / nf
                cubicTo(xa, yb + (yb - ya) * 0.18f, xb, yb + (yb - ya) * 0.18f, xb, recto)
            }
            close()
        }
        if (!o) translate(w * 0.008f, w * 0.016f) { drawPath(lam, sombraMueble(0.34f)) }
        else translate(0f, w * 0.01f) { drawPath(lam, NOCHE.copy(alpha = 0.35f)) }
        drawPath(lam, tl.c(VERDE_LUZ))
        clipPath(lam) {
            drawRect(tl.c(CREMA), Offset(le.left, ya + (yb - ya) * 0.12f), Size(le.width, (yb - ya) * 0.1f))
            drawRect(tl.b(0.4f), Offset(le.left, ya), Size(le.width, 1.2f * u))
            if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.2f), Color.Transparent, sombraMueble(0.2f)), le.left, le.right), Offset(le.left, ya), Size(le.width, yb - ya + w * 0.01f))
            drawRect(tl.s(0.22f), Offset(le.left, yb - (yb - ya) * 0.22f), Size(le.width, (yb - ya) * 0.5f))
            if (o) drawRect(Brush.radialGradient(listOf(BOMBILLA.copy(alpha = 0.55f), Color.Transparent), Offset(w * LUZ.x, yb + w * 0.01f), w * 0.3f), Offset(le.left, ya), Size(le.width, yb - ya + w * 0.02f))
        }
        for (j in 0 until nf) {
            val c = Offset(le.left + le.width * (j + 0.5f) / nf, ya + (yb - ya) * 0.62f)
            drawCircle(tl.c(VERDE_OSC), w * 0.0065f, c)
            drawCircle(tl.b(0.3f), w * 0.0065f, c + Offset(0.8f * u, 0.8f * u), style = Stroke(0.8f * u))
            listOf(-1f, 1f).forEach { s -> drawCircle(tl.c(VERDE_OSC), w * 0.0025f, c + Offset(s * w * 0.016f, -w * 0.007f)) }
        }
    }

    /** La persiana metálica a medio bajar a la derecha (de noche): sus lamas con luz y sombra, la barra y el tirador. */
    private fun DrawScope.persiana(w: Float) {
        val u = density
        val r = Rect(w * 0.645f, w * LAMBRE1 - w * 0.012f, w * (QX1 - PILAR), w * 0.652f)
        var y = r.top; var k = 0
        val lama = w * 0.011f
        while (y < r.bottom) {
            val t = tn(Offset(r.left + w * 0.05f, y))
            drawRect(t.c(mezcla(ZINC, VERDE, 0.35f)), Offset(r.left, y), Size(r.width, lama))
            drawRect(t.b(0.5f), Offset(r.left, y), Size(r.width, 1f * u))
            drawRect(t.s(0.35f), Offset(r.left, y + lama - 1.5f * u), Size(r.width, 1.5f * u))
            y += lama; k++
        }
        val t = tn(Offset(r.left, r.bottom))
        drawRect(t.s(0.4f), Offset(r.left, r.bottom), Size(r.width, w * 0.01f))
        drawRect(t.c(ZINC_OSC), Offset(r.left, r.bottom - w * 0.012f), Size(r.width, w * 0.012f))
        drawRect(t.b(0.5f), Offset(r.left, r.bottom - w * 0.012f), Size(r.width, 1f * u))
        drawRoundRect(t.c(ZINC), Offset(r.left + r.width * 0.45f, r.bottom - w * 0.004f), Size(w * 0.03f, w * 0.008f), CornerRadius(w * 0.004f))
        drawRect(t.s(0.3f), Offset(r.left, r.top), Size(w * 0.006f, r.height))
        // La sombra de la persiana se pierde hacia la derecha, lejos de la bombilla.
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, NOCHE.copy(alpha = 0.45f)), r.left + r.width * 0.3f, r.right), r.topLeft, r.size)
    }

    // ---------------------------------------------------------------- lo colgado

    /**
     * El punto focal: el alambre bajo el toldo con cinco diarios y revistas colgados con pinzas, encimados y un poco ladeados,
     * con su sombra en el fondo del quiosco.
     */
    private fun DrawScope.diarios(w: Float) {
        val u = density
        val a0 = Offset(w * 0.098f, w * 0.513f); val a1 = Offset(w * 0.548f, w * 0.519f)
        val t = tn(Offset(w * 0.3f, w * 0.52f))
        fun yAlambre(x: Float): Float { val f = (x - a0.x) / (a1.x - a0.x); return a0.y + (a1.y - a0.y) * f + w * 0.016f * 4f * f * (1f - f) }
        val al = Path().apply { moveTo(a0.x, a0.y); var x = a0.x; while (x <= a1.x) { lineTo(x, yAlambre(x)); x += w * 0.02f } }
        drawPath(al, t.c(ZINC_OSC), style = Stroke(1f * u))
        drawCircle(t.c(ZINC_OSC), 1.8f * u, a0); drawCircle(t.c(ZINC_OSC), 1.8f * u, a1)
        // (centro y ancho, alto y clase: 0 diario y 1 revista, color de portada, giro y semilla)
        listOf(
            Triple(Offset(0.158f, 0.104f), Offset(0.195f, 1f), Triple(LATA, -2.5f, 5601)),
            Triple(Offset(0.335f, 0.104f), Offset(0.175f, 1f), Triple(AZUL, -1.5f, 5603)),
            Triple(Offset(0.245f, 0.12f), Offset(0.222f, 0f), Triple(PAPEL, 1.5f, 5602)),
            Triple(Offset(0.5f, 0.09f), Offset(0.155f, 1f), Triple(OCRE, -1f, 5605)),
            Triple(Offset(0.42f, 0.115f), Offset(0.206f, 0f), Triple(PAPEL, 2.5f, 5604)),
        ).forEach { (cw, hc, rest) ->
            val cx = w * cw.x
            colgado(cx, yAlambre(cx), w * cw.y, w * hc.x, hc.y.toInt(), rest.first, rest.second, rest.third)
        }
    }

    /** Un diario o una revista colgados: la hoja con su contenido de bloques, la esquina que se levanta y la pinza. */
    private fun DrawScope.colgado(cx: Float, top: Float, pw: Float, ph: Float, clase: Int, color: Color, giro: Float, s: Int) {
        val u = density
        val w = anchoW
        val t = tn(Offset(cx, top + ph * 0.35f))
        val der = luzDerecha(cx)
        rotate(giro, Offset(cx, top)) {
            val r = Rect(cx - pw / 2, top + w * 0.004f, cx + pw / 2, top + ph)
            val esq = pw * 0.2f
            val hoja = poligono(r.left, r.top, r.right, r.top, r.right, r.bottom - esq, r.right - esq, r.bottom, r.left, r.bottom)
            sombraDetras(hoja, r.center, 0.3f)
            drawPath(hoja, t.c(color))
            clipPath(hoja) {
                val tin = t.c(TINTA)
                if (clase == 0) {
                    // El diario: la cabecera, el titular, la foto y las columnas.
                    var x = r.left + pw * 0.08f; var q = 0
                    while (x < r.right - pw * 0.1f) {
                        val lw = pw * (0.05f + 0.04f * azar(s, q))
                        drawRect(tin, Offset(x, r.top + ph * 0.04f), Size(lw, ph * 0.075f))
                        x += lw + pw * 0.018f; q++
                    }
                    drawRect(tin.copy(alpha = 0.7f), Offset(r.left + pw * 0.06f, r.top + ph * 0.13f), Size(pw * 0.88f, 0.8f * u))
                    drawRect(tin.copy(alpha = 0.85f), Offset(r.left + pw * 0.06f, r.top + ph * 0.17f), Size(pw * 0.86f, ph * 0.045f))
                    drawRect(tin.copy(alpha = 0.85f), Offset(r.left + pw * 0.06f, r.top + ph * 0.235f), Size(pw * 0.6f, ph * 0.045f))
                    val foto = Rect(r.left + pw * 0.06f, r.top + ph * 0.31f, r.left + pw * 0.6f, r.top + ph * 0.55f)
                    drawRect(t.c(mezcla(AZUL, PAPEL, 0.35f)), foto.topLeft, foto.size)
                    drawRect(t.c(mezcla(AZUL, TINTA, 0.5f)), Offset(foto.left, foto.top + foto.height * 0.55f), Size(foto.width, foto.height * 0.45f))
                    drawCircle(t.c(mezcla(ESTUCO, PAPEL, 0.4f)), foto.height * 0.25f, Offset(foto.left + foto.width * 0.65f, foto.top + foto.height * 0.45f))
                    for (k in 0 until 9) {
                        val y = r.top + ph * (0.31f + 0.028f * k)
                        drawRect(tin.copy(alpha = 0.35f), Offset(r.left + pw * 0.66f, y), Size(pw * 0.28f, 0.8f * u))
                    }
                    for (c in 0 until 3) for (k in 0 until 12) {
                        val y = r.top + ph * (0.6f + 0.031f * k)
                        drawRect(tin.copy(alpha = 0.32f), Offset(r.left + pw * (0.06f + 0.3f * c), y), Size(pw * (0.24f - 0.06f * azar(s + c, k) * (if (k % 5 == 4) 1f else 0f)), 0.8f * u))
                    }
                    drawRect(tin.copy(alpha = 0.3f), Offset(r.left + pw * 0.33f, r.top + ph * 0.6f), Size(0.6f * u, ph * 0.38f))
                } else {
                    // La revista: el título arriba, la imagen de la portada y las llamadas.
                    val claro = mezcla(color, CREMA, 0.6f)
                    var x = r.left + pw * 0.08f; var q = 0
                    while (x < r.right - pw * 0.15f) {
                        val lw = pw * (0.08f + 0.06f * azar(s, q))
                        drawRect(t.c(CREMA), Offset(x, r.top + ph * 0.05f), Size(lw, ph * 0.1f))
                        x += lw + pw * 0.03f; q++
                    }
                    drawCircle(t.c(claro), pw * 0.3f, Offset(r.center.x + pw * 0.08f, r.top + ph * 0.52f))
                    drawCircle(t.c(mezcla(color, TINTA, 0.3f)), pw * 0.17f, Offset(r.center.x + pw * 0.12f, r.top + ph * 0.46f))
                    drawPath(poligono(r.left, r.bottom, r.left + pw * 0.35f, r.top + ph * 0.62f, r.left + pw * 0.75f, r.bottom), t.c(mezcla(color, TINTA, 0.25f)))
                    drawRect(t.c(if (color == OCRE) LATA else OCRE), Offset(r.left + pw * 0.08f, r.top + ph * 0.22f), Size(pw * 0.36f, ph * 0.07f))
                    for (k in 0 until 3) drawRect(t.c(CREMA).copy(alpha = 0.85f), Offset(r.left + pw * 0.08f, r.top + ph * (0.33f + 0.05f * k)), Size(pw * (0.3f - 0.06f * k), ph * 0.025f))
                    drawRect(t.c(CREMA), Offset(r.right - pw * 0.24f, r.bottom - ph * 0.14f), Size(pw * 0.16f, ph * 0.08f))
                }
                // La luz: el lado que mira a la luz más claro y el otro en sombra; la arruga del pliegue.
                if (der) { drawRect(t.b(0.22f), Offset(r.right - pw * 0.3f, r.top), Size(pw * 0.3f, ph)); drawRect(t.s(0.1f), r.topLeft, Size(pw * 0.25f, ph)) }
                else { drawRect(t.b(0.16f), r.topLeft, Size(pw * 0.3f, ph)); drawRect(t.s(0.16f), Offset(r.right - pw * 0.25f, r.top), Size(pw * 0.25f, ph)) }
                drawRect(t.s(0.12f), Offset(r.center.x + pw * 0.05f, r.top), Size(pw * 0.06f, ph))
                drawRect(t.b(0.2f), Offset(r.center.x + pw * 0.11f, r.top), Size(pw * 0.02f, ph))
                drawRect(t.s(0.2f), r.topLeft, Size(pw, ph * 0.04f))
            }
            // La esquina que se levanta: el revés y su sombra.
            val doblez = poligono(r.right, r.bottom - esq, r.right - esq * 0.15f, r.bottom - esq * 0.1f, r.right - esq, r.bottom)
            translate(1.5f * u, 2f * u) { drawPath(doblez, t.s(0.3f)) }
            drawPath(doblez, t.c(mezcla(PAPEL, KRAFT, 0.3f)))
            // La pinza de madera, con su resorte.
            val pin = Rect(cx - w * 0.008f, top - w * 0.016f, cx + w * 0.008f, top + w * 0.024f)
            drawRect(t.s(0.3f), Offset(pin.left + 1.2f * u, pin.top + 1.6f * u), pin.size)
            drawRect(t.c(mezcla(KRAFT, CREMA, 0.3f)), pin.topLeft, pin.size)
            drawRect(t.b(0.4f), pin.topLeft, Size(pin.width * 0.35f, pin.height))
            drawRect(t.s(0.35f), Offset(pin.center.x - 0.5f * u, pin.top), Size(1f * u, pin.height))
            drawCircle(t.c(ZINC_OSC), pin.width * 0.4f, Offset(pin.center.x, pin.top + pin.height * 0.42f), style = Stroke(1f * u))
        }
    }

    /** La bombilla bajo el toldo: el cordón, el platillo de esmalte verde (blanco por dentro) y la bombilla; de noche, encendida. */
    private fun DrawScope.bombilla(w: Float) {
        val u = density
        val o = esNoche
        val c = LUZ * w
        val t = tn(c)
        drawLine(t.c(TINTA), Offset(c.x, w * LAMBRE1 - w * 0.02f), Offset(c.x, c.y - w * 0.025f), 1.2f * u)
        val plato = poligono(c.x - w * 0.008f, c.y - w * 0.027f, c.x + w * 0.008f, c.y - w * 0.027f, c.x + w * 0.032f, c.y - w * 0.006f, c.x - w * 0.032f, c.y - w * 0.006f)
        if (o) drawCircle(BOMBILLA.copy(alpha = 0.3f), w * 0.045f, c + Offset(0f, w * 0.004f))
        val bulbo = Offset(c.x, c.y + w * 0.002f)
        drawCircle(if (o) Color(0xFFFFF2D4) else mezcla(CREMA, ZINC, 0.25f), w * 0.011f, bulbo)
        if (o) drawCircle(BOMBILLA, w * 0.006f, bulbo)
        else { drawCircle(luzMueble(0.8f), w * 0.004f, bulbo - Offset(w * 0.004f, w * 0.003f)); drawCircle(sombraMueble(0.15f), w * 0.011f, bulbo + Offset(w * 0.004f, 0f)) }
        drawPath(plato, t.c(VERDE))
        clipPath(plato) {
            if (!o) { drawRect(luzMueble(0.3f), Offset(c.x - w * 0.032f, c.y - w * 0.03f), Size(w * 0.02f, w * 0.03f)); drawRect(sombraMueble(0.25f), Offset(c.x + w * 0.012f, c.y - w * 0.03f), Size(w * 0.02f, w * 0.03f)) }
            else drawRect(BOMBILLA.copy(alpha = 0.25f), Offset(c.x - w * 0.032f, c.y - w * 0.012f), Size(w * 0.064f, w * 0.006f))
        }
        drawOval(if (o) Color(0xFFFFEFC8) else mezcla(CREMA, ZINC, 0.2f), Offset(c.x - w * 0.032f, c.y - w * 0.009f), Size(w * 0.064f, w * 0.006f))
        drawRect(t.c(ZINC_OSC), Offset(c.x - w * 0.004f, c.y - w * 0.033f), Size(w * 0.008f, w * 0.007f))
    }

    /**
     * El casco de motorino colgado de una percha del pilar izquierdo, como un sombrero: la calota azul polvo vista de lado,
     * con su franja crema, el ribete de goma, la visera corta, los tres respiraderos de la oreja y la correa que cuelga con
     * su hebilla; debajo asoma la percha de hierro atornillada al pilar.
     */
    private fun DrawScope.casco(c: Offset) {
        val u = density
        val w = anchoW
        val rr = w * 0.046f
        val t = tn(c)
        // La percha: la pletina atornillada y el brazo que sube y se mete en el casco.
        val pl = Rect(c.x - w * 0.009f, c.y + rr * 0.5f, c.x + w * 0.009f, c.y + rr * 1.75f)
        drawRect(t.s(0.3f), Offset(pl.left + 1.5f * u, pl.top + 2f * u), pl.size)
        drawRect(t.c(VERDE_OSC), pl.topLeft, pl.size)
        drawRect(t.b(0.3f), pl.topLeft, Size(1.2f * u, pl.height))
        remache(Offset(pl.center.x, pl.bottom - w * 0.008f), 1.3f * u, t, ZINC)
        val calota = Path().apply {
            moveTo(c.x - rr, c.y + rr * 0.45f)
            cubicTo(c.x - rr * 1.1f, c.y - rr * 0.6f, c.x - rr * 0.45f, c.y - rr * 1.05f, c.x + rr * 0.1f, c.y - rr)
            cubicTo(c.x + rr * 0.7f, c.y - rr * 0.95f, c.x + rr * 1.02f, c.y - rr * 0.45f, c.x + rr * 1.02f, c.y - rr * 0.02f)
            lineTo(c.x + rr * 0.5f, c.y + rr * 0.04f)
            cubicTo(c.x + rr * 0.42f, c.y + rr * 0.35f, c.x + rr * 0.32f, c.y + rr * 0.6f, c.x + rr * 0.16f, c.y + rr * 0.85f)
            lineTo(c.x - rr * 0.55f, c.y + rr * 0.85f)
            cubicTo(c.x - rr * 0.85f, c.y + rr * 0.8f, c.x - rr, c.y + rr * 0.65f, c.x - rr, c.y + rr * 0.45f)
            close()
        }
        sombraDetras(calota, c, 0.32f)
        // El forro oscuro que se ve por el hueco de la cara.
        drawPath(poligono(c.x + rr * 0.3f, c.y + rr * 0.02f, c.x + rr * 0.95f, c.y, c.x + rr * 0.55f, c.y + rr * 0.7f, c.x + rr * 0.1f, c.y + rr * 0.8f), t.c(mezcla(TINTA, KRAFT, 0.25f)))
        drawPath(calota, t.c(AZUL))
        clipPath(calota) {
            // La franja crema que va de la frente a la nuca, siguiendo la calota.
            translate(0f, rr * 0.2f) { drawPath(calota, t.c(CREMA), style = Stroke(rr * 0.16f)) }
            drawRect(t.c(AZUL), Offset(c.x - rr * 1.2f, c.y + rr * 0.3f), Size(rr * 2.4f, rr))
            // El volumen: el claro del lado de la luz y la sombra del otro y abajo.
            if (luzDerecha(c.x)) {
                drawOval(t.b(0.22f), Offset(c.x + rr * 0.05f, c.y - rr * 0.85f), Size(rr * 0.7f, rr * 0.42f))
                drawRect(t.s(0.2f), Offset(c.x - rr * 1.1f, c.y - rr * 1.1f), Size(rr * 0.6f, rr * 2f))
            } else {
                drawOval(t.b(0.34f), Offset(c.x - rr * 0.78f, c.y - rr * 0.82f), Size(rr * 0.75f, rr * 0.45f))
                drawRect(t.s(0.2f), Offset(c.x + rr * 0.45f, c.y - rr * 1.1f), Size(rr * 0.7f, rr * 2f))
            }
            drawRect(t.s(0.2f), Offset(c.x - rr * 1.2f, c.y + rr * 0.35f), Size(rr * 2.4f, rr * 0.6f))
            // El ribete de goma del borde.
            drawRect(t.c(TINTA), Offset(c.x - rr * 1.2f, c.y + rr * 0.74f), Size(rr * 2.4f, rr * 0.14f))
            drawLine(t.c(TINTA), Offset(c.x + rr * 0.5f, c.y + rr * 0.04f), Offset(c.x + rr * 0.16f, c.y + rr * 0.85f), rr * 0.12f)
        }
        // Los respiraderos de la oreja, el botón de la visera y un rasguño del uso.
        listOf(-0.42f, -0.24f, -0.06f).forEach { f -> drawCircle(t.c(mezcla(TINTA, AZUL, 0.3f)), rr * 0.055f, Offset(c.x + rr * f, c.y + rr * 0.45f)) }
        remache(Offset(c.x + rr * 0.6f, c.y - rr * 0.22f), 1.3f * u, t, ZINC)
        drawLine(t.b(0.45f), Offset(c.x - rr * 0.5f, c.y - rr * 0.1f), Offset(c.x - rr * 0.15f, c.y + rr * 0.04f), 0.8f * u)
        // La visera corta, por delante.
        val vis = poligono(c.x + rr * 0.55f, c.y - rr * 0.1f, c.x + rr * 1.08f, c.y - rr * 0.08f, c.x + rr * 1.3f, c.y + rr * 0.14f, c.x + rr * 0.5f, c.y + rr * 0.06f)
        drawPath(vis, t.c(mezcla(TINTA, AZUL, 0.25f)))
        drawLine(t.b(0.45f), Offset(c.x + rr * 0.57f, c.y - rr * 0.09f), Offset(c.x + rr * 1.08f, c.y - rr * 0.07f), 1f * u)
        // La correa que cuelga, con su hebilla.
        val correa = Path().apply {
            moveTo(c.x - rr * 0.35f, c.y + rr * 0.85f)
            cubicTo(c.x - rr * 0.4f, c.y + rr * 1.5f, c.x + rr * 0.05f, c.y + rr * 1.75f, c.x + rr * 0.12f, c.y + rr * 1.35f)
            cubicTo(c.x + rr * 0.16f, c.y + rr * 1.15f, c.x + rr * 0.1f, c.y + rr * 0.95f, c.x + rr * 0.08f, c.y + rr * 0.85f)
        }
        translate(1.5f * u, 1.5f * u) { drawPath(correa, t.s(0.3f), style = Stroke(2f * u)) }
        drawPath(correa, t.c(TINTA), style = Stroke(2.2f * u))
        drawRect(t.c(ZINC), Offset(c.x - rr * 0.22f, c.y + rr * 1.48f), Size(rr * 0.2f, rr * 0.16f), style = Stroke(1.2f * u))
    }

    // ---------------------------------------------------------------- en el mostrador

    /** La maceta de geranios: el barro con su borde y su salitre, las hojas redondas con su cerco y tres umbelas rojas. */
    private fun DrawScope.geranios(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - w * 0.05f))
        val barro = mezcla(LADRILLO, ESTUCO, 0.35f)
        val mw = w * 0.075f; val mh = w * 0.062f
        val maceta = poligono(pie.x - mw / 2, pie.y - mh, pie.x + mw / 2, pie.y - mh, pie.x + mw * 0.36f, pie.y, pie.x - mw * 0.36f, pie.y)
        sombraDetras(maceta, pie, 0.2f)
        contacto(Offset(pie.x + w * 0.006f, pie.y), mw * 1.1f, 3f * u, 0.4f)
        val flores = listOf(Offset(-0.03f, -0.165f), Offset(0.028f, -0.15f), Offset(0.0f, -0.192f))
        flores.forEach { f -> drawLine(t.c(HOJA), Offset(pie.x + f.x * w * 0.4f, pie.y - mh), pie + f * w, 1.4f * u) }
        // Las hojas: redondas, chicas y encimadas, con el cerco pardo y el corte del pecíolo.
        val hojas = listOf(Offset(-0.05f, -0.07f), Offset(0.048f, -0.074f), Offset(-0.022f, -0.118f), Offset(0.02f, -0.122f), Offset(-0.058f, -0.1f), Offset(0.056f, -0.105f),
            Offset(-0.03f, -0.084f), Offset(0.026f, -0.088f), Offset(0f, -0.1f), Offset(-0.008f, -0.072f), Offset(0.04f, -0.13f), Offset(-0.042f, -0.132f))
        hojas.forEachIndexed { i, f ->
            val c = pie + f * w
            val tt = tn(c)
            val r = w * (0.014f + 0.006f * azar(5701, i))
            drawCircle(tt.s(0.3f), r, c + Offset(r * 0.3f, r * 0.3f))
            drawCircle(tt.c(mezcla(HOJA, VERDE_OSC, 0.35f * azar(5702, i))), r, c)
            drawCircle(tt.c(mezcla(HOJA, OXIDO, 0.4f)), r * 0.58f, c, style = Stroke(r * 0.14f))
            drawCircle(tt.b(0.22f), r * 0.4f, c - Offset(r * 0.38f, r * 0.38f))
            val a = 0.8f + 4f * azar(5703, i)
            drawPath(poligono(c.x, c.y, c.x + r * 1.1f * kotlin.math.cos(a), c.y + r * 1.1f * kotlin.math.sin(a), c.x + r * 1.1f * kotlin.math.cos(a + 0.35f), c.y + r * 1.1f * kotlin.math.sin(a + 0.35f)), tt.c(mezcla(VERDE_OSC, NOCHE, 0.3f)))
        }
        flores.forEachIndexed { i, f ->
            val fl = pie + f * w
            val tt = tn(fl)
            drawCircle(tt.s(0.22f), w * 0.017f, fl + Offset(w * 0.005f, w * 0.006f))
            for (k in 0 until 9) {
                val a = k * 0.7f + i
                val rad = if (k < 6) 0.014f else 0.005f
                val p = fl + Offset(w * rad * kotlin.math.cos(a), w * rad * 0.85f * kotlin.math.sin(a))
                drawCircle(tt.c(mezcla(LATA, LAMPARILLA, if (k % 2 == 0) 0.55f else 0.25f)), w * 0.0075f, p)
                drawCircle(tt.b(0.3f), w * 0.003f, p - Offset(w * 0.002f, w * 0.002f))
            }
        }
        // La maceta, por delante.
        drawPath(maceta, t.c(barro))
        clipPath(maceta) {
            cilindro(Rect(pie.x - mw / 2, pie.y - mh, pie.x + mw / 2, pie.y), t, 1.2f, luzDerecha(pie.x))
            drawRect(t.c(TRAVERTINO).copy(alpha = 0.35f), Offset(pie.x - mw / 2, pie.y - mh * 0.4f), Size(mw, mh * 0.14f))
        }
        val borde = Rect(pie.x - mw * 0.55f, pie.y - mh - w * 0.013f, pie.x + mw * 0.55f, pie.y - mh + w * 0.002f)
        drawRect(t.c(barro), borde.topLeft, borde.size)
        clipRect(borde.left, borde.top, borde.right, borde.bottom) { cilindro(borde, t, 1f, luzDerecha(pie.x)) }
        drawRect(t.b(0.4f), borde.topLeft, Size(borde.width * 0.5f, 1.2f * u))
        drawRect(t.s(0.3f), Offset(borde.left, borde.bottom - 1.5f * u), Size(borde.width, 1.5f * u))
        // Una hoja que cuelga por delante del borde.
        val hc = Offset(pie.x - mw * 0.42f, pie.y - mh + w * 0.004f)
        drawCircle(t.s(0.3f), w * 0.015f, hc + Offset(w * 0.004f, w * 0.004f))
        drawCircle(t.c(HOJA), w * 0.015f, hc)
        drawCircle(t.c(mezcla(HOJA, OXIDO, 0.4f)), w * 0.009f, hc, style = Stroke(w * 0.002f))
    }

    /**
     * El expositor giratorio de postales: el pie redondo, el mástil, tres pisos de bolsillos de alambre con sus postales (la de
     * frente entera, las de los lados en escorzo) y el remate.
     */
    private fun DrawScope.postales(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - w * 0.1f))
        val der = luzDerecha(pie.x)
        val altoE = w * 0.215f
        val mast = Rect(pie.x - w * 0.003f, pie.y - altoE, pie.x + w * 0.003f, pie.y)
        val sil = Path().apply { addRect(Rect(pie.x - w * 0.065f, pie.y - altoE + w * 0.02f, pie.x + w * 0.065f, pie.y - w * 0.02f)) }
        sombraDetras(sil, Offset(pie.x, pie.y - w * 0.1f), 0.16f)
        contacto(Offset(pie.x + w * 0.006f, pie.y), w * 0.08f, 3.5f * u, 0.4f)
        drawRect(t.c(ZINC_OSC), mast.topLeft, mast.size)
        drawOval(t.c(ZINC_OSC), Offset(pie.x - w * 0.035f, pie.y - w * 0.008f), Size(w * 0.07f, w * 0.01f))
        drawOval(t.b(0.4f), Offset(pie.x - w * 0.03f, pie.y - w * 0.008f), Size(w * 0.03f, w * 0.004f))
        val colores = listOf(AZUL, OCRE, mezcla(ROSA, LATA, 0.2f), VERDE_LUZ, ESTUCO, mezcla(AZUL, CREMA, 0.3f), LATA, mezcla(OCRE, ESTUCO, 0.5f), VERDE_BOLETO)
        for (p in 0 until 3) {
            val y1 = pie.y - w * 0.025f - p * w * 0.063f
            val alto = w * 0.055f
            listOf(-1, 1, 0).forEach { lado ->
                val i = p * 3 + lado + 1
                val col = colores[i]
                val cw = if (lado == 0) w * 0.045f else w * 0.022f
                val cx = pie.x + lado * w * 0.042f
                val r = Rect(cx - cw / 2, y1 - alto, cx + cw / 2, y1)
                val tt = t
                drawRect(tt.s(0.25f), Offset(r.left + 1.2f * u, r.top + 1.5f * u), r.size)
                drawRect(tt.c(CREMA), r.topLeft, r.size)
                val im = Rect(r.left + cw * 0.08f, r.top + alto * 0.06f, r.right - cw * 0.08f, r.bottom - alto * 0.22f)
                drawRect(tt.c(mezcla(col, CREMA, 0.15f)), im.topLeft, im.size)
                drawRect(tt.c(mezcla(col, TINTA, 0.3f)), Offset(im.left, im.top + im.height * 0.6f), Size(im.width, im.height * 0.4f))
                drawCircle(tt.c(mezcla(col, CREMA, 0.55f)), im.width * 0.2f, Offset(im.center.x + im.width * 0.15f, im.top + im.height * 0.38f))
                if (lado != 0) drawRect(tt.s(0.25f), r.topLeft, r.size)
                else if (der) drawRect(tt.b(0.18f), Offset(r.right - cw * 0.35f, r.top), Size(cw * 0.35f, alto))
                else drawRect(tt.b(0.18f), r.topLeft, Size(cw * 0.35f, alto))
                // El bolsillo de alambre por delante.
                drawLine(tt.c(ZINC_OSC), Offset(r.left - 1f * u, y1 - alto * 0.3f), Offset(r.right + 1f * u, y1 - alto * 0.3f), 1f * u)
            }
            drawLine(t.c(ZINC_OSC), Offset(pie.x - w * 0.056f, y1), Offset(pie.x + w * 0.056f, y1), 1.4f * u)
            drawLine(t.b(0.5f), Offset(pie.x - w * 0.056f, y1 - 0.8f * u), Offset(pie.x + w * 0.056f, y1 - 0.8f * u), 0.6f * u)
        }
        drawCircle(t.c(ZINC_OSC), w * 0.007f, Offset(pie.x, pie.y - altoE))
        destello(Offset(pie.x - w * 0.002f, pie.y - altoE - w * 0.002f), 1.1f * u, t)
    }

    /** El bote de bolígrafos: una lata de conserva con su etiqueta desteñida, cuatro bolígrafos y un lápiz. */
    private fun DrawScope.boligrafos(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(pie)
        val bw = w * 0.04f; val bh = w * 0.05f
        val r = Rect(pie.x - bw / 2, pie.y - bh, pie.x + bw / 2, pie.y)
        contacto(Offset(pie.x + w * 0.005f, pie.y), bw * 1.3f, 3f * u, 0.4f)
        listOf(Triple(-0.012f, -12f, AZUL), Triple(-0.004f, -4f, TINTA), Triple(0.006f, 6f, LATA), Triple(0.012f, 14f, OCRE), Triple(0.0f, 2f, VERDE)).forEachIndexed { i, (dx, g, col) ->
            val base = Offset(pie.x + w * dx, r.top + w * 0.005f)
            rotate(g, base) {
                val largo = w * (0.06f + 0.012f * azar(5801, i))
                drawRect(t.c(if (i == 3) OCRE else CREMA), Offset(base.x - w * 0.0035f, base.y - largo), Size(w * 0.007f, largo))
                drawRect(t.c(col), Offset(base.x - w * 0.0035f, base.y - largo), Size(w * 0.007f, w * 0.012f))
                drawRect(t.s(0.2f), Offset(base.x + w * 0.001f, base.y - largo), Size(w * 0.0025f, largo))
            }
        }
        drawRect(t.c(ZINC), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawRect(t.c(mezcla(LATA, PAPEL, 0.45f)), Offset(r.left, r.top + bh * 0.2f), Size(bw, bh * 0.6f))
            drawRect(t.c(mezcla(HOJA, PAPEL, 0.4f)), Offset(r.left, r.top + bh * 0.42f), Size(bw, bh * 0.16f))
            cilindro(r, t, 1.3f, luzDerecha(pie.x))
            for (k in 0 until 3) drawRect(t.s(0.15f), Offset(r.left, r.top + bh * (0.1f + 0.35f * k)), Size(bw, 0.8f * u))
        }
        drawRect(t.b(0.5f), r.topLeft, Size(bw, 1f * u))
    }

    /** La cafetera moka de aluminio: la caldera y el depósito de ocho caras, la cintura, la tapa con su pomo, el pico y el asa. */
    private fun DrawScope.moka(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - w * 0.04f))
        val der = luzDerecha(pie.x)
        val cin = pie.y - w * 0.04f
        val top = pie.y - w * 0.082f
        val abajo = poligono(pie.x - w * 0.026f, pie.y, pie.x + w * 0.026f, pie.y, pie.x + w * 0.016f, cin, pie.x - w * 0.016f, cin)
        val arriba = poligono(pie.x - w * 0.016f, cin, pie.x + w * 0.016f, cin, pie.x + w * 0.024f, top, pie.x - w * 0.024f, top)
        val todo = Path().apply { addPath(abajo); addPath(arriba) }
        sombraDetras(todo, Offset(pie.x, cin), 0.18f)
        contacto(Offset(pie.x + w * 0.006f, pie.y), w * 0.07f, 3f * u, 0.45f)
        // El asa negra, a la derecha.
        val asa = Path().apply { moveTo(pie.x + w * 0.02f, top + w * 0.006f); cubicTo(pie.x + w * 0.05f, top + w * 0.004f, pie.x + w * 0.045f, cin, pie.x + w * 0.018f, cin - w * 0.004f) }
        drawPath(asa, t.c(TINTA), style = Stroke(w * 0.008f, cap = StrokeCap.Round))
        listOf(abajo, arriba).forEach { p ->
            drawPath(p, t.c(ZINC))
            clipPath(p) {
                val r = Rect(pie.x - w * 0.026f, top, pie.x + w * 0.026f, pie.y)
                // Las caras: cuatro franjas de luz y sombra.
                drawRect(t.b(0.35f), Offset(if (der) r.right - r.width * 0.35f else r.left, r.top), Size(r.width * 0.25f, r.height))
                drawRect(t.s(0.12f), Offset(r.center.x - r.width * 0.05f, r.top), Size(r.width * 0.1f, r.height))
                drawRect(t.s(0.3f), Offset(if (der) r.left else r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
                drawLine(t.s(0.25f), Offset(r.left + r.width * 0.25f, r.top), Offset(r.left + r.width * 0.25f, r.bottom), 0.6f * u)
                drawLine(t.s(0.25f), Offset(r.right - r.width * 0.25f, r.top), Offset(r.right - r.width * 0.25f, r.bottom), 0.6f * u)
            }
        }
        // El quemado de la hornilla abajo.
        drawRect(t.c(mezcla(ZINC_OSC, TINTA, 0.4f)).copy(alpha = 0.6f), Offset(pie.x - w * 0.026f, pie.y - w * 0.005f), Size(w * 0.052f, w * 0.005f))
        // La cintura, la tapa y el pomo, y el pico.
        drawRect(t.c(ZINC_OSC), Offset(pie.x - w * 0.018f, cin - w * 0.003f), Size(w * 0.036f, w * 0.006f))
        drawRect(t.c(ZINC), Offset(pie.x - w * 0.025f, top - w * 0.004f), Size(w * 0.05f, w * 0.006f))
        drawRect(t.b(0.5f), Offset(pie.x - w * 0.025f, top - w * 0.004f), Size(w * 0.05f, 1f * u))
        drawOval(t.c(TINTA), Offset(pie.x - w * 0.006f, top - w * 0.012f), Size(w * 0.012f, w * 0.009f))
        drawPath(poligono(pie.x - w * 0.022f, top + w * 0.002f, pie.x - w * 0.035f, top - w * 0.004f, pie.x - w * 0.026f, top + w * 0.01f), t.c(ZINC))
        destello(Offset(pie.x + (if (der) w * 0.012f else -w * 0.012f), top + w * 0.015f), 1.3f * u, t)
    }

    /** La tacita de café en su platillo, con la cucharilla. */
    private fun DrawScope.tacita(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(pie)
        contacto(Offset(pie.x + w * 0.004f, pie.y), w * 0.05f, 2.5f * u, 0.4f)
        drawOval(t.c(CREMA), Offset(pie.x - w * 0.024f, pie.y - w * 0.006f), Size(w * 0.048f, w * 0.009f))
        drawOval(t.s(0.2f), Offset(pie.x - w * 0.024f, pie.y - w * 0.003f), Size(w * 0.048f, w * 0.005f))
        val taza = Path().apply {
            moveTo(pie.x - w * 0.015f, pie.y - w * 0.028f)
            lineTo(pie.x + w * 0.015f, pie.y - w * 0.028f)
            cubicTo(pie.x + w * 0.015f, pie.y - w * 0.01f, pie.x + w * 0.008f, pie.y - w * 0.004f, pie.x, pie.y - w * 0.004f)
            cubicTo(pie.x - w * 0.008f, pie.y - w * 0.004f, pie.x - w * 0.015f, pie.y - w * 0.01f, pie.x - w * 0.015f, pie.y - w * 0.028f)
            close()
        }
        drawArc(t.c(CREMA), -90f, 180f, false, Offset(pie.x + w * 0.009f, pie.y - w * 0.024f), Size(w * 0.012f, w * 0.012f), style = Stroke(1.6f * u))
        drawPath(taza, t.c(CREMA))
        clipPath(taza) {
            cilindro(Rect(pie.x - w * 0.015f, pie.y - w * 0.03f, pie.x + w * 0.015f, pie.y), t, 1.2f, luzDerecha(pie.x))
            drawRect(t.c(VERDE), Offset(pie.x - w * 0.016f, pie.y - w * 0.023f), Size(w * 0.032f, w * 0.002f))
        }
        drawOval(t.c(mezcla(OXIDO, TINTA, 0.5f)), Offset(pie.x - w * 0.014f, pie.y - w * 0.031f), Size(w * 0.028f, w * 0.005f))
        drawLine(t.c(ZINC), Offset(pie.x - w * 0.02f, pie.y - w * 0.007f), Offset(pie.x - w * 0.034f, pie.y - w * 0.004f), 1.2f * u)
    }

    /**
     * La radio de mesa: la caja mostaza de esquinas redondas, la rejilla crema del altavoz, el dial con su aguja, las dos
     * perillas, el asa de cuero y la antena estirada en diagonal.
     */
    private fun DrawScope.radio(pie: Offset) {
        val u = density
        val w = anchoW
        val r = Rect(pie.x - w * 0.068f, pie.y - w * 0.075f, pie.x + w * 0.062f, pie.y)
        val t = tn(r.center)
        val der = luzDerecha(r.center.x)
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(r, CornerRadius(w * 0.012f))) }
        sombraDetras(forma, r.center, 0.22f)
        contacto(Offset(r.center.x + w * 0.008f, pie.y), r.width * 1.05f, 3.5f * u, 0.45f)
        // La antena.
        val an0 = Offset(r.right - w * 0.012f, r.top + w * 0.004f)
        val an1 = Offset(r.right - w * 0.095f, r.top - w * 0.15f)
        drawLine(t.c(ZINC), an0, an1, 1.3f * u)
        drawLine(t.c(ZINC), an0, Offset(an0.x + (an1.x - an0.x) * 0.4f, an0.y + (an1.y - an0.y) * 0.4f), 2.2f * u)
        drawCircle(t.c(ZINC), 1.6f * u, an1)
        // El asa.
        drawArc(t.c(mezcla(OXIDO, TINTA, 0.35f)), 180f, 180f, false, Offset(r.left + w * 0.02f, r.top - w * 0.03f), Size(w * 0.07f, w * 0.06f), style = Stroke(w * 0.006f))
        drawPath(forma, t.c(OCRE))
        clipPath(forma) {
            if (!der) { drawRect(t.b(0.25f), r.topLeft, Size(r.width * 0.2f, r.height)); drawRect(t.s(0.25f), Offset(r.right - r.width * 0.16f, r.top), Size(r.width * 0.16f, r.height)) }
            else { drawRect(t.b(0.25f), Offset(r.right - r.width * 0.2f, r.top), Size(r.width * 0.2f, r.height)); drawRect(t.s(0.2f), r.topLeft, Size(r.width * 0.14f, r.height)) }
            drawRect(t.b(0.4f), r.topLeft, Size(r.width, 1.2f * u))
            drawRect(t.s(0.3f), Offset(r.left, r.bottom - w * 0.006f), Size(r.width, w * 0.006f))
        }
        // La rejilla del altavoz.
        val rej = Rect(r.left + w * 0.01f, r.top + w * 0.012f, r.left + w * 0.072f, r.bottom - w * 0.012f)
        drawRoundRect(t.c(CREMA), rej.topLeft, rej.size, CornerRadius(w * 0.006f))
        for (k in 0 until 7) drawRect(t.s(0.35f), Offset(rej.left + w * 0.004f, rej.top + rej.height * (0.12f + 0.12f * k)), Size(rej.width - w * 0.008f, 1.1f * u))
        drawRect(t.s(0.2f), rej.topLeft, Size(rej.width, 1.5f * u))
        // El dial y las perillas.
        val dial = Rect(rej.right + w * 0.008f, rej.top, r.right - w * 0.01f, rej.top + w * 0.025f)
        drawRect(t.c(mezcla(CREMA, OCRE, 0.3f)), dial.topLeft, dial.size)
        for (k in 0 until 6) drawLine(t.c(TINTA).copy(alpha = 0.6f), Offset(dial.left + dial.width * (0.1f + 0.16f * k), dial.top + dial.height * 0.25f), Offset(dial.left + dial.width * (0.1f + 0.16f * k), dial.top + dial.height * 0.5f), 0.6f * u)
        drawLine(t.c(LATA), Offset(dial.left + dial.width * 0.6f, dial.top + 1f * u), Offset(dial.left + dial.width * 0.6f, dial.bottom - 1f * u), 1f * u)
        drawRect(t.b(0.4f), dial.topLeft, Size(dial.width * 0.4f, dial.height))
        listOf(dial.left + dial.width * 0.25f, dial.left + dial.width * 0.75f).forEach { x ->
            val c = Offset(x, r.bottom - w * 0.022f)
            drawCircle(t.s(0.35f), w * 0.009f, c + Offset(1.2f * u, 1.5f * u))
            drawCircle(t.c(BRONCE), w * 0.009f, c)
            drawCircle(t.b(0.5f), w * 0.004f, c - Offset(w * 0.003f, w * 0.003f))
        }
        filo(r, t, der)
    }

    // ---------------------------------------------------------------- al pie

    /**
     * Los fardos de periódicos atados en cruz con cuerda: tres apilados y descuadrados (el de arriba, de papel rosa), cada uno
     * con su cara de arriba en escorzo, cortados por el borde izquierdo.
     */
    private fun DrawScope.fardos(b: Rect, w: Float, hb: Float) {
        val pie = b.top + hb * 0.96f
        contacto(Offset(w * 0.13f, pie), w * 0.36f, w * 0.02f, 0.42f)
        var y = pie
        listOf(
            Triple(-0.05f to 0.245f, 0.088f, 0f to 5901),
            Triple(-0.03f to 0.2f, 0.08f, -2.5f to 5902),
            Triple(-0.07f to 0.15f, 0.068f, 3f to 5903),
        ).forEachIndexed { i, (xs, alto, gs) ->
            fardo(Rect(w * xs.first, y - w * alto, w * xs.second, y), gs.first, gs.second, if (i == 2) mezcla(ROSA, PAPEL, 0.4f) else PAPEL)
            y -= w * alto * 0.98f
        }
    }

    /** El cubo de zinc, abollado, con su asa y un trapo sobre el borde. */
    private fun DrawScope.cubo(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - w * 0.04f))
        val der = luzDerecha(pie.x)
        val cw = w * 0.1f; val ch = w * 0.098f
        val cuerpo = poligono(pie.x - cw / 2, pie.y - ch, pie.x + cw / 2, pie.y - ch, pie.x + cw * 0.38f, pie.y, pie.x - cw * 0.38f, pie.y)
        contacto(Offset(pie.x + w * 0.018f, pie.y), cw * 1.4f, w * 0.016f, 0.42f)
        translate(w * 0.02f, w * 0.004f) { drawPath(cuerpo, t.s(0.2f)) }
        drawArc(t.c(ZINC_OSC), 180f, 180f, false, Offset(pie.x - cw * 0.48f, pie.y - ch - w * 0.045f), Size(cw * 0.96f, w * 0.09f), style = Stroke(1.5f * u))
        drawPath(cuerpo, t.c(ZINC))
        clipPath(cuerpo) {
            cilindro(Rect(pie.x - cw / 2, pie.y - ch, pie.x + cw / 2, pie.y), t, 1.4f, der)
            listOf(0.3f, 0.72f).forEach { f -> drawRect(t.s(0.25f), Offset(pie.x - cw / 2, pie.y - ch * f), Size(cw, 1.5f * u)); drawRect(t.b(0.3f), Offset(pie.x - cw / 2, pie.y - ch * f + 1.5f * u), Size(cw, 0.8f * u)) }
            drawOval(t.s(0.25f), Offset(pie.x + cw * 0.05f, pie.y - ch * 0.55f), Size(cw * 0.2f, ch * 0.15f))
            drawRect(t.c(OXIDO).copy(alpha = 0.3f), Offset(pie.x - cw / 2, pie.y - ch * 0.08f), Size(cw, ch * 0.08f))
        }
        drawOval(t.c(ZINC_OSC), Offset(pie.x - cw / 2, pie.y - ch - w * 0.006f), Size(cw, w * 0.012f))
        drawOval(t.c(mezcla(ZINC_OSC, TINTA, 0.45f)), Offset(pie.x - cw * 0.44f, pie.y - ch - w * 0.004f), Size(cw * 0.88f, w * 0.008f))
        listOf(-1f, 1f).forEach { s -> remache(Offset(pie.x + s * cw * 0.47f, pie.y - ch + w * 0.004f), 1.5f * u, t, ZINC) }
        // El trapo, colgado del borde, con sus pliegues.
        val trapo = Path().apply {
            moveTo(pie.x - cw * 0.5f, pie.y - ch - w * 0.006f)
            cubicTo(pie.x - cw * 0.3f, pie.y - ch - w * 0.016f, pie.x - cw * 0.1f, pie.y - ch - w * 0.002f, pie.x + cw * 0.04f, pie.y - ch - w * 0.006f)
            cubicTo(pie.x + cw * 0.02f, pie.y - ch + w * 0.02f, pie.x - cw * 0.02f, pie.y - ch + w * 0.035f, pie.x - cw * 0.08f, pie.y - ch + w * 0.05f)
            cubicTo(pie.x - cw * 0.2f, pie.y - ch + w * 0.04f, pie.x - cw * 0.3f, pie.y - ch + w * 0.058f, pie.x - cw * 0.47f, pie.y - ch + w * 0.045f)
            close()
        }
        translate(1.5f * u, 2f * u) { drawPath(trapo, t.s(0.28f)) }
        drawPath(trapo, t.c(mezcla(ROSA, KRAFT, 0.35f)))
        clipPath(trapo) {
            listOf(0.38f, 0.26f, 0.12f).forEach { f ->
                drawLine(t.s(0.26f), Offset(pie.x - cw * f, pie.y - ch - w * 0.004f), Offset(pie.x - cw * (f + 0.05f), pie.y - ch + w * 0.055f), 1.2f * u)
                drawLine(t.b(0.3f), Offset(pie.x - cw * f - 1.4f * u, pie.y - ch - w * 0.004f), Offset(pie.x - cw * (f + 0.05f) - 1.4f * u, pie.y - ch + w * 0.055f), 0.9f * u)
            }
            drawRect(t.c(LATA).copy(alpha = 0.5f), Offset(pie.x - cw * 0.6f, pie.y - ch + w * 0.03f), Size(cw * 0.7f, w * 0.004f))
        }
    }

    /**
     * La caja de madera de botellas, cortada por el borde derecho: tres tablas con sus vetas y sus clavos, las botellas verdes y
     * pardas que se ven entre ellas y sus cuellos con chapa por arriba (falta una).
     */
    private fun DrawScope.botellas(pie: Offset) {
        val u = density
        val w = anchoW
        val r = Rect(pie.x - w * 0.15f, pie.y - w * 0.108f, pie.x + w * 0.17f, pie.y)
        val t = tn(r.center)
        contacto(Offset(r.center.x + w * 0.01f, pie.y), r.width * 1.05f, w * 0.018f, 0.42f)
        translate(w * 0.02f, -w * 0.004f) { drawRect(t.s(0.2f), r.topLeft, r.size) }
        drawRect(t.c(mezcla(TINTA, OXIDO, 0.3f)), r.topLeft, r.size)
        for (k in 0 until 6) {
            if (k == 3) continue
            val x = r.left + r.width * (0.1f + 0.165f * k)
            val col = if (k % 3 == 1) mezcla(OXIDO, KRAFT, 0.25f) else mezcla(HOJA, VERDE_OSC, 0.4f)
            val alto = w * (0.056f + 0.012f * azar(5950, k))
            val hombro = r.top - alto * 0.3f
            drawRect(t.c(col), Offset(x - w * 0.021f, hombro), Size(w * 0.042f, r.bottom - hombro))
            drawPath(poligono(x - w * 0.021f, hombro, x - w * 0.007f, r.top - alto * 0.62f, x + w * 0.007f, r.top - alto * 0.62f, x + w * 0.021f, hombro), t.c(col))
            drawRect(t.c(col), Offset(x - w * 0.007f, r.top - alto), Size(w * 0.014f, alto * 0.4f))
            drawRect(t.b(0.4f), Offset(x - w * 0.016f, hombro), Size(w * 0.006f, r.bottom - hombro))
            drawRect(t.b(0.45f), Offset(x - w * 0.005f, r.top - alto), Size(w * 0.003f, alto * 0.5f))
            drawRect(t.s(0.25f), Offset(x + w * 0.008f, hombro), Size(w * 0.013f, r.bottom - hombro))
            if (k != 5) {
                drawRect(t.c(if (k % 2 == 0) LATA else ORO), Offset(x - w * 0.009f, r.top - alto - w * 0.005f), Size(w * 0.018f, w * 0.007f))
                drawRect(t.b(0.4f), Offset(x - w * 0.009f, r.top - alto - w * 0.005f), Size(w * 0.018f, 0.9f * u))
            }
        }
        val madera = mezcla(KRAFT, OXIDO, 0.22f)
        listOf(0f to 0.27f, 0.38f to 0.64f, 0.75f to 1f).forEachIndexed { i, (a, bb) ->
            val br = Rect(r.left, r.top + r.height * a, r.right, r.top + r.height * bb)
            drawRect(t.s(0.3f), Offset(br.left, br.bottom), Size(br.width, 2f * u))
            drawRect(t.c(mezcla(madera, if (i == 1) CREMA else TINTA, 0.1f)), br.topLeft, br.size)
            vetas(br, (if (esNoche) NOCHE else TINTA).copy(alpha = 0.2f), 5951 + i, 0.7f, grosor = 0.5f)
            drawRect(t.b(0.4f), br.topLeft, Size(br.width, 1.2f * u))
            drawRect(t.s(0.3f), Offset(br.left, br.bottom - 1.4f * u), Size(br.width, 1.4f * u))
        }
        // El montante de la esquina, con sus clavos, y la marca a fuego (un rectángulo, sin letras) en la tabla de en medio.
        val mo = Rect(r.left, r.top - w * 0.004f, r.left + w * 0.024f, r.bottom)
        drawRect(t.c(mezcla(madera, CREMA, 0.15f)), mo.topLeft, mo.size)
        drawRect(t.b(0.35f), mo.topLeft, Size(1.2f * u, mo.height))
        drawRect(t.s(0.3f), Offset(mo.right, mo.top), Size(1.6f * u, mo.height))
        listOf(0.13f, 0.5f, 0.87f).forEach { f -> drawCircle(t.c(mezcla(TINTA, ZINC_OSC, 0.4f)), 1.2f * u, Offset(mo.center.x, r.top + r.height * f)) }
        drawRect(t.c(mezcla(madera, TINTA, 0.45f)), Offset(r.left + r.width * 0.3f, r.top + r.height * 0.44f), Size(r.width * 0.3f, r.height * 0.14f), style = Stroke(1.2f * u))
    }

    // ---------------------------------------------------------------- los compartimentos

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val k = if (!oscuro) 0f else if (dentro) luz(centro) else 0.5f
        val dia = if (oscuro) 0f else 0.07f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.025f else 0f)
        val t = Tono(oscuro, k, 0f, dia)
        val der = oscuro && dentro && centro.x < LUZ.x
        fondoHueco(w, h, t, der)
        if (c.vacio) { vacio(w, h, c, t); return }
        val m = revolver(c.semilla)
        translate(0f, -SALIDA * h * ab) {
            when {
                c.sinTema -> zapatos(w, h, c, t, der)
                else -> when (Math.floorMod(m, 3)) {
                    0 -> latita(w, h, c, m, t, der)
                    1 -> revistas(w, h, c, m, t, der)
                    else -> cajaPostales(w, h, c, m, t, der)
                }
            }
        }
    }

    /**
     * El fondo de un hueco: la chapa del fondo del quiosco con sus juntas remachadas, la sombra de la repisa de arriba en
     * escalones, el costado que no mira a la luz más hondo y la repisa de hierro con su canto.
     */
    private fun DrawScope.fondoHueco(w: Float, h: Float, t: Tono, der: Boolean) {
        val u = density
        drawRect(t.c(mezcla(VERDE_OSC, VERDE, 0.35f)), Offset.Zero, Size(w, h))
        drawRect(t.b(0.08f), Offset(if (der) w * 0.6f else 0f, 0f), Size(w * 0.4f, h))
        listOf(0.33f, 0.66f).forEach { fx ->
            drawRect(t.s(0.3f), Offset(w * fx, 0f), Size(1.2f * u, h))
            drawRect(t.b(0.16f), Offset(w * fx + 1.2f * u, 0f), Size(0.8f * u, h))
            for (k in 0 until 4) drawCircle(t.c(VERDE_LUZ).copy(alpha = 0.6f), 1f * u, Offset(w * fx - 2.5f * u, h * (0.15f + 0.22f * k)))
        }
        listOf(0.36f to 0.1f, 0.22f to 0.12f, 0.09f to 0.24f).forEach { (a, al) -> drawRect(t.s(al), Offset.Zero, Size(w, h * a)) }
        drawRect(t.s(0.24f), Offset(if (der) 0f else w * 0.92f, 0f), Size(w * 0.08f, h))
        drawRect(t.s(0.14f), Offset(if (der) w * 0.975f else 0f, 0f), Size(w * 0.025f, h))
        val piso = h * PISO
        drawRect(t.c(VERDE_LUZ), Offset(0f, piso), Size(w, h - piso))
        drawRect(t.b(0.45f), Offset(0f, piso), Size(w, 1f * u))
        drawRect(t.s(0.25f), Offset(0f, piso + 1f * u), Size(w, 1.5f * u))
    }

    /**
     * La placa esmaltada del tema, atornillada al frente del recipiente: el esmalte crema con su filete verde, un saltado en
     * la esquina por donde asoma el hierro, su brillo y sus dos tornillos.
     */
    private fun DrawScope.esmalte(w: Float, h: Float, t: Tono) {
        val u = density
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRoundRect(t.s(0.32f), Offset(r.left + 1.5f * u, r.top + 2.2f * u), r.size, CornerRadius(4f * u))
        val fondo = if (t.o) mezcla(ESMALTE_LEJOS, mezcla(CREMA, BOMBILLA, 0.12f), (1.15f * t.k.pow(0.75f)).coerceAtMost(1f)) else t.c(CREMA)
        drawRoundRect(fondo, r.topLeft, r.size, CornerRadius(4f * u))
        drawRoundRect(if (t.o) mezcla(VERDE, fondo, 0.25f) else VERDE, Offset(r.left + 2.2f * u, r.top + 2.2f * u), Size(r.width - 4.4f * u, r.height - 4.4f * u), CornerRadius(3f * u), style = Stroke(1.2f * u))
        // El saltado del esmalte, abajo a la derecha: asoma el hierro.
        drawPath(poligono(r.right - w * 0.05f, r.bottom, r.right - 1f * u, r.bottom - h * 0.06f, r.right - 1f * u, r.bottom - 1f * u), t.c(Color(0xFF26303A)))
        drawRect(if (t.o) BOMBILLA.copy(alpha = (0.4f * t.k).coerceAtMost(0.35f)) else luzMueble(0.6f), Offset(r.left + 4f * u, r.top + 1f * u), Size(r.width * 0.35f, 1f * u))
        listOf(Offset(r.left + 4.2f * u, r.center.y), Offset(r.right - 4.2f * u, r.center.y)).forEach { remache(it, 1.3f * u, t, ZINC) }
    }

    /** El rótulo de lo que no tiene tema: un cartón atado con cordel, más claro que el kraft para que el texto se lea. */
    private fun DrawScope.carton(w: Float, h: Float, t: Tono) {
        val u = density
        val r = Rect(w * rotulo.left + w * 0.01f, h * rotulo.top, w * rotulo.right - w * 0.01f, h * rotulo.bottom)
        rotate(-1.2f, r.center) {
            val forma = poligono(r.left, r.top, r.right, r.top + 2f * u, r.right - 1f * u, r.bottom, r.left + 2f * u, r.bottom - 1f * u)
            translate(1.5f * u, 2f * u) { drawPath(forma, t.s(0.3f)) }
            val fondo = if (t.o) mezcla(ESMALTE_LEJOS, mezcla(CREMA, BOMBILLA, 0.1f), (1.15f * t.k.pow(0.75f)).coerceAtMost(1f)) else t.c(mezcla(KRAFT, CREMA, 0.6f))
            drawPath(forma, fondo)
            clipPath(forma) { pinceladas(r, if (t.o) NOCHE else KRAFT, 6001, 5, u, if (t.o) 0.08f else 0.18f) }
            drawCircle(t.s(0.4f), 1.8f * u, Offset(r.left + 6f * u, r.top + 5f * u))
            drawLine(t.c(mezcla(KRAFT, OXIDO, 0.3f)), Offset(r.left + 6f * u, r.top + 5f * u), Offset(r.left - 2f * u, r.top - h * 0.12f), 1f * u)
        }
    }

    /** Los boletos que asoman de una ranura: en abanico, más y más altos cuanto más guarda, con su franja impresa y su taladro. */
    private fun DrawScope.boletos(cx: Float, base: Float, ancho: Float, h: Float, c: EstadoCajon, t: Tono, maxN: Int = 8) {
        val u = density
        val k = c.n.coerceIn(1, maxN)
        val colores = listOf(ROSA, VERDE_BOLETO, OCRE, CREMA, mezcla(AZUL, CREMA, 0.5f))
        for (i in 0 until k) {
            val f = if (k == 1) 0f else (i - (k - 1) / 2f) / ((k - 1) / 2f)
            val x = cx + f * ancho * 0.4f
            rotate(f * 13f + (azar(c.semilla, i) - 0.5f) * 8f, Offset(x, base)) {
                val alto = h * (0.075f + 0.014f * c.n.coerceAtMost(8) + 0.035f * azar(c.semilla, i + 10))
                val bw = ancho * 0.17f
                val col = colores[(i + Math.floorMod(c.semilla, 5)) % 5]
                drawRect(t.s(0.22f), Offset(x - bw / 2 + 1.4f * u, base - alto + 1.4f * u), Size(bw, alto))
                drawRect(t.c(col), Offset(x - bw / 2, base - alto), Size(bw, alto))
                drawRect(t.c(mezcla(col, TINTA, 0.45f)), Offset(x - bw / 2, base - alto + alto * 0.12f), Size(bw, alto * 0.1f))
                drawLine(t.c(TINTA).copy(alpha = 0.35f), Offset(x - bw * 0.32f, base - alto + alto * 0.36f), Offset(x + bw * 0.32f, base - alto + alto * 0.36f), 0.8f * u)
                drawLine(t.c(TINTA).copy(alpha = 0.35f), Offset(x - bw * 0.32f, base - alto + alto * 0.48f), Offset(x + bw * 0.1f, base - alto + alto * 0.48f), 0.8f * u)
                drawRect(t.b(0.35f), Offset(x - bw / 2, base - alto), Size(bw, 0.8f * u))
                drawRect(t.s(0.12f), Offset(x + bw * 0.25f, base - alto), Size(bw * 0.25f, alto))
                if (i % 2 == 0) drawCircle(t.s(0.55f), 1.2f * u, Offset(x + bw * 0.22f, base - alto + alto * 0.3f))
            }
        }
    }

    /**
     * La latita de boletos, del color que le toque (roja, verde azulada, mostaza o azul polvo): la tapa vista un poco desde
     * arriba, con su ranura y los boletos que asoman, la franja litografiada con sus puntos, los filetes dorados, el medallón,
     * una abolladura donde asoma la hojalata y una mancha de óxido; la placa del tema, atornillada al frente.
     */
    private fun DrawScope.latita(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val lata = when (Math.floorMod(m ushr 3, 4)) { 0 -> LATA; 1 -> mezcla(VERDE_LUZ, AZUL, 0.35f); 2 -> mezcla(OCRE, OXIDO, 0.22f); else -> AZUL }
        val oscura = mezcla(lata, TINTA, 0.4f)
        val caja = Rect(w * 0.028f, h * 0.345f, w * 0.972f, piso)
        val tapa = Rect(caja.left - w * 0.012f, caja.top - h * 0.085f, caja.right + w * 0.012f, caja.top + h * 0.012f)
        val cara = h * 0.04f
        drawRect(t.s(0.3f), Offset(tapa.left + w * 0.03f, tapa.top - cara + h * 0.03f), Size(tapa.width, piso - tapa.top + cara - h * 0.03f))
        pisa(t, caja.center.x + w * 0.02f, piso, caja.width * 1.04f, h * 0.05f)
        // La cara de arriba de la tapa, en escorzo, con la ranura; de ella salen los boletos.
        val arriba = poligono(tapa.left, tapa.top, tapa.left + w * 0.03f, tapa.top - cara, tapa.right - w * 0.03f, tapa.top - cara, tapa.right, tapa.top)
        drawPath(arriba, t.c(mezcla(lata, CREMA, 0.12f)))
        drawPath(arriba, t.b(0.18f))
        val rx = w * (0.44f + 0.12f * azar(c.semilla, 3))
        drawRoundRect(t.c(Color(0xFF140A10)), Offset(rx - w * 0.3f, tapa.top - cara * 0.72f), Size(w * 0.6f, cara * 0.42f), CornerRadius(cara * 0.2f))
        boletos(rx, tapa.top - cara * 0.4f, w * 0.62f, h, c, t)
        drawRect(t.c(mezcla(lata, CREMA, 0.12f)), Offset(rx - w * 0.31f, tapa.top - cara * 0.36f), Size(w * 0.62f, cara * 0.36f))
        drawRect(t.s(0.3f), Offset(rx - w * 0.3f, tapa.top - cara * 0.4f), Size(w * 0.6f, 1.2f * u))
        // El cuerpo, con su franja litografiada, sus filetes y su medallón.
        drawRect(t.c(lata), caja.topLeft, caja.size)
        val yf = caja.top + h * 0.035f
        drawRect(t.c(CREMA), Offset(caja.left, yf), Size(caja.width, h * 0.05f))
        var x = caja.left + w * 0.012f
        while (x < caja.right - w * 0.012f) { drawRect(t.c(lata), Offset(x, yf + h * 0.015f), Size(h * 0.02f, h * 0.02f)); x += h * 0.05f }
        drawRect(t.c(ORO), Offset(caja.left, yf - 1.3f * u), Size(caja.width, 1.4f * u))
        drawRect(t.c(ORO), Offset(caja.left, yf + h * 0.05f), Size(caja.width, 1.4f * u))
        drawRect(t.c(ORO), Offset(caja.left, piso - h * 0.028f), Size(caja.width, 1.4f * u))
        val med = Offset(caja.center.x, caja.top + h * 0.15f)
        drawOval(t.c(ORO), Offset(med.x - w * 0.1f, med.y - h * 0.048f), Size(w * 0.2f, h * 0.096f))
        drawOval(t.c(oscura), Offset(med.x - w * 0.085f, med.y - h * 0.036f), Size(w * 0.17f, h * 0.072f))
        drawCircle(t.c(CREMA), h * 0.02f, med)
        listOf(-1f, 1f).forEach { s ->
            val q = Offset(med.x + s * w * 0.27f, med.y)
            drawPath(poligono(q.x - w * 0.035f, q.y, q.x, q.y - h * 0.03f, q.x + w * 0.035f, q.y, q.x, q.y + h * 0.03f), t.c(ORO))
            drawPath(poligono(q.x - w * 0.02f, q.y, q.x, q.y - h * 0.017f, q.x + w * 0.02f, q.y, q.x, q.y + h * 0.017f), t.c(lata))
        }
        // La luz del cuerpo, la abolladura y el óxido.
        if (der) { drawRect(t.b(0.2f), Offset(caja.right - caja.width * 0.14f, caja.top), Size(caja.width * 0.14f, caja.height)); drawRect(t.s(0.18f), caja.topLeft, Size(caja.width * 0.1f, caja.height)) }
        else { drawRect(t.b(0.18f), caja.topLeft, Size(caja.width * 0.13f, caja.height)); drawRect(t.s(0.22f), Offset(caja.right - caja.width * 0.12f, caja.top), Size(caja.width * 0.12f, caja.height)) }
        val abo = Offset(caja.left + caja.width * (0.12f + 0.1f * azar(c.semilla, 5)), caja.top + h * 0.15f)
        drawOval(t.c(ZINC), Offset(abo.x - w * 0.022f, abo.y - h * 0.014f), Size(w * 0.044f, h * 0.028f))
        drawOval(t.s(0.22f), Offset(abo.x - w * 0.008f, abo.y - h * 0.008f), Size(w * 0.03f, h * 0.02f))
        drawOval(t.c(OXIDO).copy(alpha = 0.55f), Offset(caja.right - w * 0.1f, caja.top + h * 0.1f), Size(w * 0.045f, h * 0.03f))
        // El frente de la tapa.
        drawRect(t.c(oscura), tapa.topLeft, tapa.size)
        drawRect(t.b(0.5f), tapa.topLeft, Size(tapa.width, 1.2f * u))
        if (der) drawRect(t.b(0.18f), Offset(tapa.right - tapa.width * 0.13f, tapa.top), Size(tapa.width * 0.13f, tapa.height))
        else { drawRect(t.b(0.16f), tapa.topLeft, Size(tapa.width * 0.13f, tapa.height)); drawRect(t.s(0.2f), Offset(tapa.right - tapa.width * 0.12f, tapa.top), Size(tapa.width * 0.12f, tapa.height)) }
        drawRect(t.c(ORO), Offset(tapa.left, tapa.bottom - 1.6f * u), Size(tapa.width, 1.6f * u))
        drawRect(t.s(0.32f), Offset(caja.left, tapa.bottom), Size(caja.width, 2.5f * u))
        filo(Rect(tapa.left, tapa.top, tapa.right, piso), t, der)
        esmalte(w, h, t)
    }

    /**
     * El fajo de revistas atado con una goma: la de delante de frente, con su título que no dice nada y su portada, y las
     * demás detrás, descuadradas, asomando por arriba (más cuantas más guarda); la goma roja las cruza, y la placa del tema
     * va sujeta debajo de ella.
     */
    private fun DrawScope.revistas(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val colores = listOf(LATA, AZUL, OCRE, ESTUCO, VERDE_LUZ, mezcla(ROSA, LATA, 0.25f), mezcla(AZUL, VERDE, 0.5f))
        val detras = (c.n + 1).coerceIn(2, 7)
        val paso = minOf(0.036f, 0.18f / detras) * h
        val frente = Rect(w * 0.03f, h * 0.225f, w * 0.94f, piso)
        pisa(t, w * 0.53f, piso, w * 0.92f, h * 0.05f)
        for (i in detras downTo 1) {
            val col = colores[Math.floorMod(m + i * 3, 7)]
            val dx = (azar(c.semilla, i) - 0.35f) * w * 0.045f + i * w * 0.003f
            val r = Rect(frente.left + dx, frente.top - i * paso, frente.right + dx, piso + h * 0.1f)
            clipRect(0f, -h, w, piso) {
                rotate((azar(c.semilla, i + 20) - 0.5f) * 3.5f, Offset(r.center.x, piso)) {
                    drawRect(t.s(0.25f), Offset(r.left + w * 0.014f, r.top + h * 0.012f), r.size)
                    drawRect(t.c(col), r.topLeft, r.size)
                    drawRect(t.c(mezcla(PAPEL, col, 0.3f)), r.topLeft, Size(r.width, 1.4f * u))
                    drawRect(t.c(CREMA).copy(alpha = 0.75f), Offset(r.left + r.width * 0.07f, r.top + paso * 0.3f), Size(r.width * (0.25f + 0.3f * azar(c.semilla, i + 40)), paso * 0.4f))
                    drawRect(t.s(0.14f), Offset(r.right - r.width * 0.1f, r.top), Size(r.width * 0.1f, r.height))
                }
            }
        }
        // La de delante: el título, la portada y el precio.
        val col0 = colores[Math.floorMod(m ushr 3, 7)]
        val claro = mezcla(col0, CREMA, 0.55f); val oscuro = mezcla(col0, TINTA, 0.38f)
        drawRect(t.s(0.3f), Offset(frente.left + w * 0.014f, frente.top + h * 0.012f), frente.size)
        drawRect(t.c(col0), frente.topLeft, frente.size)
        clipRect(frente.left, frente.top, frente.right, frente.bottom) {
            when (Math.floorMod(m ushr 6, 3)) {
                0 -> {
                    drawCircle(t.c(claro), h * 0.13f, Offset(frente.right - w * 0.22f, frente.top + h * 0.26f))
                    drawPath(poligono(frente.left, h * 0.64f, frente.left + w * 0.28f, frente.top + h * 0.17f, frente.left + w * 0.6f, h * 0.64f), t.c(oscuro))
                    drawPath(poligono(frente.left + w * 0.3f, h * 0.64f, frente.left + w * 0.5f, frente.top + h * 0.24f, frente.left + w * 0.78f, h * 0.64f), t.c(mezcla(oscuro, col0, 0.5f)))
                }
                1 -> {
                    drawRect(t.c(claro), Offset(frente.left + w * 0.07f, frente.top + h * 0.15f), Size(w * 0.36f, h * 0.3f))
                    drawCircle(t.c(oscuro), h * 0.075f, Offset(frente.left + w * 0.25f, frente.top + h * 0.27f))
                    drawRect(t.c(oscuro), Offset(frente.left + w * 0.13f, frente.top + h * 0.32f), Size(w * 0.24f, h * 0.13f))
                    for (k in 0 until 3) drawRect(t.c(CREMA).copy(alpha = 0.85f), Offset(frente.left + w * 0.5f, frente.top + h * (0.17f + 0.055f * k)), Size(w * (0.28f - 0.06f * k), h * 0.025f))
                }
                else -> {
                    drawPath(poligono(frente.left, frente.top + h * 0.3f, frente.right, frente.top + h * 0.13f, frente.right, frente.top + h * 0.27f, frente.left, frente.top + h * 0.44f), t.c(claro))
                    drawCircle(t.c(oscuro), h * 0.065f, Offset(frente.left + w * 0.62f, frente.top + h * 0.27f))
                    drawCircle(t.c(claro), h * 0.03f, Offset(frente.left + w * 0.62f, frente.top + h * 0.27f))
                }
            }
            renglonFalso(frente.left + frente.width * 0.33f, frente.top + h * 0.03f, h * 0.08f, w * 0.075f, List(6) { Math.floorMod(it * 3 + m, 8) }, t.c(CREMA))
            drawRect(t.c(CREMA), Offset(frente.right - w * 0.25f, frente.top + h * 0.035f), Size(w * 0.1f, h * 0.05f))
            if (der) { drawRect(t.b(0.2f), Offset(frente.right - frente.width * 0.16f, frente.top), Size(frente.width * 0.16f, frente.height)); drawRect(t.s(0.14f), frente.topLeft, Size(frente.width * 0.12f, frente.height)) }
            else { drawRect(t.b(0.16f), frente.topLeft, Size(frente.width * 0.16f, frente.height)); drawRect(t.s(0.2f), Offset(frente.right - frente.width * 0.14f, frente.top), Size(frente.width * 0.14f, frente.height)) }
            // El lomo grapado, a la izquierda.
            drawRect(t.s(0.2f), frente.topLeft, Size(w * 0.012f, frente.height))
            listOf(0.1f, 0.4f).forEach { f -> drawRect(t.c(ZINC), Offset(frente.left + w * 0.003f, frente.top + h * f), Size(w * 0.006f, h * 0.03f)) }
        }
        drawRect(t.b(0.4f), frente.topLeft, Size(frente.width, 1f * u))
        // La esquina de la portada, que se levanta: se ve la página de dentro.
        val eq = Offset(frente.right, frente.top)
        drawPath(poligono(eq.x - w * 0.075f, eq.y, eq.x, eq.y, eq.x, eq.y + h * 0.1f), t.c(mezcla(PAPEL, CREMA, 0.4f)))
        for (k in 0 until 2) drawLine(t.c(TINTA).copy(alpha = 0.3f), Offset(eq.x - w * (0.04f - 0.015f * k), eq.y + h * (0.018f + 0.02f * k)), Offset(eq.x - w * 0.006f, eq.y + h * (0.018f + 0.02f * k)), 0.8f * u)
        val vuelta = poligono(eq.x - w * 0.075f, eq.y, eq.x, eq.y + h * 0.1f, eq.x - w * 0.06f, eq.y + h * 0.085f)
        translate(-1.5f * u, 2f * u) { drawPath(vuelta, t.s(0.3f)) }
        drawPath(vuelta, t.c(mezcla(col0, CREMA, 0.45f)))
        // La goma: una cinta elástica roja que cruza el fajo y lo aprieta un poco.
        val gy = h * (0.475f + 0.025f * azar(c.semilla, 30))
        val x0 = frente.left - w * 0.012f; val x1 = frente.right + w * 0.02f
        drawRect(t.s(0.3f), Offset(x0, gy + h * 0.03f), Size(x1 - x0, 2f * u))
        drawRect(t.c(mezcla(LATA, OXIDO, 0.35f)), Offset(x0, gy), Size(x1 - x0, h * 0.03f))
        drawRect(t.b(0.4f), Offset(x0, gy), Size(x1 - x0, 1f * u))
        drawRect(t.s(0.25f), Offset(x1 - w * 0.03f, gy), Size(w * 0.03f, h * 0.03f))
        filo(Rect(frente.left, frente.top, frente.right, piso), t, der)
        esmalte(w, h, t)
    }

    /**
     * La caja de cartón de postales (o de cromos), de kraft, azul gris, granate o verde pálido: la caja con su franja impresa y
     * la esquina gastada, la tapa apoyada detrás, de canto, y las postales de pie que asoman con una ficha separadora, más
     * cuantas más guarda; la placa del tema, en el frente.
     */
    private fun DrawScope.cajaPostales(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val clase = Math.floorMod(m ushr 3, 4)
        val carton = when (clase) { 0 -> KRAFT; 1 -> mezcla(AZUL, PAPEL, 0.3f); 2 -> mezcla(LATA, KRAFT, 0.42f); else -> mezcla(VERDE_BOLETO, KRAFT, 0.3f) }
        val franja = when (clase) { 0 -> LATA; 1 -> mezcla(AZUL, TINTA, 0.45f); 2 -> OCRE; else -> VERDE }
        val caja = Rect(w * 0.03f, h * 0.425f, w * 0.97f, piso)
        // La tapa apoyada contra el fondo, ladeada: se le ve el lado de dentro, con su reborde.
        val izq = Math.floorMod(m ushr 5, 2) == 0
        val tp = if (izq) Rect(w * 0.07f, h * 0.1f, w * 0.52f, piso) else Rect(w * 0.48f, h * 0.1f, w * 0.93f, piso)
        clipRect(0f, -h, w, piso) {
            rotate(if (izq) -7f else 7f, Offset(tp.center.x, piso)) {
                drawRect(t.s(0.3f), Offset(tp.left + w * 0.02f, tp.top + h * 0.02f), tp.size)
                drawRect(t.c(mezcla(carton, TINTA, 0.12f)), tp.topLeft, Size(tp.width, tp.height + h * 0.1f))
                drawRect(t.c(mezcla(carton, CREMA, 0.35f)), Offset(tp.left + w * 0.02f, tp.top + h * 0.03f), Size(tp.width - w * 0.04f, tp.height + h * 0.1f))
                drawRect(t.s(0.22f), Offset(tp.left + w * 0.02f, tp.top + h * 0.03f), Size(tp.width - w * 0.04f, h * 0.025f))
                drawRect(t.b(0.4f), tp.topLeft, Size(tp.width, 1.2f * u))
            }
        }
        drawRect(t.s(0.3f), Offset(caja.left + w * 0.03f, caja.top + h * 0.02f), caja.size)
        pisa(t, caja.center.x + w * 0.02f, piso, caja.width * 1.04f, h * 0.05f)
        // El hueco oscuro de la caja y las postales de pie.
        drawRect(t.c(mezcla(carton, TINTA, 0.5f)), Offset(caja.left + w * 0.015f, caja.top - h * 0.02f), Size(caja.width - w * 0.03f, h * 0.04f))
        val cuantas = (3 + c.n / 2).coerceIn(3, 7)
        val colores = listOf(AZUL, ESTUCO, VERDE_LUZ, ROSA, OCRE, mezcla(AZUL, CREMA, 0.3f), LATA)
        val separador = cuantas / 2
        for (i in 0 until cuantas) {
            val f = (i + 0.5f) / cuantas
            val pw = w * 0.21f
            val x = caja.left + w * 0.03f + (caja.width - w * 0.06f - pw) * f
            val alto = h * (0.15f + 0.014f * c.n.coerceAtMost(8) + 0.05f * azar(c.semilla, i))
            val g = (azar(c.semilla, i + 40) - 0.5f) * 9f
            rotate(g, Offset(x + pw / 2, caja.top)) {
                val r = Rect(x, caja.top - alto, x + pw, caja.top + h * 0.1f)
                drawRect(t.s(0.24f), Offset(r.left + 1.6f * u, r.top + 1.6f * u), r.size)
                if (i == separador) {
                    // La ficha separadora, de cartulina, con su pestaña.
                    drawRect(t.c(mezcla(KRAFT, CREMA, 0.45f)), Offset(r.left, r.top + h * 0.02f), Size(r.width, r.height))
                    drawRect(t.c(mezcla(KRAFT, CREMA, 0.45f)), Offset(r.left + pw * 0.5f, r.top - h * 0.02f), Size(pw * 0.4f, h * 0.05f))
                    drawRect(t.c(TINTA).copy(alpha = 0.4f), Offset(r.left + pw * 0.56f, r.top), Size(pw * 0.28f, 1f * u))
                } else {
                    drawRect(t.c(CREMA), r.topLeft, r.size)
                    val col = colores[Math.floorMod(i + c.semilla, 7)]
                    val im = Rect(r.left + w * 0.012f, r.top + h * 0.015f, r.right - w * 0.012f, r.top + h * 0.015f + alto * 0.8f)
                    drawRect(t.c(mezcla(col, CREMA, 0.25f)), im.topLeft, im.size)
                    drawRect(t.c(mezcla(col, TINTA, 0.3f)), Offset(im.left, im.top + im.height * 0.6f), Size(im.width, im.height * 0.4f))
                    if (i % 2 == 0) drawArc(t.c(mezcla(col, TINTA, 0.15f)), 180f, 180f, true, Offset(im.left + im.width * 0.25f, im.top + im.height * 0.34f), Size(im.width * 0.4f, im.height * 0.56f))
                    else drawCircle(t.c(mezcla(col, CREMA, 0.65f)), im.width * 0.13f, Offset(im.left + im.width * 0.68f, im.top + im.height * 0.3f))
                    drawRect(t.b(0.4f), r.topLeft, Size(r.width, 0.9f * u))
                }
                drawRect(t.s(0.15f), Offset(r.right - r.width * 0.2f, r.top), Size(r.width * 0.2f, r.height))
            }
        }
        // El frente de la caja.
        drawRect(t.c(carton), caja.topLeft, caja.size)
        drawRect(t.c(franja), Offset(caja.left, caja.top + h * 0.045f), Size(caja.width, h * 0.04f))
        drawRect(t.c(mezcla(franja, CREMA, 0.6f)), Offset(caja.left, caja.top + h * 0.095f), Size(caja.width, h * 0.012f))
        pinceladas(caja, if (t.o) NOCHE else mezcla(carton, TINTA, 0.3f), c.semilla, 6, u, 0.12f)
        drawRect(t.b(0.45f), caja.topLeft, Size(caja.width, 1.2f * u))
        drawRect(t.s(0.3f), Offset(caja.left, caja.top + 1.2f * u), Size(caja.width, 1.5f * u))
        if (der) { drawRect(t.b(0.16f), Offset(caja.right - caja.width * 0.13f, caja.top), Size(caja.width * 0.13f, caja.height)); drawRect(t.s(0.16f), caja.topLeft, Size(caja.width * 0.1f, caja.height)) }
        else { drawRect(t.b(0.14f), caja.topLeft, Size(caja.width * 0.12f, caja.height)); drawRect(t.s(0.22f), Offset(caja.right - caja.width * 0.12f, caja.top), Size(caja.width * 0.12f, caja.height)) }
        // La esquina gastada, arriba a la derecha, y la cinta de papel que refuerza la otra.
        drawPath(poligono(caja.right - w * 0.06f, caja.top, caja.right, caja.top, caja.right, caja.top + h * 0.07f), t.c(mezcla(carton, CREMA, 0.45f)))
        drawRect(t.c(mezcla(KRAFT, CREMA, 0.3f)).copy(alpha = 0.8f), Offset(caja.left, caja.top), Size(w * 0.03f, caja.height))
        filo(caja, t, der)
        esmalte(w, h, t)
    }

    /**
     * Lo que no tiene tema: una caja de zapatos gris sin tapa (la tapa, apoyada de canto a la derecha) y los boletos sueltos
     * que asoman en desorden; su rótulo, en un cartón atado con cordel.
     */
    private fun DrawScope.zapatos(w: Float, h: Float, c: EstadoCajon, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val caja = Rect(w * 0.04f, h * 0.4f, w * 0.88f, piso)
        val gris = mezcla(TRAVERTINO, ZINC, 0.4f)
        // La tapa apoyada contra el costado derecho.
        clipRect(0f, -h, w, piso) {
            rotate(-9f, Offset(w * 0.95f, piso)) {
                val tp = Rect(w * 0.8f, h * 0.16f, w * 0.96f, piso + h * 0.1f)
                drawRect(t.s(0.3f), Offset(tp.left + w * 0.02f, tp.top + h * 0.02f), tp.size)
                drawRect(t.c(gris), tp.topLeft, tp.size)
                drawRect(t.c(mezcla(gris, CREMA, 0.4f)), Offset(tp.left + w * 0.012f, tp.top + h * 0.02f), Size(tp.width * 0.55f, tp.height))
                drawRect(t.s(0.25f), Offset(tp.right - tp.width * 0.3f, tp.top), Size(tp.width * 0.3f, tp.height))
                drawRect(t.c(LATA), Offset(tp.left, tp.top + tp.height * 0.2f), Size(tp.width, h * 0.03f))
            }
        }
        drawRect(t.s(0.3f), Offset(caja.left + w * 0.03f, caja.top + h * 0.02f), caja.size)
        pisa(t, caja.center.x + w * 0.03f, piso, caja.width * 1.05f, h * 0.05f)
        drawRect(t.c(mezcla(gris, TINTA, 0.5f)), Offset(caja.left + w * 0.015f, caja.top - h * 0.02f), Size(caja.width - w * 0.03f, h * 0.04f))
        // Los boletos en desorden.
        val k = (c.n + 4).coerceIn(5, 12)
        val colores = listOf(ROSA, VERDE_BOLETO, OCRE, CREMA, mezcla(AZUL, CREMA, 0.5f))
        for (i in 0 until k) {
            val x = caja.left + caja.width * (0.06f + 0.8f * azar(c.semilla, i))
            val g = (azar(c.semilla, i + 20) - 0.5f) * 70f
            rotate(g, Offset(x, caja.top)) {
                val bw = w * 0.085f; val bh = h * (0.13f + 0.1f * azar(c.semilla, i + 40))
                val col = colores[i % 5]
                drawRect(t.s(0.22f), Offset(x + 1.4f * u, caja.top - bh + 1.4f * u), Size(bw, bh))
                drawRect(t.c(col), Offset(x, caja.top - bh), Size(bw, bh + h * 0.05f))
                drawRect(t.c(mezcla(col, TINTA, 0.45f)), Offset(x, caja.top - bh + bh * 0.12f), Size(bw, bh * 0.1f))
                drawRect(t.c(TINTA).copy(alpha = 0.3f), Offset(x + bw * 0.15f, caja.top - bh + bh * 0.36f), Size(bw * 0.7f, 0.8f * u))
            }
        }
        drawRect(t.c(gris), caja.topLeft, caja.size)
        drawRect(t.c(mezcla(gris, TINTA, 0.15f)), Offset(caja.left, caja.top), Size(caja.width, h * 0.04f))
        drawRect(t.c(LATA), Offset(caja.left, caja.top + h * 0.075f), Size(caja.width, h * 0.02f))
        drawRect(t.b(0.45f), caja.topLeft, Size(caja.width, 1.2f * u))
        if (der) { drawRect(t.b(0.16f), Offset(caja.right - caja.width * 0.13f, caja.top), Size(caja.width * 0.13f, caja.height)); drawRect(t.s(0.16f), caja.topLeft, Size(caja.width * 0.1f, caja.height)) }
        else { drawRect(t.b(0.14f), caja.topLeft, Size(caja.width * 0.12f, caja.height)); drawRect(t.s(0.22f), Offset(caja.right - caja.width * 0.12f, caja.top), Size(caja.width * 0.12f, caja.height)) }
        filo(caja, t, der)
        carton(w, h, t)
    }

    /** Un hueco libre: un diario doblado con unas monedas encima, o un mechero y una caja de cerillas, según la semilla. */
    private fun DrawScope.vacio(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val piso = h * PISO
        if (Math.floorMod(revolver(c.semilla), 2) == 0) {
            // El diario doblado, tumbado en la repisa: el canto de sus hojas y la cara de arriba en escorzo.
            val fr = Rect(w * 0.08f, piso - h * 0.065f, w * 0.62f, piso)
            pisa(t, fr.center.x + w * 0.04f, piso, fr.width * 1.15f, h * 0.045f)
            val arriba = poligono(fr.left, fr.top, fr.left + w * 0.05f, fr.top - h * 0.1f, fr.right + w * 0.03f, fr.top - h * 0.1f, fr.right, fr.top)
            drawPath(arriba, t.c(mezcla(PAPEL, CREMA, 0.4f)))
            clipPath(arriba) {
                drawRect(t.c(TINTA).copy(alpha = 0.75f), Offset(fr.left + w * 0.09f, fr.top - h * 0.085f), Size(w * 0.26f, h * 0.022f))
                for (k in 0 until 3) drawRect(t.c(TINTA).copy(alpha = 0.3f), Offset(fr.left + w * 0.06f, fr.top - h * (0.05f - 0.016f * k)), Size(w * 0.42f, 0.8f * u))
                drawRect(t.s(0.16f), Offset(fr.right - w * 0.08f, fr.top - h * 0.1f), Size(w * 0.12f, h * 0.1f))
            }
            drawRect(t.c(PAPEL), fr.topLeft, fr.size)
            for (k in 0 until 4) drawRect(t.c(mezcla(PAPEL, KRAFT, 0.45f)), Offset(fr.left, fr.top + fr.height * (0.2f + 0.2f * k)), Size(fr.width, 0.9f * u))
            drawRect(t.b(0.4f), fr.topLeft, Size(fr.width, 1f * u))
            drawRect(t.s(0.2f), Offset(fr.right - fr.width * 0.12f, fr.top), Size(fr.width * 0.12f, fr.height))
            // Las monedas: tres apiladas sobre el diario y otras sueltas en la repisa.
            val rw = w * 0.095f; val rh = h * 0.048f
            val pila = Offset(w * 0.4f, fr.top - h * 0.045f)
            for (i in 0 until 3) {
                val p = pila + Offset((i % 2) * w * 0.006f, -i * h * 0.022f)
                if (i == 0) drawOval(t.s(0.3f), Offset(p.x - rw / 2 + w * 0.012f, p.y - rh / 2 + h * 0.022f), Size(rw, rh))
                drawOval(t.c(mezcla(BRONCE, TINTA, 0.35f)), Offset(p.x - rw / 2, p.y - rh / 2 + h * 0.02f), Size(rw, rh))
                drawRect(t.c(mezcla(BRONCE, TINTA, 0.35f)), Offset(p.x - rw / 2, p.y), Size(rw, h * 0.02f))
                drawOval(t.c(BRONCE), Offset(p.x - rw / 2, p.y - rh / 2), Size(rw, rh))
                drawOval(t.c(mezcla(BRONCE, TINTA, 0.25f)), Offset(p.x - rw * 0.32f, p.y - rh * 0.32f), Size(rw * 0.64f, rh * 0.64f), style = Stroke(0.8f * u))
                drawOval(t.b(0.5f), Offset(p.x - rw * 0.36f, p.y - rh * 0.4f), Size(rw * 0.26f, rh * 0.26f))
            }
            listOf(Offset(0.7f, 0.0f), Offset(0.8f, -0.022f), Offset(0.9f, 0.008f)).forEachIndexed { i, f ->
                val p = Offset(w * f.x, piso + h * f.y - h * 0.012f)
                val col = if (i == 2) ZINC else if (i == 1) ORO else BRONCE
                drawOval(t.s(0.3f), Offset(p.x - rw / 2 + 1.5f * u, p.y - rh / 2 + 2f * u), Size(rw, rh))
                drawOval(t.c(mezcla(col, TINTA, 0.3f)), Offset(p.x - rw / 2, p.y - rh / 2 + 1.4f * u), Size(rw, rh))
                drawOval(t.c(col), Offset(p.x - rw / 2, p.y - rh / 2), Size(rw, rh))
                drawOval(t.c(mezcla(col, TINTA, 0.22f)), Offset(p.x - rw * 0.3f, p.y - rh * 0.3f), Size(rw * 0.6f, rh * 0.6f), style = Stroke(0.8f * u))
                drawOval(t.b(0.5f), Offset(p.x - rw * 0.36f, p.y - rh * 0.4f), Size(rw * 0.26f, rh * 0.26f))
            }
        } else {
            // La caja de cerillas, entreabierta, y el mechero tumbado delante.
            val cj = Rect(w * 0.2f, piso - h * 0.155f, w * 0.52f, piso)
            pisa(t, cj.center.x + w * 0.03f, piso, cj.width * 1.6f, h * 0.04f)
            drawRect(t.c(mezcla(KRAFT, CREMA, 0.5f)), Offset(cj.left - w * 0.05f, cj.top + h * 0.012f), Size(w * 0.08f, cj.height - h * 0.024f))
            for (i in 0 until 3) drawRect(t.c(LATA), Offset(cj.left - w * 0.045f, cj.top + h * (0.025f + 0.025f * i)), Size(w * 0.012f, h * 0.014f))
            drawRect(t.c(AZUL), cj.topLeft, cj.size)
            drawRect(t.c(OCRE), Offset(cj.left + cj.width * 0.15f, cj.top + cj.height * 0.2f), Size(cj.width * 0.7f, cj.height * 0.6f))
            drawCircle(t.c(LATA), cj.height * 0.2f, cj.center)
            drawRect(t.b(0.4f), cj.topLeft, Size(cj.width, 1.2f * u))
            drawRect(t.s(0.25f), Offset(cj.right - cj.width * 0.15f, cj.top), Size(cj.width * 0.15f, cj.height))
            val r = Rect(w * 0.57f, piso - h * 0.115f, w * 0.88f, piso)
            pisa(t, r.center.x + w * 0.02f, piso, r.width * 1.1f, h * 0.04f)
            drawRoundRect(t.c(if (Math.floorMod(c.semilla, 3) == 0) LATA else VERDE_LUZ), r.topLeft, Size(r.width * 0.78f, r.height), CornerRadius(h * 0.02f))
            drawRect(t.c(ZINC), Offset(r.left + r.width * 0.72f, r.top), Size(r.width * 0.28f, r.height))
            drawRect(t.s(0.3f), Offset(r.left + r.width * 0.84f, r.top), Size(1.2f * u, r.height))
            drawRect(t.b(0.45f), r.topLeft, Size(r.width, 1.2f * u))
            drawRect(t.s(0.25f), Offset(r.left, r.bottom - h * 0.025f), Size(r.width, h * 0.025f))
        }
    }

    override val rotulo = Rect(0.048f, 0.555f, 0.952f, 0.875f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = Letra.placa
    override val salida = -SALIDA
    override val placa = Rect(0.3f, 0.115f, 0.7f, 0.305f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Lata litografiada: rojo con una retícula de puntos crema, y los bordes enrollados.
        drawRect(t(LATA_OSC, oscuro))
        val p = 18 * u
        var y = p / 2; var fila = 0
        while (y < h) {
            var x = if (fila % 2 == 0) p / 2 else p
            while (x < w) { drawCircle(CREMA.copy(alpha = 0.14f), 2 * u, Offset(x, y)); x += p }
            y += p / 2; fila++
        }
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(LATA, oscuro), r.topLeft, r.size) }
        drawRect(t(ORO, oscuro), Offset(pl, pl), Size(w - 2 * pl, h - 2 * pl), style = Stroke(1.4f * u))
        drawRect(t(CREMA, oscuro).copy(alpha = 0.5f), Offset(pl * 0.4f, pl * 0.4f), Size(w - pl * 0.8f, h - pl * 0.8f), style = Stroke(1.2f * u))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = CREMA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 4

    /** El boleto: un rectángulo con dos muescas a media altura. */
    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        val r = minOf(s.width, s.height) * 0.06f
        op(Path().apply { addRect(Rect(0f, 0f, s.width, s.height)) },
            Path().apply { addOval(Rect(-r, s.height / 2 - r, r, s.height / 2 + r)); addOval(Rect(s.width - r, s.height / 2 - r, s.width + r, s.height / 2 + r)) },
            PathOperation.Difference)
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        val tono = listOf(ROSA, VERDE_BOLETO, OCRE)[semilla.mod(3)]
        drawRect(tono.copy(alpha = 0.35f))
        // La línea de puntos del corte, arriba.
        var x = 4 * u
        while (x < w) { drawCircle(TINTA.copy(alpha = 0.3f), 0.8f * u * g, Offset(x, 3 * u * g)); x += 4 * u * g }
        // Las casillas de las paradas a los dos lados, y una perforada.
        val cs = 5 * u * g
        val n = ((h - 16 * u * g) / (cs * 1.5f)).toInt().coerceAtLeast(2)
        val perforada = semilla.mod(n)
        for (k in 0 until n) {
            val y = 10 * u * g + k * cs * 1.5f
            listOf(2 * u * g, w - 2 * u * g - cs).forEachIndexed { lado, xx ->
                drawRect(TINTA.copy(alpha = 0.35f), Offset(xx, y), Size(cs, cs), style = Stroke(0.6f * u))
                if (lado == 1 && k == perforada) drawCircle(Color.Black.copy(alpha = 0.55f), cs * 0.32f, Offset(xx + cs / 2, y + cs / 2))
            }
        }
    }

    override val margenes = MargenesFicha(13.dp, 15.dp, 12.dp, 24.dp, 26.dp, 24.dp)
    override val proporcionFicha = 1.15f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.78f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El timbro de la oficina.
            "hecho" -> selloHecho(VERDE, p.color, cuadrado = true)
            // La perforación del revisor.
            "convence" -> { drawCircle(TINTA, s * 0.3f, c); drawCircle(TINTA, s * 0.42f, c, style = Stroke(s * 0.06f)) }
            // El reloj de la estación.
            "piensa" -> {
                drawCircle(VERDE, s * 0.4f, c, style = Stroke(s * 0.09f))
                drawLine(VERDE, c, Offset(c.x, c.y - s * 0.26f), s * 0.08f, StrokeCap.Round)
                drawLine(VERDE, c, Offset(c.x + s * 0.18f, c.y + s * 0.06f), s * 0.08f, StrokeCap.Round)
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(LATA)
            else -> {}
        }
    }

    /** Un gettone: la ficha de bronce con su ranura. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawCircle(BRONCE, s * 0.44f, c)
        drawCircle(mezcla(BRONCE, Color.Black, 0.3f), s * 0.36f, c, style = Stroke(s * 0.05f))
        drawLine(mezcla(BRONCE, Color.Black, 0.45f), Offset(c.x - s * 0.2f, c.y), Offset(c.x + s * 0.2f, c.y), s * 0.1f, StrokeCap.Round)
    }

    override val desorden = 2f
}
