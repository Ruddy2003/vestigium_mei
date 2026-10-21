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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Enfoque
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.PlanEnfoque
import io.github.ruddy2003.vestigium.data.Repo

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

/** El anillo del segmento: lleno según lo que va, del color del enfoque o del descanso. */
@Composable
private fun Anillo(progreso: Float, descanso: Boolean, pausa: Boolean, modifier: Modifier = Modifier, contenido: @Composable () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val color = when { pausa -> cs.outline; descanso -> cs.tertiary; else -> cs.primary }
    Box(modifier.aspectRatio(1f), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val grueso = 14.dp.toPx()
            val r = size.minDimension / 2 - grueso
            val o = Offset(size.width / 2 - r, size.height / 2 - r)
            drawArc(cs.outline.copy(alpha = 0.25f), 0f, 360f, false, o, Size(r * 2, r * 2), style = Stroke(grueso))
            drawArc(color, -90f, 360f * progreso.coerceIn(0f, 1f), false, o, Size(r * 2, r * 2), style = Stroke(grueso, cap = StrokeCap.Round))
            // Marcas de los minutos, como en una esfera.
            for (k in 0 until 60) {
                val a = Math.toRadians(k * 6.0 - 90)
                val largo = if (k % 5 == 0) 10.dp.toPx() else 4.dp.toPx()
                val r1 = r - grueso; val r2 = r1 - largo
                drawLine(cs.onSurfaceVariant.copy(alpha = if (k % 5 == 0) 0.6f else 0.25f),
                    Offset(size.width / 2 + (r1 * Math.cos(a)).toFloat(), size.height / 2 + (r1 * Math.sin(a)).toFloat()),
                    Offset(size.width / 2 + (r2 * Math.cos(a)).toFloat(), size.height / 2 + (r2 * Math.sin(a)).toFloat()), 1.5.dp.toPx())
            }
        }
        contenido()
    }
}

/** La tira de la sesión: un rectángulo por bloque (enfoque) o descanso, del largo de su duración. */
@Composable
private fun TiraSesion(p: PlanEnfoque, ahora: Long, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
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
            drawRoundRect(color.copy(alpha = 0.22f), Offset(x, y), Size(w, alto), CornerRadius(4.dp.toPx()))
            val lleno = when { i < p.segmento -> 1f; i == p.segmento -> (va.toFloat() / (s.ms ?: 1L)).coerceIn(0f, 1f); else -> 0f }
            if (lleno > 0f) drawRoundRect(color, Offset(x, y), Size(w * lleno, alto), CornerRadius(4.dp.toPx()))
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
                    Text(reloj(rest ?: Enfoque.transcurrido(p, ahora)), style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp, fontWeight = FontWeight.Light),
                        color = if (pausa) cs.onSurfaceVariant else cs.onBackground)
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
        AlertDialog(onDismissRequest = { terminando = false },
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
