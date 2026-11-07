package io.github.ruddy2003.vestigium.ui.comun

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.engrane
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

// =====================================================================
//  Futuro
// =====================================================================

/**
 * Las rayas de los renglones de un campo de varios renglones, de abajo arriba: la primera ([k] = 0) al pie del último
 * renglón, a [margenY] del canto de abajo, y luego una cada 24 sp mientras quede sitio arriba (16 dp bajo [margenY], para
 * no cruzar la primera línea). Van de abajo arriba porque el campo crece con su texto: así cada raya cae bajo un renglón,
 * lleve rótulo o no.
 */
private inline fun DrawScope.renglones(margenY: Dp, raya: (k: Int, y: Float) -> Unit) {
    val paso = maxOf(24.sp.toPx(), 1f)
    val tope = margenY.toPx() + 16.dp.toPx()
    var y = size.height - margenY.toPx() + 1.dp.toPx()
    var k = 0
    while (y > tope && k < 200) { raya(k, y); y -= paso; k++ }
}

/**
 * Distopía: las piezas comunes del registro. El campo es la ventanilla de consultas del archivo, como su buscador: la placa
 * de acero con sus cuatro tornillos y el formulario metido en su ranura, con la raya punteada donde se escribe a máquina y
 * la esquina roja de «clasificado».
 */
object ComunDistopia : DisenoComun {
    private val ACERO = Color(0xFF6F7479)
    private val ACERO_OSC = Color(0xFF43474B)
    private val ACERO_LUZ = Color(0xFF9BA1A6)
    private val ROJO = Color(0xFFA8201C)
    private val NEGRO = Color(0xFF141516)
    private val PAPEL = Color(0xFFE9E4D6)
    /** El papel del formulario en oscuro: el papel carbón de la copia, hundido. */
    private val CARBON = Color(0xFF2A2C2F)
    private val PELIGRO = Color(0xFFE0A030)

    /** El color en oscuro: hundido hacia el negro del archivo. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0C0D), 0.4f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 15.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6E1D3) else NEGRO
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9A978E) else Color(0xFF66625A)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF07068) else ROJO
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF0A848) else Color(0xFF8E4C00)

    /**
     * La ventanilla de consultas: la placa de acero con su canto, sus cuatro tornillos y el piloto; el formulario metido en
     * su ranura, con la esquina roja de «clasificado» y la raya punteada de la máquina (una por renglón si son varios). Con el
     * foco, la ranura se marca en rojo, la raya se vuelve continua y el piloto se enciende; con un error, la franja de peligro
     * corre por el canto de abajo.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro).let { if (e.habilitado) it else mezcla(it, Color(0xFF7A7A7A), 0.4f) }
        // La placa de acero con su canto.
        drawRoundRect(c(ACERO), Offset.Zero, size, CornerRadius(3 * u))
        drawRect(c(ACERO_LUZ), Offset(2 * u, 0.8f * u), Size(w - 4 * u, 1.2f * u))
        drawRect(c(ACERO_OSC), Offset(2 * u, h - 1.6f * u), Size(w - 4 * u, 1.6f * u))
        // El formulario en su ranura.
        val b = 5 * u
        val f = Rect(b, b, w - b, h - b)
        drawRect(c(NEGRO).copy(alpha = 0.6f), f.inflate(1 * u).topLeft, f.inflate(1 * u).size)
        val papel = if (oscuro) CARBON else PAPEL
        drawRect(if (e.habilitado) papel else mezcla(papel, Color(0xFF8A8A8A), 0.25f), f.topLeft, f.size)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), f.topLeft, Size(f.width, 1.4f * u))
        // La esquina de «clasificado», arriba a la derecha.
        val k = minOf(9 * u, f.height * 0.3f)
        drawPath(Path().apply { moveTo(f.right - k, f.top); lineTo(f.right, f.top); lineTo(f.right, f.top + k); close() }, c(ROJO))
        // La raya de la máquina: punteada en reposo y continua con el foco.
        val raya = tintaCampo(oscuro).copy(alpha = if (e.enfocado) 0.42f else 0.26f)
        val efecto = if (e.enfocado) null else PathEffect.dashPathEffect(floatArrayOf(2 * u, 2 * u))
        val izq = f.left + 6 * u; val der = f.right - 6 * u
        if (e.multilinea) renglones(margenCampoY) { _, y -> drawLine(raya, Offset(izq, y), Offset(der, y), 0.8f * u, pathEffect = efecto) }
        else drawLine(raya, Offset(izq, f.bottom - 4 * u), Offset(der, f.bottom - 4 * u), 0.8f * u, pathEffect = efecto)
        // La ranura se marca con el foco o con el error.
        if (e.enfocado || e.error) {
            val borde = if (e.error) errorCampo(oscuro) else rotuloCampo(oscuro)
            drawRect(borde, f.topLeft, f.size, style = Stroke(1.4f * u))
        }
        // Los tornillos de las esquinas, con su ranura.
        listOf(Offset(2.6f * u, 2.6f * u), Offset(w - 2.6f * u, 2.6f * u), Offset(2.6f * u, h - 2.6f * u), Offset(w - 2.6f * u, h - 2.6f * u)).forEach { o ->
            drawCircle(c(ACERO_LUZ), 1.7f * u, o)
            drawLine(c(NEGRO), Offset(o.x - 1.1f * u, o.y - 0.6f * u), Offset(o.x + 1.1f * u, o.y + 0.6f * u), 0.6f * u)
        }
        // La franja de peligro del canto de abajo, con el error.
        if (e.error) clipRect(8 * u, h - b + 0.6f * u, w - 8 * u, h - 0.6f * u) {
            val paso = maxOf(6 * u, 1f)
            var x = 8 * u - b
            var n = 0
            while (x < w && n < 400) {
                drawPath(Path().apply { moveTo(x, h); lineTo(x + 3 * u, h); lineTo(x + 3 * u + b, h - b); lineTo(x + b, h - b); close() }, PELIGRO)
                x += paso; n++
            }
        }
        // El piloto del canto derecho: apagado, o encendido con su halo.
        val p = Offset(w - 2.5f * u, if (e.multilinea) minOf(h / 2, 22 * u) else h / 2)
        val luz = if (e.error) errorCampo(true) else Color(0xFFFF4A3A)
        val prendido = (e.enfocado || e.error) && e.habilitado
        if (prendido) drawCircle(luz.copy(alpha = 0.3f), 3.6f * u, p)
        drawCircle(if (prendido) luz else c(Color(0xFF5A1A18)), 1.6f * u, p)
        drawCircle(Color.White.copy(alpha = if (prendido) 0.7f else 0.2f), 0.5f * u, Offset(p.x - 0.5f * u, p.y - 0.5f * u))
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 8.dp

    /**
     * La perforación del formulario: una hilera de agujeritos de lado a lado (un solo trazo punteado), cortada al centro por
     * la franja roja del expediente entre sus dos marcas de registro. Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val a = if (e.tenue) 0.5f else 1f
        val cx = w / 2
        val franja = minOf(14 * u, w * 0.1f)
        val hueco = franja / 2 + 5 * u
        val tinta = (if (oscuro) Color(0xFF8E8C86) else Color(0xFF5A5750)).copy(alpha = 0.6f * a)
        val perforacion = Path().apply { moveTo(2 * u, y); lineTo(cx - hueco, y); moveTo(cx + hueco, y); lineTo(w - 2 * u, y) }
        drawPath(perforacion, tinta, style = Stroke(1.3f * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.01f, maxOf(3.5f * u, 1f)))))
        // Las marcas de registro y la franja roja.
        listOf(cx - hueco + 2.5f * u, cx + hueco - 2.5f * u).forEach { x -> drawLine(tinta, Offset(x, y - 2 * u), Offset(x, y + 2 * u), 0.8f * u) }
        drawRect((if (oscuro) Color(0xFFC0392F) else ROJO).copy(alpha = 0.85f * a), Offset(cx - franja / 2, y - 1.1f * u), Size(franja, 2.2f * u))
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFF07068) else ROJO

    /**
     * La casilla del formulario: la raya donde se llena, con sus dos topes, y la marca roja del expediente en la esquina de
     * arriba a la derecha. Al presionarlo la casilla se sella: se rellena de rojo tenue y la raya va en rojo; apagado, queda
     * solo la raya, punteada y gris, sin marca.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val izq = 4 * u; val der = w - 4 * u
        val arriba = 8 * u; val pie = h - 9 * u
        val rojo = tintaBotonTexto(oscuro)
        val tinta = if (oscuro) Color(0xFFA8A69E) else Color(0xFF3E3C37)
        if (!e.habilitado) {
            drawLine(tinta.copy(alpha = 0.28f), Offset(izq, pie), Offset(der, pie), 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 2 * u)))
            return
        }
        if (e.presionado) drawRect(rojo.copy(alpha = if (oscuro) 0.16f else 0.12f), Offset(izq, arriba), Size(der - izq, pie - arriba))
        val raya = if (e.presionado) rojo else tinta.copy(alpha = 0.5f)
        drawPath(Path().apply { moveTo(izq, pie - 3.5f * u); lineTo(izq, pie); lineTo(der, pie); lineTo(der, pie - 3.5f * u) }, raya,
            style = Stroke((if (e.presionado) 1.3f else 0.9f) * u, join = StrokeJoin.Miter))
        val k = 4 * u
        drawPath(Path().apply { moveTo(der - k, arriba); lineTo(der, arriba); lineTo(der, arriba + k); close() }, rojo.copy(alpha = if (e.presionado) 1f else 0.75f))
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El papel de las piezas del registro: el formulario en claro y el papel carbón en oscuro. */
    private fun papelFicha(oscuro: Boolean): Color = if (oscuro) Color(0xFF1C1E20) else Color(0xFFECE8DC)

    /** La tinta gris de las rayas del formulario. */
    private fun rayaFicha(oscuro: Boolean): Color = if (oscuro) Color(0xFF8E8C86) else Color(0xFF5A5750)

    override val margenFila: Dp = 12.dp

    /**
     * El renglón del libro de registro: la raya fina al pie y, a la izquierda, la pestaña de índice del archivador (más larga
     * si la fila lleva detalle). Al tocarla la entrada se marca para revisión: la pestaña se vuelve roja, el renglón se tiñe y
     * se subraya en rojo hasta la mitad.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val raya = rayaFicha(oscuro)
        val rojo = tintaBotonTexto(oscuro)
        if (e.presionado) drawRect(ROJO.copy(alpha = if (oscuro) 0.14f else 0.08f))
        drawLine(raya.copy(alpha = 0.35f), Offset(0f, h - 0.5f * u), Offset(w, h - 0.5f * u), 0.8f * u)
        if (e.presionado) drawLine(rojo, Offset(0f, h - 1 * u), Offset(w * 0.45f, h - 1 * u), 1.6f * u)
        // La pestaña de índice, con sus dos marcas de registro.
        val alto = if (e.conDetalle) minOf(26 * u, h - 12 * u) else minOf(16 * u, h - 12 * u)
        drawRect(if (e.presionado) rojo else raya.copy(alpha = 0.45f), Offset(0f, (h - alto) / 2), Size(3 * u, alto))
        listOf(-1f, 1f).forEach { s -> drawLine(raya.copy(alpha = 0.4f), Offset(4.5f * u, h / 2 + s * alto / 2), Offset(6.5f * u, h / 2 + s * alto / 2), 0.7f * u) }
    }

    /** El «▶» de la máquina de escribir, con su raya de guía delante. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        val tinta = if (oscuro) Color(0xFFA8A69E) else Color(0xFF3E3C37)
        drawRect(tinta.copy(alpha = 0.55f), Offset(c.x - 7 * u, c.y - 1 * u), Size(4 * u, 2 * u))
        drawPath(Path().apply { moveTo(c.x - 1.5f * u, c.y - 4.5f * u); lineTo(c.x + 5.5f * u, c.y); lineTo(c.x - 1.5f * u, c.y + 4.5f * u); close() }, tinta)
    }

    /**
     * El sello de «prioritario»: un cuadro con su estrella, estampado un poco torcido. Fijo, en rojo pleno con la estrella en
     * blanco de papel; sin fijar, solo el contorno punteado gris, como la casilla que nadie selló.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        val r = Rect(c.x - 8 * u, c.y - 8 * u, c.x + 8 * u, c.y + 8 * u)
        val estrellita = estrella(c, 5.6f * u, 5, 0.42f)
        if (fija) rotate(-8f, c) {
            drawRect(tintaBotonTexto(oscuro), r.topLeft, r.size)
            drawRect(papelFicha(oscuro).copy(alpha = 0.7f), r.deflate(1.6f * u).topLeft, r.deflate(1.6f * u).size, style = Stroke(0.7f * u))
            drawPath(estrellita, papelFicha(oscuro))
        } else {
            val gris = rayaFicha(oscuro).copy(alpha = 0.6f)
            drawRect(gris, r.topLeft, r.size, style = Stroke(1 * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 1.5f * u))))
            drawPath(estrellita, gris, style = Stroke(1 * u, join = StrokeJoin.Miter))
        }
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) Color(0xFFA8A69E) else Color(0xFF3E3C37)

    /**
     * El formulario en blanco: la hoja con su folio arriba (la raya del título y las cuatro casillas del número, vacías), el
     * recuadro punteado donde irían los datos y la esquina roja de «clasificado». Liso y quieto: nada que tocar.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val raya = rayaFicha(oscuro)
        drawRect(papelFicha(oscuro))
        drawRect(raya.copy(alpha = 0.5f), Offset.Zero, size, style = Stroke(0.8f * u))
        // El folio: la raya del título y las casillas del número.
        drawLine(raya.copy(alpha = 0.5f), Offset(8 * u, 8 * u), Offset(minOf(w * 0.3f, 90 * u), 8 * u), 1.2f * u)
        val casilla = 5 * u
        for (i in 0 until 4) drawRect(raya.copy(alpha = 0.45f), Offset(w - 20 * u - (4 - i) * (casilla + 1.5f * u), 4.5f * u), Size(casilla, casilla), style = Stroke(0.7f * u))
        // El recuadro de los datos, vacío.
        val r = Rect(8 * u, 14 * u, w - 8 * u, h - 8 * u)
        if (r.height > 10 * u) drawRect(raya.copy(alpha = 0.32f), r.topLeft, r.size, style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2 * u))))
        // La esquina de «clasificado».
        val k = 11 * u
        drawPath(Path().apply { moveTo(w - k, 0f); lineTo(w, 0f); lineTo(w, k); close() }, tintaBotonTexto(oscuro).copy(alpha = 0.85f))
    }

    /**
     * El cajón del archivador, sacado y vacío: el mueble de acero con el cajón de arriba cerrado (su portaetiquetas y su
     * tirador), el de abajo afuera, con el fondo a la vista sin una sola carpeta, y la tira roja cruzada sobre su etiqueta.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val borde = (if (oscuro) Color(0xFF9BA1A6) else NEGRO).copy(alpha = 0.7f)
        val g = 1 * u
        // El mueble y el cajón de arriba.
        drawRect(t(ACERO, oscuro), Offset(f(0.18f), f(0.04f)), Size(f(0.64f), f(0.66f)))
        drawRect(borde, Offset(f(0.18f), f(0.04f)), Size(f(0.64f), f(0.66f)), style = Stroke(g))
        drawRect(t(ACERO_LUZ, oscuro), Offset(f(0.24f), f(0.1f)), Size(f(0.52f), f(0.22f)))
        drawRect(borde, Offset(f(0.24f), f(0.1f)), Size(f(0.52f), f(0.22f)), style = Stroke(0.7f * g))
        drawRect(papelFicha(false), Offset(f(0.42f), f(0.13f)), Size(f(0.16f), f(0.05f)))
        drawLine(borde, Offset(f(0.42f), f(0.25f)), Offset(f(0.58f), f(0.25f)), 1.6f * g, StrokeCap.Round)
        // El hueco del cajón de abajo y el cajón afuera, con su fondo vacío.
        drawRect(NEGRO.copy(alpha = 0.75f), Offset(f(0.24f), f(0.38f)), Size(f(0.52f), f(0.28f)))
        val fondo = Path().apply { moveTo(f(0.24f), f(0.42f)); lineTo(f(0.76f), f(0.42f)); lineTo(f(0.92f), f(0.66f)); lineTo(f(0.08f), f(0.66f)); close() }
        drawPath(fondo, t(ACERO_OSC, oscuro))
        drawPath(fondo, borde, style = Stroke(0.7f * g))
        drawLine(borde.copy(alpha = 0.4f), Offset(f(0.5f), f(0.43f)), Offset(f(0.5f), f(0.65f)), 0.6f * g)
        // El frente del cajón: el portaetiquetas sin nombre, la tira roja y el tirador.
        drawRect(t(ACERO_LUZ, oscuro), Offset(f(0.08f), f(0.66f)), Size(f(0.84f), f(0.26f)))
        drawRect(borde, Offset(f(0.08f), f(0.66f)), Size(f(0.84f), f(0.26f)), style = Stroke(g))
        drawRect(papelFicha(false), Offset(f(0.38f), f(0.7f)), Size(f(0.24f), f(0.08f)))
        drawRect(borde, Offset(f(0.38f), f(0.7f)), Size(f(0.24f), f(0.08f)), style = Stroke(0.6f * g))
        rotate(-14f, Offset(f(0.5f), f(0.74f))) { drawRect(tintaBotonTexto(oscuro), Offset(f(0.3f), f(0.725f)), Size(f(0.4f), f(0.03f))) }
        drawLine(borde, Offset(f(0.4f), f(0.86f)), Offset(f(0.6f), f(0.86f)), 2 * g, StrokeCap.Round)
    }

    override val margenAviso: Dp = 32.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6E1D3) else NEGRO

    /**
     * La circular oficial: la hoja con la banda de acero a la izquierda, filo rojo, y arriba el sello cuadrado de «atención»
     * (el signo de admiración o, según el aviso, el triángulo de advertencia), con la hilera de perforaciones debajo.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(papelFicha(oscuro))
        drawLine(rayaFicha(oscuro).copy(alpha = 0.35f), Offset(0f, h - 0.5f * u), Offset(w, h - 0.5f * u), 0.8f * u)
        // La banda de acero con su filo rojo.
        val banda = 22 * u
        drawRect(t(ACERO_OSC, oscuro), Offset.Zero, Size(banda, h))
        drawRect(tintaBotonTexto(oscuro), Offset(banda, 0f), Size(1.6f * u, h))
        // El sello de «atención».
        val sello = Rect(5 * u, 7 * u, 17 * u, 19 * u)
        drawRect(tintaBotonTexto(oscuro), sello.topLeft, sello.size)
        val blanco = papelFicha(false)
        if (e.semilla % 2 == 0) {
            drawRect(blanco, Offset(sello.center.x - 0.9f * u, sello.top + 2.2f * u), Size(1.8f * u, 4.6f * u))
            drawRect(blanco, Offset(sello.center.x - 0.9f * u, sello.bottom - 3.6f * u), Size(1.8f * u, 1.8f * u))
        } else drawPath(Path().apply { moveTo(sello.center.x, sello.top + 2.2f * u); lineTo(sello.right - 2.2f * u, sello.bottom - 2.4f * u); lineTo(sello.left + 2.2f * u, sello.bottom - 2.4f * u); close() },
            blanco, style = Stroke(1.2f * u, join = StrokeJoin.Miter))
        // La perforación bajo el sello.
        if (h > 30 * u) drawLine(Color.Black.copy(alpha = 0.45f), Offset(11 * u, 25 * u), Offset(11 * u, h - 5 * u), 1.4f * u, StrokeCap.Round,
            PathEffect.dashPathEffect(floatArrayOf(0.01f, maxOf(4 * u, 1f))))
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6E1D3) else NEGRO
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFF07068) else ROJO

    /**
     * La ficha numerada: el papel del formulario en su marco de acero, con dos remaches, la sombra de la ranura arriba y la
     * esquina roja de «clasificado».
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val b = 2.5f * u
        drawRect(t(ACERO, oscuro))
        drawRect(papelFicha(oscuro), Offset(b, b), Size(w - 2 * b, h - 2 * b))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(b, b), Size(w - 2 * b, 1.2f * u))
        val k = 8 * u
        drawPath(Path().apply { moveTo(w - b - k, b); lineTo(w - b, b); lineTo(w - b, b + k); close() }, tintaBotonTexto(oscuro).copy(alpha = 0.9f))
        listOf(Offset(b / 2, h / 2), Offset(w - b / 2, h / 2)).forEach { drawCircle(t(ACERO_LUZ, oscuro), 0.9f * u, it) }
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    /**
     * El papel de cada tarjeta: el formulario (el carbón, en oscuro) para la cifra y la orden; la placa gris del toque de queda
     * para la noche; la carpeta manila del expediente para Vida.
     */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFF16181A) else Color(0xFFDAD8D0)
        TipoTarjeta.VIDA -> if (oscuro) Color(0xFF221F19) else Color(0xFFE9DDBE)
        else -> papelFicha(oscuro)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFE6E1D3) else NEGRO
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFA8A69E) else Color(0xFF4A4740)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFF07068) else ROJO

    /**
     * Las tarjetas del registro. La cifra de Hoy es la **ficha perforada**: la esquina de arriba cortada, la hilera de
     * perforaciones en el canto de arriba y el canto de acero abajo; destacada, la banda roja de «revisar» cruza la esquina.
     * «Te falta hoy» es la **orden de requisa urgente**: el marco rojo grueso, la franja de peligro a la izquierda y la raya
     * del télex arriba; completa, la franja se vuelve acero y cae el sello redondo de «visado». La noche es la **ventana alta
     * del archivo**: una tira de cielo con sus barrotes, la luna y dos estrellas; sellada, baja la persiana de acero y la
     * cruza el precinto rojo. Vida es el **expediente**: la carpeta manila con su pestaña, el canto de acero y los tres
     * cuadros del código de color. Al tocarlas el papel se hunde.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val raya = rayaFicha(oscuro)
        val rojo = acentoTarjeta(e.tipo, oscuro)
        val fondo = fondoTarjeta(e.tipo, oscuro).let { if (e.presionado) mezcla(it, raya, 0.12f) else it }
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val k = 9 * u
                val ficha = Path().apply { moveTo(k, 0f); lineTo(w, 0f); lineTo(w, h); lineTo(0f, h); lineTo(0f, k); close() }
                drawPath(ficha, fondo)
                drawPath(ficha, raya.copy(alpha = 0.55f), style = Stroke(0.8f * u))
                // Las perforaciones del canto de arriba: unas picadas y otras apenas marcadas.
                val paso = maxOf(5 * u, 1f)
                var x = k + 3 * u
                var n = 0
                while (x < w - 26 * u && n < 200) {
                    val picada = azar(e.semilla + 31, n) > 0.55f
                    drawRect(raya.copy(alpha = if (picada) 0.6f else 0.2f), Offset(x, 3 * u), Size(2 * u, 3.4f * u))
                    x += paso; n++
                }
                // El canto de acero del pie.
                drawRect(t(ACERO, oscuro), Offset(0f, h - 2.5f * u), Size(w, 2.5f * u))
                drawLine(t(ACERO_LUZ, oscuro), Offset(0f, h - 2.5f * u), Offset(w, h - 2.5f * u), 0.6f * u)
                // Destacada: la banda roja de «revisar» cruzando la esquina de arriba a la derecha.
                if (e.destacado) {
                    drawPath(Path().apply { moveTo(w - 24 * u, 0f); lineTo(w - 15 * u, 0f); lineTo(w, 15 * u); lineTo(w, 24 * u); close() }, rojo)
                    drawPath(ficha, rojo.copy(alpha = 0.7f), style = Stroke(1.4f * u))
                }
            }
            TipoTarjeta.PENDIENTE -> {
                drawRect(fondo)
                val franja = 8 * u
                if (!e.destacado) {
                    // La franja de peligro, negra y ámbar, a la izquierda.
                    clipRect(0f, 0f, franja, h) {
                        drawRect(NEGRO)
                        val paso = maxOf(8 * u, 1f)
                        var y = -franja
                        var n = 0
                        while (y < h && n < 400) {
                            drawPath(Path().apply { moveTo(0f, y + franja); lineTo(franja, y); lineTo(franja, y + 4 * u); lineTo(0f, y + franja + 4 * u); close() }, t(PELIGRO, oscuro))
                            y += paso; n++
                        }
                    }
                } else drawRect(t(ACERO, oscuro), Offset.Zero, Size(franja, h))
                // La raya del télex arriba y el marco rojo grueso.
                drawLine(rojo.copy(alpha = 0.8f), Offset(franja + 4 * u, 4 * u), Offset(w - 4 * u, 4 * u), 1.2f * u,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10 * u, 2 * u, 2 * u, 2 * u)))
                drawRect(if (e.destacado) raya.copy(alpha = 0.7f) else rojo, Offset(1.25f * u, 1.25f * u), Size(w - 2.5f * u, h - 2.5f * u), style = Stroke(2.5f * u))
                // Completa: el sello redondo de «visado», estampado un poco torcido a la derecha.
                if (e.destacado) {
                    val r = minOf(h * 0.36f, 22 * u)
                    val c = Offset(w - r - 10 * u, h / 2)
                    rotate(-12f, c) {
                        drawCircle(rojo.copy(alpha = 0.8f), r, c, style = Stroke(1.6f * u))
                        drawCircle(rojo.copy(alpha = 0.6f), r - 3 * u, c, style = Stroke(0.8f * u))
                        drawLine(rojo.copy(alpha = 0.7f), Offset(c.x - r + 2 * u, c.y), Offset(c.x + r - 2 * u, c.y), 2.6f * u)
                        drawPath(estrella(c, r * 0.28f, 5, 0.42f).also { it.translate(Offset(0f, -r * 0.48f)) }, rojo.copy(alpha = 0.7f))
                    }
                }
            }
            TipoTarjeta.NOCHE -> {
                drawRect(fondo)
                drawRect(raya.copy(alpha = 0.5f), Offset.Zero, size, style = Stroke(0.8f * u))
                // La ventana alta: una tira de cielo en el canto de arriba, del lado derecho.
                val v = Rect(maxOf(w * 0.5f, 8 * u), 2.5f * u, w - 8 * u, 10.5f * u)
                val cielo = if (oscuro) Color(0xFF0B1220) else Color(0xFF2C3446)
                drawRect(cielo, v.topLeft, v.size)
                clipRect(v.left, v.top, v.right, v.bottom) {
                    drawCircle(Color(0xFFE9E4D6), 3 * u, Offset(v.left + v.width * 0.68f, v.center.y))
                    listOf(0.15f, 0.4f).forEach { f -> drawCircle(Color(0xFFE9E4D6).copy(alpha = 0.8f), 0.7f * u, Offset(v.left + v.width * f, v.top + 2.2f * u)) }
                    if (e.destacado) {
                        // Sellada: la persiana de acero, bajada, con sus lamas.
                        drawRect(t(ACERO, oscuro), v.topLeft, v.size)
                        listOf(0.33f, 0.66f).forEach { f -> drawLine(t(ACERO_OSC, oscuro), Offset(v.left, v.top + v.height * f), Offset(v.right, v.top + v.height * f), 0.6f * u) }
                    }
                }
                // Los barrotes.
                val paso = maxOf(11 * u, 1f)
                var x = v.left + paso / 2
                var n = 0
                while (x < v.right && n < 100) { drawLine(t(ACERO_LUZ, oscuro), Offset(x, v.top), Offset(x, v.bottom), 1 * u); x += paso; n++ }
                drawRect(t(ACERO_OSC, oscuro), v.topLeft, v.size, style = Stroke(1 * u))
                // Sellada: el precinto rojo cruzando la esquina de abajo a la izquierda.
                if (e.destacado) {
                    drawPath(Path().apply { moveTo(0f, h - 22 * u); lineTo(0f, h - 15 * u); lineTo(15 * u, h); lineTo(22 * u, h); close() }, rojo)
                    drawRect(rojo.copy(alpha = 0.7f), Offset(0.6f * u, 0.6f * u), Size(w - 1.2f * u, h - 1.2f * u), style = Stroke(1.2f * u))
                }
            }
            TipoTarjeta.VIDA -> {
                // La carpeta manila con su pestaña arriba a la izquierda.
                val y0 = 5 * u
                val pestana = minOf(w * 0.38f, 70 * u)
                val carpeta = Path().apply {
                    moveTo(0f, y0); lineTo(6 * u, y0); lineTo(9 * u, 0f); lineTo(pestana - 3 * u, 0f); lineTo(pestana, y0)
                    lineTo(w, y0); lineTo(w, h); lineTo(0f, h); close()
                }
                drawPath(carpeta, fondo)
                drawPath(carpeta, raya.copy(alpha = 0.5f), style = Stroke(0.8f * u))
                drawLine(raya.copy(alpha = 0.35f), Offset(11 * u, 2.6f * u), Offset(pestana - 5 * u, 2.6f * u), 0.8f * u)
                // El canto de acero a la izquierda, con su remache.
                drawRect(t(ACERO, oscuro), Offset(0f, y0), Size(2.5f * u, h - y0))
                drawCircle(t(ACERO_LUZ, oscuro), 0.9f * u, Offset(1.25f * u, h / 2))
                // El código de color: tres cuadros abajo a la derecha.
                val lado = 4 * u
                listOf(rojo, t(ACERO, oscuro), t(PELIGRO, oscuro)).forEachIndexed { i, c ->
                    drawRect(c, Offset(w - 6 * u - (3 - i) * (lado + 1.2f * u), h - 6 * u - lado / 2), Size(lado, lado))
                }
            }
        }
    }
}

/**
 * Ciudad neón (solo existe en oscuro): las piezas comunes del callejón. El campo es el vidrio negro del quiosco de
 * información, como su buscador: la chapa violeta con sus tornillos, las líneas de barrido y el tubo de neón que lo enmarca,
 * con sus dos electrodos abajo a la izquierda.
 */
