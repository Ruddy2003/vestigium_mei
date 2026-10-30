@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package io.github.ruddy2003.vestigium.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Controles de Material con la capa del tema (lote 4)
// =====================================================================
//
// Cada envoltorio acepta los mismos parámetros con nombre que el control de Material al que sustituye, para que el
// cambio en las pantallas sea solo renombrar la llamada:
//   AlertDialog → Dialogo · Slider → Deslizador · LinearProgressIndicator → Progreso · Switch → Conmutador
//   DropdownMenu → Menu · DropdownMenuItem → OpcionMenu
// Los colores y formas que pase la pantalla se respetan cuando tienen sentido; si no, manda el tema.

// ---------------------------------------------------------------- diálogo

/**
 * Diálogo con la piel del tema: forma del marco (frontón, arco apuntado, cúpula de ger, losa, boleto…), adornos del
 * marco, filete bajo el título y el botón de confirmar con la forma de los botones del tema. Mismos parámetros que
 * `AlertDialog` de Material.
 */
@Composable
fun Dialogo(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape? = null,
    containerColor: Color = Color.Unspecified,
    iconContentColor: Color = Color.Unspecified,
    titleContentColor: Color = Color.Unspecified,
    textContentColor: Color = Color.Unspecified,
    tonalElevation: Dp = 0.dp,
    properties: DialogProperties = DialogProperties(),
) {
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        CuerpoDialogo(
            confirmButton = confirmButton, modifier = modifier.sizeIn(minWidth = 280.dp, maxWidth = 560.dp), dismissButton = dismissButton,
            icon = icon, title = title, text = text, shape = shape, containerColor = containerColor, iconContentColor = iconContentColor,
            titleContentColor = titleContentColor, textContentColor = textContentColor, tonalElevation = tonalElevation,
        )
    }
}

/**
 * El cuerpo del [Dialogo], sin la ventana: se puede dibujar dentro de una pantalla (y en las capturas, que no dibujan
 * otras ventanas). [relleno] es el margen interior (24 dp como en Material; los selectores de fecha usan menos).
 */
@Composable
fun CuerpoDialogo(
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape? = null,
    containerColor: Color = Color.Unspecified,
    iconContentColor: Color = Color.Unspecified,
    titleContentColor: Color = Color.Unspecified,
    textContentColor: Color = Color.Unspecified,
    tonalElevation: Dp = 0.dp,
    relleno: Dp = 24.dp,
) {
    val tema = Estilo.tema
    val e = MaterialTheme.colorScheme
    val piel = pielDialogo(tema)
    val fondo = if (containerColor.isSpecified) containerColor else e.surface
    val formaConfirmar = formaBotonTema(tema)
    Surface(
        modifier = modifier.drawWithContent { drawContent(); marcoDialogo(tema, e) },
        shape = shape ?: piel.forma, color = fondo, tonalElevation = tonalElevation, shadowElevation = 6.dp,
        border = BorderStroke(piel.borde, e.outline),
    ) {
        Column(Modifier.padding(start = relleno, end = relleno, top = relleno + piel.arriba, bottom = relleno + piel.abajo)) {
            if (icon != null) CompositionLocalProvider(LocalContentColor provides (if (iconContentColor.isSpecified) iconContentColor else e.secondary)) {
                Box(Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)) { icon() }
            }
            if (title != null) {
                CompositionLocalProvider(
                    LocalContentColor provides (if (titleContentColor.isSpecified) titleContentColor else e.onSurface),
                    LocalTextStyle provides MaterialTheme.typography.headlineSmall,
                ) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = if (piel.centrado || icon != null) Alignment.TopCenter else Alignment.TopStart) { title() }
                }
                Canvas(Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 14.dp).height(10.dp)) { fileteDialogo(tema, e) }
            }
            if (text != null) CompositionLocalProvider(
                LocalContentColor provides (if (textContentColor.isSpecified) textContentColor else e.onSurfaceVariant),
                LocalTextStyle provides MaterialTheme.typography.bodyMedium,
            ) {
                Box(Modifier.weight(1f, fill = false).padding(bottom = 20.dp).align(Alignment.Start)) { text() }
            }
            Box(Modifier.align(Alignment.End)) {
                CompositionLocalProvider(LocalContentColor provides e.primary, LocalTextStyle provides MaterialTheme.typography.labelLarge) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        dismissButton?.invoke()
                        // El botón de confirmar lleva la forma de los botones del tema; si la pantalla no pone botón, no se dibuja nada.
                        Box(Modifier.drawBehind {
                            if (size.width > 1f && size.height > 1f) {
                                val o = formaConfirmar.createOutline(size, layoutDirection, this)
                                drawOutline(o, e.primary.copy(alpha = 0.10f))
                                drawOutline(o, e.primary.copy(alpha = 0.7f), style = Stroke(1.2.dp.toPx()))
                            }
                        }) { confirmButton() }
                    }
                }
            }
        }
    }
}

/** Una forma medida en dp: [trazo] recibe ancho, alto y cuántos píxeles mide un dp. */
private class FormaDp(private val trazo: Path.(w: Float, h: Float, d: Float) -> Unit) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Generic(Path().apply { trazo(this, size.width, size.height, density.density) })
}

/** Forma del marco, holgura extra arriba y abajo (para frontones, arcos y dientes), título centrado y grosor del borde. */
private class PielDialogo(val forma: Shape, val arriba: Dp, val abajo: Dp, val centrado: Boolean, val borde: Dp)

private val PIELES_DIALOGO: Map<TemaId, PielDialogo> by lazy { TemaId.entries.associateWith { crearPielDialogo(it) } }

private fun pielDialogo(t: TemaId): PielDialogo = PIELES_DIALOGO.getValue(t)

private fun crearPielDialogo(t: TemaId): PielDialogo = when (t) {
    // Placa de mármol con esquinas apenas romas.
    TemaId.ROMANO -> PielDialogo(RoundedCornerShape(3.dp), 0.dp, 0.dp, false, 1.dp)
    // El portón de un palacio: arco rebajado arriba y pies rectos.
    TemaId.URBE -> PielDialogo(FormaDp { w, h, d ->
        val f = 16 * d
        moveTo(0f, h); lineTo(0f, f); quadraticTo(w / 2, -f, w, f); lineTo(w, h); close()
    }, 10.dp, 0.dp, true, 1.dp)
    // Ventana de consola con dos esquinas cortadas en diagonal.
    TemaId.CIBER -> PielDialogo(CutCornerShape(topEnd = 14.dp, bottomStart = 14.dp), 0.dp, 0.dp, false, 1.dp)
    // La placa luminosa del puesto: esquinas muy redondas.
    TemaId.NEON -> PielDialogo(RoundedCornerShape(22.dp), 0.dp, 0.dp, false, 1.5.dp)
    // Frontón de templo: un tímpano bajo arriba.
    TemaId.HELENO -> PielDialogo(FormaDp { w, h, d ->
        val g = 16 * d
        moveTo(0f, g); lineTo(w / 2, 0f); lineTo(w, g); lineTo(w, h); lineTo(0f, h); close()
    }, 8.dp, 0.dp, true, 1.dp)
    // Estela de remate redondo: un arco rebajado arriba, recta abajo.
    TemaId.EGIPTO -> PielDialogo(FormaDp { w, h, d ->
        val g = 14 * d
        moveTo(0f, h); lineTo(0f, g); quadraticTo(w / 2, -g * 0.8f, w, g); lineTo(w, h); close()
    }, 12.dp, 0.dp, true, 1.dp)
    // Esquinas cóncavas de miniatura.
    TemaId.MEDIEVAL -> PielDialogo(FormaDp { w, h, d ->
        val r = 14 * d
        moveTo(r, 0f); lineTo(w - r, 0f); quadraticTo(w - r, r, w, r); lineTo(w, h - r); quadraticTo(w - r, h - r, w - r, h)
        lineTo(r, h); quadraticTo(r, h - r, 0f, h - r); lineTo(0f, r); quadraticTo(r, r, r, 0f); close()
    }, 0.dp, 0.dp, false, 1.dp)
    // Lienzo de muralla: el borde de arriba en almenas, merlones y troneras.
    TemaId.TEMPLARIO -> PielDialogo(FormaDp { w, h, d ->
        val m = 18 * d; val a = 7 * d
        moveTo(0f, h); lineTo(0f, 0f)
        var x = 0f; var k = 0
        while (x + m < w) { val y = if (k % 2 == 0) 0f else a; lineTo(x, y); lineTo(x + m, y); x += m; k++ }
        lineTo(x, if (k % 2 == 0) 0f else a); lineTo(w, if (k % 2 == 0) 0f else a); lineTo(w, h); close()
    }, 7.dp, 0.dp, true, 1.4.dp)
    // Hoja de expediente: recta y con borde grueso.
    TemaId.DISTOPIA -> PielDialogo(RectangleShape, 0.dp, 0.dp, false, 2.dp)
    // Panel redondeado de refugio.
    TemaId.REFUGIO -> PielDialogo(RoundedCornerShape(24.dp), 0.dp, 0.dp, false, 1.5.dp)
    // Placa de acero ochavada.
    TemaId.ORDEN -> PielDialogo(CutCornerShape(12.dp), 0.dp, 0.dp, false, 1.5.dp)
    // El billete de tren de cartón: las cuatro esquinas recortadas hacia dentro.
    TemaId.VAPOR -> PielDialogo(FormaDp { w, h, d ->
        val f = 12 * d
        moveTo(f, 0f); lineTo(w - f, 0f); quadraticTo(w - f, f, w, f); lineTo(w, h - f); quadraticTo(w - f, h - f, w - f, h)
        lineTo(f, h); quadraticTo(f, h - f, 0f, h - f); lineTo(0f, f); quadraticTo(f, f, f, 0f); close()
    }, 4.dp, 0.dp, false, 1.5.dp)
    // Frasco: redondo arriba, casi recto abajo.
    TemaId.LABORATORIO -> PielDialogo(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp, bottomEnd = 6.dp, bottomStart = 6.dp), 0.dp, 0.dp, false, 1.dp)
    // Hoja de gaceta con el borde de abajo perforado en dientes.
    TemaId.COLONOS -> PielDialogo(FormaDp { w, h, d ->
        val p = 6 * d; val f = 4 * d
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h - f)
        var x = w
        while (x > p) { lineTo(x - p / 2, h); lineTo(x - p, h - f); x -= p }
        lineTo(0f, h - f); close()
    }, 0.dp, 4.dp, false, 1.dp)
    // Esquinas escalonadas, como un marco de medalla.
    TemaId.NAPOLEON -> PielDialogo(FormaDp { w, h, d ->
        val n = 8 * d
        moveTo(n, 0f); lineTo(w - n, 0f); lineTo(w - n, n); lineTo(w, n); lineTo(w, h - n); lineTo(w - n, h - n); lineTo(w - n, h)
        lineTo(n, h); lineTo(n, h - n); lineTo(0f, h - n); lineTo(0f, n); lineTo(n, n); close()
    }, 0.dp, 0.dp, false, 1.5.dp)
    // Hoja: dos esquinas muy redondas y dos en punta.
    TemaId.BOTANICO -> PielDialogo(RoundedCornerShape(topStart = 30.dp, topEnd = 4.dp, bottomEnd = 30.dp, bottomStart = 4.dp), 0.dp, 0.dp, false, 1.dp)
    // Una losa de hormigón con la esquina de arriba a la derecha desprendida en escalones.
    TemaId.RUINAS -> PielDialogo(FormaDp { w, h, d ->
        val a = 14 * d
        moveTo(0f, 0f); lineTo(w - a * 1.6f, 0f); lineTo(w - a, a * 0.4f); lineTo(w - a * 0.5f, a * 0.2f); lineTo(w, a)
        lineTo(w, h - 4 * d); lineTo(w - 4 * d, h); lineTo(a * 0.6f, h); lineTo(0f, h - a * 0.5f); close()
    }, 0.dp, 0.dp, false, 1.5.dp)
    // Tarjeta con el borde de arriba festoneado de nieve.
    TemaId.NAVIDAD -> PielDialogo(FormaDp { w, h, d ->
        val p = 18 * d; val f = 5 * d; val r = 14 * d
        moveTo(0f, f)
        var x = 0f
        while (x + p <= w + 0.5f) { quadraticTo(x + p / 2, -f * 0.6f, x + p, f); x += p }
        lineTo(w, f); lineTo(w, h - r); quadraticTo(w, h, w - r, h); lineTo(r, h); quadraticTo(0f, h, 0f, h - r); close()
    }, 6.dp, 0.dp, false, 1.5.dp)
    // Tablilla con alero, como el rótulo sobre una puerta.
    TemaId.SEDA -> PielDialogo(FormaDp { w, h, d ->
        val a = 10 * d
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, a * 0.4f); lineTo(w - a, a); lineTo(w - a, h); lineTo(a, h); lineTo(a, a); lineTo(0f, a * 0.4f); close()
    }, 4.dp, 0.dp, false, 1.dp)
    // Arco apuntado.
    TemaId.GOTICO -> PielDialogo(FormaDp { w, h, d ->
        val a = 44 * d
        moveTo(0f, h); lineTo(0f, a); quadraticTo(0f, a * 0.2f, w / 2, 0f); quadraticTo(w, a * 0.2f, w, a); lineTo(w, h); close()
    }, 22.dp, 0.dp, true, 1.5.dp)
    // Una hoja del legajo: la esquina de arriba doblada y la orilla de abajo rasgada.
    TemaId.ARCANO -> PielDialogo(FormaDp { w, h, d ->
        val doblez = 18 * d
        moveTo(0f, 0f); lineTo(w - doblez, 0f); lineTo(w, doblez); lineTo(w, h - 3 * d)
        val n = (w / (9 * d)).toInt().coerceAtLeast(4)
        for (k in n - 1 downTo 0) lineTo(w * k / n, h - (if (k % 2 == 0) 3 * d else if (k % 3 == 0) 0f else 5 * d))
        close()
    }, 6.dp, 6.dp, false, 1.dp)
    // Visor: las dos esquinas de arriba cortadas.
    TemaId.ESPACIO -> PielDialogo(CutCornerShape(topStart = 20.dp, topEnd = 20.dp), 0.dp, 0.dp, false, 1.dp)
    // La superficie del mar: el borde de arriba es una ola larga y mansa; abajo, el casco redondeado.
    TemaId.ABISMO -> PielDialogo(FormaDp { w, h, d ->
        val o = 6 * d; val r = 14 * d
        moveTo(0f, o); quadraticTo(w * 0.25f, -o * 0.6f, w * 0.5f, o); quadraticTo(w * 0.75f, o * 2.6f, w, o)
        lineTo(w, h - r); quadraticTo(w, h, w - r, h); lineTo(r, h); quadraticTo(0f, h, 0f, h - r); close()
    }, 16.dp, 2.dp, false, 1.2.dp)
    // Arco de medio punto, muy redondo arriba.
    TemaId.ANDALUS -> PielDialogo(RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp, bottomEnd = 4.dp, bottomStart = 4.dp), 12.dp, 4.dp, true, 1.dp)
    // Cúpula de ger: el borde de arriba se abomba.
    TemaId.MONGOL -> PielDialogo(FormaDp { w, h, d ->
        val b = 14 * d; val r = 10 * d
        moveTo(0f, b); quadraticTo(w / 2, -b, w, b); lineTo(w, h - r); quadraticTo(w, h, w - r, h); lineTo(r, h); quadraticTo(0f, h, 0f, h - r); close()
    }, 10.dp, 0.dp, true, 1.5.dp)
    // Hoja de cuaderno con la esquina doblada.
    TemaId.RENACIMIENTO -> PielDialogo(CutCornerShape(bottomEnd = 26.dp), 0.dp, 0.dp, false, 1.dp)
    // Una carta de marear: el borde de abajo, rasgado en olas por el uso.
    TemaId.PIRATA -> PielDialogo(FormaDp { w, h, d ->
        val f = 5 * d; val n = 6; val paso = w / n
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h - f)
        for (i in n - 1 downTo 0) quadraticTo(i * paso + paso / 2, h + f * 0.4f, i * paso, h - f)
        close()
    }, 0.dp, 10.dp, false, 1.5.dp)
    // Cartel con una cuña cortada arriba a la derecha.
    TemaId.URSS -> PielDialogo(FormaDp { w, h, d ->
        val c = 30 * d
        moveTo(0f, 0f); lineTo(w - c, 0f); lineTo(w, c); lineTo(w, h); lineTo(0f, h); close()
    }, 0.dp, 0.dp, false, 2.dp)
    // Banderola con cola de golondrina abajo.
    TemaId.REVOLUCION -> PielDialogo(FormaDp { w, h, d ->
        val n = 12 * d
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h); lineTo(w / 2 + n, h); lineTo(w / 2, h - n); lineTo(w / 2 - n, h); lineTo(0f, h); close()
    }, 0.dp, 6.dp, false, 1.5.dp)
    // Losa de piedra irregular.
    TemaId.ANTIGUAS -> PielDialogo(FormaDp { w, h, d ->
        moveTo(6 * d, 0f); lineTo(w - 14 * d, 3 * d); lineTo(w, 10 * d); lineTo(w - 3 * d, h - 8 * d); lineTo(w - 12 * d, h)
        lineTo(10 * d, h - 3 * d); lineTo(0f, h - 12 * d); lineTo(2 * d, 9 * d); close()
    }, 0.dp, 0.dp, false, 1.dp)
}

