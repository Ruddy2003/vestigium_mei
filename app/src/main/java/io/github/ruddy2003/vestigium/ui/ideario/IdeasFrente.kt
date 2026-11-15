package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.TrazosFrente
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Frente exterior
// =====================================================================

/**
 * Frente exterior: el panel falso de la sala de radar, escondido en las ruinas de la base (`docs/diseno/temas/frente.md`,
 * «La base escondida en ruinas»). Detrás, el muro de sillares viejos con su humedad y su musgo, el tubo de cobre con sus
 * abrazaderas y las raíces que cuelgan y caen por los costados de la consola. Arriba, el pupitre curvo de chapa blanca con
 * sus molduras redondas y una lámpara de cúpula en el hombro; en su panel de consola oscura, de verde casi negro, la pantalla
 * redonda de radar en su caja, la perilla de sintonía y la fila de palancas de baquelita con sus pilotos, y al otro lado el
 * contador de cuatro cifras ámbar, la botonera de botones cuadrados y dos botones; del hombro de la capota cuelga el
 * auricular, y su cordón en espiral baja por el costado hasta el zócalo. El cuerpo parece un tablero de servicio cualquiera,
 * manchado por la humedad, pero cada tema es un nicho cavado en la piedra del muro: su tapa falsa abierta sobre una bisagra
 * de piano (por dentro, la marca de la célula rayada en la chapa), las latas de microfilm apiladas (tantas como fotogramas),
 * un foco rojo de cuarto oscuro y, en el frente del nicho, un piloto y la etiqueta de cinta de papel; de los más llenos
 * cuelga la punta de la película. Al abrirlo, la tapa se abre más y la lata de arriba se levanta y suelta su tapa. Abajo, el
 * zócalo de consola oscura con sus rejillas, la cinta rotuladora roja y el piso de hormigón con la franja de peligro pintada
 * y un cable grueso tirado. De noche es la misma base en penumbra verdosa: la pantalla tiñe de verde el pupitre, la lámpara,
 * los pilotos y los botones alumbran y los nichos quedan a la luz roja de sus focos. Por dentro, el nicho cavado en la
 * piedra, con la lata abierta, su rollo, la tapa boca arriba y la tira de película que lo cruza; en la pared del frente, el
 * casquillo del piloto con sus cables. Las fichas son fotogramas copiados en papel, con las dos orillas perforadas de la
 * película y su número en cifras de segmentos.
 */
object IdeasFrente : DisenoIdeas {
    private val HUESO = TrazosFrente.HUESO
    private val HUESO_LUZ = Color(0xFFF4F0E7)
    private val HUESO_OSC = Color(0xFFC5BCA9)
    /** La sombra de la chapa blanca: un gris verdoso, el de la piedra húmeda de alrededor, no un negro. */
    private val SOMBRA = Color(0xFF4A544D)
    /** El hormigón del piso de la base. */
    private val HORMIGON = Color(0xFFA9ACA2)
    private val PANEL = TrazosFrente.CONSOLA_OSCURA
    private val PANEL_OSC = Color(0xFF141D1A)
    private val PANEL_LUZ = Color(0xFF34453F)
    private val BAQUELITA = TrazosFrente.BAQUELITA
    private val BAQUELITA_LUZ = Color(0xFF4E4741)
    private val CROMO = TrazosFrente.CROMO
    private val CROMO_LUZ = Color(0xFFEEECE7)
    private val CROMO_OSC = Color(0xFF6E6B65)
    private val AMBAR = TrazosFrente.PILOTO_AMBAR
    private val QUEMADO = Color(0xFFB85C0F)
    private val ROJO = TrazosFrente.PILOTO_ROJO
    private val BOTON = Color(0xFFA8201A)
    private val RADAR = TrazosFrente.RADAR
    private val PANTALLA = TrazosFrente.PANTALLA
    private val VERDE = Color(0xFF4F7A5A)
    private val ETIQUETA = Color(0xFFEFE6CF)
    private val PIZARRA = Color(0xFF1E2523)
    private val FORRO = Color(0xFF17201D)
    private val CINTA = Color(0xFF8E241B)
    private val PELICULA = Color(0xFF3B2414)
    private val HUECO = Color(0xFF0C1310)
    /** La penumbra verdosa de la base de noche. */
    private val NOCHE = Color(0xFF07110D)
    /** Los colores de las latas: aluminio, latón anodizado, rojo óxido, verde gris y aluminio viejo. */
    private val COLORES_LATA = listOf(CROMO, Color(0xFFC9A15A), Color(0xFF8E3A2C), Color(0xFF6E7F72), Color(0xFF9A978F))

    /** De noche, todo lo que no alumbra queda en la penumbra de la sala. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, NOCHE, 0.6f) else c

    override val cols = 2
    override val proporcion = 1.65f
    override val corona = 0.36f
    override val base = 0.13f
    override val lado = 0.07f
    override val hueco = 0.03f

    /** El radio de las esquinas de cada nicho, en fracción de su lado menor: la curva grande de la consola. */
    private const val RADIO = 0.12f
    /** Dónde empieza el frente del nicho (el canto de la repisa), en fracción de su alto. */
    private const val REPISA = 0.55f

    // ---------------------------------------------------------------- piezas de la consola

    /** El grano de la pintura: puntitos de [color] esparcidos en [r], siempre los mismos para la misma [semilla]. */
    private fun DrawScope.grano(r: Rect, color: Color, n: Int, semilla: Int) {
        val u = density
        for (k in 0 until n) drawCircle(color.copy(alpha = 0.05f + 0.07f * azar(semilla, k + 700)), (0.4f + 0.7f * azar(semilla, k + 800)) * u,
            Offset(r.left + r.width * azar(semilla, k), r.top + r.height * azar(semilla, k + 300)))
    }

    /** Un tornillo de cabeza ranurada, con su sombra y la ranura girada [giro] grados. */
    private fun DrawScope.tornillo(c: Offset, r: Float, giro: Float, oscuro: Boolean) {
        drawCircle(SOMBRA.copy(alpha = 0.35f), r * 1.15f, Offset(c.x + r * 0.2f, c.y + r * 0.28f))
        drawCircle(Brush.radialGradient(listOf(t(CROMO_LUZ, oscuro), t(CROMO_OSC, oscuro)), Offset(c.x - r * 0.35f, c.y - r * 0.35f), r * 1.6f), r, c)
        val a = Math.toRadians(giro.toDouble())
        val dx = (r * 0.78f * cos(a)).toFloat(); val dy = (r * 0.78f * sin(a)).toFloat()
        drawLine(Color.Black.copy(alpha = 0.6f), Offset(c.x - dx, c.y - dy), Offset(c.x + dx, c.y + dy), r * 0.3f)
    }

