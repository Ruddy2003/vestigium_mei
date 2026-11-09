package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
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
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.TonosBasicos
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

// =====================================================================
//  Básico: la biblioteca sin diseño
// =====================================================================

/**
 * Básico: la biblioteca sin diseño, a propósito. El estante es una superficie lisa con sus tablas; cada entrada, un lomo
 * redondeado con el día y el mes (teñido apenas con el color del ánimo) y una barrita que crece con lo escrito; la tapa,
 * una tarjeta; la página, papel blanco; las pestañas, una fila plana con la elegida en una pastilla; los sobres, un
 * rectángulo con su solapa. Todo con los colores de [PaletaBasica], bordes de 1 dp y esquinas de 4 a 12 dp: sin texturas,
 * sin ornamentos y sin degradados, para que dibujar sea barato.
 */
object BibliotecaBasico : DisenoBiblioteca {
    private val LETRA: FontFamily = FontFamily.Default

    private fun tonos(oscuro: Boolean): TonosBasicos = PaletaBasica.de(oscuro)

    /** El texto que mejor se lee sobre [fondo]: el oscuro o el claro de la paleta. */
    private fun tintaSobre(fondo: Color): Color = if (fondo.luminance() > 0.4f) PaletaBasica.claro.texto else PaletaBasica.oscuro.texto

    /** Una caja redondeada con su relleno y, si se pide, su borde fino por dentro. */
    private fun DrawScope.caja(r: Rect, relleno: Color, borde: Color?, radio: Float, grosor: Float = density) {
        if (r.width <= 0f || r.height <= 0f) return
        drawRoundRect(relleno, r.topLeft, r.size, CornerRadius(radio))
        if (borde != null) drawRoundRect(borde, Offset(r.left + grosor / 2, r.top + grosor / 2), Size(r.width - grosor, r.height - grosor),
            CornerRadius(radio), style = Stroke(grosor))
    }

    /** Un renglón de texto alineado a la izquierda en [r], centrado en alto; si no cabe, se corta con «…». */
    private fun DrawScope.textoIzq(medidor: TextMeasurer, texto: String, r: Rect, color: Color, tam: Float, peso: FontWeight = FontWeight.Normal) {
        if (texto.isBlank() || r.width <= 1f || r.height <= 1f) return
        val capa = medidor.measure(texto, TextStyle(fontFamily = LETRA, fontSize = tam.sp, fontWeight = peso, color = color), maxLines = 1,
            softWrap = false, overflow = TextOverflow.Ellipsis, constraints = Constraints(maxWidth = r.width.toInt().coerceAtLeast(1)))
        drawText(capa, topLeft = Offset(r.left, r.center.y - capa.size.height / 2f))
    }

    /** «1 palabra», «N palabras». */
    private fun palabras(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** Un título partido por palabras en a lo más [n] renglones de unas [largo] letras (el último se lleva lo que sobre). */
    private fun renglones(texto: String, n: Int, largo: Int): List<String> {
        val palabras = texto.trim().ifEmpty { "—" }.split(Regex("\\s+"))
        val out = mutableListOf<String>()
        var actual = StringBuilder()
        for (p in palabras) {
            if (actual.isNotEmpty() && out.size < n - 1 && actual.length + 1 + p.length > largo) { out.add(actual.toString()); actual = StringBuilder() }
            if (actual.isNotEmpty()) actual.append(' ')
            actual.append(p)
        }
        out.add(actual.toString())
        return out
    }

    // ---------------------------------------------------------------- el mueble

    override val corona = 0.04f
    override val base = 0.03f
    override val lado = 0.03f
    override val altoRepisa: Dp = 96.dp
    override val tabla: Dp = 18.dp
    override val separa: Dp = 4.dp
    override val letra: FontFamily = LETRA

    /** El estante: una superficie lisa con su borde; bajo cada repisa, una tabla un poco más oscura con el mes. */
    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val c = tonos(oscuro)
        caja(Rect(0f, 0f, size.width, size.height), c.contenedor, c.bordeSuave, 12 * u)
        d.tablas.forEach { t ->
            drawRect(c.variante, t.topLeft, t.size)
            drawLine(c.bordeSuave, Offset(t.left, t.top), Offset(t.right, t.top), u)
        }
        d.placas.forEach { pl -> textoEn(medidor, pl.texto, pl.r, LETRA, c.tenue, 11f, peso = FontWeight.Medium) }
    }

