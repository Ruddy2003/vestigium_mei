package io.github.ruddy2003.vestigium.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Repo
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Movimiento: secciones que entran escalonadas, cifras que cuentan hasta su valor, botones que se hunden
 * al tocarlos y un fondo vivo para cada tema (chispas de lucerna, hojas de olivo, polvo de oro, ceniza, vapor…).
 * Cada tema tiene su matiz: cómo entran las cosas, cómo rebota un botón y con qué forma se abre al cambiar de tema.
 * Todo respeta el interruptor de animaciones: si está apagado, las cosas aparecen ya quietas en su lugar.
 */

// =====================================================================
//  El matiz de movimiento de cada tema
// =====================================================================

/** La forma con que el tema nuevo se abre sobre el anterior. */
private enum class BarridoTema {
    ARCO, CUPOLA, PIXELES, SOL, PIRAMIDE, ROMBO, OJO, ESCOTILLA, ENGRANE, MEMBRANA, COLUMNAS, CORTINA,
    /** Art déco: la corona escalonada de un rascacielos que crece desde el toque. */
    ESCALONES,
    HOJA,
    TINTA,
    GRIETA,
    COPO, ABANICO, SELLO, OJIVA, CIRCULO, MAREA, OCHO, HORIZONTE, VITRUVIO, ROSA, ESTRELLA, BANDAS, HEXAGONO,
    /** Prehistoria: la nube de ocre soplado, de borde moteado. */
    SOPLO,
    PERSIANA,
    /** Templario: la cruz patada que crece sobre su disco. */
    CRUZ,
    /** Mesoamérica: el rombo escalonado de los frisos, que crece grada por grada. */
    GRADAS,
}

/**
 * Cómo se mueve cada tema. Transición de tema: [barrido], [ms] y [curva]. Entrada de las secciones: desde [dx] y [dy]
 * (en dp) y desde la escala [escala], en [entradaMs], con [paso] ms entre una y otra. Presión de un botón: [hondo]
 * multiplica cuánto se hunde, y el resorte tiene [rebote] (amortiguación: 1 no rebota) y [rigidez].
 */
private class MatizMovimiento(
    val barrido: BarridoTema, val ms: Int, val curva: Easing,
    val dx: Float, val dy: Float, val escala: Float, val entradaMs: Int, val paso: Long, val curvaEntrada: Easing,
    val hondo: Float, val rebote: Float, val rigidez: Float,
)

/** La consola avanza a saltos, como un cursor que se redibuja. */
private val SALTOS_CONSOLA = Easing { f -> floor(f * 8f) / 8f }
private val SALTOS_RENGLON = Easing { f -> floor(f * 4f) / 4f }

private fun matizDe(t: TemaId): MatizMovimiento = when (t) {
    // Arco de medio punto; las piezas asientan firmes, como mármol.
    TemaId.ROMANO -> MatizMovimiento(BarridoTema.ARCO, 760, FastOutSlowInEasing, 0f, 18f, 1f, 420, 55, FastOutSlowInEasing, 1f, 0.6f, 700f)
    // Una cúpula que crece desde el toque, con su linterna; las piezas llegan de lado, como quien dobla la esquina en motoneta.
    TemaId.URBE -> MatizMovimiento(BarridoTema.CUPOLA, 700, LinearOutSlowInEasing, 22f, 6f, 0.99f, 340, 45, LinearOutSlowInEasing, 0.9f, 0.55f, 900f)
    // Rejilla de píxeles que se enciende a saltos; nada rebota.
    TemaId.CIBER -> MatizMovimiento(BarridoTema.PIXELES, 640, SALTOS_CONSOLA, 0f, 6f, 1f, 260, 40, SALTOS_RENGLON, 0.8f, 1f, 2500f)
    // La persiana del puesto que sube de golpe; las secciones entran de lado, como letreros que se deslizan, y rebotan apenas.
    TemaId.NEON -> MatizMovimiento(BarridoTema.PERSIANA, 620, CubicBezierEasing(0.2f, 0f, 0f, 1f), 14f, 0f, 1f, 320, 45, FastOutSlowInEasing, 1.05f, 0.55f, 1400f)
    // El sol de Vergina, de dieciséis rayos.
    TemaId.HELENO -> MatizMovimiento(BarridoTema.SOL, 820, FastOutSlowInEasing, 0f, 14f, 0.98f, 480, 60, FastOutSlowInEasing, 1f, 0.5f, 800f)
    // Una pirámide que crece desde el toque; los bloques suben desde abajo, pesados y sin rebote, como sillares.
    TemaId.EGIPTO -> MatizMovimiento(BarridoTema.PIRAMIDE, 860, CubicBezierEasing(0.6f, 0f, 0.2f, 1f), 0f, 16f, 1f, 520, 65, LinearOutSlowInEasing, 0.92f, 0.9f, 620f)
    // Rombo de miniatura; todo lento, a paso de pluma.
    TemaId.MEDIEVAL -> MatizMovimiento(BarridoTema.ROMBO, 900, LinearOutSlowInEasing, 0f, 10f, 1f, 560, 70, LinearOutSlowInEasing, 0.9f, 0.75f, 500f)
    // La cruz patada que se abre desde el toque; las piezas bajan un poco y asientan pesadas, como un yelmo que se calza, sin rebote.
    TemaId.TEMPLARIO -> MatizMovimiento(BarridoTema.CRUZ, 820, CubicBezierEasing(0.55f, 0f, 0.3f, 1f), 0f, -12f, 1.02f, 420, 55, FastOutSlowInEasing, 1.15f, 0.95f, 1600f)
    // El ojo que se abre; los expedientes entran de lado y el sello cae seco.
    TemaId.DISTOPIA -> MatizMovimiento(BarridoTema.OJO, 700, CubicBezierEasing(0.7f, 0f, 0.9f, 0.6f), -16f, 0f, 1f, 300, 45, LinearEasing, 1.1f, 1f, 1800f)
    // Escotilla octogonal que gira al abrirse; resortes de caricatura.
    TemaId.REFUGIO -> MatizMovimiento(BarridoTema.ESCOTILLA, 780, CubicBezierEasing(0.7f, 0f, 0.3f, 1f), 0f, 22f, 1f, 380, 50, FastOutSlowInEasing, 1f, 0.4f, 900f)
    // Engrane que gira; acero pesado.
    TemaId.ORDEN -> MatizMovimiento(BarridoTema.ENGRANE, 760, CubicBezierEasing(0.5f, 0f, 0.2f, 1f), 0f, 12f, 1f, 360, 50, FastOutSlowInEasing, 1.2f, 0.9f, 1400f)
    // Membrana de célula que ondula; preciso y contenido.
    TemaId.LABORATORIO -> MatizMovimiento(BarridoTema.MEMBRANA, 680, FastOutSlowInEasing, 0f, 8f, 0.96f, 400, 50, FastOutSlowInEasing, 0.85f, 0.8f, 1200f)
    // Columnas de periódico que se imprimen de arriba abajo.
    TemaId.COLONOS -> MatizMovimiento(BarridoTema.COLUMNAS, 820, LinearOutSlowInEasing, 0f, 20f, 1f, 450, 65, LinearOutSlowInEasing, 1f, 0.65f, 650f)
    // Cortina que se abre al centro; las secciones entran marchando desde la derecha.
    TemaId.NAPOLEON -> MatizMovimiento(BarridoTema.CORTINA, 740, FastOutSlowInEasing, 18f, 0f, 1f, 420, 55, FastOutSlowInEasing, 1.05f, 0.55f, 1000f)
    // La corona escalonada de un rascacielos que crece desde el toque; las secciones suben como el ascensor exprés y se
    // detienen en su piso, sin rebote.
    TemaId.DECO -> MatizMovimiento(BarridoTema.ESCALONES, 720, CubicBezierEasing(0.7f, 0f, 0.2f, 1f), 0f, 26f, 0.98f, 460, 70, FastOutSlowInEasing, 1.0f, 0.5f, 850f)
    // Una hoja que se despliega; resorte de tallo.
    TemaId.BOTANICO -> MatizMovimiento(BarridoTema.HOJA, 860, CubicBezierEasing(0.3f, 0f, 0.2f, 1f), 0f, 16f, 0.97f, 520, 65, FastOutSlowInEasing, 0.95f, 0.45f, 600f)
    // Un boquete de borde quebrado que se abre en el hormigón; las secciones se asientan como polvo y el botón pesa como una losa.
    TemaId.RUINAS -> MatizMovimiento(BarridoTema.GRIETA, 820, CubicBezierEasing(0.6f, 0f, 0.4f, 1f), 0f, 14f, 1.02f, 480, 60, LinearOutSlowInEasing, 1.15f, 0.7f, 800f)
    // Copo de seis puntas; las cosas caen desde arriba, como nieve, y rebotan como cascabeles.
    TemaId.NAVIDAD -> MatizMovimiento(BarridoTema.COPO, 880, FastOutSlowInEasing, 0f, -14f, 1f, 500, 60, FastOutSlowInEasing, 1f, 0.35f, 700f)
    // Abanico que se despliega; la caravana llega desde la izquierda.
    TemaId.SEDA -> MatizMovimiento(BarridoTema.ABANICO, 800, CubicBezierEasing(0.4f, 0f, 0.2f, 1f), -18f, 0f, 1f, 460, 60, FastOutSlowInEasing, 0.95f, 0.6f, 800f)
    // Un sello que se estampa; las secciones bajan como un rollo que se desenrolla, despacio y sin rebote.
    TemaId.EDO -> MatizMovimiento(BarridoTema.SELLO, 820, CubicBezierEasing(0.25f, 0f, 0.15f, 1f), 0f, -10f, 1f, 520, 80, LinearOutSlowInEasing, 0.85f, 0.9f, 650f)
    // Arco apuntado; solemne, sin prisa.
    TemaId.GOTICO -> MatizMovimiento(BarridoTema.OJIVA, 960, CubicBezierEasing(0.6f, 0f, 0.4f, 1f), 0f, 24f, 1f, 600, 75, LinearOutSlowInEasing, 0.9f, 0.85f, 450f)
    // Una mancha de tinta que se extiende sobre el papel; las secciones suben de la niebla, lentas y sin rebote.
    TemaId.ARCANO -> MatizMovimiento(BarridoTema.TINTA, 1100, CubicBezierEasing(0.2f, 0f, 0.6f, 1f), 0f, 10f, 0.985f, 700, 90, CubicBezierEasing(0.1f, 0.6f, 0.3f, 1f), 0.8f, 0.95f, 300f)
    // El círculo de un telescopio; las secciones se acercan como al enfocar.
    TemaId.ESPACIO -> MatizMovimiento(BarridoTema.CIRCULO, 720, FastOutSlowInEasing, 0f, 0f, 0.94f, 440, 55, FastOutSlowInEasing, 1f, 0.5f, 900f)
    // El mar que sube y cubre la pantalla; las secciones bajan despacio, como lastradas, y los botones se hunden blandos.
    TemaId.ABISMO -> MatizMovimiento(BarridoTema.MAREA, 980, CubicBezierEasing(0.3f, 0f, 0.2f, 1f), 0f, -14f, 1f, 620, 75, LinearOutSlowInEasing, 1.15f, 0.45f, 380f)
    // Estrella de ocho puntas de lazo.
    TemaId.ANDALUS -> MatizMovimiento(BarridoTema.OCHO, 840, CubicBezierEasing(0.45f, 0f, 0.25f, 1f), 0f, 12f, 0.99f, 500, 60, FastOutSlowInEasing, 0.95f, 0.7f, 750f)
    // El rombo escalonado que crece desde el toque; las secciones suben como gradas, una tras otra, y los botones se
    // asientan firmes, como un sillar sobre otro.
    TemaId.MESO -> MatizMovimiento(BarridoTema.GRADAS, 900, CubicBezierEasing(0.5f, 0f, 0.3f, 1f), 0f, 20f, 1f, 560, 85, LinearOutSlowInEasing, 0.9f, 0.95f, 700f)
    // El horizonte de la estepa que se ensancha; las secciones llegan al galope.
    TemaId.MONGOL -> MatizMovimiento(BarridoTema.HORIZONTE, 760, FastOutSlowInEasing, 22f, 0f, 1f, 340, 45, FastOutSlowInEasing, 1.05f, 0.5f, 1100f)
    // Círculo y cuadrado juntos, como el hombre de Vitruvio.
    TemaId.RENACIMIENTO -> MatizMovimiento(BarridoTema.VITRUVIO, 880, CubicBezierEasing(0.35f, 0f, 0.25f, 1f), 0f, 10f, 0.985f, 540, 70, LinearOutSlowInEasing, 0.9f, 0.7f, 600f)
    // Rosa de los vientos que gira un poco al abrirse; las secciones llegan de costado, como empujadas por la marea, y los botones cabecean.
    TemaId.PIRATA -> MatizMovimiento(BarridoTema.ROSA, 820, CubicBezierEasing(0.45f, 0f, 0.35f, 1f), -14f, 8f, 0.99f, 480, 60, FastOutSlowInEasing, 1.05f, 0.45f, 520f)
    // Estrella de cinco puntas; las secciones entran en diagonal, como un cartel constructivista.
    TemaId.URSS -> MatizMovimiento(BarridoTema.ESTRELLA, 680, CubicBezierEasing(0.8f, 0f, 0.2f, 1f), 14f, 14f, 1f, 320, 40, FastOutSlowInEasing, 1.15f, 0.9f, 1600f)
    // Tres bandas, azul, blanca y roja, que se abren una tras otra.
    TemaId.REVOLUCION -> MatizMovimiento(BarridoTema.BANDAS, 780, LinearOutSlowInEasing, 0f, 18f, 1f, 400, 55, FastOutSlowInEasing, 1.05f, 0.55f, 850f)
    // Puerta hexagonal de piedra; pesada.
    TemaId.ANTIGUAS -> MatizMovimiento(BarridoTema.HEXAGONO, 900, CubicBezierEasing(0.5f, 0f, 0.3f, 1f), 0f, 20f, 1f, 560, 70, LinearOutSlowInEasing, 1f, 0.8f, 500f)
    // La nube de ocre que se sopla desde el toque; las secciones se asientan despacio, como polvo de pigmento, y el botón
    // pesa como una piedra que se apoya.
    TemaId.CUEVA -> MatizMovimiento(BarridoTema.SOPLO, 960, CubicBezierEasing(0.3f, 0f, 0.5f, 1f), 0f, 10f, 0.98f, 540, 70, LinearOutSlowInEasing, 1.08f, 0.6f, 560f)
}

