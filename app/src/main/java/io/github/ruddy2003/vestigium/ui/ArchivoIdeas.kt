package io.github.ruddy2003.vestigium.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.pantallas.simboloCalificacion

// =====================================================================
//  El archivo de ideas de cada tema: dónde se guardan las fichas y cómo son
// =====================================================================

/** Cómo se dibuja el mueble o el objeto donde se guardan las ideas de un tema. */
enum class FormaArchivo { CAJONERA, ARCON, CAPSA, CAJA, CASILLERO, DIRECTORIO, RIEL, NICHO }

/** El contorno y el adorno de cada ficha. Por dentro todas llevan el papel del tema ([paginaFicha]). */
enum class FormaFicha { TARJETA, TABLILLA, TIRA, TICKET, EXPEDIENTE, VENTANA, LAJA, RECORTE }

/** Lo que distingue el objeto de un tema de otro con la misma forma: herrajes, taracea, la cruz, el tejido, la nieve… */
enum class Adorno { NADA, HERRAJES, REMACHES, TARACEA, CRUZ, TEJIDO, LACA, NIEVE, CINTA, BRAMANTE, MEANDRO, GLIFOS, ORO, HIEDRA, CODIGO, CORREAS }

/**
 * El archivo de ideas de un tema: cómo se llaman el contenedor (un cajón, una capsa, un directorio…) y cada ficha
 * (tablilla, tanzaku, ticket, archivo…), en singular y plural, y cómo se dibujan. [fem] dice si el contenedor es femenino.
 */
class ArchivoIdeas(
    val contenedor: String, val contenedores: String, val ficha: String, val fichas: String, val fem: Boolean,
    val forma: FormaArchivo, val formaFicha: FormaFicha, val adorno: Adorno = Adorno.NADA,
    /** El mueble entero donde están los contenedores («el tansu», «la pared de la cueva»); mientras no tiene uno propio, «el fichero». */
    val mueble: String = "el fichero",
) {
    /** «3 tablillas», «1 tanzaku». */
    fun cuenta(n: Int) = "$n ${if (n == 1) ficha else fichas}"
    /** «Esta capsa está vacía.», «Este cofre está vacío.» */
    val vacio: String get() = if (fem) "Esta $contenedor está vacía." else "Este $contenedor está vacío."
}