/** Los adornos del marco del diálogo, dibujados encima del contenido y siempre en el margen. */
private fun DrawScope.marcoDialogo(t: TemaId, e: ColorScheme) {
    val w = size.width; val h = size.height; val d = 1.dp.toPx()
    when (t) {
        TemaId.ROMANO -> {
            // Tabula ansata: doble filete por dentro y una cola de milano a cada lado.
            drawRect(e.primary.copy(alpha = 0.35f), Offset(7 * d, 7 * d), Size(w - 14 * d, h - 14 * d), style = Stroke(0.8f * d))
            drawRect(e.primary.copy(alpha = 0.2f), Offset(10 * d, 10 * d), Size(w - 20 * d, h - 20 * d), style = Stroke(0.6f * d))
            listOf(1f, -1f).forEach { s ->
                val x0 = if (s > 0f) 7 * d else w - 7 * d
                drawPath(Path().apply { moveTo(x0, h / 2 - 9 * d); lineTo(x0 + s * 8 * d, h / 2); lineTo(x0, h / 2 + 9 * d); close() }, e.primary.copy(alpha = 0.5f))
            }
        }
        TemaId.URBE -> {
            // La jamba del portón por dentro (sigue el arco) y, abajo, el umbral de travertino con sus dos tornillos.
            val f = 16 * d
            drawPath(Path().apply { moveTo(7 * d, h - 7 * d); lineTo(7 * d, f + 6 * d); quadraticTo(w / 2, -f + 14 * d, w - 7 * d, f + 6 * d); lineTo(w - 7 * d, h - 7 * d) },
                e.onSurface.copy(alpha = 0.28f), style = Stroke(1.2f * d))
            drawRect(e.tertiary.copy(alpha = 0.35f), Offset(7 * d, h - 10 * d), Size(w - 14 * d, 3 * d))
            listOf(14 * d, w - 14 * d).forEach { x -> drawCircle(e.tertiary.copy(alpha = 0.7f), 1.8f * d, Offset(x, h - 15 * d)) }
            // La clave del arco, arriba al centro.
            drawPath(Path().apply { moveTo(w / 2 - 6 * d, 4 * d); lineTo(w / 2 + 6 * d, 4 * d); lineTo(w / 2 + 4 * d, 13 * d); lineTo(w / 2 - 4 * d, 13 * d); close() }, e.primary.copy(alpha = 0.55f))
        }
        TemaId.CIBER -> {
            // Recuadro punteado de consola, el indicador «>_» arriba y el cursor encendido abajo.
            drawRect(e.primary.copy(alpha = 0.45f), Offset(5 * d, 5 * d), Size(w - 10 * d, h - 10 * d),
                style = Stroke(d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * d, 3 * d))))
            drawPath(Path().apply { moveTo(11 * d, 9 * d); lineTo(15 * d, 12 * d); lineTo(11 * d, 15 * d) }, e.secondary, style = Stroke(1.4f * d))
            drawLine(e.secondary, Offset(17 * d, 15 * d), Offset(23 * d, 15 * d), 1.4f * d)
            drawRect(e.secondary, Offset(w - 16 * d, h - 20 * d), Size(6 * d, 10 * d))
        }
        TemaId.NEON -> {
            // El tubo de neón que bordea la placa, con su halo, y la ménsula de la que cuelga arriba.
            drawRoundRect(e.primary.copy(alpha = 0.22f), Offset(5 * d, 5 * d), Size(w - 10 * d, h - 10 * d), CornerRadius(18 * d), style = Stroke(6 * d))
            drawRoundRect(e.primary, Offset(5 * d, 5 * d), Size(w - 10 * d, h - 10 * d), CornerRadius(18 * d), style = Stroke(1.4f * d))
            listOf(0.3f, 0.7f).forEach { f -> drawLine(e.outline, Offset(w * f, 0f), Offset(w * f, 6 * d), 1.6f * d) }
            drawCircle(e.tertiary, 2.2f * d, Offset(w - 16 * d, h - 16 * d))
        }
        TemaId.HELENO -> {
            // El tímpano con su cornisa, un sol en el vértice y dentículos abajo.
            val g = 16 * d
            drawPath(Path().apply { moveTo(7 * d, g + 4 * d); lineTo(w / 2, 5 * d); lineTo(w - 7 * d, g + 4 * d); close() }, e.tertiary.copy(alpha = 0.7f), style = Stroke(d))
            drawCircle(e.tertiary, 2.2f * d, Offset(w / 2, 13 * d))
            var x = 10 * d
            while (x < w - 13 * d) { drawRect(e.tertiary.copy(alpha = 0.5f), Offset(x, h - 9 * d), Size(3.5f * d, 3 * d)); x += 7 * d }
        }
        TemaId.EGIPTO -> {
            // El disco solar alado bajo el arco de la estela, y la cenefa de colores al pie.
            val g = 14 * d
            drawPath(Path().apply { moveTo(6 * d, g + 6 * d); quadraticTo(w / 2, -g * 0.8f + 12 * d, w - 6 * d, g + 6 * d) }, e.tertiary.copy(alpha = 0.6f), style = Stroke(d))
            val c = Offset(w / 2, 16 * d)
            listOf(-1f, 1f).forEach { s ->
                drawPath(Path().apply {
                    moveTo(c.x + s * 5 * d, c.y - 2 * d); quadraticTo(c.x + s * 18 * d, c.y - 7 * d, c.x + s * 34 * d, c.y - 3 * d)
                    lineTo(c.x + s * 30 * d, c.y + 1 * d); quadraticTo(c.x + s * 18 * d, c.y + 1 * d, c.x + s * 5 * d, c.y + 3 * d); close()
                }, e.primary.copy(alpha = 0.75f))
                for (k in 1..3) drawLine(e.tertiary.copy(alpha = 0.7f), Offset(c.x + s * (8 + k * 6) * d, c.y - 3.5f * d), Offset(c.x + s * (8 + k * 6) * d, c.y + 1.5f * d), 0.7f * d)
            }
            drawCircle(e.secondary, 4.5f * d, c)
            drawCircle(e.tertiary, 4.5f * d, c, style = Stroke(0.8f * d))
            val colores = listOf(e.primary, e.tertiary, e.secondary, e.tertiary)
            var x = 10 * d; var k = 0
            while (x < w - 13 * d) { drawRect(colores[k % 4].copy(alpha = 0.55f), Offset(x, h - 9 * d), Size(4.5f * d, 3 * d)); x += 6 * d; k++ }
        }
        TemaId.MEDIEVAL -> {
            // Cuartos de anillo de oro en las esquinas cóncavas y el doble pautado rojo del copista en el margen.
            val r = 14 * d
            esquinasCtl {
                drawArc(e.tertiary, 0f, 90f, false, Offset(-(r + 3 * d), -(r + 3 * d)), Size(2 * (r + 3 * d), 2 * (r + 3 * d)), style = Stroke(1.4f * d))
                drawCircle(e.tertiary, 2 * d, Offset(r + 6 * d, r + 6 * d))
            }
            drawLine(e.primary.copy(alpha = 0.35f), Offset(12 * d, r + 10 * d), Offset(12 * d, h - r - 10 * d), 0.8f * d)
            drawLine(e.primary.copy(alpha = 0.35f), Offset(14.5f * d, r + 10 * d), Offset(14.5f * d, h - r - 10 * d), 0.8f * d)
        }
        TemaId.TEMPLARIO -> {
            // El adarve por dentro: una línea de acero bajo las almenas, clavos de hierro en las esquinas, la cruz patada
            // en medio de arriba y una costura de malla a lo largo del pie.
            drawLine(e.secondary.copy(alpha = 0.5f), Offset(8 * d, 14 * d), Offset(w - 8 * d, 14 * d), 1.2f * d)
            esquinasCtl { drawCircle(e.tertiary, 2.2f * d, Offset(10 * d, 22 * d)) }
            drawPath(cruzPatada(Offset(w / 2, 26 * d), 7 * d), e.primary.copy(alpha = 0.8f))
            var x = 14 * d
            while (x < w - 14 * d) { drawCircle(e.secondary.copy(alpha = 0.35f), 2.2f * d, Offset(x, h - 9 * d), style = Stroke(0.8f * d)); x += 3.6f * d }
        }
        TemaId.DISTOPIA -> {
            // Dos perforaciones de archivador y un sello de goma ladeado abajo a la izquierda.
            listOf(0.3f, 0.7f).forEach { fy ->
                drawCircle(e.onSurface.copy(alpha = 0.18f), 3.5f * d, Offset(11 * d, h * fy))
                drawCircle(e.onSurface.copy(alpha = 0.5f), 3.5f * d, Offset(11 * d, h * fy), style = Stroke(d))
            }
            val o = Offset(14 * d, h - 34 * d)
            rotate(-8f, Offset(o.x + 26 * d, o.y + 10 * d)) {
                drawRect(e.primary.copy(alpha = 0.55f), o, Size(52 * d, 20 * d), style = Stroke(1.6f * d))
                drawRect(e.primary.copy(alpha = 0.55f), Offset(o.x + 3 * d, o.y + 3 * d), Size(46 * d, 14 * d), style = Stroke(0.7f * d))
                drawLine(e.primary.copy(alpha = 0.5f), Offset(o.x + 8 * d, o.y + 8 * d), Offset(o.x + 40 * d, o.y + 8 * d), 2 * d)
                drawLine(e.primary.copy(alpha = 0.5f), Offset(o.x + 8 * d, o.y + 13 * d), Offset(o.x + 30 * d, o.y + 13 * d), 2 * d)
            }
        }
        TemaId.REFUGIO -> {
            // Panel con su línea interior, cuatro pernos y un manómetro arriba.
            drawRoundRect(e.primary.copy(alpha = 0.5f), Offset(6 * d, 6 * d), Size(w - 12 * d, h - 12 * d), CornerRadius(18 * d), style = Stroke(1.2f * d))
            listOf(Offset(14 * d, 14 * d), Offset(w - 14 * d, 14 * d), Offset(14 * d, h - 14 * d), Offset(w - 14 * d, h - 14 * d)).forEach { c ->
                drawCircle(e.secondary, 2.6f * d, c); drawCircle(e.surface.copy(alpha = 0.7f), d, Offset(c.x - 0.8f * d, c.y - 0.8f * d))
            }
            drawArc(e.primary.copy(alpha = 0.6f), 180f, 180f, false, Offset(w / 2 - 9 * d, 7 * d), Size(18 * d, 18 * d), style = Stroke(1.2f * d))
            drawLine(e.primary, Offset(w / 2, 16 * d), Offset(w / 2 + 5 * d, 10 * d), 1.2f * d)
        }
        TemaId.ORDEN -> {
            // Chaflán interior de latón, pernos en las cuatro esquinas y un galón carmesí arriba.
            val i = 5 * d; val c = 9 * d
            drawPath(Path().apply {
                moveTo(i + c, i); lineTo(w - i - c, i); lineTo(w - i, i + c); lineTo(w - i, h - i - c); lineTo(w - i - c, h - i)
                lineTo(i + c, h - i); lineTo(i, h - i - c); lineTo(i, i + c); close()
            }, e.secondary.copy(alpha = 0.8f), style = Stroke(1.2f * d))
            esquinasCtl { drawCircle(e.secondary, 2 * d, Offset(12 * d, 12 * d)) }
            drawRect(e.primary, Offset(20 * d, i), Size(minOf(w * 0.3f, 90 * d), 3 * d))
        }
        TemaId.VAPOR -> {
            // Una hilera de remaches de latón por dentro del borde y, arriba a la derecha, el manómetro de la caldera.
            val i = 9 * d
            drawRect(e.primary.copy(alpha = 0.35f), Offset(i, i), Size(w - 2 * i, h - 2 * i), style = Stroke(0.8f * d))
            var x = i + 14 * d
            while (x < w - i - 40 * d) { remacheVapor(Offset(x, i + 5 * d), 1.6f * d, e.primary.copy(alpha = 0.7f)); x += 18 * d }
            manometroVapor(Offset(w - i - 16 * d, i + 14 * d), 12 * d, e.primary, e.surface, e.onSurface, e.tertiary, 0.6f)
        }
        TemaId.LABORATORIO -> {
            // Graduación por el borde derecho y la etiqueta de muestra arriba.
            var y = 26 * d; var n = 0
            while (y < h - 14 * d) {
                drawLine(e.secondary.copy(alpha = 0.6f), Offset(w - 8 * d, y), Offset(w - 8 * d - (if (n % 5 == 0) 6 * d else 3 * d), y), 0.9f * d)
                y += 6 * d; n++
            }
            drawRoundRect(e.primary.copy(alpha = 0.6f), Offset(w - 62 * d, 8 * d), Size(40 * d, 12 * d), CornerRadius(3 * d), style = Stroke(0.9f * d))
            drawLine(e.primary.copy(alpha = 0.5f), Offset(w - 58 * d, 14 * d), Offset(w - 36 * d, 14 * d), 0.9f * d)
            drawCircle(e.primary.copy(alpha = 0.6f), 1.8f * d, Offset(w - 29 * d, 14 * d))
        }
        TemaId.COLONOS -> {
            // Raya inglesa de cabecera y la línea de perforación sobre los dientes.
            drawRect(e.onSurface.copy(alpha = 0.75f), Offset(10 * d, 9 * d), Size(w - 20 * d, 2 * d))
            drawRect(e.onSurface.copy(alpha = 0.6f), Offset(10 * d, 13 * d), Size(w - 20 * d, 0.7f * d))
            drawLine(e.onSurface.copy(alpha = 0.35f), Offset(10 * d, h - 10 * d), Offset(w - 10 * d, h - 10 * d), 0.8f * d,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * d, 2 * d)))
        }
        TemaId.NAPOLEON -> {
            // Filete de oro y una estrella junto a cada esquina escalonada.
            drawRect(e.tertiary.copy(alpha = 0.6f), Offset(11 * d, 11 * d), Size(w - 22 * d, h - 22 * d), style = Stroke(d))
            esquinasCtl { estrellaCtl(Offset(17 * d, 17 * d), 4 * d, e.tertiary) }
        }
        TemaId.BOTANICO -> {
            // El alfiler del entomólogo arriba y una fronda de helecho abajo a la izquierda.
            val c = Offset(w / 2, 9 * d)
            drawLine(e.onSurface.copy(alpha = 0.25f), c, Offset(c.x + 9 * d, c.y + 7 * d), 1.2f * d)
            drawCircle(e.secondary, 3.4f * d, c)
            drawCircle(e.surface.copy(alpha = 0.6f), 1.2f * d, Offset(c.x - d, c.y - d))
            val p0 = Offset(10 * d, h - 10 * d); val p1 = Offset(12 * d, h - 28 * d); val p2 = Offset(30 * d, h - 36 * d)
            drawPath(Path().apply { moveTo(p0.x, p0.y); quadraticTo(p1.x, p1.y, p2.x, p2.y) }, e.secondary.copy(alpha = 0.7f), style = Stroke(1.2f * d))
            for (k in 1..5) {
                val s = k / 6f; val u = 1 - s
                val q = Offset(u * u * p0.x + 2 * u * s * p1.x + s * s * p2.x, u * u * p0.y + 2 * u * s * p1.y + s * s * p2.y)
                val l = (6 - k) * 0.9f * d + 2 * d
                drawLine(e.secondary.copy(alpha = 0.7f), q, Offset(q.x + l, q.y + l * 0.3f), 1.3f * d, cap = StrokeCap.Round)
                drawLine(e.secondary.copy(alpha = 0.7f), q, Offset(q.x - l * 0.5f, q.y - l * 0.8f), 1.3f * d, cap = StrokeCap.Round)
            }
        }
        TemaId.RUINAS -> {
            // Hiedra que cuelga del borde de arriba, desde la esquina izquierda, y una grieta que sube desde abajo a la derecha.
            fun hoja(q: Offset, s: Float) = Path().apply {
                moveTo(q.x, q.y); lineTo(q.x - s * 0.55f, q.y - s * 0.15f); lineTo(q.x - s * 0.8f, q.y - s * 0.55f); lineTo(q.x - s * 0.32f, q.y - s * 0.55f)
                lineTo(q.x, q.y - s); lineTo(q.x + s * 0.32f, q.y - s * 0.55f); lineTo(q.x + s * 0.8f, q.y - s * 0.55f); lineTo(q.x + s * 0.55f, q.y - s * 0.15f); close()
            }
            val tallo = Path().apply { moveTo(0f, 6 * d); cubicTo(20 * d, 2 * d, 40 * d, 14 * d, 64 * d, 8 * d); cubicTo(76 * d, 5 * d, 84 * d, 18 * d, 82 * d, 34 * d) }
            drawPath(tallo, e.primary.copy(alpha = 0.6f), style = Stroke(1.2f * d))
            listOf(Offset(14 * d, 5 * d) to 160f, Offset(30 * d, 8 * d) to 200f, Offset(48 * d, 11 * d) to 170f, Offset(66 * d, 8 * d) to 210f,
                Offset(80 * d, 22 * d) to 175f, Offset(82 * d, 34 * d) to 185f).forEach { (q, g) ->
                rotate(g, q) { drawPath(hoja(q, 5.5f * d), e.primary.copy(alpha = 0.75f)) }
            }
            drawPath(Path().apply { moveTo(w - 18 * d, h); lineTo(w - 22 * d, h - 10 * d); lineTo(w - 16 * d, h - 18 * d); lineTo(w - 21 * d, h - 30 * d); moveTo(w - 16 * d, h - 18 * d); lineTo(w - 8 * d, h - 24 * d) },
                e.onSurface.copy(alpha = 0.3f), style = Stroke(1 * d))
        }
        TemaId.NAVIDAD -> {
            // Copos bajo el festón y una ramita de acebo arriba a la derecha.
            var x = 9 * d
            while (x < w - 6 * d) {
                drawCircle(Color.White.copy(alpha = 0.9f), 2.2f * d, Offset(x, 8 * d))
                drawCircle(e.outline.copy(alpha = 0.5f), 2.2f * d, Offset(x, 8 * d), style = Stroke(0.6f * d))
                x += 18 * d
            }
            val c = Offset(w - 24 * d, 20 * d)
            rotate(-25f, c) { drawOval(e.secondary, Offset(c.x - 9 * d, c.y - 2 * d), Size(9 * d, 4 * d)) }
            rotate(25f, c) { drawOval(e.secondary, Offset(c.x, c.y - 2 * d), Size(9 * d, 4 * d)) }
            listOf(Offset(-1.4f, 1.6f), Offset(1.4f, 1.6f), Offset(0f, -0.8f)).forEach { o -> drawCircle(e.primary, 1.7f * d, Offset(c.x + o.x * d, c.y + o.y * d)) }
        }
        TemaId.SEDA -> {
            // Una borla que cuelga del alero y el sello de cinabrio abajo a la derecha.
            val a = 10 * d; val x = a + 8 * d
            drawLine(e.primary, Offset(x, a), Offset(x, a + 13 * d), d)
            drawCircle(e.primary, 2.2f * d, Offset(x, a + 15 * d))
            for (k in -1..1) drawLine(e.primary.copy(alpha = 0.8f), Offset(x, a + 16 * d), Offset(x + k * 1.6f * d, a + 22 * d), 0.8f * d)
            val s = 14 * d; val o = Offset(w - a - 8 * d - s, h - 8 * d - s)
            drawRect(e.primary.copy(alpha = 0.85f), o, Size(s, s))
            drawRect(e.surface.copy(alpha = 0.8f), Offset(o.x + 2.5f * d, o.y + 2.5f * d), Size(s - 5 * d, s - 5 * d), style = Stroke(0.8f * d))
            drawLine(e.surface.copy(alpha = 0.8f), Offset(o.x + s / 2, o.y + 4 * d), Offset(o.x + s / 2, o.y + s - 4 * d), 0.8f * d)
        }
        TemaId.GOTICO -> {
            // El arco por dentro, en oro, y un rosetón bajo la clave.
            val a = 44 * d; val i = 7 * d
            drawPath(Path().apply {
                moveTo(i, h - i); lineTo(i, a + 2 * d); quadraticTo(i, a * 0.2f + 6 * d, w / 2, i); quadraticTo(w - i, a * 0.2f + 6 * d, w - i, a + 2 * d)
                lineTo(w - i, h - i); close()
            }, e.tertiary.copy(alpha = 0.7f), style = Stroke(1.2f * d))
            val c = Offset(w / 2, 22 * d)
            drawCircle(e.tertiary, 6 * d, c, style = Stroke(d))
            for (k in 0 until 6) {
                val ang = k * PI / 3
                drawCircle(e.tertiary.copy(alpha = 0.8f), 2 * d, Offset(c.x + 3.2f * d * cos(ang).toFloat(), c.y + 3.2f * d * sin(ang).toFloat()), style = Stroke(0.8f * d))
            }
        }
        TemaId.ARCANO -> {
            // El doblez de la esquina, el margen rojo desvaído y, abajo a la derecha, el sello del archivo con su signo.
            val doblez = 18 * d
            drawPath(Path().apply { moveTo(w - doblez, 0f); lineTo(w - doblez, doblez); lineTo(w, doblez); close() }, e.onSurface.copy(alpha = 0.08f))
            drawPath(Path().apply { moveTo(w - doblez, 0f); lineTo(w - doblez, doblez); lineTo(w, doblez) }, e.onSurface.copy(alpha = 0.3f), style = Stroke(0.8f * d))
            drawLine(e.tertiary.copy(alpha = 0.35f), Offset(16 * d, 0f), Offset(16 * d, h - 6 * d), d)
            val c = Offset(w - 30 * d, h - 30 * d)
            drawCircle(e.tertiary.copy(alpha = 0.22f), 16 * d, c, style = Stroke(1.2f * d))
            drawCircle(e.tertiary.copy(alpha = 0.16f), 13 * d, c, style = Stroke(0.6f * d))
            with(Contenedores) { signoOjo(c, 8 * d, e.tertiary.copy(alpha = 0.3f), 0.8f * d) }
        }
        TemaId.ESPACIO -> {
            // Escuadras de visor abajo, chaflanes interiores arriba y una órbita de puntos con su planeta.
            listOf(1f, -1f).forEach { s ->
                val x0 = if (s > 0f) 8 * d else w - 8 * d
                drawPath(Path().apply { moveTo(x0, h - 20 * d); lineTo(x0, h - 8 * d); lineTo(x0 + s * 12 * d, h - 8 * d) }, e.primary, style = Stroke(1.4f * d))
                drawLine(e.primary.copy(alpha = 0.5f), Offset(x0, 25 * d), Offset(x0 + s * 17 * d, 8 * d), d)
            }
            var x = 32 * d
            while (x < w - 32 * d) { drawCircle(e.primary.copy(alpha = 0.6f), 0.9f * d, Offset(x, 8 * d)); x += 6 * d }
            drawCircle(e.primary, 2.4f * d, Offset(w * 0.7f, 8 * d))
        }
        TemaId.ABISMO -> {
            // La plancha remachada del casco: una fila de pernos de latón a cada lado y, abajo a la derecha, el
            // profundímetro con su aguja.
            listOf(9 * d, w - 9 * d).forEach { x ->
                var y = 30 * d
                while (y < h - 14 * d) { drawCircle(e.secondary.copy(alpha = 0.75f), 2 * d, Offset(x, y)); drawCircle(e.surface.copy(alpha = 0.6f), 0.7f * d, Offset(x - 0.6f * d, y - 0.6f * d)); y += 26 * d }
            }
            val c = Offset(w - 30 * d, h - 22 * d); val r = 11 * d
            drawCircle(e.primary.copy(alpha = 0.5f), r, c, style = Stroke(1.2f * d))
            for (k in 0..6) {
                val a = Math.toRadians(150.0 + k * 40.0)
                drawLine(e.primary.copy(alpha = 0.6f), c + Offset((r * 0.72f * cos(a)).toFloat(), (r * 0.72f * sin(a)).toFloat()), c + Offset((r * cos(a)).toFloat(), (r * sin(a)).toFloat()), 0.8f * d)
            }
            val ag = Math.toRadians(300.0)
            drawLine(e.tertiary, c, c + Offset((r * 0.8f * cos(ag)).toFloat(), (r * 0.8f * sin(ag)).toFloat()), 1.2f * d)
            drawCircle(e.tertiary, 1.4f * d, c)
        }
        TemaId.ANDALUS -> {
            // El arco por dentro, una estrella en la clave y una cenefa de estrellas de ocho puntas abajo.
            drawPath(Path().apply {
                addRoundRect(RoundRect(6 * d, 6 * d, w - 6 * d, h - 6 * d, CornerRadius(42 * d), CornerRadius(42 * d), CornerRadius(2 * d), CornerRadius(2 * d)))
            }, e.tertiary.copy(alpha = 0.6f), style = Stroke(d))
            estrellaCtl(Offset(w / 2, 15 * d), 5 * d, e.tertiary, puntas = 8, dentro = 0.55f)
            var x = 18 * d
            while (x < w - 16 * d) { estrellaCtl(Offset(x, h - 12 * d), 4 * d, e.tertiary.copy(alpha = 0.65f), puntas = 8, dentro = 0.6f); x += 14 * d }
        }
        TemaId.MONGOL -> {
            // La corona de la ger (toono) con sus varas, y la costura del fieltro.
            val c = Offset(w / 2, 12 * d)
            drawCircle(e.primary, 6 * d, c, style = Stroke(1.2f * d))
            drawLine(e.primary, Offset(c.x - 4 * d, c.y), Offset(c.x + 4 * d, c.y), d)
            drawLine(e.primary, Offset(c.x, c.y - 4 * d), Offset(c.x, c.y + 4 * d), d)
            listOf(1f, -1f).forEach { s ->
                drawLine(e.primary.copy(alpha = 0.5f), Offset(c.x + s * 7 * d, c.y + d), Offset(c.x + s * 44 * d, c.y + 5 * d), 0.9f * d)
                drawLine(e.primary.copy(alpha = 0.5f), Offset(c.x + s * 6 * d, c.y + 4 * d), Offset(c.x + s * 32 * d, c.y + 11 * d), 0.9f * d)
            }
            drawRoundRect(e.primary.copy(alpha = 0.5f), Offset(6 * d, 16 * d), Size(w - 12 * d, h - 22 * d), CornerRadius(8 * d),
                style = Stroke(d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5 * d, 3 * d))))
        }
        TemaId.RENACIMIENTO -> {
            // La esquina doblada, líneas de construcción que se pasan y un círculo de compás.
            val c = 26 * d
            val pliegue = Path().apply { moveTo(w - c, h); lineTo(w - c, h - c); lineTo(w, h - c); close() }
            drawPath(pliegue, e.primary.copy(alpha = 0.18f))
            drawPath(pliegue, e.primary.copy(alpha = 0.5f), style = Stroke(0.8f * d))
            drawLine(e.primary.copy(alpha = 0.4f), Offset(0f, 14 * d), Offset(46 * d, 14 * d), 0.7f * d)
            drawLine(e.primary.copy(alpha = 0.4f), Offset(14 * d, 0f), Offset(14 * d, 46 * d), 0.7f * d)
            drawCircle(e.primary.copy(alpha = 0.3f), 10 * d, Offset(w - 20 * d, 20 * d), style = Stroke(0.7f * d))
            drawLine(e.primary.copy(alpha = 0.3f), Offset(w - 23 * d, 20 * d), Offset(w - 17 * d, 20 * d), 0.7f * d)
            drawLine(e.primary.copy(alpha = 0.3f), Offset(w - 20 * d, 17 * d), Offset(w - 20 * d, 23 * d), 0.7f * d)
        }
        TemaId.PIRATA -> {
            // Un cabo que corre por dentro del borde, las cantoneras de latón y una rosa pequeña arriba a la derecha.
            val cabo = Color(0xFFB59A62).copy(alpha = 0.6f)
            drawRect(cabo, Offset(7 * d, 7 * d), Size(w - 14 * d, h - 20 * d), style = Stroke(1.4f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.6f * d, 1.4f * d))))
            val laton = e.secondary.copy(alpha = 0.6f); val l = 14 * d
            listOf(Offset(0f, 0f) to Offset(1f, 1f), Offset(w, 0f) to Offset(-1f, 1f)).forEach { (o, s) ->
                drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x + s.x * l, o.y); lineTo(o.x, o.y + s.y * l); close() }, laton)
            }
            val rosa = Offset(w - 26 * d, 26 * d)
            for (k in 0 until 8) {
                val a = -Math.PI / 2 + k * Math.PI / 4; val rr = (if (k % 2 == 0) 10f else 6f) * d
                drawLine(if (k == 0) e.tertiary.copy(alpha = 0.7f) else e.primary.copy(alpha = 0.4f), rosa, Offset(rosa.x + (rr * kotlin.math.cos(a)).toFloat(), rosa.y + (rr * kotlin.math.sin(a)).toFloat()), 0.9f * d)
            }
        }
        TemaId.URSS -> {
            // Una franja roja paralela al corte y una barra negra abajo.
            val c = 30 * d
            drawPath(Path().apply { moveTo(w - c - 14 * d, 0f); lineTo(w - c - 6 * d, 0f); lineTo(w, c + 6 * d); lineTo(w, c + 14 * d); close() }, e.primary)
            drawRect(e.onSurface, Offset(10 * d, h - 12 * d), Size(minOf(w * 0.35f, 110 * d), 4 * d))
        }
        TemaId.REVOLUCION -> {
            // Franja tricolor arriba y una escarapela abajo a la izquierda.
            val tercio = (w - 20 * d) / 3
            listOf(e.secondary, e.outline, e.primary).forEachIndexed { k, col -> drawRect(col, Offset(10 * d + k * tercio, 6 * d), Size(tercio, 3 * d)) }
            val c = Offset(18 * d, h - 18 * d)
            drawCircle(e.secondary, 7 * d, c); drawCircle(Color.White, 4.6f * d, c); drawCircle(e.primary, 2.4f * d, c)
        }
        TemaId.ANTIGUAS -> {
            // Una línea tallada que sigue la losa y una gema engastada en cada esquina.
            drawPath(Path().apply {
                moveTo(12 * d, 7 * d); lineTo(w - 18 * d, 9 * d); lineTo(w - 7 * d, 15 * d); lineTo(w - 9 * d, h - 13 * d); lineTo(w - 16 * d, h - 7 * d)
                lineTo(14 * d, h - 9 * d); lineTo(7 * d, h - 16 * d); lineTo(8 * d, 14 * d); close()
            }, e.secondary.copy(alpha = 0.6f), style = Stroke(d))
            listOf(Offset(14 * d, 14 * d), Offset(w - 16 * d, 16 * d), Offset(w - 16 * d, h - 14 * d), Offset(14 * d, h - 16 * d)).forEach { romboCtl(it, 2.6f * d, 3.8f * d, e.primary) }
        }
    }
}

