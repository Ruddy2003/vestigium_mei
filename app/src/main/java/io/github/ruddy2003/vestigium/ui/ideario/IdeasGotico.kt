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
import io.github.ruddy2003.vestigium.ui.muebles.sombraContacto
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// =====================================================================
//  Gótico
// =====================================================================

/**
 * Gótico: la cajonera de la sacristía de una catedral del siglo XIV, contra un muro de sillares de caliza con sus juntas,
 * tres marcas de cantero y una cruz de consagración de almagre medio borrada. La luz de día entra por una ventana que queda
 * fuera del cuadro, arriba a la izquierda: en la piedra cae una mancha oblicua con la forma torcida de dos lancetas y un tinte
 * leve de azul, grana y oro (el de una vidriera que no se ve). El mueble es de roble oscuro: un tablero grueso con su moldura y
 * gotas de cera, una crestería de cuatro arcos apuntados calados con su tracería en relieve, montantes como contrafuertes con
 * herrajes de hierro en flor de lis, y un zócalo con cuadrifolios sobre pies gastados por el fregado. Encima, a la izquierda,
 * el atril bajo con el misal abierto (dos columnas, una inicial azul, rúbricas rojas) y sus tres cintas de registro colgando
 * por el canto; una estola grana doblada que cae y tapa parte de la crestería; al fondo, el cáliz con su patena tapado con un
 * paño de lino; delante, un candelero de latón alto con la vela a medias y la cera chorreada, y otro bajo con la vela casi
 * consumida. En el muro, el incensario colgado de un gancho por sus tres cadenas (con su mancha de hollín en la piedra), un
 * manojo de llaves en un clavo y, en el rincón, la cruz procesional apoyada, cortada por el borde de arriba. Al pie, sobre
 * las losas, un escabel de pies calados. Cada tema es un cajón con su cartela de vitela y marco de latón, de una de tres
 * clases según el tema: con paneles de pergamino plegado, con fajas de hierro en flor de lis y cerradura, o con portezuelas
 * caladas (un cuadrifolio y un trébol) tras las que se adivinan las hojas. Los más llenos quedan más entreabiertos y asoman
 * más hojas de vitela; alguno tiene el frente rozado o una grieta cosida con una grapa de hierro. Lo que no tiene tema lleva
 * una tira de vitela atada a la argolla en lugar de cartela; un hueco todavía sin tema es un cajón liso con los agujeros de
 * la cartela que no tiene, o con un paño doblado encima. De noche arden las dos velas: su halo sube por la piedra, encienden
 * el latón, el cáliz y la cruz, recortan la crestería y tienden las sombras largas hacia fuera; lo demás se hunde en azul
 * negro. Por dentro, seda azul adamascada con flores de lis. Las fichas son hojas de vitela con un filete de oro y hojas de
 * hiedra en el margen.
 */