fun archivoIdeas(t: TemaId): ArchivoIdeas = when (t) {
    TemaId.ROMANO -> ArchivoIdeas("capsa", "capsas", "rollo", "rollos", true, FormaArchivo.CAPSA, FormaFicha.TABLILLA, Adorno.ORO, "el armarium")
    TemaId.URBE -> ArchivoIdeas("caja de boletos", "cajas de boletos", "boleto", "boletos", true, FormaArchivo.CAJA, FormaFicha.TICKET, Adorno.CINTA, "la edícola")
    TemaId.HELENO -> ArchivoIdeas("ánfora", "ánforas", "óstrakon", "óstraka", true, FormaArchivo.CAPSA, FormaFicha.LAJA, Adorno.MEANDRO, "la stoa")
    TemaId.EGIPTO -> ArchivoIdeas("cofre de cedro", "cofres de cedro", "tablilla de escriba", "tablillas de escriba", false, FormaArchivo.ARCON, FormaFicha.TABLILLA, Adorno.GLIFOS, "la Casa de la Vida")
    TemaId.SEDA -> ArchivoIdeas("caja lacada", "cajas lacadas", "tira de bambú", "tiras de bambú", true, FormaArchivo.CAJA, FormaFicha.TIRA, Adorno.LACA, "el estante del caravasar")
    TemaId.EDO -> ArchivoIdeas("cajón del tansu", "cajones del tansu", "tanzaku", "tanzaku", false, FormaArchivo.CAJONERA, FormaFicha.TIRA, Adorno.HERRAJES, "el tansu")
    TemaId.MONGOL -> ArchivoIdeas("arcón de la ger", "arcones de la ger", "tablilla de abedul", "tablillas de abedul", false, FormaArchivo.ARCON, FormaFicha.TABLILLA, Adorno.TEJIDO, "la ger")
    TemaId.MEDIEVAL -> ArchivoIdeas("anaquel", "anaqueles", "folio", "folios", false, FormaArchivo.ARCON, FormaFicha.TARJETA, Adorno.HERRAJES, "el armario del scriptorium")
    TemaId.TEMPLARIO -> ArchivoIdeas("arca", "arcas", "carta sellada", "cartas selladas", true, FormaArchivo.ARCON, FormaFicha.EXPEDIENTE, Adorno.CRUZ, "la cámara del tesoro")
    TemaId.COLONOS -> ArchivoIdeas("cajetín", "cajetines", "recorte", "recortes", false, FormaArchivo.CAJONERA, FormaFicha.RECORTE, Adorno.NADA, "la cajonera del almacén")
    TemaId.ORDEN -> ArchivoIdeas("gaveta", "gavetas", "ficha técnica", "fichas técnicas", true, FormaArchivo.CAJONERA, FormaFicha.EXPEDIENTE, Adorno.REMACHES, "la bóveda de la orden")
    TemaId.VAPOR -> ArchivoIdeas("gaveta de latón", "gavetas de latón", "tarjeta perforada", "tarjetas perforadas", true, FormaArchivo.CAJONERA, FormaFicha.TICKET, Adorno.REMACHES, "el gabinete de patentes")
    TemaId.LABORATORIO -> ArchivoIdeas("caja de muestras", "cajas de muestras", "ficha de protocolo", "fichas de protocolo", true, FormaArchivo.CAJONERA, FormaFicha.TARJETA, Adorno.NADA, "el armario de muestras")
    TemaId.NAPOLEON -> ArchivoIdeas("casilla", "casillas", "despacho", "despachos", true, FormaArchivo.CASILLERO, FormaFicha.EXPEDIENTE, Adorno.ORO, "el secrétaire de campaña")
    TemaId.DECO -> ArchivoIdeas("casillero", "casilleros", "telegrama", "telegramas", false, FormaArchivo.CASILLERO, FormaFicha.TICKET, Adorno.ORO, "la recepción del hotel")
    TemaId.BOTANICO -> ArchivoIdeas("caja de herbario", "cajas de herbario", "pliego", "pliegos", true, FormaArchivo.CAJA, FormaFicha.TARJETA, Adorno.CORREAS, "el gabinete del naturalista")
    TemaId.RUINAS -> ArchivoIdeas("lata", "latas", "nota", "notas", true, FormaArchivo.CAJA, FormaFicha.RECORTE, Adorno.HIEDRA, "la estantería rescatada")
    TemaId.NAVIDAD -> ArchivoIdeas("caja de galletas", "cajas de galletas", "tarjeta de felicitación", "tarjetas de felicitación", true, FormaArchivo.CAJA, FormaFicha.TARJETA, Adorno.NIEVE, "la alacena de la cocina")
    TemaId.DISTOPIA -> ArchivoIdeas("gaveta", "gavetas", "expediente", "expedientes", true, FormaArchivo.CAJONERA, FormaFicha.EXPEDIENTE, Adorno.CINTA, "el archivo del ministerio")
    TemaId.REFUGIO -> ArchivoIdeas("cartucho", "cartuchos", "registro", "registros", false, FormaArchivo.DIRECTORIO, FormaFicha.VENTANA, Adorno.REMACHES, "el armario de cartuchos")
    TemaId.CIBER -> ArchivoIdeas("directorio", "directorios", "archivo", "archivos", false, FormaArchivo.DIRECTORIO, FormaFicha.VENTANA, Adorno.CODIGO, "la terminal")
    TemaId.NEON -> ArchivoIdeas("riel", "rieles", "ticket", "tickets", false, FormaArchivo.RIEL, FormaFicha.TICKET, Adorno.NADA, "la barra del puesto")
    TemaId.GOTICO -> ArchivoIdeas("cajón de la sacristía", "cajones de la sacristía", "hoja de vitela", "hojas de vitela", false, FormaArchivo.CAJONERA, FormaFicha.TARJETA, Adorno.ORO, "la cajonera de la sacristía")
    TemaId.ARCANO -> ArchivoIdeas("caja de archivo", "cajas de archivo", "ficha de evidencia", "fichas de evidencia", true, FormaArchivo.CAJA, FormaFicha.EXPEDIENTE, Adorno.BRAMANTE, "el archivo del pueblo")
    TemaId.ESPACIO -> ArchivoIdeas("módulo", "módulos", "registro", "registros", false, FormaArchivo.DIRECTORIO, FormaFicha.VENTANA, Adorno.NADA, "la sala de control")
    TemaId.ABISMO -> ArchivoIdeas("caja estanca", "cajas estancas", "pizarra de buceo", "pizarras de buceo", true, FormaArchivo.CAJA, FormaFicha.TABLILLA, Adorno.REMACHES, "el batiscafo")
    TemaId.ANDALUS -> ArchivoIdeas("cajita de taracea", "cajitas de taracea", "hoja", "hojas", true, FormaArchivo.CAJONERA, FormaFicha.TARJETA, Adorno.TARACEA, "la alacena")
    TemaId.MESO -> ArchivoIdeas("petaca", "petacas", "hoja de amate", "hojas de amate", true, FormaArchivo.CAJA, FormaFicha.TIRA, Adorno.TEJIDO, "el muro pintado")
    TemaId.RENACIMIENTO -> ArchivoIdeas("cajoncito del studiolo", "cajoncitos del studiolo", "hoja de boceto", "hojas de boceto", false, FormaArchivo.CAJONERA, FormaFicha.TARJETA, Adorno.HERRAJES, "el studiolo")
    TemaId.PIRATA -> ArchivoIdeas("cofre", "cofres", "carta de marear", "cartas de marear", false, FormaArchivo.ARCON, FormaFicha.RECORTE, Adorno.HERRAJES, "la bodega")
    TemaId.URSS -> ArchivoIdeas("cajón de la kartoteka", "cajones de la kartoteka", "ficha de catálogo", "fichas de catálogo", false, FormaArchivo.CAJONERA, FormaFicha.EXPEDIENTE, Adorno.NADA, "la kartoteka")
    TemaId.REVOLUCION -> ArchivoIdeas("caja de los cahiers", "cajas de los cahiers", "pasquín", "pasquines", true, FormaArchivo.CAJA, FormaFicha.RECORTE, Adorno.CINTA, "el estante de la sección")
    TemaId.ANTIGUAS -> ArchivoIdeas("arcón de la compañía", "arcones de la compañía", "página del cronista", "páginas del cronista", false, FormaArchivo.ARCON, FormaFicha.TARJETA, Adorno.CORREAS, "la sala de piedra")
    TemaId.CUEVA -> ArchivoIdeas("nicho", "nichos", "laja", "lajas", false, FormaArchivo.NICHO, FormaFicha.LAJA, Adorno.NADA, "la pared de la cueva")
}

// =====================================================================
//  El papel de las fichas
// =====================================================================

/**
 * La página del tema para una ficha: la misma letra, tinta y papel, sin capitular (una ficha no es un libro iluminado) y
 * siempre rayada: si el tema no tiene renglones, se le ponen.
 */
fun paginaFicha(t: TemaId): Pagina {
    val p = pagina(t)
    return Pagina(p.fondo, p.color, p.tinta, p.tenue, p.acento, p.detalle, p.texto, p.tam, p.interlinea, p.oscura,
        if (p.rayas == Rayas.NADA) Rayas.RENGLONES else p.rayas, capitular = null)
}

