@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import io.github.ruddy2003.vestigium.ui.Ayuda
import java.time.LocalDateTime
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Gasto
import io.github.ruddy2003.vestigium.data.Ingreso
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.sensores.Ubicacion
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Interruptor
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.barraGrafica
import io.github.ruddy2003.vestigium.ui.estiloCifras
import io.github.ruddy2003.vestigium.ui.estiloGrafica
import io.github.ruddy2003.vestigium.ui.pincelDeCifras
import kotlinx.coroutines.launch
import io.github.ruddy2003.vestigium.ui.Dialogo

fun dinero(v: Double): String = "$" + String.format(java.util.Locale.US, "%,.2f", v).removeSuffix(".00")

@Composable
fun PantallaGastos(nav: Nav? = null) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val sentir = io.github.ruddy2003.vestigium.ui.rememberSentir()
    var monto by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf<String?>(null) }
    var sub by remember { mutableStateOf("") }
    var auto by remember { mutableStateOf<String?>(null) }
    var abiertaCat by remember { mutableStateOf<String?>(null) }
    var imprevisto by remember { mutableStateOf(false) }
    var nota by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var mes by remember { mutableStateOf(Fechas.mesDe(Fechas.hoyCalendario())) }
    var armado by remember { mutableStateOf<String?>(null) }
    // null = ahora. Si se elige otra fecha u hora, se guarda tal cual y sin ubicación (la de ahora no sería la del pago).
    var cuando by remember { mutableStateOf<LocalDateTime?>(null) }
    // Lo que se corrige se abre en un diálogo, para que el cambio se vea donde lo tocaste (N-06).
    var editando by remember { mutableStateOf<Gasto?>(null) }
    // Gastos e ingresos comparten el formulario: «entra» cambia categorías, el interruptor y el color.
    var entra by remember { mutableStateOf(false) }
    var fijo by remember { mutableStateOf(false) }
    var repite by remember { mutableStateOf(false) }
    var editandoIngreso by remember { mutableStateOf<Ingreso?>(null) }

    fun limpiar() { monto = ""; nota = ""; imprevisto = false; fijo = false; repite = false; cuando = null; error = null; sub = ""; auto = null }
    androidx.compose.runtime.LaunchedEffect(Unit) { io.github.ruddy2003.vestigium.data.Dinero.asegurarCategoriasNuevas() }

    Hoja("Gastos", subtitulo = "Registra en el momento de pagar o de cobrar. Lo que se cuadra después ya no dice en qué se fue el dinero.", buscar = nav?.let { n -> { n.ir("buscar") } }) {
        Seccion("Registrar") {
            Tarjeta {
                // Gasto o ingreso: dos fichas grandes, como una balanza.
                Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(false to "− Gasto", true to "+ Ingreso").forEach { (v, t) ->
                        val sel = entra == v
                        val color = if (v) io.github.ruddy2003.vestigium.ui.Tonos.ingreso(io.github.ruddy2003.vestigium.ui.esOscuro()) else io.github.ruddy2003.vestigium.ui.Tonos.gasto(io.github.ruddy2003.vestigium.ui.esOscuro())
                        androidx.compose.material3.Surface(
                            onClick = { if (entra != v) { entra = v; cat = null; sub = ""; error = null; sentir(io.github.ruddy2003.vestigium.ui.Sentidos.Evento.ELEGIR) } },
                            shape = MaterialTheme.shapes.small, modifier = Modifier.weight(1f),
                            color = if (sel) color.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(if (sel) 2.dp else 1.dp, if (sel) color else MaterialTheme.colorScheme.outlineVariant),
                        ) {
                            Text(t, style = MaterialTheme.typography.titleMedium, color = if (sel) color else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 10.dp))
                        }
                    }
                }
                OutlinedTextField(value = monto, onValueChange = { monto = io.github.ruddy2003.vestigium.data.Dinero.limpiarMonto(it); error = null }, modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("$ ") }, placeholder = { Text("0") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), textStyle = MaterialTheme.typography.headlineSmall)
                Etiqueta("Categoría")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    (if (entra) Repo.config.categorias_ingreso else Repo.config.categorias).forEach { c -> Ficha(c, cat == c) { cat = if (cat == c) null else c; sub = ""; error = null } }
                }
                // Subcategorías con su monto habitual: elegir una llena el monto (N-29).
                val subs = cat?.takeIf { !entra }?.let { io.github.ruddy2003.vestigium.data.Dinero.subcategorias(it) }.orEmpty()
                if (subs.isNotEmpty()) {
                    Etiqueta("Subcategoría")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        subs.forEach { sc ->
                            val h = io.github.ruddy2003.vestigium.data.Dinero.montoHabitual(cat!!, sc.nombre)
                            Ficha(sc.nombre + (h?.let { " · ${dinero(it)}" } ?: ""), sub == sc.nombre) {
                                sub = if (sub == sc.nombre) "" else sc.nombre
                                if (sub.isNotEmpty() && h != null && (monto.isBlank() || monto == auto)) { monto = io.github.ruddy2003.vestigium.data.Dinero.texto(h); auto = monto }
                            }
                        }
                    }
                }
                Etiqueta("Cuándo")
                io.github.ruddy2003.vestigium.ui.SelectorHora(cuando) { cuando = it }
                if (entra) Interruptor("¿Se repite? (sueldo, beca, renta)", fijo) { fijo = it }
                else {
                    Interruptor("¿Fue imprevisto?", imprevisto) { imprevisto = it }
                    Interruptor("¿Se repite? (renta, suscripción, servicios)", repite) { repite = it }
                }
                OutlinedTextField(value = nota, onValueChange = { nota = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Nota opcional") }, minLines = 2)
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp)) }
                BotonPrincipal(if (entra) "Registrar ingreso" else "Registrar gasto", Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    val v = monto.toDoubleOrNull()
                    when {
                        v == null || v <= 0 -> error = "Escribe un monto mayor a cero."
                        cat == null -> error = "Elige una categoría."
                        entra -> {
                            val c = cuando
                            val fecha = c?.let { Fechas.iso(it.toLocalDate()) } ?: Fechas.hoyCalendario()
                            val hora = c?.let { "%02d:%02d".format(it.hour, it.minute) } ?: Fechas.horaAhora()
                            val i = Ingreso(Repo.nuevoId(), fecha, hora, Math.round(v * 100) / 100.0, cat!!, nota.trim(), fijo, ts_creado = Fechas.ahoraIso())
                            Repo.guardarIngreso(i)
                            sentir(io.github.ruddy2003.vestigium.ui.Sentidos.Evento.GUARDAR)
                            android.widget.Toast.makeText(ctx, "Ingreso registrado: ${dinero(i.monto)}", android.widget.Toast.LENGTH_SHORT).show()
                            limpiar(); mes = Fechas.mesDe(i.fecha)
                        }
                        else -> {
                            val c = cuando
                            val fecha = c?.let { Fechas.iso(it.toLocalDate()) } ?: Fechas.hoyCalendario()
                            val hora = c?.let { "%02d:%02d".format(it.hour, it.minute) } ?: Fechas.horaAhora()
                            val g = Gasto(Repo.nuevoId(), fecha, hora, Math.round(v * 100) / 100.0, cat!!, imprevisto, nota.trim(), subcategoria = sub, se_repite = repite, ts_creado = Fechas.ahoraIso())
                            Repo.guardarGasto(g)
                            if (c == null) scope.launch { Ubicacion.ultima(ctx)?.let { Repo.guardarGasto(g.copy(lat = it.latitude, lon = it.longitude)) } }
                            sentir(io.github.ruddy2003.vestigium.ui.Sentidos.Evento.GUARDAR)
                            android.widget.Toast.makeText(ctx, "Gasto registrado: ${dinero(g.monto)} · ${g.categoria}", android.widget.Toast.LENGTH_SHORT).show()
                            limpiar(); mes = Fechas.mesDe(g.fecha)
                        }
                    }
                }
            }
        }

        val items = Repo.gastos.filter { it.dia.startsWith(mes) }.sortedByDescending { it.dia + (if (it.dia != it.fecha) "2" else "1") + it.hora }
        val entradas = Repo.ingresos.filter { it.dia.startsWith(mes) }
        val total = items.sumOf { it.monto }
        val ingresado = entradas.sumOf { it.monto }
        val imp = items.filter { it.imprevisto }.sumOf { it.monto }
        Row(Modifier.fillMaxWidth().padding(top = 22.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedIconButton(onClick = { mes = Fechas.mesDe(Fechas.iso(Fechas.parse("$mes-01").minusMonths(1))); armado = null }) { Text("‹", fontSize = 20.sp) }
            Text(Fechas.mesTitulo(mes), style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            OutlinedIconButton(onClick = { mes = Fechas.mesDe(Fechas.iso(Fechas.parse("$mes-01").plusMonths(1))); armado = null }) { Text("›", fontSize = 20.sp) }
        }
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra(dinero(total), "gastado", Modifier.weight(1f)); Cifra(dinero(ingresado), "ingresos", Modifier.weight(1f)); Cifra(dinero(imp), "imprevistos", Modifier.weight(1f))
        }
        if (ingresado > 0 || total > 0) Box(Modifier.padding(top = 12.dp)) { Tarjeta { Balanza(ingresado, total) } }
        if (entradas.isNotEmpty()) Box(Modifier.padding(top = 12.dp)) {
            Tarjeta {
                Etiqueta("ingresos del mes")
                entradas.sortedByDescending { it.dia + it.hora }.forEach { i ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f).clickable { editandoIngreso = i }) {
                            Text(i.categoria + if (i.fijo) " · se repite" else "", style = MaterialTheme.typography.bodyLarge)
                            Text("${Fechas.corto(i.dia)} · ${i.hora}" + if (i.nota.isNotBlank()) " · ${i.nota}" else "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("+" + dinero(i.monto), fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo, color = io.github.ruddy2003.vestigium.ui.Tonos.ingreso(io.github.ruddy2003.vestigium.ui.esOscuro()))
                        Text("  ›", color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
        if (items.isEmpty()) { Box(Modifier.padding(top = 12.dp)) { Vacio("Sin gastos registrados este mes.") } }
        else {
            Box(Modifier.padding(top = 12.dp)) { Tarjeta { GastoPorDia(mes, items) } }
            val porCat = items.groupBy { it.categoria }.mapValues { e -> e.value.sumOf { it.monto } }.entries.sortedByDescending { it.value }
            // Un mes que suma 0 (gastos en cero o devoluciones) daba barras de ancho NaN y cerraba la pantalla.
            val max = porCat.first().value.takeIf { it > 0 } ?: 1.0
            val eg = estiloGrafica()
            val barraCat = MaterialTheme.colorScheme.primary
            val cifrasCat = estiloCifras(MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp))
            Box(Modifier.padding(top = 12.dp)) {
                Tarjeta {
                    // Cada categoría se despliega por subcategoría al tocarla (Comida $2,300 → Café $620).
                    porCat.forEach { (c, v) ->
                        val porSub = items.filter { it.categoria == c && it.subcategoria.isNotBlank() }.groupBy { it.subcategoria }.mapValues { e -> e.value.sumOf { it.monto } }
                        Row(Modifier.fillMaxWidth().clickable(enabled = porSub.isNotEmpty()) { abiertaCat = if (abiertaCat == c) null else c }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(c + if (porSub.isNotEmpty()) (if (abiertaCat == c) " ▾" else " ▸") else "", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(100.dp))
                            Box(Modifier.weight(1f)) {
                                Canvas(Modifier.fillMaxWidth((v / max).toFloat().takeIf { !it.isNaN() }?.coerceIn(0.03f, 1f) ?: 0.03f).height(9.dp)) { barraGrafica(eg, Offset.Zero, size, barraCat, horizontal = true) }
                            }
                            Text(dinero(v), style = cifrasCat, modifier = Modifier.width(80.dp), textAlign = TextAlign.End)
                        }
                        if (abiertaCat == c) {
                            val sin = v - porSub.values.sum()
                            (porSub.entries.sortedByDescending { it.value }.map { it.key to it.value } + (if (sin > 0.004) listOf("sin subcategoría" to sin) else emptyList())).forEach { (n, x) ->
                                Row(Modifier.fillMaxWidth().padding(start = 16.dp, top = 2.dp, bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("→ $n", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                                    Text(dinero(x), fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
            var ultimo = ""
            items.forEach { g ->
                if (g.dia != ultimo) {
                    ultimo = g.dia
                    Text(Fechas.corto(g.dia).uppercase(), fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 14.dp, bottom = 2.dp))
                }
                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Tocar un gasto abre su corrección: monto, categoría, fecha u hora, o borrarlo.
                    Column(Modifier.weight(1f).clickable { editando = g }) {
                        Text(g.categoria + (if (g.subcategoria.isNotBlank()) " → ${g.subcategoria}" else "") + (if (g.imprevisto) " · imprevisto" else "") + (if (g.se_repite) " · se repite" else ""), style = MaterialTheme.typography.bodyLarge)
                        Text((if (g.dia != g.fecha) "${g.hora} de la madrugada" else g.hora) + if (g.nota.isNotBlank()) " · ${g.nota}" else "",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(dinero(g.monto), fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo)
                    Text("  ›", color = MaterialTheme.colorScheme.outline)
                }
            }
        }
        Seccion("Categorías y subcategorías", "Las tuyas, con el monto habitual de cada subcategoría: con él, un gasto se registra sin escribir.", plegable = true, plegadaPorDefecto = true) {
            EditorCategorias()
        }
        Seccion("Categorías de ingreso", "Agregar, renombrar, ordenar o quitar. Quitar una no cambia los ingresos ya registrados.", plegable = true, plegadaPorDefecto = true) {
            EditorCategoriasIngreso()
        }
    }
    editando?.let { g -> DialogoGasto(g) { editando = null } }
    editandoIngreso?.let { i -> DialogoIngreso(i) { editandoIngreso = null } }
}

/**
 * Categorías y subcategorías de gasto (N-29): agregar, renombrar, ordenar y quitar, y fijar el monto habitual de cada
 * subcategoría (vacío = automático: el que más se repite en tus últimos 10). Al renombrar, pregunta si cambia también los
 * gastos anteriores. Quitar no toca los gastos ya registrados.
 */
@Composable
fun EditorCategorias() {
    val D = io.github.ruddy2003.vestigium.data.Dinero
    val c = Repo.config
    var abierta by remember { mutableStateOf<String?>(null) }
    var nueva by remember { mutableStateOf("") }
    var nuevaSub by remember { mutableStateOf("") }
    // Qué se renombra: la categoría y, si es una subcategoría, su nombre.
    var renombrar by remember { mutableStateOf<Pair<String, String?>?>(null) }
    Tarjeta {
        c.categorias.forEachIndexed { i, cat ->
            if (i > 0) androidx.compose.material3.HorizontalDivider()
            val subs = D.subcategorias(cat)
            Row(Modifier.fillMaxWidth().clickable { abierta = if (abierta == cat) null else cat; nuevaSub = "" }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text((if (abierta == cat) "▾ " else "▸ ") + cat + if (subs.isNotEmpty()) " · ${subs.size}" else "", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                if (i > 0) TextButton(onClick = { D.subir(cat) }) { Text("↑") }
                TextButton(onClick = { renombrar = cat to null }) { Text("✎") }
                TextButton(onClick = {
                    Repo.cambiarConfig { it.copy(categorias = it.categorias - cat) }
                    io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Categoría quitada") { Repo.cambiarConfig { cc -> if (cat in cc.categorias) cc else cc.copy(categorias = cc.categorias.toMutableList().apply { add(i.coerceIn(0, size), cat) }) } }
                }) { Text("×", color = MaterialTheme.colorScheme.error) }
            }
            if (abierta == cat) Column(Modifier.padding(start = 14.dp, bottom = 6.dp)) {
                subs.forEach { sc ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(sc.nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        var m by remember(cat, sc.nombre) { mutableStateOf(sc.monto?.let { D.texto(it) } ?: "") }
                        OutlinedTextField(value = m, onValueChange = { t -> m = D.limpiarMonto(t); D.fijarMonto(cat, sc.nombre, m.toDoubleOrNull()?.takeIf { it > 0 }) },
                            modifier = Modifier.width(112.dp), singleLine = true, prefix = { Text("$") },
                            placeholder = { Text(D.montoHabitual(cat, sc.nombre)?.let { D.texto(it) } ?: "auto", style = MaterialTheme.typography.bodySmall) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), textStyle = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = { renombrar = cat to sc.nombre }) { Text("✎") }
                        TextButton(onClick = {
                            D.quitarSub(cat, sc.nombre)
                            io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Subcategoría quitada") { Repo.cambiarConfig { cc -> cc.copy(subcategorias = cc.subcategorias + (cat to (cc.subcategorias[cat].orEmpty() + sc))) } }
                        }) { Text("×", color = MaterialTheme.colorScheme.error) }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = nuevaSub, onValueChange = { nuevaSub = it }, modifier = Modifier.weight(1f), singleLine = true, placeholder = { Text("Nueva subcategoría de $cat") })
                    TextButton(onClick = { D.agregarSub(cat, nuevaSub); nuevaSub = "" }, enabled = nuevaSub.isNotBlank()) { Text("Añadir") }
                }
            }
        }
        Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = nueva, onValueChange = { nueva = it }, modifier = Modifier.weight(1f), singleLine = true, placeholder = { Text("Nueva categoría") })
            TextButton(onClick = { val n = nueva.trim(); if (n.isNotEmpty() && n !in Repo.config.categorias) { Repo.cambiarConfig { it.copy(categorias = it.categorias + n) }; nueva = "" } }) { Text("Añadir") }
        }
        Ayuda("El monto vacío es automático: el que más se repite en tus últimos 10 gastos de esa subcategoría. Quitar una categoría no cambia los gastos ya registrados.", Modifier.padding(top = 6.dp))
    }
    renombrar?.let { (cat, sb) ->
        var t by remember(cat, sb) { mutableStateOf(sb ?: cat) }
        val n = D.cuantos(cat, sb)
        var tambien by remember(cat, sb) { mutableStateOf(true) }
        Dialogo(onDismissRequest = { renombrar = null }, title = { Text(if (sb == null) "Renombrar categoría" else "Renombrar subcategoría") },
            text = {
                Column {
                    OutlinedTextField(value = t, onValueChange = { t = it }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    if (n > 0) Interruptor("Cambiar también ${if (n == 1) "el gasto anterior" else "los $n gastos anteriores"}", tambien) { tambien = it }
                }
            },
            confirmButton = { TextButton(onClick = {
                if (sb == null) D.renombrarCategoria(cat, t, tambien) else D.renombrarSub(cat, sb, t, tambien)
                renombrar = null
            }, enabled = t.isNotBlank()) { Text("Guardar") } },
            dismissButton = { TextButton(onClick = { renombrar = null }) { Text("cancelar") } })
    }
}

/**
 * Categorías de ingreso (esquema 17): agregar, renombrar, ordenar y quitar, como las de gasto pero sin subcategorías.
 * Al renombrar, pregunta si cambia también los ingresos anteriores. Quitar no toca los ingresos ya registrados.
 */
@Composable
fun EditorCategoriasIngreso() {
    val D = io.github.ruddy2003.vestigium.data.Dinero
    val c = Repo.config
    var nueva by remember { mutableStateOf("") }
    var renombrar by remember { mutableStateOf<String?>(null) }
    Tarjeta {
        c.categorias_ingreso.forEachIndexed { i, cat ->
            if (i > 0) androidx.compose.material3.HorizontalDivider()
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(cat, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                if (i > 0) TextButton(onClick = { D.subirIngreso(cat) }) { Text("↑") }
                TextButton(onClick = { renombrar = cat }) { Text("✎") }
                TextButton(onClick = {
                    Repo.cambiarConfig { it.copy(categorias_ingreso = it.categorias_ingreso - cat) }
                    io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Categoría quitada") { Repo.cambiarConfig { cc -> if (cat in cc.categorias_ingreso) cc else cc.copy(categorias_ingreso = cc.categorias_ingreso.toMutableList().apply { add(i.coerceIn(0, size), cat) }) } }
                }) { Text("×", color = MaterialTheme.colorScheme.error) }
            }
        }
        Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = nueva, onValueChange = { nueva = it }, modifier = Modifier.weight(1f), singleLine = true, placeholder = { Text("Nueva categoría de ingreso") })
            TextButton(onClick = { val n = nueva.trim(); if (n.isNotEmpty() && n !in Repo.config.categorias_ingreso) { Repo.cambiarConfig { it.copy(categorias_ingreso = it.categorias_ingreso + n) }; nueva = "" } }) { Text("Añadir") }
        }
    }
    renombrar?.let { cat ->
        var t by remember(cat) { mutableStateOf(cat) }
        val n = D.cuantosIngresos(cat)
        var tambien by remember(cat) { mutableStateOf(true) }
        Dialogo(onDismissRequest = { renombrar = null }, title = { Text("Renombrar categoría de ingreso") },
            text = {
                Column {
                    OutlinedTextField(value = t, onValueChange = { t = it }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    if (n > 0) Interruptor("Cambiar también ${if (n == 1) "el ingreso anterior" else "los $n ingresos anteriores"}", tambien) { tambien = it }
                }
            },
            confirmButton = { TextButton(onClick = { D.renombrarIngreso(cat, t, tambien); renombrar = null }, enabled = t.isNotBlank()) { Text("Guardar") } },
            dismissButton = { TextButton(onClick = { renombrar = null }) { Text("cancelar") } })
    }
}

/** Corregir un gasto: monto, categoría, fecha y hora, imprevisto, si se repite y nota; o borrarlo (con confirmación y Deshacer). */
@Composable
fun DialogoGasto(g: Gasto, nuevo: Boolean = false, cerrar: () -> Unit) = DialogoDinero(
    titulo = if (nuevo) "Nuevo gasto" else "Gasto", monto0 = g.monto, cat0 = g.categoria, categorias = Repo.config.categorias, fecha0 = g.fecha, hora0 = g.hora, nota0 = g.nota,
    sub0 = g.subcategoria, subs = { cat -> io.github.ruddy2003.vestigium.data.Dinero.subcategorias(cat).map { it.nombre } },
    marca = "¿Fue imprevisto?", marca0 = g.imprevisto, cerrar = cerrar,
    marca2 = "¿Se repite? (renta, suscripción, servicios)", marca20 = g.se_repite,
    // Uno nuevo (desde «＋ agregar a este día») guarda cuándo se anotó; uno corregido, cuándo se corrigió.
    guardar = { monto, cat, sub, fecha, hora, nota, marca, marca2 ->
        val n = g.copy(monto = monto, categoria = cat, subcategoria = sub, fecha = fecha, hora = hora, nota = nota, imprevisto = marca, se_repite = marca2)
        Repo.guardarGasto(if (nuevo) n.copy(ts_creado = Fechas.ahoraIso()) else n.copy(ts_editado = Fechas.ahoraIso()))
    },
    borrar = if (nuevo) null else ({ Repo.borrarGasto(g.id); io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Gasto borrado") { Repo.guardarGasto(g) } }),
)

/** Corregir un ingreso, igual que un gasto. */
@Composable
fun DialogoIngreso(i: Ingreso, cerrar: () -> Unit) = DialogoDinero(
    titulo = "Ingreso", monto0 = i.monto, cat0 = i.categoria, categorias = Repo.config.categorias_ingreso, fecha0 = i.fecha, hora0 = i.hora, nota0 = i.nota,
    marca = "¿Se repite? (sueldo, beca, renta)", marca0 = i.fijo, cerrar = cerrar,
    guardar = { monto, cat, _, fecha, hora, nota, marca, _ -> Repo.guardarIngreso(i.copy(monto = monto, categoria = cat, fecha = fecha, hora = hora, nota = nota, fijo = marca, ts_editado = Fechas.ahoraIso())) },
    borrar = { Repo.borrarIngreso(i.id); io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Ingreso borrado") { Repo.guardarIngreso(i) } },
)

@Composable
private fun DialogoDinero(
    titulo: String, monto0: Double, cat0: String, categorias: List<String>, fecha0: String, hora0: String, nota0: String, marca: String, marca0: Boolean,
    cerrar: () -> Unit, guardar: (Double, String, String, String, String, String, Boolean, Boolean) -> Unit, borrar: (() -> Unit)?,
    sub0: String = "", subs: (String) -> List<String> = { emptyList() }, marca2: String? = null, marca20: Boolean = false,
) {
    var monto by remember { mutableStateOf(if (monto0 <= 0.0) "" else if (monto0 % 1.0 == 0.0) monto0.toLong().toString() else monto0.toString()) }
    var cat by remember { mutableStateOf(cat0) }
    var sub by remember { mutableStateOf(sub0) }
    // «9:05» también se lee (antes solo «09:05»).
    var cuando by remember { mutableStateOf<LocalDateTime?>(runCatching { LocalDateTime.of(Fechas.parse(fecha0), java.time.LocalTime.parse(hora0.trim().take(5).padStart(5, '0'))) }.getOrNull()) }
    var nota by remember { mutableStateOf(nota0) }
    var m by remember { mutableStateOf(marca0) }
    var m2 by remember { mutableStateOf(marca20) }
    var confirmar by remember { mutableStateOf(false) }
    val v = monto.toDoubleOrNull()
    Dialogo(
        onDismissRequest = cerrar,
        // El título sigue a la fecha y hora elegidas (antes se quedaba con las de antes de cambiarlas).
        title = { Text("$titulo · " + (cuando?.let { "${Fechas.corto(Fechas.iso(it.toLocalDate()))} ${"%02d:%02d".format(it.hour, it.minute)}" } ?: "${Fechas.corto(fecha0)} ${hora0.take(5)}")) },
        text = {
            Column(Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                OutlinedTextField(value = monto, onValueChange = { monto = io.github.ruddy2003.vestigium.data.Dinero.limpiarMonto(it) }, modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("$ ") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                Etiqueta("Categoría")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    (categorias + listOf(cat0).filter { it !in categorias }).forEach { c -> Ficha(c, cat == c) { if (cat != c) sub = ""; cat = c } }
                }
                val opciones = (subs(cat) + listOf(sub).filter { it.isNotBlank() && cat == cat0 && it !in subs(cat) })
                if (opciones.isNotEmpty()) {
                    Etiqueta("Subcategoría")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        opciones.forEach { o -> Ficha(o, sub == o) { sub = if (sub == o) "" else o } }
                    }
                }
                Etiqueta("Cuándo")
                io.github.ruddy2003.vestigium.ui.SelectorHora(cuando) { cuando = it }
                Interruptor(marca, m) { m = it }
                if (marca2 != null) Interruptor(marca2, m2) { m2 = it }
                OutlinedTextField(value = nota, onValueChange = { nota = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Nota opcional") }, minLines = 2)
                // Borrar va adentro, en rojo y con confirmación; abajo quedan «cancelar» y «Guardar», como en los demás diálogos.
                if (borrar != null) TextButton(onClick = { if (confirmar) { borrar(); cerrar() } else confirmar = true }, modifier = Modifier.padding(top = 6.dp)) {
                    Text(if (confirmar) "¿borrar de verdad?" else "borrar", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(enabled = v != null && v > 0, onClick = {
                // Si la hora guardada no se pudo leer y no la cambiaste, se conservan su fecha y su hora: antes el gasto se
                // movía a hoy en silencio. Solo uno nuevo, sin fecha, toma la de ahora.
                val (f, h) = cuando?.let { c -> Fechas.iso(c.toLocalDate()) to "%02d:%02d".format(c.hour, c.minute) }
                    ?: if (fecha0.isNotBlank()) fecha0 to hora0 else LocalDateTime.now().let { c -> Fechas.iso(c.toLocalDate()) to "%02d:%02d".format(c.hour, c.minute) }
                guardar(Math.round((v ?: monto0) * 100) / 100.0, cat, sub, f, h, nota.trim(), m, m2)
                cerrar()
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = cerrar) { Text("cancelar") } },
    )
}

/** Lo que entró contra lo que salió en el mes: dos barras que crecen y el saldo. */
@Composable
private fun Balanza(ingresos: Double, gastos: Double) {
    val animar = Repo.config.animaciones
    var listo by remember { mutableStateOf(!animar) }
    androidx.compose.runtime.LaunchedEffect(Unit) { listo = true }
    val p by androidx.compose.animation.core.animateFloatAsState(if (listo) 1f else 0f, androidx.compose.animation.core.tween(if (animar) 700 else 0), label = "balanza")
    val max = maxOf(ingresos, gastos).takeIf { it > 0 } ?: 1.0
    val verde = io.github.ruddy2003.vestigium.ui.Tonos.ingreso(io.github.ruddy2003.vestigium.ui.esOscuro())
    val eg = estiloGrafica()
    val cifras = estiloCifras(MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp))
    Etiqueta("balance del mes")
    listOf(Triple("entró", ingresos, verde), Triple("salió", gastos, io.github.ruddy2003.vestigium.ui.Tonos.gasto(io.github.ruddy2003.vestigium.ui.esOscuro()))).forEach { (t, v, c) ->
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(t, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(56.dp))
            Box(Modifier.weight(1f)) {
                Canvas(Modifier.fillMaxWidth(((v / max).toFloat() * p).coerceIn(0.02f, 1f)).height(12.dp)) { barraGrafica(eg, Offset.Zero, size, c, horizontal = true) }
            }
            Text(dinero(v), style = cifras, modifier = Modifier.width(90.dp), textAlign = TextAlign.End)
        }
    }
    val saldo = ingresos - gastos
    Text((if (saldo >= 0) "Saldo a favor: " else "Gastaste más de lo que entró: ") + dinero(kotlin.math.abs(saldo)) +
        (if (ingresos > 0) " · ahorro de %.0f%%".format((saldo / ingresos * 100).coerceAtLeast(-999.0)).takeIf { saldo >= 0 }.orEmpty() else ""),
        style = MaterialTheme.typography.titleSmall, color = if (saldo >= 0) verde else MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 6.dp))
}

/** Barras del gasto de cada día del mes; los imprevistos en otro tono encima. */
@Composable
private fun GastoPorDia(mes: String, items: List<Gasto>) {
    val dias = Fechas.parse("$mes-01").lengthOfMonth()
    val porDia = DoubleArray(dias); val imp = DoubleArray(dias)
    items.forEach { g -> val d = g.dia.takeLast(2).toInt() - 1; if (d in 0 until dias) { porDia[d] += g.monto; if (g.imprevisto) imp[d] += g.monto } }
    val max = porDia.maxOrNull()?.takeIf { it > 0 } ?: return
    val diasConGasto = porDia.count { it > 0 }
    val barra = MaterialTheme.colorScheme.primary
    val alerta = MaterialTheme.colorScheme.error
    val eg = estiloGrafica()
    val pincel = pincelDeCifras(MaterialTheme.colorScheme.onSurfaceVariant, 22f, android.graphics.Paint.Align.CENTER)
    Etiqueta("por día")
    Canvas(Modifier.fillMaxWidth().aspectRatio(2.8f)) {
        val abajo = 26f
        val w = size.width / dias
        for (k in 0 until dias) {
            val h = (size.height - abajo) * (porDia[k] / max).toFloat()
            if (h > 0) barraGrafica(eg, Offset(k * w + w * 0.15f, size.height - abajo - h), Size(w * 0.7f, h), barra)
            val hi = (size.height - abajo) * (imp[k] / max).toFloat()
            if (hi > 0) barraGrafica(eg, Offset(k * w + w * 0.15f, size.height - abajo - hi), Size(w * 0.7f, hi), alerta)
            if (k == 0 || (k + 1) % 5 == 0) drawContext.canvas.nativeCanvas.drawText("${k + 1}", k * w + w / 2, size.height - 4f, pincel)
        }
    }
    val top = porDia.indices.maxBy { porDia[it] }
    Ayuda("Día más alto: el ${top + 1} con ${dinero(porDia[top])} · gastaste en $diasConGasto de ${porNumero(dias, "1 día", "$dias días")} · en rojo, imprevistos.")
}
