package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Gato
import io.github.ruddy2003.vestigium.ui.GatoDelDia
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ahorroDeEnergia
import io.github.ruddy2003.vestigium.ui.rememberSentir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  El cuadro en Hoy (plan de historia y escenas, sección 3.5; tanda P6, 13 nov 2026)
// =====================================================================
//
// Arriba de Hoy, donde antes iba la banda del gato de pixel: el paisaje del personaje de «Otras vidas» del tema, a la
// hora que es, con el gato dentro. Debajo, un botón ancho con el título del cuento de hoy, que lo abre.
// Nada de esto se guarda. Lo único tuyo que usa es el ánimo del gato (`Gato.humor`), el mismo de siempre.
//
// Después de probarlo en el teléfono (13 nov 2026) el autor pidió: que no se pliegue, y que «leer» no sea un botoncito
// en una cabecera sino un botón grande debajo del cuadro, con el título, de color y con aspecto de cuento.

/**
 * Si ya existe la pantalla «Hoy en la historia» (la ruta `historia_hoy`). La crea la tanda H1 del plan, que pone esto
 * en `true`. Hasta entonces el cuadro no lleva la banderita ni atiende la pulsación larga: no se ofrece un gesto que
 * no abre nada. El gesto ya está hecho y probado ([VistaEscena], `alMantener`).
 */
const val HAY_HISTORIA_HOY = false

/**
 * El cuadro del día en Hoy: el paisaje con su gato y, debajo, el botón del cuento. No se pliega.
 *
 * - [tema] y [hoy] (tu día lógico) dicen qué cuadro toca. Se prepara fuera del hilo de la pantalla: mientras tanto
 *   su sitio queda reservado, para que Hoy no dé un tirón al abrirse ni salte cuando el cuadro llega. Solo debe
 *   llamarse si [hayCuadroEnHoy]; si al final no hay cuadro (o una escena falla al dibujar), sale el gato de pixel.
 * - **La marca de día especial** es un distintivo pequeño montado sobre el borde de arriba del cuadro, a la derecha,
 *   fuera del dibujo. Dura todo el día.
 * - **El botón del cuento** ([BotonCuento]) lleva el título del cuento de hoy y «leer el cuento»; todo él abre el
 *   cuento ([alLeer], con el id del personaje).
 * - **Gestos sobre el cuadro:** un toque corto en el gato lo acaricia (una vibración corta, si las vibraciones están
 *   encendidas) y en otro sitio lo llama; con [alHistoria], mantener el dedo 700 ms llena la banderita y abre «Hoy en
 *   la historia». Sin [alHistoria] no hay banderita ni pulsación larga.
 * - **Se detiene solo:** con las animaciones apagadas o con ahorro de energía queda quieto, y mientras está fuera de
 *   la vista (al desplazar Hoy hacia abajo) no traza nada.
 *
 * Para las capturas: [cuadro] da uno ya calculado (y entonces todo se traza ahí mismo, sin esperar), [humor] fija el
 * ánimo del gato, [horaFija] la hora y [pasoGato] la postura del gato.
 */
