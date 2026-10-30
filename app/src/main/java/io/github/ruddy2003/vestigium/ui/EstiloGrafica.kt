package io.github.ruddy2003.vestigium.ui

import android.content.Context
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.core.content.res.ResourcesCompat
import io.github.ruddy2003.vestigium.R
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * # Estilo de gráfica por tema (lote 4)
 *
 * Cada tema dibuja sus gráficas con su propio punto, trazo, rejilla, eje, barra y letra de cifras. Es **solo adorno**:
 * no cambia escalas, datos, posiciones ni colores (`Tonos` y los colores del esquema siguen saliendo de quien llama).
 * Las alturas y longitudes se respetan: la punta de una barra queda exactamente donde estaba, un punto queda centrado
 * en su valor y la rejilla pasa por las mismas y.
 *
 * ## La API
 * - `estiloGrafica(tema = Estilo.tema): EstiloGrafica` — el estilo del tema (se calcula una vez por tema).
 * - Compose (`DrawScope`), los colores siempre los pone quien llama:
 *   - `puntoGrafica(e, centro, radio, color, hueco = false)` — reemplaza a `drawCircle` de los puntos de datos.
 *   - `lineaGrafica(e, ruta: Path, color, ancho: Float? = null, acento = Color.Unspecified)` — reemplaza a
 *     `drawPath(ruta, color, style = Stroke(w))` de las series. `ancho` en px: si viene, manda sobre el del tema.
 *   - `lineaGrafica(e, puntos: List<Offset?>, color, ancho, acento)` — igual, a partir de puntos; un `null` corta la línea.
 *     `rutaGrafica(puntos)` arma esa ruta si se necesita aparte (para un relleno bajo la curva, por ejemplo).
 *   - `lineaMeta(e, desde, hasta, color, ancho: Float? = null)` — líneas de referencia (meta, promedio, tendencia, mes
 *     anterior): siempre discontinuas, con un patrón que no se confunde con el de la serie del mismo tema.
 *   - `barraGrafica(e, arriba, tam, color, horizontal = false, pista = false, acento)` — reemplaza a `drawRect`/`drawRoundRect`
 *     de las barras de datos. `horizontal = true` para barras que crecen a la derecha (la «tapa» va a la derecha).
 *     `pista = true` dibuja solo la silueta llena, para el carril de fondo de una barra.
 *   - `rejillaGrafica(e, ys, color, xs = emptyList(), area: Rect? = null)` — líneas guía horizontales en esas y (y
 *     verticales en esas x), a lo ancho y alto de [area] (por omisión, todo el lienzo).
 *   - `ejeGrafica(e, desde, hasta, color, marcas = emptyList())` — un eje recto (horizontal o vertical). Las marcas son
 *     coordenadas x (eje horizontal) o y (eje vertical); las rayas salen hacia afuera: abajo o a la izquierda.
 * - Cifras:
 *   - `@Composable estiloCifras(base = labelSmall): TextStyle` — para las cifras en `Text` o en `drawText(medidor…)`.
 *   - `e.textoCifras(base)` — lo mismo fuera de un `@Composable`.
 *   - `@Composable pincelDeCifras(tinta, tam, alinear = LEFT): Paint` — un `android.graphics.Paint` listo para
 *     `drawContext.canvas.nativeCanvas.drawText(...)` dentro de un `Canvas` de Compose.
 *   - `e.tipoCifras(ctx): Typeface` y `e.pincelCifras(paint, ctx): Paint` — para los reportes y otros lienzos de android.
 * - `android.graphics.Canvas` (reportes; 1 dp = [ESCALA_REPORTE] px porque la hoja mide 1080 px):
 *   - `c.puntoGrafica(e, x, y, r, paint)` (hueco si el `paint` es de trazo), `c.lineaGrafica(e, ruta, paint)` (ancho del
 *     `paint`), `c.lineaMeta(e, x0, y0, x1, y1, paint)`, `c.barraGrafica(e, rectF, paint, horizontal = false)`,
 *     `c.rejillaGrafica(e, ys, paint, area: RectF, xs)`, `c.ejeGrafica(e, x0, y0, x1, y1, paint, marcas)`.
 *     El color sale de `paint.color`. Todas usan por dentro las de Compose (con `CanvasDrawScope`), así que dibujan igual.
 *   - `e.pincelLinea(paint, escala, ancho)` — si se prefiere seguir dibujando a mano: pone al `paint` el ancho, la tapa,
 *     la unión y los guiones del tema (sin adornos de halo, filete, sombra…).
 *
 * ## Cómo aplicarla en sitio (antes → después)
 * ```
 * // Puntos (Explorar.Dispersion):
 * drawCircle(acento.copy(alpha = 0.45f), 9f, c); drawCircle(acento, 9f, c, style = Stroke(2f))
 * →  puntoGrafica(e, c, 9f, acento.copy(alpha = 0.45f)); puntoGrafica(e, c, 9f, acento, hueco = true)
 *
 * // Serie (Analisis.Tendencia), con val e = estiloGrafica() fuera del Canvas:
 * drawLine(color, Offset(px(a), py(a)), Offset(px(b), py(b)), w)  (segmento a segmento)
 * →  lineaGrafica(e, puntos, color, w)          // puntos: List<Offset?>, con null donde falta un día
 *
 * // Barras (Gastos.GastoPorDia, Hoy.TarjetaMetrica):
 * drawRoundRect(barra, Offset(x, y), Size(w, h), CornerRadius(2f, 2f))
 * →  barraGrafica(e, Offset(x, y), Size(w, h), barra)
 * drawRoundRect(pista, Offset.Zero, size, CornerRadius(size.height / 2))     // carril de fondo (Salud.FilaMicro)
 * →  barraGrafica(e, Offset.Zero, size, pista, horizontal = true, pista = true)
 *
 * // Rejilla y cifras del eje (Explorar.Dispersion):
 * val pincel = Paint().apply { color = texto; textSize = 26f; isAntiAlias = true }       (dentro del Canvas)
 * listOf(y0, ym, y1).forEach { v -> drawLine(linea, Offset(l, py(v)), Offset(size.width - r, py(v)), 1.5f) }
 * →  val pincel = pincelDeCifras(colorTexto, 26f)                                          (fuera del Canvas)
 *    rejillaGrafica(e, listOf(y0, ym, y1).map { py(it) }, linea, area = Rect(l, t, size.width - r, size.height - b))
 *    ejeGrafica(e, Offset(l, size.height - b), Offset(size.width - r, size.height - b), linea, marcas = listOf(px(x0), px(x1)))
 *
 * // Meta o promedio (Hoy.TarjetaMetrica, Reportes.curvaSueno):
 * drawLine(tenue, Offset(0f, y), Offset(size.width, y), 1f)  →  lineaMeta(e, Offset(0f, y), Offset(size.width, y), tenue)
 *
 * // Reportes (android.graphics), con val e = estiloGrafica(L.tema):
 * c.drawPath(path, L.trazo(color, 4f))                         →  c.lineaGrafica(e, path, L.trazo(color, 4f))
 * c.drawCircle(x, y, 6f, L.relleno(col))                       →  c.puntoGrafica(e, x, y, 6f, L.relleno(col))
 * c.drawRoundRect(RectF(a, b, cc, dd), 6f, 6f, L.relleno(col)) →  c.barraGrafica(e, RectF(a, b, cc, dd), L.relleno(col), horizontal = true)
 * L.p(L.tenue, 18f, L.mono)  (cifras de los ejes)              →  e.pincelCifras(L.p(L.tenue, 18f), ctx)
 * ```
 * Reglas al aplicarla: no cambies coordenadas, escalas, colores ni qué se dibuja; no metas el estilo en íconos,
 * leyendas de color o adornos que no sean gráficas de datos. Los textos que no son cifras (nombres, días) siguen con su letra.
 */
data class EstiloGrafica(
    /** La forma de los puntos de datos. */
    val punto: FormaPunto,
    /** Ancho de las líneas de serie, en dp. */
    val ancho: Float,
    /** Guiones de la serie, en dp (trazo, hueco, trazo…); null = continua. */
    val guiones: List<Float>?,
    val tapa: StrokeCap,
    val union: StrokeJoin,
    /** Lo que acompaña a la línea: halo, sombra, filete, pluma… */
    val adorno: AdornoTrazo,
    /** Guiones de las líneas de referencia (meta, promedio), en dp. Si el primero es casi 0, son puntos redondos. */
    val guionesMeta: List<Float>,
    val rejilla: Rejilla,
    /** Cuánto se apaga el color que se pasa a la rejilla (multiplica su alfa). */
    val alfaRejilla: Float,
    val eje: Eje,
    val barra: FormaBarra,
    /** Letra de las cifras (un `R.font`), su peso, rasgos OpenType («tnum», «onum»…), espaciado (em) y escala del tamaño. */
    val letraCifras: Int,
    val pesoCifras: FontWeight,
    val rasgosCifras: String?,
    val espaciadoCifras: Float,
    val escalaCifras: Float,
    val cursivaCifras: Boolean = false,
) {
    /** La letra de las cifras para Compose. */
    val familiaCifras: FontFamily = FontFamily(Font(letraCifras, pesoCifras))
}

/** Formas del punto de datos. */
enum class FormaPunto {
    CIRCULO, TRIANGULO, CUPOLA, PIXEL, SOL, FLOR, DIANA, CRUZ, ESCUDO, HEXAGONO, CUADRADO, DESTELLO, HOJA, COPO, MONEDA, SAKURA,
    HIEDRA,
    ROMBO, ANILLO, OCTOGRAMA, LUNA, MIRA,
    /** La «X» de las cartas del tesoro. */
    EQUIS,
    ESTRELLA, ESCARAPELA, GEMA, GOTA,
    /** Templario: la cruz patada. */
    PATADA,
    /** Prehistoria: la mano de ocre. */
    MANO,
}

/** Lo que acompaña al trazo de una serie. */
enum class AdornoTrazo {
    NINGUNO,
    /** Resplandor de fósforo: dos trazos anchos y tenues debajo. */
    HALO,
    /** Copia desplazada y tenue debajo (impresión corrida, espécimen clavado, piedra tallada). */
    SOMBRA,
    /** Hilo claro (o del color [acento]) por el centro: filete de oro, plomo de vitral. */
    FILETE,
    /** Segundo trazo fino corrido en diagonal, como el de una plumilla de punta ancha. */
    PLUMA,
    /** Trazo de construcción tenue y corrido, como en los cuadernos de dibujo. */
    BOCETO,
    /** Franjas claras encima, como un bastón de caramelo o una cinta. */
    RAYAS,
}

/** Las líneas guía del fondo de la gráfica. */
enum class Rejilla {
    NINGUNA, LINEAS, PUNTEADA, DISCONTINUA, MILIMETRADO, CRUCES, MATRIZ, DOBLE, PICADO, BANDAS, FILETES, RETICULA,
    GUIRNALDA, ONDAS, KASUMI, CELOSIA, ESTRELLAS, ISOBATAS, ROMBOS, MUESCAS, PUNTO_Y_RAYA, ALTERNA, PAUTA, TALLADA,
}

/** El eje. */
enum class Eje {
    NINGUNO, LINEA, MARCAS, REMATES, CORCHETES, DOBLE, FLORON, GRUESA, REMACHES, REGLA, GALONES, BROTES, CUENTAS,
    ATRAVESADAS, SELLOS, PINACULOS, SOLO_MARCAS, SONDA, ALMENAS, FLECHA, COMPAS,
    /** Un cabo trenzado con un nudo en cada marca. */
    CABO,
    AGUZADA, PICA, RUNAS,
}