    // ---------------------------------------------------------------- cada entrada

    override fun ancho(palabras: Int): Dp = (24 + (palabras / 40).coerceAtMost(20)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.8f + 0.12f * azar(semilla, 1)

    /** Un lomo redondeado: el día arriba, el mes a lo largo y, abajo, una barrita que crece con lo escrito. */
    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        val w = size.width; val h = size.height
        val relleno = v.tono?.let { mezcla(c.superficie, it, 0.35f) } ?: c.superficie
        caja(Rect(0f, 0f, w, h), relleno, c.borde.copy(alpha = 0.55f), 4 * u)
        val tinta = tintaSobre(relleno)
        textoEn(medidor, v.dia, Rect(2 * u, 4 * u, w - 2 * u, h * 0.22f), LETRA, tinta, 14f, peso = FontWeight.Medium)
        textoEn(medidor, v.mes, Rect(w * 0.15f, h * 0.26f, w * 0.85f, h * 0.84f), LETRA, tinta.copy(alpha = 0.75f), 10f, vertical = true, minimo = 5f)
        val largo = (w - 8 * u) * v.lleno.coerceAtLeast(0.12f)
        drawRoundRect(c.acento, Offset(4 * u, h - 7 * u), Size(largo, 3 * u), CornerRadius(1.5f * u))
    }

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 168.dp
    override val cajaTapa = Rect(0.06f, 0.34f, 0.94f, 0.8f)
    override val letraTapa: FontFamily = LETRA
    override val tamTapa = 14f
    override val lineasTapa = 3
    override val centradoTapa = false

    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = tonos(oscuro).texto
    override fun palabrasTapa(n: Int): String = palabras(n)

    /** La tapa: una tarjeta con la fecha arriba y una línea debajo; al abrirse se tiñe un poco con el acento. */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        caja(r, c.superficie, c.bordeSuave, 12 * u)
        if (a > 0f) drawRoundRect(c.acento.copy(alpha = 0.12f * a), r.topLeft, r.size, CornerRadius(12 * u))
        textoIzq(medidor, e.fecha, Rect(16 * u, 8 * u, w - 16 * u, h * 0.26f), c.texto, 16f, FontWeight.Medium)
        drawLine(c.bordeSuave, Offset(16 * u, h * 0.29f), Offset(w - 16 * u, h * 0.29f), u)
        if (e.palabras > 0) textoEn(medidor, palabrasTapa(e.palabras), Rect(w * 0.55f, h * 0.83f, w - 16 * u, h - 8 * u), LETRA, c.tenue, 11f)
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 72.dp

