@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Red
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.historia.Efemerides
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate

// =====================================================================
//  «Hoy en la historia» (plan de historia y escenas, sección 3.6; tanda H1, 13 nov 2026)
// =====================================================================
//
// Lo que pasó un día como hoy, de Wikipedia en español. Se llega manteniendo presionado el cuadro de Hoy y desde el
// buscador. Solo sale del teléfono el mes y el día ([Efemerides]); con el interruptor de Ajustes › Privacidad apagado no
// se conecta a nada. No muestra ni lee nada tuyo.

/** Lo que muestra la pantalla. */
sealed interface EstadoHistoria {
    /** El interruptor de Ajustes › Privacidad está apagado: no se pide nada. */
    data object Apagado : EstadoHistoria
    data object Buscando : EstadoHistoria
    data class Listo(val r: Efemerides.Resultado) : EstadoHistoria
}

/** La carpeta de lo guardado: un archivo por fecha, fuera de la base, del respaldo y de la exportación. */
fun carpetaHistoria(ctx: android.content.Context) = File(ctx.filesDir, "historia")

@Composable
fun PantallaHoyEnLaHistoria(nav: Nav) {
    val ctx = LocalContext.current
    val encendido = Repo.config.historia_hoy
    val hoy = remember { LocalDate.now() }
    var intento by remember { mutableIntStateOf(0) }
    var estado by remember { mutableStateOf<EstadoHistoria>(if (encendido) EstadoHistoria.Buscando else EstadoHistoria.Apagado) }
    LaunchedEffect(encendido, intento) {
        if (!encendido) { estado = EstadoHistoria.Apagado; return@LaunchedEffect }
        estado = EstadoHistoria.Buscando
        val r = withContext(Dispatchers.IO) { Efemerides.delDia(carpetaHistoria(ctx), hoy, LocalDate.now()) { Red.get(it) } }
        estado = EstadoHistoria.Listo(r)
    }
    // Tanda H2: «en la época de X ›», con las vidas que el tema deja ver y tienen cuento.
    val tema = io.github.ruddy2003.vestigium.ui.Estilo.tema.id
    val vidas = remember(tema) { vidasConCuento(tema) }
    VistaHoyEnLaHistoria(estado, hoy, atras = nav.atras, alReintentar = { intento++ }, alPrivacidad = { nav.ir("privacidad") },
        epocaDe = { anio -> io.github.ruddy2003.vestigium.vidas.Epocas.vidaDelAnio(anio, vidas, tema)?.let { it.id to it.nombre } },
        alCuento = { id -> nav.ir("cuento?vida=$id") })
}

/**
 * La pantalla sin la red: el [estado] ya resuelto (así la sacan las capturas). Los eventos van del más antiguo al más
 * reciente, con fichas para quedarse con una época, y cada uno con los artículos de Wikipedia que enlaza.
 *
 * Tanda H2: si [epocaDe] da una vida para el año del evento (su id y su nombre), el evento lleva «en la época de X ›»,
 * que abre su cuento con [alCuento]. No se copia texto: solo se enlaza.
 */
@Composable
fun VistaHoyEnLaHistoria(estado: EstadoHistoria, fecha: LocalDate, atras: (() -> Unit)?, alReintentar: () -> Unit, alPrivacidad: () -> Unit,
                         epocaDe: (Int) -> Pair<String, String>? = { null }, alCuento: (String) -> Unit = {}) {
    val ctx = LocalContext.current
    val cs = MaterialTheme.colorScheme
    val mes = fecha.monthValue; val dia = fecha.dayOfMonth
    fun abrir(u: String) { runCatching { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(u))) } }
    Hoja("Hoy en la historia", atras, "Lo que pasó un ${Efemerides.nombreDelDia(mes, dia)}, de Wikipedia en español.") {
        when (estado) {
            EstadoHistoria.Apagado -> Tarjeta(Modifier.padding(top = 12.dp)) {
                Text("Está apagado", style = MaterialTheme.typography.titleMedium)
                Ayuda("Si lo enciendes, la app le pide a Wikipedia lo que pasó un día como hoy. Solo manda el mes y el día; nada tuyo se cruza con lo que llega.",
                    Modifier.padding(top = 4.dp))
                BotonTexto(onClick = alPrivacidad) { Text("encenderlo en Privacidad →", style = MaterialTheme.typography.labelLarge) }
            }
            EstadoHistoria.Buscando -> Ayuda("Buscando en Wikipedia…", Modifier.padding(top = 16.dp))
            is EstadoHistoria.Listo -> {
                val r = estado.r
                if (r.eventos.isEmpty()) {
                    Tarjeta(Modifier.padding(top = 12.dp)) {
                        Text("Sin conexión", style = MaterialTheme.typography.titleMedium)
                        Ayuda("No pude llegar a Wikipedia y no hay nada guardado de este día. Cada mañana la app deja lista la fecha del día siguiente, para verla aunque no haya conexión.",
                            Modifier.padding(top = 4.dp))
                        BotonTexto(onClick = alReintentar) { Text("volver a intentar", style = MaterialTheme.typography.labelLarge) }
                    }
                } else ListaDeEventos(r, mes, dia, ::abrir, alReintentar, epocaDe, alCuento)
            }
        }
        // El pie: de dónde sale y con qué licencia.
        Text("De la página «${Efemerides.nombreDelDia(mes, dia)}» de Wikipedia en español",
            style = MaterialTheme.typography.bodySmall, color = cs.primary, textDecoration = TextDecoration.Underline,
            modifier = Modifier.padding(top = 18.dp).clickable(role = Role.Button) { abrir(Efemerides.paginaDelDia(mes, dia)) })
        Text("Texto con licencia ${Efemerides.LICENCIA}", style = MaterialTheme.typography.bodySmall, color = cs.primary, textDecoration = TextDecoration.Underline,
            modifier = Modifier.padding(top = 4.dp).clickable(role = Role.Button) { abrir(Efemerides.URL_LICENCIA) })
        Ayuda("Solo se mandó el mes y el día. Lo que llega se guarda en el teléfono, fuera de tus datos y de tus respaldos.", Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(28.dp))
    }
}

