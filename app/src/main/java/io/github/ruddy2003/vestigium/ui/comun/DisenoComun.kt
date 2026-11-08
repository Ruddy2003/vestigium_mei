package io.github.ruddy2003.vestigium.ui.comun

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Dp
import io.github.ruddy2003.vestigium.ui.TemaId

// =====================================================================
//  Las piezas comunes de cada tema: campos, separadores, botones de texto, filas, vacíos y tarjetas
// =====================================================================

/**
 * Lo que las piezas comunes de la interfaz dibujan para un tema ([disenoComun]): un objeto por tema, como el Ideario y las
 * bibliotecas. Cada lote de la fase B le agrega su pieza.
 */
interface DisenoComun {
    // ---------------------------------------------------------------- los campos de texto

    /** Alto mínimo de un campo de un renglón (con su marco). */
    val altoCampo: Dp
    /** Margen a los lados entre el marco y el texto. */
    val margenCampoX: Dp
    /** Margen arriba y abajo entre el marco y el texto. */
    val margenCampoY: Dp
    /** El color de lo que se escribe (y del cursor). */
    fun tintaCampo(oscuro: Boolean): Color
    /** El color de la pista que se ve con el campo vacío. */
    fun pistaCampo(oscuro: Boolean): Color
    /** El color del rótulo del campo (lo que dice qué va ahí). */
    fun rotuloCampo(oscuro: Boolean): Color
    /** El color del marco y del rótulo cuando el valor no sirve. */
    fun errorCampo(oscuro: Boolean): Color

