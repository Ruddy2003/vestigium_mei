package io.github.ruddy2003.vestigium.ui.comun

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.isSpecified
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.esOscuro

/**
 * La raya que separa dos cosas, dibujada por el tema ([DisenoComun.separador]): la greca, el filete con su rombo, la línea
 * de guiones de la consola, el cordón… Reemplaza al `HorizontalDivider` de Material. Con un [thickness] dado se queda la
 * raya lisa de ese grosor (y [color]): es una raya que la pantalla quiere así (las reglas de una gaceta, por ejemplo). Con
 * solo un [color] dado, el dibujo del tema va tenue.
 */
@Composable
fun Separador(modifier: Modifier = Modifier, thickness: Dp = Dp.Unspecified, color: Color = Color.Unspecified) {
    if (thickness.isSpecified) {
        HorizontalDivider(modifier, thickness, if (color.isSpecified) color else MaterialTheme.colorScheme.outlineVariant)
        return
    }
    val d = disenoComun(Estilo.tema)
    val oscuro = esOscuro()
    val e = EstadoSeparador(tenue = color.isSpecified, semilla = 0)
    Canvas(modifier.fillMaxWidth().height(d.altoSeparador).clearAndSetSemantics { }) { with(d) { separador(e, oscuro) } }
}

/**
 * Un botón de texto con el adorno del tema detrás ([DisenoComun.botonTexto]): el subrayado con floritura, los corchetes de
 * la consola, la cinta, el sello… y su tinta ([DisenoComun.tintaBotonTexto]). Reemplaza al `TextButton` de Material con
 * los mismos parámetros; por dentro sigue siendo uno (el toque, el tamaño mínimo y la accesibilidad son los de siempre).
 */
@Composable
fun BotonTexto(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    val d = disenoComun(Estilo.tema)
    val oscuro = esOscuro()
    val toque = remember { MutableInteractionSource() }
    val presionado by toque.collectIsPressedAsState()
    val e = EstadoBotonTexto(presionado, enabled, 0)
    TextButton(onClick, modifier.drawBehind { with(d) { botonTexto(e, oscuro) } }, enabled = enabled, contentPadding = contentPadding,
        interactionSource = toque, colors = ButtonDefaults.textButtonColors(contentColor = d.tintaBotonTexto(oscuro)), content = content)
}
