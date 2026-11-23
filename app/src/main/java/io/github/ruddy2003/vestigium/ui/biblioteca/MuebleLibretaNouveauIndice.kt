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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El índice de la libreta de momentos de Art nouveau: cada día es un carnet chico de los mismos (el cartón jaspeado de piedras,
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
        val tapa = Rect(w * 0.08f, h * 0.08f, w * 0.84f, h * 0.78f)
        val o = Offset(2.8f * u, -2.4f * u)
        val lomo = Rect(tapa.left, tapa.top, tapa.left + tapa.width * 0.2f, tapa.bottom)
        val etiqueta = Rect(lomo.right + (tapa.right - lomo.right) * 0.12f, tapa.top + tapa.height * 0.15f, tapa.right - (tapa.right - lomo.right) * 0.12f, tapa.bottom - tapa.height * 0.15f)
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
        /** Tres jaspeados: en cada uno, las piedras de los tres grupos de color (violeta; salvia y ocre; rosa y crema). */
        val piedras = List(3) { List(3) { Path() } }
        val puntas = Path().apply {
            val p = tapa.width * 0.2f
            addPath(poligonoNouveau(tapa.right - p, tapa.top, tapa.right, tapa.top, tapa.right, tapa.top + p))
            addPath(poligonoNouveau(tapa.right - p, tapa.bottom, tapa.right, tapa.bottom, tapa.right, tapa.bottom - p))
        }
        val trama = Path().apply {
            var y = lomo.top + 1f * u
            while (y < lomo.bottom) { moveTo(lomo.left, y); lineTo(lomo.right, y + lomo.width * 0.4f); y += 1.8f * u }
        }
        /** La cinta de seda: sale de entre las hojas, por abajo, y cuelga con su corte en cola de golondrina. */
        val cinta = Path()
        val cintaLuz = Path()

        init {
            val zona = Rect(lomo.right, tapa.top, tapa.right, tapa.bottom)
            // Solo se ve el jaspeado alrededor del rótulo: las piedras van ahí, recortadas al cartón y sin lo que tapa el rótulo.
            val borde = Path().apply { addRect(zona) }
            val tapado = Path().apply { addRect(Rect(etiqueta.left + 1.5f * u, etiqueta.top + 1.5f * u, etiqueta.right - 1.5f * u, etiqueta.bottom - 1.5f * u)) }
            val visible = Path().apply { op(borde, tapado, PathOperation.Difference) }
            val r0 = 2.2f * u
            for (v in 0 until 3) {
                val n = 64
                val crudas = List(3) { Path() }
                for (i in 0 until n) {
                    val c = Offset(zona.left - r0 + (zona.width + 2f * r0) * azar(611 + v, i), zona.top - r0 + (zona.height + 2f * r0) * azar(621 + v, i))
                    if (etiqueta.deflate(r0 * 1.4f).contains(c)) continue
                    val rr = r0 * (0.7f + 0.7f * azar(631 + v, i))
                    val g = (azar(641 + v, i) * 3f).toInt().coerceIn(0, 2)
                    val p = Path()
                    for (j in 0..8) {
                        val ang = 6.2832f * j / 8f
                        val rad = rr * (0.85f + 0.3f * azar(651 + v * 31 + i, j % 8))
                        val q = Offset(c.x + rad * cos(ang) * 1.1f, c.y + rad * sin(ang) * 0.9f)
                        if (j == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y)
                    }
                    p.close()
                    crudas[g].addPath(p)
                }
                // Recortadas al cartón: ninguna piedra se sale de la tapa.
                for (g in 0 until 3) piedras[v][g].op(crudas[g], visible, PathOperation.Intersect)
            }
            val x = tapa.left + tapa.width * 0.62f
            val ancho = 4.6f * u
            val fin = h * 0.985f
            cinta.moveTo(x, tapa.bottom - 1f * u); cinta.lineTo(x + ancho, tapa.bottom - 1f * u)
            cinta.quadraticTo(x + ancho + 0.8f * u, (tapa.bottom + fin) / 2f, x + ancho + 1.4f * u, fin)
            cinta.lineTo(x + ancho * 0.5f + 1f * u, fin - 2.6f * u); cinta.lineTo(x + 0.6f * u, fin)
            cinta.quadraticTo(x + 0.4f * u, (tapa.bottom + fin) / 2f, x, tapa.bottom - 1f * u); cinta.close()
            cintaLuz.moveTo(x + ancho * 0.3f, tapa.bottom); cintaLuz.quadraticTo(x + ancho * 0.4f, (tapa.bottom + fin) / 2f, x + ancho * 0.45f + 0.6f * u, fin - 2.4f * u)
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
        val capa = mapa[texto] ?: (if (numero) capaEn(medidor, texto, caja, P.TITULO, tinta, 17f, minimo = 9f)
            else capaEn(medidor, texto, caja, P.TEXTO, tinta, 8.5f, peso = FontWeight.SemiBold, minimo = 6f))
            ?.also { mapa[texto] = it } ?: return
        drawText(capa, topLeft = Offset(caja.center.x - capa.size.width / 2f, caja.center.y - capa.size.height / 2f))
    }

    /**
     * La pestaña de un día: un carnet chico con su canto, su tapa de jaspeado (uno de tres, según la semilla), el lomo y las
     * puntas de tela, el rótulo con el día y la letra de la semana, y la cinta del color del ánimo (sin ánimo, sin cinta).
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
        drawRect(if (oscuro) Color.Black.copy(alpha = 0.5f) else P.VENA.copy(alpha = 0.22f), Offset(r.left + 1.4f * u, r.top + 1.8f * u), Size(r.width + g.o.x, r.height))
        drawPath(g.atras, t(P.TELA, oscuro, 0.5f))
        drawPath(g.hojas, t(P.CANTO, oscuro, 0.3f))
        drawPath(g.lineas, P.VENA.copy(alpha = 0.25f), style = Stroke(0.5f * u))
        // La tapa: el jaspeado de piedras.
        val vena = t(P.VENA, oscuro, 0.2f)
        drawRect(vena, r.topLeft, r.size)
        drawPath(g.piedras[v][0], t(P.PIEDRAS[(s / 3) % 2 * 5], oscuro))
        drawPath(g.piedras[v][1], t(P.PIEDRAS[1 + (s / 7) % 2], oscuro))
        drawPath(g.piedras[v][2], t(P.PIEDRAS[3 + (s / 11) % 2], oscuro))
        // Las puntas y el lomo de tela, con su trama y la canal.
        val tela = t(P.TELA, oscuro, 0.35f)
        drawPath(g.puntas, tela)
        drawRect(tela, g.lomo.topLeft, g.lomo.size)
        drawPath(g.trama, t(P.TELA_LUZ, oscuro, 0.35f).copy(alpha = 0.3f), style = Stroke(0.5f * u))
        drawLine(P.VENA.copy(alpha = 0.5f), Offset(g.lomo.left + g.lomo.width * 0.35f, r.top + 1f * u), Offset(g.lomo.left + g.lomo.width * 0.35f, r.bottom - 1f * u), 0.9f * u)
        // Los cantos del cartón: la luz arriba, la sombra abajo.
        drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.35f), Offset(r.left, r.top + 0.4f * u), Offset(r.right, r.top + 0.4f * u), 0.8f * u)
        drawLine(P.VENA.copy(alpha = 0.5f), Offset(r.left, r.bottom - 0.4f * u), Offset(r.right, r.bottom - 0.4f * u), 0.8f * u)
        // El rótulo de papel con su filete, el día y la letra de la semana.
        val et = g.etiqueta
        drawRect(P.VENA.copy(alpha = 0.4f), Offset(et.left - 0.6f * u, et.top + 0.8f * u), et.size)
        drawRect(t(P.PAPEL, oscuro, 0.16f), et.topLeft, et.size)
        drawRect(t(P.FILETE, oscuro, 0.2f).copy(alpha = 0.7f), Offset(et.left + 1.6f * u, et.top + 1.6f * u), Size(et.width - 3.2f * u, et.height - 3.2f * u), style = Stroke(0.6f * u))
        val tinta = if (oscuro) mezcla(P.TINTA, PENUMBRA, 0.15f) else P.TINTA
        medido(medidor, e.dia, Rect(et.left + 2f * u, et.top + 2f * u, et.right - 2f * u, et.top + et.height * 0.68f), tinta, oscuro, true)
        medido(medidor, e.semana, Rect(et.left + 2f * u, et.top + et.height * 0.64f, et.right - 2f * u, et.bottom - 1.2f * u), tinta.copy(alpha = 0.75f), oscuro, false)
        // La cinta del ánimo: sale de entre las hojas y cuelga.
        val tono = tonoDeDiaNouveau(e.tono, oscuro) ?: return
        val cinta = t(tono, oscuro, 0.08f)
        drawPath(g.cinta, P.VENA.copy(alpha = 0.35f), style = Stroke(1.2f * u))
        drawPath(g.cinta, cinta)
        drawPath(g.cintaLuz, mezcla(cinta, Color.White, if (oscuro) 0.45f else 0.38f).copy(alpha = 0.75f), style = Stroke(0.9f * u))
    }
}