object ComunNeon : DisenoComun {
    private val CARCASA = Color(0xFF221C33)
    private val CARCASA_LUZ = Color(0xFF3B3354)
    private val ALAMBRE = Color(0xFFB9C1CE)
    private val MAGENTA = Color(0xFFFF2E8C)
    private val CIAN = Color(0xFF2EE8E0)
    private val ETIQUETA = Color(0xFFF1EEDF)
    private val VIDRIO = Color(0xFF05040A)

    /** El color bajo la luz del callejón: siempre hundido (el tema solo es oscuro). Los neones no pasan por aquí. */
    @Suppress("UNUSED_PARAMETER")
    private fun t(c: Color, oscuro: Boolean): Color = mezcla(c, Color(0xFF06050B), 0.3f)

    /** Un tubo de neón: el halo ancho, el brillo y el alma blanca. */
    private fun DrawScope.tubo(p: Path, color: Color, grosor: Float, efecto: PathEffect? = null) {
        drawPath(p, color.copy(alpha = 0.16f), style = Stroke(grosor * 4.5f, cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = efecto))
        drawPath(p, color.copy(alpha = 0.4f), style = Stroke(grosor * 2.2f, cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = efecto))
        drawPath(p, mezcla(color, Color.White, 0.5f), style = Stroke(grosor, cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = efecto))
    }

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = ETIQUETA
    override fun pistaCampo(oscuro: Boolean): Color = Color(0xFF9C94A8)
    override fun rotuloCampo(oscuro: Boolean): Color = CIAN
    override fun errorCampo(oscuro: Boolean): Color = Color(0xFFFF6A3A)

    /**
     * El vidrio del quiosco: la chapa con sus cuatro tornillos, el vidrio negro con sus líneas de barrido (y una raya tenue por
     * renglón si son varios) y el tubo de neón con sus electrodos. En reposo el tubo es cian y tibio; con el foco se enciende
     * en magenta; con un error parpadea en naranja, a tramos, como un tubo que se funde; apagado, es vidrio gris sin luz.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, true).let { if (e.habilitado) it else mezcla(it, Color(0xFF2A2A30), 0.4f) }
        drawRoundRect(c(CARCASA), Offset.Zero, size, CornerRadius(8 * u))
        drawRoundRect(c(CARCASA_LUZ), Offset(0.5f * u, 0.5f * u), Size(w - 1 * u, h - 1 * u), CornerRadius(8 * u), style = Stroke(1 * u))
        val vidrio = Rect(4 * u, 4 * u, w - 4 * u, h - 4 * u)
        drawRoundRect(VIDRIO, vidrio.topLeft, vidrio.size, CornerRadius(5 * u))
        // Las líneas de barrido del vidrio.
        val paso = maxOf(3 * u, 1f)
        var y = vidrio.top + 1 * u
        var n = 0
        while (y < vidrio.bottom && n < 2000) { drawLine(Color.White.copy(alpha = 0.025f), Offset(vidrio.left + 3 * u, y), Offset(vidrio.right - 3 * u, y), 0.6f * u); y += paso; n++ }
        if (e.multilinea) renglones(margenCampoY) { _, yy -> drawLine(CIAN.copy(alpha = 0.09f), Offset(vidrio.left + 10 * u, yy), Offset(vidrio.right - 10 * u, yy), 0.7f * u) }
        // El tubo, con el hueco de los electrodos abajo a la izquierda.
        val marco = Path().apply { addRoundRect(RoundRect(vidrio.deflate(2.5f * u), CornerRadius(4 * u))) }
        val prendido = e.habilitado
        val col = when {
            e.error -> errorCampo(true)
            e.enfocado -> MAGENTA
            else -> CIAN
        }
        when {
            !prendido -> drawPath(marco, Color(0xFF4A4852), style = Stroke(0.9f * u))
            e.error -> tubo(marco, col, 1.1f * u, PathEffect.dashPathEffect(floatArrayOf(22 * u, 3 * u, 5 * u, 4 * u)))
            e.enfocado -> tubo(marco, col, 1.1f * u)
            else -> {
                drawPath(marco, col.copy(alpha = 0.1f), style = Stroke(3 * u))
                drawPath(marco, mezcla(col, Color(0xFF3A4048), 0.45f), style = Stroke(0.9f * u))
            }
        }
        val yb = vidrio.bottom - 2.5f * u
        val x0 = vidrio.left + 14 * u
        drawRect(VIDRIO, Offset(x0, yb - 2.6f * u), Size(7 * u, 5.2f * u))
        listOf(x0 - 2.2f * u, x0 + 7 * u).forEach { x ->
            drawRoundRect(c(Color(0xFF6A6478)), Offset(x, yb - 1.4f * u), Size(2.2f * u, 2.8f * u), CornerRadius(0.6f * u))
            drawLine(c(Color(0xFF6A6478)), Offset(x + 1.1f * u, yb + 1.4f * u), Offset(x + 1.1f * u, vidrio.bottom + 1.5f * u), 0.6f * u)
        }
        // Los tornillos de la chapa.
        listOf(Offset(3 * u, 3 * u), Offset(w - 3 * u, 3 * u), Offset(3 * u, h - 3 * u), Offset(w - 3 * u, h - 3 * u))
            .forEach { drawCircle(c(ALAMBRE).copy(alpha = 0.5f), 0.9f * u, it) }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * El tubo de neón que cruza la pared: cian, con su halo, apagándose hacia las puntas (los dos degradados del dibujo), y
     * cortado al centro por sus dos electrodos con la chispa magenta entre ellos. Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val a = if (e.tenue) 0.5f else 1f
        val cx = w / 2
        val hueco = 7 * u
        // El tubo se apaga hacia las puntas en tramos planos (sin degradados: el separador se repite muchas veces en una pantalla).
        val luz = mezcla(CIAN, Color.White, 0.35f)
        val pasos = 6
        val borde = (w * 0.2f).coerceAtLeast(1f)
        fun tramo(x0: Float, x1: Float, k: Float) {
            if (x1 <= x0) return
            drawLine(CIAN.copy(alpha = 0.16f * a * k), Offset(x0, y), Offset(x1, y), 4 * u)
            drawLine(luz.copy(alpha = 0.8f * a * k), Offset(x0, y), Offset(x1, y), 1 * u)
        }
        for (i in 0 until pasos) {
            val k = (i + 1f) / pasos
            val ancho = borde / pasos
            tramo(4 * u + ancho * i, minOf(4 * u + ancho * (i + 1), cx - hueco), k)
            tramo(maxOf(w - 4 * u - ancho * (i + 1), cx + hueco), w - 4 * u - ancho * i, k)
        }
        tramo(4 * u + borde, cx - hueco, 1f)
        tramo(cx + hueco, w - 4 * u - borde, 1f)
        // Los electrodos y la chispa.
        val metal = t(Color(0xFF6A6478), true).copy(alpha = a)
        listOf(cx - hueco - 0.5f * u, cx + hueco - 1.5f * u).forEach { x -> drawRoundRect(metal, Offset(x, y - 1.3f * u), Size(2 * u, 2.6f * u), CornerRadius(0.5f * u)) }
        drawCircle(MAGENTA.copy(alpha = 0.25f * a), 2.6f * u, Offset(cx, y))
        drawCircle(mezcla(MAGENTA, Color.White, 0.4f).copy(alpha = a), 0.9f * u, Offset(cx, y))
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = Color(0xFFFF4D9D)

    /**
     * El tubo bajo el letrero: una raya de neón cian, tibia, con un electrodo en cada punta. Al presionarlo el tubo se enciende
     * en magenta y su luz baña la placa; apagado, es vidrio gris sin luz.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val y = h - 10 * u
        val izq = 9 * u; val der = w - 9 * u
        val raya = Path().apply { moveTo(izq, y); lineTo(der, y) }
        when {
            !e.habilitado -> drawPath(raya, Color(0xFF4A4852), style = Stroke(0.8f * u, cap = StrokeCap.Round))
            e.presionado -> {
                val placa = RoundRect(Rect(3 * u, 6 * u, w - 3 * u, h - 6 * u), CornerRadius(6 * u))
                drawPath(Path().apply { addRoundRect(placa) }, MAGENTA.copy(alpha = 0.1f))
                drawPath(Path().apply { addRoundRect(placa) }, MAGENTA.copy(alpha = 0.3f), style = Stroke(0.8f * u))
                tubo(raya, MAGENTA, 0.9f * u)
            }
            else -> {
                drawPath(raya, CIAN.copy(alpha = 0.12f), style = Stroke(3.5f * u, cap = StrokeCap.Round))
                drawPath(raya, mezcla(CIAN, Color(0xFF3A4048), 0.3f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
            }
        }
        // Los electrodos de las puntas.
        val metal = t(Color(0xFF6A6478), true)
        listOf(izq - 3 * u, der + 1 * u).forEach { x -> drawRoundRect(metal, Offset(x, y - 1.1f * u), Size(2 * u, 2.2f * u), CornerRadius(0.5f * u)) }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El fondo de las placas del callejón: vidrio violeta casi negro. */
    private val PLACA = Color(0xFF150F1E)
    /** El ámbar de los tubos de precaución. */
    private val AMBAR = Color(0xFFFFB02E)

    override val margenFila: Dp = 12.dp

    /**
     * La fila del letrero: un tubo cian apagado al pie (apenas tibio) y el piloto de la izquierda. Al tocarla el tubo se
     * enciende en magenta con su halo, la placa se tiñe de su luz y el piloto se prende.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val y = h - 1.2f * u
        val apagado = mezcla(CIAN, Color(0xFF3A4048), 0.55f)
        if (e.presionado) {
            drawRect(MAGENTA.copy(alpha = 0.08f))
            tubo(Path().apply { moveTo(6 * u, y); lineTo(w - 6 * u, y) }, MAGENTA, 0.7f * u)
        } else {
            drawLine(CIAN.copy(alpha = 0.06f), Offset(6 * u, y), Offset(w - 6 * u, y), 2.5f * u)
            drawLine(apagado.copy(alpha = 0.6f), Offset(6 * u, y), Offset(w - 6 * u, y), 0.6f * u)
        }
        // El piloto de la izquierda; con detalle, un segundo piloto debajo.
        val pilotos = if (e.conDetalle) listOf(h / 2 - 4 * u, h / 2 + 4 * u) else listOf(h / 2)
        pilotos.forEach { py ->
            val p = Offset(4 * u, py)
            if (e.presionado) drawCircle(MAGENTA.copy(alpha = 0.3f), 3 * u, p)
            drawCircle(if (e.presionado) mezcla(MAGENTA, Color.White, 0.4f) else apagado.copy(alpha = 0.7f), 1.1f * u, p)
        }
    }

    /** El «›» de neón cian, con su halo. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        tubo(Path().apply { moveTo(c.x - 3 * u, c.y - 5.5f * u); lineTo(c.x + 3 * u, c.y); lineTo(c.x - 3 * u, c.y + 5.5f * u) }, CIAN, 0.9f * u)
    }

    /** La estrella del letrero: encendida en magenta, con su halo y su luz dentro; sin fijar, el tubo apagado, gris violeta. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2 + 0.5f * u)
        val p = estrella(c, 8 * u, 5, 0.45f)
        if (fija) {
            drawPath(p, MAGENTA.copy(alpha = 0.2f))
            tubo(p, MAGENTA, 0.8f * u)
        } else drawPath(p, Color(0xFF5E5870), style = Stroke(1 * u, join = StrokeJoin.Round))
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = Color(0xFFBBACC6)

    /**
     * El escaparate apagado: la placa de vidrio negro con el contorno de un tubo que no está encendido (cian, a tramos y
     * sin halo) y los cuatro tornillos de la chapa.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRoundRect(PLACA, Offset.Zero, size, CornerRadius(8 * u))
        drawRoundRect(t(CARCASA_LUZ, true), Offset(0.5f * u, 0.5f * u), Size(w - 1 * u, h - 1 * u), CornerRadius(8 * u), style = Stroke(1 * u))
        drawRoundRect(mezcla(CIAN, Color(0xFF3A4048), 0.5f).copy(alpha = 0.5f), Offset(6 * u, 6 * u), Size(w - 12 * u, h - 12 * u), CornerRadius(5 * u),
            style = Stroke(0.9f * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(18 * u, 6 * u))))
        listOf(Offset(3 * u, 3 * u), Offset(w - 3 * u, 3 * u), Offset(3 * u, h - 3 * u), Offset(w - 3 * u, h - 3 * u))
            .forEach { drawCircle(t(ALAMBRE, true).copy(alpha = 0.5f), 0.9f * u, it) }
    }

    /**
     * El puesto cerrado: la cortina metálica bajada, con sus lamas, su barra y su candado, bajo la caja del rollo; encima, el
     * letrero de neón apagado, con una sola chispa magenta que todavía parpadea en una punta.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val metal = t(Color(0xFF5A5468), true)
        val sombra = Color(0xFF06050B)
        // El letrero apagado y su chispa.
        drawRoundRect(Color(0xFF6A6478), Offset(f(0.14f), f(0.04f)), Size(f(0.72f), f(0.18f)), CornerRadius(f(0.06f)), style = Stroke(1.2f * u))
        drawLine(Color(0xFF6A6478), Offset(f(0.28f), f(0.13f)), Offset(f(0.72f), f(0.13f)), 1.2f * u, StrokeCap.Round)
        drawCircle(MAGENTA.copy(alpha = 0.35f), f(0.06f), Offset(f(0.86f), f(0.1f)))
        drawCircle(mezcla(MAGENTA, Color.White, 0.5f), f(0.022f), Offset(f(0.86f), f(0.1f)))
        // La caja del rollo.
        drawRect(t(CARCASA_LUZ, true), Offset(f(0.08f), f(0.28f)), Size(f(0.84f), f(0.08f)))
        // La cortina con sus lamas.
        drawRect(metal, Offset(f(0.12f), f(0.36f)), Size(f(0.76f), f(0.52f)))
        val lamas = Path()
        for (k in 1..7) { val y = f(0.36f + 0.065f * k); lamas.moveTo(f(0.12f), y); lamas.lineTo(f(0.88f), y) }
        drawPath(lamas, sombra.copy(alpha = 0.7f), style = Stroke(0.8f * u))
        drawRect(t(ALAMBRE, true).copy(alpha = 0.6f), Offset(f(0.12f), f(0.36f)), Size(f(0.76f), f(0.52f)), style = Stroke(0.8f * u))
        // La barra de abajo y el candado.
        drawRect(t(ALAMBRE, true), Offset(f(0.1f), f(0.86f)), Size(f(0.8f), f(0.05f)))
        drawArc(t(ALAMBRE, true), 180f, 180f, false, Offset(f(0.45f), f(0.74f)), Size(f(0.1f), f(0.12f)), style = Stroke(1 * u))
        drawRoundRect(AMBAR.copy(alpha = 0.85f), Offset(f(0.43f), f(0.8f)), Size(f(0.14f), f(0.11f)), CornerRadius(f(0.02f)))
        // El suelo mojado, con el reflejo cian.
        drawLine(CIAN.copy(alpha = 0.35f), Offset(f(0.04f), f(0.96f)), Offset(f(0.96f), f(0.96f)), 0.8f * u)
    }

    override val margenAviso: Dp = 30.dp
    override fun tintaAviso(oscuro: Boolean): Color = ETIQUETA

    /**
     * El aviso de obra del callejón: la cinta de precaución en franjas ámbar a la izquierda y, al lado, el triángulo de neón
     * ámbar encendido (con su signo de admiración o, según el aviso, el rayo), sobre la placa de vidrio.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRoundRect(PLACA, Offset.Zero, size, CornerRadius(5 * u))
        drawLine(AMBAR.copy(alpha = 0.18f), Offset(30 * u, h - 1 * u), Offset(w - 6 * u, h - 1 * u), 0.8f * u)
        // La cinta de precaución.
        val ancho = 7 * u
        drawRect(AMBAR.copy(alpha = 0.18f), Offset.Zero, Size(ancho, h))
        clipRect(0f, 0f, ancho, h) {
            val paso = maxOf(8 * u, 1f)
            var y = -ancho
            var n = 0
            while (y < h && n < 400) { drawPath(Path().apply { moveTo(0f, y + 3 * u); lineTo(ancho, y); lineTo(ancho, y + 4 * u); lineTo(0f, y + 7 * u); close() }, AMBAR.copy(alpha = 0.8f)); y += paso; n++ }
        }
        // El triángulo de neón.
        val c = Offset(18.5f * u, 15 * u)
        tubo(Path().apply { moveTo(c.x, c.y - 6 * u); lineTo(c.x + 6.5f * u, c.y + 5 * u); lineTo(c.x - 6.5f * u, c.y + 5 * u); close() }, AMBAR, 0.8f * u)
        if (e.semilla % 2 == 0) {
            drawLine(mezcla(AMBAR, Color.White, 0.5f), Offset(c.x, c.y - 2.5f * u), Offset(c.x, c.y + 1 * u), 1.2f * u, StrokeCap.Round)
            drawCircle(mezcla(AMBAR, Color.White, 0.5f), 0.7f * u, Offset(c.x, c.y + 3 * u))
        } else drawPath(Path().apply { moveTo(c.x + 1 * u, c.y - 3 * u); lineTo(c.x - 1.5f * u, c.y + 0.5f * u); lineTo(c.x + 1.5f * u, c.y + 0.5f * u); lineTo(c.x - 1 * u, c.y + 3.5f * u) },
            mezcla(AMBAR, Color.White, 0.5f), style = Stroke(0.9f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = CIAN
    override fun rotuloCifra(oscuro: Boolean): Color = Color(0xFFFF8CC0)

    /** El letrerito del marcador: la placa de vidrio negro enmarcada por un tubo magenta tibio, con sus dos electrodos abajo. */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRoundRect(PLACA, Offset.Zero, size, CornerRadius(6 * u))
        val r = Rect(2.5f * u, 2.5f * u, w - 2.5f * u, h - 2.5f * u)
        drawRoundRect(MAGENTA.copy(alpha = 0.1f), r.topLeft, r.size, CornerRadius(4 * u), style = Stroke(3 * u))
        drawRoundRect(MAGENTA.copy(alpha = 0.55f), r.topLeft, r.size, CornerRadius(4 * u), style = Stroke(0.8f * u))
        // El hueco de los electrodos, abajo a la derecha.
        val x0 = w - 18 * u
        drawRect(PLACA, Offset(x0, r.bottom - 2 * u), Size(6 * u, 4 * u))
        listOf(x0 - 1.6f * u, x0 + 6 * u).forEach { x -> drawRoundRect(t(Color(0xFF6A6478), true), Offset(x, r.bottom - 1.2f * u), Size(1.6f * u, 2.4f * u), CornerRadius(0.5f * u)) }
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    /** El vidrio de cada tarjeta: el de las placas del callejón; la noche, uno más hondo. */
    private fun fondoTarjeta(tipo: TipoTarjeta): Color = if (tipo == TipoTarjeta.NOCHE) Color(0xFF0B0814) else PLACA

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = ETIQUETA
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = Color(0xFFB9A6C6)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> CIAN
        TipoTarjeta.PENDIENTE -> Color(0xFFFF4D9D)
        TipoTarjeta.NOCHE -> Color(0xFFFF8CC0)
        TipoTarjeta.VIDA -> AMBAR
    }

    /**
     * Las tarjetas del callejón (siempre de noche). La cifra de Hoy es la **pantalla de la recreativa**: la placa de vidrio
     * con dos escuadras de neón cian en esquinas opuestas; destacada, las escuadras se encienden en magenta y un tubo tibio
     * cierra el marco. «Te falta hoy» es la **marquesina**: el tubo magenta alrededor y la hilera de bombillas ámbar arriba y
     * abajo, unas prendidas y otras no; completa, el tubo pasa a cian y se prenden todas, también en cian. La noche son **las
     * azoteas**: la silueta de los tejados al pie, con sus ventanas encendidas, y la luna menguante de neón arriba a la
     * derecha; sellada, se apagan casi todas las ventanas y la luna se llena. Vida es el **letrero del local**: el tubo ámbar
     * de la izquierda y los tornillos de la chapa. Al tocarlas, el vidrio se tiñe de la luz de su tubo.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val radio = CornerRadius(6 * u)
        val fondo = fondoTarjeta(e.tipo)
        val apagado = mezcla(CIAN, Color(0xFF3A4048), 0.55f)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val luz = if (e.destacado) MAGENTA else CIAN
                drawRoundRect(fondo, Offset.Zero, size, radio)
                if (e.presionado) drawRoundRect(luz.copy(alpha = 0.1f), Offset.Zero, size, radio)
                drawRoundRect(t(CARCASA_LUZ, true), Offset(0.5f * u, 0.5f * u), Size(w - 1 * u, h - 1 * u), radio, style = Stroke(1 * u))
                if (e.destacado) drawRoundRect(MAGENTA.copy(alpha = 0.35f), Offset(3 * u, 3 * u), Size(w - 6 * u, h - 6 * u), CornerRadius(4 * u), style = Stroke(0.7f * u))
                // Las dos escuadras de neón: arriba a la izquierda y abajo a la derecha.
                val l = minOf(18 * u, w * 0.2f)
                val g = if (e.presionado) 1 * u else 0.75f * u
                tubo(Path().apply { moveTo(4 * u, 4 * u + l); lineTo(4 * u, 4 * u); lineTo(4 * u + l, 4 * u) }, luz, g)
                tubo(Path().apply { moveTo(w - 4 * u, h - 4 * u - l); lineTo(w - 4 * u, h - 4 * u); lineTo(w - 4 * u - l, h - 4 * u) }, luz, g)
                // El piloto de la esquina libre: tibio, o vivo si destaca.
                val p = Offset(w - 5 * u, 5 * u)
                if (e.destacado) drawCircle(MAGENTA.copy(alpha = 0.3f), 3 * u, p)
                drawCircle(if (e.destacado) mezcla(MAGENTA, Color.White, 0.4f) else apagado.copy(alpha = 0.7f), 1.1f * u, p)
            }
            TipoTarjeta.PENDIENTE -> {
                val luz = if (e.destacado) CIAN else MAGENTA
                drawRoundRect(fondo, Offset.Zero, size, radio)
                if (e.presionado) drawRoundRect(luz.copy(alpha = 0.1f), Offset.Zero, size, radio)
                tubo(Path().apply { addRoundRect(RoundRect(Rect(2.5f * u, 2.5f * u, w - 2.5f * u, h - 2.5f * u), CornerRadius(4.5f * u))) }, luz, 0.9f * u)
                // Las bombillas de la marquesina, arriba y abajo.
                val bombilla = if (e.destacado) CIAN else AMBAR
                val paso = maxOf(11 * u, 1f)
                val n = ((w - 20 * u) / paso).toInt().coerceIn(0, 200)
                val x0 = (w - (n - 1).coerceAtLeast(0) * paso) / 2
                for (i in 0 until n) for ((j, y) in listOf(6.5f * u, h - 6.5f * u).withIndex()) {
                    val c = Offset(x0 + i * paso, y)
                    val prendida = e.destacado || (i + j) % 3 != 0
                    if (prendida) {
                        drawCircle(bombilla.copy(alpha = 0.25f), 2.4f * u, c)
                        drawCircle(mezcla(bombilla, Color.White, 0.45f), 1.1f * u, c)
                    } else drawCircle(t(Color(0xFF6A6478), true), 1 * u, c)
                }
            }
            TipoTarjeta.NOCHE -> {
                drawRoundRect(fondo, Offset.Zero, size, radio)
                if (e.presionado) drawRoundRect(MAGENTA.copy(alpha = 0.08f), Offset.Zero, size, radio)
                drawRoundRect(t(CARCASA_LUZ, true), Offset(0.5f * u, 0.5f * u), Size(w - 1 * u, h - 1 * u), radio,
                    style = Stroke(if (e.destacado) 1.4f * u else 1 * u))
                // La luna: menguante de neón; sellada, llena, con su halo.
                val c = Offset(w - 12 * u, 10 * u)
                val r = 5 * u
                if (e.destacado) {
                    drawCircle(Brush.radialGradient(listOf(MAGENTA.copy(alpha = 0.35f), Color.Transparent), c, r * 3.2f), r * 3.2f, c)
                    drawCircle(mezcla(MAGENTA, Color.White, 0.55f), r, c)
                } else tubo(Path().apply { arcTo(Rect(c.x - r, c.y - r, c.x + r, c.y + r), 60f, 240f, false) }, MAGENTA, 0.7f * u)
                // Las azoteas al pie: la silueta de los tejados, con sus ventanas.
                clipPath(Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), radio)) }) {
                    val silueta = Color(0xFF2B2042)
                    var x = 0f
                    var k = 0
                    while (x < w && k < 200) {
                        val ancho = (14f + 16f * azar(e.semilla + 5, k)) * u
                        val alto = (7f + 8f * azar(e.semilla + 9, k)) * u
                        drawRect(silueta, Offset(x, h - alto), Size(ancho - 1 * u, alto))
                        // Las ventanas: una o dos por azotea.
                        for (v in 0 until 2) {
                            val a = azar(e.semilla + 13 + v, k)
                            val encendida = if (e.destacado) a > 0.9f else a > 0.35f
                            if (!encendida) continue
                            val color = listOf(MAGENTA, CIAN, AMBAR)[(k + v) % 3]
                            drawRect(color.copy(alpha = 0.85f), Offset(x + (3f + 6f * v) * u, h - alto + 2.5f * u), Size(2 * u, 2.2f * u))
                        }
                        x += ancho; k++
                    }
                    drawLine(CIAN.copy(alpha = 0.3f), Offset(0f, h - 0.6f * u), Offset(w, h - 0.6f * u), 0.8f * u)
                }
            }
            TipoTarjeta.VIDA -> {
                drawRoundRect(fondo, Offset.Zero, size, radio)
                if (e.presionado) drawRoundRect(AMBAR.copy(alpha = 0.1f), Offset.Zero, size, radio)
                drawRoundRect(t(CARCASA_LUZ, true), Offset(0.5f * u, 0.5f * u), Size(w - 1 * u, h - 1 * u), radio, style = Stroke(1 * u))
                // El tubo ámbar de la izquierda, con sus electrodos.
                tubo(Path().apply { moveTo(5 * u, 9 * u); lineTo(5 * u, h - 9 * u) }, AMBAR, if (e.presionado) 1.1f * u else 0.8f * u)
                listOf(5.5f * u, h - 5.5f * u).forEach { y -> drawRoundRect(t(Color(0xFF6A6478), true), Offset(4.2f * u, y - 1.2f * u), Size(1.6f * u, 2.4f * u), CornerRadius(0.5f * u)) }
                // Los tornillos de la chapa y el tubo cian, apagado, del canto de arriba.
                listOf(Offset(w - 4 * u, 4 * u), Offset(w - 4 * u, h - 4 * u)).forEach { drawCircle(t(ALAMBRE, true).copy(alpha = 0.55f), 1 * u, it) }
                drawLine(apagado.copy(alpha = 0.45f), Offset(12 * u, 3.5f * u), Offset(w - 12 * u, 3.5f * u), 0.7f * u, StrokeCap.Round)
            }
        }
    }
}

/**
 * Refugio: las piezas comunes del refugio. El campo es la terminal del supervisor, como su buscador: el bisel de chapa crema
 * con su piloto ámbar y su rejilla a la derecha, y la pantalla de fósforo verde de esquinas redondas con sus líneas de
 * barrido y el indicador «>» arriba a la izquierda.
 */
