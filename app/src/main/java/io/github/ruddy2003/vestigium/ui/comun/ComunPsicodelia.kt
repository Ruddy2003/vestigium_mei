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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.ColoresPsi
import io.github.ruddy2003.vestigium.ui.EstadoTema
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia.cablesTrolebus
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia.corona
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia.disco
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia.enCache
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia.filete
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia.granoPelicula
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia.ondas
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia.pase
import io.github.ruddy2003.vestigium.ui.TrazosCiudad
import io.github.ruddy2003.vestigium.ui.TrazosTinta
import io.github.ruddy2003.vestigium.ui.TrazosTinta.conRegistro
import io.github.ruddy2003.vestigium.ui.TrazosTinta.trama
import io.github.ruddy2003.vestigium.ui.biblioteca.textoEn
import io.github.ruddy2003.vestigium.ui.ideario.azar
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

// =====================================================================
//  Psicodelia (versión 2: la ciudad y la tinta)
// =====================================================================

/** La razón de contraste entre dos colores opacos (WCAG 2): de 1 (iguales) a 21 (negro sobre blanco). */
private fun contrastePsi(a: Color, b: Color): Float {
    val la = a.luminance(); val lb = b.luminance()
    return (maxOf(la, lb) + 0.05f) / (minOf(la, lb) + 0.05f)
}

/**
 * El color [c] de un texto tal cual si se lee sobre [fondo] (contraste de al menos [minimo]); si no, llevado poco a poco
 * hacia [tinta] (la tinta del texto de la variante) hasta que se lea. Así un texto nunca pierde contraste (la regla de
 * legibilidad de la guía), tampoco con la intensidad.
 */
private fun legiblePsi(c: Color, fondo: Color, tinta: Color, minimo: Float = 4.5f): Color {
    val opaco = c.copy(alpha = 1f)
    if (contrastePsi(opaco, fondo) >= minimo) return opaco
    for (k in 1..20) {
        val m = lerp(opaco, tinta, k * 0.05f)
        if (contrastePsi(m, fondo) >= minimo) return m
    }
    return tinta
}

/**
 * Las rayas de los renglones de un campo de varios renglones, de abajo arriba: la primera ([k] = 0) al pie del último
 * renglón, a [margenY] del canto de abajo, y luego una cada 24 sp mientras quede sitio arriba (16 dp bajo [margenY]).
 */
private inline fun DrawScope.renglonesPsi(margenY: Dp, raya: (k: Int, y: Float) -> Unit) {
    val paso = maxOf(24.sp.toPx(), 1f)
    val tope = margenY.toPx() + 16.dp.toPx()
    var y = size.height - margenY.toPx() + 1.dp.toPx()
    var k = 0
    while (y > tope && k < 200) { raya(k, y); y -= paso; k++ }
}

/** El perfil de la hoja rápida: cada gota de su nube (centro en fracción del ancho, medio ancho y cuánto sube, en dp). */
private val GOTAS_QUICK = listOf(floatArrayOf(0.12f, 44f, 6f), floatArrayOf(0.3f, 60f, 11f), floatArrayOf(0.52f, 74f, 16f), floatArrayOf(0.74f, 56f, 11.5f), floatArrayOf(0.9f, 36f, 6f))

/** Cuánto sube en [x] el borde de la hoja rápida sobre su base (como lo calcula `QuickActivity`, rama de Psicodelia). */
private fun altoQuickPsi(x: Float, w: Float, u: Float): Float {
    val k = 2.6f * u
    var suma = 1f
    for (g in GOTAS_QUICK) {
        val d = (x - g[0] * w) / (g[1] * u); val b = g[1] * 0.6f * u
        if (d * d < 1f) suma += exp((g[2] * u - b + b * sqrt(1f - d * d)) / k)
    }
    return k * ln(suma)
}

/**
 * Psicodelia v2 (`docs/diseno/temas/psicodelia.md`): las piezas comunes en las dos caras del tema, cada una con **una sola
 * geometría y dos pintores** (las figuras compartidas están en `ui/PsicodeliaPiezas.kt`).
 *
 * - **La ciudad** (cara BASE, de 5:00 a 21:00): San Francisco a fines de los 60 en una foto de revista de los 70. Placas y
 *   papeles con la luz de la tarde que entra por la izquierda, la sombra corta y café, el grano de película. Los motivos son
 *   de la calle y del cuarto: los cables del trolebús con sus colgadores, el pase de autobús, la ventanilla, la etiqueta del
 *   disco, la chincheta, las victorianas grises, la lámpara de pantalla. Sobria: nada brilla, nada se mueve.
 * - **La tinta** (cara INTENSA, de noche o con un consumo en curso): el cartel de la semana, a tintas planas sobre papel
 *   crema o sobre negro de terciopelo. El marco de arco, la trama de líneas, los rayos que ondulan alrededor del disco hecho
 *   sol, las bandas a dos tintas (a tres con la intensidad), el registro que se corre. La intensidad calienta el primario,
 *   el secundario y el terciario, y lo que depende de ella va en nueve escalones (`EstadoTema.escalon`).
 *
 * La regla de legibilidad: los papeles de fondo, la tinta y lo tenue no cambian con la intensidad; ningún motivo pasa por
 * detrás de un texto sin placa opaca (los motivos van en los márgenes, las cabeceras y las esquinas); ninguna letra de datos
 * se deforma. Las piezas grandes (tarjetas, vacío, hoja rápida) se guardan en caché por variante, escalón y estado.
 *
 * Lo que se lee: las piezas de dibujo leen `EstadoTema.cara` y `EstadoTema.intensidad` (Compose las vuelve a dibujar cuando
 * cambian); los colores, lo mismo desde la composición. **No se enteran de `SinIntensidad`** (el contrato `DisenoComun` no
 * pasa la intensidad): dentro de esas pantallas, la intensidad de estas piezas es la de la app.
 */
object ComunPsicodelia : DisenoComun {
    /** Los colores de la variante, con la cara y la intensidad de ahora (en escalones). */
    private fun k(oscuro: Boolean): ColoresPsi = PiezasPsicodelia.colores(oscuro)

    /** La clave de caché de la variante: claro u oscuro, la cara y el escalón de la intensidad. */
    private fun clave(k: ColoresPsi, vararg mas: Any): List<Any> = listOf(k.oscuro, k.cara, k.i) + mas.toList()

    /** La forma de una placa: en la ciudad, redondeada (la ventanilla, 11 dp como mucho); en la tinta, el arco del cartel. */
    private fun DrawScope.formaPlaca(r: Rect, k: ColoresPsi, radio: Float = 11 * density): Path =
        if (k.ciudad) Path().apply { addRoundRect(RoundRect(r, CornerRadius(min(radio, min(r.width, r.height) / 2)))) }
        else TrazosTinta.arco(r, min(radio * 13f / 11f, min(r.width, r.height) / 2), 2f * density)

