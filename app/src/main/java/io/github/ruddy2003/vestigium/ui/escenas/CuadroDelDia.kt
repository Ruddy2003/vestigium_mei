package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Gato
import io.github.ruddy2003.vestigium.ui.GatoDelDia
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.ahorroDeEnergia
import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import io.github.ruddy2003.vestigium.ui.rememberSentir
import kotlinx.coroutines.delay
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  El cuadro en Hoy (plan de historia y escenas, sección 3.5; tanda P6, 13 nov 2026)
// =====================================================================
//
// Arriba de Hoy, donde antes iba la banda del gato de pixel: el paisaje del personaje de «Otras vidas» del tema, a la
// hora que es, con el gato dentro. Encima lleva una cabecera con el título del cuento de hoy, «leer ›» (abre el
// cuento) y el glifo para plegarlo a una línea. Nada de esto se guarda, salvo si está plegado (`Config.plegadas`).
// Lo único tuyo que usa es el ánimo del gato (`Gato.humor`), el mismo de siempre.

/** La clave con la que `Config.plegadas` recuerda que el cuadro está plegado. */
const val CLAVE_CUADRO = "Cuadro del día"

/** Hasta cuántas letras el título del cuento va en la letra grande de la cabecera («El remo»); los más largos, en la menor. */
private const val TITULO_CORTO = 30

/**
 * Si ya existe la pantalla «Hoy en la historia» (la ruta `historia_hoy`). La crea la tanda H1 del plan, que pone esto
 * en `true`. Hasta entonces el cuadro no lleva la banderita ni atiende la pulsación larga: no se ofrece un gesto que
 * no abre nada. El gesto ya está hecho y probado ([VistaEscena], `alMantener`).
 */
const val HAY_HISTORIA_HOY = false

/**
 * El cuadro del día en Hoy: la cabecera plegable y, debajo, el paisaje con su gato.
 *
 * - [cuadro]: lo que toca hoy (de [cuadroDeHoy]). [hoy]: tu día lógico, para el ánimo del gato.
 * - **La cabecera** es la de una `Seccion` plegable, con el título del cuento en lugar del nombre de la sección (hasta
 *   dos renglones), «leer ›» ([alLeer]) y el glifo del tema para plegar; tocar el resto de la cabecera pliega o
 *   despliega. Plegado queda solo la cabecera.
 * - **La marca de día especial** es un distintivo pequeño montado sobre el borde de arriba del cuadro, a la derecha,
 *   fuera del dibujo; con el cuadro plegado sale en la cabecera. Dura todo el día.
 * - **Gestos sobre el cuadro:** un toque corto en el gato lo acaricia (una vibración corta, si las vibraciones están
 *   encendidas) y en otro sitio lo llama; con [alHistoria], mantener el dedo 700 ms llena la banderita y abre «Hoy en
 *   la historia». Sin [alHistoria] no hay banderita ni pulsación larga.
 * - **Se detiene solo:** con las animaciones apagadas o con ahorro de energía queda quieto, y mientras está fuera de
 *   la vista (al desplazar Hoy hacia abajo) no traza nada.
 * - **Si la escena falla al dibujar,** ese día sale el gato de pixel en su lugar.
 *
 * Para las capturas: [humor] fija el ánimo del gato, [horaFija] la hora, [clave] deja tener uno abierto y otro plegado
 * en la misma imagen y [pasoGato] fija la postura del gato.
 */
@Composable
fun CuadroDelDia(
    cuadro: CuadroHoy, hoy: String, alLeer: () -> Unit, modifier: Modifier = Modifier, alHistoria: (() -> Unit)? = null,
    humor: Gato.Humor? = null, horaFija: Double? = null, clave: String = CLAVE_CUADRO, pasoGato: PasoGato? = null,
) {
    val ctx = LocalContext.current
    var fallo by remember(cuadro.ficha.vida, cuadro.ficha.dia) { mutableStateOf(false) }
    if (fallo) { GatoDelDia(hoy, modifier); return }
    val animo = humor ?: remember(Repo.dias, Repo.salud, Repo.momentos, hoy) { Gato.humor(hoy) }
    val hora = horaFija ?: horaDeAhora()
    val animar = horaFija == null && !LocalInspectionMode.current && Repo.config.animaciones && !ahorroDeEnergia(ctx)
    var seVe by remember { mutableStateOf(true) }
    val sentir = rememberSentir()
    val quien = cuadro.quien
    Seccion(clave, latin = null, plegable = true, modifier = modifier, cabecera = { plegada ->
        // Los títulos de los días especiales son largos (hasta unas 75 letras): van en letra algo menor, para que quepan en dos renglones.
        val letra = if (cuadro.titulo.length > TITULO_CORTO) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium
        Text(cuadro.titulo, style = letra, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        if (plegada && cuadro.especial) MarcaEspecial(Modifier.padding(start = 8.dp))
        BotonTexto(onClick = alLeer, modifier = Modifier.semantics { contentDescription = "Leer el cuento de hoy de ${quien.nombre}" }) {
            Text("leer ›", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
    }) {
        Box {
            VistaEscena(
                cuadro.escena, cuadro.ficha, hora,
                Modifier.fillMaxWidth().onGloballyPositioned { c -> val r = c.boundsInWindow(); seVe = r.width > 1f && r.height > 1f },
                MaterialTheme.shapes.medium, animar = animar, banderita = if (alHistoria != null) cuadro.banderita else null,
                alFallar = { fallo = true }, humor = animo, pelaje = cuadro.pelaje, pasoGato = pasoGato,
                toques = true, alAcariciar = { Sentidos.vibrarEvento(ctx, Sentidos.Evento.TOQUE) },
                alMantener = alHistoria?.let { abrir -> { sentir(Sentidos.Evento.ABRIR); abrir() } }, pausa = !seVe,
                descripcion = "El cuadro de hoy: el entorno de ${quien.nombre}, ${quien.lugar}. ${cuadro.titulo}." + if (cuadro.especial) " Hoy es un día especial en su vida." else "",
                acciones = listOf("Leer el cuento de hoy" to alLeer),
            )
            if (cuadro.especial) MarcaEspecial(Modifier.align(Alignment.TopEnd).offset(x = (-12).dp, y = (-8).dp))
        }
    }
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
