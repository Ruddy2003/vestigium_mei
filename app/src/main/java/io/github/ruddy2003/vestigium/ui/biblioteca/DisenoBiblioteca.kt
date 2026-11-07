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
import io.github.ruddy2003.vestigium.ui.Pagina
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

    // ---------------------------------------------------------------- la tapa: el diario de hoy, cerrado

    /** Alto de la tapa (el objeto ocupa todo el ancho). */
    val altoTapa: Dp
    /** Dónde va el extracto de lo escrito, en fracciones de la tapa (izquierda, arriba, derecha, abajo como bordes). */
    val cajaTapa: Rect
    /** La letra del extracto. */
    val letraTapa: FontFamily
    /** Tamaño del extracto, en sp. */
    val tamTapa: Float
    /** Cuántos renglones del extracto caben. */
    val lineasTapa: Int
    /** Si el extracto va centrado. */
    val centradoTapa: Boolean
    /** El color del extracto sobre la tapa. */
    fun tintaTapa(oscuro: Boolean, noche: Boolean): Color
    /** Cómo dice el tema cuántas palabras hay («120 palabras», «[120 PALABRAS]», «120 palabras bajo el agua»…). */
    fun palabrasTapa(n: Int): String

    /**
     * El diario del día cerrado, como objeto del tema (el mismo objeto que sus volúmenes en el estante, pero grande): la
     * fecha ([EstadoTapa.fecha]), la cuenta de palabras ([palabrasTapa]) y los rótulos del tema dibujados dentro con
     * [textoEn]. El extracto lo pone la pantalla encima, en [cajaTapa]. Con [EstadoTapa.abierto] de 0 a 1 se abre: la
     * tapa gira, el rollo se desenrolla, la solapa se levanta, la pantalla se enciende…
     */
    fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean)

    // ---------------------------------------------------------------- la página abierta y el escritor

    /** Alto del encabezado de la página (el ancho es el de la columna de texto). */
    val altoEncabezado: Dp

    /**
     * El encabezado de la página abierta, dibujado por el tema sobre el papel [p] (la cabecera de una gaceta, la cartela de
     * un códice, el sello y la jornada, la ruta de un archivo en la terminal…): la cabecera del tema ([EstadoEncabezado.rotulo]),
     * la fecha y el detalle (día de la semana · hace cuánto) dibujados dentro con [textoEn]. De noche, el del sueño.
     */
    fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean)

    /**
     * Lo que el tema pone sobre el papel de la página, por debajo del texto: márgenes, filetes, juntas, sellos, la cenefa…
     * En modo oscuro ([oscuro]), además, la luz con que se lee (una lámpara, la vela, el resplandor de la pantalla): el papel
     * se hunde en la penumbra pero el centro, donde va el texto, queda legible. De noche ([noche]), la página del sueño.
     */
    fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean)

    /** La franja de la barra de arriba de la página (atrás, cuántas palabras, guardar), del tamaño del lienzo. */
    fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean)

    /** El botón de guardar como pieza del tema (el lacre, la tecla, el sello, la placa de latón…), del tamaño del lienzo. */
    fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean)

    /** El color de la letra del botón de guardar, que contraste con [botonGuardar]. */
    fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color

    // ---------------------------------------------------------------- la libreta de momentos

    /** Alto de la escena de la libreta (ocupa todo el ancho). */
    val altoLibreta: Dp

    /**
     * La libreta de momentos cerrada sobre su soporte (el atril, la mesa de campaña, la consola…): el objeto del tema
     * (el de su `Contenedor`: rollo, tablillas, códice en biombo, pincho de tickets…), que engorda con [EstadoLibreta.hojas],
     * con su nombre y su cuenta dibujados dentro con [textoEn]. Con [EstadoLibreta.abierto] de 0 a 1 se abre.
     */
    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean)

    /** La pestaña de un día en el índice de la libreta (44×56 dp): el día, la letra de la semana y el color del ánimo. */
    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean)

    /** El rótulo de un mes en el índice de la libreta, en una franja de 34 dp a todo el ancho. */
    fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean)

    // ---------------------------------------------------------------- las cartas

    /**
     * El sobre de una carta, propio del tema (su forma, su solapa, su cierre, su sello y su adorno), dibujado en
     * [EstadoCarta.cuerpo]. Lo animan [EstadoCarta.abrir] (la solapa), [EstadoCarta.romper] (el sello se parte),
     * [EstadoCarta.subir] (la carta sale, con el papel [EstadoCarta.hoja] y sus [EstadoCarta.renglones]), [EstadoCarta.cordon]
     * (el cierre de las selladas), [EstadoCarta.brillo] (el sello de la que ya se puede abrir) y [EstadoCarta.escalaSello] /
     * [EstadoCarta.alfaSello] (el sello que cae al sellar). [EstadoCarta.linea1] y [EstadoCarta.linea2] (a quién va, las fechas)
     * se dibujan dentro con [textoEn]; pueden venir vacías.
     */
    fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean)

    /** Dónde pone la pantalla las primeras palabras de la carta que asoma del sobre abierto, en fracciones del lienzo. */
    val cajaPrevia: Rect

    /** Alto del buzón (el mueble de las cartas, a todo el ancho). */
    val altoBuzon: Dp

    /**
     * El buzón del tema (el casillero de la posada, la valija, el tubo neumático, el cofre de despachos…), con tantas cartas
     * como [EstadoBuzon.listas] (las que ya se pueden abrir, que brillan), [EstadoBuzon.selladas] (atadas) y
     * [EstadoBuzon.abiertas], y sus números dibujados dentro.
     */
    fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean)

    // ---------------------------------------------------------------- lo de alrededor: pestañas, buscador, obras y recuerdos

    /** Alto de la fila de pestañas de las bibliotecas (ocupa todo el ancho). */
    val altoPestanas: Dp

    /**
     * Las cinco pestañas de las bibliotecas como un solo objeto del tema (las lengüetas del archivador, los marcadores de
     * cinta, las teclas de función…), en el orden de [EstadoPestanas.nombres]: diario, momentos, sueños, ideario y cartas.
     * Cada una ocupa una quinta parte del ancho (ahí se toca) y lleva su emblema, su nombre y su cuenta dibujados con
     * [textoEn]; la de [EstadoPestanas.actual] se distingue (sobresale, se enciende, queda hundida).
     */
    fun DrawScope.pestanas(e: EstadoPestanas, medidor: TextMeasurer, oscuro: Boolean)

    /** Alto del buscador (ocupa todo el ancho). */
    val altoCampo: Dp
    /** Dónde va el texto que se escribe en el buscador, en fracciones del lienzo. */
    val cajaCampo: Rect
    /** La letra de lo que se escribe en el buscador. */
    val letraCampo: FontFamily
    /** El color de lo que se escribe (y del cursor), que contraste con [campo]. */
    fun tintaCampo(oscuro: Boolean): Color

    /**
     * El marco del buscador (la tablilla de cera, la línea de comando, la lupa sobre el registro…), del tamaño del lienzo.
     * Con [EstadoCampo.vacio] dibuja la pista dentro de [cajaCampo]; con [EstadoCampo.enfocado] cambia (se enciende, el cursor).
     */
    fun DrawScope.campo(e: EstadoCampo, medidor: TextMeasurer, oscuro: Boolean)

    /** El fondo del recuadro de Obras en «Más»: la vitrina, el atril o la repisa donde se abren en abanico las portadas. */
    fun DrawScope.repisaObras(medidor: TextMeasurer, oscuro: Boolean)

    /**
     * La tapa dibujada de una obra sin imagen, del tamaño del lienzo (2:3): el tipo, el título, el autor y el año dibujados con
     * [textoEn]. Con [EstadoPortada.mini] (una miniatura de lista) solo la inicial.
     */
    fun DrawScope.portadaObra(e: EstadoPortada, medidor: TextMeasurer, oscuro: Boolean)

    /** Margen de la tarjeta de un recuerdo, donde empieza el contenido. */
    val margenRecuerdo: Dp
    /** Alto de la franja de arriba de la tarjeta de un recuerdo (donde el tema dibuja «Hace un año», la fecha y el clima). */
    val altoRotuloRecuerdo: Dp
    /** El color del texto de la tarjeta de un recuerdo. */
    fun tintaRecuerdo(oscuro: Boolean): Color

    /**
     * La tarjeta de «este día hace N años» (la postal clavada, el fragmento de fresco, la tira del registro…), del tamaño del
     * lienzo, con «Hace un año» o «Hace N años», la fecha, el clima y el punto del ánimo dibujados en la franja de arriba
     * ([altoRotuloRecuerdo]). Lo demás (lugares, canción, la línea del diario) lo pone la pantalla debajo, con [tintaRecuerdo].
     */
    fun DrawScope.recuerdo(e: EstadoRecuerdo, medidor: TextMeasurer, oscuro: Boolean)
}

