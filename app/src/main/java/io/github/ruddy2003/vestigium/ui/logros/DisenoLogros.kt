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

    // ---------------------------------------------------------------- el salón del hall

    /**
     * El salón del hall detrás de los trofeos, a todo el lienzo (≈ 360 × 600–1400 dp en la app; 1080 × 1350 px en la
     * imagen para compartir): el atrio con su impluvio, la nave con su rosetón, el caravasar, la estación de metro… Su
     * arquitectura (columnas, arcos, hornacinas) enmarca cada una de las [EstadoSalon.columnas] columnas de trofeos y nunca
     * pasa por detrás de una. Todo va tenue sobre [EstadoSalon.fondo] para que los trofeos y sus placas se lean encima.
     */
    fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean)

    // ---------------------------------------------------------------- las campañas

    /**
     * El marcador de la campaña, de pie sobre la parada a la que llegaste ([EstadoMarcador.punto], en píxeles del mapa;
     * el mapa mide todo el ancho × 1/2.4 del ancho, ≈ 360 × 150 dp): una figura entera del tema de ≈ 18–28 dp de alto (el
     * legionario con su estandarte, la barca del Nilo, la vela del scriptorium, la locomotora, el banderín del punto de
     * control, la nave…), con su sombra en el suelo. [EstadoMarcador.salto] (0 a 1) lo hace brincar mientras hoy falta
     * cumplir. [EstadoMarcador.acento] es el color de acento del esquema.
     */
    fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean)

    /**
     * La escena en la meta ([EstadoVictoria.meta], en píxeles del mapa) cuando se gana la campaña: la corona triunfal, los
     * fuegos, el estandarte plantado, el cofre abierto… de ≈ 30–50 dp, arriba de la meta, sin tapar el nombre de la parada
     * más de lo necesario.
     */
    fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean)

    // ---------------------------------------------------------------- objetos de Tu año

    /**
     * Uno de los objetos de Tu año del tema, centrado en [EstadoObjeto.centro] con radio [EstadoObjeto.radio] (de ≈ 4 a 20 dp:
     * es un icono, dentro de la ficha del tema), en el color [EstadoObjeto.color] sobre [EstadoObjeto.fondo]:
     * - `"luna"`: los días enteros dormido (la luna del cielo del tema: la de Khonsu, la de la cetrería, la luna llena tras
     *   las nubes, la luna de neón, la luna vista desde la nave…);
     * - `"huella"`: los kilómetros caminados (la huella del tema: la sandalia de clavos, la bota del peregrino, el zueco, la
     *   rodada de bicicleta, la bota del astronauta, la pata de bisonte…);
     * - `"pesa"`: los entrenamientos (la piedra de levantar, la pesa rusa, el yunque, la bala de cañón, el halterio del gimnasio…).
     * Que se reconozca a ese tamaño: silueta clara, pocos detalles.
     */
    fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean)
}

/** Un objeto de Tu año: su forma (luna, huella o pesa), dónde va, su radio y sus colores. */
class EstadoObjeto(val forma: String, val centro: androidx.compose.ui.geometry.Offset, val radio: Float, val color: Color, val fondo: Color)

/** El marcador de una campaña: dónde está, cuánto brinca (0 a 1) y el color de acento del esquema. */
class EstadoMarcador(val punto: androidx.compose.ui.geometry.Offset, val salto: Float, val acento: Color)

/** La victoria de una campaña: el punto de la meta y el color de acento del esquema. */
class EstadoVictoria(val meta: androidx.compose.ui.geometry.Offset, val acento: Color)

/** El salón: cuántas columnas de trofeos hay, el color de fondo de la pantalla y una semilla. */
class EstadoSalon(val columnas: Int, val fondo: Color, val semilla: Int)

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
