package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.ui.comun.CampoTexto
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.ElementoLista
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Lista
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Deshacer
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaFichas
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.rememberSentir

/** La lista abierta en [PantallaLista]. */
object EdicionLista { var id by mutableStateOf<String?>(null) }

/** Listas que se ofrecen de un toque cuando todavía no hay ninguna. */
private val SUGERIDAS = listOf("Lugares por visitar", "Cosas por comprar", "Restaurantes por probar", "Por aprender")

private fun rango(p: String) = when (p) { "pronto" -> 0; "cuando_pueda" -> 1; "algun_dia" -> 2; else -> 3 }

/** Crea una lista vacía con [nombre], al final de las demás, y la abre. */
private fun crearLista(nav: Nav, nombre: String) {
    val l = Lista(id = Repo.nuevoId(), nombre = nombre.trim(), creada = Fechas.ahoraIso(), orden = (Repo.listas.maxOfOrNull { it.orden } ?: -1) + 1)
    Repo.guardarLista(l); EdicionLista.id = l.id; nav.ir("lista")
}

/**
 * Las listas libres: lo que no es un libro ni una película pero quieres tener a mano (lugares por visitar, cosas por comprar,
 * restaurantes por probar…). No entran al panel ni a los análisis: solo se guardan y se exportan (vm_listas.csv).
 */
@Composable
fun PantallaListas(nav: Nav) {
    var nombre by remember { mutableStateOf("") }
    Hoja("Listas", nav.atras, "Lugares por visitar, cosas por comprar, restaurantes por probar… Lo de leer y ver va en «Quiero leer y ver».") {
        if (Repo.listas.isEmpty()) {
            Vacio("Todavía no tienes listas. Empieza con una de estas o ponle el nombre que quieras.")
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) { SUGERIDAS.forEach { s -> Ficha("＋ $s", false) { crearLista(nav, s) } } }
            }
        } else Tarjeta {
            Repo.listas.forEachIndexed { i, l ->
                if (i > 0) HorizontalDivider()
                val pendientes = l.elementos.count { it.hecho == null }
                Row(Modifier.fillMaxWidth().clickable { EdicionLista.id = l.id; nav.ir("lista") }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(l.nombre, style = MaterialTheme.typography.bodyLarge)
                        Text(listOfNotNull("$pendientes pendientes", l.elementos.count { it.hecho != null }.takeIf { it > 0 }?.let { "$it hechos" }).joinToString(" · "),
                            style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("›", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        Seccion("Una lista nueva") {
            CampoTexto(nombre, { nombre = it.take(60) }, Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("nombre de la lista") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = { if (nombre.isNotBlank()) { crearLista(nav, nombre); nombre = "" } }))
            BotonPrincipal("Crear", Modifier.fillMaxWidth().padding(top = 8.dp), habilitado = nombre.isNotBlank()) { crearLista(nav, nombre); nombre = "" }
            val faltan = SUGERIDAS.filter { s -> Repo.listas.none { it.nombre.equals(s, ignoreCase = true) } }
            if (Repo.listas.isNotEmpty() && faltan.isNotEmpty()) Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) { faltan.forEach { s -> Ficha("＋ $s", false) { crearLista(nav, s) } } }
            }
        }
    }
}