/** Las pestañas: sus nombres y cuentas, en orden (diario, momentos, sueños, ideario, cartas), cuál está elegida y una semilla. */
class EstadoPestanas(val nombres: List<String>, val cuentas: List<Int>, val actual: Int, val semilla: Int)

/** El buscador: la pista que se ve vacío, si está vacío y si tiene el foco. */
class EstadoCampo(val pista: String, val vacio: Boolean, val enfocado: Boolean)

/** La tapa dibujada de una obra: su tipo («LIBRO», «PELÍCULA»…), título, autor o revista, año, si es miniatura y una semilla. */
class EstadoPortada(val tipo: String, val titulo: String, val autor: String, val anio: String, val mini: Boolean, val semilla: Int)

/** Un recuerdo: hace cuántos años, la fecha como se escribe, el clima («☂ lluvia 18°», o vacío) y el color del ánimo (null si no hay). */
class EstadoRecuerdo(val anios: Int, val fecha: String, val clima: String, val animo: Color?, val semilla: Int)

/** Lo que se ve de un sobre: sus dos renglones, dónde va (en píxeles) y cómo está de abierto, sellado o brillando. */
class EstadoCarta(
    val linea1: String, val linea2: String, val cuerpo: Rect,
    val abrir: Float, val romper: Float, val subir: Float, val cordon: Float, val brillo: Float,
    val escalaSello: Float, val alfaSello: Float, val hoja: Color, val renglones: Color, val semilla: Int, val alFuturo: Boolean,
)

