package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
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

// =====================================================================
//  Tierras Antiguas
// =====================================================================

/**
 * Tierras Antiguas: el rincón de la posada del paso donde la guía de caminos guarda el equipo y los mapas de la compañía
 * (fantasía propia). Es un interior sin ventana: un muro de sillares toscos de piedra de montaña, gris cálida y fría, con musgo
 * en las juntas bajas y, a media altura, un friso de tablas de pino con runas talladas (las inventadas del tema); la luz de
 * día entra de arriba a la izquierda, fría de altura, fuera del cuadro. El mueble es un estante de pino viejo: los montantes
 * tallados con una soga que se anuda a la altura de cada balda, las clavijas de cada ensamble, la tapa gruesa con sus
 * cortes de cuchillo, las baldas y el zócalo con runas, y las patas sobre el suelo de losas. Arriba, el punto focal: el mapa
 * grande de los caminos (el paso entre dos cordales, el río, el sendero punteado desde la posada, el bosque y la rosa de los
 * vientos) clavado en el friso con clavos y un cuchillo, y cruzándolo en diagonal el bastón de guía, con sus muescas de
 * cuenta, el pomo tallado y dos cintas atadas que cuelgan. Además, un manojo de hierbas de montaña secándose, el cuerno de
 * llamada colgado de su correa, la capa de viaje colgada con su broche (cortada por el borde derecho), y en la tapa el rollo
 * de cuerda con su garfio, el cuenco con las piedras de afilar, la cantimplora de cuero y la linterna de cuerno. Al pie, unas
 * botas embarradas (una caída) y la mochila de marco de madera con la manta enrollada, cortada por el borde. Cada hueco
 * deja ver el muro de detrás; cada tema es, según la semilla, el arcón de la compañía (tablas con flejes de hierro, tapa
 * abovedada y cerradura con gema), un tubo portamapas de cuero en sus horquillas con su tablilla, o un zurrón de cuero con
 * solapa y hebillas; asoman mapas enrollados y páginas (más cuanto más guarda) y el tema va en una placa de latón o en la
 * tablilla. Lo que no tiene tema es un cesto de mimbre con rollos sueltos y la tablilla sin grabar; un hueco libre lleva una
 * piedra con una runa pintada o una vela apagada en su palmatoria. Al abrir uno, su contenido sube. De noche todo se hunde
 * en un azul verdoso hondo: alumbra solo la linterna (halo cálido en la piedra, la capa y el cuerno, el mapa encendido
 * cerca y perdido lejos), las filas de huecos se apagan de arriba abajo y las gemas guardan un punto de brillo verde propio.
 * Por dentro, forro de terciopelo verde capitoné. Las fichas son páginas del cronista: vitela de borde barbado con nudos en
 * las esquinas y un camino punteado con su rosa de los vientos.
 */
