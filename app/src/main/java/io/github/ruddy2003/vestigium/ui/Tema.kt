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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
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
    /** El Messiri, de un tipógrafo egipcio: los títulos del tema Egipto (no trae jeroglíficos; esos los pone la letra del sistema). */
    val messiri = FontFamily(Font(R.font.messiri_medium, FontWeight.Normal), Font(R.font.messiri_medium, FontWeight.Medium), Font(R.font.messiri_bold, FontWeight.SemiBold), Font(R.font.messiri_bold, FontWeight.Bold))
    /** Escriba (Gentium Book Plus renombrada): el texto del tema Egipto, con las letras de la transliteración egiptológica (ꜣ, ꜥ, ḥ, š…). */
    val escriba = FontFamily(Font(R.font.escriba_regular, FontWeight.Normal), Font(R.font.escriba_bold, FontWeight.SemiBold), Font(R.font.escriba_bold, FontWeight.Bold))
    /** Share Tech Mono: rótulos de consola para el tema Terminal. */
    val terminal = FontFamily(Font(R.font.sharetech_mono, FontWeight.Normal))
    /** Chakra Petch: la letra cuadrada de los letreros de Ciudad neón. */
    val letrero = FontFamily(Font(R.font.chakra_regular, FontWeight.Normal), Font(R.font.chakra_semibold, FontWeight.SemiBold))
    /** Titillium Web (de la Academia de Bellas Artes de Urbino): la letra de las placas y los letreros de Roma de hoy. */
    val placa = FontFamily(Font(R.font.titillium_regular, FontWeight.Normal), Font(R.font.titillium_semibold, FontWeight.Medium), Font(R.font.titillium_semibold, FontWeight.SemiBold), Font(R.font.titillium_bold, FontWeight.Bold))
    /** Newsreader: letra de periódico para leer párrafos, como el diario que se compra en el quiosco de la esquina. */
    val cronaca = FontFamily(Font(R.font.newsreader_regular, FontWeight.Normal), Font(R.font.newsreader_semibold, FontWeight.Medium), Font(R.font.newsreader_semibold, FontWeight.SemiBold))
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

/**
 * Las épocas en que se agrupan los temas en el selector y en las fichas de la rotación (plan de diseño, lote 8e).
 * Solo ordenan la lista: no se guardan.
 */
enum class Epoca(val nombre: String) {
    ANTIGUEDAD("Prehistoria y Antigüedad"),
    EDAD_MEDIA("Edad Media"),
    EDAD_MODERNA("Edad Moderna"),
    CONTEMPORANEA("Siglos XIX a XXI"),
    FUTURO("Futuro"),
    FANTASIA("Fantasía"),
}

/** Los temas. Terminal (id «ciber») solo existe en oscuro. El orden de la lista es el del selector dentro de cada [Epoca]. */
enum class TemaId(val id: String, val nombre: String, val descripcion: String, val epoca: Epoca, val soloOscuro: Boolean = false) {
    ROMANO("romano", "Romano", "Mármol, pórfido y oro. Títulos en latín, fechas romanas, corona de laurel.", Epoca.ANTIGUEDAD),
    URBE("urbe", "Roma", "La Roma de hoy, en capas: placas de mármol en las esquinas, ocre y travertino, cúpulas, fuentes y motonetas entre las ruinas. Fecha en italiano con el año de la fundación, y los rioni.", Epoca.CONTEMPORANEA),
    CIBER("ciber", "Terminal", "Pura consola: cian sobre negro, cursor parpadeante, ASCII y volcados de memoria. Todo en código. Solo oscuro.", Epoca.CONTEMPORANEA, soloOscuro = true),
    NEON("neon", "Ciudad neón", "Los niveles bajos de una megaciudad: letreros de neón entre el vapor, puestos de comida y callejones. Magenta, ámbar y verde de sodio. Solo oscuro.", Epoca.FUTURO, soloOscuro = true),
    HELENO("heleno", "Helénico", "Púrpura macedonio, oro y azul del Egeo. Títulos en griego, el sol de Vergina.", Epoca.ANTIGUEDAD),
    /** Egipto faraónico. Su id «egipto» coincide con el de una vida de `OtrasVidas.BASE` (Ipy): son espacios de nombres distintos (`TemaId` frente a `porId`) y no chocan. */
    EGIPTO("egipto", "Egipto", "Papiro, lapislázuli, oro y cornalina. El ojo protector, cartuchos, numerales jeroglíficos y el año civil de tres estaciones.", Epoca.ANTIGUEDAD),
    MEDIEVAL("medieval", "Scriptorium", "Pergamino, tinta y pan de oro. Letra gótica, capitulares en rojo, numerales de copista.", Epoca.EDAD_MEDIA),
    TEMPLARIO("templario", "Templario", "Cota de malla, sobreveste blanca con la cruz roja y un castillo de frontera entre la arena. Calendario juliano y vigilias de la noche.", Epoca.EDAD_MEDIA),
    DISTOPIA("distopia", "Distopía", "Gris de ministerio y rojo de cartel. Cada día es un expediente y un ojo lo mira todo.", Epoca.FUTURO),
    REFUGIO("refugio", "Refugio", "Optimismo atómico de día, fósforo verde de noche. La vida bajo tierra, en orden.", Epoca.FUTURO),
    ORDEN("orden", "Orden", "Acero, carmesí y latón. Caballeros que custodian el saber técnico. Letra de estarcido.", Epoca.FUTURO),
    LABORATORIO("laboratorio", "Laboratorio", "Blanco clínico, cian y violeta. Un laboratorio silencioso donde todo se mide.", Epoca.FUTURO),
    COLONOS("colonos", "Colonos", "Papel de gaceta, azul marino y ladrillo. Vecinos que reconstruyen su pueblo.", Epoca.FUTURO),
    NAPOLEON("napoleon", "Napoleónico", "El Imperio (1804–1815): azul imperial, oro y carmesí, letra Didot. Boletines de la Grande Armée y fechas en francés.", Epoca.CONTEMPORANEA),
    BOTANICO("botanico", "Botánico", "El cuaderno de un naturalista viajero: escarabajos clavados con alfiler, hileras de hormigas y helechos a tinta sepia, verde musgo y óxido. Fecha según la estación.", Epoca.CONTEMPORANEA),
    RUINAS("ruinas", "Ciudad verde", "Una ciudad que el bosque recuperó: hormigón agrietado, hiedra, musgo y óxido. Los días se cuentan desde el silencio, con la letra de una máquina de escribir rescatada.", Epoca.FUTURO),
    NAVIDAD("navidad", "Navidad", "Rojo, verde pino y oro, con nieve. Cartas junto al fuego y la cuenta hacia el 25 de diciembre.", Epoca.CONTEMPORANEA),
    SEDA("seda", "Ruta de la Seda", "Seda, lapislázuli, cinabrio y oro. Monedas con agujero, caravanas y números chinos.", Epoca.EDAD_MEDIA),
    GOTICO("gotico", "Gótico", "Una capilla de vitrales: azul de bóveda con estrellas de oro, cristal rubí y zafiro, letra textura. Horas canónicas.", Epoca.EDAD_MEDIA),
    ESPACIO("espacio", "Espacio", "Estrellas, nebulosas y la sala de control. Fecha en día juliano, como en los observatorios.", Epoca.FUTURO),
    ABISMO("abismo", "Abismo", "La bajada de un batiscafo: de día, el azul de la zona iluminada; de noche, la zona abisal y su luz viva. Sonar, medusas y la racha en metros de profundidad.", Epoca.CONTEMPORANEA),
    ANDALUS("andalus", "Al-Ándalus", "Yeserías, azulejos de lazo y oro de la Alhambra. Títulos en árabe y fecha del calendario hégira.", Epoca.EDAD_MEDIA),
    MONGOL("mongol", "Mongol", "El Cielo Azul Eterno, fieltro y oro de la estepa. Títulos en mongol y el año del animal.", Epoca.EDAD_MEDIA),
    RENACIMIENTO("renacimiento", "Renacimiento", "Los cuadernos de Leonardo: tinta sepia, sanguina y trazos de construcción. Títulos en italiano.", Epoca.EDAD_MODERNA),
    PIRATA("pirata", "Pirata", "Carta náutica, brea, lona y latón, en sepia sobre pergamino húmedo. Cada día es una singladura de la bitácora y las horas se cuentan por campanadas.", Epoca.EDAD_MODERNA),
    URSS("urss", "Soviético", "Rojo, negro y papel de cartel, en estilo constructivista. Planes quinquenales y meses en ruso.", Epoca.CONTEMPORANEA),
    REVOLUCION("revolucion", "Revolución francesa", "Papel de cartel, tinta y la escarapela azul, blanca y roja. Cuadernos de quejas, el árbol de la libertad y el calendario republicano.", Epoca.EDAD_MODERNA),
    ANTIGUAS("antiguas", "Tierras Antiguas", "Salones de piedra, runas inventadas, mapas y gemas. Una compañía que cruza aldeas, bosques, montañas y minas, contando el tiempo por lunas.", Epoca.FANTASIA),
}

