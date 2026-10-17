package io.github.ruddy2003.vestigium.ui

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat
import io.github.ruddy2003.vestigium.data.Repo
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.R

/**
 * Dirección visual: una inscripción romana leída en un editor de código.
 * De día, mármol de Carrara con pórfido imperial y bronce verde; de noche, basalto con oro, como una terminal tallada.
 * Cinzel (capitales lapidarias) para títulos, IBM Plex Mono para datos y claves, IBM Plex Sans para leer.
 */
object Letra {
    val inscripcion = FontFamily(Font(R.font.cinzel_regular, FontWeight.Normal), Font(R.font.cinzel_semibold, FontWeight.SemiBold))
    val codigo = FontFamily(Font(R.font.plex_mono_regular, FontWeight.Normal), Font(R.font.plex_mono_medium, FontWeight.Medium), Font(R.font.plex_mono_semibold, FontWeight.SemiBold))
    val texto = FontFamily(Font(R.font.plex_sans_regular, FontWeight.Normal), Font(R.font.plex_sans_medium, FontWeight.Medium), Font(R.font.plex_sans_semibold, FontWeight.SemiBold))
    /** EB Garamond: tiene griego politónico, para el tema helénico. */
    val garamond = FontFamily(Font(R.font.garamond_regular, FontWeight.Normal), Font(R.font.garamond_medium, FontWeight.Medium), Font(R.font.garamond_semibold, FontWeight.SemiBold))
    /** Share Tech Mono: rótulos de terminal para el tema cyberpunk. */
    val terminal = FontFamily(Font(R.font.sharetech_mono, FontWeight.Normal))
}

object Paleta {
    // Marmor (claro)
    val marmol = Color(0xFFECEDEA); val marmolClaro = Color(0xFFF7F7F4); val marmolVeta = Color(0xFFE1E2DD)
    val tinta = Color(0xFF17191C); val tintaTenue = Color(0xFF555A61); val veta = Color(0xFFC9CBC4)
    val porfido = Color(0xFF7C2433); val porfidoSuave = Color(0xFFEFDDDF)
    val bronce = Color(0xFF2D6B63); val bronceSuave = Color(0xFFD6E7E3)
    val oroOscuro = Color(0xFF8E6A1F)
    // Basaltum (oscuro)
    val basalto = Color(0xFF0E1113); val basaltoAlto = Color(0xFF161A1D); val basaltoVeta = Color(0xFF20252A)
    val marfil = Color(0xFFE7E3DA); val marfilTenue = Color(0xFFA6ACB2); val grieta = Color(0xFF2B3137)
    val oro = Color(0xFFD2AE5A); val oroSuave = Color(0xFF3A3120)
    val bronceClaro = Color(0xFF67BBAE); val bronceNoche = Color(0xFF1E3633)
}

/** Los tres temas. El cyberpunk solo existe en oscuro. */
enum class TemaId(val id: String, val nombre: String, val descripcion: String, val soloOscuro: Boolean = false) {
    ROMANO("romano", "Romano", "Mármol, pórfido y oro. Títulos en latín, fechas romanas."),
    CIBER("ciber", "Cyberpunk", "Terminal de neón sobre negro. Todo en código. Solo oscuro.", soloOscuro = true),
    HELENO("heleno", "Helénico", "Púrpura macedonio, oro y azul del Egeo. Títulos en griego, el sol de Vergina."),
}

private val romanoClaro = lightColorScheme(
    primary = Paleta.porfido, onPrimary = Color(0xFFFFF8F3), primaryContainer = Paleta.porfidoSuave, onPrimaryContainer = Paleta.tinta,
    secondary = Paleta.bronce, onSecondary = Color.White, secondaryContainer = Paleta.bronceSuave, onSecondaryContainer = Paleta.tinta,
    tertiary = Paleta.oroOscuro,
    background = Paleta.marmol, onBackground = Paleta.tinta, surface = Paleta.marmolClaro, onSurface = Paleta.tinta,
    surfaceVariant = Paleta.marmolVeta, onSurfaceVariant = Paleta.tintaTenue, outline = Paleta.veta, outlineVariant = Paleta.veta,
    error = Color(0xFFA3312F),
    surfaceContainer = Paleta.marmolClaro, surfaceContainerHigh = Paleta.marmolVeta, surfaceContainerHighest = Paleta.marmolVeta,
    surfaceContainerLow = Paleta.marmolClaro, surfaceContainerLowest = Paleta.marmolClaro,
)