/** El papel de una ficha: el color y la textura del tema, sin sus adornos; en modo oscuro, el papel claro se atenúa un poco. */
@Composable
fun PapelFicha(p: Pagina, modifier: Modifier = Modifier) {
    val oscuroApp = esOscuro()
    Box(modifier.fillMaxSize().background(p.color)) {
        p.fondo?.let { Image(painterResource(it), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
        // Un velo más ligero que el del diario (22 %): en una ficha chica, más oscuro ya se lee como gris.
        if (oscuroApp && !p.oscura) Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.12f)))
    }
}

/** El rayado de una ficha: la línea de color bajo el encabezado ([arriba]) y renglones finos cada [paso]. */
fun Modifier.rayadoFicha(p: Pagina, arriba: Dp, paso: Dp): Modifier = this.then(Modifier.drawBehind {
    val y0 = arriba.toPx(); val dy = paso.toPx().coerceAtLeast(6f)
    drawLine(p.acento.copy(alpha = 0.7f), Offset(0f, y0), Offset(size.width, y0), 1.6f)
    var y = y0 + dy
    while (y < size.height) { drawLine(p.tenue.copy(alpha = 0.22f), Offset(0f, y), Offset(size.width, y), 1f); y += dy }
})

/** Un número estable de 0 a 1 para que cada ficha tenga sus irregularidades, siempre las mismas. */
private fun ruido(semilla: Int, k: Int): Float = (((semilla * 31 + k * 977) xor (k * 7919)) and 0x7fff) / 32767f

/** El contorno de una ficha según su forma. [semilla] cambia las irregularidades de la laja y del recorte. */
fun formaDeFicha(f: FormaFicha, semilla: Int = 0): Shape = when (f) {
    FormaFicha.TARJETA, FormaFicha.VENTANA -> RoundedCornerShape(3.dp)
    FormaFicha.TABLILLA -> RoundedCornerShape(10.dp)
    FormaFicha.TIRA -> RoundedCornerShape(2.dp)
    // Borde dentado arriba y abajo, como un boleto cortado del rollo.
    FormaFicha.TICKET -> GenericShape { s, _ ->
        val d = (s.width / 18f).coerceAtLeast(6f)
        moveTo(0f, 0f)
        var x = 0f; while (x < s.width) { lineTo(minOf(x + d / 2, s.width), d * 0.45f); lineTo(minOf(x + d, s.width), 0f); x += d }
        lineTo(s.width, s.height)
        x = s.width; while (x > 0f) { lineTo(maxOf(x - d / 2, 0f), s.height - d * 0.45f); lineTo(maxOf(x - d, 0f), s.height); x -= d }
        close()
    }
    // La carpeta con su pestaña arriba a la izquierda.
    FormaFicha.EXPEDIENTE -> GenericShape { s, _ ->
        val tab = (s.height * 0.09f).coerceIn(8f, 40f)
        moveTo(0f, tab); lineTo(s.width * 0.06f, 0f); lineTo(s.width * 0.38f, 0f); lineTo(s.width * 0.44f, tab)
        lineTo(s.width, tab); lineTo(s.width, s.height); lineTo(0f, s.height); close()
    }
    // Una piedra o un tiesto: el rectángulo con las esquinas desportilladas, cada una distinta, y los lados apenas irregulares.
    FormaFicha.LAJA -> GenericShape { s, _ ->
        val base = minOf(s.width, s.height) * 0.09f
        fun c(k: Int) = base * (0.6f + 0.8f * ruido(semilla, k))
        fun b(k: Int) = minOf(s.width, s.height) * 0.012f * ruido(semilla, k + 30)
        moveTo(c(0), 0f)
        lineTo(s.width / 2, b(1)); lineTo(s.width - c(2), 0f)
        lineTo(s.width, c(3)); lineTo(s.width - b(4), s.height / 2); lineTo(s.width, s.height - c(5))
        lineTo(s.width - c(6), s.height); lineTo(s.width / 2, s.height - b(7)); lineTo(c(8), s.height)
        lineTo(0f, s.height - c(9)); lineTo(b(10), s.height / 2); lineTo(0f, c(11))
        close()
    }
    // Recortado con tijera: los lados con pequeños saltos.
    FormaFicha.RECORTE -> GenericShape { s, _ ->
        val pasos = 9
        moveTo(0f, 0f)
        for (k in 1..pasos) lineTo(s.width * k / pasos, (s.height * 0.025f) * ruido(semilla, k))
        for (k in 1..pasos) lineTo(s.width - (s.width * 0.02f) * ruido(semilla, k + 20), s.height * k / pasos)
        for (k in 1..pasos) lineTo(s.width - s.width * k / pasos, s.height - (s.height * 0.025f) * ruido(semilla, k + 40))
        for (k in 1 until pasos) lineTo((s.width * 0.02f) * ruido(semilla, k + 60), s.height - s.height * k / pasos)
        close()
    }
}

/**
 * Lo que cada forma de ficha lleva encima del papel: el marco de cera de la tablilla, el agujero y el cordón del tanzaku,
 * las perforaciones del boleto, el sello del expediente, la barra de título de la ventana. [m] da los colores del objeto.
 */