object ComunRefugio : DisenoComun {
    private val MURO = Color(0xFFD9CFAF)
    private val TUBO_OSC = Color(0xFF4E5557)
    private val AMARILLO = Color(0xFFE5B436)
    private val FOSFORO = Color(0xFF7DFF8C)
    private val PANTALLA = Color(0xFF041208)

    /** El color en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0A0F0F), 0.36f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 18.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = FOSFORO
    override fun pistaCampo(oscuro: Boolean): Color = Color(0xFF4FA05A)
    override fun rotuloCampo(oscuro: Boolean): Color = AMARILLO
    override fun errorCampo(oscuro: Boolean): Color = Color(0xFFFF6B52)

    /**
     * La terminal: el bisel crema (hundido en oscuro) con dos tornillos, el piloto ámbar y la rejilla a la derecha; la
     * pantalla de fósforo con sus líneas de barrido, su reflejo y el «>» arriba a la izquierda; con varios renglones, una raya
     * de fósforo tenue por renglón. Con el foco la pantalla se aviva, se ilumina su borde y el «>» y el piloto se prenden; con
     * un error el borde y el piloto van en rojo de alarma.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val bisel = (if (oscuro) mezcla(MURO, Color(0xFF0A0F0F), 0.58f) else MURO).let { if (e.habilitado) it else mezcla(it, Color(0xFF808080), 0.35f) }
        drawRoundRect(bisel, Offset.Zero, size, CornerRadius(6 * u))
        drawRoundRect(t(TUBO_OSC, oscuro).copy(alpha = 0.5f), Offset.Zero, size, CornerRadius(6 * u), style = Stroke(1 * u))
        drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.12f else 0.3f), Offset(2 * u, 1 * u), Size(w - 4 * u, 1.2f * u), CornerRadius(0.6f * u))
        // La pantalla.
        val pant = Rect(5 * u, 5 * u, w - 14 * u, h - 5 * u)
        val radio = CornerRadius(7 * u)
        drawRoundRect(PANTALLA, pant.topLeft, pant.size, radio)
        val vivo = e.enfocado && e.habilitado
        if (vivo) drawRoundRect(FOSFORO.copy(alpha = 0.07f), pant.topLeft, pant.size, radio)
        val paso = maxOf(2.5f * u, 1f)
        var y = pant.top + 1 * u
        var n = 0
        while (y < pant.bottom && n < 3000) { drawLine(Color.Black.copy(alpha = 0.28f), Offset(pant.left + 3 * u, y), Offset(pant.right - 3 * u, y), 0.6f * u); y += paso; n++ }
        if (e.multilinea) renglones(margenCampoY) { _, yy -> drawLine(FOSFORO.copy(alpha = 0.08f), Offset(pant.left + 12 * u, yy), Offset(pant.right - 6 * u, yy), 0.7f * u) }
        drawRoundRect(Color.White.copy(alpha = 0.022f), Offset(pant.left + 4 * u, pant.top + 2 * u), Size(pant.width * 0.6f, minOf(pant.height * 0.3f, 14 * u)), CornerRadius(6 * u))
        drawRoundRect(Color.Black.copy(alpha = 0.6f), pant.topLeft, pant.size, radio, style = Stroke(1.5f * u))
        when {
            e.error -> drawRoundRect(errorCampo(oscuro).copy(alpha = 0.8f), Offset(pant.left + 1 * u, pant.top + 1 * u), Size(pant.width - 2 * u, pant.height - 2 * u), CornerRadius(6 * u), style = Stroke(1.2f * u))
            vivo -> drawRoundRect(FOSFORO.copy(alpha = 0.55f), Offset(pant.left + 1 * u, pant.top + 1 * u), Size(pant.width - 2 * u, pant.height - 2 * u), CornerRadius(6 * u), style = Stroke(1 * u))
        }
        // El «>» del indicador.
        val gx = pant.left + 3.2f * u; val gy = pant.top + minOf(9 * u, pant.height / 2)
        drawPath(Path().apply { moveTo(gx, gy - 3 * u); lineTo(gx + 4 * u, gy); lineTo(gx, gy + 3 * u) },
            FOSFORO.copy(alpha = if (vivo) 1f else 0.45f), style = Stroke(1.4f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // El bisel de la derecha: dos tornillos, el piloto y la rejilla.
        val xb = w - 7 * u
        listOf(Offset(xb, 4 * u), Offset(xb, h - 4 * u)).forEach { o ->
            drawCircle(t(TUBO_OSC, oscuro).copy(alpha = 0.7f), 1.3f * u, o)
            drawLine(Color.Black.copy(alpha = 0.4f), Offset(o.x - 0.8f * u, o.y), Offset(o.x + 0.8f * u, o.y), 0.5f * u)
        }
        val lp = Offset(xb, if (e.multilinea) minOf(h * 0.32f, 16 * u) else h * 0.36f)
        val luz = if (e.error) Color(0xFFFF3B26) else Color(0xFFFFC14A)
        val prendido = (e.enfocado || e.error) && e.habilitado
        if (prendido) drawCircle((if (e.error) Color(0xFFFF3B26) else AMARILLO).copy(alpha = 0.3f), 4.5f * u, lp)
        drawCircle(if (prendido) luz else t(Color(0xFF5A4A20), oscuro), 2.2f * u, lp)
        for (k in 0 until 3) {
            val yr = lp.y + 6 * u + k * 3 * u
            if (yr < h - 7 * u) drawLine(t(TUBO_OSC, oscuro), Offset(xb - 3.5f * u, yr), Offset(xb + 3.5f * u, yr), 1 * u)
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * El conducto del refugio: el tubo de chapa (verde de fósforo apagado en oscuro) con sus abrazaderas en las puntas, una más
     * a media distancia si cabe, y la cajita del piloto ámbar al centro. Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val a = if (e.tenue) 0.5f else 1f
        val tubo = (if (oscuro) Color(0xFF2E6A3E) else TUBO_OSC).copy(alpha = (if (oscuro) 0.8f else 0.45f) * a)
        val cx = w / 2
        val caja = 5 * u
        drawPath(Path().apply { moveTo(3 * u, y); lineTo(cx - caja, y); moveTo(cx + caja, y); lineTo(w - 3 * u, y) }, tubo, style = Stroke(1.3f * u))
        // Las abrazaderas: en las puntas y, si el tramo es largo, a la mitad de cada lado.
        val abrazaderas = mutableListOf(3 * u, w - 5.4f * u)
        if (w > 200 * u) { abrazaderas += (cx - caja) / 2; abrazaderas += cx + caja + (w - cx - caja) / 2 - 2.4f * u }
        abrazaderas.forEach { x -> drawRect(tubo, Offset(x, y - 2.2f * u), Size(2.4f * u, 4.4f * u)) }
        // La caja del piloto, con su luz.
        drawRoundRect(tubo, Offset(cx - caja, y - 3 * u), Size(2 * caja, 6 * u), CornerRadius(1.5f * u), style = Stroke(0.8f * u))
        val luz = if (oscuro) AMARILLO else Color(0xFFB98A15)
        if (oscuro) drawCircle(luz.copy(alpha = 0.25f * a), 3 * u, Offset(cx, y))
        drawCircle(luz.copy(alpha = 0.9f * a), 1.5f * u, Offset(cx, y))
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFF3DFF7F) else Color(0xFF175F5B)

    /**
     * La orden de la terminal: la raya de guiones bajo el texto con el cursor en bloque al final. Al presionarlo se invierte el
     * video (el texto queda sobre un bloque de su color, tenue) y la raya se vuelve continua; apagado, queda una raya de
     * puntos gris, sin cursor.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val izq = 7 * u; val der = w - 11 * u; val pie = h - 10 * u
        if (!e.habilitado) {
            drawLine(Color(0xFF808880).copy(alpha = 0.35f), Offset(izq, pie), Offset(der, pie), 0.9f * u, StrokeCap.Round,
                PathEffect.dashPathEffect(floatArrayOf(0.01f, 2.5f * u)))
            return
        }
        val tinta = tintaBotonTexto(oscuro)
        if (e.presionado) {
            drawRoundRect(tinta.copy(alpha = if (oscuro) 0.13f else 0.12f), Offset(4 * u, 7 * u), Size(w - 8 * u, h - 14 * u), CornerRadius(2 * u))
            drawLine(tinta, Offset(izq, pie), Offset(der, pie), 1.2f * u)
        } else drawLine(tinta.copy(alpha = 0.5f), Offset(izq, pie), Offset(der, pie), 1 * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * u, 2.5f * u)))
        // El cursor en bloque, al final de la raya.
        drawRect(tinta.copy(alpha = if (e.presionado) 1f else 0.7f), Offset(der + 1.5f * u, pie - 1.4f * u), Size(3 * u, 2.8f * u))
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** La chapa de las placas del refugio: crema en claro; en oscuro, la pantalla de fósforo apagada. */
    private fun chapa(oscuro: Boolean): Color = if (oscuro) Color(0xFF0B160E) else Color(0xFFF8F1DE)

    /** El color de la orden de la terminal: verde azulado en claro, fósforo en oscuro. */
    private fun orden(oscuro: Boolean): Color = if (oscuro) Color(0xFF3DFF7F) else Color(0xFF1C6E6A)

    /** La tinta tenue de rayas y contornos. */
    private fun tenue(oscuro: Boolean): Color = if (oscuro) Color(0xFF55B872) else Color(0xFF4C5752)

    override val margenFila: Dp = 12.dp

