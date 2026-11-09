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
        NINGUNA, MARCADOR, SUBRAYADO, ONDULADO, RECUADRO, SELLO,
        /** Sello hanko: un bloque de bermellón casi cuadrado, un poco torcido, con su marco en reserva. */
        HANKO, CORCHETES,
        /** Un cabo de dos cordones trenzados bajo la palabra, que remata en un nudo. */
        CABO,
        /** Tabula ansata: recuadro con sus dos asas en cola de milano. */
        CARTELA,
        /** Cartucho egipcio: el óvalo de cuerda que encierra un nombre, con la barra del nudo al final. */
        CARTUCHO,
        /** La placa de mármol de una calle: fondo claro, doble filete y dos tornillos. */
        TARGA,
        /** Marquesina de cabaret: la palabra en su cartel, con una fila de bombillas arriba y otra abajo. */
        MARQUESINA,
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
        /** Un óvalo de tiza trazado dos veces a mano alrededor de la palabra, como las marcas en los muros. */
        TIZA,
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
        /** El bloque de un glifo maya: recuadro redondeado alrededor de la palabra, con su pie de tres lóbulos. */
        GLIFO,
        /** Subrayado de puntos y barras, como un numeral maya tendido. */
        NUMERAL,
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
        /** Escotilla: cápsula alrededor de la palabra con un perno en cada punta. */
        ESCOTILLA,
        /** Subrayado de burbujas de varios tamaños. */
        BURBUJAS,
        /** Escudete con la cruz patada delante de la palabra y un filete de acero debajo. */
        ESCUDETE,
        /** Prehistoria: la nube de ocre soplado detrás de la palabra, con motas en el borde, como alrededor de una mano en la pared. */
        SOPLO,
        /** Billete de tren de cartón: las muescas de los lados y el agujero del picado. */
        BILLETE,
        /** Subrayado de vía: dos carriles sobre sus traviesas. */
        VIA,
        /** Scriptorium: la rúbrica de pluma bajo el nombre, que ondula y se cierra en una lazada al final. */
        RUBRICA,
        /** Scriptorium: el calderón (¶) delante de la palabra, con su ojo lleno y sus dos astas. */
        CALDERON,
        /** Roma: el surco del cincel (más grueso al centro, fino en las puntas) y la interpunción triangular al final. */
        CINCEL,
        /** Gótico: un filete fino que remata en un cuadrifolio de tracería. */
        CUADRIFOLIO,
        /** Refugio: la cinta de la rotuladora, de plástico de color con su brillo, un poco torcida. */
        ROTULADORA,
        /** Refugio: la línea de órdenes de la terminal: el «>» delante, el barrido del fósforo y el cursor «_» detrás. */
        PROMPT,
        /** Imperio: la abeja imperial con las alas abiertas delante del nombre. */
        ABEJA,
        /** Imperio: una rama de laurel bajo la palabra, con las hojas por pares y la baya al final. */
        LAUREL,
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
            // La @persona, en su tabula ansata; el #tema, sobre el surco del cincel con la interpunción de las lápidas.
            TemaId.ROMANO -> if (persona) Aspecto(p, t, Forma.CARTELA, negrita = true) else Aspecto(t, t, Forma.CINCEL)
            // La @persona, como el nombre de una calle en su placa; el #tema, subrayado a mano con una onda.
            TemaId.URBE -> if (persona) Aspecto(cs.onBackground, p, Forma.TARGA, negrita = true) else Aspecto(s, s, Forma.ONDULADO, cursiva = true)
            TemaId.CIBER -> if (persona) Aspecto(cs.background, p, Forma.TERMINAL, negrita = true) else Aspecto(s, s, Forma.CORCHETES)
            TemaId.NEON -> if (persona) Aspecto(cs.background, p, Forma.MARCADOR, negrita = true) else Aspecto(s, s, Forma.ONDULADO)
            TemaId.HELENO -> if (persona) Aspecto(p, t, Forma.GRECA, negrita = true) else Aspecto(s, s, Forma.PUNTEADO)
            // Los nombres van en cartucho, como los de los reyes; los temas, sobre el signo del agua.
            TemaId.EGIPTO -> if (persona) Aspecto(p, t, Forma.CARTUCHO, negrita = true) else Aspecto(s, s, Forma.ZIGZAG)
            // La @persona, con la rúbrica del copista en bermellón (el primario de día, el terciario de noche); el #tema, con el calderón
            // en lapislázuli, como los que abren cada párrafo.
            TemaId.MEDIEVAL -> if (persona) Aspecto(p, if (p.red - p.green >= t.red - t.green) p else t, Forma.RUBRICA, negrita = true, cursiva = true)
                else Aspecto(s, s, Forma.CALDERON, cursiva = true)
            TemaId.TEMPLARIO -> if (persona) Aspecto(p, s, Forma.ESCUDETE, negrita = true) else Aspecto(s, s, Forma.DOBLE, cursiva = true)
            TemaId.DISTOPIA -> if (persona) Aspecto(cs.background, cs.onBackground, Forma.BARRA, negrita = true) else Aspecto(p, p, Forma.GOMA)
            // La @persona, en la cinta de la rotuladora con las letras en relieve; el #tema, en la línea de órdenes de la terminal.
            TemaId.REFUGIO -> if (persona) Aspecto(cs.onPrimary, p, Forma.ROTULADORA, negrita = true) else Aspecto(s, s, Forma.PROMPT)
            TemaId.ORDEN -> if (persona) Aspecto(p, p, Forma.PLACA, negrita = true) else Aspecto(t, t, Forma.DOBLE)
            // Las personas, en un billete de tren de cartón (picado); los temas, sobre la vía.
            TemaId.VAPOR -> if (persona) Aspecto(cs.onBackground, p, Forma.BILLETE, negrita = true) else Aspecto(s, s, Forma.VIA)
            TemaId.LABORATORIO -> if (persona) Aspecto(p, p, Forma.MARCADOR, negrita = true) else Aspecto(s, s, Forma.ESCALA)
            TemaId.COLONOS -> if (persona) Aspecto(p, p, Forma.RECORTE, negrita = true) else Aspecto(s, s, Forma.ONDULADO)
            // La @persona, con la abeja imperial; el #tema, sobre una rama de laurel. Las dos, en oro (el terciario de día, el primario de noche).
            TemaId.NAPOLEON -> {
                val oro = if (p.blue > p.red) t else p
                if (persona) Aspecto(p, oro, Forma.ABEJA, negrita = true, cursiva = true) else Aspecto(s, oro, Forma.LAUREL, cursiva = true)
            }
            // La @persona, en la marquesina del cabaret, con sus bombillas; el #tema, con tres líneas de velocidad debajo.
            TemaId.DECO -> if (persona) Aspecto(cs.onBackground, p, Forma.MARQUESINA, negrita = true) else Aspecto(s, s, Forma.TRIPLE)
            TemaId.BOTANICO -> if (persona) Aspecto(p, s, Forma.ETIQUETA, negrita = true) else Aspecto(t, t, Forma.MARCADOR)
            TemaId.RUINAS -> if (persona) Aspecto(p, s, Forma.TIZA, negrita = true) else Aspecto(t, t, Forma.GUIONES, cursiva = true)
            TemaId.NAVIDAD -> if (persona) Aspecto(p, p, Forma.CINTA, negrita = true) else Aspecto(s, s, Forma.ZIGZAG)
            TemaId.SEDA -> if (persona) Aspecto(cs.onPrimary, p, Forma.SELLO, negrita = true) else Aspecto(s, s, Forma.RECUADRO)
            // La persona, estampada con el sello de bermellón (el secundario de día, el primario de noche); lo demás, punteado en verde pino.
            TemaId.EDO -> if (persona) Aspecto(Color(0xFFFFF8F0), if (p.red > p.blue) p else s, Forma.HANKO, negrita = true) else Aspecto(t, t, Forma.PUNTEADO, cursiva = true)
            // La @persona, bajo su arco apuntado; el #tema, con un filete que remata en un cuadrifolio de tracería.
            TemaId.GOTICO -> if (persona) Aspecto(p, t, Forma.OJIVA, negrita = true) else Aspecto(s, s, Forma.CUADRIFOLIO, cursiva = true)
            // Los testigos, entre los corchetes de una nota del archivo; los asuntos, subrayados a lápiz con puntos.
            TemaId.ARCANO -> if (persona) Aspecto(t, s, Forma.CORCHETES, negrita = true) else Aspecto(p, s, Forma.PUNTEADO, cursiva = true)
            TemaId.ESPACIO -> if (persona) Aspecto(t, t, Forma.ESQUINAS, negrita = true) else Aspecto(p, p, Forma.GUIONES)
            TemaId.ABISMO -> if (persona) Aspecto(p, s, Forma.ESCOTILLA, negrita = true) else Aspecto(t, t, Forma.BURBUJAS)
            TemaId.ANDALUS -> if (persona) Aspecto(p, t, Forma.HERRADURA, negrita = true) else Aspecto(s, s, Forma.TRENZA)
            TemaId.MESO -> if (persona) Aspecto(p, s, Forma.GLIFO, negrita = true) else Aspecto(t, t, Forma.NUMERAL)
            TemaId.MONGOL -> if (persona) Aspecto(p, t, Forma.MEDALLON, negrita = true) else Aspecto(s, s, Forma.MARCADOR)
            TemaId.RENACIMIENTO -> if (persona) Aspecto(p, p, Forma.ONDULADO, cursiva = true) else Aspecto(s, s, Forma.SUBRAYADO, cursiva = true)
            TemaId.PIRATA -> if (persona) Aspecto(p, s, Forma.CABO, negrita = true) else Aspecto(t, t, Forma.PUNTEADO, cursiva = true)
            TemaId.URSS -> if (persona) Aspecto(cs.onPrimary, p, Forma.PARALELO, negrita = true) else Aspecto(cs.onBackground, p, Forma.FLECHA, negrita = true)
            TemaId.REVOLUCION -> if (persona) Aspecto(p, s, Forma.TRIPLE, negrita = true) else Aspecto(s, s, Forma.RECUADRO)
            TemaId.ANTIGUAS -> if (persona) Aspecto(p, s, Forma.PIEDRA, negrita = true) else Aspecto(t, t, Forma.MUESCAS, cursiva = true)
            // Las personas, en la nube de ocre soplado, como una mano en la pared; los asuntos, sobre una hilera de puntos.
            TemaId.CUEVA -> if (persona) Aspecto(cs.onBackground, p, Forma.SOPLO, negrita = true) else Aspecto(p, p, Forma.PUNTEADO)
            TemaId.BASICO -> if (persona) Aspecto(p, p, Forma.SUBRAYADO, negrita = true) else Aspecto(s, s, Forma.NINGUNA)
        }
    }

    /** En un campo de texto no se puede dibujar: el fondo de la letra que sustituye a cada forma (o nada). */
    private fun fondoCampo(f: Forma, c: Color): Color = when (f) {
        Forma.SELLO, Forma.HANKO, Forma.TERMINAL, Forma.BARRA, Forma.PARALELO -> c
        Forma.MARCADOR, Forma.CINTA, Forma.PLACA, Forma.ETIQUETA -> c.copy(alpha = 0.22f)
        Forma.RECUADRO, Forma.CORCHETES, Forma.CARTELA, Forma.TARGA, Forma.CARTUCHO, Forma.GOMA, Forma.RECORTE, Forma.OJIVA, Forma.ESQUINAS, Forma.HERRADURA, Forma.PIEDRA, Forma.ESCOTILLA -> c.copy(alpha = 0.12f)
        Forma.TIZA -> c.copy(alpha = 0.12f)
        Forma.MARQUESINA -> c.copy(alpha = 0.16f)
        Forma.NINGUNA, Forma.SUBRAYADO, Forma.ONDULADO, Forma.GRECA, Forma.PUNTEADO, Forma.DOBLE, Forma.ESCALA, Forma.ZIGZAG, Forma.GUIONES,
        Forma.TRENZA, Forma.MEDALLON, Forma.FLECHA, Forma.TRIPLE, Forma.MUESCAS, Forma.BURBUJAS -> Color.Unspecified
        Forma.CABO -> Color.Unspecified
        Forma.ESCUDETE -> c.copy(alpha = 0.12f)
        Forma.GLIFO -> c.copy(alpha = 0.12f)
        Forma.NUMERAL -> Color.Unspecified
        Forma.SOPLO -> c.copy(alpha = 0.18f)
        Forma.BILLETE -> c.copy(alpha = 0.16f)
        Forma.VIA -> Color.Unspecified
        Forma.RUBRICA, Forma.CINCEL, Forma.CUADRIFOLIO, Forma.LAUREL -> Color.Unspecified
        Forma.CALDERON, Forma.PROMPT -> c.copy(alpha = 0.12f)
        Forma.ROTULADORA -> c
        Forma.ABEJA -> c.copy(alpha = 0.14f)
    }

    /** En un campo de texto, las formas que van debajo de la palabra se quedan en un subrayado. */
    private fun subrayaCampo(f: Forma): Boolean = when (f) {
        Forma.SUBRAYADO, Forma.ONDULADO, Forma.GRECA, Forma.PUNTEADO, Forma.DOBLE, Forma.ESCALA, Forma.ZIGZAG, Forma.GUIONES,
        Forma.TRENZA, Forma.MEDALLON, Forma.FLECHA, Forma.TRIPLE, Forma.MUESCAS -> true
        Forma.CABO, Forma.BURBUJAS -> true
        Forma.NINGUNA, Forma.MARCADOR, Forma.RECUADRO, Forma.SELLO, Forma.HANKO, Forma.CORCHETES, Forma.CARTELA, Forma.TARGA, Forma.CARTUCHO, Forma.TERMINAL, Forma.BARRA, Forma.GOMA,
        Forma.PLACA, Forma.RECORTE, Forma.ETIQUETA, Forma.CINTA, Forma.OJIVA, Forma.ESQUINAS, Forma.HERRADURA, Forma.PARALELO, Forma.PIEDRA, Forma.ESCOTILLA -> false
        Forma.TIZA -> false
        Forma.MARQUESINA -> false
        Forma.ESCUDETE -> true
        Forma.GLIFO -> false
        Forma.NUMERAL -> true
        Forma.SOPLO -> false
        Forma.BILLETE -> false
        Forma.VIA -> true
        Forma.RUBRICA, Forma.CINCEL, Forma.CUADRIFOLIO, Forma.LAUREL -> true
        Forma.CALDERON, Forma.ROTULADORA, Forma.PROMPT, Forma.ABEJA -> false
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
        val p = Path(); var x = x0; var k = 0; val pa = maxOf(paso, 1f)
        while (x < x1 - 0.01f && k < 600) { val nx = minOf(x + pa, x1); p.f(x, nx, k); x = nx; k++ }
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
                var x = rr.left; var arriba = true; val paso = maxOf(3f * u, 1f); var n = 0
                while (x < rr.right && n++ < 600) { val nx = minOf(x + paso, rr.right); p.quadraticTo((x + nx) / 2, if (arriba) y - 2f * u else y + 2f * u, nx, y); x = nx; arriba = !arriba }
                drawPath(p, c.copy(alpha = 0.85f), style = Stroke(1.2f * u))
            }
            Forma.CABO -> {
                // Dos cordones que se enroscan (dos ondas a media vuelta una de otra) y el nudo al final.
                val y = rr.bottom - h * 0.06f; val fin = rr.right + 2 * u
                listOf(0, 1).forEach { s ->
                    val p = tramo(rr.left, fin, 2.4f * u) { x, nx, k -> if (k == 0) moveTo(x, y); quadraticTo((x + nx) / 2, y + (if ((k + s) % 2 == 0) -1.8f else 1.8f) * u, nx, y) }
                    drawPath(p, c.copy(alpha = if (s == 0) 0.85f else 0.55f), style = Stroke(1.1f * u, cap = StrokeCap.Round))
                }
                drawCircle(c.copy(alpha = 0.9f), 1.8f * u, Offset(fin + 1.4f * u, y))
                drawLine(c.copy(alpha = 0.7f), Offset(fin + 1.4f * u, y), Offset(fin + 3.4f * u, y + 2.6f * u), 0.9f * u, StrokeCap.Round)
            }
            Forma.RECUADRO -> drawRoundRect(c.copy(alpha = 0.7f), Offset(rr.left - 2 * u, rr.top + h * 0.08f), Size(rr.width + 4 * u, h * 0.86f),
                CornerRadius(2 * u, 2 * u), style = Stroke(1f * u))
            Forma.SELLO -> drawRoundRect(c, Offset(rr.left - 3 * u, rr.top + h * 0.06f), Size(rr.width + 6 * u, h * 0.88f), CornerRadius(h / 2, h / 2))
            Forma.HANKO -> rotate(-2f, rr.center) {
                drawRoundRect(c, Offset(rr.left - 3 * u, rr.top + h * 0.04f), Size(rr.width + 6 * u, h * 0.92f), CornerRadius(1.5f * u, 1.5f * u))
                drawRect(Color.White.copy(alpha = 0.55f), Offset(rr.left - 1.5f * u, rr.top + h * 0.04f + 1.5f * u), Size(rr.width + 3 * u, h * 0.92f - 3 * u), style = Stroke(0.7f * u))
            }
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
            Forma.CARTUCHO -> {
                // El óvalo de cuerda alrededor del nombre y, al final, la barra del nudo.
                val t = rr.top + h * 0.06f; val b = rr.bottom - h * 0.04f; val iz = rr.left - 4 * u; val de = rr.right + 4 * u
                drawRoundRect(c.copy(alpha = 0.08f), Offset(iz, t), Size(de - iz, b - t), CornerRadius((b - t) / 2, (b - t) / 2))
                drawRoundRect(c.copy(alpha = 0.85f), Offset(iz, t), Size(de - iz, b - t), CornerRadius((b - t) / 2, (b - t) / 2), style = Stroke(1.1f * u))
                drawLine(c.copy(alpha = 0.85f), Offset(de + 1.8f * u, t - 1f * u), Offset(de + 1.8f * u, b + 1f * u), 1.8f * u)
            }
            Forma.TARGA -> {
                // La placa de la calle: el mármol claro, el marco de dos filetes y un tornillo a cada lado.
                val t = rr.top + h * 0.06f; val b = rr.bottom - h * 0.04f; val iz = rr.left - 5 * u; val de = rr.right + 5 * u
                drawRect(Color(0xFFF4EEE2).copy(alpha = 0.55f), Offset(iz, t), Size(de - iz, b - t))
                drawRect(c.copy(alpha = 0.85f), Offset(iz, t), Size(de - iz, b - t), style = Stroke(1.2f * u))
                drawRect(c.copy(alpha = 0.45f), Offset(iz + 1.8f * u, t + 1.8f * u), Size(de - iz - 3.6f * u, b - t - 3.6f * u), style = Stroke(0.6f * u))
                listOf(iz + 2.6f * u, de - 2.6f * u).forEach { x -> drawCircle(c.copy(alpha = 0.8f), 0.9f * u, Offset(x, medio)) }
            }
            Forma.MARQUESINA -> {
                // El cartel de la marquesina: fondo de oro muy tenue, doble filete y las bombillas encendidas arriba y abajo.
                val t = rr.top + h * 0.02f; val b = rr.bottom; val iz = rr.left - 5 * u; val de = rr.right + 5 * u
                drawRect(c.copy(alpha = 0.14f), Offset(iz, t), Size(de - iz, b - t))
                drawRect(c.copy(alpha = 0.85f), Offset(iz, t), Size(de - iz, b - t), style = Stroke(1.1f * u))
                var x = iz + 3 * u; val paso = maxOf(4.2f * u, 1f); var n = 0
                while (x < de - 1.5f * u && n++ < 400) { drawCircle(c.copy(alpha = 0.9f), 0.8f * u, Offset(x, t + 1.6f * u)); drawCircle(c.copy(alpha = 0.9f), 0.8f * u, Offset(x, b - 1.6f * u)); x += paso }
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
            Forma.PUNTEADO -> { var x = rr.left + 0.8f * u; val paso = maxOf(3f * u, 1f); var n = 0; while (x <= rr.right && n++ < 600) { drawCircle(c.copy(alpha = 0.85f), 0.8f * u, Offset(x, baja)); x += paso } }
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
                var x = rr.left; var k = 0; val paso = maxOf(2.4f * u, 1f)
                while (x <= rr.right + 0.01f && k < 600) { drawLine(c.copy(alpha = 0.85f), Offset(x, y), Offset(x, y - (if (k % 5 == 0) 3.2f else 1.6f) * u), 0.8f * u); x += paso; k++ }
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
            Forma.GLIFO -> {
                // El recuadro redondeado del bloque de glifo y, debajo, el pie de tres lóbulos.
                val t = rr.top + h * 0.02f; val b = rr.bottom - h * 0.12f; val iz = rr.left - 3 * u; val de = rr.right + 3 * u
                drawRoundRect(c.copy(alpha = 0.08f), Offset(iz, t), Size(de - iz, b - t), CornerRadius(4 * u, 4 * u))
                drawRoundRect(c.copy(alpha = 0.85f), Offset(iz, t), Size(de - iz, b - t), CornerRadius(4 * u, 4 * u), style = Stroke(1.1f * u))
                for (k in -1..1) drawCircle(c.copy(alpha = 0.85f), 1.3f * u, Offset((iz + de) / 2 + k * 3.4f * u, b + 1.6f * u))
            }
            Forma.NUMERAL -> {
                // Cuatro puntos y una barra, y otra vez, a lo largo de la palabra.
                var x = rr.left; var k = 0
                while (x < rr.right && k < 600) {
                    if (k % 5 == 4) { drawRect(c.copy(alpha = 0.8f), Offset(x, baja - 0.7f * u), Size(minOf(6 * u, rr.right - x), 1.4f * u)); x += maxOf(8 * u, 1f) }
                    else { drawCircle(c.copy(alpha = 0.8f), 0.9f * u, Offset(x + 0.9f * u, baja)); x += maxOf(3.4f * u, 1f) }
                    k++
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
            Forma.ESCOTILLA -> {
                // La cápsula de la escotilla: llena muy tenue, con su borde y un perno de latón en cada punta.
                val iz = rr.left - 5 * u; val de = rr.right + 5 * u; val t = rr.top + h * 0.06f; val b = rr.bottom - h * 0.04f; val r = (b - t) / 2
                drawRoundRect(c.copy(alpha = 0.1f), Offset(iz, t), Size(de - iz, b - t), CornerRadius(r, r))
                drawRoundRect(c.copy(alpha = 0.85f), Offset(iz, t), Size(de - iz, b - t), CornerRadius(r, r), style = Stroke(1.1f * u))
                listOf(iz + r * 0.55f, de - r * 0.55f).forEach { x -> drawCircle(c.copy(alpha = 0.85f), 0.9f * u, Offset(x, medio)) }
            }
            Forma.BURBUJAS -> {
                // Una fila de burbujas bajo la palabra, unas más grandes que otras y a distinta altura.
                var x = rr.left + 1.5f * u; var k = 0
                while (x <= rr.right && k < 600) {
                    val r = (if (k % 3 == 1) 1.5f else 1f) * u
                    drawCircle(c.copy(alpha = 0.85f), r, Offset(x, baja - (k % 2) * 1.2f * u), style = Stroke(0.7f * u))
                    x += maxOf(4.5f * u, 1f); k++
                }
            }
            Forma.TIZA -> {
                // Dos vueltas de tiza, un poco corridas una de otra, y la punta que se pasa al cerrar.
                val iz = rr.left - 4 * u; val de = rr.right + 4 * u; val t = rr.top + h * 0.02f; val b = rr.bottom
                drawOval(c.copy(alpha = 0.75f), Offset(iz, t), Size(de - iz, b - t), style = Stroke(1.1f * u))
                rotate(-2.5f, Offset(rr.center.x, rr.center.y)) { drawOval(c.copy(alpha = 0.45f), Offset(iz + 1.5f * u, t - 1f * u), Size(de - iz - 2 * u, b - t + 1.5f * u), style = Stroke(0.8f * u)) }
                drawLine(c.copy(alpha = 0.6f), Offset(de - 6 * u, t - 1.5f * u), Offset(de + 1 * u, t + 2.5f * u), 0.9f * u, StrokeCap.Round)
            }
            Forma.MUESCAS -> {
                val y = baja
                drawLine(c.copy(alpha = 0.85f), Offset(rr.left, y), Offset(rr.right, y), 0.8f * u)
                var x = rr.left + 1.5f * u; val paso = maxOf(4f * u, 1f); var n = 0
                while (x <= rr.right && n++ < 600) { drawLine(c.copy(alpha = 0.85f), Offset(x - 0.8f * u, y + 1.4f * u), Offset(x + 0.8f * u, y - 2.2f * u), 0.8f * u); x += paso }
            }
            Forma.ESCUDETE -> {
                val o = Offset(rr.left - 4.6f * u, medio); val r = minOf(3.4f * u, h * 0.4f)
                val escudo = Path().apply { moveTo(o.x - r * 0.8f, o.y - r * 0.85f); lineTo(o.x + r * 0.8f, o.y - r * 0.85f); quadraticTo(o.x + r * 0.86f, o.y + r * 0.3f, o.x, o.y + r); quadraticTo(o.x - r * 0.86f, o.y + r * 0.3f, o.x - r * 0.8f, o.y - r * 0.85f); close() }
                drawPath(escudo, c.copy(alpha = 0.85f), style = Stroke(0.8f * u))
                drawPath(cruzPatada(Offset(o.x, o.y - r * 0.1f), r * 0.6f), Color(0xFFB0191F))
                drawLine(c.copy(alpha = 0.7f), Offset(rr.left, baja), Offset(rr.right, baja), 1.4f * u)
            }
            Forma.SOPLO -> {
                // Una nube de ocre soplado detrás de la palabra: el centro más denso y motas sueltas en el borde (fijas por la posición).
                val ce = rr.center; val rx = rr.width / 2 + 6 * u; val ry = h * 0.62f
                drawOval(androidx.compose.ui.graphics.Brush.radialGradient(listOf(c.copy(alpha = 0.3f), c.copy(alpha = 0.16f), Color.Transparent), ce, maxOf(rx, ry)), Offset(ce.x - rx, ce.y - ry), Size(rx * 2, ry * 2))
                val az = java.util.Random((rr.left * 31 + rr.top).toLong())
                repeat(10) {
                    val a = az.nextFloat() * 2 * kotlin.math.PI.toFloat(); val k = 0.85f + az.nextFloat() * 0.3f
                    drawCircle(c.copy(alpha = 0.35f), (0.6f + az.nextFloat() * 0.8f) * u, Offset(ce.x + rx * k * kotlin.math.cos(a), ce.y + ry * k * kotlin.math.sin(a)))
                }
            }
            Forma.BILLETE -> {
                // El billete de cartón: fondo color crema, el contorno con una muesca redonda a cada lado y el agujero del picado.
                val t = rr.top + h * 0.06f; val b = rr.bottom - h * 0.04f; val iz = rr.left - 6 * u; val de = rr.right + 6 * u; val m = (t + b) / 2; val k = 1.8f * u
                val billete = Path().apply {
                    moveTo(iz, t); lineTo(de, t); lineTo(de, m - k); quadraticTo(de - k, m, de, m + k); lineTo(de, b); lineTo(iz, b); lineTo(iz, m + k); quadraticTo(iz + k, m, iz, m - k); close()
                }
                drawPath(billete, Color(0xFFE9D9A8).copy(alpha = 0.6f))
                drawPath(billete, c.copy(alpha = 0.85f), style = Stroke(0.9f * u))
                drawCircle(c.copy(alpha = 0.7f), 0.9f * u, Offset(de - 3.4f * u, t + 2.4f * u))
            }
            Forma.VIA -> {
                val y = baja
                var x = rr.left; val paso = maxOf(3.2f * u, 1f); var n = 0
                while (x <= rr.right && n++ < 600) { drawLine(c.copy(alpha = 0.5f), Offset(x, y - 2.2f * u), Offset(x, y + 1.4f * u), 1.1f * u); x += paso }
                drawLine(c.copy(alpha = 0.9f), Offset(rr.left - u, y - 1.2f * u), Offset(rr.right + u, y - 1.2f * u), 0.8f * u)
                drawLine(c.copy(alpha = 0.9f), Offset(rr.left - u, y + 0.6f * u), Offset(rr.right + u, y + 0.6f * u), 0.8f * u)
            }
            Forma.RUBRICA -> {
                // La rúbrica del copista: el perfil fino de entrada, un trazo de pluma que ondula bajo el nombre y, al final,
                // la lazada que vuelve sobre sí misma y sale hacia la derecha.
                val y = rr.bottom - h * 0.1f; val iz = rr.left - 1.5f * u; val de = rr.right; val w = de - iz
                val p = Path().apply {
                    moveTo(iz, y - 0.8f * u)
                    cubicTo(iz + w * 0.3f, y + 1.4f * u, iz + w * 0.7f, y - 1.4f * u, de, y)
                    cubicTo(de + 3f * u, y + 0.6f * u, de + 3.6f * u, y - 3.2f * u, de + 1.8f * u, y - 3.2f * u)
                    cubicTo(de + 0.2f * u, y - 3.2f * u, de + 0.6f * u, y + 1.8f * u, de + 4.8f * u, y + 0.6f * u)
                }
                drawPath(p, c.copy(alpha = 0.9f), style = Stroke(1.2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawLine(c.copy(alpha = 0.6f), Offset(iz - 1.2f * u, y - 2.6f * u), Offset(iz, y - 0.8f * u), 0.5f * u, StrokeCap.Round)
            }
            Forma.CALDERON -> {
                // El calderón (¶) delante de la palabra: el ojo lleno a la izquierda, la barra de arriba y las dos astas.
                val t = rr.top + h * 0.24f; val b = rr.bottom - h * 0.2f; val alto = b - t
                val de = rr.left - 1.2f * u; val ancho = minOf(alto * 0.5f, 4.2f * u)
                val asta = de - ancho * 0.38f; val ojo = alto * 0.52f
                val col = c.copy(alpha = 0.9f)
                drawPath(Path().apply { moveTo(asta, t); lineTo(asta, t + ojo); cubicTo(asta - ancho * 0.85f, t + ojo, asta - ancho * 0.85f, t, asta, t); close() }, col)
                drawLine(col, Offset(asta, t), Offset(de, t), 0.9f * u)
                drawLine(col, Offset(asta, t), Offset(asta, b), 0.9f * u)
                drawLine(col, Offset(de, t), Offset(de, b), 0.9f * u)
            }
            Forma.CINCEL -> {
                // El surco en V del cincel: un filete que engorda al centro y se afina en las puntas; detrás, la interpunción triangular.
                val y = baja; val g = 0.85f * u; val mx = (rr.left + rr.right) / 2
                drawPath(Path().apply { moveTo(rr.left, y); quadraticTo(mx, y - 2 * g, rr.right, y); quadraticTo(mx, y + 2 * g, rr.left, y); close() }, c.copy(alpha = 0.85f))
                val x0 = rr.right + 1.4f * u; val l = 2.2f * u; val yi = rr.top + h * 0.56f
                drawPath(Path().apply { moveTo(x0, yi - l * 0.45f); lineTo(x0 + l, yi - l * 0.45f); lineTo(x0 + l / 2, yi + l * 0.5f); close() }, c.copy(alpha = 0.85f))
            }
            Forma.CUADRIFOLIO -> {
                // Un filete fino que remata en un cuadrifolio: cuatro lóbulos que dejan un ojo pequeño en medio, como en la tracería.
                val y = baja - 0.8f * u; val r = 1.15f * u; val d = 1.25f * u; val o = Offset(rr.right + 2.4f * u, y)
                drawLine(c.copy(alpha = 0.85f), Offset(rr.left, y), Offset(o.x - d, y), 0.9f * u)
                drawCircle(c.copy(alpha = 0.85f), 0.7f * u, Offset(rr.left, y))
                val flor = Path().apply { listOf(Offset(d, 0f), Offset(-d, 0f), Offset(0f, d), Offset(0f, -d)).forEach { addOval(Rect(o + it, r)) } }
                drawPath(flor, c.copy(alpha = 0.85f))
            }
            Forma.ROTULADORA -> rotate(1.2f, rr.center) {
                // La cinta de la rotuladora: el plástico de color con las puntas un poco redondeadas, el brillo arriba y el canto claro abajo.
                val t = rr.top + h * 0.1f; val b = rr.bottom - h * 0.06f; val iz = rr.left - 4 * u; val de = rr.right + 4 * u
                drawRoundRect(c.copy(alpha = 0.92f), Offset(iz, t), Size(de - iz, b - t), CornerRadius(1.2f * u, 1.2f * u))
                drawRect(Color.White.copy(alpha = 0.16f), Offset(iz + 1f * u, t + 0.8f * u), Size(de - iz - 2 * u, (b - t) * 0.28f))
                drawLine(Color.White.copy(alpha = 0.3f), Offset(iz + 1.2f * u, b - 1f * u), Offset(de - 1.2f * u, b - 1f * u), 0.5f * u)
            }
            Forma.PROMPT -> {
                // La línea de órdenes: el «>» delante, unas líneas de barrido del fósforo sobre la palabra y el cursor «_» esperando detrás.
                val t = rr.top + h * 0.12f; val b = rr.bottom - h * 0.08f; val cy = (t + b) / 2; val a = minOf(1.9f * u, (b - t) * 0.3f)
                val x0 = rr.left - 4.2f * u
                drawPath(Path().apply { moveTo(x0, cy - a); lineTo(x0 + a * 1.2f, cy); lineTo(x0, cy + a) }, c, style = Stroke(1.2f * u, cap = StrokeCap.Square))
                val paso = maxOf(2f * u, 1f); var y = t + paso / 2; var n = 0
                while (y < b && n < 40) { drawLine(c.copy(alpha = 0.12f), Offset(rr.left - 1f * u, y), Offset(rr.right + 1f * u, y), 0.6f * u); y += paso; n++ }
                drawRect(c.copy(alpha = 0.9f), Offset(rr.right + 1.4f * u, b - 1.8f * u), Size(3.6f * u, 1.5f * u))
            }
            Forma.ABEJA -> {
                // La abeja imperial vista desde arriba: las alas abiertas (llenas muy tenues, con su contorno), el abdomen, el tórax,
                // la cabeza y las antenas.
                val r = minOf(3.2f * u, h * 0.34f); val o = Offset(rr.left - r - 1f * u, medio)
                val ala = Size(r * 1.25f, r * 0.6f)
                listOf(30f to -1f, -30f to 1f).forEach { (giro, lado) ->
                    rotate(giro, o) {
                        val esq = Offset(if (lado < 0) o.x - ala.width else o.x, o.y - r * 0.2f - ala.height / 2)
                        drawOval(c.copy(alpha = 0.28f), esq, ala)
                        drawOval(c.copy(alpha = 0.85f), esq, ala, style = Stroke(0.6f * u))
                    }
                }
                drawOval(c, Offset(o.x - r * 0.3f, o.y), Size(r * 0.6f, r * 1.05f))
                drawCircle(c, r * 0.27f, Offset(o.x, o.y - r * 0.22f))
                drawCircle(c, r * 0.2f, Offset(o.x, o.y - r * 0.62f))
                listOf(-1f, 1f).forEach { s ->
                    drawLine(c.copy(alpha = 0.8f), Offset(o.x + s * r * 0.08f, o.y - r * 0.76f), Offset(o.x + s * r * 0.34f, o.y - r * 1.02f), 0.5f * u, StrokeCap.Round)
                }
            }
            Forma.LAUREL -> {
                // La rama de laurel: el tallo bajo la palabra, las hojas alternas (una arriba, otra abajo) que miran hacia delante
                // y, al final, dos bayas.
                val y = baja - 0.6f * u; val hoja = Size(3f * u, 1.3f * u)
                drawLine(c.copy(alpha = 0.8f), Offset(rr.left, y), Offset(rr.right + 0.6f * u, y), 0.7f * u)
                val paso = maxOf(2.6f * u, 1f); var x = rr.left + 0.4f * u; var n = 0
                while (x <= rr.right - hoja.width * 0.7f && n < 160) {
                    rotate(if (n % 2 == 0) -38f else 38f, Offset(x, y)) { drawOval(c.copy(alpha = 0.85f), Offset(x, y - hoja.height / 2), hoja) }
                    x += paso; n++
                }
                drawCircle(c.copy(alpha = 0.9f), 0.9f * u, Offset(rr.right + 1.4f * u, y - 0.9f * u))
                drawCircle(c.copy(alpha = 0.9f), 0.9f * u, Offset(rr.right + 1.8f * u, y + 1f * u))
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
