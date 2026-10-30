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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
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
 * Las @personas y los #temas resaltados con la mano de cada tema: tabula ansata en Roma, vídeo inverso en la terminal,
 * greca en Grecia, rúbrica roja en el scriptorium, barra de censura en la distopía, sello de cinabrio en la Ruta de la
 * Seda, arco de herradura en al-Ándalus, piedra rúnica en las Tierras Antiguas… Cada tema tiene su propia pareja
 * (forma de la persona, forma del tema) y ninguna se repite en otro. El texto no cambia (mismas letras y posiciones);
 * solo cómo se ve.
 */
object Marcas {
    enum class Forma {
        NINGUNA, MARCADOR, SUBRAYADO, ONDULADO, RECUADRO, SELLO, CORCHETES,
        /** Tabula ansata: recuadro con sus dos asas en cola de milano. */
        CARTELA,
        /** Vídeo inverso con el cursor de bloque detrás. */
        TERMINAL,
        /** Subrayado en greca (onda cuadrada). */
        GRECA, PUNTEADO,
        /** Barra de censura, a sangre. */
        BARRA,
        /** Sello de goma: doble filete, un poco torcido. */
        GOMA,
        /** Placa remachada. */
        PLACA, DOBLE,
        /** Subrayado graduado, como una regla. */
        ESCALA,
        /** Recorte de periódico: filete de guiones. */
        RECORTE,
        /** Etiqueta colgante con su ojal. */
        ETIQUETA,
        /** Cinta con las puntas en cola de golondrina. */
        CINTA, ZIGZAG,
        /** Arco apuntado sobre la palabra. */
        OJIVA,
        /** Esquinas de visor. */
        ESQUINAS, GUIONES,
        /** Arco de herradura sobre la palabra. */
        HERRADURA,
        /** Dos ondas que se cruzan, como un lazo. */
        TRENZA,
        /** Medallón (anillo con su punto) delante de la palabra. */
        MEDALLON,
        /** Paralelogramo lleno, a la manera constructivista. */
        PARALELO,
        /** Subrayado que termina en flecha. */
        FLECHA, TRIPLE,
        /** Piedra de esquinas cortadas. */
        PIEDRA,
        /** Línea con muescas inclinadas, como una inscripción de palotes. */
        MUESCAS,
        /** Escudete con la cruz patada delante de la palabra y un filete de acero debajo. */
        ESCUDETE,
    }

    /** Cómo se ve una marca: color de la letra, el trazo detrás y si va en negrita o cursiva. */
    class Aspecto(val letra: Color, val trazo: Color, val forma: Forma, val negrita: Boolean = false, val cursiva: Boolean = false, val fondoEnTexto: Color? = null)

    private val RX = Regex("([@#])[\\p{L}\\p{N}_]+")

    /** Rangos [inicio, fin) de cada etiqueta y si es persona. */
    fun rangos(t: String): List<Triple<Int, Int, Boolean>> = RX.findAll(t).map { Triple(it.range.first, it.range.last + 1, it.value.startsWith("@")) }.toList()

