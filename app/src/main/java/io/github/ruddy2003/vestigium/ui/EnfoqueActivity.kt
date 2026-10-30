@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Enfoque
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.PlanEnfoque
import io.github.ruddy2003.vestigium.data.Repo
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El reloj del enfoque a pantalla completa: se planea (en qué, cuánto tiempo, cuántos descansos y de cuánto) y luego
 * se ve la cuenta regresiva grande con el anillo del bloque, la tira de bloques y descansos, y los botones de pausar,
 * saltar el descanso y terminar. La pantalla no se apaga mientras está abierta.
 */
class EnfoqueActivity : ComponentActivity() {
    companion object { const val SIESTA = "siesta" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Repo.init(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= 27) setShowWhenLocked(true)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        val siesta = intent.getBooleanExtra(SIESTA, false)
        setContent { TemaVestigium { RelojEnfoque(siesta) { finish() } } }
    }
}

private fun reloj(ms: Long): String {
    val s = (ms / 1000).coerceAtLeast(0)
    return if (s >= 3600) "%d:%02d:%02d".format(s / 3600, (s / 60) % 60, s % 60) else "%02d:%02d".format(s / 60, s % 60)
}

private fun hm(min: Int) = if (min < 60) "$min min" else "${min / 60} h" + if (min % 60 > 0) " ${min % 60}" else ""

@Composable
fun RelojEnfoque(siestaInicial: Boolean, cerrar: () -> Unit) {
    val ctx = LocalContext.current
    val plan = Enfoque.plan(Repo.config)
    var ultima by remember { mutableStateOf<PlanEnfoque?>(null) }
    LaunchedEffect(plan) { if (plan != null) ultima = plan }
    val ahora by produceState(System.currentTimeMillis()) { while (true) { kotlinx.coroutines.delay(250); value = System.currentTimeMillis() } }
    // Si la alarma se atrasa, la pantalla avanza sola al terminar el segmento.
    LaunchedEffect(ahora / 1000) { plan?.let { p -> if (p.seg_inicio != null && Enfoque.restante(p, ahora) == 0L) Enfoque.avanzar(ctx) } }
    val cs = MaterialTheme.colorScheme
    Box(Modifier.fillMaxSize().background(cs.background).fondoTema(Estilo.tema, cs).safeDrawingPadding()) {
        when {
            plan != null -> Corriendo(plan, ahora, cerrar)
            ultima != null -> Terminado(ultima!!, cerrar)
            else -> Planear(siestaInicial, cerrar)
        }
    }
}

/**
 * La esfera del segmento: se llena según lo que va, del color del enfoque, del descanso o de la pausa. Cada tema tiene su
 * propio instrumento (corona de laurel, reloj de arena, astrolabio, esfera de radar, rosetón…); ver [esferaEnfoque].
 */
@Composable
private fun Anillo(progreso: Float, descanso: Boolean, pausa: Boolean, modifier: Modifier = Modifier, contenido: @Composable () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val tema = Estilo.tema
    val color = when { pausa -> cs.outline; descanso -> cs.tertiary; else -> cs.primary }
    Box(modifier.aspectRatio(1f), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) { esferaEnfoque(tema, progreso.coerceIn(0f, 1f), color, cs) }
        contenido()
    }
}

/** La tira de la sesión: una pieza por bloque (enfoque) o descanso, del largo de su duración, con la forma del tema. */
@Composable
private fun TiraSesion(p: PlanEnfoque, ahora: Long, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val tema = Estilo.tema
    val segs = Enfoque.segmentos(p)
    if (segs.size < 2) return
    val total = segs.sumOf { it.ms ?: 0 }.toFloat().coerceAtLeast(1f)
    val va = Enfoque.transcurrido(p, ahora)
    Canvas(modifier.fillMaxWidth().height(18.dp)) {
        var x = 0f
        val hueco = 3.dp.toPx()
        segs.forEachIndexed { i, s ->
            val w = (s.ms ?: 0) / total * size.width - hueco
            val color = if (s.enfoque) cs.primary else cs.tertiary
            val alto = if (s.enfoque) size.height else size.height * 0.5f
            val y = (size.height - alto) / 2
            if (w > 1f) {
                val pieza = piezaTira(tema, x, y, w, alto, 1.dp.toPx())
                drawPath(pieza, color.copy(alpha = 0.22f))
                val lleno = when { i < p.segmento -> 1f; i == p.segmento -> (va.toFloat() / (s.ms ?: 1L)).coerceIn(0f, 1f); else -> 0f }
                if (lleno > 0f) clipRect(left = x - 1f, top = 0f, right = x + w * lleno, bottom = size.height) { drawPath(pieza, color) }
            }
            x += w + hueco
        }
    }
}

@Composable
private fun Corriendo(p: PlanEnfoque, ahora: Long, cerrar: () -> Unit) {
    val ctx = LocalContext.current
    val cs = MaterialTheme.colorScheme
    val sentir = rememberSentir()
    var terminando by remember { mutableStateOf(false) }
    val segs = Enfoque.segmentos(p)
    val seg = segs.getOrNull(p.segmento)
    val descanso = Enfoque.enDescanso(p)
    val pausa = Enfoque.enPausa(p)
    val rest = Enfoque.restante(p, ahora)
    val nombre = if (p.tipo == "siesta") "Siesta" else Catalogo.nombreTramo(p.tipo)
    val n = Enfoque.bloques(p)
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = cerrar) { Text("✕", style = MaterialTheme.typography.titleLarge, color = cs.onSurfaceVariant) }
            Text(Estilo.pantalla(nombre, Romano.latin(nombre)).first, style = MaterialTheme.typography.titleLarge, color = cs.primary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Spacer(Modifier.widthIn(min = 48.dp))
        }
        Text(when {
            p.esperando -> "descanso terminado"
            descanso -> "descanso · luego bloque ${Enfoque.numeroBloque(p) + 1} de $n"
            n > 1 -> "bloque ${Enfoque.numeroBloque(p)} de $n"
            p.meta_min != null && p.tipo == "siesta" -> "te despierto a las " + Fechas.horaDe(Fechas.isoDeMillis(ahora + (rest ?: 0)))
            p.meta_min != null -> "meta: ${hm(p.meta_min)}"
            else -> "cronómetro libre"
        }, style = MaterialTheme.typography.labelLarge, color = cs.onSurfaceVariant)
        Spacer(Modifier.weight(1f))
        val progreso = if (p.esperando) 1f else seg?.ms?.let { 1f - (rest ?: 0).toFloat() / it } ?: ((Enfoque.transcurrido(p, ahora) % 3_600_000L) / 3_600_000f)
        Anillo(progreso, descanso, pausa || p.esperando, Modifier.fillMaxWidth(0.86f).widthIn(max = 420.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (p.esperando) {
                    Text("¿Listo para el", style = MaterialTheme.typography.titleMedium, color = cs.onSurfaceVariant)
                    Text("bloque ${Enfoque.numeroBloque(p)} de $n?", style = MaterialTheme.typography.headlineMedium, color = cs.primary)
                } else {
                    // La letra grande del tema (displayLarge); con horas («1:05:00») se achica para que quepa en la esfera.
                    val cuenta = reloj(rest ?: Enfoque.transcurrido(p, ahora))
                    val grande = MaterialTheme.typography.displayLarge
                    Text(cuenta, style = if (cuenta.length > 5) grande.copy(fontSize = grande.fontSize * 0.74f) else grande,
                        color = if (pausa) cs.onSurfaceVariant else cs.onBackground, maxLines = 1, softWrap = false)
                    Text(when { pausa -> "en pausa"; descanso -> "de descanso"; rest != null -> "quedan"; else -> "llevas" }, style = MaterialTheme.typography.labelLarge, color = cs.onSurfaceVariant)
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        TiraSesion(p, ahora, Modifier.padding(horizontal = 8.dp))
        val fin = Enfoque.finEstimado(p, ahora)
        Text("enfoque en esta sesión: " + reloj(Enfoque.enfoqueTotal(p, ahora)).let { if (it.length == 5) "${it.substringBefore(':').toInt()} min" else it } +
            (fin?.takeIf { !pausa && !p.esperando }?.let { " · terminas a las " + Fechas.horaDe(Fechas.isoDeMillis(it)) } ?: ""),
            style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant, modifier = Modifier.padding(top = 10.dp), textAlign = TextAlign.Center)
        Spacer(Modifier.weight(1f))
        if (p.esperando) BotonPrincipal("Empezar", Modifier.fillMaxWidth().height(60.dp)) { sentir(Sentidos.Evento.CAMBIO); Enfoque.reanudar(ctx) }
        else Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (descanso && !pausa) BotonSecundario("Saltar descanso", Modifier.weight(1f).height(56.dp)) { Enfoque.saltarDescanso(ctx) }
            else BotonSecundario(if (pausa) "Reanudar" else "Pausar", Modifier.weight(1f).height(56.dp)) {
                sentir(Sentidos.Evento.TOQUE); if (pausa) Enfoque.reanudar(ctx) else Enfoque.pausar(ctx)
            }
            if (descanso && !pausa) BotonSecundario("Pausar", Modifier.weight(1f).height(56.dp)) { Enfoque.pausar(ctx) }
        }
        TextButton(onClick = { terminando = true }, modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)) {
            Text("terminar ahora", style = MaterialTheme.typography.labelLarge, color = cs.error)
        }
    }
    if (terminando) {
        var nota by remember { mutableStateOf("") }
        val min = (Enfoque.enfoqueTotal(p) / 60_000).toInt()
        Dialogo(onDismissRequest = { terminando = false },
            title = { Text(if (p.tipo == "siesta") "¿Terminar la siesta?" else "¿Terminar ya?") },
            text = {
                Column {
                    Text(if (min >= 1) "Se guardan ${hm(min)}" + (if (p.tipo == "siesta") " de siesta." else " de enfoque.") else "Va menos de un minuto: no se guarda nada.", style = MaterialTheme.typography.bodyMedium)
                    if (p.tipo != "siesta") OutlinedTextField(nota, { nota = it }, Modifier.fillMaxWidth().padding(top = 10.dp), singleLine = true,
                        placeholder = { Text("Qué hiciste (opcional): #tesis, capítulo 2…") }, visualTransformation = rememberMarcas())
                }
            },
            confirmButton = { TextButton(onClick = { Enfoque.terminar(ctx, nota); terminando = false; cerrar() }) { Text("Terminar y guardar") } },
            dismissButton = { TextButton(onClick = { terminando = false }) { Text("seguir") } })
    }
}