/** La forma de las barras. */
enum class FormaBarra {
    RECTA, REDONDA, ARCO, PILDORA, BISEL, OJIVA, HERRADURA, TAPA, TORRE, BLOQUES, ROLLO, TANZAKU, BANDERIN, SESGADA, TERCIOS,
    ESTRIADA, OBELISCO, TABLAS, MARCO, PUNTEADO, LAZO, RAYADA, HUECA, BURBUJAS,
    /** Mesoamérica: el extremo baja en gradas, como el perfil de un basamento escalonado. */
    GRADAS,
}

/** Píxeles por dp en los reportes (la hoja mide 1080 px de ancho, como un teléfono de unos 540 dp). */
const val ESCALA_REPORTE = 2f

private val ESTILOS_GRAFICA: Map<TemaId, EstiloGrafica> by lazy { TemaId.entries.associateWith { crearEstiloGrafica(it) } }

/** El estilo de gráfica del tema. */
fun estiloGrafica(tema: TemaId = Estilo.tema): EstiloGrafica = ESTILOS_GRAFICA.getValue(tema)

private fun crearEstiloGrafica(t: TemaId): EstiloGrafica = when (t) {
    // Inscripción: interpunct triangular, líneas finas cinceladas, eje con remates de serif y barras como columnas con capitel.
    TemaId.ROMANO -> EstiloGrafica(FormaPunto.TRIANGULO, 1.8f, null, StrokeCap.Butt, StrokeJoin.Miter, AdornoTrazo.NINGUNO, listOf(6f, 3f),
        Rejilla.LINEAS, 0.55f, Eje.REMATES, FormaBarra.TAPA, R.font.plex_mono_regular, FontWeight.Normal, null, 0.02f, 1f)
    // Roma de hoy: puntos en forma de cúpula, la línea de la ruta del autobús (guion largo y punto), la pauta del cuaderno
    // de fondo, el eje como una regla y las barras como pilares de arcada; cifras de letrero, en Titillium.
    TemaId.URBE -> EstiloGrafica(FormaPunto.CUPOLA, 2.2f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.NINGUNO, listOf(8f, 2f, 1f, 2f),
        Rejilla.PAUTA, 0.45f, Eje.REGLA, FormaBarra.ARCO, R.font.titillium_semibold, FontWeight.SemiBold, "tnum", 0.03f, 1.02f)
    // Terminal: píxel de fósforo con halo, trazo en segmentos de barrido, matriz de puntos del monitor y barras de vúmetro.
    TemaId.CIBER -> EstiloGrafica(FormaPunto.PIXEL, 1.6f, listOf(4f, 1.2f), StrokeCap.Butt, StrokeJoin.Miter, AdornoTrazo.HALO, listOf(0.1f, 2.2f),
        Rejilla.MATRIZ, 0.6f, Eje.CORCHETES, FormaBarra.BLOQUES, R.font.sharetech_mono, FontWeight.Normal, null, 0.04f, 1.05f)
    // Ciudad neón: tubos redondos con su halo, puntos como gotas de lluvia, rejilla de guiones y barras de cápsula, como letreros.
    TemaId.NEON -> EstiloGrafica(FormaPunto.GOTA, 2f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.HALO, listOf(5f, 3f),
        Rejilla.DISCONTINUA, 0.45f, Eje.LINEA, FormaBarra.PILDORA, R.font.chakra_semibold, FontWeight.SemiBold, "tnum", 0.06f, 1f)
    // El sol de Vergina, doble filete de vaso, barras como columnas estriadas.
    TemaId.HELENO -> EstiloGrafica(FormaPunto.SOL, 1.8f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.NINGUNO, listOf(4f, 2f, 1f, 2f),
        Rejilla.DOBLE, 0.5f, Eje.DOBLE, FormaBarra.ESTRIADA, R.font.garamond_medium, FontWeight.Medium, "lnum, tnum", 0.03f, 1.12f)
    // El anillo shen, el trazo hundido de un relieve, los registros de un muro, el collar de cuentas y barras como obeliscos de punta dorada.
    TemaId.EGIPTO -> EstiloGrafica(FormaPunto.ANILLO, 1.7f, null, StrokeCap.Butt, StrokeJoin.Miter, AdornoTrazo.SOMBRA, listOf(5f, 2f),
        Rejilla.BANDAS, 0.5f, Eje.CUENTAS, FormaBarra.OBELISCO, R.font.messiri_bold, FontWeight.Bold, "lnum, tnum", 0.02f, 1.05f)
    // Scriptorium: cuadrifolio, trazo de pluma, renglones con el picado del copista, floroncillos y arcos de medio punto.
    TemaId.MEDIEVAL -> EstiloGrafica(FormaPunto.FLOR, 2f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.PLUMA, listOf(0.1f, 2.6f),
        Rejilla.PICADO, 0.5f, Eje.FLORON, FormaBarra.ARCO, R.font.garamond_regular, FontWeight.Normal, "onum", 0f, 1.15f)
    // Templario: la cruz patada, trazo firme con sombra, rejilla de hiladas alternas (los sillares), el eje almenado de
    // la muralla y barras como gallardetes de lanza.
    TemaId.TEMPLARIO -> EstiloGrafica(FormaPunto.PATADA, 2.2f, null, StrokeCap.Square, StrokeJoin.Miter, AdornoTrazo.SOMBRA, listOf(5f, 2f),
        Rejilla.ALTERNA, 0.4f, Eje.ALMENAS, FormaBarra.BANDERIN, R.font.regla_bold, FontWeight.Bold, "lnum, tnum", 0.04f, 1.05f)
    // Expediente: el ojo, trazo grueso, renglones de registro en bandas, eje duro con marcas y barras rectas.
    TemaId.DISTOPIA -> EstiloGrafica(FormaPunto.DIANA, 2.6f, null, StrokeCap.Square, StrokeJoin.Miter, AdornoTrazo.NINGUNO, listOf(8f, 3f),
        Rejilla.BANDAS, 0.55f, Eje.GRUESA, FormaBarra.RECTA, R.font.oswald_medium, FontWeight.Medium, "tnum", 0.05f, 1.05f)
    // Refugio: la mira «+» de la pantalla, trazo con resplandor, cruces de retícula y barras de esquinas suaves.
    TemaId.REFUGIO -> EstiloGrafica(FormaPunto.CRUZ, 2f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.HALO, listOf(3f, 3f),
        Rejilla.CRUCES, 0.55f, Eje.MARCAS, FormaBarra.REDONDA, R.font.vt323, FontWeight.Normal, null, 0.02f, 1.35f)
    // Orden: escudo, trazo de estarcido (con puentes), eje remachado y placas biseladas.
    TemaId.ORDEN -> EstiloGrafica(FormaPunto.ESCUDO, 2.2f, listOf(7f, 1.6f), StrokeCap.Butt, StrokeJoin.Miter, AdornoTrazo.NINGUNO, listOf(0.1f, 2.4f),
        Rejilla.DISCONTINUA, 0.5f, Eje.REMACHES, FormaBarra.BISEL, R.font.saira_stencil, FontWeight.Normal, null, 0.06f, 1f)
    // Laboratorio: hexágono, trazo fino, papel milimetrado, eje de regla y barras huecas de probeta.
    TemaId.LABORATORIO -> EstiloGrafica(FormaPunto.HEXAGONO, 1.4f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.NINGUNO, listOf(1.5f, 1.5f),
        Rejilla.MILIMETRADO, 0.6f, Eje.REGLA, FormaBarra.HUECA, R.font.jost_regular, FontWeight.Normal, "tnum", 0.03f, 1.05f)
    // Gaceta: cuadradito de imprenta, raya inglesa (gruesa y fina) y barras de tablones.
    TemaId.COLONOS -> EstiloGrafica(FormaPunto.CUADRADO, 1.8f, null, StrokeCap.Butt, StrokeJoin.Bevel, AdornoTrazo.NINGUNO, listOf(5f, 2.5f),
        Rejilla.FILETES, 0.45f, Eje.LINEA, FormaBarra.TABLAS, R.font.fell, FontWeight.Normal, null, 0.01f, 1.12f)
    // Imperio: destello ✦, trazo con filete dorado, cuadrícula de estado mayor, galones y barras con marco.
    TemaId.NAPOLEON -> EstiloGrafica(FormaPunto.DESTELLO, 2.4f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.FILETE, listOf(6f, 2f, 1f, 2f),
        Rejilla.RETICULA, 0.4f, Eje.GALONES, FormaBarra.MARCO, R.font.bodoni_titulo, FontWeight.Normal, "lnum", 0.05f, 1.08f)
    // Naturalista: hoja, espécimen con sombra, hileras de hormigas, eje con brotes y barras punteadas de grabado.
    TemaId.BOTANICO -> EstiloGrafica(FormaPunto.HOJA, 1.6f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.SOMBRA, listOf(0.1f, 3f),
        Rejilla.PUNTEADA, 0.6f, Eje.BROTES, FormaBarra.PUNTEADO, R.font.fraunces_titulo, FontWeight.Normal, null, 0f, 1.05f)
    // Ciudad verde: hoja de hiedra, trazo con su sombra en el hormigón, metas como la línea discontinua de una calle, rejilla de
    // cruces (las juntas de las losas), eje con marcas, barras de bloques de hormigón apilados y cifras de letrero pintado.
    TemaId.RUINAS -> EstiloGrafica(FormaPunto.HIEDRA, 2f, null, StrokeCap.Butt, StrokeJoin.Bevel, AdornoTrazo.SOMBRA, listOf(8f, 5f),
        Rejilla.CRUCES, 0.5f, Eje.MARCAS, FormaBarra.BLOQUES, R.font.letrero_titulo, FontWeight.Normal, "tnum", 0.02f, 1.08f)
    // Navidad: copo, bastón de caramelo, guirnaldas, eje de foquitos y barras con lazo de regalo.
    TemaId.NAVIDAD -> EstiloGrafica(FormaPunto.COPO, 2.2f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.RAYAS, listOf(4f, 2.5f),
        Rejilla.GUIRNALDA, 0.5f, Eje.CUENTAS, FormaBarra.LAZO, R.font.cinzel_deco_regular, FontWeight.Normal, null, 0.04f, 1f)
    // Ruta de la Seda: moneda con agujero, dunas, eje como varilla de ábaco y barras como rollos de seda.
    TemaId.SEDA -> EstiloGrafica(FormaPunto.MONEDA, 1.8f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.NINGUNO, listOf(7f, 2f, 2f, 2f),
        Rejilla.ONDAS, 0.45f, Eje.ATRAVESADAS, FormaBarra.ROLLO, R.font.almendra_regular, FontWeight.Normal, null, 0.02f, 1.1f)
    // Japón Edo: flor de cerezo, trazo de pincel que se afila en las puntas, bandas de niebla, eje con sellitos y barras de tanzaku.
    TemaId.EDO -> EstiloGrafica(FormaPunto.SAKURA, 2f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.NINGUNO, listOf(10f, 3f),
        Rejilla.KASUMI, 0.5f, Eje.SELLOS, FormaBarra.TANZAKU, R.font.zenmincho_regular, FontWeight.Normal, null, 0.01f, 1.05f)
    // Gótico: losange de vitral, plomo grueso, celosía de vidrieras, pináculos y barras en ojiva.
    TemaId.GOTICO -> EstiloGrafica(FormaPunto.ROMBO, 2.6f, null, StrokeCap.Butt, StrokeJoin.Miter, AdornoTrazo.NINGUNO, listOf(5f, 1.5f),
        Rejilla.CELOSIA, 0.45f, Eje.PINACULOS, FormaBarra.OJIVA, R.font.gotica_semibold, FontWeight.SemiBold, null, 0.02f, 1.15f)
    // Horror cósmico: fases de la luna, tinta que falla a ratos, renglones de libreta y barras tachadas, como cifras corregidas.
    TemaId.ARCANO -> EstiloGrafica(FormaPunto.LUNA, 1.7f, listOf(12f, 1.6f, 3f, 1.6f), StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.SOMBRA, listOf(0.1f, 3.5f),
        Rejilla.PAUTA, 0.5f, Eje.MARCAS, FormaBarra.RAYADA, R.font.castoro_regular, FontWeight.Normal, "lnum", 0.01f, 1.06f)
    // Espacio: anillo de planeta, cielo de estrellas, solo las marcas del instrumento y barras en cápsula.
    TemaId.ESPACIO -> EstiloGrafica(FormaPunto.ANILLO, 1.6f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.NINGUNO, listOf(0.1f, 4f),
        Rejilla.ESTRELLAS, 0.6f, Eje.SOLO_MARCAS, FormaBarra.PILDORA, R.font.orbitron_medium, FontWeight.Medium, null, 0.08f, 0.92f)
    // Abismo: gotas de agua, las isóbatas de una carta del fondo, el cabo del escandallo y columnas de agua con burbujas.
    TemaId.ABISMO -> EstiloGrafica(FormaPunto.GOTA, 1.7f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.NINGUNO, listOf(4f, 1.5f, 1f, 1.5f),
        Rejilla.ISOBATAS, 0.55f, Eje.SONDA, FormaBarra.BURBUJAS, R.font.batiscafo_medium, FontWeight.Medium, "tnum", 0.02f, 1.1f)
    // Al-Ándalus: estrella de ocho, cadena de rombos de azulejo, almenas y arcos de herradura.
    TemaId.ANDALUS -> EstiloGrafica(FormaPunto.OCTOGRAMA, 1.8f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.NINGUNO, listOf(3f, 1.5f, 1f, 1.5f),
        Rejilla.ROMBOS, 0.5f, Eje.ALMENAS, FormaBarra.HERRADURA, R.font.kufi_medium, FontWeight.Medium, null, 0.02f, 1f)
    // Mesoamérica: bloques de glifo como puntos, metas en puntos (los del numeral), los renglones rojos del códice, el eje
    // ensartado de cuentas de jade y barras que rematan en gradas de pirámide.
    TemaId.MESO -> EstiloGrafica(FormaPunto.CUADRADO, 1.9f, null, StrokeCap.Square, StrokeJoin.Miter, AdornoTrazo.NINGUNO, listOf(0.1f, 3.2f),
        Rejilla.FILETES, 0.5f, Eje.CUENTAS, FormaBarra.GRADAS, R.font.alegreya_sc_bold, FontWeight.Bold, "lnum, tnum", 0.03f, 1.08f)
    // Estepa: luna, costura de fieltro, cielo abierto (solo muescas), eje como flecha emplumada y barras de banderín.
    TemaId.MONGOL -> EstiloGrafica(FormaPunto.LUNA, 2f, listOf(9f, 2.4f), StrokeCap.Butt, StrokeJoin.Round, AdornoTrazo.NINGUNO, listOf(0.1f, 3f),
        Rejilla.MUESCAS, 0.7f, Eje.FLECHA, FormaBarra.BANDERIN, R.font.philosopher_regular, FontWeight.Normal, null, 0.03f, 1.08f)
    // Leonardo: mira de compás, trazo con línea de construcción, punto y raya, eje con sobretrazo y sombreado de plumilla.
    TemaId.RENACIMIENTO -> EstiloGrafica(FormaPunto.MIRA, 1.4f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.BOCETO, listOf(9f, 2f, 1f, 2f),
        Rejilla.PUNTO_Y_RAYA, 0.5f, Eje.COMPAS, FormaBarra.RAYADA, R.font.cormorant_medium, FontWeight.Medium, "onum", 0f, 1.2f)
    // La carta del tesoro: la «X» en cada dato, la derrota punteada, las ondas del mar al fondo, el eje de cabo y las barras como tablones.
    TemaId.PIRATA -> EstiloGrafica(FormaPunto.EQUIS, 1.5f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.SOMBRA, listOf(0.1f, 3f),
        Rejilla.ONDAS, 0.5f, Eje.CABO, FormaBarra.TABLAS, R.font.caslon_semibold, FontWeight.SemiBold, "lnum", 0.02f, 1.05f)
    // Constructivismo: estrella, trazo grueso con impresión corrida, líneas alternas, la cuña y barras cortadas en diagonal.
    TemaId.URSS -> EstiloGrafica(FormaPunto.ESTRELLA, 3f, null, StrokeCap.Butt, StrokeJoin.Miter, AdornoTrazo.SOMBRA, listOf(9f, 3f),
        Rejilla.ALTERNA, 0.5f, Eje.AGUZADA, FormaBarra.SESGADA, R.font.russo, FontWeight.Normal, null, 0.03f, 1f)
    // Revolución: escarapela plisada, cuaderno de quejas pautado, eje como pica y barras en tres franjas.
    TemaId.REVOLUCION -> EstiloGrafica(FormaPunto.ESCARAPELA, 2.2f, null, StrokeCap.Square, StrokeJoin.Bevel, AdornoTrazo.NINGUNO, listOf(4f, 1.5f, 4f, 3f),
        Rejilla.PAUTA, 0.5f, Eje.PICA, FormaBarra.TERCIOS, R.font.bodoni_texto, FontWeight.Normal, "lnum", 0.01f, 1.1f, cursivaCifras = true)
    // Tierras Antiguas: gema tallada, surcos de cincel, eje rúnico y barras como torres almenadas.
    TemaId.ANTIGUAS -> EstiloGrafica(FormaPunto.GEMA, 2f, null, StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.SOMBRA, listOf(2.5f, 1.5f, 0.5f, 1.5f),
        Rejilla.TALLADA, 0.55f, Eje.RUNAS, FormaBarra.TORRE, R.font.cinzel_semibold, FontWeight.SemiBold, null, 0.05f, 1f)
    // Prehistoria: manos de ocre por puntos, un trazo de carbón que se corta con su boceto debajo, referencias en hilera de
    // puntos, muescas en la rejilla y cifras a mano alzada.
    TemaId.CUEVA -> EstiloGrafica(FormaPunto.MANO, 2.4f, listOf(7f, 1.5f), StrokeCap.Round, StrokeJoin.Round, AdornoTrazo.BOCETO, listOf(0.1f, 5f),
        Rejilla.MUESCAS, 0.5f, Eje.CUENTAS, FormaBarra.REDONDA, R.font.amatic_bold, FontWeight.Bold, null, 0.02f, 1.3f)
}