    /** El encabezado: el rótulo chico en el acento, la fecha y el detalle, y una línea fina debajo. */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        textoIzq(medidor, e.rotulo, Rect(0f, 0f, w, h * 0.26f), p.acento, 11f, FontWeight.Medium)
        textoIzq(medidor, e.fecha, Rect(0f, h * 0.26f, w, h * 0.64f), p.tinta, 18f, FontWeight.Medium)
        textoIzq(medidor, e.detalle, Rect(0f, h * 0.64f, w, h * 0.9f), p.tenue, 11f)
        drawLine(p.tenue.copy(alpha = 0.35f), Offset(0f, h - u), Offset(w, h - u), u)
    }

    /** Nada sobre el papel; en modo oscuro, un velo muy leve sobre el papel claro (el texto sigue con su tinta). */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        if (oscuro && !p.oscura) drawRect(Color.Black.copy(alpha = 0.06f))
    }

    /** La barra de arriba: el papel apenas más oscuro y una línea fina abajo. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        drawRect(mezcla(p.color, p.tinta, 0.04f))
        drawLine(p.tenue.copy(alpha = 0.35f), Offset(0f, size.height - u), Offset(size.width, size.height - u), u)
    }

    /** El botón de guardar: una pastilla llena del acento. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        drawRoundRect(p.acento, cornerRadius = CornerRadius(size.height / 2))
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = if (p.acento.luminance() > 0.4f) Color(0xFF0D2240) else Color.White

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 150.dp

    /** La libreta: una tarjeta con su lomo de color a la izquierda, el nombre y la cuenta; detrás, más hojas cuantas más tiene. */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        val r = Rect(w * 0.1f, h * 0.1f, w * 0.86f, h * 0.86f)
        val capas = (e.hojas / 6).coerceIn(0, 3)
        for (k in capas downTo 1) caja(r.translate(k * 3 * u, k * 3 * u), c.contenedor, c.bordeSuave, 10 * u)
        caja(r, c.superficie, c.bordeSuave, 10 * u)
        drawRoundRect(c.acento, Offset(r.left + 8 * u, r.top + 12 * u), Size(5 * u, r.height - 24 * u), CornerRadius(2.5f * u))
        textoIzq(medidor, e.nombre, Rect(r.left + 24 * u, r.top + r.height * 0.2f, r.right - 12 * u, r.top + r.height * 0.5f), c.texto, 16f, FontWeight.Medium)
        textoIzq(medidor, e.cuenta, Rect(r.left + 24 * u, r.top + r.height * 0.52f, r.right - 12 * u, r.top + r.height * 0.78f), c.tenue, 12f)
        if (a > 0f) drawRoundRect(c.acento.copy(alpha = 0.12f * a), r.topLeft, r.size, CornerRadius(10 * u))
    }

    /** La pestaña de un día: una caja con el número y la letra de la semana, teñida con el ánimo. */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        val w = size.width; val h = size.height
        val r = Rect(2 * u, 2 * u, w - 2 * u, h - 2 * u)
        val relleno = e.tono?.let { mezcla(c.superficie, it, 0.3f) } ?: c.superficie
        caja(r, relleno, c.bordeSuave, 8 * u)
        val tinta = tintaSobre(relleno)
        textoEn(medidor, e.dia, Rect(r.left, r.top + 4 * u, r.right, r.top + r.height * 0.6f), LETRA, tinta, 16f, peso = FontWeight.Medium)
        textoEn(medidor, e.semana, Rect(r.left, r.top + r.height * 0.6f, r.right, r.bottom - 4 * u), LETRA, tinta.copy(alpha = 0.75f), 10f)
    }

    /** El rótulo de un mes: el nombre, a la izquierda, en el tono tenue. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        textoIzq(medidor, texto, Rect(8 * u, 0f, size.width - 8 * u, size.height), tonos(oscuro).tenue, 13f, FontWeight.Medium)
    }

    // ---------------------------------------------------------------- las cartas

    /**
     * El sobre: un rectángulo con la solapa en triángulo y un sello redondo en su punta. Al abrir, la solapa se voltea hacia
     * atrás y la hoja sube con sus renglones; el sello se apaga al romperse. Las selladas llevan una cinta de lado a lado.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        val q = e.cuerpo
        val W = q.width; val H = q.height
        if (W <= 0f || H <= 0f) return
        val ab = e.abrir.coerceIn(0f, 1f)
        val papel = if (oscuro) c.variante else c.superficie
        val linea = c.borde.copy(alpha = 0.55f)
        // Atrás.
        caja(q, mezcla(papel, c.texto, 0.05f), linea, 8 * u)
        // La solapa: baja hasta la mitad cerrada y, al abrirse, sube por detrás.
        val k = 1f - 2f * ab
        val solapa = Path().apply {
            moveTo(q.left + 2 * u, q.top + 2 * u); lineTo(q.center.x, q.top + H * 0.5f * k); lineTo(q.right - 2 * u, q.top + 2 * u); close()
        }
        if (k < 0f) { drawPath(solapa, mezcla(papel, c.texto, 0.02f)); drawPath(solapa, linea, style = Stroke(u)) }
        // La hoja, que sube al abrir.
        if (ab > 0.3f || e.subir > 0f) {
            val arriba = q.top + H * 0.07f - H * 0.72f * e.subir.coerceIn(0f, 1f)
            val hoja = Rect(q.left + W * 0.12f, arriba, q.right - W * 0.12f, arriba + H * 0.84f)
            caja(hoja, e.hoja, c.bordeSuave, 4 * u)
            val paso = maxOf(H * 0.075f, 6 * u)
            var y = hoja.top + H * 0.12f; var i = 0
            while (y < hoja.bottom - 4 * u && i < 10) {
                drawLine(e.renglones, Offset(hoja.left + hoja.width * 0.08f, y), Offset(hoja.right - hoja.width * (if (i % 4 == 3) 0.3f else 0.08f), y), u)
                y += paso; i++
            }
        }
        // El frente tapa la hoja de la boca para abajo.
        caja(Rect(q.left, q.top + H * 0.36f, q.right, q.bottom), papel, linea, 8 * u)
        if (k >= 0f) { drawPath(solapa, mezcla(papel, c.texto, 0.06f)); drawPath(solapa, linea, style = Stroke(u)) }
        // La cinta de las selladas, que se va atando de izquierda a derecha.
        val cy = q.top + H * 0.5f
        if (e.cordon > 0f) drawLine(c.acento.copy(alpha = 0.8f), Offset(q.left, cy), Offset(q.left + W * e.cordon.coerceIn(0f, 1f), cy), 2.5f * u)
        // El sello, en la punta de la solapa cerrada.
        if (k >= 0f) {
            val rS = minOf(H * 0.12f, W * 0.08f) * e.escalaSello
            val cS = Offset(q.center.x, cy)
            val alfa = (e.alfaSello * (1f - e.romper.coerceIn(0f, 1f))).coerceIn(0f, 1f)
            if (e.brillo > 0f) drawCircle(c.acento.copy(alpha = 0.25f * e.brillo.coerceIn(0f, 1f)), rS * 1.8f, cS)
            if (alfa > 0.01f && rS > 0f) {
                drawCircle(c.acento.copy(alpha = alfa), rS, cS)
                drawCircle(c.sobreAcento.copy(alpha = 0.6f * alfa), rS * 0.6f, cS, style = Stroke(u))
            }
        }
        // A quién va y las fechas.
        textoEn(medidor, e.linea1, Rect(q.left + W * 0.08f, q.top + H * 0.62f, q.right - W * 0.08f, q.top + H * 0.78f), LETRA, c.texto, 12f,
            peso = FontWeight.Medium, minimo = 7f)
        textoEn(medidor, e.linea2, Rect(q.left + W * 0.1f, q.top + H * 0.78f, q.right - W * 0.1f, q.top + H * 0.9f), LETRA, c.tenue, 9.5f, minimo = 7f)
    }

    override val cajaPrevia: Rect = Rect(0.17f, 0.175f, 0.83f, 0.345f)

    override val altoBuzon: Dp = 150.dp

    /** El buzón: una superficie con tres columnas (listas, selladas, abiertas), cada una con un sobrecito y su número. */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        val w = size.width; val h = size.height
        val r = Rect(w * 0.06f, h * 0.08f, w * 0.94f, h * 0.92f)
        caja(r, c.contenedor, c.bordeSuave, 12 * u)
        val datos = listOf(e.listas to "listas", e.selladas to "selladas", e.abiertas to "abiertas")
        val col = r.width / 3f
        datos.forEachIndexed { i, (n, rotulo) ->
            val cx = r.left + col * (i + 0.5f)
            val activo = n > 0
            val destaca = i == 0 && activo
            val s = Rect(cx - col * 0.28f, r.top + r.height * 0.16f, cx + col * 0.28f, r.top + r.height * 0.16f + col * 0.36f)
            caja(s, if (destaca) c.acentoSuave else c.superficie, if (activo) c.borde else c.bordeSuave, 4 * u)
            drawPath(Path().apply { moveTo(s.left + 2 * u, s.top + 2 * u); lineTo(s.center.x, s.center.y); lineTo(s.right - 2 * u, s.top + 2 * u) },
                c.borde.copy(alpha = if (activo) 0.8f else 0.4f), style = Stroke(u))
            val y0 = s.bottom + 4 * u
            val alto = (r.bottom - 6 * u - y0) / 2f
            if (alto > 1f) {
                textoEn(medidor, "$n", Rect(cx - col * 0.45f, y0, cx + col * 0.45f, y0 + alto), LETRA, if (destaca) c.acento else c.texto, 18f, peso = FontWeight.Medium)
                textoEn(medidor, rotulo, Rect(cx - col * 0.45f, y0 + alto, cx + col * 0.45f, y0 + 2 * alto), LETRA, c.tenue, 11f)
            }
        }
    }

    // ---------------------------------------------------------------- pestañas, buscador, obras y recuerdos

    override val altoPestanas: Dp = 56.dp

    /** Las cinco pestañas: una fila plana con su línea de abajo; la elegida, en una pastilla con el acento suave y su raya. */
    override fun DrawScope.pestanas(e: EstadoPestanas, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        val w = size.width; val h = size.height
        val q = w / 5f
        drawLine(c.bordeSuave, Offset(0f, h - u / 2), Offset(w, h - u / 2), u)
        for (k in 0 until 5) {
            val sel = k == e.actual
            val r = Rect(q * k + 3 * u, 4 * u, q * (k + 1) - 3 * u, h - 6 * u)
            if (sel) {
                drawRoundRect(c.acentoSuave, r.topLeft, r.size, CornerRadius(12 * u))
                drawLine(c.acento, Offset(r.left + 8 * u, h - 1.5f * u), Offset(r.right - 8 * u, h - 1.5f * u), 3 * u, StrokeCap.Round)
            }
            val tinta = if (sel) c.sobreAcentoSuave else c.tenue
            textoEn(medidor, e.nombres.getOrElse(k) { "" }, Rect(r.left + 2 * u, r.top + 3 * u, r.right - 2 * u, r.top + r.height * 0.6f), LETRA, tinta, 12f,
                peso = if (sel) FontWeight.SemiBold else FontWeight.Normal, minimo = 7f)
            textoEn(medidor, "${e.cuentas.getOrElse(k) { 0 }}", Rect(r.left + 2 * u, r.top + r.height * 0.6f, r.right - 2 * u, r.bottom - 2 * u), LETRA,
                tinta.copy(alpha = 0.8f), 10f, minimo = 6f)
        }
    }

    override val altoCampo: Dp = 48.dp
    override val cajaCampo: Rect = Rect(0.13f, 0.18f, 0.94f, 0.82f)
    override val letraCampo: FontFamily = LETRA
    override fun tintaCampo(oscuro: Boolean): Color = tonos(oscuro).texto

    /** El buscador: una pastilla de fondo suave con la lupa; con el foco, el borde del acento. */
    override fun DrawScope.campo(e: EstadoCampo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        val w = size.width; val h = size.height
        val r = Rect(u, u, w - u, h - u)
        drawRoundRect(c.contenedorAlto, r.topLeft, r.size, CornerRadius(r.height / 2))
        if (e.enfocado) drawRoundRect(c.acento, r.topLeft, r.size, CornerRadius(r.height / 2), style = Stroke(2 * u))
        val cl = Offset(w * 0.065f, h * 0.46f); val rl = h * 0.16f
        drawCircle(c.tenue, rl, cl, style = Stroke(1.6f * u))
        drawLine(c.tenue, cl + Offset(rl * 0.7f, rl * 0.7f), cl + Offset(rl * 1.5f, rl * 1.5f), 1.6f * u, StrokeCap.Round)
        if (e.vacio) textoIzq(medidor, e.pista, Rect(w * cajaCampo.left, h * cajaCampo.top, w * cajaCampo.right, h * cajaCampo.bottom), c.tenue, 14f)
    }

    /** El fondo de las obras: una superficie lisa con una línea donde se apoyan las portadas. */
    override fun DrawScope.repisaObras(medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        drawRoundRect(c.contenedor, cornerRadius = CornerRadius(12 * u))
        drawLine(c.bordeSuave, Offset(12 * u, size.height * 0.86f), Offset(size.width - 12 * u, size.height * 0.86f), u)
    }

    /** La tapa de una obra sin imagen: un color apagado (uno por obra) con el tipo, el título, el autor y el año en claro. */
    override fun DrawScope.portadaObra(e: EstadoPortada, medidor: TextMeasurer, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val colores = listOf(Color(0xFF4A5B72), Color(0xFF55665A), Color(0xFF65586E), Color(0xFF6E5A4C), Color(0xFF4A6670))
        val fondo = colores[(e.semilla and 0x7fffffff) % colores.size]
        drawRoundRect(fondo, cornerRadius = CornerRadius(minOf(w, h) * 0.06f))
        val claro = Color(0xFFF2F4F7)
        if (e.mini) {
            textoEn(medidor, e.titulo.trim().take(1).uppercase().ifEmpty { "·" }, Rect(0f, 0f, w, h), LETRA, claro, 20f, peso = FontWeight.Medium)
            return
        }
        textoEn(medidor, e.tipo, Rect(w * 0.08f, h * 0.06f, w * 0.92f, h * 0.16f), LETRA, claro.copy(alpha = 0.75f), 9f, minimo = 5f)
        val lineas = renglones(e.titulo, 3, 14)
        val alto = h * 0.12f
        val y0 = h * 0.28f
        lineas.forEachIndexed { i, l ->
            textoEn(medidor, l, Rect(w * 0.08f, y0 + i * alto, w * 0.92f, y0 + (i + 1) * alto), LETRA, claro, 14f, peso = FontWeight.SemiBold, minimo = 6f)
        }
        textoEn(medidor, e.autor, Rect(w * 0.08f, h * 0.74f, w * 0.92f, h * 0.84f), LETRA, claro.copy(alpha = 0.85f), 10f, minimo = 5f)
        textoEn(medidor, e.anio, Rect(w * 0.08f, h * 0.86f, w * 0.92f, h * 0.95f), LETRA, claro.copy(alpha = 0.7f), 9f, minimo = 5f)
    }

    override val margenRecuerdo: Dp = 16.dp
    override val altoRotuloRecuerdo: Dp = 44.dp
    override fun tintaRecuerdo(oscuro: Boolean): Color = tonos(oscuro).texto

    /** El recuerdo: una tarjeta con «Hace un año», la fecha y el clima arriba, el punto del ánimo a la derecha y una línea. */
    override fun DrawScope.recuerdo(e: EstadoRecuerdo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val c = tonos(oscuro)
        val w = size.width
        caja(Rect(0f, 0f, w, size.height), c.superficie, c.bordeSuave, 12 * u)
        val m = margenRecuerdo.toPx(); val alto = altoRotuloRecuerdo.toPx()
        val hace = if (e.anios == 1) "Hace un año" else "Hace ${e.anios} años"
        textoIzq(medidor, hace, Rect(m, 4 * u, w - m - 20 * u, alto * 0.58f), c.acento, 14f, FontWeight.Medium)
        textoIzq(medidor, e.fecha + (if (e.clima.isNotBlank()) " · " + e.clima else ""), Rect(m, alto * 0.56f, w - m - 20 * u, alto), c.tenue, 11f)
        e.animo?.let { drawCircle(it, 6 * u, Offset(w - m - 6 * u, alto * 0.4f)) }
        drawLine(c.bordeSuave, Offset(m, alto + 2 * u), Offset(w - m, alto + 2 * u), u)
    }
}
