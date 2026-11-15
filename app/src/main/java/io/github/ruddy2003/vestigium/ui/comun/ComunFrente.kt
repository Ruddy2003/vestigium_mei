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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TrazosFrente
import io.github.ruddy2003.vestigium.ui.biblioteca.textoEn
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Frente exterior
// =====================================================================

/**
 * Las rayas de los renglones de un campo de varios renglones, de abajo arriba: la primera ([k] = 0) al pie del último
 * renglón, a [margenY] del canto de abajo, y luego una cada 24 sp mientras quede sitio arriba (16 dp bajo [margenY]).
 */
private inline fun DrawScope.renglonesFrente(margenY: Dp, raya: (k: Int, y: Float) -> Unit) {
    val paso = maxOf(24.sp.toPx(), 1f)
    val tope = margenY.toPx() + 16.dp.toPx()
    var y = size.height - margenY.toPx() + 1.dp.toPx()
    var k = 0
    while (y > tope && k < 200) { raya(k, y); y -= paso; k++ }
}

/** La razón de contraste entre dos colores opacos (WCAG 2): de 1 (iguales) a 21 (negro sobre blanco). */
private fun contrasteFrente(a: Color, b: Color): Float {
    val la = a.luminance(); val lb = b.luminance()
    return (maxOf(la, lb) + 0.05f) / (minOf(la, lb) + 0.05f)
}

/**
 * El color [c] de un texto, tal cual si se lee sobre [fondo] (contraste de al menos [minimo]:1); si no, llevado poco a poco
 * hacia el negro o el blanco hasta que se lea. Con [tenue] se apaga antes hacia el fondo, lo más que deja ese contraste.
 */
private fun legibleFrente(c: Color, fondo: Color, tenue: Boolean = false, minimo: Float = 4.6f): Color {
    val opaco = c.copy(alpha = 1f)
    if (tenue) for (k in 8 downTo 1) {
        val m = mezcla(opaco, fondo, k * 0.05f)
        if (contrasteFrente(m, fondo) >= minimo) return m
    }
    if (contrasteFrente(opaco, fondo) >= minimo) return opaco
    val hacia = if (fondo.luminance() > 0.18f) Color.Black else Color.White
    for (k in 1..20) {
        val m = mezcla(opaco, hacia, k * 0.05f)
        if (contrasteFrente(m, fondo) >= minimo) return m
    }
    return hacia
}

/** El ancho, en píxeles, de [texto] en una sola línea con el mismo estilo con que lo dibuja [textoEn]. */
private fun anchoFrente(medidor: TextMeasurer, texto: String, letra: FontFamily, tam: Float, peso: FontWeight = FontWeight.Normal, espaciado: Float = 0f): Float =
    if (texto.isBlank()) 0f
    else medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = tam.sp, lineHeight = (tam * 1.05f).sp, fontWeight = peso, letterSpacing = espaciado.sp),
        maxLines = 1, softWrap = false).size.width.toFloat()

/** El recuadro donde [textoEn] deja un texto de [ancho] alineado a la izquierda desde [x], sin pasar de [tope]. */
private fun cajaFrente(ancho: Float, x: Float, arriba: Float, abajo: Float, tope: Float): Rect =
    Rect(x, arriba, minOf(x + ancho + 2f, maxOf(tope, x + 1f)), abajo)

/**
 * Frente exterior: las piezas comunes de la sala de mando de la red. Todo es la consola curva color hueso de los 70: la cara
 * de baquelita y cromo, la fila de interruptores de palanca y sus pilotos ámbar, la perilla, la pantalla redonda de radar con
 * su retícula, la cinta perforada del teletipo y el cartucho de datos. El campo es la ventanilla de lectura de la consola:
 * la cara hueso con su moldura de cromo, la ventanilla hundida con el renglón grabado, la palanca a la izquierda (abajo en
 * reposo, arriba con el foco) y el piloto a la derecha. De noche es la misma sala con las luces bajas.
 */
object ComunFrente : DisenoComun {
    private val BAQUELITA = TrazosFrente.BAQUELITA
    private val PANTALLA = TrazosFrente.PANTALLA
    private val TRAZO = TrazosFrente.RADAR
    private val ROJO = TrazosFrente.PILOTO_ROJO
    private val AMBAR = TrazosFrente.PILOTO_AMBAR
    /** La letra en relieve de la cinta de rotular: hueso sobre la cinta negra. */
    private val LETRA_CINTA = Color(0xFFEFE8D8)

    // ---------------------------------------------------------------- los colores de la sala (de día y de noche)

    /** La cara de la consola: hueso de día; de noche, la misma cara en penumbra. */
    private fun cara(oscuro: Boolean): Color = if (oscuro) Color(0xFF2F3234) else Color(0xFFE9E2D2)
    /** La cara con la luz de arriba (las tapas, la cara del tambor, la ventanilla elegida). */
    private fun caraLuz(oscuro: Boolean): Color = if (oscuro) Color(0xFF3B3E40) else Color(0xFFF6F1E6)
    /** La ventanilla hundida en la consola. */
    private fun hundido(oscuro: Boolean): Color = if (oscuro) Color(0xFF212528) else Color(0xFFDCD3C1)
    /** El panel de las tarjetas: la superficie de la consola. */
    private fun panel(oscuro: Boolean): Color = if (oscuro) Color(0xFF22272A) else Color(0xFFF0EBE0)
    /** El filo de las piezas. */
    private fun borde(oscuro: Boolean): Color = if (oscuro) Color(0xFF3A4249) else Color(0xFFA69E8E)
    private fun cromo(oscuro: Boolean): Color = if (oscuro) Color(0xFF6E6D69) else Color(0xFFB9B6AE)
    private fun cromoLuz(oscuro: Boolean): Color = if (oscuro) Color(0xFF8E8C86) else Color(0xFFE6E4DE)
    private fun cromoSombra(oscuro: Boolean): Color = if (oscuro) Color(0xFF45443F) else Color(0xFF7D7A73)
    /** La baquelita de perillas y placas: de noche, apenas más oscura que la cara para que se siga viendo. */
    private fun baquelita(oscuro: Boolean): Color = if (oscuro) Color(0xFF171513) else BAQUELITA
    private fun tinta(oscuro: Boolean): Color = if (oscuro) Color(0xFFE7E1D4) else Color(0xFF23252A)
    private fun tenue(oscuro: Boolean): Color = if (oscuro) Color(0xFFA8A49A) else Color(0xFF5A5D63)
    /** El ámbar de los rótulos: quemado de día, encendido de noche. */
    private fun ambar(oscuro: Boolean): Color = if (oscuro) Color(0xFFF0A04B) else Color(0xFF9C4E0C)
    private fun verde(oscuro: Boolean): Color = if (oscuro) Color(0xFF8FC79A) else Color(0xFF3F6E4B)
    private fun acero(oscuro: Boolean): Color = if (oscuro) Color(0xFF9FB4C0) else Color(0xFF3E4C57)
    /** Las rayas grabadas y las marcas de escala. */
    private fun marca(oscuro: Boolean): Color = if (oscuro) Color(0xFF8E8A80) else Color(0xFF6A6358)

    // ---------------------------------------------------------------- las piezas de la consola

    /**
     * Un piloto de la consola en [c]: el aro de cromo con su brillo arriba, el vidrio de [luz] (apagado, hundido hacia la
     * baquelita) y, si está [encendido], su halo y el reflejo.
     */
    private fun DrawScope.piloto(c: Offset, r: Float, luz: Color, encendido: Boolean, oscuro: Boolean) {
        val u = density
        if (encendido) {
            drawCircle(luz.copy(alpha = if (oscuro) 0.2f else 0.14f), r * 2.6f, c)
            drawCircle(luz.copy(alpha = if (oscuro) 0.34f else 0.24f), r * 1.75f, c)
        }
        val aro = r * 1.38f
        drawCircle(cromoSombra(oscuro), aro, c)
        drawCircle(cromo(oscuro), aro - maxOf(r * 0.14f, 0.25f * u), c, style = Stroke(maxOf(r * 0.26f, 0.5f * u)))
        drawArc(cromoLuz(oscuro), 200f, 110f, false, Offset(c.x - aro, c.y - aro), Size(2 * aro, 2 * aro), style = Stroke(maxOf(r * 0.16f, 0.4f * u)))
        drawCircle(if (encendido) luz else mezcla(luz, BAQUELITA, if (oscuro) 0.74f else 0.62f), r, c)
        drawCircle(Color.White.copy(alpha = if (encendido) 0.75f else 0.22f), r * 0.32f, Offset(c.x - r * 0.36f, c.y - r * 0.36f))
    }