    /** Una palanca de consola: la tuerca de cromo y la palanca con su punta de baquelita, arriba ([arriba] = 1) o abajo (−1). */
    private fun DrawScope.palanca(c: Offset, l: Float, arriba: Float, oscuro: Boolean) {
        val u = density
        drawCircle(t(CROMO_OSC, oscuro), l * 0.4f, c)
        drawCircle(t(CROMO, oscuro), l * 0.3f, c)
        val punta = Offset(c.x + l * 0.1f, c.y - l * arriba)
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(c.x + 1 * u, c.y + 1.2f * u), Offset(punta.x + 1 * u, punta.y + 1.2f * u), l * 0.2f, StrokeCap.Round)
        drawLine(t(CROMO_LUZ, oscuro), c, punta, l * 0.17f, StrokeCap.Round)
        drawCircle(t(BAQUELITA, oscuro), l * 0.17f, punta)
        drawCircle(Color.White.copy(alpha = 0.3f), l * 0.06f, Offset(punta.x - l * 0.05f, punta.y - l * 0.05f))
    }

    /** La perilla de sintonía: la falda moleteada de baquelita, la tapa, su raya y las marcas de la escala alrededor. */
    private fun DrawScope.perilla(c: Offset, r: Float, giro: Float, marcas: Color, oscuro: Boolean) {
        val u = density
        for (k in 0..10) {
            val a = Math.toRadians(135.0 + k * 27.0)
            val l = if (k % 5 == 0) 1.42f else 1.3f
            drawLine(marcas, Offset(c.x + r * 1.16f * cos(a).toFloat(), c.y + r * 1.16f * sin(a).toFloat()),
                Offset(c.x + r * l * cos(a).toFloat(), c.y + r * l * sin(a).toFloat()), (if (k % 5 == 0) 1.3f else 0.8f) * u)
        }
        drawCircle(Color.Black.copy(alpha = 0.35f), r, Offset(c.x + r * 0.12f, c.y + r * 0.18f))
        drawCircle(t(BAQUELITA, oscuro), r, c)
        for (k in 0 until 28) {
            val a = k * Math.PI / 14
            drawLine(Color.White.copy(alpha = 0.07f), Offset(c.x + r * 0.8f * cos(a).toFloat(), c.y + r * 0.8f * sin(a).toFloat()),
                Offset(c.x + r * 0.98f * cos(a).toFloat(), c.y + r * 0.98f * sin(a).toFloat()), 0.8f * u)
        }
        drawCircle(Brush.radialGradient(listOf(t(BAQUELITA_LUZ, oscuro), t(BAQUELITA, oscuro)), Offset(c.x - r * 0.25f, c.y - r * 0.3f), r * 0.8f), r * 0.7f, c)
        val a = Math.toRadians(giro.toDouble())
        drawLine(t(HUESO_LUZ, oscuro), Offset(c.x + r * 0.15f * cos(a).toFloat(), c.y + r * 0.15f * sin(a).toFloat()),
            Offset(c.x + r * 0.64f * cos(a).toFloat(), c.y + r * 0.64f * sin(a).toFloat()), 1.4f * u, StrokeCap.Round)
    }

    /** Un botón cuadrado de consola, de plástico de [color] en su bisel de cromo; de noche se enciende por detrás. */
    private fun DrawScope.boton(c: Offset, r: Float, color: Color, oscuro: Boolean) {
        val u = density
        if (oscuro) mancha(c, r * 2.8f, color.copy(alpha = 0.3f))
        val b = r * 1.25f
        drawRoundRect(Color.Black.copy(alpha = 0.35f), Offset(c.x - b + 1 * u, c.y - b + 1.4f * u), Size(2 * b, 2 * b), CornerRadius(r * 0.45f))
        drawRoundRect(Brush.verticalGradient(listOf(t(CROMO_LUZ, oscuro), t(CROMO_OSC, oscuro)), c.y - b, c.y + b), Offset(c.x - b, c.y - b), Size(2 * b, 2 * b), CornerRadius(r * 0.45f))
        val cara = if (oscuro) color else mezcla(color, Color.Black, 0.12f)
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(cara, Color.White, 0.25f), cara), c.y - r, c.y + r), Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), CornerRadius(r * 0.3f))
        drawLine(Color.White.copy(alpha = 0.4f), Offset(c.x - r * 0.6f, c.y - r * 0.7f), Offset(c.x + r * 0.6f, c.y - r * 0.7f), 0.8f * u, StrokeCap.Round)
    }

    /** Qué segmentos se encienden para cada cifra (a, b, c, d, e, f, g, del bit más bajo al más alto). */
    private val SEGMENTOS = intArrayOf(0x3F, 0x06, 0x5B, 0x4F, 0x66, 0x6D, 0x7D, 0x07, 0x7F, 0x6F)
    /** Los siete segmentos en una caja de 1 × 1: de dónde a dónde va cada uno. */
    private val TRAMOS = arrayOf(
        floatArrayOf(0f, 0f, 1f, 0f), floatArrayOf(1f, 0f, 1f, 0.5f), floatArrayOf(1f, 0.5f, 1f, 1f), floatArrayOf(0f, 1f, 1f, 1f),
        floatArrayOf(0f, 0.5f, 0f, 1f), floatArrayOf(0f, 0f, 0f, 0.5f), floatArrayOf(0f, 0.5f, 1f, 0.5f),
    )

    /** Una cifra de segmentos inclinada, como la del contador de la consola: [n] de 0 a 9, con los segmentos apagados tenues. */
    private fun DrawScope.cifra(r: Rect, n: Int, luz: Color, apagado: Color, grosor: Float) {
        val mapa = SEGMENTOS[n.coerceIn(0, 9)]
        val incl = 0.18f
        fun p(x: Float, y: Float) = Offset(r.left + (x + (1 - y) * incl) * r.width / (1 + incl), r.top + y * r.height)
        TRAMOS.forEachIndexed { k, s ->
            val x0 = s[0] + (s[2] - s[0]) * 0.16f; val y0 = s[1] + (s[3] - s[1]) * 0.16f
            val x1 = s[2] - (s[2] - s[0]) * 0.16f; val y1 = s[3] - (s[3] - s[1]) * 0.16f
            drawLine(if (((mapa shr k) and 1) == 1) luz else apagado, p(x0, y0), p(x1, y1), grosor, StrokeCap.Round)
        }
    }

    /** El visor de cifras de segmentos sobre vidrio oscuro, en su marco: [valor] con [cifras] cifras. De noche echa su luz. */
    private fun DrawScope.visor(r: Rect, valor: Int, cifras: Int, luz: Color, oscuro: Boolean) {
        val u = density
        if (oscuro) mancha(r.center, r.width * 0.75f, luz.copy(alpha = 0.22f))
        drawRoundRect(t(CROMO_OSC, oscuro), Offset(r.left - 1.8f * u, r.top - 1.8f * u), Size(r.width + 3.6f * u, r.height + 3.6f * u), CornerRadius(2.5f * u))
        drawRoundRect(Color(0xFF1C1008), r.topLeft, r.size, CornerRadius(1.5f * u))
        var tope = 1
        repeat(cifras) { tope *= 10 }
        val v = valor.coerceIn(0, tope - 1)
        val pad = r.height * 0.17f
        val celda = (r.width - pad) / cifras
        var div = tope / 10
        for (i in 0 until cifras) {
            val l = r.left + pad / 2 + celda * i
            cifra(Rect(l + celda * 0.16f, r.top + pad, l + celda * 0.84f, r.bottom - pad), (v / div) % 10, luz, luz.copy(alpha = 0.08f), celda * 0.13f)
            div /= 10
        }
        drawLine(Color.White.copy(alpha = 0.12f), Offset(r.left + 2 * u, r.top + 1.2f * u), Offset(r.right - 2 * u, r.top + 1.2f * u), 0.8f * u)
    }

    /** El auricular colgado de su gancho: la diadema de baquelita, las dos copas con su almohadilla y el micrófono en su brazo. */
    private fun DrawScope.auricular(g: Offset, r: Float, oscuro: Boolean) {
        val u = density
        drawArc(Color.Black.copy(alpha = 0.25f), 180f, 180f, false, Offset(g.x - r + 1.2f * u, g.y + 1.6f * u), Size(2 * r, 2 * r), style = Stroke(2.6f * u))
        drawArc(t(BAQUELITA, oscuro), 180f, 180f, false, Offset(g.x - r, g.y), Size(2 * r, 2 * r), style = Stroke(2.6f * u, cap = StrokeCap.Round))
        drawArc(Color.White.copy(alpha = if (oscuro) 0.06f else 0.2f), 215f, 55f, false, Offset(g.x - r, g.y), Size(2 * r, 2 * r), style = Stroke(0.8f * u))
        drawCircle(t(CROMO_OSC, oscuro), 1.8f * u, g)
        val cw = r * 0.55f; val chh = r * 0.95f
        listOf(-1f, 1f).forEach { s ->
            val c = Offset(g.x + s * r, g.y + r * 1.25f)
            drawRoundRect(t(mezcla(HUESO_OSC, SOMBRA, 0.4f), oscuro), Offset(if (s < 0) c.x + cw * 0.3f else c.x - cw * 0.62f, c.y - chh * 0.4f), Size(cw * 0.32f, chh * 0.8f), CornerRadius(cw * 0.15f))
            drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(c.x - cw / 2 + 1.2f * u, c.y - chh / 2 + 1.6f * u), Size(cw, chh), CornerRadius(cw * 0.45f))
            drawRoundRect(Brush.horizontalGradient(listOf(t(BAQUELITA_LUZ, oscuro), t(BAQUELITA, oscuro)), c.x - cw / 2, c.x + cw / 2), Offset(c.x - cw / 2, c.y - chh / 2), Size(cw, chh), CornerRadius(cw * 0.45f))
            drawCircle(t(CROMO, oscuro), cw * 0.13f, Offset(c.x, c.y - chh * 0.5f))
        }
        val m0 = Offset(g.x - r, g.y + r * 1.55f)
        drawPath(Path().apply { moveTo(m0.x, m0.y); quadraticTo(m0.x - r * 0.25f, m0.y + r * 0.9f, m0.x + r * 0.35f, m0.y + r * 1.1f) }, t(CROMO_OSC, oscuro), style = Stroke(1.1f * u, cap = StrokeCap.Round))
        drawRoundRect(t(BAQUELITA, oscuro), Offset(m0.x + r * 0.3f, m0.y + r * 0.96f), Size(r * 0.42f, r * 0.3f), CornerRadius(r * 0.15f))
    }

    /** Un cordón en espiral que cuelga de [a] hasta [fondo]: una fila de lazos con su brillo, que se comba un poco. */
    private fun DrawScope.espiral(a: Offset, fondo: Float, ancho: Float, oscuro: Boolean) {
        val u = density
        val paso = 2.6f * u
        val n = ((fondo - a.y) / paso).toInt().coerceAtLeast(1)
        for (k in 0 until n) {
            val x = a.x + 2.5f * u * sin(k / n.toFloat() * Math.PI.toFloat())
            val y = a.y + k * paso
            drawOval(t(BAQUELITA, oscuro), Offset(x - ancho, y), Size(2 * ancho, paso * 1.8f), style = Stroke(1.2f * u))
            drawArc(Color.White.copy(alpha = if (oscuro) 0.06f else 0.2f), 200f, 70f, false, Offset(x - ancho, y), Size(2 * ancho, paso * 1.8f), style = Stroke(0.6f * u))
        }
    }

    /** El muro de sillares de las ruinas en [r]: de día, piedra verdosa; de noche, en la penumbra verde de la base. */
    private fun DrawScope.muro(r: Rect, oscuro: Boolean, alto: Float, semilla: Int) {
        val piedra = if (oscuro) mezcla(TrazosFrente.PIEDRA, NOCHE, 0.56f) else TrazosFrente.PIEDRA
        val junta = if (oscuro) mezcla(TrazosFrente.PIEDRA_SOMBRA, NOCHE, 0.55f) else TrazosFrente.PIEDRA_SOMBRA
        val humedad = if (oscuro) Color(0xFF04100A).copy(alpha = 0.75f) else Color(0xFF34402F).copy(alpha = 0.5f)
        with(TrazosFrente) { muroSillares(r, alto, piedra, junta, humedad, t(MUSGO, oscuro), semilla, if (oscuro) 0.28f else 1f) }
    }

    /** Las raíces que cuelgan desde [y0]: cada una es (x, largo, grosor), en manojo con una compañera más fina. */
    private fun DrawScope.raices(lista: List<Triple<Float, Float, Float>>, y0: Float, oscuro: Boolean, semilla: Int) {
        val color = if (oscuro) mezcla(TrazosFrente.RAIZ, NOCHE, 0.45f) else TrazosFrente.RAIZ
        val brillo = if (oscuro) TrazosFrente.VIDRIO_TRAZO.copy(alpha = 0.14f) else Color(0xFFC9BC94).copy(alpha = 0.32f)
        lista.forEachIndexed { k, (x, largo, grosor) ->
            with(TrazosFrente) {
                raiz(Offset(x + (if (k % 2 == 0) 1f else -1f) * grosor * 0.9f, y0), largo * 0.7f, grosor * 0.55f, mezcla(color, Color.Black, 0.15f), brillo, semilla * 13 + k + 50, sombra = if (oscuro) 0.4f else 0.26f)
                raiz(Offset(x, y0), largo, grosor, color, brillo, semilla * 13 + k, sombra = if (oscuro) 0.4f else 0.28f)
            }
        }
    }

    /** Una botonera de botones cuadrados en [r]: unos encendidos (ámbar, verde del vidrio, blanco o rojo), otros apagados. */
    private fun DrawScope.botonera(r: Rect, cols: Int, filas: Int, semilla: Int, oscuro: Boolean) {
        val u = density
        val paso = minOf(r.width / cols, r.height / filas)
        val lado = paso * 0.7f
        val colores = listOf(AMBAR, TrazosFrente.VIDRIO_TRAZO, HUESO_LUZ, AMBAR, ROJO, RADAR)
        val x0 = r.center.x - paso * cols / 2f; val y0 = r.center.y - paso * filas / 2f
        for (j in 0 until filas) for (i in 0 until cols) {
            val c = Offset(x0 + paso * (i + 0.5f), y0 + paso * (j + 0.5f))
            val k = (semilla * 5 + i * 7 + j * 11) % 13
            val on = k % 3 != 0
            val col = colores[(k + j) % colores.size]
            if (on && oscuro) mancha(c, lado * 1.4f, col.copy(alpha = 0.4f))
            drawRoundRect(Color.Black.copy(alpha = 0.6f), Offset(c.x - lado / 2 - 0.6f * u, c.y - lado / 2 - 0.6f * u), Size(lado + 1.2f * u, lado + 1.2f * u), CornerRadius(1f * u))
            val lente = if (on) (if (oscuro) col else mezcla(col, Color.White, 0.12f)) else t(mezcla(col, PANEL, 0.8f), oscuro)
            drawRoundRect(lente, Offset(c.x - lado / 2, c.y - lado / 2), Size(lado, lado), CornerRadius(0.8f * u))
            drawLine(Color.White.copy(alpha = if (on) 0.5f else 0.1f), Offset(c.x - lado * 0.3f, c.y - lado * 0.28f), Offset(c.x + lado * 0.15f, c.y - lado * 0.28f), lado * 0.1f, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- el microfilm

    /** Un punto de la curva de Bézier cúbica de [a] a [d], en [t] (de 0 a 1). */
    private fun bezier(a: Offset, b: Offset, c: Offset, d: Offset, t: Float): Offset {
        val m = 1 - t
        return a * (m * m * m) + b * (3 * m * m * t) + c * (3 * m * t * t) + d * (t * t * t)
    }

    /**
     * Una tira de película a lo largo de una curva: su sombra, la base color miel con los fotogramas más claros al centro, las
     * rayas entre fotograma y fotograma y las perforaciones de las dos orillas, que dejan ver [fondo].
     */
    private fun DrawScope.tiraPelicula(a: Offset, b: Offset, c: Offset, d: Offset, ancho: Float, fondo: Color, oscuro: Boolean) {
        val u = density
        val n = 140
        val pts = List(n + 1) { bezier(a, b, c, d, it / n.toFloat()) }
        val camino = Path().apply { moveTo(pts[0].x, pts[0].y); for (i in 1..n) lineTo(pts[i].x, pts[i].y) }
        translate(ancho * 0.08f + 1 * u, ancho * 0.12f + 1 * u) { drawPath(camino, Color.Black.copy(alpha = 0.35f), style = Stroke(ancho)) }
        drawPath(camino, t(PELICULA, oscuro), style = Stroke(ancho))
        drawPath(camino, t(mezcla(PELICULA, AMBAR, 0.32f), oscuro).copy(alpha = 0.6f), style = Stroke(ancho * 0.6f))
        val paso = ancho * 0.32f
        var acc = 0f; var cuenta = 0
        for (i in 1 until n) {
            acc += (pts[i] - pts[i - 1]).getDistance()
            if (acc < paso) continue
            acc = 0f; cuenta++
            val tg = pts[i + 1] - pts[i - 1]
            val l = tg.getDistance().coerceAtLeast(0.001f)
            val tx = tg.x / l; val ty = tg.y / l
            listOf(1f, -1f).forEach { s ->
                val o = Offset(pts[i].x - ty * s * ancho * 0.39f, pts[i].y + tx * s * ancho * 0.39f)
                drawLine(fondo, Offset(o.x - tx * ancho * 0.05f, o.y - ty * ancho * 0.05f), Offset(o.x + tx * ancho * 0.05f, o.y + ty * ancho * 0.05f), ancho * 0.1f, StrokeCap.Round)
            }
            if (cuenta % 4 == 0) drawLine(t(PELICULA, oscuro), Offset(pts[i].x - ty * ancho * 0.3f, pts[i].y + tx * ancho * 0.3f),
                Offset(pts[i].x + ty * ancho * 0.3f, pts[i].y - tx * ancho * 0.3f), maxOf(0.6f * u, ancho * 0.05f))
        }
        drawPath(camino, Color.White.copy(alpha = if (oscuro) 0.04f else 0.1f), style = Stroke(ancho * 0.08f))
    }

    /**
     * Una lata de microfilm acostada, vista un poco desde arriba: el canto de metal con su reborde y su etiqueta y, si es la
     * de arriba de la pila, la tapa con sus anillos prensados. Con [tapa] más de 0, la tapa se despega y deja ver el rollo.
     */
    private fun DrawScope.lata(r: Rect, color: Color, tope: Boolean, tapa: Float, oscuro: Boolean) {
        val u = density
        val elipse = r.height * 0.9f
        val metal = Brush.horizontalGradient(0f to mezcla(color, Color.Black, 0.45f), 0.25f to mezcla(color, Color.White, 0.3f), 0.55f to color,
            1f to mezcla(color, Color.Black, 0.5f), startX = r.left, endX = r.right)
        drawRect(metal, r.topLeft, r.size)
        drawArc(metal, 0f, 180f, true, Offset(r.left, r.bottom - elipse / 2), Size(r.width, elipse))
        drawArc(Color.Black.copy(alpha = 0.4f), 0f, 180f, false, Offset(r.left, r.bottom - elipse / 2), Size(r.width, elipse), style = Stroke(0.8f * u))
        drawArc(Color.White.copy(alpha = 0.3f), 10f, 160f, false, Offset(r.left, r.top - elipse / 2 + r.height * 0.32f), Size(r.width, elipse), style = Stroke(0.7f * u))
        // La etiqueta pegada en el canto.
        val e = Rect(r.center.x - r.width * 0.17f, r.top + r.height * 0.42f, r.center.x + r.width * 0.17f, r.bottom + elipse * 0.22f)
        drawRect(t(ETIQUETA, oscuro).copy(alpha = 0.92f), e.topLeft, e.size)
        drawLine(t(PIZARRA, oscuro).copy(alpha = 0.6f), Offset(e.left + e.width * 0.15f, e.center.y), Offset(e.left + e.width * 0.7f, e.center.y), 0.7f * u)
        if (!tope) return
        val boca = Rect(r.left, r.top - elipse / 2, r.right, r.top + elipse / 2)
        if (tapa > 0.01f) {
            // La lata abierta: el rollo de película color miel y el carrete de cromo.
            drawOval(Color(0xFF0B0907), boca.topLeft, boca.size)
            val rollo = Rect(boca.left + boca.width * 0.1f, boca.top + boca.height * 0.14f, boca.right - boca.width * 0.1f, boca.bottom - boca.height * 0.06f)
            drawOval(t(PELICULA, oscuro), rollo.topLeft, rollo.size)
            for (j in 1..3) {
                val f = 0.1f * j
                drawOval(Color.White.copy(alpha = 0.07f), Offset(rollo.left + rollo.width * f, rollo.top + rollo.height * f), Size(rollo.width * (1 - 2 * f), rollo.height * (1 - 2 * f)), style = Stroke(0.6f * u))
            }
            drawOval(t(CROMO, oscuro), Offset(rollo.center.x - rollo.width * 0.14f, rollo.center.y - rollo.height * 0.14f), Size(rollo.width * 0.28f, rollo.height * 0.28f))
        }
        val tp = boca.translate(0f, -tapa * r.height * 1.3f)
        rotate(-10f * tapa, tp.center) {
            drawOval(Brush.linearGradient(listOf(mezcla(color, Color.White, 0.4f), color, mezcla(color, Color.Black, 0.25f)), tp.topLeft, tp.bottomRight), tp.topLeft, tp.size)
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(tp.left + tp.width * 0.16f, tp.top + tp.height * 0.16f), Size(tp.width * 0.68f, tp.height * 0.68f), style = Stroke(0.7f * u))
            drawOval(Color.White.copy(alpha = 0.28f), Offset(tp.left + tp.width * 0.3f, tp.top + tp.height * 0.3f), Size(tp.width * 0.4f, tp.height * 0.4f), style = Stroke(0.6f * u))
            drawOval(mezcla(color, Color.Black, 0.3f), Offset(tp.center.x - tp.width * 0.06f, tp.center.y - tp.height * 0.06f), Size(tp.width * 0.12f, tp.height * 0.12f))
        }
    }

    /**
     * Las latas apiladas en el nicho: dos pilas, tantas latas como fotogramas (hasta diez), cada una un poco corrida. Al abrir el
     * nicho, la de arriba de la primera pila se levanta y suelta su tapa.
     */
    private fun DrawScope.latas(c: EstadoCajon, piso: Float, ab: Float, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val k = c.n.coerceIn(0, 10)
        val alto = h * 0.085f
        val ancho = w * 0.27f
        listOf(w * 0.44f to (k + 1) / 2, w * 0.75f to k / 2).forEachIndexed { pi, (cx, cuantas) ->
            for (i in 0 until cuantas) {
                val tope = i == cuantas - 1
                val sube = if (pi == 0 && tope) h * 0.14f * ab else 0f
                val dx = (azar(c.semilla + pi * 17, i) - 0.5f) * w * 0.035f
                val col = if (c.sinTema) CROMO else COLORES_LATA[Math.floorMod(c.semilla + i * 3 + pi * 5, COLORES_LATA.size)]
                lata(Rect(cx - ancho / 2 + dx, piso - (i + 1) * alto - sube, cx + ancho / 2 + dx, piso - i * alto - sube), t(col, oscuro), tope, if (pi == 0 && tope) ab else 0f, oscuro)
            }
        }
    }

    /** La etiqueta de cinta de papel, con las puntas rasgadas a mano, su sombra y sus fibras. */
    private fun DrawScope.cinta(r: Rect, semilla: Int, oscuro: Boolean) {
        val u = density
        val n = 5
        val p = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top)
            for (k in 1..n) lineTo(r.right + (if (k % 2 == 1) -1.6f else 0.4f) * u * (0.6f + azar(semilla, k)), r.top + r.height * k / n)
            lineTo(r.left, r.bottom)
            for (k in n - 1 downTo 1) lineTo(r.left + (if (k % 2 == 1) 1.6f else -0.4f) * u * (0.6f + azar(semilla, k + 10)), r.top + r.height * k / n)
            close()
        }
        translate(0.8f * u, 1.2f * u) { drawPath(p, Color.Black.copy(alpha = 0.25f)) }
        drawPath(p, if (oscuro) mezcla(ETIQUETA, NOCHE, 0.25f) else ETIQUETA)
        clipPath(p) {
            for (k in 0 until 7) {
                val y = r.top + r.height * azar(semilla, k + 30)
                drawLine(SOMBRA.copy(alpha = 0.08f), Offset(r.left, y), Offset(r.right, y + 0.6f * u), 0.5f * u)
            }
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.25f), Color.Transparent), r.top, r.top + r.height * 0.4f), r.topLeft, Size(r.width, r.height * 0.4f))
        }
    }

    // ---------------------------------------------------------------- el mueble

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        val y0 = d.corona.top
        fun tn(x: Color) = t(x, oscuro)
        // El muro de las ruinas donde se esconde la consola: sillares viejos con su humedad y su musgo, las raíces de atrás y,
        // arriba, el tubo de cobre con sus abrazaderas.
        muro(Rect(0f, y0, w, d.base.top), oscuro, 19f * u, 5)
        if (oscuro) mancha(Offset(w / 2, y0 + ch * 0.55f), w * 0.7f, RADAR.copy(alpha = 0.1f))
        raices(listOf(Triple(w * 0.025f, ch * 0.9f, 3.4f * u), Triple(w * 0.3f, ch * 0.14f, 2.4f * u), Triple(w * 0.985f, ch * 1.1f, 3.6f * u)), y0, oscuro, 21)
        val yTubo = y0 + ch * 0.05f
        with(TrazosFrente) { tubo(Offset(-2f * u, yTubo), Offset(w + 2f * u, yTubo), 4.2f * u, tn(TrazosFrente.COBRE), if (oscuro) 0.2f else 1f, juntas = 90f * u) }
        listOf(0.1f, 0.38f, 0.66f, 0.92f).forEach { f ->
            drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(w * f - 2f * u, yTubo - 3f * u), Size(5 * u, 7f * u), CornerRadius(1 * u))
            drawRoundRect(tn(CROMO_OSC), Offset(w * f - 2.5f * u, yTubo - 3.4f * u), Size(5 * u, 6.8f * u), CornerRadius(1 * u))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.3f), Offset(w * f - 1.8f * u, yTubo - 2.8f * u), Offset(w * f - 1.8f * u, yTubo + 2.8f * u), 0.7f * u)
        }
        // La lámpara de cúpula blanca sobre el hombro de la consola.
        with(TrazosFrente) { lamparaCupula(Offset(w * 0.4f, y0 + ch * 0.1f - 3f * u), 7f * u, 3.5f * u, tn(CROMO_OSC), if (oscuro) mezcla(HUESO_LUZ, NOCHE, 0.12f) else HUESO_LUZ, if (oscuro) 1f else 0.5f) }

        // La capota curva del pupitre, de chapa color hueso, con su sombra en la pared y la luz de arriba.
        val capota = Rect(w * 0.02f, y0 + ch * 0.1f, w * 0.98f, d.corona.bottom)
        val rc = ch * 0.3f
        val formaCapota = Path().apply { addRoundRect(RoundRect(capota, CornerRadius(rc), CornerRadius(rc), CornerRadius.Zero, CornerRadius.Zero)) }
        sombraSuave(capota, rc, 7 * u, 0.2f)
        drawPath(formaCapota, Brush.verticalGradient(listOf(tn(HUESO_LUZ), tn(HUESO), tn(mezcla(HUESO, HUESO_OSC, 0.6f))), capota.top, capota.bottom))
        clipPath(formaCapota) {
            drawRect(Brush.horizontalGradient(0f to SOMBRA.copy(alpha = 0.3f), 0.07f to Color.Transparent, 0.93f to Color.Transparent, 1f to SOMBRA.copy(alpha = 0.36f),
                startX = capota.left, endX = capota.right), capota.topLeft, capota.size)
            if (!oscuro) mancha(Offset(w * 0.22f, capota.top), w * 0.4f, Color.White.copy(alpha = 0.3f))
            grano(capota, SOMBRA, 90, 3)
        }
        drawLine(Color.White.copy(alpha = if (oscuro) 0.1f else 0.55f), Offset(capota.left + rc, capota.top + 0.8f * u), Offset(capota.right - rc, capota.top + 0.8f * u), 1f * u)
        // La rejilla de la capota y, pintada a escondidas en la chapa, la marca de la célula, ya descascarada.
        val banda = capota.top + ch * 0.07f
        for (k in 0 until 10) {
            val x = w * 0.62f + k * 4.2f * u
            drawRoundRect(tn(SOMBRA).copy(alpha = 0.7f), Offset(x, banda - ch * 0.035f), Size(1.8f * u, ch * 0.07f), CornerRadius(0.9f * u))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.4f), Offset(x, banda + ch * 0.04f), Offset(x + 1.8f * u, banda + ch * 0.04f), 0.6f * u)
        }
        val marca = Offset(w * 0.2f, banda)
        with(TrazosFrente) { emblemaRed(marca, ch * 0.042f, tn(BOTON).copy(alpha = 0.8f), 1.2f * u) }
        for (k in 0 until 6) drawCircle(tn(HUESO), 0.6f * u, Offset(marca.x + ch * 0.12f * (azar(41, k) - 0.5f), marca.y + ch * 0.1f * (azar(42, k) - 0.5f)))

        // Las molduras redondas de la capota, sobre la de cromo: la consola blanca anillada.
        for (k in 0 until 2) {
            val y = d.corona.bottom - 6f * u - (k + 1) * 3.4f * u
            val m = Rect(capota.left + 0.5f * u, y, capota.right - 0.5f * u, y + 3.4f * u)
            drawRoundRect(Brush.verticalGradient(listOf(tn(HUESO_LUZ), tn(HUESO), tn(mezcla(HUESO, SOMBRA, 0.35f))), m.top, m.bottom), m.topLeft, m.size, CornerRadius(1.7f * u))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.75f), Offset(m.left + 2f * u, m.top + 0.8f * u), Offset(m.right - 2f * u, m.top + 0.8f * u), 0.8f * u, StrokeCap.Round)
            drawLine(SOMBRA.copy(alpha = 0.55f), Offset(m.left + 1f * u, m.bottom - 0.3f * u), Offset(m.right - 1f * u, m.bottom - 0.3f * u), 0.8f * u)
        }

        // El pupitre: el panel hundido de consola oscura, de verde casi negro.
        val panel = Rect(w * 0.055f, y0 + ch * 0.24f, w * 0.905f, y0 + ch * 0.9f)
        val rp = ch * 0.09f
        drawRoundRect(tn(SOMBRA).copy(alpha = 0.55f), Offset(panel.left - 2 * u, panel.top - 2 * u), Size(panel.width + 4 * u, panel.height + 4 * u), CornerRadius(rp + 2 * u))
        val formaPanel = Path().apply { addRoundRect(RoundRect(panel, CornerRadius(rp))) }
        drawPath(formaPanel, Brush.verticalGradient(listOf(tn(PANEL_OSC), tn(PANEL)), panel.top, panel.bottom))
        clipPath(formaPanel) {
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent), panel.top, panel.top + 9 * u), panel.topLeft, Size(panel.width, 9 * u))
            grano(panel, Color.Black, 70, 5)
        }
        drawLine(tn(HUESO_LUZ).copy(alpha = 0.6f), Offset(panel.left + rp, panel.bottom + 2.2f * u), Offset(panel.right - rp, panel.bottom + 2.2f * u), 0.9f * u)

        // La pantalla redonda de radar en su caja de chapa, con el bisel de cromo, la retícula, el barrido y los ecos.
        val pc = Offset(w / 2, panel.center.y)
        val rr = panel.height * 0.46f
        if (oscuro) mancha(pc, rr * 3.2f, RADAR.copy(alpha = 0.3f))
        val caja = Rect(pc.x - rr * 1.2f, pc.y - rr * 1.2f, pc.x + rr * 1.2f, pc.y + rr * 1.2f)
        sombraSuave(caja, rr * 0.38f, 5 * u, 0.32f)
        drawRoundRect(Brush.verticalGradient(listOf(tn(HUESO_LUZ), tn(HUESO), tn(HUESO_OSC)), caja.top, caja.bottom), caja.topLeft, caja.size, CornerRadius(rr * 0.38f))
        drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.4f), Offset(caja.left + 1 * u, caja.top + 1 * u), Size(caja.width - 2 * u, caja.height - 2 * u),
            CornerRadius(rr * 0.36f), style = Stroke(0.9f * u))
        listOf(Offset(-1f, -1f), Offset(1f, -1f), Offset(-1f, 1f), Offset(1f, 1f)).forEachIndexed { i, o ->
            tornillo(Offset(pc.x + o.x * rr * 1.02f, pc.y + o.y * rr * 1.02f), 1.6f * u, 30f + 47f * i, oscuro)
        }
        drawCircle(Brush.linearGradient(listOf(tn(CROMO_LUZ), tn(CROMO), tn(CROMO_OSC)), Offset(pc.x - rr, pc.y - rr), Offset(pc.x + rr, pc.y + rr)), rr * 1.1f, pc)
        drawCircle(BAQUELITA, rr * 1.01f, pc)
        drawCircle(Brush.radialGradient(listOf(mezcla(PANTALLA, RADAR, if (oscuro) 0.2f else 0.12f), PANTALLA), pc, rr), rr * 0.95f, pc)
        with(TrazosFrente) {
            reticula(pc, rr * 0.86f, RADAR.copy(alpha = 0.34f), 0.8f * u, 3)
            barrido(pc, rr * 0.86f, -38f, RADAR.copy(alpha = 0.9f), 1.3f * u)
        }
        listOf(Offset(0.3f, -0.36f), Offset(-0.42f, 0.24f), Offset(0.12f, 0.52f)).forEachIndexed { i, o ->
            val p = Offset(pc.x + o.x * rr, pc.y + o.y * rr)
            mancha(p, 4.5f * u, RADAR.copy(alpha = 0.55f - 0.15f * i))
            drawCircle(RADAR.copy(alpha = 0.95f - 0.25f * i), 1.2f * u, p)
        }
        drawArc(Color.White.copy(alpha = 0.14f), 200f, 70f, false, Offset(pc.x - rr * 0.78f, pc.y - rr * 0.78f), Size(rr * 1.56f, rr * 1.56f), style = Stroke(rr * 0.1f, cap = StrokeCap.Round))

        // La perilla de sintonía y la fila de palancas, cada una con su leyenda arriba y su piloto abajo.
        perilla(Offset(w * 0.125f, panel.center.y + panel.height * 0.04f), panel.height * 0.2f, 205f, tn(HUESO_LUZ).copy(alpha = 0.75f), oscuro)
        val arribas = listOf(1f, -1f, 1f, 1f)
        val luces = listOf(AMBAR, null, AMBAR, ROJO)
        for (k in 0 until 4) {
            val x = w * (0.215f + 0.04f * k)
            drawRect(tn(HUESO_LUZ).copy(alpha = 0.55f), Offset(x - w * 0.011f, panel.top + panel.height * 0.13f), Size(w * 0.022f, 1.4f * u))
            palanca(Offset(x, panel.center.y - panel.height * 0.02f), panel.height * 0.17f, arribas[k], oscuro)
            val luz = luces[k]
            val lp = Offset(x, panel.bottom - panel.height * 0.16f)
            if (oscuro && luz != null) mancha(lp, 10 * u, luz.copy(alpha = 0.45f))
            with(TrazosFrente) { piloto(lp, 1.6f * u, luz ?: AMBAR, luz != null) }
        }
        // El contador de cuatro cifras, la botonera de botones cuadrados y los dos botones.
        val cont = Rect(w * 0.65f, panel.top + panel.height * 0.14f, w * 0.815f, panel.top + panel.height * 0.42f)
        visor(cont, d.huecos.size + d.mas, 4, AMBAR, oscuro)
        val alt = Rect(w * 0.65f, panel.top + panel.height * 0.54f, w * 0.815f, panel.bottom - panel.height * 0.12f)
        drawRoundRect(tn(mezcla(PANEL_OSC, Color.Black, 0.3f)), alt.topLeft, alt.size, CornerRadius(2 * u))
        botonera(alt, 5, 2, 3, oscuro)
        drawRoundRect(Color.White.copy(alpha = 0.1f), alt.topLeft, alt.size, CornerRadius(2 * u), style = Stroke(0.8f * u))
        boton(Offset(w * 0.862f, panel.top + panel.height * 0.3f), w * 0.017f, BOTON, oscuro)
        boton(Offset(w * 0.862f, panel.top + panel.height * 0.7f), w * 0.017f, AMBAR, oscuro)
        // Del hombro de la capota cuelga el auricular.
        val ra = ch * 0.1f
        val gancho = Offset(w * 0.948f, capota.top + ch * 0.13f)
        auricular(gancho, ra, oscuro)

        // El cuerpo de la consola: chapa hueso un poco abombada, con mugre abajo y su grano.
        val cuerpo = Rect(w * 0.03f, d.corona.bottom - 4 * u, w * 0.97f, d.base.top + 3 * u)
        drawRoundRect(Brush.horizontalGradient(0f to tn(HUESO_OSC), 0.05f to tn(HUESO), 0.24f to tn(HUESO_LUZ), 0.6f to tn(HUESO), 0.95f to tn(mezcla(HUESO, HUESO_OSC, 0.5f)),
            1f to tn(HUESO_OSC), startX = cuerpo.left, endX = cuerpo.right), cuerpo.topLeft, cuerpo.size, CornerRadius(5 * u))
        val mugre = minOf(34 * u, cuerpo.height)
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, SOMBRA.copy(alpha = 0.22f)), cuerpo.bottom - mugre, cuerpo.bottom), Offset(cuerpo.left, cuerpo.bottom - mugre), Size(cuerpo.width, mugre))
        grano(cuerpo, SOMBRA, 260, 9)
        // Las juntas de los paneles: una entre las dos columnas y una entre fila y fila, con su canto de luz.
        val cx = w / 2
        drawLine(SOMBRA.copy(alpha = 0.35f), Offset(cx, cuerpo.top + 6 * u), Offset(cx, cuerpo.bottom - 4 * u), 1f * u)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.35f), Offset(cx + 1.1f * u, cuerpo.top + 6 * u), Offset(cx + 1.1f * u, cuerpo.bottom - 4 * u), 0.7f * u)
        d.filas.zipWithNext().forEach { (a, b) ->
            val m = (a.bottom + b.top) / 2
            drawLine(SOMBRA.copy(alpha = 0.35f), Offset(cuerpo.left + 4 * u, m), Offset(cuerpo.right - 4 * u, m), 1f * u)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.35f), Offset(cuerpo.left + 4 * u, m + 1.1f * u), Offset(cuerpo.right - 4 * u, m + 1.1f * u), 0.7f * u)
        }
        // El rebaje de cada nicho: la sombra cálida alrededor y la luz en el labio de abajo.
        d.huecos.forEach { r ->
            val rn = minOf(r.width, r.height) * RADIO
            drawRoundRect(SOMBRA.copy(alpha = 0.6f), Offset(r.left - 2.2f * u, r.top - 2.2f * u), Size(r.width + 4.4f * u, r.height + 4.4f * u), CornerRadius(rn + 2.2f * u))
            drawRoundRect(HUECO, r.topLeft, r.size, CornerRadius(rn))
            drawLine(tn(HUESO_LUZ).copy(alpha = 0.85f), Offset(r.left + rn, r.bottom + 2.8f * u), Offset(r.right - rn, r.bottom + 2.8f * u), 1f * u)
        }
        d.filas.forEachIndexed { i, f -> tornillo(Offset(w * 0.045f, f.center.y), 1.7f * u, 20f + 61f * i, oscuro) }
        // La moldura de cromo entre la capota y el cuerpo.
        val moldura = Rect(w * 0.022f, d.corona.bottom - 6 * u, w * 0.978f, d.corona.bottom - 1.5f * u)
        drawRoundRect(Brush.verticalGradient(listOf(tn(CROMO_LUZ), tn(CROMO), tn(CROMO_OSC)), moldura.top, moldura.bottom), moldura.topLeft, moldura.size, CornerRadius(2 * u))

        // Las manchas de humedad que bajan por la chapa desde la capota, y el óxido bajo los tornillos.
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            listOf(0.05f to 0.5f, 0.5f to 0.18f, 0.93f to 0.42f).forEach { (f, l) ->
                val x = cuerpo.left + cuerpo.width * f; val largo = cuerpo.height * l
                drawRect(Brush.verticalGradient(listOf(tn(Color(0xFF5E6650)).copy(alpha = 0.32f), Color.Transparent), cuerpo.top, cuerpo.top + largo), Offset(x - 5f * u, cuerpo.top), Size(10f * u, largo))
                drawLine(tn(Color(0xFF5E6650)).copy(alpha = 0.25f), Offset(x, cuerpo.top), Offset(x + 1f * u, cuerpo.top + largo * 1.1f), 1.2f * u)
            }
            d.filas.forEach { f -> drawLine(tn(QUEMADO).copy(alpha = 0.22f), Offset(w * 0.045f, f.center.y + 2f * u), Offset(w * 0.046f, f.center.y + 14f * u), 1.4f * u, StrokeCap.Round) }
        }

        // El zócalo de consola oscura, metido bajo la consola, con sus dos rejillas.
        val bh = d.base.height
        val zocalo = Rect(w * 0.045f, cuerpo.bottom, w * 0.955f, d.base.top + bh * 0.68f)
        drawRect(Brush.verticalGradient(listOf(tn(mezcla(PANEL, Color.Black, 0.5f)), tn(PANEL), tn(PANEL_LUZ)), zocalo.top, zocalo.bottom), zocalo.topLeft, zocalo.size)
        listOf(w * 0.07f, w * 0.76f).forEach { x0 ->
            for (fila in 0 until 2) {
                val y = zocalo.top + zocalo.height * (0.38f + 0.3f * fila)
                var x = x0
                while (x < x0 + w * 0.17f) {
                    drawRoundRect(Color.Black.copy(alpha = 0.75f), Offset(x, y - 1 * u), Size(5 * u, 2 * u), CornerRadius(1 * u))
                    drawLine(Color.White.copy(alpha = 0.12f), Offset(x + 0.5f * u, y + 1.4f * u), Offset(x + 4.5f * u, y + 1.4f * u), 0.6f * u)
                    x += 7 * u
                }
            }
        }
        // El parachoques de goma al pie del cuerpo.
        val goma = Rect(w * 0.025f, cuerpo.bottom - 2.5f * u, w * 0.975f, cuerpo.bottom + 2.5f * u)
        drawRoundRect(tn(BAQUELITA_LUZ), goma.topLeft, goma.size, CornerRadius(2.5f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.22f), Offset(goma.left + 3 * u, goma.top + 1 * u), Offset(goma.right - 3 * u, goma.top + 1 * u), 0.8f * u)
        // El piso de hormigón de la base, con sus manchas, la franja de peligro pintada al pie de la consola y un cable grueso
        // tirado; en sombra junto a la consola.
        val piso = Rect(0f, zocalo.bottom, w, d.base.bottom)
        drawRect(Brush.verticalGradient(listOf(tn(mezcla(HORMIGON, Color.Black, 0.25f)), tn(HORMIGON)), piso.top, piso.bottom), piso.topLeft, piso.size)
        for (k in 0 until 14) drawOval(tn(Color(0xFF4A5244)).copy(alpha = 0.1f + 0.1f * azar(61, k)), Offset(w * azar(62, k) - 8 * u, piso.top + piso.height * azar(63, k) - 2 * u), Size((8 + 18 * azar(64, k)) * u, (2 + 3 * azar(65, k)) * u))
        grano(piso, Color.Black, 120, 66)
        with(TrazosFrente) {
            franjaPeligro(Rect(0f, piso.top + 2.5f * u, w, piso.top + 6.5f * u), tn(TrazosFrente.PELIGRO), tn(BAQUELITA), 6f * u)
        }
        for (k in 0 until 30) drawCircle(tn(HORMIGON).copy(alpha = 0.7f), (0.5f + 1.2f * azar(67, k)) * u, Offset(w * azar(68, k), piso.top + 2.5f * u + 4f * u * azar(69, k)))
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent), piso.top, piso.top + 7 * u), piso.topLeft, Size(w, 7 * u))
        with(TrazosFrente) {
            cableGrueso(Path().apply { moveTo(-3f * u, piso.bottom - 2f * u); cubicTo(w * 0.25f, piso.bottom - 12f * u, w * 0.4f, piso.bottom + 2f * u, w * 0.62f, piso.bottom - 5f * u); cubicTo(w * 0.75f, piso.bottom - 9f * u, w * 0.85f, piso.top + 3f * u, w * 0.94f, zocalo.bottom - 2f * u) },
                4.2f * u, tn(BAQUELITA), if (oscuro) 0.3f else 1f, 0.4f)
        }

        // El cordón del auricular, en espiral, por el costado derecho hasta su pasacables en el zócalo.
        val xc = w * 0.957f
        val sale = Offset(gancho.x + ra, gancho.y + ra * 1.25f + ra * 0.475f)
        val inicio = Offset(xc, d.corona.bottom + 2 * u)
        drawPath(Path().apply { moveTo(sale.x, sale.y); quadraticTo(sale.x + 1 * u, inicio.y - ch * 0.1f, inicio.x, inicio.y) }, tn(BAQUELITA), style = Stroke(1.3f * u))
        espiral(inicio, zocalo.top + 2 * u, 3.2f * u, oscuro)
        drawCircle(tn(CROMO_OSC), 2.6f * u, Offset(xc, zocalo.top + 4 * u))
        drawCircle(Color.Black, 1.4f * u, Offset(xc, zocalo.top + 4 * u))

        // Las raíces de delante, que caen del muro por los costados de la consola (nunca sobre el pupitre ni los nichos).
        val tapados = Path().apply {
            addRect(Rect(-w, -w, 2 * w, size.height + w)); fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
            addRoundRect(RoundRect(panel, CornerRadius(rp))); d.huecos.forEach { addRect(it) }
        }
        clipPath(tapados) { raices(listOf(Triple(w * 0.04f, ch * 1.5f, 3.6f * u), Triple(w * 0.075f, ch * 0.4f, 2.2f * u), Triple(w * 0.965f, ch * 0.75f, 3.2f * u), Triple(w * 0.6f, ch * 0.12f, 2.4f * u)), y0, oscuro, 31) }

        // La placa de «y N más»: un trozo de cinta rotuladora roja, con sus letras en relieve.
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRoundRect(Color.Black.copy(alpha = 0.4f), Offset(r.left + 1 * u, r.top + 1.4f * u), r.size, CornerRadius(1.5f * u))
            drawRoundRect(Brush.verticalGradient(listOf(tn(mezcla(CINTA, Color.White, 0.12f)), tn(CINTA), tn(mezcla(CINTA, Color.Black, 0.2f))), r.top, r.bottom), r.topLeft, r.size, CornerRadius(1.5f * u))
            for (k in 0 until 3) listOf(r.left + (2.5f + 1.8f * k) * u, r.right - (2.5f + 1.8f * k) * u).forEach { x ->
                drawLine(Color.Black.copy(alpha = 0.3f), Offset(x, r.top + 2 * u), Offset(x, r.bottom - 2 * u), 0.6f * u)
            }
        }
    }

    // ---------------------------------------------------------------- el nicho

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        fun tn(x: Color) = t(x, oscuro)
        val rr = minOf(w, h) * RADIO
        val repisa = h * REPISA
        val nicho = Rect(0f, 0f, w, repisa)
        val formaNicho = Path().apply { addRoundRect(RoundRect(nicho, CornerRadius(rr), CornerRadius(rr), CornerRadius.Zero, CornerRadius.Zero)) }
        val k = c.n.coerceIn(0, 10)
        val piso = repisa - h * 0.025f
        if (c.vacio) {
            // Un nicho sin tema: la tapa falsa cerrada, que parece un panel de servicio cualquiera.
            drawPath(formaNicho, Brush.verticalGradient(listOf(tn(HUESO_LUZ), tn(HUESO), tn(mezcla(HUESO, HUESO_OSC, 0.5f))), 0f, repisa))
            clipPath(formaNicho) { grano(nicho, SOMBRA, 30, c.semilla) }
            drawPath(formaNicho, SOMBRA.copy(alpha = 0.45f), style = Stroke(1.2f * u))
            for (j in 0 until 4) {
                val y = repisa * (0.32f + 0.12f * j)
                drawRoundRect(tn(SOMBRA).copy(alpha = 0.6f), Offset(w * 0.3f, y), Size(w * 0.4f, 2 * u), CornerRadius(1 * u))
                drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.4f), Offset(w * 0.3f, y + 2.6f * u), Offset(w * 0.7f, y + 2.6f * u), 0.6f * u)
            }
            listOf(Offset(rr, rr * 0.9f), Offset(w - rr, rr * 0.9f), Offset(rr, repisa - rr * 0.7f), Offset(w - rr, repisa - rr * 0.7f)).forEachIndexed { i, o ->
                tornillo(o, 1.7f * u, 25f + 50f * i, oscuro)
            }
        } else clipPath(formaNicho) {
            // El fondo del nicho, el piso visto un poco desde arriba, el foco rojo de cuarto oscuro y las latas.
            // El fondo es la piedra de las ruinas, en sombra: el nicho está cavado en el muro.
            muro(nicho, true, h * 0.17f, c.semilla)
            drawRect(Brush.verticalGradient(listOf(HUECO.copy(alpha = if (oscuro) 0.35f else 0.15f), HUECO.copy(alpha = 0.75f)), 0f, repisa), Offset.Zero, nicho.size)
            drawRect(Brush.verticalGradient(listOf(tn(mezcla(HUESO_OSC, HUECO, 0.7f)), tn(mezcla(HUESO_OSC, HUECO, 0.45f))), repisa - h * 0.06f, repisa),
                Offset(0f, repisa - h * 0.06f), Size(w, h * 0.06f))
            if (!oscuro) mancha(Offset(w * 0.3f, 0f), w * 0.5f, HUESO_LUZ.copy(alpha = 0.12f))
            latas(c, piso, ab, oscuro)
            val foco = Offset(w * 0.6f, 3.5f * u)
            if (oscuro) mancha(foco, w * 0.55f, ROJO.copy(alpha = 0.2f))
            drawRect(t(BAQUELITA, oscuro), Offset(foco.x - 4 * u, 0f), Size(8 * u, 2.5f * u))
            if (oscuro) mancha(foco, 6 * u, ROJO.copy(alpha = 0.6f))
            drawCircle(if (oscuro) ROJO else mezcla(ROJO, Color.Black, 0.55f), 2.2f * u, foco)
            sombraInterior(nicho, w * 0.06f, 0.5f)
        }

        // El frente del nicho, de chapa hueso, con el canto de cromo de la repisa y la sombra que echa.
        val fr = Rect(0f, repisa, w, h)
        val formaFrente = Path().apply { addRoundRect(RoundRect(fr, CornerRadius.Zero, CornerRadius.Zero, CornerRadius(rr), CornerRadius(rr))) }
        drawPath(formaFrente, Brush.verticalGradient(listOf(tn(HUESO_LUZ), tn(HUESO), tn(mezcla(HUESO, HUESO_OSC, 0.7f))), fr.top, fr.bottom))
        clipPath(formaFrente) {
            drawRect(Brush.verticalGradient(listOf(SOMBRA.copy(alpha = 0.35f), Color.Transparent), repisa + 2 * u, repisa + 7 * u), Offset(0f, repisa + 2 * u), Size(w, 5 * u))
            grano(fr, SOMBRA, 34, c.semilla + 5)
            for (j in 0 until 3) {
                val x0 = w * (0.2f + 0.6f * azar(c.semilla, j + 210)); val y = repisa + (h - repisa) * (0.15f + 0.75f * azar(c.semilla, j + 230))
                drawLine(Color.White.copy(alpha = 0.35f), Offset(x0, y), Offset(x0 + w * 0.05f * (0.3f + azar(c.semilla, j + 250)), y + 1.5f * u * (azar(c.semilla, j + 270) - 0.5f)), 0.5f * u)
            }
        }
        drawRect(Brush.verticalGradient(listOf(tn(CROMO_LUZ), tn(CROMO_OSC)), repisa - 1 * u, repisa + 2.2f * u), Offset(0f, repisa - 1 * u), Size(w, 3.2f * u))
        // El piloto (ámbar si guarda algo, rojo si es lo sin clasificar) y su tornillo, y la rejilla de mentira a la derecha.
        val luz: Color? = if (c.vacio) null else if (c.sinTema) ROJO else if (c.n > 0) AMBAR else null
        val pl = Offset(w * 0.085f, h * 0.73f)
        if (oscuro && luz != null) mancha(pl, 14 * u, luz.copy(alpha = 0.45f))
        with(TrazosFrente) { piloto(pl, 2.4f * u, luz ?: AMBAR, luz != null) }
        tornillo(Offset(w * 0.085f, h * 0.89f), 1.6f * u, 40f + 90f * azar(c.semilla, 3), oscuro)
        for (j in 0 until 3) {
            val y = h * (0.68f + 0.085f * j)
            drawRoundRect(tn(SOMBRA).copy(alpha = 0.6f), Offset(w * 0.845f, y), Size(w * 0.11f, 1.8f * u), CornerRadius(0.9f * u))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.4f), Offset(w * 0.845f, y + 2.4f * u), Offset(w * 0.955f, y + 2.4f * u), 0.6f * u)
        }
        if (c.vacio) return

        // La etiqueta de cinta de papel con el tema.
        cinta(Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom), c.semilla, oscuro)
        // De los nichos más llenos cuelga la punta de la película, por encima del canto.
        if (k >= 3) {
            val a = Offset(w * 0.86f, piso - (k / 2 - 0.5f) * h * 0.085f)
            tiraPelicula(a, Offset(w * 0.98f, a.y), Offset(w * 0.97f, repisa + h * 0.02f), Offset(w * 0.9f, repisa + h * 0.24f), 5 * u,
                tn(mezcla(HUESO_OSC, HUECO, 0.4f)), oscuro)
        }

        // La tapa falsa, abierta sobre su bisagra de piano: por dentro, la rejilla y la marca de la célula rayada en la chapa.
        val dw = w * (0.17f - 0.1f * ab)
        val ai = h * 0.03f; val bi = repisa - h * 0.01f
        val ad = -h * 0.045f; val bd = repisa + h * 0.06f
        fun enPuerta(sx: Float, v: Float): Offset {
            val ya = ai + (bi - ai) * v; val yb = ad + (bd - ad) * v
            return Offset(dw * sx, ya + (yb - ya) * sx)
        }
        clipPath(formaNicho) {
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.4f * (1 - 0.5f * ab)), Color.Transparent), dw, dw + w * 0.08f), Offset(dw, 0f), Size(w * 0.08f, repisa))
        }
        val puerta = Path().apply { moveTo(0f, ai); lineTo(dw, ad); lineTo(dw, bd); lineTo(0f, bi); close() }
        drawPath(puerta, Brush.horizontalGradient(listOf(tn(HUESO_OSC), tn(mezcla(HUESO, HUESO_LUZ, 0.4f))), 0f, dw))
        clipPath(puerta) {
            grano(Rect(0f, ad, dw, bd), SOMBRA, 16, c.semilla + 9)
            for (j in 0 until 4) {
                val v = 0.16f + 0.075f * j
                val p0 = enPuerta(0.28f, v); val p1 = enPuerta(0.82f, v)
                drawLine(tn(SOMBRA).copy(alpha = 0.55f), p0, p1, 1.6f * u, StrokeCap.Round)
                drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.3f), Offset(p0.x, p0.y + 1.4f * u), Offset(p1.x, p1.y + 1.4f * u), 0.6f * u)
            }
            val pm = enPuerta(0.55f, 0.68f)
            scale(0.62f, 1f, pm) { with(TrazosFrente) { emblemaRed(pm, maxOf(3 * u, dw * 0.24f), tn(SOMBRA).copy(alpha = 0.75f), 0.9f * u) } }
            val pe = enPuerta(0.9f, 0.5f)
            drawRoundRect(tn(CROMO_OSC), Offset(pe.x - 1.2f * u, pe.y - 3 * u), Size(2.4f * u, 6 * u), CornerRadius(1 * u))
        }
        drawLine(tn(SOMBRA).copy(alpha = 0.7f), Offset(dw + 1 * u, ad + 1 * u), Offset(dw + 1 * u, bd - 1 * u), 2 * u)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.45f), Offset(dw - 0.5f * u, ad), Offset(dw - 0.5f * u, bd), 0.8f * u)
        drawLine(tn(CROMO), Offset(0f, ai), Offset(0f, bi), 3 * u)
        var yb = ai + 2 * u
        while (yb < bi) { drawLine(tn(CROMO_OSC), Offset(-1.5f * u, yb), Offset(1.5f * u, yb), 0.7f * u); yb += 5 * u }
    }

    override val rotulo = Rect(0.17f, 0.62f, 0.8f, 0.93f)
    override fun tintaRotulo(oscuro: Boolean) = PIZARRA
    override val letraRotulo: FontFamily = LetraExtra.chivoMono
    override val salida = 0f
    override val placa: Rect = Rect(0.33f, 0.16f, 0.67f, 0.56f)
    override fun tintaPlaca(oscuro: Boolean) = HUESO_LUZ

    // ---------------------------------------------------------------- el interior

    /** La tapa de la lata, boca arriba: el reborde de cromo, los anillos prensados y la etiqueta con su franja de color. */
    private fun DrawScope.tapaGrande(c: Offset, r: Float, oscuro: Boolean) {
        val u = density
        drawCircle(Color.Black.copy(alpha = 0.4f), r * 1.02f, Offset(c.x + 4 * u, c.y + 6 * u))
        drawCircle(Brush.linearGradient(listOf(t(CROMO_LUZ, oscuro), t(CROMO, oscuro), t(CROMO_OSC, oscuro)), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r)), r, c)
        drawCircle(t(mezcla(CROMO, CROMO_OSC, 0.5f), oscuro), r * 0.9f, c)
        listOf(0.74f, 0.56f).forEach { f ->
            drawCircle(Color.White.copy(alpha = 0.18f), r * f, c, style = Stroke(1.2f * u))
            drawCircle(Color.Black.copy(alpha = 0.18f), r * f - 1.2f * u, c, style = Stroke(0.8f * u))
        }
        rotate(-14f, c) {
            val e = Rect(c.x - r * 0.62f, c.y - r * 0.16f, c.x + r * 0.62f, c.y + r * 0.16f)
            drawRect(t(ETIQUETA, oscuro), e.topLeft, e.size)
            drawRect(t(QUEMADO, oscuro), e.topLeft, Size(e.width * 0.16f, e.height))
            for (k in 0 until 3) drawLine(t(PIZARRA, oscuro).copy(alpha = 0.55f), Offset(e.left + e.width * 0.24f, e.top + e.height * (0.3f + 0.2f * k)),
                Offset(e.left + e.width * (0.5f + 0.35f * azar(13, k)), e.top + e.height * (0.3f + 0.2f * k)), 1.2f * u)
        }
        drawArc(Color.White.copy(alpha = if (oscuro) 0.1f else 0.3f), 200f, 60f, false, Offset(c.x - r * 0.95f, c.y - r * 0.95f), Size(r * 1.9f, r * 1.9f), style = Stroke(2 * u, cap = StrokeCap.Round))
    }

    /** La lata abierta, vista desde arriba, con el rollo de película en su carrete de tres agujeros. */
    private fun DrawScope.lataAbierta(c: Offset, r: Float, oscuro: Boolean) {
        val u = density
        drawCircle(Color.Black.copy(alpha = 0.45f), r * 1.02f, Offset(c.x + 4 * u, c.y + 6 * u))
        drawCircle(Brush.linearGradient(listOf(t(CROMO_LUZ, oscuro), t(CROMO_OSC, oscuro)), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r)), r, c)
        drawCircle(t(mezcla(CROMO_OSC, HUECO, 0.4f), oscuro), r * 0.93f, c)
        val rollo = r * 0.86f
        drawCircle(t(PELICULA, oscuro), rollo, c)
        var k = 0
        var rv = rollo
        while (rv > r * 0.3f) {
            drawCircle(Color.Black.copy(alpha = 0.12f + 0.08f * azar(17, k)), rv, c, style = Stroke(0.6f * u))
            rv -= (1.6f + 1.4f * azar(18, k)) * u; k++
        }
        drawArc(AMBAR.copy(alpha = if (oscuro) 0.1f else 0.18f), 200f, 70f, false, Offset(c.x - rollo * 0.8f, c.y - rollo * 0.8f), Size(rollo * 1.6f, rollo * 1.6f), style = Stroke(rollo * 0.3f))
        drawCircle(t(CROMO, oscuro), r * 0.28f, c)
        for (j in 0 until 3) {
            val a = Math.toRadians(j * 120.0 - 90.0)
            drawCircle(t(PELICULA, oscuro), r * 0.07f, Offset(c.x + (r * 0.17f * cos(a)).toFloat(), c.y + (r * 0.17f * sin(a)).toFloat()))
        }
        drawCircle(HUECO, r * 0.05f, c)
    }

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun tn(x: Color) = t(x, oscuro)
        // El fondo del nicho: la piedra de las ruinas donde está cavado, en la penumbra, con su grano.
        muro(Rect(0f, 0f, w, h), true, 46f * u, 77)
        drawRect(Brush.verticalGradient(listOf(tn(FORRO).copy(alpha = if (oscuro) 0.45f else 0.35f), tn(FORRO).copy(alpha = 0.7f)), 0f, h))
        grano(Rect(0f, 0f, w, h), Color.Black, 260, 77)
        val pl = 14 * u; val pa = 12 * u; val pf = 24 * u
        val dentro = Rect(pl, pa, w - pl, h - pf)
        clipRect(dentro.left, dentro.top, dentro.right, dentro.bottom) {
            // La tapa boca arriba, la lata abierta con su rollo y la tira de película que sale del rollo y cruza el nicho.
            tapaGrande(Offset(w * 0.16f, h * 0.17f), w * 0.3f, oscuro)
            lataAbierta(Offset(w * 0.8f, h * 0.84f), w * 0.4f, oscuro)
            tiraPelicula(Offset(w * 0.52f, h * 0.76f), Offset(w * 0.12f, h * 0.66f), Offset(w * 0.95f, h * 0.4f), Offset(w * 0.6f, -h * 0.04f), 30 * u, tn(FORRO), oscuro)
            // La luz: de día, la de la sala desde arriba; de noche, el foco rojo de cuarto oscuro.
            if (oscuro) mancha(Offset(w / 2, 0f), w * 0.95f, ROJO.copy(alpha = 0.13f)) else mancha(Offset(w * 0.25f, 0f), w * 0.8f, HUESO_LUZ.copy(alpha = 0.06f))
        }
        // Las paredes del nicho, de chapa hueso: la del fondo, los costados y el frente, más grueso, con el canto de cromo.
        drawRect(Brush.verticalGradient(listOf(tn(HUESO), tn(HUESO_OSC)), 0f, pa), Offset.Zero, Size(w, pa))
        drawRect(Brush.horizontalGradient(listOf(tn(HUESO_OSC), tn(HUESO)), 0f, pl), Offset.Zero, Size(pl, h))
        drawRect(Brush.horizontalGradient(listOf(tn(HUESO), tn(HUESO_OSC)), w - pl, w), Offset(w - pl, 0f), Size(pl, h))
        drawRect(Brush.verticalGradient(listOf(tn(HUESO_LUZ), tn(HUESO), tn(HUESO_OSC)), h - pf, h), Offset(0f, h - pf), Size(w, pf))
        drawRect(Brush.verticalGradient(listOf(tn(CROMO_LUZ), tn(CROMO_OSC)), h - pf, h - pf + 3 * u), Offset(0f, h - pf), Size(w, 3 * u))
        grano(Rect(0f, h - pf, w, h), SOMBRA, 60, 79)
        sombraInterior(dentro, 20 * u, 0.45f)
        // La bisagra de piano en la pared de la izquierda, y tornillos en la de la derecha.
        val bis = pl * 0.5f
        drawRect(tn(CROMO), Offset(bis - 1.6f * u, pa), Size(3.2f * u, h - pa - pf))
        var y = pa + 3 * u
        while (y < h - pf) { drawLine(tn(CROMO_OSC), Offset(bis - 1.6f * u, y), Offset(bis + 1.6f * u, y), 0.7f * u); y += 6 * u }
        var ys = pa + 40 * u
        var n = 0
        while (ys < h - pf - 10 * u) { tornillo(Offset(w - pl * 0.5f, ys), 1.7f * u, 30f + 53f * n, oscuro); ys += 140 * u; n++ }
        // La placa de inventario remachada en la pared del fondo.
        val inv = Rect(w * 0.42f, 2.5f * u, w * 0.58f, pa - 2.5f * u)
        drawRect(tn(CROMO), inv.topLeft, inv.size)
        listOf(inv.left + 2.5f * u, inv.right - 2.5f * u).forEach { x -> drawCircle(tn(CROMO_OSC), 1 * u, Offset(x, inv.center.y)) }
        drawLine(tn(PIZARRA).copy(alpha = 0.5f), Offset(inv.left + 6 * u, inv.center.y), Offset(inv.right - 6 * u, inv.center.y), 0.8f * u)
        // En la pared del frente, por detrás: el casquillo del piloto, sus dos cables, sus grapas y el pasacables.
        val enchufe = Offset(w * 0.1f, h - pf * 0.5f)
        if (oscuro) mancha(enchufe, 16 * u, AMBAR.copy(alpha = 0.25f))
        drawCircle(tn(CROMO_OSC), 4.5f * u, enchufe)
        drawCircle(tn(CROMO), 3.2f * u, enchufe)
        drawCircle(BAQUELITA, 1.6f * u, enchufe)
        listOf(tn(BOTON) to 0f, tn(BAQUELITA) to 2.2f * u).forEach { (col, dy) ->
            val cable = Path().apply {
                moveTo(enchufe.x + 3 * u, enchufe.y - 1.2f * u + dy)
                cubicTo(w * 0.3f, h - pf * 0.85f + dy, w * 0.6f, h - pf * 0.2f + dy, w * 0.9f, h - pf * 0.55f + dy)
            }
            drawPath(cable, col, style = Stroke(1.6f * u, cap = StrokeCap.Round))
        }
        listOf(0.4f, 0.66f).forEach { f -> drawRoundRect(tn(CROMO), Offset(w * f - 1.5f * u, h - pf * 0.72f), Size(3 * u, pf * 0.4f), CornerRadius(1 * u)) }
        drawCircle(tn(BAQUELITA), 3.4f * u, Offset(w * 0.9f, h - pf * 0.5f))
        drawCircle(Color.Black, 1.8f * u, Offset(w * 0.9f, h - pf * 0.5f))
    }

    override fun tintaInterior(oscuro: Boolean) = HUESO_LUZ
    override val pared: Dp = 22.dp
    override val columnas = 2
    override val lineas = 4

    // ---------------------------------------------------------------- las fichas

    override fun forma(semilla: Int): Shape = RoundedCornerShape(2.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        val banda = 9 * u * g
        // La copia en papel del fotograma: se oscurece un poco hacia los bordes y lleva el marco fino de la imagen.
        drawRect(Brush.radialGradient(listOf(Color.Transparent, PIZARRA.copy(alpha = 0.08f)), Offset(w / 2, h / 2), maxOf(w, h) * 0.75f))
        drawRect(p.tinta.copy(alpha = 0.14f), Offset(3 * u * g, banda + 2.5f * u * g), Size(w - 6 * u * g, h - 2 * banda - 5 * u * g), style = Stroke(0.6f * u))
        // Las rayas finas que deja la película al correr por el proyector.
        for (k in 0 until 2) {
            val x = w * (0.15f + 0.7f * azar(semilla, k + 40))
            drawLine(p.tinta.copy(alpha = 0.05f), Offset(x, banda), Offset(x + 2 * u * (azar(semilla, k + 50) - 0.5f), h - banda), 0.6f * u)
        }
        // Las dos orillas de la película, con sus perforaciones (arriba dejan lugar a lo impreso en la orilla).
        listOf(0f, h - banda).forEachIndexed { i, y ->
            drawRect(PELICULA, Offset(0f, y), Size(w, banda))
            drawRect(Color.White.copy(alpha = 0.07f), Offset(0f, if (i == 0) y + banda - 1 * u else y), Size(w, 1 * u))
            val hw = 3.4f * u * g; val hh = 4f * u * g
            val hasta = if (i == 0) w * 0.68f else w - 2 * u
            var x = 3 * u * g
            while (x + hw < hasta) {
                drawRoundRect(p.color, Offset(x, y + (banda - hh) / 2), Size(hw, hh), CornerRadius(0.9f * u * g))
                x += 8 * u * g
            }
        }
        // Lo impreso en la orilla: la flecha y el número del fotograma en cifras de segmentos ámbar.
        val numero = Math.floorMod(semilla, 100)
        val ac = banda * 0.62f
        val cw = ac * 0.56f
        val x0 = w * 0.72f
        drawPath(Path().apply { moveTo(x0, banda / 2 - ac * 0.35f); lineTo(x0 + ac * 0.5f, banda / 2); lineTo(x0, banda / 2 + ac * 0.35f); close() }, AMBAR.copy(alpha = 0.85f))
        listOf(numero / 10, numero % 10).forEachIndexed { k, cifraN ->
            val l = x0 + ac * 0.9f + k * cw * 1.45f
            cifra(Rect(l, (banda - ac) / 2, l + cw, (banda + ac) / 2), cifraN, AMBAR.copy(alpha = 0.9f), AMBAR.copy(alpha = 0.1f), 0.9f * u * g)
        }
        val xa = x0 + ac * 0.9f + 2 * cw * 1.45f + 1.5f * u * g
        drawLine(AMBAR.copy(alpha = 0.6f), Offset(xa, banda / 2), Offset(xa + 3.5f * u * g, banda / 2), 0.9f * u * g)
    }

    override val margenes = MargenesFicha(17.dp, 11.dp, 22.dp, 28.dp, 22.dp, 30.dp)
    override val proporcionFicha = 1.12f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.8f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El piloto verde de canal limpio, con la palomita recortada: comprobado.
            "hecho" -> {
                drawCircle(CROMO_OSC, s * 0.46f, c)
                drawCircle(VERDE, s * 0.38f, c)
                drawPath(Path().apply { moveTo(c.x - s * 0.19f, c.y + s * 0.01f); lineTo(c.x - s * 0.05f, c.y + s * 0.15f); lineTo(c.x + s * 0.2f, c.y - s * 0.14f) },
                    p.color, style = Stroke(s * 0.1f, cap = StrokeCap.Round))
            }
            // La marca de la célula: es de los nuestros.
            "convence" -> with(TrazosFrente) { emblemaRed(c, s * 0.34f, QUEMADO, s * 0.08f) }
            // La pantallita de radar, todavía en rastreo.
            "piensa" -> {
                drawCircle(PANTALLA, s * 0.44f, c)
                drawCircle(RADAR.copy(alpha = 0.45f), s * 0.25f, c, style = Stroke(s * 0.05f))
                drawLine(RADAR, c, Offset(c.x + s * 0.3f, c.y - s * 0.26f), s * 0.07f, StrokeCap.Round)
                drawCircle(RADAR, s * 0.06f, Offset(c.x - s * 0.18f, c.y + s * 0.14f))
            }
            // La señal que va y viene: un círculo punteado.
            "duda" -> drawCircle(p.tenue, s * 0.34f, c, style = Stroke(s * 0.1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(s * 0.16f, s * 0.12f))))
            // El botón rojo de corte, con su barra.
            "desacuerdo" -> {
                drawRoundRect(CROMO_OSC, Offset(c.x - s * 0.44f, c.y - s * 0.44f), Size(s * 0.88f, s * 0.88f), CornerRadius(s * 0.16f))
                drawRoundRect(BOTON, Offset(c.x - s * 0.36f, c.y - s * 0.36f), Size(s * 0.72f, s * 0.72f), CornerRadius(s * 0.12f))
                drawLine(HUESO_LUZ, Offset(c.x - s * 0.2f, c.y), Offset(c.x + s * 0.2f, c.y), s * 0.11f, StrokeCap.Round)
            }
            else -> {}
        }
    }

    /** Una lata de microfilm vista desde arriba, con su etiqueta. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawCircle(CROMO_OSC, s * 0.46f, c)
        drawCircle(CROMO, s * 0.4f, c)
        drawCircle(CROMO_OSC, s * 0.26f, c, style = Stroke(s * 0.05f))
        drawRect(ETIQUETA, Offset(c.x - s * 0.3f, c.y - s * 0.1f), Size(s * 0.6f, s * 0.2f))
        drawRect(QUEMADO, Offset(c.x - s * 0.3f, c.y - s * 0.1f), Size(s * 0.12f, s * 0.2f))
    }

    override val desorden = 1f
}
