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
import androidx.compose.ui.graphics.drawscope.withTransform
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
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Edad Media: el armario del scriptorium
// =====================================================================

/**
 * Medieval: el *armarium* del scriptorium de una abadía benedictina hacia 1150, junto al pupitre del hermano Odo. Está contra
 * un muro románico de sillarejo enlucido y encalado (las hiladas se adivinan bajo la cal), con un arco ciego de medio punto
 * en relieve a la derecha (dovelas alternas de cal y almagre, y una cruz sencilla pintada en el tímpano), una imposta corrida
 * con un ajedrezado pintado, el enlucido con sus faltas, por donde asoman las piedras, y el hollín que la vela ha ido dejando
 * encima de donde arde. La luz de día baja de arriba a la izquierda, de algo que queda fuera del cuadro. El mueble es de
 * roble: dos calles de anaqueles de medio punto, cada uno con su arquivolta fileteada de almagre sobre dos columnillas de
 * capitel cúbico y con rosetas hexapétalas talladas en las enjutas; un friso con su arquería pintada de almagre y azurita, ya
 * gastada; un zócalo con dientes de sierra, y las dos puertas abiertas del todo a los lados, con sus herrajes de hierro en
 * volutas.
 *
 * Encima, a la derecha y dentro del arco, el atril con el códice grande abierto: la página que mira a la vela lleva una
 * inicial a medio pintar (media dorada y con color, media solo con el dibujo a pluma y el bol) y la otra está a medio
 * escribir sobre el pautado, sujeta por un peso de plomo. A su lado, la vela en su palmatoria; a la izquierda, los cuernos de
 * tinta negra y roja en su soporte, con dos plumas y el cortaplumas; el cuenco del pan de oro con su bruñidor, tres conchas
 * con bermellón, azurita y verdigrís, la piedra pómez, una tabla de cera apoyada en la pared con su punzón y la regla, que
 * sobresale del canto. En el muro, el bastidor con una piel tensada secándose y un manojo de plumas colgado de un clavo. Al
 * pie, sobre las baldosas de barro, un cesto de mimbre con recortes de pergamino, cortado por el borde, y el escabel.
 *
 * Cada tema es un anaquel, de una de cuatro clases según el tema: la pila de folios sueltos en su cartapacio de cuero, con su
 * piedra de peso; los códices encuadernados en tabla, tumbados, con correas y bullones; los rollos de pergamino, tumbados y
 * atados con su cordel (alguno en su funda de lienzo, otro con el sello colgando) y uno o dos de pie al fondo; o las
 * tablillas de cera, de pie y en abanico, atadas con una tira. Los cueros, las ceras y los cordeles cambian de color con el
 * tema, y cuantas más ideas, más lleno. El tema va en una etiqueta de pergamino clavada en la tabla del anaquel. Lo que no
 * tiene tema es una caja de madera clara con su cerradura y folios revueltos; un hueco sin tema guarda un cabo de vela o una
 * pluma con su tintero, y en la tabla queda la punta de una etiqueta que se arrancó.
 *
 * De noche solo arde la vela: su halo sube por el encalado, enciende el oro de la inicial y la página, tiende las sombras
 * hacia fuera de la llama, y su luz baja por el mueble de fila en fila hasta perderse en el azul violeta de la noche. Por
 * dentro, tablas de roble a la luz de una vela. Las fichas son folios de pergamino pautados en rojo, con los picados del
 * margen.
 */
