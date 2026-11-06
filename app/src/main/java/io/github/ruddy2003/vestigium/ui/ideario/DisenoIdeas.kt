package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.TemaId

// =====================================================================
//  El Ideario de cada tema: el mueble, sus compartimentos, su interior y sus fichas
// =====================================================================

/**
 * Lo que se sabe de un compartimento al dibujarlo: cuántas fichas guarda ([n]), si es el de las ideas sin tema, cuánto está
 * abierto (de 0 a 1, para la animación al tocarlo) y una [semilla] para que cada uno tenga sus irregularidades, siempre las
 * mismas. [vacio] es un hueco del mueble que no tiene tema: se dibuja cerrado y sin rótulo.
 */
class EstadoCajon(val n: Int, val sinTema: Boolean, val abierto: Float, val semilla: Int, val vacio: Boolean = false)

/**
 * Dónde quedó cada parte del mueble, en píxeles del lienzo entero: la corona, cada fila, cada hueco y la base. [mas] es
 * cuántos compartimentos no se ven (la vista corta de la pestaña Diario): si es más de 0, la base lleva su placa.
 */
class Disposicion(val corona: Rect, val filas: List<Rect>, val huecos: List<Rect>, val base: Rect, val separacion: Float, val mas: Int)

/** Los márgenes del texto dentro de una ficha, chica y grande. */
class MargenesFicha(val arriba: Dp, val lado: Dp, val abajo: Dp, val arribaGrande: Dp, val ladoGrande: Dp, val abajoGrande: Dp)

/**
 * Todo lo que el Ideario dibuja para un tema. Cada tema tiene el suyo ([disenoIdeas]): el mueble entero (el tansu con sus
 * cajones, la pared de la cueva con sus nichos…), cada compartimento con su rótulo, el interior cuando se abre y la forma y
 * el adorno de sus fichas. Las medidas del mueble van en fracciones de su ancho, para que se vea igual en cualquier teléfono.
 */
interface DisenoIdeas {
    // ---------------------------------------------------------------- el mueble

    /** Cuántos compartimentos por fila. */
    val cols: Int
    /** Ancho entre alto de cada compartimento. */
    val proporcion: Float
    /** Alto de la corona (el remate de arriba) y de la base, en fracciones del ancho del mueble. */
    val corona: Float
    val base: Float
    /** El margen a los lados de la rejilla y la separación entre compartimentos, en fracciones del ancho. */
    val lado: Float
    val hueco: Float

    /** El mueble sin sus compartimentos: el cuerpo, la corona, los travesaños entre filas y la base. */
    fun DrawScope.mueble(d: Disposicion, oscuro: Boolean)

    /** Un compartimento, en un lienzo del tamaño de su hueco. Puede salirse un poco (el cajón que sale al abrirse). */
    fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean)

    /** Dónde va el rótulo dentro del compartimento, en fracciones de su ancho y su alto. */
    val rotulo: Rect
    fun tintaRotulo(oscuro: Boolean): Color
    /** La letra del rótulo; null, la de la app. */
    val letraRotulo: FontFamily?
    /** Cuánto baja el rótulo (con el frente del cajón) cuando el compartimento se abre del todo, en fracciones de su alto. */
    val salida: Float

    /** Dónde va la placa de «y N más» dentro de la base (fracciones de la base); null, debajo del mueble. */
    val placa: Rect?
    fun tintaPlaca(oscuro: Boolean): Color

    // ---------------------------------------------------------------- el interior

    /** El interior del compartimento abierto, a pantalla completa: el piso y las paredes. */
    fun DrawScope.interior(oscuro: Boolean)
    /** El color del texto sobre el interior. */
    fun tintaInterior(oscuro: Boolean): Color
    /** Cuánto ocupan las paredes del interior a cada lado (el contenido va adentro). */
    val pared: Dp
    /** Cuántas fichas por fila en el interior, y cuántos renglones caben en cada una. */
    val columnas: Int
    val lineas: Int

    // ---------------------------------------------------------------- las fichas

    /** El contorno de una ficha. [semilla] cambia sus irregularidades. */
    fun forma(semilla: Int): Shape
    /** Lo que la ficha lleva encima del papel (el cordón del tanzaku, las vetas de la laja…). */
    fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int)
    val margenes: MargenesFicha
    /** Ancho entre alto de la ficha chica. */
    val proporcionFicha: Float
    /** La ficha abierta: qué parte del ancho ocupa y su alto mínimo (ancho entre alto). */
    val anchoGrande: Float
    val proporcionGrande: Float
    /** Si el encabezado (tema y fecha) va en una barra de título, como el nombre de un archivo en una ventana. */
    val barra: Boolean
    fun altoBarra(grande: Boolean): Dp
    /** Si la fecha va debajo del tema (en las fichas angostas no caben en una línea). */
    val apilado: Boolean
    /** Cómo la tienes («convence», «piensa», «duda», «desacuerdo»), dibujado en un lienzo cuadrado chico. */
    fun DrawScope.marcaPostura(id: String, p: Pagina)
    /** La marca de una idea fijada, en un lienzo cuadrado chico. */
    fun DrawScope.marcaFijada(p: Pagina)
    /** El giro máximo, en grados, de las fichas en el interior (0 en las rígidas). */
    val desorden: Float
}

