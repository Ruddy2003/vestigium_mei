package io.github.ruddy2003.vestigium.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.R
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.pantallas.racha
import kotlinx.coroutines.delay

/** Se lanza al sellar el día en la hoja de la noche. */
object Sello {
    var n by mutableIntStateOf(0)
    var fecha by mutableStateOf("")
    fun lanzar(f: String) { fecha = f; n++ }
}

/** Lo que dice la pantalla de la huella en cada tema. */
fun textoSello(tema: TemaId): String = when (tema) {
    TemaId.ROMANO -> "DIES SIGNATVS"; TemaId.CIBER -> "> día.commit() ✓"; TemaId.HELENO -> "ΕΣΦΡΑΓΙΣΘΗ"
    TemaId.MEDIEVAL -> "Sellado y asentado"; TemaId.DISTOPIA -> "DECLARACIÓN ARCHIVADA"; TemaId.REFUGIO -> "BITÁCORA GUARDADA"
    TemaId.ORDEN -> "PARTE ENTREGADO"; TemaId.LABORATORIO -> "SESIÓN REGISTRADA"; TemaId.COLONOS -> "Jornada asentada"
    TemaId.NAPOLEON -> "BOLETÍN ENVIADO"; TemaId.BOTANICO -> "Ejemplar prensado"; TemaId.NAVIDAD -> "Guardado bajo el árbol"
    TemaId.SEDA -> "Rollo sellado"; TemaId.URSS -> "PLAN CUMPLIDO"
    TemaId.GOTICO -> "Signatum et lucet"; TemaId.ESPACIO -> "TRANSMISIÓN COMPLETA"; TemaId.ANDALUS -> "Sellado · خُتِم"
    TemaId.MONGOL -> "ТАМГА ДАРАВ · sellado"; TemaId.RENACIMIENTO -> "Fatto."
}

/** Pantalla completa: la huella del tema se va formando desde el centro, como en un lector, y luego se sella. Toca para cerrar. */
@Composable
fun CapaHuella() {
    val n = Sello.n
    if (n == 0) return
    var visible by remember(n) { mutableStateOf(true) }
    if (!visible) return
    val animar = Repo.config.animaciones
    val prog = remember(n) { Animatable(if (animar) 0f else 1f) }
    val sentir = rememberSentir()
    LaunchedEffect(n) {
        if (animar) prog.animateTo(1f, tween(2600, easing = LinearEasing))
        delay(if (animar) 1500 else 1800)
        visible = false
    }
    LaunchedEffect(n) { delay(if (animar) 1700 else 100); sentir(Sentidos.Evento.LOGRO) }
    VistaHuella(prog.value, Sello.fecha, Modifier.clickable(remember { MutableInteractionSource() }, null) { visible = false })
}

/** La huella de cada tema: el fondo con su textura (papiro, terminal, lacre, cartel…) y la huella entintada encima. */
fun recursosHuella(t: TemaId): Pair<Int, Int> = when (t) {
    TemaId.ROMANO -> R.drawable.huella_fondo_romano to R.drawable.huella_romano
    TemaId.CIBER -> R.drawable.huella_fondo_ciber to R.drawable.huella_ciber
    TemaId.HELENO -> R.drawable.huella_fondo_heleno to R.drawable.huella_heleno
    TemaId.MEDIEVAL -> R.drawable.huella_fondo_medieval to R.drawable.huella_medieval
    TemaId.DISTOPIA -> R.drawable.huella_fondo_distopia to R.drawable.huella_distopia
    TemaId.REFUGIO -> R.drawable.huella_fondo_refugio to R.drawable.huella_refugio
    TemaId.ORDEN -> R.drawable.huella_fondo_orden to R.drawable.huella_orden
    TemaId.LABORATORIO -> R.drawable.huella_fondo_laboratorio to R.drawable.huella_laboratorio
    TemaId.COLONOS -> R.drawable.huella_fondo_colonos to R.drawable.huella_colonos
    TemaId.NAPOLEON -> R.drawable.huella_fondo_napoleon to R.drawable.huella_napoleon
    TemaId.BOTANICO -> R.drawable.huella_fondo_botanico to R.drawable.huella_botanico
    TemaId.NAVIDAD -> R.drawable.huella_fondo_navidad to R.drawable.huella_navidad
    TemaId.SEDA -> R.drawable.huella_fondo_seda to R.drawable.huella_seda
    TemaId.URSS -> R.drawable.huella_fondo_urss to R.drawable.huella_urss
    TemaId.GOTICO -> R.drawable.huella_fondo_gotico to R.drawable.huella_gotico
    TemaId.ESPACIO -> R.drawable.huella_fondo_espacio to R.drawable.huella_espacio
    TemaId.ANDALUS -> R.drawable.huella_fondo_andalus to R.drawable.huella_andalus
    TemaId.MONGOL -> R.drawable.huella_fondo_mongol to R.drawable.huella_mongol
    TemaId.RENACIMIENTO -> R.drawable.huella_fondo_renacimiento to R.drawable.huella_renacimiento
}

