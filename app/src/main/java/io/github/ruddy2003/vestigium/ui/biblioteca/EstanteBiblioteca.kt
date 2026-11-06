package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.data.porNumero
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.Tonos
import io.github.ruddy2003.vestigium.ui.esOscuro
import io.github.ruddy2003.vestigium.ui.rememberSentir
import kotlinx.coroutines.launch

/** Un grupo de entradas que comparten repisa y rótulo (un mes); sin [titulo], el canto de la tabla va sin rótulo. */
class GrupoEstante(val titulo: String?, val dias: List<Dia>)

/** Dónde va un volumen dentro del mueble. */
private class Lugar(val dia: Dia, val x: Dp, val y: Dp, val ancho: Dp, val alto: Dp)

/**
 * El estante del diario dibujado por el tema ([DisenoBiblioteca]): el mueble entero, que crece con las entradas, y cada
 * entrada como volumen del tema. Cada grupo ([grupos], un mes) empieza en una repisa nueva y lleva su rótulo en el canto
 * de la tabla. De noche ([noche]) guarda los sueños: el texto y el color son los del sueño. Al tocar un volumen sale de la
 * repisa, suena y se llama a [abrir] con su fecha.
 */
@Composable
fun EstanteTema(d: DisenoBiblioteca, grupos: List<GrupoEstante>, noche: Boolean, abrir: (String) -> Unit, modifier: Modifier = Modifier) {
    if (grupos.all { it.dias.isEmpty() }) return
    val oscuro = esOscuro()
    val sentir = rememberSentir()
    val alcance = rememberCoroutineScope()
    val medidor = rememberTextMeasurer(cacheSize = 64)
    val texto: (Dia) -> String = if (noche) { x -> x.sueno_texto } else { x -> x.nota }
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val ancho = maxWidth
        val lado = ancho * d.lado
        val interior = ancho - lado * 2
        val altoCorona = ancho * d.corona
        val altoBase = ancho * d.base
        val fila = d.altoRepisa + d.tabla
        // Se acomodan los volúmenes de izquierda a derecha; cada grupo empieza en una repisa nueva.
        val lugares = mutableListOf<Lugar>()
        val placas = mutableListOf<Pair<Int, String>>()
        var repisa = 0
        grupos.filter { it.dias.isNotEmpty() }.forEachIndexed { g, grupo ->
            if (g > 0) repisa++
            grupo.titulo?.let { placas.add(repisa to it) }
            var x = d.separa
            grupo.dias.forEach { dia ->
                val w = d.ancho(Texto.palabras(texto(dia)))
                if (x + w > interior - d.separa && x > d.separa) { repisa++; x = d.separa }
                val h = d.altoRepisa * d.alto(dia.fecha.hashCode())
                val y = altoCorona + fila * repisa + (d.altoRepisa - h)
                lugares.add(Lugar(dia, lado + x, y, w, h))
                x += w + d.separa
            }
        }
        val repisas = repisa + 1
        val alto = altoCorona + fila * repisas + altoBase
        Box(Modifier.fillMaxWidth().height(alto)) {
            Canvas(Modifier.fillMaxSize().graphicsLayer()) {
                val l = lado.toPx(); val w = size.width
                val disp = DisposicionEstante(
                    corona = Rect(0f, 0f, w, altoCorona.toPx()),
                    repisas = (0 until repisas).map { k -> Rect(l, (altoCorona + fila * k).toPx(), w - l, (altoCorona + fila * k + d.altoRepisa).toPx()) },
                    tablas = (0 until repisas).map { k -> Rect(l, (altoCorona + fila * k + d.altoRepisa).toPx(), w - l, (altoCorona + fila * (k + 1)).toPx()) },
                    izquierda = Rect(0f, altoCorona.toPx(), l, size.height - altoBase.toPx()),
                    derecha = Rect(w - l, altoCorona.toPx(), w, size.height - altoBase.toPx()),
                    base = Rect(0f, size.height - altoBase.toPx(), w, size.height),
                    placas = placas.map { (k, t) ->
                        val y0 = (altoCorona + fila * k + d.altoRepisa).toPx()
                        val ancho0 = (w - 2 * l) * 0.42f
                        PlacaEstante(Rect(w / 2 - ancho0 / 2, y0 + d.tabla.toPx() * 0.12f, w / 2 + ancho0 / 2, y0 + d.tabla.toPx() * 0.88f), t)
                    },
                )
                clipRect { with(d) { estante(disp, medidor, oscuro, noche) } }
            }
            lugares.forEach { lu ->
                val dia = lu.dia
                key(dia.fecha) {
                    val anim = remember { Animatable(0f) }
                    val toque = remember { MutableInteractionSource() }
                    val n = Texto.palabras(texto(dia))
                    val v = Volumen(
                        dia = dia.fecha.takeLast(2).trimStart('0'),
                        mes = Estilo.mes(dia.fecha.substring(5, 7).toIntOrNull() ?: 1),
                        palabras = n,
                        tono = Tonos.valencia((if (noche) dia.sueno_valencia else dia.valencia)?.toDouble(), oscuro),
                        semilla = dia.fecha.hashCode(), noche = noche,
                    )
                    val etiqueta = Fechas.corto(dia.fecha) + ", " + porNumero(n, "1 palabra", "$n palabras")
                    Canvas(Modifier.offset(lu.x, lu.y).size(lu.ancho, lu.alto)
                        .graphicsLayer { translationY = -anim.value * lu.alto.toPx() * 0.22f }
                        .semantics { contentDescription = etiqueta }
                        .clickable(toque, null) {
                            alcance.launch {
                                sentir(Sentidos.Evento.ABRIR)
                                anim.animateTo(1f, tween(240))
                                abrir(dia.fecha)
                                anim.snapTo(0f)
                            }
                        }) { with(d) { volumen(v, medidor, oscuro) } }
                }
            }
        }
    }
}
