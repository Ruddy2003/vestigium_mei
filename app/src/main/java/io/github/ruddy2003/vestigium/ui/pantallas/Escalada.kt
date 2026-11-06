@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Ascenso
import io.github.ruddy2003.vestigium.data.Bloque
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Grados
import io.github.ruddy2003.vestigium.data.Opcion
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Sesion
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Clave
import io.github.ruddy2003.vestigium.ui.Editor
import io.github.ruddy2003.vestigium.ui.Escala
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaFichas
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Tonos
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.esOscuro
import io.github.ruddy2003.vestigium.ui.barraGrafica
import io.github.ruddy2003.vestigium.ui.estiloCifras
import io.github.ruddy2003.vestigium.ui.estiloGrafica
import io.github.ruddy2003.vestigium.ui.lineaGrafica
import io.github.ruddy2003.vestigium.ui.puntoGrafica
import io.github.ruddy2003.vestigium.ui.rejillaGrafica
import androidx.health.connect.client.records.ExerciseSessionRecord
import java.time.LocalDate
import io.github.ruddy2003.vestigium.ui.elegirFecha

/** Sesión que se está editando (la elige la lista y la lee el editor). */
object EdicionEscalada { var id by mutableStateOf<String?>(null) }

private val MODALIDADES = listOf(Opcion("boulder", "Boulder"), Opcion("cuerda", "Cuerda"), Opcion("boulder_roca", "Boulder en roca"), Opcion("cuerda_roca", "Vía en roca"))

private fun maxGrado(a: Ascenso): Pair<Double, String>? =
    a.bloques.filter { Grados.encadenado(it.estilo) }.mapNotNull { b -> Grados.valor(b.grado)?.let { it to b.grado } }.maxByOrNull { it.first }