/** El filete bajo el título del diálogo (y en la cabecera del menú). Se dibuja en una franja de 10 dp. */
private fun DrawScope.fileteDialogo(t: TemaId, e: ColorScheme) {
    val w = size.width; val cy = size.height / 2; val d = 1.dp.toPx()
    when (t) {
        // Filete con una interpunción triangular en medio, como en las inscripciones.
        TemaId.ROMANO -> {
            drawLine(e.primary.copy(alpha = 0.5f), Offset(0f, cy), Offset(w / 2 - 8 * d, cy), d)
            drawLine(e.primary.copy(alpha = 0.5f), Offset(w / 2 + 8 * d, cy), Offset(w, cy), d)
            drawPath(Path().apply { moveTo(w / 2 - 4 * d, cy - 3 * d); lineTo(w / 2 + 4 * d, cy - 3 * d); lineTo(w / 2, cy + 3 * d); close() }, e.primary)
        }
        // Las vías del tranvía: dos rieles con sus durmientes.
        TemaId.URBE -> {
            drawLine(e.onSurface.copy(alpha = 0.4f), Offset(0f, cy - 2.5f * d), Offset(w, cy - 2.5f * d), 0.9f * d)
            drawLine(e.onSurface.copy(alpha = 0.4f), Offset(0f, cy + 2.5f * d), Offset(w, cy + 2.5f * d), 0.9f * d)
            var x = 3 * d; while (x < w) { drawLine(e.primary.copy(alpha = 0.45f), Offset(x, cy - 4 * d), Offset(x, cy + 4 * d), 1.6f * d); x += 9 * d }
        }
        // Guiones de consola que terminan en el cursor.
        TemaId.CIBER -> {
            var x = 0f
            while (x < w - 14 * d) { drawLine(e.primary.copy(alpha = 0.7f), Offset(x, cy), Offset(x + 3 * d, cy), 1.2f * d); x += 6 * d }
            drawRect(e.secondary, Offset(w - 8 * d, cy - 4 * d), Size(5 * d, 8 * d))
        }
        // Un tubo ámbar con su halo, partido al centro donde va un rombo magenta.
        TemaId.NEON -> {
            listOf(0f to w * 0.45f, w * 0.55f to w).forEach { (a, b) ->
                drawLine(e.secondary.copy(alpha = 0.25f), Offset(a, cy), Offset(b, cy), 5 * d, StrokeCap.Round)
                drawLine(e.secondary, Offset(a, cy), Offset(b, cy), 1.4f * d, StrokeCap.Round)
            }
            drawPath(Path().apply { moveTo(w / 2, cy - 4 * d); lineTo(w / 2 + 4 * d, cy); lineTo(w / 2, cy + 4 * d); lineTo(w / 2 - 4 * d, cy); close() }, e.primary)
        }
        // Una greca sobre su línea de base.
        TemaId.HELENO -> {
            val p = Path(); var x = 0f
            while (x + 6 * d <= w) {
                p.moveTo(x, cy + 4 * d); p.lineTo(x, cy - 4 * d); p.lineTo(x + 6 * d, cy - 4 * d); p.lineTo(x + 6 * d, cy + 1.5f * d)
                p.lineTo(x + 3 * d, cy + 1.5f * d); p.lineTo(x + 3 * d, cy - d)
                x += 10 * d
            }
            drawPath(p, e.tertiary, style = Stroke(d, cap = StrokeCap.Square))
            drawLine(e.tertiary, Offset(0f, cy + 4 * d), Offset(w, cy + 4 * d), d)
        }
        // Un filete con el ankh en medio.
        TemaId.EGIPTO -> {
            drawLine(e.tertiary.copy(alpha = 0.7f), Offset(0f, cy), Offset(w / 2 - 9 * d, cy), d)
            drawLine(e.tertiary.copy(alpha = 0.7f), Offset(w / 2 + 9 * d, cy), Offset(w, cy), d)
            signoEgipcio(0, Offset(w / 2, cy), 12 * d, e.primary, 1.3f * d)
        }
        // Rúbrica roja con puntos de oro y un trébol en medio.
        TemaId.MEDIEVAL -> {
            drawLine(e.primary, Offset(8 * d, cy), Offset(w - 8 * d, cy), 1.2f * d)
            drawCircle(e.tertiary, 2.2f * d, Offset(4 * d, cy)); drawCircle(e.tertiary, 2.2f * d, Offset(w - 4 * d, cy))
            drawCircle(e.surface, 6 * d, Offset(w / 2, cy))
            listOf(Offset(-2.5f, 1f), Offset(2.5f, 1f), Offset(0f, -2.5f)).forEach { o -> drawCircle(e.tertiary, 2 * d, Offset(w / 2 + o.x * d, cy + o.y * d)) }
        }
        // Una espada tendida: la hoja de acero, la guarda en cruz y el pomo; en medio, la cruz roja.
        TemaId.TEMPLARIO -> {
            drawLine(e.secondary, Offset(w * 0.14f, cy), Offset(w - 6 * d, cy), 1.6f * d)
            drawLine(e.secondary, Offset(w * 0.14f, cy - 6 * d), Offset(w * 0.14f, cy + 6 * d), 2.2f * d)
            drawLine(e.secondary, Offset(w * 0.06f, cy), Offset(w * 0.14f, cy), 2.6f * d); drawCircle(e.secondary, 3 * d, Offset(w * 0.05f, cy))
            drawCircle(e.surface, 8 * d, Offset(w / 2, cy)); drawPath(cruzPatada(Offset(w / 2, cy), 6 * d), e.primary)
        }
        // Barra negra de formulario con un trazo rojo debajo.
        TemaId.DISTOPIA -> {
            drawRect(e.onSurface, Offset(0f, cy - 3 * d), Size(w, 3 * d))
            drawRect(e.primary, Offset(0f, cy + 2 * d), Size(w * 0.4f, 1.2f * d))
        }
        // Línea redonda con un destello atómico en medio.
        TemaId.REFUGIO -> {
            drawLine(e.primary.copy(alpha = 0.6f), Offset(2 * d, cy), Offset(w - 2 * d, cy), 1.4f * d, cap = StrokeCap.Round)
            drawCircle(e.surface, 7 * d, Offset(w / 2, cy))
            estrellaCtl(Offset(w / 2, cy), 5 * d, e.primary, puntas = 4, dentro = 0.3f)
            drawCircle(e.primary, 6.5f * d, Offset(w / 2, cy), style = Stroke(0.8f * d))
        }
        // Línea de latón con su tramo carmesí y galones a la derecha.
        TemaId.ORDEN -> {
            drawLine(e.secondary, Offset(0f, cy), Offset(w - 20 * d, cy), 1.2f * d)
            drawRect(e.primary, Offset(0f, cy - 1.5f * d), Size(minOf(w, 40 * d), 3 * d))
            for (k in 0 until 3) {
                val x = w - 16 * d + k * 5 * d
                drawPath(Path().apply { moveTo(x, cy - 3 * d); lineTo(x + 3 * d, cy); lineTo(x, cy + 3 * d) }, e.secondary, style = Stroke(1.2f * d))
            }
        }
        // El tubo de vapor con una brida en medio y la rueda de la llave de paso.
        TemaId.VAPOR -> {
            drawLine(e.primary.copy(alpha = 0.6f), Offset(0f, cy), Offset(w, cy), 2.4f * d)
            drawRect(e.primary, Offset(w / 2 - 1.5f * d, cy - 4 * d), Size(3 * d, 8 * d))
            val llave = Offset(w * 0.82f, cy)
            drawCircle(e.surface, 5 * d, llave); drawCircle(e.tertiary, 5 * d, llave, style = Stroke(1.2f * d))
            drawLine(e.tertiary, llave - Offset(4 * d, 0f), llave + Offset(4 * d, 0f), 1f * d); drawLine(e.tertiary, llave - Offset(0f, 4 * d), llave + Offset(0f, 4 * d), 1f * d)
        }
        // Regla graduada.
        TemaId.LABORATORIO -> {
            drawLine(e.secondary.copy(alpha = 0.6f), Offset(0f, cy + 3 * d), Offset(w, cy + 3 * d), 0.9f * d)
            var x = 0f; var n = 0
            while (x <= w) { drawLine(e.secondary.copy(alpha = 0.6f), Offset(x, cy + 3 * d), Offset(x, cy + 3 * d - (if (n % 5 == 0) 7 * d else 3.5f * d)), 0.8f * d); x += 5 * d; n++ }
        }
        // Raya inglesa de gaceta: gruesa y fina.
        TemaId.COLONOS -> {
            drawRect(e.onSurface.copy(alpha = 0.8f), Offset(0f, cy - 2.5f * d), Size(w, 2 * d))
            drawRect(e.onSurface.copy(alpha = 0.6f), Offset(0f, cy + 1.5f * d), Size(w, 0.7f * d))
        }
        // Filete de oro con una estrella y dos rombos.
        TemaId.NAPOLEON -> {
            drawLine(e.tertiary, Offset(0f, cy), Offset(w / 2 - 13 * d, cy), d)
            drawLine(e.tertiary, Offset(w / 2 + 13 * d, cy), Offset(w, cy), d)
            estrellaCtl(Offset(w / 2, cy), 4.5f * d, e.tertiary)
            romboCtl(Offset(w / 2 - 9 * d, cy), 2 * d, 2.5f * d, e.primary); romboCtl(Offset(w / 2 + 9 * d, cy), 2 * d, 2.5f * d, e.primary)
        }
        // Línea punteada a lápiz con una hoja al empezar.
        TemaId.BOTANICO -> {
            drawLine(e.onSurface.copy(alpha = 0.45f), Offset(14 * d, cy), Offset(w, cy), d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * d, 3 * d)))
            rotate(-30f, Offset(6 * d, cy)) { drawOval(e.secondary, Offset(0f, cy - 2.5f * d), Size(11 * d, 5 * d)) }
        }
        // La línea de una calle, gastada y partida, con un brote en la grieta.
        TemaId.RUINAS -> {
            val m = w * 0.42f
            drawLine(e.tertiary.copy(alpha = 0.7f), Offset(0f, cy), Offset(m - 3 * d, cy), 2 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10 * d, 6 * d)))
            drawLine(e.tertiary.copy(alpha = 0.7f), Offset(m + 3 * d, cy), Offset(w, cy), 2 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10 * d, 6 * d)))
            drawLine(e.primary, Offset(m, cy + d), Offset(m, cy - 5 * d), 1.2f * d)
            rotate(-40f, Offset(m, cy - 4 * d)) { drawOval(e.primary, Offset(m, cy - 5.5f * d), Size(5 * d, 2.6f * d)) }
            rotate(40f, Offset(m, cy - 3 * d)) { drawOval(e.primary, Offset(m - 5 * d, cy - 4.5f * d), Size(5 * d, 2.6f * d)) }
        }
        // Guirnalda que ondula, con bayas.
        TemaId.NAVIDAD -> {
            val amp = 2 * d; val lam = 16 * d
            val p = Path(); var x = 0f
            p.moveTo(0f, cy)
            while (x <= w) { p.lineTo(x, cy + amp * sin(2 * PI * x / lam).toFloat()); x += 2f }
            drawPath(p, e.secondary, style = Stroke(1.6f * d))
            var b = 4 * d
            while (b < w) { drawCircle(e.primary, 1.8f * d, Offset(b, cy + amp * sin(2 * PI * b / lam).toFloat())); b += 32 * d }
        }
        // Hilo con nudos y una moneda en medio.
        TemaId.SEDA -> {
            drawLine(e.primary.copy(alpha = 0.6f), Offset(0f, cy), Offset(w, cy), d)
            var x = 12 * d
            while (x < w) { drawCircle(e.primary, 1.8f * d, Offset(x, cy)); x += 24 * d }
            drawCircle(e.tertiary, 5 * d, Offset(w / 2, cy))
            drawRect(e.surface, Offset(w / 2 - 1.8f * d, cy - 1.8f * d), Size(3.6f * d, 3.6f * d))
        }
        // Una arquería de arcos apuntados.
        TemaId.GOTICO -> {
            val p = Path(); var x = 0f
            while (x + 10 * d <= w) {
                p.moveTo(x, cy + 4 * d); p.quadraticTo(x, cy - 2 * d, x + 5 * d, cy - 4.5f * d); p.quadraticTo(x + 10 * d, cy - 2 * d, x + 10 * d, cy + 4 * d)
                x += 12 * d
            }
            drawPath(p, e.tertiary, style = Stroke(0.9f * d))
            drawLine(e.tertiary, Offset(0f, cy + 4 * d), Offset(w, cy + 4 * d), 0.9f * d)
        }
        // Una raya a pulso que tiembla, con una palabra tachada en medio.
        TemaId.ARCANO -> {
            val p = Path(); var x = 0f; p.moveTo(0f, cy)
            while (x <= w) { p.lineTo(x, cy + 0.9f * d * sin(x / (3.1f * d)) + 0.6f * d * sin(x / (11f * d))); x += 2f }
            drawPath(p, e.onSurface.copy(alpha = 0.45f), style = Stroke(0.9f * d))
            drawRect(e.surface, Offset(w / 2 - 14 * d, cy - 4 * d), Size(28 * d, 8 * d))
            listOf(-1f, 1f).forEach { s -> drawLine(e.tertiary.copy(alpha = 0.7f), Offset(w / 2 - 11 * d, cy + s * 3 * d), Offset(w / 2 + 11 * d, cy - s * 3 * d), 1.1f * d) }
        }
        // Órbita de puntos con un planeta anillado.
        TemaId.ESPACIO -> {
            var x = 0f
            while (x <= w) { drawCircle(e.primary.copy(alpha = 0.6f), 0.9f * d, Offset(x, cy)); x += 5 * d }
            val c = Offset(w * 0.7f, cy)
            drawCircle(e.surface, 4 * d, c); drawCircle(e.primary, 3 * d, c)
            drawOval(e.primary, Offset(c.x - 6 * d, c.y - 2 * d), Size(12 * d, 4 * d), style = Stroke(0.8f * d))
        }
        // Una ola larga, y en medio tres burbujas que suben.
        TemaId.ABISMO -> {
            val p = Path(); var x = 0f
            p.moveTo(0f, cy)
            while (x <= w) { p.lineTo(x, cy + 2 * d * sin(x / (9 * d))); x += 2 * d }
            drawPath(p, e.primary.copy(alpha = 0.55f), style = Stroke(d))
            listOf(Offset(-5f, -2f) to 1.6f, Offset(2f, -6f) to 2.2f, Offset(-1f, -11f) to 1.3f).forEach { (o, r) ->
                drawCircle(e.surface, (r + 0.8f) * d, Offset(w / 2 + o.x * d, cy + o.y * d))
                drawCircle(e.tertiary, r * d, Offset(w / 2 + o.x * d, cy + o.y * d), style = Stroke(0.8f * d))
            }
        }
        // Estrella de ocho puntas entre dos líneas de oro.
        TemaId.ANDALUS -> {
            drawLine(e.tertiary, Offset(0f, cy), Offset(w / 2 - 18 * d, cy), d)
            drawLine(e.tertiary, Offset(w / 2 + 18 * d, cy), Offset(w, cy), d)
            estrellaCtl(Offset(w / 2, cy), 5.5f * d, e.tertiary, puntas = 8, dentro = 0.6f)
            drawCircle(e.tertiary, 1.3f * d, Offset(w / 2 - 13 * d, cy)); drawCircle(e.tertiary, 1.3f * d, Offset(w / 2 + 13 * d, cy))
        }
        // Montes de la estepa en zigzag.
        TemaId.MONGOL -> {
            val p = Path(); var x = 0f
            p.moveTo(0f, cy + 3 * d)
            while (x + 12 * d <= w) { p.lineTo(x + 6 * d, cy - 3 * d); p.lineTo(x + 12 * d, cy + 3 * d); x += 12 * d }
            drawPath(p, e.primary, style = Stroke(1.2f * d))
        }
        // Dos trazos de sanguina y un círculo de construcción.
        TemaId.RENACIMIENTO -> {
            drawLine(e.primary.copy(alpha = 0.7f), Offset(0f, cy + d), Offset(w * 0.92f, cy - 0.5f * d), 0.8f * d)
            drawLine(e.primary.copy(alpha = 0.45f), Offset(w * 0.05f, cy + 2.5f * d), Offset(w * 0.7f, cy + 2 * d), 0.7f * d)
            drawCircle(e.primary.copy(alpha = 0.5f), 3 * d, Offset(w * 0.95f, cy), style = Stroke(0.6f * d))
        }
        // Una ola de tinta que termina en la «X» roja.
        TemaId.PIRATA -> {
            val p = Path(); var x = 0f; val l = 10 * d; p.moveTo(0f, cy)
            while (x < w * 0.86f) { p.quadraticTo(x + l / 4, cy - 2 * d, x + l / 2, cy); p.quadraticTo(x + l * 3 / 4, cy + 2 * d, x + l, cy); x += l }
            drawPath(p, e.primary.copy(alpha = 0.55f), style = Stroke(0.9f * d))
            val c = Offset(w * 0.94f, cy); val s = 3 * d
            drawLine(e.tertiary.copy(alpha = 0.8f), c - Offset(s, s), c + Offset(s, s), 1.4f * d, StrokeCap.Round)
            drawLine(e.tertiary.copy(alpha = 0.8f), c + Offset(-s, s), c + Offset(s, -s), 1.4f * d, StrokeCap.Round)
        }
        // Barra roja de cartel y una línea negra.
        TemaId.URSS -> {
            drawRect(e.primary, Offset(0f, cy - 2.5f * d), Size(w * 0.42f, 5 * d))
            drawRect(e.onSurface, Offset(w * 0.42f + 4 * d, cy - 0.5f * d), Size(w * 0.58f - 4 * d, 1.2f * d))
        }
        // Tramo azul, blanco y rojo, y la línea sigue.
        TemaId.REVOLUCION -> {
            listOf(e.secondary, e.outline, e.primary).forEachIndexed { k, col -> drawRect(col, Offset(k * 22 * d, cy - 1.5f * d), Size(22 * d, 3 * d)) }
            drawLine(e.onSurface.copy(alpha = 0.5f), Offset(70 * d, cy), Offset(w, cy), 0.8f * d)
        }
        // Línea tallada con muescas que termina en una gema.
        TemaId.ANTIGUAS -> {
            drawLine(e.secondary.copy(alpha = 0.7f), Offset(0f, cy), Offset(w - 12 * d, cy), d)
            var x = 9 * d
            while (x < w - 16 * d) { drawLine(e.secondary.copy(alpha = 0.7f), Offset(x, cy - 2.5f * d), Offset(x, cy + 2.5f * d), d); x += 18 * d }
            romboCtl(Offset(w - 6 * d, cy), 3.5f * d, 4.5f * d, e.primary)
        }
    }
}

