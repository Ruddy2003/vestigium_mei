package io.github.ruddy2003.vestigium.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

/**
 * Pantalla: nombre latino grabado en capitales, el nombre en español como comentario de código,
 * y una banda de meandro debajo. El contenido se desplaza verticalmente.
 */
/** Desplazamiento de la pestaña abierta: cada pestaña recuerda dónde ibas aunque cambies a otra. */
val LocalScrollPestana = androidx.compose.runtime.staticCompositionLocalOf<ScrollState?> { null }

/** Abre la guía de uso en una sección (el «?» del encabezado). Null fuera de la app principal. */
val LocalGuia = androidx.compose.runtime.staticCompositionLocalOf<((String) -> Unit)?> { null }

@Composable
fun Hoja(titulo: String, atras: (() -> Unit)? = null, subtitulo: String? = null, latin: String? = Romano.latin(titulo), encabezado: (@Composable () -> Unit)? = null,
         modifier: Modifier = Modifier, scroll: ScrollState = LocalScrollPestana.current ?: rememberScrollState(), buscar: (() -> Unit)? = null,
         ajustes: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxSize().verticalScroll(scroll).padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            if (atras != null) TextButton(onClick = atras) { Text(Estilo.atras, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) }
        }
        if (encabezado != null) encabezado()
        else {
            val (nombre, comentario) = Estilo.pantalla(titulo, latin)
            Row(Modifier.fillMaxWidth().padding(top = if (atras == null) 14.dp else 2.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    buildAnnotatedString {
                        append(nombre)
                        // En cyberpunk el título termina con un cursor de terminal.
                        if (Estilo.tema == TemaId.CIBER) withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) { append("_") }
                    },
                    style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f),
                )
                val guia = LocalGuia.current
                val seccionGuia = io.github.ruddy2003.vestigium.data.GuiaUso.PANTALLAS[titulo]
                if (guia != null && seccionGuia != null) TextButton(onClick = { guia(seccionGuia) }) { Text("?", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary) }
                if (buscar != null) BotonBuscar(onClick = buscar)
                if (ajustes != null) BotonAjustes(Modifier.padding(start = 4.dp), onClick = ajustes)
                BotonModo()
                Emblema(modifier = Modifier.padding(start = 4.dp))
            }
            if (comentario != null) Text(comentario, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Meandro(Modifier.padding(top = 10.dp, bottom = 4.dp))
        if (subtitulo != null) Ayuda(subtitulo, Modifier.padding(top = 6.dp))
        content()
        Spacer(Modifier.height(56.dp))
    }
}