fun DrawScope.adornoFicha(f: FormaFicha, p: Pagina, m: Materia, grande: Boolean = false) {
    val w = size.width; val h = size.height
    val g = if (grande) 2f else 1f
    when (f) {
        FormaFicha.TABLILLA -> {
            val marco = 6.dp.toPx() * g
            drawRoundRect(m.cuerpo, Offset(marco / 2, marco / 2), Size(w - marco, h - marco), CornerRadius(8.dp.toPx() * g), style = Stroke(marco))
            drawRoundRect(m.sombra.copy(alpha = 0.5f), Offset(marco, marco), Size(w - 2 * marco, h - 2 * marco), CornerRadius(5.dp.toPx() * g), style = Stroke(1.2f))
        }
        FormaFicha.TIRA -> {
            val c = Offset(w / 2, 9.dp.toPx() * g)
            drawCircle(m.sombra.copy(alpha = 0.7f), 3.dp.toPx() * g, c)
            drawLine(m.acento, c, Offset(w / 2 - 6.dp.toPx() * g, 0f), 1.5f * g)
            drawLine(m.acento, c, Offset(w / 2 + 6.dp.toPx() * g, 0f), 1.5f * g)
        }
        FormaFicha.TICKET -> {
            var x = 10.dp.toPx(); val y = 18.dp.toPx() * g
            while (x < w - 6.dp.toPx()) { drawCircle(p.tenue.copy(alpha = 0.35f), 1.2.dp.toPx(), Offset(x, y)); x += 7.dp.toPx() }
        }
        // El sello del expediente: solo en la ficha grande (en las chicas taparía el texto).
        FormaFicha.EXPEDIENTE -> if (grande) {
            val r = (minOf(w, h) * 0.11f).coerceAtMost(46.dp.toPx())
            val c = Offset(w - r * 1.5f, h - r * 1.5f)
            drawCircle(p.acento.copy(alpha = 0.35f), r, c, style = Stroke(2f * g))
            drawCircle(p.acento.copy(alpha = 0.25f), r * 0.72f, c, style = Stroke(1f * g))
        }
        FormaFicha.VENTANA -> {
            val barra = altoBarraVentana(grande).toPx()
            drawRect(p.acento.copy(alpha = 0.85f), Offset.Zero, Size(w, barra))
            listOf(0, 1, 2).forEach { k -> drawCircle(p.color, barra * 0.18f, Offset(w - barra * (0.7f + k * 0.6f), barra / 2)) }
        }
        FormaFicha.LAJA -> {
            // Vetas de la piedra o del barro.
            for (k in 0 until 3) drawLine(p.tenue.copy(alpha = 0.18f), Offset(w * (0.1f + 0.3f * k), h * 0.9f), Offset(w * (0.3f + 0.3f * k), h * 0.75f), 1f)
        }
        FormaFicha.RECORTE, FormaFicha.TARJETA -> {}
    }
}

/** Cuánto baja el texto en cada forma (la barra de la ventana, la pestaña del expediente, el agujero del tanzaku, el marco). */
fun margenSuperior(f: FormaFicha, grande: Boolean = false): Dp = when (f) {
    FormaFicha.VENTANA -> 0.dp
    FormaFicha.EXPEDIENTE -> if (grande) 26.dp else 13.dp
    FormaFicha.TIRA -> if (grande) 30.dp else 16.dp
    FormaFicha.TABLILLA -> if (grande) 18.dp else 10.dp
    FormaFicha.TICKET -> if (grande) 30.dp else 22.dp
    else -> if (grande) 12.dp else 6.dp
}

/** El alto de la barra de título de la ficha-ventana: ahí va el encabezado (tema y fecha). */
fun altoBarraVentana(grande: Boolean = false): Dp = if (grande) 30.dp else 16.dp

/** El color del texto sobre el interior del contenedor abierto (claro sobre madera o pantalla, oscuro sobre papel). */
fun tintaInterior(a: ArchivoIdeas, m: Materia): Color {
    val fondo = when (a.forma) {
        FormaArchivo.DIRECTORIO -> m.hoja
        FormaArchivo.NICHO -> lerp(m.sombra, Color.Black, 0.3f)
        FormaArchivo.RIEL -> m.cuerpo
        else -> lerp(m.sombra, m.cuerpo, 0.5f).let { lerp(it, Color.Black, 0.3f) }
    }
    return if (fondo.luminance() < 0.45f) Color(0xFFF4EEDF) else Color(0xFF1E1A16)
}

/** Margen a los lados según la forma (el marco de la tablilla, los bordes de la laja). */
fun margenLado(f: FormaFicha, grande: Boolean = false): Dp = when (f) {
    FormaFicha.TABLILLA -> if (grande) 22.dp else 11.dp
    FormaFicha.LAJA -> if (grande) 26.dp else 12.dp
    FormaFicha.TIRA -> if (grande) 20.dp else 7.dp
    else -> if (grande) 22.dp else 9.dp
}

// =====================================================================
//  El contenedor cerrado (en el fichero) y su interior (al abrirlo)
// =====================================================================

/** Dónde va la etiqueta con el tema dentro del contenedor, en fracciones del ancho y del alto. La misma para todas las formas. */
val ETIQUETA_ARCHIVO = Rect(0.18f, 0.44f, 0.82f, 0.74f)

/** El color del papel de la etiqueta y el de su letra. En las pantallas, la etiqueta es el propio cristal. */
fun colorEtiqueta(a: ArchivoIdeas, m: Materia): Pair<Color, Color> = when (a.forma) {
    FormaArchivo.DIRECTORIO -> lerp(m.cuerpo, Color.Black, 0.3f) to m.acento
    FormaArchivo.NICHO -> lerp(m.hoja, m.cuerpo, 0.25f) to m.acento
    else -> m.hoja to m.tinta
}