private val romanoOscuro = darkColorScheme(
    primary = Paleta.oro, onPrimary = Color(0xFF1A1405), primaryContainer = Paleta.oroSuave, onPrimaryContainer = Paleta.marfil,
    secondary = Paleta.bronceClaro, onSecondary = Color(0xFF07211D), secondaryContainer = Paleta.bronceNoche, onSecondaryContainer = Paleta.marfil,
    tertiary = Paleta.oro,
    background = Paleta.basalto, onBackground = Paleta.marfil, surface = Paleta.basaltoAlto, onSurface = Paleta.marfil,
    surfaceVariant = Paleta.basaltoVeta, onSurfaceVariant = Paleta.marfilTenue, outline = Paleta.grieta, outlineVariant = Paleta.grieta,
    error = Color(0xFFE58C86),
    surfaceContainer = Paleta.basaltoAlto, surfaceContainerHigh = Paleta.basaltoVeta, surfaceContainerHighest = Paleta.basaltoVeta,
    surfaceContainerLow = Paleta.basaltoAlto, surfaceContainerLowest = Paleta.basalto,
)

/** Neón cian y magenta sobre negro azulado. */
private val ciber = darkColorScheme(
    primary = Color(0xFF00E5FF), onPrimary = Color(0xFF00161A), primaryContainer = Color(0xFF06282F), onPrimaryContainer = Color(0xFFBFF8FF),
    secondary = Color(0xFFFF2E88), onSecondary = Color(0xFF1A0010), secondaryContainer = Color(0xFF2B0A1C), onSecondaryContainer = Color(0xFFFFD0E4),
    tertiary = Color(0xFFE8FF3A),
    background = Color(0xFF05070B), onBackground = Color(0xFFD6F3F7), surface = Color(0xFF0A0E15), onSurface = Color(0xFFD6F3F7),
    surfaceVariant = Color(0xFF111925), onSurfaceVariant = Color(0xFF86A0B3), outline = Color(0xFF15404A), outlineVariant = Color(0xFF15404A),
    error = Color(0xFFFF5470),
    surfaceContainer = Color(0xFF0A0E15), surfaceContainerHigh = Color(0xFF111925), surfaceContainerHighest = Color(0xFF111925),
    surfaceContainerLow = Color(0xFF0A0E15), surfaceContainerLowest = Color(0xFF05070B),
)

/** Pergamino y mármol con púrpura macedonio y azul del Egeo. */
private val helenoClaro = lightColorScheme(
    primary = Color(0xFF5A2A82), onPrimary = Color.White, primaryContainer = Color(0xFFEADCF5), onPrimaryContainer = Color(0xFF1E1A2B),
    secondary = Color(0xFF1E5F8C), onSecondary = Color.White, secondaryContainer = Color(0xFFD7E7F3), onSecondaryContainer = Color(0xFF1E1A2B),
    tertiary = Color(0xFFA27B22),
    background = Color(0xFFF3EFE6), onBackground = Color(0xFF1E1A2B), surface = Color(0xFFFBF8F1), onSurface = Color(0xFF1E1A2B),
    surfaceVariant = Color(0xFFEAE3D4), onSurfaceVariant = Color(0xFF575066), outline = Color(0xFFD3C8B3), outlineVariant = Color(0xFFD3C8B3),
    error = Color(0xFFA3312F),
    surfaceContainer = Color(0xFFFBF8F1), surfaceContainerHigh = Color(0xFFEAE3D4), surfaceContainerHighest = Color(0xFFEAE3D4),
    surfaceContainerLow = Color(0xFFFBF8F1), surfaceContainerLowest = Color(0xFFFBF8F1),
)