@Composable
fun CuadroDelDia(
    tema: TemaId, hoy: String, alLeer: (vida: String) -> Unit, modifier: Modifier = Modifier, alHistoria: (() -> Unit)? = null,
    cuadro: CuadroHoy? = null, humor: Gato.Humor? = null, horaFija: Double? = null, pasoGato: PasoGato? = null,
) {
    val ctx = LocalContext.current
    val enVivo = cuadro == null && !LocalInspectionMode.current
    // El cuadro de hoy: el que se dio, el que ya estaba calculado o, la primera vez, el que se calcula en segundo plano.
    var listo by remember(tema, hoy, cuadro) { mutableStateOf(cuadro ?: if (enVivo) cuadroEnMemoria(tema, hoy) else cuadroDeHoy(tema, hoy)) }
    var sinCuadro by remember(tema, hoy, cuadro) { mutableStateOf(!enVivo && listo == null) }
    if (enVivo) LaunchedEffect(tema, hoy) {
        if (listo != null) return@LaunchedEffect
        val calculado = withContext(Dispatchers.Default) { runCatching { cuadroDeHoy(tema, hoy) }.getOrNull() }
        if (calculado == null) sinCuadro = true else listo = calculado
    }
    var fallo by remember(tema, hoy) { mutableStateOf(false) }
    if (sinCuadro || fallo) { GatoDelDia(hoy, modifier); return }

    val cs = MaterialTheme.colorScheme
    val forma = MaterialTheme.shapes.medium
    val sentir = rememberSentir()
    val c = listo
    Column(modifier.fillMaxWidth().padding(top = 16.dp)) {
        if (c == null) {
            // Todavía se prepara: su sitio, con el mismo tamaño que tendrá.
            Box(Modifier.fillMaxWidth().aspectRatio(4f / 3f).clip(forma).background(cs.surfaceVariant.copy(alpha = 0.35f)))
            BotonCuento("", especial = false, alLeer = null, modifier = Modifier.padding(top = 8.dp))
        } else {
            val animo = humor ?: remember(Repo.dias, Repo.salud, Repo.momentos, hoy) { Gato.humor(hoy) }
            val hora = horaFija ?: horaDeAhora()
            val animar = horaFija == null && enVivo && Repo.config.animaciones && !ahorroDeEnergia(ctx)
            var seVe by remember { mutableStateOf(true) }
            val quien = c.quien
            val leer = { sentir(Sentidos.Evento.ABRIR); alLeer(quien.id) }
            Box {
                VistaEscena(
                    c.escena, c.ficha, hora,
                    Modifier.fillMaxWidth().onGloballyPositioned { donde -> val r = donde.boundsInWindow(); seVe = r.width > 1f && r.height > 1f },
                    forma, animar = animar, banderita = if (alHistoria != null) c.banderita else null,
                    alFallar = { fallo = true }, humor = animo, pelaje = c.pelaje, pasoGato = pasoGato,
                    toques = true, alAcariciar = { Sentidos.vibrarEvento(ctx, Sentidos.Evento.TOQUE) },
                    alMantener = alHistoria?.let { abrir -> { sentir(Sentidos.Evento.ABRIR); abrir() } }, pausa = !seVe,
                    descripcion = "El cuadro de hoy: el entorno de ${quien.nombre}, ${quien.lugar}." + if (c.especial) " Hoy es un día especial en su vida." else "",
                    acciones = listOf("Leer el cuento de hoy" to leer),
                    enSegundoPlano = enVivo, fondo = cs.surfaceVariant.copy(alpha = 0.35f),
                )
                if (c.especial) MarcaEspecial(Modifier.align(Alignment.TopEnd).offset(x = (-12).dp, y = (-8).dp))
            }
            BotonCuento(c.titulo, c.especial, leer, Modifier.padding(top = 8.dp), de = quien.nombre)
        }
    }
}

/** Hasta cuántas letras el título del cuento va en la letra grande del botón («El remo»); los más largos, en la menor. */
private const val TITULO_CORTO = 30

/**
 * El botón del cuento: una franja ancha del color principal del tema, debajo del cuadro, como la cartela de una
 * lámina. Lleva el [titulo] del cuento de hoy y, debajo y más pequeño, «leer el cuento»: el botón es todo, no solo
 * esas palabras. Para que se entienda que es un cuento tiene un libro abierto a la izquierda, un filete por dentro
 * como el de una tapa y una cinta de marcapáginas que cuelga a la derecha. Con [especial], el renglón pequeño lo dice.
 * Sin [alLeer] (mientras el cuadro se prepara) queda apagado y vacío, con su tamaño.
 */
