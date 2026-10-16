@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package io.github.ruddy2003.vestigium.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Opcion

@Composable
fun Seccion(titulo: String, ayuda: String? = null, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 22.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleLarge)
        if (ayuda != null) Text(ayuda, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp, bottom = 10.dp))
        else Spacer(Modifier.padding(top = 8.dp))
        content()
    }
}

@Composable
fun Tarjeta(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) { Column(Modifier.padding(14.dp)) { content() } }
}

@Composable
fun Etiqueta(texto: String) {
    Text(texto.uppercase(), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 14.dp, bottom = 8.dp))
}

@Composable
fun Ayuda(texto: String, modifier: Modifier = Modifier) {
    Text(texto, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = modifier)
}

/** Fila de números (1-7, 0-5…) con el ancla del valor elegido debajo. Tocar el elegido lo desmarca. */
@Composable
fun Escala(valor: Int?, desde: Int, hasta: Int, anclas: Map<Int, String>? = null, onCambio: (Int?) -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            for (i in desde..hasta) {
                val sel = valor == i
                Box(
                    Modifier.weight(1f).heightIn(min = 46.dp).clip(RoundedCornerShape(8.dp))
                        .background(if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                        .border(1.dp, if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .clickable { onCambio(if (sel) null else i) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("$i", fontFamily = FontFamily.Monospace, fontSize = 16.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                        color = if (sel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        if (anclas != null) {
            Text(
                if (valor == null) "Sin marcar" else "$valor · ${anclas[valor] ?: ""}",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
fun Ficha(texto: String, activa: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (activa) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (activa) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
    ) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
            if (activa) { Box(Modifier.size(7.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary)); Spacer(Modifier.width(7.dp)) }
            Text(texto, style = MaterialTheme.typography.bodyMedium, fontWeight = if (activa) FontWeight.SemiBold else FontWeight.Normal)
        }
    }
}

@Composable
fun FilaFichas(opciones: List<Opcion>, valor: String?, onCambio: (String?) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        opciones.forEach { o -> Ficha(o.nombre, valor == o.id) { onCambio(if (valor == o.id) null else o.id) } }
    }
}

/** Opciones con explicación corta, para las categorías científicas. */
@Composable
fun Opciones(opciones: List<Opcion>, valor: String?, onCambio: (String?) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp), maxItemsInEachRow = 2) {
        opciones.forEach { o ->
            val sel = valor == o.id
            Surface(
                onClick = { onCambio(if (sel) null else o.id) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                color = if (sel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
            ) {
                Column(Modifier.padding(10.dp)) {
                    Text(o.nombre, style = MaterialTheme.typography.titleMedium, color = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    if (o.detalle.isNotEmpty()) Text(o.detalle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun Interruptor(texto: String, valor: Boolean, detalle: String? = null, onCambio: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onCambio(!valor) }.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(texto, style = MaterialTheme.typography.bodyLarge)
            if (detalle != null) Ayuda(detalle)
        }
        Switch(checked = valor, onCheckedChange = onCambio)
    }
}

/** Lista de etiquetas cortas que se pueden agregar y quitar (hitos, temas del sueño, medicamentos). */
@Composable
fun ListaEtiquetas(lista: List<String>, pista: String, prefijo: String = "", normalizar: (String) -> String, onCambio: (List<String>) -> Unit) {
    var texto by remember { mutableStateOf("") }
    fun agregar() {
        val nuevos = texto.split(",").map(normalizar).filter { it.isNotEmpty() }
        if (nuevos.isNotEmpty()) { onCambio(lista + nuevos); texto = "" }
    }
    Column {
        if (lista.isEmpty()) Ayuda("Nada todavía.")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            lista.forEachIndexed { i, t ->
                Surface(shape = RoundedCornerShape(7.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 10.dp)) {
                        Text(prefijo + t, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                        TextButton(onClick = { onCambio(lista.filterIndexed { j, _ -> j != i }) }, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(36.dp)) { Text("×", fontSize = 16.sp) }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = texto, onValueChange = { texto = it }, modifier = Modifier.weight(1f), singleLine = true,
                placeholder = { Text(pista) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { agregar() }),
            )
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = { agregar() }) { Text("Añadir") }
        }
    }
}

/** Botón que abre el dictado por voz de Android y agrega el texto reconocido. */
@Composable
fun BotonDictado(onTexto: (String) -> Unit) {
    val ctx = LocalContext.current
    val lanzar = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        val t = res.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!t.isNullOrBlank()) onTexto(t)
    }
    OutlinedButton(onClick = {
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-MX")
            .putExtra(RecognizerIntent.EXTRA_PROMPT, "Dicta")
        try { lanzar.launch(i) } catch (e: ActivityNotFoundException) { Toast.makeText(ctx, "Este teléfono no tiene dictado por voz disponible.", Toast.LENGTH_SHORT).show() }
    }) { Text("Dictar") }
}

@Composable
fun BotonPrincipal(texto: String, modifier: Modifier = Modifier, habilitado: Boolean = true, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = habilitado, modifier = modifier.heightIn(min = 48.dp), shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text(texto) }
}

@Composable
fun Cifra(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(Modifier.padding(10.dp)) {
            Text(valor, fontFamily = FontFamily.Monospace, fontSize = 19.sp, fontWeight = FontWeight.Medium, maxLines = 1)
            Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun Vacio(texto: String) {
    Box(Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)).padding(18.dp), contentAlignment = Alignment.Center) {
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
fun Aviso(texto: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
        Text(texto, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(10.dp))
    }
}

@Composable
fun FilaMenu(titulo: String, detalle: String? = null, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge)
            if (detalle != null) Ayuda(detalle)
        }
        Text("›", fontSize = 22.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

fun tipoNombre(id: String, discreto: Boolean): String = Catalogo.tipo(id)?.let { if (discreto) it.discreto else it.nombre } ?: if (id == "muestreo") "Ánimo" else id