/** Noche púrpura con el oro del sol de Vergina. */
private val helenoOscuro = darkColorScheme(
    primary = Color(0xFFD8B45C), onPrimary = Color(0xFF1C1405), primaryContainer = Color(0xFF3A2E14), onPrimaryContainer = Color(0xFFEFE9DC),
    secondary = Color(0xFF73B4E2), onSecondary = Color(0xFF04192A), secondaryContainer = Color(0xFF13324A), onSecondaryContainer = Color(0xFFEFE9DC),
    tertiary = Color(0xFFB992EA),
    background = Color(0xFF0E0B17), onBackground = Color(0xFFEFE9DC), surface = Color(0xFF161222), onSurface = Color(0xFFEFE9DC),
    surfaceVariant = Color(0xFF211B33), onSurfaceVariant = Color(0xFFAAA2BF), outline = Color(0xFF322A4A), outlineVariant = Color(0xFF322A4A),
    error = Color(0xFFE58C86),
    surfaceContainer = Color(0xFF161222), surfaceContainerHigh = Color(0xFF211B33), surfaceContainerHighest = Color(0xFF211B33),
    surfaceContainerLow = Color(0xFF161222), surfaceContainerLowest = Color(0xFF0E0B17),
)

private val tipoRomano = Typography(
    displaySmall = TextStyle(fontFamily = Letra.inscripcion, fontWeight = FontWeight.Normal, fontSize = 30.sp, lineHeight = 36.sp, letterSpacing = 2.sp),
    headlineMedium = TextStyle(fontFamily = Letra.inscripcion, fontWeight = FontWeight.Normal, fontSize = 27.sp, lineHeight = 33.sp, letterSpacing = 2.4.sp),
    headlineSmall = TextStyle(fontFamily = Letra.inscripcion, fontWeight = FontWeight.Normal, fontSize = 21.sp, lineHeight = 27.sp, letterSpacing = 1.6.sp),
    titleLarge = TextStyle(fontFamily = Letra.inscripcion, fontWeight = FontWeight.SemiBold, fontSize = 16.5.sp, lineHeight = 22.sp, letterSpacing = 2.2.sp),
    titleMedium = TextStyle(fontFamily = Letra.texto, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 21.sp),
    titleSmall = TextStyle(fontFamily = Letra.codigo, fontWeight = FontWeight.Medium, fontSize = 11.5.sp, lineHeight = 16.sp, letterSpacing = 0.2.sp),
    bodyLarge = TextStyle(fontFamily = Letra.texto, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Letra.texto, fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontFamily = Letra.texto, fontSize = 12.5.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = Letra.codigo, fontWeight = FontWeight.Medium, fontSize = 13.sp, letterSpacing = 0.6.sp),
    labelMedium = TextStyle(fontFamily = Letra.codigo, fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.4.sp),
    labelSmall = TextStyle(fontFamily = Letra.codigo, fontSize = 10.5.sp, letterSpacing = 0.4.sp),
)

/** Todo en monoespaciada: rótulos de terminal y texto de código. */
private val tipoCiber = Typography(
    displaySmall = TextStyle(fontFamily = Letra.terminal, fontSize = 30.sp, lineHeight = 36.sp, letterSpacing = 1.5.sp),
    headlineMedium = TextStyle(fontFamily = Letra.terminal, fontSize = 28.sp, lineHeight = 34.sp, letterSpacing = 1.6.sp),
    headlineSmall = TextStyle(fontFamily = Letra.terminal, fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = 1.2.sp),
    titleLarge = TextStyle(fontFamily = Letra.terminal, fontSize = 17.sp, lineHeight = 22.sp, letterSpacing = 1.2.sp),
    titleMedium = TextStyle(fontFamily = Letra.codigo, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp, lineHeight = 21.sp),
    titleSmall = TextStyle(fontFamily = Letra.codigo, fontWeight = FontWeight.Medium, fontSize = 11.5.sp, lineHeight = 16.sp, letterSpacing = 0.2.sp),
    bodyLarge = TextStyle(fontFamily = Letra.codigo, fontSize = 15.sp, lineHeight = 23.sp),
    bodyMedium = TextStyle(fontFamily = Letra.codigo, fontSize = 13.5.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = Letra.codigo, fontSize = 12.sp, lineHeight = 17.sp),
    labelLarge = TextStyle(fontFamily = Letra.codigo, fontWeight = FontWeight.Medium, fontSize = 13.sp, letterSpacing = 0.6.sp),
    labelMedium = TextStyle(fontFamily = Letra.codigo, fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.4.sp),
    labelSmall = TextStyle(fontFamily = Letra.codigo, fontSize = 10.5.sp, letterSpacing = 0.4.sp),
)

