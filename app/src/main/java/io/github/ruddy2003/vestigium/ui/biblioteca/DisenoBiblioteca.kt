package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.TemaId

// =====================================================================
//  Las bibliotecas de cada tema: el estante del diario (y, en los lotes siguientes, la tapa, la libreta y las cartas)
// =====================================================================

/**
 * Un día guardado en el estante, tal como se dibuja: el número del día y el mes como los nombra el tema, cuántas palabras
 * tiene, el color de su ánimo (o del sueño, de noche; null si no se marcó), una semilla para sus irregularidades y si es
 * de noche. [lleno] va de 0 a 1 con las palabras (800 o más es lleno).
 */
class Volumen(val dia: String, val mes: String, val palabras: Int, val tono: Color?, val semilla: Int, val noche: Boolean) {
    val lleno: Float get() = (palabras / 800f).coerceIn(0f, 1f)
}

/** Un rótulo del estante (el mes de un grupo de volúmenes), en el canto de la tabla donde empieza el grupo. */
class PlacaEstante(val r: Rect, val texto: String)

/**
 * Dónde va cada parte del estante, en píxeles del lienzo: la corona, el hueco de cada repisa (donde se paran los volúmenes),
 * la tabla bajo cada repisa, los costados, la base y los rótulos de los meses.
 */
class DisposicionEstante(
    val corona: Rect, val repisas: List<Rect>, val tablas: List<Rect>, val izquierda: Rect, val derecha: Rect, val base: Rect,
    val placas: List<PlacaEstante>,
)

/**
 * Todo lo que la biblioteca del diario dibuja para un tema ([disenoBiblioteca]): el mueble entero donde se guardan las
 * entradas (el hondana de paulonia, el estante del registro…) y cada entrada como objeto del tema (el estuche de tela, el
 * archivador de palanca…). El texto (día, mes, rótulos) se dibuja dentro, con [letra].
 */
interface DisenoBiblioteca {
    // ---------------------------------------------------------------- el mueble

    /** Alto de la corona, en fracción del ancho del mueble. */
    val corona: Float
    /** Alto de la base, en fracción del ancho. */
    val base: Float
    /** Ancho de cada costado, en fracción del ancho. */
    val lado: Float
    /** Alto del hueco de cada repisa. */
    val altoRepisa: Dp
    /** Grosor de cada tabla (su canto lleva el rótulo del mes). */
    val tabla: Dp
    /** Espacio entre volúmenes. */
    val separa: Dp
    /** La letra de los rótulos y de los volúmenes. */
    val letra: FontFamily

    /** El mueble: la corona, el fondo de cada repisa, las tablas con sus rótulos, los costados y la base. */
    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean)

    // ---------------------------------------------------------------- cada entrada

    /** Qué tan grueso es un volumen según las palabras de la entrada. */
    fun ancho(palabras: Int): Dp
    /**
     * Alto del volumen, en fracción del hueco de la repisa: cada uno un poco distinto según su [semilla]; los que van
     * tumbados (rollos, pothi) engordan con las [palabras].
     */
    fun alto(semilla: Int, palabras: Int): Float
    /** Un volumen entero, en su lienzo: el objeto, sus marcas y el día y el mes dibujados con [letra]. */
    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean)
}

/** El diseño de la biblioteca de cada tema: cada uno con su mueble y su volumen propios. */
fun disenoBiblioteca(t: TemaId): DisenoBiblioteca = when (t) {
    TemaId.ROMANO -> EstanteRomano
    TemaId.URBE -> EstanteUrbe
    TemaId.CIBER -> EstanteCiber
    TemaId.NEON -> EstanteNeon
    TemaId.HELENO -> EstanteHeleno
    TemaId.EGIPTO -> EstanteEgipto
    TemaId.MEDIEVAL -> EstanteMedieval
    TemaId.TEMPLARIO -> EstanteTemplario
    TemaId.DISTOPIA -> EstanteDistopia
    TemaId.REFUGIO -> EstanteRefugio
    TemaId.ORDEN -> EstanteOrden
    TemaId.VAPOR -> EstanteVapor
    TemaId.LABORATORIO -> EstanteLaboratorio
    TemaId.COLONOS -> EstanteColonos
    TemaId.NAPOLEON -> EstanteNapoleon
    TemaId.DECO -> EstanteDeco
    TemaId.BOTANICO -> EstanteBotanico
    TemaId.RUINAS -> EstanteRuinas
    TemaId.NAVIDAD -> EstanteNavidad
    TemaId.SEDA -> EstanteSeda
    TemaId.EDO -> EstanteEdo
    TemaId.GOTICO -> EstanteGotico
    TemaId.ARCANO -> EstanteArcano
    TemaId.ESPACIO -> EstanteEspacio
    TemaId.ABISMO -> EstanteAbismo
    TemaId.ANDALUS -> EstanteAndalus
    TemaId.MESO -> EstanteMeso
    TemaId.MONGOL -> EstanteMongol
    TemaId.RENACIMIENTO -> EstanteRenacimiento
    TemaId.PIRATA -> EstantePirata
    TemaId.URSS -> EstanteUrss
    TemaId.REVOLUCION -> EstanteRevolucion
    TemaId.ANTIGUAS -> EstanteAntiguas
    TemaId.CUEVA -> EstanteCueva
}

// =====================================================================
//  Ayudantes de la biblioteca
// =====================================================================

/**
 * Un texto dibujado dentro de [r], centrado: si no cabe, se achica (hasta [minimo] sp) y, si aun así no cabe, se corta. Con
 * [vertical] corre de abajo arriba, a lo largo de un lomo.
 */
internal fun DrawScope.textoEn(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, color: Color, tam: Float,
                               vertical: Boolean = false, peso: FontWeight = FontWeight.Normal, espaciado: Float = 0f, minimo: Float = 6f) {
    if (texto.isBlank() || r.width <= 1f || r.height <= 1f) return
    val largo = if (vertical) r.height else r.width
    val grueso = if (vertical) r.width else r.height
    var estilo = TextStyle(fontFamily = letra, fontSize = tam.sp, lineHeight = (tam * 1.05f).sp, color = color, fontWeight = peso,
        textAlign = TextAlign.Center, letterSpacing = espaciado.sp)
    var capa = medidor.measure(texto, estilo, maxLines = 1, softWrap = false)
    val k = minOf(1f, largo / capa.size.width.coerceAtLeast(1), grueso / capa.size.height.coerceAtLeast(1))
    if (k < 1f) {
        val nuevo = maxOf(minimo, tam * k)
        estilo = estilo.copy(fontSize = nuevo.sp, lineHeight = (nuevo * 1.05f).sp)
        capa = medidor.measure(texto, estilo, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis,
            constraints = Constraints(maxWidth = (kotlin.math.ceil(largo).toInt() + 1).coerceAtLeast(1)))
    }
    val c = r.center
    val esquina = Offset(c.x - capa.size.width / 2f, c.y - capa.size.height / 2f)
    if (vertical) rotate(-90f, c) { drawText(capa, topLeft = esquina) } else drawText(capa, topLeft = esquina)
}
