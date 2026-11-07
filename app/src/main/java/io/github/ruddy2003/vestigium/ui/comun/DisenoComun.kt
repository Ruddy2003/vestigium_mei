package io.github.ruddy2003.vestigium.ui.comun

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
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
}

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