    /** La marca de cada tema: la @persona tiene una forma que solo usa ese tema, así que ninguna pareja se repite. */
    fun estilo(tema: TemaId, cs: ColorScheme, persona: Boolean): Aspecto {
        val p = cs.primary; val s = cs.secondary; val t = cs.tertiary
        return when (tema) {
            TemaId.ROMANO -> if (persona) Aspecto(p, t, Forma.CARTELA, negrita = true) else Aspecto(t, t, Forma.SUBRAYADO)
            TemaId.CIBER -> if (persona) Aspecto(cs.background, p, Forma.TERMINAL, negrita = true) else Aspecto(s, s, Forma.CORCHETES)
            TemaId.NEON -> if (persona) Aspecto(cs.background, p, Forma.TERMINAL, negrita = true) else Aspecto(s, s, Forma.CORCHETES)
            TemaId.HELENO -> if (persona) Aspecto(p, t, Forma.GRECA, negrita = true) else Aspecto(s, s, Forma.PUNTEADO)
            TemaId.MEDIEVAL -> if (persona) Aspecto(p, p, Forma.NINGUNA, negrita = true, cursiva = true) else Aspecto(t, t, Forma.SUBRAYADO, cursiva = true)
            TemaId.TEMPLARIO -> if (persona) Aspecto(p, s, Forma.ESCUDETE, negrita = true) else Aspecto(s, s, Forma.DOBLE, cursiva = true)
            TemaId.DISTOPIA -> if (persona) Aspecto(cs.background, cs.onBackground, Forma.BARRA, negrita = true) else Aspecto(p, p, Forma.GOMA)
            TemaId.REFUGIO -> Aspecto(if (persona) p else s, if (persona) p else s, Forma.RECUADRO, negrita = persona)
            TemaId.ORDEN -> if (persona) Aspecto(p, p, Forma.PLACA, negrita = true) else Aspecto(t, t, Forma.DOBLE)
            TemaId.LABORATORIO -> if (persona) Aspecto(p, p, Forma.MARCADOR, negrita = true) else Aspecto(s, s, Forma.ESCALA)
            TemaId.COLONOS -> if (persona) Aspecto(p, p, Forma.RECORTE, negrita = true) else Aspecto(s, s, Forma.ONDULADO)
            TemaId.NAPOLEON -> if (persona) Aspecto(p, t, Forma.SUBRAYADO, negrita = true, cursiva = true) else Aspecto(s, s, Forma.NINGUNA, cursiva = true)
            TemaId.BOTANICO -> if (persona) Aspecto(p, s, Forma.ETIQUETA, negrita = true) else Aspecto(t, t, Forma.MARCADOR)
            TemaId.NAVIDAD -> if (persona) Aspecto(p, p, Forma.CINTA, negrita = true) else Aspecto(s, s, Forma.ZIGZAG)
            TemaId.SEDA -> if (persona) Aspecto(cs.onPrimary, p, Forma.SELLO, negrita = true) else Aspecto(s, s, Forma.RECUADRO)
            TemaId.GOTICO -> if (persona) Aspecto(p, t, Forma.OJIVA, negrita = true) else Aspecto(s, s, Forma.SUBRAYADO, cursiva = true)
            TemaId.ESPACIO -> if (persona) Aspecto(t, t, Forma.ESQUINAS, negrita = true) else Aspecto(p, p, Forma.GUIONES)
            TemaId.ANDALUS -> if (persona) Aspecto(p, t, Forma.HERRADURA, negrita = true) else Aspecto(s, s, Forma.TRENZA)
            TemaId.MONGOL -> if (persona) Aspecto(p, t, Forma.MEDALLON, negrita = true) else Aspecto(s, s, Forma.MARCADOR)
            TemaId.RENACIMIENTO -> if (persona) Aspecto(p, p, Forma.ONDULADO, cursiva = true) else Aspecto(s, s, Forma.SUBRAYADO, cursiva = true)
            TemaId.URSS -> if (persona) Aspecto(cs.onPrimary, p, Forma.PARALELO, negrita = true) else Aspecto(cs.onBackground, p, Forma.FLECHA, negrita = true)
            TemaId.REVOLUCION -> if (persona) Aspecto(p, s, Forma.TRIPLE, negrita = true) else Aspecto(s, s, Forma.RECUADRO)
            TemaId.ANTIGUAS -> if (persona) Aspecto(p, s, Forma.PIEDRA, negrita = true) else Aspecto(t, t, Forma.MUESCAS, cursiva = true)
        }
    }

