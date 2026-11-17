package io.github.ruddy2003.vestigium.ui.escenas

import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.github.ruddy2003.vestigium.data.CuentosLeidos
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Gato
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ahorroDeEnergia
import io.github.ruddy2003.vestigium.ui.galeria.BotonMeGusta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// =====================================================================
//  El cuadro a pantalla completa y el cuadro del cuento (parte 8, encargo B; `docs/diseno/CUADROS_PLAN.md`)
// =====================================================================
//
// - **A pantalla completa** ([CuadroAmpliado]): fondo negro, sin barras del sistema, el cuadro centrado a todo lo que
//   quepa (4:3; en horizontal ocupa el alto entero), con su gato y sus caricias, la parte viva animada y el «me gusta»
//   de la galería en una esquina. Sale con un toque fuera del gato (en el cuadro o en lo negro), con la ✕ o con «atrás».
//   Va en una ventana propia encima de la pantalla en la que estás: así la pantalla del cuento sigue debajo (con su
//   ambiente y su lugar de lectura) y Hoy no necesita una ruta para abrirla. La ruta `cuadro?tema=…&dia=…`
//   ([PantallaCuadroCompleto]) abre lo mismo.
// - **El cuadro del cuento** ([CuadroDelCuento]): arriba de la pantalla del cuento, con su gato, el «me gusta» y el
//   botón del ambiente. Un toque fuera del gato lo amplía.
//
// Nada de esto se guarda (las caricias tampoco: D.190). Lo guarda, si acaso, el «me gusta» (encargo C).

/** El último cuadro que se pidió abrir por la ruta ([rutaCuadro]): así sale el mismo personaje (en Ciudad neón hay dos). */
private object CuadroPedido {
    @Volatile var ultimo: CuadroHoy? = null
    fun de(tema: String, dia: String): CuadroHoy? = ultimo?.takeIf { it.tema.id == tema && it.ficha.dia == dia }
}

/** La ruta del cuadro [c] a pantalla completa (`cuadro?tema=<id>&dia=<AAAA-MM-DD>`); recuerda el cuadro para no calcularlo otra vez. */
fun rutaCuadro(c: CuadroHoy): String {
    CuadroPedido.ultimo = c
    return "cuadro?tema=${c.tema.id}&dia=${c.ficha.dia}"
}

/**
 * El cuadro del tema [tema] (su id, `TemaId.id`) para el día lógico [dia], a pantalla completa (ruta
 * `cuadro?tema=<id>&dia=<AAAA-MM-DD>`), con su gato y el «me gusta» de la galería (`ui.galeria.BotonMeGusta`). Un día
 * futuro no se abre. El cuadro es el que se pidió con [rutaCuadro] o, si no, el de ese día del tema (`cuadroDeHoy`).
 * Al cerrarlo se vuelve atrás.
 */
@Composable
fun PantallaCuadroCompleto(nav: Nav, tema: String, dia: String) {
    val futuro = !CuentosLeidos.sePuedeLeer(dia, Fechas.diaLogico())
    val t = TemaId.entries.firstOrNull { it.id == tema }
    // null: todavía se calcula; Pair(null, true): no hay cuadro.
    val buscado by produceState<Pair<CuadroHoy?, Boolean>?>(CuadroPedido.de(tema, dia)?.let { it to true }, tema, dia) {
        if (value != null) return@produceState
        value = if (futuro || t == null) null to true
        else withContext(Dispatchers.Default) { runCatching { cuadroDeHoy(t, dia) }.getOrNull() } to true
    }
    val c = buscado?.first
    when {
        buscado == null -> Box(Modifier.fillMaxSize().background(Color.Black))
        c == null -> Hoja("El cuadro", nav.atras) {
            Ayuda(if (futuro) "Ese día todavía no llega: su cuadro no se puede ver antes." else "Este tema no tiene cuadro, o ese día no lo hay.", Modifier.padding(top = 12.dp))
        }
        else -> {
            Box(Modifier.fillMaxSize().background(Color.Black))
            CuadroAmpliado(c, horaDeAhora(), alSalir = nav.atras)
        }
    }
}

/**
 * El cuadro [cuadro] a la [hora] a pantalla completa, en su propia ventana (encima de todo, sin barras del sistema).
 * [alSalir] la cierra: un toque fuera del gato, la ✕ o «atrás».
 */
