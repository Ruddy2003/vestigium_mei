@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ciencia.Estadistica
import io.github.ruddy2003.vestigium.ciencia.Logros
import io.github.ruddy2003.vestigium.ciencia.TusNumeros
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.LogroPropio
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Fiesta
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.rememberSentir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val METALES = listOf(Color(0xFFB27A45), Color(0xFFBFC6CF), Color(0xFFE3B748), Color(0xFF9AD8E6))

/** Una medalla: el metal según el nivel, el signo del logro al centro y, si falta, cuánto llevas en un arco. */
@Composable
fun MedallaLogro(l: Logros.Logro, tam: Dp, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val metal = METALES[(l.nivel - 1).coerceIn(0, 3)]
    val secreto = l.secreto && !l.hecho
    Box(modifier.size(tam), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val r = size.minDimension / 2
            if (l.hecho) {
                drawCircle(Brush.radialGradient(listOf(metal.copy(alpha = 1f), metal.copy(alpha = 0.72f), metal.copy(alpha = 0.45f)), center, r), r)
                drawCircle(cs.surface.copy(alpha = 0.35f), r * 0.78f, style = Stroke(r * 0.06f))
                drawCircle(metal, r, style = Stroke(r * 0.07f))
                if (l.nivel >= 4) for (i in 0 until 12) {
                    val a = Math.toRadians(i * 30.0)
                    drawLine(metal, Offset(center.x + (r * 1.0f) * kotlin.math.cos(a).toFloat(), center.y + r * kotlin.math.sin(a).toFloat()),
                        Offset(center.x + r * 0.9f * kotlin.math.cos(a).toFloat(), center.y + r * 0.9f * kotlin.math.sin(a).toFloat()), r * 0.05f)
                }
            } else {
                drawCircle(cs.surfaceVariant.copy(alpha = 0.6f), r)
                drawCircle(cs.outline.copy(alpha = 0.5f), r * 0.94f, style = Stroke(r * 0.06f))
                if (!secreto && l.fraccion > 0f) drawArc(cs.primary, -90f, 360f * l.fraccion, false, Offset(center.x - r * 0.94f, center.y - r * 0.94f),
                    Size(r * 1.88f, r * 1.88f), style = Stroke(r * 0.1f, cap = StrokeCap.Round))
            }
        }
        Text(if (secreto) "?" else l.icono, fontSize = (tam.value * 0.42f).sp, textAlign = TextAlign.Center,
            color = if (l.hecho) Color(0xFF2A1E0E) else cs.onSurfaceVariant.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun rememberLogros(): List<Logros.Logro>? {
    val ctx = LocalContext.current
    val v by produceState(if (TusNumeros.sincrono) Logros.todos(ctx) else null, Repo.config, Repo.dias, Repo.salud) {
        if (!TusNumeros.sincrono) value = withContext(Dispatchers.Default) { runCatching { Logros.todos(ctx) }.getOrDefault(emptyList()) }
    }
    return v
}

/** En Más: los últimos logros como medallas, cuántos llevas y si hay nuevos. */
@Composable
fun RecuadroLogros(nav: Nav) {
    val l = rememberLogros()
    val hechos = l?.filter { it.hecho }?.sortedByDescending { it.conseguido }.orEmpty()
    val nuevos = l?.count { it.hecho && it.id !in Repo.config.logros_vistos } ?: 0
    val cs = MaterialTheme.colorScheme
    val detalle = when {
        l == null -> "Contando…"
        nuevos > 0 && Repo.config.logros_vistos.isNotEmpty() -> "¡$nuevos nuevo${if (nuevos == 1) "" else "s"}! · ${hechos.size} de ${l.size} conseguidos"
        else -> "${hechos.size} de ${l.size} conseguidos" + (l.filter { !it.hecho && !it.secreto }.maxByOrNull { it.fraccion }?.let { " · cerca: ${it.nombre}" } ?: "")
    }
    Recuadro("Logros", detalle, alto = 118.dp, onClick = { nav.ir("logros") }) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(cs.primary.copy(alpha = 0.10f), cs.surface)))) {
            Row(Modifier.fillMaxSize().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                val muestra = (hechos.take(5) + l.orEmpty().filter { !it.hecho && !it.secreto }.sortedByDescending { it.fraccion }.take(5)).take(6)
                if (muestra.isEmpty()) Text("Tus medallas aparecerán aquí", style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant)
                muestra.forEach { MedallaLogro(it, 58.dp) }
            }
        }
    }
}

