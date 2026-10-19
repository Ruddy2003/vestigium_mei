package io.github.ruddy2003.vestigium.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
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
    val kufi = FontFamily(Font(R.font.kufi_medium, FontWeight.Normal), Font(R.font.kufi_medium, FontWeight.Medium), Font(R.font.kufi_bold, FontWeight.Bold), Font(R.font.kufi_bold, FontWeight.SemiBold))
    val amiri = FontFamily(Font(R.font.amiri_regular, FontWeight.Normal))
    val filosofo = FontFamily(Font(R.font.philosopher_regular, FontWeight.Normal), Font(R.font.philosopher_bold, FontWeight.Bold), Font(R.font.philosopher_bold, FontWeight.SemiBold))
    val italianno = FontFamily(Font(R.font.italianno, FontWeight.Normal))
    val cormorant = FontFamily(Font(R.font.cormorant_regular, FontWeight.Normal), Font(R.font.cormorant_medium, FontWeight.Medium), Font(R.font.cormorant_medium, FontWeight.SemiBold))
    val fraunces = FontFamily(Font(R.font.fraunces_titulo, FontWeight.Normal), Font(R.font.fraunces_titulo, FontWeight.Medium), Font(R.font.fraunces_negrita, FontWeight.SemiBold), Font(R.font.fraunces_negrita, FontWeight.Bold))
    val cinzelDeco = FontFamily(Font(R.font.cinzel_deco_regular, FontWeight.Normal), Font(R.font.cinzel_deco_bold, FontWeight.Bold))
    val almendra = FontFamily(Font(R.font.almendra_regular, FontWeight.Normal), Font(R.font.almendra_bold, FontWeight.Bold))
    val russo = FontFamily(Font(R.font.russo, FontWeight.Normal))
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
    ) else lightColorScheme(
        primary = c(primario), onPrimary = c(sobrePrimario), primaryContainer = c(contenedor), onPrimaryContainer = c(sobreContenedor),
        secondary = c(secundario), onSecondary = Color.White, secondaryContainer = c(contSecundario), onSecondaryContainer = c(tinta),
        tertiary = c(terciario), background = c(fondo), onBackground = c(tinta), surface = c(superficie), onSurface = c(tinta),
        surfaceVariant = c(variante), onSurfaceVariant = c(tenue), outline = c(borde), outlineVariant = c(borde), error = c(error),
        surfaceContainer = c(superficie), surfaceContainerHigh = c(variante), surfaceContainerHighest = c(variante),
        surfaceContainerLow = c(superficie), surfaceContainerLowest = c(superficie),
    )
}