object IdeasAntiguas : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del rincón
    /** La piedra de montaña: gris frío del granito, gris cálido de la arenisca, el mortero y el musgo de las juntas. */
    private val PIEDRA_FRIA = Color(0xFF7E817F)
    private val PIEDRA_CALIDA = Color(0xFF978C7B)
    private val MORTERO = Color(0xFF4B4842)
    private val MUSGO = Color(0xFF5F7142)
    /** El pino viejo del estante, del friso y de las tablillas: su luz y su sombra. */
    private val PINO = Color(0xFFAA8656)
    private val PINO_LUZ = Color(0xFFCFAC78)
    private val PINO_OSC = Color(0xFF6A4E30)
    /** El hierro, el latón, el cuero y la vitela. */
    private val HIERRO = Color(0xFF3B3B40)
    private val LATON = Color(0xFFC9A855)
    private val CUERO = Color(0xFF7C4F2E)
    private val CUERO_OSC = Color(0xFF4A2F1C)
    private val VITELA = Color(0xFFE9DCBB)
    private val VITELA_OSC = Color(0xFFC4B08A)
    /** La lana verde gris de la capa, el cuerno, la cuerda, la cera, el barro y las hierbas. */
    private val LANA = Color(0xFF56604F)
    private val CUERNO = Color(0xFFD8BE86)
    private val CUERDA = Color(0xFFB79E6C)
    private val CERA = Color(0xFFE6DCC0)
    private val BARRO = Color(0xFF5C4936)
    private val HIERBA = Color(0xFF7D8858)
    /** La tinta del mapa y el agua del mapa (verde gris, no azul). */
    private val TINTA_MAPA = Color(0xFF4A3524)
    private val AGUA_MAPA = Color(0xFF5E7E80)
    /** La llama de la linterna y el brillo propio de las gemas, de noche. */
    private val LLAMA = Color(0xFFFFC56E)
    private val GEMA_LUZ = Color(0xFF8BE6AE)
    /** La noche del rincón: azul verdoso hondo. */
    private val NOCHE = Color(0xFF0C1C20)
    /**
     * Las placas del tema: el latón y la tablilla de pino claro. De noche, lejos de la linterna, bajan hasta [LATON_LEJOS] y
     * [TABLILLA_LEJOS]: con [TINTA] dan unos 6:1 en el título (medido en la imagen, con el grano).
     */
    private val LATON_PLACA = Color(0xFFD6BB76)
    private val LATON_LEJOS = Color(0xFF9F906C)
    private val TABLILLA = Color(0xFFDCCAA2)
    private val TABLILLA_LEJOS = Color(0xFF9E9278)
    /** Los del interior y las fichas (sin cambios). */
    private val MADERA = Color(0xFF5A3A22)
    private val MADERA_OSC = Color(0xFF33210F)
    private val ORO = Color(0xFFC9A04A)
    private val ESMERALDA = Color(0xFF2E7A5A)
    private val RUBI = Color(0xFF8E1B24)
    private val TERCIOPELO = Color(0xFF1F4A38)
    private val TINTA = Color(0xFF1E1D1A)

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.3f
    override val corona = 0.64f
    override val base = 0.27f
    override val lado = 0.06f
    override val hueco = 0.045f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** En la corona, en fracciones del ancho: el friso, la tapa vista desde arriba, su canto de frente y lo bajo del canto. */
    private const val F0 = 0.055f
    private const val F1 = 0.125f
    private const val SUP = 0.53f
    private const val FRENTE = 0.565f
    private const val CANTO = 0.605f
    /** En la base, en fracción de su alto: lo bajo del zócalo, el rincón del muro con el suelo, las patas y el pie de las cosas. */
    private const val ZOC1 = 0.3f
    private const val SUELO = 0.36f
    private const val PISAN = 0.44f
    /** Los montantes de los costados, en fracción del ancho. */
    private const val IZQ0 = 0.012f
    private const val DER1 = 0.988f
    /** La balda de cada hueco y cuánto sube su contenido al abrirlo, en fracción de su alto. */
    private const val PISO = 0.9f
    private const val SALIDA = 0.07f

    // ---------------------------------------------------------------- la luz y la noche

    /** La llama de la linterna, en fracciones del ancho. */
    private val LUZ = Offset(0.785f, 0.465f)

    /** Cuánto le llega de la linterna a un punto [p] (en fracciones del ancho): cae con la distancia; lo lejano, casi nada. */
    private fun luz(p: Offset): Float {
        val dx = p.x - LUZ.x
        val dy = (p.y - LUZ.y) * (if (p.y > LUZ.y) 0.75f else 1f)
        val radial = 1.38f * kotlin.math.exp(-3.4f * kotlin.math.sqrt(dx * dx + dy * dy))
        return maxOf(radial, luzCuerpo(p)).coerceIn(0.03f, 1f)
    }

    /**
     * La luz que la linterna echa hacia abajo sobre el estante: cae de fila en fila (la primera, encendida; la segunda, a medias;
     * la tercera, tenue; las de abajo, casi perdidas), algo menos en la columna de la izquierda. Los puntos son los centros de
     * las filas con 9 temas, en fracciones del ancho.
     */
    private val CAIDA = listOf(0.64f to 0.85f, 0.846f to 0.78f, 1.212f to 0.5f, 1.579f to 0.25f, 1.945f to 0.08f, 2.311f to 0.04f, 2.75f to 0.03f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.5f * (abs(p.x - LUZ.x) - 0.05f).coerceAtLeast(0f)).coerceIn(0.7f, 1f)
        return k * lado
    }

    /** El color propio de algo, hundido en la noche: oscuro, de su mismo tono, con el aire azul verdoso. */
    private fun hondo(c: Color) = Color(c.red * 0.13f + NOCHE.red * 0.8f, c.green * 0.13f + NOCHE.green * 0.8f, c.blue * 0.13f + NOCHE.blue * 0.8f, c.alpha)

    /** Cómo se pinta algo según dónde está. De día, tal cual (con [dia], un poco más claro); de noche, según [k], lo que le llega. */
    private class Tono(val o: Boolean, val k: Float, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.9f))
        return mezcla(v, LLAMA, 0.14f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, la noche del rincón. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la llama. */
    private fun Tono.b(a: Float): Color = if (!o) luzMueble(a) else LLAMA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f))

    /** Lo que sabe el mueble que se está pintando: si es de noche y su ancho. */
    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset): Tono = Tono(esNoche, if (esNoche) luz(p / anchoW) else 0f)

    /** De noche la luz viene de la linterna: lo que está a su izquierda la recibe por la derecha. */
    private fun luzDerecha(x: Float) = esNoche && x / anchoW < LUZ.x - 0.02f

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    private fun Path.ir(p: Offset) = moveTo(p.x, p.y)
    private fun Path.curva(a: Offset, b: Offset, c: Offset) = cubicTo(a.x, a.y, b.x, b.y, c.x, c.y)

    /** Un punto de la curva de Bézier cúbica en [s] (de 0 a 1). */
    private fun punto(p0: Offset, p1: Offset, p2: Offset, p3: Offset, s: Float): Offset {
        val m = 1f - s
        return p0 * (m * m * m) + p1 * (3f * m * m * s) + p2 * (3f * m * s * s) + p3 * (s * s * s)
    }

    private fun normal(p0: Offset, p1: Offset, p2: Offset, p3: Offset, s: Float): Offset {
        val m = 1f - s
        val d = (p1 - p0) * (3f * m * m) + (p2 - p1) * (6f * m * s) + (p3 - p2) * (3f * s * s)
        val l = d.getDistance().coerceAtLeast(0.001f)
        return Offset(-d.y / l, d.x / l)
    }

    /** Un cuerpo largo que sigue una curva (un cuerno, una cinta, un bastón): de radio [r0] en un extremo a [r1] en el otro. */
    private fun cuerpo(p0: Offset, p1: Offset, p2: Offset, p3: Offset, r0: Float, r1: Float, pasos: Int = 14): Path {
        val a = ArrayList<Offset>(); val b = ArrayList<Offset>()
        for (i in 0..pasos) {
            val s = i / pasos.toFloat()
            val p = punto(p0, p1, p2, p3, s); val n = normal(p0, p1, p2, p3, s)
            val r = r0 + (r1 - r0) * s
            a.add(p + n * r); b.add(p - n * r)
        }
        return Path().apply { ir(a[0]); for (q in a.drop(1)) lineTo(q.x, q.y); for (q in b.reversed()) lineTo(q.x, q.y); close() }
    }

    /** Una línea paralela a la curva, corrida [f] veces el radio hacia un lado (el brillo o la sombra de un cuerpo largo). */
    private fun paralela(p0: Offset, p1: Offset, p2: Offset, p3: Offset, r0: Float, r1: Float, f: Float, desde: Float = 0f, hasta: Float = 1f): Path = Path().apply {
        val pasos = 12
        for (i in 0..pasos) {
            val s = desde + (hasta - desde) * i / pasos
            val q = punto(p0, p1, p2, p3, s) + normal(p0, p1, p2, p3, s) * ((r0 + (r1 - r0) * s) * f)
            if (i == 0) ir(q) else lineTo(q.x, q.y)
        }
    }

    private fun puntos(a: Float, b: Float) = PathEffect.dashPathEffect(floatArrayOf(a, b))

    /**
     * La sombra de algo en la pared: de día corrida a la derecha y abajo; de noche, hacia fuera de la linterna, tanto más
     * marcada cuanto más cerca está de ella. No baja de [hasta] (la tapa que la corta).
     */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, a: Float = 0.18f) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.02f, w * 0.012f) { drawPath(p, sombraMueble(a)) }
            else {
                val k = luz(desde / w)
                if (k > 0.12f) {
                    val dx = (desde.x / w - LUZ.x).coerceIn(-0.3f, 0.3f) / 0.3f
                    translate(w * 0.035f * dx, w * 0.01f) { drawPath(p, NOCHE.copy(alpha = (0.6f * k).coerceAtMost(0.5f))) }
                }
            }
        }
    }

    /** La sombra de contacto de lo que pisa la tapa, la balda o el suelo, corrida a la derecha. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float, o: Boolean = esNoche) =
        drawOval(if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /** Un cuerpo tumbado dentro de [r]: la luz arriba, la sombra abajo (ir dentro de su recorte). */
    private fun DrawScope.tumbado(r: Rect, t: Tono, fuerza: Float = 1f) {
        drawRect(t.b(0.18f * fuerza), r.topLeft, Size(r.width, r.height * 0.32f))
        drawRect(t.b(0.3f * fuerza), Offset(r.left, r.top + r.height * 0.16f), Size(r.width, r.height * 0.08f))
        drawRect(t.s(0.16f * fuerza), Offset(r.left, r.top + r.height * 0.6f), Size(r.width, r.height * 0.4f))
        drawRect(t.s(0.22f * fuerza), Offset(r.left, r.top + r.height * 0.82f), Size(r.width, r.height * 0.18f))
    }

    /** El filo cálido de la linterna en un recipiente, de noche: el canto de arriba y el costado que mira a la luz. */
    private fun DrawScope.filo(r: Rect, t: Tono, der: Boolean) {
        if (!t.o || t.k < 0.06f) return
        val u = density
        drawRect(LLAMA.copy(alpha = (0.85f * t.k).coerceAtMost(0.75f)), r.topLeft, Size(r.width, 1.5f * u))
        drawRect(LLAMA.copy(alpha = (0.3f * t.k).coerceAtMost(0.3f)), Offset(r.left, r.top + 1.5f * u), Size(r.width, 3f * u))
        val x = if (der) r.right - 1.3f * u else r.left
        drawRect(LLAMA.copy(alpha = (0.6f * t.k).coerceAtMost(0.55f)), Offset(x, r.top), Size(1.3f * u, r.height))
    }

    /** El destello de la linterna en el latón o el hierro, de noche (de día, la luz crema). */
    private fun DrawScope.destello(c: Offset, r: Float, t: Tono) {
        if (t.o) { if (t.k > 0.06f) { drawCircle(LLAMA.copy(alpha = (0.35f * t.k).coerceAtMost(0.3f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(luzMueble(0.8f), r, c)
    }

    /** Un clavo de hierro de cabeza redonda, con su sombra y su brillo. */
    private fun DrawScope.clavo(p: Offset, r: Float, t: Tono) {
        drawCircle(t.s(0.4f), r * 1.05f, p + Offset(r * 0.6f, r * 0.6f))
        drawCircle(t.c(HIERRO), r, p)
        drawCircle(t.b(0.55f), r * 0.4f, p - Offset(r * 0.3f, r * 0.3f))
    }

    /** Una clavija de colgar que sale del friso: la cabeza redonda de pino con su sombra. */
    private fun DrawScope.clavija(p: Offset, r: Float, t: Tono) {
        drawCircle(t.s(0.4f), r * 1.1f, p + Offset(r * 0.7f, r * 0.8f))
        drawCircle(t.c(PINO_OSC), r, p)
        drawCircle(t.c(PINO), r * 0.78f, p - Offset(r * 0.1f, r * 0.1f))
        drawCircle(t.b(0.4f), r * 0.3f, p - Offset(r * 0.35f, r * 0.35f))
    }

    /** Una gema tallada: un rombo con sus facetas y un brillo. */
    private fun DrawScope.gema(c: Offset, r: Float, color: Color) {
        val p = Path().apply { moveTo(c.x, c.y - r); lineTo(c.x + r, c.y); lineTo(c.x, c.y + r); lineTo(c.x - r, c.y); close() }
        drawPath(p, color)
        drawPath(Path().apply { moveTo(c.x, c.y - r); lineTo(c.x + r, c.y); lineTo(c.x, c.y); close() }, mezcla(color, Color.White, 0.35f))
        drawPath(Path().apply { moveTo(c.x - r, c.y); lineTo(c.x, c.y + r); lineTo(c.x, c.y); close() }, mezcla(color, Color.Black, 0.3f))
        drawPath(p, ORO, style = Stroke(r * 0.18f))
    }

    /** La gema de una cerradura con la luz del sitio; de noche guarda un punto de brillo verde propio, muy tenue. */
    private fun DrawScope.gemaViva(c: Offset, r: Float, t: Tono) {
        val u = density
        if (t.o) {
            gema(c, r, mezcla(hondo(ESMERALDA), ESMERALDA, 0.35f + 0.5f * t.k))
            drawCircle(GEMA_LUZ.copy(alpha = 0.12f), r * 2.3f, c)
            drawCircle(GEMA_LUZ.copy(alpha = 0.8f), r * 0.3f, c - Offset(r * 0.15f, r * 0.2f))
        } else {
            gema(c, r, ESMERALDA)
            drawCircle(luzMueble(0.85f), 0.8f * u, c - Offset(r * 0.35f, r * 0.4f))
        }
    }

    /** Una runa inventada: dos a cuatro trazos rectos, siempre los mismos para cada [k]. */
    private fun DrawScope.runa(k: Int, o: Offset, s: Float, color: Color, grosor: Float) {
        drawLine(color, Offset(o.x, o.y - s / 2), Offset(o.x, o.y + s / 2), grosor, StrokeCap.Round)
        val trazos = 1 + k.mod(3)
        for (j in 0 until trazos) {
            val y0 = o.y - s / 2 + s * (0.1f + 0.35f * j)
            val dx = if ((k + j) % 2 == 0) s * 0.4f else -s * 0.4f
            drawLine(color, Offset(o.x, y0), Offset(o.x + dx, y0 + s * (if (k % 4 < 2) 0.3f else -0.2f + 0.5f * (j % 2))), grosor, StrokeCap.Round)
        }
    }

    /** Una runa tallada en la madera: el surco en sombra y el labio de abajo a la derecha con luz. */
    private fun DrawScope.runaTallada(k: Int, o: Offset, s: Float, t: Tono, grosor: Float) {
        runa(k, o, s, t.s(0.6f), grosor)
        runa(k, o + Offset(grosor * 0.6f, grosor * 0.6f), s, t.b(0.3f), grosor * 0.45f)
    }

    /** Una hojita: un óvalo girado [giro] grados con su mitad en sombra. */
    private fun DrawScope.hojita(c: Offset, largo: Float, giro: Float, col: Color, t: Tono) {
        rotate(giro, c) {
            drawOval(col, Offset(c.x - largo * 0.2f, c.y - largo / 2), Size(largo * 0.4f, largo))
            drawOval(t.s(0.22f), Offset(c.x, c.y - largo * 0.45f), Size(largo * 0.18f, largo * 0.9f))
        }
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
        val sup = w * SUP; val frente = w * FRENTE
        val suelo = b.top + hb * SUELO
        val pieA = sup + (frente - sup) * 0.35f
        val pieF = sup + (frente - sup) * 0.8f

        // ---- el muro de sillares (arriba y bajo el zócalo), su luz y el suelo de losas
        val arriba = Rect(0f, 0f, w, ch + d.separacion)
        pared(arriba, 53)
        luzDePared(arriba)
        bajoElEstante(Rect(0f, b.top, w, suelo + 2f * u))
        piso(w, suelo, alto)

        // ---- el friso de pino con sus runas, y lo colgado
        friso(w)
        hierbas(Offset(w * 0.085f, w * 0.09f))
        mapa(Rect(w * 0.245f, w * 0.1f, w * 0.655f, w * 0.405f))
        cuerno(Offset(w * 0.735f, w * 0.09f))
        capa(Offset(w * 0.935f, w * 0.085f))

        // ---- el estante y su tapa
        estante(d, b)
        tapa(w)

        // ---- lo de encima, de atrás adelante
        baston(Offset(w * 0.19f, pieF - w * 0.004f), Offset(w * 0.355f, w * 0.014f))
        cantimplora(Offset(w * 0.622f, sup + w * 0.004f))
        linterna(Offset(w * LUZ.x, pieA))
        cuenco(Offset(w * 0.5f, pieF))
        cuerda(Offset(w * 0.1f, pieF))

        // ---- al pie: las botas y la mochila, cortada por el borde derecho
        botas(b.top + hb * 0.9f)
        mochila(b.top + hb * 0.93f, b)

        // ---- la placa de «y N más»: una tablilla clavada en el zócalo
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            drawRect(t.s(0.35f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size)
            drawRect(if (o) mezcla(TABLILLA_LEJOS, TABLILLA, (1.3f * t.k.pow(0.6f)).coerceAtMost(1f)) else TABLILLA, r.topLeft, r.size)
            vetas(r, (if (o) NOCHE else PINO).copy(alpha = 0.18f), 6901, 0.5f, grosor = 0.4f, ondulacion = 0.4f)
            drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1f * u))
            drawRect(t.s(0.3f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
            listOf(r.left + 3f * u, r.right - 3f * u).forEach { x -> clavo(Offset(x, r.center.y), 1.2f * u, t) }
        }

        // ---- la luz de la linterna, por encima de lo que tiene cerca
        if (o) {
            val l = LUZ * w
            mancha(l + Offset(0f, w * 0.03f), w * 0.6f, LLAMA.copy(alpha = 0.12f))
            mancha(l, w * 0.3f, LLAMA.copy(alpha = 0.26f))
            mancha(l + Offset(w * 0.02f, -w * 0.06f), w * 0.2f, LLAMA.copy(alpha = 0.22f))
            mancha(l, w * 0.07f, Color(0xFFFFE6B0).copy(alpha = 0.45f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    // ---------------------------------------------------------------- el muro y el suelo

    /** El muro de sillares toscos en [r]: hiladas de alto desigual, juntas que no casan, cada piedra con su tono. */
    private fun DrawScope.pared(r: Rect, semilla: Int) {
        val w = anchoW
        drawRect(tn(r.center).c(MORTERO), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top - w * 0.012f * azar(semilla, 0)
            var fila = 0
            while (y < r.bottom) {
                val alto = w * (0.05f + 0.028f * azar(semilla, 100 + fila))
                var x = -w * 0.07f * azar(semilla, 200 + fila)
                var k = 0
                while (x < w) {
                    val largo = w * (0.07f + 0.12f * azar(semilla + fila, k))
                    // Algunas piedras son más bajas que la hilada y dejan una cuña de ripio debajo.
                    val bajo = if (azar(semilla + 3, fila * 13 + k) > 0.7f) alto * 0.55f else alto
                    sillar(Rect(x, y, x + largo, y + bajo), semilla * 7 + fila * 31 + k)
                    if (bajo < alto) sillar(Rect(x + largo * 0.15f, y + bajo, x + largo * 0.85f, y + alto), semilla * 11 + fila * 37 + k)
                    x += largo; k++
                }
                y += alto; fila++
            }
        }
    }

    /** Un sillar tosco: las esquinas comidas, los cantos que no son rectos, el bisel de luz arriba a la izquierda y sus poros. */
    private fun DrawScope.sillar(r: Rect, s: Int) {
        val u = density
        val j = 1.1f * u + 0.9f * u * azar(s, 1)
        val a = Rect(r.left + j, r.top + j, r.right - j, r.bottom - j)
        if (a.width < 3f * u || a.height < 3f * u) return
        val t = tn(a.center)
        val e = minOf(a.width, a.height) * 0.3f
        fun q(k: Int) = e * azar(s, 10 + k)
        fun m(k: Int) = (azar(s, 30 + k) - 0.5f) * e * 0.9f
        val pts = listOf(
            Offset(a.left + q(0), a.top), Offset(a.center.x + m(0), a.top + m(1)), Offset(a.right - q(1), a.top),
            Offset(a.right, a.top + q(2)), Offset(a.right + m(2), a.center.y), Offset(a.right, a.bottom - q(3)),
            Offset(a.right - q(4), a.bottom), Offset(a.center.x + m(3), a.bottom + m(4)), Offset(a.left + q(5), a.bottom),
            Offset(a.left, a.bottom - q(6)), Offset(a.left + m(5), a.center.y), Offset(a.left, a.top + q(7)),
        )
        val forma = Path().apply { ir(pts[0]); pts.drop(1).forEach { lineTo(it.x, it.y) }; close() }
        var col = mezcla(PIEDRA_FRIA, PIEDRA_CALIDA, azar(s, 2))
        col = mezcla(col, MORTERO, 0.35f * azar(s, 3))
        col = mezcla(col, LUZ_MUEBLE, 0.18f * azar(s, 4))
        drawPath(forma, t.c(col))
        // El bisel: luz en el canto de arriba y el de la izquierda; sombra abajo y a la derecha.
        val luzP = Path().apply { ir(pts[9]); lineTo(pts[10].x, pts[10].y); lineTo(pts[11].x, pts[11].y); lineTo(pts[0].x, pts[0].y); lineTo(pts[1].x, pts[1].y); lineTo(pts[2].x, pts[2].y) }
        val somP = Path().apply { ir(pts[3]); lineTo(pts[4].x, pts[4].y); lineTo(pts[5].x, pts[5].y); lineTo(pts[6].x, pts[6].y); lineTo(pts[7].x, pts[7].y); lineTo(pts[8].x, pts[8].y) }
        drawPath(luzP, t.b(0.32f), style = Stroke(1.3f * u))
        drawPath(somP, t.s(0.38f), style = Stroke(1.6f * u))
        // Poros y una veta de la piedra.
        if (azar(s, 5) > 0.4f) drawCircle(t.s(0.3f), 0.9f * u, Offset(a.left + a.width * azar(s, 6), a.top + a.height * azar(s, 7)))
        if (azar(s, 8) > 0.6f) drawLine(t.s(0.18f), Offset(a.left + a.width * 0.2f, a.top + a.height * azar(s, 9)), Offset(a.left + a.width * 0.6f, a.top + a.height * azar(s, 12)), 0.6f * u)
    }

    /** La luz de día en el muro: la mancha arriba a la izquierda, la sombra hacia la derecha y unas pinceladas. */
    private fun DrawScope.luzDePared(r: Rect) {
        val u = density
        val w = anchoW
        if (!esNoche) {
            mancha(Offset(w * 0.02f, -w * 0.04f), w * 0.85f, luzMueble(0.36f))
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, sombraMueble(0.2f)), w * 0.35f, w), r.topLeft, r.size)
        }
        pinceladas(r, if (esNoche) NOCHE else PIEDRA_FRIA, 5310, 26, u, 0.08f)
    }

    /** Lo que se ve bajo el zócalo, entre las patas: el muro en sombra con el musgo de las juntas bajas. */
    private fun DrawScope.bajoElEstante(r: Rect) {
        val u = density
        val w = anchoW
        val t = tn(r.center)
        drawRect(t.c(mezcla(MORTERO, PIEDRA_FRIA, 0.3f)), r.topLeft, r.size)
        drawRect(t.s(0.45f), r.topLeft, r.size)
        for (k in 0 until 9) {
            val x = w * (0.05f + 0.1f * k + 0.04f * azar(5320, k))
            drawOval(t.c(MUSGO), Offset(x, r.bottom - 4f * u), Size(w * (0.03f + 0.03f * azar(5321, k)), 4.5f * u))
            drawOval(t.c(mezcla(MUSGO, PINO_LUZ, 0.3f)), Offset(x + 1f * u, r.bottom - 4f * u), Size(w * 0.015f, 1.6f * u))
        }
    }

    /** El suelo de losas que se abren hacia quien mira: cada losa con su tono, sus juntas, el musgo del rincón y el barro. */
    private fun DrawScope.piso(w: Float, suelo: Float, alto: Float) {
        val u = density
        val t = tn(Offset(w * 0.5f, alto))
        drawRect(t.c(MORTERO), Offset(0f, suelo), Size(w, alto - suelo))
        val y1 = suelo + (alto - suelo) * 0.4f
        fun abre(x: Float, y: Float): Float = w / 2 + (x - w / 2) * (1f + 0.6f * (y - suelo) / (alto - suelo))
        listOf(suelo to y1, y1 to alto).forEachIndexed { fila, (ya, yb) ->
            var x = -w * 0.1f * azar(5330, fila)
            var k = 0
            while (x < w * 1.1f) {
                val largo = w * (0.15f + 0.08f * azar(5331 + fila, k))
                val j = 1.3f * u
                val forma = poligono(abre(x, ya) + j, ya + j, abre(x + largo, ya) - j, ya + j, abre(x + largo, yb) - j, yb - j, abre(x, yb) + j, yb - j)
                val tt = tn(Offset(abre(x + largo / 2, yb), (ya + yb) / 2))
                drawPath(forma, tt.c(mezcla(mezcla(PIEDRA_FRIA, PIEDRA_CALIDA, azar(5332 + fila, k)), MORTERO, 0.25f + 0.15f * azar(5333, k))))
                drawLine(tt.b(0.3f), Offset(abre(x, ya) + j, ya + j), Offset(abre(x + largo, ya) - j, ya + j), 1.1f * u)
                drawLine(tt.s(0.3f), Offset(abre(x + largo, ya) - j, ya + j), Offset(abre(x + largo, yb) - j, yb - j), 1.2f * u)
                x += largo; k++
            }
        }
        pinceladas(Rect(0f, suelo, w, alto), if (esNoche) NOCHE else PIEDRA_FRIA, 5340, 14, u, 0.12f)
        // El barro que dejaron las botas.
        for (k in 0 until 6) drawOval(t.c(BARRO).copy(alpha = 0.55f), Offset(w * (0.08f + 0.26f * azar(5341, k)), suelo + (alto - suelo) * (0.55f + 0.4f * azar(5342, k))), Size(w * (0.012f + 0.015f * azar(5343, k)), 2.5f * u))
        // La sombra del rincón.
        val tt = tn(Offset(w * 0.5f, suelo))
        drawRect(tt.s(0.4f), Offset(0f, suelo), Size(w, 2.5f * u))
        drawRect(tt.s(0.16f), Offset(0f, suelo + 2.5f * u), Size(w, 5f * u))
    }

    /** El friso de tablas de pino con su hilera de runas talladas, separadas por puntos, y las clavijas de sus juntas. */
    private fun DrawScope.friso(w: Float) {
        val u = density
        val y0 = w * F0; val y1 = w * F1
        // Su sombra en el muro, debajo.
        for (s in 0 until 6) {
            val tt = tn(Offset(w * (s + 0.5f) / 6f, y1))
            drawRect(tt.s(0.32f), Offset(w * s / 6f, y1), Size(w / 6f + 1f, 2.5f * u))
            drawRect(tt.s(0.14f), Offset(w * s / 6f, y1 + 2.5f * u), Size(w / 6f + 1f, 4f * u))
        }
        val cortes = listOf(0f, 0.31f, 0.68f, 1f)
        for (i in 0 until 3) {
            val r = Rect(w * cortes[i], y0, w * cortes[i + 1], y1)
            for (s in 0 until 4) {
                val x0 = r.left + r.width * s / 4f
                val tt = tn(Offset(x0 + r.width / 8f, (y0 + y1) / 2))
                drawRect(tt.c(mezcla(PINO, PINO_OSC, 0.25f + 0.12f * i)), Offset(x0, y0), Size(r.width / 4f + 1f, y1 - y0))
            }
            vetas(r, (if (esNoche) NOCHE else PINO_OSC).copy(alpha = 0.4f), 5500 + i, 0.7f, grosor = 0.6f, ondulacion = 0.5f)
            if (i > 0) {
                val tt = tn(r.topLeft)
                drawRect(tt.s(0.5f), Offset(r.left - 0.7f * u, y0), Size(1.4f * u, y1 - y0))
                clavija(Offset(r.left + w * 0.012f, y0 + (y1 - y0) * 0.3f), 1.6f * u, tt)
                clavija(Offset(r.left + w * 0.012f, y0 + (y1 - y0) * 0.72f), 1.6f * u, tt)
            }
        }
        // Las molduras: el canto de arriba con luz, la ranura y el canto de abajo.
        for (s in 0 until 6) {
            val x = w * s / 6f
            val tt = tn(Offset(x + w / 12f, y0))
            drawRect(tt.b(0.5f), Offset(x, y0), Size(w / 6f + 1f, 1.2f * u))
            drawRect(tt.s(0.35f), Offset(x, y0 + (y1 - y0) * 0.14f), Size(w / 6f + 1f, 0.9f * u))
            drawRect(tt.b(0.25f), Offset(x, y0 + (y1 - y0) * 0.14f + 0.9f * u), Size(w / 6f + 1f, 0.7f * u))
            drawRect(tt.s(0.3f), Offset(x, y1 - (y1 - y0) * 0.16f), Size(w / 6f + 1f, 0.9f * u))
            drawRect(tt.s(0.4f), Offset(x, y1 - 1.3f * u), Size(w / 6f + 1f, 1.3f * u))
        }
        // Las runas: grupos de tres separados por dos puntos.
        val yc = (y0 + y1) / 2 + (y1 - y0) * 0.01f
        val n = 19
        for (j in 0 until n) {
            val x = w * (0.035f + 0.93f * j / (n - 1))
            val tt = tn(Offset(x, yc))
            if (j % 4 == 3) {
                listOf(-1f, 1f).forEach { s -> drawCircle(tt.s(0.55f), 1.2f * u, Offset(x, yc + s * (y1 - y0) * 0.14f)); drawCircle(tt.b(0.3f), 0.6f * u, Offset(x + 0.6f * u, yc + s * (y1 - y0) * 0.14f + 0.6f * u)) }
            } else runaTallada(j * 7 + 2, Offset(x, yc), (y1 - y0) * 0.42f, tt, 1.5f * u)
        }
    }

    // ---------------------------------------------------------------- en la pared

    /** El manojo de hierbas de montaña, boca abajo, secándose colgado de una clavija: los tallos, las hojitas y las flores secas. */
    private fun DrawScope.hierbas(p: Offset) {
        val u = density
        val w = anchoW
        val t = tn(p + Offset(0f, w * 0.1f))
        val atado = p + Offset(w * 0.002f, w * 0.04f)
        val forma = poligono(atado.x - w * 0.01f, atado.y, atado.x + w * 0.01f, atado.y, atado.x + w * 0.05f, atado.y + w * 0.15f, atado.x - w * 0.045f, atado.y + w * 0.16f)
        sombraEnPared(forma, atado, w * SUP, 0.14f)
        drawLine(t.c(CUERDA), p, atado, 1f * u)
        clavija(p, 2.2f * u, t)
        for (i in 0 until 9) {
            val a = -0.55f + 1.1f * i / 8f + 0.1f * (azar(5700, i) - 0.5f)
            val largo = w * (0.15f + 0.05f * azar(5701, i))
            val fin = atado + Offset(sin(a) * largo * 0.55f, cos(a) * largo)
            val c1 = atado + Offset(sin(a) * largo * 0.12f, largo * 0.4f)
            val c2 = fin + Offset(-sin(a) * largo * 0.1f, -largo * 0.25f)
            val verde = mezcla(HIERBA, if (i % 3 == 0) PINO_LUZ else MUSGO, 0.35f * azar(5702, i))
            drawPath(Path().apply { ir(atado); curva(c1, c2, fin) }, t.c(mezcla(verde, CUERO, 0.35f)), style = Stroke(0.8f * u))
            for (j in 1..4) {
                val f = j / 5f
                val q = punto(atado, c1, c2, fin, f)
                val lado = if ((i + j) % 2 == 0) 1f else -1f
                hojita(q + Offset(lado * w * 0.005f, 0f), w * 0.026f * (1.1f - f * 0.4f), lado * 38f - a * 57f, t.c(mezcla(verde, PINO_LUZ, 0.15f * (j % 2))), t)
            }
            if (i % 2 == 0) for (q in 0 until 3) drawCircle(t.c(if (i % 4 == 0) mezcla(LATON, PINO_LUZ, 0.3f) else mezcla(RUBI, PINO_LUZ, 0.45f)), w * 0.0045f, fin + Offset(w * 0.004f * (q - 1), w * 0.003f * (q % 2)))
        }
        // El atado de cordel.
        drawRect(t.c(CUERDA), Offset(atado.x - w * 0.009f, atado.y - w * 0.004f), Size(w * 0.018f, w * 0.011f))
        for (k in 0 until 3) drawLine(t.s(0.4f), Offset(atado.x - w * 0.009f, atado.y - w * 0.002f + k * w * 0.0035f), Offset(atado.x + w * 0.009f, atado.y - w * 0.001f + k * w * 0.0035f), 0.6f * u)
        drawRect(t.b(0.4f), Offset(atado.x - w * 0.009f, atado.y - w * 0.004f), Size(w * 0.018f, 0.8f * u))
    }

    /**
     * El mapa grande de los caminos, clavado en el friso y un poco torcido: la vitela de borde barbado con su marco a pluma, el
     * río, los dos cordales con el paso entre ellos, el bosque, el sendero punteado desde la posada hasta la marca, el puente,
     * la rosa de los vientos, los dobleces de llevarlo plegado, la esquina que se levanta, los clavos y el cuchillo.
     */
    private fun DrawScope.mapa(r: Rect) {
        val u = density
        val w = anchoW
        val o = esNoche
        val t = tn(Offset(r.left + r.width * 0.65f, r.center.y))
        val tinta = t.c(TINTA_MAPA)
        fun f(x: Float, y: Float) = Offset(r.left + r.width * x, r.top + r.height * y)
        rotate(-1.3f, r.center) {
            val hoja = contornoIrregular(r, 5800, 44, 0.012f)
            sombraEnPared(hoja, r.center, w * SUP, 0.24f)
            drawPath(hoja, t.c(VITELA))
            clipPath(hoja) {
                pinceladas(r, if (o) NOCHE else mezcla(VITELA, CUERO, 0.5f), 5801, 16, u, 0.1f)
                drawPath(hoja, t.c(mezcla(VITELA, CUERO, 0.4f)).copy(alpha = 0.55f), style = Stroke(w * 0.014f))
                val m = r.width * 0.045f
                drawRect(tinta.copy(alpha = 0.6f), Offset(r.left + m, r.top + m), Size(r.width - 2 * m, r.height - 2 * m), style = Stroke(0.9f * u))
                drawRect(tinta.copy(alpha = 0.35f), Offset(r.left + m * 1.5f, r.top + m * 1.5f), Size(r.width - 3 * m, r.height - 3 * m), style = Stroke(0.5f * u))
                // El río, de arriba a la derecha hasta abajo a la izquierda.
                val rio = Path().apply { ir(f(0.68f, 0.07f)); curva(f(0.58f, 0.3f), f(0.66f, 0.45f), f(0.44f, 0.6f)); curva(f(0.28f, 0.7f), f(0.4f, 0.86f), f(0.22f, 0.94f)) }
                drawPath(rio, t.c(AGUA_MAPA), style = Stroke(2.4f * u, cap = StrokeCap.Round))
                drawPath(rio, t.c(mezcla(AGUA_MAPA, VITELA, 0.55f)), style = Stroke(0.7f * u))
                // Los dos cordales: el de delante, más grande; entre los dos, el paso.
                for (i in 0 until 9) {
                    if (i == 4) continue
                    val x = 0.1f + 0.1f * i
                    val y = 0.47f - 0.18f * (i / 8f) + 0.05f * (azar(5802, i) - 0.5f)
                    monte(f(x, y), r.width * (0.045f + 0.025f * azar(5803, i)), tinta, t)
                }
                for (i in 0 until 7) {
                    if (i == 3) continue
                    monte(f(0.16f + 0.11f * i, 0.33f - 0.15f * (i / 6f)), r.width * (0.028f + 0.012f * azar(5804, i)), tinta.copy(alpha = 0.7f), t)
                }
                // La marca del paso: dos arcos encontrados.
                val pc = f(0.5f, 0.38f)
                drawArc(tinta, -60f, 120f, false, Offset(pc.x - r.width * 0.05f, pc.y - r.height * 0.05f), Size(r.width * 0.04f, r.height * 0.1f), style = Stroke(1f * u))
                drawArc(tinta, 120f, 120f, false, Offset(pc.x + r.width * 0.01f, pc.y - r.height * 0.05f), Size(r.width * 0.04f, r.height * 0.1f), style = Stroke(1f * u))
                // El bosque.
                for (i in 0 until 8) {
                    val q = f(0.64f + 0.055f * (i % 4) + 0.025f * (i / 4), 0.62f + 0.08f * (i / 4) + 0.03f * azar(5805, i))
                    val s = r.width * 0.016f
                    drawLine(tinta, q, q + Offset(0f, s * 1.2f), 0.7f * u)
                    drawCircle(t.c(mezcla(MUSGO, VITELA, 0.25f)), s, q)
                    drawCircle(tinta.copy(alpha = 0.7f), s, q, style = Stroke(0.6f * u))
                }
                // El sendero, de la posada al paso y a la marca.
                val senda = Path().apply { ir(f(0.18f, 0.78f)); curva(f(0.3f, 0.72f), f(0.42f, 0.56f), f(0.5f, 0.4f)); curva(f(0.58f, 0.27f), f(0.72f, 0.24f), f(0.85f, 0.16f)) }
                drawPath(senda, t.c(mezcla(RUBI, CUERO, 0.2f)), style = Stroke(1.4f * u, cap = StrokeCap.Round, pathEffect = puntos(2.2f * u, 2.8f * u)))
                // El puente donde el sendero cruza el río.
                val pt = f(0.425f, 0.585f)
                listOf(-1f, 1f).forEach { s -> drawLine(tinta, pt + Offset(-r.width * 0.025f, s * r.height * 0.018f), pt + Offset(r.width * 0.025f, s * r.height * 0.018f - r.height * 0.01f), 0.9f * u) }
                // La posada: una casita con su tejado.
                val po = f(0.15f, 0.8f)
                val s = r.width * 0.022f
                drawRect(t.c(mezcla(VITELA, CUERO, 0.3f)), Offset(po.x - s, po.y - s), Size(s * 2, s * 1.4f))
                drawRect(tinta, Offset(po.x - s, po.y - s), Size(s * 2, s * 1.4f), style = Stroke(0.7f * u))
                drawPath(poligono(po.x - s * 1.3f, po.y - s, po.x, po.y - s * 2f, po.x + s * 1.3f, po.y - s), tinta)
                // La marca de la meta.
                val me = f(0.87f, 0.14f)
                drawLine(t.c(RUBI), me - Offset(s * 0.7f, s * 0.7f), me + Offset(s * 0.7f, s * 0.7f), 1.3f * u)
                drawLine(t.c(RUBI), me + Offset(-s * 0.7f, s * 0.7f), me + Offset(s * 0.7f, -s * 0.7f), 1.3f * u)
                // La rosa de los vientos, abajo a la derecha.
                val ro = f(0.84f, 0.8f)
                val rr = r.width * 0.06f
                drawCircle(tinta.copy(alpha = 0.6f), rr * 0.62f, ro, style = Stroke(0.6f * u))
                drawPath(estrella(ro, rr, 4, 0.28f), t.c(mezcla(VITELA, CUERO, 0.3f)))
                drawPath(estrella(ro, rr, 4, 0.28f), tinta, style = Stroke(0.6f * u))
                drawPath(estrella(ro, rr * 0.55f, 4, 0.3f, -45f), tinta.copy(alpha = 0.7f))
                drawLine(t.c(RUBI), ro, ro - Offset(0f, rr), 1.1f * u)
                // Los dobleces de llevarlo plegado.
                listOf(0.5f).forEach { fx -> drawLine(t.s(0.2f), f(fx, 0f), f(fx, 1f), 1.2f * u); drawLine(t.b(0.3f), f(fx, 0f) + Offset(1.2f * u, 0f), f(fx, 1f) + Offset(1.2f * u, 0f), 0.8f * u) }
                drawLine(t.s(0.16f), f(0f, 0.5f), f(1f, 0.5f), 1.2f * u)
                drawLine(t.b(0.25f), f(0f, 0.5f) + Offset(0f, 1.2f * u), f(1f, 0.5f) + Offset(0f, 1.2f * u), 0.8f * u)
                // La luz: de día, más clara a la izquierda; de noche, perdida lejos de la linterna.
                if (o) drawRect(Brush.horizontalGradient(listOf(NOCHE.copy(alpha = 0.6f), Color.Transparent), r.left, r.right), r.topLeft, r.size)
                else drawRect(Brush.horizontalGradient(listOf(luzMueble(0.2f), Color.Transparent), r.left, r.center.x), r.topLeft, r.size)
            }
            // La esquina de abajo a la derecha, que se levanta: el revés y su sombra.
            val e = r.width * 0.07f
            val doblez = poligono(r.right - e, r.bottom, r.right, r.bottom - e, r.right - e * 0.15f, r.bottom - e * 0.2f)
            translate(1.5f * u, 2f * u) { drawPath(doblez, t.s(0.3f)) }
            drawPath(doblez, t.c(VITELA_OSC))
            // Los clavos.
            listOf(f(0.035f, 0.05f), f(0.04f, 0.95f), f(0.62f, 0.035f)).forEach { clavo(it, 1.7f * u, tn(it)) }
            // El cuchillo clavado en la esquina de arriba a la derecha.
            cuchillo(f(0.96f, 0.05f))
        }
    }

    /** Un monte del mapa: el contorno a pluma, el lado de la sombra rayado y una pizca de nieve en la punta. */
    private fun DrawScope.monte(p: Offset, s: Float, tinta: Color, t: Tono) {
        val u = density
        val cima = p + Offset(-s * 0.1f, -s * 1.15f)
        drawPath(poligono(p.x - s, p.y, cima.x, cima.y, p.x + s, p.y), t.c(mezcla(VITELA, CUERO, 0.12f)))
        drawPath(Path().apply { moveTo(p.x - s, p.y); lineTo(cima.x, cima.y); lineTo(p.x + s, p.y) }, tinta, style = Stroke(0.8f * u, cap = StrokeCap.Round))
        for (k in 1..3) {
            val f = k / 4f
            val a = cima + (Offset(p.x + s, p.y) - cima) * f
            drawLine(tinta.copy(alpha = 0.6f), a, Offset(a.x - s * 0.25f, p.y - s * 0.05f), 0.5f * u)
        }
        drawLine(tinta.copy(alpha = 0.5f), Offset(cima.x + s * 0.15f, cima.y + s * 0.2f), Offset(cima.x - s * 0.1f, cima.y + s * 0.35f), 0.5f * u)
    }

    /** El cuchillo clavado: la hoja hundida en la madera, la guarda de hierro, el mango con su tira de cuero y el pomo. */
    private fun DrawScope.cuchillo(p: Offset) {
        val u = density
        val w = anchoW
        val t = tn(p)
        rotate(-38f, p) {
            val largo = w * 0.06f
            val mango = Rect(p.x + w * 0.006f, p.y - w * 0.007f, p.x + w * 0.006f + largo, p.y + w * 0.007f)
            translate(w * 0.008f, w * 0.008f) { drawRoundRect(t.s(0.3f), mango.topLeft, mango.size, CornerRadius(w * 0.006f)) }
            // Un dedo de hoja que asoma.
            drawPath(poligono(p.x - w * 0.004f, p.y, p.x + w * 0.006f, p.y - w * 0.004f, p.x + w * 0.006f, p.y + w * 0.004f), t.c(mezcla(HIERRO, PIEDRA_FRIA, 0.5f)))
            drawRoundRect(t.c(mezcla(PINO_OSC, CUERO_OSC, 0.4f)), mango.topLeft, mango.size, CornerRadius(w * 0.006f))
            drawRect(t.b(0.4f), Offset(mango.left, mango.top + 1f * u), Size(mango.width, 1f * u))
            for (k in 0 until 4) drawLine(t.c(CUERO), Offset(mango.left + mango.width * (0.3f + 0.1f * k), mango.top), Offset(mango.left + mango.width * (0.36f + 0.1f * k), mango.bottom), 1.4f * u)
            drawRect(t.c(HIERRO), Offset(p.x + w * 0.004f, p.y - w * 0.012f), Size(w * 0.005f, w * 0.024f))
            drawCircle(t.c(HIERRO), w * 0.008f, Offset(mango.right, mango.center.y))
            destello(Offset(mango.right - w * 0.002f, mango.center.y - w * 0.003f), 0.8f * u, t)
        }
    }

    /**
     * El cuerno de llamada colgado de su correa de una clavija: el cuerno curvo, claro en la boca y oscuro en la punta, con sus
     * anillos de crecimiento, las abrazaderas de latón, la boquilla y la boca honda.
     */
    private fun DrawScope.cuerno(c: Offset) {
        val u = density
        val w = anchoW
        val p0 = Offset(w * 0.688f, w * 0.236f); val p1 = Offset(w * 0.7f, w * 0.3f)
        val p2 = Offset(w * 0.782f, w * 0.305f); val p3 = Offset(w * 0.806f, w * 0.218f)
        val r0 = w * 0.004f; val r1 = w * 0.021f
        val t = tn(punto(p0, p1, p2, p3, 0.6f))
        val forma = cuerpo(p0, p1, p2, p3, r0, r1)
        val a0 = punto(p0, p1, p2, p3, 0.12f); val a1 = punto(p0, p1, p2, p3, 0.9f)
        // La correa, de la clavija a las dos abrazaderas.
        val correa = Path().apply { ir(a0); lineTo(c.x, c.y + w * 0.004f); lineTo(a1.x, a1.y) }
        sombraEnPared(forma, punto(p0, p1, p2, p3, 0.5f), w * SUP, 0.22f)
        translate(w * 0.006f, w * 0.004f) { drawPath(correa, t.s(0.25f), style = Stroke(w * 0.007f)) }
        drawPath(correa, t.c(CUERO_OSC), style = Stroke(w * 0.007f))
        drawPath(correa, t.b(0.3f), style = Stroke(0.6f * u))
        clavija(c, 2.2f * u, t)
        // El cuerno.
        drawPath(forma, t.c(CUERNO))
        clipPath(forma) {
            drawCircle(t.c(mezcla(CUERNO, CUERO_OSC, 0.75f)), w * 0.022f, p0)
            drawCircle(t.c(mezcla(CUERNO, CUERO_OSC, 0.4f)), w * 0.02f, punto(p0, p1, p2, p3, 0.22f))
            drawPath(paralela(p0, p1, p2, p3, r0, r1, 0.65f), t.s(0.32f), style = Stroke(w * 0.012f))
            drawPath(paralela(p0, p1, p2, p3, r0, r1, -0.5f, 0.15f, 1f), if (luzDerecha(p3.x)) t.b(0.3f) else t.b(0.45f), style = Stroke(w * 0.004f))
            for (k in 0 until 6) {
                val s = 0.35f + 0.09f * k
                val q = punto(p0, p1, p2, p3, s); val n = normal(p0, p1, p2, p3, s); val rr = r0 + (r1 - r0) * s
                drawLine(t.s(0.25f), q - n * rr, q + n * rr, 0.6f * u)
            }
        }
        // Las abrazaderas y la boquilla.
        listOf(0.12f to 1.25f, 0.9f to 1.15f).forEach { (s, g) ->
            val q = punto(p0, p1, p2, p3, s); val n = normal(p0, p1, p2, p3, s); val rr = (r0 + (r1 - r0) * s) * g
            drawLine(t.c(LATON), q - n * rr, q + n * rr, w * 0.006f)
            destello(q - n * rr * 0.6f, 0.8f * u, t)
        }
        drawCircle(t.c(LATON), w * 0.005f, p0)
        // La boca.
        val nb = normal(p0, p1, p2, p3, 1f)
        rotate(Math.toDegrees(kotlin.math.atan2(nb.y.toDouble(), nb.x.toDouble())).toFloat(), p3) {
            drawOval(t.c(LATON), Offset(p3.x - r1 * 1.08f, p3.y - r1 * 0.38f), Size(r1 * 2.16f, r1 * 0.76f))
            drawOval(t.c(mezcla(CUERO_OSC, NOCHE, 0.4f)), Offset(p3.x - r1 * 0.85f, p3.y - r1 * 0.26f), Size(r1 * 1.7f, r1 * 0.52f))
        }
    }

    /**
     * La capa de viaje colgada de su clavija: la lana que cae en pliegues desde el cuello fruncido, la capucha doblada, la
     * orla de su borde y el broche con su gema; cortada por el borde derecho y metida detrás de la tapa.
     */
    private fun DrawScope.capa(c: Offset) {
        val u = density
        val w = anchoW
        val arriba = c + Offset(0f, w * 0.01f)
        val fondo = w * 0.6f
        val silueta = Path().apply {
            moveTo(arriba.x - w * 0.018f, arriba.y)
            cubicTo(arriba.x - w * 0.05f, arriba.y + w * 0.08f, w * 0.866f, w * 0.24f, w * 0.852f, w * 0.38f)
            cubicTo(w * 0.842f, w * 0.47f, w * 0.85f, w * 0.52f, w * 0.83f, fondo)
            lineTo(w * 1.02f, fondo); lineTo(w * 1.02f, arriba.y + w * 0.03f)
            cubicTo(w * 0.99f, arriba.y + w * 0.004f, arriba.x + w * 0.03f, arriba.y - w * 0.004f, arriba.x - w * 0.018f, arriba.y)
            close()
        }
        val t = tn(Offset(w * 0.88f, w * 0.38f))
        sombraEnPared(silueta, Offset(w * 0.9f, w * 0.3f), w * SUP, 0.26f)
        drawPath(silueta, t.c(LANA))
        clipPath(silueta) {
            // Los pliegues: bandas que bajan del cuello y se abren.
            // Los pliegues: hondos y blandos, que bajan del cuello fruncido y se abren ondeando, cada uno distinto.
            for (i in 0 until 5) {
                val x0 = arriba.x - w * 0.014f + w * 0.008f * i
                val x1 = w * (0.85f + 0.04f * i + 0.014f * azar(5900, i))
                val vaiven = w * (0.012f + 0.012f * azar(5901, i)) * (if (i % 2 == 0) 1f else -1f)
                val tt = tn(Offset((x0 + x1) / 2, w * 0.4f))
                val pl = Path().apply { moveTo(x0, arriba.y + w * 0.012f); cubicTo(x0 - w * 0.025f + vaiven, w * 0.24f, x1 - vaiven, w * 0.38f, x1, fondo) }
                drawPath(pl, tt.s(0.16f), style = Stroke(w * (0.02f + 0.01f * azar(5902, i)), cap = StrokeCap.Round))
                drawPath(pl, tt.s(0.24f), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
                translate(-w * 0.016f, 0f) { drawPath(pl, tt.b(if (i == 0) 0.3f else 0.16f), style = Stroke(w * 0.011f, cap = StrokeCap.Round)) }
            }
            // Los pliegues de tensión que salen de la clavija, en diagonal hacia la izquierda.
            for (i in 0 until 3) {
                val tt = tn(arriba + Offset(0f, w * 0.15f))
                val d = Path().apply { moveTo(arriba.x + w * 0.004f * i, arriba.y + w * 0.015f); cubicTo(arriba.x - w * 0.02f, arriba.y + w * (0.07f + 0.03f * i), arriba.x - w * (0.04f + 0.01f * i), arriba.y + w * (0.14f + 0.04f * i), arriba.x - w * (0.05f + 0.006f * i), arriba.y + w * (0.2f + 0.06f * i)) }
                drawPath(d, tt.s(0.2f), style = Stroke(w * 0.008f, cap = StrokeCap.Round))
                translate(-w * 0.008f, -w * 0.002f) { drawPath(d, tt.b(0.14f), style = Stroke(w * 0.006f, cap = StrokeCap.Round)) }
            }
            pinceladas(Rect(w * 0.82f, arriba.y, w, fondo), if (esNoche) NOCHE else mezcla(LANA, LUZ_MUEBLE, 0.3f), 5903, 14, u, 0.12f)
            if (!esNoche) drawRect(sombraMueble(0.14f), Offset(w * 0.95f, 0f), Size(w * 0.06f, fondo))
            // La capucha doblada que cuelga del cuello.
            val cap = Path().apply {
                moveTo(arriba.x - w * 0.03f, arriba.y + w * 0.02f)
                cubicTo(arriba.x - w * 0.07f, arriba.y + w * 0.08f, arriba.x - w * 0.03f, arriba.y + w * 0.14f, arriba.x + w * 0.0f, arriba.y + w * 0.13f)
                cubicTo(arriba.x + w * 0.03f, arriba.y + w * 0.12f, arriba.x + w * 0.04f, arriba.y + w * 0.06f, arriba.x + w * 0.02f, arriba.y + w * 0.02f)
                close()
            }
            val tc = tn(arriba + Offset(0f, w * 0.08f))
            translate(w * 0.006f, w * 0.008f) { drawPath(cap, tc.s(0.35f)) }
            drawPath(cap, tc.c(mezcla(LANA, LUZ_MUEBLE, 0.08f)))
            drawPath(Path().apply { moveTo(arriba.x - w * 0.04f, arriba.y + w * 0.06f); cubicTo(arriba.x - w * 0.03f, arriba.y + w * 0.1f, arriba.x, arriba.y + w * 0.11f, arriba.x + w * 0.025f, arriba.y + w * 0.07f) }, tc.s(0.35f), style = Stroke(w * 0.006f))
            drawPath(Path().apply { moveTo(arriba.x - w * 0.045f, arriba.y + w * 0.05f); cubicTo(arriba.x - w * 0.04f, arriba.y + w * 0.08f, arriba.x - w * 0.02f, arriba.y + w * 0.1f, arriba.x, arriba.y + w * 0.1f) }, tc.b(0.3f), style = Stroke(w * 0.004f))
        }
        // La orla del borde, de un ocre tejido.
        val orla = Path().apply {
            moveTo(arriba.x - w * 0.016f, arriba.y + w * 0.01f)
            cubicTo(arriba.x - w * 0.048f, arriba.y + w * 0.08f, w * 0.868f, w * 0.24f, w * 0.854f, w * 0.38f)
            cubicTo(w * 0.844f, w * 0.47f, w * 0.852f, w * 0.52f, w * 0.832f, fondo)
        }
        drawPath(orla, t.c(mezcla(LATON, CUERO, 0.35f)), style = Stroke(w * 0.006f))
        drawPath(orla, t.c(mezcla(RUBI, CUERO, 0.3f)), style = Stroke(w * 0.0018f, pathEffect = puntos(2f * u, 2f * u)))
        if (esNoche) drawPath(orla, LLAMA.copy(alpha = (0.7f * t.k).coerceAtMost(0.5f)), style = Stroke(1.3f * u))
        clavija(c, 2.2f * u, tn(c))
        // El broche: un aro de latón con su gema y el alfiler.
        val br = Offset(arriba.x - w * 0.022f, arriba.y + w * 0.055f)
        val tb = tn(br)
        drawCircle(tb.s(0.35f), w * 0.013f, br + Offset(w * 0.003f, w * 0.004f))
        drawCircle(tb.c(LATON), w * 0.012f, br, style = Stroke(w * 0.005f))
        drawLine(tb.c(mezcla(LATON, HIERRO, 0.3f)), br + Offset(-w * 0.017f, w * 0.008f), br + Offset(w * 0.017f, -w * 0.008f), 1.3f * u)
        gemaViva(br, w * 0.006f, tb)
    }

    // ---------------------------------------------------------------- el estante

    /** El estante de pino: su sombra en el muro y el suelo, las baldas, el travesaño de arriba, los montantes y el zócalo. */
    private fun DrawScope.estante(d: Disposicion, b: Rect) {
        val u = density
        val w = anchoW
        val hb = b.height
        val canto = w * CANTO
        val pisan = b.top + hb * PISAN
        val izq1 = w * lado; val der0 = w * (1f - lado)
        // Su sombra a la derecha y en el suelo.
        val t0 = tn(Offset(w, w * 0.8f))
        drawPath(poligono(w * DER1, w * SUP, w, w * SUP + w * 0.01f, w, b.top + hb * SUELO, w * DER1, b.top + hb * SUELO), t0.s(0.3f))
        val tp = tn(Offset(w * 0.5f, pisan))
        drawRect(tp.s(0.32f), Offset(w * IZQ0 + w * 0.015f, pisan - 1.5f * u), Size(w * (DER1 - IZQ0), hb * 0.07f))
        drawRect(tp.s(0.14f), Offset(w * IZQ0 + w * 0.03f, pisan + hb * 0.07f - 1.5f * u), Size(w * (DER1 - IZQ0), hb * 0.07f))
        // Las baldas entre fila y fila, y el travesaño bajo la tapa.
        d.filas.forEachIndexed { i, f -> tabla(Rect(izq1 - 2f * u, f.bottom, der0 + 2f * u, f.bottom + d.separacion), 5600 + i) }
        val trav = Rect(izq1 - 2f * u, canto, der0 + 2f * u, d.huecos.first().top)
        tabla(trav, 5590)
        // En el travesaño, una soga tallada de lado a lado.
        soga(Rect(trav.left, trav.top + trav.height * 0.32f, trav.right, trav.top + trav.height * 0.72f), false)
        // El zócalo, con su hilera de runas.
        val zoc = Rect(izq1 - 2f * u, b.top, der0 + 2f * u, b.top + hb * ZOC1)
        tabla(zoc, 5620)
        val tz = tn(zoc.center)
        drawRect(tz.b(0.35f), Offset(zoc.left, zoc.top + zoc.height * 0.08f), Size(zoc.width, 0.8f * u))
        drawRect(tz.s(0.3f), Offset(zoc.left, zoc.bottom - zoc.height * 0.12f), Size(zoc.width, 1f * u))
        for (j in 0 until 14) {
            val x = zoc.left + zoc.width * (0.05f + 0.9f * j / 13f)
            if (d.mas > 0 && x > w * (placa.left - 0.02f) && x < w * (placa.right + 0.02f)) continue
            val tt = tn(Offset(x, zoc.center.y))
            if (j % 4 == 3) drawCircle(tt.s(0.5f), 1.2f * u, Offset(x, zoc.center.y))
            else runaTallada(j * 5 + 3, Offset(x, zoc.center.y), zoc.height * 0.4f, tt, 1.3f * u)
        }
        // Los montantes, de la tapa al suelo.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val postes = mutableListOf(IZQ0 to lado - 0.002f, 1f - lado + 0.002f to DER1)
        for (k in 1 until cols) { val x0 = lado + anchoH * k + hueco * (k - 1); postes.add(x0 to x0 + hueco) }
        postes.forEach { (a, z) -> montante(Rect(w * a, canto - w * 0.004f, w * z, pisan), d, b) }
    }

    /** Una tabla de pino de frente: su tono por tramos (de noche, según la linterna), la veta, el canto de arriba y el de abajo. */
    private fun DrawScope.tabla(r: Rect, s: Int) {
        val u = density
        val tramos = 14
        for (i in 0 until tramos) {
            val x0 = r.left + r.width * i / tramos
            val tt = tn(Offset(x0 + r.width / (2 * tramos), r.center.y))
            drawRect(tt.c(mezcla(PINO, PINO_OSC, 0.12f + 0.1f * azar(s, i))), Offset(x0, r.top), Size(r.width / tramos + 1f, r.height))
            drawRect(tt.b(0.5f), Offset(x0, r.top), Size(r.width / tramos + 1f, 1.2f * u))
            drawRect(tt.s(0.4f), Offset(x0, r.bottom - 1.5f * u), Size(r.width / tramos + 1f, 1.5f * u))
        }
        vetas(r, (if (esNoche) NOCHE else PINO_OSC).copy(alpha = 0.38f), s, 0.9f, grosor = 0.6f, ondulacion = 0.6f)
        if (!esNoche) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.16f), Color.Transparent, sombraMueble(0.14f)), r.left, r.right), r.topLeft, r.size)
        // Un golpe y un corte de uso.
        val gx = r.left + r.width * (0.2f + 0.6f * azar(s, 20))
        val tg = tn(Offset(gx, r.center.y))
        drawLine(tg.s(0.4f), Offset(gx, r.top + r.height * 0.3f), Offset(gx + r.height * 0.6f, r.top + r.height * 0.45f), 0.8f * u)
        drawLine(tg.b(0.3f), Offset(gx, r.top + r.height * 0.3f + 0.8f * u), Offset(gx + r.height * 0.6f, r.top + r.height * 0.45f + 0.8f * u), 0.5f * u)
    }

    /** Una soga tallada en relieve dentro de [r]: los cabos torcidos, cada uno con su surco en sombra y su lomo con luz. */
    private fun DrawScope.soga(r: Rect, vertical: Boolean, saltar: List<ClosedFloatingPointRange<Float>> = emptyList()) {
        val u = density
        val w = anchoW
        val paso = w * 0.022f
        val largo = if (vertical) r.height else r.width
        var s = 0f
        while (s < largo) {
            val enEje = (if (vertical) r.top else r.left) + s
            if (saltar.none { enEje in it }) {
                val p = if (vertical) Offset(r.center.x, enEje) else Offset(enEje, r.center.y)
                val tt = tn(p)
                if (vertical) {
                    drawPath(poligono(r.left, enEje, r.right, enEje + paso * 0.45f, r.right, enEje + paso * 0.95f, r.left, enEje + paso * 0.5f), tt.c(mezcla(PINO, PINO_LUZ, 0.25f)))
                    drawLine(tt.s(0.5f), Offset(r.left, enEje + paso * 0.5f), Offset(r.right, enEje + paso * 0.95f), 1.1f * u)
                    drawLine(tt.b(0.4f), Offset(r.left, enEje + paso * 0.05f), Offset(r.right, enEje + paso * 0.5f), 0.8f * u)
                } else {
                    drawPath(poligono(enEje, r.bottom, enEje + paso * 0.45f, r.top, enEje + paso * 0.95f, r.top, enEje + paso * 0.5f, r.bottom), tt.c(mezcla(PINO, PINO_LUZ, 0.25f)))
                    drawLine(tt.s(0.5f), Offset(enEje + paso * 0.5f, r.bottom), Offset(enEje + paso * 0.95f, r.top), 1.1f * u)
                    drawLine(tt.b(0.4f), Offset(enEje + paso * 0.05f, r.bottom), Offset(enEje + paso * 0.5f, r.top), 0.8f * u)
                }
            }
            s += paso
        }
        // Los bordes de la soga, hundidos en la tabla.
        val t = tn(r.center)
        if (vertical) {
            drawRect(t.s(0.35f), Offset(r.left - 1f * u, r.top), Size(1f * u, r.height))
            drawRect(t.s(0.3f), Offset(r.right, r.top), Size(1.2f * u, r.height))
        } else {
            drawRect(t.s(0.35f), Offset(r.left, r.top - 1f * u), Size(r.width, 1f * u))
            drawRect(t.b(0.3f), Offset(r.left, r.bottom), Size(r.width, 1f * u))
        }
    }

    /**
     * Un montante: el poste cuadrado de pino con su cara de luz y su cara de sombra, la soga tallada que baja por él y se
     * anuda a media altura de cada fila, las clavijas de cada balda y la pata que pisa las losas, con barro.
     */
    private fun DrawScope.montante(r: Rect, d: Disposicion, b: Rect) {
        val u = density
        val w = anchoW
        val tramos = 14
        for (i in 0 until tramos) {
            val y0 = r.top + r.height * i / tramos
            val tt = tn(Offset(r.center.x, y0 + r.height / (2 * tramos)))
            drawRect(tt.c(mezcla(PINO, PINO_OSC, 0.18f)), Offset(r.left, y0), Size(r.width, r.height / tramos + 1f))
            val der = luzDerecha(r.center.x)
            drawRect(tt.b(if (der) 0.1f else 0.3f), Offset(r.left, y0), Size(r.width * 0.16f, r.height / tramos + 1f))
            drawRect(tt.s(if (der) 0.12f else 0.28f), Offset(r.right - r.width * 0.18f, y0), Size(r.width * 0.18f, r.height / tramos + 1f))
            if (der) drawRect(tt.b(0.35f), Offset(r.right - 1.2f * u, y0), Size(1.2f * u, r.height / tramos + 1f))
        }
        vetas(r, (if (esNoche) NOCHE else PINO_OSC).copy(alpha = 0.3f), (r.left / u).toInt(), 0.6f, vertical = true, grosor = 0.5f, ondulacion = 0.4f)
        // La soga tallada, que se interrumpe en los nudos y en las clavijas.
        val nudos = d.filas.map { it.center.y }
        val radioN = r.width * 0.42f
        val banda = Rect(r.center.x - r.width * 0.24f, r.top + w * 0.05f, r.center.x + r.width * 0.24f, b.top - w * 0.01f)
        soga(banda, true, nudos.map { (it - radioN)..(it + radioN) })
        // Los nudos: un bulto redondo con su luz, su sombra y el cruce de los cabos.
        nudos.forEach { y ->
            val c = Offset(r.center.x, y)
            val tt = tn(c)
            drawCircle(tt.s(0.35f), radioN, c + Offset(radioN * 0.18f, radioN * 0.22f))
            drawCircle(tt.c(mezcla(PINO, PINO_LUZ, 0.2f)), radioN, c)
            drawArc(tt.s(0.45f), 200f, 160f, false, Offset(c.x - radioN * 0.75f, c.y - radioN * 0.9f), Size(radioN * 1.5f, radioN * 1.2f), style = Stroke(1.1f * u))
            drawArc(tt.s(0.45f), 20f, 160f, false, Offset(c.x - radioN * 0.75f, c.y - radioN * 0.3f), Size(radioN * 1.5f, radioN * 1.2f), style = Stroke(1.1f * u))
            drawCircle(tt.b(0.4f), radioN * 0.3f, c - Offset(radioN * 0.4f, radioN * 0.45f))
            drawArc(tt.s(0.35f), -10f, 120f, false, Offset(c.x - radioN, c.y - radioN), Size(radioN * 2, radioN * 2), style = Stroke(1.2f * u))
        }
        // Las clavijas de cada balda, del travesaño y del zócalo.
        val ys = d.filas.map { it.bottom + d.separacion / 2 } + listOf(w * CANTO + (d.huecos.first().top - w * CANTO) / 2, b.top + b.height * ZOC1 / 2)
        ys.forEach { y ->
            val tt = tn(Offset(r.center.x, y))
            listOf(-0.22f, 0.22f).forEach { f ->
                val p = Offset(r.center.x + r.width * f, y + (if (f < 0) -1f else 1f) * r.width * 0.12f)
                drawCircle(tt.s(0.4f), r.width * 0.13f, p + Offset(0.8f * u, 1f * u))
                drawCircle(tt.c(PINO_OSC), r.width * 0.12f, p)
                drawCircle(tt.c(mezcla(PINO, PINO_LUZ, 0.4f)), r.width * 0.08f, p - Offset(0.4f * u, 0.4f * u))
            }
        }
        // La pata: el chaflán de abajo y el barro.
        val tp = tn(Offset(r.center.x, r.bottom))
        val pie = b.top + b.height * ZOC1
        drawRect(tp.s(0.25f), Offset(r.left, pie), Size(r.width, 2f * u))
        drawRect(tp.c(mezcla(PINO_OSC, BARRO, 0.5f)), Offset(r.left, r.bottom - (r.bottom - pie) * 0.35f), Size(r.width, (r.bottom - pie) * 0.35f))
        drawRect(tp.s(0.3f), Offset(r.left, r.bottom - 1.5f * u), Size(r.width, 1.5f * u))
    }

    /** La tapa: la tabla gruesa vista un poco desde arriba, con su veta, cortes de cuchillo, una gota de cera y su canto. */
    private fun DrawScope.tapa(w: Float) {
        val u = density
        val o = esNoche
        val xL = w * 0.004f; val xR = w * 0.996f
        val sup = w * SUP; val frente = w * FRENTE; val canto = w * CANTO
        val tabla = Rect(xL, sup, xR, frente)
        for (k in 0 until 20) {
            val x0 = xL + (xR - xL) * k / 20f
            val tk = tn(Offset(x0 + (xR - xL) / 40f, sup))
            drawRect(tk.c(mezcla(PINO_LUZ, PINO, 0.35f)), Offset(x0, sup), Size((xR - xL) / 20f + 1f, frente - sup))
        }
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.3f), Color.Transparent, sombraMueble(0.12f)), xL, xR), tabla.topLeft, tabla.size)
        vetas(tabla, (if (o) NOCHE else PINO_OSC).copy(alpha = 0.32f), 5640, 0.8f, grosor = 0.5f, ondulacion = 0.4f)
        val t = tn(Offset(w * 0.5f, sup))
        drawRect(t.s(0.38f), tabla.topLeft, Size(tabla.width, 1.5f * u))
        // Cortes de cuchillo, un cerco y la cera que goteó junto a la linterna.
        for (k in 0 until 6) {
            val x = w * (0.08f + 0.84f * azar(5641, k))
            val tk = tn(Offset(x, sup))
            val y = sup + (frente - sup) * (0.3f + 0.5f * azar(5642, k))
            drawLine(tk.s(0.35f), Offset(x, y), Offset(x + w * 0.03f, y + (frente - sup) * 0.12f), 0.7f * u)
            drawLine(tk.b(0.3f), Offset(x, y + 0.7f * u), Offset(x + w * 0.03f, y + (frente - sup) * 0.12f + 0.7f * u), 0.5f * u)
        }
        drawOval(tn(Offset(w * 0.4f, sup)).s(0.18f), Offset(w * 0.37f, sup + (frente - sup) * 0.3f), Size(w * 0.05f, (frente - sup) * 0.5f), style = Stroke(0.9f * u))
        val tc = tn(Offset(w * 0.72f, frente))
        drawOval(tc.c(CERA), Offset(w * 0.72f, sup + (frente - sup) * 0.55f), Size(w * 0.012f, (frente - sup) * 0.25f))
        // El canto de frente, con su clavija en cada montante.
        for (k in 0 until 20) {
            val x0 = xL + (xR - xL) * k / 20f
            val tk = tn(Offset(x0 + (xR - xL) / 40f, frente))
            drawRect(tk.c(mezcla(PINO, PINO_OSC, 0.15f)), Offset(x0, frente), Size((xR - xL) / 20f + 1f, canto - frente))
            drawRect(tk.b(0.7f), Offset(x0, frente), Size((xR - xL) / 20f + 1f, 1.3f * u))
            drawRect(tk.s(0.35f), Offset(x0, canto - 1.6f * u), Size((xR - xL) / 20f + 1f, 1.6f * u))
        }
        vetas(Rect(xL, frente, xR, canto), (if (o) NOCHE else PINO_OSC).copy(alpha = 0.3f), 5643, 0.7f, grosor = 0.5f, ondulacion = 0.3f)
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val xs = listOf((IZQ0 + lado) / 2, (1f - lado + DER1) / 2) + (1 until cols).map { lado + anchoH * it + hueco * (it - 0.5f) }
        xs.forEach { fx -> val p = Offset(w * fx, (frente + canto) / 2); val tk = tn(p); drawCircle(tk.s(0.4f), 2.2f * u, p + Offset(0.8f * u, 0.8f * u)); drawCircle(tk.c(PINO_OSC), 2f * u, p); drawCircle(tk.c(PINO_LUZ), 1.2f * u, p - Offset(0.4f * u, 0.4f * u)) }
        // La sombra del canto en el travesaño.
        drawRect(t.s(0.32f), Offset(w * IZQ0, canto), Size(w * (DER1 - IZQ0), 2.4f * u))
        drawRect(t.s(0.14f), Offset(w * IZQ0, canto + 2.4f * u), Size(w * (DER1 - IZQ0), 3f * u))
    }

    // ---------------------------------------------------------------- lo de encima

    /**
     * El bastón de guía, apoyado en diagonal contra el muro y el mapa: la contera de hierro, la madera pulida por la mano,
     * las muescas de cuenta (de cinco en cinco, con su raya cruzada), el pomo tallado y las dos cintas atadas que cuelgan.
     */
    private fun DrawScope.baston(pie: Offset, punta: Offset) {
        val u = density
        val w = anchoW
        val dv = punta - pie
        val len = dv.getDistance()
        val dir = dv / len
        val nrm = Offset(-dir.y, dir.x)
        fun en(s: Float) = pie + dv * s
        val r0 = w * 0.0085f; val r1 = w * 0.0095f
        val forma = cuerpo(pie, en(0.33f), en(0.66f), punta, r0, r1, 6)
        sombraEnPared(forma, en(0.6f), w * SUP, 0.26f)
        contacto(pie + Offset(w * 0.008f, 0f), w * 0.035f, 3f * u, 0.45f)
        val der = luzDerecha(pie.x)
        val ladoLuz = if (der) nrm else nrm * -1f
        val ladoSombra = ladoLuz * -1f
        for (i in 0 until 5) {
            val a = i / 5f; val z = (i + 1) / 5f
            val tt = tn(en((a + z) / 2))
            val ra = r0 + (r1 - r0) * a; val rz = r0 + (r1 - r0) * z
            drawPath(cuerpo(en(a), en(a + (z - a) / 3), en(a + 2 * (z - a) / 3), en((z + 0.004f).coerceAtMost(1f)), ra, rz, 2), tt.c(mezcla(PINO, CUERO, 0.45f)))
            drawLine(tt.b(0.45f), en(a) + ladoLuz * (ra * 0.5f), en(z) + ladoLuz * (rz * 0.5f), ra * 0.4f)
            drawLine(tt.s(0.4f), en(a) + ladoSombra * (ra * 0.6f), en(z) + ladoSombra * (rz * 0.6f), ra * 0.55f)
        }
        // La contera de hierro.
        val tc = tn(pie)
        drawPath(cuerpo(pie, en(0.01f), en(0.03f), en(0.045f), r0 * 1.05f, r0 * 1.1f, 2), tc.c(HIERRO))
        drawLine(tc.b(0.4f), en(0.0f) + ladoLuz * (r0 * 0.5f), en(0.045f) + ladoLuz * (r0 * 0.5f), 0.8f * u)
        // Las muescas de cuenta.
        for (j in 0 until 14) {
            val s = 0.36f + 0.026f * j + (j / 5) * 0.012f
            val c = en(s)
            val tt = tn(c)
            val rr = r0 + (r1 - r0) * s
            if (j % 5 == 4) {
                drawLine(tt.s(0.6f), c - nrm * rr - dir * (rr * 1.6f), c + nrm * rr + dir * (rr * 3.4f), 0.9f * u)
            } else {
                drawLine(tt.s(0.6f), c - nrm * rr, c + nrm * (rr * 0.85f), 1f * u)
                drawLine(tt.b(0.35f), c - nrm * rr - dir * (0.9f * u), c + nrm * (rr * 0.85f) - dir * (0.9f * u), 0.5f * u)
            }
        }
        // El pomo tallado: un bulto con dos anillos.
        val tpm = tn(punta)
        drawCircle(tpm.s(0.3f), r1 * 1.5f, punta + Offset(w * 0.004f, w * 0.004f))
        drawOval(tpm.c(mezcla(PINO, CUERO, 0.55f)), Offset(punta.x - r1 * 1.45f, punta.y - r1 * 1.7f), Size(r1 * 2.9f, r1 * 3.2f))
        drawCircle(tpm.b(0.45f), r1 * 0.5f, punta - Offset(r1 * 0.6f, r1 * 0.8f))
        listOf(0.955f, 0.935f).forEach { s -> val c = en(s); drawLine(tpm.s(0.55f), c - nrm * (r1 * 1.1f), c + nrm * (r1 * 1.1f), 1f * u) }
        // Las cintas: dos vueltas atadas y sus cabos sueltos que cuelgan.
        val ta = en(0.8f)
        val tt = tn(ta)
        listOf(RUBI to 0.8f, MUSGO to 0.825f).forEach { (col, s) ->
            val c = en(s); val rr = r0 + (r1 - r0) * s
            drawLine(tt.c(col), c - nrm * (rr * 1.25f), c + nrm * (rr * 1.25f), w * 0.0075f)
            drawLine(tt.b(0.35f), c - nrm * (rr * 1.25f) - dir * (w * 0.002f), c + nrm * (rr * 1.25f) - dir * (w * 0.002f), 0.7f * u)
        }
        listOf(
            listOf(Offset(-0.004f, 0.0f), Offset(-0.016f, 0.03f), Offset(0.012f, 0.055f), Offset(-0.006f, 0.1f)) to RUBI,
            listOf(Offset(0.002f, 0.0f), Offset(0.016f, 0.025f), Offset(-0.004f, 0.05f), Offset(0.01f, 0.078f)) to MUSGO,
        ).forEach { (ps, col) ->
            val q = ps.map { ta + Offset(w * it.x, w * it.y) }
            val cinta = cuerpo(q[0], q[1], q[2], q[3], w * 0.0042f, w * 0.0032f, 12)
            translate(w * 0.008f, w * 0.006f) { drawPath(cinta, tt.s(0.22f)) }
            drawPath(cinta, tt.c(col))
            drawPath(paralela(q[0], q[1], q[2], q[3], w * 0.0042f, w * 0.0032f, 0.45f), tt.s(0.35f), style = Stroke(w * 0.0028f))
            drawPath(paralela(q[0], q[1], q[2], q[3], w * 0.0042f, w * 0.0032f, -0.55f, 0f, 0.6f), tt.b(0.35f), style = Stroke(w * 0.0014f))
        }
    }

    /** El rollo de cuerda de escalar, tumbado en la tapa en vueltas que no casan, con su cabo colgando y el garfio encima. */
    private fun DrawScope.cuerda(c: Offset) {
        val u = density
        val w = anchoW
        val t = tn(c)
        contacto(c + Offset(w * 0.004f, w * 0.002f), w * 0.17f, w * 0.016f, 0.42f)
        // El cabo que cuelga por el canto.
        val cabo = Path().apply { moveTo(c.x + w * 0.045f, c.y + w * 0.004f); cubicTo(c.x + w * 0.07f, c.y + w * 0.02f, c.x + w * 0.058f, w * CANTO, c.x + w * 0.072f, w * CANTO + w * 0.03f) }
        translate(w * 0.004f, w * 0.004f) { drawPath(cabo, t.s(0.3f), style = Stroke(w * 0.0075f)) }
        drawPath(cabo, t.c(CUERDA), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
        drawPath(cabo, t.s(0.4f), style = Stroke(w * 0.0022f, pathEffect = puntos(1.4f * u, 1.8f * u)))
        val fin = Offset(c.x + w * 0.072f, w * CANTO + w * 0.03f)
        for (k in -1..1) drawLine(t.c(CUERDA), fin, fin + Offset(k * w * 0.004f, w * 0.008f), 0.7f * u)
        // Las vueltas.
        for (i in 0 until 5) {
            val rx = w * (0.068f - 0.006f * i + 0.005f * azar(5910, i))
            val ry = w * 0.017f
            val cy = c.y - w * 0.008f - w * 0.0068f * i
            val cx = c.x + w * 0.005f * (azar(5911, i) - 0.5f)
            val rr = Rect(cx - rx, cy - ry, cx + rx, cy + ry)
            drawOval(t.s(0.35f), rr.topLeft + Offset(1f * u, 1.6f * u), rr.size, style = Stroke(w * 0.0085f))
            drawOval(t.c(mezcla(CUERDA, PINO_OSC, 0.1f * (i % 2))), rr.topLeft, rr.size, style = Stroke(w * 0.0078f))
            drawOval(t.s(0.38f), rr.topLeft, rr.size, style = Stroke(w * 0.0024f, pathEffect = puntos(1.5f * u, 2f * u)))
            drawArc(t.b(0.45f), 190f, 160f, false, rr.topLeft - Offset(0f, w * 0.0018f), rr.size, style = Stroke(w * 0.0022f))
        }
        // El garfio de hierro, tumbado sobre las vueltas: el vástago, la argolla y las tres uñas.
        val a = Offset(c.x - w * 0.04f, c.y - w * 0.042f)
        val z = Offset(c.x + w * 0.035f, c.y - w * 0.05f)
        val tg = tn(a)
        translate(w * 0.004f, w * 0.005f) { drawLine(tg.s(0.35f), a, z, w * 0.006f) }
        drawLine(tg.c(HIERRO), a, z, w * 0.0055f, StrokeCap.Round)
        drawLine(tg.b(0.4f), a - Offset(0f, w * 0.0015f), z - Offset(0f, w * 0.0015f), 0.7f * u)
        drawCircle(tg.c(HIERRO), w * 0.009f, z + Offset(w * 0.008f, 0f), style = Stroke(w * 0.0035f))
        listOf(-1f, 1f).forEach { s ->
            val una = Path().apply { moveTo(a.x, a.y); cubicTo(a.x - w * 0.016f, a.y + s * w * 0.004f, a.x - w * 0.02f, a.y + s * w * 0.018f, a.x - w * 0.008f, a.y + s * w * 0.022f) }
            drawPath(una, tg.c(HIERRO), style = Stroke(w * 0.005f, cap = StrokeCap.Round))
            drawPath(una, tg.b(0.3f), style = Stroke(0.6f * u))
        }
        drawLine(tg.c(HIERRO), a, a + Offset(-w * 0.018f, w * 0.004f), w * 0.005f, StrokeCap.Round)
        destello(z + Offset(w * 0.004f, -w * 0.007f), 0.9f * u, tg)
    }

    /** La cantimplora de cuero apoyada en el muro: el cuerpo redondo, la costura del canto, el cuello con su tapón y la correa. */
    private fun DrawScope.cantimplora(pie: Offset) {
        val u = density
        val w = anchoW
        val r = w * 0.04f
        val c = Offset(pie.x, pie.y - r)
        val t = tn(c)
        val der = luzDerecha(c.x)
        // La correa, tirada en la tapa.
        val correa = Path().apply { moveTo(c.x - w * 0.012f, c.y - r * 0.9f); cubicTo(c.x - w * 0.07f, c.y - r * 0.4f, c.x - w * 0.07f, pie.y + w * 0.01f, c.x - w * 0.02f, pie.y + w * 0.014f); cubicTo(c.x + w * 0.03f, pie.y + w * 0.02f, c.x + w * 0.06f, pie.y + w * 0.01f, c.x + w * 0.012f, c.y - r * 0.9f) }
        drawPath(correa, t.s(0.3f), style = Stroke(w * 0.007f))
        drawPath(correa, t.c(CUERO_OSC), style = Stroke(w * 0.0055f))
        val cuerpoP = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
        sombraEnPared(cuerpoP, c, pie.y, 0.24f)
        contacto(Offset(c.x + w * 0.006f, pie.y), r * 2.1f, 3f * u, 0.4f)
        // El cuello y el tapón.
        drawRect(t.c(CUERO_OSC), Offset(c.x - w * 0.008f, c.y - r - w * 0.012f), Size(w * 0.016f, w * 0.016f))
        drawRoundRect(t.c(PINO_LUZ), Offset(c.x - w * 0.006f, c.y - r - w * 0.022f), Size(w * 0.012f, w * 0.012f), CornerRadius(w * 0.003f))
        drawRect(t.s(0.3f), Offset(c.x + w * 0.002f, c.y - r - w * 0.022f), Size(w * 0.004f, w * 0.012f))
        drawPath(cuerpoP, t.c(CUERO))
        clipPath(cuerpoP) {
            drawCircle(t.b(if (der) 0.12f else 0.22f), r, c + (if (der) Offset(r * 0.3f, -r * 0.3f) else Offset(-r * 0.3f, -r * 0.3f)))
            drawCircle(t.s(0.3f), r, c + (if (der) Offset(-r * 0.45f, r * 0.45f) else Offset(r * 0.45f, r * 0.45f)))
            pinceladas(Rect(c.x - r, c.y - r, c.x + r, c.y + r), if (esNoche) NOCHE else CUERO_OSC, 5920, 6, u, 0.15f)
        }
        drawCircle(t.c(mezcla(CUERO, VITELA, 0.4f)), r * 0.86f, c, style = Stroke(0.9f * u, pathEffect = puntos(1.6f * u, 1.6f * u)))
        runa(4, c + Offset(0f, r * 0.05f), r * 0.6f, t.c(CUERO_OSC), 1.4f * u)
        if (esNoche && t.k > 0.1f) drawArc(LLAMA.copy(alpha = (0.7f * t.k).coerceAtMost(0.6f)), -70f, 110f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(1.4f * u))
    }

    /** El cuenco de madera con las piedras de afilar: dos de pie dentro y una tumbada al lado. */
    private fun DrawScope.cuenco(c: Offset) {
        val u = density
        val w = anchoW
        val t = tn(c)
        val bw = w * 0.068f; val bh = w * 0.04f
        contacto(Offset(c.x + w * 0.006f, c.y), bw * 2.3f, 3.5f * u, 0.42f)
        // Las piedras de dentro.
        listOf(Triple(-0.016f, -14f, 0.04f), Triple(0.016f, 10f, 0.048f)).forEachIndexed { i, (dx, g, al) ->
            val p = Offset(c.x + w * dx, c.y - bh * 0.7f)
            rotate(g, p) {
                val r = Rect(p.x - w * 0.009f, p.y - w * al, p.x + w * 0.009f, p.y)
                drawRoundRect(t.c(if (i == 0) PIEDRA_FRIA else mezcla(PIEDRA_FRIA, MORTERO, 0.5f)), r.topLeft, r.size, CornerRadius(w * 0.006f))
                drawRect(t.b(0.35f), Offset(r.left + 1f * u, r.top + 2f * u), Size(1.2f * u, r.height - 3f * u))
                drawRect(t.s(0.3f), Offset(r.right - 2f * u, r.top + 1f * u), Size(2f * u, r.height - 1f * u))
            }
        }
        // El cuenco.
        val boca = Rect(c.x - bw, c.y - bh - w * 0.006f, c.x + bw, c.y - bh + w * 0.006f)
        val forma = Path().apply { moveTo(boca.left, boca.center.y); cubicTo(boca.left, c.y - bh * 0.1f, c.x - bw * 0.5f, c.y, c.x, c.y); cubicTo(c.x + bw * 0.5f, c.y, boca.right, c.y - bh * 0.1f, boca.right, boca.center.y); close() }
        drawPath(forma, t.c(mezcla(PINO, CUERO, 0.4f)))
        clipPath(forma) {
            drawRect(t.b(0.25f), Offset(c.x - bw, boca.top), Size(bw * 0.6f, bh * 1.5f))
            drawRect(t.s(0.3f), Offset(c.x + bw * 0.35f, boca.top), Size(bw * 0.7f, bh * 1.5f))
            for (k in 0 until 3) drawArc(t.s(0.25f), 20f, 140f, false, Offset(c.x - bw * (0.9f - 0.15f * k), boca.top - bh * (0.7f - 0.2f * k)), Size(bw * (1.8f - 0.3f * k), bh * 1.6f), style = Stroke(0.6f * u))
        }
        drawArc(t.b(0.5f), 180f, 180f, false, boca.topLeft, boca.size, style = Stroke(1.1f * u))
        // La piedra tumbada al lado, a la derecha.
        val p = Offset(c.x + bw * 1.6f, c.y - w * 0.004f)
        contacto(p + Offset(w * 0.004f, w * 0.003f), w * 0.04f, 2.5f * u, 0.4f)
        drawRoundRect(t.c(mezcla(PIEDRA_CALIDA, MORTERO, 0.3f)), Offset(p.x - w * 0.018f, p.y - w * 0.008f), Size(w * 0.036f, w * 0.01f), CornerRadius(w * 0.005f))
        drawRect(t.b(0.35f), Offset(p.x - w * 0.015f, p.y - w * 0.007f), Size(w * 0.03f, 1f * u))
    }

    /**
     * La linterna de cuerno, de pie en la tapa: la base y el remate de hierro con su argolla, los montantes, las hojas de cuerno
     * translúcido y la vela de dentro. De día está apagada; de noche, su llama es la única luz del rincón.
     */
    private fun DrawScope.linterna(pie: Offset) {
        val u = density
        val w = anchoW
        val o = esNoche
        val t = tn(pie - Offset(0f, w * 0.06f))
        val bw = w * 0.074f
        val base = Rect(pie.x - bw * 0.62f, pie.y - w * 0.014f, pie.x + bw * 0.62f, pie.y)
        val caja = Rect(pie.x - bw / 2, base.top - w * 0.11f, pie.x + bw / 2, base.top)
        val cima = caja.top - w * 0.04f
        val techo = poligono(caja.left - w * 0.008f, caja.top, pie.x - w * 0.006f, cima, pie.x + w * 0.006f, cima, caja.right + w * 0.008f, caja.top)
        val todo = Path().apply { addRect(base); addRect(caja); addPath(techo) }
        if (!o) sombraEnPared(todo, Offset(pie.x, pie.y - w * 0.06f), pie.y, 0.22f)
        contacto(Offset(pie.x + w * 0.006f, pie.y), bw * 1.6f, 3.5f * u, 0.45f)
        // Por dentro: el fondo, la vela y (de noche) la llama.
        val vela = Rect(pie.x - w * 0.01f, caja.bottom - w * 0.056f, pie.x + w * 0.01f, caja.bottom)
        if (o) {
            drawRect(mezcla(LLAMA, Color(0xFFFFF0CF), 0.25f), caja.topLeft, caja.size)
            drawRect(mezcla(CERA, LLAMA, 0.3f), vela.topLeft, vela.size)
            val lc = Offset(pie.x, vela.top - w * 0.011f)
            drawOval(Color(0xFFFFE09A), Offset(lc.x - w * 0.006f, lc.y - w * 0.012f), Size(w * 0.012f, w * 0.022f))
            drawOval(Color(0xFFFFF7E2), Offset(lc.x - w * 0.003f, lc.y - w * 0.006f), Size(w * 0.006f, w * 0.012f))
        } else {
            drawRect(mezcla(CUERNO, CUERO_OSC, 0.4f), caja.topLeft, caja.size)
            drawRect(CERA, vela.topLeft, vela.size)
            drawRect(sombraMueble(0.25f), Offset(vela.right - w * 0.005f, vela.top), Size(w * 0.005f, vela.height))
            drawLine(TINTA, Offset(pie.x, vela.top), Offset(pie.x + w * 0.001f, vela.top - w * 0.006f), 1f * u)
        }
        // Las hojas de cuerno, con sus vetas, y los montantes de hierro.
        val col = if (o) mezcla(LLAMA, CUERNO, 0.35f).copy(alpha = 0.5f) else mezcla(CUERNO, PINO_LUZ, 0.2f).copy(alpha = 0.42f)
        drawRect(col, caja.topLeft, caja.size)
        for (k in 0 until 4) drawLine((if (o) Color(0xFFFFF0CF) else luzMueble(1f)).copy(alpha = 0.2f), Offset(caja.left + caja.width * (0.12f + 0.24f * k), caja.top + 2f * u), Offset(caja.left + caja.width * (0.16f + 0.24f * k), caja.bottom - 2f * u), 0.7f * u)
        if (!o) { drawRect(luzMueble(0.2f), caja.topLeft, Size(caja.width * 0.3f, caja.height)); drawRect(sombraMueble(0.22f), Offset(caja.right - caja.width * 0.3f, caja.top), Size(caja.width * 0.3f, caja.height)) }
        if (o) drawOval(Color(0xFFFFF4D8).copy(alpha = 0.6f), Offset(pie.x - w * 0.004f, vela.top - w * 0.02f), Size(w * 0.008f, w * 0.014f))
        val hierro = t.c(HIERRO)
        listOf(caja.left, caja.right).forEach { x -> drawRect(hierro, Offset(x - 1.2f * u, caja.top), Size(2.4f * u, caja.height)) }
        drawRect(hierro, Offset(caja.left, caja.center.y + caja.height * 0.12f), Size(caja.width, 1.6f * u))
        drawRect(hierro, Offset(caja.left - 1.5f * u, caja.top), Size(caja.width + 3f * u, 2.2f * u))
        drawRect(hierro, Offset(caja.left - 1.5f * u, caja.bottom - 2.2f * u), Size(caja.width + 3f * u, 2.2f * u))
        listOf(caja.left, caja.right).forEach { x -> drawCircle(t.c(LATON), 0.9f * u, Offset(x, caja.top + 1.1f * u)) }
        // El remate, la base y la argolla.
        drawPath(techo, hierro)
        clipPath(techo) {
            drawRect(t.b(0.25f), Offset(caja.left - w * 0.01f, cima), Size(bw * 0.45f, caja.top - cima))
            drawRect(t.s(0.3f), Offset(pie.x + bw * 0.15f, cima), Size(bw * 0.5f, caja.top - cima))
            for (k in 0 until 3) drawCircle(if (o) LLAMA.copy(alpha = 0.8f) else sombraMueble(0.5f), 0.9f * u, Offset(pie.x - w * 0.012f + w * 0.012f * k, caja.top - w * 0.01f))
        }
        drawRect(hierro, base.topLeft, base.size)
        drawRect(t.b(0.35f), base.topLeft, Size(base.width, 1f * u))
        drawCircle(hierro, w * 0.011f, Offset(pie.x, cima - w * 0.009f), style = Stroke(w * 0.003f))
        destello(Offset(pie.x - w * 0.008f, cima - w * 0.016f), 0.8f * u, if (o) Tono(true, 0.6f) else t)
    }

    // ---------------------------------------------------------------- al pie

    /** Las botas de camino, embarradas: una de pie con la punta a la izquierda y la otra caída a su lado. */
    private fun DrawScope.botas(suelo: Float) {
        val w = anchoW
        contacto(Offset(w * 0.12f, suelo), w * 0.15f, w * 0.012f, 0.42f)
        contacto(Offset(w * 0.27f, suelo), w * 0.17f, w * 0.012f, 0.4f)
        bota(Offset(w * 0.27f, suelo - w * 0.012f), 82f, 1)
        bota(Offset(w * 0.13f, suelo), 0f, 0)
    }

    /** Una bota de cuero alta: la caña con su vuelta, el empeine, la suela, las arrugas del tobillo y el barro de abajo. */
    private fun DrawScope.bota(p: Offset, giro: Float, s: Int) {
        val u = density
        val w = anchoW
        val x = p.x; val y = p.y
        val t = tn(p)
        fun q(a: Float, b: Float) = Offset(x + w * a, y + w * b)
        val forma = Path().apply {
            ir(q(-0.028f, -0.135f)); lineTo(x + w * 0.026f, y - w * 0.135f)
            curva(q(0.03f, -0.09f), q(0.032f, -0.04f), q(0.034f, -0.006f)); lineTo(x + w * 0.034f, y)
            lineTo(x - w * 0.07f, y)
            curva(q(-0.088f, 0f), q(-0.086f, -0.03f), q(-0.06f, -0.036f))
            curva(q(-0.04f, -0.04f), q(-0.03f, -0.05f), q(-0.028f, -0.07f))
            close()
        }
        rotate(giro, p) {
            drawPath(forma, t.c(mezcla(CUERO_OSC, CUERO, 0.3f * s)))
            clipPath(forma) {
                drawRect(t.b(0.2f), q(-0.03f, -0.14f), Size(w * 0.016f, w * 0.14f))
                drawRect(t.s(0.3f), q(0.018f, -0.14f), Size(w * 0.02f, w * 0.14f))
                // La vuelta de la caña.
                drawRect(t.c(mezcla(CUERO, VITELA, 0.15f)), q(-0.03f, -0.137f), Size(w * 0.066f, w * 0.022f))
                drawRect(t.s(0.35f), q(-0.03f, -0.116f), Size(w * 0.066f, 1.2f * u))
                // Las arrugas del tobillo.
                for (k in 0 until 3) drawArc(t.s(0.4f), 200f, 110f, false, q(-0.032f, -0.07f + 0.012f * k), Size(w * 0.05f, w * 0.02f), style = Stroke(0.8f * u))
                // El barro: una franja de borde irregular y salpicaduras.
                val barro = Path().apply {
                    moveTo(x - w * 0.1f, y + w * 0.01f); lineTo(x - w * 0.1f, y - w * 0.03f)
                    var xx = x - w * 0.1f
                    var k = 0
                    while (xx < x + w * 0.05f) { xx += w * 0.012f; lineTo(xx, y - w * (0.028f + 0.022f * azar(5930 + s, k))); k++ }
                    lineTo(x + w * 0.05f, y + w * 0.01f); close()
                }
                drawPath(barro, t.c(BARRO))
                for (k in 0 until 5) drawCircle(t.c(BARRO), w * (0.003f + 0.003f * azar(5931 + s, k)), q(-0.06f + 0.022f * k, -0.06f - 0.02f * azar(5932 + s, k)))
                drawRect(t.c(mezcla(BARRO, NOCHE, 0.4f)), q(-0.1f, -0.006f), Size(w * 0.15f, w * 0.008f))
            }
            drawLine(t.b(0.3f), q(-0.027f, -0.135f), q(0.025f, -0.135f), 1f * u)
            // La suela gruesa y el tacón.
            drawRect(t.c(mezcla(CUERO_OSC, NOCHE, 0.45f)), q(-0.075f, -0.008f), Size(w * 0.11f, w * 0.009f))
            drawRect(t.c(mezcla(CUERO_OSC, NOCHE, 0.45f)), q(0.008f, -0.016f), Size(w * 0.027f, w * 0.017f))
        }
    }

    /**
     * La mochila de marco: dos varas de madera con sus travesaños, el saco de lona con su solapa de cuero y sus correas, la
     * manta enrollada encima y la correa del hombro que cuelga; apoyada en el zócalo y cortada por el borde derecho.
     */
    private fun DrawScope.mochila(suelo: Float, b: Rect) {
        val u = density
        val w = anchoW
        val t = tn(Offset(w * 0.9f, suelo - w * 0.1f))
        val arribaV = b.top - w * 0.03f
        contacto(Offset(w * 0.93f, suelo), w * 0.2f, w * 0.014f, 0.42f)
        // Las varas del marco.
        listOf(w * 0.835f to w * 0.85f, w * 0.99f to w * 1.005f).forEach { (x0, x1) ->
            drawLine(t.s(0.35f), Offset(x0 + w * 0.008f, suelo), Offset(x1 + w * 0.012f, arribaV), w * 0.011f)
            drawLine(t.c(mezcla(PINO, PINO_OSC, 0.3f)), Offset(x0, suelo), Offset(x1, arribaV), w * 0.011f, StrokeCap.Round)
            drawLine(t.b(0.4f), Offset(x0 - w * 0.003f, suelo), Offset(x1 - w * 0.003f, arribaV), 1f * u)
        }
        listOf(0.25f, 0.92f).forEach { f ->
            val y = suelo + (arribaV - suelo) * f
            drawLine(t.c(mezcla(PINO, PINO_OSC, 0.4f)), Offset(w * 0.84f, y), Offset(w * 1.01f, y), w * 0.008f)
            drawLine(t.b(0.3f), Offset(w * 0.84f, y - w * 0.003f), Offset(w * 1.01f, y - w * 0.003f), 0.8f * u)
        }
        // El saco.
        val saco = Rect(w * 0.85f, suelo - w * 0.165f, w * 1.04f, suelo - w * 0.022f)
        val sacoP = Path().apply { addRoundRect(RoundRect(saco, CornerRadius(w * 0.03f))) }
        translate(w * 0.008f, w * 0.006f) { drawPath(sacoP, t.s(0.3f)) }
        val lona = mezcla(PIEDRA_CALIDA, CUERO, 0.35f)
        drawPath(sacoP, t.c(lona))
        clipPath(sacoP) {
            drawRect(t.b(0.2f), saco.topLeft, Size(saco.width * 0.18f, saco.height))
            drawRect(t.s(0.28f), Offset(saco.left, saco.bottom - saco.height * 0.25f), Size(saco.width, saco.height * 0.25f))
            pinceladas(saco, if (esNoche) NOCHE else mezcla(lona, LUZ_MUEBLE, 0.3f), 5940, 8, u, 0.14f)
            for (k in 0 until 3) drawLine(t.s(0.3f), Offset(saco.left + saco.width * (0.2f + 0.25f * k), saco.top + saco.height * 0.45f), Offset(saco.left + saco.width * (0.24f + 0.25f * k), saco.bottom), 0.8f * u)
            // La solapa de cuero.
            val sol = Path().apply { moveTo(saco.left, saco.top); lineTo(saco.right, saco.top); lineTo(saco.right, saco.top + saco.height * 0.42f); cubicTo(saco.left + saco.width * 0.7f, saco.top + saco.height * 0.5f, saco.left + saco.width * 0.3f, saco.top + saco.height * 0.5f, saco.left, saco.top + saco.height * 0.4f); close() }
            translate(0f, 2f * u) { drawPath(sol, t.s(0.3f)) }
            drawPath(sol, t.c(CUERO))
            drawPath(sol, t.c(mezcla(CUERO, VITELA, 0.4f)), style = Stroke(0.7f * u, pathEffect = puntos(1.6f * u, 1.6f * u)))
            drawRect(t.b(0.3f), saco.topLeft, Size(saco.width, 1f * u))
        }
        // Una correa con su hebilla.
        val cx = saco.left + saco.width * 0.32f
        drawRect(t.c(CUERO_OSC), Offset(cx, saco.top + saco.height * 0.3f), Size(w * 0.012f, saco.height * 0.62f))
        drawRect(t.c(LATON), Offset(cx - w * 0.002f, saco.top + saco.height * 0.5f), Size(w * 0.016f, w * 0.012f), style = Stroke(1.2f * u))
        destello(Offset(cx, saco.top + saco.height * 0.5f), 0.8f * u, t)
        // La manta enrollada encima, con su lista.
        val manta = Rect(w * 0.842f, saco.top - w * 0.042f, w * 1.04f, saco.top + w * 0.002f)
        translate(w * 0.006f, w * 0.006f) { drawRoundRect(t.s(0.3f), manta.topLeft, manta.size, CornerRadius(manta.height / 2)) }
        drawRoundRect(t.c(LANA), manta.topLeft, manta.size, CornerRadius(manta.height / 2))
        clipRect(manta.left, manta.top, manta.right, manta.bottom) {
            tumbado(manta, t, 1.2f)
            drawRect(t.c(mezcla(RUBI, LANA, 0.25f)), Offset(manta.left + manta.width * 0.6f, manta.top), Size(manta.width * 0.05f, manta.height))
        }
        drawOval(t.c(mezcla(LANA, LUZ_MUEBLE, 0.12f)), Offset(manta.left, manta.top), Size(manta.height * 0.6f, manta.height))
        drawOval(t.s(0.35f), Offset(manta.left + manta.height * 0.12f, manta.top + manta.height * 0.22f), Size(manta.height * 0.36f, manta.height * 0.56f), style = Stroke(0.8f * u))
        listOf(0.3f, 0.82f).forEach { f -> drawRect(t.c(CUERO_OSC), Offset(manta.left + manta.width * f, manta.top - 1f * u), Size(w * 0.008f, manta.height + 2f * u)) }
        // La correa del hombro que cuelga.
        val hombro = Path().apply { moveTo(w * 0.86f, saco.top + w * 0.01f); cubicTo(w * 0.81f, saco.top + w * 0.05f, w * 0.82f, saco.bottom, w * 0.86f, saco.bottom - w * 0.01f) }
        drawPath(hombro, t.s(0.3f), style = Stroke(w * 0.01f))
        drawPath(hombro, t.c(CUERO_OSC), style = Stroke(w * 0.008f))
        drawPath(hombro, t.b(0.25f), style = Stroke(0.6f * u))
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
        // Dónde está este hueco en el mueble (en fracciones del ancho), para saber cuánto le llega de la linterna.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val k = if (!oscuro) 0f else if (dentro) luz(centro) else 0.5f
        val dia = if (oscuro) 0f else 0.07f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.025f else 0f)
        val t = Tono(oscuro, k, dia)
        val der = oscuro && (!dentro || centro.x < LUZ.x)
        clipRect(0f, 0f, w, h) { fondoHueco(w, h, t, c) }
        if (c.vacio) { vacio(w, h, c, t); return }
        val m = mezclar(c.semilla)
        translate(0f, -SALIDA * h * ab) {
            when {
                c.sinTema -> cesto(w, h, c, t, der)
                else -> when (Math.floorMod(m ushr 11, 3)) {
                    0 -> arcon(w, h, c, m, t, der)
                    1 -> portamapas(w, h, c, m, t, der)
                    else -> zurron(w, h, c, m, t, der)
                }
            }
        }
    }

    /**
     * El fondo de un hueco: el mismo muro de sillares, en la sombra del estante (el de las filas bajas, con musgo en las
     * juntas), la sombra de la balda de arriba en escalones y la del montante de la izquierda, y la balda vista desde arriba.
     */
    private fun DrawScope.fondoHueco(w: Float, h: Float, t: Tono, c: EstadoCajon) {
        val u = density
        drawRect(t.c(MORTERO), Offset.Zero, Size(w, h))
        val musgo = c.filas >= 2 && c.fila >= c.filas - 2
        val base = mezcla(PIEDRA_FRIA, MORTERO, 0.3f)
        var y = -h * 0.2f * azar(c.fila * 7 + c.col, 3)
        var fila = 0
        while (y < h) {
            val alto = h * (0.3f + 0.08f * azar(6000 + c.fila, fila))
            var x = -w * 0.25f * azar(6001 + c.col, fila + c.fila)
            var kk = 0
            while (x < w) {
                val largo = w * (0.28f + 0.2f * azar(6002 + fila, kk + c.col * 5 + c.fila))
                val r = Rect(x + 1.3f * u, y + 1.3f * u, x + largo - 1.3f * u, y + alto - 1.3f * u)
                drawRoundRect(t.c(mezcla(base, PIEDRA_CALIDA, 0.45f * azar(6003 + fila, kk + c.fila))), r.topLeft, r.size, CornerRadius(2.5f * u))
                drawRect(t.b(0.18f), Offset(r.left + 2f * u, r.top), Size(r.width - 4f * u, 1.2f * u))
                drawRect(t.s(0.28f), Offset(r.left + 2f * u, r.bottom - 1.5f * u), Size(r.width - 4f * u, 1.5f * u))
                if (musgo && azar(6004 + fila, kk + c.col) > 0.3f) {
                    drawOval(t.c(MUSGO), Offset(r.left + r.width * (0.1f + 0.4f * azar(6005, kk)), r.bottom - 1.5f * u), Size(r.width * 0.4f, 4f * u))
                    drawOval(t.c(mezcla(MUSGO, PINO_LUZ, 0.3f)), Offset(r.left + r.width * (0.15f + 0.4f * azar(6005, kk)), r.bottom - 1.2f * u), Size(r.width * 0.15f, 1.5f * u))
                }
                x += largo; kk++
            }
            y += alto; fila++
        }
        // La sombra del estante: el fondo más hondo, la balda de arriba en escalones y el montante de la izquierda.
        drawRect(t.s(0.16f), Offset.Zero, Size(w, h))
        listOf(0.34f to 0.1f, 0.2f to 0.12f, 0.08f to 0.24f).forEach { (a, al) -> drawRect(t.s(al), Offset.Zero, Size(w, h * a)) }
        drawRect(t.s(0.2f), Offset.Zero, Size(w * 0.045f, h))
        drawRect(t.s(0.1f), Offset(w * 0.045f, 0f), Size(w * 0.05f, h))
        if (!t.o) drawRect(luzMueble(0.08f), Offset(w * 0.1f, h * 0.34f), Size(w * 0.4f, h * 0.56f))
        // La balda.
        val piso = h * PISO
        drawRect(t.c(mezcla(PINO, PINO_LUZ, 0.3f)), Offset(0f, piso), Size(w, h - piso))
        drawRect(t.s(0.35f), Offset(0f, piso), Size(w, 1.4f * u))
        drawLine(t.s(0.15f), Offset(w * 0.1f, piso + (h - piso) * 0.55f), Offset(w * 0.7f, piso + (h - piso) * 0.6f), 0.6f * u)
    }

    /** La placa del tema: latón remachado o una tablilla de pino. De noche, más hundida cuanto más lejos de la linterna. */
    private fun DrawScope.placaTema(w: Float, h: Float, t: Tono, madera: Boolean) {
        val u = density
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        val base = if (madera) TABLILLA else LATON_PLACA
        val lejos = if (madera) TABLILLA_LEJOS else LATON_LEJOS
        drawRect(t.s(0.32f), Offset(r.left + 1.6f * u, r.top + 2f * u), r.size)
        val fondo = if (t.o) mezcla(lejos, mezcla(base, LLAMA, 0.12f), (1.3f * t.k.pow(0.6f)).coerceAtMost(1f)) else t.c(base)
        drawRect(fondo, r.topLeft, r.size)
        if (madera) {
            vetas(r, (if (t.o) NOCHE else PINO).copy(alpha = 0.16f), 6100, 0.5f, grosor = 0.5f, ondulacion = 0.5f)
            drawRect(t.b(0.45f), r.topLeft, Size(r.width, 1f * u))
            drawRect(t.s(0.3f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
            drawRect(t.s(0.18f), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
            listOf(r.left + 3f * u, r.right - 3f * u).forEach { x -> clavo(Offset(x, r.top + 3f * u), 1f * u, t) }
        } else {
            drawRect(TINTA.copy(alpha = 0.3f), Offset(r.left + 2.2f * u, r.top + 2.2f * u), Size(r.width - 4.4f * u, r.height - 4.4f * u), style = Stroke(0.7f * u))
            drawRect(t.b(0.55f), r.topLeft, Size(r.width, 1f * u))
            drawRect(t.s(0.3f), Offset(r.left, r.bottom - 1.3f * u), Size(r.width, 1.3f * u))
            listOf(r.topLeft, Offset(r.right, r.top), Offset(r.left, r.bottom), r.bottomRight).forEach { p ->
                val q = Offset(p.x + (if (p.x < r.center.x) 1.6f else -1.6f) * u, p.y + (if (p.y < r.center.y) 1.6f else -1.6f) * u)
                drawCircle(t.c(mezcla(LATON, HIERRO, 0.5f)), 1f * u, q)
            }
        }
    }

    /** Los mapas y las páginas que asoman por arriba de [x0]..[x1], desde [arriba]: más y más altos cuanto más guarda. */
    private fun DrawScope.paginas(x0: Float, x1: Float, arriba: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val n = c.n.coerceIn(0, 10)
        val cuantos = (1 + n / 2).coerceIn(1, 5)
        val sube = h * (0.05f + 0.016f * n)
        for (i in 0 until cuantos) {
            val f = (i + 0.5f) / cuantos
            val rollo = Math.floorMod(c.semilla + i, 3) == 1
            val pw = (x1 - x0) * (if (rollo) 0.1f else 0.24f + 0.12f * azar(c.semilla, i + 20))
            val x = x0 + (x1 - x0 - pw) * (f * 0.9f + 0.05f * azar(c.semilla, i + 30))
            val y = arriba - sube * (0.45f + 0.55f * azar(c.semilla, i))
            val g = (azar(c.semilla, i + 40) - 0.5f) * (if (rollo) 18f else 6f)
            rotate(g, Offset(x + pw / 2, arriba)) {
                if (rollo) {
                    drawRect(t.s(0.2f), Offset(x + 1.5f * u, y + 1.5f * u), Size(pw, arriba - y))
                    drawRect(t.c(VITELA_OSC), Offset(x, y), Size(pw, arriba - y + 2f * u))
                    drawRect(t.b(0.3f), Offset(x, y), Size(pw * 0.35f, arriba - y))
                    drawRect(t.s(0.22f), Offset(x + pw * 0.68f, y), Size(pw * 0.32f, arriba - y))
                    drawOval(t.c(VITELA), Offset(x, y - pw * 0.25f), Size(pw, pw * 0.5f))
                    drawOval(t.c(VITELA_OSC), Offset(x + pw * 0.25f, y - pw * 0.12f), Size(pw * 0.5f, pw * 0.25f), style = Stroke(0.6f * u))
                    drawRect(t.c(RUBI), Offset(x, y + (arriba - y) * 0.35f), Size(pw, 1.8f * u))
                } else {
                    val hoja = Path().apply { moveTo(x, arriba + 2f * u); lineTo(x, y + 1f * u); lineTo(x + pw * 0.3f, y); lineTo(x + pw * 0.55f, y + 1.2f * u); lineTo(x + pw * 0.8f, y - 0.5f * u); lineTo(x + pw, y + 1f * u); lineTo(x + pw, arriba + 2f * u); close() }
                    translate(1.5f * u, 1.5f * u) { drawPath(hoja, t.s(0.2f)) }
                    drawPath(hoja, t.c(if (i % 2 == 0) VITELA else mezcla(VITELA, PINO_LUZ, 0.35f)))
                    drawRect(t.s(0.12f), Offset(x + pw * 0.82f, y), Size(pw * 0.18f, arriba - y))
                    drawRect(t.b(0.4f), Offset(x, y), Size(pw, 0.8f * u))
                    if (i % 2 == 0) for (q in 0 until 3) drawLine(t.c(TINTA_MAPA).copy(alpha = 0.45f), Offset(x + pw * 0.15f, y + (q + 1) * 3.2f * u), Offset(x + pw * (0.75f - 0.15f * (q % 2)), y + (q + 1) * 3.2f * u), 0.6f * u)
                    else drawLine(t.c(RUBI).copy(alpha = 0.6f), Offset(x + pw * 0.15f, y + 4f * u), Offset(x + pw * 0.8f, y + (arriba - y) * 0.8f), 0.8f * u, pathEffect = puntos(1.5f * u, 1.5f * u))
                }
            }
        }
    }

    /**
     * El arcón de la compañía: tablas con su veta (de pino, de castaño o pintadas de verde gastado, según la semilla), los
     * flejes y las esquinas de hierro con sus clavos, la tapa abovedada que se levanta con lo que guarda (y deja ver los mapas
     * y las páginas; con muchas, alguna cuelga por el canto), la cerradura con su gema y la placa de latón.
     */
    private fun DrawScope.arcon(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val madera = when (Math.floorMod(m ushr 3, 3)) { 0 -> mezcla(PINO, PINO_OSC, 0.3f); 1 -> mezcla(CUERO_OSC, PINO_OSC, 0.35f); else -> mezcla(MUSGO, PINO_OSC, 0.4f) }
        val n = c.n.coerceIn(0, 10)
        val cuerpoR = Rect(w * 0.07f, h * 0.45f, w * 0.93f, piso)
        val sube = h * 0.013f * n.coerceAtMost(7)
        val ty = cuerpoR.top - sube
        val alto = h * 0.19f
        // Su sombra en el muro y en la balda.
        drawRoundRect(t.s(0.28f), Offset(cuerpoR.left + w * 0.035f, ty - alto * 0.7f + h * 0.03f), Size(cuerpoR.width, piso - ty + alto * 0.7f - h * 0.03f), CornerRadius(w * 0.1f))
        contacto(Offset(cuerpoR.center.x + w * 0.04f, piso), cuerpoR.width * 1.06f, h * 0.05f, 0.42f)
        // La boca entre la tapa y el arcón, y lo que asoma.
        if (sube > 0.5f * u) {
            drawRect(t.s(0.8f), Offset(cuerpoR.left + 2f * u, ty), Size(cuerpoR.width - 4f * u, sube + 1f * u))
            clipRect(0f, ty - alto, w, cuerpoR.top + 1f * u) { paginas(cuerpoR.left + w * 0.08f, cuerpoR.right - w * 0.08f, cuerpoR.top + 2f * u, h, c, t) }
        }
        // El cuerpo de tablas.
        drawRect(t.c(madera), cuerpoR.topLeft, cuerpoR.size)
        vetas(cuerpoR, (if (t.o) NOCHE else PINO_OSC).copy(alpha = 0.4f), c.semilla, 0.9f, grosor = 0.6f, ondulacion = 0.5f)
        for (j in 1 until 3) {
            val y = cuerpoR.top + cuerpoR.height * j / 3f
            drawRect(t.s(0.4f), Offset(cuerpoR.left, y - 0.6f * u), Size(cuerpoR.width, 1.2f * u))
            drawRect(t.b(0.2f), Offset(cuerpoR.left, y + 0.6f * u), Size(cuerpoR.width, 0.7f * u))
        }
        if (der) { drawRect(t.b(0.16f), Offset(cuerpoR.right - cuerpoR.width * 0.18f, cuerpoR.top), Size(cuerpoR.width * 0.18f, cuerpoR.height)); drawRect(t.s(0.2f), cuerpoR.topLeft, Size(cuerpoR.width * 0.12f, cuerpoR.height)) }
        else { drawRect(t.b(0.16f), cuerpoR.topLeft, Size(cuerpoR.width * 0.2f, cuerpoR.height)); drawRect(t.s(0.22f), Offset(cuerpoR.right - cuerpoR.width * 0.12f, cuerpoR.top), Size(cuerpoR.width * 0.12f, cuerpoR.height)) }
        // Las páginas que cuelgan por el canto, si guarda muchas.
        if (n >= 4) for (i in 0 until (n - 2) / 3) {
            val x = cuerpoR.left + cuerpoR.width * (0.62f - 0.4f * i + 0.05f * azar(c.semilla, 90 + i))
            val hoja = Path().apply { moveTo(x, cuerpoR.top - 1f * u); lineTo(x + w * 0.13f, cuerpoR.top - 1f * u); lineTo(x + w * 0.135f, cuerpoR.top + h * 0.07f); cubicTo(x + w * 0.1f, cuerpoR.top + h * 0.1f, x + w * 0.04f, cuerpoR.top + h * 0.06f, x + w * 0.004f, cuerpoR.top + h * 0.085f); close() }
            translate(1.5f * u, 2f * u) { drawPath(hoja, t.s(0.25f)) }
            drawPath(hoja, t.c(if (i % 2 == 0) VITELA else mezcla(VITELA, PINO_LUZ, 0.35f)))
            drawLine(t.c(TINTA_MAPA).copy(alpha = 0.4f), Offset(x + w * 0.02f, cuerpoR.top + h * 0.03f), Offset(x + w * 0.1f, cuerpoR.top + h * 0.035f), 0.6f * u)
        }
        // Los flejes de hierro, el del pie y las esquinas.
        val hierro = t.c(HIERRO)
        listOf(0.2f, 0.8f).forEach { fx ->
            val x = w * fx
            drawRect(hierro, Offset(x - w * 0.025f, cuerpoR.top), Size(w * 0.05f, cuerpoR.height))
            drawRect(t.b(0.3f), Offset(x - w * 0.025f, cuerpoR.top), Size(1f * u, cuerpoR.height))
            var yy = cuerpoR.top + h * 0.04f
            while (yy < cuerpoR.bottom - 2f * u) { drawCircle(t.c(mezcla(HIERRO, PIEDRA_FRIA, 0.5f)), 1.1f * u, Offset(x, yy)); yy += h * 0.075f }
        }
        drawRect(hierro, Offset(cuerpoR.left, cuerpoR.bottom - h * 0.04f), Size(cuerpoR.width, h * 0.04f))
        drawRect(t.b(0.3f), Offset(cuerpoR.left, cuerpoR.bottom - h * 0.04f), Size(cuerpoR.width, 0.8f * u))
        filo(cuerpoR, t, der)
        // La tapa abovedada, con sus tablas curvas y los flejes que la cruzan.
        val tapa = Path().apply { moveTo(cuerpoR.left - 2f * u, ty); cubicTo(cuerpoR.left - 2f * u, ty - alto * 1.3f, cuerpoR.right + 2f * u, ty - alto * 1.3f, cuerpoR.right + 2f * u, ty); close() }
        translate(0f, 2f * u) { drawPath(tapa, t.s(0.35f)) }
        drawPath(tapa, t.c(mezcla(madera, PINO_LUZ, 0.12f)))
        clipPath(tapa) {
            for (j in 1..2) drawPath(Path().apply { moveTo(cuerpoR.left, ty - alto * 0.32f * j + alto * 0.1f); cubicTo(cuerpoR.left + w * 0.1f, ty - alto * (0.32f * j + 0.3f), cuerpoR.right - w * 0.1f, ty - alto * (0.32f * j + 0.3f), cuerpoR.right, ty - alto * 0.32f * j + alto * 0.1f) }, t.s(0.35f), style = Stroke(1f * u))
            drawOval(t.b(if (der) 0.1f else 0.25f), Offset(cuerpoR.left + w * 0.04f, ty - alto * 0.98f), Size(cuerpoR.width * 0.5f, alto * 0.35f))
            drawRect(t.s(0.25f), Offset(cuerpoR.left, ty - alto * 0.3f), Size(cuerpoR.width, alto * 0.3f))
            listOf(0.2f, 0.8f).forEach { fx -> drawRect(hierro, Offset(w * fx - w * 0.025f, ty - alto), Size(w * 0.05f, alto)) }
            if (t.o) drawRect(LLAMA.copy(alpha = (0.4f * t.k).coerceAtMost(0.35f)), Offset(cuerpoR.left, ty - alto), Size(cuerpoR.width, 2.5f * u))
        }
        drawRect(hierro, Offset(cuerpoR.left - 2f * u, ty - h * 0.03f), Size(cuerpoR.width + 4f * u, h * 0.03f))
        drawRect(t.b(0.35f), Offset(cuerpoR.left - 2f * u, ty - h * 0.03f), Size(cuerpoR.width + 4f * u, 0.8f * u))
        // La cerradura: la aldaba que baja de la tapa y la gema.
        val cx = w * 0.5f
        val cy = cuerpoR.top + h * 0.045f
        val ald = poligono(cx - w * 0.06f, ty - h * 0.025f, cx + w * 0.06f, ty - h * 0.025f, cx + w * 0.05f, cy + h * 0.035f, cx, cy + h * 0.06f, cx - w * 0.05f, cy + h * 0.035f)
        translate(1.2f * u, 1.6f * u) { drawPath(ald, t.s(0.4f)) }
        drawPath(ald, hierro)
        drawPath(ald, t.b(0.25f), style = Stroke(0.7f * u))
        gemaViva(Offset(cx, cy), h * 0.03f, t)
        placaTema(w, h, t, madera = false)
    }

    /**
     * El tubo portamapas de cuero (natural, rojizo o verdoso, según la semilla), tumbado en dos horquillas: la costura, las
     * abrazaderas, la runa marcada a fuego, la tapa a la derecha atada con un tiento, la boca abierta a la izquierda por la que
     * salen los mapas enrollados (más y más largos cuanto más guarda), la correa que cuelga y la tablilla con el tema.
     */
    private fun DrawScope.portamapas(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val cuero = when (Math.floorMod(m ushr 3, 3)) { 0 -> CUERO; 1 -> mezcla(CUERO, RUBI, 0.35f); else -> mezcla(CUERO_OSC, MUSGO, 0.4f) }
        val n = c.n.coerceIn(0, 10)
        val tubo = Rect(w * 0.18f, h * 0.2f, w * 0.85f, h * 0.41f)
        val horquillas = listOf(w * 0.3f, w * 0.72f)
        // Los pies de las horquillas, detrás.
        horquillas.forEach { x ->
            contacto(Offset(x + w * 0.01f, piso), w * 0.1f, h * 0.035f, 0.4f)
            drawLine(t.s(0.3f), Offset(x + w * 0.012f, piso), Offset(x + w * 0.012f, tubo.bottom), w * 0.032f)
            drawLine(t.c(mezcla(PINO, PINO_OSC, 0.3f)), Offset(x, piso), Offset(x, tubo.center.y), w * 0.03f)
            drawLine(t.b(0.3f), Offset(x - w * 0.01f, piso), Offset(x - w * 0.01f, tubo.center.y), 1f * u)
            drawLine(t.s(0.3f), Offset(x + w * 0.011f, piso), Offset(x + w * 0.011f, tubo.center.y), 1.2f * u)
        }
        // La sombra del tubo en el muro.
        drawRoundRect(t.s(0.26f), Offset(tubo.left + w * 0.03f, tubo.top + h * 0.05f), tubo.size, CornerRadius(tubo.height / 2))
        // La correa, que cuelga por delante entre las dos abrazaderas.
        val correa = Path().apply { moveTo(w * 0.26f, tubo.bottom - h * 0.02f); cubicTo(w * 0.36f, h * 0.55f, w * 0.62f, h * 0.55f, w * 0.76f, tubo.bottom - h * 0.02f) }
        // El tubo.
        drawRect(t.c(cuero), tubo.topLeft, tubo.size)
        clipRect(tubo.left, tubo.top, tubo.right, tubo.bottom) {
            tumbado(tubo, t, 1.3f)
            pinceladas(tubo, if (t.o) NOCHE else CUERO_OSC, c.semilla, 6, u, 0.14f)
            drawLine(t.c(mezcla(cuero, VITELA, 0.45f)), Offset(tubo.left, tubo.top + tubo.height * 0.22f), Offset(tubo.right, tubo.top + tubo.height * 0.22f), 0.8f * u, pathEffect = puntos(1.8f * u, 1.6f * u))
        }
        runa(Math.floorMod(m ushr 5, 7), tubo.center + Offset(-w * 0.04f, 0f), tubo.height * 0.55f, t.c(mezcla(CUERO_OSC, NOCHE, 0.3f)).copy(alpha = 0.85f), 1.5f * u)
        // Las abrazaderas con su anilla de latón.
        listOf(w * 0.26f, w * 0.76f).forEach { x ->
            val r = Rect(x - w * 0.022f, tubo.top - h * 0.012f, x + w * 0.022f, tubo.bottom + h * 0.012f)
            drawRect(t.c(mezcla(cuero, CUERO_OSC, 0.6f)), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) { tumbado(r, t, 1.2f) }
            drawCircle(t.c(LATON), w * 0.012f, Offset(x, r.bottom + w * 0.004f), style = Stroke(1.3f * u))
        }
        drawPath(correa, t.s(0.3f), style = Stroke(w * 0.03f))
        drawPath(correa, t.c(mezcla(cuero, CUERO_OSC, 0.5f)), style = Stroke(w * 0.025f))
        drawPath(correa, t.b(0.3f), style = Stroke(0.7f * u))
        // La tapa de la derecha, con su canto de latón y el tiento que la ata.
        val tapa = Rect(tubo.right - w * 0.05f, tubo.top - h * 0.018f, tubo.right + w * 0.03f, tubo.bottom + h * 0.018f)
        drawRect(t.s(0.3f), Offset(tapa.left - 1.5f * u, tapa.top), Size(1.5f * u, tapa.height))
        drawRect(t.c(mezcla(cuero, CUERO_OSC, 0.45f)), tapa.topLeft, tapa.size)
        clipRect(tapa.left, tapa.top, tapa.right, tapa.bottom) { tumbado(tapa, t, 1.2f) }
        drawOval(t.c(mezcla(cuero, CUERO_OSC, 0.6f)), Offset(tapa.right - w * 0.015f, tapa.top), Size(w * 0.03f, tapa.height))
        drawRect(t.c(LATON), Offset(tapa.left, tapa.top), Size(w * 0.008f, tapa.height))
        drawPath(Path().apply { moveTo(tapa.left + w * 0.004f, tapa.bottom - 1f * u); cubicTo(tapa.left - w * 0.02f, tapa.bottom + h * 0.05f, tapa.left - w * 0.06f, tapa.bottom + h * 0.02f, tapa.left - w * 0.07f, tubo.bottom - 1f * u) }, t.c(CUERO_OSC), style = Stroke(1.2f * u))
        // La boca de la izquierda y los mapas que salen.
        val boca = Rect(tubo.left - tubo.height * 0.2f, tubo.top, tubo.left + tubo.height * 0.2f, tubo.bottom)
        drawOval(t.c(mezcla(cuero, CUERO_OSC, 0.4f)), boca.topLeft, boca.size)
        drawOval(t.s(0.85f), Offset(boca.left + boca.width * 0.18f, boca.top + boca.height * 0.12f), Size(boca.width * 0.64f, boca.height * 0.76f))
        val rollos = (1 + n / 3).coerceIn(1, 4)
        for (i in 0 until rollos) {
            val salen = w * (0.03f + 0.014f * n) * (0.6f + 0.4f * azar(c.semilla, i))
            val d = tubo.height * (0.3f - 0.03f * i)
            val cy = tubo.center.y + tubo.height * (-0.18f + 0.14f * i) * (if (i % 2 == 0) 1f else -1f)
            val rr = Rect(boca.center.x - salen, cy - d / 2, boca.center.x, cy + d / 2)
            drawRect(t.s(0.25f), Offset(rr.left + 1f * u, rr.top + 1.5f * u), rr.size)
            drawRect(t.c(if (i % 2 == 0) VITELA else mezcla(VITELA, PINO_LUZ, 0.4f)), rr.topLeft, rr.size)
            clipRect(rr.left, rr.top, rr.right, rr.bottom) { tumbado(rr, t, 0.9f) }
            drawOval(t.c(VITELA_OSC), Offset(rr.left - d * 0.22f, rr.top), Size(d * 0.44f, d))
            drawOval(t.c(mezcla(VITELA_OSC, CUERO, 0.3f)), Offset(rr.left - d * 0.1f, rr.top + d * 0.25f), Size(d * 0.2f, d * 0.5f), style = Stroke(0.6f * u))
            if (i == 0) drawRect(t.c(RUBI), Offset(rr.left + rr.width * 0.4f, rr.top), Size(1.8f * u, rr.height))
        }
        filo(tubo, t, der)
        // La horquilla de delante, que abraza el tubo.
        horquillas.forEach { x ->
            drawLine(t.c(mezcla(PINO, PINO_OSC, 0.2f)), Offset(x, tubo.bottom + h * 0.01f), Offset(x - w * 0.012f, tubo.top - h * 0.04f), w * 0.016f, StrokeCap.Round)
            drawLine(t.b(0.3f), Offset(x - w * 0.006f, tubo.bottom), Offset(x - w * 0.018f, tubo.top - h * 0.035f), 0.8f * u)
        }
        // La tablilla con el tema, apoyada en los pies.
        placaTema(w, h, t, madera = true)
    }

    /**
     * El zurrón de cuero (natural, tostado o pardo, según la semilla): el cuerpo panzudo con sus costuras, la correa del hombro
     * en arco, la solapa que se levanta con lo que guarda (asoman mapas y páginas), dos correas con hebilla y la placa de latón.
     */
    private fun DrawScope.zurron(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val cuero = when (Math.floorMod(m ushr 3, 3)) { 0 -> CUERO; 1 -> mezcla(CUERO, PINO_LUZ, 0.3f); else -> mezcla(CUERO_OSC, PIEDRA_CALIDA, 0.2f) }
        val n = c.n.coerceIn(0, 10)
        val bolsa = Rect(w * 0.1f, h * 0.3f, w * 0.9f, piso)
        // La correa del hombro, en arco por detrás.
        val asa = Path().apply { moveTo(bolsa.left + w * 0.06f, bolsa.top + h * 0.03f); cubicTo(bolsa.left + w * 0.12f, h * 0.0f, bolsa.right - w * 0.12f, h * 0.0f, bolsa.right - w * 0.06f, bolsa.top + h * 0.03f) }
        translate(w * 0.02f, h * 0.025f) { drawPath(asa, t.s(0.25f), style = Stroke(w * 0.035f)) }
        drawPath(asa, t.c(mezcla(cuero, CUERO_OSC, 0.55f)), style = Stroke(w * 0.032f))
        drawPath(asa, t.b(0.3f), style = Stroke(0.8f * u))
        // Sombra en el muro y en la balda.
        drawRoundRect(t.s(0.28f), Offset(bolsa.left + w * 0.035f, bolsa.top + h * 0.03f), bolsa.size, CornerRadius(w * 0.09f))
        contacto(Offset(bolsa.center.x + w * 0.04f, piso), bolsa.width * 1.06f, h * 0.05f, 0.42f)
        paginas(bolsa.left + w * 0.1f, bolsa.right - w * 0.1f, bolsa.top + h * 0.04f, h, c, t)
        // El cuerpo.
        val cuerpoP = Path().apply { addRoundRect(RoundRect(bolsa, CornerRadius(3f * u), CornerRadius(3f * u), CornerRadius(w * 0.09f), CornerRadius(w * 0.09f))) }
        drawPath(cuerpoP, t.c(cuero))
        clipPath(cuerpoP) {
            pinceladas(bolsa, if (t.o) NOCHE else CUERO_OSC, c.semilla, 8, u, 0.14f)
            if (der) { drawRect(t.b(0.18f), Offset(bolsa.right - bolsa.width * 0.2f, bolsa.top), Size(bolsa.width * 0.2f, bolsa.height)); drawRect(t.s(0.2f), bolsa.topLeft, Size(bolsa.width * 0.14f, bolsa.height)) }
            else { drawRect(t.b(0.18f), bolsa.topLeft, Size(bolsa.width * 0.22f, bolsa.height)); drawRect(t.s(0.26f), Offset(bolsa.right - bolsa.width * 0.14f, bolsa.top), Size(bolsa.width * 0.14f, bolsa.height)) }
            drawRect(t.s(0.25f), Offset(bolsa.left, bolsa.bottom - bolsa.height * 0.18f), Size(bolsa.width, bolsa.height * 0.18f))
            // El roce de la esquina.
            drawOval(t.c(mezcla(cuero, VITELA, 0.35f)).copy(alpha = 0.45f), Offset(bolsa.left + bolsa.width * 0.06f, bolsa.bottom - h * 0.07f), Size(w * 0.1f, h * 0.04f))
        }
        drawPath(cuerpoP, t.c(mezcla(cuero, VITELA, 0.4f)).copy(alpha = 0.8f), style = Stroke(0.7f * u, pathEffect = puntos(1.8f * u, 1.8f * u)))
        // Las puntas de las correas, debajo de la placa.
        listOf(0.3f, 0.7f).forEach { fx -> drawRect(t.c(mezcla(cuero, CUERO_OSC, 0.6f)), Offset(w * fx - w * 0.025f, h * 0.8f), Size(w * 0.05f, piso - h * 0.8f - 1f * u)) }
        // La solapa, levantada con lo que guarda.
        val lift = h * 0.008f * n.coerceAtMost(6)
        val sol = Rect(bolsa.left - 1f * u, bolsa.top - lift, bolsa.right + 1f * u, h * 0.535f)
        val solP = Path().apply { addRoundRect(RoundRect(sol, CornerRadius(3f * u), CornerRadius(3f * u), CornerRadius(w * 0.1f), CornerRadius(w * 0.1f))) }
        translate(0.8f * u, 2.4f * u) { drawPath(solP, t.s(0.35f)) }
        drawPath(solP, t.c(mezcla(cuero, CUERO_OSC, 0.2f)))
        clipPath(solP) {
            drawRect(t.b(0.22f), sol.topLeft, Size(sol.width, sol.height * 0.2f))
            drawRect(t.s(0.15f), Offset(sol.left, sol.bottom - sol.height * 0.25f), Size(sol.width, sol.height * 0.25f))
        }
        drawRoundRect(t.c(mezcla(cuero, VITELA, 0.4f)).copy(alpha = 0.8f), Offset(sol.left + 3f * u, sol.top + 3f * u), Size(sol.width - 6f * u, sol.height - 6f * u), CornerRadius(w * 0.08f), style = Stroke(0.7f * u, pathEffect = puntos(1.8f * u, 1.8f * u)))
        drawRect(t.b(0.45f), sol.topLeft, Size(sol.width, 1f * u))
        filo(sol, t, der)
        // Las correas de la solapa con su hebilla.
        listOf(0.3f, 0.7f).forEachIndexed { i, fx ->
            val x = w * fx
            drawRect(t.s(0.3f), Offset(x - w * 0.025f + 1.2f * u, sol.top + h * 0.06f), Size(w * 0.05f, sol.bottom - sol.top - h * 0.02f))
            drawRect(t.c(mezcla(cuero, CUERO_OSC, 0.6f)), Offset(x - w * 0.025f, sol.top + h * 0.05f), Size(w * 0.05f, sol.bottom - sol.top - h * 0.02f))
            drawRect(t.b(0.3f), Offset(x - w * 0.025f, sol.top + h * 0.05f), Size(1f * u, sol.bottom - sol.top - h * 0.02f))
            val hb = Rect(x - w * 0.035f, sol.bottom - h * 0.075f, x + w * 0.035f, sol.bottom - h * 0.015f)
            drawRect(t.s(0.35f), hb.topLeft + Offset(1f * u, 1.3f * u), hb.size, style = Stroke(1.5f * u))
            drawRect(t.c(LATON), hb.topLeft, hb.size, style = Stroke(1.5f * u))
            drawLine(t.c(LATON), Offset(x, hb.top), Offset(x, hb.bottom), 1f * u)
            destello(Offset(if (der) hb.right else hb.left, hb.top), 1f * u, t)
            if (i == 1) drawRect(t.c(mezcla(cuero, CUERO_OSC, 0.6f)), Offset(hb.right, hb.center.y - w * 0.01f), Size(w * 0.03f, w * 0.02f))
        }
        placaTema(w, h, t, madera = false)
    }

    /** Lo que no tiene tema: un cesto de mimbre con rollos sueltos que asoman (más con más fichas) y la tablilla sin grabar. */
    private fun DrawScope.cesto(w: Float, h: Float, c: EstadoCajon, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val top = h * 0.4f
        val mimbre = mezcla(PINO_LUZ, CUERDA, 0.4f)
        val n = c.n.coerceIn(0, 10)
        // Los rollos, metidos de cualquier manera.
        val rollos = (2 + n / 2).coerceAtMost(6)
        for (i in 0 until rollos) {
            val x = w * (0.2f + 0.6f * (i + 0.5f) / rollos)
            val largo = h * (0.18f + 0.025f * n) * (0.7f + 0.3f * azar(c.semilla, i))
            val g = (azar(c.semilla, i + 10) - 0.5f) * 40f
            val d = w * 0.05f
            rotate(g, Offset(x, top + h * 0.1f)) {
                val r = Rect(x - d / 2, top + h * 0.1f - largo, x + d / 2, top + h * 0.1f)
                drawRect(t.s(0.25f), Offset(r.left + 1.5f * u, r.top + 1.5f * u), r.size)
                drawRect(t.c(if (i % 2 == 0) VITELA else mezcla(VITELA, PINO_LUZ, 0.4f)), r.topLeft, r.size)
                drawRect(t.b(0.3f), r.topLeft, Size(r.width * 0.3f, r.height))
                drawRect(t.s(0.22f), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
                drawOval(t.c(VITELA_OSC), Offset(r.left, r.top - d * 0.22f), Size(d, d * 0.44f))
                drawOval(t.c(mezcla(VITELA_OSC, CUERO, 0.3f)), Offset(r.left + d * 0.3f, r.top - d * 0.1f), Size(d * 0.4f, d * 0.2f), style = Stroke(0.6f * u))
                if (i % 3 == 0) drawRect(t.c(if (i % 2 == 0) RUBI else MUSGO), Offset(r.left, r.top + r.height * 0.3f), Size(r.width, 1.8f * u))
            }
        }
        // El cesto.
        val cesta = poligono(w * 0.08f, top, w * 0.92f, top, w * 0.86f, piso, w * 0.14f, piso)
        translate(w * 0.03f, h * 0.025f) { drawPath(cesta, t.s(0.25f)) }
        contacto(Offset(w * 0.54f, piso), w * 0.8f, h * 0.05f, 0.42f)
        drawPath(cesta, t.c(mimbre))
        clipPath(cesta) {
            val alto = h * 0.075f; val ancho = w * 0.09f
            var y = top + h * 0.03f; var fila = 0
            while (y < piso) {
                var x = w * 0.06f - (if (fila % 2 == 0) 0f else ancho / 2)
                while (x < w) {
                    drawRoundRect(t.c(mezcla(mimbre, if (fila % 2 == 0) PINO_LUZ else PINO, 0.25f * azar(c.semilla + fila, (x / u).toInt()))), Offset(x + 0.8f * u, y + 0.8f * u), Size(ancho - 1.6f * u, alto - 1.6f * u), CornerRadius(alto / 2))
                    drawRect(t.s(0.22f), Offset(x + 2f * u, y + alto - 2.2f * u), Size(ancho - 4f * u, 1.2f * u))
                    x += ancho
                }
                y += alto; fila++
            }
            if (der) drawRect(t.b(0.15f), Offset(w * 0.75f, top), Size(w * 0.2f, piso - top))
            else drawRect(t.b(0.15f), Offset(w * 0.08f, top), Size(w * 0.18f, piso - top))
            drawRect(t.s(0.25f), Offset(if (der) w * 0.08f else w * 0.78f, top), Size(w * 0.14f, piso - top))
            drawRect(t.s(0.25f), Offset(0f, piso - h * 0.08f), Size(w, h * 0.08f))
        }
        // El borde trenzado y las asas.
        val borde = Rect(w * 0.07f, top - h * 0.03f, w * 0.93f, top + h * 0.03f)
        drawRoundRect(t.s(0.3f), borde.topLeft + Offset(0f, 2f * u), borde.size, CornerRadius(borde.height / 2))
        drawRoundRect(t.c(mezcla(mimbre, PINO, 0.3f)), borde.topLeft, borde.size, CornerRadius(borde.height / 2))
        drawLine(t.s(0.35f), Offset(borde.left + 3f * u, borde.center.y), Offset(borde.right - 3f * u, borde.center.y), borde.height * 0.7f, pathEffect = puntos(1.2f * u, 2.6f * u))
        drawLine(t.b(0.4f), Offset(borde.left + 3f * u, borde.top + 1f * u), Offset(borde.right - 3f * u, borde.top + 1f * u), 0.8f * u)
        listOf(w * 0.08f, w * 0.92f).forEach { x -> drawArc(t.c(mezcla(mimbre, PINO, 0.3f)), 180f, 180f, false, Offset(x - w * 0.04f, top - h * 0.1f), Size(w * 0.08f, h * 0.16f), style = Stroke(w * 0.012f)) }
        filo(Rect(borde.left, borde.top, borde.right, piso), t, der)
        // La tablilla sin grabar, atada con un cordel.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        listOf(r.left + w * 0.06f, r.right - w * 0.06f).forEach { x -> drawLine(t.c(CUERDA), Offset(x, borde.bottom), Offset(x, r.top + 2f * u), 1f * u) }
        placaTema(w, h, t, madera = true)
    }

    /** Un hueco libre: una piedra del camino con una runa pintada, o una vela apagada en su palmatoria de hierro. */
    private fun DrawScope.vacio(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val piso = h * PISO
        if (Math.floorMod(c.semilla ushr 2, 2) == 0) {
            val r = Rect(w * 0.32f, h * 0.55f, w * 0.64f, piso + h * 0.01f)
            val piedra = Path().apply {
                moveTo(r.left, r.bottom)
                cubicTo(r.left - r.width * 0.06f, r.top + r.height * 0.3f, r.left + r.width * 0.25f, r.top, r.center.x, r.top)
                cubicTo(r.right - r.width * 0.12f, r.top, r.right + r.width * 0.06f, r.top + r.height * 0.45f, r.right, r.bottom)
                close()
            }
            translate(w * 0.03f, h * 0.02f) { drawPath(piedra, t.s(0.28f)) }
            contacto(Offset(r.center.x + w * 0.03f, piso), r.width * 1.2f, h * 0.05f, 0.42f)
            drawPath(piedra, t.c(PIEDRA_CALIDA))
            clipPath(piedra) {
                drawOval(t.b(0.25f), Offset(r.left, r.top), Size(r.width * 0.55f, r.height * 0.6f))
                drawOval(t.s(0.3f), Offset(r.center.x, r.top + r.height * 0.3f), Size(r.width * 0.7f, r.height))
                for (k in 0 until 4) drawCircle(t.s(0.3f), 0.9f * u, Offset(r.left + r.width * azar(c.semilla, k), r.top + r.height * azar(c.semilla, k + 5)))
            }
            runa(Math.floorMod(c.semilla, 9), r.center + Offset(0f, r.height * 0.08f), r.height * 0.45f, t.c(mezcla(RUBI, LATON, 0.35f)).copy(alpha = 0.85f), w * 0.013f)
            // Un guijarro al lado.
            val g = Offset(w * 0.74f, piso - h * 0.03f)
            contacto(g + Offset(w * 0.01f, h * 0.03f), w * 0.08f, h * 0.03f, 0.4f)
            drawOval(t.c(PIEDRA_FRIA), Offset(g.x - w * 0.035f, g.y - h * 0.03f), Size(w * 0.07f, h * 0.06f))
            drawOval(t.b(0.25f), Offset(g.x - w * 0.028f, g.y - h * 0.026f), Size(w * 0.03f, h * 0.02f))
        } else {
            val cx = w * 0.45f
            contacto(Offset(cx + w * 0.02f, piso), w * 0.3f, h * 0.05f, 0.42f)
            // La cera que se derramó en la balda.
            drawOval(t.c(CERA), Offset(cx + w * 0.12f, piso - h * 0.012f), Size(w * 0.05f, h * 0.02f))
            // La palmatoria: el plato, el asa y el cubo.
            val plato = Rect(cx - w * 0.12f, piso - h * 0.06f, cx + w * 0.12f, piso)
            drawOval(t.c(HIERRO), plato.topLeft, plato.size)
            drawArc(t.b(0.35f), 180f, 180f, false, plato.topLeft, plato.size, style = Stroke(1f * u))
            drawCircle(t.c(HIERRO), w * 0.03f, Offset(plato.right + w * 0.02f, plato.center.y - h * 0.01f), style = Stroke(w * 0.008f))
            drawRect(t.c(HIERRO), Offset(cx - w * 0.04f, plato.center.y - h * 0.06f), Size(w * 0.08f, h * 0.06f))
            // La vela, consumida a medias, con sus chorreras y el pabilo negro.
            val vela = Rect(cx - w * 0.032f, h * 0.48f, cx + w * 0.032f, plato.center.y - h * 0.05f)
            drawRect(t.s(0.25f), Offset(vela.left + w * 0.02f, vela.top + h * 0.02f), vela.size)
            drawRect(t.c(CERA), vela.topLeft, vela.size)
            drawRect(t.b(0.3f), vela.topLeft, Size(vela.width * 0.3f, vela.height))
            drawRect(t.s(0.2f), Offset(vela.right - vela.width * 0.3f, vela.top), Size(vela.width * 0.3f, vela.height))
            listOf(0.25f to 0.4f, 0.7f to 0.6f).forEach { (fx, fl) -> drawRoundRect(t.c(mezcla(CERA, VITELA, 0.3f)), Offset(vela.left + vela.width * fx - 1.5f * u, vela.top), Size(3f * u, vela.height * fl), CornerRadius(1.5f * u)) }
            drawOval(t.c(mezcla(CERA, VITELA_OSC, 0.3f)), Offset(vela.left, vela.top - h * 0.012f), Size(vela.width, h * 0.024f))
            drawLine(t.c(TINTA), Offset(cx, vela.top), Offset(cx + w * 0.006f, vela.top - h * 0.04f), 1.2f * u, StrokeCap.Round)
        }
    }

    override val rotulo = Rect(0.09f, 0.565f, 0.91f, 0.835f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.camino
    override val salida = -SALIDA
    override val placa = Rect(0.33f, 0.05f, 0.67f, 0.25f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Terciopelo verde capitoné: un brillo en diagonal, botones dorados en rombo y los pliegues que salen de cada uno.
        drawRect(Brush.linearGradient(listOf(t(mezcla(TERCIOPELO, Color.Black, 0.25f), oscuro), t(mezcla(TERCIOPELO, Color.White, 0.08f), oscuro), t(mezcla(TERCIOPELO, Color.Black, 0.3f), oscuro)), Offset.Zero, Offset(w, h)))
        val paso = 58 * u
        var fila = 0
        var y = paso / 2
        while (y < h) {
            var x = if (fila % 2 == 0) paso / 2 else 0f
            while (x < w + paso) {
                val o = Offset(x, y)
                listOf(Offset(paso / 2, paso / 2), Offset(-paso / 2, paso / 2), Offset(paso / 2, -paso / 2), Offset(-paso / 2, -paso / 2)).forEach { dlt ->
                    drawLine(Color.Black.copy(alpha = 0.25f), o, Offset(o.x + dlt.x, o.y + dlt.y), 1.6f * u)
                    drawLine(Color.White.copy(alpha = 0.06f), Offset(o.x + 1 * u, o.y), Offset(o.x + dlt.x + 1 * u, o.y + dlt.y), 1f * u)
                }
                mancha(o, 10 * u, Color.Black.copy(alpha = 0.3f))
                drawCircle(t(ORO, oscuro), 2.6f * u, o)
                drawCircle(Color.White.copy(alpha = 0.5f), 0.9f * u, Offset(o.x - 0.8f * u, o.y - 0.8f * u))
                x += paso
            }
            y += paso / 2; fila++
        }
        // Las paredes de tablas con su fila de clavos dorados.
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r ->
            drawRect(t(MADERA, oscuro), r.topLeft, r.size)
            vetas(r, MADERA_OSC.copy(alpha = 0.5f), (r.left + r.top).toInt(), 1f, vertical = r.height > r.width, grosor = 0.6f, ondulacion = 0.5f)
        }
        var x = pl * 2
        while (x < w - pl) { drawCircle(t(ORO, oscuro), 1.4f * u, Offset(x, pl / 2)); drawCircle(t(ORO, oscuro), 1.4f * u, Offset(x, h - pl / 2)); x += 14 * u }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.35f)
    }

    override fun tintaInterior(oscuro: Boolean) = Color(0xFFEFE6CC)
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 34, 0.007f)) }

    /** Un nudo en una esquina: dos lazos cuadrados entrelazados, en verde y oro. */
    private fun DrawScope.nudo(c: Offset, s: Float, grosor: Float) {
        rotate(45f, c) {
            drawRoundRect(ESMERALDA.copy(alpha = 0.8f), Offset(c.x - s * 0.5f, c.y - s * 0.22f), Size(s, s * 0.44f), CornerRadius(s * 0.2f), style = Stroke(grosor))
            drawRoundRect(ORO, Offset(c.x - s * 0.22f, c.y - s * 0.5f), Size(s * 0.44f, s), CornerRadius(s * 0.2f), style = Stroke(grosor))
        }
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.8f else 1f
        val s = 10 * u * g
        listOf(Offset(s * 0.8f, s * 0.8f), Offset(w - s * 0.8f, s * 0.8f), Offset(s * 0.8f, h - s * 0.8f), Offset(w - s * 0.8f, h - s * 0.8f)).forEach { nudo(it, s, 1.1f * u * g) }
        // Un camino punteado tenue que cruza la página, y una rosa de los vientos chica.
        val camino = Path().apply {
            moveTo(w * 0.1f, h * (0.6f + 0.2f * azar(semilla, 1)))
            cubicTo(w * 0.35f, h * (0.3f + 0.4f * azar(semilla, 2)), w * 0.6f, h * (0.5f + 0.4f * azar(semilla, 3)), w * 0.9f, h * (0.35f + 0.3f * azar(semilla, 4)))
        }
        drawPath(camino, RUBI.copy(alpha = 0.18f), style = Stroke(1.2f * u * g, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u * g, 3 * u * g))))
        val rosa = Offset(w - s * 2.6f, h - s * 2.8f)
        for (k in 0 until 4) {
            val a = k * 1.5708f
            drawPath(Path().apply { moveTo(rosa.x, rosa.y); lineTo(rosa.x + s * 0.25f * cos(a + 0.785f), rosa.y + s * 0.25f * sin(a + 0.785f)); lineTo(rosa.x + s * cos(a), rosa.y + s * sin(a)); close() }, TINTA.copy(alpha = 0.18f))
        }
        drawCircle(TINTA.copy(alpha = 0.15f), s * 0.55f, rosa, style = Stroke(0.6f * u))
    }

    override val margenes = MargenesFicha(15.dp, 16.dp, 16.dp, 28.dp, 32.dp, 34.dp)
    override val proporcionFicha = 0.9f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.75f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // Una runa grabada en piedra.
            "hecho" -> { drawRoundRect(MADERA, Offset(c.x - s * 0.36f, c.y - s * 0.42f), Size(s * 0.72f, s * 0.84f), androidx.compose.ui.geometry.CornerRadius(s * 0.16f)); runa(3, c, s * 0.56f, p.color, s * 0.09f) }
            "convence" -> gema(c, s * 0.42f, ESMERALDA)
            "piensa" -> runa(5, c, s * 0.8f, MADERA, s * 0.1f)
            // Una media luna: la compañía cuenta el tiempo por lunas.
            "duda" -> { drawCircle(p.tenue, s * 0.36f, c); drawCircle(p.color, s * 0.3f, Offset(c.x + s * 0.16f, c.y - s * 0.06f)) }
            "desacuerdo" -> { runa(2, c, s * 0.8f, RUBI, s * 0.1f); drawLine(RUBI, Offset(c.x - s * 0.4f, c.y + s * 0.3f), Offset(c.x + s * 0.4f, c.y - s * 0.3f), s * 0.1f) }
            else -> {}
        }
    }

    /** Un broche: un aro de oro con un rubí. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawCircle(ORO, s * 0.42f, c, style = Stroke(s * 0.14f))
        drawCircle(RUBI, s * 0.22f, c)
        drawCircle(Color.White.copy(alpha = 0.5f), s * 0.07f, Offset(c.x - s * 0.07f, c.y - s * 0.07f))
    }

    override val desorden = 0f
}