object IdeasGotico : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta de la sacristía
    /** La caliza del muro, su cara en sombra y el mortero de las juntas. */
    private val CALIZA = Color(0xFFD8CCAE)
    private val CALIZA_OSC = Color(0xFFAC9D7F)
    private val JUNTA = Color(0xFF8E8370)
    /** Las losas del suelo. */
    private val LOSA = Color(0xFF9A8F7E)
    /** El roble oscuro del mueble: su luz y lo hondo de la talla. */
    private val ROBLE = Color(0xFF5A3E24)
    private val ROBLE_LUZ = Color(0xFF8A6842)
    private val ROBLE_OSC = Color(0xFF2E1F12)
    /** El hierro forjado de herrajes, llaves y argollas. */
    private val HIERRO = Color(0xFF34323A)
    private val HIERRO_LUZ = Color(0xFF7A7680)
    /** El latón de los candeleros, el incensario y los marcos; el oro viejo del cáliz y la cruz. */
    private val LATON = Color(0xFFC29A46)
    private val LATON_LUZ = Color(0xFFF2DC98)
    private val LATON_OSC = Color(0xFF6A4E22)
    private val ORO = Color(0xFFC79A3A)
    /** La vitela de las hojas y las cartelas; la de las cartelas de noche, a la luz de las velas, para que el rótulo se lea. */
    private val VITELA = Color(0xFFF3ECDC)
    private val VITELA_OSC = Color(0xFFD9C9A6)
    private val VITELA_NOCHE = Color(0xFFBCA882)
    /** La cera de las velas. */
    private val CERA = Color(0xFFF0E3B4)
    /** La grana de la estola y las cintas, el ultramar y el almagre de la cruz de consagración. */
    private val GRANA = Color(0xFF9C2A30)
    private val ULTRAMAR = Color(0xFF34518E)
    private val ALMAGRE = Color(0xFFA4503A)
    private val TINTA = Color(0xFF141A33)
    /** La noche de la sacristía: azul negro. Y la llama de las velas. */
    private val NOCHE = Color(0xFF0D0F1B)
    private val LLAMA = Color(0xFFFFC56E)

    /** Los colores del interior y de las fichas (los de siempre). */
    private val AZUL = Color(0xFF1F3A9E)
    private val CARMESI = Color(0xFFB3122E)
    private val DAMASCO = Color(0xFF1C2A5C)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- la luz y la noche

    /** Dónde arden las dos velas de noche (la punta de la llama), en fracciones del ancho. */
    private val VELAS = listOf(Offset(0.655f, 0.19f), Offset(0.765f, 0.322f))

    /** Cuánto alumbran las velas el punto [p] (0 a 1) en un lienzo de ancho [w]. */
    private fun calor(p: Offset, w: Float): Float {
        var m = 0f
        for (v in VELAS) m = maxOf(m, 1f - (p - v * w).getDistance() / (w * 0.62f))
        return m.coerceIn(0f, 1f)
    }

    /** Un color a [k] (0 a 1) de las velas: de día, tal cual; de noche, hundido en el azul negro y calentado cerca de ellas. */
    private fun n(c: Color, oscuro: Boolean, k: Float): Color {
        if (!oscuro) return c
        val q = k.coerceIn(0f, 1f)
        return mezcla(mezcla(c, NOCHE, 0.9f - 0.62f * q.pow(1.2f)), LLAMA, 0.24f * q)
    }

    /** La sombra: de día, violeta hondo; de noche, el azul negro. */
    private fun sombra(oscuro: Boolean, a: Float) = if (oscuro) NOCHE.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo cálido de las velas, según lo cerca que esté. */
    private fun brillo(oscuro: Boolean, a: Float, k: Float = 0f) =
        if (oscuro) LLAMA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f)) else luzMueble(a)

    override val cols = 2
    override val proporcion = 2.0f
    override val corona = 0.6f
    override val base = 0.25f
    override val lado = 0.085f
    override val hueco = 0.03f

    /** El tablero: su cara de arriba, su canto y el pie de la crestería, en fracciones del ancho desde arriba. */
    private const val TAB = 0.44f
    private const val TAB_F = 0.455f
    private const val TAB_B = 0.483f
    private const val CREST_B = 0.585f
    /** Los costados del cuerpo y los del tablero, que vuela un poco, en fracciones del ancho. */
    private const val BL = 0.045f
    private const val BR = 0.955f
    private const val TL = 0.022f
    private const val TR = 0.978f
    /** En la base: el rincón del muro con el suelo, el fin del zócalo y donde pisan los pies, en fracción de su alto. */
    private const val RINCON = 0.24f
    private const val ZOC = 0.38f
    private const val PIES = 0.56f
    /** Cuánto baja el frente del cajón al abrirse, en fracción de su alto. */
    private const val SALIDA = 0.18f

    // ---------------------------------------------------------------- ayudantes

    /** Un tramo recto de [a] a [b] con [ancho], para las siluetas (cadenas, astas). */
    private fun Path.tramo(a: Offset, b: Offset, ancho: Float) {
        val d = b - a
        val l = d.getDistance().coerceAtLeast(0.001f)
        val nx = -d.y / l * ancho / 2; val ny = d.x / l * ancho / 2
        moveTo(a.x + nx, a.y + ny); lineTo(b.x + nx, b.y + ny); lineTo(b.x - nx, b.y - ny); lineTo(a.x - nx, a.y - ny); close()
    }

    /** La unión de varias figuras en una silueta sin huecos. */
    private fun unir(partes: List<Path>): Path {
        var r = Path()
        for (p in partes) r = Path().apply { op(r, p, PathOperation.Union) }
        return r
    }

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /**
     * La sombra de una cosa en el muro, por detrás: de día, corrida a la derecha y abajo; de noche, la de la vela [vela],
     * larga y hacia fuera de ella. Solo cae en la parte del muro que se ve sobre el tablero.
     */
    private fun DrawScope.sombraEnMuro(sil: Path, centro: Offset, w: Float, oscuro: Boolean, vela: Int = 0, fuerza: Float = 1f) {
        clipRect(0f, 0f, w, TAB * w + 1f) {
            if (!oscuro) translate(w * 0.024f, w * 0.009f) { drawPath(sil, sombraMueble(0.2f * fuerza)) }
            else {
                val v = VELAS[vela] * w
                val o = (centro - v) * 0.32f
                withTransform({ translate(o.x, o.y); scale(1.2f, 1.2f, centro) }) { drawPath(sil, NOCHE.copy(alpha = 0.45f * fuerza)) }
            }
        }
    }

    /** Una figura en relieve: su sombra abajo a la derecha, su filo de luz arriba a la izquierda y la figura encima. */
    private fun DrawScope.relieve(p: Path, base: Color, oscuro: Boolean, k: Float, d: Float) {
        translate(d, d) { drawPath(p, sombra(oscuro, 0.55f)) }
        translate(-d * 0.6f, -d * 0.6f) { drawPath(p, brillo(oscuro, 0.4f, k + 0.3f)) }
        drawPath(p, base)
    }

    /** Una figura hundida en la madera: el fondo en sombra y, abajo a la derecha, el suelo del rebaje que coge la luz. */
    private fun DrawScope.hundido(p: Path, fondo: Color, suelo: Color, d: Float, oscuro: Boolean, k: Float) {
        drawPath(p, fondo)
        clipPath(p) { translate(d, d) { drawPath(p, suelo) } }
        translate(d * 0.4f, d * 0.4f) { drawPath(p, brillo(oscuro, 0.22f, k + 0.2f), style = Stroke(0.7f * density)) }
    }

    /** Un cuadrifolio: cuatro lóbulos alrededor de [c]. */
    private fun cuadrifolioPath(c: Offset, r: Float): Path = unir(listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f)).map { o ->
        Path().apply { addOval(Rect(Offset(c.x + o.x * r * 0.5f, c.y + o.y * r * 0.5f), r * 0.52f)) }
    } + Path().apply { addRect(Rect(c.x - r * 0.5f, c.y - r * 0.5f, c.x + r * 0.5f, c.y + r * 0.5f)) })

    /** Un trébol: tres lóbulos alrededor de [c], uno arriba. */
    private fun trebolPath(c: Offset, r: Float): Path = unir((0 until 3).map { i ->
        val a = Math.toRadians(-90.0 + i * 120.0)
        Path().apply { addOval(Rect(Offset(c.x + r * 0.48f * cos(a).toFloat(), c.y + r * 0.48f * sin(a).toFloat()), r * 0.5f)) }
    } + Path().apply { addOval(Rect(c, r * 0.4f)) })

    /** Una flor de lis de [s] de alto, centrada en [c]. */
    private fun DrawScope.florDeLis(c: Offset, s: Float, color: Color) {
        drawPath(Path().apply {
            moveTo(c.x, c.y - s * 0.48f); quadraticTo(c.x + s * 0.17f, c.y - s * 0.16f, c.x, c.y + s * 0.06f); quadraticTo(c.x - s * 0.17f, c.y - s * 0.16f, c.x, c.y - s * 0.48f); close()
            moveTo(c.x + s * 0.04f, c.y + s * 0.04f); cubicTo(c.x + s * 0.3f, c.y - s * 0.04f, c.x + s * 0.46f, c.y - s * 0.28f, c.x + s * 0.32f, c.y - s * 0.36f)
            quadraticTo(c.x + s * 0.36f, c.y - s * 0.1f, c.x + s * 0.08f, c.y + s * 0.1f); close()
            moveTo(c.x - s * 0.04f, c.y + s * 0.04f); cubicTo(c.x - s * 0.3f, c.y - s * 0.04f, c.x - s * 0.46f, c.y - s * 0.28f, c.x - s * 0.32f, c.y - s * 0.36f)
            quadraticTo(c.x - s * 0.36f, c.y - s * 0.1f, c.x - s * 0.08f, c.y + s * 0.1f); close()
            moveTo(c.x - s * 0.06f, c.y + s * 0.14f); lineTo(c.x + s * 0.06f, c.y + s * 0.14f); lineTo(c.x + s * 0.02f, c.y + s * 0.46f); lineTo(c.x - s * 0.02f, c.y + s * 0.46f); close()
        }, color)
        drawRect(color, Offset(c.x - s * 0.22f, c.y + s * 0.06f), Size(s * 0.44f, s * 0.09f))
    }

    /** Un cuadrifolio: cuatro lóbulos alrededor de [c]. */
    private fun DrawScope.cuadrifolio(c: Offset, r: Float, color: Color, trazo: Float? = null) {
        listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f)).forEach { o ->
            val p = Offset(c.x + o.x * r * 0.5f, c.y + o.y * r * 0.5f)
            if (trazo == null) drawCircle(color, r * 0.5f, p) else drawCircle(color, r * 0.5f, p, style = Stroke(trazo))
        }
    }

    // ---------------------------------------------------------------- el muro

    /** Un sillar de caliza en [r]: su tono, la labra, el canto de arriba a la luz, el de abajo en sombra y algún desconchón. */
    private fun DrawScope.sillar(r: Rect, s: Int, oscuro: Boolean, w: Float) {
        val u = density
        // Cada sillar toma la luz de su centro, pero suave: el halo de encima hace el resto, sin escalones de piedra a piedra.
        val k = if (oscuro) 0.55f * calor(r.center, w) else 0f
        var tono = mezcla(CALIZA, CALIZA_OSC, 0.05f + 0.38f * azar(s, 1))
        if (azar(s, 2) > 0.72f) tono = mezcla(tono, Color(0xFFCDB48A), 0.35f)
        drawRect(n(tono, oscuro, k), r.topLeft, r.size)
        // La labra: dos manchas de otro tono, como de trinchante.
        for (j in 0 until 2) {
            val ow = r.width * (0.3f + 0.4f * azar(s, 10 + j)); val oh = r.height * (0.35f + 0.3f * azar(s, 20 + j))
            drawOval(n(mezcla(tono, if (j == 0) CALIZA_OSC else LUZ_MUEBLE, 0.35f), oscuro, k).copy(alpha = 0.4f),
                Offset(r.left + (r.width - ow) * azar(s, 30 + j), r.top + (r.height - oh) * azar(s, 40 + j)), Size(ow, oh))
        }
        drawRect(brillo(oscuro, 0.26f, k), r.topLeft, Size(r.width, 1.1f * u))
        drawRect(brillo(oscuro, 0.14f, k), r.topLeft, Size(1f * u, r.height))
        drawRect(sombra(oscuro, 0.24f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
        drawRect(sombra(oscuro, 0.16f), Offset(r.right - 1f * u, r.top), Size(1f * u, r.height))
        // Un desconchón en una esquina.
        if (azar(s, 3) > 0.74f) {
            val e = r.height * (0.2f + 0.2f * azar(s, 4))
            val der = azar(s, 5) > 0.5f
            val x = if (der) r.right else r.left
            val sx = if (der) -1f else 1f
            val p = poligono(x, r.bottom, x + sx * e * 1.4f, r.bottom, x + sx * e * 0.5f, r.bottom - e * 0.5f, x, r.bottom - e)
            drawPath(p, sombra(oscuro, 0.3f))
            drawLine(brillo(oscuro, 0.3f, k), Offset(x, r.bottom - e), Offset(x + sx * e * 0.5f, r.bottom - e * 0.5f), 0.8f * u)
        }
    }

    /** Una marca de cantero grabada en [c], de [s] de alto: una flecha, un triángulo con su raya o un reloj de arena. */
    private fun DrawScope.marcaCantero(c: Offset, s: Float, tipo: Int, oscuro: Boolean, k: Float) {
        val u = density
        val l = when (tipo) {
            0 -> listOf(0f, 0.5f, 0f, -0.5f, -0.28f, -0.2f, 0f, -0.5f, 0f, -0.5f, 0.28f, -0.2f)
            1 -> listOf(-0.4f, 0.4f, 0.4f, 0.4f, 0.4f, 0.4f, 0f, -0.45f, 0f, -0.45f, -0.4f, 0.4f, 0f, -0.45f, 0f, 0.55f)
            else -> listOf(-0.35f, -0.45f, 0.35f, 0.45f, 0.35f, -0.45f, -0.35f, 0.45f, -0.35f, -0.45f, 0.35f, -0.45f, -0.35f, 0.45f, 0.35f, 0.45f)
        }
        var i = 0
        while (i < l.size) {
            val a = Offset(c.x + l[i] * s, c.y + l[i + 1] * s); val b = Offset(c.x + l[i + 2] * s, c.y + l[i + 3] * s)
            drawLine(brillo(oscuro, 0.3f, k), a + Offset(0.7f * u, 0.7f * u), b + Offset(0.7f * u, 0.7f * u), 0.7f * u, StrokeCap.Round)
            drawLine(sombra(oscuro, 0.45f), a, b, 0.9f * u, StrokeCap.Round)
            i += 4
        }
    }

    /** El muro de sillares, de arriba hasta [hasta]: entero sobre el tablero y, más abajo, solo en las franjas de los lados. */
    private fun DrawScope.muro(w: Float, hasta: Float, oscuro: Boolean) {
        val u = density
        drawRect(n(JUNTA, oscuro, 0.1f), Offset.Zero, Size(w, hasta))
        var y = 0f; var fila = 0
        while (y < hasta) {
            val alto = w * (0.078f + 0.02f * azar(311, fila))
            var x = -w * 0.14f * azar(312, fila)
            var j = 0
            val todo = y < w * (TAB_B + 0.01f)
            while (x < w) {
                val largo = w * (0.19f + 0.16f * azar(313 + fila, j))
                val r = Rect(x + 0.8f * u, y + 0.8f * u, x + largo - 0.8f * u, minOf(y + alto, hasta) - 0.8f * u)
                if (r.height > 1f && (todo || r.left < w * BL + 2f || r.right > w * BR - 2f)) sillar(r, fila * 37 + j, oscuro, w)
                x += largo; j++
            }
            y += alto; fila++
        }
        pinceladas(Rect(0f, 0f, w, hasta), if (oscuro) NOCHE else CALIZA_OSC, 401, 46, u, if (oscuro) 0.05f else 0.08f)
        // La humedad que sube del suelo y, de día, la piedra que se apaga hacia la derecha, lejos de la ventana.
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombra(oscuro, 0.26f)), hasta - w * 0.16f, hasta), Offset(0f, hasta - w * 0.16f), Size(w, w * 0.16f))
        if (!oscuro) drawRect(Brush.horizontalGradient(listOf(Color.Transparent, sombraMueble(0.18f)), w * 0.3f, w), Offset(w * 0.3f, 0f), Size(w * 0.7f, hasta))
    }

    /** La cruz de consagración pintada en la piedra, de almagre, medio borrada. */
    private fun DrawScope.cruzConsagracion(c: Offset, r: Float, oscuro: Boolean, k: Float) {
        val u = density
        val pintura = n(ALMAGRE, oscuro, k)
        for (i in 0 until 6) {
            val a0 = i * 60f + 8f * azar(77, i)
            drawArc(pintura.copy(alpha = 0.45f + 0.2f * azar(78, i)), a0, 38f + 14f * azar(79, i), false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(2.6f * u, cap = StrokeCap.Round))
        }
        drawPath(cruzPatada(c, r * 0.68f), pintura.copy(alpha = 0.42f))
        // Lo que se llevó el tiempo: manchas de piedra limpia encima.
        for (i in 0 until 9) {
            val p = Offset(c.x + r * 1.1f * (azar(80, i) - 0.5f) * 2f, c.y + r * 1.1f * (azar(81, i) - 0.5f) * 2f)
            val q = r * (0.08f + 0.14f * azar(82, i))
            drawOval(n(mezcla(CALIZA, CALIZA_OSC, 0.25f), oscuro, k).copy(alpha = 0.7f), Offset(p.x - q, p.y - q * 0.7f), Size(2 * q, 1.4f * q))
        }
    }

    /** Dos lancetas de una ventana que no se ve, proyectadas en el muro y torcidas por la luz que baja oblicua. */
    private fun lancetas(w: Float, ensanche: Float = 0f): List<Path> = listOf(0.03f, 0.133f).map { x0f ->
        val x0 = w * x0f - ensanche; val an = w * 0.085f + 2 * ensanche
        val y0 = -w * 0.04f - ensanche; val alto = w * 0.44f + 2 * ensanche
        val corte = 0.42f
        fun p(x: Float, y: Float) = Offset(x + (y - y0) * corte, y)
        val fl = an * 0.95f
        val cx = x0 + an / 2
        Path().apply {
            val a = p(x0, y0 + alto); moveTo(a.x, a.y)
            val b = p(x0, y0 + fl); lineTo(b.x, b.y)
            val c1 = p(x0, y0 + fl * 0.45f); val c2 = p(cx - an * 0.22f, y0 + fl * 0.1f); val d = p(cx, y0)
            cubicTo(c1.x, c1.y, c2.x, c2.y, d.x, d.y)
            val c3 = p(cx + an * 0.22f, y0 + fl * 0.1f); val c4 = p(x0 + an, y0 + fl * 0.45f); val e = p(x0 + an, y0 + fl)
            cubicTo(c3.x, c3.y, c4.x, c4.y, e.x, e.y)
            val f = p(x0 + an, y0 + alto); lineTo(f.x, f.y); close()
        }
    }

    /** La luz de día que entra por la ventana de fuera del cuadro: crema, con un tinte muy leve de vidriera. */
    private fun DrawScope.luzDeVentana(w: Float) {
        val u = density
        lancetas(w, 3f * u).forEach { drawPath(it, luzMueble(0.08f)) }
        lancetas(w).forEachIndexed { i, l ->
            drawPath(l, luzMueble(0.24f))
            clipPath(l) {
                val x = w * (0.08f + 0.1f * i)
                drawOval(ULTRAMAR.copy(alpha = 0.09f), Offset(x - w * 0.02f + w * 0.06f * i, w * 0.04f), Size(w * 0.12f, w * 0.1f))
                drawOval(GRANA.copy(alpha = 0.07f), Offset(x + w * 0.02f, w * 0.14f), Size(w * 0.12f, w * 0.09f))
                drawOval(LATON.copy(alpha = 0.09f), Offset(x + w * 0.06f - w * 0.04f * i, w * 0.24f), Size(w * 0.12f, w * 0.1f))
            }
        }
    }

    /** El halo de las dos velas en la piedra: sube por el muro. */
    private fun DrawScope.haloDeVelas(w: Float) {
        val a = VELAS[0] * w; val b = VELAS[1] * w
        mancha(Offset((a.x + b.x) / 2, a.y - w * 0.03f), w * 0.62f, LLAMA.copy(alpha = 0.22f))
        mancha(a - Offset(0f, w * 0.04f), w * 0.28f, LLAMA.copy(alpha = 0.24f))
        mancha(b, w * 0.18f, LLAMA.copy(alpha = 0.16f))
    }

    // ---------------------------------------------------------------- lo colgado del muro

    /** Una cadena de eslabones de [a] a [b]. */
    private fun DrawScope.cadena(a: Offset, b: Offset, oscuro: Boolean, k: Float) {
        val u = density
        val d = b - a; val l = d.getDistance()
        val paso = 2.6f * u
        val m = (l / paso).toInt().coerceAtLeast(1)
        val ang = Math.toDegrees(atan2(d.y.toDouble(), d.x.toDouble())).toFloat()
        for (i in 0 until m) {
            val c = a + d * ((i + 0.5f) / m)
            rotate(ang, c) {
                if (i % 2 == 0) {
                    drawOval(n(LATON_OSC, oscuro, k), Offset(c.x - paso * 0.62f, c.y - 1.1f * u), Size(paso * 1.24f, 2.2f * u), style = Stroke(0.8f * u))
                    drawLine(brillo(oscuro, 0.5f, k + 0.3f), Offset(c.x - paso * 0.3f, c.y - 0.9f * u), Offset(c.x + paso * 0.2f, c.y - 0.9f * u), 0.5f * u)
                } else drawLine(n(LATON, oscuro, k), Offset(c.x - paso * 0.55f, c.y), Offset(c.x + paso * 0.55f, c.y), 1f * u, StrokeCap.Round)
            }
        }
    }

    /** El incensario: dónde cuelga y las piezas de su silueta. */
    private class Incensario(val gancho: Offset, val tapa: Rect, val vaso: Rect, val izq: Offset, val der: Offset, val cima: Offset)

    private fun incensario(w: Float) = Incensario(
        gancho = Offset(w * 0.455f, w * 0.05f),
        tapa = Rect(w * 0.437f, w * 0.255f, w * 0.473f, w * 0.298f),
        vaso = Rect(w * 0.427f, w * 0.296f, w * 0.483f, w * 0.336f),
        izq = Offset(w * 0.429f, w * 0.301f), der = Offset(w * 0.481f, w * 0.301f), cima = Offset(w * 0.455f, w * 0.255f),
    )

    private fun siluetaIncensario(i: Incensario, u: Float): Path = unir(listOf(
        Path().apply { tramo(i.gancho, i.izq, 1.4f * u) }, Path().apply { tramo(i.gancho, i.der, 1.4f * u) }, Path().apply { tramo(i.gancho, i.cima, 1.4f * u) },
        Path().apply { addRect(i.tapa) }, Path().apply { addOval(Rect(i.vaso.left, i.vaso.top - i.vaso.height, i.vaso.right, i.vaso.bottom)) },
    ))

    /** El incensario de latón colgado de su gancho por tres cadenas, con su tapa de torrecilla calada y el hollín encima. */
    private fun DrawScope.dibujaIncensario(i: Incensario, w: Float, oscuro: Boolean) {
        val u = density
        val k = if (oscuro) calor(i.vaso.center, w) else 0f
        // El hollín de tantas veces que colgó humeando.
        mancha(Offset(i.gancho.x + w * 0.01f, i.tapa.top - w * 0.07f), w * 0.07f, sombra(oscuro, 0.16f))
        // El gancho de hierro clavado en la junta.
        val g = i.gancho
        drawCircle(sombra(oscuro, 0.4f), 2.6f * u, g + Offset(1.2f * u, -w * 0.016f + 1.2f * u))
        drawCircle(n(HIERRO, oscuro, k), 2.4f * u, g + Offset(0f, -w * 0.016f))
        drawCircle(brillo(oscuro, 0.4f, k), 0.9f * u, g + Offset(-0.8f * u, -w * 0.017f))
        drawArc(n(HIERRO, oscuro, k), 200f, 230f, false, Offset(g.x - 3f * u, g.y - w * 0.016f), Size(6f * u, w * 0.017f + 2f * u), style = Stroke(1.6f * u))
        cadena(g, i.izq, oscuro, k); cadena(g, i.der, oscuro, k); cadena(g, i.cima, oscuro, k)
        val metal = Brush.horizontalGradient(listOf(n(LATON_LUZ, oscuro, k), n(LATON, oscuro, k), n(LATON_OSC, oscuro, k)), i.vaso.left, i.vaso.right)
        // El vaso, con su pie.
        val v = i.vaso
        val pie = Rect(v.center.x - w * 0.01f, v.bottom - 1f * u, v.center.x + w * 0.01f, v.bottom + w * 0.008f)
        drawRect(n(LATON_OSC, oscuro, k), pie.topLeft, pie.size)
        drawOval(n(LATON, oscuro, k), Offset(pie.left - 2f * u, pie.bottom - 2f * u), Size(pie.width + 4f * u, 3f * u))
        drawArc(metal, 0f, 180f, true, Offset(v.left, v.top - v.height), Size(v.width, v.height * 2))
        drawArc(brillo(oscuro, 0.45f, k + 0.3f), 120f, 40f, false, Offset(v.left + 2f * u, v.top - v.height + 2f * u), Size(v.width - 4f * u, v.height * 2 - 4f * u), style = Stroke(1.2f * u))
        // La tapa: una torrecilla de seis caras con sus ventanillas apuntadas caladas y su tejadillo.
        val t = i.tapa
        drawOval(n(LATON, oscuro, k), Offset(v.left - 1f * u, v.top - 2f * u), Size(v.width + 2f * u, 4f * u))
        val cuerpo = Rect(t.left, t.top + t.height * 0.38f, t.right, v.top)
        drawRect(metal, cuerpo.topLeft, cuerpo.size)
        for (j in 0 until 3) {
            val vw = cuerpo.width * 0.17f
            val r = Rect(cuerpo.left + cuerpo.width * (0.13f + 0.3f * j), cuerpo.top + cuerpo.height * 0.2f, cuerpo.left + cuerpo.width * (0.13f + 0.3f * j) + vw, cuerpo.bottom - cuerpo.height * 0.15f)
            drawPath(arcoApuntado(r, vw * 0.9f), n(Color(0xFF241A10), oscuro, k * 0.5f))
        }
        drawPath(poligono(t.left - 1f * u, cuerpo.top, t.center.x, t.top + 1f * u, t.right + 1f * u, cuerpo.top), metal)
        drawLine(brillo(oscuro, 0.5f, k + 0.3f), Offset(t.left, cuerpo.top), Offset(t.center.x, t.top + 1.5f * u), 0.8f * u)
        drawCircle(n(LATON, oscuro, k), 1.6f * u, Offset(t.center.x, t.top))
        listOf(i.izq, i.der).forEach { drawCircle(n(LATON_OSC, oscuro, k), 1.5f * u, it, style = Stroke(0.9f * u)) }
        if (oscuro) drawArc(LLAMA.copy(alpha = 0.35f * k), -60f, 80f, false, Offset(v.left, v.top - v.height), Size(v.width, v.height * 2), style = Stroke(1.2f * u))
    }

    /** Las llaves: el clavo y, para cada una, su giro y su largo. */
    private val LLAVES = listOf(4f to 0.1f, -16f to 0.084f, 22f to 0.074f)

    private fun siluetaLlaves(w: Float, u: Float): Path {
        val aro = Offset(w * 0.555f, w * 0.114f)
        val colg = aro + Offset(0f, w * 0.016f)
        return unir(listOf(Path().apply { addOval(Rect(aro, w * 0.017f)) }) + LLAVES.map { (g, l) ->
            val a = Math.toRadians(g.toDouble())
            val fin = colg + Offset((-sin(a) * l * w).toFloat(), (cos(a) * l * w).toFloat())
            Path().apply { tramo(colg, fin, w * 0.012f) }
        })
    }

    /** El manojo de llaves grandes de hierro, en su aro, colgado de un clavo. */
    private fun DrawScope.llaves(w: Float, oscuro: Boolean) {
        val u = density
        val aro = Offset(w * 0.555f, w * 0.114f)
        val k = if (oscuro) calor(aro + Offset(0f, w * 0.05f), w) else 0f
        val hierro = n(HIERRO, oscuro, k)
        // El clavo.
        val clavo = Offset(aro.x, aro.y - w * 0.016f)
        drawCircle(sombra(oscuro, 0.4f), 2.2f * u, clavo + Offset(1f * u, 1f * u))
        drawCircle(hierro, 2f * u, clavo)
        drawCircle(brillo(oscuro, 0.45f, k), 0.8f * u, clavo - Offset(0.6f * u, 0.6f * u))
        val colg = aro + Offset(0f, w * 0.016f)
        LLAVES.forEachIndexed { j, (g, l) ->
            rotate(g, colg) {
                val largo = l * w
                // El ojo: un anillo con una lazada de hierro.
                val ojo = Offset(colg.x, colg.y + w * 0.012f)
                drawCircle(hierro, w * 0.0105f, ojo, style = Stroke(1.8f * u))
                drawArc(brillo(oscuro, 0.4f, k), 160f, 90f, false, Offset(ojo.x - w * 0.0105f, ojo.y - w * 0.0105f), Size(w * 0.021f, w * 0.021f), style = Stroke(0.7f * u))
                // La caña con su collar.
                val c0 = ojo.y + w * 0.0105f
                drawRect(hierro, Offset(colg.x - w * 0.0024f, c0), Size(w * 0.0048f, largo - (c0 - colg.y)))
                drawLine(brillo(oscuro, 0.3f, k), Offset(colg.x - w * 0.0018f, c0), Offset(colg.x - w * 0.0018f, colg.y + largo), 0.6f * u)
                drawRect(hierro, Offset(colg.x - w * 0.0045f, c0 + w * 0.004f), Size(w * 0.009f, w * 0.004f))
                // El paletón, con sus dientes.
                val p = Rect(colg.x, colg.y + largo - w * (0.018f + 0.004f * j), colg.x + w * 0.013f, colg.y + largo - w * 0.002f)
                drawRect(hierro, p.topLeft, p.size)
                drawRect(n(CALIZA_OSC, oscuro, k), Offset(p.left + p.width * 0.45f, p.top + p.height * 0.25f), Size(p.width * 0.55f, p.height * 0.18f))
                drawRect(n(CALIZA_OSC, oscuro, k), Offset(p.left + p.width * 0.6f, p.top + p.height * 0.62f), Size(p.width * 0.4f, p.height * 0.16f))
            }
        }
        // El aro, por delante.
        drawCircle(hierro, w * 0.017f, aro, style = Stroke(1.7f * u))
        drawArc(brillo(oscuro, 0.45f, k + 0.2f), 180f, 80f, false, Offset(aro.x - w * 0.017f, aro.y - w * 0.017f), Size(w * 0.034f, w * 0.034f), style = Stroke(0.7f * u))
    }

    // ---------------------------------------------------------------- la cruz procesional, en el rincón

    /** El punto del asta de la cruz a la altura [y] (las dos en píxeles). */
    private fun asta(y: Float, w: Float) = Offset(w * 0.86f + (y / (TAB * w)) * w * 0.04f, y)

    private val GIRO_CRUZ = -Math.toDegrees(atan2(0.04, 0.44)).toFloat()

    private fun siluetaCruz(w: Float, alto: Float): Path {
        val c = asta(w * 0.045f, w)
        val partes = listOf(
            Path().apply { tramo(asta(-w * 0.1f, w), asta(alto, w), w * 0.012f) },
            Path().apply { addRect(Rect(c.x - w * 0.09f, c.y - w * 0.011f, c.x + w * 0.09f, c.y + w * 0.011f)) },
            Path().apply { addOval(Rect(asta(w * 0.13f, w), w * 0.02f)) },
        )
        return unir(partes)
    }

    /** La cruz procesional de latón dorado, apoyada en el rincón: el asta de roble, el nudo y la cruz con sus remates de trébol. */
    private fun DrawScope.cruzProcesional(w: Float, alto: Float, oscuro: Boolean) {
        val u = density
        val c = asta(w * 0.045f, w)
        val k = if (oscuro) calor(c + Offset(0f, w * 0.08f), w) else 0f
        // El asta, en diagonal hasta salir por abajo.
        val a = asta(-w * 0.05f, w); val b = asta(alto, w)
        drawLine(n(ROBLE, oscuro, k * 0.6f), a, b, w * 0.013f)
        drawLine(brillo(oscuro, 0.25f, k), a - Offset(w * 0.003f, 0f), b - Offset(w * 0.003f, 0f), 0.8f * u)
        drawLine(sombra(oscuro, 0.3f), a + Offset(w * 0.004f, 0f), b + Offset(w * 0.004f, 0f), 1f * u)
        rotate(GIRO_CRUZ, c) {
            val oro = n(ORO, oscuro, k)
            val luz = brillo(oscuro, 0.55f, k + 0.35f)
            val osc = n(LATON_OSC, oscuro, k)
            // El cubo y el nudo, con sus ventanillas.
            val nudo = Offset(c.x, c.y + w * 0.112f)
            drawRect(oro, Offset(c.x - w * 0.008f, c.y + w * 0.07f), Size(w * 0.016f, w * 0.07f))
            drawOval(oro, Offset(nudo.x - w * 0.022f, nudo.y - w * 0.015f), Size(w * 0.044f, w * 0.03f))
            drawOval(osc, Offset(nudo.x - w * 0.022f, nudo.y - w * 0.015f), Size(w * 0.044f, w * 0.03f), style = Stroke(0.8f * u))
            for (j in -1..1) drawPath(arcoApuntado(Rect(nudo.x + j * w * 0.011f - w * 0.0035f, nudo.y - w * 0.007f, nudo.x + j * w * 0.011f + w * 0.0035f, nudo.y + w * 0.008f), w * 0.006f), osc)
            drawArc(luz, 150f, 70f, false, Offset(nudo.x - w * 0.021f, nudo.y - w * 0.014f), Size(w * 0.042f, w * 0.028f), style = Stroke(1f * u))
            // La cruz: los brazos, con su filo de luz arriba y a la izquierda, y los remates de trébol.
            val br = w * 0.0105f
            val vert = Rect(c.x - br, c.y - w * 0.16f, c.x + br, c.y + w * 0.066f)
            val hor = Rect(c.x - w * 0.078f, c.y - br, c.x + w * 0.078f, c.y + br)
            listOf(Offset(c.x - w * 0.086f, c.y), Offset(c.x + w * 0.086f, c.y), Offset(c.x, c.y + w * 0.074f)).forEach { p ->
                translate(1.2f * u, 1.2f * u) { drawPath(trebolPath(p, w * 0.02f), osc) }
                drawPath(trebolPath(p, w * 0.02f), oro)
                drawCircle(luz, w * 0.004f, p - Offset(w * 0.004f, w * 0.004f))
            }
            listOf(vert, hor).forEach { r -> drawRect(osc, r.topLeft + Offset(1.2f * u, 1.2f * u), r.size); drawRect(oro, r.topLeft, r.size) }
            // La labra de los brazos: un filete hundido por el medio.
            drawLine(osc, Offset(c.x, vert.top), Offset(c.x, vert.bottom - w * 0.006f), 0.8f * u)
            drawLine(osc, Offset(hor.left + w * 0.006f, c.y), Offset(hor.right - w * 0.006f, c.y), 0.8f * u)
            drawLine(luz, Offset(vert.left + 0.6f * u, vert.top), Offset(vert.left + 0.6f * u, vert.bottom), 1f * u)
            drawLine(luz, Offset(hor.left, hor.top + 0.6f * u), Offset(hor.right, hor.top + 0.6f * u), 1f * u)
            // El medallón del cruce, con su esmalte azul.
            translate(1.2f * u, 1.2f * u) { drawPath(cuadrifolioPath(c, w * 0.03f), osc) }
            drawPath(cuadrifolioPath(c, w * 0.03f), oro)
            drawCircle(n(ULTRAMAR, oscuro, k), w * 0.009f, c)
            drawCircle(luz, w * 0.0035f, c - Offset(w * 0.003f, w * 0.003f))
        }
    }

    // ---------------------------------------------------------------- sobre el tablero

    private fun siluetaAtril(w: Float): Path = poligono(
        w * 0.075f, w * 0.449f, w * 0.075f, w * 0.404f, w * 0.1f, w * 0.29f, w * 0.215f, w * 0.296f, w * 0.33f, w * 0.29f,
        w * 0.355f, w * 0.404f, w * 0.355f, w * 0.449f)

    /** Una página del misal: [g] es la x del lomo y [s] el lado (-1 la izquierda, 1 la derecha). */
    private fun pagina(w: Float, s: Float): Path {
        val g = w * 0.215f
        fun x(f: Float) = g + s * w * f
        return Path().apply {
            moveTo(g, w * 0.309f)
            cubicTo(x(0.03f), w * 0.296f, x(0.08f), w * 0.294f, x(0.104f), w * 0.303f)
            lineTo(x(0.116f), w * 0.398f)
            cubicTo(x(0.08f), w * 0.393f, x(0.03f), w * 0.394f, g, w * 0.404f)
            close()
        }
    }

    /** El atril bajo de roble con el misal abierto: dos columnas, la inicial azul, las rúbricas rojas y tres cintas de registro. */
    private fun DrawScope.atrilConMisal(w: Float, oscuro: Boolean) {
        val u = density
        val k = if (oscuro) calor(Offset(w * 0.215f, w * 0.37f), w) else 0f
        fun c(col: Color, extra: Float = 0f) = n(col, oscuro, k + extra)
        sombraContacto(Offset(w * 0.225f, w * 0.451f), w * 0.3f, 4f * u, if (oscuro) 0.5f else 0.36f)
        // La caja del pie, con tres arquillos ciegos.
        val caja = Rect(w * 0.088f, w * 0.416f, w * 0.342f, w * 0.449f)
        drawRect(c(ROBLE), caja.topLeft, caja.size)
        vetas(caja, ROBLE_OSC.copy(alpha = 0.4f), 515, 0.6f, grosor = 0.6f, ondulacion = 0.3f)
        for (j in 0 until 5) {
            val aw = caja.width / 5
            val r = Rect(caja.left + aw * j + aw * 0.22f, caja.top + caja.height * 0.3f, caja.left + aw * (j + 1) - aw * 0.22f, caja.bottom - caja.height * 0.18f)
            hundido(arcoApuntado(r, r.width * 0.8f), sombra(oscuro, 0.6f), c(ROBLE_OSC, 0.05f), 1.2f * u, oscuro, k)
        }
        drawRect(brillo(oscuro, 0.3f, k), caja.topLeft, Size(caja.width, 1f * u))
        drawPath(poligono(caja.right, caja.top, caja.right + w * 0.01f, caja.top - w * 0.004f, caja.right + w * 0.01f, caja.bottom - w * 0.002f, caja.right, caja.bottom), c(ROBLE_OSC))
        // El tablero inclinado del atril, con su canto en sombra a la derecha.
        drawPath(poligono(w * 0.08f, w * 0.417f, w * 0.1f, w * 0.29f, w * 0.33f, w * 0.29f, w * 0.35f, w * 0.417f), c(mezcla(ROBLE, ROBLE_OSC, 0.2f)))
        drawPath(poligono(w * 0.33f, w * 0.29f, w * 0.338f, w * 0.293f, w * 0.358f, w * 0.417f, w * 0.35f, w * 0.417f), c(ROBLE_OSC))
        drawLine(brillo(oscuro, 0.35f, k), Offset(w * 0.08f, w * 0.417f), Offset(w * 0.1f, w * 0.29f), 1f * u)
        // Las tapas del libro, de cuero sobre tabla, asomando alrededor de las hojas.
        val cuero = mezcla(GRANA, ROBLE_OSC, 0.55f)
        drawPath(poligono(w * 0.093f, w * 0.406f, w * 0.106f, w * 0.297f, w * 0.215f, w * 0.302f, w * 0.324f, w * 0.297f, w * 0.337f, w * 0.406f, w * 0.215f, w * 0.412f), c(cuero))
        // El canto de las hojas, por fuera.
        for (j in 0 until 3) {
            val d = (1.2f + 1.1f * j) * u
            drawLine(c(if (j % 2 == 0) VITELA_OSC else mezcla(VITELA_OSC, ROBLE, 0.3f)), Offset(w * 0.111f - d * 0.3f, w * 0.304f + d * 0.4f), Offset(w * 0.099f - d * 0.3f, w * 0.398f), 1f * u)
            drawLine(c(if (j % 2 == 0) VITELA_OSC else mezcla(VITELA_OSC, ROBLE, 0.3f)), Offset(w * 0.319f + d * 0.3f, w * 0.304f + d * 0.4f), Offset(w * 0.331f + d * 0.3f, w * 0.398f), 1f * u)
        }
        // Las dos páginas: la izquierda se oscurece hacia el lomo; la derecha coge la luz junto a él.
        val izq = pagina(w, -1f); val der = pagina(w, 1f)
        drawPath(izq, Brush.horizontalGradient(listOf(c(VITELA, 0.05f), c(mezcla(VITELA, VITELA_OSC, 0.4f)), c(mezcla(VITELA_OSC, SOMBRA_MUEBLE, 0.2f))), w * 0.1f, w * 0.215f))
        drawPath(der, Brush.horizontalGradient(listOf(c(mezcla(VITELA, LUZ_MUEBLE, 0.3f), 0.1f), c(VITELA, 0.1f), c(mezcla(VITELA, VITELA_OSC, 0.6f), 0.1f)), w * 0.215f, w * 0.33f))
        // El texto: dos columnas por página, con rúbricas rojas, la inicial azul con su filigrana y otra roja.
        val tinta = c(Color(0xFF4A3426)).copy(alpha = 0.6f)
        val rubrica = c(GRANA).copy(alpha = 0.8f)
        listOf(0.118f to 0.157f, 0.165f to 0.205f, 0.226f to 0.265f, 0.273f to 0.312f).forEachIndexed { col, (a, b) ->
            var y = w * 0.317f; var l = 0
            while (y < w * 0.392f) {
                // Cerca del lomo la hoja se curva hacia abajo: las líneas bajan un poco.
                val curva = if (col == 1 || col == 2) w * 0.002f else 0f
                val corta = (col == 0 && l < 3) || azar(520 + col, l) > 0.86f
                val x0 = w * a + (if (col == 0 && l < 3) w * 0.017f else 0f)
                val x1 = if (corta && !(col == 0 && l < 3)) w * (a + (b - a) * 0.55f) else w * b
                drawLine(if (azar(530 + col, l) > 0.84f) rubrica else tinta, Offset(x0, y + curva), Offset(x1, y + curva), 0.9f * u)
                y += w * 0.0056f; l++
            }
        }
        val ini = Rect(w * 0.118f, w * 0.314f, w * 0.133f, w * 0.329f)
        drawRect(c(ULTRAMAR, 0.05f), ini.topLeft, ini.size)
        drawRect(c(ORO, 0.1f), ini.topLeft, ini.size, style = Stroke(0.6f * u))
        drawLine(rubrica, Offset(ini.left - w * 0.004f, ini.top), Offset(ini.left - w * 0.004f, w * 0.37f), 0.6f * u)
        drawRect(rubrica, Offset(w * 0.273f, w * 0.35f), Size(w * 0.01f, w * 0.01f))
        // El lomo, hundido en sombra.
        drawLine(sombra(oscuro, 0.4f), Offset(w * 0.215f, w * 0.31f), Offset(w * 0.215f, w * 0.404f), 2.2f * u)
        drawLine(brillo(oscuro, 0.4f, k), Offset(w * 0.218f, w * 0.312f), Offset(w * 0.218f, w * 0.402f), 0.6f * u)
        // El reborde del atril donde descansa el libro.
        val reborde = Rect(w * 0.077f, w * 0.404f, w * 0.353f, w * 0.417f)
        drawRect(c(ROBLE), reborde.topLeft, reborde.size)
        drawRect(brillo(oscuro, 0.4f, k), reborde.topLeft, Size(reborde.width, 1f * u))
        drawRect(sombra(oscuro, 0.4f), Offset(reborde.left, reborde.bottom - 1.2f * u), Size(reborde.width, 1.2f * u))
        // El broche: una correa de cuero con su cierre de latón, suelta del canto derecho.
        drawLine(c(cuero), Offset(w * 0.335f, w * 0.35f), Offset(w * 0.343f, w * 0.393f), 2.4f * u, StrokeCap.Round)
        drawRect(c(LATON, 0.1f), Offset(w * 0.339f, w * 0.389f), Size(w * 0.009f, w * 0.008f))
        drawLine(brillo(oscuro, 0.5f, k), Offset(w * 0.339f, w * 0.389f), Offset(w * 0.348f, w * 0.389f), 0.6f * u)
        // Las tres cintas de registro: salen del lomo, pasan por el reborde y caen por el canto del tablero.
        listOf(Triple(GRANA, 0.211f, 0.523f), Triple(ULTRAMAR, 0.216f, 0.548f), Triple(ORO, 0.221f, 0.508f)).forEachIndexed { j, (col, x, fin) ->
            val x0 = w * x; val y0 = w * 0.402f
            val dx = w * (0.004f * (j - 1))
            val p = Path().apply {
                moveTo(x0, y0)
                cubicTo(x0, w * 0.41f, x0 + dx, w * 0.415f, x0 + dx, w * 0.42f)
                lineTo(x0 + dx, w * 0.449f)
                cubicTo(x0 + dx, w * 0.454f, x0 + dx * 1.5f, w * 0.456f, x0 + dx * 1.5f, w * 0.462f)
                lineTo(x0 + dx * 1.3f + w * 0.002f * j, fin * w)
            }
            translate(w * 0.006f, w * 0.002f) { drawPath(p, sombra(oscuro, 0.3f), style = Stroke(w * 0.0062f)) }
            drawPath(p, c(col, 0.05f), style = Stroke(w * 0.0062f))
            // La punta cortada en cola de golondrina.
            val pf = Offset(x0 + dx * 1.3f + w * 0.002f * j, fin * w)
            drawPath(poligono(pf.x - w * 0.0031f, pf.y, pf.x + w * 0.0031f, pf.y, pf.x + w * 0.0031f, pf.y + w * 0.006f, pf.x, pf.y + w * 0.002f, pf.x - w * 0.0031f, pf.y + w * 0.006f), c(col, 0.05f))
            drawPath(p, brillo(oscuro, 0.3f, k), style = Stroke(0.6f * u))
        }
    }

    /** La estola grana, doblada en el tablero; sus dos caídas tapan parte de la crestería. */
    private fun DrawScope.estola(w: Float, oscuro: Boolean) {
        val u = density
        val k = if (oscuro) calor(Offset(w * 0.41f, w * 0.48f), w) else 0f
        val tela = n(GRANA, oscuro, k)
        val oro = n(ORO, oscuro, k)
        // Lo que descansa en el tablero, doblado.
        val arriba = Rect(w * 0.356f, w * 0.441f, w * 0.466f, w * 0.456f)
        drawRect(sombra(oscuro, 0.3f), Offset(arriba.left + 1.5f * u, arriba.top + 1.5f * u), arriba.size)
        drawRect(mezcla(tela, LUZ_MUEBLE, if (oscuro) 0f else 0.1f), arriba.topLeft, arriba.size)
        drawLine(oro, Offset(arriba.left, arriba.top + 1.4f * u), Offset(arriba.right, arriba.top + 1.4f * u), 0.7f * u)
        drawLine(sombra(oscuro, 0.35f), Offset(w * 0.41f, arriba.top), Offset(w * 0.413f, arriba.bottom), 0.8f * u)
        // Las dos caídas, que se ensanchan hacia la punta, con su fleco de oro y la cruz bordada.
        listOf(Triple(0.369f, 0.404f, 0.572f), Triple(0.408f, 0.448f, 0.538f)).forEachIndexed { j, (a, b, fin) ->
            val ensancha = w * 0.006f
            val p = Path().apply {
                moveTo(w * a, w * TAB_F); lineTo(w * b, w * TAB_F)
                cubicTo(w * b + 1f * u, w * (TAB_F + 0.02f), w * b + ensancha * 0.5f, w * (fin - 0.04f), w * b + ensancha, w * fin)
                lineTo(w * a - ensancha, w * fin)
                cubicTo(w * a - ensancha * 0.5f, w * (fin - 0.04f), w * a - 1f * u, w * (TAB_F + 0.02f), w * a, w * TAB_F)
                close()
            }
            translate(w * 0.012f, w * 0.004f) { drawPath(p, sombra(oscuro, 0.35f)) }
            drawPath(p, tela)
            clipPath(p) {
                // El doblez sobre el canto, a la luz; debajo, la sombra del pliegue.
                drawRect(brillo(oscuro, 0.3f, k), Offset(w * a - 2f * u, w * TAB_F), Size(w * (b - a) + 4f * u, w * 0.006f))
                drawRect(sombra(oscuro, 0.22f), Offset(w * a - 2f * u, w * (TAB_F + 0.012f)), Size(w * (b - a) + 4f * u, w * 0.012f))
                drawRect(sombra(oscuro, 0.18f), Offset(w * (a + (b - a) * 0.62f), w * TAB_F), Size(w * (b - a) * 0.5f, w * (fin - TAB_F)))
                drawLine(oro, Offset(w * a + 1.6f * u, w * TAB_F), Offset(w * a + 1.6f * u - ensancha, w * fin), 0.7f * u)
                drawLine(oro, Offset(w * b - 1.6f * u, w * TAB_F), Offset(w * b - 1.6f * u + ensancha, w * fin), 0.7f * u)
            }
            val cx = w * (a + b) / 2
            drawPath(cruzPatada(Offset(cx, w * (fin - 0.017f)), w * 0.0085f), oro)
            for (q in 0 until 9) {
                val x = w * a - ensancha + (w * (b - a) + 2 * ensancha) * (q + 0.5f) / 9f
                drawLine(oro, Offset(x, w * fin), Offset(x + 0.3f * u, w * fin + (2.4f + 1.2f * azar(70 + j, q)) * u), 0.7f * u, StrokeCap.Round)
            }
        }
    }

    private fun siluetaCaliz(w: Float): Path = unir(listOf(
        poligono(w * 0.564f, w * 0.338f, w * 0.625f, w * 0.338f, w * 0.646f, w * 0.402f, w * 0.542f, w * 0.402f),
        Path().apply { addRect(Rect(w * 0.588f, w * 0.393f, w * 0.6f, w * 0.43f)) },
        Path().apply { addOval(Rect(w * 0.563f, w * 0.424f, w * 0.625f, w * 0.446f)) },
    ))

    /** El cáliz de plata dorada con su patena, tapado con el paño de lino: se ven el pie, el nudo y el borde de la patena. */
    private fun DrawScope.caliz(w: Float, oscuro: Boolean) {
        val u = density
        val cx = w * 0.594f
        val k = if (oscuro) calor(Offset(cx, w * 0.39f), w) else 0f
        val oro = Brush.horizontalGradient(listOf(n(LATON_LUZ, oscuro, k), n(ORO, oscuro, k), n(LATON_OSC, oscuro, k)), cx - w * 0.032f, cx + w * 0.032f)
        sombraContacto(Offset(cx + 2f * u, w * 0.446f), w * 0.07f, 3f * u, if (oscuro) 0.5f else 0.35f)
        // El pie: ancho y bajo, con sus lóbulos, y el tallo con su nudo de esmaltes.
        drawPath(Path().apply {
            moveTo(cx - w * 0.031f, w * 0.444f)
            cubicTo(cx - w * 0.026f, w * 0.434f, cx - w * 0.008f, w * 0.43f, cx - w * 0.006f, w * 0.42f)
            lineTo(cx + w * 0.006f, w * 0.42f)
            cubicTo(cx + w * 0.008f, w * 0.43f, cx + w * 0.026f, w * 0.434f, cx + w * 0.031f, w * 0.444f)
            close()
        }, oro)
        drawOval(oro, Offset(cx - w * 0.031f, w * 0.44f), Size(w * 0.062f, w * 0.008f))
        for (j in -1..1) drawLine(n(LATON_OSC, oscuro, k).copy(alpha = 0.6f), Offset(cx + j * w * 0.013f, w * 0.441f), Offset(cx + j * w * 0.004f, w * 0.426f), 0.6f * u)
        drawRect(oro, Offset(cx - w * 0.005f, w * 0.395f), Size(w * 0.01f, w * 0.027f))
        drawOval(oro, Offset(cx - w * 0.013f, w * 0.405f), Size(w * 0.026f, w * 0.012f))
        listOf(-0.006f, 0.001f, 0.008f).forEach { dx -> drawRect(n(ULTRAMAR, oscuro, k), Offset(cx + w * dx - w * 0.0018f, w * 0.409f), Size(w * 0.0036f, w * 0.004f)) }
        drawLine(brillo(oscuro, 0.6f, k + 0.3f), Offset(cx - w * 0.011f, w * 0.408f), Offset(cx - w * 0.004f, w * 0.406f), 0.8f * u)
        // El paño de lino, que cae sobre la copa y la patena, con sus pliegues y una cruz bordada.
        // Un paño cuadrado sobre una copa redonda: arriba plano por la patena, y cuelga en tres picos (las esquinas).
        val pano = Path().apply {
            moveTo(cx - w * 0.032f, w * 0.34f)
            cubicTo(cx - w * 0.026f, w * 0.333f, cx + w * 0.026f, w * 0.333f, cx + w * 0.032f, w * 0.34f)
            cubicTo(cx + w * 0.034f, w * 0.36f, cx + w * 0.033f, w * 0.38f, cx + w * 0.04f, w * 0.401f)
            cubicTo(cx + w * 0.03f, w * 0.394f, cx + w * 0.022f, w * 0.389f, cx + w * 0.014f, w * 0.39f)
            cubicTo(cx + w * 0.008f, w * 0.395f, cx + w * 0.003f, w * 0.403f, cx - w * 0.001f, w * 0.409f)
            cubicTo(cx - w * 0.006f, w * 0.4f, cx - w * 0.012f, w * 0.391f, cx - w * 0.02f, w * 0.389f)
            cubicTo(cx - w * 0.027f, w * 0.39f, cx - w * 0.033f, w * 0.395f, cx - w * 0.039f, w * 0.4f)
            cubicTo(cx - w * 0.033f, w * 0.38f, cx - w * 0.034f, w * 0.36f, cx - w * 0.032f, w * 0.34f)
            close()
        }
        val lino = mezcla(VITELA, LUZ_MUEBLE, 0.4f)
        drawPath(pano, Brush.horizontalGradient(listOf(n(lino, oscuro, k + 0.1f), n(lino, oscuro, k), n(mezcla(lino, SOMBRA_MUEBLE, 0.3f), oscuro, k)), cx - w * 0.04f, cx + w * 0.04f))
        clipPath(pano) {
            // El borde de la patena que se marca bajo el paño.
            drawLine(sombra(oscuro, 0.22f), Offset(cx - w * 0.03f, w * 0.346f), Offset(cx + w * 0.03f, w * 0.346f), 1f * u)
            drawLine(brillo(oscuro, 0.5f, k), Offset(cx - w * 0.03f, w * 0.341f), Offset(cx + w * 0.02f, w * 0.339f), 1f * u)
            // Los pliegues blandos que bajan a los picos.
            drawPath(Path().apply { moveTo(cx + w * 0.012f, w * 0.348f); cubicTo(cx + w * 0.02f, w * 0.37f, cx + w * 0.026f, w * 0.385f, cx + w * 0.038f, w * 0.4f) }, sombra(oscuro, 0.2f), style = Stroke(1.6f * u))
            drawPath(Path().apply { moveTo(cx - w * 0.014f, w * 0.35f); cubicTo(cx - w * 0.02f, w * 0.37f, cx - w * 0.028f, w * 0.385f, cx - w * 0.037f, w * 0.399f) }, brillo(oscuro, 0.4f, k), style = Stroke(1.2f * u))
            drawPath(Path().apply { moveTo(cx + w * 0.002f, w * 0.352f); cubicTo(cx + w * 0.0f, w * 0.375f, cx - w * 0.001f, w * 0.395f, cx - w * 0.001f, w * 0.407f) }, sombra(oscuro, 0.12f), style = Stroke(1.2f * u))
        }
        // El dobladillo, con su filete de oro.
        drawPath(pano, n(ORO, oscuro, k).copy(alpha = 0.55f), style = Stroke(0.7f * u))
        val cc = Offset(cx - w * 0.002f, w * 0.362f)
        drawLine(n(GRANA, oscuro, k), cc - Offset(0f, w * 0.008f), cc + Offset(0f, w * 0.008f), 1.3f * u)
        drawLine(n(GRANA, oscuro, k), cc - Offset(w * 0.006f, w * 0.002f), cc + Offset(w * 0.006f, -w * 0.002f), 1.3f * u)
        if (oscuro) drawLine(LLAMA.copy(alpha = 0.5f * k), Offset(cx + w * 0.032f, w * 0.345f), Offset(cx + w * 0.048f, w * 0.396f), 1f * u)
    }

    /** Un candelero de latón: dónde pisa, su ancho, dónde está el platillo y la vela que le queda. */
    private class Candelero(val cx: Float, val pie: Float, val ancho: Float, val plato: Float, val vela: Float, val gruesa: Float)

    private fun candeleros(w: Float) = listOf(
        Candelero(w * 0.655f, w * 0.452f, w * 0.07f, w * 0.31f, w * 0.105f, w * 0.022f),
        Candelero(w * 0.765f, w * 0.444f, w * 0.058f, w * 0.365f, w * 0.027f, w * 0.02f),
    )

    private fun siluetaCandelero(c: Candelero): Path = unir(listOf(
        Path().apply { addOval(Rect(c.cx - c.ancho / 2, c.pie - c.ancho * 0.36f, c.cx + c.ancho / 2, c.pie + c.ancho * 0.04f)) },
        Path().apply { addRect(Rect(c.cx - c.ancho * 0.09f, c.plato, c.cx + c.ancho * 0.09f, c.pie - c.ancho * 0.2f)) },
        Path().apply { addOval(Rect(c.cx - c.ancho * 0.42f, c.plato - c.ancho * 0.07f, c.cx + c.ancho * 0.42f, c.plato + c.ancho * 0.12f)) },
        Path().apply { addRect(Rect(c.cx - c.gruesa / 2, c.plato - c.vela, c.cx + c.gruesa / 2, c.plato)) },
    ))

    /** El candelero de pincho, de latón: el pie abombado, el tallo con sus nudos, el platillo con la cera derramada y la vela. */
    private fun DrawScope.candelero(c: Candelero, w: Float, oscuro: Boolean, consumida: Boolean) {
        val u = density
        val k = if (oscuro) calor(Offset(c.cx, c.plato), w) else 0f
        val a = c.ancho
        val metal = Brush.horizontalGradient(listOf(n(LATON_LUZ, oscuro, k), n(LATON, oscuro, k), n(LATON_OSC, oscuro, k * 0.6f)), c.cx - a / 2, c.cx + a / 2)
        val luz = brillo(oscuro, 0.7f, k + 0.3f)
        sombraContacto(Offset(c.cx + 2f * u, c.pie), a * 1.15f, 3.4f * u, if (oscuro) 0.5f else 0.38f)
        // El pie abombado, con su moldura.
        drawPath(Path().apply {
            moveTo(c.cx - a / 2, c.pie)
            cubicTo(c.cx - a * 0.48f, c.pie - a * 0.22f, c.cx - a * 0.16f, c.pie - a * 0.22f, c.cx - a * 0.1f, c.pie - a * 0.36f)
            lineTo(c.cx + a * 0.1f, c.pie - a * 0.36f)
            cubicTo(c.cx + a * 0.16f, c.pie - a * 0.22f, c.cx + a * 0.48f, c.pie - a * 0.22f, c.cx + a / 2, c.pie)
            close()
        }, metal)
        drawOval(metal, Offset(c.cx - a / 2, c.pie - a * 0.05f), Size(a, a * 0.09f))
        drawArc(luz, 190f, 60f, false, Offset(c.cx - a * 0.44f, c.pie - a * 0.32f), Size(a * 0.88f, a * 0.6f), style = Stroke(0.9f * u))
        // El tallo y sus nudos.
        val t0 = c.pie - a * 0.34f
        drawRect(metal, Offset(c.cx - a * 0.08f, c.plato), Size(a * 0.16f, t0 - c.plato))
        val nudos = if ((t0 - c.plato) > a * 1.2f) listOf(0.3f, 0.66f) else listOf(0.5f)
        nudos.forEach { f ->
            val y = c.plato + (t0 - c.plato) * f
            drawOval(metal, Offset(c.cx - a * 0.17f, y - a * 0.07f), Size(a * 0.34f, a * 0.14f))
            drawLine(luz, Offset(c.cx - a * 0.12f, y - a * 0.04f), Offset(c.cx - a * 0.03f, y - a * 0.06f), 0.8f * u)
        }
        drawLine(luz, Offset(c.cx - a * 0.05f, c.plato + 2f * u), Offset(c.cx - a * 0.05f, t0), 0.7f * u)
        // El platillo, con la cera que se derramó y cuelga por el borde.
        drawPath(Path().apply {
            moveTo(c.cx - a * 0.42f, c.plato)
            cubicTo(c.cx - a * 0.36f, c.plato + a * 0.12f, c.cx + a * 0.36f, c.plato + a * 0.12f, c.cx + a * 0.42f, c.plato)
            close()
        }, metal)
        drawOval(n(LATON_OSC, oscuro, k), Offset(c.cx - a * 0.42f, c.plato - a * 0.06f), Size(a * 0.84f, a * 0.12f))
        drawOval(n(LATON, oscuro, k), Offset(c.cx - a * 0.38f, c.plato - a * 0.045f), Size(a * 0.76f, a * 0.08f))
        val cera = n(CERA, oscuro, k + 0.2f)
        val charco = a * (if (consumida) 0.62f else 0.4f)
        drawOval(cera, Offset(c.cx - charco / 2, c.plato - a * 0.035f), Size(charco, a * 0.06f))
        val gota = c.cx + a * (if (consumida) 0.34f else -0.3f)
        drawPath(Path().apply {
            moveTo(gota - a * 0.04f, c.plato); lineTo(gota + a * 0.04f, c.plato)
            cubicTo(gota + a * 0.035f, c.plato + a * 0.1f, gota + a * 0.04f, c.plato + a * 0.18f, gota, c.plato + a * 0.2f)
            cubicTo(gota - a * 0.04f, c.plato + a * 0.18f, gota - a * 0.03f, c.plato + a * 0.1f, gota - a * 0.04f, c.plato)
            close()
        }, cera)
        drawArc(luz, 170f, 40f, false, Offset(c.cx - a * 0.42f, c.plato - a * 0.06f), Size(a * 0.84f, a * 0.12f), style = Stroke(0.9f * u))
        // La vela de cera, con sus chorreras; la consumida, un cabo con el borde derretido.
        val g = c.gruesa
        val vr = Rect(c.cx - g / 2, c.plato - c.vela, c.cx + g / 2, c.plato - a * 0.02f)
        drawRect(Brush.horizontalGradient(listOf(n(mezcla(CERA, LUZ_MUEBLE, 0.5f), oscuro, k + 0.3f), cera, n(mezcla(CERA, SOMBRA_MUEBLE, 0.3f), oscuro, k + 0.1f)), vr.left, vr.right), vr.topLeft, vr.size)
        val borde = Path().apply {
            moveTo(vr.left, vr.top + g * 0.1f)
            cubicTo(vr.left + g * 0.2f, vr.top - g * 0.12f, vr.right - g * 0.2f, vr.top - g * 0.12f, vr.right, vr.top + g * 0.1f)
            lineTo(vr.right, vr.top + g * 0.25f); lineTo(vr.left, vr.top + g * 0.25f); close()
        }
        drawPath(borde, n(mezcla(CERA, LUZ_MUEBLE, 0.3f), oscuro, k + 0.3f))
        drawOval(n(mezcla(CERA, SOMBRA_MUEBLE, 0.25f), oscuro, k), Offset(vr.left + g * 0.15f, vr.top - g * 0.02f), Size(g * 0.7f, g * 0.22f))
        val chorros = if (consumida) listOf(0.15f to 0.9f, 0.8f to 0.7f) else listOf(0.2f to 0.45f, 0.72f to 0.75f, 0.5f to 0.22f)
        chorros.forEach { (fx, fl) ->
            val x = vr.left + g * fx
            val l = (vr.height) * fl
            drawLine(n(mezcla(CERA, LUZ_MUEBLE, 0.4f), oscuro, k + 0.3f), Offset(x, vr.top + g * 0.1f), Offset(x + 0.3f * u, vr.top + l), g * 0.16f, StrokeCap.Round)
            drawCircle(n(mezcla(CERA, LUZ_MUEBLE, 0.4f), oscuro, k + 0.3f), g * 0.11f, Offset(x + 0.3f * u, vr.top + l))
        }
        // La mecha, doblada y negra.
        drawLine(Color(0xFF1C1612), Offset(c.cx, vr.top + g * 0.02f), Offset(c.cx + g * 0.12f, vr.top - g * 0.28f), 1.1f * u, StrokeCap.Round)
    }

    /** La llama de una vela encendida, con su resplandor. */
    private fun DrawScope.llama(p: Offset, s: Float) {
        mancha(p - Offset(0f, s * 0.6f), s * 5f, LLAMA.copy(alpha = 0.32f))
        val f = Path().apply {
            moveTo(p.x, p.y - s * 1.7f)
            cubicTo(p.x + s * 0.55f, p.y - s * 0.85f, p.x + s * 0.5f, p.y, p.x, p.y + s * 0.15f)
            cubicTo(p.x - s * 0.5f, p.y, p.x - s * 0.55f, p.y - s * 0.85f, p.x, p.y - s * 1.7f)
            close()
        }
        drawPath(f, LLAMA)
        drawPath(Path().apply {
            moveTo(p.x, p.y - s * 1.1f)
            cubicTo(p.x + s * 0.3f, p.y - s * 0.6f, p.x + s * 0.26f, p.y - s * 0.05f, p.x, p.y + s * 0.02f)
            cubicTo(p.x - s * 0.26f, p.y - s * 0.05f, p.x - s * 0.3f, p.y - s * 0.6f, p.x, p.y - s * 1.1f)
            close()
        }, Color(0xFFFFF4D6))
        drawOval(ULTRAMAR.copy(alpha = 0.5f), Offset(p.x - s * 0.22f, p.y - s * 0.12f), Size(s * 0.44f, s * 0.26f))
    }

    // ---------------------------------------------------------------- el mueble

    /** El color de una tabla larga de [x0] a [x1] a la altura [y]: de noche, más tibio hacia las velas. */
    private fun tabla(col: Color, x0: Float, x1: Float, y: Float, w: Float, oscuro: Boolean): Brush =
        if (!oscuro) Brush.horizontalGradient(listOf(col, col), x0, x1)
        else Brush.horizontalGradient(List(6) { i -> n(col, true, calor(Offset(x0 + (x1 - x0) * i / 5f, y), w)) }, x0, x1)

    /** La crestería: cuatro arcos apuntados grandes, con su tracería en relieve y sus enjutas, bajo el tablero. */
    private fun DrawScope.cresteria(w: Float, oscuro: Boolean) {
        val u = density
        val r = Rect(w * BL, w * TAB_B, w * BR, w * CREST_B)
        drawRect(tabla(ROBLE, r.left, r.right, r.center.y, w, oscuro), r.topLeft, r.size)
        vetas(r, ROBLE_OSC.copy(alpha = 0.35f), 731, 0.6f, grosor = 0.6f, ondulacion = 0.4f)
        val aw = r.width / 4
        for (j in 0 until 4) {
            val k = if (oscuro) calor(Offset(r.left + aw * (j + 0.5f), r.center.y), w) else 0f
            val madera = n(mezcla(ROBLE, ROBLE_LUZ, 0.35f), oscuro, k)
            val fondo = n(mezcla(ROBLE_OSC, NOCHE, 0.35f), oscuro, k * 0.5f)
            val suelo = n(ROBLE_OSC, oscuro, k)
            val ar = Rect(r.left + aw * j + aw * 0.07f, r.top + r.height * 0.1f, r.left + aw * (j + 1) - aw * 0.07f, r.bottom - r.height * 0.08f)
            // La moldura del arco, en relieve, y su campo hundido.
            relieve(arcoApuntado(ar, ar.width * 0.62f), madera, oscuro, k, 1.8f * u)
            val campo = Rect(ar.left + aw * 0.06f, ar.top + aw * 0.06f, ar.right - aw * 0.06f, ar.bottom)
            hundido(arcoApuntado(campo, campo.width * 0.62f), fondo, suelo, 3.2f * u, oscuro, k)
            // La tracería: un parteluz, dos arquillos y un trébol en la cabeza, en relieve sobre el fondo.
            val tr = n(mezcla(ROBLE, ROBLE_LUZ, 0.25f), oscuro, k)
            val mx = campo.center.x
            val arranque = campo.top + campo.width * 0.5f
            val arquillos = Path().apply {
                addRect(Rect(mx - 1.4f * u, arranque, mx + 1.4f * u, campo.bottom))
                addPath(arcoApuntado(Rect(campo.left + 1.5f * u, arranque, mx, campo.bottom + 4f * u), campo.width * 0.32f))
                addPath(arcoApuntado(Rect(mx, arranque, campo.right - 1.5f * u, campo.bottom + 4f * u), campo.width * 0.32f))
            }
            clipPath(arcoApuntado(campo, campo.width * 0.62f)) {
                translate(1f * u, 1f * u) { drawPath(arquillos, sombra(oscuro, 0.6f), style = Stroke(2.2f * u)) }
                drawPath(arquillos, tr, style = Stroke(2.2f * u))
                drawPath(arquillos, brillo(oscuro, 0.3f, k + 0.3f), style = Stroke(0.6f * u))
                val tc = Offset(mx, campo.top + campo.width * 0.3f)
                val trebol = trebolPath(tc, campo.width * 0.17f)
                translate(1f * u, 1f * u) { drawPath(trebol, sombra(oscuro, 0.6f), style = Stroke(2f * u)) }
                drawPath(trebol, tr, style = Stroke(2f * u))
                drawPath(trebol, brillo(oscuro, 0.3f, k + 0.3f), style = Stroke(0.6f * u))
            }
            // De noche, el filo de las velas en el arco que las mira.
            if (oscuro) drawPath(arcoApuntado(ar, ar.width * 0.62f), LLAMA.copy(alpha = 0.28f * k), style = Stroke(1f * u))
            // La enjuta de la izquierda: un cuadrifolio hundido.
            val ej = Offset(r.left + aw * j + (if (j == 0) aw * 0.04f else 0f), r.top + r.height * 0.24f)
            if (j > 0) hundido(cuadrifolioPath(ej, aw * 0.075f), fondo, suelo, 1.2f * u, oscuro, k)
        }
        // El bocel de abajo.
        drawRect(n(ROBLE_OSC, oscuro, 0.1f), Offset(r.left, r.bottom - 2.5f * u), Size(r.width, 2.5f * u))
        drawRect(brillo(oscuro, 0.3f, 0.4f), Offset(r.left, r.bottom - 2.5f * u), Size(r.width, 0.8f * u))
        // La sombra que el vuelo del tablero deja encima.
        drawRect(Brush.verticalGradient(listOf(sombra(oscuro, 0.5f), Color.Transparent), r.top, r.top + w * 0.02f), r.topLeft, Size(r.width, w * 0.02f))
    }

    /** Un montante como contrafuerte, de [x0] a [x1]: su cara de luz, su chaflán en sombra y un retallo con su tejadillo en cada fila. */
    private fun DrawScope.contrafuerte(x0: Float, x1: Float, y0: Float, y1: Float, filas: List<Rect>, w: Float, oscuro: Boolean, derecho: Boolean) {
        val u = density
        val cara = Rect(x0, y0, x1, y1)
        val k = if (oscuro) calor(Offset(cara.center.x, y0), w) else 0f
        val col = if (derecho) mezcla(ROBLE, ROBLE_OSC, 0.25f) else mezcla(ROBLE, ROBLE_LUZ, 0.25f)
        drawRect(Brush.verticalGradient(listOf(n(col, oscuro, k), n(col, oscuro, k * 0.3f)), y0, y1), cara.topLeft, cara.size)
        vetas(cara, ROBLE_OSC.copy(alpha = 0.35f), (x0 / u).toInt(), 0.7f, vertical = true, grosor = 0.6f, ondulacion = 0.3f)
        drawRect(brillo(oscuro, if (derecho) 0.12f else 0.3f, k), cara.topLeft, Size(1.2f * u, cara.height))
        drawRect(sombra(oscuro, 0.35f), Offset(cara.right - cara.width * 0.22f, y0), Size(cara.width * 0.22f, cara.height))
        // Un retallo con su tejadillo en lo alto de cada fila.
        filas.forEach { f ->
            val y = f.top + f.height * 0.1f
            drawPath(poligono(x0, y + cara.width * 0.45f, (x0 + x1) / 2, y, x1, y + cara.width * 0.45f, x1, y + cara.width * 0.6f, x0, y + cara.width * 0.6f), n(mezcla(col, ROBLE_LUZ, 0.3f), oscuro, k))
            drawLine(brillo(oscuro, 0.4f, k), Offset(x0, y + cara.width * 0.45f), Offset((x0 + x1) / 2, y), 0.9f * u)
            drawLine(sombra(oscuro, 0.5f), Offset(x0, y + cara.width * 0.62f), Offset(x1, y + cara.width * 0.62f), 1.2f * u)
        }
    }

    /** Un herraje de hierro con su remate de flor de lis, de [a] (la esquina) hacia abajo, con sus clavos. */
    private fun DrawScope.herraje(a: Offset, largo: Float, ancho: Float, oscuro: Boolean, k: Float) {
        val u = density
        val hierro = n(HIERRO, oscuro, k)
        drawRect(sombra(oscuro, 0.45f), Offset(a.x - ancho / 2 + 1f * u, a.y + 1f * u), Size(ancho, largo))
        drawRect(hierro, Offset(a.x - ancho / 2, a.y), Size(ancho, largo))
        translate(1f * u, 1f * u) { florDeLis(Offset(a.x, a.y + largo + ancho * 0.9f), ancho * 2.6f, sombra(oscuro, 0.45f)) }
        rotate(180f, Offset(a.x, a.y + largo + ancho * 0.9f)) { florDeLis(Offset(a.x, a.y + largo + ancho * 0.9f), ancho * 2.6f, hierro) }
        drawLine(brillo(oscuro, 0.3f, k), Offset(a.x - ancho / 2 + 0.6f * u, a.y), Offset(a.x - ancho / 2 + 0.6f * u, a.y + largo), 0.6f * u)
        for (j in 0 until 3) {
            val p = Offset(a.x, a.y + largo * (0.18f + 0.33f * j))
            drawCircle(n(HIERRO_LUZ, oscuro, k), ancho * 0.22f, p)
            drawCircle(brillo(oscuro, 0.4f, k), ancho * 0.09f, p - Offset(ancho * 0.07f, ancho * 0.07f))
        }
    }

    /** La cartela de vitela con su marco de latón y sus remaches; [cresta] le pone encima el gablete con su trébol. */
    private fun DrawScope.cartela(r: Rect, oscuro: Boolean, k: Float, cresta: Boolean) {
        val u = density
        val m = 2.6f * u
        drawRect(sombra(oscuro, 0.4f), Offset(r.left - m + 1.6f * u, r.top - m + 2f * u), Size(r.width + 2 * m, r.height + 2 * m))
        if (cresta) {
            val g = poligono(r.center.x - r.width * 0.16f, r.top - m + 0.5f, r.center.x, r.top - m - r.height * 0.22f, r.center.x + r.width * 0.16f, r.top - m + 0.5f)
            translate(1.2f * u, 1.2f * u) { drawPath(g, sombra(oscuro, 0.4f)) }
            drawPath(g, n(LATON, oscuro, k + 0.1f))
            drawLine(brillo(oscuro, 0.5f, k + 0.3f), Offset(r.center.x - r.width * 0.16f, r.top - m), Offset(r.center.x, r.top - m - r.height * 0.22f), 0.8f * u)
            drawPath(trebolPath(Offset(r.center.x, r.top - m - r.height * 0.1f), r.height * 0.11f), n(LATON_OSC, oscuro, k))
        }
        drawRect(n(LATON, oscuro, k + 0.1f), Offset(r.left - m, r.top - m), Size(r.width + 2 * m, r.height + 2 * m))
        drawRect(brillo(oscuro, 0.6f, k + 0.35f), Offset(r.left - m, r.top - m), Size(r.width + 2 * m, 0.9f * u))
        drawRect(brillo(oscuro, 0.4f, k + 0.35f), Offset(r.left - m, r.top - m), Size(0.9f * u, r.height + 2 * m))
        drawRect(n(LATON_OSC, oscuro, k), Offset(r.left - m, r.bottom + m - 0.9f * u), Size(r.width + 2 * m, 0.9f * u))
        drawRect(n(LATON_OSC, oscuro, k), Offset(r.right + m - 0.9f * u, r.top - m), Size(0.9f * u, r.height + 2 * m))
        drawRect(if (oscuro) VITELA_NOCHE else VITELA, r.topLeft, r.size)
        drawRect(sombra(oscuro, 0.18f), r.topLeft, Size(r.width, 1.2f * u))
        drawRect(sombra(oscuro, 0.1f), r.topLeft, Size(1.2f * u, r.height))
        listOf(r.topLeft, Offset(r.right, r.top), Offset(r.left, r.bottom), r.bottomRight).forEach { p ->
            val q = Offset(p.x + (if (p.x < r.center.x) -m / 2 else m / 2), p.y + (if (p.y < r.center.y) -m / 2 else m / 2))
            drawCircle(n(LATON_OSC, oscuro, k), 1f * u, q)
            drawCircle(brillo(oscuro, 0.6f, k + 0.3f), 0.45f * u, q - Offset(0.3f * u, 0.3f * u))
        }
    }

    /** El escabel de roble, al pie: la tabla gastada por arriba y dos pies calados con un arco apuntado. */
    private fun DrawScope.escabel(r: Rect, w: Float, oscuro: Boolean) {
        val u = density
        val k = 0f
        sombraContacto(Offset(r.center.x + w * 0.02f, r.bottom), r.width * 1.15f, 5f * u, if (oscuro) 0.55f else 0.38f)
        val tablaAlta = r.height * 0.2f
        val cara = r.height * 0.1f
        // Los pies, con su arco calado.
        listOf(r.left + r.width * 0.08f, r.right - r.width * 0.08f - r.width * 0.15f).forEachIndexed { j, x ->
            val p = Rect(x, r.top + tablaAlta, x + r.width * 0.15f, r.bottom)
            drawRect(n(if (j == 0) mezcla(ROBLE, ROBLE_LUZ, 0.2f) else mezcla(ROBLE, ROBLE_OSC, 0.15f), oscuro, k), p.topLeft, p.size)
            vetas(p, ROBLE_OSC.copy(alpha = 0.35f), 840 + j, 0.6f, vertical = true, grosor = 0.6f, ondulacion = 0.3f)
            drawPath(arcoApuntado(Rect(p.left + p.width * 0.22f, p.top + p.height * 0.45f, p.right - p.width * 0.22f, p.bottom + 1f), p.width * 0.5f), sombra(oscuro, 0.7f))
            drawRect(brillo(oscuro, 0.3f, k), p.topLeft, Size(1f * u, p.height))
            drawRect(sombra(oscuro, 0.3f), Offset(p.right - 1.4f * u, p.top), Size(1.4f * u, p.height))
            // El fregado: la madera lavada y el cerco del agua al pie.
            drawRect(mezcla(n(CALIZA, oscuro, k), Color.Transparent, 0.75f), Offset(p.left, p.bottom - p.height * 0.28f), Size(p.width, p.height * 0.12f))
            drawRect(sombra(oscuro, 0.3f), Offset(p.left, p.bottom - p.height * 0.14f), Size(p.width, p.height * 0.14f))
        }
        // El travesaño entre los pies.
        drawRect(n(ROBLE, oscuro, k), Offset(r.left + r.width * 0.2f, r.top + tablaAlta + r.height * 0.08f), Size(r.width * 0.6f, r.height * 0.11f))
        drawRect(sombra(oscuro, 0.35f), Offset(r.left + r.width * 0.2f, r.top + tablaAlta + r.height * 0.18f), Size(r.width * 0.6f, 1.2f * u))
        // La tabla: la cara de arriba, gastada al centro, y su canto.
        val arriba = Rect(r.left, r.top - cara, r.right, r.top)
        drawRect(n(mezcla(ROBLE_LUZ, LUZ_MUEBLE, 0.15f), oscuro, k), arriba.topLeft, arriba.size)
        drawOval(n(mezcla(ROBLE_LUZ, LUZ_MUEBLE, 0.4f), oscuro, k).copy(alpha = 0.6f), Offset(r.left + r.width * 0.3f, arriba.top + cara * 0.15f), Size(r.width * 0.35f, cara * 0.7f))
        val canto = Rect(r.left, r.top, r.right, r.top + tablaAlta)
        drawRect(n(ROBLE, oscuro, k), canto.topLeft, canto.size)
        vetas(canto, ROBLE_OSC.copy(alpha = 0.4f), 845, 0.6f, grosor = 0.6f, ondulacion = 0.3f)
        drawRect(brillo(oscuro, 0.4f, k), canto.topLeft, Size(canto.width, 1f * u))
        drawRect(sombra(oscuro, 0.4f), Offset(canto.left, canto.bottom - 1.2f * u), Size(canto.width, 1.2f * u))
        drawRect(sombra(oscuro, 0.3f), Offset(canto.right - 1.5f * u, arriba.top), Size(1.5f * u, canto.bottom - arriba.top))
        // Un golpe en el canto.
        drawPath(poligono(r.left + r.width * 0.7f, r.top, r.left + r.width * 0.76f, r.top, r.left + r.width * 0.73f, r.top + tablaAlta * 0.45f), sombra(oscuro, 0.35f))
    }

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val alto = size.height
        val b = d.base; val hb = b.height
        val rincon = b.top + hb * RINCON
        val zoc = b.top + hb * ZOC
        val pie = b.top + hb * PIES
        fun k(x: Float, y: Float) = if (oscuro) calor(Offset(x, y), w) else 0f

        // ---- el muro, su luz, su cruz de consagración y sus marcas
        muro(w, rincon + 1f * u, oscuro)
        cruzConsagracion(Offset(w * 0.178f, w * 0.125f), w * 0.046f, oscuro, k(w * 0.178f, w * 0.125f))
        marcaCantero(Offset(w * 0.355f, w * 0.06f), w * 0.016f, 0, oscuro, k(w * 0.355f, w * 0.06f))
        marcaCantero(Offset(w * 0.715f, w * 0.075f), w * 0.016f, 1, oscuro, k(w * 0.715f, w * 0.075f))
        marcaCantero(Offset(w * 0.024f, w * 0.9f), w * 0.013f, 2, oscuro, k(0f, w))
        if (oscuro) haloDeVelas(w) else luzDeVentana(w)

        // ---- las sombras en el muro de lo que tiene delante
        val inc = incensario(w)
        val cands = candeleros(w)
        sombraEnMuro(siluetaCruz(w, alto), asta(w * 0.2f, w), w, oscuro, 0, 0.8f)
        sombraEnMuro(siluetaIncensario(inc, u), inc.vaso.center, w, oscuro, 0)
        sombraEnMuro(siluetaLlaves(w, u), Offset(w * 0.555f, w * 0.16f), w, oscuro, 0)
        sombraEnMuro(siluetaAtril(w), Offset(w * 0.215f, w * 0.38f), w, oscuro, 0, 0.9f)
        sombraEnMuro(siluetaCaliz(w), Offset(w * 0.594f, w * 0.39f), w, oscuro, 0)
        sombraEnMuro(siluetaCandelero(cands[0]), Offset(cands[0].cx, w * 0.33f), w, oscuro, 1, if (oscuro) 0.45f else 0.9f)
        sombraEnMuro(siluetaCandelero(cands[1]), Offset(cands[1].cx, w * 0.4f), w, oscuro, 0, 0.9f)

        // ---- lo colgado del muro y la cruz del rincón
        cruzProcesional(w, alto, oscuro)
        dibujaIncensario(inc, w, oscuro)
        llaves(w, oscuro)

        // ---- la sombra del mueble en el muro, a su derecha
        val sombraMuro = poligono(w * TR, w * (TAB + 0.012f), w * (TR + 0.02f), w * (TAB + 0.02f), w * (TR + 0.02f), w * (TAB_B + 0.024f),
            w * (BR + 0.034f), w * (TAB_B + 0.034f), w * (BR + 0.034f), rincon, w * BR, rincon, w * BR, w * TAB_B, w * TR, w * TAB_B)
        drawPath(sombraMuro, sombra(oscuro, if (oscuro) 0.4f else 0.3f))

        // ---- el suelo de losas
        drawRect(Brush.verticalGradient(listOf(n(mezcla(LOSA, SOMBRA_MUEBLE, 0.3f), oscuro, 0.06f), n(LOSA, oscuro, 0.08f), n(mezcla(LOSA, LUZ_MUEBLE, 0.12f), oscuro, 0.1f)), rincon, alto),
            Offset(0f, rincon), Size(w, alto - rincon))
        val juntas = listOf(0.12f, 0.3f, 0.56f, 0.88f).map { rincon + (alto - rincon) * it }
        val fuga = Offset(w * 0.46f, rincon - w * 1.2f)
        var y0 = rincon
        juntas.forEachIndexed { i, y ->
            drawLine(sombra(oscuro, 0.35f), Offset(0f, y), Offset(w, y), 1f * u)
            drawLine(brillo(oscuro, 0.18f, 0.1f), Offset(0f, y + 1f * u), Offset(w, y + 1f * u), 0.6f * u)
            for (j in -6..6) {
                val xb = w * 0.46f + (j + (if (i % 2 == 0) 0f else 0.5f)) * w * 0.2f
                fun x(yy: Float) = fuga.x + (xb - fuga.x) * (yy - fuga.y) / (alto - fuga.y)
                drawLine(sombra(oscuro, 0.3f), Offset(x(y0), y0), Offset(x(y), y), 1f * u)
            }
            y0 = y
        }
        pinceladas(Rect(0f, rincon, w, alto), if (oscuro) NOCHE else SOMBRA_MUEBLE, 905, 16, u, if (oscuro) 0.1f else 0.07f)
        // La sombra del mueble en las losas, corrida a la derecha.
        drawPath(poligono(w * (BL + 0.02f), pie - 1f * u, w * (BR + 0.01f), pie - 1f * u, w * (BR + 0.06f), pie + hb * 0.12f, w * (BL + 0.05f), pie + hb * 0.12f), sombra(oscuro, if (oscuro) 0.45f else 0.3f))

        // ---- el cuerpo del mueble
        val cuerpo = Rect(w * BL, w * TAB_B, w * BR, zoc)
        drawRect(if (oscuro) Brush.verticalGradient(listOf(n(ROBLE, true, 0.4f), n(ROBLE, true, 0.12f), n(ROBLE, true, 0f)), cuerpo.top, cuerpo.top + w * 1.1f)
            else Brush.verticalGradient(listOf(ROBLE, ROBLE), cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, ROBLE_OSC.copy(alpha = 0.4f), 13, 0.7f, grosor = 0.7f, ondulacion = 0.4f)
        // Los rebajes de los cajones, con su filo de luz por fuera y las clavijas de las uniones.
        d.huecos.forEach { r ->
            val kk = k(r.center.x, r.top)
            drawRect(n(mezcla(ROBLE_OSC, Color(0xFF140C06), 0.5f), oscuro, 0f), Offset(r.left - 2.4f * u, r.top - 2.4f * u), Size(r.width + 4.8f * u, r.height + 4.8f * u))
            drawRect(brillo(oscuro, 0.25f, kk), Offset(r.left - 3.4f * u, r.bottom + 2.4f * u), Size(r.width + 6.8f * u, 1f * u))
            drawRect(sombra(oscuro, 0.3f), Offset(r.left - 3.4f * u, r.top - 3.6f * u), Size(r.width + 6.8f * u, 1.2f * u))
        }
        d.filas.forEach { f ->
            val y = f.bottom + d.separacion / 2
            listOf(w * (BL + 0.02f), (d.huecos[0].right + d.huecos[1].left) / 2, w * (BR - 0.02f)).forEach { x ->
                listOf(-1f, 1f).forEach { s -> drawCircle(n(ROBLE_OSC, oscuro, k(x, y)), 1.2f * u, Offset(x + s * 2.6f * u, y)) }
            }
        }
        // Los montantes: contrafuertes a los lados y uno más fino en medio.
        val m0 = d.huecos[0].right + 2.6f * u; val m1 = d.huecos[1].left - 2.6f * u
        contrafuerte(w * BL, d.huecos[0].left - 2.6f * u, w * CREST_B, zoc, d.filas, w, oscuro, false)
        contrafuerte(d.huecos[1].right + 2.6f * u, w * BR, w * CREST_B, zoc, d.filas, w, oscuro, true)
        contrafuerte(m0, m1, w * CREST_B, zoc, d.filas, w, oscuro, false)
        // Los herrajes de las esquinas.
        val hx0 = (w * BL + d.huecos[0].left) / 2; val hx1 = (d.huecos[1].right + w * BR) / 2
        herraje(Offset(hx0, w * CREST_B + 1f * u), w * 0.07f, w * 0.009f, oscuro, k(hx0, w * 0.6f))
        herraje(Offset(hx1, w * CREST_B + 1f * u), w * 0.07f, w * 0.009f, oscuro, k(hx1, w * 0.6f))
        // La crestería bajo el tablero.
        cresteria(w, oscuro)
        // Un filo de noche en el costado derecho del cuerpo, el que mira a las velas.
        if (oscuro) drawLine(LLAMA.copy(alpha = 0.2f), Offset(w * BR - 0.6f * u, w * TAB_B), Offset(w * BR - 0.6f * u, w * 0.9f), 1f * u)

        // ---- el tablero: su cara de arriba, su canto con moldura, la cera caída y el desgaste
        val cara = Rect(w * TL, w * TAB, w * TR, w * TAB_F)
        drawRect(tabla(mezcla(ROBLE_LUZ, LUZ_MUEBLE, 0.1f), cara.left, cara.right, cara.center.y, w, oscuro), cara.topLeft, cara.size)
        if (!oscuro) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.18f), Color.Transparent, sombraMueble(0.12f)), cara.left, cara.right), cara.topLeft, cara.size)
        vetas(cara, ROBLE_OSC.copy(alpha = 0.32f), 21, 0.9f, grosor = 0.6f, ondulacion = 0.3f)
        val canto = Rect(w * TL, w * TAB_F, w * TR, w * TAB_B)
        drawRect(tabla(ROBLE, canto.left, canto.right, canto.center.y, w, oscuro), canto.topLeft, canto.size)
        vetas(canto, ROBLE_OSC.copy(alpha = 0.4f), 23, 0.8f, grosor = 0.6f, ondulacion = 0.3f)
        drawRect(brillo(oscuro, 0.45f, 0.6f), canto.topLeft, Size(canto.width, 1.2f * u))
        drawLine(sombra(oscuro, 0.4f), Offset(canto.left, canto.top + canto.height * 0.5f), Offset(canto.right, canto.top + canto.height * 0.5f), 1f * u)
        drawLine(brillo(oscuro, 0.22f, 0.5f), Offset(canto.left, canto.top + canto.height * 0.5f + 1f * u), Offset(canto.right, canto.top + canto.height * 0.5f + 1f * u), 0.7f * u)
        drawRect(sombra(oscuro, 0.45f), Offset(canto.left, canto.bottom - 1.6f * u), Size(canto.width, 1.6f * u))
        drawRect(brillo(oscuro, 0.3f, 0f), Offset(canto.left, cara.top), Size(1.4f * u, canto.bottom - cara.top))
        drawRect(sombra(oscuro, 0.4f), Offset(canto.right - 1.6f * u, cara.top), Size(1.6f * u, canto.bottom - cara.top))
        // El canto gastado por las manos y la cera que cayó cerca de las velas.
        drawOval(n(mezcla(ROBLE_LUZ, LUZ_MUEBLE, 0.2f), oscuro, 0.2f).copy(alpha = 0.35f), Offset(w * 0.47f, canto.top + 1f * u), Size(w * 0.14f, canto.height * 0.4f))
        listOf(0.63f to 0f, 0.69f to 0.01f, 0.735f to 0f, 0.79f to 0.005f).forEachIndexed { j, (x, dy) ->
            val p = Offset(w * x, w * (TAB + 0.006f + dy))
            drawOval(n(CERA, oscuro, k(p.x, p.y) + 0.2f), Offset(p.x - w * 0.004f, p.y - w * 0.0018f), Size(w * 0.008f, w * 0.0036f))
            if (j == 1) {
                drawLine(n(CERA, oscuro, k(p.x, p.y) + 0.2f), Offset(p.x, canto.top), Offset(p.x + 0.4f * u, canto.top + canto.height * 0.7f), 1.6f * u, StrokeCap.Round)
                drawCircle(n(CERA, oscuro, k(p.x, p.y) + 0.2f), 1.3f * u, Offset(p.x + 0.4f * u, canto.top + canto.height * 0.7f))
            }
        }
        drawOval(sombra(oscuro, 0.25f), Offset(w * 0.8f, w * (TAB + 0.004f)), Size(w * 0.03f, w * 0.006f))

        // ---- lo de encima, de atrás adelante
        caliz(w, oscuro)
        candelero(cands[1], w, oscuro, consumida = true)
        atrilConMisal(w, oscuro)
        // De día, la luz de la ventana también cae sobre el misal.
        if (!oscuro) clipPath(siluetaAtril(w)) { lancetas(w).forEach { l -> clipPath(l) { drawRect(luzMueble(0.14f), Offset.Zero, size) } } }
        estola(w, oscuro)
        candelero(cands[0], w, oscuro, consumida = false)

        // ---- el zócalo con sus cuadrifolios, y los pies gastados por el fregado
        val z = Rect(w * (BL - 0.008f), b.top, w * (BR + 0.008f), zoc)
        drawRect(if (oscuro) Brush.horizontalGradient(listOf(n(ROBLE, true, 0f), n(ROBLE, true, 0.06f)), z.left, z.right) else Brush.horizontalGradient(listOf(ROBLE, mezcla(ROBLE, ROBLE_OSC, 0.25f)), z.left, z.right), z.topLeft, z.size)
        vetas(z, ROBLE_OSC.copy(alpha = 0.4f), 31, 0.8f, grosor = 0.6f, ondulacion = 0.3f)
        drawRect(n(mezcla(ROBLE, ROBLE_LUZ, 0.35f), oscuro, 0.05f), z.topLeft, Size(z.width, z.height * 0.16f))
        drawRect(brillo(oscuro, 0.4f, 0.2f), z.topLeft, Size(z.width, 1f * u))
        drawRect(sombra(oscuro, 0.45f), Offset(z.left, z.top + z.height * 0.16f), Size(z.width, 1.2f * u))
        val fx = if (d.mas > 0) listOf(0.1f, 0.22f, 0.78f, 0.9f) else listOf(0.1f, 0.22f, 0.36f, 0.5f, 0.64f, 0.78f, 0.9f)
        fx.forEach { f ->
            hundido(cuadrifolioPath(Offset(w * f, z.top + z.height * 0.58f), z.height * 0.32f), n(mezcla(ROBLE_OSC, SOMBRA_MUEBLE, 0.3f), oscuro, 0f), n(mezcla(ROBLE, ROBLE_OSC, 0.4f), oscuro, 0.05f), 1.6f * u, oscuro, 0f)
        }
        drawRect(sombra(oscuro, 0.4f), Offset(z.left, z.bottom - 1.4f * u), Size(z.width, 1.4f * u))
        // Bajo el zócalo, entre los pies, la oscuridad de debajo del mueble.
        drawRect(sombra(oscuro, 0.62f), Offset(z.left, zoc), Size(z.width, pie - zoc))
        listOf(z.left to z.left + w * 0.075f, w * 0.47f to w * 0.53f, z.right - w * 0.075f to z.right).forEachIndexed { j, (xa, xb) ->
            val p = Rect(xa, zoc, xb, pie)
            drawRoundRect(n(if (j == 2) mezcla(ROBLE, ROBLE_OSC, 0.3f) else ROBLE, oscuro, 0f), p.topLeft, p.size, CornerRadius(0f))
            vetas(p, ROBLE_OSC.copy(alpha = 0.35f), 41 + j, 0.6f, vertical = true, grosor = 0.6f, ondulacion = 0.3f)
            drawRect(brillo(oscuro, 0.3f, 0f), p.topLeft, Size(1.2f * u, p.height))
            drawRect(sombra(oscuro, 0.35f), Offset(p.right - p.width * 0.2f, p.top), Size(p.width * 0.2f, p.height))
            // La madera lavada por el fregado y el cerco oscuro del agua; las esquinas romas de tanto golpe.
            drawRect(mezcla(n(CALIZA, oscuro, 0f), Color.Transparent, 0.72f), Offset(p.left, p.bottom - p.height * 0.48f), Size(p.width, p.height * 0.2f))
            drawRect(sombra(oscuro, 0.32f), Offset(p.left, p.bottom - p.height * 0.28f), Size(p.width, p.height * 0.28f))
            drawPath(poligono(p.left, p.bottom, p.left + 2.2f * u, p.bottom, p.left, p.bottom - 2.2f * u), n(LOSA, oscuro, 0f))
            drawPath(poligono(p.right, p.bottom, p.right - 2.2f * u, p.bottom, p.right, p.bottom - 2.2f * u), n(LOSA, oscuro, 0f))
            sombraContacto(Offset(p.center.x + 2f * u, p.bottom), p.width * 1.2f, 3f * u, if (oscuro) 0.5f else 0.4f)
        }

        // ---- el escabel, al pie, a la izquierda
        escabel(Rect(w * 0.07f, b.top + hb * 0.5f, w * 0.3f, b.top + hb * 0.95f), w, oscuro)

        // ---- de noche: lo lejano se hunde y arden las velas
        if (oscuro) {
            val c = Offset(w * 0.7f, w * 0.26f)
            drawRect(Brush.radialGradient(listOf(Color.Transparent, NOCHE.copy(alpha = 0.26f), NOCHE.copy(alpha = 0.5f)), c, w * 1.5f), Offset.Zero, size)
            cands.forEach { cd -> llama(Offset(cd.cx + cd.gruesa * 0.12f, cd.plato - cd.vela - cd.gruesa * 0.3f), w * 0.009f) }
        }

        // ---- la placa de «y N más», en el zócalo
        if (d.mas > 0) placa.let { pl -> cartela(d.enBase(pl), oscuro, 0.15f, cresta = false) }
        granoMueble(Rect(0f, 0f, w, alto), oscuro)
    }

    // ---------------------------------------------------------------- los compartimentos

    /** Las hojas de vitela que asoman por arriba de un cajón entreabierto: más cuantas más guarda; revueltas, las sin tema. */
    private fun DrawScope.hojasAsomadas(w: Float, arriba: Float, k: Int, semilla: Int, revueltas: Boolean, oscuro: Boolean, cerca: Float) {
        val u = density
        drawRect(n(Color(0xFF1A110A), oscuro, 0f), Offset.Zero, Size(w, arriba + 3f * u))
        val m = k.coerceIn(1, 9)
        for (i in 0 until m) {
            val ancho = w * (0.085f + 0.04f * azar(semilla, i + 50))
            val x = w * (0.06f + 0.88f * (i + 0.5f) / m) - ancho / 2 + (azar(semilla, i + 60) - 0.5f) * w * 0.03f
            val y0 = arriba * (0.06f + 0.5f * azar(semilla, i)) + 1f * u
            val incl = (azar(semilla, i + 70) - 0.5f) * w * (if (revueltas) 0.05f else 0.012f)
            val hoja = poligono(x, arriba + 4f * u, x + incl, y0, x + ancho + incl, y0 + (azar(semilla, i + 80) - 0.5f) * 3f * u, x + ancho, arriba + 4f * u)
            drawPath(hoja, n(if (i % 2 == 0) VITELA else VITELA_OSC, oscuro, cerca + 0.15f))
            drawLine(brillo(oscuro, 0.5f, cerca + 0.3f), Offset(x + incl, y0), Offset(x + ancho + incl, y0), 0.8f * u)
            drawLine(sombra(oscuro, 0.3f), Offset(x + ancho + incl, y0), Offset(x + ancho, arriba + 4f * u), 0.8f * u)
            if (i % 3 == 1) drawRect(n(if (i % 2 == 0) GRANA else ULTRAMAR, oscuro, cerca), Offset(x + incl + ancho * 0.4f, y0 - 3f * u), Size(w * 0.014f, 4f * u))
        }
    }

    /** El frente de roble de un cajón: veta, canto de arriba a la luz, lado derecho en sombra y la moldura hundida alrededor. */
    private fun DrawScope.frente(f: Rect, semilla: Int, oscuro: Boolean, cerca: Float) {
        val u = density
        drawRect(Brush.verticalGradient(listOf(n(mezcla(ROBLE, ROBLE_LUZ, 0.32f), oscuro, cerca + 0.1f), n(ROBLE, oscuro, cerca), n(mezcla(ROBLE, ROBLE_OSC, 0.3f), oscuro, cerca - 0.1f)), f.top, f.bottom), f.topLeft, f.size)
        vetas(f, ROBLE_OSC.copy(alpha = 0.38f), semilla, 1.1f, grosor = 0.7f, ondulacion = 0.4f)
        if (oscuro) drawRect(Brush.horizontalGradient(listOf(Color.Transparent, LLAMA.copy(alpha = 0.08f)), f.left + f.width * 0.35f, f.right), f.topLeft, f.size)
        drawRect(brillo(oscuro, 0.4f, cerca + 0.4f), f.topLeft, Size(f.width, 1.3f * u))
        drawRect(brillo(oscuro, 0.18f, cerca), f.topLeft, Size(1.3f * u, f.height))
        drawRect(sombra(oscuro, 0.45f), Offset(f.left, f.bottom - 1.6f * u), Size(f.width, 1.6f * u))
        drawRect(sombra(oscuro, 0.35f), Offset(f.right - 1.6f * u, f.top), Size(1.6f * u, f.height))
        val i = 4.5f * u
        val m = Rect(f.left + i, f.top + i, f.right - i, f.bottom - i)
        drawLine(sombra(oscuro, 0.45f), m.topLeft, Offset(m.right, m.top), 1f * u)
        drawLine(sombra(oscuro, 0.35f), m.topLeft, Offset(m.left, m.bottom), 1f * u)
        drawLine(brillo(oscuro, 0.3f, cerca + 0.2f), Offset(m.left, m.bottom), m.bottomRight, 1f * u)
        drawLine(brillo(oscuro, 0.2f, cerca + 0.2f), Offset(m.right, m.top), m.bottomRight, 1f * u)
    }

    /** Un panel hundido con su fondo y su moldura (luz abajo a la derecha, sombra arriba a la izquierda). */
    private fun DrawScope.panel(r: Rect, oscuro: Boolean, cerca: Float) {
        val u = density
        drawRect(n(mezcla(ROBLE, ROBLE_OSC, 0.3f), oscuro, cerca), r.topLeft, r.size)
        drawRect(sombra(oscuro, 0.45f), r.topLeft, Size(r.width, 1.4f * u))
        drawRect(sombra(oscuro, 0.35f), r.topLeft, Size(1.4f * u, r.height))
        drawRect(brillo(oscuro, 0.25f, cerca + 0.2f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
        drawRect(brillo(oscuro, 0.18f, cerca + 0.2f), Offset(r.right - 1f * u, r.top), Size(1f * u, r.height))
    }

    /** Un panel de pergamino plegado tallado: tres pliegues en relieve con sus remates enrollados. */
    private fun DrawScope.pergamino(r: Rect, oscuro: Boolean, cerca: Float) {
        val u = density
        panel(r, oscuro, cerca)
        val pw = r.width / 3
        for (k in 0 until 3) {
            val x = r.left + pw * k
            val top = r.top + pw * 0.5f; val bot = r.bottom - pw * 0.5f
            drawRect(n(mezcla(ROBLE, ROBLE_LUZ, 0.3f), oscuro, cerca + 0.1f), Offset(x + pw * 0.14f, top), Size(pw * 0.36f, bot - top))
            drawRect(n(mezcla(ROBLE, ROBLE_OSC, 0.1f), oscuro, cerca), Offset(x + pw * 0.5f, top), Size(pw * 0.36f, bot - top))
            drawLine(brillo(oscuro, 0.4f, cerca + 0.3f), Offset(x + pw * 0.22f, top), Offset(x + pw * 0.22f, bot), 1f * u)
            drawLine(sombra(oscuro, 0.55f), Offset(x + pw * 0.9f, top - pw * 0.1f), Offset(x + pw * 0.9f, bot + pw * 0.1f), 1.2f * u)
            drawArc(brillo(oscuro, 0.35f, cerca + 0.3f), 180f, 180f, false, Offset(x + pw * 0.12f, r.top + pw * 0.22f), Size(pw * 0.76f, pw * 0.56f), style = Stroke(1f * u))
            drawArc(sombra(oscuro, 0.5f), 0f, 180f, false, Offset(x + pw * 0.12f, r.bottom - pw * 0.78f), Size(pw * 0.76f, pw * 0.56f), style = Stroke(1f * u))
        }
    }

    /** Una portezuela calada: un cuadrifolio o un trébol bajo un arco, y tras el calado las hojas que se adivinan. */
    private fun DrawScope.calado(r: Rect, trebol: Boolean, n0: Int, semilla: Int, oscuro: Boolean, cerca: Float) {
        val u = density
        panel(r, oscuro, cerca)
        val c = r.center
        val rr = minOf(r.width, r.height) * 0.4f
        val hueco = if (trebol) unir(listOf(arcoApuntado(Rect(c.x - rr * 0.75f, c.y - rr * 1.15f, c.x + rr * 0.75f, c.y + rr * 1.2f), rr * 1.2f)))
            else cuadrifolioPath(c, rr * 1.05f)
        drawPath(hueco, n(Color(0xFF2A1C11), oscuro, 0f))
        clipPath(hueco) {
            val m = n0.coerceIn(1, 5)
            for (i in 0 until m) {
                val x = r.left + r.width * (0.18f + 0.64f * (i + 0.5f) / m) + (azar(semilla, 90 + i) - 0.5f) * r.width * 0.1f
                val y = c.y - rr * (0.2f + 0.8f * azar(semilla, 95 + i))
                drawRect(n(if (i % 2 == 0) VITELA_OSC else VITELA, oscuro, cerca).copy(alpha = 0.8f), Offset(x, y), Size(r.width * 0.11f, r.bottom - y))
                drawLine(sombra(oscuro, 0.5f), Offset(x + r.width * 0.08f, y), Offset(x + r.width * 0.08f, r.bottom), 1f * u)
            }
            drawRect(Brush.verticalGradient(listOf(sombra(oscuro, 0.55f), Color.Transparent), r.top, c.y), r.topLeft, Size(r.width, c.y - r.top))
        }
        if (trebol) {
            // Los tres lóbulos de la cabeza del arco, en relieve.
            val tc = Offset(c.x, c.y - rr * 0.42f)
            val t = trebolPath(tc, rr * 0.78f)
            translate(0.9f * u, 0.9f * u) { drawPath(t, sombra(oscuro, 0.6f), style = Stroke(1.6f * u)) }
            drawPath(t, n(mezcla(ROBLE, ROBLE_LUZ, 0.2f), oscuro, cerca), style = Stroke(1.6f * u))
        }
        translate(-0.7f * u, -0.7f * u) { drawPath(hueco, brillo(oscuro, 0.3f, cerca + 0.3f), style = Stroke(0.8f * u)) }
        drawPath(hueco, n(ROBLE_OSC, oscuro, cerca), style = Stroke(1f * u))
    }

    /** Una faja de hierro horizontal de [x0] a [x1] que acaba en flor de lis hacia [x1]. */
    private fun DrawScope.faja(x0: Float, x1: Float, y: Float, alto: Float, oscuro: Boolean, cerca: Float) {
        val u = density
        val hierro = n(HIERRO, oscuro, cerca)
        val dir = if (x1 > x0) 1f else -1f
        val fin = x1 - dir * alto * 1.6f
        drawRect(sombra(oscuro, 0.45f), Offset(minOf(x0, fin) + 1f * u, y - alto / 2 + 1.2f * u), Size(kotlin.math.abs(fin - x0), alto))
        drawRect(hierro, Offset(minOf(x0, fin), y - alto / 2), Size(kotlin.math.abs(fin - x0), alto))
        drawLine(brillo(oscuro, 0.3f, cerca + 0.2f), Offset(x0, y - alto / 2 + 0.5f * u), Offset(fin, y - alto / 2 + 0.5f * u), 0.6f * u)
        val fc = Offset(x1 - dir * alto * 1.1f, y)
        rotate(90f * dir, fc) {
            translate(0.9f * u, 0.9f * u) { florDeLis(fc, alto * 3.2f, sombra(oscuro, 0.45f)) }
            florDeLis(fc, alto * 3.2f, hierro)
        }
        for (j in 0 until 3) {
            val p = Offset(x0 + (fin - x0) * (0.15f + 0.35f * j), y)
            drawCircle(n(HIERRO_LUZ, oscuro, cerca), alto * 0.24f, p)
            drawCircle(brillo(oscuro, 0.45f, cerca + 0.2f), alto * 0.1f, p - Offset(alto * 0.07f, alto * 0.07f))
        }
    }

    /** La argolla de hierro que cuelga de una roseta cuadrifolia, con su sombra en la madera. */
    private fun DrawScope.argolla(c: Offset, rr: Float, oscuro: Boolean, cerca: Float) {
        val u = density
        val hierro = n(HIERRO, oscuro, cerca)
        translate(1f * u, 1f * u) { drawPath(cuadrifolioPath(c, rr * 0.9f), sombra(oscuro, 0.45f)) }
        drawPath(cuadrifolioPath(c, rr * 0.9f), hierro)
        drawCircle(n(HIERRO_LUZ, oscuro, cerca), rr * 0.18f, c)
        val anillo = Rect(c.x - rr, c.y, c.x + rr, c.y + 2 * rr)
        drawOval(sombra(oscuro, 0.4f), Offset(anillo.left + 1.4f * u, anillo.top + 1.8f * u), anillo.size, style = Stroke(1.9f * u))
        drawOval(hierro, anillo.topLeft, anillo.size, style = Stroke(1.9f * u))
        drawArc(brillo(oscuro, 0.75f, cerca + 0.5f), 110f, 110f, false, anillo.topLeft, anillo.size, style = Stroke(0.9f * u))
        drawArc(n(HIERRO_LUZ, oscuro, cerca), 300f, 60f, false, anillo.topLeft, anillo.size, style = Stroke(0.7f * u))
    }

    /** La cerradura de hierro: una chapa apuntada con su bocallave y su picaporte. */
    private fun DrawScope.cerradura(c: Offset, a: Float, oscuro: Boolean, cerca: Float) {
        val u = density
        val chapa = Path().apply {
            moveTo(c.x - a / 2, c.y - a * 0.5f); lineTo(c.x + a / 2, c.y - a * 0.5f); lineTo(c.x + a / 2, c.y + a * 0.25f)
            lineTo(c.x, c.y + a * 0.75f); lineTo(c.x - a / 2, c.y + a * 0.25f); close()
        }
        translate(1.2f * u, 1.4f * u) { drawPath(chapa, sombra(oscuro, 0.45f)) }
        drawPath(chapa, n(HIERRO, oscuro, cerca))
        drawLine(brillo(oscuro, 0.35f, cerca + 0.2f), Offset(c.x - a / 2, c.y - a * 0.5f), Offset(c.x + a / 2, c.y - a * 0.5f), 0.7f * u)
        drawCircle(n(Color(0xFF0E0B08), oscuro, 0f), a * 0.1f, Offset(c.x, c.y - a * 0.05f))
        drawPath(poligono(c.x - a * 0.05f, c.y, c.x + a * 0.05f, c.y, c.x + a * 0.03f, c.y + a * 0.28f, c.x - a * 0.03f, c.y + a * 0.28f), n(Color(0xFF0E0B08), oscuro, 0f))
        listOf(Offset(c.x - a * 0.36f, c.y - a * 0.36f), Offset(c.x + a * 0.36f, c.y - a * 0.36f), Offset(c.x, c.y + a * 0.55f)).forEach {
            drawCircle(n(HIERRO_LUZ, oscuro, cerca), a * 0.06f, it)
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        val k = if (c.vacio) 0 else c.n
        // De noche, las velas quedan encima y a la derecha del mueble: a los cajones les llega poco, más al canto de arriba.
        val cerca = 0.15f
        // Los más llenos quedan más entreabiertos; el de las ideas sin tema, un poco más.
        val entre = if (c.vacio || k == 0) 0f else h * (0.025f + 0.017f * k.coerceIn(0, 7)) + (if (c.sinTema) h * 0.02f else 0f)
        val baja = h * SALIDA * ab
        val arriba = entre + baja
        if (arriba > 0.5f) hojasAsomadas(w, arriba, k.coerceAtLeast(1), c.semilla, c.sinTema, oscuro, cerca)
        // El frente: al abrirse baja y se acerca un poco.
        val e = w * 0.02f * ab
        val f = Rect(-e, arriba, w + e, h + baja + h * 0.04f * ab)
        if (ab > 0f) drawRect(sombra(oscuro, 0.35f * ab), Offset(f.left + 2f * u, f.bottom - 1f * u), Size(f.width, 1f * u + 5f * u * ab))
        frente(f, c.semilla, oscuro, cerca)
        if (arriba < 0.5f) drawRect(sombra(oscuro, 0.3f), Offset(0f, 0f), Size(w, 2.2f * u))
        val fh = f.height
        val izq = Rect(w * 0.045f, f.top + fh * 0.15f, w * 0.205f, f.bottom - fh * 0.13f)
        val der = Rect(w * 0.795f, izq.top, w * 0.955f, izq.bottom)
        val roseta = Offset(w / 2, h * 0.775f + baja)
        val rr = h * 0.055f

        if (c.vacio) {
            // Un cajón todavía sin tema: liso, con los agujeros de la cartela que aún no tiene, o con un paño doblado encima.
            panel(Rect(w * 0.06f, f.top + fh * 0.14f, w * 0.94f, f.bottom - fh * 0.13f), oscuro, cerca)
            listOf(0.36f, 0.64f).forEach { x ->
                drawCircle(n(ROBLE_OSC, oscuro, 0f), 1.2f * u, Offset(w * x, h * 0.36f))
                drawCircle(brillo(oscuro, 0.25f, cerca), 0.5f * u, Offset(w * x + 0.8f * u, h * 0.36f + 0.8f * u))
            }
            argolla(roseta, rr, oscuro, cerca)
            if (Math.floorMod(c.semilla / 5, 2) == 1) {
                val lino = mezcla(VITELA, LUZ_MUEBLE, 0.35f)
                val p = Path().apply {
                    moveTo(w * 0.07f, 0f); lineTo(w * 0.4f, 0f)
                    cubicTo(w * 0.41f, h * 0.2f, w * 0.38f, h * 0.36f, w * 0.4f, h * 0.5f)
                    lineTo(w * 0.33f, h * 0.46f); lineTo(w * 0.25f, h * 0.53f); lineTo(w * 0.17f, h * 0.47f); lineTo(w * 0.09f, h * 0.52f)
                    cubicTo(w * 0.08f, h * 0.35f, w * 0.06f, h * 0.18f, w * 0.07f, 0f); close()
                }
                translate(w * 0.012f, h * 0.025f) { drawPath(p, sombra(oscuro, 0.35f)) }
                drawPath(p, Brush.horizontalGradient(listOf(n(lino, oscuro, cerca + 0.1f), n(mezcla(lino, SOMBRA_MUEBLE, 0.28f), oscuro, cerca)), w * 0.07f, w * 0.4f))
                clipPath(p) {
                    drawRect(sombra(oscuro, 0.18f), Offset(w * 0.07f, h * 0.1f), Size(w * 0.34f, h * 0.035f))
                    drawRect(brillo(oscuro, 0.4f, cerca), Offset(w * 0.07f, 0f), Size(w * 0.34f, h * 0.03f))
                    drawLine(sombra(oscuro, 0.2f), Offset(w * 0.2f, h * 0.14f), Offset(w * 0.17f, h * 0.46f), 1f * u)
                    drawLine(sombra(oscuro, 0.2f), Offset(w * 0.31f, h * 0.14f), Offset(w * 0.33f, h * 0.45f), 1f * u)
                }
                drawPath(cruzPatada(Offset(w * 0.24f, h * 0.3f), h * 0.06f), n(GRANA, oscuro, cerca))
            }
            return
        }

        // Las tres clases de cajón (la de las ideas sin tema, siempre de pergamino plegado).
        val clase = if (c.sinTema) 0 else Math.floorMod(c.semilla / 3, 3)
        when (clase) {
            0 -> { pergamino(izq, oscuro, cerca); pergamino(der, oscuro, cerca) }
            1 -> {
                listOf(0.3f, 0.74f).forEach { fy ->
                    val y = f.top + fh * fy
                    faja(w * 0.025f, w * 0.225f, y, h * 0.068f, oscuro, cerca)
                    faja(w * 0.975f, w * 0.775f, y, h * 0.068f, oscuro, cerca)
                }
            }
            else -> { calado(izq, false, k, c.semilla, oscuro, cerca); calado(der, true, k, c.semilla + 1, oscuro, cerca) }
        }
        // El uso: un frente rozado abajo, o una grieta cosida con una grapa de hierro.
        if (Math.floorMod(c.semilla / 7, 3) == 0) {
            drawOval(n(mezcla(ROBLE_LUZ, LUZ_MUEBLE, 0.2f), oscuro, cerca).copy(alpha = 0.1f), Offset(w * 0.3f, f.bottom - fh * 0.3f), Size(w * 0.4f, fh * 0.22f))
            drawOval(n(mezcla(ROBLE_LUZ, LUZ_MUEBLE, 0.2f), oscuro, cerca).copy(alpha = 0.1f), Offset(w * 0.36f, f.bottom - fh * 0.25f), Size(w * 0.28f, fh * 0.14f))
            for (j in 0 until 10) {
                val x = w * (0.3f + 0.4f * azar(c.semilla, 300 + j)); val y = f.bottom - fh * (0.1f + 0.18f * azar(c.semilla, 310 + j))
                drawLine(brillo(oscuro, 0.22f, cerca), Offset(x, y), Offset(x + w * 0.03f, y - fh * 0.03f), 0.6f * u)
            }
        }
        if (Math.floorMod(c.semilla / 11, 4) == 1) {
            val g = Path(); val x0 = w * 0.99f; val y0 = f.top + fh * 0.4f
            for (j in 0..8) {
                val p = Offset(x0 - w * 0.17f * j / 8f, y0 + fh * 0.2f * j / 8f + (azar(c.semilla, 320 + j) - 0.5f) * 2.4f * u)
                if (j == 0) g.moveTo(p.x, p.y) else g.lineTo(p.x, p.y)
            }
            drawPath(g, n(Color(0xFF140D08), oscuro, 0f), style = Stroke(1.1f * u))
            translate(0.7f * u, 0.7f * u) { drawPath(g, brillo(oscuro, 0.2f, cerca), style = Stroke(0.5f * u)) }
            val gc = Offset(w * 0.905f, y0 + fh * 0.1f)
            drawRect(sombra(oscuro, 0.45f), Offset(gc.x - w * 0.006f + 1f * u, gc.y - fh * 0.08f + 1f * u), Size(w * 0.012f, fh * 0.16f))
            drawRect(n(HIERRO, oscuro, cerca), Offset(gc.x - w * 0.006f, gc.y - fh * 0.08f), Size(w * 0.012f, fh * 0.16f))
            drawCircle(n(HIERRO_LUZ, oscuro, cerca), 1f * u, gc - Offset(0f, fh * 0.055f))
            drawCircle(n(HIERRO_LUZ, oscuro, cerca), 1f * u, gc + Offset(0f, fh * 0.055f))
        }
        // La cartela de vitela con su marco de latón; o, en el de las ideas sin tema, una tira de vitela atada a la argolla.
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
        if (c.sinTema) {
            val tira = contornoIrregular(Rect(r.left - 2f * u, r.top - 1f * u, r.right + 2f * u, r.bottom + 2f * u), c.semilla, 28, 0.02f)
            translate(1.6f * u, 2.2f * u) { drawPath(tira, sombra(oscuro, 0.4f)) }
            drawPath(tira, if (oscuro) VITELA_NOCHE else VITELA)
            clipPath(tira) {
                drawLine(sombra(oscuro, 0.12f), Offset(r.left, r.top + r.height * 0.52f), Offset(r.right, r.top + r.height * 0.48f), 1f * u)
                drawLine(brillo(oscuro, 0.5f, cerca), Offset(r.left, r.top + r.height * 0.52f + 1f * u), Offset(r.right, r.top + r.height * 0.48f + 1f * u), 0.6f * u)
            }
            val ag = Offset(r.center.x, r.bottom - 3f * u)
            drawCircle(n(ROBLE_OSC, oscuro, 0f), 1.6f * u, ag)
            argolla(roseta, rr, oscuro, cerca)
            val cordel = n(mezcla(VITELA_OSC, ROBLE, 0.45f), oscuro, cerca + 0.1f)
            drawPath(Path().apply { moveTo(ag.x, ag.y); cubicTo(ag.x + w * 0.02f, ag.y + h * 0.04f, roseta.x + w * 0.03f, roseta.y + rr, roseta.x + rr * 0.6f, roseta.y + rr * 1.8f) }, cordel, style = Stroke(1.1f * u))
            drawCircle(cordel, 1.6f * u, Offset(roseta.x + rr * 0.6f, roseta.y + rr * 1.8f))
            drawLine(cordel, Offset(roseta.x + rr * 0.6f, roseta.y + rr * 1.8f), Offset(roseta.x + rr * 0.9f, roseta.y + rr * 2.6f), 1f * u)
        } else {
            cartela(r, oscuro, cerca, cresta = true)
            if (clase == 1) cerradura(Offset(w / 2, h * 0.82f + baja), h * 0.16f, oscuro, cerca)
            else argolla(roseta, rr, oscuro, cerca)
        }
        // Al abrirlo, sube un poco de luz de dentro.
        if (ab > 0f) mancha(Offset(w / 2, arriba), w * 0.4f, if (oscuro) LLAMA.copy(alpha = 0.18f * ab) else luzMueble(0.3f * ab))
    }

    override val rotulo = Rect(0.25f, 0.22f, 0.75f, 0.67f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.goudy
    override val salida = SALIDA
    override val placa = Rect(0.33f, 0.07f, 0.67f, 0.33f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Seda azul adamascada: una retícula de rombos tenue y flores de lis doradas al tresbolillo.
        drawRect(t(DAMASCO, oscuro))
        val p = 44 * u
        var d0 = -h
        while (d0 < w + h) {
            drawLine(Color.White.copy(alpha = 0.05f), Offset(d0, 0f), Offset(d0 + h, h), 1f * u)
            drawLine(Color.White.copy(alpha = 0.05f), Offset(d0 + h, 0f), Offset(d0, h), 1f * u)
            d0 += p
        }
        var y = p / 2; var fila = 0
        while (y < h) {
            var x = if (fila % 2 == 0) p / 2 else p
            while (x < w) { florDeLis(Offset(x, y), 14 * u, ORO.copy(alpha = 0.3f)); x += p }
            y += p / 2; fila++
        }
        // Las paredes del cajón, de roble, con su filete dorado.
        val pl = 16 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r ->
            drawRect(t(ROBLE, oscuro), r.topLeft, r.size)
            vetas(r, ROBLE_OSC.copy(alpha = 0.5f), (r.left + r.top).toInt(), 1f, vertical = r.height > r.width, grosor = 0.6f, ondulacion = 0.4f)
        }
        drawRect(t(ORO, oscuro), Offset(pl, pl), Size(w - 2 * pl, h - 2 * pl), style = Stroke(1.2f * u))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.35f)
    }

    override fun tintaInterior(oscuro: Boolean) = VITELA
    override val pared: Dp = 18.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 30, 0.005f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.7f else 1f
        drawRect(VITELA.copy(alpha = 0.4f))
        // El filete de oro a la izquierda, de punta a punta, con hojas de hiedra azules y rojas a los lados y un rizo en cada extremo.
        val x = 6 * u * g
        val y0 = h * 0.08f; val y1 = h * 0.92f
        drawLine(ORO, Offset(x, y0), Offset(x, y1), 2.2f * u * g)
        drawCircle(ORO, 3 * u * g, Offset(x + 2.5f * u * g, y0 - 2 * u * g), style = Stroke(1.2f * u * g))
        drawCircle(ORO, 3 * u * g, Offset(x + 2.5f * u * g, y1 + 2 * u * g), style = Stroke(1.2f * u * g))
        var y = y0 + 8 * u * g; var k = 0
        while (y < y1 - 4 * u * g) {
            val s = if (k % 2 == 0) 1f else -1f
            val hoja = Offset(x + s * 4 * u * g, y)
            drawLine(TINTA.copy(alpha = 0.5f), Offset(x, y - 2 * u * g), hoja, 0.6f * u)
            drawPath(Path().apply { moveTo(hoja.x, hoja.y - 2.4f * u * g); lineTo(hoja.x + s * 2.6f * u * g, hoja.y + 1.4f * u * g); lineTo(hoja.x - s * 0.6f * u * g, hoja.y + 2 * u * g); close() },
                (if (k % 3 == 0) CARMESI else AZUL).copy(alpha = 0.75f))
            y += 13 * u * g; k++
        }
    }

    override val margenes = MargenesFicha(12.dp, 18.dp, 12.dp, 24.dp, 32.dp, 28.dp)
    override val proporcionFicha = 0.8f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.7f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "hecho" -> selloHecho(AZUL, ORO)
            "convence" -> florDeLis(c, s * 0.92f, ORO)
            "piensa" -> cuadrifolio(c, s * 0.48f, AZUL)
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(CARMESI)
            else -> {}
        }
    }

    /** Una cinta de seda carmesí que hace de registro, con su punta dorada. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(Path().apply { moveTo(c.x - s * 0.16f, c.y - s * 0.5f); lineTo(c.x + s * 0.16f, c.y - s * 0.5f); lineTo(c.x + s * 0.16f, c.y + s * 0.46f); lineTo(c.x, c.y + s * 0.3f); lineTo(c.x - s * 0.16f, c.y + s * 0.46f); close() }, CARMESI)
        drawRect(ORO, Offset(c.x - s * 0.16f, c.y - s * 0.5f), Size(s * 0.32f, s * 0.1f))
    }

    override val desorden = 0f
}