    /**
     * La línea de la terminal: la raya de guiones al pie y, a la izquierda, el cursor en bloque, apagado (dos bloques si la
     * fila lleva detalle). Al tocarla se invierte el video: la fila se tiñe del color de la orden, el cursor se prende y la
     * raya se vuelve continua.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val c = orden(oscuro)
        val y = h - 1 * u
        if (e.presionado) {
            drawRect(c.copy(alpha = 0.1f))
            drawLine(c.copy(alpha = 0.7f), Offset(0f, y), Offset(w, y), 1 * u)
        } else drawLine((if (oscuro) FOSFORO else TUBO_OSC).copy(alpha = if (oscuro) 0.22f else 0.35f), Offset(0f, y), Offset(w, y), 0.8f * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * u, 2.5f * u)))
        val bloques = if (e.conDetalle) 2 else 1
        for (k in 0 until bloques) {
            val yb = h / 2 + (k - (bloques - 1) / 2f) * 9 * u
            drawRect(c.copy(alpha = if (e.presionado) 1f else 0.3f), Offset(0f, yb - 3.5f * u), Size(3 * u, 7 * u))
        }
    }

    /** El indicador «>» de la terminal, con su raya de cursor debajo. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        val tinta = orden(oscuro)
        drawPath(Path().apply { moveTo(c.x - 4 * u, c.y - 4.5f * u); lineTo(c.x + 2 * u, c.y); lineTo(c.x - 4 * u, c.y + 4.5f * u) }, tinta,
            style = Stroke(1.6f * u, cap = StrokeCap.Square, join = StrokeJoin.Miter))
        drawRect(tinta.copy(alpha = 0.6f), Offset(c.x + 3 * u, c.y + 3 * u), Size(3.5f * u, 1.6f * u))
    }

    /**
     * La insignia del refugio: el disco con su estrella. Fija, el disco es amarillo pleno (con su resplandor en oscuro) y la
     * estrella va oscura; sin fijar, contorno tenue del disco y de la estrella.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        val r = 8.5f * u
        val estrellita = estrella(c, 5.4f * u, 5, 0.42f)
        if (fija) {
            if (oscuro) drawCircle(AMARILLO.copy(alpha = 0.25f), r + 2 * u, c)
            drawCircle(AMARILLO, r, c)
            drawCircle(Color.Black.copy(alpha = 0.25f), r - 1.2f * u, c, style = Stroke(0.6f * u))
            drawPath(estrellita, if (oscuro) PANTALLA else Color(0xFF1F2A2A))
        } else {
            val g = tenue(oscuro).copy(alpha = 0.6f)
            drawCircle(g, r, c, style = Stroke(1 * u))
            drawPath(estrellita, g, style = Stroke(0.9f * u, join = StrokeJoin.Miter))
        }
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = tenue(oscuro)

    /**
     * El panel sin lecturas: en claro, la chapa crema con sus cuatro pernos y las franjas de peligro en la esquina de abajo; en
     * oscuro, la pantalla de fósforo apagada con sus líneas de barrido. Un borde fino, sin brillo: nada que tocar.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRoundRect(chapa(oscuro), Offset.Zero, size, CornerRadius(6 * u))
        if (oscuro) {
            val paso = maxOf(3 * u, 1f)
            var y = 2 * u
            var n = 0
            while (y < h - 2 * u && n < 400) { drawLine(Color.Black.copy(alpha = 0.35f), Offset(4 * u, y), Offset(w - 4 * u, y), 0.6f * u); y += paso; n++ }
            drawRoundRect(FOSFORO.copy(alpha = 0.28f), Offset.Zero, size, CornerRadius(6 * u), style = Stroke(1 * u))
        } else {
            drawRoundRect(TUBO_OSC.copy(alpha = 0.45f), Offset.Zero, size, CornerRadius(6 * u), style = Stroke(1 * u))
            drawLine(Color.White.copy(alpha = 0.6f), Offset(6 * u, 1.5f * u), Offset(w - 6 * u, 1.5f * u), 1 * u)
            // Las franjas de peligro, en la esquina de abajo a la izquierda.
            val k = minOf(16 * u, h * 0.3f)
            clipPath(Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(6 * u))) }) {
                clipPath(Path().apply { moveTo(0f, h - k); lineTo(k, h); lineTo(0f, h); close() }) {
                    drawRect(Color(0xFF1F2A2A).copy(alpha = 0.8f), Offset(0f, h - k), Size(k, k))
                    for (i in 0 until 4) drawPath(Path().apply { val x = i * 5 * u; moveTo(x, h); lineTo(x + 2.5f * u, h); lineTo(x - k + 2.5f * u, h - k); lineTo(x - k, h - k); close() }, AMARILLO)
                }
            }
        }
        listOf(Offset(4 * u, 4 * u), Offset(w - 4 * u, 4 * u), Offset(w - 4 * u, h - 4 * u)).forEach {
            drawCircle(t(TUBO_OSC, oscuro).copy(alpha = 0.7f), 1.4f * u, it)
            drawLine(Color.Black.copy(alpha = 0.4f), Offset(it.x - 0.9f * u, it.y), Offset(it.x + 0.9f * u, it.y), 0.5f * u)
        }
    }

    /**
     * La lata de conservas abierta y vacía: el cuerpo con sus dos costillas y la etiqueta sin nada, el interior oscuro a la
     * vista y la tapa levantada, todavía colgando del borde.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val l = tenue(oscuro)
        val acento = if (oscuro) AMARILLO else Color(0xFFB98A15)
        val tr = Stroke(1.3f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // La etiqueta, debajo de todo.
        drawRect(acento.copy(alpha = 0.22f), Offset(f(0.22f), f(0.5f)), Size(f(0.56f), f(0.16f)))
        // El cuerpo.
        drawLine(l, Offset(f(0.22f), f(0.35f)), Offset(f(0.22f), f(0.84f)), tr.width)
        drawLine(l, Offset(f(0.78f), f(0.35f)), Offset(f(0.78f), f(0.84f)), tr.width)
        drawArc(l, 0f, 180f, false, Offset(f(0.22f), f(0.78f)), Size(f(0.56f), f(0.12f)), style = tr)
        listOf(0.44f, 0.6f).forEach { y -> drawArc(l.copy(alpha = 0.5f), 0f, 180f, false, Offset(f(0.22f), f(y)), Size(f(0.56f), f(0.12f)), style = Stroke(0.8f * u)) }
        // La boca, con el interior vacío.
        drawOval(if (oscuro) Color.Black else Color(0xFF2A3032), Offset(f(0.24f), f(0.3f)), Size(f(0.52f), f(0.1f)))
        drawOval(l, Offset(f(0.22f), f(0.29f)), Size(f(0.56f), f(0.12f)), style = tr)
        // La tapa levantada, colgando del borde de la derecha.
        rotate(36f, Offset(f(0.78f), f(0.35f))) {
            drawOval(acento.copy(alpha = 0.18f), Offset(f(0.22f), f(0.29f)), Size(f(0.56f), f(0.12f)))
            drawOval(acento, Offset(f(0.22f), f(0.29f)), Size(f(0.56f), f(0.12f)), style = tr)
            drawOval(acento.copy(alpha = 0.45f), Offset(f(0.3f), f(0.315f)), Size(f(0.4f), f(0.07f)), style = Stroke(0.7f * u))
        }
        // La sombra en el suelo.
        drawLine(l.copy(alpha = 0.4f), Offset(f(0.14f), f(0.94f)), Offset(f(0.9f), f(0.94f)), 0.8f * u, StrokeCap.Round)
    }

    override val margenAviso: Dp = 30.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) Color(0xFF9CF5B0) else Color(0xFF1F2A2A)

    /**
     * El cartel de seguridad del refugio: la chapa con la franja de peligro amarilla y negra a la izquierda y, arriba, la señal
     * (el triángulo con su admiración o, según el aviso, el trébol de radiación) sobre su disco amarillo.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRoundRect(chapa(oscuro), Offset.Zero, size, CornerRadius(4 * u))
        drawRoundRect(tenue(oscuro).copy(alpha = 0.35f), Offset.Zero, size, CornerRadius(4 * u), style = Stroke(0.8f * u))
        // La franja de peligro.
        val ancho = 6 * u
        val amarillo = if (oscuro) Color(0xFFFFB23F) else AMARILLO
        clipRect(0f, 0f, ancho, h) {
            drawRect(Color(0xFF1F2A2A))
            val paso = maxOf(7 * u, 1f)
            var y = -ancho
            var n = 0
            while (y < h && n < 400) { drawPath(Path().apply { moveTo(0f, y + ancho); lineTo(ancho, y); lineTo(ancho, y + 3.5f * u); lineTo(0f, y + ancho + 3.5f * u); close() }, amarillo); y += paso; n++ }
        }
        // La señal.
        val c = Offset(18 * u, 15 * u)
        val oscura = Color(0xFF1F2A2A)
        drawCircle(amarillo, 7 * u, c)
        if (e.semilla % 2 == 0) {
            drawPath(Path().apply { moveTo(c.x, c.y - 4.5f * u); lineTo(c.x + 4.5f * u, c.y + 3.2f * u); lineTo(c.x - 4.5f * u, c.y + 3.2f * u); close() }, oscura, style = Stroke(1 * u, join = StrokeJoin.Round))
            drawLine(oscura, Offset(c.x, c.y - 1.6f * u), Offset(c.x, c.y + 0.6f * u), 1 * u)
            drawCircle(oscura, 0.6f * u, Offset(c.x, c.y + 2 * u))
        } else {
            listOf(-90f, 30f, 150f).forEach { a -> drawArc(oscura, a - 30f, 60f, true, Offset(c.x - 5 * u, c.y - 5 * u), Size(10 * u, 10 * u)) }
            drawCircle(amarillo, 1.8f * u, c)
            drawCircle(oscura, 1.1f * u, c)
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFF9CF5B0) else Color(0xFF1F2A2A)
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFF55B872) else Color(0xFF1C6E6A)

    /**
     * El lector del panel: la chapa (o la pantalla apagada, en oscuro) con su borde, el brillo de arriba, dos tornillos y el
     * piloto ámbar del canto de la derecha.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRoundRect(chapa(oscuro), Offset.Zero, size, CornerRadius(5 * u))
        drawRoundRect((if (oscuro) FOSFORO else TUBO_OSC).copy(alpha = if (oscuro) 0.3f else 0.45f), Offset.Zero, size, CornerRadius(5 * u), style = Stroke(1 * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.6f), Offset(5 * u, 1.6f * u), Offset(w - 5 * u, 1.6f * u), 0.9f * u)
        listOf(Offset(3.5f * u, h - 3.5f * u), Offset(w - 3.5f * u, h - 3.5f * u)).forEach { drawCircle(t(TUBO_OSC, oscuro).copy(alpha = 0.6f), 1 * u, it) }
        val p = Offset(w - 4 * u, 5 * u)
        if (oscuro) drawCircle(AMARILLO.copy(alpha = 0.25f), 3 * u, p)
        drawCircle(if (oscuro) Color(0xFFFFC14A) else Color(0xFFB98A15), 1.3f * u, p)
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFF9CF5B0) else Color(0xFF1F2A2A)
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = tenue(oscuro)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.PENDIENTE, TipoTarjeta.VIDA -> if (oscuro) Color(0xFFFFC14A) else Color(0xFF175F5B)
        else -> orden(oscuro)
    }

    /**
     * Las tarjetas del refugio, sobre la chapa crema (o la pantalla de fósforo apagada, en oscuro). La cifra de Hoy es el
     * **contador de la sala de máquinas**: la chapa con su cabecera de metal oscuro, dos ranuras de ventilación y dos pernos al
     * pie; destacada, la cabecera se pinta de amarillo y se prende su piloto. «Te falta hoy» es la **alerta de la consola**:
     * las franjas de peligro amarillas y negras arriba y abajo; completa, las franjas se quitan, queda una raya verde y aparece
     * a la derecha la rueda de la escotilla. La noche es la **lámpara de la galería**: la bombilla enjaulada colgando del canto
     * de arriba, con su luz tibia; sellada, la lámpara se apaga y los dos cerrojos ámbar corren en el canto de la derecha.
     * Vida es el **carné del refugio**: la franja de color arriba, el ojal del cordón y el código de barras abajo a la
     * derecha. Al tocarlas la chapa se hunde.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val radio = CornerRadius(5 * u)
        val fondo = chapa(oscuro).let { if (e.presionado) mezcla(it, tenue(oscuro), if (oscuro) 0.14f else 0.1f) else it }
        val borde = (if (oscuro) FOSFORO else TUBO_OSC).copy(alpha = if (oscuro) 0.3f else 0.45f)
        val oscura = Color(0xFF1F2A2A)
        val amarillo = if (oscuro) Color(0xFFFFB23F) else AMARILLO
        drawRoundRect(fondo, Offset.Zero, size, radio)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                // La cabecera de metal, con sus dos ranuras y el piloto.
                val alto = 5 * u
                val cabecera = Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, alto), CornerRadius(5 * u, 5 * u), CornerRadius(5 * u, 5 * u), CornerRadius.Zero, CornerRadius.Zero)) }
                drawPath(cabecera, if (e.destacado) amarillo else t(TUBO_OSC, oscuro))
                listOf(10 * u, 18 * u).forEach { x -> drawRoundRect(Color.Black.copy(alpha = 0.45f), Offset(x, 1.6f * u), Size(5 * u, 1.6f * u), CornerRadius(0.8f * u)) }
                val p = Offset(w - 8 * u, alto / 2)
                if (e.destacado) {
                    drawCircle(Color(0xFFFF6A3D).copy(alpha = 0.35f), 2.5f * u, p)
                    drawCircle(Color(0xFFFF6A3D), 1.5f * u, p)
                } else drawCircle(Color.Black.copy(alpha = 0.4f), 1.3f * u, p)
                drawRoundRect(if (e.destacado) amarillo else borde, Offset.Zero, size, radio, style = Stroke(if (e.destacado) 1.4f * u else 1 * u))
                // Los pernos del pie.
                listOf(Offset(4 * u, h - 4 * u), Offset(w - 4 * u, h - 4 * u)).forEach {
                    drawCircle(t(TUBO_OSC, oscuro).copy(alpha = 0.7f), 1.4f * u, it)
                    drawLine(Color.Black.copy(alpha = 0.4f), Offset(it.x - 0.9f * u, it.y), Offset(it.x + 0.9f * u, it.y), 0.5f * u)
                }
            }
            TipoTarjeta.PENDIENTE -> {
                drawRoundRect(borde, Offset.Zero, size, radio, style = Stroke(1 * u))
                clipPath(Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), radio)) }) {
                    if (!e.destacado) {
                        // Las franjas de peligro, arriba y abajo.
                        val alto = 5 * u
                        for (y0 in listOf(0f, h - alto)) clipRect(0f, y0, w, y0 + alto) {
                            drawRect(oscura, Offset(0f, y0), Size(w, alto))
                            val paso = maxOf(9 * u, 1f)
                            var x = -alto
                            var n = 0
                            while (x < w && n < 400) {
                                drawPath(Path().apply { moveTo(x, y0 + alto); lineTo(x + alto, y0); lineTo(x + alto + 4.5f * u, y0); lineTo(x + 4.5f * u, y0 + alto); close() }, amarillo)
                                x += paso; n++
                            }
                        }
                    } else {
                        listOf(0f, h - 2.5f * u).forEach { y -> drawRect(orden(oscuro), Offset(0f, y), Size(w, 2.5f * u)) }
                    }
                }
                // Completa: la rueda de la escotilla, cerrada, a la derecha.
                if (e.destacado) {
                    val r = minOf(h * 0.3f, 20 * u)
                    val c = Offset(w - r - 12 * u, h / 2)
                    val l = orden(oscuro).copy(alpha = 0.6f)
                    drawCircle(l, r, c, style = Stroke(2 * u))
                    for (k in 0 until 3) rotate(k * 60f + 15f, c) { drawLine(l, Offset(c.x - r, c.y), Offset(c.x + r, c.y), 1.4f * u) }
                    drawCircle(l, r * 0.28f, c)
                }
            }
            TipoTarjeta.NOCHE -> {
                drawRoundRect(if (e.destacado) orden(oscuro).copy(alpha = 0.7f) else borde, Offset.Zero, size, radio, style = Stroke(if (e.destacado) 1.4f * u else 1 * u))
                // La lámpara de la galería, colgando del canto de arriba.
                val c = Offset(w * 0.6f, 8.5f * u)
                val r = 3.2f * u
                if (!e.destacado) clipPath(Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), radio)) }) {
                    drawCircle(Brush.radialGradient(listOf(amarillo.copy(alpha = if (oscuro) 0.3f else 0.4f), Color.Transparent), c, 34 * u), 34 * u, c)
                }
                drawLine(t(TUBO_OSC, oscuro), Offset(c.x, 0f), Offset(c.x, c.y - r - 1 * u), 0.9f * u)
                drawRect(t(TUBO_OSC, oscuro), Offset(c.x - 1.8f * u, c.y - r - 1.6f * u), Size(3.6f * u, 1.6f * u))
                drawCircle(if (e.destacado) Color(0xFF6E7472) else mezcla(amarillo, Color.White, 0.5f), r, c)
                // La jaula: tres alambres y el aro.
                val jaula = t(TUBO_OSC, oscuro)
                listOf(-1f, 0f, 1f).forEach { s -> drawLine(jaula, Offset(c.x + s * r * 0.9f, c.y - r), Offset(c.x + s * r * 1.2f, c.y + r * 0.6f), 0.6f * u) }
                drawArc(jaula, 0f, 180f, false, Offset(c.x - r * 1.2f, c.y - r * 0.4f), Size(r * 2.4f, r * 2f), style = Stroke(0.7f * u))
                // Sellada: los dos cerrojos ámbar corridos en el canto de la derecha.
                if (e.destacado) listOf(h * 0.22f, h * 0.78f).forEach { y ->
                    drawRoundRect(amarillo, Offset(w - 9 * u, y - 2 * u), Size(9 * u, 4 * u), CornerRadius(1.2f * u))
                    drawCircle(oscura, 0.9f * u, Offset(w - 6 * u, y))
                }
            }
            TipoTarjeta.VIDA -> {
                drawRoundRect(borde, Offset.Zero, size, radio, style = Stroke(1 * u))
                // La franja del carné, con el ojal del cordón.
                drawRect(acentoTarjeta(e.tipo, oscuro).copy(alpha = if (oscuro) 0.55f else 0.85f), Offset(0f, 5 * u), Size(w, 2.5f * u))
                val ojal = Offset(w / 2, 2.6f * u)
                drawRoundRect(Color.Black.copy(alpha = 0.5f), Offset(ojal.x - 5 * u, ojal.y - 1 * u), Size(10 * u, 2 * u), CornerRadius(1 * u))
                // El código de barras abajo a la derecha.
                var x = w - 6 * u
                var k = 0
                while (k < 11) {
                    val g = (if (azar(41, k) > 0.6f) 1.2f else 0.5f) * u
                    drawRect(tenue(oscuro).copy(alpha = 0.6f), Offset(x - g, h - 9 * u), Size(g, 5 * u))
                    x -= g + 0.9f * u; k++
                }
            }
        }
    }
}

/**
 * Orden: las piezas comunes de la orden. El campo es la ranura del archivo sellado, como su buscador: la plancha de acero
 * con su filete de latón y sus remaches, el hueco ochavado de esmalte negro donde se escribe y, en cada esquina, un engrane
 * de latón con su lente.
 */
object ComunOrden : DisenoComun {
    private val ACERO = Color(0xFF3C424A)
    private val ACERO_LUZ = Color(0xFF6A727C)
    private val LATON = Color(0xFFB8933E)
    private val LATON_LUZ = Color(0xFFE0C070)
    private val ALERTA = Color(0xFFFF2B1E)
    private val ESMALTE = Color(0xFF0C0D10)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF09090B), 0.35f) else c

    /** El latón: se hunde menos que el acero, para que siga brillando en oscuro. */
    private fun placa(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF09090B), 0.16f) else c

    /** Un rectángulo con las cuatro esquinas cortadas en [ch]. */
    private fun ochavado(r: Rect, ch: Float): Path = Path().apply {
        moveTo(r.left + ch, r.top); lineTo(r.right - ch, r.top); lineTo(r.right, r.top + ch); lineTo(r.right, r.bottom - ch)
        lineTo(r.right - ch, r.bottom); lineTo(r.left + ch, r.bottom); lineTo(r.left, r.bottom - ch); lineTo(r.left, r.top + ch); close()
    }

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 11.dp
    override fun tintaCampo(oscuro: Boolean): Color = Color(0xFFEFE3C2)
    override fun pistaCampo(oscuro: Boolean): Color = Color(0xFF8E8672)
    override fun rotuloCampo(oscuro: Boolean): Color = LATON_LUZ
    override fun errorCampo(oscuro: Boolean): Color = Color(0xFFFF6150)

    /**
     * La ranura sellada: la plancha de acero con su filete de latón y los remaches a lo largo de los cantos; el hueco ochavado
     * de esmalte negro con su filo de latón y su sombra de arriba (y una raya tenue por renglón si son varios); un engrane de
     * latón con su lente en cada esquina. Con el foco el filo se pule y engorda, los engranes giran y sus lentes se calientan en
     * ámbar; con un error el filo y las lentes van en rojo de alerta; apagado, el latón se vuelve plomo.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro).let { if (e.habilitado) it else mezcla(it, Color(0xFF55585C), 0.35f) }
        fun lt(x: Color) = placa(x, oscuro).let { if (e.habilitado) it else mezcla(it, Color(0xFF6E6E6E), 0.6f) }
        drawRoundRect(c(ACERO), Offset.Zero, size, CornerRadius(3 * u))
        drawRoundRect(lt(LATON), Offset(0.6f * u, 0.6f * u), Size(w - 1.2f * u, h - 1.2f * u), CornerRadius(3 * u), style = Stroke(1.2f * u))
        // Los remaches de los cantos, entre los engranes.
        val paso = maxOf(34 * u, 1f)
        var x = 18 * u
        var n = 0
        while (x < w - 16 * u && n < 300) {
            listOf(3.2f * u, h - 3.2f * u).forEach { y ->
                drawCircle(Color.Black.copy(alpha = 0.4f), 1.2f * u, Offset(x + 0.3f * u, y + 0.4f * u))
                drawCircle(c(ACERO_LUZ), 1.1f * u, Offset(x, y))
            }
            x += paso; n++
        }
        if (h > 80 * u) {
            var y = 18 * u
            var m = 0
            while (y < h - 16 * u && m < 300) {
                listOf(3.2f * u, w - 3.2f * u).forEach { xx -> drawCircle(Color.Black.copy(alpha = 0.4f), 1.2f * u, Offset(xx + 0.3f * u, y + 0.4f * u)); drawCircle(c(ACERO_LUZ), 1.1f * u, Offset(xx, y)) }
                y += paso; m++
            }
        }
        // El hueco ochavado de esmalte.
        val r = Rect(6.5f * u, 6.5f * u, w - 6.5f * u, h - 6.5f * u)
        val hueco = ochavado(r, 6 * u)
        drawPath(hueco, ESMALTE)
        clipPath(hueco) {
            drawRect(Color.Black.copy(alpha = 0.55f), r.topLeft, Size(r.width, 2.2f * u))
            if (e.multilinea) renglones(margenCampoY) { _, y -> drawLine(LATON.copy(alpha = 0.12f), Offset(r.left + 8 * u, y), Offset(r.right - 8 * u, y), 0.7f * u) }
        }
        val filo = when {
            e.error -> errorCampo(oscuro)
            e.enfocado -> lt(LATON_LUZ)
            else -> lt(LATON)
        }
        drawPath(hueco, filo, style = Stroke((if (e.enfocado || e.error) 2f else 1.2f) * u))
        // Los engranes de las esquinas, con su lente.
        val giro = if (e.enfocado) 15f else 0f
        val lente = when {
            !e.habilitado -> Color(0xFF1A1C20)
            e.error -> mezcla(Color(0xFF2A0A0A), ALERTA, 0.6f)
            e.enfocado -> Color(0xFFE09A30)
            else -> Color(0xFF1A2228)
        }
        listOf(Offset(5 * u, 5 * u), Offset(w - 5 * u, 5 * u), Offset(5 * u, h - 5 * u), Offset(w - 5 * u, h - 5 * u)).forEach { o ->
            val rr = 4.2f * u
            rotate(giro, o) {
                drawPath(engrane(Offset(o.x + 0.5f * u, o.y + 0.6f * u), rr, 8), Color.Black.copy(alpha = 0.35f))
                drawPath(engrane(o, rr, 8), lt(LATON))
            }
            drawCircle(lt(LATON_LUZ), rr * 0.6f, o)
            drawCircle(lente, rr * 0.42f, o)
            drawCircle(Color.White.copy(alpha = 0.4f), rr * 0.13f, Offset(o.x - rr * 0.14f, o.y - rr * 0.14f))
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /**
     * El filete doble de latón, con un remache en cada punta y, al centro, el engrane de la orden con su lente. Tenue, a la
     * mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val a = if (e.tenue) 0.5f else 1f
        val laton = (if (oscuro) placa(LATON, true) else Color(0xFF8C6E2A)).copy(alpha = 0.7f * a)
        val cx = w / 2
        val r = 4 * u
        val hueco = r + 4 * u
        // El filete doble.
        val filete = Path().apply {
            listOf(-1.2f * u, 1.2f * u).forEach { dy -> moveTo(8 * u, y + dy); lineTo(cx - hueco, y + dy); moveTo(cx + hueco, y + dy); lineTo(w - 8 * u, y + dy) }
        }
        drawPath(filete, laton, style = Stroke(0.7f * u))
        // Los remaches de las puntas.
        listOf(4.5f * u, w - 4.5f * u).forEach { drawCircle(laton, 1.3f * u, Offset(it, y)) }
        // El engrane del centro, con su lente.
        drawPath(engrane(Offset(cx, y), r, 8), laton)
        drawCircle(Color(0xFF1A2228).copy(alpha = 0.85f * a), r * 0.42f, Offset(cx, y))
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFD6B25A) else Color(0xFF6E5414)

    /**
     * La placa de latón: el contorno ochavado, fino, con un remache a cada lado. Al presionarlo la placa se pule (se rellena de
     * latón tenue y el contorno engorda); apagada, es de plomo.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(3 * u, 7 * u, w - 3 * u, h - 7 * u)
        val contorno = ochavado(r, 4 * u)
        val laton = if (e.habilitado) tintaBotonTexto(oscuro) else Color(0xFF8A8C90)
        val pulida = e.presionado && e.habilitado
        if (pulida) drawPath(contorno, laton.copy(alpha = if (oscuro) 0.16f else 0.14f))
        drawPath(contorno, laton.copy(alpha = when { !e.habilitado -> 0.25f; pulida -> 0.9f; else -> 0.3f }), style = Stroke((if (pulida) 1.3f else 0.9f) * u))
        // Los dos remaches, a los lados.
        listOf(r.left + 3.5f * u, r.right - 3.5f * u).forEach { drawCircle(laton.copy(alpha = if (e.habilitado) 0.7f else 0.3f), 1 * u, Offset(it, r.center.y)) }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** La plancha de las placas de la orden: acero claro pulido, o pavonado en oscuro. */
    private fun plancha(oscuro: Boolean): Color = if (oscuro) Color(0xFF1A1D22) else Color(0xFFF0F2F4)

    /** El latón de filetes y remaches, más oscuro sobre el acero claro. */
    private fun filete(oscuro: Boolean): Color = if (oscuro) placa(LATON, true) else Color(0xFF8C6E2A)

    /** Uno o varios galones (las «V» de los grados) apuntando hacia [d] desde [c]; [d] = (1, 0) apunta a la derecha. */
    private fun galones(c: Offset, ancho: Float, alto: Float, cuantos: Int, sep: Float, d: Offset): Path = Path().apply {
        for (k in 0 until cuantos) {
            val o = Offset(c.x + d.x * k * sep, c.y + d.y * k * sep)
            if (d.x != 0f) { moveTo(o.x - ancho / 2, o.y - alto / 2); lineTo(o.x + ancho / 2, o.y); lineTo(o.x - ancho / 2, o.y + alto / 2) }
            else { moveTo(o.x - ancho / 2, o.y + alto / 2); lineTo(o.x, o.y - alto / 2); lineTo(o.x + ancho / 2, o.y + alto / 2) }
        }
    }

    override val margenFila: Dp = 12.dp

    /**
     * La fila del escalafón: el filete de latón al pie con un remache en la punta y otro a la izquierda, a media altura. Al
     * tocarla la plancha se pule (un baño de latón tenue) y el remache se vuelve un galón doble, en latón vivo.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val laton = filete(oscuro)
        if (e.presionado) drawRect(laton.copy(alpha = if (oscuro) 0.12f else 0.1f))
        drawLine(laton.copy(alpha = if (e.presionado) 0.8f else 0.4f), Offset(4 * u, h - 1 * u), Offset(w - 6 * u, h - 1 * u), 0.7f * u)
        drawCircle(laton.copy(alpha = 0.6f), 1.2f * u, Offset(w - 3 * u, h - 1 * u))
        if (e.presionado) drawPath(galones(Offset(4 * u, h / 2), 4 * u, 8 * u, 2, 2.6f * u, Offset(1f, 0f)), tintaBotonTexto(oscuro), style = Stroke(1.2f * u, join = StrokeJoin.Miter))
        else drawCircle(laton.copy(alpha = 0.55f), 1.2f * u, Offset(4 * u, h / 2))
    }

    /** El galón doble de latón, apuntando hacia adelante. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        drawPath(galones(Offset(size.width / 2 - 2.5f * u, size.height / 2), 5 * u, 10 * u, 2, 4 * u, Offset(1f, 0f)), tintaBotonTexto(oscuro),
            style = Stroke(1.5f * u, join = StrokeJoin.Miter))
    }

    /**
     * El engrane de la orden: fijo, de latón macizo con su lente encendida en rojo; sin fijar, solo el contorno del engrane y
     * de la lente, en plomo.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        val r = 8.5f * u
        if (fija) {
            drawPath(engrane(Offset(c.x + 0.5f * u, c.y + 0.6f * u), r, 8), Color.Black.copy(alpha = 0.3f))
            drawPath(engrane(c, r, 8), placa(LATON, oscuro))
            drawCircle(placa(LATON_LUZ, oscuro), r * 0.58f, c)
            drawCircle(mezcla(Color(0xFF2A0A0A), ALERTA, 0.75f), r * 0.4f, c)
            drawCircle(Color.White.copy(alpha = 0.5f), r * 0.12f, Offset(c.x - r * 0.14f, c.y - r * 0.14f))
        } else {
            val plomo = (if (oscuro) Color(0xFF8A9098) else Color(0xFF5E646C)).copy(alpha = 0.6f)
            drawPath(engrane(c, r, 8), plomo, style = Stroke(0.9f * u, join = StrokeJoin.Miter))
            drawCircle(plomo, r * 0.4f, c, style = Stroke(0.9f * u))
        }
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) Color(0xFF9DA6B0) else Color(0xFF4B525A)

    /**
     * La vitrina del arsenal sin nada dentro: la plancha ochavada con su filete de latón hacia dentro y una hilera de remaches
     * en los cantos de arriba y de abajo.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val laton = filete(oscuro)
        val todo = ochavado(Rect(0f, 0f, w, h), 7 * u)
        drawPath(todo, plancha(oscuro))
        drawPath(todo, t(ACERO_LUZ, oscuro).copy(alpha = 0.6f), style = Stroke(1 * u))
        drawPath(ochavado(Rect(4 * u, 4 * u, w - 4 * u, h - 4 * u), 5 * u), laton.copy(alpha = 0.55f), style = Stroke(0.8f * u))
        val paso = maxOf(34 * u, 1f)
        var x = 16 * u
        var n = 0
        while (x < w - 14 * u && n < 300) {
            listOf(2 * u, h - 2 * u).forEach { y -> drawCircle(laton.copy(alpha = 0.6f), 0.9f * u, Offset(x, y)) }
            x += paso; n++
        }
    }

    /**
     * La caja fuerte abierta y vacía: el cuerpo de acero con su filete de latón, el hueco negro con su repisa y nada en ella, y
     * la puerta abierta de par en par, con la rueda de la combinación (el engrane) y su manija.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val acero = t(ACERO, oscuro).let { if (oscuro) mezcla(it, Color(0xFF8A929C), 0.25f) else it }
        val laton = placa(LATON, oscuro)
        // El cuerpo y su hueco.
        drawRect(acero, Offset(f(0.08f), f(0.1f)), Size(f(0.62f), f(0.78f)))
        drawRect(laton, Offset(f(0.08f), f(0.1f)), Size(f(0.62f), f(0.78f)), style = Stroke(0.9f * u))
        drawRect(ESMALTE, Offset(f(0.15f), f(0.17f)), Size(f(0.48f), f(0.64f)))
        drawLine(t(ACERO_LUZ, oscuro).copy(alpha = 0.6f), Offset(f(0.15f), f(0.5f)), Offset(f(0.63f), f(0.5f)), 1 * u)
        drawRect(Color.Black.copy(alpha = 0.5f), Offset(f(0.15f), f(0.17f)), Size(f(0.48f), f(0.05f)))
        // Las patas.
        listOf(0.12f, 0.58f).forEach { drawRect(acero, Offset(f(it), f(0.88f)), Size(f(0.08f), f(0.05f))) }
        // La puerta abierta.
        val puerta = Path().apply { moveTo(f(0.7f), f(0.1f)); lineTo(f(0.94f), f(0.2f)); lineTo(f(0.94f), f(0.8f)); lineTo(f(0.7f), f(0.88f)); close() }
        drawPath(puerta, t(ACERO_LUZ, oscuro))
        drawPath(puerta, laton, style = Stroke(0.9f * u, join = StrokeJoin.Miter))
        val rueda = Offset(f(0.83f), f(0.45f))
        drawPath(engrane(rueda, f(0.08f), 8), laton)
        drawCircle(ESMALTE, f(0.03f), rueda)
        drawLine(ESMALTE, Offset(f(0.83f), f(0.6f)), Offset(f(0.83f), f(0.72f)), 1.6f * u, StrokeCap.Round)
    }

    override val margenAviso: Dp = 28.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) Color(0xFFDDE2E7) else Color(0xFF15181B)

    /**
     * La orden del día clavada en la plancha: la banda de esmalte negro a la izquierda con sus galones de latón apilados (dos o
     * tres, según el aviso) y el filete de latón que la separa del texto.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawPath(ochavado(Rect(0f, 0f, w, h), 4 * u), plancha(oscuro))
        drawLine(filete(oscuro).copy(alpha = 0.35f), Offset(24 * u, h - 1 * u), Offset(w - 6 * u, h - 1 * u), 0.7f * u)
        val banda = 20 * u
        drawPath(Path().apply { moveTo(4 * u, 0f); lineTo(banda, 0f); lineTo(banda, h); lineTo(4 * u, h); lineTo(0f, h - 4 * u); lineTo(0f, 4 * u); close() }, ESMALTE)
        drawLine(placa(LATON, oscuro), Offset(banda, 0f), Offset(banda, h), 1.2f * u)
        val cuantos = 2 + e.semilla % 2
        drawPath(galones(Offset(banda / 2, 12 * u), 10 * u, 4.5f * u, cuantos, 3.4f * u, Offset(0f, 1f)), placa(LATON_LUZ, oscuro),
            style = Stroke(1.3f * u, join = StrokeJoin.Miter))
        if (h > 34 * u) drawCircle(placa(LATON, oscuro), 1.1f * u, Offset(banda / 2, h - 6 * u))
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFEFE3C2) else Color(0xFF15181B)
    override fun rotuloCifra(oscuro: Boolean): Color = tintaBotonTexto(oscuro)

    /** La placa de grado: la plancha ochavada con su filete de latón y un remache a cada lado, a media altura. */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val p = ochavado(Rect(0f, 0f, w, h), 5 * u)
        drawPath(p, plancha(oscuro))
        drawPath(p, filete(oscuro).copy(alpha = 0.75f), style = Stroke(1 * u))
        drawLine(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.08f), Offset(5 * u, 1.6f * u), Offset(w - 5 * u, 1.6f * u), 1 * u)
        listOf(Offset(3 * u, h / 2), Offset(w - 3 * u, h / 2)).forEach { drawCircle(filete(oscuro), 1 * u, it) }
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFEFE3C2) else Color(0xFF15181B)
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFF9DA6B0) else Color(0xFF4B525A)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color =
        if (tipo == TipoTarjeta.PENDIENTE) (if (oscuro) Color(0xFFFF6A5E) else Color(0xFFB0140C)) else tintaBotonTexto(oscuro)

    /**
     * Las tarjetas de la orden, sobre la plancha de acero. La cifra de Hoy es la **placa del cuadro de mando**: ochavada solo en
     * dos esquinas opuestas, con un triángulo de latón en cada chaflán y el riel graduado de latón en el canto izquierdo;
     * destacada, el filete se vuelve latón pleno y se enciende el piloto rojo del chaflán de abajo. «Te falta hoy» es el
     * **decreto**: la banda de esmalte negro arriba, con sus tres galones de latón en medio y una luz roja de alerta a cada
     * lado, y el filete rojo alrededor; completo, las luces pasan a latón y el sello del engrane se estampa a la derecha. La
     * noche es la **guardia**: la luna creciente grabada en su aro de latón y la hilera de estrellas, a lo largo del canto de
     * arriba; sellada, el engrane de latón cierra la esquina de la derecha y el filete se dora. Vida es la
     * **placa de vitrina**: la plancha ochavada con la cinta de la condecoración arriba a la derecha y dos remaches al pie. Al
     * tocarlas la plancha se hunde.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val fondo = plancha(oscuro).let { if (e.presionado) mezcla(it, ACERO_LUZ, if (oscuro) 0.16f else 0.12f) else it }
        val laton = placa(LATON, oscuro)
        val alerta = acentoTarjeta(TipoTarjeta.PENDIENTE, oscuro)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val ch = 9 * u
                val p = Path().apply { moveTo(ch, 0f); lineTo(w, 0f); lineTo(w, h - ch); lineTo(w - ch, h); lineTo(0f, h); lineTo(0f, ch); close() }
                drawPath(p, fondo)
                drawPath(p, if (e.destacado) laton else filete(oscuro).copy(alpha = 0.6f), style = Stroke(if (e.destacado) 1.6f * u else 1 * u))
                // Los triángulos de latón en los chaflanes.
                val k = 4.5f * u
                drawPath(Path().apply { moveTo(ch - k + 2 * u, 2 * u); lineTo(ch + 2 * u, 2 * u); lineTo(2 * u, ch + 2 * u); lineTo(2 * u, ch - k + 2 * u); close() }, laton.copy(alpha = 0.8f))
                val pie = Path().apply { moveTo(w - 2 * u, h - ch - 2 * u); lineTo(w - 2 * u, h - ch + k - 2 * u); lineTo(w - ch + k - 2 * u, h - 2 * u); lineTo(w - ch - 2 * u, h - 2 * u); close() }
                drawPath(pie, if (e.destacado) ALERTA else laton.copy(alpha = 0.8f))
                if (e.destacado) drawCircle(ALERTA.copy(alpha = 0.3f), 5 * u, Offset(w - ch / 2 - 1.5f * u, h - ch / 2 - 1.5f * u))
                // El riel graduado del canto izquierdo.
                val paso = maxOf(5 * u, 1f)
                val marcas = Path()
                var y = ch + 6 * u
                var n = 0
                while (y < h - 6 * u && n < 100) { marcas.moveTo(1.5f * u, y); marcas.lineTo((if (n % 3 == 0) 5f else 3.5f) * u, y); y += paso; n++ }
                drawPath(marcas, filete(oscuro).copy(alpha = 0.7f), style = Stroke(0.7f * u))
            }
            TipoTarjeta.PENDIENTE -> {
                val p = ochavado(Rect(0f, 0f, w, h), 6 * u)
                drawPath(p, fondo)
                drawPath(p, if (e.destacado) laton else alerta, style = Stroke(1.6f * u))
                // La banda de esmalte con sus galones y las dos luces.
                val banda = Rect(18 * u, 0f, w - 18 * u, 6 * u)
                if (banda.width > 30 * u) {
                    drawPath(Path().apply {
                        moveTo(banda.left, banda.top); lineTo(banda.right, banda.top); lineTo(banda.right - 4 * u, banda.bottom); lineTo(banda.left + 4 * u, banda.bottom); close()
                    }, ESMALTE)
                    drawPath(galones(Offset(banda.center.x - 3.4f * u, banda.center.y), 3.6f * u, 4 * u, 3, 3.4f * u, Offset(1f, 0f)), placa(LATON_LUZ, oscuro),
                        style = Stroke(1 * u, join = StrokeJoin.Miter))
                    listOf(banda.left + 8 * u, banda.right - 8 * u).forEach { x ->
                        val c = Offset(x, banda.center.y)
                        val luz = if (e.destacado) placa(LATON_LUZ, oscuro) else ALERTA
                        drawCircle(luz.copy(alpha = 0.3f), 2.8f * u, c)
                        drawCircle(luz, 1.6f * u, c)
                    }
                }
                // Completo: el sello del engrane a la derecha.
                if (e.destacado) {
                    val r = minOf(h * 0.3f, 20 * u)
                    val c = Offset(w - r - 14 * u, h / 2 + 2 * u)
                    drawPath(engrane(c, r, 12), laton.copy(alpha = 0.85f))
                    drawCircle(fondo, r * 0.55f, c)
                    drawPath(galones(Offset(c.x, c.y + r * 0.12f), r * 0.6f, r * 0.3f, 2, r * 0.22f, Offset(0f, -1f)), laton, style = Stroke(1.2f * u, join = StrokeJoin.Miter))
                }
            }
            TipoTarjeta.NOCHE -> {
                val p = ochavado(Rect(0f, 0f, w, h), 5 * u)
                drawPath(p, fondo)
                drawPath(p, if (e.destacado) laton else filete(oscuro).copy(alpha = 0.6f), style = Stroke(if (e.destacado) 1.6f * u else 1 * u))
                // Las estrellas del canto de arriba.
                val paso = maxOf(16 * u, 1f)
                var x = 44 * u
                var n = 0
                while (x < w - 30 * u && n < 60) {
                    drawPath(estrella(Offset(x, (4f + 2f * azar(e.semilla + 3, n)) * u), (1.2f + 0.8f * azar(e.semilla + 7, n)) * u, 4, 0.4f), laton.copy(alpha = 0.55f))
                    x += paso; n++
                }
                // La luna creciente en su aro, en el canto de arriba.
                val c = Offset(30 * u, 6.5f * u)
                drawCircle(laton.copy(alpha = 0.7f), 4.4f * u, c, style = Stroke(0.8f * u))
                drawCircle(placa(LATON_LUZ, oscuro), 3.2f * u, c)
                drawCircle(fondo, 2.8f * u, Offset(c.x + 1.5f * u, c.y - 0.8f * u))
                // Sellada: el engrane de latón cerrando la esquina de la derecha.
                if (e.destacado) {
                    val g = Offset(w - 10 * u, 10 * u)
                    drawPath(engrane(g, 6.5f * u, 10), laton)
                    drawCircle(ESMALTE, 2.2f * u, g)
                    drawCircle(placa(LATON_LUZ, oscuro), 1 * u, g)
                }
            }
            TipoTarjeta.VIDA -> {
                val p = ochavado(Rect(0f, 0f, w, h), 4 * u)
                drawPath(p, fondo)
                drawPath(p, filete(oscuro).copy(alpha = 0.6f), style = Stroke(1 * u))
                // La cinta de la condecoración, arriba a la derecha: tres franjas y la punta cortada.
                val x0 = w - 22 * u
                val franjas = listOf(t(ALERTA, oscuro), placa(LATON_LUZ, oscuro), t(ACERO, oscuro), placa(LATON_LUZ, oscuro), t(ALERTA, oscuro))
                val ancho = 2 * u
                clipPath(Path().apply { moveTo(x0, 0f); lineTo(x0 + 5 * ancho, 0f); lineTo(x0 + 5 * ancho, 10 * u); lineTo(x0 + 2.5f * ancho, 8 * u); lineTo(x0, 10 * u); close() }) {
                    franjas.forEachIndexed { i, c -> drawRect(c, Offset(x0 + i * ancho, 0f), Size(ancho, 10 * u)) }
                }
                // Los dos remaches del pie.
                listOf(Offset(6 * u, h - 5 * u), Offset(w - 6 * u, h - 5 * u)).forEach { drawCircle(filete(oscuro), 1.1f * u, it) }
            }
        }
    }
}

/**
 * Laboratorio: las piezas comunes del laboratorio. El campo es el portaobjetos sobre la platina, como su buscador: la
 * platina de metal con sus dos pinzas, el vidrio con su extremo esmerilado a la izquierda y la escala micrométrica grabada a
 * lo largo del canto de abajo.
 */
object ComunLaboratorio : DisenoComun {
    private val GRIS = Color(0xFFAFB9C1)
    private val GRIS_OSC = Color(0xFF6B7781)
    private val CIAN = Color(0xFF1FB5C9)
    private val ROJO = Color(0xFFD8312A)
    private val TINTA = Color(0xFF12202B)

