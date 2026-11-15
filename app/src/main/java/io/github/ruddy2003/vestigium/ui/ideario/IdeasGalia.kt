package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
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
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.TrazosGalia
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraContacto
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Galia
// =====================================================================

/**
 * Galia: el granero sobre pilotes del oppidum (`docs/diseno/temas/galia.md`), como los graneros elevados de la Edad del
 * Hierro: una casita de roble alzada sobre postes, con su tejado de paja y su escalera de tronco con muescas. Arriba, el
 * tejado de paja visto de frente, con las hiladas, la paja vieja y gris de lluvia en lo alto, las varas de avellano que la
 * sujetan cerca del alero y los cabrios cruzados en la cumbre; por detrás sube el humo de una casa del oppidum y, a la
 * derecha, cruza una rama de roble con su bola de muérdago. De día el sol bajo viene de arriba a la izquierda, con nubes y
 * dos golondrinas; de noche, el mismo cielo verde negro con la luna y sus estrellas. El frente del granero está abierto:
 * entre los postes de roble labrados a azuela, los estantes de tablas con su viga, y en cada uno una cesta de mimbre con las
 * tablillas de haya de un tema asomando (más cuanto más guarda; las más llenas tienen al lado un haz de tablillas atado con
 * una correa) y, colgada de una clavija de la viga, la tablilla de haya con su rótulo. Al fondo de cada estante, la pared de
 * zarzo y barro, con un desconchón por donde se ve el zarzo, y a veces un manojo de hierbas colgado, un cubilete de barro o
 * unas espigas de escanda. Lo que no tiene tema va en un costal con la boca enrollada; un estante todavía sin tema es una
 * cesta empezada: el fondo tejido y las varas levantadas, con un haz de mimbres al lado. Al abrir uno, las tablillas suben.
 * Abajo, el piso del granero con la solera y los pilotes, y por debajo se ve lejos la colina del oppidum con su muralla y,
 * de noche, en el horizonte, la fila recta de las hogueras del campamento romano. En el suelo, el molino de mano, un manto a
 * cuadros echado en la solera y la hoguera: de día, la ceniza con su hilo de humo; de noche, encendida, y su luz cálida solo
 * alumbra lo de abajo. Por dentro, la cesta vista desde arriba: el fondo tejido, las paredes, el borde trenzado y, en el
 * fondo, granos, tamo, dos hojas de roble secas, una correa y el estilo de hierro. Las fichas son tablillas de haya de
 * esquinas cortadas a cuchillo, con su marco de haya moteado, el agujero con su correa y una «S» grabada de su dueño.
 */