    /** En un campo de texto no se puede dibujar: el fondo de la letra que sustituye a cada forma (o nada). */
    private fun fondoCampo(f: Forma, c: Color): Color = when (f) {
        Forma.SELLO, Forma.TERMINAL, Forma.BARRA, Forma.PARALELO -> c
        Forma.MARCADOR, Forma.CINTA, Forma.PLACA, Forma.ETIQUETA -> c.copy(alpha = 0.22f)
        Forma.RECUADRO, Forma.CORCHETES, Forma.CARTELA, Forma.GOMA, Forma.RECORTE, Forma.OJIVA, Forma.ESQUINAS, Forma.HERRADURA, Forma.PIEDRA -> c.copy(alpha = 0.12f)
        Forma.NINGUNA, Forma.SUBRAYADO, Forma.ONDULADO, Forma.GRECA, Forma.PUNTEADO, Forma.DOBLE, Forma.ESCALA, Forma.ZIGZAG, Forma.GUIONES,
        Forma.TRENZA, Forma.MEDALLON, Forma.FLECHA, Forma.TRIPLE, Forma.MUESCAS -> Color.Unspecified
        Forma.ESCUDETE -> c.copy(alpha = 0.12f)
    }

    /** En un campo de texto, las formas que van debajo de la palabra se quedan en un subrayado. */
    private fun subrayaCampo(f: Forma): Boolean = when (f) {
        Forma.SUBRAYADO, Forma.ONDULADO, Forma.GRECA, Forma.PUNTEADO, Forma.DOBLE, Forma.ESCALA, Forma.ZIGZAG, Forma.GUIONES,
        Forma.TRENZA, Forma.MEDALLON, Forma.FLECHA, Forma.TRIPLE, Forma.MUESCAS -> true
        Forma.NINGUNA, Forma.MARCADOR, Forma.RECUADRO, Forma.SELLO, Forma.CORCHETES, Forma.CARTELA, Forma.TERMINAL, Forma.BARRA, Forma.GOMA,
        Forma.PLACA, Forma.RECORTE, Forma.ETIQUETA, Forma.CINTA, Forma.OJIVA, Forma.ESQUINAS, Forma.HERRADURA, Forma.PARALELO, Forma.PIEDRA -> false
        Forma.ESCUDETE -> true
    }