object IdeasMedieval : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del scriptorium
    /** El roble del armario: su luz y lo hondo de la talla (también del interior). */
    private val ROBLE = Color(0xFF6E4C2C)
    private val ROBLE_LUZ = Color(0xFF94704A)
    private val ROBLE_OSC = Color(0xFF3B2715)
    /** El hierro de herrajes y clavos. */
    private val HIERRO = Color(0xFF26231F)
    private val HIERRO_LUZ = Color(0xFF6A635A)
    /** El pergamino de folios, rollos y etiquetas, y su cara más amarilla. */
    private val PERGAMINO = Color(0xFFF1E6C8)
    private val PERG_OSC = Color(0xFFD6C397)
    /**
     * La etiqueta de noche lejos de la vela: con [TINTA] da unos 9:1 en el título y algo más de 5:1 en la cuenta (al 78 %),
     * medidos en la imagen. Cerca de la vela va hacia el pergamino tibio.
     */
    private val PERG_NOCHE = Color(0xFFC6B28C)
    /** El bermellón y el lapislázuli de las fichas (los de siempre) y el oro. */
    private val BERMELLON = Color(0xFF9E1B1B)
    private val LAPIS = Color(0xFF1F3F8F)
    private val ORO = Color(0xFFC09A3A)
    private val TINTA = Color(0xFF2A1B10)
    private val VELA = Color(0xFFF2C46B)
    /** La cal del muro y su sombra; la piedra del sillarejo; el barro de las baldosas. */
    private val CAL = Color(0xFFE9DEC6)
    private val CAL_OSC = Color(0xFFC9B99A)
    private val PIEDRA = Color(0xFF9C8C74)
    private val TERRACOTA = Color(0xFFB0674A)
    /** Los pigmentos apagados del muro y del friso: almagre, azurita y verdigrís (también los cueros y los cordeles). */
    private val ALMAGRE = Color(0xFFA8432F)
    private val AZURITA = Color(0xFF3F5C8C)
    private val VERDIGRIS = Color(0xFF5F7C5C)
    /** El cuero curtido claro (la badana). */
    private val BADANA = Color(0xFFA9794A)
    /** La noche del scriptorium: azul violeta hondo. Y la llama de la vela. */
    private val NOCHE = Color(0xFF13101C)
    private val LLAMA = Color(0xFFFFC56E)
    /** Variaciones de los de arriba: el cuerno de los tinteros y del mimbre, y la cera de la vela. */
    private val CUERNO = mezcla(BADANA, PERG_OSC, 0.55f)
    private val CERA = mezcla(PERGAMINO, VELA, 0.22f)
    /** Los cueros teñidos de códices y cartapacios, y los tintes de cordeles, cintas y fundas. */
    private val CUEROS = listOf(mezcla(BERMELLON, ROBLE_OSC, 0.3f), mezcla(VERDIGRIS, ROBLE_OSC, 0.25f), BADANA, mezcla(AZURITA, ROBLE_OSC, 0.3f), mezcla(ALMAGRE, BADANA, 0.3f))
    private val TINTES = listOf(ALMAGRE, AZURITA, VERDIGRIS)

    /** El tono de siempre para el interior. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false
    override val proporcion = 1.05f
    override val corona = 0.52f
    override val base = 0.26f
    override val lado = 0.11f
    override val hueco = 0.03f

    // ---------------------------------------------------------------- medidas (fracciones del ancho)
    /** El tablero de arriba: su cara, su canto; el friso pintado y la moldura de debajo. */
    private const val TAPA = 0.385f
    private const val TAPA_F = 0.4f
    private const val CANTO_B = 0.416f
    private const val FRISO_B = 0.49f
    private const val MOLD_B = 0.506f
    /** Los costados del cuerpo y los del tablero, que vuela un poco. */
    private const val BL = 0.085f
    private const val BR = 0.915f
    private const val TL = 0.07f
    private const val TR = 0.93f
    /** La imposta ajedrezada del muro. */
    private const val IMP_T = 0.262f
    private const val IMP_B = 0.288f
    /** El arco ciego: su centro, su radio de dentro y el de fuera. */
    private const val ARCO_X = 0.725f
    private const val ARCO_RI = 0.2f
    private const val ARCO_RO = 0.245f
    /**
     * En cada anaquel: dónde acaba el nicho (fracción del alto), el grosor de las jambas (fracción del ancho), la clave del arco
     * (fracción del alto) y el ancho de la arquivolta y de las columnillas (fracción del ancho).
     */
    private const val NICHO = 0.7f
    private const val JAMBA = 0.1f
    private const val CLAVE = 0.04f
    private const val ROSCA = 0.055f

    // ---------------------------------------------------------------- la luz y la noche

    /** La llama de la vela, en fracciones del ancho. */
    private val LUZ_VELA = Offset(0.5f, 0.262f)

    /**
     * La luz de la vela que baja por el cuerpo del armario, por filas (centros de las filas con 9 temas, en fracciones del
     * ancho): la primera, cálida; la segunda, a medias; la tercera, tenue; la cuarta y la quinta, casi perdidas.
     */
    private val CAIDA = listOf(0.5f to 0.86f, 0.73f to 0.74f, 1.12f to 0.46f, 1.51f to 0.22f, 1.9f to 0.08f, 2.29f to 0.045f, 2.8f to 0.035f)

    /** Cuánto le llega de la vela a un punto [p] (fracciones del ancho). */
    private fun luz(p: Offset): Float {
        val dx = p.x - LUZ_VELA.x
        val dy = (p.y - LUZ_VELA.y) * (if (p.y > LUZ_VELA.y) 0.8f else 1f)
        val radial = 1.4f * exp(-3.4f * sqrt(dx * dx + dy * dy))
        return maxOf(radial, luzCuerpo(p)).coerceIn(0.03f, 1f)
    }

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.6f * (abs(p.x - LUZ_VELA.x) - 0.1f).coerceAtLeast(0f)).coerceIn(0.72f, 1f)
        return k * lado
    }

    /** El color propio de algo, hundido en la noche: oscuro, de su mismo tono (no gris). */
    private fun hondo(c: Color) = Color(c.red * 0.13f + NOCHE.red * 0.8f, c.green * 0.13f + NOCHE.green * 0.8f, c.blue * 0.13f + NOCHE.blue * 0.8f, c.alpha)

    /**
     * Cómo se pinta algo según dónde está: de día tal cual (con [dia], un poco más claro si es más de 0 y un poco más apagado si
     * es menos); de noche, según [k], lo que le llega de la vela.
     */
    private class Tono(val o: Boolean, val k: Float, val dia: Float = 0f)

    /** De día, cuánto más claro (o más apagado) va algo según lo abajo que esté en el armario ([f], de 0 arriba a 1 abajo). */
    private fun diaSegunAltura(f: Float) = 0.1f - 0.18f * f.coerceIn(0f, 1f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else if (dia < 0f) mezcla(col, SOMBRA_MUEBLE, -dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.92f))
        return mezcla(v, LLAMA, 0.16f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, la noche del scriptorium. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la llama según lo cerca que esté. */
    private fun Tono.b(a: Float): Color = if (!o) luzMueble(a) else LLAMA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f))

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /**
     * Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las cuatro clases y
     * sus colores. Da un número sin signo. La sal está escogida para que los temas de ejemplo de las capturas salgan variados.
     */
    private fun mezclar(s: Int): Int {
        var x = (s xor 3932) * 0x9E3779B1L.toInt()
        x = x xor (x ushr 15)
        x *= 0x85EBCA6BL.toInt()
        return (x xor (x ushr 13)) and 0x7fffffff
    }

    /** Una mancha irregular de [n] puntos alrededor de [c], de radios [rx] y [ry]. */
    private fun borron(c: Offset, rx: Float, ry: Float, semilla: Int, n: Int = 11): Path = Path().apply {
        for (i in 0 until n) {
            val a = i * 6.2832f / n
            val f = 0.72f + 0.42f * azar(semilla, i)
            val p = Offset(c.x + cos(a) * rx * f, c.y + sin(a) * ry * f)
            if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }

    /**
     * La sombra de una cosa en el muro, detrás. [lejos] dice cuánto se separa del muro (0, pegada; 1, en el tablero). De día,
     * corrida a la derecha y abajo. De noche es la de la vela: la silueta agrandada desde la llama (así la tiende una luz
     * cercana), más larga cuanto más lejos del muro. Solo cae en el muro, por encima del tablero.
     */
    private fun DrawScope.sombraEnMuro(sil: Path, w: Float, o: Boolean, lejos: Float, fuerza: Float = 1f) {
        clipRect(0f, 0f, w, TAPA * w + 1f) {
            if (!o) translate(w * (0.007f + 0.015f * lejos), w * (0.005f + 0.006f * lejos)) { drawPath(sil, sombraMueble(0.2f * fuerza)) }
            else {
                val e = 1.02f + 0.1f * lejos
                withTransform({ scale(e, e, LUZ_VELA * w) }) { drawPath(sil, NOCHE.copy(alpha = 0.34f * fuerza)) }
            }
        }
    }

    /** La sombra de contacto, con el color de la hora. */
    private fun DrawScope.contacto(c: Offset, ancho: Float, alto: Float, t: Tono, a: Float = 0.34f) =
        drawOval(t.s(if (t.o) a * 0.9f else a), Offset(c.x - ancho * 0.42f, c.y - alto / 2), Size(ancho, alto))

    // ---------------------------------------------------------------- el muro

    /**
     * El muro de sillarejo enlucido y encalado hasta [hasta]: la cal, la mancha de luz de día arriba a la izquierda (o el halo de
     * la vela de noche), las pinceladas, las hiladas del sillarejo que se adivinan bajo la cal y la humedad de abajo.
     */
    private fun DrawScope.pared(w: Float, hasta: Float, o: Boolean) {
        val u = density
        drawRect(if (o) Tono(true, 0.05f).c(CAL) else CAL, Offset.Zero, Size(w, hasta))
        if (!o) {
            mancha(Offset(w * 0.04f, -w * 0.03f), w * 0.9f, luzMueble(0.55f))
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, sombraMueble(0.2f)), w * 0.45f, w), Offset(w * 0.45f, 0f), Size(w * 0.55f, hasta))
        } else {
            val v = LUZ_VELA * w
            mancha(v - Offset(0f, w * 0.03f), w * 0.47f, mezcla(Tono(true, 0.36f).c(CAL), LLAMA, 0.16f).copy(alpha = 0.92f))
            mancha(v - Offset(0f, w * 0.02f), w * 0.27f, mezcla(Tono(true, 1f).c(CAL), LLAMA, 0.32f).copy(alpha = 0.8f))
        }
        pinceladas(Rect(0f, 0f, w, hasta), if (o) NOCHE else CAL_OSC, 611, 54, u, if (o) 0.07f else 0.1f)
        pinceladas(Rect(0f, 0f, w, hasta), LUZ_MUEBLE, 612, 30, u, if (o) 0.02f else 0.09f)
        // Las hiladas del sillarejo bajo la cal: un relieve leve, la llaga en sombra y la piedra de debajo con su filo de luz.
        val ts = if (o) NOCHE.copy(alpha = 0.2f) else sombraMueble(0.08f)
        val tl = if (o) LLAMA.copy(alpha = 0.04f) else luzMueble(0.3f)
        var y = w * 0.045f; var f = 0
        while (y < minOf(hasta, w * 0.4f)) {
            var x = -w * 0.06f * azar(620 + f, 0); var j = 0
            while (x < w) {
                val largo = w * (0.07f + 0.09f * azar(621 + f, j))
                val dy = w * 0.005f * (azar(622 + f, j) - 0.5f)
                if (azar(623 + f, j) > 0.3f) {
                    drawLine(ts, Offset(x, y + dy), Offset(x + largo * 0.85f, y + dy + w * 0.003f * (azar(624 + f, j) - 0.5f)), 1f * u, StrokeCap.Round)
                    drawLine(tl, Offset(x, y + dy + 1.1f * u), Offset(x + largo * 0.85f, y + dy + 1.1f * u), 0.8f * u, StrokeCap.Round)
                }
                if (azar(625 + f, j) > 0.5f) drawLine(ts, Offset(x + largo, y + dy), Offset(x + largo + w * 0.003f, y + dy + w * 0.045f * azar(626 + f, j)), 0.9f * u, StrokeCap.Round)
                x += largo; j++
            }
            y += w * (0.05f + 0.02f * azar(627, f)); f++
        }
        // La humedad y el roce que suben desde el suelo, en las franjas de los lados.
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, (if (o) NOCHE else SOMBRA_MUEBLE).copy(alpha = 0.3f)), hasta - w * 0.5f, hasta), Offset(0f, hasta - w * 0.5f), Size(w, w * 0.5f))
    }

    /** Una falta del enlucido en [c]: el hueco con su mortero, las piedras del sillarejo y el canto de la cal. */
    private fun DrawScope.falta(c: Offset, rx: Float, ry: Float, semilla: Int, w: Float, o: Boolean) {
        val u = density
        val t = Tono(o, if (o) luz(c / w) else 0f)
        val p = borron(c, rx, ry, semilla, 13)
        drawPath(p, t.c(mezcla(PIEDRA, CAL_OSC, 0.45f)))
        clipPath(p) {
            for (j in 0 until 7) {
                val q = Offset(c.x + rx * 1.6f * (azar(semilla, 20 + j) - 0.5f), c.y + ry * 1.6f * (azar(semilla, 30 + j) - 0.5f))
                val a = rx * (0.28f + 0.22f * azar(semilla, 40 + j)); val b = a * (0.55f + 0.2f * azar(semilla, 50 + j))
                val piedra = t.c(mezcla(PIEDRA, if (j % 3 == 0) TERRACOTA else CAL_OSC, 0.12f + 0.2f * azar(semilla, 60 + j)))
                drawOval(t.s(0.3f), Offset(q.x - a + 1f * u, q.y - b + 1f * u), Size(2 * a, 2 * b))
                drawOval(piedra, Offset(q.x - a, q.y - b), Size(2 * a, 2 * b))
                drawArc(t.b(0.4f), 190f, 80f, false, Offset(q.x - a, q.y - b), Size(2 * a, 2 * b), style = Stroke(0.8f * u))
            }
            // El canto de la cal arriba a la izquierda echa sombra dentro del hueco.
            translate(1.8f * u, 1.8f * u) { drawPath(p, t.s(0.42f), style = Stroke(3.2f * u)) }
        }
        // El grosor del enlucido: abajo a la derecha coge la luz.
        translate(0.8f * u, 0.8f * u) { drawPath(p, t.b(0.45f), style = Stroke(1f * u)) }
        drawPath(p, t.s(0.25f), style = Stroke(0.7f * u))
    }

    /** Medio punto sobre jambas: el contorno de un arco de radio [r] con centro en ([cx], [cy]) que baja hasta [hasta]. */
    private fun vano(cx: Float, cy: Float, r: Float, hasta: Float): Path = Path().apply {
        moveTo(cx - r, hasta); lineTo(cx - r, cy)
        arcTo(Rect(cx - r, cy - r, cx + r, cy + r), 180f, 180f, false)
        lineTo(cx + r, hasta); close()
    }

    /**
     * El arco ciego de medio punto, en relieve: la rosca con sus dovelas alternas de cal y almagre, las jambas, el rebaje del
     * tímpano y la cruz pintada. Va con veladuras sobre el muro, para que la mancha de luz (o el halo de la vela) siga debajo.
     */
    private fun DrawScope.arcoCiego(w: Float, o: Boolean) {
        val u = density
        val cx = w * ARCO_X; val cy = w * IMP_T
        val ri = w * ARCO_RI; val ro = w * ARCO_RO
        val hasta = w * TAPA + 2f
        fun tn(x: Float, y: Float) = Tono(o, if (o) luz(Offset(x / w, y / w)) else 0f)
        val rebaje = vano(cx, cy, ri, hasta); val fuera = vano(cx, cy, ro, hasta)
        // La sombra del relieve en el muro: de día, abajo a la derecha; de noche, hacia fuera de la vela (a la derecha y arriba).
        val corrida = Path().apply { addPath(fuera, if (o) Offset(w * 0.009f, -w * 0.005f) else Offset(w * 0.009f, w * 0.007f)) }
        drawPath(Path().apply { op(corrida, fuera, PathOperation.Difference) }, if (o) NOCHE.copy(alpha = 0.34f) else sombraMueble(0.2f))
        // La rosca y las jambas sobresalen: de día su cal coge algo más de luz.
        if (!o) drawPath(fuera, luzMueble(0.25f))
        // Las dovelas, alternas de cal y almagre, gastadas.
        val n = 13
        for (i in 0 until n) {
            val a0 = 180f + 180f * i / n; val a1 = 180f + 180f * (i + 1) / n
            val am = Math.toRadians(((a0 + a1) / 2).toDouble())
            val tc = tn(cx + cos(am).toFloat() * (ri + ro) / 2, cy + sin(am).toFloat() * (ri + ro) / 2)
            val dov = Path().apply {
                arcTo(Rect(cx - ro, cy - ro, cx + ro, cy + ro), a0, a1 - a0, true)
                arcTo(Rect(cx - ri, cy - ri, cx + ri, cy + ri), a1, a0 - a1, false)
                close()
            }
            if (i % 2 == 1) drawPath(dov, tc.c(mezcla(ALMAGRE, CAL, 0.2f + 0.22f * azar(71, i))))
            drawPath(dov, tc.s(0.2f), style = Stroke(0.7f * u))
            // Lo que se llevó el tiempo: la pintura saltada en algún punto.
            if (i % 2 == 1 && azar(72, i) > 0.35f) {
                val r = ri + (ro - ri) * (0.3f + 0.4f * azar(76, i))
                val q = Offset(cx + cos(am).toFloat() * r, cy + sin(am).toFloat() * r)
                drawPath(borron(q, w * 0.009f, w * 0.006f, 73 + i, 7), tc.c(mezcla(CAL, CAL_OSC, 0.3f)).copy(alpha = 0.9f))
            }
        }
        // El filo de la rosca: de día, luz arriba a la izquierda y sombra abajo a la derecha.
        if (!o) {
            drawArc(luzMueble(0.6f), 180f, 95f, false, Offset(cx - ro, cy - ro), Size(2 * ro, 2 * ro), style = Stroke(1.2f * u))
            drawArc(sombraMueble(0.25f), 290f, 70f, false, Offset(cx - ro, cy - ro), Size(2 * ro, 2 * ro), style = Stroke(1.2f * u))
        }
        // El rebaje: algo más hondo que el muro.
        drawPath(rebaje, if (o) NOCHE.copy(alpha = 0.2f) else sombraMueble(0.09f))
        clipPath(rebaje) {
            pinceladas(Rect(cx - ri, cy - ri, cx + ri, hasta), if (o) NOCHE else CAL_OSC, 615, 16, u, 0.1f)
            if (!o) {
                // De día, la rosca deja su sombra dentro, arriba a la izquierda.
                val sombra = Path().apply { op(rebaje, Path().apply { addPath(rebaje, Offset(w * 0.017f, w * 0.019f)) }, PathOperation.Difference) }
                drawPath(sombra, sombraMueble(0.26f))
            }
            // La cruz pintada: sencilla, de brazos apenas abiertos, de almagre, con su filete de ocre y las faltas del tiempo.
            val c = Offset(cx, w * 0.118f)
            val tk = tn(c.x, c.y)
            val s = w * 0.056f
            val cruz = Path().apply {
                addPath(poligono(c.x - s * 0.12f, c.y - s * 0.62f, c.x + s * 0.12f, c.y - s * 0.62f, c.x + s * 0.075f, c.y + s * 0.95f, c.x - s * 0.075f, c.y + s * 0.95f))
                addPath(poligono(c.x - s * 0.5f, c.y - s * 0.16f, c.x + s * 0.5f, c.y - s * 0.16f, c.x + s * 0.46f, c.y + s * 0.07f, c.x - s * 0.46f, c.y + s * 0.07f))
            }
            drawPath(cruz, tk.c(mezcla(ORO, CAL, 0.3f)).copy(alpha = 0.7f), style = Stroke(1.8f * u))
            drawPath(cruz, tk.c(ALMAGRE).copy(alpha = 0.78f))
            for (j in 0 until 6) {
                val q = Offset(c.x + s * 0.9f * (azar(74, j) - 0.5f), c.y + s * 1.4f * (azar(75, j) - 0.4f))
                drawOval(tk.c(mezcla(CAL, CAL_OSC, 0.4f)).copy(alpha = 0.8f), Offset(q.x - s * 0.06f, q.y - s * 0.04f), Size(s * 0.12f, s * 0.08f))
            }
        }
        if (!o) {
            // Las jambas: su canto de dentro, a la izquierda en sombra y a la derecha con luz.
            drawLine(sombraMueble(0.3f), Offset(cx - ri + 0.6f * u, cy), Offset(cx - ri + 0.6f * u, hasta), 1.4f * u)
            drawLine(luzMueble(0.55f), Offset(cx + ri - 0.6f * u, cy), Offset(cx + ri - 0.6f * u, hasta), 1.1f * u)
        } else {
            // De noche, la vela está junto a la jamba izquierda: el canto de enfrente y el de arriba la miran.
            drawArc(LLAMA.copy(alpha = 0.3f), 270f, 90f, false, Offset(cx - ri, cy - ri), Size(2 * ri, 2 * ri), style = Stroke(1.2f * u))
            drawLine(LLAMA.copy(alpha = 0.3f), Offset(cx + ri, cy), Offset(cx + ri, w * 0.2f), 1.2f * u)
            drawArc(LLAMA.copy(alpha = 0.22f), 180f, 70f, false, Offset(cx - ro, cy - ro), Size(2 * ro, 2 * ro), style = Stroke(1.1f * u))
        }
    }

    /** La imposta corrida con su ajedrezado pintado, de lado a lado (salvo el vano del arco ciego). */
    private fun DrawScope.imposta(w: Float, o: Boolean) {
        val u = density
        val y0 = w * IMP_T; val y1 = w * IMP_B
        val listel = w * 0.006f
        val lado = (y1 - y0 - listel) / 2
        val tramos = listOf(0f to w * (ARCO_X - ARCO_RI), w * (ARCO_X + ARCO_RI) to w)
        tramos.forEach { (xa, xb) ->
            // La sombra que deja en el muro, abajo.
            drawRect(if (o) NOCHE.copy(alpha = 0.32f) else sombraMueble(0.22f), Offset(xa, y1), Size(xb - xa, w * 0.007f))
            var x = xa; var j = 0
            while (x < xb) {
                val t = Tono(o, if (o) luz(Offset((x + lado / 2) / w, (y0 + y1) / 2 / w)) else 0f)
                val ancho = minOf(lado, xb - x)
                drawRect(t.c(mezcla(CAL, LUZ_MUEBLE, 0.2f)), Offset(x, y0), Size(ancho, listel))
                for (f in 0 until 2) {
                    val pintado = (j + f) % 2 == 0
                    val gasto = azar(91 + f, j)
                    val col = if (pintado) mezcla(ALMAGRE, CAL, if (gasto > 0.8f) 0.55f else 0.12f) else mezcla(CAL, CAL_OSC, 0.25f)
                    drawRect(t.c(col), Offset(x, y0 + listel + f * lado), Size(ancho, lado + 0.4f))
                }
                // Cada taco con su cara de arriba a la luz y su canto derecho en sombra.
                drawRect(t.b(0.3f), Offset(x, y0 + listel), Size(ancho, 0.8f * u))
                drawRect(t.s(0.22f), Offset(x + ancho - 0.7f * u, y0 + listel), Size(0.7f * u, y1 - y0 - listel))
                x += lado; j++
            }
            drawRect(Tono(o, if (o) 0.3f else 0f).b(0.5f), Offset(xa, y0), Size(xb - xa, 0.9f * u))
            drawRect(if (o) NOCHE.copy(alpha = 0.35f) else sombraMueble(0.3f), Offset(xa, y0 + listel - 0.8f * u), Size(xb - xa, 0.8f * u))
        }
    }

    /** El hollín que la vela ha ido dejando en la cal, encima de donde arde siempre. */
    private fun DrawScope.hollin(w: Float, o: Boolean) {
        val c = Offset(w * (LUZ_VELA.x + 0.003f), w * (LUZ_VELA.y - 0.1f))
        val col = if (o) NOCHE else mezcla(SOMBRA_MUEBLE, TINTA, 0.6f)
        withTransform({ scale(0.32f, 1f, c) }) {
            drawCircle(Brush.radialGradient(listOf(col.copy(alpha = if (o) 0.34f else 0.2f), col.copy(alpha = 0f)), c, w * 0.12f), w * 0.12f, c)
        }
    }

    // ---------------------------------------------------------------- lo colgado del muro

    /** Un punto [p] girado [g] grados alrededor de [c]. */
    private fun girar(p: Offset, c: Offset, g: Float): Offset {
        val a = Math.toRadians(g.toDouble())
        val d = p - c
        return Offset(c.x + (d.x * cos(a) - d.y * sin(a)).toFloat(), c.y + (d.x * sin(a) + d.y * cos(a)).toFloat())
    }

    private const val GIRO_BASTIDOR = -2.5f
    private fun clavoBastidor(w: Float) = Offset(w * 0.21f, w * 0.02f)
    private fun marcoBastidor(w: Float) = Rect(w * 0.068f, w * 0.05f, w * 0.35f, w * 0.236f)

    /**
     * El bastidor de madera con una piel tensada secándose, colgado de un clavo: la piel, tirada por los cordeles desde sus
     * puntas (el borde entra entre cordel y cordel), con el lado del pelo más amarillo, el raspado del cuchillo y un ojo cosido.
     */
    private fun DrawScope.bastidor(w: Float, o: Boolean) {
        val u = density
        val r = marcoBastidor(w); val clavo = clavoBastidor(w)
        val t = Tono(o, if (o) luz(r.center / w) else 0f)
        // El clavo y la cuerda de colgar.
        drawLine(t.c(mezcla(PERG_OSC, BADANA, 0.4f)), clavo, girar(Offset(r.left + r.width * 0.2f, r.top), clavo, GIRO_BASTIDOR), 1f * u)
        drawLine(t.c(mezcla(PERG_OSC, BADANA, 0.4f)), clavo, girar(Offset(r.right - r.width * 0.2f, r.top), clavo, GIRO_BASTIDOR), 1f * u)
        drawCircle(t.c(HIERRO), 2f * u, clavo)
        drawCircle(t.b(0.5f), 0.7f * u, clavo - Offset(0.6f * u, 0.6f * u))
        rotate(GIRO_BASTIDOR, clavo) {
            val b = w * 0.014f
            // Los listones: cruzan en las esquinas y sobresalen.
            val listones = listOf(
                Rect(r.left - w * 0.01f, r.top, r.right + w * 0.01f, r.top + b), Rect(r.left - w * 0.01f, r.bottom - b, r.right + w * 0.01f, r.bottom),
                Rect(r.left, r.top - w * 0.01f, r.left + b, r.bottom + w * 0.01f), Rect(r.right - b, r.top - w * 0.01f, r.right, r.bottom + w * 0.01f),
            )
            // Los puntos de donde tiran los cordeles (las puntas de la piel) y, entre ellos, los entrantes del borde.
            val dentro = Rect(r.left + w * 0.04f, r.top + w * 0.036f, r.right - w * 0.04f, r.bottom - w * 0.034f)
            val nx = 5; val ny = 3
            val puntas = ArrayList<Pair<Offset, Offset>>()   // la punta de la piel y el punto del listón al que va atada
            for (i in 0..nx) puntas += Offset(dentro.left + dentro.width * i / nx, dentro.top) to Offset(r.left + b + (r.width - 2 * b) * i / nx, r.top + b * 0.5f)
            for (i in 1 until ny) puntas += Offset(dentro.right, dentro.top + dentro.height * i / ny) to Offset(r.right - b * 0.5f, r.top + b + (r.height - 2 * b) * i / ny)
            for (i in nx downTo 0) puntas += Offset(dentro.left + dentro.width * i / nx, dentro.bottom) to Offset(r.left + b + (r.width - 2 * b) * i / nx, r.bottom - b * 0.5f)
            for (i in ny - 1 downTo 1) puntas += Offset(dentro.left, dentro.top + dentro.height * i / ny) to Offset(r.left + b * 0.5f, r.top + b + (r.height - 2 * b) * i / ny)
            val centro = dentro.center
            val piel = Path().apply {
                puntas.forEachIndexed { i, (p, a) ->
                    // La punta sale un poco hacia su cordel; el borde entre dos puntas entra hacia el centro.
                    val q = p + (a - p) * (0.22f + 0.2f * azar(813, i))
                    if (i == 0) moveTo(q.x, q.y)
                    val (p2, a2) = puntas[(i + 1) % puntas.size]
                    val q2 = p2 + (a2 - p2) * (0.22f + 0.2f * azar(813, (i + 1) % puntas.size))
                    val m = (q + q2) / 2f
                    val e = m + (centro - m) * (0.1f + 0.08f * azar(814, i))
                    quadraticTo(e.x, e.y, q2.x, q2.y)
                }
                close()
            }
            // Los cordeles, de cada punta a su listón.
            val cord = t.c(mezcla(PERG_OSC, BADANA, 0.45f))
            puntas.forEachIndexed { i, (p, a) ->
                val q = p + (a - p) * (0.22f + 0.2f * azar(813, i))
                drawLine(cord, q, a, 0.9f * u)
            }
            // La sombra de cada pieza en el muro: de día, abajo a la derecha; de noche, hacia fuera de la vela.
            val ds = if (o) Offset(-2.6f * u, -1.4f * u) else Offset(2.2f * u, 2.8f * u)
            listones.forEach { l -> drawRect(t.s(0.3f), l.topLeft + ds, l.size) }
            translate(ds.x, ds.y) { drawPath(piel, t.s(0.22f)) }
            drawPath(piel, t.c(mezcla(PERGAMINO, PERG_OSC, 0.22f)))
            clipPath(piel) {
                // El lado del lomo, más amarillo; el brillo de la piel tensa arriba a la izquierda; el raspado.
                drawOval(t.c(PERG_OSC).copy(alpha = 0.5f), Offset(r.left + r.width * 0.3f, r.top + r.height * 0.36f), Size(r.width * 0.42f, r.height * 0.28f))
                drawOval(t.s(0.1f), Offset(r.left + r.width * 0.5f, r.top + r.height * 0.3f), Size(r.width * 0.5f, r.height * 0.7f))
                drawOval(t.b(0.4f), Offset(r.left + r.width * 0.14f, r.top + r.height * 0.16f), Size(r.width * 0.3f, r.height * 0.22f))
                for (j in 0 until 7) {
                    val y = r.top + r.height * (0.26f + 0.085f * j)
                    drawLine(t.b(0.22f), Offset(r.left + r.width * (0.2f + 0.1f * azar(81, j)), y), Offset(r.left + r.width * (0.55f + 0.28f * azar(82, j)), y - r.height * 0.05f), 1.3f * u, StrokeCap.Round)
                }
                // Un ojo de la piel, cosido.
                val ag = Offset(r.left + r.width * 0.7f, r.top + r.height * 0.42f)
                drawOval(t.s(0.3f), Offset(ag.x - w * 0.009f, ag.y - w * 0.004f), Size(w * 0.018f, w * 0.008f))
                for (j in -2..2) drawLine(t.c(BADANA), Offset(ag.x + j * w * 0.0034f, ag.y - w * 0.006f), Offset(ag.x + j * w * 0.0034f + w * 0.001f, ag.y + w * 0.006f), 0.7f * u)
                drawPath(piel, t.s(0.22f), style = Stroke(1.6f * u))
            }
            // Los listones encima: con su luz arriba y su canto en sombra.
            listones.forEachIndexed { i, l ->
                drawRect(t.c(if (i < 2) ROBLE_LUZ else mezcla(ROBLE_LUZ, ROBLE, 0.45f)), l.topLeft, l.size)
                if (i < 2) drawRect(t.b(0.5f), l.topLeft, Size(l.width, 0.9f * u)) else drawRect(t.b(0.4f), l.topLeft, Size(0.9f * u, l.height))
                if (i < 2) drawRect(t.s(0.35f), Offset(l.left, l.bottom - 0.9f * u), Size(l.width, 0.9f * u)) else drawRect(t.s(0.3f), Offset(l.right - 0.9f * u, l.top), Size(0.9f * u, l.height))
            }
            // Las clavijas de las esquinas y las muescas de los cordeles en los listones.
            listOf(r.topLeft, Offset(r.right, r.top), r.bottomRight, Offset(r.left, r.bottom)).forEach { p ->
                val q = Offset(p.x + (if (p.x < r.center.x) b / 2 else -b / 2), p.y + (if (p.y < r.center.y) b / 2 else -b / 2))
                drawCircle(t.c(ROBLE_OSC), 1.5f * u, q)
                drawCircle(t.b(0.4f), 0.5f * u, q - Offset(0.5f * u, 0.5f * u))
            }
            puntas.forEach { (_, a) -> drawCircle(cord, 1.1f * u, a) }
        }
    }

    /** Las plumas del manojo: su giro y su largo, en fracciones del ancho. */
    private val PLUMAS = listOf(-17f to 0.112f, -6f to 0.132f, 5f to 0.12f, 15f to 0.102f, -1f to 0.096f)
    private fun nudoPlumas(w: Float) = Offset(w * 0.414f, w * 0.064f)

    private fun siluetaPlumas(w: Float): Path = Path().apply {
        val n = nudoPlumas(w)
        PLUMAS.forEach { (g, l) ->
            val f = girar(Offset(n.x, n.y + l * w), n, g)
            addPath(poligono(n.x - 1f, n.y, n.x + 1f, n.y, f.x + w * 0.009f, f.y, f.x - w * 0.009f, f.y))
        }
    }

    /** El manojo de plumas de ganso colgado de un clavo por el cálamo, atado con un cordel. */
    private fun DrawScope.manojoPlumas(w: Float, o: Boolean) {
        val u = density
        val n = nudoPlumas(w)
        val t = Tono(o, if (o) luz(Offset(n.x / w, n.y / w + 0.06f)) else 0f)
        val clavo = n - Offset(0f, w * 0.02f)
        drawLine(t.c(mezcla(ALMAGRE, BADANA, 0.4f)), clavo, n, 1.1f * u)
        drawCircle(t.c(HIERRO), 1.9f * u, clavo)
        drawCircle(t.b(0.5f), 0.6f * u, clavo - Offset(0.5f * u, 0.5f * u))
        PLUMAS.forEachIndexed { i, (g, l) ->
            rotate(g, n) {
                val largo = l * w
                val x = n.x
                val y0 = n.y + w * 0.006f
                val barba = Path().apply {
                    moveTo(x, y0 + largo * 0.25f)
                    cubicTo(x + w * 0.019f, y0 + largo * 0.4f, x + w * 0.02f, y0 + largo * 0.8f, x + w * 0.004f, y0 + largo)
                    lineTo(x, y0 + largo)
                    cubicTo(x - w * 0.009f, y0 + largo * 0.8f, x - w * 0.01f, y0 + largo * 0.45f, x, y0 + largo * 0.25f)
                    close()
                }
                val blanco = if (i % 2 == 0) mezcla(CAL, LUZ_MUEBLE, 0.4f) else mezcla(CAL, PIEDRA, 0.3f)
                translate(1f * u, 1.2f * u) { drawPath(barba, t.s(0.18f)) }
                drawPath(barba, t.c(blanco))
                clipPath(barba) {
                    drawRect(t.s(0.2f), Offset(x + w * 0.004f, y0), Size(w * 0.022f, largo))
                    for (j in 0 until 6) {
                        val yy = y0 + largo * (0.33f + 0.11f * j)
                        drawLine(t.s(0.22f), Offset(x, yy), Offset(x + w * 0.019f, yy + w * 0.007f), 0.6f * u)
                        drawLine(t.s(0.16f), Offset(x, yy), Offset(x - w * 0.01f, yy + w * 0.006f), 0.5f * u)
                    }
                }
                drawLine(t.c(mezcla(CERA, CUERNO, 0.4f)), Offset(x, y0), Offset(x, y0 + largo), 1f * u)
                drawLine(t.b(0.4f), Offset(x - 0.5f * u, y0), Offset(x - 0.5f * u, y0 + largo * 0.6f), 0.5f * u)
            }
        }
        // El cordel que las ata, con sus dos cabos.
        val cordel = t.c(mezcla(ALMAGRE, BADANA, 0.3f))
        drawOval(cordel, Offset(n.x - w * 0.008f, n.y + w * 0.004f), Size(w * 0.016f, w * 0.008f))
        drawLine(cordel, n + Offset(w * 0.004f, w * 0.01f), n + Offset(w * 0.012f, w * 0.03f), 0.9f * u)
        drawLine(cordel, n + Offset(w * 0.002f, w * 0.01f), n + Offset(-w * 0.004f, w * 0.026f), 0.9f * u)
    }

    // ---------------------------------------------------------------- sobre el tablero

    /** El contorno del atril (el tablero inclinado, sus perillas y su base), para su sombra en el muro. */
    private fun siluetaAtril(w: Float): Path = Path().apply {
        addPath(poligono(w * 0.552f, w * 0.398f, w * 0.574f, w * 0.186f, w * 0.936f, w * 0.186f, w * 0.954f, w * 0.398f))
        listOf(0.593f, 0.907f).forEach { x ->
            addRect(Rect(w * (x - 0.007f), w * 0.156f, w * (x + 0.007f), w * 0.19f))
            addOval(Rect(w * (x - 0.012f), w * 0.139f, w * (x + 0.012f), w * 0.163f))
        }
    }

    /**
     * El atril de roble con el códice grande abierto. El tablero inclinado con sus dos perillas torneadas, el reborde donde
     * descansa el libro y la base calada. El códice, de hojas gruesas, con las tapas de tabla forradas de badana, sus bullones
     * y las manecillas colgando: la página de la izquierda (la que mira a la vela) lleva la inicial a medio pintar (media con
     * su oro y su color, media solo con el dibujo a pluma y el bol) y la de la derecha está a medio escribir sobre el pautado,
     * con un peso de plomo que la sujeta.
     */
    private fun DrawScope.atrilConCodice(w: Float, o: Boolean) {
        val u = density
        fun tn(x: Float, y: Float) = Tono(o, if (o) (luz(Offset(x, y)) * 1.15f).coerceAtMost(1f) else 0f)
        val t = tn(0.75f, 0.3f); val ti = tn(0.64f, 0.29f); val td = tn(0.84f, 0.28f)
        val g = w * 0.75f
        contacto(Offset(w * 0.77f, w * 0.399f), w * 0.42f, 4.6f * u, t, 0.42f)
        // Las perillas torneadas de los montantes, que asoman por encima del tablero.
        listOf(0.593f, 0.907f).forEachIndexed { i, x ->
            val tp = if (i == 0) ti else td
            val c = Offset(w * x, w * 0.151f); val r = w * 0.012f
            drawRect(tp.c(mezcla(ROBLE, ROBLE_OSC, 0.2f)), Offset(w * (x - 0.007f), w * 0.158f), Size(w * 0.014f, w * 0.03f))
            drawRect(tp.s(0.3f), Offset(w * (x + 0.002f), w * 0.158f), Size(w * 0.005f, w * 0.03f))
            drawRect(tp.c(ROBLE_LUZ), Offset(w * (x - 0.01f), w * 0.164f), Size(w * 0.02f, w * 0.005f))
            drawCircle(tp.c(mezcla(ROBLE, ROBLE_LUZ, 0.4f)), r, c)
            drawArc(tp.s(0.35f), -30f, 140f, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(r * 0.4f))
            drawCircle(tp.b(0.55f), r * 0.3f, c - Offset(r * 0.35f, r * 0.4f))
        }
        // El tablero inclinado y su canto derecho, en sombra.
        val tablero = poligono(w * 0.556f, w * 0.362f, w * 0.574f, w * 0.186f, w * 0.926f, w * 0.186f, w * 0.944f, w * 0.362f)
        drawPath(tablero, t.c(mezcla(ROBLE, ROBLE_OSC, 0.18f)))
        clipPath(tablero) {
            vetas(Rect(w * 0.556f, w * 0.186f, w * 0.944f, w * 0.362f), ROBLE_OSC.copy(alpha = 0.3f), 517, 0.4f, grosor = 0.6f, ondulacion = 0.3f)
            drawRect(td.s(0.14f), Offset(w * 0.8f, w * 0.186f), Size(w * 0.15f, w * 0.18f))
        }
        drawPath(poligono(w * 0.926f, w * 0.186f, w * 0.936f, w * 0.19f, w * 0.954f, w * 0.362f, w * 0.944f, w * 0.362f), td.c(ROBLE_OSC))
        drawLine(ti.b(0.5f), Offset(w * 0.574f, w * 0.186f), Offset(w * 0.926f, w * 0.186f), 1.2f * u)
        drawLine(ti.b(0.45f), Offset(w * 0.556f, w * 0.362f), Offset(w * 0.574f, w * 0.186f), 1.2f * u)
        // Las tapas: tabla forrada de badana, asomando alrededor de las hojas, con sus bullones de latón en las esquinas.
        val tapa = poligono(w * 0.588f, w * 0.356f, w * 0.595f, w * 0.204f, g, w * 0.213f, w * 0.905f, w * 0.204f, w * 0.912f, w * 0.356f, g, w * 0.362f)
        translate(1.6f * u, 1.8f * u) { drawPath(tapa, t.s(0.3f)) }
        drawPath(tapa, t.c(mezcla(BADANA, ROBLE_OSC, 0.45f)))
        drawPath(tapa, t.s(0.3f), style = Stroke(0.8f * u))
        listOf(0.5985f to 0.209f, 0.9015f to 0.209f, 0.5925f to 0.3515f, 0.9075f to 0.3515f).forEach { (x, y) ->
            drawCircle(t.c(ORO), w * 0.0042f, Offset(w * x, w * y))
            drawCircle(t.b(0.7f), w * 0.0015f, Offset(w * (x - 0.001f), w * (y - 0.001f)))
        }
        // El grueso de las hojas, que asoma por los cantos y por abajo.
        for (j in 0 until 4) {
            val d = (0.8f + 0.9f * j) * u
            val col = t.c(if (j % 2 == 0) PERG_OSC else mezcla(PERG_OSC, BADANA, 0.35f))
            drawLine(col, Offset(w * 0.6035f - d * 0.5f, w * 0.215f + d * 0.4f), Offset(w * 0.5985f - d * 0.5f, w * 0.348f + d * 0.6f), 0.9f * u)
            drawLine(col, Offset(w * 0.8965f + d * 0.5f, w * 0.215f + d * 0.4f), Offset(w * 0.9015f + d * 0.5f, w * 0.348f + d * 0.6f), 0.9f * u)
            drawPath(Path().apply {
                moveTo(w * 0.5985f - d * 0.5f, w * 0.348f + d * 0.6f)
                cubicTo(w * 0.64f, w * 0.343f + d * 0.8f, w * 0.71f, w * 0.345f + d * 0.8f, g, w * 0.356f + d * 0.5f)
                cubicTo(w * 0.79f, w * 0.345f + d * 0.8f, w * 0.86f, w * 0.343f + d * 0.8f, w * 0.9015f + d * 0.5f, w * 0.348f + d * 0.6f)
            }, col, style = Stroke(0.9f * u))
        }
        // Las dos páginas, combadas: la de la izquierda coge la luz y se apaga hacia el lomo.
        fun pagina(s: Float): Path {
            fun x(f: Float) = g + s * w * f
            return Path().apply {
                moveTo(g, w * 0.223f)
                cubicTo(x(0.04f), w * 0.208f, x(0.11f), w * 0.206f, x(0.146f), w * 0.214f)
                lineTo(x(0.151f), w * 0.347f)
                cubicTo(x(0.11f), w * 0.342f, x(0.04f), w * 0.344f, g, w * 0.355f)
                close()
            }
        }
        val izq = pagina(-1f); val der = pagina(1f)
        drawPath(izq, Brush.horizontalGradient(listOf(ti.c(mezcla(PERGAMINO, LUZ_MUEBLE, 0.25f)), ti.c(PERGAMINO), ti.c(mezcla(PERG_OSC, SOMBRA_MUEBLE, 0.2f))), w * 0.6f, g))
        drawPath(der, Brush.horizontalGradient(listOf(td.c(mezcla(PERG_OSC, SOMBRA_MUEBLE, 0.14f)), td.c(mezcla(PERGAMINO, LUZ_MUEBLE, 0.15f)), td.c(mezcla(PERGAMINO, PERG_OSC, 0.3f))), g, w * 0.9f))
        // El texto de la izquierda: dos columnas, con rúbricas rojas, bajo la inicial y a su lado.
        val paso = w * 0.0062f
        val ini = Rect(w * 0.612f, w * 0.2225f, w * 0.674f, w * 0.2845f)
        val tinta = ti.c(Color(0xFF4A3426)).copy(alpha = 0.66f)
        val rubrica = ti.c(mezcla(BERMELLON, ALMAGRE, 0.4f)).copy(alpha = 0.9f)
        listOf(0.612f to 0.672f, 0.684f to 0.742f).forEachIndexed { col, (a, b) ->
            var y = w * 0.2265f; var l = 0
            while (y < w * 0.339f) {
                if (col == 1 || y > ini.bottom + paso * 0.6f) {
                    val corta = azar(520 + col, l) > 0.86f
                    val x1 = if (corta) w * (a + (b - a) * 0.55f) else w * b
                    drawLine(if (azar(530 + col, l) > 0.86f) rubrica else tinta, Offset(w * a, y - (if (col == 1) w * 0.0015f else 0f)), Offset(x1, y - (if (col == 1) w * 0.0005f else 0f)), 1f * u)
                }
                y += paso; l++
            }
        }
        drawRect(ti.c(AZURITA), Offset(w * 0.684f, w * 0.286f), Size(w * 0.011f, w * 0.0125f))
        // El texto de la derecha: lo que el copista lleva escrito; lo demás, solo el pautado a punta de plomo y los picados.
        val tintaD = td.c(Color(0xFF4A3426)).copy(alpha = 0.66f)
        val plomo = td.c(TINTA).copy(alpha = 0.16f)
        listOf(0.758f to 0.817f, 0.829f to 0.888f).forEachIndexed { col, (a, b) ->
            var y = w * 0.2255f; var l = 0
            while (y < w * 0.339f) {
                val yy = y - (if (col == 0) w * 0.001f else -w * 0.001f)
                val escrito = col == 0 && l < 11
                if (escrito) drawLine(if (l == 4) td.c(mezcla(BERMELLON, ALMAGRE, 0.4f)).copy(alpha = 0.9f) else tintaD, Offset(w * a, yy), Offset(if (l == 10) w * (a + (b - a) * 0.45f) else w * b, yy), 1f * u)
                else drawLine(plomo, Offset(w * a, yy), Offset(w * b, yy), 0.6f * u)
                if (col == 1) drawCircle(td.c(TINTA).copy(alpha = 0.4f), 0.5f * u, Offset(w * 0.893f, yy))
                y += paso; l++
            }
            drawLine(plomo, Offset(w * a, w * 0.22f), Offset(w * a, w * 0.342f), 0.6f * u)
            drawLine(plomo, Offset(w * b, w * 0.22f), Offset(w * b, w * 0.342f), 0.6f * u)
        }
        // La inicial: un marco cuadrado con su letra (un fuste que baja por el margen como un tallo, una panza y un zarcillo).
        // La mitad izquierda, dorada y con color; la derecha, solo el dibujo a pluma y el bol rojizo donde irá el oro.
        val s = ini.width
        val fuste = Rect(ini.left + s * 0.2f, ini.top + s * 0.13f, ini.left + s * 0.35f, ini.bottom - s * 0.12f)
        val panza = Offset(ini.left + s * 0.56f, ini.top + s * 0.4f)
        val zarcillo = Path().apply {
            moveTo(fuste.right, ini.bottom - s * 0.24f)
            cubicTo(ini.left + s * 0.62f, ini.bottom - s * 0.12f, ini.left + s * 0.9f, ini.top + s * 0.8f, ini.left + s * 0.74f, ini.top + s * 0.68f)
            cubicTo(ini.left + s * 0.62f, ini.top + s * 0.6f, ini.left + s * 0.52f, ini.top + s * 0.7f, ini.left + s * 0.6f, ini.top + s * 0.76f)
        }
        val tallo = Path().apply {
            moveTo(fuste.center.x, ini.bottom)
            cubicTo(fuste.center.x - s * 0.1f, ini.bottom + s * 0.3f, fuste.center.x - s * 0.24f, ini.bottom + s * 0.5f, ini.left - s * 0.08f, ini.bottom + s * 0.86f)
        }
        val hojas = listOf(Offset(ini.left + s * 0.2f, ini.bottom + s * 0.3f), Offset(ini.left + s * 0.02f, ini.bottom + s * 0.62f))
        drawRect(td.c(mezcla(ALMAGRE, BADANA, 0.5f)).copy(alpha = 0.5f), Offset(ini.left + s * 0.56f, ini.top), Size(s * 0.44f, s * 0.1f))
        drawRect(td.c(mezcla(ALMAGRE, BADANA, 0.5f)).copy(alpha = 0.5f), Offset(ini.left + s * 0.56f, ini.bottom - s * 0.1f), Size(s * 0.44f, s * 0.1f))
        drawRect(td.c(mezcla(ALMAGRE, BADANA, 0.5f)).copy(alpha = 0.5f), Offset(ini.right - s * 0.1f, ini.top), Size(s * 0.1f, s))
        clipRect(ini.left - s * 0.3f, ini.top, ini.left + s * 0.56f, ini.bottom + s * 0.45f) {
            drawRect(ti.c(ORO), ini.topLeft, ini.size)
            drawRect(ti.c(mezcla(ORO, LUZ_MUEBLE, 0.5f)), ini.topLeft, Size(s, s * 0.1f))
            drawRect(ti.c(AZURITA), Offset(ini.left + s * 0.1f, ini.top + s * 0.1f), Size(s * 0.8f, s * 0.8f))
            for (j in 0 until 3) for (q in 0 until 3) drawCircle(ti.c(CAL).copy(alpha = 0.8f), s * 0.014f, Offset(ini.left + s * (0.42f + 0.012f * q + 0.025f * (q % 2)), ini.top + s * (0.62f + 0.09f * j)))
            drawRect(ti.c(mezcla(BERMELLON, ALMAGRE, 0.25f)), fuste.topLeft, fuste.size)
            drawRect(ti.c(mezcla(CAL, LUZ_MUEBLE, 0.5f)).copy(alpha = 0.7f), Offset(fuste.left + fuste.width * 0.22f, fuste.top + s * 0.04f), Size(fuste.width * 0.16f, fuste.height - s * 0.08f))
            drawCircle(ti.c(mezcla(BERMELLON, ALMAGRE, 0.25f)), s * 0.2f, panza, style = Stroke(s * 0.1f))
            drawPath(zarcillo, ti.c(VERDIGRIS), style = Stroke(s * 0.065f, cap = StrokeCap.Round))
            drawPath(tallo, ti.c(VERDIGRIS), style = Stroke(s * 0.06f, cap = StrokeCap.Round))
            drawOval(ti.c(mezcla(BERMELLON, ALMAGRE, 0.25f)), Offset(hojas[0].x - s * 0.07f, hojas[0].y - s * 0.045f), Size(s * 0.14f, s * 0.09f))
        }
        val pluma = ti.c(Color(0xFF5A4030)).copy(alpha = 0.75f)
        drawRect(pluma, ini.topLeft, ini.size, style = Stroke(0.7f * u))
        drawRect(pluma, Offset(ini.left + s * 0.1f, ini.top + s * 0.1f), Size(s * 0.8f, s * 0.8f), style = Stroke(0.5f * u))
        drawRect(pluma, fuste.topLeft, fuste.size, style = Stroke(0.5f * u))
        drawCircle(pluma, s * 0.25f, panza, style = Stroke(0.5f * u))
        drawCircle(pluma, s * 0.15f, panza, style = Stroke(0.5f * u))
        drawPath(zarcillo, pluma, style = Stroke(0.6f * u))
        drawPath(tallo, pluma, style = Stroke(0.6f * u))
        hojas.forEach { h -> drawOval(pluma, Offset(h.x - s * 0.07f, h.y - s * 0.045f), Size(s * 0.14f, s * 0.09f), style = Stroke(0.5f * u)) }
        // El brillo del oro: de día, crema; de noche, la vela lo enciende.
        drawLine(if (o) LLAMA.copy(alpha = (1.1f * ti.k).coerceAtMost(1f)) else luzMueble(0.75f), Offset(ini.left + s * 0.045f, ini.top + s * 0.05f), Offset(ini.left + s * 0.045f, ini.bottom - s * 0.05f), 1.2f * u)
        if (o) {
            mancha(Offset(ini.left + s * 0.3f, ini.center.y), s * 1.1f, LLAMA.copy(alpha = 0.3f * ti.k))
            drawCircle(Color(0xFFFFF0C8).copy(alpha = (ti.k * 1.2f).coerceIn(0f, 1f)), 1.4f * u, Offset(ini.left + s * 0.06f, ini.bottom - s * 0.14f))
            drawCircle(Color(0xFFFFF0C8).copy(alpha = (ti.k * 1.1f).coerceIn(0f, 1f)), 1.1f * u, Offset(ini.left + s * 0.3f, ini.top + s * 0.05f))
        }
        // El lomo, hundido en sombra.
        drawLine(t.s(0.42f), Offset(g, w * 0.224f), Offset(g, w * 0.355f), 2.4f * u)
        drawLine((if (o) ti else td).b(0.4f), Offset(g + (if (o) -1.6f else 1.6f) * u, w * 0.226f), Offset(g + (if (o) -1.6f else 1.6f) * u, w * 0.353f), 0.6f * u)
        // El peso de plomo que sujeta la página, colgado de su cordel por encima del tablero.
        val px = w * 0.846f
        drawLine(td.c(mezcla(BADANA, ROBLE_OSC, 0.4f)), Offset(px + w * 0.002f, w * 0.186f), Offset(px, w * 0.306f), 0.9f * u)
        drawOval(td.s(0.3f), Offset(px - w * 0.004f + 1.4f * u, w * 0.304f + 1.6f * u), Size(w * 0.011f, w * 0.018f))
        drawPath(Path().apply {
            moveTo(px, w * 0.302f)
            cubicTo(px + w * 0.008f, w * 0.312f, px + w * 0.007f, w * 0.323f, px, w * 0.323f)
            cubicTo(px - w * 0.007f, w * 0.323f, px - w * 0.008f, w * 0.312f, px, w * 0.302f)
            close()
        }, td.c(mezcla(HIERRO_LUZ, PIEDRA, 0.35f)))
        drawCircle(td.b(0.6f), 0.8f * u, Offset(px - w * 0.002f, w * 0.313f))
        // Las manecillas: dos correas con su broche de latón, colgando del canto de la tapa.
        listOf(0.25f, 0.318f).forEach { y ->
            val cor = Path().apply {
                moveTo(w * 0.909f, w * y)
                cubicTo(w * 0.924f, w * (y - 0.002f), w * 0.93f, w * (y + 0.008f), w * 0.927f, w * (y + 0.026f))
            }
            translate(1.2f * u, 1.2f * u) { drawPath(cor, td.s(0.3f), style = Stroke(w * 0.006f)) }
            drawPath(cor, td.c(mezcla(BADANA, ROBLE_OSC, 0.25f)), style = Stroke(w * 0.006f))
            drawRect(td.c(ORO), Offset(w * 0.9225f, w * (y + 0.024f)), Size(w * 0.009f, w * 0.009f))
            drawRect(td.b(0.6f), Offset(w * 0.9225f, w * (y + 0.024f)), Size(w * 0.009f, 0.8f * u))
        }
        // El reborde donde descansa el libro: su cara de arriba y su frente.
        val reborde = Rect(w * 0.55f, w * 0.357f, w * 0.95f, w * 0.379f)
        drawRect(t.c(mezcla(ROBLE_LUZ, LUZ_MUEBLE, 0.1f)), reborde.topLeft, Size(reborde.width, w * 0.006f))
        drawRect(t.c(ROBLE), Offset(reborde.left, reborde.top + w * 0.006f), Size(reborde.width, reborde.height - w * 0.006f))
        vetas(Rect(reborde.left, reborde.top + w * 0.006f, reborde.right, reborde.bottom), ROBLE_OSC.copy(alpha = 0.3f), 519, 0.5f, grosor = 0.5f, ondulacion = 0.3f)
        drawRect(t.b(0.5f), Offset(reborde.left, reborde.top + w * 0.006f), Size(reborde.width, 1f * u))
        drawRect(t.s(0.4f), Offset(reborde.left, reborde.bottom - 1.2f * u), Size(reborde.width, 1.2f * u))
        drawRect(td.s(0.28f), Offset(reborde.right - w * 0.012f, reborde.top), Size(w * 0.012f, reborde.height))
        // La base, calada con dos arcos rebajados: tres pies.
        val base = Rect(w * 0.566f, w * 0.379f, w * 0.936f, w * 0.398f)
        drawRect(t.c(mezcla(ROBLE, ROBLE_OSC, 0.3f)), base.topLeft, base.size)
        drawRect(t.s(0.35f), base.topLeft, Size(base.width, 1.6f * u))
        clipRect(base.left, base.top, base.right, base.bottom) {
            listOf(0.6f to 0.725f, 0.777f to 0.902f).forEach { (a, b) ->
                drawOval(t.s(0.8f), Offset(w * a, w * 0.384f), Size(w * (b - a), w * 0.03f))
                drawArc(t.b(0.3f), 200f, 60f, false, Offset(w * a, w * 0.384f), Size(w * (b - a), w * 0.03f), style = Stroke(0.8f * u))
            }
        }
        drawRect(td.s(0.3f), Offset(base.right - w * 0.014f, base.top), Size(w * 0.014f, base.height))
        // La cinta de registro: sale del lomo, pasa por el reborde y cuelga por delante del canto del armario.
        val cinta = Path().apply {
            moveTo(g - w * 0.006f, w * 0.354f)
            cubicTo(g - w * 0.006f, w * 0.372f, g - w * 0.011f, w * 0.385f, g - w * 0.01f, w * 0.402f)
            cubicTo(g - w * 0.009f, w * 0.42f, g - w * 0.006f, w * 0.44f, g - w * 0.01f, w * 0.47f)
        }
        val verde = t.c(mezcla(VERDIGRIS, ROBLE_OSC, 0.15f))
        translate(w * 0.004f, w * 0.002f) { drawPath(cinta, t.s(0.3f), style = Stroke(w * 0.0055f)) }
        drawPath(cinta, verde, style = Stroke(w * 0.0055f))
        drawPath(poligono(g - w * 0.0128f, w * 0.47f, g - w * 0.0072f, w * 0.47f, g - w * 0.0072f, w * 0.477f, g - w * 0.01f, w * 0.4735f, g - w * 0.0128f, w * 0.477f), verde)
    }

    /** La palmatoria de latón con la vela de cera: el platillo, el cañón, el asa de anillo y la vela gastada, con sus chorreras. */
    private fun DrawScope.palmatoria(w: Float, o: Boolean) {
        val u = density
        val t = Tono(o, if (o) 1f else 0f)
        val q = w * 1.5f
        val cx = w * LUZ_VELA.x
        val y = w * 0.397f
        contacto(Offset(cx + q * 0.004f, y + q * 0.001f), q * 0.066f, 3.6f * u, Tono(o, 0.4f), 0.4f)
        val metal = Brush.horizontalGradient(listOf(t.c(mezcla(ORO, LUZ_MUEBLE, 0.45f)), t.c(ORO), t.c(mezcla(ORO, ROBLE_OSC, 0.5f))), cx - q * 0.027f, cx + q * 0.027f)
        // El asa de anillo, a la izquierda (hacia quien la coge).
        drawCircle(t.c(mezcla(ORO, ROBLE_OSC, 0.4f)), q * 0.008f, Offset(cx - q * 0.033f, y - q * 0.006f), style = Stroke(1.8f * u))
        drawArc(t.b(0.5f), 180f, 90f, false, Offset(cx - q * 0.041f, y - q * 0.014f), Size(q * 0.016f, q * 0.016f), style = Stroke(0.7f * u))
        // El platillo.
        drawPath(Path().apply {
            moveTo(cx - q * 0.027f, y - q * 0.004f)
            cubicTo(cx - q * 0.025f, y + q * 0.003f, cx + q * 0.025f, y + q * 0.003f, cx + q * 0.027f, y - q * 0.004f)
            close()
        }, metal)
        drawOval(t.c(mezcla(ORO, ROBLE_OSC, 0.45f)), Offset(cx - q * 0.027f, y - q * 0.008f), Size(q * 0.054f, q * 0.008f))
        drawOval(t.c(ORO), Offset(cx - q * 0.024f, y - q * 0.007f), Size(q * 0.048f, q * 0.006f))
        drawArc(if (o) LLAMA.copy(alpha = 0.85f) else luzMueble(0.6f), 180f, 60f, false, Offset(cx - q * 0.027f, y - q * 0.008f), Size(q * 0.054f, q * 0.008f), style = Stroke(0.9f * u))
        // La cera derramada en el platillo.
        drawOval(t.c(CERA), Offset(cx - q * 0.014f, y - q * 0.0065f), Size(q * 0.024f, q * 0.004f))
        // El cañón.
        drawRect(metal, Offset(cx - q * 0.01f, y - q * 0.015f), Size(q * 0.02f, q * 0.01f))
        drawOval(t.c(ORO), Offset(cx - q * 0.011f, y - q * 0.018f), Size(q * 0.022f, q * 0.005f))
        // La vela, gastada hasta la mitad, con sus chorreras.
        val g = q * 0.016f
        val vr = Rect(cx - g / 2, w * (LUZ_VELA.y + 0.02f), cx + g / 2, y - q * 0.016f)
        drawRect(t.c(mezcla(CERA, LUZ_MUEBLE, 0.4f)), vr.topLeft, Size(g * 0.35f, vr.height))
        drawRect(t.c(CERA), Offset(vr.left + g * 0.35f, vr.top), Size(g * 0.35f, vr.height))
        drawRect(t.c(mezcla(CERA, if (o) LLAMA else SOMBRA_MUEBLE, 0.3f)), Offset(vr.left + g * 0.7f, vr.top), Size(g * 0.3f, vr.height))
        // De noche la cera se trasluce junto a la llama.
        if (o) drawRect(Brush.verticalGradient(listOf(LLAMA.copy(alpha = 0.55f), Color.Transparent), vr.top, vr.top + vr.height * 0.5f), vr.topLeft, Size(g, vr.height * 0.5f))
        drawOval(t.c(mezcla(CERA, SOMBRA_MUEBLE, 0.2f)), Offset(vr.left + g * 0.1f, vr.top - g * 0.08f), Size(g * 0.8f, g * 0.24f))
        listOf(0.2f to 0.5f, 0.78f to 0.8f, 0.5f to 0.25f).forEach { (fx, fl) ->
            val x = vr.left + g * fx
            drawLine(t.c(mezcla(CERA, LUZ_MUEBLE, 0.5f)), Offset(x, vr.top + g * 0.05f), Offset(x + 0.3f * u, vr.top + vr.height * fl), g * 0.17f, StrokeCap.Round)
            drawCircle(t.c(mezcla(CERA, LUZ_MUEBLE, 0.5f)), g * 0.12f, Offset(x + 0.3f * u, vr.top + vr.height * fl))
        }
        // La mecha negra, doblada.
        drawLine(Color(0xFF1C1612), Offset(cx, vr.top + g * 0.02f), Offset(cx + g * 0.14f, vr.top - g * 0.32f), 1.2f * u, StrokeCap.Round)
    }

    /** Un cuerno de tinta: de la boca [boca] (con su ancho [ab]) a la punta metida en el soporte [punta]; [tinta] su color. */
    private fun DrawScope.cuerno(boca: Offset, ab: Float, punta: Offset, tinta: Color, t: Tono) {
        val u = density
        val cuerpo = Path().apply {
            moveTo(boca.x - ab / 2, boca.y)
            cubicTo(boca.x - ab * 0.5f, boca.y + (punta.y - boca.y) * 0.4f, punta.x - ab * 0.32f, punta.y - (punta.y - boca.y) * 0.2f, punta.x - ab * 0.18f, punta.y)
            lineTo(punta.x + ab * 0.18f, punta.y)
            cubicTo(punta.x + ab * 0.3f, punta.y - (punta.y - boca.y) * 0.3f, boca.x + ab * 0.55f, boca.y + (punta.y - boca.y) * 0.35f, boca.x + ab / 2, boca.y)
            close()
        }
        drawPath(cuerpo, t.c(CUERNO))
        clipPath(cuerpo) {
            drawRect(t.c(mezcla(CUERNO, ROBLE_OSC, 0.45f)), Offset(boca.x + ab * 0.12f, boca.y), Size(ab, punta.y - boca.y))
            drawRect(t.b(0.45f), Offset(boca.x - ab * 0.42f, boca.y), Size(ab * 0.18f, punta.y - boca.y))
            for (j in 1..3) drawLine(t.s(0.25f), Offset(boca.x - ab, boca.y + (punta.y - boca.y) * j * 0.24f), Offset(boca.x + ab, boca.y + (punta.y - boca.y) * j * 0.24f + 1f * u), 0.7f * u)
        }
        drawOval(t.c(mezcla(CUERNO, LUZ_MUEBLE, 0.3f)), Offset(boca.x - ab / 2, boca.y - ab * 0.16f), Size(ab, ab * 0.32f))
        drawOval(t.c(tinta), Offset(boca.x - ab * 0.38f, boca.y - ab * 0.1f), Size(ab * 0.76f, ab * 0.22f))
        drawArc(t.b(0.6f), 180f, 70f, false, Offset(boca.x - ab / 2, boca.y - ab * 0.16f), Size(ab, ab * 0.32f), style = Stroke(0.7f * u))
    }

    /** Una pluma de ganso cortada: el cañón de [a] (la punta, dentro del cuerno) a [b], con la barba recortada en la mitad de arriba. */
    private fun DrawScope.pluma(a: Offset, b: Offset, t: Tono, izquierda: Boolean) {
        val u = density
        val d = b - a
        val l = d.getDistance()
        val n = Offset(-d.y / l, d.x / l) * (if (izquierda) -1f else 1f)
        val m0 = a + d * 0.42f
        val barba = Path().apply {
            moveTo(m0.x, m0.y)
            val c1 = a + d * 0.6f + n * (l * 0.14f); val c2 = a + d * 0.9f + n * (l * 0.12f)
            cubicTo(c1.x, c1.y, c2.x, c2.y, b.x, b.y)
            val c3 = a + d * 0.85f - n * (l * 0.04f)
            quadraticTo(c3.x, c3.y, m0.x, m0.y)
            close()
        }
        drawPath(barba, t.c(mezcla(CAL, LUZ_MUEBLE, 0.35f)))
        clipPath(barba) {
            for (j in 0 until 7) {
                val p = a + d * (0.47f + 0.075f * j)
                drawLine(t.s(0.2f), p, p + n * (l * 0.14f) + d * 0.06f, 0.6f * u)
            }
        }
        drawLine(t.c(mezcla(CERA, CUERNO, 0.3f)), a, b, 1.1f * u, StrokeCap.Round)
        drawLine(t.b(0.4f), a + n * (0.6f * u), a + d * 0.5f + n * (0.6f * u), 0.5f * u)
    }

    /** Los puntos del soporte de los cuernos: los del dibujo de origen, llevados a su sitio y a su tamaño. */
    private fun pt(w: Float, x: Float, y: Float) = Offset(w * (0.414f + (x - 0.604f) * 1.35f), w * (0.4f + (y - 0.4f) * 1.35f))
    private val PLUMA_A = Offset(0.356f, 0.206f)
    private val PLUMA_B = Offset(0.466f, 0.222f)

    private fun siluetaCuernos(w: Float): Path = Path().apply {
        fun cuatro(vararg xy: Float) {
            val p = (0 until 4).map { pt(w, xy[2 * it], xy[2 * it + 1]) }
            addPath(poligono(p[0].x, p[0].y, p[1].x, p[1].y, p[2].x, p[2].y, p[3].x, p[3].y))
        }
        cuatro(0.567f, 0.362f, 0.64f, 0.362f, 0.64f, 0.4f, 0.567f, 0.4f)
        cuatro(0.565f, 0.32f, 0.59f, 0.32f, 0.59f, 0.365f, 0.58f, 0.365f)
        cuatro(0.62f, 0.326f, 0.643f, 0.326f, 0.627f, 0.365f, 0.618f, 0.365f)
        val a = pt(w, 0.577f, 0.322f); val b = pt(w, 0.631f, 0.329f)
        addPath(poligono(a.x - 2f, a.y, a.x + 2f, a.y, w * PLUMA_A.x + w * 0.012f, w * PLUMA_A.y, w * PLUMA_A.x - w * 0.006f, w * PLUMA_A.y))
        addPath(poligono(b.x - 2f, b.y, b.x + 2f, b.y, w * PLUMA_B.x + w * 0.012f, w * PLUMA_B.y, w * PLUMA_B.x - w * 0.006f, w * PLUMA_B.y))
    }

    /** El soporte de los cuernos de tinta (negra y roja), con dos plumas metidas y el cortaplumas delante; y la mancha de tinta. */
    private fun DrawScope.tinteros(w: Float, o: Boolean) {
        val u = density
        val e = w * 1.35f
        val t = Tono(o, if (o) luz(Offset(0.414f, 0.35f)) else 0f)
        // La mancha de tinta en el tablero.
        drawPath(borron(pt(w, 0.604f, 0.3935f), e * 0.02f, e * 0.003f, 601, 9), t.c(TINTA).copy(alpha = 0.45f))
        contacto(pt(w, 0.606f, 0.3985f), e * 0.085f, 3.6f * u, t, 0.4f)
        // Las plumas, detrás.
        pluma(pt(w, 0.578f, 0.322f), PLUMA_A * w, t, true)
        pluma(pt(w, 0.63f, 0.33f), PLUMA_B * w, t, false)
        // Los cuernos.
        cuerno(pt(w, 0.577f, 0.322f), e * 0.024f, pt(w, 0.586f, 0.366f), TINTA, t)
        cuerno(pt(w, 0.631f, 0.329f), e * 0.021f, pt(w, 0.623f, 0.366f), mezcla(BERMELLON, ALMAGRE, 0.2f), t)
        // El soporte: un taco de roble con su cara de arriba a la luz, y lo que la tinta ha chorreado.
        val a0 = pt(w, 0.567f, 0.363f); val b0 = pt(w, 0.64f, 0.399f)
        val s = Rect(a0.x, a0.y, b0.x, b0.y)
        drawRect(t.c(ROBLE_LUZ), s.topLeft, Size(s.width, e * 0.006f))
        drawRect(t.c(ROBLE), Offset(s.left, s.top + e * 0.006f), Size(s.width, s.height - e * 0.006f))
        vetas(Rect(s.left, s.top + e * 0.006f, s.right, s.bottom), ROBLE_OSC.copy(alpha = 0.3f), 603, 0.5f, grosor = 0.5f, ondulacion = 0.3f)
        drawRect(t.s(0.35f), Offset(if (o) s.left else s.right - e * 0.008f, s.top), Size(e * 0.008f, s.height))
        drawRect(t.b(0.45f), s.topLeft, Size(s.width, 0.9f * u))
        drawLine(t.s(0.3f), Offset(s.left, s.top + e * 0.006f), Offset(s.right, s.top + e * 0.006f), 0.8f * u)
        drawLine(t.c(TINTA).copy(alpha = 0.55f), Offset(s.left + s.width * 0.3f, s.top + e * 0.006f), Offset(s.left + s.width * 0.32f, s.bottom - e * 0.01f), 1.6f * u, StrokeCap.Round)
        drawLine(t.c(BERMELLON).copy(alpha = 0.5f), Offset(s.left + s.width * 0.74f, s.top + e * 0.006f), Offset(s.left + s.width * 0.73f, s.top + s.height * 0.5f), 1.3f * u, StrokeCap.Round)
        // El cortaplumas, tumbado delante: hoja de hierro y mango de hueso.
        val a = pt(w, 0.556f, 0.402f); val b = pt(w, 0.592f, 0.3995f); val c = pt(w, 0.62f, 0.3975f)
        drawLine(t.s(0.35f), a + Offset(1f * u, 1.6f * u), c + Offset(1f * u, 1.6f * u), 3f * u)
        drawPath(poligono(a.x, a.y, b.x, b.y - e * 0.003f, b.x, b.y + e * 0.0012f, a.x + e * 0.004f, a.y + e * 0.001f), t.c(mezcla(HIERRO_LUZ, PIEDRA, 0.4f)))
        drawLine(t.b(0.6f), a, b - Offset(0f, e * 0.003f), 0.6f * u)
        drawLine(t.c(mezcla(CERA, CUERNO, 0.5f)), b, c, 3.2f * u, StrokeCap.Round)
        drawLine(t.b(0.4f), b - Offset(0f, 1f * u), c - Offset(0f, 1f * u), 0.6f * u)
    }

    /** Una concha de [ancho] con su pigmento, apoyada en [c]. */
    private fun DrawScope.concha(c: Offset, ancho: Float, pigmento: Color, t: Tono, semilla: Int) {
        val u = density
        val alto = ancho * 0.44f
        contacto(Offset(c.x + ancho * 0.06f, c.y), ancho * 1.15f, 2.8f * u, t, 0.38f)
        val cuerpo = Path().apply {
            moveTo(c.x - ancho / 2, c.y - alto * 0.75f)
            cubicTo(c.x - ancho * 0.45f, c.y + alto * 0.05f, c.x + ancho * 0.45f, c.y + alto * 0.05f, c.x + ancho / 2, c.y - alto * 0.75f)
            close()
        }
        val nacar = mezcla(CAL, TERRACOTA, 0.22f + 0.1f * azar(semilla, 1))
        drawPath(cuerpo, t.c(nacar))
        clipPath(cuerpo) {
            drawRect(t.s(0.22f), Offset(if (t.o) c.x - ancho * 1.1f else c.x + ancho * 0.1f, c.y - alto), Size(ancho, alto * 1.2f))
            for (j in -3..3) drawLine(t.s(0.24f), Offset(c.x + j * ancho * 0.07f, c.y + alto * 0.1f), Offset(c.x + j * ancho * 0.16f, c.y - alto), 0.7f * u)
        }
        drawOval(t.c(mezcla(nacar, LUZ_MUEBLE, 0.3f)), Offset(c.x - ancho / 2, c.y - alto * 0.95f), Size(ancho, alto * 0.42f))
        drawOval(t.c(pigmento), Offset(c.x - ancho * 0.38f, c.y - alto * 0.88f), Size(ancho * 0.76f, alto * 0.28f))
        drawOval(t.b(0.35f), Offset(c.x - ancho * 0.25f, c.y - alto * 0.86f), Size(ancho * 0.25f, alto * 0.1f))
        drawArc(t.b(0.6f), 180f, 80f, false, Offset(c.x - ancho / 2, c.y - alto * 0.95f), Size(ancho, alto * 0.42f), style = Stroke(0.7f * u))
    }

    /** Lo del iluminador: tres conchas con pigmento, el cuenco del pan de oro con su bruñidor y la piedra pómez. */
    private fun DrawScope.iluminador(w: Float, o: Boolean) {
        val u = density
        val t = Tono(o, if (o) luz(Offset(0.3f, 0.38f)) else 0f)
        val ti = Tono(o, if (o) luz(Offset(0.22f, 0.38f)) else 0f)
        concha(Offset(w * 0.264f, w * 0.3915f), w * 0.052f, mezcla(VERDIGRIS, CAL, 0.15f), ti, 3)
        concha(Offset(w * 0.228f, w * 0.4005f), w * 0.068f, mezcla(BERMELLON, Color(0xFFD9502E), 0.45f), ti, 1)
        concha(Offset(w * 0.293f, w * 0.4015f), w * 0.058f, AZURITA, t, 2)
        // El cuenco de barro con el pan de oro.
        val cc = Offset(w * 0.342f, w * 0.3995f)
        val an = w * 0.072f
        contacto(Offset(cc.x + w * 0.004f, cc.y), an * 1.2f, 3.2f * u, t, 0.4f)
        val cuenco = Path().apply {
            moveTo(cc.x - an / 2, cc.y - an * 0.36f)
            cubicTo(cc.x - an * 0.48f, cc.y - an * 0.02f, cc.x - an * 0.3f, cc.y, cc.x, cc.y)
            cubicTo(cc.x + an * 0.3f, cc.y, cc.x + an * 0.48f, cc.y - an * 0.02f, cc.x + an / 2, cc.y - an * 0.36f)
            close()
        }
        drawPath(cuenco, t.c(TERRACOTA))
        clipPath(cuenco) {
            drawRect(t.s(0.3f), Offset(if (o) cc.x - an * 1.12f else cc.x + an * 0.12f, cc.y - an), Size(an, an))
            drawRect(t.b(0.3f), Offset(if (o) cc.x + an * 0.32f else cc.x - an * 0.42f, cc.y - an), Size(an * 0.1f, an))
            drawRect(t.s(0.16f), Offset(cc.x - an, cc.y - an * 0.2f), Size(an * 2, 0.8f * u))
        }
        drawOval(t.c(mezcla(TERRACOTA, ROBLE_OSC, 0.55f)), Offset(cc.x - an / 2, cc.y - an * 0.44f), Size(an, an * 0.17f))
        for (j in 0 until 5) {
            val p = Offset(cc.x - an * 0.3f + an * 0.15f * j, cc.y - an * 0.365f + (azar(611, j) - 0.5f) * an * 0.05f)
            rotate(24f * (azar(612, j) - 0.5f), p) { drawRect(if (o) mezcla(t.c(ORO), LLAMA, 0.45f) else mezcla(ORO, LUZ_MUEBLE, 0.3f), Offset(p.x - an * 0.08f, p.y - an * 0.03f), Size(an * 0.16f, an * 0.055f)) }
        }
        drawArc(t.b(0.55f), 180f, 70f, false, Offset(cc.x - an / 2, cc.y - an * 0.44f), Size(an, an * 0.17f), style = Stroke(0.7f * u))
        // El bruñidor, cruzado sobre el borde: el mango de madera y el diente de ágata en la punta.
        val m0 = Offset(w * 0.302f, w * 0.342f); val m1 = Offset(w * 0.378f, w * 0.378f)
        drawLine(t.s(0.3f), m0 + Offset(1.4f * u, 1.8f * u), m1 + Offset(1.4f * u, 1.8f * u), 2.8f * u, StrokeCap.Round)
        drawLine(t.c(ROBLE_LUZ), m0 + (m1 - m0) * 0.25f, m1, 2.8f * u, StrokeCap.Round)
        drawLine(t.b(0.4f), m0 + (m1 - m0) * 0.25f - Offset(0f, 0.8f * u), m1 - Offset(0f, 0.8f * u), 0.6f * u)
        drawLine(t.c(mezcla(HIERRO_LUZ, ORO, 0.4f)), m0 + (m1 - m0) * 0.22f, m0 + (m1 - m0) * 0.3f, 3.2f * u)
        drawLine(t.c(mezcla(CERA, BADANA, 0.35f)), m0, m0 + (m1 - m0) * 0.22f, 3.6f * u, StrokeCap.Round)
        drawCircle(t.b(0.7f), 0.8f * u, m0 + Offset(0.4f * u, -0.7f * u))
        // La piedra pómez, porosa, delante de la tabla de cera.
        val pp = Offset(w * 0.186f, w * 0.3935f)
        contacto(Offset(pp.x + w * 0.003f, w * 0.4f), w * 0.04f, 2.8f * u, ti, 0.38f)
        val pomez = borron(pp, w * 0.019f, w * 0.0095f, 621, 10)
        drawPath(pomez, ti.c(mezcla(PIEDRA, CAL, 0.4f)))
        clipPath(pomez) {
            drawRect(ti.s(0.25f), Offset(if (o) pp.x - w * 0.024f else pp.x + w * 0.003f, pp.y - w * 0.012f), Size(w * 0.022f, w * 0.024f))
            for (j in 0 until 11) drawCircle(ti.s(0.35f), 0.65f * u, Offset(pp.x + w * 0.03f * (azar(622, j) - 0.5f), pp.y + w * 0.014f * (azar(623, j) - 0.5f)))
        }
    }

    /** La regla, tumbada al borde del tablero, que sobresale del canto por la izquierda. */
    private fun DrawScope.regla(w: Float, o: Boolean) {
        val u = density
        val t = Tono(o, if (o) luz(Offset(0.12f, 0.4f)) else 0f)
        val r0 = Offset(w * 0.026f, w * 0.4065f); val r1 = Offset(w * 0.222f, w * 0.4035f)
        drawLine(t.s(0.3f), r0 + Offset(2f * u, w * 0.011f), r1 + Offset(2f * u, 2.4f * u), 2.6f * u)
        drawLine(t.c(mezcla(ROBLE_LUZ, CAL, 0.25f)), r0, r1, 3f * u)
        drawLine(t.b(0.5f), r0 - Offset(0f, 1.1f * u), r1 - Offset(0f, 1.1f * u), 0.6f * u)
        for (j in 1..11) {
            val p = r0 + (r1 - r0) * (j / 12f)
            drawLine(t.s(0.4f), p - Offset(0f, 1.4f * u), p + Offset(0f, if (j % 4 == 0) 1.2f * u else 0.2f * u), 0.5f * u)
        }
    }

    private const val GIRO_TABLA = -6f
    private fun tabla(w: Float) = Rect(w * 0.086f, w * 0.262f, w * 0.182f, w * 0.397f)

    private fun siluetaTabla(w: Float): Path {
        val r = tabla(w); val c = Offset(r.center.x, r.bottom)
        val pts = listOf(Offset(r.left - w * 0.012f, r.top - w * 0.006f), Offset(r.right, r.top - w * 0.006f), r.bottomRight, Offset(r.left - w * 0.012f, r.bottom)).map { girar(it, c, GIRO_TABLA) }
        return poligono(pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x, pts[2].y, pts[3].x, pts[3].y)
    }

    /** La tabla de cera apoyada en la pared: el marco de madera, la cera verde negra con lo escrito a punzón, su hermana detrás y el punzón colgando. */
    private fun DrawScope.tablaDeCera(w: Float, o: Boolean) {
        val u = density
        val r = tabla(w)
        val t = Tono(o, if (o) luz(Offset(0.14f, 0.34f)) else 0f)
        contacto(Offset(r.center.x + w * 0.004f, w * 0.399f), r.width * 1.25f, 3.2f * u, t, 0.38f)
        rotate(GIRO_TABLA, Offset(r.center.x, r.bottom)) {
            // La otra hoja, detrás, unida por las tiras de cuero.
            drawRect(t.c(mezcla(ROBLE, ROBLE_OSC, 0.4f)), Offset(r.left - w * 0.012f, r.top - w * 0.006f), r.size)
            drawRect(t.b(0.3f), Offset(r.left - w * 0.012f, r.top - w * 0.006f), Size(r.width, 0.9f * u))
            listOf(0.25f, 0.75f).forEach { f -> drawRect(t.c(mezcla(BADANA, ROBLE_OSC, 0.3f)), Offset(r.left - w * 0.006f, r.top + r.height * f - w * 0.004f), Size(w * 0.009f, w * 0.008f)) }
            drawRect(t.c(ROBLE_LUZ), r.topLeft, r.size)
            vetas(r, ROBLE_OSC.copy(alpha = 0.3f), 633, 0.4f, vertical = true, grosor = 0.5f, ondulacion = 0.3f)
            drawRect(t.b(0.5f), r.topLeft, Size(r.width, 0.9f * u))
            drawRect(t.b(0.35f), r.topLeft, Size(0.9f * u, r.height))
            drawRect(t.s(0.35f), Offset(r.right - 1.3f * u, r.top), Size(1.3f * u, r.height))
            val cera = Rect(r.left + w * 0.009f, r.top + w * 0.009f, r.right - w * 0.009f, r.bottom - w * 0.009f)
            drawRect(t.c(mezcla(VERDIGRIS, NOCHE, 0.62f)), cera.topLeft, cera.size)
            drawRect(t.s(0.45f), cera.topLeft, Size(cera.width, 1.4f * u))
            drawRect(t.s(0.35f), cera.topLeft, Size(1.2f * u, cera.height))
            drawRect(t.b(0.3f), Offset(cera.left, cera.bottom - 0.8f * u), Size(cera.width, 0.8f * u))
            for (j in 0 until 9) {
                val y = cera.top + cera.height * (0.12f + 0.09f * j)
                if (j != 5) drawLine(t.c(mezcla(VERDIGRIS, CAL, 0.55f)).copy(alpha = 0.65f), Offset(cera.left + w * 0.005f, y), Offset(cera.right - w * (0.005f + 0.022f * azar(631, j)), y), 0.7f * u)
            }
            // Lo borrado con la paleta del punzón: una mancha más lisa.
            drawOval(t.b(0.08f), Offset(cera.left + cera.width * 0.2f, cera.top + cera.height * 0.52f), Size(cera.width * 0.6f, cera.height * 0.12f))
            // El punzón, colgado de su correa de un lado del marco.
            val a = Offset(r.right - w * 0.004f, r.top + w * 0.012f)
            drawLine(t.c(mezcla(BADANA, ROBLE_OSC, 0.3f)), a, a + Offset(w * 0.012f, w * 0.03f), 0.9f * u)
            drawLine(t.s(0.3f), a + Offset(w * 0.013f, w * 0.031f), a + Offset(w * 0.015f, w * 0.085f), 1.6f * u)
            drawLine(t.c(mezcla(HIERRO_LUZ, PIEDRA, 0.3f)), a + Offset(w * 0.012f, w * 0.03f), a + Offset(w * 0.014f, w * 0.084f), 1.5f * u, StrokeCap.Round)
            drawRect(t.c(mezcla(HIERRO_LUZ, PIEDRA, 0.3f)), Offset(a.x + w * 0.009f, a.y + w * 0.028f), Size(w * 0.007f, w * 0.006f))
        }
    }

    // ---------------------------------------------------------------- el armario

    /** El tablero de arriba, el friso con su arquería pintada y la moldura de debajo. */
    private fun DrawScope.coronamiento(w: Float, o: Boolean) {
        val u = density
        fun tn(x: Float, y: Float) = Tono(o, if (o) luz(Offset(x / w, y / w)) else 0f)
        // La sombra del tablero en el muro, a la derecha.
        drawPath(poligono(w * TR, w * TAPA, w * (TR + 0.018f), w * (TAPA + 0.008f), w * (TR + 0.018f), w * (CANTO_B + 0.01f), w * TR, w * CANTO_B),
            if (o) NOCHE.copy(alpha = 0.35f) else sombraMueble(0.25f))
        // El friso, por tramos (de noche, más encendido junto a la vela).
        val fr = Rect(w * (BL - 0.006f), w * CANTO_B, w * (BR + 0.006f), w * FRISO_B)
        val tramos = 12
        val aw = fr.width / tramos
        for (j in 0 until tramos) {
            val x = fr.left + aw * j
            val t = tn(x + aw / 2, fr.center.y)
            drawRect(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.35f)), Offset(x, fr.top), Size(aw + 0.6f, fr.height))
            val ar = Rect(x + aw * 0.16f, fr.top + fr.height * 0.2f, x + aw * 0.84f, fr.bottom - fr.height * 0.14f)
            // La columnita pintada entre arco y arco, el campo y su filete de ocre.
            drawRect(t.c(mezcla(ORO, CAL, 0.4f)), Offset(x - aw * 0.05f, ar.top + ar.width * 0.4f), Size(aw * 0.1f, ar.bottom - ar.top - ar.width * 0.4f))
            val campo = mezcla(if (j % 2 == 0) ALMAGRE else AZURITA, ROBLE, 0.08f + 0.14f * azar(699, j))
            drawPath(arcoMedioPunto(ar), t.c(campo))
            // La luz de arriba a la izquierda deja en sombra el canto de arriba de cada campo (está un poco rehundido).
            drawArc(t.s(0.3f), 180f, 180f, false, Offset(ar.left + 0.8f * u, ar.top + 0.8f * u), Size(ar.width - 1.6f * u, ar.width - 1.6f * u), style = Stroke(1.4f * u))
            drawPath(arcoMedioPunto(ar), t.c(mezcla(ORO, CAL, 0.4f)), style = Stroke(1.1f * u))
            drawCircle(t.c(ORO), 1.4f * u, Offset(ar.center.x, ar.top + ar.width * 0.62f))
            // La pintura saltada: la madera asoma.
            for (q in 0 until 3) if (azar(700 + j, q) > 0.3f) {
                val p = Offset(x + aw * azar(710 + j, q), fr.top + fr.height * (0.25f + 0.6f * azar(720 + j, q)))
                drawPath(borron(p, aw * (0.08f + 0.12f * azar(730 + j, q)), fr.height * (0.06f + 0.08f * azar(735 + j, q)), 740 + j * 3 + q, 8), t.c(mezcla(ROBLE, ROBLE_LUZ, 0.3f)).copy(alpha = 0.92f))
            }
        }
        vetas(fr, ROBLE_OSC.copy(alpha = 0.2f), 751, 0.5f, grosor = 0.5f, ondulacion = 0.3f)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.12f), Color.Transparent, sombraMueble(0.2f)), fr.left, fr.right), fr.topLeft, fr.size)
        // La sombra del canto del tablero, que vuela, sobre el friso.
        drawRect(if (o) NOCHE.copy(alpha = 0.4f) else sombraMueble(0.32f), fr.topLeft, Size(fr.width, w * 0.009f))
        drawRect(if (o) NOCHE.copy(alpha = 0.4f) else sombraMueble(0.3f), Offset(fr.right - w * 0.012f, fr.top), Size(w * 0.012f, fr.height))
        // La moldura de debajo: un bocel.
        for (j in 0 until 6) {
            val x = fr.left + fr.width * j / 6f
            val t = tn(x + fr.width / 12f, w * FRISO_B)
            drawRect(t.c(mezcla(ROBLE, ROBLE_OSC, 0.3f)), Offset(x, w * FRISO_B), Size(fr.width / 6f + 0.6f, w * (MOLD_B - FRISO_B)))
            drawRect(t.b(0.45f), Offset(x, w * FRISO_B + w * 0.003f), Size(fr.width / 6f + 0.6f, 1f * u))
        }
        drawRect(if (o) NOCHE.copy(alpha = 0.5f) else sombraMueble(0.35f), Offset(fr.left, w * MOLD_B - 1.4f * u), Size(fr.width, 1.4f * u))
        // El tablero: su cara de arriba (gastada, con cera y tinta), y su canto.
        for (j in 0 until 10) {
            val x0 = w * TL + w * (TR - TL) * j / 10f; val x1 = w * TL + w * (TR - TL) * (j + 1) / 10f
            val t = tn((x0 + x1) / 2, w * TAPA_F)
            drawRect(t.c(mezcla(ROBLE_LUZ, LUZ_MUEBLE, 0.12f)), Offset(x0, w * TAPA), Size(x1 - x0 + 0.6f, w * (TAPA_F - TAPA)))
            drawRect(t.c(ROBLE), Offset(x0, w * TAPA_F), Size(x1 - x0 + 0.6f, w * (CANTO_B - TAPA_F)))
        }
        vetas(Rect(w * TL, w * TAPA, w * TR, w * TAPA_F), ROBLE_OSC.copy(alpha = 0.25f), 761, 0.8f, grosor = 0.5f, ondulacion = 0.3f)
        vetas(Rect(w * TL, w * TAPA_F, w * TR, w * CANTO_B), ROBLE_OSC.copy(alpha = 0.22f), 762, 0.8f, grosor = 0.5f, ondulacion = 0.3f)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.2f), Color.Transparent, sombraMueble(0.16f)), w * TL, w * TR), Offset(w * TL, w * TAPA), Size(w * (TR - TL), w * (CANTO_B - TAPA)))
        val tc = tn(w * 0.5f, w * TAPA_F)
        drawRect(tc.b(0.55f), Offset(w * TL, w * TAPA_F), Size(w * (TR - TL), 1.1f * u))
        drawLine(tc.s(0.35f), Offset(w * TL, w * (TAPA_F + CANTO_B) / 2), Offset(w * TR, w * (TAPA_F + CANTO_B) / 2), 0.9f * u)
        drawRect(tc.s(0.4f), Offset(w * TL, w * CANTO_B - 1.2f * u), Size(w * (TR - TL), 1.2f * u))
        drawRect(tc.s(0.35f), Offset(w * TR - 1.4f * u, w * TAPA), Size(1.4f * u, w * (CANTO_B - TAPA)))
        drawRect(Tono(o, 0f).b(0.3f), Offset(w * TL, w * TAPA), Size(1.2f * u, w * (CANTO_B - TAPA)))
        // La cera caída junto a la palmatoria (una gota llegó al canto y bajó).
        listOf(-0.036f to 0.006f, 0.04f to 0.011f, -0.02f to 0.0125f).forEach { (dx, dy) ->
            drawOval(Tono(o, 0.9f).c(CERA), Offset(w * (LUZ_VELA.x + dx) - w * 0.005f, w * (TAPA + dy) - w * 0.0018f), Size(w * 0.01f, w * 0.0036f))
        }
        drawLine(Tono(o, 0.9f).c(CERA), Offset(w * (LUZ_VELA.x + 0.026f), w * (TAPA_F - 0.002f)), Offset(w * (LUZ_VELA.x + 0.0265f), w * (TAPA_F + 0.009f)), 1.6f * u, StrokeCap.Round)
        // De noche, el charco de luz de la vela en el tablero.
        if (o) clipRect(w * TL, w * TAPA, w * TR, w * CANTO_B) {
            withTransform({ scale(1f, 0.16f, Offset(w * LUZ_VELA.x, w * TAPA_F)) }) {
                drawCircle(Brush.radialGradient(listOf(LLAMA.copy(alpha = 0.5f), LLAMA.copy(alpha = 0.18f), Color.Transparent), Offset(w * LUZ_VELA.x, w * TAPA_F), w * 0.32f), w * 0.32f, Offset(w * LUZ_VELA.x, w * TAPA_F))
            }
        }
    }

    /** Una roseta hexapétala tallada en [c], de radio [r]: seis arcos hundidos. */
    private fun DrawScope.roseta(c: Offset, r: Float, t: Tono) {
        val u = density
        for (i in 0 until 6) {
            val a = Math.toRadians(i * 60.0)
            val p = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
            val ini = (i * 60f + 120f)
            translate(0.7f * u, 0.7f * u) { drawArc(t.b(0.35f), ini, 120f, false, Offset(p.x - r, p.y - r), Size(2 * r, 2 * r), style = Stroke(0.7f * u)) }
            drawArc(t.s(0.5f), ini, 120f, false, Offset(p.x - r, p.y - r), Size(2 * r, 2 * r), style = Stroke(0.8f * u))
        }
        drawCircle(t.s(0.45f), r, c, style = Stroke(0.8f * u))
    }

    /** El cuerpo del armario: el roble por franjas (de noche, más apagado cuanto más abajo), los costados y los travesaños. */
    private fun DrawScope.cuerpo(d: Disposicion, w: Float, y1: Float, o: Boolean) {
        val u = density
        val y0 = w * MOLD_B
        var y = y0
        while (y < y1) {
            val y2 = minOf(y + w * 0.025f, y1)
            val t = Tono(o, if (o) luz(Offset(0.5f, (y + y2) / 2 / w)) else 0f, diaSegunAltura((y - y0) / (y1 - y0)))
            drawRect(t.c(ROBLE), Offset(w * BL, y), Size(w * (BR - BL), y2 - y + 0.6f))
            y = y2
        }
        vetas(Rect(w * BL, y0, w * BR, y1), ROBLE_OSC.copy(alpha = if (o) 0.22f else 0.35f), 13, 0.6f, vertical = true, grosor = 0.6f, ondulacion = 0.4f)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.14f), Color.Transparent, sombraMueble(0.16f)), w * BL, w * BR), Offset(w * BL, y0), Size(w * (BR - BL), y1 - y0))
        // Los costados: el izquierdo con su canto a la luz, el derecho en sombra.
        drawRect(Tono(o, 0f).b(0.3f), Offset(w * BL, y0), Size(1.3f * u, y1 - y0))
        drawRect(if (o) NOCHE.copy(alpha = 0.35f) else sombraMueble(0.3f), Offset(w * BR - w * 0.008f, y0), Size(w * 0.008f, y1 - y0))
        // Los travesaños entre filas: la tabla con su filo de luz arriba y su sombra debajo.
        d.filas.forEach { f ->
            val t = Tono(o, if (o) luz(Offset(0.5f, f.top / w)) else 0f)
            val tb = Tono(o, if (o) luz(Offset(0.5f, f.bottom / w)) else 0f, diaSegunAltura((f.bottom - y0) / (y1 - y0)))
            // La sombra que el travesaño de arriba deja sobre la fila.
            drawRect(t.s(0.3f), Offset(w * BL, f.top - 1.6f * u), Size(w * (BR - BL), 1.6f * u))
            // El de abajo: el canto de la tabla, que vuela un poco.
            drawRect(tb.c(mezcla(ROBLE, ROBLE_LUZ, 0.25f)), Offset(w * BL, f.bottom), Size(w * (BR - BL), d.separacion * 0.6f))
            drawRect(tb.b(0.5f), Offset(w * BL, f.bottom), Size(w * (BR - BL), 1f * u))
            drawRect(tb.s(0.4f), Offset(w * BL, f.bottom + d.separacion * 0.6f), Size(w * (BR - BL), 1.6f * u))
            drawRect(tb.s(0.18f), Offset(w * BL, f.bottom + d.separacion * 0.6f + 1.6f * u), Size(w * (BR - BL), d.separacion * 0.25f))
            // Las clavijas de las uniones.
            listOf(w * (BL + 0.012f), w * 0.5f, w * (BR - 0.012f)).forEach { x ->
                drawCircle(tb.c(ROBLE_OSC), 1.4f * u, Offset(x, f.bottom + d.separacion * 0.3f))
                drawCircle(tb.b(0.3f), 0.5f * u, Offset(x - 0.5f * u, f.bottom + d.separacion * 0.3f - 0.5f * u))
            }
        }
        // El montante del centro, entre las dos calles: una junta a cada lado.
        if (d.huecos.size >= 2) {
            val xa = d.huecos[0].right; val xb = d.huecos[1].left
            d.filas.forEach { f ->
                val t = Tono(o, if (o) luz(Offset(0.5f, f.center.y / w)) else 0f, diaSegunAltura((f.center.y - y0) / (y1 - y0)))
                drawRect(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.12f)), Offset(xa, f.top), Size(xb - xa, f.height))
                drawRect(t.b(0.3f), Offset(xa, f.top), Size(1f * u, f.height))
                drawRect(t.s(0.3f), Offset(xb - 1.2f * u, f.top), Size(1.2f * u, f.height))
            }
        }
    }

    /** Una voluta de hierro: el brazo que sale de [a] a lo largo de [l] y se enrosca hacia arriba ([s] = -1) o hacia abajo ([s] = 1). */
    private fun voluta(a: Offset, l: Float, s: Float): Path = Path().apply {
        moveTo(a.x, a.y)
        cubicTo(a.x + l * 0.4f, a.y, a.x + l * 0.8f, a.y + s * l * 0.1f, a.x + l * 0.85f, a.y + s * l * 0.32f)
        cubicTo(a.x + l * 0.88f, a.y + s * l * 0.52f, a.x + l * 0.6f, a.y + s * l * 0.56f, a.x + l * 0.55f, a.y + s * l * 0.4f)
        cubicTo(a.x + l * 0.52f, a.y + s * l * 0.3f, a.x + l * 0.64f, a.y + s * l * 0.26f, a.x + l * 0.68f, a.y + s * l * 0.34f)
    }

    /**
     * Una puerta abierta del todo, doblada hacia la pared: [bisagra] es la x del canto de las bisagras y [libre] la del canto
     * suelto (más lejos: se ve más corto), de [y0] a [y1]. De tablas, con sus herrajes de hierro en volutas.
     */
    private fun DrawScope.puerta(bisagra: Float, libre: Float, y0: Float, y1: Float, w: Float, o: Boolean, luzDeFrente: Boolean) {
        val u = density
        val enc = w * 0.014f
        val p = poligono(bisagra, y0, libre, y0 + enc, libre, y1 - enc, bisagra, y1)
        val dir = if (libre < bisagra) -1f else 1f
        // Su sombra en la pared, del lado de fuera (a la derecha solo de día).
        if (!o && dir > 0) drawPath(poligono(libre, y0 + enc, libre + w * 0.02f, y0 + enc + w * 0.02f, libre + w * 0.02f, y1 - enc + w * 0.03f, libre, y1 - enc), sombraMueble(0.28f))
        // Las tablas, por franjas de alto (de noche, más apagadas abajo).
        clipPath(p) {
            var y = y0
            while (y < y1) {
                val y2 = minOf(y + w * 0.03f, y1)
                val t = Tono(o, if (o) luz(Offset(bisagra / w, (y + y2) / 2 / w)) * 0.85f else 0f, diaSegunAltura((y - y0) / (y1 - y0)))
                drawRect(t.c(if (luzDeFrente) mezcla(ROBLE, ROBLE_LUZ, 0.3f) else mezcla(ROBLE, ROBLE_OSC, 0.3f)), Offset(minOf(bisagra, libre), y), Size(abs(libre - bisagra), y2 - y + 0.6f))
                y = y2
            }
            val tt = Tono(o, if (o) 0.2f else 0f)
            for (j in 1..3) {
                val x = bisagra + (libre - bisagra) * j / 4f
                drawLine(tt.s(0.4f), Offset(x, y0), Offset(x, y1), 0.8f * u)
                drawLine(tt.b(0.18f), Offset(x + 0.8f * u, y0), Offset(x + 0.8f * u, y1), 0.5f * u)
            }
            vetas(Rect(minOf(bisagra, libre), y0, maxOf(bisagra, libre), y1), ROBLE_OSC.copy(alpha = 0.25f), (bisagra / u).toInt(), 0.5f, vertical = true, grosor = 0.5f, ondulacion = 0.3f)
            // El lado de las bisagras recibe la sombra del cuerpo del armario.
            drawRect(tt.s(0.22f), Offset(if (dir < 0) bisagra - w * 0.012f else bisagra, y0), Size(w * 0.012f, y1 - y0))
        }
        // El canto suelto: el grueso de la puerta.
        drawRect(Tono(o, 0.15f).c(if (dir < 0) ROBLE_LUZ else ROBLE_OSC), Offset(if (dir < 0) libre - w * 0.004f else libre, y0 + enc), Size(w * 0.004f, y1 - y0 - 2 * enc))
        // Los herrajes: un brazo desde las bisagras, apretado por el escorzo, que se abre en dos volutas grandes y dos chicas.
        val ancho = abs(libre - bisagra)
        (if (y1 - y0 < w * 0.7f) listOf(0.2f, 0.8f) else listOf(0.1f, 0.5f, 0.9f)).forEach { f ->
            val y = y0 + (y1 - y0) * f
            val t = Tono(o, if (o) luz(Offset(bisagra / w, y / w)) * 0.9f else 0f)
            val hierro = t.c(HIERRO)
            withTransform({ scale(dir * ancho / (w * 0.185f), 1f, Offset(bisagra, y)) }) {
                val l = w * 0.17f
                translate(1.6f * u, 1.6f * u) {
                    drawLine(t.s(0.4f), Offset(bisagra, y), Offset(bisagra + l * 0.55f, y), w * 0.011f)
                    drawPath(voluta(Offset(bisagra + l * 0.45f, y), l * 0.6f, -1f), t.s(0.4f), style = Stroke(w * 0.0075f, cap = StrokeCap.Round))
                    drawPath(voluta(Offset(bisagra + l * 0.45f, y), l * 0.6f, 1f), t.s(0.4f), style = Stroke(w * 0.0075f, cap = StrokeCap.Round))
                }
                drawLine(hierro, Offset(bisagra, y), Offset(bisagra + l * 0.55f, y), w * 0.011f)
                drawPath(voluta(Offset(bisagra + l * 0.45f, y), l * 0.6f, -1f), hierro, style = Stroke(w * 0.0075f, cap = StrokeCap.Round))
                drawPath(voluta(Offset(bisagra + l * 0.45f, y), l * 0.6f, 1f), hierro, style = Stroke(w * 0.0075f, cap = StrokeCap.Round))
                drawPath(voluta(Offset(bisagra + l * 0.1f, y), l * 0.34f, -1f), hierro, style = Stroke(w * 0.006f, cap = StrokeCap.Round))
                drawPath(voluta(Offset(bisagra + l * 0.1f, y), l * 0.34f, 1f), hierro, style = Stroke(w * 0.006f, cap = StrokeCap.Round))
                drawLine(t.b(0.45f), Offset(bisagra, y - w * 0.004f), Offset(bisagra + l * 0.55f, y - w * 0.004f), 0.7f * u)
                for (j in 0 until 3) {
                    drawCircle(t.c(HIERRO_LUZ), w * 0.0032f, Offset(bisagra + l * (0.08f + 0.2f * j), y))
                }
            }
            // El gozne en el costado del armario.
            drawRect(hierro, Offset(bisagra - w * 0.004f * dir - w * 0.0035f, y - w * 0.015f), Size(w * 0.007f, w * 0.03f))
            drawLine(t.b(0.45f), Offset(bisagra - w * 0.004f * dir - w * 0.0025f, y - w * 0.014f), Offset(bisagra - w * 0.004f * dir - w * 0.0025f, y + w * 0.014f), 0.6f * u)
        }
    }

    /** El escabel de roble, al pie: la tabla gastada y dos pies con su arco de medio punto calado. */
    private fun DrawScope.escabel(r: Rect, o: Boolean, k: Float) {
        val u = density
        val t = Tono(o, k)
        drawOval(t.s(0.38f), Offset(r.left + r.width * 0.02f, r.bottom - 3f * u), Size(r.width * 1.12f, 6f * u))
        val tablaAlta = r.height * 0.2f
        val cara = r.height * 0.12f
        listOf(r.left + r.width * 0.08f, r.right - r.width * 0.23f).forEachIndexed { j, x ->
            val p = Rect(x, r.top + tablaAlta, x + r.width * 0.15f, r.bottom)
            drawRect(t.c(if (j == 0) mezcla(ROBLE, ROBLE_LUZ, 0.2f) else mezcla(ROBLE, ROBLE_OSC, 0.15f)), p.topLeft, p.size)
            vetas(p, ROBLE_OSC.copy(alpha = 0.35f), 840 + j, 0.6f, vertical = true, grosor = 0.6f, ondulacion = 0.3f)
            drawPath(arcoMedioPunto(Rect(p.left + p.width * 0.22f, p.top + p.height * 0.5f, p.right - p.width * 0.22f, p.bottom + 1f)), t.s(0.7f))
            drawRect(t.b(0.3f), p.topLeft, Size(1f * u, p.height))
            drawRect(t.s(0.3f), Offset(p.right - 1.4f * u, p.top), Size(1.4f * u, p.height))
            drawRect(t.s(0.3f), p.topLeft, Size(p.width, r.height * 0.07f))
        }
        // El travesaño que une los pies, con sus espigas pasantes y sus cuñas.
        val trav = Rect(r.left + r.width * 0.04f, r.top + tablaAlta + r.height * 0.22f, r.right - r.width * 0.04f, r.top + tablaAlta + r.height * 0.33f)
        drawRect(t.c(ROBLE), Offset(r.left + r.width * 0.2f, trav.top), Size(r.width * 0.6f, trav.height))
        drawRect(t.b(0.35f), Offset(r.left + r.width * 0.2f, trav.top), Size(r.width * 0.6f, 1f * u))
        drawRect(t.s(0.35f), Offset(r.left + r.width * 0.2f, trav.bottom), Size(r.width * 0.6f, 1.2f * u))
        listOf(trav.left, r.right - r.width * 0.08f).forEach { x ->
            drawRect(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.3f)), Offset(x, trav.top + trav.height * 0.1f), Size(r.width * 0.04f, trav.height * 0.8f))
            drawRect(t.c(ROBLE_OSC), Offset(x + r.width * 0.012f, trav.top - trav.height * 0.25f), Size(r.width * 0.014f, trav.height * 1.5f))
        }
        val arriba = Rect(r.left, r.top - cara, r.right, r.top)
        drawPath(poligono(arriba.left + r.width * 0.03f, arriba.top, arriba.right - r.width * 0.03f, arriba.top, arriba.right, arriba.bottom, arriba.left, arriba.bottom), t.c(mezcla(ROBLE_LUZ, LUZ_MUEBLE, 0.15f)))
        drawOval(t.c(mezcla(ROBLE_LUZ, LUZ_MUEBLE, 0.45f)).copy(alpha = 0.6f), Offset(r.left + r.width * 0.3f, arriba.top + cara * 0.15f), Size(r.width * 0.38f, cara * 0.7f))
        val canto = Rect(r.left, r.top, r.right, r.top + tablaAlta)
        drawRect(t.c(ROBLE), canto.topLeft, canto.size)
        vetas(canto, ROBLE_OSC.copy(alpha = 0.4f), 845, 0.6f, grosor = 0.6f, ondulacion = 0.3f)
        drawRect(t.b(0.4f), canto.topLeft, Size(canto.width, 1f * u))
        drawRect(t.s(0.4f), Offset(canto.left, canto.bottom - 1.2f * u), Size(canto.width, 1.2f * u))
        drawRect(t.s(0.3f), Offset(canto.right - 1.5f * u, canto.top), Size(1.5f * u, canto.height))
        // Una gota de tinta y un golpe en el canto.
        drawOval(t.c(TINTA).copy(alpha = 0.5f), Offset(r.left + r.width * 0.6f, arriba.top + cara * 0.3f), Size(r.width * 0.05f, cara * 0.4f))
        drawPath(poligono(r.left + r.width * 0.3f, r.top, r.left + r.width * 0.36f, r.top, r.left + r.width * 0.33f, r.top + tablaAlta * 0.45f), t.s(0.35f))
    }

    /** El cesto de mimbre con los recortes de pergamino, cortado por el borde izquierdo; uno se cayó al suelo. */
    private fun DrawScope.cesto(r: Rect, o: Boolean, k: Float) {
        val u = density
        val t = Tono(o, k)
        drawOval(t.s(0.38f), Offset(r.left + r.width * 0.05f, r.bottom - 4f * u), Size(r.width * 1.1f, 8f * u))
        // Los recortes que asoman, detrás del borde: tiras largas y recortes en cuña.
        for (j in 0 until 8) {
            val x = r.left + r.width * (0.32f + 0.085f * j)
            val alto = r.height * (0.16f + 0.26f * azar(861, j))
            val g = (azar(862, j) - 0.5f) * 46f
            rotate(g, Offset(x, r.top + r.height * 0.1f)) {
                val tira = poligono(x - r.width * 0.04f, r.top + r.height * 0.12f, x - r.width * 0.02f, r.top - alto, x + r.width * 0.035f, r.top - alto * 0.78f, x + r.width * 0.045f, r.top + r.height * 0.12f)
                drawPath(tira, t.c(if (j % 3 == 0) PERG_OSC else if (j % 3 == 1) PERGAMINO else mezcla(PERGAMINO, PERG_OSC, 0.5f)))
                drawLine(t.b(0.4f), Offset(x - r.width * 0.02f, r.top - alto), Offset(x + r.width * 0.035f, r.top - alto * 0.78f), 0.8f * u)
                drawLine(t.s(0.28f), Offset(x + r.width * 0.035f, r.top - alto * 0.78f), Offset(x + r.width * 0.045f, r.top + r.height * 0.12f), 1f * u)
            }
        }
        // El cuerpo del cesto, un poco más ancho arriba.
        val cuerpo = poligono(r.left, r.top, r.right, r.top, r.right - r.width * 0.05f, r.bottom, r.left + r.width * 0.06f, r.bottom)
        drawPath(cuerpo, t.c(mezcla(BADANA, CUERNO, 0.4f)))
        clipPath(cuerpo) {
            val filas = 7
            for (f in 0 until filas) {
                val y = r.top + r.height * 0.1f + (r.height * 0.9f) * f / filas
                val h = r.height * 0.9f / filas
                var x = r.left - (if (f % 2 == 0) 0f else r.width * 0.05f); var j = 0
                while (x < r.right) {
                    drawOval(t.c(mezcla(BADANA, CUERNO, 0.15f + 0.3f * azar(870 + f, j))), Offset(x, y + h * 0.08f), Size(r.width * 0.1f, h * 0.84f))
                    drawArc(t.b(0.35f), 200f, 100f, false, Offset(x, y + h * 0.08f), Size(r.width * 0.1f, h * 0.84f), style = Stroke(0.7f * u))
                    x += r.width * 0.1f; j++
                }
                drawLine(t.s(0.35f), Offset(r.left, y), Offset(r.right, y), 0.8f * u)
            }
            drawRect(t.s(0.3f), Offset(r.left + r.width * 0.72f, r.top), Size(r.width, r.height))
            drawRect(t.s(0.14f), Offset(r.left + r.width * 0.58f, r.top), Size(r.width, r.height))
            drawRect(t.s(0.2f), Offset(r.left, r.bottom - r.height * 0.12f), Size(r.width, r.height * 0.12f))
        }
        // El borde trenzado.
        val borde = Rect(r.left - r.width * 0.02f, r.top - r.height * 0.02f, r.right + r.width * 0.02f, r.top + r.height * 0.11f)
        drawRoundRect(t.c(mezcla(BADANA, CUERNO, 0.25f)), borde.topLeft, borde.size, CornerRadius(borde.height / 2))
        var x = borde.left
        while (x < borde.right) { drawLine(t.s(0.35f), Offset(x, borde.top), Offset(x + borde.height * 0.6f, borde.bottom), 0.9f * u); x += borde.height * 0.55f }
        drawLine(t.b(0.45f), Offset(borde.left, borde.top + 0.9f * u), Offset(borde.right - borde.height * 0.4f, borde.top + 0.9f * u), 0.8f * u)
        drawRoundRect(t.s(0.25f), Offset(borde.right - borde.width * 0.2f, borde.top), Size(borde.width * 0.2f, borde.height), CornerRadius(borde.height / 2))
        // Dos recortes en el suelo.
        listOf(Offset(r.right + r.width * 0.3f, r.bottom - r.height * 0.16f) to 14f, Offset(r.right + r.width * 0.66f, r.bottom - r.height * 0.4f) to -20f).forEach { (c, g) ->
            drawOval(t.s(0.3f), Offset(c.x - r.width * 0.09f, c.y - 0.5f * u), Size(r.width * 0.24f, 4f * u))
            rotate(g, c) {
                drawPath(poligono(c.x - r.width * 0.11f, c.y, c.x + r.width * 0.01f, c.y - r.height * 0.075f, c.x + r.width * 0.13f, c.y - r.height * 0.04f, c.x + r.width * 0.01f, c.y + r.height * 0.035f), t.c(PERGAMINO))
                drawLine(t.s(0.25f), Offset(c.x - r.width * 0.11f, c.y), Offset(c.x + r.width * 0.01f, c.y + r.height * 0.035f), 0.8f * u)
            }
        }
    }

    /** La llama de la vela, con su resplandor. */
    private fun DrawScope.llama(p: Offset, s: Float) {
        mancha(p - Offset(0f, s * 0.6f), s * 6f, LLAMA.copy(alpha = 0.4f))
        drawPath(Path().apply {
            moveTo(p.x, p.y - s * 1.9f)
            cubicTo(p.x + s * 0.55f, p.y - s * 0.85f, p.x + s * 0.5f, p.y, p.x, p.y + s * 0.15f)
            cubicTo(p.x - s * 0.5f, p.y, p.x - s * 0.55f, p.y - s * 0.85f, p.x, p.y - s * 1.9f)
            close()
        }, LLAMA)
        drawPath(Path().apply {
            moveTo(p.x, p.y - s * 1.15f)
            cubicTo(p.x + s * 0.3f, p.y - s * 0.6f, p.x + s * 0.26f, p.y - s * 0.05f, p.x, p.y + s * 0.02f)
            cubicTo(p.x - s * 0.26f, p.y - s * 0.05f, p.x - s * 0.3f, p.y - s * 0.6f, p.x, p.y - s * 1.15f)
            close()
        }, Color(0xFFFFF4D6))
        drawOval(AZURITA.copy(alpha = 0.5f), Offset(p.x - s * 0.22f, p.y - s * 0.12f), Size(s * 0.44f, s * 0.26f))
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val o = oscuro
        val w = size.width; val alto = size.height
        val b = d.base; val hb = b.height
        val rincon = b.top + hb * 0.2f
        val zoc = b.top + hb * 0.3f
        val pie = b.top + hb * 0.42f
        // Lo que llega de la vela al zócalo y al suelo: casi nada con el armario entero, algo más si tiene pocas filas.
        val kZoc = if (o) luz(Offset(0.5f, b.top / w)).coerceAtLeast(0.04f) else 0f
        val kPie = if (o) (kZoc * 0.5f).coerceAtLeast(0.04f) else 0f

        // ---- el muro, el arco ciego, las faltas, la imposta y el hollín de la vela
        pared(w, rincon + 1f * u, o)
        arcoCiego(w, o)
        falta(Offset(w * 0.012f, w * 0.045f), w * 0.04f, w * 0.026f, 903, w, o)
        falta(Offset(w * 0.03f, w * 0.345f), w * 0.024f, w * 0.016f, 904, w, o)
        falta(Offset(w * 0.455f, w * 0.215f), w * 0.022f, w * 0.014f, 901, w, o)
        falta(Offset(w * 0.99f, w * 0.3f), w * 0.03f, w * 0.024f, 902, w, o)
        imposta(w, o)
        hollin(w, o)

        // ---- las sombras en el muro de lo que tiene delante
        sombraEnMuro(siluetaPlumas(w), w, o, 0.2f)
        sombraEnMuro(siluetaAtril(w), w, o, 0.6f, 0.95f)
        sombraEnMuro(siluetaCuernos(w), w, o, 1f)
        sombraEnMuro(siluetaTabla(w), w, o, 0.6f)
        if (!o) sombraEnMuro(Path().apply { addRect(Rect(w * (LUZ_VELA.x - 0.011f), w * (LUZ_VELA.y + 0.02f), w * (LUZ_VELA.x + 0.011f), w * 0.396f)) }, w, o, 1f)

        // ---- lo colgado
        bastidor(w, o)
        manojoPlumas(w, o)

        // ---- el suelo de baldosas de barro
        var y = rincon
        while (y < alto) {
            val y2 = minOf(y + w * 0.02f, alto)
            val f = (y - rincon) / (alto - rincon)
            val t = Tono(o, kPie)
            drawRect(t.c(mezcla(mezcla(TERRACOTA, SOMBRA_MUEBLE, 0.35f), mezcla(TERRACOTA, LUZ_MUEBLE, 0.1f), f)), Offset(0f, y), Size(w, y2 - y + 0.6f))
            y = y2
        }
        val ts = Tono(o, kPie)
        val juntas = listOf(0.1f, 0.26f, 0.48f, 0.76f).map { rincon + (alto - rincon) * it }
        val fuga = Offset(w * 0.5f, rincon - w * 1.1f)
        var y0 = rincon
        juntas.forEachIndexed { i, yj ->
            for (j in -7..7) {
                val xb = w * 0.5f + (j + (if (i % 2 == 0) 0f else 0.5f)) * w * 0.15f
                fun x(yy: Float) = fuga.x + (xb - fuga.x) * (yy - fuga.y) / (alto - fuga.y)
                // Cada baldosa, de su tono.
                val xn = w * 0.5f + (j + 1 + (if (i % 2 == 0) 0f else 0.5f)) * w * 0.15f
                fun x2(yy: Float) = fuga.x + (xn - fuga.x) * (yy - fuga.y) / (alto - fuga.y)
                val tono = azar(950 + i, j + 20)
                if (tono > 0.55f) drawPath(poligono(x(y0), y0, x2(y0), y0, x2(yj), yj, x(yj), yj), (if (tono > 0.8f) ts.b(0.12f) else ts.s(0.14f)))
                drawLine(ts.s(0.35f), Offset(x(y0), y0), Offset(x(yj), yj), 1f * u)
            }
            drawLine(ts.s(0.4f), Offset(0f, yj), Offset(w, yj), 1f * u)
            drawLine(ts.b(0.2f), Offset(0f, yj + 1f * u), Offset(w, yj + 1f * u), 0.6f * u)
            y0 = yj
        }
        pinceladas(Rect(0f, rincon, w, alto), if (o) NOCHE else SOMBRA_MUEBLE, 905, 16, u, if (o) 0.1f else 0.07f)
        drawRect(ts.s(0.35f), Offset(0f, rincon), Size(w, 1.6f * u))
        // La sombra del armario en el suelo, corrida a la derecha.
        drawPath(poligono(w * (BL + 0.02f), pie - 1f * u, w * (BR + 0.01f), pie - 1f * u, w * (BR + 0.06f), pie + hb * 0.12f, w * (BL + 0.05f), pie + hb * 0.12f), ts.s(0.32f))

        // ---- el cuerpo del armario, el zócalo y los pies
        cuerpo(d, w, zoc, o)
        val tz = Tono(o, kZoc, diaSegunAltura(1f))
        val z = Rect(w * (BL - 0.008f), b.top, w * (BR + 0.008f), zoc)
        drawRect(tz.c(ROBLE), z.topLeft, z.size)
        vetas(z, ROBLE_OSC.copy(alpha = 0.35f), 31, 0.8f, grosor = 0.6f, ondulacion = 0.3f)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.1f), Color.Transparent, sombraMueble(0.2f)), z.left, z.right), z.topLeft, z.size)
        val dientes = Path().apply {
            val ya = z.top + z.height * 0.4f; val yb = z.top + z.height * 0.72f
            val paso = w * 0.024f
            var x = z.left + w * 0.02f; var k = 0
            moveTo(x, yb)
            while (x + paso < z.right - w * 0.02f) { x += paso; k++; lineTo(x, if (k % 2 == 1) ya else yb) }
        }
        translate(0.9f * u, 1f * u) { drawPath(dientes, Tono(o, 0.1f).b(0.3f), style = Stroke(1f * u)) }
        drawPath(dientes, tz.s(0.5f), style = Stroke(1.4f * u))
        listOf(0.34f, 0.78f).forEach { fy ->
            drawRect(tz.s(0.4f), Offset(z.left + w * 0.012f, z.top + z.height * fy), Size(z.width - w * 0.024f, 1.2f * u))
            drawRect(Tono(o, 0.1f).b(0.25f), Offset(z.left + w * 0.012f, z.top + z.height * fy + 1.2f * u), Size(z.width - w * 0.024f, 0.8f * u))
        }
        drawRect(tz.c(mezcla(ROBLE, ROBLE_LUZ, 0.35f)), z.topLeft, Size(z.width, z.height * 0.16f))
        drawRect(Tono(o, 0.12f).b(0.45f), z.topLeft, Size(z.width, 1f * u))
        drawRect(tz.s(0.45f), Offset(z.left, z.top + z.height * 0.16f), Size(z.width, 1.2f * u))
        drawRect(tz.s(0.4f), Offset(z.left, z.bottom - 1.4f * u), Size(z.width, 1.4f * u))
        // Bajo el zócalo, la oscuridad de debajo del mueble; los pies, gastados.
        drawRect(tz.s(0.6f), Offset(z.left, zoc), Size(z.width, pie - zoc))
        listOf(z.left to z.left + w * 0.07f, w * 0.47f to w * 0.53f, z.right - w * 0.07f to z.right).forEachIndexed { j, (xa, xb) ->
            val p = Rect(xa, zoc, xb, pie)
            drawRect(tz.c(if (j == 2) mezcla(ROBLE, ROBLE_OSC, 0.3f) else ROBLE), p.topLeft, p.size)
            vetas(p, ROBLE_OSC.copy(alpha = 0.35f), 41 + j, 0.6f, vertical = true, grosor = 0.6f, ondulacion = 0.3f)
            drawRect(tz.b(0.3f), p.topLeft, Size(1.2f * u, p.height))
            drawRect(tz.s(0.35f), Offset(p.right - p.width * 0.2f, p.top), Size(p.width * 0.2f, p.height))
            drawRect(tz.s(0.3f), Offset(p.left, p.bottom - p.height * 0.3f), Size(p.width, p.height * 0.3f))
            drawOval(tz.s(0.4f), Offset(p.left - 1f * u, p.bottom - 2f * u), Size(p.width * 1.25f, 4f * u))
        }

        // ---- las puertas abiertas, con sus herrajes
        val pTop = w * MOLD_B; val pBot = b.top + hb * 0.27f
        puerta(w * BL, w * 0.016f, pTop, pBot, w, o, luzDeFrente = true)
        puerta(w * BR, w * 0.984f, pTop, pBot, w, o, luzDeFrente = false)

        // ---- el tablero, el friso y lo de encima, de atrás adelante
        coronamiento(w, o)
        tablaDeCera(w, o)
        atrilConCodice(w, o)
        tinteros(w, o)
        palmatoria(w, o)
        iluminador(w, o)
        regla(w, o)

        // ---- al pie: el cesto a la izquierda, cortado, y el escabel a la derecha
        cesto(Rect(-w * 0.075f, b.top + hb * 0.5f, w * 0.2f, b.top + hb * 0.99f), o, kPie)
        escabel(Rect(w * 0.68f, b.top + hb * 0.56f, w * 0.93f, b.top + hb * 0.97f), o, kPie)

        // ---- de noche: la llama
        if (o) llama(Offset(w * LUZ_VELA.x + w * 0.002f, w * LUZ_VELA.y + w * 0.019f), w * 0.0125f)

        // ---- la placa de «y N más», clavada en el zócalo
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val et = contornoIrregular(r, 77, 16, 0.04f)
            translate(1.5f * u, 2f * u) { drawPath(et, tz.s(0.4f)) }
            drawPath(et, if (o) PERG_NOCHE else PERGAMINO)
            listOf(r.left + 5f * u, r.right - 5f * u).forEach { xx ->
                drawCircle(HIERRO, 1.7f * u, Offset(xx, r.center.y))
                drawCircle(HIERRO_LUZ, 0.6f * u, Offset(xx - 0.5f * u, r.center.y - 0.5f * u))
            }
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    // ---------------------------------------------------------------- los compartimentos

    /** El arco del nicho dentro del compartimento. */
    private fun arcoNicho(w: Float, h: Float) = Rect(w * JAMBA, h * CLAVE, w * (1f - JAMBA), h * NICHO)

    /** Una columnilla de la jamba, de [x0] a [x1] y de [y0] (donde arranca el arco) a [y1]: ábaco, capitel cúbico, fuste y basa. */
    private fun DrawScope.columnilla(x0: Float, x1: Float, y0: Float, y1: Float, t: Tono) {
        val u = density
        val g = x1 - x0; val cx = (x0 + x1) / 2
        val alto = y1 - y0
        val cap = alto * 0.2f
        val madera = mezcla(ROBLE, ROBLE_LUZ, 0.3f)
        // El fuste, redondo: su luz a la izquierda y su sombra a la derecha, y la que deja en la jamba.
        val f0 = cx - g * 0.36f; val fw = g * 0.72f
        drawRect(t.s(0.3f), Offset(f0 + 1.8f * u, y0 + cap), Size(fw, alto - cap))
        drawRect(t.c(madera), Offset(f0, y0 + cap), Size(fw, alto - cap))
        drawRect(t.b(0.4f), Offset(f0 + fw * 0.14f, y0 + cap), Size(fw * 0.2f, alto - cap))
        drawRect(t.s(0.34f), Offset(f0 + fw * 0.66f, y0 + cap), Size(fw * 0.34f, alto - cap))
        // El capitel cúbico: cuadrado arriba, redondeado hacia el fuste, con su semicírculo tallado.
        val capitel = Path().apply {
            moveTo(x0, y0 + cap * 0.12f); lineTo(x1, y0 + cap * 0.12f); lineTo(x1, y0 + cap * 0.55f)
            cubicTo(x1, y0 + cap, f0 + fw, y0 + cap * 0.9f, f0 + fw, y0 + cap)
            lineTo(f0, y0 + cap)
            cubicTo(f0, y0 + cap * 0.9f, x0, y0 + cap, x0, y0 + cap * 0.55f)
            close()
        }
        drawPath(capitel, t.c(mezcla(ROBLE, ROBLE_LUZ, 0.42f)))
        clipPath(capitel) { drawRect(t.s(0.3f), Offset(cx + g * 0.18f, y0), Size(g, cap)) }
        drawArc(t.s(0.42f), 0f, 180f, false, Offset(x0 + g * 0.16f, y0 - cap * 0.2f), Size(g * 0.68f, cap * 0.95f), style = Stroke(0.8f * u))
        // El ábaco y el collarino.
        drawRect(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.5f)), Offset(x0 - g * 0.12f, y0 - cap * 0.12f), Size(g * 1.24f, cap * 0.26f))
        drawRect(t.b(0.5f), Offset(x0 - g * 0.12f, y0 - cap * 0.12f), Size(g * 1.24f, 0.9f * u))
        drawRect(t.s(0.35f), Offset(x0 - g * 0.12f, y0 + cap * 0.14f), Size(g * 1.24f, 1f * u))
        drawRect(t.s(0.3f), Offset(f0, y0 + cap), Size(fw, 1.2f * u))
        // La basa: un toro sobre su plinto.
        drawRect(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.25f)), Offset(x0 - g * 0.1f, y1 - alto * 0.07f), Size(g * 1.2f, alto * 0.07f))
        drawOval(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.4f)), Offset(x0 - g * 0.02f, y1 - alto * 0.14f), Size(g * 1.04f, alto * 0.085f))
        drawArc(t.b(0.45f), 180f, 90f, false, Offset(x0 - g * 0.02f, y1 - alto * 0.14f), Size(g * 1.04f, alto * 0.085f), style = Stroke(0.7f * u))
        drawRect(t.b(0.35f), Offset(x0 - g * 0.1f, y1 - alto * 0.07f), Size(g * 1.2f, 0.8f * u))
    }

    /**
     * El anaquel sin lo que guarda: la madera alrededor (enjutas con su roseta), el fondo del nicho con sus tablas, la sombra
     * del arco y de la jamba izquierda, el suelo del anaquel, la arquivolta con su filete de almagre y las dos columnillas.
     */
    private fun DrawScope.nicho(w: Float, h: Float, t: Tono, semilla: Int) {
        val u = density
        drawRect(t.c(ROBLE), Offset.Zero, Size(w, h * NICHO))
        vetas(Rect(0f, 0f, w, h * NICHO), ROBLE_OSC.copy(alpha = if (t.o) 0.2f else 0.32f), 4711 + Math.floorMod(semilla, 7), 0.45f, vertical = true, grosor = 0.6f, ondulacion = 0.4f)
        val ar = arcoNicho(w, h)
        val p = arcoMedioPunto(ar)
        val rad = ar.width / 2
        val c = Offset(ar.center.x, ar.top + rad)
        val g = w * ROSCA
        // Las rosetas de las enjutas.
        roseta(Offset(w * 0.062f, h * 0.078f), w * 0.04f, t)
        roseta(Offset(w * 0.938f, h * 0.078f), w * 0.04f, t)
        // El fondo: tablas verticales, más hondas que el mueble.
        drawPath(p, t.c(mezcla(ROBLE_OSC, ROBLE, 0.34f)))
        clipPath(p) {
            listOf(0.31f, 0.52f, 0.71f).forEach { fx ->
                drawRect(t.s(0.3f), Offset(w * fx, 0f), Size(1f * u, h))
                drawRect(t.b(0.14f), Offset(w * fx + 1f * u, 0f), Size(0.7f * u, h))
            }
            // La luz entra de arriba a la izquierda: da en lo bajo del fondo, a la derecha; arriba y a la izquierda quedan
            // la sombra del arco y la de la jamba, en dos pasos.
            drawPath(poligono(w * 0.36f, h * NICHO, w * 0.95f, h * 0.24f, w, h * 0.24f, w, h * NICHO), t.b(0.07f))
            drawPath(Path().apply { op(p, Path().apply { addPath(p, Offset(w * 0.085f, h * 0.12f)) }, PathOperation.Difference) }, t.s(0.42f))
            drawPath(Path().apply { op(p, Path().apply { addPath(p, Offset(w * 0.035f, h * 0.05f)) }, PathOperation.Difference) }, t.s(0.26f))
            // El suelo del anaquel, visto un poco desde arriba, con la sombra de la jamba.
            val s0 = h * NICHO - h * 0.04f
            drawRect(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.34f)), Offset(0f, s0), Size(w, h * 0.04f))
            drawPath(poligono(0f, s0, w * 0.2f, s0, w * 0.3f, h * NICHO, 0f, h * NICHO), t.s(0.32f))
            drawRect(t.s(0.3f), Offset(0f, s0 - 1.2f * u), Size(w, 1.2f * u))
        }
        // La arquivolta: una banda con su bocel de fuera a la luz, el de dentro en sombra y el filete de almagre, gastado.
        val rm = rad + g / 2
        drawArc(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.34f)), 180f, 180f, false, Offset(c.x - rm, c.y - rm), Size(2 * rm, 2 * rm), style = Stroke(g))
        val re = rad + g
        drawArc(t.b(0.5f), 185f, 95f, false, Offset(c.x - re, c.y - re), Size(2 * re, 2 * re), style = Stroke(1f * u))
        drawArc(t.s(0.3f), 290f, 70f, false, Offset(c.x - re, c.y - re), Size(2 * re, 2 * re), style = Stroke(1.2f * u))
        drawArc(t.s(0.4f), 180f, 180f, false, Offset(c.x - rad, c.y - rad), Size(2 * rad, 2 * rad), style = Stroke(1.2f * u))
        drawArc(t.b(0.3f), 285f, 75f, false, Offset(c.x - rad + 1.2f * u, c.y - rad + 1.2f * u), Size(2 * rad - 2.4f * u, 2 * rad - 2.4f * u), style = Stroke(0.9f * u))
        for (i in 0 until 11) if (azar(semilla, 400 + i) > 0.22f)
            drawArc(t.c(ALMAGRE).copy(alpha = 0.78f), 181f + i * 16.4f, 13.5f, false, Offset(c.x - rm, c.y - rm), Size(2 * rm, 2 * rm), style = Stroke(g * 0.26f))
        // Las columnillas de las jambas.
        columnilla(ar.left - g, ar.left, c.y, h * NICHO, t)
        columnilla(ar.right, ar.right + g, c.y, h * NICHO, t)
    }

    /** La tabla del anaquel, debajo del nicho: el canto de delante a la luz y la sombra de abajo. */
    private fun DrawScope.tablaAnaquel(w: Float, h: Float, t: Tono, semilla: Int) {
        val u = density
        val r = Rect(0f, h * NICHO, w, h)
        drawRect(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.18f)), r.topLeft, r.size)
        vetas(r, ROBLE_OSC.copy(alpha = if (t.o) 0.2f else 0.32f), 4800 + Math.floorMod(semilla, 11), 0.6f, grosor = 0.6f, ondulacion = 0.4f)
        drawRect(t.b(0.55f), r.topLeft, Size(w, 1.2f * u))
        drawRect(t.s(0.3f), Offset(0f, r.top + 1.2f * u), Size(w, 1.4f * u))
        drawRect(t.s(0.3f), Offset(0f, h - 1.4f * u), Size(w, 1.4f * u))
    }

    /** La etiqueta de pergamino clavada en la tabla, pautada en rojo; la de lo sin tema, de un clavo y con una esquina rota. */
    private fun DrawScope.etiqueta(w: Float, h: Float, t: Tono, semilla: Int, sinTema: Boolean) {
        val u = density
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        val et = contornoIrregular(r, semilla, 18, 0.035f)
        translate(1.4f * u, 1.8f * u) { drawPath(et, t.s(0.35f)) }
        val fondo = if (t.o) mezcla(PERG_NOCHE, mezcla(PERGAMINO, LLAMA, 0.12f), (1.3f * t.k.pow(0.6f)).coerceAtMost(1f)) else t.c(PERGAMINO)
        drawPath(et, fondo)
        clipPath(et) {
            drawRect((if (t.o) NOCHE else PERG_OSC).copy(alpha = 0.22f), Offset(r.left, r.bottom - r.height * 0.16f), Size(r.width, r.height * 0.16f))
            drawLine(ALMAGRE.copy(alpha = 0.45f), Offset(r.left + 6f * u, r.top + 3.2f * u), Offset(r.right - 6f * u, r.top + 3.2f * u), 0.7f * u)
            drawLine(ALMAGRE.copy(alpha = 0.45f), Offset(r.left + 6f * u, r.bottom - 2.8f * u), Offset(r.right - 6f * u, r.bottom - 2.8f * u), 0.7f * u)
        }
        drawRect(t.b(0.4f), Offset(r.left + 2f * u, r.top + 0.6f * u), Size(r.width - 4f * u, 0.7f * u))
        val clavos = if (sinTema) listOf(Offset(r.center.x, r.top + 1.6f * u)) else listOf(Offset(r.left + 4.5f * u, r.top + 1.6f * u), Offset(r.right - 4.5f * u, r.top + 1.6f * u))
        clavos.forEach { p ->
            drawCircle(t.c(HIERRO), 1.9f * u, p)
            drawCircle(t.c(HIERRO_LUZ), 0.6f * u, p - Offset(0.5f * u, 0.5f * u))
        }
        if (sinTema) {
            // La esquina de abajo a la derecha, rota y doblada.
            val e = Offset(r.right, r.bottom)
            drawPath(poligono(e.x - r.height * 0.32f, e.y + 1f, e.x + 1f, e.y + 1f, e.x + 1f, e.y - r.height * 0.32f), t.c(mezcla(ROBLE, ROBLE_LUZ, 0.18f)))
            drawPath(poligono(e.x - r.height * 0.32f, e.y, e.x, e.y - r.height * 0.32f, e.x - r.height * 0.26f, e.y - r.height * 0.26f), t.c(PERG_OSC))
        }
    }

    /**
     * Una pila de folios sueltos en [r]: las hojas de canto, desiguales (el pergamino no se corta a máquina), alguna salida y
     * más amarilla, y la de arriba vista un poco desde encima, combada.
     */
    private fun DrawScope.pila(r: Rect, papel: Color, semilla: Int, t: Tono) {
        val u = density
        val g = 2.6f * u
        val m = (r.height / g).toInt().coerceAtLeast(2)
        contacto(Offset(r.center.x + r.width * 0.06f, r.bottom), r.width * 1.1f, 4f * u, t, 0.4f)
        drawRect(t.c(mezcla(papel, PERG_OSC, 0.3f)), Offset(r.left + r.width * 0.02f, r.top), Size(r.width * 0.96f, r.height))
        for (i in 0 until m) {
            val y = r.bottom - (i + 1) * g
            val dx = (azar(semilla, i) - 0.5f) * r.width * 0.07f
            val da = (azar(semilla, 300 + i) - 0.5f) * r.width * 0.06f
            val tono = if (azar(semilla, 100 + i) > 0.66f) mezcla(papel, PERG_OSC, 0.6f) else if (azar(semilla, 200 + i) > 0.78f) mezcla(papel, LUZ_MUEBLE, 0.4f) else papel
            drawRect(t.c(tono), Offset(r.left + dx, y), Size(r.width + da, g - 0.6f * u))
            drawLine(t.s(0.24f), Offset(r.left + dx, y + g - 0.5f * u), Offset(r.right + dx + da, y + g - 0.5f * u), 0.6f * u)
        }
        // El lado derecho, en sombra; el izquierdo coge la luz.
        drawRect(t.s(0.2f), Offset(r.right - r.width * 0.16f, r.top), Size(r.width * 0.22f, r.height))
        drawRect(t.b(0.3f), Offset(r.left - r.width * 0.03f, r.top), Size(1.2f * u, r.height))
        // La hoja de arriba: su cara, combada, con las líneas escritas.
        val alza = r.width * 0.1f + 2f * u
        val cara = Path().apply {
            moveTo(r.left + r.width * 0.01f, r.top)
            lineTo(r.left + r.width * 0.06f, r.top - alza)
            cubicTo(r.left + r.width * 0.4f, r.top - alza * 1.25f, r.left + r.width * 0.7f, r.top - alza * 0.8f, r.right + r.width * 0.03f, r.top - alza)
            lineTo(r.right, r.top)
            close()
        }
        drawPath(cara, t.c(mezcla(papel, LUZ_MUEBLE, 0.3f)))
        for (j in 0 until 3) {
            val y = r.top - alza * (0.25f + 0.24f * j)
            drawLine(t.c(TINTA).copy(alpha = 0.32f), Offset(r.left + r.width * 0.14f, y), Offset(r.right - r.width * (0.1f + 0.2f * azar(semilla, 500 + j)), y), 0.6f * u)
        }
        drawLine(t.s(0.2f), Offset(r.left + r.width * 0.01f, r.top), Offset(r.right, r.top), 0.6f * u)
    }

    /**
     * La clase de los folios sueltos: una pila en su cartapacio de cuero (la solapa de delante, con sus dos tiras atadas por
     * encima), otra más vieja al lado si hay muchos, y encima la piedra de peso o un cuadernillo doblado.
     */
    private fun DrawScope.folios(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono) {
        val u = density
        val f = h * NICHO - h * 0.022f
        val n = c.n.coerceIn(1, 8)
        val alto = h * (0.25f + 0.036f * n)
        val cuero = CUEROS[Math.floorMod(m / 4, CUEROS.size)]
        if (n >= 3) pila(Rect(w * 0.58f, f - alto * 0.58f, w * 0.83f, f), mezcla(PERG_OSC, BADANA, 0.2f), c.semilla + 3, t)
        val a = Rect(w * 0.18f, f - alto, w * 0.64f, f)
        pila(a, PERGAMINO, c.semilla, t)
        // La solapa del cartapacio: cuero, con su borde de arriba combado y cosido, más oscuro a la derecha.
        val ch = minOf(alto * 0.5f, h * 0.2f)
        val x0 = a.left - w * 0.022f; val x1 = a.right + w * 0.022f
        val solapa = Path().apply {
            moveTo(x0, f); lineTo(x0, f - ch * 0.84f)
            cubicTo(a.left + a.width * 0.3f, f - ch * 1.06f, a.left + a.width * 0.7f, f - ch * 0.84f, x1, f - ch)
            lineTo(x1, f); close()
        }
        translate(0f, -1.6f * u) { drawPath(solapa, t.s(0.25f)) }
        drawPath(solapa, t.c(cuero))
        clipPath(solapa) {
            drawRect(t.s(0.26f), Offset(x1 - (x1 - x0) * 0.2f, f - ch * 1.1f), Size((x1 - x0) * 0.2f, ch * 1.1f))
            drawRect(t.b(0.16f), Offset(x0, f - ch * 1.1f), Size((x1 - x0) * 0.22f, ch * 1.1f))
            drawRect(t.s(0.22f), Offset(x0, f - ch * 0.16f), Size(x1 - x0, ch * 0.16f))
            // El pespunte del borde.
            translate(0f, ch * 0.13f) {
                drawPath(Path().apply {
                    moveTo(x0, f - ch * 0.84f)
                    cubicTo(a.left + a.width * 0.3f, f - ch * 1.06f, a.left + a.width * 0.7f, f - ch * 0.84f, x1, f - ch)
                }, t.c(mezcla(cuero, PERGAMINO, 0.55f)), style = Stroke(0.9f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(3f * u, 2.4f * u))))
            }
        }
        drawPath(Path().apply {
            moveTo(x0, f - ch * 0.84f)
            cubicTo(a.left + a.width * 0.3f, f - ch * 1.06f, a.left + a.width * 0.7f, f - ch * 0.84f, x1, f - ch)
        }, t.b(0.4f), style = Stroke(0.9f * u))
        // Las dos tiras que suben por delante de la pila y se atan arriba.
        val tira = t.c(mezcla(cuero, ROBLE_OSC, 0.4f))
        listOf(0.28f, 0.74f).forEach { fx ->
            val x = a.left + a.width * fx
            drawRect(t.s(0.25f), Offset(x + 1.4f * u, a.top - 1f * u), Size(w * 0.022f, alto - ch * 0.5f))
            drawRect(tira, Offset(x, a.top - a.width * 0.1f - 2f * u), Size(w * 0.022f, alto - ch * 0.5f + a.width * 0.1f + 2f * u))
            drawRect(t.b(0.25f), Offset(x, a.top - a.width * 0.1f - 2f * u), Size(0.8f * u, alto - ch * 0.5f + a.width * 0.1f))
        }
        val pc = Offset(a.center.x + a.width * 0.02f, a.top - a.width * 0.1f - 2f * u)
        if (n >= 4) {
            // La piedra de peso: un canto rodado, con su luz y su sombra.
            val rx = w * 0.095f; val ry = h * 0.058f
            val c0 = pc - Offset(0f, ry * 0.7f)
            drawOval(t.s(0.35f), Offset(c0.x - rx * 0.8f, c0.y + ry * 0.45f), Size(rx * 2f, ry * 0.9f))
            drawOval(t.c(mezcla(PIEDRA, AZURITA, 0.15f)), Offset(c0.x - rx, c0.y - ry), Size(2 * rx, 2 * ry))
            drawOval(t.s(0.3f), Offset(c0.x - rx * 0.1f, c0.y - ry * 0.3f), Size(rx * 1.1f, ry * 1.3f))
            drawOval(t.b(0.45f), Offset(c0.x - rx * 0.7f, c0.y - ry * 0.75f), Size(rx * 0.7f, ry * 0.5f))
        } else {
            // El nudo de las tiras y un cuadernillo doblado, de lado, encima.
            val q = Rect(a.left + a.width * 0.12f, pc.y - h * 0.06f, a.right - a.width * 0.16f, pc.y)
            rotate(-4f, q.center) {
                drawRect(t.s(0.25f), Offset(q.left + 1.4f * u, q.top + 1.6f * u), q.size)
                drawRect(t.c(mezcla(PERGAMINO, PERG_OSC, 0.35f)), q.topLeft, q.size)
                for (j in 1..3) drawLine(t.s(0.22f), Offset(q.left, q.top + q.height * j / 4f), Offset(q.right, q.top + q.height * j / 4f), 0.6f * u)
                drawRect(t.s(0.25f), Offset(q.right - q.width * 0.12f, q.top), Size(q.width * 0.12f, q.height))
                drawRect(t.b(0.4f), q.topLeft, Size(q.width, 0.8f * u))
                // El hilo de la costura, que asoma por el pliegue.
                for (j in 0 until 3) drawLine(t.c(TINTES[Math.floorMod(m / 16 + 1, 3)]), Offset(q.left + q.width * (0.2f + 0.25f * j), q.center.y), Offset(q.left + q.width * (0.3f + 0.25f * j), q.center.y), 1f * u)
            }
        }
    }

    /** Un códice tumbado: [r] es su cara de delante; [lomo] si da el lomo (con sus nervios) o el corte (con correas y broches). */
    private fun DrawScope.codice(r: Rect, cuero: Color, lomo: Boolean, semilla: Int, t: Tono) {
        val u = density
        val fondo = r.height * 0.32f
        // La tapa de arriba, vista desde encima, con sus bullones de latón.
        val tapa = poligono(r.left, r.top, r.left + r.width * 0.04f, r.top - fondo, r.right + r.width * 0.02f, r.top - fondo, r.right, r.top)
        drawPath(tapa, t.c(mezcla(cuero, LUZ_MUEBLE, 0.18f)))
        drawLine(t.b(0.45f), Offset(r.left + r.width * 0.04f, r.top - fondo), Offset(r.right + r.width * 0.02f, r.top - fondo), 0.8f * u)
        listOf(0.14f, 0.5f, 0.86f).forEach { fx ->
            val b = Offset(r.left + r.width * (fx + 0.02f), r.top - fondo * 0.5f)
            drawOval(t.s(0.35f), Offset(b.x - r.width * 0.035f + 0.8f * u, b.y - fondo * 0.25f + 0.8f * u), Size(r.width * 0.07f, fondo * 0.6f))
            drawOval(t.c(ORO), Offset(b.x - r.width * 0.035f, b.y - fondo * 0.28f), Size(r.width * 0.07f, fondo * 0.56f))
            drawCircle(t.b(0.7f), 0.8f * u, Offset(b.x - r.width * 0.012f, b.y - fondo * 0.1f))
        }
        if (lomo) {
            // El lomo de cuero con sus nervios.
            drawRoundRect(t.c(cuero), r.topLeft, r.size, CornerRadius(r.height * 0.3f))
            drawRect(t.b(0.25f), Offset(r.left, r.top + r.height * 0.12f), Size(r.width, r.height * 0.18f))
            drawRect(t.s(0.25f), Offset(r.left, r.bottom - r.height * 0.3f), Size(r.width, r.height * 0.3f))
            for (j in 1..4) {
                val x = r.left + r.width * j / 5f
                drawRect(t.s(0.35f), Offset(x + 1f * u, r.top), Size(1.4f * u, r.height))
                drawRect(t.c(mezcla(cuero, LUZ_MUEBLE, 0.2f)), Offset(x - 1f * u, r.top), Size(2f * u, r.height))
            }
            // El tejuelo de pergamino.
            drawRect(t.c(PERG_OSC), Offset(r.left + r.width * 0.42f, r.top + r.height * 0.3f), Size(r.width * 0.16f, r.height * 0.4f))
            drawLine(t.c(TINTA).copy(alpha = 0.4f), Offset(r.left + r.width * 0.45f, r.center.y), Offset(r.left + r.width * 0.55f, r.center.y), 0.7f * u)
        } else {
            // Las tablas de arriba y de abajo forradas, el corte de las hojas en medio.
            val tb = r.height * 0.22f
            drawRect(t.c(mezcla(PERGAMINO, PERG_OSC, 0.4f)), Offset(r.left + r.width * 0.015f, r.top + tb), Size(r.width * 0.97f, r.height - 2 * tb))
            for (j in 1..4) drawLine(t.s(0.18f), Offset(r.left + r.width * 0.02f, r.top + tb + (r.height - 2 * tb) * j / 5f), Offset(r.right - r.width * 0.02f, r.top + tb + (r.height - 2 * tb) * j / 5f), 0.5f * u)
            drawRect(t.c(cuero), r.topLeft, Size(r.width, tb))
            drawRect(t.c(mezcla(cuero, ROBLE_OSC, 0.25f)), Offset(r.left, r.bottom - tb), Size(r.width, tb))
            drawRect(t.b(0.4f), r.topLeft, Size(r.width, 0.8f * u))
            // Las correas que bajan de la tapa y los broches de latón.
            listOf(0.3f, 0.7f).forEach { fx ->
                val x = r.left + r.width * fx
                drawRect(t.c(mezcla(cuero, ROBLE_OSC, 0.35f)), Offset(x - r.width * 0.03f, r.top - fondo * 0.2f), Size(r.width * 0.06f, r.height * 0.7f))
                drawRect(t.c(ORO), Offset(x - r.width * 0.04f, r.top + r.height * 0.58f), Size(r.width * 0.08f, r.height * 0.24f))
                drawRect(t.b(0.6f), Offset(x - r.width * 0.04f, r.top + r.height * 0.58f), Size(r.width * 0.08f, 0.8f * u))
            }
        }
        // El extremo derecho en sombra.
        drawRect(t.s(0.25f), Offset(r.right - r.width * 0.1f, r.top), Size(r.width * 0.1f, r.height))
        drawLine(t.s(0.35f), Offset(r.left, r.bottom), Offset(r.right, r.bottom), 1.2f * u)
    }

    /** La clase de los códices: tumbados unos sobre otros, de cueros distintos, más cuantos más guarda. */
    private fun DrawScope.codices(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono) {
        val u = density
        val f = h * NICHO - h * 0.022f
        val cuantos = when { c.n <= 2 -> 2; c.n <= 5 -> 3; else -> 4 }
        val anchos = listOf(0.68f, 0.62f, 0.56f, 0.47f)
        val gruesos = listOf(0.118f, 0.1f, 0.106f, 0.086f)
        contacto(Offset(w * 0.5f, f), w * 0.8f, 4f * u, t, 0.4f)
        var y = f
        for (i in 0 until cuantos) {
            val an = w * (anchos[i] + (azar(c.semilla, i) - 0.5f) * 0.05f)
            val x0 = w * 0.5f - an / 2 + (azar(c.semilla, 10 + i) - 0.5f) * w * 0.07f
            val gr = h * gruesos[i]
            val r = Rect(x0, y - gr, x0 + an, y)
            codice(r, CUEROS[Math.floorMod(m / 4 + i * 2, CUEROS.size)], (i + m / 4) % 2 == 0, c.semilla + i, t)
            y -= gr + gr * 0.32f
        }
    }

    /**
     * Un rollo de pergamino tumbado en [r] (su alto es el diámetro): el cilindro con su luz arriba, el borde suelto de la hoja,
     * la boca de la izquierda con las vueltas, y el cordel atado. Con [funda], va metido en su funda de lienzo teñido.
     */
    private fun DrawScope.rollo(r: Rect, papel: Color, cordel: Color, semilla: Int, t: Tono, funda: Color?) {
        val u = density
        val rad = r.height / 2
        val ex = rad * 0.46f
        val cuerpo = funda ?: papel
        drawOval(t.c(mezcla(cuerpo, ROBLE_OSC, 0.25f)), Offset(r.right - 2 * ex, r.top), Size(2 * ex, r.height))
        drawRect(t.c(cuerpo), Offset(r.left + ex, r.top), Size(r.width - 2 * ex, r.height))
        clipRect(r.left + ex, r.top, r.right, r.bottom) {
            drawRect(t.b(0.34f), Offset(r.left, r.top + r.height * 0.12f), Size(r.width, r.height * 0.16f))
            drawRect(t.s(0.16f), Offset(r.left, r.top + r.height * 0.55f), Size(r.width, r.height * 0.45f))
            drawRect(t.s(0.22f), Offset(r.left, r.top + r.height * 0.8f), Size(r.width, r.height * 0.2f))
        }
        if (funda == null) {
            // El borde suelto de la hoja, a lo largo, un poco ondulado.
            drawPath(Path().apply {
                moveTo(r.left + ex, r.top + r.height * 0.4f)
                cubicTo(r.left + r.width * 0.35f, r.top + r.height * 0.34f, r.left + r.width * 0.65f, r.top + r.height * 0.47f, r.right - ex * 0.6f, r.top + r.height * 0.4f)
            }, t.s(0.3f), style = Stroke(0.8f * u))
        } else {
            // Los pliegues del lienzo y el fruncido de la boca.
            for (j in 0 until 4) {
                val x = r.left + r.width * (0.2f + 0.2f * j)
                drawLine(t.s(0.22f), Offset(x, r.top + r.height * 0.15f), Offset(x + r.width * 0.05f, r.bottom - r.height * 0.1f), 0.8f * u)
            }
            drawRect(t.c(mezcla(funda, ROBLE_OSC, 0.4f)), Offset(r.left + ex * 2.1f, r.top), Size(1.6f * u, r.height))
        }
        // La boca: las vueltas de la hoja.
        val boca = Offset(r.left + ex, r.center.y)
        drawOval(t.c(mezcla(papel, LUZ_MUEBLE, 0.2f)), Offset(boca.x - ex, r.top), Size(2 * ex, r.height))
        for (j in 1..3) {
            val k = 1f - j * 0.26f
            drawOval(t.s(0.34f), Offset(boca.x - ex * k, boca.y - rad * k), Size(2 * ex * k, 2 * rad * k), style = Stroke(0.7f * u))
        }
        drawOval(t.s(0.6f), Offset(boca.x - ex * 0.16f, boca.y - rad * 0.16f), Size(ex * 0.32f, rad * 0.32f))
        drawArc(t.b(0.55f), 150f, 100f, false, Offset(boca.x - ex, r.top), Size(2 * ex, r.height), style = Stroke(0.9f * u))
        drawLine(t.s(0.3f), Offset(r.left + ex, r.bottom), Offset(r.right - ex, r.bottom), 1f * u)
        // El cordel, con dos vueltas y sus cabos.
        val xc = r.left + r.width * (0.5f + 0.2f * azar(semilla, 5))
        drawRect(t.c(cordel), Offset(xc, r.top), Size(2.4f * u, r.height))
        drawRect(t.c(cordel), Offset(xc + 3.6f * u, r.top), Size(1.6f * u, r.height))
        drawRect(t.b(0.35f), Offset(xc, r.top + r.height * 0.14f), Size(5.2f * u, r.height * 0.12f))
        drawLine(t.c(cordel), Offset(xc + 2.6f * u, r.top + r.height * 0.46f), Offset(xc + 0.6f * u, r.top + r.height * 0.84f), 1.2f * u, StrokeCap.Round)
        drawLine(t.c(cordel), Offset(xc + 2.6f * u, r.top + r.height * 0.46f), Offset(xc + 6.4f * u, r.top + r.height * 0.8f), 1.2f * u, StrokeCap.Round)
        drawCircle(t.c(mezcla(cordel, ROBLE_OSC, 0.3f)), 1.8f * u, Offset(xc + 2.6f * u, r.top + r.height * 0.46f))
    }

    /** La clase de los rollos: tumbados unos sobre otros, de pergaminos y cordeles distintos; uno va en su funda, otro lleva el sello colgando. */
    private fun DrawScope.rollos(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono) {
        val u = density
        val f = h * NICHO - h * 0.022f
        val cuantos = when { c.n <= 1 -> 2; c.n <= 3 -> 3; c.n <= 5 -> 4; else -> 5 }
        val diam = h * 0.118f
        val largos = listOf(0.6f, 0.52f, 0.56f, 0.44f, 0.36f)
        val papeles = listOf(PERGAMINO, mezcla(PERG_OSC, BADANA, 0.3f), mezcla(PERGAMINO, PERG_OSC, 0.6f))
        val conFunda = Math.floorMod(m / 4, cuantos)
        contacto(Offset(w * 0.5f, f), w * 0.8f, 4f * u, t, 0.4f)
        // Al fondo, de pie y apoyados en el rincón, uno o dos rollos largos.
        for (j in (if (c.n >= 3) 1 else 0) downTo 0) {
            val cx = w * (0.76f - 0.125f * j); val alto = h * (0.5f - 0.09f * j); val rr = diam * 0.52f
            rotate(5f - 11f * j, Offset(cx, f)) {
                val r = Rect(cx - rr, f - alto, cx + rr, f)
                val papel = papeles[Math.floorMod(m / 4 + 1 + j, 3)]
                drawRect(t.c(mezcla(papel, ROBLE_OSC, 0.12f)), r.topLeft, r.size)
                drawRect(t.b(0.3f), Offset(r.left + r.width * 0.12f, r.top), Size(r.width * 0.18f, r.height))
                drawRect(t.s(0.3f), Offset(r.left + r.width * 0.62f, r.top), Size(r.width * 0.38f, r.height))
                drawLine(t.s(0.26f), Offset(r.left + r.width * 0.45f, r.top + r.height * 0.05f), Offset(r.left + r.width * 0.42f, r.bottom), 0.8f * u)
                drawOval(t.c(mezcla(papel, LUZ_MUEBLE, 0.15f)), Offset(r.left, r.top - rr * 0.4f), Size(r.width, rr * 0.8f))
                drawOval(t.s(0.34f), Offset(r.left + r.width * 0.2f, r.top - rr * 0.24f), Size(r.width * 0.6f, rr * 0.48f), style = Stroke(0.7f * u))
                drawOval(t.s(0.6f), Offset(r.left + r.width * 0.4f, r.top - rr * 0.08f), Size(r.width * 0.2f, rr * 0.16f))
                val cordel = t.c(TINTES[Math.floorMod(m / 16 + 1 + j, 3)])
                drawRect(cordel, Offset(r.left, r.top + r.height * 0.34f), Size(r.width, 2.4f * u))
                drawRect(cordel, Offset(r.left, r.top + r.height * 0.34f + 3.6f * u), Size(r.width, 1.6f * u))
            }
        }
        var sello: Offset? = null
        for (i in 0 until cuantos) {
            val largo = w * (largos[i] + (azar(c.semilla, 30 + i) - 0.5f) * 0.05f)
            val x0 = w * 0.45f - largo / 2 + (azar(c.semilla, 40 + i) - 0.5f) * w * 0.06f
            val y = f - i * diam * 0.9f
            val r = Rect(x0, y - diam, x0 + largo, y)
            if (i > 0) drawOval(t.s(0.3f), Offset(r.left + diam * 0.2f, r.bottom - diam * 0.14f), Size(r.width - diam * 0.2f, diam * 0.24f))
            rollo(r, papeles[Math.floorMod(m / 4 + i, 3)], TINTES[Math.floorMod(m / 16 + i, 3)], c.semilla + i, t, if (i == conFunda) TINTES[Math.floorMod(m / 16 + 2, 3)].let { mezcla(it, ROBLE_OSC, 0.2f) } else null)
            if (i == 0) sello = Offset(r.right - largo * 0.2f, r.top + diam * 0.5f)
        }
        // El sello de cera colgando de su cordón, por delante del rollo de abajo.
        if (Math.floorMod(m / 64, 3) != 0 || c.n >= 4) sello?.let { s ->
            val b = Offset(s.x + w * 0.012f, f - h * 0.012f)
            drawLine(t.c(VERDIGRIS), s, b, 1f * u)
            drawLine(t.c(VERDIGRIS), s + Offset(1.6f * u, 0f), b + Offset(1.6f * u, 0f), 1f * u)
            val sr = w * 0.044f
            val sc = Offset(b.x, b.y - sr * 0.3f)
            drawCircle(t.s(0.35f), sr, sc + Offset(1.2f * u, 1.6f * u))
            drawCircle(t.c(mezcla(BERMELLON, ROBLE_OSC, 0.15f)), sr, sc)
            drawCircle(t.c(mezcla(BERMELLON, ROBLE_OSC, 0.4f)), sr * 0.62f, sc, style = Stroke(0.8f * u))
            drawPath(poligono(sc.x - sr * 0.12f, sc.y - sr * 0.42f, sc.x + sr * 0.12f, sc.y - sr * 0.42f, sc.x + sr * 0.12f, sc.y + sr * 0.42f, sc.x - sr * 0.12f, sc.y + sr * 0.42f), t.c(mezcla(BERMELLON, ROBLE_OSC, 0.45f)))
            drawRect(t.c(mezcla(BERMELLON, ROBLE_OSC, 0.45f)), Offset(sc.x - sr * 0.36f, sc.y - sr * 0.16f), Size(sr * 0.72f, sr * 0.22f))
            drawArc(t.b(0.6f), 190f, 80f, false, Offset(sc.x - sr, sc.y - sr), Size(2 * sr, 2 * sr), style = Stroke(0.9f * u))
        }
    }

    /** Una tablilla de cera de pie en [r]: el marco de madera, la cera de su color con lo escrito a punzón y las tiras que la unen a sus hermanas. */
    private fun DrawScope.tablilla(r: Rect, cera: Color, semilla: Int, t: Tono) {
        val u = density
        val madera = mezcla(ROBLE_LUZ, CAL, 0.12f + 0.16f * azar(semilla, 1))
        drawRect(t.s(0.3f), Offset(r.left + 2f * u, r.top + 1.6f * u), r.size)
        drawRect(t.c(madera), r.topLeft, r.size)
        drawRect(t.b(0.45f), r.topLeft, Size(r.width, 1f * u))
        drawRect(t.b(0.3f), r.topLeft, Size(1f * u, r.height))
        drawRect(t.s(0.32f), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
        val m = r.width * 0.12f
        val c = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
        // Los ingletes del marco.
        listOf(r.topLeft to c.topLeft, Offset(r.right, r.top) to Offset(c.right, c.top), r.bottomRight to c.bottomRight, Offset(r.left, r.bottom) to Offset(c.left, c.bottom)).forEach { (a, b) -> drawLine(t.s(0.3f), a, b, 0.7f * u) }
        drawRect(t.c(cera), c.topLeft, c.size)
        drawRect(t.s(0.45f), c.topLeft, Size(c.width, 1.6f * u))
        drawRect(t.s(0.35f), c.topLeft, Size(1.4f * u, c.height))
        drawRect(t.b(0.3f), Offset(c.left, c.bottom - 0.8f * u), Size(c.width, 0.8f * u))
        val raya = t.c(mezcla(cera, CAL, 0.6f)).copy(alpha = 0.7f)
        val lineas = (c.height / (4.6f * u)).toInt().coerceIn(3, 11)
        for (j in 0 until lineas) {
            val y = c.top + c.height * (j + 0.9f) / (lineas + 0.8f)
            if (azar(semilla, 20 + j) > 0.12f) drawLine(raya, Offset(c.left + c.width * 0.08f, y), Offset(c.right - c.width * (0.08f + 0.3f * azar(semilla, 40 + j) * azar(semilla, 60 + j)), y), 0.8f * u)
        }
        // Los agujeros de las tiras, en el canto de la izquierda.
        listOf(0.25f, 0.75f).forEach { fy ->
            drawCircle(t.s(0.6f), 1f * u, Offset(r.left + m * 0.5f, r.top + r.height * fy - 2.4f * u))
            drawCircle(t.s(0.6f), 1f * u, Offset(r.left + m * 0.5f, r.top + r.height * fy + 2.4f * u))
        }
    }

    /**
     * La clase de las tablillas de cera (donde se apunta antes de pasar a limpio): de pie y en abanico, apoyadas unas en
     * otras, unidas por sus tiras de cuero; la de delante enseña la cera escrita. Más tablillas cuanto más guarda.
     */
    private fun DrawScope.tablillas(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono) {
        val u = density
        val f = h * NICHO - h * 0.022f
        val cuantas = when { c.n <= 1 -> 2; c.n <= 3 -> 3; c.n <= 5 -> 4; else -> 5 }
        val cera = listOf(mezcla(VERDIGRIS, NOCHE, 0.5f), mezcla(ALMAGRE, NOCHE, 0.45f), mezcla(AZURITA, NOCHE, 0.55f))[Math.floorMod(m / 4, 3)]
        val ancho = w * 0.3f
        val paso = w * (0.34f / (cuantas - 1).coerceAtLeast(2))
        val x0 = w * 0.5f - (ancho + paso * (cuantas - 1)) / 2 - w * 0.02f
        contacto(Offset(w * 0.52f, f), w * 0.78f, 4f * u, t, 0.4f)
        for (i in cuantas - 1 downTo 0) {
            val x = x0 + paso * i
            val alto = h * (0.4f + 0.05f * azar(c.semilla, 80 + i) - 0.012f * i)
            val giro = 2f + (8f + 2f * azar(c.semilla, 90 + i)) * i
            val pie = Offset(x + ancho, f)
            rotate(giro, pie) {
                tablilla(Rect(x, f - alto, x + ancho, f), cera, c.semilla + i, t)
                // Las de detrás, algo más apagadas: la de delante les da sombra.
                if (i > 0) drawRect(t.s(0.1f + 0.05f * i), Offset(x, f - alto), Size(ancho, alto))
            }
        }
        // La tira de cuero que las ata, por delante, con su nudo; y el punzón metido en ella.
        val y = f - h * 0.2f
        val tira = t.c(CUEROS[Math.floorMod(m / 16, CUEROS.size)])
        rotate(2f, Offset(x0 + ancho, f)) {
            drawRect(t.s(0.3f), Offset(x0 - 1f * u, y + 1.8f * u), Size(ancho + 2f * u, h * 0.03f))
            drawRect(tira, Offset(x0 - 1f * u, y), Size(ancho + 2f * u, h * 0.03f))
            drawRect(t.b(0.3f), Offset(x0 - 1f * u, y), Size(ancho + 2f * u, 0.8f * u))
            drawCircle(tira, h * 0.024f, Offset(x0 + ancho * 0.66f, y + h * 0.015f))
            drawCircle(t.s(0.3f), h * 0.012f, Offset(x0 + ancho * 0.67f, y + h * 0.018f))
            drawLine(tira, Offset(x0 + ancho * 0.66f, y + h * 0.02f), Offset(x0 + ancho * 0.6f, y + h * 0.085f), 1.6f * u, StrokeCap.Round)
            drawLine(tira, Offset(x0 + ancho * 0.66f, y + h * 0.02f), Offset(x0 + ancho * 0.74f, y + h * 0.075f), 1.6f * u, StrokeCap.Round)
            // El punzón: de hierro, con su paleta de borrar arriba.
            val px = x0 + ancho * 0.3f
            drawLine(t.s(0.3f), Offset(px + 1.4f * u, y - h * 0.1f), Offset(px + 1.4f * u, y + h * 0.1f), 1.8f * u)
            drawLine(t.c(mezcla(HIERRO_LUZ, PIEDRA, 0.3f)), Offset(px, y - h * 0.1f), Offset(px, y + h * 0.1f), 1.8f * u, StrokeCap.Round)
            drawRect(t.c(mezcla(HIERRO_LUZ, PIEDRA, 0.3f)), Offset(px - 2.4f * u, y - h * 0.115f), Size(4.8f * u, h * 0.025f))
            drawLine(t.b(0.5f), Offset(px - 0.7f * u, y - h * 0.09f), Offset(px - 0.7f * u, y - h * 0.005f), 0.6f * u)
            drawRect(tira, Offset(px - 3f * u, y), Size(6f * u, h * 0.03f))
        }
    }

    /** Lo que no tiene tema: una caja de madera clara con herrajes de esquina y folios revueltos que asoman torcidos. */
    private fun DrawScope.caja(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val f = h * NICHO - h * 0.022f
        val r = Rect(w * 0.19f, f - h * 0.27f, w * 0.81f, f)
        contacto(Offset(r.center.x, f), r.width * 1.1f, 4f * u, t, 0.42f)
        // El fondo de la caja, por dentro.
        drawRect(t.c(mezcla(ROBLE_OSC, ROBLE, 0.4f)), Offset(r.left + w * 0.02f, r.top - h * 0.04f), Size(r.width - w * 0.02f, h * 0.05f))
        // Los folios revueltos.
        val m = (2 + c.n).coerceIn(3, 8)
        for (i in 0 until m) {
            val x = r.left + r.width * (0.1f + 0.8f * (i + 0.5f) / m)
            val g = (azar(c.semilla, 60 + i) - 0.5f) * 50f
            val alto = h * (0.14f + 0.12f * azar(c.semilla, 70 + i))
            rotate(g, Offset(x, r.top + h * 0.05f)) {
                val hoja = Rect(x - w * 0.085f, r.top - alto, x + w * 0.085f, r.top + h * 0.06f)
                drawRect(t.s(0.2f), Offset(hoja.left + 1.4f * u, hoja.top + 1f * u), hoja.size)
                drawRect(t.c(if (i % 3 == 0) PERGAMINO else if (i % 3 == 1) PERG_OSC else mezcla(PERGAMINO, PERG_OSC, 0.5f)), hoja.topLeft, hoja.size)
                drawRect(t.b(0.35f), hoja.topLeft, Size(hoja.width, 0.8f * u))
                drawRect(t.s(0.2f), Offset(hoja.right - hoja.width * 0.15f, hoja.top), Size(hoja.width * 0.15f, hoja.height))
                for (j in 0 until 3) drawLine(t.c(TINTA).copy(alpha = 0.3f), Offset(hoja.left + hoja.width * 0.15f, hoja.top + hoja.height * (0.2f + 0.12f * j)), Offset(hoja.right - hoja.width * 0.2f, hoja.top + hoja.height * (0.2f + 0.12f * j)), 0.5f * u)
            }
        }
        // El frente: dos tablas de madera clara, la junta, los herrajes de las esquinas.
        val madera = mezcla(ROBLE_LUZ, CAL, 0.18f)
        drawRect(t.c(madera), r.topLeft, r.size)
        vetas(r, ROBLE.copy(alpha = 0.35f), 5100 + Math.floorMod(c.semilla, 13), 0.6f, grosor = 0.6f, ondulacion = 0.5f)
        drawLine(t.s(0.4f), Offset(r.left, r.center.y), Offset(r.right, r.center.y), 1f * u)
        drawLine(t.b(0.3f), Offset(r.left, r.center.y + 1f * u), Offset(r.right, r.center.y + 1f * u), 0.6f * u)
        drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1f * u))
        drawRect(t.s(0.3f), Offset(r.right - r.width * 0.1f, r.top), Size(r.width * 0.1f, r.height))
        listOf(r.topLeft, Offset(r.right, r.top), Offset(r.left, r.bottom), r.bottomRight).forEach { p ->
            val sx = if (p.x < r.center.x) 1f else -1f; val sy = if (p.y < r.center.y) 1f else -1f
            val e = r.height * 0.28f
            drawPath(poligono(p.x, p.y, p.x + sx * e, p.y, p.x, p.y + sy * e), t.c(HIERRO))
            drawCircle(t.c(HIERRO_LUZ), 0.8f * u, Offset(p.x + sx * e * 0.3f, p.y + sy * e * 0.3f))
        }
        // La cerradura: la chapa de hierro con su ojo y la aldaba que baja de la tapa.
        val ch = Rect(r.center.x - r.width * 0.07f, r.top + r.height * 0.14f, r.center.x + r.width * 0.07f, r.top + r.height * 0.56f)
        drawRect(t.s(0.3f), Offset(ch.left + 1.2f * u, ch.top + 1.4f * u), ch.size)
        drawRect(t.c(HIERRO), ch.topLeft, ch.size)
        drawRect(t.b(0.35f), ch.topLeft, Size(ch.width, 0.8f * u))
        drawCircle(t.c(madera), 1.5f * u, Offset(ch.center.x, ch.center.y + ch.height * 0.12f))
        drawRect(t.c(madera), Offset(ch.center.x - 0.6f * u, ch.center.y + ch.height * 0.12f), Size(1.2f * u, ch.height * 0.24f))
        drawRect(t.c(HIERRO), Offset(ch.center.x - r.width * 0.022f, r.top - 1f * u), Size(r.width * 0.044f, ch.top - r.top + ch.height * 0.3f))
        drawRect(t.b(0.3f), Offset(ch.center.x - r.width * 0.022f, r.top - 1f * u), Size(0.8f * u, ch.top - r.top + ch.height * 0.3f))
        listOf(ch.topLeft, Offset(ch.right, ch.top), Offset(ch.left, ch.bottom), ch.bottomRight).forEach { p ->
            drawCircle(t.c(HIERRO_LUZ), 0.7f * u, Offset(p.x + (if (p.x < ch.center.x) 1.6f else -1.6f) * u, p.y + (if (p.y < ch.center.y) 1.6f else -1.6f) * u))
        }
    }

    /** Un hueco todavía sin tema: un cabo de vela en su platillo de barro, o una pluma tumbada junto a un tintero. */
    private fun DrawScope.vacio(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val f = h * NICHO - h * 0.022f
        if (Math.floorMod(mezclar(c.semilla), 2) == 0) {
            val cx = w * 0.42f
            contacto(Offset(cx, f), w * 0.36f, 3.8f * u, t, 0.4f)
            drawPath(Path().apply {
                moveTo(cx - w * 0.15f, f - h * 0.045f)
                cubicTo(cx - w * 0.13f, f, cx + w * 0.13f, f, cx + w * 0.15f, f - h * 0.045f)
                close()
            }, t.c(TERRACOTA))
            drawOval(t.c(mezcla(TERRACOTA, LUZ_MUEBLE, 0.15f)), Offset(cx - w * 0.15f, f - h * 0.058f), Size(w * 0.3f, h * 0.026f))
            drawOval(t.c(mezcla(TERRACOTA, ROBLE_OSC, 0.4f)), Offset(cx - w * 0.125f, f - h * 0.054f), Size(w * 0.25f, h * 0.018f))
            val cera = Rect(cx - w * 0.045f, f - h * 0.19f, cx + w * 0.045f, f - h * 0.045f)
            drawOval(t.c(CERA), Offset(cx - w * 0.09f, f - h * 0.056f), Size(w * 0.18f, h * 0.02f))
            drawRect(t.c(CERA), cera.topLeft, cera.size)
            drawOval(t.c(mezcla(CERA, SOMBRA_MUEBLE, 0.18f)), Offset(cera.left, cera.top - h * 0.008f), Size(cera.width, h * 0.016f))
            drawRect(t.s(0.25f), Offset(cera.right - cera.width * 0.3f, cera.top), Size(cera.width * 0.3f, cera.height))
            drawLine(t.c(mezcla(CERA, LUZ_MUEBLE, 0.5f)), Offset(cera.left + cera.width * 0.2f, cera.top), Offset(cera.left + cera.width * 0.2f, cera.top + cera.height * 0.7f), 2.2f * u, StrokeCap.Round)
            drawLine(t.c(mezcla(CERA, LUZ_MUEBLE, 0.5f)), Offset(cera.left + cera.width * 0.62f, cera.top), Offset(cera.left + cera.width * 0.62f, cera.top + cera.height * 0.4f), 1.8f * u, StrokeCap.Round)
            drawLine(Color(0xFF1C1612), Offset(cx, cera.top), Offset(cx + w * 0.012f, cera.top - h * 0.035f), 1.2f * u, StrokeCap.Round)
            // Unas gotas de cera en el anaquel.
            drawOval(t.c(CERA), Offset(w * 0.66f, f - h * 0.008f), Size(w * 0.035f, h * 0.01f))
            drawOval(t.c(CERA), Offset(w * 0.72f, f - h * 0.004f), Size(w * 0.02f, h * 0.007f))
        } else {
            // El tintero de barro y la pluma tumbada.
            val cx = w * 0.32f
            contacto(Offset(cx, f), w * 0.24f, 3.8f * u, t, 0.4f)
            val tin = Rect(cx - w * 0.08f, f - h * 0.15f, cx + w * 0.08f, f)
            drawRoundRect(t.c(TERRACOTA), tin.topLeft, tin.size, CornerRadius(w * 0.03f))
            drawRect(t.s(0.3f), Offset(tin.right - tin.width * 0.3f, tin.top + 2f * u), Size(tin.width * 0.3f, tin.height - 4f * u))
            drawRect(t.b(0.3f), Offset(tin.left + tin.width * 0.12f, tin.top + 3f * u), Size(tin.width * 0.1f, tin.height - 6f * u))
            drawOval(t.c(mezcla(TERRACOTA, LUZ_MUEBLE, 0.2f)), Offset(tin.left, tin.top - h * 0.014f), Size(tin.width, h * 0.028f))
            drawOval(t.c(TINTA), Offset(tin.left + tin.width * 0.2f, tin.top - h * 0.009f), Size(tin.width * 0.6f, h * 0.016f))
            drawLine(t.c(TINTA).copy(alpha = 0.6f), Offset(tin.left + tin.width * 0.3f, tin.top), Offset(tin.left + tin.width * 0.28f, tin.top + tin.height * 0.5f), 1.6f * u, StrokeCap.Round)
            pluma(Offset(w * 0.46f, f - h * 0.01f), Offset(w * 0.84f, f - h * 0.06f), t, true)
        }
    }

    /** La clase de un tema: 0, folios; 1, códices; 2, rollos; 3, tablillas. */
    private fun clase(m: Int) = Math.floorMod(m, 4)

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val u = density
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este anaquel en el mueble (en fracciones del ancho), para saber cuánto le llega de la vela.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val k = if (!oscuro) 0f else if (dentro) luz(centro) else 0.5f
        // De día, la luz de arriba a la izquierda aclara apenas las filas de arriba y la columna de la izquierda.
        val dia = if (oscuro) 0f else diaSegunAltura((c.fila + 0.5f) / c.filas) + (if (c.col == 0) 0.02f else -0.01f)
        val t = Tono(oscuro, k, dia)
        nicho(w, h, t, c.semilla)
        if (c.vacio) {
            clipPath(arcoMedioPunto(arcoNicho(w, h))) { vacio(w, h, c, t) }
            tablaAnaquel(w, h, t, c.semilla)
            // Los agujeros de los clavos de una etiqueta que ya no está, y la punta de pergamino que quedó bajo uno.
            val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
            val a = Offset(r.left + r.width * 0.14f, r.top + r.height * 0.22f); val b = Offset(r.right - r.width * 0.14f, r.top + r.height * 0.22f)
            drawPath(poligono(a.x - 4f * u, a.y - 2.4f * u, a.x + 9f * u, a.y - 1.6f * u, a.x + 5f * u, a.y + 5f * u, a.x - 3f * u, a.y + 7f * u), t.c(mezcla(PERGAMINO, PERG_OSC, 0.5f)))
            drawCircle(t.c(HIERRO), 1.7f * u, a)
            drawCircle(t.c(ROBLE_OSC), 1.2f * u, b)
            drawCircle(t.b(0.25f), 0.5f * u, b + Offset(0.8f * u, 0.8f * u))
            return
        }
        val m = mezclar(c.semilla)
        // Al tocarlo, lo que guarda se levanta un poco y sube luz de dentro.
        clipPath(arcoMedioPunto(arcoNicho(w, h))) {
            translate(0f, -h * 0.05f * ab) {
                when {
                    c.sinTema -> caja(w, h, c, t)
                    else -> when (clase(m)) {
                        0 -> folios(w, h, c, m, t)
                        1 -> codices(w, h, c, m, t)
                        2 -> rollos(w, h, c, m, t)
                        else -> tablillas(w, h, c, m, t)
                    }
                }
            }
            if (ab > 0f) mancha(Offset(w / 2, h * 0.4f), w * 0.45f, if (oscuro) LLAMA.copy(alpha = 0.22f * ab) else luzMueble(0.3f * ab))
        }
        tablaAnaquel(w, h, t, c.semilla)
        etiqueta(w, h, t, c.semilla, c.sinTema)
    }

    override val rotulo = Rect(0.025f, 0.745f, 0.975f, 0.98f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.cardo
    override val salida = 0f
    override val placa = Rect(0.33f, 0.06f, 0.67f, 0.27f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (los de siempre)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Tablas de roble a lo largo, con sus clavos, y la luz de una vela arriba a la izquierda.
        drawRect(t(ROBLE_OSC, oscuro))
        val alto = 44 * u
        var y = 0f; var k = 0
        while (y < h) {
            val r = Rect(0f, y, w, minOf(y + alto, h))
            drawRect(t(mezcla(ROBLE_OSC, ROBLE, 0.35f + 0.3f * azar(51, k)), oscuro), r.topLeft, r.size)
            vetas(r, Color.Black.copy(alpha = 0.3f), 60 + k, 0.7f, grosor = 0.6f, ondulacion = 0.4f)
            drawLine(Color.Black.copy(alpha = 0.5f), Offset(0f, r.top), Offset(w, r.top), 1.2f * u)
            listOf(22 * u, w - 22 * u).forEach { xx -> drawCircle(t(HIERRO, oscuro), 1.7f * u, Offset(xx, r.center.y)) }
            y += alto; k++
        }
        mancha(Offset(w * 0.2f, h * 0.12f), w * 0.75f, VELA.copy(alpha = if (oscuro) 0.06f else 0.1f))
        // Las bandas de hierro del borde, remachadas.
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(HIERRO, oscuro), r.topLeft, r.size) }
        var p = pl * 2
        while (p < w - pl) { drawCircle(t(HIERRO_LUZ, oscuro), 1.3f * u, Offset(p, pl / 2)); drawCircle(t(HIERRO_LUZ, oscuro), 1.3f * u, Offset(p, h - pl / 2)); p += 20 * u }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 20 * u, 0.35f)
    }

    override fun tintaInterior(oscuro: Boolean) = PERGAMINO
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 28, 0.006f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.7f else 1f
        drawRect(PERGAMINO.copy(alpha = 0.35f))
        // El lado del pelo, más amarillo hacia los bordes.
        val b = 6 * u * g
        drawRect(PERG_OSC.copy(alpha = 0.22f), Offset.Zero, Size(w, b))
        drawRect(PERG_OSC.copy(alpha = 0.22f), Offset(0f, h - b), Size(w, b))
        drawRect(PERG_OSC.copy(alpha = 0.18f), Offset.Zero, Size(b, h))
        drawRect(PERG_OSC.copy(alpha = 0.18f), Offset(w - b, 0f), Size(b, h))
        // El pautado del margen: a la izquierda en rojo y azul alternados (un filete de pluma), a la derecha en rojo.
        val m = 7 * u * g
        val tramo = 14 * u * g
        var y = 0f; var k = 0
        while (y < h) { drawLine((if (k % 2 == 0) BERMELLON else LAPIS).copy(alpha = 0.4f), Offset(m, y), Offset(m, minOf(y + tramo, h)), 1f * u * g); y += tramo; k++ }
        drawLine(BERMELLON.copy(alpha = 0.3f), Offset(w - m, 0f), Offset(w - m, h), 0.7f * u)
        // Los picados del margen, para el pautado.
        y = 10 * u * g
        while (y < h - 6 * u) { drawCircle(TINTA.copy(alpha = 0.3f), 0.7f * u * g, Offset(w - 3 * u * g, y)); y += 9 * u * g }
    }

    override val margenes = MargenesFicha(12.dp, 14.dp, 12.dp, 24.dp, 28.dp, 28.dp)
    override val proporcionFicha = 0.8f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.68f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El sello pendiente del documento.
            "hecho" -> selloHecho(LAPIS, p.color)
            // El calderón rojo (¶) de los rubricadores.
            "convence" -> {
                drawCircle(BERMELLON, s * 0.19f, Offset(c.x - s * 0.1f, c.y - s * 0.16f))
                drawLine(BERMELLON, Offset(c.x + s * 0.04f, c.y - s * 0.36f), Offset(c.x + s * 0.04f, c.y + s * 0.4f), s * 0.09f)
                drawLine(BERMELLON, Offset(c.x + s * 0.22f, c.y - s * 0.36f), Offset(c.x + s * 0.22f, c.y + s * 0.4f), s * 0.09f)
                drawLine(BERMELLON, Offset(c.x - s * 0.1f, c.y - s * 0.36f), Offset(c.x + s * 0.28f, c.y - s * 0.36f), s * 0.09f)
            }
            // Un trébol azul.
            "piensa" -> {
                for (k in 0 until 3) {
                    val a = Math.toRadians(-90.0 + k * 120.0)
                    drawCircle(LAPIS, s * 0.17f, Offset(c.x + s * 0.18f * cos(a).toFloat(), c.y - s * 0.04f + s * 0.18f * sin(a).toFloat()))
                }
                drawLine(LAPIS, Offset(c.x, c.y + s * 0.1f), Offset(c.x, c.y + s * 0.44f), s * 0.08f)
            }
            // El óbelo (÷): así marcaban los copistas un pasaje dudoso.
            "duda" -> {
                drawLine(p.tenue, Offset(c.x - s * 0.36f, c.y), Offset(c.x + s * 0.36f, c.y), s * 0.1f, StrokeCap.Round)
                drawCircle(p.tenue, s * 0.08f, Offset(c.x, c.y - s * 0.25f)); drawCircle(p.tenue, s * 0.08f, Offset(c.x, c.y + s * 0.25f))
            }
            "desacuerdo" -> aspa(BERMELLON)
            else -> {}
        }
    }

    /** Un broche de libro: la correa de cuero y su cierre de latón. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawRect(ROBLE_OSC, Offset(c.x - s * 0.46f, c.y - s * 0.14f), Size(s * 0.56f, s * 0.28f))
        drawRoundRect(ORO, Offset(c.x - s * 0.02f, c.y - s * 0.24f), Size(s * 0.46f, s * 0.48f), CornerRadius(s * 0.1f))
        drawCircle(ROBLE_OSC, s * 0.07f, Offset(c.x + s * 0.21f, c.y))
    }

    override val desorden = 1f
}
