package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los trofeos de cada tema, uno por categoría de logro (Diario, Mundo, Cuerpo, Mente, Cultura, Gente, Constancia,
 * Curiosos y Tuyos), en el metal de su nivel. En Roma son obras de ingeniería; en Grecia, columnas, ánforas y la lira;
 * en el scriptorium, capitulares, sellos y nudos; en la catedral, vitrales; en el Renacimiento, instrumentos; en el
 * Imperio, laureles y condecoraciones; en la Revolución, sus símbolos. Sin figuras humanas ni animales.
 */
object TrofeosTema {
    /** Cómo se llama el trofeo de cada categoría en cada tema (para la placa y el detalle). */
    fun nombre(tema: TemaId, categoria: String): String? = (NOMBRES[tema] ?: TrofeosMas.NOMBRES[tema])?.let { it[categoria] ?: it["Tuyos"] }

    private val NOMBRES: Map<TemaId, Map<String, String>> = mapOf(
        TemaId.ROMANO to mapOf("Diario" to "Columna conmemorativa", "Mundo" to "Miliario", "Cuerpo" to "Acueducto", "Mente" to "Panteón", "Cultura" to "Arco de triunfo",
            "Gente" to "Puente", "Constancia" to "Faro", "Curiosos" to "Anfiteatro", "Tuyos" to "Templo"),
        TemaId.HELENO to mapOf("Diario" to "Ánfora", "Mundo" to "Trirreme", "Cuerpo" to "Corona de olivo", "Mente" to "Columna jónica", "Cultura" to "Lira",
            "Gente" to "Crátera", "Constancia" to "Columna dórica", "Curiosos" to "Trípode", "Tuyos" to "Columna corintia"),
        TemaId.MEDIEVAL to mapOf("Diario" to "Capitular", "Mundo" to "Mapamundi", "Cuerpo" to "Sello de cera", "Mente" to "Nudo entrelazado", "Cultura" to "Códice",
            "Gente" to "Cenefa", "Constancia" to "Llave", "Curiosos" to "Tintero", "Tuyos" to "Rúbrica"),
        TemaId.GOTICO to mapOf("Diario" to "Vitral del libro", "Mundo" to "Rosetón", "Cuerpo" to "Vitral de la cruz", "Mente" to "Vitral de la estrella", "Cultura" to "Vitral del lirio",
            "Gente" to "Vitral de la rosa", "Constancia" to "Vitral de la campana", "Curiosos" to "Vitral del cáliz", "Tuyos" to "Vitral de la llave"),
        TemaId.RENACIMIENTO to mapOf("Diario" to "Tipos móviles", "Mundo" to "Esfera armilar", "Cuerpo" to "Compás y escuadra", "Mente" to "Dodecaedro", "Cultura" to "Cúpula",
            "Gente" to "Florín", "Constancia" to "Reloj de arena", "Curiosos" to "Nocturlabio", "Tuyos" to "Tornillo aéreo"),
        TemaId.NAPOLEON to mapOf("Diario" to "Código", "Mundo" to "Mapa de campaña", "Cuerpo" to "Tambor", "Mente" to "Columna triunfal", "Cultura" to "Arco de la Estrella",
            "Gente" to "Legión de Honor", "Constancia" to "Laurel", "Curiosos" to "Bicornio", "Tuyos" to "Monograma"),
        TemaId.REVOLUCION to mapOf("Diario" to "Declaración", "Mundo" to "Árbol de la libertad", "Cuerpo" to "Escarapela", "Mente" to "Balanza", "Cultura" to "Pluma",
            "Gente" to "Gorro frigio", "Constancia" to "Llave de la Bastilla", "Curiosos" to "Fasces", "Tuyos" to "Nivel"),
    )

    /** Lo que se necesita para dibujar: tamaño, si ya se logró y los tonos del metal. */
    class Pincel(val w: Float, val h: Float, val hecho: Boolean, val nivel: Int, val oscuro: Color, val base: Color, val brillo: Color, val sombra: Color) {
        fun x(f: Float) = w * f
        fun y(f: Float) = h * f
        fun metal(x0: Float, x1: Float): Brush = if (hecho) Brush.horizontalGradient(listOf(oscuro, base, brillo, base, oscuro), w * x0, w * x1) else SolidColor(sombra)
        val linea: Color get() = if (hecho) oscuro.copy(alpha = 0.7f) else sombra.copy(alpha = sombra.alpha * 1.6f)
        val claro: Color get() = if (hecho) lerp(brillo, Color.White, 0.25f) else sombra
    }

    // ================================================================== pedestal

    /** El pedestal de cada tema: piedra, mármol, madera de atril, marco del vitral, plinto con colgadura… */
    fun DrawScope.pedestal(tema: TemaId, p: Pincel, base: Color) {
        val w = size.width; val h = size.height
        val ped = if (p.hecho) base else p.sombra.copy(alpha = 0.08f)
        fun piedra(c: Color) = Brush.verticalGradient(listOf(lerp(c, Color.White, 0.18f), c, lerp(c, Color.Black, 0.2f)), h * 0.77f, h * 0.96f)
        drawRoundRect(Color.Black.copy(alpha = if (p.hecho) 0.18f else 0.05f), Offset(w * 0.12f, h * 0.955f), Size(w * 0.76f, h * 0.04f), CornerRadius(h * 0.02f))
        when (tema) {
            TemaId.ROMANO -> {
                // Basa de travertino con cornisa y plinto y, al frente, la tabla ansada de pórfido donde iría la dedicatoria.
                val c = if (p.hecho) Color(0xFFCDBD9A) else ped
                drawRect(piedra(c), Offset(w * 0.16f, h * 0.79f), Size(w * 0.68f, h * 0.15f))
                drawRect(piedra(c), Offset(w * 0.12f, h * 0.77f), Size(w * 0.76f, h * 0.025f))
                drawRect(piedra(lerp(c, Color.Black, 0.08f)), Offset(w * 0.1f, h * 0.935f), Size(w * 0.8f, h * 0.025f))
                if (p.hecho) {
                    val porfido = Color(0xFF7C2433); val y0 = h * 0.815f; val y1 = h * 0.905f
                    drawRect(porfido, Offset(w * 0.32f, y0), Size(w * 0.36f, y1 - y0))
                    listOf(-1f, 1f).forEach { s ->
                        val x = w * (0.5f + s * 0.18f)
                        drawPath(Path().apply { moveTo(x, y0 + (y1 - y0) * 0.3f); lineTo(x + s * w * 0.06f, y0); lineTo(x + s * w * 0.06f, y1); lineTo(x, y1 - (y1 - y0) * 0.3f); close() }, porfido)
                    }
                    drawLine(Color(0xFFD4AF37).copy(alpha = 0.8f), Offset(w * 0.36f, (y0 + y1) / 2), Offset(w * 0.64f, (y0 + y1) / 2), h * 0.008f)
                }
            }
            TemaId.RENACIMIENTO -> {
                // Pedestal de pietra serena: la losa moldurada sobre dos balaustres en forma de jarrón y el plinto abajo.
                val c = if (p.hecho) Color(0xFF8C9188) else ped
                listOf(0.32f, 0.68f).forEach { cx ->
                    drawPath(Path().apply {
                        moveTo(w * (cx - 0.04f), h * 0.805f); lineTo(w * (cx + 0.04f), h * 0.805f); lineTo(w * (cx + 0.025f), h * 0.825f)
                        cubicTo(w * (cx + 0.075f), h * 0.86f, w * (cx + 0.075f), h * 0.9f, w * (cx + 0.035f), h * 0.92f); lineTo(w * (cx + 0.05f), h * 0.935f)
                        lineTo(w * (cx - 0.05f), h * 0.935f); lineTo(w * (cx - 0.035f), h * 0.92f)
                        cubicTo(w * (cx - 0.075f), h * 0.9f, w * (cx - 0.075f), h * 0.86f, w * (cx - 0.025f), h * 0.825f); close()
                    }, piedra(c))
                }
                drawRect(piedra(lerp(c, Color.White, 0.15f)), Offset(w * 0.14f, h * 0.77f), Size(w * 0.72f, h * 0.035f))
                if (p.hecho) drawLine(lerp(c, Color.Black, 0.3f), Offset(w * 0.14f, h * 0.795f), Offset(w * 0.86f, h * 0.795f), h * 0.004f)
                drawRect(piedra(c), Offset(w * 0.12f, h * 0.935f), Size(w * 0.76f, h * 0.025f))
            }
            TemaId.HELENO -> {
                // Estilóbato de tres escalones de mármol.
                val c = if (p.hecho) Color(0xFFE8E2D4) else ped
                for (k in 0 until 3) drawRect(piedra(lerp(c, Color.Black, k * 0.06f)), Offset(w * (0.22f - k * 0.05f), h * (0.79f + k * 0.055f)), Size(w * (0.56f + k * 0.1f), h * 0.055f))
            }
            TemaId.MEDIEVAL -> {
                // Atril de madera inclinado.
                val c = if (p.hecho) Color(0xFF5A3A22) else ped
                drawPath(Path().apply { moveTo(w * 0.18f, h * 0.78f); lineTo(w * 0.82f, h * 0.74f); lineTo(w * 0.82f, h * 0.79f); lineTo(w * 0.18f, h * 0.83f); close() }, c)
                drawRect(c, Offset(w * 0.46f, h * 0.8f), Size(w * 0.08f, h * 0.14f)); drawRect(c, Offset(w * 0.3f, h * 0.935f), Size(w * 0.4f, h * 0.025f))
            }
            TemaId.GOTICO -> {
                // El alféizar de piedra bajo el vitral.
                val c = if (p.hecho) Color(0xFF6B6F7A) else ped
                drawRect(piedra(c), Offset(w * 0.14f, h * 0.84f), Size(w * 0.72f, h * 0.06f))
                drawRect(piedra(lerp(c, Color.Black, 0.1f)), Offset(w * 0.2f, h * 0.9f), Size(w * 0.6f, h * 0.05f))
            }
            TemaId.NAPOLEON -> {
                // Plinto de mármol verde con filete de oro.
                val c = if (p.hecho) Color(0xFF2F5D4A) else ped
                drawRect(piedra(c), Offset(w * 0.18f, h * 0.79f), Size(w * 0.64f, h * 0.16f))
                if (p.hecho) { drawRect(Color(0xFFD4AF37), Offset(w * 0.18f, h * 0.79f), Size(w * 0.64f, h * 0.012f)); drawRect(Color(0xFFD4AF37), Offset(w * 0.18f, h * 0.938f), Size(w * 0.64f, h * 0.012f)) }
                drawRect(Brush.horizontalGradient(listOf(p.oscuro, p.base, p.brillo, p.base, p.oscuro), w * 0.36f, w * 0.64f).takeIf { p.hecho } ?: SolidColor(p.sombra),
                    Offset(w * 0.36f, h * 0.845f), Size(w * 0.28f, h * 0.05f))
            }
            TemaId.REVOLUCION -> {
                // Plinto sencillo de piedra con una franja tricolor.
                val c = if (p.hecho) Color(0xFFD8D1C2) else ped
                drawRect(piedra(c), Offset(w * 0.2f, h * 0.79f), Size(w * 0.6f, h * 0.16f))
                if (p.hecho) listOf(Color(0xFF1F3A6E), Color.White, Color(0xFFB22234)).forEachIndexed { i, col -> drawRect(col, Offset(w * (0.2f + i * 0.2f), h * 0.79f), Size(w * 0.2f, h * 0.03f)) }
            }
            TemaId.ANDALUS -> with(TrofeosMas) { pedestalMas(tema, p, ped) }
            TemaId.SEDA -> with(TrofeosMas) { pedestalMas(tema, p, ped) }
            TemaId.MONGOL -> with(TrofeosMas) { pedestalMas(tema, p, ped) }
            TemaId.COLONOS -> with(TrofeosMas) { pedestalMas(tema, p, ped) }
            TemaId.BOTANICO -> with(TrofeosMas) { pedestalMas(tema, p, ped) }
            TemaId.NAVIDAD -> with(TrofeosMas) { pedestalMas(tema, p, ped) }
            TemaId.ANTIGUAS -> with(TrofeosMas) { pedestalMas(tema, p, ped) }
            TemaId.DISTOPIA -> with(TrofeosMas) { pedestalMas(tema, p, ped) }
            TemaId.CIBER -> with(TrofeosMas) { pedestalMas(tema, p, ped) }
            TemaId.NEON -> with(TrofeosMas) { pedestalMas(tema, p, ped) }
            TemaId.REFUGIO -> with(TrofeosMas) { pedestalMas(tema, p, ped) }
            TemaId.ORDEN -> with(TrofeosMas) { pedestalMas(tema, p, ped) }
            TemaId.LABORATORIO -> with(TrofeosMas) { pedestalMas(tema, p, ped) }
            TemaId.ESPACIO -> with(TrofeosMas) { pedestalMas(tema, p, ped) }
            TemaId.URSS -> with(TrofeosMas) { pedestalMas(tema, p, ped) }
        }
    }

