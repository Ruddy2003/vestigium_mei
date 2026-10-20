package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.sensores.Galeria
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TextoMarcado
import io.github.ruddy2003.vestigium.ui.Tonos
import io.github.ruddy2003.vestigium.ui.esOscuro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime

/** Algo con hora en la cinta: un tramo (de [a] a [b]) o un instante (b = null), con su texto al tocarlo. */
private class Cosa(val pista: Int, val a: Long, val b: Long?, val color: Color, val rotulo: String, val detalle: String, val alto: Float = 1f)

/** Una pista de la cinta: su nombre y su signo. */
private class PistaCinta(val nombre: String, val signo: String)

/**
 * La cinta del día: 29 horas (de las 0:00 a las 5:00 del día siguiente) en una tira que se desliza, con una pista por
 * cada cosa que pasó: cielo, sueño, siestas y enfoque, lugares, ejercicio, pantalla, notificaciones, luz, música,
 * momentos, comidas, gastos y fotos. Tocar algo lo explica abajo.
 */
@Composable
fun PantallaCinta(nav: Nav) {
    var fecha by remember { mutableStateOf(Seleccion.cinta ?: Fechas.diaLogico()) }
    LaunchedEffect(fecha) { Seleccion.cinta = fecha }
    val ctx = LocalContext.current
    val cs = MaterialTheme.colorScheme
    val oscuro = esOscuro()
    val ini = Fechas.inicioDia(fecha)
    val fin = ini + 29 * 3_600_000L
    val musica by produceState(emptyList<Pair<Long, String>>(), fecha) {
        value = withContext(Dispatchers.IO) { runCatching { Repo.escuchas(fecha, Fechas.sumar(fecha, 1)).map { Fechas.millis(it.ts) to (it.titulo + if (it.artista.isNotBlank()) " · ${it.artista}" else "") } }.getOrDefault(emptyList()) }
    }
    val fotos by produceState(emptyList<Long>(), fecha) {
        value = withContext(Dispatchers.IO) { runCatching { if (Galeria.tienePermiso(ctx)) Galeria.delDia(ctx, fecha).map { it.ms } else emptyList() }.getOrDefault(emptyList()) }
    }
    val comidas by produceState(emptyList<Pair<Long, String>>(), fecha) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                Repo.comidas(fecha, fecha).mapNotNull { c ->
                    val h = runCatching { LocalTime.parse(c.hora.take(5)) }.getOrNull() ?: return@mapNotNull null
                    java.time.LocalDateTime.of(Fechas.parse(c.fecha), h).atZone(Fechas.zona()).toInstant().toEpochMilli() to c.alimento
                }
            }.getOrDefault(emptyList())
        }
    }

    val pistas = ArrayList<PistaCinta>()
    val cosas = ArrayList<Cosa>()
    fun pista(nombre: String, signo: String): Int { pistas += PistaCinta(nombre, signo); return pistas.lastIndex }
    fun hm(ms: Long) = Fechas.horaDe(Fechas.isoDeMillis(ms))

    // Sueño: la noche que terminó esta mañana y la que empieza esta noche.
    val noches = listOf(fecha, Fechas.sumar(fecha, 1)).mapNotNull { f -> Repo.salud[f]?.let { s -> if (s.sueno_inicio != null && s.sueno_fin != null) s else null } }
    val tramos = Repo.tramos.filter { it.fecha == fecha }
    if (noches.isNotEmpty() || tramos.any { it.tipo == "siesta" }) {
        val p = pista("sueño", "☾")
        noches.forEach { s ->
            val a = Fechas.millis(s.sueno_inicio!!); val b = Fechas.millis(s.sueno_fin!!)
            cosas += Cosa(p, a, b, cs.secondary, "${(s.sueno_min ?: 0) / 60} h", "Dormiste de ${hm(a)} a ${hm(b)}" + (s.sueno_min?.let { " · ${it / 60} h ${it % 60} min" } ?: ""))
        }
        tramos.filter { it.tipo == "siesta" }.forEach { t ->
            val a = Fechas.millis(t.inicio)
            cosas += Cosa(p, a, a + t.min * 60_000L, cs.tertiary, "siesta", "Siesta de ${t.min} min a las ${hm(a)}")
        }
    }
    val enfoque = tramos.filter { it.tipo != "siesta" }
    if (enfoque.isNotEmpty()) {
        val p = pista("enfoque", "◷")
        val colores = listOf(cs.primary, cs.tertiary, cs.secondary)
        enfoque.forEach { t ->
            val a = Fechas.millis(t.inicio)
            val i = Catalogo.ENFOQUE.indexOfFirst { it.id == t.tipo }.coerceAtLeast(0)
            cosas += Cosa(p, a, a + t.min * 60_000L, colores[i % 3], Catalogo.nombreTramo(t.tipo).lowercase(),
                "${Catalogo.nombreTramo(t.tipo)}: ${t.min} min desde las ${hm(a)}" + if (t.nota.isNotBlank()) " — ${t.nota}" else "")
        }
    }
    val visitas = Repo.visitas.filter { Fechas.millis(it.entrada) < fin && (it.salida?.let { s -> Fechas.millis(s) } ?: System.currentTimeMillis()) > ini }
    if (visitas.isNotEmpty()) {
        val p = pista("lugares", "⌂")
        val ids = visitas.map { it.lugar }.distinct()
        val colores = listOf(cs.primary, cs.secondary, cs.tertiary)
        visitas.forEach { v ->
            val a = Fechas.millis(v.entrada); val b = v.salida?.let { Fechas.millis(it) } ?: System.currentTimeMillis()
            val n = Repo.lugares.firstOrNull { it.id == v.lugar }?.nombre ?: "lugar"
            cosas += Cosa(p, a, b, colores[ids.indexOf(v.lugar) % 3], n, "$n: de ${hm(a)} a ${if (v.salida == null) "ahora" else hm(b)}")
        }
    }
    val ses = Repo.sesiones.filter { it.fecha == fecha }
    val esc = Repo.ascensos.filter { it.fecha == fecha && it.inicio != null }
    if (ses.isNotEmpty() || esc.isNotEmpty()) {
        val p = pista("ejercicio", "▲")
        ses.forEach { s -> val a = Fechas.millis(s.inicio); val b = Fechas.millis(s.fin); cosas += Cosa(p, a, b, cs.tertiary, s.tipo_nombre.lowercase(), "${s.tipo_nombre}: ${s.min} min desde las ${hm(a)}") }
        esc.forEach { x ->
            val h = runCatching { LocalTime.parse(x.inicio!!.take(5)) }.getOrNull() ?: return@forEach
            val a = java.time.LocalDateTime.of(Fechas.parse(x.fecha), h).atZone(Fechas.zona()).toInstant().toEpochMilli()
            cosas += Cosa(p, a, a + (x.min ?: 90) * 60_000L, cs.primary, "escalada", "Escalada en ${x.lugar.ifBlank { "el muro" }}: ${x.bloques.size} bloques")
        }
    }
    Repo.pantalla[fecha]?.por_hora_min?.takeIf { it.sum() > 0 }?.let { h ->
        val p = pista("pantalla", "▭")
        h.forEachIndexed { i, m -> if (m > 0) cosas += Cosa(p, ini + i * 3_600_000L, ini + (i + 1) * 3_600_000L, cs.primary, "", "De ${i}:00 a ${i + 1}:00: $m min de pantalla", alto = (m / 60f).coerceIn(0.08f, 1f)) }
    }
    Repo.notifs[fecha]?.por_hora?.takeIf { it.sum() > 0 }?.let { h ->
        val p = pista("avisos", "◌")
        val max = (h.maxOrNull() ?: 1).coerceAtLeast(1)
        h.forEachIndexed { i, n -> if (n > 0) cosas += Cosa(p, ini + i * 3_600_000L, ini + (i + 1) * 3_600_000L, cs.secondary, "", "De ${i}:00 a ${i + 1}:00: $n notificaciones", alto = (n.toFloat() / max).coerceIn(0.08f, 1f)) }
    }
    Repo.conexiones[fecha]?.let { cx ->
        if (cx.luz_n.sum() > 0) {
            val p = pista("luz", "☀")
            val vals = cx.luz_suma.indices.map { i -> if (cx.luz_n[i] > 0) cx.luz_suma[i] / cx.luz_n[i] else 0.0 }
            vals.forEachIndexed { i, v -> if (v > 0) cosas += Cosa(p, ini + i * 3_600_000L, ini + (i + 1) * 3_600_000L, Color(0xFFE8B923), "", "De ${i}:00 a ${i + 1}:00: unos ${v.toInt()} lux" + if (v > 1000) " (luz de exterior)" else "",
                alto = (kotlin.math.ln(1 + v) / kotlin.math.ln(1 + 20000.0)).toFloat().coerceIn(0.08f, 1f)) }
        }
    }
    if (musica.isNotEmpty()) { val p = pista("música", "♫"); musica.forEach { (t, s) -> cosas += Cosa(p, t, null, cs.primary, "", "${hm(t)} · $s") } }
    val privado = Repo.config.seguimiento_privado
    val mom = Repo.momentos.filter { it.fecha == fecha && (privado || it.tipo != "recaida") }
    if (mom.isNotEmpty()) {
        val p = pista("momentos", "◷")
        mom.forEach { m ->
            val t = Fechas.millis(m.ts)
            val color = if (m.tipo == "recaida") cs.error else Tonos.valencia(m.valencia?.toDouble(), oscuro) ?: cs.secondary
            cosas += Cosa(p, t, null, color, io.github.ruddy2003.vestigium.ui.tipoNombre(m.tipo, Repo.config.discreto).take(1),
                "${hm(t)} · ${io.github.ruddy2003.vestigium.ui.tipoNombre(m.tipo, Repo.config.discreto)}" + (m.valencia?.let { " · $it/7" } ?: "") + (if (m.texto.isNotBlank()) " — ${m.texto}" else "") +
                    (Repo.lugares.firstOrNull { it.id == m.lugar }?.nombre?.let { " · en $it" } ?: ""))
        }
    }
    if (comidas.isNotEmpty()) { val p = pista("comidas", "◒"); comidas.forEach { (t, s) -> cosas += Cosa(p, t, null, cs.tertiary, "", "${hm(t)} · $s") } }
    val gastos = Repo.gastos.filter { it.dia == fecha }
    if (gastos.isNotEmpty()) {
        val p = pista("gastos", "$")
        gastos.forEach { g ->
            runCatching { java.time.LocalDateTime.of(Fechas.parse(g.fecha), LocalTime.parse(g.hora.take(5))).atZone(Fechas.zona()).toInstant().toEpochMilli() }.getOrNull()?.let { t ->
                cosas += Cosa(p, t, null, cs.error, "", "${g.hora} · $" + "%,.0f".format(g.monto) + " en ${g.categoria.lowercase()}" + if (g.nota.isNotBlank()) " — ${g.nota}" else "")
            }
        }
    }
    if (fotos.isNotEmpty()) { val p = pista("fotos", "▣"); fotos.forEach { t -> cosas += Cosa(p, t, null, cs.secondary, "", "${hm(t)} · una foto") } }

    var elegida by remember(fecha) { mutableStateOf<Cosa?>(null) }
    val scroll = rememberScrollState()
    val densidad = LocalDensity.current
    val anchoHora = 64.dp
    LaunchedEffect(fecha) {
        kotlinx.coroutines.delay(60)
        val hora = if (fecha == Fechas.diaLogico()) (java.time.LocalTime.now().hour - 3).coerceAtLeast(0) else 6
        scroll.scrollTo(with(densidad) { (anchoHora * hora).roundToPx() })
    }

    Hoja("La cinta", nav.atras, "Tu día hora por hora. Desliza a los lados y toca algo para ver qué fue.") {
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { fecha = Fechas.sumar(fecha, -1) }) { Text("‹", style = MaterialTheme.typography.titleLarge) }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(Estilo.fecha(fecha), style = MaterialTheme.typography.titleMedium, color = cs.primary)
                Text(Fechas.titulo(fecha).lowercase(), style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant)
            }
            TextButton(onClick = { fecha = Fechas.sumar(fecha, 1) }, enabled = fecha < Fechas.diaLogico()) { Text("›", style = MaterialTheme.typography.titleLarge) }
        }
        val e = Repo.entorno[fecha]
        if (e != null) Ayuda(listOfNotNull(e.t_max?.let { "máx ${it.toInt()}°" }, e.t_min?.let { "mín ${it.toInt()}°" }, e.precip_mm?.takeIf { it > 0.2 }?.let { "lluvia ${"%.1f".format(it)} mm" },
            e.amanecer?.let { "amanece ${it.takeLast(5)}" }, e.atardecer?.let { "anochece ${it.takeLast(5)}" }).joinToString(" · "), Modifier.padding(bottom = 6.dp))
        if (pistas.isEmpty()) { Ayuda("Este día no tiene nada con hora todavía."); return@Hoja }
        val alto = 34.dp
        val medidor = rememberTextMeasurer()
        val estiloRotulo = MaterialTheme.typography.labelSmall.copy(color = cs.onPrimary)
        val estiloEje = MaterialTheme.typography.labelSmall.copy(color = cs.onSurfaceVariant)
        val noche = cs.surfaceVariant
        val linea = cs.outline
        fun hora(s: String?) = s?.let { runCatching { LocalTime.parse(it.takeLast(5)) }.getOrNull() }
        val amanecer = hora(e?.amanecer); val atardecer = hora(e?.atardecer)
        Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
            Column(Modifier.width(30.dp)) {
                pistas.forEach { p -> Box(Modifier.height(alto), contentAlignment = Alignment.Center) { Text(p.signo, style = MaterialTheme.typography.titleMedium, color = cs.primary) } }
            }
            Box(Modifier.weight(1f).horizontalScroll(scroll)) {
                Canvas(Modifier.width(anchoHora * 29).height(alto * pistas.size + 22.dp).pointerInput(fecha, cosas.size) {
                    detectTapGestures { o ->
                        val h = alto.toPx()
                        val i = (o.y / h).toInt()
                        val ms = ini + ((o.x / size.width) * (fin - ini)).toLong()
                        val tol = (fin - ini) * (14.dp.toPx() / size.width)
                        elegida = cosas.filter { it.pista == i }.minByOrNull { c ->
                            val b = c.b
                            if (b != null && ms in c.a..b) 0L else minOf(kotlin.math.abs(ms - c.a), kotlin.math.abs(ms - (b ?: c.a)))
                        }?.takeIf { c -> val b = c.b; (b != null && ms in c.a..b) || minOf(kotlin.math.abs(ms - c.a), kotlin.math.abs(ms - (b ?: c.a))) < tol }
                    }
                }) {
                    val h = alto.toPx()
                    val total = h * pistas.size
                    fun x(ms: Long) = ((ms - ini).toFloat() / (fin - ini)) * size.width
                    // La noche, sombreada con el amanecer y el atardecer del día.
                    if (amanecer != null && atardecer != null) {
                        val a = x(ini + amanecer.toSecondOfDay() * 1000L); val t = x(ini + atardecer.toSecondOfDay() * 1000L)
                        drawRect(noche, Offset(0f, 0f), Size(a, total))
                        drawRect(Brush.horizontalGradient(listOf(noche.copy(alpha = 0f), noche), t, t + 40f), Offset(t, 0f), Size(size.width - t, total))
                    }
                    for (hh in 0..29) {
                        val xa = x(ini + hh * 3_600_000L)
                        drawLine(linea.copy(alpha = if (hh % 6 == 0) 0.6f else 0.2f), Offset(xa, 0f), Offset(xa, total), 1f)
                        val r = medidor.measure("${hh % 24}", estiloEje)
                        drawText(r, topLeft = Offset((xa - r.size.width / 2).coerceIn(0f, size.width - r.size.width), total + 3f))
                    }
                    pistas.indices.forEach { i -> drawLine(linea.copy(alpha = 0.35f), Offset(0f, (i + 1) * h), Offset(size.width, (i + 1) * h), 0.6f) }
                    cosas.forEach { c ->
                        val y = c.pista * h
                        val b = c.b
                        if (b != null) {
                            val xa = x(c.a).coerceIn(0f, size.width); val xb = x(b).coerceIn(0f, size.width)
                            if (xb > xa) {
                                val alt = h * 0.64f * c.alto
                                drawRoundRect(c.color.copy(alpha = if (c === elegida) 1f else 0.78f), Offset(xa, y + h * 0.18f + (h * 0.64f - alt)), Size(xb - xa - 1f, alt), CornerRadius(4f, 4f))
                                if (c.rotulo.isNotBlank() && xb - xa > 34f) {
                                    val r = medidor.measure(c.rotulo.take(((xb - xa) / 14f).toInt().coerceAtLeast(2)), estiloRotulo)
                                    drawText(r, topLeft = Offset(xa + 4f, y + (h - r.size.height) / 2))
                                }
                            }
                        } else {
                            val xa = x(c.a)
                            if (xa in 0f..size.width) {
                                if (c.rotulo.isNotBlank()) {
                                    drawCircle(c.color, h * 0.26f, Offset(xa, y + h / 2))
                                    val r = medidor.measure(c.rotulo, estiloRotulo)
                                    drawText(r, topLeft = Offset(xa - r.size.width / 2, y + (h - r.size.height) / 2))
                                } else drawLine(c.color, Offset(xa, y + h * 0.2f), Offset(xa, y + h * 0.8f), 3f)
                                if (c === elegida) drawCircle(c.color, h * 0.4f, Offset(xa, y + h / 2), style = Stroke(2f))
                            }
                        }
                    }
                    val ahora = System.currentTimeMillis()
                    if (ahora in ini..fin) { val xa = x(ahora); drawLine(cs.error, Offset(xa, 0f), Offset(xa, total), 2f) }
                }
            }
        }
        Ayuda(pistas.joinToString("   ") { "${it.signo} ${it.nombre}" }, Modifier.padding(top = 6.dp))
        Tarjeta(Modifier.padding(top = 10.dp)) {
            val c = elegida
            if (c == null) Ayuda("Toca un bloque o una marca de la cinta.")
            else TextoMarcado(c.detalle, style = MaterialTheme.typography.bodyMedium)
        }
        TextButton(onClick = { Seleccion.abrir(nav, fecha) }, modifier = Modifier.padding(top = 4.dp)) { Text("abrir este día ›", style = MaterialTheme.typography.labelLarge, color = cs.primary) }
    }
}
