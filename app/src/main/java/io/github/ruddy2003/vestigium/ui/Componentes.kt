@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.privado
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Opcion

/** Esquinas según el tema (rectas en cyberpunk, suaves en helénico). */
private val R4 @Composable get() = MaterialTheme.shapes.small

/** Banda ornamental bajo cada título: meandro (romano), greca enmarcada (helénico) o pista de circuito (cyberpunk). */
@Composable
fun Meandro(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)) {
    when (Estilo.tema) {
        TemaId.CIBER -> Circuito(modifier)
        TemaId.HELENO -> Greca(modifier, color)
        TemaId.MEDIEVAL -> Zarcillo(modifier)
        TemaId.DISTOPIA -> Filete(modifier)
        TemaId.REFUGIO -> Destellos(modifier)
        TemaId.ORDEN -> Galones(modifier)
        TemaId.LABORATORIO -> Regla(modifier)
        TemaId.COLONOS -> Vineta(modifier)
        TemaId.NAPOLEON -> Laureles(modifier)
        TemaId.BOTANICO -> Enredadera(modifier)
        TemaId.GOTICO -> Arqueria(modifier)
        TemaId.ESPACIO -> Orbita(modifier)
        TemaId.ANDALUS -> Lazo(modifier)
        TemaId.MONGOL -> Alhan(modifier)
        TemaId.RENACIMIENTO -> Trazo(modifier)
        TemaId.NAVIDAD -> Guirnalda(modifier)
        TemaId.SEDA -> Monedas(modifier)
        TemaId.URSS -> Barra(modifier)
        TemaId.REVOLUCION -> Tricolor(modifier)
        TemaId.ANTIGUAS -> Runica(modifier)
        TemaId.ROMANO -> Canvas(modifier.fillMaxWidth().height(9.dp)) {
            val h = size.height; val u = h / 3f
            val paso = u * 4
            val p = Path()
            var x = 0f
            p.moveTo(0f, h)
            while (x + paso <= size.width) {
                p.lineTo(x, 0f); p.lineTo(x + u * 3, 0f); p.lineTo(x + u * 3, u * 2); p.lineTo(x + u, u * 2); p.lineTo(x + u, u)
                p.lineTo(x + u * 2, u); p.moveTo(x + u * 2, u); p.moveTo(x, h); p.lineTo(x + paso, h)
                p.moveTo(x + paso, h)
                x += paso
            }
            drawPath(p, color, style = Stroke(width = 1.4f, cap = StrokeCap.Square, join = StrokeJoin.Miter))
        }
    }
}

/** Greca griega entre dos filetes, como en el borde de un vaso ático. */
@Composable
private fun Greca(modifier: Modifier, color: Color) {
    Canvas(modifier.fillMaxWidth().height(15.dp)) {
        val m = size.height * 0.22f
        val h = size.height - 2 * m
        val u = h / 3f
        val paso = u * 4
        drawLine(color, Offset(0f, 0.6f), Offset(size.width, 0.6f), 1.1f)
        drawLine(color, Offset(0f, size.height - 0.6f), Offset(size.width, size.height - 0.6f), 1.1f)
        val p = Path()
        var x = (size.width % paso) / 2
        val b = m + h
        p.moveTo(0f, b); p.lineTo(x, b)
        while (x + paso <= size.width) {
            p.lineTo(x, m); p.lineTo(x + u * 3, m); p.lineTo(x + u * 3, m + u * 2); p.lineTo(x + u, m + u * 2); p.lineTo(x + u, m + u); p.lineTo(x + u * 2, m + u)
            p.moveTo(x, b); p.lineTo(x + paso, b)
            x += paso
        }
        p.lineTo(size.width, b)
        drawPath(p, color, style = Stroke(width = 1.5f, cap = StrokeCap.Square, join = StrokeJoin.Miter))
    }
}

/** Pista de circuito: línea fina con nodos y un tramo encendido en magenta. */
@Composable
private fun Circuito(modifier: Modifier) {
    val cian = MaterialTheme.colorScheme.primary
    val magenta = MaterialTheme.colorScheme.secondary
    Canvas(modifier.fillMaxWidth().height(10.dp)) {
        val y = size.height / 2
        drawLine(cian.copy(alpha = 0.35f), Offset(0f, y), Offset(size.width, y), 1.5f)
        drawLine(magenta, Offset(0f, y), Offset(size.width * 0.16f, y), 2.5f)
        drawLine(cian, Offset(size.width * 0.16f, y), Offset(size.width * 0.22f, y), 2.5f)
        val paso = 46.dp.toPx()
        var x = size.width * 0.28f
        var k = 0
        while (x < size.width) {
            val lado = if (k % 3 == 0) 6f else 3.5f
            drawRect(cian.copy(alpha = if (k % 3 == 0) 0.9f else 0.5f), Offset(x - lado / 2, y - lado / 2), androidx.compose.ui.geometry.Size(lado, lado))
            if (k % 4 == 1) drawLine(cian.copy(alpha = 0.4f), Offset(x, y), Offset(x + 10f, y - size.height / 2), 1.2f)
            x += paso; k++
        }
    }
}