// ---------------------------------------------------------------- deslizador, progreso y conmutador

/**
 * Deslizador con el riel y la perilla del tema (tablilla con asas, cursor de bloque, sol de Vergina, sello de cera,
 * aguja, escudo, gota, casa, estrella, hoja, esfera, moneda, lanceta, planeta, estrella de ocho puntas, punta de
 * flecha, compás, cuña, escarapela, gema). Mismos parámetros que `Slider` de Material.
 */
@Composable
fun Deslizador(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val tema = Estilo.tema
    val e = MaterialTheme.colorScheme
    val toque = interactionSource ?: remember { MutableInteractionSource() }
    val apretado by toque.collectIsPressedAsState()
    val arrastrado by toque.collectIsDraggedAsState()
    val activo = if (enabled) e.primary else e.onSurface.copy(alpha = 0.38f)
    val inactivo = e.outline.copy(alpha = if (enabled) 1f else 0.38f)
    Slider(
        value = value, onValueChange = onValueChange, modifier = modifier, enabled = enabled, onValueChangeFinished = onValueChangeFinished,
        colors = colors ?: SliderDefaults.colors(), interactionSource = toque, steps = steps,
        thumb = {
            Canvas(Modifier.size(26.dp, 30.dp)) {
                val r = size.minDimension / 2f * (if (apretado || arrastrado) 1f else 0.86f)
                perillaTema(tema, e, center, r, activo, e.surface)
            }
        },
        track = { st ->
            val rango = st.valueRange.endInclusive - st.valueRange.start
            val f = if (rango > 0f) ((st.value - st.valueRange.start) / rango).coerceIn(0f, 1f) else 0f
            Canvas(Modifier.fillMaxWidth().height(14.dp)) { rielTema(tema, e, f, st.steps, activo, inactivo) }
        },
        valueRange = valueRange,
    )
}