/**
 * Tipografía a partir de tres familias: títulos, texto y etiquetas (datos).
 * [brillo] agrega un resplandor de pantalla antigua a los títulos (cyberpunk y refugio de noche).
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
internal val tipoMedieval = tipografia(LetraExtra.gotica, FontWeight.Normal, 1.18f, 0.6f, Letra.garamond, 1.12f, Letra.garamond, 1.15f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Distopía: papel de periódico gris, tinta negra y rojo de cartel.
// ---------------------------------------------------------------------
internal val distopiaClaro = esquema(false, 0xFFDEDBD2, 0xFFECE9E1, 0xFFCFCBC0, 0xFFA7A296, 0xFF141414, 0xFF474744,
    0xFFB3121B, 0xFFFFFFFF, 0xFFF0D2D0, 0xFF141414, 0xFF2E3B44, 0xFFC9D1D6, 0xFF5E5E33, 0xFF8A0E14)
internal val distopiaOscuro = esquema(true, 0xFF0F1011, 0xFF17191A, 0xFF222527, 0xFF34383B, 0xFFE8E6E0, 0xFFA8A8A1,
    0xFFE0262F, 0xFFFFFFFF, 0xFF3A1012, 0xFFF2E0DF, 0xFF9FB0BA, 0xFF24313A, 0xFFB7B37A, 0xFFFF6B6B)
internal val tipoDistopia = tipografia(LetraExtra.oswald, FontWeight.Bold, 1.05f, 1.4f, Letra.texto, 1f, Letra.codigo, 1f)

// ---------------------------------------------------------------------
//  Refugio: optimismo atómico de los cincuenta de día; pantalla de fósforo verde de noche.
// ---------------------------------------------------------------------
internal val refugioClaro = esquema(false, 0xFFF1E8D0, 0xFFF8F1DE, 0xFFE6DAB8, 0xFFCDBE95, 0xFF1F2A2A, 0xFF4C5752,
    0xFF1C6E6A, 0xFFFFFFFF, 0xFFCFE6E1, 0xFF1F2A2A, 0xFFD9572B, 0xFFF6D6C6, 0xFFB98A15, 0xFFA3312F)
internal val refugioOscuro = esquema(true, 0xFF070D08, 0xFF0B140D, 0xFF112016, 0xFF1C3A24, 0xFF9CF5B0, 0xFF55B872,
    0xFF3DFF7F, 0xFF021407, 0xFF0F2D18, 0xFFB8FFC9, 0xFFFFB23F, 0xFF2C2008, 0xFF7CFFD0, 0xFFFF6B5B)
internal val tipoRefugioClaro = tipografia(LetraExtra.vt323, FontWeight.Normal, 1.4f, 1f, Letra.codigo, 0.97f, Letra.codigo, 1f)
internal val tipoRefugioOscuro = tipografia(LetraExtra.vt323, FontWeight.Normal, 1.4f, 1f, Letra.codigo, 0.97f, Letra.codigo, 1f, brillo = Color(0xFF3DFF7F))

// ---------------------------------------------------------------------
//  Orden: acero cepillado, carmesí y latón; letra de estarcido militar.
// ---------------------------------------------------------------------
internal val ordenClaro = esquema(false, 0xFFE2E5E8, 0xFFEEF0F2, 0xFFD3D7DC, 0xFFAEB4BB, 0xFF15181B, 0xFF4B525A,
    0xFF9E1B22, 0xFFFFFFFF, 0xFFEED3D4, 0xFF15181B, 0xFF7E6219, 0xFFEEE1BF, 0xFF2F4858, 0xFF8A0E14)
internal val ordenOscuro = esquema(true, 0xFF101215, 0xFF171A1E, 0xFF22262B, 0xFF3A4048, 0xFFDDE2E7, 0xFF9DA6B0,
    0xFFD0333B, 0xFFFFFFFF, 0xFF3A1416, 0xFFF5DADB, 0xFFD6B25A, 0xFF33290F, 0xFF8FB3C8, 0xFFFF7A7A)
internal val tipoOrden = tipografia(LetraExtra.estarcido, FontWeight.Normal, 1.05f, 1.6f, Letra.texto, 1f, Letra.codigo, 1f)

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
internal val tipoColonos = tipografia(LetraExtra.fell, FontWeight.Normal, 1.12f, 0.8f, Letra.garamond, 1.12f, Letra.garamond, 1.15f, cifrasAlineadas = true)

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
internal val tipoBotanico = tipografia(LetraExtra.fraunces, FontWeight.Medium, 1.04f, 0.2f, Letra.garamond, 1.12f, Letra.garamond, 1.14f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Navidad: nieve y crema, rojo, verde pino y oro; de noche, el bosque nevado.
// ---------------------------------------------------------------------
internal val navidadClaro = esquema(false, 0xFFF6F1E7, 0xFFFFFFFF, 0xFFECE4D6, 0xFFD8CBB2, 0xFF1E2A22, 0xFF56635A,
    0xFFB71C1C, 0xFFFFFFFF, 0xFFF6D5D3, 0xFF1E2A22, 0xFF1B5E20, 0xFFD5EBD6, 0xFFC9A227, 0xFF8A1A1A)
internal val navidadOscuro = esquema(true, 0xFF0E1A14, 0xFF14241B, 0xFF1C3125, 0xFF2E4A39, 0xFFF3EFE6, 0xFFB5C2B8,
    0xFFE5484D, 0xFFFFFFFF, 0xFF4A1416, 0xFFF3EFE6, 0xFF66BB6A, 0xFF1D3A22, 0xFFF2C94C, 0xFFFF8A80)
internal val tipoNavidad = tipografia(LetraExtra.cinzelDeco, FontWeight.Normal, 1.02f, 1.0f, Letra.garamond, 1.12f, Letra.garamond, 1.15f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Ruta de la Seda: seda cruda, lapislázuli, cinabrio y turquesa; de noche, el desierto bajo las estrellas.
// ---------------------------------------------------------------------
internal val sedaClaro = esquema(false, 0xFFF5EEDC, 0xFFFBF6E9, 0xFFEADFC3, 0xFFD4C29A, 0xFF1F1B3A, 0xFF59546F,
    0xFF23408E, 0xFFFFFFFF, 0xFFD9E0F4, 0xFF1F1B3A, 0xFFC0392B, 0xFFF5D5CF, 0xFF1A8C8C, 0xFF8A1A1A)
internal val sedaOscuro = esquema(true, 0xFF0F1426, 0xFF161D35, 0xFF1F2846, 0xFF33406A, 0xFFF2EAD3, 0xFFB9B29F,
    0xFFE0B04F, 0xFF1A1405, 0xFF2B3A6E, 0xFFF2EAD3, 0xFFE26A50, 0xFF3D1E18, 0xFF4FC3C3, 0xFFFF8A80)
internal val tipoSeda = tipografia(LetraExtra.almendra, FontWeight.Normal, 1.12f, 0.4f, Letra.garamond, 1.12f, Letra.garamond, 1.15f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Soviético: papel de cartel, rojo y negro en diagonales constructivistas.
// ---------------------------------------------------------------------
internal val urssClaro = esquema(false, 0xFFEFE6D2, 0xFFF7F0DE, 0xFFE3D6B8, 0xFFC9B994, 0xFF161414, 0xFF4A4440,
    0xFFC1121F, 0xFFFFFFFF, 0xFFF3D0D0, 0xFF161414, 0xFF1D1D1B, 0xFFDDD3BC, 0xFFD4A017, 0xFF8A0E14)
internal val urssOscuro = esquema(true, 0xFF121010, 0xFF1B1717, 0xFF262020, 0xFF3D3333, 0xFFF1E7D2, 0xFFB7AA95,
    0xFFE5252F, 0xFFFFFFFF, 0xFF4A1013, 0xFFF1E7D2, 0xFFE8D9B5, 0xFF3A3328, 0xFFE8B923, 0xFFFF6B6B)
internal val tipoUrss = tipografia(LetraExtra.russo, FontWeight.Normal, 1.0f, 1.0f, Letra.texto, 1f, Letra.codigo, 1f)

// ---------------------------------------------------------------------
//  Gótico: una capilla de vitrales. De día, piedra caliza con cristal zafiro y rubí; de noche, la bóveda azul con estrellas de oro.
// ---------------------------------------------------------------------
internal val goticoClaro = esquema(false, 0xFFEDE7DA, 0xFFF7F3EA, 0xFFE2D9C6, 0xFFC9BC9F, 0xFF141A33, 0xFF4F5570,
    0xFF1F3A9E, 0xFFFFFFFF, 0xFFDCE2F6, 0xFF141A33, 0xFFB3122E, 0xFFF4D3D8, 0xFFB8892B, 0xFF8A1A1A)
internal val goticoOscuro = esquema(true, 0xFF0A0F2A, 0xFF111842, 0xFF1A2356, 0xFF2E3A7A, 0xFFF0E9D8, 0xFFB0AFC8,
    0xFFE2B34A, 0xFF1A1405, 0xFF26306E, 0xFFF0E9D8, 0xFFE0405E, 0xFF3A1222, 0xFF5C8DFF, 0xFFFF8A80)
internal val tipoGotico = tipografia(LetraExtra.textura, FontWeight.Normal, 1.16f, 0.3f, Letra.garamond, 1.12f, Letra.garamond, 1.15f, cifrasAlineadas = true)

// ---------------------------------------------------------------------
//  Espacio: de día, la sala de control (blanco, azul marino y naranja); de noche, estrellas y nebulosas.
// ---------------------------------------------------------------------
internal val espacioClaro = esquema(false, 0xFFEEF1F6, 0xFFFFFFFF, 0xFFE1E6EF, 0xFFC3CCDA, 0xFF0D1526, 0xFF4E5A70,
    0xFF1B3A8C, 0xFFFFFFFF, 0xFFD7E0F5, 0xFF0D1526, 0xFFE8601C, 0xFFFBDCCB, 0xFF6B4FD6, 0xFFB3261E)
internal val espacioOscuro = esquema(true, 0xFF03050C, 0xFF0A0F1E, 0xFF121A31, 0xFF26324F, 0xFFE8ECF8, 0xFF8E9AB8,
    0xFF8AB4FF, 0xFF020818, 0xFF18264D, 0xFFE8ECF8, 0xFFFFB86B, 0xFF3A2410, 0xFFC59BFF, 0xFFFF8A80)
internal val tipoEspacio = tipografia(LetraExtra.orbita, FontWeight.Bold, 0.96f, 2.0f, LetraExtra.grotesk, 1.02f, Letra.codigo, 1f)
internal val tipoEspacioOscuro = tipografia(LetraExtra.orbita, FontWeight.Bold, 0.96f, 2.0f, LetraExtra.grotesk, 1.02f, Letra.codigo, 1f, brillo = Color(0xFF8AB4FF))

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
internal val tipoMongol = tipografia(LetraExtra.filosofo, FontWeight.Bold, 1.04f, 1.2f, Letra.texto, 1f, LetraExtra.filosofo, 1.06f)

// ---------------------------------------------------------------------
//  Renacimiento: los cuadernos de Leonardo. Papel envejecido, tinta sepia y sanguina; de noche, nogal y pergamino.
// ---------------------------------------------------------------------
internal val renacimientoClaro = esquema(false, 0xFFF0E4C6, 0xFFF7EDD4, 0xFFE6D6B0, 0xFFCBB687, 0xFF3A2614, 0xFF6E5638,
    0xFF8C3B1E, 0xFFFFFFFF, 0xFFF2D6C6, 0xFF3A2614, 0xFF2F4F6F, 0xFFD6E0EA, 0xFF9C7A2E, 0xFF8A1A1A)
internal val renacimientoOscuro = esquema(true, 0xFF1A130C, 0xFF241A11, 0xFF302318, 0xFF4A3826, 0xFFEFE0BE, 0xFFBCA884,
    0xFFE08A5E, 0xFF2A1006, 0xFF4A2A18, 0xFFEFE0BE, 0xFF9EC0E0, 0xFF22303E, 0xFFD9B566, 0xFFFF8A80)
internal val tipoRenacimiento = tipografia(LetraExtra.italianno, FontWeight.Normal, 1.78f, 0f, LetraExtra.cormorant, 1.2f, LetraExtra.cormorant, 1.22f, cifrasAlineadas = true)

/** Juegos de color para gráficas: bajo, medio, alto y el color fuerte (secuencial). */
internal fun juegoTonos(tema: TemaId, oscuro: Boolean): List<Color> = when (tema) {
    TemaId.ROMANO -> if (oscuro) listOf(0xFFA63A4D, 0xFF2A2F34, 0xFF3FA393, 0xFFD2AE5A) else listOf(0xFF9E3445, 0xFFE4E2DA, 0xFF2D7A6F, 0xFFB8892B)
    TemaId.CIBER -> listOf(0xFFFF2E88, 0xFF1A2232, 0xFF00E5FF, 0xFF00E5FF)
    TemaId.HELENO -> if (oscuro) listOf(0xFFB04A6A, 0xFF2A2440, 0xFF5FA8D8, 0xFFD8B45C) else listOf(0xFF8E2C48, 0xFFE9E3D6, 0xFF1F6E9C, 0xFFA27B22)
    TemaId.MEDIEVAL -> if (oscuro) listOf(0xFFC9473B, 0xFF2A2016, 0xFF8DA5E0, 0xFFD5A84A) else listOf(0xFF9E1B1B, 0xFFE5D6B4, 0xFF1F3F8F, 0xFFA97C1E)
    TemaId.DISTOPIA -> if (oscuro) listOf(0xFFE0262F, 0xFF2A2C2E, 0xFFCFCFC8, 0xFFE0262F) else listOf(0xFFB3121B, 0xFFCFCBC0, 0xFF2E3B44, 0xFFB3121B)
    TemaId.REFUGIO -> if (oscuro) listOf(0xFFFFB23F, 0xFF112016, 0xFF3DFF7F, 0xFF3DFF7F) else listOf(0xFFD9572B, 0xFFE6DAB8, 0xFF1C6E6A, 0xFF1C6E6A)
    TemaId.ORDEN -> if (oscuro) listOf(0xFFD0333B, 0xFF262A30, 0xFF8FB3C8, 0xFFD6B25A) else listOf(0xFF9E1B22, 0xFFD3D7DC, 0xFF2F4858, 0xFF7E6219)
    TemaId.LABORATORIO -> if (oscuro) listOf(0xFFFF7A9A, 0xFF18222F, 0xFF3CD2E5, 0xFF3CD2E5) else listOf(0xFFC0466A, 0xFFE6EDF2, 0xFF0B8FA1, 0xFF0B8FA1)
    TemaId.COLONOS -> if (oscuro) listOf(0xFFE07A62, 0xFF1E2838, 0xFF8FB3E0, 0xFFE3C27A) else listOf(0xFFA23B2A, 0xFFE7DDC8, 0xFF1F3A5F, 0xFF7A5A2E)
    TemaId.NAPOLEON -> if (oscuro) listOf(0xFFE05A6A, 0xFF1B2550, 0xFF9DB4FF, 0xFFD9B25B) else listOf(0xFFA3172E, 0xFFEAE1CB, 0xFF1E3A8A, 0xFFB8892B)
    TemaId.BOTANICO -> if (oscuro) listOf(0xFFE09A5E, 0xFF2A251B, 0xFFA9C27A, 0xFF6FC2B0) else listOf(0xFF8C4A22, 0xFFE3D9C0, 0xFF3F5A2C, 0xFF2E5E5A)
    TemaId.NAVIDAD -> if (oscuro) listOf(0xFFE5484D, 0xFF1C3125, 0xFF66BB6A, 0xFFF2C94C) else listOf(0xFFB71C1C, 0xFFECE4D6, 0xFF1B5E20, 0xFFC9A227)
    TemaId.SEDA -> if (oscuro) listOf(0xFFE26A50, 0xFF1F2846, 0xFF4FC3C3, 0xFFE0B04F) else listOf(0xFFC0392B, 0xFFEADFC3, 0xFF23408E, 0xFF1A8C8C)
    TemaId.URSS -> if (oscuro) listOf(0xFFE5252F, 0xFF262020, 0xFFE8D9B5, 0xFFE5252F) else listOf(0xFFC1121F, 0xFFE3D6B8, 0xFF1D1D1B, 0xFFC1121F)
    TemaId.GOTICO -> if (oscuro) listOf(0xFFE0405E, 0xFF1A2356, 0xFF5C8DFF, 0xFFE2B34A) else listOf(0xFFB3122E, 0xFFE2D9C6, 0xFF1F3A9E, 0xFFB8892B)
    TemaId.ESPACIO -> if (oscuro) listOf(0xFFFF8A65, 0xFF121A31, 0xFF8AB4FF, 0xFFC59BFF) else listOf(0xFFE8601C, 0xFFE1E6EF, 0xFF1B3A8C, 0xFF6B4FD6)
    TemaId.ANDALUS -> if (oscuro) listOf(0xFFE07A5F, 0xFF18302D, 0xFF4FC1A6, 0xFFE0B65A) else listOf(0xFFB5432A, 0xFFE9DEC6, 0xFF1E6B5C, 0xFF2A4F9E)
    TemaId.MONGOL -> if (oscuro) listOf(0xFFFF7A59, 0xFF18263F, 0xFF6FB1FF, 0xFFF2C14E) else listOf(0xFFC2412D, 0xFFE6DFCF, 0xFF1F5FA8, 0xFFC48A1E)
    TemaId.RENACIMIENTO -> if (oscuro) listOf(0xFFE08A5E, 0xFF302318, 0xFF9EC0E0, 0xFFD9B566) else listOf(0xFF8C3B1E, 0xFFE6D6B0, 0xFF2F4F6F, 0xFF9C7A2E)
}.map { Color(it) }