    /** El color en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0E12), 0.38f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 17.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFE4F0F4) else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF8A9CA6) else Color(0xFF63707A)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF55D2E2) else Color(0xFF0B7180)
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFFF7A6E) else Color(0xFFC0261F)

    /**
     * El portaobjetos: la platina (gris en claro, grafito en oscuro) con sus dos pinzas arriba; el vidrio claro, o ahumado en
     * oscuro, con su brillo, el extremo esmerilado con dos rayas de lápiz y la escala micrométrica abajo; con varios renglones,
     * una línea punteada tenue por renglón. Con el foco el canto del vidrio se tiñe de cian y se enciende la luz de la platina
     * debajo; con un error el canto va en rojo y aparece el rombo de riesgo en el esmerilado.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun ap(x: Color) = if (e.habilitado) x else mezcla(x, Color(0xFF8C9296), 0.35f)
        // La platina.
        drawRoundRect(ap(if (oscuro) Color(0xFF20262C) else GRIS), Offset.Zero, size, CornerRadius(5 * u))
        drawRoundRect(Color.Black.copy(alpha = 0.2f), Offset.Zero, size, CornerRadius(5 * u), style = Stroke(1 * u))
        // El vidrio.
        val vi = Rect(3 * u, 6 * u, w - 3 * u, h - 5 * u)
        val vidrio = ap(if (oscuro) Color(0xFF16222A) else Color(0xFFF4FAFB))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(vi.left + 0.8f * u, vi.top + 1.4f * u), vi.size)
        drawRect(vidrio, vi.topLeft, vi.size)
        val es = Rect(vi.left, vi.top, vi.left + 9 * u, vi.bottom)
        if (e.enfocado && e.habilitado) drawRect(CIAN.copy(alpha = if (oscuro) 0.1f else 0.07f), Offset(es.right, vi.top), Size(vi.right - es.right, vi.height))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.14f else 0.9f), Offset(vi.left + 2 * u, vi.top + 1.4f * u), Offset(vi.right - 2 * u, vi.top + 1.4f * u), 0.8f * u)
        // El extremo esmerilado, con sus granos y dos rayas de lápiz.
        drawRect(ap(if (oscuro) Color(0xFF2C3840) else Color(0xFFE2E8EA)), es.topLeft, es.size)
        val granos = (es.height / u * 0.8f).toInt().coerceIn(10, 120)
        for (i in 0 until granos) drawCircle(Color.Black.copy(alpha = 0.06f), 0.6f * u, Offset(es.left + es.width * azar(61 + e.semilla, i), es.top + es.height * azar(67 + e.semilla, i)))
        val lapiz = if (oscuro) Color(0xFF8C98A0) else GRIS_OSC
        val ym = es.top + minOf(es.height / 2, 14 * u)
        drawLine(lapiz.copy(alpha = 0.7f), Offset(es.left + 2 * u, ym - 2.5f * u), Offset(es.right - 2 * u, ym - 3 * u), 0.9f * u, StrokeCap.Round)
        drawLine(lapiz.copy(alpha = 0.7f), Offset(es.left + 2 * u, ym + 1 * u), Offset(es.right - 3.5f * u, ym + 1 * u), 0.9f * u, StrokeCap.Round)
        if (e.error) {
            val o = Offset(es.center.x, minOf(ym + 9 * u, es.bottom - 4 * u)); val d = 3.2f * u
            drawPath(Path().apply { moveTo(o.x, o.y - d); lineTo(o.x + d, o.y); lineTo(o.x, o.y + d); lineTo(o.x - d, o.y); close() }, errorCampo(oscuro), style = Stroke(1 * u))
        }
        // Las líneas de los renglones.
        if (e.multilinea) renglones(margenCampoY) { _, y ->
            drawLine(CIAN.copy(alpha = if (oscuro) 0.2f else 0.28f), Offset(es.right + 5 * u, y), Offset(vi.right - 5 * u, y), 0.7f * u,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(1 * u, 2 * u)))
        }
        // La escala micrométrica del canto de abajo.
        val paso = maxOf(2.4f * u, 1f)
        var x = es.right + 3 * u
        var k = 0
        val marca = (if (oscuro) Color(0xFF8C98A0) else GRIS_OSC).copy(alpha = 0.45f)
        while (x < vi.right - 3 * u && k < 2000) {
            val largo = if (k % 10 == 0) 3 * u else if (k % 5 == 0) 2.2f * u else 1.3f * u
            drawLine(marca, Offset(x, vi.bottom - 0.6f * u), Offset(x, vi.bottom - 0.6f * u - largo), 0.5f * u)
            x += paso; k++
        }
        // El canto del vidrio.
        val canto = when {
            e.error -> errorCampo(oscuro)
            e.enfocado && e.habilitado -> CIAN
            else -> Color(0xFF9FB4BE).let { if (oscuro) mezcla(it, Color(0xFF16222A), 0.4f) else it }
        }
        drawRect(canto, vi.topLeft, vi.size, style = Stroke((if (e.enfocado || e.error) 1.6f else 1f) * u))
        // Las pinzas de la platina.
        listOf(es.right + 2 * u, vi.right - 9 * u).forEach { xp ->
            drawRoundRect(Color.Black.copy(alpha = 0.25f), Offset(xp - 2.5f * u + 0.8f * u, 0f), Size(5 * u, 9.5f * u), CornerRadius(1.8f * u))
            drawRoundRect(ap(if (oscuro) Color(0xFF4A5560) else Color(0xFF8C98A2)), Offset(xp - 2.5f * u, 0f), Size(5 * u, 9 * u), CornerRadius(1.8f * u))
            drawCircle(Color.White.copy(alpha = 0.35f), 1 * u, Offset(xp, 2.6f * u))
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * La línea de la escala: un filo gris de lado a lado y, al centro, un tramo de escala micrométrica (una marca cada 3 dp,
     * larga cada cinco, en un solo trazo) con su punto cian de referencia. Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2 + 1.5f * u
        val a = if (e.tenue) 0.5f else 1f
        // Más marcado que una raya de pelo: en claro, sobre el papel azulado, al 50 % casi no se veía.
        val gris = (if (oscuro) Color(0xFF8C98A0) else GRIS_OSC).copy(alpha = (if (oscuro) 0.6f else 0.75f) * a)
        drawLine(gris, Offset(0f, y), Offset(w, y), 1f * u)
        // La escala del centro.
        val cx = w / 2
        val paso = maxOf(3 * u, 1f)
        val n = ((minOf(48 * u, w * 0.3f) / paso).toInt() / 2).coerceIn(1, 10)
        val escala = Path()
        for (k in -n..n) {
            val x = cx + k * paso
            escala.moveTo(x, y); escala.lineTo(x, y - (if (k % 5 == 0) 3.4f else 1.8f) * u)
        }
        drawPath(escala, gris, style = Stroke(0.6f * u))
        drawCircle((if (oscuro) Color(0xFF3CD2E5) else CIAN).copy(alpha = 0.9f * a), 1.2f * u, Offset(cx, y - 4.8f * u))
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFF3CD2E5) else Color(0xFF0B7180)

    /**
     * La línea de cota del dibujo técnico, bajo el texto: la raya con sus dos topes y sus dos flechas. Al presionarlo la cota
     * se marca y el botón se tiñe de cian, redondeado como los frascos del tema; apagado, la cota es gris y sin flechas.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val y = h - 10 * u
        val izq = 6 * u; val der = w - 6 * u
        val tinta = if (e.habilitado) tintaBotonTexto(oscuro) else Color(0xFF8C9296)
        if (e.presionado && e.habilitado) drawRoundRect(tinta.copy(alpha = if (oscuro) 0.12f else 0.09f), Offset(3 * u, 6 * u), Size(w - 6 * u, h - 12 * u), CornerRadius(10 * u))
        val c = tinta.copy(alpha = when { !e.habilitado -> 0.35f; e.presionado -> 0.95f; else -> 0.5f })
        val g = (if (e.presionado) 1.1f else 0.8f) * u
        // La raya de cota con sus topes.
        drawLine(c, Offset(izq, y), Offset(der, y), g)
        listOf(izq, der).forEach { drawLine(c, Offset(it, y - 2.6f * u), Offset(it, y + 2.6f * u), g) }
        // Las flechas, hacia los topes.
        if (e.habilitado) {
            val f = 2.8f * u
            drawPath(Path().apply {
                moveTo(izq, y); lineTo(izq + f, y - 1.2f * u); lineTo(izq + f, y + 1.2f * u); close()
                moveTo(der, y); lineTo(der - f, y - 1.2f * u); lineTo(der - f, y + 1.2f * u); close()
            }, c)
        }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El papel de las placas del laboratorio: blanco de cuaderno en claro, vidrio ahumado en oscuro. */
    private fun hoja(oscuro: Boolean): Color = if (oscuro) Color(0xFF101823) else Color(0xFFFCFDFE)

    /** El gris de los filos y las escalas. */
    private fun filo(oscuro: Boolean): Color = if (oscuro) Color(0xFF8C98A0) else GRIS_OSC

    override val margenFila: Dp = 14.dp

    /**
     * El renglón del cuaderno de laboratorio: la raya gris fina al pie, con tres marcas de escala donde empieza, y el punto cian
     * de la muestra a la izquierda (vacío; con detalle, con su anillo). Al tocarla la fila se tiñe de cian, redondeada como los
     * frascos, y el punto se llena.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cian = tintaBotonTexto(oscuro)
        if (e.presionado) drawRoundRect(cian.copy(alpha = if (oscuro) 0.12f else 0.08f), Offset(2 * u, 2 * u), Size(w - 4 * u, h - 4 * u), CornerRadius(12 * u))
        val y = h - 0.6f * u
        val gris = filo(oscuro).copy(alpha = 0.32f)
        drawLine(gris, Offset(10 * u, y), Offset(w, y), 0.6f * u)
        drawPath(Path().apply { for (k in 0 until 3) { moveTo(10 * u + k * 3 * u, y); lineTo(10 * u + k * 3 * u, y - (if (k == 0) 3f else 1.8f) * u) } }, gris, style = Stroke(0.6f * u))
        val p = Offset(6 * u, h / 2)
        if (e.presionado) drawCircle(cian, 1.8f * u, p) else drawCircle(cian.copy(alpha = 0.55f), 1.6f * u, p, style = Stroke(0.8f * u))
        if (e.conDetalle) drawCircle(cian.copy(alpha = 0.3f), 3.4f * u, p, style = Stroke(0.6f * u))
    }

    /** La flecha del vector: el trazo cian con su punta llena. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        val cian = tintaBotonTexto(oscuro)
        drawLine(cian, Offset(c.x - 6 * u, c.y), Offset(c.x + 2 * u, c.y), 1.3f * u, StrokeCap.Round)
        drawPath(Path().apply { moveTo(c.x + 6.5f * u, c.y); lineTo(c.x + 1 * u, c.y - 3.4f * u); lineTo(c.x + 1 * u, c.y + 3.4f * u); close() }, cian)
    }

    /** El asterisco de «significativo», en estrella de seis puntas: lleno en cian si está fijo, en contorno gris si no. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        val p = estrella(c, 8.5f * u, 6, 0.48f)
        if (fija) drawPath(p, tintaBotonTexto(oscuro))
        else drawPath(p, filo(oscuro).copy(alpha = 0.6f), style = Stroke(1 * u, join = StrokeJoin.Round))
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) Color(0xFF97A8B9) else Color(0xFF4D5D6B)

    /**
     * La hoja de papel milimetrado sin datos: la cuadrícula fina cada 10 dp (más marcada cada cinco), en un recuadro de
     * esquinas redondas con su filo gris.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val radio = CornerRadius(8 * u)
        drawRoundRect(hoja(oscuro), Offset.Zero, size, radio)
        val paso = maxOf(10 * u, 1f)
        val fina = Path(); val gruesa = Path()
        var x = paso; var i = 1
        while (x < w && i < 200) { (if (i % 5 == 0) gruesa else fina).apply { moveTo(x, 1 * u); lineTo(x, h - 1 * u) }; x += paso; i++ }
        var y = paso; var j = 1
        while (y < h && j < 200) { (if (j % 5 == 0) gruesa else fina).apply { moveTo(1 * u, y); lineTo(w - 1 * u, y) }; y += paso; j++ }
        drawPath(fina, CIAN.copy(alpha = if (oscuro) 0.07f else 0.09f), style = Stroke(0.5f * u))
        drawPath(gruesa, CIAN.copy(alpha = if (oscuro) 0.14f else 0.18f), style = Stroke(0.7f * u))
        drawRoundRect(filo(oscuro).copy(alpha = 0.45f), Offset.Zero, size, radio, style = Stroke(0.8f * u))
    }

    /**
     * El matraz de Erlenmeyer limpio y vacío, un poco inclinado: el cuello con su boca, el cuerpo cónico de fondo redondeado
     * con el vidrio apenas teñido, las marcas de graduación y una sola gota que queda en el borde.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val l = tintaVacio(oscuro).copy(alpha = 0.9f)
        val cian = tintaBotonTexto(oscuro)
        val tr = Stroke(1.3f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        rotate(-10f, Offset(f(0.5f), f(0.55f))) {
            val matraz = Path().apply {
                moveTo(f(0.42f), f(0.1f)); lineTo(f(0.42f), f(0.38f)); lineTo(f(0.18f), f(0.78f))
                quadraticTo(f(0.13f), f(0.9f), f(0.26f), f(0.9f)); lineTo(f(0.74f), f(0.9f))
                quadraticTo(f(0.87f), f(0.9f), f(0.82f), f(0.78f)); lineTo(f(0.58f), f(0.38f)); lineTo(f(0.58f), f(0.1f))
            }
            drawPath(matraz, cian.copy(alpha = 0.08f))
            drawPath(matraz, l, style = tr)
            drawLine(l, Offset(f(0.37f), f(0.1f)), Offset(f(0.63f), f(0.1f)), tr.width, StrokeCap.Round)
            // Las marcas de graduación y el brillo del vidrio.
            listOf(0.56f, 0.66f, 0.76f).forEachIndexed { k, y -> drawLine(l.copy(alpha = 0.6f), Offset(f(0.62f), f(y)), Offset(f(if (k == 1) 0.72f else 0.68f), f(y)), 0.8f * u) }
            drawLine(Color.White.copy(alpha = if (oscuro) 0.25f else 0.8f), Offset(f(0.3f), f(0.72f)), Offset(f(0.38f), f(0.58f)), 1.2f * u, StrokeCap.Round)
            // La gota del borde.
            drawPath(Path().apply { moveTo(f(0.62f), f(0.12f)); quadraticTo(f(0.68f), f(0.2f), f(0.65f), f(0.23f)); quadraticTo(f(0.6f), f(0.24f), f(0.61f), f(0.19f)); close() }, cian)
        }
        drawLine(l.copy(alpha = 0.35f), Offset(f(0.12f), f(0.97f)), Offset(f(0.88f), f(0.97f)), 0.8f * u, StrokeCap.Round)
    }

    override val margenAviso: Dp = 30.dp
    override fun tintaAviso(oscuro: Boolean): Color = tintaCampo(oscuro)

    /**
     * La nota del protocolo: el recuadro con la raya cian a la izquierda y, arriba, su marca: la «i» de información en su
     * círculo o, según el aviso, el rombo rojo de riesgo con su admiración.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRoundRect(if (oscuro) Color(0xFF0F1A24) else Color(0xFFE2EFF3), Offset.Zero, size, CornerRadius(6 * u))
        drawRoundRect(tintaBotonTexto(oscuro), Offset(3 * u, 4 * u), Size(2 * u, h - 8 * u), CornerRadius(1 * u))
        val c = Offset(17 * u, 15 * u)
        if (e.semilla % 2 == 0) {
            val cian = tintaBotonTexto(oscuro)
            drawCircle(cian, 6 * u, c, style = Stroke(1.2f * u))
            drawCircle(cian, 0.9f * u, Offset(c.x, c.y - 2.8f * u))
            drawLine(cian, Offset(c.x, c.y - 0.8f * u), Offset(c.x, c.y + 3.4f * u), 1.4f * u, StrokeCap.Round)
        } else {
            val rojo = errorCampo(oscuro)
            val d = 6.5f * u
            drawPath(Path().apply { moveTo(c.x, c.y - d); lineTo(c.x + d, c.y); lineTo(c.x, c.y + d); lineTo(c.x - d, c.y); close() }, rojo, style = Stroke(1.2f * u, join = StrokeJoin.Miter))
            drawLine(rojo, Offset(c.x, c.y - 2.8f * u), Offset(c.x, c.y + 0.8f * u), 1.3f * u, StrokeCap.Round)
            drawCircle(rojo, 0.8f * u, Offset(c.x, c.y + 2.8f * u))
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = tintaCampo(oscuro)
    override fun rotuloCifra(oscuro: Boolean): Color = rotuloCampo(oscuro)

    /**
     * La lectura del instrumento: el recuadro de esquinas redondas, como los frascos, con su filo y una escala micrométrica a
     * lo largo del canto de arriba (un solo trazo, larga cada cinco marcas).
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val radio = CornerRadius(8 * u)
        drawRoundRect(hoja(oscuro), Offset.Zero, size, radio)
        drawRoundRect(if (oscuro) Color(0xFF263447) else Color(0xFFCBD6DF), Offset.Zero, size, radio, style = Stroke(1 * u))
        val paso = maxOf(3 * u, 1f)
        val escala = Path()
        var x = 9 * u; var k = 0
        while (x < w - 9 * u && k < 300) { escala.moveTo(x, 0.5f * u); escala.lineTo(x, (if (k % 5 == 0) 3.6f else 2f) * u); x += paso; k++ }
        drawPath(escala, filo(oscuro).copy(alpha = 0.5f), style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = tintaCampo(oscuro)
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFF97A8B9) else Color(0xFF4D5D6B)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color =
        if (tipo == TipoTarjeta.PENDIENTE) errorCampo(oscuro) else rotuloCampo(oscuro)

    /**
     * Las tarjetas del laboratorio, sobre la hoja blanca (o el vidrio ahumado, en oscuro). La cifra de Hoy es la **etiqueta de
     * la muestra**: la tapa de color del tubo en el canto izquierdo y las cruces de registro en las otras tres esquinas;
     * destacada, la tapa se vuelve roja y un círculo rojo encierra la cruz de arriba. «Te falta hoy» es el **protocolo en el
     * portapapeles**: el tablero gris alrededor de la hoja, la pinza de metal arriba a la derecha y la franja roja de «urgente»
     * a la izquierda; completo, la franja pasa a cian y se estampa el círculo con su visto bueno. La noche es la **tabla de las
     * fases de la luna**: una hilera de lunas a lo largo del canto de arriba, de nueva a llena; sellada, la luna llena del
     * centro queda marcada con su anillo y el filo se pinta de cian. Vida es la **libreta del laboratorio**: la columna de
     * agujeros de la espiral a la izquierda y la etiqueta numerada arriba a la derecha. Al tocarlas la hoja se tiñe de cian.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val radio = CornerRadius(6 * u)
        val fondo = hoja(oscuro).let { if (e.presionado) mezcla(it, CIAN, if (oscuro) 0.12f else 0.08f) else it }
        val canto = if (oscuro) Color(0xFF263447) else Color(0xFFCBD6DF)
        val rojo = errorCampo(oscuro)
        val cian = rotuloCampo(oscuro)
        fun DrawScope.cruz(c: Offset, l: Float, color: Color) {
            drawLine(color, Offset(c.x - l, c.y), Offset(c.x + l, c.y), 0.7f * u)
            drawLine(color, Offset(c.x, c.y - l), Offset(c.x, c.y + l), 0.7f * u)
        }
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                drawRoundRect(fondo, Offset.Zero, size, radio)
                drawRoundRect(if (e.destacado) rojo.copy(alpha = 0.7f) else canto, Offset.Zero, size, radio, style = Stroke(1 * u))
                // La tapa del tubo, en el canto izquierdo.
                val tapa = if (e.destacado) rojo else cian
                clipPath(Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), radio)) }) {
                    drawRect(tapa, Offset.Zero, Size(4 * u, h))
                    listOf(0.3f, 0.5f, 0.7f).forEach { f -> drawLine(Color.Black.copy(alpha = 0.25f), Offset(0f, h * f), Offset(4 * u, h * f), 0.6f * u) }
                }
                // Las cruces de registro.
                val gris = filo(oscuro).copy(alpha = 0.6f)
                cruz(Offset(w - 6 * u, 6 * u), 2.5f * u, if (e.destacado) rojo else gris)
                cruz(Offset(w - 6 * u, h - 6 * u), 2.5f * u, gris)
                cruz(Offset(10 * u, h - 6 * u), 2.5f * u, gris)
                if (e.destacado) drawCircle(rojo, 4 * u, Offset(w - 6 * u, 6 * u), style = Stroke(0.9f * u))
            }
            TipoTarjeta.PENDIENTE -> {
                // El tablero del portapapeles y la hoja encima.
                val tablero = t(if (oscuro) GRIS_OSC else GRIS, oscuro)
                drawRoundRect(tablero, Offset.Zero, size, radio)
                val b = 3 * u
                drawRoundRect(fondo, Offset(b, b), Size(w - 2 * b, h - 2 * b), CornerRadius(3 * u))
                // La franja de «urgente» (o la de visto bueno), a la izquierda de la hoja.
                drawRect(if (e.destacado) cian else rojo, Offset(b, b + 3 * u), Size(3 * u, h - 2 * b - 6 * u))
                // La pinza de metal arriba a la derecha.
                val px = w - 34 * u
                drawRoundRect(t(GRIS_OSC, oscuro), Offset(px, 0f), Size(22 * u, 6 * u), CornerRadius(1.5f * u))
                drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.15f else 0.5f), Offset(px + 2 * u, 1 * u), Size(18 * u, 1.2f * u), CornerRadius(0.6f * u))
                drawCircle(tablero, 1.4f * u, Offset(px + 11 * u, 3.4f * u))
                // Completo: el círculo con su visto bueno, a la derecha.
                if (e.destacado) {
                    val r = minOf(h * 0.28f, 18 * u)
                    val c = Offset(w - r - 14 * u, h / 2 + 2 * u)
                    drawCircle(cian.copy(alpha = 0.6f), r, c, style = Stroke(1.4f * u))
                    drawPath(Path().apply { moveTo(c.x - r * 0.45f, c.y); lineTo(c.x - r * 0.1f, c.y + r * 0.35f); lineTo(c.x + r * 0.5f, c.y - r * 0.35f) }, cian.copy(alpha = 0.7f),
                        style = Stroke(1.8f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
                }
            }
            TipoTarjeta.NOCHE -> {
                drawRoundRect(fondo, Offset.Zero, size, radio)
                drawRoundRect(if (e.destacado) cian else canto, Offset.Zero, size, radio, style = Stroke(if (e.destacado) 1.4f * u else 1 * u))
                // Las fases de la luna, a lo largo del canto de arriba: la luz y la sombra que la va tapando.
                val paso = 13 * u
                val n = ((w * 0.5f) / paso).toInt().coerceIn(3, 9)
                val x0 = w - 14 * u - (n - 1) * paso
                val r = 2.8f * u
                val luz = if (oscuro) Color(0xFFDDE7EE) else Color(0xFF8C98A0)
                for (i in 0 until n) {
                    val c = Offset(x0 + i * paso, 7 * u)
                    val f = i / (n - 1).toFloat()
                    drawCircle(luz.copy(alpha = 0.25f), r, c, style = Stroke(0.5f * u))
                    if (f > 0f) {
                        drawCircle(luz, r, c)
                        if (f < 1f) drawCircle(fondo, r, Offset(c.x - 2 * r * f, c.y))
                    }
                }
                if (e.destacado) drawCircle(cian, r + 2 * u, Offset(x0 + (n - 1) * paso, 7 * u), style = Stroke(1 * u))
                // La escala de horas del canto de abajo.
                val marcas = Path()
                val pasoE = maxOf(6 * u, 1f)
                var x = 8 * u
                var k = 0
                while (x < w - 8 * u && k < 300) { marcas.moveTo(x, h - 0.5f * u); marcas.lineTo(x, h - (if (k % 4 == 0) 3.5f else 2f) * u); x += pasoE; k++ }
                drawPath(marcas, filo(oscuro).copy(alpha = 0.5f), style = Stroke(0.6f * u))
            }
            TipoTarjeta.VIDA -> {
                drawRoundRect(fondo, Offset.Zero, size, radio)
                drawRoundRect(canto, Offset.Zero, size, radio, style = Stroke(1 * u))
                // Los agujeros de la espiral.
                val paso = maxOf(9 * u, 1f)
                var y = 8 * u
                var n = 0
                while (y < h - 5 * u && n < 100) {
                    drawCircle(if (oscuro) Color(0xFF05080C) else Color(0xFFDDE4EA), 1.6f * u, Offset(5 * u, y))
                    drawCircle(filo(oscuro).copy(alpha = 0.6f), 1.6f * u, Offset(5 * u, y), style = Stroke(0.5f * u))
                    y += paso; n++
                }
                // La etiqueta numerada arriba a la derecha, con sus rayas de número.
                val r = Rect(w - 26 * u, 3 * u, w - 4 * u, 9 * u)
                drawRoundRect(cian.copy(alpha = 0.7f), r.topLeft, r.size, CornerRadius(1.5f * u), style = Stroke(0.8f * u))
                drawLine(cian.copy(alpha = 0.6f), Offset(r.left + 3 * u, r.center.y), Offset(r.right - 3 * u, r.center.y), 1 * u,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.5f * u, 1.2f * u)))
            }
        }
    }
}

/**
 * Colonos: las piezas comunes del almacén. El campo es la pizarra del almacén, como su buscador: la pizarra en su marco de
 * pino con sus clavos y, abajo, el canto ancho donde descansa la tiza.
 */
object ComunColonos : DisenoComun {
    private val PINO = Color(0xFFC49C66)
    private val PINO_LUZ = Color(0xFFDAB884)
    private val PINO_OSC = Color(0xFF8C6B41)
    private val VETA = Color(0xFF6E5030)
    private val CLAVO = Color(0xFF3A3530)
    private val TIZA = Color(0xFFF2EEE2)

    /** El color en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C08), 0.34f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 15.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = TIZA
    override fun pistaCampo(oscuro: Boolean): Color = Color(0xFFA3A69C)
    override fun rotuloCampo(oscuro: Boolean): Color = Color(0xFFE8C877)
    override fun errorCampo(oscuro: Boolean): Color = Color(0xFFEE8A70)

    /**
     * La pizarra: el marco de pino con sus vetas, sus clavos y el canto de abajo más ancho, con el polvo de tiza; la pizarra
     * con los borrones de lo de antes (y una raya de tiza tenue por renglón si son varios). Con el foco aparece el trozo de tiza
     * en el canto y el borde de la pizarra se aclara; con un error, la tiza es de almagre y el borde va en ese rojo; apagada, la
     * pizarra queda velada de polvo.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro).let { if (e.habilitado) it else mezcla(it, Color(0xFF8A8478), 0.35f) }
        // El marco de pino.
        drawRect(c(PINO), Offset.Zero, size)
        vetas(Rect(0f, 0f, w, h), c(VETA).copy(alpha = 0.32f), 41 + e.semilla % 7, densidad = 0.6f)
        val canto = 7.5f * u
        val pz = Rect(5 * u, 5 * u, w - 5 * u, h - canto)
        // El canto de abajo: su luz arriba y su sombra al pie.
        drawRect(c(PINO_LUZ), Offset(0f, pz.bottom + 1 * u), Size(w, 1.2f * u))
        drawRect(c(PINO_OSC), Offset(0f, h - 1.2f * u), Size(w, 1.2f * u))
        // La pizarra.
        drawRect(Color.Black.copy(alpha = 0.35f), pz.inflate(1 * u).topLeft, pz.inflate(1 * u).size)
        val pizarra = if (oscuro) Color(0xFF1E2522) else Color(0xFF2F3A35)
        drawRect(pizarra, pz.topLeft, pz.size)
        // Los borrones de lo que se escribió antes.
        val borrones = (pz.width / (70 * u)).toInt().coerceIn(2, 10)
        for (i in 0 until borrones) {
            val bw = pz.width * (0.12f + 0.08f * azar(e.semilla + 83, i))
            drawOval(TIZA.copy(alpha = 0.022f), Offset(pz.left + (pz.width - bw) * (i + azar(e.semilla + 89, i)) / borrones, pz.top + pz.height * 0.15f * azar(e.semilla + 97, i)),
                Size(bw, minOf(pz.height * 0.55f, 22 * u)))
        }
        if (e.multilinea) renglones(margenCampoY) { _, y -> drawLine(TIZA.copy(alpha = 0.1f), Offset(pz.left + 6 * u, y), Offset(pz.right - 6 * u, y), 0.8f * u, StrokeCap.Round) }
        if (!e.habilitado) drawRect(TIZA.copy(alpha = 0.08f), pz.topLeft, pz.size)
        val roja = errorCampo(oscuro)
        when {
            e.error -> drawRect(roja.copy(alpha = 0.85f), pz.topLeft, pz.size, style = Stroke(1.3f * u))
            e.enfocado -> drawRect(TIZA.copy(alpha = 0.6f), pz.topLeft, pz.size, style = Stroke(1.2f * u))
        }
        // El polvo de tiza en el canto.
        for (i in 0 until 9) drawCircle(TIZA.copy(alpha = 0.18f), (0.5f + 0.6f * azar(e.semilla + 101, i)) * u, Offset(pz.left + pz.width * azar(e.semilla + 103, i), pz.bottom + 3 * u + 2 * u * azar(e.semilla + 107, i)))
        // El trozo de tiza (o de almagre), con el foco o con el error.
        if ((e.enfocado || e.error) && e.habilitado) {
            val ti = Rect(w - 38 * u, pz.bottom + 1.6f * u, w - 23 * u, pz.bottom + 5.6f * u)
            drawRoundRect(Color.Black.copy(alpha = 0.2f), Offset(ti.left + 0.6f * u, ti.top + 0.8f * u), ti.size, CornerRadius(2 * u))
            drawRoundRect(if (e.error) roja else Color(0xFFF7F4EA), ti.topLeft, ti.size, CornerRadius(2 * u))
            drawRoundRect(Color.Black.copy(alpha = 0.15f), Offset(ti.left, ti.bottom - 1.2f * u), Size(ti.width, 1.2f * u), CornerRadius(1 * u))
        }
        // Los clavos de las esquinas.
        listOf(Offset(2.5f * u, 2.5f * u), Offset(w - 2.5f * u, 2.5f * u), Offset(2.5f * u, h - 3.5f * u), Offset(w - 2.5f * u, h - 3.5f * u)).forEach {
            drawCircle(c(CLAVO), 1.1f * u, it)
            drawCircle(Color.White.copy(alpha = 0.25f), 0.4f * u, Offset(it.x - 0.3f * u, it.y - 0.3f * u))
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * El cordel del almacén: las hebras torcidas en diagonal (todas en un solo trazo) de punta a punta, con el nudo al centro y
     * sus dos cabos colgando. Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val a = if (e.tenue) 0.5f else 1f
        val cuerda = (if (oscuro) Color(0xFFA8865A) else PINO_OSC).copy(alpha = (if (oscuro) 0.6f else 0.7f) * a)
        val cx = w / 2
        val nudo = 5 * u
        // Las hebras, a los dos lados del nudo.
        val paso = maxOf(2.6f * u, 1f)
        val d = 1.1f * u
        val hebras = Path()
        listOf(4 * u to cx - nudo, cx + nudo to w - 4 * u).forEach { (x0, x1) ->
            var x = x0
            var n = 0
            while (x < x1 - 2 * u && n < 200) { hebras.moveTo(x, y + d); hebras.lineTo(x + 2 * u, y - d); x += paso; n++ }
        }
        drawPath(hebras, cuerda, style = Stroke(0.9f * u, cap = StrokeCap.Round))
        // El nudo, con sus dos cabos.
        drawOval(cuerda, Offset(cx - 3.5f * u, y - 2.2f * u), Size(7 * u, 4.4f * u), style = Stroke(1.1f * u))
        drawLine(cuerda, Offset(cx + 1.5f * u, y + 1.8f * u), Offset(cx + 3.5f * u, y + 4.2f * u), 0.9f * u, StrokeCap.Round)
        drawLine(cuerda, Offset(cx - 1.5f * u, y + 1.8f * u), Offset(cx - 3 * u, y + 4.4f * u), 0.9f * u, StrokeCap.Round)
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFE3C27A) else Color(0xFF1F3A5F)

    /**
     * El vale del almacén: el contorno de papel con las cuatro esquinas comidas, en raya de recortar. Al presionarlo el vale se
     * llena de papel de estraza y el contorno se vuelve continuo; apagado, queda un contorno gris muy tenue.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(3 * u, 7 * u, w - 3 * u, h - 7 * u)
        val k = 3.2f * u
        val vale = Path().apply {
            moveTo(r.left + k, r.top); lineTo(r.right - k, r.top); quadraticTo(r.right - k, r.top + k, r.right, r.top + k)
            lineTo(r.right, r.bottom - k); quadraticTo(r.right - k, r.bottom - k, r.right - k, r.bottom)
            lineTo(r.left + k, r.bottom); quadraticTo(r.left + k, r.bottom - k, r.left, r.bottom - k)
            lineTo(r.left, r.top + k); quadraticTo(r.left + k, r.top + k, r.left + k, r.top); close()
        }
        val lleno = e.presionado && e.habilitado
        val tinta = if (!e.habilitado) Color(0xFF8A8478) else if (oscuro) Color(0xFFC9A66A) else PINO_OSC
        if (lleno) drawPath(vale, (if (oscuro) Color(0xFFE3C27A) else PINO_LUZ).copy(alpha = if (oscuro) 0.1f else 0.25f))
        drawPath(vale, tinta.copy(alpha = when { !e.habilitado -> 0.3f; lleno -> 0.95f; else -> 0.55f }),
            style = Stroke((if (lleno) 1.2f else 0.9f) * u, pathEffect = if (lleno) null else PathEffect.dashPathEffect(floatArrayOf(3 * u, 1.6f * u))))
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El papel del almacén: estraza clara en claro; en oscuro, la tabla de pizarra de noche. */
    private fun papelAlmacen(oscuro: Boolean): Color = if (oscuro) Color(0xFF1E2636) else Color(0xFFF8F2E4)