    /**
     * El marco de un campo de texto, del tamaño del lienzo (de un renglón o de varios, [EstadoMarco.multilinea]): la tablilla
     * de cera, el recuadro de formulario con su raya, los corchetes de la consola, la cartela… Con [EstadoMarco.enfocado]
     * se nota que se está escribiendo ahí; con [EstadoMarco.error], que el valor no sirve; sin [EstadoMarco.habilitado], apagado.
     */
    fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean)

    // ---------------------------------------------------------------- separadores y botones de texto

    /** Alto del lienzo del separador (la raya va centrada en él). */
    val altoSeparador: Dp

    /**
     * La raya que separa dos cosas, a todo el ancho del lienzo (de 120 a 400 dp): la greca, el filete con su rombo, la línea
     * de guiones de la consola, el cordón… Discreta, porque se repite entre las filas de una lista. Con [EstadoSeparador.tenue]
     * va más suave.
     */
    fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean)

    /** El color del texto de los botones de texto. */
    fun tintaBotonTexto(oscuro: Boolean): Color

    /**
     * El adorno de un botón de texto, del tamaño del botón (de 48×48 a 300×48 dp), detrás de su texto: el subrayado con
     * floritura, los corchetes de la consola, la cinta… Discreto: el texto manda. Con [EstadoBotonTexto.presionado] se nota
     * el toque; sin [EstadoBotonTexto.habilitado], apagado.
     */
    fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean)

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** Margen a los lados del texto de una fila de menú. */
    val margenFila: Dp
    /** Lo que va detrás de una fila de menú (de Ajustes, de «Más»), del tamaño de la fila; [EstadoFila.presionado] al tocarla. */
    fun DrawScope.fila(e: EstadoFila, oscuro: Boolean)
    /** La flecha (mano, cursor…) al final de una fila de menú, en un lienzo de 20×20 dp. */
    fun DrawScope.flechaFila(oscuro: Boolean)
    /** La marca para fijar una fila de «Más» arriba (estrella, alfiler, lacre…), en 24×24 dp: llena si [fija]. */
    fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean)

    /** Lado del dibujo de lo vacío, arriba del texto. */
    val tamDibujoVacio: Dp
    /** El color del texto de un vacío. */
    fun tintaVacio(oscuro: Boolean): Color
    /** El recuadro de «todavía no hay…», del tamaño del lienzo. */
    fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean)
    /** Una ilustración pequeña de algo vacío del mundo del tema (el ánfora, la lata, el cofre abierto…), en [tamDibujoVacio]. */
    fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean)

    /** Margen izquierdo del texto de un aviso (ahí va su marca). */
    val margenAviso: Dp
    /** El color del texto de un aviso. */
    fun tintaAviso(oscuro: Boolean): Color
    /** Lo que va detrás de un aviso, del tamaño del lienzo, con su marca a la izquierda dentro de [margenAviso]. */
    fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean)

    /** Margen entre el borde de una cifra y su texto. */
    val margenCifra: Dp
    /** El color del número de una cifra. */
    fun tintaCifra(oscuro: Boolean): Color
    /** El color del rótulo de una cifra. */
    fun rotuloCifra(oscuro: Boolean): Color
    /** Lo que va detrás de una cifra (la tesela, la placa, el contador…), del tamaño del lienzo. */
    fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean)

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    /** Margen entre el borde de una tarjeta y su contenido. */
    val margenTarjeta: Dp
    /** El color del texto principal de una tarjeta de ese tipo. */
    fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color
    /** El color de los rótulos y el texto secundario de una tarjeta de ese tipo. */
    fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color
    /** El color de acento de una tarjeta de ese tipo (el título, la barra del día, lo que se destaca). */
    fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color

    /**
     * Lo que va detrás de una tarjeta, del tamaño del lienzo: la pieza del tema según [EstadoTarjeta.tipo] (una cifra del
     * tablero de Hoy, lo que te falta hoy, la noche de Día, una tarjeta de Vida). [EstadoTarjeta.destacado]: la cifra que se
     * aparta de tu normal, la noche ya sellada. [EstadoTarjeta.presionado] al tocarla.
     */
    fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean)

    // ---------------------------------------------------------------- Gastos

    /** Alto de la balanza de Gastos (a todo el ancho). */
    val altoBalanza: Dp

    /**
     * La elección entre gasto e ingreso, como un solo objeto del tema a todo el ancho (la balanza de dos platillos, la
     * moneda de dos caras, las dos pestañas del libro de cuentas…): la mitad izquierda es el gasto y la derecha el ingreso
     * (ahí se toca), cada una con su texto dibujado con [textoEn] y su color ([EstadoBalanza.colorGasto],
     * [EstadoBalanza.colorIngreso]); la elegida ([EstadoBalanza.ingreso]) se nota.
     */
    fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean)

    /** Alto del encabezado del mes en Gastos (a todo el ancho). */
    val altoMes: Dp

    /**
     * El encabezado del mes del libro de cuentas: el nombre del mes ([EstadoMes.titulo]) al centro, dibujado con [textoEn], y
     * las flechas para ir al mes anterior y al siguiente en los extremos (48 dp cada una: ahí se toca).
     */
    fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean)

    /** Lo que va detrás de cada movimiento de la lista (un gasto o un ingreso), del tamaño de la fila; discreto. */
    fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean)

    /** Alto del rótulo de un día en la lista de movimientos. */
    val altoRotuloDia: Dp

    /** El rótulo de un día en la lista de movimientos («LUN 3 NOV»), a todo el ancho, con [textoEn]. */
    fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean)

    // ---------------------------------------------------------------- adornos de la hoja rápida, las relaciones y la cinta

    /**
     * El adorno de la hoja rápida (la que se abre desde el aviso para anotar al momento), dibujado ENCIMA de su contenido,
     * del tamaño de la hoja (≈ 360 × 300–700 dp): solo en el borde de arriba (bajo su perfil), las esquinas y los cantos,
     * nunca sobre el centro donde van los botones y el texto.
     */
    fun DrawScope.adornoQuick(e: EstadoAdorno, oscuro: Boolean)

    /**
     * El detalle de un recuadro de variable en Relaciones (≈ 100–160 × 56–90 dp), DETRÁS de su nombre y su grupo:
     * molduras, corchetes, grecas, remaches, sellos, gemas… tenue en el centro.
     */
    fun DrawScope.adornoRelacion(e: EstadoAdorno, oscuro: Boolean)

    /**
     * El marco de un icono de pista de la cinta (≈ 14–28 dp cuadrado), en [EstadoAdorno.primario]: el clípeo, los corchetes
     * de la consola, el frontón, el sello… El glifo de la pista va dentro, con un margen de 22 % por lado: deja ese hueco libre.
     */
    fun DrawScope.marcoPista(e: EstadoAdorno, oscuro: Boolean)
}