    /**
     * Un interruptor de palanca visto de frente, en [c]: la sombra que echa, la tuerca de cromo con su agujero y la palanca de
     * cromo con su punta de bola, hacia [hacia] (un vector de largo 1; arriba es encendido). [r] es el radio de la tuerca.
     */
    private fun DrawScope.palanca(c: Offset, r: Float, hacia: Offset, oscuro: Boolean, a: Float = 1f) {
        val u = density
        val punta = c + hacia * (r * 2.1f)
        drawLine(Color.Black.copy(alpha = (if (oscuro) 0.4f else 0.2f) * a), c + Offset(r * 0.3f, r * 0.35f), punta + Offset(r * 0.4f, r * 0.5f), r * 0.8f, StrokeCap.Round)
        drawCircle(cromo(oscuro).copy(alpha = a), r, c)
        drawCircle(cromoSombra(oscuro).copy(alpha = a), r, c, style = Stroke(maxOf(r * 0.2f, 0.5f * u)))
        drawCircle(baquelita(oscuro).copy(alpha = a), r * 0.42f, c)
        drawLine(cromoSombra(oscuro).copy(alpha = a), c, punta, r * 0.64f, StrokeCap.Round)
        drawLine(cromoLuz(oscuro).copy(alpha = a), c + Offset(-r * 0.08f, -r * 0.08f), punta + Offset(-r * 0.08f, -r * 0.08f), r * 0.26f, StrokeCap.Round)
        drawCircle(cromo(oscuro).copy(alpha = a), r * 0.56f, punta)
        drawCircle(Color.White.copy(alpha = 0.6f * a), r * 0.2f, Offset(punta.x - r * 0.18f, punta.y - r * 0.18f))
    }

    /** La ranura grabada en la consola, de [x0] a [x1] a la altura [y]: el trazo hundido y, debajo, su filo de luz. */
    private fun DrawScope.ranura(x0: Float, x1: Float, y: Float, oscuro: Boolean, a: Float = 1f, efecto: PathEffect? = null) {
        if (x1 <= x0) return
        val u = density
        drawLine((if (oscuro) Color.Black else Color(0xFF5A5246)).copy(alpha = (if (oscuro) 0.6f else 0.42f) * a), Offset(x0, y), Offset(x1, y), 0.9f * u, StrokeCap.Round, efecto)
        drawLine(Color.White.copy(alpha = (if (oscuro) 0.07f else 0.65f) * a), Offset(x0, y + 0.9f * u), Offset(x1, y + 0.9f * u), 0.6f * u, StrokeCap.Round, efecto)
    }

