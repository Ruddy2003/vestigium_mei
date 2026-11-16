package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import kotlin.math.sqrt

/**
 * Tierras Antiguas: el paño de un guía, colgado de su bastón. Un paño de lana cruda, «de lana de oveja sin lavar» como las
 * capas del cuento, tejido en casa con sus listas de rubí y de verde bosque abajo y su fleco anudado; en medio, bordada en
 * oro viejo, **la marca de «camino que sigue»** («es la de "camino", con una raya más que no termina»): un palo con dos
 * rayas cortas que suben una a cada lado, como el camino en zetas, y la de abajo, larga, que se va hasta la orilla del
 * paño y no termina en él, como en la última piedra del paso. Cuelga de tres presillas de un bastón de guía de fresno, con
 * su puño forrado de cuero y su punta de hierro, y del puño cuelga de su correa un cuerno de carnero con la boca de latón,
 * el que suena en el paso y el que da nombre a la posada. Sin letras ni cifras: la marca es inventada, de la familia de
 * las que el cuadro talla en los mojones (trazos rectos y cortos), pero sus rayas no salen todas del mismo lado del palo,
 * para que no se lea como ninguna letra ni como ninguna runa de verdad. Nada de gemas, de anillos ni de runas que brillan.
 * Al mantener el dedo sube desde el fleco el oro viejo de la luz de las linternas, y la marca, ya bajo el oro, pasa a
 * verde bosque para que se siga viendo.
 *
 * **[Diseño propio]**: el tema de pixel no tiene banderita. La lana cruda y no el verde bosque, porque arriba a la
 * izquierda del cuadro está el tronco pardo rojizo del pino viejo con sus agujas verde oscuro, y de noche todo es oscuro:
 * el paño claro se despega de los dos a toda hora, y contra el cielo pálido de invierno lo recortan su sombra, su orilla,
 * las listas, el cuerno y el bastón. Los colores son los del tema (`Disenios.kt`: verde bosque #2E5A3E, oro viejo #9A7B2E,
 * rubí #8E1B24) y los de la paleta del cuadro (`ColorAntiguas`: la lana, el fresno, el cuero, la escoria).
 */
internal object BanderitaAntiguas : Banderita {
    private val LANA = Color(0xFFDCD0B4)
    private val LANA_LUZ = Color(0xFFF1E8D4)
    private val LANA_OSC = Color(0xFFAE9F80)
    private val ORILLA = Color(0xFF6C5A40)
    private val ORO = Color(0xFF9A7B2E)
    private val ORO_LUZ = Color(0xFFD9BC6A)
    private val ORO_OSC = Color(0xFF5E4A18)
    private val ORO_LLENO = Color(0xFFC9A04A)
    private val ORO_FILO = Color(0xFFF2DC9A)
    private val VERDE = Color(0xFF2E5A3E)
    private val VERDE_TINTA = Color(0xFF1F3A28)
    private val VERDE_LUZ = Color(0xFF4F7E5C)
    private val RUBI = Color(0xFF8E1B24)
    private val RUBI_LUZ = Color(0xFFB0404A)
    private val FRESNO = Color(0xFFCDB487)
    private val FRESNO_LUZ = Color(0xFFEADAB4)
    private val FRESNO_OSC = Color(0xFF8A7048)
    private val CUERO = Color(0xFF7C4E30)
    private val CUERO_OSC = Color(0xFF4A2E1C)
    private val HIERRO = Color(0xFF2E2C30)
    private val HIERRO_LUZ = Color(0xFF8A8890)
    private val CUERNO = Color(0xFFC2A676)
    private val CUERNO_LUZ = Color(0xFFE6D6AE)
    private val CUERNO_OSC = Color(0xFF6E5838)
    private val HUECO = Color(0xFF2E2218)