@Composable
internal fun BotonCuento(titulo: String, especial: Boolean, alLeer: (() -> Unit)?, modifier: Modifier = Modifier, de: String = "") {
    val cs = MaterialTheme.colorScheme
    val forma = MaterialTheme.shapes.medium
    val tinta = cs.onPrimary
    // La cinta, del color del tema que más se distinga del fondo del botón.
    val cinta = listOf(cs.tertiary, cs.secondary, tinta).maxByOrNull { abs(it.luminance() - cs.primary.luminance()) } ?: tinta
    val apagado = alLeer == null
    Row(
        modifier.fillMaxWidth().heightIn(min = 64.dp).clip(forma).background(if (apagado) cs.surfaceVariant.copy(alpha = 0.35f) else cs.primary)
            .then(if (apagado) Modifier else Modifier
                .drawBehind { adornoDeCuento(tinta, cinta) }
                .clickable(onClickLabel = "leer el cuento", role = Role.Button, onClick = alLeer!!)
                .semantics(mergeDescendants = true) { contentDescription = "El cuento de hoy" + (if (de.isNotEmpty()) " de $de" else "") + ": $titulo" })
            .padding(start = 14.dp, end = 40.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!apagado) {
            Canvas(Modifier.size(34.dp, 26.dp).clearAndSetSemantics { }) { libroAbierto(tinta) }
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                val letra = if (titulo.length > TITULO_CORTO) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium
                Text(titulo, style = letra, color = tinta, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text((if (especial) "día especial · " else "") + "leer el cuento ›", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                    color = tinta.copy(alpha = 0.82f), modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}

/** Lo que el botón del cuento lleva pintado detrás del texto: el filete de la tapa y la cinta de marcapáginas. */
private fun DrawScope.adornoDeCuento(tinta: Color, cinta: Color) {
    val u = 1.dp.toPx()
    // El filete, por dentro del borde, con las esquinas apenas redondeadas.
    drawRoundRect(tinta.copy(alpha = 0.32f), Offset(4 * u, 4 * u), Size(size.width - 8 * u, size.height - 8 * u),
        androidx.compose.ui.geometry.CornerRadius(3 * u), style = Stroke(0.9f * u))
    // La cinta: cuelga del borde de arriba, con su muesca en pico.
    val x = size.width - 28 * u; val ancho = 11 * u; val largo = minOf(size.height * 0.62f, 40 * u)
    val forma = Path().apply {
        moveTo(x, 0f); lineTo(x + ancho, 0f); lineTo(x + ancho, largo); lineTo(x + ancho / 2, largo - 5 * u); lineTo(x, largo); close()
    }
    drawPath(forma, Color.Black.copy(alpha = 0.18f), style = Stroke(1.6f * u))
    drawPath(forma, cinta)
    drawLine(Color.White.copy(alpha = 0.25f), Offset(x + 2.5f * u, 0f), Offset(x + 2.5f * u, largo - 4 * u), 0.8f * u)
}

/** Un libro abierto visto de frente: dos páginas que se curvan desde el lomo, con sus renglones. */
private fun DrawScope.libroAbierto(tinta: Color) {
    val w = size.width; val h = size.height; val medio = w / 2
    val trazo = Stroke(1.5.dp.toPx(), cap = StrokeCap.Round)
    fun pagina(lado: Float) = Path().apply {
        moveTo(medio, h * 0.2f)
        cubicTo(medio + lado * w * 0.14f, h * 0.02f, medio + lado * w * 0.34f, h * 0.02f, medio + lado * w * 0.48f, h * 0.14f)
        lineTo(medio + lado * w * 0.48f, h * 0.86f)
        cubicTo(medio + lado * w * 0.34f, h * 0.74f, medio + lado * w * 0.14f, h * 0.74f, medio, h * 0.94f)
        close()
    }
    for (lado in listOf(-1f, 1f)) {
        drawPath(pagina(lado), tinta.copy(alpha = 0.16f))
        drawPath(pagina(lado), tinta, style = trazo)
        // Los renglones de cada página.
        for (k in 0..2) {
            val y = h * (0.3f + 0.15f * k)
            drawLine(tinta.copy(alpha = 0.7f), Offset(medio + lado * w * 0.13f, y - h * 0.035f), Offset(medio + lado * w * 0.38f, y - h * 0.07f), 1.dp.toPx(), cap = StrokeCap.Round)
        }
    }
    drawLine(tinta, Offset(medio, h * 0.2f), Offset(medio, h * 0.94f), 1.5.dp.toPx(), cap = StrokeCap.Round)
}

/** La hora de ahora, de 0 a 24 con decimales. Se vuelve a mirar cada veinte segundos, solo mientras la app está en pantalla. */
@Composable
private fun horaDeAhora(): Double {
    fun ahora() = LocalTime.now().let { it.hour + it.minute / 60.0 }
    val hora by produceState(ahora()) {
        while (true) { delay(20_000); withFrameMillis { }; value = ahora() }
    }
    return hora
}

/**
 * La marca de día especial: un distintivo redondo, como el de una notificación, con una estrella de cuatro puntas.
 * Toma los colores del tema y lleva un aro del color del fondo, que lo separa del dibujo sobre el que va montado.
 */
@Composable
private fun MarcaEspecial(modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    Canvas(modifier.size(18.dp).semantics { contentDescription = "Día especial" }) {
        val r = size.minDimension / 2f
        drawCircle(cs.background, r)
        drawCircle(cs.primary, r - 2.dp.toPx())
        val estrella = Path().apply {
            for (i in 0 until 8) {
                val a = (-PI / 2 + i * PI / 4).toFloat()
                val d = if (i % 2 == 0) r * 0.58f else r * 0.2f
                val p = Offset(center.x + d * cos(a), center.y + d * sin(a))
                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }
        drawPath(estrella, cs.onPrimary)
    }
}