    /** El estilo de letra de la marca. Con [conFondo], lo que no se puede dibujar (en un campo de texto) va como fondo de la letra. */
    fun span(e: Aspecto, conFondo: Boolean): SpanStyle = SpanStyle(
        color = e.letra,
        fontWeight = if (e.negrita) FontWeight.SemiBold else null,
        fontStyle = if (e.cursiva) FontStyle.Italic else null,
        background = if (!conFondo) Color.Unspecified else fondoCampo(e.forma, e.trazo),
        textDecoration = if (conFondo && subrayaCampo(e.forma)) TextDecoration.Underline else null,
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
            for (rr in porLinea.values) forma(e.forma, rr, e.trazo, u)
        }
    }

    /** Una línea que va de [x0] a [x1] a la altura [y] siguiendo [paso] (para las ondas, grecas y zigzags). */
    private fun tramo(x0: Float, x1: Float, paso: Float, f: Path.(x: Float, nx: Float, k: Int) -> Unit): Path {
        val p = Path(); var x = x0; var k = 0
        while (x < x1 - 0.01f) { val nx = minOf(x + paso, x1); p.f(x, nx, k); x = nx; k++ }
        return p
    }

    private fun DrawScope.forma(forma: Forma, rr: Rect, c: Color, u: Float) {
        val h = rr.height; val medio = rr.top + h / 2
        val baja = rr.bottom - h * 0.08f
        when (forma) {
            Forma.NINGUNA -> {}
            Forma.MARCADOR -> {
                // Trazo de marcatextos: un poco inclinado y más ancho que la palabra.
                val alto = h * 0.62f
                val top = rr.top + h * 0.28f
                val p = Path().apply {
                    moveTo(rr.left - 2 * u, top + alto * 0.12f); lineTo(rr.right + 2 * u, top)
                    lineTo(rr.right + 1 * u, top + alto); lineTo(rr.left - 3 * u, top + alto * 1.06f); close()
                }
                drawPath(p, c.copy(alpha = 0.26f))
            }
            Forma.SUBRAYADO -> drawLine(c.copy(alpha = 0.85f), Offset(rr.left, baja), Offset(rr.right, baja), 1.4f * u, StrokeCap.Round)
            Forma.ONDULADO -> {
                val y = rr.bottom - h * 0.06f
                val p = Path().apply { moveTo(rr.left, y) }
                var x = rr.left; var arriba = true
                while (x < rr.right) { val nx = minOf(x + 3f * u, rr.right); p.quadraticTo((x + nx) / 2, if (arriba) y - 2f * u else y + 2f * u, nx, y); x = nx; arriba = !arriba }
                drawPath(p, c.copy(alpha = 0.85f), style = Stroke(1.2f * u))
            }
            Forma.RECUADRO -> drawRoundRect(c.copy(alpha = 0.7f), Offset(rr.left - 2 * u, rr.top + h * 0.08f), Size(rr.width + 4 * u, h * 0.86f),
                CornerRadius(2 * u, 2 * u), style = Stroke(1f * u))
            Forma.SELLO -> drawRoundRect(c, Offset(rr.left - 3 * u, rr.top + h * 0.06f), Size(rr.width + 6 * u, h * 0.88f), CornerRadius(h / 2, h / 2))
            Forma.CORCHETES -> {
                val t = rr.top + h * 0.12f; val b = rr.bottom - h * 0.08f; val w = 3f * u
                drawRect(c.copy(alpha = 0.10f), Offset(rr.left - 2 * u, t), Size(rr.width + 4 * u, b - t))
                listOf(rr.left - 2 * u to 1f, rr.right + 2 * u to -1f).forEach { (x, d) ->
                    drawLine(c, Offset(x, t), Offset(x, b), 1.2f * u)
                    drawLine(c, Offset(x, t), Offset(x + d * w, t), 1.2f * u)
                    drawLine(c, Offset(x, b), Offset(x + d * w, b), 1.2f * u)
                }
            }
            Forma.CARTELA -> {
                // Tabula ansata: el recuadro y, a cada lado, un asa en cola de milano.
                val t = rr.top + h * 0.1f; val b = rr.bottom - h * 0.06f; val iz = rr.left - 3 * u; val de = rr.right + 3 * u
                drawRect(c.copy(alpha = 0.08f), Offset(iz, t), Size(de - iz, b - t))
                drawRect(c.copy(alpha = 0.8f), Offset(iz, t), Size(de - iz, b - t), style = Stroke(1f * u))
                listOf(iz to -1f, de to 1f).forEach { (x, d) ->
                    drawPath(Path().apply { moveTo(x, medio - 1.6f * u); lineTo(x + d * 3.6f * u, medio - 3.6f * u); lineTo(x + d * 3.6f * u, medio + 3.6f * u); lineTo(x, medio + 1.6f * u); close() }, c.copy(alpha = 0.8f))
                }
            }
            Forma.TERMINAL -> {
                // Vídeo inverso: la palabra sobre su bloque y el cursor esperando detrás.
                val t = rr.top + h * 0.06f; val alto = h * 0.88f
                drawRect(c, Offset(rr.left - 1.5f * u, t), Size(rr.width + 3 * u, alto))
                drawRect(c.copy(alpha = 0.55f), Offset(rr.right + 3 * u, t), Size(minOf(6 * u, alto * 0.45f), alto))
            }
            Forma.GRECA -> {
                val y = rr.bottom - h * 0.02f; val a = 2.6f * u
                val p = tramo(rr.left, rr.right, 2.6f * u) { x, nx, k -> if (k == 0) moveTo(x, y); if (k % 2 == 0) { lineTo(x, y - a); lineTo(nx, y - a) } else { lineTo(x, y); lineTo(nx, y) } }
                drawPath(p, c.copy(alpha = 0.85f), style = Stroke(1f * u))
            }
            Forma.PUNTEADO -> { var x = rr.left + 0.8f * u; while (x <= rr.right) { drawCircle(c.copy(alpha = 0.85f), 0.8f * u, Offset(x, baja)); x += 3f * u } }
            Forma.BARRA -> drawRect(c, Offset(rr.left - 2 * u, rr.top + h * 0.1f), Size(rr.width + 4 * u, h * 0.82f))
            Forma.GOMA -> rotate(-3f, Offset(rr.center.x, rr.center.y)) {
                val t = rr.top + h * 0.04f; val alto = h * 0.9f
                drawRect(c.copy(alpha = 0.75f), Offset(rr.left - 3 * u, t), Size(rr.width + 6 * u, alto), style = Stroke(1.3f * u))
                drawRect(c.copy(alpha = 0.55f), Offset(rr.left - 1.4f * u, t + 1.6f * u), Size(rr.width + 2.8f * u, alto - 3.2f * u), style = Stroke(0.6f * u))
            }
            Forma.PLACA -> {
                val t = rr.top + h * 0.1f; val alto = h * 0.84f
                drawRect(c.copy(alpha = 0.16f), Offset(rr.left - 5 * u, t), Size(rr.width + 10 * u, alto))
                drawRect(c.copy(alpha = 0.6f), Offset(rr.left - 5 * u, t), Size(rr.width + 10 * u, alto), style = Stroke(0.6f * u))
                drawCircle(c, 0.9f * u, Offset(rr.left - 2.6f * u, medio)); drawCircle(c, 0.9f * u, Offset(rr.right + 2.6f * u, medio))
            }
            Forma.DOBLE -> { val y = rr.bottom - h * 0.16f
                drawLine(c.copy(alpha = 0.85f), Offset(rr.left, y), Offset(rr.right, y), 0.9f * u); drawLine(c.copy(alpha = 0.85f), Offset(rr.left, y + 2.2f * u), Offset(rr.right, y + 2.2f * u), 0.9f * u) }
            Forma.ESCALA -> {
                val y = baja
                drawLine(c.copy(alpha = 0.85f), Offset(rr.left, y), Offset(rr.right, y), 0.9f * u)
                var x = rr.left; var k = 0
                while (x <= rr.right + 0.01f) { drawLine(c.copy(alpha = 0.85f), Offset(x, y), Offset(x, y - (if (k % 5 == 0) 3.2f else 1.6f) * u), 0.8f * u); x += 2.4f * u; k++ }
            }
            Forma.RECORTE -> drawRect(c.copy(alpha = 0.8f), Offset(rr.left - 3 * u, rr.top + h * 0.04f), Size(rr.width + 6 * u, h * 0.92f),
                style = Stroke(1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2 * u))))
            Forma.ETIQUETA -> {
                val t = rr.top + h * 0.08f; val b = rr.bottom - h * 0.04f; val punta = rr.left - 7 * u; val hombro = rr.left - 3 * u
                val p = Path().apply { moveTo(punta, medio); lineTo(hombro, t); lineTo(rr.right + 3 * u, t); lineTo(rr.right + 3 * u, b); lineTo(hombro, b); close() }
                drawPath(p, c.copy(alpha = 0.16f)); drawPath(p, c.copy(alpha = 0.75f), style = Stroke(0.8f * u))
                drawCircle(c.copy(alpha = 0.85f), 1f * u, Offset(punta + 3 * u, medio), style = Stroke(0.7f * u))
            }
            Forma.CINTA -> {
                val t = rr.top + h * 0.14f; val b = rr.bottom - h * 0.06f
                val p = Path().apply {
                    moveTo(rr.left - 7 * u, t); lineTo(rr.right + 7 * u, t); lineTo(rr.right + 4 * u, (t + b) / 2); lineTo(rr.right + 7 * u, b)
                    lineTo(rr.left - 7 * u, b); lineTo(rr.left - 4 * u, (t + b) / 2); close()
                }
                drawPath(p, c.copy(alpha = 0.24f))
                drawLine(c.copy(alpha = 0.7f), Offset(rr.left - 3 * u, t), Offset(rr.right + 3 * u, t), 0.7f * u)
                drawLine(c.copy(alpha = 0.7f), Offset(rr.left - 3 * u, b), Offset(rr.right + 3 * u, b), 0.7f * u)
            }
            Forma.ZIGZAG -> {
                val y = rr.bottom - h * 0.06f
                val p = tramo(rr.left, rr.right, 2.4f * u) { x, nx, k -> if (k == 0) moveTo(x, y); lineTo(nx, if (k % 2 == 0) y - 2f * u else y) }
                drawPath(p, c.copy(alpha = 0.85f), style = Stroke(1f * u, join = StrokeJoin.Round))
            }
            Forma.OJIVA -> {
                val iz = rr.left - 2.5f * u; val de = rr.right + 2.5f * u; val pie = rr.bottom - h * 0.04f; val arranque = rr.top + h * 0.5f; val clave = rr.top + h * 0.02f
                val cx = (iz + de) / 2
                val p = Path().apply { moveTo(iz, pie); lineTo(iz, arranque); quadraticTo(iz, clave + (arranque - clave) * 0.25f, cx, clave); quadraticTo(de, clave + (arranque - clave) * 0.25f, de, arranque); lineTo(de, pie) }
                drawPath(p, c.copy(alpha = 0.07f)); drawPath(p, c.copy(alpha = 0.85f), style = Stroke(1f * u))
            }
            Forma.ESQUINAS -> {
                val iz = rr.left - 2.5f * u; val de = rr.right + 2.5f * u; val t = rr.top + h * 0.06f; val b = rr.bottom - h * 0.04f; val l = minOf(4f * u, (de - iz) / 3)
                listOf(Offset(iz, t) to Offset(1f, 1f), Offset(de, t) to Offset(-1f, 1f), Offset(iz, b) to Offset(1f, -1f), Offset(de, b) to Offset(-1f, -1f)).forEach { (o, d) ->
                    drawLine(c, o, Offset(o.x + d.x * l, o.y), 1.2f * u); drawLine(c, o, Offset(o.x, o.y + d.y * l), 1.2f * u)
                }
            }
            Forma.GUIONES -> drawLine(c.copy(alpha = 0.85f), Offset(rr.left, baja), Offset(rr.right, baja), 1.2f * u,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * u, 2.5f * u)))
            Forma.HERRADURA -> {
                // El arco pasa de la media vuelta y se cierra hacia dentro al llegar abajo, como en una puerta califal.
                val alto = h * 1.3f
                drawArc(c.copy(alpha = 0.07f), 155f, 230f, false, Offset(rr.left - 3 * u, rr.top + h * 0.04f), Size(rr.width + 6 * u, alto))
                drawArc(c.copy(alpha = 0.85f), 155f, 230f, false, Offset(rr.left - 3 * u, rr.top + h * 0.04f), Size(rr.width + 6 * u, alto), style = Stroke(1.1f * u))
            }
            Forma.TRENZA -> {
                val y = rr.bottom - h * 0.08f
                listOf(1f, -1f).forEach { s ->
                    val p = tramo(rr.left, rr.right, 3f * u) { x, nx, k -> if (k == 0) moveTo(x, y); quadraticTo((x + nx) / 2, y + s * (if (k % 2 == 0) -2f else 2f) * u, nx, y) }
                    drawPath(p, c.copy(alpha = 0.8f), style = Stroke(0.9f * u))
                }
            }
            Forma.MEDALLON -> {
                val o = Offset(rr.left - 3.6f * u, medio)
                drawCircle(c.copy(alpha = 0.85f), 2.2f * u, o, style = Stroke(1f * u)); drawCircle(c, 0.9f * u, o)
                drawLine(c.copy(alpha = 0.5f), Offset(rr.left, baja), Offset(rr.right, baja), 0.7f * u)
            }
            Forma.PARALELO -> {
                val t = rr.top + h * 0.08f; val b = rr.bottom - h * 0.06f; val sesgo = 3f * u
                drawPath(Path().apply { moveTo(rr.left - 2 * u + sesgo, t); lineTo(rr.right + 3 * u + sesgo, t); lineTo(rr.right + 3 * u - sesgo, b); lineTo(rr.left - 2 * u - sesgo, b); close() }, c)
            }
            Forma.FLECHA -> {
                val y = baja; val fin = rr.right + 4 * u
                drawLine(c.copy(alpha = 0.9f), Offset(rr.left, y), Offset(fin, y), 1.4f * u)
                drawPath(Path().apply { moveTo(fin + 2.5f * u, y); lineTo(fin - 1f * u, y - 2.2f * u); lineTo(fin - 1f * u, y + 2.2f * u); close() }, c.copy(alpha = 0.9f))
            }
            Forma.TRIPLE -> { val y = rr.bottom - h * 0.22f
                listOf(0f, 1.6f, 3.2f).forEachIndexed { k, d -> drawLine(c.copy(alpha = if (k == 1) 0.45f else 0.85f), Offset(rr.left, y + d * u), Offset(rr.right, y + d * u), 0.7f * u) } }
            Forma.PIEDRA -> {
                val iz = rr.left - 3 * u; val de = rr.right + 3 * u; val t = rr.top + h * 0.06f; val b = rr.bottom - h * 0.04f; val ch = minOf(3f * u, h * 0.25f)
                val p = Path().apply {
                    moveTo(iz + ch, t); lineTo(de - ch * 0.6f, t); lineTo(de, t + ch); lineTo(de, b - ch * 0.5f); lineTo(de - ch, b)
                    lineTo(iz + ch * 0.5f, b); lineTo(iz, b - ch); lineTo(iz, t + ch * 0.7f); close()
                }
                drawPath(p, c.copy(alpha = 0.09f)); drawPath(p, c.copy(alpha = 0.8f), style = Stroke(1.1f * u, join = StrokeJoin.Bevel))
            }
            Forma.MUESCAS -> {
                val y = baja
                drawLine(c.copy(alpha = 0.85f), Offset(rr.left, y), Offset(rr.right, y), 0.8f * u)
                var x = rr.left + 1.5f * u
                while (x <= rr.right) { drawLine(c.copy(alpha = 0.85f), Offset(x - 0.8f * u, y + 1.4f * u), Offset(x + 0.8f * u, y - 2.2f * u), 0.8f * u); x += 4f * u }
            }
            Forma.ESCUDETE -> {
                val o = Offset(rr.left - 4.6f * u, medio); val r = minOf(3.4f * u, h * 0.4f)
                val escudo = Path().apply { moveTo(o.x - r * 0.8f, o.y - r * 0.85f); lineTo(o.x + r * 0.8f, o.y - r * 0.85f); quadraticTo(o.x + r * 0.86f, o.y + r * 0.3f, o.x, o.y + r); quadraticTo(o.x - r * 0.86f, o.y + r * 0.3f, o.x - r * 0.8f, o.y - r * 0.85f); close() }
                drawPath(escudo, c.copy(alpha = 0.85f), style = Stroke(0.8f * u))
                drawPath(cruzPatada(Offset(o.x, o.y - r * 0.1f), r * 0.6f), Color(0xFFB0191F))
                drawLine(c.copy(alpha = 0.7f), Offset(rr.left, baja), Offset(rr.right, baja), 1.4f * u)
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
