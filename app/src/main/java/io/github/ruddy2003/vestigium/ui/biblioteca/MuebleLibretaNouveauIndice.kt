package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.sin

/**
 * El índice de la libreta de momentos de Art nouveau: cada día es un carnet chico de los mismos (el cartón jaspeado al peine,
 * el lomo y las puntas de tela verde, el canto de las hojas y el rótulo de papel con el día y la letra de la semana a tinta),
 * con la cinta de seda que le sale de entre las hojas, del color del ánimo de ese día. Sin ánimo, no lleva cinta: el carnet
 * queda cerrado y liso por abajo (se distingue por la forma, no solo por el color). Las pestañas son decenas: todo lo que no
 * depende del día se arma una vez por tamaño (tres jaspeados distintos, que se turnan con la semilla) y los números y las
 * letras se miden una sola vez.
 */
internal object IndiceLibretaNouveau {
    private val P = PaletaLibretaNouveau
    /** La penumbra del índice en modo oscuro: el ciruela del cuarto. */
    private val PENUMBRA = Color(0xFF1C1018)

    private fun t(c: Color, oscuro: Boolean, cuanto: Float = 0.4f): Color = if (oscuro) mezcla(c, PENUMBRA, cuanto) else c

    /** Los caminos de una pestaña de [w] × [h], que comparten todas. */
    private class Pestana(val w: Float, val h: Float, u: Float) {
        val tapa = Rect(w * 0.08f, h * 0.07f, w * 0.84f, h * 0.68f)
        val o = Offset(2.8f * u, -2.4f * u)
        val lomo = Rect(tapa.left, tapa.top, tapa.left + tapa.width * 0.18f, tapa.bottom)
        val etiqueta = Rect(lomo.right + (tapa.right - lomo.right) * 0.1f, tapa.top + tapa.height * 0.11f, tapa.right - (tapa.right - lomo.right) * 0.1f, tapa.bottom - tapa.height * 0.11f)
        /** El canto: la tapa de atrás que asoma, la cara de arriba y la de la derecha de las hojas, y sus líneas. */
        val atras = poligonoNouveau(tapa.left + o.x, tapa.top + o.y - 0.8f * u, tapa.right + o.x + 0.8f * u, tapa.top + o.y - 0.8f * u, tapa.right + o.x + 0.8f * u, tapa.bottom + o.y, tapa.right, tapa.bottom, tapa.left, tapa.top)
        val hojas = Path().apply {
            addPath(poligonoNouveau(tapa.left, tapa.top, tapa.right, tapa.top, tapa.right + o.x, tapa.top + o.y, tapa.left + o.x, tapa.top + o.y))
            addPath(poligonoNouveau(tapa.right, tapa.top, tapa.right + o.x, tapa.top + o.y, tapa.right + o.x, tapa.bottom + o.y, tapa.right, tapa.bottom))
        }
        val lineas = Path().apply {
            for (i in 1..2) {
                val f = i / 3f
                moveTo(tapa.left + o.x * f + lomo.width * 0.4f, tapa.top + o.y * f); lineTo(tapa.right + o.x * f, tapa.top + o.y * f); lineTo(tapa.right + o.x * f, tapa.bottom + o.y * f)
            }
        }
        /** Tres jaspeados peinados (se turnan con la semilla): en cada uno, dos vetas de látigo y la vena de ciruela. */
        val vetas = List(3) { List(2) { Path() } }
        val venas = List(3) { Path() }
        val puntas = Path().apply {
            val p = tapa.width * 0.2f
            addPath(poligonoNouveau(tapa.right - p, tapa.top, tapa.right, tapa.top, tapa.right, tapa.top + p))
            addPath(poligonoNouveau(tapa.right - p, tapa.bottom, tapa.right, tapa.bottom, tapa.right, tapa.bottom - p))
        }
        val trama = Path().apply {
            var y = lomo.top + 1f * u
            while (y < lomo.bottom) { moveTo(lomo.left, y); lineTo(lomo.right, y + lomo.width * 0.4f); y += 1.8f * u }
        }
        /** La cinta de seda: sale de entre las hojas, por abajo, y cuelga larga con su corte en cola de golondrina. */
        val cinta = Path()
        val cintaLuz = Path()

