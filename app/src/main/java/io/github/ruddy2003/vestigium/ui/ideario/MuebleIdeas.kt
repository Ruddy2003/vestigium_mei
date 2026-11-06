package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.esOscuro
import io.github.ruddy2003.vestigium.ui.rememberSentir
import kotlinx.coroutines.launch

/** Lo que se ve de un compartimento del mueble: su tema ("" = sin tema), cuántas fichas tiene y la fecha de la más reciente. */
class CajonIdeas(val tema: String, val n: Int, val ultima: String?)

/**
 * El mueble entero del Ideario ([DisenoIdeas.mueble]): la corona, las filas de compartimentos (uno por tema, [cajones]) y la
 * base. Crece con el número de temas; los huecos de la última fila que sobran se dibujan cerrados y sin rótulo. Si [mas] es
 * más de 0 (la vista corta), lo dice [textoMas] en la placa de la base, o debajo del mueble si el tema no tiene placa.
 * Al tocar un compartimento se abre (el cajón sale, el nicho se ilumina…) y después se llama a [abrir].
 */
@Composable
fun MuebleIdeas(cajones: List<CajonIdeas>, rotulo: (String) -> String, sub: (CajonIdeas) -> String, modifier: Modifier = Modifier,
                mas: Int = 0, textoMas: String = "", abrir: ((CajonIdeas) -> Unit)? = null) {
    val d = disenoIdeas(Estilo.tema)
    val oscuro = esOscuro()
    val sentir = rememberSentir()
    val alcance = rememberCoroutineScope()
    Column(modifier.fillMaxWidth()) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val ancho = maxWidth
            val lado = ancho * d.lado; val sep = ancho * d.hueco
            val anchoH = (ancho - lado * 2 - sep * (d.cols - 1)) / d.cols
            val altoH = anchoH / d.proporcion
            val filas = ((cajones.size + d.cols - 1) / d.cols).coerceAtLeast(1)
            val altoCorona = ancho * d.corona; val altoBase = ancho * d.base
            val alto = altoCorona + sep + altoH * filas + sep * (filas - 1) + sep + altoBase
            val huecos = filas * d.cols
            fun x(i: Int): Dp = lado + (anchoH + sep) * (i % d.cols)
            fun y(i: Int): Dp = altoCorona + sep + (altoH + sep) * (i / d.cols)
            Box(Modifier.fillMaxWidth().height(alto)) {
                Canvas(Modifier.fillMaxSize()) {
                    val disp = Disposicion(
                        corona = Rect(0f, 0f, size.width, altoCorona.toPx()),
                        filas = (0 until filas).map { f -> Rect(0f, y(f * d.cols).toPx(), size.width, (y(f * d.cols) + altoH).toPx()) },
                        huecos = (0 until huecos).map { i -> Rect(x(i).toPx(), y(i).toPx(), (x(i) + anchoH).toPx(), (y(i) + altoH).toPx()) },
                        base = Rect(0f, size.height - altoBase.toPx(), size.width, size.height),
                        separacion = sep.toPx(), mas = mas,
                    )
                    with(d) { mueble(disp, oscuro) }
                }
                for (i in 0 until huecos) {
                    val c = cajones.getOrNull(i)
                    val lugar = Modifier.offset(x(i), y(i)).size(anchoH, altoH)
                    if (c == null) Canvas(lugar) { with(d) { compartimento(EstadoCajon(0, false, 0f, 7919 * (i + 1), vacio = true), oscuro) } }
                    else key(c.tema) {
                        val anim = remember { Animatable(0f) }
                        val toque = remember { MutableInteractionSource() }
                        CompartimentoIdeas(d, c.n, c.tema.isEmpty(), c.tema.hashCode(), rotulo(c.tema), sub(c), oscuro,
                            lugar.clickable(toque, null, enabled = abrir != null) {
                                alcance.launch {
                                    sentir(Sentidos.Evento.ABRIR)
                                    anim.animateTo(1f, tween(260))
                                    abrir?.invoke(c)
                                    anim.snapTo(0f)
                                }
                            }, abierto = { anim.value })
                    }
                }
                val placa = d.placa
                if (mas > 0 && placa != null) {
                    val alto0 = alto - altoBase
                    val altoP = altoBase * placa.height
                    Box(Modifier.offset(ancho * placa.left, alto0 + altoBase * placa.top).size(ancho * placa.width, altoP), contentAlignment = Alignment.Center) {
                        Text(textoMas, color = d.tintaPlaca(oscuro), fontFamily = d.letraRotulo ?: MaterialTheme.typography.labelMedium.fontFamily,
                            fontSize = (altoP.value * 0.42f).coerceIn(9f, 14f).sp, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                    }
                }
            }
        }
        if (mas > 0 && d.placa == null)
            Text(textoMas, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
    }
}

