package io.github.ruddy2003.vestigium.ui

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Las @personas y los #temas resaltados con la mano de cada tema: rúbrica roja en el scriptorium, marcatextos en el
 * cuaderno del naturalista, sello de cinabrio en la Ruta de la Seda, barra de censura en la distopía, corchetes de neón…
 * El texto no cambia (mismas letras y posiciones); solo cómo se ve.
 */
object Marcas {
    enum class Forma { NINGUNA, MARCADOR, SUBRAYADO, ONDULADO, RECUADRO, SELLO, CORCHETES }

    /** Cómo se ve una marca: color de la letra, el trazo detrás y si va en negrita o cursiva. */
    class Aspecto(val letra: Color, val trazo: Color, val forma: Forma, val negrita: Boolean = false, val cursiva: Boolean = false, val fondoEnTexto: Color? = null)

    private val RX = Regex("([@#])[\\p{L}\\p{N}_]+")

    /** Rangos [inicio, fin) de cada etiqueta y si es persona. */
    fun rangos(t: String): List<Triple<Int, Int, Boolean>> = RX.findAll(t).map { Triple(it.range.first, it.range.last + 1, it.value.startsWith("@")) }.toList()

    fun estilo(tema: TemaId, cs: ColorScheme, persona: Boolean): Aspecto {
        val p = cs.primary; val s = cs.secondary; val t = cs.tertiary
        return when (tema) {
            TemaId.ROMANO -> if (persona) Aspecto(p, t, Forma.SUBRAYADO, negrita = true) else Aspecto(t, t, Forma.SUBRAYADO)
            TemaId.CIBER -> if (persona) Aspecto(s, s, Forma.CORCHETES) else Aspecto(p, p, Forma.CORCHETES)
            TemaId.HELENO -> if (persona) Aspecto(p, t, Forma.MARCADOR, negrita = true) else Aspecto(s, s, Forma.ONDULADO)
            TemaId.MEDIEVAL -> if (persona) Aspecto(p, p, Forma.NINGUNA, negrita = true, cursiva = true) else Aspecto(t, t, Forma.SUBRAYADO, cursiva = true)
            TemaId.DISTOPIA -> if (persona) Aspecto(cs.onPrimary, p, Forma.SELLO, negrita = true) else Aspecto(cs.background, cs.onBackground, Forma.SELLO)
            TemaId.REFUGIO -> Aspecto(if (persona) p else s, if (persona) p else s, Forma.RECUADRO, negrita = persona)
            TemaId.ORDEN -> if (persona) Aspecto(p, p, Forma.SUBRAYADO, negrita = true) else Aspecto(t, t, Forma.RECUADRO)
            TemaId.LABORATORIO -> Aspecto(if (persona) p else s, if (persona) p else s, Forma.MARCADOR, negrita = persona)
            TemaId.COLONOS -> if (persona) Aspecto(p, p, Forma.SUBRAYADO, negrita = true) else Aspecto(s, s, Forma.ONDULADO)
            TemaId.NAPOLEON -> if (persona) Aspecto(p, t, Forma.SUBRAYADO, negrita = true, cursiva = true) else Aspecto(s, s, Forma.NINGUNA, cursiva = true)
            TemaId.BOTANICO -> Aspecto(if (persona) p else t, if (persona) s else t, Forma.MARCADOR, negrita = persona)
            TemaId.NAVIDAD -> if (persona) Aspecto(p, p, Forma.MARCADOR, negrita = true) else Aspecto(s, s, Forma.ONDULADO)
            TemaId.SEDA -> if (persona) Aspecto(cs.onPrimary, p, Forma.SELLO, negrita = true) else Aspecto(s, s, Forma.RECUADRO)
            TemaId.GOTICO -> if (persona) Aspecto(p, t, Forma.MARCADOR, negrita = true) else Aspecto(s, s, Forma.SUBRAYADO, cursiva = true)
            TemaId.ESPACIO -> Aspecto(if (persona) t else p, if (persona) t else p, Forma.CORCHETES, negrita = persona)
            TemaId.ANDALUS -> if (persona) Aspecto(p, t, Forma.MARCADOR, negrita = true) else Aspecto(s, s, Forma.ONDULADO)
            TemaId.MONGOL -> if (persona) Aspecto(p, t, Forma.SUBRAYADO, negrita = true) else Aspecto(s, s, Forma.MARCADOR)
            TemaId.RENACIMIENTO -> if (persona) Aspecto(p, p, Forma.ONDULADO, cursiva = true) else Aspecto(s, s, Forma.SUBRAYADO, cursiva = true)
            TemaId.URSS -> if (persona) Aspecto(cs.onPrimary, p, Forma.SELLO, negrita = true) else Aspecto(cs.onBackground, cs.onBackground, Forma.SUBRAYADO, negrita = true)
        }
    }