/** Barra de progreso con el riel del tema. Mismos parámetros que `LinearProgressIndicator` (el progreso como función). */
@Composable
fun Progreso(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    trackColor: Color = Color.Unspecified,
    strokeCap: StrokeCap = StrokeCap.Round,
) {
    val tema = Estilo.tema
    val e = MaterialTheme.colorScheme
    val activo = if (color.isSpecified) color else e.primary
    val inactivo = if (trackColor.isSpecified) trackColor else e.outline
    val v = progress().coerceIn(0f, 1f)
    // El tamaño propio va después del de la pantalla: si la pantalla fija ancho o alto, manda el suyo.
    Canvas(modifier.progressSemantics(v).size(240.dp, 4.dp)) { rielTema(tema, e, progress().coerceIn(0f, 1f), 0, activo, inactivo) }
}

/** Lo mismo con el progreso como número. */
@Composable
fun Progreso(progress: Float, modifier: Modifier = Modifier, color: Color = Color.Unspecified, trackColor: Color = Color.Unspecified) =
    Progreso({ progress }, modifier, color, trackColor)

/**
 * Interruptor suelto (sin texto) con la forma de los botones del tema y su perilla; encendido, el riel lleva el glifo de
 * «elegido» del tema. Mismos parámetros que `Switch` de Material; [thumbContent] y [colors] se aceptan y no se usan.
 * Con [onCheckedChange] nulo solo se dibuja (lo usa `Interruptor`, que hace tocable toda la fila).
 */
@Composable
fun Conmutador(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    thumbContent: (@Composable () -> Unit)? = null,
    colors: SwitchColors? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val tema = Estilo.tema
    val e = MaterialTheme.colorScheme
    val sentir = rememberSentir()
    val riel = rielInterruptor(tema)
    val forma = formaBotonTema(tema)
    val f by animateFloatAsState(if (checked) 1f else 0f, label = "conmutador")
    val medidor = rememberTextMeasurer()
    val glifo = glifoElegido(tema)
    val tinta = Tonos.sobre(riel)
    val capa = remember(glifo, tinta) { medidor.measure(glifo, androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = tinta)) }
    val tocable = if (onCheckedChange != null) Modifier.minimumInteractiveComponentSize().toggleable(
        value = checked, enabled = enabled, role = Role.Switch, interactionSource = interactionSource, indication = null,
    ) { sentir(Sentidos.Evento.ELEGIR); onCheckedChange(it) } else Modifier
    Box(modifier.then(tocable), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(52.dp, 32.dp)) {
            val alfa = if (enabled) 1f else 0.38f
            val o = forma.createOutline(size, layoutDirection, this)
            drawOutline(o, if (checked) riel.copy(alpha = 0.85f * alfa) else e.surfaceVariant.copy(alpha = alfa))
            drawOutline(o, (if (checked) riel else e.outline).copy(alpha = alfa), style = Stroke(1.5.dp.toPx()))
            if (f > 0.5f) drawText(capa, topLeft = Offset(size.height * 0.32f, (size.height - capa.size.height) / 2f), alpha = alfa)
            val cx = size.height / 2 + (size.width - size.height) * f
            perillaTema(tema, e, Offset(cx, size.height / 2), size.height * 0.36f,
                (if (checked) e.surface else e.outline).copy(alpha = alfa), if (checked) riel else e.surfaceVariant)
        }
    }
}

/**
 * El riel del tema, de la izquierda hasta la fracción [f] en [activo] y el resto en [inactivo]. Sirve para el deslizador,
 * la barra de progreso y la cuenta atrás de Deshacer; se ajusta a la altura (de 3 dp a 14 dp). Con [pasos] dibuja las marcas.
 */
