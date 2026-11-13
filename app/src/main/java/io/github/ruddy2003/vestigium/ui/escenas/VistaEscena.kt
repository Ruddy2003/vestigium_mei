package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.roundToInt
import io.github.ruddy2003.vestigium.ui.Gato
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.FichaDelDia

/** El instante en que queda la parte viva cuando no se anima (animaciones apagadas, ahorro de energía, capturas). */
const val RELOJ_QUIETO = 3.7f

/** Cada cuántos milisegundos se vuelve a trazar la parte viva: unos 15 cuadros por segundo. */
private const val PASO_MS = 66L

/** Cuánto hay que mantener el dedo sobre el cuadro para abrir «Hoy en la historia» (decisión 13 del plan). */
const val MS_MANTENER = 700L

/** Lo que tarda la banderita en empezar a llenarse: un toque corto o el arranque de un desplazamiento no la encienden. */
private const val MS_ANTES_DE_LLENAR = 150L

/** Cuánto puede moverse el dedo, en dp, sin que deje de contar como toque o como pulsación: más que eso es desplazar la pantalla. */
const val HOLGURA_DEDO_DP = 8

/**
 * El cuadro de una escena: el paisaje del personaje a la [hora] (0 a 24, con decimales) del día de su [ficha], en
 * proporción 4:3 y a todo el ancho que se le dé, con las esquinas de [forma] y sin marco. En Hoy lo pone
 * `CuadroDelDia`, con su cabecera y la marca de día especial; esto es el dibujo y sus gestos.
 *
 * - **La parte quieta** va en su propia capa, guardada como imagen: solo se vuelve a trazar cuando cambia el sello
 *   del momento (otro día, otros veinte minutos, otro tramo del día del personaje) o el tamaño.
 *   - Trazarla cuesta (unas décimas de segundo). Con [enSegundoPlano] se pinta fuera del hilo de la pantalla, en una
 *     imagen que se recuerda entre una visita a Hoy y la siguiente: abrir la pestaña no da un tirón. La primera vez
 *     el cuadro aparece un instante después, sobre el color [fondo]; al cambiar la luz, la imagen anterior se queda
 *     hasta que la nueva está lista. Sin [enSegundoPlano] (las capturas) se traza ahí mismo, como siempre.
 * - **La parte viva** (gotas, humo, llamas, destellos) va encima, en otra capa, y es lo único que se traza en cada
 *   cuadro, unas 15 veces por segundo. Con [animar] en `false` (animaciones apagadas, ahorro de energía, capturas)
 *   se traza una vez, en el instante [relojFijo], y queda quieta. El reloj se detiene solo mientras la app no está
 *   en pantalla. Con [pausa] (el cuadro quedó fuera de la vista al desplazar la pantalla) todo se detiene donde iba
 *   y sigue desde ahí al volver: no gasta batería mientras no se ve.
 * - **La banderita** «Hoy en la historia» del tema ([banderita]) cuelga dentro, arriba a la izquierda. Con
 *   [alMantener], mantener el dedo [MS_MANTENER] ms sobre el cuadro la va llenando y, al llenarse, llama a
 *   [alMantener]; se cancela si el dedo se mueve más de [HOLGURA_DEDO_DP] dp. [lleno], de 0 a 1, la pinta llena a
 *   mano (las capturas).
 * - **El gato** va en una tercera capa, encima de las dos, si se da su [humor] (el de `Gato.humor`): camina por el
 *   suelo de la escena con el [pelaje] de su tema. Sin [animar] queda sentado; [pasoGato] lo fija en un cuadro
 *   concreto (las capturas). Sin [humor] no hay gato. Con [toques] responde al dedo: un toque corto sobre él es una
 *   caricia (avisa por [alAcariciar], para la vibración; [mimoFijo] pinta su corazón en las capturas) y en otro sitio
 *   lo hace mirar o ir hacia ahí.
 * - **Todos los gestos los recoge un solo detector,** el del cuadro entero: el toque corto va al gato y la pulsación
 *   larga a [alMantener]. Dos detectores, uno dentro de otro, se quitarían los toques.
 * - **Para lectores de pantalla,** con [descripcion] el cuadro se anuncia con ese texto y ofrece sus acciones con
 *   nombre: acariciar al gato, abrir «Hoy en la historia» y las de [acciones] (leer el cuento).
 * - **Si una escena falla al dibujar,** el cuadro se queda con lo que llevaba pintado y avisa por [alFallar]: un
 *   adorno no puede tumbar la pantalla de Hoy.
 */
