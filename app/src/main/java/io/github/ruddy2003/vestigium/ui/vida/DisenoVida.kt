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
 * - El mismo nivel de belleza y detalle que los muebles que ya existen (el armario del Ideario, los estantes de las
 *   bibliotecas, los salones del Hall): materiales reales y opacos (veta de la madera, vetas del mármol, metal con brillo),
 *   molduras, cornisas, bases, herrajes, luz y sombra; limpio y nítido.
 * - El contenido va en su nicho ([area]); nada se dibuja encima de él.
 * - Que se lea en claro y en oscuro, sin texto propio (los títulos y cifras los pone la pantalla) y sin animación.
 */
interface DisenoVida {
    /**
     * El nicho de cada mueble: la parte del lienzo (en fracciones de 0 a 1 del ancho y del alto) donde la pantalla pone el
     * contenido (el grafo de tu gente, el mapa para rascar, la fila de medallas, la cuadrícula de notas). El mueble dibuja
     * ese nicho con un fondo **sólido** de su material (el interior del armario, el tablero de la mesa, el corcho), de un
     * tono cercano a [EstadoMueble.fondo] en luminancia (claro en claro, oscuro en oscuro) para que el contenido y su texto
     * se lean; todo lo demás (cornisa, columnas, base, puertas, ornamentos, objetos) va alrededor, opaco y con detalle.
     */
    fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.10f, 0.14f, 0.90f, 0.84f)
        MuebleVida.MAPAS -> Rect(0.07f, 0.12f, 0.93f, 0.86f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.06f, 0.16f, 0.94f, 0.82f)
        MuebleVida.NOTAS -> Rect(0.06f, 0.10f, 0.94f, 0.90f)
    }

    /**
     * Social: el lugar donde se reúne tu gente, a todo el lienzo (≈ 360 × 230 dp): el triclinio, la plaza, el salón del
     * vagón, el comedor del refugio, la sala del capítulo… El grafo de personas va en su nicho ([area] SOCIAL): un fondo
     * sólido (el mosaico del piso, el mantel, el tablero de la mesa vista desde arriba, la pared) alrededor del cual está
     * el mueble entero y detallado.
     */
    fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean)

    /**
     * Mapas: el mueble de los mapas, a todo el lienzo (≈ 360 × 220 dp): la mesa del agrimensor, el escritorio del
     * cartógrafo, el arcón de cartas náuticas, la consola de navegación… El mapa para rascar va en su nicho ([area] MAPAS),
     * sobre el tablero o el pergamino; alrededor, el mueble con sus patas, bordes, herrajes e instrumentos.
     */
    fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean)

    /**
     * Otras vidas: la vitrina de las medallas, a todo el lienzo (≈ 360 × 190 dp): las hornacinas, el aparador, el gabinete
     * de curiosidades, el expositor… Hasta 5 medallas (cada una de ≈ 46 dp, con su año debajo) van en fila en su nicho
     * ([area] OTRAS_VIDAS), repartidas a lo ancho con el mismo espacio entre ellas: el nicho puede dividirse en cinco
     * compartimentos iguales.
     */
    fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean)

    /**
     * El ícono de cada mapa en «Tus mapas», en un lienzo de ≈ 160 × 70 dp, centrado y de ≈ 60 dp de alto, con el detalle de
     * una miniatura bien dibujada:
     * - `"unesco"`: el Patrimonio Mundial con un monumento del tema (el templo, la pirámide, la catedral, la pagoda, la
     *   torre de radio, la estación orbital…);
     * - `"rascar"`: el mapa para rascar a la manera del tema (la moneda que raspa, el pergamino con una esquina rascada,
     *   la placa de cobre, la pantalla con la capa que se borra…);
     * - `"lugares"`: tus lugares con los marcadores del tema (las teselas, los sellos, las chinchetas, las balizas…).
     * Con [EstadoIcono.c1] (el acento) y [EstadoIcono.c2] (el secundario) sobre [EstadoIcono.fondo]; silueta clara.
     */
    fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean)

    /**
     * Notas: el mueble de las notas en la pestaña Diario, a todo el lienzo (≈ 360 × 280 dp): el tablón de corcho con su
     * marco, la tablilla de cera, el panel de avisos, la puerta con papeles clavados… Las notas van en su nicho ([area]
     * NOTAS), en una cuadrícula de 3 × 2.
     */
    fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean)

    /**
     * Una nota sobre el tablero, en [EstadoNota.rect] (≈ 100 × 100 dp): la tablilla, el pergamino clavado, la ficha con
     * chincheta, el papel con cinta… Según [EstadoNota.tipo] («idea», «pensamiento», «proposito», «recordatorio») cambia un
     * detalle (el color de la chincheta, la esquina doblada, el sello); [EstadoNota.fijada] la destaca. La pantalla escribe
     * el texto encima, dentro del rectángulo con ≈ 12 % de margen a los lados y ≈ 16 % arriba y abajo, en el color
     * [tintaNota]: el papel debe dejarlo legible.
     */
    fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean)

    /** El color del texto escrito sobre una nota del tablero. */
    fun tintaNota(oscuro: Boolean): Color
}

/** Los muebles que tienen nicho para un contenido. */
enum class MuebleVida { SOCIAL, MAPAS, OTRAS_VIDAS, NOTAS }

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
    TemaId.ROMANO -> VidaRomano
    TemaId.URBE -> VidaUrbe
    TemaId.CIBER -> VidaCiber
    TemaId.NEON -> VidaNeon
    TemaId.HELENO -> VidaHeleno
    TemaId.EGIPTO -> VidaEgipto
    TemaId.MEDIEVAL -> VidaMedieval
    TemaId.TEMPLARIO -> VidaTemplario
    TemaId.DISTOPIA -> VidaDistopia
    TemaId.REFUGIO -> VidaRefugio
    TemaId.ORDEN -> VidaOrden
    TemaId.VAPOR -> VidaVapor
    TemaId.LABORATORIO -> VidaLaboratorio
    TemaId.COLONOS -> VidaColonos
    TemaId.NAPOLEON -> VidaNapoleon
    TemaId.DECO -> VidaDeco
    TemaId.BOTANICO -> VidaBotanico
    TemaId.RUINAS -> VidaRuinas
    TemaId.NAVIDAD -> VidaNavidad
    TemaId.SEDA -> VidaSeda
    TemaId.EDO -> VidaEdo
    TemaId.URSS -> VidaUrss
    TemaId.GOTICO -> VidaGotico
    TemaId.ARCANO -> VidaArcano
    TemaId.ESPACIO -> VidaEspacio
    TemaId.ABISMO -> VidaAbismo
    TemaId.ANDALUS -> VidaAndalus
    TemaId.MESO -> VidaMeso
    TemaId.MONGOL -> VidaPlana
    TemaId.RENACIMIENTO -> VidaPlana
    TemaId.PIRATA -> VidaPlana
    TemaId.REVOLUCION -> VidaPlana
    TemaId.ANTIGUAS -> VidaPlana
    TemaId.CUEVA -> VidaPlana
    TemaId.BASICO -> VidaPlana
}