    // ================================================================== figuras

    /** Dibuja la figura del tema: las de los siete primeros están aquí y las de los demás, en [TrofeosMas]. */
    fun DrawScope.figura(tema: TemaId, categoria: String, p: Pincel) {
        when (tema) {
            TemaId.ROMANO -> romano(categoria, p)
            TemaId.HELENO -> heleno(categoria, p)
            TemaId.MEDIEVAL -> medieval(categoria, p)
            TemaId.GOTICO -> gotico(categoria, p)
            TemaId.RENACIMIENTO -> renacimiento(categoria, p)
            TemaId.NAPOLEON -> napoleon(categoria, p)
            TemaId.REVOLUCION -> revolucion(categoria, p)
            TemaId.ANDALUS -> with(TrofeosMas) { andalus(categoria, p) }
            TemaId.SEDA -> with(TrofeosMas) { seda(categoria, p) }
            TemaId.MONGOL -> with(TrofeosMas) { mongol(categoria, p) }
            TemaId.COLONOS -> with(TrofeosMas) { colonos(categoria, p) }
            TemaId.BOTANICO -> with(TrofeosMas) { botanico(categoria, p) }
            TemaId.NAVIDAD -> with(TrofeosMas) { navidad(categoria, p) }
            TemaId.ANTIGUAS -> with(TrofeosMas) { antiguas(categoria, p) }
            TemaId.DISTOPIA -> with(TrofeosMas) { distopia(categoria, p) }
            TemaId.CIBER -> with(TrofeosMas) { terminal(categoria, p) }
            TemaId.NEON -> with(TrofeosMas) { terminal(categoria, p) }
            TemaId.REFUGIO -> with(TrofeosMas) { refugio(categoria, p) }
            TemaId.ORDEN -> with(TrofeosMas) { orden(categoria, p) }
            TemaId.LABORATORIO -> with(TrofeosMas) { laboratorio(categoria, p) }
            TemaId.ESPACIO -> with(TrofeosMas) { espacio(categoria, p) }
            TemaId.URSS -> with(TrofeosMas) { urss(categoria, p) }
        }
    }

    // ---------------------------------------------------------------- utilidades

    internal fun Pincel.r(x0: Float, y0: Float, x1: Float, y1: Float) = Rect(x(x0), y(y0), x(x1), y(y1))

    internal fun DrawScope.caja(p: Pincel, x0: Float, y0: Float, x1: Float, y1: Float, b: Brush = p.metal(x0, x1)) =
        drawRect(b, Offset(p.x(x0), p.y(y0)), Size(p.x(x1 - x0), p.y(y1 - y0)))

    /** Un arco de medio punto en un path: de [x0] a [x1], con los arranques en [yArranque] y los pies en [yPie]. */
    internal fun Path.arco(p: Pincel, x0: Float, x1: Float, yArranque: Float, yPie: Float) {
        moveTo(p.x(x0), p.y(yPie)); lineTo(p.x(x0), p.y(yArranque))
        arcTo(Rect(p.x(x0), p.y(yArranque) - p.x(x1 - x0) / 2, p.x(x1), p.y(yArranque) + p.x(x1 - x0) / 2), 180f, 180f, false)
        lineTo(p.x(x1), p.y(yPie)); close()
    }

    /** Una figura con huecos (arcos, vanos): el contorno menos los huecos. */
    internal fun DrawScope.conHuecos(p: Pincel, x0: Float, x1: Float, contorno: Path, huecos: Path.() -> Unit) {
        val todo = Path().apply { fillType = PathFillType.EvenOdd; addPath(contorno); huecos() }
        drawPath(todo, p.metal(x0, x1))
    }

    internal fun DrawScope.columna(p: Pincel, cx: Float, ancho: Float, y0: Float, y1: Float, estrias: Int = 3) {
        caja(p, cx - ancho / 2, y0, cx + ancho / 2, y1)
        if (p.hecho) for (k in 1..estrias) { val xx = cx - ancho / 2 + ancho * k / (estrias + 1); drawLine(p.linea.copy(alpha = 0.35f), Offset(p.x(xx), p.y(y0)), Offset(p.x(xx), p.y(y1)), p.w * 0.006f) }
    }