/** Aparece desde un lado y aclarándose, con el matiz del tema. [orden] retrasa unos milisegundos, para que las secciones entren una tras otra. */
fun Modifier.entrada(orden: Int = 0): Modifier = composed {
    val animar = Repo.config.animaciones
    val mz = matizDe(Estilo.tema)
    val p = remember { Animatable(if (animar) 0f else 1f) }
    LaunchedEffect(Unit) {
        if (p.value < 1f) { delay(orden.coerceIn(0, 8) * mz.paso); p.animateTo(1f, tween(mz.entradaMs, easing = mz.curvaEntrada)) }
    }
    graphicsLayer {
        val q = p.value
        alpha = q
        translationX = (1f - q) * mz.dx.dp.toPx(); translationY = (1f - q) * mz.dy.dp.toPx()
        val s = mz.escala + (1f - mz.escala) * q
        scaleX = s; scaleY = s
    }
}

/** Se hunde un poco mientras el dedo lo aprieta; cuánto y con qué rebote depende del tema. */
fun Modifier.presion(interaccion: MutableInteractionSource, cuanto: Float = 0.95f): Modifier = composed {
    val apretado by interaccion.collectIsPressedAsState()
    val animar = Repo.config.animaciones
    val mz = matizDe(Estilo.tema)
    val hundido = 1f - (1f - cuanto) * mz.hondo
    val s by animateFloatAsState(if (apretado && animar) hundido else 1f, spring(dampingRatio = mz.rebote, stiffness = mz.rigidez), label = "presion")
    graphicsLayer { scaleX = s; scaleY = s }
}

private val NUMERO = Regex("^(\\D*?)(\\d[\\d,]*(?:\\.\\d+)?)(.*)$", RegexOption.DOT_MATCHES_ALL)

/** El texto de una cifra contando desde cero hasta su valor («$1,250» pasa por «$312», «$874»…). Sin animaciones, el valor tal cual. */
@Composable
fun contando(valor: String): String {
    val animar = Repo.config.animaciones
    val m = NUMERO.find(valor)
    val objetivo = m?.groupValues?.get(2)?.replace(",", "")?.toDoubleOrNull()
    if (!animar || m == null || objetivo == null || objetivo == 0.0) return valor
    val p = remember(valor) { Animatable(0f) }
    LaunchedEffect(valor) { p.animateTo(1f, tween(750, easing = FastOutSlowInEasing)) }
    if (p.value >= 1f) return valor
    val (antes, num, despues) = Triple(m.groupValues[1], m.groupValues[2], m.groupValues[3])
    val decimales = num.substringAfter('.', "").length
    val miles = num.contains(',')
    val v = objetivo * p.value
    val texto = if (miles) String.format(java.util.Locale.US, "%,.${decimales}f", v) else String.format(java.util.Locale.US, "%.${decimales}f", v)
    return antes + texto + despues
}

// =====================================================================
//  Fondo vivo: una capa muy tenue que se mueve detrás de todo
// =====================================================================

/**
 * Lo que se mueve detrás de cada tema. Un solo despacho ([vivoTema]) para el fondo vivo de la app y para el fondo
 * vivo del teléfono, para que los dos digan lo mismo. [periodo]: cuánto dura una vuelta completa, en ms.
 */
internal enum class VivoTema(val periodo: Int) {
    LUCERNA(16000), STORNI(30000), VOLCADO(6000), OLIVO(30000), RAYO_ORO(26000), CENIZA(28000), VAPOR(18000), FRAGUA(7000),
    BURBUJAS(16000), GACETA(40000), CHARRETERAS(9000),
    /** Art déco: los reflectores de una noche de estreno sobre los rascacielos. */
    REFLECTORES(20000),
    HORMIGAS(70000), LUCIERNAGAS(9000), NIEVE(24000),
    NIEBLA(60000),
    VILANOS(30000), GOTERAS(12000),
    ARENA(14000), PETALOS(20000), VITRAL(80000), ESTRELLAS(9000), MAR(15000), AGUA(14000), NUBES(180000), SAMARAS(24000), OLEAJE(30000), ESTRELLAS_ROJAS(36000),
    CONFETI(22000), RUNAS(14000), LLOVIZNA(6000),
    /** Prehistoria: de día, manos en negativo que aparecen sopladas en la pared; de noche, la hoguera en lo hondo de la cueva. */
    MANOS(26000), HOGUERA(9000),
    /** Templario: antorchas en la muralla. */
    ANTORCHAS(8000),
    JEROGLIFOS(20000),
    /** Mesoamérica: las dos ruedas del tzolk'in (13 números y 20 signos) que avanzan un día por segundo; 260 días = 260 s. */
    TZOLKIN(260000),
}

/** El tipo de fondo vivo de cada tema (solo el Botánico cambia entre el día y la noche; el Refugio suma el barrido de noche). */
internal fun vivoTema(t: TemaId, oscuro: Boolean): VivoTema = when (t) {
    TemaId.ROMANO -> VivoTema.LUCERNA            // chispas que suben de una lucerna de aceite
    TemaId.URBE -> VivoTema.STORNI             // la bandada de estorninos que gira sobre los tejados al atardecer
    TemaId.CIBER -> VivoTema.VOLCADO             // un volcado de memoria: columnas de hexadecimal que caen
    TemaId.NEON -> VivoTema.LLOVIZNA             // la llovizna que cae entre letreros y salpica en los charcos
    TemaId.HELENO -> VivoTema.OLIVO              // hojas de olivo que bajan girando
    TemaId.EGIPTO -> VivoTema.JEROGLIFOS         // signos de un muro que la lámpara ilumina y deja en sombra
    TemaId.MEDIEVAL -> VivoTema.RAYO_ORO         // un rayo de luz con polvo de oro suspendido
    TemaId.TEMPLARIO -> VivoTema.ANTORCHAS        // la muralla almenada con antorchas que tiemblan y sueltan pavesas
    TemaId.DISTOPIA -> VivoTema.CENIZA           // ceniza de archivo que cae dando tumbos
    TemaId.REFUGIO -> VivoTema.VAPOR             // vapor que sube, de día y de noche
    TemaId.ORDEN -> VivoTema.FRAGUA              // chispas de fragua en parábola
    TemaId.LABORATORIO -> VivoTema.BURBUJAS      // burbujas que suben en el matraz
    TemaId.COLONOS -> VivoTema.GACETA            // hojas de periódico que arrastra el viento
    TemaId.NAPOLEON -> VivoTema.CHARRETERAS      // destellos de oro de charreteras
    TemaId.DECO -> VivoTema.REFLECTORES          // reflectores de estreno que barren el cielo sobre las torres escalonadas
    TemaId.BOTANICO -> if (oscuro) VivoTema.LUCIERNAGAS else VivoTema.HORMIGAS
    TemaId.RUINAS -> if (oscuro) VivoTema.GOTERAS else VivoTema.VILANOS   // de día, vilanos y hiedra colgante; de noche, goteras del techo roto
    TemaId.NAVIDAD -> VivoTema.NIEVE             // nieve con algún copo
    TemaId.SEDA -> VivoTema.ARENA                // arena que el viento arrastra de lado
    TemaId.EDO -> VivoTema.PETALOS              // pétalos de cerezo que caen meciéndose
    TemaId.GOTICO -> VivoTema.VITRAL             // manchas de luz de vitral
    TemaId.ARCANO -> VivoTema.NIEBLA             // bancos de niebla y el haz del faro
    TemaId.ESPACIO -> VivoTema.ESTRELLAS         // estrellas que titilan
    TemaId.ABISMO -> VivoTema.MAR                // nieve marina; de día, haces de sol y un cardumen; de noche, medusas que laten
    TemaId.ANDALUS -> VivoTema.AGUA              // reflejos de alberca
    TemaId.MESO -> VivoTema.TZOLKIN           // las ruedas de la cuenta de los días, y el humo del copal
    TemaId.MONGOL -> VivoTema.NUBES              // nubes del Cielo Azul
    TemaId.RENACIMIENTO -> VivoTema.SAMARAS      // semillas aladas que caen girando, como el tornillo aéreo
    TemaId.PIRATA -> VivoTema.OLEAJE             // gaviotas que planean sobre la mar de fondo
    TemaId.URSS -> VivoTema.ESTRELLAS_ROJAS      // estrellas rojas que suben
    TemaId.REVOLUCION -> VivoTema.CONFETI        // confeti azul, blanco y rojo, tenue
    TemaId.ANTIGUAS -> VivoTema.RUNAS            // runas que brillan y se apagan
    TemaId.CUEVA -> if (oscuro) VivoTema.HOGUERA else VivoTema.MANOS   // de día, manos que se soplan en la pared; de noche, la hoguera
}

internal class MotaVivo(val x: Float, val y: Float, val v: Float, val fase: Float, val tam: Float, val tono: Int)

/** Las mismas motas en la app y en el teléfono (la semilla es el tema). */
internal fun motasVivo(t: TemaId): List<MotaVivo> {
    val r = java.util.Random(t.ordinal.toLong() + 5)
    return List(34) { MotaVivo(r.nextFloat(), r.nextFloat(), 0.4f + r.nextFloat() * 0.6f, r.nextFloat(), 0.5f + r.nextFloat(), r.nextInt(3)) }
}

/** Runas inventadas: trazos (x1, y1, x2, y2) en una caja de 0.6 de ancho por 1 de alto. */
internal val RUNAS_VIVO: List<FloatArray> = listOf(
    floatArrayOf(0.3f, 0f, 0.3f, 1f, 0.3f, 0.2f, 0.6f, 0.45f),
    floatArrayOf(0.1f, 0f, 0.1f, 1f, 0.1f, 0.1f, 0.5f, 0.35f, 0.5f, 0.35f, 0.1f, 0.6f),
    floatArrayOf(0.3f, 0f, 0.3f, 1f, 0f, 0.3f, 0.6f, 0.7f),
    floatArrayOf(0.1f, 0f, 0.1f, 1f, 0.5f, 0f, 0.5f, 1f, 0.1f, 0.5f, 0.5f, 0.25f),
    floatArrayOf(0.3f, 0f, 0.6f, 0.4f, 0.6f, 0.4f, 0.3f, 0.8f, 0.3f, 0.8f, 0f, 0.4f, 0f, 0.4f, 0.3f, 0f, 0.3f, 0.8f, 0.3f, 1f),
    floatArrayOf(0.3f, 0f, 0.3f, 1f, 0.3f, 0.5f, 0f, 0.2f, 0.3f, 0.5f, 0.6f, 0.2f),
    floatArrayOf(0f, 0f, 0f, 1f, 0f, 0f, 0.6f, 0.3f, 0f, 0.5f, 0.6f, 0.8f),
    floatArrayOf(0.6f, 0f, 0f, 0.5f, 0f, 0.5f, 0.6f, 1f, 0.3f, 0.25f, 0.3f, 0.75f),
)

/** Dibuja una runa de [RUNAS_VIVO] centrada en [c], de [alto] de alto. */
internal fun DrawScope.runaVivo(trazos: FloatArray, c: Offset, alto: Float, color: Color, grosor: Float) {
    var i = 0
    while (i + 3 < trazos.size) {
        drawLine(color, Offset(c.x + (trazos[i] - 0.3f) * alto, c.y + (trazos[i + 1] - 0.5f) * alto),
            Offset(c.x + (trazos[i + 2] - 0.3f) * alto, c.y + (trazos[i + 3] - 0.5f) * alto), grosor, cap = StrokeCap.Round)
        i += 4
    }
}

