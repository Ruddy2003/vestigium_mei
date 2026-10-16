package io.github.ruddy2003.vestigium.ui

import androidx.compose.foundation.isSystemInDarkTheme
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
}

object Paleta {
    // Marmor (claro)
    val marmol = Color(0xFFECEDEA); val marmolClaro = Color(0xFFF7F7F4); val marmolVeta = Color(0xFFE1E2DD)
    val tinta = Color(0xFF17191C); val tintaTenue = Color(0xFF5B6067); val veta = Color(0xFFC9CBC4)
    val porfido = Color(0xFF7C2433); val porfidoSuave = Color(0xFFEFDDDF)
    val bronce = Color(0xFF2D6B63); val bronceSuave = Color(0xFFD6E7E3)
    val oroOscuro = Color(0xFF8E6A1F)
    // Basaltum (oscuro)
    val basalto = Color(0xFF0E1113); val basaltoAlto = Color(0xFF161A1D); val basaltoVeta = Color(0xFF20252A)
    val marfil = Color(0xFFE7E3DA); val marfilTenue = Color(0xFF9AA0A6); val grieta = Color(0xFF2B3137)
    val oro = Color(0xFFD2AE5A); val oroSuave = Color(0xFF3A3120)
    val bronceClaro = Color(0xFF67BBAE); val bronceNoche = Color(0xFF1E3633)
}

private val claro = lightColorScheme(
    primary = Paleta.porfido, onPrimary = Color(0xFFFFF8F3), primaryContainer = Paleta.porfidoSuave, onPrimaryContainer = Paleta.tinta,
    secondary = Paleta.bronce, onSecondary = Color.White, secondaryContainer = Paleta.bronceSuave, onSecondaryContainer = Paleta.tinta,
    tertiary = Paleta.oroOscuro,
    background = Paleta.marmol, onBackground = Paleta.tinta, surface = Paleta.marmolClaro, onSurface = Paleta.tinta,
    surfaceVariant = Paleta.marmolVeta, onSurfaceVariant = Paleta.tintaTenue, outline = Paleta.veta, outlineVariant = Paleta.veta,
    error = Color(0xFFA3312F),
    surfaceContainer = Paleta.marmolClaro, surfaceContainerHigh = Paleta.marmolVeta, surfaceContainerHighest = Paleta.marmolVeta,
    surfaceContainerLow = Paleta.marmolClaro, surfaceContainerLowest = Paleta.marmolClaro,
)

private val oscuro = darkColorScheme(
    primary = Paleta.oro, onPrimary = Color(0xFF1A1405), primaryContainer = Paleta.oroSuave, onPrimaryContainer = Paleta.marfil,
    secondary = Paleta.bronceClaro, onSecondary = Color(0xFF07211D), secondaryContainer = Paleta.bronceNoche, onSecondaryContainer = Paleta.marfil,
    tertiary = Paleta.oro,
    background = Paleta.basalto, onBackground = Paleta.marfil, surface = Paleta.basaltoAlto, onSurface = Paleta.marfil,
    surfaceVariant = Paleta.basaltoVeta, onSurfaceVariant = Paleta.marfilTenue, outline = Paleta.grieta, outlineVariant = Paleta.grieta,
    error = Color(0xFFE58C86),
    surfaceContainer = Paleta.basaltoAlto, surfaceContainerHigh = Paleta.basaltoVeta, surfaceContainerHighest = Paleta.basaltoVeta,
    surfaceContainerLow = Paleta.basaltoAlto, surfaceContainerLowest = Paleta.basalto,
)

private val tipografia = Typography(
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

private val formas = Shapes(
    extraSmall = RoundedCornerShape(3.dp), small = RoundedCornerShape(4.dp), medium = RoundedCornerShape(6.dp),
    large = RoundedCornerShape(8.dp), extraLarge = RoundedCornerShape(10.dp),
)

@Composable
fun TemaVestigium(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) oscuro else claro, typography = tipografia, shapes = formas, content = content)
}

@Composable
fun esOscuro(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f

/** Escalas de color para mapas de calor: pórfido (bajo) → piedra (medio) → bronce verde (alto). */
object Tonos {
    private val bajoC = Color(0xFF9E3445); private val medioC = Color(0xFFE4E2DA); private val altoC = Color(0xFF2D7A6F)
    private val bajoO = Color(0xFFA63A4D); private val medioO = Color(0xFF2A2F34); private val altoO = Color(0xFF3FA393)
    private val oroC = Color(0xFFB8892B); private val oroO = Color(0xFFD2AE5A)

    /** t entre 0 y 1. */
    fun divergente(t: Float, oscuro: Boolean): Color {
        val x = t.coerceIn(0f, 1f)
        val (b, m, a) = if (oscuro) Triple(bajoO, medioO, altoO) else Triple(bajoC, medioC, altoC)
        return if (x < 0.5f) lerp(b, m, x * 2) else lerp(m, a, (x - 0.5f) * 2)
    }

    /** Intensidad de una sola dirección (para pasos, gasto, fotos…): de piedra a oro. */
    fun secuencial(t: Float, oscuro: Boolean): Color {
        val x = t.coerceIn(0f, 1f)
        return if (oscuro) lerp(medioO, oroO, x) else lerp(medioC, oroC, x)
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
