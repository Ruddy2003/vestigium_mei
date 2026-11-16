// Generado por el arnés: piezas de ArchivoIdeas.kt (ArchivoIdeas, archivoIdeas), copiadas tal cual en cada compilación. No se edita.
@file:Suppress("unused")
package io.github.ruddy2003.vestigium.ui



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
    // Las ideas, cortas como un sencillo: cada una un disco de 45 en su funda de papel, guardado en las cajas de sencillos del
    // mostrador de la tienda de discos (la tienda es recurso: las del Haight de 1967 no se comprobaron). Las del diario son los
    // discos de larga duración de la repisa del departamento.
    TemaId.PSICODELIA -> ArchivoIdeas("caja de sencillos", "cajas de sencillos", "sencillo", "sencillos", true, "el mostrador de la tienda de discos")
    // Las ideas, como pruebas de imprenta guardadas en los cajones planos del mueble de láminas del taller de litografía, donde se
    // guardan los carteles sin doblar.
    TemaId.NOUVEAU -> ArchivoIdeas("cajón de láminas", "cajones de láminas", "prueba de imprenta", "pruebas de imprenta", false, "el mueble de cajones planos del taller")
    // Las ideas, rayadas en tablillas de haya que se guardan en cestas de mimbre, en el granero sobre pilotes del oppidum (las
    // del diario van en el haz de tablillas de la casa grande).
    TemaId.GALIA -> ArchivoIdeas("cesta de mimbre", "cestas de mimbre", "tablilla de haya", "tablillas de haya", true, "el granero sobre pilotes")
    // Las ideas sueltas de la comunidad, en hojas de papiro guardadas en arquetas de olivo (como las hojas sueltas que se
    // hallaron metidas en la tapa de un códice de Nag Hammadi), en la cueva donde se reúnen.
    TemaId.ARCONTES -> ArchivoIdeas("arqueta de olivo", "arquetas de olivo", "hoja suelta", "hojas sueltas", true, "la cueva de la reunión escondida")
    // Las ideas de la red, fotografiadas en microfilm: cada una un fotograma, guardado en latas detrás de un panel falso.
    TemaId.FRENTE -> ArchivoIdeas("lata de microfilm", "latas de microfilm", "fotograma", "fotogramas", true, "el panel falso de la sala de radar")
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
    TemaId.BASICO -> ArchivoIdeas("carpeta", "carpetas", "nota", "notas", true, "el archivo")
}

// =====================================================================
//  El papel de las fichas
// =====================================================================