    /**
     * Una placa entera, como base de casi todas las piezas: en la ciudad, la sombra corta café, el papel [relleno], la luz de
     * la tarde desde la izquierda (de noche, la de la lámpara), el grano y el filete [linea]; en la tinta, el papel plano y
     * el filete de la tinta que graba (con [linea] distinta, impresa otra vez corrida de registro).
     */
    private fun DrawScope.placa(forma: Path, k: ColoresPsi, relleno: Color, linea: Color?, grueso: Float = density, grano: Boolean = true) {
        val u = density
        if (k.ciudad) {
            translate(0.8f * u, 1.2f * u) { drawPath(forma, k.sombra) }
            drawPath(forma, relleno)
            val b = forma.getBounds()
            if (!b.isEmpty) drawPath(forma, Brush.horizontalGradient(listOf(k.luz, Color.Transparent), b.left, b.left + min(b.width * 0.6f, 260 * u)))
            if (grano) granoPelicula(forma, k.grano)
            if (linea != null) drawPath(forma, linea, style = Stroke(grueso))
        } else {
            drawPath(forma, relleno)
            if (linea != null) {
                if (linea != k.grabado && k.m.registro > 0f) conRegistro(TrazosTinta.Tinta.MAGENTA, k.m.registro * 0.5f) {
                    drawPath(forma, k.ultramar.copy(alpha = 0.7f), style = Stroke(grueso))
                }
                drawPath(forma, linea, style = Stroke(grueso))
            }
        }
    }

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = k(oscuro).tinta
    override fun pistaCampo(oscuro: Boolean): Color = k(oscuro).tenue
    override fun rotuloCampo(oscuro: Boolean): Color = k(oscuro).primario
    override fun errorCampo(oscuro: Boolean): Color = k(oscuro).error

    /**
     * El campo. La ciudad: la ventanilla de un formulario de la oficina de tránsito, la placa de papel con su luz y su
     * grano, y con el foco, la franja de color del canto izquierdo y el filete en el naranja quemado. La tinta: la cartela
     * del cartel, de arco, con la franja de trama en el margen izquierdo (de tinta de grabar; con el foco, de ultramar) y el
     * filete en magenta, impreso dos veces con el registro corrido. Con un error, el rojo de letrero o el bermellón; apagado,
     * la placa hundida y la raya de puntos. Los renglones de varios renglones, finos, en el color del filete.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val k = k(oscuro)
        val u = density
        val w = size.width; val h = size.height
        if (w < 24 * u || h < 20 * u) return
        val foco = e.enfocado && e.habilitado
        val caja = Rect(1f * u, 1f * u, w - 2f * u, h - 2.5f * u)
        val forma = formaPlaca(caja, k, 8 * u)
        val relleno = if (e.habilitado) k.superficie else lerp(k.superficie, k.variante, 0.6f)
        val linea = when {
            e.error -> k.error
            foco -> k.primario
            !e.habilitado -> k.borde.copy(alpha = 0.5f)
            else -> if (k.ciudad) k.borde else k.grabado.copy(alpha = 0.75f)
        }
        placa(forma, k, relleno, linea, if (foco || e.error) 1.6f * u else 1f * u, grano = e.habilitado)
        clipPath(forma) {
            if (k.ciudad) {
                if (foco || e.error) drawRect(linea, caja.topLeft, Size(3.2f * u, caja.height))
            } else {
                val franja = Path().apply { addRect(Rect(caja.left, caja.top, caja.left + 7 * u, caja.bottom)) }
                trama(franja, 2.4f * u, 0.8f * u, if (e.error) k.error else if (foco) k.ultramar else k.grabado.copy(alpha = 0.3f), 45f)
            }
        }
        val izq = margenCampoX.toPx() - 2 * u; val der = w - margenCampoX.toPx()
        if (!e.habilitado) {
            if (der - izq > 24 * u) drawLine(k.tenue.copy(alpha = 0.4f), Offset(izq, h - 9 * u), Offset(der, h - 9 * u), 0.9f * u, StrokeCap.Round,
                PathEffect.dashPathEffect(floatArrayOf(0.4f * u, 2.6f * u)))
            return
        }
        if (e.multilinea && der - izq > 24 * u) renglonesPsi(margenCampoY) { _, y ->
            drawLine((if (k.ciudad) k.borde else k.grabado).copy(alpha = 0.28f), Offset(izq, y - 1 * u), Offset(der, y - 1 * u), 0.7f * u)
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /** La ciudad: los dos cables del trolebús con el colgador en medio. La tinta: dos ondas que llegan al disco chico. Tenue, a la mitad. */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        filete(3 * density, size.width - 3 * density, size.height / 2, k(oscuro), if (e.tenue) 0.5f else 1f)
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = k(oscuro).primario

    /**
     * La ciudad: la raya pintada bajo la palabra, como en un letrero; al presionarlo, la placa clara detrás. La tinta: la
     * onda corta a dos tintas bajo la palabra; al presionarlo, la cartela de arco en el contenedor, con su segunda pasada
     * corrida. Apagado, la raya de puntos.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val k = k(oscuro)
        val u = density
        val w = size.width; val h = size.height
        val izq = minOf(12 * u, w * 0.18f); val der = w - izq
        val y = h / 2 + 11 * u
        if (der - izq < 10 * u) return
        if (!e.habilitado) {
            drawLine(k.tenue.copy(alpha = 0.4f), Offset(izq, y), Offset(der, y), 0.9f * u, StrokeCap.Round, PathEffect.dashPathEffect(floatArrayOf(0.4f * u, 2.6f * u)))
            return
        }
        if (e.presionado && h > 20 * u) {
            val r = Rect(izq - 4 * u, h / 2 - 15 * u, der + 4 * u, h / 2 + 15 * u)
            val forma = formaPlaca(r, k, 15 * u)
            if (k.ciudad) drawPath(forma, k.contenedor.copy(alpha = 0.7f))
            else {
                conRegistro(TrazosTinta.Tinta.MAGENTA, k.m.registro * 0.5f) { drawPath(forma, k.ultramar.copy(alpha = 0.35f), style = Stroke(1.2f * u)) }
                drawPath(forma, k.contenedor.copy(alpha = 0.75f))
            }
        }
        val a = if (e.presionado) 1f else 0.7f
        if (k.ciudad) drawLine(k.primario.copy(alpha = a), Offset(izq + 4 * u, y), Offset(der - 4 * u, y), 1.3f * u, StrokeCap.Butt)
        else ondas(izq + 4 * u, der - 4 * u, y, 1.2f * u, 16 * u, 1.3f * u, 2.2f * u,
            listOf(TrazosTinta.Tinta.ULTRAMAR to k.ultramar.copy(alpha = a * 0.8f), TrazosTinta.Tinta.MAGENTA to k.primario.copy(alpha = a)), k.m.registro * 0.4f)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    override val margenFila: Dp = 22.dp

    /** El color de la etiqueta del disco de una fila, por semilla. */
    private fun etiqueta(k: ColoresPsi, semilla: Int): Color =
        if (k.ciudad) listOf(TrazosCiudad.ETIQUETA, k.secundario, k.terciario, k.primario)[semilla.mod(4)]
        else listOf(k.magenta, k.ultramar, k.terciario)[semilla.mod(3)]