@Composable
internal fun CuadroAmpliado(cuadro: CuadroHoy, hora: Double, alSalir: () -> Unit) {
    Dialog(onDismissRequest = alSalir, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        val vista = LocalView.current
        DisposableEffect(vista) {
            (vista.parent as? DialogWindowProvider)?.window?.let { ventana ->
                runCatching {
                    ventana.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    ventana.attributes = ventana.attributes.also { it.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES }
                    WindowCompat.getInsetsController(ventana, vista).apply {
                        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        hide(WindowInsetsCompat.Type.systemBars())
                    }
                }
            }
            onDispose { }
        }
        ContenidoCuadroCompleto(cuadro, hora, alSalir)
    }
}

/**
 * Lo que va dentro de la pantalla completa (sin la ventana, para las capturas). [humor] y [animar] los fijan las
 * capturas; en la app salen del ánimo del gato y de los ajustes (animaciones, ahorro de energía).
 */
@Composable
internal fun ContenidoCuadroCompleto(cuadro: CuadroHoy, hora: Double, alSalir: () -> Unit, humor: Gato.Humor? = null, animar: Boolean? = null, pasoGato: PasoGato? = null) {
    val ctx = LocalContext.current
    val enVivo = !LocalInspectionMode.current
    val animo = humor ?: remember { Gato.humor() }
    val seAnima = animar ?: (enVivo && Repo.config.animaciones && !ahorroDeEnergia(ctx))
    var caricias by remember { mutableIntStateOf(0) }
    val quien = cuadro.quien
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        // Todo lo negro y el cuadro: un toque que no fue caricia sale.
        BoxWithConstraints(Modifier.fillMaxSize().toqueSinCaricia({ caricias }, alSalir), contentAlignment = Alignment.Center) {
            val ancho = if (maxWidth * 3f / 4f <= maxHeight) maxWidth else maxHeight * 4f / 3f
            VistaEscena(
                cuadro.escena, cuadro.ficha, hora, Modifier.width(ancho), animar = seAnima, humor = animo, pelaje = cuadro.pelaje, pasoGato = pasoGato,
                toques = true, alAcariciar = { caricias++; acariciar(ctx) },
                descripcion = "El cuadro de ${quien.nombre}, ${quien.lugar}, a pantalla completa.",
                acciones = listOf("Cerrar" to alSalir), enSegundoPlano = enVivo, fondo = Color.Black,
            )
        }
        // La ✕, arriba a la izquierda; el «me gusta», arriba a la derecha (lo dibuja la galería).
        BotonRedondo("Cerrar", Modifier.align(Alignment.TopStart).displayCutoutPadding().padding(12.dp), alSalir) { cruz(it) }
        BotonMeGusta(cuadro, hora, Modifier.align(Alignment.TopEnd).displayCutoutPadding().padding(12.dp))
        Text("${quien.nombre} · ${quien.lugar} · ${LecturaCuento.fecha(cuadro.ficha.dia, Fechas.diaLogico())}",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.6.sp), color = Color.White.copy(alpha = 0.55f),
            modifier = Modifier.align(Alignment.BottomCenter).displayCutoutPadding().padding(12.dp).clearAndSetSemantics { })
    }
}

/**
 * El cuadro arriba de la pantalla del cuento: [cuadro] a la [hora], con su gato (las caricias vibran y ronronean), el
 * «me gusta» arriba a la derecha y, abajo, el botón del ambiente (si su tema tiene, [conAmbiente]) y el de ampliar. Un
 * toque fuera del gato también lo amplía. Mientras está fuera de la vista no traza nada.
 */