// =====================================================================
//  Cifras
// =====================================================================

/** La letra de las cifras sobre un estilo de texto (conserva color y lo demás de [base]). */
fun EstiloGrafica.textoCifras(base: TextStyle): TextStyle = base.copy(
    fontFamily = familiaCifras, fontWeight = pesoCifras, fontFeatureSettings = rasgosCifras ?: base.fontFeatureSettings,
    letterSpacing = espaciadoCifras.em, fontStyle = if (cursivaCifras) FontStyle.Italic else FontStyle.Normal,
    fontSize = if (base.fontSize.isSp) base.fontSize * escalaCifras else base.fontSize,
)

/** El estilo de las cifras de las gráficas del tema, para `Text` o `drawText`. */
@Composable
fun estiloCifras(base: TextStyle = MaterialTheme.typography.labelSmall, tema: TemaId = Estilo.tema): TextStyle = estiloGrafica(tema).textoCifras(base)

private val TIPOS_CIFRAS = ConcurrentHashMap<Int, Typeface>()

/** La letra de las cifras como `Typeface` de android (se carga una vez). */
fun EstiloGrafica.tipoCifras(ctx: Context): Typeface = TIPOS_CIFRAS[letraCifras]
    ?: (runCatching { ResourcesCompat.getFont(ctx, letraCifras) }.getOrNull() ?: Typeface.DEFAULT).also { TIPOS_CIFRAS[letraCifras] = it }

/** Pone a [paint] la letra, el espaciado, los rasgos y la escala de las cifras del tema. Llamarla una sola vez por pincel (multiplica el tamaño). */
fun EstiloGrafica.pincelCifras(paint: Paint, ctx: Context): Paint {
    val tf = tipoCifras(ctx); val esp = espaciadoCifras; val rasgos = rasgosCifras; val esc = escalaCifras; val cursiva = cursivaCifras
    return paint.apply {
        typeface = tf; letterSpacing = esp; textSize *= esc
        if (rasgos != null) fontFeatureSettings = rasgos
        textSkewX = if (cursiva) -0.2f else 0f
        isAntiAlias = true
    }
}

/** Un pincel de android para las cifras de una gráfica de Compose (`nativeCanvas.drawText`). Se crea fuera del `Canvas`. */
@Composable
fun pincelDeCifras(tinta: Color, tam: Float, alinear: Paint.Align = Paint.Align.LEFT, tema: TemaId = Estilo.tema): Paint {
    val ctx = LocalContext.current
    return remember(tinta, tam, alinear, tema) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG); p.color = tinta.toArgb(); p.textSize = tam; p.textAlign = alinear
        estiloGrafica(tema).pincelCifras(p, ctx)
    }
}

// =====================================================================
//  Puntos
// =====================================================================