    /**
     * Una pista de la lista del reverso del disco: a la izquierda, el disco chico con su etiqueta (si la fila lleva detalle,
     * asoma de su funda: en la ciudad, de cartón con su franja; en la tinta, impresa con trama); al pie, el surco fino que se
     * pierde a la derecha (en la tinta, la raya de puntos del cartel). Al tocarla, en la ciudad entra la luz de la tarde por la
     * izquierda; en la tinta, la fila se imprime en el contenedor.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val k = k(oscuro)
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        if (e.presionado) {
            if (k.ciudad) drawRect(Brush.horizontalGradient(listOf(k.contenedor.copy(alpha = 0.7f), k.contenedor.copy(alpha = 0f)), 0f, w * 0.8f))
            else drawRect(k.contenedor.copy(alpha = 0.6f))
        }
        val yl = h - 1.2f * u
        val x0 = margenFila.toPx() - 2 * u
        if (w - x0 > 30 * u) {
            if (k.ciudad) {
                drawLine(Brush.horizontalGradient(0f to k.borde, 0.7f to k.borde.copy(alpha = 0.6f), 1f to k.borde.copy(alpha = 0f), startX = x0, endX = w - 2 * u),
                    Offset(x0, yl), Offset(w - 2 * u, yl), 0.8f * u)
                drawLine(k.luz, Offset(x0, yl + 0.8f * u), Offset(w * 0.6f, yl + 0.8f * u), 0.5f * u)
            } else drawLine(k.grabado.copy(alpha = 0.4f), Offset(x0, yl), Offset(w - 2 * u, yl), 0.9f * u, StrokeCap.Round,
                PathEffect.dashPathEffect(floatArrayOf(2.6f * u, 2.2f * u)))
        }
        if (h < 16 * u) return
        val col = etiqueta(k, e.semilla)
        if (e.conDetalle && h >= 30 * u) {
            disco(Offset(14.6f * u, cy), 6.6f * u, k, giro = azar(e.semilla, 2) * 360f, etiqueta = col)
            val funda = Rect(0.6f * u, cy - 6.8f * u, 13.6f * u, cy + 6.8f * u)
            if (k.ciudad) {
                drawRect(k.sombra, funda.topLeft + Offset(0.6f * u, 0.9f * u), funda.size)
                val carton = if (oscuro) Color(0xFF8C7656) else Color(0xFFD9C7A2)
                drawRect(carton, funda.topLeft, funda.size)
                drawRect(TrazosCiudad.enSombra(col, 0.15f), Offset(funda.left, funda.top + funda.height * 0.62f), Size(funda.width, funda.height * 0.22f))
                drawRect(k.luz, funda.topLeft, Size(funda.width * 0.35f, funda.height))
                drawRect(TrazosCiudad.enSombra(carton, 0.4f), funda.topLeft, funda.size, style = Stroke(0.4f * u))
            } else {
                drawRect(k.papel, funda.topLeft, funda.size)
                trama(Path().apply { addRect(funda) }, 1.8f * u, 0.7f * u, col, -30f)
                drawRect(k.grabado, funda.topLeft, funda.size, style = Stroke(0.7f * u))
            }
        } else disco(Offset(10.6f * u, cy), 6.2f * u, k, giro = azar(e.semilla, 2) * 360f, etiqueta = col)
    }

    /**
     * La ciudad: la flecha de una placa de parada, gruesa y sobria, con su sombra corta. La tinta: la flecha de bloque del
     * cartel, plana, en magenta, impresa otra vez en ultramar corrida de registro.
     */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val k = k(oscuro)
        val s = size.minDimension
        val c = Offset(size.width / 2, size.height / 2)
        if (k.ciudad) {
            val p = Path().apply { moveTo(c.x - s * 0.16f, c.y - s * 0.3f); lineTo(c.x + s * 0.16f, c.y); lineTo(c.x - s * 0.16f, c.y + s * 0.3f) }
            translate(s * 0.04f, s * 0.05f) { drawPath(p, k.sombra, style = Stroke(s * 0.13f, cap = StrokeCap.Round, join = StrokeJoin.Round)) }
            drawPath(p, k.primario, style = Stroke(s * 0.13f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        } else {
            val p = Path().apply {
                moveTo(c.x - s * 0.36f, c.y - s * 0.1f); lineTo(c.x, c.y - s * 0.1f); lineTo(c.x, c.y - s * 0.3f); lineTo(c.x + s * 0.38f, c.y)
                lineTo(c.x, c.y + s * 0.3f); lineTo(c.x, c.y + s * 0.1f); lineTo(c.x - s * 0.36f, c.y + s * 0.1f); close()
            }
            translate(s * 0.06f, s * 0.05f) { drawPath(p, k.ultramar.copy(alpha = 0.75f)) }
            conRegistro(TrazosTinta.Tinta.MAGENTA, k.m.registro * 0.4f) { drawPath(p, k.primario) }
        }
    }

    /**
     * La ciudad: la chincheta del corcho: fija, la cabeza de color con su brillo y su sombra; sin fijar, solo su contorno. La
     * tinta: fija, el disco hecho sol; sin fijar, la corona en línea, sin tinta.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val k = k(oscuro)
        val s = size.minDimension
        val c = Offset(size.width / 2, size.height / 2)
        if (k.ciudad) {
            if (fija) {
                drawLine(k.sombra, c, c + Offset(s * 0.32f, s * 0.36f), s * 0.07f, StrokeCap.Round)
                drawCircle(k.sombra, s * 0.3f, c + Offset(s * 0.05f, s * 0.07f))
                drawCircle(k.primario, s * 0.3f, c)
                drawCircle(TrazosCiudad.enSombra(k.primario, 0.35f), s * 0.18f, c)
                drawArc(TrazosCiudad.AL_SOL.copy(alpha = 0.6f), 200f, 60f, false, Offset(c.x - s * 0.22f, c.y - s * 0.22f), Size(s * 0.44f, s * 0.44f), style = Stroke(s * 0.05f, cap = StrokeCap.Round))
            } else {
                drawCircle(k.tenue.copy(alpha = 0.6f), s * 0.28f, c, style = Stroke(s * 0.05f))
                drawCircle(k.tenue.copy(alpha = 0.6f), s * 0.12f, c, style = Stroke(s * 0.04f))
            }
        } else {
            if (fija) {
                corona(c, s * 0.24f, s * 0.48f, k, n = 14)
                disco(c, s * 0.24f, k, sombra = false)
            } else drawPath(PiezasPsicodelia.cunas(c, s * 0.22f, s * 0.46f, 14, 0.5f, 0.12f, 0f), k.tenue.copy(alpha = 0.6f), style = Stroke(s * 0.035f, join = StrokeJoin.Round))
        }
    }

    override val tamDibujoVacio: Dp = 48.dp
    override fun tintaVacio(oscuro: Boolean): Color = k(oscuro).tenue

    /**
     * El recuadro de lo vacío. La ciudad: la placa con su luz y su grano y, en los márgenes, la calle que no tiene nada:
     * arriba, los cables del trolebús que salen de un poste en la esquina; abajo, la hilera de victorianas grises (una
     * pintada), tenues, con su mirador. La tinta: el cartel en blanco, de arco, con la corona que asoma en la esquina de
     * arriba, la trama abajo a la derecha y una banda a dos tintas al pie. Todo en los márgenes: el texto va en el centro.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val k = k(oscuro)
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 40 * u) return
        enCache(clave(k, "vacio", e.semilla)) {
            val r = Rect(1f * u, 1f * u, w - 2.5f * u, h - 3f * u)
            val forma = formaPlaca(r, k, 11 * u)
            placa(forma, k, k.superficie, if (k.ciudad) k.borde.copy(alpha = 0.7f) else k.grabado.copy(alpha = 0.8f), 1f * u)
            clipPath(forma) {
                if (k.ciudad) {
                    // La hilera de casas, a 15 dp del pie como mucho: tenue, para que no compita.
                    val pie = r.bottom
                    var x = r.left - 4 * u; var j = 0
                    while (x < r.right && j < 60) {
                        val ancho = (24f + 10f * azar(e.semilla + 3, j)) * u
                        val alto = (9f + 6f * azar(e.semilla + 4, j)) * u
                        val col = TrazosCiudad.CASAS[(j + e.semilla).mod(TrazosCiudad.CASAS.size)].let { if (oscuro) TrazosCiudad.enSombra(it, 0.6f) else it }.copy(alpha = 0.38f)
                        val casa = Path().apply {
                            moveTo(x, pie); lineTo(x, pie - alto)
                            if (j % 3 == 1) { lineTo(x + ancho / 2, pie - alto - 4 * u); lineTo(x + ancho, pie - alto) } else lineTo(x + ancho, pie - alto)
                            lineTo(x + ancho, pie); close()
                        }
                        drawPath(casa, col)
                        drawRect(TrazosCiudad.alSol(col, 0.4f), Offset(x, pie - alto), Size(ancho * 0.12f, alto))
                        if (j % 2 == 0) drawRect(TrazosCiudad.enSombra(col, 0.3f), Offset(x + ancho * 0.25f, pie - alto + 2 * u), Size(ancho * 0.22f, min(alto * 0.6f, 6 * u)))
                        x += ancho + 1.2f * u; j++
                    }
                    val poste = k.tenue.copy(alpha = 0.5f)
                    drawLine(poste, Offset(r.left + 14 * u, r.top), Offset(r.left + 14 * u, r.top + 18 * u), 2.2f * u)
                    cablesTrolebus(r.left + 14 * u, r.right, r.top + 8 * u, poste, tramo = 160 * u)
                } else {
                    val sol = Offset(r.left + 4 * u, r.top + 4 * u)
                    corona(sol, 10 * u, 26 * u, k, n = 20)
                    disco(sol, 9 * u, k, sombra = false)
                    val b = Offset(r.right, r.bottom)
                    trama(Path().apply { moveTo(b.x, b.y); lineTo(b.x - 40 * u, b.y); lineTo(b.x, b.y - 40 * u); close() }, 2.2f * u, 0.8f * u, k.ultramar, 45f)
                    ondas(r.left + 12 * u, r.right - 44 * u, r.bottom - 7 * u, 1.4f * u, 24 * u, 1.3f * u, 2.6f * u, PiezasPsicodelia.tintasBanda(k), k.m.registro * 0.5f)
                }
            }
        }
    }

    /**
     * Lo vacío: la funda de un disco sin su disco. La ciudad: la funda de cartón, un poco ladeada, con su franja de color y el
     * agujero redondo por el que no se ve ningún disco, y la funda interior de papel que asoma. La tinta: la misma funda
     * impresa: la corona de rayos alrededor del agujero vacío, en la tinta que graba, con la etiqueta que falta en magenta.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val k = k(oscuro)
        val s = size.minDimension
        val u = density
        val lado = s * 0.7f
        val x0 = size.width / 2 - s * 0.42f
        val portada = Rect(x0, s * 0.2f, x0 + lado, s * 0.2f + lado)
        val interior = Rect(portada.left + s * 0.2f, portada.top - s * 0.08f, portada.left + s * 0.2f + lado * 0.95f, portada.top - s * 0.08f + lado * 0.95f)
        // La funda interior, de papel, con su agujero.
        rotate(6f, interior.center) {
            val funda = Path().apply {
                fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
                addRect(interior); addOval(Rect(interior.center, lado * 0.11f))
            }
            if (k.ciudad) translate(s * 0.012f, s * 0.02f) { drawPath(funda, k.sombra) }
            drawPath(funda, if (k.ciudad) (if (oscuro) Color(0xFFBDB09A) else Color(0xFFF6F0E2)) else k.papel)
            drawPath(funda, if (k.ciudad) k.borde else k.grabado, style = Stroke(max(0.5f * u, s * 0.012f)))
        }
        // La portada.
        rotate(-4f, portada.center) {
            val forma = Path().apply {
                fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
                addRect(portada); addOval(Rect(portada.center, lado * 0.2f))
            }
            if (k.ciudad) {
                translate(s * 0.015f, s * 0.03f) { drawPath(forma, k.sombra) }
                val carton = if (oscuro) Color(0xFF8C7656) else Color(0xFFD9C7A2)
                drawPath(forma, carton)
                clipPath(forma) {
                    drawRect(TrazosCiudad.enSombra(k.primario, 0.1f), Offset(portada.left, portada.top + lado * 0.72f), Size(lado, lado * 0.16f))
                    drawRect(Brush.horizontalGradient(listOf(k.luz, Color.Transparent), portada.left, portada.right))
                    granoPelicula(forma, k.grano)
                }
                drawPath(forma, TrazosCiudad.enSombra(carton, 0.45f), style = Stroke(max(0.5f * u, s * 0.01f)))
            } else {
                drawPath(forma, k.papel)
                clipPath(forma) { corona(portada.center, lado * 0.22f, lado * 0.62f, k, n = 18) }
                conRegistro(TrazosTinta.Tinta.MAGENTA, k.m.registro * 0.3f) {
                    drawCircle(k.magenta, lado * 0.2f, portada.center, style = Stroke(max(0.8f * u, s * 0.03f)))
                }
                drawPath(forma, k.grabado, style = Stroke(max(0.6f * u, s * 0.014f)))
            }
        }
    }

    override val margenAviso: Dp = 38.dp
    override fun tintaAviso(oscuro: Boolean): Color = k(oscuro).tinta

    /**
     * El aviso. La ciudad: la placa con su franja de color a la izquierda y, en el margen, el disco o el pase de autobús. La
     * tinta: la cartela de arco con el disco hecho sol en el margen (o, según el aviso, tres bandas que suben).
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val k = k(oscuro)
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 20 * u) return
        val r = Rect(0.6f * u, 0.6f * u, w - 1.6f * u, h - 2f * u)
        val forma = formaPlaca(r, k, 10 * u)
        placa(forma, k, k.superficie, if (k.ciudad) k.borde.copy(alpha = 0.7f) else k.grabado.copy(alpha = 0.7f), 0.9f * u)
        val cy = minOf(h / 2, 21 * u)
        clipPath(forma) { if (k.ciudad) drawRect(k.primario, r.topLeft, Size(3f * u, r.height)) }
        if (k.ciudad) {
            if (e.semilla % 2 == 0) disco(Offset(20 * u, cy), 9 * u, k, giro = 30f)
            else pase(Rect(8 * u, cy - 7 * u, 32 * u, cy + 7 * u), k, -8f)
        } else {
            if (e.semilla % 2 == 0) {
                corona(Offset(19 * u, cy), 7 * u, 14 * u, k, n = 14)
                disco(Offset(19 * u, cy), 7 * u, k, sombra = false)
            } else {
                val altoB = min(r.height - 8 * u, 26 * u)
                listOf(k.ultramar, k.magenta, if (k.m.bermellon > 0f) k.bermellon else k.terciario).forEachIndexed { j, c ->
                    val x = 12 * u + j * 5.4f * u
                    val p = Path()
                    var t = 0f
                    while (t <= 1.001f) {
                        val yy = cy + altoB / 2 - altoB * t
                        val xx = x + 1.4f * u * kotlin.math.sin(t * 2f * kotlin.math.PI.toFloat() * 1.5f + j)
                        if (t == 0f) p.moveTo(xx, yy) else p.lineTo(xx, yy)
                        t += 0.05f
                    }
                    drawPath(p, c, style = Stroke(2f * u, cap = StrokeCap.Round))
                }
            }
        }
    }

    override val margenCifra: Dp = 12.dp
    override fun tintaCifra(oscuro: Boolean): Color = k(oscuro).tinta
    override fun rotuloCifra(oscuro: Boolean): Color = k(oscuro).tenue

    /**
     * La cifra. La ciudad: el pase de autobús, la cartulina con su luz y su grano, el talón a la derecha separado por el
     * picado, tenido del color de la línea, con la picadura del revisor. La tinta: la entrada del baile, impresa: la cartela
     * de arco con el talón de trama (de ultramar; con la intensidad, con su franja de bermellón).
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val k = k(oscuro)
        val u = density
        val w = size.width; val h = size.height
        if (w < 30 * u || h < 20 * u) return
        val r = Rect(0.4f * u, 0.4f * u, w - 1.2f * u, h - 1.8f * u)
        val xp = r.right - 8.5f * u
        val forma = formaPlaca(r, k, 8 * u)
        placa(forma, k, k.variante, if (k.ciudad) null else k.grabado.copy(alpha = 0.55f), 0.8f * u)
        clipPath(forma) {
            val talon = Rect(xp, r.top, r.right, r.bottom)
            if (k.ciudad) {
                drawRect(lerp(k.variante, k.primario, 0.22f), talon.topLeft, talon.size)
                drawCircle(k.sombra, 1.6f * u, Offset(talon.center.x, r.top + r.height * 0.3f))
            } else {
                trama(Path().apply { addRect(talon) }, 2f * u, 0.8f * u, k.ultramar, 0f)
                if (k.m.bermellon > 0f) drawRect(k.bermellon.copy(alpha = k.m.bermellon), Offset(talon.left, talon.bottom - talon.height * 0.25f), Size(talon.width, talon.height * 0.25f))
            }
        }
        drawLine(if (k.ciudad) k.borde else k.grabado.copy(alpha = 0.5f), Offset(xp, r.top + 3 * u), Offset(xp, r.bottom - 3 * u), 0.8f * u, StrokeCap.Round,
            PathEffect.dashPathEffect(floatArrayOf(0.1f * u, 2.2f * u)))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 14.dp

    /** La noche siempre es oscura (el cuarto con la lámpara, o el cartel de luz negra), también con el modo claro. */
    private fun kTarjeta(tipo: TipoTarjeta, oscuro: Boolean): ColoresPsi = when (tipo) {
        TipoTarjeta.NOCHE -> k(true)
        TipoTarjeta.METRICA -> k(oscuro)
        TipoTarjeta.PENDIENTE -> k(oscuro)
        TipoTarjeta.VIDA -> k(oscuro)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = kTarjeta(tipo, oscuro).tinta
    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = kTarjeta(tipo, oscuro).tenue
    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> k(oscuro).primario
        TipoTarjeta.PENDIENTE -> k(oscuro).secundario
        TipoTarjeta.NOCHE -> k(true).primario
        TipoTarjeta.VIDA -> k(oscuro).terciario
    }

    /**
     * Las tarjetas (en caché por variante, escalón y estado). Todo el dibujo va en el margen de 14 dp; donde va el texto, la
     * placa es opaca.
     * - La cifra de Hoy: la ciudad, la foto pegada en el álbum (la placa con su luz y su grano, y las esquineras de papel);
     *   destacada, la franja de color a la izquierda. La tinta: la cartela de arco con su triángulo de trama en la esquina;
     *   destacada, el filete en magenta corrido de registro y el disco hecho sol que asoma en la esquina.
     * - «Te falta hoy»: la ventanilla del trolebús, con su marco de chapa y su goma, el vidrio un poco empañado de niebla;
     *   completa, la luz de la tarde en el marco. La tinta: el marco doble del cartel.
     * - La noche (siempre oscura): el cuarto con la lámpara de pantalla en la esquina; sellada, la lámpara encendida y el
     *   disco en el plato. La tinta: el negro de terciopelo con la corona que asoma arriba; sellada, el filete magenta.
     * - Vida: el recorte del periódico del barrio, con el filete doble de la cabecera. La tinta: el periódico a dos tintas,
     *   con la franja del tintero partido (de magenta a ultramar) en la cabecera.
     * Al tocarlas, la placa se hunde un poco.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        if (size.width < 24 * u || size.height < 24 * u) return
        val k = kTarjeta(e.tipo, oscuro)
        enCache(clave(k, "tarjeta", e.tipo, e.destacado, e.presionado)) {
            when (e.tipo) {
                TipoTarjeta.METRICA -> tarjetaFoto(e, k)
                TipoTarjeta.PENDIENTE -> tarjetaVentanilla(e, k)
                TipoTarjeta.NOCHE -> tarjetaNoche(e, k)
                TipoTarjeta.VIDA -> tarjetaPeriodico(e, k)
            }
        }
    }

    /** La placa de una tarjeta, con su forma, hundida si se toca. */
    private fun DrawScope.baseTarjeta(e: EstadoTarjeta, k: ColoresPsi, relleno: Color, linea: Color?): Pair<Rect, Path> {
        val u = density
        val r = Rect(0.6f * u, 0.6f * u, size.width - 1.8f * u, size.height - 2.4f * u)
        val forma = formaPlaca(r, k, 11 * u)
        val fondo = if (e.presionado) lerp(relleno, if (k.oscuro) Color.Black else TrazosCiudad.SOMBRA, 0.07f) else relleno
        placa(forma, k, fondo, linea, if (!k.ciudad && e.destacado) 1.6f * u else 1f * u)
        return r to forma
    }

    private fun DrawScope.tarjetaFoto(e: EstadoTarjeta, k: ColoresPsi) {
        val u = density
        val linea = if (k.ciudad) k.borde.copy(alpha = 0.55f) else if (e.destacado) k.magenta else k.grabado.copy(alpha = 0.7f)
        val (r, forma) = baseTarjeta(e, k, k.superficie, linea)
        clipPath(forma) {
            if (k.ciudad) {
                if (e.destacado) drawRect(k.primario, r.topLeft, Size(4f * u, r.height))
            } else {
                val b = Offset(r.right, r.top)
                trama(Path().apply { moveTo(b.x, b.y); lineTo(b.x - 24 * u, b.y); lineTo(b.x, b.y + 24 * u); close() }, 2f * u, 0.7f * u, k.ultramar, 45f)
                if (e.destacado) {
                    val c = Offset(r.right - 2 * u, r.top + 2 * u)
                    corona(c, 7 * u, 13 * u, k, n = 16)
                    disco(c, 6.5f * u, k, sombra = false)
                }
            }
        }
        if (k.ciudad) with(PiezasPsicodelia) { esquinas(3 * u, 4.2f * u, k) }
    }

    private fun DrawScope.tarjetaVentanilla(e: EstadoTarjeta, k: ColoresPsi) {
        val u = density
        if (k.ciudad) {
            val r = Rect(0.6f * u, 0.6f * u, size.width - 1.8f * u, size.height - 2.4f * u)
            val marco = Path().apply { addRoundRect(RoundRect(r, CornerRadius(11 * u))) }
            val chapa = if (k.oscuro) Color(0xFF4A3F35) else Color(0xFFB9AE98)
            translate(0.8f * u, 1.2f * u) { drawPath(marco, k.sombra) }
            drawPath(marco, chapa)
            drawPath(marco, Brush.verticalGradient(listOf(TrazosCiudad.alSol(chapa, 0.4f), chapa, TrazosCiudad.enSombra(chapa, 0.25f)), r.top, r.bottom))
            if (e.destacado) drawPath(marco, Brush.horizontalGradient(listOf(TrazosCiudad.LUZ_BAJA.copy(alpha = 0.45f), Color.Transparent), r.left, r.left + r.width * 0.5f))
            granoPelicula(marco, k.grano)
            // La goma negra y el vidrio.
            val vidrio = Rect(r.left + 5 * u, r.top + 5 * u, r.right - 5 * u, r.bottom - 5 * u)
            val goma = Path().apply { addRoundRect(RoundRect(vidrio, CornerRadius(8 * u))) }
            drawPath(goma, Color(0xFF2A221C), style = Stroke(2.4f * u))
            val fondo = if (e.presionado) lerp(k.superficie, TrazosCiudad.SOMBRA, 0.06f) else k.superficie
            drawPath(goma, fondo)
            clipPath(goma) {
                drawRect(Brush.verticalGradient(listOf((if (k.oscuro) TrazosCiudad.LAMPARA_LUZ.copy(alpha = 0.08f) else TrazosCiudad.BRUMA.copy(alpha = 0.55f)), Color.Transparent),
                    vidrio.top, vidrio.top + vidrio.height * 0.45f))
                drawLine(Color.White.copy(alpha = if (k.oscuro) 0.05f else 0.35f), Offset(vidrio.left + vidrio.width * 0.08f, vidrio.top + 2 * u),
                    Offset(vidrio.left + vidrio.width * 0.02f, vidrio.top + 14 * u), 1.4f * u, StrokeCap.Round)
            }
        } else {
            val (r, _) = baseTarjeta(e, k, k.superficie, k.grabado)
            val dentro = Rect(r.left + 4 * u, r.top + 4 * u, r.right - 4 * u, r.bottom - 4 * u)
            val f2 = TrazosTinta.arco(dentro, 10 * u, 1.5f * u)
            if (e.destacado) conRegistro(TrazosTinta.Tinta.MAGENTA, k.m.registro * 0.5f) { drawPath(f2, k.magenta, style = Stroke(1.4f * u)) }
            else drawPath(f2, k.grabado.copy(alpha = 0.45f), style = Stroke(0.7f * u))
        }
    }

    private fun DrawScope.tarjetaNoche(e: EstadoTarjeta, k: ColoresPsi) {
        val u = density
        val linea = if (k.ciudad) k.borde else if (e.destacado) k.magenta else k.borde
        val (r, forma) = baseTarjeta(e, k, k.superficie, linea)
        clipPath(forma) {
            if (k.ciudad) {
                // La lámpara de pantalla plisada, en la esquina de arriba a la derecha, y su luz en la pared.
                val c = Offset(r.right - 13 * u, r.top + 2 * u)
                val luz = if (e.destacado) 0.2f else 0.1f
                drawCircle(Brush.radialGradient(listOf(TrazosCiudad.LAMPARA_LUZ.copy(alpha = luz), Color.Transparent), c + Offset(0f, 8 * u), 46 * u), 46 * u, c + Offset(0f, 8 * u))
                val pantalla = Path().apply { moveTo(c.x - 5 * u, c.y); lineTo(c.x + 5 * u, c.y); lineTo(c.x + 8.5f * u, c.y + 9 * u); lineTo(c.x - 8.5f * u, c.y + 9 * u); close() }
                drawPath(pantalla, if (e.destacado) TrazosCiudad.LAMPARA else TrazosCiudad.enSombra(TrazosCiudad.LAMPARA, 0.45f))
                for (j in -3..3) drawLine(TrazosCiudad.enSombra(TrazosCiudad.LAMPARA, 0.3f).copy(alpha = 0.4f), Offset(c.x + j * 1.4f * u, c.y), Offset(c.x + j * 2.4f * u, c.y + 9 * u), 0.4f * u)
                if (e.destacado) disco(Offset(r.right - 8 * u, r.bottom - 8 * u), 10 * u, k, giro = 20f)
                granoPelicula(forma, k.grano)
            } else {
                val c = Offset(r.right - 4 * u, r.top + 4 * u)
                corona(c, 9 * u, 22 * u, k, n = 18)
                disco(c, 8.5f * u, k, sombra = false)
            }
        }
    }

    private fun DrawScope.tarjetaPeriodico(e: EstadoTarjeta, k: ColoresPsi) {
        val u = density
        val papel = if (k.ciudad) lerp(k.superficie, k.variante, 0.35f) else k.superficie
        val (r, forma) = baseTarjeta(e, k, papel, if (k.ciudad) k.borde.copy(alpha = 0.5f) else k.grabado.copy(alpha = 0.7f))
        clipPath(forma) {
            if (k.ciudad) {
                drawLine(k.tinta.copy(alpha = 0.7f), Offset(r.left + 12 * u, r.top + 5 * u), Offset(r.right - 12 * u, r.top + 5 * u), 1.4f * u)
                drawLine(k.tinta.copy(alpha = 0.5f), Offset(r.left + 12 * u, r.top + 7.6f * u), Offset(r.right - 12 * u, r.top + 7.6f * u), 0.6f * u)
                if (e.destacado) drawRect(k.terciario, Offset(r.left, r.top), Size(4f * u, r.height))
            } else {
                // El tintero partido: la franja de la cabecera pasa del magenta al ultramar.
                drawRect(Brush.horizontalGradient(listOf(k.magenta, k.ultramar), r.left, r.right), r.topLeft, Size(r.width, 6 * u))
                drawLine(k.grabado, Offset(r.left, r.top + 8 * u), Offset(r.right, r.top + 8 * u), 0.7f * u)
                if (e.destacado) {
                    val b = Offset(r.right, r.bottom)
                    trama(Path().apply { moveTo(b.x, b.y); lineTo(b.x - 22 * u, b.y); lineTo(b.x, b.y - 22 * u); close() }, 2f * u, 0.7f * u, k.magenta, 45f)
                }
            }
        }
    }

    // ---------------------------------------------------------------- Gastos

    /** Las letras: los rótulos en Libre Franklin seminegra (las dos caras); los títulos, el titular de la ciudad o el bloque de cartel. */
    private val letraRotulos: FontFamily = LetraExtra.franklinRotulo
    private fun letraTitulos(k: ColoresPsi): FontFamily = if (k.ciudad) LetraExtra.franklinTitular else LetraExtra.bloque

    override val altoBalanza: Dp = 64.dp

    /**
     * La balanza de gasto e ingreso: dos placas (redondeadas en la ciudad, de arco en la tinta). La elegida toma su color de
     * fondo y su filete, con su flecha (hacia abajo lo que sale, hacia arriba lo que entra); la otra queda en papel, con la
     * letra tenue. En medio, el disco chico.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val k = k(oscuro)
        val u = density
        val w = size.width; val h = size.height
        if (w < 120 * u || h < 36 * u) return
        val xm = w / 2
        val hueco = 9 * u
        for (lado in 0..1) {
            val ingreso = lado == 1
            val elegido = e.ingreso == ingreso
            val color = if (ingreso) e.colorIngreso else e.colorGasto
            val r = if (ingreso) Rect(xm + hueco, 3f * u, w - 2.2f * u, h - 3.4f * u) else Rect(1f * u, 3f * u, xm - hueco, h - 3.4f * u)
            val forma = formaPlaca(r, k, 12 * u)
            val fondo = if (elegido) lerp(k.superficie, color, if (oscuro) 0.2f else 0.14f) else k.superficie
            placa(forma, k, fondo, if (elegido) color else k.borde.copy(alpha = 0.6f), if (elegido) 1.5f * u else 0.9f * u, grano = elegido)
            val tinta = if (elegido) legiblePsi(color, fondo, k.tinta, 3f) else k.tenue
            val marca = Offset(r.left + 17 * u, r.center.y)
            val flecha = Path().apply {
                val s = if (ingreso) -1f else 1f
                moveTo(marca.x, marca.y + s * 6 * u); lineTo(marca.x - 5 * u, marca.y - s * 0.5f * u); lineTo(marca.x - 1.8f * u, marca.y - s * 0.5f * u)
                lineTo(marca.x - 1.8f * u, marca.y - s * 6 * u); lineTo(marca.x + 1.8f * u, marca.y - s * 6 * u); lineTo(marca.x + 1.8f * u, marca.y - s * 0.5f * u)
                lineTo(marca.x + 5 * u, marca.y - s * 0.5f * u); close()
            }
            if (k.ciudad) { translate(0.6f * u, 0.9f * u) { drawPath(flecha, k.sombra) }; drawPath(flecha, tinta) }
            else {
                if (elegido) translate(1.2f * u, 1f * u) { drawPath(flecha, k.ultramar.copy(alpha = 0.6f)) }
                conRegistro(TrazosTinta.Tinta.MAGENTA, if (elegido) k.m.registro * 0.4f else 0f) { drawPath(flecha, tinta) }
            }
            val texto = if (ingreso) e.textoIngreso else e.textoGasto
            textoEn(medidor, texto, Rect(marca.x + 12 * u, r.top + 2 * u, r.right - 8 * u, r.bottom - 2 * u), letraRotulos,
                legiblePsi(if (elegido) color else k.tenue, fondo, k.tinta), if (elegido) 17f else 15f)
        }
        disco(Offset(xm, h / 2 - 0.4f * u), 6.2f * u, k)
    }

    override val altoMes: Dp = 52.dp

    /**
     * El encabezado del mes. La ciudad: la placa de un letrero pintado (con su filete de color por dentro) con el mes en el
     * titular de los 70, y a los lados las flechas de una placa de parada. La tinta: la cartela de arco, impresa dos veces
     * con el registro corrido, con el mes en la letra de bloque (sin deformar: es un dato), y las flechas de bloque planas.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val k = k(oscuro)
        val u = density
        val w = size.width; val h = size.height
        if (w < 140 * u || h < 30 * u) return
        val tam = 19f
        val ancho = if (e.titulo.isBlank()) 0f else medidor.measure(e.titulo, TextStyle(fontFamily = letraTitulos(k), fontSize = tam.sp), maxLines = 1, softWrap = false).size.width.toFloat()
        val cw = (ancho + 40 * u).coerceIn(120 * u, w - 112 * u)
        val cart = Rect(w / 2 - cw / 2, 5 * u, w / 2 + cw / 2, h - 5 * u)
        if (k.ciudad) with(TrazosCiudad) { placaLetrero(cart, k.superficie, k.primario, 6 * u) }
        else {
            val forma = TrazosTinta.arco(cart, 13 * u, 2 * u)
            translate(1.6f * u, 1.4f * u) { drawPath(forma, k.ultramar.copy(alpha = 0.55f)) }
            drawPath(forma, k.superficie)
            conRegistro(TrazosTinta.Tinta.MAGENTA, k.m.registro * 0.5f) { drawPath(forma, k.magenta, style = Stroke(1.4f * u)) }
            drawPath(forma, k.grabado, style = Stroke(0.8f * u))
        }
        val caja = Rect(cart.left + 12 * u, cart.top + 3 * u, cart.right - 12 * u, cart.bottom - 3 * u)
        textoEn(medidor, e.titulo, caja, letraTitulos(k), legiblePsi(if (k.ciudad) k.tinta else k.primario, k.superficie, k.tinta), tam)
        for (s in listOf(-1f, 1f)) {
            val c = Offset(if (s < 0) 24 * u else w - 24 * u, h / 2)
            val p = Path().apply {
                if (k.ciudad) { moveTo(c.x - s * 4 * u, c.y - 8 * u); lineTo(c.x + s * 4 * u, c.y); lineTo(c.x - s * 4 * u, c.y + 8 * u) }
                else {
                    moveTo(c.x - s * 9 * u, c.y - 2.6f * u); lineTo(c.x, c.y - 2.6f * u); lineTo(c.x, c.y - 8 * u); lineTo(c.x + s * 9 * u, c.y)
                    lineTo(c.x, c.y + 8 * u); lineTo(c.x, c.y + 2.6f * u); lineTo(c.x - s * 9 * u, c.y + 2.6f * u); close()
                }
            }
            if (k.ciudad) {
                translate(0.7f * u, 1f * u) { drawPath(p, k.sombra, style = Stroke(3f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)) }
                drawPath(p, k.primario, style = Stroke(3f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
            } else {
                translate(1.4f * u, 1.2f * u) { drawPath(p, k.ultramar.copy(alpha = 0.7f)) }
                conRegistro(TrazosTinta.Tinta.MAGENTA, k.m.registro * 0.4f) { drawPath(p, k.primario) }
            }
        }
    }

    /** Un renglón de la cuenta: el surco fino al pie y, al principio, la marca: un triángulo hacia arriba si entra, hacia abajo si sale. */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val k = k(oscuro)
        val u = density
        val w = size.width; val h = size.height
        if (w < 30 * u || h < 10 * u) return
        val y = h - 1.2f * u
        if (k.ciudad) drawLine(Brush.horizontalGradient(0f to k.borde, 0.8f to k.borde.copy(alpha = 0.6f), 1f to k.borde.copy(alpha = 0f), startX = 12 * u, endX = w - 2 * u),
            Offset(12 * u, y), Offset(w - 2 * u, y), 0.7f * u)
        else drawLine(k.grabado.copy(alpha = 0.35f), Offset(12 * u, y), Offset(w - 2 * u, y), 0.8f * u, StrokeCap.Round, PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 2f * u)))
        val p = Offset(5f * u, h - 5.5f * u)
        val s = if (e.ingreso) -1f else 1f
        val tri = Path().apply { moveTo(p.x - 3.8f * u, p.y - s * 2.3f * u); lineTo(p.x + 3.8f * u, p.y - s * 2.3f * u); lineTo(p.x, p.y + s * 3.2f * u); close() }
        val c = if (e.ingreso) k.secundario else k.primario
        if (k.ciudad) drawPath(tri, c) else conRegistro(TrazosTinta.Tinta.MAGENTA, k.m.registro * 0.3f) { drawPath(tri, c) }
    }

    override val altoRotuloDia: Dp = 22.dp

    /**
     * La fecha de un día de la cuenta, en mayúsculas espaciadas. La ciudad: la placa de un autobús, con su sombra corta, y
     * el cable que sigue hasta el borde. La tinta: la cartela de arco, y una onda a dos tintas hasta el borde.
     */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val k = k(oscuro)
        val u = density
        val w = size.width; val h = size.height
        if (w < 60 * u || h < 12 * u) return
        val fecha = texto.uppercase()
        val tam = 11f; val esp = 1.5f
        val ancho = minOf(medidor.measure(fecha, TextStyle(fontFamily = letraRotulos, fontSize = tam.sp, letterSpacing = esp.sp), maxLines = 1, softWrap = false).size.width.toFloat(), w * 0.6f)
        val etiqueta = Rect(3 * u, 2.5f * u, 3 * u + ancho + 16 * u, h - 3 * u)
        val forma = formaPlaca(etiqueta, k, etiqueta.height / 2)
        placa(forma, k, k.variante, if (k.ciudad) null else k.grabado.copy(alpha = 0.6f), 0.8f * u, grano = false)
        textoEn(medidor, fecha, Rect(etiqueta.left + 6 * u, etiqueta.top, etiqueta.right - 6 * u, etiqueta.bottom), letraRotulos,
            legiblePsi(k.tinta, k.variante, k.tinta), tam, espaciado = esp)
        if (etiqueta.right + 30 * u < w) {
            if (k.ciudad) drawLine(k.borde, Offset(etiqueta.right + 4 * u, h / 2), Offset(w - 6 * u, h / 2), 0.8f * u)
            else ondas(etiqueta.right + 4 * u, w - 6 * u, h / 2, 1f * u, 18 * u, 1f * u, 2f * u,
                listOf(TrazosTinta.Tinta.ULTRAMAR to k.ultramar.copy(alpha = 0.7f), TrazosTinta.Tinta.MAGENTA to k.magenta.copy(alpha = 0.7f)), k.m.registro * 0.3f)
        }
    }

    // ---------------------------------------------------------------- adornos de la hoja rápida, las relaciones y la cinta

    /**
     * La hoja rápida tiene arriba el perfil de una nube de cinco lomos (lo corta `QuickActivity`). La ciudad: es la niebla que
     * entra a ras sobre la ciudad, con la luz de la tarde en cada lomo (de noche, la luz de la lámpara) y un filete fino por
     * dentro del borde; abajo a la izquierda, el pase de autobús. La tinta: cada lomo es una mancha plana de luz líquida
     * (magenta, ultramar y, con la intensidad, bermellón), el borde se graba en dos pasadas con el registro corrido y, abajo a
     * la izquierda, asoma el disco hecho sol. Solo el borde de arriba y las esquinas: el centro queda libre.
     */
    override fun DrawScope.adornoQuick(e: EstadoAdorno, oscuro: Boolean) {
        val k = k(oscuro)
        val u = density; val w = size.width; val h = size.height
        if (w < 160 * u || h < 120 * u) return
        enCache(clave(k, "quick")) {
            val p = 16 * u
            val pts = ArrayList<Offset>()
            var x = 0f
            while (x < w) { pts.add(Offset(x, p - altoQuickPsi(x, w, u))); x += 1.5f * u }
            pts.add(Offset(w, p - altoQuickPsi(w, w, u)))
            val nube = Path().apply { moveTo(0f, p + 2 * u); pts.forEach { lineTo(it.x, it.y) }; lineTo(w, p + 2 * u); close() }
            val borde = Path().apply { pts.forEachIndexed { j, q -> if (j == 0) moveTo(q.x, q.y + 2.4f * u) else lineTo(q.x, q.y + 2.4f * u) } }
            if (k.ciudad) {
                clipPath(nube) {
                    GOTAS_QUICK.forEach { g ->
                        val c = Offset(g[0] * w, p - g[2] * u * 0.4f)
                        val rr = g[1] * u
                        val col = if (oscuro) TrazosCiudad.LAMPARA_LUZ.copy(alpha = 0.12f) else TrazosCiudad.LUZ_BAJA.copy(alpha = 0.3f)
                        drawCircle(Brush.radialGradient(listOf(col, Color.Transparent), c, rr), rr, c)
                    }
                }
                drawPath(borde, k.borde.copy(alpha = 0.7f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
                pase(Rect(10 * u, h - 30 * u, 50 * u, h - 11 * u), k, -6f)
            } else {
                val tintas = listOf(k.magenta, k.ultramar, if (k.m.bermellon > 0f) k.bermellon else k.magenta, k.ultramar, k.magenta)
                clipPath(nube) {
                    GOTAS_QUICK.forEachIndexed { j, g ->
                        val c = Offset(g[0] * w, p + 4 * u)
                        drawOval(tintas[j].copy(alpha = if (oscuro) 0.32f else 0.22f), Offset(c.x - g[1] * u * 0.7f, c.y - g[2] * u - 6 * u), Size(g[1] * u * 1.4f, (g[2] + 14f) * u))
                    }
                }
                conRegistro(TrazosTinta.Tinta.MAGENTA, k.m.registro * 0.5f) { drawPath(borde, k.magenta, style = Stroke(1.3f * u, cap = StrokeCap.Round)) }
                drawPath(borde, k.grabado, style = Stroke(0.8f * u, cap = StrokeCap.Round))
                val sol = Offset(0f, h)
                clipPath(Path().apply { addRect(Rect(0f, h - 40 * u, 40 * u, h)) }) {
                    corona(sol, 14 * u, 36 * u, k, n = 20)
                    disco(sol, 13 * u, k, sombra = false)
                }
            }
        }
    }

    /**
     * El recuadro de Relaciones. La ciudad: los cables del trolebús por dentro del canto de arriba y, abajo a la izquierda, la
     * etiqueta del disco. La tinta: una onda a dos tintas arriba y un triángulo de trama abajo a la derecha. Tenue en el centro.
     */
    override fun DrawScope.adornoRelacion(e: EstadoAdorno, oscuro: Boolean) {
        val k = k(oscuro)
        val u = density; val w = size.width; val h = size.height
        if (w < 40 * u || h < 30 * u) return
        if (k.ciudad) {
            cablesTrolebus(8 * u, w - 8 * u, 6 * u, e.primario.copy(alpha = e.primario.alpha * 0.5f), tramo = w)
            drawCircle(e.secundario.copy(alpha = e.secundario.alpha * 0.5f), 3.2f * u, Offset(8 * u, h - 8 * u))
            drawCircle(e.fondo, 0.8f * u, Offset(8 * u, h - 8 * u))
        } else {
            ondas(8 * u, w - 8 * u, 6 * u, 1f * u, 18 * u, 1f * u, 2f * u,
                listOf(TrazosTinta.Tinta.ULTRAMAR to k.ultramar.copy(alpha = 0.6f), TrazosTinta.Tinta.MAGENTA to e.primario.copy(alpha = e.primario.alpha * 0.6f)), k.m.registro * 0.3f)
            val b = Offset(w - 3 * u, h - 3 * u)
            trama(Path().apply { moveTo(b.x, b.y); lineTo(b.x - 14 * u, b.y); lineTo(b.x, b.y - 14 * u); close() }, 1.8f * u, 0.6f * u, e.secundario.copy(alpha = e.secundario.alpha * 0.6f), 45f)
        }
    }

    /**
     * El marco del ícono de la cinta. La ciudad: el canto del disco, un aro con el brillo de la tarde arriba a la izquierda. La
     * tinta: la corona de rayos que ondulan. Los dos dejan libre el 22 % de cada lado, donde va el glifo.
     */
    override fun DrawScope.marcoPista(e: EstadoAdorno, oscuro: Boolean) {
        val k = k(oscuro)
        val s = size.minDimension
        val c = Offset(size.width / 2, size.height / 2)
        if (k.ciudad) {
            drawCircle(e.primario, s * 0.46f, c, style = Stroke(maxOf(s * 0.07f, 0.8f)))
            drawArc(TrazosCiudad.AL_SOL.copy(alpha = 0.5f), 200f, 50f, false, Offset(c.x - s * 0.46f, c.y - s * 0.46f), Size(s * 0.92f, s * 0.92f),
                style = Stroke(maxOf(s * 0.04f, 0.6f), cap = StrokeCap.Round))
        } else drawPath(PiezasPsicodelia.cunas(c, s * 0.39f, s * 0.5f, 16, 0.5f, 0.1f, 0f), e.primario)
    }
}