internal fun DrawScope.rielTema(t: TemaId, e: ColorScheme, f: Float, pasos: Int, activo: Color, inactivo: Color) {
    val w = size.width; val cy = size.height / 2; val d = 1.dp.toPx()
    val g = minOf(size.height, 5 * d)
    val x = w * f.coerceIn(0f, 1f)
    fun linea(c: Color, desde: Float, hasta: Float, grosor: Float, y: Float = cy, cap: StrokeCap = StrokeCap.Butt) {
        if (hasta > desde) drawLine(c, Offset(desde, y), Offset(hasta, y), grosor, cap = cap)
    }
    when (t) {
        // Doble filete; lo recorrido, una barra llena.
        TemaId.ROMANO -> {
            linea(inactivo, x, w, g * 0.18f, cy - g * 0.3f); linea(inactivo, x, w, g * 0.18f, cy + g * 0.3f)
            if (x > 0f) drawRect(activo, Offset(0f, cy - g / 2), Size(x, g))
        }
        // La vía del tranvía: dos rieles por recorrer; lo recorrido, el riel lleno con sus durmientes claros.
        TemaId.URBE -> {
            linea(inactivo, x, w, g * 0.16f, cy - g * 0.32f); linea(inactivo, x, w, g * 0.16f, cy + g * 0.32f)
            if (x > 0f) {
                drawRect(activo, Offset(0f, cy - g * 0.42f), Size(x, g * 0.84f))
                var k = g * 0.7f; while (k < x) { drawLine(e.surface.copy(alpha = 0.45f), Offset(k, cy - g * 0.42f), Offset(k, cy + g * 0.42f), maxOf(g * 0.14f, 0.6f * d)); k += g * 1.6f }
            }
        }
        // Celdas de píxel: encendidas hasta donde va.
        TemaId.CIBER -> {
            val cw = maxOf(g * 1.3f, 2 * d); val paso = cw + maxOf(g * 0.5f, d)
            var cx = 0f
            while (cx < w) {
                val ancho = minOf(cw, w - cx)
                drawRect(if (cx + ancho / 2 <= x) activo else inactivo.copy(alpha = inactivo.alpha * 0.4f), Offset(cx, cy - g / 2), Size(ancho, g))
                cx += paso
            }
        }
        // El tubo: apagado de punta a punta y encendido, con su halo, hasta donde llega el valor.
        TemaId.NEON -> {
            linea(inactivo.copy(alpha = inactivo.alpha * 0.5f), 0f, w, g, cap = StrokeCap.Round)
            linea(activo.copy(alpha = activo.alpha * 0.3f), 0f, x, g * 3f, cap = StrokeCap.Round)
            linea(activo, 0f, x, g, cap = StrokeCap.Round)
        }
        // Greca en onda cuadrada sobre una línea fina.
        TemaId.HELENO -> {
            linea(inactivo, x, w, g * 0.3f)
            if (x > 0f) {
                val a = g * 0.4f; val per = maxOf(g * 2.4f, 4 * d)
                val p = Path(); var px = 0f; var arriba = true
                p.moveTo(0f, cy + a)
                while (px < x) {
                    val sig = minOf(px + per / 2, x)
                    if (arriba) { p.lineTo(px, cy - a); p.lineTo(sig, cy - a) } else { p.lineTo(px, cy + a); p.lineTo(sig, cy + a) }
                    px = sig; arriba = !arriba
                }
                drawPath(p, activo, style = Stroke(maxOf(g * 0.28f, d)))
            }
        }
        // Lo recorrido es el signo del agua: una línea en zigzag de puntas agudas, como el Nilo escrito.
        TemaId.EGIPTO -> {
            linea(inactivo, x, w, g * 0.3f)
            if (x > 0f) {
                val a = g * 0.42f; val per = maxOf(g * 1.6f, 3 * d)
                val p = Path(); var px = 0f; var arriba = true
                p.moveTo(0f, cy)
                while (px < x) {
                    val sig = minOf(px + per / 2, x)
                    p.lineTo(sig, if (arriba) cy - a else cy + a)
                    px = sig; arriba = !arriba
                }
                drawPath(p, activo, style = Stroke(maxOf(g * 0.26f, d), join = StrokeJoin.Miter))
            }
        }
        // Puntos de rúbrica sin recorrer; tinta corrida y un punto de oro al final.
        TemaId.MEDIEVAL -> {
            var px = x + g
            while (px < w) { drawCircle(inactivo, maxOf(g * 0.18f, 0.6f * d), Offset(px, cy)); px += maxOf(g * 1.1f, 2.5f * d) }
            linea(activo, 0f, x, g * 0.6f, cap = StrokeCap.Round)
            if (x > 0f) drawCircle(e.tertiary, g * 0.45f, Offset(x, cy))
        }
        // Lo que falta, una fila de anillas de malla; lo recorrido, la banda roja de la sobreveste.
        TemaId.TEMPLARIO -> {
            var px = x + g * 0.4f
            while (px < w) { drawCircle(inactivo, g * 0.32f, Offset(px, cy), style = Stroke(maxOf(g * 0.1f, 0.5f * d))); px += g * 0.5f }
            if (x > 0f) drawRect(activo, Offset(0f, cy - g * 0.35f), Size(x, g * 0.7f))
        }
        // Casilla de formulario: contorno duro, relleno y rayas cada décimo.
        TemaId.DISTOPIA -> {
            if (x > 0f) drawRect(activo, Offset(0f, cy - g / 2), Size(x, g))
            drawRect(inactivo, Offset(0f, cy - g / 2), Size(w, g), style = Stroke(maxOf(g * 0.15f, 0.8f * d)))
            for (k in 1 until 10) drawLine(e.onSurface.copy(alpha = 0.5f), Offset(w * k / 10f, cy - g / 2), Offset(w * k / 10f, cy + g / 2), maxOf(g * 0.12f, 0.6f * d))
        }
        // Tubo de indicador con luces encendidas por dentro.
        TemaId.REFUGIO -> {
            drawRoundRect(inactivo, Offset(0f, cy - g / 2), Size(w, g), CornerRadius(g / 2), style = Stroke(maxOf(g * 0.15f, 0.8f * d)))
            if (x > g * 0.4f) {
                drawRoundRect(activo, Offset(g * 0.2f, cy - g * 0.3f), Size(x - g * 0.4f, g * 0.6f), CornerRadius(g * 0.3f))
                var px = g; while (px < x - g * 0.5f) { drawCircle(e.surface.copy(alpha = 0.4f), g * 0.12f, Offset(px, cy)); px += g * 2 }
            }
        }
        // Riel de latón; lo recorrido, barra de acero carmesí; remaches a cada cuarto.
        TemaId.ORDEN -> {
            linea(e.secondary.copy(alpha = inactivo.alpha), x, w, g * 0.4f)
            if (x > 0f) drawRect(activo, Offset(0f, cy - g / 2), Size(x, g))
            for (k in 1..3) drawCircle(e.secondary, g * 0.35f, Offset(w * k / 4f, cy))
        }
        // La vía: lo que falta son dos carriles sobre sus traviesas; lo recorrido, la barra de latón de la biela.
        TemaId.VAPOR -> {
            var px = x + g * 0.4f
            while (px < w) { drawLine(inactivo, Offset(px, cy - g * 0.6f), Offset(px, cy + g * 0.6f), maxOf(g * 0.18f, 0.6f * d)); px += g * 1.2f }
            linea(inactivo, x, w, maxOf(g * 0.12f, 0.5f * d), cy - g * 0.32f); linea(inactivo, x, w, maxOf(g * 0.12f, 0.5f * d), cy + g * 0.32f)
            if (x > 0f) { drawRect(activo, Offset(0f, cy - g * 0.35f), Size(x, g * 0.7f)); drawCircle(e.tertiary, g * 0.3f, Offset(x, cy)) }
        }
        // Pipeta: línea base graduada y el líquido hasta donde va.
        TemaId.LABORATORIO -> {
            val base = cy + g * 0.35f
            linea(inactivo, 0f, w, maxOf(g * 0.2f, 0.6f * d), base)
            for (k in 0..10) drawLine(inactivo, Offset(w * k / 10f, base), Offset(w * k / 10f, base - (if (k % 5 == 0) g else g * 0.5f)), maxOf(g * 0.15f, 0.6f * d))
            linea(activo, 0f, x, g * 0.7f, cy - g * 0.1f, StrokeCap.Round)
        }
        // Cerca: sin recorrer, línea de puntadas; recorrida, tablas con sus junturas.
        TemaId.COLONOS -> {
            if (x < w) drawLine(inactivo, Offset(x, cy), Offset(w, cy), g * 0.35f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(maxOf(g * 1.2f, 2 * d), maxOf(g * 0.8f, 1.5f * d))))
            if (x > 0f) {
                drawRect(activo, Offset(0f, cy - g / 2), Size(x, g))
                var px = g * 1.6f; while (px < x) { drawLine(e.surface.copy(alpha = 0.55f), Offset(px, cy - g / 2), Offset(px, cy + g / 2), maxOf(g * 0.25f, 0.6f * d)); px += g * 1.6f }
            }
        }
        // Cinta con orla de oro arriba y abajo.
        TemaId.NAPOLEON -> {
            linea(inactivo, x, w, g * 0.3f)
            if (x > 0f) {
                drawRect(activo, Offset(0f, cy - g * 0.4f), Size(x, g * 0.8f))
                linea(e.tertiary, 0f, x, maxOf(g * 0.15f, 0.6f * d), cy - g * 0.45f); linea(e.tertiary, 0f, x, maxOf(g * 0.15f, 0.6f * d), cy + g * 0.45f)
            }
        }
        // Un tallo con hojas alternas.
        TemaId.BOTANICO -> {
            linea(inactivo, x, w, g * 0.25f)
            linea(activo, 0f, x, g * 0.35f)
            var px = g * 1.2f; var arriba = true
            while (px < x - g * 0.6f) {
                val y = if (arriba) cy - g * 0.35f else cy + g * 0.35f
                drawOval(activo.copy(alpha = activo.alpha * 0.85f), Offset(px - g * 0.6f, y - g * 0.27f), Size(g * 1.2f, g * 0.55f))
                px += g * 2.4f; arriba = !arriba
            }
        }
        // El riel es una calle: lo recorrido lleva la línea discontinua pintada; lo demás es asfalto con alguna grieta.
        TemaId.RUINAS -> {
            linea(inactivo, x, w, g * 0.3f)
            linea(activo.copy(alpha = activo.alpha * 0.35f), 0f, x, g * 0.45f)
            var px = g * 0.4f
            while (px < x - g * 0.2f) { drawLine(activo, Offset(px, cy), Offset(minOf(px + g * 1.2f, x), cy), g * 0.22f); px += g * 2f }
            var qx = x + g * 3f
            while (qx < w - g) { drawLine(inactivo, Offset(qx, cy - g * 0.5f), Offset(qx + g * 0.3f, cy + g * 0.5f), g * 0.08f); qx += g * 6f }
        }
        // Bastón de caramelo: franjas al sesgo.
        TemaId.NAVIDAD -> {
            linea(inactivo, x, w, g * 0.3f)
            if (x > 0f) {
                drawRect(activo, Offset(0f, cy - g / 2), Size(x, g))
                clipRect(0f, cy - g / 2, x, cy + g / 2) {
                    var px = -g
                    while (px < x + g) { drawLine(Color.White.copy(alpha = 0.75f), Offset(px, cy + g / 2), Offset(px + g, cy - g / 2), g * 0.45f); px += g * 1.5f }
                }
            }
        }
        // Hilo de seda con nudos.
        TemaId.SEDA -> {
            linea(inactivo, x, w, g * 0.25f)
            linea(activo, 0f, x, g * 0.4f)
            var px = g * 1.5f; while (px < x) { drawCircle(activo, g * 0.4f, Offset(px, cy)); px += g * 3 }
        }
        // Barra con rombos de oro, como una vidriera emplomada.
        TemaId.GOTICO -> {
            linea(inactivo, x, w, g * 0.3f)
            if (x > 0f) {
                drawRect(activo, Offset(0f, cy - g * 0.4f), Size(x, g * 0.8f))
                var px = g * 1.25f; while (px < x - g * 0.3f) { romboCtl(Offset(px, cy), g * 0.28f, g * 0.38f, e.tertiary); px += g * 2.5f }
            }
        }
        // Una regla de cinta métrica a tinta; lo recorrido, tinta corrida que termina en una gota.
        TemaId.ARCANO -> {
            linea(inactivo, 0f, w, maxOf(g * 0.14f, 0.6f * d))
            var px = 0f; var k = 0
            while (px <= w) { drawLine(inactivo, Offset(px, cy), Offset(px, cy - g * (if (k % 5 == 0) 0.9f else 0.45f)), maxOf(g * 0.1f, 0.5f * d)); px += maxOf(g * 0.9f, 3 * d); k++ }
            if (x > 0f) {
                linea(activo, 0f, x, g * 0.5f, cap = StrokeCap.Round)
                drawCircle(activo, g * 0.42f, Offset(x, cy + g * 0.12f))
                drawCircle(activo.copy(alpha = activo.alpha * 0.5f), g * 0.18f, Offset(x + g * 0.6f, cy + g * 0.5f))
            }
        }
        // Órbita de puntos; lo recorrido, estela con brillo y una estrella en la punta.
        TemaId.ESPACIO -> {
            var px = x + g; while (px < w) { drawCircle(inactivo, maxOf(g * 0.15f, 0.5f * d), Offset(px, cy)); px += maxOf(g * 0.9f, 2 * d) }
            linea(activo.copy(alpha = activo.alpha * 0.3f), 0f, x, g, cap = StrokeCap.Round)
            linea(activo, 0f, x, g * 0.35f, cap = StrokeCap.Round)
            if (x > 0f) estrellaCtl(Offset(x, cy), g * 0.7f, activo, puntas = 4, dentro = 0.35f)
        }
        // El cabo de sondeo: lo que falta, una raya fina con sus marcas; lo hecho, agua que ondula hasta la plomada.
        TemaId.ABISMO -> {
            linea(inactivo, x, w, g * 0.25f)
            var px = 0f; var k = 0
            while (px < w) { if (px > x) drawLine(inactivo, Offset(px, cy - g * (if (k % 5 == 0) 0.6f else 0.3f)), Offset(px, cy + g * (if (k % 5 == 0) 0.6f else 0.3f)), maxOf(g * 0.12f, 0.6f * d)); px += maxOf(g * 1.4f, 4 * d); k++ }
            if (x > 0f) {
                val p = Path(); var q = 0f
                p.moveTo(0f, cy)
                while (q <= x) { p.lineTo(q, cy + g * 0.3f * sin(q / (g * 0.9f))); q += maxOf(g * 0.25f, 0.8f * d) }
                drawPath(p, activo, style = Stroke(g * 0.4f, cap = StrokeCap.Round))
                drawCircle(activo, g * 0.55f, Offset(x, cy))
            }
        }
        // Azulejos alternos.
        TemaId.ANDALUS -> {
            linea(inactivo, x, w, g * 0.3f)
            var px = 0f; var k = 0
            while (px < x) { drawRect(if (k % 2 == 0) activo else e.tertiary, Offset(px, cy - g * 0.4f), Size(minOf(g * 1.6f, x - px), g * 0.8f)); px += g * 1.6f; k++ }
        }
        // Montes en zigzag hasta donde va; luego, la llanura.
        TemaId.MONGOL -> {
            linea(inactivo, x, w, g * 0.3f)
            if (x > 0f) {
                val a = g * 0.4f; val per = maxOf(g * 1.6f, 3 * d)
                val p = Path(); var px = 0f; var arriba = true
                p.moveTo(0f, cy + a)
                while (px < x) { px = minOf(px + per / 2, x); p.lineTo(px, if (arriba) cy - a else cy + a); arriba = !arriba }
                drawPath(p, activo, style = Stroke(maxOf(g * 0.35f, d)))
            }
        }
        // Dos trazos de sanguina, a mano alzada.
        TemaId.RENACIMIENTO -> {
            linea(inactivo, x, w, g * 0.25f)
            if (x > 0f) {
                drawLine(activo.copy(alpha = activo.alpha * 0.85f), Offset(0f, cy - g * 0.1f), Offset(x, cy - g * 0.25f), g * 0.4f)
                drawLine(activo.copy(alpha = activo.alpha * 0.5f), Offset(g * 0.5f, cy + g * 0.3f), Offset(x * 0.96f, cy + g * 0.2f), g * 0.28f)
            }
        }
        // Lo que falta, un cabo fino; lo recorrido, un cabo grueso con sus vueltas.
        TemaId.PIRATA -> {
            linea(inactivo, x, w, g * 0.22f)
            if (x > 0f) {
                linea(activo, 0f, x, g * 0.6f, cap = StrokeCap.Round)
                var k = g * 0.3f
                while (k < x - g * 0.3f) { drawLine(inactivo.copy(alpha = 0.6f), Offset(k, cy + g * 0.28f), Offset(k + g * 0.36f, cy - g * 0.28f), g * 0.12f); k += g * 0.55f }
            }
        }
        // Barra negra; lo recorrido, rojo con el corte en diagonal.
        TemaId.URSS -> {
            if (x < w) drawRect(e.onSurface.copy(alpha = 0.75f * inactivo.alpha), Offset(x, cy - g * 0.4f), Size(w - x, g * 0.8f))
            if (x > 0f) drawPath(Path().apply { moveTo(0f, cy - g / 2); lineTo(x + g * 0.4f, cy - g / 2); lineTo(x - g * 0.4f, cy + g / 2); lineTo(0f, cy + g / 2); close() }, activo)
        }
        // Lo recorrido, en tres tercios: azul, blanco y rojo.
        TemaId.REVOLUCION -> {
            linea(inactivo, x, w, g * 0.3f)
            listOf(e.secondary, e.outline, activo).forEachIndexed { k, c -> if (x > 0f) drawRect(c, Offset(x * k / 3f, cy - g * 0.4f), Size(x / 3f, g * 0.8f)) }
        }
        // Surco tallado en la piedra; lo recorrido, con una gema en la punta.
        TemaId.ANTIGUAS -> {
            linea(inactivo, x, w, g * 0.25f, cy - g * 0.2f)
            linea(e.surface.copy(alpha = 0.5f), x, w, g * 0.25f, cy + g * 0.2f)
            linea(activo, 0f, x, g * 0.7f)
            if (x > 0f) romboCtl(Offset(x, cy), g * 0.45f, g * 0.65f, e.tertiary)
        }
    }
    if (pasos in 1..40) for (k in 1..pasos) {
        val fx = k / (pasos + 1f)
        drawCircle(if (fx <= f) e.surface.copy(alpha = 0.7f) else inactivo, maxOf(g * 0.18f, 0.8f * d), Offset(w * fx, cy))
    }
}