    /** El rojo de las rayas del libro de cuentas. */
    private fun rojoCuentas(oscuro: Boolean): Color = if (oscuro) Color(0xFFE07A62) else Color(0xFFA23B2A)

    /** El latón de las tachuelas. */
    private val TACHUELA = Color(0xFFC9A04A)

    override val margenFila: Dp = 12.dp

    /**
     * El renglón del libro de cuentas: la raya de lápiz al pie y, a la izquierda, la doble raya roja del margen. Al tocarla la
     * fila se llena de papel de estraza; con detalle, la doble raya lleva un punto de tinta donde empieza el renglón.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (e.presionado) drawRect((if (oscuro) Color(0xFFE3C27A) else PINO_LUZ).copy(alpha = if (oscuro) 0.09f else 0.28f))
        drawLine((if (oscuro) Color(0xFFA9B0BC) else Color(0xFF525866)).copy(alpha = if (oscuro) 0.22f else 0.3f), Offset(0f, h - 0.6f * u), Offset(w, h - 0.6f * u), 0.7f * u)
        val rojo = rojoCuentas(oscuro).copy(alpha = if (e.presionado) 0.85f else 0.45f)
        listOf(3 * u, 5.2f * u).forEach { x -> drawLine(rojo, Offset(x, 0f), Offset(x, h), 0.7f * u) }
        if (e.conDetalle) drawCircle(rojo, 0.9f * u, Offset(8 * u, h / 2))
    }

    /** El letrero de tablón en punta, clavado: de pino, con su veta y su clavo. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        val tabla = Path().apply { moveTo(c.x - 8 * u, c.y - 3.5f * u); lineTo(c.x + 3.5f * u, c.y - 3.5f * u); lineTo(c.x + 8 * u, c.y); lineTo(c.x + 3.5f * u, c.y + 3.5f * u); lineTo(c.x - 8 * u, c.y + 3.5f * u); close() }
        drawPath(tabla, t(PINO, oscuro))
        drawLine(t(VETA, oscuro).copy(alpha = 0.5f), Offset(c.x - 7 * u, c.y + 0.8f * u), Offset(c.x + 4 * u, c.y + 0.4f * u), 0.6f * u)
        drawPath(tabla, t(PINO_OSC, oscuro), style = Stroke(0.8f * u, join = StrokeJoin.Round))
        drawCircle(CLAVO, 0.9f * u, Offset(c.x - 5.5f * u, c.y - 1 * u))
    }

    /**
     * La tachuela que clava la nota arriba: fija, la cabeza de latón con su brillo y su sombra en el papel; sin fijar, el
     * contorno de la cabeza con el agujerito, tenue.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        val r = 6.5f * u
        if (fija) {
            drawCircle(Color.Black.copy(alpha = 0.25f), r, Offset(c.x + 1.2f * u, c.y + 1.4f * u))
            drawCircle(if (oscuro) Color(0xFFE3C27A) else TACHUELA, r, c)
            drawCircle(Color(0xFF7A5A2E).copy(alpha = 0.6f), r, c, style = Stroke(0.8f * u))
            drawCircle(Color.White.copy(alpha = 0.55f), r * 0.32f, Offset(c.x - r * 0.35f, c.y - r * 0.35f))
        } else {
            val g = (if (oscuro) Color(0xFFA9B0BC) else Color(0xFF525866)).copy(alpha = 0.55f)
            drawCircle(g, r, c, style = Stroke(1 * u))
            drawCircle(g, 0.9f * u, c)
        }
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) Color(0xFFD8CFBC) else Color(0xFF524634)

    /**
     * La hoja del pedido sin apuntar: el papel con su borde de lápiz, una esquina doblada arriba a la derecha y una tachuela en
     * cada una de las otras tres.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val k = 12 * u
        val hoja = Path().apply { moveTo(0f, 0f); lineTo(w - k, 0f); lineTo(w, k); lineTo(w, h); lineTo(0f, h); close() }
        drawPath(hoja, papelAlmacen(oscuro))
        drawPath(hoja, t(PINO_OSC, oscuro).copy(alpha = 0.45f), style = Stroke(0.8f * u))
        // La esquina doblada.
        val pliegue = Path().apply { moveTo(w - k, 0f); lineTo(w - k, k); lineTo(w, k); close() }
        drawPath(pliegue, t(PINO_LUZ, oscuro).copy(alpha = if (oscuro) 0.5f else 0.9f))
        drawPath(pliegue, t(PINO_OSC, oscuro).copy(alpha = 0.5f), style = Stroke(0.7f * u))
        listOf(Offset(5 * u, 5 * u), Offset(5 * u, h - 5 * u), Offset(w - 5 * u, h - 5 * u)).forEach {
            drawCircle(Color.Black.copy(alpha = 0.2f), 2 * u, Offset(it.x + 0.5f * u, it.y + 0.6f * u))
            drawCircle(if (oscuro) Color(0xFFB8945A) else TACHUELA, 2 * u, it)
            drawCircle(Color.White.copy(alpha = 0.45f), 0.6f * u, Offset(it.x - 0.6f * u, it.y - 0.6f * u))
        }
    }

    /**
     * El cajón de madera abierto y vacío, visto de tres cuartos: el frente de tablas con sus clavos, el costado en sombra, la
     * boca abierta con el fondo a la vista y una sola brizna de paja que quedó dentro.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val borde = t(VETA, oscuro).let { if (oscuro) mezcla(it, Color(0xFFD8CFBC), 0.3f) else it }
        val g = Stroke(0.9f * u, join = StrokeJoin.Round)
        // La boca, con el fondo a la vista.
        val boca = Path().apply { moveTo(f(0.08f), f(0.44f)); lineTo(f(0.26f), f(0.3f)); lineTo(f(0.92f), f(0.3f)); lineTo(f(0.74f), f(0.44f)); close() }
        drawPath(boca, t(Color(0xFF3A2A18), oscuro))
        drawPath(Path().apply { moveTo(f(0.3f), f(0.4f)); quadraticTo(f(0.45f), f(0.33f), f(0.6f), f(0.38f)) }, Color(0xFFE2C46A).copy(alpha = 0.8f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        drawPath(boca, borde, style = g)
        // El costado, en sombra.
        val costado = Path().apply { moveTo(f(0.74f), f(0.44f)); lineTo(f(0.92f), f(0.3f)); lineTo(f(0.92f), f(0.76f)); lineTo(f(0.74f), f(0.9f)); close() }
        drawPath(costado, t(PINO_OSC, oscuro))
        drawPath(costado, borde, style = g)
        // El frente de tablas, con sus clavos.
        drawRect(t(PINO, oscuro), Offset(f(0.08f), f(0.44f)), Size(f(0.66f), f(0.46f)))
        listOf(0.595f, 0.745f).forEach { y -> drawLine(borde, Offset(f(0.08f), f(y)), Offset(f(0.74f), f(y)), 0.7f * u) }
        drawRect(borde, Offset(f(0.08f), f(0.44f)), Size(f(0.66f), f(0.46f)), style = g)
        listOf(0.52f, 0.67f, 0.82f).forEach { y -> listOf(0.13f, 0.69f).forEach { x -> drawCircle(CLAVO, 0.7f * u, Offset(f(x), f(y))) } }
    }

    override val margenAviso: Dp = 31.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) Color(0xFFEEE7D7) else Color(0xFF1F2A3A)

    /**
     * La nota del almacén clavada en el poste: el papel y, a la izquierda, la tira de pino con sus vetas y su clavo; con
     * algunos avisos, la marca roja del libro de cuentas (la cruz de «ojo») junto al clavo.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(papelAlmacen(oscuro))
        drawLine(t(PINO_OSC, oscuro).copy(alpha = 0.35f), Offset(0f, h - 0.6f * u), Offset(w, h - 0.6f * u), 0.7f * u)
        val tira = Rect(0f, 0f, 14 * u, h)
        drawRect(t(PINO, oscuro), tira.topLeft, tira.size)
        vetas(tira, t(VETA, oscuro).copy(alpha = 0.35f), 41 + e.semilla % 5, densidad = 0.5f, vertical = true)
        drawRect(t(PINO_OSC, oscuro), Offset(tira.right - 1.2f * u, 0f), Size(1.2f * u, h))
        val clavo = Offset(tira.center.x, 11 * u)
        drawCircle(Color.Black.copy(alpha = 0.25f), 1.6f * u, Offset(clavo.x + 0.4f * u, clavo.y + 0.5f * u))
        drawCircle(CLAVO, 1.5f * u, clavo)
        drawCircle(Color.White.copy(alpha = 0.3f), 0.5f * u, Offset(clavo.x - 0.4f * u, clavo.y - 0.4f * u))
        if (e.semilla % 2 == 1) {
            val c = Offset(20 * u, 11 * u); val r = 2.5f * u
            val rojo = rojoCuentas(oscuro)
            drawLine(rojo, Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r), 1.2f * u, StrokeCap.Round)
            drawLine(rojo, Offset(c.x + r, c.y - r), Offset(c.x - r, c.y + r), 1.2f * u, StrokeCap.Round)
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFEEE7D7) else Color(0xFF1F2A3A)
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFE3C27A) else Color(0xFFA23B2A)

    /** La etiqueta de bulto: el vale con las esquinas comidas, su borde de lápiz y el ojal con su arandela arriba a la derecha. */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val k = 4 * u
        val vale = Path().apply {
            moveTo(k, 0f); lineTo(w - k, 0f); quadraticTo(w - k, k, w, k); lineTo(w, h - k); quadraticTo(w - k, h - k, w - k, h)
            lineTo(k, h); quadraticTo(k, h - k, 0f, h - k); lineTo(0f, k); quadraticTo(k, k, k, 0f); close()
        }
        drawPath(vale, papelAlmacen(oscuro))
        drawPath(vale, (if (oscuro) Color(0xFFC9A66A) else PINO_OSC).copy(alpha = 0.6f), style = Stroke(1 * u))
        val ojal = Offset(w - 6 * u, 6 * u)
        drawCircle(if (oscuro) Color(0xFFB8945A) else TACHUELA, 1.9f * u, ojal, style = Stroke(1.1f * u))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    /** El papel de cada tarjeta: el del almacén; la noche, en claro, el papel tostado por la luz del quinqué. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color =
        if (tipo == TipoTarjeta.NOCHE && !oscuro) Color(0xFFEFE6D2) else papelAlmacen(oscuro)

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFEEE7D7) else Color(0xFF1F2A3A)
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFBFB6A2) else Color(0xFF5A5040)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color =
        if (tipo == TipoTarjeta.PENDIENTE) rojoCuentas(oscuro) else if (oscuro) Color(0xFFE3C27A) else Color(0xFFA23B2A)

    /**
     * Las tarjetas del almacén. La cifra de Hoy es la **testa del cajón de semillas**: el marco de pino con sus vetas, el
     * papel de la etiqueta adentro y un clavo en cada esquina; destacada, la escarapela roja con sus dos colas se prende en la
     * esquina de arriba a la derecha. «Te falta hoy» es la **nota del tablón**: el tablón de pino detrás y el papel encima,
     * con el canto de abajo roto a mano y dos chinchetas rojas arriba; completa, las chinchetas pasan a latón y el papel lleva
     * el sello redondo de «saldado». La noche es la **ventana con el quinqué**: el antepecho de pino al pie y el quinqué
     * colgado arriba a la derecha, con su luz tibia; sellada, la mecha se apaga, sube un hilo de humo y el canto se dora. Vida
     * es la **hoja del libro de cuentas**: el doble margen rojo a la izquierda, la raya azul de la cabecera y la esquina de
     * abajo doblada. Al tocarlas el papel se oscurece un poco, como al apoyar la mano.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val fondo = fondoTarjeta(e.tipo, oscuro).let { if (e.presionado) mezcla(it, PINO_OSC, if (oscuro) 0.16f else 0.12f) else it }
        val lapiz = (if (oscuro) Color(0xFFC9A66A) else PINO_OSC).copy(alpha = 0.6f)
        val rojo = rojoCuentas(oscuro)
        val laton = if (oscuro) Color(0xFFB8945A) else TACHUELA
        fun DrawScope.clavo(c: Offset, r: Float = 1.1f * u) {
            drawCircle(t(CLAVO, oscuro), r, c)
            drawCircle(Color.White.copy(alpha = 0.25f), r * 0.4f, Offset(c.x - r * 0.3f, c.y - r * 0.3f))
        }
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val m = 4.5f * u
                drawRect(t(PINO, oscuro))
                vetas(Rect(0f, 0f, w, h), t(VETA, oscuro).copy(alpha = 0.5f), 17, densidad = 0.8f)
                drawRect(fondo, Offset(m, m), Size(w - 2 * m, h - 2 * m))
                drawRect(t(PINO_OSC, oscuro), Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(0.8f * u))
                drawRect(t(PINO_OSC, oscuro), Offset.Zero, size, style = Stroke(0.8f * u))
                listOf(Offset(m / 2, m / 2), Offset(w - m / 2, m / 2), Offset(m / 2, h - m / 2), Offset(w - m / 2, h - m / 2)).forEach { clavo(it, 0.9f * u) }
                // Destacada: la escarapela roja con sus dos colas.
                if (e.destacado) {
                    val c = Offset(w - 9 * u, 7 * u)
                    listOf(-1f, 1f).forEach { s ->
                        drawPath(Path().apply { moveTo(c.x + s * 1 * u, c.y); lineTo(c.x + s * 3.5f * u, c.y + 9 * u); lineTo(c.x + s * 2 * u, c.y + 8 * u); lineTo(c.x + s * 0.5f * u, c.y + 9.5f * u); close() }, t(rojo, oscuro))
                    }
                    drawCircle(rojo, 4.2f * u, c)
                    drawCircle(laton, 2 * u, c)
                    drawPath(Path().apply { addRect(Rect(m, m, w - m, h - m)) }, rojo.copy(alpha = 0.7f), style = Stroke(1.2f * u))
                }
            }
            TipoTarjeta.PENDIENTE -> {
                // El tablón detrás: dos tablas de pino.
                drawRect(t(PINO_OSC, oscuro))
                vetas(Rect(0f, 0f, w, h), t(VETA, oscuro).copy(alpha = 0.55f), 23, densidad = 0.7f)
                drawLine(Color.Black.copy(alpha = 0.35f), Offset(0f, h * 0.5f), Offset(w, h * 0.5f), 1 * u)
                // El papel, con el canto de abajo roto a mano.
                val m = 3 * u
                val nota = Path().apply {
                    moveTo(m, m); lineTo(w - m, m); lineTo(w - m, h - m - 1.5f * u)
                    val paso = maxOf(6 * u, 1f)
                    var x = w - m
                    var k = 0
                    while (x > m && k < 300) { x -= paso; lineTo(maxOf(x, m), h - m - (if (k % 2 == 0) 0f else 2f) * u - 1.4f * u * azar(53, k)); k++ }
                    close()
                }
                drawPath(nota, Color.Black.copy(alpha = 0.25f), style = Stroke(1.2f * u))
                drawPath(nota, fondo)
                // Las chinchetas.
                val pin = if (e.destacado) laton else t(Color(0xFFC0392B), oscuro)
                listOf(Offset(9 * u, 7 * u), Offset(w - 9 * u, 7 * u)).forEach { c ->
                    drawCircle(Color.Black.copy(alpha = 0.25f), 2.8f * u, Offset(c.x + 0.8f * u, c.y + 0.8f * u))
                    drawCircle(pin, 2.6f * u, c)
                    drawCircle(Color.White.copy(alpha = 0.4f), 0.9f * u, Offset(c.x - 0.8f * u, c.y - 0.8f * u))
                }
                // Completa: el sello de «saldado», con su doble raya del libro de cuentas.
                if (e.destacado) {
                    val r = minOf(h * 0.3f, 19 * u)
                    val c = Offset(w - r - 18 * u, h / 2 + 1 * u)
                    rotate(-10f, c) {
                        drawCircle(rojo.copy(alpha = 0.75f), r, c, style = Stroke(1.5f * u))
                        listOf(-1.5f, 1.5f).forEach { d -> drawLine(rojo.copy(alpha = 0.7f), Offset(c.x - r * 0.7f, c.y + d * u), Offset(c.x + r * 0.7f, c.y + d * u), 1 * u) }
                    }
                }
            }
            TipoTarjeta.NOCHE -> {
                drawRect(fondo)
                drawRect(if (e.destacado) acentoTarjeta(e.tipo, oscuro) else lapiz, Offset.Zero, size, style = Stroke(if (e.destacado) 1.6f * u else 1 * u))
                // El quinqué colgado arriba a la derecha y su luz.
                val c = Offset(w - 30 * u, 9 * u)
                if (!e.destacado) mancha(c, 30 * u, Color(0xFFFFC85A).copy(alpha = if (oscuro) 0.3f else 0.35f))
                drawLine(t(CLAVO, oscuro), Offset(c.x, 0f), Offset(c.x, c.y - 5 * u), 0.8f * u)
                // El tubo de vidrio, el depósito y la llama (o el humo).
                drawPath(Path().apply {
                    moveTo(c.x - 1.4f * u, c.y - 5 * u); lineTo(c.x + 1.4f * u, c.y - 5 * u); quadraticTo(c.x + 3.4f * u, c.y, c.x + 2 * u, c.y + 2.5f * u)
                    lineTo(c.x - 2 * u, c.y + 2.5f * u); quadraticTo(c.x - 3.4f * u, c.y, c.x - 1.4f * u, c.y - 5 * u); close()
                }, (if (oscuro) Color(0xFFDDE6EE) else Color(0xFF7A8796)).copy(alpha = 0.6f), style = Stroke(0.7f * u))
                drawOval(laton, Offset(c.x - 3.2f * u, c.y + 2.5f * u), Size(6.4f * u, 3.4f * u))
                if (!e.destacado) drawPath(Path().apply { moveTo(c.x, c.y - 2.6f * u); quadraticTo(c.x + 1.3f * u, c.y + 0.4f * u, c.x, c.y + 1.6f * u); quadraticTo(c.x - 1.3f * u, c.y + 0.4f * u, c.x, c.y - 2.6f * u); close() }, Color(0xFFFFB238))
                else drawPath(Path().apply { moveTo(c.x, c.y - 5 * u); quadraticTo(c.x + 2 * u, c.y - 7 * u, c.x, c.y - 9 * u); quadraticTo(c.x - 2 * u, c.y - 11 * u, c.x + 0.5f * u, c.y - 13 * u) },
                    lapiz.copy(alpha = 0.5f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
                // El antepecho de pino al pie.
                drawRect(t(PINO, oscuro), Offset(0f, h - 4 * u), Size(w, 4 * u))
                drawLine(t(PINO_LUZ, oscuro), Offset(0f, h - 4 * u), Offset(w, h - 4 * u), 0.8f * u)
                drawLine(t(VETA, oscuro).copy(alpha = 0.5f), Offset(w * 0.1f, h - 2 * u), Offset(w * 0.6f, h - 2 * u), 0.5f * u)
            }
            TipoTarjeta.VIDA -> {
                val k = 9 * u
                val hoja = Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h - k); lineTo(w - k, h); lineTo(0f, h); close() }
                drawPath(hoja, fondo)
                drawPath(hoja, lapiz, style = Stroke(0.8f * u))
                // La esquina doblada.
                drawPath(Path().apply { moveTo(w, h - k); lineTo(w - k, h - k); lineTo(w - k, h); close() }, mezcla(fondo, PINO_OSC, 0.25f))
                drawPath(Path().apply { moveTo(w, h - k); lineTo(w - k, h - k); lineTo(w - k, h) }, lapiz, style = Stroke(0.6f * u))
                // El doble margen rojo y la raya azul de la cabecera.
                listOf(6 * u, 7.8f * u).forEach { x -> drawLine(rojo.copy(alpha = 0.6f), Offset(x, 0f), Offset(x, h), 0.6f * u) }
                drawLine((if (oscuro) Color(0xFF7FA0D0) else Color(0xFF1F3A5F)).copy(alpha = 0.45f), Offset(0f, 6 * u), Offset(w, 6 * u), 0.7f * u)
            }
        }
    }
}

/**
 * Ciudad verde: las piezas comunes de las ruinas. El campo es una tira de cinta pegada en el muro de hormigón rajado, como
 * su buscador: plateada y escrita con rotulador negro en claro; en oscuro, cinta de tela negra escrita con marcador de
 * pintura. Tiene las puntas rotas a mano, sus arrugas, una mata de musgo en la esquina y una hoja de hiedra que asoma.
 */
object ComunRuinas : DisenoComun {
    private val HORMIGON = Color(0xFF8C8D84)
    private val HORMIGON_OSC = Color(0xFF65665E)
    private val MUSGO = Color(0xFF55722A)
    private val MUSGO_LUZ = Color(0xFF86A33C)
    private val HOJA = Color(0xFF3D6A2A)
    private val LUCIERNAGA = Color(0xFFE6FF7A)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0D0A), 0.36f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6E8E0) else Color(0xFF16181A)
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9A9D95) else Color(0xFF51544F)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFA4C052) else Color(0xFF3A5618)
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFEC8A5A) else Color(0xFF8C3416)

    /**
     * La cinta en el muro: el hormigón con dos grietas, la cinta con sus puntas rotas en zigzag, su brillo de arriba, sus
     * arrugas tenues y, si son varios renglones, el traslape de una tira con la siguiente en cada renglón; el musgo abajo a la
     * derecha y la hoja de hiedra arriba a la izquierda. Con el foco se posa una luciérnaga en la esquina de arriba y el canto
     * de la cinta se marca en verde musgo; con un error, una mancha de óxido escurre desde arriba y el canto va en óxido.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro).let { if (e.habilitado) it else mezcla(it, Color(0xFF7E7E7A), 0.35f) }
        // El muro con sus grietas.
        drawRect(c(HORMIGON))
        val s = e.semilla
        val g1 = w * (0.3f + 0.4f * azar(s, 1))
        drawPath(Path().apply { moveTo(g1, 0f); lineTo(g1 - 2 * u, 3 * u); lineTo(g1 + 1.5f * u, 5 * u) }, c(HORMIGON_OSC), style = Stroke(0.9f * u))
        val g2 = w * (0.15f + 0.5f * azar(s, 2))
        drawPath(Path().apply { moveTo(g2, h); lineTo(g2 + 1.5f * u, h - 2.5f * u); lineTo(g2 - 1 * u, h - 4.5f * u) }, c(HORMIGON_OSC), style = Stroke(0.9f * u))
        // La cinta, con sus puntas rotas.
        val ci = Rect(4 * u, 4.5f * u, w - 4 * u, h - 4.5f * u)
        val dientes = (ci.height / (5 * u)).toInt().coerceIn(4, 60)
        val cinta = Path().apply {
            moveTo(ci.left + 2 * u, ci.top)
            lineTo(ci.right - 2 * u, ci.top)
            for (i in 1..dientes) lineTo(ci.right + (if (i % 2 == 0) -2f else 0.5f) * u * (0.6f + 0.8f * azar(s + 5, i)), ci.top + ci.height * i / dientes)
            lineTo(ci.left + 2 * u, ci.bottom)
            for (i in dientes - 1 downTo 0) lineTo(ci.left + (if (i % 2 == 0) 2f else -0.5f) * u * (0.6f + 0.8f * azar(s + 7, i)), ci.top + ci.height * i / dientes)
            close()
        }
        drawPath(cinta, Color.Black.copy(alpha = 0.28f), style = Stroke(1.8f * u))
        val tela = if (oscuro) Color(0xFF2C2E2C) else Color(0xFFC9CDC7)
        drawPath(cinta, if (e.habilitado) tela else mezcla(tela, Color(0xFF8A8C88), 0.3f))
        clipPath(cinta) {
            // Las arrugas, tenues, y el brillo de arriba.
            val arrugas = (ci.width / (46 * u)).toInt().coerceIn(2, 14)
            for (i in 0 until arrugas) {
                val x = ci.left + ci.width * (i + 0.3f + 0.4f * azar(s + 11, i)) / arrugas
                drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.22f), Offset(x, ci.top), Offset(x + 6 * u, ci.top + minOf(ci.height, 40 * u)), 0.8f * u)
                drawLine(Color.Black.copy(alpha = 0.06f), Offset(x + 2 * u, ci.top), Offset(x + 8 * u, ci.top + minOf(ci.height, 40 * u)), 0.8f * u)
            }
            drawLine(Color.White.copy(alpha = if (oscuro) 0.1f else 0.4f), Offset(ci.left, ci.top + 1 * u), Offset(ci.right, ci.top + 1 * u), 0.8f * u)
            // El traslape de las tiras, uno por renglón.
            if (e.multilinea) renglones(margenCampoY) { k, y ->
                // La primera raya sería el pie de la cinta: solo los traslapes entre renglones.
                if (k > 0) {
                    drawLine(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(ci.left, y), Offset(ci.right, y + 0.6f * u), 1 * u)
                    drawLine(Color.White.copy(alpha = if (oscuro) 0.07f else 0.3f), Offset(ci.left, y + 1 * u), Offset(ci.right, y + 1.6f * u), 0.7f * u)
                }
            }
            // La mancha de óxido que escurre, con el error.
            if (e.error) {
                val ox = ci.right - 26 * u
                val roja = errorCampo(oscuro).copy(alpha = 0.32f)
                drawOval(roja, Offset(ox - 6 * u, ci.top - 3 * u), Size(14 * u, 7 * u))
                listOf(0f to 0.55f, 4f to 0.35f, 7f to 0.7f).forEach { (dx, f) ->
                    drawLine(roja, Offset(ox + dx * u - 3 * u, ci.top), Offset(ox + dx * u - 3 * u, ci.top + minOf(ci.height * f, 30 * u)), 1.4f * u, StrokeCap.Round)
                }
            }
        }
        when {
            e.error -> drawPath(cinta, errorCampo(oscuro), style = Stroke(1.3f * u))
            e.enfocado -> drawPath(cinta, rotuloCampo(oscuro), style = Stroke(1.2f * u))
        }
        // La hoja de hiedra que asoma arriba a la izquierda.
        listOf(Triple(Offset(1.5f * u, 2 * u), 30f, 10f), Triple(Offset(9 * u, 0.5f * u), 70f, 6.5f)).forEach { (ho, giro, largo) ->
            rotate(giro, ho) {
                val l = largo * u
                drawPath(Path().apply { moveTo(ho.x, ho.y); quadraticTo(ho.x + l * 0.45f, ho.y - l * 0.42f, ho.x + l, ho.y); quadraticTo(ho.x + l * 0.45f, ho.y + l * 0.42f, ho.x, ho.y); close() }, c(HOJA))
                drawLine(c(MUSGO_LUZ).copy(alpha = 0.6f), ho, Offset(ho.x + l * 0.85f, ho.y), 0.5f * u)
            }
        }
        // La mata de musgo abajo a la derecha.
        val m = Offset(w - 5 * u, h - 1.5f * u)
        for (i in 0 until 13) {
            val a = (190f + 160f * azar(s + 13, i)) * (Math.PI / 180f).toFloat()
            val d = (1 + 6 * azar(s + 17, i)) * u
            drawCircle(c(if (i % 3 == 0) MUSGO_LUZ else MUSGO), (1.3f + 1.5f * azar(s + 19, i)) * u, Offset(m.x + d * kotlin.math.cos(a), m.y + d * kotlin.math.sin(a) * 0.55f))
        }
        // La luciérnaga que se posa, con el foco.
        if (e.enfocado && e.habilitado) {
            val l = Offset(w - 9 * u, 3 * u)
            mancha(l, 9 * u, LUCIERNAGA.copy(alpha = 0.45f))
            drawCircle(LUCIERNAGA.copy(alpha = 0.7f), 2 * u, l)
            drawCircle(Color(0xFFF8FFD0), 1 * u, l)
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /**
     * El tallo de hiedra que corre por el muro: ondulado, de punta a punta, con tres hojas alternadas al centro. Tenue, a la
     * mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val a = if (e.tenue) 0.5f else 1f
        val tallo = (if (oscuro) Color(0xFF6E8A3A) else MUSGO).copy(alpha = 0.6f * a)
        val hoja = (if (oscuro) Color(0xFF55813A) else HOJA).copy(alpha = 0.85f * a)
        // El tallo, ondulado.
        val medio = maxOf(14 * u, 1f)
        val amp = 1.3f * u
        val p = Path().apply {
            moveTo(4 * u, y)
            var x = 4 * u
            var k = 0
            while (x < w - 4 * u && k < 100) {
                val x2 = minOf(x + medio, w - 4 * u)
                quadraticTo((x + x2) / 2, y + 2 * (if (k % 2 == 0) -amp else amp), x2, y)
                x = x2; k++
            }
        }
        drawPath(p, tallo, style = Stroke(0.9f * u, cap = StrokeCap.Round))
        // Las tres hojas del centro.
        val cx = w / 2
        listOf(Triple(Offset(cx - 11 * u, y), 145f, 4.8f), Triple(Offset(cx, y - 0.6f * u), -40f, 5.2f), Triple(Offset(cx + 12 * u, y), 35f, 4.5f)).forEach { (ho, giro, largo) ->
            rotate(giro, ho) {
                val l = largo * u
                drawPath(Path().apply { moveTo(ho.x, ho.y); quadraticTo(ho.x + l * 0.45f, ho.y - l * 0.42f, ho.x + l, ho.y); quadraticTo(ho.x + l * 0.45f, ho.y + l * 0.42f, ho.x, ho.y); close() }, hoja)
            }
        }
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFF8FCB6A) else Color(0xFF355E23)

    /**
     * Un trozo de la cinta del campo, pegado detrás del texto: más bajo que el botón, con las puntas rotas a mano, su brillo de
     * arriba y una pizca de musgo en la esquina. Al presionarlo la cinta se aprieta contra el muro (más opaca, con el canto en
     * verde musgo); apagada, apenas se ve y no tiene musgo.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val alto = minOf(h - 16 * u, 24 * u)
        val ci = Rect(3 * u, (h - alto) / 2, w - 3 * u, (h + alto) / 2)
        val dientes = 4
        val cinta = Path().apply {
            moveTo(ci.left + 1.5f * u, ci.top)
            lineTo(ci.right - 1.5f * u, ci.top)
            for (i in 1..dientes) lineTo(ci.right + (if (i % 2 == 0) -1.5f else 0.5f) * u * (0.6f + 0.8f * azar(s + 5, i)), ci.top + ci.height * i / dientes)
            lineTo(ci.left + 1.5f * u, ci.bottom)
            for (i in dientes - 1 downTo 0) lineTo(ci.left + (if (i % 2 == 0) 1.5f else -0.5f) * u * (0.6f + 0.8f * azar(s + 7, i)), ci.top + ci.height * i / dientes)
            close()
        }
        val tela = if (oscuro) Color(0xFF2C2E2C) else Color(0xFFC9CDC7)
        val opaca = when { !e.habilitado -> if (oscuro) 0.28f else 0.16f; e.presionado -> if (oscuro) 1f else 0.8f; else -> if (oscuro) 0.6f else 0.35f }
        drawPath(cinta, tela.copy(alpha = opaca))
        drawLine(Color.White.copy(alpha = (if (oscuro) 0.08f else 0.35f) * opaca), Offset(ci.left + 2 * u, ci.top + 1 * u), Offset(ci.right - 2 * u, ci.top + 1 * u), 0.8f * u)
        if (!e.habilitado) return
        if (e.presionado) drawPath(cinta, rotuloCampo(oscuro).copy(alpha = 0.8f), style = Stroke(1 * u))
        // La pizca de musgo, abajo a la derecha.
        val m = Offset(ci.right - 3 * u, ci.bottom)
        for (i in 0 until 5) {
            val dx = (-4 + 6 * azar(s + 13, i)) * u
            drawCircle(t(if (i % 2 == 0) MUSGO_LUZ else MUSGO, oscuro), (0.9f + 0.7f * azar(s + 19, i)) * u, Offset(m.x + dx, m.y - 0.8f * u * azar(s + 17, i)))
        }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El hormigón liso de las placas: claro y lavado por la lluvia, u oscuro y húmedo de noche. */
    private fun losa(oscuro: Boolean): Color = if (oscuro) Color(0xFF1A201C) else Color(0xFFDCDCD3)

    /** El gris de las grietas. */
    private fun grieta(oscuro: Boolean): Color = if (oscuro) Color(0xFF4A554C) else HORMIGON_OSC

    /** Una mata de musgo de [n] bolitas alrededor de [c], de hasta [r] de radio. */
    private fun DrawScope.matita(c: Offset, r: Float, n: Int, semilla: Int, oscuro: Boolean) {
        for (i in 0 until n) {
            val a = (190f + 160f * azar(semilla, i)) * (Math.PI / 180f).toFloat()
            val d = r * azar(semilla + 4, i)
            drawCircle(t(if (i % 3 == 0) MUSGO_LUZ else MUSGO, oscuro), r * (0.25f + 0.25f * azar(semilla + 8, i)),
                Offset(c.x + d * kotlin.math.cos(a), c.y + d * kotlin.math.sin(a) * 0.55f))
        }
    }

    /** Una hoja de hiedra de punta en [o], girada [giro] grados y de [l] de largo, con su nervio. */
    private fun DrawScope.hojita(o: Offset, giro: Float, l: Float, color: Color, nervio: Color, relleno: Boolean = true, grosor: Float = 1f) {
        rotate(giro, o) {
            val p = Path().apply { moveTo(o.x, o.y); quadraticTo(o.x + l * 0.45f, o.y - l * 0.46f, o.x + l, o.y); quadraticTo(o.x + l * 0.45f, o.y + l * 0.46f, o.x, o.y); close() }
            if (relleno) drawPath(p, color) else drawPath(p, color, style = Stroke(grosor, join = StrokeJoin.Round))
            drawLine(nervio, o, Offset(o.x + l * 0.85f, o.y), grosor * 0.6f)
        }
    }

    override val margenFila: Dp = 12.dp

    /**
     * La fila del muro: una grieta fina que corre al pie, quebrada a su manera en cada fila, y una matita de musgo abajo a la
     * izquierda. Al tocarla el muro se tiñe de verde y una luciérnaga se enciende junto al musgo.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (e.presionado) drawRect(MUSGO.copy(alpha = if (oscuro) 0.14f else 0.1f))
        val s = e.semilla
        val tramos = (w / (40 * u)).toInt().coerceIn(2, 30)
        val raya = Path().apply {
            moveTo(8 * u, h - 1 * u)
            for (i in 1..tramos) lineTo(8 * u + (w - 8 * u) * i / tramos, h - (0.6f + 1.6f * azar(s, i)) * u)
        }
        drawPath(raya, grieta(oscuro).copy(alpha = if (oscuro) 0.9f else 0.5f), style = Stroke(0.7f * u, join = StrokeJoin.Round))
        matita(Offset(4 * u, h - 2.5f * u), (if (e.conDetalle) 4.5f else 3.5f) * u, 5, s + 3, oscuro)
        if (e.presionado) {
            val l = Offset(6 * u, h / 2 - 4 * u)
            mancha(l, 7 * u, LUCIERNAGA.copy(alpha = 0.45f))
            drawCircle(Color(0xFFF8FFD0), 0.9f * u, l)
        }
    }

    /** La flecha pintada con aerosol en la pared: el trazo verde musgo con su punta y una gota que escurre. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        val pintura = tintaBotonTexto(oscuro)
        drawLine(pintura, Offset(c.x - 6.5f * u, c.y + 0.4f * u), Offset(c.x + 4 * u, c.y), 1.8f * u, StrokeCap.Round)
        drawPath(Path().apply { moveTo(c.x + 0.5f * u, c.y - 4.5f * u); lineTo(c.x + 5.5f * u, c.y); lineTo(c.x + 0.5f * u, c.y + 4.5f * u) }, pintura,
            style = Stroke(1.8f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(pintura.copy(alpha = 0.7f), Offset(c.x - 3 * u, c.y + 1 * u), Offset(c.x - 3 * u, c.y + 5.5f * u), 0.9f * u, StrokeCap.Round)
        drawCircle(pintura.copy(alpha = 0.7f), 0.9f * u, Offset(c.x - 3 * u, c.y + 6 * u))
    }

    /** La hoja de hiedra: fija, llena y verde, con su nervio claro; sin fijar, solo su contorno, gris. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val o = Offset(size.width / 2 - 6 * u, size.height / 2 + 6 * u)
        val l = 17 * u
        drawLine(if (fija) t(HOJA, oscuro) else grieta(oscuro), o, Offset(o.x - 3 * u, o.y + 3 * u), 1 * u, StrokeCap.Round)
        if (fija) hojita(o, -45f, l, if (oscuro) Color(0xFF6FA04A) else HOJA, t(MUSGO_LUZ, false).copy(alpha = 0.8f), grosor = 1.2f * u)
        else hojita(o, -45f, l, grieta(oscuro).copy(alpha = if (oscuro) 1f else 0.8f), grieta(oscuro).copy(alpha = 0.6f), relleno = false, grosor = 1 * u)
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) Color(0xFFAAB3A6) else Color(0xFF3A3F38)

    /**
     * El trozo de muro donde no queda nada: la losa de hormigón con dos grietas, una mata de musgo en la esquina de abajo a la
     * izquierda y una hoja de hiedra que se asoma por la de arriba a la derecha.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        drawRect(losa(oscuro))
        drawRect(grieta(oscuro).copy(alpha = 0.35f), Offset.Zero, size, style = Stroke(0.8f * u))
        val g = grieta(oscuro).copy(alpha = if (oscuro) 0.9f else 0.6f)
        val g1 = w * (0.12f + 0.2f * azar(s, 1))
        drawPath(Path().apply { moveTo(g1, 0f); lineTo(g1 + 3 * u, 4 * u); lineTo(g1 + 1 * u, 8 * u); lineTo(g1 + 5 * u, 12 * u) }, g, style = Stroke(0.8f * u))
        val g2 = w * (0.62f + 0.25f * azar(s, 2))
        drawPath(Path().apply { moveTo(g2, h); lineTo(g2 - 2 * u, h - 4 * u); lineTo(g2 + 1.5f * u, h - 7 * u); moveTo(g2 - 2 * u, h - 4 * u); lineTo(g2 - 6 * u, h - 6 * u) }, g, style = Stroke(0.8f * u))
        matita(Offset(10 * u, h - 3 * u), 7 * u, 12, s + 13, oscuro)
        hojita(Offset(w - 2 * u, 1 * u), 140f, 11 * u, t(HOJA, oscuro), t(MUSGO_LUZ, oscuro).copy(alpha = 0.6f), grosor = 0.9f * u)
        hojita(Offset(w - 2 * u, 1 * u), 105f, 8 * u, t(MUSGO, oscuro), t(MUSGO_LUZ, oscuro).copy(alpha = 0.6f), grosor = 0.8f * u)
    }

    /**
     * La maceta de barro rota y vacía: el borde mellado, el hueco oscuro sin tierra, la grieta que baja por el cuerpo y una hoja
     * seca caída a su lado, en el suelo.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val barro = t(Color(0xFFB0643A), oscuro)
        val borde = if (oscuro) Color(0xFFD08A60) else Color(0xFF6E3A1E)
        val tr = Stroke(1.1f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // El cuerpo.
        val cuerpo = Path().apply { moveTo(f(0.24f), f(0.42f)); lineTo(f(0.32f), f(0.88f)); lineTo(f(0.68f), f(0.88f)); lineTo(f(0.76f), f(0.42f)); close() }
        drawPath(cuerpo, barro)
        drawPath(cuerpo, borde, style = tr)
        // El borde, con su mella a la derecha.
        val labio = Path().apply {
            moveTo(f(0.18f), f(0.3f)); lineTo(f(0.6f), f(0.3f)); lineTo(f(0.66f), f(0.37f)); lineTo(f(0.72f), f(0.32f)); lineTo(f(0.82f), f(0.32f))
            lineTo(f(0.82f), f(0.42f)); lineTo(f(0.18f), f(0.42f)); close()
        }
        drawPath(labio, mezcla(barro, Color.White, 0.12f))
        drawPath(labio, borde, style = tr)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.6f else 0.45f), Offset(f(0.22f), f(0.38f)), Size(f(0.56f), f(0.035f)))
        // La grieta.
        drawPath(Path().apply { moveTo(f(0.56f), f(0.42f)); lineTo(f(0.52f), f(0.56f)); lineTo(f(0.58f), f(0.66f)); lineTo(f(0.54f), f(0.8f)) }, borde, style = Stroke(0.8f * u, join = StrokeJoin.Round))
        // El trozo caído y la hoja seca.
        drawPath(Path().apply { moveTo(f(0.8f), f(0.9f)); lineTo(f(0.88f), f(0.84f)); lineTo(f(0.94f), f(0.9f)); close() }, barro)
        hojita(Offset(f(0.06f), f(0.9f)), -12f, f(0.2f), t(Color(0xFF8A7A3A), oscuro), borde.copy(alpha = 0.5f), grosor = 0.8f * u)
        drawLine(grieta(oscuro), Offset(f(0.02f), f(0.93f)), Offset(f(0.98f), f(0.93f)), 0.8f * u, StrokeCap.Round)
    }

    override val margenAviso: Dp = 28.dp
    override fun tintaAviso(oscuro: Boolean): Color = tintaCampo(oscuro)

    /**
     * La cinta del campo pegada en el muro con su aviso: las puntas rotas a mano, su brillo de arriba y, a la izquierda, la
     * marca pintada con aerosol en óxido: la admiración o, según el aviso, la cruz de «cuidado».
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val dientes = (h / (5 * u)).toInt().coerceIn(3, 40)
        val cinta = Path().apply {
            moveTo(1.5f * u, 0f); lineTo(w - 1.5f * u, 0f)
            for (i in 1..dientes) lineTo(w + (if (i % 2 == 0) -1.5f else 0f) * u * (0.6f + 0.8f * azar(s + 5, i)), h * i / dientes)
            lineTo(1.5f * u, h)
            for (i in dientes - 1 downTo 0) lineTo((if (i % 2 == 0) 1.5f else 0f) * u * (0.6f + 0.8f * azar(s + 7, i)), h * i / dientes)
            close()
        }
        drawPath(cinta, if (oscuro) Color(0xFF2C2E2C) else Color(0xFFCFD3CD))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.45f), Offset(3 * u, 1.2f * u), Offset(w - 3 * u, 1.2f * u), 0.8f * u)
        val oxido = errorCampo(oscuro)
        val x = 13 * u
        if (s % 2 == 0) {
            drawLine(oxido, Offset(x, 6 * u), Offset(x + 0.6f * u, 15 * u), 2.4f * u, StrokeCap.Round)
            drawCircle(oxido, 1.4f * u, Offset(x + 0.6f * u, 20 * u))
            drawLine(oxido.copy(alpha = 0.6f), Offset(x + 0.4f * u, 15 * u), Offset(x + 0.4f * u, minOf(17.5f * u, h - 2 * u)), 0.8f * u, StrokeCap.Round)
        } else {
            drawLine(oxido, Offset(x - 4 * u, 8 * u), Offset(x + 4 * u, 18 * u), 2.2f * u, StrokeCap.Round)
            drawLine(oxido, Offset(x + 4 * u, 8 * u), Offset(x - 4 * u, 18 * u), 2.2f * u, StrokeCap.Round)
            drawLine(oxido.copy(alpha = 0.6f), Offset(x + 3 * u, 17 * u), Offset(x + 3 * u, minOf(22 * u, h - 2 * u)), 0.8f * u, StrokeCap.Round)
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFE3E6DC) else Color(0xFF1F2420)
    override fun rotuloCifra(oscuro: Boolean): Color = rotuloCampo(oscuro)

    /** La baldosa de hormigón: su canto, una grieta que baja de la esquina de arriba y una pizca de musgo en la de abajo. */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(losa(oscuro))
        drawRect(grieta(oscuro).copy(alpha = 0.4f), Offset.Zero, size, style = Stroke(0.8f * u))
        drawPath(Path().apply { moveTo(w - 10 * u, 0f); lineTo(w - 7 * u, 3 * u); lineTo(w - 8 * u, 6 * u); lineTo(w - 4 * u, 8 * u) },
            grieta(oscuro).copy(alpha = if (oscuro) 0.9f else 0.6f), style = Stroke(0.7f * u))
        matita(Offset(w - 6 * u, h - 2.5f * u), 4 * u, 6, e.semilla, oscuro)
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    /** El fondo de cada tarjeta: la losa de hormigón; Vida, el papel del herbario. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color =
        if (tipo == TipoTarjeta.VIDA) (if (oscuro) Color(0xFF1E241F) else Color(0xFFF1F0E8)) else losa(oscuro)

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFE3E6DC) else Color(0xFF1F2420)
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (oscuro) Color(0xFFAAB3A6) else Color(0xFF454B44)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = rotuloCampo(oscuro)

    /** Una luciérnaga: el halo en dos capas (sin degradado) y su punto de luz; de día, más verde para que se vea. */
    private fun DrawScope.luciernaga(c: Offset, r: Float, oscuro: Boolean, fuerza: Float = 1f) {
        val halo = if (oscuro) LUCIERNAGA else Color(0xFFA8C020)
        drawCircle(halo.copy(alpha = 0.16f * fuerza), r * 3.2f, c)
        drawCircle(halo.copy(alpha = 0.35f * fuerza), r * 1.8f, c)
        drawCircle((if (oscuro) mezcla(LUCIERNAGA, Color.White, 0.5f) else Color(0xFF6E8A00)).copy(alpha = fuerza), r, c)
    }

    /**
     * Las tarjetas de la ciudad verde. La cifra de Hoy es la **placa de calle caída**: la chapa con su filete pintado adentro,
     * dos agujeros de tornillo, manchas de óxido en dos esquinas y una hoja de hiedra que asoma abajo a la izquierda;
     * destacada, una luciérnaga se posa en la esquina de arriba y el filete se pinta de verde. «Te falta hoy» es la **nota
     * pegada en el muro**: la losa de hormigón con el papel encima, dos tiras de cinta en las esquinas de arriba y la hiedra
     * que trepa por el canto izquierdo; completa, el papel se enmarca de verde y tres luciérnagas lo rondan. La noche son **las
     * luciérnagas sobre la hierba**: la luna arriba a la derecha, unas luciérnagas sueltas por los cantos y las briznas de
     * hierba al pie; sellada, se juntan en el frasco de vidrio, abajo a la derecha. Vida es la **hoja del herbario**: el papel
     * con sus cuatro esquineros y una hoja prensada en la esquina de abajo. Al tocarlas la losa se tiñe de musgo.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val fondo = fondoTarjeta(e.tipo, oscuro).let { if (e.presionado) mezcla(it, MUSGO, if (oscuro) 0.16f else 0.14f) else it }
        val verde = rotuloCampo(oscuro)
        val hoja = if (oscuro) Color(0xFF6FA04A) else HOJA
        val nervio = t(MUSGO_LUZ, false).copy(alpha = 0.8f)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val radio = CornerRadius(5 * u)
                drawRoundRect(fondo, Offset.Zero, size, radio)
                drawRoundRect(grieta(oscuro).copy(alpha = 0.6f), Offset.Zero, size, radio, style = Stroke(0.8f * u))
                // El filete pintado de la placa.
                drawRoundRect(if (e.destacado) verde else grieta(oscuro).copy(alpha = 0.55f), Offset(3 * u, 3 * u), Size(w - 6 * u, h - 6 * u), CornerRadius(3 * u),
                    style = Stroke(if (e.destacado) 1.4f * u else 1 * u))
                // Las manchas de óxido, en dos esquinas, y los agujeros de tornillo.
                val oxido = if (oscuro) Color(0xFF7A4A28) else Color(0xFFA8642E)
                for (i in 0 until 5) {
                    drawCircle(oxido.copy(alpha = 0.18f + 0.2f * azar(61, i)), (1 + 2.2f * azar(62, i)) * u, Offset(w - (2 + 7 * azar(63, i)) * u, h - (2 + 7 * azar(64, i)) * u))
                    drawCircle(oxido.copy(alpha = 0.15f + 0.2f * azar(65, i)), (1 + 1.8f * azar(66, i)) * u, Offset((2 + 6 * azar(67, i)) * u, (2 + 6 * azar(68, i)) * u))
                }
                listOf(Offset(w / 2, 1.8f * u + 1.5f * u), Offset(w / 2, h - 3.3f * u)).forEach {
                    drawCircle(if (oscuro) Color(0xFF0B0D0A) else Color(0xFF5A5B54), 1.1f * u, it)
                }
                // La hoja de hiedra que asoma abajo a la izquierda.
                hojita(Offset(1.5f * u, h - 3 * u), -25f, 11 * u, hoja, nervio, grosor = 1 * u)
                if (e.destacado) luciernaga(Offset(w - 7 * u, 6 * u), 1.1f * u, oscuro)
            }
            TipoTarjeta.PENDIENTE -> {
                drawRect(losa(oscuro))
                drawRect(grieta(oscuro).copy(alpha = 0.5f), Offset.Zero, size, style = Stroke(0.8f * u))
                // El papel pegado en el muro.
                val papel = Rect(5 * u, 3 * u, w - 3 * u, h - 3 * u)
                val claro = if (oscuro) Color(0xFF242B26) else Color(0xFFF4F3EC)
                drawRect(Color.Black.copy(alpha = 0.18f), Offset(papel.left + 1 * u, papel.top + 1.2f * u), papel.size)
                drawRect(if (e.presionado) mezcla(claro, MUSGO, 0.14f) else claro, papel.topLeft, papel.size)
                if (e.destacado) drawRect(verde, papel.topLeft, papel.size, style = Stroke(1.6f * u))
                // Las dos tiras de cinta, cruzadas en las esquinas de arriba.
                val cinta = (if (oscuro) Color(0xFF8E948C) else Color(0xFFB9BDB5)).copy(alpha = 0.75f)
                clipRect(0f, 0f, w, h) {
                    listOf(Offset(papel.left + 3 * u, papel.top + 1 * u) to -40f, Offset(papel.right - 3 * u, papel.top + 1 * u) to 40f).forEach { (c, a) ->
                        rotate(a, c) { drawRect(cinta, Offset(c.x - 8 * u, c.y - 2.5f * u), Size(16 * u, 5 * u)) }
                    }
                }
                // La hiedra que trepa por el canto izquierdo.
                drawLine(t(HOJA, oscuro), Offset(2.5f * u, h), Offset(2.5f * u, h * 0.25f), 0.9f * u, StrokeCap.Round)
                val paso = maxOf(14 * u, 1f)
                var y = h - 6 * u
                var k = 0
                while (y > h * 0.25f && k < 40) {
                    hojita(Offset(2.5f * u, y), if (k % 2 == 0) -60f else -120f, 7 * u, hoja, nervio, grosor = 0.8f * u)
                    y -= paso; k++
                }
                if (e.destacado) listOf(Offset(w - 10 * u, 8 * u), Offset(w - 22 * u, h - 6 * u), Offset(w * 0.6f, 4 * u)).forEach { luciernaga(it, 1 * u, oscuro) }
            }
            TipoTarjeta.NOCHE -> {
                drawRect(fondo)
                drawRect(grieta(oscuro).copy(alpha = 0.5f), Offset.Zero, size, style = Stroke(0.8f * u))
                // La luna, arriba a la derecha.
                val luna = Offset(w - 10 * u, 8 * u)
                drawCircle((if (oscuro) Color(0xFFE8EBD8) else Color(0xFF9AA08F)).copy(alpha = 0.8f), 3.6f * u, luna)
                drawCircle(fondo, 3.2f * u, Offset(luna.x - 1.8f * u, luna.y - 0.6f * u))
                // Las briznas de hierba al pie.
                val briznas = Path()
                val paso = maxOf(4 * u, 1f)
                var x = 2 * u
                var k = 0
                while (x < w - 2 * u && k < 300) {
                    val alto = (2.5f + 4f * azar(71, k)) * u
                    briznas.moveTo(x, h); briznas.quadraticTo(x + 0.5f * u, h - alto * 0.6f, x + (azar(72, k) - 0.5f) * 3 * u, h - alto)
                    x += paso; k++
                }
                drawPath(briznas, t(MUSGO, oscuro).copy(alpha = 0.8f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
                // Las luciérnagas sueltas por los cantos (sellada: casi todas en el frasco).
                val sueltas = listOf(Offset(w * 0.3f, 5 * u), Offset(w * 0.55f, 4 * u), Offset(w * 0.45f, h - 9 * u), Offset(5 * u, h * 0.55f), Offset(w * 0.78f, 6 * u))
                sueltas.forEachIndexed { i, c -> if (!e.destacado || i == 0) luciernaga(c, 0.9f * u, oscuro, if (e.destacado) 0.6f else 0.9f) }
                if (e.destacado) {
                    // El frasco de vidrio con su tapa, abajo a la derecha, lleno de luces.
                    val f = Rect(w - 18 * u, h - 15 * u, w - 7 * u, h - 2 * u)
                    drawRoundRect(LUCIERNAGA.copy(alpha = 0.18f), f.topLeft, f.size, CornerRadius(2 * u))
                    drawRoundRect((if (oscuro) Color(0xFFC8D0C0) else Color(0xFF5A6458)).copy(alpha = 0.8f), f.topLeft, f.size, CornerRadius(2 * u), style = Stroke(0.8f * u))
                    drawRect(t(HORMIGON_OSC, oscuro), Offset(f.left - 0.5f * u, f.top - 1.6f * u), Size(f.width + 1 * u, 1.8f * u))
                    for (i in 0 until 4) luciernaga(Offset(f.left + (2 + 7 * azar(81, i)) * u, f.top + (2.5f + 7 * azar(82, i)) * u), 0.6f * u, oscuro)
                }
            }
            TipoTarjeta.VIDA -> {
                drawRect(fondo)
                drawRect(grieta(oscuro).copy(alpha = 0.45f), Offset.Zero, size, style = Stroke(0.8f * u))
                // Los cuatro esquineros.
                val k = 7 * u
                val esquinero = if (oscuro) Color(0xFF3E4A40) else Color(0xFF4A5546)
                listOf(Offset(0f, 0f) to Offset(1f, 1f), Offset(w, 0f) to Offset(-1f, 1f), Offset(0f, h) to Offset(1f, -1f), Offset(w, h) to Offset(-1f, -1f)).forEach { (o, d) ->
                    drawPath(Path().apply { moveTo(o.x + d.x * 1.5f * u, o.y + d.y * 1.5f * u); lineTo(o.x + d.x * (k + 1.5f * u), o.y + d.y * 1.5f * u); lineTo(o.x + d.x * 1.5f * u, o.y + d.y * (k + 1.5f * u)); close() }, esquinero)
                }
                // La hoja prensada, abajo a la derecha, desvaída.
                hojita(Offset(w - 30 * u, h - 6 * u), -18f, 18 * u, hoja.copy(alpha = 0.35f), nervio.copy(alpha = 0.4f), grosor = 0.9f * u)
                drawLine(t(HOJA, oscuro).copy(alpha = 0.4f), Offset(w - 30 * u, h - 6 * u), Offset(w - 36 * u, h - 3 * u), 0.8f * u, StrokeCap.Round)
            }
        }
    }
}

/**
 * Espacio: las piezas comunes de la nave. El campo es el visor de exploración del casco, como su buscador: el cristal
 * oscuro ochavado en su marco de panel, con los corchetes de las cuatro esquinas y las tres luces de estado del panel.
 */