/** Un punto de datos con la forma del tema, centrado en [centro]. [hueco]: solo el contorno. */
fun DrawScope.puntoGrafica(e: EstiloGrafica, centro: Offset, radio: Float, color: Color, hueco: Boolean = false) {
    val r = radio.coerceAtLeast(0.5f)
    val g = max(1f, r * 0.32f)
    val c = centro
    fun figura(p: Path) { if (hueco) drawPath(p, color, style = Stroke(g, join = StrokeJoin.Round)) else drawPath(p, color) }
    when (e.punto) {
        FormaPunto.CIRCULO -> if (hueco) drawCircle(color, r - g / 2, c, style = Stroke(g)) else drawCircle(color, r, c)
        FormaPunto.ANILLO -> { drawCircle(color, r - g / 2, c, style = Stroke(g)); if (!hueco) drawCircle(color, r * 0.32f, c) }
        FormaPunto.DIANA -> {
            drawCircle(color, r - g * 0.4f, c, style = Stroke(g * 0.8f))
            if (hueco) drawCircle(color, r * 0.36f, c, style = Stroke(g * 0.6f)) else drawCircle(color, r * 0.45f, c)
        }
        FormaPunto.MIRA -> {
            drawCircle(color, r * 0.72f, c, style = Stroke(g * 0.6f))
            drawLine(color, Offset(c.x - r * 1.15f, c.y), Offset(c.x + r * 1.15f, c.y), g * 0.55f)
            drawLine(color, Offset(c.x, c.y - r * 1.15f), Offset(c.x, c.y + r * 1.15f), g * 0.55f)
            if (!hueco) drawCircle(color, r * 0.3f, c)
        }
        FormaPunto.EQUIS -> {
            val a = r * 0.95f; val ancho = if (hueco) g * 0.8f else g * 1.4f
            drawLine(color, Offset(c.x - a, c.y - a), Offset(c.x + a, c.y + a), ancho, StrokeCap.Round)
            drawLine(color, Offset(c.x + a, c.y - a), Offset(c.x - a, c.y + a), ancho, StrokeCap.Round)
        }
        FormaPunto.TRIANGULO -> figura(egPoligono(c, r * 1.15f, 3, -90f))
        // La cúpula: casquete, tambor y linterna.
        FormaPunto.CUPOLA -> figura(Path().apply {
            moveTo(c.x - r, c.y + 0.75f * r); lineTo(c.x - r, c.y + 0.2f * r)
            arcTo(Rect(c.x - r, c.y - 0.8f * r, c.x + r, c.y + 1.2f * r), 180f, 180f, false)
            lineTo(c.x + r, c.y + 0.75f * r); close()
            addRect(Rect(c.x - 0.2f * r, c.y - 1.15f * r, c.x + 0.2f * r, c.y - 0.75f * r))
        })
        FormaPunto.CUADRADO -> figura(egPoligono(c, r * 1.2f, 4, 45f))
        FormaPunto.ROMBO -> figura(egPoligono(c, r * 1.3f, 4, -90f))
        FormaPunto.HEXAGONO -> figura(egPoligono(c, r * 1.12f, 6, -90f))
        FormaPunto.PIXEL -> {
            drawRect(color.copy(alpha = color.alpha * 0.22f), Offset(c.x - r * 1.6f, c.y - r * 1.6f), Size(r * 3.2f, r * 3.2f))
            val l = r * 0.85f
            if (hueco) drawRect(color, Offset(c.x - l, c.y - l), Size(2 * l, 2 * l), style = Stroke(g))
            else drawRect(color, Offset(c.x - l, c.y - l), Size(2 * l, 2 * l))
        }
        FormaPunto.SOL -> figura(egEstrella(c, r * 1.25f, 8, 0.45f))
        FormaPunto.OCTOGRAMA -> figura(egEstrella(c, r * 1.2f, 8, 0.765f, -67.5f))
        FormaPunto.ESTRELLA -> figura(egEstrella(c, r * 1.3f, 5, 0.42f))
        FormaPunto.DESTELLO -> figura(egEstrella(c, r * 1.35f, 4, 0.32f))
        FormaPunto.CRUZ -> figura(egRuta(c, r * 1.1f, -0.3f, -1f, 0.3f, -1f, 0.3f, -0.3f, 1f, -0.3f, 1f, 0.3f, 0.3f, 0.3f,
            0.3f, 1f, -0.3f, 1f, -0.3f, 0.3f, -1f, 0.3f, -1f, -0.3f, -0.3f, -0.3f))
        FormaPunto.ESCUDO -> figura(Path().apply {
            moveTo(c.x - 0.85f * r, c.y - 0.95f * r); lineTo(c.x + 0.85f * r, c.y - 0.95f * r); lineTo(c.x + 0.85f * r, c.y - 0.15f * r)
            quadraticTo(c.x + 0.8f * r, c.y + 0.6f * r, c.x, c.y + 1.05f * r)
            quadraticTo(c.x - 0.8f * r, c.y + 0.6f * r, c.x - 0.85f * r, c.y - 0.15f * r); close()
        })
        FormaPunto.HOJA -> {
            val a = Offset(c.x - 0.9f * r, c.y + 0.9f * r); val b = Offset(c.x + 0.9f * r, c.y - 0.9f * r)
            figura(Path().apply {
                moveTo(a.x, a.y); quadraticTo(c.x - 1.05f * r, c.y - 1.05f * r, b.x, b.y)
                quadraticTo(c.x + 1.05f * r, c.y + 1.05f * r, a.x, a.y); close()
            })
            if (!hueco && r >= 3f) drawLine(egTinte(color), a, b, max(1f, r * 0.12f))
        }
        // Hoja de hiedra de cinco puntas, la del centro hacia arriba.
        FormaPunto.HIEDRA -> figura(Path().apply {
            val s = r * 1.15f
            moveTo(c.x, c.y + 0.75f * s); lineTo(c.x - 0.55f * s, c.y + 0.55f * s); lineTo(c.x - 0.95f * s, c.y); lineTo(c.x - 0.38f * s, c.y - 0.05f * s)
            lineTo(c.x, c.y - 0.95f * s); lineTo(c.x + 0.38f * s, c.y - 0.05f * s); lineTo(c.x + 0.95f * s, c.y); lineTo(c.x + 0.55f * s, c.y + 0.55f * s); close()
        })
        FormaPunto.COPO -> {
            for (k in 0 until 3) {
                val ang = (k * 60f + 90f) * (PI.toFloat() / 180f)
                val v = Offset(cos(ang), sin(ang)) * (r * 1.1f)
                drawLine(color, c - v, c + v, g * 0.8f, StrokeCap.Round)
            }
            if (!hueco) drawCircle(color, r * 0.28f, c)
        }
        FormaPunto.MONEDA -> {
            val h = r * 0.32f
            if (hueco) {
                drawCircle(color, r - g / 2, c, style = Stroke(g * 0.8f))
                drawRect(color, Offset(c.x - h, c.y - h), Size(2 * h, 2 * h), style = Stroke(g * 0.6f))
            } else drawPath(Path().apply { fillType = PathFillType.EvenOdd; addOval(Rect(c, r)); addRect(Rect(c.x - h, c.y - h, c.x + h, c.y + h)) }, color)
        }
        FormaPunto.SAKURA -> {
            // Flor de cerezo: cinco pétalos redondos alrededor del centro.
            var f = Path()
            for (k in 0 until 5) {
                val ang = (k * 72f - 90f) * (PI.toFloat() / 180f)
                val petalo = Path().apply { addOval(Rect(Offset(c.x + r * 0.52f * cos(ang), c.y + r * 0.52f * sin(ang)), r * 0.5f)) }
                f = if (k == 0) petalo else Path.combine(PathOperation.Union, f, petalo)
            }
            figura(f)
            if (!hueco && r >= 3f) drawCircle(egTinte(color), r * 0.22f, c)
        }
        FormaPunto.LUNA -> figura(Path.combine(PathOperation.Difference, Path().apply { addOval(Rect(c, r)) },
            Path().apply { addOval(Rect(Offset(c.x + r * 0.42f, c.y - r * 0.22f), r * 0.82f)) }))
        FormaPunto.FLOR -> {
            val o = r * 0.48f; val rr = r * 0.55f
            var f = Path().apply { addOval(Rect(Offset(c.x - o, c.y), rr)) }
            listOf(Offset(c.x + o, c.y), Offset(c.x, c.y - o), Offset(c.x, c.y + o)).forEach { q ->
                f = Path.combine(PathOperation.Union, f, Path().apply { addOval(Rect(q, rr)) })
            }
            figura(f)
        }
        FormaPunto.ESCARAPELA -> {
            val p = Path().apply { fillType = PathFillType.EvenOdd }
            val n = 12
            fun en(ang: Float, rad: Float) = Offset(c.x + rad * cos(ang), c.y + rad * sin(ang))
            val paso = 2f * PI.toFloat() / n
            val p0 = en(0f, r * 0.84f); p.moveTo(p0.x, p0.y)
            for (i in 1..n) {
                val q = en((i - 0.5f) * paso, r * 1.16f); val s = en(i * paso, r * 0.84f)
                p.quadraticTo(q.x, q.y, s.x, s.y)
            }
            p.close(); p.addOval(Rect(c, r * 0.3f))
            if (hueco) drawPath(p, color, style = Stroke(g * 0.7f)) else drawPath(p, color)
        }
        FormaPunto.GEMA -> {
            val s = r * 1.1f
            figura(egRuta(c, s, -0.75f, -0.3f, -0.4f, -0.8f, 0.4f, -0.8f, 0.75f, -0.3f, 0f, 0.95f))
            if (!hueco && r >= 3f) drawLine(egTinte(color), Offset(c.x - 0.75f * s, c.y - 0.3f * s), Offset(c.x + 0.75f * s, c.y - 0.3f * s), max(1f, r * 0.12f))
        }
        FormaPunto.GOTA -> figura(Path().apply {
            moveTo(c.x, c.y - 1.1f * r)
            cubicTo(c.x + 0.35f * r, c.y - 0.55f * r, c.x + 0.85f * r, c.y - 0.1f * r, c.x + 0.85f * r, c.y + 0.32f * r)
            cubicTo(c.x + 0.85f * r, c.y + 0.82f * r, c.x + 0.42f * r, c.y + 1.05f * r, c.x, c.y + 1.05f * r)
            cubicTo(c.x - 0.42f * r, c.y + 1.05f * r, c.x - 0.85f * r, c.y + 0.82f * r, c.x - 0.85f * r, c.y + 0.32f * r)
            cubicTo(c.x - 0.85f * r, c.y - 0.1f * r, c.x - 0.35f * r, c.y - 0.55f * r, c.x, c.y - 1.1f * r); close()
        })
        FormaPunto.PATADA -> figura(cruzPatada(c, r * 1.15f, 0.16f, 0.45f))
        FormaPunto.MANO -> figura(manoUnida(c, r * 1.15f))
    }
}

// =====================================================================
//  Líneas
// =====================================================================

/** Una ruta a partir de puntos; un `null` corta la línea (día sin dato). */
fun rutaGrafica(puntos: List<Offset?>): Path {
    val p = Path(); var abierta = false
    puntos.forEach { o ->
        if (o == null) abierta = false
        else if (!abierta) { p.moveTo(o.x, o.y); abierta = true } else p.lineTo(o.x, o.y)
    }
    return p
}

/**
 * Una serie con el trazo del tema. [ancho] en px: si viene, manda sobre el del tema (para conservar jerarquías como
 * «7 días fino, 30 días grueso»). [acento]: color del filete o de las franjas; por omisión, un tono más claro u oscuro del color.
 */
