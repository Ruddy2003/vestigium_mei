package io.github.ruddy2003.vestigium.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp

// =====================================================================
//  El archivo de ideas de cada tema: dónde se guardan las fichas y cómo son
// =====================================================================

/**
 * El archivo de ideas de un tema: cómo se llaman el contenedor (un cajón, una capsa, un directorio…) y cada ficha
 * (tablilla, tanzaku, ticket, archivo…), en singular y plural, y el mueble que los reúne. [fem] dice si el contenedor es
 * femenino. Cómo se dibuja cada cosa está en [io.github.ruddy2003.vestigium.ui.ideario.DisenoIdeas].
 */
class ArchivoIdeas(
    val contenedor: String, val contenedores: String, val ficha: String, val fichas: String, val fem: Boolean,
    /** El mueble entero donde están los contenedores («el tansu», «la pared de la cueva»). */
    val mueble: String,
) {
    /** «3 tablillas», «1 tanzaku». */
    fun cuenta(n: Int) = "$n ${if (n == 1) ficha else fichas}"
    /** «Esta capsa está vacía.», «Este cofre está vacío.» */
    val vacio: String get() = if (fem) "Esta $contenedor está vacía." else "Este $contenedor está vacío."
}

fun archivoIdeas(t: TemaId): ArchivoIdeas = when (t) {
    TemaId.ROMANO -> ArchivoIdeas("capsa", "capsas", "rollo", "rollos", true, "el armarium")
    TemaId.URBE -> ArchivoIdeas("caja de boletos", "cajas de boletos", "boleto", "boletos", true, "la edícola")
    TemaId.HELENO -> ArchivoIdeas("ánfora", "ánforas", "óstrakon", "óstraka", true, "la stoa")
    TemaId.EGIPTO -> ArchivoIdeas("cofre de cedro", "cofres de cedro", "tablilla de escriba", "tablillas de escriba", false, "la Casa de la Vida")
    TemaId.SEDA -> ArchivoIdeas("caja lacada", "cajas lacadas", "tira de bambú", "tiras de bambú", true, "el estante del caravasar")
    TemaId.EDO -> ArchivoIdeas("cajón del tansu", "cajones del tansu", "tanzaku", "tanzaku", false, "el tansu")
    TemaId.MONGOL -> ArchivoIdeas("arcón de la ger", "arcones de la ger", "tablilla de abedul", "tablillas de abedul", false, "la ger")
    TemaId.MEDIEVAL -> ArchivoIdeas("anaquel", "anaqueles", "folio", "folios", false, "el armario del scriptorium")
    TemaId.TEMPLARIO -> ArchivoIdeas("arca", "arcas", "carta sellada", "cartas selladas", true, "la cámara del tesoro")
    TemaId.COLONOS -> ArchivoIdeas("cajetín", "cajetines", "recorte", "recortes", false, "la cajonera del almacén")
    TemaId.ORDEN -> ArchivoIdeas("gaveta", "gavetas", "ficha técnica", "fichas técnicas", true, "la bóveda de la orden")
    TemaId.VAPOR -> ArchivoIdeas("gaveta de latón", "gavetas de latón", "tarjeta perforada", "tarjetas perforadas", true, "el gabinete de patentes")
    TemaId.LABORATORIO -> ArchivoIdeas("caja de muestras", "cajas de muestras", "ficha de protocolo", "fichas de protocolo", true, "el armario de muestras")
    TemaId.NAPOLEON -> ArchivoIdeas("casilla", "casillas", "despacho", "despachos", true, "el secrétaire de campaña")
    TemaId.DECO -> ArchivoIdeas("casillero", "casilleros", "telegrama", "telegramas", false, "la recepción del hotel")
    TemaId.BOTANICO -> ArchivoIdeas("caja de herbario", "cajas de herbario", "pliego", "pliegos", true, "el gabinete del naturalista")
    TemaId.RUINAS -> ArchivoIdeas("lata", "latas", "nota", "notas", true, "la estantería rescatada")
    TemaId.NAVIDAD -> ArchivoIdeas("caja de galletas", "cajas de galletas", "tarjeta de felicitación", "tarjetas de felicitación", true, "la alacena de la cocina")
    TemaId.DISTOPIA -> ArchivoIdeas("gaveta", "gavetas", "expediente", "expedientes", true, "el archivo del ministerio")
    TemaId.REFUGIO -> ArchivoIdeas("cartucho", "cartuchos", "registro", "registros", false, "el armario de cartuchos")
    TemaId.CIBER -> ArchivoIdeas("directorio", "directorios", "archivo", "archivos", false, "la terminal")
    TemaId.NEON -> ArchivoIdeas("riel", "rieles", "ticket", "tickets", false, "la barra del puesto")
    TemaId.GOTICO -> ArchivoIdeas("cajón de la sacristía", "cajones de la sacristía", "hoja de vitela", "hojas de vitela", false, "la cajonera de la sacristía")
    TemaId.ARCANO -> ArchivoIdeas("caja de archivo", "cajas de archivo", "ficha de evidencia", "fichas de evidencia", true, "el archivo del pueblo")
    TemaId.ESPACIO -> ArchivoIdeas("módulo", "módulos", "registro", "registros", false, "la sala de control")
    TemaId.ABISMO -> ArchivoIdeas("caja estanca", "cajas estancas", "pizarra de buceo", "pizarras de buceo", true, "el batiscafo")
    TemaId.ANDALUS -> ArchivoIdeas("cajita de taracea", "cajitas de taracea", "hoja", "hojas", true, "la alacena")
    TemaId.MESO -> ArchivoIdeas("petaca", "petacas", "hoja de amate", "hojas de amate", true, "el muro pintado")
    TemaId.RENACIMIENTO -> ArchivoIdeas("cajoncito del studiolo", "cajoncitos del studiolo", "hoja de boceto", "hojas de boceto", false, "el studiolo")
    TemaId.PIRATA -> ArchivoIdeas("cofre", "cofres", "carta de marear", "cartas de marear", false, "la bodega")
    TemaId.URSS -> ArchivoIdeas("cajón de la kartoteka", "cajones de la kartoteka", "ficha de catálogo", "fichas de catálogo", false, "la kartoteka")
    TemaId.REVOLUCION -> ArchivoIdeas("caja de los cahiers", "cajas de los cahiers", "pasquín", "pasquines", true, "el estante de la sección")
    TemaId.ANTIGUAS -> ArchivoIdeas("arcón de la compañía", "arcones de la compañía", "página del cronista", "páginas del cronista", false, "la sala de piedra")
    TemaId.CUEVA -> ArchivoIdeas("nicho", "nichos", "laja", "lajas", false, "la pared de la cueva")
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
