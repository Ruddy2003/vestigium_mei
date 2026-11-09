package io.github.ruddy2003.vestigium.ui.comun

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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.PaletaBasica
import io.github.ruddy2003.vestigium.ui.TonosBasicos
import io.github.ruddy2003.vestigium.ui.biblioteca.textoEn
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

// =====================================================================
//  Básico: las piezas comunes sin diseño
// =====================================================================

/**
 * Básico: las piezas comunes sin diseño, a propósito, a la manera de Material 3. Campos con su contorno redondeado (el
 * acento con el foco, el rojo con el error), separadores de una línea fina, botones de texto en el acento con un fondo
 * tenue al tocarlos, tarjetas lisas de 12 dp con borde de 1 dp, avisos sobre el acento suave con su «i», la balanza como
 * un botón segmentado y el encabezado del mes con dos flechas. Colores de [PaletaBasica]; sin texturas ni ornamentos.
 */
object ComunBasico : DisenoComun {
    private val LETRA: FontFamily = FontFamily.Default

    private fun tonos(oscuro: Boolean): TonosBasicos = PaletaBasica.de(oscuro)

    /** Un contorno redondeado por dentro del lienzo, de [grosor] píxeles. */
    private fun DrawScope.contorno(color: Color, radio: Float, grosor: Float, efecto: PathEffect? = null) =
        drawRoundRect(color, Offset(grosor / 2, grosor / 2), Size(size.width - grosor, size.height - grosor), CornerRadius(radio),
            style = Stroke(grosor, pathEffect = efecto))

    /** Un renglón de texto alineado a la izquierda en [r], centrado en alto; si no cabe, se corta con «…». */
    private fun DrawScope.textoIzq(medidor: TextMeasurer, texto: String, r: Rect, color: Color, tam: Float, peso: FontWeight = FontWeight.Normal) {
        if (texto.isBlank() || r.width <= 1f || r.height <= 1f) return
        val capa = medidor.measure(texto, TextStyle(fontFamily = LETRA, fontSize = tam.sp, fontWeight = peso, color = color), maxLines = 1,
            softWrap = false, overflow = TextOverflow.Ellipsis, constraints = Constraints(maxWidth = r.width.toInt().coerceAtLeast(1)))
        drawText(capa, topLeft = Offset(r.left, r.center.y - capa.size.height / 2f))
    }