/**
 * Cuatro signos egipcios dibujados con trazos, centrados en [c] y de [alto] de alto: 0 el ankh (la vida), 1 el ojo udyat
 * (el ojo protector), 2 la pluma de la Maat (lo justo) y 3 el escarabajo (Jepri, el sol que renace).
 */
internal fun DrawScope.signoEgipcio(tipo: Int, c: Offset, alto: Float, color: Color, grosor: Float) {
    val a = alto; val tr = Stroke(grosor, cap = StrokeCap.Round)
    when (Math.floorMod(tipo, 4)) {
        0 -> {
            drawOval(color, Offset(c.x - 0.2f * a, c.y - 0.5f * a), Size(0.4f * a, 0.42f * a), style = tr)
            drawLine(color, Offset(c.x - 0.32f * a, c.y - 0.04f * a), Offset(c.x + 0.32f * a, c.y - 0.04f * a), grosor, StrokeCap.Round)
            drawLine(color, Offset(c.x, c.y - 0.08f * a), Offset(c.x, c.y + 0.5f * a), grosor, StrokeCap.Round)
        }
        1 -> {
            drawPath(Path().apply {
                moveTo(c.x - 0.45f * a, c.y - 0.05f * a); quadraticTo(c.x, c.y - 0.36f * a, c.x + 0.45f * a, c.y - 0.05f * a)
                quadraticTo(c.x, c.y + 0.2f * a, c.x - 0.45f * a, c.y - 0.05f * a); close()
            }, color, style = tr)
            drawCircle(color, 0.1f * a, Offset(c.x, c.y - 0.06f * a))
            drawPath(Path().apply { moveTo(c.x - 0.48f * a, c.y - 0.3f * a); quadraticTo(c.x, c.y - 0.52f * a, c.x + 0.5f * a, c.y - 0.32f * a) }, color, style = tr)
            drawLine(color, Offset(c.x - 0.04f * a, c.y + 0.1f * a), Offset(c.x - 0.12f * a, c.y + 0.46f * a), grosor, StrokeCap.Round)
            drawPath(Path().apply { moveTo(c.x + 0.06f * a, c.y + 0.1f * a); quadraticTo(c.x + 0.3f * a, c.y + 0.5f * a, c.x + 0.42f * a, c.y + 0.28f * a) }, color, style = tr)
        }
        2 -> {
            drawPath(Path().apply {
                moveTo(c.x - 0.06f * a, c.y + 0.5f * a); lineTo(c.x - 0.12f * a, c.y - 0.18f * a)
                quadraticTo(c.x - 0.06f * a, c.y - 0.5f * a, c.x + 0.24f * a, c.y - 0.46f * a)
                quadraticTo(c.x + 0.12f * a, c.y - 0.2f * a, c.x + 0.1f * a, c.y + 0.5f * a)
            }, color, style = tr)
            drawLine(color, Offset(c.x - 0.02f * a, c.y + 0.5f * a), Offset(c.x + 0.02f * a, c.y - 0.3f * a), grosor * 0.7f, StrokeCap.Round)
        }
        else -> {
            drawOval(color, Offset(c.x - 0.22f * a, c.y - 0.2f * a), Size(0.44f * a, 0.62f * a), style = tr)
            drawOval(color, Offset(c.x - 0.13f * a, c.y - 0.42f * a), Size(0.26f * a, 0.2f * a), style = tr)
            drawLine(color, Offset(c.x, c.y - 0.2f * a), Offset(c.x, c.y + 0.42f * a), grosor * 0.7f)
            for (s in listOf(-1f, 1f)) for (k in 0 until 3) {
                val y = c.y - 0.08f * a + k * 0.2f * a
                drawLine(color, Offset(c.x + s * 0.22f * a, y), Offset(c.x + s * 0.4f * a, y + (k - 1) * 0.12f * a), grosor * 0.8f, StrokeCap.Round)
            }
        }
    }
}

private fun fraccionVivo(x: Float): Float = x - floor(x)
private fun senoVuelta(x: Float): Float = sin(2 * PI * x).toFloat()
private fun cosenoVuelta(x: Float): Float = cos(2 * PI * x).toFloat()

/**
 * Dibuja un instante [t] (0 a 1, una vuelta del [VivoTema.periodo]) del fondo vivo. Los movimientos dan vueltas enteras
 * por periodo, así el ciclo vuelve a empezar sin salto. [fuerza] multiplica la opacidad (el teléfono, sin nada encima, va más visible).
 */