    /**
     * Una perilla de baquelita en [c]: su sombra, la falda de cromo con sus dieciséis estrías, la tapa negra con su brillo y la
     * raya índice hacia [angulo] (en grados; 0 es a la derecha).
     */
    private fun DrawScope.perilla(c: Offset, r: Float, angulo: Float, oscuro: Boolean) {
        val u = density
        drawCircle(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f), r, c + Offset(r * 0.12f, r * 0.18f))
        drawCircle(cromo(oscuro), r, c)
        for (k in 0 until 16) {
            val a = Math.toRadians(k * 22.5).toFloat()
            val d = Offset(cos(a), sin(a))
            drawLine(cromoSombra(oscuro), c + d * (r * 0.8f), c + d * r, maxOf(r * 0.08f, 0.5f * u))
        }
        val tapa = r * 0.74f
        drawCircle(if (oscuro) Color(0xFF3A352F) else BAQUELITA, tapa, c)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.08f else 0.14f), tapa * 0.52f, Offset(c.x - tapa * 0.3f, c.y - tapa * 0.32f))
        val a = Math.toRadians(angulo.toDouble()).toFloat()
        val d = Offset(cos(a), sin(a))
        drawLine(Color(0xFFF6F1E6), c + d * (tapa * 0.18f), c + d * (tapa * 0.92f), maxOf(r * 0.14f, 0.7f * u), StrokeCap.Round)
    }

    /**
     * La pantalla redonda de radar en [c]: el aro de cromo (con sus marcas de rumbo si cabe), el vidrio verde oscuro, la
     * retícula (dos anillos y la cruz), el barrido en [angulo] (grados) con su estela y, si [ecos], dos contactos recién
     * barridos. Todo a [a] de opacidad.
     */
    private fun DrawScope.radar(c: Offset, r: Float, angulo: Float, oscuro: Boolean, ecos: Boolean = false, semilla: Int = 0, a: Float = 1f) {
        val u = density
        val g = maxOf(r * 0.09f, 0.5f * u)
        val aro = r + g * 1.6f
        drawCircle(cromo(oscuro).copy(alpha = a), aro, c)
        drawCircle(cromoSombra(oscuro).copy(alpha = a), aro, c, style = Stroke(g * 0.6f))
        if (r > 8 * u) for (k in 0 until 12) {
            val ar = Math.toRadians(k * 30.0).toFloat()
            val d = Offset(cos(ar), sin(ar))
            drawLine(cromoSombra(oscuro).copy(alpha = a), c + d * (r + g * 0.3f), c + d * (aro - g * 0.3f), g * 0.5f)
        }
        drawCircle(PANTALLA.copy(alpha = a), r, c)
        val reticula = TRAZO.copy(alpha = 0.32f * a)
        drawCircle(reticula, r * 0.66f, c, style = Stroke(g * 0.5f))
        drawCircle(reticula, r * 0.33f, c, style = Stroke(g * 0.5f))
        drawLine(reticula, Offset(c.x - r, c.y), Offset(c.x + r, c.y), g * 0.5f)
        drawLine(reticula, Offset(c.x, c.y - r), Offset(c.x, c.y + r), g * 0.5f)
        // La estela del barrido: seis tajadas cada vez más tenues, detrás de la línea.
        for (k in 0 until 6) drawArc(TRAZO.copy(alpha = (0.34f - k * 0.055f) * a), angulo - (k + 1) * 9f, 9f, true, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r))
        val ar = Math.toRadians(angulo.toDouble()).toFloat()
        drawLine(TRAZO.copy(alpha = 0.9f * a), c, Offset(c.x + r * cos(ar), c.y + r * sin(ar)), g * 0.7f, StrokeCap.Round)
        if (ecos) for (k in 0 until 2) {
            val ae = ar - (0.4f + 0.9f * azar(semilla, k))
            val de = r * (0.35f + 0.5f * azar(semilla, k + 9))
            drawCircle(TRAZO.copy(alpha = 0.85f * a), maxOf(r * 0.06f, 0.6f * u), Offset(c.x + de * cos(ae), c.y + de * sin(ae)))
        }
        // El reflejo del vidrio, arriba a la izquierda.
        drawArc(Color.White.copy(alpha = 0.12f * a), 200f, 70f, false, Offset(c.x - r * 0.78f, c.y - r * 0.78f), Size(r * 1.56f, r * 1.56f),
            style = Stroke(g * 0.8f, cap = StrokeCap.Round))
    }

    /** Un tornillo de cabeza redonda con su hueco hexagonal, en [c]. */
    private fun DrawScope.tornillo(c: Offset, r: Float, oscuro: Boolean) {
        drawCircle(cromo(oscuro), r, c)
        drawCircle(cromoSombra(oscuro), r, c, style = Stroke(maxOf(r * 0.18f, 0.4f * density)))
        drawPath(Path().apply {
            for (k in 0 until 6) {
                val a = Math.toRadians(k * 60.0 + 30.0).toFloat()
                val x = c.x + r * 0.46f * cos(a); val y = c.y + r * 0.46f * sin(a)
                if (k == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }, cromoSombra(oscuro))
    }

    /**
     * Un tramo de cinta perforada del teletipo en [r]: el papel, la hilera fina de arrastre por el centro y los agujeros del
     * mensaje en cuatro carriles, sacados de [semilla]; [vertical] si corre de arriba abajo. Los agujeros son de [hueco].
     */
    private fun DrawScope.cinta(r: Rect, papel: Color, hueco: Color, semilla: Int, vertical: Boolean) {
        val u = density
        drawRect(papel, r.topLeft, r.size)
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        val paso = maxOf(2.4f * u, 1f)
        val rad = minOf(0.8f * u, ancho * 0.09f)
        val n = (largo / paso).toInt().coerceIn(0, 200)
        val agujeros = Path(); val arrastre = Path()
        val carriles = listOf(-0.36f, -0.19f, 0.19f, 0.36f)
        for (k in 0 until n) {
            val a = (k + 0.5f) * paso
            for (j in -1 until carriles.size) {
                if (j >= 0 && azar(semilla, k * 5 + j) <= 0.5f) continue
                val f = if (j < 0) 0f else carriles[j]
                val x = if (vertical) r.center.x + f * ancho else r.left + a
                val y = if (vertical) r.top + a else r.center.y + f * ancho
                if (j < 0) arrastre.addOval(Rect(Offset(x, y), rad * 0.55f)) else agujeros.addOval(Rect(Offset(x, y), rad))
            }
        }
        drawPath(arrastre, hueco.copy(alpha = hueco.alpha * 0.7f))
        drawPath(agujeros, hueco)
    }

    /**
     * La cara de una consola en [r], de esquinas [radio]: el hueso con la luz que baja de arriba y el canto de abajo en sombra,
     * y la moldura de cromo por dentro con su filo de luz.
     */
    private fun DrawScope.caraConsola(r: Rect, radio: Float, oscuro: Boolean, apagada: Boolean = false) {
        val u = density
        fun c(x: Color) = if (apagada) mezcla(x, if (oscuro) Color(0xFF3A3D40) else Color(0xFFB4B0A8), 0.45f) else x
        drawRoundRect(c(cara(oscuro)), r.topLeft, r.size, CornerRadius(radio))
        drawRoundRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.05f else 0.42f), Color.Transparent,
            Color.Black.copy(alpha = if (oscuro) 0.22f else 0.08f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(radio))
        val m = 1.6f * u
        drawRoundRect(c(cromo(oscuro)), Offset(r.left + m, r.top + m), Size(r.width - 2 * m, r.height - 2 * m), CornerRadius(maxOf(radio - m, 1f)), style = Stroke(1.3f * u))
        drawLine(c(cromoLuz(oscuro)), Offset(r.left + radio, r.top + m - 0.3f * u), Offset(r.right - radio, r.top + m - 0.3f * u), 0.6f * u)
    }

    /** Una ventanilla hundida en la consola: el fondo, la sombra de su canto de arriba y el filo de luz de su labio de abajo. */
    private fun DrawScope.ventanilla(r: Rect, radio: Float, color: Color, oscuro: Boolean): Path {
        val u = density
        val p = Path().apply { addRoundRect(RoundRect(r, CornerRadius(radio))) }
        drawPath(p, color)
        clipPath(p) {
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f), Color.Transparent), r.top, r.top + 6 * u),
                r.topLeft, Size(r.width, 6 * u))
        }
        drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.55f), Offset(r.left + radio, r.bottom + 0.6f * u), Offset(r.right - radio, r.bottom + 0.6f * u), 0.7f * u)
        return p
    }

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 18.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = tinta(oscuro)
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9A968C) else Color(0xFF66686D)
    override fun rotuloCampo(oscuro: Boolean): Color = ambar(oscuro)
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFFF7A6E) else Color(0xFFA8201A)

    /**
     * La ventanilla de lectura de la consola: la cara hueso con la luz de arriba y su moldura de cromo, la ventanilla hundida
     * con el renglón grabado (uno por renglón si son varios), la palanca en el canto izquierdo y el piloto en el derecho. Con
     * el foco la palanca sube, la ventanilla se marca en ámbar y el piloto ámbar se enciende; con un error, el marco y el
     * piloto van en rojo de botón; apagado, todo se va al gris y la palanca queda abajo.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val radio = minOf(13 * u, h / 2)
        caraConsola(Rect(0f, 0f, w, h), radio, oscuro, apagada = !e.habilitado)
        // La ventanilla hundida donde se escribe.
        val b = 4.2f * u
        val fondo = hundido(oscuro).let { if (e.habilitado) it else mezcla(it, if (oscuro) Color(0xFF3A3D40) else Color(0xFFB4B0A8), 0.35f) }
        val vent = ventanilla(Rect(b, b, w - b, h - b), maxOf(radio - b, 2 * u), fondo, oscuro)
        // El renglón grabado: más marcado con el foco.
        val izq = margenCampoX.toPx(); val der = w - margenCampoX.toPx()
        if (e.multilinea) renglonesFrente(margenCampoY) { _, y -> ranura(izq, der, y, oscuro, if (e.enfocado) 0.85f else 0.6f) }
        else ranura(izq, der, h - 9.5f * u, oscuro, if (e.enfocado) 0.9f else 0.6f)
        // Con el foco o con un error, la ventanilla se marca en su color.
        if (e.enfocado || e.error) drawPath(vent, if (e.error) errorCampo(oscuro) else rotuloCampo(oscuro), style = Stroke(1.3f * u))
        // La palanca del canto izquierdo: abajo en reposo, arriba con el foco.
        val arriba = e.enfocado && e.habilitado
        val yc = if (e.multilinea) minOf(h / 2, 22 * u) else h / 2
        palanca(Offset(11 * u, yc + (if (arriba) 2.6f else -2.6f) * u), 2.6f * u, Offset(0f, if (arriba) -1f else 1f), oscuro, if (e.habilitado) 1f else 0.5f)
        // El piloto del canto derecho: ámbar con el foco, rojo con un error.
        piloto(Offset(w - 11 * u, yc), 2.2f * u, if (e.error) ROJO else AMBAR, (e.enfocado || e.error) && e.habilitado, oscuro)
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * La junta entre dos paneles de la consola: la ranura grabada de lado a lado, cortada al centro por una pantallita de
     * radar con su barrido, entre las dos marcas de sus topes. Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val a = if (e.tenue) 0.5f else 1f
        val cx = w / 2
        val r = 3.3f * u
        val hueco = r + 6 * u
        ranura(3 * u, cx - hueco, y, oscuro, 0.8f * a)
        ranura(cx + hueco, w - 3 * u, y, oscuro, 0.8f * a)
        listOf(cx - hueco + 2.5f * u, cx + hueco - 2.5f * u).forEach { x -> drawLine(marca(oscuro).copy(alpha = 0.6f * a), Offset(x, y - 2 * u), Offset(x, y + 2 * u), 0.8f * u) }
        radar(Offset(cx, y), r, -60f, oscuro, a = a)
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = ambar(oscuro)

    /**
     * El rótulo de una tecla de la consola: la ranura grabada bajo el texto con su piloto apagado a la izquierda. Al
     * presionarlo la tecla se ilumina por detrás (el cristal ámbar redondo con su filo) y el piloto se enciende; apagado,
     * queda solo la ranura, punteada y tenue, sin piloto.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val izq = 3 * u; val der = w - 3 * u
        val arriba = 7 * u; val pie = h - 7 * u
        if (!e.habilitado) {
            ranura(izq + 9 * u, der - 4 * u, pie - 1 * u, oscuro, 0.4f, PathEffect.dashPathEffect(floatArrayOf(2 * u, 2 * u)))
            return
        }
        if (e.presionado && pie > arriba) {
            val tecla = Path().apply { addRoundRect(RoundRect(Rect(izq, arriba, der, pie), CornerRadius((pie - arriba) / 2))) }
            drawPath(tecla, AMBAR.copy(alpha = if (oscuro) 0.2f else 0.18f))
            drawPath(tecla, ambar(oscuro).copy(alpha = 0.8f), style = Stroke(1 * u))
        } else ranura(izq + 9 * u, der - 4 * u, pie - 1 * u, oscuro, 0.75f)
        piloto(Offset(izq + 4.6f * u, h / 2), 1.4f * u, AMBAR, e.presionado, oscuro)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    override val margenFila: Dp = 16.dp

    /**
     * Una línea de la consola: la ranura grabada al pie y, en el canto izquierdo, su piloto apagado (dos, ámbar y verde, si la
     * fila lleva detalle). Al tocarla los pilotos se encienden, la fila toma la luz ámbar y la ranura se vuelve ámbar.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (e.presionado) {
            drawRect(AMBAR.copy(alpha = if (oscuro) 0.12f else 0.1f))
            drawLine(ambar(oscuro), Offset(16 * u, h - 1.2f * u), Offset(w, h - 1.2f * u), 1.2f * u)
        } else ranura(16 * u, w, h - 1.6f * u, oscuro, 0.55f)
        if (e.conDetalle && h > 30 * u) {
            piloto(Offset(7 * u, h / 2 - 5 * u), 1.7f * u, AMBAR, e.presionado, oscuro)
            piloto(Offset(7 * u, h / 2 + 5 * u), 1.7f * u, TRAZO, e.presionado, oscuro)
        } else piloto(Offset(7 * u, h / 2), 1.7f * u, AMBAR, e.presionado, oscuro)
    }

    /** La perilla de la consola, con su raya índice hacia adelante y las tres marcas de su escala (la del frente, más larga). */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2 - 1.5f * u, size.height / 2)
        listOf(-32f, 0f, 32f).forEach { g ->
            val a = Math.toRadians(g.toDouble()).toFloat()
            val d = Offset(cos(a), sin(a))
            val frente = g == 0f
            drawLine(tenue(oscuro).copy(alpha = if (frente) 0.9f else 0.5f), c + d * (7.4f * u), c + d * ((if (frente) 10f else 9f) * u),
                (if (frente) 1.2f else 0.8f) * u, StrokeCap.Round)
        }
        perilla(c, 5.6f * u, 0f, oscuro)
    }

    /**
     * El interruptor de palanca en su placa con dos tornillos: fija, la palanca arriba y el piloto ámbar de al lado encendido;
     * sin fijar, la palanca abajo, el piloto apagado y la placa tenue.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2 - 1.5f * u, size.height / 2)
        val a = if (fija) 1f else 0.55f
        val placa = Rect(c.x - 5.5f * u, c.y - 10 * u, c.x + 5.5f * u, c.y + 10 * u)
        drawRoundRect(cara(oscuro).copy(alpha = a), placa.topLeft, placa.size, CornerRadius(3 * u))
        drawRoundRect(cromoSombra(oscuro).copy(alpha = a), placa.topLeft, placa.size, CornerRadius(3 * u), style = Stroke(0.8f * u))
        listOf(placa.top + 2.4f * u, placa.bottom - 2.4f * u).forEach { y -> drawCircle(cromoSombra(oscuro).copy(alpha = a), 0.9f * u, Offset(c.x, y)) }
        palanca(Offset(c.x, c.y + (if (fija) 2f else -2f) * u), 2.6f * u, Offset(0f, if (fija) -1f else 1f), oscuro, a)
        piloto(Offset(c.x + 8.6f * u, c.y - 6.5f * u), 1.4f * u, AMBAR, fija, oscuro)
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = tenue(oscuro)

    /**
     * La consola sin nada conectado: la cara hueso con su filo y su moldura de cromo, la fila de pilotos de arriba toda apagada
     * con su palanca abajo y, al pie, la ranura de los cartuchos vacía, con su tapita de resorte entre dos tornillos.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val radio = minOf(13 * u, h / 2, w / 2)
        drawRoundRect(cara(oscuro), Offset.Zero, size, CornerRadius(radio))
        drawRoundRect(borde(oscuro), Offset.Zero, size, CornerRadius(radio), style = Stroke(0.8f * u))
        val m = 3 * u
        if (w > 2 * m && h > 2 * m) drawRoundRect(cromo(oscuro).copy(alpha = 0.6f), Offset(m, m), Size(w - 2 * m, h - 2 * m), CornerRadius(maxOf(radio - m, 1f)), style = Stroke(0.9f * u))
        // La fila de pilotos apagados y la palanca abajo.
        if (h > 34 * u && w > 90 * u) {
            for (i in 0 until 4) piloto(Offset(15 * u + i * 8 * u, 10 * u), 1.6f * u, if (i == 3) ROJO else AMBAR, false, oscuro)
            palanca(Offset(15 * u + 4 * 8 * u + 1 * u, 8 * u), 1.8f * u, Offset(0f, 1f), oscuro)
        }
        // La ranura de los cartuchos, vacía.
        if (h > 44 * u && w > 80 * u) {
            val ancho = minOf(w * 0.4f, 120 * u)
            val rr = Rect(w / 2 - ancho / 2, h - 11 * u, w / 2 + ancho / 2, h - 6 * u)
            drawRoundRect(baquelita(oscuro).copy(alpha = 0.85f), rr.topLeft, rr.size, CornerRadius(2.5f * u))
            drawLine(cromo(oscuro).copy(alpha = 0.7f), Offset(rr.left + 2.5f * u, rr.top + 1 * u), Offset(rr.right - 2.5f * u, rr.top + 1 * u), 0.8f * u)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.5f), Offset(rr.left + 2.5f * u, rr.bottom + 0.7f * u), Offset(rr.right - 2.5f * u, rr.bottom + 0.7f * u), 0.6f * u)
            listOf(rr.left - 5 * u, rr.right + 5 * u).forEach { x -> tornillo(Offset(x, rr.center.y), 1.4f * u, oscuro) }
        }
    }

    /**
     * La pantalla de radar sin un solo eco: la caja hueso con su luz de arriba y su sombra, la pantalla redonda en su aro de
     * cromo con las marcas de rumbo, la retícula y el barrido que pasa sin encontrar nada; abajo, el piloto apagado y la
     * perilla. De noche, la pantalla echa su resplandor verde.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val caja = Rect(f(0.08f), f(0.04f), f(0.92f), f(0.9f))
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(caja.left + f(0.03f), caja.top + f(0.05f)), caja.size, CornerRadius(f(0.2f)))
        drawRoundRect(cara(oscuro), caja.topLeft, caja.size, CornerRadius(f(0.2f)))
        drawRoundRect(caraLuz(oscuro), Offset(caja.left + f(0.1f), caja.top + f(0.02f)), Size(caja.width - f(0.2f), f(0.04f)), CornerRadius(f(0.02f)))
        drawRoundRect(borde(oscuro), caja.topLeft, caja.size, CornerRadius(f(0.2f)), style = Stroke(0.9f * u))
        val c = Offset(f(0.5f), f(0.42f))
        if (oscuro) drawCircle(Brush.radialGradient(listOf(TRAZO.copy(alpha = 0.16f), Color.Transparent), c, f(0.44f)), f(0.44f), c)
        radar(c, f(0.27f), -40f, oscuro)
        piloto(Offset(f(0.3f), f(0.79f)), f(0.04f), AMBAR, false, oscuro)
        perilla(Offset(f(0.68f), f(0.79f)), f(0.065f), 200f, oscuro)
    }

    override val margenAviso: Dp = 30.dp
    override fun tintaAviso(oscuro: Boolean): Color = tinta(oscuro)

    /**
     * El aviso del teletipo: el panel con la banda de baquelita del canto izquierdo y su filo de cromo; arriba, el piloto grande
     * encendido (ámbar si avisa, rojo si alarma, según el aviso) y, debajo, la cinta perforada que sale por su ranura y baja
     * con el mensaje.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val forma = Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(minOf(10 * u, h / 2)))) }
        drawPath(forma, cara(oscuro))
        val banda = 22 * u
        clipPath(forma) {
            drawRect(baquelita(oscuro), Offset.Zero, Size(banda, h))
            drawRect(cromo(oscuro), Offset(banda, 0f), Size(1.2f * u, h))
            drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.08f), Offset.Zero, Size(banda, h * 0.4f))
            if (h > 30 * u) {
                // La ranura del teletipo y la cinta que sale de ella.
                drawRoundRect(Color.Black.copy(alpha = 0.6f), Offset(4.5f * u, 21 * u), Size(13 * u, 2 * u), CornerRadius(1 * u))
                cinta(Rect(6.5f * u, 22.4f * u, 15.5f * u, h + 2 * u), if (oscuro) Color(0xFF9A9488) else Color(0xFFEDE6D3), baquelita(oscuro), e.semilla + 17, vertical = true)
            }
        }
        drawPath(forma, borde(oscuro), style = Stroke(0.8f * u))
        piloto(Offset(11 * u, 12 * u), 3.2f * u, if (e.semilla % 2 == 0) AMBAR else ROJO, true, oscuro)
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = tinta(oscuro)
    override fun rotuloCifra(oscuro: Boolean): Color = ambar(oscuro)

    /**
     * El tambor del contador mecánico de la consola: el bisel de cromo con su filo de luz arriba y su sombra abajo, la cara
     * hueso del tambor, curva (sombreada arriba y abajo, como lo que gira), con las muescas de sus dientes por los cantos, y
     * un tornillo a cada lado.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val radio = minOf(7 * u, h / 2)
        drawRoundRect(cromo(oscuro), Offset.Zero, size, CornerRadius(radio))
        drawLine(cromoLuz(oscuro), Offset(radio, 0.9f * u), Offset(w - radio, 0.9f * u), 0.9f * u)
        drawLine(cromoSombra(oscuro), Offset(radio, h - 0.9f * u), Offset(w - radio, h - 0.9f * u), 1 * u)
        val b = 2.6f * u
        val tambor = Rect(b, b, w - b, h - b)
        if (tambor.width < 2 * u || tambor.height < 2 * u) return
        val cr = CornerRadius(maxOf(radio - b, 1.5f * u))
        drawRoundRect(caraLuz(oscuro), tambor.topLeft, tambor.size, cr)
        val sombra = Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f)
        drawRoundRect(Brush.verticalGradient(0f to sombra, 0.3f to Color.Transparent, 0.7f to Color.Transparent, 1f to sombra, startY = tambor.top, endY = tambor.bottom),
            tambor.topLeft, tambor.size, cr)
        // Las muescas de los dientes, por los dos cantos.
        var y = tambor.top + 3 * u; var k = 0
        while (y < tambor.bottom - 2 * u && k < 100) {
            drawLine(sombra, Offset(tambor.left, y), Offset(tambor.left + 2.2f * u, y), 0.7f * u)
            drawLine(sombra, Offset(tambor.right - 2.2f * u, y), Offset(tambor.right, y), 0.7f * u)
            y += maxOf(3 * u, 1f); k++
        }
        listOf(Offset(b / 2, h / 2), Offset(w - b / 2, h / 2)).forEach { tornillo(it, 1 * u, oscuro) }
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    /**
     * El fondo de cada tarjeta: la superficie de la consola para la cifra y la orden; la pantalla de radar (oscura también de
     * día) para la noche; el cuerpo del cartucho para Vida.
     */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> panel(oscuro)
        TipoTarjeta.PENDIENTE -> panel(oscuro)
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFF0B1411) else Color(0xFF13201B)
        TipoTarjeta.VIDA -> if (oscuro) Color(0xFF2A2D2F) else Color(0xFFDDD4C2)
    }

    /** La etiqueta de papel del cartucho. */
    private fun etiqueta(oscuro: Boolean): Color = if (oscuro) Color(0xFF33373A) else Color(0xFFF4EEE2)

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (tipo == TipoTarjeta.NOCHE) Color(0xFFE7E1D4) else tinta(oscuro)
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = if (tipo == TipoTarjeta.NOCHE) Color(0xFF9DB8A4) else tenue(oscuro)
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> ambar(oscuro)
        TipoTarjeta.PENDIENTE -> ambar(oscuro)
        TipoTarjeta.NOCHE -> TRAZO
        TipoTarjeta.VIDA -> acero(oscuro)
    }

    /**
     * Las tarjetas de la sala de mando. La cifra de Hoy es el **módulo de lectura**: la curva grande de la consola en la esquina
     * de arriba a la izquierda, el zócalo de cromo al pie y la fila de tres pilotos arriba a la derecha (el verde de «en
     * línea», siempre encendido); destacada, se enciende el ámbar y el filo del módulo se vuelve ámbar. «Te falta hoy» es la
     * **orden de transmitir**: el filo ámbar doble y el botón rojo de transmisión con su tapa de seguridad levantada; completa,
     * la tapa baja sobre el botón y el piloto y el filo pasan a verde. La noche es la **pantalla grande de radar** que asoma
     * por la esquina de abajo, con sus anillos, el barrido y dos ecos; sellada, el eco queda fijado en su anillo de puntería.
     * Vida es el **cartucho de datos**, con su bisel, su muesca, la etiqueta y la placa de cromo. Al tocarlas el panel se hunde.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val base = fondoTarjeta(e.tipo, oscuro).let { if (e.presionado) mezcla(it, if (oscuro) Color.Black else Color(0xFF8A8070), 0.12f) else it }
        when (e.tipo) {
            TipoTarjeta.METRICA -> tarjetaModulo(e, base, oscuro)
            TipoTarjeta.PENDIENTE -> tarjetaOrden(e, base, oscuro)
            TipoTarjeta.NOCHE -> tarjetaRadar(e, base, oscuro)
            TipoTarjeta.VIDA -> tarjetaCartucho(base, oscuro)
        }
    }

    /** El módulo de lectura de la cifra de Hoy (ver [tarjeta]). */
    private fun DrawScope.tarjetaModulo(e: EstadoTarjeta, base: Color, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val esquina = minOf(16 * u, h / 2, w / 2)
        val forma = Path().apply { addRoundRect(RoundRect(0f, 0f, w, h, CornerRadius(esquina), CornerRadius(5 * u), CornerRadius(5 * u), CornerRadius(5 * u))) }
        drawPath(forma, base)
        clipPath(forma) {
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.05f else 0.55f), Color.Transparent), 0f, 18 * u), Offset.Zero, Size(w, 18 * u))
            // El zócalo de cromo, con su filo de luz y dos tornillos.
            drawRect(cromo(oscuro), Offset(0f, h - 3 * u), Size(w, 3 * u))
            drawLine(cromoLuz(oscuro), Offset(0f, h - 3 * u), Offset(w, h - 3 * u), 0.7f * u)
        }
        listOf(9 * u, w - 9 * u).forEach { x -> tornillo(Offset(x, h - 1.5f * u), 1 * u, oscuro) }
        drawPath(forma, if (e.destacado) ambar(oscuro) else borde(oscuro), style = Stroke((if (e.destacado) 1.3f else 0.8f) * u))
        // La ranura grabada del canto de arriba, desde la curva hasta los pilotos.
        ranura(esquina + 4 * u, w - 30 * u, 6 * u, oscuro, 0.4f)
        // Los pilotos: rojo, ámbar (encendido si la cifra se aparta de tu normal) y el verde de «en línea».
        piloto(Offset(w - 23 * u, 6 * u), 1.5f * u, ROJO, false, oscuro)
        piloto(Offset(w - 16 * u, 6 * u), 1.5f * u, AMBAR, e.destacado, oscuro)
        piloto(Offset(w - 9 * u, 6 * u), 1.5f * u, TRAZO, true, oscuro)
    }

    /** La orden de transmitir de «Te falta hoy» (ver [tarjeta]). */
    private fun DrawScope.tarjetaOrden(e: EstadoTarjeta, base: Color, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val luz = if (e.destacado) verde(oscuro) else ambar(oscuro)
        val radio = minOf(13 * u, h / 2)
        drawRoundRect(base, Offset.Zero, size, CornerRadius(radio))
        drawRoundRect(luz, Offset(1 * u, 1 * u), Size(w - 2 * u, h - 2 * u), CornerRadius(radio), style = Stroke(2 * u))
        if (w > 10 * u && h > 10 * u) drawRoundRect(luz.copy(alpha = 0.45f), Offset(4.5f * u, 4.5f * u), Size(w - 9 * u, h - 9 * u), CornerRadius(maxOf(radio - 3.5f * u, 1f)),
            style = Stroke(0.7f * u))
        // El botón de transmisión: el aro de cromo, el botón rojo con su brillo.
        val c = Offset(w - 15 * u, 15 * u)
        val r = 3.4f * u
        drawCircle(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f), r * 1.6f, c + Offset(0.6f * u, 0.9f * u))
        drawCircle(cromo(oscuro), r * 1.6f, c)
        drawCircle(cromoSombra(oscuro), r * 1.6f, c, style = Stroke(0.7f * u))
        drawCircle(if (oscuro) mezcla(ROJO, Color.Black, 0.2f) else ROJO, r, c)
        drawCircle(Color.Black.copy(alpha = 0.25f), r, c, style = Stroke(0.6f * u))
        drawCircle(Color.White.copy(alpha = 0.45f), r * 0.36f, Offset(c.x - r * 0.35f, c.y - r * 0.38f))
        // La tapa de seguridad, de plástico ámbar translúcido, con su bisagra arriba.
        val bisagra = c.y - r * 1.75f
        val ancho = r * 1.7f
        drawLine(cromoSombra(oscuro), Offset(c.x - ancho, bisagra), Offset(c.x + ancho, bisagra), 1.2f * u, StrokeCap.Round)
        val tapa = if (e.destacado) Rect(c.x - ancho, bisagra, c.x + ancho, c.y + r * 1.75f) else Rect(c.x - ancho, maxOf(bisagra - 5.5f * u, 1.5f * u), c.x + ancho, bisagra)
        drawRoundRect(AMBAR.copy(alpha = if (e.destacado) 0.32f else 0.22f), tapa.topLeft, tapa.size, CornerRadius(1.6f * u))
        drawRoundRect(mezcla(AMBAR, BAQUELITA, 0.35f).copy(alpha = 0.8f), tapa.topLeft, tapa.size, CornerRadius(1.6f * u), style = Stroke(0.7f * u))
        drawLine(Color.White.copy(alpha = 0.45f), Offset(tapa.left + tapa.width * 0.25f, tapa.bottom - tapa.height * 0.2f),
            Offset(tapa.left + tapa.width * 0.5f, tapa.top + tapa.height * 0.2f), 0.6f * u, StrokeCap.Round)
        // El piloto de la orden: ámbar mientras falta; verde, cerrada.
        piloto(Offset(c.x - 12 * u, 7.5f * u), 1.5f * u, if (e.destacado) TRAZO else AMBAR, true, oscuro)
    }

    /** La pantalla grande de radar de la noche (ver [tarjeta]). */
    private fun DrawScope.tarjetaRadar(e: EstadoTarjeta, base: Color, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val forma = Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(minOf(13 * u, h / 2)))) }
        drawPath(forma, base)
        val c = Offset(w - 26 * u, h + 14 * u)
        val radio = maxOf(h * 1.15f, 90 * u)
        val barrido = 235f
        val eco = barrido - 14f
        val pe = Offset(c.x + radio * 0.62f * cos(Math.toRadians(eco.toDouble()).toFloat()), c.y + radio * 0.62f * sin(Math.toRadians(eco.toDouble()).toFloat()))
        clipPath(forma) {
            // El resplandor de la pantalla (el único degradado).
            drawCircle(Brush.radialGradient(listOf(TRAZO.copy(alpha = if (oscuro) 0.15f else 0.2f), Color.Transparent), c, radio), radio, c)
            // Los anillos de distancia, los rayos de rumbo y las marcas del aro de fuera.
            for (k in 1..4) drawCircle(TRAZO.copy(alpha = 0.2f), radio * k / 4f, c, style = Stroke(0.8f * u))
            for (k in 0 until 12) {
                val a = Math.toRadians(k * 30.0).toFloat()
                drawLine(TRAZO.copy(alpha = 0.11f), c, Offset(c.x + radio * cos(a), c.y + radio * sin(a)), 0.6f * u)
            }
            for (k in 0 until 72) {
                val a = Math.toRadians(k * 5.0).toFloat(); val d = Offset(cos(a), sin(a))
                drawLine(TRAZO.copy(alpha = 0.25f), c + d * (radio - (if (k % 6 == 0) 5f else 2.5f) * u), c + d * radio, 0.7f * u)
            }
            // El barrido y su estela.
            for (k in 0 until 8) drawArc(TRAZO.copy(alpha = 0.16f - k * 0.018f), barrido - (k + 1) * 6f, 6f, true, Offset(c.x - radio, c.y - radio), Size(2 * radio, 2 * radio))
            val ab = Math.toRadians(barrido.toDouble()).toFloat()
            drawLine(TRAZO.copy(alpha = 0.55f), c, Offset(c.x + radio * cos(ab), c.y + radio * sin(ab)), 1 * u)
            // Los dos ecos: el recién barrido, vivo; el otro, ya apagándose.
            val otro = Math.toRadians((barrido - 46f).toDouble()).toFloat()
            drawCircle(TRAZO.copy(alpha = 0.3f), 2.8f * u, pe)
            drawCircle(TRAZO, 1.5f * u, pe)
            drawCircle(TRAZO.copy(alpha = 0.4f), 1.2f * u, Offset(c.x + radio * 0.4f * cos(otro), c.y + radio * 0.4f * sin(otro)))
            // Las estrellas por el canto de arriba.
            val n = (w / (26 * u)).toInt().coerceIn(3, 20)
            for (i in 0 until n) drawCircle(Color(0xFFE7E1D4).copy(alpha = 0.3f + 0.5f * azar(e.semilla + 61, i)), (0.4f + 0.6f * azar(e.semilla + 62, i)) * u,
                Offset(w * azar(e.semilla + 63, i), (2 + 6 * azar(e.semilla + 64, i)) * u))
            // Sellada: el eco queda fijado en su anillo de puntería, con la línea punteada al centro de la pantalla.
            if (e.destacado) {
                drawLine(TRAZO.copy(alpha = 0.5f), pe, c, 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 2.5f * u)))
                drawCircle(TRAZO, 5 * u, pe, style = Stroke(1 * u))
                listOf(Offset(1f, 0f), Offset(-1f, 0f), Offset(0f, 1f), Offset(0f, -1f)).forEach { d ->
                    drawLine(TRAZO, pe + d * (5 * u), pe + d * (8 * u), 1 * u, StrokeCap.Round)
                }
            }
        }
        drawPath(forma, if (e.destacado) TRAZO.copy(alpha = 0.85f) else Color(0xFF2C3D35), style = Stroke((if (e.destacado) 1.5f else 1f) * u))
    }

    /** El cartucho de datos de Vida (ver [tarjeta]). */
    private fun DrawScope.tarjetaCartucho(base: Color, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val bisel = 8 * u; val r = 5 * u
        val m0 = minOf(w - 34 * u, w * 0.7f)
        val cuerpo = Path().apply {
            moveTo(bisel, 0f)
            if (m0 > bisel + 4 * u) { lineTo(m0, 0f); lineTo(m0, 4 * u); lineTo(m0 + 12 * u, 4 * u); lineTo(m0 + 12 * u, 0f) }
            lineTo(w - r, 0f); quadraticTo(w, 0f, w, r); lineTo(w, h - r); quadraticTo(w, h, w - r, h)
            lineTo(r, h); quadraticTo(0f, h, 0f, h - r); lineTo(0f, bisel); close()
        }
        drawPath(cuerpo, base)
        clipPath(cuerpo) {
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.04f else 0.3f), Color.Transparent), 0f, 10 * u), Offset.Zero, Size(w, 10 * u))
            // La placa de cromo del pie.
            drawRect(cromo(oscuro), Offset(0f, h - 4 * u), Size(w, 4 * u))
            drawLine(cromoLuz(oscuro), Offset(0f, h - 4 * u), Offset(w, h - 4 * u), 0.7f * u)
        }
        listOf(6 * u, w - 6 * u).forEach { x -> tornillo(Offset(x, h - 2 * u), 1.1f * u, oscuro) }
        drawPath(cuerpo, borde(oscuro), style = Stroke(0.9f * u))
        // La etiqueta de papel, con su franja de color arriba.
        val et = Rect(6 * u, 7 * u, w - 6 * u, h - 9 * u)
        if (et.height > 10 * u && et.width > 10 * u) {
            drawRoundRect(etiqueta(oscuro), et.topLeft, et.size, CornerRadius(1.5f * u))
            drawRect(acero(oscuro).copy(alpha = 0.75f), Offset(et.left + 1 * u, et.top), Size(et.width - 2 * u, 2 * u))
            drawRoundRect(borde(oscuro).copy(alpha = 0.5f), et.topLeft, et.size, CornerRadius(1.5f * u), style = Stroke(0.6f * u))
        }
        // El disco de protección contra escritura, junto al bisel, con su ranura.
        val d = Offset(bisel + 6 * u, 3.6f * u)
        drawCircle(cromo(oscuro), 2 * u, d)
        drawCircle(cromoSombra(oscuro), 2 * u, d, style = Stroke(0.5f * u))
        drawLine(baquelita(oscuro), Offset(d.x - 1.2f * u, d.y + 0.6f * u), Offset(d.x + 1.2f * u, d.y - 0.6f * u), 0.8f * u, StrokeCap.Round)
        // La ventanilla de la cinta, al pie: el hueco oscuro con la cinta café asomando.
        if (w > 40 * u) {
            val vt = Rect(w / 2 - 9 * u, h - 8 * u, w / 2 + 9 * u, h - 5.2f * u)
            drawRoundRect(baquelita(oscuro), vt.topLeft, vt.size, CornerRadius(1.2f * u))
            drawRect(Color(0xFF6B4A2E), Offset(vt.left + 3 * u, vt.top + 0.8f * u), Size(vt.width - 6 * u, vt.height - 1.6f * u))
        }
    }

    // ---------------------------------------------------------------- Gastos

    /** La letra de la balanza: Chivo, la de leer (con su seminegrita para la mitad elegida). */
    private val letraGastos: FontFamily = LetraExtra.chivo
    /** La letra de los rótulos grabados y de la cinta de rotular. */
    private val letraRotulos: FontFamily = LetraExtra.chivoMono
    /** La letra ancha de los títulos, para el mes. */
    private val letraTitulo: FontFamily = LetraExtra.michroma

    override val altoBalanza: Dp = 58.dp

    /**
     * El conmutador de dos posiciones de la consola: la cara hueso con su moldura de cromo y, al centro, la placa de baquelita
     * con el interruptor de palanca, acostado, que apunta a la mitad elegida (a la izquierda el gasto, a la derecha el
     * ingreso). La elegida queda encendida: su ventanilla más clara, el filo de su color y su piloto prendido, con la letra en
     * seminegrita; la otra, hundida, con el piloto apagado y la letra apagada hacia el fondo.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        caraConsola(Rect(0f, 0f, w, h), minOf(13 * u, h / 2), oscuro)
        // La placa de baquelita del centro, con las dos marcas de las posiciones y la palanca.
        val pl = Rect(w / 2 - 15 * u, 6 * u, w / 2 + 15 * u, h - 6 * u)
        drawRoundRect(baquelita(oscuro), pl.topLeft, pl.size, CornerRadius(6 * u))
        drawLine(Color.White.copy(alpha = 0.1f), Offset(pl.left + 6 * u, pl.top + 0.8f * u), Offset(pl.right - 6 * u, pl.top + 0.8f * u), 0.6f * u)
        listOf(-1f, 1f).forEach { s ->
            val elegida = (s > 0) == e.ingreso
            drawLine(Color(0xFFEFE8D8).copy(alpha = if (elegida) 0.85f else 0.35f), Offset(pl.center.x + s * 11 * u, pl.center.y - 3.5f * u),
                Offset(pl.center.x + s * 11 * u, pl.center.y + 3.5f * u), 1 * u, StrokeCap.Round)
        }
        palanca(pl.center + Offset((if (e.ingreso) -1.5f else 1.5f) * u, 0f), 3.2f * u, Offset(if (e.ingreso) 1f else -1f, 0f), oscuro)
        listOf(false, true).forEach { ingreso ->
            val elegida = e.ingreso == ingreso
            val r = if (ingreso) Rect(pl.right + 5 * u, 7 * u, w - 7 * u, h - 7 * u) else Rect(7 * u, 7 * u, pl.left - 5 * u, h - 7 * u)
            if (r.width < 20 * u) return@forEach
            val color = if (ingreso) e.colorIngreso else e.colorGasto
            val fondo = if (elegida) caraLuz(oscuro) else hundido(oscuro)
            val radio = minOf(9 * u, r.height / 2)
            val vent = ventanilla(r, radio, fondo, oscuro)
            if (elegida) drawPath(vent, color, style = Stroke(1.4f * u))
            val luz = Offset(r.left + 10 * u, r.center.y)
            piloto(luz, 2.2f * u, color, elegida, oscuro)
            val texto = if (ingreso) e.textoIngreso else e.textoGasto
            textoEn(medidor, texto, Rect(luz.x + 6.5f * u, r.top + 3 * u, r.right - 6 * u, r.bottom - 3 * u), letraGastos,
                legibleFrente(color, fondo, tenue = !elegida), if (elegida) 16f else 14.5f, peso = if (elegida) FontWeight.SemiBold else FontWeight.Normal)
        }
    }

    override val altoMes: Dp = 50.dp

    /**
     * La banda de la consola con el mes: la cara hueso con su moldura y, al centro, la ventanilla hundida con el nombre del
     * mes en la letra ancha de los títulos; en cada punta, una perilla de baquelita con su escala grabada, la de la izquierda
     * girada hacia atrás y la de la derecha hacia adelante.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        caraConsola(Rect(0f, 0f, w, h), minOf(13 * u, h / 2), oscuro)
        listOf(-1f, 1f).forEach { s ->
            val c = Offset(if (s < 0) 24 * u else w - 24 * u, h / 2)
            val gira = if (s < 0) 180f else 0f
            for (k in -2..2) {
                val a = Math.toRadians((gira + k * 25f).toDouble()).toFloat()
                val d = Offset(cos(a), sin(a))
                drawLine(marca(oscuro).copy(alpha = if (k == 0) 0.95f else 0.5f), c + d * (12.5f * u), c + d * ((if (k == 0) 16f else 14.5f) * u),
                    (if (k == 0) 1.3f else 0.8f) * u, StrokeCap.Round)
            }
            perilla(c, 10 * u, gira, oscuro)
        }
        val f = Rect(46 * u, 8 * u, w - 46 * u, h - 8 * u)
        if (f.width > 30 * u) {
            ventanilla(f, minOf(8 * u, f.height / 2), hundido(oscuro), oscuro)
            textoEn(medidor, e.titulo, Rect(f.left + 8 * u, f.top + 2 * u, f.right - 8 * u, f.bottom - 2 * u), letraTitulo, tinta(oscuro), 15f, espaciado = 1.2f)
        }
    }

    /**
     * Un renglón del registro de la consola: la ranura grabada al pie y, abajo a la izquierda, el tramo de cinta perforada
     * del tipo (ámbar si es un gasto, verde de radar si es un ingreso), con los agujeros de su mensaje. Una muesca grabada
     * marca la columna de los importes.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        ranura(24 * u, w - 2 * u, h - 1.6f * u, oscuro, 0.5f)
        val papel = if (e.ingreso) (if (oscuro) Color(0xFF5E8C66) else Color(0xFFA9D3AE)) else (if (oscuro) Color(0xFFB57A2C) else Color(0xFFE9B565))
        cinta(Rect(0f, h - 5.2f * u, 20 * u, h - 0.6f * u), papel, BAQUELITA.copy(alpha = 0.6f), e.semilla, vertical = false)
        val x = w - 112 * u
        if (x > w / 2) drawLine(marca(oscuro).copy(alpha = 0.45f), Offset(x, h - 4.6f * u), Offset(x, h - 1.8f * u), 0.8f * u)
    }

    override val altoRotuloDia: Dp = 22.dp

    /**
     * La fecha en la cinta de rotular: el día en letras de relieve color hueso sobre la cinta negra, con su sombrita, y detrás
     * la ranura grabada que sigue hasta el piloto apagado del borde.
     */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tam = 11f; val esp = 1.6f
        val ancho = minOf(anchoFrente(medidor, texto, letraRotulos, tam, FontWeight.Medium, esp), w * 0.7f)
        val tira = Rect(1 * u, 3 * u, 1 * u + ancho + 14 * u, h - 3 * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.18f), tira.topLeft + Offset(0.6f * u, 0.9f * u), tira.size, CornerRadius(2.2f * u))
        drawRoundRect(if (oscuro) Color(0xFF35312C) else BAQUELITA, tira.topLeft, tira.size, CornerRadius(2.2f * u))
        drawLine(Color.White.copy(alpha = 0.12f), Offset(tira.left + 2 * u, tira.top + 0.8f * u), Offset(tira.right - 2 * u, tira.top + 0.8f * u), 0.6f * u)
        textoEn(medidor, texto, cajaFrente(ancho, tira.left + 7 * u, tira.top + 1 * u, tira.bottom - 1 * u, tira.right - 5 * u), letraRotulos, LETRA_CINTA, tam,
            peso = FontWeight.Medium, espaciado = esp)
        if (tira.right + 16 * u < w) {
            ranura(tira.right + 6 * u, w - 10 * u, h / 2, oscuro, 0.7f)
            piloto(Offset(w - 5 * u, h / 2), 1.4f * u, AMBAR, false, oscuro)
        }
    }

    // ---------------------------------------------------------------- adornos de la hoja rápida, las relaciones y la cinta

    /** Un interruptor de palanca dibujado a trazo (para los adornos que van encima del contenido): la tuerca, la palanca y su bola. */
    private fun DrawScope.palancaTrazo(c: Offset, r: Float, arriba: Boolean, color: Color) {
        val u = density
        val punta = Offset(c.x, c.y + (if (arriba) -1f else 1f) * r * 2.2f)
        drawCircle(color, r, c, style = Stroke(0.8f * u))
        drawLine(color, c, punta, maxOf(r * 0.55f, 0.8f * u), StrokeCap.Round)
        drawCircle(color, r * 0.55f, punta)
    }

    /**
     * La hoja rápida es el panel de la consola. Arriba a la izquierda, la fila de cinco interruptores (dos arriba) entre sus
     * pilotos, con su rótulo grabado debajo de cada uno, y la escala que corre hasta la pantallita de radar de la esquina. En
     * el canto izquierdo, el tablero de clavijas con un cable de conexión colgando entre dos; en el derecho, la escala del
     * deslizador con su perilla. Abajo, la ranura de los cartuchos entre dos tornillos, la fila de pilotos y el emblema del
     * Frente; un tornillo en cada esquina.
     */
    override fun DrawScope.adornoQuick(e: EstadoAdorno, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val linea = e.secundario.copy(alpha = 0.55f); val fino = e.secundario.copy(alpha = 0.3f); val luz = e.primario
        // Los interruptores de arriba, entre sus pilotos (el primero encendido).
        val y = 12 * u
        drawCircle(luz.copy(alpha = 0.2f), 4.4f * u, Offset(13 * u, y)); drawCircle(luz.copy(alpha = 0.9f), 2.2f * u, Offset(13 * u, y))
        for (i in 0 until 5) {
            val x = 24 * u + i * 10 * u
            palancaTrazo(Offset(x, y + 1 * u), 2.2f * u, i < 2, linea)
            drawLine(fino, Offset(x - 2 * u, y + 9 * u), Offset(x + 2 * u, y + 9 * u), 0.9f * u, StrokeCap.Round)
        }
        drawCircle(linea, 2.2f * u, Offset(76 * u, y), style = Stroke(0.8f * u))
        // La pantallita de radar de la esquina: el aro, la retícula, el barrido con su estela y un eco.
        val rc = Offset(w - 24 * u, 14 * u); val rr = 9 * u
        drawCircle(linea, rr, rc, style = Stroke(1.1f * u))
        drawCircle(fino, rr * 0.55f, rc, style = Stroke(0.7f * u))
        drawLine(fino, Offset(rc.x - rr, rc.y), Offset(rc.x + rr, rc.y), 0.6f * u)
        drawLine(fino, Offset(rc.x, rc.y - rr), Offset(rc.x, rc.y + rr), 0.6f * u)
        for (k in 0 until 4) drawArc(luz.copy(alpha = 0.26f - k * 0.055f), -40f - (k + 1) * 12f, 12f, true, Offset(rc.x - rr, rc.y - rr), Size(2 * rr, 2 * rr))
        val ab = Math.toRadians(-40.0).toFloat()
        drawLine(luz.copy(alpha = 0.75f), rc, Offset(rc.x + rr * cos(ab), rc.y + rr * sin(ab)), 0.9f * u)
        drawCircle(luz.copy(alpha = 0.85f), 1 * u, Offset(rc.x - rr * 0.35f, rc.y - rr * 0.45f))
        // La escala grabada entre los interruptores y el radar.
        if (w - 38 * u > 92 * u) {
            drawLine(fino, Offset(86 * u, y + 2 * u), Offset(w - 38 * u, y + 2 * u), 0.8f * u)
            var x = 86 * u; var k = 0
            while (x <= w - 38 * u && k < 80) { drawLine(fino, Offset(x, y + 2 * u), Offset(x, y + (if (k % 4 == 0) -3f else -0.5f) * u), 0.7f * u); x += maxOf(8 * u, 1f); k++ }
        }
        // El tablero de clavijas del canto izquierdo, con el cable de conexión entre dos.
        val clavijas = ArrayList<Offset>()
        var yc = 56 * u
        while (yc < h - 60 * u && clavijas.size < 24) { clavijas.add(Offset(8 * u, yc)); yc += maxOf(30 * u, 1f) }
        clavijas.forEach { p -> drawCircle(linea, 2.6f * u, p, style = Stroke(0.8f * u)); drawCircle(fino, 1 * u, p) }
        if (clavijas.size >= 4) {
            val a = clavijas[1]; val b = clavijas[3]
            drawPath(Path().apply { moveTo(a.x + 2 * u, a.y); cubicTo(a.x + 22 * u, a.y + 8 * u, b.x + 22 * u, b.y - 8 * u, b.x + 2 * u, b.y) }, luz.copy(alpha = 0.45f),
                style = Stroke(1.6f * u, cap = StrokeCap.Round))
            listOf(a, b).forEach { p -> drawRoundRect(luz.copy(alpha = 0.7f), Offset(p.x + 1 * u, p.y - 1.5f * u), Size(4 * u, 3 * u), CornerRadius(0.8f * u)) }
        }
        // El deslizador del canto derecho: la escala y su perilla a un tercio.
        if (h - 50 * u > 60 * u) {
            val xf = w - 8 * u
            drawLine(linea, Offset(xf, 50 * u), Offset(xf, h - 50 * u), 1 * u)
            var yy = 50 * u; var k = 0
            while (yy <= h - 50 * u && k < 120) { drawLine(fino, Offset(xf - (if (k % 5 == 0) 5f else 3f) * u, yy), Offset(xf - 1 * u, yy), 0.7f * u); yy += maxOf(10 * u, 1f); k++ }
            val yp = 50 * u + (h - 100 * u) * 0.35f
            drawRoundRect(linea, Offset(xf - 4 * u, yp - 2.5f * u), Size(8 * u, 5 * u), CornerRadius(1.5f * u))
            drawLine(luz.copy(alpha = 0.8f), Offset(xf - 3 * u, yp), Offset(xf + 3 * u, yp), 0.9f * u)
        }
        // Abajo: la ranura de los cartuchos entre dos tornillos, los pilotos y el emblema.
        val rr2 = Rect(w / 2 - 32 * u, h - 14 * u, w / 2 + 32 * u, h - 8 * u)
        drawRoundRect(linea, rr2.topLeft, rr2.size, CornerRadius(3 * u), style = Stroke(1 * u))
        drawLine(fino, Offset(rr2.left + 4 * u, rr2.center.y), Offset(rr2.right - 4 * u, rr2.center.y), 0.8f * u)
        listOf(rr2.left - 8 * u, rr2.right + 8 * u).forEach { x -> drawCircle(linea, 2.2f * u, Offset(x, rr2.center.y), style = Stroke(0.8f * u)); drawCircle(fino, 0.9f * u, Offset(x, rr2.center.y)) }
        for (i in 0 until 3) {
            val p = Offset(16 * u + i * 7 * u, h - 11 * u)
            if (i == 0) { drawCircle(luz.copy(alpha = 0.2f), 3.4f * u, p); drawCircle(luz.copy(alpha = 0.85f), 1.7f * u, p) } else drawCircle(linea, 1.7f * u, p, style = Stroke(0.8f * u))
        }
        with(TrazosFrente) { emblemaRed(Offset(w - 22 * u, h - 15 * u), 5.5f * u, luz.copy(alpha = 0.6f), 1.3f * u) }
        // Los tornillos de las esquinas.
        listOf(Offset(5.5f * u, 5.5f * u), Offset(w - 5.5f * u, 5.5f * u), Offset(5.5f * u, h - 5.5f * u), Offset(w - 5.5f * u, h - 5.5f * u)).forEach { p ->
            drawCircle(linea, 2.2f * u, p, style = Stroke(0.8f * u)); drawCircle(fino, 0.9f * u, p)
        }
    }

    /**
     * El recuadro de Relaciones es un módulo de la consola: dos interruptores arriba a la izquierda (uno arriba, otro abajo),
     * tres pilotos arriba a la derecha (el primero encendido), la ranura grabada del pie y, en la esquina de abajo a la
     * derecha, el cuarto de pantalla de radar con su barrido y un eco.
     */
    override fun DrawScope.adornoRelacion(e: EstadoAdorno, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val linea = e.secundario.copy(alpha = 0.45f); val fino = e.secundario.copy(alpha = 0.22f)
        palancaTrazo(Offset(8 * u, 9 * u), 1.6f * u, true, linea)
        palancaTrazo(Offset(15 * u, 6 * u), 1.6f * u, false, linea)
        for (i in 0 until 3) {
            val p = Offset(w - 7 * u - i * 5.5f * u, 6 * u)
            if (i == 0) { drawCircle(e.primario.copy(alpha = 0.2f), 3 * u, p); drawCircle(e.primario.copy(alpha = 0.85f), 1.4f * u, p) }
            else drawCircle(linea, 1.4f * u, p, style = Stroke(0.7f * u))
        }
        drawLine(fino, Offset(6 * u, h - 4 * u), Offset(w * 0.55f, h - 4 * u), 0.8f * u)
        val c = Offset(w, h)
        for (k in 1..3) drawArc(if (k == 3) linea else fino, 180f, 90f, false, Offset(c.x - k * 6 * u, c.y - k * 6 * u), Size(k * 12 * u, k * 12 * u), style = Stroke(0.8f * u))
        drawArc(e.primario.copy(alpha = 0.22f), 205f, 30f, true, Offset(c.x - 18 * u, c.y - 18 * u), Size(36 * u, 36 * u))
        val a = Math.toRadians(232.0).toFloat()
        drawCircle(e.primario.copy(alpha = 0.8f), 1 * u, Offset(c.x + 11 * u * cos(a), c.y + 11 * u * sin(a)))
    }

    /**
     * El icono de la cinta en el aro de una pantalla de radar: el aro grueso, las marcas de rumbo hacia adentro en los cuatro
     * puntos (la de arriba, más larga), las de las diagonales hacia afuera y el piloto de la esquina de arriba a la derecha.
     */
    override fun DrawScope.marcoPista(e: EstadoAdorno, oscuro: Boolean) {
        val s = size.minDimension; val g = s * 0.06f
        val c = Offset(s / 2, s / 2)
        drawCircle(e.primario, s * 0.41f, c, style = Stroke(g * 1.3f))
        for (k in 0 until 8) {
            if (k == 1) continue
            val a = Math.toRadians(k * 45.0 - 90.0).toFloat()
            val d = Offset(cos(a), sin(a))
            if (k % 2 == 0) drawLine(e.primario, c + d * (s * (if (k == 0) 0.29f else 0.32f)), c + d * (s * 0.37f), g * 0.9f)
            else drawLine(e.primario, c + d * (s * 0.45f), c + d * (s * 0.5f), g * 0.8f)
        }
        drawCircle(e.secundario, s * 0.065f, Offset(s * 0.88f, s * 0.12f))
    }
}