    /** El estilo de letra de la marca. Con [conFondo], lo que no se puede dibujar (en un campo de texto) va como fondo de la letra. */
    fun span(e: Aspecto, conFondo: Boolean): SpanStyle = SpanStyle(
        color = e.letra,
        fontWeight = if (e.negrita) FontWeight.SemiBold else null,
        fontStyle = if (e.cursiva) FontStyle.Italic else null,
        background = if (!conFondo) Color.Unspecified else when (e.forma) {
            Forma.SELLO -> e.trazo
            Forma.MARCADOR -> e.trazo.copy(alpha = 0.22f)
            Forma.RECUADRO, Forma.CORCHETES -> e.trazo.copy(alpha = 0.12f)
            else -> Color.Unspecified
        },
        textDecoration = if (conFondo && (e.forma == Forma.SUBRAYADO || e.forma == Forma.ONDULADO)) TextDecoration.Underline else null,
    )

    /** Añade las marcas a un texto que ya puede traer otros estilos (la capitular, por ejemplo). */
    fun anotar(a: AnnotatedString, tema: TemaId, cs: ColorScheme, conFondo: Boolean): AnnotatedString {
        val r = rangos(a.text)
        if (r.isEmpty()) return a
        return buildAnnotatedString {
            append(a)
            r.forEach { (i, f, persona) -> addStyle(span(estilo(tema, cs, persona), conFondo), i, f) }
        }
    }