/** Las fichas que asoman por arriba del contenedor: más cuanto más lleno (hasta 5), con la forma de las fichas del tema. */
private fun DrawScope.asoman(a: ArchivoIdeas, m: Materia, n: Int, x0: Float, x1: Float, arriba: Float, abajo: Float) {
    val k = n.coerceIn(1, 5)
    val ancho = x1 - x0
    for (i in 0 until k) {
        val c = lerp(m.hoja, Color.Black, 0.06f * i)
        when (a.formaFicha) {
            FormaFicha.TIRA -> {
                val wi = ancho * 0.1f; val xi = x0 + ancho * (0.12f + i * 0.17f)
                drawRect(c, Offset(xi, arriba + (abajo - arriba) * 0.05f * i), Size(wi, abajo - arriba))
                drawCircle(m.acento, wi * 0.18f, Offset(xi + wi / 2, arriba + (abajo - arriba) * 0.05f * i + wi * 0.4f))
            }
            FormaFicha.TABLILLA -> drawRoundRect(lerp(m.cuerpo, Color.Black, 0.1f * i), Offset(x0 + ancho * (0.08f + 0.03f * i), arriba + (abajo - arriba) * 0.12f * i),
                Size(ancho * 0.8f, abajo - arriba), CornerRadius(6f))
            FormaFicha.VENTANA -> {
                val xi = x0 + ancho * (0.08f + 0.04f * i); val yi = arriba + (abajo - arriba) * 0.12f * i
                drawRect(m.acento.copy(alpha = 0.25f + 0.1f * i), Offset(xi, yi), Size(ancho * 0.78f, abajo - arriba), style = Stroke(1.5f))
            }
            else -> drawRect(c, Offset(x0 + ancho * (0.08f + 0.035f * i), arriba + (abajo - arriba) * 0.12f * i), Size(ancho * 0.8f - ancho * 0.02f * i, abajo - arriba))
        }
    }
}

/** La etiqueta (portaetiquetas de latón con su cartulina, o la placa que le toque a cada forma). */
private fun DrawScope.etiqueta(a: ArchivoIdeas, m: Materia) {
    val w = size.width; val h = size.height
    val r = Rect(ETIQUETA_ARCHIVO.left * w, ETIQUETA_ARCHIVO.top * h, ETIQUETA_ARCHIVO.right * w, ETIQUETA_ARCHIVO.bottom * h)
    val (fondo, _) = colorEtiqueta(a, m)
    when (a.forma) {
        FormaArchivo.DIRECTORIO -> {
            drawRect(fondo, r.topLeft, r.size)
            drawRect(m.acento.copy(alpha = 0.6f), r.topLeft, r.size, style = Stroke(1.5f))
        }
        FormaArchivo.NICHO -> drawRoundRect(fondo.copy(alpha = 0.85f), r.topLeft, r.size, CornerRadius(r.height * 0.4f))
        FormaArchivo.ARCON, FormaArchivo.CAPSA -> {
            drawRoundRect(m.detalle, Offset(r.left - 3f, r.top - 3f), Size(r.width + 6f, r.height + 6f), CornerRadius(r.height * 0.25f))
            drawRoundRect(fondo, r.topLeft, r.size, CornerRadius(r.height * 0.2f))
        }
        else -> {
            drawRoundRect(m.detalle, Offset(r.left - 3f, r.top - 3f), Size(r.width + 6f, r.height + 6f), CornerRadius(3f))
            drawRect(fondo, r.topLeft, r.size)
        }
    }
}

