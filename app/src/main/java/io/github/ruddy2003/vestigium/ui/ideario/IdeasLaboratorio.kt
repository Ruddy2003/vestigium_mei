package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
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
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraContacto
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Laboratorio
// =====================================================================

/**
 * Laboratorio: el armario de muestras de un laboratorio de biología de un futuro cercano, limpio pero usado, contra una pared
 * de azulejo blanco con juntas grises y un zócalo de resina. Es un interior sin ventana: de día la luz es la de los tubos del
 * techo, fuera del cuadro arriba a la izquierda, y deja la sombra del armario en el azulejo. El armario es metálico, esmaltado
 * en blanco hueso, con el marco de acero, un panel con su pantallita y tres pilotos, la pintura saltada en las esquinas, una
 * cinta de riesgo gastada en el canto de la tapa, etiquetas viejas a medio despegar y patas con ruedas frenadas. Encima, el
 * microscopio binocular (base, brazo, platina con su pinza y su portaobjetos, revólver con tres objetivos, oculares) con su
 * cable suelto; a su izquierda, una piseta de plástico blando y una gradilla con tubos de alturas y líquidos distintos; a la
 * derecha, un Erlenmeyer violeta y un vaso de precipitados cian con su varilla, el cuaderno de laboratorio abierto apoyado en
 * el azulejo con un bolígrafo delante, un temporizador y un flexo de base pesada. En la pared, unas gafas de seguridad
 * colgadas de un gancho y una hoja de protocolo pegada con cinta, con la esquina levantada. Al pie, el contenedor amarillo de
 * residuos con el símbolo de riesgo biológico y dos cajas de poliestireno apiladas, cortados por los bordes. Cada tema es una
 * repisa de vidrio grueso de canto verdoso, y en ella, según la semilla, una caja de criogenia azul hielo con sus viales de
 * tapones de colores, una gradilla de tubos cónicos con tapón de rosca o una caja gris de portaobjetos con la tapa abatible
 * entreabierta: más viales, tubos o portaobjetos cuanto más guarda. Lo que no tiene tema va en una cubeta de acero con frascos
 * sin etiqueta y un rótulo de cinta de papel; una repisa sin tema está vacía, con la marca limpia donde hubo una caja y el
 * portaetiquetas sin tarjeta. Al abrir una, sale hacia delante y la de criogenia echa su vapor frío. De noche, el laboratorio
 * está cerrado y hundido en azul petróleo: alumbra el flexo que alguien dejó encendido sobre el cuaderno (halo cálido en el
 * azulejo y en la hoja, filos en el vidrio y en el metal), brillan la pantallita cian del temporizador y los pilotos verdes
 * del armario, y las esquinas lejanas se pierden. Por dentro, una bandeja blanca con su cuadrícula, gotas y una pipeta. Las
 * fichas son fichas de protocolo con su encabezado cian, casillas de verificación y un código tenue.
 */