/**
 * Un compartimento con su rótulo: el dibujo del tema ([DisenoIdeas.compartimento]) y, encima, el nombre del tema y su cuenta
 * en la zona de [DisenoIdeas.rotulo], con la letra del tema. La letra se ajusta al alto del rótulo. [abierto] se lee al dibujar
 * (sin recomponer): el rótulo baja con el frente del cajón.
 */
@Composable
fun CompartimentoIdeas(d: DisenoIdeas, n: Int, sinTema: Boolean, semilla: Int, titulo: String, sub: String, oscuro: Boolean,
                       modifier: Modifier = Modifier, abierto: () -> Float = { 0f }) {
    BoxWithConstraints(modifier) {
        Canvas(Modifier.fillMaxSize()) { with(d) { compartimento(EstadoCajon(n, sinTema, abierto(), semilla), oscuro) } }
        val r = d.rotulo
        val altoR = maxHeight * r.height
        val tam = (altoR.value * 0.36f).coerceIn(9f, 17f)
        val altoPx = with(LocalDensity.current) { maxHeight.toPx() }
        val tinta = d.tintaRotulo(oscuro)
        val letra = d.letraRotulo ?: MaterialTheme.typography.labelLarge.fontFamily
        Column(Modifier.offset(maxWidth * r.left, maxHeight * r.top).size(maxWidth * r.width, altoR)
            .graphicsLayer { translationY = abierto() * d.salida * altoPx }.padding(horizontal = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(titulo, color = tinta, fontFamily = letra, fontSize = tam.sp, lineHeight = (tam * 1.12f).sp, fontWeight = FontWeight.SemiBold,
                fontStyle = if (sinTema) FontStyle.Italic else FontStyle.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
            if (sub.isNotEmpty()) Text(sub, color = tinta.copy(alpha = 0.78f), fontFamily = letra, fontSize = (tam * 0.74f).sp, lineHeight = (tam * 0.86f).sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        }
    }
}

/** El interior del compartimento abierto, a pantalla completa ([DisenoIdeas.interior]). [oscuro] se pasa: adentro de una ficha el esquema de colores es el del papel. */
@Composable
fun InteriorIdeas(oscuro: Boolean, modifier: Modifier = Modifier) {
    val d = disenoIdeas(Estilo.tema)
    Canvas(modifier.fillMaxSize()) { with(d) { interior(oscuro) } }
}

/** La marca de cómo la tienes, chica, para poner junto al texto de una ficha. No dibuja nada si la idea no tiene postura. */
@Composable
fun MarcaPostura(id: String, p: Pagina, modifier: Modifier = Modifier, tam: Dp = 10.dp) {
    if (id.isBlank()) return
    val d = disenoIdeas(Estilo.tema)
    Canvas(modifier.size(tam)) { with(d) { marcaPostura(id, p) } }
}

/** La marca de una idea fijada (la chincheta, el lacre, el nudo… de cada tema). */
@Composable
fun MarcaFijada(p: Pagina, modifier: Modifier = Modifier, tam: Dp = 10.dp) {
    val d = disenoIdeas(Estilo.tema)
    Canvas(modifier.size(tam)) { with(d) { marcaFijada(p) } }
}

/** Un botón sobre el interior del contenedor: una placa con el borde y la letra de la tinta del interior. */
@Composable
fun BotonArchivo(texto: String, tinta: Color, modifier: Modifier = Modifier, habilitado: Boolean = true, onClick: () -> Unit) {
    val d = disenoIdeas(Estilo.tema)
    val forma = RoundedCornerShape(5.dp)
    Text(texto, color = tinta.copy(alpha = if (habilitado) 1f else 0.35f), fontFamily = d.letraRotulo ?: MaterialTheme.typography.labelLarge.fontFamily,
        fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1,
        modifier = modifier.clip(forma).background(tinta.copy(alpha = 0.08f)).border(1.dp, tinta.copy(alpha = if (habilitado) 0.45f else 0.15f), forma)
            .clickable(enabled = habilitado, onClick = onClick).padding(horizontal = 14.dp, vertical = 8.dp))
}

/**
 * La parte de arriba de un compartimento abierto, en una franja de [alto]: para que una ficha suelta (como «Una idea de
 * antes») se vea saliendo de su cajón.
 */
@Composable
fun BordeCompartimento(modifier: Modifier = Modifier, alto: Dp = 26.dp, n: Int = 3) {
    val d = disenoIdeas(Estilo.tema)
    val oscuro = esOscuro()
    BoxWithConstraints(modifier.fillMaxWidth().height(alto).clipToBounds()) {
        val completo = maxWidth / d.proporcion
        Canvas(Modifier.fillMaxWidth().wrapContentHeight(Alignment.Top, unbounded = true).requiredHeight(completo)) {
            with(d) { compartimento(EstadoCajon(n, false, 1f, 31), oscuro) }
        }
    }
}