/** Los adornos propios del tema sobre el cuerpo del contenedor ([cuerpo] es el rectángulo del frente). */
private fun DrawScope.adornoArchivo(a: ArchivoIdeas, m: Materia, cuerpo: Rect) {
    val w = cuerpo.width; val h = cuerpo.height
    fun punto(x: Float, y: Float, r: Float, c: Color) = drawCircle(c, r, Offset(cuerpo.left + w * x, cuerpo.top + h * y))
    when (a.adorno) {
        Adorno.HERRAJES -> {
            // Escuadras de hierro en las cuatro esquinas.
            val l = w * 0.12f; val g = 3.5f
            listOf(0f to 0f, 1f to 0f, 0f to 1f, 1f to 1f).forEach { (fx, fy) ->
                val x = cuerpo.left + fx * w; val y = cuerpo.top + fy * h
                val sx = if (fx == 0f) 1 else -1; val sy = if (fy == 0f) 1 else -1
                drawLine(m.sombra, Offset(x, y + sy * g), Offset(x + sx * l, y + sy * g), g * 1.6f)
                drawLine(m.sombra, Offset(x + sx * g, y), Offset(x + sx * g, y + sy * l), g * 1.6f)
            }
        }
        Adorno.REMACHES -> listOf(0.05f to 0.08f, 0.95f to 0.08f, 0.05f to 0.92f, 0.95f to 0.92f).forEach { (x, y) -> punto(x, y, 3f, m.brillo); punto(x, y, 1.6f, m.sombra) }
        Adorno.TARACEA -> {
            // Una cenefa de rombos alrededor del frente.
            val n = 14
            for (i in 0 until n) {
                val x = cuerpo.left + w * (i + 0.5f) / n
                listOf(cuerpo.top + h * 0.06f, cuerpo.bottom - h * 0.06f).forEach { y ->
                    val d = w / n * 0.32f
                    drawPath(Path().apply { moveTo(x, y - d); lineTo(x + d, y); lineTo(x, y + d); lineTo(x - d, y); close() }, if (i % 2 == 0) m.detalle else m.brillo)
                }
            }
        }
        Adorno.CRUZ -> {
            val c = Offset(cuerpo.left + w * 0.1f, cuerpo.top + h * 0.25f); val l = h * 0.14f
            drawLine(m.acento, Offset(c.x - l, c.y), Offset(c.x + l, c.y), l * 0.5f)
            drawLine(m.acento, Offset(c.x, c.y - l), Offset(c.x, c.y + l), l * 0.5f)
        }
        Adorno.TEJIDO -> {
            // El tejido del petate o del fieltro: rayas cruzadas.
            var x = cuerpo.left; val paso = w / 16
            while (x < cuerpo.right) { drawLine(m.sombra.copy(alpha = 0.35f), Offset(x, cuerpo.top), Offset(minOf(x + h * 0.5f, cuerpo.right), cuerpo.bottom), 1.2f); x += paso }
            x = cuerpo.left
            while (x < cuerpo.right) { drawLine(m.brillo.copy(alpha = 0.3f), Offset(minOf(x + h * 0.5f, cuerpo.right), cuerpo.top), Offset(x, cuerpo.bottom), 1.2f); x += paso }
        }
        Adorno.LACA -> {
            drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.22f), Color.Transparent), Offset(cuerpo.left, cuerpo.top), Offset(cuerpo.left + w * 0.5f, cuerpo.top + h)), cuerpo.topLeft, cuerpo.size)
            drawRect(m.detalle, Offset(cuerpo.left + 4f, cuerpo.top + 4f), Size(w - 8f, h - 8f), style = Stroke(1.5f))
        }
        Adorno.NIEVE -> {
            drawRect(Color.White.copy(alpha = 0.9f), cuerpo.topLeft, Size(w, h * 0.08f))
            for (i in 0 until 9) punto(0.05f + i * 0.11f, 0.08f + 0.04f * (i % 2), 3f + (i % 3), Color.White)
        }
        Adorno.CINTA -> {
            // Tiras de cinta (los boletos de la Urbe) o la cinta tricolor.
            val y = cuerpo.top + h * 0.22f
            drawRect(m.extra.copy(alpha = 0.85f), Offset(cuerpo.left, y), Size(w, h * 0.07f))
            drawRect(m.acento.copy(alpha = 0.85f), Offset(cuerpo.left, y + h * 0.07f), Size(w, h * 0.04f))
        }
        Adorno.BRAMANTE -> {
            drawLine(m.extra, Offset(cuerpo.left + w * 0.5f, cuerpo.top), Offset(cuerpo.left + w * 0.5f, cuerpo.bottom), 2f)
            drawLine(m.extra, Offset(cuerpo.left, cuerpo.top + h * 0.3f), Offset(cuerpo.right, cuerpo.top + h * 0.3f), 2f)
        }
        Adorno.MEANDRO -> {
            // La greca, abajo.
            val y = cuerpo.bottom - h * 0.1f; val paso = w / 12; var x = cuerpo.left + paso * 0.3f
            val p = Path().apply { moveTo(x, y) }
            while (x < cuerpo.right - paso) { p.lineTo(x, y - paso * 0.5f); p.lineTo(x + paso * 0.6f, y - paso * 0.5f); p.lineTo(x + paso * 0.6f, y); p.lineTo(x + paso, y); x += paso }
            drawPath(p, m.detalle, style = Stroke(1.6f))
        }
        Adorno.GLIFOS -> for (i in 0 until 6) {
            val x = cuerpo.left + w * (0.08f + i * 0.17f); val y = cuerpo.top + h * 0.2f
            if (i % 2 == 0) drawCircle(m.acento, h * 0.04f, Offset(x, y)) else drawLine(m.detalle, Offset(x, y - h * 0.05f), Offset(x, y + h * 0.05f), 2.5f)
        }
        Adorno.ORO -> drawRect(m.detalle, Offset(cuerpo.left + 5f, cuerpo.top + 5f), Size(w - 10f, h - 10f), style = Stroke(1.6f))
        Adorno.HIEDRA -> for (i in 0 until 5) {
            val c = Offset(cuerpo.left + w * (0.06f + i * 0.08f), cuerpo.top + h * (0.15f + 0.12f * (i % 2)))
            drawOval(m.extra, Offset(c.x - h * 0.05f, c.y - h * 0.03f), Size(h * 0.1f, h * 0.06f))
        }
        Adorno.CODIGO -> {
            var y = cuerpo.top + h * 0.12f
            while (y < cuerpo.bottom) { drawLine(m.acento.copy(alpha = 0.08f), Offset(cuerpo.left, y), Offset(cuerpo.right, y), 1f); y += 4f }
        }
        Adorno.CORREAS -> listOf(0.22f, 0.78f).forEach { x ->
            drawRect(m.sombra, Offset(cuerpo.left + w * x - w * 0.03f, cuerpo.top), Size(w * 0.06f, h))
            drawRect(m.detalle, Offset(cuerpo.left + w * x - w * 0.035f, cuerpo.top + h * 0.82f), Size(w * 0.07f, h * 0.08f), style = Stroke(1.5f))
        }
        Adorno.NADA -> {}
    }
}

/**
 * Un contenedor cerrado del fichero, con su forma: el cajón con su portaetiquetas, el arcón abierto con las fichas de pie, la
 * capsa redonda, la caja, el casillero, la carpeta en pantalla, el riel de tickets o el nicho. [n] fichas asoman. La etiqueta
 * queda en [ETIQUETA_ARCHIVO], para escribir encima el tema.
 */
