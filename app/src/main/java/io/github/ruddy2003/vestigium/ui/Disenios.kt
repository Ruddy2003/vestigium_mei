package io.github.ruddy2003.vestigium.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.R

/** Letras de los temas nuevos. */
object LetraExtra {
    val gotica = FontFamily(Font(R.font.gotica_regular, FontWeight.Normal), Font(R.font.gotica_semibold, FontWeight.SemiBold))
    val oswald = FontFamily(Font(R.font.oswald_medium, FontWeight.Medium), Font(R.font.oswald_bold, FontWeight.Bold))
    val vt323 = FontFamily(Font(R.font.vt323, FontWeight.Normal))
    val estarcido = FontFamily(Font(R.font.saira_stencil, FontWeight.Normal))
    val jost = FontFamily(Font(R.font.jost_regular, FontWeight.Normal), Font(R.font.jost_medium, FontWeight.Medium), Font(R.font.jost_semibold, FontWeight.SemiBold))
    val fell = FontFamily(Font(R.font.fell, FontWeight.Normal))
    val bodoniTitulo = FontFamily(Font(R.font.bodoni_titulo, FontWeight.Normal), Font(R.font.bodoni_titulo, FontWeight.SemiBold))
    val bodoni = FontFamily(Font(R.font.bodoni_texto, FontWeight.Normal), Font(R.font.bodoni_titulo, FontWeight.SemiBold))
    val textura = FontFamily(Font(R.font.gotica_textura, FontWeight.Normal))
    val orbita = FontFamily(Font(R.font.orbitron_medium, FontWeight.Normal), Font(R.font.orbitron_medium, FontWeight.Medium), Font(R.font.orbitron_bold, FontWeight.Bold), Font(R.font.orbitron_bold, FontWeight.SemiBold))
    val grotesk = FontFamily(Font(R.font.grotesk_regular, FontWeight.Normal), Font(R.font.grotesk_medium, FontWeight.Medium), Font(R.font.grotesk_medium, FontWeight.SemiBold))
    /** Barlow Condensed: los rótulos estrechos de los instrumentos del batiscafo (títulos del Abismo). */
    val batiscafo = FontFamily(Font(R.font.batiscafo_medium, FontWeight.Normal), Font(R.font.batiscafo_medium, FontWeight.Medium), Font(R.font.batiscafo_semibold, FontWeight.SemiBold), Font(R.font.batiscafo_semibold, FontWeight.Bold))
    /** Atkinson Hyperlegible Next: hecha para leerse con poca luz y poca vista (texto del Abismo). */
    val atkinson = FontFamily(Font(R.font.atkinson_regular, FontWeight.Normal), Font(R.font.atkinson_regular, FontWeight.Medium), Font(R.font.atkinson_semibold, FontWeight.SemiBold), Font(R.font.atkinson_semibold, FontWeight.Bold))
    val kufi = FontFamily(Font(R.font.kufi_medium, FontWeight.Normal), Font(R.font.kufi_medium, FontWeight.Medium), Font(R.font.kufi_bold, FontWeight.Bold), Font(R.font.kufi_bold, FontWeight.SemiBold))
    val amiri = FontFamily(Font(R.font.amiri_regular, FontWeight.Normal))
    val filosofo = FontFamily(Font(R.font.philosopher_regular, FontWeight.Normal), Font(R.font.philosopher_bold, FontWeight.Bold), Font(R.font.philosopher_bold, FontWeight.SemiBold))
    val italianno = FontFamily(Font(R.font.italianno, FontWeight.Normal))
    val cormorant = FontFamily(Font(R.font.cormorant_regular, FontWeight.Normal), Font(R.font.cormorant_medium, FontWeight.Medium), Font(R.font.cormorant_medium, FontWeight.SemiBold))
    /** «Corsario Vestigium» (Pirata One renombrada): la letra negra de las cartas de marear y los avisos de puerto. */
    val corsario = FontFamily(Font(R.font.corsario_titulo, FontWeight.Normal))
    /** Libre Caslon Text: el Caslon (h. 1725) de los libros que se imprimían cuando zarpaban los balandros. */
    val caslon = FontFamily(Font(R.font.caslon_regular, FontWeight.Normal), Font(R.font.caslon_semibold, FontWeight.SemiBold), Font(R.font.caslon_semibold, FontWeight.Bold))
    val fraunces = FontFamily(Font(R.font.fraunces_titulo, FontWeight.Normal), Font(R.font.fraunces_titulo, FontWeight.Medium), Font(R.font.fraunces_negrita, FontWeight.SemiBold), Font(R.font.fraunces_negrita, FontWeight.Bold))
    val cinzelDeco = FontFamily(Font(R.font.cinzel_deco_regular, FontWeight.Normal), Font(R.font.cinzel_deco_bold, FontWeight.Bold))
    val almendra = FontFamily(Font(R.font.almendra_regular, FontWeight.Normal), Font(R.font.almendra_bold, FontWeight.Bold))
    val russo = FontFamily(Font(R.font.russo, FontWeight.Normal))