object IdeasLaboratorio : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del laboratorio (el mueble)
    /** El azulejo blanco frío de la pared y sus juntas grises. */
    private val AZULEJO = Color(0xFFE5EBEE)
    private val JUNTA = Color(0xFFA9B5BC)
    /** La resina gris verdosa del zócalo y del suelo. */
    private val RESINA = Color(0xFF7D9192)
    /** El esmalte blanco hueso del armario, y su sombra. */
    private val HUESO = Color(0xFFEDE7D8)
    private val HUESO_OSC = Color(0xFFC2BAA7)
    /** El vidrio grueso de las repisas, visto de canto. */
    private val CANTO_VIDRIO = Color(0xFF8DC0AC)
    /** Los pilotos del armario. */
    private val PILOTO = Color(0xFF63D98E)
    /** La noche del laboratorio cerrado: azul petróleo muy oscuro. */
    private val NOCHE = Color(0xFF0A1A20)
    /** La bombilla del flexo. */
    private val FLEXO = Color(0xFFFFCF8A)

    // ---------------------------------------------------------------- de la paleta de siempre (también el interior y las fichas)
    private val BLANCO = Color(0xFFF2F6F8)
    private val BLANCO_OSC = Color(0xFFD6DEE4)
    private val CIAN = Color(0xFF0B8FA3)
    private val VIOLETA = Color(0xFF7A5AC8)
    private val ROSA = Color(0xFFE0557A)
    /** El acero del marco, las patas y los herrajes, y su sombra. */
    private val GRIS = Color(0xFF8A98A6)
    private val GRIS_OSC = Color(0xFF5A6876)
    private val VIDRIO = Color(0xFFCFE8EE)
    private val TINTA = Color(0xFF1B2733)
    private val HIELO = Color(0xFFA8D8F0)
    /** El ámbar de aviso: la cinta de riesgo y el contenedor de residuos. */
    private val AMARILLO = Color(0xFFF2C84A)
    private val TAPONES = listOf(Color(0xFF0B8FA3), Color(0xFF7A5AC8), Color(0xFFE0557A), Color(0xFFF2C84A), Color(0xFF4AB86A), Color(0xFFFFFFFF))

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.35f) else c

    /** Un color a [cerca] (0 a 1) del flexo: de día, tal cual; de noche, hundido en el azul petróleo y entibiado cerca de él. */
    private fun luz(c: Color, o: Boolean, cerca: Float): Color {
        if (!o) return c
        val k = cerca.coerceIn(0f, 1f)
        return mezcla(mezcla(c, NOCHE, 0.9f - 0.62f * Math.pow(k.toDouble(), 1.15).toFloat()), FLEXO, 0.24f * k)
    }

    /** La sombra: de día, violeta hondo, como en los muebles; de noche, el azul petróleo. */
    private fun sombra(o: Boolean, a: Float) = if (o) NOCHE.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo cálido del flexo, según lo cerca que esté. */
    private fun brillo(o: Boolean, a: Float, cerca: Float = 0f) =
        if (o) FLEXO.copy(alpha = (a * 0.9f * cerca).coerceIn(0f, 1f)) else luzMueble(a)

    /** La tarjeta de los rótulos: de día, blanca; de noche, gris azulado, para que la tinta se lea igual. */
    private fun tarjeta(o: Boolean) = if (o) mezcla(BLANCO, NOCHE, 0.3f) else BLANCO

    override val cols = 2
    override val proporcion = 1.25f
    override val corona = 0.62f
    override val base = 0.25f
    override val lado = 0.1f
    override val hueco = 0.05f

    /** En la corona: el fondo de la tapa del armario, su canto de frente y lo bajo del canto, en fracción de su alto. */
    private const val SUP = 0.86f
    private const val FRENTE = 0.9f
    private const val CANTO = 0.945f
    /** En la base: el fin del zócalo del armario, la línea de la pared con el suelo y donde pisan las ruedas, en fracción de su alto. */
    private const val ZOCALO = 0.36f
    private const val SUELO = 0.6f
    private const val PISAN = 0.86f
    /** Los costados del armario, en fracción del ancho. */
    private const val IZQ = 0.04f
    private const val DER = 0.96f
    /** Cuánto sale el contenido de un compartimento al abrirlo, en fracción de su alto. */
    private const val SALIDA = 0.07f

    // ---------------------------------------------------------------- ayudantes

    /** El brillo de un vidrio: un trazo claro a la izquierda y, de noche, un filo cálido a la derecha (el flexo). */
    private fun DrawScope.brilloVidrio(x0: Float, x1: Float, y0: Float, y1: Float, o: Boolean, k: Float) {
        val u = density
        drawLine(brillo(o, 0.75f, k + 0.2f), Offset(x0 + (x1 - x0) * 0.2f, y0), Offset(x0 + (x1 - x0) * 0.2f, y1), 1.3f * u, StrokeCap.Round)
        if (o) drawLine(FLEXO.copy(alpha = 0.6f * k), Offset(x1 - 1.2f * u, y0), Offset(x1 - 1.2f * u, y1), 0.9f * u, StrokeCap.Round)
    }

    /** Un cuerpo de vidrio: el vidrio casi transparente, su contorno fino y un poco del azul del vidrio grueso. */
    private fun DrawScope.vidrio(p: Path, o: Boolean, k: Float, a: Float = 0.4f) {
        drawPath(p, luz(VIDRIO, o, k).copy(alpha = a))
        drawPath(p, luz(GRIS_OSC, o, k).copy(alpha = 0.55f), style = Stroke(0.9f * density))
    }

    /** Un líquido dentro de [p], de [y] para abajo, con su menisco más claro. */
    private fun DrawScope.liquido(p: Path, r: Rect, y: Float, color: Color, o: Boolean, k: Float, alto: Float = 2.4f) {
        val u = density
        clipPath(p) {
            drawRect(luz(color, o, k).copy(alpha = 0.82f), Offset(r.left, y), Size(r.width, r.bottom - y))
            drawRect(luz(mezcla(color, Color.Black, 0.25f), o, k).copy(alpha = 0.35f), Offset(r.left + r.width * 0.62f, y), Size(r.width * 0.38f, r.bottom - y))
            drawOval(luz(mezcla(color, Color.White, 0.45f), o, k + 0.1f).copy(alpha = 0.9f), Offset(r.left, y - alto * u / 2), Size(r.width, alto * u))
        }
    }

    /** Las cifras de una pantalla de siete segmentos (sin letras). */
    private val SEGMENTOS = mapOf('0' to "abcdef", '1' to "bc", '2' to "abged", '3' to "abgcd", '4' to "fgbc", '5' to "afgcd", '6' to "afgedc",
        '7' to "abc", '8' to "abcdefg", '9' to "abfgcd", '-' to "g")

    private fun DrawScope.cifras(texto: String, x: Float, y: Float, a: Float, al: Float, col: Color) {
        val g = maxOf(al * 0.13f, 0.6f * density)
        val m = al / 2
        texto.forEachIndexed { i, ch ->
            val x0 = x + i * a * 1.35f
            SEGMENTOS[ch]?.forEach { s ->
                when (s) {
                    'a' -> drawRect(col, Offset(x0 + g, y), Size(a - 2 * g, g))
                    'b' -> drawRect(col, Offset(x0 + a - g, y + g), Size(g, m - g))
                    'c' -> drawRect(col, Offset(x0 + a - g, y + m), Size(g, m - g))
                    'd' -> drawRect(col, Offset(x0 + g, y + al - g), Size(a - 2 * g, g))
                    'e' -> drawRect(col, Offset(x0, y + m), Size(g, m - g))
                    'f' -> drawRect(col, Offset(x0, y + g), Size(g, m - g))
                    'g' -> drawRect(col, Offset(x0 + g, y + m - g / 2), Size(a - 2 * g, g))
                }
            }
        }
    }

    /** El símbolo genérico de riesgo biológico: tres medias lunas y un anillo, en [tinta] sobre [fondo]. */
    private fun DrawScope.riesgoBio(c: Offset, r: Float, tinta: Color, fondo: Color) {
        fun dir(g: Double) = Offset(cos(Math.toRadians(g)).toFloat(), sin(Math.toRadians(g)).toFloat())
        for (i in 0 until 3) drawCircle(tinta, r * 0.5f, c + dir(-90.0 + 120 * i) * (r * 0.42f))
        for (i in 0 until 3) drawCircle(fondo, r * 0.37f, c + dir(-90.0 + 120 * i) * (r * 0.58f))
        drawCircle(fondo, r * 0.15f, c)
        drawCircle(tinta, r * 0.36f, c, style = Stroke(r * 0.08f))
        for (i in 0 until 3) drawCircle(fondo, r * 0.08f, c + dir(-30.0 + 120 * i) * (r * 0.36f))
    }

    /** Una tira de cinta de papel con los extremos rotos, de [a] a [b] y de [ancho]. */
    private fun cinta(a: Offset, b: Offset, ancho: Float, semilla: Int): Path {
        val d = b - a; val l = d.getDistance().coerceAtLeast(1f)
        val n = Offset(-d.y / l, d.x / l) * (ancho / 2)
        val t = d / l
        return Path().apply {
            moveTo(a.x + n.x, a.y + n.y)
            lineTo(b.x + n.x, b.y + n.y)
            for (k in 1..3) { val f = k / 4f; val m = b + n * (1 - 2 * f) + t * (ancho * 0.12f * (azar(semilla, k) - 0.5f)); lineTo(m.x, m.y) }
            lineTo(b.x - n.x, b.y - n.y)
            lineTo(a.x - n.x, a.y - n.y)
            for (k in 1..3) { val f = k / 4f; val m = a - n * (1 - 2 * f) + t * (ancho * 0.12f * (azar(semilla + 5, k) - 0.5f)); lineTo(m.x, m.y) }
            close()
        }
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val o = oscuro
        val w = size.width; val alto = size.height
        val ch = d.corona.height
        val b = d.base; val hb = b.height
        val sup = ch * SUP; val frente = ch * FRENTE; val canto = ch * CANTO
        val pieA = sup + (frente - sup) * 0.3f
        val pieF = sup + (frente - sup) * 0.85f
        val suelo = b.top + hb * SUELO
        val pisan = b.top + hb * PISAN
        val zb = b.top + hb * ZOCALO
        val xL = w * IZQ; val xR = w * DER
        // El flexo, encima del armario a la derecha: de noche, la luz que manda.
        val boca = Offset(w * 0.735f, ch * 0.5f)
        val alcance = w * 0.8f
        fun calor(p: Offset) = if (!o) 0f else (1f - (p - boca).getDistance() / alcance).coerceIn(0f, 1f)
        fun c(col: Color, p: Offset) = luz(col, o, calor(p))

        // ---- la pared de azulejo
        azulejos(w, suelo, ch, o, boca, alcance)
        // ---- el zócalo de resina y el suelo
        val zs = suelo - hb * 0.1f
        drawRect(c(RESINA, Offset(w * 0.7f, zs)), Offset(0f, zs), Size(w, suelo - zs))
        drawRect(brillo(o, 0.35f, 0.3f), Offset(0f, zs), Size(w, 1.4f * u))
        drawRect(sombra(o, 0.25f), Offset(0f, suelo - 2f * u), Size(w, 2f * u))
        drawRect(Brush.verticalGradient(listOf(c(mezcla(RESINA, AZULEJO, 0.42f), Offset(w * 0.6f, suelo)), c(mezcla(RESINA, AZULEJO, 0.2f), Offset(w * 0.6f, alto))), suelo, alto),
            Offset(0f, suelo), Size(w, alto - suelo))
        pinceladas(Rect(0f, suelo, w, alto), if (o) NOCHE else RESINA, 1301, 16, u, 0.12f)
        // Lo gastado del zócalo: rozaduras de carros y de pies.
        for (k in 0 until 6) {
            val x = w * azar(1302, k); val y = zs + (suelo - zs) * (0.3f + 0.5f * azar(1303, k))
            drawLine(sombra(o, 0.18f), Offset(x, y), Offset(x + w * (0.02f + 0.04f * azar(1304, k)), y + 0.5f * u), (0.8f + azar(1305, k)) * u, StrokeCap.Round)
        }

        // ---- en la pared: las gafas colgadas y la hoja de protocolo
        gafas(Offset(w * 0.13f, ch * 0.12f), w * 0.16f, o, calor(Offset(w * 0.13f, ch * 0.25f)))
        protocolo(Rect(w * 0.575f, ch * 0.07f, w * 0.765f, ch * 0.43f), o, calor(Offset(w * 0.67f, ch * 0.3f)))

        // ---- la sombra del armario en el azulejo (a la derecha) y en el suelo
        val sombraPared = Path().apply {
            moveTo(xR, canto); lineTo(xR + w * 0.032f, canto + w * 0.03f); lineTo(xR + w * 0.032f, suelo); lineTo(xR, suelo); close()
        }
        drawPath(sombraPared, sombra(o, 0.26f))
        drawRect(sombra(o, 0.16f), Offset(w * 0.97f, frente + 2f * u), Size(w * 0.02f, w * 0.03f))
        drawRect(sombra(o, if (o) 0.6f else 0.3f), Offset(xL + 2f * u, zb), Size(xR - xL + w * 0.02f, suelo - zb))
        drawRect(sombra(o, if (o) 0.5f else 0.22f), Offset(xL + w * 0.02f, suelo), Size(xR - xL + w * 0.01f, (pisan - suelo) * 0.75f))
        drawRect(sombra(o, if (o) 0.3f else 0.12f), Offset(xL + w * 0.035f, suelo + (pisan - suelo) * 0.75f), Size(xR - xL, (pisan - suelo) * 0.4f))

        // ---- el cuerpo del armario
        armario(d, o, ::calor)

        // ---- la base: el zócalo del armario, las patas con sus ruedas frenadas
        val zocalo = Rect(xL, d.filas.last().bottom, xR, zb)
        drawRect(c(HUESO, Offset(w * 0.5f, zb)), zocalo.topLeft, zocalo.size)
        drawRect(Brush.horizontalGradient(listOf(brillo(o, 0.22f), Color.Transparent, sombra(o, 0.22f)), xL, xR), zocalo.topLeft, zocalo.size)
        val chapa = Rect(xL, zb - hb * 0.1f, xR, zb)
        drawRect(c(GRIS, Offset(w * 0.5f, zb)), chapa.topLeft, chapa.size)
        for (k in 0 until 7) drawLine(brillo(o, 0.18f, 0.1f).copy(alpha = if (o) 0.05f else 0.15f + 0.1f * azar(1310, k)),
            Offset(xL, chapa.top + chapa.height * (k + 0.5f) / 7f), Offset(xR, chapa.top + chapa.height * (k + 0.5f) / 7f), 0.6f * u)
        drawRect(brillo(o, 0.5f, 0.1f), Offset(xL, chapa.top), Size(xR - xL, 1f * u))
        drawRect(sombra(o, 0.4f), Offset(xL, zb - 1.2f * u), Size(xR - xL, 1.2f * u))
        // El cajón bajo, de chapa esmaltada, con su tirador de barra y su golpe en la esquina.
        val cajon = Rect(xL + w * 0.03f, zocalo.top + d.separacion * 0.35f, xR - w * 0.03f, chapa.top - 4f * u)
        if (cajon.height > 10f * u) {
            drawRect(sombra(o, 0.25f), Offset(cajon.left - 1.5f * u, cajon.top - 1.5f * u), Size(cajon.width + 3f * u, cajon.height + 3f * u))
            drawRect(c(mezcla(HUESO, BLANCO, 0.2f), Offset(w * 0.5f, cajon.top)), cajon.topLeft, cajon.size)
            drawRect(sombra(o, 0.12f), Offset(cajon.right - cajon.width * 0.18f, cajon.top), Size(cajon.width * 0.18f, cajon.height))
            bisel(cajon, brillo(o, 0.55f, 0.1f), sombra(o, 0.3f), 1.2f * u)
            pinceladas(cajon, if (o) NOCHE else HUESO_OSC, 1314, 8, u, 0.1f)
            val ty = cajon.top + 7f * u
            drawLine(sombra(o, 0.3f), Offset(w * 0.37f + 2f * u, ty + 3f * u), Offset(w * 0.63f + 2f * u, ty + 3f * u), 3f * u, StrokeCap.Round)
            drawLine(c(GRIS, Offset(w * 0.5f, ty)), Offset(w * 0.37f, ty), Offset(w * 0.63f, ty), 3f * u, StrokeCap.Round)
            drawLine(brillo(o, 0.7f, 0.1f), Offset(w * 0.37f, ty - 0.8f * u), Offset(w * 0.63f, ty - 0.8f * u), 0.8f * u, StrokeCap.Round)
            listOf(w * 0.37f, w * 0.63f).forEach { x -> drawRect(c(GRIS_OSC, Offset(x, ty)), Offset(x - 1.5f * u, ty - 1f * u), Size(3f * u, 6f * u)) }
            // Un golpe con la pintura saltada, abajo a la derecha, y una etiqueta de inventario.
            drawOval(c(GRIS_OSC, Offset(w * 0.8f, cajon.bottom)), Offset(cajon.right - w * 0.07f, cajon.bottom - 6f * u), Size(w * 0.03f, 3.5f * u))
            if (d.mas == 0) {
                val inv = Rect(cajon.left + w * 0.04f, cajon.top + cajon.height * 0.35f, cajon.left + w * 0.13f, cajon.top + cajon.height * 0.75f)
                drawRect(c(BLANCO, inv.center), inv.topLeft, inv.size)
                var bx = inv.left + 2f * u; var q = 0
                while (bx < inv.right - 2f * u) { val bw = (0.5f + azar(1316, q)) * u; drawRect(c(TINTA, inv.center).copy(alpha = 0.6f), Offset(bx, inv.top + inv.height * 0.25f), Size(bw, inv.height * 0.5f)); bx += bw + 0.8f * u; q++ }
            }
        }
        // Las rozaduras de la chapa: los carros que chocan.
        for (k in 0 until 4) {
            val x = xL + (xR - xL) * (0.1f + 0.8f * azar(1311, k))
            drawLine(sombra(o, 0.3f), Offset(x, chapa.top + chapa.height * 0.3f), Offset(x + w * 0.03f, chapa.top + chapa.height * (0.35f + 0.3f * azar(1312, k))), 0.8f * u, StrokeCap.Round)
        }
        listOf(xL + w * 0.05f, xR - w * 0.05f).forEachIndexed { i, x -> rueda(Offset(x, pisan), hb, o, i == 0) }
        // La placa con «y N más», un portaetiquetas de acero atornillado al zócalo.
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            drawRect(sombra(o, 0.3f), Offset(r.left + 2f * u, r.top + 2.5f * u), r.size)
            drawRoundRect(c(GRIS, r.center), r.topLeft, r.size, CornerRadius(2f * u))
            val dentro = Rect(r.left + 3f * u, r.top + 3f * u, r.right - 3f * u, r.bottom - 3f * u)
            drawRect(tarjeta(o), dentro.topLeft, dentro.size)
            drawRect(c(CIAN, r.center), dentro.topLeft, Size(dentro.width, 1.6f * u))
            drawRect(brillo(o, 0.5f, 0.2f), r.topLeft, Size(r.width, 1f * u))
            listOf(r.left + 1.6f * u, r.right - 1.6f * u).forEach { x -> drawCircle(c(GRIS_OSC, r.center), 1f * u, Offset(x, r.center.y)) }
        }

        // ---- la tapa del armario: el tablero visto un poco desde arriba y su canto
        val tapa = Rect(w * 0.028f, sup, w * 0.972f, frente)
        drawRect(c(mezcla(HUESO, BLANCO, 0.4f), Offset(w * 0.5f, sup)), tapa.topLeft, tapa.size)
        drawRect(Brush.horizontalGradient(listOf(brillo(o, 0.45f), Color.Transparent, sombra(o, 0.12f)), tapa.left, tapa.right), tapa.topLeft, tapa.size)
        drawRect(sombra(o, 0.3f), tapa.topLeft, Size(tapa.width, 1.6f * u))
        // Las marcas de uso del tablero: aros de vasos, una mancha de reactivo.
        drawOval(c(VIOLETA, Offset(w * 0.45f, pieA)).copy(alpha = 0.18f), Offset(w * 0.455f, sup + (frente - sup) * 0.35f), Size(w * 0.03f, (frente - sup) * 0.35f))
        drawOval(sombra(o, 0.12f), Offset(w * 0.6f, sup + (frente - sup) * 0.2f), Size(w * 0.05f, (frente - sup) * 0.5f), style = Stroke(0.7f * u))
        val cantoR = Rect(tapa.left, frente, tapa.right, canto)
        drawRect(c(HUESO_OSC, Offset(w * 0.5f, frente)), cantoR.topLeft, cantoR.size)
        drawRect(Brush.horizontalGradient(listOf(brillo(o, 0.3f), Color.Transparent, sombra(o, 0.2f)), cantoR.left, cantoR.right), cantoR.topLeft, cantoR.size)
        drawRect(brillo(o, 0.85f, calor(Offset(w * 0.75f, frente)) + 0.15f), Offset(cantoR.left, frente), Size(cantoR.width, 1.3f * u))
        drawRect(sombra(o, 0.35f), Offset(cantoR.left, canto - 1f * u), Size(cantoR.width, 1f * u))
        // La cinta de riesgo, gastada, en el canto de la izquierda.
        val cr = Rect(tapa.left + w * 0.01f, frente + 1.6f * u, tapa.left + w * 0.2f, canto - 1.2f * u)
        clipRect(cr.left, cr.top, cr.right, cr.bottom) {
            drawRect(c(AMARILLO, cr.center), cr.topLeft, cr.size)
            var x = cr.left - cr.height
            var i = 0
            while (x < cr.right) {
                drawPath(Path().apply { moveTo(x, cr.bottom); lineTo(x + cr.height * 0.9f, cr.top); lineTo(x + cr.height * 1.8f, cr.top); lineTo(x + cr.height * 0.9f, cr.bottom); close() },
                    c(TINTA, cr.center).copy(alpha = 0.85f))
                x += cr.height * 1.8f; i++
            }
            // Lo gastado: el esmalte que asoma donde la cinta se levantó.
            for (k in 0 until 5) {
                val gx = cr.left + cr.width * azar(1320, k)
                drawOval(c(HUESO_OSC, cr.center), Offset(gx, cr.top + cr.height * azar(1321, k) * 0.6f), Size(cr.width * (0.04f + 0.06f * azar(1322, k)), cr.height * 0.5f))
            }
        }
        drawRect(c(AMARILLO, cr.center).copy(alpha = 0.5f), Offset(cr.right, cr.top), Size(w * 0.012f, cr.height * 0.5f))
        // La sombra del canto sobre el frente.
        drawRect(sombra(o, 0.3f), Offset(xL, canto), Size(xR - xL, 2.2f * u))
        drawRect(sombra(o, 0.14f), Offset(xL, canto + 2.2f * u), Size(xR - xL, 2.8f * u))

        // ---- lo de encima: las sombras en la pared y los objetos, de atrás adelante
        val mx = w * 0.345f
        val hm = ch * 0.63f
        piseta(Offset(w * 0.075f, pieA), ch * 0.33f, w * 0.068f, o, calor(Offset(w * 0.075f, pieA - ch * 0.15f)))
        flexo(Offset(w * 0.925f, pieA), boca, ch, o)
        cuaderno(Rect(w * 0.64f, pieA - ch * 0.25f, w * 0.865f, pieA), o, ::calor)
        erlenmeyer(Offset(w * 0.535f, pieA), ch * 0.3f, w * 0.095f, o, calor(Offset(w * 0.535f, pieA - ch * 0.12f)))
        microscopio(Offset(mx, pieA + (pieF - pieA) * 0.4f), hm, w * 0.215f, o, ::calor)
        gradilla(Rect(w * 0.105f, pieF - ch * 0.12f, w * 0.26f, pieF), ch, o, calor(Offset(w * 0.18f, pieF - ch * 0.1f)))
        vaso(Offset(w * 0.605f, pieF), ch * 0.18f, w * 0.072f, o, calor(Offset(w * 0.605f, pieF - ch * 0.09f)))
        temporizador(Rect(w * 0.83f, pieF - ch * 0.115f, w * 0.925f, pieF), o, calor(Offset(w * 0.87f, pieF - ch * 0.06f)))
        boligrafo(Offset(w * 0.665f, pieF + 0.6f * u), Offset(w * 0.79f, pieF - (frente - sup) * 0.35f), o, calor(Offset(w * 0.72f, pieF)))

        // ---- al pie: el contenedor de residuos y las cajas de poliestireno, cortados por los bordes
        contenedor(Offset(w * 0.26f, alto - hb * 0.03f), w * 0.19f, hb * 0.62f, o)
        poliestireno(Offset(w * 0.95f, alto - hb * 0.02f), w * 0.24f, hb, o)

        // ---- la luz del flexo y de lo que brilla, por encima de todo lo que tiene cerca
        if (o) {
            mancha(Offset(w * 0.72f, ch * 0.66f), w * 0.5f, FLEXO.copy(alpha = 0.13f))
            mancha(Offset(w * 0.75f, ch * 0.78f), w * 0.2f, FLEXO.copy(alpha = 0.2f))
            mancha(Offset(w * 0.877f, pieF - ch * 0.065f), w * 0.08f, CIAN.copy(alpha = 0.32f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    /**
     * La pared de azulejo: el blanco frío con la mancha de luz de los tubos del techo arriba a la izquierda (de noche, el halo
     * del flexo), las juntas grises con su bisel, unas pocas piezas de otro lote, una rajada y una con la esquina saltada.
     */
    private fun DrawScope.azulejos(w: Float, suelo: Float, ch: Float, o: Boolean, boca: Offset, alcance: Float) {
        val u = density
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(AZULEJO, Color.White, 0.35f), AZULEJO, mezcla(AZULEJO, JUNTA, 0.4f)), 0f, w), Offset.Zero, Size(w, suelo))
            mancha(Offset(w * 0.12f, ch * 0.05f), w * 0.75f, luzMueble(0.5f))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombraMueble(0.12f)), ch, suelo), Offset(0f, ch), Size(w, suelo - ch))
        } else {
            val halo = Offset(boca.x - w * 0.02f, boca.y + ch * 0.12f)
            drawRect(Brush.radialGradient(listOf(luz(AZULEJO, true, 0.85f), luz(AZULEJO, true, 0.55f), luz(AZULEJO, true, 0.22f), mezcla(NOCHE, Color.Black, 0.25f)),
                halo, alcance * 0.85f), Offset.Zero, Size(w, suelo))
        }
        val lado = w / 10.4f
        val x0 = -lado * 0.35f
        // Las piezas de otro lote, un poco más frías o más cálidas.
        var fila = 0
        var y = 0f
        while (y < suelo) {
            var col = 0
            var x = x0
            while (x < w) {
                val a = azar(1250 + fila, col)
                if (a < 0.22f && !o) drawRect((if (a < 0.1f) Color.White else JUNTA).copy(alpha = if (o) 0.05f else 0.16f), Offset(x, y), Size(lado, minOf(lado, suelo - y)))
                // El vidriado: un brillo corto arriba a la izquierda de cada pieza.
                if (!o && y < ch) drawLine(luzMueble(0.45f), Offset(x + 2.5f * u, y + 2.5f * u), Offset(x + lado * (0.3f + 0.3f * a), y + 2.5f * u), 0.9f * u, StrokeCap.Round)
                x += lado; col++
            }
            y += lado; fila++
        }
        // Las juntas, con su bisel claro debajo.
        val junta = if (o) NOCHE.copy(alpha = 0.55f) else JUNTA
        y = lado
        while (y < suelo) {
            drawLine(junta, Offset(0f, y), Offset(w, y), 1.4f * u)
            if (!o) drawLine(luzMueble(0.4f), Offset(0f, y + 1.3f * u), Offset(w, y + 1.3f * u), 0.6f * u)
            y += lado
        }
        var x = x0 + lado
        while (x < w) {
            drawLine(junta, Offset(x, 0f), Offset(x, suelo), 1.4f * u)
            if (!o) drawLine(luzMueble(0.35f), Offset(x + 1.3f * u, 0f), Offset(x + 1.3f * u, suelo), 0.6f * u)
            x += lado
        }
        // Una pieza rajada arriba a la derecha y otra con la esquina saltada, junto al gancho de las gafas.
        val rx = x0 + lado * 9; val ry = lado * 0.0f
        drawPath(Path().apply { moveTo(rx + lado * 0.2f, ry + lado); lineTo(rx + lado * 0.35f, ry + lado * 0.7f); lineTo(rx + lado * 0.3f, ry + lado * 0.45f); lineTo(rx + lado * 0.5f, ry + lado * 0.2f) },
            if (o) NOCHE.copy(alpha = 0.6f) else sombraMueble(0.35f), style = Stroke(0.7f * u))
        val ex = x0 + lado * 3; val ey = lado * 2
        drawPath(Path().apply { moveTo(ex, ey); lineTo(ex + lado * 0.16f, ey); lineTo(ex, ey + lado * 0.12f); close() }, luz(JUNTA, o, 0.2f))
        // El roce y la mugre de abajo, junto al zócalo.
        pinceladas(Rect(0f, suelo - ch * 0.25f, w, suelo), if (o) NOCHE else JUNTA, 1260, 18, u, 0.12f)
    }

    /**
     * El cuerpo del armario: los costados de esmalte con su marco de acero, el panel de arriba con la pantallita y los pilotos,
     * el montante del centro, el canto verdoso de cada repisa de vidrio, la pintura saltada y las etiquetas viejas.
     */
    private fun DrawScope.armario(d: Disposicion, o: Boolean, calor: (Offset) -> Float) {
        val u = density
        val w = size.width
        val ch = d.corona.height
        val canto = ch * CANTO
        val xL = w * IZQ; val xR = w * DER
        val primero = d.huecos.first(); val ultimo = d.filas.last()
        fun c(col: Color, p: Offset) = luz(col, o, calor(p))
        val cuerpo = Rect(xL, canto, xR, ultimo.bottom)
        // El fondo del armario, detrás de los compartimentos (en sombra).
        drawRect(c(mezcla(HUESO_OSC, GRIS_OSC, 0.3f), Offset(w * 0.5f, cuerpo.center.y)), Offset(primero.left, primero.top), Size(d.huecos[cols - 1].right - primero.left, ultimo.bottom - primero.top))
        // El panel de arriba.
        val panel = Rect(xL, canto, xR, primero.top)
        drawRect(c(HUESO, Offset(w * 0.4f, canto)), panel.topLeft, panel.size)
        // Los costados.
        val izq = Rect(xL, canto, primero.left, ultimo.bottom)
        val der = Rect(d.huecos[cols - 1].right, canto, xR, ultimo.bottom)
        drawRect(c(HUESO, Offset(izq.center.x, canto)), izq.topLeft, izq.size)
        drawRect(c(mezcla(HUESO, HUESO_OSC, 0.45f), Offset(der.center.x, canto)), der.topLeft, der.size)
        // El montante del centro.
        val centro = Rect(d.huecos[0].right, primero.top, d.huecos[1].left, ultimo.bottom)
        drawRect(c(mezcla(HUESO, HUESO_OSC, 0.2f), centro.center), centro.topLeft, centro.size)
        bisel(centro, brillo(o, 0.5f, 0.2f), sombra(o, 0.3f), 1.2f * u)
        // Entre filas: el canto verdoso del vidrio de cada repisa y su soporte de acero.
        d.filas.forEachIndexed { i, f ->
            val siguiente = d.filas.getOrNull(i + 1)?.top ?: (f.bottom + d.separacion * 0.4f)
            if (siguiente > f.bottom + 1f) {
                drawRect(c(mezcla(HUESO_OSC, GRIS_OSC, 0.45f), Offset(w * 0.5f, f.bottom)), Offset(primero.left, f.bottom), Size(d.huecos[cols - 1].right - primero.left, siguiente - f.bottom))
                val g = minOf(d.separacion * 0.3f, 9f * u)
                val cv = c(CANTO_VIDRIO, Offset(w * 0.7f, f.bottom))
                drawRect(cv, Offset(primero.left - 2f * u, f.bottom), Size(d.huecos[cols - 1].right - primero.left + 4f * u, g))
                drawRect(mezcla(cv, Color.White, 0.5f).copy(alpha = if (o) 0.3f else 0.8f), Offset(primero.left - 2f * u, f.bottom), Size(d.huecos[cols - 1].right - primero.left + 4f * u, 1f * u))
                drawRect(mezcla(cv, NOCHE, 0.35f), Offset(primero.left - 2f * u, f.bottom + g * 0.62f), Size(d.huecos[cols - 1].right - primero.left + 4f * u, g * 0.38f))
                if (o) drawRect(FLEXO.copy(alpha = 0.12f), Offset(w * 0.55f, f.bottom), Size(d.huecos[cols - 1].right - w * 0.55f, 1.2f * u))
                // La sombra de la repisa en el fondo, y los soportes.
                drawRect(sombra(o, 0.35f), Offset(primero.left, f.bottom + g), Size(d.huecos[cols - 1].right - primero.left, 2.5f * u))
                listOf(primero.left + 3f * u, d.huecos[cols - 1].right - 8f * u, d.huecos[0].right - 7f * u, d.huecos[1].left + 2f * u).forEach { x ->
                    drawRect(c(GRIS, Offset(x, f.bottom)), Offset(x, f.bottom + g), Size(5f * u, minOf(4f * u, siguiente - f.bottom - g)))
                }
            }
        }
        // El marco de acero: los cantos de los costados.
        drawRect(c(GRIS, Offset(xL, canto)), Offset(xL, canto), Size(w * 0.01f, cuerpo.height))
        drawRect(brillo(o, 0.6f, 0.1f), Offset(xL, canto), Size(1f * u, cuerpo.height))
        drawRect(c(GRIS_OSC, Offset(xR, canto)), Offset(xR - w * 0.01f, canto), Size(w * 0.01f, cuerpo.height))
        listOf(izq, der).forEach { r ->
            drawRect(sombra(o, 0.25f), Offset(r.right - 1.2f * u, primero.top), Size(1.2f * u, cuerpo.bottom - primero.top))
            drawRect(brillo(o, 0.45f, 0.2f), Offset(r.left + w * 0.01f, canto), Size(1f * u, cuerpo.height))
        }
        drawRect(Brush.horizontalGradient(listOf(brillo(o, 0.15f), Color.Transparent, sombra(o, 0.18f)), xL, xR), panel.topLeft, panel.size)
        drawRect(sombra(o, 0.3f), Offset(primero.left - 1.5f * u, primero.top - 1.5f * u), Size(d.huecos[cols - 1].right - primero.left + 3f * u, 1.5f * u))

        // La pantallita y los pilotos, en el panel de arriba a la derecha.
        val pr = Rect(w * 0.64f, canto + panel.height * 0.24f, w * 0.88f, primero.top - panel.height * 0.22f)
        if (pr.height > 6f * u) {
            drawRoundRect(c(GRIS_OSC, pr.center), pr.topLeft, pr.size, CornerRadius(2f * u))
            drawRect(brillo(o, 0.4f, 0.3f), Offset(pr.left + 2f * u, pr.top), Size(pr.width - 4f * u, 0.8f * u))
            val lcd = Rect(pr.left + pr.width * 0.06f, pr.top + pr.height * 0.18f, pr.left + pr.width * 0.46f, pr.bottom - pr.height * 0.18f)
            drawRect(if (o) Color(0xFF071318) else mezcla(TINTA, CIAN, 0.2f), lcd.topLeft, lcd.size)
            val ac = lcd.height * 0.36f
            cifras("-80", lcd.left + lcd.width * 0.12f, lcd.top + lcd.height * 0.18f, ac, lcd.height * 0.64f, if (o) mezcla(CIAN, Color.White, 0.35f) else mezcla(CIAN, Color.White, 0.2f))
            // Tres pilotos: dos verdes encendidos y uno apagado.
            for (k in 0 until 3) {
                val p = Offset(pr.left + pr.width * (0.6f + 0.13f * k), pr.center.y)
                val enc = k < 2
                if (enc && o) mancha(p, pr.height * 0.9f, PILOTO.copy(alpha = 0.5f))
                drawCircle(if (enc) PILOTO else c(mezcla(GRIS_OSC, TINTA, 0.5f), p), pr.height * 0.17f, p)
                drawCircle(Color.White.copy(alpha = if (enc) 0.6f else 0.15f), pr.height * 0.06f, p - Offset(pr.height * 0.05f, pr.height * 0.05f))
            }
        }
        // Una etiqueta vieja en el panel, a medio despegar, y lo pegajoso de otra que ya se quitaron.
        val et = Rect(w * 0.14f, canto + panel.height * 0.28f, w * 0.29f, primero.top - panel.height * 0.24f)
        if (et.height > 6f * u) {
            drawRect(sombra(o, 0.2f), Offset(et.left + 1.5f * u, et.top + 1.5f * u), et.size)
            val esquina = et.height * 0.55f
            drawPath(Path().apply { moveTo(et.left, et.top); lineTo(et.right - esquina, et.top); lineTo(et.right, et.top + esquina); lineTo(et.right, et.bottom); lineTo(et.left, et.bottom); close() },
                c(mezcla(BLANCO, AMARILLO, 0.18f), et.center))
            drawLine(c(TINTA, et.center).copy(alpha = 0.45f), Offset(et.left + 3f * u, et.center.y - et.height * 0.12f), Offset(et.right - esquina - 2f * u, et.center.y - et.height * 0.12f), 0.8f * u)
            drawLine(c(TINTA, et.center).copy(alpha = 0.3f), Offset(et.left + 3f * u, et.center.y + et.height * 0.18f), Offset(et.left + et.width * 0.55f, et.center.y + et.height * 0.18f), 0.8f * u)
            // La esquina doblada hacia fuera, con su revés más oscuro y su sombra.
            drawPath(Path().apply { moveTo(et.right - esquina, et.top); lineTo(et.right + esquina * 0.25f, et.top - esquina * 0.35f); lineTo(et.right, et.top + esquina); close() },
                c(mezcla(BLANCO, HUESO_OSC, 0.5f), et.center))
            drawRect(c(HUESO_OSC, et.center).copy(alpha = 0.5f), Offset(w * 0.32f, et.top + 1f * u), Size(w * 0.07f, et.height * 0.7f))
        }
        // La pintura saltada en las esquinas y los golpes: el acero que asoma.
        val saltos = listOf(Offset(xL + w * 0.012f, canto + 3f * u), Offset(xR - w * 0.014f, ultimo.bottom - 6f * u), Offset(centro.left + 2f * u, centro.bottom - 9f * u),
            Offset(izq.right - 4f * u, izq.top + izq.height * 0.55f), Offset(der.left + 2f * u, der.top + der.height * 0.3f), Offset(w * 0.5f, canto + 2f * u))
        saltos.forEachIndexed { k, p ->
            val r = (1.6f + 2.2f * azar(1330, k)) * u
            val forma = Path().apply {
                for (j in 0 until 7) {
                    val a = j / 7f * 6.283f
                    val rr = r * (0.55f + 0.6f * azar(1331 + k, j))
                    val q = Offset(p.x + rr * cos(a) * 1.3f, p.y + rr * sin(a))
                    if (j == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
                }
                close()
            }
            drawPath(forma, c(GRIS_OSC, p))
            translate(-0.6f * u, -0.6f * u) { drawPath(forma, brillo(o, 0.5f, 0.1f), style = Stroke(0.5f * u)) }
        }
        // El canto de abajo del cuerpo, con su sombra.
        drawRect(sombra(o, 0.4f), Offset(xL, ultimo.bottom - 1f * u), Size(xR - xL, 1f * u))
    }

    /** Una pata con su rueda: la horquilla de acero, la rueda de goma y el pedal del freno, pisado. */
    private fun DrawScope.rueda(pie: Offset, hb: Float, o: Boolean, izquierda: Boolean) {
        val u = density
        val r = hb * 0.08f
        val c = Offset(pie.x, pie.y - r)
        val arriba = pie.y - hb * (PISAN - ZOCALO)
        sombraContacto(Offset(pie.x + 4f * u, pie.y + 1f * u), r * 3.4f, 4f * u, if (o) 0.5f else 0.38f)
        // El vástago y la placa.
        drawRect(luz(GRIS_OSC, o, 0.05f), Offset(pie.x - r * 0.9f, arriba), Size(r * 1.8f, r * 0.45f))
        drawRect(luz(GRIS, o, 0.05f), Offset(pie.x - r * 0.25f, arriba), Size(r * 0.5f, c.y - arriba - r * 0.6f))
        drawRect(brillo(o, 0.5f, 0.05f), Offset(pie.x - r * 0.25f, arriba), Size(0.8f * u, c.y - arriba - r * 0.6f))
        // La horquilla.
        drawPath(Path().apply {
            moveTo(c.x - r * 1.05f, c.y); lineTo(c.x - r * 0.7f, c.y - r * 1.4f); lineTo(c.x + r * 0.7f, c.y - r * 1.4f); lineTo(c.x + r * 1.05f, c.y)
            lineTo(c.x + r * 0.75f, c.y); lineTo(c.x + r * 0.5f, c.y - r * 1.05f); lineTo(c.x - r * 0.5f, c.y - r * 1.05f); lineTo(c.x - r * 0.75f, c.y); close()
        }, luz(GRIS, o, 0.05f))
        // La rueda.
        drawCircle(luz(mezcla(GRIS_OSC, TINTA, 0.55f), o, 0.03f), r, c)
        drawCircle(luz(GRIS, o, 0.05f), r * 0.38f, c)
        drawArc(brillo(o, 0.45f, 0.05f), 190f, 70f, false, Offset(c.x - r * 0.85f, c.y - r * 0.85f), Size(r * 1.7f, r * 1.7f), style = Stroke(1f * u))
        // El pedal del freno, amarillo y pisado hacia abajo.
        val s = if (izquierda) -1f else 1f
        drawPath(Path().apply {
            moveTo(c.x + s * r * 0.6f, c.y - r * 1.2f); lineTo(c.x + s * r * 2.1f, c.y - r * 0.55f); lineTo(c.x + s * r * 2.1f, c.y - r * 0.15f); lineTo(c.x + s * r * 0.6f, c.y - r * 0.8f); close()
        }, luz(AMARILLO, o, 0.05f))
        drawLine(sombra(o, 0.4f), Offset(c.x + s * r * 0.6f, c.y - r * 0.8f), Offset(c.x + s * r * 2.1f, c.y - r * 0.15f), 0.8f * u)
    }

    // ---------------------------------------------------------------- los objetos de encima

    /** La piseta: un frasco de plástico blando, con su tapón cian, el pitorro doblado y una abolladura de tanto apretarla. */
    private fun DrawScope.piseta(pie: Offset, alto: Float, ancho: Float, o: Boolean, k: Float) {
        val u = density
        val a = ancho / 2
        val cuerpo = Path().apply {
            moveTo(pie.x - a, pie.y - alto * 0.04f)
            lineTo(pie.x - a * 1.02f, pie.y - alto * 0.56f)
            cubicTo(pie.x - a, pie.y - alto * 0.7f, pie.x - a * 0.5f, pie.y - alto * 0.74f, pie.x - a * 0.38f, pie.y - alto * 0.76f)
            lineTo(pie.x + a * 0.38f, pie.y - alto * 0.76f)
            cubicTo(pie.x + a * 0.5f, pie.y - alto * 0.74f, pie.x + a, pie.y - alto * 0.7f, pie.x + a * 1.02f, pie.y - alto * 0.56f)
            // La abolladura del lado derecho.
            cubicTo(pie.x + a * 0.86f, pie.y - alto * 0.45f, pie.x + a * 0.86f, pie.y - alto * 0.3f, pie.x + a, pie.y - alto * 0.2f)
            lineTo(pie.x + a, pie.y - alto * 0.04f)
            cubicTo(pie.x + a, pie.y, pie.x - a, pie.y, pie.x - a, pie.y - alto * 0.04f)
            close()
        }
        // Su sombra en la pared.
        translate(ancho * 0.32f, alto * 0.04f) { drawPath(cuerpo, sombra(o, 0.16f)) }
        sombraContacto(Offset(pie.x + a * 0.3f, pie.y), ancho * 1.4f, 4f * u, if (o) 0.45f else 0.3f)
        val r = Rect(pie.x - a * 1.05f, pie.y - alto * 0.76f, pie.x + a * 1.05f, pie.y)
        drawPath(cuerpo, luz(mezcla(BLANCO, VIDRIO, 0.35f), o, k))
        // El agua que se ve a través del plástico, y el lado de sombra.
        clipPath(cuerpo) {
            drawRect(luz(mezcla(VIDRIO, CIAN, 0.12f), o, k).copy(alpha = 0.7f), Offset(r.left, pie.y - alto * 0.38f), Size(r.width, alto * 0.38f))
            drawLine(luz(mezcla(VIDRIO, Color.White, 0.5f), o, k), Offset(r.left, pie.y - alto * 0.38f), Offset(r.right, pie.y - alto * 0.38f), 1f * u)
            drawRect(sombra(o, 0.2f), Offset(pie.x + a * 0.35f, r.top), Size(a, r.height))
            // La marca a rotulador: un garabato que fue «etanol».
            drawPath(Path().apply {
                moveTo(pie.x - a * 0.6f, pie.y - alto * 0.52f)
                cubicTo(pie.x - a * 0.4f, pie.y - alto * 0.58f, pie.x - a * 0.2f, pie.y - alto * 0.46f, pie.x, pie.y - alto * 0.53f)
                cubicTo(pie.x + a * 0.2f, pie.y - alto * 0.58f, pie.x + a * 0.3f, pie.y - alto * 0.48f, pie.x + a * 0.55f, pie.y - alto * 0.54f)
            }, luz(VIOLETA, o, k).copy(alpha = 0.8f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        }
        drawPath(cuerpo, luz(GRIS, o, k).copy(alpha = 0.55f), style = Stroke(0.8f * u))
        brilloVidrio(r.left + a * 0.1f, r.right, pie.y - alto * 0.66f, pie.y - alto * 0.1f, o, k)
        // El tapón de rosca, con sus estrías.
        val tp = Rect(pie.x - a * 0.48f, pie.y - alto * 0.86f, pie.x + a * 0.48f, pie.y - alto * 0.75f)
        drawRoundRect(luz(CIAN, o, k), tp.topLeft, tp.size, CornerRadius(1.2f * u))
        for (j in 1 until 6) drawLine(luz(mezcla(CIAN, TINTA, 0.4f), o, k), Offset(tp.left + tp.width * j / 6f, tp.top + 1f * u), Offset(tp.left + tp.width * j / 6f, tp.bottom - 1f * u), 0.6f * u)
        drawRect(brillo(o, 0.5f, k), tp.topLeft, Size(tp.width * 0.3f, tp.height))
        // El pitorro: sube y se dobla hacia la izquierda.
        val tubo = Path().apply {
            moveTo(pie.x, tp.top)
            lineTo(pie.x, pie.y - alto * 0.94f)
            cubicTo(pie.x, pie.y - alto * 1.02f, pie.x - a * 0.4f, pie.y - alto * 1.03f, pie.x - a * 0.9f, pie.y - alto * 0.95f)
            lineTo(pie.x - a * 1.25f, pie.y - alto * 0.88f)
        }
        drawPath(tubo, luz(GRIS, o, k), style = Stroke(2.6f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(tubo, luz(mezcla(BLANCO, VIDRIO, 0.3f), o, k), style = Stroke(1.5f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    /** La gradilla de alambre con sus tubos de ensayo: alturas, líquidos y tapones distintos, uno ladeado. */
    private fun DrawScope.gradilla(r: Rect, ch: Float, o: Boolean, k: Float) {
        val u = density
        val placa = r.top + r.height * 0.28f
        val piso = r.bottom - r.height * 0.1f
        sombraContacto(Offset(r.center.x + r.width * 0.08f, r.bottom), r.width * 1.05f, 5f * u, if (o) 0.5f else 0.34f)
        // Las patas de atrás y el piso de alambre.
        val acero = luz(GRIS, o, k)
        drawLine(luz(GRIS_OSC, o, k), Offset(r.left + 3f * u, piso), Offset(r.right - 3f * u, piso), 1.6f * u)
        // Los tubos: (altura en fracción de la corona, nivel del líquido de 0 a 1, color o null, tapón o null, giro).
        val tubos = listOf(
            listOf(0.22f, 0.55f, VIOLETA, null, 0f), listOf(0.2f, 0f, null, null, 0f), listOf(0.17f, 0.4f, AMARILLO, ROSA, 0f),
            listOf(0.23f, 0.7f, CIAN, null, 0f), listOf(0.2f, 0.25f, ROSA, null, 9f), listOf(0.15f, 0.6f, mezcla(VIOLETA, CIAN, 0.4f), CIAN, 0f))
        val n = tubos.size
        val paso = (r.width - 8f * u) / n
        val tw = paso * 0.62f
        tubos.forEachIndexed { i, t ->
            val altoT = ch * (t[0] as Float)
            val nivel = t[1] as Float
            val col = t[2] as Color?
            val tapon = t[3] as Color?
            val giro = t[4] as Float
            val x = r.left + 4f * u + paso * (i + 0.5f)
            val fondo = piso - 1f * u
            rotate(giro, Offset(x, fondo)) {
                val cuerpo = Path().apply {
                    moveTo(x - tw / 2, fondo - altoT)
                    lineTo(x - tw / 2, fondo - tw / 2)
                    arcTo(Rect(x - tw / 2, fondo - tw, x + tw / 2, fondo), 180f, -180f, false)
                    lineTo(x + tw / 2, fondo - altoT)
                    close()
                }
                // La sombra del tubo en la pared, corrida a la derecha.
                translate(tw * 1.1f, tw * 0.3f) { drawPath(cuerpo, sombra(o, 0.1f)) }
                vidrio(cuerpo, o, k, 0.45f)
                if (col != null && nivel > 0f) liquido(cuerpo, Rect(x - tw / 2, fondo - altoT, x + tw / 2, fondo), fondo - altoT * nivel, col, o, k, 1.8f)
                brilloVidrio(x - tw / 2, x + tw / 2, fondo - altoT + 3f * u, fondo - 3f * u, o, k)
                // El labio.
                drawOval(luz(GRIS_OSC, o, k).copy(alpha = 0.6f), Offset(x - tw / 2 - 0.8f * u, fondo - altoT - 1.4f * u), Size(tw + 1.6f * u, 2.8f * u), style = Stroke(0.8f * u))
                if (tapon != null) {
                    drawRoundRect(luz(tapon, o, k), Offset(x - tw / 2 - 0.8f * u, fondo - altoT - 4.5f * u), Size(tw + 1.6f * u, 6f * u), CornerRadius(1.5f * u))
                    drawRect(brillo(o, 0.45f, k), Offset(x - tw / 2, fondo - altoT - 4f * u), Size(1.4f * u, 5f * u))
                }
            }
        }
        // La placa de arriba, con los agujeros, delante de los tubos.
        val pr = Rect(r.left, placa, r.right, placa + r.height * 0.09f)
        drawRect(acero, pr.topLeft, pr.size)
        drawRect(brillo(o, 0.6f, k), pr.topLeft, Size(pr.width, 1f * u))
        drawRect(sombra(o, 0.3f), Offset(pr.left, pr.bottom - 1f * u), Size(pr.width, 1f * u))
        // Las patas de los extremos.
        listOf(r.left, r.right - 2.2f * u).forEach { x ->
            drawRect(acero, Offset(x, placa), Size(2.2f * u, r.bottom - placa))
        }
        drawRect(sombra(o, 0.3f), Offset(r.right - 1f * u, placa), Size(1f * u, r.bottom - placa))
        drawLine(acero, Offset(r.left, r.bottom - 0.8f * u), Offset(r.right, r.bottom - 0.8f * u), 1.6f * u)
    }

    /**
     * El microscopio binocular, de perfil y de tres cuartos: la base pesada con su lámpara, el brazo curvo con los mandos de
     * enfoque, la platina negra con la pinza y un portaobjetos, el condensador, el revólver con tres objetivos de anillos de
     * colores, el cabezal y los dos oculares inclinados hacia quien mira. El cable cuelga por detrás de la tapa.
     */
    private fun DrawScope.microscopio(pie: Offset, H: Float, B: Float, o: Boolean, calor: (Offset) -> Float) {
        val u = density
        fun X(f: Float) = pie.x + f * B
        fun Y(f: Float) = pie.y - f * H
        val k = calor(Offset(X(0f), Y(0.5f)))
        val kd = calor(Offset(X(0.3f), Y(0.5f)))
        val esmalte = luz(HUESO, o, k)
        val esmalteOsc = luz(mezcla(HUESO, HUESO_OSC, 0.75f), o, k)
        val negro = luz(mezcla(GRIS_OSC, TINTA, 0.55f), o, k)
        // Las piezas grandes, para su sombra en la pared.
        val base = Path().apply {
            moveTo(X(-0.5f), Y(0f)); lineTo(X(-0.5f), Y(0.045f))
            cubicTo(X(-0.49f), Y(0.085f), X(-0.44f), Y(0.095f), X(-0.36f), Y(0.095f))
            lineTo(X(0.26f), Y(0.11f))
            cubicTo(X(0.36f), Y(0.115f), X(0.43f), Y(0.09f), X(0.44f), Y(0.05f))
            lineTo(X(0.44f), Y(0f)); close()
        }
        val brazo = Path().apply {
            moveTo(X(0.12f), Y(0.1f))
            cubicTo(X(0.15f), Y(0.3f), X(0.15f), Y(0.5f), X(0.04f), Y(0.68f))
            lineTo(X(0.2f), Y(0.8f))
            cubicTo(X(0.34f), Y(0.62f), X(0.38f), Y(0.36f), X(0.36f), Y(0.1f))
            close()
        }
        val cabezal = Path().apply {
            moveTo(X(-0.28f), Y(0.62f)); lineTo(X(-0.24f), Y(0.76f))
            cubicTo(X(-0.22f), Y(0.8f), X(-0.1f), Y(0.82f), X(0.05f), Y(0.82f))
            lineTo(X(0.22f), Y(0.82f))
            cubicTo(X(0.27f), Y(0.82f), X(0.28f), Y(0.78f), X(0.27f), Y(0.74f))
            lineTo(X(0.24f), Y(0.62f)); close()
        }
        val giroOc = -32f
        val pivote = Offset(X(-0.1f), Y(0.8f))
        val oculares = Path().apply {
            val g = Math.toRadians(giroOc.toDouble()); val cg = cos(g).toFloat(); val sg = sin(g).toFloat()
            listOf(Offset(-B * 0.11f, -H * 0.27f), Offset(B * 0.11f, -H * 0.27f), Offset(B * 0.11f, H * 0.02f), Offset(-B * 0.11f, H * 0.02f)).forEachIndexed { i, q ->
                val x = pivote.x + q.x * cg - q.y * sg; val y = pivote.y + q.x * sg + q.y * cg
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        val silueta = Path().apply { op(base, brazo, PathOperation.Union) }
        val silueta2 = Path().apply { op(silueta, cabezal, PathOperation.Union) }
        val silueta3 = Path().apply { op(silueta2, oculares, PathOperation.Union) }
        // La sombra en el azulejo, corrida a la derecha y un poco abajo; y la de contacto en la tapa.
        clipRect(0f, 0f, size.width, pie.y - 1f * u) { translate(B * 0.2f, H * 0.05f) { drawPath(silueta3, sombra(o, 0.17f)) } }
        sombraContacto(Offset(X(0.05f), pie.y + 1f * u), B * 1.15f, 6f * u, if (o) 0.55f else 0.36f)

        // El cable: sale de la base por detrás y se va por el fondo de la tapa.
        drawPath(Path().apply {
            moveTo(X(0.42f), Y(0.03f)); cubicTo(X(0.6f), Y(0.02f), X(0.62f), Y(-0.025f), X(0.78f), Y(-0.015f)); cubicTo(X(0.9f), Y(-0.01f), X(0.95f), Y(0.02f), X(1.02f), Y(0.035f))
        }, luz(TINTA, o, k).copy(alpha = 0.85f), style = Stroke(2f * u, cap = StrokeCap.Round))

        // Los oculares (detrás del cabezal).
        rotate(giroOc, pivote) {
            val px = pivote.x; val py = pivote.y
            // El prisma.
            drawRect(esmalte, Offset(px - B * 0.11f, py - H * 0.1f), Size(B * 0.22f, H * 0.12f))
            drawRect(sombra(o, 0.18f), Offset(px + B * 0.04f, py - H * 0.1f), Size(B * 0.07f, H * 0.12f))
            // Los dos tubos: el de atrás un poco corrido.
            listOf(Offset(B * 0.05f, -H * 0.012f) to 0.25f, Offset(-B * 0.035f, 0f) to 0f).forEach { (dx, osc) ->
                val tx = px + dx.x; val ty = py - H * 0.1f + dx.y
                drawRect(mezcla(luz(GRIS, o, k), luz(GRIS_OSC, o, k), osc), Offset(tx - B * 0.045f, ty - H * 0.1f), Size(B * 0.09f, H * 0.1f))
                drawRect(brillo(o, 0.5f, k), Offset(tx - B * 0.04f, ty - H * 0.1f), Size(1.2f * u, H * 0.1f))
                drawRoundRect(mezcla(negro, NOCHE, osc * 0.6f), Offset(tx - B * 0.055f, ty - H * 0.155f), Size(B * 0.11f, H * 0.06f), CornerRadius(2f * u))
                drawRect(brillo(o, 0.3f, k), Offset(tx - B * 0.05f, ty - H * 0.155f), Size(B * 0.1f, 1f * u))
            }
        }

        // La base pesada.
        drawPath(base, esmalte)
        clipPath(base) {
            drawRect(sombra(o, 0.22f), Offset(X(0.2f), Y(0.12f)), Size(B * 0.3f, H * 0.12f))
            drawRect(sombra(o, 0.28f), Offset(X(-0.5f), Y(0.025f)), Size(B, H * 0.03f))
            drawRect(brillo(o, 0.6f, k), Offset(X(-0.48f), Y(0.098f)), Size(B * 0.7f, 1.2f * u))
        }
        drawPath(base, sombra(o, 0.25f), style = Stroke(0.6f * u))
        // La lámpara de la base, bajo la platina.
        val lampara = Offset(X(-0.12f), Y(0.1f))
        drawOval(negro, Offset(lampara.x - B * 0.075f, lampara.y - H * 0.014f), Size(B * 0.15f, H * 0.028f))
        drawOval(luz(mezcla(VIDRIO, Color.White, 0.4f), o, k), Offset(lampara.x - B * 0.05f, lampara.y - H * 0.01f), Size(B * 0.1f, H * 0.018f))

        // El brazo.
        drawPath(brazo, esmalte)
        clipPath(brazo) {
            drawRect(sombra(o, 0.24f), Offset(X(0.26f), Y(0.82f)), Size(B * 0.2f, H * 0.75f))
            drawRect(brillo(o, 0.4f, k), Offset(X(0.1f), Y(0.7f)), Size(1.4f * u, H * 0.58f))
            if (o) drawRect(FLEXO.copy(alpha = 0.28f * kd), Offset(X(0.31f), Y(0.82f)), Size(B * 0.1f, H * 0.75f))
        }
        // Los mandos de enfoque, coaxiales: el grande con su moleteado y el fino delante.
        val mando = Offset(X(0.25f), Y(0.27f))
        drawCircle(sombra(o, 0.25f), B * 0.1f, mando + Offset(2f * u, 2f * u))
        drawCircle(negro, B * 0.095f, mando)
        for (j in 0 until 10) {
            val a = j / 10f * 6.283f
            drawLine(luz(GRIS, o, k).copy(alpha = 0.6f), mando + Offset(cos(a), sin(a)) * (B * 0.065f), mando + Offset(cos(a), sin(a)) * (B * 0.09f), 0.6f * u)
        }
        drawCircle(luz(GRIS, o, k), B * 0.05f, mando - Offset(B * 0.02f, 0f))
        drawCircle(brillo(o, 0.6f, k), B * 0.018f, mando - Offset(B * 0.04f, B * 0.02f))

        // El condensador, bajo la platina.
        drawRect(negro, Offset(X(-0.18f), Y(0.36f)), Size(B * 0.12f, H * 0.08f))
        drawRect(brillo(o, 0.4f, k), Offset(X(-0.18f), Y(0.36f)), Size(1.2f * u, H * 0.08f))
        drawLine(luz(GRIS, o, k), Offset(X(-0.06f), Y(0.3f)), Offset(X(0.02f), Y(0.27f)), 1.4f * u, StrokeCap.Round)
        // La platina: el tablero negro visto un poco desde arriba y su canto.
        val pl0 = X(-0.44f); val pl1 = X(0.2f)
        drawRect(negro, Offset(pl0, Y(0.4f)), Size(pl1 - pl0, H * 0.035f))
        drawRect(luz(mezcla(GRIS_OSC, GRIS, 0.35f), o, k), Offset(pl0, Y(0.425f)), Size(pl1 - pl0, H * 0.025f))
        drawRect(brillo(o, 0.4f, k), Offset(pl0, Y(0.425f)), Size(pl1 - pl0, 1f * u))
        // Los mandos de la platina, colgando a la derecha.
        drawRect(negro, Offset(X(0.13f), Y(0.365f)), Size(B * 0.035f, H * 0.09f))
        drawRect(luz(GRIS, o, k), Offset(X(0.12f), Y(0.3f)), Size(B * 0.055f, H * 0.025f))
        // El portaobjetos con su muestra teñida, y la pinza que lo sujeta.
        drawRect(luz(mezcla(VIDRIO, Color.White, 0.3f), o, k).copy(alpha = 0.9f), Offset(X(-0.33f), Y(0.432f)), Size(B * 0.36f, H * 0.014f))
        drawRect(luz(Color.White, o, k).copy(alpha = 0.85f), Offset(X(-0.33f), Y(0.432f)), Size(B * 0.08f, H * 0.014f))
        drawOval(luz(ROSA, o, k).copy(alpha = 0.85f), Offset(X(-0.14f), Y(0.43f)), Size(B * 0.06f, H * 0.009f))
        drawPath(Path().apply { moveTo(X(0.16f), Y(0.44f)); cubicTo(X(0.08f), Y(0.46f), X(-0.02f), Y(0.45f), X(-0.07f), Y(0.437f)) },
            luz(GRIS, o, k), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawCircle(luz(GRIS, o, k), 2f * u, Offset(X(0.16f), Y(0.44f)))

        // El cabezal.
        drawPath(cabezal, esmalte)
        clipPath(cabezal) {
            drawRect(sombra(o, 0.2f), Offset(X(0.12f), Y(0.84f)), Size(B * 0.2f, H * 0.24f))
            drawRect(sombra(o, 0.25f), Offset(X(-0.3f), Y(0.645f)), Size(B * 0.6f, H * 0.025f))
            drawRect(brillo(o, 0.55f, k), Offset(X(-0.24f), Y(0.82f)), Size(B * 0.4f, 1.3f * u))
            if (o) drawRect(FLEXO.copy(alpha = 0.3f * kd), Offset(X(0.2f), Y(0.84f)), Size(B * 0.1f, H * 0.24f))
        }
        drawPath(cabezal, sombra(o, 0.22f), style = Stroke(0.6f * u))
        // Una tira de cinta con el número del aparato, en el cabezal.
        drawRect(luz(mezcla(BLANCO, AMARILLO, 0.2f), o, k), Offset(X(-0.18f), Y(0.74f)), Size(B * 0.16f, H * 0.04f))
        drawLine(luz(TINTA, o, k).copy(alpha = 0.6f), Offset(X(-0.16f), Y(0.72f)), Offset(X(-0.05f), Y(0.72f)), 0.9f * u)

        // El revólver y los tres objetivos.
        val rev = Offset(X(-0.12f), Y(0.61f))
        drawOval(luz(GRIS, o, k), Offset(rev.x - B * 0.18f, rev.y - H * 0.035f), Size(B * 0.36f, H * 0.07f))
        drawArc(brillo(o, 0.5f, k), 180f, 90f, false, Offset(rev.x - B * 0.14f, rev.y - H * 0.028f), Size(B * 0.28f, H * 0.056f), style = Stroke(1f * u))
        listOf(Triple(30f, 0.1f, AMARILLO), Triple(-32f, 0.11f, VIOLETA), Triple(0f, 0.14f, CIAN)).forEach { (g, l, anillo) ->
            rotate(g, rev) {
                val ow = B * 0.095f
                val p = Path().apply {
                    moveTo(rev.x - ow / 2, rev.y); lineTo(rev.x + ow / 2, rev.y)
                    lineTo(rev.x + ow * 0.36f, rev.y + H * l); lineTo(rev.x - ow * 0.36f, rev.y + H * l); close()
                }
                drawPath(p, luz(if (g == 0f) GRIS else mezcla(GRIS, GRIS_OSC, 0.4f), o, k))
                drawRect(luz(anillo, o, k), Offset(rev.x - ow * 0.44f, rev.y + H * l * 0.62f), Size(ow * 0.88f, H * 0.012f))
                drawRect(brillo(o, 0.5f, k), Offset(rev.x - ow * 0.38f, rev.y + 1f * u), Size(1.2f * u, H * l - 2f * u))
                drawRect(negro, Offset(rev.x - ow * 0.36f, rev.y + H * l - 1.5f * u), Size(ow * 0.72f, 1.5f * u))
            }
        }
    }

    /** El Erlenmeyer con su líquido violeta, sus marcas de graduación y el brillo del vidrio. */
    private fun DrawScope.erlenmeyer(pie: Offset, alto: Float, ancho: Float, o: Boolean, k: Float) {
        val u = density
        val a = ancho / 2
        val cuello = a * 0.24f
        val p = Path().apply {
            moveTo(pie.x - a, pie.y - 2f * u)
            cubicTo(pie.x - a, pie.y, pie.x - a * 0.9f, pie.y, pie.x - a * 0.85f, pie.y)
            lineTo(pie.x + a * 0.85f, pie.y)
            cubicTo(pie.x + a * 0.9f, pie.y, pie.x + a, pie.y, pie.x + a, pie.y - 2f * u)
            lineTo(pie.x + cuello, pie.y - alto * 0.66f)
            lineTo(pie.x + cuello, pie.y - alto * 0.96f)
            lineTo(pie.x + cuello * 1.35f, pie.y - alto)
            lineTo(pie.x - cuello * 1.35f, pie.y - alto)
            lineTo(pie.x - cuello, pie.y - alto * 0.96f)
            lineTo(pie.x - cuello, pie.y - alto * 0.66f)
            close()
        }
        clipRect(0f, 0f, size.width, pie.y - 1f * u) { translate(ancho * 0.28f, alto * 0.05f) { drawPath(p, sombra(o, 0.12f)) } }
        sombraContacto(Offset(pie.x + a * 0.2f, pie.y), ancho * 1.2f, 4f * u, if (o) 0.45f else 0.3f)
        vidrio(p, o, k, 0.38f)
        val r = Rect(pie.x - a, pie.y - alto, pie.x + a, pie.y)
        liquido(p, r, pie.y - alto * 0.32f, VIOLETA, o, k, 3.2f)
        // Las marcas blancas de graduación, a la izquierda.
        for (j in 0 until 4) {
            val y = pie.y - alto * (0.15f + 0.1f * j)
            val xi = pie.x - a + (a - cuello) * ((pie.y - y) / (alto * 0.66f)) + 2f * u
            drawLine(luz(Color.White, o, k).copy(alpha = 0.75f), Offset(xi, y), Offset(xi + a * (if (j % 2 == 0) 0.3f else 0.18f), y), 0.8f * u)
        }
        // El brillo: un trazo largo en la falda izquierda y otro en el cuello.
        drawLine(brillo(o, 0.75f, k + 0.2f), Offset(pie.x - a * 0.62f, pie.y - alto * 0.12f), Offset(pie.x - cuello * 0.6f, pie.y - alto * 0.6f), 1.5f * u, StrokeCap.Round)
        drawLine(brillo(o, 0.6f, k + 0.2f), Offset(pie.x - cuello * 0.5f, pie.y - alto * 0.7f), Offset(pie.x - cuello * 0.5f, pie.y - alto * 0.94f), 1.1f * u, StrokeCap.Round)
        if (o) drawLine(FLEXO.copy(alpha = 0.7f * k), Offset(pie.x + a * 0.8f, pie.y - alto * 0.05f), Offset(pie.x + cuello, pie.y - alto * 0.62f), 1f * u, StrokeCap.Round)
        // Un tapón de algodón, gastado.
        drawRoundRect(luz(mezcla(BLANCO, HUESO, 0.5f), o, k), Offset(pie.x - cuello * 1.1f, pie.y - alto * 1.07f), Size(cuello * 2.2f, alto * 0.11f), CornerRadius(cuello))
        drawRect(sombra(o, 0.15f), Offset(pie.x + cuello * 0.2f, pie.y - alto * 1.06f), Size(cuello * 0.9f, alto * 0.09f))
    }

    /** El vaso de precipitados: el pico, la graduación, el líquido cian con su menisco y una varilla de vidrio apoyada adentro. */
    private fun DrawScope.vaso(pie: Offset, alto: Float, ancho: Float, o: Boolean, k: Float) {
        val u = density
        val a = ancho / 2
        val p = Path().apply {
            moveTo(pie.x - a, pie.y - alto)
            lineTo(pie.x - a, pie.y - 2f * u)
            cubicTo(pie.x - a, pie.y, pie.x + a, pie.y, pie.x + a, pie.y - 2f * u)
            lineTo(pie.x + a, pie.y - alto)
            close()
        }
        val r = Rect(pie.x - a, pie.y - alto, pie.x + a, pie.y)
        sombraContacto(Offset(pie.x + a * 0.25f, pie.y + 1f * u), ancho * 1.3f, 4f * u, if (o) 0.5f else 0.32f)
        // La varilla, por detrás del vidrio del frente.
        val v0 = Offset(pie.x - a * 0.5f, pie.y - alto * 0.08f); val v1 = Offset(pie.x + a * 1.25f, pie.y - alto * 1.45f)
        drawLine(luz(VIDRIO, o, k), v0, v1, 2.2f * u, StrokeCap.Round)
        drawLine(brillo(o, 0.7f, k + 0.2f), v0 + Offset(-0.6f * u, 0f), v1 + Offset(-0.6f * u, 0f), 0.6f * u, StrokeCap.Round)
        vidrio(p, o, k, 0.4f)
        liquido(p, r, pie.y - alto * 0.52f, CIAN, o, k, 3f)
        // La graduación.
        for (j in 0 until 5) {
            val y = pie.y - alto * (0.18f + 0.14f * j)
            drawLine(luz(Color.White, o, k).copy(alpha = 0.7f), Offset(pie.x - a * 0.55f, y), Offset(pie.x - a * (if (j % 2 == 0) 0.15f else 0.35f), y), 0.7f * u)
        }
        // El labio con su pico.
        drawOval(luz(GRIS_OSC, o, k).copy(alpha = 0.6f), Offset(pie.x - a - 0.8f * u, pie.y - alto - 2f * u), Size(ancho + 1.6f * u, 4f * u), style = Stroke(0.9f * u))
        drawPath(Path().apply { moveTo(pie.x - a - 0.5f * u, pie.y - alto); lineTo(pie.x - a - 3.5f * u, pie.y - alto - 2.5f * u); lineTo(pie.x - a + 2.5f * u, pie.y - alto - 0.5f * u) },
            luz(GRIS_OSC, o, k).copy(alpha = 0.6f), style = Stroke(0.9f * u))
        brilloVidrio(r.left, r.right, r.top + 3f * u, r.bottom - 3f * u, o, k)
    }

    /**
     * El cuaderno de laboratorio abierto, apoyado en el azulejo: las tapas violeta que asoman, las dos páginas cuadriculadas con
     * la letra, un esquema con flechas, el lomo hundido y una pestaña amarilla.
     */
    private fun DrawScope.cuaderno(r: Rect, o: Boolean, calor: (Offset) -> Float) {
        val u = density
        val k = calor(r.center)
        val incl = r.width * 0.025f
        val tapa = Path().apply {
            moveTo(r.left - 2f * u, r.bottom); lineTo(r.left + incl - 2f * u, r.top - 2f * u); lineTo(r.right - incl + 2f * u, r.top - 2f * u); lineTo(r.right + 2f * u, r.bottom); close()
        }
        // La sombra en la pared (se apoya: poca) y en la tapa del armario.
        translate(r.width * 0.06f, 2f * u) { drawPath(tapa, sombra(o, 0.2f)) }
        drawPath(tapa, luz(mezcla(VIOLETA, TINTA, 0.35f), o, k))
        drawPath(tapa, sombra(o, 0.25f), style = Stroke(0.6f * u))
        // La pestaña amarilla que asoma arriba.
        drawRect(luz(AMARILLO, o, k), Offset(r.right - r.width * 0.2f, r.top - r.height * 0.07f), Size(r.width * 0.05f, r.height * 0.1f))
        // Las dos páginas, un poco curvas arriba.
        val mid = r.center.x
        listOf(true, false).forEach { izquierda ->
            val x0 = if (izquierda) r.left + incl + 1f * u else mid
            val x1 = if (izquierda) mid else r.right - incl - 1f * u
            val pg = Path().apply {
                moveTo(if (izquierda) r.left + 1f * u else mid, r.bottom - 1.5f * u)
                lineTo(if (izquierda) x0 else mid, r.top + 2f * u + (if (izquierda) 0f else r.height * 0.05f))
                cubicTo(x0 + (x1 - x0) * 0.3f, r.top - r.height * 0.02f, x0 + (x1 - x0) * 0.7f, r.top - r.height * 0.02f, x1, r.top + 2f * u + (if (izquierda) r.height * 0.05f else 0f))
                lineTo(if (izquierda) mid else r.right - 1f * u, r.bottom - 1.5f * u)
                close()
            }
            drawPath(pg, luz(mezcla(BLANCO, HUESO, 0.4f), o, calor(Offset((x0 + x1) / 2, r.center.y)) + 0.1f))
            clipPath(pg) {
                // La cuadrícula.
                val paso = r.height / 11f
                var y = r.top + paso * 1.2f
                while (y < r.bottom) { drawLine(luz(CIAN, o, k).copy(alpha = 0.18f), Offset(x0, y), Offset(x1, y), 0.5f * u); y += paso }
                var x = x0 + paso * 0.5f
                while (x < x1) { drawLine(luz(CIAN, o, k).copy(alpha = 0.12f), Offset(x, r.top), Offset(x, r.bottom), 0.5f * u); x += paso }
                // La letra: renglones de garabato, más cortos al final de un párrafo.
                val tinta = luz(TINTA, o, k).copy(alpha = 0.6f)
                val renglones = if (izquierda) 8 else 3
                for (j in 0 until renglones) {
                    val yy = r.top + paso * (1.9f + j)
                    val largo = (x1 - x0) * (if (j % 4 == 3) 0.45f else 0.78f + 0.12f * azar(1340 + (if (izquierda) 0 else 1), j))
                    val trazo = Path().apply {
                        moveTo(x0 + (x1 - x0) * 0.1f, yy)
                        var xx = x0 + (x1 - x0) * 0.1f; var q = 0
                        while (xx < x0 + (x1 - x0) * 0.1f + largo) {
                            xx += 1.6f * u
                            lineTo(xx, yy - (if (q % 2 == 0) 1.2f else 0f) * u * (0.5f + azar(1350 + j, q)))
                            q++
                        }
                    }
                    drawPath(trazo, tinta, style = Stroke(0.6f * u))
                }
                if (!izquierda) {
                    // Un esquema: dos círculos y una flecha, y una cifra subrayada.
                    val ey = r.top + r.height * 0.68f
                    val ex = x0 + (x1 - x0) * 0.25f
                    drawCircle(tinta, r.height * 0.075f, Offset(ex, ey), style = Stroke(0.8f * u))
                    drawCircle(luz(VIOLETA, o, k).copy(alpha = 0.6f), r.height * 0.05f, Offset(ex + (x1 - x0) * 0.42f, ey), style = Stroke(0.8f * u))
                    drawLine(tinta, Offset(ex + r.height * 0.1f, ey), Offset(ex + (x1 - x0) * 0.42f - r.height * 0.08f, ey), 0.8f * u)
                    drawPath(Path().apply { val tx = ex + (x1 - x0) * 0.42f - r.height * 0.08f; moveTo(tx - 3f * u, ey - 2f * u); lineTo(tx, ey); lineTo(tx - 3f * u, ey + 2f * u) }, tinta, style = Stroke(0.8f * u))
                    drawLine(luz(ROSA, o, k).copy(alpha = 0.7f), Offset(x0 + (x1 - x0) * 0.1f, r.top + r.height * 0.88f), Offset(x0 + (x1 - x0) * 0.5f, r.top + r.height * 0.88f), 0.9f * u)
                }
            }
        }
        // El lomo hundido.
        drawLine(sombra(o, 0.35f), Offset(mid, r.top + r.height * 0.05f), Offset(mid, r.bottom - 1f * u), 1.6f * u)
        drawRect(sombra(o, 0.12f), Offset(mid, r.top + r.height * 0.05f), Size(r.width * 0.06f, r.height * 0.93f))
        // El canto de las hojas, abajo.
        drawRect(luz(HUESO_OSC, o, k), Offset(r.left + 1f * u, r.bottom - 2f * u), Size(r.width - 2f * u, 2f * u))
    }

    /** El bolígrafo, tumbado en la tapa delante del cuaderno. */
    private fun DrawScope.boligrafo(a: Offset, b: Offset, o: Boolean, k: Float) {
        val u = density
        drawLine(sombra(o, 0.3f), a + Offset(1.5f * u, 2f * u), b + Offset(1.5f * u, 2f * u), 2.8f * u, StrokeCap.Round)
        drawLine(luz(mezcla(TINTA, CIAN, 0.25f), o, k), a, b, 2.6f * u, StrokeCap.Round)
        val m = a + (b - a) * 0.72f
        drawLine(luz(CIAN, o, k), m, b, 2.8f * u, StrokeCap.Round)
        drawLine(luz(GRIS, o, k), a + (b - a) * 0.8f, a + (b - a) * 0.95f + Offset(0f, -1.5f * u), 0.8f * u)
        drawLine(brillo(o, 0.5f, k), a + Offset(0f, -0.8f * u), b + Offset(0f, -0.8f * u), 0.6f * u)
    }

    /** El temporizador: la caja blanca con su pantallita de cifras cian, dos botones y el pie. */
    private fun DrawScope.temporizador(r: Rect, o: Boolean, k: Float) {
        val u = density
        translate(r.width * 0.18f, r.height * 0.05f) { drawRoundRect(sombra(o, 0.18f), r.topLeft, r.size, CornerRadius(3f * u)) }
        sombraContacto(Offset(r.center.x + 3f * u, r.bottom), r.width * 1.2f, 3.5f * u, if (o) 0.5f else 0.32f)
        // Los botones de arriba.
        drawRoundRect(luz(GRIS, o, k), Offset(r.left + r.width * 0.15f, r.top - 3f * u), Size(r.width * 0.22f, 4f * u), CornerRadius(1.5f * u))
        drawRoundRect(luz(ROSA, o, k), Offset(r.left + r.width * 0.6f, r.top - 3f * u), Size(r.width * 0.22f, 4f * u), CornerRadius(1.5f * u))
        drawRoundRect(luz(BLANCO, o, k), r.topLeft, r.size, CornerRadius(3f * u))
        drawRoundRect(sombra(o, 0.2f), Offset(r.left + r.width * 0.7f, r.top), Size(r.width * 0.3f, r.height), CornerRadius(3f * u))
        drawRoundRect(brillo(o, 0.6f, k), r.topLeft, Size(r.width, 1.4f * u), CornerRadius(1f * u))
        // La pantallita: de día, gris; de noche, encendida en cian.
        val s = Rect(r.left + r.width * 0.12f, r.top + r.height * 0.18f, r.right - r.width * 0.12f, r.top + r.height * 0.62f)
        drawRect(if (o) Color(0xFF06161B) else mezcla(GRIS, TINTA, 0.55f), s.topLeft, s.size)
        val dig = if (o) mezcla(CIAN, Color.White, 0.55f) else mezcla(CIAN, Color.White, 0.3f)
        val ac = s.width * 0.15f
        cifras("12", s.left + s.width * 0.08f, s.top + s.height * 0.2f, ac, s.height * 0.6f, dig)
        drawRect(dig, Offset(s.left + s.width * 0.5f, s.top + s.height * 0.35f), Size(1.2f * u, 1.2f * u))
        drawRect(dig, Offset(s.left + s.width * 0.5f, s.top + s.height * 0.6f), Size(1.2f * u, 1.2f * u))
        cifras("30", s.left + s.width * 0.57f, s.top + s.height * 0.2f, ac, s.height * 0.6f, dig)
        // Dos teclas abajo.
        for (j in 0 until 3) drawCircle(luz(GRIS, o, k), r.height * 0.07f, Offset(r.left + r.width * (0.28f + 0.22f * j), r.top + r.height * 0.8f))
    }

    /**
     * El flexo: la base pesada redonda, el brazo de dos tramos con su muelle, las articulaciones y la pantalla cónica apuntando
     * al cuaderno. De noche, encendido: la bombilla, el borde de la pantalla y el haz sobre el cuaderno.
     */
    private fun DrawScope.flexo(pie: Offset, boca: Offset, ch: Float, o: Boolean) {
        val u = density
        val codo = Offset(pie.x + size.width * 0.03f, ch * 0.3f)
        val cabeza = Offset(boca.x + size.width * 0.045f, boca.y - ch * 0.07f)
        val k = if (o) 0.85f else 0f
        val acero = luz(GRIS, o, k * 0.6f)
        val oscuro = luz(GRIS_OSC, o, k * 0.6f)
        // Las sombras de los brazos en la pared.
        listOf(Offset(pie.x, pie.y - ch * 0.04f) to codo, codo to cabeza).forEach { (a, b) ->
            drawLine(sombra(o, 0.14f), a + Offset(size.width * 0.03f, ch * 0.03f), b + Offset(size.width * 0.03f, ch * 0.03f), 2.4f * u, StrokeCap.Round)
        }
        // La base.
        sombraContacto(Offset(pie.x + 4f * u, pie.y + 1f * u), size.width * 0.085f, 5f * u, if (o) 0.4f else 0.35f)
        drawOval(oscuro, Offset(pie.x - size.width * 0.035f, pie.y - ch * 0.04f), Size(size.width * 0.07f, ch * 0.045f))
        drawOval(acero, Offset(pie.x - size.width * 0.03f, pie.y - ch * 0.045f), Size(size.width * 0.06f, ch * 0.03f))
        drawArc(brillo(o, 0.6f, k), 190f, 80f, false, Offset(pie.x - size.width * 0.03f, pie.y - ch * 0.045f), Size(size.width * 0.06f, ch * 0.03f), style = Stroke(1f * u))
        // Los brazos: dos varillas paralelas y el muelle.
        val p0 = Offset(pie.x, pie.y - ch * 0.04f)
        fun varillas(a: Offset, b: Offset) {
            val d = b - a; val l = d.getDistance(); val n = Offset(-d.y / l, d.x / l) * (1.6f * u)
            drawLine(acero, a + n, b + n, 1.5f * u, StrokeCap.Round)
            drawLine(oscuro, a - n, b - n, 1.5f * u, StrokeCap.Round)
        }
        varillas(p0, codo)
        varillas(codo, cabeza)
        // El muelle, junto al tramo de abajo.
        val m0 = p0 + (codo - p0) * 0.15f; val m1 = p0 + (codo - p0) * 0.6f
        val dm = m1 - m0; val lm = dm.getDistance(); val nm = Offset(-dm.y / lm, dm.x / lm) * (3.2f * u)
        drawPath(Path().apply {
            moveTo(m0.x + nm.x, m0.y + nm.y)
            for (j in 1..14) { val q = m0 + dm * (j / 14f) + nm * (if (j % 2 == 0) 1f else 0.4f); lineTo(q.x, q.y) }
        }, acero, style = Stroke(0.8f * u))
        // Las articulaciones.
        listOf(p0, codo, cabeza).forEach { q -> drawCircle(oscuro, 2.6f * u, q); drawCircle(brillo(o, 0.5f, k), 0.9f * u, q - Offset(0.8f * u, 0.8f * u)) }
        // La pantalla: un cono que apunta abajo a la izquierda.
        val eje = boca - cabeza
        val le = eje.getDistance()
        val e = eje / le
        val nrm = Offset(-e.y, e.x)
        val ra = ch * 0.025f; val rb = ch * 0.07f
        val pantalla = Path().apply {
            val a1 = cabeza + nrm * ra; val a2 = cabeza - nrm * ra
            val b1 = boca + nrm * rb; val b2 = boca - nrm * rb
            moveTo(a1.x, a1.y); lineTo(b1.x, b1.y); lineTo(b2.x, b2.y); lineTo(a2.x, a2.y); close()
        }
        drawPath(pantalla, luz(mezcla(GRIS_OSC, CIAN, 0.25f), o, k * 0.5f))
        clipPath(pantalla) {
            drawLine(brillo(o, 0.55f, k), cabeza + nrm * (ra * 0.6f), boca + nrm * (rb * 0.7f), 1.6f * u)
            drawLine(sombra(o, 0.3f), cabeza - nrm * (ra * 0.5f), boca - nrm * (rb * 0.7f), 3f * u)
        }
        drawCircle(oscuro, ra * 1.1f, cabeza)
        // La boca de la pantalla: un óvalo, oscuro de día y encendido de noche.
        val giro = Math.toDegrees(kotlin.math.atan2(e.y.toDouble(), e.x.toDouble())).toFloat() + 90f
        rotate(giro, boca) {
            drawOval(if (o) FLEXO else luz(mezcla(GRIS_OSC, TINTA, 0.5f), o, 0f), Offset(boca.x - rb, boca.y - rb * 0.32f), Size(rb * 2f, rb * 0.64f))
            if (o) drawOval(Color.White.copy(alpha = 0.85f), Offset(boca.x - rb * 0.45f, boca.y - rb * 0.14f), Size(rb * 0.9f, rb * 0.28f))
            drawOval(luz(GRIS, o, k), Offset(boca.x - rb, boca.y - rb * 0.32f), Size(rb * 2f, rb * 0.64f), style = Stroke(1f * u))
        }
        if (o) {
            mancha(boca + e * (rb * 0.3f), rb * 2.6f, FLEXO.copy(alpha = 0.55f))
            // El haz: un cono tenue hasta el cuaderno.
            val fin = boca + e * (ch * 0.28f)
            val haz = Path().apply {
                val b1 = boca + nrm * rb; val b2 = boca - nrm * rb
                val f1 = fin + nrm * (rb * 2.6f); val f2 = fin - nrm * (rb * 2.6f)
                moveTo(b1.x, b1.y); lineTo(f1.x, f1.y); lineTo(f2.x, f2.y); lineTo(b2.x, b2.y); close()
            }
            drawPath(haz, Brush.linearGradient(listOf(FLEXO.copy(alpha = 0.22f), FLEXO.copy(alpha = 0f)), boca, fin))
        }
    }

    // ---------------------------------------------------------------- en la pared

    /** Las gafas de seguridad colgadas de un gancho por la goma: la montura de plástico claro, las rejillas y el reflejo. */
    private fun DrawScope.gafas(gancho: Offset, ancho: Float, o: Boolean, k: Float) {
        val u = density
        val alto = ancho * 0.42f
        val c = Offset(gancho.x + ancho * 0.04f, gancho.y + ancho * 0.62f)
        val giro = 7f
        // El gancho, con su sombra.
        drawCircle(sombra(o, 0.25f), 3.2f * u, gancho + Offset(2.5f * u, 2.5f * u))
        drawCircle(luz(GRIS, o, k), 3.2f * u, gancho)
        drawCircle(brillo(o, 0.6f, k), 1.1f * u, gancho - Offset(1f * u, 1f * u))
        rotate(giro, gancho) {
            val izq = Offset(c.x - ancho * 0.5f, c.y - alto * 0.1f)
            val der = Offset(c.x + ancho * 0.5f, c.y - alto * 0.1f)
            val goma = Path().apply {
                moveTo(izq.x, izq.y)
                cubicTo(izq.x + ancho * 0.05f, izq.y - alto * 0.8f, gancho.x - ancho * 0.12f, gancho.y + 4f * u, gancho.x, gancho.y + 2.5f * u)
                cubicTo(gancho.x + ancho * 0.12f, gancho.y + 4f * u, der.x - ancho * 0.05f, der.y - alto * 0.8f, der.x, der.y)
            }
            val montura = Path().apply {
                moveTo(c.x - ancho * 0.48f, c.y - alto * 0.35f)
                cubicTo(c.x - ancho * 0.2f, c.y - alto * 0.55f, c.x + ancho * 0.2f, c.y - alto * 0.55f, c.x + ancho * 0.48f, c.y - alto * 0.35f)
                cubicTo(c.x + ancho * 0.56f, c.y, c.x + ancho * 0.48f, c.y + alto * 0.42f, c.x + ancho * 0.3f, c.y + alto * 0.45f)
                cubicTo(c.x + ancho * 0.15f, c.y + alto * 0.46f, c.x + ancho * 0.1f, c.y + alto * 0.2f, c.x, c.y + alto * 0.2f)
                cubicTo(c.x - ancho * 0.1f, c.y + alto * 0.2f, c.x - ancho * 0.15f, c.y + alto * 0.46f, c.x - ancho * 0.3f, c.y + alto * 0.45f)
                cubicTo(c.x - ancho * 0.48f, c.y + alto * 0.42f, c.x - ancho * 0.56f, c.y, c.x - ancho * 0.48f, c.y - alto * 0.35f)
                close()
            }
            // Las sombras en el azulejo.
            translate(ancho * 0.07f, alto * 0.2f) {
                drawPath(goma, sombra(o, 0.18f), style = Stroke(2.4f * u))
                drawPath(montura, sombra(o, 0.18f))
            }
            drawPath(goma, luz(mezcla(GRIS_OSC, CIAN, 0.3f), o, k), style = Stroke(2.6f * u, cap = StrokeCap.Round))
            drawPath(goma, brillo(o, 0.3f, k), style = Stroke(0.6f * u))
            drawPath(montura, luz(mezcla(VIDRIO, HIELO, 0.3f), o, k).copy(alpha = 0.72f))
            clipPath(montura) {
                drawRect(sombra(o, 0.16f), Offset(c.x + ancho * 0.15f, c.y - alto), Size(ancho * 0.5f, alto * 2f))
                // El reflejo del tubo del techo.
                drawLine(brillo(o, 0.85f, k + 0.3f), Offset(c.x - ancho * 0.36f, c.y - alto * 0.1f), Offset(c.x - ancho * 0.22f, c.y - alto * 0.3f), 2f * u, StrokeCap.Round)
                drawLine(brillo(o, 0.6f, k + 0.3f), Offset(c.x + ancho * 0.08f, c.y - alto * 0.18f), Offset(c.x + ancho * 0.16f, c.y - alto * 0.3f), 1.3f * u, StrokeCap.Round)
            }
            drawPath(montura, luz(GRIS, o, k), style = Stroke(2.2f * u, join = StrokeJoin.Round))
            // Las rejillas de ventilación de arriba.
            for (j in 0 until 4) {
                val x = c.x + ancho * (-0.33f + 0.22f * j)
                drawRoundRect(luz(GRIS_OSC, o, k), Offset(x - ancho * 0.035f, c.y - alto * 0.47f), Size(ancho * 0.07f, alto * 0.08f), CornerRadius(1f * u))
            }
        }
    }

    /** La hoja de protocolo pegada con cinta, con su encabezado, sus pasos y casillas, una tabla y la esquina levantada. */
    private fun DrawScope.protocolo(r: Rect, o: Boolean, k: Float) {
        val u = density
        val doblez = r.width * 0.16f
        val hoja = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - doblez); lineTo(r.right - doblez, r.bottom); lineTo(r.left, r.bottom); close()
        }
        rotate(-1.5f, r.center) {
            translate(2.5f * u, 3f * u) { drawPath(hoja, sombra(o, 0.18f)) }
            drawPath(hoja, luz(BLANCO, o, k + 0.12f))
            clipPath(hoja) {
                drawRect(sombra(o, 0.06f), Offset(r.left + r.width * 0.6f, r.top), Size(r.width * 0.4f, r.height))
                // El encabezado cian y el título.
                drawRect(luz(CIAN, o, k), Offset(r.left, r.top + r.height * 0.06f), Size(r.width, r.height * 0.07f))
                drawRect(luz(Color.White, o, k).copy(alpha = 0.8f), Offset(r.left + r.width * 0.08f, r.top + r.height * 0.085f), Size(r.width * 0.5f, r.height * 0.022f))
                // Los pasos: casilla y renglón.
                val tinta = luz(TINTA, o, k).copy(alpha = 0.5f)
                for (j in 0 until 6) {
                    val y = r.top + r.height * (0.2f + 0.075f * j)
                    drawRect(tinta, Offset(r.left + r.width * 0.08f, y - r.height * 0.018f), Size(r.height * 0.036f, r.height * 0.036f), style = Stroke(0.6f * u))
                    if (j < 4) drawLine(luz(VIOLETA, o, k).copy(alpha = 0.8f), Offset(r.left + r.width * 0.085f, y), Offset(r.left + r.width * 0.12f, y - r.height * 0.02f), 0.9f * u)
                    drawLine(tinta, Offset(r.left + r.width * 0.2f, y), Offset(r.left + r.width * (0.55f + 0.35f * azar(1360, j)), y), 1f * u)
                }
                // Una tabla de tiempos y temperaturas.
                val t0 = r.top + r.height * 0.67f
                for (i in 0..3) drawLine(tinta, Offset(r.left + r.width * 0.08f, t0 + r.height * 0.06f * i), Offset(r.right - r.width * 0.1f, t0 + r.height * 0.06f * i), 0.5f * u)
                for (j in 0..3) drawLine(tinta, Offset(r.left + r.width * (0.08f + 0.273f * j), t0), Offset(r.left + r.width * (0.08f + 0.273f * j), t0 + r.height * 0.18f), 0.5f * u)
                // Una nota a mano en rojo, en el margen.
                drawPath(Path().apply { moveTo(r.left + r.width * 0.55f, r.top + r.height * 0.93f); cubicTo(r.left + r.width * 0.6f, r.top + r.height * 0.89f, r.left + r.width * 0.66f, r.top + r.height * 0.95f, r.left + r.width * 0.72f, r.top + r.height * 0.91f) },
                    luz(ROSA, o, k), style = Stroke(1f * u, cap = StrokeCap.Round))
            }
            // La esquina levantada: el revés del papel y su sombra en el azulejo.
            val esq = Path().apply { moveTo(r.right, r.bottom - doblez); lineTo(r.right - doblez * 0.35f, r.bottom - doblez * 0.3f); lineTo(r.right - doblez, r.bottom); close() }
            translate(2f * u, 2.5f * u) { drawPath(esq, sombra(o, 0.25f)) }
            drawPath(esq, luz(mezcla(BLANCO, HUESO_OSC, 0.45f), o, k))
            // Dos tiras de cinta arriba, translúcidas.
            listOf(Offset(r.left + r.width * 0.02f, r.top + r.height * 0.01f) to -38f, Offset(r.right - r.width * 0.02f, r.top + r.height * 0.01f) to 34f).forEachIndexed { i, (p, g) ->
                rotate(g, p) {
                    val cin = cinta(Offset(p.x - r.width * 0.11f, p.y), Offset(p.x + r.width * 0.11f, p.y), r.height * 0.06f, 1370 + i)
                    drawPath(cin, luz(mezcla(HUESO, AMARILLO, 0.25f), o, k).copy(alpha = 0.6f))
                }
            }
        }
    }

    // ---------------------------------------------------------------- al pie

    /** El contenedor amarillo de residuos: la tapa con su bisagra, el pedal, la bolsa que asoma y el símbolo de riesgo biológico. */
    private fun DrawScope.contenedor(pie: Offset, ancho: Float, alto: Float, o: Boolean) {
        val u = density
        val a = ancho / 2
        val tapaAlto = alto * 0.13f
        val cuerpo = Path().apply {
            moveTo(pie.x - a * 0.9f, pie.y); lineTo(pie.x - a, pie.y - alto + tapaAlto); lineTo(pie.x + a, pie.y - alto + tapaAlto); lineTo(pie.x + a * 0.9f, pie.y); close()
        }
        sombraContacto(Offset(pie.x + a * 0.3f, pie.y + 1f * u), ancho * 1.25f, 8f * u, if (o) 0.6f else 0.4f)
        drawPath(cuerpo, luz(AMARILLO, o, 0.02f))
        clipPath(cuerpo) {
            drawRect(sombra(o, 0.25f), Offset(pie.x + a * 0.45f, pie.y - alto), Size(a, alto))
            drawRect(brillo(o, 0.4f), Offset(pie.x - a * 0.85f, pie.y - alto), Size(2f * u, alto))
            // Las nervaduras del plástico.
            for (j in 1..3) drawLine(sombra(o, 0.12f), Offset(pie.x - a, pie.y - alto * 0.22f * j), Offset(pie.x + a, pie.y - alto * 0.22f * j), 0.8f * u)
            // El roce de abajo.
            drawRect(sombra(o, 0.12f), Offset(pie.x - a, pie.y - alto * 0.08f), Size(ancho, alto * 0.08f))
        }
        // La bolsa que asoma bajo la tapa.
        drawPath(Path().apply {
            moveTo(pie.x - a * 1.02f, pie.y - alto + tapaAlto + 2f * u)
            cubicTo(pie.x - a * 0.6f, pie.y - alto + tapaAlto + 7f * u, pie.x + a * 0.2f, pie.y - alto + tapaAlto + 2f * u, pie.x + a * 1.02f, pie.y - alto + tapaAlto + 5f * u)
            lineTo(pie.x + a * 1.02f, pie.y - alto + tapaAlto); lineTo(pie.x - a * 1.02f, pie.y - alto + tapaAlto); close()
        }, luz(mezcla(AMARILLO, Color.White, 0.3f), o, 0.02f).copy(alpha = 0.85f))
        // La tapa.
        val tp = Rect(pie.x - a * 1.06f, pie.y - alto, pie.x + a * 1.06f, pie.y - alto + tapaAlto)
        drawRoundRect(luz(mezcla(AMARILLO, Color(0xFFB8862A), 0.35f), o, 0.02f), tp.topLeft, tp.size, CornerRadius(2f * u))
        drawRect(brillo(o, 0.55f), Offset(tp.left + 2f * u, tp.top), Size(tp.width - 4f * u, 1.3f * u))
        drawRect(sombra(o, 0.25f), Offset(tp.left, tp.bottom - 1.5f * u), Size(tp.width, 1.5f * u))
        // El símbolo de riesgo biológico.
        val sc = Offset(pie.x + a * 0.05f, pie.y - alto * 0.5f)
        riesgoBio(sc, alto * 0.17f, luz(TINTA, o, 0.02f), luz(AMARILLO, o, 0.02f))
        // El pedal.
        drawRect(luz(GRIS_OSC, o, 0.02f), Offset(pie.x - a * 0.35f, pie.y - alto * 0.05f), Size(a * 0.7f, alto * 0.05f))
        drawRect(brillo(o, 0.4f), Offset(pie.x - a * 0.35f, pie.y - alto * 0.05f), Size(a * 0.7f, 1f * u))
    }

    /** Dos cajas de poliestireno apiladas, la de arriba más chica y corrida, con su cinta, una etiqueta y su textura de bolitas. */
    private fun DrawScope.poliestireno(pie: Offset, ancho: Float, hb: Float, o: Boolean) {
        val u = density
        val k = 0.04f
        sombraContacto(Offset(pie.x + ancho * 0.1f, pie.y + 1f * u), ancho * 1.2f, 8f * u, if (o) 0.6f else 0.4f)
        val cajas = listOf(Rect(pie.x - ancho / 2, pie.y - hb * 0.34f, pie.x + ancho / 2, pie.y), Rect(pie.x - ancho * 0.36f, pie.y - hb * 0.56f, pie.x + ancho * 0.4f, pie.y - hb * 0.34f))
        cajas.forEachIndexed { i, r ->
            // La sombra de la de arriba sobre la de abajo.
            if (i == 1) drawRect(sombra(o, 0.25f), Offset(r.left + 3f * u, r.bottom), Size(r.width, 3f * u))
            drawRect(luz(mezcla(BLANCO, HUESO, 0.3f), o, k), r.topLeft, r.size)
            // La tapa: una franja arriba, con su junta.
            drawRect(luz(BLANCO, o, k), r.topLeft, Size(r.width, r.height * 0.24f))
            drawLine(sombra(o, 0.3f), Offset(r.left, r.top + r.height * 0.24f), Offset(r.right, r.top + r.height * 0.24f), 1f * u)
            drawRect(brillo(o, 0.6f), r.topLeft, Size(r.width, 1.2f * u))
            drawRect(sombra(o, 0.2f), Offset(r.right - r.width * 0.16f, r.top), Size(r.width * 0.16f, r.height))
            // Las bolitas del poliestireno.
            for (j in 0 until 14) drawCircle(sombra(o, 0.1f), (0.6f + 0.8f * azar(1380 + i, j)) * u, Offset(r.left + r.width * azar(1382 + i, j), r.top + r.height * azar(1384 + i, j)))
            // La cinta de embalar que cruza.
            drawRect(luz(mezcla(HUESO, AMARILLO, 0.3f), o, k).copy(alpha = 0.75f), Offset(r.left + r.width * (0.18f + 0.1f * i), r.top), Size(r.width * 0.1f, r.height))
        }
        // La etiqueta de la de abajo, escrita a rotulador.
        val et = Rect(cajas[0].left + ancho * 0.36f, cajas[0].top + hb * 0.12f, cajas[0].left + ancho * 0.66f, cajas[0].top + hb * 0.24f)
        drawRect(luz(BLANCO, o, k), et.topLeft, et.size)
        drawRect(luz(ROSA, o, k), et.topLeft, Size(et.width, 1.6f * u))
        drawLine(luz(TINTA, o, k).copy(alpha = 0.6f), Offset(et.left + 2f * u, et.center.y + 1f * u), Offset(et.right - 4f * u, et.center.y), 1f * u)
    }

    // ---------------------------------------------------------------- los compartimentos

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val o = oscuro
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // De noche, el flexo queda encima del armario: a las repisas les llega poco, más por arriba a la derecha.
        val k = 0.2f
        val piso = h * 0.9f
        // ---- el fondo de la repisa: el fondo del armario, la sombra de la repisa de arriba y el vidrio del piso
        drawRect(luz(mezcla(HUESO_OSC, HUESO, 0.35f), o, k * 0.5f))
        // La sombra de la repisa de arriba, en tres escalones (sin degradados: se repinta en cada cuadro al abrir).
        val hs = h * (if (o) 0.5f else 0.3f)
        drawRect(sombra(o, if (o) 0.3f else 0.1f), Offset.Zero, Size(w, hs))
        drawRect(sombra(o, if (o) 0.3f else 0.12f), Offset.Zero, Size(w, hs * 0.55f))
        drawRect(sombra(o, if (o) 0.3f else 0.18f), Offset.Zero, Size(w, hs * 0.22f))
        drawRect(sombra(o, 0.22f), Offset(w * 0.93f, 0f), Size(w * 0.07f, h))
        drawRect(brillo(o, 0.25f, 0.1f), Offset(0f, 0f), Size(w * 0.03f, h))
        // Los agujeros de la chapa del fondo, en filas.
        for (fy in 0 until 4) for (fx in 0 until 9) {
            val p = Offset(w * (0.08f + 0.105f * fx + (if (fy % 2 == 0) 0f else 0.052f)), h * (0.12f + 0.12f * fy))
            drawCircle(sombra(o, 0.22f), 1.3f * u, p)
        }
        // El piso de vidrio grueso, visto desde arriba, con su reflejo.
        drawRect(luz(mezcla(CANTO_VIDRIO, BLANCO, 0.45f), o, k), Offset(0f, piso), Size(w, h - piso))
        drawRect(brillo(o, 0.5f, 0.2f), Offset(0f, piso), Size(w, 1f * u))
        if (o) { drawRect(FLEXO.copy(alpha = 0.04f), Offset(w * 0.55f, 0f), Size(w * 0.45f, h)); drawRect(FLEXO.copy(alpha = 0.18f), Offset(0f, piso), Size(w, 1f * u)) }

        if (c.vacio) { repisaVacia(w, h, piso, o, k); return }
        val dy = SALIDA * h * ab
        translate(0f, dy) {
            when {
                c.sinTema -> cubeta(w, h, piso, c, o, k)
                else -> when ((azar(c.semilla, 11) * 3f).toInt().coerceIn(0, 2)) {
                    0 -> criogenia(w, h, piso, c, ab, o, k)
                    1 -> conicos(w, h, piso, c, o, k)
                    else -> portaobjetos(w, h, piso, c, ab, o, k)
                }
            }
        }
    }

    /** La tarjeta impresa del rótulo, con su franja de color. */
    private fun DrawScope.etiqueta(w: Float, h: Float, franja: Color, o: Boolean, k: Float) {
        val u = density
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(sombra(o, 0.15f), Offset(r.left + 1f * u, r.top + 1.2f * u), r.size)
        drawRect(tarjeta(o), r.topLeft, r.size)
        drawRect(luz(franja, o, k + 0.3f), r.topLeft, Size(r.width, 2.4f * u))
        drawRect(brillo(o, 0.4f, k), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
    }

    /** La caja de criogenia azul hielo: la rejilla de viales con sus tapones (más cuanto más guarda), la tapa apoyada detrás y la escarcha. */
    private fun DrawScope.criogenia(w: Float, h: Float, piso: Float, c: EstadoCajon, ab: Float, o: Boolean, k: Float) {
        val u = density
        val frente = Rect(w * 0.12f, h * 0.48f, w * 0.86f, piso + h * 0.03f)
        val fondo = 0.035f * w
        val arriba = h * 0.37f
        // La tapa, apoyada contra el fondo, detrás de la caja.
        val tapa = Path().apply { moveTo(w * 0.2f, arriba + 2f * u); lineTo(w * 0.25f, h * 0.1f); lineTo(w * 0.82f, h * 0.12f); lineTo(w * 0.8f, arriba + 2f * u); close() }
        translate(w * 0.03f, h * 0.02f) { drawPath(tapa, sombra(o, 0.2f)) }
        drawPath(tapa, luz(HIELO, o, k).copy(alpha = 0.75f))
        drawPath(tapa, luz(mezcla(HIELO, TINTA, 0.35f), o, k), style = Stroke(0.9f * u))
        drawLine(brillo(o, 0.6f, k), Offset(w * 0.27f, h * 0.14f), Offset(w * 0.24f, arriba - 2f * u), 1.2f * u)
        // La sombra de la caja en el vidrio.
        drawRect(sombra(o, 0.3f), Offset(frente.left + w * 0.03f, piso), Size(frente.width, h * 0.05f))
        // La cara de arriba: la rejilla de 3 × 9 agujeros, vista un poco desde arriba.
        val sup = Path().apply { moveTo(frente.left, frente.top); lineTo(frente.left + fondo, arriba); lineTo(frente.right - fondo, arriba); lineTo(frente.right, frente.top); close() }
        drawPath(sup, luz(mezcla(HIELO, Color.White, 0.25f), o, k))
        val llenos = (c.n * 3).coerceIn(2, 27)
        for (fila in 0 until 3) for (col in 0 until 9) {
            val f = (fila + 0.5f) / 3f
            val izq = frente.left + fondo * (1 - f); val der = frente.right - fondo * (1 - f)
            val y = arriba + (frente.top - arriba) * f
            val x = izq + (der - izq) * (col + 0.5f) / 9f
            val rx = (der - izq) / 9f * 0.38f; val ry = (frente.top - arriba) / 3f * 0.32f
            val i = fila * 9 + col
            // Los viales de la fila de atrás asoman un poco más.
            if (i < llenos) {
                val tap = TAPONES[(i + c.semilla.mod(TAPONES.size)) % TAPONES.size]
                drawRect(luz(VIDRIO, o, k).copy(alpha = 0.7f), Offset(x - rx * 0.8f, y - ry * 2.2f), Size(rx * 1.6f, ry * 2.2f))
                drawOval(luz(tap, o, k), Offset(x - rx, y - ry * 3.2f), Size(rx * 2, ry * 2))
                drawOval(luz(mezcla(tap, Color.Black, 0.25f), o, k), Offset(x - rx, y - ry * 2.6f), Size(rx * 2, ry * 1.4f))
                drawOval(luz(tap, o, k), Offset(x - rx, y - ry * 3.2f), Size(rx * 2, ry * 1.6f))
                drawCircle(brillo(o, 0.7f, k + 0.2f), rx * 0.28f, Offset(x - rx * 0.35f, y - ry * 2.6f))
            } else drawOval(luz(mezcla(HIELO, TINTA, 0.55f), o, k), Offset(x - rx * 0.8f, y - ry * 0.8f), Size(rx * 1.6f, ry * 1.6f))
        }
        drawPath(sup, luz(mezcla(HIELO, TINTA, 0.3f), o, k), style = Stroke(0.8f * u))
        // El frente de la caja, con su lado de sombra y la escarcha en el canto.
        drawRect(luz(HIELO, o, k), frente.topLeft, frente.size)
        drawRect(sombra(o, 0.2f), Offset(frente.right - frente.width * 0.14f, frente.top), Size(frente.width * 0.14f, frente.height))
        drawRect(brillo(o, 0.6f, k + 0.1f), frente.topLeft, Size(frente.width, 1.3f * u))
        drawRect(brillo(o, 0.3f, k), frente.topLeft, Size(1.4f * u, frente.height))
        for (j in 0 until 9) drawCircle(luz(Color.White, o, k + 0.2f).copy(alpha = 0.7f), (0.8f + 1.2f * azar(c.semilla, j)) * u, Offset(frente.left + frente.width * azar(c.semilla + 3, j), frente.top + (1f + 3f * azar(c.semilla + 5, j)) * u))
        etiqueta(w, h, CIAN, o, k)
        // El código de barras, debajo de la etiqueta.
        val by = (h * rotulo.bottom + frente.bottom) / 2
        var bx = frente.right - frente.width * 0.18f; var q = 0
        while (bx > frente.right - frente.width * 0.42f) { val bw = (0.5f + 1.1f * azar(c.semilla, q)) * u; drawRect(luz(TINTA, o, k).copy(alpha = 0.6f), Offset(bx - bw, by - 2f * u), Size(bw, 4f * u)); bx -= bw + 0.9f * u; q++ }
        // Con más de nueve ideas, un vial suelto tumbado al lado.
        if (c.n > 9) {
            val vy = piso + h * 0.015f
            drawRoundRect(luz(VIDRIO, o, k), Offset(w * 0.86f, vy - 4f * u), Size(w * 0.1f, 4f * u), CornerRadius(2f * u))
            drawRect(luz(TAPONES[c.semilla.mod(TAPONES.size)], o, k), Offset(w * 0.86f, vy - 4.5f * u), Size(w * 0.025f, 5f * u))
        }
        // Al abrir, el vapor frío del nitrógeno.
        if (ab > 0f) for (j in 0 until 6) drawOval(luz(Color.White, o, k + 0.3f).copy(alpha = 0.4f * ab * (1f - j / 8f)),
            Offset(w * (0.12f + 0.13f * j), arriba - h * (0.05f + 0.06f * ab) - j * 1.5f * u), Size(w * (0.14f + 0.03f * j), h * 0.07f))
    }

    /** La gradilla de tubos cónicos con tapón de rosca: un tubo por cada idea (hasta ocho), cada uno con su nivel y su color. */
    private fun DrawScope.conicos(w: Float, h: Float, piso: Float, c: EstadoCajon, o: Boolean, k: Float) {
        val u = density
        val panel = Rect(w * 0.12f, h * 0.52f, w * 0.86f, piso + h * 0.03f)
        val placa = h * 0.46f
        drawRect(sombra(o, 0.3f), Offset(panel.left + w * 0.03f, piso), Size(panel.width, h * 0.05f))
        // La placa de arriba de la gradilla, con sus agujeros, vista un poco desde arriba.
        drawRect(luz(mezcla(BLANCO, HUESO, 0.3f), o, k), Offset(panel.left + w * 0.02f, placa), Size(panel.width - w * 0.04f, panel.top - placa))
        val n = c.n.coerceIn(1, 8)
        val paso = (panel.width - w * 0.06f) / 8f
        for (i in 0 until 8) {
            val x = panel.left + w * 0.03f + paso * (i + 0.5f)
            val tw = paso * 0.66f
            if (i < n) {
                val largo = h * (if (azar(c.semilla, i) < 0.3f) 0.24f else 0.32f)
                val nivel = 0.2f + 0.6f * azar(c.semilla + 1, i)
                val col = listOf(CIAN, VIOLETA, ROSA, AMARILLO, mezcla(VIOLETA, CIAN, 0.5f))[(i + c.semilla.mod(5)) % 5]
                val top = placa + h * 0.04f - largo
                val cuerpo = Path().apply {
                    moveTo(x - tw / 2, top); lineTo(x + tw / 2, top); lineTo(x + tw / 2, placa + h * 0.03f); lineTo(x - tw / 2, placa + h * 0.03f); close()
                }
                translate(tw * 0.5f, h * 0.01f) { drawPath(cuerpo, sombra(o, 0.12f)) }
                vidrio(cuerpo, o, k, 0.5f)
                liquido(cuerpo, Rect(x - tw / 2, top, x + tw / 2, placa + h * 0.03f), placa + h * 0.03f - largo * nivel, col, o, k, 1.6f)
                // Las marcas impresas de la graduación.
                for (j in 1..3) drawLine(luz(GRIS_OSC, o, k).copy(alpha = 0.5f), Offset(x - tw / 2, top + largo * 0.22f * j), Offset(x - tw * 0.1f, top + largo * 0.22f * j), 0.5f * u)
                drawLine(brillo(o, 0.7f, k + 0.2f), Offset(x - tw * 0.28f, top + 4f * u), Offset(x - tw * 0.28f, placa), 1f * u)
                // El tapón de rosca, con sus estrías.
                val tp = Rect(x - tw * 0.62f, top - h * 0.05f, x + tw * 0.62f, top + 1f * u)
                drawRoundRect(luz(TAPONES[(i + c.semilla.mod(6)) % 6], o, k), tp.topLeft, tp.size, CornerRadius(1.2f * u))
                for (j in 1 until 4) drawLine(sombra(o, 0.3f), Offset(tp.left + tp.width * j / 4f, tp.top + 1f * u), Offset(tp.left + tp.width * j / 4f, tp.bottom - 1f * u), 0.5f * u)
                drawRect(brillo(o, 0.6f, k + 0.1f), tp.topLeft, Size(tp.width * 0.25f, tp.height))
            }
            drawOval(luz(mezcla(HUESO_OSC, TINTA, 0.35f), o, k), Offset(x - tw * 0.55f, placa + (panel.top - placa) * 0.25f), Size(tw * 1.1f, (panel.top - placa) * 0.5f))
            if (i < n) drawRect(luz(VIDRIO, o, k).copy(alpha = 0.7f), Offset(x - tw / 2, placa), Size(tw, (panel.top - placa) * 0.55f))
        }
        // El frente de la gradilla, con su lado de sombra.
        drawRect(luz(mezcla(BLANCO, HUESO, 0.4f), o, k), panel.topLeft, panel.size)
        drawRect(sombra(o, 0.18f), Offset(panel.right - panel.width * 0.12f, panel.top), Size(panel.width * 0.12f, panel.height))
        drawRect(brillo(o, 0.6f, k + 0.1f), panel.topLeft, Size(panel.width, 1.3f * u))
        drawRect(sombra(o, 0.2f), Offset(panel.left, panel.bottom - h * 0.03f), Size(panel.width, h * 0.03f))
        etiqueta(w, h, VIOLETA, o, k)
        // Un tubo cónico tumbado en el vidrio, junto a la gradilla, con su punta.
        val ty = piso + (h - piso) * 0.45f
        val tx = w * 0.865f
        val tubo = Path().apply { moveTo(tx, ty - 3.5f * u); lineTo(tx + w * 0.07f, ty - 3.5f * u); lineTo(tx + w * 0.105f, ty); lineTo(tx + w * 0.07f, ty + 3.5f * u); lineTo(tx, ty + 3.5f * u); close() }
        drawRect(sombra(o, 0.2f), Offset(tx + 1f * u, ty + 2f * u), Size(w * 0.1f, 3f * u))
        vidrio(tubo, o, k, 0.6f)
        drawRect(luz(TAPONES[(c.semilla + 2).mod(6)], o, k), Offset(tx - w * 0.025f, ty - 4.5f * u), Size(w * 0.03f, 9f * u))
    }

    /** La caja gris de portaobjetos con la tapa abatible entreabierta: un portaobjetos por idea (de dos en dos), con su muestra teñida. */
    private fun DrawScope.portaobjetos(w: Float, h: Float, piso: Float, c: EstadoCajon, ab: Float, o: Boolean, k: Float) {
        val u = density
        val frente = Rect(w * 0.12f, h * 0.5f, w * 0.86f, piso + h * 0.03f)
        val boca = h * 0.42f
        val gris = mezcla(GRIS, BLANCO, 0.25f)
        drawRect(sombra(o, 0.3f), Offset(frente.left + w * 0.03f, piso), Size(frente.width, h * 0.05f))
        // La tapa abatible, abierta hacia atrás: más cuanto más se abre el compartimento.
        val alzada = h * (0.2f + 0.06f * ab)
        val tapa = Path().apply {
            moveTo(frente.left + w * 0.03f, boca); lineTo(frente.left + w * 0.06f, boca - alzada); lineTo(frente.right - w * 0.0f, boca - alzada + h * 0.015f); lineTo(frente.right - w * 0.03f, boca); close()
        }
        translate(w * 0.03f, h * 0.015f) { drawPath(tapa, sombra(o, 0.2f)) }
        drawPath(tapa, luz(mezcla(gris, Color.White, 0.45f), o, k).copy(alpha = 0.88f))
        clipPath(tapa) {
            // Las ranuras por dentro de la tapa.
            for (j in 0 until 6) {
                val x = frente.left + w * 0.06f + (frente.width - w * 0.06f) * (j + 0.5f) / 6f
                drawLine(sombra(o, 0.1f), Offset(x, boca - alzada), Offset(x - w * 0.02f, boca), 0.7f * u)
            }
            drawLine(brillo(o, 0.6f, k), Offset(frente.left + w * 0.07f, boca - alzada + 2f * u), Offset(frente.left + w * 0.04f, boca - 2f * u), 1.2f * u)
            drawRect(sombra(o, 0.12f), Offset(frente.right - frente.width * 0.15f, boca - alzada), Size(frente.width * 0.2f, alzada))
        }
        drawPath(tapa, luz(GRIS_OSC, o, k), style = Stroke(0.8f * u))
        // La boca de la caja: el hueco oscuro y los portaobjetos parados en sus ranuras.
        drawRect(luz(mezcla(GRIS_OSC, TINTA, 0.3f), o, k), Offset(frente.left + w * 0.02f, boca), Size(frente.width - w * 0.04f, frente.top - boca))
        val total = 20
        val llenos = (c.n * 2).coerceIn(2, total)
        val paso = (frente.width - w * 0.08f) / total
        for (i in 0 until total) {
            val x = frente.left + w * 0.04f + paso * (i + 0.5f)
            if (i < llenos) {
                val sube = h * (0.11f + 0.03f * azar(c.semilla, i))
                drawRect(luz(mezcla(VIDRIO, Color.White, 0.3f), o, k).copy(alpha = 0.92f), Offset(x - paso * 0.32f, boca - sube), Size(paso * 0.64f, frente.top - boca + sube))
                // El extremo esmerilado, para escribir, y la muestra teñida.
                drawRect(luz(Color.White, o, k + 0.1f), Offset(x - paso * 0.32f, boca - sube), Size(paso * 0.64f, sube * 0.4f))
                drawRect(luz(if (azar(c.semilla + 2, i) < 0.5f) ROSA else VIOLETA, o, k).copy(alpha = 0.7f), Offset(x - paso * 0.22f, boca - sube * 0.35f), Size(paso * 0.44f, sube * 0.5f))
                drawLine(sombra(o, 0.25f), Offset(x + paso * 0.32f, boca - sube), Offset(x + paso * 0.32f, frente.top), 0.6f * u)
            } else drawLine(sombra(o, 0.3f), Offset(x, boca + 1f * u), Offset(x, frente.top), 0.6f * u)
        }
        // El frente de la caja, con el cierre y su lado de sombra.
        drawRect(luz(gris, o, k), frente.topLeft, frente.size)
        drawRect(sombra(o, 0.22f), Offset(frente.right - frente.width * 0.12f, frente.top), Size(frente.width * 0.12f, frente.height))
        drawRect(brillo(o, 0.55f, k + 0.1f), frente.topLeft, Size(frente.width, 1.3f * u))
        drawRect(brillo(o, 0.3f, k), frente.topLeft, Size(1.4f * u, frente.height))
        drawRoundRect(luz(GRIS_OSC, o, k), Offset(frente.center.x - w * 0.05f, frente.top - 1f * u), Size(w * 0.1f, h * 0.04f), CornerRadius(1.5f * u))
        etiqueta(w, h, ROSA, o, k)
    }

    /** Lo que no tiene tema: una cubeta de acero con frascos sin etiqueta (más cuanto más guarda) y un rótulo de cinta de papel. */
    private fun DrawScope.cubeta(w: Float, h: Float, piso: Float, c: EstadoCajon, o: Boolean, k: Float) {
        val u = density
        val frente = Rect(w * 0.1f, h * 0.52f, w * 0.88f, piso + h * 0.03f)
        drawRect(sombra(o, 0.3f), Offset(frente.left + w * 0.03f, piso), Size(frente.width, h * 0.05f))
        // Los frascos: ámbar, transparente y gotero, de alturas distintas, ladeados.
        val n = (2 + c.n).coerceIn(3, 5)
        val tipos = listOf(Triple(0.2f, 0.44f, Color(0xFFB0702A)), Triple(0.38f, 0.36f, VIDRIO), Triple(0.55f, 0.3f, Color(0xFFB0702A)), Triple(0.7f, 0.4f, VIDRIO), Triple(0.82f, 0.26f, HIELO))
        for (i in 0 until n) {
            val (fx, fh, col) = tipos[i]
            val x = w * fx; val altoF = h * fh; val a = w * (0.05f + 0.015f * (i % 2))
            val giro = (azar(c.semilla, i) - 0.5f) * 10f
            rotate(giro, Offset(x, frente.top + h * 0.1f)) {
                val pie = frente.top + h * 0.12f
                val cuerpo = Path().apply {
                    moveTo(x - a, pie); lineTo(x - a, pie - altoF * 0.62f)
                    cubicTo(x - a, pie - altoF * 0.76f, x - a * 0.4f, pie - altoF * 0.78f, x - a * 0.35f, pie - altoF * 0.82f)
                    lineTo(x + a * 0.35f, pie - altoF * 0.82f)
                    cubicTo(x + a * 0.4f, pie - altoF * 0.78f, x + a, pie - altoF * 0.76f, x + a, pie - altoF * 0.62f)
                    lineTo(x + a, pie); close()
                }
                translate(a * 0.6f, h * 0.01f) { drawPath(cuerpo, sombra(o, 0.14f)) }
                drawPath(cuerpo, luz(col, o, k).copy(alpha = if (col == VIDRIO) 0.6f else 0.92f))
                clipPath(cuerpo) { drawRect(sombra(o, 0.25f), Offset(x + a * 0.35f, pie - altoF), Size(a, altoF)) }
                drawPath(cuerpo, luz(GRIS_OSC, o, k).copy(alpha = 0.5f), style = Stroke(0.7f * u))
                drawLine(brillo(o, 0.7f, k + 0.2f), Offset(x - a * 0.6f, pie - altoF * 0.65f), Offset(x - a * 0.6f, pie - altoF * 0.1f), 1.2f * u, StrokeCap.Round)
                // El tapón negro, o la perilla del gotero.
                if (i == 4) drawOval(luz(TINTA, o, k), Offset(x - a * 0.4f, pie - altoF * 1.05f), Size(a * 0.8f, altoF * 0.26f))
                else drawRect(luz(mezcla(TINTA, GRIS_OSC, 0.4f), o, k), Offset(x - a * 0.42f, pie - altoF * 0.95f), Size(a * 0.84f, altoF * 0.14f))
                // Una etiqueta en blanco, sin nada escrito, en el de en medio.
                if (i == 1) drawRect(luz(BLANCO, o, k), Offset(x - a * 0.8f, pie - altoF * 0.45f), Size(a * 1.6f, altoF * 0.22f))
            }
        }
        // La cubeta de acero inoxidable: el frente cepillado, el borde doblado y su lado de sombra.
        drawRect(luz(mezcla(GRIS, BLANCO, 0.4f), o, k), frente.topLeft, frente.size)
        for (j in 0 until 8) drawLine(brillo(o, 0.18f, k), Offset(frente.left, frente.top + frente.height * (j + 0.5f) / 8f), Offset(frente.right, frente.top + frente.height * (j + 0.5f) / 8f), 0.5f * u)
        drawRect(sombra(o, 0.22f), Offset(frente.right - frente.width * 0.12f, frente.top), Size(frente.width * 0.12f, frente.height))
        drawRect(luz(mezcla(GRIS, BLANCO, 0.6f), o, k + 0.1f), Offset(frente.left - 2f * u, frente.top - 2f * u), Size(frente.width + 4f * u, 4f * u))
        drawRect(brillo(o, 0.7f, k + 0.1f), Offset(frente.left - 2f * u, frente.top - 2f * u), Size(frente.width + 4f * u, 1f * u))
        // El rótulo: una tira de cinta de papel con los extremos rotos.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        val cin = cinta(Offset(r.left - w * 0.02f, r.center.y), Offset(r.right + w * 0.02f, r.center.y), r.height, c.semilla)
        translate(1f * u, 1.5f * u) { drawPath(cin, sombra(o, 0.15f)) }
        drawPath(cin, if (o) mezcla(mezcla(HUESO, AMARILLO, 0.2f), NOCHE, 0.3f) else mezcla(HUESO, AMARILLO, 0.2f))
    }

    /** Una repisa sin tema: el vidrio vacío, la marca limpia donde hubo una caja, una punta de pipeta olvidada y el portaetiquetas sin tarjeta. */
    private fun DrawScope.repisaVacia(w: Float, h: Float, piso: Float, o: Boolean, k: Float) {
        val u = density
        // La marca: el polvo alrededor y el rectángulo limpio en el centro.
        val marca = Rect(w * 0.18f, piso + (h - piso) * 0.15f, w * 0.78f, h - (h - piso) * 0.2f)
        drawRect(luz(mezcla(CANTO_VIDRIO, HUESO_OSC, 0.55f), o, k), Offset(0f, piso), Size(w, h - piso))
        pinceladas(Rect(0f, piso, w, h), if (o) NOCHE else GRIS_OSC, 1390, 6, u, 0.12f)
        drawRect(luz(mezcla(CANTO_VIDRIO, BLANCO, 0.55f), o, k), marca.topLeft, marca.size)
        drawRect(sombra(o, 0.22f), marca.topLeft, marca.size, style = Stroke(1f * u))
        // Una punta de pipeta, tumbada.
        drawPath(Path().apply { moveTo(w * 0.82f, piso + (h - piso) * 0.5f); lineTo(w * 0.93f, piso + (h - piso) * 0.35f); lineTo(w * 0.935f, piso + (h - piso) * 0.55f); close() },
            luz(mezcla(AMARILLO, Color.White, 0.4f), o, k).copy(alpha = 0.85f))
        // El portaetiquetas de plástico, sin tarjeta, pegado al fondo.
        val pr = Rect(w * 0.3f, h * 0.62f, w * 0.66f, h * 0.76f)
        drawRect(sombra(o, 0.15f), Offset(pr.left + 1.5f * u, pr.top + 1.5f * u), pr.size)
        drawRect(luz(VIDRIO, o, k).copy(alpha = 0.35f), pr.topLeft, pr.size)
        drawRect(luz(GRIS, o, k), pr.topLeft, pr.size, style = Stroke(1f * u))
        drawLine(brillo(o, 0.6f, k), Offset(pr.left + 2f * u, pr.top + 2f * u), Offset(pr.left + pr.width * 0.4f, pr.top + 2f * u), 1f * u)
        drawRect(luz(GRIS, o, k), Offset(pr.left, pr.top - 2f * u), Size(pr.width, 2.5f * u))
    }

    override val rotulo = Rect(0.16f, 0.58f, 0.8f, 0.87f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.jost
    override val salida = SALIDA
    override val placa = Rect(0.33f, 0.0f, 0.67f, 0.22f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    /** Un matraz de Erlenmeyer con su líquido (la marca «me convence»). */
    private fun DrawScope.matraz(b: Offset, alto: Float, liquido: Color) {
        val u = density
        val ancho = alto * 0.7f
        val p = Path().apply { moveTo(b.x - ancho / 2, b.y); lineTo(b.x - ancho * 0.12f, b.y - alto * 0.62f); lineTo(b.x - ancho * 0.12f, b.y - alto); lineTo(b.x + ancho * 0.12f, b.y - alto); lineTo(b.x + ancho * 0.12f, b.y - alto * 0.62f); lineTo(b.x + ancho / 2, b.y); close() }
        clipPath(p) { drawRect(liquido.copy(alpha = 0.75f), Offset(b.x - ancho, b.y - alto * 0.38f), Size(ancho * 2, alto * 0.38f)) }
        drawPath(p, Color.White.copy(alpha = 0.25f))
        drawPath(p, GRIS_OSC, style = Stroke(1.2f * u))
    }

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // La bandeja blanca con su cuadrícula, unas gotas y una pipeta.
        val b = if (oscuro) mezcla(BLANCO, Color.Black, 0.6f) else BLANCO
        drawRect(b)
        val p = 20 * u
        var x = 0f
        while (x < w) { drawLine(GRIS.copy(alpha = 0.18f), Offset(x, 0f), Offset(x, h), 0.8f * u); x += p }
        var y = 0f
        while (y < h) { drawLine(GRIS.copy(alpha = 0.18f), Offset(0f, y), Offset(w, y), 0.8f * u); y += p }
        for (k in 0 until 8) drawCircle(CIAN.copy(alpha = 0.18f), (3f + 4f * azar(81, k)) * u, Offset(w * azar(82, k), h * azar(83, k)))
        rotate(-30f, Offset(w * 0.7f, h * 0.85f)) {
            drawRoundRect(GRIS.copy(alpha = 0.6f), Offset(w * 0.5f, h * 0.85f - 3 * u), Size(w * 0.36f, 6 * u), CornerRadius(3 * u))
            drawRoundRect(VIOLETA.copy(alpha = 0.6f), Offset(w * 0.86f, h * 0.85f - 6 * u), Size(18 * u, 12 * u), CornerRadius(6 * u))
        }
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(BLANCO_OSC, oscuro), r.topLeft, r.size) }
        drawRect(t(CIAN, oscuro), Offset(pl, pl), Size(w - 2 * pl, h - 2 * pl), style = Stroke(1.2f * u))
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) BLANCO else TINTA
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(3.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // El encabezado cian, las casillas de verificación en el margen y un código tenue.
        drawRect(CIAN, Offset.Zero, Size(w, 3 * u * g))
        val cs = 4.5f * u * g
        var y = 16 * u * g; var k = 0
        while (y < h - 10 * u * g) {
            drawRect(GRIS.copy(alpha = 0.6f), Offset(3 * u * g, y), Size(cs, cs), style = Stroke(0.7f * u))
            if (azar(semilla, k) < 0.5f) drawPath(Path().apply { moveTo(3 * u * g + cs * 0.2f, y + cs * 0.5f); lineTo(3 * u * g + cs * 0.45f, y + cs * 0.8f); lineTo(3 * u * g + cs * 0.9f, y + cs * 0.15f) }, CIAN, style = Stroke(0.9f * u * g))
            y += 14 * u * g; k++
        }
        val q = 1.6f * u * g
        val qx = w - 12 * u * g; val qy = h * 0.45f
        for (i in 0 until 6) for (j in 0 until 6) if (azar(semilla + 7, i * 6 + j) < 0.5f || (i < 2 && j < 2)) drawRect(TINTA.copy(alpha = 0.18f), Offset(qx + i * q, qy + j * q), Size(q, q))
    }

    override val margenes = MargenesFicha(13.dp, 16.dp, 12.dp, 26.dp, 30.dp, 26.dp)
    override val proporcionFicha = 0.88f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // Resultado reproducido.
            "hecho" -> selloHecho(CIAN, p.color, cuadrado = true)
            "convence" -> matraz(Offset(c.x, c.y + s * 0.44f), s * 0.88f, CIAN)
            // Una molécula: tres átomos enlazados.
            "piensa" -> {
                val a = Offset(c.x - s * 0.26f, c.y + s * 0.18f); val b = Offset(c.x + s * 0.26f, c.y + s * 0.18f); val d = Offset(c.x, c.y - s * 0.28f)
                listOf(a to b, b to d, d to a).forEach { (p1, p2) -> drawLine(VIOLETA, p1, p2, s * 0.06f) }
                listOf(a, b, d).forEach { drawCircle(VIOLETA, s * 0.13f, it) }
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(VIOLETA)
            else -> {}
        }
    }

    /** Un vial con su tapón rosa. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawRoundRect(VIDRIO, Offset(c.x - s * 0.16f, c.y - s * 0.2f), Size(s * 0.32f, s * 0.66f), CornerRadius(s * 0.12f))
        drawRoundRect(GRIS_OSC, Offset(c.x - s * 0.16f, c.y - s * 0.2f), Size(s * 0.32f, s * 0.66f), CornerRadius(s * 0.12f), style = Stroke(s * 0.04f))
        drawRoundRect(ROSA, Offset(c.x - s * 0.2f, c.y - s * 0.48f), Size(s * 0.4f, s * 0.3f), CornerRadius(s * 0.06f))
    }

    override val desorden = 0f
}