@Composable
fun VistaEscena(
    escena: EscenaVida, ficha: FichaDelDia, hora: Double, modifier: Modifier = Modifier, forma: Shape = RectangleShape,
    animar: Boolean = true, relojFijo: Float = RELOJ_QUIETO, banderita: Banderita? = null, lleno: Float = 0f,
    alFallar: (Throwable) -> Unit = {}, humor: Gato.Humor? = null, pelaje: Pelaje = Pelaje.COMUN, pasoGato: PasoGato? = null,
    toques: Boolean = false, alAcariciar: () -> Unit = {}, mimoFijo: Float = -1f,
    alMantener: (() -> Unit)? = null, pausa: Boolean = false, descripcion: String? = null, acciones: List<Pair<String, () -> Unit>> = emptyList(),
    enSegundoPlano: Boolean = false, fondo: Color = Color.Transparent,
) {
    // El momento solo cambia (y con él la parte quieta) cada veinte minutos, o cuando el personaje pasa a otro tramo de su día.
    val momento = remember(ficha, (hora * 3).toInt(), ficha.tramo(hora)?.que) { Momento(ficha, hora) }
    val seMueve = animar && escena.seMueve(momento)
    var reloj by remember { mutableFloatStateOf(relojFijo) }
    if (seMueve && !pausa) LaunchedEffect(escena) {
        // Sigue desde donde iba: al volver de una pausa el humo y las llamas no dan un salto.
        val base = reloj; var inicio = -1L; var ultimo = -PASO_MS
        while (true) withFrameMillis { ahora ->
            if (inicio < 0) inicio = ahora
            if (ahora - ultimo >= PASO_MS) { ultimo = ahora; reloj = base + (ahora - inicio) / 1000f }
        }
    }
    // Los gestos: un solo detector para todo el cuadro. El toque corto se lo pasa a la capa del gato por [dedo].
    val dedo = remember { DedoDelCuadro() }
    val mantener by rememberUpdatedState(alMantener)
    val hayMantener = alMantener != null
    var presionado by remember { mutableStateOf(false) }
    var llenando by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(presionado) {
        llenando = 0f
        if (!presionado) return@LaunchedEffect
        val t0 = withFrameMillis { it }
        while (true) {
            val t = withFrameMillis { it } - t0
            llenando = ((t - MS_ANTES_DE_LLENAR).toFloat() / (MS_MANTENER - MS_ANTES_DE_LLENAR)).coerceIn(0f, 1f)
            if (t >= MS_MANTENER) break
        }
    }
    val gestos = if (!toques && !hayMantener) Modifier else Modifier.pointerInput(toques, hayMantener) {
        gestosDelCuadro(
            conMantener = hayMantener, alBajar = { if (hayMantener) presionado = true }, alSoltar = { presionado = false },
            alTocar = { donde -> if (toques) dedo.alTocar?.invoke(donde / (size.width / ANCHO)) }, alMantener = { mantener?.invoke() },
        )
    }
    val voz = if (descripcion == null) Modifier else Modifier.semantics {
        contentDescription = descripcion
        customActions = listOfNotNull(
            if (toques && humor != null) CustomAccessibilityAction("Acariciar al gato") { dedo.alMimar?.invoke(); true } else null,
            mantener?.let { abrir -> CustomAccessibilityAction("Abrir «Hoy en la historia»") { abrir(); true } },
        ) + acciones.map { (nombre, hacer) -> CustomAccessibilityAction(nombre) { hacer(); true } }
    }
    // La parte quieta pintada en segundo plano (o null si se traza aquí mismo, o si todavía no está).
    var medida by remember { mutableStateOf(IntSize.Zero) }
    val imagen = if (enSegundoPlano) quietaPintada(escena, momento, medida, alFallar) else null
    val lista = !enSegundoPlano || imagen != null
    val aparece by animateFloatAsState(if (lista) 1f else 0f, label = "cuadro")
    Box(modifier.aspectRatio(4f / 3f).clip(forma).background(fondo).onSizeChanged { medida = it }.then(gestos).then(voz)) {
        if (lista) Box(Modifier.fillMaxSize().graphicsLayer { alpha = aparece }) {
            if (imagen != null) Spacer(Modifier.fillMaxSize().drawBehind { drawImage(imagen, dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())) })
            else Spacer(Modifier.fillMaxSize().graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }.drawBehind {
                aSalvo(alFallar) { lienzo { with(escena) { quieta(momento) } } }
            })
            Spacer(Modifier.fillMaxSize().graphicsLayer().drawBehind {
                val instante = if (seMueve) reloj else relojFijo
                aSalvo(alFallar) { lienzo { with(escena) { viva(momento, instante) } } }
            })
            if (humor != null) CapaGato(escena, momento, humor, pelaje, animar, pasoGato, Modifier.fillMaxSize(), alFallar, if (toques) dedo else null, alAcariciar, mimoFijo, pausa)
            if (banderita != null) Spacer(
                Modifier.align(Alignment.TopStart).padding(start = 14.dp).size(Banderita.ANCHO.dp, Banderita.ALTO.dp).drawBehind { with(banderita) { dibujar(maxOf(lleno, llenando)) } },
            )
        }
    }
}