/** Una lista: añadir con solo escribir, tachar lo hecho (guarda la fecha), qué tan pronto, nota y enlace; lo hecho, plegado al final. */
@Composable
fun PantallaLista(nav: Nav) {
    val l = Repo.listas.firstOrNull { it.id == EdicionLista.id }
    if (l == null) { Hoja("Lista", nav.atras) { Vacio("Esta lista ya no existe.") }; return }
    val sentir = rememberSentir()
    var texto by remember { mutableStateOf("") }
    var abierto by remember { mutableStateOf<String?>(null) }
    var confirmar by remember { mutableStateOf(false) }
    fun g(n: Lista) = Repo.guardarLista(n)
    fun cambiar(e: ElementoLista) = g(l.copy(elementos = l.elementos.map { if (it.id == e.id) e else it }))
    fun anadir() {
        val t = texto.trim(); if (t.isEmpty()) return
        g(l.copy(elementos = l.elementos + ElementoLista(id = Repo.nuevoId(), texto = t, agregado = Fechas.diaLogico())))
        sentir(Sentidos.Evento.GUARDAR); texto = ""
    }
    val pendientes = l.elementos.filter { it.hecho == null }.sortedWith(compareBy<ElementoLista> { rango(it.prioridad) }.thenByDescending { it.agregado })
    val hechos = l.elementos.filter { it.hecho != null }.sortedByDescending { it.hecho }
    Hoja(l.nombre, nav.atras, "${pendientes.size} pendientes" + (if (hechos.isNotEmpty()) " · ${hechos.size} hechos" else "")) {
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            CampoTexto(texto, { texto = it.take(200) }, Modifier.weight(1f), singleLine = true, placeholder = { Text("añadir a la lista") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = { anadir() }))
            TextButton(onClick = { anadir() }, enabled = texto.isNotBlank()) { Text("añadir", style = MaterialTheme.typography.labelLarge) }
        }
        if (pendientes.isEmpty()) Vacio(if (hechos.isEmpty()) "La lista está vacía." else "Todo hecho.")
        else Tarjeta {
            pendientes.forEachIndexed { i, e ->
                if (i > 0) HorizontalDivider()
                FilaElemento(e, abierto == e.id, { abierto = if (abierto == e.id) null else e.id }, { cambiar(it) },
                    borrar = { g(l.copy(elementos = l.elementos.filter { it.id != e.id })); Deshacer.ofrecer("Quitado de la lista") { Repo.listas.firstOrNull { it.id == l.id }?.let { x -> Repo.guardarLista(x.copy(elementos = x.elementos + e)) } } })
            }
        }
        if (hechos.isNotEmpty()) Seccion("Hechos", "${hechos.size}", plegable = true, plegadaPorDefecto = true) {
            Tarjeta {
                hechos.forEachIndexed { i, e ->
                    if (i > 0) HorizontalDivider()
                    FilaElemento(e, abierto == e.id, { abierto = if (abierto == e.id) null else e.id }, { cambiar(it) },
                        borrar = { g(l.copy(elementos = l.elementos.filter { it.id != e.id })) })
                }
            }
        }
        Seccion("La lista", plegable = true, plegadaPorDefecto = true) {
            var nombre by remember(l.id) { mutableStateOf(l.nombre) }
            Etiqueta("Nombre")
            CampoTexto(nombre, { nombre = it.take(60); if (it.isNotBlank()) g(l.copy(nombre = it.trim())) }, Modifier.fillMaxWidth(), singleLine = true)
            TextButton(onClick = {
                if (!confirmar) { confirmar = true; return@TextButton }
                Repo.borrarLista(l.id); nav.atras(); Deshacer.ofrecer("Lista borrada") { Repo.guardarLista(l) }
            }, modifier = Modifier.padding(top = 8.dp)) {
                Text(if (confirmar) "¿borrar la lista y todo lo que tiene?" else "borrar la lista", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

/** Un elemento: la casilla para tacharlo, el texto y, al abrirlo, qué tan pronto, nota, enlace y quitar. */
@Composable
private fun FilaElemento(e: ElementoLista, abierto: Boolean, alternar: () -> Unit, cambiar: (ElementoLista) -> Unit, borrar: () -> Unit) {
    val sentir = rememberSentir()
    val uri = LocalUriHandler.current
    val hecho = e.hecho != null
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().clickable(onClick = alternar).padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(if (hecho) "☑" else "☐", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { sentir(Sentidos.Evento.CAMBIO); cambiar(e.copy(hecho = if (hecho) null else Fechas.diaLogico())) }.padding(end = 10.dp))
            Column(Modifier.weight(1f)) {
                Text(e.texto, style = MaterialTheme.typography.bodyLarge, textDecoration = if (hecho) TextDecoration.LineThrough else null,
                    color = if (hecho) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface, maxLines = if (abierto) 6 else 2, overflow = TextOverflow.Ellipsis)
                val detalle = listOfNotNull(Catalogo.nombrePrioridad(e.prioridad)?.lowercase(), e.nota.ifBlank { null }?.lines()?.first()?.take(60),
                    e.hecho?.let { "hecho el ${Fechas.corto(it)}" }).joinToString(" · ")
                if (detalle.isNotBlank()) Text(detalle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        if (abierto) Column(Modifier.padding(start = 30.dp, bottom = 8.dp)) {
            CampoTexto(e.texto, { if (it.isNotBlank()) cambiar(e.copy(texto = it.take(200))) }, Modifier.fillMaxWidth(), singleLine = true)
            Etiqueta("Qué tan pronto")
            FilaFichas(Catalogo.PRIORIDADES_DESEO, e.prioridad.ifBlank { null }) { cambiar(e.copy(prioridad = it.orEmpty())) }
            Etiqueta("Nota")
            CampoTexto(e.nota, { cambiar(e.copy(nota = it.take(500))) }, Modifier.fillMaxWidth(), minLines = 2)
            Etiqueta("Enlace")
            CampoTexto(e.enlace, { cambiar(e.copy(enlace = it.trim())) }, Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("https://…") })
            Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (e.enlace.startsWith("http")) BotonSecundario("abrir enlace") { runCatching { uri.openUri(e.enlace) } }
                Ayuda("Agregado el ${Fechas.corto(e.agregado)}", Modifier.weight(1f))
                TextButton(onClick = borrar) { Text("quitar", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge) }
            }
        }
    }
}
