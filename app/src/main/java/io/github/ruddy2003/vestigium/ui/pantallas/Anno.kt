package io.github.ruddy2003.vestigium.ui.pantallas

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Grados
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.reportes.Reportes
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Clave
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Romano
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Tonos
import io.github.ruddy2003.vestigium.ui.esOscuro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.YearMonth

@Composable
fun PantallaAnno(nav: Nav) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var anio by remember { mutableStateOf(Fechas.diaLogico().take(4).toInt()) }
    var msg by remember { mutableStateOf<String?>(null) }
    val pre = "$anio-"
    val oscuro = esOscuro()
    val vacio = MaterialTheme.colorScheme.surfaceVariant
    val mu = remember(anio, Repo.momentos.size) { Repo.momentos.filter { it.fecha.startsWith(pre) && it.tipo == "muestreo" && it.valencia != null }.groupBy { it.fecha } }
    fun valencia(f: String): Double? = Repo.dias[f]?.valencia?.toDouble() ?: mu[f]?.mapNotNull { it.valencia }?.average()

    Hoja("Tu año", nav.atras, subtitulo = "Todo el año de un vistazo. Toca un cuadro para abrir ese día.") {
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { anio-- }, shape = RoundedCornerShape(4.dp)) { Text("‹", fontSize = 18.sp) }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ANNVS ${Romano.de(anio)}", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                Text("// $anio", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            OutlinedButton(onClick = { anio++ }, shape = RoundedCornerShape(4.dp)) { Text("›", fontSize = 18.sp) }
        }

        Seccion("El año en píxeles", "Cada fila es un mes y cada cuadro un día. Pórfido: días difíciles; bronce: días buenos.", numero = 1) {
            androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxWidth()) {
                val celda = (maxWidth - 30.dp) / 31
                Row {
                    Column(Modifier.width(30.dp)) {
                        for (m in 1..12) androidx.compose.foundation.layout.Box(Modifier.height(celda), contentAlignment = Alignment.CenterStart) {
                            Text(Romano.de(m), style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Canvas(Modifier.weight(1f).height(celda * 12).pointerInput(anio) {
                        detectTapGestures { p ->
                            val cw = size.width / 31f; val ch = size.height / 12f
                            val d = (p.x / cw).toInt() + 1; val m = (p.y / ch).toInt() + 1
                            if (m in 1..12 && d in 1..YearMonth.of(anio, m).lengthOfMonth()) Seleccion.abrir(nav, "%04d-%02d-%02d".format(anio, m, d))
                        }
                    }) {
                        val cw = size.width / 31f; val ch = size.height / 12f
                        for (m in 1..12) {
                            val dm = YearMonth.of(anio, m).lengthOfMonth()
                            for (d in 1..dm) {
                                val v = valencia("%04d-%02d-%02d".format(anio, m, d))
                                drawRect(Tonos.valencia(v, oscuro) ?: vacio, Offset((d - 1) * cw, (m - 1) * ch), Size(cw - 2f, ch - 2f))
                            }
                        }
                    }
                }
            }
        }

        val dias = Repo.dias.values.filter { it.fecha.startsWith(pre) }
        val palabras = dias.sumOf { Texto.palabras(it.nota) }
        Seccion("Mes por mes", numero = 2) {
            val medias = (1..12).map { m -> dias.filter { it.fecha.substring(5, 7).toInt() == m }.mapNotNull { it.valencia } }
            val color = MaterialTheme.colorScheme.primary
            Canvas(Modifier.fillMaxWidth().height(120.dp)) {
                val w = size.width / 12f
                medias.forEachIndexed { i, l ->
                    if (l.isEmpty()) return@forEachIndexed
                    val v = l.average(); val h = ((v - 1) / 6 * size.height).toFloat()
                    drawRect(Tonos.valencia(v, oscuro) ?: color, Offset(i * w + 4f, size.height - h), Size(w - 8f, h))
                }
            }
            Row(Modifier.fillMaxWidth()) { for (m in 1..12) Text(Romano.de(m), Modifier.weight(1f), style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), textAlign = TextAlign.Center) }
            val conDatos = medias.mapIndexedNotNull { i, l -> if (l.isEmpty()) null else (i + 1) to l.average() }
            if (conDatos.size >= 2) {
                val mejor = conDatos.maxBy { it.second }; val peor = conDatos.minBy { it.second }
                Clave("optimus", "${Fechas.mesTitulo("%04d-%02d".format(anio, mejor.first)).lowercase()} · %.2f".format(mejor.second))
                Clave("pessimus", "${Fechas.mesTitulo("%04d-%02d".format(anio, peor.first)).lowercase()} · %.2f".format(peor.second))
            }
        }

        Seccion("Resumen", numero = 3, latin = "NVMERI") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Cifra("${dias.size}", "días escritos", Modifier.weight(1f))
                Cifra("%,d".format(palabras), "palabras", Modifier.weight(1f))
                Cifra("${Repo.momentos.count { it.fecha.startsWith(pre) && it.tipo != "muestreo" }}", "momentos", Modifier.weight(1f))
            }
            val salud = Repo.salud.values.filter { it.fecha.startsWith(pre) }
            Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Cifra(salud.mapNotNull { it.sueno_min }.takeIf { it.isNotEmpty() }?.average()?.let { "%.1f h".format(it / 60) } ?: "—", "sueño", Modifier.weight(1f))
                Cifra(salud.mapNotNull { it.pasos }.takeIf { it.isNotEmpty() }?.average()?.let { "%,.0f".format(it) } ?: "—", "pasos/día", Modifier.weight(1f))
                Cifra("$" + "%,.0f".format(Repo.gastos.filter { it.fecha.startsWith(pre) }.sumOf { it.monto }), "gasto", Modifier.weight(1f))
            }
        }

        Seccion("Lo más frecuente", numero = 4) {
            val eq = Repo.equivalencias()
            val personas = dias.flatMap { d -> Texto.personas(d.nota).map { Texto.canon(it, eq) }.distinct() }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.take(8)
            val temas = dias.flatMap { d -> Texto.temas(d.nota).map { Texto.canon(it, eq) }.distinct() }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.take(8)
            val tipos = Repo.momentos.filter { it.fecha.startsWith(pre) && it.tipo != "muestreo" }.groupingBy { it.tipo }.eachCount().entries.sortedByDescending { it.value }.take(4)
            val musica by produceState(emptyList<Pair<String, Int>>(), anio) {
                value = withContext(Dispatchers.IO) {
                    runCatching { Repo.escuchas("$anio-01-01", "$anio-12-31").filter { it.artista.isNotBlank() }.groupingBy { it.artista }.eachCount().entries.sortedByDescending { it.value }.take(5).map { it.key to it.value } }.getOrDefault(emptyList())
                }
            }
            Tarjeta {
                if (personas.isNotEmpty()) Clave("personae", personas.joinToString("  ") { "@${it.key} ${it.value}" })
                if (temas.isNotEmpty()) Clave("themata", temas.joinToString("  ") { "#${it.key} ${it.value}" })
                if (tipos.isNotEmpty()) Clave("momenta", tipos.joinToString(" · ") { "${Catalogo.nombreTipo(it.key).lowercase()} ${it.value}" })
                if (musica.isNotEmpty()) Clave("musica", musica.joinToString(" · ") { "${it.first} ${it.second}" })
                val lugares = Repo.visitas.filter { it.entrada.startsWith(pre) }.groupingBy { it.lugar }.eachCount().entries.sortedByDescending { it.value }.take(4)
                if (lugares.isNotEmpty()) Clave("loca", lugares.joinToString(" · ") { e -> (Repo.lugares.firstOrNull { it.id == e.key }?.nombre ?: "?") + " ${e.value}" })
                val asc = Repo.ascensos.filter { it.fecha.startsWith(pre) }
                if (asc.isNotEmpty()) {
                    val max = asc.flatMap { it.bloques }.filter { Grados.encadenado(it.estilo) }.mapNotNull { b -> Grados.valor(b.grado)?.let { it to b.grado } }.maxByOrNull { it.first }?.second
                    Clave("ascensvs", "${asc.size} sesiones · ${asc.sumOf { it.bloques.size }} bloques" + (max?.let { " · máximo $it" } ?: ""))
                }
                val obras = Repo.obras.filter { it.estado == "terminado" && it.fin?.startsWith(pre) == true }
                if (obras.isNotEmpty()) Clave("bibliotheca", TIPOS_OBRA.mapNotNull { t -> obras.count { it.tipo == t.id }.takeIf { it > 0 }?.let { "$it ${t.nombre.lowercase()}" } }.joinToString(" · "))
                obras.filter { it.calificacion != null }.maxByOrNull { it.calificacion!! }?.let { Clave("praeclarum", "${it.titulo} · ${it.calificacion}/10") }
                val fotos = Repo.fotos.values.filter { it.fecha.startsWith(pre) }.sumOf { it.fotos }
                if (fotos > 0) Clave("imagines", "%,d fotos".format(fotos))
                val notif = Repo.notifs.values.filter { it.fecha.startsWith(pre) }.sumOf { it.total }
                if (notif > 0) Clave("monita", "%,d notificaciones".format(notif))
                val hitos = dias.flatMap { it.hitos }.distinct()
                if (hitos.isNotEmpty()) Clave("memorabilia", hitos.take(12).joinToString(", "))
                val feliz = dias.filter { it.valencia != null }.maxByOrNull { it.valencia!! }
                feliz?.let { Clave("dies felix", Fechas.titulo(it.fecha).lowercase()) }
            }
        }

        BotonPrincipal("Guardar imagen del año", Modifier.fillMaxWidth().padding(top = 18.dp)) {
            scope.launch {
                msg = "Dibujando…"
                val uri = withContext(Dispatchers.IO) { runCatching { Reportes.anual(ctx, anio) }.getOrNull() }
                msg = if (uri == null) "No pude guardar la imagen." else "Guardada en tu galería (Pictures/VestigiumMei) y en PDF en Descargas."
                if (uri != null) runCatching { ctx.startActivity(Intent(Intent.ACTION_VIEW).setDataAndType(uri, "image/png").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)) }
            }
        }
        msg?.let { Ayuda(it, Modifier.padding(top = 8.dp)) }
    }
}