/** El diseño del Ideario de cada tema. */
fun disenoIdeas(t: TemaId): DisenoIdeas = when (t) {
    TemaId.ROMANO -> DisenoAnterior(TemaId.ROMANO)
    TemaId.URBE -> DisenoAnterior(TemaId.URBE)
    TemaId.CIBER -> DisenoAnterior(TemaId.CIBER)
    TemaId.NEON -> DisenoAnterior(TemaId.NEON)
    TemaId.HELENO -> DisenoAnterior(TemaId.HELENO)
    TemaId.EGIPTO -> DisenoAnterior(TemaId.EGIPTO)
    TemaId.MEDIEVAL -> DisenoAnterior(TemaId.MEDIEVAL)
    TemaId.TEMPLARIO -> DisenoAnterior(TemaId.TEMPLARIO)
    TemaId.DISTOPIA -> DisenoAnterior(TemaId.DISTOPIA)
    TemaId.REFUGIO -> DisenoAnterior(TemaId.REFUGIO)
    TemaId.ORDEN -> DisenoAnterior(TemaId.ORDEN)
    TemaId.VAPOR -> DisenoAnterior(TemaId.VAPOR)
    TemaId.LABORATORIO -> DisenoAnterior(TemaId.LABORATORIO)
    TemaId.COLONOS -> DisenoAnterior(TemaId.COLONOS)
    TemaId.NAPOLEON -> DisenoAnterior(TemaId.NAPOLEON)
    TemaId.DECO -> DisenoAnterior(TemaId.DECO)
    TemaId.BOTANICO -> DisenoAnterior(TemaId.BOTANICO)
    TemaId.RUINAS -> DisenoAnterior(TemaId.RUINAS)
    TemaId.NAVIDAD -> DisenoAnterior(TemaId.NAVIDAD)
    TemaId.SEDA -> DisenoAnterior(TemaId.SEDA)
    TemaId.EDO -> IdeasEdo
    TemaId.GOTICO -> DisenoAnterior(TemaId.GOTICO)
    TemaId.ARCANO -> DisenoAnterior(TemaId.ARCANO)
    TemaId.ESPACIO -> DisenoAnterior(TemaId.ESPACIO)
    TemaId.ABISMO -> DisenoAnterior(TemaId.ABISMO)
    TemaId.ANDALUS -> DisenoAnterior(TemaId.ANDALUS)
    TemaId.MESO -> DisenoAnterior(TemaId.MESO)
    TemaId.MONGOL -> DisenoAnterior(TemaId.MONGOL)
    TemaId.RENACIMIENTO -> DisenoAnterior(TemaId.RENACIMIENTO)
    TemaId.PIRATA -> DisenoAnterior(TemaId.PIRATA)
    TemaId.URSS -> DisenoAnterior(TemaId.URSS)
    TemaId.REVOLUCION -> DisenoAnterior(TemaId.REVOLUCION)
    TemaId.ANTIGUAS -> DisenoAnterior(TemaId.ANTIGUAS)
    TemaId.CUEVA -> IdeasCueva
}

