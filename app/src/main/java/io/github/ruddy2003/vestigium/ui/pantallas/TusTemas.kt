@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Presencias
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.data.TusTemas
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Deshacer
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaMenu
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TextoMarcado
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.Dialogo

/**
 * Tus #temas en orden: cuántos días aparece cada uno, los que parecen el mismo escrito distinto, los de un solo día
 * (quizá eran hitos) y los que en realidad son sentimientos. Unir no toca tus textos; renombrar sí, con vista previa
 * y «deshacer».
 */
@Composable
fun PantallaTusTemas(nav: Nav) {
    val c = Repo.config
    val lista = remember(Repo.dias, Repo.momentos, Repo.tramos, Repo.consumos, Repo.cartas, c.equivalencias) { TusTemas.lista() }
    val pares = remember(lista, c.temas_distintos) { TusTemas.parecidos() }
    val unDia = remember(lista) { TusTemas.deUnDia() }
    val sentimientos = remember(lista) { TusTemas.deSentimientos() }
    var editando by remember { mutableStateOf<TusTemas.Tema?>(null) }
    var verTodos by remember { mutableIntStateOf(25) }
    var aviso by remember { mutableStateOf<String?>(null) }
    fun hecho(texto: String, antes: TusTemas.Antes) { aviso = null; Deshacer.ofrecer(texto) { TusTemas.restaurar(antes) } }

    Hoja("Tus temas", nav.atras, "Cada #tema con los días en que aparece. Un mismo tema escrito de dos formas cuenta como dos en los análisis: aquí los juntas.") {
        aviso?.let { Ayuda(it, Modifier.padding(top = 8.dp)) }
        if (lista.isEmpty()) Vacio("Todavía no usas #temas. Escribe #tesis, #familia o #dinero en el diario o en un momento y aparecerán aquí.")

        if (pares.isNotEmpty()) Seccion("¿Es el mismo tema?", "Se parecen. «Unir» los cuenta juntos en los análisis sin tocar tus textos; «renombrar» los corrige en todos tus textos.") {
            pares.take(8).forEach { (a, b) ->
                Tarjeta(Modifier.padding(bottom = 8.dp)) {
                    TextoMarcado("#$a  y  #$b", style = MaterialTheme.typography.titleSmall)
                    FlowRow(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Ficha("unir en #$a", false) { TusTemas.unir(b, a) }
                        Ficha("renombrar #$b → #$a", false) { val (n, antes) = TusTemas.renombrar(b, a); hecho("Renombré #$b en $n textos.", antes) }
                        Ficha("son distintos", false) { TusTemas.distintos(a, b) }
                    }
                }
            }
        }

        if (sentimientos.isNotEmpty()) Seccion("Sentimientos con #", "Lo que sentiste va mejor en los botones de «¿Cómo estás ahora?»: así queda con su hora y se puede cruzar con todo. Quitar el # deja la palabra en el texto.") {
            Tarjeta {
                sentimientos.forEach { t ->
                    FilaMenu("#${t.nombre}", "${t.dias} ${if (t.dias == 1) "día" else "días"} · quitar el # (queda «${t.nombre.removePrefix("sentimiento_").removePrefix("sentimientos_").replace('_', ' ')}»)") {
                        val (n, antes) = TusTemas.quitarAlmohadilla(t.nombre); hecho("Quité el # de #${t.nombre} en $n textos.", antes)
                    }
                }
            }
        }

        if (unDia.isNotEmpty()) Seccion("Aparecen un solo día", "Un tema que pasa una vez en la vida quizá es un hito (un vuelo, una mudanza). Pasarlo a hitos lo guarda en ese día y le quita el #.") {
            Tarjeta {
                unDia.take(12).forEach { t ->
                    FilaMenu("#${t.nombre}", "${Fechas.corto(t.primera)} · pasar a hitos") { val (n, antes) = TusTemas.aHito(t); hecho("#${t.nombre} ahora es un hito ($n textos).", antes) }
                }
            }
        }

        if (lista.isNotEmpty()) Seccion("Todos", "${lista.size} temas, del más usado al menos. Toca uno para renombrarlo o unirlo con otro.") {
            Tarjeta {
                lista.take(verTodos).forEach { t ->
                    val unidos = c.equivalencias.filterValues { it == t.nombre }.keys
                    FilaMenu("#${t.nombre}", "${t.dias} ${if (t.dias == 1) "día" else "días"} · desde ${Fechas.corto(t.primera)}" +
                        (if (t.ultima != t.primera) " · última vez ${Fechas.corto(t.ultima)}" else "") + if (unidos.isNotEmpty()) " · incluye " + unidos.joinToString(", ") { "#$it" } else "") { editando = t }
                }
            }
            if (lista.size > verTodos) BotonSecundario("ver más", Modifier.padding(top = 8.dp)) { verTodos += 50 }
        }
    }

    editando?.let { t ->
        var nuevo by remember(t.nombre) { mutableStateOf(t.nombre) }
        val destino = Texto.etiqueta(nuevo)
        val previa = remember(t.nombre, destino) { if (destino.isNotBlank() && destino != t.nombre) TusTemas.vistaPrevia(t.nombre, destino) else null }
        val existe = lista.any { it.nombre == destino && destino != t.nombre }
        Dialogo(onDismissRequest = { editando = null },
            title = { TextoMarcado("#${t.nombre}", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Ayuda("${t.dias} días · de ${Fechas.corto(t.primera)} a ${Fechas.corto(t.ultima)}")
                    OutlinedTextField(nuevo, { nuevo = it }, Modifier.fillMaxWidth().padding(top = 10.dp), singleLine = true, label = { Text("nuevo nombre") }, prefix = { Text("#") })
                    previa?.let { (n, ej) ->
                        Ayuda("Cambiaría $n ${if (n == 1) "texto" else "textos"}." + if (existe) " #$destino ya existe: quedan juntos." else "", Modifier.padding(top = 8.dp))
                        ej?.let { (a, b) ->
                            Ayuda("antes:", Modifier.padding(top = 6.dp)); TextoMarcado("…$a…", style = MaterialTheme.typography.bodySmall)
                            Ayuda("después:", Modifier.padding(top = 4.dp)); TextoMarcado("…$b…", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    if (existe) BotonSecundario("solo unir en #$destino (sin tocar tus textos)", Modifier.fillMaxWidth().padding(top = 10.dp)) { TusTemas.unir(t.nombre, destino); editando = null }
                }
            },
            confirmButton = {
                TextButton(enabled = previa != null, onClick = {
                    val (n, antes) = TusTemas.renombrar(t.nombre, destino); editando = null; hecho("Renombré #${t.nombre} → #$destino en $n textos.", antes)
                }) { Text("renombrar en todo") }
            },
            dismissButton = { TextButton(onClick = { editando = null }) { Text("cancelar") } })
    }
}

/**
 * Los días con personas que todavía no revisaste: para cada @persona, si estuviste con ella, solo pensaste en ella
 * o hablaron por mensaje. Lo de antes de la versión 10 cuenta como «estuve» hasta que lo cambies.
 */
@Composable
fun PantallaPresencias(nav: Nav) {
    var todos by remember { mutableStateOf(false) }
    // La lista se fija al abrir (o al cambiar de vista): tocar un nombre no quita la tarjeta; solo «así está bien» la da por revisada.
    val reabiertos = remember { Presencias.paraReabrir().map { it.fecha }.toSet() }
    androidx.compose.runtime.LaunchedEffect(Unit) { Presencias.reabrirUnaVez() }
    val lista = remember(todos) {
        if (todos) Presencias.conPersonas() else (Presencias.porRevisar() + Presencias.conPersonas().filter { it.fecha in reabiertos }).distinctBy { it.fecha }.sortedByDescending { it.fecha }
    }
    var listos by remember { mutableStateOf(emptySet<String>()) }
    val pendientes = lista.filter { it.fecha !in listos }
    var cuantos by remember { mutableIntStateOf(20) }
    Hoja("¿Estuviste o pensaste?", nav.atras, "Ver a alguien no es lo mismo que pensar en esa persona. Toca cada nombre para cambiar entre «estuve con», «pensé en» y «mensajes». Lo que no cambies se queda como «estuve con».") {
        Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("por revisar", !todos) { todos = false; listos = emptySet() }
            Ficha("todos los días", todos) { todos = true; listos = emptySet() }
        }
        if (pendientes.isEmpty()) Vacio(if (todos) "No hay días con personas en el diario." else "No hay días por revisar.")
        else {
            Ayuda("${pendientes.size} días por revisar. No hace falta hacerlo todo: sirve más revisar los días en que pensaste en alguien sin verlo.", Modifier.padding(top = 8.dp))
            BotonSecundario("dejar todos como están", Modifier.padding(top = 8.dp)) {
                Repo.importarDias(pendientes.map { (Repo.dias[it.fecha] ?: it).copy(presencias_ok = true) }, sobrescribir = true)
                listos = listos + pendientes.map { it.fecha }
            }
        }
        pendientes.take(cuantos).forEach { d0 ->
            // El día como está ahora (con lo que ya tocaste), aunque la lista se haya fijado al abrir.
            val d = Repo.dias[d0.fecha] ?: d0
            Tarjeta(Modifier.padding(top = 10.dp)) {
                Text(Fechas.titulo(d.fecha) + if (todos && d.presencias_ok) " · revisado" else "", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                TextoMarcado(d.nota.trim().take(220) + if (d.nota.length > 220) "…" else "", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                ChipsPresencia(d, revisado = false) { n -> Repo.importarDias(listOf(n), sobrescribir = true) }
                TextButton(onClick = { Repo.importarDias(listOf(Repo.dia(d.fecha).copy(presencias_ok = true)), sobrescribir = true); listos = listos + d.fecha }) {
                    Text("así está bien", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        if (pendientes.size > cuantos) BotonPrincipal("ver más días", Modifier.fillMaxWidth().padding(top = 10.dp)) { cuantos += 20 }
    }
}

/** Una ficha por @persona del diario de ese día; cada toque cambia entre «estuve con», «pensé en» y «mensajes». */
@Composable
fun ChipsPresencia(d: io.github.ruddy2003.vestigium.data.Dia, revisado: Boolean = true, onCambio: (io.github.ruddy2003.vestigium.data.Dia) -> Unit) {
    val eq = Repo.equivalencias()
    val personas = Texto.personas(d.nota).distinct()
    if (personas.isEmpty()) return
    FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        personas.forEach { p ->
            val e = Presencias.enDiario(d, p, eq)
            Ficha("${Presencias.nombre(e)} @$p", e != Presencias.ESTUVE) { onCambio(Presencias.con(d, p, Presencias.siguiente(e), revisado)) }
        }
    }
}