/** Garamond en todo: capitales griegas espaciadas en títulos, cifras alineadas en datos. */
private val tipoHeleno = Typography(
    displaySmall = TextStyle(fontFamily = Letra.garamond, fontWeight = FontWeight.Medium, fontSize = 32.sp, lineHeight = 38.sp, letterSpacing = 3.sp),
    headlineMedium = TextStyle(fontFamily = Letra.garamond, fontWeight = FontWeight.Medium, fontSize = 29.sp, lineHeight = 35.sp, letterSpacing = 3.2.sp),
    headlineSmall = TextStyle(fontFamily = Letra.garamond, fontWeight = FontWeight.Medium, fontSize = 23.sp, lineHeight = 29.sp, letterSpacing = 2.sp),
    titleLarge = TextStyle(fontFamily = Letra.garamond, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 23.sp, letterSpacing = 2.6.sp),
    titleMedium = TextStyle(fontFamily = Letra.garamond, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 22.sp, fontFeatureSettings = "lnum"),
    titleSmall = TextStyle(fontFamily = Letra.garamond, fontWeight = FontWeight.Medium, fontSize = 13.5.sp, lineHeight = 17.sp, letterSpacing = 0.6.sp, fontFeatureSettings = "lnum"),
    bodyLarge = TextStyle(fontFamily = Letra.garamond, fontSize = 18.sp, lineHeight = 26.sp),
    bodyMedium = TextStyle(fontFamily = Letra.garamond, fontSize = 16.sp, lineHeight = 22.sp),
    bodySmall = TextStyle(fontFamily = Letra.garamond, fontSize = 14.sp, lineHeight = 19.sp),
    labelLarge = TextStyle(fontFamily = Letra.garamond, fontWeight = FontWeight.Medium, fontSize = 15.sp, letterSpacing = 0.5.sp, fontFeatureSettings = "lnum, tnum"),
    labelMedium = TextStyle(fontFamily = Letra.garamond, fontWeight = FontWeight.Medium, fontSize = 13.sp, letterSpacing = 0.5.sp, fontFeatureSettings = "lnum, tnum"),
    labelSmall = TextStyle(fontFamily = Letra.garamond, fontSize = 12.5.sp, letterSpacing = 0.4.sp, fontFeatureSettings = "lnum, tnum"),
)

private fun formas(r: Int) = Shapes(
    extraSmall = RoundedCornerShape((r * 0.75f).dp), small = RoundedCornerShape(r.dp), medium = RoundedCornerShape((r * 1.5f).dp),
    large = RoundedCornerShape((r * 2).dp), extraLarge = RoundedCornerShape((r * 2.5f).dp),
)

@Composable
fun TemaVestigium(content: @Composable () -> Unit) {
    val tema = Estilo.tema
    val oscuro = tema.soloOscuro || isSystemInDarkTheme()
    val esquema = when (tema) {
        TemaId.ROMANO -> if (oscuro) romanoOscuro else romanoClaro
        TemaId.CIBER -> ciber
        TemaId.HELENO -> if (oscuro) helenoOscuro else helenoClaro
    }
    val tipo = when (tema) { TemaId.ROMANO -> tipoRomano; TemaId.CIBER -> tipoCiber; TemaId.HELENO -> tipoHeleno }
    val radio = when (tema) { TemaId.ROMANO -> 4; TemaId.CIBER -> 1; TemaId.HELENO -> 6 }
    // Íconos de la barra de estado legibles aunque el teléfono esté en modo claro y el tema sea oscuro.
    val vista = LocalView.current
    if (!vista.isInEditMode) SideEffect {
        (vista.context as? Activity)?.window?.let { w ->
            WindowCompat.getInsetsController(w, vista).apply { isAppearanceLightStatusBars = !oscuro; isAppearanceLightNavigationBars = !oscuro }
        }
    }
    val d = LocalDensity.current
    val escala = Repo.config.letra.coerceIn(0.8f, 1.4f)
    CompositionLocalProvider(LocalDensity provides Density(d.density, d.fontScale * escala)) {
        MaterialTheme(colorScheme = esquema, typography = tipo, shapes = formas(radio), content = content)
    }
}