object ComunEspacio : DisenoComun {
    private val PANEL = Color(0xFFD6DCE5)
    private val PANEL_OSC = Color(0xFF9AA4B4)
    private val CIAN = Color(0xFF5CE1FF)
    private val VERDE = Color(0xFF5CFFB0)
    private val AMBAR = Color(0xFFFFB84A)
    private val CRISTAL = Color(0xFF070C18)

    /** El color en modo oscuro, con las luces de la nave bajas. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF05070D), 0.62f) else c

    /** Un rectángulo con las cuatro esquinas cortadas en [ch]. */
    private fun ochavado(r: Rect, ch: Float): Path = Path().apply {
        moveTo(r.left + ch, r.top); lineTo(r.right - ch, r.top); lineTo(r.right, r.top + ch); lineTo(r.right, r.bottom - ch)
        lineTo(r.right - ch, r.bottom); lineTo(r.left + ch, r.bottom); lineTo(r.left, r.bottom - ch); lineTo(r.left, r.top + ch); close()
    }

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = Color(0xFFE8F4FF)
    override fun pistaCampo(oscuro: Boolean): Color = Color(0xFF8396AC)
    override fun rotuloCampo(oscuro: Boolean): Color = CIAN
    override fun errorCampo(oscuro: Boolean): Color = Color(0xFFFF7088)