/** El sol de Vergina: la estrella de dieciséis rayos de la casa real macedonia, la de Alejandro. */
@Composable
fun SolVergina(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Canvas(modifier.size(30.dp)) { sol(Offset(size.width / 2, size.height / 2), size.minDimension / 2, color) }
}

/** Encabezado de sección: numeral y nombre según el tema, con el nombre en español como comentario. */
@Composable
fun Seccion(titulo: String, ayuda: String? = null, numero: Int? = null, latin: String? = Romano.latin(titulo), modifier: Modifier = Modifier,
            plegable: Boolean = false, plegadaPorDefecto: Boolean = false, autoPlegar: Boolean = false, content: @Composable () -> Unit) {
    val (nombre, comentario) = Estilo.seccion(titulo, latin)
    // Las secciones plegables recuerdan si las cerraste (se guarda en tu configuración). Las que empiezan plegadas
    // guardan «+título» cuando las abres. Las de [autoPlegar] se vuelven a plegar al cerrar la app o al cambiar de día.
    val plegadas = io.github.ruddy2003.vestigium.data.Repo.config.plegadas
    val claveAuto = "${SesionApp.n}|${io.github.ruddy2003.vestigium.data.Fechas.diaLogico()}"
    var abiertaAuto by androidx.compose.runtime.remember(claveAuto) { mutableStateOf(false) }
    val plegable = plegable || autoPlegar
    val plegada = if (autoPlegar) !abiertaAuto else plegable && if (plegadaPorDefecto) "+$titulo" !in plegadas else titulo in plegadas
    Column(modifier.fillMaxWidth().padding(top = 26.dp).entrada(numero ?: 0)) {
        Row(
            verticalAlignment = if (Estilo.tema == TemaId.MEDIEVAL || Estilo.tema == TemaId.DISTOPIA) Alignment.CenterVertically else Alignment.Bottom,
            modifier = if (!plegable) Modifier else if (autoPlegar) Modifier.fillMaxWidth().clickable { abiertaAuto = !abiertaAuto } else Modifier.fillMaxWidth().clickable {
                io.github.ruddy2003.vestigium.data.Repo.cambiarConfig { c ->
                    val clave = if (plegadaPorDefecto) "+$titulo" else titulo
                    val quitar = if (plegadaPorDefecto) !plegada else plegada
                    c.copy(plegadas = if (quitar) c.plegadas - clave else c.plegadas + clave)
                }
            },
        ) {
            val tema = Estilo.tema
            if (numero != null) {
                Text(Estilo.numeral(numero), style = MaterialTheme.typography.titleLarge, color = if (tema == TemaId.CIBER) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary)
                Text(when (tema) { TemaId.CIBER, TemaId.DISTOPIA, TemaId.REFUGIO, TemaId.LABORATORIO, TemaId.ORDEN -> " "; else -> "  ·  " },
                    style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.outline)
            }
            val estilo = MaterialTheme.typography.titleLarge
            val peso = if (plegable) Modifier.weight(1f, fill = tema != TemaId.DISTOPIA) else Modifier
            when (tema) {
                // Capitular en rojo, como las rúbricas de los manuscritos.
                TemaId.MEDIEVAL -> Text(buildAnnotatedString {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontSize = estilo.fontSize * 1.55f)) { append(nombre.take(1)) }
                    append(nombre.drop(1))
                }, style = estilo, modifier = peso)
                // Rótulo de expediente: blanco sobre negro.
                TemaId.DISTOPIA -> Text(nombre, style = estilo, color = MaterialTheme.colorScheme.background,
                    modifier = peso.background(MaterialTheme.colorScheme.onBackground).padding(horizontal = 6.dp, vertical = 1.dp))
                TemaId.REFUGIO -> Text("▸ $nombre", style = estilo, modifier = peso)
                else -> Text(nombre, style = estilo, color = if (tema == TemaId.CIBER) MaterialTheme.colorScheme.primary else Color.Unspecified, modifier = peso)
            }
            if (plegable && tema == TemaId.DISTOPIA) Spacer(Modifier.weight(1f))
            if (plegable) Text(if (plegada) "▸" else "▾", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (comentario != null) Text(comentario, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 1.dp))
        androidx.compose.animation.AnimatedVisibility(!plegada) {
            Column {
                if (ayuda != null) Text(ayuda, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp, bottom = 10.dp))
                else Spacer(Modifier.height(10.dp))
                content()
            }
        }
    }
}