    /** Dibuja detrás del texto el trazo de cada etiqueta, línea por línea. */
    fun DrawScope.dibujar(layout: TextLayoutResult, texto: String, tema: TemaId, cs: ColorScheme) {
        val u = 1.dp.toPx()
        for ((i, f, persona) in rangos(texto)) {
            val e = estilo(tema, cs, persona)
            if (e.forma == Forma.NINGUNA) continue
            // Un rectángulo por línea que ocupa la etiqueta.
            val porLinea = LinkedHashMap<Int, Rect>()
            for (k in i until f) {
                val b = runCatching { layout.getBoundingBox(k) }.getOrNull() ?: continue
                if (b.width <= 0f) continue
                val linea = runCatching { layout.getLineForOffset(k) }.getOrDefault(0)
                porLinea[linea] = porLinea[linea]?.let { Rect(minOf(it.left, b.left), minOf(it.top, b.top), maxOf(it.right, b.right), maxOf(it.bottom, b.bottom)) } ?: b
            }
            for (rr in porLinea.values) {
                val c = e.trazo
                when (e.forma) {
                    Forma.MARCADOR -> {
                        // Trazo de marcatextos: un poco inclinado y más ancho que la palabra.
                        val alto = rr.height * 0.62f
                        val top = rr.top + rr.height * 0.28f
                        val p = Path().apply {
                            moveTo(rr.left - 2 * u, top + alto * 0.12f); lineTo(rr.right + 2 * u, top)
                            lineTo(rr.right + 1 * u, top + alto); lineTo(rr.left - 3 * u, top + alto * 1.06f); close()
                        }
                        drawPath(p, c.copy(alpha = 0.26f))
                    }
                    Forma.SUBRAYADO -> {
                        val y = rr.bottom - rr.height * 0.08f
                        drawLine(c.copy(alpha = 0.85f), Offset(rr.left, y), Offset(rr.right, y), 1.4f * u, StrokeCap.Round)
                    }
                    Forma.ONDULADO -> {
                        val y = rr.bottom - rr.height * 0.06f
                        val p = Path(); var x = rr.left; p.moveTo(x, y); var arriba = true
                        val paso = 3f * u
                        while (x < rr.right) { val nx = minOf(x + paso, rr.right); p.quadraticTo((x + nx) / 2, if (arriba) y - 2f * u else y + 2f * u, nx, y); x = nx; arriba = !arriba }
                        drawPath(p, c.copy(alpha = 0.85f), style = Stroke(1.2f * u))
                    }
                    Forma.RECUADRO -> drawRoundRect(c.copy(alpha = 0.7f), Offset(rr.left - 2 * u, rr.top + rr.height * 0.08f), Size(rr.width + 4 * u, rr.height * 0.86f),
                        CornerRadius(2 * u, 2 * u), style = Stroke(1f * u))
                    Forma.SELLO -> drawRoundRect(c, Offset(rr.left - 3 * u, rr.top + rr.height * 0.06f), Size(rr.width + 6 * u, rr.height * 0.88f), CornerRadius(rr.height / 2, rr.height / 2))
                    Forma.CORCHETES -> {
                        val t = rr.top + rr.height * 0.12f; val b = rr.bottom - rr.height * 0.08f; val w = 3f * u
                        drawRect(c.copy(alpha = 0.10f), Offset(rr.left - 2 * u, t), Size(rr.width + 4 * u, b - t))
                        listOf(rr.left - 2 * u to 1f, rr.right + 2 * u to -1f).forEach { (x, d) ->
                            drawLine(c, Offset(x, t), Offset(x, b), 1.2f * u)
                            drawLine(c, Offset(x, t), Offset(x + d * w, t), 1.2f * u)
                            drawLine(c, Offset(x, b), Offset(x + d * w, b), 1.2f * u)
                        }
                    }
                    Forma.NINGUNA -> {}
                }
            }
        }
    }
}

/** Para campos de texto: las etiquetas toman el color, la letra y un fondo del tema mientras escribes. */
class TransformacionMarcas(private val tema: TemaId, private val cs: ColorScheme, private val antes: VisualTransformation = VisualTransformation.None) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val base = antes.filter(text)
        return TransformedText(Marcas.anotar(base.text, tema, cs, conFondo = true), base.offsetMapping)
    }
    override fun equals(other: Any?): Boolean = other is TransformacionMarcas && other.tema == tema && other.cs.primary == cs.primary && other.antes == antes
    override fun hashCode(): Int = tema.hashCode() * 31 + cs.primary.hashCode()
}

@Composable
fun rememberMarcas(antes: VisualTransformation = VisualTransformation.None): VisualTransformation {
    val tema = Estilo.tema
    val cs = MaterialTheme.colorScheme
    return remember(tema, cs, antes) { TransformacionMarcas(tema, cs, antes) }
}

/** Un texto del usuario con sus @personas y #temas marcados al estilo del tema. */
@Composable
fun TextoMarcado(
    texto: String, modifier: Modifier = Modifier, style: TextStyle = LocalTextStyle.current, color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE, overflow: TextOverflow = TextOverflow.Clip, tema: TemaId = Estilo.tema,
) {
    val cs = MaterialTheme.colorScheme
    val anotado = remember(texto, tema, cs) { Marcas.anotar(AnnotatedString(texto), tema, cs, conFondo = false) }
    var layout by remember(texto) { mutableStateOf<TextLayoutResult?>(null) }
    val conMarcas = Marcas.rangos(texto).isNotEmpty()
    Text(anotado, modifier = if (!conMarcas) modifier else modifier.drawBehind { layout?.let { with(Marcas) { dibujar(it, texto, tema, cs) } } },
        style = style, color = color, maxLines = maxLines, overflow = overflow, onTextLayout = { layout = it },
        inlineContent = emptyMap<String, InlineTextContent>())
}
