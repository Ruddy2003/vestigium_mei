package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.ruddy2003.vestigium.ui.TemaId

// =====================================================================
//  Los muebles de Vida y de Diario de cada tema (lote E6, 9 nov 2026)
// =====================================================================

/**
 * Lo que dibujan las secciones especiales de Vida y el mueble de Notas para un tema ([disenoVida]): un objeto por tema,
 * como las bibliotecas, el Ideario y los logros. Vida es una habitación más de la casa del tema: cada sección es un mueble
 * (el Hall de logros en pequeño, la estantería de obras, la mesa de mapas, la vitrina de las otras vidas, el lugar donde
 * se reúne tu gente…). Todo se dibuja con el [EstadoMueble] que da la pantalla, en el lienzo completo y escalado a él.
 *
 * Reglas para todos los dibujos:
 * - Que se lea en claro y en oscuro: [EstadoMueble.fondo] es el color de la superficie de la tarjeta; el mueble va sobre él,
 *   con contraste suficiente pero sin tapar lo que la pantalla pone encima (el grafo, el mapa, las medallas, las notas).
 * - Sin degradados chillones ni texto propio (los títulos y cifras los pone la pantalla).
 * - Barato: nada que dependa del tiempo, sin bucles de miles de trazos (se dibuja en listas que se desplazan).
 */
interface DisenoVida {
    /**
     * Social: el lugar donde se reúne tu gente, a todo el lienzo (≈ 360 × 168 dp): el triclinio con su mesa, la plaza del
     * pueblo, la mesa redonda del salón, el tablero de conexiones, la fogata del campamento… El grafo de personas se dibuja
     * encima, en el centro (≈ 80 % del ancho y del alto): el mueble lo **enmarca** por los bordes y abajo, y el centro queda
     * tranquilo (tenue) para que se lean los puntos y los nombres.
     */
    fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean)

    /**
     * Mapas: el mueble de los mapas, a todo el lienzo (≈ 360 × 168 dp): la mesa del cartógrafo, el arcón de cartas
     * náuticas, el atril con el portulano, la consola de navegación… El mapa para rascar va encima, en el centro
     * (≈ 88 % del ancho × 80 % del alto): el mueble asoma alrededor (patas, bordes, instrumentos en las esquinas).
     */
    fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean)

    /**
     * Otras vidas: la vitrina de las medallas de tus otras vidas, a todo el lienzo (≈ 360 × 118 dp): la hornacina, el
     * aparador con puertas de vidrio, el gabinete de curiosidades, el expositor… Hasta 5 medallas van encima, en una fila
     * centrada a media altura (cada una de ≈ 56 dp): la vitrina les da un estante o un nicho a cada una.
     */
    fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean)

    /**
     * El ícono de cada mapa en «Tus mapas», en un lienzo de ≈ 160 × 70 dp, centrado y de ≈ 56 dp de alto:
     * - `"unesco"`: el Patrimonio Mundial con un monumento del tema (el templo, la pirámide, la catedral, la pagoda, la
     *   torre de radio, la estación orbital…);
     * - `"rascar"`: el mapa para rascar a la manera del tema (la moneda que raspa, el pergamino con una esquina rascada,
     *   la placa de cobre, la pantalla con la capa que se borra…);
     * - `"lugares"`: tus lugares con los marcadores del tema (las teselas, los sellos, las chinchetas, los balizas…).
     * Con [EstadoIcono.c1] (el acento) y [EstadoIcono.c2] (el secundario) sobre [EstadoIcono.fondo]; silueta clara.
     */
    fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean)

    /**
     * Notas: el mueble de las notas en la pestaña Diario, a todo el lienzo (≈ 360 × 200 dp): el tablón de corcho, la
     * tablilla de cera, el atril con hojas sueltas, el panel de avisos, el muro con papeles pegados… Las notas van encima
     * en una cuadrícula de 3 × 2 (con ≈ 8 % de margen alrededor): el mueble es el soporte.
     */
    fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean)

    /**
     * Una nota sobre el tablero, en [EstadoNota.rect] (≈ 100 × 70 dp): la tablilla, el pergamino clavado, la ficha con
     * chincheta, el papel con cinta… Según [EstadoNota.tipo] («idea», «pensamiento», «proposito», «recordatorio») cambia un
     * detalle (el color de la chincheta, la esquina doblada, el sello); [EstadoNota.fijada] la destaca. La pantalla escribe
     * el texto encima, dentro del rectángulo con ≈ 12 % de margen, en el color [tintaNota]: el papel debe dejarlo legible.
     */
    fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean)

    /** El color del texto escrito sobre una nota del tablero. */
    fun tintaNota(oscuro: Boolean): Color
}

/** Los colores con que se dibuja un mueble y una semilla para lo que varía (vetas, piedras, estrellas). */
class EstadoMueble(val fondo: Color, val acento: Color, val secundario: Color, val tinta: Color, val semilla: Int = 0)

/** Los colores de un ícono de mapa. */
class EstadoIcono(val c1: Color, val c2: Color, val fondo: Color, val tinta: Color)

/** Una nota en el tablero: dónde va, su tipo, si está fijada, su lugar en el tablero (0 a 5) y los colores del esquema. */
class EstadoNota(val rect: Rect, val tipo: String, val fijada: Boolean, val indice: Int, val acento: Color, val fondo: Color) {
    val centro: Offset get() = rect.center
}

/** Los muebles de Vida y de Diario de cada tema: cada uno con su objeto. */
fun disenoVida(t: TemaId): DisenoVida = when (t) {
    TemaId.ROMANO -> VidaPlana
    TemaId.URBE -> VidaPlana
    TemaId.CIBER -> VidaPlana
    TemaId.NEON -> VidaPlana
    TemaId.HELENO -> VidaPlana
    TemaId.EGIPTO -> VidaPlana
    TemaId.MEDIEVAL -> VidaPlana
    TemaId.TEMPLARIO -> VidaPlana
    TemaId.DISTOPIA -> VidaPlana
    TemaId.REFUGIO -> VidaPlana
    TemaId.ORDEN -> VidaPlana
    TemaId.VAPOR -> VidaPlana
    TemaId.LABORATORIO -> VidaLaboratorio
    TemaId.COLONOS -> VidaColonos
    TemaId.NAPOLEON -> VidaNapoleon
    TemaId.DECO -> VidaDeco
    TemaId.BOTANICO -> VidaPlana
    TemaId.RUINAS -> VidaPlana
    TemaId.NAVIDAD -> VidaPlana
    TemaId.SEDA -> VidaPlana
    TemaId.EDO -> VidaPlana
    TemaId.URSS -> VidaPlana
    TemaId.GOTICO -> VidaPlana
    TemaId.ARCANO -> VidaPlana
    TemaId.ESPACIO -> VidaPlana
    TemaId.ABISMO -> VidaPlana
    TemaId.ANDALUS -> VidaPlana
    TemaId.MESO -> VidaPlana
    TemaId.MONGOL -> VidaPlana
    TemaId.RENACIMIENTO -> VidaPlana
    TemaId.PIRATA -> VidaPlana
    TemaId.REVOLUCION -> VidaPlana
    TemaId.ANTIGUAS -> VidaPlana
    TemaId.CUEVA -> VidaPlana
    TemaId.BASICO -> VidaPlana
}