@Composable
fun esOscuro(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f

/** Escalas de color para mapas de calor y gráficas, según el tema: bajo → medio → alto. */
object Tonos {
    private class Juego(val bajo: Color, val medio: Color, val alto: Color, val oro: Color)
    private fun juego(oscuro: Boolean): Juego = when (Estilo.tema) {
        TemaId.ROMANO -> if (oscuro) Juego(Color(0xFFA63A4D), Color(0xFF2A2F34), Color(0xFF3FA393), Color(0xFFD2AE5A))
            else Juego(Color(0xFF9E3445), Color(0xFFE4E2DA), Color(0xFF2D7A6F), Color(0xFFB8892B))
        TemaId.CIBER -> Juego(Color(0xFFFF2E88), Color(0xFF1A2232), Color(0xFF00E5FF), Color(0xFF00E5FF))
        TemaId.HELENO -> if (oscuro) Juego(Color(0xFFB04A6A), Color(0xFF2A2440), Color(0xFF5FA8D8), Color(0xFFD8B45C))
            else Juego(Color(0xFF8E2C48), Color(0xFFE9E3D6), Color(0xFF1F6E9C), Color(0xFFA27B22))
    }

    /** t entre 0 y 1. */
    fun divergente(t: Float, oscuro: Boolean): Color {
        val x = t.coerceIn(0f, 1f); val j = juego(oscuro)
        return if (x < 0.5f) lerp(j.bajo, j.medio, x * 2) else lerp(j.medio, j.alto, (x - 0.5f) * 2)
    }

    /** Intensidad de una sola dirección (para pasos, gasto, fotos…): de piedra al color fuerte del tema. */
    fun secuencial(t: Float, oscuro: Boolean): Color {
        val x = t.coerceIn(0f, 1f); val j = juego(oscuro)
        return lerp(j.medio, j.oro, x)
    }

    /** Escala de calor del mapa: poco → mucho. */
    fun calor(t: Float, oscuro: Boolean): Color {
        val x = t.coerceIn(0f, 1f)
        val (a, b, c) = when (Estilo.tema) {
            TemaId.ROMANO -> if (oscuro) Triple(Color(0xFF3FA393), Color(0xFFD2AE5A), Color(0xFFE0604F)) else Triple(Color(0xFF2D7A6F), Color(0xFFB8892B), Color(0xFF9E3445))
            TemaId.CIBER -> Triple(Color(0xFF00E5FF), Color(0xFFE8FF3A), Color(0xFFFF2E88))
            TemaId.HELENO -> if (oscuro) Triple(Color(0xFF5FA8D8), Color(0xFFD8B45C), Color(0xFFB992EA)) else Triple(Color(0xFF1F6E9C), Color(0xFFA27B22), Color(0xFF5A2A82))
        }
        return if (x < 0.5f) lerp(a, b, x * 2) else lerp(b, c, (x - 0.5f) * 2)
    }

    fun valencia(v: Double?, oscuro: Boolean): Color? = v?.let { divergente(((it - 1) / 6).toFloat(), oscuro) }

    /** Texto legible encima de un color de fondo. */
    fun sobre(c: Color): Color = if (c.luminance() > 0.45f) Color(0xFF17191C) else Color(0xFFF2EFE8)
}

/** Numerales romanos y nombres latinos de cada parte de la app. */
object Romano {
    fun de(n: Int): String {
        if (n <= 0) return "N"
        val v = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
        val s = arrayOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
        var x = n; val sb = StringBuilder()
        for (i in v.indices) while (x >= v[i]) { sb.append(s[i]); x -= v[i] }
        return sb.toString()
    }
    fun fecha(iso: String): String {
        val p = iso.split("-")
        return if (p.size < 3) iso else "${de(p[2].toInt())} · ${de(p[1].toInt())} · ${de(p[0].toInt())}"
    }

    private val LATIN = mapOf(
        "Día" to "DIES", "Momentos" to "MOMENTA", "Gastos" to "SVMPTVS", "Explorar" to "EXPLORATIO", "Más" to "CETERA",
        "Hábitos" to "CONSVETVDINES", "Cartas al futuro" to "EPISTVLAE", "Experimentos personales" to "EXPERIMENTA",
        "Guía científica" to "DOCTRINA", "Cuestionarios" to "QVAESTIONES", "Prueba de reacción" to "CELERITAS",
        "Lugares" to "LOCA", "Datos" to "TABVLARIVM", "Permisos y sensores" to "SENSVS", "Ajustes" to "ORDINATIO",
        "Reportes" to "RELATIONES", "Bienvenido" to "SALVE", "Calendario" to "CALENDARIVM", "Escalada" to "ASCENSVS",
        "Biblioteca" to "BIBLIOTHECA", "Tu año" to "ANNVS", "Privacidad" to "SECRETVM",
        // Secciones
        "Sueño" to "SOMNVS", "Contexto" to "CONTEXTVS", "Hitos" to "MEMORABILIA", "Ánimo y cuerpo" to "ANIMVS ET CORPVS",
        "Lectura del día" to "LECTIO DIEI", "Lo mejor y lo peor" to "OPTIMVM ET PESSIMVM", "Medicamentos" to "MEDICAMENTA",
        "Seguimiento privado" to "ARCANVM", "Diario" to "DIARIVM", "Predicción" to "PRAESAGIVM", "Momentos de este día" to "MOMENTA DIEI",
        "Este día hace un año" to "ANTE ANNVM", "Lo que registró el teléfono" to "VESTIGIA", "Registrar" to "SCRIBERE",
        "Ahora mismo estoy…" to "NVNC", "Últimos 30 días" to "TRIGINTA DIES", "Cobertura" to "CONSTANTIA",
        "Cruzar dos variables" to "COMPARATIO", "Las relaciones más marcadas" to "NEXVS", "¿Qué tan bien te conoces?" to "NOSCE TE IPSVM",
        "A qué hora aparecen tus emociones" to "HORAE", "Personas, temas e hitos" to "PERSONAE", "Lugares y ánimo" to "LOCA ET ANIMVS",
        "Medir" to "METIRI", "Entenderte" to "INTELLEGERE", "Sistema" to "MACHINA", "Mapa emocional" to "MAPPA",
        "Tus lugares" to "LOCA TVA", "Marcar dónde estoy ahora" to "HIC ET NVNC", "Resultado" to "EXITVS", "Historial" to "ANNALES",
        "Exportar" to "EXPORTARE", "Importar" to "IMPORTARE", "Respaldo automático" to "CVSTODIA", "Google Drive" to "ARCA",
        "Etiquetas equivalentes" to "AEQVIVALENTIA", "Categorías de gasto" to "GENERA", "Resumen" to "SVMMA",
        "Avisos" to "MONITA", "Ubicación" to "LOCVS", "Cuerpo" to "CORPVS", "Teléfono" to "TELEPHONVM", "Entorno" to "AMBITVS",
        "Generar ahora" to "FACERE", "Nuevo experimento" to "NOVVM", "Escribir una carta" to "SCRIBERE", "Selladas" to "SIGNATAE",
        "Activos" to "ACTIVAE", "Archivados" to "ARCHIVVM", "Disponibles" to "PARATA", "Cómo leer tus datos" to "LEGERE",
        "Marcos y referencias" to "FONTES", "Clima" to "CAELVM",
        "Fotos del día" to "IMAGINES", "Mapa" to "MAPPA", "Sesiones" to "EXERCITATIONES", "Nueva sesión" to "NOVA EXERCITATIO",
        "Progreso" to "PROGRESSVS", "Bloques" to "SAXA", "Cómo te sentiste" to "AFFECTVS", "Libros" to "LIBRI", "Películas" to "PELLICVLAE",
        "Series" to "SERIES", "Juegos" to "LVDI", "Leyendo ahora" to "IN MANIBVS", "Terminados" to "PERFECTA", "Pendientes" to "EXPECTANTIA",
        "Mes por mes" to "MENSES", "El año en píxeles" to "ANNVS IN PVNCTIS", "Lo más frecuente" to "FREQVENTISSIMA", "Este mes" to "HIC MENSIS",
        "Ir a una fecha" to "SALTVS", "Sincronizar" to "SYNCHRONIA", "Notificaciones y música" to "SONI", "Galería y agenda" to "IMAGINES ET FASTI",
        "Movimiento" to "MOTVS", "Música" to "MVSICA", "Agenda" to "FASTI", "Escuchado hoy" to "AVDITA", "Capas" to "STRATA",
        "Rutas del día" to "ITINERA", "Widgets" to "TABELLAE",
    )
    fun latin(titulo: String): String? = LATIN[titulo]

    val MESES = listOf("IANVARIVS", "FEBRVARIVS", "MARTIVS", "APRILIS", "MAIVS", "IVNIVS", "IVLIVS", "AVGVSTVS", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER")
    /** Días de la semana romanos, de lunes a domingo: de ahí vienen lunes (Lunae), martes (Martis), miércoles (Mercurii)… */
    val SEMANA = listOf("LVN", "MAR", "MER", "IOV", "VEN", "SAT", "SOL")

    fun mes(n: Int): String = MESES[(n - 1).coerceIn(0, 11)]

    /** Calendas (día 1), Nonas (5 o 7) e Idus (13 o 15): en marzo, mayo, julio y octubre caen más tarde. */
    fun marca(fecha: String): String? {
        val p = fecha.split("-"); if (p.size < 3) return null
        val m = p[1].toInt(); val d = p[2].toInt()
        val tarde = m == 3 || m == 5 || m == 7 || m == 10
        return when (d) {
            1 -> "KAL"
            (if (tarde) 7 else 5) -> "NON"
            (if (tarde) 15 else 13) -> "ID"
            else -> null
        }
    }
}

/** Muestra de un tema para elegirlo en Ajustes: fondo, título en su letra y sus colores. */
@Composable
fun VistaPreviaTema(tema: TemaId, oscuro: Boolean, elegido: Boolean, modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier, onClick: () -> Unit) {
    val o = tema.soloOscuro || oscuro
    val e = when (tema) {
        TemaId.ROMANO -> if (o) romanoOscuro else romanoClaro
        TemaId.CIBER -> ciber
        TemaId.HELENO -> if (o) helenoOscuro else helenoClaro
    }
    val t = when (tema) { TemaId.ROMANO -> tipoRomano; TemaId.CIBER -> tipoCiber; TemaId.HELENO -> tipoHeleno }
    val titulo = when (tema) { TemaId.ROMANO -> "DIES"; TemaId.CIBER -> "DÍA_"; TemaId.HELENO -> "ΗΜΕΡΑ" }
    val linea = when (tema) { TemaId.ROMANO -> "XVII · X"; TemaId.CIBER -> "2026.10.17"; TemaId.HELENO -> "ΙΖʹ ΥΠΕΡΒ." }
    androidx.compose.material3.Surface(
        onClick = onClick, modifier = modifier, color = e.background, shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(if (elegido) 2.dp else 1.dp, if (elegido) e.primary else e.outline),
    ) {
        androidx.compose.foundation.layout.Column(androidx.compose.ui.Modifier.padding(10.dp)) {
            androidx.compose.material3.Text(titulo, style = t.headlineSmall, color = e.onBackground, maxLines = 1)
            androidx.compose.material3.Text(linea, style = t.labelSmall, color = e.primary, maxLines = 1)
            androidx.compose.foundation.layout.Row(androidx.compose.ui.Modifier.padding(top = 8.dp)) {
                listOf(e.primary, e.secondary, e.tertiary, e.surfaceVariant).forEach { c ->
                    androidx.compose.foundation.layout.Box(androidx.compose.ui.Modifier.padding(end = 4.dp).size(14.dp).background(c, RoundedCornerShape(3.dp)))
                }
            }
            androidx.compose.material3.Text(tema.nombre + if (elegido) "  ✓" else "", style = t.titleSmall, color = e.onSurfaceVariant, modifier = androidx.compose.ui.Modifier.padding(top = 6.dp))
        }
    }
}