@Composable
fun Tarjeta(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(modifier = modifier.fillMaxWidth(), shape = R4, color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
        Column(Modifier.padding(14.dp)) { content() }
    }
}

/** Etiqueta de campo, como la clave de una línea de código. */
@Composable
fun Etiqueta(texto: String) {
    Row(Modifier.padding(top = 14.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(Estilo.prefijo, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        Text(texto.lowercase(), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun Ayuda(texto: String, modifier: Modifier = Modifier) {
    Text(texto, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = modifier)
}

/** Línea `clave = valor` en monoespaciada, para datos automáticos. */
@Composable
fun Clave(clave: String, valor: String, ancho: Int = 14) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        // Ancho fijo medido con la letra del tema: así las columnas se alinean aunque no sea monoespaciada.
        val medidor = rememberTextMeasurer()
        val estilo = MaterialTheme.typography.labelLarge
        val anchoDp = with(LocalDensity.current) { medidor.measure("0".repeat(ancho), estilo).size.width.toDp() }
        Text(Estilo.clave(clave), style = estilo, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(anchoDp), maxLines = 1)
        Text("= ", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(valor, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
    }
}

@Composable
fun Escala(valor: Int?, desde: Int, hasta: Int, anclas: Map<Int, String>? = null, onCambio: (Int?) -> Unit) {
    val sentir = rememberSentir()
    val oscuro = esOscuro()
    val n = hasta - desde + 1
    // Las escalas desde 0 son de intensidad (dolor, impulso); las demás van de desagradable a agradable.
    fun tono(i: Int): Color = if (desde == 0) Tonos.secuencial((i - desde).toFloat() / (n - 1), oscuro) else Tonos.divergente((i - desde).toFloat() / (n - 1), oscuro)
    val actual by androidx.compose.runtime.rememberUpdatedState(valor)
    Column {
        // Se puede tocar un número o deslizar el dedo sobre la fila.
        Row(
            Modifier.fillMaxWidth().pointerInput(desde, hasta) {
                detectHorizontalDragGestures { change, _ ->
                    val i = (desde + (change.position.x / size.width * n).toInt()).coerceIn(desde, hasta)
                    if (i != actual) { onCambio(i); sentir(Sentidos.Evento.ELEGIR) }
                    change.consume()
                }
            },
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            for (i in desde..hasta) {
                val sel = valor == i
                val c = tono(i)
                Box(
                    Modifier.weight(1f).heightIn(min = 52.dp).clip(R4)
                        .background(if (sel) c else MaterialTheme.colorScheme.surface)
                        .border(if (sel) 2.dp else 1.dp, if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, R4)
                        .clickable { onCambio(if (sel) null else i); sentir(Sentidos.Evento.ELEGIR) },
                    contentAlignment = Alignment.Center,
                ) {
                    // Una franja del color de cada valor, para leer la escala sin depender solo del número.
                    if (!sel) Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(4.dp).background(c))
                    Text("$i", style = MaterialTheme.typography.labelLarge.copy(fontSize = 17.sp, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal),
                        color = if (sel) Tonos.sobre(c) else MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        if (anclas != null) Row(Modifier.padding(top = 7.dp)) {
            Text(if (valor == null) "= null" else "= $valor", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(if (valor == null) "  sin marcar · toca o desliza" else "  ${anclas[valor] ?: ""}".lowercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun Ficha(texto: String, activa: Boolean, onClick: () -> Unit) {
    val sentir = rememberSentir()
    val toque = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    Surface(
        onClick = { sentir(Sentidos.Evento.ELEGIR); onClick() }, shape = R4, interactionSource = toque, modifier = Modifier.presion(toque, 0.93f),
        color = if (activa) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (activa) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
    ) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
            if (activa) { Box(Modifier.size(6.dp).background(MaterialTheme.colorScheme.primary)); Spacer(Modifier.width(8.dp)) }
            Text(texto, style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (activa) FontWeight.SemiBold else FontWeight.Normal))
        }
    }
}

@Composable
fun FilaFichas(opciones: List<Opcion>, valor: String?, onCambio: (String?) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        opciones.forEach { o -> Ficha(o.nombre, valor == o.id) { onCambio(if (valor == o.id) null else o.id) } }
    }
}

@Composable
fun Opciones(opciones: List<Opcion>, valor: String?, onCambio: (String?) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp), maxItemsInEachRow = 2) {
        opciones.forEach { o ->
            val sel = valor == o.id
            Surface(
                onClick = { onCambio(if (sel) null else o.id) }, modifier = Modifier.weight(1f), shape = R4,
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
    val sentir = rememberSentir()
    Row(Modifier.fillMaxWidth().clickable { sentir(Sentidos.Evento.ELEGIR); onCambio(!valor) }.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(texto, style = MaterialTheme.typography.bodyLarge)
            if (detalle != null) Ayuda(detalle)
        }
        Switch(checked = valor, onCheckedChange = { sentir(Sentidos.Evento.ELEGIR); onCambio(it) }, colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary))
    }
}

@Composable
fun ListaEtiquetas(lista: List<String>, pista: String, prefijo: String = "", normalizar: (String) -> String, onCambio: (List<String>) -> Unit) {
    var texto by remember { mutableStateOf("") }
    fun agregar() {
        val nuevos = texto.split(",").map(normalizar).filter { it.isNotEmpty() }
        if (nuevos.isNotEmpty()) { onCambio(lista + nuevos); texto = "" }
    }
    Column {
        if (lista.isEmpty()) Text("[ ]", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            lista.forEachIndexed { i, t ->
                Surface(shape = R4, color = MaterialTheme.colorScheme.surfaceVariant) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 10.dp)) {
                        Text(prefijo + t, style = MaterialTheme.typography.labelLarge)
                        TextButton(onClick = { onCambio(lista.filterIndexed { j, _ -> j != i }) }, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(36.dp)) { Text("×", fontSize = 16.sp) }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = texto, onValueChange = { texto = it }, modifier = Modifier.weight(1f), singleLine = true,
                placeholder = { Text(pista, style = MaterialTheme.typography.labelLarge) }, textStyle = MaterialTheme.typography.labelLarge,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { agregar() }),
            )
            Spacer(Modifier.width(8.dp))
            BotonSecundario("+ añadir") { agregar() }
        }
    }
}

/** Campo de texto largo con barra superior como un archivo abierto en un editor. */
@Composable
fun Editor(valor: String, archivo: String, pista: String, minimo: Int = 200, pie: String? = null, onCambio: (String) -> Unit) {
    // Se lleva el cursor para saber qué palabra se está escribiendo y sugerir @personas y #temas.
    var campo by remember { mutableStateOf(TextFieldValue(valor, TextRange(valor.length))) }
    if (campo.text != valor) campo = TextFieldValue(valor, TextRange(valor.length))
    val etiquetas = remember(io.github.ruddy2003.vestigium.data.Repo.dias.size, io.github.ruddy2003.vestigium.data.Repo.momentos.size) { Etiquetas.frecuentes() }
    val antes = campo.text.substring(0, campo.selection.start.coerceIn(0, campo.text.length))
    val token = Regex("[@#][\\p{L}\\d_]*$").find(antes)?.value
    val sugerencias = if (token == null) emptyList() else {
        val pre = token.first(); val q = token.drop(1).lowercase()
        (if (pre == '@') etiquetas.first else etiquetas.second).filter { it.startsWith(q) && it != q }.take(6).map { "$pre$it" }
    }
    Column(Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, R4)) {
        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(7.dp).background(MaterialTheme.colorScheme.primary))
            Text("  $archivo", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
            if (pie != null) Text(pie, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(Modifier.width(3.dp).fillMaxHeight().background(MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)))
            androidx.compose.foundation.text.BasicTextField(
                value = campo, onValueChange = { campo = it; if (it.text != valor) onCambio(it.text) },
                modifier = Modifier.weight(1f).heightIn(min = minimo.dp).padding(12.dp),
                textStyle = MaterialTheme.typography.labelLarge.copy(fontSize = 14.5.sp, lineHeight = 22.sp, letterSpacing = 0.sp, fontWeight = FontWeight.Normal, color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                visualTransformation = rememberMarcas(),
                decorationBox = { campoTexto ->
                    Box {
                        if (campo.text.isEmpty()) Text(pista, style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        campoTexto()
                    }
                },
            )
        }
        if (sugerencias.isNotEmpty()) FlowRow(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            sugerencias.forEach { s ->
                Ficha(s, false) {
                    val ini = antes.length - token!!.length
                    val nuevo = campo.text.substring(0, ini) + s + " " + campo.text.substring(antes.length)
                    campo = TextFieldValue(nuevo, TextRange(ini + s.length + 1))
                    onCambio(nuevo)
                }
            }
        }
    }
}

/** Personas y temas que ya usaste, de más a menos frecuentes, para sugerirlos al escribir. */
object Etiquetas {
    fun frecuentes(): Pair<List<String>, List<String>> {
        val p = HashMap<String, Int>(); val t = HashMap<String, Int>()
        val textos = io.github.ruddy2003.vestigium.data.Repo.dias.values.map { it.nota } + io.github.ruddy2003.vestigium.data.Repo.momentos.filter { m -> !m.privado }.map { it.texto }
        textos.forEach { x ->
            io.github.ruddy2003.vestigium.data.Texto.personas(x).distinct().forEach { p.merge(it, 1, Int::plus) }
            io.github.ruddy2003.vestigium.data.Texto.temas(x).distinct().forEach { t.merge(it, 1, Int::plus) }
        }
        return p.entries.sortedByDescending { it.value }.map { it.key } to t.entries.sortedByDescending { it.value }.map { it.key }
    }
}

@Composable
fun BotonDictado(onTexto: (String) -> Unit) {
    val ctx = LocalContext.current
    val lanzar = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        val t = res.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!t.isNullOrBlank()) onTexto(t)
    }
    BotonSecundario("● dictar") {
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-MX")
            .putExtra(RecognizerIntent.EXTRA_PROMPT, "Dicta")
        try { lanzar.launch(i) } catch (e: ActivityNotFoundException) { Toast.makeText(ctx, "Este teléfono no tiene dictado por voz disponible.", Toast.LENGTH_SHORT).show() }
    }
}

@Composable
fun BotonPrincipal(texto: String, modifier: Modifier = Modifier, habilitado: Boolean = true, onClick: () -> Unit) {
    val sentir = rememberSentir()
    val toque = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    Button(onClick = { sentir(Sentidos.Evento.GUARDAR); onClick() }, enabled = habilitado, modifier = modifier.heightIn(min = 48.dp).presion(toque), shape = R4,
        interactionSource = toque, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
        val tema = Estilo.tema
        val base = MaterialTheme.typography.titleLarge
        // La letra de Leonardo es caligráfica y muy fina: en botones va la Cormorant, que se lee mejor chica.
        val estilo = if (tema == TemaId.RENACIMIENTO) base.copy(fontFamily = LetraExtra.cormorant, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, letterSpacing = 0.5.sp)
            else base.copy(fontSize = if (tema == TemaId.MEDIEVAL || tema == TemaId.GOTICO) 17.sp else 14.sp, letterSpacing = if (Estilo.mayusculas) 2.sp else 0.5.sp)
        Text(if (Estilo.mayusculas) texto.uppercase() else texto, style = estilo)
    }
}

@Composable
fun BotonSecundario(texto: String, modifier: Modifier = Modifier, habilitado: Boolean = true, onClick: () -> Unit) {
    val sentir = rememberSentir()
    val toque = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    OutlinedButton(onClick = { sentir(Sentidos.Evento.TOQUE); onClick() }, enabled = habilitado, modifier = modifier.presion(toque), shape = R4, interactionSource = toque, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)) {
        Text(texto, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun Cifra(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = R4, color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(Modifier.padding(10.dp)) {
            Text(contando(valor), style = MaterialTheme.typography.labelLarge.copy(fontSize = 19.sp, letterSpacing = 0.sp), maxLines = 1)
            Text(etiqueta.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
        }
    }
}

@Composable
fun Vacio(texto: String) {
    val linea = MaterialTheme.colorScheme.outline
    Box(
        Modifier.fillMaxWidth().drawBehind {
            drawRoundRect(linea, cornerRadius = CornerRadius(4.dp.toPx()), style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))))
        }.padding(18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("∅  $texto", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
fun Aviso(texto: String) {
    Row(Modifier.fillMaxWidth().padding(top = 10.dp).height(IntrinsicSize.Min).background(MaterialTheme.colorScheme.secondaryContainer)) {
        Box(Modifier.width(3.dp).fillMaxHeight().background(MaterialTheme.colorScheme.secondary))
        Text(texto, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(10.dp))
    }
}

@Composable
fun FilaMenu(titulo: String, detalle: String? = null, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge)
            if (detalle != null) Text(detalle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text("→", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun Pastilla(texto: String, color: Color = MaterialTheme.colorScheme.primary) {
    Box(Modifier.border(1.dp, color, R4).padding(horizontal = 6.dp, vertical = 1.dp).widthIn(min = 10.dp)) {
        Text(texto, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

fun tipoNombre(id: String, discreto: Boolean): String = Catalogo.tipo(id)?.let { if (discreto) it.discreto else it.nombre } ?: if (id == "muestreo" || id == "animo") "Ánimo" else id