@Composable
internal fun CuadroDelCuento(cuadro: CuadroHoy, hora: Double, modifier: Modifier = Modifier, conAmbiente: Boolean = false,
                             humor: Gato.Humor? = null, animar: Boolean? = null, pasoGato: PasoGato? = null) {
    val ctx = LocalContext.current
    val enVivo = !LocalInspectionMode.current
    val cs = MaterialTheme.colorScheme
    val forma = MaterialTheme.shapes.medium
    val animo = humor ?: remember(Repo.dias, Repo.salud, Repo.momentos) { Gato.humor() }
    val seAnima = animar ?: (enVivo && Repo.config.animaciones && !ahorroDeEnergia(ctx))
    var seVe by remember { mutableStateOf(true) }
    var caricias by remember { mutableIntStateOf(0) }
    var ampliado by remember(cuadro) { mutableStateOf(false) }
    val ampliar = { ampliado = true }
    val quien = cuadro.quien
    Box(modifier.fillMaxWidth()) {
        VistaEscena(
            cuadro.escena, cuadro.ficha, hora,
            Modifier.fillMaxWidth().onGloballyPositioned { donde -> val r = donde.boundsInWindow(); seVe = r.width > 1f && r.height > 1f }
                .toqueSinCaricia({ caricias }, ampliar),
            forma, animar = seAnima, humor = animo, pelaje = cuadro.pelaje, pasoGato = pasoGato, pausa = !seVe,
            toques = true, alAcariciar = { caricias++; acariciar(ctx) },
            descripcion = "El cuadro de ${quien.nombre}: ${quien.lugar}." + if (cuadro.especial) " Es un día especial en su vida." else "",
            acciones = listOf("Ver el cuadro a pantalla completa" to ampliar),
            enSegundoPlano = enVivo, fondo = cs.surfaceVariant.copy(alpha = 0.35f),
        )
        BotonMeGusta(cuadro, hora, Modifier.align(Alignment.TopEnd).padding(8.dp))
        if (conAmbiente) {
            val encendido = Repo.config.ambiente_cuadro
            BotonRedondo(if (encendido) "Apagar el ambiente del cuadro" else "Encender el ambiente del cuadro", Modifier.align(Alignment.BottomStart).padding(8.dp),
                { runCatching { Repo.cambiarConfig { it.copy(ambiente_cuadro = !it.ambiente_cuadro) } } }) { altavoz(it, encendido) }
        }
        BotonAmpliar(ampliar, Modifier.align(Alignment.BottomEnd).padding(8.dp))
    }
    if (ampliado) CuadroAmpliado(cuadro, hora) { ampliado = false }
}

/** El sitio del cuadro del cuento mientras se calcula: del mismo tamaño, para que la pantalla no salte cuando llega. */
@Composable
internal fun SitioDelCuadro(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().aspectRatio(4f / 3f).clip(MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)))
}

/** Si el personaje [vida] tiene cuadro, sin calcularlo: su tema tiene escena y la escena lo pinta (para reservarle el sitio). */
internal fun tieneCuadro(vida: String): Boolean {
    val tema = OtrasVidas.porId(vida)?.tema ?: return false
    val t = TemaId.entries.firstOrNull { it.id == tema } ?: return false
    return escenaDe(t)?.let { vida in it.vidas } == true
}

/** Una caricia al gato: la vibración corta de siempre y el ronroneo (con sonidos encendidos). No se guarda (D.190). */
internal fun acariciar(ctx: android.content.Context) {
    Sentidos.vibrarEvento(ctx, Sentidos.Evento.TOQUE)
}

// ---------------------------------------------------------------- el toque que no es caricia

/**
 * Un toque corto sobre esto que **no** fue una caricia al gato llama a [alTocar]. Mira el gesto sin quitárselo a nadie
 * (en la pasada inicial y en la final de Compose): el detector del cuadro ([gestosDelCuadro], dentro de [VistaEscena])
 * sigue recibiendo todo y decide si el toque cayó sobre el gato. Un instante después de soltar se mira si el contador de
 * [caricias] cambió: si no, fue un toque fuera del gato. No cuenta si el dedo se movió más de [HOLGURA_DEDO_DP] dp
 * (desplazar la pantalla) o si entró otro dedo. Los botones encima (✕, «me gusta») se llevan su toque y no llegan aquí.
 */
@Composable
internal fun Modifier.toqueSinCaricia(caricias: () -> Int, alTocar: () -> Unit): Modifier {
    val cuenta by rememberUpdatedState(caricias)
    val tocar by rememberUpdatedState(alTocar)
    return this.pointerInput(Unit) {
        val holgura = HOLGURA_DEDO_DP.dp.toPx()
        coroutineScope {
            awaitEachGesture {
                val abajo = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                val antes = cuenta()
                var vale = true
                while (true) {
                    val ev = awaitPointerEvent(PointerEventPass.Final)
                    val c = ev.changes.firstOrNull { it.id == abajo.id }
                    if (c == null || ev.changes.size > 1) { vale = false; break }
                    if ((c.position - abajo.position).getDistance() > holgura) vale = false
                    if (!c.pressed) break
                }
                if (vale) launch { delay(80); if (cuenta() == antes) tocar() }
            }
        }
    }
}