@Composable
private fun Terminado(p: PlanEnfoque, cerrar: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    var nota by remember { mutableStateOf("") }
    val min = Math.round(Repo.tramos.filter { it.id.startsWith("enf${p.inicio}_") }.sumOf { it.min }.toDouble()).toInt()
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(if (p.tipo == "siesta") "Hora de despertar" else "¡Terminaste!", style = MaterialTheme.typography.headlineMedium, color = cs.primary)
        Text(hm(min) + if (p.tipo == "siesta") " de siesta" else " de ${Catalogo.nombreTramo(p.tipo).lowercase()}" + (if (Enfoque.bloques(p) > 1) " en ${Enfoque.bloques(p)} bloques" else ""),
            style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 6.dp))
        if (p.tipo != "siesta") OutlinedTextField(nota, { nota = it }, Modifier.fillMaxWidth().padding(top = 18.dp), singleLine = true,
            placeholder = { Text("Qué hiciste (opcional)") }, visualTransformation = rememberMarcas())
        BotonPrincipal("Listo", Modifier.fillMaxWidth().padding(top = 16.dp)) { if (nota.isNotBlank()) Enfoque.anotarSesion(p.inicio, nota); cerrar() }
    }
}

@Composable
private fun Planear(siestaInicial: Boolean, cerrar: () -> Unit) {
    val ctx = LocalContext.current
    val cs = MaterialTheme.colorScheme
    val u = Repo.config.enfoque_ultimo
    var tipo by remember { mutableStateOf(if (siestaInicial) "siesta" else u?.tipo?.takeIf { it != "siesta" } ?: "estudio") }
    var libre by remember { mutableStateOf(u != null && u.meta_min == null) }
    var meta by remember { mutableStateOf<Int?>(u?.meta_min ?: 120) }
    var descansos by remember { mutableStateOf(u?.descansos ?: 3) }
    var largo by remember { mutableStateOf(u?.descanso_min ?: 10) }
    var alarma by remember { mutableStateOf<Int?>(25) }
    var pasado by remember { mutableStateOf(false) }
    val siesta = tipo == "siesta"
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = cerrar) { Text("✕", style = MaterialTheme.typography.titleLarge, color = cs.onSurfaceVariant) }
            Text(Estilo.pantalla(if (siesta) "Siesta" else "Enfoque", null).first, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
        }
        Etiqueta("¿En qué?")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            (Catalogo.ENFOQUE + Catalogo.SIESTA).forEach { o -> Ficha(o.nombre, tipo == o.id) { tipo = o.id } }
        }
        if (siesta) {
            Etiqueta("¿Te despierto?")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(15, 20, 25, 30, 45, 60, 90).forEach { m -> Ficha("en ${hm(m)}", alarma == m) { alarma = m } }
                Ficha("sin alarma", alarma == null) { alarma = null }
            }
            Ayuda(alarma?.let { "Suena a las " + Fechas.horaDe(Fechas.isoDeMillis(System.currentTimeMillis() + it * 60_000L)) + "." } ?: "Cuenta hasta que la termines.", Modifier.padding(top = 6.dp))
        } else {
            Etiqueta("¿Cuánto tiempo de enfoque?")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Ficha("libre, sin meta", libre) { libre = true }
                listOf(25, 45, 60, 90, 120, 180).forEach { m -> Ficha(hm(m), !libre && meta == m) { libre = false; meta = m } }
            }
            if (!libre) {
                SelectorDuracion(meta?.takeIf { it !in listOf(25, 45, 60, 90, 120, 180) }, emptyList()) { meta = it ?: meta }
                Etiqueta("¿Cuántos descansos?")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    (0..6).forEach { k -> Ficha(if (k == 0) "ninguno" else "$k", descansos == k) { descansos = k } }
                }
                if (descansos > 0) {
                    Etiqueta("¿De cuánto cada uno?")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(5, 10, 15, 20, 30).forEach { m -> Ficha("$m min", largo == m) { largo = m } }
                    }
                }
                val m = meta ?: 0
                if (m > 0) {
                    val prueba = PlanEnfoque(tipo, m, descansos, largo)
                    val k = Enfoque.bloques(prueba)
                    val bloque = m / k
                    val total = m + descansos * largo
                    Spacer(Modifier.height(14.dp))
                    TiraSesion(prueba, 0L)
                    Ayuda((if (k > 1) "$k bloques de ${hm(bloque)}" + (if (m % k != 0) " (aprox.)" else "") + " con $descansos descanso${if (descansos > 1) "s" else ""} de $largo min" else "Un bloque de ${hm(m)}") +
                        " · ${hm(total)} en total · terminas hacia las " + Fechas.horaDe(Fechas.isoDeMillis(System.currentTimeMillis() + total * 60_000L)) +
                        ". Al acabar cada descanso te pregunto antes de seguir.", Modifier.padding(top = 6.dp))
                }
            } else Ayuda("Cuenta hacia arriba hasta que lo termines. Puedes pausarlo.", Modifier.padding(top = 6.dp))
        }
        BotonPrincipal(if (siesta) "Empezar siesta" else "Empezar", Modifier.fillMaxWidth().padding(top = 22.dp).height(60.dp), habilitado = siesta || libre || (meta ?: 0) > 0) {
            if (siesta) Enfoque.iniciarPlan(ctx, "siesta", alarma, 0, 10)
            else Enfoque.iniciarPlan(ctx, tipo, if (libre) null else meta, if (libre) 0 else descansos, largo)
        }
        // Lo que ya pasó, para anotarlo sin cronómetro.
        TextButton(onClick = { pasado = !pasado }, modifier = Modifier.padding(top = 14.dp)) {
            Text((if (pasado) "− " else "+ ") + "¿ya pasó? anótalo", style = MaterialTheme.typography.labelLarge, color = cs.primary)
        }
        if (pasado) {
            var cuando by remember { mutableStateOf<java.time.LocalDateTime?>(java.time.LocalDateTime.now().withSecond(0).withNano(0).minusMinutes(30)) }
            var min by remember { mutableStateOf<Int?>(null) }
            Etiqueta("¿Cuándo empezó?")
            SelectorHora(cuando, atajos = listOf(30, 60, 90, 120, 180)) { cuando = it }
            Etiqueta("¿Cuánto duró?")
            SelectorDuracion(min, listOf(10, 15, 20, 30, 45, 60, 90, 120)) { min = it }
            BotonSecundario("Guardar", Modifier.padding(top = 10.dp), habilitado = (min ?: 0) > 0) {
                val t = Enfoque.agregarMs(tipo, (cuando ?: java.time.LocalDateTime.now()).aMillis(), min ?: 0)
                android.widget.Toast.makeText(ctx, if (t != null) "Guardado" else "No se pudo guardar.", android.widget.Toast.LENGTH_SHORT).show()
                if (t != null) cerrar()
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

// =====================================================================
//  La esfera y la tira, por tema.
// =====================================================================

/**
 * La esfera del reloj de Enfoque en el tema [t]: [p] (0 a 1) es lo que va del segmento y [color], el del enfoque, el
 * descanso o la pausa. El centro queda para la cuenta: los adornos van en el anillo o, si pasan por detrás de la cuenta,
 * muy tenues.
 */
private fun DrawScope.esferaEnfoque(t: TemaId, p: Float, color: Color, e: ColorScheme) {
    val c = center
    val R = size.minDimension / 2
    val u = 1.dp.toPx()
    val g = 14 * u
    val r = R - g
    val pista = e.outline.copy(alpha = 0.25f)
    val tenue = e.onSurfaceVariant
    /** El punto a la fracción [f] de la vuelta (0 arriba, en el sentido del reloj), a [radio] del centro. */
    fun en(f: Float, radio: Float): Offset {
        val a = Math.toRadians(f * 360.0 - 90.0)
        return Offset(c.x + radio * cos(a).toFloat(), c.y + radio * sin(a).toFloat())
    }
    /** Arco de [desde] a [hasta] (fracciones de vuelta), a [radio], con trazo [grosor]. */
    fun arco(col: Color, desde: Float, hasta: Float, radio: Float, grosor: Float, punta: StrokeCap = StrokeCap.Butt) {
        if (hasta <= desde) return
        drawArc(col, -90f + desde * 360f, (hasta - desde) * 360f, false, Offset(c.x - radio, c.y - radio), Size(radio * 2, radio * 2), style = Stroke(grosor, cap = punta))
    }
    /** ¿La pieza [k] de [n] ya quedó atrás? */
    fun pasado(k: Int, n: Int) = (k + 0.5f) / n <= p
    fun rombo(q: Offset, rx: Float, ry: Float, col: Color) =
        drawPath(Path().apply { moveTo(q.x, q.y - ry); lineTo(q.x + rx, q.y); lineTo(q.x, q.y + ry); lineTo(q.x - rx, q.y); close() }, col)
    when (t) {
        // Corona de laurel abierta arriba: las hojas se colorean conforme avanza; abajo, el nudo de la cinta.
        TemaId.ROMANO -> {
            drawCircle(pista, r, c, style = Stroke(1.5f * u))
            val n = 44
            for (k in 0 until n) {
                val f = 0.035f + 0.93f * k / (n - 1)
                val lado = if (k % 2 == 0) 1f else -1f
                val q = en(f, r + lado * 5.5f * u)
                rotate(f * 360f + lado * 28f, q) { drawOval(if (f <= p) color else pista, Offset(q.x - 7 * u, q.y - 2.6f * u), Size(14 * u, 5.2f * u)) }
            }
            val nudo = en(0.5f, r)
            drawLine(e.tertiary, nudo, Offset(c.x - 9 * u, c.y + r + 10 * u), 2.5f * u)
            drawLine(e.tertiary, nudo, Offset(c.x + 9 * u, c.y + r + 10 * u), 2.5f * u)
            drawCircle(e.tertiary, 4 * u, nudo)
        }
        // Terminal: una vuelta de celdas; las hechas, encendidas; el cursor en la que sigue; corchetes en las esquinas.
        TemaId.CIBER -> {
            val n = 48; val lado = 6.5f * u; val sigue = (p * n).toInt()
            for (k in 0 until n) {
                val f = (k + 0.5f) / n; val q = en(f, r); val o = Offset(q.x - lado / 2, q.y - lado / 2)
                rotate(f * 360f, q) {
                    if (pasado(k, n)) drawRect(color, o, Size(lado, lado))
                    else if (k == sigue) drawRect(e.secondary, o, Size(lado, lado))
                    else drawRect(pista, o, Size(lado, lado), style = Stroke(u))
                }
            }
            val l = 18 * u; val m = 2 * u; val col = color.copy(alpha = 0.7f); val w = size.width; val h = size.height
            listOf(Offset(m, m) to Offset(1f, 1f), Offset(w - m, m) to Offset(-1f, 1f), Offset(m, h - m) to Offset(1f, -1f), Offset(w - m, h - m) to Offset(-1f, -1f)).forEach { (o, s) ->
                drawLine(col, o, Offset(o.x + s.x * l, o.y), 1.5f * u)
                drawLine(col, o, Offset(o.x, o.y + s.y * l), 1.5f * u)
            }
        }
        // Un reloj de tubo de neón: el aro apagado, el tramo encendido con su halo y un foquito que corre por delante.
        TemaId.NEON -> {
            drawCircle(pista, r, c, style = Stroke(3 * u))
            val barrido = 360f * p
            drawArc(color.copy(alpha = 0.3f), -90f, barrido, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(12 * u, cap = StrokeCap.Round))
            drawArc(color, -90f, barrido, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(3.5f * u, cap = StrokeCap.Round))
            val q = en(p, r)
            drawCircle(e.tertiary.copy(alpha = 0.35f), 8 * u, q); drawCircle(e.tertiary, 3.5f * u, q)
        }
        // Sol de Vergina: dieciséis rayos que se encienden uno a uno alrededor de un doble aro.
        TemaId.HELENO -> {
            drawCircle(tenue.copy(alpha = 0.5f), r - 8 * u, c, style = Stroke(1.2f * u))
            drawCircle(tenue.copy(alpha = 0.3f), r - 12 * u, c, style = Stroke(0.8f * u))
            val n = 16; val base = 0.22f / n
            for (k in 0 until n) {
                val f = k.toFloat() / n
                val a = en(f - base, r - 6 * u); val b = en(f + base, r - 6 * u); val punta = en(f, R - 2 * u)
                drawPath(Path().apply { moveTo(a.x, a.y); lineTo(punta.x, punta.y); lineTo(b.x, b.y); close() }, if (pasado(k, n)) color else pista)
            }
            drawCircle(color, 3 * u, en(p, r - 8 * u))
        }
        // Las doce horas, cada una con su estrella de cinco puntas (como en los relojes estelares de los techos de las
        // tumbas), y la barca del sol que las recorre.
        TemaId.EGIPTO -> {
            drawCircle(tenue.copy(alpha = 0.45f), r - 8 * u, c, style = Stroke(1.2f * u))
            val n = 12
            for (k in 0 until n) {
                val q = en((k + 0.5f) / n, r - 2 * u); val s = 5.5f * u
                val estrella = Path().apply {
                    for (i in 0 until 10) {
                        val ang = -Math.PI / 2 + Math.PI * i / 5; val rr = if (i % 2 == 0) s else s * 0.4f
                        val px = q.x + (rr * cos(ang)).toFloat(); val py = q.y + (rr * sin(ang)).toFloat()
                        if (i == 0) moveTo(px, py) else lineTo(px, py)
                    }
                    close()
                }
                drawPath(estrella, if (pasado(k, n)) color else pista)
            }
            val b = en(p, r - 10 * u)
            drawPath(Path().apply { moveTo(b.x - 7 * u, b.y - 2 * u); quadraticTo(b.x, b.y + 5 * u, b.x + 7 * u, b.y - 2 * u); quadraticTo(b.x, b.y + 1.5f * u, b.x - 7 * u, b.y - 2 * u); close() }, color)
            drawCircle(e.secondary, 2.4f * u, Offset(b.x, b.y - 2.5f * u))
        }
        // Reloj de arena: la arena baja, tenue, por detrás de la cuenta; afuera, un aro de rúbrica con doce rombos de oro.
        TemaId.MEDIEVAL -> {
            drawCircle(pista, r, c, style = Stroke(1.2f * u))
            arco(color, 0f, p, r, 4 * u)
            for (k in 0 until 12) rombo(en(k / 12f, r + 8 * u), 2.2f * u, 3.4f * u, e.tertiary)
            val hw = r * 0.46f; val alto = r * 0.74f; val y0 = c.y - alto; val y1 = c.y + alto
            val vidrio = Path().apply {
                moveTo(c.x - hw, y0); lineTo(c.x + hw, y0)
                cubicTo(c.x + hw, c.y - alto * 0.35f, c.x + 5 * u, c.y - 9 * u, c.x + 3 * u, c.y)
                cubicTo(c.x + 5 * u, c.y + 9 * u, c.x + hw, c.y + alto * 0.35f, c.x + hw, y1)
                lineTo(c.x - hw, y1)
                cubicTo(c.x - hw, c.y + alto * 0.35f, c.x - 5 * u, c.y + 9 * u, c.x - 3 * u, c.y)
                cubicTo(c.x - 5 * u, c.y - 9 * u, c.x - hw, c.y - alto * 0.35f, c.x - hw, y0)
                close()
            }
            val arena = color.copy(alpha = 0.2f)
            clipPath(vidrio) {
                val arriba = (alto - 6 * u) * (1f - p)
                drawRect(arena, Offset(c.x - hw, c.y - arriba), Size(hw * 2, arriba))
                val abajo = (alto - 2 * u) * p
                drawRect(arena, Offset(c.x - hw, y1 - abajo), Size(hw * 2, abajo))
            }
            if (p < 1f) drawLine(arena, c, Offset(c.x, y1 - (alto - 2 * u) * p), 1.5f * u)
            drawPath(vidrio, tenue.copy(alpha = 0.3f), style = Stroke(1.2f * u))
            listOf(y0, y1).forEach { y -> drawRect(tenue.copy(alpha = 0.35f), Offset(c.x - hw - 6 * u, y - 2.5f * u), Size(hw * 2 + 12 * u, 5 * u)) }
        }
        // La vela de horas de los castillos: una vela marcada con doce rayas rojas que se consume conforme pasa el bloque;
        // alrededor, el anillo de acero con doce clavos, que se tiñe de rojo con lo recorrido.
        TemaId.TEMPLARIO -> {
            drawCircle(pista, r, c, style = Stroke(1.2f * u))
            arco(color, 0f, p, r, 4 * u)
            for (k in 0 until 12) drawCircle(if (pasado(k, 12)) color else e.secondary.copy(alpha = 0.6f), 2.2f * u, en(k / 12f, r + 8 * u))
            val hw = r * 0.16f; val base = c.y + r * 0.62f; val alto = r * 1.15f
            val arriba = base - alto * (1f - p).coerceIn(0.04f, 1f)
            drawRect(tenue.copy(alpha = 0.35f), Offset(c.x - r * 0.34f, base), Size(r * 0.68f, 4 * u))
            drawRect(Color(0xFFF2EBDA).copy(alpha = 0.9f), Offset(c.x - hw, arriba), Size(hw * 2, base - arriba))
            drawRect(tenue.copy(alpha = 0.4f), Offset(c.x - hw, arriba), Size(hw * 2, base - arriba), style = Stroke(1 * u))
            for (k in 1 until 12) { val y = base - alto * k / 12f; if (y > arriba) drawLine(e.primary, Offset(c.x - hw, y), Offset(c.x + hw, y), 1.4f * u) }
            if (p < 1f) {
                drawLine(tenue, Offset(c.x, arriba), Offset(c.x, arriba - 4 * u), 1.2f * u)
                drawPath(Path().apply { moveTo(c.x - 4 * u, arriba - 4 * u); quadraticTo(c.x - 4 * u, arriba - 12 * u, c.x, arriba - 18 * u); quadraticTo(c.x + 4 * u, arriba - 12 * u, c.x + 4 * u, arriba - 4 * u); close() }, Color(0xFFF0A040))
                drawCircle(Color(0xFFFFE2A8), 1.8f * u, Offset(c.x, arriba - 8 * u))
            }
        }
        // El ojo que vigila: el iris es el anillo, con sus estrías; los párpados lo cierran por arriba y por abajo.
        TemaId.DISTOPIA -> {
            arco(pista, 0f, 1f, r, g)
            arco(color, 0f, p, r, g)
            for (k in 0 until 72) { val f = k / 72f; drawLine(e.background.copy(alpha = 0.35f), en(f, r - g / 2), en(f, r + g / 2), if (k % 6 == 0) 2 * u else 0.8f * u) }
            val parpado = e.onBackground.copy(alpha = 0.7f)
            val p0 = Offset(c.x - R, c.y); val p1 = Offset(c.x, c.y - R * 1.85f); val p2 = Offset(c.x + R, c.y)
            drawPath(Path().apply { moveTo(p0.x, p0.y); quadraticTo(p1.x, p1.y, p2.x, p2.y); quadraticTo(c.x, c.y + R * 1.85f, p0.x, p0.y); close() }, parpado, style = Stroke(2.2f * u))
            for (k in -3..3) {
                val s = 0.5f + k * 0.09f
                val q = Offset((1 - s) * (1 - s) * p0.x + 2 * (1 - s) * s * p1.x + s * s * p2.x, (1 - s) * (1 - s) * p0.y + 2 * (1 - s) * s * p1.y + s * s * p2.y)
                val d = q - c; val l = d.getDistance().coerceAtLeast(1f)
                drawLine(parpado, q, q + d * (6 * u / l), 1.5f * u)
            }
        }
        // Medidor de aguja de tres cuartos de vuelta, con la zona roja al final y la aguja corta en la orilla.
        TemaId.REFUGIO -> {
            val ini = 135f; val barrido = 270f
            val o = Offset(c.x - r, c.y - r); val lado = Size(r * 2, r * 2)
            drawArc(pista, ini, barrido, false, o, lado, style = Stroke(g * 0.6f))
            drawArc(color, ini, barrido * p, false, o, lado, style = Stroke(g * 0.6f))
            val rr = r + g * 0.55f
            drawArc(e.error.copy(alpha = 0.6f), ini + barrido * 0.9f, barrido * 0.1f, false, Offset(c.x - rr, c.y - rr), Size(rr * 2, rr * 2), style = Stroke(2.5f * u))
            for (k in 0..27) {
                val a = Math.toRadians((ini + barrido * k / 27f).toDouble())
                val r1 = r - g * 0.45f; val r2 = r1 - (if (k % 3 == 0) 9 else 4) * u
                drawLine(tenue.copy(alpha = if (k % 3 == 0) 0.7f else 0.35f), Offset(c.x + r1 * cos(a).toFloat(), c.y + r1 * sin(a).toFloat()),
                    Offset(c.x + r2 * cos(a).toFloat(), c.y + r2 * sin(a).toFloat()), 1.5f * u)
            }
            val a = Math.toRadians((ini + barrido * p).toDouble())
            val dir = Offset(cos(a).toFloat(), sin(a).toFloat())
            val base = c + dir * (r - g * 1.2f)
            drawLine(e.onBackground, base, c + dir * (R - u), 3 * u, cap = StrokeCap.Round)
            drawCircle(e.secondary, 4 * u, base)
        }
        // Engranaje: los dientes se tiñen conforme avanza; dentro, un aro con seis pernos.
        TemaId.ORDEN -> {
            val n = 30; val r0 = r + u; val r1 = R - u; val ancho = 0.32f / n
            for (k in 0 until n) {
                val f = (k + 0.5f) / n
                val a = en(f - ancho, r0); val b = en(f - ancho * 0.6f, r1); val d = en(f + ancho * 0.6f, r1); val z = en(f + ancho, r0)
                drawPath(Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(d.x, d.y); lineTo(z.x, z.y); close() }, if (pasado(k, n)) color else pista)
            }
            drawCircle(pista, r - 3 * u, c, style = Stroke(8 * u))
            arco(color.copy(alpha = 0.5f), 0f, p, r - 3 * u, 8 * u)
            for (k in 0 until 6) drawCircle(e.secondary, 2.2f * u, en(k / 6f + 1f / 12f, r - 3 * u))
            drawCircle(tenue.copy(alpha = 0.4f), r - 10 * u, c, style = Stroke(u))
        }
        // Reloj de agua: un matraz redondo que se llena desde abajo, con su graduación y su cuello.
        TemaId.LABORATORIO -> {
            val rv = r + 4 * u; val dentro = rv - 2 * u
            val nivel = c.y + dentro * (1f - 2f * p)
            clipPath(Path().apply { addOval(Rect(c, dentro)) }) {
                val agua = Path().apply {
                    moveTo(c.x - rv, nivel)
                    var x = c.x - rv
                    while (x <= c.x + rv) { lineTo(x, nivel + 2.5f * u * sin((x - c.x) / (14 * u))); x += 3 * u }
                    lineTo(c.x + rv, c.y + rv); lineTo(c.x - rv, c.y + rv); close()
                }
                drawPath(agua, color.copy(alpha = 0.22f))
                drawCircle(color.copy(alpha = 0.35f), 2.5f * u, Offset(c.x + rv * 0.3f, nivel + 18 * u))
                drawCircle(color.copy(alpha = 0.3f), 1.6f * u, Offset(c.x - rv * 0.25f, nivel + 30 * u))
            }
            val vidrio = tenue.copy(alpha = 0.55f)
            drawCircle(vidrio, rv, c, style = Stroke(2 * u))
            for (k in 1..9) {
                val y = c.y + rv - 2f * rv * k / 10f
                val x = c.x - sqrt(rv * rv - (y - c.y) * (y - c.y)) + 4 * u
                drawLine(vidrio, Offset(x, y), Offset(x + (if (k % 5 == 0) 12 else 6) * u, y), u)
            }
            // El cuello del matraz, arriba.
            drawRect(e.background, Offset(c.x - 10 * u, c.y - rv - 2 * u), Size(20 * u, 5 * u))
            drawLine(vidrio, Offset(c.x - 10 * u, c.y - rv + 2 * u), Offset(c.x - 10 * u, c.y - R + u), 2 * u)
            drawLine(vidrio, Offset(c.x + 10 * u, c.y - rv + 2 * u), Offset(c.x + 10 * u, c.y - R + u), 2 * u)
            drawLine(vidrio, Offset(c.x - 14 * u, c.y - R + u), Offset(c.x + 14 * u, c.y - R + u), 2 * u)
        }
        // Cuadrante solar: las líneas de las horas en la orilla, lo ya pasado al sol, la sombra del gnomon marcando dónde va.
        TemaId.COLONOS -> {
            drawCircle(tenue.copy(alpha = 0.5f), R - 2 * u, c, style = Stroke(1.5f * u))
            drawCircle(tenue.copy(alpha = 0.35f), r - 6 * u, c, style = Stroke(0.8f * u))
            arco(color.copy(alpha = 0.3f), 0f, p, r + 2 * u, g)
            for (k in 0 until 24) {
                val f = k / 24f
                drawLine(tenue.copy(alpha = if (k % 3 == 0) 0.75f else 0.4f), en(f, r - 6 * u), en(f, R - 2 * u), if (k % 3 == 0) 1.8f * u else 0.9f * u)
            }
            drawPath(Path().apply { moveTo(c.x - 5 * u, c.y - R + 2 * u); lineTo(c.x + 5 * u, c.y - R + 2 * u); lineTo(c.x, c.y - r + 6 * u); close() }, e.onBackground.copy(alpha = 0.7f))
            drawLine(e.onBackground.copy(alpha = 0.6f), en(p, r - 10 * u), en(p, R - 2 * u), 4 * u, cap = StrokeCap.Round)
        }
        // Esfera guilloché: rayos finos, doble filete de oro, doce puntos y una estrella que marca la marcha.
        TemaId.NAPOLEON -> {
            for (k in 0 until 120) drawLine(tenue.copy(alpha = 0.18f), en(k / 120f, r - g * 0.5f), en(k / 120f, r + g * 0.5f), 0.7f * u)
            drawCircle(e.tertiary.copy(alpha = 0.7f), R - u, c, style = Stroke(u))
            drawCircle(e.tertiary.copy(alpha = 0.7f), R - 4 * u, c, style = Stroke(0.6f * u))
            arco(pista, 0f, 1f, r, 3 * u)
            arco(color, 0f, p, r, 3 * u)
            for (k in 0 until 12) drawCircle(e.tertiary, if (k % 3 == 0) 2.6f * u else 1.6f * u, en(k / 12f, r - g * 0.9f))
            estrellaEnfoque(en(p, r), 8 * u, e.tertiary)
        }
        // Reloj floral: doce pétalos que se abren uno a uno alrededor de un tallo punteado.
        TemaId.BOTANICO -> {
            drawCircle(e.primary.copy(alpha = 0.35f), r - g * 0.75f, c, style = Stroke(u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 3 * u))))
            val n = 12
            for (k in 0 until n) {
                val f = (k + 0.5f) / n; val q = en(f, r)
                val o = Offset(q.x - g * 0.9f, q.y - g * 0.42f); val lado = Size(g * 1.8f, g * 0.84f)
                rotate(f * 360f - 90f, q) {
                    drawOval(if (pasado(k, n)) color else pista, o, lado)
                    drawOval(tenue.copy(alpha = 0.4f), o, lado, style = Stroke(0.8f * u))
                }
                drawCircle(e.tertiary, 1.8f * u, en(f, r - g * 0.75f))
            }
        }
        TemaId.RUINAS -> {
            // Una corona de hiedra de 24 hojas que se van poniendo verdes conforme pasa el tiempo, sobre un tallo que da la vuelta.
            drawCircle(tenue.copy(alpha = 0.4f), r, c, style = Stroke(0.8f * u))
            val n = 24
            for (k in 0 until n) {
                val f = (k + 0.5f) / n; val q = en(f, r)
                rotate(f * 360f + (if (k % 2 == 0) 20f else -20f), q) {
                    val s = g * 1.1f
                    drawPath(Path().apply {
                        moveTo(q.x, q.y + s * 0.4f); lineTo(q.x - s * 0.5f, q.y + s * 0.25f); lineTo(q.x - s * 0.7f, q.y - s * 0.15f); lineTo(q.x - s * 0.28f, q.y - s * 0.12f)
                        lineTo(q.x, q.y - s * 0.6f); lineTo(q.x + s * 0.28f, q.y - s * 0.12f); lineTo(q.x + s * 0.7f, q.y - s * 0.15f); lineTo(q.x + s * 0.5f, q.y + s * 0.25f); close()
                    }, if (pasado(k, n)) color else pista)
                }
            }
        }
        // Corona de pino con doce bolas que se encienden y un moño arriba.
        TemaId.NAVIDAD -> {
            val pino = Path(); val m = 120
            for (k in 0..m) { val q = en(k.toFloat() / m, if (k % 2 == 0) r - 6 * u else r + 6 * u); if (k == 0) pino.moveTo(q.x, q.y) else pino.lineTo(q.x, q.y) }
            drawPath(pino, e.secondary.copy(alpha = 0.75f), style = Stroke(1.4f * u))
            for (k in 0 until 12) {
                val q = en((k + 0.5f) / 12, r + (if (k % 2 == 0) 3 else -3) * u)
                if (pasado(k, 12)) { drawCircle(color, 5.5f * u, q); drawCircle(Color.White.copy(alpha = 0.7f), 1.6f * u, Offset(q.x - 1.8f * u, q.y - 1.8f * u)) }
                else drawCircle(pista, 5.5f * u, q, style = Stroke(1.2f * u))
            }
            val arriba = en(0f, r)
            drawPath(Path().apply { moveTo(arriba.x, arriba.y); lineTo(arriba.x - 11 * u, arriba.y - 7 * u); lineTo(arriba.x - 11 * u, arriba.y + 7 * u); close() }, e.primary)
            drawPath(Path().apply { moveTo(arriba.x, arriba.y); lineTo(arriba.x + 11 * u, arriba.y - 7 * u); lineTo(arriba.x + 11 * u, arriba.y + 7 * u); close() }, e.primary)
            drawCircle(e.primary, 3 * u, arriba)
        }
        // Sarta de monedas con agujero cuadrado: las ya contadas, del color de la marcha; abajo, el nudo y la borla.
        TemaId.SEDA -> {
            drawCircle(tenue.copy(alpha = 0.45f), r, c, style = Stroke(u))
            val n = 24; val rm = 7 * u
            for (k in 0 until n) {
                val f = (k + 0.5f) / n; val q = en(f, r)
                if (pasado(k, n)) drawCircle(color, rm, q)
                else { drawCircle(e.background, rm, q); drawCircle(pista, rm, q, style = Stroke(1.2f * u)) }
                rotate(f * 360f, q) { drawRect(e.background, Offset(q.x - 2.2f * u, q.y - 2.2f * u), Size(4.4f * u, 4.4f * u)) }
            }
            val nudo = en(0.5f, r + rm + u)
            drawLine(e.secondary, nudo, Offset(nudo.x, c.y + R), 2 * u)
            drawCircle(e.secondary, 2.5f * u, nudo)
        }
        // Rosetón: doce lancetas que se encienden como vidrieras, entre dos aros de tracería.
        TemaId.GOTICO -> {
            val rIn = r - g * 0.6f; val rOut = R - 3 * u; val rMid = rOut - 10 * u; val bw = 9 * u
            val lanceta = Path().apply {
                moveTo(c.x - bw, c.y - rIn); lineTo(c.x - bw, c.y - rMid)
                quadraticTo(c.x - bw, c.y - rOut + 3 * u, c.x, c.y - rOut)
                quadraticTo(c.x + bw, c.y - rOut + 3 * u, c.x + bw, c.y - rMid)
                lineTo(c.x + bw, c.y - rIn); close()
            }
            for (k in 0 until 12) {
                rotate((k + 0.5f) * 30f, c) {
                    drawPath(lanceta, if (pasado(k, 12)) color.copy(alpha = 0.85f) else pista)
                    drawPath(lanceta, tenue.copy(alpha = 0.6f), style = Stroke(1.2f * u))
                }
            }
            drawCircle(tenue.copy(alpha = 0.5f), rIn - 3 * u, c, style = Stroke(1.2f * u))
            drawCircle(tenue.copy(alpha = 0.5f), R - u, c, style = Stroke(u))
        }
        // Esfera de radar: aros de distancia, la cruz en la orilla, el barrido hasta donde va y el eco que lo marca.
        TemaId.ESPACIO -> {
            drawArc(color.copy(alpha = 0.1f), -90f, 360f * p, true, Offset(c.x - r, c.y - r), Size(r * 2, r * 2))
            drawCircle(tenue.copy(alpha = 0.5f), r, c, style = Stroke(1.2f * u))
            drawCircle(tenue.copy(alpha = 0.2f), r * 0.66f, c, style = Stroke(0.8f * u))
            drawCircle(tenue.copy(alpha = 0.35f), R - u, c, style = Stroke(0.8f * u))
            for (k in 0 until 72) drawLine(tenue.copy(alpha = 0.4f), en(k / 72f, r), en(k / 72f, r + (if (k % 6 == 0) 7 else 3) * u), 0.8f * u)
            listOf(0f, 0.25f, 0.5f, 0.75f).forEach { f -> drawLine(tenue.copy(alpha = 0.6f), en(f, r * 0.82f), en(f, R - u), u) }
            listOf(0.13f to 0.9f, 0.41f to 0.8f, 0.68f to 0.93f, 0.86f to 0.78f).forEach { (f, k) -> if (f <= p) drawCircle(color.copy(alpha = 0.6f), 1.8f * u, en(f, r * k)) }
            drawLine(color, en(p, r * 0.75f), en(p, r), 2 * u)
            val eco = en(p, r)
            drawCircle(color.copy(alpha = 0.3f), 8 * u, eco)
            drawCircle(color, 3.5f * u, eco)
        }
        // El ojo de buey que se inunda: el agua sube por dentro del cristal a medida que pasa la sesión, con su ola y sus
        // burbujas; alrededor, la brida con doce pernos y la escala del profundímetro.
        TemaId.ABISMO -> {
            val ventana = Path().apply { addOval(Rect(c, r)) }
            clipPath(ventana) {
                val nivel = c.y + r - 2 * r * p
                val agua = Path(); var x = c.x - r
                agua.moveTo(x, c.y + r); agua.lineTo(x, nivel)
                while (x <= c.x + r) { agua.lineTo(x, nivel + 3 * u * sin((x - c.x) / (14 * u))); x += 3 * u }
                agua.lineTo(c.x + r, c.y + r); agua.close()
                drawPath(agua, color.copy(alpha = 0.22f))
                listOf(0.3f to 0.2f, 0.55f to 0.55f, 0.42f to 0.8f, 0.68f to 0.35f, 0.2f to 0.65f).forEach { (fx, fy) ->
                    val b = Offset(c.x - r + 2 * r * fx, c.y + r - 2 * r * p * fy)
                    if (p > 0.05f) drawCircle(color.copy(alpha = 0.5f), (2f + 2f * fx) * u, b, style = Stroke(u))
                }
            }
            drawCircle(tenue.copy(alpha = 0.55f), r, c, style = Stroke(2 * u))
            drawCircle(pista, R - g * 0.5f, c, style = Stroke(g * 0.8f))
            for (k in 0 until 12) drawCircle(tenue.copy(alpha = 0.6f), 2.6f * u, en(k / 12f + 1f / 24f, R - g * 0.5f))
            for (k in 0 until 60) drawLine(tenue.copy(alpha = 0.35f), en(k / 60f, r - (if (k % 5 == 0) 9 else 4) * u), en(k / 60f, r), 0.8f * u)
            arco(color, 0f, p, R - g * 0.5f, 3 * u, StrokeCap.Round)
            drawCircle(color, 3.5f * u, en(p, R - g * 0.5f))
        }
        // Astrolabio: el limbo graduado, la red en estrella de ocho puntas detrás de la cuenta, el anillo arriba y la alidada.
        TemaId.ANDALUS -> {
            val a1 = r - g * 0.5f; val a2 = r + g * 0.5f; val limbo = tenue.copy(alpha = 0.55f)
            arco(color.copy(alpha = 0.45f), 0f, p, r, g)
            drawCircle(limbo, a1, c, style = Stroke(u))
            drawCircle(limbo, a2, c, style = Stroke(u))
            for (k in 0 until 72) drawLine(limbo, en(k / 72f, a2), en(k / 72f, a2 - (if (k % 6 == 0) g else 4 * u)), if (k % 6 == 0) 1.2f * u else 0.7f * u)
            val red = Path()
            for (s in 0..1) {
                for (i in 0..3) { val q = en(s / 8f + i / 4f, a1 - 4 * u); if (i == 0) red.moveTo(q.x, q.y) else red.lineTo(q.x, q.y) }
                red.close()
            }
            drawPath(red, e.tertiary.copy(alpha = 0.25f), style = Stroke(1.2f * u))
            drawCircle(e.tertiary, 3.5f * u, Offset(c.x, c.y - R + 4 * u), style = Stroke(1.5f * u))
            val punta = en(p, a1); val izq = en(p - 0.012f, R - u); val der = en(p + 0.012f, R - u)
            drawPath(Path().apply { moveTo(punta.x, punta.y); lineTo(izq.x, izq.y); lineTo(der.x, der.y); close() }, e.tertiary)
        }
        // La rueda del techo de la yurta: las varas que salen de ella se tiñen al avanzar; afuera, la costura del fieltro.
        TemaId.MONGOL -> {
            val rIn = r - g * 0.7f; val rOut = r + g * 0.45f
            drawCircle(tenue.copy(alpha = 0.6f), rIn, c, style = Stroke(2.5f * u))
            drawCircle(tenue.copy(alpha = 0.6f), rIn - 4 * u, c, style = Stroke(u))
            val n = 36
            for (k in 0 until n) drawLine(if (pasado(k, n)) color else pista, en((k + 0.5f) / n, rIn + 2 * u), en((k + 0.5f) / n, rOut), 3 * u, cap = StrokeCap.Round)
            drawCircle(e.primary.copy(alpha = 0.5f), R - 2 * u, c, style = Stroke(1.5f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * u, 3 * u))))
        }
        // Construcción a la manera de Vitruvio: cuadrado y círculo a tinta, golpes de compás y la marcha a sanguina en dos trazos.
        TemaId.RENACIMIENTO -> {
            val tinta = tenue.copy(alpha = 0.35f); val l = r * 0.93f
            drawRect(tinta, Offset(c.x - l, c.y - l), Size(2 * l, 2 * l), style = Stroke(0.8f * u))
            drawCircle(tinta, r, c, style = Stroke(0.8f * u))
            listOf(Offset(-1f, -1f), Offset(1f, -1f), Offset(-1f, 1f), Offset(1f, 1f)).forEach { s ->
                val q = Offset(c.x + s.x * l, c.y + s.y * l)
                drawLine(tinta, q, Offset(q.x - s.x * 12 * u, q.y - s.y * 12 * u), 0.8f * u)
            }
            for (k in 0 until 8) {
                val q = en(k / 8f, r)
                drawLine(tinta, Offset(q.x - 4 * u, q.y - 4 * u), Offset(q.x + 4 * u, q.y + 4 * u), 0.7f * u)
                drawLine(tinta, Offset(q.x - 4 * u, q.y + 4 * u), Offset(q.x + 4 * u, q.y - 4 * u), 0.7f * u)
            }
            arco(color.copy(alpha = 0.85f), 0f, p, r, 2 * u, StrokeCap.Round)
            arco(color.copy(alpha = 0.4f), 0.006f, p, r + 3 * u, u)
            val q = en(p, r)
            drawLine(color, Offset(q.x - 5 * u, q.y), Offset(q.x + 5 * u, q.y), u)
            drawLine(color, Offset(q.x, q.y - 5 * u), Offset(q.x, q.y + 5 * u), u)
        }
        // La ampolleta de la guardia: un compás náutico graduado en treinta y dos cuartas, con la flor de lis al norte; el
        // avance es un cabo que da la vuelta y la aguja apunta adonde va.
        TemaId.PIRATA -> {
            val tinta = tenue.copy(alpha = 0.4f)
            drawCircle(tinta, r + 6 * u, c, style = Stroke(0.8f * u))
            for (k in 0 until 32) {
                val q0 = en(k / 32f, r - (if (k % 4 == 0) 7 else 3) * u); val q1 = en(k / 32f, r + 2 * u)
                drawLine(tinta, q0, q1, (if (k % 4 == 0) 1.1f else 0.6f) * u)
            }
            val norte = en(0f, r + 12 * u)
            drawCircle(color.copy(alpha = 0.6f), 2.6f * u, norte); drawCircle(color.copy(alpha = 0.6f), 1.8f * u, norte + Offset(-3 * u, 2 * u)); drawCircle(color.copy(alpha = 0.6f), 1.8f * u, norte + Offset(3 * u, 2 * u))
            arco(color.copy(alpha = 0.85f), 0f, p, r, 3 * u, StrokeCap.Round)
            arco(tenue.copy(alpha = 0.5f), 0f, p, r, 1 * u, StrokeCap.Butt)
            val q = en(p, r)
            drawCircle(color, 4 * u, q); drawCircle(tenue, 1.5f * u, q)
        }
        // Cartel constructivista: cuñas del color de la marcha y negras, alternadas; una diagonal en la esquina y la estrella en la punta.
        TemaId.URSS -> {
            drawLine(e.onBackground.copy(alpha = 0.85f), Offset(size.width * 0.72f, 0f), Offset(size.width, size.height * 0.28f), 6 * u)
            val n = 24; val a1 = r - g * 0.5f; val a2 = r + g * 0.5f
            for (k in 0 until n) {
                val f0 = k.toFloat() / n; val f1 = (k + 1f) / n - 0.006f
                val cuna = Path().apply {
                    arcTo(Rect(c, a2), -90f + f0 * 360f, (f1 - f0) * 360f, true)
                    arcTo(Rect(c, a1), -90f + f1 * 360f, -(f1 - f0) * 360f, false)
                    close()
                }
                drawPath(cuna, if (pasado(k, n)) (if (k % 2 == 0) color else e.onBackground) else pista)
            }
            estrellaEnfoque(en(p, r), 10 * u, e.tertiary)
        }
        // Escarapela plisada con el reloj decimal de la República: diez horas y cien minutos; lo hecho se tiñe.
        TemaId.REVOLUCION -> {
            val n = 60; val rIn = r - g * 0.5f; val rOut = R - u
            val plisado = Path().apply {
                for (k in 0..n * 2) { val q = en(k.toFloat() / (n * 2), if (k % 2 == 0) rOut else r + g * 0.15f); if (k == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }
                close()
                addOval(Rect(c, rIn))
                fillType = PathFillType.EvenOdd
            }
            drawPath(plisado, pista)
            if (p > 0f) clipPath(Path().apply { moveTo(c.x, c.y); arcTo(Rect(c, R + 2 * u), -90f, 360f * minOf(p, 0.9999f), false); close() }) {
                drawPath(plisado, color.copy(alpha = 0.55f))
            }
            drawCircle(e.secondary.copy(alpha = 0.8f), rIn, c, style = Stroke(3 * u))
            drawCircle(Color.White.copy(alpha = 0.6f), rIn - 3 * u, c, style = Stroke(2 * u))
            for (k in 0 until 100) {
                val largo = if (k % 10 == 0) 10 * u else 3 * u
                drawLine(tenue.copy(alpha = if (k % 10 == 0) 0.7f else 0.35f), en(k / 100f, rIn - 6 * u), en(k / 100f, rIn - 6 * u - largo), if (k % 10 == 0) 1.6f * u else 0.6f * u)
            }
        }
        // Anillo de piedra con runas inventadas que se encienden y cuatro gemas que se encienden a cada cuarto.
        TemaId.ANTIGUAS -> {
            drawCircle(tenue.copy(alpha = 0.4f), R - 2 * u, c, style = Stroke(2 * u))
            drawCircle(tenue.copy(alpha = 0.4f), r - g * 0.75f, c, style = Stroke(1.2f * u))
            val n = 24; val h = 6 * u; val gr = 1.6f * u
            for (k in 0 until n) {
                val f = (k + 0.5f) / n; val q = en(f, r); val col = if (pasado(k, n)) color else pista
                rotate(f * 360f, q) {
                    drawLine(col, Offset(q.x, q.y - h), Offset(q.x, q.y + h), gr)
                    when (k % 4) {
                        0 -> { drawLine(col, Offset(q.x, q.y - h), Offset(q.x + 4 * u, q.y - h * 0.4f), gr); drawLine(col, Offset(q.x, q.y), Offset(q.x + 4 * u, q.y + h * 0.6f), gr) }
                        1 -> drawLine(col, Offset(q.x - 4 * u, q.y - h * 0.5f), Offset(q.x + 4 * u, q.y + h * 0.1f), gr)
                        2 -> { drawLine(col, Offset(q.x, q.y - h * 0.3f), Offset(q.x - 4 * u, q.y + h * 0.2f), gr); drawLine(col, Offset(q.x, q.y - h * 0.3f), Offset(q.x + 4 * u, q.y + h * 0.2f), gr) }
                        else -> { drawLine(col, Offset(q.x, q.y - h), Offset(q.x + 4 * u, q.y - h * 0.5f), gr); drawLine(col, Offset(q.x + 4 * u, q.y - h * 0.5f), Offset(q.x, q.y), gr) }
                    }
                }
            }
            for (j in 1..4) rombo(en(j / 4f, r), 4 * u, 6.5f * u, if (j / 4f <= p) e.tertiary else pista)
        }
    }
}