@Composable
fun PantallaLogros(nav: Nav) {
    val ctx = LocalContext.current
    val sentir = rememberSentir()
    val todos = rememberLogros()
    var filtro by remember { mutableStateOf("todos") }
    var nuevos by remember { mutableStateOf<List<Logros.Logro>>(emptyList()) }
    var crear by remember { mutableStateOf(false) }
    // Al abrir: se festejan los nuevos (la primera vez solo se cuentan, para no festejar todo tu historial de golpe).
    LaunchedEffect(todos != null) {
        val l = todos ?: return@LaunchedEffect
        val vistos = Repo.config.logros_vistos
        val n = l.filter { it.hecho && it.id !in vistos }
        if (n.isNotEmpty()) {
            if (vistos.isNotEmpty()) { nuevos = n; Fiesta.lanzar(); sentir(Sentidos.Evento.LOGRO) }
            Logros.marcarVistos(n)
        }
    }
    Hoja("Logros", nav.atras, "Lo que has conseguido: escribir, moverte, viajar, leer, ser constante. Los de constancia piden la mayoría de los días, no una racha perfecta.") {
        val l = todos
        if (l == null) { Vacio("Contando…"); return@Hoja }
        val hechos = l.filter { it.hecho }
        Tarjeta(Modifier.padding(top = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("${hechos.size} de ${l.size}", style = MaterialTheme.typography.headlineMedium)
                    Ayuda("logros conseguidos · ${(hechos.size * 100 / maxOf(1, l.size))}%")
                }
                (1..4).forEach { nv ->
                    val k = hechos.count { it.nivel == nv }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(start = 10.dp)) {
                        Box(Modifier.size(22.dp).background(METALES[nv - 1], androidx.compose.foundation.shape.CircleShape))
                        Text("$k", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            LinearProgressIndicator(progress = { hechos.size.toFloat() / maxOf(1, l.size) }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
        }
        Seccion("Hall de logros", "Tus favoritos, en exhibición. Toca «elegir piezas» para cambiarlos.") { CuerpoHall(l) }
        if (nuevos.isNotEmpty()) Tarjeta(Modifier.padding(top = 10.dp)) {
            Text("¡Nuevo" + if (nuevos.size > 1) "s logros!" else " logro!", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Row(Modifier.padding(top = 8.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                nuevos.take(12).forEach { n ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 4.dp)) {
                        MedallaLogro(n, 64.dp)
                        Text(n.nombre, style = MaterialTheme.typography.labelMedium, maxLines = 2, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp).size(width = 86.dp, height = 36.dp))
                    }
                }
            }
        }
        Row(Modifier.padding(top = 12.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            (listOf("todos" to "Todos", "hechos" to "Conseguidos", "cerca" to "Cerca") + Logros.CATEGORIAS.filter { c -> l.any { it.categoria == c } || c == "Tuyos" }.map { it to it })
                .forEach { (id, n) -> Ficha(n, filtro == id) { filtro = id } }
        }
        val lista = when (filtro) {
            "todos" -> l.sortedWith(compareByDescending<Logros.Logro> { it.hecho }.thenByDescending { it.conseguido ?: "" }.thenByDescending { it.fraccion })
            "hechos" -> hechos.sortedByDescending { it.conseguido }
            "cerca" -> l.filter { !it.hecho && !it.secreto && it.fraccion > 0f }.sortedByDescending { it.fraccion }.take(25)
            else -> l.filter { it.categoria == filtro }.sortedWith(compareByDescending<Logros.Logro> { it.hecho }.thenByDescending { it.fraccion })
        }
        if (filtro == "Tuyos") {
            BotonPrincipal("+ Inventar un logro", Modifier.fillMaxWidth().padding(top = 10.dp)) { crear = true }
            Ayuda("Por ejemplo: «Leer 20 de 30 días», «10 veces con @abuela», «100,000 pasos en una semana» o «Gimnasio 3 veces por semana».", Modifier.padding(top = 6.dp))
        }
        if (crear) EditorLogro(cerrar = { crear = false })
        if (lista.isEmpty()) Vacio(if (filtro == "Tuyos") "Todavía no inventas ninguno." else "Nada por aquí todavía.")
        Tarjeta(Modifier.padding(top = 10.dp)) {
            lista.forEachIndexed { i, g ->
                if (i > 0) HorizontalDivider()
                FilaLogro(g)
            }
        }
        if (filtro != "Tuyos") TextButton(onClick = { filtro = "Tuyos"; crear = true }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
            Text("+ inventar un logro propio", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun FilaLogro(g: Logros.Logro) {
    val secreto = g.secreto && !g.hecho
    var abierto by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().clickable { abierto = !abierto }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        MedallaLogro(g, 52.dp)
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(if (secreto) "Logro secreto" else g.nombre, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(if (secreto) "Sigue viviendo y escribiendo: aparece cuando lo logras." else g.descripcion, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = if (abierto) 4 else 2, overflow = TextOverflow.Ellipsis)
            when {
                g.hecho -> Text("✓ " + Fechas.titulo(g.conseguido!!).lowercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                !secreto -> {
                    LinearProgressIndicator(progress = { g.fraccion }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp).height(5.dp))
                    Text(cuanto(g.progreso) + " / " + cuanto(g.meta) + if (g.unidad.isNotEmpty()) " ${g.unidad}" else "", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (abierto && g.propio) TextButton(onClick = {
                Repo.cambiarConfig { c -> c.copy(logros_propios = c.logros_propios.filterNot { "p_" + it.id == g.id }) }
            }) { Text("borrar este logro", style = MaterialTheme.typography.labelMedium) }
        }
    }
}

private fun cuanto(x: Double): String = when {
    x >= 1e6 -> "%.1f M".format(x / 1e6)
    x >= 10_000 -> "%,d".format(x.toLong())
    x == Math.floor(x) -> "%,d".format(x.toLong())
    else -> "%.1f".format(x)
}

private val ICONOS = listOf("★", "✎", "❖", "☾", "▣", "❦", "♥", "⚑", "✓", "☀", "⚡", "♫", "✈", "☕", "✿", "◆")

/** Inventar un logro: de qué (una variable de tu día, un hábito o una @persona/#tema), cómo se cuenta y la meta. */
@Composable
private fun EditorLogro(cerrar: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var fuente by remember { mutableStateOf("habito") }
    var clave by remember { mutableStateOf("") }
    var modo by remember { mutableStateOf("dias") }
    var meta by remember { mutableStateOf("20") }
    var ventana by remember { mutableStateOf("30") }
    var umbral by remember { mutableStateOf("") }
    var icono by remember { mutableStateOf("★") }
    var buscar by remember { mutableStateOf("") }
    val variables = remember { runCatching { Estadistica.variables(dinamicas = false) }.getOrDefault(emptyList()) }
    var version by remember { mutableIntStateOf(0) }
    Tarjeta(Modifier.padding(top = 10.dp)) {
        Text("Nuevo logro", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(nombre, { nombre = it }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, label = { Text("Nombre") })
        Etiqueta("Qué cuenta")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("habito" to "Un hábito", "variable" to "Algo de tu día", "etiqueta" to "@persona o #tema").forEach { (id, n) -> Ficha(n, fuente == id) { fuente = id; clave = "" } }
        }
        when (fuente) {
            "habito" -> FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val hs = Repo.config.habitos.filter { it.activo }
                if (hs.isEmpty()) Ayuda("Primero crea un hábito en Más → Hábitos.")
                hs.forEach { h -> Ficha(h.nombre, clave == h.id) { clave = h.id; if (nombre.isBlank()) nombre = h.nombre } }
            }
            "etiqueta" -> OutlinedTextField(clave, { clave = it.trim() }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, placeholder = { Text("@abuela o #guitarra") })
            else -> {
                OutlinedTextField(buscar, { buscar = it }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, placeholder = { Text("buscar: pasos, sueño, palabras…") })
                val q = buscar.trim().lowercase()
                FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    variables.filter { q.isEmpty() || it.nombre.lowercase().contains(q) || it.id.contains(q) }.take(14).forEach { v ->
                        Ficha(v.nombre, clave == v.id) { clave = v.id; if (nombre.isBlank()) nombre = v.nombre }
                    }
                }
                OutlinedTextField(umbral, { umbral = it.filter { ch -> ch.isDigit() || ch == '.' } }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true,
                    label = { Text("Un día cuenta si pasa de… (opcional)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            }
        }
        Etiqueta("Cómo se cuenta")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("dias" to "Días", "racha" to "Seguidos", "total" to "Suma total").forEach { (id, n) -> Ficha(n, modo == id) { modo = id } }
        }
        Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(meta, { meta = it.filter { ch -> ch.isDigit() || ch == '.' } }, Modifier.weight(1f), singleLine = true,
                label = { Text(if (modo == "total") "Meta (suma)" else "Días") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            if (modo == "dias") OutlinedTextField(ventana, { ventana = it.filter { ch -> ch.isDigit() } }, Modifier.weight(1f), singleLine = true,
                label = { Text("de cada (0 = desde siempre)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }
        Ayuda(when (modo) {
            "dias" -> if ((ventana.toIntOrNull() ?: 0) > 0) "Se logra cuando en cualquier tramo de ${ventana.ifBlank { "?" }} días lo hiciste ${meta.ifBlank { "?" }} veces: no necesitas racha." else "Se logra al juntar ${meta.ifBlank { "?" }} días con eso."
            "racha" -> "Se logra con ${meta.ifBlank { "?" }} días seguidos."
            else -> "Se logra cuando lo acumulado llega a ${meta.ifBlank { "?" }}."
        }, Modifier.padding(top = 4.dp))
        Etiqueta("Signo")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { ICONOS.forEach { i -> Ficha(i, icono == i) { icono = i } } }
        val listo = nombre.isNotBlank() && clave.isNotBlank() && (meta.toDoubleOrNull() ?: 0.0) > 0
        Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonPrincipal("Guardar", habilitado = listo) {
                val p = LogroPropio(Repo.nuevoId(), nombre.trim(), fuente, clave.trim(), modo, meta.toDoubleOrNull() ?: 1.0,
                    if (modo == "dias") ventana.toIntOrNull() ?: 0 else 0, umbral.toDoubleOrNull() ?: 0.0, Fechas.diaLogico(), icono)
                Repo.cambiarConfig { c -> c.copy(logros_propios = c.logros_propios + p) }
                version++; cerrar()
            }
            TextButton(onClick = cerrar) { Text("cancelar", style = MaterialTheme.typography.labelLarge) }
        }
    }
}