/**
 * La perilla del tema, centrada en [c] con radio [r], en [color]; [hueco] es el color de los detalles calados
 * (el fondo sobre el que va). Es también la marca de la ficha elegida y de la barra de Deshacer.
 */
internal fun DrawScope.perillaTema(t: TemaId, e: ColorScheme, c: Offset, r: Float, color: Color, hueco: Color) {
    when (t) {
        // Tablilla con asas de cola de milano.
        TemaId.ROMANO -> {
            val a = r * 0.75f; val b = r * 0.55f
            drawRect(color, Offset(c.x - a, c.y - b), Size(2 * a, 2 * b))
            listOf(1f, -1f).forEach { s ->
                val x0 = c.x + s * a
                drawPath(Path().apply { moveTo(x0, c.y - b * 0.8f); lineTo(x0 + s * r * 0.3f, c.y); lineTo(x0, c.y + b * 0.8f); close() }, color)
            }
            drawRect(hueco.copy(alpha = 0.8f), Offset(c.x - a + r * 0.15f, c.y - b + r * 0.15f), Size(2 * a - r * 0.3f, 2 * b - r * 0.3f), style = Stroke(r * 0.07f))
        }
        // La tapa de alcantarilla de la calle, redonda, con su aro y la reja de barras.
        TemaId.URBE -> {
            drawCircle(color, r * 0.92f, c)
            drawCircle(hueco.copy(alpha = 0.8f), r * 0.74f, c, style = Stroke(r * 0.08f))
            listOf(-0.36f, 0f, 0.36f).forEach { f -> drawLine(hueco.copy(alpha = 0.75f), Offset(c.x - r * 0.62f * (1f - f * f * 1.2f), c.y + f * r), Offset(c.x + r * 0.62f * (1f - f * f * 1.2f), c.y + f * r), r * 0.08f) }
        }
        // Cursor de bloque con su subrayado.
        TemaId.CIBER -> {
            drawRect(color, Offset(c.x - r * 0.45f, c.y - r * 0.8f), Size(r * 0.9f, r * 1.4f))
            drawRect(e.secondary, Offset(c.x - r * 0.55f, c.y + r * 0.75f), Size(r * 1.1f, r * 0.18f))
        }
        // Un foco de neón: el halo y la bombilla encendida.
        TemaId.NEON -> {
            drawCircle(color.copy(alpha = 0.3f), r * 1.15f, c)
            drawCircle(color, r * 0.8f, c)
            drawCircle(Color.White.copy(alpha = 0.6f), r * 0.3f, c + Offset(-r * 0.2f, -r * 0.2f))
        }
        // Sol de Vergina: dieciséis rayos de dos largos.
        TemaId.HELENO -> {
            for (k in 0 until 16) {
                val ang = k * PI / 8; val l = if (k % 2 == 0) r else r * 0.75f
                drawLine(color, Offset(c.x + r * 0.45f * cos(ang).toFloat(), c.y + r * 0.45f * sin(ang).toFloat()),
                    Offset(c.x + l * cos(ang).toFloat(), c.y + l * sin(ang).toFloat()), r * 0.12f)
            }
            drawCircle(color, r * 0.42f, c)
            drawCircle(hueco, r * 0.15f, c)
        }
        // El anillo shen (lo que no tiene fin): un aro de cuerda atado con su barra abajo.
        TemaId.EGIPTO -> {
            drawCircle(color, r * 0.72f, Offset(c.x, c.y - r * 0.1f), style = Stroke(r * 0.3f))
            drawRect(color, Offset(c.x - r * 0.75f, c.y + r * 0.6f), Size(r * 1.5f, r * 0.28f))
            drawCircle(hueco, r * 0.22f, Offset(c.x, c.y - r * 0.1f))
        }
        // Sello de lacre con el borde ondulado.
        TemaId.MEDIEVAL -> {
            for (k in 0 until 10) { val ang = k * PI / 5; drawCircle(color, r * 0.22f, Offset(c.x + r * 0.78f * cos(ang).toFloat(), c.y + r * 0.78f * sin(ang).toFloat())) }
            drawCircle(color, r * 0.8f, c)
            drawCircle(hueco.copy(alpha = 0.5f), r * 0.5f, c, style = Stroke(r * 0.08f))
        }
        // Un escudo de lágrima con la cruz patada vaciada.
        TemaId.TEMPLARIO -> {
            val escudo = Path().apply {
                moveTo(c.x - r * 0.8f, c.y - r * 0.85f); lineTo(c.x + r * 0.8f, c.y - r * 0.85f)
                quadraticTo(c.x + r * 0.86f, c.y + r * 0.3f, c.x, c.y + r); quadraticTo(c.x - r * 0.86f, c.y + r * 0.3f, c.x - r * 0.8f, c.y - r * 0.85f); close()
            }
            drawPath(escudo, color)
            drawPath(cruzPatada(c + Offset(0f, -r * 0.1f), r * 0.55f), hueco.copy(alpha = 0.75f))
        }
        // Aguja de medidor.
        TemaId.DISTOPIA -> {
            drawPath(Path().apply { moveTo(c.x - r * 0.7f, c.y - r * 0.9f); lineTo(c.x + r * 0.7f, c.y - r * 0.9f); lineTo(c.x, c.y + r * 0.1f); close() }, color)
            drawLine(color, c, Offset(c.x, c.y + r * 0.95f), r * 0.16f)
        }
        // Perilla de tablero con un destello.
        TemaId.REFUGIO -> {
            drawCircle(color, r * 0.85f, c)
            drawCircle(hueco.copy(alpha = 0.6f), r * 0.85f, c, style = Stroke(r * 0.1f))
            estrellaCtl(c, r * 0.55f, hueco, puntas = 4, dentro = 0.3f)
        }
        // Escudo de caballero con su asta de latón.
        TemaId.ORDEN -> {
            val p = Path().apply {
                moveTo(c.x - r * 0.75f, c.y - r * 0.85f); lineTo(c.x + r * 0.75f, c.y - r * 0.85f); lineTo(c.x + r * 0.75f, c.y - r * 0.1f)
                quadraticTo(c.x + r * 0.7f, c.y + r * 0.6f, c.x, c.y + r * 0.95f); quadraticTo(c.x - r * 0.7f, c.y + r * 0.6f, c.x - r * 0.75f, c.y - r * 0.1f); close()
            }
            drawPath(p, color)
            drawPath(p, e.secondary, style = Stroke(r * 0.1f))
            drawLine(e.secondary, Offset(c.x, c.y - r * 0.7f), Offset(c.x, c.y + r * 0.6f), r * 0.1f)
        }
        // La esfera de un manómetro de latón, con sus marcas y la aguja.
        TemaId.VAPOR -> {
            drawCircle(color, r * 0.9f, c); drawCircle(hueco, r * 0.66f, c)
            for (k in 0..6) { val a = Math.toRadians(135.0 + 45.0 * k); drawLine(color, Vapor.en(c, r * 0.6f, a), Vapor.en(c, r * 0.44f, a), r * 0.08f) }
            drawLine(e.tertiary, c, Vapor.en(c, r * 0.5f, Math.toRadians(300.0)), r * 0.1f, StrokeCap.Round)
            drawCircle(color, r * 0.12f, c)
        }
        // Gota.
        TemaId.LABORATORIO -> {
            drawCircle(color, r * 0.62f, Offset(c.x, c.y + r * 0.25f))
            drawPath(Path().apply { moveTo(c.x, c.y - r); lineTo(c.x + r * 0.56f, c.y + r * 0.02f); lineTo(c.x - r * 0.56f, c.y + r * 0.02f); close() }, color)
            drawCircle(hueco.copy(alpha = 0.5f), r * 0.14f, Offset(c.x - r * 0.22f, c.y + r * 0.1f))
        }
        // Casa de colono con su puerta.
        TemaId.COLONOS -> {
            drawPath(Path().apply {
                moveTo(c.x - r * 0.7f, c.y + r * 0.85f); lineTo(c.x - r * 0.7f, c.y - r * 0.1f); lineTo(c.x, c.y - r * 0.85f)
                lineTo(c.x + r * 0.7f, c.y - r * 0.1f); lineTo(c.x + r * 0.7f, c.y + r * 0.85f); close()
            }, color)
            drawRect(hueco.copy(alpha = 0.7f), Offset(c.x - r * 0.15f, c.y + r * 0.3f), Size(r * 0.3f, r * 0.55f))
        }
        // Estrella de oro con contorno.
        TemaId.NAPOLEON -> {
            estrellaCtl(c, r, e.tertiary)
            estrellaCtl(c, r, color, estilo = Stroke(r * 0.1f))
        }
        // Hoja con su nervio.
        TemaId.BOTANICO -> rotate(-35f, c) {
            drawOval(color, Offset(c.x - r, c.y - r * 0.45f), Size(2 * r, r * 0.9f))
            drawLine(hueco.copy(alpha = 0.6f), Offset(c.x - r * 0.85f, c.y), Offset(c.x + r * 0.85f, c.y), r * 0.08f)
        }
        // Una tapa de alcantarilla: el disco de hierro con su cuadrícula y el borde gastado.
        TemaId.RUINAS -> {
            drawCircle(color, r, c)
            drawCircle(hueco.copy(alpha = 0.5f), r * 0.86f, c, style = Stroke(r * 0.08f))
            for (k in -1..1) {
                drawLine(hueco.copy(alpha = 0.45f), Offset(c.x + k * r * 0.35f, c.y - r * 0.6f), Offset(c.x + k * r * 0.35f, c.y + r * 0.6f), r * 0.07f)
                drawLine(hueco.copy(alpha = 0.45f), Offset(c.x - r * 0.6f, c.y + k * r * 0.35f), Offset(c.x + r * 0.6f, c.y + k * r * 0.35f), r * 0.07f)
            }
        }
        // Bola del árbol con su capuchón y su brillo.
        TemaId.NAVIDAD -> {
            drawCircle(color, r * 0.75f, Offset(c.x, c.y + r * 0.15f))
            drawRect(e.tertiary, Offset(c.x - r * 0.22f, c.y - r * 0.82f), Size(r * 0.44f, r * 0.25f))
            drawCircle(e.tertiary, r * 0.14f, Offset(c.x, c.y - r * 0.95f), style = Stroke(r * 0.07f))
            drawCircle(Color.White.copy(alpha = 0.6f), r * 0.16f, Offset(c.x - r * 0.28f, c.y - r * 0.1f))
        }
        // Moneda con agujero cuadrado.
        TemaId.SEDA -> {
            drawCircle(color, r * 0.85f, c)
            drawCircle(e.tertiary, r * 0.85f, c, style = Stroke(r * 0.1f))
            drawRect(hueco, Offset(c.x - r * 0.22f, c.y - r * 0.22f), Size(r * 0.44f, r * 0.44f))
        }
        // Ventana lanceta con su parteluz.
        TemaId.GOTICO -> {
            val p = Path().apply {
                moveTo(c.x - r * 0.55f, c.y + r * 0.9f); lineTo(c.x - r * 0.55f, c.y - r * 0.1f); quadraticTo(c.x - r * 0.55f, c.y - r * 0.7f, c.x, c.y - r)
                quadraticTo(c.x + r * 0.55f, c.y - r * 0.7f, c.x + r * 0.55f, c.y - r * 0.1f); lineTo(c.x + r * 0.55f, c.y + r * 0.9f); close()
            }
            drawPath(p, color)
            drawPath(p, e.tertiary, style = Stroke(r * 0.09f))
            drawLine(e.tertiary, Offset(c.x, c.y - r * 0.6f), Offset(c.x, c.y + r * 0.9f), r * 0.07f)
        }
        // Un sello de lacre con la orilla derramada y el ojo de pupila vertical.
        TemaId.ARCANO -> {
            val p = Path()
            for (k in 0..12) {
                val a = -PI / 2 + k * 2 * PI / 12; val rr = r * (if (k % 2 == 0) 0.95f else 0.82f)
                val q = Offset(c.x + (rr * cos(a)).toFloat(), c.y + (rr * sin(a)).toFloat())
                if (k == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y)
            }
            p.close()
            drawPath(p, color)
            drawCircle(hueco.copy(alpha = 0.5f), r * 0.6f, c, style = Stroke(r * 0.06f))
            drawPath(Path().apply { moveTo(c.x - r * 0.42f, c.y); quadraticTo(c.x, c.y - r * 0.42f, c.x + r * 0.42f, c.y); quadraticTo(c.x, c.y + r * 0.42f, c.x - r * 0.42f, c.y); close() }, hueco.copy(alpha = 0.75f), style = Stroke(r * 0.07f))
            drawOval(hueco.copy(alpha = 0.8f), Offset(c.x - r * 0.06f, c.y - r * 0.18f), Size(r * 0.12f, r * 0.36f))
        }
        // Planeta con anillo.
        TemaId.ESPACIO -> {
            drawCircle(color, r * 0.55f, c)
            rotate(-20f, c) { drawOval(color, Offset(c.x - r, c.y - r * 0.3f), Size(2 * r, r * 0.6f), style = Stroke(r * 0.12f)) }
        }
        // Un ojo de buey: la brida con cuatro pernos y el cristal hundido.
        TemaId.ABISMO -> {
            drawCircle(color, r * 0.92f, c)
            drawCircle(hueco, r * 0.52f, c)
            drawCircle(color.copy(alpha = color.alpha * 0.35f), r * 0.52f, c)
            for (k in 0 until 4) {
                val a = PI / 4 + k * PI / 2
                drawCircle(hueco, r * 0.1f, c + Offset((r * 0.72f * cos(a)).toFloat(), (r * 0.72f * sin(a)).toFloat()))
            }
            drawArc(Color.White.copy(alpha = 0.5f), 200f, 70f, false, Offset(c.x - r * 0.38f, c.y - r * 0.38f), Size(r * 0.76f, r * 0.76f), style = Stroke(r * 0.1f))
        }
        // Estrella de ocho puntas: dos cuadrados cruzados.
        TemaId.ANDALUS -> {
            drawRect(color, Offset(c.x - r * 0.62f, c.y - r * 0.62f), Size(r * 1.24f, r * 1.24f))
            rotate(45f, c) { drawRect(color, Offset(c.x - r * 0.62f, c.y - r * 0.62f), Size(r * 1.24f, r * 1.24f)) }
            drawCircle(e.tertiary, r * 0.25f, c)
        }
        // Punta de flecha de arquero.
        TemaId.MONGOL -> {
            drawPath(Path().apply { moveTo(c.x, c.y - r); lineTo(c.x + r * 0.7f, c.y + r * 0.5f); lineTo(c.x, c.y + r * 0.15f); lineTo(c.x - r * 0.7f, c.y + r * 0.5f); close() }, color)
            drawLine(color, Offset(c.x, c.y + r * 0.15f), Offset(c.x, c.y + r), r * 0.14f)
        }
        // Compás de dibujo.
        TemaId.RENACIMIENTO -> {
            val cabeza = Offset(c.x, c.y - r * 0.75f)
            drawLine(color, cabeza, Offset(c.x - r * 0.55f, c.y + r * 0.9f), r * 0.13f, cap = StrokeCap.Round)
            drawLine(color, cabeza, Offset(c.x + r * 0.55f, c.y + r * 0.9f), r * 0.13f, cap = StrokeCap.Round)
            drawLine(color.copy(alpha = color.alpha * 0.7f), Offset(c.x - r * 0.3f, c.y + r * 0.3f), Offset(c.x + r * 0.3f, c.y + r * 0.3f), r * 0.07f)
            drawCircle(color, r * 0.2f, cabeza)
        }
        // La rueda del timón: el aro, el cubo y las ocho cabillas que sobresalen.
        TemaId.PIRATA -> {
            for (k in 0 until 8) {
                val a = k * Math.PI / 4
                val fin = Offset(c.x + (r * 1.05f * kotlin.math.cos(a)).toFloat(), c.y + (r * 1.05f * kotlin.math.sin(a)).toFloat())
                drawLine(color, c, fin, r * 0.14f, cap = StrokeCap.Round)
            }
            drawCircle(color, r * 0.72f, c, style = Stroke(r * 0.18f))
            drawCircle(hueco, r * 0.24f, c); drawCircle(color, r * 0.13f, c)
        }
        // Cuña roja de cartel con su barra negra.
        TemaId.URSS -> {
            drawPath(Path().apply { moveTo(c.x - r * 0.75f, c.y - r * 0.85f); lineTo(c.x + r * 0.9f, c.y); lineTo(c.x - r * 0.75f, c.y + r * 0.85f); close() }, color)
            drawRect(e.onSurface, Offset(c.x - r * 0.95f, c.y - r * 0.85f), Size(r * 0.15f, r * 1.7f))
        }
        // Escarapela.
        TemaId.REVOLUCION -> {
            drawCircle(e.secondary, r * 0.9f, c)
            drawCircle(Color.White, r * 0.6f, c)
            drawCircle(color, r * 0.32f, c)
        }
        // Gema tallada.
        TemaId.ANTIGUAS -> {
            romboCtl(c, r * 0.65f, r * 0.95f, color)
            drawLine(hueco.copy(alpha = 0.5f), Offset(c.x - r * 0.65f, c.y), Offset(c.x + r * 0.65f, c.y), r * 0.06f)
            drawLine(hueco.copy(alpha = 0.5f), Offset(c.x, c.y - r * 0.95f), Offset(c.x, c.y + r * 0.95f), r * 0.06f)
        }
    }
}