internal val romanoClaro = lightColorScheme(
    primary = Paleta.porfido, onPrimary = Color(0xFFFFF8F3), primaryContainer = Paleta.porfidoSuave, onPrimaryContainer = Paleta.tinta,
    secondary = Paleta.bronce, onSecondary = Color.White, secondaryContainer = Paleta.bronceSuave, onSecondaryContainer = Paleta.tinta,
    tertiary = Paleta.oroOscuro,
    background = Paleta.marmol, onBackground = Paleta.tinta, surface = Paleta.marmolClaro, onSurface = Paleta.tinta,
    surfaceVariant = Paleta.marmolVeta, onSurfaceVariant = Paleta.tintaTenue, outline = Paleta.veta, outlineVariant = Paleta.veta,
    error = Color(0xFFA3312F),
    surfaceContainer = Paleta.marmolClaro, surfaceContainerHigh = Paleta.marmolVeta, surfaceContainerHighest = Paleta.marmolVeta,
    surfaceContainerLow = Paleta.marmolClaro, surfaceContainerLowest = Paleta.marmolClaro,
).conInversos()

internal val romanoOscuro = darkColorScheme(
    primary = Paleta.oro, onPrimary = Color(0xFF1A1405), primaryContainer = Paleta.oroSuave, onPrimaryContainer = Paleta.marfil,
    secondary = Paleta.bronceClaro, onSecondary = Color(0xFF07211D), secondaryContainer = Paleta.bronceNoche, onSecondaryContainer = Paleta.marfil,
    tertiary = Paleta.oro,
    background = Paleta.basalto, onBackground = Paleta.marfil, surface = Paleta.basaltoAlto, onSurface = Paleta.marfil,
    surfaceVariant = Paleta.basaltoVeta, onSurfaceVariant = Paleta.marfilTenue, outline = Paleta.grieta, outlineVariant = Paleta.grieta,
    error = Color(0xFFE58C86),
    surfaceContainer = Paleta.basaltoAlto, surfaceContainerHigh = Paleta.basaltoVeta, surfaceContainerHighest = Paleta.basaltoVeta,
    surfaceContainerLow = Paleta.basaltoAlto, surfaceContainerLowest = Paleta.basalto,
).conInversos()

/** Consola monocroma: cian de fósforo en tres intensidades sobre negro. El error es el único color que no es cian. */
internal val ciber = darkColorScheme(
    primary = Color(0xFF00E5FF), onPrimary = Color(0xFF001417), primaryContainer = Color(0xFF04282E), onPrimaryContainer = Color(0xFFBFF8FF),
    secondary = Color(0xFF5FC4D2), onSecondary = Color(0xFF001417), secondaryContainer = Color(0xFF0A1D21), onSecondaryContainer = Color(0xFFA6E7EF),
    tertiary = Color(0xFFC8FBFF),
    background = Color(0xFF020506), onBackground = Color(0xFFBDEFF5), surface = Color(0xFF060E10), onSurface = Color(0xFFBDEFF5),
    surfaceVariant = Color(0xFF0B191C), onSurfaceVariant = Color(0xFF6FA3AB), outline = Color(0xFF1A464D), outlineVariant = Color(0xFF12333A),
    error = Color(0xFFFF8F8F),
    surfaceContainer = Color(0xFF060E10), surfaceContainerHigh = Color(0xFF0B191C), surfaceContainerHighest = Color(0xFF0F2125),
    surfaceContainerLow = Color(0xFF040A0B), surfaceContainerLowest = Color(0xFF020506),
).conInversos()

/** Asfalto mojado de noche bajo letreros: magenta de neón, ámbar de farol y verde de sodio, entre vapor violáceo. */
internal val neon = darkColorScheme(
    primary = Color(0xFFFF3D9A), onPrimary = Color(0xFF1C0010), primaryContainer = Color(0xFF3A0D27), onPrimaryContainer = Color(0xFFFFD3E8),
    secondary = Color(0xFFFFB238), onSecondary = Color(0xFF1F1300), secondaryContainer = Color(0xFF35240A), onSecondaryContainer = Color(0xFFFFE1B0),
    tertiary = Color(0xFF8CFF5A),
    background = Color(0xFF0C0811), onBackground = Color(0xFFF4E7F0), surface = Color(0xFF150E1B), onSurface = Color(0xFFF4E7F0),
    surfaceVariant = Color(0xFF221729), onSurfaceVariant = Color(0xFFB99DB2), outline = Color(0xFF4B2B47), outlineVariant = Color(0xFF33203A),
    error = Color(0xFFFF6B5E),
    surfaceContainer = Color(0xFF150E1B), surfaceContainerHigh = Color(0xFF221729), surfaceContainerHighest = Color(0xFF2A1C33),
    surfaceContainerLow = Color(0xFF110B16), surfaceContainerLowest = Color(0xFF0C0811),
).conInversos()

/** Pergamino y mármol con púrpura macedonio y azul del Egeo. */
internal val helenoClaro = lightColorScheme(
    primary = Color(0xFF5A2A82), onPrimary = Color.White, primaryContainer = Color(0xFFEADCF5), onPrimaryContainer = Color(0xFF1E1A2B),
    secondary = Color(0xFF1E5F8C), onSecondary = Color.White, secondaryContainer = Color(0xFFD7E7F3), onSecondaryContainer = Color(0xFF1E1A2B),
    tertiary = Color(0xFFA27B22),
    background = Color(0xFFF3EFE6), onBackground = Color(0xFF1E1A2B), surface = Color(0xFFFBF8F1), onSurface = Color(0xFF1E1A2B),
    surfaceVariant = Color(0xFFEAE3D4), onSurfaceVariant = Color(0xFF575066), outline = Color(0xFFD3C8B3), outlineVariant = Color(0xFFD3C8B3),
    error = Color(0xFFA3312F),
    surfaceContainer = Color(0xFFFBF8F1), surfaceContainerHigh = Color(0xFFEAE3D4), surfaceContainerHighest = Color(0xFFEAE3D4),
    surfaceContainerLow = Color(0xFFFBF8F1), surfaceContainerLowest = Color(0xFFFBF8F1),
).conInversos()

/** Noche púrpura con el oro del sol de Vergina. */
internal val helenoOscuro = darkColorScheme(
    primary = Color(0xFFD8B45C), onPrimary = Color(0xFF1C1405), primaryContainer = Color(0xFF2B2245), onPrimaryContainer = Color(0xFFEFE9DC),
    secondary = Color(0xFF73B4E2), onSecondary = Color(0xFF04192A), secondaryContainer = Color(0xFF13324A), onSecondaryContainer = Color(0xFFEFE9DC),
    tertiary = Color(0xFFB992EA),
    background = Color(0xFF0E0B17), onBackground = Color(0xFFEFE9DC), surface = Color(0xFF161222), onSurface = Color(0xFFEFE9DC),
    surfaceVariant = Color(0xFF211B33), onSurfaceVariant = Color(0xFFAAA2BF), outline = Color(0xFF322A4A), outlineVariant = Color(0xFF322A4A),
    error = Color(0xFFE58C86),
    surfaceContainer = Color(0xFF161222), surfaceContainerHigh = Color(0xFF211B33), surfaceContainerHighest = Color(0xFF211B33),
    surfaceContainerLow = Color(0xFF161222), surfaceContainerLowest = Color(0xFF0E0B17),
).conInversos()

/** Papiro de día: tinta de carbón, lapislázuli, cornalina y oro sobre la hoja amarilla. */
internal val egiptoClaro = lightColorScheme(
    primary = Color(0xFF1D2F7A), onPrimary = Color.White, primaryContainer = Color(0xFFD9DDF0), onPrimaryContainer = Color(0xFF231A0E),
    secondary = Color(0xFFB8471E), onSecondary = Color.White, secondaryContainer = Color(0xFFF2D6C2), onSecondaryContainer = Color(0xFF231A0E),
    tertiary = Color(0xFF9A7212),
    background = Color(0xFFE8D6A0), onBackground = Color(0xFF231A0E), surface = Color(0xFFF2E5BE), onSurface = Color(0xFF231A0E),
    surfaceVariant = Color(0xFFDDC88C), onSurfaceVariant = Color(0xFF5E4C2C), outline = Color(0xFFC2A867), outlineVariant = Color(0xFFC2A867),
    error = Color(0xFF9E2B25),
    surfaceContainer = Color(0xFFF2E5BE), surfaceContainerHigh = Color(0xFFDDC88C), surfaceContainerHighest = Color(0xFFDDC88C),
    surfaceContainerLow = Color(0xFFF2E5BE), surfaceContainerLowest = Color(0xFFF2E5BE),
).conInversos()

/** La tumba a la luz de la lámpara: negro cálido de hollín, oro, lapislázuli claro y cornalina. */
internal val egiptoOscuro = darkColorScheme(
    primary = Color(0xFFE3B341), onPrimary = Color(0xFF1E1503), primaryContainer = Color(0xFF3B2C0E), onPrimaryContainer = Color(0xFFF1E4C0),
    secondary = Color(0xFF7895F0), onSecondary = Color(0xFF0A1236), secondaryContainer = Color(0xFF1C2659), onSecondaryContainer = Color(0xFFF1E4C0),
    tertiary = Color(0xFFE8774E),
    background = Color(0xFF140E0A), onBackground = Color(0xFFF1E4C0), surface = Color(0xFF1D1610), onSurface = Color(0xFFF1E4C0),
    surfaceVariant = Color(0xFF2A2017), onSurfaceVariant = Color(0xFFB9A887), outline = Color(0xFF3F3121), outlineVariant = Color(0xFF3F3121),
    error = Color(0xFFE58C86),
    surfaceContainer = Color(0xFF1D1610), surfaceContainerHigh = Color(0xFF2A2017), surfaceContainerHighest = Color(0xFF2A2017),
    surfaceContainerLow = Color(0xFF1D1610), surfaceContainerLowest = Color(0xFF140E0A),
).conInversos()

