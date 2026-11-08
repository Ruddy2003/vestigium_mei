package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.esOscuro

/**
 * El buscador de las bibliotecas, dibujado por el tema ([DisenoBiblioteca.campo]): el marco del tema detrás y lo que se escribe
 * dentro de [DisenoBiblioteca.cajaCampo], con su letra y su tinta. Vacío, el tema dibuja la [pista].
 */
@Composable
fun CampoTema(valor: String, cambiar: (String) -> Unit, pista: String, modifier: Modifier = Modifier) {
    val d = disenoBiblioteca(Estilo.tema)
    val oscuro = esOscuro()
    val medidor = rememberTextMeasurer(cacheSize = 8)
    val toque = remember { MutableInteractionSource() }
    val enfocado by toque.collectIsFocusedAsState()
    val tinta = d.tintaCampo(oscuro)
    BoxWithConstraints(modifier.fillMaxWidth().height(d.altoCampo)) {
        val caja = d.cajaCampo
        Canvas(Modifier.fillMaxSize()) { with(d) { campo(EstadoCampo(pista, valor.isEmpty(), enfocado), medidor, oscuro) } }
        BasicTextField(
            valor, cambiar,
            Modifier.offset(maxWidth * caja.left, maxHeight * caja.top).width(maxWidth * caja.width).height(maxHeight * caja.height)
                .semantics { contentDescription = pista },
            singleLine = true, interactionSource = toque, cursorBrush = SolidColor(tinta),
            textStyle = TextStyle(fontFamily = d.letraCampo, fontSize = 16.sp, color = tinta),
            decorationBox = { campo -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) { campo() } },
        )
    }
}