fun DrawScope.lineaGrafica(e: EstiloGrafica, ruta: Path, color: Color, ancho: Float? = null, acento: Color = Color.Unspecified) {
    val d = 1.dp.toPx()
    val w = ancho ?: (e.ancho * d)
    val efecto = e.guiones?.let { g -> PathEffect.dashPathEffect(FloatArray(g.size) { g[it] * d }) }
    fun trazo(a: Float, ef: PathEffect? = efecto) = Stroke(width = a, cap = e.tapa, join = e.union, pathEffect = ef)
    val tinte = if (acento != Color.Unspecified) acento else egTinte(color)
    when (e.adorno) {
        AdornoTrazo.HALO -> {
            drawPath(ruta, color, alpha = 0.1f, style = Stroke(width = w * 4.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(ruta, color, alpha = 0.22f, style = Stroke(width = w * 2.2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        AdornoTrazo.SOMBRA -> translate(0.9f * d + w * 0.3f, 1.3f * d + w * 0.4f) { drawPath(ruta, color, alpha = 0.3f, style = trazo(w)) }
        AdornoTrazo.NINGUNO, AdornoTrazo.FILETE, AdornoTrazo.PLUMA, AdornoTrazo.BOCETO, AdornoTrazo.RAYAS -> {}
    }
    drawPath(ruta, color, style = trazo(w))
    when (e.adorno) {
        AdornoTrazo.FILETE -> drawPath(ruta, tinte, style = trazo(max(1f, w * 0.32f)))
        AdornoTrazo.PLUMA -> translate(0.8f * d, -0.8f * d) { drawPath(ruta, color, style = trazo(w * 0.5f)) }
        AdornoTrazo.BOCETO -> translate(0.6f * d, 1.6f * d) {
            drawPath(ruta, color, alpha = 0.4f, style = Stroke(width = max(1f, w * 0.35f), cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        AdornoTrazo.RAYAS -> drawPath(ruta, tinte, style = Stroke(width = w * 0.62f, cap = StrokeCap.Butt, join = e.union,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(w * 1.3f, w * 1.3f))))
        AdornoTrazo.NINGUNO, AdornoTrazo.HALO, AdornoTrazo.SOMBRA -> {}
    }
}

/** Una serie a partir de sus puntos (un `null` la corta). */
fun DrawScope.lineaGrafica(e: EstiloGrafica, puntos: List<Offset?>, color: Color, ancho: Float? = null, acento: Color = Color.Unspecified) =
    lineaGrafica(e, rutaGrafica(puntos), color, ancho, acento)

/** Línea de referencia (meta, promedio, tendencia, mes anterior): discontinua, con el patrón de referencia del tema. */
fun DrawScope.lineaMeta(e: EstiloGrafica, desde: Offset, hasta: Offset, color: Color, ancho: Float? = null) {
    val d = 1.dp.toPx()
    val g = e.guionesMeta
    val redondo = g.first() < 0.5f
    val w = ancho ?: max(1f, (if (redondo) 1.8f else 1.2f) * d)
    drawLine(color, desde, hasta, w, if (redondo) StrokeCap.Round else StrokeCap.Butt, PathEffect.dashPathEffect(FloatArray(g.size) { g[it] * d }))
}

// =====================================================================
//  Barras
// =====================================================================

/**
 * Una barra de datos con la forma del tema, en el rectángulo ([arriba], [tam]). La punta (arriba, o la derecha si
 * [horizontal]) queda en su sitio; algunas formas se adornan por dentro o asoman un poco a los lados (el capitel).
 * [pista]: solo la silueta llena, para el carril de fondo. [acento]: color de lazos, marcos y estrías.
 */
fun DrawScope.barraGrafica(e: EstiloGrafica, arriba: Offset, tam: Size, color: Color, horizontal: Boolean = false, pista: Boolean = false,
                           acento: Color = Color.Unspecified) {
    if (tam.width <= 0f || tam.height <= 0f) return
    val d = 1.dp.toPx()
    val t = EgBarra(arriba, tam, horizontal)
    val silueta = egSilueta(e.barra, t, d)
    if (pista) { drawPath(silueta, color); return }
    val tinte = if (acento != Color.Unspecified) acento else egTinte(color)
    val largo = t.largo; val grosor = t.grosor
    fun raya(u0: Float, v0: Float, u1: Float, v1: Float, col: Color, w: Float) = drawLine(col, t.p(u0, v0), t.p(u1, v1), w)
    when (e.barra) {
        FormaBarra.RECTA, FormaBarra.REDONDA, FormaBarra.ARCO, FormaBarra.PILDORA, FormaBarra.BISEL, FormaBarra.OJIVA,
        FormaBarra.HERRADURA, FormaBarra.TAPA, FormaBarra.TORRE, FormaBarra.BLOQUES, FormaBarra.BANDERIN, FormaBarra.SESGADA -> drawPath(silueta, color)
        FormaBarra.TERCIOS -> {
            if (grosor < 5f * d) drawPath(silueta, color)
            else {
                val g = max(1f, grosor * 0.08f); val w3 = (grosor - 2 * g) / 3
                drawPath(t.nueva().also { t.rect(0f, 0f, largo, w3); t.rect(0f, 2 * w3 + 2 * g, largo, grosor) }, color)
                drawPath(t.nueva().also { t.rect(0f, w3 + g, largo, 2 * w3 + g) }, color, alpha = 0.55f)
            }
        }
        FormaBarra.ROLLO -> {
            drawPath(silueta, color)
            val ec = min(grosor * 0.2f, largo / 2)
            if (ec >= 1.5f) drawPath(t.nueva().also { t.ovalo(largo - 2 * ec, 0f, largo, grosor) }, tinte, style = Stroke(max(1f, 0.8f * d)))
        }
        FormaBarra.TANZAKU -> {
            // La tira de papel de los poemas: el doblez cerca de la punta y el agujerito del cordón.
            drawPath(silueta, color)
            if (grosor >= 5f * d && largo >= grosor) {
                raya(largo - grosor * 0.75f, 0f, largo - grosor * 0.75f, grosor, tinte, max(1f, 0.6f * d))
                drawCircle(tinte, max(1f, grosor * 0.11f), t.p(largo - grosor * 0.35f, grosor / 2))
            }
        }
        FormaBarra.ESTRIADA -> {
            drawPath(silueta, color)
            if (grosor >= 4f * d && largo > 3f * d) {
                val n = if (grosor >= 9f * d) 2 else 1
                val margen = min(1.5f * d, largo / 4)
                for (i in 1..n) { val v = grosor * i / (n + 1); raya(margen, v, largo - margen, v, egOscuro(color), max(1f, grosor * 0.08f)) }
            }
        }
        FormaBarra.OBELISCO -> {
            // El fuste y, en la punta, el piramidión dorado.
            drawPath(silueta, color)
            val pp = min(grosor * 0.6f, largo * 0.4f)
            if (pp >= 1.5f * d) drawPath(t.nueva().also { t.ir(largo - pp, grosor * 0.12f); t.a(largo, grosor / 2); t.a(largo - pp, grosor * 0.88f); t.cerrar() }, tinte)
        }
        FormaBarra.TABLAS -> {
            drawPath(silueta, color)
            val tabla = max(grosor * 0.9f, 5f * d)
            var u = tabla
            while (u < largo - 1f * d) {
                raya(u, 0f, u, grosor, egOscuro(color), max(1f, 0.7f * d))
                if (grosor >= 6f * d && u > 2f * d) {
                    drawCircle(egOscuro(color), max(0.7f, 0.5f * d), t.p(u - 1.6f * d, grosor * 0.22f))
                    drawCircle(egOscuro(color), max(0.7f, 0.5f * d), t.p(u - 1.6f * d, grosor * 0.78f))
                }
                u += tabla
            }
        }
        FormaBarra.MARCO -> {
            drawPath(silueta, color)
            val i = 1.6f * d
            if (grosor >= 6f * d && largo >= 6f * d) drawPath(t.nueva().also { t.rect(i, i, largo - i, grosor - i) }, tinte, style = Stroke(max(1f, 0.8f * d)))
        }
        FormaBarra.PUNTEADO -> {
            drawPath(silueta, color, alpha = 0.4f)
            val paso = 2.4f * d
            val puntos = ArrayList<Offset>()
            var u = paso / 2; var fila = 0
            while (u < largo) {
                var v = if (fila % 2 == 0) paso / 2 else paso
                while (v < grosor) { puntos += t.p(u, v); v += paso }
                u += paso; fila++
            }
            drawPoints(puntos, PointMode.Points, color, strokeWidth = max(1.2f, 1.1f * d), cap = StrokeCap.Round)
            raya(largo, 0f, largo, grosor, color, max(1f, 1f * d))
        }
        FormaBarra.LAZO -> {
            drawPath(silueta, color)
            if (grosor >= 5f * d) drawPath(t.nueva().also { t.rect(0f, grosor * 0.39f, largo, grosor * 0.61f) }, tinte)
            if (grosor >= 7f * d && largo >= grosor * 1.2f) {
                val w = max(1f, grosor * 0.08f)
                drawPath(t.nueva().also { t.ovalo(largo - grosor * 0.42f, grosor * 0.12f, largo - grosor * 0.1f, grosor * 0.48f) }, tinte, style = Stroke(w))
                drawPath(t.nueva().also { t.ovalo(largo - grosor * 0.42f, grosor * 0.52f, largo - grosor * 0.1f, grosor * 0.88f) }, tinte, style = Stroke(w))
            }
        }
        FormaBarra.RAYADA -> {
            drawPath(silueta, color, alpha = 0.18f)
            val x0 = arriba.x; val y0 = arriba.y; val alto = tam.height
            val sw = max(1f, 0.9f * d)
            clipPath(silueta) {
                var s = -alto
                while (s < tam.width) { drawLine(color, Offset(x0 + s, y0 + alto), Offset(x0 + s + alto, y0), sw); s += 3.2f * d }
            }
            if (tam.width > sw && tam.height > sw) drawRect(color, Offset(x0 + sw / 2, y0 + sw / 2), Size(tam.width - sw, tam.height - sw), style = Stroke(sw))
            else drawPath(silueta, color)
        }
        FormaBarra.GRADAS -> {
            // El cuerpo con su escalinata pintada a lo largo, en el color del acento.
            drawPath(silueta, color)
            if (grosor >= 6f * d && largo >= 8f * d) {
                var u = grosor * 0.5f
                while (u < largo - grosor * 0.7f) { raya(u, grosor * 0.38f, u, grosor * 0.62f, tinte, max(1f, 0.7f * d)); u += maxOf(max(2.4f * d, grosor * 0.3f), 1f) }
            }
        }
        FormaBarra.BURBUJAS -> {
            // Una columna de agua: el cuerpo lleno y, por dentro, burbujas que suben hacia la punta.
            drawPath(silueta, color)
            if (grosor >= 5f * d && largo >= 6f * d) {
                val rb = grosor * 0.15f; var u = grosor * 0.6f; var k = 0
                while (u < largo - grosor * 0.55f) {
                    drawCircle(tinte, rb * (0.7f + 0.3f * (k % 3)), t.p(u, grosor * (if (k % 2 == 0) 0.36f else 0.62f)), style = Stroke(max(1f, 0.6f * d)))
                    u += maxOf(grosor * 0.9f, 1f); k++
                }
            }
        }
        FormaBarra.HUECA -> {
            drawPath(silueta, color, alpha = 0.22f)
            val sw = max(1f, 1.2f * d)
            if (tam.width > sw && tam.height > sw) drawRect(color, Offset(arriba.x + sw / 2, arriba.y + sw / 2), Size(tam.width - sw, tam.height - sw), style = Stroke(sw))
            else drawPath(silueta, color)
        }
    }
}

/**
 * Ubica una barra en coordenadas propias: u va de la base a la punta (largo) y v cruza su grosor. Así cada forma se
 * escribe una sola vez y sirve igual para barras verticales (crecen hacia arriba) y horizontales (crecen a la derecha).
 */
private class EgBarra(arriba: Offset, tam: Size, val horizontal: Boolean) {
    private val x0 = arriba.x
    private val y0 = arriba.y
    private val alto = tam.height
    val largo = if (horizontal) tam.width else tam.height
    val grosor = if (horizontal) tam.height else tam.width
    var ruta = Path()

    fun p(u: Float, v: Float): Offset = if (horizontal) Offset(x0 + u, y0 + v) else Offset(x0 + v, y0 + alto - u)
    fun nueva(): Path { ruta = Path(); return ruta }
    fun ir(u: Float, v: Float) { val o = p(u, v); ruta.moveTo(o.x, o.y) }
    fun a(u: Float, v: Float) { val o = p(u, v); ruta.lineTo(o.x, o.y) }
    fun q(cu: Float, cv: Float, u: Float, v: Float) { val c = p(cu, cv); val o = p(u, v); ruta.quadraticTo(c.x, c.y, o.x, o.y) }
    fun cc(u1: Float, v1: Float, u2: Float, v2: Float, u: Float, v: Float) {
        val c1 = p(u1, v1); val c2 = p(u2, v2); val o = p(u, v); ruta.cubicTo(c1.x, c1.y, c2.x, c2.y, o.x, o.y)
    }
    fun cerrar() = ruta.close()
    fun rect(u0: Float, v0: Float, u1: Float, v1: Float) { ir(u0, v0); a(u1, v0); a(u1, v1); a(u0, v1); cerrar() }
    fun ovalo(u0: Float, v0: Float, u1: Float, v1: Float) {
        val m = p(u0, v0); val n = p(u1, v1)
        ruta.addOval(Rect(min(m.x, n.x), min(m.y, n.y), max(m.x, n.x), max(m.y, n.y)))
    }
    /** Media vuelta por la punta: de (largo − ru, 0) a (largo − ru, grosor), con radio ru a lo largo. */
    fun vueltaPunta(ru: Float) {
        val k = 0.5523f; val rv = grosor / 2
        cc(largo - ru + k * ru, 0f, largo, rv - k * rv, largo, rv)
        cc(largo, rv + k * rv, largo - ru + k * ru, grosor, largo - ru, grosor)
    }
    /** Media vuelta por la base: de (ru, grosor) a (ru, 0). */
    fun vueltaBase(ru: Float) {
        val k = 0.5523f; val rv = grosor / 2
        cc(ru - k * ru, grosor, 0f, rv + k * rv, 0f, rv)
        cc(0f, rv - k * rv, ru - k * ru, 0f, ru, 0f)
    }
}

/** La silueta de cada forma de barra. */
private fun egSilueta(forma: FormaBarra, t: EgBarra, d: Float): Path {
    t.nueva()
    val l = t.largo; val g = t.grosor
    when (forma) {
        FormaBarra.RECTA, FormaBarra.RAYADA, FormaBarra.HUECA, FormaBarra.PUNTEADO, FormaBarra.ESTRIADA, FormaBarra.TABLAS,
        FormaBarra.MARCO, FormaBarra.LAZO -> t.rect(0f, 0f, l, g)
        FormaBarra.REDONDA -> {
            val r = min(min(g * 0.3f, l / 2), g / 2)
            t.ir(0f, r); t.a(0f, g - r); t.q(0f, g, r, g); t.a(l - r, g); t.q(l, g, l, g - r)
            t.a(l, r); t.q(l, 0f, l - r, 0f); t.a(r, 0f); t.q(0f, 0f, 0f, r); t.cerrar()
        }
        FormaBarra.ARCO -> { val ru = min(g / 2, l); t.ir(0f, 0f); t.a(l - ru, 0f); t.vueltaPunta(ru); t.a(0f, g); t.cerrar() }
        // La columna de agua: base recta y la punta redonda, como la cabeza de una burbuja.
        FormaBarra.BURBUJAS -> { val ru = min(g / 2, l); t.ir(0f, 0f); t.a(l - ru, 0f); t.vueltaPunta(ru); t.a(0f, g); t.cerrar() }
        FormaBarra.PILDORA -> { val ru = min(g / 2, l / 2); t.ir(ru, 0f); t.a(l - ru, 0f); t.vueltaPunta(ru); t.a(ru, g); t.vueltaBase(ru); t.cerrar() }
        FormaBarra.BISEL -> {
            val b = min(g * 0.32f, l / 2)
            t.ir(0f, 0f); t.a(l - b, 0f); t.a(l, b); t.a(l, g - b); t.a(l - b, g); t.a(0f, g); t.cerrar()
        }
        FormaBarra.OJIVA -> {
            val rp = min(g * 0.95f, l)
            t.ir(0f, 0f); t.a(l - rp, 0f)
            t.cc(l - rp * 0.45f, 0f, l - rp * 0.08f, g * 0.28f, l, g / 2)
            t.cc(l - rp * 0.08f, g * 0.72f, l - rp * 0.45f, g, l - rp, g)
            t.a(0f, g); t.cerrar()
        }
        FormaBarra.HERRADURA -> {
            if (l < g * 0.9f) { val ru = min(g / 2, l); t.ir(0f, 0f); t.a(l - ru, 0f); t.vueltaPunta(ru); t.a(0f, g); t.cerrar() }
            else {
                // Cuello más angosto que el arco: el arco de herradura pasa de la media vuelta.
                val n = g * 0.1f; val rv = g / 2; val lc = l - rv; val k = 0.5523f
                t.ir(0f, n); t.a(lc - g * 0.2f, n); t.q(lc - g * 0.12f, 0f, lc, 0f)
                t.cc(lc + k * rv, 0f, l, rv - k * rv, l, rv)
                t.cc(l, rv + k * rv, lc + k * rv, g, lc, g)
                t.q(lc - g * 0.12f, g, lc - g * 0.2f, g - n); t.a(0f, g - n); t.cerrar()
            }
        }
        FormaBarra.TAPA -> {
            // Columna: fuste, capitel que asoma a los lados y, si cabe, basa.
            val s = min(max(2.2f * d, g * 0.2f), l * 0.35f); val o = g * 0.12f
            t.rect(0f, 0f, l, g)
            t.rect(l - s, -o, l, g + o)
            if (l >= 3 * s) t.rect(0f, -o * 0.6f, s * 0.7f, g + o * 0.6f)
        }
        FormaBarra.OBELISCO -> {
            // Se adelgaza un poco hacia la punta y termina en el piramidión.
            val pp = min(g * 0.6f, l * 0.4f)
            t.ir(0f, 0f); t.a(l - pp, g * 0.12f); t.a(l, g / 2); t.a(l - pp, g * 0.88f); t.a(0f, g); t.cerrar()
        }
        FormaBarra.TORRE -> {
            val m = min(g * 0.32f, l * 0.4f); val w5 = g / 5
            t.rect(0f, 0f, l - m, g)
            t.rect(l - m - 0.5f, 0f, l, w5); t.rect(l - m - 0.5f, 2 * w5, l, 3 * w5); t.rect(l - m - 0.5f, 4 * w5, l, g)
        }
        FormaBarra.BLOQUES -> {
            val seg = max(g * 0.5f, 3f * d); val hueco = max(1f, 0.9f * d)
            var u = 0f
            while (u < l) { t.rect(u, 0f, min(u + seg, l), g); u += seg + hueco }
        }
        FormaBarra.ROLLO -> { val ec = min(g * 0.2f, l / 2); t.rect(0f, 0f, l - ec, g); t.ovalo(l - 2 * ec, 0f, l, g) }
        FormaBarra.TANZAKU -> t.rect(0f, 0f, l, g)
        FormaBarra.BANDERIN -> { val n = min(g * 0.42f, l * 0.5f); t.ir(0f, 0f); t.a(l, 0f); t.a(l - n, g / 2); t.a(l, g); t.a(0f, g); t.cerrar() }
        FormaBarra.SESGADA -> { val s = min(g * 0.55f, l); t.ir(0f, 0f); t.a(l, 0f); t.a(l - s, g); t.a(0f, g); t.cerrar() }
        FormaBarra.GRADAS -> {
            // Tres cuerpos que se angostan hacia la punta, como una pirámide vista de lado.
            val s = min(g * 0.3f, l / 4)
            t.ir(0f, 0f); t.a(l - 2 * s, 0f); t.a(l - 2 * s, g * 0.17f); t.a(l - s, g * 0.17f); t.a(l - s, g * 0.34f); t.a(l, g * 0.34f)
            t.a(l, g * 0.66f); t.a(l - s, g * 0.66f); t.a(l - s, g * 0.83f); t.a(l - 2 * s, g * 0.83f); t.a(l - 2 * s, g); t.a(0f, g); t.cerrar()
        }
        FormaBarra.TERCIOS -> {
            if (g < 5f * d) t.rect(0f, 0f, l, g)
            else { val h = max(1f, g * 0.08f); val w3 = (g - 2 * h) / 3; t.rect(0f, 0f, l, w3); t.rect(0f, w3 + h, l, 2 * w3 + h); t.rect(0f, 2 * w3 + 2 * h, l, g) }
        }
    }
    return t.ruta
}

// =====================================================================
//  Rejilla
// =====================================================================

/**
 * Las líneas guía del tema: horizontales en [ys] y verticales en [xs], dentro de [area] (por omisión, todo el lienzo).
 * El [color] lo pone quien llama (suele ser `outline`); el tema solo le baja el alfa.
 */
fun DrawScope.rejillaGrafica(e: EstiloGrafica, ys: List<Float>, color: Color, xs: List<Float> = emptyList(), area: Rect? = null) {
    val a = area ?: Rect(0f, 0f, size.width, size.height)
    if (a.width <= 0f || a.height <= 0f) return
    val d = 1.dp.toPx()
    val c = color.copy(alpha = color.alpha * e.alfaRejilla)
    val g = max(1f, 0.8f * d)
    fun tenue(f: Float) = c.copy(alpha = c.alpha * f)
    fun h(y: Float, col: Color = c, w: Float = g, ef: PathEffect? = null, cap: StrokeCap = StrokeCap.Butt) = drawLine(col, Offset(a.left, y), Offset(a.right, y), w, cap, ef)
    fun v(x: Float, col: Color = c, w: Float = g, ef: PathEffect? = null, cap: StrokeCap = StrokeCap.Butt) = drawLine(col, Offset(x, a.top), Offset(x, a.bottom), w, cap, ef)
    fun guion(vararg l: Float): PathEffect = PathEffect.dashPathEffect(FloatArray(l.size) { l[it] * d })
    fun punteadaV(x: Float, paso: Float = 3f) = v(x, w = g * 1.4f, ef = guion(0.01f, paso), cap = StrokeCap.Round)
    fun columnas(paso: Float): List<Float> { val l = ArrayList<Float>(); var x = a.left + paso / 2; while (x < a.right) { l += x; x += paso }; return l }
    when (e.rejilla) {
        Rejilla.NINGUNA -> {}
        Rejilla.LINEAS -> { ys.forEach { h(it) }; xs.forEach { v(it) } }
        Rejilla.PUNTEADA -> { val ef = guion(0.01f, 3f); ys.forEach { h(it, w = g * 1.5f, ef = ef, cap = StrokeCap.Round) }; xs.forEach { punteadaV(it) } }
        Rejilla.DISCONTINUA -> { val ef = guion(5f, 3f); ys.forEach { h(it, ef = ef) }; xs.forEach { v(it, ef = ef) } }
        Rejilla.MILIMETRADO -> {
            val fina = tenue(0.35f); val wf = max(0.6f, 0.45f * d); val paso = 5f * d
            var y = a.top; while (y <= a.bottom) { h(y, fina, wf); y += paso }
            var x = a.left; while (x <= a.right) { v(x, fina, wf); x += paso }
            ys.forEach { h(it) }; xs.forEach { v(it) }
        }
        Rejilla.CRUCES -> {
            val cols = xs.ifEmpty { columnas(22f * d) }; val b = 2.4f * d
            ys.forEach { y ->
                h(y, tenue(0.3f))
                cols.forEach { x -> drawLine(c, Offset(x - b, y), Offset(x + b, y), g); drawLine(c, Offset(x, y - b), Offset(x, y + b), g) }
            }
            xs.forEach { v(it, tenue(0.3f)) }
        }
        Rejilla.MATRIZ -> {
            val paso = 8f * d
            val fondo = ArrayList<Offset>()
            var y = a.top + paso / 2
            while (y < a.bottom) { var x = a.left + paso / 2; while (x < a.right) { fondo += Offset(x, y); x += paso }; y += paso }
            drawPoints(fondo, PointMode.Points, tenue(0.35f), strokeWidth = 1.2f * d, cap = StrokeCap.Square)
            val filas = ArrayList<Offset>()
            ys.forEach { yy -> var x = a.left; while (x <= a.right) { filas += Offset(x, yy); x += 3f * d } }
            xs.forEach { xx -> var y2 = a.top; while (y2 <= a.bottom) { filas += Offset(xx, y2); y2 += 3f * d } }
            drawPoints(filas, PointMode.Points, c, strokeWidth = 1.3f * d, cap = StrokeCap.Square)
        }
        Rejilla.DOBLE -> { val s = 1.1f * d; ys.forEach { h(it - s, w = g * 0.8f); h(it + s, w = g * 0.8f) }; xs.forEach { v(it - s, w = g * 0.8f); v(it + s, w = g * 0.8f) } }
        Rejilla.PICADO -> {
            ys.forEach { y -> h(y, tenue(0.6f)); drawCircle(c, 0.9f * d, Offset(a.left + 2.5f * d, y)); drawCircle(c, 0.9f * d, Offset(a.right - 2.5f * d, y)) }
            xs.forEach { v(it, tenue(0.6f)) }
        }
        Rejilla.BANDAS -> {
            val o = ys.sorted()
            for (i in 0 until o.size - 1 step 2) drawRect(tenue(0.35f), Offset(a.left, o[i]), Size(a.width, o[i + 1] - o[i]))
            o.forEach { h(it, w = g * 0.7f) }; xs.forEach { v(it, w = g * 0.7f) }
        }
        Rejilla.FILETES -> { ys.forEach { h(it - 0.9f * d, w = g * 1.8f); h(it + 1.4f * d, w = g * 0.6f) }; xs.forEach { v(it) } }
        Rejilla.RETICULA -> {
            ys.forEach { h(it) }
            xs.ifEmpty { columnas(a.width / 8f) }.forEach { v(it, w = g * 0.8f) }
            drawRect(c, a.topLeft, a.size, style = Stroke(g))
        }
        Rejilla.GUIRNALDA -> {
            val tramo = 18f * d; val caida = 2.6f * d
            ys.forEach { y ->
                val p = Path(); p.moveTo(a.left, y)
                var x = a.left
                while (x < a.right) {
                    val x2 = min(x + tramo, a.right)
                    p.quadraticTo((x + x2) / 2, y + caida, x2, y)
                    drawCircle(c, 1f * d, Offset(x2, y))
                    x = x2
                }
                drawPath(p, c, style = Stroke(g))
            }
            xs.forEach { punteadaV(it) }
        }
        Rejilla.ONDAS -> {
            val medio = 7f * d; val amp = 1.8f * d
            ys.forEach { y ->
                val p = Path(); p.moveTo(a.left, y)
                var x = a.left; var sube = true
                while (x < a.right) {
                    val x2 = min(x + medio, a.right)
                    p.quadraticTo((x + x2) / 2, if (sube) y - amp else y + amp, x2, y)
                    sube = !sube; x = x2
                }
                drawPath(p, c, style = Stroke(g))
            }
            xs.forEach { punteadaV(it) }
        }
        Rejilla.KASUMI -> {
            // Bandas de niebla (kasumi), como las que cortan el paisaje en los grabados: tramos largos de puntas redondas.
            ys.forEach { y -> drawLine(c, Offset(a.left, y), Offset(a.right, y), g * 1.6f, StrokeCap.Round, PathEffect.dashPathEffect(floatArrayOf(26f * d, 7f * d), (y * 7f) % (33f * d))) }
            xs.forEach { punteadaV(it) }
        }
        Rejilla.CELOSIA -> {
            clipRect(a.left, a.top, a.right, a.bottom) {
                val paso = 14f * d; val t = tenue(0.3f)
                var s = -a.height
                while (s < a.width + a.height) {
                    drawLine(t, Offset(a.left + s, a.bottom), Offset(a.left + s + a.height, a.top), g * 0.8f)
                    drawLine(t, Offset(a.left + s, a.top), Offset(a.left + s + a.height, a.bottom), g * 0.8f)
                    s += paso
                }
            }
            ys.forEach { h(it) }; xs.forEach { v(it) }
        }
        Rejilla.ISOBATAS -> {
            // Las isóbatas de una carta del fondo: curvas de igual profundidad, suaves e irregulares, cada una distinta.
            ys.forEachIndexed { i, y ->
                val p = Path(); var x = a.left
                p.moveTo(x, y)
                while (x < a.right) {
                    x = min(x + 3f * d, a.right)
                    p.lineTo(x, y + 1.6f * d * sin(x / (23f * d) + i * 1.3f) + 0.7f * d * sin(x / (7f * d) + i))
                }
                drawPath(p, c, style = Stroke(g))
            }
            xs.forEach { punteadaV(it, 4f) }
        }
        Rejilla.ESTRELLAS -> {
            val pts = ArrayList<Offset>(); val chispas = ArrayList<Offset>()
            ys.forEachIndexed { i, y ->
                var x = a.left + (i % 3) * 2f * d; var k = i
                while (x <= a.right) { if (k % 7 == 3) chispas += Offset(x, y) else pts += Offset(x, y); x += 6f * d; k++ }
            }
            drawPoints(pts, PointMode.Points, c, strokeWidth = 1.3f * d, cap = StrokeCap.Round)
            val b = 2.6f * d
            chispas.forEach { s ->
                drawLine(c, Offset(s.x - b, s.y), Offset(s.x + b, s.y), g * 0.8f); drawLine(c, Offset(s.x, s.y - b), Offset(s.x, s.y + b), g * 0.8f)
                drawCircle(c, 0.9f * d, s)
            }
            xs.forEach { punteadaV(it, 6f) }
        }
        Rejilla.ROMBOS -> {
            val m = 1.7f * d
            ys.forEach { y ->
                h(y, tenue(0.5f), g * 0.7f)
                var x = a.left + 6f * d
                while (x < a.right) { drawPath(egPoligono(Offset(x, y), m, 4, -90f), c); x += 12f * d }
            }
            xs.forEach { v(it, tenue(0.5f), g * 0.7f) }
        }
        Rejilla.MUESCAS -> {
            val l = 5f * d
            ys.forEach { y -> drawLine(c, Offset(a.left, y), Offset(a.left + l, y), g * 1.2f); drawLine(c, Offset(a.right - l, y), Offset(a.right, y), g * 1.2f) }
            xs.forEach { x -> drawLine(c, Offset(x, a.bottom - l), Offset(x, a.bottom), g * 1.2f) }
        }
        Rejilla.PUNTO_Y_RAYA -> { val ef = guion(7f, 2f, 1f, 2f); ys.forEach { h(it, ef = ef) }; xs.forEach { v(it, ef = ef) } }
        Rejilla.ALTERNA -> { ys.sorted().forEachIndexed { i, y -> h(y, w = if (i % 2 == 0) g * 2.2f else g * 0.7f) }; xs.forEach { v(it, w = g * 0.7f) } }
        Rejilla.PAUTA -> {
            ys.forEach { h(it) }; xs.forEach { v(it, w = g * 0.7f) }
            val m = a.left + 6f * d; v(m, w = g * 0.8f); v(m + 1.6f * d, w = g * 0.8f)
        }
        Rejilla.TALLADA -> {
            ys.forEachIndexed { i, y ->
                h(y, w = g * 1.3f, ef = PathEffect.dashPathEffect(floatArrayOf(9f * d, 1.6f * d, 4f * d, 1.2f * d, 13f * d, 2f * d, 6f * d, 1.4f * d), i * 11f * d))
            }
            xs.forEach { v(it, ef = guion(9f, 1.6f, 4f, 1.2f)) }
        }
    }
}

// =====================================================================
//  Eje
// =====================================================================

/**
 * El eje del tema, recto (horizontal o vertical). [marcas]: coordenadas x en un eje horizontal, y en uno vertical.
 * Las rayas salen hacia afuera de la gráfica: hacia abajo en un eje horizontal, a la izquierda en uno vertical.
 */
fun DrawScope.ejeGrafica(e: EstiloGrafica, desde: Offset, hasta: Offset, color: Color, marcas: List<Float> = emptyList()) {
    val largo = (hasta - desde).getDistance()
    if (largo <= 0f) return
    val d = 1.dp.toPx()
    val g = max(1f, 1.1f * d)
    val horiz = abs(hasta.x - desde.x) >= abs(hasta.y - desde.y)
    val dir = (hasta - desde) / largo
    val normal = Offset(-dir.y, dir.x)
    val fuera = if (horiz) Offset(0f, 1f) else Offset(-1f, 0f)
    val ms = marcas.map { if (horiz) Offset(it, desde.y) else Offset(desde.x, it) }
    val extremos = listOf(desde, hasta)
    fun linea(w: Float = g) = drawLine(color, desde, hasta, w)
    fun raya(o: Offset, l: Float, w: Float = g) = drawLine(color, o, o + fuera * l, w)
    fun cruzada(o: Offset, l: Float, w: Float = g) = drawLine(color, o - fuera * l, o + fuera * l, w)
    when (e.eje) {
        Eje.NINGUNO -> {}
        Eje.LINEA -> linea()
        Eje.MARCAS -> { linea(); ms.forEach { raya(it, 4f * d) } }
        Eje.REMATES -> { linea(); extremos.forEach { cruzada(it, 3f * d, g * 1.2f) }; ms.forEach { raya(it, 2.5f * d) } }
        Eje.CORCHETES -> {
            linea(); val b = 3f * d
            extremos.forEachIndexed { i, o ->
                val hacia = if (i == 0) dir else -dir
                cruzada(o, b)
                drawLine(color, o + fuera * b, o + fuera * b + hacia * (1.8f * d), g)
                drawLine(color, o - fuera * b, o - fuera * b + hacia * (1.8f * d), g)
            }
            ms.forEach { raya(it, 2f * d) }
        }
        Eje.DOBLE -> { linea(); drawLine(color, desde + fuera * (2.4f * d), hasta + fuera * (2.4f * d), g * 0.6f); ms.forEach { raya(it, 2.4f * d, g * 0.8f) } }
        Eje.FLORON -> { linea(); extremos.forEach { drawPath(egPoligono(it, 2.8f * d, 4, -90f), color) }; ms.forEach { drawCircle(color, 1.1f * d, it + fuera * (2.2f * d)) } }
        Eje.GRUESA -> { linea(g * 2.6f); ms.forEach { raya(it, 5f * d, g * 1.5f) } }
        Eje.REMACHES -> { linea(g * 1.7f); (ms + extremos).forEach { drawCircle(color, 1.7f * d, it) } }
        Eje.SONDA -> {
            // El cabo del escandallo: marcas largas y cortas alternadas y, en la punta, la plomada.
            linea()
            ms.forEachIndexed { i, m -> raya(m, if (i % 2 == 0) 4.5f * d else 2.4f * d) }
            val b = hasta + dir * (2.6f * d)
            drawLine(color, hasta, b, g)
            drawPath(Path().apply {
                val n = normal * (1.8f * d)
                moveTo(b.x + n.x, b.y + n.y); lineTo(b.x - n.x, b.y - n.y)
                val pu = b + dir * (4.6f * d); lineTo(pu.x, pu.y); close()
            }, color)
        }
        Eje.REGLA -> {
            linea()
            val o = ms.sortedBy { if (horiz) it.x else it.y }
            o.forEach { raya(it, 5f * d) }
            for (i in 0 until o.size - 1) for (k in 1..3) raya(o[i] + (o[i + 1] - o[i]) * (k / 4f), if (k == 2) 3.2f * d else 2f * d, g * 0.8f)
        }
        Eje.GALONES -> {
            linea(); val b = 2.4f * d
            ms.forEach { m ->
                val base = m + fuera * (1.2f * d); val pico = m + fuera * (3.6f * d)
                drawLine(color, base - dir * b, pico, g); drawLine(color, pico, base + dir * b, g)
            }
        }
        Eje.BROTES -> {
            linea()
            ms.forEach { m -> drawLine(color, m, m + fuera * (3f * d) - dir * (2f * d), g * 0.9f); drawLine(color, m, m + fuera * (3f * d) + dir * (2f * d), g * 0.9f) }
            drawCircle(color, 1.4f * d, hasta)
        }
        Eje.CUENTAS -> {
            linea(g * 0.8f)
            val tam = if (horiz) Size(2.2f * d, 3.2f * d) else Size(3.2f * d, 2.2f * d)
            ms.forEach { m ->
                drawLine(color, m, m + fuera * (1.4f * d), g * 0.8f)
                val cb = m + fuera * (3f * d)
                drawOval(color, Offset(cb.x - tam.width / 2, cb.y - tam.height / 2), tam)
            }
        }
        Eje.ATRAVESADAS -> { linea(); (ms + extremos).forEach { cruzada(it, 3f * d) } }
        Eje.SELLOS -> {
            // Un sellito en cada marca y, al final, el sello grande con su marco en reserva.
            linea()
            ms.forEach { m -> val q = m + fuera * (2.6f * d); drawRect(color, Offset(q.x - 1.4f * d, q.y - 1.4f * d), Size(2.8f * d, 2.8f * d)) }
            drawRect(color, Offset(hasta.x - 2.6f * d, hasta.y - 2.6f * d), Size(5.2f * d, 5.2f * d), style = Stroke(g))
        }
        Eje.PINACULOS -> {
            linea()
            extremos.forEach { o ->
                val p1 = o - dir * (1.6f * d); val p2 = o + dir * (1.6f * d); val pico = o - fuera * (6f * d)
                drawPath(Path().apply { moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(pico.x, pico.y); close() }, color)
            }
            ms.forEach { raya(it, 2.5f * d) }
        }
        Eje.SOLO_MARCAS -> {
            drawLine(color.copy(alpha = color.alpha * 0.35f), desde, hasta, g * 0.8f, StrokeCap.Round, PathEffect.dashPathEffect(floatArrayOf(0.01f, 4f * d)))
            val b = 2.4f * d
            ms.forEach { m -> drawLine(color, m - dir * b, m + dir * b, g * 0.8f); drawLine(color, m - fuera * b, m + fuera * b, g * 0.8f) }
            extremos.forEach { drawCircle(color, 1.2f * d, it) }
        }
        Eje.ALMENAS -> {
            linea()
            ms.forEach { m ->
                val p1 = m - dir * (1.4f * d); val p2 = m + dir * (1.4f * d) + fuera * (2.6f * d)
                drawRect(color, Offset(min(p1.x, p2.x), min(p1.y, p2.y)), Size(abs(p2.x - p1.x), abs(p2.y - p1.y)))
            }
        }
        Eje.FLECHA -> {
            linea()
            val atras = hasta - dir * (5f * d)
            drawPath(Path().apply {
                moveTo(hasta.x, hasta.y); val l1 = atras + normal * (2.6f * d); val l2 = atras - normal * (2.6f * d)
                lineTo(l1.x, l1.y); lineTo(l2.x, l2.y); close()
            }, color)
            for (k in 0..1) {
                val base = desde + dir * (k * 2.2f * d + 2.4f * d)
                drawLine(color, base, base - dir * (2.4f * d) + normal * (2.2f * d), g * 0.8f)
                drawLine(color, base, base - dir * (2.4f * d) - normal * (2.2f * d), g * 0.8f)
            }
            ms.forEach { raya(it, 2.5f * d) }
        }
        Eje.COMPAS -> {
            val sobra = 4f * d
            drawLine(color, desde - dir * sobra, hasta + dir * sobra, g * 0.75f)
            extremos.forEach { drawCircle(color, 1.9f * d, it, style = Stroke(g * 0.75f)) }
            ms.forEach { cruzada(it, 2.2f * d, g * 0.75f) }
        }
        Eje.CABO -> {
            linea(g * 1.8f)
            val largo = (hasta - desde).getDistance(); val paso = 2.6f * d
            var t = 0f
            while (t < largo) { val p = desde + dir * t; drawLine(color.copy(alpha = color.alpha * 0.45f), p - normal * (g * 0.9f), p + dir * (g * 1.2f) + normal * (g * 0.9f), g * 0.5f); t += maxOf(paso, 1f) }
            ms.forEach { drawCircle(color, 1.8f * d, it) }
        }
        Eje.AGUZADA -> {
            val a0 = 1.7f * d; val a1 = 0.35f * d
            drawPath(Path().apply {
                val p1 = desde + normal * a0; val p2 = hasta + normal * a1; val p3 = hasta - normal * a1; val p4 = desde - normal * a0
                moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); lineTo(p4.x, p4.y); close()
            }, color)
            ms.forEach { raya(it, 4f * d, g * 1.2f) }
        }
        Eje.PICA -> {
            linea()
            val atras = hasta - dir * (6f * d); val medio = hasta - dir * (3.5f * d)
            drawPath(Path().apply {
                val l1 = medio + normal * (2f * d); val l2 = medio - normal * (2f * d)
                moveTo(hasta.x, hasta.y); lineTo(l1.x, l1.y); lineTo(atras.x, atras.y); lineTo(l2.x, l2.y); close()
            }, color)
            drawLine(color, atras + normal * (2.4f * d), atras - normal * (2.4f * d), g)
            ms.forEach { raya(it, 3f * d) }
        }
        Eje.RUNAS -> {
            linea()
            ms.forEachIndexed { i, m ->
                raya(m, 4.5f * d)
                val mitad = m + fuera * (2.2f * d)
                drawLine(color, mitad, mitad + fuera * (1.8f * d) + (if (i % 2 == 0) dir else -dir) * (2f * d), g)
            }
        }
    }
}

// =====================================================================
//  Lienzos de android (reportes)
// =====================================================================

/** Dibuja con las funciones de Compose sobre un lienzo de android; [escala] = píxeles por dp. */
fun android.graphics.Canvas.dibujarGrafica(escala: Float = ESCALA_REPORTE, dibujo: DrawScope.() -> Unit) {
    CanvasDrawScope().draw(Density(escala), LayoutDirection.Ltr, androidx.compose.ui.graphics.Canvas(this), Size(width.toFloat(), height.toFloat()), dibujo)
}

/** Punto de datos con el color de [paint]; hueco si [paint] es de trazo. */
fun android.graphics.Canvas.puntoGrafica(e: EstiloGrafica, x: Float, y: Float, r: Float, paint: Paint,
                                         hueco: Boolean = paint.style == Paint.Style.STROKE, escala: Float = ESCALA_REPORTE) {
    val col = Color(paint.color)
    dibujarGrafica(escala) { puntoGrafica(e, Offset(x, y), r, col, hueco) }
}

/** Serie con el color y el ancho de [paint] (si el ancho es 0, el del tema). [acento]: color del filete o las franjas (ARGB). */
fun android.graphics.Canvas.lineaGrafica(e: EstiloGrafica, ruta: android.graphics.Path, paint: Paint, escala: Float = ESCALA_REPORTE, acento: Int? = null) {
    val col = Color(paint.color); val w = paint.strokeWidth.takeIf { it > 0f }
    val ac = acento?.let { Color(it) } ?: Color.Unspecified
    val r = ruta.asComposePath()
    dibujarGrafica(escala) { lineaGrafica(e, r, col, w, ac) }
}

/** Línea de referencia con el color de [paint] (y su ancho, si tiene). */
fun android.graphics.Canvas.lineaMeta(e: EstiloGrafica, x0: Float, y0: Float, x1: Float, y1: Float, paint: Paint, escala: Float = ESCALA_REPORTE) {
    val col = Color(paint.color); val w = paint.strokeWidth.takeIf { it > 0f }
    dibujarGrafica(escala) { lineaMeta(e, Offset(x0, y0), Offset(x1, y1), col, w) }
}

/** Barra en [rect] con el color de [paint]. */
fun android.graphics.Canvas.barraGrafica(e: EstiloGrafica, rect: RectF, paint: Paint, horizontal: Boolean = false, pista: Boolean = false,
                                         escala: Float = ESCALA_REPORTE, acento: Int? = null) {
    val col = Color(paint.color); val ac = acento?.let { Color(it) } ?: Color.Unspecified
    dibujarGrafica(escala) { barraGrafica(e, Offset(rect.left, rect.top), Size(rect.width(), rect.height()), col, horizontal, pista, ac) }
}

/** Rejilla con el color de [paint] dentro de [area]. */
fun android.graphics.Canvas.rejillaGrafica(e: EstiloGrafica, ys: List<Float>, paint: Paint, area: RectF, xs: List<Float> = emptyList(), escala: Float = ESCALA_REPORTE) {
    val col = Color(paint.color)
    dibujarGrafica(escala) { rejillaGrafica(e, ys, col, xs, Rect(area.left, area.top, area.right, area.bottom)) }
}

/** Eje con el color de [paint]. */
fun android.graphics.Canvas.ejeGrafica(e: EstiloGrafica, x0: Float, y0: Float, x1: Float, y1: Float, paint: Paint, marcas: List<Float> = emptyList(),
                                       escala: Float = ESCALA_REPORTE) {
    val col = Color(paint.color)
    dibujarGrafica(escala) { ejeGrafica(e, Offset(x0, y0), Offset(x1, y1), col, marcas) }
}

/**
 * Pone a [paint] el trazo del tema (ancho, tapa, unión y guiones) para quien siga dibujando a mano. Sin adornos (halo,
 * filete, sombra…): para tenerlos, usar `lineaGrafica`. [ancho] en px: si viene, manda sobre el del tema.
 */
fun EstiloGrafica.pincelLinea(paint: Paint, escala: Float = ESCALA_REPORTE, ancho: Float? = null): Paint {
    val w = ancho ?: (this.ancho * escala)
    val cap = if (tapa == StrokeCap.Round) Paint.Cap.ROUND else if (tapa == StrokeCap.Square) Paint.Cap.SQUARE else Paint.Cap.BUTT
    val join = if (union == StrokeJoin.Round) Paint.Join.ROUND else if (union == StrokeJoin.Bevel) Paint.Join.BEVEL else Paint.Join.MITER
    val efecto = guiones?.let { g -> DashPathEffect(FloatArray(g.size) { g[it] * escala }, 0f) }
    return paint.apply { style = Paint.Style.STROKE; strokeWidth = w; strokeCap = cap; strokeJoin = join; pathEffect = efecto; isAntiAlias = true }
}

// =====================================================================
//  Ayudantes
// =====================================================================

/** Un tono para filetes y lazos: más oscuro sobre colores claros, más claro sobre oscuros. */
private fun egTinte(c: Color): Color = if (c.luminance() > 0.5f) lerp(c, Color.Black, 0.35f) else lerp(c, Color.White, 0.5f)

/** Un tono más oscuro para estrías y juntas. */
private fun egOscuro(c: Color): Color = lerp(c, Color.Black, 0.3f).copy(alpha = c.alpha * 0.8f)

private fun egPoligono(c: Offset, r: Float, lados: Int, giro: Float): Path {
    val p = Path()
    for (i in 0 until lados) {
        val ang = (giro + i * 360f / lados) * (PI.toFloat() / 180f)
        val x = c.x + r * cos(ang); val y = c.y + r * sin(ang)
        if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
    }
    p.close(); return p
}

private fun egEstrella(c: Offset, r: Float, puntas: Int, interior: Float, giro: Float = -90f): Path {
    val p = Path()
    for (i in 0 until puntas * 2) {
        val rr = if (i % 2 == 0) r else r * interior
        val ang = (giro + i * 180f / puntas) * (PI.toFloat() / 180f)
        val x = c.x + rr * cos(ang); val y = c.y + rr * sin(ang)
        if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
    }
    p.close(); return p
}

/** Polígono a partir de coordenadas en unidades del radio (x, y, x, y…). */
private fun egRuta(c: Offset, r: Float, vararg xy: Float): Path {
    val p = Path()
    var i = 0
    while (i + 1 < xy.size) {
        val x = c.x + xy[i] * r; val y = c.y + xy[i + 1] * r
        if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
        i += 2
    }
    p.close(); return p
}