@Composable
fun PantallaEscalada(nav: Nav) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        // Solo se limpian las sesiones que quedaron del todo vacías (abiertas y sin nada): antes se borraban aunque tuvieran
        // lugar, duración, piel, esfuerzo, satisfacción o dolor.
        Repo.ascensos.filter { it.bloques.isEmpty() && it.nota.isBlank() && it.sesion_reloj == null && it.energia == null && it.lugar.isBlank() &&
            it.min == null && it.piel == null && it.esfuerzo == null && it.satisfaccion == null && it.dolor.isBlank() }.forEach { Repo.borrarAscenso(it.id) }
    }
    val sesiones = Repo.ascensos.sortedByDescending { it.fecha + (it.inicio ?: "") }
    val enlazadas = Repo.ascensos.mapNotNull { it.sesion_reloj }.toSet()
    val delReloj = Repo.sesiones.filter { it.tipo == ExerciseSessionRecord.EXERCISE_TYPE_ROCK_CLIMBING && it.id !in enlazadas }.sortedByDescending { it.inicio }

    fun nueva(desde: Sesion? = null) {
        val previa = Repo.ascensos.maxByOrNull { it.fecha }
        val a = Ascenso(
            id = Repo.nuevoId(), fecha = desde?.fecha ?: Fechas.diaLogico(), lugar = previa?.lugar ?: "", modalidad = previa?.modalidad ?: "boulder",
            inicio = desde?.inicio ?: Fechas.ahoraIso(), min = desde?.min, sesion_reloj = desde?.id,
        )
        Repo.guardarAscenso(a); EdicionEscalada.id = a.id; nav.ir("ascenso")
    }

    Hoja("Escalada", nav.atras, "Bitácora de sesiones: cada bloque con su grado, estilo e intentos. Así se ve tu progreso real, no el que recuerdas.") {
        BotonPrincipal("Nueva sesión", Modifier.fillMaxWidth().padding(top = 12.dp)) { nueva() }
        delReloj.take(3).forEach { s ->
            Tarjeta(Modifier.padding(top = 8.dp)) {
                Text("Tu reloj registró escalada el ${Fechas.corto(s.fecha)} · ${s.min} min" + (s.fc_media?.let { " · %.0f lpm".format(it) } ?: ""), style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = { nueva(s) }) { Text("+ anotar los bloques de esa sesión", style = MaterialTheme.typography.labelLarge) }
            }
        }
        if (sesiones.isEmpty()) { Spacer(Modifier.height(12.dp)); Vacio("Aún no hay sesiones. Al terminar de escalar, anota cada bloque que intentaste."); return@Hoja }

        val todos = sesiones.flatMap { a -> a.bloques.map { a to it } }
        val enc = todos.filter { Grados.encadenado(it.second.estilo) }
        val maxTotal = enc.mapNotNull { (_, b) -> Grados.valor(b.grado)?.let { it to b.grado } }.maxByOrNull { it.first }
        val hace30 = Fechas.sumar(Fechas.diaLogico(), -30)
        val max30 = enc.filter { it.first.fecha >= hace30 }.mapNotNull { (_, b) -> Grados.valor(b.grado)?.let { it to b.grado } }.maxByOrNull { it.first }
        val primeros = todos.filter { it.second.estilo == "flash" || it.second.estilo == "a_vista" }.size

        Seccion("Progreso") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Cifra("${sesiones.size}", "sesiones", Modifier.weight(1f))
                Cifra(maxTotal?.second ?: "—", "máximo", Modifier.weight(1f))
                Cifra(max30?.second ?: "—", "últimos 30 días", Modifier.weight(1f))
            }
            Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Cifra("${enc.size}", "encadenados", Modifier.weight(1f))
                Cifra(if (todos.isEmpty()) "—" else "${primeros * 100 / todos.size}%", "a la primera", Modifier.weight(1f))
                Cifra("%.1f".format(todos.sumOf { it.second.intentos }.toDouble() / maxOf(1, todos.size)), "intentos/bloque", Modifier.weight(1f))
            }
            Etiqueta("grado máximo por sesión")
            GraficaGrados(sesiones.sortedBy { it.fecha }.takeLast(40))
            Etiqueta("pirámide · bloques encadenados por grado")
            Piramide(enc.map { it.second })
        }

        Seccion("Sesiones") {
            Tarjeta {
                sesiones.forEachIndexed { i, a ->
                    if (i > 0) HorizontalDivider()
                    Row(Modifier.fillMaxWidth().clickable { EdicionEscalada.id = a.id; nav.ir("ascenso") }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(Fechas.corto(a.fecha) + (if (a.lugar.isNotBlank()) " · ${a.lugar}" else ""), style = MaterialTheme.typography.bodyLarge)
                            Text("${porNumero(a.bloques.size, "1 bloque", "${a.bloques.size} bloques")} · ${a.bloques.count { Grados.encadenado(it.estilo) }} encadenados" + (a.min?.let { " · $it min" } ?: ""),
                                style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(maxGrado(a)?.second ?: "—", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun GraficaGrados(sesiones: List<Ascenso>) {
    val puntos = sesiones.mapNotNull { a -> maxGrado(a)?.first }
    if (puntos.size < 2) { Ayuda("Con dos sesiones o más aparece la gráfica."); return }
    val linea = MaterialTheme.colorScheme.primary; val rejilla = MaterialTheme.colorScheme.outline; val tenue = MaterialTheme.colorScheme.onSurfaceVariant
    val eg = estiloGrafica()
    val lo = kotlin.math.floor(puntos.min()); val hi = kotlin.math.ceil(puntos.max()).coerceAtLeast(lo + 1)
    Row {
        Column(Modifier.height(140.dp).padding(end = 6.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text(Grados.nombre(hi, sesiones.lastOrNull()?.bloques?.firstOrNull()?.grado), style = estiloCifras(), color = tenue)
            Text(Grados.nombre(lo, sesiones.lastOrNull()?.bloques?.firstOrNull()?.grado), style = estiloCifras(), color = tenue)
        }
        Canvas(Modifier.weight(1f).height(140.dp)) {
            rejillaGrafica(eg, (0..4).map { k -> size.height * k / 4 }, rejilla)
            val pts = puntos.mapIndexed { i, v -> Offset(size.width * i / (puntos.size - 1), (size.height * (1 - (v - lo) / (hi - lo))).toFloat()) }
            lineaGrafica(eg, pts, linea, 4f)
            pts.forEach { puntoGrafica(eg, it, 5f, linea) }
        }
    }
}

@Composable
private fun Piramide(bloques: List<Bloque>) {
    val porGrado = bloques.mapNotNull { b -> Grados.valor(b.grado)?.let { kotlin.math.round(it) to b.grado } }.groupBy { it.first }
    if (porGrado.isEmpty()) { Ayuda("Todavía no hay bloques encadenados."); return }
    val maxN = porGrado.values.maxOf { it.size }
    val oscuro = esOscuro()
    val eg = estiloGrafica()
    val lo = porGrado.keys.min(); val hi = porGrado.keys.max()
    Column {
        var g = hi
        while (g >= lo) {
            val l = porGrado[g].orEmpty()
            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                Text((l.firstOrNull()?.second ?: Grados.nombre(g, bloques.firstOrNull()?.grado)).padEnd(5), style = MaterialTheme.typography.labelLarge, modifier = Modifier.width(52.dp))
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    val f = l.size.toFloat() / maxN
                    if (l.isNotEmpty()) {
                        val tono = Tonos.secuencial(0.3f + 0.7f * ((g - lo) / maxOf(1.0, hi - lo)).toFloat(), oscuro)
                        Canvas(Modifier.fillMaxWidth(f.coerceAtLeast(0.04f)).height(16.dp)) { barraGrafica(eg, Offset.Zero, size, tono, horizontal = true) }
                    }
                }
                Text("${l.size}", style = estiloCifras(MaterialTheme.typography.labelMedium), modifier = Modifier.width(30.dp).padding(start = 6.dp))
            }
            g -= 1.0
        }
    }
}

@Composable
fun PantallaAscenso(nav: Nav) {
    val ctx = LocalContext.current
    val id = EdicionEscalada.id
    val a = Repo.ascensos.firstOrNull { it.id == id }
    if (a == null) { Hoja("Escalada", nav.atras) { Vacio("No encontré esa sesión.") }; return }
    fun g(n: Ascenso) = Repo.guardarAscenso(n)
    var abierto by remember(a.id) { mutableStateOf<Int?>(null) }
    var confirmar by remember(a.id) { mutableStateOf(false) }
    val escala = Repo.config.escalada_escala
    val esCuerda = a.modalidad.startsWith("cuerda")
    val grados = if (esCuerda) Grados.YDS else if (escala == "Font") Grados.FONT else Grados.V
    val gimnasios = (Repo.config.gimnasios + Repo.ascensos.map { it.lugar }).filter { it.isNotBlank() }.distinct().take(8)

    // «Nueva sesión» solo mientras está vacía; después, la sesión de su fecha.
    val vacia = a.bloques.isEmpty() && a.nota.isBlank() && a.lugar.isBlank() && a.min == null && a.energia == null && a.esfuerzo == null
    Hoja(if (vacia) "Nueva sesión" else "Sesión del ${Fechas.corto(a.fecha)}", nav.atras, latin = "EXERCITATIO", subtitulo = "Se guarda sola mientras escribes.") {
        Seccion("Cuándo y dónde", numero = 1, latin = "QVANDO ET VBI") {
            Tarjeta {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Clave("dies", Fechas.titulo(a.fecha).lowercase(), ancho = 6)
                }
                BotonSecundario("cambiar fecha", Modifier.padding(top = 4.dp)) {
                    elegirFecha(ctx, a.fecha) { g(a.copy(fecha = it)) }
                }
                Etiqueta("Lugar")
                OutlinedTextField(a.lugar, { g(a.copy(lugar = it)) }, Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("gimnasio o zona", style = MaterialTheme.typography.labelLarge) })
                if (gimnasios.isNotEmpty()) FlowRow(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    gimnasios.forEach { l -> Ficha(l, a.lugar == l) { g(a.copy(lugar = l)) } }
                }
                Etiqueta("Modalidad")
                FilaFichas(MODALIDADES, a.modalidad) { g(a.copy(modalidad = it ?: "boulder")) }
                Etiqueta("Duración en minutos")
                OutlinedTextField(a.min?.toString() ?: "", { t -> g(a.copy(min = t.filter { it.isDigit() }.take(4).toIntOrNull())) }, Modifier.width(140.dp), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                a.sesion_reloj?.let { s -> Repo.sesiones.firstOrNull { it.id == s }?.let { x -> Clave("horologium", "${x.min} min" + (x.fc_media?.let { " · fc media %.0f".format(it) } ?: "") + (x.fc_max?.let { " · máx %.0f".format(it) } ?: "")) } }
            }
        }

        Seccion("Bloques", "Toca un grado para añadir un bloque; tócalo en la lista para ajustar estilo, intentos y tipo.", numero = 2) {
            if (!esCuerda) Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Ficha("escala V", escala == "V") { Repo.cambiarConfig { it.copy(escalada_escala = "V") } }
                Ficha("Fontainebleau", escala == "Font") { Repo.cambiarConfig { it.copy(escalada_escala = "Font") } }
            }
            FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                grados.forEach { gr -> Ficha(gr, false) { g(a.copy(bloques = a.bloques + Bloque(grado = gr))); abierto = a.bloques.size } }
            }
            if (a.bloques.isNotEmpty()) Tarjeta(Modifier.padding(top = 10.dp)) {
                a.bloques.forEachIndexed { i, b ->
                    if (i > 0) HorizontalDivider()
                    fun cambiarB(n: Bloque) = g(a.copy(bloques = a.bloques.mapIndexed { j, x -> if (j == i) n else x }))
                    Row(Modifier.fillMaxWidth().clickable { abierto = if (abierto == i) null else i }.padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${i + 1}".padStart(2, '0') + "  ", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(b.grado, style = MaterialTheme.typography.titleLarge, color = if (Grados.encadenado(b.estilo)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(72.dp))
                        Text((Grados.ESTILOS.firstOrNull { it.id == b.estilo }?.nombre ?: b.estilo).lowercase() + " · ${b.intentos} int." +
                            (if (b.angulo.isNotBlank()) " · ${b.angulo}" else "") + (if (b.rasgos.isNotEmpty()) " · " + b.rasgos.joinToString(", ") { Grados.nombreRasgo(it).lowercase() } else ""),
                            style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                        Text(if (abierto == i) "−" else "+", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                    if (abierto == i) Column(Modifier.padding(bottom = 10.dp)) {
                        FilaFichas(Grados.ESTILOS, b.estilo) { cambiarB(b.copy(estilo = it ?: "encadenado", intentos = if (it == "flash" || it == "a_vista") 1 else b.intentos)) }
                        Grados.ESTILOS.firstOrNull { it.id == b.estilo }?.detalle?.takeIf { it.isNotBlank() }?.let { Ayuda(it, Modifier.padding(top = 4.dp)) }
                        Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("intentos = ", style = MaterialTheme.typography.labelLarge)
                            BotonSecundario("−") { cambiarB(b.copy(intentos = (b.intentos - 1).coerceAtLeast(1))) }
                            Text("${b.intentos}", style = MaterialTheme.typography.titleLarge)
                            BotonSecundario("+") { cambiarB(b.copy(intentos = b.intentos + 1, estilo = if (b.estilo == "flash" || b.estilo == "a_vista") "encadenado" else b.estilo)) }
                        }
                        Etiqueta("Ángulo"); FilaFichas(Grados.ANGULOS, b.angulo.ifBlank { null }) { cambiarB(b.copy(angulo = it ?: "")) }
                        Etiqueta("Qué tenía")
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Grados.RASGOS.forEach { r -> val on = r in b.rasgos; Ficha(Grados.nombreRasgo(r), on) { cambiarB(b.copy(rasgos = if (on) b.rasgos - r else b.rasgos + r)) } }
                        }
                        OutlinedTextField(b.nota, { cambiarB(b.copy(nota = it)) }, Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true,
                            placeholder = { Text("nota: color, nombre, beta…", style = MaterialTheme.typography.labelLarge) })
                        TextButton(onClick = {
                            val antes = a
                            g(a.copy(bloques = a.bloques.filterIndexed { j, _ -> j != i })); abierto = null
                            io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Bloque quitado") { g(antes) }
                        }) {
                            Text("quitar este bloque", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }

        Seccion("Cómo te sentiste", numero = 3) {
            Tarjeta {
                Etiqueta("Energía al empezar"); Escala(a.energia, 1, 5, mapOf(1 to "agotado", 3 to "normal", 5 to "con todo")) { g(a.copy(energia = it)) }
                Etiqueta("Piel de los dedos"); Escala(a.piel, 1, 5, mapOf(1 to "destrozada", 3 to "aguanta", 5 to "perfecta")) { g(a.copy(piel = it)) }
                Etiqueta("Esfuerzo de la sesión (1 a 10)"); Escala(a.esfuerzo, 1, 10, mapOf(1 to "paseo", 5 to "moderado", 8 to "duro", 10 to "al límite")) { g(a.copy(esfuerzo = it)) }
                Etiqueta("Satisfacción"); Escala(a.satisfaccion, 1, 5, mapOf(1 to "frustrado", 3 to "bien", 5 to "increíble")) { g(a.copy(satisfaccion = it)) }
                Etiqueta("Dolor o lesión")
                OutlinedTextField(a.dolor, { g(a.copy(dolor = it)) }, Modifier.fillMaxWidth(), minLines = 2, placeholder = { Text("dedo anular izquierdo, hombro…", style = MaterialTheme.typography.labelLarge) })
                Etiqueta("Notas")
                Editor(a.nota, "ascensus_${a.fecha}.txt", "Qué funcionó, qué no, con quién fuiste…", minimo = 90) { g(a.copy(nota = it)) }
            }
        }
        TextButton(onClick = { if (confirmar) { Repo.borrarAscenso(a.id); nav.atras(); io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Sesión borrada") { Repo.guardarAscenso(a) } } else confirmar = true }, modifier = Modifier.padding(top = 16.dp)) {
            Text(if (confirmar) "¿borrar la sesión de verdad?" else "borrar sesión", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
        }
    }
}
