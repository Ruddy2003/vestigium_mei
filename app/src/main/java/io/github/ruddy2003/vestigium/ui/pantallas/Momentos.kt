@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.sensores.Ubicacion
import io.github.ruddy2003.vestigium.ui.Aviso
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.Escala
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.QuickActivity
import io.github.ruddy2003.vestigium.ui.Reanudar
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.aMillis

@Composable
fun PantallaMomentos(nav: Nav) {
    val ctx = LocalContext.current
    val discreto = Repo.config.discreto
    var editando by remember { mutableStateOf<Momento?>(null) }
    @Suppress("UNUSED_VARIABLE") val r = Reanudar.n
    val conUbicacion = Ubicacion.tienePermiso(ctx)

    fun abrir(modo: String, tipo: String? = null) {
        val i = Intent(ctx, QuickActivity::class.java).putExtra(QuickActivity.MODO, modo).putExtra(QuickActivity.ORIGEN, "app")
        if (tipo != null) i.putExtra(QuickActivity.TIPO, tipo)
        ctx.startActivity(i)
    }

    Hoja("Momentos", subtitulo = "Lo que piensas o sientes en el instante. Un toque y se guarda con la hora y el lugar.") {
        if (!conUbicacion) Aviso("Sin permiso de ubicación, los momentos se guardan sin lugar. Actívalo en Más → Permisos y sensores.")
        BotonAnimo(Modifier.padding(top = 14.dp)) { abrir(QuickActivity.M_ANIMO) }
        Ayuda("También puedes agregar los widgets de Vestigium a tu pantalla de inicio o el botón al panel de ajustes rápidos.", Modifier.padding(top = 8.dp))

        val recientes = Repo.momentos.filter { it.fecha >= Fechas.sumar(Fechas.diaLogico(), -30) && (Repo.config.seguimiento_privado || it.tipo != "recaida") }.sortedByDescending { it.ts }
        Seccion("Últimos 30 días", "${recientes.size} registros. Toca uno para editarlo.") {
            if (recientes.isEmpty()) Vacio("Todavía no hay momentos. Toca «¿Cómo estás ahora?» cuando algo te pase por la cabeza.")
            var ultimoDia = ""
            recientes.take(200).forEach { m ->
                if (m.fecha != ultimoDia) {
                    ultimoDia = m.fecha
                    Text(Fechas.corto(m.fecha).uppercase(), fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 14.dp, bottom = 4.dp))
                }
                Surface(onClick = { editando = m }, color = MaterialTheme.colorScheme.background) {
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
                        Text(Fechas.horaDe(m.ts), fontFamily = io.github.ruddy2003.vestigium.ui.Letra.codigo, fontSize = 13.sp, modifier = Modifier.padding(end = 10.dp))
                        Column(Modifier.weight(1f)) {
                            val extra = listOfNotNull(m.valencia?.let { "valencia $it" }, m.activacion?.let { "energía $it" }, m.intensidad?.let { "intensidad $it" }).joinToString(" · ")
                            Text(Catalogo.titulo(m, discreto) + if (extra.isNotEmpty()) " · $extra" else "", style = MaterialTheme.typography.titleMedium)
                            if (m.texto.isNotBlank()) io.github.ruddy2003.vestigium.ui.TextoMarcado(m.texto, style = MaterialTheme.typography.bodyMedium)
                            val lugar = Repo.lugares.firstOrNull { it.id == m.lugar }?.nombre
                            Ayuda(listOfNotNull(lugar, if (m.lat != null && lugar == null) "con ubicación" else null, m.origen).joinToString(" · "))
                        }
                    }
                }
            }
        }
    }

    editando?.let { m -> DialogoMomento(m) { editando = null } }
}