    // Letras de texto propias de cada tema (lote 5; ver herramientas/fuentes.py).
    /** Courier Prime: la máquina de escribir del refugio. */
    val maquina = FontFamily(Font(R.font.courier_regular, FontWeight.Normal), Font(R.font.courier_bold, FontWeight.Bold), Font(R.font.courier_bold, FontWeight.SemiBold))
    /** Cardo: la letra de los medievalistas. */
    val cardo = FontFamily(Font(R.font.cardo_regular, FontWeight.Normal), Font(R.font.cardo_bold, FontWeight.Bold), Font(R.font.cardo_bold, FontWeight.SemiBold))
    /** «Regla Vestigium» (Gentium Book Plus renombrada): el libro de la regla de la orden, para leer en párrafos. */
    val regla = FontFamily(Font(R.font.regla_regular, FontWeight.Normal), Font(R.font.regla_bold, FontWeight.Bold), Font(R.font.regla_bold, FontWeight.SemiBold))
    /** «Cruzada Vestigium» (MedievalSharp renombrada): la letra de los estandartes y las cartas de la frontera. */
    val cruzada = FontFamily(Font(R.font.cruzada_titulo, FontWeight.Normal), Font(R.font.cruzada_titulo, FontWeight.SemiBold))
    /** Barlow: la grotesca de los rótulos oficiales. */
    val barlow = FontFamily(Font(R.font.barlow_regular, FontWeight.Normal), Font(R.font.barlow_semibold, FontWeight.SemiBold))
    /** Saira: la hermana de texto del estarcido de la orden. */
    val saira = FontFamily(Font(R.font.saira_regular, FontWeight.Normal), Font(R.font.saira_semibold, FontWeight.SemiBold))
    /** Old Standard: la letra de los periódicos del siglo XIX. */
    val gaceta = FontFamily(Font(R.font.oldstandard_regular, FontWeight.Normal), Font(R.font.oldstandard_bold, FontWeight.Bold), Font(R.font.oldstandard_bold, FontWeight.SemiBold))
    /** Crimson Pro: el libro del naturalista. */
    val crimson = FontFamily(Font(R.font.crimson_regular, FontWeight.Normal), Font(R.font.crimson_semibold, FontWeight.SemiBold))
    /** «Letrero Vestigium» (Londrina Solid renombrada): los letreros pintados a mano sobre tablas en la Ciudad verde. */
    val letrero = FontFamily(Font(R.font.letrero_titulo, FontWeight.Normal), Font(R.font.letrero_titulo, FontWeight.Medium), Font(R.font.letrero_titulo, FontWeight.SemiBold), Font(R.font.letrero_titulo, FontWeight.Bold))
    /** Overpass: la letra de las señales de carretera, para el texto de la Ciudad verde. */
    val overpass = FontFamily(Font(R.font.overpass_regular, FontWeight.Normal), Font(R.font.overpass_regular, FontWeight.Medium), Font(R.font.overpass_semibold, FontWeight.SemiBold), Font(R.font.overpass_semibold, FontWeight.Bold))
    /** «Calida Vestigium» (Lora renombrada): cálida, de cuento de Navidad. */
    val calida = FontFamily(Font(R.font.calida_regular, FontWeight.Normal), Font(R.font.calida_semibold, FontWeight.SemiBold))
    /** Alegreya: humanista caligráfica, para las crónicas de la caravana. */
    val alegreya = FontFamily(Font(R.font.alegreya_regular, FontWeight.Normal), Font(R.font.alegreya_semibold, FontWeight.SemiBold))
    /** «Obrera Vestigium» (PT Sans renombrada): hecha para el ruso. */
    val obrera = FontFamily(Font(R.font.obrera_regular, FontWeight.Normal), Font(R.font.obrera_bold, FontWeight.Bold), Font(R.font.obrera_bold, FontWeight.SemiBold))
    /** Fira Sans: sans con cirílico para la estepa. */
    val fira = FontFamily(Font(R.font.fira_regular, FontWeight.Normal), Font(R.font.fira_semibold, FontWeight.SemiBold))
    /** Sorts Mill Goudy: libro antiguo para la capilla. */
    val goudy = FontFamily(Font(R.font.goudy_regular, FontWeight.Normal))
    /** «Ciudadana Vestigium» (Libre Baskerville renombrada): la letra del siglo de la Revolución. */
    val ciudadana = FontFamily(Font(R.font.ciudadana_regular, FontWeight.Normal), Font(R.font.ciudadana_semibold, FontWeight.SemiBold))
    /** Vollkorn: el libro de crónicas de las Tierras Antiguas. */
    val vollkorn = FontFamily(Font(R.font.vollkorn_regular, FontWeight.Normal), Font(R.font.vollkorn_semibold, FontWeight.SemiBold))
    /** «Camino Vestigium» (Uncial Antiqua renombrada): títulos de las Tierras Antiguas. */
    val camino = FontFamily(Font(R.font.camino_titulo, FontWeight.Normal))
    /** Amatic SC: los títulos de la Prehistoria, angostos y a mano alzada, como trazos de carbón en la pared. */
    val trazo = FontFamily(Font(R.font.amatic_regular, FontWeight.Normal), Font(R.font.amatic_regular, FontWeight.Medium), Font(R.font.amatic_bold, FontWeight.SemiBold), Font(R.font.amatic_bold, FontWeight.Bold))
    /** «Abrigo Vestigium» (Andika renombrada, hecha para quien aprende a leer): el texto de la Prehistoria. */
    val abrigo = FontFamily(Font(R.font.abrigo_regular, FontWeight.Normal), Font(R.font.abrigo_regular, FontWeight.Medium), Font(R.font.abrigo_bold, FontWeight.SemiBold), Font(R.font.abrigo_bold, FontWeight.Bold))
    /** «Convencion Vestigium» (Playfair Display renombrada): los carteles de la Convención. */
    val convencion = FontFamily(Font(R.font.convencion_titulo, FontWeight.Normal), Font(R.font.convencion_negrita, FontWeight.Bold), Font(R.font.convencion_negrita, FontWeight.SemiBold))
}

/** Arma un esquema de color a partir de sus piezas. */
internal fun esquema(
    oscuro: Boolean, fondo: Long, superficie: Long, variante: Long, borde: Long, tinta: Long, tenue: Long,
    primario: Long, sobrePrimario: Long, contenedor: Long, sobreContenedor: Long, secundario: Long, contSecundario: Long, terciario: Long, error: Long,
): ColorScheme {
    val c = { x: Long -> Color(x) }
    return if (oscuro) darkColorScheme(
        primary = c(primario), onPrimary = c(sobrePrimario), primaryContainer = c(contenedor), onPrimaryContainer = c(sobreContenedor),
        secondary = c(secundario), onSecondary = c(fondo), secondaryContainer = c(contSecundario), onSecondaryContainer = c(tinta),
        tertiary = c(terciario), background = c(fondo), onBackground = c(tinta), surface = c(superficie), onSurface = c(tinta),
        surfaceVariant = c(variante), onSurfaceVariant = c(tenue), outline = c(borde), outlineVariant = c(borde), error = c(error),
        surfaceContainer = c(superficie), surfaceContainerHigh = c(variante), surfaceContainerHighest = c(variante),
        surfaceContainerLow = c(superficie), surfaceContainerLowest = c(fondo),
    ).conInversos() else lightColorScheme(
        primary = c(primario), onPrimary = c(sobrePrimario), primaryContainer = c(contenedor), onPrimaryContainer = c(sobreContenedor),
        secondary = c(secundario), onSecondary = Color.White, secondaryContainer = c(contSecundario), onSecondaryContainer = c(tinta),
        tertiary = c(terciario), background = c(fondo), onBackground = c(tinta), surface = c(superficie), onSurface = c(tinta),
        surfaceVariant = c(variante), onSurfaceVariant = c(tenue), outline = c(borde), outlineVariant = c(borde), error = c(error),
        surfaceContainer = c(superficie), surfaceContainerHigh = c(variante), surfaceContainerHighest = c(variante),
        surfaceContainerLow = c(superficie), surfaceContainerLowest = c(superficie),
    ).conInversos()
}

/**
 * Los colores «inversos» (la barra de Deshacer): la superficie es la tinta del tema, el texto es su superficie y el botón
 * es el primario del tema, aclarado u oscurecido hasta que se lea sobre esa barra (contraste de al menos 4.5).
 * Sin esto, Material pone su gris y su lila por defecto en todos los temas.
 */
internal fun ColorScheme.conInversos(): ColorScheme {
    val barra = onSurface; val texto = surface
    fun contraste(a: Color, b: Color): Float {
        val (x, y) = a.luminance() to b.luminance()
        return (maxOf(x, y) + 0.05f) / (minOf(x, y) + 0.05f)
    }
    var boton = primary; var t = 0f
    while (contraste(boton, barra) < 4.5f && t < 1f) { t += 0.1f; boton = lerp(primary, texto, t) }
    return copy(inverseSurface = barra, inverseOnSurface = texto, inversePrimary = boton)
}

/**
 * Tipografía a partir de tres familias: títulos, texto y etiquetas (datos).
 * [brillo] agrega un resplandor de pantalla antigua a los títulos (Terminal y refugio de noche).
 */
internal fun tipografia(
    titulo: FontFamily, pesoTitulo: FontWeight, escTitulo: Float, espaciado: Float,
    texto: FontFamily, escTexto: Float, etiqueta: FontFamily, escEtiqueta: Float, cifrasAlineadas: Boolean = false, brillo: Color? = null,
): Typography {
    val sombra = brillo?.let { Shadow(it.copy(alpha = 0.65f), blurRadius = 14f) }
    val rasgos = if (cifrasAlineadas) "lnum, tnum" else null
    fun t(tam: Float, alto: Float, esp: Float, peso: FontWeight = pesoTitulo) =
        TextStyle(fontFamily = titulo, fontWeight = peso, fontSize = (tam * escTitulo).sp, lineHeight = (alto * escTitulo).sp, letterSpacing = esp.sp, shadow = sombra)
    fun b(tam: Float, alto: Float) = TextStyle(fontFamily = texto, fontSize = (tam * escTexto).sp, lineHeight = (alto * escTexto).sp)
    fun l(tam: Float, esp: Float, peso: FontWeight = FontWeight.Medium) =
        TextStyle(fontFamily = etiqueta, fontWeight = peso, fontSize = (tam * escEtiqueta).sp, letterSpacing = esp.sp, fontFeatureSettings = rasgos)
    return Typography(
        displaySmall = t(30f, 36f, espaciado), headlineMedium = t(27f, 33f, espaciado * 1.1f), headlineSmall = t(21f, 27f, espaciado * 0.8f),
        titleLarge = t(16.5f, 22f, espaciado * 0.9f),
        titleMedium = TextStyle(fontFamily = texto, fontWeight = FontWeight.SemiBold, fontSize = (15 * escTexto).sp, lineHeight = (21 * escTexto).sp, fontFeatureSettings = rasgos),
        titleSmall = l(11.5f, 0.2f), bodyLarge = b(16f, 24f), bodyMedium = b(14f, 21f), bodySmall = b(12.5f, 18f),
        labelLarge = l(13f, 0.6f), labelMedium = l(11f, 0.4f), labelSmall = l(10.5f, 0.4f, FontWeight.Normal),
    )
}