/**
 * La parte quieta de [escena] en el [momento], como imagen de [medida] pixeles, pintada fuera del hilo de la pantalla.
 * Devuelve null mientras no hay ninguna (la primera vez); después devuelve siempre la última que hubo, también
 * mientras se pinta la siguiente. La última imagen se recuerda fuera de la pantalla ([MemoriaQuieta]): al volver a Hoy
 * sale al instante.
 */
@Composable
private fun quietaPintada(escena: EscenaVida, momento: Momento, medida: IntSize, alFallar: (Throwable) -> Unit): ImageBitmap? {
    val densidad = LocalDensity.current.density
    var imagen by remember(escena) { mutableStateOf(MemoriaQuieta.de(escena, momento.sello)) }
    LaunchedEffect(escena, momento.sello, medida) {
        if (medida.width <= 0 || medida.height <= 0) return@LaunchedEffect
        val guardada = MemoriaQuieta.de(escena, momento.sello)
        if (guardada != null && abs(guardada.width - medida.width) <= 2) { imagen = guardada; return@LaunchedEffect }
        try {
            val nueva = withContext(Dispatchers.Default) { pintarQuieta(escena, momento, medida.width, medida.height, densidad) }
            MemoriaQuieta.guardar(escena, momento.sello, nueva)
            imagen = nueva
        } catch (e: CancellationException) { throw e } catch (e: Exception) { alFallar(e) }
    }
    return imagen
}

/** Pinta la parte quieta de [escena] en una imagen de [ancho] × [alto] pixeles. No toca la pantalla: se puede llamar desde cualquier hilo. */
internal fun pintarQuieta(escena: EscenaVida, momento: Momento, ancho: Int, alto: Int, densidad: Float): ImageBitmap {
    val imagen = ImageBitmap(ancho, alto)
    CanvasDrawScope().draw(Density(densidad), LayoutDirection.Ltr, Canvas(imagen), Size(ancho.toFloat(), alto.toFloat())) { lienzo { with(escena) { quieta(momento) } } }
    imagen.prepareToDraw()
    return imagen
}