/**
 * Roma de hoy. De día, travertino y revoque ocre con el rojo pompeyano de las fachadas, el verde de las persianas y el
 * oro de la ocra; de noche, los sampietrini bajo las farolas de sodio, con el travertino que brilla tibio.
 */
internal val urbeClaro = esquema(false, 0xFFEFE6D3, 0xFFF8F1E2, 0xFFE6D9BF, 0xFFCDBB98, 0xFF2B2420, 0xFF6A5D51,
    0xFFA8432A, 0xFFFFF7EE, 0xFFF3D9C9, 0xFF2B2420, 0xFF3D5E45, 0xFFD5E2D3, 0xFFB98322, 0xFF9B1C1C)
internal val urbeOscuro = esquema(true, 0xFF171412, 0xFF201B18, 0xFF2B2420, 0xFF433830, 0xFFF1E6D2, 0xFFB5A794,
    0xFFE8955A, 0xFF2A1405, 0xFF4A2A18, 0xFFF6E6D2, 0xFF8DB596, 0xFF1F3325, 0xFFE3C07A, 0xFFFF8A7A)

/** Titillium en los títulos, espaciada como los nombres de las calles; Newsreader para leer; Titillium otra vez en los datos. */
internal val tipoUrbe = tipografia(Letra.placa, FontWeight.SemiBold, 1.04f, 1.3f, Letra.cronaca, 1.06f, Letra.placa, 1.02f, cifrasAlineadas = true)