// ---------------------------------------------------------------------
//  Medieval: pergamino, tinta ferrogálica, rúbrica roja, lapislázuli y pan de oro.
// ---------------------------------------------------------------------
internal val medievalClaro = esquema(false, 0xFFEFE3C8, 0xFFF6ECD6, 0xFFE5D6B4, 0xFFCDB98E, 0xFF2A1B10, 0xFF5E4A35,
    0xFF9E1B1B, 0xFFFFF6E6, 0xFFF2D5C4, 0xFF2A1B10, 0xFF1F3F8F, 0xFFD5DDF0, 0xFFA97C1E, 0xFF8B1A1A)
internal val medievalOscuro = esquema(true, 0xFF15100A, 0xFF1E1710, 0xFF2A2016, 0xFF3F3122, 0xFFEDDFC2, 0xFFB7A587,
    0xFFD5A84A, 0xFF1C1405, 0xFF3A1717, 0xFFF3E4C8, 0xFF8DA5E0, 0xFF1B2645, 0xFFC9473B, 0xFFE58C86)
internal val tipoMedieval = tipografia(LetraExtra.gotica, FontWeight.Normal, 1.18f, 0.6f, LetraExtra.cardo, 1.06f, LetraExtra.cardo, 1.08f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Templario: sobreveste de lino blanco, cruz roja, cota de malla de hierro y arena de la frontera; de noche, la vigilia
//  en la muralla: piedra oscura, la cruz a la luz de la antorcha y la arena bajo la luna.
// ---------------------------------------------------------------------
internal val templarioClaro = esquema(false, 0xFFECE2CC, 0xFFFBF8F0, 0xFFDFD2B5, 0xFFBDA984, 0xFF201E1B, 0xFF5B574E,
    0xFFB0191F, 0xFFFFF8F2, 0xFFF4D2CC, 0xFF2A0F0D, 0xFF4A5560, 0xFFD8DDE1, 0xFFA2763A, 0xFF7E1610)
internal val templarioOscuro = esquema(true, 0xFF12110E, 0xFF1B1915, 0xFF262219, 0xFF3B352A, 0xFFEEE7D8, 0xFFB1A891,
    0xFFE2453B, 0xFF1F0604, 0xFF461714, 0xFFF7E5DE, 0xFFA8B3BC, 0xFF26303A, 0xFFDAAE68, 0xFFF29C90)
internal val tipoTemplario = tipografia(LetraExtra.cruzada, FontWeight.Normal, 1.12f, 0.9f, LetraExtra.regla, 1.0f, LetraExtra.regla, 1.02f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Distopía: papel de periódico gris, tinta negra y rojo de cartel.
// ---------------------------------------------------------------------
internal val distopiaClaro = esquema(false, 0xFFDEDBD2, 0xFFECE9E1, 0xFFCFCBC0, 0xFFA7A296, 0xFF141414, 0xFF474744,
    0xFFB3121B, 0xFFFFFFFF, 0xFFF0D2D0, 0xFF141414, 0xFF2E3B44, 0xFFC9D1D6, 0xFF5E5E33, 0xFF8A0E14)
internal val distopiaOscuro = esquema(true, 0xFF0F1011, 0xFF17191A, 0xFF222527, 0xFF34383B, 0xFFE8E6E0, 0xFFA8A8A1,
    0xFFE0262F, 0xFFFFFFFF, 0xFF3A1012, 0xFFF2E0DF, 0xFF9FB0BA, 0xFF24313A, 0xFFB7B37A, 0xFFFF6B6B)
internal val tipoDistopia = tipografia(LetraExtra.oswald, FontWeight.Bold, 1.05f, 1.4f, LetraExtra.barlow, 1.02f, LetraExtra.barlow, 1.04f)

// ---------------------------------------------------------------------
//  Refugio: optimismo atómico de los cincuenta de día; pantalla de fósforo verde de noche.
// ---------------------------------------------------------------------
internal val refugioClaro = esquema(false, 0xFFF1E8D0, 0xFFF8F1DE, 0xFFE6DAB8, 0xFFCDBE95, 0xFF1F2A2A, 0xFF4C5752,
    0xFF1C6E6A, 0xFFFFFFFF, 0xFFCFE6E1, 0xFF1F2A2A, 0xFFD9572B, 0xFFF6D6C6, 0xFFB98A15, 0xFFA3312F)
internal val refugioOscuro = esquema(true, 0xFF070D08, 0xFF0B140D, 0xFF112016, 0xFF1C3A24, 0xFF9CF5B0, 0xFF55B872,
    0xFF3DFF7F, 0xFF021407, 0xFF0F2D18, 0xFFB8FFC9, 0xFFFFB23F, 0xFF2C2008, 0xFF7CFFD0, 0xFFFF6B5B)
internal val tipoRefugioClaro = tipografia(LetraExtra.vt323, FontWeight.Normal, 1.4f, 1f, LetraExtra.maquina, 0.98f, LetraExtra.maquina, 1f)
internal val tipoRefugioOscuro = tipografia(LetraExtra.vt323, FontWeight.Normal, 1.4f, 1f, LetraExtra.maquina, 0.98f, LetraExtra.maquina, 1f, brillo = Color(0xFF3DFF7F))

// ---------------------------------------------------------------------
//  Orden: acero cepillado, carmesí y latón; letra de estarcido militar.
// ---------------------------------------------------------------------
internal val ordenClaro = esquema(false, 0xFFE2E5E8, 0xFFEEF0F2, 0xFFD3D7DC, 0xFFAEB4BB, 0xFF15181B, 0xFF4B525A,
    0xFF9E1B22, 0xFFFFFFFF, 0xFFEED3D4, 0xFF15181B, 0xFF7E6219, 0xFFEEE1BF, 0xFF2F4858, 0xFF8A0E14)
internal val ordenOscuro = esquema(true, 0xFF101215, 0xFF171A1E, 0xFF22262B, 0xFF3A4048, 0xFFDDE2E7, 0xFF9DA6B0,
    0xFFD0333B, 0xFFFFFFFF, 0xFF3A1416, 0xFFF5DADB, 0xFFD6B25A, 0xFF33290F, 0xFF8FB3C8, 0xFFFF7A7A)
internal val tipoOrden = tipografia(LetraExtra.estarcido, FontWeight.Normal, 1.05f, 1.6f, LetraExtra.saira, 0.98f, LetraExtra.saira, 1f)

// ---------------------------------------------------------------------
//  Laboratorio: blanco clínico, cian y violeta; geometría limpia.
// ---------------------------------------------------------------------
internal val laboratorioClaro = esquema(false, 0xFFF4F7F9, 0xFFFFFFFF, 0xFFE6EDF2, 0xFFCBD6DF, 0xFF12202B, 0xFF4D5D6B,
    0xFF0B8FA1, 0xFFFFFFFF, 0xFFD2EEF2, 0xFF12202B, 0xFF5E4FD1, 0xFFE3E0FA, 0xFF1E8E5A, 0xFFC03A3A)
internal val laboratorioOscuro = esquema(true, 0xFF0A1018, 0xFF101823, 0xFF18222F, 0xFF263447, 0xFFE3ECF3, 0xFF97A8B9,
    0xFF3CD2E5, 0xFF00191D, 0xFF0C3640, 0xFFCFF6FB, 0xFF9D93FF, 0xFF241F4D, 0xFF5FD49C, 0xFFFF7A7A)
internal val tipoLaboratorio = tipografia(LetraExtra.jost, FontWeight.Medium, 1f, 2.2f, LetraExtra.jost, 1.04f, LetraExtra.jost, 1.08f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Colonos: papel crema de gaceta, azul marino, rojo ladrillo y madera; de noche, faroles.
// ---------------------------------------------------------------------
internal val colonosClaro = esquema(false, 0xFFF2EBDC, 0xFFF8F3E8, 0xFFE7DDC8, 0xFFCDBFA2, 0xFF1F2A3A, 0xFF525866,
    0xFF1F3A5F, 0xFFFFFFFF, 0xFFD4DEEC, 0xFF1F2A3A, 0xFFA23B2A, 0xFFF2D8D1, 0xFF7A5A2E, 0xFF8A1A1A)
internal val colonosOscuro = esquema(true, 0xFF0E1420, 0xFF151D2B, 0xFF1E2838, 0xFF2E3B50, 0xFFEEE7D7, 0xFFA9B0BC,
    0xFFE3C27A, 0xFF1C1405, 0xFF2B3A55, 0xFFEEE7D7, 0xFFE07A62, 0xFF3A1E18, 0xFFB89463, 0xFFFF8A80)
internal val tipoColonos = tipografia(LetraExtra.fell, FontWeight.Normal, 1.12f, 0.8f, LetraExtra.gaceta, 1.08f, LetraExtra.gaceta, 1.12f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Napoleónico: azul imperial, oro y carmesí sobre papel de despacho; de noche, terciopelo azul.
// ---------------------------------------------------------------------
internal val napoleonClaro = esquema(false, 0xFFF4EEDF, 0xFFFBF7EC, 0xFFEAE1CB, 0xFFCDBF9C, 0xFF14213D, 0xFF4F5874,
    0xFF1E3A8A, 0xFFFFFFFF, 0xFFDCE3F5, 0xFF14213D, 0xFFA3172E, 0xFFF3D6DA, 0xFFB8892B, 0xFF8A1A1A)
internal val napoleonOscuro = esquema(true, 0xFF0B1230, 0xFF121A3D, 0xFF1B2550, 0xFF2C3970, 0xFFF1E9D2, 0xFFB3AE9C,
    0xFFD9B25B, 0xFF1A1405, 0xFF263266, 0xFFF1E9D2, 0xFFE05A6A, 0xFF3A1420, 0xFF9DB4FF, 0xFFFF8A80)
internal val tipoNapoleon = tipografia(LetraExtra.bodoniTitulo, FontWeight.Normal, 1.06f, 1.7f, LetraExtra.bodoni, 1.06f, LetraExtra.bodoni, 1.08f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Botánico: el cuaderno de un naturalista viajero. Papel envejecido, tinta sepia, verde musgo, óxido de hormiga
//  y el verde metálico de los escarabajos; de noche, el gabinete de especímenes con luciérnagas.
// ---------------------------------------------------------------------
internal val botanicoClaro = esquema(false, 0xFFEFE8D6, 0xFFF7F2E4, 0xFFE3D9C0, 0xFFBFAF8A, 0xFF2A2318, 0xFF5E5340,
    0xFF3F5A2C, 0xFFFFFFFF, 0xFFDCE3C8, 0xFF1E2A14, 0xFF8C4A22, 0xFFF0D9C4, 0xFF2E5E5A, 0xFF8A1A1A)
internal val botanicoOscuro = esquema(true, 0xFF14120D, 0xFF1C1912, 0xFF2A251B, 0xFF4A4231, 0xFFEDE4CF, 0xFFB8AC90,
    0xFFA9C27A, 0xFF16200A, 0xFF33421F, 0xFFEDE4CF, 0xFFE09A5E, 0xFF4A2A14, 0xFF6FC2B0, 0xFFFF8A80)
internal val tipoBotanico = tipografia(LetraExtra.fraunces, FontWeight.Medium, 1.04f, 0.2f, LetraExtra.crimson, 1.12f, LetraExtra.crimson, 1.14f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Ciudad verde: una ciudad que el bosque recuperó. De día, hormigón gris claro, hiedra, óxido y el ámbar del semáforo
//  muerto; de noche, asfalto mojado, musgo, óxido encendido y luciérnagas entre los edificios.
// ---------------------------------------------------------------------
internal val ruinasClaro = esquema(false, 0xFFE4E3DC, 0xFFF0EFE9, 0xFFD6D6CC, 0xFFA9AA9C, 0xFF1F2420, 0xFF545B52,
    0xFF3E6B2A, 0xFFFFFFFF, 0xFFD3E4C2, 0xFF15240E, 0xFFA34A1E, 0xFFF2D5C2, 0xFFB57A0E, 0xFFB3261E)
internal val ruinasOscuro = esquema(true, 0xFF0F1411, 0xFF161C18, 0xFF212923, 0xFF3A443B, 0xFFE3E6DC, 0xFFA2AB9E,
    0xFF8FCB6A, 0xFF0E1A08, 0xFF26391C, 0xFFE3E6DC, 0xFFE08A55, 0xFF452414, 0xFFE9C25A, 0xFFFF8A7A)
internal val tipoRuinas = tipografia(LetraExtra.letrero, FontWeight.Normal, 1.12f, 0.5f, LetraExtra.overpass, 1.0f, LetraExtra.overpass, 1.02f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Navidad: nieve y crema, rojo, verde pino y oro; de noche, el bosque nevado.
// ---------------------------------------------------------------------
internal val navidadClaro = esquema(false, 0xFFF6F1E7, 0xFFFFFFFF, 0xFFECE4D6, 0xFFD8CBB2, 0xFF1E2A22, 0xFF56635A,
    0xFFB71C1C, 0xFFFFFFFF, 0xFFF6D5D3, 0xFF1E2A22, 0xFF1B5E20, 0xFFD5EBD6, 0xFFC9A227, 0xFF8A1A1A)
internal val navidadOscuro = esquema(true, 0xFF0E1A14, 0xFF14241B, 0xFF1C3125, 0xFF2E4A39, 0xFFF3EFE6, 0xFFB5C2B8,
    0xFFE5484D, 0xFFFFFFFF, 0xFF4A1416, 0xFFF3EFE6, 0xFF66BB6A, 0xFF1D3A22, 0xFFF2C94C, 0xFFFF8A80)
internal val tipoNavidad = tipografia(LetraExtra.cinzelDeco, FontWeight.Normal, 1.02f, 1.0f, LetraExtra.calida, 1.0f, LetraExtra.calida, 1.02f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Ruta de la Seda: seda cruda, lapislázuli, cinabrio y turquesa; de noche, el desierto bajo las estrellas.
// ---------------------------------------------------------------------
internal val sedaClaro = esquema(false, 0xFFF5EEDC, 0xFFFBF6E9, 0xFFEADFC3, 0xFFD4C29A, 0xFF1F1B3A, 0xFF59546F,
    0xFF23408E, 0xFFFFFFFF, 0xFFD9E0F4, 0xFF1F1B3A, 0xFFC0392B, 0xFFF5D5CF, 0xFF1A8C8C, 0xFF8A1A1A)
internal val sedaOscuro = esquema(true, 0xFF0F1426, 0xFF161D35, 0xFF1F2846, 0xFF33406A, 0xFFF2EAD3, 0xFFB9B29F,
    0xFFE0B04F, 0xFF1A1405, 0xFF2B3A6E, 0xFFF2EAD3, 0xFFE26A50, 0xFF3D1E18, 0xFF4FC3C3, 0xFFFF8A80)
internal val tipoSeda = tipografia(LetraExtra.almendra, FontWeight.Normal, 1.12f, 0.4f, LetraExtra.alegreya, 1.06f, LetraExtra.alegreya, 1.08f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Soviético: papel de cartel, rojo y negro en diagonales constructivistas.
// ---------------------------------------------------------------------
internal val urssClaro = esquema(false, 0xFFEFE6D2, 0xFFF7F0DE, 0xFFE3D6B8, 0xFFC9B994, 0xFF161414, 0xFF4A4440,
    0xFFC1121F, 0xFFFFFFFF, 0xFFF3D0D0, 0xFF161414, 0xFF1D1D1B, 0xFFDDD3BC, 0xFFD4A017, 0xFF8A0E14)
internal val urssOscuro = esquema(true, 0xFF121010, 0xFF1B1717, 0xFF262020, 0xFF3D3333, 0xFFF1E7D2, 0xFFB7AA95,
    0xFFE5252F, 0xFFFFFFFF, 0xFF4A1013, 0xFFF1E7D2, 0xFFE8D9B5, 0xFF3A3328, 0xFFE8B923, 0xFFFF6B6B)
internal val tipoUrss = tipografia(LetraExtra.russo, FontWeight.Normal, 1.0f, 1.0f, LetraExtra.obrera, 1.0f, LetraExtra.obrera, 1.02f)

// ---------------------------------------------------------------------
//  Gótico: una capilla de vitrales. De día, piedra caliza con cristal zafiro y rubí; de noche, la bóveda azul con estrellas de oro.
// ---------------------------------------------------------------------
internal val goticoClaro = esquema(false, 0xFFEDE7DA, 0xFFF7F3EA, 0xFFE2D9C6, 0xFFC9BC9F, 0xFF141A33, 0xFF4F5570,
    0xFF1F3A9E, 0xFFFFFFFF, 0xFFDCE2F6, 0xFF141A33, 0xFFB3122E, 0xFFF4D3D8, 0xFFB8892B, 0xFF8A1A1A)
internal val goticoOscuro = esquema(true, 0xFF0A0F2A, 0xFF111842, 0xFF1A2356, 0xFF2E3A7A, 0xFFF0E9D8, 0xFFB0AFC8,
    0xFFE2B34A, 0xFF1A1405, 0xFF26306E, 0xFFF0E9D8, 0xFFE0405E, 0xFF3A1222, 0xFF5C8DFF, 0xFFFF8A80)
internal val tipoGotico = tipografia(LetraExtra.textura, FontWeight.Normal, 1.16f, 0.3f, LetraExtra.goudy, 1.08f, LetraExtra.goudy, 1.1f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Horror cósmico: el archivo de un pueblo costero. De día, papel de legajo amarillento con manchas, tinta verde-negra,
//  sepia y lacre oscuro; de noche, la niebla de la costa, la luz pálida del faro y el verde del agua que brilla.
// ---------------------------------------------------------------------
internal val arcanoClaro = esquema(false, 0xFFE3D9C1, 0xFFEEE6D2, 0xFFD8CCAF, 0xFFB3A683, 0xFF18231D, 0xFF4C564C,
    0xFF2D5446, 0xFFF1EADA, 0xFFCADBCF, 0xFF0F1D17, 0xFF6A4B26, 0xFFE6D2B2, 0xFF7A2C24, 0xFF8F2B1F)
internal val arcanoOscuro = esquema(true, 0xFF0A1210, 0xFF101B18, 0xFF182622, 0xFF2B3D37, 0xFFCDD6C7, 0xFF8C9E93,
    0xFFE6D58C, 0xFF1A1806, 0xFF2C3524, 0xFFCDD6C7, 0xFF7DBBA4, 0xFF173229, 0xFFC98472, 0xFFFF8A80)
internal val tipoArcano = tipografia(Letra.spectral, FontWeight.Medium, 1.06f, 0.4f, Letra.castoro, 1.06f, Letra.castoro, 1.06f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Espacio: de día, la sala de control (blanco, azul marino y naranja); de noche, estrellas y nebulosas.
// ---------------------------------------------------------------------
internal val espacioClaro = esquema(false, 0xFFEEF1F6, 0xFFFFFFFF, 0xFFE1E6EF, 0xFFC3CCDA, 0xFF0D1526, 0xFF4E5A70,
    0xFF1B3A8C, 0xFFFFFFFF, 0xFFD7E0F5, 0xFF0D1526, 0xFFE8601C, 0xFFFBDCCB, 0xFF6B4FD6, 0xFFB3261E)
internal val espacioOscuro = esquema(true, 0xFF03050C, 0xFF0A0F1E, 0xFF121A31, 0xFF26324F, 0xFFE8ECF8, 0xFF8E9AB8,
    0xFF8AB4FF, 0xFF020818, 0xFF18264D, 0xFFE8ECF8, 0xFFFFB86B, 0xFF3A2410, 0xFFC59BFF, 0xFFFF8A80)
internal val tipoEspacio = tipografia(LetraExtra.orbita, FontWeight.Bold, 0.96f, 2.0f, LetraExtra.grotesk, 1.02f, LetraExtra.grotesk, 1.02f)
internal val tipoEspacioOscuro = tipografia(LetraExtra.orbita, FontWeight.Bold, 0.96f, 2.0f, LetraExtra.grotesk, 1.02f, LetraExtra.grotesk, 1.02f, brillo = Color(0xFF8AB4FF))

// ---------------------------------------------------------------------
//  Abismo: de día, la zona iluminada (azul de agua con sol, espuma, el latón del casco de buzo); de noche, la zona
//  abisal, casi negra, donde la única luz es la de los animales (verde agua, azul de luciferina y el violeta de las medusas).
// ---------------------------------------------------------------------
internal val abismoClaro = esquema(false, 0xFFE6F2F6, 0xFFF4FAFC, 0xFFD3E7EF, 0xFFA6C8D5, 0xFF05263A, 0xFF41657A,
    0xFF0A5C8C, 0xFFFFFFFF, 0xFFCBE6F3, 0xFF05263A, 0xFF8C6A1F, 0xFFF0E4C2, 0xFF0F8A86, 0xFFA8322A)
internal val abismoOscuro = esquema(true, 0xFF01060B, 0xFF050F18, 0xFF0A1A26, 0xFF163347, 0xFFD6F0F2, 0xFF7DA2AF,
    0xFF5CF2C6, 0xFF00201A, 0xFF0A3A32, 0xFFD6F0F2, 0xFFE29BFF, 0xFF2D1640, 0xFF3FA9FF, 0xFFFF8A80)
internal val tipoAbismo = tipografia(LetraExtra.batiscafo, FontWeight.SemiBold, 1.1f, 1.1f, LetraExtra.atkinson, 1.0f, LetraExtra.batiscafo, 1.1f, cifrasAlineadas = true)
internal val tipoAbismoOscuro = tipografia(LetraExtra.batiscafo, FontWeight.SemiBold, 1.1f, 1.1f, LetraExtra.atkinson, 1.0f, LetraExtra.batiscafo, 1.1f, cifrasAlineadas = true, brillo = Color(0xFF5CF2C6))

// ---------------------------------------------------------------------
//  Al-Ándalus: yeso tallado, azulejo verde y almagre; de noche, el jardín del Generalife con oro y turquesa.
// ---------------------------------------------------------------------
internal val andalusClaro = esquema(false, 0xFFF3EBDA, 0xFFFBF6EA, 0xFFE9DEC6, 0xFFD2C29E, 0xFF2A1A12, 0xFF6A5640,
    0xFF1E6B5C, 0xFFFFFFFF, 0xFFD3EBE4, 0xFF2A1A12, 0xFFB5432A, 0xFFF4D9CE, 0xFF2A4F9E, 0xFF8A1A1A)
internal val andalusOscuro = esquema(true, 0xFF0B1716, 0xFF112220, 0xFF18302D, 0xFF2B4A45, 0xFFF3EAD6, 0xFFB4AE9B,
    0xFFE0B65A, 0xFF1A1405, 0xFF1E3D38, 0xFFF3EAD6, 0xFF4FC1A6, 0xFF123A33, 0xFFE07A5F, 0xFFFF8A80)
internal val tipoAndalus = tipografia(LetraExtra.kufi, FontWeight.Medium, 1.06f, 0.5f, LetraExtra.amiri, 1.08f, LetraExtra.amiri, 1.1f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Mongol: el Cielo Azul Eterno, fieltro blanco, rojo y oro; de noche, la estepa bajo las estrellas.
// ---------------------------------------------------------------------
internal val mongolClaro = esquema(false, 0xFFF1EDE3, 0xFFFAF7F0, 0xFFE6DFCF, 0xFFCCC0A6, 0xFF18202E, 0xFF555D6D,
    0xFF1F5FA8, 0xFFFFFFFF, 0xFFD5E4F5, 0xFF18202E, 0xFFC2412D, 0xFFF5D6CF, 0xFFC48A1E, 0xFF8A1A1A)
internal val mongolOscuro = esquema(true, 0xFF0A1220, 0xFF101B2E, 0xFF18263F, 0xFF2B3E5E, 0xFFF0EBDD, 0xFFAAB0BD,
    0xFF6FB1FF, 0xFF041427, 0xFF1B3456, 0xFFF0EBDD, 0xFFFF7A59, 0xFF3E1E16, 0xFFF2C14E, 0xFFFF8A80)
internal val tipoMongol = tipografia(LetraExtra.filosofo, FontWeight.Bold, 1.04f, 1.2f, LetraExtra.fira, 0.98f, LetraExtra.filosofo, 1.06f)

// ---------------------------------------------------------------------
//  Renacimiento: los cuadernos de Leonardo. Papel envejecido, tinta sepia y sanguina; de noche, nogal y pergamino.
// ---------------------------------------------------------------------
internal val renacimientoClaro = esquema(false, 0xFFF0E4C6, 0xFFF7EDD4, 0xFFE6D6B0, 0xFFCBB687, 0xFF3A2614, 0xFF6E5638,
    0xFF8C3B1E, 0xFFFFFFFF, 0xFFF2D6C6, 0xFF3A2614, 0xFF2F4F6F, 0xFFD6E0EA, 0xFF9C7A2E, 0xFF8A1A1A)
internal val renacimientoOscuro = esquema(true, 0xFF1A130C, 0xFF241A11, 0xFF302318, 0xFF4A3826, 0xFFEFE0BE, 0xFFBCA884,
    0xFFE08A5E, 0xFF2A1006, 0xFF4A2A18, 0xFFEFE0BE, 0xFF9EC0E0, 0xFF22303E, 0xFFD9B566, 0xFFFF8A80)
internal val tipoRenacimiento = tipografia(LetraExtra.italianno, FontWeight.Normal, 1.78f, 0f, LetraExtra.cormorant, 1.2f, LetraExtra.cormorant, 1.22f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Pirata: la carta náutica y la bitácora. De día, sepia sobre pergamino húmedo, el verde del mar de las cartas, latón
//  y el rojo de la bandera; de noche, brea y lona a la luz del farol, con el latón encendido.
// ---------------------------------------------------------------------
internal val pirataClaro = esquema(false, 0xFFE6DCBF, 0xFFF0E8D0, 0xFFD9CBA6, 0xFFAD9A70, 0xFF2A1C0F, 0xFF66553C,
    0xFF1E5157, 0xFFFFFFFF, 0xFFCFE2DC, 0xFF0E2A2D, 0xFF85621A, 0xFFEEDDB4, 0xFF9A2A1C, 0xFF8A1A1A)
internal val pirataOscuro = esquema(true, 0xFF100E0B, 0xFF1A1712, 0xFF25201A, 0xFF41382A, 0xFFE9DEC3, 0xFFAFA386,
    0xFFD6AA4C, 0xFF201604, 0xFF3D3015, 0xFFE9DEC3, 0xFF7DBFB6, 0xFF1A3432, 0xFFE47B4E, 0xFFFF8A80)
internal val tipoPirata = tipografia(LetraExtra.corsario, FontWeight.Normal, 1.14f, 0.4f, LetraExtra.caslon, 0.98f, LetraExtra.caslon, 1.0f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Revolución francesa: papel de cartel crudo, tinta de imprenta, el azul y el rojo de la escarapela y el verde del
//  árbol de la libertad; de noche, la plaza a la luz de las antorchas.
// ---------------------------------------------------------------------
internal val revolucionClaro = esquema(false, 0xFFF2ECDD, 0xFFFAF6EC, 0xFFE6DDC8, 0xFFC8BB9C, 0xFF1C1A17, 0xFF57524A,
    0xFFB22234, 0xFFFFFFFF, 0xFFF4D6D8, 0xFF1C1A17, 0xFF1F3A6E, 0xFFD8E0EF, 0xFF3F6B3A, 0xFF8A1A1A)
internal val revolucionOscuro = esquema(true, 0xFF131417, 0xFF1B1D22, 0xFF25282F, 0xFF3A3F4A, 0xFFF0EADB, 0xFFB2AC9E,
    0xFFE5525E, 0xFF1A0508, 0xFF4A161C, 0xFFF0EADB, 0xFF8FAEE8, 0xFF1E2A44, 0xFF8CC08A, 0xFFFF8A80)
internal val tipoRevolucion = tipografia(LetraExtra.convencion, FontWeight.Normal, 1.04f, 1.4f, LetraExtra.ciudadana, 0.96f, LetraExtra.ciudadana, 0.98f, cifrasAlineadas = true)

/** Tierras Antiguas: piedra gris, verde de bosque viejo, oro gastado y rubí; títulos en uncial, texto y rótulos en Vollkorn. */
internal val antiguasClaro = esquema(false, 0xFFE9E3D4, 0xFFF4F0E4, 0xFFDCD5C3, 0xFFB9B09A, 0xFF1E1D1A, 0xFF57534A,
    0xFF2E5A3E, 0xFFFFFFFF, 0xFFD3E2D2, 0xFF13241A, 0xFF9A7B2E, 0xFFEFE2BC, 0xFF8E1B24, 0xFF8A1A1A)
internal val antiguasOscuro = esquema(true, 0xFF151618, 0xFF1F2023, 0xFF2A2B2F, 0xFF45464C, 0xFFEDE7D8, 0xFFB0AA9C,
    0xFF8CC79A, 0xFF0D1F14, 0xFF1F3A28, 0xFFEDE7D8, 0xFFD9BC6A, 0xFF3A3018, 0xFFE06A72, 0xFFFF8A80)
internal val tipoAntiguas = tipografia(LetraExtra.camino, FontWeight.Normal, 1.0f, 1.2f, LetraExtra.vollkorn, 1.02f, LetraExtra.vollkorn, 1.04f, cifrasAlineadas = true)

/**
 * Prehistoria: de día, la caliza de la boca de la cueva con ocre rojo (hematita), ocre amarillo y carbón; de noche, lo hondo
 * de la cueva a la luz de una lámpara de grasa: la roca casi negra, el ocre encendido y la llama amarilla. Títulos en Amatic SC
 * (trazo de carbón) y texto en Abrigo (Andika).
 */
internal val cuevaClaro = esquema(false, 0xFFEADCC6, 0xFFF5EEE1, 0xFFE0D0B6, 0xFFC1AB8C, 0xFF2A211B, 0xFF66574A,
    0xFFA2401F, 0xFFFFFFFF, 0xFFF2D2C0, 0xFF3A160A, 0xFF4D4037, 0xFFE2D7C6, 0xFFB07418, 0xFF9B1C1C)
internal val cuevaOscuro = esquema(true, 0xFF16100C, 0xFF211913, 0xFF2D2219, 0xFF4B3A2B, 0xFFEEDFC8, 0xFFB5A08A,
    0xFFE8804F, 0xFF2A0E04, 0xFF4D2616, 0xFFF6DCCB, 0xFFD9AE62, 0xFF3D2D14, 0xFFF4C76E, 0xFFFF8A7A)
internal val tipoCueva = tipografia(LetraExtra.trazo, FontWeight.Bold, 1.32f, 0.6f, LetraExtra.abrigo, 1.0f, LetraExtra.abrigo, 1.0f, cifrasAlineadas = true)

/** Juegos de color para gráficas: bajo, medio, alto y el color fuerte (secuencial). */
internal fun juegoTonos(tema: TemaId, oscuro: Boolean): List<Color> = when (tema) {
    TemaId.ROMANO -> if (oscuro) listOf(0xFFA63A4D, 0xFF2A2F34, 0xFF3FA393, 0xFFD2AE5A) else listOf(0xFF9E3445, 0xFFE4E2DA, 0xFF2D7A6F, 0xFFB8892B)
    // Del rojo pompeyano de las fachadas al verde de las persianas, pasando por el travertino; fuerte, la ocra.
    TemaId.URBE -> if (oscuro) listOf(0xFFE07A52, 0xFF2B2420, 0xFF8DB596, 0xFFE3C07A) else listOf(0xFFA8432A, 0xFFE6D9BF, 0xFF3D5E45, 0xFFB98322)
    TemaId.CIBER -> listOf(0xFF2C6F7A, 0xFF0B191C, 0xFF00E5FF, 0xFFC8FBFF)
    TemaId.NEON -> listOf(0xFFFFB238, 0xFF221729, 0xFFFF3D9A, 0xFF8CFF5A)
    TemaId.HELENO -> if (oscuro) listOf(0xFFB04A6A, 0xFF2A2440, 0xFF5FA8D8, 0xFFD8B45C) else listOf(0xFF8E2C48, 0xFFE9E3D6, 0xFF1F6E9C, 0xFFA27B22)
    // Lo malo en cornalina, lo neutro en papiro y lo bueno en el verde de la fayenza (el verde «wadj» del papiro vivo).
    TemaId.EGIPTO -> if (oscuro) listOf(0xFFD9653E, 0xFF2C2218, 0xFF4FB8A6, 0xFFE3B341) else listOf(0xFFB0441E, 0xFFE3D3A2, 0xFF1F7A6C, 0xFF9A7212)
    TemaId.MEDIEVAL -> if (oscuro) listOf(0xFFC9473B, 0xFF2A2016, 0xFF8DA5E0, 0xFFD5A84A) else listOf(0xFF9E1B1B, 0xFFE5D6B4, 0xFF1F3F8F, 0xFFA97C1E)
    TemaId.TEMPLARIO -> if (oscuro) listOf(0xFFD0453A, 0xFF262219, 0xFF9AA8B4, 0xFFDAAE68) else listOf(0xFFA8181E, 0xFFE2D6BC, 0xFF46525D, 0xFFA2763A)
    TemaId.DISTOPIA -> if (oscuro) listOf(0xFFE0262F, 0xFF2A2C2E, 0xFFCFCFC8, 0xFFE0262F) else listOf(0xFFB3121B, 0xFFCFCBC0, 0xFF2E3B44, 0xFFB3121B)
    TemaId.REFUGIO -> if (oscuro) listOf(0xFFFFB23F, 0xFF112016, 0xFF3DFF7F, 0xFF3DFF7F) else listOf(0xFFD9572B, 0xFFE6DAB8, 0xFF1C6E6A, 0xFF1C6E6A)
    TemaId.ORDEN -> if (oscuro) listOf(0xFFD0333B, 0xFF262A30, 0xFF8FB3C8, 0xFFD6B25A) else listOf(0xFF9E1B22, 0xFFD3D7DC, 0xFF2F4858, 0xFF7E6219)
    TemaId.LABORATORIO -> if (oscuro) listOf(0xFFFF7A9A, 0xFF18222F, 0xFF3CD2E5, 0xFF3CD2E5) else listOf(0xFFC0466A, 0xFFE6EDF2, 0xFF0B8FA1, 0xFF0B8FA1)
    TemaId.COLONOS -> if (oscuro) listOf(0xFFE07A62, 0xFF1E2838, 0xFF8FB3E0, 0xFFE3C27A) else listOf(0xFFA23B2A, 0xFFE7DDC8, 0xFF1F3A5F, 0xFF7A5A2E)
    TemaId.NAPOLEON -> if (oscuro) listOf(0xFFE05A6A, 0xFF1B2550, 0xFF9DB4FF, 0xFFD9B25B) else listOf(0xFFA3172E, 0xFFEAE1CB, 0xFF1E3A8A, 0xFFB8892B)
    TemaId.BOTANICO -> if (oscuro) listOf(0xFFE09A5E, 0xFF2A251B, 0xFFA9C27A, 0xFF6FC2B0) else listOf(0xFF8C4A22, 0xFFE3D9C0, 0xFF3F5A2C, 0xFF2E5E5A)
    TemaId.RUINAS -> if (oscuro) listOf(0xFFE08A55, 0xFF212923, 0xFF8FCB6A, 0xFFE9C25A) else listOf(0xFFA34A1E, 0xFFD6D6CC, 0xFF3E6B2A, 0xFFB57A0E)
    TemaId.NAVIDAD -> if (oscuro) listOf(0xFFE5484D, 0xFF1C3125, 0xFF66BB6A, 0xFFF2C94C) else listOf(0xFFB71C1C, 0xFFECE4D6, 0xFF1B5E20, 0xFFC9A227)
    TemaId.SEDA -> if (oscuro) listOf(0xFFE26A50, 0xFF1F2846, 0xFF4FC3C3, 0xFFE0B04F) else listOf(0xFFC0392B, 0xFFEADFC3, 0xFF23408E, 0xFF1A8C8C)
    TemaId.URSS -> if (oscuro) listOf(0xFFE5252F, 0xFF262020, 0xFFE8D9B5, 0xFFE5252F) else listOf(0xFFC1121F, 0xFFE3D6B8, 0xFF1D1D1B, 0xFFC1121F)
    TemaId.GOTICO -> if (oscuro) listOf(0xFFE0405E, 0xFF1A2356, 0xFF5C8DFF, 0xFFE2B34A) else listOf(0xFFB3122E, 0xFFE2D9C6, 0xFF1F3A9E, 0xFFB8892B)
    TemaId.ARCANO -> if (oscuro) listOf(0xFFC98472, 0xFF182622, 0xFF7DBBA4, 0xFFE6D58C) else listOf(0xFF7A2C24, 0xFFD8CCAF, 0xFF2D5446, 0xFF6A4B26)
    TemaId.ESPACIO -> if (oscuro) listOf(0xFFFF8A65, 0xFF121A31, 0xFF8AB4FF, 0xFFC59BFF) else listOf(0xFFE8601C, 0xFFE1E6EF, 0xFF1B3A8C, 0xFF6B4FD6)
    TemaId.ABISMO -> if (oscuro) listOf(0xFFE29BFF, 0xFF0A1A26, 0xFF5CF2C6, 0xFF3FA9FF) else listOf(0xFFC0563F, 0xFFD3E7EF, 0xFF0A5C8C, 0xFF0F8A86)
    TemaId.ANDALUS -> if (oscuro) listOf(0xFFE07A5F, 0xFF18302D, 0xFF4FC1A6, 0xFFE0B65A) else listOf(0xFFB5432A, 0xFFE9DEC6, 0xFF1E6B5C, 0xFF2A4F9E)
    TemaId.MONGOL -> if (oscuro) listOf(0xFFFF7A59, 0xFF18263F, 0xFF6FB1FF, 0xFFF2C14E) else listOf(0xFFC2412D, 0xFFE6DFCF, 0xFF1F5FA8, 0xFFC48A1E)
    TemaId.RENACIMIENTO -> if (oscuro) listOf(0xFFE08A5E, 0xFF302318, 0xFF9EC0E0, 0xFFD9B566) else listOf(0xFF8C3B1E, 0xFFE6D6B0, 0xFF2F4F6F, 0xFF9C7A2E)
    TemaId.PIRATA -> if (oscuro) listOf(0xFFD6AA4C, 0xFF25201A, 0xFF7DBFB6, 0xFFE47B4E) else listOf(0xFF1E5157, 0xFFD9CBA6, 0xFF85621A, 0xFF9A2A1C)
    TemaId.REVOLUCION -> if (oscuro) listOf(0xFFE5525E, 0xFF25282F, 0xFF8FAEE8, 0xFF8CC08A) else listOf(0xFFB22234, 0xFFE6DDC8, 0xFF1F3A6E, 0xFF3F6B3A)
    TemaId.ANTIGUAS -> if (oscuro) listOf(0xFFE06A72, 0xFF2A2B2F, 0xFF8CC79A, 0xFFD9BC6A) else listOf(0xFF8E1B24, 0xFFDCD5C3, 0xFF2E5A3E, 0xFF9A7B2E)
    TemaId.CUEVA -> if (oscuro) listOf(0xFFE8804F, 0xFF2D2219, 0xFFDCC9A8, 0xFFF4C76E) else listOf(0xFFA2401F, 0xFFE0D0B6, 0xFF3B302A, 0xFFB07418)   // ocre rojo, caliza, carbón y ocre amarillo
}.map { Color(it) }

/** Escala de calor del mapa: poco, medio, mucho. */
internal fun juegoCalor(tema: TemaId, oscuro: Boolean): List<Color> = when (tema) {
    TemaId.ROMANO -> if (oscuro) listOf(0xFF3FA393, 0xFFD2AE5A, 0xFFE0604F) else listOf(0xFF2D7A6F, 0xFFB8892B, 0xFF9E3445)
    TemaId.URBE -> if (oscuro) listOf(0xFF8DB596, 0xFFE3C07A, 0xFFE8955A) else listOf(0xFF3D5E45, 0xFFC98A3A, 0xFFA8432A)
    TemaId.CIBER -> listOf(0xFF1E5E68, 0xFF00B8CC, 0xFFC8FBFF)
    TemaId.NEON -> listOf(0xFF5A3A70, 0xFFFFB238, 0xFFFF3D9A)
    TemaId.HELENO -> if (oscuro) listOf(0xFF5FA8D8, 0xFFD8B45C, 0xFFB992EA) else listOf(0xFF1F6E9C, 0xFFA27B22, 0xFF5A2A82)
    TemaId.EGIPTO -> if (oscuro) listOf(0xFF7895F0, 0xFFE3B341, 0xFFE8774E) else listOf(0xFF1D2F7A, 0xFF9A7212, 0xFFB8471E)
    TemaId.MEDIEVAL -> if (oscuro) listOf(0xFF8DA5E0, 0xFFD5A84A, 0xFFC9473B) else listOf(0xFF1F3F8F, 0xFFA97C1E, 0xFF9E1B1B)
    TemaId.TEMPLARIO -> if (oscuro) listOf(0xFF9AA8B4, 0xFFDAAE68, 0xFFE2453B) else listOf(0xFF46525D, 0xFFB98A45, 0xFFB0191F)
    TemaId.DISTOPIA -> if (oscuro) listOf(0xFF6D7377, 0xFFCFCFC8, 0xFFE0262F) else listOf(0xFF6D7377, 0xFF2E3B44, 0xFFB3121B)
    TemaId.REFUGIO -> if (oscuro) listOf(0xFF1E8A45, 0xFF3DFF7F, 0xFFFFB23F) else listOf(0xFF1C6E6A, 0xFFB98A15, 0xFFD9572B)
    TemaId.ORDEN -> if (oscuro) listOf(0xFF8FB3C8, 0xFFD6B25A, 0xFFD0333B) else listOf(0xFF2F4858, 0xFF7E6219, 0xFF9E1B22)
    TemaId.LABORATORIO -> if (oscuro) listOf(0xFF3CD2E5, 0xFF9D93FF, 0xFFFF7A9A) else listOf(0xFF0B8FA1, 0xFF5E4FD1, 0xFFC0466A)
    TemaId.COLONOS -> if (oscuro) listOf(0xFF8FB3E0, 0xFFE3C27A, 0xFFE07A62) else listOf(0xFF1F3A5F, 0xFF7A5A2E, 0xFFA23B2A)
    TemaId.NAPOLEON -> if (oscuro) listOf(0xFF9DB4FF, 0xFFD9B25B, 0xFFE05A6A) else listOf(0xFF1E3A8A, 0xFFB8892B, 0xFFA3172E)
    TemaId.BOTANICO -> if (oscuro) listOf(0xFF6FC2B0, 0xFFA9C27A, 0xFFE09A5E) else listOf(0xFF2E5E5A, 0xFF3F5A2C, 0xFF8C4A22)
    TemaId.RUINAS -> if (oscuro) listOf(0xFF8FCB6A, 0xFFE9C25A, 0xFFE0604A) else listOf(0xFF3E6B2A, 0xFFB57A0E, 0xFFB3261E)   // el semáforo: verde, ámbar, rojo
    TemaId.NAVIDAD -> if (oscuro) listOf(0xFF66BB6A, 0xFFF2C94C, 0xFFE5484D) else listOf(0xFF1B5E20, 0xFFC9A227, 0xFFB71C1C)
    TemaId.SEDA -> if (oscuro) listOf(0xFF4FC3C3, 0xFFE0B04F, 0xFFE26A50) else listOf(0xFF23408E, 0xFF1A8C8C, 0xFFC0392B)
    TemaId.URSS -> if (oscuro) listOf(0xFFE8D9B5, 0xFFE8B923, 0xFFE5252F) else listOf(0xFF1D1D1B, 0xFFD4A017, 0xFFC1121F)
    TemaId.GOTICO -> if (oscuro) listOf(0xFF5C8DFF, 0xFFE2B34A, 0xFFE0405E) else listOf(0xFF1F3A9E, 0xFFB8892B, 0xFFB3122E)
    TemaId.ARCANO -> if (oscuro) listOf(0xFF7DBBA4, 0xFFE6D58C, 0xFFC98472) else listOf(0xFF2D5446, 0xFF6A4B26, 0xFF7A2C24)
    TemaId.ESPACIO -> if (oscuro) listOf(0xFF8AB4FF, 0xFFC59BFF, 0xFFFFB86B) else listOf(0xFF1B3A8C, 0xFF6B4FD6, 0xFFE8601C)
    TemaId.ABISMO -> if (oscuro) listOf(0xFF3FA9FF, 0xFF5CF2C6, 0xFFE29BFF) else listOf(0xFF0F8A86, 0xFF0A5C8C, 0xFFC0563F)
    TemaId.ANDALUS -> if (oscuro) listOf(0xFF4FC1A6, 0xFFE0B65A, 0xFFE07A5F) else listOf(0xFF1E6B5C, 0xFF2A4F9E, 0xFFB5432A)
    TemaId.MONGOL -> if (oscuro) listOf(0xFF6FB1FF, 0xFFF2C14E, 0xFFFF7A59) else listOf(0xFF1F5FA8, 0xFFC48A1E, 0xFFC2412D)
    TemaId.RENACIMIENTO -> if (oscuro) listOf(0xFF9EC0E0, 0xFFD9B566, 0xFFE08A5E) else listOf(0xFF2F4F6F, 0xFF9C7A2E, 0xFF8C3B1E)
    TemaId.PIRATA -> if (oscuro) listOf(0xFF7DBFB6, 0xFFD6AA4C, 0xFFE47B4E) else listOf(0xFF1E5157, 0xFF85621A, 0xFF9A2A1C)
    TemaId.REVOLUCION -> if (oscuro) listOf(0xFF8FAEE8, 0xFF8CC08A, 0xFFE5525E) else listOf(0xFF1F3A6E, 0xFF3F6B3A, 0xFFB22234)
    TemaId.ANTIGUAS -> if (oscuro) listOf(0xFF8CC79A, 0xFFD9BC6A, 0xFFE06A72) else listOf(0xFF2E5A3E, 0xFF9A7B2E, 0xFF8E1B24)
    TemaId.CUEVA -> if (oscuro) listOf(0xFF8C7A66, 0xFFD9AE62, 0xFFE8804F) else listOf(0xFF7A6A58, 0xFFB07418, 0xFFA2401F)   // del carbón al ocre rojo
}.map { Color(it) }