    /** Una flecha « › » (o « ‹ » con [izquierda]) centrada en [c], de [lado] de alto. */
    private fun DrawScope.chevron(c: Offset, lado: Float, color: Color, izquierda: Boolean = false) {
        val s = if (izquierda) -1f else 1f
        drawPath(Path().apply { moveTo(c.x - s * lado * 0.22f, c.y - lado * 0.45f); lineTo(c.x + s * lado * 0.22f, c.y); lineTo(c.x - s * lado * 0.22f, c.y + lado * 0.45f) },
            color, style = Stroke(lado * 0.14f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 12.dp
    override fun tintaCampo(oscuro: Boolean): Color = tonos(oscuro).texto
    override fun pistaCampo(oscuro: Boolean): Color = tonos(oscuro).tenue
    override fun rotuloCampo(oscuro: Boolean): Color = tonos(oscuro).acento
    override fun errorCampo(oscuro: Boolean): Color = tonos(oscuro).error

    /** El campo: la superficie con su contorno de 8 dp; con el foco, el acento de 2 dp; con el error, el rojo. */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        val radio = 8 * u
        drawRoundRect(if (e.habilitado) c.superficie else c.variante, cornerRadius = CornerRadius(radio))
        when {
            e.error -> contorno(c.error, radio, 2 * u)
            e.enfocado && e.habilitado -> contorno(c.acento, radio, 2 * u)
            e.habilitado -> contorno(c.borde, radio, u)
            else -> contorno(c.bordeSuave, radio, u)
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 9.dp

    /** Una línea fina a todo el ancho. */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val c = tonos(oscuro)
        drawLine(if (e.tenue) c.bordeSuave.copy(alpha = 0.6f) else c.bordeSuave, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), density)
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = tonos(oscuro).acento

    /** El botón de texto: nada detrás; al tocarlo, una pastilla tenue del acento. */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        if (e.presionado && e.habilitado) drawRoundRect(tonos(oscuro).acento.copy(alpha = 0.12f), cornerRadius = CornerRadius(size.height / 2))
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    override val margenFila: Dp = 16.dp

    /** La fila de menú: nada detrás; al tocarla, un fondo tenue. */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        if (e.presionado) drawRoundRect(tonos(oscuro).acento.copy(alpha = 0.08f), cornerRadius = CornerRadius(8 * density))
    }

    override fun DrawScope.flechaFila(oscuro: Boolean) {
        chevron(center, size.minDimension * 0.6f, tonos(oscuro).tenue)
    }

    /** La marca para fijar: una estrella, llena con el acento si está fija. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val c = tonos(oscuro)
        val p = estrella(center, size.minDimension * 0.42f, 5, 0.45f)
        if (fija) drawPath(p, c.acento) else drawPath(p, c.tenue, style = Stroke(1.4f * density, join = StrokeJoin.Round))
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = tonos(oscuro).tenue

    /** El recuadro de lo vacío: un contorno de guiones. */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        contorno(tonos(oscuro).bordeSuave, 12 * u, u, PathEffect.dashPathEffect(floatArrayOf(5 * u, 4 * u)))
    }

    /** Lo vacío: una caja abierta, en el tono tenue. */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val s = size.minDimension; val u = density
        val col = tonos(oscuro).tenue
        val x0 = (size.width - s) / 2 + s * 0.14f; val x1 = (size.width + s) / 2 - s * 0.14f
        val y0 = (size.height - s) / 2 + s * 0.36f; val y1 = (size.height + s) / 2 - s * 0.16f
        drawRoundRect(col, Offset(x0, y0), Size(x1 - x0, y1 - y0), CornerRadius(3 * u), style = Stroke(1.6f * u))
        drawLine(col, Offset(x0, y0), Offset(x0 - s * 0.08f, y0 - s * 0.14f), 1.6f * u, StrokeCap.Round)
        drawLine(col, Offset(x1, y0), Offset(x1 + s * 0.08f, y0 - s * 0.14f), 1.6f * u, StrokeCap.Round)
    }

    override val margenAviso: Dp = 32.dp
    override fun tintaAviso(oscuro: Boolean): Color = tonos(oscuro).sobreAcentoSuave