    /**
     * El visor: el marco de panel ochavado (claro en claro, de casco en oscuro) con las tres luces de estado arriba a la
     * derecha; el cristal oscuro ochavado con su fila de puntos de exploración al pie (y una raya tenue por renglón si son
     * varios) y los corchetes de las esquinas. Con el foco los corchetes se encienden en cian, la luz verde se prende y una
     * línea de exploración cruza el pie del cristal; con un error los corchetes y la primera luz van en rojo de alarma.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro).let { if (e.habilitado) it else mezcla(it, Color(0xFF6E737A), 0.4f) }
        val todo = ochavado(Rect(0f, 0f, w, h), 8 * u)
        drawPath(todo, c(PANEL))
        drawPath(todo, c(PANEL_OSC), style = Stroke(1 * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.6f), Offset(9 * u, 1.2f * u), Offset(w - 9 * u, 1.2f * u), 0.8f * u)
        val cr = Rect(4 * u, 4 * u, w - 4 * u, h - 4 * u)
        val cristal = ochavado(cr, 6 * u)
        drawPath(cristal, CRISTAL)
        val vivo = e.enfocado && e.habilitado
        clipPath(cristal) {
            drawRect(Color.Black.copy(alpha = 0.5f), cr.topLeft, Size(cr.width, 1.8f * u))
            if (vivo) drawRect(CIAN.copy(alpha = 0.05f), cr.topLeft, cr.size)
            if (e.multilinea) renglones(margenCampoY) { k, y -> if (k > 0) drawLine(CIAN.copy(alpha = 0.08f), Offset(cr.left + 12 * u, y), Offset(cr.right - 12 * u, y), 0.7f * u) }
        }
        // La fila de puntos de exploración, al pie del cristal; con el foco, la línea que la cruza.
        val yp = cr.bottom - 2.6f * u
        val paso = maxOf(8 * u, 1f)
        var x = cr.left + 14 * u
        var k = 0
        while (x < cr.right - 12 * u && k < 500) { drawCircle(CIAN.copy(alpha = if (vivo) 0.3f else 0.14f), 0.6f * u, Offset(x, yp)); x += paso; k++ }
        if (vivo) drawLine(CIAN.copy(alpha = 0.45f), Offset(cr.left + 12 * u, yp), Offset(cr.right - 12 * u, yp), 0.7f * u)
        // Los corchetes de las esquinas.
        val col = when {
            !e.habilitado -> CIAN.copy(alpha = 0.18f)
            e.error -> errorCampo(oscuro)
            vivo -> CIAN
            else -> CIAN.copy(alpha = 0.38f)
        }
        val l = 8 * u
        val gr = (if (vivo || e.error) 1.6f else 1.2f) * u
        listOf(Offset(cr.left + 3 * u, cr.top + 3 * u) to Offset(1f, 1f), Offset(cr.right - 3 * u, cr.top + 3 * u) to Offset(-1f, 1f),
            Offset(cr.left + 3 * u, cr.bottom - 3 * u) to Offset(1f, -1f), Offset(cr.right - 3 * u, cr.bottom - 3 * u) to Offset(-1f, -1f)).forEach { (o, d) ->
            val p = Path().apply { moveTo(o.x + d.x * l, o.y); lineTo(o.x + d.x * 2.5f * u, o.y); lineTo(o.x, o.y + d.y * 2.5f * u); lineTo(o.x, o.y + d.y * l * 0.75f) }
            drawPath(p, col, style = Stroke(gr, cap = StrokeCap.Square, join = StrokeJoin.Miter))
        }
        // Las tres luces de estado del panel, arriba a la derecha.
        val luces = listOf(
            if (e.error) errorCampo(oscuro) else VERDE,
            AMBAR,
            CIAN,
        )
        luces.forEachIndexed { i, cl ->
            val o = Offset(w - 30 * u + i * 5 * u, 2 * u)
            val prendida = e.habilitado && when (i) { 0 -> vivo || e.error; 1 -> false; else -> true }
            drawCircle(if (prendida) cl else mezcla(cl, Color(0xFF1A1E26), 0.7f), 0.95f * u, o)
            if (prendida) drawCircle(cl.copy(alpha = 0.3f), 2 * u, o)
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * La línea del visor: un trazo fino con un tope en cada punta, cortado al centro por una órbita inclinada con su planeta
     * ámbar y su satélite. Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val a = if (e.tenue) 0.5f else 1f
        val azul = if (oscuro) Color(0xFF8AB4FF) else Color(0xFF1B3A8C)
        val linea = azul.copy(alpha = 0.42f * a)
        val cx = w / 2
        val hueco = 11 * u
        val p = Path().apply {
            moveTo(4 * u, y); lineTo(cx - hueco, y); moveTo(cx + hueco, y); lineTo(w - 4 * u, y)
            // Los topes de las puntas.
            moveTo(4 * u, y - 2 * u); lineTo(4 * u, y + 2 * u); moveTo(w - 4 * u, y - 2 * u); lineTo(w - 4 * u, y + 2 * u)
        }
        drawPath(p, linea, style = Stroke(0.8f * u))
        // La órbita, el planeta y el satélite.
        val giro = -12f
        rotate(giro, Offset(cx, y)) { drawOval(linea, Offset(cx - 8 * u, y - 2.6f * u), Size(16 * u, 5.2f * u), style = Stroke(0.7f * u)) }
        drawCircle((if (oscuro) AMBAR else Color(0xFFE8601C)).copy(alpha = 0.9f * a), 1.8f * u, Offset(cx, y))
        val th = Math.toRadians(205.0); val g = Math.toRadians(giro.toDouble())
        val ex = 8 * u * kotlin.math.cos(th).toFloat(); val ey = 2.6f * u * kotlin.math.sin(th).toFloat()
        val sat = Offset(cx + ex * kotlin.math.cos(g).toFloat() - ey * kotlin.math.sin(g).toFloat(), y + ex * kotlin.math.sin(g).toFloat() + ey * kotlin.math.cos(g).toFloat())
        drawCircle(azul.copy(alpha = a), 0.9f * u, sat)
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFF8AB4FF) else Color(0xFF1B3A8C)

    /**
     * Los corchetes del visor en las cuatro esquinas, como en el campo. Al presionarlo se cierran sobre el texto (fijan el
     * blanco), se encienden y el recuadro se tiñe; apagado, quedan muy tenues y grises.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val fijo = e.presionado && e.habilitado
        val tinta = if (e.habilitado) tintaBotonTexto(oscuro) else Color(0xFF8A93A3)
        val dentro = if (fijo) 2.5f * u else 0f
        val r = Rect(3 * u + dentro, 6 * u + dentro, w - 3 * u - dentro, h - 6 * u - dentro)
        if (fijo) drawRect(tinta.copy(alpha = if (oscuro) 0.12f else 0.08f), r.topLeft, r.size)
        val l = minOf(6 * u, r.width * 0.3f, r.height * 0.35f)
        val esquinas = Path().apply {
            moveTo(r.left, r.top + l); lineTo(r.left, r.top); lineTo(r.left + l, r.top)
            moveTo(r.right - l, r.top); lineTo(r.right, r.top); lineTo(r.right, r.top + l)
            moveTo(r.right, r.bottom - l); lineTo(r.right, r.bottom); lineTo(r.right - l, r.bottom)
            moveTo(r.left + l, r.bottom); lineTo(r.left, r.bottom); lineTo(r.left, r.bottom - l)
        }
        drawPath(esquinas, tinta.copy(alpha = when { !e.habilitado -> 0.3f; fijo -> 1f; else -> 0.55f }),
            style = Stroke((if (fijo) 1.4f else 1f) * u, cap = StrokeCap.Square, join = StrokeJoin.Miter))
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El fondo de las pantallas de la nave: blanco de panel en claro, el negro del espacio en oscuro. */
    private fun pantalla(oscuro: Boolean): Color = if (oscuro) Color(0xFF0A0F1E) else Color(0xFFF8FAFD)

    /** El filo de los paneles. */
    private fun filo(oscuro: Boolean): Color = if (oscuro) Color(0xFF26324F) else Color(0xFFC3CCDA)

    override val margenFila: Dp = 14.dp

    /**
     * La línea del visor: un trazo fino al pie con un tope en cada punta y, a la izquierda, el corchete del visor, tenue. Al
     * tocarla el corchete se cierra sobre la fila y se enciende, y la fila se tiñe de azul.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val azul = tintaBotonTexto(oscuro)
        if (e.presionado) drawRect(azul.copy(alpha = if (oscuro) 0.1f else 0.07f))
        val y = h - 1 * u
        val linea = azul.copy(alpha = if (e.presionado) 0.6f else 0.22f)
        drawPath(Path().apply { moveTo(10 * u, y); lineTo(w - 4 * u, y); moveTo(w - 4 * u, y - 2.5f * u); lineTo(w - 4 * u, y) }, linea, style = Stroke(0.7f * u))
        val alto = (if (e.conDetalle) 0.56f else 0.42f) * h
        val x = if (e.presionado) 4 * u else 2 * u
        drawPath(Path().apply { moveTo(x + 3 * u, h / 2 - alto / 2); lineTo(x, h / 2 - alto / 2); lineTo(x, h / 2 + alto / 2); lineTo(x + 3 * u, h / 2 + alto / 2) },
            (if (e.presionado) (if (oscuro) CIAN else azul) else azul.copy(alpha = 0.4f)), style = Stroke((if (e.presionado) 1.3f else 0.9f) * u, join = StrokeJoin.Miter))
    }

    /** El doble galón del visor, «»», el de adelante más marcado. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        val azul = tintaBotonTexto(oscuro)
        listOf(-3.5f to 0.45f, 1.5f to 1f).forEach { (dx, a) ->
            drawPath(Path().apply { moveTo(c.x + dx * u - 2.5f * u, c.y - 4.5f * u); lineTo(c.x + dx * u + 2 * u, c.y); lineTo(c.x + dx * u - 2.5f * u, c.y + 4.5f * u) },
                azul.copy(alpha = a), style = Stroke(1.4f * u, cap = StrokeCap.Square, join = StrokeJoin.Miter))
        }
    }

    /** El destello de cuatro puntas de una estrella: fijo, lleno de ámbar (con su halo en oscuro); sin fijar, en contorno tenue. */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        val p = estrella(c, 9 * u, 4, 0.32f)
        if (fija) {
            val ambar = if (oscuro) AMBAR else Color(0xFFD9550F)
            if (oscuro) drawCircle(ambar.copy(alpha = 0.22f), 6 * u, c)
            drawPath(p, ambar)
        } else drawPath(p, (if (oscuro) Color(0xFF8E9AB8) else Color(0xFF4E5A70)).copy(alpha = 0.6f), style = Stroke(1 * u, join = StrokeJoin.Miter))
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) Color(0xFF8E9AB8) else Color(0xFF4E5A70)

    /**
     * La pantalla de exploración sin señal: el panel ochavado con su filo, una rejilla de puntos de referencia y las tres luces
     * de estado del canto de arriba, apagadas.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val todo = ochavado(Rect(0f, 0f, w, h), 8 * u)
        drawPath(todo, pantalla(oscuro))
        drawPath(todo, filo(oscuro), style = Stroke(1 * u))
        // La rejilla de puntos: una raya de puntos por renglón.
        val paso = maxOf(14 * u, 1f)
        val puntos = Path()
        var y = paso; var n = 0
        while (y < h - 4 * u && n < 100) { puntos.moveTo(paso, y); puntos.lineTo(w - 4 * u, y); y += paso; n++ }
        drawPath(puntos, tintaBotonTexto(oscuro).copy(alpha = if (oscuro) 0.22f else 0.16f), style = Stroke(1.1f * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.01f, paso))))
        // Las luces, apagadas.
        listOf(VERDE, AMBAR, CIAN).forEachIndexed { i, cl -> drawCircle(mezcla(cl, if (oscuro) Color(0xFF1A1E26) else Color(0xFF9AA4B4), 0.7f), 1 * u, Offset(w - 24 * u + i * 5 * u, 3.5f * u)) }
    }

    /**
     * La órbita sin planeta: la elipse punteada de la trayectoria, el hueco del planeta marcado con su círculo a trazos y la
     * cruz de puntería, un satélite que sigue dando vueltas y dos estrellas lejanas.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val l = tintaVacio(oscuro)
        val azul = tintaBotonTexto(oscuro)
        val c = Offset(f(0.5f), f(0.52f))
        rotate(-14f, c) { drawOval(l, Offset(f(0.04f), f(0.32f)), Size(f(0.92f), f(0.4f)), style = Stroke(1.1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(f(0.05f), f(0.04f))))) }
        drawCircle(azul, f(0.13f), c, style = Stroke(1.1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(f(0.04f), f(0.035f)))))
        val cruz = Path().apply { moveTo(c.x - f(0.07f), c.y); lineTo(c.x + f(0.07f), c.y); moveTo(c.x, c.y - f(0.07f)); lineTo(c.x, c.y + f(0.07f)) }
        drawPath(cruz, azul.copy(alpha = 0.6f), style = Stroke(0.8f * u))
        // El satélite, en la órbita.
        val sat = Offset(f(0.16f), f(0.64f))
        drawRect(l, Offset(sat.x - f(0.03f), sat.y - f(0.03f)), Size(f(0.06f), f(0.06f)))
        drawLine(azul, Offset(sat.x - f(0.1f), sat.y), Offset(sat.x + f(0.1f), sat.y), 1.6f * u)
        // Las estrellas lejanas.
        drawPath(estrella(Offset(f(0.84f), f(0.14f)), f(0.08f), 4, 0.32f), if (oscuro) AMBAR else Color(0xFFD9550F))
        drawCircle(l, f(0.02f), Offset(f(0.2f), f(0.18f)))
    }

    override val margenAviso: Dp = 28.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) Color(0xFFE8ECF8) else Color(0xFF0D1526)

    /**
     * La alerta de la consola de a bordo: el panel con la barra azul a la izquierda y, arriba, su señal (el triángulo ámbar
     * con su admiración o, según el aviso, el anillo de información), con una fila de tres luces de estado debajo.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val p = ochavado(Rect(0f, 0f, w, h), 5 * u)
        drawPath(p, if (oscuro) Color(0xFF0B1226) else Color(0xFFE9EEF6))
        drawPath(p, filo(oscuro), style = Stroke(0.8f * u))
        val azul = tintaBotonTexto(oscuro)
        drawRect(azul, Offset(3 * u, 5 * u), Size(2 * u, h - 10 * u))
        val c = Offset(16 * u, 13 * u)
        if (e.semilla % 2 == 0) {
            val ambar = if (oscuro) AMBAR else Color(0xFFC24A0C)
            drawPath(Path().apply { moveTo(c.x, c.y - 6 * u); lineTo(c.x + 6 * u, c.y + 4.5f * u); lineTo(c.x - 6 * u, c.y + 4.5f * u); close() }, ambar, style = Stroke(1.2f * u, join = StrokeJoin.Miter))
            drawLine(ambar, Offset(c.x, c.y - 2.2f * u), Offset(c.x, c.y + 1 * u), 1.3f * u)
            drawRect(ambar, Offset(c.x - 0.65f * u, c.y + 2 * u), Size(1.3f * u, 1.3f * u))
        } else {
            drawCircle(azul, 5.5f * u, c, style = Stroke(1.2f * u))
            drawRect(azul, Offset(c.x - 0.65f * u, c.y - 3 * u), Size(1.3f * u, 1.3f * u))
            drawLine(azul, Offset(c.x, c.y - 0.8f * u), Offset(c.x, c.y + 3.2f * u), 1.3f * u)
        }
        if (h > 30 * u) listOf(VERDE, AMBAR, CIAN).forEachIndexed { i, cl ->
            drawCircle(if (oscuro) cl else mezcla(cl, Color(0xFF0D1526), 0.35f), 0.9f * u, Offset(c.x - 4 * u + i * 4 * u, 24 * u))
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFE8F4FF) else Color(0xFF0D1526)
    override fun rotuloCifra(oscuro: Boolean): Color = tintaBotonTexto(oscuro)

    /** La lectura de telemetría: el panel ochavado con su filo, la regla de marcas en el canto izquierdo y la luz de estado arriba. */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val p = ochavado(Rect(0f, 0f, w, h), 6 * u)
        drawPath(p, pantalla(oscuro))
        drawPath(p, filo(oscuro), style = Stroke(1 * u))
        val paso = maxOf(4 * u, 1f)
        val marcas = Path()
        var y = 9 * u; var k = 0
        while (y < h - 9 * u && k < 100) { marcas.moveTo(2 * u, y); marcas.lineTo((if (k % 4 == 0) 5.5f else 3.8f) * u, y); y += paso; k++ }
        drawPath(marcas, tintaBotonTexto(oscuro).copy(alpha = 0.45f), style = Stroke(0.6f * u))
        val luz = Offset(w - 5 * u, 5 * u)
        if (oscuro) drawCircle(CIAN.copy(alpha = 0.25f), 2.4f * u, luz)
        drawCircle(if (oscuro) CIAN else Color(0xFF1B8FB0), 1 * u, luz)
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    /** El fondo de cada tarjeta: la pantalla de la nave; la noche, el cristal del ojo de buey, oscuro también de día. */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (tipo == TipoTarjeta.NOCHE) CRISTAL else pantalla(oscuro)

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color =
        if (tipo == TipoTarjeta.NOCHE || oscuro) Color(0xFFE8F4FF) else Color(0xFF0D1526)
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when {
        tipo == TipoTarjeta.NOCHE -> Color(0xFF9AA8C8)
        oscuro -> Color(0xFF8E9AB8)
        else -> Color(0xFF4E5A70)
    }
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.NOCHE -> CIAN
        TipoTarjeta.PENDIENTE -> if (oscuro) AMBAR else Color(0xFF8A5200)
        else -> tintaBotonTexto(oscuro)
    }

    /**
     * Las tarjetas de la nave. La cifra de Hoy es el **módulo de instrumento**: el panel con la esquina de abajo a la izquierda
     * cortada, la hilera de cinco segmentos de estado en el canto de arriba y las tres estrías de agarre en el canto de la
     * derecha; destacada, la barra ámbar del canto izquierdo se enciende y los segmentos se vuelven ámbar. «Te falta hoy» es la
     * **alerta de la consola**: el panel ochavado con doble filo, las cuatro escuadras gruesas en ámbar y los tres galones de
     * alerta arriba a la derecha; completa, todo pasa a verde y el anillo de estado se enciende a la derecha. La noche es el
     * **ojo de buey**: el cristal oscuro con unas pocas estrellas y el borde de un planeta que asoma, iluminado, por la esquina
     * de abajo a la derecha; sellada, la hilera de luces verdes de atraque se enciende al pie y el filo se vuelve verde. Vida es
     * el **módulo de la estación**: el panel con la franja rayada de identificación en la esquina de arriba y el conector al
     * pie. Al tocarlas el panel se tiñe del color de su luz.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val base = fondoTarjeta(e.tipo, oscuro)
        val azul = tintaBotonTexto(oscuro)
        val ambar = if (oscuro) AMBAR else Color(0xFFB86E00)
        val verde = if (oscuro) VERDE else Color(0xFF128A55)
        when (e.tipo) {
            TipoTarjeta.METRICA -> {
                val ch = 8 * u
                val p = Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h); lineTo(ch, h); lineTo(0f, h - ch); close() }
                drawPath(p, if (e.presionado) mezcla(base, azul, 0.1f) else base)
                drawPath(p, if (e.destacado) ambar else filo(oscuro), style = Stroke(1 * u))
                // Los segmentos de estado del canto de arriba.
                val luz = if (e.destacado) ambar else azul.copy(alpha = 0.5f)
                for (i in 0 until 5) drawRect(if (i < 3 || e.destacado) luz else filo(oscuro), Offset(12 * u + i * 6 * u, 3 * u), Size(4.5f * u, 2 * u))
                // Las estrías de agarre del canto de la derecha.
                for (i in -1..1) drawLine(filo(oscuro), Offset(w - 5 * u, h / 2 + i * 3 * u), Offset(w - 2 * u, h / 2 + i * 3 * u), 1 * u, StrokeCap.Round)
                // Destacada: la barra ámbar encendida del canto izquierdo.
                if (e.destacado) {
                    drawRect(ambar.copy(alpha = 0.25f), Offset(1 * u, 8 * u), Size(4 * u, h - ch - 12 * u))
                    drawRect(ambar, Offset(1.8f * u, 8 * u), Size(2 * u, h - ch - 12 * u))
                }
            }
            TipoTarjeta.PENDIENTE -> {
                val luz = if (e.destacado) verde else ambar
                val p = ochavado(Rect(0f, 0f, w, h), 7 * u)
                drawPath(p, if (e.presionado) mezcla(base, luz, 0.08f) else base)
                drawPath(p, filo(oscuro), style = Stroke(1 * u))
                drawPath(ochavado(Rect(3 * u, 3 * u, w - 3 * u, h - 3 * u), 5 * u), luz.copy(alpha = 0.4f), style = Stroke(0.7f * u))
                // Las cuatro escuadras gruesas.
                val l = minOf(16 * u, h * 0.3f)
                val esquinas = listOf(Offset(3 * u, 3 * u) to Offset(1f, 1f), Offset(w - 3 * u, 3 * u) to Offset(-1f, 1f),
                    Offset(3 * u, h - 3 * u) to Offset(1f, -1f), Offset(w - 3 * u, h - 3 * u) to Offset(-1f, -1f))
                esquinas.forEach { (o, d) ->
                    drawPath(Path().apply { moveTo(o.x, o.y + d.y * l); lineTo(o.x, o.y + d.y * 5 * u); lineTo(o.x + d.x * 5 * u, o.y); lineTo(o.x + d.x * l, o.y) }, luz,
                        style = Stroke(2.2f * u, join = StrokeJoin.Miter))
                }
                if (!e.destacado) {
                    // Los galones de alerta, arriba a la derecha.
                    for (i in 0 until 3) {
                        val x = w - 40 * u + i * 5 * u
                        drawPath(Path().apply { moveTo(x, 3.5f * u); lineTo(x + 2.5f * u, 6 * u); lineTo(x, 8.5f * u) }, luz.copy(alpha = 0.5f + 0.25f * i),
                            style = Stroke(1.3f * u, join = StrokeJoin.Miter))
                    }
                } else {
                    // Completa: el anillo de estado, encendido.
                    val r = minOf(h * 0.26f, 17 * u)
                    val c = Offset(w - r - 16 * u, h / 2)
                    drawCircle(luz.copy(alpha = 0.18f), r + 3 * u, c)
                    drawCircle(luz, r, c, style = Stroke(1.6f * u))
                    for (k in 0 until 8) rotate(k * 45f, c) { drawLine(luz.copy(alpha = 0.6f), Offset(c.x, c.y - r + 2.5f * u), Offset(c.x, c.y - r + 4.5f * u), 1 * u) }
                    drawCircle(luz, r * 0.25f, c)
                }
            }
            TipoTarjeta.NOCHE -> {
                val radio = CornerRadius(10 * u)
                drawRoundRect(base, Offset.Zero, size, radio)
                clipPath(Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), radio)) }) {
                    // El borde del planeta, iluminado, por la esquina de abajo a la derecha.
                    val c = Offset(w + 8 * u, h + 60 * u)
                    val r = 82 * u
                    drawCircle(Brush.radialGradient(listOf(CIAN.copy(alpha = 0.22f), Color.Transparent), c, r + 16 * u), r + 16 * u, c)
                    drawCircle(Color(0xFF0E1A33), r, c)
                    drawCircle(CIAN.copy(alpha = 0.55f), r, c, style = Stroke(1 * u))
                    // Unas pocas estrellas, lejos del texto: por los cantos.
                    val n = (w / (22 * u)).toInt().coerceIn(4, 24)
                    for (i in 0 until n) {
                        val arriba = i % 2 == 0
                        val x = w * azar(e.semilla + 91, i)
                        val y = if (arriba) (2 + 7 * azar(e.semilla + 92, i)) * u else h - (2 + 6 * azar(e.semilla + 93, i)) * u
                        drawCircle(Color.White.copy(alpha = 0.35f + 0.5f * azar(e.semilla + 94, i)), (0.4f + 0.6f * azar(e.semilla + 95, i)) * u, Offset(x, y))
                    }
                    // Sellada: las luces verdes de atraque al pie.
                    if (e.destacado) for (i in 0 until 7) {
                        val p = Offset(w * 0.15f + i * w * 0.07f, h - 3.5f * u)
                        drawCircle(VERDE.copy(alpha = 0.3f), 2.2f * u, p)
                        drawCircle(VERDE, 1 * u, p)
                    }
                }
                drawRoundRect(if (e.destacado) VERDE else Color(0xFF26324F), Offset(0.6f * u, 0.6f * u), Size(w - 1.2f * u, h - 1.2f * u), radio,
                    style = Stroke(if (e.destacado) 1.6f * u else 1.2f * u))
            }
            TipoTarjeta.VIDA -> {
                val radio = CornerRadius(4 * u)
                drawRoundRect(if (e.presionado) mezcla(base, azul, 0.1f) else base, Offset.Zero, size, radio)
                drawRoundRect(filo(oscuro), Offset.Zero, size, radio, style = Stroke(1 * u))
                // La franja rayada de identificación, en la esquina de arriba a la derecha.
                val k = 16 * u
                clipPath(Path().apply { moveTo(w - k, 0f); lineTo(w, 0f); lineTo(w, k); close() }) {
                    clipPath(Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), radio)) }) {
                        val paso = maxOf(4 * u, 1f)
                        var x = w - k - 2 * k
                        var n = 0
                        while (x < w && n < 60) {
                            drawPath(Path().apply { moveTo(x, 0f); lineTo(x + 2 * u, 0f); lineTo(x + 2 * u + k, k); lineTo(x + k, k); close() }, azul.copy(alpha = 0.55f))
                            x += paso; n++
                        }
                    }
                }
                // El conector del pie, con sus clavijas.
                val c = Rect(w / 2 - 9 * u, h - 4 * u, w / 2 + 9 * u, h)
                drawRoundRect(filo(oscuro), c.topLeft, c.size, CornerRadius(1.2f * u, 1.2f * u))
                for (i in 0 until 4) drawRect(if (i == 0) CIAN else (if (oscuro) PANEL_OSC.copy(alpha = 0.5f) else PANEL_OSC), Offset(c.left + 2.5f * u + i * 3.7f * u, c.top + 1.2f * u), Size(1.6f * u, 1.6f * u))
            }
        }
    }
}
