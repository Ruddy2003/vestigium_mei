package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
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
import io.github.ruddy2003.vestigium.ui.TrazosArcontes
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraContacto
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Arcontes
// =====================================================================

/**
 * Arcontes: la cueva de la reunión escondida, en la caliza de un monte sobre el lago de Galilea (`docs/diseno/temas/arcontes.md`).
 * Arriba, a la izquierda, la boca de la cueva, medio tapada por un velo de lino recogido con un cordel: de día se ve por ella
 * el lago con una barca de vela y los montes de la otra orilla en la calima, y entra un rayo de sol con su polvo dorado (el
 * oro de más del mundo visible); de noche, el cielo antiguo con sus estrellas y los círculos de las siete esferas, una nube
 * donde se adivina la sombra de un arconte y, en el lago, la lámpara de una barca que pesca. En la roca, una repisa tallada
 * con la lámpara de aceite (encendida de noche, con el tizne de tantas noches encima) y la alcuza; a la derecha, grabado a
 * cincel, el diagrama de las siete esferas (recurso: así lo dibuja el tema) y una grieta fina con la luz blanca y violeta.
 * Cada tema es un nicho de arco rebajado tallado en la caliza, con una arqueta de madera de olivo: la tapa con su canto, el
 * frente con su friso de muescas a punta de cuchillo, las esquinas y la cerradura de bronce y el pasador de hueso del que
 * cuelga, de un cordel de lino, la etiqueta de papiro con el tema. Cuantas más hojas guarda, más se le levanta la tapa y
 * asoman los cantos de las hojas; las más llenas tienen un legajo atado al lado. Lo que no tiene tema va en un cesto de
 * juncos, con las hojas sueltas y sin atar. Al lado, una lámpara de barro: apagada de día, encendida de noche, cuando es la
 * única luz del nicho. Al abrirlo, la tapa se levanta y se ve el legajo atado con su cordel en cruz y, por dentro de la
 * tapa, el ouroboros grabado: la contraseña de la comunidad. Abajo, el poyo de piedra donde se sientan los de la reunión,
 * con un manto de lana doblado, un cuenco y otra lámpara, y en el suelo unas sandalias. Un nicho todavía sin tallar es solo
 * el arco trazado en la piedra y el picado del cincel. Por dentro, la arqueta vista desde arriba: el fondo de tablas de
 * olivo con sus vetas, las paredes con su canto, las bisagras, el cordel desatado, un cálamo y un manojo de hisopo. Las
 * fichas son hojas sueltas de papiro, de borde deshilachado, con la juntura de dos hojas pegadas y los dos agujeros del
 * cordel que las ata.
 */