/** Escala de calor del mapa: poco, medio, mucho. */
internal fun juegoCalor(tema: TemaId, oscuro: Boolean): List<Color> = when (tema) {
    TemaId.ROMANO -> if (oscuro) listOf(0xFF3FA393, 0xFFD2AE5A, 0xFFE0604F) else listOf(0xFF2D7A6F, 0xFFB8892B, 0xFF9E3445)
    TemaId.CIBER -> listOf(0xFF00E5FF, 0xFFE8FF3A, 0xFFFF2E88)
    TemaId.HELENO -> if (oscuro) listOf(0xFF5FA8D8, 0xFFD8B45C, 0xFFB992EA) else listOf(0xFF1F6E9C, 0xFFA27B22, 0xFF5A2A82)
    TemaId.MEDIEVAL -> if (oscuro) listOf(0xFF8DA5E0, 0xFFD5A84A, 0xFFC9473B) else listOf(0xFF1F3F8F, 0xFFA97C1E, 0xFF9E1B1B)
    TemaId.DISTOPIA -> if (oscuro) listOf(0xFF6D7377, 0xFFCFCFC8, 0xFFE0262F) else listOf(0xFF6D7377, 0xFF2E3B44, 0xFFB3121B)
    TemaId.REFUGIO -> if (oscuro) listOf(0xFF1E8A45, 0xFF3DFF7F, 0xFFFFB23F) else listOf(0xFF1C6E6A, 0xFFB98A15, 0xFFD9572B)
    TemaId.ORDEN -> if (oscuro) listOf(0xFF8FB3C8, 0xFFD6B25A, 0xFFD0333B) else listOf(0xFF2F4858, 0xFF7E6219, 0xFF9E1B22)
    TemaId.LABORATORIO -> if (oscuro) listOf(0xFF3CD2E5, 0xFF9D93FF, 0xFFFF7A9A) else listOf(0xFF0B8FA1, 0xFF5E4FD1, 0xFFC0466A)
    TemaId.COLONOS -> if (oscuro) listOf(0xFF8FB3E0, 0xFFE3C27A, 0xFFE07A62) else listOf(0xFF1F3A5F, 0xFF7A5A2E, 0xFFA23B2A)
    TemaId.NAPOLEON -> if (oscuro) listOf(0xFF9DB4FF, 0xFFD9B25B, 0xFFE05A6A) else listOf(0xFF1E3A8A, 0xFFB8892B, 0xFFA3172E)
    TemaId.BOTANICO -> if (oscuro) listOf(0xFF6FC2B0, 0xFFA9C27A, 0xFFE09A5E) else listOf(0xFF2E5E5A, 0xFF3F5A2C, 0xFF8C4A22)
    TemaId.NAVIDAD -> if (oscuro) listOf(0xFF66BB6A, 0xFFF2C94C, 0xFFE5484D) else listOf(0xFF1B5E20, 0xFFC9A227, 0xFFB71C1C)
    TemaId.SEDA -> if (oscuro) listOf(0xFF4FC3C3, 0xFFE0B04F, 0xFFE26A50) else listOf(0xFF23408E, 0xFF1A8C8C, 0xFFC0392B)
    TemaId.URSS -> if (oscuro) listOf(0xFFE8D9B5, 0xFFE8B923, 0xFFE5252F) else listOf(0xFF1D1D1B, 0xFFD4A017, 0xFFC1121F)
    TemaId.GOTICO -> if (oscuro) listOf(0xFF5C8DFF, 0xFFE2B34A, 0xFFE0405E) else listOf(0xFF1F3A9E, 0xFFB8892B, 0xFFB3122E)
    TemaId.ESPACIO -> if (oscuro) listOf(0xFF8AB4FF, 0xFFC59BFF, 0xFFFFB86B) else listOf(0xFF1B3A8C, 0xFF6B4FD6, 0xFFE8601C)
    TemaId.ANDALUS -> if (oscuro) listOf(0xFF4FC1A6, 0xFFE0B65A, 0xFFE07A5F) else listOf(0xFF1E6B5C, 0xFF2A4F9E, 0xFFB5432A)
    TemaId.MONGOL -> if (oscuro) listOf(0xFF6FB1FF, 0xFFF2C14E, 0xFFFF7A59) else listOf(0xFF1F5FA8, 0xFFC48A1E, 0xFFC2412D)
    TemaId.RENACIMIENTO -> if (oscuro) listOf(0xFF9EC0E0, 0xFFD9B566, 0xFFE08A5E) else listOf(0xFF2F4F6F, 0xFF9C7A2E, 0xFF8C3B1E)
}.map { Color(it) }
