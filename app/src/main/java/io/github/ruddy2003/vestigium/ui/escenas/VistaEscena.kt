package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.vidas.FichaDelDia

/** El instante en que queda la parte viva cuando no se anima (animaciones apagadas, ahorro de energía, capturas). */
const val RELOJ_QUIETO = 3.7f

/** Cada cuántos milisegundos se vuelve a trazar la parte viva: unos 15 cuadros por segundo. */
private const val PASO_MS = 66L

/**
 * El cuadro de una escena: el paisaje del personaje a la [hora] (0 a 24, con decimales) del día de su [ficha], en
 * proporción 4:3 y a todo el ancho que se le dé, con las esquinas de [forma] y sin marco. Lo pone en Hoy la tanda P6
 * (`CuadroDelDia`), con su cabecera, sus gestos y la marca de día especial; esto es solo el dibujo.
 *
 * - **La parte quieta** va en su propia capa, guardada como imagen: solo se vuelve a trazar cuando cambia el sello
 *   del momento (otro día, otros veinte minutos, otro tramo del día del personaje) o el tamaño.
 * - **La parte viva** (gotas, humo, llamas, destellos) va encima, en otra capa, y es lo único que se traza en cada
 *   cuadro, unas 15 veces por segundo. Con [animar] en `false` (animaciones apagadas, ahorro de energía, capturas)
 *   se traza una vez, en el instante [relojFijo], y queda quieta. El reloj se detiene solo mientras la app no está
 *   en pantalla.
 * - **La banderita** «Hoy en la historia» del tema ([banderita]) cuelga dentro, arriba a la izquierda. [lleno], de
 *   0 a 1, la va llenando mientras se mantiene el dedo (lo maneja P6).
 * - **Si una escena falla al dibujar,** el cuadro se queda con lo que llevaba pintado y avisa por [alFallar]: un
 *   adorno no puede tumbar la pantalla de Hoy.
 */
@Composable
fun VistaEscena(
    escena: EscenaVida, ficha: FichaDelDia, hora: Double, modifier: Modifier = Modifier, forma: Shape = RectangleShape,
    animar: Boolean = true, relojFijo: Float = RELOJ_QUIETO, banderita: Banderita? = null, lleno: Float = 0f,
    alFallar: (Throwable) -> Unit = {},
) {
    // El momento solo cambia (y con él la parte quieta) cada veinte minutos, o cuando el personaje pasa a otro tramo de su día.
    val momento = remember(ficha, (hora * 3).toInt(), ficha.tramo(hora)?.que) { Momento(ficha, hora) }
    val seMueve = animar && escena.seMueve(momento)
    var reloj by remember { mutableStateOf(relojFijo) }
    if (seMueve) LaunchedEffect(escena) {
        var inicio = -1L; var ultimo = -PASO_MS
        while (true) withFrameMillis { ahora ->
            if (inicio < 0) inicio = ahora
            if (ahora - ultimo >= PASO_MS) { ultimo = ahora; reloj = relojFijo + (ahora - inicio) / 1000f }
        }
    }
    Box(modifier.aspectRatio(4f / 3f).clip(forma)) {
        Spacer(Modifier.fillMaxSize().graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }.drawBehind {
            aSalvo(alFallar) { lienzo { with(escena) { quieta(momento) } } }
        })
        Spacer(Modifier.fillMaxSize().drawBehind {
            val instante = if (seMueve) reloj else relojFijo
            aSalvo(alFallar) { lienzo { with(escena) { viva(momento, instante) } } }
        })
        if (banderita != null) Spacer(
            Modifier.align(Alignment.TopStart).padding(start = 14.dp).size(Banderita.ANCHO.dp, Banderita.ALTO.dp).drawBehind { with(banderita) { dibujar(lleno) } },
        )
    }
}

/**
 * Dibuja [dibujo] y, si lanza una excepción, deja el lienzo como estaba antes de empezar (los recortes y los giros a
 * medio hacer se deshacen) y avisa por [alFallar], en vez de dejar que la excepción cierre la app.
 */
private inline fun DrawScope.aSalvo(alFallar: (Throwable) -> Unit, dibujo: DrawScope.() -> Unit) {
    drawIntoCanvas { tela ->
        val guardados = tela.nativeCanvas.saveCount
        try { dibujo() } catch (e: Exception) { tela.nativeCanvas.restoreToCount(guardados); alFallar(e) }
    }
}