internal val tipoRomano = Typography(
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
internal val tipoCiber = Typography(
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

/** Chakra Petch en todo: títulos en mayúsculas anchas, como letreros, con un halo de neón; texto cuadrado y legible. */
internal val tipoNeon = Typography(
    displaySmall = TextStyle(fontFamily = Letra.letrero, fontWeight = FontWeight.SemiBold, fontSize = 30.sp, lineHeight = 36.sp, letterSpacing = 2.sp,
        shadow = Shadow(Color(0xCCFF3D9A), blurRadius = 18f)),
    headlineMedium = TextStyle(fontFamily = Letra.letrero, fontWeight = FontWeight.SemiBold, fontSize = 27.sp, lineHeight = 33.sp, letterSpacing = 2.sp,
        shadow = Shadow(Color(0xB3FF3D9A), blurRadius = 14f)),
    headlineSmall = TextStyle(fontFamily = Letra.letrero, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = 1.6.sp),
    titleLarge = TextStyle(fontFamily = Letra.letrero, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 23.sp, letterSpacing = 1.2.sp),
    titleMedium = TextStyle(fontFamily = Letra.letrero, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 21.sp, letterSpacing = 0.4.sp),
    titleSmall = TextStyle(fontFamily = Letra.letrero, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.8.sp),
    bodyLarge = TextStyle(fontFamily = Letra.letrero, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Letra.letrero, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = Letra.letrero, fontSize = 12.5.sp, lineHeight = 17.sp),
    labelLarge = TextStyle(fontFamily = Letra.letrero, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, letterSpacing = 1.sp),
    labelMedium = TextStyle(fontFamily = Letra.letrero, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, letterSpacing = 0.8.sp),
    labelSmall = TextStyle(fontFamily = Letra.letrero, fontSize = 10.5.sp, letterSpacing = 0.6.sp),
)

/** Garamond en todo: capitales griegas espaciadas en títulos, cifras alineadas en datos. */
internal val tipoHeleno = Typography(
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

/** El Messiri en los títulos, sin espaciar, como un friso; Escriba (Gentium) en el texto y en los datos, con cifras alineadas. */
internal val tipoEgipto = Typography(
    displaySmall = TextStyle(fontFamily = Letra.messiri, fontWeight = FontWeight.Medium, fontSize = 31.sp, lineHeight = 38.sp, letterSpacing = 0.6.sp),
    headlineMedium = TextStyle(fontFamily = Letra.messiri, fontWeight = FontWeight.Medium, fontSize = 28.sp, lineHeight = 35.sp, letterSpacing = 0.5.sp),
    headlineSmall = TextStyle(fontFamily = Letra.messiri, fontWeight = FontWeight.Medium, fontSize = 22.sp, lineHeight = 29.sp, letterSpacing = 0.4.sp),
    titleLarge = TextStyle(fontFamily = Letra.messiri, fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 23.sp, letterSpacing = 0.8.sp),
    titleMedium = TextStyle(fontFamily = Letra.escriba, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontFamily = Letra.messiri, fontWeight = FontWeight.Medium, fontSize = 12.5.sp, lineHeight = 17.sp, letterSpacing = 0.5.sp),
    bodyLarge = TextStyle(fontFamily = Letra.escriba, fontSize = 17.sp, lineHeight = 26.sp),
    bodyMedium = TextStyle(fontFamily = Letra.escriba, fontSize = 15.sp, lineHeight = 22.sp),
    bodySmall = TextStyle(fontFamily = Letra.escriba, fontSize = 13.sp, lineHeight = 19.sp),
    labelLarge = TextStyle(fontFamily = Letra.messiri, fontWeight = FontWeight.Medium, fontSize = 14.sp, letterSpacing = 0.4.sp, fontFeatureSettings = "lnum, tnum"),
    labelMedium = TextStyle(fontFamily = Letra.messiri, fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 0.4.sp, fontFeatureSettings = "lnum, tnum"),
    labelSmall = TextStyle(fontFamily = Letra.escriba, fontSize = 12.sp, letterSpacing = 0.3.sp, fontFeatureSettings = "lnum, tnum"),
)

/**
 * Cómo dobla la esquina de las tarjetas, botones y diálogos de un tema: [REDONDA] (las cuatro iguales), [CORTADA] (en
 * chaflán, como una carpeta o una placa de acero), [HOJA] (dos esquinas opuestas redondas y dos casi rectas, como una hoja
 * o una página) y [ARCO] (redondas arriba y casi rectas abajo, como un arco o una ventana).
 */
enum class Esquina { REDONDA, CORTADA, HOJA, ARCO }

/** El radio (en dp, el de la forma «small») y la forma de las esquinas de un tema. Ningún tema repite la pareja. */
data class Esquinas(val radio: Int, val forma: Esquina)

private fun forma(e: Esquinas, k: Float): androidx.compose.foundation.shape.CornerBasedShape {
    val r = (e.radio * k).dp
    val poco = (e.radio * k * 0.25f).dp
    return when (e.forma) {
        Esquina.REDONDA -> RoundedCornerShape(r)
        Esquina.CORTADA -> CutCornerShape(r)
        Esquina.HOJA -> RoundedCornerShape(topStart = r, topEnd = poco, bottomEnd = r, bottomStart = poco)
        Esquina.ARCO -> RoundedCornerShape(topStart = r, topEnd = r, bottomEnd = poco, bottomStart = poco)
    }
}

private fun formas(e: Esquinas) = Shapes(
    extraSmall = forma(e, 0.75f), small = forma(e, 1f), medium = forma(e, 1.5f), large = forma(e, 2f), extraLarge = forma(e, 2.5f),
)

/** Colores, letra y redondez de cada tema. */
internal fun esquemaDe(tema: TemaId, oscuro: Boolean) = when (tema) {
    TemaId.ROMANO -> if (oscuro) romanoOscuro else romanoClaro
    TemaId.URBE -> if (oscuro) urbeOscuro else urbeClaro
    TemaId.CIBER -> ciber
    TemaId.NEON -> neon
    TemaId.HELENO -> if (oscuro) helenoOscuro else helenoClaro
    TemaId.EGIPTO -> if (oscuro) egiptoOscuro else egiptoClaro
    TemaId.MEDIEVAL -> if (oscuro) medievalOscuro else medievalClaro
    TemaId.TEMPLARIO -> if (oscuro) templarioOscuro else templarioClaro
    TemaId.DISTOPIA -> if (oscuro) distopiaOscuro else distopiaClaro
    TemaId.REFUGIO -> if (oscuro) refugioOscuro else refugioClaro
    TemaId.ORDEN -> if (oscuro) ordenOscuro else ordenClaro
    TemaId.LABORATORIO -> if (oscuro) laboratorioOscuro else laboratorioClaro
    TemaId.COLONOS -> if (oscuro) colonosOscuro else colonosClaro
    TemaId.NAPOLEON -> if (oscuro) napoleonOscuro else napoleonClaro
    TemaId.BOTANICO -> if (oscuro) botanicoOscuro else botanicoClaro
    TemaId.RUINAS -> if (oscuro) ruinasOscuro else ruinasClaro
    TemaId.NAVIDAD -> if (oscuro) navidadOscuro else navidadClaro
    TemaId.SEDA -> if (oscuro) sedaOscuro else sedaClaro
    TemaId.URSS -> if (oscuro) urssOscuro else urssClaro
    TemaId.GOTICO -> if (oscuro) goticoOscuro else goticoClaro
    TemaId.ESPACIO -> if (oscuro) espacioOscuro else espacioClaro
    TemaId.ABISMO -> if (oscuro) abismoOscuro else abismoClaro
    TemaId.ANDALUS -> if (oscuro) andalusOscuro else andalusClaro
    TemaId.MONGOL -> if (oscuro) mongolOscuro else mongolClaro
    TemaId.RENACIMIENTO -> if (oscuro) renacimientoOscuro else renacimientoClaro
    TemaId.PIRATA -> if (oscuro) pirataOscuro else pirataClaro
    TemaId.REVOLUCION -> if (oscuro) revolucionOscuro else revolucionClaro
    TemaId.ANTIGUAS -> if (oscuro) antiguasOscuro else antiguasClaro
}

internal fun tipoDe(tema: TemaId, oscuro: Boolean): Typography = when (tema) {
    TemaId.ROMANO -> tipoRomano
    TemaId.URBE -> tipoUrbe
    TemaId.CIBER -> tipoCiber
    TemaId.NEON -> tipoNeon
    TemaId.HELENO -> tipoHeleno
    TemaId.EGIPTO -> tipoEgipto
    TemaId.MEDIEVAL -> tipoMedieval
    TemaId.TEMPLARIO -> tipoTemplario
    TemaId.DISTOPIA -> tipoDistopia
    TemaId.REFUGIO -> if (oscuro) tipoRefugioOscuro else tipoRefugioClaro
    TemaId.ORDEN -> tipoOrden
    TemaId.LABORATORIO -> tipoLaboratorio
    TemaId.COLONOS -> tipoColonos
    TemaId.NAPOLEON -> tipoNapoleon
    TemaId.BOTANICO -> tipoBotanico
    TemaId.RUINAS -> tipoRuinas
    TemaId.NAVIDAD -> tipoNavidad
    TemaId.SEDA -> tipoSeda
    TemaId.URSS -> tipoUrss
    TemaId.GOTICO -> tipoGotico
    TemaId.ESPACIO -> if (oscuro) tipoEspacioOscuro else tipoEspacio
    TemaId.ABISMO -> if (oscuro) tipoAbismoOscuro else tipoAbismo
    TemaId.ANDALUS -> tipoAndalus
    TemaId.MONGOL -> tipoMongol
    TemaId.RENACIMIENTO -> tipoRenacimiento
    TemaId.PIRATA -> tipoPirata
    TemaId.REVOLUCION -> tipoRevolucion
    TemaId.ANTIGUAS -> tipoAntiguas
}.conReloj(tema)

/**
 * La letra grande (`displayLarge`): la del reloj de Enfoque y la del año en «Tu año». Es la letra de título del tema
 * (familia, peso y, si lo tiene, el resplandor de su `displaySmall`) con cifras de ancho fijo, para que el reloj no
 * baile al pasar los segundos. El tamaño, en sp, compensa lo ancha o angosta que es cada letra: «25:00» ocupa más o
 * menos lo mismo en todos los temas. El segundo número es el espaciado entre letras, en sp.
 */
private fun letraReloj(tema: TemaId): Pair<Float, Float> = when (tema) {
    TemaId.ROMANO -> 58f to 1f          // Cinzel: capitales anchas
    TemaId.URBE -> 64f to 0.5f          // Titillium, semiangosta
    TemaId.CIBER -> 62f to 0f           // Share Tech Mono
    TemaId.NEON -> 58f to 2f           // Chakra Petch: cuadrada y ancha
    TemaId.HELENO -> 66f to 1f          // Garamond, cifras alineadas
    TemaId.EGIPTO -> 60f to 1.5f        // El Messiri, redonda y abierta
    TemaId.MEDIEVAL -> 66f to 0.5f      // gótica
    TemaId.TEMPLARIO -> 60f to 1.2f      // letra de estandarte (MedievalSharp)
    TemaId.DISTOPIA -> 70f to 1f        // Oswald, angosta
    TemaId.REFUGIO -> 74f to 0f         // VT323, de píxeles
    TemaId.ORDEN -> 60f to 1.5f         // estarcido
    TemaId.LABORATORIO -> 58f to 1f     // Jost, geométrica
    TemaId.COLONOS -> 64f to 0.5f       // Fell
    TemaId.NAPOLEON -> 64f to 1f        // Bodoni
    TemaId.BOTANICO -> 62f to 0f        // Fraunces
    TemaId.RUINAS -> 64f to 1f        // Letrero (Londrina), condensada
    TemaId.NAVIDAD -> 54f to 1f         // Cinzel Decorative, muy ancha
    TemaId.SEDA -> 64f to 0.5f          // Almendra
    TemaId.GOTICO -> 66f to 0.5f        // textura
    TemaId.ESPACIO -> 48f to 2f         // Orbitron, ancha y gruesa
    TemaId.ABISMO -> 70f to 0.5f        // Barlow Condensed, angosta como un rótulo de instrumento
    TemaId.ANDALUS -> 60f to 0.5f       // Kufi
    TemaId.MONGOL -> 62f to 1f          // Filosofo
    TemaId.RENACIMIENTO -> 88f to 0f    // Italianno, caligráfica y chica
    TemaId.PIRATA -> 72f to 1f          // Corsario (Pirata One), letra negra angosta
    TemaId.URSS -> 56f to 1f            // Russo One, ancha
    TemaId.REVOLUCION -> 60f to 1f      // Convención (Playfair)
    TemaId.ANTIGUAS -> 56f to 1f        // uncial del camino
}

private fun Typography.conReloj(tema: TemaId): Typography {
    val (tam, esp) = letraReloj(tema)
    return copy(displayLarge = displaySmall.copy(fontSize = tam.sp, lineHeight = (tam * 1.15f).sp, letterSpacing = esp.sp, fontFeatureSettings = "lnum, tnum"))
}

internal fun radioDe(tema: TemaId): Esquinas = when (tema) {
    TemaId.ROMANO -> Esquinas(4, Esquina.REDONDA)
    TemaId.URBE -> Esquinas(6, Esquina.ARCO)               // el arco de un portón romano
    TemaId.CIBER -> Esquinas(1, Esquina.REDONDA)
    TemaId.NEON -> Esquinas(10, Esquina.CORTADA)          // la placa del letrero
    TemaId.HELENO -> Esquinas(6, Esquina.REDONDA)
    TemaId.EGIPTO -> Esquinas(11, Esquina.ARCO)            // la estela de remate redondo
    TemaId.MEDIEVAL -> Esquinas(3, Esquina.REDONDA)
    TemaId.TEMPLARIO -> Esquinas(5, Esquina.CORTADA)       // el sillar tallado de la muralla
    TemaId.DISTOPIA -> Esquinas(4, Esquina.CORTADA)        // la carpeta del expediente
    TemaId.REFUGIO -> Esquinas(10, Esquina.REDONDA)
    TemaId.ORDEN -> Esquinas(6, Esquina.CORTADA)           // placa de acero
    TemaId.LABORATORIO -> Esquinas(14, Esquina.REDONDA)
    TemaId.COLONOS -> Esquinas(5, Esquina.REDONDA)
    TemaId.NAPOLEON -> Esquinas(2, Esquina.REDONDA)
    TemaId.BOTANICO -> Esquinas(12, Esquina.HOJA)          // la hoja
    TemaId.RUINAS -> Esquinas(5, Esquina.CORTADA)        // el bloque de hormigón desportillado
    TemaId.NAVIDAD -> Esquinas(8, Esquina.REDONDA)
    TemaId.SEDA -> Esquinas(7, Esquina.REDONDA)
    TemaId.URSS -> Esquinas(0, Esquina.REDONDA)
    TemaId.GOTICO -> Esquinas(8, Esquina.ARCO)             // el ventanal
    TemaId.ESPACIO -> Esquinas(16, Esquina.REDONDA)
    TemaId.ABISMO -> Esquinas(15, Esquina.REDONDA)         // la escotilla del batiscafo
    TemaId.ANDALUS -> Esquinas(12, Esquina.ARCO)           // el arco de herradura
    TemaId.MONGOL -> Esquinas(9, Esquina.REDONDA)          // la yurta
    TemaId.RENACIMIENTO -> Esquinas(3, Esquina.HOJA)       // la página del cuaderno
    TemaId.PIRATA -> Esquinas(5, Esquina.CORTADA)          // el cofre con cantoneras
    TemaId.REVOLUCION -> Esquinas(2, Esquina.CORTADA)      // el cartel recortado
    TemaId.ANTIGUAS -> Esquinas(4, Esquina.ARCO)           // la puerta de piedra
}

/** ¿Va en oscuro? Lo decide el botón de la app (claro, oscuro o como el teléfono); Terminal y Ciudad neón siempre son oscuros. */
@Composable
fun modoOscuro(tema: TemaId = Estilo.tema): Boolean = tema.soloOscuro || when (Repo.config.modo) {
    "claro" -> false
    "oscuro" -> true
    else -> isSystemInDarkTheme()
}

@Composable
fun TemaVestigium(content: @Composable () -> Unit) {
    val tema = Estilo.tema
    val oscuro = modoOscuro(tema)
    val esquema = esquemaDe(tema, oscuro)
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
        MaterialTheme(colorScheme = esquema, typography = tipoDe(tema, oscuro), shapes = formas(radioDe(tema)), content = content)
    }
}

@Composable
fun esOscuro(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f

/** Escalas de color para mapas de calor y gráficas, según el tema: bajo → medio → alto. */
object Tonos {
    private class Juego(val bajo: Color, val medio: Color, val alto: Color, val oro: Color)
    private fun juego(oscuro: Boolean): Juego = juegoTonos(Estilo.tema, oscuro).let { Juego(it[0], it[1], it[2], it[3]) }

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
        val (a, b, c) = juegoCalor(Estilo.tema, oscuro)
        return if (x < 0.5f) lerp(a, b, x * 2) else lerp(b, c, (x - 0.5f) * 2)
    }

    fun valencia(v: Double?, oscuro: Boolean): Color? = v?.let { divergente(((it - 1) / 6).toFloat(), oscuro) }

    /** Color de los ingresos: el extremo «bueno» de la escala del tema, para que se distinga del gasto. */
    fun ingreso(oscuro: Boolean): Color = juego(oscuro).alto

    /** Color de lo que sale: el otro extremo de la escala. */
    fun gasto(oscuro: Boolean): Color = juego(oscuro).bajo

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
        "Mundo" to "ORBIS TERRARVM", "Patrimonio Mundial" to "MIRABILIA MVNDI", "Mapa para rascar" to "ORBIS PICTVS", "Ciudades visitadas" to "CIVITATES",
        "Biblioteca de sueños" to "LIBRI SOMNIORVM", "Cartas" to "EPISTVLAE", "Carta" to "EPISTVLA", "Conocerte" to "DE TE", "Brújula política" to "RES PVBLICA", "Mapas" to "TABVLAE ORBIS", "Salud" to "VALETVDO", "Tus apps" to "INSTRVMENTA", "Otras vidas" to "ALIAE VITAE", "Logros" to "PALMAE", "Tu año en historias" to "ANNVS TVVS", "Te moviste" to "MOTVS", "Dormiste" to "SOMNVS", "Tu ánimo" to "ANIMVS", "Tu gente" to "HOMINES", "Lo que leíste y viste" to "CVLTVRA", "Lo que escuchaste" to "MVSICA", "Comida y fuerza" to "CORPVS", "Tu pantalla" to "TELEPHONVM", "Tu dinero" to "PECVNIA", "Lo más común" to "VSITATVM", "Único y curioso" to "MIRABILIA", "Tus medallas" to "PALMAE", "Tu arquetipo" to "TYPVS", "La palabra de tu año" to "VERBVM ANNI", "Anoche" to "HAC NOCTE", "Listas para abrir" to "APERIENDAE", "A otras personas" to "AD ALIOS", "Del pasado" to "EX PRAETERITO", "Tus mapas" to "TABVLAE", "Tus calles" to "VIAE", "Tú" to "TV", "Con quién vives" to "CONTVBERNIVM", "A quién ves más" to "FAMILIARES", "Pareja" to "CONIVX", "A qué te dedicas" to "OPVS", "Tus costumbres" to "MORES", "Lo que está pasando en tu vida" to "TEMPVS PRAESENS", "Cómo ha cambiado" to "MVTATIONES", "Tu brújula" to "PYXIS", "Los cinco ejes" to "QVINQVE AXES", "Qué cambió" to "QVID MVTATVM", "Lo que viviste cada año" to "ANNALES", "¿Para quién?" to "CVI?", "La carta" to "EPISTVLA", "Donde más has estado" to "VBI MAXIME",
        "Día" to "DIES", "Momentos" to "MOMENTA", "Gastos" to "SVMPTVS", "Explorar" to "EXPLORATIO", "Más" to "CETERA",
        "Hábitos" to "CONSVETVDINES", "Cartas al futuro" to "EPISTVLAE", "Experimentos personales" to "EXPERIMENTA",
        "Cómo leer tus datos" to "DOCTRINA", "Guía científica" to "DOCTRINA", "Cuestionarios" to "QVAESTIONES", "Prueba de reacción" to "CELERITAS",
        "Lugares" to "LOCA", "Datos" to "TABVLARIVM", "Permisos y sensores" to "SENSVS", "Ajustes" to "ORDINATIO",
        "Reportes" to "RELATIONES", "Bienvenido" to "SALVE", "Calendario" to "CALENDARIVM", "Escalada" to "ASCENSVS",
        "Obras" to "OPERA", "Tu año" to "ANNVS", "Privacidad" to "SECRETVM",
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
        "Tablero" to "TABVLA", "Tu día" to "HORAE DIEI", "Tu día en 24 horas" to "HORAE DIEI", "Te falta hoy" to "RELIQVA", "Datos que faltan" to "LACVNAE",
        "Tema" to "SPECIES", "Recordatorios" to "MONITIONES", "Resumen semanal" to "HEBDOMAS", "Totales" to "SVMMA OMNIVM",
        "Gimnasio" to "ROBVR", "Análisis" to "ANALYSIS", "Buscar" to "QVAERERE", "Cifras" to "NVMERI", "Momentos de la semana" to "MOMENTA HEBDOMADIS",
        "Llenar el día" to "VESPERA", "Entender" to "INTELLEGERE", "Registros especiales" to "TABVLAE", "Apariencia" to "SPECIES", "Avisos y recordatorios" to "MONITIONES", "Biblioteca del diario" to "LIBRI DIERVM", "Entrada" to "PAGINA", "Hoy" to "HODIE", "Línea de tiempo" to "ORDO TEMPORVM", "Ahora mismo estoy…" to "NVNC",
    )
    fun latin(titulo: String): String? = io.github.ruddy2003.vestigium.ui.nombres.NombresCompletos.de(TemaId.ROMANO, titulo) ?: LATIN[titulo] ?: NombresNuevos.de(TemaId.ROMANO, titulo)

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

/**
 * La Roma de hoy (tema «urbe»): la fecha en italiano con el año contado desde la fundación de la ciudad, los números de
 * los portales, los meses y los días como en los letreros, los 22 rioni del centro y las fiestas de la ciudad.
 * Los numerales romanos (del año de la fundación y de los rioni) salen de [Romano.de].
 */
object Urbe {
    /** El número del portal, como en las placas de travertino de las casas: «n. 17». */
    fun numero(n: Int): String = if (n in 1..9999) "n. $n" else "$n"

    /**
     * El año desde la fundación de Roma (ab Urbe condita), en la cuenta de Varrón: el 1 d. C. es el 754 AUC, así que se suma
     * 753. Se cuenta por años civiles, como en las fechas de las placas modernas; la fiesta del 21 de abril no lo cambia.
     */
    fun auc(anio: Int): Int = anio + 753

    val MESI = listOf("gennaio", "febbraio", "marzo", "aprile", "maggio", "giugno", "luglio", "agosto", "settembre", "ottobre", "novembre", "dicembre")
    private val GIORNI = listOf("lunedì", "martedì", "mercoledì", "giovedì", "venerdì", "sabato", "domenica")

    /** Los días, en dos letras, como en los horarios de las tiendas y de los autobuses. */
    val SEMANA = listOf("LU", "MA", "ME", "GI", "VE", "SA", "DO")

    /** «Roma, sabato 17 ottobre 2026 · AUC MMDCCLXXIX». */
    fun fecha(iso: String): String = runCatching {
        val d = io.github.ruddy2003.vestigium.data.Fechas.parse(iso)
        "Roma, ${GIORNI[d.dayOfWeek.value - 1]} ${if (d.dayOfMonth == 1) "1º" else d.dayOfMonth} ${MESI[d.monthValue - 1]} ${d.year} · AUC ${Romano.de(auc(d.year))}"
    }.getOrDefault(iso)

    /** El mes en mayúsculas, como en las placas: «OTTOBRE 2026». */
    fun mes(n: Int, anio: Int? = null): String = MESI[(n - 1).coerceIn(0, 11)].uppercase() + (anio?.let { " $it" } ?: "")

    /** Los 22 rioni del centro de Roma, en su orden oficial (el número va en romanos en las placas: «R. XIII TRASTEVERE»). */
    val RIONI = listOf("Monti", "Trevi", "Colonna", "Campo Marzio", "Ponte", "Parione", "Regola", "Sant'Eustachio", "Pigna", "Campitelli",
        "Sant'Angelo", "Ripa", "Trastevere", "Borgo", "Esquilino", "Ludovisi", "Sallustiano", "Castro Pretorio", "Celio", "Testaccio",
        "San Saba", "Prati")

    /** «R. XIII · TRASTEVERE»: el rione [n] (de 1 a 22). */
    fun rione(n: Int): String {
        val i = Math.floorMod(n - 1, RIONI.size)
        return "R. ${Romano.de(i + 1)} · ${RIONI[i].uppercase()}"
    }

    /**
     * Las fiestas de la ciudad: el Natale di Roma (21 de abril), los santos patronos Pedro y Pablo (29 de junio), la
     * nieve de Santa Maria Maggiore (5 de agosto), la brecha de Porta Pia (20 de septiembre de 1870) y las fiestas de toda Italia.
     */
    private val FIESTAS = mapOf(
        "01-01" to "Capodanno", "01-06" to "Befana", "01-17" to "S. Antonio", "04-21" to "Natale Urbe", "04-25" to "Liberazione",
        "05-01" to "1º maggio", "06-02" to "Repubblica", "06-29" to "Ss. Pietro", "08-05" to "Neve", "08-15" to "Ferragosto",
        "09-20" to "Porta Pia", "11-01" to "Ognissanti", "12-08" to "Immacolata", "12-25" to "Natale",
    )

    fun marca(fecha: String): String? = FIESTAS[fecha.takeLast(5)]
}

/** Muestra de un tema para elegirlo en Ajustes: fondo, emblema, título en su letra y sus colores, en una tarjeta con la forma del tema. */
@Composable
fun VistaPreviaTema(tema: TemaId, oscuro: Boolean, elegido: Boolean, modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier, onClick: () -> Unit) {
    val o = tema.soloOscuro || oscuro
    val e = esquemaDe(tema, o)
    val t = tipoDe(tema, o)
    val titulo = when (tema) {
        TemaId.ROMANO -> "DIES"
        TemaId.URBE -> "OGGI"
        TemaId.CIBER -> "DÍA_"
        TemaId.NEON -> "夜 · NOCHE"
        TemaId.HELENO -> "ΗΜΕΡΑ"
        TemaId.EGIPTO -> "Hru"
        TemaId.MEDIEVAL -> "Del día"
        TemaId.TEMPLARIO -> "Guardia"
        TemaId.DISTOPIA -> "PARTE"
        TemaId.REFUGIO -> "DIARIO"
        TemaId.ORDEN -> "CAMPO"
        TemaId.LABORATORIO -> "SESIÓN"
        TemaId.COLONOS -> "Jornada"
        TemaId.NAPOLEON -> "BOLETÍN"
        TemaId.BOTANICO -> "Herbario"
        TemaId.RUINAS -> "Día 214"
        TemaId.NAVIDAD -> "El día"
        TemaId.SEDA -> "Etapa"
        TemaId.URSS -> "PLAN"
        TemaId.GOTICO -> "Laudes"
        TemaId.ESPACIO -> "ÓRBITA"
        TemaId.ABISMO -> "COTA"
        TemaId.ANDALUS -> "النهار"
        TemaId.MONGOL -> "ӨДӨР"
        TemaId.RENACIMIENTO -> "Giornata"
        TemaId.PIRATA -> "Bitácora"
        TemaId.REVOLUCION -> "CAHIER"
        TemaId.ANTIGUAS -> "CRÓNICA"
    }
    val linea = when (tema) {
        TemaId.ROMANO -> "XVII · X"
        TemaId.URBE -> "sabato 17 ottobre"
        TemaId.CIBER -> "2026.10.17"
        TemaId.NEON -> "NIVEL −3 · 22:47"
        TemaId.HELENO -> "ΙΖʹ ΥΠΕΡΒ."
        TemaId.EGIPTO -> "II Ajet 7 · Paofi"
        TemaId.MEDIEVAL -> "xvij de otubre"
        TemaId.TEMPLARIO -> "iv oct. · juliano"
        TemaId.DISTOPIA -> "EXP. 0290"
        TemaId.REFUGIO -> "DÍA 290"
        TemaId.ORDEN -> "17 OCT 26"
        TemaId.LABORATORIO -> "T+290"
        TemaId.COLONOS -> "17 de octubre"
        TemaId.NAPOLEON -> "samedi 17 octobre"
        TemaId.BOTANICO -> "otoño · día 26"
        TemaId.RUINAS -> "tras el silencio · otoño"
        TemaId.NAVIDAD -> "faltan 69 días"
        TemaId.SEDA -> "十月十七日"
        TemaId.URSS -> "ОКТЯБРЬ"
        TemaId.GOTICO -> "hora de vísperas"
        TemaId.ESPACIO -> "JD 2461331"
        TemaId.ABISMO -> "−290 m · 30 bar"
        TemaId.ANDALUS -> "1448 H"
        TemaId.MONGOL -> "Морин жил"
        TemaId.RENACIMIENTO -> "addì 17 d'ottobre"
        TemaId.PIRATA -> "singladura 290 · 8bre"
        TemaId.REVOLUCION -> "26 vendémiaire"
        TemaId.ANTIGUAS -> "17 de la Luna de las Hojas"
    }
    val forma = formaPrevia(tema)
    // Las formas con arco, frontón o punta necesitan más aire arriba o abajo para que nada quede cortado; la suma es la misma en todas, para que las filas queden parejas.
    val (arriba, abajo) = when (tema) {
        TemaId.ROMANO -> 16 to 16
        TemaId.URBE -> 20 to 12
        TemaId.CIBER -> 16 to 16
        TemaId.NEON -> 16 to 16
        TemaId.HELENO -> 20 to 12
        TemaId.EGIPTO -> 21 to 11
        TemaId.MEDIEVAL -> 16 to 16
        TemaId.TEMPLARIO -> 22 to 10
        TemaId.DISTOPIA -> 20 to 12
        TemaId.REFUGIO -> 16 to 16
        TemaId.ORDEN -> 16 to 16
        TemaId.LABORATORIO -> 12 to 20
        TemaId.COLONOS -> 16 to 16
        TemaId.NAPOLEON -> 12 to 20
        TemaId.BOTANICO -> 16 to 16
        TemaId.RUINAS -> 18 to 14
        TemaId.NAVIDAD -> 20 to 12
        TemaId.SEDA -> 12 to 20
        TemaId.GOTICO -> 24 to 8
        TemaId.ESPACIO -> 18 to 14
        TemaId.ABISMO -> 20 to 12
        TemaId.ANDALUS -> 22 to 10
        TemaId.MONGOL -> 14 to 18
        TemaId.RENACIMIENTO -> 16 to 16
        TemaId.PIRATA -> 12 to 20
        TemaId.URSS -> 16 to 16
        TemaId.REVOLUCION -> 12 to 20
        TemaId.ANTIGUAS -> 18 to 14
    }
    // En el trapecio y el paralelogramo los lados se meten: un poco más de margen a los lados.
    val lados = when (tema) {
        TemaId.ROMANO -> 12
        TemaId.URBE -> 11
        TemaId.CIBER -> 10
        TemaId.NEON -> 10
        TemaId.HELENO -> 10
        TemaId.EGIPTO -> 13
        TemaId.MEDIEVAL -> 10
        TemaId.TEMPLARIO -> 12
        TemaId.DISTOPIA -> 10
        TemaId.REFUGIO -> 12
        TemaId.ORDEN -> 12
        TemaId.LABORATORIO -> 10
        TemaId.COLONOS -> 14
        TemaId.NAPOLEON -> 10
        TemaId.BOTANICO -> 12
        TemaId.RUINAS -> 13
        TemaId.NAVIDAD -> 10
        TemaId.SEDA -> 10
        TemaId.GOTICO -> 12
        TemaId.ESPACIO -> 10
        TemaId.ABISMO -> 11
        TemaId.ANDALUS -> 12
        TemaId.MONGOL -> 10
        TemaId.RENACIMIENTO -> 16
        TemaId.PIRATA -> 12
        TemaId.URSS -> 18
        TemaId.REVOLUCION -> 10
        TemaId.ANTIGUAS -> 12
    }
    androidx.compose.material3.Surface(
        onClick = onClick, modifier = modifier, color = e.background, shape = forma,
        border = androidx.compose.foundation.BorderStroke(if (elegido) 2.dp else 1.dp, if (elegido) e.primary else e.outline),
    ) {
        androidx.compose.foundation.layout.Column(androidx.compose.ui.Modifier.padding(start = lados.dp, end = lados.dp, top = arriba.dp, bottom = abajo.dp)) {
            androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                // Los títulos de más de cinco letras se achican para caber junto al emblema en las tarjetas de tres en fila.
                val escala = if (titulo.length > 5) 0.82f * 5f / titulo.length else 0.82f
                androidx.compose.material3.Text(titulo, style = t.headlineSmall.copy(fontSize = t.headlineSmall.fontSize * escala), color = e.onBackground, maxLines = 1,
                    softWrap = false, overflow = androidx.compose.ui.text.style.TextOverflow.Clip, modifier = androidx.compose.ui.Modifier.weight(1f))
                Emblema(tema, 22.dp, e, animado = false)
            }
            androidx.compose.material3.Text(linea, style = t.labelSmall, color = e.primary, maxLines = 1)
            androidx.compose.foundation.layout.Row(androidx.compose.ui.Modifier.padding(top = 8.dp)) {
                listOf(e.primary, e.secondary, e.tertiary, e.surfaceVariant).forEach { c ->
                    androidx.compose.foundation.Canvas(androidx.compose.ui.Modifier.padding(end = 4.dp).size(12.dp)) { cuadritoPrevia(tema, c, e.background, e.outline) }
                }
            }
            androidx.compose.material3.Text(tema.nombre + if (elegido) "  ✓" else "", style = t.titleSmall, color = e.onSurfaceVariant, maxLines = 1, modifier = androidx.compose.ui.Modifier.padding(top = 6.dp))
        }
    }
}

/** Forma que delimita el contorno de la tarjeta de muestra, propia de cada tema (cartela, chaflán, frontón, carpeta, ojiva, trapecio…). */
private fun formaPrevia(tema: TemaId): Shape = when (tema) {
    // Cartela de esquinas cóncavas.
    TemaId.ROMANO -> GenericShape { s, _ ->
        val r = s.minDimension * 0.09f
        moveTo(r, 0f); lineTo(s.width - r, 0f); quadraticTo(s.width - r, r, s.width, r); lineTo(s.width, s.height - r)
        quadraticTo(s.width - r, s.height - r, s.width - r, s.height); lineTo(r, s.height); quadraticTo(r, s.height - r, 0f, s.height - r)
        lineTo(0f, r); quadraticTo(r, r, r, 0f); close()
    }
    // Portón con arco rebajado, como los de los palacios: el arco apenas sube sobre los dos pies derechos.
    TemaId.URBE -> GenericShape { s, _ ->
        val f = s.height * 0.08f
        moveTo(0f, s.height); lineTo(0f, f); quadraticTo(s.width / 2, -f, s.width, f); lineTo(s.width, s.height); close()
    }
    // Panel de terminal con dos esquinas cortadas en diagonal.
    TemaId.CIBER -> CutCornerShape(topStart = 0.dp, topEnd = 12.dp, bottomEnd = 0.dp, bottomStart = 12.dp)
    // Letrero colgado de la fachada: placa con las cuatro esquinas cortadas.
    TemaId.NEON -> CutCornerShape(10.dp)
    // Fachada con frontón.
    TemaId.HELENO -> GenericShape { s, _ ->
        val f = s.height * 0.1f
        moveTo(0f, f); lineTo(s.width / 2, 0f); lineTo(s.width, f); lineTo(s.width, s.height); lineTo(0f, s.height); close()
    }
    // Pilono de templo: los muros en talud y, arriba, la gola que se abre hacia afuera.
    TemaId.EGIPTO -> GenericShape { s, _ ->
        val d = s.width * 0.06f; val f = s.height * 0.12f
        moveTo(0f, s.height); lineTo(d, f); quadraticTo(d * 0.9f, f * 0.35f, d * 0.15f, 0f)
        lineTo(s.width - d * 0.15f, 0f); quadraticTo(s.width - d * 0.9f, f * 0.35f, s.width - d, f); lineTo(s.width, s.height); close()
    }
    // Folio con la esquina doblada.
    TemaId.MEDIEVAL -> CutCornerShape(topStart = 0.dp, topEnd = 14.dp, bottomEnd = 0.dp, bottomStart = 0.dp)
    // Lienzo de muralla: almenas arriba, con sus merlones y troneras.
    TemaId.TEMPLARIO -> GenericShape { s, _ ->
        val n = 7; val paso = s.width / (2 * n - 1); val a = s.height * 0.07f
        moveTo(0f, s.height); lineTo(0f, 0f)
        for (i in 0 until n) {
            val x0 = 2 * i * paso
            lineTo(x0 + paso, 0f)
            if (i < n - 1) { lineTo(x0 + paso, a); lineTo(x0 + 2 * paso, a); lineTo(x0 + 2 * paso, 0f) }
        }
        lineTo(s.width, 0f); lineTo(s.width, s.height); close()
    }
    // Carpeta de archivo con su pestaña.
    TemaId.DISTOPIA -> GenericShape { s, _ ->
        val p = s.height * 0.08f; val a = s.width * 0.42f
        moveTo(0f, 0f); lineTo(a, 0f); lineTo(a + p, p); lineTo(s.width, p); lineTo(s.width, s.height); lineTo(0f, s.height); close()
    }
    // Pantalla de tubo, muy redonda.
    TemaId.REFUGIO -> RoundedCornerShape(18.dp)
    // Compuerta de esquinas cortadas.
    TemaId.ORDEN -> CutCornerShape(9.dp)
    // Vaso de precipitado: arriba recto, abajo muy redondo.
    TemaId.LABORATORIO -> RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp, bottomEnd = 20.dp, bottomStart = 20.dp)
    // Recorte de gaceta con muescas a los lados, como un boleto.
    TemaId.COLONOS -> GenericShape { s, _ ->
        val r = s.minDimension * 0.06f; val m = s.height / 2
        moveTo(0f, 0f); lineTo(s.width, 0f); lineTo(s.width, m - r)
        arcTo(Rect(Offset(s.width, m), r), 270f, -180f, false)
        lineTo(s.width, s.height); lineTo(0f, s.height); lineTo(0f, m + r)
        arcTo(Rect(Offset(0f, m), r), 90f, -180f, false)
        close()
    }
    // Escudo: abajo termina en punta.
    TemaId.NAPOLEON -> GenericShape { s, _ ->
        val p = s.height * 0.1f
        moveTo(0f, 0f); lineTo(s.width, 0f); lineTo(s.width, s.height - p); lineTo(s.width / 2, s.height); lineTo(0f, s.height - p); close()
    }
    // Hoja: dos esquinas opuestas muy redondas.
    TemaId.BOTANICO -> RoundedCornerShape(topStart = 22.dp, topEnd = 3.dp, bottomEnd = 22.dp, bottomStart = 3.dp)
    // Losa de hormigón rota: la esquina de arriba a la derecha se desprendió en escalones.
    TemaId.RUINAS -> GenericShape { s, _ ->
        val a = s.minDimension * 0.12f
        moveTo(0f, 0f); lineTo(s.width * 0.68f, 0f); lineTo(s.width * 0.74f, a * 0.55f); lineTo(s.width * 0.82f, a * 0.25f)
        lineTo(s.width * 0.88f, a); lineTo(s.width, a * 1.35f); lineTo(s.width, s.height); lineTo(s.width * 0.1f, s.height)
        lineTo(0f, s.height - a * 0.6f); close()
    }
    // Glaseado de nieve: borde de arriba en festón.
    TemaId.NAVIDAD -> GenericShape { s, _ ->
        val n = 6; val paso = s.width / n; val f = s.height * 0.06f
        moveTo(0f, f)
        for (i in 0 until n) quadraticTo(i * paso + paso / 2, -f, (i + 1) * paso, f)
        lineTo(s.width, s.height); lineTo(0f, s.height); close()
    }
    // Banderola con cola de golondrina abajo.
    TemaId.SEDA -> GenericShape { s, _ ->
        val p = s.height * 0.1f
        moveTo(0f, 0f); lineTo(s.width, 0f); lineTo(s.width, s.height); lineTo(s.width / 2, s.height - p); lineTo(0f, s.height); close()
    }
    // Ventana ojival.
    TemaId.GOTICO -> GenericShape { s, _ ->
        val a = s.height * 0.2f
        moveTo(0f, s.height); lineTo(0f, a)
        cubicTo(0f, a * 0.45f, s.width * 0.28f, a * 0.12f, s.width / 2, 0f)
        cubicTo(s.width * 0.72f, a * 0.12f, s.width, a * 0.45f, s.width, a)
        lineTo(s.width, s.height); close()
    }
    // Cápsula: nariz redonda arriba, base casi recta.
    TemaId.ESPACIO -> RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp, bottomEnd = 6.dp, bottomStart = 6.dp)
    // La superficie del mar: el borde de arriba es una ola mansa; abajo, el casco redondeado.
    TemaId.ABISMO -> GenericShape { s, _ ->
        val o = s.height * 0.05f; val r = s.minDimension * 0.12f; val n = 3
        moveTo(0f, o)
        for (i in 0 until n) {
            val x0 = s.width * i / n; val x1 = s.width * (i + 1) / n
            quadraticTo((x0 + x1) / 2, if (i % 2 == 0) 0f else o * 2, x1, o)
        }
        lineTo(s.width, s.height - r); quadraticTo(s.width, s.height, s.width - r, s.height)
        lineTo(r, s.height); quadraticTo(0f, s.height, 0f, s.height - r); close()
    }
    // Arco de medio punto peraltado.
    TemaId.ANDALUS -> GenericShape { s, _ ->
        val a = s.height * 0.18f
        moveTo(0f, s.height); lineTo(0f, a); quadraticTo(0f, 0f, s.width / 2, 0f); quadraticTo(s.width, 0f, s.width, a)
        lineTo(s.width, s.height); close()
    }
    // Fieltro con las esquinas de abajo cortadas.
    TemaId.MONGOL -> CutCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomEnd = 12.dp, bottomStart = 12.dp)
    // Hoja vista en perspectiva: un trapecio.
    TemaId.RENACIMIENTO -> GenericShape { s, _ ->
        val d = s.width * 0.05f
        moveTo(d, 0f); lineTo(s.width - d, 0f); lineTo(s.width, s.height); lineTo(0f, s.height); close()
    }
    // Bandera al viento: el borde de abajo ondea en tres olas.
    TemaId.PIRATA -> GenericShape { s, _ ->
        val n = 3; val paso = s.width / n; val f = s.height * 0.045f
        moveTo(0f, 0f); lineTo(s.width, 0f); lineTo(s.width, s.height - f)
        for (i in n - 1 downTo 0) cubicTo(i * paso + paso * 0.7f, s.height + f, i * paso + paso * 0.3f, s.height - f * 3f, i * paso, s.height - f)
        close()
    }
    // Bloque de cartel inclinado.
    TemaId.URSS -> GenericShape { s, _ ->
        val d = s.width * 0.07f
        moveTo(d, 0f); lineTo(s.width, 0f); lineTo(s.width - d, s.height); lineTo(0f, s.height); close()
    }
    // Cartel arrancado: el borde de abajo en dientes.
    TemaId.REVOLUCION -> GenericShape { s, _ ->
        val n = 9; val paso = s.width / n; val f = s.height * 0.05f
        moveTo(0f, 0f); lineTo(s.width, 0f); lineTo(s.width, s.height - f)
        for (i in n - 1 downTo 0) { lineTo(i * paso + paso / 2, if (i % 2 == 0) s.height else s.height - f * 0.4f); lineTo(i * paso, s.height - f) }
        close()
    }
    // Lápida rúnica: esquinas de arriba muy cortadas, las de abajo apenas.
    TemaId.ANTIGUAS -> CutCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 4.dp, bottomStart = 4.dp)
}