// =====================================================================
//  Ayudantes de dibujo para todos los temas
// =====================================================================

/** Un número estable de 0 a 1: cada cosa tiene sus irregularidades, siempre las mismas. */
internal fun azar(semilla: Int, k: Int): Float {
    var x = semilla * 374761393 + k * 668265263
    x = (x xor (x ushr 13)) * 1274126177
    x = x xor (x ushr 16)
    return (x and 0x7fffffff) / 2147483647f
}

/** Una sombra suave debajo de algo (varias capas cada vez más transparentes). */
internal fun DrawScope.sombraSuave(r: Rect, radio: Float, alcance: Float, fuerza: Float = 0.22f) {
    for (k in 4 downTo 1) {
        val e = alcance * k / 4f
        drawRoundRect(Color.Black.copy(alpha = fuerza / 4f), Offset(r.left - e * 0.3f, r.top + e * 0.6f), Size(r.width + e * 0.6f, r.height + e * 0.4f),
            androidx.compose.ui.geometry.CornerRadius(radio + e))
    }
}

/**
 * Vetas de madera en un rectángulo: líneas onduladas a lo largo, más o menos marcadas, con algún nudo. [vertical] si la
 * tabla corre de arriba abajo.
 */
internal fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, densidad: Float = 1f, vertical: Boolean = false, grosor: Float = 1f) {
    val d = density
    val largo = if (vertical) r.height else r.width
    val ancho = if (vertical) r.width else r.height
    val n = (ancho / (3.2f * d) * densidad).toInt().coerceIn(3, 80)
    for (k in 0 until n) {
        val base = ancho * (k + azar(semilla, k)) / n
        val amp = (0.6f + 1.8f * azar(semilla, k + 100)) * d
        val fase = azar(semilla, k + 200) * 6.28f
        val ondas = 1.5f + 2.5f * azar(semilla, k + 300)
        val alfa = 0.25f + 0.75f * azar(semilla, k + 400)
        val p = Path()
        val pasos = 18
        for (s in 0..pasos) {
            val u = largo * s / pasos
            val v = base + amp * kotlin.math.sin(fase + ondas * 6.28f * s / pasos)
            val x = if (vertical) r.left + v else r.left + u
            val y = if (vertical) r.top + u else r.top + v
            if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
        }
        drawPath(p, color.copy(alpha = color.alpha * alfa), style = androidx.compose.ui.graphics.drawscope.Stroke(grosor * d * (0.5f + azar(semilla, k + 500))))
    }
}

/** Un bisel: luz arriba e izquierda, sombra abajo y derecha, para que una tabla se vea con grosor. */
internal fun DrawScope.bisel(r: Rect, luz: Color, sombra: Color, ancho: Float) {
    drawRect(luz, r.topLeft, Size(r.width, ancho))
    drawRect(luz.copy(alpha = luz.alpha * 0.7f), r.topLeft, Size(ancho, r.height))
    drawRect(sombra, Offset(r.left, r.bottom - ancho), Size(r.width, ancho))
    drawRect(sombra.copy(alpha = sombra.alpha * 0.8f), Offset(r.right - ancho, r.top), Size(ancho, r.height))
}

/** Una mancha radial suave (luz de fuego, humedad, pigmento). */
internal fun DrawScope.mancha(c: Offset, r: Float, color: Color) =
    drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

/** Un color entre dos, también para usar fuera de Compose. */
internal fun mezcla(a: Color, b: Color, f: Float) = lerp(a, b, f.coerceIn(0f, 1f))

/** El margen chico estándar: el de las fichas sin adornos arriba. */
internal val MARGEN_SIMPLE = MargenesFicha(6.dp, 9.dp, 6.dp, 12.dp, 22.dp, 22.dp)
