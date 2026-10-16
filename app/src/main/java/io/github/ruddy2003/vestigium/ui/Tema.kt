package io.github.ruddy2003.vestigium.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Paleta de bronce patinado, igual que la versión web. */
object Paleta {
    val fondo = Color(0xFFECEEEF); val superficie = Color(0xFFF8F9F9); val superficie2 = Color(0xFFE2E6E7)
    val tinta = Color(0xFF18212A); val tenue = Color(0xFF58646F); val linea = Color(0xFFCDD4D7)
    val acento = Color(0xFF1E6E68); val acentoSuave = Color(0xFFD3E8E5); val ambar = Color(0xFF9A5B12); val ambarSuave = Color(0xFFF4E6D2)
    val peligro = Color(0xFFA3312F)

    val fondoO = Color(0xFF11171B); val superficieO = Color(0xFF182026); val superficie2O = Color(0xFF222C33)
    val tintaO = Color(0xFFE4E9EC); val tenueO = Color(0xFF93A0AA); val lineaO = Color(0xFF2C3841)
    val acentoO = Color(0xFF4DB3A8); val acentoSuaveO = Color(0xFF1D3836); val ambarO = Color(0xFFE0A55A); val ambarSuaveO = Color(0xFF3A2C1A)
}

private val claro = lightColorScheme(
    primary = Paleta.acento, onPrimary = Color.White, primaryContainer = Paleta.acentoSuave, onPrimaryContainer = Paleta.tinta,
    secondary = Paleta.ambar, secondaryContainer = Paleta.ambarSuave, onSecondaryContainer = Paleta.tinta,
    background = Paleta.fondo, onBackground = Paleta.tinta, surface = Paleta.superficie, onSurface = Paleta.tinta,
    surfaceVariant = Paleta.superficie2, onSurfaceVariant = Paleta.tenue, outline = Paleta.linea, outlineVariant = Paleta.linea,
    error = Paleta.peligro, surfaceContainer = Paleta.superficie, surfaceContainerHigh = Paleta.superficie2, surfaceContainerLow = Paleta.superficie,
)

private val oscuro = darkColorScheme(
    primary = Paleta.acentoO, onPrimary = Color(0xFF0B1A19), primaryContainer = Paleta.acentoSuaveO, onPrimaryContainer = Paleta.tintaO,
    secondary = Paleta.ambarO, secondaryContainer = Paleta.ambarSuaveO, onSecondaryContainer = Paleta.tintaO,
    background = Paleta.fondoO, onBackground = Paleta.tintaO, surface = Paleta.superficieO, onSurface = Paleta.tintaO,
    surfaceVariant = Paleta.superficie2O, onSurfaceVariant = Paleta.tenueO, outline = Paleta.lineaO, outlineVariant = Paleta.lineaO,
    error = Color(0xFFE7807C), surfaceContainer = Paleta.superficieO, surfaceContainerHigh = Paleta.superficie2O, surfaceContainerLow = Paleta.superficieO,
)

private val tipografia = Typography(
    headlineMedium = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium, fontSize = 26.sp, lineHeight = 32.sp),
    headlineSmall = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium, fontSize = 22.sp, lineHeight = 28.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium, fontSize = 21.sp, lineHeight = 27.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp, letterSpacing = 0.9.sp, lineHeight = 16.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontSize = 12.5.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
)

@Composable
fun TemaVestigium(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) oscuro else claro, typography = tipografia, content = content)
}