/** Cuadrito de color de la muestra, con la figura del tema (tesela, píxel, moneda, rombo, tuerca, probeta, hoja, estrella…). */
private fun DrawScope.cuadritoPrevia(tema: TemaId, c: Color, fondo: Color, borde: Color) {
    val w = size.width; val h = size.height; val cx = w / 2; val cy = h / 2; val r = w / 2
    fun poligono(lados: Int, giro: Float, interior: Float? = null) = Path().apply {
        val n = if (interior != null) lados * 2 else lados
        for (i in 0 until n) {
            val a = Math.toRadians(giro + i * 360.0 / n); val rr = if (interior != null && i % 2 == 1) interior else r
            val x = cx + rr * kotlin.math.cos(a).toFloat(); val y = cy + rr * kotlin.math.sin(a).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    val fino = androidx.compose.ui.graphics.drawscope.Stroke(w * 0.1f)
    when (tema) {
        // Tesela de mosaico.
        TemaId.ROMANO -> { drawRect(c); drawRect(fondo.copy(alpha = 0.35f), Offset(w * 0.12f, h * 0.12f), Size(w * 0.76f, h * 0.76f), style = fino) }
        // Sampietrino: el adoquín de basalto, gastado en las aristas, con su junta y el brillo de la piedra pisada.
        TemaId.URBE -> {
            drawRoundRect(c, Offset(w * 0.06f, h * 0.06f), Size(w * 0.88f, h * 0.88f), CornerRadius(w * 0.16f))
            drawLine(fondo.copy(alpha = 0.45f), Offset(w * 0.24f, h * 0.24f), Offset(w * 0.6f, h * 0.2f), w * 0.08f)
        }
        // Píxel encendido dentro de su celda.
        TemaId.CIBER -> { drawRect(c, style = fino); drawRect(c, Offset(w * 0.28f, h * 0.28f), Size(w * 0.44f, h * 0.44f)) }
        // Tubo de neón doblado en «N», con su halo.
        TemaId.NEON -> {
            val n = Path().apply { moveTo(w * 0.24f, h * 0.8f); lineTo(w * 0.24f, h * 0.2f); lineTo(w * 0.76f, h * 0.8f); lineTo(w * 0.76f, h * 0.2f) }
            drawPath(n, c.copy(alpha = 0.3f), style = androidx.compose.ui.graphics.drawscope.Stroke(w * 0.26f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(n, c, style = androidx.compose.ui.graphics.drawscope.Stroke(w * 0.11f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        // Dracma.
        TemaId.HELENO -> { drawCircle(c, r); drawCircle(fondo.copy(alpha = 0.4f), r * 0.68f, style = fino) }
        // Cartucho: el óvalo de cuerda que encierra el nombre del rey, con su nudo abajo.
        TemaId.EGIPTO -> {
            drawRoundRect(c, Offset(w * 0.2f, 0f), Size(w * 0.6f, h * 0.8f), CornerRadius(w * 0.3f))
            drawRoundRect(fondo.copy(alpha = 0.45f), Offset(w * 0.32f, h * 0.12f), Size(w * 0.36f, h * 0.56f), CornerRadius(w * 0.18f), style = androidx.compose.ui.graphics.drawscope.Stroke(w * 0.07f))
            drawRect(c, Offset(w * 0.1f, h * 0.86f), Size(w * 0.8f, h * 0.14f))
        }
        // Losange de blasón.
        TemaId.MEDIEVAL -> drawPath(poligono(4, -90f), c)
        // Escudo de lágrima (el de los caballeros del siglo XII) con la cruz patada vaciada.
        TemaId.TEMPLARIO -> {
            drawPath(Path().apply { moveTo(w * 0.1f, 0f); lineTo(w * 0.9f, 0f); quadraticTo(w * 0.92f, h * 0.62f, cx, h); quadraticTo(w * 0.08f, h * 0.62f, w * 0.1f, 0f); close() }, c)
            drawRect(fondo.copy(alpha = 0.7f), Offset(cx - w * 0.06f, h * 0.12f), Size(w * 0.12f, h * 0.6f))
            drawRect(fondo.copy(alpha = 0.7f), Offset(w * 0.24f, h * 0.3f), Size(w * 0.52f, h * 0.12f))
        }
        // Ficha de archivo apaisada.
        TemaId.DISTOPIA -> drawRect(c, Offset(0f, h * 0.2f), Size(w, h * 0.6f))
        // Botón de consola.
        TemaId.REFUGIO -> drawRoundRect(c, cornerRadius = CornerRadius(w * 0.3f))
        // Tuerca.
        TemaId.ORDEN -> { drawPath(poligono(6, 0f), c); drawCircle(fondo, r * 0.32f) }
        // Probeta.
        TemaId.LABORATORIO -> drawRoundRect(c, Offset(w * 0.28f, 0f), Size(w * 0.44f, h), CornerRadius(w * 0.22f))
        // Casita del pueblo.
        TemaId.COLONOS -> drawPath(Path().apply { moveTo(0f, h * 0.45f); lineTo(cx, 0f); lineTo(w, h * 0.45f); lineTo(w, h); lineTo(0f, h); close() }, c)
        // Escudo.
        TemaId.NAPOLEON -> drawPath(Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h * 0.55f); quadraticTo(w, h * 0.85f, cx, h); quadraticTo(0f, h * 0.85f, 0f, h * 0.55f); close() }, c)
        // Hoja.
        TemaId.BOTANICO -> drawPath(Path().apply { moveTo(0f, h); cubicTo(0f, h * 0.3f, w * 0.3f, 0f, w, 0f); cubicTo(w, h * 0.7f, w * 0.7f, h, 0f, h); close() }, c)
        // Semáforo apagado: la caja y sus tres luces; solo la verde sigue viva.
        TemaId.RUINAS -> { drawRoundRect(c, Offset(w * 0.3f, 0f), Size(w * 0.4f, h), CornerRadius(w * 0.12f)); for (k in 0..2) drawCircle(fondo.copy(alpha = if (k == 2) 0.9f else 0.3f), w * 0.13f, Offset(cx, h * (0.18f + 0.32f * k))) }
        // Bola de árbol con su capuchón.
        TemaId.NAVIDAD -> { drawCircle(c, r * 0.8f, Offset(cx, cy + r * 0.2f)); drawRect(borde, Offset(cx - r * 0.25f, 0f), Size(r * 0.5f, r * 0.3f)) }
        // Moneda con agujero cuadrado.
        TemaId.SEDA -> { drawCircle(c, r); drawRect(fondo, Offset(cx - r * 0.3f, cy - r * 0.3f), Size(r * 0.6f, r * 0.6f)) }
        // Lanceta de vitral.
        TemaId.GOTICO -> drawPath(Path().apply { moveTo(w * 0.15f, h); lineTo(w * 0.15f, h * 0.4f); quadraticTo(w * 0.15f, h * 0.08f, cx, 0f); quadraticTo(w * 0.85f, h * 0.08f, w * 0.85f, h * 0.4f); lineTo(w * 0.85f, h); close() }, c)
        // Planeta con anillo.
        TemaId.ESPACIO -> { drawCircle(c, r * 0.62f); drawOval(c, Offset(0f, cy - h * 0.14f), Size(w, h * 0.28f), style = androidx.compose.ui.graphics.drawscope.Stroke(w * 0.08f)) }
        // Medusa: la campana y tres tentáculos.
        TemaId.ABISMO -> {
            drawArc(c, 180f, 180f, true, Offset(w * 0.1f, h * 0.05f), Size(w * 0.8f, h * 0.8f))
            for (k in 0..2) {
                val x = w * (0.28f + 0.22f * k)
                drawPath(Path().apply { moveTo(x, h * 0.45f); quadraticTo(x - w * 0.1f, h * 0.7f, x, h) }, c, style = androidx.compose.ui.graphics.drawscope.Stroke(w * 0.08f))
            }
        }
        // Estrella de ocho puntas.
        TemaId.ANDALUS -> drawPath(poligono(8, -90f, r * 0.765f), c)
        // Yurta.
        TemaId.MONGOL -> drawPath(Path().apply { moveTo(0f, h); lineTo(0f, h * 0.5f); cubicTo(w * 0.1f, h * 0.05f, w * 0.9f, h * 0.05f, w, h * 0.5f); lineTo(w, h); close() }, c)
        // Triángulo de construcción.
        TemaId.RENACIMIENTO -> drawPath(Path().apply { moveTo(cx, 0f); lineTo(w, h); lineTo(0f, h); close() }, c)
        // La «X» del tesoro.
        TemaId.PIRATA -> {
            drawLine(c, Offset(w * 0.16f, h * 0.16f), Offset(w * 0.84f, h * 0.84f), w * 0.24f, androidx.compose.ui.graphics.StrokeCap.Round)
            drawLine(c, Offset(w * 0.84f, h * 0.16f), Offset(w * 0.16f, h * 0.84f), w * 0.24f, androidx.compose.ui.graphics.StrokeCap.Round)
        }
        // Estrella de cinco puntas.
        TemaId.URSS -> drawPath(poligono(5, -90f, r * 0.42f), c)
        // Escarapela.
        TemaId.REVOLUCION -> { drawCircle(c, r); drawCircle(fondo, r * 0.62f); drawCircle(c, r * 0.36f) }
        // Gema tallada.
        TemaId.ANTIGUAS -> {
            drawPath(Path().apply { moveTo(w * 0.2f, h * 0.3f); lineTo(w * 0.35f, 0f); lineTo(w * 0.65f, 0f); lineTo(w * 0.8f, h * 0.3f); lineTo(cx, h); close() }, c)
            drawLine(fondo.copy(alpha = 0.5f), Offset(w * 0.2f, h * 0.3f), Offset(w * 0.8f, h * 0.3f), w * 0.06f)
        }
    }
}