/** El sello del tema en un círculo: fondo con textura y huella. [revela] (0–1) la descubre desde el núcleo, como un lector. */
@Composable
fun SelloHuella(modifier: Modifier = Modifier, tema: TemaId = Estilo.tema, revela: Float = 1f, fondo: Float = 1f) {
    val cs = MaterialTheme.colorScheme
    val (rf, rh) = recursosHuella(tema)
    val pf = painterResource(rf); val ph = painterResource(rh)
    Box(modifier.clip(CircleShape).border(2.dp, cs.primary.copy(alpha = 0.6f), CircleShape)) {
        Image(pf, null, Modifier.fillMaxSize().graphicsLayer { alpha = fondo }, contentScale = ContentScale.Crop)
        if (revela < 1f) Image(ph, null, Modifier.fillMaxSize().graphicsLayer { alpha = 0.16f * fondo }, contentScale = ContentScale.Crop)
        if (revela > 0f) Image(ph, null, Modifier.fillMaxSize().drawWithContent {
            val nucleo = Offset(size.width * 0.5f, size.height * 0.49f)
            val r = size.minDimension * 0.62f * revela
            clipPath(Path().apply { addOval(androidx.compose.ui.geometry.Rect(nucleo, r)) }) { this@drawWithContent.drawContent() }
            if (revela in 0.01f..0.99f) {
                drawCircle(cs.secondary.copy(alpha = 0.7f), r, nucleo, style = Stroke(3.dp.toPx()))
                drawCircle(cs.secondary.copy(alpha = 0.25f), r, nucleo, style = Stroke(10.dp.toPx()))
            }
        }, contentScale = ContentScale.Crop)
    }
}

/** La huella en un punto de la animación (0 = vacía, 1 = sellada). Separada para poder verla en las capturas. */
@Composable
fun VistaHuella(progreso: Float, fecha: String, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val tema = Estilo.tema
    // 0–0.15: aparece el fondo; 0.1–0.62: la huella se revela desde el núcleo; 0.62–0.75: destello del sello; después, el texto.
    val fondo = (progreso / 0.15f).coerceIn(0f, 1f)
    val revela = FastOutSlowInEasing.transform(((progreso - 0.1f) / 0.52f).coerceIn(0f, 1f))
    val destello = ((progreso - 0.62f) / 0.13f).coerceIn(0f, 1f)
    val texto = ((progreso - 0.7f) / 0.2f).coerceIn(0f, 1f)
    val escala = 1f + 0.05f * kotlin.math.sin(Math.PI * destello).toFloat()
    Box(modifier.fillMaxSize().background(cs.background.copy(alpha = 0.97f)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SelloHuella(Modifier.size(280.dp).graphicsLayer { scaleX = escala; scaleY = escala; alpha = 0.3f + 0.7f * fondo }, tema, revela, fondo)
            Text(textoSello(tema), style = MaterialTheme.typography.headlineMedium, color = cs.onBackground, textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 22.dp).graphicsLayer { alpha = texto; translationY = (1 - texto) * 24f })
            if (fecha.isNotBlank()) {
                val r = racha(fecha)
                Text(Estilo.fecha(fecha) + if (r >= 2) "\nracha de $r días" else "", style = MaterialTheme.typography.labelLarge, color = cs.primary,
                    textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp).graphicsLayer { alpha = texto })
            }
        }
    }
}