object IdeasGalia : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del granero
    /** La paja del tejado: su luz, su sombra y la paja vieja de arriba, gris de lluvia. */
    private val PAJA_LUZ = Color(0xFFEEDDA6)
    private val PAJA_OSC = Color(0xFFAA9152)
    private val PAJA_HONDA = Color(0xFF6E5A2E)
    private val PAJA_VIEJA = Color(0xFF8C8670)
    /** El barro de las paredes y el zarzo de avellano que asoma donde se cayó. */
    private val BARRO_PARED = Color(0xFFB59A68)
    private val BARRO_OSC = Color(0xFF86704A)
    private val ZARZO = Color(0xFF6A4E30)
    private val ZARZO_LUZ = Color(0xFF9A7A50)
    /** El roble de los postes y las vigas. */
    private val VIGA = Color(0xFF7A5C3E)
    private val VIGA_LUZ = Color(0xFFA2805C)
    private val VIGA_OSC = Color(0xFF3E2C1A)
    /** Las tablas del estante, más claras. */
    private val TABLA = Color(0xFF9C7D56)
    /** La haya de las tablillas: clara, rosada, con sus motas oscuras. */
    private val HAYA = Color(0xFFD2AC80)
    private val HAYA_LUZ = Color(0xFFEBD3AC)
    private val HAYA_OSC = Color(0xFFA27C55)
    private val HAYA_MOTA = Color(0xFF8A5A36)
    /** La cera de las tablillas, vista de canto en la cesta. */
    private val CERA = Color(0xFFC7A866)
    /** El mimbre pelado, el que conserva la corteza y su sombra. */
    private val MIMBRE = Color(0xFFC8A364)
    private val MIMBRE_LUZ = Color(0xFFE4CB92)
    private val MIMBRE_OSC = Color(0xFF8A6836)
    private val MIMBRE_PARDO = Color(0xFF7A4F2C)
    /** La estopa del costal. */
    private val ESTOPA = Color(0xFFBCA97F)
    private val CUERO = Color(0xFF6B4226)
    private val TINTA = Color(0xFF20251A)
    /** La noche del robledal: el verde negro donde se hunde lo que no alumbra el fuego. */
    private val NOCHE = Color(0xFF0C110D)
    private val TIERRA = Color(0xFF7E6A48)
    private val HIERBA = Color(0xFF6E7D3C)
    private val PIEDRA = Color(0xFF8A8276)
    private val ESCANDA = Color(0xFFC9A55A)
    private val BARRO_COCIDO = Color(0xFF4C4038)
    /** Las hogueras del campamento romano, lejos (el terciario del tema de noche). */
    private val ROMA_LEJOS = Color(0xFFE58A5C)

    /** Un color del granero de noche: hundido en el verde negro (lo que no alumbra la hoguera). */
    private fun n(c: Color, oscuro: Boolean, k: Float = 0.82f) = if (oscuro) mezcla(c, NOCHE, k) else c

    /** Un color a [cerca] (0 a 1) de la hoguera: de día, tal cual; de noche, menos hundido y calentado por el fuego. */
    private fun luz(c: Color, oscuro: Boolean, cerca: Float): Color {
        if (!oscuro) return c
        val k = cerca.coerceIn(0f, 1f)
        return mezcla(mezcla(c, NOCHE, 0.86f - 0.52f * Math.pow(k.toDouble(), 1.3).toFloat()), TrazosGalia.FUEGO, 0.22f * k)
    }

    /** La sombra: de día, violeta hondo, como en los muebles; de noche, el verde negro del robledal. */
    private fun sombra(oscuro: Boolean, a: Float) = if (oscuro) NOCHE.copy(alpha = (a * 1.1f).coerceIn(0f, 1f)) else sombraMueble(a)

    override val cols = 2
    override val proporcion = 1.16f
    override val corona = 0.5f
    override val base = 0.3f
    override val lado = 0.07f
    override val hueco = 0.045f

    /** Dónde acaba el tejado (el alero), en fracción de la corona. */
    private const val ALERO = 0.9f
    /** El fondo del estante (donde empieza la tabla) y su canto (donde empieza la viga), en fracción del alto del compartimento. */
    private const val REPISA = 0.6f
    private const val CANTO = 0.68f
    /** En la base: el canto del piso, la solera y el suelo, en fracción de su alto. */
    private const val PISO = 0.06f
    private const val SOLERA = 0.22f
    private const val SUELO = 0.8f

    // ---------------------------------------------------------------- ayudantes del granero

    /** Un punto de la curva de Bézier cúbica de [a] a [d] en [t]. */
    private fun curva(a: Offset, b: Offset, c: Offset, d: Offset, t: Float): Offset {
        val s = 1f - t
        return a * (s * s * s) + b * (3 * s * s * t) + c * (3 * s * t * t) + d * (t * t * t)
    }

    /** Una clavija de madera clavada de frente: la sombra, la cabeza con su luz y la veta de testa. */
    private fun DrawScope.clavija(p: Offset, r: Float, color: Color, oscuro: Boolean) {
        val u = density
        drawCircle(sombra(oscuro, 0.45f), r * 1.15f, p + Offset(0.5f * u, 0.7f * u))
        drawCircle(color, r, p)
        drawCircle(luzMueble(if (oscuro) 0.06f else 0.4f), r * 0.45f, p - Offset(r * 0.3f, r * 0.3f))
        drawCircle(sombra(oscuro, 0.35f), r * 0.55f, p, style = Stroke(0.5f * u))
    }

    /**
     * Una pieza de roble labrada a azuela dentro de [r]: el color [base] (o su degradado de noche), la luz de un lado y la
     * sombra del otro, la veta, los golpes de la azuela y una grieta de secado. [vertical] si es un poste.
     */
    private fun DrawScope.roble(r: Rect, base: Brush, semilla: Int, oscuro: Boolean, vertical: Boolean) {
        val u = density
        drawRect(base, r.topLeft, r.size)
        if (vertical) drawRect(Brush.horizontalGradient(listOf(luzMueble(if (oscuro) 0.04f else 0.26f), Color.Transparent, sombra(oscuro, if (oscuro) 0.4f else 0.42f)), r.left, r.right), r.topLeft, r.size)
        else drawRect(Brush.verticalGradient(listOf(luzMueble(if (oscuro) 0.03f else 0.22f), Color.Transparent, sombra(oscuro, if (oscuro) 0.4f else 0.38f)), r.top, r.bottom), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, VIGA_OSC.copy(alpha = if (oscuro) 0.45f else 0.35f), semilla, densidad = 0.45f, vertical = vertical, grosor = 0.9f, ondulacion = 0.7f)
            // Los golpes de la azuela: facetas cortas, cada una con su filo en luz y su borde en sombra.
            val largo = if (vertical) r.height else r.width
            val ancho = if (vertical) r.width else r.height
            var s = 4f * u * azar(semilla, 1); var k = 0
            while (s < largo) {
                val l = ancho * (0.18f + 0.3f * azar(semilla, k + 10))
                val a = ancho * (0.05f + (0.9f - l / ancho) * azar(semilla, k + 20))
                fun p(sl: Float, an: Float) = if (vertical) Offset(r.left + an, r.top + sl) else Offset(r.left + sl, r.top + an)
                drawLine(sombra(oscuro, if (oscuro) 0.3f else 0.2f), p(s, a), p(s + 1.2f * u, a + l), 0.8f * u)
                drawLine(luzMueble(if (oscuro) 0.02f else 0.13f), p(s + 1.2f * u, a), p(s + 2.4f * u, a + l), 0.6f * u)
                s += (9f + 15f * azar(semilla, k + 30)) * u; k++
            }
            // Una grieta de secado a lo largo.
            val g = Path()
            val a0 = ancho * (0.3f + 0.4f * azar(semilla, 40))
            val s0 = largo * 0.15f * azar(semilla, 41); val s1 = s0 + largo * (0.25f + 0.35f * azar(semilla, 42))
            for (i in 0..10) {
                val sl = s0 + (s1 - s0) * i / 10f
                val an = a0 + (azar(semilla, 50 + i) - 0.5f) * 1.6f * u
                val q = if (vertical) Offset(r.left + an, r.top + sl) else Offset(r.left + sl, r.top + an)
                if (i == 0) g.moveTo(q.x, q.y) else g.lineTo(q.x, q.y)
            }
            drawPath(g, VIGA_OSC.copy(alpha = if (oscuro) 0.7f else 0.55f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
        }
    }

    /** El contorno de una tablilla de haya: un rectángulo de esquinas cortadas a cuchillo, cada una un poco distinta. */
    private fun contornoTablilla(r: Rect, semilla: Int, corte: Float): Path = Path().apply {
        fun c(k: Int) = corte * (0.7f + 0.6f * azar(semilla, 900 + k))
        val c0 = c(0); val c1 = c(1); val c2 = c(2); val c3 = c(3)
        moveTo(r.left + c0, r.top); lineTo(r.right - c1, r.top); lineTo(r.right, r.top + c1)
        lineTo(r.right, r.bottom - c2); lineTo(r.right - c2, r.bottom); lineTo(r.left + c3, r.bottom)
        lineTo(r.left, r.bottom - c3); lineTo(r.left, r.top + c0); close()
    }

    /** Las motas de la haya (sus radios, cortos y oscuros) dentro de [r], a lo largo de la veta. */
    private fun DrawScope.motas(r: Rect, color: Color, semilla: Int, densidad: Float, vertical: Boolean = false) {
        val u = density
        val cuantas = (r.width * r.height / (u * u * 30f) * densidad).toInt().coerceIn(3, 400)
        for (k in 0 until cuantas) {
            val p = Offset(r.left + r.width * azar(semilla, k), r.top + r.height * azar(semilla + 1, k))
            val l = (0.8f + 1.8f * azar(semilla + 2, k)) * u
            drawLine(color.copy(alpha = color.alpha * (0.4f + 0.6f * azar(semilla + 3, k))), p, if (vertical) p + Offset(0f, l) else p + Offset(l, 0f), 0.7f * u, StrokeCap.Round)
        }
    }

    /**
     * La tablilla de haya del rótulo, colgada de una clavija por la orejuela de arriba: el marco moteado, la veta, el agujero
     * con la clavija y su sombra en la viga. [calor] es cuánto la alumbra la hoguera de noche.
     */
    private fun DrawScope.etiqueta(r: Rect, semilla: Int, oscuro: Boolean, calor: Float) {
        val u = density
        val oreja = r.width * 0.06f
        val clavo = Offset(r.center.x, r.top - oreja * 0.15f)
        val cuerpo = contornoTablilla(r, semilla, 3.4f * u)
        val forma = Path().apply { op(cuerpo, Path().apply { addOval(Rect(Offset(r.center.x, r.top), oreja)) }, PathOperation.Union) }
        val madera = if (oscuro) mezcla(mezcla(HAYA, NOCHE, 0.3f), TrazosGalia.FUEGO, 0.08f + 0.1f * calor) else HAYA
        translate(1.4f * u, 2.2f * u) { drawPath(forma, sombra(oscuro, if (oscuro) 0.55f else 0.32f)) }
        drawPath(forma, madera)
        clipPath(forma) {
            val caja = Rect(r.left, r.top - oreja, r.right, r.bottom)
            drawRect(Brush.verticalGradient(listOf(luzMueble(if (oscuro) 0.04f else 0.3f), Color.Transparent, sombra(oscuro, if (oscuro) 0.25f else 0.14f)), caja.top, caja.bottom), caja.topLeft, caja.size)
            vetas(caja, HAYA_OSC.copy(alpha = 0.35f), semilla + 3, densidad = 0.5f, grosor = 0.7f, ondulacion = 0.4f)
            motas(caja, HAYA_MOTA.copy(alpha = 0.4f), semilla + 5, 0.5f)
            // El filo del marco, rebajado a cuchillo alrededor.
            drawPath(cuerpo, luzMueble(if (oscuro) 0.05f else 0.3f), style = Stroke(2.2f * u))
        }
        drawPath(forma, mezcla(madera, VIGA_OSC, 0.45f), style = Stroke(0.9f * u))
        // El agujero de la orejuela y la clavija que lo atraviesa.
        drawCircle(VIGA_OSC.copy(alpha = 0.85f), oreja * 0.5f, clavo)
        clavija(clavo, oreja * 0.36f, luz(VIGA_LUZ, oscuro, 0.4f + 0.4f * calor), oscuro)
    }

    // ---------------------------------------------------------------- el cielo y lo de lejos

    /** El cielo de las esquinas de arriba: de día, el sol bajo con sus nubes y dos golondrinas; de noche, la luna y sus estrellas. */
    private fun DrawScope.cielo(ch: Float, oscuro: Boolean) {
        val u = density
        val w = size.width
        val bajo = ch * ALERO
        drawRect(Brush.verticalGradient(if (oscuro) listOf(Color(0xFF060A07), Color(0xFF17221A)) else listOf(Color(0xFFB4C7C0), Color(0xFFEDE7D0)), 0f, bajo),
            Offset.Zero, Size(w, bajo + 2 * u))
        if (!oscuro) {
            // El sol bajo, arriba a la izquierda (de ahí viene la luz), con su calima.
            mancha(Offset(w * 0.03f, ch * 0.06f), w * 0.45f, Color(0xFFFFF0C8).copy(alpha = 0.75f))
            mancha(Offset(w * 0.03f, ch * 0.06f), w * 0.09f, Color(0xFFFFFAEA))
            nube(Offset(w * 0.22f, ch * 0.3f), w * 0.15f, false, 1)
            nube(Offset(w * 0.86f, ch * 0.14f), w * 0.12f, false, 2)
            pinceladas(Rect(0f, 0f, w, bajo), Color.White, 501, 14, u, 0.14f)
            // Dos golondrinas.
            listOf(Offset(w * 0.17f, ch * 0.2f) to 1f, Offset(w * 0.27f, ch * 0.13f) to 0.75f).forEach { (p, e) ->
                val a = 6f * u * e
                val ave = Path().apply {
                    moveTo(p.x - a, p.y - a * 0.1f); quadraticTo(p.x - a * 0.5f, p.y - a * 0.55f, p.x, p.y)
                    quadraticTo(p.x + a * 0.5f, p.y - a * 0.6f, p.x + a * 1.05f, p.y - a * 0.25f)
                }
                drawPath(ave, Color(0xFF2B2F2A), style = Stroke(1.3f * u * e, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawLine(Color(0xFF2B2F2A), p, p + Offset(-a * 0.15f, a * 0.35f), 0.9f * u * e, StrokeCap.Round)
            }
        } else {
            // Las estrellas.
            for (k in 0 until 60) {
                val p = Offset(w * azar(503, k), bajo * 0.8f * azar(504, k))
                val t = (0.35f + 0.85f * azar(505, k) * azar(506, k)) * u
                drawCircle(TrazosGalia.LUNA.copy(alpha = 0.35f + 0.55f * azar(507, k)), t, p)
            }
            // La luna, con su halo y sus mares, arriba a la izquierda (de ahí viene su luz fría).
            val lc = Offset(w * 0.14f, ch * 0.2f); val lr = w * 0.042f
            mancha(lc, lr * 5f, TrazosGalia.LUNA.copy(alpha = 0.13f))
            mancha(lc, lr * 2.2f, TrazosGalia.LUNA.copy(alpha = 0.18f))
            drawCircle(TrazosGalia.LUNA, lr, lc)
            listOf(Offset(-0.3f, -0.2f) to 0.32f, Offset(0.25f, 0.1f) to 0.24f, Offset(-0.05f, 0.38f) to 0.18f).forEach { (o, rr) ->
                drawCircle(Color(0xFF8E9284).copy(alpha = 0.3f), lr * rr, lc + o * lr)
            }
            drawCircle(sombra(oscuro, 0.12f), lr, lc + Offset(lr * 0.12f, lr * 0.1f), style = Stroke(lr * 0.18f))
            nube(Offset(w * 0.2f, ch * 0.3f), w * 0.15f, true, 1)
            nube(Offset(w * 0.86f, ch * 0.12f), w * 0.11f, true, 2)
        }
    }

    /** Una nube de [ancho], plana por abajo: de día blanca con su sombra; de noche gris verde, con el filo de la luna arriba. */
    private fun DrawScope.nube(c: Offset, ancho: Float, oscuro: Boolean, semilla: Int) {
        val cuerpo = if (oscuro) Color(0xFF26302A) else Color.White
        for (k in 0 until 6) {
            val f = k / 5f
            val rx = ancho * (0.22f + 0.16f * azar(semilla, k)) * (1f - 0.35f * kotlin.math.abs(f - 0.5f))
            val ry = rx * (0.5f + 0.2f * azar(semilla, k + 10))
            val cx = c.x - ancho / 2 + ancho * f
            val cy = c.y - ry * 0.55f
            if (oscuro) drawOval(TrazosGalia.LUNA.copy(alpha = 0.08f), Offset(cx - rx, cy - ry - 1.2f * density), Size(rx * 2, ry * 2))
            drawOval(cuerpo.copy(alpha = if (oscuro) 0.75f else 0.62f), Offset(cx - rx, cy - ry), Size(rx * 2, ry * 2))
        }
        drawOval(sombra(oscuro, if (oscuro) 0.2f else 0.07f), Offset(c.x - ancho * 0.55f, c.y - ancho * 0.03f), Size(ancho * 1.1f, ancho * 0.1f))
    }

    /** Un hilo de humo que sube de [desde] y se tuerce y se abre hacia [hasta]. */
    private fun DrawScope.humo(desde: Offset, hasta: Offset, ancho: Float, color: Color, alfa: Float, semilla: Int) {
        val c1 = Offset(desde.x + (hasta.x - desde.x) * 0.1f, desde.y + (hasta.y - desde.y) * 0.45f)
        val c2 = Offset(hasta.x + (desde.x - hasta.x) * 0.35f, desde.y + (hasta.y - desde.y) * 0.7f)
        for (i in 0..40) {
            val t = i / 40f
            val p = curva(desde, c1, c2, hasta, t) + Offset((azar(semilla, i) - 0.5f) * ancho * 0.5f * t, 0f)
            val r = ancho * (0.25f + 0.9f * t)
            mancha(p, r, color.copy(alpha = alfa * 0.8f * (1f - 0.8f * t)))
        }
    }

    /**
     * Lo que se ve por debajo del granero, entre los pilotes: los montes lejanos, la colina del oppidum con su muralla, sus
     * techos y su humo, y el prado; de noche, las hogueras del oppidum y, en el horizonte, la fila recta del campamento romano.
     */
    private fun DrawScope.lejania(r: Rect, oscuro: Boolean) {
        val u = density
        val w = size.width
        val hz = r.top + r.height * 0.52f
        drawRect(Brush.verticalGradient(if (oscuro) listOf(Color(0xFF0E1611), Color(0xFF1B271E)) else listOf(Color(0xFFD5DCCB), Color(0xFFEFE8D2)), r.top, hz),
            r.topLeft, Size(r.width, hz - r.top + 1 * u))
        drawRect(if (oscuro) Color(0xFF111A13) else Color(0xFF9FA56C), Offset(r.left, hz), Size(r.width, r.bottom - hz))
        // Los montes lejanos, en la calima (o casi negros de noche).
        val lejos = Path().apply {
            moveTo(r.left, hz + 1 * u)
            for (k in 0..14) lineTo(r.left + r.width * k / 14f, hz - r.height * (0.05f + 0.1f * azar(401, k)))
            lineTo(r.right, hz + 1 * u); close()
        }
        drawPath(lejos, if (oscuro) Color(0xFF131D16) else Color(0xFFAEB7A8))
        // El campamento romano, lejos a la derecha: la línea recta de su valla; de noche, la fila de sus hogueras.
        val cx0 = w * 0.54f; val cx1 = w * 0.7f
        val cy = hz - r.height * 0.05f
        if (oscuro) {
            for (k in 0..8) {
                val p = Offset(cx0 + (cx1 - cx0) * k / 8f, cy)
                mancha(p, 3.2f * u, ROMA_LEJOS.copy(alpha = 0.5f))
                drawCircle(ROMA_LEJOS, 0.75f * u, p)
            }
        } else {
            drawLine(Color(0xFF8D948A), Offset(cx0, cy + 0.8f * u), Offset(cx1, cy + 0.8f * u), 0.8f * u)
            for (k in 0..10) drawRect(Color(0xFFD9D6C6), Offset(cx0 + (cx1 - cx0) * k / 10f - 0.9f * u, cy - 1.4f * u), Size(1.8f * u, 1.4f * u))
        }
        // La colina del oppidum, más cerca, a la izquierda: la meseta con su muralla, sus techos de paja y su humo.
        val pl0 = w * 0.18f; val pl1 = w * 0.44f
        val cima = hz - r.height * 0.24f
        val colina = Path().apply {
            moveTo(r.left, hz + r.height * 0.12f)
            cubicTo(w * 0.08f, hz, pl0 - w * 0.06f, cima, pl0, cima)
            lineTo(pl1, cima + 1 * u)
            cubicTo(pl1 + w * 0.07f, cima, w * 0.56f, hz + r.height * 0.02f, w * 0.62f, hz + r.height * 0.1f)
            lineTo(w * 0.62f, r.bottom); lineTo(r.left, r.bottom); close()
        }
        drawPath(colina, if (oscuro) Color(0xFF0F1813) else Color(0xFF8A9360))
        if (!oscuro) drawPath(colina, Brush.horizontalGradient(listOf(luzMueble(0.25f), Color.Transparent), pl0 - w * 0.08f, pl1), style = Stroke(1.2f * u))
        // Los techos de paja, detrás de la muralla, con su humo.
        for (k in 0 until 4) {
            val x = pl0 + (pl1 - pl0) * (0.12f + 0.24f * k + 0.06f * azar(411, k))
            val a = (3.2f + 1.6f * azar(412, k)) * u
            drawPath(Path().apply { moveTo(x - a, cima); lineTo(x, cima - a * 0.95f); lineTo(x + a, cima); close() }, if (oscuro) Color(0xFF1C241B) else Color(0xFFB59E64))
            if (k % 2 == 0) humo(Offset(x, cima - a), Offset(x - w * 0.03f, r.top - r.height * 0.1f), 2.6f * u,
                if (oscuro) Color(0xFF39433A) else TrazosGalia.HUMO, if (oscuro) 0.22f else 0.38f, 413 + k)
        }
        // El murus gallicus: la cara de piedra con las cabezas de las vigas, a lo largo de la meseta.
        drawRect(if (oscuro) Color(0xFF26302A) else Color(0xFFC9BE9C), Offset(pl0 - 2 * u, cima - 0.6f * u), Size(pl1 - pl0 + 4 * u, 2.6f * u))
        var x = pl0
        while (x < pl1) { drawRect(if (oscuro) Color(0xFF0B110D) else Color(0xFF5E513A), Offset(x, cima + 0.2f * u), Size(0.8f * u, 0.8f * u)); x += 3.2f * u }
        if (oscuro) for (k in 0 until 3) {
            val p = Offset(pl0 + (pl1 - pl0) * (0.2f + 0.33f * k + 0.1f * azar(421, k)), cima - 1.6f * u)
            mancha(p, 4f * u, TrazosGalia.FUEGO.copy(alpha = 0.4f))
            drawCircle(TrazosGalia.FUEGO_HALO, 0.8f * u, p)
        }
        // El prado y un seto, delante de todo lo lejano.
        val prado = Path().apply {
            moveTo(r.left, hz + r.height * 0.18f)
            cubicTo(w * 0.3f, hz + r.height * 0.1f, w * 0.6f, hz + r.height * 0.22f, r.right, hz + r.height * 0.12f)
            lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); close()
        }
        drawPath(prado, Brush.verticalGradient(if (oscuro) listOf(Color(0xFF111A13), Color(0xFF0D140F)) else listOf(Color(0xFF9DA463), Color(0xFF7F8A4C)), hz, r.bottom))
        for (k in 0 until 18) {
            val p = Offset(w * azar(431, k), hz + r.height * (0.16f + 0.05f * azar(432, k)))
            val rr = (2.4f + 3f * azar(433, k)) * u
            drawOval(if (oscuro) Color(0xFF0A110C) else Color(0xFF5E6B33), Offset(p.x - rr, p.y - rr * 0.7f), Size(rr * 2, rr * 1.2f))
        }
        if (!oscuro) pinceladas(Rect(r.left, hz, r.right, r.bottom), luzMueble(1f), 435, 16, u, 0.08f)
    }

    // ---------------------------------------------------------------- el tejado

    /** Los cabrios que se cruzan sobre la cumbre del tejado (se dibujan antes: la paja tapa su arranque). */
    private fun DrawScope.cabrios(ch: Float, oscuro: Boolean) {
        val u = density
        val w = size.width
        val cima = Offset(w * 0.5f, ch * 0.1f)
        listOf(-1f, 1f).forEach { s ->
            val a = Offset(cima.x - s * w * 0.07f, cima.y + ch * 0.12f)
            val b = Offset(cima.x + s * w * 0.035f, cima.y - ch * 0.09f)
            drawLine(sombra(oscuro, 0.35f), a + Offset(1.2f * u, 1.2f * u), b + Offset(1.2f * u, 1.2f * u), 4.2f * u, StrokeCap.Round)
            drawLine(n(VIGA, oscuro, 0.78f), a, b, 4f * u, StrokeCap.Round)
            drawLine(if (oscuro) TrazosGalia.LUNA.copy(alpha = 0.25f) else luzMueble(0.45f), a + Offset(-1.2f * u, 0f), b + Offset(-1.2f * u, 0f), 1f * u, StrokeCap.Round)
            drawCircle(n(VIGA_LUZ, oscuro, 0.75f), 2f * u, b)
        }
    }

    /**
     * El tejado de paja visto de frente: la paja nueva abajo y la vieja arriba, gris de lluvia, con sus hebras, las hiladas, el
     * musgo, las varas de avellano que la sujetan cerca del alero, el canto cortado del alero y la sombra que echa abajo.
     */
    private fun DrawScope.tejado(ch: Float, oscuro: Boolean) {
        val u = density
        val w = size.width
        val alero = ch * ALERO
        val cima = Offset(w * 0.5f, ch * 0.1f)
        val izq = Offset(-w * 0.06f, alero); val der = Offset(w * 1.06f, alero)
        val forma = Path().apply {
            moveTo(izq.x, izq.y)
            quadraticTo(w * 0.17f, ch * 0.42f, cima.x, cima.y)
            quadraticTo(w * 0.83f, ch * 0.42f, der.x, der.y)
            close()
        }
        // La sombra del tejado sobre la viga de arriba del frente.
        val ceja1 = alero + ch * 0.06f
        drawRect(Brush.verticalGradient(listOf(sombra(oscuro, if (oscuro) 0.7f else 0.55f), Color.Transparent), ceja1 - 2 * u, ceja1 + ch * 0.1f),
            Offset(0f, ceja1 - 2 * u), Size(w, ch * 0.1f + 2 * u))
        drawPath(forma, Brush.linearGradient(listOf(n(PAJA_LUZ, oscuro, 0.8f), n(TrazosGalia.PAJA, oscuro, 0.82f), n(PAJA_OSC, oscuro, 0.84f)),
            Offset(w * 0.1f, ch * 0.2f), Offset(w * 0.9f, alero)))
        clipPath(forma) {
            // La paja vieja de arriba, gris de lluvia.
            drawRect(Brush.verticalGradient(listOf(n(PAJA_VIEJA, oscuro, 0.8f), n(PAJA_VIEJA, oscuro, 0.8f).copy(alpha = 0f)), cima.y, ch * 0.6f), Offset.Zero, Size(w, ch * 0.6f))
            // Las hebras: cada una baja desde la cumbre.
            val hebras = (w * alero / (u * u * 24f)).toInt().coerceIn(200, 1800)
            for (k in 0 until hebras) {
                val p = Offset(w * azar(511, k), cima.y + (alero - cima.y) * azar(512, k))
                val d = p - cima; val l = d.getDistance().coerceAtLeast(1f)
                val largo = (4f + 7f * azar(513, k)) * u
                val col = if (azar(514, k) > 0.42f) n(PAJA_LUZ, oscuro, 0.76f).copy(alpha = 0.45f) else n(PAJA_HONDA, oscuro, 0.85f).copy(alpha = 0.22f)
                drawLine(col, p, p + d * (largo / l), (0.5f + 0.5f * azar(515, k)) * u, StrokeCap.Round)
            }
            // Las hiladas, más anchas hacia abajo: el canto de cada una en luz y su sombra debajo.
            var y = cima.y + ch * 0.1f; var j = 0
            while (y < alero - ch * 0.05f) {
                val linea = Path()
                var x = -w * 0.06f; var i = 0
                linea.moveTo(x, y)
                while (x < w * 1.06f) { x += 2.6f * u; linea.lineTo(x, y + (azar(521 + j, i) - 0.5f) * 2.2f * u + 0.8f * u * sin(i * 0.7f + j)); i++ }
                translate(0f, 1.6f * u) { drawPath(linea, sombra(oscuro, if (oscuro) 0.35f else 0.16f), style = Stroke(2.8f * u)) }
                drawPath(linea, if (oscuro) TrazosGalia.LUNA.copy(alpha = 0.05f) else luzMueble(0.3f), style = Stroke(0.9f * u))
                // Las puntas de la hilada que cuelgan sobre la de abajo.
                for (q in 0 until (w / (4f * u)).toInt()) {
                    val xq = w * 1.1f * azar(526 + j, q) - w * 0.05f
                    drawLine(if (q % 2 == 0) n(PAJA_LUZ, oscuro, 0.78f).copy(alpha = 0.55f) else n(PAJA_HONDA, oscuro, 0.85f).copy(alpha = 0.3f),
                        Offset(xq, y), Offset(xq + 0.3f * u, y + (2f + 3f * azar(527 + j, q)) * u), 0.6f * u, StrokeCap.Round)
                }
                y += (8.5f + 4f * azar(522, j)) * u * (1f + (y - cima.y) / ch * 0.6f); j++
            }
            // El musgo en la paja vieja de la izquierda.
            pinceladas(Rect(w * 0.06f, ch * 0.5f, w * 0.38f, alero), n(Color(0xFF6C7A40), oscuro, 0.8f), 531, 14, u, if (oscuro) 0.12f else 0.2f)
            // Las varas de avellano que sujetan la paja cerca del alero, con sus horquillas en aspa.
            val y1 = alero - ch * 0.08f; val y2 = alero - ch * 0.17f
            val vara = n(ZARZO, oscuro, 0.78f)
            val brillo = if (oscuro) TrazosGalia.LUNA.copy(alpha = 0.16f) else luzMueble(0.5f)
            val paso = 15f * u
            var xs = -w * 0.06f
            while (xs < w * 1.06f) {
                drawLine(sombra(oscuro, 0.25f), Offset(xs + 1 * u, y2 + 1.2f * u), Offset(xs + paso + 1 * u, y1 + 1.2f * u), 1.4f * u, StrokeCap.Round)
                drawLine(vara, Offset(xs, y2), Offset(xs + paso, y1), 1.2f * u, StrokeCap.Round)
                drawLine(vara, Offset(xs + paso, y2), Offset(xs, y1), 1.2f * u, StrokeCap.Round)
                xs += paso
            }
            listOf(y1, y2).forEach { yy ->
                drawLine(sombra(oscuro, if (oscuro) 0.45f else 0.3f), Offset(-w * 0.06f, yy + 1.6f * u), Offset(w * 1.06f, yy + 1.6f * u), 2.4f * u)
                drawLine(vara, Offset(-w * 0.06f, yy), Offset(w * 1.06f, yy), 2.2f * u)
                drawLine(brillo, Offset(-w * 0.06f, yy - 0.7f * u), Offset(w * 1.06f, yy - 0.7f * u), 0.7f * u)
            }
        }
        // El filo de la luz (el sol o la luna) en la caída de la izquierda; la de la derecha, en sombra.
        val izquierda = Path().apply { moveTo(izq.x, izq.y); quadraticTo(w * 0.17f, ch * 0.42f, cima.x, cima.y) }
        val derecha = Path().apply { moveTo(cima.x, cima.y); quadraticTo(w * 0.83f, ch * 0.42f, der.x, der.y) }
        drawPath(izquierda, if (oscuro) TrazosGalia.LUNA.copy(alpha = 0.4f) else luzMueble(0.7f), style = Stroke(1.6f * u))
        drawPath(derecha, sombra(oscuro, if (oscuro) 0.5f else 0.32f), style = Stroke(1.6f * u))
        // El alero: el canto de la paja cortada, con las puntas que cuelgan desparejas.
        val a0 = alero - 2f * u
        val ceja = Path().apply {
            moveTo(-w * 0.06f, a0); lineTo(w * 1.06f, a0); lineTo(w * 1.06f, ceja1)
            var x = w * 1.06f; var i = 0
            while (x > -w * 0.06f) { x -= 2.5f * u; lineTo(x, ceja1 + (azar(541, i) * 3.2f - 1f) * u); i++ }
            close()
        }
        drawPath(ceja, Brush.verticalGradient(listOf(n(TrazosGalia.PAJA, oscuro, 0.8f), n(PAJA_OSC, oscuro, 0.86f)), a0, ceja1))
        clipPath(ceja) {
            val puntas = (w / (1.3f * u)).toInt()
            for (k in 0 until puntas) {
                val x = w * 1.1f * azar(543, k) - w * 0.05f
                val y = a0 + (ceja1 - a0) * 0.8f * azar(544, k)
                drawLine(if (k % 2 == 0) n(PAJA_LUZ, oscuro, 0.78f).copy(alpha = 0.7f) else n(PAJA_HONDA, oscuro, 0.85f).copy(alpha = 0.5f),
                    Offset(x, y), Offset(x + 0.4f * u, y + (3f + 4f * azar(545, k)) * u), 0.7f * u, StrokeCap.Round)
            }
        }
        drawLine(if (oscuro) TrazosGalia.LUNA.copy(alpha = 0.18f) else luzMueble(0.6f), Offset(-w * 0.06f, a0), Offset(w * 1.06f, a0), 1.1f * u)
    }

    /** La rama de roble que cruza la esquina de arriba a la derecha, con su bola de muérdago colgando. */
    private fun DrawScope.rama(ch: Float, oscuro: Boolean) {
        val u = density
        val w = size.width
        val p0 = Offset(w * 1.03f, ch * 0.0f); val p1 = Offset(w * 0.92f, ch * 0.06f)
        val p2 = Offset(w * 0.78f, ch * 0.3f); val p3 = Offset(w * 0.63f, ch * 0.3f)
        val corteza = n(Color(0xFF4A3A26), oscuro, 0.72f)
        // La rama: un trazo que se adelgaza, con su sombra sobre el tejado (de día).
        fun tramo(dx: Float, dy: Float, color: Color, extra: Float) {
            for (i in 0 until 24) {
                val a = curva(p0, p1, p2, p3, i / 24f); val b = curva(p0, p1, p2, p3, (i + 1) / 24f)
                drawLine(color, a + Offset(dx, dy), b + Offset(dx, dy), (7.5f - 5.5f * i / 24f) * u + extra, StrokeCap.Round)
            }
        }
        if (!oscuro) tramo(5 * u, 8 * u, sombra(oscuro, 0.14f), 1f * u)
        tramo(0f, 0f, corteza, 0f)
        tramo(-1.2f * u, -1.2f * u, if (oscuro) TrazosGalia.LUNA.copy(alpha = 0.12f) else luzMueble(0.22f), -4f * u)
        // Las ramitas y las hojas: las de atrás en sombra, las de delante a la luz.
        val hojaAtras = n(TrazosGalia.ROBLE_SOMBRA, oscuro, 0.66f)
        val hojaDelante = n(TrazosGalia.ROBLE, oscuro, 0.72f)
        val nervio = n(TrazosGalia.ROBLE_SOMBRA, oscuro, 0.8f)
        val puntos = listOf(0.2f, 0.36f, 0.52f, 0.68f, 0.84f, 1f)
        puntos.forEachIndexed { i, t ->
            val a = curva(p0, p1, p2, p3, t)
            val ang = (if (i % 2 == 0) 70f else 130f) + 30f * azar(551, i)
            val l = w * (0.04f + 0.03f * azar(552, i))
            val b = a + Offset(cos(Math.toRadians(ang.toDouble())).toFloat() * l, sin(Math.toRadians(ang.toDouble())).toFloat() * l)
            drawLine(corteza, a, b, 1.6f * u, StrokeCap.Round)
            for (j in 0 until 3) {
                val c = b + Offset((azar(553, i * 3 + j) - 0.5f) * l * 0.8f, (azar(554, i * 3 + j) - 0.3f) * l * 0.7f)
                val delante = (i + j) % 2 == 0
                val giro = ang - 90f + (azar(555, i * 3 + j) - 0.5f) * 80f
                with(TrazosGalia) { hojaRoble(c, w * (0.045f + 0.025f * azar(556, i * 3 + j)), giro, if (delante) hojaDelante else hojaAtras, nervio) }
            }
        }
        // La bola de muérdago, colgada de la rama: ramitas que se ahorquillan hacia todos lados.
        val m = curva(p0, p1, p2, p3, 0.46f) + Offset(0f, ch * 0.08f)
        drawLine(corteza, curva(p0, p1, p2, p3, 0.46f), m, 1.4f * u)
        val hoja = n(TrazosGalia.MUERDAGO, oscuro, 0.7f)
        val baya = if (oscuro) mezcla(TrazosGalia.BAYA, NOCHE, 0.35f) else TrazosGalia.BAYA
        if (!oscuro) drawCircle(sombra(oscuro, 0.12f), w * 0.05f, m + Offset(5 * u, 7 * u))
        // La masa de la bola, en sombra, y encima las ramitas: las de atrás más oscuras y sin bayas, las de delante con ellas.
        drawCircle(mezcla(hoja, NOCHE, if (oscuro) 0.4f else 0.45f), w * 0.034f, m)
        for (pasada in 0..1) for (k in 0 until 11) {
            val a = 6.28f * azar(557 + pasada, k)
            val rr = w * 0.03f * sqrt(azar(558 + pasada, k))
            val c = m + Offset(cos(a) * rr, sin(a) * rr * 0.9f)
            val g = 360f * azar(559 + pasada, k)
            val tam = w * (0.028f + 0.014f * azar(560 + pasada, k))
            if (pasada == 0) with(TrazosGalia) { muerdago(c, tam, g, mezcla(hoja, NOCHE, 0.3f), mezcla(hoja, NOCHE, 0.3f)) }
            else with(TrazosGalia) { muerdago(c, tam, g, hoja, if (k % 3 == 0) baya else hoja) }
        }
        // El filo de la luz arriba a la izquierda de la bola.
        drawArc(if (oscuro) TrazosGalia.LUNA.copy(alpha = 0.22f) else luzMueble(0.35f), 190f, 80f, false, m - Offset(w * 0.04f, w * 0.04f), Size(w * 0.08f, w * 0.08f), style = Stroke(1.2f * u))
    }

    // ---------------------------------------------------------------- el mueble: el granero entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val alto = size.height
        val ch = d.corona.height
        val b = d.base; val hb = b.height
        val alero = ch * ALERO
        val piso = b.top + hb * PISO
        val bajo = b.top + hb * SOLERA
        val suelo = b.top + hb * SUELO
        // La hoguera, en el suelo, a la derecha: de noche, lo único cálido; alumbra lo de abajo y se apaga hacia arriba.
        val fuego = Offset(w * 0.75f, suelo + hb * 0.12f)
        val alcance = w * 0.95f
        fun calor(p: Offset) = (1f - (p - fuego).getDistance() / alcance).coerceIn(0f, 1f)
        fun c(col: Color, p: Offset) = luz(col, oscuro, calor(p))
        /** El color de una pieza larga, de arriba abajo: de noche se calienta al acercarse a la hoguera. */
        fun largo(col: Color, x: Float, y0: Float, y1: Float): Brush =
            if (!oscuro) Brush.verticalGradient(listOf(col, col), y0, y1) else Brush.verticalGradient(List(6) { i -> c(col, Offset(x, y0 + (y1 - y0) * i / 5f)) }, y0, y1)

        // Los postes: los de las esquinas y uno entre cada columna de estantes; abajo siguen como pilotes.
        val x0 = d.huecos.first().left
        val postes = listOf(0f to x0 + 1 * u) + (0 until cols - 1).map { i -> d.huecos[i].right - 1 * u to d.huecos[i + 1].left + 1 * u } + listOf(w - x0 - 1 * u to w)

        // El cielo, el humo de una casa del oppidum que sube detrás del tejado, y lo de lejos, por debajo del granero.
        cielo(ch, oscuro)
        humo(Offset(w * 0.2f, ch * 0.6f), Offset(w * 0.05f, -ch * 0.08f), w * 0.05f, if (oscuro) Color(0xFF59625A) else TrazosGalia.HUMO, if (oscuro) 0.2f else 0.42f, 561)
        lejania(Rect(0f, bajo, w, suelo + hb * 0.04f), oscuro)

        // El frente: la pared de barro en sombra que se ve entre los estantes, con la sombra de cada viga encima.
        drawRect(largo(mezcla(BARRO_PARED, SOMBRA_MUEBLE, 0.5f), w / 2, alero, b.top), Offset(0f, alero), Size(w, b.top - alero))
        d.filas.forEachIndexed { i, f ->
            val y1 = d.filas.getOrNull(i + 1)?.top ?: b.top
            drawRect(Brush.verticalGradient(listOf(c(mezcla(BARRO_OSC, VIGA_OSC, 0.6f), Offset(w / 2, f.bottom)), c(BARRO_OSC, Offset(w / 2, y1))), f.bottom, y1), Offset(0f, f.bottom), Size(w, y1 - f.bottom))
            pinceladas(Rect(0f, f.bottom, w, y1), BARRO_PARED, 640 + i, 12, u, if (oscuro) 0.03f else 0.1f)
        }
        // Los postes de roble, labrados a azuela.
        val arriba = alero + ch * 0.03f
        postes.forEachIndexed { i, (xa, xb) ->
            val r = Rect(xa, arriba, xb, b.top)
            roble(r, largo(VIGA, r.center.x, r.top, r.bottom), 600 + 7 * i, oscuro, vertical = true)
            if (oscuro) drawLine(TrazosGalia.LUNA.copy(alpha = 0.1f), Offset(xa + 0.6f * u, r.top), Offset(xa + 0.6f * u, r.bottom), 1f * u)
            // Las clavijas donde cada estante se ensambla en el poste.
            d.filas.forEach { f ->
                val y = f.top + f.height * (CANTO + (1f - CANTO) * 0.5f)
                clavija(Offset((xa + xb) / 2, y), 1.8f * u, c(VIGA_LUZ, Offset(xa, y)), oscuro)
            }
        }
        // La viga de arriba del frente, bajo el alero, con una hilera de eses grabadas.
        val solera = Rect(0f, arriba, w, d.huecos.first().top)
        roble(solera, largo(VIGA, w / 2, solera.top, solera.bottom), 650, oscuro, vertical = false)
        val yEse = solera.top + solera.height * 0.6f
        var xe = w * 0.14f
        while (xe < w * 0.9f) {
            with(TrazosGalia) {
                esse(Offset(xe + 0.6f * u, yEse + 0.7f * u), solera.height * 0.8f, 1.5f * u, luzMueble(if (oscuro) 0.04f else 0.25f))
                esse(Offset(xe, yEse), solera.height * 0.8f, 1.5f * u, VIGA_OSC.copy(alpha = if (oscuro) 0.7f else 0.55f))
            }
            xe += solera.height * 1.45f
        }
        postes.forEach { (xa, xb) -> clavija(Offset((xa + xb) / 2, solera.top + solera.height * 0.62f), 1.9f * u, c(VIGA_LUZ, Offset(xa, solera.top)), oscuro) }

        // El tejado, con los cabrios que se cruzan arriba y la rama de roble que cruza delante.
        cabrios(ch, oscuro)
        tejado(ch, oscuro)
        rama(ch, oscuro)

        // ---- la base: el suelo, los pilotes, el piso, la escalera y lo que hay abajo
        // El suelo de tierra apisonada.
        drawRect(Brush.verticalGradient(listOf(c(mezcla(TIERRA, LUZ_MUEBLE, 0.12f), Offset(fuego.x, suelo)), c(TIERRA, Offset(fuego.x, alto))), suelo, alto), Offset(0f, suelo), Size(w, alto - suelo))
        pinceladas(Rect(0f, suelo, w, alto), if (oscuro) TrazosGalia.FUEGO else LUZ_MUEBLE, 571, 18, u, if (oscuro) 0.05f else 0.12f)
        for (k in 0 until 18) {
            val p = Offset(w * azar(572, k), suelo + (alto - suelo) * (0.2f + 0.75f * azar(573, k)))
            val r = (0.8f + 1.8f * azar(574, k)) * u
            drawOval(c(PIEDRA, p), Offset(p.x - r, p.y - r * 0.6f), Size(2 * r, 1.2f * r))
            drawOval(luzMueble(if (oscuro) 0.04f else 0.3f), Offset(p.x - r * 0.6f, p.y - r * 0.55f), Size(r, 0.5f * r))
        }
        // Los pilotes: troncos redondos con su corteza, calzados con piedras al pie.
        postes.forEachIndexed { i, (xa, xb) ->
            val ex = w * 0.006f
            val r = Rect(xa - ex, bajo - 2 * u, xb + ex, suelo + hb * 0.07f)
            sombraContacto(Offset(r.center.x + 2 * u, r.bottom), r.width * 1.5f, 4 * u, if (oscuro) 0.5f else 0.35f)
            drawRect(largo(VIGA, r.center.x, r.top, r.bottom), r.topLeft, r.size)
            drawRect(Brush.horizontalGradient(listOf(luzMueble(if (oscuro) 0.04f else 0.3f), Color.Transparent, sombra(oscuro, if (oscuro) 0.5f else 0.5f)), r.left, r.right), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) {
                // La corteza, en tiras que corren hacia abajo.
                for (k in 0 until 9) {
                    val x = r.left + r.width * azar(581 + i, k)
                    val y0 = r.top + r.height * azar(582 + i, k)
                    drawLine(VIGA_OSC.copy(alpha = 0.55f), Offset(x, y0), Offset(x + (azar(583 + i, k) - 0.5f) * u, y0 + r.height * (0.15f + 0.3f * azar(584 + i, k))), (1f + 1.4f * azar(585 + i, k)) * u, StrokeCap.Round)
                }
            }
            for (k in 0 until 3) {
                val p = Offset(r.left + r.width * (0.05f + 0.45f * k), r.bottom - 1 * u)
                val pr = (2.4f + 1.4f * azar(586 + i, k)) * u
                drawOval(c(PIEDRA, p), Offset(p.x - pr, p.y - pr * 0.6f), Size(pr * 2, pr * 1.2f))
                drawOval(luzMueble(if (oscuro) 0.04f else 0.3f), Offset(p.x - pr * 0.7f, p.y - pr * 0.6f), Size(pr, pr * 0.5f))
            }
        }
        // La sombra del piso sobre lo de abajo.
        drawRect(Brush.verticalGradient(listOf(sombra(oscuro, if (oscuro) 0.6f else 0.42f), Color.Transparent), bajo, bajo + hb * 0.16f), Offset(0f, bajo), Size(w, hb * 0.16f))
        // El canto del piso: las cabezas de las tablas, una al lado de otra.
        var xt = 0f; var kt = 0
        while (xt < w) {
            val tw = w * (0.05f + 0.03f * azar(591, kt))
            val r = Rect(xt, b.top, xt + tw - 0.8f * u, piso)
            drawRect(c(mezcla(TABLA, LUZ_MUEBLE, 0.15f), r.center), r.topLeft, r.size)
            drawRect(sombra(oscuro, 0.3f), Offset(r.right, r.top), Size(0.8f * u, r.height))
            drawOval(VIGA_OSC.copy(alpha = 0.25f), Offset(r.center.x - r.width * 0.2f, r.top + r.height * 0.25f), Size(r.width * 0.4f, r.height * 0.5f), style = Stroke(0.5f * u))
            xt += tw; kt++
        }
        // La solera: la viga grande del piso, con sus clavijas sobre cada pilote.
        val sol = Rect(0f, piso, w, bajo)
        roble(sol, largo(VIGA, fuego.x, sol.top, sol.bottom), 661, oscuro, vertical = false)
        postes.forEach { (xa, xb) ->
            listOf(0.35f, 0.7f).forEach { f -> clavija(Offset((xa + xb) / 2, sol.top + sol.height * f), 1.9f * u, c(VIGA_LUZ, Offset(xa, sol.top)), oscuro) }
        }
        // El molino de mano, en el suelo: la piedra de abajo, la de arriba con su ojo y su mango de madera.
        molino(Offset(w * 0.41f, suelo + hb * 0.12f), w * 0.1f, oscuro, calor(Offset(w * 0.41f, suelo)))
        // La escalera: un tronco con muescas, apoyado en el piso.
        escalera(Offset(w * 0.165f, suelo + hb * 0.15f), Offset(w * 0.215f, b.top + hb * 0.01f), w * 0.05f, oscuro) { p -> calor(p) }
        // El manto a cuadros, echado sobre la solera.
        manto(Rect(w * 0.79f, b.top - 1.5f * u, w * 0.9f, bajo + hb * 0.24f), oscuro, calor(Offset(w * 0.85f, bajo)))
        // La hoguera: de día, la ceniza con su hilo de humo; de noche, encendida, y su luz en lo de abajo.
        if (oscuro) {
            mancha(fuego - Offset(0f, hb * 0.2f), w * 0.85f, TrazosGalia.FUEGO_HALO.copy(alpha = 0.13f))
            mancha(fuego - Offset(0f, hb * 0.2f), w * 0.35f, TrazosGalia.FUEGO_HALO.copy(alpha = 0.16f))
            with(TrazosGalia) { hoguera(fuego, w * 0.1f, 0.8f) }
        } else ceniza(fuego, w * 0.1f, bajo)
        // La tablilla con «y N más», colgada de una clavija de la solera.
        if (d.mas > 0) placa.let { pl -> etiqueta(d.enBase(pl), 23, oscuro, calor(Offset(w * 0.5f, bajo))) }
        // El grano de papel, al final.
        granoMueble(Rect(0f, 0f, w, alto), oscuro)
    }

    /** El molino de mano: la piedra de abajo, la de arriba con su ojo y el mango de madera, de lado. */
    private fun DrawScope.molino(pie: Offset, ancho: Float, oscuro: Boolean, calor: Float) {
        val u = density
        val piedra = luz(PIEDRA, oscuro, calor)
        sombraContacto(Offset(pie.x + 2 * u, pie.y), ancho * 1.25f, 4 * u, if (oscuro) 0.5f else 0.35f)
        val alto0 = ancho * 0.24f; val alto1 = ancho * 0.22f
        // La de abajo.
        drawRect(Brush.horizontalGradient(listOf(mezcla(piedra, LUZ_MUEBLE, 0.2f), piedra, mezcla(piedra, SOMBRA_MUEBLE, 0.45f)), pie.x - ancho / 2, pie.x + ancho / 2),
            Offset(pie.x - ancho / 2, pie.y - alto0), Size(ancho, alto0))
        drawOval(mezcla(piedra, SOMBRA_MUEBLE, 0.3f), Offset(pie.x - ancho / 2, pie.y - ancho * 0.06f), Size(ancho, ancho * 0.12f))
        // La de arriba, un poco más angosta y abombada.
        val a1 = ancho * 0.88f
        val y1 = pie.y - alto0
        drawRect(Brush.horizontalGradient(listOf(mezcla(piedra, LUZ_MUEBLE, 0.25f), piedra, mezcla(piedra, SOMBRA_MUEBLE, 0.5f)), pie.x - a1 / 2, pie.x + a1 / 2),
            Offset(pie.x - a1 / 2, y1 - alto1), Size(a1, alto1))
        drawLine(sombra(oscuro, 0.4f), Offset(pie.x - ancho / 2, y1), Offset(pie.x + ancho / 2, y1), 1f * u)
        drawOval(mezcla(piedra, LUZ_MUEBLE, if (oscuro) 0.05f else 0.25f), Offset(pie.x - a1 / 2, y1 - alto1 - a1 * 0.1f), Size(a1, a1 * 0.2f))
        drawOval(mezcla(piedra, SOMBRA_MUEBLE, 0.7f), Offset(pie.x - a1 * 0.1f, y1 - alto1 - a1 * 0.04f), Size(a1 * 0.2f, a1 * 0.07f))
        pinceladas(Rect(pie.x - ancho / 2, y1 - alto1, pie.x + ancho / 2, pie.y), sombra(oscuro, 1f), 595, 6, u, 0.12f)
        // El mango, clavado en el borde de la de arriba.
        val m0 = Offset(pie.x + a1 * 0.36f, y1 - alto1 - a1 * 0.03f)
        drawLine(luz(VIGA, oscuro, calor), m0, m0 + Offset(ancho * 0.08f, -ancho * 0.3f), 2.4f * u, StrokeCap.Round)
        drawLine(luzMueble(if (oscuro) 0.05f else 0.3f), m0 + Offset(-0.8f * u, 0f), m0 + Offset(ancho * 0.08f - 0.8f * u, -ancho * 0.3f), 0.7f * u, StrokeCap.Round)
    }

    /** La escalera de un solo tronco con muescas, de [pie] a [cima], de [ancho]: la corteza a los lados y cada muesca con su peldaño. */
    private fun DrawScope.escalera(pie: Offset, cima: Offset, ancho: Float, oscuro: Boolean, calor: (Offset) -> Float) {
        val u = density
        val d = cima - pie
        val largo = d.getDistance()
        val giro = Math.toDegrees(atan2(d.x.toDouble(), (-d.y).toDouble())).toFloat()
        val k = calor((pie + cima) / 2f)
        // La sombra en el suelo y en la solera, corrida a la derecha.
        sombraContacto(Offset(pie.x + 3 * u, pie.y), ancho * 1.6f, 4 * u, if (oscuro) 0.5f else 0.35f)
        rotate(giro, pie) {
            val r = Rect(pie.x - ancho / 2, pie.y - largo, pie.x + ancho / 2, pie.y)
            translate(2.5f * u, 0f) { drawRoundRect(sombra(oscuro, 0.3f), r.topLeft, r.size, CornerRadius(ancho / 2)) }
            val tronco = luz(VIGA, oscuro, k)
            drawRoundRect(Brush.horizontalGradient(listOf(mezcla(tronco, LUZ_MUEBLE, if (oscuro) 0.05f else 0.25f), tronco, mezcla(tronco, SOMBRA_MUEBLE, 0.55f)), r.left, r.right),
                r.topLeft, r.size, CornerRadius(ancho / 2))
            clipRect(r.left, r.top, r.right, r.bottom) {
                for (j in 0 until 10) {
                    val x = if (j % 2 == 0) r.left + ancho * 0.12f * azar(601, j) else r.right - ancho * 0.12f * azar(601, j)
                    val y0 = r.top + r.height * azar(602, j)
                    drawLine(VIGA_OSC.copy(alpha = 0.6f), Offset(x, y0), Offset(x, y0 + r.height * 0.18f), 1.6f * u, StrokeCap.Round)
                }
            }
            // Las muescas: arriba, la cara del corte en sombra; abajo, el peldaño de madera fresca en luz.
            val m = 7
            for (j in 0 until m) {
                val y = r.bottom - r.height * (0.1f + 0.82f * j / (m - 1))
                val mw = ancho * 0.78f
                drawOval(Brush.verticalGradient(listOf(sombra(oscuro, if (oscuro) 0.7f else 0.6f), sombra(oscuro, 0.2f)), y - 5f * u, y), Offset(pie.x - mw / 2, y - 5f * u), Size(mw, 5f * u))
                drawOval(luz(mezcla(HAYA, VIGA, 0.35f), oscuro, k + 0.1f), Offset(pie.x - mw / 2, y - 1.6f * u), Size(mw, 2.6f * u))
                drawOval(luzMueble(if (oscuro) 0.06f else 0.35f), Offset(pie.x - mw * 0.35f, y - 1.4f * u), Size(mw * 0.5f, 1.2f * u))
            }
            drawLine(if (oscuro) TrazosGalia.LUNA.copy(alpha = 0.12f) else luzMueble(0.4f), Offset(r.left + 1 * u, r.top + ancho / 2), Offset(r.left + 1 * u, r.bottom - ancho / 2), 0.9f * u)
        }
    }

    /** El manto de lana a cuadros, doblado sobre la solera y colgando por delante, con sus pliegues y sus flecos. */
    private fun DrawScope.manto(r: Rect, oscuro: Boolean, calor: Float) {
        val u = density
        val forma = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top)
            cubicTo(r.right + r.width * 0.04f, r.top + r.height * 0.4f, r.right - r.width * 0.06f, r.top + r.height * 0.7f, r.right - r.width * 0.02f, r.bottom - r.height * 0.06f)
            for (k in 6 downTo 0) lineTo(r.left + r.width * (0.06f + 0.15f * k), r.bottom - r.height * (0.06f * (k % 2)))
            cubicTo(r.left - r.width * 0.02f, r.top + r.height * 0.6f, r.left + r.width * 0.04f, r.top + r.height * 0.3f, r.left, r.top)
            close()
        }
        translate(2.2f * u, 1.6f * u) { drawPath(forma, sombra(oscuro, if (oscuro) 0.5f else 0.3f)) }
        drawPath(forma, luz(TrazosGalia.LANA_ROJA, oscuro, calor))
        clipPath(forma) {
            // Los cuadros: franjas verdes que se cruzan y un hilo de ocre entre ellas.
            val paso = 7f * u
            val verde = luz(TrazosGalia.LANA_VERDE, oscuro, calor).copy(alpha = 0.75f)
            val ocre = luz(TrazosGalia.LANA_OCRE, oscuro, calor).copy(alpha = 0.8f)
            var x = r.left + 1.5f * u
            while (x < r.right) { drawRect(verde, Offset(x, r.top), Size(2.4f * u, r.height)); drawLine(ocre, Offset(x + 4.6f * u, r.top), Offset(x + 4.6f * u, r.bottom), 0.6f * u); x += paso }
            var y = r.top + 2 * u
            while (y < r.bottom) { drawRect(verde, Offset(r.left, y), Size(r.width, 2.4f * u)); drawLine(ocre, Offset(r.left, y + 4.6f * u), Offset(r.right, y + 4.6f * u), 0.6f * u); y += paso }
            // Los pliegues.
            for (k in 0 until 4) {
                val xp = r.left + r.width * (0.15f + 0.22f * k)
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, sombra(oscuro, 0.32f), Color.Transparent), xp - 3 * u, xp + 3 * u), Offset(xp - 3 * u, r.top), Size(6 * u, r.height))
            }
            // El doblez de arriba, sobre el canto del piso, a la luz.
            drawRect(Brush.verticalGradient(listOf(luzMueble(if (oscuro) 0.05f else 0.25f), Color.Transparent), r.top, r.top + r.height * 0.18f), r.topLeft, Size(r.width, r.height * 0.18f))
        }
        // Los flecos.
        for (k in 0 until 12) {
            val x = r.left + r.width * (0.07f + 0.075f * k)
            val y = r.bottom - r.height * 0.04f * ((k / 2) % 2)
            drawLine(luz(TrazosGalia.LANA_ROJA, oscuro, calor), Offset(x, y - 1 * u), Offset(x + 0.3f * u, y + (2.5f + 2f * azar(611, k)) * u), 0.7f * u, StrokeCap.Round)
        }
    }

    /** La hoguera apagada, de día: el corro de piedras, la ceniza, los tizones y un hilo de humo que sube hasta el piso. */
    private fun DrawScope.ceniza(c: Offset, ancho: Float, techo: Float) {
        val u = density
        drawOval(Color(0xFFA8A398), Offset(c.x - ancho * 0.42f, c.y - ancho * 0.1f), Size(ancho * 0.84f, ancho * 0.2f))
        drawOval(Color(0xFF7E7A70).copy(alpha = 0.5f), Offset(c.x - ancho * 0.25f, c.y - ancho * 0.06f), Size(ancho * 0.5f, ancho * 0.1f))
        drawLine(Color(0xFF2E2620), Offset(c.x - ancho * 0.3f, c.y - ancho * 0.02f), Offset(c.x + ancho * 0.1f, c.y - ancho * 0.07f), 3f * u, StrokeCap.Round)
        drawLine(Color(0xFF3A3028), Offset(c.x + ancho * 0.28f, c.y), Offset(c.x - ancho * 0.05f, c.y - ancho * 0.08f), 2.6f * u, StrokeCap.Round)
        for (k in 0 until 9) {
            val a = PI * 2 * k / 9 + 0.3
            val p = Offset(c.x + ancho * 0.5f * cos(a).toFloat(), c.y + ancho * 0.13f * sin(a).toFloat())
            val pw = ancho * (0.14f + 0.04f * azar(621, k)); val ph = pw * 0.62f
            drawOval(sombraMueble(0.3f), Offset(p.x - pw / 2 + 1 * u, p.y - ph / 2 + 1 * u), Size(pw, ph))
            drawOval(PIEDRA, Offset(p.x - pw / 2, p.y - ph / 2), Size(pw, ph))
            drawOval(luzMueble(0.35f), Offset(p.x - pw * 0.35f, p.y - ph * 0.45f), Size(pw * 0.5f, ph * 0.35f))
        }
        humo(Offset(c.x, c.y - ancho * 0.1f), Offset(c.x - ancho * 0.6f, techo + 4 * u), ancho * 0.2f, TrazosGalia.HUMO, 0.4f, 623)
    }

    // ---------------------------------------------------------------- el compartimento: un estante con su cesta

    /** El barro de la pared del fondo dentro de [r], con sus grietas, las pasadas de la mano y, en un rincón, el zarzo que asoma. */
    private fun DrawScope.barro(r: Rect, semilla: Int, oscuro: Boolean, color: Color, oscura: Color) {
        val u = density
        clipRect(r.left, r.top, r.right, r.bottom) {
            pinceladas(r, mezcla(color, LUZ_MUEBLE, 0.3f), semilla + 1, 18, u, if (oscuro) 0.04f else 0.14f)
            pinceladas(r, oscura, semilla + 2, 14, u, 0.12f)
            // Las pasadas de la mano al alisar el barro.
            for (k in 0 until 6) {
                val c = Offset(r.left + r.width * azar(semilla, k + 70), r.top + r.height * azar(semilla, k + 71))
                val rr = (6f + 10f * azar(semilla, k + 72)) * u
                drawArc(oscura.copy(alpha = 0.16f), 200f + 60f * azar(semilla, k + 73), 80f, false, Offset(c.x - rr, c.y - rr), Size(2 * rr, 2 * rr), style = Stroke(1.6f * u))
                drawArc(luzMueble(if (oscuro) 0.02f else 0.16f), 200f + 60f * azar(semilla, k + 73), 80f, false, Offset(c.x - rr, c.y - rr - 1.2f * u), Size(2 * rr, 2 * rr), style = Stroke(0.9f * u))
            }
            // Las grietas del barro seco.
            for (g in 0 until 2) {
                val p = Path()
                var q = Offset(r.left + r.width * azar(semilla, g + 80), r.top + r.height * azar(semilla, g + 81) * 0.5f)
                p.moveTo(q.x, q.y)
                for (i in 0 until 7) {
                    q += Offset((azar(semilla, g * 10 + i + 82) - 0.5f) * 6f * u, (2f + 3f * azar(semilla, g * 10 + i + 83)) * u)
                    p.lineTo(q.x, q.y)
                }
                drawPath(p, oscura.copy(alpha = 0.5f), style = Stroke(0.7f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
    }

    /** Un desconchón de la pared en [r]: el barro caído y el zarzo de avellano que asoma, las varas por delante y por detrás. */
    private fun DrawScope.zarzo(r: Rect, semilla: Int, oscuro: Boolean, cerca: Float) {
        val u = density
        val hueco = contornoIrregular(r, semilla, 14, 0.2f)
        drawPath(hueco, luz(mezcla(ZARZO, VIGA_OSC, 0.6f), oscuro, cerca))
        clipPath(hueco) {
            val fila = 3.2f * u; val estaca = 7f * u
            val vara = luz(ZARZO_LUZ, oscuro, cerca)
            val estacas = luz(ZARZO, oscuro, cerca)
            var j = 0; var y = r.top
            while (y < r.bottom) {
                drawLine(vara, Offset(r.left, y + fila / 2), Offset(r.right, y + fila / 2), fila * 0.75f, StrokeCap.Round)
                drawLine(luzMueble(if (oscuro) 0.04f else 0.25f), Offset(r.left, y + fila * 0.3f), Offset(r.right, y + fila * 0.3f), 0.5f * u)
                var i = 0; var x = r.left + estaca * 0.5f
                while (x < r.right) {
                    if ((i + j) % 2 == 0) drawRect(estacas, Offset(x - 1.2f * u, y), Size(2.4f * u, fila))
                    x += estaca; i++
                }
                y += fila; j++
            }
            drawPath(hueco, sombra(oscuro, 0.35f), style = Stroke(3f * u))
        }
        // El borde roto del barro: su canto a la luz arriba a la izquierda.
        translate(0.9f * u, 0.9f * u) { drawPath(hueco, luzMueble(if (oscuro) 0.04f else 0.35f), style = Stroke(1.1f * u)) }
        drawPath(hueco, luz(mezcla(BARRO_OSC, VIGA_OSC, 0.3f), oscuro, cerca), style = Stroke(0.9f * u))
    }

    /** Una tablilla de haya vista de frente en [r], de pie: el marco, la cera con unos renglones rayados y el agujero con su correa. */
    private fun DrawScope.tablillaDePie(r: Rect, semilla: Int, oscuro: Boolean, cerca: Float) {
        val u = density
        val haya = luz(HAYA, oscuro, cerca)
        val forma = contornoTablilla(r, semilla, 1.6f * u)
        drawPath(forma, Brush.horizontalGradient(listOf(mezcla(haya, LUZ_MUEBLE, if (oscuro) 0.04f else 0.25f), haya, mezcla(haya, SOMBRA_MUEBLE, 0.3f)), r.left, r.right))
        val m = r.width * 0.16f
        val cera = Rect(r.left + m, r.top + m, r.right - m, r.bottom)
        drawRect(luz(CERA, oscuro, cerca), cera.topLeft, cera.size)
        drawLine(sombra(oscuro, 0.4f), cera.topLeft, Offset(cera.right, cera.top), 0.8f * u)
        drawLine(sombra(oscuro, 0.3f), cera.topLeft, Offset(cera.left, cera.bottom), 0.8f * u)
        for (l in 0 until 4) {
            val y = cera.top + (2.6f + 2.6f * l) * u
            if (y < cera.bottom) drawLine(TINTA.copy(alpha = 0.35f), Offset(cera.left + 1.4f * u, y), Offset(cera.left + cera.width * (0.4f + 0.5f * azar(semilla, l)), y), 0.5f * u)
        }
        motas(Rect(r.left, r.top, r.right, r.top + m), HAYA_MOTA.copy(alpha = 0.5f), semilla, 0.6f)
        drawPath(forma, mezcla(haya, VIGA_OSC, 0.5f), style = Stroke(0.6f * u))
        drawLine(luzMueble(if (oscuro) 0.06f else 0.6f), Offset(r.left + 1.6f * u, r.top + 0.4f * u), Offset(r.right - 1.6f * u, r.top + 0.4f * u), 0.7f * u)
        // El agujero y la correa que cuelga.
        val a = Offset(r.left + m * 0.55f, r.top + m * 0.55f)
        drawCircle(VIGA_OSC, 0.9f * u, a)
        val correa = luz(CUERO, oscuro, cerca)
        drawLine(correa, a, a + Offset(-1.6f * u, 5f * u), 0.9f * u, StrokeCap.Round)
        drawLine(correa, a, a + Offset(0.6f * u, 6f * u), 0.9f * u, StrokeCap.Round)
    }

    /**
     * La cesta de mimbre en el estante, con su pie en [pie]: de [ancho] y [alto], con [k] tablillas de haya de pie dentro (que
     * suben [ab] al abrirla). Cada una tiene su forma (abierta, panzuda o recta con asas) y, algunas, una franja de mimbre con corteza.
     */
    private fun DrawScope.cesta(pie: Offset, ancho: Float, alto: Float, k: Int, ab: Float, semilla: Int, oscuro: Boolean, cerca: Float) {
        val u = density
        val tipo = Math.floorMod(semilla, 3)
        val rx = ancho / 2; val ry = ancho * 0.11f
        val boca = pie.y - alto
        val abajo = rx * (if (tipo == 1) 0.74f else if (tipo == 2) 0.9f else 0.76f)
        val cx = pie.x
        sombraContacto(Offset(cx + 3 * u, pie.y), ancho * 1.05f, ry * 1.2f, if (oscuro) 0.5f else 0.34f)
        // La boca por dentro, oscura, con el borde de atrás.
        val bocaR = Rect(cx - rx, boca - ry, cx + rx, boca + ry)
        drawOval(luz(mezcla(MIMBRE_OSC, SOMBRA_MUEBLE, 0.6f), oscuro, cerca * 0.5f), bocaR.topLeft, bocaR.size)
        drawArc(luz(MIMBRE_OSC, oscuro, cerca * 0.6f), 180f, 180f, false, bocaR.topLeft, bocaR.size, style = Stroke(3.4f * u))
        // Las tablillas de pie, en abanico, que suben al abrir.
        val kk = k.coerceIn(0, 8)
        for (i in 0 until kk) {
            val f = if (kk == 1) 0.5f else i / (kk - 1f)
            val x = cx + rx * 0.62f * (f * 2f - 1f) + (azar(semilla, i + 200) - 0.5f) * rx * 0.1f
            val tw = ancho * (0.15f + 0.04f * azar(semilla, i + 210))
            val sobre = alto * (0.3f + 0.28f * azar(semilla, i + 220)) + ab * alto * (0.3f + 0.15f * azar(semilla, i + 230))
            val giro = (f * 2f - 1f) * 9f + (azar(semilla, i + 240) - 0.5f) * 8f
            val base = Offset(x, boca + ry * 0.6f)
            rotate(giro, base) { tablillaDePie(Rect(x - tw / 2, boca - sobre, x + tw / 2, boca + ry * 0.6f), semilla + i * 13, oscuro, cerca * 0.8f) }
        }
        // El cuerpo: se angosta hacia el pie (o se abomba), con las hileras del tejido siguiendo la vuelta de la cesta.
        val cuerpo = Path().apply {
            moveTo(cx - rx, boca)
            when (tipo) {
                1 -> cubicTo(cx - rx * 1.14f, boca + alto * 0.38f, cx - abajo * 1.1f, pie.y - alto * 0.12f, cx - abajo, pie.y)
                2 -> cubicTo(cx - rx * 1.02f, boca + alto * 0.4f, cx - abajo, pie.y - alto * 0.2f, cx - abajo, pie.y)
                else -> quadraticTo(cx - rx * 0.95f, boca + alto * 0.6f, cx - abajo, pie.y)
            }
            quadraticTo(cx, pie.y + ry * 1.4f, cx + abajo, pie.y)
            when (tipo) {
                1 -> cubicTo(cx + abajo * 1.1f, pie.y - alto * 0.12f, cx + rx * 1.14f, boca + alto * 0.38f, cx + rx, boca)
                2 -> cubicTo(cx + abajo, pie.y - alto * 0.2f, cx + rx * 1.02f, boca + alto * 0.4f, cx + rx, boca)
                else -> quadraticTo(cx + rx * 0.95f, boca + alto * 0.6f, cx + rx, boca)
            }
            arcTo(bocaR, 0f, 180f, false)
            close()
        }
        val claro = luz(MIMBRE, oscuro, cerca)
        drawPath(cuerpo, claro)
        val franja = if (Math.floorMod(semilla / 3, 2) == 0) (3..4) else (99..99)
        clipPath(cuerpo) {
            val fila = 2.8f * u; val estaca = 5.2f * u
            var y = boca + ry * 0.3f; var j = 0
            while (y < pie.y + ry * 1.4f) {
                // El radio de la cesta a esta altura (para que las hileras se curven como su boca).
                val t = ((y - boca) / alto).coerceIn(0f, 1f)
                val rxy = rx + (abajo - rx) * t + (if (tipo == 1) rx * 0.1f * sin(PI.toFloat() * t) else 0f)
                val col = luz(if (j in franja) MIMBRE_PARDO else if (j % 2 == 0) MIMBRE else mezcla(MIMBRE, MIMBRE_OSC, 0.18f), oscuro, cerca)
                val paso = estaca / rxy.coerceAtLeast(1f)
                var th = (if (j % 2 == 0) 0f else paso / 2) + paso / 2
                while (th < PI.toFloat()) {
                    val x = cx - rxy * cos(th)
                    val yy = y + ry * 0.92f * sin(th)
                    val bw = (rxy * sin(th) * paso).coerceAtLeast(0.6f * u)
                    drawOval(col, Offset(x - bw * 0.48f, yy - fila * 0.46f), Size(bw * 0.96f, fila * 0.92f))
                    drawLine(luzMueble(if (oscuro) 0.05f else 0.3f), Offset(x - bw * 0.32f, yy - fila * 0.2f), Offset(x + bw * 0.12f, yy - fila * 0.34f), 0.6f * u, StrokeCap.Round)
                    th += paso
                }
                y += fila; j++
            }
            // La luz de un lado y la sombra del otro (redonda); de noche, la hoguera de abajo calienta el frente.
            drawRect(Brush.horizontalGradient(listOf(luzMueble(if (oscuro) 0.04f else 0.26f), Color.Transparent, sombra(oscuro, if (oscuro) 0.5f else 0.45f)), cx - rx, cx + rx),
                Offset(cx - rx * 1.2f, boca - ry), Size(rx * 2.4f, alto + ry * 3))
            drawRect(Brush.verticalGradient(listOf(sombra(oscuro, 0.25f), Color.Transparent, if (oscuro) TrazosGalia.FUEGO.copy(alpha = 0.16f) else sombra(oscuro, 0.2f)), boca, pie.y + ry),
                Offset(cx - rx * 1.2f, boca), Size(rx * 2.4f, alto + ry))
        }
        // Las asas de mimbre torcido, en las cestas rectas.
        if (tipo == 2) listOf(-1f, 1f).forEach { s ->
            val asa = Rect(cx + s * rx - rx * 0.16f, boca - ry * 0.4f, cx + s * rx + rx * 0.16f, boca + alto * 0.36f)
            drawArc(luz(MIMBRE_OSC, oscuro, cerca), if (s < 0) 90f else -90f, 180f, false, asa.topLeft, asa.size, style = Stroke(2.8f * u))
            drawArc(luz(MIMBRE_LUZ, oscuro, cerca), if (s < 0) 90f else -90f, 180f, false, asa.topLeft, asa.size, style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * u, 1.6f * u))))
        }
        // El borde trenzado de delante.
        val labio = Path().apply { arcTo(bocaR, 0f, 180f, true) }
        drawPath(labio, luz(MIMBRE_OSC, oscuro, cerca), style = Stroke(4.4f * u))
        drawPath(labio, luz(MIMBRE_LUZ, oscuro, cerca), style = Stroke(2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.6f * u, 1.8f * u))))
        drawPath(labio, sombra(oscuro, 0.25f), style = Stroke(1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.8f * u, 2.6f * u), 1.3f * u)))
    }

    /** El costal de estopa de lo que no tiene tema: la boca enrollada y las tablillas que asoman torcidas, sin orden. */
    private fun DrawScope.costal(pie: Offset, ancho: Float, alto: Float, k: Int, ab: Float, semilla: Int, oscuro: Boolean, cerca: Float) {
        val u = density
        val cx = pie.x
        val boca = pie.y - alto
        val bocaR = Rect(cx - ancho * 0.38f, boca - ancho * 0.08f, cx + ancho * 0.38f, boca + ancho * 0.08f)
        val tela = luz(ESTOPA, oscuro, cerca)
        sombraContacto(Offset(cx + 3 * u, pie.y), ancho * 1.15f, ancho * 0.12f, if (oscuro) 0.5f else 0.34f)
        drawOval(luz(mezcla(ESTOPA, SOMBRA_MUEBLE, 0.7f), oscuro, cerca * 0.4f), bocaR.topLeft, bocaR.size)
        for (i in 0 until k.coerceIn(0, 6)) {
            val f = if (k <= 1) 0.5f else i / (k.coerceIn(0, 6) - 1f)
            val x = cx + ancho * 0.24f * (f * 2f - 1f)
            val tw = ancho * 0.15f
            val sobre = alto * (0.28f + 0.3f * azar(semilla, i + 300)) + ab * alto * 0.35f
            val base = Offset(x, boca + ancho * 0.05f)
            rotate((f * 2f - 1f) * 20f + (azar(semilla, i + 310) - 0.5f) * 18f, base) {
                tablillaDePie(Rect(x - tw / 2, boca - sobre, x + tw / 2, boca + ancho * 0.05f), semilla + i * 17, oscuro, cerca * 0.8f)
            }
        }
        val cuerpo = Path().apply {
            moveTo(bocaR.left, boca)
            cubicTo(cx - ancho * 0.5f, boca + alto * 0.35f, cx - ancho * 0.58f, pie.y - alto * 0.06f, cx - ancho * 0.44f, pie.y)
            quadraticTo(cx, pie.y + alto * 0.08f, cx + ancho * 0.46f, pie.y)
            cubicTo(cx + ancho * 0.6f, pie.y - alto * 0.1f, cx + ancho * 0.46f, boca + alto * 0.3f, bocaR.right, boca)
            arcTo(bocaR, 0f, 180f, false)
            close()
        }
        drawPath(cuerpo, tela)
        clipPath(cuerpo) {
            // La trama de la estopa.
            var x = cx - ancho * 0.6f
            while (x < cx + ancho * 0.6f) { drawLine(sombra(oscuro, 0.08f), Offset(x, boca), Offset(x, pie.y + 4 * u), 0.5f * u); x += 1.8f * u }
            var y = boca
            while (y < pie.y + 4 * u) { drawLine(luzMueble(if (oscuro) 0.02f else 0.1f), Offset(cx - ancho * 0.6f, y), Offset(cx + ancho * 0.6f, y), 0.5f * u); y += 1.8f * u }
            // Los pliegues, que bajan desde la boca y se juntan en el pie.
            for (j in 0 until 5) {
                val x0 = bocaR.left + bocaR.width * (0.1f + 0.2f * j)
                val p = Path().apply { moveTo(x0, boca + ancho * 0.06f); quadraticTo(x0 + (azar(semilla, j + 320) - 0.5f) * ancho * 0.3f, boca + alto * 0.5f, cx + (x0 - cx) * 1.2f, pie.y - alto * 0.1f) }
                drawPath(p, sombra(oscuro, 0.25f), style = Stroke(2.4f * u, cap = StrokeCap.Round))
                translate(-1.4f * u, 0f) { drawPath(p, luzMueble(if (oscuro) 0.03f else 0.2f), style = Stroke(1f * u, cap = StrokeCap.Round)) }
            }
            drawRect(Brush.horizontalGradient(listOf(luzMueble(if (oscuro) 0.04f else 0.22f), Color.Transparent, sombra(oscuro, if (oscuro) 0.5f else 0.42f)), cx - ancho * 0.55f, cx + ancho * 0.55f),
                Offset(cx - ancho * 0.6f, boca - 4 * u), Size(ancho * 1.2f, alto + 10 * u))
            if (oscuro) drawRect(Brush.verticalGradient(listOf(Color.Transparent, TrazosGalia.FUEGO.copy(alpha = 0.14f)), boca, pie.y), Offset(cx - ancho * 0.6f, boca), Size(ancho * 1.2f, alto))
        }
        // La boca enrollada: un rollo grueso de estopa por delante.
        val rollo = Path().apply { arcTo(bocaR, 0f, 180f, true) }
        drawPath(rollo, mezcla(tela, SOMBRA_MUEBLE, 0.25f), style = Stroke(6f * u, cap = StrokeCap.Round))
        translate(0f, -1.2f * u) { drawPath(rollo, mezcla(tela, LUZ_MUEBLE, if (oscuro) 0.04f else 0.25f), style = Stroke(3f * u, cap = StrokeCap.Round)) }
        // Unos granos que se salieron.
        for (j in 0 until 6) {
            val p = Offset(cx + ancho * (0.3f + 0.25f * azar(semilla, j + 330)), pie.y + (azar(semilla, j + 331) - 0.3f) * 3f * u)
            drawOval(luz(ESCANDA, oscuro, cerca), Offset(p.x - 1.2f * u, p.y - 0.7f * u), Size(2.4f * u, 1.4f * u))
        }
    }

    /** Un haz de tablillas acostadas en el estante, atado con una correa de cuero con su nudo. */
    private fun DrawScope.haz(pie: Offset, ancho: Float, alto: Float, semilla: Int, oscuro: Boolean, cerca: Float) {
        val u = density
        sombraContacto(Offset(pie.x + 2 * u, pie.y), ancho * 1.1f, alto * 0.5f, if (oscuro) 0.5f else 0.3f)
        for (j in 0 until 3) {
            val y1 = pie.y - j * alto / 3f; val y0 = y1 - alto / 3f + 0.4f * u
            val dx = (azar(semilla, j + 340) - 0.5f) * ancho * 0.12f
            val r = Rect(pie.x - ancho / 2 + dx, y0, pie.x + ancho / 2 + dx, y1)
            val haya = luz(mezcla(HAYA, HAYA_OSC, 0.2f * j), oscuro, cerca)
            drawRect(haya, r.topLeft, r.size)
            drawLine(luzMueble(if (oscuro) 0.05f else 0.45f), r.topLeft, Offset(r.right, r.top), 0.7f * u)
            drawLine(sombra(oscuro, 0.4f), Offset(r.left, r.bottom), r.bottomRight, 0.7f * u)
            drawLine(luz(CERA, oscuro, cerca), Offset(r.left + 1.6f * u, r.center.y), Offset(r.right - 1.6f * u, r.center.y), 0.8f * u)
        }
        val correa = luz(CUERO, oscuro, cerca)
        val xc = pie.x - ancho * 0.12f
        drawLine(correa, Offset(xc, pie.y - alto - 0.5f * u), Offset(xc, pie.y), 2.6f * u)
        drawLine(luzMueble(if (oscuro) 0.05f else 0.3f), Offset(xc - 0.7f * u, pie.y - alto), Offset(xc - 0.7f * u, pie.y), 0.6f * u)
        drawCircle(correa, 2.2f * u, Offset(xc, pie.y - alto * 0.5f))
        drawLine(correa, Offset(xc, pie.y - alto * 0.5f), Offset(xc + ancho * 0.22f, pie.y + 1.5f * u), 1.4f * u, StrokeCap.Round)
    }

    /**
     * La cesta empezada de un estante sin tema: el fondo tejido en el estante, unas hileras de mimbre y las varas de la urdimbre
     * levantadas; al lado, un haz de mimbres sin tejer.
     */
    private fun DrawScope.cestaEmpezada(pie: Offset, ancho: Float, alto: Float, semilla: Int, oscuro: Boolean, cerca: Float) {
        val u = density
        val rr = ancho * 0.44f; val ry = ancho * 0.1f
        val cx = pie.x
        val banda = alto * 0.22f
        val arriba = pie.y - banda
        sombraContacto(Offset(cx + 2 * u, pie.y), ancho * 1.05f, ry * 1.4f, if (oscuro) 0.5f else 0.3f)
        val mimbre = luz(MIMBRE, oscuro, cerca)
        val bocaR = Rect(cx - rr, arriba - ry, cx + rr, arriba + ry)
        // Las varas de la urdimbre, que salen de las hileras y se abren hacia arriba: las de atrás, detrás de todo.
        fun vara(th: Float, atras: Boolean) {
            val x = cx - rr * cos(th)
            val y = arriba + (if (atras) -1f else 1f) * ry * sin(th)
            val tope = Offset(cx - rr * 1.3f * cos(th), pie.y - alto * (0.88f + 0.12f * azar(semilla, (th * 100).toInt())) + (if (atras) -2f * u else 0f))
            drawLine(luz(if (atras) MIMBRE_OSC else MIMBRE_LUZ, oscuro, cerca), Offset(x, y), tope, 1.3f * u, StrokeCap.Round)
            if (!atras) drawLine(sombra(oscuro, 0.25f), Offset(x + 0.8f * u, y), tope + Offset(0.8f * u, 0f), 0.5f * u)
        }
        for (i in 0 until 12) vara(PI.toFloat() * (i + 0.5f) / 12f, true)
        // La boca por dentro: el fondo tejido, con sus radios.
        drawOval(luz(mezcla(MIMBRE, SOMBRA_MUEBLE, 0.4f), oscuro, cerca * 0.6f), bocaR.topLeft, bocaR.size)
        for (i in 0 until 8) {
            val a = PI.toFloat() * i / 8f
            drawLine(luz(MIMBRE_OSC, oscuro, cerca), Offset(cx - rr * 0.85f * cos(a), arriba - ry * 0.85f * sin(a)), Offset(cx + rr * 0.85f * cos(a), arriba + ry * 0.85f * sin(a)), 0.8f * u)
        }
        // Las primeras hileras: una banda baja que da la vuelta.
        val cuerpo = Path().apply {
            moveTo(cx - rr, arriba); lineTo(cx - rr, pie.y)
            arcTo(Rect(cx - rr, pie.y - ry, cx + rr, pie.y + ry), 180f, -180f, false)
            lineTo(cx + rr, arriba)
            arcTo(bocaR, 0f, 180f, false)
            close()
        }
        drawPath(cuerpo, mimbre)
        clipPath(cuerpo) {
            var y = arriba + ry * 0.2f; var j = 0
            while (y < pie.y + ry) {
                val col = if (j % 2 == 0) mimbre else mezcla(mimbre, MIMBRE_OSC, 0.25f)
                drawArc(col, 0f, 180f, false, Offset(cx - rr, y - ry), Size(rr * 2, ry * 2), style = Stroke(2.2f * u))
                y += 2.6f * u; j++
            }
            drawRect(Brush.horizontalGradient(listOf(luzMueble(if (oscuro) 0.04f else 0.25f), Color.Transparent, sombra(oscuro, 0.4f)), cx - rr, cx + rr), Offset(cx - rr, arriba - ry), Size(rr * 2, banda + ry * 3))
        }
        drawArc(luz(MIMBRE_LUZ, oscuro, cerca), 0f, 180f, false, bocaR.topLeft, bocaR.size, style = Stroke(1.4f * u))
        for (i in 0 until 12) vara(PI.toFloat() * (i + 0.5f) / 12f, false)
    }

    /** Un manojo de hierbas puesto a secar, colgado de un clavo de la pared, con su sombra. */
    private fun DrawScope.hierbas(clavo: Offset, largo: Float, semilla: Int, oscuro: Boolean, cerca: Float) {
        val u = density
        val tallo = luz(Color(0xFF7A7444), oscuro, cerca)
        val hoja = luz(Color(0xFF8C8E58), oscuro, cerca)
        val flor = luz(Color(0xFFD8C060), oscuro, cerca)
        val atado = clavo + Offset(0f, largo * 0.18f)
        for (pasada in 0..1) {
            val d = if (pasada == 0) Offset(3 * u, 3 * u) else Offset.Zero
            if (pasada == 0) {
                drawLine(sombra(oscuro, 0.2f), clavo + d, atado + d, 0.8f * u)
            } else drawLine(luz(Color(0xFFB59E6E), oscuro, cerca), clavo, atado, 0.8f * u)
            for (i in 0 until 13) {
                val a = Math.toRadians(90.0 + (i - 6) * 2.6 + (azar(semilla, i + 360) - 0.5f) * 3.0)
                val l = largo * (0.5f + 0.3f * azar(semilla, i + 361))
                val fin = atado + Offset(cos(a).toFloat() * l, sin(a).toFloat() * l) + d
                if (pasada == 0) { drawLine(sombra(oscuro, 0.16f), atado + d, fin, 1f * u); continue }
                drawLine(tallo, atado, fin, 0.7f * u, StrokeCap.Round)
                for (h in 1..4) {
                    val p = atado + (fin - atado) * (0.25f + h * 0.17f)
                    val lado = if ((h + i) % 2 == 0) 1f else -1f
                    drawOval(if (h % 2 == 0) hoja else mezcla(hoja, tallo, 0.4f), Offset(p.x - 0.5f * u + lado * 0.9f * u, p.y - 1.4f * u), Size(1.6f * u, 2.8f * u))
                }
                if (i % 2 == 0) { drawCircle(flor, 0.9f * u, fin); drawCircle(flor, 0.7f * u, fin + Offset(0.9f * u, -0.8f * u)) }
            }
        }
        drawCircle(luz(CUERO, oscuro, cerca), 1.6f * u, atado)
        clavija(clavo, 1.3f * u, luz(VIGA_OSC, oscuro, cerca), oscuro)
    }

    /** Un cubilete de barro negro bruñido con su pie, en el estante. */
    private fun DrawScope.cubilete(pie: Offset, alto: Float, oscuro: Boolean, cerca: Float) {
        val u = density
        val a = alto * 0.62f
        sombraContacto(Offset(pie.x + 2 * u, pie.y), a * 1.1f, 3 * u, if (oscuro) 0.5f else 0.3f)
        val barro = luz(BARRO_COCIDO, oscuro, cerca)
        val forma = Path().apply {
            moveTo(pie.x - a * 0.22f, pie.y); lineTo(pie.x - a * 0.14f, pie.y - alto * 0.12f)
            cubicTo(pie.x - a * 0.52f, pie.y - alto * 0.3f, pie.x - a * 0.5f, pie.y - alto * 0.62f, pie.x - a * 0.4f, pie.y - alto)
            lineTo(pie.x + a * 0.4f, pie.y - alto)
            cubicTo(pie.x + a * 0.5f, pie.y - alto * 0.62f, pie.x + a * 0.52f, pie.y - alto * 0.3f, pie.x + a * 0.14f, pie.y - alto * 0.12f)
            lineTo(pie.x + a * 0.22f, pie.y); close()
        }
        drawPath(forma, Brush.horizontalGradient(listOf(mezcla(barro, LUZ_MUEBLE, if (oscuro) 0.05f else 0.25f), barro, mezcla(barro, SOMBRA_MUEBLE, 0.5f)), pie.x - a * 0.5f, pie.x + a * 0.5f))
        drawOval(mezcla(barro, SOMBRA_MUEBLE, 0.6f), Offset(pie.x - a * 0.4f, pie.y - alto - a * 0.07f), Size(a * 0.8f, a * 0.14f))
        drawLine(mezcla(barro, LUZ_MUEBLE, if (oscuro) 0.08f else 0.35f), Offset(pie.x - a * 0.46f, pie.y - alto * 0.5f), Offset(pie.x + a * 0.46f, pie.y - alto * 0.5f), 0.7f * u)
        drawLine(luzMueble(if (oscuro) 0.08f else 0.5f), Offset(pie.x - a * 0.3f, pie.y - alto * 0.85f), Offset(pie.x - a * 0.34f, pie.y - alto * 0.35f), 1f * u, StrokeCap.Round)
    }

    /** Unas espigas de escanda acostadas en el estante: el tallo, los granos en dos filas y las barbas. */
    private fun DrawScope.espigas(pie: Offset, largo: Float, semilla: Int, oscuro: Boolean, cerca: Float) {
        val u = density
        val grano = luz(ESCANDA, oscuro, cerca)
        for (e in 0 until 3) {
            val a = Math.toRadians(-8.0 - 10.0 * e + 6.0 * azar(semilla, e + 370))
            val dir = Offset(cos(a).toFloat(), sin(a).toFloat())
            val p0 = pie + Offset(-largo * 0.5f, -e * 1.4f * u)
            val p1 = p0 + dir * largo
            drawLine(luz(Color(0xFFB09A5E), oscuro, cerca), p0, p0 + dir * (largo * 0.5f), 0.8f * u)
            for (g in 0 until 7) {
                val p = p0 + dir * (largo * (0.5f + 0.07f * g))
                val lado = if (g % 2 == 0) 1f else -1f
                drawOval(grano, Offset(p.x - 1.6f * u, p.y - 1f * u + lado * 0.7f * u), Size(3.2f * u, 1.8f * u))
                drawLine(grano.copy(alpha = 0.7f), p, p + Offset(dir.x * 4f * u - dir.y * lado * 2f * u, dir.y * 4f * u + dir.x * lado * 2f * u - 1.5f * u), 0.4f * u)
            }
            drawLine(grano, p1 - dir * (2f * u), p1 + dir * (4f * u), 0.4f * u)
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        val fondoY = h * REPISA; val canto = h * CANTO
        val lado = w * 0.055f
        // De noche, la hoguera alumbra desde abajo y por delante: más cálido cuanto más abajo.
        fun cerca(y: Float) = 0.04f + 0.22f * (y / h).coerceIn(0f, 1f)
        fun t(col: Color, y: Float) = luz(col, oscuro, cerca(y))

        // La pared de barro del fondo, con la sombra del estante de arriba y la del poste de la izquierda.
        drawRect(Brush.verticalGradient(listOf(t(mezcla(BARRO_PARED, SOMBRA_MUEBLE, 0.3f), 0f), t(BARRO_PARED, fondoY)), 0f, fondoY), Offset.Zero, Size(w, fondoY + 1 * u))
        barro(Rect(lado, 0f, w - lado, fondoY), c.semilla, oscuro, t(BARRO_PARED, fondoY * 0.5f), t(BARRO_OSC, fondoY * 0.5f))
        val izquierda = Math.floorMod(c.semilla / 7, 2) == 0
        zarzo(Rect(if (izquierda) w * 0.08f else w * 0.75f, h * 0.1f, if (izquierda) w * 0.25f else w * 0.92f, h * 0.28f), c.semilla + 11, oscuro, cerca(h * 0.2f))
        // Los costados del estante: el de la izquierda en sombra, el de la derecha a la luz.
        drawPath(Path().apply { moveTo(0f, 0f); lineTo(lado, 0f); lineTo(lado, fondoY); lineTo(0f, canto); close() }, t(mezcla(BARRO_PARED, SOMBRA_MUEBLE, 0.55f), fondoY))
        drawPath(Path().apply { moveTo(w, 0f); lineTo(w - lado, 0f); lineTo(w - lado, fondoY); lineTo(w, canto); close() }, t(mezcla(BARRO_PARED, LUZ_MUEBLE, 0.1f), fondoY))
        drawRect(Brush.verticalGradient(listOf(sombra(oscuro, if (oscuro) 0.65f else 0.5f), Color.Transparent), 0f, h * 0.26f), Offset.Zero, Size(w, h * 0.26f))
        if (!oscuro) drawPath(Path().apply { moveTo(0f, 0f); lineTo(w * 0.3f, 0f); lineTo(w * 0.1f, fondoY); lineTo(0f, fondoY); close() }, sombra(oscuro, 0.14f))
        else drawRect(Brush.verticalGradient(listOf(Color.Transparent, TrazosGalia.FUEGO_HALO.copy(alpha = 0.08f)), 0f, fondoY), Offset.Zero, Size(w, fondoY))

        // La tabla del estante, vista un poco desde arriba, con la junta de dos tablas y unos granos sueltos.
        val tabla = Path().apply { moveTo(lado, fondoY); lineTo(w - lado, fondoY); lineTo(w, canto); lineTo(0f, canto); close() }
        drawPath(tabla, Brush.horizontalGradient(listOf(t(mezcla(TABLA, LUZ_MUEBLE, 0.2f), canto), t(TABLA, canto), t(mezcla(TABLA, SOMBRA_MUEBLE, 0.2f), canto)), 0f, w))
        clipPath(tabla) {
            vetas(Rect(0f, fondoY, w, canto), VIGA_OSC.copy(alpha = 0.25f), c.semilla + 13, densidad = 0.7f, grosor = 0.6f, ondulacion = 0.3f)
            drawLine(sombra(oscuro, 0.35f), Offset(0f, (fondoY + canto) / 2 + 0.5f * u), Offset(w, (fondoY + canto) / 2 + 0.5f * u), 0.7f * u)
        }
        drawLine(sombra(oscuro, if (oscuro) 0.6f else 0.4f), Offset(lado, fondoY), Offset(w - lado, fondoY), 1f * u)

        val pieY = (fondoY + canto) / 2 + 1 * u
        val k = c.n
        val izqCesta = Math.floorMod(c.semilla, 2) == 0
        val cx = w * (0.5f + (azar(c.semilla, 1) - 0.5f) * 0.08f)
        val ancho = w * 0.46f; val alto = h * 0.34f
        val bocaY = pieY - alto
        if (c.vacio) {
            cestaEmpezada(Offset(cx, pieY), ancho * 0.9f, alto, c.semilla, oscuro, cerca(pieY))
            haz(Offset(if (izqCesta) w * 0.17f else w * 0.83f, pieY + 1 * u), w * 0.16f, h * 0.05f, c.semilla + 3, oscuro, cerca(pieY))
        } else {
            // La sombra de la cesta en la pared: de día, a la derecha; de noche, la hoguera de abajo la echa hacia arriba.
            clipRect(0f, 0f, w, fondoY) {
                if (oscuro) {
                    val sc = Offset(cx, bocaY - alto * 0.05f)
                    withTransform({ scale(1f, 1.6f, sc) }) {
                        drawCircle(Brush.radialGradient(listOf(NOCHE.copy(alpha = 0.55f), NOCHE.copy(alpha = 0.4f), NOCHE.copy(alpha = 0f)), sc, ancho * 0.62f), ancho * 0.62f, sc)
                    }
                }
                else drawOval(sombra(oscuro, 0.16f), Offset(cx - ancho * 0.3f, bocaY - alto * 0.12f), Size(ancho * 1.1f, alto * 1.05f))
            }
            // Lo de los lados: el manojo de hierbas, el cubilete o las espigas, y el haz de las más llenas.
            val xa = if (izqCesta) w * 0.85f else w * 0.15f
            val xb = if (izqCesta) w * 0.16f else w * 0.84f
            when (Math.floorMod(c.semilla / 5, 3)) {
                0 -> hierbas(Offset(xa, h * 0.06f), h * 0.42f, c.semilla, oscuro, cerca(h * 0.3f))
                1 -> cubilete(Offset(xa, pieY), h * 0.16f, oscuro, cerca(pieY))
                else -> espigas(Offset(xa, pieY - 1 * u), w * 0.14f, c.semilla, oscuro, cerca(pieY))
            }
            if (!c.sinTema && k >= 7) haz(Offset(xb, pieY + 1 * u), w * 0.16f, h * 0.06f, c.semilla + 3, oscuro, cerca(pieY))
            for (j in 0 until 7) {
                val p = Offset(w * (0.08f + 0.84f * azar(c.semilla, j + 380)), fondoY + (canto - fondoY) * (0.35f + 0.6f * azar(c.semilla, j + 381)))
                drawOval(t(ESCANDA, canto), Offset(p.x - 1.2f * u, p.y - 0.7f * u), Size(2.4f * u, 1.4f * u))
            }
            if (c.sinTema) costal(Offset(cx, pieY), ancho * 0.92f, alto * 1.02f, k, ab, c.semilla, oscuro, cerca(pieY))
            else cesta(Offset(cx, pieY), ancho, alto, k, ab, c.semilla, oscuro, cerca(pieY))
            // Al abrirla, sube un poco de luz de la boca.
            if (ab > 0f) mancha(Offset(cx, bocaY), ancho * 0.6f, luzMueble(0.3f * ab))
        }

        // La viga del estante, de frente, con la clavija de donde cuelga el rótulo.
        val viga = Rect(0f, canto, w, h)
        roble(viga, Brush.verticalGradient(listOf(t(VIGA, canto), t(VIGA, h)), canto, h), c.semilla + 17, oscuro, vertical = false)
        drawLine(if (oscuro) TrazosGalia.FUEGO.copy(alpha = 0.2f) else luzMueble(0.55f), Offset(0f, canto + 0.5f * u), Offset(w, canto + 0.5f * u), 1f * u)
        if (oscuro) drawRect(Brush.verticalGradient(listOf(Color.Transparent, TrazosGalia.FUEGO_HALO.copy(alpha = 0.1f)), canto, h), viga.topLeft, viga.size)
        val r = Rect(w * rotulo.left - 3 * u, h * (rotulo.top - 0.012f), w * rotulo.right + 3 * u, h * (rotulo.bottom + 0.01f))
        if (c.vacio) {
            // Sin tablilla: solo la clavija vacía.
            clavija(Offset(r.center.x, r.top), 2f * u, t(VIGA_LUZ, canto), oscuro)
        } else etiqueta(r, c.semilla, oscuro, if (oscuro) 0.45f else 0f)
    }

    override val rotulo = Rect(0.15f, 0.73f, 0.85f, 0.965f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.ysabeau
    override val salida = 0f
    override val placa: Rect = Rect(0.3f, 0.12f, 0.7f, 0.42f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior: la cesta vista desde arriba

    /** Una banda de mimbre tejido dentro de [forma], con las hileras a lo largo ([horizontal]) o de arriba abajo. */
    private fun DrawScope.tejido(forma: Path, r: Rect, horizontal: Boolean, color: Color, semilla: Int, oscuro: Boolean) {
        val u = density
        clipPath(forma) {
            drawRect(mezcla(color, SOMBRA_MUEBLE, 0.3f), r.topLeft, r.size)
            val fila = 4.6f * u; val estaca = 11f * u
            val largo = if (horizontal) r.width else r.height
            val ancho = if (horizontal) r.height else r.width
            var a = 0f; var j = 0
            while (a < ancho) {
                var l = if (j % 2 == 0) 0f else -estaca / 2; var i = 0
                while (l < largo) {
                    val col = if (azar(semilla + j, i) > 0.5f) color else mezcla(color, MIMBRE_OSC, 0.15f)
                    if (horizontal) drawRoundRect(col, Offset(r.left + l + 0.5f * u, r.top + a + 0.35f * u), Size(estaca - 1f * u, fila - 0.7f * u), CornerRadius(fila * 0.42f))
                    else drawRoundRect(col, Offset(r.left + a + 0.35f * u, r.top + l + 0.5f * u), Size(fila - 0.7f * u, estaca - 1f * u), CornerRadius(fila * 0.42f))
                    l += estaca; i++
                }
                a += fila; j++
            }
            if (!oscuro) pinceladas(r, LUZ_MUEBLE, semilla + 5, 10, u, 0.08f)
        }
    }

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val pl = 20 * u; val pa = 34 * u; val pf = 18 * u
        val fondo = Rect(pl, pa, w - pl, h - pf)
        val centro = Offset(w * 0.5f, h * 0.52f)
        // Lo cerca que está cada punto de la tea que se sostiene encima (de noche).
        fun cerca(p: Offset) = (1f - (p - centro).getDistance() / (maxOf(w, h) * 0.8f)).coerceIn(0.12f, 1f)
        fun t(col: Color, p: Offset) = luz(col, oscuro, cerca(p))
        // El fondo tejido: las hileras de mimbre de lado a lado, parejas y sin mucho contraste, para que se lea encima.
        val piso = Path().apply { addRect(fondo) }
        drawRect(t(mezcla(MIMBRE, MIMBRE_OSC, 0.25f), centro), fondo.topLeft, fondo.size)
        clipPath(piso) {
            // Las hileras de mimbre de lado a lado: cada tramo entre dos estacas se abomba como una almohadilla, y las hileras se traban.
            val fila = 6f * u; val estaca = 14f * u
            drawRect(t(mezcla(MIMBRE, MIMBRE_OSC, 0.6f), centro), fondo.topLeft, fondo.size)
            var y = fondo.top; var j = 0
            while (y < fondo.bottom) {
                var x = fondo.left - (if (j % 2 == 0) 0f else estaca / 2); var i = 0
                while (x < fondo.right) {
                    val p = Offset(x + estaca / 2, y + fila / 2)
                    val tono = 0.18f * azar(700 + j, i)
                    drawRoundRect(t(mezcla(MIMBRE, MIMBRE_OSC, tono), p), Offset(x + 0.4f * u, y + 0.25f * u), Size(estaca - 0.8f * u, fila - 0.5f * u), CornerRadius(fila * 0.42f))
                    drawLine(luzMueble(if (oscuro) 0.03f else 0.2f), Offset(x + 3f * u, y + 1.5f * u), Offset(x + estaca - 4f * u, y + 1.4f * u), 0.9f * u, StrokeCap.Round)
                    drawLine(sombra(oscuro, 0.1f), Offset(x + 2.5f * u, y + fila - 1.3f * u), Offset(x + estaca - 2.5f * u, y + fila - 1.3f * u), 0.8f * u, StrokeCap.Round)
                    x += estaca; i++
                }
                y += fila; j++
            }
            // Lo que quedó en el fondo, junto a las paredes: tamo, granos de escanda, dos hojas de roble secas, una correa y el estilo de hierro.
            for (k in 0 until 50) {
                val borde = azar(711, k)
                val p = if (borde < 0.5f) Offset(fondo.left + fondo.width * azar(712, k), if (borde < 0.25f) fondo.top + 30 * u * azar(713, k) else fondo.bottom - 30 * u * azar(713, k))
                else Offset(if (borde < 0.75f) fondo.left + 24 * u * azar(713, k) else fondo.right - 24 * u * azar(713, k), fondo.top + fondo.height * azar(712, k))
                val a = azar(714, k) * 6.28f
                if (k % 2 == 0) drawLine(t(PAJA_LUZ, p).copy(alpha = 0.8f), p, p + Offset(cos(a) * 5f * u, sin(a) * 5f * u), 0.8f * u, StrokeCap.Round)
                else rotate(a * 57.3f, p) {
                    drawOval(sombra(oscuro, 0.25f), Offset(p.x - 1.2f * u, p.y - 0.4f * u), Size(3f * u, 1.8f * u))
                    drawOval(t(ESCANDA, p), Offset(p.x - 1.5f * u, p.y - 0.9f * u), Size(3f * u, 1.8f * u))
                }
            }
            val seca = Color(0xFF9A7E42); val secaNervio = Color(0xFF6A5228)
            listOf(Offset(fondo.right - w * 0.14f, fondo.top + h * 0.06f) to 35f, Offset(fondo.left + w * 0.12f, fondo.bottom - h * 0.1f) to -125f).forEach { (p, g) ->
                with(TrazosGalia) {
                    hojaRoble(p + Offset(2 * u, 3 * u), w * 0.13f, g, sombra(oscuro, 0.25f), Color.Transparent)
                    hojaRoble(p, w * 0.13f, g, luz(seca, oscuro, cerca(p)), luz(secaNervio, oscuro, cerca(p)))
                }
            }
            val correa = t(CUERO, Offset(w * 0.5f, fondo.bottom))
            val curvaCorrea = Path().apply {
                moveTo(fondo.left - 4 * u, fondo.bottom - h * 0.03f)
                cubicTo(w * 0.3f, fondo.bottom - h * 0.07f, w * 0.42f, fondo.bottom + h * 0.01f, w * 0.6f, fondo.bottom - h * 0.035f)
                cubicTo(w * 0.7f, fondo.bottom - h * 0.06f, w * 0.62f, fondo.bottom - h * 0.1f, w * 0.55f, fondo.bottom - h * 0.075f)
            }
            translate(1.5f * u, 2f * u) { drawPath(curvaCorrea, sombra(oscuro, 0.3f), style = Stroke(3f * u, cap = StrokeCap.Round)) }
            drawPath(curvaCorrea, correa, style = Stroke(2.6f * u, cap = StrokeCap.Round))
            drawPath(curvaCorrea, luzMueble(if (oscuro) 0.04f else 0.25f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
            // El estilo de hierro: la punta para rayar la cera y la paleta para borrarla.
            val e0 = Offset(fondo.right - w * 0.36f, fondo.bottom - h * 0.06f)
            rotate(-14f, e0) {
                val l = w * 0.3f
                drawLine(sombra(oscuro, 0.35f), e0 + Offset(2 * u, 3 * u), e0 + Offset(l + 2 * u, 3 * u), 3f * u, StrokeCap.Round)
                val hierro = t(TrazosGalia.HIERRO, e0)
                drawPath(Path().apply { moveTo(e0.x, e0.y); lineTo(e0.x + l * 0.85f, e0.y - 1.3f * u); lineTo(e0.x + l * 0.85f, e0.y + 1.3f * u); close() }, hierro)
                drawPath(Path().apply { moveTo(e0.x + l * 0.85f, e0.y - 1f * u); lineTo(e0.x + l, e0.y - 3f * u); lineTo(e0.x + l, e0.y + 3f * u); lineTo(e0.x + l * 0.85f, e0.y + 1f * u); close() }, hierro)
                drawLine(luzMueble(if (oscuro) 0.08f else 0.5f), e0 + Offset(l * 0.1f, -0.5f * u), e0 + Offset(l * 0.84f, -1f * u), 0.6f * u)
                for (a in 1..3) drawLine(mezcla(hierro, SOMBRA_MUEBLE, 0.4f), e0 + Offset(l * (0.6f + 0.05f * a), -1.2f * u), e0 + Offset(l * (0.6f + 0.05f * a), 1.2f * u), 0.5f * u)
            }
        }
        // De noche, solo la tea que se sostiene encima alumbra el fondo; las esquinas se hunden.
        if (oscuro) {
            mancha(centro, minOf(w, h) * 0.9f, TrazosGalia.FUEGO_HALO.copy(alpha = 0.14f))
            drawRect(Brush.radialGradient(listOf(Color.Transparent, NOCHE.copy(alpha = 0.65f)), centro, maxOf(w, h) * 0.7f))
        }
        // Las paredes de mimbre, vistas desde arriba: la de atrás y la de la izquierda en sombra, la de delante y la de la derecha a la luz.
        val atras = Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w - pl, pa); lineTo(pl, pa); close() }
        val izq = Path().apply { moveTo(0f, 0f); lineTo(pl, pa); lineTo(pl, h - pf); lineTo(0f, h); close() }
        val der = Path().apply { moveTo(w, 0f); lineTo(w, h); lineTo(w - pl, h - pf); lineTo(w - pl, pa); close() }
        val delante = Path().apply { moveTo(0f, h); lineTo(pl, h - pf); lineTo(w - pl, h - pf); lineTo(w, h); close() }
        tejido(atras, Rect(0f, 0f, w, pa), true, t(mezcla(MIMBRE, SOMBRA_MUEBLE, 0.35f), Offset(w / 2, 0f)), 721, oscuro)
        tejido(izq, Rect(0f, 0f, pl, h), false, t(mezcla(MIMBRE, SOMBRA_MUEBLE, 0.25f), Offset(0f, h / 2)), 731, oscuro)
        tejido(der, Rect(w - pl, 0f, w, h), false, t(MIMBRE, Offset(w, h / 2)), 741, oscuro)
        tejido(delante, Rect(0f, h - pf, w, h), true, t(mezcla(MIMBRE, LUZ_MUEBLE, 0.2f), Offset(w / 2, h)), 751, oscuro)
        drawPath(Path().apply { addRect(fondo) }, sombra(oscuro, if (oscuro) 0.6f else 0.35f), style = Stroke(1.2f * u))
        sombraInterior(fondo, 22 * u, if (oscuro) 0.4f else 0.25f)
        // El borde trenzado, alrededor: tres varas que se cruzan, en luz y en sombra.
        val borde = 9 * u
        val oscuroBorde = t(MIMBRE_OSC, centro)
        val claroBorde = t(MIMBRE_LUZ, centro)
        listOf(Rect(0f, 0f, w, borde), Rect(0f, h - borde, w, h), Rect(0f, 0f, borde, h), Rect(w - borde, 0f, w, h)).forEachIndexed { i, r ->
            drawRect(oscuroBorde, r.topLeft, r.size)
            val horizontal = i < 2
            val largo = if (horizontal) r.width else r.height
            var s = 0f
            while (s < largo) {
                if (horizontal) {
                    drawLine(claroBorde, Offset(r.left + s, r.bottom - 1.5f * u), Offset(r.left + s + 4f * u, r.top + 1.5f * u), 2.2f * u, StrokeCap.Round)
                    drawLine(luzMueble(if (oscuro) 0.05f else 0.35f), Offset(r.left + s + 0.6f * u, r.bottom - 2.4f * u), Offset(r.left + s + 3f * u, r.top + 2.4f * u), 0.6f * u)
                } else {
                    drawLine(claroBorde, Offset(r.left + 1.5f * u, r.top + s), Offset(r.right - 1.5f * u, r.top + s + 4f * u), 2.2f * u, StrokeCap.Round)
                    drawLine(luzMueble(if (oscuro) 0.05f else 0.35f), Offset(r.left + 2.4f * u, r.top + s + 0.6f * u), Offset(r.right - 2.4f * u, r.top + s + 3f * u), 0.6f * u)
                }
                s += 3.6f * u
            }
        }
        drawRect(sombra(oscuro, 0.35f), Offset(borde, borde), Size(w - 2 * borde, 1.2f * u))
        drawRect(sombra(oscuro, 0.3f), Offset(borde, borde), Size(1.2f * u, h - 2 * borde))
        granoMueble(Rect(0f, 0f, w, h), oscuro, 0.7f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) Color(0xFFE6E1CF) else TINTA
    override val pared: Dp = 22.dp
    override val columnas = 2
    override val lineas = 4

    // ---------------------------------------------------------------- las fichas: tablillas de haya

    /** El corte de las esquinas de una ficha de [ancho] × [alto]: proporcional al lado menor. */
    private fun corteFicha(ancho: Float, alto: Float) = minOf(ancho, alto) * 0.055f

    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        addPath(contornoTablilla(Rect(0f, 0f, s.width, s.height), semilla, corteFicha(s.width, s.height)))
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.5f else 1f
        val marco = 5.5f * u * g
        val corte = corteFicha(w, h)
        val fuera = contornoTablilla(Rect(0f, 0f, w, h), semilla, corte)
        val dentro = contornoTablilla(Rect(marco, marco, w - marco, h - marco), semilla, (corte - marco * 0.4f).coerceAtLeast(1f))
        val anillo = Path().apply { addPath(fuera); addPath(dentro); fillType = PathFillType.EvenOdd }
        // El marco de haya: más claro arriba a la izquierda, con su veta y sus motas.
        drawPath(anillo, Brush.linearGradient(listOf(HAYA_LUZ, HAYA, HAYA_OSC), Offset.Zero, Offset(w, h)))
        clipPath(anillo) {
            vetas(Rect(0f, 0f, w, marco), HAYA_OSC.copy(alpha = 0.4f), semilla + 1, densidad = 1.2f, grosor = 0.6f, ondulacion = 0.4f)
            vetas(Rect(0f, h - marco, w, h), HAYA_OSC.copy(alpha = 0.4f), semilla + 2, densidad = 1.2f, grosor = 0.6f, ondulacion = 0.4f)
            vetas(Rect(0f, 0f, marco, h), HAYA_OSC.copy(alpha = 0.4f), semilla + 3, densidad = 1.2f, vertical = true, grosor = 0.6f, ondulacion = 0.4f)
            vetas(Rect(w - marco, 0f, w, h), HAYA_OSC.copy(alpha = 0.4f), semilla + 4, densidad = 1.2f, vertical = true, grosor = 0.6f, ondulacion = 0.4f)
            motas(Rect(0f, 0f, w, marco), HAYA_MOTA.copy(alpha = 0.55f), semilla + 5, 1.4f)
            motas(Rect(0f, h - marco, w, h), HAYA_MOTA.copy(alpha = 0.55f), semilla + 6, 1.4f)
            motas(Rect(0f, 0f, marco, h), HAYA_MOTA.copy(alpha = 0.55f), semilla + 7, 1.4f, vertical = true)
            motas(Rect(w - marco, 0f, w, h), HAYA_MOTA.copy(alpha = 0.55f), semilla + 8, 1.4f, vertical = true)
            // El canto de afuera, gastado, en luz.
            drawPath(fuera, luzMueble(0.45f), style = Stroke(1.6f * u))
        }
        drawPath(fuera, mezcla(HAYA_OSC, VIGA_OSC, 0.4f), style = Stroke(1f * u))
        // La cera hundida en el marco: la sombra que echa el marco arriba y a la izquierda, y el filo de luz abajo a la derecha.
        clipPath(dentro) {
            drawPath(dentro, sombraMueble(0.32f), style = Stroke(3.2f * u * g))
            drawRect(Brush.verticalGradient(listOf(sombraMueble(0.18f), Color.Transparent), marco, marco + 7f * u * g), Offset(marco, marco), Size(w - 2 * marco, 7f * u * g))
        }
        drawLine(luzMueble(0.6f), Offset(marco + corte * 0.4f, h - marco + 0.4f * u), Offset(w - marco - corte * 0.4f, h - marco + 0.4f * u), 0.8f * u)
        drawLine(luzMueble(0.5f), Offset(w - marco + 0.4f * u, marco + corte * 0.4f), Offset(w - marco + 0.4f * u, h - marco - corte * 0.4f), 0.8f * u)
        // El agujero arriba, al centro del marco, y la correa de cuero que pasa por él y sale por el canto.
        val a = Offset(w * 0.5f, marco * 0.5f)
        val correa = CUERO
        val cabo1 = Path().apply { moveTo(a.x, a.y); quadraticTo(a.x - 6f * u * g, a.y + 0.8f * u * g, a.x - 12f * u * g, a.y - 0.4f * u * g); lineTo(a.x - 15f * u * g, -1f * u) }
        val cabo2 = Path().apply { moveTo(a.x, a.y); quadraticTo(a.x + 5f * u * g, a.y + 1f * u * g, a.x + 9f * u * g, a.y + 0.2f * u * g) }
        listOf(cabo1, cabo2).forEach { p0 ->
            translate(0.6f * u, 0.9f * u) { drawPath(p0, sombraMueble(0.3f), style = Stroke(1.8f * u * g, cap = StrokeCap.Round)) }
            drawPath(p0, correa, style = Stroke(1.5f * u * g, cap = StrokeCap.Round))
            drawPath(p0, luzMueble(0.25f), style = Stroke(0.4f * u * g, cap = StrokeCap.Round))
        }
        drawCircle(mezcla(HAYA_OSC, VIGA_OSC, 0.6f), 2.1f * u * g, a)
        drawCircle(correa, 1.7f * u * g, a)
        drawCircle(luzMueble(0.35f), 0.6f * u * g, a - Offset(0.5f * u, 0.5f * u))
        // La «S» grabada de su dueño, en el marco de abajo a la derecha.
        val ese = Offset(marco + corte + 8f * u * g, h - marco * 0.5f)
        with(TrazosGalia) {
            esse(ese + Offset(0.5f * u, 0.5f * u), marco * 2.6f, marco * 0.14f, luzMueble(0.5f))
            esse(ese, marco * 2.6f, marco * 0.14f, VIGA_OSC.copy(alpha = 0.6f))
        }
    }

    override val margenes = MargenesFicha(15.dp, 14.dp, 13.dp, 27.dp, 28.dp, 26.dp)
    override val proporcionFicha = 1.1f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.8f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El estátero de oro con su caballo: pagado, hecho.
            "hecho" -> {
                drawCircle(mezcla(TrazosGalia.ORO, Color.Black, 0.4f), s * 0.48f, c + Offset(s * 0.02f, s * 0.03f))
                drawCircle(TrazosGalia.ORO, s * 0.45f, c)
                drawCircle(TrazosGalia.ORO_BRILLO.copy(alpha = 0.7f), s * 0.4f, c, style = Stroke(s * 0.04f))
                with(TrazosGalia) { caballo(c + Offset(0f, s * 0.04f), s * 0.62f, mezcla(TrazosGalia.ORO, Color.Black, 0.5f)) }
                drawCircle(mezcla(TrazosGalia.ORO, Color.Black, 0.5f), s * 0.05f, c + Offset(-s * 0.02f, -s * 0.25f))
            }
            // La hoja del roble sagrado: convence.
            "convence" -> with(TrazosGalia) { hojaRoble(Offset(c.x, c.y + s * 0.04f), s * 0.92f, -18f, TrazosGalia.ROBLE, TrazosGalia.ROBLE_SOMBRA) }
            // La «S» de las monedas, que da vueltas sobre sí: le sigue pensando.
            "piensa" -> with(TrazosGalia) { esse(c, s * 0.92f, s * 0.12f, p.acento, -25f) }
            // La luna que todavía no se llena: se espera.
            "duda" -> {
                val luna = Path().apply { addOval(Rect(c, s * 0.4f)) }
                val corte = Path().apply { addOval(Rect(c + Offset(s * 0.2f, -s * 0.1f), s * 0.34f)) }
                drawPath(Path().apply { op(luna, corte, PathOperation.Difference) }, p.tenue)
            }
            // La espada doblada, inutilizada como las de las ofrendas: eso no.
            "desacuerdo" -> {
                val hierro = TrazosGalia.HIERRO
                val mango = Offset(c.x - s * 0.34f, c.y + s * 0.3f)
                val codo = Offset(c.x + s * 0.06f, c.y - s * 0.16f)
                val punta = Offset(c.x + s * 0.42f, c.y + s * 0.1f)
                drawLine(hierro, mango, codo, s * 0.12f, StrokeCap.Butt)
                drawLine(hierro, codo, punta, s * 0.11f, StrokeCap.Round)
                drawCircle(hierro, s * 0.06f, codo)
                drawLine(mezcla(hierro, Color.White, 0.45f), mango + Offset(-s * 0.02f, -s * 0.03f), codo + Offset(-s * 0.02f, -s * 0.03f), s * 0.03f)
                val d = (codo - mango) / (codo - mango).getDistance()
                val nn = Offset(-d.y, d.x)
                drawLine(mezcla(hierro, Color.Black, 0.3f), mango + nn * (s * 0.1f), mango - nn * (s * 0.1f), s * 0.07f, StrokeCap.Round)
                val pomo = mango - d * (s * 0.18f)
                drawLine(CUERO, mango, pomo, s * 0.08f, StrokeCap.Round)
                drawCircle(mezcla(hierro, Color.Black, 0.3f), s * 0.06f, pomo)
            }
            else -> {}
        }
    }

    /** Una fíbula de bronce, con su resorte, su arco, el pie vuelto y la aguja, prendida de la tablilla. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        val br = TrazosGalia.BRONCE
        val brl = mezcla(br, Color.White, 0.4f)
        val brs = mezcla(br, Color.Black, 0.35f)
        val resorte = Offset(c.x - s * 0.3f, c.y + s * 0.08f)
        // La aguja, debajo de todo.
        drawLine(brs, resorte + Offset(0f, s * 0.1f), Offset(c.x + s * 0.36f, c.y + s * 0.18f), s * 0.05f, StrokeCap.Round)
        // El arco y el pie vuelto con su bolita.
        val arco = Path().apply {
            moveTo(resorte.x, resorte.y - s * 0.12f)
            quadraticTo(c.x - s * 0.02f, c.y - s * 0.6f, c.x + s * 0.32f, c.y + s * 0.12f)
            quadraticTo(c.x + s * 0.3f, c.y - s * 0.02f, c.x + s * 0.16f, c.y - s * 0.1f)
        }
        drawPath(arco, brs, style = Stroke(s * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(arco, br, style = Stroke(s * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(br, s * 0.07f, Offset(c.x + s * 0.16f, c.y - s * 0.1f))
        drawPath(Path().apply { moveTo(resorte.x + s * 0.06f, resorte.y - s * 0.16f); quadraticTo(c.x - s * 0.04f, c.y - s * 0.5f, c.x + s * 0.14f, c.y - s * 0.26f) },
            brl, style = Stroke(s * 0.03f, cap = StrokeCap.Round))
        // El resorte: las vueltas del alambre.
        drawCircle(brs, s * 0.15f, resorte)
        drawCircle(br, s * 0.12f, resorte)
        for (k in 0 until 3) drawLine(brs, resorte + Offset(-s * 0.08f + k * s * 0.07f, -s * 0.11f), resorte + Offset(-s * 0.1f + k * s * 0.07f, s * 0.11f), s * 0.025f)
        drawCircle(brl, s * 0.035f, resorte - Offset(s * 0.05f, s * 0.05f))
    }

    override val desorden = 1.5f
}
