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
    val rye = FontFamily(Font(R.font.rye, FontWeight.Normal))
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
//  Día de Muertos: papel de china, rosa mexicano, cempasúchil y morado; de noche, el panteón con velas.
// ---------------------------------------------------------------------
internal val muertosClaro = esquema(false, 0xFFFFF4E2, 0xFFFFFAF0, 0xFFF8E3C4, 0xFFE9C48F, 0xFF2A1033, 0xFF6B4A6E,
    0xFFC2185B, 0xFFFFFFFF, 0xFFF9D3E2, 0xFF2A1033, 0xFFE67E00, 0xFFFFE0B2, 0xFF6A1B9A, 0xFFB71C1C)
internal val muertosOscuro = esquema(true, 0xFF140A1C, 0xFF1E1029, 0xFF2A1838, 0xFF44275A, 0xFFFBEFFF, 0xFFC6A9D3,
    0xFFFF4FA3, 0xFF2A0016, 0xFF4A1236, 0xFFFBEFFF, 0xFFFFA726, 0xFF3D2A0A, 0xFFB388FF, 0xFFFF8A80)
internal val tipoMuertos = tipografia(LetraExtra.rye, FontWeight.Normal, 0.98f, 0.8f, Letra.texto, 1f, Letra.codigo, 1f)

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
    TemaId.MUERTOS -> if (oscuro) listOf(0xFFB388FF, 0xFF2A1838, 0xFFFFA726, 0xFFFF4FA3) else listOf(0xFF6A1B9A, 0xFFF8E3C4, 0xFFE67E00, 0xFFC2185B)
    TemaId.NAVIDAD -> if (oscuro) listOf(0xFFE5484D, 0xFF1C3125, 0xFF66BB6A, 0xFFF2C94C) else listOf(0xFFB71C1C, 0xFFECE4D6, 0xFF1B5E20, 0xFFC9A227)
    TemaId.SEDA -> if (oscuro) listOf(0xFFE26A50, 0xFF1F2846, 0xFF4FC3C3, 0xFFE0B04F) else listOf(0xFFC0392B, 0xFFEADFC3, 0xFF23408E, 0xFF1A8C8C)
    TemaId.URSS -> if (oscuro) listOf(0xFFE5252F, 0xFF262020, 0xFFE8D9B5, 0xFFE5252F) else listOf(0xFFC1121F, 0xFFE3D6B8, 0xFF1D1D1B, 0xFFC1121F)
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
    TemaId.MUERTOS -> if (oscuro) listOf(0xFFB388FF, 0xFFFF4FA3, 0xFFFFA726) else listOf(0xFF6A1B9A, 0xFFC2185B, 0xFFE67E00)
    TemaId.NAVIDAD -> if (oscuro) listOf(0xFF66BB6A, 0xFFF2C94C, 0xFFE5484D) else listOf(0xFF1B5E20, 0xFFC9A227, 0xFFB71C1C)
    TemaId.SEDA -> if (oscuro) listOf(0xFF4FC3C3, 0xFFE0B04F, 0xFFE26A50) else listOf(0xFF23408E, 0xFF1A8C8C, 0xFFC0392B)
    TemaId.URSS -> if (oscuro) listOf(0xFFE8D9B5, 0xFFE8B923, 0xFFE5252F) else listOf(0xFF1D1D1B, 0xFFD4A017, 0xFFC1121F)
}.map { Color(it) }