/** Los colores de un adorno: el primario (ya con su transparencia), el secundario y el fondo sobre el que va. */
class EstadoAdorno(val primario: Color, val secundario: Color, val fondo: Color)

/** La balanza: si está elegido el ingreso, los textos de cada lado y sus colores. */
class EstadoBalanza(val ingreso: Boolean, val textoGasto: String, val textoIngreso: String, val colorGasto: Color, val colorIngreso: Color)

/** El encabezado del mes: su nombre. */
class EstadoMes(val titulo: String)

/** Un movimiento de la lista: si es un ingreso y una semilla. */
class EstadoAsiento(val ingreso: Boolean, val semilla: Int)

/** Qué tarjeta es: una cifra del tablero de Hoy, «Te falta hoy», la noche de Día o una tarjeta de Vida. */
enum class TipoTarjeta { METRICA, PENDIENTE, NOCHE, VIDA }

/** Una tarjeta: su tipo, si se destaca, si se está tocando y una semilla. */
class EstadoTarjeta(val tipo: TipoTarjeta, val destacado: Boolean, val presionado: Boolean, val semilla: Int)

/** Una fila de menú: si se está tocando, si lleva detalle y una semilla. */
class EstadoFila(val presionado: Boolean, val conDetalle: Boolean, val semilla: Int)

/** Un vacío: una semilla (para variar el dibujo). */
class EstadoVacio(val semilla: Int)

/** Un aviso: una semilla. */
class EstadoAviso(val semilla: Int)

/** Una cifra: una semilla. */
class EstadoCifra(val semilla: Int)

/** Un separador: si va tenue (la pantalla le pidió un color propio) y una semilla. */
class EstadoSeparador(val tenue: Boolean, val semilla: Int)

/** Un botón de texto: si se está presionando, si está habilitado y una semilla. */
class EstadoBotonTexto(val presionado: Boolean, val habilitado: Boolean, val semilla: Int)

/** Cómo está un campo: con el foco, con un error, habilitado, de varios renglones, vacío, y una semilla. */
class EstadoMarco(val enfocado: Boolean, val error: Boolean, val habilitado: Boolean, val multilinea: Boolean, val vacio: Boolean, val semilla: Int)

/** Las piezas comunes de cada tema: cada uno con su objeto. */
fun disenoComun(t: TemaId): DisenoComun = when (t) {
    TemaId.ROMANO -> ComunRomano
    TemaId.URBE -> ComunUrbe
    TemaId.CIBER -> ComunCiber
    TemaId.NEON -> ComunNeon
    TemaId.HELENO -> ComunHeleno
    TemaId.EGIPTO -> ComunEgipto
    TemaId.MEDIEVAL -> ComunMedieval
    TemaId.TEMPLARIO -> ComunTemplario
    TemaId.DISTOPIA -> ComunDistopia
    TemaId.REFUGIO -> ComunRefugio
    TemaId.ORDEN -> ComunOrden
    TemaId.VAPOR -> ComunVapor
    TemaId.LABORATORIO -> ComunLaboratorio
    TemaId.COLONOS -> ComunColonos
    TemaId.NAPOLEON -> ComunNapoleon
    TemaId.DECO -> ComunDeco
    TemaId.BOTANICO -> ComunBotanico
    TemaId.RUINAS -> ComunRuinas
    TemaId.NAVIDAD -> ComunNavidad
    TemaId.SEDA -> ComunSeda
    TemaId.EDO -> ComunEdo
    TemaId.GOTICO -> ComunGotico
    TemaId.ARCANO -> ComunArcano
    TemaId.ESPACIO -> ComunEspacio
    TemaId.ABISMO -> ComunAbismo
    TemaId.ANDALUS -> ComunAndalus
    TemaId.MESO -> ComunMeso
    TemaId.MONGOL -> ComunMongol
    TemaId.RENACIMIENTO -> ComunRenacimiento
    TemaId.PIRATA -> ComunPirata
    TemaId.URSS -> ComunUrss
    TemaId.REVOLUCION -> ComunRevolucion
    TemaId.ANTIGUAS -> ComunAntiguas
    TemaId.CUEVA -> ComunCueva
}
