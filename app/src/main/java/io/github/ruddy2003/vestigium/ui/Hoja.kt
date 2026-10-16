package io.github.ruddy2003.vestigium.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Pantalla con título y, si es secundaria, botón para regresar. El contenido se desplaza verticalmente. */
@Composable
fun Hoja(titulo: String, atras: (() -> Unit)? = null, subtitulo: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (atras != null) TextButton(onClick = atras) { Text("‹ Atrás", fontSize = 15.sp) }
        }
        Text(titulo, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = if (atras == null) 12.dp else 0.dp))
        if (subtitulo != null) Ayuda(subtitulo, Modifier.padding(top = 4.dp))
        content()
        Spacer(Modifier.height(48.dp))
    }
}