    internal fun DrawScope.texto(t: String, p: Pincel, cx: Float, cy: Float, tam: Float, color: Color, negrita: Boolean = true) {
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color.toArgb(); textSize = p.w * tam; textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.SERIF, if (negrita) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
        }
        drawContext.canvas.nativeCanvas.drawText(t, p.x(cx), p.y(cy) + paint.textSize * 0.35f, paint)
    }

    internal fun DrawScope.estrella(c: Offset, R: Float, r: Float, puntas: Int, b: Brush, giro: Double = -PI / 2) {
        val path = Path()
        for (k in 0 until puntas * 2) {
            val a = giro + k * PI / puntas
            val rr = if (k % 2 == 0) R else r
            val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
            if (k == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close(); drawPath(path, b)
    }

    internal fun DrawScope.laurel(p: Pincel, c: Offset, r: Float, b: Brush) {
        for (lado in listOf(-1f, 1f)) for (k in 0 until 9) {
            val a = (PI / 2 + lado * (0.3 + k * 0.3)).toFloat()
            val o = Offset(c.x + r * cos(a), c.y + r * sin(a))
            val g = a + lado * 1.15f; val l = r * 0.5f; val ww = r * 0.2f
            drawPath(Path().apply {
                moveTo(o.x - l / 2 * cos(g), o.y - l / 2 * sin(g))
                quadraticTo(o.x - ww * sin(g), o.y + ww * cos(g), o.x + l / 2 * cos(g), o.y + l / 2 * sin(g))
                quadraticTo(o.x + ww * sin(g), o.y - ww * cos(g), o.x - l / 2 * cos(g), o.y - l / 2 * sin(g)); close()
            }, b)
        }
    }

    // ================================================================== Roma: obras de ingeniería

    private fun DrawScope.romano(cat: String, p: Pincel) {
        val lw = p.w * 0.008f
        when (cat) {
            "Diario" -> {
                // Columna conmemorativa con su friso en espiral.
                caja(p, 0.36f, 0.70f, 0.64f, 0.77f); caja(p, 0.38f, 0.08f, 0.62f, 0.12f)
                columna(p, 0.5f, 0.17f, 0.12f, 0.70f, 0)
                if (p.hecho) { var yy = 0.14f; while (yy < 0.68f) { drawLine(p.linea, Offset(p.x(0.415f), p.y(yy + 0.05f)), Offset(p.x(0.585f), p.y(yy)), lw); yy += 0.05f } }
                drawOval(p.metal(0.44f, 0.56f), p.r(0.44f, 0.02f, 0.56f, 0.09f).let { Offset(it.left, it.top) }, Size(p.x(0.12f), p.y(0.07f)))
            }
            "Mundo" -> {
                // Miliario a la orilla de la calzada, con su inscripción.
                val calzada = Path().apply { moveTo(p.x(0.06f), p.y(0.77f)); lineTo(p.x(0.94f), p.y(0.77f)); lineTo(p.x(0.66f), p.y(0.6f)); lineTo(p.x(0.34f), p.y(0.6f)); close() }
                drawPath(calzada, if (p.hecho) SolidColor(lerp(p.base, Color.Black, 0.3f)) else SolidColor(p.sombra))
                if (p.hecho) for (k in 0 until 4) { val yy = 0.62f + k * 0.04f; drawLine(p.linea.copy(alpha = 0.4f), Offset(p.x(0.3f - k * 0.05f), p.y(yy)), Offset(p.x(0.7f + k * 0.05f), p.y(yy)), lw) }
                caja(p, 0.38f, 0.18f, 0.62f, 0.66f)
                drawOval(p.metal(0.38f, 0.62f), Offset(p.x(0.38f), p.y(0.15f)), Size(p.x(0.24f), p.y(0.06f)))
                if (p.hecho) texto("M·P", p, 0.5f, 0.3f, 0.1f, p.linea); if (p.hecho) texto("XII", p, 0.5f, 0.42f, 0.1f, p.linea)
            }
            "Cuerpo" -> {
                // Acueducto de dos pisos de arcos y el canal arriba.
                val contorno = Path().apply { addRect(p.r(0.06f, 0.2f, 0.94f, 0.75f)) }
                conHuecos(p, 0.06f, 0.94f, contorno) {
                    for (k in 0 until 3) { val x0 = 0.1f + k * 0.28f; arco(p, x0, x0 + 0.22f, 0.56f, 0.75f) }
                    for (k in 0 until 5) { val x0 = 0.09f + k * 0.172f; arco(p, x0, x0 + 0.13f, 0.33f, 0.47f) }
                }
                caja(p, 0.04f, 0.16f, 0.96f, 0.21f)
                if (p.hecho) drawLine(Color(0xFF5FA8D8), Offset(p.x(0.06f), p.y(0.175f)), Offset(p.x(0.94f), p.y(0.175f)), p.h * 0.01f)
            }
            "Mente" -> {
                // El Panteón: la cúpula con su óculo, el tambor, el frontón y las columnas del pórtico.
                drawArc(p.metal(0.18f, 0.82f), 180f, 180f, true, Offset(p.x(0.18f), p.y(0.14f)), Size(p.x(0.64f), p.x(0.64f)))
                caja(p, 0.18f, 0.14f + 0.32f * p.w / p.h, 0.82f, 0.5f)
                if (p.hecho) drawCircle(p.linea, p.w * 0.03f, Offset(p.x(0.5f), p.y(0.15f)))
                drawPath(Path().apply { moveTo(p.x(0.2f), p.y(0.52f)); lineTo(p.x(0.5f), p.y(0.42f)); lineTo(p.x(0.8f), p.y(0.52f)); close() }, p.metal(0.2f, 0.8f))
                for (k in 0 until 4) columna(p, 0.28f + k * 0.147f, 0.06f, 0.52f, 0.76f, 1)
            }
            "Cultura" -> {
                // Arco de triunfo con ático.
                val contorno = Path().apply { addRect(p.r(0.12f, 0.24f, 0.88f, 0.76f)) }
                conHuecos(p, 0.12f, 0.88f, contorno) { arco(p, 0.37f, 0.63f, 0.46f, 0.76f); arco(p, 0.17f, 0.29f, 0.58f, 0.76f); arco(p, 0.71f, 0.83f, 0.58f, 0.76f) }
                caja(p, 0.1f, 0.12f, 0.9f, 0.24f)
                if (p.hecho) { drawLine(p.linea, Offset(p.x(0.16f), p.y(0.18f)), Offset(p.x(0.84f), p.y(0.18f)), lw); for (k in 0 until 4) drawLine(p.linea.copy(alpha = 0.5f), Offset(p.x(0.155f + k * 0.23f), p.y(0.28f)), Offset(p.x(0.155f + k * 0.23f), p.y(0.74f)), lw * 2) }
            }
            "Gente" -> {
                // Puente de piedra de tres arcos sobre el río.
                if (p.hecho) drawRect(Brush.verticalGradient(listOf(Color(0xFF5FA8D8).copy(alpha = 0.5f), Color(0xFF2E6F96).copy(alpha = 0.6f)), p.y(0.66f), p.y(0.77f)), Offset(p.x(0.02f), p.y(0.66f)), Size(p.x(0.96f), p.y(0.11f)))
                val contorno = Path().apply { addRect(p.r(0.04f, 0.36f, 0.96f, 0.77f)) }
                conHuecos(p, 0.04f, 0.96f, contorno) { arco(p, 0.08f, 0.32f, 0.56f, 0.77f); arco(p, 0.38f, 0.62f, 0.52f, 0.77f); arco(p, 0.68f, 0.92f, 0.56f, 0.77f) }
                caja(p, 0.02f, 0.32f, 0.98f, 0.37f)
            }
            "Constancia" -> {
                // El faro: tres cuerpos que se angostan y el fuego arriba.
                caja(p, 0.28f, 0.48f, 0.72f, 0.77f); caja(p, 0.34f, 0.28f, 0.66f, 0.48f); caja(p, 0.40f, 0.15f, 0.60f, 0.28f)
                if (p.hecho) for (k in 0 until 3) drawRect(p.linea.copy(alpha = 0.6f), Offset(p.x(0.46f), p.y(0.52f + k * 0.07f)), Size(p.x(0.08f), p.y(0.035f)))
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.02f)); cubicTo(p.x(0.62f), p.y(0.08f), p.x(0.6f), p.y(0.14f), p.x(0.56f), p.y(0.15f)); lineTo(p.x(0.44f), p.y(0.15f)); cubicTo(p.x(0.4f), p.y(0.12f), p.x(0.44f), p.y(0.07f), p.x(0.5f), p.y(0.02f)); close() },
                    if (p.hecho) Brush.verticalGradient(listOf(Color(0xFFFFE27A), Color(0xFFFF8A2E)), p.y(0.02f), p.y(0.15f)) else SolidColor(p.sombra))
            }
            "Curiosos" -> {
                // Anfiteatro: dos pisos de arcos y el borde roto de un lado.
                val contorno = Path().apply {
                    moveTo(p.x(0.06f), p.y(0.76f)); lineTo(p.x(0.06f), p.y(0.3f)); quadraticTo(p.x(0.5f), p.y(0.2f), p.x(0.8f), p.y(0.26f))
                    lineTo(p.x(0.86f), p.y(0.4f)); lineTo(p.x(0.94f), p.y(0.44f)); lineTo(p.x(0.94f), p.y(0.76f)); close()
                }
                conHuecos(p, 0.06f, 0.94f, contorno) {
                    for (k in 0 until 5) { val x0 = 0.1f + k * 0.17f; arco(p, x0, x0 + 0.11f, 0.62f, 0.74f) }
                    for (k in 0 until 4) { val x0 = 0.1f + k * 0.17f; arco(p, x0, x0 + 0.11f, 0.42f, 0.53f) }
                }
            }
            else -> templo(p)
        }
    }

    private fun DrawScope.templo(p: Pincel) {
        drawPath(Path().apply { moveTo(p.x(0.12f), p.y(0.3f)); lineTo(p.x(0.5f), p.y(0.12f)); lineTo(p.x(0.88f), p.y(0.3f)); close() }, p.metal(0.12f, 0.88f))
        caja(p, 0.12f, 0.3f, 0.88f, 0.35f)
        for (k in 0 until 4) columna(p, 0.2f + k * 0.2f, 0.08f, 0.35f, 0.7f, 2)
        caja(p, 0.1f, 0.7f, 0.9f, 0.735f); caja(p, 0.07f, 0.735f, 0.93f, 0.77f)
    }

    // ================================================================== Grecia

    private fun DrawScope.heleno(cat: String, p: Pincel) {
        val lw = p.w * 0.01f
        when (cat) {
            "Diario" -> {
                // Ánfora de estilo geométrico, con su meandro.
                val cuerpo = Path().apply {
                    moveTo(p.x(0.42f), p.y(0.08f)); lineTo(p.x(0.58f), p.y(0.08f)); lineTo(p.x(0.56f), p.y(0.2f))
                    cubicTo(p.x(0.8f), p.y(0.26f), p.x(0.78f), p.y(0.56f), p.x(0.56f), p.y(0.7f)); lineTo(p.x(0.58f), p.y(0.76f)); lineTo(p.x(0.42f), p.y(0.76f)); lineTo(p.x(0.44f), p.y(0.7f))
                    cubicTo(p.x(0.22f), p.y(0.56f), p.x(0.2f), p.y(0.26f), p.x(0.44f), p.y(0.2f)); close()
                }
                drawPath(cuerpo, p.metal(0.22f, 0.78f))
                drawArc(p.metal(0.25f, 0.42f), 100f, 160f, false, Offset(p.x(0.26f), p.y(0.12f)), Size(p.x(0.2f), p.y(0.16f)), style = Stroke(lw * 1.6f))
                drawArc(p.metal(0.58f, 0.75f), -80f, 160f, false, Offset(p.x(0.54f), p.y(0.12f)), Size(p.x(0.2f), p.y(0.16f)), style = Stroke(lw * 1.6f))
                if (p.hecho) {
                    val u = 0.035f; var xx = 0.3f; val yy = 0.38f; val path = Path()
                    while (xx + u * 3 <= 0.72f) { path.moveTo(p.x(xx), p.y(yy + u * 1.4f)); path.lineTo(p.x(xx), p.y(yy)); path.lineTo(p.x(xx + u * 2), p.y(yy)); path.lineTo(p.x(xx + u * 2), p.y(yy + u)); path.lineTo(p.x(xx + u), p.y(yy + u)); xx += u * 3 }
                    drawPath(path, p.linea, style = Stroke(lw * 0.7f))
                    drawLine(p.linea, Offset(p.x(0.28f), p.y(0.34f)), Offset(p.x(0.72f), p.y(0.34f)), lw * 0.6f); drawLine(p.linea, Offset(p.x(0.28f), p.y(0.47f)), Offset(p.x(0.72f), p.y(0.47f)), lw * 0.6f)
                }
            }
            "Mundo" -> {
                // Trirreme: casco, vela cuadrada, remos y olas.
                drawPath(Path().apply { moveTo(p.x(0.06f), p.y(0.5f)); lineTo(p.x(0.94f), p.y(0.46f)); lineTo(p.x(0.84f), p.y(0.6f)); lineTo(p.x(0.18f), p.y(0.6f)); close() }, p.metal(0.06f, 0.94f))
                drawLine(p.linea, Offset(p.x(0.5f), p.y(0.48f)), Offset(p.x(0.5f), p.y(0.1f)), lw)
                drawPath(Path().apply { moveTo(p.x(0.3f), p.y(0.14f)); lineTo(p.x(0.7f), p.y(0.14f)); lineTo(p.x(0.72f), p.y(0.42f)); lineTo(p.x(0.28f), p.y(0.42f)); close() }, if (p.hecho) SolidColor(p.claro) else SolidColor(p.sombra))
                for (k in 0 until 7) { val xx = 0.22f + k * 0.09f; drawLine(p.linea, Offset(p.x(xx), p.y(0.58f)), Offset(p.x(xx - 0.05f), p.y(0.7f)), lw * 0.6f) }
                if (p.hecho) drawCircle(p.linea, p.w * 0.015f, Offset(p.x(0.88f), p.y(0.5f)))
                for (k in 0 until 4) drawArc(if (p.hecho) Color(0xFF5FA8D8) else p.sombra, 0f, 180f, false, Offset(p.x(0.1f + k * 0.2f), p.y(0.7f)), Size(p.x(0.2f), p.y(0.05f)), style = Stroke(lw))
            }
            "Cuerpo" -> laurel(p, Offset(p.x(0.5f), p.y(0.4f)), p.w * 0.3f, p.metal(0.2f, 0.8f))
            "Mente" -> {
                columna(p, 0.5f, 0.2f, 0.2f, 0.74f, 4)
                // Capitel jónico: el ábaco y sus dos volutas.
                caja(p, 0.28f, 0.14f, 0.72f, 0.2f)
                listOf(0.32f, 0.68f).forEach { cx -> drawCircle(p.metal(cx - 0.06f, cx + 0.06f), p.w * 0.06f, Offset(p.x(cx), p.y(0.22f)), style = Stroke(lw * 1.2f)); drawCircle(p.linea, p.w * 0.015f, Offset(p.x(cx), p.y(0.22f))) }
                caja(p, 0.36f, 0.74f, 0.64f, 0.77f)
            }
            "Cultura" -> {
                // La lira: dos brazos curvos, el travesaño y las cuerdas.
                drawPath(Path().apply { moveTo(p.x(0.32f), p.y(0.7f)); cubicTo(p.x(0.14f), p.y(0.5f), p.x(0.36f), p.y(0.3f), p.x(0.26f), p.y(0.08f)) }, p.metal(0.14f, 0.4f), style = Stroke(lw * 2.4f, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(p.x(0.68f), p.y(0.7f)); cubicTo(p.x(0.86f), p.y(0.5f), p.x(0.64f), p.y(0.3f), p.x(0.74f), p.y(0.08f)) }, p.metal(0.6f, 0.86f), style = Stroke(lw * 2.4f, cap = StrokeCap.Round))
                caja(p, 0.24f, 0.16f, 0.76f, 0.2f)
                drawOval(p.metal(0.28f, 0.72f), Offset(p.x(0.28f), p.y(0.62f)), Size(p.x(0.44f), p.y(0.14f)))
                if (p.hecho) for (k in 0 until 5) { val xx = 0.38f + k * 0.06f; drawLine(p.claro, Offset(p.x(xx), p.y(0.2f)), Offset(p.x(xx), p.y(0.64f)), lw * 0.4f) }
            }
            "Gente" -> {
                // Crátera: la vasija grande para mezclar el vino y compartirlo.
                drawPath(Path().apply {
                    moveTo(p.x(0.18f), p.y(0.18f)); lineTo(p.x(0.82f), p.y(0.18f)); cubicTo(p.x(0.78f), p.y(0.42f), p.x(0.66f), p.y(0.56f), p.x(0.56f), p.y(0.6f))
                    lineTo(p.x(0.58f), p.y(0.72f)); lineTo(p.x(0.42f), p.y(0.72f)); lineTo(p.x(0.44f), p.y(0.6f)); cubicTo(p.x(0.34f), p.y(0.56f), p.x(0.22f), p.y(0.42f), p.x(0.18f), p.y(0.18f)); close()
                }, p.metal(0.18f, 0.82f))
                caja(p, 0.36f, 0.72f, 0.64f, 0.76f)
                listOf(0.2f to 0.12f, 0.8f to 0.88f).forEach { (a, b) -> drawLine(p.linea, Offset(p.x(a), p.y(0.3f)), Offset(p.x(b), p.y(0.36f)), lw * 1.4f) }
                if (p.hecho) drawLine(p.linea, Offset(p.x(0.22f), p.y(0.28f)), Offset(p.x(0.78f), p.y(0.28f)), lw * 0.6f)
            }
            "Constancia" -> {
                columna(p, 0.5f, 0.22f, 0.2f, 0.74f, 5)
                // Capitel dórico: equino y ábaco, sin adornos.
                drawPath(Path().apply { moveTo(p.x(0.38f), p.y(0.2f)); lineTo(p.x(0.62f), p.y(0.2f)); lineTo(p.x(0.68f), p.y(0.16f)); lineTo(p.x(0.32f), p.y(0.16f)); close() }, p.metal(0.32f, 0.68f))
                caja(p, 0.3f, 0.12f, 0.7f, 0.16f); caja(p, 0.34f, 0.74f, 0.66f, 0.77f)
            }
            "Curiosos" -> {
                // El trípode de Delfos: el caldero sobre tres patas.
                drawArc(p.metal(0.24f, 0.76f), 0f, 180f, true, Offset(p.x(0.24f), p.y(0.12f)), Size(p.x(0.52f), p.y(0.3f)))
                caja(p, 0.22f, 0.25f, 0.78f, 0.28f)
                listOf(0.3f to 0.18f, 0.5f to 0.5f, 0.7f to 0.82f).forEach { (a, b) -> drawLine(p.linea, Offset(p.x(a), p.y(0.4f)), Offset(p.x(b), p.y(0.76f)), lw * 1.6f) }
                listOf(0.38f, 0.62f).forEach { cx -> drawCircle(p.metal(cx - 0.05f, cx + 0.05f), p.w * 0.05f, Offset(p.x(cx), p.y(0.1f)), style = Stroke(lw)) }
            }
            else -> {
                columna(p, 0.5f, 0.2f, 0.26f, 0.74f, 4)
                // Capitel corintio: hojas de acanto en dos filas.
                for (fila in 0 until 2) for (k in 0 until 4) {
                    val cx = 0.33f + k * 0.113f; val yy = 0.26f - fila * 0.07f
                    drawPath(Path().apply { moveTo(p.x(cx - 0.05f), p.y(yy)); quadraticTo(p.x(cx), p.y(yy - 0.12f), p.x(cx + 0.05f), p.y(yy)); close() }, p.metal(cx - 0.05f, cx + 0.05f))
                }
                caja(p, 0.28f, 0.1f, 0.72f, 0.13f); caja(p, 0.34f, 0.74f, 0.66f, 0.77f)
            }
        }
    }

    // ================================================================== Scriptorium

    private val ROJO = Color(0xFFB0281E)
    private val AZUL = Color(0xFF1F3F8F)

    private fun DrawScope.medieval(cat: String, p: Pincel) {
        val lw = p.w * 0.01f
        val rojo = if (p.hecho) ROJO else p.sombra; val azul = if (p.hecho) AZUL else p.sombra
        when (cat) {
            "Diario" -> {
                // Capitular iluminada: marco de oro, fondo azul y la letra en rojo con zarcillos.
                caja(p, 0.16f, 0.12f, 0.84f, 0.72f)
                drawRect(azul, Offset(p.x(0.21f), p.y(0.16f)), Size(p.x(0.58f), p.y(0.52f)))
                if (p.hecho) for (k in 0 until 6) drawCircle(Color.White.copy(alpha = 0.6f), p.w * 0.008f, Offset(p.x(0.26f + k * 0.1f), p.y(0.2f)))
                texto("V", p, 0.5f, 0.4f, 0.55f, if (p.hecho) lerp(ROJO, Color.White, 0.15f) else p.sombra)
                drawPath(Path().apply { moveTo(p.x(0.84f), p.y(0.6f)); cubicTo(p.x(0.95f), p.y(0.62f), p.x(0.92f), p.y(0.74f), p.x(0.84f), p.y(0.72f)) }, rojo, style = Stroke(lw))
            }
            "Mundo" -> {
                // Mapamundi en T: el océano alrededor y tres partes del mundo.
                val c = Offset(p.x(0.5f), p.y(0.4f)); val R = p.w * 0.34f
                drawCircle(if (p.hecho) SolidColor(Color(0xFF5F8FC8)) else SolidColor(p.sombra), R, c)
                drawCircle(p.metal(0.2f, 0.8f), R * 0.8f, c)
                drawLine(azul, Offset(c.x - R * 0.8f, c.y), Offset(c.x + R * 0.8f, c.y), lw * 1.6f)
                drawLine(azul, Offset(c.x, c.y), Offset(c.x, c.y + R * 0.8f), lw * 1.6f)
                if (p.hecho) drawCircle(rojo, p.w * 0.025f, Offset(c.x, c.y - R * 0.4f))
                caja(p, 0.46f, 0.73f, 0.54f, 0.77f)
            }
            "Cuerpo" -> {
                // Sello de lacre con sus cintas.
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.5f)); lineTo(p.x(0.5f + s * 0.14f), p.y(0.76f)); lineTo(p.x(0.5f + s * 0.06f), p.y(0.7f)); lineTo(p.x(0.5f + s * 0.02f), p.y(0.76f)); close() }, if (p.hecho) SolidColor(AZUL) else SolidColor(p.sombra)) }
                val c = Offset(p.x(0.5f), p.y(0.38f))
                drawCircle(if (p.hecho) Brush.radialGradient(listOf(lerp(ROJO, Color.White, 0.25f), ROJO, lerp(ROJO, Color.Black, 0.35f)), c, p.w * 0.3f) else SolidColor(p.sombra), p.w * 0.27f, c)
                drawCircle(p.metal(0.3f, 0.7f), p.w * 0.19f, c, style = Stroke(lw))
                estrella(c, p.w * 0.13f, p.w * 0.055f, 6, p.metal(0.38f, 0.62f))
            }
            "Mente" -> {
                // Nudo entrelazado: dos lazos que pasan uno por encima del otro.
                val c = Offset(p.x(0.5f), p.y(0.4f)); val r = p.w * 0.17f
                drawCircle(p.metal(0.2f, 0.66f), r, Offset(c.x - r * 0.55f, c.y), style = Stroke(lw * 2.4f))
                drawCircle(p.metal(0.34f, 0.8f), r, Offset(c.x + r * 0.55f, c.y), style = Stroke(lw * 2.4f))
                drawCircle(p.metal(0.3f, 0.7f), r, Offset(c.x, c.y - r * 0.55f), style = Stroke(lw * 2.4f))
                drawCircle(p.metal(0.3f, 0.7f), r, Offset(c.x, c.y + r * 0.55f), style = Stroke(lw * 2.4f))
                caja(p, 0.46f, 0.68f, 0.54f, 0.77f)
            }
            "Cultura" -> {
                // Códice cerrado de pie, con broches.
                caja(p, 0.26f, 0.1f, 0.74f, 0.74f)
                drawRect(rojo, Offset(p.x(0.3f), p.y(0.14f)), Size(p.x(0.4f), p.y(0.56f)), style = Stroke(lw))
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.28f)); lineTo(p.x(0.6f), p.y(0.42f)); lineTo(p.x(0.5f), p.y(0.56f)); lineTo(p.x(0.4f), p.y(0.42f)); close() }, rojo, style = Stroke(lw))
                listOf(0.3f, 0.56f).forEach { yy -> drawRect(p.linea, Offset(p.x(0.72f), p.y(yy)), Size(p.x(0.06f), p.y(0.04f))) }
            }
            "Gente" -> {
                // Cenefa: un tallo en círculo con hojas y frutos.
                val c = Offset(p.x(0.5f), p.y(0.4f)); val R = p.w * 0.3f
                drawCircle(p.metal(0.2f, 0.8f), R, c, style = Stroke(lw * 1.4f))
                for (k in 0 until 10) {
                    val a = k * PI / 5; val o = Offset(c.x + (R * cos(a)).toFloat(), c.y + (R * sin(a)).toFloat())
                    if (k % 2 == 0) drawOval(if (p.hecho) SolidColor(Color(0xFF3F7A3A)) else SolidColor(p.sombra), Offset(o.x - p.w * 0.04f, o.y - p.w * 0.025f), Size(p.w * 0.08f, p.w * 0.05f))
                    else drawCircle(rojo, p.w * 0.022f, o)
                }
                caja(p, 0.46f, 0.7f, 0.54f, 0.77f)
            }
            "Constancia" -> {
                // Llave de hierro con su paletón.
                drawCircle(p.metal(0.34f, 0.66f), p.w * 0.13f, Offset(p.x(0.5f), p.y(0.2f)), style = Stroke(lw * 2.2f))
                caja(p, 0.47f, 0.28f, 0.53f, 0.7f)
                caja(p, 0.53f, 0.56f, 0.66f, 0.6f); caja(p, 0.53f, 0.64f, 0.62f, 0.68f)
            }
            "Curiosos" -> {
                // Tintero y pluma.
                drawRoundRect(p.metal(0.3f, 0.7f), Offset(p.x(0.3f), p.y(0.5f)), Size(p.x(0.4f), p.y(0.24f)), CornerRadius(p.w * 0.04f))
                drawRect(if (p.hecho) SolidColor(Color(0xFF14110C)) else SolidColor(p.sombra), Offset(p.x(0.4f), p.y(0.46f)), Size(p.x(0.2f), p.y(0.05f)))
                drawPath(Path().apply { moveTo(p.x(0.48f), p.y(0.48f)); cubicTo(p.x(0.5f), p.y(0.32f), p.x(0.62f), p.y(0.14f), p.x(0.84f), p.y(0.04f)); cubicTo(p.x(0.74f), p.y(0.22f), p.x(0.62f), p.y(0.4f), p.x(0.5f), p.y(0.48f)); close() },
                    if (p.hecho) SolidColor(lerp(Color.White, p.base, 0.3f)) else SolidColor(p.sombra))
            }
            else -> texto("¶", p, 0.5f, 0.38f, 0.62f, rojo)
        }
    }

    // ================================================================== Catedral: vitrales

    /** Colores del cristal por nivel: ámbar (bronce), zafiro (plata), rubí y oro (oro), y todos (legendario). */
    private fun vidrios(nivel: Int): List<Color> = when (nivel) {
        1 -> listOf(Color(0xFFD08A2E), Color(0xFFE9B85A), Color(0xFF8C4A1A))
        2 -> listOf(Color(0xFF2A4FB0), Color(0xFF6E8FE0), Color(0xFF1A2E6E))
        3 -> listOf(Color(0xFFB3122E), Color(0xFFE2B34A), Color(0xFF1F3A9E))
        else -> listOf(Color(0xFFB3122E), Color(0xFF1F3A9E), Color(0xFFE2B34A), Color(0xFF2E7D4A), Color(0xFF6A3A9E))
    }

    private fun DrawScope.gotico(cat: String, p: Pincel) {
        // La ojiva del vitral: dos arcos que se juntan en punta.
        val x0 = p.x(0.18f); val x1 = p.x(0.82f); val yTop = p.y(0.02f); val yArr = p.y(0.28f); val yPie = p.y(0.8f)
        val ojiva = Path().apply {
            moveTo(x0, yPie); lineTo(x0, yArr)
            quadraticTo(x0, yTop + (yArr - yTop) * 0.3f, (x0 + x1) / 2, yTop)
            quadraticTo(x1, yTop + (yArr - yTop) * 0.3f, x1, yArr); lineTo(x1, yPie); close()
        }
        val v = vidrios(p.nivel)
        val plomo = Color(0xFF1C1A22)
        clipPath(ojiva) {
            // El fondo de cristal en rombos de dos tonos.
            val paso = p.w * 0.11f
            var yy = yTop; var fila = 0
            while (yy < yPie) {
                var xx = x0 - (if (fila % 2 == 0) 0f else paso / 2); var k = 0
                while (xx < x1) {
                    val c = if (p.hecho) v[(k + fila) % 2 + 0].copy(alpha = 0.85f) else p.sombra
                    drawPath(Path().apply { moveTo(xx + paso / 2, yy); lineTo(xx + paso, yy + paso / 2); lineTo(xx + paso / 2, yy + paso); lineTo(xx, yy + paso / 2); close() }, c)
                    xx += paso; k++
                }
                yy += paso / 2; fila++
            }
            if (p.hecho) drawRect(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent), Offset(p.x(0.4f), p.y(0.2f)), p.w * 0.5f), Offset(x0, yTop), Size(x1 - x0, yPie - yTop))
        }
        // El motivo, en cristal de color con su plomo.
        val c = Offset(p.x(0.5f), p.y(0.42f))
        val fondo = if (p.hecho) v.getOrElse(2) { v[0] } else p.sombra.copy(alpha = p.sombra.alpha * 1.8f)
        val acento = if (p.hecho) (if (p.nivel >= 3) Color(0xFFE2B34A) else lerp(v[1], Color.White, 0.3f)) else p.sombra.copy(alpha = p.sombra.alpha * 2.4f)
        val lw = p.w * 0.012f
        when (cat) {
            "Diario" -> {
                drawPath(Path().apply { moveTo(c.x, c.y - p.y(0.12f)); lineTo(p.x(0.28f), c.y - p.y(0.14f)); lineTo(p.x(0.28f), c.y + p.y(0.12f)); lineTo(c.x, c.y + p.y(0.14f)); close() }, acento)
                drawPath(Path().apply { moveTo(c.x, c.y - p.y(0.12f)); lineTo(p.x(0.72f), c.y - p.y(0.14f)); lineTo(p.x(0.72f), c.y + p.y(0.12f)); lineTo(c.x, c.y + p.y(0.14f)); close() }, acento)
                drawLine(plomo, Offset(c.x, c.y - p.y(0.12f)), Offset(c.x, c.y + p.y(0.14f)), lw)
            }
            "Mundo" -> {
                // Rosetón: pétalos en círculo con el centro.
                val R = p.w * 0.24f
                drawCircle(fondo, R, c)
                for (k in 0 until 12) { val a = k * PI / 6; drawCircle(if (k % 2 == 0) acento else v[0], R * 0.22f, Offset(c.x + (R * 0.7f * cos(a)).toFloat(), c.y + (R * 0.7f * sin(a)).toFloat())) }
                drawCircle(acento, R * 0.28f, c)
                for (k in 0 until 12) { val a = k * PI / 6; drawLine(plomo, c, Offset(c.x + (R * cos(a)).toFloat(), c.y + (R * sin(a)).toFloat()), lw * 0.6f) }
                drawCircle(plomo, R, c, style = Stroke(lw))
            }
            "Cuerpo" -> { caja(p, 0.44f, 0.18f, 0.56f, 0.66f, SolidColor(acento)); caja(p, 0.3f, 0.32f, 0.7f, 0.42f, SolidColor(acento)); drawRect(plomo, Offset(p.x(0.44f), p.y(0.18f)), Size(p.x(0.12f), p.y(0.48f)), style = Stroke(lw)) }
            "Mente" -> { estrella(c, p.w * 0.24f, p.w * 0.1f, 8, SolidColor(acento)); drawCircle(fondo, p.w * 0.07f, c) }
            "Cultura" -> {
                // Flor de lis.
                drawPath(Path().apply { moveTo(c.x, c.y - p.y(0.2f)); quadraticTo(c.x + p.x(0.1f), c.y - p.y(0.06f), c.x, c.y + p.y(0.06f)); quadraticTo(c.x - p.x(0.1f), c.y - p.y(0.06f), c.x, c.y - p.y(0.2f)); close() }, acento)
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(c.x + s * p.x(0.03f), c.y + p.y(0.02f)); quadraticTo(c.x + s * p.x(0.24f), c.y - p.y(0.12f), c.x + s * p.x(0.18f), c.y + p.y(0.1f)); quadraticTo(c.x + s * p.x(0.1f), c.y + p.y(0.02f), c.x + s * p.x(0.03f), c.y + p.y(0.02f)); close() }, acento) }
                caja(p, 0.36f, 0.47f, 0.64f, 0.51f, SolidColor(fondo))
            }
            "Gente" -> {
                // Rosa de cinco pétalos.
                for (k in 0 until 5) { val a = -PI / 2 + k * 2 * PI / 5; drawCircle(acento, p.w * 0.1f, Offset(c.x + (p.w * 0.11f * cos(a)).toFloat(), c.y + (p.w * 0.11f * sin(a)).toFloat())) }
                drawCircle(fondo, p.w * 0.07f, c)
            }
            "Constancia" -> {
                // Campana.
                drawPath(Path().apply { moveTo(c.x - p.x(0.16f), c.y + p.y(0.14f)); cubicTo(c.x - p.x(0.12f), c.y - p.y(0.02f), c.x - p.x(0.1f), c.y - p.y(0.16f), c.x, c.y - p.y(0.17f)); cubicTo(c.x + p.x(0.1f), c.y - p.y(0.16f), c.x + p.x(0.12f), c.y - p.y(0.02f), c.x + p.x(0.16f), c.y + p.y(0.14f)); close() }, acento)
                drawCircle(fondo, p.w * 0.03f, Offset(c.x, c.y + p.y(0.17f)))
            }
            "Curiosos" -> {
                // Cáliz.
                drawPath(Path().apply { moveTo(c.x - p.x(0.16f), c.y - p.y(0.16f)); lineTo(c.x + p.x(0.16f), c.y - p.y(0.16f)); quadraticTo(c.x + p.x(0.14f), c.y + p.y(0.02f), c.x, c.y + p.y(0.03f)); quadraticTo(c.x - p.x(0.14f), c.y + p.y(0.02f), c.x - p.x(0.16f), c.y - p.y(0.16f)); close() }, acento)
                caja(p, 0.48f, 0.45f, 0.52f, 0.54f, SolidColor(acento)); drawOval(acento, Offset(c.x - p.x(0.12f), c.y + p.y(0.11f)), Size(p.x(0.24f), p.y(0.04f)))
            }
            else -> {
                drawCircle(acento, p.w * 0.08f, Offset(c.x, c.y - p.y(0.1f)), style = Stroke(lw * 2))
                caja(p, 0.48f, 0.37f, 0.52f, 0.6f, SolidColor(acento)); caja(p, 0.52f, 0.52f, 0.6f, 0.55f, SolidColor(acento))
            }
        }
        // El marco de piedra y el plomo del contorno.
        drawPath(ojiva, plomo, style = Stroke(lw * 1.4f))
        drawPath(ojiva, if (p.hecho) SolidColor(Color(0xFF8C8F99)) else SolidColor(p.sombra), style = Stroke(lw * 0.6f))
        // El parteluz y la tracería de abajo.
        drawLine(plomo, Offset(x0, p.y(0.62f)), Offset(x1, p.y(0.62f)), lw * 0.8f)
        drawLine(plomo, Offset(p.x(0.5f), p.y(0.62f)), Offset(p.x(0.5f), yPie), lw * 0.8f)
    }

    // ================================================================== Renacimiento: instrumentos

    private fun DrawScope.renacimiento(cat: String, p: Pincel) {
        val lw = p.w * 0.012f
        when (cat) {
            "Diario" -> {
                // Un tipo móvil de pie, con su letra en relieve, junto a una hoja impresa.
                drawRect(if (p.hecho) SolidColor(Color(0xFFF2E6C8)) else SolidColor(p.sombra), Offset(p.x(0.12f), p.y(0.2f)), Size(p.x(0.36f), p.y(0.5f)))
                if (p.hecho) for (k in 0 until 8) drawLine(Color(0xFF5A3A1C).copy(alpha = 0.5f), Offset(p.x(0.16f), p.y(0.26f + k * 0.05f)), Offset(p.x(0.44f), p.y(0.26f + k * 0.05f)), lw * 0.5f)
                caja(p, 0.54f, 0.24f, 0.84f, 0.74f)
                texto("A", p, 0.69f, 0.16f, 0.26f, p.linea)
            }
            "Mundo" -> {
                // Esfera armilar: anillos alrededor de un globo pequeño.
                val c = Offset(p.x(0.5f), p.y(0.38f)); val R = p.w * 0.3f
                drawCircle(p.metal(0.2f, 0.8f), R, c, style = Stroke(lw * 1.4f))
                drawOval(p.metal(0.2f, 0.8f), Offset(c.x - R, c.y - R * 0.3f), Size(R * 2, R * 0.6f), style = Stroke(lw))
                drawOval(p.metal(0.35f, 0.65f), Offset(c.x - R * 0.3f, c.y - R), Size(R * 0.6f, R * 2), style = Stroke(lw))
                drawLine(p.linea, Offset(c.x - R * 0.7f, c.y + R * 0.7f), Offset(c.x + R * 0.7f, c.y - R * 0.7f), lw)
                drawCircle(p.metal(0.44f, 0.56f), R * 0.18f, c)
                caja(p, 0.48f, 0.38f + 0.3f * p.w / p.h, 0.52f, 0.74f); caja(p, 0.36f, 0.74f, 0.64f, 0.77f)
            }
            "Cuerpo" -> {
                // Compás abierto sobre una escuadra.
                drawPath(Path().apply { moveTo(p.x(0.14f), p.y(0.74f)); lineTo(p.x(0.14f), p.y(0.32f)); lineTo(p.x(0.2f), p.y(0.32f)); lineTo(p.x(0.2f), p.y(0.68f)); lineTo(p.x(0.86f), p.y(0.68f)); lineTo(p.x(0.86f), p.y(0.74f)); close() }, p.metal(0.14f, 0.86f))
                drawLine(p.linea, Offset(p.x(0.5f), p.y(0.1f)), Offset(p.x(0.32f), p.y(0.66f)), lw * 1.6f)
                drawLine(p.linea, Offset(p.x(0.5f), p.y(0.1f)), Offset(p.x(0.7f), p.y(0.66f)), lw * 1.6f)
                drawCircle(p.metal(0.46f, 0.54f), p.w * 0.04f, Offset(p.x(0.5f), p.y(0.1f)))
                drawArc(p.linea.copy(alpha = 0.5f), 20f, 140f, false, Offset(p.x(0.26f), p.y(0.36f)), Size(p.x(0.48f), p.y(0.36f)), style = Stroke(lw * 0.5f))
            }
            "Mente" -> {
                // Dodecaedro en alambre: un pentágono al centro y sus caras alrededor.
                val c = Offset(p.x(0.5f), p.y(0.4f)); val R = p.w * 0.32f
                fun anillo(r: Float, giro: Double) = (0 until 5).map { k -> val a = giro + k * 2 * PI / 5; Offset(c.x + (r * cos(a)).toFloat(), c.y + (r * sin(a)).toFloat()) }
                val fuera = anillo(R, -PI / 2); val medio = anillo(R * 0.72f, -PI / 2 + PI / 5); val dentro = anillo(R * 0.42f, -PI / 2)
                drawPath(Path().apply { fuera.forEachIndexed { i, o -> if (i == 0) moveTo(o.x, o.y) else lineTo(o.x, o.y) }; close() }, if (p.hecho) SolidColor(p.base.copy(alpha = 0.25f)) else SolidColor(p.sombra))
                val linea = p.metal(0.2f, 0.8f)
                for (i in 0 until 5) {
                    drawLine(linea, fuera[i], fuera[(i + 1) % 5], lw); drawLine(linea, dentro[i], dentro[(i + 1) % 5], lw)
                    drawLine(linea, fuera[i], medio[(i + 4) % 5], lw * 0.8f); drawLine(linea, medio[i], dentro[i], lw * 0.8f); drawLine(linea, medio[i], dentro[(i + 1) % 5], lw * 0.8f)
                }
            }
            "Cultura" -> {
                // La cúpula de ocho gajos con su linterna.
                val c = Offset(p.x(0.5f), p.y(0.56f))
                drawPath(Path().apply { moveTo(p.x(0.16f), c.y); cubicTo(p.x(0.16f), p.y(0.3f), p.x(0.36f), p.y(0.16f), p.x(0.5f), p.y(0.14f)); cubicTo(p.x(0.64f), p.y(0.16f), p.x(0.84f), p.y(0.3f), p.x(0.84f), c.y); close() }, p.metal(0.16f, 0.84f))
                if (p.hecho) listOf(0.3f, 0.4f, 0.5f, 0.6f, 0.7f).forEach { xx -> drawLine(Color.White.copy(alpha = 0.6f), Offset(p.x(0.5f), p.y(0.15f)), Offset(p.x(xx), c.y), lw * 0.8f) }
                caja(p, 0.44f, 0.05f, 0.56f, 0.15f); drawCircle(p.metal(0.47f, 0.53f), p.w * 0.03f, Offset(p.x(0.5f), p.y(0.04f)))
                caja(p, 0.12f, 0.56f, 0.88f, 0.66f)
                if (p.hecho) for (k in 0 until 6) drawCircle(p.linea, p.w * 0.018f, Offset(p.x(0.2f + k * 0.12f), p.y(0.61f)))
            }
            "Gente" -> {
                // Florín de oro con el lirio.
                val c = Offset(p.x(0.5f), p.y(0.38f))
                drawCircle(p.metal(0.2f, 0.8f), p.w * 0.3f, c); drawCircle(p.linea, p.w * 0.26f, c, style = Stroke(lw * 0.6f))
                drawPath(Path().apply { moveTo(c.x, c.y - p.w * 0.16f); quadraticTo(c.x + p.w * 0.06f, c.y, c.x, c.y + p.w * 0.06f); quadraticTo(c.x - p.w * 0.06f, c.y, c.x, c.y - p.w * 0.16f); close() }, p.linea)
                listOf(-1f, 1f).forEach { s -> drawLine(p.linea, Offset(c.x, c.y + p.w * 0.02f), Offset(c.x + s * p.w * 0.13f, c.y - p.w * 0.08f), lw) }
                caja(p, 0.47f, 0.68f, 0.53f, 0.77f)
            }
            "Constancia" -> {
                // Reloj de arena.
                caja(p, 0.28f, 0.1f, 0.72f, 0.14f); caja(p, 0.28f, 0.7f, 0.72f, 0.74f)
                drawPath(Path().apply { moveTo(p.x(0.34f), p.y(0.14f)); lineTo(p.x(0.66f), p.y(0.14f)); lineTo(p.x(0.52f), p.y(0.42f)); lineTo(p.x(0.66f), p.y(0.7f)); lineTo(p.x(0.34f), p.y(0.7f)); lineTo(p.x(0.48f), p.y(0.42f)); close() },
                    if (p.hecho) SolidColor(Color.White.copy(alpha = 0.35f)) else SolidColor(p.sombra), style = Stroke(lw))
                if (p.hecho) {
                    drawPath(Path().apply { moveTo(p.x(0.4f), p.y(0.26f)); lineTo(p.x(0.6f), p.y(0.26f)); lineTo(p.x(0.5f), p.y(0.42f)); close() }, Color(0xFFD9B566))
                    drawPath(Path().apply { moveTo(p.x(0.38f), p.y(0.7f)); lineTo(p.x(0.62f), p.y(0.7f)); lineTo(p.x(0.5f), p.y(0.58f)); close() }, Color(0xFFD9B566))
                }
                listOf(0.3f, 0.7f).forEach { xx -> caja(p, xx - 0.015f, 0.14f, xx + 0.015f, 0.7f) }
            }
            "Curiosos" -> {
                // Nocturlabio: el reloj de noche. El disco de las horas con sus doce dientes (el más largo marca la medianoche)
                // para contarlas a tientas, el mango abajo, el agujero del centro para mirar la Estrella Polar y el brazo largo
                // que se gira hasta la Osa Mayor.
                val c = Offset(p.x(0.5f), p.y(0.36f)); val R = p.w * 0.25f
                caja(p, 0.455f, 0.36f + 0.25f * p.w / p.h, 0.545f, 0.74f); caja(p, 0.38f, 0.74f, 0.62f, 0.77f)
                for (k in 0 until 12) {
                    val a = -PI / 2 + k * PI / 6; val rr = R + p.w * (if (k == 0) 0.055f else 0.03f)
                    drawCircle(p.metal(0.2f, 0.8f), p.w * (if (k == 0) 0.032f else 0.022f), Offset(c.x + (rr * cos(a)).toFloat(), c.y + (rr * sin(a)).toFloat()))
                }
                drawCircle(p.metal(0.25f, 0.75f), R, c)
                drawCircle(p.linea, R * 0.64f, c, style = Stroke(lw * 0.6f))
                if (p.hecho) for (k in 0 until 12) { val a = -PI / 2 + k * PI / 6; drawLine(p.linea, Offset(c.x + (R * 0.64f * cos(a)).toFloat(), c.y + (R * 0.64f * sin(a)).toFloat()), Offset(c.x + (R * 0.8f * cos(a)).toFloat(), c.y + (R * 0.8f * sin(a)).toFloat()), lw * 0.5f) }
                val g = -PI / 3; val punta = Offset(c.x + (R * 1.6f * cos(g)).toFloat(), c.y + (R * 1.6f * sin(g)).toFloat())
                drawPath(Path().apply {
                    val nx = (-sin(g) * p.w * 0.03f).toFloat(); val ny = (cos(g) * p.w * 0.03f).toFloat()
                    moveTo(c.x + nx, c.y + ny); lineTo(punta.x, punta.y); lineTo(c.x - nx, c.y - ny); close()
                }, p.metal(0.5f, 0.9f))
                drawCircle(p.metal(0.44f, 0.56f), p.w * 0.05f, c)
                drawCircle(if (p.hecho) Color(0xFF1C1A16) else p.sombra, p.w * 0.022f, c)
            }
            else -> {
                // El tornillo aéreo de Leonardo: una hélice de lona sobre su eje.
                caja(p, 0.48f, 0.12f, 0.52f, 0.74f)
                drawPath(Path().apply {
                    moveTo(p.x(0.2f), p.y(0.36f)); cubicTo(p.x(0.2f), p.y(0.18f), p.x(0.8f), p.y(0.14f), p.x(0.8f), p.y(0.3f))
                    cubicTo(p.x(0.8f), p.y(0.42f), p.x(0.3f), p.y(0.44f), p.x(0.24f), p.y(0.5f)); cubicTo(p.x(0.36f), p.y(0.54f), p.x(0.72f), p.y(0.5f), p.x(0.76f), p.y(0.56f)); close()
                }, if (p.hecho) SolidColor(Color(0xFFE8D9B0).copy(alpha = 0.9f)) else SolidColor(p.sombra))
                drawPath(Path().apply { moveTo(p.x(0.2f), p.y(0.36f)); cubicTo(p.x(0.2f), p.y(0.18f), p.x(0.8f), p.y(0.14f), p.x(0.8f), p.y(0.3f)) }, p.linea, style = Stroke(lw))
                caja(p, 0.3f, 0.68f, 0.7f, 0.74f)
            }
        }
    }

    // ================================================================== Imperio

    private fun DrawScope.napoleon(cat: String, p: Pincel) {
        val lw = p.w * 0.012f
        val oro = p.metal(0.2f, 0.8f)
        when (cat) {
            "Diario" -> {
                // El Código: un libro de pie con la N dentro de un laurel.
                caja(p, 0.24f, 0.1f, 0.76f, 0.74f, if (p.hecho) SolidColor(Color(0xFF1E3A8A)) else SolidColor(p.sombra))
                drawRect(oro, Offset(p.x(0.28f), p.y(0.14f)), Size(p.x(0.44f), p.y(0.56f)), style = Stroke(lw))
                laurel(p, Offset(p.x(0.5f), p.y(0.4f)), p.w * 0.16f, oro)
                texto("N", p, 0.5f, 0.39f, 0.18f, if (p.hecho) p.brillo else p.sombra)
            }
            "Mundo" -> {
                // Mapa de campaña desplegado con la flecha del avance.
                drawPath(Path().apply { moveTo(p.x(0.1f), p.y(0.2f)); lineTo(p.x(0.36f), p.y(0.16f)); lineTo(p.x(0.64f), p.y(0.22f)); lineTo(p.x(0.9f), p.y(0.18f)); lineTo(p.x(0.9f), p.y(0.66f)); lineTo(p.x(0.64f), p.y(0.7f)); lineTo(p.x(0.36f), p.y(0.64f)); lineTo(p.x(0.1f), p.y(0.68f)); close() },
                    if (p.hecho) SolidColor(Color(0xFFF1E9D2)) else SolidColor(p.sombra))
                if (p.hecho) { listOf(0.36f, 0.64f).forEach { xx -> drawLine(Color.Black.copy(alpha = 0.15f), Offset(p.x(xx), p.y(0.16f)), Offset(p.x(xx), p.y(0.7f)), lw * 0.6f) } }
                val flecha = Path().apply { moveTo(p.x(0.18f), p.y(0.56f)); cubicTo(p.x(0.36f), p.y(0.3f), p.x(0.56f), p.y(0.56f), p.x(0.76f), p.y(0.32f)) }
                drawPath(flecha, if (p.hecho) SolidColor(Color(0xFFA3172E)) else SolidColor(p.sombra), style = Stroke(lw * 1.6f))
                drawPath(Path().apply { moveTo(p.x(0.82f), p.y(0.26f)); lineTo(p.x(0.72f), p.y(0.28f)); lineTo(p.x(0.78f), p.y(0.37f)); close() }, if (p.hecho) SolidColor(Color(0xFFA3172E)) else SolidColor(p.sombra))
                caja(p, 0.47f, 0.7f, 0.53f, 0.77f)
            }
            "Cuerpo" -> {
                // Tambor con sus cuerdas en zigzag y las baquetas.
                caja(p, 0.22f, 0.34f, 0.78f, 0.7f, if (p.hecho) SolidColor(Color(0xFF1E3A8A)) else SolidColor(p.sombra))
                drawOval(oro, Offset(p.x(0.22f), p.y(0.28f)), Size(p.x(0.56f), p.y(0.12f)))
                if (p.hecho) drawOval(Color(0xFFF1E9D2), Offset(p.x(0.25f), p.y(0.295f)), Size(p.x(0.5f), p.y(0.09f)))
                val zig = Path().apply { moveTo(p.x(0.22f), p.y(0.4f)); var k = 0; var xx = 0.22f; while (xx < 0.78f) { xx += 0.07f; lineTo(p.x(xx.coerceAtMost(0.78f)), p.y(if (k % 2 == 0) 0.68f else 0.4f)); k++ } }
                drawPath(zig, p.metal(0.22f, 0.78f), style = Stroke(lw * 0.8f))
                drawLine(p.linea, Offset(p.x(0.36f), p.y(0.3f)), Offset(p.x(0.2f), p.y(0.08f)), lw * 1.2f); drawLine(p.linea, Offset(p.x(0.64f), p.y(0.3f)), Offset(p.x(0.8f), p.y(0.08f)), lw * 1.2f)
                caja(p, 0.2f, 0.7f, 0.8f, 0.74f)
            }
            "Mente" -> {
                // Columna triunfal con su friso en espiral.
                caja(p, 0.34f, 0.7f, 0.66f, 0.77f); columna(p, 0.5f, 0.16f, 0.14f, 0.7f, 0); caja(p, 0.38f, 0.1f, 0.62f, 0.14f)
                if (p.hecho) { var yy = 0.16f; while (yy < 0.68f) { drawLine(p.linea, Offset(p.x(0.42f), p.y(yy + 0.045f)), Offset(p.x(0.58f), p.y(yy)), lw * 0.6f); yy += 0.045f } }
                laurel(p, Offset(p.x(0.5f), p.y(0.05f)), p.w * 0.07f, oro)
            }
            "Cultura" -> {
                // El Arco de la Estrella: un gran arco de triunfo de un solo vano.
                val contorno = Path().apply { addRect(p.r(0.12f, 0.2f, 0.88f, 0.76f)) }
                conHuecos(p, 0.12f, 0.88f, contorno) { arco(p, 0.34f, 0.66f, 0.42f, 0.76f) }
                caja(p, 0.1f, 0.12f, 0.9f, 0.2f)
                if (p.hecho) { laurel(p, Offset(p.x(0.5f), p.y(0.3f)), p.w * 0.05f, SolidColor(p.linea)); listOf(0.2f, 0.8f).forEach { xx -> drawRect(p.linea.copy(alpha = 0.4f), Offset(p.x(xx - 0.04f), p.y(0.3f)), Size(p.x(0.08f), p.y(0.3f)), style = Stroke(lw * 0.6f)) } }
            }
            "Gente" -> {
                // La Legión de Honor: estrella de cinco brazos con puntas dobles, el medallón y la cinta roja.
                drawRect(if (p.hecho) SolidColor(Color(0xFFB3122E)) else SolidColor(p.sombra), Offset(p.x(0.4f), p.y(0.04f)), Size(p.x(0.2f), p.y(0.14f)))
                caja(p, 0.44f, 0.16f, 0.56f, 0.2f)
                val c = Offset(p.x(0.5f), p.y(0.44f))
                for (k in 0 until 5) {
                    // Cada brazo se abre hacia afuera y termina en dos puntas con una muesca, esmaltado en blanco con filete de oro.
                    val a = -PI / 2 + k * 2 * PI / 5; val R = p.w * 0.32f
                    fun pt(rr: Float, da: Double) = Offset(c.x + (rr * cos(a + da)).toFloat(), c.y + (rr * sin(a + da)).toFloat())
                    val brazo = Path().apply {
                        val b1 = pt(R * 0.22f, -0.32); val b2 = pt(R * 0.22f, 0.32)
                        val t1 = pt(R, -0.24); val m = pt(R * 0.8f, 0.0); val t2 = pt(R, 0.24)
                        moveTo(b1.x, b1.y); lineTo(t1.x, t1.y); lineTo(m.x, m.y); lineTo(t2.x, t2.y); lineTo(b2.x, b2.y); close()
                    }
                    drawPath(brazo, if (p.hecho) SolidColor(Color.White.copy(alpha = 0.95f)) else SolidColor(p.sombra))
                    drawPath(brazo, oro, style = Stroke(lw * 0.8f))
                    listOf(-0.24, 0.24).forEach { da -> drawCircle(oro, p.w * 0.014f, pt(R, da)) }
                }
                drawCircle(oro, p.w * 0.1f, c); drawCircle(if (p.hecho) SolidColor(Color(0xFF1E3A8A)) else SolidColor(p.sombra), p.w * 0.07f, c)
            }
            "Constancia" -> { laurel(p, Offset(p.x(0.5f), p.y(0.4f)), p.w * 0.3f, oro); if (p.hecho) drawLine(Color(0xFFB3122E), Offset(p.x(0.44f), p.y(0.72f)), Offset(p.x(0.56f), p.y(0.72f)), lw * 2) }
            "Curiosos" -> {
                // El bicornio con su escarapela.
                drawPath(Path().apply { moveTo(p.x(0.06f), p.y(0.56f)); quadraticTo(p.x(0.5f), p.y(0.06f), p.x(0.94f), p.y(0.56f)); quadraticTo(p.x(0.5f), p.y(0.46f), p.x(0.06f), p.y(0.56f)); close() },
                    if (p.hecho) Brush.verticalGradient(listOf(Color(0xFF3A3A48), Color(0xFF15151C)), p.y(0.2f), p.y(0.56f)) else SolidColor(p.sombra))
                drawPath(Path().apply { moveTo(p.x(0.06f), p.y(0.56f)); quadraticTo(p.x(0.5f), p.y(0.06f), p.x(0.94f), p.y(0.56f)) }, oro, style = Stroke(lw))
                with(Contenedores) { if (p.hecho) escarapela(Offset(p.x(0.62f), p.y(0.36f)), p.w * 0.06f) }
                caja(p, 0.46f, 0.6f, 0.54f, 0.77f)
            }
            else -> {
                // La N coronada dentro de su laurel.
                laurel(p, Offset(p.x(0.5f), p.y(0.44f)), p.w * 0.28f, oro)
                texto("N", p, 0.5f, 0.44f, 0.32f, if (p.hecho) p.base else p.sombra)
                drawPath(Path().apply { moveTo(p.x(0.38f), p.y(0.14f)); lineTo(p.x(0.4f), p.y(0.06f)); lineTo(p.x(0.45f), p.y(0.11f)); lineTo(p.x(0.5f), p.y(0.04f)); lineTo(p.x(0.55f), p.y(0.11f)); lineTo(p.x(0.6f), p.y(0.06f)); lineTo(p.x(0.62f), p.y(0.14f)); close() }, oro)
            }
        }
    }

    // ================================================================== Revolución

    private fun DrawScope.revolucion(cat: String, p: Pincel) {
        val lw = p.w * 0.012f
        val azul = if (p.hecho) Color(0xFF1F3A6E) else p.sombra; val rojo = if (p.hecho) Color(0xFFB22234) else p.sombra
        when (cat) {
            "Diario" -> {
                // La Declaración: dos tablas de bordes redondeados, con renglones.
                listOf(0.14f, 0.52f).forEach { x0 ->
                    drawPath(Path().apply { moveTo(p.x(x0), p.y(0.74f)); lineTo(p.x(x0), p.y(0.24f)); quadraticTo(p.x(x0), p.y(0.1f), p.x(x0 + 0.17f), p.y(0.1f)); quadraticTo(p.x(x0 + 0.34f), p.y(0.1f), p.x(x0 + 0.34f), p.y(0.24f)); lineTo(p.x(x0 + 0.34f), p.y(0.74f)); close() },
                        p.metal(x0, x0 + 0.34f))
                    if (p.hecho) for (k in 0 until 7) drawLine(p.linea, Offset(p.x(x0 + 0.05f), p.y(0.26f + k * 0.065f)), Offset(p.x(x0 + 0.29f), p.y(0.26f + k * 0.065f)), lw * 0.5f)
                }
            }
            "Mundo" -> {
                // El árbol de la libertad, con el gorro en la punta.
                caja(p, 0.46f, 0.42f, 0.54f, 0.77f, if (p.hecho) SolidColor(Color(0xFF6B4A2A)) else SolidColor(p.sombra))
                listOf(Offset(0.5f, 0.3f) to 0.22f, Offset(0.34f, 0.38f) to 0.15f, Offset(0.66f, 0.38f) to 0.15f).forEach { (o, r) ->
                    drawCircle(if (p.hecho) SolidColor(Color(0xFF3F7A3A)) else SolidColor(p.sombra), p.w * r, Offset(p.x(o.x), p.y(o.y)))
                }
                gorro(p, 0.5f, 0.06f, 0.1f, rojo)
                if (p.hecho) listOf(azul, Color.White, rojo).forEachIndexed { i, c -> drawRect(c, Offset(p.x(0.44f + i * 0.04f), p.y(0.6f)), Size(p.x(0.04f), p.y(0.04f))) }
            }
            "Cuerpo" -> {
                if (p.hecho) with(Contenedores) { escarapela(Offset(p.x(0.5f), p.y(0.38f)), p.w * 0.3f) }
                else drawCircle(p.sombra, p.w * 0.3f, Offset(p.x(0.5f), p.y(0.38f)))
                caja(p, 0.47f, 0.68f, 0.53f, 0.77f)
            }
            "Mente" -> {
                // La balanza en equilibrio.
                caja(p, 0.48f, 0.12f, 0.52f, 0.72f); caja(p, 0.34f, 0.72f, 0.66f, 0.76f)
                caja(p, 0.14f, 0.16f, 0.86f, 0.19f)
                listOf(0.2f, 0.8f).forEach { cx ->
                    drawLine(p.linea, Offset(p.x(cx), p.y(0.19f)), Offset(p.x(cx - 0.1f), p.y(0.46f)), lw * 0.6f); drawLine(p.linea, Offset(p.x(cx), p.y(0.19f)), Offset(p.x(cx + 0.1f), p.y(0.46f)), lw * 0.6f)
                    drawArc(p.metal(cx - 0.12f, cx + 0.12f), 0f, 180f, true, Offset(p.x(cx - 0.12f), p.y(0.42f)), Size(p.x(0.24f), p.y(0.1f)))
                }
                drawCircle(p.metal(0.46f, 0.54f), p.w * 0.04f, Offset(p.x(0.5f), p.y(0.12f)))
            }
            "Cultura" -> {
                // La pluma en el tintero.
                drawRoundRect(p.metal(0.32f, 0.68f), Offset(p.x(0.32f), p.y(0.52f)), Size(p.x(0.36f), p.y(0.22f)), CornerRadius(p.w * 0.04f))
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.5f)); cubicTo(p.x(0.52f), p.y(0.32f), p.x(0.64f), p.y(0.12f), p.x(0.86f), p.y(0.02f)); cubicTo(p.x(0.76f), p.y(0.2f), p.x(0.64f), p.y(0.4f), p.x(0.52f), p.y(0.5f)); close() },
                    if (p.hecho) SolidColor(Color.White.copy(alpha = 0.95f)) else SolidColor(p.sombra))
                drawPath(Path().apply { moveTo(p.x(0.5f), p.y(0.5f)); cubicTo(p.x(0.58f), p.y(0.32f), p.x(0.68f), p.y(0.16f), p.x(0.84f), p.y(0.04f)) }, p.linea, style = Stroke(lw * 0.5f))
                if (p.hecho) listOf(azul, Color.White, rojo).forEachIndexed { i, c -> drawRect(c, Offset(p.x(0.38f + i * 0.08f), p.y(0.6f)), Size(p.x(0.08f), p.y(0.04f))) }
            }
            "Gente" -> { gorro(p, 0.5f, 0.16f, 0.36f, rojo); with(Contenedores) { if (p.hecho) escarapela(Offset(p.x(0.36f), p.y(0.56f)), p.w * 0.07f) }; caja(p, 0.3f, 0.66f, 0.7f, 0.7f) }
            "Constancia" -> {
                // La llave de la Bastilla: grande, de anillo ancho.
                drawCircle(p.metal(0.28f, 0.72f), p.w * 0.16f, Offset(p.x(0.5f), p.y(0.2f)), style = Stroke(lw * 2.4f))
                caja(p, 0.47f, 0.3f, 0.53f, 0.72f)
                caja(p, 0.53f, 0.56f, 0.68f, 0.6f); caja(p, 0.53f, 0.64f, 0.64f, 0.68f); caja(p, 0.53f, 0.69f, 0.6f, 0.72f)
            }
            "Curiosos" -> {
                // Fasces: el haz de varas atado con cintas.
                for (k in 0 until 5) caja(p, 0.39f + k * 0.045f, 0.12f, 0.425f + k * 0.045f, 0.74f)
                listOf(0.24f, 0.44f, 0.64f).forEach { yy -> drawRect(rojo, Offset(p.x(0.37f), p.y(yy)), Size(p.x(0.26f), p.y(0.025f))) }
                drawPath(Path().apply { moveTo(p.x(0.62f), p.y(0.16f)); quadraticTo(p.x(0.84f), p.y(0.14f), p.x(0.82f), p.y(0.3f)); lineTo(p.x(0.62f), p.y(0.26f)); close() }, p.metal(0.62f, 0.84f))
            }
            else -> {
                // El nivel: un triángulo con su plomada, símbolo de la igualdad.
                drawPath(Path().apply { moveTo(p.x(0.14f), p.y(0.72f)); lineTo(p.x(0.5f), p.y(0.12f)); lineTo(p.x(0.86f), p.y(0.72f)) }, p.metal(0.14f, 0.86f), style = Stroke(lw * 2.4f, cap = StrokeCap.Round))
                caja(p, 0.22f, 0.56f, 0.78f, 0.6f)
                drawLine(p.linea, Offset(p.x(0.5f), p.y(0.14f)), Offset(p.x(0.5f), p.y(0.5f)), lw * 0.5f)
                drawCircle(rojo, p.w * 0.035f, Offset(p.x(0.5f), p.y(0.52f)))
            }
        }
    }

    /** El gorro frigio: la punta caída hacia adelante. */
    private fun DrawScope.gorro(p: Pincel, cx: Float, y0: Float, tam: Float, color: Color) {
        val s = tam
        drawPath(Path().apply {
            moveTo(p.x(cx - s * 0.5f), p.y(y0 + s * 1.0f)); cubicTo(p.x(cx - s * 0.6f), p.y(y0 + s * 0.3f), p.x(cx - s * 0.1f), p.y(y0), p.x(cx + s * 0.3f), p.y(y0 + s * 0.1f))
            cubicTo(p.x(cx + s * 0.55f), p.y(y0 + s * 0.2f), p.x(cx + s * 0.6f), p.y(y0 + s * 0.45f), p.x(cx + s * 0.42f), p.y(y0 + s * 0.5f))
            cubicTo(p.x(cx + s * 0.5f), p.y(y0 + s * 0.7f), p.x(cx + s * 0.5f), p.y(y0 + s * 0.9f), p.x(cx + s * 0.5f), p.y(y0 + s * 1.0f)); close()
        }, color)
        drawRect(if (p.hecho) Color.White.copy(alpha = 0.85f) else p.sombra, Offset(p.x(cx - s * 0.52f), p.y(y0 + s * 0.92f)), Size(p.x(s * 1.04f), p.y(s * 0.1f)))
    }
}