/** Editar o borrar un momento (desde Momentos o desde la línea de tiempo del Diario). */
@Composable
fun DialogoMomento(m: Momento, cerrar: () -> Unit) {
    val discreto = Repo.config.discreto
    var texto by remember(m.id) { mutableStateOf(m.texto) }
    var intensidad by remember(m.id) { mutableStateOf(m.intensidad) }
    var valencia by remember(m.id) { mutableStateOf(m.valencia) }
    var activacion by remember(m.id) { mutableStateOf(m.activacion) }
    var emociones by remember(m.id) { mutableStateOf(Catalogo.emocionesDe(m)) }
    var pensamiento by remember(m.id) { mutableStateOf(Catalogo.pensamientoDe(m)) }
    var confirmar by remember(m.id) { mutableStateOf(false) }
    val registro = Catalogo.esRegistro(m)
    // En una recaída también se corrigen la hora de inicio y cuánto duró (con los selectores de siempre).
    val esRecaida = m.tipo == "recaida"
    var cuando by remember(m.id) { mutableStateOf<java.time.LocalDateTime?>(io.github.ruddy2003.vestigium.ui.horaLocal(Fechas.millis(m.ts))) }
    var dur by remember(m.id) { mutableStateOf(m.duracion_min) }
    AlertDialog(
        onDismissRequest = { cerrar() },
        title = { Text("${Catalogo.titulo(m, discreto)} · ${Fechas.horaDe(m.ts)}") },
        text = {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            OutlinedTextField(value = texto, onValueChange = { texto = it }, modifier = Modifier.fillMaxWidth(), minLines = 2, visualTransformation = io.github.ruddy2003.vestigium.ui.rememberMarcas())
            if (registro) {
                Etiqueta("Qué tan agradable (1 a 7)"); Escala(valencia, 1, 7) { valencia = it }
                Etiqueta("Energía (1 a 7)"); Escala(activacion, 1, 7) { activacion = it }
                Etiqueta("Qué sentías")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    (Catalogo.SENTIMIENTOS.map { it.id } + Catalogo.NO_SE).forEach { id ->
                        val a = id in emociones
                        io.github.ruddy2003.vestigium.ui.Ficha(Catalogo.nombreSentimiento(id, discreto), a) { emociones = if (a) emociones - id else emociones + id }
                    }
                }
                Etiqueta("En qué pensabas")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Catalogo.PENSAMIENTOS.forEach { o -> io.github.ruddy2003.vestigium.ui.Ficha(if (discreto) "·" else o.nombre, pensamiento == o.id) { pensamiento = if (pensamiento == o.id) null else o.id } }
                }
            } else { Etiqueta("Intensidad"); Escala(intensidad, 1, 5) { intensidad = it } }
            if (esRecaida) {
                Etiqueta("Empezó")
                io.github.ruddy2003.vestigium.ui.SelectorHora(cuando, atajos = listOf(15, 30, 60, 120)) { cuando = it }
                Etiqueta("Duró")
                io.github.ruddy2003.vestigium.ui.SelectorDuracion(dur) { dur = it }
                Ayuda("Si cambias la hora, el lugar se toma de tus ubicaciones de esa hora.", Modifier.padding(top = 4.dp))
            }
        }
        },
        confirmButton = { TextButton(onClick = {
            val n = m.copy(texto = texto, intensidad = intensidad, valencia = valencia, activacion = activacion,
                emociones = if (registro) emociones else m.emociones, pensamiento = if (registro) pensamiento else m.pensamiento,
                duracion_min = if (esRecaida) dur else m.duracion_min)
            val ms = if (esRecaida) (cuando ?: java.time.LocalDateTime.now()).aMillis() else null
            if (ms != null && Fechas.horaDe(Fechas.isoDeMillis(ms)) != Fechas.horaDe(m.ts)) io.github.ruddy2003.vestigium.data.Recaidas.moverHora(n, ms)
            else Repo.guardarMomento(n)
            cerrar()
        }) { Text("Guardar") } },
        dismissButton = {
        TextButton(onClick = {
            if (confirmar) {
                Repo.borrarMomento(m.id); cerrar()
                io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Momento borrado") { Repo.guardarMomento(m) }
            } else confirmar = true
        }) {
            Text(if (confirmar) "¿Borrar de verdad?" else "Borrar", color = MaterialTheme.colorScheme.error)
        }
        },
    )
}