// ---------------------------------------------------------------- botones pequeños sobre el cuadro

/** El botón de ampliar, sobre la esquina de abajo del cuadro: cuatro esquinas que se abren. */
@Composable
internal fun BotonAmpliar(alAmpliar: () -> Unit, modifier: Modifier = Modifier) {
    BotonRedondo("Ver el cuadro a pantalla completa", modifier, alAmpliar) { esquinas(it) }
}

/** Un botón redondo y pequeño (34 dp) sobre el dibujo: un disco oscuro translúcido con su figura en blanco. */
@Composable
private fun BotonRedondo(nombre: String, modifier: Modifier, alTocar: () -> Unit, figura: DrawScope.(Color) -> Unit) {
    Canvas(
        modifier.size(34.dp).clip(CircleShape).clickable(onClickLabel = nombre, role = Role.Button, onClick = alTocar)
            .semantics { contentDescription = nombre },
    ) {
        drawCircle(Color.Black.copy(alpha = 0.42f))
        drawCircle(Color.White.copy(alpha = 0.28f), style = Stroke(1.dp.toPx()))
        figura(Color.White.copy(alpha = 0.92f))
    }
}

/** Cuatro esquinas que apuntan hacia fuera. */
private fun DrawScope.esquinas(tinta: Color) {
    val u = size.minDimension / 34f
    val trazo = Stroke(1.8f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val a = 10f * u; val b = 15f * u; val l = 4.5f * u
    val c = center
    for ((sx, sy) in listOf(-1f to -1f, 1f to -1f, -1f to 1f, 1f to 1f)) {
        val x = c.x + sx * b; val y = c.y + sy * b
        val p = Path().apply {
            moveTo(x, y - sy * l); lineTo(x, y); lineTo(x - sx * l, y)
        }
        drawPath(p, tinta, style = trazo)
        drawLine(tinta, Offset(x, y), Offset(c.x + sx * a * 0.55f, c.y + sy * a * 0.55f), 1.4f * u, cap = StrokeCap.Round)
    }
}

/** La ✕ de cerrar. */
private fun DrawScope.cruz(tinta: Color) {
    val u = size.minDimension / 34f
    val d = 7f * u
    drawLine(tinta, Offset(center.x - d, center.y - d), Offset(center.x + d, center.y + d), 2f * u, cap = StrokeCap.Round)
    drawLine(tinta, Offset(center.x + d, center.y - d), Offset(center.x - d, center.y + d), 2f * u, cap = StrokeCap.Round)
}

/** Un altavoz con sus ondas (encendido) o tachado (apagado). */
private fun DrawScope.altavoz(tinta: Color, encendido: Boolean) {
    val u = size.minDimension / 34f
    val x0 = center.x - 8f * u; val y = center.y
    val cuerpo = Path().apply {
        moveTo(x0, y - 3.5f * u); lineTo(x0 + 4f * u, y - 3.5f * u); lineTo(x0 + 9f * u, y - 8f * u)
        lineTo(x0 + 9f * u, y + 8f * u); lineTo(x0 + 4f * u, y + 3.5f * u); lineTo(x0, y + 3.5f * u); close()
    }
    drawPath(cuerpo, tinta)
    val trazo = Stroke(1.6f * u, cap = StrokeCap.Round)
    if (encendido) {
        for (k in 1..2) {
            // Centradas en la boca del altavoz: solo se ve el cuarto de vuelta de la derecha.
            val r = (4f + 4f * k) * u
            drawArc(tinta, -45f, 90f, false, Offset(x0 + 9f * u - r, y - r), androidx.compose.ui.geometry.Size(r * 2, r * 2), style = trazo)
        }
    } else {
        drawLine(tinta, Offset(x0 + 12f * u, y - 5f * u), Offset(x0 + 20f * u, y + 5f * u), 1.8f * u, cap = StrokeCap.Round)
        drawLine(tinta, Offset(x0 + 20f * u, y - 5f * u), Offset(x0 + 12f * u, y + 5f * u), 1.8f * u, cap = StrokeCap.Round)
    }
}