// ---------------------------------------------------------------- menú

/**
 * Menú desplegable con la forma y el filete del tema. Mismos parámetros que `DropdownMenu` de Material
 * ([containerColor] sin indicar = la superficie del tema).
 */
@Composable
fun Menu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    properties: PopupProperties = PopupProperties(focusable = true),
    shape: Shape? = null,
    containerColor: Color = Color.Unspecified,
    content: @Composable ColumnScope.() -> Unit,
) {
    val tema = Estilo.tema
    val e = MaterialTheme.colorScheme
    val piel = pielDialogo(tema)
    DropdownMenu(
        expanded = expanded, onDismissRequest = onDismissRequest, modifier = modifier, offset = offset, scrollState = rememberScrollState(),
        properties = properties, shape = shape ?: piel.forma, containerColor = if (containerColor.isSpecified) containerColor else e.surface,
        tonalElevation = 0.dp, shadowElevation = 6.dp, border = BorderStroke(piel.borde, e.outline),
    ) {
        CabeceraMenu(tema, e, piel)
        content()
    }
}

/** El menú tal como se ve abierto, sin la ventana emergente (para las capturas). */
@Composable
fun MenuVista(modifier: Modifier = Modifier, containerColor: Color = Color.Unspecified, content: @Composable ColumnScope.() -> Unit) {
    val tema = Estilo.tema
    val e = MaterialTheme.colorScheme
    val piel = pielDialogo(tema)
    Surface(modifier, shape = piel.forma, color = if (containerColor.isSpecified) containerColor else e.surface, shadowElevation = 6.dp, border = BorderStroke(piel.borde, e.outline)) {
        Column(Modifier.padding(vertical = 8.dp).width(androidx.compose.foundation.layout.IntrinsicSize.Max)) {
            CabeceraMenu(tema, e, piel)
            content()
        }
    }
}

/** Arriba del menú: la holgura que pide la forma (frontón, arco, cúpula) y el filete del tema. */
@Composable
private fun CabeceraMenu(tema: TemaId, e: ColorScheme, piel: PielDialogo) {
    Spacer(Modifier.height(piel.arriba))
    Canvas(Modifier.fillMaxWidth().padding(horizontal = 12.dp).height(10.dp)) { fileteDialogo(tema, e) }
}

/** Opción de menú con la letra del tema y su marca delante. Mismos parámetros que `DropdownMenuItem`. */
@Composable
fun OpcionMenu(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    interactionSource: MutableInteractionSource? = null,
) {
    val tema = Estilo.tema
    val e = MaterialTheme.colorScheme
    val marca: @Composable () -> Unit = { Text(glifoFlecha(tema), style = MaterialTheme.typography.labelLarge, color = e.primary) }
    DropdownMenuItem(
        text = { ProvideTextStyle(MaterialTheme.typography.bodyLarge) { text() } },
        onClick = onClick, modifier = modifier,
        leadingIcon = leadingIcon ?: marca,
        trailingIcon = trailingIcon, enabled = enabled, contentPadding = contentPadding, interactionSource = interactionSource,
    )
}

// ---------------------------------------------------------------- selectores de fecha y hora

/** Diálogo para elegir un día, con el cuerpo del tema. [minimo] y [maximo] limitan los días que se pueden tocar. */
@Composable
fun DialogoFecha(inicial: LocalDate, minimo: LocalDate? = null, maximo: LocalDate? = null, titulo: String = "¿Qué día?",
                 onCerrar: () -> Unit, onElegir: (LocalDate) -> Unit) {
    Dialog(onDismissRequest = onCerrar, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        CuerpoSelectorFecha(inicial, titulo, minimo, maximo, Modifier.padding(horizontal = 12.dp).widthIn(max = 440.dp), onCerrar, onElegir)
    }
}

/** El cuerpo del selector de día, sin la ventana (para las capturas). */
@Composable
fun CuerpoSelectorFecha(inicial: LocalDate, titulo: String = "¿Qué día?", minimo: LocalDate? = null, maximo: LocalDate? = null,
                        modifier: Modifier = Modifier, onCerrar: () -> Unit = {}, onElegir: (LocalDate) -> Unit = {}) {
    val e = MaterialTheme.colorScheme
    // El calendario de Material cuenta en milisegundos a medianoche UTC.
    val limites = remember(minimo, maximo) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val dia = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC).toLocalDate()
                return (minimo == null || !dia.isBefore(minimo)) && (maximo == null || !dia.isAfter(maximo))
            }
            override fun isSelectableYear(year: Int): Boolean = (minimo == null || year >= minimo.year) && (maximo == null || year <= maximo.year)
        }
    }
    val estado = rememberDatePickerState(initialSelectedDateMillis = inicial.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(), selectableDates = limites)
    CuerpoDialogo(
        confirmButton = {
            TextButton(onClick = { estado.selectedDateMillis?.let { onElegir(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()) } },
                enabled = estado.selectedDateMillis != null) { Text("aceptar") }
        },
        modifier = modifier,
        dismissButton = { TextButton(onClick = onCerrar) { Text("cancelar") } },
        title = { Text(titulo) },
        text = {
            DatePicker(state = estado, title = null, showModeToggle = false, colors = DatePickerDefaults.colors(
                containerColor = Color.Transparent, titleContentColor = e.onSurfaceVariant, headlineContentColor = e.onSurface,
                weekdayContentColor = e.onSurfaceVariant, navigationContentColor = e.primary, yearContentColor = e.onSurface,
                currentYearContentColor = e.primary, selectedYearContentColor = e.onPrimary, selectedYearContainerColor = e.primary,
                dayContentColor = e.onSurface, selectedDayContentColor = e.onPrimary, selectedDayContainerColor = e.primary,
                todayContentColor = e.primary, todayDateBorderColor = e.tertiary, dividerColor = e.outline,
            ))
        },
        relleno = 12.dp,
    )
}

/** Diálogo para elegir una hora (reloj de 24 h), con el cuerpo del tema. */
@Composable
fun DialogoHora(hora: Int, minuto: Int, titulo: String = "¿A qué hora?", onCerrar: () -> Unit, onElegir: (Int, Int) -> Unit) {
    Dialog(onDismissRequest = onCerrar) {
        CuerpoSelectorHora(hora, minuto, titulo, Modifier.sizeIn(minWidth = 280.dp, maxWidth = 560.dp), onCerrar, onElegir)
    }
}

/** El cuerpo del selector de hora, sin la ventana (para las capturas). */
@Composable
fun CuerpoSelectorHora(hora: Int, minuto: Int, titulo: String = "¿A qué hora?", modifier: Modifier = Modifier,
                       onCerrar: () -> Unit = {}, onElegir: (Int, Int) -> Unit = { _, _ -> }) {
    val e = MaterialTheme.colorScheme
    val estado = rememberTimePickerState(initialHour = hora, initialMinute = minuto, is24Hour = true)
    CuerpoDialogo(
        confirmButton = { TextButton(onClick = { onElegir(estado.hour, estado.minute) }) { Text("aceptar") } },
        modifier = modifier,
        dismissButton = { TextButton(onClick = onCerrar) { Text("cancelar") } },
        title = { Text(titulo) },
        text = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(state = estado, colors = TimePickerDefaults.colors(
                    clockDialColor = e.surfaceVariant, clockDialSelectedContentColor = e.onPrimary, clockDialUnselectedContentColor = e.onSurfaceVariant,
                    selectorColor = e.primary, containerColor = Color.Transparent,
                    timeSelectorSelectedContainerColor = e.primaryContainer, timeSelectorUnselectedContainerColor = e.surfaceVariant,
                    timeSelectorSelectedContentColor = e.onPrimaryContainer, timeSelectorUnselectedContentColor = e.onSurface,
                ))
            }
        },
    )
}

// ---------------------------------------------------------------- ayudantes de dibujo

/** Dibuja [bloque] en la esquina de arriba a la izquierda y su reflejo en las otras tres. */
private fun DrawScope.esquinasCtl(bloque: DrawScope.() -> Unit) {
    val c = Offset(size.width / 2, size.height / 2)
    bloque()
    scale(-1f, 1f, c) { bloque() }
    scale(1f, -1f, c) { bloque() }
    scale(-1f, -1f, c) { bloque() }
}

/** Estrella de [puntas] puntas; [dentro] es el radio de los valles respecto al de las puntas. */
private fun DrawScope.estrellaCtl(c: Offset, r: Float, color: Color, puntas: Int = 5, dentro: Float = 0.45f, estilo: DrawStyle = Fill) {
    val p = Path()
    for (i in 0 until puntas * 2) {
        val rr = if (i % 2 == 0) r else r * dentro
        val a = Math.toRadians(-90.0 + 180.0 / puntas * i)
        val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
        if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
    }
    p.close()
    drawPath(p, color, style = estilo)
}

private fun DrawScope.romboCtl(c: Offset, rx: Float, ry: Float, color: Color) {
    drawPath(Path().apply { moveTo(c.x, c.y - ry); lineTo(c.x + rx, c.y); lineTo(c.x, c.y + ry); lineTo(c.x - rx, c.y); close() }, color)
}