        init {
            val zona = Rect(lomo.right, tapa.top, tapa.right, tapa.bottom)
            // Solo se ve el jaspeado alrededor del rótulo: las vetas se recortan al cartón y sin lo que tapa el rótulo.
            val borde = Path().apply { addRect(zona) }
            val tapado = Path().apply { addRect(Rect(etiqueta.left + 1.5f * u, etiqueta.top + 1.5f * u, etiqueta.right - 1.5f * u, etiqueta.bottom - 1.5f * u)) }
            val visible = Path().apply { op(borde, tapado, PathOperation.Difference) }
            for (v in 0 until 3) {
                val fase = 1.3f * v
                fun y(x: Float, base: Float) = zona.top + zona.height * base + zona.height * 0.1f * sin((x - zona.left) / zona.width * 4.4f + fase)
                fun banda(b0: Float, b1: Float): Path = Path().apply {
                    for (s in 0..12) { val x = zona.left + zona.width * s / 12f; if (s == 0) moveTo(x, y(x, b0)) else lineTo(x, y(x, b0)) }
                    for (s in 12 downTo 0) { val x = zona.left + zona.width * s / 12f; lineTo(x, y(x, b1)) }
                    close()
                }
                vetas[v][0].op(banda(0.02f + 0.06f * v, 0.36f + 0.05f * v), visible, PathOperation.Intersect)
                vetas[v][1].op(banda(0.62f - 0.04f * v, 0.92f), visible, PathOperation.Intersect)
                venas[v].op(banda(0.5f, 0.535f), visible, PathOperation.Intersect)
            }
            val x = tapa.left + tapa.width * 0.62f
            val ancho = 4.8f * u
            val fin = h * 0.99f
            cinta.moveTo(x, tapa.bottom - 1f * u); cinta.lineTo(x + ancho, tapa.bottom - 1f * u)
            cinta.quadraticTo(x + ancho + 1f * u, (tapa.bottom + fin) / 2f, x + ancho + 1.8f * u, fin)
            cinta.lineTo(x + ancho * 0.5f + 1.2f * u, fin - 3f * u); cinta.lineTo(x + 0.8f * u, fin)
            cinta.quadraticTo(x + 0.5f * u, (tapa.bottom + fin) / 2f, x, tapa.bottom - 1f * u); cinta.close()
            cintaLuz.moveTo(x + ancho * 0.3f, tapa.bottom); cintaLuz.quadraticTo(x + ancho * 0.4f, (tapa.bottom + fin) / 2f, x + ancho * 0.45f + 0.8f * u, fin - 2.8f * u)
        }
    }

    private var cachePestana: Pestana? = null
    private fun pestanaDe(w: Float, h: Float, u: Float): Pestana = cachePestana?.takeIf { it.w == w && it.h == h } ?: Pestana(w, h, u).also { cachePestana = it }

    /**
     * Los números de los días y las letras de la semana ya medidos (uno para el claro y otro para el oscuro, porque la tinta va
     * en la medida): con decenas de pestañas, el medidor de la pantalla (8 medidas) los volvería a medir en cada dibujo. Se
     * vacían si cambian la densidad, la escala de la letra o el tamaño del rótulo.
     */
    private val medidas = List(4) { HashMap<String, TextLayoutResult>() }
    private val claves = List(2) { FloatArray(4) }

    /** El día ([numero]) o la letra de la semana, centrado en [caja], igual que lo dibujaría [textoEn] pero medido una vez. */
    private fun DrawScope.medido(medidor: TextMeasurer, texto: String, caja: Rect, tinta: Color, oscuro: Boolean, numero: Boolean) {
        val n = if (numero) 0 else 1
        val k = claves[n]
        if (k[0] != density || k[1] != fontScale || k[2] != caja.width || k[3] != caja.height) {
            medidas[2 * n].clear(); medidas[2 * n + 1].clear(); k[0] = density; k[1] = fontScale; k[2] = caja.width; k[3] = caja.height
        }
        val mapa = medidas[2 * n + if (oscuro) 1 else 0]
        val capa = mapa[texto] ?: (if (numero) capaEn(medidor, texto, caja, P.TITULO, tinta, 20f, minimo = 10f)
            else capaEn(medidor, texto, caja, P.TEXTO, tinta, 8.5f, peso = FontWeight.SemiBold, minimo = 6f))
            ?.also { mapa[texto] = it } ?: return
        drawText(capa, topLeft = Offset(caja.center.x - capa.size.width / 2f, caja.center.y - capa.size.height / 2f))
    }

