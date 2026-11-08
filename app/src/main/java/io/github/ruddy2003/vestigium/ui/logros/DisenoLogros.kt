package io.github.ruddy2003.vestigium.ui.logros

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import io.github.ruddy2003.vestigium.ui.TemaId

// =====================================================================
//  Los logros y el viaje de cada tema: pedestales, placas, salones, marcadores de campaña y objetos de Tu año
// =====================================================================

/**
 * Lo que los logros y el viaje dibujan para un tema ([disenoLogros]): un objeto por tema, como el Ideario, las bibliotecas
 * y las piezas comunes. Cada lote de la fase C le agrega sus piezas. Las figuras de los trofeos siguen en
 * `TrofeosTema` / `TrofeosMas` (ya son propias de cada tema).
 */
interface DisenoLogros {
    // ---------------------------------------------------------------- pedestales y placa

    /**
     * El pedestal de un trofeo, en la franja de abajo del lienzo del trofeo (de ≈ 76 % a 97 % del alto; el lienzo mide de
     * 40 a 220 dp de alto y 0.82 de ancho por alto): la basa de travertino, el atril, el plinto con colgadura, la peana de
     * hierro… Con [EstadoPedestal.hecho] en sus materiales y con el metal del nivel en sus detalles; sin él, una silueta
     * tenue (el trofeo aún no se gana). Todo escala con el lienzo.
     */
    fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean)

    /**
     * El pedestal del hall, debajo de cada trofeo expuesto (un lienzo de ≈ 70–90 × 22 dp): la columna, el plinto, la mesa de
     * ofrendas, el sillar… con el metal del nivel ([EstadoPedestal.base]) en un detalle.
     */
    fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean)

    /** Margen de la placa del hall alrededor de su texto. */
    val margenPlaca: Dp
    /** El color del nombre en la placa del hall. */
    fun tintaPlaca(oscuro: Boolean): Color
    /** El color de la fecha en la placa del hall. */
    fun rotuloPlaca(oscuro: Boolean): Color

    /**
     * La placa del hall bajo cada pedestal, del tamaño del lienzo (≈ 90–120 × 40–56 dp): la tabula ansata, la cartela, la
     * etiqueta de museo, la chapa grabada… con el metal del nivel en su borde. El nombre y la fecha los pone la pantalla encima.
     */
    fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean)
}

/**
 * Un pedestal o una placa: si el logro ya se ganó, su nivel (1 a 4: bronce, plata, oro, legendario), los tonos del metal de
 * ese nivel (oscuro, base y brillo) y una semilla.
 */
class EstadoPedestal(val hecho: Boolean, val nivel: Int, val metalOscuro: Color, val base: Color, val brillo: Color, val semilla: Int)

/** Los logros y el viaje de cada tema: cada uno con su objeto. */
fun disenoLogros(t: TemaId): DisenoLogros = when (t) {
    TemaId.ROMANO -> LogrosRomano
    TemaId.URBE -> LogrosUrbe
    TemaId.CIBER -> LogrosCiber
    TemaId.NEON -> LogrosNeon
    TemaId.HELENO -> LogrosHeleno
    TemaId.EGIPTO -> LogrosEgipto
    TemaId.MEDIEVAL -> LogrosMedieval
    TemaId.TEMPLARIO -> LogrosTemplario
    TemaId.DISTOPIA -> LogrosDistopia
    TemaId.REFUGIO -> LogrosRefugio
    TemaId.ORDEN -> LogrosOrden
    TemaId.VAPOR -> LogrosVapor
    TemaId.LABORATORIO -> LogrosLaboratorio
    TemaId.COLONOS -> LogrosColonos
    TemaId.NAPOLEON -> LogrosNapoleon
    TemaId.DECO -> LogrosDeco
    TemaId.BOTANICO -> LogrosBotanico
    TemaId.RUINAS -> LogrosRuinas
    TemaId.NAVIDAD -> LogrosNavidad
    TemaId.SEDA -> LogrosSeda
    TemaId.EDO -> LogrosEdo
    TemaId.GOTICO -> LogrosGotico
    TemaId.ARCANO -> LogrosArcano
    TemaId.ESPACIO -> LogrosEspacio
    TemaId.ABISMO -> LogrosAbismo
    TemaId.ANDALUS -> LogrosAndalus
    TemaId.MESO -> LogrosMeso
    TemaId.MONGOL -> LogrosMongol
    TemaId.RENACIMIENTO -> LogrosRenacimiento
    TemaId.PIRATA -> LogrosPirata
    TemaId.URSS -> LogrosUrss
    TemaId.REVOLUCION -> LogrosRevolucion
    TemaId.ANTIGUAS -> LogrosAntiguas
    TemaId.CUEVA -> LogrosCueva
}