/** Las épocas de las fichas: desde, hasta (incluidos). */
private val EPOCAS = listOf(
    Triple("todo", Int.MIN_VALUE, Int.MAX_VALUE),
    Triple("hasta 1499", Int.MIN_VALUE, 1499),
    Triple("1500 a 1899", 1500, 1899),
    Triple("desde 1900", 1900, Int.MAX_VALUE),
)

@Composable
private fun ListaDeEventos(r: Efemerides.Resultado, mes: Int, dia: Int, abrir: (String) -> Unit, alReintentar: () -> Unit,
                           epocaDe: (Int) -> Pair<String, String>?, alCuento: (String) -> Unit) {
    val cs = MaterialTheme.colorScheme
    var epoca by rememberSaveable(mes, dia) { mutableStateOf(0) }
    if (r.sinConexion) Tarjeta(Modifier.padding(top = 12.dp)) {
        Text("Sin conexión", style = MaterialTheme.typography.titleSmall)
        Ayuda(r.bajado?.let { "Es lo que se guardó el ${Efemerides.nombreDelDia(it.monthValue, it.dayOfMonth)} de ${it.year}." } ?: "Es lo que había guardado.",
            Modifier.padding(top = 2.dp))
        BotonTexto(onClick = alReintentar) { Text("volver a intentar", style = MaterialTheme.typography.labelLarge) }
    }
    val (_, desde, hasta) = EPOCAS[epoca]
    val vistos = r.eventos.filter { it.anio in desde..hasta }
    Text(porNumeroEventos(r.eventos.size), style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant, modifier = Modifier.padding(top = 12.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 6.dp)) {
        EPOCAS.forEachIndexed { i, (nombre, d, h) ->
            val n = r.eventos.count { it.anio in d..h }
            if (i == 0 || n > 0) Ficha(if (i == 0) nombre else "$nombre ($n)", epoca == i) { epoca = i }
        }
    }
    Tarjeta(Modifier.padding(top = 10.dp)) {
        vistos.forEachIndexed { i, e ->
            Row(Modifier.padding(top = if (i == 0) 0.dp else 12.dp)) {
                Text(Efemerides.nombreDelAnio(e.anio), style = MaterialTheme.typography.labelLarge, color = cs.primary, modifier = Modifier.width(64.dp))
                Column(Modifier.weight(1f)) {
                    Text(e.texto, style = MaterialTheme.typography.bodyMedium)
                    epocaDe(e.anio)?.let { (id, nombre) ->
                        Text("en la época de ${enFrase(nombre)} ›", style = MaterialTheme.typography.labelLarge, color = cs.secondary,
                            modifier = Modifier.clickable(role = Role.Button) { alCuento(id) }.padding(top = 4.dp, bottom = 2.dp))
                    }
                    if (e.enlaces.isNotEmpty()) FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 2.dp)) {
                        e.enlaces.take(Efemerides.ENLACES_POR_EVENTO).forEach { l ->
                            Text(l.titulo, style = MaterialTheme.typography.labelMedium, color = cs.primary, textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable(role = Role.Button) { abrir(l.url) }.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

/** «Un joven naturalista» → «un joven naturalista», para que se lea dentro de la frase; los nombres propios quedan igual. */
internal fun enFrase(nombre: String): String =
    if (listOf("Un ", "Una ", "El ", "La ").any { nombre.startsWith(it) }) nombre.replaceFirstChar { it.lowercase() } else nombre

private fun porNumeroEventos(n: Int) = if (n == 1) "1 acontecimiento" else "$n acontecimientos"