    /**
     * La pestaña de un día: un carnet chico con su canto, su tapa de jaspeado peinado (uno de tres, según la semilla), el lomo y
     * las puntas de tela, el rótulo con el día (lo primero que se lee) y la letra de la semana, y la cinta larga del color del
     * ánimo (sin ánimo, sin cinta).
     */
    fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val g = pestanaDe(w, h, u)
        val r = g.tapa
        val s = e.semilla and 0x7fffffff
        val v = s % 3
        // La sombra en la página, el canto y la tapa de atrás.
        drawRect(if (oscuro) Color.Black.copy(alpha = 0.5f) else P.CIRUELA.copy(alpha = 0.2f), Offset(r.left + 1.4f * u, r.top + 1.8f * u), Size(r.width + g.o.x, r.height))
        drawPath(g.atras, t(P.TELA, oscuro, 0.5f))
        drawPath(g.hojas, t(P.CANTO, oscuro, 0.3f))
        drawPath(g.lineas, P.CIRUELA.copy(alpha = 0.25f), style = Stroke(0.5f * u))
        // La tapa: el jaspeado peinado, de fondo salvia, con dos vetas y la vena de ciruela.
        drawRect(t(P.VETAS[0], oscuro, 0.3f), r.topLeft, r.size)
        drawPath(g.vetas[v][0], t(P.VETAS[if (s / 3 % 2 == 0) 1 else 4], oscuro, 0.3f))
        drawPath(g.vetas[v][1], t(P.VETAS[if (s / 7 % 2 == 0) 2 else 5], oscuro, 0.3f))
        drawPath(g.venas[v], t(P.CIRUELA, oscuro, 0.2f))
        // Las puntas y el lomo de tela, con su trama y la canal.
        val tela = t(P.TELA, oscuro, 0.35f)
        drawPath(g.puntas, tela)
        drawRect(tela, g.lomo.topLeft, g.lomo.size)
        drawPath(g.trama, t(P.TELA_LUZ, oscuro, 0.35f).copy(alpha = 0.3f), style = Stroke(0.5f * u))
        drawLine(P.CIRUELA.copy(alpha = 0.5f), Offset(g.lomo.left + g.lomo.width * 0.35f, r.top + 1f * u), Offset(g.lomo.left + g.lomo.width * 0.35f, r.bottom - 1f * u), 0.9f * u)
        // Los cantos del cartón: la luz arriba, la sombra abajo.
        drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.35f), Offset(r.left, r.top + 0.4f * u), Offset(r.right, r.top + 0.4f * u), 0.8f * u)
        drawLine(P.CIRUELA.copy(alpha = 0.5f), Offset(r.left, r.bottom - 0.4f * u), Offset(r.right, r.bottom - 0.4f * u), 0.8f * u)
        // El rótulo de papel con su filete, el día (grande) y la letra de la semana.
        val et = g.etiqueta
        drawRect(P.CIRUELA.copy(alpha = 0.35f), Offset(et.left - 0.6f * u, et.top + 0.8f * u), et.size)
        drawRect(t(P.PAPEL, oscuro, 0.16f), et.topLeft, et.size)
        drawRect(t(P.FILETE, oscuro, 0.2f).copy(alpha = 0.7f), Offset(et.left + 1.4f * u, et.top + 1.4f * u), Size(et.width - 2.8f * u, et.height - 2.8f * u), style = Stroke(0.6f * u))
        val tinta = if (oscuro) mezcla(P.TINTA, PENUMBRA, 0.15f) else P.TINTA
        medido(medidor, e.dia, Rect(et.left + 1.6f * u, et.top + 1.6f * u, et.right - 1.6f * u, et.top + et.height * 0.72f), tinta, oscuro, true)
        medido(medidor, e.semana, Rect(et.left + 2f * u, et.top + et.height * 0.68f, et.right - 2f * u, et.bottom - 1f * u), tinta.copy(alpha = 0.7f), oscuro, false)
        // La cinta del ánimo: sale de entre las hojas y cuelga.
        val tono = tonoDeDiaNouveau(e.tono, oscuro) ?: return
        val cinta = t(tono, oscuro, 0.08f)
        drawPath(g.cinta, P.CIRUELA.copy(alpha = 0.35f), style = Stroke(1.2f * u))
        drawPath(g.cinta, cinta)
        drawPath(g.cintaLuz, mezcla(cinta, Color.White, if (oscuro) 0.45f else 0.38f).copy(alpha = 0.75f), style = Stroke(0.9f * u))
    }
}