object IdeasArcontes : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta de la cueva
    private val CALIZA = TrazosArcontes.CALIZA
    private val CALIZA_SOMBRA = TrazosArcontes.CALIZA_SOMBRA
    private val CALIZA_LUZ = Color(0xFFEBDFBF)
    /** El fondo de los nichos, en sombra, y lo más hondo. */
    private val HUECO = Color(0xFF8E7C5E)
    private val HUECO_HONDO = Color(0xFF5A4C3C)
    /** La madera de olivo: su color, su luz, su sombra y la veta oscura que se arremolina. */
    private val OLIVO = Color(0xFFB39567)
    private val OLIVO_LUZ = Color(0xFFD6BE8E)
    private val OLIVO_OSC = Color(0xFF7C623E)
    private val OLIVO_VETA = Color(0xFF4F3B25)
    /** El bronce de las esquinas y la cerradura, y el hueso del pasador. */
    private val BRONCE = Color(0xFF8C6A34)
    private val BRONCE_LUZ = Color(0xFFC9A45E)
    private val HUESO = Color(0xFFE8DEC6)
    /** El papiro de las hojas y su fibra; el cordel de lino. */
    private val PAPIRO = Color(0xFFEDE3C8)
    private val PAPIRO_OSC = Color(0xFFD6C49B)
    private val FIBRA = Color(0xFF8E7650)
    private val CORDEL = Color(0xFFCDBB8E)
    private val LINO = TrazosArcontes.LINO
    /** La tinta de carbón, violácea de tan negra. */
    private val TINTA = Color(0xFF1E1A2B)
    private val BARRO = TrazosArcontes.BARRO
    private val BARRO_CLARO = TrazosArcontes.BARRO_CLARO
    private val ORO = TrazosArcontes.ORO_DEMIURGO
    private val LLAMA = TrazosArcontes.LLAMA
    private val LLAMA_HALO = TrazosArcontes.LLAMA_HALO
    private val CIELO = TrazosArcontes.CIELO_ANTIGUO
    private val ESFERA = TrazosArcontes.ESFERA
    private val GRIETA = TrazosArcontes.GRIETA
    private val LAGO = TrazosArcontes.LAGO
    private val LAGO_HONDO = TrazosArcontes.LAGO_HONDO
    /** Los juncos del cesto, la lana del manto y el cuero de las sandalias. */
    private val JUNCO = Color(0xFFB59A5E)
    private val JUNCO_OSC = Color(0xFF7A6438)
    private val LANA = Color(0xFF8B7357)
    private val CUERO = TrazosArcontes.CUERO
    /** La tierra apisonada del suelo de la cueva. */
    private val TIERRA = Color(0xFF7F6A4C)
    /** La noche de la cueva: el azul negro del cielo antiguo, donde se hunde la piedra lejos de la lámpara. */
    private val NOCHE = Color(0xFF0A0C1B)

    /** Un color de la cueva: de noche, hundido en el azul negro (lo que no alumbra ninguna lámpara). */
    private fun n(c: Color, oscuro: Boolean, k: Float = 0.84f) = if (oscuro) mezcla(c, NOCHE, k) else c

    /** Un color a [cerca] (0 a 1) de una lámpara: de día, tal cual; de noche, menos hundido y calentado por la llama. */
    private fun luz(c: Color, oscuro: Boolean, cerca: Float): Color {
        if (!oscuro) return c
        val k = cerca.coerceIn(0f, 1f)
        return mezcla(mezcla(c, NOCHE, 0.84f - 0.5f * k), LLAMA, 0.16f * k)
    }

    override val cols = 2
    override val proporcion = 1.22f
    override val corona = 0.42f
    override val base = 0.17f
    override val lado = 0.06f
    override val hueco = 0.045f

    /** El alto de la boca del nicho (el resto del compartimento es la roca donde cuelga la etiqueta), en fracción del alto. */
    private const val PISO = 0.7f
    /** La flecha del arco rebajado del nicho, en fracción del alto. */
    private const val FLECHA = 0.17f

    // ---------------------------------------------------------------- ayudantes de la cueva

    /**
     * La caliza dentro de [r]: el color [base], los estratos anchos y finos que la cruzan, los poros, el picado del cincel en
     * parches de golpes paralelos y unas pinceladas que rompen el plano. [fuerza] baja el dibujo (en los fondos en sombra).
     */
    private fun DrawScope.piedra(r: Rect, base: Color, semilla: Int, fuerza: Float = 1f) {
        val u = density
        drawRect(base, r.topLeft, r.size)
        val oscura = mezcla(base, SOMBRA_MUEBLE, 0.55f)
        val clara = mezcla(base, LUZ_MUEBLE, 0.5f)
        clipRect(r.left, r.top, r.right, r.bottom) {
            // Los estratos: bandas onduladas, una más clara cada tanto.
            var y = r.top + 6 * u * azar(semilla, 900); var k = 0
            while (y < r.bottom) {
                val alto = (2.5f + 7f * azar(semilla, k + 910)) * u
                val fase = azar(semilla, k + 920) * 6.28f
                val banda = Path()
                val pasos = 12
                for (s in 0..pasos) {
                    val x = r.left + r.width * s / pasos
                    val yy = y + 2.2f * u * sin(fase + s * 0.9f)
                    if (s == 0) banda.moveTo(x, yy) else banda.lineTo(x, yy)
                }
                for (s in pasos downTo 0) {
                    val x = r.left + r.width * s / pasos
                    banda.lineTo(x, y + alto + 1.6f * u * sin(fase * 1.3f + s * 0.7f))
                }
                banda.close()
                drawPath(banda, (if (k % 3 == 1) clara else oscura).copy(alpha = (0.05f + 0.06f * azar(semilla, k + 930)) * fuerza))
                y += (12f + 26f * azar(semilla, k + 940)) * u; k++
            }
        }
        vetas(r, oscura.copy(alpha = 0.13f * fuerza), semilla, densidad = 0.2f, grosor = 1.1f, ondulacion = 0.6f)
        // Los poros de la caliza.
        val poros = (r.width * r.height / (u * u * 80f)).toInt().coerceIn(8, 300)
        for (k in 0 until poros) {
            val p = Offset(r.left + r.width * azar(semilla + 1, k), r.top + r.height * azar(semilla + 2, k))
            val t = (0.35f + 0.9f * azar(semilla + 3, k)) * u
            if (azar(semilla + 4, k) > 0.82f) drawCircle(clara.copy(alpha = 0.45f * fuerza), t, p)
            else drawCircle(oscura.copy(alpha = (0.12f + 0.2f * azar(semilla + 5, k)) * fuerza), t, p)
        }
        // El picado del cincel: parches de cinco golpes paralelos, cada parche con su inclinación.
        val parches = (r.width * r.height / (u * u * 1800f)).toInt().coerceIn(1, 36)
        for (k in 0 until parches) {
            val o = Offset(r.left + r.width * azar(semilla + 6, k), r.top + r.height * azar(semilla + 7, k))
            val a = 0.9f + 0.8f * azar(semilla + 8, k)
            val l = (2.5f + 2.5f * azar(semilla + 9, k)) * u
            val dx = cos(a) * l; val dy = sin(a) * l
            for (j in 0 until 5) {
                val p = o + Offset(j * 1.7f * u, -j * 0.5f * u)
                drawLine(oscura.copy(alpha = 0.22f * fuerza), p, p + Offset(dx, dy), 0.7f * u, StrokeCap.Round)
                drawLine(clara.copy(alpha = 0.2f * fuerza), p + Offset(0.6f * u, 0f), p + Offset(dx + 0.6f * u, dy), 0.45f * u, StrokeCap.Round)
            }
        }
        val pinc = (r.width * r.height / (u * u * 500f)).toInt().coerceIn(4, 110)
        pinceladas(r, clara, semilla + 11, pinc, u, 0.06f * fuerza)
        pinceladas(r, oscura, semilla + 13, pinc, u, 0.05f * fuerza)
    }

    /** Un cordel de lino torcido a lo largo de [p]: su sombra, el cuerpo y las vueltas de la torsión. */
    private fun DrawScope.cordel(p: Path, color: Color, g: Float) {
        drawPath(p, mezcla(color, SOMBRA_MUEBLE, 0.5f), style = Stroke(g * 1.35f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, color, style = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, mezcla(color, LUZ_MUEBLE, 0.45f), style = Stroke(g * 0.4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 0.8f, g * 1.1f))))
    }

    /** Un cordel recto de [a] a [b], que se comba un poco por su peso ([comba], hacia abajo). */
    private fun DrawScope.cordel(a: Offset, b: Offset, color: Color, g: Float, comba: Float = 0f) =
        cordel(Path().apply { moveTo(a.x, a.y); quadraticTo((a.x + b.x) / 2, (a.y + b.y) / 2 + comba, b.x, b.y) }, color, g)

    /** El nicho de arco rebajado dentro de [r]: el arco es un segmento de círculo de flecha [flecha], el arco pequeño de la caliza. */
    private fun nicho(r: Rect, flecha: Float): Path = Path().apply {
        val c = r.width / 2
        val f = flecha.coerceIn(1f, c)
        val radio = (c * c + f * f) / (2 * f)
        val cx = r.center.x; val cy = r.top + radio
        val a0 = Math.toDegrees(kotlin.math.atan2((f - radio).toDouble(), (-c).toDouble())).toFloat()
        moveTo(r.left, r.bottom); lineTo(r.left, r.top + f)
        arcTo(Rect(cx - radio, cy - radio, cx + radio, cy + radio), a0, -180f - 2 * a0, false)
        lineTo(r.right, r.bottom); close()
    }

    /** La lámpara de aceite de barro sobre una piedra ([TrazosArcontes.lampara]): encendida, con el halo de su llama. */
    private fun DrawScope.lamparaEn(base: Offset, lw: Float, oscuro: Boolean, encendida: Boolean, cerca: Float = 0.9f) {
        val c = Offset(base.x, base.y - lw * 0.36f * 0.45f)
        sombraContacto(Offset(base.x + lw * 0.06f, base.y), lw * 0.82f, lw * 0.12f, if (oscuro) 0.45f else 0.3f)
        val barro = if (encendida && oscuro) mezcla(luz(BARRO_CLARO, true, cerca), LLAMA, 0.2f) else luz(BARRO_CLARO, oscuro, cerca)
        with(TrazosArcontes) { lampara(c, lw, barro, if (encendida) 1f else 0f) }
        // Apagada: el pico ennegrecido por el humo.
        if (!encendida) drawCircle(TINTA.copy(alpha = 0.45f), lw * 0.05f, Offset(c.x + lw * 0.4f, c.y - lw * 0.36f * 0.36f))
    }

    /** Dónde está la llama de una lámpara puesta en [base] con ancho [lw]. */
    private fun llama(base: Offset, lw: Float) = Offset(base.x + lw * 0.395f, base.y - lw * 0.36f * 0.45f - lw * 0.36f * 0.4f - lw * 0.12f)

    /** El halo de una lámpara encendida sobre lo que la rodea, de noche: un círculo cálido grande y otro chico. */
    private fun DrawScope.halo(f: Offset, r: Float, fuerza: Float = 1f) {
        mancha(f, r, LLAMA_HALO.copy(alpha = 0.26f * fuerza))
        mancha(f, r * 0.3f, LLAMA_HALO.copy(alpha = 0.3f * fuerza))
    }

    /** Una hoja de papiro (o la etiqueta) de borde deshilachado dentro de [r]. */
    private fun hoja(r: Rect, semilla: Int, puntos: Int = 40, hondo: Float = 0.012f): Path = contornoIrregular(r, semilla, puntos, hondo)

    /** Las fibras del papiro en [r]: la capa horizontal, más marcada, y la vertical, apenas. */
    private fun DrawScope.fibras(r: Rect, color: Color, semilla: Int, paso: Float) {
        val u = density
        var y = r.top + paso * 0.5f; var k = 0
        while (y < r.bottom) {
            val x0 = r.left + r.width * 0.25f * azar(semilla + 6, k)
            drawLine(color.copy(alpha = color.alpha * (0.25f + 0.75f * azar(semilla, k))), Offset(x0, y), Offset(r.right - r.width * 0.2f * azar(semilla + 7, k), y + (azar(semilla + 1, k) - 0.5f) * u), 0.45f * u)
            y += paso * (0.7f + 0.6f * azar(semilla + 2, k)); k++
        }
        var x = r.left + paso * 3 * azar(semilla + 4, 0); k = 0
        while (x < r.right) {
            drawLine(color.copy(alpha = color.alpha * 0.14f * (0.4f + azar(semilla + 5, k))), Offset(x, r.top), Offset(x, r.bottom), 0.7f * u)
            x += paso * (5f + 8f * azar(semilla + 3, k)); k++
        }
    }

    // ---------------------------------------------------------------- la boca de la cueva

    /** El contorno de la boca de la cueva en [m]: un arco irregular de roca, con el umbral casi plano abajo. */
    private fun boca(m: Rect): Path = Path().apply {
        val pasos = 22
        val cx = m.center.x; val rx = m.width / 2; val ry = m.height * 0.86f
        moveTo(m.left + m.width * 0.02f, m.bottom)
        for (k in 0..pasos) {
            val a = PI * (1.0 - k / pasos.toDouble())
            val ruido = 1f + (azar(77, k) - 0.5f) * 0.09f
            val x = cx + (rx * ruido * cos(a)).toFloat()
            val y = m.bottom - (ry * ruido * sin(a).toFloat().let { if (it < 0f) 0f else Math.pow(it.toDouble(), 0.7).toFloat() }) - m.height * 0.1f * (1f - abs(cos(a).toFloat()))
            lineTo(x, y.coerceAtLeast(m.top))
        }
        lineTo(m.right - m.width * 0.03f, m.bottom)
        quadraticTo(cx, m.bottom + m.height * 0.03f, m.left + m.width * 0.02f, m.bottom)
        close()
    }

    /** Lo que se ve por la boca: de día el lago, la barca y los montes en la calima; de noche el cielo antiguo, las esferas y la barca que pesca con su lámpara. */
    private fun DrawScope.afuera(m: Rect, oscuro: Boolean) {
        val u = density
        val horizonte = m.top + m.height * 0.5f
        val orilla = m.top + m.height * 0.8f
        if (!oscuro) {
            // El cielo de calima, con el resplandor del sol, más dorado de la cuenta.
            drawRect(Brush.verticalGradient(listOf(Color(0xFFF8E6B4), Color(0xFFF6EDD7)), m.top, horizonte), m.topLeft, Size(m.width, horizonte - m.top + 2 * u))
            mancha(Offset(m.left + m.width * 0.18f, m.top + m.height * 0.05f), m.width * 0.7f, ORO.copy(alpha = 0.32f))
            pinceladas(Rect(m.left, m.top, m.right, horizonte), Color.White, 81, 10, u, 0.18f)
        } else {
            // El cielo antiguo, sus estrellas y los círculos de las siete esferas, que solo se ven de noche.
            drawRect(Brush.verticalGradient(listOf(CIELO, Color(0xFF1C2142)), m.top, horizonte), m.topLeft, Size(m.width, horizonte - m.top + 2 * u))
            with(TrazosArcontes) { esferas(Offset(m.left + m.width * 0.62f, m.top - m.height * 0.42f), m.width * 1.15f, ESFERA.copy(alpha = 0.55f), 0.8f * u, 0.18f) }
            for (k in 0 until 46) {
                val p = Offset(m.left + m.width * azar(83, k), m.top + (horizonte - m.top) * azar(84, k))
                val t = (0.35f + 0.8f * azar(85, k) * azar(86, k)) * u
                drawCircle(GRIETA.copy(alpha = 0.45f + 0.5f * azar(87, k)), t, p)
            }
            // Una nube baja y, dentro, la sombra de un arconte que solo se adivina, cortada por la roca.
            val nube = Offset(m.right - m.width * 0.12f, m.top + m.height * 0.2f)
            for (k in 0 until 6) drawOval(Color(0xFF262B4C).copy(alpha = 0.55f), Offset(nube.x - m.width * (0.3f - 0.07f * k), nube.y - m.height * (0.04f + 0.03f * azar(88, k))),
                Size(m.width * (0.2f + 0.08f * azar(89, k)), m.height * (0.09f + 0.05f * azar(90, k))))
            with(TrazosArcontes) { sombraArconte(Offset(nube.x + m.width * 0.04f, nube.y - m.height * 0.03f), m.height * 0.13f, TrazosArcontes.SOMBRA.copy(alpha = 0.8f), mira = -1f, desvanece = true) }
        }
        // Los montes de la otra orilla, en dos planos: el de atrás en la calima (o casi negro de noche).
        val lejos = Path().apply {
            moveTo(m.left, horizonte)
            for (k in 0..10) lineTo(m.left + m.width * k / 10f, horizonte - m.height * (0.05f + 0.07f * azar(91, k)) * (if (k in 3..6) 1.4f else 1f))
            lineTo(m.right, horizonte + 1 * u); lineTo(m.left, horizonte + 1 * u); close()
        }
        drawPath(lejos, if (oscuro) Color(0xFF151933) else Color(0xFFBDB4C2))
        val cerca = Path().apply {
            moveTo(m.left, horizonte + 1 * u)
            for (k in 0..8) lineTo(m.left + m.width * k / 8f, horizonte - m.height * (0.01f + 0.03f * azar(92, k)) - (if (k < 3) m.height * 0.025f * (3 - k) else 0f))
            lineTo(m.right, horizonte + 2 * u); close()
        }
        drawPath(cerca, if (oscuro) Color(0xFF10132A) else Color(0xFF9E9A9E))
        // El lago, más claro hacia el horizonte, con sus brillos.
        drawRect(Brush.verticalGradient(if (oscuro) listOf(Color(0xFF162B3C), Color(0xFF0C1A26)) else listOf(mezcla(LAGO, Color(0xFFF6EDD7), 0.45f), LAGO, LAGO_HONDO), horizonte, orilla),
            Offset(m.left, horizonte + 1 * u), Size(m.width, orilla - horizonte))
        for (k in 0 until 14) {
            val y = horizonte + (orilla - horizonte) * (0.1f + 0.85f * azar(93, k))
            val x = m.left + m.width * azar(94, k)
            val l = (3f + 9f * azar(95, k)) * u * (0.6f + (y - horizonte) / (orilla - horizonte))
            drawLine((if (oscuro) Color(0xFF8A93C0) else LINO).copy(alpha = if (oscuro) 0.18f else 0.5f), Offset(x, y), Offset(x + l, y), 0.7f * u, StrokeCap.Round)
        }
        // La barca: el casco oscuro y, de día, la vela cuadrada; de noche, la lámpara de los que pescan y su reflejo.
        val b = Offset(m.left + m.width * 0.6f, horizonte + (orilla - horizonte) * 0.42f)
        val bl = m.width * 0.13f
        drawPath(Path().apply { moveTo(b.x - bl / 2, b.y - bl * 0.12f); lineTo(b.x + bl / 2, b.y - bl * 0.16f); lineTo(b.x + bl * 0.36f, b.y); lineTo(b.x - bl * 0.4f, b.y); close() },
            if (oscuro) Color(0xFF070910) else Color(0xFF4A3426))
        if (!oscuro) {
            drawLine(Color(0xFF4A3426), Offset(b.x, b.y - bl * 0.12f), Offset(b.x, b.y - bl * 0.85f), 0.8f * u)
            drawPath(Path().apply { moveTo(b.x - bl * 0.3f, b.y - bl * 0.8f); lineTo(b.x + bl * 0.3f, b.y - bl * 0.82f); lineTo(b.x + bl * 0.26f, b.y - bl * 0.22f); lineTo(b.x - bl * 0.28f, b.y - bl * 0.2f); close() },
                Color(0xFFF2E9D2))
            drawLine(Color(0xFFC9B48A), Offset(b.x + bl * 0.06f, b.y - bl * 0.8f), Offset(b.x + bl * 0.05f, b.y - bl * 0.22f), 0.6f * u)
        } else {
            val f = Offset(b.x + bl * 0.3f, b.y - bl * 0.3f)
            mancha(f, bl * 0.9f, LLAMA_HALO.copy(alpha = 0.35f))
            drawCircle(LLAMA, 1.1f * u, f)
            for (k in 0 until 4) drawLine(LLAMA.copy(alpha = 0.45f - 0.08f * k), Offset(f.x - (1.5f + k) * u, b.y + (1.5f + 2.2f * k) * u), Offset(f.x + (1.5f + k) * u, b.y + (1.5f + 2.2f * k) * u), 0.7f * u)
        }
        // El umbral de la cueva, visto desde dentro, con matas de hisopo que crecen en la roca.
        val umbral = Path().apply {
            moveTo(m.left, orilla - m.height * 0.02f)
            for (k in 0..8) lineTo(m.left + m.width * k / 8f, orilla - m.height * (0.02f + 0.05f * azar(96, k)))
            lineTo(m.right, m.bottom + 2 * u); lineTo(m.left, m.bottom + 2 * u); close()
        }
        drawPath(umbral, if (oscuro) Color(0xFF14151F) else mezcla(CALIZA_SOMBRA, SOMBRA_MUEBLE, 0.15f))
        drawPath(umbral, (if (oscuro) Color(0xFF8A93C0).copy(alpha = 0.3f) else luzMueble(0.7f)), style = Stroke(1f * u))
        for (k in 0 until 3) {
            val base = Offset(m.left + m.width * (0.3f + 0.25f * k + 0.08f * azar(97, k)), orilla - m.height * 0.03f)
            for (j in 0 until 7) {
                val a = -PI / 2 + (j - 3) * 0.24 + (azar(98, k * 7 + j) - 0.5f) * 0.2
                val l = m.height * (0.05f + 0.06f * azar(99, k * 7 + j))
                drawLine(if (oscuro) Color(0xFF0D0F18) else Color(0xFF6E7048), base, Offset(base.x + l * cos(a).toFloat(), base.y + l * sin(a).toFloat()), 1f * u, StrokeCap.Round)
            }
        }
    }

    /**
     * El velo de lino colgado de un cordel sobre la boca y recogido a la izquierda con otro cordel: de día el sol lo traspasa;
     * de noche apenas lo toca la luna. [m] es la boca.
     */
    private fun DrawScope.velo(m: Rect, oscuro: Boolean) {
        val u = density
        val y0 = m.top + m.height * 0.06f
        val xa = m.left - m.width * 0.06f; val xb = m.left + m.width * 0.46f
        val nudo = Offset(m.left + m.width * 0.1f, m.top + m.height * 0.62f)
        val pie = m.bottom + m.height * 0.03f
        val forma = Path().apply {
            moveTo(xa, y0)
            for (k in 1..8) lineTo(xa + (xb - xa) * k / 8f, y0 + 1.6f * u * sin(k * 1.9f))
            cubicTo(xb - m.width * 0.04f, y0 + (nudo.y - y0) * 0.45f, nudo.x + m.width * 0.16f, nudo.y - m.height * 0.12f, nudo.x + m.width * 0.05f, nudo.y)
            cubicTo(nudo.x + m.width * 0.1f, nudo.y + m.height * 0.12f, nudo.x + m.width * 0.13f, pie - m.height * 0.1f, nudo.x + m.width * 0.18f, pie)
            lineTo(xa, pie); close()
        }
        val tela = if (oscuro) Color(0xFF2B2E42) else mezcla(LINO, LUZ_MUEBLE, 0.25f)
        // La sombra que echa en la roca de la derecha.
        translate(3f * u, 2f * u) { drawPath(forma, sombraMueble(if (oscuro) 0.45f else 0.18f)) }
        drawPath(forma, tela)
        clipPath(forma) {
            // Los pliegues: van de lo alto al nudo y del nudo se abren hacia el pie.
            for (k in 0..9) {
                val x = xa + (xb - xa) * k / 9f
                val p = Path().apply { moveTo(x, y0); quadraticTo(x * 0.5f + nudo.x * 0.5f, (y0 + nudo.y) / 2, nudo.x + (k - 4.5f) * 0.6f * u, nudo.y) }
                drawPath(p, (if (k % 2 == 0) sombraMueble(if (oscuro) 0.4f else 0.16f) else luzMueble(if (oscuro) 0.04f else 0.5f)), style = Stroke((1.4f + 1.6f * azar(101, k)) * u))
                val q = Path().apply { moveTo(nudo.x + (k - 4.5f) * 0.6f * u, nudo.y); quadraticTo(nudo.x + (k - 3f) * 1.5f * u, (nudo.y + pie) / 2, xa + (nudo.x + m.width * 0.2f - xa) * k / 9f, pie) }
                drawPath(q, (if (k % 2 == 1) sombraMueble(if (oscuro) 0.4f else 0.16f) else luzMueble(if (oscuro) 0.04f else 0.45f)), style = Stroke((1.2f + 1.4f * azar(102, k)) * u))
            }
            // El sol que lo traspasa (de día) o el filo de luna (de noche) en el borde que da a la boca.
            if (!oscuro) mancha(Offset(m.left + m.width * 0.22f, m.top + m.height * 0.2f), m.width * 0.4f, ORO.copy(alpha = 0.22f))
            drawPath(forma, (if (oscuro) Color(0xFF8A93C0).copy(alpha = 0.25f) else luzMueble(0.6f)), style = Stroke(1.6f * u))
            // El orillo tejido abajo, con sus hilos sueltos.
            drawLine(sombraMueble(if (oscuro) 0.5f else 0.2f), Offset(xa, pie - 3 * u), Offset(nudo.x + m.width * 0.2f, pie - 3 * u), 1.2f * u)
        }
        // El cordel que la recoge, con su nudo y sus cabos.
        val g = 1.4f * u
        val cuerda = if (oscuro) n(CORDEL, true, 0.62f) else CORDEL
        cordel(Offset(nudo.x - m.width * 0.12f, nudo.y - 1 * u), Offset(nudo.x + m.width * 0.06f, nudo.y + 0.5f * u), cuerda, g, 2 * u)
        drawCircle(cuerda, 2f * u, Offset(nudo.x + m.width * 0.05f, nudo.y + 0.5f * u))
        cordel(Offset(nudo.x + m.width * 0.05f, nudo.y + 1 * u), Offset(nudo.x + m.width * 0.09f, nudo.y + 9 * u), cuerda, g * 0.8f, 1 * u)
        // El cordel de arriba de donde cuelga, de estaca a estaca.
        cordel(Offset(m.left - m.width * 0.08f, y0 - 1 * u), Offset(m.right + m.width * 0.04f, y0 - 2 * u), cuerda, g, 3 * u)
        listOf(Offset(m.left - m.width * 0.08f, y0 - 1 * u), Offset(m.right + m.width * 0.04f, y0 - 2 * u)).forEach { e ->
            drawCircle(sombraMueble(0.4f), 2.8f * u, e + Offset(0.8f * u, 1f * u))
            drawCircle(n(OLIVO_OSC, oscuro, 0.7f), 2.4f * u, e)
            drawCircle(luzMueble(if (oscuro) 0.1f else 0.4f), 1f * u, e - Offset(0.7f * u, 0.7f * u))
        }
    }

    /** El diagrama de las siete esferas grabado a cincel en la roca: cada círculo es un surco con su pared en sombra y su labio en luz. */
    private fun DrawScope.diagrama(c: Offset, r: Float, roca: Color, oscuro: Boolean, cerca: Float) {
        val u = density
        val surco = mezcla(roca, SOMBRA_MUEBLE, 0.65f).copy(alpha = if (oscuro) 0.85f else 0.6f)
        val labio = if (oscuro) LLAMA.copy(alpha = 0.18f * cerca + 0.04f) else luzMueble(0.65f)
        for (i in 0 until 7) {
            val ri = r * (0.3f + 0.7f * i / 6f)
            drawCircle(surco, ri, c - Offset(0.4f * u, 0.4f * u), style = Stroke(1.1f * u))
            drawCircle(labio, ri, c + Offset(0.6f * u, 0.6f * u), style = Stroke(0.6f * u))
            // Un golpe de taladro en cada esfera: su planeta.
            val a = (i * 51.0 + 200.0) * PI / 180.0
            val p = Offset(c.x + ri * cos(a).toFloat(), c.y + ri * sin(a).toFloat())
            drawCircle(surco, 1.3f * u, p)
            drawCircle(labio, 0.6f * u, p + Offset(0.6f * u, 0.6f * u))
        }
        // En el centro, la chispa grabada.
        val e = Path()
        for (i in 0 until 8) {
            val rr = if (i % 2 == 0) r * 0.16f else r * 0.04f
            val a = -PI / 2 + PI / 4 * i
            val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
            if (i == 0) e.moveTo(x, y) else e.lineTo(x, y)
        }
        e.close()
        drawPath(e, surco)
        translate(0.6f * u, 0.6f * u) { drawPath(e, labio, style = Stroke(0.5f * u)) }
    }

    /** La alcuza: el jarrito de aceite de barro con su asa, para llenar la lámpara. */
    private fun DrawScope.alcuza(base: Offset, a: Float, oscuro: Boolean, cerca: Float) {
        val u = density
        val barro = luz(BARRO, oscuro, cerca)
        sombraContacto(Offset(base.x + a * 0.05f, base.y), a * 0.55f, a * 0.08f, 0.3f)
        val cuerpo = Path().apply {
            moveTo(base.x - a * 0.12f, base.y)
            cubicTo(base.x - a * 0.36f, base.y - a * 0.05f, base.x - a * 0.34f, base.y - a * 0.5f, base.x - a * 0.08f, base.y - a * 0.62f)
            lineTo(base.x - a * 0.07f, base.y - a * 0.86f); lineTo(base.x + a * 0.07f, base.y - a * 0.86f); lineTo(base.x + a * 0.08f, base.y - a * 0.62f)
            cubicTo(base.x + a * 0.34f, base.y - a * 0.5f, base.x + a * 0.36f, base.y - a * 0.05f, base.x + a * 0.12f, base.y)
            close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(mezcla(barro, LUZ_MUEBLE, 0.25f), barro, mezcla(barro, SOMBRA_MUEBLE, 0.45f)), base.x - a * 0.3f, base.x + a * 0.3f))
        drawPath(Path().apply { moveTo(base.x + a * 0.07f, base.y - a * 0.78f); cubicTo(base.x + a * 0.34f, base.y - a * 0.82f, base.x + a * 0.34f, base.y - a * 0.5f, base.x + a * 0.2f, base.y - a * 0.44f) },
            mezcla(barro, SOMBRA_MUEBLE, 0.3f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawOval(mezcla(barro, SOMBRA_MUEBLE, 0.6f), Offset(base.x - a * 0.09f, base.y - a * 0.9f), Size(a * 0.18f, a * 0.07f))
        for (k in 0 until 3) drawLine(mezcla(barro, SOMBRA_MUEBLE, 0.35f), Offset(base.x - a * 0.26f, base.y - a * (0.2f + 0.08f * k)), Offset(base.x + a * 0.26f, base.y - a * (0.2f + 0.08f * k)), 0.5f * u)
    }

    // ---------------------------------------------------------------- el mueble: la pared de la cueva

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val alto = size.height
        val ch = d.corona.height
        val roca = n(CALIZA, oscuro, 0.86f)
        // La pared de caliza de la cueva, de arriba abajo.
        piedra(Rect(0f, 0f, w, alto), roca, 17, if (oscuro) 0.7f else 1f)
        // La luz del día entra por la boca, arriba a la izquierda; lo de abajo a la derecha queda en sombra violeta.
        if (!oscuro) drawRect(Brush.linearGradient(listOf(luzMueble(0.28f), Color.Transparent, sombraMueble(0.2f)), Offset(0f, 0f), Offset(w, alto)))
        else drawRect(Brush.verticalGradient(listOf(NOCHE.copy(alpha = 0.2f), Color.Transparent, NOCHE.copy(alpha = 0.3f)), 0f, alto))
        // Los bordes de la cueva: la roca se tuerce hacia dentro a los lados y se hunde en la sombra.
        listOf(false, true).forEach { der ->
            val p = Path()
            val x0 = if (der) w else 0f
            p.moveTo(x0, 0f)
            val pasos = 24
            for (k in 0..pasos) {
                val y = alto * k / pasos
                val ancho = w * (0.016f + 0.022f * azar(if (der) 113 else 111, k))
                p.lineTo(if (der) w - ancho else ancho, y)
            }
            p.lineTo(x0, alto); p.close()
            drawPath(p, n(mezcla(CALIZA_SOMBRA, SOMBRA_MUEBLE, 0.45f), oscuro, 0.88f))
            drawPath(p, (if (oscuro) NOCHE.copy(alpha = 0.5f) else luzMueble(0.35f)), style = Stroke(0.8f * u))
        }
        // El techo: la roca de arriba avanza y echa su sombra.
        val techo = Path().apply {
            moveTo(0f, 0f); lineTo(w, 0f)
            for (k in 16 downTo 0) lineTo(w * k / 16f, ch * (0.05f + 0.09f * azar(121, k)))
            close()
        }
        drawPath(techo, n(mezcla(CALIZA_SOMBRA, SOMBRA_MUEBLE, 0.35f), oscuro, 0.9f))
        drawPath(techo, (if (oscuro) Color(0xFF8A93C0).copy(alpha = 0.12f) else luzMueble(0.45f)), style = Stroke(0.9f * u))
        drawRect(Brush.verticalGradient(listOf(sombraMueble(if (oscuro) 0.4f else 0.28f), Color.Transparent), 0f, ch * 0.3f), Offset.Zero, Size(w, ch * 0.3f))

        // La boca de la cueva, con el lago o el cielo antiguo detrás, su canto de roca y el velo de lino.
        val m = Rect(w * 0.05f, ch * 0.14f, w * 0.4f, ch * 0.95f)
        val forma = boca(m)
        drawPath(forma, sombraMueble(if (oscuro) 0.6f else 0.35f), style = Stroke(6f * u))
        clipPath(forma) { afuera(m, oscuro) }
        drawPath(forma, n(mezcla(CALIZA_SOMBRA, SOMBRA_MUEBLE, 0.3f), oscuro, 0.86f), style = Stroke(3.2f * u))
        drawPath(forma, (if (oscuro) Color(0xFF8A93C0).copy(alpha = 0.35f) else luzMueble(0.85f)), style = Stroke(1.1f * u))
        // De día, el rayo de sol que entra por la boca y cruza la cueva, con su polvo dorado.
        if (!oscuro) {
            val rayo = Path().apply {
                moveTo(m.left + m.width * 0.5f, m.top + m.height * 0.12f); lineTo(m.right - m.width * 0.04f, m.top + m.height * 0.3f)
                lineTo(w * 0.86f, ch * 1.35f); lineTo(w * 0.5f, ch * 1.35f); close()
            }
            drawPath(rayo, Brush.linearGradient(listOf(LLAMA_HALO.copy(alpha = 0.26f), LLAMA_HALO.copy(alpha = 0f)), Offset(m.right, m.top), Offset(w * 0.7f, ch * 1.35f)))
            clipPath(rayo) {
                for (k in 0 until 30) {
                    val p = Offset(m.left + (w * 0.86f - m.left) * azar(131, k), m.top + (ch * 1.35f - m.top) * azar(132, k))
                    drawCircle(ORO.copy(alpha = 0.35f + 0.45f * azar(133, k)), (0.4f + 0.8f * azar(134, k)) * u, p)
                }
            }
        }
        velo(m, oscuro)

        // La repisa tallada con la lámpara y la alcuza; encima, el tizne de tantas noches.
        val rep = Rect(w * 0.46f, ch * 0.7f, w * 0.71f, ch * 0.77f)
        val lw = w * 0.12f
        val pie = Offset(w * 0.535f, rep.top + 1.5f * u)
        val fuego = llama(pie, lw)
        mancha(Offset(fuego.x - 1 * u, fuego.y - ch * 0.12f), ch * 0.16f, TINTA.copy(alpha = if (oscuro) 0.3f else 0.22f))
        if (oscuro) halo(fuego, w * 0.5f)
        drawRect(sombraMueble(if (oscuro) 0.55f else 0.3f), Offset(rep.left + 2 * u, rep.bottom), Size(rep.width, 4 * u))
        drawRect(luz(CALIZA_LUZ, oscuro, 0.95f), Offset(rep.left, rep.top), Size(rep.width, rep.height * 0.42f))
        drawRect(luz(mezcla(CALIZA, SOMBRA_MUEBLE, 0.12f), oscuro, 0.75f), Offset(rep.left, rep.top + rep.height * 0.42f), Size(rep.width, rep.height * 0.58f))
        drawLine(luzMueble(if (oscuro) 0.15f else 0.7f), Offset(rep.left, rep.top + 0.5f * u), Offset(rep.right, rep.top + 0.5f * u), 0.8f * u)
        alcuza(Offset(w * 0.655f, rep.top + 1.5f * u), ch * 0.2f, oscuro, 0.85f)
        lamparaEn(pie, lw, oscuro, encendida = oscuro, cerca = 1f)

        // A la derecha, el diagrama de las esferas grabado en la roca y la grieta con la luz de lo alto.
        diagrama(Offset(w * 0.83f, ch * 0.44f), minOf(ch * 0.3f, w * 0.12f), roca, oscuro, 0.5f)
        with(TrazosArcontes) { grieta(Offset(w * 0.958f, ch * 0.12f), Offset(w * 0.932f, ch * 0.66f), 1.1f * u, if (oscuro) 1f else 0.6f, 11) }

        // El poyo de piedra donde se sientan los de la reunión: la cara de arriba, gastada y en luz, y el frente picado.
        val b = d.base
        val asiento = b.top + b.height * 0.06f
        val frente = b.top + b.height * 0.26f
        val suelo = b.top + b.height * 0.78f
        val fuegoPoyo = llama(Offset(w * 0.78f, frente - 1 * u), w * 0.09f)
        drawRect(n(TIERRA, oscuro, 0.86f), Offset(0f, suelo), Size(w, b.bottom - suelo))
        pinceladas(Rect(0f, suelo, w, b.bottom), n(mezcla(TIERRA, LUZ_MUEBLE, 0.4f), oscuro, 0.8f), 141, 18, u, 0.12f)
        for (k in 0 until 16) {
            val p = Offset(w * azar(142, k), suelo + (b.bottom - suelo) * (0.2f + 0.7f * azar(143, k)))
            val r = (0.8f + 2f * azar(144, k)) * u
            drawOval(n(if (k % 2 == 0) CALIZA_SOMBRA else HUECO, oscuro, 0.84f), Offset(p.x - r, p.y - r * 0.6f), Size(2 * r, 1.2f * r))
        }
        drawRect(Brush.verticalGradient(listOf(sombraMueble(if (oscuro) 0.7f else 0.4f), Color.Transparent), suelo, suelo + 6 * u), Offset(0f, suelo), Size(w, 6 * u))
        piedra(Rect(0f, frente, w, suelo), n(mezcla(CALIZA, SOMBRA_MUEBLE, 0.12f), oscuro, 0.86f), 151, if (oscuro) 0.6f else 0.9f)
        drawRect(n(CALIZA_LUZ, oscuro, 0.84f), Offset(0f, asiento), Size(w, frente - asiento))
        pinceladas(Rect(0f, asiento, w, frente), n(CALIZA, oscuro, 0.84f), 152, 14, u, 0.2f)
        drawLine(luzMueble(if (oscuro) 0.12f else 0.8f), Offset(0f, frente - 0.6f * u), Offset(w, frente - 0.6f * u), 1.2f * u)
        drawRect(Brush.verticalGradient(listOf(sombraMueble(if (oscuro) 0.4f else 0.22f), Color.Transparent), frente, frente + 5 * u), Offset(0f, frente), Size(w, 5 * u))
        drawRect(Brush.verticalGradient(listOf(sombraMueble(if (oscuro) 0.5f else 0.3f), Color.Transparent), b.top - 2 * u, asiento + 2 * u), Offset(0f, b.top - 2 * u), Size(w, asiento - b.top + 4 * u))
        if (oscuro) halo(fuegoPoyo, w * 0.36f, 0.9f)
        // El manto de lana doblado, con sus dos franjas, a la izquierda del poyo.
        val manto = Rect(w * 0.07f, asiento - b.height * 0.17f, w * 0.27f, frente + b.height * 0.04f)
        sombraContacto(Offset(manto.center.x + 3 * u, frente), manto.width * 1.05f, 5 * u, 0.3f)
        val lana = n(LANA, oscuro, 0.82f)
        drawRoundRect(lana, manto.topLeft, manto.size, CornerRadius(manto.height * 0.4f))
        drawRoundRect(n(mezcla(LANA, LUZ_MUEBLE, 0.25f), oscuro, 0.84f), Offset(manto.left + 2 * u, manto.top), Size(manto.width * 0.9f, manto.height * 0.45f), CornerRadius(manto.height * 0.3f))
        listOf(0.3f, 0.7f).forEach { f ->
            drawLine(n(Color(0xFF3C2C24), oscuro, 0.7f), Offset(manto.left + manto.width * f, manto.top + 1 * u), Offset(manto.left + manto.width * (f + 0.02f), manto.bottom - 1 * u), 2.2f * u)
        }
        drawLine(sombraMueble(0.35f), Offset(manto.left + 3 * u, manto.center.y + 1 * u), Offset(manto.right - 3 * u, manto.center.y + 1.5f * u), 1 * u)
        // El cuenco de barro y la otra lámpara (encendida de noche), a la derecha del poyo.
        val cu = Offset(w * 0.66f, frente - 1 * u)
        val cr = w * 0.045f
        sombraContacto(Offset(cu.x + 2 * u, cu.y), cr * 2.2f, 4 * u, 0.3f)
        drawPath(Path().apply { moveTo(cu.x - cr, cu.y - cr * 0.7f); quadraticTo(cu.x - cr * 0.95f, cu.y, cu.x, cu.y); quadraticTo(cu.x + cr * 0.95f, cu.y, cu.x + cr, cu.y - cr * 0.7f); close() },
            luz(BARRO_CLARO, oscuro, 0.7f))
        drawOval(luz(mezcla(BARRO, SOMBRA_MUEBLE, 0.3f), oscuro, 0.7f), Offset(cu.x - cr, cu.y - cr * 0.86f), Size(cr * 2, cr * 0.32f))
        lamparaEn(Offset(w * 0.78f, frente - 1 * u), w * 0.09f, oscuro, encendida = oscuro, cerca = 1f)
        // Las sandalias de alguien de la reunión, en el suelo.
        listOf(0f, 1f).forEach { k ->
            val c0 = Offset(w * (0.4f + 0.05f * k), suelo + (b.bottom - suelo) * (0.5f + 0.06f * k))
            rotate(-8f + 14f * k, c0) {
                val suela = Rect(c0.x - w * 0.016f, c0.y - (b.bottom - suelo) * 0.2f, c0.x + w * 0.016f, c0.y + (b.bottom - suelo) * 0.2f)
                drawOval(sombraMueble(0.35f), suela.translate(1.2f * u, 1.5f * u).topLeft, suela.size)
                drawOval(n(CUERO, oscuro, 0.8f), suela.topLeft, suela.size)
                drawOval(n(mezcla(CUERO, LUZ_MUEBLE, 0.2f), oscuro, 0.8f), Offset(suela.left + 1 * u, suela.top + 1 * u), Size(suela.width - 2 * u, suela.height - 2 * u))
                drawLine(n(TrazosArcontes.CORREA, oscuro, 0.7f), Offset(suela.left, suela.top + suela.height * 0.4f), Offset(suela.right, suela.top + suela.height * 0.35f), 1.6f * u)
                drawLine(n(TrazosArcontes.CORREA, oscuro, 0.7f), Offset(suela.center.x, suela.top + 1 * u), Offset(suela.center.x, suela.top + suela.height * 0.38f), 1f * u)
            }
        }
        // La tira de papiro con «y N más», colgada de dos estacas en el frente del poyo.
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val e1 = Offset(r.left + r.width * 0.12f, frente + 2 * u); val e2 = Offset(r.right - r.width * 0.12f, frente + 2 * u)
            val forma0 = hoja(r, 23, 30, 0.03f)
            translate(1.5f * u, 2f * u) { drawPath(forma0, sombraMueble(0.35f)) }
            drawPath(forma0, luz(PAPIRO, oscuro, 0.62f))
            clipPath(forma0) { fibras(r, FIBRA.copy(alpha = 0.5f), 29, 2.6f * u) }
            drawPath(forma0, FIBRA.copy(alpha = 0.45f), style = Stroke(0.8f * u))
            listOf(e1, e2).forEach { e ->
                cordel(e, Offset(e.x, r.top + 3 * u), n(CORDEL, oscuro, 0.5f), 1.1f * u)
                drawCircle(n(OLIVO_OSC, oscuro, 0.7f), 1.8f * u, e)
            }
        }
        // El grano de papel, al final.
        granoMueble(Rect(0f, 0f, w, alto), oscuro)
    }

    // ---------------------------------------------------------------- el compartimento: un nicho con su arqueta

    /** Un punto de la arqueta en el espacio (x a lo ancho, y hacia arriba, z hacia el fondo), visto un poco desde arriba. */
    private class Vista(val ox: Float, val oy: Float, val kz: Float) {
        fun p(x: Float, y: Float, z: Float) = Offset(ox + x + 0.35f * kz * z, oy - y - kz * z)
    }

    /** El rectángulo que encierra unos puntos. */
    private fun limites(vararg ps: Offset) = Rect(ps.minOf { it.x }, ps.minOf { it.y }, ps.maxOf { it.x }, ps.maxOf { it.y })

    private fun cuad(a: Offset, b: Offset, c: Offset, d: Offset) = Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(c.x, c.y); lineTo(d.x, d.y); close() }

    /**
     * La arqueta de olivo en el nicho. [v] la pone en el espacio, de [ancho] × [alto] × [hondo]; [giro] es lo abierta que está
     * la tapa (en radianes); [k], cuántas hojas guarda (hasta 10); [lamparaX], de qué lado le llega la llama de noche.
     */
    private fun DrawScope.arqueta(v: Vista, ancho: Float, alto: Float, hondo: Float, giro: Float, k: Int, semilla: Int, oscuro: Boolean, lamparaX: Float) {
        val u = density
        val canto = alto * 0.12f
        // La cercanía a la llama de cada lado: lo que mira a la lámpara se calienta.
        val izqCerca = if (lamparaX < v.ox + ancho / 2) 1f else 0.45f
        val derCerca = if (lamparaX < v.ox + ancho / 2) 0.45f else 1f
        fun madera(c: Color, cerca: Float) = luz(c, oscuro, cerca)
        // La sombra que echa en el piso y en el fondo del nicho, a la derecha.
        drawPath(cuad(v.p(ancho * 0.1f, 0f, 0f), v.p(ancho + alto * 0.5f, 0f, 0f), v.p(ancho + alto * 0.45f, 0f, hondo * 1.4f), v.p(ancho * 0.1f, 0f, hondo * 1.4f)), sombraMueble(if (oscuro) 0.45f else 0.3f))
        drawPath(cuad(v.p(ancho, 0f, hondo), v.p(ancho + alto * 0.6f, 0f, hondo * 1.3f), v.p(ancho + alto * 0.5f, alto * 1.1f, hondo * 1.3f), v.p(ancho, alto, hondo)), sombraMueble(if (oscuro) 0.3f else 0.16f))
        // La boca de la arqueta, por dentro: oscura, con las paredes en canto y las hojas atadas en un legajo.
        val boca = cuad(v.p(0f, alto, 0f), v.p(ancho, alto, 0f), v.p(ancho, alto, hondo), v.p(0f, alto, hondo))
        val dentro = madera(mezcla(OLIVO_OSC, SOMBRA_MUEBLE, 0.55f), 0.5f)
        drawPath(boca, dentro)
        if (k > 0) clipPath(boca) {
            val lleno = (k / 10f).coerceIn(0.1f, 1f)
            val ys = alto * (0.55f + 0.42f * lleno)
            val m = ancho * 0.07f; val mz = hondo * 0.1f
            val papel = luz(PAPIRO, oscuro, 0.8f)
            // Los cantos de las hojas de abajo, más oscuros, y la de arriba con sus fibras.
            for (j in 3 downTo 1) drawPath(cuad(v.p(m - j * 0.6f * u, ys - j * 1.2f * u, mz), v.p(ancho - m + j * 0.5f * u, ys - j * 1.2f * u, mz), v.p(ancho - m, ys - j * 1.2f * u, hondo - mz), v.p(m, ys - j * 1.2f * u, hondo - mz)),
                mezcla(papel, FIBRA, 0.15f + 0.1f * j))
            val tope = cuad(v.p(m, ys, mz), v.p(ancho - m, ys, mz), v.p(ancho - m, ys, hondo - mz), v.p(m, ys, hondo - mz))
            drawPath(tope, papel)
            clipPath(tope) {
                for (j in 0 until 8) {
                    val z = mz + (hondo - 2 * mz) * (j + 0.5f) / 8f
                    drawLine(FIBRA.copy(alpha = 0.25f), v.p(m, ys, z), v.p(ancho - m, ys, z), 0.5f * u)
                }
            }
            // El cordel en cruz que ata el legajo, con su nudo.
            val cuerda = luz(CORDEL, oscuro, 0.8f)
            cordel(v.p(ancho / 2, ys, mz), v.p(ancho / 2, ys, hondo - mz), cuerda, 1.2f * u)
            cordel(v.p(m, ys, hondo / 2), v.p(ancho - m, ys, hondo / 2), cuerda, 1.2f * u)
            drawCircle(cuerda, 2f * u, v.p(ancho / 2, ys, hondo / 2))
            // La sombra de la pared de delante sobre las hojas.
            drawPath(cuad(v.p(0f, alto, 0f), v.p(ancho, alto, 0f), v.p(ancho, ys, hondo * 0.35f), v.p(0f, ys, hondo * 0.35f)), sombraMueble(if (oscuro) 0.4f else 0.25f))
        }
        // El canto de las paredes alrededor de la boca.
        drawPath(boca, madera(OLIVO_LUZ, 0.8f).copy(alpha = 0.8f), style = Stroke(1.4f * u))
        // Los cantos de las hojas que asoman bajo la tapa entreabierta (se tapan donde está la tapa).
        if (k > 0) for (j in 0 until minOf(k, 7)) {
            val y = alto + (0.6f + j * 0.9f) * u
            val z = hondo * (0.04f + 0.012f * j)
            val x0 = ancho * (0.06f + 0.04f * azar(semilla, j + 60)); val x1 = ancho * (0.94f - 0.04f * azar(semilla, j + 61))
            drawLine(luz(mezcla(PAPIRO, FIBRA, 0.12f * j), oscuro, 0.85f), v.p(x0, y, z), v.p(x1, y + (azar(semilla, j + 62) - 0.5f) * u, z), 1.1f * u, StrokeCap.Round)
        }

        // El frente de olivo: la luz de un lado, la veta que se arremolina, el friso de muescas, las esquinas y la cerradura de bronce.
        val f0 = v.p(0f, alto, 0f); val f1 = v.p(ancho, 0f, 0f)
        val frente = Rect(f0.x, f0.y, f1.x, f1.y)
        drawRect(Brush.horizontalGradient(listOf(madera(OLIVO_LUZ, izqCerca), madera(OLIVO, (izqCerca + derCerca) / 2), madera(mezcla(OLIVO, OLIVO_OSC, 0.5f), derCerca)), frente.left, frente.right),
            frente.topLeft, frente.size)
        clipRect(frente.left, frente.top, frente.right, frente.bottom) {
            vetas(frente, madera(OLIVO_VETA, 0.7f).copy(alpha = 0.32f), semilla, densidad = 0.55f, grosor = 0.9f, ondulacion = 2.2f)
            // Los remolinos de la veta del olivo.
            for (j in 0 until 2) {
                val c = Offset(frente.left + frente.width * (0.2f + 0.6f * azar(semilla, j + 70)), frente.top + frente.height * (0.3f + 0.4f * azar(semilla, j + 71)))
                for (a in 1..3) drawOval(madera(OLIVO_VETA, 0.7f).copy(alpha = 0.28f), Offset(c.x - a * 2.6f * u, c.y - a * 1.2f * u), Size(a * 5.2f * u, a * 2.4f * u), style = Stroke(0.6f * u))
            }
            // El friso de muescas a punta de cuchillo: triángulos alternos en luz y en sombra.
            val fy = frente.top + frente.height * 0.1f; val fa = frente.height * 0.13f
            var x = frente.left + 3 * u; var j = 0
            while (x + fa < frente.right - 2 * u) {
                val tri = Path().apply { moveTo(x, fy); lineTo(x + fa * 0.5f, fy + fa); lineTo(x + fa, fy); close() }
                drawPath(tri, if (j % 2 == 0) sombraMueble(if (oscuro) 0.45f else 0.32f) else luzMueble(if (oscuro) 0.08f else 0.32f))
                x += fa * 0.55f; j++
            }
            drawLine(sombraMueble(0.3f), Offset(frente.left, fy + fa + 1.5f * u), Offset(frente.right, fy + fa + 1.5f * u), 0.7f * u)
            drawLine(sombraMueble(0.3f), Offset(frente.left, frente.bottom - frente.height * 0.12f), Offset(frente.right, frente.bottom - frente.height * 0.12f), 0.7f * u)
        }
        drawLine(luzMueble(if (oscuro) 0.12f else 0.5f), frente.topLeft, Offset(frente.right, frente.top), 0.8f * u)
        // El costado de la derecha, en sombra.
        val costado = cuad(v.p(ancho, alto, 0f), v.p(ancho, alto, hondo), v.p(ancho, 0f, hondo), v.p(ancho, 0f, 0f))
        drawPath(costado, madera(mezcla(OLIVO_OSC, SOMBRA_MUEBLE, 0.35f), derCerca * 0.8f))
        clipPath(costado) { vetas(Rect(frente.right, frente.top - hondo * v.kz, frente.right + hondo * v.kz, frente.bottom), madera(OLIVO_VETA, 0.5f).copy(alpha = 0.25f), semilla + 3, densidad = 0.5f) }
        // Las esquinas de bronce y la cerradura con su ojo, y el pasador de hueso de donde cuelga la etiqueta.
        val br = madera(BRONCE, 0.85f); val brl = madera(BRONCE_LUZ, 0.85f)
        val es = frente.height * 0.22f
        listOf(frente.topLeft, Offset(frente.right, frente.top), Offset(frente.left, frente.bottom), frente.bottomRight).forEachIndexed { i, e ->
            val sx = if (i % 2 == 0) 1f else -1f; val sy = if (i < 2) 1f else -1f
            val esq = Path().apply { moveTo(e.x, e.y); lineTo(e.x + sx * es, e.y); lineTo(e.x + sx * es * 0.4f, e.y + sy * es * 0.4f); lineTo(e.x, e.y + sy * es); close() }
            drawPath(esq, br)
            drawPath(esq, brl.copy(alpha = 0.6f), style = Stroke(0.6f * u))
            drawCircle(mezcla(br, SOMBRA_MUEBLE, 0.5f), 0.8f * u, Offset(e.x + sx * es * 0.25f, e.y + sy * es * 0.25f))
        }
        val cerr = Rect(frente.center.x - frente.width * 0.07f, frente.top + frente.height * 0.3f, frente.center.x + frente.width * 0.07f, frente.top + frente.height * 0.62f)
        drawRect(sombraMueble(0.35f), cerr.translate(0.8f * u, 1f * u).topLeft, cerr.size)
        drawRect(Brush.verticalGradient(listOf(brl, br), cerr.top, cerr.bottom), cerr.topLeft, cerr.size)
        listOf(cerr.topLeft, Offset(cerr.right, cerr.top), Offset(cerr.left, cerr.bottom), cerr.bottomRight).forEach { e ->
            drawCircle(mezcla(br, SOMBRA_MUEBLE, 0.5f), 0.7f * u, Offset(e.x + (if (e.x < cerr.center.x) 1.6f else -1.6f) * u, e.y + (if (e.y < cerr.center.y) 1.6f else -1.6f) * u))
        }
        val ojo = Offset(cerr.center.x, cerr.center.y - cerr.height * 0.08f)
        drawCircle(TINTA.copy(alpha = 0.85f), cerr.width * 0.13f, ojo)
        drawRect(TINTA.copy(alpha = 0.85f), Offset(ojo.x - cerr.width * 0.05f, ojo.y), Size(cerr.width * 0.1f, cerr.height * 0.28f))
        val pas = Offset(frente.center.x, frente.top + frente.height * 0.78f)
        drawCircle(sombraMueble(0.4f), 2.6f * u, pas + Offset(0.6f * u, 0.9f * u))
        drawCircle(madera(HUESO, 0.9f), 2.4f * u, pas)
        drawCircle(luzMueble(if (oscuro) 0.2f else 0.7f), 0.9f * u, pas - Offset(0.8f * u, 0.8f * u))

        // La tapa, girada [giro] sobre las bisagras de atrás: de canto por delante y, cerrada, la cara de arriba; abierta, la de
        // dentro, con el ouroboros grabado, la contraseña de la comunidad.
        val sg = sin(giro); val cg = cos(giro)
        fun tapa(x: Float, s: Float, t: Float) = v.p(x, alto + s * sg + t * cg, hondo - s * cg + t * sg)
        val sobra = ancho * 0.03f
        val a0 = -sobra; val a1 = ancho + sobra
        if (giro < 0.22f) {
            val arriba = cuad(tapa(a0, 0f, canto), tapa(a1, 0f, canto), tapa(a1, hondo + sobra, canto), tapa(a0, hondo + sobra, canto))
            drawPath(arriba, madera(mezcla(OLIVO, OLIVO_LUZ, 0.4f), (izqCerca + derCerca) / 2))
            clipPath(arriba) { vetas(limites(tapa(a0, 0f, canto), tapa(a1, 0f, canto), tapa(a1, hondo + sobra, canto), tapa(a0, hondo + sobra, canto)), madera(OLIVO_VETA, 0.6f).copy(alpha = 0.25f), semilla + 9, densidad = 0.6f, ondulacion = 1.6f) }
            drawLine(luzMueble(if (oscuro) 0.1f else 0.6f), tapa(a0, hondo + sobra, canto), tapa(a1, hondo + sobra, canto), 0.8f * u)
        } else {
            val dentroTapa = cuad(tapa(0f, 0f, 0f), tapa(ancho, 0f, 0f), tapa(ancho, hondo, 0f), tapa(0f, hondo, 0f))
            drawPath(dentroTapa, madera(mezcla(OLIVO_LUZ, OLIVO, 0.3f), 0.95f))
            clipPath(dentroTapa) {
                // El ouroboros grabado por dentro: lo pongo en el plano de la tapa con una transformación afín.
                val o = tapa(0f, 0f, 0f); val ex = tapa(ancho, 0f, 0f) - o; val ez = tapa(0f, hondo, 0f) - o
                withTransform({ transform(Matrix(floatArrayOf(ex.x, ex.y, 0f, 0f, ez.x, ez.y, 0f, 0f, 0f, 0f, 1f, 0f, o.x, o.y, 0f, 1f))) }) {
                    withTransform({ scale(1f, ancho / hondo, Offset(0.5f, 0.5f)) }) {
                        with(TrazosArcontes) {
                            ouroboros(Offset(0.5f + 0.012f, 0.5f + 0.012f), 0.18f, 0.06f, luzMueble(if (oscuro) 0.15f else 0.5f))
                            ouroboros(Offset(0.5f, 0.5f), 0.18f, 0.06f, madera(OLIVO_VETA, 0.9f).copy(alpha = 0.75f))
                        }
                    }
                }
                vetas(Rect(o.x - ancho, minOf(o.y, (o + ez).y) - 4 * u, o.x + ancho * 2, maxOf(o.y, (o + ez).y) + 4 * u), madera(OLIVO_VETA, 0.8f).copy(alpha = 0.16f), semilla + 11, densidad = 0.4f, ondulacion = 1.4f)
            }
            drawPath(dentroTapa, sombraMueble(0.4f), style = Stroke(0.7f * u))
        }
        // El canto de la tapa, por delante.
        val cantoTapa = cuad(tapa(a0, hondo + sobra, 0f), tapa(a1, hondo + sobra, 0f), tapa(a1, hondo + sobra, canto), tapa(a0, hondo + sobra, canto))
        drawPath(cantoTapa, madera(mezcla(OLIVO, OLIVO_OSC, 0.3f), (izqCerca + derCerca) / 2))
        drawPath(cantoTapa, sombraMueble(0.35f), style = Stroke(0.6f * u))
        if (giro < 0.22f) drawLine(sombraMueble(if (oscuro) 0.5f else 0.35f), tapa(a0, hondo + sobra, 0f) + Offset(0f, 1f * u), tapa(a1, hondo + sobra, 0f) + Offset(0f, 1f * u), 1.4f * u)
    }

    /** Un legajo de hojas atadas, acostado en el piso del nicho: el papiro, los cantos y el cordel con su lazo. */
    private fun DrawScope.legajo(c: Offset, ancho: Float, alto: Float, semilla: Int, oscuro: Boolean, cerca: Float) {
        val u = density
        sombraContacto(Offset(c.x + 1 * u, c.y), ancho * 1.05f, alto * 0.5f, 0.3f)
        for (j in 2 downTo 0) {
            val r = Rect(c.x - ancho / 2 + j * 0.8f * u, c.y - alto - j * 1.5f * u, c.x + ancho / 2 + j * 0.6f * u, c.y - j * 1.5f * u)
            drawPath(hoja(r, semilla + j, 24, 0.05f), luz(mezcla(PAPIRO, FIBRA, 0.12f * j), oscuro, cerca))
        }
        val cuerda = luz(CORDEL, oscuro, cerca)
        cordel(Offset(c.x, c.y - alto - 3.5f * u), Offset(c.x, c.y + 0.5f * u), cuerda, 1.1f * u)
        drawCircle(cuerda, 1.6f * u, Offset(c.x, c.y - alto * 0.5f))
        cordel(Offset(c.x, c.y - alto * 0.5f), Offset(c.x + ancho * 0.2f, c.y + 1.5f * u), cuerda, 0.9f * u, 1 * u)
    }

    /** El cesto de juncos de lo que no tiene tema: las hojas sueltas, sin atar, asomando torcidas. */
    private fun DrawScope.cesto(base: Offset, ancho: Float, alto: Float, k: Int, ab: Float, semilla: Int, oscuro: Boolean, lamparaX: Float) {
        val u = density
        val cerca = if (lamparaX < base.x) 1f else 0.6f
        val boca = Rect(base.x - ancho / 2, base.y - alto - ancho * 0.08f, base.x + ancho / 2, base.y - alto + ancho * 0.08f)
        sombraContacto(Offset(base.x + 3 * u, base.y), ancho * 1.1f, 5 * u, if (oscuro) 0.45f else 0.32f)
        drawOval(luz(mezcla(JUNCO_OSC, SOMBRA_MUEBLE, 0.5f), oscuro, 0.5f), boca.topLeft, boca.size)
        // Las hojas sueltas, cada una torcida a su modo.
        for (j in 0 until minOf(k, 6)) {
            val x = boca.left + boca.width * (0.2f + 0.6f * (j + 0.5f) / minOf(k, 6).coerceAtLeast(1))
            val ha = alto * (0.55f + 0.3f * azar(semilla, j + 80)) + alto * 0.25f * ab
            val r = Rect(x - ancho * 0.13f, boca.center.y - ha, x + ancho * 0.13f, boca.center.y + alto * 0.2f)
            rotate(-22f + 44f * azar(semilla, j + 81), Offset(x, boca.center.y)) {
                val h = hoja(r, semilla + j, 24, 0.03f)
                drawPath(h, luz(mezcla(PAPIRO, FIBRA, 0.08f * (j % 3)), oscuro, cerca))
                clipPath(h) { fibras(r, FIBRA.copy(alpha = 0.4f), semilla + j, 2.6f * u) }
                drawPath(h, FIBRA.copy(alpha = 0.4f), style = Stroke(0.6f * u))
                for (l in 0 until 3) drawLine(TINTA.copy(alpha = 0.35f), Offset(r.left + r.width * 0.2f, r.top + r.height * (0.15f + 0.09f * l)), Offset(r.left + r.width * (0.5f + 0.3f * azar(semilla, j * 3 + l)), r.top + r.height * (0.15f + 0.09f * l)), 0.6f * u)
            }
        }
        // El cuerpo del cesto: se angosta hacia el pie, con las hileras del tejido en luz y en sombra.
        val cuerpo = Path().apply {
            moveTo(boca.left, boca.center.y); lineTo(boca.right, boca.center.y)
            lineTo(base.x + ancho * 0.4f, base.y); quadraticTo(base.x, base.y + ancho * 0.06f, base.x - ancho * 0.4f, base.y); close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(luz(mezcla(JUNCO, LUZ_MUEBLE, 0.2f), oscuro, cerca), luz(JUNCO, oscuro, 0.7f), luz(JUNCO_OSC, oscuro, 1.4f - cerca)), boca.left, boca.right))
        clipPath(cuerpo) {
            var y = boca.center.y + 2 * u; var fila = 0
            while (y < base.y + 4 * u) {
                var x = boca.left + (if (fila % 2 == 0) 0f else 2.5f * u)
                while (x < boca.right) {
                    drawLine(sombraMueble(if (oscuro) 0.4f else 0.28f), Offset(x, y), Offset(x + 2.2f * u, y + 2.2f * u), 0.9f * u, StrokeCap.Round)
                    drawLine(luzMueble(if (oscuro) 0.06f else 0.3f), Offset(x + 2.5f * u, y + 2.2f * u), Offset(x + 4.7f * u, y), 0.7f * u, StrokeCap.Round)
                    x += 5f * u
                }
                y += 3.2f * u; fila++
            }
        }
        // El borde trenzado.
        val labio = Path().apply { addArcRect(boca, 0f, 180f) }
        drawPath(labio, luz(JUNCO_OSC, oscuro, 0.8f), style = Stroke(3.2f * u))
        drawPath(labio, luz(mezcla(JUNCO, LUZ_MUEBLE, 0.3f), oscuro, cerca), style = Stroke(1.4f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.5f * u, 1.5f * u))))
    }

    /** Un arco abierto de [r] de [desde] a [barrido] grados. */
    private fun Path.addArcRect(r: Rect, desde: Float, barrido: Float) { arcTo(r, desde, barrido, true) }

    /** La etiqueta de papiro con el tema, colgada con un cordel en V del pasador [pas], y su sombra en la roca. */
    private fun DrawScope.etiqueta(r: Rect, pas: Offset, semilla: Int, oscuro: Boolean, cerca: Float) {
        val u = density
        val forma = hoja(r, semilla, 36, 0.035f)
        translate(1.4f * u, 2.2f * u) { drawPath(forma, sombraMueble(if (oscuro) 0.5f else 0.28f)) }
        val papel = if (oscuro) mezcla(mezcla(PAPIRO, NOCHE, 0.36f), LLAMA, 0.12f + 0.06f * cerca) else PAPIRO
        drawPath(forma, papel)
        clipPath(forma) {
            fibras(r, FIBRA.copy(alpha = 0.45f), semilla, 2.8f * u)
            drawRect(Brush.verticalGradient(listOf(luzMueble(if (oscuro) 0.05f else 0.35f), Color.Transparent, FIBRA.copy(alpha = 0.18f)), r.top, r.bottom), r.topLeft, r.size)
        }
        drawPath(forma, mezcla(papel, FIBRA, 0.55f), style = Stroke(1f * u))
        // Los dos agujeros con su cordel, que sube en V hasta el pasador.
        val cuerda = luz(CORDEL, oscuro, 0.9f)
        listOf(Offset(r.left + r.width * 0.09f, r.top + r.height * 0.18f), Offset(r.right - r.width * 0.09f, r.top + r.height * 0.18f)).forEach { a ->
            drawCircle(TINTA.copy(alpha = 0.55f), 1.4f * u, a)
            cordel(Path().apply { moveTo(pas.x, pas.y); quadraticTo((pas.x + a.x) / 2, (pas.y + a.y) / 2 + 2 * u, a.x, a.y) }, cuerda, 1f * u)
            drawCircle(cuerda, 1.3f * u, a)
        }
        drawCircle(cuerda, 1.8f * u, pas)
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        val piso = h * PISO
        val nr = Rect(w * 0.035f, h * 0.02f, w * 0.965f, piso)
        val forma = nicho(nr, h * FLECHA)
        val roca = n(CALIZA, oscuro, 0.86f)
        if (c.vacio) {
            // Un nicho todavía por tallar: el arco trazado con el cincel en la piedra, el picado empezado y la lasca en el suelo.
            drawPath(forma, mezcla(roca, SOMBRA_MUEBLE, 0.5f).copy(alpha = 0.55f), style = Stroke(1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f * u, 2.5f * u))))
            translate(0.8f * u, 0.8f * u) { drawPath(forma, luzMueble(if (oscuro) 0.06f else 0.4f), style = Stroke(0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f * u, 2.5f * u)))) }
            clipPath(forma) {
                for (k in 0 until 26) {
                    val o = Offset(nr.left + nr.width * (0.2f + 0.6f * azar(c.semilla, k)), nr.top + nr.height * (0.12f + 0.4f * azar(c.semilla, k + 30)))
                    drawLine(mezcla(roca, SOMBRA_MUEBLE, 0.55f).copy(alpha = 0.45f), o, o + Offset(1.6f * u, 3.2f * u), 0.8f * u, StrokeCap.Round)
                    drawLine(luzMueble(if (oscuro) 0.05f else 0.35f), o + Offset(0.7f * u, 0f), o + Offset(2.3f * u, 3.2f * u), 0.5f * u, StrokeCap.Round)
                }
                drawOval(sombraMueble(if (oscuro) 0.3f else 0.12f), Offset(nr.left + nr.width * 0.25f, nr.top + nr.height * 0.15f), Size(nr.width * 0.5f, nr.height * 0.36f))
            }
            for (k in 0 until 5) {
                val p = Offset(nr.left + nr.width * (0.35f + 0.3f * azar(c.semilla, k + 60)), piso - (1f + 2f * azar(c.semilla, k + 61)) * u)
                drawPath(cuad(p, p + Offset(3f * u, -1.5f * u), p + Offset(4.5f * u, 0.5f * u), p + Offset(1f * u, 1.2f * u)), n(CALIZA_LUZ, oscuro, 0.84f))
            }
            return
        }

        // De qué lado está la lámpara del nicho, y dónde arde.
        val izquierda = Math.floorMod(c.semilla, 2) == 0
        val lw = w * 0.15f
        val pie = Offset(if (izquierda) nr.left + w * 0.11f else nr.right - w * 0.11f - lw * 0.3f, piso - h * 0.035f)
        val fuego = llama(pie, lw)
        // La luz de noche se derrama por la roca de alrededor de la boca.
        if (oscuro) mancha(Offset(fuego.x, piso), w * 0.62f, LLAMA_HALO.copy(alpha = 0.13f))
        // El marco labrado alrededor de la boca: la roca alisada con el cincel, con su filo en luz.
        drawPath(forma, mezcla(roca, LUZ_MUEBLE, if (oscuro) 0.04f else 0.25f), style = Stroke(6f * u))
        drawPath(forma, sombraMueble(if (oscuro) 0.5f else 0.25f), style = Stroke(2.4f * u))

        // El hueco: el fondo en sombra, la pared de la izquierda en sombra, la de la derecha y el piso a la luz, y la sombra del
        // arco y de la jamba sobre el fondo.
        val lc = if (izquierda) 1f else 0.5f; val rc = if (izquierda) 0.5f else 1f
        clipPath(forma) {
            val fondo = luz(HUECO, oscuro, 0.55f)
            drawRect(fondo, nr.topLeft, nr.size)
            piedra(nr, fondo, c.semilla + 3, 0.6f)
            val lado = w * 0.07f; val fd = h * 0.085f
            drawPath(cuad(Offset(nr.left, nr.top), Offset(nr.left + lado, nr.top + h * 0.12f), Offset(nr.left + lado, piso - fd), Offset(nr.left, piso)), luz(mezcla(HUECO, SOMBRA_MUEBLE, 0.45f), oscuro, lc))
            drawPath(cuad(Offset(nr.right, nr.top), Offset(nr.right - lado, nr.top + h * 0.12f), Offset(nr.right - lado, piso - fd), Offset(nr.right, piso)), luz(mezcla(HUECO, LUZ_MUEBLE, 0.2f), oscuro, rc))
            drawPath(cuad(Offset(nr.left, piso), Offset(nr.left + lado, piso - fd), Offset(nr.right - lado, piso - fd), Offset(nr.right, piso)), luz(mezcla(CALIZA, HUECO, 0.35f), oscuro, 0.8f))
            pinceladas(Rect(nr.left, piso - fd, nr.right, piso), luz(CALIZA_LUZ, oscuro, 0.8f), c.semilla + 5, 6, u, 0.2f)
            drawLine(sombraMueble(0.25f), Offset(nr.left + lado, piso - fd), Offset(nr.right - lado, piso - fd), 0.8f * u)
            drawRect(Brush.verticalGradient(listOf(sombraMueble(if (oscuro) 0.6f else 0.5f), Color.Transparent), nr.top, nr.top + h * 0.3f), nr.topLeft, Size(nr.width, h * 0.3f))
            if (!oscuro) drawPath(cuad(Offset(nr.left, nr.top), Offset(nr.left + w * 0.35f, nr.top), Offset(nr.left + w * 0.12f, piso - h * 0.1f), Offset(nr.left, piso - h * 0.1f)), sombraMueble(0.16f))
            if (oscuro) mancha(fuego, w * 0.7f, LLAMA_HALO.copy(alpha = 0.3f))

            // Las más llenas: un legajo atado más, en el piso, detrás de la lámpara.
            if (!c.sinTema && c.n >= 7) legajo(Offset(pie.x + lw * 0.05f, piso - h * 0.075f), w * 0.17f, h * 0.05f, c.semilla + 40, oscuro, 0.95f)
            // La lámpara del nicho: apagada de día, encendida de noche.
            lamparaEn(pie, lw, oscuro, encendida = oscuro, cerca = 1f)

            // La arqueta (o, sin tema, el cesto de juncos), al otro lado de la lámpara.
            val ancho = w * 0.58f; val alto = h * 0.24f; val hondo = w * 0.24f
            val x0 = if (izquierda) nr.right - w * 0.06f - ancho else nr.left + w * 0.06f
            val k = c.n.coerceIn(0, 10)
            if (c.sinTema) {
                cesto(Offset(x0 + ancho * 0.5f, piso - h * 0.03f), ancho * 0.72f, alto * 0.95f, k, ab, c.semilla, oscuro, fuego.x)
            } else {
                val vista = Vista(x0, piso - h * 0.03f, 0.22f)
                val cerrada = if (k == 0) 0f else minOf(0.04f + 0.022f * k, 0.26f)
                val giro = cerrada + (1.32f - cerrada) * ab
                arqueta(vista, ancho, alto, hondo, giro, k, c.semilla, oscuro, fuego.x)
            }
            sombraInterior(nr, w * 0.04f, if (oscuro) 0.3f else 0.2f)
        }

        // La etiqueta de papiro que cuelga del pasador de la arqueta (o del borde del cesto).
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        val ancho = w * 0.58f
        val x0 = if (izquierda) nr.right - w * 0.06f - ancho else nr.left + w * 0.06f
        val pas = if (c.sinTema) Offset(x0 + ancho * 0.5f, piso - h * 0.12f) else Offset(x0 + ancho * 0.5f + 0f, piso - h * 0.03f - h * 0.24f * 0.22f)
        etiqueta(r, pas, c.semilla, oscuro, if (oscuro) 0.6f else 0f)
    }

    override val rotulo = Rect(0.15f, 0.725f, 0.85f, 0.975f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.caudex
    override val salida = 0f
    override val placa: Rect = Rect(0.3f, 0.36f, 0.7f, 0.74f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior: la arqueta por dentro

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val pl = 18 * u; val pa = 30 * u; val pf = 16 * u
        val fondo = Rect(pl, pa, w - pl, h - pf)
        val centro = Offset(w * 0.5f, h * 0.55f)
        // Lo cerca que está cada punto de la lámpara que se sostiene encima (de noche).
        fun cerca(p: Offset) = (1f - (p - centro).getDistance() / (maxOf(w, h) * 0.75f)).coerceIn(0.15f, 1f)
        fun t(c: Color, p: Offset) = luz(c, oscuro, cerca(p))
        // El fondo: tres tablas de olivo a lo largo, con la veta que se arremolina y sus nudos.
        val tablas = 3
        for (i in 0 until tablas) {
            val r = Rect(fondo.left + fondo.width * i / tablas, fondo.top, fondo.left + fondo.width * (i + 1) / tablas, fondo.bottom)
            val tono = listOf(OLIVO, mezcla(OLIVO, OLIVO_LUZ, 0.3f), mezcla(OLIVO, OLIVO_OSC, 0.2f))[i]
            drawRect(t(tono, r.center), r.topLeft, r.size)
            vetas(r, t(OLIVO_VETA, r.center).copy(alpha = 0.3f), 300 + i, densidad = 0.45f, vertical = true, grosor = 1.1f, ondulacion = 3f)
            for (j in 0 until 2) {
                val c = Offset(r.left + r.width * (0.3f + 0.4f * azar(310 + i, j)), r.top + r.height * (0.15f + 0.7f * azar(320 + i, j)))
                for (a in 1..4) drawOval(t(OLIVO_VETA, c).copy(alpha = 0.3f - 0.05f * a), Offset(c.x - a * 2.2f * u, c.y - a * 5f * u), Size(a * 4.4f * u, a * 10f * u), style = Stroke(0.8f * u))
                drawOval(t(OLIVO_VETA, c).copy(alpha = 0.6f), Offset(c.x - 2 * u, c.y - 3.5f * u), Size(4 * u, 7 * u))
            }
            pinceladas(r, t(OLIVO_LUZ, r.center), 330 + i, 26, u, 0.1f)
            if (i > 0) {
                drawLine(t(mezcla(OLIVO_VETA, SOMBRA_MUEBLE, 0.4f), r.center).copy(alpha = 0.8f), Offset(r.left, r.top), Offset(r.left, r.bottom), 1.4f * u)
                drawLine(luzMueble(if (oscuro) 0.05f else 0.3f), Offset(r.left + 1.2f * u, r.top), Offset(r.left + 1.2f * u, r.bottom), 0.7f * u)
            }
        }
        // Lo que hay en el fondo, debajo de las hojas: el cordel desatado, un cálamo, un manojo de hisopo y unos recortes.
        val cuerda = t(CORDEL, Offset(w * 0.5f, h * 0.5f))
        cordel(Path().apply { moveTo(fondo.left - 2 * u, h * 0.3f); cubicTo(w * 0.3f, h * 0.22f, w * 0.45f, h * 0.62f, w * 0.7f, h * 0.55f); cubicTo(w * 0.85f, h * 0.5f, w * 0.8f, h * 0.8f, fondo.right - 30 * u, h * 0.86f) }, cuerda, 2f * u)
        val c0 = Offset(fondo.right - w * 0.36f, fondo.bottom - 18 * u)
        rotate(-12f, c0) {
            drawLine(sombraMueble(0.35f), c0 + Offset(2 * u, 3 * u), c0 + Offset(w * 0.32f, 3 * u), 4 * u, StrokeCap.Round)
            drawLine(t(Color(0xFFC2A86E), c0), c0, c0 + Offset(w * 0.3f, 0f), 3.4f * u, StrokeCap.Round)
            for (k in 1..2) drawLine(t(Color(0xFF8E7444), c0), c0 + Offset(w * 0.1f * k, -1.7f * u), c0 + Offset(w * 0.1f * k, 1.7f * u), 0.8f * u)
            drawPath(Path().apply { moveTo(c0.x, c0.y - 1.7f * u); lineTo(c0.x - 7 * u, c0.y); lineTo(c0.x, c0.y + 1.7f * u); close() }, t(Color(0xFF9E8650), c0))
            drawLine(TINTA.copy(alpha = 0.8f), c0 + Offset(-6.5f * u, 0f), c0 + Offset(-3f * u, 0f), 0.8f * u)
        }
        val ma = Offset(fondo.left + 22 * u, fondo.top + 30 * u)
        for (k in 0 until 9) {
            val a = 0.3f + 0.13f * k
            val fin = ma + Offset(cos(a) * w * 0.2f, sin(a) * w * 0.2f)
            drawLine(t(Color(0xFF6E6A44), ma), ma, fin, 1 * u, StrokeCap.Round)
            for (j in 1..4) {
                val p = ma + (fin - ma) * (j / 4.5f)
                drawOval(t(Color(0xFF7E7F52), p), Offset(p.x - 2.4f * u, p.y - 1.1f * u), Size(4.8f * u, 2.2f * u))
            }
        }
        cordel(ma + Offset(-3 * u, 2 * u), ma + Offset(6 * u, -3 * u), cuerda, 2.4f * u)
        for (k in 0 until 3) {
            val p = Offset(fondo.left + fondo.width * (0.15f + 0.3f * k), fondo.top + fondo.height * (0.72f + 0.12f * azar(340, k)))
            rotate(-30f + 60f * azar(341, k), p) {
                val r = Rect(p.x - 9 * u, p.y - 5 * u, p.x + 9 * u, p.y + 5 * u)
                drawPath(hoja(r, 342 + k, 18, 0.08f), t(PAPIRO_OSC, p))
            }
        }
        // De noche, solo la lámpara que se sostiene encima alumbra el fondo; las esquinas se hunden.
        if (oscuro) {
            mancha(centro, maxOf(w, h) * 0.55f, LLAMA_HALO.copy(alpha = 0.12f))
            drawRect(Brush.radialGradient(listOf(Color.Transparent, NOCHE.copy(alpha = 0.55f)), centro, maxOf(w, h) * 0.75f))
        }
        // Las paredes, vistas desde arriba: la de atrás y la de la izquierda en sombra, la de delante y la de la derecha a la luz.
        val rim = 5 * u
        drawPath(cuad(Offset(0f, 0f), Offset(w, 0f), Offset(w - pl, pa), Offset(pl, pa)), t(mezcla(OLIVO_OSC, SOMBRA_MUEBLE, 0.3f), Offset(w / 2, 0f)))
        drawPath(cuad(Offset(0f, 0f), Offset(pl, pa), Offset(pl, h - pf), Offset(0f, h)), t(mezcla(OLIVO_OSC, SOMBRA_MUEBLE, 0.2f), Offset(0f, h / 2)))
        drawPath(cuad(Offset(w, 0f), Offset(w, h), Offset(w - pl, h - pf), Offset(w - pl, pa)), t(OLIVO, Offset(w, h / 2)))
        drawPath(cuad(Offset(0f, h), Offset(pl, h - pf), Offset(w - pl, h - pf), Offset(w, h)), t(mezcla(OLIVO, OLIVO_LUZ, 0.4f), Offset(w / 2, h)))
        vetas(Rect(0f, 0f, w, pa), t(OLIVO_VETA, Offset(w / 2, 0f)).copy(alpha = 0.2f), 351, densidad = 0.7f)
        vetas(Rect(w - pl, 0f, w, h), t(OLIVO_VETA, Offset(w, h / 2)).copy(alpha = 0.2f), 352, densidad = 0.7f, vertical = true)
        vetas(Rect(0f, 0f, pl, h), t(OLIVO_VETA, Offset(0f, h / 2)).copy(alpha = 0.2f), 353, densidad = 0.7f, vertical = true)
        // El canto de las paredes, por arriba, con la luz en el borde.
        drawRect(t(OLIVO_LUZ, Offset(w / 2, 0f)), Offset.Zero, Size(w, rim))
        drawRect(t(OLIVO_LUZ, Offset(0f, h / 2)), Offset.Zero, Size(rim, h))
        drawRect(t(mezcla(OLIVO_LUZ, OLIVO, 0.4f), Offset(w, h / 2)), Offset(w - rim, 0f), Size(rim, h))
        drawRect(t(mezcla(OLIVO_LUZ, OLIVO, 0.2f), Offset(w / 2, h)), Offset(0f, h - rim), Size(w, rim))
        drawRect(sombraMueble(0.3f), Offset(rim, rim), Size(w - 2 * rim, 1f * u))
        drawRect(sombraMueble(0.3f), Offset(rim, rim), Size(1f * u, h - 2 * rim))
        // Las aristas donde las paredes se encuentran con el fondo.
        drawPath(Path().apply { moveTo(pl, pa); lineTo(w - pl, pa); lineTo(w - pl, h - pf); lineTo(pl, h - pf); close() }, sombraMueble(if (oscuro) 0.6f else 0.35f), style = Stroke(1.2f * u))
        sombraInterior(fondo, 22 * u, if (oscuro) 0.4f else 0.25f)
        // Las bisagras de bronce en el canto de atrás, y los clavos de las esquinas.
        listOf(0.22f, 0.78f).forEach { f ->
            val b = Rect(w * f - 10 * u, 0f, w * f + 10 * u, rim + 3 * u)
            drawRect(sombraMueble(0.4f), b.translate(1 * u, 1.2f * u).topLeft, b.size)
            drawRect(Brush.verticalGradient(listOf(t(BRONCE_LUZ, b.center), t(BRONCE, b.center)), b.top, b.bottom), b.topLeft, b.size)
            drawLine(t(mezcla(BRONCE, SOMBRA_MUEBLE, 0.5f), b.center), Offset(b.left, b.top + rim * 0.5f), Offset(b.right, b.top + rim * 0.5f), 0.8f * u)
            listOf(b.left + 3 * u, b.right - 3 * u).forEach { x -> drawCircle(t(mezcla(BRONCE, SOMBRA_MUEBLE, 0.4f), b.center), 1 * u, Offset(x, b.bottom - 2 * u)) }
        }
        listOf(Offset(rim * 0.5f, rim * 0.5f), Offset(w - rim * 0.5f, rim * 0.5f), Offset(rim * 0.5f, h - rim * 0.5f), Offset(w - rim * 0.5f, h - rim * 0.5f)).forEach { p ->
            drawCircle(t(BRONCE, p), 1.6f * u, p)
            drawCircle(luzMueble(if (oscuro) 0.1f else 0.5f), 0.6f * u, p - Offset(0.5f * u, 0.5f * u))
        }
        granoMueble(Rect(0f, 0f, w, h), oscuro, 0.7f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) LINO else TINTA
    override val pared: Dp = 22.dp
    override val columnas = 2
    override val lineas = 4

    // ---------------------------------------------------------------- las fichas: hojas sueltas de papiro

    /** El contorno de la hoja: deshilachado. Si es muy alta (el escritor), con más puntos, para que el borde siga fino. */
    private fun contornoHoja(r: Rect, semilla: Int) = hoja(r, semilla, if (r.height > r.width * 1.6f) 110 else 52, 0.009f)

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoHoja(Rect(0f, 0f, s.width, s.height), semilla)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        val borde = contornoHoja(Rect(0f, 0f, w, h), semilla)
        // El papiro se oscurece hacia los cantos, que se deshilachan.
        drawRect(Brush.radialGradient(listOf(Color.Transparent, FIBRA.copy(alpha = 0.16f)), Offset(w * 0.45f, h * 0.45f), maxOf(w, h) * 0.75f))
        drawPath(borde, FIBRA.copy(alpha = 0.32f), style = Stroke(2.4f * u * g))
        drawPath(borde, FIBRA.copy(alpha = 0.4f), style = Stroke(0.8f * u))
        // La juntura de dos hojas pegadas (la kóllesis): una franja apenas más oscura, con su borde.
        val xj = w * (0.66f + 0.18f * azar(semilla, 1))
        drawRect(FIBRA.copy(alpha = 0.06f), Offset(xj, 0f), Size(5f * u * g, h))
        drawLine(FIBRA.copy(alpha = 0.18f), Offset(xj, 0f), Offset(xj + 0.6f * u, h), 0.6f * u)
        // Fibras sueltas más oscuras.
        for (k in 0 until (if (grande) 9 else 5)) {
            val y = h * azar(semilla, k + 10)
            val x0 = w * azar(semilla, k + 20) * 0.6f
            drawLine(FIBRA.copy(alpha = 0.14f), Offset(x0, y), Offset(x0 + w * (0.15f + 0.3f * azar(semilla, k + 30)), y + (azar(semilla, k + 40) - 0.5f) * u), 0.6f * u)
        }
        // Los dos agujeros del cordel que ata las hojas, en el margen de la izquierda, y el cordel que pasa por el canto.
        val x = 6.5f * u * g
        val ys = listOf(h * 0.36f, h * 0.68f)
        ys.forEach { y ->
            cordel(Offset(-2f * u, y - 1.5f * u * g), Offset(x, y), CORDEL, 1.1f * u * g)
            drawCircle(TINTA.copy(alpha = 0.7f), 1.5f * u * g, Offset(x, y))
            drawCircle(mezcla(PAPIRO_OSC, FIBRA, 0.4f), 1.5f * u * g, Offset(x, y), style = Stroke(0.7f * u))
        }
        // El nudo y sus dos cabos en el agujero de arriba.
        val nudo = Offset(x, ys[0])
        cordel(nudo, nudo + Offset(-3f * u * g, 9f * u * g), CORDEL, 0.9f * u * g, 1f * u)
        cordel(nudo, nudo + Offset(2.5f * u * g, 8f * u * g), CORDEL, 0.9f * u * g, 1f * u)
        drawCircle(CORDEL, 1.6f * u * g, nudo)
    }

    override val margenes = MargenesFicha(13.dp, 17.dp, 14.dp, 24.dp, 30.dp, 30.dp)
    override val proporcionFicha = 1.08f
    override val anchoGrande = 0.95f
    override val proporcionGrande = 0.78f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // La bula de barro apretada sobre el cordel: queda sellado.
            "hecho" -> {
                drawCircle(BARRO, s * 0.44f, c)
                drawCircle(BARRO_CLARO, s * 0.38f, c)
                drawCircle(mezcla(BARRO, Color.Black, 0.25f), s * 0.22f, c, style = Stroke(s * 0.08f))
                drawCircle(mezcla(BARRO, Color.Black, 0.25f), s * 0.06f, c)
                drawArc(Color.White.copy(alpha = 0.35f), 200f, 70f, false, Offset(c.x - s * 0.3f, c.y - s * 0.3f), Size(s * 0.6f, s * 0.6f), style = Stroke(s * 0.05f))
            }
            // La chispa: esto es luz.
            "convence" -> with(TrazosArcontes) { chispa(c, s * 0.48f, 1f, p.detalle, TrazosArcontes.GRIETA_HALO) }
            // Las esferas: le sigue dando vueltas.
            "piensa" -> with(TrazosArcontes) { esferas(c, s * 0.44f, p.acento, s * 0.055f, 0.1f, 3) }
            // La serpiente que todavía no se muerde la cola.
            "duda" -> {
                drawArc(p.tenue, 40f, 270f, false, Offset(c.x - s * 0.32f, c.y - s * 0.32f), Size(s * 0.64f, s * 0.64f), style = Stroke(s * 0.11f, cap = StrokeCap.Round))
                val a = Math.toRadians(310.0)
                drawCircle(p.tenue, s * 0.1f, Offset(c.x + s * 0.32f * cos(a).toFloat(), c.y + s * 0.32f * sin(a).toFloat()))
            }
            // La sombra de un arconte: eso es de los de abajo.
            "desacuerdo" -> {
                val disco = Path().apply { addOval(Rect(c, s * 0.46f)) }
                drawPath(disco, TrazosArcontes.SOMBRA.copy(alpha = 0.18f))
                clipPath(disco) { with(TrazosArcontes) { sombraArconte(Offset(c.x - s * 0.08f, c.y - s * 0.04f), s * 0.42f, TrazosArcontes.SOMBRA) } }
                drawPath(disco, TrazosArcontes.SOMBRA.copy(alpha = 0.6f), style = Stroke(s * 0.05f))
            }
            else -> {}
        }
    }

    /** Una gema de jaspe en su bisel de bronce, puesta encima de la hoja para que no se vuele. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawCircle(mezcla(BRONCE, Color.Black, 0.3f), s * 0.47f, c)
        drawCircle(BRONCE_LUZ, s * 0.42f, c)
        with(TrazosArcontes) { gemaJaspe(c, s * 0.36f) }
    }

    override val desorden = 2f
}
