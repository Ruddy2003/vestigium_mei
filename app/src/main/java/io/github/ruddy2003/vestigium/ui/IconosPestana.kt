package io.github.ruddy2003.vestigium.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Íconos de la barra inferior dibujados para cada tema: la tablilla de cera romana, el puesto de mercado
 * medieval, la cartilla de racionamiento, la caja de suministros… Siguen el color que les da la barra.
 */
@Composable
fun IconoPestana(id: String, tema: TemaId = Estilo.tema, nombre: String = id) {
    val color = LocalContentColor.current
    Canvas(Modifier.size(24.dp).semantics { contentDescription = nombre }) { Trazo(this, color).dibujar(id, tema) }
}

/** Pequeño lápiz sobre una cuadrícula de 24 × 24. */
private class Trazo(val s: DrawScope, val c: Color) {
    val u = s.size.minDimension / 24f
    val grueso = Stroke(1.7f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val fino = Stroke(1.1f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
    fun o(x: Float, y: Float) = Offset(x * u, y * u)
    fun l(x1: Float, y1: Float, x2: Float, y2: Float, w: Float = 1.7f) = s.drawLine(c, o(x1, y1), o(x2, y2), w * u, StrokeCap.Round)
    fun r(x: Float, y: Float, w: Float, h: Float, rad: Float = 1f, relleno: Boolean = false) =
        s.drawRoundRect(c, o(x, y), Size(w * u, h * u), CornerRadius(rad * u), style = if (relleno) Fill else grueso)
    fun ci(x: Float, y: Float, rad: Float, relleno: Boolean = false, st: Stroke = grueso) = s.drawCircle(c, rad * u, o(x, y), style = if (relleno) Fill else st)
    fun p(relleno: Boolean = false, st: Stroke = grueso, f: Path.() -> Unit) = s.drawPath(Path().apply(f), c, style = if (relleno) Fill else st)
    fun Path.m(x: Float, y: Float) = moveTo(x * u, y * u)
    fun Path.t(x: Float, y: Float) = lineTo(x * u, y * u)
    fun Path.q(cx: Float, cy: Float, x: Float, y: Float) = quadraticTo(cx * u, cy * u, x * u, y * u)
    fun rayos(cx: Float, cy: Float, n: Int, r1: Float, r2: Float, desde: Double = 0.0, hasta: Double = 360.0) {
        for (i in 0 until n) {
            val a = Math.toRadians(desde + (hasta - desde) * i / (if (hasta - desde >= 360.0) n else n - 1)).toFloat()
            l(cx + r1 * cos(a), cy + r1 * sin(a), cx + r2 * cos(a), cy + r2 * sin(a))
        }
    }

    fun dibujar(id: String, tm: TemaId) = when (id) {
        "dia" -> dia(tm); "momentos" -> momentos(tm); "gastos" -> gastos(tm); "explorar" -> explorar(tm); else -> mas(tm)
    }

    // ------------------------------------------------------------ Día
    private fun dia(tm: TemaId) { when (tm) {
        TemaId.ROMANO -> { r(4f, 3f, 16f, 18f, 1.5f); l(7.5f, 8f, 16.5f, 8f); l(7.5f, 12f, 16.5f, 12f); l(7.5f, 16f, 13f, 16f) }          // tablilla de cera
        TemaId.CIBER -> { r(2.5f, 4f, 19f, 16f, 1f); p { m(6.5f, 9f); t(9.5f, 12f); t(6.5f, 15f) }; l(11.5f, 15f, 16.5f, 15f) }              // terminal
        TemaId.HELENO -> { ci(12f, 12f, 3.6f, relleno = true); rayos(12f, 12f, 8, 6f, 9.5f) }                                               // sol
        TemaId.MEDIEVAL -> { p { m(12f, 6.5f); q(8f, 4f, 3f, 5f); t(3f, 18.5f); q(8f, 17.5f, 12f, 19.5f); q(16f, 17.5f, 21f, 18.5f); t(21f, 5f); q(16f, 4f, 12f, 6.5f) }; l(12f, 6.5f, 12f, 19.5f, 1.1f) } // códice abierto
        TemaId.DISTOPIA -> { r(5f, 4f, 14f, 17.5f, 0.6f); r(8.5f, 2.5f, 7f, 3.2f, 0.4f, relleno = true); l(8f, 10f, 16f, 10f); l(8f, 13.5f, 16f, 13.5f); l(8f, 17f, 12.5f, 17f) } // expediente
        TemaId.REFUGIO -> { r(3f, 3.5f, 18f, 13.5f, 3f); l(12f, 17f, 12f, 20f); l(8f, 20.5f, 16f, 20.5f); p(st = fino) { m(6f, 10.5f); t(9f, 10.5f); t(10.5f, 7.5f); t(12.5f, 13.5f); t(14f, 10.5f); t(18f, 10.5f) } } // monitor
        TemaId.ORDEN -> { p { m(12f, 3f); t(19f, 5.5f); t(19f, 11f); q(19f, 17.5f, 12f, 21f); q(5f, 17.5f, 5f, 11f); t(5f, 5.5f); close() }; l(12f, 7f, 12f, 17f); l(8.5f, 10.5f, 15.5f, 10.5f) } // escudo
        TemaId.LABORATORIO -> { p { m(9.5f, 3f); t(9.5f, 9f); t(4.5f, 19f); q(4f, 21f, 6f, 21f); t(18f, 21f); q(20f, 21f, 19.5f, 19f); t(14.5f, 9f); t(14.5f, 3f) }; l(8f, 3f, 16f, 3f); l(7.2f, 15f, 16.8f, 15f, 1.1f) } // matraz
        TemaId.COLONOS -> { p { m(6.5f, 17f); q(6.5f, 11.5f, 12f, 11.5f); q(17.5f, 11.5f, 17.5f, 17f) }; l(3f, 17f, 21f, 17f); rayos(12f, 17f, 5, 7.5f, 10f, -160.0, -20.0); l(7f, 20.5f, 17f, 20.5f) } // amanecer
    } }

    // ------------------------------------------------------------ Momentos
    private fun momentos(tm: TemaId) { when (tm) {
        TemaId.ROMANO -> { p { m(3f, 15f); q(3f, 11f, 9f, 11f); t(16f, 11f); q(20f, 11f, 21f, 14f); t(17f, 15f); q(14f, 18f, 9f, 18f); q(3f, 18f, 3f, 15f); close() }
            p(relleno = true) { m(19.8f, 10f); q(22f, 7f, 20.3f, 4.5f); q(18.4f, 7.5f, 19.8f, 10f); close() } }                              // lucerna
        TemaId.CIBER -> p { m(13f, 2.5f); t(5f, 13.5f); t(11f, 13.5f); t(10f, 21.5f); t(19f, 10f); t(13f, 10f); close() }                        // rayo
        TemaId.HELENO -> { p { m(7.5f, 4f); q(4f, 12f, 8f, 19f) }; p { m(16.5f, 4f); q(20f, 12f, 16f, 19f) }; l(6f, 6.5f, 18f, 6.5f); l(8f, 19f, 16f, 19f)
            l(10f, 6.5f, 10f, 19f, 1.1f); l(12f, 6.5f, 12f, 19f, 1.1f); l(14f, 6.5f, 14f, 19f, 1.1f) }                                         // lira
        TemaId.MEDIEVAL -> { p { m(20.5f, 3f); q(10f, 5f, 6.5f, 16f); t(8.5f, 17f); q(17.5f, 13f, 20.5f, 3f); close() }; l(6.5f, 16f, 4f, 21f); l(18f, 5.5f, 9.5f, 13.5f, 1.1f) } // pluma
        TemaId.DISTOPIA -> { p { m(2.5f, 12f); q(12f, 3f, 21.5f, 12f); q(12f, 21f, 2.5f, 12f); close() }; ci(12f, 12f, 3.3f, relleno = true) }  // ojo
        TemaId.REFUGIO -> { ci(12f, 16f, 1.8f, relleno = true); p { m(8f, 13f); q(12f, 9f, 16f, 13f) }; p { m(5f, 10f); q(12f, 3.5f, 19f, 10f) }; l(12f, 17.5f, 12f, 21f) } // radio
        TemaId.ORDEN -> { l(6f, 3f, 6f, 21f); p { m(6f, 4f); t(19f, 7.5f); t(6f, 11f) }; l(6f, 7.5f, 13f, 7.5f, 1.1f) }                        // banderín
        TemaId.LABORATORIO -> { p { m(12f, 3f); q(18f, 11f, 18f, 14.5f); q(18f, 20.5f, 12f, 20.5f); q(6f, 20.5f, 6f, 14.5f); q(6f, 11f, 12f, 3f); close() }; p(st = fino) { m(9f, 14.5f); q(9f, 17.5f, 12f, 17.5f) } } // gota
        TemaId.COLONOS -> { ci(12f, 3.6f, 1.4f, st = fino); p { m(8f, 7f); t(16f, 7f); t(14f, 5f); t(10f, 5f); close() }; r(7.5f, 7f, 9f, 11.5f, 0.8f)
            l(6.5f, 19.8f, 17.5f, 19.8f); p(relleno = true) { m(12f, 9.5f); q(14.3f, 12.5f, 12f, 15f); q(9.7f, 12.5f, 12f, 9.5f); close() } }   // farol
    } }

    // ------------------------------------------------------------ Gastos
    private fun gastos(tm: TemaId) { when (tm) {
        TemaId.ROMANO -> { ci(12f, 12f, 8.6f); ci(12f, 12f, 5.6f, st = fino); ci(12f, 12f, 1.8f, relleno = true) }                            // denario
        TemaId.CIBER -> { r(2.5f, 5.5f, 19f, 13f, 2f); r(5.5f, 9f, 5f, 4f, 0.6f); l(13f, 15f, 19f, 15f, 1.1f); l(13f, 11.5f, 17f, 11.5f, 1.1f) } // tarjeta con chip
        TemaId.HELENO -> { p { m(9.5f, 3f); t(14.5f, 3f) }; p { m(10f, 3f); t(10f, 6f); q(5f, 9f, 6.5f, 14f); q(8f, 19f, 12f, 21f); q(16f, 19f, 17.5f, 14f); q(19f, 9f, 14f, 6f); t(14f, 3f) }
            p(st = fino) { m(10f, 6.5f); q(6.5f, 5.5f, 7f, 9.5f) }; p(st = fino) { m(14f, 6.5f); q(17.5f, 5.5f, 17f, 9.5f) } }               // ánfora
        TemaId.MEDIEVAL -> { p { m(3f, 9f); t(5f, 4f); t(19f, 4f); t(21f, 9f); close() }; l(8.5f, 4f, 7.6f, 9f, 1.1f); l(12f, 4f, 12f, 9f, 1.1f); l(15.5f, 4f, 16.4f, 9f, 1.1f)
            l(5f, 9f, 5f, 20f); l(19f, 9f, 19f, 20f); r(4f, 13.5f, 16f, 2.6f, 0.4f); l(3f, 20.5f, 21f, 20.5f) }                              // puesto de mercado
        TemaId.DISTOPIA -> { r(3f, 6f, 18f, 12f, 1f); for (y in listOf(8f, 10.5f, 13f, 15.5f)) ci(15.5f, y, 0.6f, relleno = true); l(6f, 10f, 12f, 10f, 1.1f); l(6f, 14f, 11f, 14f, 1.1f) } // cartilla de racionamiento
        TemaId.REFUGIO -> { r(3.5f, 6f, 17f, 14f, 1f); r(6.5f, 9f, 11f, 8f, 0.3f, relleno = false); l(6.5f, 17f, 17.5f, 9f, 1.1f); l(3.5f, 4f, 20.5f, 4f) } // caja de suministros
        TemaId.ORDEN -> { r(3f, 7f, 18f, 12f, 0.5f); l(3f, 11f, 21f, 11f, 1.1f); r(6f, 14f, 3f, 2f, 0.2f, true); r(10.5f, 14f, 3f, 2f, 0.2f, true); r(15f, 14f, 3f, 2f, 0.2f, true); r(9.5f, 4.5f, 5f, 2.5f, 0.6f) } // caja de intendencia
        TemaId.LABORATORIO -> { l(12f, 4f, 12f, 20f); l(7f, 20.5f, 17f, 20.5f); l(4f, 7f, 20f, 7f); l(4f, 7f, 2.5f, 12f, 1.1f); l(4f, 7f, 5.5f, 12f, 1.1f); l(20f, 7f, 18.5f, 12f, 1.1f); l(20f, 7f, 21.5f, 12f, 1.1f)
            p { m(2f, 12f); q(4f, 15f, 6f, 12f) }; p { m(18f, 12f); q(20f, 15f, 22f, 12f) } }                                              // balanza
        TemaId.COLONOS -> { p { m(8f, 8f); q(3f, 13f, 5f, 18f); q(7f, 21.5f, 12f, 21.5f); q(17f, 21.5f, 19f, 18f); q(21f, 13f, 16f, 8f); close() }; l(8f, 8f, 16f, 8f)
            l(9.5f, 8f, 8f, 4.5f, 1.1f); l(12f, 8f, 12f, 4f, 1.1f); l(14.5f, 8f, 16f, 4.5f, 1.1f) }                                          // bolsa de monedas
    } }

    // ------------------------------------------------------------ Explorar
    private fun explorar(tm: TemaId) { when (tm) {
        TemaId.ROMANO -> { p { m(8f, 21f); t(8f, 7f); q(8f, 3f, 12f, 3f); q(16f, 3f, 16f, 7f); t(16f, 21f) }; l(5.5f, 21f, 18.5f, 21f); l(10f, 9f, 14f, 9f, 1.1f); l(10f, 12f, 14f, 12f, 1.1f); l(10f, 15f, 14f, 15f, 1.1f) } // miliario
        TemaId.CIBER -> { ci(12f, 12f, 9f); ci(12f, 12f, 4.8f, st = fino); l(12f, 12f, 18.4f, 5.6f); ci(15.5f, 9f, 1.2f, relleno = true) }           // radar
        TemaId.HELENO -> { ci(12f, 12f, 8.6f); p(relleno = true) { m(12f, 5f); t(13.8f, 12f); t(10.2f, 12f); close() }; p(st = fino) { m(10.2f, 12f); t(12f, 19f); t(13.8f, 12f) } } // brújula
        TemaId.MEDIEVAL -> { ci(12f, 12f, 8.6f); l(3.6f, 12f, 20.4f, 12f); l(12f, 12f, 12f, 20.4f) }                                              // mapamundi en T-O
        TemaId.DISTOPIA -> { ci(7f, 15f, 4f); ci(17f, 15f, 4f); l(4.5f, 11.8f, 6.5f, 5.5f); l(19.5f, 11.8f, 17.5f, 5.5f); l(6.5f, 5.5f, 10f, 5.5f); l(14f, 5.5f, 17.5f, 5.5f); l(10.5f, 9f, 13.5f, 9f) } // binoculares
        TemaId.REFUGIO -> { p { m(12f, 21f); q(5f, 13f, 5f, 9.5f); q(5f, 3f, 12f, 3f); q(19f, 3f, 19f, 9.5f); q(19f, 13f, 12f, 21f); close() }; ci(12f, 9.5f, 2.6f) } // marcador de mapa
        TemaId.ORDEN -> { ci(12f, 12f, 7.5f); l(12f, 2f, 12f, 7f); l(12f, 17f, 12f, 22f); l(2f, 12f, 7f, 12f); l(17f, 12f, 22f, 12f); ci(12f, 12f, 1.2f, relleno = true) } // retícula
        TemaId.LABORATORIO -> { p { m(4f, 4f); t(4f, 20f); t(20f, 20f) }; p(st = fino) { m(6.5f, 16f); t(10f, 11f); t(13f, 13.5f); t(19f, 6f) }
            ci(10f, 11f, 1.3f, relleno = true); ci(13f, 13.5f, 1.3f, relleno = true); ci(19f, 6f, 1.3f, relleno = true) }                       // gráfica
        TemaId.COLONOS -> { l(4f, 19f, 9.5f, 13.5f, 3.4f); l(9.5f, 13.5f, 14.5f, 8.5f, 2.6f); l(14.5f, 8.5f, 19f, 4f, 1.9f); l(7f, 21f, 9.5f, 18.5f, 1.1f) } // catalejo
    } }

    // ------------------------------------------------------------ Más
    private fun mas(tm: TemaId) { when (tm) {
        TemaId.ROMANO -> { r(6f, 5f, 12f, 14f, 0.5f); l(4.5f, 4f, 19.5f, 4f, 2.4f); l(4.5f, 20f, 19.5f, 20f, 2.4f); l(8.5f, 9f, 15.5f, 9f, 1.1f); l(8.5f, 12f, 15.5f, 12f, 1.1f); l(8.5f, 15f, 13f, 15f, 1.1f) } // rollo
        TemaId.CIBER -> { r(4f, 4f, 6.5f, 6.5f, 0.6f); r(13.5f, 4f, 6.5f, 6.5f, 0.6f); r(4f, 13.5f, 6.5f, 6.5f, 0.6f); r(13.5f, 13.5f, 6.5f, 6.5f, 0.6f, true) } // módulos
        TemaId.HELENO -> { l(4f, 4f, 20f, 4f); ci(6.5f, 6.3f, 1.8f, st = fino); ci(17.5f, 6.3f, 1.8f, st = fino); l(6.5f, 8f, 17.5f, 8f, 1.1f)
            l(8f, 8f, 8f, 19f, 1.1f); l(12f, 8f, 12f, 19f, 1.1f); l(16f, 8f, 16f, 19f, 1.1f); l(6f, 19.5f, 18f, 19.5f); l(5f, 21.5f, 19f, 21.5f) } // columna jónica
        TemaId.MEDIEVAL -> { ci(7.5f, 8f, 4f); l(10.3f, 10.8f, 20f, 20.5f); l(15.5f, 16f, 17.7f, 13.8f); l(18f, 18.5f, 20.2f, 16.3f) }           // llave
        TemaId.DISTOPIA -> { r(5f, 3f, 14f, 18f, 0.5f); l(5f, 9f, 19f, 9f, 1.1f); l(5f, 15f, 19f, 15f, 1.1f); l(10.5f, 6f, 13.5f, 6f); l(10.5f, 12f, 13.5f, 12f); l(10.5f, 18f, 13.5f, 18f) } // archivero
        TemaId.REFUGIO -> { l(6f, 18f, 14.5f, 9.5f, 2.6f); p { m(14.5f, 9.5f); q(12.5f, 5f, 16.5f, 3.5f); t(15f, 6.5f); t(17.5f, 9f); t(20.5f, 7.5f); q(19f, 11.5f, 14.5f, 9.5f) } } // llave inglesa
        TemaId.ORDEN -> { l(5.5f, 20.5f, 13.5f, 12.5f, 2.2f); p(relleno = true) { m(10.5f, 6f); t(17f, 12.5f); t(20f, 9.5f); t(13.5f, 3f); close() } } // martillo
        TemaId.LABORATORIO -> { l(3f, 6f, 21f, 6f); for (x in listOf(5f, 10.25f, 15.5f)) p { m(x, 4f); t(x, 18f); q(x, 20.5f, x + 1.75f, 20.5f); q(x + 3.5f, 20.5f, x + 3.5f, 18f); t(x + 3.5f, 4f) }
            l(5.8f, 14f, 7.7f, 14f, 1.1f); l(11f, 11f, 13f, 11f, 1.1f); l(16.3f, 16f, 18.2f, 16f, 1.1f) }                                  // tubos de ensayo
        TemaId.COLONOS -> { p { m(3.5f, 11f); t(12f, 4f); t(20.5f, 11f) }; r(6f, 10f, 12f, 10.5f, 0.4f); r(10.5f, 15f, 3f, 5.5f, 0.3f) }          // casa del cabildo
    } }
}