/** La última parte quieta pintada en segundo plano, para no repetirla cada vez que se abre Hoy. Una sola imagen (unos 3 MB). */
internal object MemoriaQuieta {
    @Volatile private var ultima: Triple<EscenaVida, String, ImageBitmap>? = null
    fun de(escena: EscenaVida, sello: String): ImageBitmap? = ultima?.takeIf { it.first === escena && it.second == sello }?.third
    fun guardar(escena: EscenaVida, sello: String, imagen: ImageBitmap) { ultima = Triple(escena, sello, imagen) }
    fun olvidar() { ultima = null }
}

/**
 * Por aquí le llegan al gato los toques del cuadro. El detector es uno solo y vive en [VistaEscena]; la capa del gato
 * deja aquí qué hacer con un toque corto ([alTocar], con el sitio en unidades del lienzo) y con una caricia pedida
 * desde un lector de pantalla ([alMimar]).
 */
internal class DedoDelCuadro {
    var alTocar: ((Offset) -> Unit)? = null
    var alMimar: (() -> Unit)? = null
}

/**
 * Los gestos del cuadro, en un solo detector:
 * - **toque corto** (bajar y soltar sin moverse): [alTocar], con el sitio donde bajó el dedo;
 * - **pulsación larga** (solo [conMantener]): si el dedo sigue puesto a los [MS_MANTENER] ms, [alMantener]; lo que
 *   quede del gesto se descarta, para que al soltar no cuente además como toque ni arrastre la pantalla;
 * - **cualquier otra cosa no cuenta:** si el dedo se mueve más de [HOLGURA_DEDO_DP] dp (está desplazando la pantalla
 *   o cambiando de día), si entra otro dedo o si alguien más se quedó con el gesto.
 *
 * [alBajar] y [alSoltar] avisan de cuándo hay un dedo puesto, para ir llenando la banderita. El dedo que baja no se
 * consume: la pantalla se puede desplazar empezando sobre el cuadro.
 */
internal suspend fun PointerInputScope.gestosDelCuadro(conMantener: Boolean, alBajar: () -> Unit, alSoltar: () -> Unit, alTocar: (Offset) -> Unit, alMantener: () -> Unit) {
    val holgura = HOLGURA_DEDO_DP.dp.toPx()
    awaitEachGesture {
        val abajo = awaitFirstDown()
        alBajar()
        try {
            // Dónde se soltó; sin sitio si el gesto no cuenta; null si pasó el tiempo con el dedo puesto.
            val soltado = if (conMantener) withTimeoutOrNull(MS_MANTENER) { hastaSoltar(abajo, holgura) } else hastaSoltar(abajo, holgura)
            if (soltado == null) {
                alMantener()
                do { val ev = awaitPointerEvent(); ev.changes.forEach { it.consume() } } while (ev.changes.any { it.pressed })
            } else if (soltado.isSpecified) alTocar(soltado)
        } finally { alSoltar() }
    }
}

/** Espera a que se levante el dedo que bajó en [abajo]: devuelve dónde bajó, o [Offset.Unspecified] si el gesto dejó de contar. */
private suspend fun AwaitPointerEventScope.hastaSoltar(abajo: PointerInputChange, holgura: Float): Offset {
    while (true) {
        val ev = awaitPointerEvent()
        val c = ev.changes.firstOrNull { it.id == abajo.id }
        if (c == null || ev.changes.size > 1 || c.isConsumed || (c.position - abajo.position).getDistance() > holgura) return Offset.Unspecified
        if (!c.pressed) { c.consume(); return abajo.position }
    }
}

/**
 * Dibuja [dibujo] y, si lanza una excepción, deja el lienzo como estaba antes de empezar (los recortes y los giros a
 * medio hacer se deshacen) y avisa por [alFallar], en vez de dejar que la excepción cierre la app.
 */
internal inline fun DrawScope.aSalvo(alFallar: (Throwable) -> Unit, dibujo: DrawScope.() -> Unit) {
    drawIntoCanvas { tela ->
        val guardados = tela.nativeCanvas.saveCount
        try { dibujo() } catch (e: Exception) { tela.nativeCanvas.restoreToCount(guardados); alFallar(e) }
    }
}