fun DrawScope.dibujarContenedor(a: ArchivoIdeas, m: Materia, t: TemaId, n: Int) {
    val w = size.width; val h = size.height
    val top = h * 0.18f
    val frente = Rect(0f, top, w, h)
    when (a.forma) {
        FormaArchivo.CAJONERA -> {
            asoman(a, m, n, w * 0.06f, w * 0.94f, 0f, top + 4f)
            drawRoundRect(m.sombra, frente.topLeft, frente.size, CornerRadius(4f))
            drawRoundRect(lerp(m.cuerpo, m.brillo, 0.25f), Offset(3f, top + 3f), Size(w - 6f, h - top - 6f), CornerRadius(3f))
            drawRoundRect(m.sombra.copy(alpha = 0.6f), Offset(w * 0.06f, top + h * 0.06f), Size(w * 0.88f, h - top - h * 0.12f), CornerRadius(3f), style = Stroke(1.2f))
            adornoArchivo(a, m, frente)
            // El tirador, debajo de la etiqueta.
            drawRoundRect(m.detalle, Offset(w * 0.4f, h * (ETIQUETA_ARCHIVO.bottom + 0.06f)), Size(w * 0.2f, h * 0.07f), CornerRadius(h * 0.035f))
        }
        FormaArchivo.ARCON -> {
            // La tapa abierta hacia atrás, las fichas de pie y el cuerpo con sus bandas.
            drawPath(Path().apply { moveTo(w * 0.04f, top + 4f); lineTo(w * 0.12f, 0f); lineTo(w * 0.88f, 0f); lineTo(w * 0.96f, top + 4f); close() }, lerp(m.cuerpo, Color.Black, 0.35f))
            asoman(a, m, n, w * 0.1f, w * 0.9f, h * 0.04f, top + 6f)
            drawRoundRect(m.sombra, frente.topLeft, frente.size, CornerRadius(8f))
            drawRoundRect(m.cuerpo, Offset(3f, top + 3f), Size(w - 6f, h - top - 6f), CornerRadius(6f))
            listOf(0.08f, 0.92f).forEach { x -> drawRect(m.detalle.copy(alpha = 0.85f), Offset(w * x - w * 0.025f, top), Size(w * 0.05f, h - top)) }
            adornoArchivo(a, m, frente)
            drawRoundRect(m.detalle, Offset(w * 0.45f, h * (ETIQUETA_ARCHIVO.bottom + 0.05f)), Size(w * 0.1f, h * 0.1f), CornerRadius(3f))
        }
        FormaArchivo.CAPSA -> {
            // Un cilindro visto de frente: la boca ovalada arriba, con las fichas enrolladas, y aros de metal.
            val boca = Rect(w * 0.06f, top - h * 0.06f, w * 0.94f, top + h * 0.1f)
            drawOval(lerp(m.cuerpo, Color.Black, 0.45f), boca.topLeft, boca.size)
            for (i in 0 until n.coerceIn(1, 6)) {
                val cx = w * (0.2f + i * 0.12f); val cy = top - h * 0.02f - h * 0.06f * (i % 2)
                drawCircle(m.hoja, h * 0.07f, Offset(cx, cy)); drawCircle(m.sombra.copy(alpha = 0.5f), h * 0.07f, Offset(cx, cy), style = Stroke(1.2f))
                drawCircle(m.sombra.copy(alpha = 0.4f), h * 0.025f, Offset(cx, cy))
            }
            drawRoundRect(m.sombra, Offset(w * 0.06f, top + h * 0.02f), Size(w * 0.88f, h - top - h * 0.02f), CornerRadius(w * 0.08f))
            drawRoundRect(Brush.horizontalGradient(listOf(m.sombra, m.cuerpo, m.brillo, m.cuerpo, m.sombra)), Offset(w * 0.07f, top + h * 0.03f), Size(w * 0.86f, h - top - h * 0.05f), CornerRadius(w * 0.07f))
            listOf(top + h * 0.08f, h * 0.9f).forEach { y -> drawRect(m.detalle, Offset(w * 0.06f, y), Size(w * 0.88f, h * 0.04f)) }
            adornoArchivo(a, m, Rect(w * 0.07f, top, w * 0.93f, h))
        }
        FormaArchivo.CAJA -> {
            // Caja abierta: la pared de atrás, las fichas y el frente.
            drawRect(lerp(m.cuerpo, Color.Black, 0.3f), Offset(w * 0.03f, top - h * 0.06f), Size(w * 0.94f, h * 0.12f))
            asoman(a, m, n, w * 0.06f, w * 0.94f, 0f, top + 4f)
            drawRect(m.sombra, frente.topLeft, frente.size)
            drawRect(m.cuerpo, Offset(2f, top + 2f), Size(w - 4f, h - top - 4f))
            drawLine(m.brillo, Offset(2f, top + 3f), Offset(w - 2f, top + 3f), 2f)
            adornoArchivo(a, m, frente)
        }
        FormaArchivo.CASILLERO -> {
            // Casillas de 4 × 2 en el marco; en las primeras asoman las fichas.
            drawRoundRect(m.sombra, Offset.Zero, Size(w, h), CornerRadius(4f))
            drawRoundRect(m.cuerpo, Offset(3f, 3f), Size(w - 6f, h - 6f), CornerRadius(3f))
            val cols = 4; val filas = 2
            val ch = h * 0.34f; val cw = (w - 12f) / cols
            for (f in 0 until filas) for (c in 0 until cols) {
                val x = 6f + c * cw; val y = 6f + f * (ch * 0.62f)
                drawRect(lerp(m.cuerpo, Color.Black, 0.45f), Offset(x + 2f, y + 2f), Size(cw - 4f, ch * 0.62f - 4f))
                if (f * cols + c < n) drawRect(m.hoja, Offset(x + cw * 0.15f, y + ch * 0.18f), Size(cw * 0.7f, ch * 0.62f - ch * 0.2f - 4f))
            }
            adornoArchivo(a, m, Rect(0f, 0f, w, h))
        }
        FormaArchivo.DIRECTORIO -> {
            // Una carpeta en pantalla: la pestaña, el cuerpo con su borde de luz y las líneas de barrido.
            val cuerpo = lerp(m.cuerpo, Color.Black, 0.1f)
            drawRoundRect(cuerpo, Offset(0f, top), Size(w, h - top), CornerRadius(6f))
            drawPath(Path().apply { moveTo(0f, top + 6f); lineTo(0f, top - h * 0.06f); lineTo(w * 0.36f, top - h * 0.06f); lineTo(w * 0.42f, top); close() }, cuerpo)
            drawRoundRect(m.acento.copy(alpha = 0.8f), Offset(1f, top + 1f), Size(w - 2f, h - top - 2f), CornerRadius(6f), style = Stroke(1.5f))
            for (i in 0 until n.coerceIn(1, 5)) drawLine(m.acento.copy(alpha = 0.5f), Offset(w * 0.08f, top + h * (0.08f + 0.05f * i)), Offset(w * (0.35f + 0.08f * i), top + h * (0.08f + 0.05f * i)), 2f)
            adornoArchivo(a, m, Rect(0f, top, w, h))
        }
        FormaArchivo.RIEL -> {
            // El riel de la cocina del puesto, con tickets colgando de sus pinzas y un brillo de neón abajo.
            drawRect(m.detalle, Offset(0f, h * 0.06f), Size(w, h * 0.05f))
            val k = n.coerceIn(1, 5)
            for (i in 0 until k) {
                val x = w * (0.04f + i * 0.19f)
                drawRect(m.brillo, Offset(x + w * 0.05f, h * 0.03f), Size(w * 0.06f, h * 0.08f))
                drawRect(lerp(m.hoja, Color.White, 0.8f).copy(alpha = 0.9f - 0.1f * i), Offset(x, h * 0.11f), Size(w * 0.16f, h * 0.3f))
            }
            drawRoundRect(m.cuerpo, Offset(0f, h * 0.4f), Size(w, h * 0.6f), CornerRadius(6f))
            drawRoundRect(m.acento.copy(alpha = 0.85f), Offset(2f, h * 0.4f + 2f), Size(w - 4f, h * 0.6f - 4f), CornerRadius(6f), style = Stroke(2.5f))
            drawRoundRect(m.acento.copy(alpha = 0.25f), Offset(6f, h * 0.4f + 6f), Size(w - 12f, h * 0.6f - 12f), CornerRadius(5f), style = Stroke(6f))
        }
        FormaArchivo.NICHO -> {
            // La pared de roca con un hueco y lajas apoyadas dentro.
            drawRoundRect(m.cuerpo, Offset.Zero, Size(w, h), CornerRadius(w * 0.08f))
            for (i in 0 until 18) drawCircle(m.sombra.copy(alpha = 0.25f), 2f + (i % 3), Offset(w * ruido(7, i), h * ruido(11, i)))
            val hueco = Path().apply {
                moveTo(w * 0.1f, h * 0.9f); quadraticTo(w * 0.05f, h * 0.15f, w * 0.5f, h * 0.08f); quadraticTo(w * 0.95f, h * 0.15f, w * 0.9f, h * 0.9f); close()
            }
            drawPath(hueco, lerp(m.sombra, Color.Black, 0.35f))
            for (i in 0 until n.coerceIn(1, 4)) drawOval(lerp(m.hoja, m.cuerpo, 0.2f + 0.1f * i), Offset(w * (0.18f + i * 0.15f), h * (0.18f + 0.03f * i)), Size(w * 0.2f, h * 0.3f))
            // Una mano en negativo en la pared, como en las cuevas.
            drawCircle(m.acento.copy(alpha = 0.35f), h * 0.08f, Offset(w * 0.88f, h * 0.16f))
        }
    }
    etiqueta(a, m)
    // El símbolo del tema en una esquina de la etiqueta.
    drawPath(simboloCalificacion(t, Offset(w * (ETIQUETA_ARCHIVO.right + 0.06f), h * (ETIQUETA_ARCHIVO.top + 0.05f)), h * 0.05f), m.acento)
}

