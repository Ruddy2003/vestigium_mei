package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
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
        if (encabezado != null) {
            // Las pantallas con su propio encabezado (la bienvenida) solo llevan aquí «volver».
            if (atras != null) Row(Modifier.fillMaxWidth().padding(top = 6.dp)) { BotonVolver(atras) }
            encabezado()
        } else {
            val (nombre, espanol, comentario) = Estilo.pantallaEnLinea(titulo, latin)
            Encabezado(tituloEnLinea(nombre, espanol), atras = atras, buscar = buscar, ajustes = ajustes,
                guia = io.github.ruddy2003.vestigium.data.GuiaUso.PANTALLAS[titulo], comentario = comentario)
        }
        Meandro(Modifier.padding(top = 10.dp, bottom = 4.dp))
        if (subtitulo != null) Ayuda(subtitulo, Modifier.padding(top = 6.dp))
        content()
        Spacer(Modifier.height(56.dp))
    }
}

/** El título de una pantalla en una línea: el nombre del tema con más contraste y el español al lado, más tenue (P-1). */
@Composable
fun tituloEnLinea(nombre: String, espanol: String?): AnnotatedString = buildAnnotatedString {
    if (espanol != null) withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)) { append(nombre) }
    else append(nombre)
    // En Terminal el título termina con el cursor.
    Estilo.sufijoTitulo.takeIf { it.isNotEmpty() }?.let { suf -> withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) { append(suf) } }
    if (espanol != null) withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) { append(" · $espanol") }
}

/**
 * La parte de arriba de cada pantalla (13 nov 2026). Una fila chica: «volver» (si la hay) y el emblema a la izquierda; a la
 * derecha, el «?» de la guía (solo en las pantallas de adentro que tienen su sección), buscar y «Más». Debajo, el título con
 * todo el ancho. Antes todo iba en la misma fila que el título (también el sol o la luna del modo) y al título le quedaban
 * unos 80 dp: se partía en renglones. El modo claro u oscuro está ahora en «Más».
 */
@Composable
fun Encabezado(titulo: AnnotatedString, atras: (() -> Unit)? = null, buscar: (() -> Unit)? = null, ajustes: (() -> Unit)? = null,
               guia: String? = null, comentario: String? = null, tamEmblema: Dp = 30.dp) {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp).heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
        if (atras != null) BotonVolver(atras)
        Emblema(tam = tamEmblema, modifier = Modifier.padding(start = if (atras != null) 8.dp else 0.dp))
        Spacer(Modifier.weight(1f))
        val abrirGuia = LocalGuia.current
        // El «?» de la guía va en las pantallas de adentro; en las pestañas, cada sección trae el suyo y la guía entera está en «Más».
        if (atras != null && guia != null && abrirGuia != null) BotonTexto(onClick = { abrirGuia(guia) }, modifier = Modifier.semantics { contentDescription = "Ayuda de esta pantalla" }) {
            Text("?", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.clearAndSetSemantics { })
        }
        if (buscar != null) BotonBuscar(onClick = buscar)
        if (ajustes != null) BotonAjustes(Modifier.padding(start = 4.dp), onClick = ajustes)
    }
    Text(titulo, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.fillMaxWidth().padding(top = 2.dp))
    if (comentario != null) Text(comentario, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

/** «Volver»: el texto cambia con el tema («← tornar», «cd ..»); para TalkBack siempre dice «Volver» (N-27). */
@Composable
fun BotonVolver(atras: () -> Unit) {
    BotonTexto(onClick = atras, modifier = Modifier.semantics { contentDescription = "Volver" }) {
        Text(Estilo.atras, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.clearAndSetSemantics { })
    }
}
