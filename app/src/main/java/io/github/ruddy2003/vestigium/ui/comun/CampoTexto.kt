package io.github.ruddy2003.vestigium.ui.comun

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.esOscuro

/**
 * Un campo de texto con el marco del tema ([DisenoComun.marcoCampo]): la tablilla de cera, el recuadro de formulario, los
 * corchetes de la consola… Recibe lo mismo que el `OutlinedTextField` de Material (y lo reemplaza en toda la app): el
 * [label] va dentro del marco, arriba y chico; la [placeholder], donde se escribe, mientras está vacío; [prefix] y [suffix]
 * a los lados del texto; [trailingIcon] al final del renglón. Sin un ancho dado, mide al menos 280 dp, como el de Material.
 */
@Composable
fun CampoTexto(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
) {
    val d = disenoComun(Estilo.tema)
    val oscuro = esOscuro()
    val toque = remember { MutableInteractionSource() }
    val enfocado by toque.collectIsFocusedAsState()
    val tinta = if (isError) d.errorCampo(oscuro) else d.tintaCampo(oscuro)
    // El color que traiga el estilo se respeta; si no trae, va la tinta del tema.
    val estilo = textStyle.let { if (it.color.isSpecified) it else it.copy(color = d.tintaCampo(oscuro)) }
    val semilla = remember { (System.identityHashCode(toque) and 0xffff) }
    // De varios renglones solo el que los pide desde el principio (minLines); los demás crecen si hace falta, pero se dibujan como de uno.
    val e = EstadoMarco(enfocado, isError, enabled, !singleLine && minLines > 1, value.isEmpty(), semilla)
    BasicTextField(
        value = value, onValueChange = onValueChange,
        modifier = modifier.defaultMinSize(minWidth = 280.dp, minHeight = d.altoCampo).alpha(if (enabled) 1f else 0.6f)
            .drawBehind { with(d) { marcoCampo(e, oscuro) } },
        enabled = enabled, readOnly = readOnly, textStyle = estilo, keyboardOptions = keyboardOptions, keyboardActions = keyboardActions,
        singleLine = singleLine, maxLines = maxLines, minLines = minLines, visualTransformation = visualTransformation,
        interactionSource = toque, cursorBrush = SolidColor(tinta),
        decorationBox = { campo ->
            Box(contentAlignment = Alignment.CenterStart) {
                Column(Modifier.padding(horizontal = d.margenCampoX, vertical = d.margenCampoY)) {
                    if (label != null) CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.labelMedium,
                        LocalContentColor provides if (isError) d.errorCampo(oscuro) else d.rotuloCampo(oscuro),
                    ) { Box(Modifier.padding(bottom = 2.dp)) { label() } }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (prefix != null) CompositionLocalProvider(LocalTextStyle provides estilo, LocalContentColor provides d.rotuloCampo(oscuro)) { prefix() }
                        Box(Modifier.weight(1f, fill = false)) {
                            if (value.isEmpty() && placeholder != null) CompositionLocalProvider(
                                LocalTextStyle provides estilo.copy(color = d.pistaCampo(oscuro)), LocalContentColor provides d.pistaCampo(oscuro),
                            ) { placeholder() }
                            campo()
                        }
                        if (suffix != null) CompositionLocalProvider(LocalTextStyle provides estilo, LocalContentColor provides d.rotuloCampo(oscuro)) { suffix() }
                        if (trailingIcon != null) {
                            Box(Modifier.weight(1f))
                            CompositionLocalProvider(LocalContentColor provides d.rotuloCampo(oscuro)) { trailingIcon() }
                        }
                    }
                }
            }
        },
    )
}