/** Estrella de [puntas] puntas centrada en [c]; [dentro] es el radio de los valles respecto al de las puntas. */
private fun DrawScope.estrellaEnfoque(c: Offset, r: Float, color: Color, puntas: Int = 5, dentro: Float = 0.42f) {
    val p = Path()
    for (i in 0 until puntas * 2) {
        val rr = if (i % 2 == 0) r else r * dentro
        val a = Math.toRadians(-90.0 + 180.0 / puntas * i)
        val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
        if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
    }
    p.close()
    drawPath(p, color)
}

/**
 * Una pieza de la tira de la sesión en el tema [t], en el rectángulo ([x], [y], [w], [h]); [u] son los píxeles de un dp.
 * Cartela, celdas de terminal, cuenta de collar, cinta ondulada, ficha perforada, tubo, placa cortada, tubo de ensayo,
 * boleto, banderín, hoja, festón de nieve, sarta de monedas, ojiva, vía con nodos, rombos de sebka, cola de golondrina,
 * trapecio, paralelogramo, cartel arrancado y piedra rúnica.
 */
private fun piezaTira(t: TemaId, x: Float, y: Float, w: Float, h: Float, u: Float): Path = Path().apply {
    when (t) {
        TemaId.ROMANO -> {
            val r = minOf(h * 0.3f, w * 0.3f)
            moveTo(x + r, y); lineTo(x + w - r, y); quadraticTo(x + w - r, y + r, x + w, y + r); lineTo(x + w, y + h - r); quadraticTo(x + w - r, y + h - r, x + w - r, y + h)
            lineTo(x + r, y + h); quadraticTo(x + r, y + h - r, x, y + h - r); lineTo(x, y + r); quadraticTo(x + r, y + r, x + r, y); close()
        }
        TemaId.CIBER -> {
            val n = (w / (h * 0.9f)).toInt().coerceAtLeast(1); val ancho = w / n
            for (i in 0 until n) addRect(Rect(x + i * ancho, y, x + (i + 1) * ancho - minOf(1.5f * u, ancho * 0.3f), y + h))
        }
        // Un tubo de neón: la tira redondeada en las puntas.
        TemaId.NEON -> addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(x, y, x + w, y + h), androidx.compose.ui.geometry.CornerRadius(h / 2)))
        TemaId.HELENO -> {
            val d = minOf(h * 0.5f, w / 2)
            moveTo(x, y + h / 2); lineTo(x + d, y); lineTo(x + w - d, y); lineTo(x + w, y + h / 2); lineTo(x + w - d, y + h); lineTo(x + d, y + h); close()
        }
        // Cartuchos: redondos a la izquierda y cerrados por la barra a la derecha.
        TemaId.EGIPTO -> {
            val rr = minOf(h / 2, w / 2)
            moveTo(x + rr, y); lineTo(x + w, y); lineTo(x + w, y + h); lineTo(x + rr, y + h)
            arcTo(Rect(x, y, x + 2 * rr, y + h), 90f, 180f, false); close()
        }
        TemaId.MEDIEVAL -> {
            val a = h * 0.15f; val l = 10 * u
            moveTo(x, y + a)
            var xx = x
            while (xx < x + w) { xx = minOf(xx + 2 * u, x + w); lineTo(xx, y + a + a * sin((xx - x) / l)) }
            while (xx > x) { lineTo(xx, y + h - a + a * sin((xx - x) / l)); xx = maxOf(xx - 2 * u, x) }
            lineTo(x, y + h - a); close()
        }
        // Un tramo de muralla: el borde de arriba almenado.
        TemaId.TEMPLARIO -> {
            val m = maxOf(h * 0.4f, 3 * u); val a = h * 0.22f
            moveTo(x, y + h); lineTo(x, y)
            var xx = x; var k = 0
            while (xx + m < x + w) { val yy = if (k % 2 == 0) y else y + a; lineTo(xx, yy); lineTo(xx + m, yy); xx += m; k++ }
            lineTo(xx, if (k % 2 == 0) y else y + a); lineTo(x + w, if (k % 2 == 0) y else y + a); lineTo(x + w, y + h); close()
        }
        TemaId.DISTOPIA -> {
            addRect(Rect(x, y, x + w, y + h))
            if (w > h) { addOval(Rect(Offset(x + h * 0.45f, y + h / 2), h * 0.18f)); fillType = PathFillType.EvenOdd }
        }
        TemaId.REFUGIO -> addRoundRect(RoundRect(x, y, x + w, y + h, CornerRadius(minOf(h, w) / 2)))
        TemaId.ORDEN -> {
            val k = minOf(h * 0.3f, w / 2)
            moveTo(x + k, y); lineTo(x + w - k, y); lineTo(x + w, y + k); lineTo(x + w, y + h - k); lineTo(x + w - k, y + h); lineTo(x + k, y + h); lineTo(x, y + h - k); lineTo(x, y + k); close()
        }
        TemaId.LABORATORIO -> {
            val r = minOf(h / 2, w / 2)
            addRoundRect(RoundRect(x, y, x + w, y + h, topLeftCornerRadius = CornerRadius.Zero, topRightCornerRadius = CornerRadius(r),
                bottomRightCornerRadius = CornerRadius(r), bottomLeftCornerRadius = CornerRadius.Zero))
        }
        TemaId.COLONOS -> {
            val r = minOf(h * 0.22f, w / 4); val m = y + h / 2
            moveTo(x, y); lineTo(x + w, y); lineTo(x + w, m - r)
            arcTo(Rect(Offset(x + w, m), r), 270f, -180f, false)
            lineTo(x + w, y + h); lineTo(x, y + h); lineTo(x, m + r)
            arcTo(Rect(Offset(x, m), r), 90f, -180f, false)
            close()
        }
        TemaId.NAPOLEON -> {
            val d = minOf(h * 0.45f, w * 0.5f)
            moveTo(x, y); lineTo(x + w - d, y); lineTo(x + w, y + h / 2); lineTo(x + w - d, y + h); lineTo(x, y + h); close()
        }
        TemaId.BOTANICO -> {
            moveTo(x, y + h / 2); quadraticTo(x + w / 2, y - h / 2, x + w, y + h / 2); quadraticTo(x + w / 2, y + h * 1.5f, x, y + h / 2); close()
        }
        TemaId.RUINAS -> {
            // Un adoquín desportillado: cuatro lados desiguales.
            moveTo(x, y); lineTo(x + w, y + h * 0.1f); lineTo(x + w - h * 0.2f, y + h); lineTo(x + h * 0.15f, y + h * 0.9f); close()
        }
        TemaId.NAVIDAD -> {
            val f = h * 0.25f; val n = (w / (h * 0.9f)).toInt().coerceAtLeast(1); val paso = w / n
            moveTo(x, y + h); lineTo(x, y + f)
            for (i in 0 until n) quadraticTo(x + i * paso + paso / 2, y - f, x + (i + 1) * paso, y + f)
            lineTo(x + w, y + h); close()
        }
        TemaId.SEDA -> {
            val n = (w / h).toInt().coerceAtLeast(1); val paso = w / n; val rm = minOf(h, paso) * 0.46f
            for (i in 0 until n) { val cc = Offset(x + (i + 0.5f) * paso, y + h / 2); addOval(Rect(cc, rm)); addRect(Rect(cc, rm * 0.32f)) }
            fillType = PathFillType.EvenOdd
        }
        TemaId.GOTICO -> {
            val d = minOf(h * 0.6f, w / 2)
            moveTo(x, y + h / 2); quadraticTo(x + d * 0.15f, y, x + d, y); lineTo(x + w - d, y); quadraticTo(x + w - d * 0.15f, y, x + w, y + h / 2)
            quadraticTo(x + w - d * 0.15f, y + h, x + w - d, y + h); lineTo(x + d, y + h); quadraticTo(x + d * 0.15f, y + h, x, y + h / 2); close()
        }
        TemaId.ESPACIO -> {
            val rn = minOf(h * 0.42f, w / 4)
            addRect(Rect(x + rn, y + h * 0.32f, x + w - rn, y + h * 0.68f))
            addOval(Rect(Offset(x + rn, y + h / 2), rn)); addOval(Rect(Offset(x + w - rn, y + h / 2), rn))
        }
        // Una ristra de burbujas: cada pieza de la sesión es una fila de burbujas de varios tamaños.
        TemaId.ABISMO -> {
            val n = (w / (h * 0.8f)).toInt().coerceAtLeast(1); val paso = w / n
            for (i in 0 until n) {
                val rr = h * (if (i % 3 == 1) 0.42f else 0.32f)
                addOval(Rect(Offset(x + paso * (i + 0.5f), y + h / 2 + (if (i % 2 == 0) -h * 0.08f else h * 0.08f)), minOf(rr, paso * 0.48f)))
            }
        }
        TemaId.ANDALUS -> {
            val n = (w / (h * 1.1f)).toInt().coerceAtLeast(1); val paso = w / n
            for (i in 0 until n) {
                val cx = x + (i + 0.5f) * paso
                moveTo(x + i * paso, y + h / 2); lineTo(cx, y); lineTo(x + (i + 1) * paso, y + h / 2); lineTo(cx, y + h); close()
            }
        }
        TemaId.MONGOL -> {
            val d = minOf(h * 0.35f, w * 0.3f)
            moveTo(x, y); lineTo(x + w, y); lineTo(x + w - d, y + h / 2); lineTo(x + w, y + h); lineTo(x, y + h); lineTo(x + d, y + h / 2); close()
        }
        TemaId.RENACIMIENTO -> {
            val d = minOf(h * 0.35f, w * 0.25f)
            moveTo(x + d, y); lineTo(x + w - d, y); lineTo(x + w, y + h); lineTo(x, y + h); close()
        }
        // Un banderín de cola de golondrina.
        TemaId.PIRATA -> {
            val d = minOf(h * 0.5f, w * 0.2f)
            moveTo(x, y); lineTo(x + w, y); lineTo(x + w - d, y + h / 2); lineTo(x + w, y + h); lineTo(x, y + h); close()
        }
        TemaId.URSS -> {
            val s = minOf(h * 0.5f, w * 0.4f)
            moveTo(x + s, y); lineTo(x + w, y); lineTo(x + w - s, y + h); lineTo(x, y + h); close()
        }
        TemaId.REVOLUCION -> {
            val diente = h * 0.25f
            moveTo(x, y); lineTo(x + w, y); lineTo(x + w, y + h * 0.8f)
            var xx = x + w; var i = 0
            while (xx > x) { xx = maxOf(xx - diente, x); lineTo(xx, if (i % 2 == 0) y + h else y + h * 0.7f); i++ }
            lineTo(x, y + h * 0.8f); close()
        }
        TemaId.ANTIGUAS -> {
            val k = minOf(h * 0.25f, w * 0.2f)
            moveTo(x, y + h * 0.25f); lineTo(x + minOf(w * 0.3f, h), y); lineTo(x + w, y + h * 0.12f); lineTo(x + w - k, y + h); lineTo(x + minOf(h * 0.1f, w * 0.1f), y + h); close()
        }
    }
}