internal fun DrawScope.pintarVivoTema(v: VivoTema, t: Float, oscuro: Boolean, e: ColorScheme, motas: List<MotaVivo>, fuerza: Float = 1f) {
    val w = size.width; val h = size.height; val u = 1.dp.toPx()
    fun al(c: Color, a: Float) = c.copy(alpha = (a * fuerza).coerceIn(0f, 1f))
    // 1, 2 o 3 vueltas por periodo: unas motas van más rápido que otras y el ciclo cierra justo.
    fun vueltas(m: MotaVivo) = 1 + (m.v * 2.99f).toInt()
    when (v) {
        VivoTema.LUCERNA -> motas.take(22).forEach { m ->
            // Chispas que suben de la llama y se apagan al subir, parpadeando.
            val k = vueltas(m)
            val vida = fraccionVivo(m.y + t * k)
            val x = (m.x + 0.025f * senoVuelta(t * 3 * k + m.fase)) * w
            val y = h * (1.04f - vida * 1.1f)
            val a = (1f - vida) * (0.65f + 0.35f * senoVuelta(t * 23 + m.fase * 7))
            val brasa = if (oscuro) Color(0xFFF2A541) else e.tertiary
            val r = 7f * u * m.tam
            drawCircle(Brush.radialGradient(listOf(al(brasa, 0.30f * a), Color.Transparent), center = Offset(x, y), radius = r), r, Offset(x, y))
            drawCircle(al(if (oscuro) Color(0xFFFFE2A8) else brasa, (if (oscuro) 0.6f else 0.35f) * a), 1.1f * u * m.tam, Offset(x, y))
        }
        VivoTema.STORNI -> {
            // Los estorninos de las tardes de otoño: una bandada que da vueltas sobre los tejados, se estira, se encoge y
            // se dobla como una sábana al viento. Cada pájaro es una «v» pequeña que aletea; abajo, el perfil de las cúpulas.
            val cx = w * (0.5f + 0.26f * senoVuelta(t)); val cy = h * (0.3f + 0.08f * senoVuelta(t * 2 + 0.25f))
            val ancho = w * (0.2f + 0.07f * senoVuelta(t * 3)); val alto = h * (0.06f + 0.035f * cosenoVuelta(t * 2))
            val giro = 0.5f * senoVuelta(t * 2)
            val ave = if (oscuro) Color(0xFFF1E6D2) else Color(0xFF2B2420)
            motas.forEach { m ->
                val k = vueltas(m)
                val a = m.fase + t * k
                val rr = 0.3f + 0.7f * m.tam
                val px = ancho * rr * cosenoVuelta(a); val py = alto * rr * senoVuelta(2 * a) + alto * 0.6f * (m.y - 0.5f)
                val x = cx + px * cos(giro) - py * sin(giro); val y = cy + px * sin(giro) + py * cos(giro)
                val ala = 2.6f * u * (0.7f + 0.3f * m.tam); val bate = ala * (0.35f + 0.5f * abs(senoVuelta(t * 40 * k + m.fase)))
                val col = al(ave, if (oscuro) 0.3f else 0.22f)
                drawLine(col, Offset(x - ala, y - bate), Offset(x, y), 0.9f * u, cap = StrokeCap.Round)
                drawLine(col, Offset(x + ala, y - bate), Offset(x, y), 0.9f * u, cap = StrokeCap.Round)
            }
            // El horizonte de cúpulas y campanarios, muy tenue, como recortado contra el cielo.
            val linea = h * 0.97f; val sil = al(ave, if (oscuro) 0.05f else 0.04f)
            drawRect(sil, Offset(0f, linea), Size(w, h - linea))
            listOf(0.18f to 0.05f, 0.46f to 0.09f, 0.78f to 0.06f).forEach { (fx, fr) ->
                val r = w * fr
                drawArc(sil, 180f, 180f, true, Offset(w * fx - r, linea - r), Size(2 * r, 2 * r))
                drawRect(sil, Offset(w * fx - r * 0.12f, linea - r * 1.35f), Size(r * 0.24f, r * 0.4f))
            }
            listOf(0.32f, 0.63f).forEach { fx -> drawRect(sil, Offset(w * fx, linea - w * 0.13f), Size(w * 0.025f, w * 0.13f)) }
        }
        VivoTema.VOLCADO -> {
            // Un volcado de memoria: columnas de pares hexadecimales alineadas a la rejilla de la consola, que bajan; la
            // cabeza va más brillante y la cola se apaga. Los dígitos cambian con la memoria que pasa.
            val paso = 22f * u; val alto = 11f * u
            val hex = "0123456789ABCDEF"
            val pincel = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { typeface = android.graphics.Typeface.MONOSPACE; textSize = 9f * u }
            val lienzo = drawContext.canvas.nativeCanvas
            motas.take(24).forEach { m ->
                val k = vueltas(m)
                val cabeza = fraccionVivo(m.y + t * k) * (h + 120f * u) - 60f * u
                val x = kotlin.math.round(m.x * w / paso) * paso
                val largo = 4 + (m.tam * 6).toInt()
                for (j in 0 until largo) {
                    val y = cabeza - j * alto
                    if (y < -alto || y > h + alto) continue
                    val semilla = (m.fase * 997).toInt() + j * 7 + (t * k * 12).toInt()
                    val texto = "" + hex[semilla and 15] + hex[(semilla * 5 + 3) and 15]
                    pincel.color = al(e.primary, (if (j == 0) 0.42f else 0.2f * (1f - j.toFloat() / largo))).toArgb()
                    lienzo.drawText(texto, x, y, pincel)
                }
            }
        }
        VivoTema.LLOVIZNA -> {
            // La llovizna que cae en diagonal, teñida por los letreros (magenta, ámbar y verde), y las salpicaduras que
            // abren anillos en los charcos de abajo. A la derecha, el reflejo de un letrero que parpadea.
            val tonos = listOf(e.primary, e.secondary, e.tertiary)
            motas.forEach { m ->
                val k = vueltas(m) + 1
                val caida = fraccionVivo(m.y + t * k)
                val y = caida * (h + 40f * u) - 20f * u
                val x = (m.x - 0.08f * caida) * w
                val l = 16f * u * (0.6f + m.tam * 0.6f)
                drawLine(al(tonos[m.tono], 0.14f), Offset(x, y), Offset(x - l * 0.22f, y + l), 1f * u, StrokeCap.Round)
            }
            motas.take(10).forEach { m ->
                val f = fraccionVivo(m.fase + t * 2)
                val c = Offset(m.x * w, h * (0.86f + 0.12f * m.y))
                drawOval(al(tonos[m.tono], 0.18f * (1f - f)), Offset(c.x - 14f * u * f, c.y - 4f * u * f), Size(28f * u * f, 8f * u * f), style = Stroke(0.8f * u))
            }
            val parpadeo = if (fraccionVivo(t * 3) in 0.42f..0.47f) 0.02f else 0.07f
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, al(e.primary, parpadeo)), startX = w * 0.7f, endX = w), Offset(w * 0.7f, 0f), Size(w * 0.3f, h))
        }
        VivoTema.OLIVO -> motas.take(16).forEach { m ->
            // Hojas de olivo, largas y estrechas, que bajan en diagonal girando.
            val k = vueltas(m)
            val caida = fraccionVivo(m.y + t * k)
            val y = caida * (h + 40f * u) - 20f * u
            val x = (m.x + 0.10f * caida + 0.03f * senoVuelta(t * 2 * k + m.fase)) * w
            val l = 8f * u * (0.8f + m.tam * 0.4f)
            val hoja = if (oscuro) Color(0xFFB7C9A8) else Color(0xFF5E7A4C)
            rotate(35f + 40f * senoVuelta(t * k + m.fase), Offset(x, y)) {
                drawPath(Path().apply { moveTo(x - l, y); quadraticTo(x, y - l * 0.32f, x + l, y); quadraticTo(x, y + l * 0.32f, x - l, y); close() }, al(hoja, 0.18f))
                drawLine(al(hoja, 0.22f), Offset(x - l * 1.25f, y), Offset(x + l * 0.9f, y), 0.5f * u)
            }
        }
        VivoTema.RAYO_ORO -> {
            // Un rayo de luz que entra en diagonal por la ventana del scriptorium; dentro, motas de oro suspendidas.
            val oro = Color(0xFFD9AE4E)
            val respira = 0.8f + 0.2f * senoVuelta(t * 2)
            val haz = Path().apply { moveTo(0.16f * w, 0f); lineTo(0.42f * w, 0f); lineTo(0.98f * w, h); lineTo(0.52f * w, h); close() }
            drawPath(haz, Brush.verticalGradient(listOf(al(oro, (if (oscuro) 0.09f else 0.07f) * respira), al(oro, 0.01f)), startY = 0f, endY = h))
            motas.forEach { m ->
                val k = vueltas(m)
                val s = (m.y + 0.04f * senoVuelta(t * k + m.fase)).coerceIn(0f, 1f)
                val l = (m.x + 0.10f * senoVuelta(t * (k + 1) + m.fase * 3)).coerceIn(0f, 1f)
                val xi = (0.16f + 0.36f * s) * w; val xd = (0.42f + 0.56f * s) * w
                val brillo = 0.45f + 0.55f * senoVuelta(t * 9 + m.fase * 5).coerceAtLeast(0f)
                drawCircle(al(oro, (if (oscuro) 0.55f else 0.4f) * brillo), 1.1f * u * m.tam, Offset(xi + (xd - xi) * l, s * h))
            }
        }
        VivoTema.CENIZA -> motas.take(24).forEach { m ->
            // Pavesas irregulares de papel quemado que caen dando tumbos; de noche, alguna conserva una brasa.
            val k = vueltas(m)
            val y = fraccionVivo(m.y + t * k) * (h + 30f * u) - 15f * u
            val x = (m.x + 0.03f * senoVuelta(t * 2 * k + m.fase)) * w
            val r = 3.2f * u * m.tam
            val gris = if (oscuro) Color(0xFFA19E98) else Color(0xFF3F3E3B)
            val pavesa = Path().apply {
                for (i in 0 until 5) {
                    val ang = 2 * PI * i / 5 + m.fase
                    val rr = r * (0.6f + 0.4f * fraccionVivo(m.fase * (i + 3) * 7.3f))
                    val px = x + (rr * cos(ang)).toFloat(); val py = y + (rr * sin(ang)).toFloat()
                    if (i == 0) moveTo(px, py) else lineTo(px, py)
                }
                close()
            }
            rotate(360f * (t * k + m.fase), Offset(x, y)) {
                scale(0.25f + 0.75f * abs(cosenoVuelta(t * 2 * k + m.fase)), 1f, Offset(x, y)) {
                    drawPath(pavesa, al(gris, if (oscuro) 0.22f else 0.14f))
                }
            }
            if (m.tono == 0 && oscuro) drawCircle(al(Color(0xFFE0703A), 0.25f * (0.5f + 0.5f * senoVuelta(t * 11 + m.fase))), 0.8f * u, Offset(x, y))
        }
        VivoTema.VAPOR -> {
            // Volutas de vapor de las tuberías que suben, se ensanchan y se deshacen.
            val vaho = if (oscuro) e.primary else e.onBackground
            motas.take(10).forEach { m ->
                val k = vueltas(m)
                val vida = fraccionVivo(m.y + t * k)
                val y = h * (1.08f - vida * 0.95f)
                val x = (m.x + 0.05f * senoVuelta(vida * 1.5f + m.fase)) * w
                val r = (16f + 46f * vida) * u * m.tam
                val a = senoVuelta(vida * 0.5f) * (if (oscuro) 0.07f else 0.05f)
                drawCircle(Brush.radialGradient(listOf(al(vaho, a), Color.Transparent), center = Offset(x, y), radius = r), r, Offset(x, y))
            }
            // De noche, además, el barrido de la pantalla de fósforo (solo en el Refugio).
            if (oscuro) drawRect(al(e.primary, 0.035f), Offset(0f, fraccionVivo(t * 3) * h), Size(w, 26f * u))
        }
        VivoTema.FRAGUA -> motas.forEach { m ->
            // Chispas que saltan del yunque en parábola: trazos cortos que siguen su propia velocidad.
            val k = vueltas(m) + 1
            val tau = fraccionVivo(m.y + t * k)
            val sube = 0.9f + 0.6f * m.v
            val vx = (m.fase - 0.5f) * 0.5f * w
            val x = (0.2f + 0.6f * m.x) * w + vx * tau
            val y = h * 1.02f - h * sube * (tau - 0.7f * tau * tau)
            val dy = -h * sube * (1f - 1.4f * tau)
            val n = sqrt(vx * vx + dy * dy).coerceAtLeast(1f)
            val l = 8f * u * m.tam
            val chispa = if (oscuro) Color(0xFFFFB04A) else e.primary
            drawLine(al(chispa, (1f - tau) * (if (oscuro) 0.55f else 0.28f)), Offset(x, y), Offset(x - vx / n * l, y - dy / n * l), 1.2f * u, cap = StrokeCap.Round)
        }
        VivoTema.BURBUJAS -> motas.take(22).forEach { m ->
            // Burbujas de contorno fino, con su brillo, que suben temblando y crecen un poco.
            val k = vueltas(m)
            val sube = fraccionVivo(m.y + t * k)
            val y = h * (1.04f - sube * 1.1f)
            val x = (m.x + 0.012f * senoVuelta(t * 4 * k + m.fase)) * w
            val r = (2.2f + 3.6f * m.tam) * u * (0.8f + 0.4f * sube)
            drawCircle(al(e.primary, if (oscuro) 0.20f else 0.16f), r, Offset(x, y), style = Stroke(0.9f * u))
            drawCircle(al(if (oscuro) Color.White else e.primary, 0.18f), r * 0.28f, Offset(x - r * 0.38f, y - r * 0.38f))
        }
        VivoTema.GACETA -> motas.take(6).forEachIndexed { i, m ->
            // Hojas de gaceta con su cabecera y sus columnas, que el viento arrastra de lado y voltea.
            val k = vueltas(m)
            val x = (fraccionVivo(m.x + t * k) * 1.3f - 0.15f) * w
            val y = (0.08f + 0.84f * m.y) * h + 18f * u * senoVuelta(t * 2 * k + m.fase)
            val ancho = 20f * u * m.tam; val alto = 26f * u * m.tam
            val tinta = e.onBackground
            rotate(25f * senoVuelta(t * k + m.fase) + (i % 2) * 12f, Offset(x, y)) {
                scale(0.35f + 0.65f * abs(cosenoVuelta(t * k + m.fase)), 1f, Offset(x, y)) {
                    val o = Offset(x - ancho / 2, y - alto / 2)
                    drawRect(al(if (oscuro) tinta else e.surface, if (oscuro) 0.04f else 0.5f), o, Size(ancho, alto))
                    drawRect(al(tinta, 0.10f), o, Size(ancho, alto), style = Stroke(0.6f * u))
                    drawRect(al(tinta, 0.14f), Offset(o.x + ancho * 0.1f, o.y + alto * 0.08f), Size(ancho * 0.8f, alto * 0.09f))
                    for (col in 0 until 3) for (fila in 0 until 6) {
                        val cx = o.x + ancho * (0.1f + col * 0.28f); val fy = o.y + alto * (0.28f + fila * 0.11f)
                        drawLine(al(tinta, 0.09f), Offset(cx, fy), Offset(cx + ancho * 0.22f, fy), 0.6f * u)
                    }
                }
            }
        }
        VivoTema.CHARRETERAS -> motas.take(20).forEach { m ->
            // Destellos de cuatro puntas que se encienden y se apagan de golpe, como el oro de una charretera al moverse.
            val k = vueltas(m)
            val s = senoVuelta(t * k + m.fase).coerceAtLeast(0f).let { it * it * it }
            if (s > 0.02f) {
                val p = Offset(m.x * w, m.y * h); val r = 7f * u * m.tam * (0.4f + 0.6f * s)
                val oro = if (oscuro) Color(0xFFF0CF6A) else e.tertiary
                val q = r * 0.18f
                val destello = Path().apply {
                    moveTo(p.x, p.y - r); quadraticTo(p.x + q, p.y - q, p.x + r, p.y); quadraticTo(p.x + q, p.y + q, p.x, p.y + r)
                    quadraticTo(p.x - q, p.y + q, p.x - r, p.y); quadraticTo(p.x - q, p.y - q, p.x, p.y - r); close()
                }
                rotate(if (m.tono == 1) 45f else 0f, p) { drawPath(destello, al(oro, (if (oscuro) 0.6f else 0.4f) * s)) }
                drawCircle(al(oro, 0.25f * s), r * 0.15f, p)
            }
        }
        VivoTema.REFLECTORES -> {
            // Los reflectores de una noche de estreno barren el cielo desde el pie de los rascacielos; abajo, el perfil
            // escalonado de las torres con su aguja y, delante, burbujas de champán que suben despacio.
            val oro = if (oscuro) Color(0xFFF2D27A) else e.primary
            listOf(Triple(0.18f, 0f, 1), Triple(0.56f, 0.33f, 2), Triple(0.86f, 0.71f, 1)).forEach { (fx, fase, k) ->
                val pie = Offset(w * fx, h * 1.02f)
                val ang = (-PI / 2 + 0.5 * senoVuelta(t * k + fase)).toFloat()
                val largo = h * 1.15f
                val punta = Offset(pie.x + largo * cos(ang), pie.y + largo * sin(ang))
                val nx = -sin(ang); val ny = cos(ang); val ab = w * 0.07f
                val haz = Path().apply {
                    moveTo(pie.x - nx * 2f * u, pie.y - ny * 2f * u); lineTo(punta.x - nx * ab, punta.y - ny * ab)
                    lineTo(punta.x + nx * ab, punta.y + ny * ab); lineTo(pie.x + nx * 2f * u, pie.y + ny * 2f * u); close()
                }
                drawPath(haz, Brush.linearGradient(listOf(al(oro, if (oscuro) 0.16f else 0.1f), Color.Transparent), start = pie, end = punta))
            }
            val tinta = if (oscuro) Color(0xFFF0E6CC) else e.onBackground
            val sil = al(tinta, if (oscuro) 0.06f else 0.05f)
            val linea = h * 0.96f
            drawRect(sil, Offset(0f, linea), Size(w, h - linea))
            listOf(0.08f to 0.1f, 0.3f to 0.17f, 0.5f to 0.24f, 0.7f to 0.14f, 0.9f to 0.19f).forEach { (fx, fa) ->
                val a = h * fa; val an = w * 0.09f; val x = w * fx
                drawRect(sil, Offset(x - an / 2, linea - a * 0.6f), Size(an, a * 0.6f))
                drawRect(sil, Offset(x - an * 0.34f, linea - a * 0.82f), Size(an * 0.68f, a * 0.22f))
                drawRect(sil, Offset(x - an * 0.18f, linea - a * 0.95f), Size(an * 0.36f, a * 0.13f))
                drawLine(sil, Offset(x, linea - a * 0.95f), Offset(x, linea - a * 1.08f), 1.2f * u)
            }
            motas.take(14).forEach { m ->
                val k = vueltas(m)
                val vida = fraccionVivo(m.y + t * k)
                val x = (m.x + 0.012f * senoVuelta(t * 4 * k + m.fase)) * w
                val y = h * (1f - vida * 0.75f)
                drawCircle(al(oro, (if (oscuro) 0.35f else 0.25f) * (1f - vida)), 2.2f * u * (0.5f + m.tam), Offset(x, y), style = Stroke(0.8f * u))
            }
        }
        VivoTema.HORMIGAS -> motas.take(7).forEachIndexed { i, m ->
            // Unas pocas hormigas que cruzan despacio la hoja, cada una por su camino.
            val avance = (m.x + t * (0.5f + m.v * 0.6f)) % 1.3f - 0.15f
            val dir = if (i % 2 == 0) 1f else -1f
            val x = (if (dir > 0) avance else 1f - avance) * w
            val fase = 2 * PI * (avance * 2 + m.fase)
            val y = m.y * h + 22f * u * sin(fase).toFloat()
            val pend = (22f * u * cos(fase) * 2 * PI * 2 / w).toFloat()
            val ang = kotlin.math.atan2(pend * dir, dir)
            hormiga(Offset(x, y), 7f * u * (0.8f + m.tam * 0.3f), ang, al(e.onBackground, 0.16f), t * 6 + m.fase)
        }
        VivoTema.LUCIERNAGAS -> motas.forEach { m ->
            val k = vueltas(m)
            val x = (m.x + 0.04f * senoVuelta(t * k + m.fase)) * w
            val y = (m.y + 0.03f * cosenoVuelta(t * (k + 1) + m.fase)) * h
            val a = senoVuelta(t * 2 * k + m.fase).coerceAtLeast(0f)
            if (a > 0.01f) {
                drawCircle(Brush.radialGradient(listOf(al(Color(0xFFD8F07A), 0.45f * a), Color.Transparent), center = Offset(x, y), radius = 9f * u), 9f * u, Offset(x, y))
                drawCircle(al(Color(0xFFF2FFB0), 0.8f * a), 1.4f * u, Offset(x, y))
            }
        }
        VivoTema.VILANOS -> {
            // Vilanos de diente de león que suben con el aire tibio entre los edificios: el aquenio y su sombrilla de pelos.
            motas.take(18).forEach { m ->
                val k = vueltas(m)
                val sube = fraccionVivo(m.y + t * k)
                val y = h * (1.05f - sube * 1.1f)
                val x = (m.x + 0.05f * senoVuelta(t * 2 * k + m.fase) + 0.04f * sube) * w
                val l = 5f * u * m.tam
                rotate(18f * senoVuelta(t * 3 * k + m.fase), Offset(x, y)) {
                    drawLine(al(e.onBackground, 0.16f), Offset(x, y), Offset(x, y - l), 0.5f * u)
                    for (j in 0 until 7) {
                        val a = PI + PI * j / 6
                        drawLine(al(e.onBackground, 0.10f), Offset(x, y - l), Offset(x + (l * 0.8f * cos(a)).toFloat(), y - l + (l * 0.8f * sin(a)).toFloat()), 0.4f * u)
                    }
                    drawCircle(al(e.secondary, 0.3f), 0.9f * u, Offset(x, y))
                }
            }
            // Tres guías de hiedra que cuelgan del borde de arriba y se mecen; cada hoja tiene cinco puntas.
            fun hojaHiedra(p: Offset, r: Float, giro: Float, col: Color) = rotate(giro, p) {
                drawPath(Path().apply {
                    moveTo(p.x, p.y); lineTo(p.x - 0.55f * r, p.y + 0.15f * r); lineTo(p.x - 0.75f * r, p.y + 0.55f * r); lineTo(p.x - 0.3f * r, p.y + 0.55f * r)
                    lineTo(p.x, p.y + r); lineTo(p.x + 0.3f * r, p.y + 0.55f * r); lineTo(p.x + 0.75f * r, p.y + 0.55f * r); lineTo(p.x + 0.55f * r, p.y + 0.15f * r); close()
                }, col)
            }
            listOf(0.1f, 0.62f, 0.9f).forEachIndexed { i, fx ->
                val largo = h * (0.10f + 0.05f * i)
                val vaiven = 6f * u * senoVuelta(t * 2 + i * 0.3f)
                val base = Offset(fx * w, 0f); val punta = Offset(fx * w + vaiven, largo)
                drawLine(al(e.primary, 0.18f), base, punta, 0.8f * u)
                for (j in 1..4) {
                    val f = j / 5f
                    hojaHiedra(Offset(base.x + (punta.x - base.x) * f, largo * f), 5f * u, if (j % 2 == 0) 35f else -35f, al(e.primary, 0.16f))
                }
            }
        }
        VivoTema.GOTERAS -> motas.take(9).forEach { m ->
            // Goteras del techo roto: la gota cae acelerando y, al llegar a su charco, abre un anillo que se apaga.
            val k = vueltas(m)
            val f = fraccionVivo(m.fase + t * k)
            val x = (0.06f + 0.88f * m.x) * w
            val suelo = (0.55f + 0.4f * m.y) * h
            val agua = if (oscuro) Color(0xFF9FD0C8) else e.primary
            if (f < 0.6f) {
                val c = f / 0.6f
                val y = suelo * c * c
                drawLine(al(agua, 0.35f), Offset(x, y - 5f * u), Offset(x, y), 1.4f * u, cap = StrokeCap.Round)
            } else {
                val c = (f - 0.6f) / 0.4f
                val r = 14f * u * m.tam * c
                drawOval(al(agua, 0.3f * (1f - c)), Offset(x - r, suelo - r * 0.3f), Size(2 * r, r * 0.6f), style = Stroke(0.8f * u))
            }
            drawOval(al(agua, 0.06f), Offset(x - 16f * u, suelo - 2.5f * u), Size(32f * u, 5f * u))
        }
        VivoTema.NIEVE -> motas.forEach { m ->
            val k = vueltas(m)
            val y = fraccionVivo(m.y + t * k) * (h + 10f * u) - 5f * u
            val x = (m.x + 0.015f * senoVuelta(t * 6 * k + m.fase)) * w
            val col = al(if (oscuro) Color.White else e.secondary, if (oscuro) 0.35f else 0.14f)
            if (m.tono == 0) {
                // Algunos son copos de seis brazos que giran despacio.
                val l = 3.4f * u * m.tam
                rotate(60f * (t * k + m.fase), Offset(x, y)) {
                    for (j in 0 until 3) {
                        val a = PI / 3 * j
                        val d = Offset((l * cos(a)).toFloat(), (l * sin(a)).toFloat())
                        drawLine(col, Offset(x, y) - d, Offset(x, y) + d, 0.8f * u)
                    }
                }
            } else drawCircle(col, 1.8f * u * m.tam, Offset(x, y))
        }
        VivoTema.ANTORCHAS -> {
            // La muralla de la frontera: el perfil almenado abajo y cuatro antorchas cuya llama tiembla y suelta pavesas.
            // De día la luz casi no se ve; de noche, en la vigilia, alumbra el adarve.
            val muro = al(e.onBackground, if (oscuro) 0.06f else 0.05f)
            val base = h * 0.9f; val merlon = 18f * u
            drawRect(muro, Offset(0f, base), Size(w, h - base))
            var mx = 0f
            while (mx < w) { drawRect(muro, Offset(mx, base - 14f * u), Size(merlon, 14f * u)); mx += maxOf(merlon * 2, 1f) }
            val fuego = if (oscuro) Color(0xFFF0A040) else e.tertiary
            val sitios = listOf(0.12f, 0.38f, 0.64f, 0.9f)
            sitios.forEachIndexed { i, fx ->
                val tx = fx * w; val ty = base - 30f * u
                val tiembla = 0.85f + 0.15f * senoVuelta(t * (7 + i) + i * 0.3f)
                drawLine(muro, Offset(tx, ty), Offset(tx, base - 14f * u), 2.4f * u)
                val rr = 34f * u * tiembla; val cl = Offset(tx, ty - 6f * u)
                drawCircle(Brush.radialGradient(listOf(al(fuego, (if (oscuro) 0.16f else 0.07f) * tiembla), Color.Transparent), center = cl, radius = rr), rr, cl)
                val alto = 12f * u * tiembla; val ancho = 4.2f * u; val ladeo = 2f * u * senoVuelta(t * (5 + i) + 0.2f * i)
                drawPath(Path().apply {
                    moveTo(tx - ancho, ty); quadraticTo(tx - ancho, ty - alto * 0.6f, tx + ladeo, ty - alto); quadraticTo(tx + ancho, ty - alto * 0.6f, tx + ancho, ty); close()
                }, al(fuego, if (oscuro) 0.45f else 0.2f))
            }
            motas.take(16).forEach { m ->
                val vida = fraccionVivo(m.y + t * vueltas(m))
                val fx = sitios[(m.x * 4).toInt().coerceIn(0, 3)]
                val px = fx * w + 10f * u * senoVuelta(vida + m.fase) + vida * 10f * u
                val py = base - 42f * u - vida * h * 0.35f
                drawCircle(al(fuego, (1f - vida) * (if (oscuro) 0.5f else 0.22f)), 1.1f * u * m.tam, Offset(px, py))
            }
        }
        VivoTema.ARENA -> motas.forEach { m ->
            // Granos que el viento arrastra de lado en rachas, con una estela corta.
            val k = vueltas(m) + 1
            val avance = fraccionVivo(m.x + t * k)
            val x = (avance * 1.1f - 0.05f) * w
            val y = (m.y + 0.02f * senoVuelta(avance * 2 + m.fase)) * h
            val estela = 6f * u * (0.5f + m.v)
            drawLine(al(e.tertiary, 0.10f), Offset(x - estela, y + estela * 0.1f), Offset(x, y), 1f * u * m.tam)
            drawCircle(al(e.tertiary, 0.22f), 1.1f * u * m.tam, Offset(x, y))
        }
        VivoTema.PETALOS -> motas.take(22).forEach { m ->
            // Pétalos de cerezo (con su muesca en la punta) que caen meciéndose y girando, como en las estampas de primavera.
            val k = vueltas(m)
            val y = fraccionVivo(m.y + t * k) * (h + 30f * u) - 15f * u
            val x = (m.x + 0.06f * senoVuelta(t * 2 * k + m.fase)) * w
            val l = 5.5f * u * m.tam
            val col = al(if (oscuro) Color(0xFFF2C6CF) else Color(0xFFE08CA4), if (oscuro) 0.2f else 0.28f)
            rotate(200f * (t * 3 * k + m.fase), Offset(x, y)) {
                val petalo = Path().apply {
                    moveTo(x, y + l); cubicTo(x - l * 0.9f, y + l * 0.3f, x - l * 0.6f, y - l * 0.9f, x - l * 0.16f, y - l)
                    lineTo(x, y - l * 0.72f); lineTo(x + l * 0.16f, y - l)
                    cubicTo(x + l * 0.6f, y - l * 0.9f, x + l * 0.9f, y + l * 0.3f, x, y + l); close()
                }
                drawPath(petalo, col)
            }
        }
        VivoTema.VITRAL -> motas.take(14).forEach { m ->
            // Manchas de luz de colores que cruzan despacio, como el sol a través de las vidrieras.
            val x = (fraccionVivo(m.x + t) * 1.2f - 0.1f) * w; val y = (m.y + 0.03f * senoVuelta(t + m.fase)) * h
            val col = listOf(e.secondary, if (oscuro) e.tertiary else e.primary, Color(0xFF2E8B57), Color(0xFFE2B34A))[m.tono % 4]
            val r = 46f * u * m.tam
            drawCircle(Brush.radialGradient(listOf(al(col, if (oscuro) 0.12f else 0.09f), Color.Transparent), center = Offset(x, y), radius = r), r, Offset(x, y))
        }
        VivoTema.NIEBLA -> {
            // Bancos de niebla que cruzan despacio, unos más rápido que otros, y el haz del faro que va y viene dos veces por vuelta.
            val bruma = if (oscuro) Color(0xFF9AB0A6) else Color.White
            motas.take(7).forEach { m ->
                val x = (fraccionVivo(m.x + t * (1 + m.tono)) * 1.6f - 0.3f) * w; val y = (0.2f + m.y * 0.75f) * h; val s = u * m.tam
                listOf(Offset(0f, 0f) to 70f, Offset(60f, 10f) to 50f, Offset(-60f, 8f) to 46f).forEach { (o, r) ->
                    drawOval(Brush.radialGradient(listOf(al(bruma, if (oscuro) 0.06f else 0.24f), Color.Transparent), center = Offset(x + o.x * s, y + o.y * s), radius = r * s * 1.6f),
                        Offset(x + o.x * s - r * s * 1.6f, y + o.y * s - r * s * 0.45f), Size(r * s * 3.2f, r * s * 0.9f))
                }
            }
            val fuente = Offset(w * 0.88f, h * 0.16f)
            val ang = PI * 0.92 + 0.42 * senoVuelta(t * 2)
            val largo = maxOf(w, h) * 1.2f
            val p1 = Offset(fuente.x + (largo * cos(ang - 0.07)).toFloat(), fuente.y + (largo * sin(ang - 0.07)).toFloat())
            val p2 = Offset(fuente.x + (largo * cos(ang + 0.07)).toFloat(), fuente.y + (largo * sin(ang + 0.07)).toFloat())
            val luz = if (oscuro) e.primary else e.secondary
            drawPath(Path().apply { moveTo(fuente.x, fuente.y); lineTo(p1.x, p1.y); lineTo(p2.x, p2.y); close() },
                Brush.radialGradient(listOf(al(luz, if (oscuro) 0.09f else 0.05f), Color.Transparent), center = fuente, radius = largo))
            drawCircle(al(luz, if (oscuro) 0.3f else 0.12f), 3f * u, fuente)
        }
        VivoTema.ESTRELLAS -> motas.forEach { m ->
            val a = 0.15f + 0.55f * (0.5f + 0.5f * senoVuelta(t * 3 * vueltas(m) + m.fase))
            val p = Offset(m.x * w, m.y * h); val s = 2.2f * u * m.tam
            val col = al(if (oscuro) Color.White else e.primary, a * (if (oscuro) 0.6f else 0.25f))
            drawLine(col, p - Offset(s, 0f), p + Offset(s, 0f), 0.9f * u); drawLine(col, p - Offset(0f, s), p + Offset(0f, s), 0.9f * u)
            drawCircle(col, 0.9f * u, p)
        }
        VivoTema.MAR -> {
            // Nieve marina: restos diminutos que bajan sin prisa, de día y de noche.
            motas.forEach { m ->
                val k = vueltas(m)
                val y = fraccionVivo(m.y + t * k) * (h + 10f * u) - 5f * u
                val x = (m.x + 0.012f * senoVuelta(t * 2 * k + m.fase)) * w
                drawCircle(al(if (oscuro) Color(0xFFC4ECE6) else e.onBackground, if (oscuro) 0.18f else 0.07f), 0.8f * u * m.tam, Offset(x, y))
            }
            if (!oscuro) {
                // Zona iluminada: haces de sol que bajan desde la superficie y se mecen con el oleaje.
                for (j in 0 until 4) {
                    val x0 = w * (0.08f + 0.26f * j) + 16f * u * senoVuelta(t * 2 + j * 0.27f)
                    val ancho = w * (0.04f + 0.03f * (j % 2))
                    val haz = Path().apply { moveTo(x0, 0f); lineTo(x0 + ancho, 0f); lineTo(x0 + ancho * 2.4f + w * 0.12f, h); lineTo(x0 + w * 0.12f, h); close() }
                    val luz = 0.7f + 0.3f * senoVuelta(t * 3 + j * 0.4f)
                    drawPath(haz, Brush.verticalGradient(listOf(al(Color.White, 0.22f * luz), Color.Transparent), startY = 0f, endY = h * 0.8f))
                }
                // Un cardumen pequeño que cruza de un lado a otro, meciéndose.
                motas.take(7).forEachIndexed { i, m ->
                    val avance = fraccionVivo(m.x + t * (1 + i % 2))
                    val x = (avance * 1.3f - 0.15f) * w
                    val y = (0.3f + 0.25f * m.y) * h + 8f * u * senoVuelta(t * 3 + m.fase)
                    val l = 6f * u * m.tam
                    val pez = Path().apply {
                        moveTo(x + l, y); quadraticTo(x, y - l * 0.45f, x - l * 0.7f, y); quadraticTo(x, y + l * 0.45f, x + l, y); close()
                        moveTo(x - l * 0.6f, y); lineTo(x - l * 1.1f, y - l * 0.35f); lineTo(x - l * 1.1f, y + l * 0.35f); close()
                    }
                    drawPath(pez, al(e.primary, 0.13f))
                }
            } else {
                // Zona abisal: medusas con luz propia que laten (la campana se cierra y se abre) y suben despacio.
                motas.take(7).forEach { m ->
                    val k = vueltas(m)
                    val sube = fraccionVivo(m.y + t)
                    val y = h * (1.1f - sube * 1.2f)
                    val x = (m.x + 0.03f * senoVuelta(t * k + m.fase)) * w
                    val late = 0.5f + 0.5f * senoVuelta(t * 6 * k + m.fase)
                    val r = 11f * u * m.tam
                    val ancho = r * (1.1f - 0.25f * late); val alto = r * (0.7f + 0.2f * late)
                    val luz = when (m.tono) { 0 -> e.primary; 1 -> e.secondary; else -> e.tertiary }
                    drawCircle(Brush.radialGradient(listOf(al(luz, 0.14f), Color.Transparent), center = Offset(x, y), radius = r * 2.6f), r * 2.6f, Offset(x, y))
                    val campana = Path().apply {
                        moveTo(x - ancho, y); cubicTo(x - ancho, y - alto * 1.3f, x + ancho, y - alto * 1.3f, x + ancho, y)
                        quadraticTo(x, y - alto * 0.25f, x - ancho, y); close()
                    }
                    drawPath(campana, al(luz, 0.2f))
                    drawPath(campana, al(luz, 0.42f), style = Stroke(0.8f * u))
                    for (j in 0 until 4) {
                        val tx = x - ancho * 0.7f + ancho * 0.47f * j
                        val onda = 3f * u * senoVuelta(t * 6 * k + m.fase + j * 0.2f)
                        val tentaculo = Path().apply { moveTo(tx, y - alto * 0.1f); quadraticTo(tx + onda, y + r * 0.9f, tx - onda * 0.6f, y + r * (1.5f + 0.3f * late)) }
                        drawPath(tentaculo, al(luz, 0.24f), style = Stroke(0.6f * u))
                    }
                }
            }
        }
        VivoTema.AGUA -> {
            // Reflejos de una alberca: líneas onduladas que se mecen.
            for (k in 0 until 6) {
                val y0 = h * (0.15f + k * 0.15f)
                val p = Path(); var x = 0f
                p.moveTo(0f, y0)
                while (x <= w) { p.lineTo(x, y0 + 5f * u * senoVuelta(x / (90f * u) + t + k * 0.3f)); x += 6f * u }
                drawPath(p, al(e.secondary, if (oscuro) 0.06f else 0.05f), style = Stroke(1.2f * u))
            }
        }
        VivoTema.NUBES -> motas.take(6).forEach { m ->
            val x = (fraccionVivo(m.x + t) * 1.4f - 0.2f) * w; val y = m.y * h * 0.6f
            val col = al(Color.White, if (oscuro) 0.025f else 0.35f)
            listOf(Offset(0f, 0f) to 26f, Offset(22f, -8f) to 20f, Offset(-22f, -4f) to 18f, Offset(40f, 4f) to 14f).forEach { (o, r) ->
                drawCircle(col, r * u * m.tam, Offset(x + o.x * u * m.tam, y + o.y * u * m.tam))
            }
        }
        VivoTema.SAMARAS -> motas.take(12).forEach { m ->
            // Semillas aladas de arce que caen girando sobre su nuez, como el tornillo aéreo de los cuadernos.
            val k = vueltas(m)
            val y = fraccionVivo(m.y + t * k) * (h + 40f * u) - 20f * u
            val x = (m.x + 0.05f * senoVuelta(t * k + m.fase)) * w
            val l = 13f * u * m.tam
            val sepia = e.onBackground
            rotate(360f * (t * 6 * k + m.fase), Offset(x, y)) {
                val ala = Path().apply {
                    moveTo(x, y - l * 0.12f); cubicTo(x + l * 0.4f, y - l * 0.36f, x + l * 0.95f, y - l * 0.26f, x + l, y)
                    cubicTo(x + l * 0.8f, y + l * 0.16f, x + l * 0.3f, y + l * 0.12f, x, y + l * 0.12f); close()
                }
                drawPath(ala, al(sepia, if (oscuro) 0.12f else 0.10f))
                for (j in 1..3) drawLine(al(sepia, 0.12f), Offset(x, y), Offset(x + l * (0.55f + 0.13f * j), y - l * (0.18f - 0.06f * j)), 0.5f * u)
                drawCircle(al(sepia, 0.2f), l * 0.14f, Offset(x, y))
            }
        }
        VivoTema.OLEAJE -> {
            // La mar de fondo abajo: tres líneas de oleaje que avanzan, con borreguitos de espuma en las crestas.
            val tinta = e.onBackground
            for (k in 0 until 3) {
                val y0 = h * (0.8f + k * 0.07f); val largo = (150f + 40f * k) * u; val alto = (4f + 1.5f * k) * u
                val desliza = t * (k + 1)
                val p = Path(); var x = -largo
                p.moveTo(x, y0)
                while (x <= w + largo) { p.lineTo(x, y0 - alto * senoVuelta(x / largo - desliza)); x += 5f * u }
                drawPath(p, al(tinta, if (oscuro) 0.07f else 0.08f), style = Stroke((1f + 0.3f * k) * u))
                var c = fraccionVivo(desliza) * largo - largo
                while (c < w + largo) {
                    // Cresta: donde el seno vale 1 (x / largo - desliza = 1/4 + n).
                    if (c > -largo) {
                        val cx = c + largo * 0.25f
                        drawLine(al(tinta, if (oscuro) 0.09f else 0.11f), Offset(cx - 5f * u, y0 - alto + 1.5f * u), Offset(cx + 1f * u, y0 - alto - 1f * u), 0.9f * u, cap = StrokeCap.Round)
                    }
                    c += maxOf(largo, 1f)
                }
            }
            // Gaviotas que cruzan planeando y a ratos aletean (una «m» abierta, como en los grabados de las cartas).
            motas.take(7).forEach { m ->
                val k = 1 + (m.v * 1.99f).toInt()
                val x = (fraccionVivo(m.x + t * k) * 1.3f - 0.15f) * w
                val y = h * (0.1f + m.y * 0.55f) + 8f * u * senoVuelta(t * k + m.fase)
                val ala = 9f * u * m.tam
                val aleteo = senoVuelta(t * 9 * k + m.fase * 3).let { if (it > 0.4f) 0.5f * it else 0f }
                val col = al(tinta, if (oscuro) 0.16f else 0.2f)
                val g = Path().apply {
                    moveTo(x - ala, y - ala * (0.15f + aleteo))
                    quadraticTo(x - ala * 0.45f, y - ala * (0.55f + aleteo), x, y)
                    quadraticTo(x + ala * 0.45f, y - ala * (0.55f + aleteo), x + ala, y - ala * (0.15f + aleteo))
                }
                drawPath(g, col, style = Stroke(1.2f * u, cap = StrokeCap.Round))
            }
        }
        VivoTema.ESTRELLAS_ROJAS -> motas.take(14).forEach { m ->
            // Estrellas rojas de cinco puntas que suben despacio, meciéndose; algunas solo de contorno.
            val k = vueltas(m)
            val sube = fraccionVivo(m.y + t * k)
            val y = h * (1.05f - sube * 1.1f)
            val x = (m.x + 0.06f * sube) * w
            val r = 6f * u * m.tam
            val rojo = if (oscuro) Color(0xFFE0393E) else e.primary
            val estrella = Path().apply {
                for (i in 0 until 10) {
                    val rr = if (i % 2 == 0) r else r * 0.42f
                    val ang = -PI / 2 + PI * i / 5
                    val px = x + (rr * cos(ang)).toFloat(); val py = y + (rr * sin(ang)).toFloat()
                    if (i == 0) moveTo(px, py) else lineTo(px, py)
                }
                close()
            }
            rotate(20f * senoVuelta(t * k + m.fase), Offset(x, y)) {
                if (m.tono == 2) drawPath(estrella, al(rojo, 0.2f), style = Stroke(0.9f * u)) else drawPath(estrella, al(rojo, if (oscuro) 0.16f else 0.12f))
            }
        }
        VivoTema.CONFETI -> motas.take(26).forEach { m ->
            // Papelitos azules, blancos y rojos que caen volteándose.
            val k = vueltas(m)
            val y = fraccionVivo(m.y + t * k) * (h + 20f * u) - 10f * u
            val x = (m.x + 0.025f * senoVuelta(t * 3 * k + m.fase)) * w
            val col = when (m.tono) { 0 -> Color(0xFF2B4BA8); 1 -> if (oscuro) Color(0xFFF2EEE4) else Color.White; else -> Color(0xFFC8352E) }
            rotate(360f * (t * 2 * k + m.fase), Offset(x, y)) {
                scale(1f, 0.15f + 0.85f * abs(cosenoVuelta(t * 4 * k + m.fase)), Offset(x, y)) {
                    val o = Offset(x - 1.6f * u * m.tam, y - 3f * u * m.tam); val s = Size(3.2f * u * m.tam, 6f * u * m.tam)
                    drawRect(al(col, if (oscuro) 0.22f else 0.20f), o, s)
                    if (m.tono == 1 && !oscuro) drawRect(al(e.onBackground, 0.12f), o, s, style = Stroke(0.5f * u))
                }
            }
        }
        VivoTema.RUNAS -> motas.take(14).forEachIndexed { i, m ->
            // Runas grabadas que se encienden, suben un poco al brillar y se apagan.
            val k = vueltas(m)
            val brillo = senoVuelta(t * k + m.fase).coerceAtLeast(0f).let { it * it }
            if (brillo > 0.02f) {
                val alto = 16f * u * m.tam
                val o = Offset(m.x * w, m.y * h - 6f * u * brillo)
                val luz = if (oscuro) Color(0xFF9FD8FF) else e.tertiary
                val runa = RUNAS_VIVO[i % RUNAS_VIVO.size]
                runaVivo(runa, o, alto, al(luz, 0.12f * brillo), 4f * u)
                runaVivo(runa, o, alto, al(luz, (if (oscuro) 0.5f else 0.35f) * brillo), 1.2f * u)
            }
        }
        VivoTema.TZOLKIN -> {
            // La cuenta de 260 días como dos ruedas dentadas que engranan: la de los 13 números y la de los 20 signos.
            // Cada segundo avanzan un diente las dos (un día), con un golpe suave; a los 260 segundos vuelven a empezar
            // juntas, como el tzolk'in. Abajo a la izquierda, el humo de un brasero de copal.
            val pasos = 260f * t; val dia = floor(pasos); val f = ((pasos - dia) / 0.35f).coerceIn(0f, 1f)
            val avance = dia + f * f * (3f - 2f * f)
            val tinta = al(e.onBackground, if (oscuro) 0.07f else 0.06f)
            val acento = al(if (oscuro) e.primary else e.secondary, if (oscuro) 0.16f else 0.11f)
            val r20 = 0.36f * w; val r13 = r20 * 13f / 20f
            val c20 = Offset(w * 0.86f, h * 0.84f)
            val ang = Math.toRadians(-128.0)
            val c13 = Offset(c20.x + (r20 + r13 * 1.02f) * cos(ang).toFloat(), c20.y + (r20 + r13 * 1.02f) * sin(ang).toFloat())
            fun rueda(c: Offset, r: Float, dientes: Int, giro: Float, numeros: Boolean) {
                drawCircle(tinta, r * 0.9f, c, style = Stroke(1.4f * u))
                drawCircle(tinta, r * 0.56f, c, style = Stroke(1f * u))
                drawCircle(tinta, r * 0.1f, c)
                val diente = (2f * PI.toFloat() * r / dientes) * 0.5f
                for (k in 0 until dientes) rotate(giro + k * 360f / dientes, c) {
                    drawRect(tinta, Offset(c.x - diente / 2, c.y - r * 1.06f), Size(diente, r * 0.16f))
                    drawLine(tinta, Offset(c.x, c.y - r * 0.9f), Offset(c.x, c.y - r * 0.56f), 0.6f * u)
                    val m = Offset(c.x, c.y - r * 0.73f); val col = if (k == 0) acento else tinta
                    rotate(180f / dientes, c) {
                        if (numeros) numeralMaya(k + 1, m, r * 0.15f, col)
                        else drawRoundRect(col, Offset(m.x - r * 0.07f, m.y - r * 0.07f), Size(r * 0.14f, r * 0.14f), androidx.compose.ui.geometry.CornerRadius(r * 0.04f), style = Stroke(0.9f * u))
                    }
                }
            }
            rueda(c20, r20, 20, 18f * avance, false)
            rueda(c13, r13, 13, -360f / 13f * avance + 180f / 13f, true)
            motas.take(9).forEach { m ->
                val vida = fraccionVivo(m.y + t * 40f * vueltas(m))
                val x = w * 0.12f + 26f * u * senoVuelta(vida * 1.5f + m.fase) * vida
                val y = h * 0.98f - vida * h * 0.42f
                val rr = (8f + 26f * vida) * u * m.tam
                drawCircle(Brush.radialGradient(listOf(al(e.onBackground, (1f - vida) * (if (oscuro) 0.08f else 0.06f)), Color.Transparent), center = Offset(x, y), radius = rr), rr, Offset(x, y))
            }
        }
        VivoTema.MANOS -> {
            // Manos en negativo que aparecen en la pared como sopladas, se quedan un rato y se borran; e hileras de puntos
            // de ocre que titilan.
            motas.take(7).forEachIndexed { i, m ->
                val f = fraccionVivo(t * (1 + i % 2) + m.fase)
                val a = (if (f < 0.25f) f / 0.25f else if (f < 0.7f) 1f else (1f - f) / 0.3f).coerceIn(0f, 1f)
                if (a > 0.01f) {
                    val o = Offset((0.08f + 0.84f * m.x) * w, (0.1f + 0.8f * m.y) * h); val r = 18f * u * (0.8f + 0.5f * m.tam)
                    drawCircle(Brush.radialGradient(listOf(al(e.primary, 0.16f * a), al(e.primary, 0.08f * a), Color.Transparent), center = o, radius = r * 1.6f), r * 1.6f, o)
                    rotate((m.v - 0.5f) * 50f, o) { drawPath(manoUnida(o, r), al(e.background, 0.9f * a)) }
                }
            }
            motas.drop(7).take(10).forEach { m ->
                val brillo = 0.5f + 0.5f * senoVuelta(t * vueltas(m) + m.fase)
                val o = Offset(m.x * w, m.y * h)
                for (j in 0 until 4) drawCircle(al(e.primary, 0.08f + 0.1f * brillo), 2.2f * u, Offset(o.x + j * 7f * u, o.y + (j % 2) * 2f * u))
            }
        }
        VivoTema.HOGUERA -> {
            // La luz de una hoguera en lo hondo de la cueva: el resplandor de abajo respira y tiembla, las brasas suben
            // meciéndose y se apagan, y la sombra del techo baja y sube con la llama.
            val temblor = 0.75f + 0.15f * senoVuelta(t * 7) + 0.1f * senoVuelta(t * 13 + 0.3f)
            val fuego = Offset(w * 0.5f, h * 1.02f)
            drawCircle(Brush.radialGradient(listOf(al(Color(0xFFFF9A3C), 0.16f * temblor), al(Color(0xFFE8804F), 0.07f * temblor), Color.Transparent), center = fuego, radius = h * 0.6f), h * 0.6f, fuego)
            motas.take(16).forEach { m ->
                val k = vueltas(m)
                val sube = fraccionVivo(m.y + t * k)
                val y = h * (1.02f - sube * 0.75f)
                val x = (0.3f + 0.4f * m.x + 0.04f * senoVuelta(t * 3 * k + m.fase) * sube) * w
                val a = (1f - sube) * (0.6f + 0.4f * senoVuelta(t * 9 * k + m.fase))
                if (a > 0.02f) {
                    drawCircle(al(Color(0xFFFFB347), 0.18f * a), 3.5f * u * m.tam, Offset(x, y))
                    drawCircle(al(Color(0xFFFFE08A), 0.6f * a), 1.1f * u, Offset(x, y))
                }
            }
            drawRect(Brush.verticalGradient(listOf(al(Color.Black, (0.25f * (1.2f - temblor)).coerceAtLeast(0f)), Color.Transparent), 0f, h * 0.3f), Offset.Zero, Size(w, h * 0.3f))
        }
        VivoTema.JEROGLIFOS -> {
            // Un muro de signos en columnas, como se escribe en los templos: la luz de una lámpara pasa despacio de un lado
            // a otro y los signos que alcanza se encienden en oro y vuelven a la sombra.
            val columnas = 7; val ancho = w / columnas
            val lampara = 0.5f + 0.5f * senoVuelta(t)
            motas.take(28).forEachIndexed { i, m ->
                val col = i % columnas
                val x = (col + 0.5f) * ancho
                val y = (((m.y + 0.04f * senoVuelta(t + m.fase)) * 0.92f + 0.04f) * h)
                val cerca = (1f - abs(col / (columnas - 1f) - lampara) * 2.2f).coerceIn(0f, 1f)
                val brillo = cerca * (0.55f + 0.45f * senoVuelta(t * vueltas(m) + m.fase).coerceAtLeast(0f))
                val alto = 15f * u * (0.8f + m.tam * 0.3f)
                val tinta = e.primary
                signoEgipcio(i + m.tono, Offset(x, y), alto, al(tinta, 0.05f + (if (oscuro) 0.38f else 0.22f) * brillo), 1.3f * u)
            }
            for (k in 1 until columnas) drawLine(al(e.onBackground, 0.03f), Offset(k * ancho, 0f), Offset(k * ancho, h), 0.6f * u)
        }
    }
}