/**
 * El interior del contenedor abierto, detrás de las fichas: la madera del cajón, el metal de la gaveta, el fondo de la lata, el
 * cristal de la pantalla o la piedra del nicho, con su textura (vetas, barrido, cartón corrugado…).
 */
@Composable
fun InteriorArchivo(a: ArchivoIdeas, m: Materia, modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxSize()) { dibujarInteriorArchivo(a, m) }
}

/** Lo mismo que [InteriorArchivo], para dibujarlo dentro de otro lienzo. */
fun DrawScope.dibujarInteriorArchivo(a: ArchivoIdeas, m: Materia) {
    val fondo = when (a.forma) {
        FormaArchivo.DIRECTORIO -> listOf(lerp(m.hoja, Color.Black, 0.2f), m.hoja)
        FormaArchivo.NICHO -> listOf(lerp(m.sombra, Color.Black, 0.25f), lerp(m.cuerpo, Color.Black, 0.35f))
        FormaArchivo.RIEL -> listOf(lerp(m.cuerpo, Color.Black, 0.3f), m.cuerpo)
        else -> listOf(lerp(m.sombra, Color.Black, 0.25f), lerp(m.cuerpo, Color.Black, 0.4f))
    }
    drawRect(Brush.verticalGradient(fondo))
    val w = size.width; val h = size.height
    when (a.forma) {
        FormaArchivo.CAJONERA, FormaArchivo.ARCON, FormaArchivo.CASILLERO -> {
            // Vetas de la madera (o el cepillado del metal).
            for (i in 0 until 26) {
                val y = h * i / 26f + 8f * ruido(3, i)
                drawLine(m.brillo.copy(alpha = 0.06f), Offset(0f, y), Offset(w, y + 14f * (ruido(5, i) - 0.5f)), 1.5f)
            }
        }
        FormaArchivo.CAJA -> { var x = 0f; while (x < w) { drawLine(Color.Black.copy(alpha = 0.08f), Offset(x, 0f), Offset(x, h), 2f); x += 9.dp.toPx() } }
        FormaArchivo.CAPSA -> { var x = 0f; while (x < w) { drawLine(m.sombra.copy(alpha = 0.3f), Offset(x, 0f), Offset(x, h), 1.5f); x += 46.dp.toPx() } }
        FormaArchivo.DIRECTORIO -> {
            var y = 0f; while (y < h) { drawLine(m.acento.copy(alpha = 0.05f), Offset(0f, y), Offset(w, y), 1f); y += 3.dp.toPx() }
        }
        FormaArchivo.RIEL -> drawRect(m.acento.copy(alpha = 0.12f), Offset(0f, h - 3.dp.toPx()), Size(w, 3.dp.toPx()))
        FormaArchivo.NICHO -> for (i in 0 until 60) drawCircle(m.brillo.copy(alpha = 0.08f), 1.5f + 2 * ruido(13, i), Offset(w * ruido(17, i), h * ruido(19, i)))
    }
}