/** Cuántas cartas hay en el buzón: listas para abrir, selladas (al futuro) y abiertas o enviadas. */
class EstadoBuzon(val listas: Int, val selladas: Int, val abiertas: Int)

/**
 * Lo que se ve de la libreta cerrada: su nombre («Rollo de momentos»), la cuenta («12 columnas · desde el 3 oct»), cuántas
 * hojas tiene (engorda con ellas), cuánto se ha abierto (0 a 1) y una semilla.
 */
class EstadoLibreta(val nombre: String, val cuenta: String, val hojas: Int, val abierto: Float, val semilla: Int)

/** Una pestaña del índice: el número del día, la letra de su semana, el color de su ánimo medio (null si no hay) y una semilla. */
class EstadoPestana(val dia: String, val semana: String, val tono: Color?, val semilla: Int)

/**
 * Lo que dice el encabezado de una página: la cabecera del tema («ACTA · DIVRNA», «CRONACA DI ROMA · Trastevere»…), la
 * fecha como la escribe el tema, el detalle («viernes · hace 2 días») y el número del día del año, por si el tema lo usa
 * (folios, singladuras, cotas). [noche]: la página de un sueño.
 */
class EstadoEncabezado(val rotulo: String, val fecha: String, val detalle: String, val numero: Int, val noche: Boolean)

/**
 * Lo que se ve del diario cerrado de un día: la fecha como la escribe el tema, cuántas palabras tiene, si está en blanco,
 * si es de noche (el sueño), cuánto se ha abierto (0 a 1, al tocarlo) y una semilla para sus irregularidades.
 */
class EstadoTapa(val fecha: String, val palabras: Int, val vacio: Boolean, val noche: Boolean, val abierto: Float, val semilla: Int)

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