/** Lo que flota en el fondo de cada tema ([vivoTema]): el mismo dibujo que el fondo vivo del teléfono, más tenue. */
@Composable
fun FondoVivo(tema: TemaId = Estilo.tema, fijo: Float? = null) {
    val c = Repo.config
    // [fijo]: un instante quieto (para el fondo del teléfono y las capturas), aunque las animaciones estén apagadas.
    if (fijo == null && (!c.animaciones || !c.fondo_vivo)) return
    val e = MaterialTheme.colorScheme
    val oscuro = e.background.luminance() < 0.5f
    val tipo = vivoTema(tema, oscuro)
    val motas = remember(tema) { motasVivo(tema) }
    val t = if (fijo != null) fijo else {
        val v by rememberInfiniteTransition(label = "fondo").animateFloat(0f, 1f, infiniteRepeatable(tween(tipo.periodo, easing = LinearEasing)), label = "t")
        v
    }
    Canvas(Modifier.fillMaxSize()) { pintarVivoTema(tipo, t, oscuro, e, motas) }
}

// =====================================================================
//  Al cambiar de tema: el tema nuevo se abre, con su forma, desde donde tocaste
// =====================================================================

/** La pantalla tal como estaba con el tema anterior, y desde dónde se abre el nuevo. */
object TransicionTema {
    var foto by androidx.compose.runtime.mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null)
    var centro by androidx.compose.runtime.mutableStateOf(Offset.Zero)
    var vuelta by androidx.compose.runtime.mutableIntStateOf(0)

    /** Toma la foto de la pantalla antes de cambiar el tema. Si no se puede (o no hay animaciones), simplemente no hay transición. */
    fun preparar(vista: android.view.View, centroEnVentana: Offset) {
        if (!Repo.config.animaciones) return
        runCatching {
            val raiz = vista.rootView
            if (raiz.width <= 0 || raiz.height <= 0) return
            val bmp = android.graphics.Bitmap.createBitmap(raiz.width, raiz.height, android.graphics.Bitmap.Config.ARGB_8888)
            raiz.draw(android.graphics.Canvas(bmp))
            foto = bmp.asImageBitmap()
            centro = centroEnVentana
            vuelta++
        }
    }
}