    /** El aviso: el acento suave, redondeado, con una «i» en un círculo a la izquierda. */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        drawRoundRect(c.acentoSuave, cornerRadius = CornerRadius(12 * u))
        val m = Offset(16 * u, minOf(size.height / 2, 20 * u))
        drawCircle(c.acento, 8 * u, m, style = Stroke(1.5f * u))
        drawCircle(c.acento, 1.1f * u, Offset(m.x, m.y - 3.4f * u))
        drawLine(c.acento, Offset(m.x, m.y - 0.6f * u), Offset(m.x, m.y + 4 * u), 1.6f * u, StrokeCap.Round)
    }

    override val margenCifra: Dp = 12.dp
    override fun tintaCifra(oscuro: Boolean): Color = tonos(oscuro).texto
    override fun rotuloCifra(oscuro: Boolean): Color = tonos(oscuro).tenue

    /** La cifra: una tarjeta lisa con su borde. */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        drawRoundRect(c.superficie, cornerRadius = CornerRadius(12 * u))
        contorno(c.bordeSuave, 12 * u, u)
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 14.dp
    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = tonos(oscuro).texto
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = tonos(oscuro).tenue
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = tonos(oscuro).acento

    /** La tarjeta: la superficie de 12 dp con borde de 1 dp; la destacada, con el borde del acento; al tocarla, un velo tenue. */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        val radio = 12 * u
        drawRoundRect(c.superficie, cornerRadius = CornerRadius(radio))
        if (e.presionado) drawRoundRect(c.acento.copy(alpha = 0.08f), cornerRadius = CornerRadius(radio))
        contorno(if (e.destacado) c.acento else c.bordeSuave, radio, if (e.destacado) 1.5f * u else u)
    }

    // ---------------------------------------------------------------- Gastos

    override val altoBalanza: Dp = 52.dp

    /** La balanza: un botón segmentado de dos mitades; la elegida, teñida con su color y con el texto en negrita. */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 20 * u) return
        val r = Rect(u, 4 * u, w - u, h - 4 * u)
        val radio = r.height / 2
        drawRoundRect(c.superficie, r.topLeft, r.size, CornerRadius(radio))
        for (lado in 0..1) {
            val elegida = (lado == 1) == e.ingreso
            val color = if (lado == 0) e.colorGasto else e.colorIngreso
            val mitad = if (lado == 0) Rect(r.left, r.top, w / 2, r.bottom) else Rect(w / 2, r.top, r.right, r.bottom)
            if (elegida) {
                // Redonda solo del lado de afuera (esquinas: arriba a la izquierda, arriba a la derecha, abajo a la derecha, abajo a la izquierda).
                val izq = CornerRadius(if (lado == 0) radio else 0f); val der = CornerRadius(if (lado == 1) radio else 0f)
                val p = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(mitad.left, mitad.top, mitad.right, mitad.bottom, izq, der, der, izq)) }
                drawPath(p, color.copy(alpha = 0.16f))
            }
            val texto = if (lado == 0) e.textoGasto else e.textoIngreso
            val tinta = if (elegida) mezcla(color, c.texto, 0.35f) else c.tenue
            textoEn(medidor, texto, Rect(mitad.left + 12 * u, mitad.top + 2 * u, mitad.right - 12 * u, mitad.bottom - 2 * u), LETRA, tinta, 16f,
                peso = if (elegida) FontWeight.SemiBold else FontWeight.Normal)
        }
        drawRoundRect(c.borde, r.topLeft, r.size, CornerRadius(radio), style = Stroke(u))
        drawLine(c.borde, Offset(w / 2, r.top), Offset(w / 2, r.bottom), u)
    }

    override val altoMes: Dp = 52.dp

    /** El encabezado del mes: el nombre al centro y una flecha a cada lado. */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        val w = size.width; val h = size.height
        if (w < 120 * u) return
        textoEn(medidor, e.titulo, Rect(52 * u, 0f, w - 52 * u, h), LETRA, c.texto, 20f, peso = FontWeight.Medium)
        chevron(Offset(24 * u, h / 2), 18 * u, c.tenue, izquierda = true)
        chevron(Offset(w - 24 * u, h / 2), 18 * u, c.tenue)
    }

    /** Cada movimiento: solo una línea fina abajo. */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val c = tonos(oscuro)
        drawLine(c.bordeSuave.copy(alpha = 0.7f), Offset(0f, size.height - density / 2), Offset(size.width, size.height - density / 2), density)
    }

    override val altoRotuloDia: Dp = 28.dp

    /** El rótulo de un día: el texto chico, a la izquierda, en el tono tenue. */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        textoIzq(medidor, texto, Rect(4 * density, 0f, size.width, size.height), tonos(oscuro).tenue, 12f, FontWeight.Medium)
    }

    // ---------------------------------------------------------------- adornos de la hoja rápida, las relaciones y la cinta

    /** La hoja rápida: solo el asa de arrastre al centro del borde de arriba, como en las hojas de Material. */
    override fun DrawScope.adornoQuick(e: EstadoAdorno, oscuro: Boolean) {
        val u = density
        if (size.width < 60 * u) return
        drawRoundRect(tonos(oscuro).borde.copy(alpha = 0.6f), Offset(size.width / 2 - 16 * u, 8 * u), Size(32 * u, 4 * u), CornerRadius(2 * u))
    }

    /** Los recuadros de Relaciones: sin adorno. */
    override fun DrawScope.adornoRelacion(e: EstadoAdorno, oscuro: Boolean) {}

    /** El marco de un icono de la cinta: un círculo fino. */
    override fun DrawScope.marcoPista(e: EstadoAdorno, oscuro: Boolean) {
        drawCircle(e.primario, size.minDimension * 0.47f, center, style = Stroke(maxOf(1f, size.minDimension * 0.06f)))
    }
}