    /** El bastón: su eje y su grueso; dónde acaba la madera y empieza la punta de hierro. */
    private const val EJE = 2.2f
    private const val GRUESO = 2.4f
    private const val MADERA_FIN = 22.2f
    /** Las tres presillas del paño y dónde cuelga el paño de ellas. */
    private val PRESILLAS = listOf(6.4f, 13f, 19.6f)
    private const val ARRIBA = 4.4f
    /** El bajo del paño: cae más donde cae cada presilla (los pliegues que bajan de ella) y menos entre ellas. */
    private val BAJO = listOf(Offset(3.5f, 34.2f), Offset(6.4f, 35.2f), Offset(9.7f, 34.6f), Offset(13f, 35.4f), Offset(16.3f, 34.6f), Offset(19.6f, 35.3f), Offset(22.5f, 34.3f))
    /** Hasta dónde llega el fleco. */
    private const val FLECO = 39.4f

    /**
     * La marca de «camino que sigue», en el paño: el palo, la raya de arriba (a la derecha), la del medio (a la
     * izquierda) y la larga de abajo, que sube hacia la derecha y sale por la orilla.
     */
    private val MARCA = listOf(
        Offset(12.8f, 9.8f) to Offset(12.8f, 26.4f),
        Offset(12.8f, 13.8f) to Offset(17f, 10.8f),
        Offset(12.8f, 18.4f) to Offset(8.6f, 15.4f),
        Offset(12.8f, 23.4f) to Offset(25f, 18.6f),
    )

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            val l = lleno.coerceIn(0f, 1f)
            val nivel = FLECO - (FLECO - ARRIBA + 0.4f) * l
            val pano = pano()
            drawPath(Path().apply { addPath(pano, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            baston()
            tela(pano, nivel, l)
            presillas()
            fleco(nivel, l)
            drawPath(pano, ORILLA.copy(alpha = 0.55f), style = Stroke(0.42f))
            cuerno()
        }
    }

    // ---------------------------------------------------------------- El paño

    /** El bajo a la altura de [x]: entre los puntos de [BAJO], en línea recta (lo justo para que las listas lo sigan). */
    private fun bajoEn(x: Float): Float {
        val i = BAJO.indexOfLast { it.x <= x }.coerceIn(0, BAJO.size - 2)
        val a = BAJO[i]; val b = BAJO[i + 1]
        return a.y + (b.y - a.y) * ((x - a.x) / (b.x - a.x)).coerceIn(0f, 1f)
    }

    /** La forma del paño: el borde de arriba, que se comba entre presilla y presilla; las orillas, que ondulan; y el bajo. */
    private fun pano(): Path = Path().apply {
        moveTo(4.2f, ARRIBA)
        var x0 = 4.2f
        for (x in PRESILLAS + 21.8f) {
            val medio = (x0 + x) / 2f
            cubicTo(Offset(x0 + (medio - x0) * 0.5f, ARRIBA + 0.5f), Offset(medio, ARRIBA + 0.6f), Offset(x, ARRIBA - 0.15f))
            x0 = x
        }
        cubicTo(Offset(22.1f, 14f), Offset(22.6f, 25f), BAJO.last())
        for (i in BAJO.size - 2 downTo 0) {
            val a = BAJO[i + 1]; val b = BAJO[i]
            cubicTo(Offset(a.x - (a.x - b.x) * 0.4f, a.y + 0.15f), Offset(b.x + (a.x - b.x) * 0.4f, b.y + 0.15f), b)
        }
        cubicTo(Offset(3.4f, 24f), Offset(4f, 14f), Offset(4.2f, ARRIBA))
        close()
    }

    /** La lana con su luz y sus pliegues, el oro que sube, las listas, el tejido, el uso y la marca bordada. */
    private fun DrawScope.tela(pano: Path, nivel: Float, l: Float) {
        clipPath(pano) {
            drawRect(Brush.linearGradient(listOf(LANA_LUZ, LANA, LANA_OSC), Offset(3f, 6f), Offset(24f, 30f)), Offset(0f, 0f), Size(26f, 40f))
            pliegues()
            tejido()
            if (l > 0f) {
                drawRect(Brush.verticalGradient(listOf(ORO_FILO, ORO_LLENO, ORO), nivel, FLECO), Offset(0f, nivel), Size(26f, 40f - nivel))
                drawLine(Color.White.copy(alpha = 0.6f), Offset(0f, nivel), Offset(26f, nivel), 0.35f)
            }
            listas()
            uso()
            clipRect(0f, 0f, 26f, nivel) { marca(ORO, ORO_LUZ, ORO_OSC) }
            if (nivel < 40f) clipRect(0f, nivel, 26f, 40f) { marca(VERDE_TINTA, VERDE_LUZ, VERDE_TINTA) }
        }
    }

    /**
     * Los pliegues que bajan de cada presilla (el lomo, con luz) y las hondonadas entre ellas (en sombra): bandas de borde
     * blando que se abren y se suavizan hacia el bajo, como cae la lana pesada.
     */
    private fun DrawScope.pliegues() {
        val tramos = 9; val alto = (36f - ARRIBA) / tramos
        for (j in 0 until tramos) {
            val y = ARRIBA + j * alto; val baja = j / (tramos - 1f)
            for ((i, x) in PRESILLAS.withIndex()) banda(x - 0.3f + (i - 1) * 0.7f * baja, 1.7f + 0.8f * baja, y, alto, Color.White, 0.32f - 0.2f * baja)
            for (x in listOf(9.7f, 16.3f, 21.8f)) banda(x + (x - 13f) * 0.06f * baja, 1.9f + 0.7f * baja, y, alto, ORILLA, 0.3f - 0.16f * baja)
        }
    }

    /** Una banda vertical de [color], de [media] de medio ancho, en [x]: más fuerte en medio y nada en las orillas. */
    private fun DrawScope.banda(x: Float, media: Float, y: Float, alto: Float, color: Color, fuerza: Float) {
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, color.copy(alpha = fuerza), Color.Transparent), x - media, x + media), Offset(x - media, y), Size(media * 2f, alto + 0.05f))
    }

    /** La trama de la lana, apenas: hilos de través y alguna mota de lana sin lavar. */
    private fun DrawScope.tejido() {
        var y = ARRIBA + 0.5f
        while (y < 36f) { drawLine(ORILLA.copy(alpha = 0.07f), Offset(3f, y), Offset(23f, y + 0.2f), 0.22f); y += 0.85f }
        for ((x, yy) in listOf(5.6f to 11.2f, 18.8f to 8.4f, 7.2f to 24.6f, 20.4f to 26.8f, 15.6f to 29.4f)) {
            drawLine(ORILLA.copy(alpha = 0.22f), Offset(x, yy), Offset(x + 1.1f, yy + 0.1f), 0.3f, StrokeCap.Round)
        }
    }

    /** Una lista tejida a [d] por encima del bajo, de [g] de grueso: sigue la caída del bajo, pero menos. */
    private fun DrawScope.lista(d: Float, g: Float, color: Color) {
        val arriba = (0..44).map { i -> val x = 2.8f + i * 0.5f; Offset(x, 34.8f + (bajoEn(x) - 34.8f) * 0.55f - d) }
        val forma = Path().apply {
            moveTo(arriba.first())
            for (q in arriba.drop(1)) lineTo(q)
            for (q in arriba.reversed()) lineTo(q + Offset(0f, g))
            close()
        }
        drawPath(forma, color)
    }

    /** Las listas: arriba, una de rubí; abajo, una ancha de rubí entre dos finas de verde bosque, con su brillo de lana. */
    private fun DrawScope.listas() {
        drawRect(RUBI, Offset(0f, 6.5f), Size(26f, 0.75f))
        drawRect(VERDE, Offset(0f, 7.6f), Size(26f, 0.35f))
        lista(6.2f, 0.55f, VERDE)
        lista(5.1f, 1.9f, RUBI)
        lista(4.85f, 0.4f, RUBI_LUZ.copy(alpha = 0.7f))
        lista(2.6f, 0.55f, VERDE)
    }

    /** El uso: el bajo más oscuro, una mancha de hollín o de sebo y un zurcido en una orilla. */
    private fun DrawScope.uso() {
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, ORILLA.copy(alpha = 0.3f)), 31f, 36f), Offset(0f, 31f), Size(26f, 5f))
        drawOval(ORILLA.copy(alpha = 0.12f), Offset(16.4f, 24.2f), Size(3.4f, 2.2f))
        for (j in 0..3) drawLine(LANA_LUZ.copy(alpha = 0.8f), Offset(4.3f, 20.4f + j * 0.55f), Offset(5.6f, 20.2f + j * 0.55f), 0.24f)
    }

    /**
     * La marca bordada: cada raya, un cordón de punto lleno con su sombra (está en relieve sobre la lana), su luz arriba y
     * las puntadas de través. [hilo], [luz] y [hondo]: oro viejo, o verde bosque bajo el oro que sube.
     */
    private fun DrawScope.marca(hilo: Color, luz: Color, hondo: Color) {
        for ((a, b) in MARCA) drawLine(hondo.copy(alpha = 0.55f), a + Offset(0.25f, 0.35f), b + Offset(0.25f, 0.35f), 1.5f, StrokeCap.Round)
        for ((a, b) in MARCA) {
            drawLine(hilo, a, b, 1.35f, StrokeCap.Round)
            val v = b - a; val largo = sqrt(v.x * v.x + v.y * v.y); val u = v / largo
            val n = Offset(u.y, -u.x).let { if (it.y > 0f || (it.y == 0f && it.x > 0f)) -it else it }
            drawLine(luz.copy(alpha = 0.85f), a + n * 0.32f + u * 0.4f, b + n * 0.32f - u * 0.4f, 0.36f, StrokeCap.Round)
            var t = 0.5f
            while (t < largo - 0.3f) { val c = a + u * t; drawLine(hondo.copy(alpha = 0.32f), c + n * 0.6f + u * 0.18f, c - n * 0.6f - u * 0.18f, 0.16f); t += 0.6f }
        }
    }

    /** El fleco: hebras de lana anudadas de tres en tres, que cuelgan del bajo; las que ya cubrió el oro, de oro. */
    private fun DrawScope.fleco(nivel: Float, l: Float) {
        var i = 0; var x = 4.1f
        while (x < 22.1f) {
            val y = bajoEn(x) - 0.3f
            val fin = Offset(x + ((i % 3) - 1) * 0.28f, minOf(FLECO, y + 3.3f + (i % 2) * 0.5f))
            val oro = l > 0f && fin.y > nivel
            drawLine(if (oro) ORO_LLENO else if (i % 2 == 0) LANA else LANA_OSC, Offset(x, y), fin, 0.5f, StrokeCap.Round)
            if (i % 3 == 1) drawCircle(if (oro) ORO else LANA_OSC, 0.55f, Offset(x, y + 0.9f))
            x += 0.9f; i++
        }
    }

    // ---------------------------------------------------------------- El bastón y las presillas

    /** El bastón de fresno, tendido: su luz arriba, las runas del paso talladas a lo largo, el puño de cuero y la punta de hierro. */
    private fun DrawScope.baston() {
        val y0 = EJE - GRUESO / 2f
        drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(1.4f, y0 + 0.8f), Size(MADERA_FIN, GRUESO), CornerRadius(1.2f))
        drawRoundRect(Brush.verticalGradient(listOf(FRESNO_LUZ, FRESNO, FRESNO_OSC), y0, y0 + GRUESO), Offset(0.8f, y0), Size(MADERA_FIN - 0.8f, GRUESO), CornerRadius(1.2f))
        drawLine(Color.White.copy(alpha = 0.35f), Offset(2f, y0 + 0.55f), Offset(MADERA_FIN - 0.4f, y0 + 0.5f), 0.4f, StrokeCap.Round)
        // Las marcas talladas, cortas, una tras otra (se ven entre las presillas).
        for (x in listOf(8.4f, 9.3f, 10.6f, 11.2f, 15.2f, 16.1f, 17.3f, 21.2f)) {
            drawLine(FRESNO_OSC, Offset(x, y0 + 0.6f), Offset(x + 0.35f, y0 + 1.9f), 0.32f, StrokeCap.Round)
            drawLine(FRESNO_LUZ.copy(alpha = 0.7f), Offset(x + 0.3f, y0 + 0.6f), Offset(x + 0.65f, y0 + 1.9f), 0.16f)
        }
        // El puño, forrado de cuero, con sus vueltas.
        drawRoundRect(CUERO, Offset(1.4f, y0 - 0.15f), Size(3.2f, GRUESO + 0.3f), CornerRadius(0.5f))
        for (j in 0..4) drawLine(CUERO_OSC.copy(alpha = 0.8f), Offset(1.6f + j * 0.65f, y0 + GRUESO + 0.1f), Offset(2.1f + j * 0.65f, y0 - 0.1f), 0.22f)
        drawLine(Color.White.copy(alpha = 0.22f), Offset(1.6f, y0 + 0.4f), Offset(4.4f, y0 + 0.4f), 0.3f)
        punta()
    }

    /** La punta de hierro: un regatón en cono con su anillo, oscuro y con un filo de luz. */
    private fun DrawScope.punta() {
        val y0 = EJE - GRUESO / 2f
        val cono = Path().apply { moveTo(MADERA_FIN - 0.4f, y0 + 0.05f); lineTo(24.2f, y0 + 0.4f); lineTo(25.9f, EJE); lineTo(24.2f, y0 + GRUESO - 0.4f); lineTo(MADERA_FIN - 0.4f, y0 + GRUESO - 0.05f); close() }
        drawPath(cono, Brush.verticalGradient(listOf(HIERRO_LUZ, HIERRO, HIERRO), y0, y0 + GRUESO))
        drawLine(Color.White.copy(alpha = 0.4f), Offset(MADERA_FIN, y0 + 0.5f), Offset(24.6f, y0 + 0.75f), 0.25f, StrokeCap.Round)
        drawRoundRect(HIERRO, Offset(MADERA_FIN - 0.7f, y0 - 0.2f), Size(0.8f, GRUESO + 0.4f), CornerRadius(0.25f))
        drawLine(HIERRO_LUZ, Offset(MADERA_FIN - 0.45f, y0 + 0.1f), Offset(MADERA_FIN - 0.45f, y0 + 0.9f), 0.2f)
    }

    /** Las presillas: tiras de la misma lana que dan la vuelta al bastón y bajan cosidas al paño. */
    private fun DrawScope.presillas() {
        val y0 = EJE - GRUESO / 2f
        for (x in PRESILLAS) {
            drawRoundRect(Color.Black.copy(alpha = 0.2f), Offset(x - 1f, y0 + 0.4f), Size(2.4f, GRUESO), CornerRadius(0.6f))
            drawRoundRect(Brush.horizontalGradient(listOf(LANA_LUZ, LANA, LANA_OSC), x - 1.25f, x + 1.25f), Offset(x - 1.25f, y0 - 0.45f), Size(2.5f, ARRIBA + 1.2f - y0), CornerRadius(0.7f))
            drawLine(ORILLA.copy(alpha = 0.35f), Offset(x - 1.1f, y0 + GRUESO + 0.25f), Offset(x + 1.1f, y0 + GRUESO + 0.25f), 0.3f)
            for (dx in listOf(-0.6f, 0.6f)) drawCircle(ORILLA.copy(alpha = 0.6f), 0.18f, Offset(x + dx, ARRIBA + 0.3f))
        }
    }

    // ---------------------------------------------------------------- El cuerno

    /** Un punto de la curva del cuerno, de la boca ancha ([t] = 0) a la punta con el latón ([t] = 1). */
    private fun enCuerno(t: Float): Offset {
        val a = Offset(2.3f, 16.4f); val b = Offset(-0.1f, 12.2f); val c = Offset(0.9f, 6.9f); val d = Offset(5.2f, 6.5f)
        val q = 1f - t
        return a * (q * q * q) + b * (3f * q * q * t) + c * (3f * q * t * t) + d * (t * t * t)
    }

    private fun radioCuerno(t: Float) = 1.85f - 1.3f * t

    /**
     * El cuerno de carnero, colgado del puño por su correa: la sombra que echa en el paño, el cuerno con su volumen y sus
     * anillos, la boca abierta abajo y la boquilla de latón arriba.
     */
    private fun DrawScope.cuerno() {
        // La correa, de la vuelta del puño a los dos cabos del cuerno.
        drawLine(CUERO_OSC, Offset(2.4f, 3.2f), enCuerno(0.12f) + Offset(-1f, 0f), 0.42f, StrokeCap.Round)
        drawLine(CUERO_OSC, Offset(3.4f, 3.3f), enCuerno(0.9f) + Offset(0f, -0.4f), 0.42f, StrokeCap.Round)
        for (i in 0..24) { val t = i / 24f; drawCircle(Color.Black.copy(alpha = 0.035f), radioCuerno(t), enCuerno(t) + Offset(0.5f, 0.7f)) }
        for (i in 0..24) { val t = i / 24f; drawCircle(lerp(CUERNO_OSC, Color(0xFF4A3A24), t * 0.5f), radioCuerno(t), enCuerno(t)) }
        for (i in 0..24) { val t = i / 24f; drawCircle(lerp(CUERNO, CUERNO_OSC, t * 0.55f), radioCuerno(t) * 0.78f, enCuerno(t) + Offset(-0.22f, -0.2f)) }
        for (i in 2..20) { val t = i / 24f; drawCircle(CUERNO_LUZ.copy(alpha = 0.55f), radioCuerno(t) * 0.28f, enCuerno(t) + Offset(-radioCuerno(t) * 0.42f, -radioCuerno(t) * 0.3f)) }
        // Los anillos de través, como los tiene el cuerno de carnero.
        for (j in 1..7) {
            val t = j / 8.5f; val c = enCuerno(t); val v = enCuerno(t + 0.02f) - enCuerno(t - 0.02f)
            val n = Offset(-v.y, v.x).let { it / sqrt(it.x * it.x + it.y * it.y) } * radioCuerno(t)
            drawLine(CUERNO_OSC.copy(alpha = 0.55f), c + n * 0.92f, c - n * 0.92f, 0.22f)
        }
        boca()
        boquilla()
    }

    /** La boca ancha del cuerno, abierta hacia abajo: el hueco oscuro y su borde de luz. */
    private fun DrawScope.boca() {
        val c = enCuerno(0f); val r = radioCuerno(0f)
        drawOval(CUERNO_LUZ, Offset(c.x - r * 0.95f, c.y - r * 0.38f), Size(r * 1.9f, r * 0.95f))
        drawOval(HUECO, Offset(c.x - r * 0.72f, c.y - r * 0.2f), Size(r * 1.44f, r * 0.62f))
    }

    /** La boquilla de latón de la punta, con su anillo y su brillo. */
    private fun DrawScope.boquilla() {
        val c = enCuerno(1f); val v = c - enCuerno(0.94f); val u = v / sqrt(v.x * v.x + v.y * v.y)
        val fin = c + u * 1.3f
        drawLine(ORO_OSC, c, fin, 1.15f, StrokeCap.Round)
        drawLine(ORO, c + Offset(0f, -0.15f), fin + Offset(0f, -0.15f), 0.8f, StrokeCap.Round)
        drawLine(ORO_LUZ, c + Offset(0f, -0.35f), fin + Offset(0f, -0.35f), 0.3f, StrokeCap.Round)
        val n = Offset(-u.y, u.x)
        drawLine(ORO_OSC, c - n * 0.75f, c + n * 0.75f, 0.35f, StrokeCap.Round)
    }
}