/** Un polígono estrellado: [puntas] vértices a [fuera] y otros tantos a [dentro] (iguales: polígono regular). */
private fun Path.poligonoBarrido(c: Offset, puntas: Int, fuera: Float, dentro: Float, giroGrados: Float) {
    val n = puntas * 2
    for (i in 0 until n) {
        val r = if (i % 2 == 0) fuera else dentro
        val a = Math.toRadians(giroGrados.toDouble()) - PI / 2 + PI * i / puntas
        val x = c.x + (r * cos(a)).toFloat(); val y = c.y + (r * sin(a)).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

/**
 * El hueco por donde se ve el tema nuevo, en el avance [p] (0 a 1). [r0]: la distancia del toque a la esquina más lejana;
 * cada forma crece lo bastante para cubrir un círculo de radio [r0]·[p], así al llegar a 1 cubre toda la pantalla.
 */
private fun agujeroTema(b: BarridoTema, c: Offset, r0: Float, p: Float, w: Float, h: Float, u: Float): Path {
    val ruta = Path()
    val r = r0 * p
    when (b) {
        BarridoTema.CIRCULO -> ruta.addOval(Rect(c, r))
        BarridoTema.MAREA -> {
            // El agua sube desde abajo con su ola hasta cubrir la pantalla.
            val nivel = h * 1.04f - h * 1.14f * p
            ruta.moveTo(0f, h); ruta.lineTo(0f, nivel)
            var x = 0f
            while (x <= w) { ruta.lineTo(x, nivel + 9f * u * sin(x / (38f * u) + 7f * p)); x += 8f * u }
            ruta.lineTo(w, nivel); ruta.lineTo(w, h); ruta.close()
        }
        BarridoTema.ARCO -> {
            val s = r * 1.3f
            ruta.moveTo(c.x - s, c.y + s); ruta.lineTo(c.x - s, c.y - s * 0.2f)
            ruta.arcTo(Rect(c.x - s, c.y - s * 1.2f, c.x + s, c.y + s * 0.8f), 180f, 180f, false)
            ruta.lineTo(c.x + s, c.y + s); ruta.close()
        }
        BarridoTema.CUPOLA -> {
            // La cúpula: tambor recto, casquete de media elipse y la linterna arriba.
            val s = r * 1.35f
            ruta.moveTo(c.x - s, c.y + s); ruta.lineTo(c.x - s, c.y)
            ruta.arcTo(Rect(c.x - s, c.y - s * 1.1f, c.x + s, c.y + s * 1.1f), 180f, 180f, false)
            ruta.lineTo(c.x + s, c.y + s); ruta.close()
            ruta.addRect(Rect(c.x - s * 0.14f, c.y - s * 1.45f, c.x + s * 0.14f, c.y - s * 1.0f))
        }
        BarridoTema.OJIVA -> {
            val s = r * 1.4f
            ruta.moveTo(c.x - s, c.y + s); ruta.lineTo(c.x - s, c.y - s * 0.2f)
            ruta.cubicTo(c.x - s, c.y - s * 0.9f, c.x - s * 0.3f, c.y - s * 1.5f, c.x, c.y - s * 1.8f)
            ruta.cubicTo(c.x + s * 0.3f, c.y - s * 1.5f, c.x + s, c.y - s * 0.9f, c.x + s, c.y - s * 0.2f)
            ruta.lineTo(c.x + s, c.y + s); ruta.close()
        }
        BarridoTema.CRUZ -> {
            // La cruz patada sobre su disco: el disco cubre el círculo de radio r y los cuatro brazos se ensanchan hacia fuera.
            val r1 = r * 1.02f; val l = r * 2.3f
            fun pt(rad: Float, g: Float) = Offset(c.x + rad * cos(Math.toRadians(g.toDouble())).toFloat(), c.y + rad * sin(Math.toRadians(g.toDouble())).toFloat())
            for (k in 0 until 4) {
                val g = k * 90f - 90f
                val p1 = pt(r1, g - 14f); val p2 = pt(l, g - 24f); val p3 = pt(l, g + 24f); val p4 = pt(r1, g + 14f); val q = pt(l * 0.93f, g)
                if (k == 0) ruta.moveTo(p1.x, p1.y) else ruta.lineTo(p1.x, p1.y)
                ruta.lineTo(p2.x, p2.y); ruta.quadraticTo(q.x, q.y, p3.x, p3.y); ruta.lineTo(p4.x, p4.y)
                ruta.arcTo(Rect(c, r1), g + 14f, 62f, false)
            }
            ruta.close()
        }
        BarridoTema.TINTA -> {
            // Una mancha de tinta: doce lóbulos desiguales que se ondulan mientras crece.
            val n = 12
            fun borde(k: Int): Offset {
                val a = 2 * PI * k / n
                val rr = r * 1.45f * (1f + 0.22f * sin(k * 2.7 + p * 3.0).toFloat())
                return Offset(c.x + (rr * cos(a)).toFloat(), c.y + (rr * sin(a)).toFloat())
            }
            val primero = borde(0)
            ruta.moveTo(primero.x, primero.y)
            for (k in 1..n) {
                val q = borde(k % n); val am = 2 * PI * (k - 0.5) / n; val rm = r * 1.25f
                ruta.quadraticTo(c.x + (rm * cos(am)).toFloat(), c.y + (rm * sin(am)).toFloat(), q.x, q.y)
            }
            ruta.close()
        }
        BarridoTema.ROMBO -> {
            val d = r * 1.42f
            ruta.moveTo(c.x, c.y - d); ruta.lineTo(c.x + d, c.y); ruta.lineTo(c.x, c.y + d); ruta.lineTo(c.x - d, c.y); ruta.close()
        }
        BarridoTema.OJO -> {
            val a = r * 2.3f; val k = r * 2.6f
            ruta.moveTo(c.x - a, c.y); ruta.quadraticTo(c.x, c.y - k, c.x + a, c.y); ruta.quadraticTo(c.x, c.y + k, c.x - a, c.y); ruta.close()
        }
        BarridoTema.HOJA -> {
            // La misma vesica que el ojo, pero inclinada 45° y más larga: una hoja.
            val d = Offset(0.7071f, -0.7071f); val n = Offset(0.7071f, 0.7071f)
            val l = r * 2.5f; val k = r * 2.7f
            val a = c - d * l; val z = c + d * l
            ruta.moveTo(a.x, a.y); ruta.quadraticTo(c.x + n.x * k, c.y + n.y * k, z.x, z.y); ruta.quadraticTo(c.x - n.x * k, c.y - n.y * k, a.x, a.y); ruta.close()
        }
        BarridoTema.GRIETA -> {
            // Un boquete en el hormigón: borde quebrado de 22 vértices con radios desiguales (fijos, para que no tiemble).
            // El vértice de dentro queda a 1.2·r o más, así el borde nunca pasa por dentro del círculo de radio r.
            val n = 22
            for (i in 0 until n) {
                val a = 2 * PI * i / n + 0.3f * p
                val ruido = (((i * 2654435761L) ushr 7) and 255L) / 255f
                val rr = r * (if (i % 2 == 0) 1.55f + 0.35f * ruido else 1.2f + 0.15f * ruido)
                val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
                if (i == 0) ruta.moveTo(x, y) else ruta.lineTo(x, y)
            }
            ruta.close()
        }
        BarridoTema.ESCOTILLA -> ruta.poligonoBarrido(c, 4, r * 1.1f, r * 1.1f, 22.5f + 45f * p)
        BarridoTema.HEXAGONO -> ruta.poligonoBarrido(c, 3, r * 1.2f, r * 1.2f, 30f)
        BarridoTema.SOPLO -> {
            // La nube de ocre soplado: un borde de motas desparejas (radios fijos, siempre más allá de 1.2·r) que gira apenas.
            val n = 28
            for (i in 0 until n) {
                val a = 2 * PI * i / n + 0.15f * p
                val ruido = (((i * 2246822519L) ushr 9) and 255L) / 255f
                val rr = r * (1.22f + 0.28f * ruido)
                val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
                if (i == 0) ruta.moveTo(x, y) else ruta.lineTo(x, y)
            }
            ruta.close()
        }
        BarridoTema.SOL -> ruta.poligonoBarrido(c, 16, r * 2f, r * 1.2f, 20f * p)
        // Triángulo equilátero con el vértice arriba: su círculo inscrito tiene radio r, así que sus vértices van a 2r.
        BarridoTema.PIRAMIDE -> {
            val s = r * 2.05f
            ruta.moveTo(c.x, c.y - s); ruta.lineTo(c.x + s * 0.866f, c.y + s * 0.5f); ruta.lineTo(c.x - s * 0.866f, c.y + s * 0.5f); ruta.close()
        }
        BarridoTema.GRADAS -> {
            // Rombo escalonado: cada lado baja en cinco gradas; las esquinas de adentro quedan sobre el rombo de
            // semidiagonal d, cuyo círculo inscrito (radio d/√2) cubre al de radio r.
            val d = r * 1.5f; val n = 5; val g = d / n
            for (q in 0 until 4) {
                // (sx, sy): hacia dónde avanza cada cuarto, en el sentido de las agujas del reloj desde arriba.
                val sx = if (q == 0 || q == 3) 1f else -1f; val sy = if (q < 2) 1f else -1f
                val x0 = c.x + (if (q == 1) d else if (q == 3) -d else 0f)
                val y0 = c.y + (if (q == 0) -d else if (q == 2) d else 0f)
                if (q == 0) ruta.moveTo(x0, y0)
                for (k in 0 until n) {
                    if (q % 2 == 0) { ruta.lineTo(x0 + sx * g * (k + 1), y0 + sy * g * k); ruta.lineTo(x0 + sx * g * (k + 1), y0 + sy * g * (k + 1)) }
                    else { ruta.lineTo(x0 + sx * g * k, y0 + sy * g * (k + 1)); ruta.lineTo(x0 + sx * g * (k + 1), y0 + sy * g * (k + 1)) }
                }
            }
            ruta.close()
        }
        BarridoTema.COPO -> ruta.poligonoBarrido(c, 6, r * 2f, r * 1.25f, 0f)
        BarridoTema.OCHO -> ruta.poligonoBarrido(c, 8, r * 1.75f, r * 1.3f, 22.5f)
        BarridoTema.ESTRELLA -> ruta.poligonoBarrido(c, 5, r * 2.6f, r * 1.25f, 0f)
        BarridoTema.ENGRANE -> ruta.poligonoBarrido(c, 12, r * 1.25f, r * 1.08f, 30f * p)
        BarridoTema.MEMBRANA -> {
            for (i in 0..72) {
                val a = 2 * PI * i / 72
                val rr = r * (1.08f + 0.06f * sin(7 * a + 6 * p).toFloat())
                val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
                if (i == 0) ruta.moveTo(x, y) else ruta.lineTo(x, y)
            }
            ruta.close()
        }
        BarridoTema.VITRUVIO -> {
            ruta.addOval(Rect(c, r * 1.02f))
            ruta.addRect(Rect(c.x - r * 0.9f, c.y - r * 0.9f, c.x + r * 0.9f, c.y + r * 0.9f))
        }
        BarridoTema.ROSA -> {
            // Rosa de los vientos de ocho puntas: las cuatro cardinales largas, las otras cuatro cortas; gira un poco al abrirse.
            for (i in 0 until 16) {
                val rr = when { i % 4 == 0 -> r * 2.5f; i % 2 == 0 -> r * 1.7f; else -> r * 1.05f }
                val a = Math.toRadians(15.0 * p) - PI / 2 + PI * i / 8
                val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
                if (i == 0) ruta.moveTo(x, y) else ruta.lineTo(x, y)
            }
            ruta.close()
        }
        BarridoTema.PIXELES -> {
            // Celdas de la rejilla que se encienden por distancia al toque, con un poco de desorden.
            val lado = 24f * u
            val umbral = (r0 * 1.25f + 34f * u) * p
            var j = 0
            while (j * lado < h) {
                var i = 0
                while (i * lado < w) {
                    val centro = Offset((i + 0.5f) * lado, (j + 0.5f) * lado)
                    val ruido = (((i * 73856093) xor (j * 19349663)) and 1023) / 1023f * 0.2f * r0
                    if ((centro - c).getDistance() + ruido <= umbral) ruta.addRect(Rect(i * lado, j * lado, (i + 1) * lado, (j + 1) * lado))
                    i++
                }
                j++
            }
        }
        BarridoTema.COLUMNAS -> {
            val n = 6; val ancho = w / n
            val propia = (c.x / ancho).toInt().coerceIn(0, n - 1)
            for (i in 0 until n) {
                val q = ((p - abs(i - propia) * 0.07f) / (1f - 5 * 0.07f)).coerceIn(0f, 1f)
                if (q > 0f) ruta.addRect(Rect(i * ancho, 0f, (i + 1) * ancho, h * q))
            }
        }
        BarridoTema.BANDAS -> {
            val ancho = w / 3
            for (i in 0 until 3) {
                val q = ((p - i * 0.12f) / 0.76f).coerceIn(0f, 1f)
                if (q > 0f) {
                    if (i == 1) ruta.addRect(Rect(ancho, h * (1f - q), 2 * ancho, h)) else ruta.addRect(Rect(i * ancho, 0f, (i + 1) * ancho, h * q))
                }
            }
        }
        BarridoTema.PERSIANA -> {
            // La persiana metálica que sube desde abajo, con la orilla en lamas.
            val alto = h * p * 1.03f
            ruta.addRect(Rect(0f, h - alto, w, h))
        }
        BarridoTema.CORTINA -> {
            val s = maxOf(c.x, w - c.x) * p * 1.02f
            ruta.addRect(Rect(c.x - s, 0f, c.x + s, h))
        }
        BarridoTema.ESCALONES -> {
            // El cuerpo de la torre, tres retranqueos cada vez más estrechos y la aguja.
            val s = r * 1.5f
            ruta.addRect(Rect(c.x - s, c.y - s * 0.7f, c.x + s, c.y + s))
            ruta.addRect(Rect(c.x - s * 0.78f, c.y - s * 0.95f, c.x + s * 0.78f, c.y - s * 0.7f))
            ruta.addRect(Rect(c.x - s * 0.56f, c.y - s * 1.18f, c.x + s * 0.56f, c.y - s * 0.95f))
            ruta.addRect(Rect(c.x - s * 0.34f, c.y - s * 1.4f, c.x + s * 0.34f, c.y - s * 1.18f))
            ruta.addRect(Rect(c.x - s * 0.05f, c.y - s * 1.7f, c.x + s * 0.05f, c.y - s * 1.4f))
        }
        BarridoTema.HORIZONTE -> {
            val s = maxOf(c.y, h - c.y) * p * 1.02f
            ruta.addRect(Rect(0f, c.y - s, w, c.y + s))
        }
        BarridoTema.ABANICO -> {
            if (p >= 0.995f) ruta.addRect(Rect(0f, 0f, w, h))
            else if (p > 0f) { ruta.moveTo(c.x, c.y); ruta.arcTo(Rect(c, r0 * 1.05f), -90f, 360f * p, false); ruta.close() }
        }
        BarridoTema.SELLO -> {
            // El sello hanko que se estampa: un cuadrado de esquinas redondeadas que crece desde el toque.
            val s = r * 1.15f
            ruta.addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(c.x - s, c.y - s, c.x + s, c.y + s), androidx.compose.ui.geometry.CornerRadius(s * 0.3f)))
        }
    }
    return ruta
}

@Composable
fun CapaTransicionTema() {
    val foto = TransicionTema.foto ?: return
    val vuelta = TransicionTema.vuelta
    val mz = matizDe(Estilo.tema)
    val p = remember(vuelta) { Animatable(0f) }
    val color = MaterialTheme.colorScheme.primary
    var origen by remember { androidx.compose.runtime.mutableStateOf(Offset.Zero) }
    LaunchedEffect(vuelta) {
        p.animateTo(1f, tween(mz.ms, easing = mz.curva))
        if (TransicionTema.vuelta == vuelta) TransicionTema.foto = null
    }
    Canvas(Modifier.fillMaxSize().onGloballyPositioned { origen = it.positionInWindow() }) {
        val c = TransicionTema.centro - origen
        val r = listOf(Offset(0f, 0f), Offset(size.width, 0f), Offset(0f, size.height), Offset(size.width, size.height)).maxOf { (it - c).getDistance() }
        val agujero = agujeroTema(mz.barrido, c, r, p.value, size.width, size.height, 1.dp.toPx())
        clipPath(agujero, androidx.compose.ui.graphics.ClipOp.Difference) {
            drawImage(foto, topLeft = -origen)
        }
        // El borde de la forma con el color del tema nuevo (fino en las formas hechas de muchas piezas).
        val grosor = when (mz.barrido) { BarridoTema.PIXELES, BarridoTema.COLUMNAS, BarridoTema.BANDAS -> 1.5f; else -> 6f }
        drawPath(agujero, color.copy(alpha = 0.9f * (1f - p.value)), style = Stroke(grosor.dp.toPx() * (1f - p.value * 0.6f)))
    }
}
